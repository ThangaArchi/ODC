package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class SocketOutputBuffer extends ByteArrayOutputStream 
   implements Runnable, TunnelCommon {
   
  /* We may want these to be dynamically changing values based on
     consumption rate .
  */
   static private final int XOFF_MARK                      = 1024*250;
   static private final int XON_MARK                       = 1024*100;
   static private final int PER_WRITE_MAX                  = 1024*50;
   static private final int CONSUMPTION_BYTE_MAX           = 1024*3000;
   static public  final int CONSUMPTION_MAX_VALUE          = 20*1024*1024;
   static public  final int CONSUMPTION_MIN_VALUE          = 10000;
   static private final int CONSUMPTION_RECALC_TIME        = 2000;
   static private final int CONSUMPTION_CONSIDER_BYTECOUNT = 2048;
   static private final int CONSUMPTION_ARRIVAL_RATE_MAXDELTA = 3000;  
   
   private int lastConsumptionRate                  = CONSUMPTION_MAX_VALUE;
   private int lastReportedConsumptionRate          = CONSUMPTION_MAX_VALUE;
   private long startConsumptionTime                = 0;
   private int consumptionByteCnt                   = 0;
   private int consumptionTotTime                   = 0;
   
   private int  arrivalBytes                        = 0;
   private int  arrivalRate                         = 0;
   private long arrivalStartTime                    = 0;
   
   private OutputStream out = null;
   private boolean stop = false;
   private String xcookie = null;
   private Hashtable messages;
   private int maxbuf;
   private IOException threadException = null;
   private SessionManager sessionMgr = null;
   private long totalCount = 0;
   private short nextRecord;
   private Byte recordSync;
   private byte eidV = 0;
   private byte iidV = 0;
   private boolean xonOff = true;
   private Inflater inflater = null;
   
   /* subu 05/20/02 */
   protected TunnelSocket flushRequested = null;   
   
   public SocketOutputBuffer(SessionManager cs, OutputStream out, String xcook) {
      super(100 * 1024);
      this.out = out;
      sessionMgr = cs;
      maxbuf = 0;
      nextRecord = 1;
      recordSync = new Byte((byte)0);
      messages = new Hashtable();
      xcookie = xcook;
	  
      if (out != null) {
         Thread thread = new Thread(this);
         thread.setName("SocketOutBuf");
         thread.start();
         cs.addThread(thread);
      }
   }   
   
   public long getTotalCount() { return totalCount; }
   
   protected void decompress(TunnelMessage tm) {
      if (tm.isCompressed()) {
         if (inflater == null) {
            inflater = new Inflater();
         }
         try {
            tm.decompress(inflater);
         } catch(DataFormatException de) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "SockOutBuf: Asked to decompress, error: ");
            DebugPrint.println(DebugPrint.ERROR, de);
         }
      }
   }
   
  // Send based on data arrival rate
   protected synchronized void decreaseConsumption() {
      if (lastConsumptionRate > CONSUMPTION_MIN_VALUE) {
         lastConsumptionRate -= lastConsumptionRate/4;
         if (lastConsumptionRate < CONSUMPTION_MIN_VALUE) {
            lastConsumptionRate = CONSUMPTION_MIN_VALUE;
         }
      }
      
     // Report this rate change IFF last reported rate < 2*actualDataRate
      if (lastReportedConsumptionRate < (arrivalRate << 1)) {
         lastReportedConsumptionRate = lastConsumptionRate;
         sessionMgr.sendConsumptionRate(eidV, iidV, lastConsumptionRate);
      }
   }
   
   protected synchronized void increaseConsumption() {
      if (lastConsumptionRate < CONSUMPTION_MAX_VALUE) {
         lastConsumptionRate += lastConsumptionRate/4;
         if (lastConsumptionRate > CONSUMPTION_MAX_VALUE) {
            lastConsumptionRate = CONSUMPTION_MAX_VALUE;
         }
      }
      
     // Report this rate change IFF last reported rate < 2*actualDataRate
      if (lastReportedConsumptionRate < (arrivalRate << 1)) {
         lastReportedConsumptionRate = lastConsumptionRate;
         sessionMgr.sendConsumptionRate(eidV, iidV, lastConsumptionRate);
      }
   }
   
   public void addTunnelMessage(TunnelMessage tm) {
      boolean ret = false;
      
      if (eidV == 0) {
         eidV=tm.getEarId();
         iidV=tm.getInstId();
      }
      
      if (!tm.checkValidRecordOrNext(nextRecord)) {
         
         if (sessionMgr.dotiming) {
            sessionMgr.timingRecord("SOB:atm:ignore", eidV, iidV, count, "");
         }
         
         DebugPrint.printlnd("SocketOutBuf: Ignore record <OLD>: " + 
                             tm.toString() + " \n" + toString());
      } else {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "SockOutBuffer: addTunnelMessage: " + 
                               tm.toString());
         }
                  
         long curtime = System.currentTimeMillis();
         
         synchronized (this) {
            
           // Used to be outside of synchronize ... move it here since we have
           //  only 1 inflater (today) and have multiple senders. Can interfere
           //  Consider making this an inflater per sender (somehow). Don't
           //  want to create a new inflater each time. But perhaps that is
           //  what we should do
           //
           //  TODO!!
            if (tm.isCompressed()) {
               decompress(tm);
            }
               
            synchronized (recordSync) {
            
            
               
              // Now that we are recordSync'd, check that this record is 
              //  still valid. Its possible (cause we have multiple senders,
              //  and we may change to have upbound streaming) to have the 
              //  same record sent by two different senders (perhaps some 
              //  kind of resend). OR, that this record was already in the 
              //  list, and this is a resend. 
              //
              // Either way, if the consumer moves the record count fwd in
              //  between the first checkValid and this one, we will now
              //  compensate for that. 
               if (!tm.checkValidRecordOrNext(nextRecord)) {
                  if (sessionMgr.dotiming) {
                     sessionMgr.timingRecord("SOB:atm:ignore", eidV, iidV, 
                                             count, "");
                  }
                  
                  DebugPrint.printlnd("SocketOutBuf: Ignore record <OLD>: " + 
                                      tm.toString() + " \n" + toString());
                  return;
               }
               
              // Keep interval total of arriving bytes
               arrivalBytes += tm.getLength();
               long deltatime = curtime - arrivalStartTime;
               if (deltatime < 0 || arrivalStartTime == 0) {
                  arrivalStartTime = curtime;
               } else if (deltatime >= CONSUMPTION_ARRIVAL_RATE_MAXDELTA) {
               
                  arrivalRate = 
                     (int)((((long)arrivalBytes)*1000)/(deltatime+1));
                     
                  if (DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG2, "ArrivalRate (" + 
                                        eidV + ", " + iidV + ") = " + 
                                        arrivalRate);
                  }
                  
                 // Report the consumption rate if arrivalRate closing in
                  if (lastReportedConsumptionRate < (arrivalRate << 1)) {
                     lastReportedConsumptionRate = lastConsumptionRate;
                     sessionMgr.sendConsumptionRate(eidV, iidV, 
                                                    lastConsumptionRate);
                  }
                  
                  arrivalStartTime = curtime;
                  arrivalBytes     = 0;
               }
               
              // Add message to out message hash
               messages.put(new Short(tm.getRecordNumber()), tm);
               
               if (sessionMgr.dotiming) {
                  short rec = tm.getRecordNumber();
                  sessionMgr.timingRecord("SOB:atm:add", eidV, iidV, count, 
                                          "sz=" + messages.size() + 
                                          " rec=" + rec + 
                                          (rec == nextRecord
                                           ? "" : " Not Next: " + nextRecord));
               }
               
               if (xonOff && tm.getLength() + count >= XOFF_MARK) {
                  
                  if (sessionMgr.dotiming) {
                     sessionMgr.timingRecord("SOB:atm:xoff", eidV, iidV, count, 
                                             "rec=" + tm.getRecordNumber());
                  }
                  
                  if (DebugPrint.doDebug()) {
                     DebugPrint.printlnd(DebugPrint.DEBUG, 
                                         "XOFF for socket: " + 
                                         this.toString());
                  }
                  
                  xonOff = false;
                  sessionMgr.sendXoff(eidV=tm.getEarId(), iidV=tm.getInstId());
                  decreaseConsumption();
               }
            }
         }
      }
   }   
   public int getHighWaterMark() {
      return maxbuf;
   }   
   public TunnelMessage getWaitingTunnelMessage() {
   
      TunnelMessage ret = null;
      synchronized (recordSync) {
         ret = (TunnelMessage)messages.remove(new Short(nextRecord));
      }
         
      if (ret != null) {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "OB.getWaitingTunnelMessage: Ret=> " + 
                               ret.toString());
         }
      }
      return ret;
   }
   public String getXCookie() {
      return xcookie;
   }   
   
   public int handleXCookie() throws IOException {
      int newwaitlen = 12;
      if (xcookie == null)
         return -1;
      if (totalCount == 0 && count >= 12) {
         int l1, l2;
         if (buf[0] == 0x42) {
            l1 = ((((int) buf[6]) & 0xff) << 8) | buf[7];
            l2 = ((((int) buf[8]) & 0xff) << 8) | buf[9];
         } else
            if (buf[0] == 0x6c) {
               l1 = ((((int) buf[7]) & 0xff) << 8) | buf[6];
               l2 = ((((int) buf[9]) & 0xff) << 8) | buf[8];
            } else {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                  "SockOutBuf: Gak! Doing XWindow cookie replace, and not valid 1st byte!");
               return -1;
            }
         if (l1 > 64 || l2 > 256) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "SockOutBuf: Gak! Doing XWindow cookie replace: l1=" + l1 + "l2=" + l2);
            newwaitlen = 0;
         } else {
            newwaitlen = 12 + l1 + (((~l1) + 1) & 3) + l2 + (((~l2) + 1) & 3);
            if (newwaitlen <= count) {
               int i;
               if (newwaitlen != count) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "SockOutBuf: Gak! newwaitlen=" + 
                                     newwaitlen + " but count=" + count);
               }
               byte ans = 0;
               byte mitname[] = "MIT-MAGIC-COOKIE-1".getBytes();
               byte tarr[] = toByteArray();
               reset();
               super.write(tarr, 0, 6);
               byte l2arr[] = new byte[xcookie.length() / 2 + 4];
               for (i = 0; i < xcookie.length(); i++) {
                  char ch = xcookie.charAt(i);
                  int v = Character.digit(ch, 16);
                  ans <<= 4;
                  ans |= v;
                  if (v >= 0 && v <= 15) {
                     if ((i & 1) != 0) {
                        l2arr[i / 2] = (byte) ans;
                     }
                  } else {
                     break;
                  }
               }
               l1 = mitname.length;
               l2 = i / 2;
               int l1pad = (l1 & 3) != 0 ? 4 - (l1 & 3) : 0;
               int l2pad = (l2 & 3) != 0 ? 4 - (l2 & 3) : 0;
               if (buf[0] == 0x42) {
                  tarr[0] = (byte) ((l1 >> 8) & 0xff);
                  tarr[1] = (byte) ((l1) & 0xff);
                  tarr[2] = (byte) ((l2 >> 8) & 0xff);
                  tarr[3] = (byte) ((l2) & 0xff);
               } else {
                  tarr[1] = (byte) ((l1 >> 8) & 0xff);
                  tarr[0] = (byte) ((l1) & 0xff);
                  tarr[3] = (byte) ((l2 >> 8) & 0xff);
                  tarr[2] = (byte) ((l2) & 0xff);
               }
               super.write(tarr, 0, 6);
               super.write(mitname);
               super.write(mitname, 0, l1pad);
               super.write(l2arr, 0, l2);
               super.write(mitname, 0, l2pad);
               newwaitlen = -1;
            }
         }
      }
      return newwaitlen;
   }
/**
 * Those interested in being awoken when some data is available should call this method
 * Creation date: (8/9/00 9:48:10 AM)
 * @return int
 */
   public void recordDone(short rec, int len) {
   
      synchronized (recordSync) {
         if (sessionMgr.dotiming) {
            sessionMgr.timingRecord("SOB:recdone", eidV, iidV, count, 
                                    "sz=" + messages.size() + 
                                    " rec=" + nextRecord);
         }
         
        // Make sure this record is cleared out (just in case someone added
        // it while it was being processed by another thread.
         messages.remove(new Short(nextRecord));
         
         nextRecord = sessionMgr.calculateNextRecord(nextRecord);
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "SockOutBuffer: recordDone for " + rec 
                               + " next is " + nextRecord + 
                               " totlen = " +len);		 
         }
         
         try {
            recordSync.notifyAll();  // Let them fight it out
         } catch (Exception e) {
         }
      }
   }
   
   
  // The job of this thread is to take data bound for the socket from the Buffer (this) and write it.
  // There is only 1 reader (us) and potentially many writers.
  // Bing ... 9/15/03 ... Run is only 1 mode. also support someone reading
  //  data out directly.
   public void run() {
      try {
         readData(null, 0, 0);
      } catch(IOException io) {}
   }
   
   boolean handledStartup = false;
   int waitlen = 0;
   byte [] arr = new byte[PER_WRITE_MAX];
   public int readData(byte outbuf[], int bofs, int blen) throws IOException {
   
      if (stop && out == null) throw new IOException("SOB: Input Closed");
   
      if (!handledStartup) {
         handledStartup = true;
         startConsumptionTime = System.currentTimeMillis();
         if (xcookie != null) {
            waitlen = 12;   // Wait for initial Connection Protocol to come in
         }
      }
      
      try	{
         while (!stop) {
            int lcount=0;
            synchronized (this) {
            
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                     "SockOutBuffer: run: calling waitUntilLen");
               }
               
               waitUntilLen(waitlen);
               if (count > 0) {
                  if (count > maxbuf) {
                     maxbuf = count;
                  }

                  if (waitlen > 0) {
                     waitlen = handleXCookie();
                  }
                  if (waitlen <= 0 && count != 0) {
                  
                     
                     if (out != null) {
                        lcount =(count > arr.length)?arr.length:count;
                        System.arraycopy(buf, 0, arr, 0, lcount);
                     } else {
                        lcount =(count > blen)?blen:count;
                        System.arraycopy(buf, 0, outbuf, bofs, lcount);
                     }
                     count -= lcount;
                     
                    // Shift leftover contents down
                     if (count > 0) {
                        System.arraycopy(buf, lcount, buf, 0, count);
                     }
                     
                     if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.DEBUG5, 
                                           "SockOutBuffer: Getting " + 
                                           lcount + " Bytes from " + count);
                     }
                     
                     if (!xonOff && count < XON_MARK) {
               
                        if (DebugPrint.doDebug()) {
                           DebugPrint.printlnd(DebugPrint.DEBUG, 
                                               "XON  for socket: " + 
                                               this.toString());
                        }
               
                        xonOff = true;
                        sessionMgr.sendXon(eidV, iidV);
                     }
                  }
               }
            }
			
            if (lcount > 0) {
            
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "SockOutBuffer: Writing " + 
                                     lcount + " Bytes"); 
               }
               
               long t1 = System.currentTimeMillis();
               
              // Only write it if we have an OutputStream
              // Otherwise, we have already copied it to the proper location 
               if (out != null) {
                  out.write(arr, 0, lcount);
                  out.flush();
               }
               long t2 = System.currentTimeMillis();
               
               if (sessionMgr.getDoConsumption()) {
                  
                  synchronized(this) {
                     long deltatime = (t2-startConsumptionTime);
                     if (lcount >= CONSUMPTION_CONSIDER_BYTECOUNT) {
                        consumptionTotTime += t2 - t1;
                        consumptionByteCnt += lcount;
                     }
                     
                     if (consumptionByteCnt >= CONSUMPTION_BYTE_MAX || 
                         consumptionTotTime >= CONSUMPTION_RECALC_TIME) {
                        try {
                           
                           if (consumptionByteCnt > 0 && 
                               consumptionTotTime > 0) {
                              long cbc = ((long)consumptionByteCnt)*1000;
                              int instrate = (int)(cbc/consumptionTotTime);
                              int totrate  = (int)(cbc/deltatime);
                              
                              if (instrate < lastConsumptionRate) {
                                 decreaseConsumption();
                                 DebugPrint.println(DebugPrint.INFO5,
                                                    "Consumption: < [" + 
                                                    instrate + "] [" +
                                                    totrate + "] r=" +
                                                    lastConsumptionRate);
//                              } else if (!xonOff) {
                              } else {
                                 increaseConsumption();
                                 DebugPrint.println(DebugPrint.INFO5,
                                                    "Consumption: > [" + 
                                                    instrate + "] [" +
                                                    totrate + "] r=" + 
                                                    lastConsumptionRate);
                              }
                              
                              if (instrate < CONSUMPTION_MIN_VALUE) {
                                 instrate = CONSUMPTION_MIN_VALUE;
                              }
                           } else {
                              increaseConsumption();
                           }
                        } catch(Throwable tt) {
                           DebugPrint.printlnd(
                              DebugPrint.ERROR, 
                              "Error sending Consumption rate info cbc=" +
                              consumptionByteCnt + " ctt=" + consumptionTotTime);
                           DebugPrint.println(DebugPrint.ERROR, tt);
                        }
                        consumptionByteCnt = 0;
                        consumptionTotTime = 0;
                        startConsumptionTime = t2;
                     }
                  }
               }
               
               totalCount += lcount;
               sessionMgr.incrLocalOut(lcount);
               
            }
            
           /* subu 05/20/02 */
            if (count == 0 && flushRequested != null) {
               TunnelSocket ts = flushRequested;
               flushRequested = null;
               sessionMgr.cleanupConnection(ts.getEarId(), ts.getId());
               ts=null;
               return lcount;
            }
            
           // If not doing socket writes && we have data return the len
           //  otherwise, loop around and get some more data
            if (out == null && lcount > 0) return lcount;
            
         }
      } catch (IOException e) {
         threadException = e;
      }
      
      return -1;
   }   
   
  /* SUBU */
   public void flushAndShutdown(TunnelSocket ts) {
      flushRequested = ts;
   } 
   public void setXCookie(String cook) {
      xcookie = cook;
   }   
   public void shutdown() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SocketInpBuf: SHUTDOWN enter: " + toString());
      }
      
      stopWriting();
      synchronized(this) {
         notifyAll();
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SocketInpBuf: SHUTDOWN exit : " + toString());
      }
   }   
   public void stopWriting() {
      stop = true;
      try {
         if (out != null) out.close();
      } catch(IOException e) {}
	  
   }   
   public String toString() {
      Enumeration enum = messages.elements();
      long msgsBytes = 0;
      while(enum.hasMoreElements()) {
         TunnelMessage tm = (TunnelMessage)enum.nextElement();
         if (tm != null) {
            msgsBytes += tm.getLength();
         }
      }
      return "SockOutBuf: TotCnt=" + totalCount + " Cnt=" + count 
         + " maxbuf=" + maxbuf + " nextRec = " + nextRecord 
         + " XCook=" + xcookie + " nonCurrRecs=" + messages.size() 
         + " nonCurrBytes= "   + msgsBytes
         + "\n";
   }   
/**
 * Those interested in being awoken when some data is availabel should call this method
 * Creation date: (8/9/00 9:48:10 AM)
 * @return int
 */
   public synchronized int waitUntilLen() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: wautUntilLen: Enter");
      }
      
      while (count <= 0 && !stop) {
         try {	
            wait();
         } catch(InterruptedException e) {
         }
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: wautUntilLen: Exit");
      }
      
      return count;
   }   
/**
 * Those interested in being awoken when some data is availabel should call this method
 * Creation date: (8/9/00 9:48:10 AM)
 * @return int
 */
   public synchronized int waitUntilLen(int len) {
      boolean waited = false;
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: wautUntilLen: Enter " + len);
      }
      
      if (len <= 0) len = 1;
      while (count < len && !stop) {
         try {	
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SOB:waitULen1", eidV, iidV, count, 
                                       "sz=" + messages.size() + 
                                       " waitlen=" + len);
            }
            waited = true;
            wait();
            
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SOB:waitULen2", eidV, iidV, count, 
                                       "sz=" + messages.size() + 
                                       " waitlen=" + len);
            }
         } catch(InterruptedException e) {
         }
      }
      
      if (sessionMgr.dotiming && waited) {
         sessionMgr.timingRecord("SOB:waitULen3", eidV, iidV, count, 
                                 "sz=" + messages.size() + 
                                 " waitlen=" + len);
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: wautUntilLen: Exit " + count);
      }
      
      return count;
   }   
   
  // This method called to fill the OuputBuffer with data. 
  // Provides Synchronized access as well as
  // giving a good hook for notifying others that Data is available 
  // (threads that called waitUntilLen)
  //  We also ensure that the 'record' being written is the one expected. 
  //  If not, wait for our turn.
   public void write(int recNum, byte[] b, int off, int len) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: write rec " + recNum + 
                            " Bytes " + len + " TOP");
      }
      
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SOB:write", eidV, iidV, count,
                                 "sz=" + messages.size() + " rec=" + recNum + 
                                 " len=" + len);
      }

      
      int r;
	  
     // nextrecord protected by recordSync as well
      synchronized (recordSync) {
         while(recNum != nextRecord) {
            try {
            
               if (sessionMgr.dotiming) {
                  sessionMgr.timingRecord("SOB:write:wait1", eidV, iidV, count,
                                          "sz=" + messages.size() + 
                                          " rec=" + recNum + 
                                          " nextrec=" + nextRecord);
               }
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                     "SockOutBuf: Going to sleep on recordSyn monitor! Rec " + 
                                     recNum + " next = " + nextRecord);
                  
               }
               
               recordSync.wait();
               
               if (sessionMgr.dotiming) {
                  sessionMgr.timingRecord("SOB:write:waitdone", eidV, iidV, 
                                          count,
                                          "sz=" + messages.size() + 
                                          " rec=" + recNum + 
                                          " nextrec=" + nextRecord);
               }
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                     "SockOutBuf: WOKE UP FROM RECSYNC after wait! Rec " + 
                     recNum);
               }
               
            } catch (Exception e) {
            
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                     "SockOutBuf: WOKE UP FROM RECSYNC in Excpt! Rec " + 
                     recNum);
               }
            }
         }
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SockOutBuffer: write rec " + recNum + 
                            " Bytes " + len + " About to Write");
      }
      
      synchronized (this) {
         super.write(b, 0, len);
         notify();
      }
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SOB:write:done", eidV, iidV, count,
                                 "sz=" + messages.size() + " rec=" + recNum + 
                                 " len=" + len);
      }
   }   
}
