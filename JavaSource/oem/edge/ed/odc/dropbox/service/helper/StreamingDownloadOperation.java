package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
import java.io.*;
import java.net.*;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

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
 * Downloads a file from a package and stores it to the provided stream. This
 *  helper should be used over the DownloadOperation class only if you need
 *  to stream the data (ie. cannot store it into a RandomAccessFile).  Only a single
 *  worker/channel can be used to accomplish the download, and restarts are not
 *  supported.
 */
public class StreamingDownloadOperation extends Operation {

   public static final long MAX_BUFFER_SIZE = 2 * 1024 * 1024;

   protected OutputStream outstrm;
   protected FileInfo finfo;
   
  /**
   * Creates an Operation instance to download a file from a package.
   * 
   * @param srv   DropboxAccess proxy to communicate with dropbox session
   * @param os    Stream where downloaded data should be written
   * @param pid   packageid containing file to download
   * @param fid   fileid describing file to download from package    
   */
   public StreamingDownloadOperation(DropboxAccess srv, OutputStream os, 
                                     long pid, long fid) {
      super(srv, pid, fid);
      outstrm = os;
   }
      
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
            
        // Let super class start up thread. Limit to one
         if (getNumberOfWorkers() > 1) setNumberOfWorkers(1);
      }
      
     // Process OUTSIDE of threads synchronization
      super.process();
      
      return true;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
           //System.out.println("Doing validate");
            if (getStatus() < STATUS_TERMINATED) {
               try {
                  if (getRemainingLength() == 0) {
                     setStatus(STATUS_FINISHED);
                  } else {
                     setStatus(STATUS_TERMINATED);
                  }
                  
                 // Register the transfer speed info
                  srv.registerAuditInformation(packid, fileid, 
                                               getTotalXfered(),
                                               getElapsedTime(),
                                               false);
               } catch(Exception e) {
                  if (getVerbose()) {
                     System.out.println("Error while querying status of file " + fileid);
                  }
                  return false;
               } finally {
                 //System.out.println("validate complete");
                  transferEnded();
               }
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
            
         DataDownloadTransfer ddt = new DataDownloadTransfer(srv, packid, 
                                                             fileid, outstrm);
         ddt.setMaxBufferSize(getMaxBufferSize());
         ddt.addProgressListener(this);
            
         while(getRemainingLength() > 0 && doOperation) {
            
            long len = getRemainingLength();
            if (len > MAX_BUFFER_SIZE) len = MAX_BUFFER_SIZE;
            long sent = ddt.doTransfer(getTotalXfered(), len);
            if (sent == 0) throw new DboxException("Ran out of bytes to download");
         }
         
        // If we read it all, check the MD5 ... if good, then GOOD
         if (getRemainingLength() == 0) {
            String lmd5 = toHexString(ddt.getDigest().digest());
            if (!finfo.getFileMD5().equals(lmd5)) {
               setStatus(STATUS_TERMINATED);
            }
         }
         
      } catch(AbortTransferException e) {
        // Do nothing for Abort exception
      } catch(Exception e) {
         addError(e);
         if (getVerbose()) {
            System.out.println("Error while processing thread: " + toString());
            e.printStackTrace(System.out);
         }
            
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
   
  // We never allow more than 1 worker thread
   public void setNumberOfWorkers(int v) {
      if (v > 1) v = 1;
      super.setNumberOfWorkers(v);
   }
   
}
