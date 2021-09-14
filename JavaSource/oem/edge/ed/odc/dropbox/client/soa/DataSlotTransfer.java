package oem.edge.ed.odc.dropbox.client.soa;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.DboxException;

import java.io.*;
import java.net.*;

/*
** Move the specified fileslot to the server
*/
public class DataSlotTransfer {

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
   public void setLastSent(long v) { lastSent = v;    }
   public long getLastSent()       { return lastSent; }
   
   public DataSlotTransfer(DropboxAccess dropbox, ConnectionFactory fac, 
                           long packid, long fileid, RandomAccessFile f) {
      this.dropbox   = dropbox;
      this.factory   = fac;
      this.packid    = packid;
      this.fileid    = fileid;
      this.rf        = f;
   }
   
   public DataSlotTransfer(DropboxAccess dropbox, ConnectionFactory fac, 
                           long packid, long fileid, InputStream inpstrm) {
      this.dropbox   = dropbox;
      this.factory   = fac;
      this.packid    = packid;
      this.fileid    = fileid;
      this.inpstrm   = inpstrm;
      
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
  
  
   protected void doTransferHttp(FileSlot slot) throws IOException, DboxException {
      HttpURLConnection conn = null;
      OutputStream       out = null;
      try {
         factory.getTopURL();
         URL theurl = new URL(factory.getTopURL() + "/DropboxTransfer");
         
         conn = (HttpURLConnection)theurl.openConnection();
         conn.setDoOutput(true);
         conn.setDoInput(true);
         conn.setAllowUserInteraction(false);
         
         String sessionid = factory.getSessionId(dropbox);
         
         long len = slot.getRemainingBytes();
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
         while(len > 0) {
            int l = buf.length;
            if (l > len) l = (int)len;
            
            int r = 0;
            if (rf != null) r = rf.read(buf, 0, l);
            else            r = inpstrm.read(buf, 0, l);
            
            if (r < 0) {
               break;
            }
            len -= r;
            out.write(buf, 0, r);
            if (ldigest != null) ldigest.update(buf, 0, r);
         }
         
         out.close();
         out = null;
         
         int rc = conn.getResponseCode();
         if (rc < 200 || rc >= 300) {
            throw new IOException("Bad response from upload request: " + 
                                  theurl.toString());
         }
         
         long xfered = slot.getRemainingBytes()-len;
         
         digest = ldigest;
         
         setLastSent(xfered);
         ofs += xfered;           // only valid for streaming
         
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
   
   protected FileSlot doTransferBuffer(FileSlot slot) 
      throws IOException, DboxException {
   
      int rb = (int)slot.getRemainingBytes();
      if (rb > UPLOAD_BUFFER_SIZE) rb = UPLOAD_BUFFER_SIZE;
      
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
      while(rb > 0) {
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
         
         rb -= r;
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
   
   public long doTransfer() throws IOException, DboxException {
      
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
          !fname.equals("oem.edge.ed.odc.dropbox.client.soa.DirectConnectionFactory")) {
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
