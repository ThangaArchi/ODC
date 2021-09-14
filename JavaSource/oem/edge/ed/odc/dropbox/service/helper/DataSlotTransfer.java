package oem.edge.ed.odc.dropbox.service.helper;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.DboxException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;

import oem.edge.ed.odc.util.ProxyDebugInterface;

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
* This class encapsulates the mechanics of uploading to the Dropbox Service.
*  An application developer should use the various upload helper classes to achieve 
*  there data upload goals. The intention of this class is to support those 
*  upload helper classes.
*/
public class DataSlotTransfer extends DataTransfer {

   public static final int UPLOAD_BUFFER_SIZE = 2*1024*1024;
   
   
   RandomAccessFile  rf;
   InputStream       inpstrm;
   long              packid;
   long              fileid;
   DropboxAccess     dropbox;
   ConnectionFactory factory;
   
   FileSlot          slot;
   
   long              ofs;
   java.security.MessageDigest digest;
   
   long lastSent;
   
   protected void setLastSent(long v) { lastSent = v;    }
   protected long getLastSent()       { return lastSent; }
   
   int mybuffersize = UPLOAD_BUFFER_SIZE;
   
   public void setMaxBufferSize(int s) { 
      if (s == 0 || s > UPLOAD_BUFFER_SIZE) s = UPLOAD_BUFFER_SIZE;
      else if      (s < 32768)              s = 32768;
      mybuffersize = s;
   }
   public int  getMaxBufferSize() { return mybuffersize; }
   
   
  /**
   * Create a transfer object which will allow uploading to the specified
   *  package/file from the specified RandomAccessFile. This constructor provides
   *  the most flexibility and performance, as multiple threads can be employed
   *  (using multiple instances of this class) to upload different sections of
   *  the file simultaneously
   *
   * @param dropbox DropboxAccess proxy which is used to talk to dropbox session
   * @param packid  Package identifier for package containing file to upload
   * @param fileid  File identifier which we want to upload
   * @param f       RandomAccessFile allowing seek and read
   */
   public DataSlotTransfer(DropboxAccess dropbox, long packid, long fileid,
                           RandomAccessFile f) {
      this.dropbox   = dropbox;
      this.packid    = packid;
      this.fileid    = fileid;
      this.rf        = f;
      factory = (ConnectionFactory)((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");
   }
   
  /**
   * Create a transfer object which will allow uploading to the specified
   *  package/file from the specified InputStream. This constructor should be
   *  used only when a RandomAccessFile is not an option, as restarts and multi-channel
   *  uploads are not possible.
   *
   * @param dropbox DropboxAccess proxy which is used to talk to dropbox session
   * @param packid  Package identifier for package containing file to upload
   * @param fileid  File identifier which we want to upload
   * @param inpstrm InputStream allowing sequential access to file contents
   */
   public DataSlotTransfer(DropboxAccess dropbox, long packid, long fileid, 
                           InputStream inpstrm) {
      this.dropbox   = dropbox;
      this.packid    = packid;
      this.fileid    = fileid;
      this.inpstrm   = inpstrm;
      factory = (ConnectionFactory)((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");
      
      try {
         digest         = java.security.MessageDigest.getInstance("MD5");
      } catch(Exception ee) {}
   }
   
   public java.security.MessageDigest getDigest() { return digest; }
   
   
  /**
   * Upload data using DropboxTransfer servlet rather than WebService array method.
   *
   * <pre>
   *       COMMAND        =  UPLOAD
   *       DBOXSESSIONID  =  the sessionid
   *       PACKID         =  packid
   *       FILEID         =  fileid
   *       SLOTID         =  slotid_for_upload or ofsset for download
   *       XFERSIZE       =  number of bytes to xfer
   * </pre>
   *
   * @param slot  FileSlot to which we should upload
   */
   protected void doTransferHttp(FileSlot slot) 
      throws AbortTransferException, IOException, DboxException {
      
      HttpURLConnection conn = null;
      OutputStream       out = null;
      try {
         factory.getTopURL();
         URL theurl = new URL(factory.getTopURL() + "/DropboxTransfer");
         
         conn = (HttpURLConnection)theurl.openConnection();
         
        // Modify the connection to behave WRT proxy and ild
         HttpConnection.modifyConnection(conn);
         
         long len = slot.getRemainingBytes();
         
        // If we are doing random file access, we assume that the advertised slot side
        //  is good to go ... try to stream it.
         boolean httpfixed = false;
         if (rf != null) {
            try {
               Class cls = conn.getClass();
               Class cargs[] = { int.class };
               java.lang.reflect.Method m = 
                  cls.getMethod("setFixedLengthStreamingMode", cargs);
               Object args[] = { new Integer((int)len) };
               m.invoke(conn, args);
               httpfixed = true;
            } catch(Exception e) {
              //System.out.println("Exception setting fix len mode");
              // e.printStackTrace(System.out);
            }
         }
         
        // If we don't have the capability to avoid buffering in URLConnection, 
        //  then limit the size to be the same as WebSvc transfer buffer size
         if (!httpfixed && len > mybuffersize) {
            len = mybuffersize;
         }
         
         conn.setDoOutput(true);
         conn.setDoInput(true);
         conn.setAllowUserInteraction(false);
         
         String sessionid = factory.getSessionId(dropbox);
         
         conn.setRequestProperty("COMMAND",       "UPLOAD");
         conn.setRequestProperty("DBOXSESSIONID", sessionid);
         conn.setRequestProperty("PACKID",        ""+packid);
         conn.setRequestProperty("FILEID",        ""+fileid);
         conn.setRequestProperty("SLOTID",        ""+slot.getSlotId());
         conn.setRequestProperty("XFERSIZE",      ""+len);
         
         out = conn.getOutputStream();
         
         if (rf != null) rf.seek(slot.getCurrentOffset());
         
         java.security.MessageDigest ldigest = null;
         try {
            if (digest != null) {
               ldigest = (java.security.MessageDigest)digest.clone();
            }
         } catch(Exception ee) {}
         
         byte buf[] = new byte[32768];
         int xferred = 0;
         while(len > 0 && doOperation) {
            int l = buf.length;
            if (l > len) l = (int)len;
            
            int r = 0;
            if (rf != null) r = rf.read(buf, 0, l);
            else            r = inpstrm.read(buf, 0, l);
            
            if (r < 0) {
               break;
            }
            xferred += r;
            len -= r;
            out.write(buf, 0, r);
            
           // Tell anyone interested that we got some data
            sendEvent(new ActionEvent(this, r, "update"));
            
            if (ldigest != null) ldigest.update(buf, 0, r);
         }
         
         out.close();
         out = null;
         
         if (doOperation == false) {
            throw new AbortTransferException("Transfer aborted by user");
         }
         
         int rc = conn.getResponseCode();
         if (rc < 200 || rc >= 300) {
            String errmsg = conn.getHeaderField("dropbox-errormsg");
            if (errmsg != null) {
               throw new 
                  DboxException("DataSlotTransfer: Server error uploading data: " +
                                errmsg);
            }
            throw new IOException("Bad response from upload request: " + 
                                  theurl.toString());
         }
         
         digest = ldigest;
         
         setLastSent(xferred);
         ofs += xferred;           // only valid for streaming
         
      } finally {
         try {
            if (out != null) {
               out.close();
            }
         } catch(Exception e) {
         }
         try {
            if (conn != null) {
               conn.getInputStream().close();
            }
         } catch(Exception e) {
         }
      }
   }
   
   /**
   * Upload data using WebService array method. This method is usually avoided
   *  as its costly in space. It is used for DirectConnectionFactory proxies
   *
   * @param slot  FileSlot to which we should upload
   */
   protected FileSlot doTransferBuffer(FileSlot slot) 
      throws AbortTransferException, IOException, DboxException {
   
      int rb = (int)slot.getRemainingBytes();
      if (rb > mybuffersize) rb = mybuffersize;
      
      long cofs = slot.getCurrentOffset();
            
      if (rf != null) {
         rf.seek(cofs);
         long flen = rf.length();
         if (flen < (cofs+rb)) {
            rb = (int)(flen - cofs);
            if (rb < 0) throw new IOException("Slot past end of file");
         }
      }
      
      int totlen = rb;
      
      byte buf[] = new byte[rb];
      while(rb > 0 && doOperation) {
         int r = 0;
         if (rf != null) r = rf.read(buf, totlen-rb, rb);
         else            r = inpstrm.read(buf, totlen-rb, rb);
         
        // If we run out of bytes and did not fulfill buffer full, copy to correct 
        //  sized buffer
         if (r < 0) {
            byte nb[] = new byte[totlen-rb];
            System.arraycopy(buf, 0, nb, 0, totlen-rb);
            buf = nb;
            break;
         }
         
        // Tell anyone interested that we got some data
         sendEvent(new ActionEvent(this, r, "update"));
         
         rb -= r;
      }
      
      if (doOperation == false) {
         throw new AbortTransferException("Transfer aborted by user");
      }
      
      slot =  dropbox.uploadFileSlotToPackage(packid, fileid, 
                                              slot.getSlotId(), true, 
                                              buf);
      
      if (digest != null) digest.update(buf, 0, buf.length);
      
      int xfered = buf.length;
      ofs += xfered;               // only valid for streaming
      setLastSent(xfered);
      
      return slot;
   }
   
  /**
   * This routine allocates a slot for upload, and carries attempts to upload the
   *  appropriate data describe by the FileSlot.
   *
   * @return long Number of bytes transferred during transfer action
   */
   public long doTransfer() 
      throws AbortTransferException, IOException, DboxException {
      
      setLastSent(0);
      
      if (slot == null) {
         slot = dropbox.allocateUploadFileSlot(packid, fileid, 3);
      }
      
      if (slot == null) {
         return 0;
      }
      
      if (inpstrm != null && slot.getCurrentOffset() != ofs) {
         throw new DboxException("Streaming Upload failed, slot ofs (" + 
                                 slot.getCurrentOffset() + ") differs from my ofs(" 
                                 + ofs + ")");
      }
      
     // Use servlet method to transfer if JAXRPC is factory, otherwise, use buffer
     // Web xfer method has additional round trip to get new slot each time
      String fname = factory.getClass().getName();
      if (factory.getTopURL() != null &&
          !fname.equals("oem.edge.ed.odc.dropbox.service.helper.DirectConnectionFactory")) {
         try {
            doTransferHttp(slot);
         } finally {
            slot = null;
         }
      } else {
         slot=doTransferBuffer(slot);
      }
      
      return getLastSent();
   }
}
