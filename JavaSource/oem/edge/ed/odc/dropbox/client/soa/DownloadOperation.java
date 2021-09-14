package oem.edge.ed.odc.dropbox.client.soa;

import java.lang.*;
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

public class DownloadOperation extends Operation implements ActionListener {

   final static long RESERVATION_LENGTH = 2 * 1024 * 1024;

   File file;
   
   FileInfo fileinfo;
   
   long nextReservationOfs = 0;
   Vector returnedReservations = null;
      
   public DownloadOperation(DropboxAccess srv, ConnectionFactory fac, 
                            File f, long pid, FileInfo finfo) {
      super(srv, fac, pid, finfo.getFileId());
      fileinfo = finfo;
      file = new File(f.getPath());
   }
   public DownloadOperation(DropboxAccess srv, ConnectionFactory fac, 
                            File f, long pid, long fileid) throws Exception {
      super(srv, fac, pid, fileid);
      
      fileinfo = srv.queryFile(fileid);
      file = new File(f.getPath());
   }
   public DownloadOperation(DropboxAccess srv, ConnectionFactory fac, 
                            String f, long pid, FileInfo finfo) {
      super(srv, fac, pid, finfo.getFileId());
      fileinfo = finfo;
      file = new File(f);
   }
   
  /**
   * Returns a valid starting offset
   */
   protected long getReservation() {
      long ret = -1;
      long nextReservationLen = -1;
      synchronized(threads) {
         if (returnedReservations != null && returnedReservations.size() > 0) {
            ret = ((Long)returnedReservations.remove(returnedReservations.size()-1)).longValue();
         } else {
            long fsize = fileinfo.getFileSize();
            nextReservationLen = fsize - nextReservationOfs;
            if (nextReservationLen > 0) {
               if (nextReservationLen > RESERVATION_LENGTH) {
                  nextReservationLen = RESERVATION_LENGTH;
               }
            
               ret = nextReservationOfs;
               nextReservationOfs += nextReservationLen;
            }
         }
      }
      
     //System.out.println("getReservation: ret[" + ret + "] len[" + nextReservationLen + 
     //                   "] next[" + nextReservationOfs + "]");
      return ret;
   }
   
   protected void returnReservation(long v) {
      synchronized(threads) {
         if (v >= 0) {
            if (returnedReservations == null) {
               returnedReservations = new Vector();
            }
            
            returnedReservations.add(new Long(v));
           //System.out.println("returnReservation: [" + v + "]");
         }
      }
   }
   
   public boolean process() throws DboxException {
      boolean restarted = true;
      synchronized(threads) {
      
         long restartofs = 0;
         
         long flen = fileinfo.getFileSize();
         setTotalSize(flen);
         
        // If the file for download exists, do restart (if possible)
         if (file.exists()) {
         
            if (!file.isFile()) {
               throw new DboxException("Destination for file download is NOT valid: " + 
                                       file.toString());
            }
            
            dataValidating();
            
            long lflen = file.length();
            
           // Limit local file to remote file size
            if (lflen > flen) lflen = flen;
            
            if (lflen > 0) {
            
              //System.out.println("Download: Have a local file already of size: " + lflen);
                              
              //System.out.println("Download: md5arr len = " + md5arr.length);
               
               byte buf[] = new byte[32768];
               RandomAccessFile fis = null;
               
               try {
               
                 // Get some valid start points
                  String md5arr[] = srv.getPackageItemMD5(packid, 
                                                          fileinfo.getFileId(),
                                                          lflen);
               
                  fis = new RandomAccessFile(file, "r");
                  
                  java.security.MessageDigest digest;
                  digest = java.security.MessageDigest.getInstance("MD5");
               
                 // for each returned startpoint, check if valid. If restartofs > 0 at the
                 //  end, then we found a restart point
                 //
                 // ofs var starts at shortest returned MD5 point, and works forward from there
                  long ofs = lflen - ((1024*1024)*(md5arr.length-1));
                  for (int i=md5arr.length; i > 0; i--, ofs += 1024*1024) {
                     long readlen = ofs - restartofs;
                     
                     while(readlen > 0) {
                        int r = (int)(readlen > buf.length ? buf.length : readlen);
                        r = fis.read(buf, 0, r);
                        if (r == -1) {
                           throw new IOException("Ran out of bytes before finished MD5");
                        }
                        digest.update(buf, 0, r);
                        readlen -= r;
                     }
                     
                    // get MD5 String value to compare
                     byte arr[] = ((java.security.MessageDigest)digest.clone()).digest();
                     StringBuffer ans = new StringBuffer();
                     for(int j=0 ; j < arr.length; j++) {
                        String v = Integer.toHexString(((int)arr[j]) & 0xff);
                        if (v.length() == 1) ans.append("0");
                        ans.append(v);
                     }
                     
                    //System.out.println("Download: ofs[" + ofs + "] lmd5[" + ans.toString() + 
                    //                   "] rmd5[" + md5arr[i-1] + "]");
                     
                    // If md5 matches, set restartofs
                     if (ans.toString().equals(md5arr[i-1])) {
                        restartofs = ofs;
                     } else {
                        break;
                     }
                  }
               } catch(Exception e) {
               } finally {
                  if (fis != null) try { fis.close(); } catch(Exception eee) {}
               }
               
              // If we can't restart, mark that, and delete file
               if (restartofs == 0) {
                  restarted = false;
                  if (!file.delete()) {
                     throw 
                        new DboxException("Error deleting local file after failure to restart: " + 
                                          file.toString());
                  } 
               } else {
                 // get REAL local file len for truncation check
                  lflen = file.length();
                  
                 // If local file is GREATER than remote file, then we need to truncate it
                  if (lflen > flen) {
                     restarted = false;
                     restartofs = 0;
                     System.out.println("DownloadOperation: TODOTODOTODO!! use reflection to check for getChannel so we can truncate file!!");
                     System.out.println("... its bigger than file being downloaded AND could be restarted!");
                     if (!file.delete()) {
                        throw 
                           new DboxException("Error deleting local file after failure to restart (truncation): " + 
                                             file.toString());
                     } 
                  }
               }
               nextReservationOfs = restartofs;
            }
         }
         
        // Set bytes to upload
         setToXfer(getTotalSize()-restartofs);
            
        // Let super class start up threads
         super.process();
      }
      return restarted;
   }
      
   public boolean validate() {
      synchronized (threads) {
        //System.out.println("Doing validate");
         if (threads.size() == 0) {
            if (getStatus() < STATUS_TERMINATED) {
               if (getRemainingLength() == 0) {
                  setStatus(STATUS_FINISHED);
               } else {
                  setStatus(STATUS_TERMINATED);
               }
            }
            transferEnded();
         }
         
         return getStatus() == STATUS_FINISHED;
      }
   }
      
  // gets called with num bytes xfered as ID
   public void actionPerformed(ActionEvent ev) {
      transferUpdate(ev.getID());
   }
      
   public void run() {
      
     //System.out.println("In run for thread: " + toString());
      long reserveOfs = -1;
      long filesize = fileinfo.getFileSize();
      RandomAccessFile rf = null;
         
      try {
      
         rf = new RandomAccessFile(file, "rw");
         
        // obtain and flush to disk the reservation amount
         while(doOperation) {
         
            DataDownloadTransfer ddt = new DataDownloadTransfer(srv, factory, 
                                                                packid, fileid, 
                                                                rf);
            ddt.addProgressListener(this);
            
           // getReservation always returns amt to match RESERVATION_LENGTH, unless its at EOF
            reserveOfs      = getReservation();
            long reserveLen = RESERVATION_LENGTH;
            if (reserveOfs + reserveLen > filesize) {
               reserveLen = filesize-reserveOfs;
            }
            
           // if we are done, then bag out
            if (reserveOfs < 0) break;
         
            long lreserveLen = reserveLen;
            long lreserveOfs = reserveOfs;
            while(lreserveLen > 0) {
            
               long sent = ddt.doTransfer(lreserveOfs, lreserveLen);               
               
               if (sent == 0) throw new DboxException("Ran out of bytes to download");
               
              // Added a listener, he does this, but on smaller chuncjs
              //transferUpdate(sent);
               
               lreserveLen -= sent;
               lreserveOfs += sent;
            }
            
            reserveOfs = -1;
         }
         
      } catch(Exception e) {
         System.out.println("Error while processing thread: " + toString());
         e.printStackTrace(System.out);
            
      } finally {
      
         if (rf != null) try {rf.close();} catch(Exception ee) {}
         
        //System.out.println("In finally for thread: " + toString());
         synchronized(threads) {
         
           // Send back anything that we reserved and did not finish
            returnReservation(reserveOfs);
            
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
