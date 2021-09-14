package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import com.ibm.as400.webaccess.common.*;
import netscape.security.*;
import com.ms.security.*;
import java.util.*;

import oem.edge.ed.odc.tunnel.common.*;
//import oem.edge.ed.odc.ftp.client.*;

public class ReceiveFromServer extends Thread {
   private final int bufsize = 64 * 1024;
   
   protected AppletSessionManager sessionMgr;
   
   protected boolean keeprunning = false;  // For debug
   
   protected boolean exitout     = false;  // For REAL
   
   private String uri;
   private String serverhost;
   private int serverport = -1;
   
   private Socket sock = null;
   
   private URL url = null;
   
   protected long lastDataInTime = 0;
   
   public long getLastDataInTime() { return lastDataInTime; }
   
   public void setKeepRunning(boolean a) { keeprunning = a; }
   
  // Used to abandon this thread
   public void forceExit() { 
      exitout = true;
      try {
         interrupt();
      } catch(Exception ee) {}
   }
   
   public ReceiveFromServer(AppletSessionManager cs, String host, 
                            String sessionid, String servletcontext) {
                                                        
     uri = servletcontext + "/servlet/oem/edge/ed/odc/tunnel/SendToClient";
      serverhost = host;
      serverport = cs.doSecure()?443:80;
      
      int idx = host.lastIndexOf(":");
      if (idx >= 0) {
         try {
            serverport = Integer.parseInt(host.substring(idx+1));
         } catch(NumberFormatException e) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "ReceiveFromServer: host = " + host + 
                               " Has illegal port!");
         }
         serverhost = host.substring(0, idx);
      }
      if (DebugPrint.getLevel() >= DebugPrint.INFO4) {
         DebugPrint.printlnd(DebugPrint.INFO4,
                            "ReceiveFromServer: Host=[" + serverhost 
                            + "] serverport=" + serverport);
      }
      sessionMgr = cs;
      setName("RFS");
   }   
   
   public void run() {

     // Get the time we last got data (init it)
      lastDataInTime = System.currentTimeMillis();
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG, "Receive thread started");
      }
      
      if (DebugPrint.inAnApplet()) {
         try {
            Class.forName("netscape.security.PrivilegeManager");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "Netscape PrivMgr is FOUND");
            }
           //		    PrivilegeManager.enablePrivilege("UniversalFileAccess");
         
            PrivilegeManager.enablePrivilege("UniversalConnect");
           // 		    PrivilegeManager.enablePrivilege("SuperUser");		    
         } catch (ClassNotFoundException e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "Netscape PrivMgr NOT found or NOT granted");
            }
         }
         try {
            Class.forName("com.ms.security.PermissionID");
            Class.forName("com.ms.security.PolicyEngine");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "Explorer PrivInfo is FOUND");
            }
            PolicyEngine.assertPermission(PermissionID.NETIO);
         } catch (ClassNotFoundException e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                  "Explorer PrivInfo is NOT found or NOT granted");
            }
         }
      }
   
      int errcnt = 0;
      while (sessionMgr.keepRunning() && !exitout) {
         TunnelMessage tm = null;
         try {
            
            String did = sessionMgr.desktopID();
            String str = DebugPrint.urlRewrite(uri + "?compname=" 
                                                   + sessionMgr.getToken()
                                                   + "&did=" + did, did);
            
            URL newURL = new URL(sessionMgr.doSecure()?"https":"http",
                                 serverhost, serverport, 
                                 str);
                                 
            if (url == null) {
               DebugPrint.printlnd(DebugPrint.INFO4, 
                                  "URL = " + newURL.toString());
               DebugPrint.println(DebugPrint.INFO4, 
                                  "just host = " + newURL.getHost());
            }
            
            url = newURL;
            
            URLConnection connection = null;
            
           // JMC 9/6/01 - Use URLConnection2 so Proxy support switching 
           //              works
            if (sessionMgr.doStreaming()) {
              // Streaming is now just keepalive with sockets!
               connection = new URLConnection2(url);
            } else {
               connection = url.openConnection();
            }
      
           //connection.setDoOutput(true);
           //connection.setDoInput(true);
            connection.setUseCaches(false);
            
            connection.connect();
            
            DebugPrint.printlnd(DebugPrint.INFO4, 
               "RFS: Establishing new Receive Tunnel ...");
            
            InputStream in = connection.getInputStream();
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, "RFS: Got contentlen of " 
                                  + connection.getContentLength());
            }
            
            errcnt = 0;
            
            if (connection.getContentType().equalsIgnoreCase(
                                               "application/octet-stream")) {
               keeprunning = true;
               while (keeprunning && sessionMgr.keepRunning() && !exitout) {
               
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "RFS(): About to read recnum");
                  }
                  
                  int totlen = 0;
                  SocketOutputBuffer outputBuffer = null;
                  if (sessionMgr.getProtocolVersion() > 1) {
                     tm = new TunnelMessage();
                  } else {
                     tm = new TunnelMessageV1();
                  }
                  if (!tm.readAll(in)) {
                     throw new IOException("RFS(): Error reading TunnelMessage??");
                  }
                  
                 // Get the time we last got data
                  lastDataInTime = System.currentTimeMillis();
                  
                  TunnelSocket ts = null;
                  
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "RFS (): " + tm.toString());
                  }
               
                 // Let otherside know we got this (udp anyone)
                  sessionMgr.sendAck(tm);
                  
                 /* Control port command */
               
                  if (tm.getEarId() == 0) {
                  
                     synchronized (sessionMgr.getControlMessageSyncObject()) {
                        short lastrec = 
                           sessionMgr.getLastControlRecordNumber();
                        if (!tm.isNextRecord(lastrec)) {
                           if (!tm.checkValidRecord(lastrec)) {
                              DebugPrint.printlnd(DebugPrint.WARN, 
                                 "RFS: Ignore incoming Cntl rec <OLD>: lrec=" 
                                                 + lastrec + " inmsg=" 
                                                 + tm.toString());
                           } else {
                              DebugPrint.println(DebugPrint.DEBUG, 
                                 "RFS: Incoming Cntl rec not Next Qit: lrec=" 
                                 + lastrec + " inmsg=" + tm.toString());
                              sessionMgr.saveControlMessage(tm);
                           }
                           tm = null;
                        }
                     }
                     if (tm != null) {
                       // Process rec ... will remove and process any 'next' 
                       //  records queued up as well.
                        handleControlCommand(tm);
                     }
                     
                  } else {
                  
                     byte id1 = tm.getEarId();
                     byte id2 = tm.getInstId();
                     SocketOutputBuffer outBuffer = null;
                     ts = sessionMgr.getTunnelSocket(id1, id2);
                     if (ts == null) {
                        TunnelEarInfo ti = sessionMgr.getTunnelEarInfo(id1);
                        if (ti == null) {
                           DebugPrint.printlnd(DebugPrint.INFO4, 
                              "RFS: Gak!: No ear info to do connect!: " + 
                              tm.toString());
                           throw new 
                              IOException("RFS(): No Ear Info to do connect");
                        } else {
                           DebugPrint.println(DebugPrint.DEBUG3, 
                                              "RFS(): RecvFromSrv: InstData from instid=" 
                                              + id2 + " for ear: " + 
                                              tm.toString());
                           
                           ConfigObject cf = new ConfigObject();
                           cf.setProperty("command", "killinst");
                           cf.setIntProperty("earid", (int)id1);
                           cf.setIntProperty("instid", (int)id2);
                           sessionMgr.writeControlCommand(cf);
                        }
                     } else {
                        outBuffer = ts.getOutputBuffer();
                     }
                     if (outBuffer != null) {
                        outBuffer.addTunnelMessage(tm);
                        sessionMgr.incrControlIn(tm.getOverhead());
                        sessionMgr.incrIn(tm.getAlternateLength(),
                                          tm.getLength());
                                          
                        tm = outBuffer.getWaitingTunnelMessage();
                        
                        int total = 0;
                        int bytesRead = 0;
                        while (tm != null) {
                           outBuffer.write(tm.getRecordNumber(), 
                                           tm.getBuffer(), 0, 
                                           tm.getLength());
                                           
                           if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                              DebugPrint.println(DebugPrint.DEBUG5, 
                                                 "RFS(): calling recordDone");
                           }
                           
                           outBuffer.recordDone(tm.getRecordNumber(), 
                                                tm.getLength());
                           tm = outBuffer.getWaitingTunnelMessage();
                        }
                     }
                  }
               } // end while(true)
            
            
               if (sessionMgr.keepRunning()) {
                  DebugPrint.printlnd(DebugPrint.DEBUG, 
                                     "RFS: Broke from inner loop.");
               
                 /* NOTE: This would block ... Yuk
                 **
                 System.out.println("RFS: Try closing");
                 in.close();  // This was blocking us for some reason
                 */
               
                  if (connection instanceof HttpURLConnection) {
                     DebugPrint.println(DebugPrint.DEBUG, 
                                        "RFS: Try disconnecting");
                     ((HttpURLConnection)connection).disconnect();
                  }
                  connection = null;
                  System.gc();
                  DebugPrint.println(DebugPrint.DEBUG, "RFS: Done, reloop");
               } else {
                  break;
               }
               
            } else if (connection.getContentType().equalsIgnoreCase(
                                               "text/plain")) {
               if (!sessionMgr.startingShutdown()) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                            "RFS: Invalid response from server! Abort!");
               }
               break;
            } else {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                  "RecvFromSrv: Content type NOT correct! " + 
                                  connection.getContentType());
                                  
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  byte buf[] = new byte[1024];
                  int len = 0;
                  while(len >= 0) {
                     len = in.read(buf);
                     DebugPrint.println(DebugPrint.DEBUG, 
                                        "Data len=" + len + "[" + 
                                        (new String(buf, 0, len)) + "]");
                  }
               }
               
               break;
            }
         } catch (NullPointerException e) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "RFS: Null Pointer Exception!");
            DebugPrint.println(DebugPrint.ERROR, e);
         } catch (MalformedURLException e) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "RFS: Malformed URL. Potential SOCKS setting problem?");
            DebugPrint.println(DebugPrint.ERROR, e);
         } catch (IOException e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, e);
            }
         } catch (OutOfMemoryError e) {
            boolean showstats = true;
            
           // Don't complain if its the Bogus error
            try {
               ByteArrayOutputStream os = new ByteArrayOutputStream();
               PrintWriter pw = new PrintWriter(os);
               e.printStackTrace(pw);
               pw.flush();
               String ss = new String(os.toByteArray());
               if (ss.indexOf(".skip(") > 0) {
                  showstats = false;
               }
            } catch(OutOfMemoryError ne) {
               ;
            }
            if (showstats) {
               Runtime rt = Runtime.getRuntime();
               long fm = rt.freeMemory();
               long totm = rt.totalMemory();
               
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                  "Got OOM: Freemem: " + fm + 
                                  " Total mem: " + totm + " Inuse: " + (totm-fm));
               DebugPrint.println(DebugPrint.ERROR, e);
               System.gc();
               DebugPrint.println(DebugPrint.ERROR, 
                                  "After GC Freemem: " + fm + 
                                  " Total mem: " + totm + " Inuse: " + (totm-fm));
               
               if (tm != null) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "lastTM value = " + tm.toString());
               }
            }
         }
         
        // Show we got data, even though error, cause we are working on it
         lastDataInTime = System.currentTimeMillis();
         
         if (sessionMgr.keepRunning()) {
            try {
               if ((errcnt % 5) == 0) {
                  if (!sessionMgr.startingShutdown()) {
                     DebugPrint.printlnd(DebugPrint.WARN, 
                                     "RFS: Error in connect/xmit ... Retry: "
                                     + (errcnt+1));
                  }
               }
               Thread.sleep(1000);
            } catch(InterruptedException e) {}
            errcnt++;
         }         
      }
      
      if (!exitout) {
         if (!sessionMgr.startingShutdown()) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd(DebugPrint.DEBUG, 
                                   "RecvFromSrv: Calling SessMgr Shutdown!");
            }
            sessionMgr.shutdown();
         }
      } else {
         DebugPrint.printlnd(DebugPrint.INFO, 
                             "RecvFromSrv: Exiting from Interrupt call");
      }
   }

   public String toString() {
      return "RecvFromSrv: " + super.toString();
   }   
   
   private void handleControlCommand(TunnelMessage tm) {
      while(tm != null) {
         
         try {
            
            sessionMgr.incrControlIn(tm.getOverhead() + tm.getLength());
            
            byte buf[] = tm.getBuffer();
            int i=0;
            
           // May be more than one control command in single TM
           // If the IGNORE flag is on ... ignoreit !!
            while(i < buf.length && (tm.getFlags() & 0x02) == 0) {
               
              // Find CONTROL SEPARATOR. If none (oldway), take it 
               int ofs = i;
               while(i < buf.length) {
                  if (buf[i] == SessionManager.CONTROL_SEPARATOR) {
                     if ((i-ofs) == 0) {
                        i++;
                        ofs = i;
                        continue;
                     }
                     break;
                  }
                  i++;
               }
               
               if ((i-ofs) == 0) continue;
               
               if (ofs != 0) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "SM: Got a double CntlCmd!\n");
               }
               
               ConfigFile cf = 
                  new ConfigFile(new ByteArrayInputStream(buf, ofs, i-ofs));
               
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "RFS(): Got control command!" + 
                                     cf.toString());
               }
               
               String cmd = cf.getProperty("command");
               if (cmd == null) cmd = "null";
               
               if (cmd.equals("acks")) {
                  String s = null;
                  for(int j=1; (s=cf.getProperty(""+j)) != null ; j++) {
                     boolean ackFound = sessionMgr.removeMessage(s);
                     if (ackFound) {
                        if (DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG3, 
                              "RFCMP: Ack found " + s);
                        }
                     }
                  }
               } else if (cmd.equals("killinst")) {
                  int earid = cf.getIntProperty("earid", 0);
                  int instid = cf.getIntProperty("instid", 0);
                  sessionMgr.cleanupConnection((byte) earid, (byte) instid);
               } else if (cmd.equals("killear")) {
                  int earid = cf.getIntProperty("earid", 0);
                  sessionMgr.cleanupConnection((byte)earid);
               } else if (cmd.equals("newtoken")) {
                  String token = cf.getProperty("token", null);
                  if (token == null) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                        "'newtoken' arrived with no TOKEN!");
                  } else {
                     sessionMgr.setToken(token);
                     
                     String logintoken = sessionMgr.getLoginToken();
                     if (logintoken != null) {
                        ConfigObject co2 = new ConfigObject();
                        co2.setProperty("COMMAND", "rewrap_token");
                        co2.setProperty("TOKEN",   logintoken);
                        sessionMgr.writeControlCommand(co2);
                     }
                  }
               } else if (cmd.equals("rewrap_token_resp")) {
                  String token = cf.getProperty("TOKEN", null);
                  if (token == null) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                 "'rewrap_token_resp' arrived with no TOKEN!");
                  } else {
                     sessionMgr.setLoginToken(token);
                     sessionMgr.fireActionEvent("RewrappedToken");
                  }
               } else if (cmd.equals("shutdown")) {
                  if (!sessionMgr.startingShutdown()) {
                     DebugPrint.printlnd("RFS: Received request to Shutdown");
                     sessionMgr.shutdown();
                  }
               } else if (cmd.equals("serverping")) {
                  sessionMgr.writeControlCommand(cf);
               } else if (cmd.equals("ping")) {
                  long curtime = System.currentTimeMillis();
                  long start = cf.getLongProperty("clientSend", 0);
                  long recv  = cf.getLongProperty("serverRecv", 0);
                 /*
                  long c1  = cf.getLongProperty("C1", 0);
                  long c2  = cf.getLongProperty("C2", 0);
                  long c3  = cf.getLongProperty("C3", 0);
                  long c4  = cf.getLongProperty("C4", 0);
                  if (c4 != 0) {
                     if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                        DebugPrint.printlnd("Ping = cls[" + start + "] srv[" 
                                            + recv + "] clr[" + curtime + "]");
                        DebugPrint.println("Round Delt = [" + (curtime-start) +
                                           "] Send Delt ["  + (recv-start)    +
                                           "] Recv Delt ["  + (curtime-recv)  
                                           + "]");
                     } else {
                        long s1   = cf.getLongProperty("S1", 0);
                        long s2   = cf.getLongProperty("S2", 0);
                        long s3   = cf.getLongProperty("S3", 0);
                        long s4   = cf.getLongProperty("S4", 0);
                        long t1  = cf.getLongProperty("T1", 0);
                        long t2  = cf.getLongProperty("T2", 0);
                        long t3  = cf.getLongProperty("T3", 0);
                        long t4  = cf.getLongProperty("T4", 0);
                        
                        DebugPrint.printlnd("CPing: "+(curtime-start) + " ms");
                        DebugPrint.printlnd("CPing: "+(t4-c4) + " ms");
                        DebugPrint.printlnd("CPing: "+(t3-c3) + " ms");
                        DebugPrint.printlnd("CPing: "+(t2-c2) + " ms");
                        DebugPrint.printlnd("CPing: "+(t1-c1) + " ms");
                        DebugPrint.printlnd("SLoop: "+(recv-s4) + " ms");
                        DebugPrint.printlnd("SLoop: "+(s4-s3) + " ms");
                        DebugPrint.printlnd("SLoop: "+(s3-s2) + " ms");
                        DebugPrint.printlnd("SLoop: "+(s2-s1) + " ms");
                     }
                  } else {
                     String cn="C1"; 
                     String sn="S1";
                     String ct="T1";
                     if        (c3 != 0) {
                        cn="C4"; sn="S4"; ct="T4";
                     } else if (c2 != 0) {
                        cn="C3"; sn="S3"; ct="T3";
                     } else if (c1 != 0) {
                        cn="C2"; sn="S2"; ct="T2";
                     }
                     cf.setLongProperty(cn, start);
                     cf.setLongProperty(sn, recv);
                     cf.setLongProperty(ct, curtime);
                     
                     curtime = System.currentTimeMillis();
                     cf.setLongProperty("clientSend", curtime);
                     sessionMgr.writeControlCommand(cf);
                  }
                 */
                  if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
                     DebugPrint.printlnd("Ping = cls[" + start + "] srv[" 
                                         + recv + "] clr[" + curtime + "]");
                     DebugPrint.println("Round Delt = [" + (curtime-start) +
                                        "] Send Delt ["  + (recv-start)    +
                                        "] Recv Delt ["  + (curtime-recv)  
                                        + "]");
                  } else {
                     DebugPrint.printlnd("Ping: "+(curtime-start) + " ms");
                  }
                     
               } else if (cmd.equals("consumption")) {
                  int earid    = cf.getIntProperty("earid", 0);
                  int instid   = cf.getIntProperty("instid", 0);
                  int rate     = cf.getIntProperty("rate", 0);
                  sessionMgr.setConsumptionRate((byte) earid, (byte) instid,
                                                rate);
               } else if (cmd.equals("doconsumption")) {
                  boolean onOff = cf.getBoolProperty("onOff", true);
                  sessionMgr.setDoConsumption(onOff);
               } else if (cmd.equals("compressx")) {
                  sessionMgr.setXCompression(true);
               } else if (cmd.equals("nocompressx")) {
                  sessionMgr.setXCompression(false);
               } else if (cmd.equals("compressall")) {
                  sessionMgr.setCompression(true);
               } else if (cmd.equals("nocompressall")) {
                  sessionMgr.setCompression(false);
               } else if (cmd.equals("xon")) {
                  int earid = cf.getIntProperty("earid", 0);
                  int instid = cf.getIntProperty("instid", 0);
                  sessionMgr.setXonOff(true, (byte) earid, (byte) instid);
               } else if (cmd.equals("xoff")) {
                  int earid = cf.getIntProperty("earid", 0);
                  int instid = cf.getIntProperty("instid", 0);
                  sessionMgr.setXonOff(false, (byte) earid, (byte) instid);
               } else if (cmd.equals("newear")) {
                  String targethost = cf.getProperty("targethost");
                  int targetport = cf.getIntProperty("targetport", 0);
                  int earid = cf.getIntProperty("earid", -1);
                  String eartype = cf.getProperty("eartype");
                  String otherinfo = cf.getProperty("otherinfo");
                  int startport = 0;
                  boolean isICA = false;
                  if (targethost == null) {
                     targethost = "UnknownHost";
                  }
                  if (earid == -1 || earid == 0) {
                     earid = (int) sessionMgr.getNextEarId();
                  }
                  if (eartype != null) {
                     if (eartype.equals("X")) {
                        startport = 6008;
                     } else if (eartype.equals("ica")) {
                        isICA = true;
		     } else if (eartype.equals("IMS"))  {/* subu 05/19/02 */
			startport = 1533;
                     } else if (eartype.equals("STM"))  {
                        ;
                     } else if (eartype.toUpperCase()
                                       .startsWith("DESKTOPONCALLSERVICE"))  {
                        startport = targetport;
                     }
                  }
                  
                  if (!(sessionMgr.doCombo() && isICA)) {
                     
                     if (eartype.equals("ftp-nolonger")) { 
                        try {
                           Class guiclass    = Class.forName(
                              "oem.edge.ed.odc.ftp.client.GUIActionHandler");
                           Class socketclass = Class.forName(
                              "java.net.Socket");
                              
                           Class classparms[] = new Class[1];
                           classparms[0] = socketclass;
                           java.lang.reflect.Method meth = 
                              guiclass.getMethod("recevSocket", classparms);
                              
                           TunnelEarInfo ti = 
                              new TunnelEarInfo(sessionMgr, 
                                                eartype , 
                                                (byte) earid, 
                                                targethost, 
                                                targetport, 
                                                otherinfo);
                           sessionMgr.registerTunnelEar(ti);
                           
                          // Do this to keep things separate for now
                           Object guiobj = guiclass.newInstance();
                           Object methparms[] = new Object[1];
                           methparms[0] = ti.generatePairedSocket("ftp");
                           meth.invoke(guiobj, methparms);
                           
                           ti.informEarCreated();
                        } catch(java.lang.reflect.InvocationTargetException 
                                ite) {
                           DebugPrint.printlnd(DebugPrint.ERROR, 
                               "Incoming FTP request failed with exception");
                           DebugPrint.println(DebugPrint.ERROR, 
                                              ite.getTargetException());
                           
                        } catch(Throwable tt) {
                           DebugPrint.printlnd(DebugPrint.ERROR, 
                               "Incoming FTP request failed with exception");
                           DebugPrint.println(DebugPrint.ERROR, tt);
                        }
                        
                     } else if (eartype.equals("ftp")) { 
                     
                       // No listening socket needed, just create a plain
                       // TunnelEar which DSMP code will use to materialize
                       // a socket
                        TunnelEarInfo ti = new TunnelEarInfo(sessionMgr, 
                                                             eartype , 
                                                             (byte) earid, 
                                                             targethost, 
                                                             targetport, 
                                                             otherinfo);
                        sessionMgr.registerTunnelEar(ti);
                        ti.informEarCreated();
                        
                       // Mike (the listener) should call 
                       // ti.generatePairedSocket("eartype") to create a 
                       // connected 'socket' to the tunnel
                        sessionMgr.handleLaunch(ti);
                        
                     } else if (eartype.equalsIgnoreCase("xfr")) { 
                     
                       // No listening socket needed, just create a plain
                       // TunnelEar which DSMP code will use to materialize
                       // a socket
                        TunnelEarInfo ti = new TunnelEarInfo(sessionMgr, 
                                                             eartype , 
                                                             (byte) earid, 
                                                             targethost, 
                                                             targetport, 
                                                             otherinfo);
                        sessionMgr.registerTunnelEar(ti);
                        ti.informEarCreated();
                        
                       // Mike (the listener) should call 
                       // ti.generatePairedSocket("eartype") to create a 
                       // connected 'socket' to the tunnel
                        sessionMgr.handleLaunch(ti);
                        
                     } else {
                       // Create a listening ear for all 'normal' uses
                        HttpTunnelThread tt = 
                           new HttpTunnelThread(sessionMgr, 
                                                eartype, 
                                                (byte) earid, 
                                                targethost, 
                                                targetport, 
                                                startport, 
                                                otherinfo);
                        sessionMgr.registerTunnelEar(tt);
                        int lport = tt.getLocalPort();
                        tt.informEarCreated();
                        DebugPrint.printlnd(DebugPrint.INFO, 
                            "RFS: Creating local listening socket: " + lport);
                        //sessionMgr.handleICALaunch(tt);
                        sessionMgr.handleLaunch(tt);
                     }
                  } else {
                     DebugPrint.printlnd(DebugPrint.INFO, 
                        "Combomode ... Ignore create ICAEar");
                  }
               } else if (cmd.equals("NOOP")) {
                  ;
               } else if (cmd.equals("THRUPUT")) {
                  int bytecount = cf.getIntProperty("BYTECOUNT", -1);
                  String origin = cf.getProperty("ORIGIN");
                  
                  DebugPrint.println(DebugPrint.INFO2, 
                                     "THRUPUT: CNT=" + bytecount +
                                     " origin = "    + origin);
                  
                  if (origin.equals("CLIENT")) {
                     sessionMgr.fireActionEvent("THRUPUT-CLIENT");
                  } else {
                     sessionMgr.fireActionEvent("THRUPUT-SERVER");
                  }
               } else if (cmd.equals("showclient")) {
                  String str = cf.getProperty("message");
                  if (str == null) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                 "Got showclient cntlmsg, but no data!");
                  } else {
                     if (str.indexOf("but your connectivity tested fine!")
                         >= 0) {
                        sessionMgr.fireActionEvent("ConnectivityOK");
                     } else {
                        sessionMgr.fireActionEvent(
                           new TunnelEvent(sessionMgr,
                                           java.awt.event.ActionEvent.ACTION_PERFORMED,
                                           "MsgFromServer", str));
                        DebugPrint.printlnd(DebugPrint.DEBUG, 
                                            "Message from Server\n" + str);
                     }
                  }
               } else {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "Unknown control command from other side was [" 
                                     + cmd + "]");
               }
            }
            
            synchronized(sessionMgr) {
               sessionMgr.setLastControlRecordNumber(tm.getRecordNumber());
               tm = sessionMgr.getWaitingControlMessage();
            }
         } catch(IOException e) {
         }
      }
   }
}
      
