package oem.edge.ed.odc.tunnel.common;

/**
 * Insert the type's description here.
 * Creation date: (10/3/00 5:02:59 PM)
 * @author: Administrator
 */

import java.lang.*;
import java.net.*;
import java.io.*;

public class OSocket extends java.net.Socket {
   int timeout = 0;
   boolean nodelay = false;
   InputStream in=null;
   OutputStream out=null;
   int linger = 0;
   boolean closed = false;
   OSocket other = null;
   
   protected String name; 
   public void setName(String n) {
      name = n;
      if (in != null && in instanceof ICAInputStream) {
         ((ICAInputStream)in).setName(n + "_IS");
      }
      if (out != null && out instanceof ICAOutputStream) {
         ((ICAOutputStream)out).setName(n + "_OS");
      }
   }
        
   public OSocket(InputStream tin, OutputStream tout) {
      in = tin;
      out = tout;
   }
   public synchronized void close() throws IOException {
      java.io.IOException eret1=null;
      java.io.IOException eret2=null;
      if (closed) throw new IOException();
      closed = true;
      try{ in.close();} catch(IOException ee1) { eret1=ee1; }
      try{out.close();} catch(IOException ee2) { eret2=ee2; }
      if (eret1 != null) throw eret1;
      if (eret2 != null) throw eret2;
   }
   public InetAddress getInetAddress() {
      InetAddress ret = null;
      try {
         ret = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
      }
      return ret;
   }
   public InputStream getInputStream() throws IOException {
      DebugPrint.println(DebugPrint.DEBUG, "OSocket getInputStream");
      return in;
   }
   public InetAddress getLocalAddress() {
      InetAddress ret = null;
      try {
         ret = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
      }
      return ret;
   }
   public int getLocalPort() {
      return 1;
   }
   public OutputStream getOutputStream() throws IOException {
      DebugPrint.println(DebugPrint.DEBUG, "OSocket getOutputStream");
      return out;
   }
   public int getPort() {
      return 1;
   }
   public int getSoLinger() throws SocketException {
      return linger;
   }
   public boolean getTcpNoDelay() throws SocketException {
      return nodelay;
   }
/* Hookup creates a short circuited OSocket pair to keep both the Tunnel and
   ICA happy.
*/
   public static OSocket hookup(SessionManager sm, String host, int port){
      DebugPrint.println(DebugPrint.DEBUG, 
                         "In Hookup with " + host + " and port " + port);
      
      OSocket forOther  = socketpair();
      OSocket forTunnel = forOther.getOther();

      try {
         if (sm == null) sm = SessionManager.getLastSessionMgr();
         TunnelEarInfo tei = (TunnelEarInfo)sm.getTunnelEarInfo((byte)port);
         tei.makeTunnelSocket(forTunnel);
      } catch(IOException e) {
         forOther = null;
      }
	   
      return forOther;
   }
        
   public static OSocket socketpair() {
      ICAInputStream ins1 = new ICAInputStream();
      ICAInputStream ins2 = new ICAInputStream();
      OSocket forTunnel   = new OSocket(ins1, ins2.getOutputStream());
      OSocket forOther    = new OSocket(ins2, ins1.getOutputStream());
      forTunnel.setOther(forOther);
      forOther.setOther(forTunnel);

      return forOther;
   }
        
   public OSocket getOther()            { return other; }
   public void    setOther(OSocket oth) { other = oth;  }
        
   public boolean isClosed() {
      return closed;
   }
   public void setSoLinger(boolean v, int val) throws SocketException {
      linger = val;
   }
   public void setSoTimeout(int t) throws SocketException {
      timeout = t;
   }
   public void setTcpNoDelay(boolean on) throws SocketException {
      nodelay = on;
   }
}
