package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
import java.io.*;
import java.net.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.Nester;

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
 * This class serves as the abstract base for all Upload and Download helpers. An
 * Operation supports the movement of data from here to there, and so the base
 * functionality allows access to values such as trasfer statistics (how fast, how
 * complete) and status (starting, inprogress, finished ...)  as well as providing
 * the polymorphic structure  to process the the operation (process, run, validate)
 */
abstract public class Operation implements Runnable, ActionListener {

  /**
   * Status value in effect prior to process being called
   */
   public static final int STATUS_STARTUP    = 0;
   
  /**
   * Status value in effect once process has been called, and channels are allocated
   */
   public static final int STATUS_INPROGRESS = 2;
   
  /**
   * Status value in effect once data transfer process has been validated to be 
   *  complete and unsuccessful
   */
   public static final int STATUS_TERMINATED = 50;
   
  /**
   * Status value in effect once the data transfer process has been validated to be 
   *  complete and successful
   */
   public static final int STATUS_FINISHED   = 51;
   
  /**
   * Status value in effect once the data transfer process has bee terminated via
   *  the abort method
   */
   public static final int STATUS_ABORTED    = 99;
   
  /**
   * Total number of channels that can be running for the operation
   */
   public static final int MAX_WORKERS       = 5;
   
   
  // Minimum transfer size needed for each additional worker thread
   private static final int MEANINGFUL_WORKER_SIZE = 2*1024*1024;
   
   
   long fileid;
   long packid;
   Vector threads = new Vector();
   boolean doOperation = false;
   
   Vector operrors = new Vector();
   
   boolean verbose = false;
   public void setVerbose(boolean v) { verbose = v; }
   public boolean getVerbose() { return verbose;    }
   
   private int numWorkers = 3;
      
   DropboxAccess srv;
   
   int status = STATUS_STARTUP;
   protected  void setStatus(int v) { synchronized(threads) { status = v; }  }
   
  /**
   * Query the current status value
   */
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
      
   protected int mybuffersize = 0;
      
      
  /**
   * Instantiate the base with the DropboxAccess proxy and any package/file id
   *  information available.
   */
   public Operation(DropboxAccess srv, long packid, long fileid) {
      this.packid  = packid;
      this.fileid  = fileid;
      this.srv     = srv;
      
      setNumberOfWorkers(numWorkers);
   }

  /**
   * Returns a vector of errors encountered during execution of operation. If
   *  the operation has terminated and is not complete, the cause will be able
   *  in the returned vector. The vector contains the errors in the order that
   *  they occured (or were added).
   * @return Vector of Exceptions
   */
   public Vector getErrors() {
      return (Vector)operrors.clone();
   }
   
  /**
   * Returns an error message string built from the errors returned from the getErrors
   *  method. This is a simple string containing the getMessage() return value from
   *  the exception. If there is more than one exception, then each exception will be
   *  on a separate line and indented
   * @returns String containing errors messages associated with the execution of
   *           the operation. or empty string ("") if no errors.
   */
   public String getErrorMessages() {
      int i=0;
      String ret = "";
      Iterator it = getErrors().iterator();
      while(it.hasNext()) {
         Exception e = (Exception)it.next();
         String s = e.getMessage();
         String detail = null;
         
         if (s == null || s.length() == 0) {
            s = e.getClass().getName();
         } else {
            int idx = s.indexOf("<@-@>");
            if (idx >= 0) {
               detail = s.substring(idx+5);
               s = s.substring(0, idx);
            }
         }
            
         if (i > 0) ret += "\n";
         
         ret += s; i++;
         if (detail != null) {
            ret += Nester.nest("\ndetail:\n"+Nester.nest(detail));
            i++;
         }
      }
      if (i > 1) {
         ret = Nester.nest("\n" + ret, 1);
      }
      return ret;
   }
  
   
   protected void addError(Exception e) {
      operrors.add(e);
   }
   
  /**
   * This method is invoked by underlying transfer helper classs to update the 
   * Operation data transferred statistics
   *
   * @param ev Event describing transfer occured
   */
   public void actionPerformed(ActionEvent ev) {
      Object obj = ev.getSource();
      if (obj != null && obj instanceof DataTransfer) {
         DataTransfer dt = (DataTransfer)obj;
         transferUpdate(ev.getID());
         if (!doOperation) dt.abortTransfer();
      }
   }
   
  /**
   * Allows a listener to be registered with the Operation object. The listener will 
   * receive asychronous calls operationUpdate method as they occur.
   * @see OperationEvent for a description of the events.
   * @param l  Listener being registered with the Operation 
   */
   public void addOperationListener(OperationListener l) {
	   if (l == null) return;
	   listeners = ServiceMulticaster.addOperationListener(listeners,l);
   }
   
  /**
   * Removes a listener which had been previously registered with the Operation
   * @param l  Listener to de-register from the Operation 
   */
   public void removeOperationListener(OperationListener l) {
	   if (l == null) return;
	   listeners = ServiceMulticaster.removeOperationListener(listeners,l);
   }
   
  /**
   * Causes the MD5 OperationEvent to be sent to all registered listeners
   */
   public void dataValidating() {
	if (listeners != null) {
	   OperationEvent e = new OperationEvent(OperationEvent.MD5,this);
	   listeners.operationUpdate(e);
	}
   }
  /**
   * Causes the DATA OperationEvent to be sent to all registered listeners
   */
   public void dataTransferred() {
	  if (listeners != null) {
		 OperationEvent e = new OperationEvent(OperationEvent.DATA,this);
		 listeners.operationUpdate(e);
	  }
   }
  /**
   * Causes the ENDED OperationEvent to be sent to all registered listeners
   */
   public void transferEnded() {
	  if (listeners != null) {
		 OperationEvent e = new OperationEvent(OperationEvent.ENDED,this);
		 listeners.operationUpdate(e);
	  }
   }
   
   protected void startTimedOperation() {
      starttime = System.currentTimeMillis();
   }
   protected void startTimedOperation(long l) {
      starttime = l;
   }
   protected void endTimedOperation() {
      endtime = System.currentTimeMillis();
   }
   protected void endTimedOperation(long l) {
      endtime = l;
   }
   
  /**
   * Set the largest buffer size to be used when transferring data. The transfer objects
   *  have the final say on the actual max size to be used (upper bound), this routine
   *  gives the application a say in how SMALL the upper bound can be. If specified as 0, 
   *  then the transfer objects will decide the max buffer size.  
   */
   public void setMaxBufferSize(int s) { 
      if (s < 0) s = 0;
      mybuffersize = s;
   }
   
  /**
   * Get the user desired max buffer size. If set to 0, then the transfer object will decide.
   */
   public int  getMaxBufferSize() { return mybuffersize; }
   
  /**
   * Get the number of workers currently configured to process the Operation.
   *
   * @return int Number of worker threads configured
   */
   public int  getNumberOfWorkers() {
      return numWorkers;
   }
   
  /**
   * Set the number of workers which will be used to process the Operation. 
   *  The actual number of workers spawned will be based on the size of the
   *  file being transfer and will be bounded between 0 and the number set.
   *  If the number set <i>is</i> 0, then the thread calling the process 
   *  method to kick things off will be used as the only worker thread. Any
   *  number greater than 0 will result in worker threads being spawned as
   *  described above, and process will return immediately.
   *
   * @param  v Number of worker threads to use for Operation
   */
   public void setNumberOfWorkers(int v) {
      if (v > MAX_WORKERS) {
         numWorkers = MAX_WORKERS;
      } else if (v >= 0) {
         numWorkers = v;
      }
   }
   
   
  /**
   * Set the total size of the transfer to get good statistics and drive when
   *  transfer is complete. 
   *
   * This is usually managed by the Operation subclass
   *
   * @param v Size of total transfer
   */
   public void setTotalSize(long v) { 
      totalsize = v;
   }
   
  /**
   * Set the size of the data which still needs to be transferred. This is used to 
   *  drive both statistics and the transfer itself.
   *
   * @param v Size of total transfer
   */
   protected void setToXfer(long v) { 
      totToXfer = v;
   }
   
   protected synchronized void setTotXfered(long v) {
      totXfered = v;
   }
      
  /**
   * Get the packageid associated with this transfer
   * @return long Packageid associated with this Operation
   */
   public long getPackageId()           { return packid;    }
      
  /**
   * Get the fileid associated with this transfer
   * @return long fileid associated with this Operation
   */
   public long getFileId()              { return fileid;    }
   
  /**
   * Get the total bytes transferred thus far during this Operation
   * @return long Total bytes transferred so far
   */
   public long getTotalXfered()         { return totXfered; }
      
  /**
   * Get the total bytes transferred thus far during this Operation
   * @return long Total bytes transferred so far (synonym for getTotalXfered)
   */
   public long getTotalConfirmed()      { return totXfered; }
      
  /**
   * Get the number of bytes to transfer for this Operation
   * @return long Number of bytes left to transfer
   */
   public long getToXfer()              { return totToXfer; }
      
  /**
   * Get the total number of bytes associated with the data (filesize)
   * @return long Total number of bytes associated with the data (filesize)
   */
   public long getTotalSize()           { return totalsize; }
      
  /**
   * Get the number of bytes remaining to transfer for the Operation
   * @return long Number of bytes remaining to transfer for the operation
   */
   public long getRemainingLength()     { return totToXfer - totXfered; }
      
  /**
   * Get the Milliseconds since 70 GMT that the Operation was started
   * @return long time operation was started (may be 0)
   */
   public long getStartTime()           { return starttime; }
      
  /**
   * Get the Milliseconds since 70 GMT that the Operation ended
   * @return long time operation ended (may be 0)
   */
   public long getEndTime()             { return endtime;   }
   
  /**
   * Get a number 0 - 100 indicating the percent complete of the Operation
   * @return int Percent completion for the Operation
   */
   public int  percentDone() {
      if (totalsize == 0L) return 100;
      
      return(int) ((getTotalSize() - getRemainingLength()) * 100 / totalsize);

   }
   
  /**
   * This is the 'start' method for the Operation. Invoking this method will 
   *  create the specified/allowed number worker threads, and initiate the 
   *  transfer process. Once called, the caller can go about its business and
   *  rely on asynchronous notification for any cleanup/exception processing, or
   *  can make a call to waitForCompletion and simply block until transfer is
   *  complete.
   */
   public boolean process() throws DboxException {
     //System.out.println("Doing Process");
         
      synchronized(threads) {
         
         if (threads.size() > 0 || getStatus() > STATUS_STARTUP) {
            throw new DboxException("Operation is already in Progress");
         }
            
         setStatus(STATUS_INPROGRESS);
         
         startTimedOperation();
         
         doOperation = true;
         Thread t;
            
         if (numWorkers > 0) {
           // Limit the number of workers started based on the amount of
           //  data to be transferred
            int lnumworkers = (int)(getRemainingLength() / MEANINGFUL_WORKER_SIZE); 
            
            if (lnumworkers > numWorkers)  lnumworkers = numWorkers;
            else if (lnumworkers <= 0)     lnumworkers = 1;
         
            for(int i = 0; i < lnumworkers; i++) {
               threads.add(t=new Thread(this)); t.start();
            }
         } else {
            threads.add(Thread.currentThread());
         }
      }
      
     //System.out.println("Process started");
     
     // If we are stealing the callers thread, run outside of the synchronized call
      if (numWorkers == 0) run();
     
     // we ALWAYS think restarting is possible
      return true;
   }
      
  /**
   * Control will be returned only after the operation is complete (ended)
   */
   public void waitForCompletion() {
      while(!checkForCompletion(-1L));
   }
   
  /**
   * Control will be returned only after the operation is complete (ended) or
   *  the specified number of milliseconds has elapsed.
   *
   * @param ms Milliseconds to wait
   * @return boolean True if option is complete, false otherwise
   */
   public boolean waitForCompletion(long ms) {
      return checkForCompletion(ms);
   }
   
  /**
   * Return whether or not the Operation is complete (ended)
   * @return boolean True if option is complete, false otherwise
   */
   public boolean checkForCompletion() {
      return checkForCompletion(0);
   }
   
  // Wait UP TO specified MS value, then return if complete
   protected boolean checkForCompletion(long ms) {
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
               if (verbose) {
                  System.out.println("Abort called from an Operation thread. no wait");
               }
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
      
  /**
   * Returns true if the Operation is completed and successful, otherwise returns false.
   * @return boolean true if operation is a success
   */
   public abstract boolean validate();
   
  /**
   * Worker threads run the provided logic to manage the Operation data transfer
   */
   public abstract void run();
      
  /**
   * Returns the instantaneous transfer rate, which is defined to be the number of
   *  bytes transfer since the last call to this method divided by the elapsed time
   *  since the last call to this method.  If the elapsed time is 0, it is forced to 
   *  be 1 to avoid Div by 0.
   *
   * @return long Instantaneous transfer rate
   */
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
   
  /**
   * As worker threads accomplish verified bytes transferred, they will invoke this
   *  method to keep the statistics up to date and drive the transfer algorithm forward.
   *  The dataTransferred method will also be invoked, so any OperationListeners will 
   *  receive a DATA OperationEvent
   * @param bytes Number of bytes transferred
   */
   public synchronized void transferUpdate(long bytes) {
      totXfered += bytes;
      dataTransferred();
   }
      
  /**
   * Returns the transfer rate for this operation, which is defined to be the number of
   *  bytes transferred so far, divided by the elapsed time since the Operation was 
   *  started. If the elapsed time is 0, it is forced to be 1 to avoid Div by 0.
   *
   * @return long transfer rate
   */
   public synchronized long getXferRate() { 
      long millis = getElapsedTime();
      if (millis <= 0) millis = 1;
      
      return (totXfered*1000)/millis;
   }
   
  /**
   * Returns the time (milliseconds) that have elapsed during the timed operation so 
   *  far. If the operation is complete, then it will be the total time for the 
   *  execution of the Operation.
   *
   * @return long elapsed time for Operation in milliseconds
   */
   public synchronized long getElapsedTime() {
      long etime = endtime;
      if (etime == 0) {
         etime = System.currentTimeMillis();
      }
      
      long millis = etime - starttime;
      if (millis < 0) millis = 0;
      
      return millis;
   }
   
  /**
   * Utility routine converts a byte array to a Hex string
   * @param arr Byte array to use when generating hex string
   * @return String Hexidecimal string generated from byte array paramater
   */
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
   
