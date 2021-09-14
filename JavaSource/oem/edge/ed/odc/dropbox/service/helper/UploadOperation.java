package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
import java.io.*;
import java.net.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
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
 * Uploads a local file to a dropbox package. This is the preferred
 *  helper to use when uploading file data as it provides restart capability as well
 *  supporting multiple upload channels. 
 */
public class UploadOperation extends Operation implements ActionListener {
   File file;
   FileInfo fileInfo = null;
         
  /**
   * Creates an Operation instance to upload a local file to a package.
   * 
   * @param srv   DropboxAccess proxy to communicate with dropbox session
   * @param f     File object describing where to read the local file data
   * @param pid   packageid containing destination file
   * @param fid   fileid descibing the destination file
   */
   public UploadOperation(DropboxAccess srv, File f, long pid, long fid) {
      super(srv, pid, fid);
      file = new File(f.getPath());
   }
  /**
   * Creates an Operation instance to upload a local file to a package.
   * 
   * @param srv   DropboxAccess proxy to communicate with dropbox session
   * @param f     File name describing where to read the local file data
   * @param pid   packageid containing destination file
   * @param fid   fileid descibing the destination file
   */
   public UploadOperation(DropboxAccess srv, String f, long pid, long fid) {
      super(srv, pid, fid);
      file = new File(f);
   }

  /**
   * Get the FileInfo object corresponding to the state of the file at the end of
   *  processing. The value null will be returned if the Operation is still 
   *  running or error with queryFile method.
   * @return FileInfo describing the dropbox File when transfer was completed (ended).
   */
   public FileInfo getFileInfo() {
     return fileInfo;
   }
   
   public boolean process() throws DboxException {
      boolean restarted = true;
      synchronized(threads) {
         
         try {
            if (!file.exists()) {
               throw new DboxException("File for upload does not exist! : " + 
                                       file.toString());
            }

			dataValidating();

            long flen = 0;
            flen = file.length();
            setTotalSize(flen);
         
           // Make all slots avail for me
            srv.releaseFileSlot(packid, fileid, srv.ALL_SLOTS);
            
            Vector slots = srv.queryFileSlots(packid, fileid);
            Iterator it = slots.iterator();
            long completed = 0;
            while(it.hasNext()) {
               FileSlot fs = (FileSlot)it.next();
               
               String md5 = fs.getMD5();
               
               if (md5 != null) {
                  String lmd5 = null;
                  try {
                     lmd5 = SearchEtc.calculateMD5(file, 
                                                   fs.getStartingOffset(), 
                                                   fs.getLength());
                  } catch(IOException ioe) {
                    // Assume that the offset/len were out of bounds for file
                     md5 = null;
                  }
                  
                  if (md5 == null || !lmd5.equals(md5)) {
                    //System.out.println("Slot does NOT match MD5!!: " + fs.toString());
                     md5 = null;
                  }
               }
               
              // If the MD5 value of the slot is no good ... remove the slot
               if (md5 == null) {
                  
                 // If this was the CULLED info, then the file is damaged.
                 //  Delete the file and start over
                  if (fs.getSlotId() == srv.CULLED_SLOT) {
                     
                    // If slot 0 is 0 len, then there would NOT need to be an MD5
                     if (fs.getLength() > 0) {
                        
                        FileInfo info = srv.queryFile(fileid);
                        srv.removeItemFromPackage(packid, fileid);
                        fileid = srv.uploadFileToPackage(packid, 
                                                         info.getFileName(), 
                                                         file.length());
                        restarted = false;
                        completed = 0;
                        break;
                     } else {
                       // if the CULLED_SLOT is 0 len, then ignore md5
                        continue;
                     }
                  } else {
                     srv.removeFileSlot(packid, fileid, fs.getSlotId());
                  }
               } else {
                  completed += fs.getLength();
               }
            }
            
           // Set bytes to upload
            setToXfer(getTotalSize()-completed);
            
           // Start the transfer ticker
            transferUpdate(0);
            
         } catch(java.rmi.RemoteException re) {
            throw new DboxException("Got remote exception", re);
         }
      }
      
     // Process OUTSIDE of threads synchronization
      super.process();
      
      return restarted;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
           //System.out.println("Doing validate");
            if (getStatus() < STATUS_TERMINATED) {
               try {
                  
                  fileInfo = srv.queryFile(fileid);
                  if (fileInfo.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
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
               
                 // Set as terminated if not at least there as of now
                  if (getStatus() < STATUS_TERMINATED) {
                     setStatus(STATUS_TERMINATED);
                  }
               
                  transferEnded();
                 //System.out.println("validate complete");
               }
            }
         }
      }
      return getStatus() == STATUS_FINISHED;
   }
      
   public void run() {
      
      RandomAccessFile rf = null;
      try {
      
         rf = new RandomAccessFile(file, "r");
         
         DataSlotTransfer dst = new DataSlotTransfer(srv, packid, fileid, rf);
         dst.setMaxBufferSize(getMaxBufferSize());
         dst.addProgressListener(this);
         
         while(doOperation) {
            long sent = dst.doTransfer();
            if (sent <= 0) break;
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
         if (rf != null) try {rf.close();} catch(Exception ee) {}
         
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
   
// No need to limit on client side for AFS issues ... done on server side.
//   public void setNumberOfWorkers(int v) {
//      if (v > 1) v = 1;
//      super.setNumberOfWorkers(v);
//   }
   
}
