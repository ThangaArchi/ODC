package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
import java.io.*;
import java.net.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
 * Uploads a file to a package by reading from a provided stream. This  
 *  helper should be used over the UploadOperation class only if you need
 *  to stream the data (ie. cannot read it from a RandomAccessFile).  Only a single
 *  worker/channel can be used to accomplish the upload, and restarts are not
 *  supported.
 * <p>
 * Note that the actual file size is NOT known in advance. If the size IS known,
 *  then that should be provided after the object is created but prior to invoking
 *  process. With out that information, the Operation reads until the stream returns
 *  EOF.
 */
public class StreamingUploadOperation extends Operation {
   InputStream inpstrm;
   
   public static final int UPLOAD_BUFFER_SIZE = 2*1024*1024;
   public static final long DEFAULT_SIZE = 0xFFFFFFFFFFFFL;
      
   public StreamingUploadOperation(DropboxAccess srv, InputStream is, 
                                   long pid, long fid) {
      super(srv, pid, fid);
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
            
           // Let super class start up thread. Limit to one
            if (getNumberOfWorkers() > 1) setNumberOfWorkers(1);
            
           // Start the transfer ticker
            transferUpdate(0);
            
         } catch (java.rmi.RemoteException re) {
            throw new DboxException("Got remote exception", re);
         }
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
                        
                  FileInfo info = srv.queryFile(fileid);
                  if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
                     setStatus(STATUS_FINISHED);
                  } else {
                     setStatus(STATUS_TERMINATED);
                  }
                  
                 // Register the transfer speed info
                  srv.registerAuditInformation(packid, fileid, 
                                               getTotalXfered(),
                                               getElapsedTime(),
                                               true);
               } catch(Exception e) {
                  if (getVerbose()) {
                     System.out.println("Error while querying status of file " + fileid);
                  }
                  return false;
               } finally {
                 // Set as terminated if not there as of now
                  if (getStatus() < STATUS_TERMINATED) {
                     setStatus(STATUS_TERMINATED);
                  }
               
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
      
         long ofs = 0;
         
         DataSlotTransfer dst = new DataSlotTransfer(srv, packid, fileid, inpstrm);
         dst.setMaxBufferSize(getMaxBufferSize());
         dst.addProgressListener(this);
         
         while(doOperation) {
            long sent = dst.doTransfer();
            if (sent <= 0) break;
            ofs += sent;
         }
         
        // If the stream is empty ... assume we can do the commit
         if (getTotalSize() == DEFAULT_SIZE && inpstrm.read() == -1) {
            srv.commitUploadedFile(packid, fileid, ofs, 
                                   toHexString(dst.getDigest().digest()));
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
   
  // We never allow more than 1 worker thread
   public void setNumberOfWorkers(int v) {
      if (v > 1) v = 1;
      super.setNumberOfWorkers(v);
   }
   
}
