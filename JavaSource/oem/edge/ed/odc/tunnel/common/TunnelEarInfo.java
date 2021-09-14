package oem.edge.ed.odc.tunnel.common;

import java.io.*;
import java.net.*;
import java.util.*;
import com.ibm.as400.webaccess.common.*;

public class TunnelEarInfo implements TunnelCommon {
   protected byte      earid;
   protected boolean  shuttingDown=false;
   protected Hashtable mysockets  = new Hashtable();
   protected String    remoteHost = null;
   protected String    otherInfo = null;
   protected SessionManager sessionMgr = null;
   protected int    localport = -1;
   protected int       remotePort = -1;
   protected String earType = null;
   
   protected Vector listeners = null;
   
   protected Object userdata = null;
   public void   setUserData(Object v) { userdata = v;    }
   public Object getUserData()         { return userdata; }
   
   private byte nextInstId = 1;
   
  // Returns 0 if no free ID found
  //  ---> Must be called Synchronized on mysockets!! or there is a
  //       window of vuln. Kept as a standalone so different subclasses
  //       of TunnelSocket can be created and added.
   protected byte getNextFreeId() {
      byte wrapid = getNextId();
      byte curid = wrapid;
      do {
         if (getTunnelSocket(curid) == null) {
            return curid;
         }
         if (++curid == 0) curid++;
      } while(curid != wrapid);
      return 0;
   }   
   
  // Never return 0
   protected byte getNextId() {
      if (nextInstId == 0) nextInstId++;
      return nextInstId++;
   }   
   
   public void addListener(TunnelEarListener tl) {
      if (listeners == null) listeners = new Vector();
      listeners.addElement(tl);
   }
   public void removeListener(TunnelEarListener tl) {
      if (listeners != null) {
         synchronized(listeners) {
            for(int i=listeners.size()-1; i >= 0; i--) {
               if ((Object)tl == listeners.elementAt(i)) {
                  listeners.removeElementAt(i);
               }
            }
         }
      }
   }
   
  // A Direct connect socket should be better/faster than the socket pair
  //  as the application directly reads/writes from/into the SIB/SOB. Just
  //  trying to make things faster
   public Socket generateDirectConnectSocket() {
      Socket ret = null;
      try {
         TunnelSocket ts = makeTunnelSocket(null);
         ret = new ISocket(ts.getInputBuffer(), ts.getOutputBuffer());
      } catch(Exception ee) {}
      return ret;
   }
   
  // Generates a 'socket (OSocket) which is paired to another OSocket, tied and
  // tied into the tunnel via a TunnelSocket. Returns the non-tunnel socket. 
  // So, tunnel reads/writes to one OSocket, and the other 'incore' entity 
  // (what-ever that beast may be) reads and writes to the other end ... all
  // just buffer xfers.
   public Socket generatePairedSocket(String n) {
      Socket ret = null;
      try {
         OSocket pair = OSocket.socketpair();
         pair.setName("Tnl");
         pair.getOther().setName(n);
         TunnelSocket ts = this.makeTunnelSocket(pair);
         ret = pair.getOther();
         ts.setCallMainConnComplete(isICA() || isNewODC() || 
                                    isXFR() || isFDR());
      } catch(Exception ee) {}
      return ret;
   }
   
   void fireSocketCreated(TunnelSocket ts) {
      if (listeners != null) {
         Enumeration enum = listeners.elements();
         while(enum.hasMoreElements()) {
            TunnelEarListener tel = (TunnelEarListener)enum.nextElement();
            tel.socketCreated(ts);
         }
      }
   }
   
   void fireSocketDestroyed(TunnelSocket ts) {
      if (listeners != null) {
         Enumeration enum = listeners.elements();
         while(enum.hasMoreElements()) {
            TunnelEarListener tel = (TunnelEarListener)enum.nextElement();
            tel.socketDestroyed(ts);
         }
      }
   }
   
   void fireEarDestroyed(TunnelEarInfo tei) {
      if (listeners != null) {
         Enumeration enum = listeners.elements();
         while(enum.hasMoreElements()) {
            TunnelEarListener tel = (TunnelEarListener)enum.nextElement();
            tel.earDestroyed(tei);
         }
      }
   }
   
   public TunnelEarInfo(SessionManager sm, String et, byte id, String host, 
                        int port, String other) {
      sessionMgr = sm;
      otherInfo  = other;
      earid      = id;
      remoteHost = host;
      remotePort = port;
      earType    = et;
   }   
   public void addTunnelSocket(TunnelSocket ts) {
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "Adding Tunnel Socket");
      }
      
      if (otherInfo != null && isX()) {
         ts.getOutputBuffer().setXCookie(otherInfo);
      }
      mysockets.put(new Byte(ts.getId()), ts);
      fireSocketCreated(ts);
   }   
   
   public byte getEarId() {
      return earid;
   }   
   public String getHost() { return remoteHost; }   
   public int getLocalPort() {
      return localport;
   }   
   public String getOtherInfo() { return otherInfo; }   
   public int    getPort() { return remotePort; }   
   
   public TunnelSocket getTunnelSocket(byte id) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5,
                            "TunnelEarInfo: GetTunnelSocket for " + id);
      }
      
      return (TunnelSocket)mysockets.get(new Byte(id));
   }   
   public void informEarCreated() {
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "earcreated");
      cf.setIntProperty("earid", (int)earid);
      cf.setProperty("eartype", earType);
      cf.setProperty("targethost", remoteHost);
      cf.setIntProperty("targetport", remotePort);
      try {
         cf.setProperty("localhost", 
                        InetAddress.getLocalHost().getHostAddress());
      } catch (Throwable t) {}
      
      cf.setIntProperty("localport", localport);
      cf.setProperty("otherinfo", otherInfo != null?otherInfo:"");
      sessionMgr.writeControlCommand(cf);	   
   }   
   public boolean isICA() { return earType.equalsIgnoreCase("ica"); }   
   
   public boolean isNewODC() { return earType.equalsIgnoreCase("NEWODC"); }
   
   /* subu 05/23/02 */  
   public boolean isIM()  { return earType.equalsIgnoreCase("IM")  || 
                                   earType.equalsIgnoreCase("IMS"); 
   }
   
   public boolean isRM()  { return earType.equalsIgnoreCase("RM")  ||
                                   earType.equalsIgnoreCase("STM"); 
   } 
   public boolean isFTP() { return earType.equalsIgnoreCase("ftp"); }
   public boolean isXFR() { return earType.equalsIgnoreCase("xfr"); }
   public boolean isFDR() { return earType.equalsIgnoreCase("fdr"); }
   public boolean isX()   { return earType.equals("X"); }
   
   public void   setEarType(String v) { earType = v; }   
   public String getEarType() { return earType; }
   
   public TunnelSocket removeTunnelSocket(byte instid) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "Removing Tunnel Socket");
      }
      
      TunnelSocket ret = (TunnelSocket)mysockets.remove(new Byte(instid));
      if (ret != null) {
         fireSocketDestroyed(ret);
      }
      return ret;
   }   
   
   public void setLocalPort(int lp) {
      localport = lp;
   }   
   public void setOtherInfo(String other) {
      otherInfo = other;
   }   
   
   
   public TunnelSocket makeTunnelSocket(Socket sock) throws IOException {
      
      TunnelSocket tunnelSocket = null;
      synchronized(mysockets) {
         byte curid = getNextFreeId();
         if (curid != 0) {
            tunnelSocket = new TunnelSocket(sessionMgr, earType, 
                                            sock, 
                                            getEarId(), 
                                            curid, null);
            addTunnelSocket(tunnelSocket);
         } else {
            DebugPrint.println(DebugPrint.ERROR,
                               "TunThrd: Too many ear instances!");
            throw new IOException("TunThrd: Too many ear instances.");
         }
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "New Conection Established : ear=" + getEarId() + 
                            " inst=" + tunnelSocket.getId());
      }
      
      return tunnelSocket;
   }
   
   public void shutdown() {
	
      boolean sd = false;
	
      synchronized (this) {
         if (!shuttingDown) {
            sd = true;
            shuttingDown = true;
         }
      }
	
      if (sd) {
         shutdownInstances();
      }
      fireEarDestroyed(this);
   }
   public void shutdownInstances() {
      Enumeration e;
      Vector v = new Vector();
      synchronized (this) {
         e = mysockets.elements();
         while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
         }
         mysockets.clear();
      }
      e = v.elements();
      while (e.hasMoreElements()) {
         TunnelSocket ts = (TunnelSocket) e.nextElement();
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG,
                               "TEI: ShutdownInst =>\n   " + ts.toString());
         }

         try {
            fireSocketDestroyed(ts);
            ts.shutdown();
         } catch (Exception ex) {
            ex.printStackTrace(System.out);
         }
      }
      
      System.gc();
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "TEI: ShutdownInst exit: " + toString());
      }
   }
   
   public String toString() {
      return "TunnelEarInfo: " + " earid=" + earid
         + " earTp=" + earType
         + " rHost=" + remoteHost 
         + " rPort=" + remotePort
         + " lPort"  + localport
         + " oinfo=" + otherInfo
         + " Sockets =>\n" 
         + mysockets.toString() + "\n";
   }   
}
