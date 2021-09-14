package oem.edge.ed.odc.dropbox.service.helper;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.DboxException;

import oem.edge.ed.odc.util.ProxyDebugInterface;
import oem.edge.ed.odc.util.MultipartInputStream;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.Vector;

import java.lang.reflect.*;

import java.io.*;
import java.net.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
* This class encapsulates the mechanics of downloading from the Dropbox Service.
*  An application developer should use the various download helper classes to achieve 
*  there data access goals. The intention of this class is to support those 
*  download helper classes.
*/
public class DataDownloadTransfer extends DataTransfer {

   RandomAccessFile  rf;
   OutputStream      outstrm;
   long              packid;
   long              fileid;
   DropboxAccess     dropbox;
   ConnectionFactory factory;
   
   String            encoding  = null;
   
   long              ofs;
   java.security.MessageDigest digest;
   
   static public final int DOWNLOAD_BUFFER_SIZE = 2 * 1024 * 1024;
   
   int mybuffersize = DOWNLOAD_BUFFER_SIZE;
   
   public void setMaxBufferSize(int s) { 
      if (s == 0 || s > DOWNLOAD_BUFFER_SIZE) s = DOWNLOAD_BUFFER_SIZE;
      else if      (s < 32768)                s = 32768;
      mybuffersize = s;
   }
   public int  getMaxBufferSize() { return mybuffersize; }

   
  /**
   * Create a transfer object which will allow downloading from the specified
   *  package/file into the specified RandomAccessFile. This constructor provides
   *  the most flexibility and performance, as multiple threads can be employed
   *  (using multiple instances of this class) to download different sections of
   *  the file simultaneously
   *
   * @param dropbox DropboxAccess proxy which is used to talk to dropbox session
   * @param packid  Package identifier for package containing file to download
   * @param fileid  File identifier which we want to download
   * @param f       RandomAccessFile allowing seek and store
   */
   public DataDownloadTransfer(DropboxAccess dropbox, long packid, long fileid, 
                               RandomAccessFile f) {
   
      this.dropbox   = dropbox;
      this.packid    = packid;
      this.fileid    = fileid;
      this.rf        = f;
      factory = (ConnectionFactory)((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");
      
   }
   
   
  /**
   * Create a transfer object which will allow downloading from the specified
   *  package/file into the specified OutputStream. This method of creation 
   *  allows only a single Transfer object to be used for a download, and must
   *  transfer the data from byte 0 to the end in order.
   *
   * @param dropbox DropboxAccess proxy which is used to talk to dropbox session
   * @param packid  Package identifier for package containing file to download
   * @param fileid  File identifier which we want to download
   * @param outstrm OutputStream into which file should be streamed 
   */
   public DataDownloadTransfer(DropboxAccess dropbox, long packid, long fileid, 
                               OutputStream outstrm) {
      this.dropbox   = dropbox;
      this.packid    = packid;
      this.fileid    = fileid;
      this.outstrm   = outstrm;
      factory = (ConnectionFactory)((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");
      
      try {
         digest         = java.security.MessageDigest.getInstance("MD5");
      } catch(Exception ee) {}
   }
   
  /**
   * Create a transfer object which will allow downloading ALL files from the
   *  package using the encoding into the specified OutputStream. 
   *  This method of creation 
   *  allows only a single Transfer object to be used for a download, and must
   *  transfer the data from byte 0 to the end in order.
   *
   * @param dropbox  DropboxAccess proxy which is used to talk to dropbox session
   * @param packid   Package identifier for package containing file to download
   * @param encoding encoding to use for the the package download (tar, tgz, zip)
   * @param outstrm  OutputStream into which file should be streamed 
   */
   public DataDownloadTransfer(DropboxAccess dropbox, long packid, String encoding,
                               OutputStream outstrm) {
      this.dropbox   = dropbox;
      this.packid    = packid;
      this.fileid    = -1;
      this.outstrm   = outstrm;
      this.encoding  = encoding;
      factory = (ConnectionFactory)((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");
      
      try {
         digest         = java.security.MessageDigest.getInstance("MD5");
      } catch(Exception ee) {}
   }
   
   public java.security.MessageDigest getDigest() { return digest; }
   
   
  /**
   *  This routine finishes the streaming download from inputstream.
   * 
   *  If its tagged as multipart, then assume multipart-mixed, and will contain
   *   2 sections. First is data payload followed by 2nd empty section containing
   *   headers dropbox-datalength and dropbox-datamd5 OR dropbox-errormsg
   */
   protected long finishDownload(InputStream is, boolean multipart, 
                                 long rofs, long rlen) 
      throws AbortTransferException, IOException, DboxException {
         
      long inrlen = rlen;
      
      MultipartInputStream mis = null;
      
      if (multipart) {
         mis = (MultipartInputStream)is;
      }
      
      if (rf != null) rf.seek(rofs);
         
     // This is the cumulative digest (could be more than one DDT transaction
      java.security.MessageDigest lcdigest = null;
      try {
         if (digest != null) {
            lcdigest = (java.security.MessageDigest)digest.clone();
         }
      } catch(Exception ee) {}
      
     // Digest for just this data that is being downloaded
      java.security.MessageDigest ldigest  = null;
      if (multipart) {
         try {
            ldigest = java.security.MessageDigest.getInstance("MD5");
         } catch(Exception ee) {}
      }
         
      long xfered = 0;
         
      byte buf[] = new byte[32768];
      while(rlen > 0 && doOperation) {
         int l = buf.length;
         if (l > rlen) l = (int)rlen;
            
         int r = is.read(buf, 0, l);
            
         if (r < 0) {
            break;
         }
            
         if (rf != null) rf.write(buf, 0, r);
         else            outstrm.write(buf, 0, r);
            
        // Tell anyone interested that we got some data
         sendEvent(new ActionEvent(this, r, "update"));
            
         rlen -= r;
         if (ldigest  != null) ldigest.update(buf, 0, r);
         if (lcdigest != null) lcdigest.update(buf, 0, r);
         xfered += r;
      }
         
      if (doOperation == false) {
         throw new AbortTransferException("Transfer aborted by user");
      }
                  
      if (is.read(buf, 0, buf.length) != -1) {
         throw new DboxException("Data left in stream after all requested drained: Asked for "
                                 + inrlen + " bytes processed " + (inrlen-rlen));
      }
         
     // Multipart, good, we can validate sent length and md5. May contain
     //  error info which we will package up as DboxException and throw
      if (multipart) {
         if (mis.hasNext()) {
            mis.next();
            
            String errmsg = mis.getHeader("dropbox-errormsg");
            if (errmsg != null) {
               throw new DboxException("Multipart Download - Server error: " + 
                                       errmsg);
            }
            
            String srlenS = mis.getHeader("dropbox-datalength");
            long srlen = 0;
            try {
               srlen = Long.parseLong(srlenS);
            } catch(Exception nfe) {
               throw new DboxException("Downloaded data len does not match server expectation: Asked for " + 
                                       inrlen + " bytes processed " + (inrlen-rlen) +
                  " + server expectation " + srlenS + " - bad server number format");
            }
            
            if (srlen != (inrlen - rlen)) {
               throw new DboxException("Downloaded data len does not match server expectation: Asked for " + 
                                       inrlen + " bytes processed " + (inrlen-rlen) +
                  " + server expectation " + srlen);
            }
            
            String srmd5  = mis.getHeader("dropbox-datamd5");
            
            byte arr[] = ldigest.digest();
            StringBuffer ans = new StringBuffer();
            for(int i=0 ; i < arr.length; i++) {
               String v = Integer.toHexString(((int)arr[i]) & 0xff);
               if (v.length() == 1) ans.append("0");
               ans.append(v);
            }
            
            String lmd5 = ans.toString();
            
            if (srmd5 == null || !srmd5.trim().equalsIgnoreCase(lmd5)) {
               throw new DboxException("Downloaded data MD5 does not match server expectation: Asked for " + 
                                       inrlen + " got " + (inrlen-rlen) +
                                       " servermd5 = " + srmd5);
            }

         } else {
            throw new DboxException("Multipart download response has only data portion: Asked for "
                                    + inrlen + " bytes processed " + (inrlen-rlen));
         }
      }
         
      is.close();
      is = null;
         
      digest = lcdigest;
         
      ofs += xfered;           // only valid for streaming
         
      return xfered;
   }
   
  /**
   * Download data using DropboxTransfer servlet rather than WebService array method.
   *
   * <pre>
   *       COMMAND        =  DOWNLOAD || DOWNLOADPACKAGE
   *       DBOXSESSIONID  =  the sessionid
   *       PACKID         =  packid
   *       FILEID         =  fileid
   *       SLOTID         =  slotid_for_upload or ofsset for download
   *       XFERSIZE       =  number of bytes to xfer
   *       ENCODING       =  encoding for package download
   * </pre>
   *
   * @param rofs Offset into file to transfer data
   * @param rlen Requested length of data transfer
   * @return long number of bytes actually transferred
   */
   protected long doTransferHttp(long rofs, long rlen) 
      throws AbortTransferException, IOException, DboxException {
      
      HttpURLConnection conn = null;
      InputStream         is = null;
      try {
         factory.getTopURL();
         URL theurl = new URL(factory.getTopURL() + "/DropboxTransfer");
         
         conn = (HttpURLConnection)theurl.openConnection();
         
        // Modify the connection to behave WRT proxy and ild
         HttpConnection.modifyConnection(conn);
         
         conn.setDoOutput(true);
         conn.setDoInput(true);
         conn.setAllowUserInteraction(false);
         
         String sessionid = factory.getSessionId(dropbox);
         
        // Show we like straight data or can take data AND md5/len
        // Wanted to use addRequestProp ... but 1.4 
         conn.setRequestProperty("ACCEPT",        
                                 "application/octetstream,multipart/mixed");
                                 
         conn.setRequestProperty("COMMAND",       "DOWNLOAD");
         conn.setRequestProperty("DBOXSESSIONID", sessionid);
         conn.setRequestProperty("PACKID",        ""+packid);
         conn.setRequestProperty("FILEID",        ""+fileid);
         conn.setRequestProperty("SLOTID",        ""+rofs);
         if (encoding != null) {
            conn.setRequestProperty("COMMAND",       "DOWNLOADPACKAGE");
            conn.setRequestProperty("ENCODING",      encoding);
         }
         conn.setRequestProperty("XFERSIZE",      ""+rlen);
         
         conn.getOutputStream().close();
         
         int rc = conn.getResponseCode();
         if (rc < 200 || rc >= 300) {
            String errmsg = conn.getHeaderField("dropbox-errormsg");
            if (errmsg != null) {
               throw new DboxException("HTTP Download - Server error: " + 
                                       errmsg);
            }
            throw new IOException("Bad response from download request: " + 
                                  theurl.toString());
         }
         
         is = conn.getInputStream();
         
         
        // If the payload is multipart/mixed, then we expect the first section
        //  to be data payload, 2nd to contain info validating sent data OR
        //  exception information
         String contentType = conn.getContentType();
         if (contentType != null) {
            String ucaseContentType = contentType.toUpperCase();
            if (ucaseContentType.startsWith("MULTIPART/MIXED")) {
               int idx1 = ucaseContentType.indexOf(";");
               int idx2 = ucaseContentType.indexOf("BOUNDRY");
               int idx3 = ucaseContentType.indexOf("=");
               String boundry = null;
               if (idx1 < idx2 && idx2 < idx3 && idx1 > 0) {
                  boundry = contentType.substring(idx3+1);
                  idx1 = boundry.indexOf(";");
                  if (idx1 >= 0) {
                     boundry = boundry.substring(0, idx1);
                  }
                  
                  boundry = boundry.trim();
                  if (boundry.indexOf(" ") >= 0 || boundry.indexOf("\t") >= 0) {
                     boundry = null;
                  } else {
                  
                    // I am being very rigid in my use for expediency. 
                    //  First part will be data requested, 2nd will be length 
                    //  of data downloaded <nl> md5 for data downloaded OR
                    //  exception data, which will be thrown when processed.
                     is = new MultipartInputStream(is, boundry);
                     
                    // Drive to first body section
                     ((MultipartInputStream)is).next();
                  }
               }
               
               if (boundry == null || boundry.length() == 0) {
                  throw new IOException("Unable to parse Multipart type: " +
                                        contentType);
               }
            }
         }
         
         return finishDownload(is, is instanceof MultipartInputStream, rofs, rlen);
         
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch(Exception e) {
         }
      }
   }
   
   /**
   * Download data using WebService array method. This method is usually avoided
   *  as its costly in space. It is used for DirectConnectionFactory proxies
   *
   * @param rofs Offset into file to transfer data
   * @param rlen Requested length of data transfer
   * @return long number of bytes actually transferred
   */
   protected long doTransferBuffer(long rofs, long rlen) 
      throws DboxException, AbortTransferException, IOException {
      
     // If we have a package download, do it
      if (encoding != null) {
        // NOTE, this ONLY works for direct!!
         
         InputStream         is = null;
         Object dboxsrv = null;
         try {
            
           // Reflect to get to method NOT advertised in DropboxAccess
           // Get actual direct access object from ProxyDebug
            dboxsrv = ((ProxyDebugInterface)dropbox).getProxiedObject();
            Class cls = dboxsrv.getClass();
            Class cparms[] = new Class[] { long.class, String.class };
            Method downloadPackageStream = cls.getMethod("downloadPackageStream", 
                                                         cparms);
            
            Object parms[] = new Object[] { new Long(packid), encoding };
            
           // Since we are using DirectFactory, but we are NOT making the next
           //  calls via the ProxyDebug object, we need to do the precall 
           //  ourselves to setup the credentials. Not the best, but need to for now
            ((ProxyDebugInterface)dropbox).doPreCallers(dboxsrv);
            is = (InputStream)downloadPackageStream.invoke(dboxsrv, parms);
            
            return finishDownload(is, false, rofs, rlen);
         } catch(NoSuchMethodException e) {
            throw new DboxException("PackageDownload FAILED as direct proxy does NOT support downloadPackageStream method!");
         } catch(InvocationTargetException e) {
            throw new DboxException("PackageDownload FAILED: Proxy yuk: ", e);
         } catch(IllegalAccessException e) {
            throw new DboxException("PackageDownload FAILED as IllegalAccess", e);
         } catch(SecurityException e) {
            throw new DboxException("PackageDownload FAILED as security Exception", e);
         } finally {
            try {
               if (is != null) {
                  is.close();
               }
            } catch(Exception e) {
            }
            
           // Since we are using DirectFactory, and we are calling DropboxAccessSrv
           //  directly, we have to undo the precall we made earlier, which setup
           //  our credentials
            if (dboxsrv != null) {
               try {
                  ((ProxyDebugInterface)dropbox).doPostCallers(dboxsrv);
               } catch(Exception eeee) {}
            }
         }
         
      } else if (fileid == -1) {
         throw new DboxException("Fileid not set for file download");
      } else {
         byte buf[] = dropbox.downloadPackageItem(packid, fileid, rofs, rlen);
      
         if (rlen < buf.length) {
            throw new DboxException("Youch! More data was returned than asked for!!: " + 
                                    toString());
         }
         if (rf != null) {
            rf.seek(rofs);      
            rf.write(buf, 0, buf.length);
         } else {
            outstrm.write(buf, 0, buf.length);
         }
      
        // Tell anyone interested that we got some data
         sendEvent(new ActionEvent(this, buf.length, "update"));
         
         ofs += buf.length;
         if (digest != null) digest.update(buf, 0, buf.length);
      
         return buf.length;
      }
   }
   
  /**
   * Attempts to transfer bytes from offset rofs for a length of rlen. Just as with
   *  the java.io read API, the actual number of bytes may be less.
   *
   * @param rofs Offset into file to transfer data
   * @param rlen Requested length of data transfer
   * @return long number of bytes actually transferred
   */
   public long doTransfer(long rofs, long rlen) 
      throws AbortTransferException, IOException, DboxException {
      
     // If streaming, and offset != expected offset ... error
      if (outstrm != null && rofs != ofs) {
         throw new DboxException("Streaming Download failed, slot ofs (" + 
                                 rofs + ") differs from my ofs(" + ofs + ")");
      }
      
     // Use servlet method to transfer if JAXRPC is factory, otherwise, use buffer
     // Web xfer method has additional round trip to get new slot each time
      String fname = factory.getClass().getName();
      if (factory.getTopURL() != null &&
          !fname.equals("oem.edge.ed.odc.dropbox.service.helper.DirectConnectionFactory")) {
         return doTransferHttp(rofs, rlen);
      } else {
         return doTransferBuffer(rofs, rlen);
      }
   }
}
