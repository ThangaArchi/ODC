package oem.edge.ed.odc.ftp.common;

import  oem.edge.ed.odc.dsmp.common.*;

public abstract class Operation {
   public static final int STATUS_STARTUP    = 0;
   public static final int STATUS_BEGIN      = 1;
   public static final int STATUS_INPROGRESS = 2;
   public static final int STATUS_AWAITING_CONFIRMATION = 49;
   public static final int STATUS_TERMINATED = 50;
   public static final int STATUS_FINISHED   = 51;
   public static final int STATUS_ABORTED    = 99;
   
   protected byte handleToUse        = (byte)0;
   
   protected long ofs                = 0;
   protected int  status             = STATUS_BEGIN;
   protected long totalsize          = 0;
   protected long totXfered          = 0;
   protected long totToXfer          = 0;
   protected int  id                 = -1;
   protected DSMPBaseHandler handler = null;
   protected synchronized void setStatus(int v) { status = v; }
   
   protected long startTime     = 0;
   protected long endTime       = 0;
   protected long lastTotXfered = 0;
   protected long lastTime      = 0;
   
   public Operation(DSMPBaseHandler handler, int id, 
                    long totToXfer, long ofs) {
      this.handler   = handler;
      this.id        = id;
      this.totToXfer = totToXfer;
      this.ofs       = ofs;
      this.totalsize = totToXfer + ofs;
      startTimedOperation();
   }
   
   public int  getId()                  { return id;        }
   public int  getStatus()              { return status;    }
   public long getOffset()              { return ofs;       }
   public long getTotalXfered()         { return totXfered; }
   public long getToXfer()              { return totToXfer; }
   public long getTotalSize()           { return totalsize; }
   public DSMPBaseHandler  getHandler() { return handler;   }
   
  // Some bad boy sub's need this
   public void setToXfer(long v) { 
      totToXfer = v;
      totalsize = v + ofs;
   }
   
   
   public int  percentDone() {
      if (totalsize == 0L) return 100;
      
      return(int) ((getTotalSize() - 
                    getToXfer()    + 
                    getTotalXfered())   * 100 / totalsize);

   }
   
   public void setHandleToUse(byte h) { handleToUse = h;    }
   public byte getHandleToUse()       { return handleToUse; }
   
   public long  getStartTime() {
      return startTime;
   }
   public long  getEndTime() {
      return endTime;
   }

   public void startTimedOperation() {
      startTime = System.currentTimeMillis();
   }
   public void startTimedOperation(long l) {
      startTime = l;
   }
   public void endTimedOperation() {
      endTime = System.currentTimeMillis();
   }
   public void endTimedOperation(long l) {
      endTime = l;
   }
   
   public long getInstantaneousXferRate() {
      long curTime = System.currentTimeMillis();
      long diff    = curTime - lastTime;
      lastTime     = curTime;
      
      long totx    = getTotalXfered();
      long xdiff   = totx - lastTotXfered;
      lastTotXfered= totx;
      if (diff == 0) return 0;
      
      return ((xdiff * 1000)/ diff);
   }
   
   public long getXferRate() {
      long curTime = System.currentTimeMillis();
      long diff    = curTime - startTime;
      if (diff == 0) return 0;
      return ((getTotalXfered() * 1000)/ diff);
   }
   
   public long getTotalConfirmed()      { return totXfered; }
   
  // Called when operation is ending in error
   abstract public void  handleEndError(String reason);
   
   public void dataTransferred() {}
   
   public synchronized boolean endOperation(String reason) {
      boolean ret = false;
      if (status < STATUS_TERMINATED) {
         
         endTimedOperation();
      
         setStatus((reason != null) ? STATUS_ABORTED : STATUS_FINISHED);
         
        // If anyone is waiting for the status to change, wake them up
         notifyAll();
         
         ret = true;
         FTPDispatchBase dispatch = (FTPDispatchBase)handler.getDispatch();
         dispatch.operationComplete(this);
         
         handleEndError(reason);
      }
      return ret;
   }
}
