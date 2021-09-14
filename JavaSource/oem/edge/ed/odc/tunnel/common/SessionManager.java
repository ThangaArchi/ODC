package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.util.*;
import java.applet.*;
import com.ibm.as400.webaccess.common.*;
import java.awt.*;
import java.awt.event.*;


public class SessionManager implements TunnelCommon, Runnable {

   
   public class SocketSMInputBuffer extends SocketInputBuffer {
      int needamt = 0;
      int totsent = 0;
      
      public SocketSMInputBuffer(SessionManager ss, InputStream in, 
                               byte inearId, byte inId) {
         super(ss, in, inearId, inId);
      }
      
      public synchronized void thruputTest(int amt) {
         needamt = amt;
         totsent = 0;
         sessionMgr.addInputBuffer(this);
      }
      
     // Generates a Control tunnelmessage with Seq#, tagged as ignore
      public synchronized 
      TunnelMessage generateIgnoredTunnelMessage(int doamt)  {
         TunnelMessage tm = null;
      
         if (sessionMgr.getProtocolVersion() > 1) {
            tm = new TunnelMessage((byte)0, (byte)0, nextRecord, 
                                   new byte[doamt], 
                                   doamt, (byte)2);
         } else {
            tm = new TunnelMessageV1((byte)0, (byte)0, nextRecord, 
                                     new byte[doamt],
                                     doamt, (byte)2);
         }
         
         tm.setAlternateLength(doamt);
         nextRecord = sessionMgr.calculateNextRecord(nextRecord);
         return tm;
      }
      
      
     // This method generates thruput data if desired, when count == 0. The
     //  tunnelmessage is marked with flags bit 1 set, indicating that the
     //  data should just be chucked away
      public TunnelMessage generateTunnelMessage() {
         TunnelMessage tm = null;
         synchronized (this) {
            if (count == 0) {
               if (needamt > 0) {
                  int doamt = needamt  % 100000;
                  tm = generateIgnoredTunnelMessage(doamt);
               }
            }
            
            if (tm == null) {
               tm = super.generateTunnelMessage();
            }
            
            if (needamt > 0) {
               if (tm != null) {
                  int v = tm.getLength() + tm.getOverhead();
                  needamt -= v;
                  totsent += v;
                  if (needamt <= 0) {
                     ConfigObject co = new ConfigObject();
                     co.setProperty   ("COMMAND",   "THRUPUT");
                     co.setIntProperty("BYTECOUNT", totsent);
                     co.setProperty   ("ORIGIN",    sessionMgr.getName());
                     sessionMgr.writeControlCommand(co);
                  } else {
                     sessionMgr.addInputBuffer(this);
                  }
               } else {
                  sessionMgr.addInputBuffer(this);
               }
            }
         }
         
         return tm;
      }
   }
   
   class SenderTabs {
      long bps, time;
      SenderTabs(long bps, long time) {
         this.bps       = bps;
         this.time      = time;
      }
      
      long getBPS()  { return bps;  }
      long getTime() { return time; }
      void setBPS(long v)  { bps  = v;  }
      void setTime(long v) { time = v; }
      boolean isCurrent(long curtime) {
         return curtime < (time+SENDERTAB_TIMEOUT_DELTA);
      }
      long timeoutInMillis(long curtime) {
         return (time+SENDERTAB_TIMEOUT_DELTA) - curtime;
      }
   }
   
   
   public synchronized void thruputTest(int amt) {
      controlBuffer.thruputTest(amt);
   }

   protected boolean hosting = false;   // Used for application launch of ICA
   
      
   protected final int CONSUMPTION_BYTE_MAX           = 1024*3000;
   protected final int CONSUMPTION_MAX_VALUE          = 20*1024*1024;
   protected final int CONSUMPTION_MIN_VALUE          = 30000;
   protected final int CONSUMPTION_RECALC_TIME        = 2000;
   protected final int CONSUMPTION_CONSIDER_BYTECOUNT = 2048;
   protected final int CONSUMPTION_MAX_WALL_TIME      = 1000;
   
  // Each 5 seconds of sending data, check for potential adjust num senders
   static public final long MAX_SENDER_ADJUST_DELTA   = 5000;
   
   protected long  startConsumptionTime                = 0;
   protected long  startAutoSenderTime                 = 0;
   protected long  consumptionByteCnt                  = 0;
   protected long  consumptionTotTime                  = 0;
   protected long  consumptionWaitTime                 = 0;
   protected long  consumptionOHTime                   = 0;
   protected long  NSconsumptionByteCnt                = 0;
   protected long  NSconsumptionTotTime                = 0;
   protected long  NSconsumptionWaitTime               = 0;
   protected long  NSconsumptionOHTime                 = 0;
      
   protected int    tunnelSendRate          = CONSUMPTION_MAX_VALUE;
   protected int    tunnelEffectiveSendRate = CONSUMPTION_MAX_VALUE;
   protected boolean doCutdownEffectiveRate = false;
   
  // Every minute the senderTab info expires
   static final int SENDERTAB_TIMEOUT_DELTA = 1000*60*1;
   protected Hashtable senderPerformance = new Hashtable();
      
      
   protected Vector buffers = new Vector();
   protected String desktopid = null;
   protected Applet appletV = null;
   protected String hostv = null;
   protected SMStats totalCnt = new SMStats();
   protected SMStats sinceLastQuery = new SMStats();
   protected SMStats lastInterval = new SMStats();
   protected SMStats currentInterval = new SMStats();
   protected String  lastToken = null;
   protected boolean startshutdown = false;
   protected boolean dorun = true;
   protected boolean dostream = false;
   protected boolean dosecure = false;
   protected boolean docombo = false;
   protected boolean doConsumption = true;
   protected boolean doTSR = true;
   protected boolean compressX = true;
   protected boolean compressAll = false;
   protected int uniq = 1;
   protected static SessionManager lastSess = null;
   protected boolean dodebug = false;
   protected Hashtable tunnelEars = new Hashtable();
   protected Vector threads = null;
   protected SocketSMInputBuffer controlBuffer 
                   = new SocketSMInputBuffer(this, null, (byte)0, (byte)0);
	  
   protected int maxSenders = 3; 
   protected Vector senders = new Vector();
   protected SenderFactory senderFactory = null;
   protected boolean autoAdjustSenders = true;
   
   public void setAutoAdjustSenders(boolean v) { autoAdjustSenders = v; }
   public boolean getAutoAdjustSenders() { return autoAdjustSenders; }
   
   public void setSenderFactory(SenderFactory sf) { 
      senderFactory=sf; 
   }
   public    void setMaxSenders(int m)  { 
      if (m <= 10) {
         maxSenders = m;
      } else {
         maxSenders = 10;
         DebugPrint.printlnd("Attempt to set maxSenders > 10. Use 10");
      }
   }
   public    int  getMaxSenders()       { 
      return maxSenders;
   }
   public    int  getNumSenders()       { 
      synchronized(senders) {
         return senders.size(); 
      }
   }
   public    void registerSender(Sender s) {
      synchronized (senders) {
         if (!senders.contains(s)) {
            senders.addElement(s);
         }
      }
   }
   public    void removeSender(Sender s) {
      synchronized(senders) {
         senders.removeElement(s);
      }
   }
   
   public    void addNewSender() {
      if (senderFactory != null) {
         if (senders.size() < maxSenders) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               "SessionManager: addNewSender called");
            senderFactory.newSender(this);
         } else {
            DebugPrint.println(DebugPrint.WARN, "Asked to addNewSender ... already at max of " + maxSenders);
         }
      }
   }
   public String getSenderInfo() {
      StringBuffer sb = new StringBuffer("Current senders[");
      int sz = senders.size();
      sb.append(sz).append("]");
      
      long curtime = System.currentTimeMillis();
      for(int i=1; true; i++) {
         SenderTabs st = (SenderTabs)senderPerformance.get(new Integer(i));
         if (st == null) {
            break;
         }
         long to = st.timeoutInMillis(curtime);
         sb.append("\n   ");
         sb.append((i <= sz)?"*":" ");
         sb.append("[").append(i).append("] BPS[").append(st.getBPS());
         sb.append("] TO[").append(to).append("]");
      }
      return sb.toString();
   }
   public    void forceSenderSize(int v) {
      forceSenderSize(v, false);
   }
   public    void forceSenderSize(int v, boolean resetStats) {
      DebugPrint.println(DebugPrint.DEBUG, 
                         "SessionManager: forceSenderSize called [" + 
                         v + "]");
      if (v < 1) v = 1;
      if (senders.size() > 1 && v < senders.size()) {
         Vector del = new Vector();
         synchronized(senders) {
            while(senders.size() > v) {
               int idx = senders.size()-1;
               Sender s = (Sender)senders.elementAt(idx);
               del.addElement(s);
               senders.removeElementAt(idx);
               if (resetStats) senderPerformance.remove(new Integer(idx+1));
            }
         }
         
         Enumeration enum = del.elements();
         while(enum.hasMoreElements()) {
            Sender s = (Sender)enum.nextElement();
            s.bagout();
         }
      }
      
      if (resetStats && v == 1) {
         senderPerformance.remove(new Integer(1));
      }
   }
          
   protected int pingDelay = -1;
   
   protected String name="name";
   public    void   setName(String v) { name = v;    }
   public    String getName()         { return name; }
          
   private int protover = 2;
   protected int TMModuloMask       = 4096-1;
   protected short   lastControlRec = 0;
   protected Hashtable controlMessages = new Hashtable();
   
   protected transient ActionListener actionListener = null;
        
   public static final boolean dotiming = false;
   private long lastmillis = 0;
   public void timingRecord(String id, byte ear, byte inst, 
                            long len, String rest) {
      long millis = System.currentTimeMillis();
      long delta = 0;
      if (lastmillis != 0) delta = millis-lastmillis;
      System.out.println("TIMING: Delt:" + delta + " Tm:" + millis + 
                         " Id:" + id + " E:" 
                         + ear + " I:" + inst + " Len:" + len + " Rest:" + rest);
      lastmillis = millis;
   }
        
   
   public void    setPingDelay(int v)  { pingDelay = v;    }
   public int     getPingDelay()       { return pingDelay; }
   
   public void    setToken(String in)  { lastToken = in;   }
   public String  getToken()           { return lastToken; }
   
   public void    setHosting(boolean in) { hosting = in;   }
   public boolean getHosting()           { return hosting; }
   
  // XCompression only
  //
  // Use setXCompression to set whether or not X protocol is compressed
  //  before being sent TO other side
  //
  // Use setRemoteXCompression to set whether or not X protocol is compressed
  //  before being sent FROM other side
  //
   public void    setXCompression(boolean in) { 
      compressX = in;   
   }
   public void    setRemoteXCompression(boolean in) { 
      if (getProtocolVersion() > 1) {
         ConfigFile cf = new ConfigFile();
         if (in) {
            cf.setProperty("command", "compressx");
         } else {
            cf.setProperty("command", "nocompressx");
         }
         writeControlCommand(cf);
      }
   }
   public boolean getXCompression()           { 
      return compressX && protover > 1; 
   }
   
  // ALL tunnel traffic Compression
  //
  // Use setAllCompression to set whether or not ALL protocol is compressed
  //  before being sent TO other side
  //
  // Use setRemoteAllCompression to set whether or not ALL proto is compressed
  //  before being sent FROM other side
  //
   public void    setCompression(boolean in) { 
      compressAll = in;   
   }
   public void    setRemoteCompression(boolean in) { 
      if (getProtocolVersion() > 1) {
         ConfigFile cf = new ConfigFile();
         if (in) {
            cf.setProperty("command", "compressall");
         } else {
            cf.setProperty("command", "nocompressall");
         }
         writeControlCommand(cf);
      }   
   }
   public boolean getCompression()           { 
      return compressAll && protover > 1; 
   }
      
   public static final byte CONTROL_SEPARATOR = 0x00;
   
   public Object getControlMessageSyncObject() { return controlMessages; }
   public short  getLastControlRecordNumber()  { return lastControlRec; }
   public void   setLastControlRecordNumber(short in) { 
      synchronized(controlMessages) {
         lastControlRec = in;   
      }
   }
   
   public void saveControlMessage(TunnelMessage tm) { 
      controlMessages.put(new Short(tm.getRecordNumber()), tm);
   }
   public TunnelMessage getWaitingControlMessage() {
      synchronized(controlMessages) {
         return (TunnelMessage) controlMessages.remove(
            new Short(calculateNextRecord(lastControlRec)));
      }
   }
   
   public void addPingInfo(int id, long ms, long datain, long dataout) {
      totalCnt.addPingInfo(id, ms, datain, dataout);
      sinceLastQuery.addPingInfo(id, ms, datain, dataout);
      currentInterval.addPingInfo(id, ms, datain, dataout);
   }
   
   public short calculateNextRecord(short rec) {
      return (short)(TMModuloMask & (1+rec));
   }
   
   public int getProtocolVersion() { return protover; }
   public boolean setProtocolVersion(int ver) {
      boolean ret = true;
      protover = ver;
      if (ver == 1) {
         TMModuloMask = 256-1;
      } else if (ver != 2) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "SessionMgr: Unknown Protocol Version " + 
                             ver + "!!");
         ret = false;
      }
      return ret;
   }
   
   public SessionManager() {
      lastSess = this;
      lastInterval.freezeElap(new Date());
   }
   
   
   protected boolean doLogging        = false;
   protected boolean realtimeLogging  = false;
   private ByteArrayOutputStream logo = null;
   private PrintWriter           logw = null;
   public void    setDoLogging(boolean v) { doLogging = v;    }
   public boolean getDoLogging()          { return doLogging; }
   public void    setDoRealtimeLogging(boolean v) { 
      realtimeLogging = v;    
      logo = null;
      logw = null;
   }
   public boolean getDoRealtimeLogging()          { return realtimeLogging; }
   public void log(String s) {
      if (logo == null) {
         synchronized(this) {
            logo = new ByteArrayOutputStream(4096);
            if (realtimeLogging) {
               logw = new PrintWriter(System.out);
            } else {
               logw = new PrintWriter(logo);
            }
         }
      }
      
      synchronized(logo) {
         logw.println(s);
         logw.flush();
      }
      
      if (logo.size() > 4096) {
         printlog();
      }
   }
   
   public String logAsString() {
      String ret = null;
      if (logo != null) {
         synchronized(logo) {
            ret = logo.toString();
            logo.reset();
         }
      }
      return ret;
   }
   public void printlog() {
      String o = logAsString();
      if (o != null) {
         DebugPrint.println("Log info " + desktopid + "\n" + o);
      }
   }
   
   
   
  /**
   * Buffer is added to SessionManager to hand out to a consumer. The internal
   * sync object is notified to wake any current waiters.
   */
   public void addInputBuffer(SocketInputBuffer in) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "setInputBuffer: add InputBuffer!");
      }

      synchronized (buffers) {
         if (in.isXon() && !in.isInReserve()) {
         
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                  "setInputBuffer:  Adding inp to list, NOTIFY!");
            }
            
            in.isInReserve(true);
            buffers.addElement(in);
            buffers.notify();
         }
      }
   }   
   
   public boolean removeBufferFromReserve(SocketInputBuffer sib) {
      boolean ret = false;
      synchronized(buffers) {
         if (sib.isInReserve()) {
            sib.isInReserve(false);
            for (int i=0; i < buffers.size(); i++) {
               SocketInputBuffer sob = 
                  (SocketInputBuffer)buffers.elementAt(i);
               if (sob == sib) {
                  buffers.removeElementAt(i);
                  ret = true;
                  break;
               }
            }
         }
      }
      return ret;
   }
   
   public void setXonOff(boolean xonOff, byte eid, byte iid) {
      TunnelSocket ts = null;
      TunnelEarInfo te = getTunnelEarInfo(eid);
      
      if (te != null) {
         ts = te.getTunnelSocket(iid);
      }
      
      if (ts != null) {
         if (DebugPrint.doDebug() || true) {
            DebugPrint.println(DebugPrint.INFO5, 
                               "SM: setXonOff(" + xonOff + "): ts: " 
                               + ts.toString());
         }
         
         synchronized (buffers) {
            SocketInputBuffer ib = ts.getInputBuffer();
            ib.isXon(xonOff);
            if (xonOff) {
               if (!ib.isInReserve()) {
                  if (ib.size() > 0) {
                     addInputBuffer(ib);
                  }
               }
            } else {
               removeBufferFromReserve(ib);
            }
         }
         
      } else {
         DebugPrint.println(DebugPrint.INFO, 
                            "SM: setXonOff: (Ear/Inst) not found! (" + 
                            eid + "/" + iid + ")");
      }
   }
   
  /**
   * Add specified TunnelEarInfo to the Session to be managed
   */
   public void addThread(Thread t) {
      if (threads == null) threads = new Vector();
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.printlnd(DebugPrint.DEBUG, 
                             "SessionMgr: AddThread: " + 
                             t.toString() + " ... punt for now");
      }
     // threads.addElement(t);
   }   
   
   public boolean removeThread(Thread t) {
      if (threads == null) return false;
      
      if (t == null) t = Thread.currentThread();
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.printlnd(DebugPrint.DEBUG, 
                             "SessionMgr: RemoveThread: " + 
                             t.toString() + " ... punt for now");
      }
      
      return threads.removeElement(t);
   }   
   public Applet applet() {
      return appletV;
   }
   public void applet(Applet v) {
      appletV = v;
   }
           
   public void mainConnectionComplete() {
      ;
   }
   
  /**
   * Cleanup the specified connection, and tell other side about it
   */
   public void cleanupConnection(byte id1, byte id2) {
      TunnelSocket ret = null;
      TunnelEarInfo te = getTunnelEarInfo(id1);

      if (te != null) {
		  
         ret = te.getTunnelSocket(id2);
         if (ret != null) {
         
           /* subu 05/20/02 - Potentially do staged shutdown (flush data) */
            TunnelSocket ts = te.getTunnelSocket(id2);
            if (ts != null) {
               SocketOutputBuffer sob = ts.getOutputBuffer();
               if (sob != null) {
                  if (sob.size() > 0) {
                     sob.flushAndShutdown(ts);
                     return;
                  }
               }
            }	  

            ret = te.removeTunnelSocket(id2);
            ret.shutdown();
			 
            ConfigObject cf = new ConfigObject();
            cf.setProperty("command", "killinst");
            cf.setIntProperty("earid", (int)id1);
            cf.setIntProperty("instid", (int)id2);
            writeControlCommand(cf);
         }
      }
   }   
   
  /**
   * Cleanup the specified connection, and tell other side about it
   */
   public void cleanupConnection(byte earid) {
      TunnelEarInfo te = getTunnelEarInfo(earid);

      if (te != null) {
         DebugPrint.printlnd(DebugPrint.DEBUG,
                             "SM: Cleanup Ear connection called!");
                             
         try {
            if (te.getEarType().equals("X")) {
               fireActionEvent("XEarDestroyed");
            }
         } catch(NullPointerException ne) {
         }
         te.shutdownInstances();
         removeTunnelEar(earid);
         ConfigObject cf = new ConfigObject();
         cf.setProperty("command", "killear");
         cf.setIntProperty("earid", (int)earid);
         writeControlCommand(cf);
         te.shutdown();
      }
   }   
   
   public void cleanupConnectionEarByPort(String host, int port) {
      
      Enumeration e = tunnelEars.elements();
      while(e.hasMoreElements()) {
         TunnelEarInfo te = (TunnelEarInfo)e.nextElement();
         if (host.equals(te.getHost()) && te.getPort() == port) {
            cleanupConnection(te.getEarId());
         }
      }
      
   }
   
   public void setDoTunnelSendRate(boolean onOff) {
      synchronized(senders) {
         if (!onOff) {
            setTunnelSendRate(CONSUMPTION_MAX_VALUE);
         }
         doTSR = onOff;
      }
   }   
   public boolean getDoTunnelSendRate() {
      return doTSR;
   }
   
   public void setDoConsumption(boolean onOff) {
      doConsumption = onOff;
      if (doConsumption) {
         consumptionByteCnt                  = 0;
         consumptionTotTime                  = 0;
         consumptionWaitTime                 = 0;
         consumptionOHTime                   = 0;
         NSconsumptionByteCnt                = 0;
         NSconsumptionTotTime                = 0;
         NSconsumptionWaitTime               = 0;
         NSconsumptionOHTime                 = 0;
         tunnelSendRate                      = CONSUMPTION_MAX_VALUE;
         tunnelEffectiveSendRate             = CONSUMPTION_MAX_VALUE;
      }
   }
   public boolean getDoConsumption() {
      return doConsumption;
   }
   
   public void sendDoConsumption(boolean v) {
      if (protover >= 2) {
         ConfigObject cf = new ConfigObject();
         cf.setBoolProperty("onOff", v);
         cf.setProperty("command", "doconsumption");
         writeControlCommand(cf);
      }
   }
   
   public void sendConsumptionRate(byte eid, byte iid, int rate) {
      if (protover >= 2 && doConsumption) {
         ConfigObject cf = new ConfigObject();
         cf.setProperty("command",     "consumption");
         cf.setIntProperty("earid",    (int)eid);
         cf.setIntProperty("instid",   (int)iid);
         cf.setIntProperty("rate",     rate);
         writeControlCommand(cf);
      }
   }
   
   public void adjustTunnelSendRate(long writetime, 
                                    long totlen) {
      adjustTunnelSendRate(writetime, totlen, 0, 0);
   }
   
   public void adjustTunnelSendRate(long writetime, 
                                    long totlen,
                                    long waittime,
                                    long overheadtime) {
      
      long curtime       = System.currentTimeMillis();      
      
    synchronized(senders) { 
      try {
         
        // Add or remove senders as appropriate
         if (autoAdjustSenders) {
            
            boolean adjustmentMade = false;
            
            NSconsumptionTotTime  += writetime;
            NSconsumptionByteCnt  += totlen;
            NSconsumptionWaitTime += waittime;
            NSconsumptionOHTime   += overheadtime;
            
            int numsendIn = getNumSenders();
            if (numsendIn < 1) numsendIn = 1;
            
            if (startAutoSenderTime == 0) {
               startAutoSenderTime = curtime;
            }
            
            long intervalDelta = curtime - startAutoSenderTime;
            if (intervalDelta < 0) {
               startAutoSenderTime = curtime;
            } else if (intervalDelta >= MAX_SENDER_ADJUST_DELTA) {
               
              /*
              ** If we have sustained xmit over the last interval, consider
              **  adding a sender. Sustained xmit here means that the 
              **  writetime+OHtime specified is > 75% of elapsed time in
              **  the interval (adjusted for the number of senders). 
              **  
              ** If the first step passes, then check if we have tried 
              **  adding this sender within the last minute, if so, see if
              **  things got better, worse, or no change. If better (increase
              **  of 20% or more), then allow it, otherwise, don't do it.
              */
               long hiwater    = intervalDelta - (intervalDelta >> 2);
               long lowater    = intervalDelta >> 1;
               
               hiwater            *= numsendIn;
               lowater            *= numsendIn;
               long ncbc1000 = ((long)NSconsumptionByteCnt)*1000;
               long bps  = ncbc1000/(NSconsumptionTotTime+1);
               long ebps = ncbc1000/(intervalDelta+1);
               
               DebugPrint.println(DebugPrint.INFO2, 
                                  "consumptime[" + NSconsumptionTotTime +
                                  "] consumpwait[" + NSconsumptionWaitTime +
                                  "] consumpOH[" + NSconsumptionOHTime +
                                  "] walltime[" + intervalDelta +
                                  "] bytes[" + NSconsumptionByteCnt +
                                  "] lowater["     + lowater + 
                                  "] hiwater["     + hiwater +
                                  "] senders["     + numsendIn + 
                                  "] BPS[" + bps + 
                                  "] EBPS[" + ebps + "]");
                                  
               
               Integer numsendInINT = new Integer(numsendIn);
               SenderTabs sendbps = 
                  (SenderTabs)senderPerformance.get(numsendInINT);
                  
               if (sendbps == null) {
                  sendbps = new SenderTabs(ebps, curtime); 
                  senderPerformance.put(numsendInINT, sendbps);
                  DebugPrint.println(DebugPrint.INFO3, 
                                     "Setting MAX for sender " + numsendIn +
                                     " = " + ebps);
               } else if (!sendbps.isCurrent(curtime) ||
                          sendbps.getBPS() < ebps) {
                  DebugPrint.println(DebugPrint.INFO3, 
                                     "Setting MAX for sender " + numsendIn +
                                     " = " + ebps);
                  sendbps.setBPS(ebps);
                  sendbps.setTime(curtime);
               }
               
               if (numsendIn > 1) {
                  Integer smallerNumSend = new Integer(numsendIn-1);
                  SenderTabs smallerbps = 
                     (SenderTabs)senderPerformance.get(smallerNumSend);
                  if (smallerbps == null || !smallerbps.isCurrent(curtime)) {
                     DebugPrint.println(DebugPrint.INFO2, 
                                        "SM: removing sender, no info on previous state.");
                     forceSenderSize(numsendIn-1);
                     adjustmentMade = true;

                  } else {
                     long better = ((smallerbps.getBPS())*100)/(sendbps.getBPS()+1);
                     if (better > 90) {
                        DebugPrint.println(DebugPrint.INFO2, 
                                           "SM: removing sender, previous better acceptable. better = " + better);
                        forceSenderSize(numsendIn-1);
                        adjustmentMade = true;
                     }
                  }
               }
               
               
               if (NSconsumptionTotTime >= hiwater && !adjustmentMade) {
                  SenderTabs nextbps = (SenderTabs)
                     senderPerformance.get(new Integer(numsendIn+1));
                     
                  if (nextbps != null && nextbps.isCurrent(curtime)) {
                     long better = ((nextbps.getBPS())*100)/(sendbps.getBPS()+1);
                     if (better > 120) {
                        if (numsendIn < maxSenders) {
                           DebugPrint.println(DebugPrint.INFO3, 
                                              "SM: adding sender, better = " + 
                                              better);
                           addNewSender();
                           adjustmentMade = true;
                        }
                     } else {
                        DebugPrint.println(DebugPrint.INFO3, 
                                           "SM: skip sender add, better = " + 
                                           better);
                     }
                  } else {
                     if (numsendIn < maxSenders) {
                        addNewSender();
                        adjustmentMade = true;
                     }
                  }
                  
               } else if (NSconsumptionTotTime <= lowater && !adjustmentMade) {
                  if (numsendIn > 1) {
                     DebugPrint.println(DebugPrint.INFO2, 
                                        "Removing Sender: " + (numsendIn-1));
                     forceSenderSize(numsendIn-1);
                     adjustmentMade = true;
                  }
               }
               
               startAutoSenderTime = curtime;
               NSconsumptionByteCnt  = 0;
               NSconsumptionTotTime  = 0;
               NSconsumptionWaitTime = 0;
               NSconsumptionOHTime   = 0;
            }
         }
         
        // Do consumption now that we know the number of senders
         long elapWallTime  = 0;
         if (startConsumptionTime == 0 || startConsumptionTime > curtime) {
            startConsumptionTime = curtime;
         } else {
            elapWallTime = curtime - startConsumptionTime;
         }
         
         
//      if (totlen >= CONSUMPTION_CONSIDER_BYTECOUNT) { 
         consumptionTotTime    += writetime;
         consumptionByteCnt    += totlen;
         consumptionWaitTime   += waittime;
         consumptionOHTime     += overheadtime;
         
//      }
         if (consumptionByteCnt >= CONSUMPTION_BYTE_MAX    || 
             consumptionTotTime >= CONSUMPTION_RECALC_TIME || 
             elapWallTime       >= CONSUMPTION_MAX_WALL_TIME) {
         
            startConsumptionTime = curtime;
         
            int instrate = 0;            
            int numsendCur = getNumSenders();
            int tsrx3 = tunnelSendRate*3;
            if (consumptionByteCnt > 0 && consumptionTotTime > 0) {
               long cbc = (((long)consumptionByteCnt)*1000);
               instrate = (int)(cbc/consumptionTotTime);
            } else {
               instrate = tsrx3;
            }
            
           // Tunnel throttles up no more than double its previous rate
            if (instrate > tsrx3) {
               instrate = tsrx3;
            }
            
           // At this point, instrate is the actual bps rate realized, 
           //  but its a PER CONNECTION rate. Connections will be cleaned
           //  up if they are under utilized, so just multiply by 
           //  number of connections
            
            setTunnelSendRate(instrate * numsendCur);            
            consumptionByteCnt  = 0;
            consumptionTotTime  = 0;
            consumptionWaitTime = 0;
            consumptionOHTime   = 0;
         }
      } catch(Throwable tt) {
         DebugPrint.printlnd(
            DebugPrint.ERROR, 
            "Error setting TunnelRate rate info cbc=" +
            consumptionByteCnt + " ctt=" + consumptionTotTime);
         DebugPrint.println(DebugPrint.ERROR, tt);
      } 
    } // synchronized
    
   }
      
   public int getTunnelEffectiveSendRate() { 
      return tunnelEffectiveSendRate; 
   }
   
   public int  getTunnelSendRate() { return tunnelSendRate; }
   public void setTunnelSendRate(int rate) { 
   
      if (doTSR) {
         synchronized(senders) {
            tunnelSendRate = 
               (rate <= CONSUMPTION_MIN_VALUE) ? 
               CONSUMPTION_MIN_VALUE :
               ((tunnelSendRate > CONSUMPTION_MAX_VALUE) ? 
                CONSUMPTION_MAX_VALUE : rate);
            
            if (doCutdownEffectiveRate) {
               tunnelEffectiveSendRate = tunnelSendRate - (tunnelSendRate >> 3);
            } else {
               tunnelEffectiveSendRate = tunnelSendRate;
            }
            
           // For each SocketInputBuffer, use this rate if < SIB rate
            if (DebugPrint.getLevel() >= DebugPrint.INFO3) {
               DebugPrint.println(DebugPrint.INFO3,
                                  "SM: setTunnelSendRate(" + rate + ")");
            }
         }
      }
   }
   
  // This sets the consumption rate value for SocketInputBuf
   public void setConsumptionRate(byte eid, byte iid, int instrate) {
      TunnelSocket ts = null;
      TunnelEarInfo te = getTunnelEarInfo(eid);
      
      if (te != null) {
         ts = te.getTunnelSocket(iid);
      }
      
      if (ts != null) {
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               "SM: setConsumption(" + instrate + "): ts: " 
                               + ts.toString());
         }
         
         synchronized (buffers) {
            SocketInputBuffer ib = ts.getInputBuffer();
            ib.setBPS(instrate);
         }
         
      } else {
         DebugPrint.println(DebugPrint.INFO, 
                            "SM: setConsumption: (Ear/Inst) not found! (" + 
                            eid + "/" + iid + ")");
      }
      
   }
   
   public void sendXon(byte eid, byte iid) {
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "xon");
      cf.setIntProperty("earid", (int)eid);
      cf.setIntProperty("instid", (int)iid);
      writeControlCommand(cf);
   }
   
   public void sendXoff(byte eid, byte iid) {
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "xoff");
      cf.setIntProperty("earid", (int)eid);
      cf.setIntProperty("instid", (int)iid);
      writeControlCommand(cf);
   }
   
   public TunnelEarInfo createEar(boolean isica, String h, 
                                  int port, String other) {
      return createEar(isica?"ica":"X", h, port, other); 
   }
   
   public TunnelEarInfo createEar(String eartype, String h, 
                                  int port, String other) {
                                  
      byte earid = getNextEarId();
      
      /* subu 05/16/02 */
      DebugPrint.println(DebugPrint.DEBUG3, 
                         "SM:createEar"+" port: "+port+" earid: "+earid); 
      
      TunnelEarInfo ret = new TunnelEarInfo(this, eartype, (byte)earid, 
                                            h, port, other);
      registerTunnelEar(ret);
	
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "newear");
      cf.setIntProperty("earid", (int)earid);
      cf.setProperty("eartype", eartype);
      cf.setProperty("targethost", h);
      cf.setIntProperty("targetport", port);
      
      if(ret.isRM()) {
	cf.setProperty("otherinfo",other);
      }
      
      writeControlCommand(cf);
      return ret;
   }
   
   public String desktopID() {
      return desktopid;
   }
   public void desktopID(String dtid) {
      desktopid = dtid;
   }
   public boolean doCombo() {
      return docombo;
   }
   public void doCombo(boolean v) {
      docombo = v;
   }
   public boolean doDebug() {
      return dodebug;
   }
   public void doDebug(boolean v) {
      dodebug = v;
   }
   public boolean doSecure() {
      return dosecure;
   }
   public void doSecure(boolean v) {
      dosecure = v;
   }
   public boolean doStreaming() {
      return dostream;
   }
   public void doStreaming(boolean v) {
      dostream = v;
   }
/**
 * Calling thread is blocked until an input buffer is available (could be
 *  immediate
 */
   public SocketInputBuffer getInputBuffer(int to) {
      int iter = 0;
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, "getInputBuffer: Enter");
      }
      
      SocketInputBuffer ret = null;
      while (ret == null && (iter == 0 || to < 0)) {
         synchronized (buffers) {
            ret = getInputBufferNoblock();
            if (ret == null && to != 0) {
               try {
                  if (to > 0) {
                     buffers.wait(to);
                  } else if (to < 0) {
                     buffers.wait();	
                  }
                  
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "getInputBuffer:  Wake!");
                  }
                  
               } catch (InterruptedException e) {
                  ;
               }
               ret = getInputBufferNoblock();
            }
         }
         iter++;
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, "getInputBuffer: Exit");
      }
      
      return ret;
   }
/**
 * Calling thread gets an available buffer or null
 */
   public SocketInputBuffer getInputBufferNoblock() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, "getInputBufferNoblock");
      }
      
      SocketInputBuffer ret = null;
      synchronized (buffers) {
         if (!buffers.isEmpty()) {
            ret = (SocketInputBuffer) buffers.firstElement();
            buffers.removeElementAt(0);
            ret.isInReserve(false);
         }
      }
      return ret;
   }

   public static SessionManager getLastSessionMgr() {
      return lastSess;
   }         
  /**
   * Add specified TunnelEarInfo to the Session to be managed
   */
   public byte getNextEarId() {
      byte earid = 1;
      while(earid != 0 && tunnelEars.containsKey(new Byte(earid))) {
         earid++;
      }

      if (earid == 0) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "ZOINKS! SesMgr: GetNextEarId == 0!!!");
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SessManager: GetNextEarId found " + earid);
      }
      
      return earid;
   }   
  /**
   * Return the Tunnel instance associated with the specified tunnelEarId (id1)
   *  and instance id (id2).
   */
   public TunnelEarInfo getTunnelEarInfo(byte id1) {
      TunnelEarInfo te = (TunnelEarInfo)tunnelEars.get(new Byte(id1));
      return te;
   }   
   
   public Enumeration getTunnelEarInfos() {
      return tunnelEars.elements();
   }   
  /**
   * Return the Tunnel instance associated with the specified tunnelEarId (id1)
   *  and instance id (id2).
   */
   public TunnelSocket getTunnelSocket(byte id1, byte id2) {
      TunnelSocket ret = null;
      TunnelEarInfo te = (TunnelEarInfo)tunnelEars.get(new Byte(id1));
      if (te != null) {
         ret = te.getTunnelSocket(id2);
      }
      return ret;
   }   
   public String host() {
      return hostv;
   }
   public void host(String v) {
      hostv = v;
   }

   public SMStats getLastIntervalStats() { 
      return new SMStats(lastInterval); 
   }
   public SMStats getCurrentIntervalStats() {
      return new SMStats(currentInterval); 
   }
   public SMStats getTotalStats() { 
      return new SMStats(totalCnt);        
   }
   public SMStats getSinceLastStats() {
      return new SMStats(sinceLastQuery);  
   }
   
   public void resetSinceLastStats() {
      sinceLastQuery.reset();
   }
   public void resetCurrentIntervalStats() {
      currentInterval.reset();
   }
   public void copyCurrentToLastAndResetStats(Date indate) {
      lastInterval = currentInterval;
      lastInterval.freezeElap(indate);
      currentInterval = new SMStats();
   }
   
   public long getTotIn()         { return totalCnt.getTotIn();   }
   public long getTotOut()        { return totalCnt.getTotOut();  }
   public long getTotUncompressedIn() {
      return totalCnt.getTotUncompressedIn();   
   }
   public long getTotUncompressedOut() {
      return totalCnt.getTotUncompressedOut();   
   }
   public long getDataIn()        { return totalCnt.getDataIn();  }
   public long getDataOut()       { return totalCnt.getDataOut(); }
   public long getUncompressedDataIn() {
      return totalCnt.getUncompressedDataIn();   
   }
   public long getUncompressedDataOut() {
      return totalCnt.getUncompressedDataOut();   
   }
   public long getTotControlIn()  { return totalCnt.getTotControlIn();  }
   public long getTotControlOut() { return totalCnt.getTotControlOut(); }
   
   public void incrLocalIn(int amt) {
      totalCnt.incrLocalIn(amt);
      sinceLastQuery.incrLocalIn(amt);
      currentInterval.incrLocalIn(amt);
   }      
   
   public void incrLocalOut(int amt) {
      totalCnt.incrLocalOut(amt);
      sinceLastQuery.incrLocalOut(amt);
      currentInterval.incrLocalOut(amt);
   }      
   
   public void incrIn(int amt) {
      totalCnt.incrIn(amt, amt);
      sinceLastQuery.incrIn(amt, amt);
      currentInterval.incrIn(amt, amt);
   }      
   public void incrIn(int amt, int uncomp) {
      totalCnt.incrIn(amt, uncomp);
      sinceLastQuery.incrIn(amt, uncomp);
      currentInterval.incrIn(amt, uncomp);
   }      

   public void incrOut(int amt, int uncomp) {
      totalCnt.incrOut(amt, uncomp);
      sinceLastQuery.incrOut(amt, uncomp);
      currentInterval.incrOut(amt, uncomp);
   }         
   public void incrOut(int amt) {
      totalCnt.incrOut(amt, amt);
      sinceLastQuery.incrOut(amt, amt);
      currentInterval.incrOut(amt, amt);
   }         
   public void incrControlIn(int amt) {
      totalCnt.incrControlIn(amt);
      sinceLastQuery.incrControlIn(amt);
      currentInterval.incrControlIn(amt);
   }      

   public void incrControlOut(int amt) {
      totalCnt.incrControlOut(amt);
      sinceLastQuery.incrControlOut(amt);
      currentInterval.incrControlOut(amt);
   }         
   
   public boolean keepRunning() { return dorun; }
   public void keepRunning(boolean v) { dorun = v; }
   
   public boolean startingShutdown() { return startshutdown; }
   public void startingShutdown(boolean v) { startshutdown = v; }
   
   public int nextUnique() { return uniq++; }
   
  /**
   * Add specified TunnelEarInfo to the Session to be managed
   */
   public void registerTunnelEar(TunnelEarInfo t) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SessManager: Register TunnelEar for " + 
                            t.getEarId());
      }
      
      tunnelEars.put(new Byte(t.getEarId()), t);
   }   
   public TunnelEarInfo removeTunnelEar(byte id) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SessManager: Remove TunnelEar for " + id);
      }
      
      return (TunnelEarInfo)tunnelEars.remove(new Byte(id));
   }   
   public void run() {

     /* Not Used ... tried during BS Explorer bug search */
      Enumeration e = threads.elements();
      while(e.hasMoreElements()) {
         Thread t = (Thread)e.nextElement();
         DebugPrint.println(DebugPrint.DEBUG, 
                            "SesMgr: WOULD ShutdownThread killing [" 
                            + t.toString() + "]");
        // t.stop();
      }

      threads = null;
      DebugPrint.println(DebugPrint.DEBUG, 
                         "SesMgr THread: Calling sleep, then shutdown ...");
      try {
         Thread.sleep((long)5000);
      } catch (Exception ex) {}
      DebugPrint.println(DebugPrint.DEBUG, "SesMgr THread: Calling Shutdown");
      shutdown();
      DebugPrint.println(DebugPrint.DEBUG, 
                         "SesMgr THread: back from shutdown, quit");
   }
   public void shutdown() {
      boolean lkeepRunning = false;
      Enumeration e = null;
      Vector v = null;
      synchronized (this) {
         lkeepRunning = keepRunning();
         if (lkeepRunning) {
            v = new Vector();
            e = tunnelEars.elements();
            while (e.hasMoreElements()) {
               v.addElement(e.nextElement());
            }
            e = v.elements();
            tunnelEars.clear();
            keepRunning(false);
         }
      }
      if (lkeepRunning) {
           
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG, 
               "SessionMgr: shutdown: sending shutdown cmd to other side");
         }
         
         ConfigObject o = new ConfigObject();
         o.setProperty("command", "shutdown");
         writeControlCommand(o);
           
         while (e.hasMoreElements()) {
            TunnelEarInfo te = (TunnelEarInfo) e.nextElement();
            try {
               
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                   "SesMgr: Calling TE->Shutdown =>\n\t" 
                                     + te.toString());
               }
               
               te.shutdown();
            } catch (Exception ex) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "Error while shutting down tunnelEar: " 
                                  + te.toString());
               DebugPrint.println(DebugPrint.DEBUG, ex);
            }
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "SesMgr: After shutdown iter");
            }
         }
         v = null;
		
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               "SesMgr: Killing all active threads");
         }
         
         Thread cur = Thread.currentThread();
                
         if (threads != null) {
            int num = threads.size();
            boolean curfound = false;
            for(int i=0; i < num; i++) {
               Thread thread = (Thread)threads.elementAt(i);
               if (thread != cur) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "SesMgr: SHOULD stop thread: " + i + " "
                                     + thread.toString() + " isAlive: " 
                                     + thread.isAlive() + " is Interr: " 
                                     + thread.isInterrupted());
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "SHOULD Stop thread : " + i + " : " +
                                     thread.toString()); 
                 //thread.stop();
               } else {
                  curfound = true;
               }
            }
            threads = null;
            System.gc();
                   
            if (curfound) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "SesMgr: SHOULD Killing my thread: " + 
                                  cur.toString() + " isAlive: " + 
                                  cur.isAlive() + " is Interr: " + 
                                  cur.isInterrupted());
              // cur.stop();
            }
         }
                
                
        /*
          Thread threads[] = new Thread[100];
          int num = cur.enumerate(threads);
          for(int i=0; i < num; i++) {
          if (threads[i] != cur) {
          System.out.println("Stopping thread: " + threads[i].toString());	
          cur.stop();
          }
          }

          System.gc();
          DebugPrint.println("SesMgr: Killing my thread: " + cur.toString());
          cur.stop();
        */
        
         fireShutdownEvent();
      }
   }
   
   public void fireActionEvent(String evname) {
      if (actionListener != null) {
         actionListener.actionPerformed(
            new ActionEvent(this,ActionEvent.ACTION_PERFORMED,
                            evname));
      }
   }
   
   protected void fireShutdownEvent() {
      fireActionEvent("TunnelShutdown");
   }
   
   public void fireActionEvent(ActionEvent e) {
      if (actionListener != null) {
         actionListener.actionPerformed(e);
      }
   }
   
  /**
   * Write the ConfigObject down the tunnel (schedule it anyway)
   *
   *   If ConfigObject is null, still add controlBuffer, will result in a
   *    0 length tunnel message. Sok.
   */
   public void writeControlCommand(ConfigObject o) {
      synchronized(controlBuffer) {
      
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, "Writing control command");
            if (o != null) {            
               DebugPrint.println(DebugPrint.DEBUG, o.toString());
            }
         }
         
         if (o != null) {
            o.save(controlBuffer);
            controlBuffer.write(CONTROL_SEPARATOR);
         }
         
         addInputBuffer(controlBuffer);
      }
   }   
   
   public SocketSMInputBuffer getControlBuffer() {
      return controlBuffer;
   }   
   
   public synchronized 
   void addActionListener(java.awt.event.ActionListener l) {
      if (l == null) {
         return;
      }
      actionListener = AWTEventMulticaster.add(actionListener, l);
   }
   public synchronized 
   void removeActionListener(java.awt.event.ActionListener l) {
      if (l == null) {
         return;
      }
      actionListener = AWTEventMulticaster.remove(actionListener, l);
   }
   
   
  // ----------------- Methods from HttpSessionManager ------------------
  
   protected Hashtable messages = new Hashtable();
   public    final int MAX_SENT_LEN = 1536*1024;  // 1.5Meg
   public    final int MAX_SENT_CNT = 240;
   protected       int totsentmsglen = 0;
   
  // Wait if messages awaiting Acks > threshold 
  // Wake up every 10 secs to do potential timeout processing
   public boolean waitOnAckThreshold() {
      boolean ret = false;
      if (messages.size() >= MAX_SENT_CNT ||
          totsentmsglen   >= MAX_SENT_LEN) {
          
         synchronized (messages) {
            if (messages.size() >= MAX_SENT_CNT ||
                totsentmsglen   >= MAX_SENT_LEN) {
               ret = true;
               try {
                  DebugPrint.println(DebugPrint.INFO4, 
                                     (new Date().toString()) + 
                                     ": WaitOnAck: Need to wait Cnt=" 
                                     + messages.size() + " len=" + 
                                     totsentmsglen + " id=" + desktopID());
                  messages.wait(10000);
               } catch(InterruptedException e) {
               }
            }
         }
         
      }
      return ret;
   }
   
   public boolean wouldWaitOnAckThreshold() {
      boolean ret = false;
      if (messages.size() >= MAX_SENT_CNT ||
          totsentmsglen   >= MAX_SENT_LEN) {
         ret = true;
      }
      return ret;
   }
   
  // Save sent message for reliability
   public void saveMessage(TunnelMessage m) {
      if (m == null) return;
      
      String s = "" + m.getEarId() + " " + m.getInstId() 
                                   + " " + m.getRecordNumberString();
      
      if (messages.put(s, m) != null) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "HTS: Ahhh HAAA! Wrapping message!!!!!! " +
                             m.toString());
      }
      
      totsentmsglen += m.getLength();
      
      if (DebugPrint.doDebug()) {
         if (messages.size() >= MAX_SENT_CNT || 
             totsentmsglen   >= MAX_SENT_LEN) {
             
            DebugPrint.println(DebugPrint.DEBUG, 
                               "HttpSesMgr: saved messages at " + 
                               messages.size() + " Len = " + totsentmsglen);
         }
      }
      
     /*
      messages.addElement(m);
      if (messages.size() > MAX_SENT_CNT) {
         TunnelMessage tm = (TunnelMessage)messages.elementAt(0);
         messages.removeElementAt(0);
         totsentmsglen -= tm.getLength();
      }
     */
      
   }
   
   public boolean removeMessage(byte ear, byte inst, byte recnum) {
      String s = "" + ear + " " + inst + " " + recnum;
      return removeMessage(s);
   }
   public boolean removeMessage(String s) {
   
      boolean ret = false;
      int i, len;
      synchronized (messages) {
         
         int oldlen = totsentmsglen;
         int oldcnt = messages.size();
         
         TunnelMessage m = (TunnelMessage)messages.remove(s);
         if (m != null) {
            ret = true;
            totsentmsglen -= m.getLength();
            if (totsentmsglen < 0) totsentmsglen = 0;
            
            if (oldcnt+1 == MAX_SENT_CNT || 
                (oldlen >= MAX_SENT_LEN && totsentmsglen < MAX_SENT_LEN)) {
                
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG,
                     "HttpSesMgr: saved messages < thrshold. Notify " 
                     + messages.size() + " Len = " + totsentmsglen);
               }
               
               messages.notifyAll();
            }
         } else {
            DebugPrint.println(DebugPrint.WARN, 
                  "HttpSesMgr: Got removeMsg, but not in message list: " + s);
         }
      }
      return ret;
   }
      
   public Enumeration getSavedMessages() {
      return messages.elements();
   }
   
   
  // ----------------- Methods from AppletSessionManager ------------------
     
   protected Vector  acks = new Vector();
   protected int totackmsglen = 0;
   public final int MAX_WAITING_ACKS = 100;
   public final int MAX_WAITING_LEN  = 200000;
   
   public boolean flushAcks() {
      ConfigObject o = null;
      
     // True if flushed cause we are full
      boolean bigflush = false;
      
      synchronized (acks) {
         int sz = acks.size();
         if (sz > 0) {
         
            if (acks.size()   > MAX_WAITING_ACKS || 
                totackmsglen >= MAX_WAITING_LEN) {
               bigflush = true;
            }
         
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "AppletSessionMgr: flushAcks. num = " 
                                  + acks.size() + " totlen = " + totackmsglen);
            }
            
            o = new ConfigObject();
            o.setProperty("command", "acks");
            for(int i=0; i < sz; i++) {
               String s = (String)acks.elementAt(i);
               o.setProperty("" + (i+1), s);
            }
            acks.removeAllElements();
            totackmsglen = 0;
         }
         if (o != null) {
            writeControlCommand(o);
         }
      }
      
     // If we have flushed ACKS which could prevent acking deadlock, AND, 
     //  our senders would block awaiting acks,  kick them awake
      if (bigflush && wouldWaitOnAckThreshold()) {
         synchronized(messages) {
            messages.notifyAll();
         }
      }
      return o != null;
   }
   
   public void sendAck(TunnelMessage m) {
      String s = "" + m.getEarId() + " " + m.getInstId() 
                                   + " " + m.getRecordNumberString();
      synchronized (acks) {
         acks.addElement(s);
         totackmsglen += m.getLength();
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG2, 
                               "AppletSessionMgr: sendAck. Adding[ " + 
                               m.toString() + "] acknum = " 
                               + acks.size() + " acktotlen = " + totackmsglen);
         }
         if (acks.size()   > MAX_WAITING_ACKS || 
             totackmsglen >= MAX_WAITING_LEN) {
            flushAcks();
         }
      }
   }
   
   
   public String toString() {
      return "SessionMgr: TunnelSendRate=" + tunnelSendRate + " tunnelEffSendRate=" + tunnelEffectiveSendRate + " senders=" + getNumSenders() + " totin=" + getDataIn() + " totout=" + getDataOut() +
         " totCin=" + getTotControlIn() + " totCout=" + getTotControlOut() +
         " TunnelEars =>\n" + tunnelEars.toString() + "\n\n Acks => num=" + 
         acks.size() + " len=" + totackmsglen + "\n" + acks +
         "\n WAITING Acks => num=" + messages.size() + " len=" + 
         totsentmsglen;
   }      
}
