package oem.edge.ed.odc.tunnel.common;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.zip.*;

public class SocketInputBuffer extends ByteArrayOutputStream 
   implements Runnable, TunnelCommon {
   private InputStream in;
   private byte earid;
   private boolean icaconn = false;
   private boolean xconn = false;
   private byte instid;
   private boolean reserved = false;
   private Byte reservedSync;
   protected short nextRecord;
   private boolean stop;
   private IOException threadException = null;
   private int maxbuf;
   private long totalCount = 0;
   private byte [] buf;
   private final int hiMark  = 150*1024;
   protected Thread mythread = null;
   private final int lowMark = 60*1024;
   
   private int bpsrate              = SocketOutputBuffer.CONSUMPTION_MAX_VALUE;
   private int bytesLeftInInterval  = SocketOutputBuffer.CONSUMPTION_MAX_VALUE;
   private long nextIntervalStarts  = 0;
   private long totsleep            = 0;
   
   private Byte BPSSync = new Byte((byte)0);
   
   private Byte lowSync;
   protected SessionManager sessionMgr;
   private TunnelMessage message = null;
   private String myid = "";
   private boolean xonOff = true;
   public static final int COMPRESSED_LOW_BYTECOUNT = 4096;
   protected Deflater deflater = null;
   private long totread = 0;
   
   public SocketInputBuffer(SessionManager ss, InputStream in, 
                            byte inearId, byte inId) {
      super(175 * 1024);
      init(ss, in, inearId, inId);
   }
   
   public void init(SessionManager ss, InputStream in, 
                    byte inearId, byte inId) {
               
      sessionMgr = ss;
      instid = inId;
      earid = inearId;
      this.in = in; 
      stop = false;
      maxbuf = 0;
      nextRecord = 1;
      buf = new byte[16*1024];
      lowSync = new Byte((byte)1);
      reservedSync = new Byte((byte)1);
	  
      if (in != null) {
         Thread thread = new Thread(this);
         ss.addThread(thread);
         thread.setName("SocketInpBuf");
         thread.start();
      }
   }   
   
   public byte getEarId() {
      return earid;
   }   
   
   public TunnelMessage getLastMessage() {
      return message;
   }   
   
   public void setBPS(int bps) { 
      if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
         DebugPrint.printlnd(DebugPrint.INFO4, myid + 
                             ": BPSRate change from " + 
                             bpsrate + " to " + bps);
      }
      
      int tunnelrate = sessionMgr.getTunnelEffectiveSendRate();
      synchronized(BPSSync) {
         bpsrate = bps;  
         if (bpsrate <= SocketOutputBuffer.CONSUMPTION_MIN_VALUE) {
            bpsrate = SocketOutputBuffer.CONSUMPTION_MIN_VALUE;
         }
         
         int tbytesLeftInInterval = bpsrate < tunnelrate?bpsrate:tunnelrate;
         if (tbytesLeftInInterval < bytesLeftInInterval) {
            bytesLeftInInterval = tbytesLeftInInterval;
         }
      }
   } 
   public int  getBPS()        { return bpsrate; }      
                            
   public boolean isICA() {
      return icaconn;
   }   
   public boolean isX() {
      return xconn;
   }   
   public long getTotalCount() {
      return totread;
   }   
   public void isICA(boolean v) {
      icaconn = v;
   }   
   public void isX(boolean v) {
      xconn = v;
   }   
   public void isXon(boolean v) {
      xonOff = v;
   }   
   public boolean isXon() {
      return xonOff;
   }   
   public boolean isInReserve() {
      boolean ret = false;
      synchronized(reservedSync) {
         ret = reserved;
      }
      return ret;
   }   
   public void isInReserve(boolean s) {
      synchronized(reservedSync) {
         reserved = s;
      }
   }   
   
  // If reading data from socket/FD ...we need a thread
   public void run() {
      try {
         mythread = Thread.currentThread();
         writeData(null, 0, 0);
      } catch(IOException ioe) {
         ;
      }
   }   
   
   
  // This routine does double duty. If we are threaded, the thread runs in
  // here ... otherwise, this routine is called to inject data.
   public void writeData(byte inbuf[], int bofs, int blen) throws IOException {
      if (stop) { throw new IOException("InputStream is Stopped!"); }
      
      try {
         myid = " Eid=" + earid + " Inst=" + instid;
         
         while (!stop && (in != null || blen > 0)) {
         
           // JMC If we have enough, wait for lowMark signal
            if (count > hiMark) {
               synchronized(lowSync) { 
                  if (count > hiMark) {
                  
                     if (sessionMgr.dotiming) {
                        sessionMgr.timingRecord("SIB:run:himark1", 
                                                earid, instid, 
                                                count,
                                                "nextrec=" + nextRecord);
                     }
                     if (DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.INFO2, 
                                           "InpBuf: Block on HiMark " + myid);
                     }
                     
                     try { 
                        lowSync.wait(); 
                     } catch (Exception e) {
                     }
                     if (sessionMgr.dotiming) {
                        sessionMgr.timingRecord("SIB:run:himark2", 
                                                earid, instid, 
                                                count,
                                                "nextrec=" + nextRecord);
                     }
                  }
               }
               continue;
            }
         
	   // read until data is available
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "InpBuf: calling Read " + myid);
            }
            
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SIB:run:read1", 
                                       earid, instid, 
                                       count,
                                       "nextrec=" + nextRecord);
            }
            
            
            int readlen=0;
            synchronized(BPSSync) {
               
              // Don't allow it to be more than the effective tunnelrate allows
               int tunnelrate = sessionMgr.getTunnelEffectiveSendRate();
               int tbytesLeftInInterval = bpsrate < tunnelrate
                                          ?bpsrate:tunnelrate;
               if (tbytesLeftInInterval < bytesLeftInInterval) {
                  bytesLeftInInterval = tbytesLeftInInterval;
               }
               
               if (!sessionMgr.getDoConsumption() ||
                  buf.length < bytesLeftInInterval) { 
                  readlen=buf.length; 
               } else {
                  if (bytesLeftInInterval <= 0) {
                     long curtime = System.currentTimeMillis();
                     if (curtime >= nextIntervalStarts) {
                        nextIntervalStarts = curtime + 1000;
                     } else {
                        long sleepmillis = nextIntervalStarts-curtime;
                        if (sleepmillis <= 1000) {
                           totsleep += sleepmillis;
                           try {
                              BPSSync.wait(sleepmillis);
                           } catch (Throwable ee) {
                           }
                        } else {
                           DebugPrint.printlnd(DebugPrint.ERROR, 
                                               "Zoinks! SIB: sleepmillis = " + 
                                               sleepmillis);
                        }
                        curtime = System.currentTimeMillis();
                        nextIntervalStarts = curtime + 1000;
                     }
                     
                    // Don't allow it to be more than the effective tunnelrate
                     tunnelrate = sessionMgr.getTunnelEffectiveSendRate();
                     
                     bytesLeftInInterval = 
                        (bpsrate < tunnelrate) ? bpsrate : tunnelrate;
                     
                     readlen = (buf.length < bytesLeftInInterval) ?
                        buf.length                         :
                        bytesLeftInInterval;
                  } else {
                     readlen = bytesLeftInInterval;
                  }
               }
            }
               
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SIB:run:read2 - after BPSSync", 
                                       earid, instid, 
                                       count,
                                       "");
            }
            
            int len = 0;
            if (in != null) {
              // We are threaded (have input stream), read from Input Stream
               len = in.read(buf, 0, readlen);
            } else {
              // Not threaded/no input stream, data is being injected via parms
               len = readlen>blen?blen:readlen;
            }
            
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SIB:run:read3", 
                                       earid, instid, 
                                       count,
                                       "len=" + len);
            }
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "InpBuf: Back from read with " + len 
                                  + " " + myid);
            }

            if (len > 0 && !stop) {
               sessionMgr.incrLocalIn(len);
               totread += len;
			
               synchronized (this)	{
			   
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "InpBuf: Awake again " + myid);
                  }
                  
                  if (sessionMgr.dotiming) {
                     sessionMgr.timingRecord("SIB:run:write1", 
                                             earid, instid, 
                                             count,
                                             "len=" + len);
                  }
                  
                 // write data to buffer
                  if (in != null) {
                    // If we are threaded, write the data we read from in
                     super.write(buf, 0, len);
                  } else {
                    // Not threaded, write the data from parms
                     super.write(inbuf, bofs, len);
                     bofs += len;
                     blen -= len;
                  }
                  
                  super.flush();
                  notifyAll();
                  
                  bytesLeftInInterval -= len;
                  
                  if (sessionMgr.dotiming) {
                     sessionMgr.timingRecord("SIB:run:write2", 
                                             earid, instid, 
                                             count,
                                             "len=" + len);
                  }
                 // Tell sessionMgr I have data to send
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "InpBuf: Calling session.addInpBuf" + 
                                        myid);
                  }
               }
               
               if (count > 0) {
                  sessionMgr.addInputBuffer(this);
               }
               
            } else if (len < 0){
               break;
            }
         }
         
         if (in != null || stop) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, 
                          "SockInpBuf: cleaningupConnection. -1 received: " 
                                  + toString());
            }
            
            if(count==0) { /* subu 05/20/02 */ 
               sessionMgr.cleanupConnection(earid, instid);
            }
         } else {
            return;
         }
      } catch (IOException ioe) {
      
        // Only complain/show this when we are NOT in the process of shutting
        // down
         if (!sessionMgr.startingShutdown()) {
            DebugPrint.printlnd(DebugPrint.INFO, 
                             "Local Socket exception. Closing connection "
                                + myid);
            DebugPrint.println(DebugPrint.DEBUG, ioe);
         }
         if(count==0) { /* subu 05/20/02 */ 
            sessionMgr.cleanupConnection(earid, instid);
         }
      } catch (Throwable e) {
         DebugPrint.printlnd(DebugPrint.INFO,
                            ": Local Socket: Unhandled exception! " + myid);
         DebugPrint.println(DebugPrint.INFO, e);
               
         if(count==0) { /* subu 05/20/02 */ 
            sessionMgr.cleanupConnection(earid, instid);
         }
      }
      
      stop=true;/* subu 05/21/02 */
      
      if (in == null) throw new IOException("Error exchanging data. Closed?");
   }   
   
   public void shutdown() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SIB: SHUTDOWN enter: " + toString());
      }
      
      stopReading();
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
            "SocketInpBuf: SHUTDOWN back from stopReading: " + toString());
      }
	  
      synchronized(this) {
         notifyAll();
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
            "SocketInpBuf: SHUTDOWN back from this.notifyAll(): " + 
            toString());
      }
	  
      synchronized(lowSync) {
         lowSync.notifyAll();
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
            "SocketInpBuf: SHUTDOWN back from lowSync.notifyAll(): " + 
            toString());
      }
	  
//	  if (isICA()) {
//		  sessionMgr.shutdown();
//	  }
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SocketInpBuf: SHUTDOWN exit:" + toString()); 
      }
   }   
   
   public void stopReading() {
      stop = true;
     /* AGGGH! This was causing Deadlock with in.read above. Send interrupt to
        thread instead
      try {
         in.close();
      } catch (IOException e) {}
     */
      if (mythread != null) {
         mythread.interrupt();
      } else {
        // If not threaded, consider closing down the connection as the writer
        // is probably the one calling this method anyway
         if(count==0) {
            sessionMgr.cleanupConnection(earid, instid);
         }
      }
   }   
   public String toString() {
      return "SockInpBuf: Eid/Instid=" + earid + "/" + instid +
              " totcnt=" + totalCount + " Count=" + count + 
              " bpsrate=" + bpsrate + " totsleep=" + totsleep + 
              " nextrec=" + nextRecord + "\n";
   }   
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 10:07:13 AM)
 * @return int
 */
   public int waitUntilLen() {
      synchronized (this) {
         while (count <= 0) {
            try {
            
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "InputBuffer:waitUntilLen: wait " + myid);
               }
               if (sessionMgr.dotiming) {
                  sessionMgr.timingRecord("SIB:waitUnLen1", 
                                          earid, instid, 
                                          count, "");
               }
               
               wait();
            } catch(InterruptedException e) {
            
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "InputBuffer:waitUntilLen: wakeup " + 
                                     myid);
               }
               
            }
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SIB:waitUnLen2", 
                                       earid, instid, 
                                       count, "");
            }
         }
      }
      return count;
   }   
  // JMC
  //
  // This method is called from a thread dedicated to reading data from here, 
  // and writing to out. Modifications made to copy data to another array to 
  // limit synch time.
  //	
   public TunnelMessage generateTunnelMessage() {
      byte [] arr = null;
      short rec = 0;
      int lcount = 0;
      int unCompCnt;
      TunnelMessage lmessage = null;
      
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SIB:genTunMsg", 
                                 earid, instid, 
                                 count, "");
      }
      synchronized (this) {
         
         unCompCnt = count;
         
        // keep track of high water mark
         if (count > maxbuf)	{
            maxbuf = count;
         }
         
        // JMC Get local byte array ... Perhaps take small bytes if too large
        //  then, use lowMark and selectively notify lowSync
         byte flags = 0;
         if (count > 0) {
            arr = toByteArray();
            lcount = count;
            
           // JMC 3/18/01 - If doing compression
            if ((sessionMgr.getCompression() ||
                 (xconn && sessionMgr.getXCompression())) && 
                count > COMPRESSED_LOW_BYTECOUNT) {
            
               if (deflater == null) {
                  deflater = new Deflater();
               }

               deflater.reset();
               deflater.setInput(arr, 0, count);
               deflater.finish();
               byte comparr[] = new byte[count < 1024?1024:count*2];
               lcount = deflater.deflate(comparr);
               if ((lcount+(lcount>>3)) < count) {
                  flags = 1;
                  arr = comparr;
                  if (DebugPrint.getLevel() > DebugPrint.INFO4) {
                     DebugPrint.println("Compress: " + count + " -> " + lcount +
                                        " or %" + ((count*100)/lcount));
                  }
               } else {
                  if (DebugPrint.getLevel() > DebugPrint.INFO4) {
                     DebugPrint.println("NO Compress: " + count + " -> " + lcount +
                                        " or %" + ((count*100)/lcount));
                  }
                  lcount = count;
               }
            }
         }
         
         rec = nextRecord;
         
         if (sessionMgr.getProtocolVersion() > 1) {
            lmessage = new TunnelMessage(earid, instid, rec, 
                                         arr, lcount, flags);
         } else {
            lmessage = new TunnelMessageV1(earid, instid, rec, 
                                           arr, lcount, flags);
         }
         
         lmessage.setAlternateLength(unCompCnt);
         
         nextRecord = sessionMgr.calculateNextRecord(nextRecord);
         
         if (count >= hiMark) {
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.INFO2, 
                                  "SIB: Sending lowsync, hiMark reset : " + 
                                  count);
            }
            
            if (sessionMgr.dotiming) {
               sessionMgr.timingRecord("SIB:genTunMsg:himarkreset", 
                                       earid, instid, 
                                       count, "");
            }
         }
         
         reset();
        // isInReserve(false);
         synchronized(lowSync) { lowSync.notifyAll(); }
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
               "SockInputBuffer:generateTunnelMessage: Copy " + 
               lcount + " " + myid);
         }
         
         totalCount += lcount;
         
         if (stop) { /* subu 05/21/02 */
            sessionMgr.cleanupConnection(earid, instid); 
         }
      
      }
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SIB:genTunMsg:done", 
                                 earid, instid, 
                                 count, "");
      }
      
      return message=lmessage;
   }   
   
   public TunnelMessage generateEmptyTunnelMessage() {
      byte [] arr = null;
      short rec = 0;
      TunnelMessage lmessage = null;
      
      synchronized (this) {
         
         rec = nextRecord;
         
         if (sessionMgr.getProtocolVersion() > 1) {
            lmessage = new TunnelMessage(earid, instid, rec, 
                                         null, 0, (byte)0);
         } else {
            lmessage = new TunnelMessageV1(earid, instid, rec, 
                                           null, 0, (byte)0);
         }
         
         lmessage.setAlternateLength(0);
         
         nextRecord = sessionMgr.calculateNextRecord(nextRecord);
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
               "SockInputBuffer:generateEmptyTunnelMessage: " + myid);
         }
         
         if (stop) { /* subu 05/21/02 */
            sessionMgr.cleanupConnection(earid, instid); 
         }
      
      }
      return message=lmessage;
   }   
   
   public int writeToStream(OutputStream out, String name) throws IOException {
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SIB:writeToStrm:" + name, 
                                 earid, instid, 
                                 count, "");
      }
      TunnelMessage tm = generateTunnelMessage();
      tm.write(out);
      if (sessionMgr.dotiming) {
         sessionMgr.timingRecord("SIB:writeToStrm:" + name + ":done", 
                                 earid, instid, 
                                 tm.getLength(), "");
      }
      return tm.getLength();
   }
}
