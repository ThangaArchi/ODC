package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
import java.io.*;
import java.net.*;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;

abstract public class Operation implements Runnable {

   public static final int STATUS_STARTUP    = 0;
   public static final int STATUS_BEGIN      = 1;
   public static final int STATUS_INPROGRESS = 2;
   public static final int STATUS_AWAITING_CONFIRMATION = 49;
   public static final int STATUS_TERMINATED = 50;
   public static final int STATUS_FINISHED   = 51;
   public static final int STATUS_ABORTED    = 99;
   
   public static final int MAX_WORKERS       = 5;
   
   long fileid;
   long packid;
   Vector threads = new Vector();
   boolean doOperation = false;
   
   int numWorkers = 3;
      
   DropboxAccess srv;
   ConnectionFactory factory;
   
   int status = STATUS_STARTUP;
   protected  void setStatus(int v) { synchronized(threads) { status = v; }  }
   public     int  getStatus()              { return status;    }
   
      
  // for total stats
   long totXfered;
   long totToXfer;
   long totalsize;
   long starttime;
   long endtime;
   
   long lastTotXfered = 0;
   long lastTime      = 0;
   
   OperationListener listeners = null;
      
   public Operation(DropboxAccess srv, ConnectionFactory fac, 
                    long packid, long fileid) {
      this.packid  = packid;
      this.fileid  = fileid;
      this.srv     = srv;
      this.factory = fac;
   }

   // Listener registration:
   public void addOperationListener(OperationListener l) {
	   if (l == null) return;
	   listeners = DBEventMulticaster.addOperationListener(listeners,l);
   }
   public void removeOperationListener(OperationListener l) {
	   if (l == null) return;
	   listeners = DBEventMulticaster.removeOperationListener(listeners,l);
   }
   
   // Listener notification:
   public void dataValidating() {
	if (listeners != null) {
	   OperationEvent e = new OperationEvent(OperationEvent.MD5,this);
	   listeners.operationUpdate(e);
	}
   }
   public void dataTransferred() {
	  if (listeners != null) {
		 OperationEvent e = new OperationEvent(OperationEvent.DATA,this);
		 listeners.operationUpdate(e);
	  }
   }
   public void transferEnded() {
	  if (listeners != null) {
		 OperationEvent e = new OperationEvent(OperationEvent.ENDED,this);
		 listeners.operationUpdate(e);
	  }
   }
   
   public void startTimedOperation() {
      starttime = System.currentTimeMillis();
   }
   public void startTimedOperation(long l) {
      starttime = l;
   }
   public void endTimedOperation() {
      endtime = System.currentTimeMillis();
   }
   public void endTimedOperation(long l) {
      endtime = l;
   }
   
   public int  getNumberOfWorkers() {
      return numWorkers;
   }
   public void setNumberOfWorkers(int v) {
      if (v > MAX_WORKERS) {
         numWorkers = MAX_WORKERS;
      } else if (v > 0) {
         numWorkers = v;
      }
   }
   
  // Have to set the amount left to xfer and total size to get good stats.
  //  Should only be needed at Operation startup
   public void setTotalSize(long v) { 
      totalsize = v;
   }
   public void setToXfer(long v) { 
      totToXfer = v;
   }
   
   protected synchronized void setTotXfered(long v) {
      totXfered = v;
   }
      
   public long getPackageId()           { return packid;    }
   public long getFileId()              { return fileid;    }
   
   public long getTotalXfered()         { return totXfered; }
   public long getTotalConfirmed()      { return totXfered; }
   public long getToXfer()              { return totToXfer; }
   public long getTotalSize()           { return totalsize; }
   public long getRemainingLength()     { return totToXfer - totXfered; }
      
   public long getStartTime()           { return starttime; }
   public long getEndTime()             { return endtime;   }
   
   public int  percentDone() {
      if (totalsize == 0L) return 100;
      
      return(int) ((getTotalSize() - getRemainingLength()) * 100 / totalsize);

   }
   
   public boolean process() throws DboxException {
     //System.out.println("Doing Process");
         
      synchronized(threads) {
         
         if (threads.size() > 0) {
            throw new DboxException("Operation is already in Progress");
         }
            
         doOperation = true;
         Thread t;
            
         for(int i = 0; i < numWorkers; i++) {
            threads.add(t=new Thread(this)); t.start();
         }
      }
         
      startTimedOperation();
     //System.out.println("Process started");
     
     // we ALWAYS think restarting is possible
      return true;
   }
      
  // Loop here until done
   public void waitForCompletion() {
      while(!checkForCompletion(-1L));
   }
   
  // Loop here until done
   public void waitForCompletion(long ms) {
      checkForCompletion(ms);
   }
   
  // Wait UP TO specified MS value, then return if complete
   public boolean checkForCompletion() {
      return checkForCompletion(0);
   }
   
  // Wait UP TO specified MS value, then return if complete
   public boolean checkForCompletion(long ms) {
     //System.out.println("Doing waitForCompletion");
      
      long curwaitms = System.currentTimeMillis();
      long endwaitms = curwaitms + ms;
      
      synchronized(threads) {
         while(true) {
            if (threads.size() > 0) {
               try {
                  
                 //System.out.println("doing wait for operation to complete ...");
                  if (ms < 0) threads.wait();
                  else {
                     long towait = endwaitms - curwaitms;
                     if (towait <= 0) break;
                     threads.wait(towait);
                  }
                 //System.out.println("... either timeout or notified");
               } catch(Exception e) {
               }
            } else {
               break;
            }
            
            curwaitms = System.currentTimeMillis();
            
         } 
         
         return threads.size() == 0;
      }
     //System.out.println("waitForCompletion complete. Operation " + 
     //                    (getStatus() == STATUS_FINISHED?"Completed":"Failed"));
   }
   
  /**
   * This method will abort the operation and wait for the abort to complete
   */
   public void abort() {
     //System.out.println("Doing Abort");
      
      synchronized(threads) {
         if (doOperation) {
            doOperation = false;
            if (threads.contains(Thread.currentThread())) {
               System.out.println("Abort called from an Operation thread. no wait");
               return;
            }
            
            waitForCompletion();
            if (getStatus() < STATUS_TERMINATED) {
               System.out.println("HUH? status < terminated, and we are complete");
            } else if (getStatus() != STATUS_FINISHED) {
               setStatus(STATUS_ABORTED);
            }
            transferEnded(); 
         }
      }
     //System.out.println("Abort complete");
   }
      
   public abstract boolean validate();
   public abstract void run();
      
   public synchronized long getInstantaneousXferRate() {
      long curTime = System.currentTimeMillis();
      long diff    = curTime - lastTime;
      lastTime     = curTime;
      
      long totx    = getTotalXfered();
      long xdiff   = totx - lastTotXfered;
      lastTotXfered= totx;
      if (diff == 0) return 1;
      
      return ((xdiff * 1000)/ diff);
   }
   
   public synchronized void transferUpdate(long bytes) {
      totXfered += bytes;
      dataTransferred();
   }
      
   public synchronized long getXferRate() { 
      long etime = endtime;
      if (etime == 0) {
         etime = System.currentTimeMillis();
      }
      
      long millis = etime - starttime;
      if (millis <= 0) millis = 1;
      
      return (totXfered*1000)/millis;
   }
   
   static public String toHexString(byte arr[]) {
      StringBuffer ans = new StringBuffer();
      for(int i=0 ; i < arr.length; i++) {
         String v = Integer.toHexString(((int)arr[i]) & 0xff);
         if (v.length() == 1) ans.append("0");
         ans.append(v);
      }
      
      return ans.toString();
   }
   
   public String toString() {
      StringBuffer sb = new StringBuffer("Operation:\n");
      sb.append("  packid    = ").append(packid).append("\n");
      sb.append("  fileid    = ").append(fileid).append("\n");
      sb.append("  totXfered = ").append(totXfered).append("\n");
      sb.append("  toToXfer  = ").append(totToXfer).append("\n");
      sb.append("  remaining = ").append(getRemainingLength()).append("\n");
      sb.append("  totalSize = ").append(totalsize).append("\n");
      return sb.toString();
   }
}
   
