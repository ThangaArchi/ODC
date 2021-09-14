package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
import java.io.*;
import java.net.*;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

public class StreamingUploadOperation extends Operation {
   InputStream inpstrm;
   
   public static final int UPLOAD_BUFFER_SIZE = 2*1024*1024;
   public static final long DEFAULT_SIZE = 0xFFFFFFFFFFFFL;
      
   public StreamingUploadOperation(DropboxAccess srv, ConnectionFactory fac, 
                                   InputStream is, long pid, long fid) {
      super(srv, fac, pid, fid);
      inpstrm = is;
      
     // Default is PRETTY darn big
      setTotalSize(DEFAULT_SIZE);
   }
      
  /**
   * Start up the thread to read the inpstrm. Only a single worker pls.
   */
   public boolean process() throws DboxException {
      synchronized(threads) {
         
         try {
           // Dump any existing fileslot data
            srv.removeFileSlot(packid, fileid, srv.ALL_SLOTS);
            
           // If there is ANY culled data, can't do the upload
            FileInfo  finfo = srv.queryFile(fileid);
            if (finfo.getFileSize() > 0) {
               throw new DboxException("Filesize > 0 ... cannot stream upload: \n" + 
                                       finfo.toString());
            }
         
           // Set bytes to upload to entire size registered
            setToXfer(getTotalSize());
            
           // Let super class start up thread
            setNumberOfWorkers(1);
            super.process();
         } catch (java.rmi.RemoteException re) {
            throw new DboxException("Got remote exception", re);
         }
      }
      return true;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
            try {
              //System.out.println("Doing validate");
               if (getStatus() < STATUS_TERMINATED) {
                  
                  FileInfo info = srv.queryFile(fileid);
                  if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
                     setStatus(STATUS_FINISHED);
                  } else {
                     setStatus(STATUS_TERMINATED);
                  }
               }
            } catch(Exception e) {
               System.out.println("Error while querying status of file " + fileid);
               return false;
            } finally {
              //System.out.println("validate complete");
            }
         }
      }
      return getStatus() == STATUS_FINISHED;
   }
      
   public void run() {
      
     //System.out.println("In run for thread: " + toString());
         
      try {
      
        // Fixed buffer (sigh)
         byte buf[] = new byte[UPLOAD_BUFFER_SIZE];
         
         long ofs = 0;
         
         DataSlotTransfer dst = new DataSlotTransfer(srv, factory, packid, 
                                                     fileid, inpstrm);
         
         while(doOperation) {
            long sent = dst.doTransfer();
            if (sent <= 0) break;
            ofs += sent;
            transferUpdate(sent);
         }
         
        // If the stream is empty ... assume we can do the commit
         if (inpstrm.read() == -1 && getTotalSize() == DEFAULT_SIZE) {
            srv.commitUploadedFile(packid, fileid, ofs, 
                                   toHexString(dst.getDigest().digest()));
         }
         
      } catch(Exception e) {
         System.out.println("Error while processing thread: " + toString());
         e.printStackTrace(System.out);
            
      } finally {
         if (inpstrm != null) try {inpstrm.close();} catch(Exception ee) {}
         
        //System.out.println("In finally for thread: " + toString());
         synchronized(threads) {
            threads.remove(Thread.currentThread());
            if (threads.size() == 0) {
               endTimedOperation();
               validate();
            }
            threads.notifyAll();
         }
      }
   }
}
