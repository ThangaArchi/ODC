package oem.edge.ed.odc.dropbox.service.helper;

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
 * Downloads a file from a package and stores it local file. This is the preferred
 *  helper to use when downloading files as it provides restart capability as well
 *  supporting multiple download channels. 
 */
public class DownloadOperation extends Operation {

   final static long RESERVATION_LENGTH = 2 * 1024 * 1024;
   final static long MIN_TAIL_BLOCK_SIZE   = 10000;          // Size of final read

   File file;
   
   FileInfo fileinfo;
   
   long nextReservationOfs = 0;
   Vector returnedReservations = null;
   HashMap outReservations = null;
      
  /**
   * Creates an Operation instance to download a file from a package.
   * 
   * @param srv   DropboxAccess proxy to communicate with dropbox session
   * @param f     File object describing where to store the downloaded file
   * @param pid   packageid containing file to download
   * @param finfo Object describing file to download from package    
   */
   public DownloadOperation(DropboxAccess srv, File f, long pid, FileInfo finfo) {
      super(srv, pid, finfo.getFileId());
      fileinfo = new FileInfo(finfo);
      file = new File(f.getPath());
   }
   
  /**
   * Creates an Operation instance to download a file from a package. This constructor
   *  will invoke the proxy to get a FileInfo object (queryFile), so if you have one
   *  handy already, use the other constructor forms.
   * 
   * @param srv    DropboxAccess proxy to communicate with dropbox session
   * @param f      File object describing where to store the downloaded file
   * @param pid    packageid containing file to download
   * @param fileid Fileid for the file to download
   */
   public DownloadOperation(DropboxAccess srv, File f, long pid, 
                            long fileid) throws Exception {
      super(srv, pid, fileid);
      
      fileinfo = srv.queryFile(fileid);
      file = new File(f.getPath());
   }
   
  /**
   * Creates an Operation instance to download a file from the package.
   * 
   * @param srv   DropboxAccess proxy to communicate with dropbox session
   * @param f     String containing file describing where to store the downloaded data
   * @param pid   packageid containing file to download
   * @param finfo Object describing file to download from package    
   */
   public DownloadOperation(DropboxAccess srv, String f, long pid, FileInfo finfo) {
      super(srv, pid, finfo.getFileId());
      fileinfo = new FileInfo(finfo);
      file = new File(f);
   }
   
  /**
   * Returns the associated length for the specified reservation offset, -1 if 
   *  invalid reservation
   */
   protected int getReservationLength(long ofs) {
      long fsize   = fileinfo.getFileSize();
      long lastofs = fsize - MIN_TAIL_BLOCK_SIZE;
      long llen = fsize - ofs;
      
      if (lastofs < 0) lastofs = 0;
      
      if (llen > 0) {
         if (llen > RESERVATION_LENGTH) {
            llen = RESERVATION_LENGTH;
         }
         
         long endofs = ofs+llen;
         
        // If we are asking about something inside the last slot, error
         if (ofs > lastofs) return -1;
         
        // If we are diving into the last block, and are not taking the
        //  entire thing ... correct for that
         if (endofs > lastofs && endofs < fsize) {
            llen = lastofs-ofs;
         }
      } else if (llen < 0) {
         llen = -1;
      }
      
      return (int)llen;
   }
   
  /**
   * Returns a valid starting offset for download. Only return the final segment
   *  once all others have been completed.
   */
   protected long getReservation() {
      long ret = -1;
      long fsize   = fileinfo.getFileSize();
      long lastofs = fsize - MIN_TAIL_BLOCK_SIZE;
      
      if (lastofs < 0) lastofs = 0;
      
      synchronized(threads) {
         if (returnedReservations != null && returnedReservations.size() > 0) {
            ret = ((Long)returnedReservations.remove(returnedReservations.size()-1)).longValue();
         } else {
            int nextReservationLen = getReservationLength(nextReservationOfs);
            if (nextReservationLen > 0) {
            
              // If this is NOT the last block add it ... OR
              //  if it IS the last block, only let it go if there are NO
              //    other outstanding reservations. The thought here is that
              //    the last block should not be started upon until ALL other
              //    blocks are finished.
               if (nextReservationOfs + nextReservationLen != fsize ||
                  (outReservations == null || outReservations.size() == 0)) {
                  ret = nextReservationOfs;
                  nextReservationOfs += nextReservationLen;
               }
            }
         }
         
        // Show that this reservation is out and pending
         if (ret >= 0) {
            if (outReservations == null) {
               outReservations = new HashMap();
            }
            outReservations.put(new Long(ret), "");
         }
      }
      
     //System.out.println("getReservation: ret[" + ret + "] len[" + nextReservationLen + 
     //                   "] next[" + nextReservationOfs + "]");
      return ret;
   }
   
  /**
   * validates that the reservation has been fulfilled
   */
   protected void validateReservation(long v) {
      synchronized(threads) {
         if (v >= 0) {
            if (outReservations == null) {
               return;
            }
            
            outReservations.remove(new Long(v));
           //System.out.println("outReservation: [" + v + "]");
         }
      }
   }
   
  /**
   * Done with reservation, return leftovers to be handed out to next requestor
   */
   protected void returnReservation(long v) {
      synchronized(threads) {
         if (v >= 0) {
            if (returnedReservations == null) {
               returnedReservations = new Vector();
            }
            
            Long l = new Long(v);
            if (outReservations != null) {
               outReservations.remove(l);
            }
            
            returnedReservations.add(l);
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
               
              // If we have a restart ... make sure we start PRIOR to tail block
              // If NOT, then take entire tail block
               if (restartofs > 0) {
                  if (getReservationLength(restartofs) < 0) { 
                     restartofs = getTotalSize() - MIN_TAIL_BLOCK_SIZE;
                  }
               }
               
               nextReservationOfs = restartofs;
            }
         }
         
        // Set bytes to upload
         setToXfer(getTotalSize()-restartofs);
            
      }
      
     // Let super class start up threads OUTSIDE of synchronized call
      super.process();
      
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
               
               try {
                 // Register the transfer speed info
                 // If we fail for whatever reason, don't even complain
                  srv.registerAuditInformation(packid, fileid, 
                                               getTotalXfered(),
                                               getElapsedTime(),
                                               false);
               } catch(Exception e) {}
               
               transferEnded();
            }
         }
         
         return getStatus() == STATUS_FINISHED;
      }
   }
      
   public void run() {
      
     //System.out.println("In run for thread: " + toString());
      long reserveOfs = -1;
      long filesize = fileinfo.getFileSize();
      RandomAccessFile rf = null;
         
      try {
      
         rf = new RandomAccessFile(file, "rw");
         DataDownloadTransfer ddt = new DataDownloadTransfer(srv, packid, fileid, rf);
         ddt.setMaxBufferSize(getMaxBufferSize());
         ddt.addProgressListener(this);
         
        // obtain and flush to disk the reservation amount
         while(doOperation) {
            
           // getReservation always returns amt to match RESERVATION_LENGTH, unless its 
           // at EOF OR the last section (which is last N bytes). The last N bytes must
           // be downloaded as a unit. N, today, is 10000.
            reserveOfs      = getReservation();
            long reserveLen = getReservationLength(reserveOfs);
            
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
            
            validateReservation(reserveOfs);
            reserveOfs = -1;
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
