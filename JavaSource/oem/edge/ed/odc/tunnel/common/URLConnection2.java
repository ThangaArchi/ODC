package oem.edge.ed.odc.tunnel.common;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.io.*;
import netscape.security.*;
import com.ms.security.*;

import oem.edge.ed.odc.util.SSLizer;
import oem.edge.ed.odc.util.SSLStartupException;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 


/*
** JMC 2/2/01
** 
** Sigh ...
**
**   Apache does not support simple HTTP/1.0 KeepAlive. So, I have to impl
**   HTTP/1.1 client keepalive, which results in the need to implement
**   chunked encoding, which, ...
*/

public class URLConnection2 extends java.net.URLConnection {

   class KeepAliveMarker {
      Socket s;      
      long   expiration;
      
      public KeepAliveMarker(Socket sock, long expire) { 
         s = sock;
         expiration = expire;
      }
      
      public long   getExpiration() { return expiration; }
      public Socket getSocket()     { return s;          }
   }
   
   class InformOutputStream extends ByteArrayOutputStream {
      URLConnection2 conn = null;
      public InformOutputStream(URLConnection2 u) {
         super();
         conn = u;
      }
      public void close() throws IOException {
         conn.inform(this);
      }
      public void realClose() throws IOException {
         super.close();
      }
   }
   
  // STUPID THING! For some reason, using the BaseClass impl of FilterOS 
  //               results in CRAP! Just override the write methods myself
  //               Whole point is I want to avoid the 'close'
   class MyFilterOutputStream extends FilterOutputStream {
      OutputStream myf = null;
      URLConnection2 conn = null;
      boolean closed = false;
      public MyFilterOutputStream(OutputStream os, URLConnection2 conn) {
         super(os);
         myf = os;
         this.conn = conn;
      }
      
      public void close() throws IOException {
         if (!closed) {
            myf.flush();
            closed = true;
            conn.writeclose();
         }
      }
      
      public void write(byte b[], int off, int len) throws IOException {
         if (!closed) {
            myf.write(b, off, len);
         } else {
            throw new IOException("OS Closed");
         }
      }
      public void write(byte b[]) throws IOException {
         if (!closed) {
            myf.write(b);
         } else {
            throw new IOException("OS Closed");
         }
      }
      public void write(byte b) throws IOException {
         if (!closed) {
            myf.write(b);
         } else {
            throw new IOException("OS Closed");
         }
      }
   }
   
   class URLInputStream extends InputStream {

      URLConnection2 conn = null;
      byte buf1[] = new byte[1];
      public URLInputStream(URLConnection2 u) {
         super();
         conn = u;
      }
      
      public int available() throws IOException {
         if (conn == null) throw new IOException("Fini");
         return conn.available();
      }
      
      public void close() throws IOException {
         if (conn == null) throw new IOException("Fini");
         conn.close();
         conn = null;
      }
      public int read() throws IOException {
         if (conn == null) throw new IOException("Fini");
         
         int v = 0;
         while((v = conn.read(buf1, 0, 1)) != 1 && v != -1);
         if (v == 1) v = buf1[0];
         return v;
      }
      public int read(byte b[]) throws IOException {
         if (conn == null) throw new IOException("Fini");
         return conn.read(b, 0, b.length);
      }
      public int read(byte b[], int off, int len) throws IOException {
         if (conn == null) throw new IOException("Fini");
         return conn.read(b, off, len);
      }
   }


  /*
  ** Keep used, but available connections here. This is a very simplistic,
  ** implementation ... serial use (no request stacking). 
  */ 
  
  // This version is used to invalidate sockets from being added to keepalive
  //  pool. Primary reason is if connectivity options change, don't want thos
  //  old sockets kept
   static int       keepAliveVersion = 1;
   
   static Hashtable keepAliveConns = new Hashtable();
   static boolean   doKeepAlive = true;
   
  // keepAlive version for URLC2 instance
   private int kaVer = 1;
   
   private Socket sock   = null;
   private Hashtable reqProps = new Hashtable();
   private Hashtable header = new Hashtable();
   private Vector    headerKeys = new Vector();
   private Vector    headerVals = new Vector();
   private int sendContentLength = -1;
   private boolean   shouldSendContentLengthV = true;
   private String connString = null;
   private String responseHeader = null;
   private String httpver = null;
   private String statusString = null;
   private int    statusInt = 504;
   private boolean closed = false;
   private boolean draining = false;
   
  /*
   private boolean usesocks  = true;
  */
  
   private int curbyte  = 0;
   private int curchunkbytes = 0;
  
   private boolean chunked = false;
   private OutputStream outStream = null;
   private InputStream  inpStream = null;
   
   private boolean keepalive = true;
   private long    keepAliveExpiration = 0;
   private boolean usingHTTPProxy = false;
   private boolean usingHTTPS = false;
   
   private int port            = -1;
   
   private String proto = null;
   
   private boolean gotKeepAlive = false;
   
   private boolean headRead    = false;
   private boolean headWritten = false;
   byte crlf[] = new byte[2];
   
   static protected int rwin = -1;
   static protected int swin = -1;
   static protected java.lang.reflect.Method recvmeth = null;
   static protected java.lang.reflect.Method sendmeth = null;
   
  /*
   static public  boolean forceChecked = false;
   static public  boolean forceJSkit = false;
  */
   
  /* 
  ** JMC 3/1/06 - SSLight usage terminated ... commented out
   static private String keyringClassName = "EDesignKeyring"; 
   static private String keyringpw        = "0x120163";
   private static com.ibm.sslight.SSLContext myctx = new com.ibm.sslight.SSLContext();
   private static com.ibm.sslight.SSLightKeyRing ring  = null;
   
   static public void setKeyringClassName(String s) {
      keyringClassName = s;
      if (ring != null) {
         ring          = null;
         myctx         = new com.ibm.sslight.SSLContext();
      }
   }
   static public void setKeyringPassword(String s) {
      keyringpw = s;
      if (ring != null) {
         ring   = null;
         myctx  = new com.ibm.sslight.SSLContext();
      }
   }
   
   static public com.ibm.sslight.SSLContext getContext() { return myctx; }
   static public void validateKeyring() throws Exception {
      
      if (ring == null) {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3, 
                               "URLConnection2: Loading Keyring");
         }
         ring = (com.ibm.sslight.SSLightKeyRing) 
            Class.forName(keyringClassName).newInstance();
            
         String keydata = ring.getKeyRingData();
         myctx.importKeyRings(keydata, keyringpw);
         
         if (DebugPrint.doDebug()) {
            myctx.debug = true;
            String s[] = myctx.getEnabledCipherSuites();
            
            DebugPrint.println(DebugPrint.DEBUG, 
                               "--- Enabled Cipher Suites ---");
            for(int i=0; i < s.length; i++) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "   [" + (i+1) + "]: " + s[i]);
            }
         }
      }
   }
   
  // Uses jskit/sslight
   public static Socket sslizeSocketJskit(Socket sock, boolean server) 
      throws IOException {
      
      try {
         validateKeyring();
      } catch (Exception e) {
         throw new IOException("sslizeSocket failed in validate keyring");
      }
      setWindowSizes(sock, true);
      sock.setTcpNoDelay(true);
      
      
      sock = new com.ibm.sslight.SSLSocket(sock, false, myctx, 
                                           server? com.ibm.sslight.SSLSocket.SERVER
                                                 : com.ibm.sslight.SSLSocket.CLIENT, 
                                           null);
                           
      sock.setTcpNoDelay(true);
      return sock;
   }
  */   
   
   public static Socket sslizeSocket(Socket sock) throws IOException {
      return sslizeSocket(sock, false);
   }
   
  // Uses jsse
   public static Socket sslizeSocket(Socket sock, boolean server) throws IOException {
      
     // if we don't support anonymous JSSE, use jskit stuff
     /* Actually ... we no longer support OLD OLD OLD Jskit
      if (!forceChecked) {
        // JMC 3/1/06 -  synchronized(myctx) {
         if (!forceChecked) {
            forceJSkit = false;
            forceChecked = true;
            try {
               String v = System.getProperty("URLConnection2.forceJSkit", 
                                             "false");
               forceJSkit = v.equalsIgnoreCase("true");
            } catch(Throwable tt) {}
         }
      }
     */
     
      if (/*forceJSkit ||*/ !SSLizer.supportsAnonymousJSSE()) {
         throw new IOException("JSSE Not found, and JSKIT no longer supported");
        //return sslizeSocketJskit(sock, server);
      }
      
      setWindowSizes(sock, true);
      sock.setTcpNoDelay(true);
      
     /*
       try {
         javax.net.ssl.SSLSocketFactory sf = (javax.net.ssl.SSLSocketFactory)
            javax.net.ssl.SSLSocketFactory.getDefault();
      
         sock = sf.createSocket(sock, sock.getInetAddress().getHostAddress(), 
                                sock.getPort(), true);
      } catch(javax.net.ssl.SSLException ssle) {
         throw new IOException("SSL Error occured: " + ssle.getMessage());
      }
     */
     
      try {
         sock = SSLizer.sslizeSocketAnonymous(sock, server);
      } catch(SSLStartupException sse) {
         throw new IOException("SSL Error occured: " + sse.getMessage());
      }
                           
      sock.setTcpNoDelay(true);
      return sock;
   }

  /*
   public static Socket createSSLSocket(String host, int port) throws IOException {
   
      X509TrustManager myTM[] = MyX509TrustManager.getTM();
   
      SSLContext ctx = null;
      try {
         ctx = SSLContext.getInstance("SSL");
      } catch(Exception e1) {
         try {
            ctx = SSLContext.getInstance("SSL3");
         } catch(Exception e2) {
            try {
               ctx = SSLContext.getInstance("TLS");
            } catch(Exception e3) {
               System.out.println("Zoinks: No protos!");
            }
         }
      }
      
      try {
         ctx.init(null, myTM, null);
      } catch(Exception e4) {
         e4.printStackTrace(System.out);
      }
   
      SSLSocket sock = null;
      try {
         javax.net.ssl.SSLSocketFactory sf = 
            (javax.net.ssl.SSLSocketFactory)ctx.getSocketFactory();
            
         //SSLSocketFactory sf = (SSLSocketFactory)SSLSocketFactory.getDefault();
      
         sock = (SSLSocket)sf.createSocket(host, port);
         
         sock.startHandshake();
         
      } catch(javax.net.ssl.SSLException ssle) {
         ssle.printStackTrace(System.out);
         throw new IOException("SSL Error occured: " + ssle.getMessage());
      }
      
      setWindowSizes(sock, true);
      sock.setTcpNoDelay(true);
      return sock;
   }
   */
   
   static public int  getOverrideReceiveWindow() { 
      synchronized(keepAliveConns) {  
         return rwin; 
      }
   }
   static public int  getOverrideSendWindow() { 
      synchronized(keepAliveConns) { 
         return swin; 
      }
   }
    
   static public void setOverrideReceiveWindow(int w) { 
      synchronized(keepAliveConns) { 
         rwin = w;    
      }
   }
   static public void setOverrideSendWindow(int w)    { 
      synchronized(keepAliveConns) { 
         swin = w;    
      }
   }
   
   static public void setWindowSizes(Socket s, boolean complain) {
      synchronized(keepAliveConns) { 
         java.lang.reflect.Method dbgmeth = null;
         Integer starting_v = null;
         Integer ending_v   = null;
         
         if (rwin > -1) {
            try {
               if (recvmeth == null) {
                  Class classparms[] = new Class[1];
                  classparms[0] = int.class;
                  recvmeth = s.getClass().getMethod("setReceiveBufferSize", 
                                                    classparms);
               }
               
               if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                  dbgmeth = s.getClass().getMethod("getReceiveBufferSize", 
                                                   null);
                  starting_v = (Integer)dbgmeth.invoke(s, null);
               }
               
               Object methparms[] = new Object[1];
               methparms[0] = new Integer(rwin);
               recvmeth.invoke(s, methparms);
               
               if (dbgmeth != null) {
                  ending_v = (Integer)dbgmeth.invoke(s, null);
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "SetWindowSizes: RWIN(" + rwin + ")" +
                                     " preval=" + starting_v + 
                                     " postval=" + ending_v);
               }
               
            } catch(Throwable tt) {
               if (complain) {
                  DebugPrint.printlnd(DebugPrint.DEBUG , 
                                      "URL2: setWindowSizes: RWin adjustments not possible");
                  if (DebugPrint.getLevel() >= DebugPrint.DEBUG ) {
                     tt.printStackTrace(System.out);
                  }
               }
              // If the method does not exist ... stop trying
               if (tt instanceof NoSuchMethodException) {
                  rwin = -1;
               }
            }
         }
         if (swin > -1) {
            try {
               dbgmeth = null;
               if (sendmeth == null) {
                  Class classparms[] = new Class[1];
                  classparms[0] = int.class;
                  sendmeth = s.getClass().getMethod("setSendBufferSize", 
                                                    classparms);
               }
               
               if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                  dbgmeth = s.getClass().getMethod("getSendBufferSize", 
                                                   null);
                  starting_v = (Integer)dbgmeth.invoke(s, null);
               }
               
               Object methparms[] = new Object[1];
               methparms[0] = new Integer(swin);
               sendmeth.invoke(s, methparms);
               
               if (dbgmeth != null) {
                  ending_v = (Integer)dbgmeth.invoke(s, null);
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "SetWindowSizes: SWIN(" + swin + ")" +
                                     " preval=" + starting_v + 
                                     " postval=" + ending_v);
               }
               
            } catch(Throwable tt) {
               if (complain) {
                  DebugPrint.printlnd(DebugPrint.DEBUG, 
                                      "URL2: setWindowSizes: SWin adjustments not possible");
                  if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                     tt.printStackTrace(System.out);
                  }
               }
               if (tt instanceof NoSuchMethodException) {
                  swin = -1;
               }
            }
         }
      }
   }
   
   public String getRequestProperty(String k) {
      return (String)reqProps.get(k);
   }
   
   public void setKeepAlive(boolean v) {
      keepalive = v;
   }
   
   public void setRequestProperty(String k, String v) {
      reqProps.put(k, v);
   }
      
   public void shouldSendContentLength(boolean v) {
      shouldSendContentLengthV = v;
   }
   public void setSendContentLength(int len) {
      shouldSendContentLength(true);
      sendContentLength = len;
   }
   public int getSendContentLength() {
      return sendContentLength;
   }
   
   public URLConnection2(URL u, boolean usesocksIn) {
      super(u);
     //usesocks = usesocksIn;
      crlf[0] = 13;
      crlf[1] = 10;
   }
   
   public URLConnection2(URL u) {
      super(u);
     //usesocks = true;
      crlf[0] = 13;
      crlf[1] = 10;
   }
   
   public void setProtocol(String inproto) {
      proto = inproto;
   }
   
   public boolean usedKeepAlive() {
      return gotKeepAlive;
   }
   
   public static void useKeepAlive(boolean b) {
      doKeepAlive = b;
   }
   
   protected void addKeepAlive(String connString, 
                               Socket sock, int ver) {
      synchronized(keepAliveConns) { 
         if (doKeepAlive && keepalive) {
            if (ver == keepAliveVersion) {
               Vector vec = (Vector)keepAliveConns.get(connString);
               if (vec == null) {
                  vec = new Vector();
                  keepAliveConns.put(connString, vec);
               }
               vec.addElement(new KeepAliveMarker(sock, keepAliveExpiration));
            } else {
               DebugPrint.printlnd(DebugPrint.INFO4, 
                                   "URLC2: socket rejected for keepalive. " +
                                   " KeepAliveVer = " + keepAliveVersion + 
                                   " socketver = " + ver);
            }
         }
      }
   }
   
   protected Socket getKeepAlive(String connString) {
      Socket sock = null;
      synchronized(keepAliveConns) { 
         if (doKeepAlive && keepalive) {
            Vector vec = (Vector)keepAliveConns.get(connString);
            if (vec != null) {
               int idx;
               long curtime = System.currentTimeMillis();
               while((idx=vec.size()) > 0) {
                  idx--;
                  KeepAliveMarker m = (KeepAliveMarker)vec.elementAt(idx);
                  vec.removeElementAt(idx);
                     
                  long mExpire = m.getExpiration();
                  if (mExpire <= 0 || mExpire > curtime) {
                     if (mExpire == 0) {
                        DebugPrint.printlnd(DebugPrint.DEBUG, 
                                          "URLC2: Have mExpire of 0. Take it");
                     } else {
                        DebugPrint.printlnd(DebugPrint.DEBUG4, 
                                          "URLC2: keepalive expires in (ms): " 
                                            + (mExpire-curtime));
                     }
                     
                     sock = m.getSocket();
                     break;
                  } else {
                     DebugPrint.printlnd(DebugPrint.DEBUG, 
                                         "URLC2: found expired keepalive ... skip it (ms): " + (mExpire-curtime));
                  }
               }
            } 
         }
      }
      return sock;
   }
   
   public static int purgeKeepAlives() {
      int numpurged = 0;
      Enumeration vals = null;
      synchronized(keepAliveConns) { 
      
        // Increase version number ... Old version sockets will not be added to
        //  keep alive pool
         keepAliveVersion++;
         
         vals = keepAliveConns.elements();
         
        // Can't just clear keepAliveConns with vals enum to be used later. 
        //  Believe clearing will invalidate cursor.
         Vector v = new Vector();
         while(vals.hasMoreElements()) {
            v.addElement(vals.nextElement());
         }
         vals = v.elements();
         
         keepAliveConns.clear();
      }
      
      if (vals != null) {
         while(vals.hasMoreElements()) {
            Enumeration se = ((Vector)vals.nextElement()).elements();
            while(se.hasMoreElements()) {
               numpurged++;
               try {
                  KeepAliveMarker m = (KeepAliveMarker)se.nextElement();
                  Socket s = m.getSocket();
                  s.close();
               } catch(Throwable gen) {
               }
            }
         }
      }
      return numpurged;
   }
      
   public void connect() throws IOException {
   
      if (proto == null) {
         proto = url.getProtocol();
      }
      
      usingHTTPS = proto.equalsIgnoreCase("https"); 
      
      port = 80;
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, 
                            "URLC2: CONNECT = " + url.toString());
      }
      
      if (connString != null) {
         throw new IOException("Only use this object ONCE!");
      }
      
      try {
         Class.forName("netscape.security.PrivilegeManager");
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "Netscape PrivMgr is FOUND");
         }
         
         PrivilegeManager.enablePrivilege("UniversalConnect");
      } catch (Exception e) {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "Netscape PrivMgr NOT found or NOT granted");
         }
      }

      
     // This is BS ... should not be hard coded even if we ARE using socks
     /*
      if (usesocks) {
         if (proto.equalsIgnoreCase("https")) {
            Proxy.setDefaultProxy("socksa.btv.ibm.com", 1080, "");
         } else {
            Proxy.setDefaultProxy("socks1.server.ibm.com", 1080, "");
         }
      }
     */
      
      try {
         Class.forName("com.ms.security.PermissionID");
         Class.forName("com.ms.security.PolicyEngine");
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
                               "Explorer PrivInfo is FOUND");
         }
         
         PolicyEngine.assertPermission(PermissionID.NETIO);
      } catch (Exception e) {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5, 
               "Explorer PrivInfo is NOT found or NOT granted");
         }
      }
      
      if ((port = url.getPort()) == -1) {
         if (usingHTTPS) {
            port = 443;
         } else if (proto.equalsIgnoreCase("http")) {
            port = 80;
         } else {
            port = -1;
         }
      }
      
      connString = proto + "://" + url.getHost() + ":" + port;
      
     // Try getting a keepalive socket. If we do find one, try to access the
     //  output stream of the socket. If it fails, try getting another.
      sock = getKeepAlive(connString);
      while (sock != null) {
         try {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "URLConn2: Found Keepalive socket " + 
                                  connString + ": " + sock);
            }
            
            setWindowSizes(sock, false);
            sock.getOutputStream();
            gotKeepAlive = true;
            kaVer = keepAliveVersion;
            break;
         } catch(IOException kaoscheck) {
            sock = null;
         }
         sock = getKeepAlive(connString);
      }
      
      String ph = System.getProperty("proxyHost");
      String pp = System.getProperty("proxyPort");
      int pport = -1;
      if (ph != null && pp != null) {
         usingHTTPProxy = true;
         try { 
            pport = Integer.parseInt(pp);
         } catch(NumberFormatException ne) {
            DebugPrint.printlnd(DebugPrint.ERROR,
                               "Asked to use HTTP Proxy, but port = " + pp);
         }
      }      
      
      if (sock == null) {
         if (usingHTTPS) {
            try {
            
              // JMC 3/1/06 - Comment out myctx and validate
              //synchronized(myctx) {
              // validateKeyring();
               
              /*if (usesocks) {
                sock = new SocksSocket(url.getHost(), port);
                } else {
              */
               
               if (usingHTTPProxy) {
                  sock = new Socket(ph, pport);
               } else {
                  sock = new Socket(url.getHost(), port);
               }
               
               setWindowSizes(sock, true);
               
               sock.setTcpNoDelay(true);
               
               if (usingHTTPProxy) {
                  httpProxyHandshake();
               }
               
              //sock = new SSLSocket(sock, false, myctx, SSLSocket.CLIENT, null);
               sock = sslizeSocket(sock, false);
               
               sock.setTcpNoDelay(true);
              // }
               
              //  sock = new SSLSocket(url.getHost(), port,
              //                       myctx, SSLSocket.CLIENT, null);
               
            } catch (IOException e) {
               String h = url.getHost();
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "Error while SSLSocket to host [" 
                                  + h + "] port[" + port + "] OR badProxyHost");
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "URL in error = " + url.toString());
               try {sock.close();} catch(Throwable tt) {}
               throw e;
            } catch (Exception e) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "KeyData not found, No SSL");
               DebugPrint.println(DebugPrint.DEBUG, 
                                  e);
               throw new IOException("Error from SSL connect: " + e.toString());
            }
         } else if (proto.equalsIgnoreCase("http")) {
            {
              /* if (usesocks) {
                 sock = new SocksSocket(url.getHost(), port);
                 } else {
              */
               if (usingHTTPProxy) {
                  sock = new Socket(ph, pport);
               } else {
                  sock = new Socket(url.getHost(), port);
               }
               setWindowSizes(sock, true);
               sock.setTcpNoDelay(true);
            }
//         System.out.println("Hookup to [" + url.getHost() + "] port = " + port);
         } else {
            throw new IOException("Unsupported Prototcol from URLConn2! " + proto);
         }
         
         sock.setSoLinger(true, 10);
         kaVer = keepAliveVersion;
         
      }
      
      try {
         writeHeaderInfo();
         if (!getDoOutput() || (!shouldSendContentLengthV || 
                                sendContentLength >= 0)) {
            finishHeaderInfo(sendContentLength);
         }
         
         if (!getDoOutput()) {
            readHeaderInfo();
         }
      } catch(IOException ee) {
         try {sock.close();} catch(Throwable tt) {}
         sock = null;
         throw ee;
      }
      
      connected    = true;
      
     // If we should not send content len, then we can't reuse this connection
      if (!shouldSendContentLengthV) {
         setKeepAlive(false);
      }
      
   }
   
   private void httpProxyHandshake() throws IOException {
   
      OutputStream out = new BufferedOutputStream(sock.getOutputStream());
      StringBuffer lines = new StringBuffer(512).append("CONNECT ").
         append(url.getHost()).append(":"+port).append(" HTTP/1.1\r\n");

      String auth = 
         URLConnection.getDefaultRequestProperty("Proxy-Authorization");
      if (auth == null) {
         auth=System.getProperty("Proxy-Authorization");
      }
      
      if (auth == null) {
         auth=System.getProperty("proxyAuth");
         if (auth != null) {
            auth = "Basic " + auth;
         }
      }
     
      if (auth != null) {
         lines.append("Proxy-Authorization: ").append(auth).append("\r\n");
      }
      String useragent = URLConnection.getDefaultRequestProperty("User-Agent");
      if (useragent == null) {
         useragent = "J_J_MoJo_Processing_Agent";
      }
      lines.append("Host: ").append(url.getHost()).append(":").append(port).append("\r\n");
      lines.append("User-Agent: ").append(useragent).append("\r\n");
         
      lines.append("Connection: ").append(keepalive?"Keep-Alive":"Close").append("\r\n\r\n");
         
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, "Doing: " + lines.toString());
      }
      
      byte[] b=asciiToBytes(lines.toString());
      out.write(b,0,b.length);
      out.flush();
      
      b = new byte[1024];
      InputStream in = sock.getInputStream();
      int r, t=0;
      int lastsrch = 0;
      boolean connOk = false;
      byte lfbyte = (byte)'\n';
      byte crbyte = (byte)'\r';
      while(t < 1024) {
      
         r = in.read(b, t, 1024-t);
         
         
         if (r > 0) {
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "httpPrxHand Read: " + 
                                  (new String(b, t, r)).toString());
            }
            t += r;
         } else {
            throw new IOException("Error doing httpProxyHandshake");
         }
         
         if (!connOk) {
            int clf=0;
            int i;
            for(i=0; i < t; i++) {
               byte cbyte = (clf != 0) ? lfbyte : crbyte;
               if (b[i] == cbyte) {
                  if (++clf == 2) {
                     if (DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.DEBUG3,
                                           "httpPrxHand: Found end, \\r\\n. Chkval!");
                     }
                     for(int j=0; j < i; j++) {
                        if (b[j] == (byte)' ') {
                           for(; j < i; j++) {
                              if (b[j] != ' ') {
                                 if (j+3 < i       &&
                                     b[j]   == '2' &&
                                     b[j+1] == '0' &&
                                     b[j+2] == '0' &&
                                     b[j+3] == ' ') {
                                    connOk = true;
                                    DebugPrint.println(DebugPrint.DEBUG3,
                                                       "httpPrxHand: 200 found.");
                                 }
                              }
                           }
                        }
                     }
                     
                     if (!connOk) {
                        throw new IOException("Error from HTTP Proxy");
                     }
                     
                     if (t == 1024) {
                        i++;
                        t = t-i;
                        System.arraycopy(b, i, b, 0, t);
                     }
                     
                     
                     break;
                  }
               } else {
                  clf=0;
               }
            }
         }
                  
        // If we already have Connection OK, then we skip the rest and
        //  look for \r\n\r\n, which WILL be the last bytes coming, as 
        //  we as the client will start the SSL handshake
         if (connOk) {
            if (t >= 4) {
               if (b[t-4] == crbyte && b[t-3] == lfbyte &&
                   b[t-2] == crbyte && b[t-1] == lfbyte) {
                  DebugPrint.println(DebugPrint.DEBUG3,
                                     "httpPrxHand: Found end, Return!");
                  return;
               }
               if (t > 512) {
                  System.arraycopy(b, t-3, b, 0, 3);
                  t = 3;
               }
            }
         }

      }
   }
   
   private void writeHeaderInfo() throws IOException {
   
      OutputStream out = sock.getOutputStream();
      StringBuffer lines = 
         new StringBuffer(512).append(getDoOutput()?"POST ":"GET ");
         
      if (!usingHTTPS && usingHTTPProxy) {
         lines.append(connString);
      }
      lines.append(url.getFile()).append(" HTTP/1.1\r\n");

      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, "Doing: " + lines.toString());
      }
      
      lines.append("Accept: application/octet-stream,*/*\r\n");
         
      String useragent = URLConnection.getDefaultRequestProperty("User-Agent");
      if (useragent == null) {
         useragent = "J_J_MoJo_Processing_Agent";
      }
      lines.append("User-Agent: ").append(useragent).append("\r\n");
         
      lines.append("Connection: ").append(keepalive?"Keep-Alive":"Close").append("\r\n");
         
     // Put out any and all reqProps
      for (Enumeration e = reqProps.keys() ; e.hasMoreElements() ;) {
         String k = (String)e.nextElement();
         lines.append(k).append(": ").append((String)reqProps.get(k)).append("\r\n");	 
      }
         
      lines.append("Host: ").append(url.getHost()).append(":").append(port).append("\r\n");
      
      byte[] b=asciiToBytes(lines.toString());
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, "URLC2: OutputHeaders:\n" + 
                            lines);
      }
      
      out.write(b,0,b.length);
      out.flush();
   }
      
   private void finishHeaderInfo(int len) throws IOException {
   
      OutputStream out = sock.getOutputStream();
      
      if (headWritten) return;
      headWritten = true;
         
      if (!getDoOutput()) {
         len = 0;
      }
      
      if (shouldSendContentLengthV) {
         out.write(asciiToBytes("Content-length: " + len));
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3, "Content-length: " + len);
         }
      } else {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3, "No content len sent. Told not to");
         }
      }
      
      out.write(crlf);
      out.write(crlf);
      out.flush();
   }
      
   private void readHeaderInfo() throws IOException {
   
      if (headRead) return;
      headRead = true;
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, "Getting input stream ... ");
      }
      
      InputStream in = sock.getInputStream();
      
      byte b[] = new byte[32768];
      int i = 0;
      int linenum = 0;
      int lastWasLen = -1;
      
      int dlev = DebugPrint.getLevel();
      
      while (true) {
         int l = in.read(b, i, 1);
         
         if (dlev >= 255) {
            DebugPrint.println(255, 
                               "Header Byte Read i=" + i + 
                               " byte=" + b[i] + " l=" + l);
         }
         
         if (l == 1) {
            if (i >= 1) {
               if (b[i-1] == crlf[0] && b[i] == crlf[1]) {
               
                  if (dlev >= 255) {
                     DebugPrint.println(255, 
                                        "Got crlf, i=" +i + 
                                        " lastWasLen=" + lastWasLen);
                  }
                 
                  if (i == 1) {
                     break;
                  }
                  lastWasLen = i-1;
                  i=-1;
                  String s = new String(b, 0, lastWasLen);
                  if (linenum > 0) {
                     int idx = s.indexOf(':');
                     if (idx > 0) {
                        String k = s.substring(0, idx);
                        String v = s.substring(idx+2);
                        if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG3,
                                              "Header key value => [" + 
                                              k + "] [" + v + "]");
                        }
                        
                        String lowk = k.toLowerCase();
                        header.put(lowk, v);
                        headerKeys.addElement(lowk);
                        headerVals.addElement(v);
                     }
                  } else {
                     responseHeader = s;
                  }
                  linenum++;
               }
            }
            i++;
         } else if (l == 0) {
            ;
         } else {
            System.out.println("Breaking ... i = " + i + " l = " + l);
            sock.close();
            setKeepAlive(false);
            sock = null;
            throw new IOException("Error before retrieving entire header");
         }
      }
      
      if (responseHeader == null) {
         throw new IOException("No Header line");
      }
      
      int idx  = responseHeader.indexOf("HTTP/");
      int idx2 = responseHeader.indexOf(" ");
      if (idx < 0 || idx2 < 0) {
         throw new IOException("Unknown HTTP Response: " + responseHeader);
      }
      
      httpver=responseHeader.substring(idx+5, idx2);
      
      statusString = responseHeader.substring(idx2+1);
      int idx3 = statusString.indexOf(" ");
      if (idx3 < 0) {
         throw new IOException("Unknown HTTP Status: " + responseHeader);
      }
      
      statusString = statusString.substring(0, idx3);
      try {
         statusInt = Integer.parseInt(statusString);
      } catch(NumberFormatException e) {
         throw new IOException("Unknown HTTP Status: " + responseHeader);
      }
      
     // Do better parseing here ... for 1.2 and beyond!
      if (!httpver.equals("1.1") && !httpver.equals("1.0")) {
         throw new IOException("Unknown HTTP Protocol version: " 
                               + responseHeader);
      }

     // If connection is to be terminated, don't save it
      String tc = (String)header.get("connection");
      if (tc != null && tc.trim().equalsIgnoreCase("close")) {
         setKeepAlive(false);
      }
      
     // If connection is to be terminated, don't save it
      tc = (String)header.get("keep-alive");
      if (tc != null) {
         tc = tc.toLowerCase();
         int toidx = tc.indexOf("timeout=");
         if (toidx >=0) {
            int commaidx = tc.indexOf(",", toidx);
            if (commaidx > 0) {
               tc = tc.substring(toidx+8, commaidx);  
            } else {
               tc = tc.substring(toidx+8);
            }
            try {
               int toval = Integer.parseInt(tc.trim());
               keepAliveExpiration = System.currentTimeMillis() + toval*1000;
               DebugPrint.println(DebugPrint.DEBUG2, "KeepAlive timeout is " +
                                  toval + " secs in future");
            } catch(NumberFormatException nfe) {
               DebugPrint.println(DebugPrint.DEBUG2, 
                                  "Error parsing Keepalive timeout string: " +
                                  tc);
            }
         } else {
            DebugPrint.println(DebugPrint.DEBUG4, 
                               "URLC2: No keep-alive value found");
         }
      }
      
      curbyte = 0;
      curchunkbytes = -1;
      
      String s = (String)header.get("content-length");
      if (s != null) {
         try {
            curchunkbytes = Integer.parseInt(s);
         } catch(NumberFormatException e) {
            ;
         }
      }
         
     // If chunked, read hex chunk size
      tc = (String)header.get("transfer-encoding");
      if (tc != null && tc.indexOf("chunked") >= 0) {
         chunked = true;
         
        // This Chunking happens during READ only
        // readChunkId(null);
        // if (curchunkbytes == 0) disconnect();
        
      } else if (curchunkbytes <= 0) {
        // If we are not chunked, and we have no data to download, just exit
         disconnect();
      } else if (!getDoInput()) {
         disconnect();
      }
   }
   
   public int getStatusInt() {
      return statusInt;
   }
   
   public int getContentLength() {
      int ret = -1;
      
      String s = getHeaderField("content-length");
      try {
         ret = Integer.parseInt(s);
      } catch(NumberFormatException e) {
         ;
      }
      
      return ret;
   }
   
   public String getContentType() {
      return getHeaderField("content-type");
   }
   
   public InputStream getInputStream() throws IOException {
      if (!closed && !connected) connect();
      
     // Instead of below check, close the output stream if they access input stream
      getOutputStream().close();
      
     //if (!headWritten) throw new IOException("Must write data before accessing InputStream!");
      
      readHeaderInfo();
      
      if (inpStream == null) {
         inpStream = new URLInputStream(this);
      }
      
      return inpStream;
   }
   
      
  // Look into InformOutputStream and Chunking in the UP direction ... ???
   public OutputStream getOutputStream() throws IOException {
      if (!closed && !connected) connect();
      if (outStream == null) {
         if (getDoOutput() && (!shouldSendContentLengthV ||
                               sendContentLength >= 0)) {
            outStream = new MyFilterOutputStream(sock.getOutputStream(), this);
         } else {
            outStream = new InformOutputStream(this);
         }
      }
      return outStream;
   }
   
   
   public void inform(InformOutputStream iout) throws IOException {
      if (!headWritten) {
         iout.flush();
         byte arr[] = iout.toByteArray();
         iout.realClose();
         
         finishHeaderInfo(arr.length);
         if (getDoOutput()) {
            OutputStream out = sock.getOutputStream();
            out.write(arr);
            out.flush();
         }
      }
   }
   
   protected void writeclose() throws IOException {
      readHeaderInfo();
   }
   
   protected void readTrailer(byte b[]) throws IOException {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "URLC2: readTrailer: " + toString());
      }
      
      if (b == null) b = new byte[1024];
      
      int i = 0;
      InputStream in = sock.getInputStream();
      
      while (true) {
         int l = in.read(b, i, 1);
         
         if (l == 1) {
            if (i >= 1) {
               if (b[i-1] == crlf[0] && b[i] == crlf[1]) {
               
                  if (i == 1) return;
                  i = -1;
               } else {
                  b[0] = 0;
                  b[1] = b[i];
                  i=1;
               }
            }
            i++;
         } else if (l < 0) {
            curchunkbytes = curbyte = 0;
            DebugPrint.println(DebugPrint.DEBUG3, 
                               "URL2:readTrailer Keepalive off, l < 0");
            setKeepAlive(false);
            break;
         }
      }
   }
   
   protected void readChunkId(byte b[]) {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "URLC2: readChunkId: " + toString());
      }
      
      if (b == null) b = new byte[1024];
      
      int i = 0;
      try {
      
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3,
                               "   URLC2: readChunkId: before getstream");
         }
         
         InputStream in = sock.getInputStream();
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3,
                               "   URLC2: readChunkId: after getstream");
         }
         
         while (true) {
         
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3,
                                  "   URLC2: readChunkId: before read");
            }
            
            int l = in.read(b, i, 1);
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3,
                                  "   URLC2: readChunkId: after read");
            }
            
            if (l == 1) {
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG3,
                                     "   URLC2: cur = " + 
                                     (new String(b, 0, i+1)));
               }
               
               if (i >= 1) {
                  if (b[i-1] == crlf[0] && b[i] == crlf[1]) {
                     if (i == 1) {
                     
                        if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG3,
                              "   URLC2: readChunkId: strip crlf");
                        }
                        
                        i=0;
                        continue;
                     }
                     int lastWasLen = i-1;
                     i=-1;
                     String curchunkS = new String(b, 0, lastWasLen);
                     try {
                        curchunkbytes = Integer.parseInt(curchunkS.trim(), 16);
                        curbyte       = 0;
                        if (curchunkbytes == 0) {
                           readTrailer(b);
                        }
                     } catch(NumberFormatException e) {
                     
                        if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG3,
                                              "   URLC2: readChunkId: badcsz=["
                                              + curchunkS + "] len=" + 
                                              curchunkS.length());
                        }
                        
                        throw new IOException("Bad chunk size : " + curchunkS);
                     }
                     break;
                  }
               }
               i++;
            } else if (l < 0) {
               curchunkbytes = curbyte = 0;
               setKeepAlive(false);
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "URL2:readChunkId Keepalive off, l < 0");
               break;
            }
         }
      } catch(IOException e) {
         curchunkbytes = curbyte = 0;
         setKeepAlive(false);
         
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG3,
                               "   URLC2: readChunkId: in IOX handler");
         }
      }
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "    readId: new chunk = " + curchunkbytes); 
      }
   }
   
   public void disconnect() throws IOException {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "URLC2: disconnect: " + toString());
      }
      
      if (sock != null) {
         if (keepalive && !draining) {
           // Drain rest of body (ifany)
            
           // Note: read will reenter this routine and finish this
           //       code, So if sock == null, just return
            byte buf[] = new byte[1024];
            draining = true;
            while (read(buf, 0, 1024) != -1);
            
           // Have to set close AFTER read, or won't drain
            closed = true;
      
            if (sock != null) {
            
              // At this point, if we haven't gotten an IOException, the
              // connection is up for reuse. Add it
               addKeepAlive(connString, sock, kaVer);
               
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG3,
                                     "URLConn2: Saving socket to " + 
                                     connString);
               }
            }
         } else {
            sock.close();
         }
         sock = null;
         setKeepAlive(false);
         curbyte = curchunkbytes = 0;
      } else {
         closed = true;
        //   throw new IOException("Not connected");
      }
   }
   
  //
  // Methods used by URLInputStream
  //
   protected int read(byte b[], int off, int len) throws IOException {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "URLC2: read(b[" + b.length + "], " 
                            + off + ", " + len + ") curchunkbyte=" +  
                            curchunkbytes + " curbytes=" + curbyte);
      }
   
      if (closed) return -1;
      
      if (!connected) {
      
         if (draining) return -1;
         
         throw new IOException("URL2: Read: Never Connected");
      }
      
     // JMC 2/19/03 --- UGG, I was blocking by always wanting a current 
     //                 chunkid, and so was reading the next chunk after 
     //                 current chunk was finished, but before I returned 
     //                 that data. I need to read the chunkid BEFORE I try 
     //                 to read, if necessary
     
     // If we have a negative curchunkbytes value, 
     // we need to read next chunkid. This will set curchunkbytes.
      if (chunked && curchunkbytes < 0) {
         readChunkId(null);
      }
            
     // If we have some chunk data, calculate the max len to read
      if (curchunkbytes > 0) {
         int curchunkleft = curchunkbytes - curbyte;
         if (curchunkleft <= 0) {
         
            if (chunked) {
               DebugPrint.printlnd(DebugPrint.ERROR, "URL2: ERR! curchunkbytes = " + 
                                   curchunkbytes + " curbyte = " + curbyte + "!!!");
               setKeepAlive(false);
               if (!draining) disconnect();
            }
            return -1;
         }
         
         if (curchunkleft < len) len = curchunkleft;
      }
      
      if (curchunkbytes == 0) {
         if (!draining) disconnect();
         return -1;
      }
      
                         
      InputStream is = sock.getInputStream();
      int ret = is.read(b, off, len);
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3,
                            "URLC2: read limit len = " + 
                            len + " Act = " + ret
                            + " curchunkbyte=" +  
                            curchunkbytes + " curbytes=" + curbyte);
         if (DebugPrint.getLevel() >= 255) {
            try {
               
               DebugPrint.println(255, "OUTPUT Start ===>");
               DebugPrint.println(255, DebugPrint.showbytes(b, off, ret));
               DebugPrint.println(255, "\n<=== OUTPUT end");
            } catch(Throwable tt) {
               ;
            }
         }
      }
         
      if (curchunkbytes > 0) {
         if (ret >= 0) {
            curbyte += ret;
            
           // If we are at a chunk end
            if (curbyte >= curchunkbytes) {
            
              // If we are NOT chunked, then we finished!
               if (!chunked) {
                  disconnect();
               } else {
               
                 // Flag we need to read next chunk
                  curchunkbytes = -1;
                  curbyte = 0;
               }
            }
         } else {
            if (!draining) disconnect();
         }
      } else {
         if (ret < 0) {
            curchunkbytes = 0;
         } else {
            curbyte += ret;
         }
      }
      
      return ret;
   }
   
   protected int available() throws IOException {
      if (!connected || sock == null) {
         throw new IOException();
      }
      
      int ret = sock.getInputStream().available();
      if (curchunkbytes >= 0) {
         int curchunkleft = curchunkbytes - curbyte;
         if (ret > curchunkleft) {
            ret = curchunkleft;
         }
      }
      return ret;
   }
   
   protected void close() throws IOException {
   
      if (!connected) 
         throw new IOException("Never connected!");
         
      if (sock != null) {
         disconnect();
      } else {
         closed = true;
      }
   }

   public void finalize() {
      try {
         close();
      } catch(IOException e) {
      }
   }
   
   public String toString() {
      String s = "URLC2: Hd[" + responseHeader 
            + "] chunked=" + chunked 
            + " keepalive=" + keepalive
            + " curcb [" + curchunkbytes
            + "] curbyte [" + curbyte + "]";
            
      return s;
   }

   public static byte[] asciiToBytes(String buf) {
      int size = buf.length();
      byte[] bytebuf = new byte[size];

      char[] charBuff= new char[size];
      buf.getChars(0,size,charBuff,0); // copy char[]
       
      for (int i = 0; i < size; i++) {
         //bytebuf[i] = (byte)buf.charAt(i);
         bytebuf[i] = (byte) charBuff[i];	    
      }
      return bytebuf;
   }
   
   
// 9/6/01 - Fill it out   
   public Object getContent() throws IOException {
      throw new UnknownServiceException("URLC2: get Content not implemented!");
   }
   public String getContentEncoding() {
      return getHeaderField("content-encoding");
   }
   public long getDate() {
      return getHeaderFieldDate("date", 0);
   }
   
   public String getHeaderField(String s) {
      try {
         if (getInputStream() == null) { 
            return null;
         }
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
         return null;
      }
      
      return (String)header.get(s.toLowerCase());
   }
   
   public long getExpiration() {
      return getHeaderFieldDate("expires", 0);
   }
   
   public long getHeaderFieldDate(String f, long df) {
      long ret = df;
      String ldate = getHeaderField(f);
      try {
        //Date d = (new java.text.DateFormat()).parse(ldate);
         Date d = new Date(ldate);
         ret = d.getTime();
      } catch(Throwable t) {
         ;
      }
      return ret;
   }
   public int getHeaderFieldInt(String f, int d) {
      int ret = d;
      String v = getHeaderField(f);
      try {
         ret = Integer.parseInt(v);
      } catch (NumberFormatException e) {
      }
      return ret;
   }
   
   public String getHeaderField(int i) {
   
      try {
         if (!closed && !connected) connect();
         
        // Make sure headers are read
         getInputStream();
         
      } catch (IOException e) {
         return null;
      }
      
      String ret = null;
      
      if (i >= 0 && i < headerVals.size()) {
         ret = (String)headerVals.elementAt(i);
      }
      
      return ret;
   }
   
   public String getHeaderFieldKey(int i) {
   
      try {
         if (!closed && !connected) connect();
         
        // Make sure headers are read
         getInputStream();
         
      } catch (IOException e) {
         return null;
      }
      
      String ret = null;
      
      if (i >= 0 && i < headerKeys.size()) {
         ret = (String)headerKeys.elementAt(i);
      }
      
      return ret;
   }
   
   public long getLastModified() {
      return getHeaderFieldDate("last-modified", 0);
   }
}
