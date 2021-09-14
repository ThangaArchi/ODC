package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
import java.io.*;
import java.net.*;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

public class StreamingDownloadOperation extends Operation {

   public static final long MAX_BUFFER_SIZE = 2 * 1024 * 1024;

   protected OutputStream outstrm;
   protected FileInfo finfo;
   
   public StreamingDownloadOperation(DropboxAccess srv, ConnectionFactory fac, 
                                     OutputStream os, long pid, long fid) {
      super(srv, fac, pid, fid);
      outstrm = os;
   }
      
  /**
   * Start up the thread to read the inpstrm. Only a single worker pls.
   */
   public boolean process() throws DboxException {
      synchronized(threads) {
         
        // If there is ANY culled data, can't do the upload
         try {
            finfo = srv.queryFile(fileid);
         } catch(java.rmi.RemoteException re) {
            throw new DboxException("Remote Exception received", re);
         }
         
        // Set bytes to download to entire size registered
         setTotalSize(finfo.getFileSize());
         setToXfer(getTotalSize());
            
        // Let super class start up thread
         setNumberOfWorkers(1);
         super.process();
      }
      return true;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
            try {
              //System.out.println("Doing validate");
               if (getStatus() < STATUS_TERMINATED) {
                  if (getRemainingLength() == 0) {
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
      
         java.security.MessageDigest digest;
         digest = java.security.MessageDigest.getInstance("MD5");
            
         DataDownloadTransfer ddt = new DataDownloadTransfer(srv, factory, 
                                                             packid, fileid, 
                                                             outstrm);
            
         while(getRemainingLength() > 0 && doOperation) {
            
            long len = getRemainingLength();
            if (len > MAX_BUFFER_SIZE) len = MAX_BUFFER_SIZE;
            long sent = ddt.doTransfer(getTotalXfered(), len);
            if (sent == 0) throw new DboxException("Ran out of bytes to download");
            transferUpdate(sent);
         }
         
        // If we read it all, check the MD5 ... if good, then GOOD
         if (getRemainingLength() == 0) {
            String lmd5 = toHexString(ddt.getDigest().digest());
            if (!finfo.getFileMD5().equals(lmd5)) {
               setStatus(STATUS_TERMINATED);
            }
         }
         
      } catch(Exception e) {
         System.out.println("Error while processing thread: " + toString());
         e.printStackTrace(System.out);
            
      } finally {
         if (outstrm != null) try {outstrm.close();} catch(Exception ee) {}
         
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
