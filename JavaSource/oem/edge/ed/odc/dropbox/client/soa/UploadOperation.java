package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
import java.io.*;
import java.net.*;

import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

public class UploadOperation extends Operation {
   File file;
   FileInfo fileInfo = null;
         
   public UploadOperation(DropboxAccess srv, ConnectionFactory fac, 
                          File f, long pid, long fid) {
      super(srv, fac, pid, fid);
      file = new File(f.getPath());
   }
   public UploadOperation(DropboxAccess srv, ConnectionFactory fac, 
                          String f, long pid, long fid) {
      super(srv, fac, pid, fid);
      file = new File(f);
   }

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
            
           // Let super class start up threads
            super.process();
         } catch(java.rmi.RemoteException re) {
            throw new DboxException("Got remote exception", re);
         }
      }
      return restarted;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
            try {
              //System.out.println("Doing validate");
               if (getStatus() < STATUS_TERMINATED) {
                  
                  fileInfo = srv.queryFile(fileid);
                  if (fileInfo.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
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
              transferEnded();
            }
         }
      }
      return getStatus() == STATUS_FINISHED;
   }
      
   public void run() {
      
      RandomAccessFile rf = null;
      try {
      
         rf = new RandomAccessFile(file, "r");
         
         DataSlotTransfer dst = new DataSlotTransfer(srv, factory, packid, fileid, rf);
         
         while(doOperation) {
            long sent = dst.doTransfer();
            if (sent <= 0) break;
            transferUpdate(sent);
         }
            
      } catch(Exception e) {
         System.out.println("Error while processing thread: " + toString());
         e.printStackTrace(System.out);
            
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
}
