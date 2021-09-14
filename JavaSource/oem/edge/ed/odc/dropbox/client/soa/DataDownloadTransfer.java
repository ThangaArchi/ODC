package oem.edge.ed.odc.dropbox.client.soa;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.DboxException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.Vector;

import java.io.*;
import java.net.*;

/*
** Encapsulate download mechanics
*/
public class DataDownloadTransfer {

   RandomAccessFile  rf;
   OutputStream      outstrm;
   long              packid;
   long              fileid;
   DropboxAccess     dropbox;
   ConnectionFactory factory;
   
   Vector            listeners = new Vector();
   
   String            encoding  = null;
   
   boolean           doOperation = true;
   
   long              ofs;
   java.security.MessageDigest digest;
   
   public DataDownloadTransfer(DropboxAccess dropbox, ConnectionFactory fac, 
                               long packid, long fileid, RandomAccessFile f) {
   
      this.dropbox   = dropbox;
      this.factory   = fac;
      this.packid    = packid;
      this.fileid    = fileid;
      this.rf        = f;
   }
   
   public DataDownloadTransfer(DropboxAccess dropbox, ConnectionFactory fac, 
                               long packid, long fileid, OutputStream outstrm) {
      this.dropbox   = dropbox;
      this.factory   = fac;
      this.packid    = packid;
      this.fileid    = fileid;
      this.outstrm   = outstrm;
      
      try {
         digest         = java.security.MessageDigest.getInstance("MD5");
      } catch(Exception ee) {}
   }
   
   public DataDownloadTransfer(DropboxAccess dropbox, ConnectionFactory fac, 
                               long packid, String encoding, OutputStream outstrm) {
      this.dropbox   = dropbox;
      this.factory   = fac;
      this.packid    = packid;
      this.fileid    = -1;
      this.outstrm   = outstrm;
      this.encoding  = encoding;
      
      try {
         digest         = java.security.MessageDigest.getInstance("MD5");
      } catch(Exception ee) {}
   }
   
   public java.security.MessageDigest getDigest() { return digest; }
   
   
  /*
  **       COMMAND        =  UPLOAD || DOWNLOAD
  **       DBOXSESSIONID  =  the sessionid
  **       PACKID         =  packid
  **       FILEID         =  fileid
  **       SLOTID         =  slotid_for_upload or ofsset for download
  **       XFERSIZE       =  number of bytes to xfer
  */
  
  
   protected long doTransferHttp(long rofs, long rlen) 
      throws IOException, DboxException {
      
      HttpURLConnection conn = null;
      InputStream         is = null;
      try {
         factory.getTopURL();
         URL theurl = new URL(factory.getTopURL() + "/DropboxTransfer");
         
         conn = (HttpURLConnection)theurl.openConnection();
         conn.setDoOutput(true);
         conn.setDoInput(true);
         conn.setAllowUserInteraction(false);
         
         String sessionid = factory.getSessionId(dropbox);
         
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
         is = conn.getInputStream();
         
         int rc = conn.getResponseCode();
         if (rc < 200 || rc >= 300) {
            throw new IOException("Bad response from download request: " + 
                                  theurl.toString());
         }
         
         if (rf != null) rf.seek(rofs);
         
         java.security.MessageDigest ldigest = null;
         try {
            if (digest != null) {
               ldigest = (java.security.MessageDigest)digest.clone();
            }
         } catch(Exception ee) {}
         
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
            if (ldigest != null) ldigest.update(buf, 0, r);
            xfered += r;
         }
         
         is.close();
         is = null;
         
         if (doOperation == false) {
            throw new IOException("Transfer aborted by user");
         }
                  
         digest = ldigest;
         
         ofs += xfered;           // only valid for streaming
         
         return xfered;
         
      } finally {
         try {
            if (is != null) {
               is.close();
            }
         } catch(Exception e) {
         }
      }
   }
   
   protected long doTransferBuffer(long rofs, long rlen) 
      throws DboxException, IOException {
      
      if (fileid == -1) {
         throw new 
            DboxException("Can't do package download with buffer transfer method!");
      }
      
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
   
   public long doTransfer(long rofs, long rlen) throws IOException, DboxException {
      
     // If streaming, and offset != expected offset ... error
      if (outstrm != null && rofs != ofs) {
         throw new DboxException("Streaming Download failed, slot ofs (" + 
                                 rofs + ") differs from my ofs(" + ofs + ")");
      }
      
     // Use servlet method to transfer if JAXRPC is factory, otherwise, use buffer
     // Web xfer method has additional round trip to get new slot each time
      String fname = factory.getClass().getName();
      if (factory.getTopURL() != null &&
          !fname.equals("oem.edge.ed.odc.dropbox.client.soa.DirectConnectionFactory")) {
         return doTransferHttp(rofs, rlen);
      } else {
         return doTransferBuffer(rofs, rlen);
      }
   }
   
   public void addProgressListener(ActionListener list) { 
      listeners.add(list);
   }
   
   protected void sendEvent(ActionEvent ev) {
     // If there are any registered listeners, call them now
      Iterator it = listeners.iterator();
      while(it.hasNext()) {
         ((ActionListener)it.next()).actionPerformed(ev);
      }
   }
   
}
