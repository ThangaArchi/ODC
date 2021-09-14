package oem.edge.ed.odc.tunnel.common;

/**
 * Insert the type's description here.
 * Creation date: (10/3/00 5:02:59 PM)
 * @author: Administrator
 */

import java.lang.*;
import java.net.*;
import java.io.*;

public class ISocket extends java.net.Socket {
   int timeout = 0;
   boolean nodelay = false;
   SocketOutputBuffer sockobuf = null;
   SocketInputBuffer  sockibuf = null;
   InputStream in   = null;
   OutputStream out = null;
   int linger = 0;
   boolean closed = false;
   
   public class OStream extends OutputStream {
      boolean isclosed = false;
      
      public  void close() {
         isclosed = true;
         sockibuf.stopReading();
      }
   
      public boolean isClosed() {
         return isclosed;
      }
      
      public void flush() throws IOException {
         if (isclosed) throw new IOException("Stream is Closed");
      }
      public void write(byte b[]) throws IOException {
         write(b, 0, b.length);
      }
      public void write(byte b[], int off, int len) throws IOException {
         sockibuf.writeData(b, off, len);
      }
      public void write(int ch) throws IOException {
         byte b[] = new byte[1];
         b[0] = (byte)ch;
         write(b, 0, 1);
      }
   }
   
   class IStream extends InputStream {
   
      boolean isclosed = false;
      public  void close() {
         isclosed = true;
         sockobuf.stopWriting();
      }
   
      public boolean isClosed() {
         return isclosed;
      }
   
      public boolean markSupported() {
         return false;
      }
   
      public int read() throws IOException {
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG,
                               ": IS READ char top");
         }
         
         byte buf[] = new byte[1];
         int ret = sockobuf.readData(buf, 0, 1);
         
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               ": IS READ char DONE");
         }
         if (ret <= 0) return -1;
         else          return buf[0];
      }
      
      public int read(byte b[]) throws IOException {
         return read(b, 0, b.length);
      }
      
      public int read(byte b[], int off, int len) throws IOException {
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               ": IS READ arr top: " + len);
         }
         
         return sockobuf.readData(b, off, len);
      }
   
      public void reset() {
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, 
                        "IS : -------------- CALLING RESET ---------------!");
         }
      }
   
      public long skip(long n) throws IOException {
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.println(DebugPrint.DEBUG, 
                        "IS : -------------- CALLING SKIP ---------------!");
         }
         
         byte b[] = new byte[(int)n>4096?4096:(int)n];
         long tot = 0;
         while(!isclosed && tot < n) {
            int toread = (int)(n-tot);
            if (toread > b.length) toread = b.length;
            int r = sockobuf.readData(b, 0, toread);
            if (r < 0) break;
            tot += r;
         }
         return tot;
      }
   }
        
        
   public ISocket(SocketInputBuffer tin, SocketOutputBuffer tout) {
      sockibuf = tin;
      sockobuf = tout;
   }
   public void close() throws IOException {
      if (closed) throw new IOException();
      closed = true;
      sockibuf.stopReading();
      sockobuf.stopWriting();      
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
      DebugPrint.println(DebugPrint.DEBUG, "ISocket getInputStream");
      synchronized(this) {
         if (in == null) in = new IStream(); 
      }
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
      DebugPrint.println(DebugPrint.DEBUG, "ISocket getOutputStream");
      synchronized(this) {
         if (out== null) out = new OStream(); 
      }
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
