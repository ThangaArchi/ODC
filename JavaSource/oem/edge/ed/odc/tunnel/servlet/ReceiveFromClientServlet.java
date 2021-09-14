package oem.edge.ed.odc.tunnel.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ed.odc.tunnel.common.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.cntl.*;

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

public class ReceiveFromClientServlet extends HttpTunnelServlet {

  // This class is needed to shield the user from InterruptedException
   class RFCInputStream extends InputStream {
      InputStream in = null;
      long tot       = 0;
      byte buf[]     = new byte[64*1024];
      int count      = 0;
      int pos        = 0;
      boolean closed = false;
      public RFCInputStream(InputStream is) {
         in = is;
      }
      
      public int read() throws IOException {
         byte inbuf[] = new byte[1];
         int l = read(inbuf, 0, inbuf.length);
         if (l == 1) return inbuf[0];
         else        return -1;
      }
      public int read(byte inbuf[]) throws IOException {
         return read(inbuf, 0, inbuf.length);
      }
      
      public int read(byte inbuf[], int ofs, int len) throws IOException {
         if (closed) throw new IOException("Stream closed");
         if (pos == count) {
            count = pos = 0;
            
           // We get InterruptedIOException when reading takes too long.
           //  Just ignore it for now
            while(count == 0) {
               try {
                  count=in.read(buf, 0, buf.length);
               } catch(InterruptedIOException iioe) {
                  DebugPrint.printlnd(DebugPrint.INFO4, 
                                      "InterruptedException ... continue");
               }
            }
            if (count < 0) {
               count = 0;
            } else {
               tot += count;
               if (DebugPrint.getLevel() >= DebugPrint.DEBUG5) {
                  DebugPrint.printlnd(DebugPrint.DEBUG5, 
                                      "Buffered Read bytes: " + count + 
                                      " tot = " + tot + "\n");
               
                  DebugPrint.printlnd(DebugPrint.DEBUG5, 
                                      DebugPrint.showbytes(buf, 0, count));
               }
            }
         }
         
         int ret = -1;
         if (count > pos) {
            ret = count - pos;
            if (ret > len) ret = len;
            if (ret > 0) {
               System.arraycopy(buf, pos, inbuf, ofs, ret);
               pos += ret;
            }
         }
         return ret;
      }
      
      
     // This was used for debug ... can be deleted
      public int directread(byte inbuf[], 
                            int ofs, int len) throws IOException {
                                                   
         if (closed) throw new IOException("Stream closed");
         int ret = 0;
            
         if (len == 0) return 0;
         
        // We get InterruptedIOException when reading takes too long.
        //  Just ignore it for now
         while(ret == 0) {
            try {
               ret=in.read(inbuf, ofs, len);
            } catch(InterruptedIOException iioe) {
               DebugPrint.printlnd(DebugPrint.INFO4, 
                                   "InterruptedException ... continue");
            }
         }
         return ret;
      }
      
      public int  available() throws IOException {
         if (closed) throw new IOException("Stream closed");
         return count-pos;
      }
      public void close() throws IOException {
         if (closed) throw new IOException("Already closed");
         closed = true;
      }
   }

  // so we can keep stats across function calls
   class RFCStats {
      long numTMRead=0, numTMWritten=0, 
           totAmtRead=0, totAmtWritten=0, beforeReadAll=0, afterReadAll=0,
           readData=0, handleCntlCmd=0, numCntlCmds=0, writeTM=0,
           topTM=0, botTM=0;
      String name;
      boolean reallyDoLogging;
      
      RFCStats(String n, boolean logging) {
         name = n; reallyDoLogging = logging;
      }
   }

   static private final boolean dologging = true;

  // Consume the tunnel message
   public void handleTunnelMessage(TunnelMessage tm, 
                                   HttpTunnelSession sessionMgr, 
                                   RFCStats stats) throws IOException {
   
      long ttm;
   
      TunnelSocket ts = null;
      if (tm.getEarId() == 0) {
         
        /* Control port command */
        
         synchronized (sessionMgr.getControlMessageSyncObject()) {
            short lastrec = sessionMgr.getLastControlRecordNumber();
            
            if (!tm.isNextRecord(lastrec)) {
               if (!tm.checkValidRecord(lastrec)) {
                  DebugPrint.println(DebugPrint.INFO4,
                                     "RFCMP: Incoming Cntl record OLD: lastrec=" + 
                                     lastrec + tm.toString());
                  return;
               }
               DebugPrint.println(DebugPrint.INFO4, 
                                  "RFCMP: Incoming Cntl record not Next Qit: lastrec=" + 
                                  lastrec + " inmsg=" + tm.toString());
               sessionMgr.saveControlMessage(tm);
               tm = null;
            }
         }  
         if (tm != null) {
           // Process record ... will remove and process any 'next' 
           //  records queued up as well.
            stats.numCntlCmds++;
            handleControlCommand(tm, sessionMgr);
            
         }
         
         if (dologging && stats.reallyDoLogging) {
            ttm = System.currentTimeMillis();
            stats.handleCntlCmd += ttm - stats.afterReadAll;
         }
         
      } else {
         byte  id1 = tm.getEarId();
         byte  id2 = tm.getInstId();
         short rec = tm.getRecordNumber();
         
         ts = sessionMgr.getTunnelSocket(id1, id2);
         if (ts == null) {
         
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "RFCMP:processTunnel:"+" EarID "+id1);
            }
            
            
            TunnelEarInfo ti = sessionMgr.getTunnelEarInfo(id1);
            
           // Synchronize on ti here, so that if we have multiple senders,
           //  the first sender in here, whether it is rec 1 or not, is the
           //  creator of the tunnel socket. All others wait until the ts is
           //  created.
            if (ti != null) synchronized (ti) {
               ts = sessionMgr.getTunnelSocket(id1, id2);
               if (ts == null) {
               
                  String et = ti.getEarType();
                  boolean ftpinternal = false;
               
                  if (et.equals("ftp")) {
                     DebugPrint.printlnd(DebugPrint.INFO4, 
                                         "Got a request to start FTP session");
                     String internalftp = 
                        DesktopServlet.getDesktopProperty("edodc.ftpInternal");
                     
                     if (internalftp != null && 
                         internalftp.equalsIgnoreCase("true")) {
                        ftpinternal = true;
                     }
                  }
                  
                  if (ftpinternal) {
                     DebugPrint.printlnd(DebugPrint.INFO4, 
                                         "Got ftpinternal request");
                     
                     try {
                        
                        Class guiclass    = Class.forName(
                           "oem.edge.ed.odc.ftp.server.FTPServer");
                        Class socketclass = Class.forName(
                           "java.net.Socket");
                        
                        Class classparms[] = new Class[1];
                        classparms[0] = socketclass;
                        java.lang.reflect.Constructor construct = 
                           guiclass.getConstructor(classparms);
                        
                        OSocket osock = OSocket.socketpair();
                        OSocket othersock = osock.getOther();
                        
                        osock.setName("tnl");
                        othersock.setName("ftp");
                        ts = ti.makeTunnelSocket(osock);
                        
                       // FTPServer ftp = new FTPServer(othersock);
                       // ftp.start();
                        
                       // Do this instead to keep things separate
                        Object constparms[] = new Object[1];
                        constparms[0] = othersock;
                        Object ftp = construct.newInstance(constparms);
                        ((Thread)ftp).start();
                        
                     } catch(Throwable tt) {
                        DebugPrint.printlnd(DebugPrint.ERROR, 
                                            "Incoming FTP request failed with exception");
                        DebugPrint.println(DebugPrint.ERROR, tt);
                     }
                     
                  } else {
                     
                    /* New connection on defined ear */
                     
                     if (DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.DEBUG, 
                                           "RFCMP(" + stats.name + 
                                           "): Creating new Socket!");
                     }
                     
                     Socket socket = null;
                     try {
                        socket = new Socket(ti.getHost(), ti.getPort());
                        socket.setTcpNoDelay(true);
                        
                       // Total HACK! Check userdata on TEI ... if exists, then
                       //  assume that we are talking to a SUBU White Board 
                       //  proxy
                        if (ti.getUserData() != null) {
                           socket = URLConnection2.sslizeSocket(socket);
                           DesktopServlet.handleXChannelStartup(ti, socket);
                        }
                        
                        if (DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG, 
                                              "RFCMP(" + stats.name  +
                                              "): socket opened at " +
                                              Integer.toString(ti.getPort()));
                        }
                        
                     } catch (Throwable ioe) {
                        
                        if (socket != null) try {
                           socket.close();
                        } catch(Exception ee) {}
                        
                        String s = "RFCMP(" + stats.name 
                           + "): Unable to connect to " 
                           + ti.getHost() 
                           + ":" + ti.getPort();
                        DebugPrint.println(DebugPrint.ERROR, 
                                           "Exception creating socket: " + s);
                        DebugPrint.println(DebugPrint.ERROR, ioe);
                        
                        ConfigObject co = new ConfigObject();
                        co.setProperty("COMMAND", "showclient");
                        co.setProperty("MESSAGE", 
                                       "Error establishing connection: " +
                                       ioe.getMessage());
                        
                        sessionMgr.writeControlCommand(co);
                        
                        sessionMgr.cleanupConnection(id1);
                        return;
                     }
                     
                     
                     if (ti.isRM()) {
                        String v;
                        v=DesktopServlet.getDesktopProperty("edodc.rmProxy");
                        if (v == null || !v.equalsIgnoreCase("FALSE")) {
                           try {
                              DataInputStream dis = 
                                 new DataInputStream(socket.getInputStream());
                              DataOutputStream dos = 
                                new DataOutputStream(socket.getOutputStream());
                              dos.writeUTF("Connect to the 45th Galaxy");
                              String syn = dis.readUTF();
                              
                              if(!syn.equals("No I want Blue tie")) {
                                 throw new Exception("Bad Proxy Handshake");
                              } else {
                                 String rmURL = ti.getOtherInfo();
                                 int idx = rmURL.indexOf('/');
                                 if (idx == 0) {
                                    rmURL = rmURL.substring(1);
                                 }
                                 idx = rmURL.indexOf('/');
                                 if (idx > 0) {
                                    rmURL = rmURL.substring(0, idx);
                                 }
                                 dos.writeUTF(rmURL);
                              }
                           } catch(Throwable tt) {
                              try { socket.close(); } catch(Throwable cs) {}
                              ConfigObject co = new ConfigObject();
                              co.setProperty("COMMAND", "showclient");
                              co.setProperty("MESSAGE", 
                                             "RM Proxy authentication error: "
                                             + tt.getMessage());
                              
                              sessionMgr.writeControlCommand(co);
                              
                              sessionMgr.cleanupConnection(id1);
                              return;
                           }
                        }
                     }
                     
                     ts = new TunnelSocket(sessionMgr, ti.getEarType(), 
                                           socket, id1, id2, 
                                           ti.getOtherInfo());
                     
                     ti.addTunnelSocket(ts);
                  }
               } else {
                  DebugPrint.println(DebugPrint.WARN, 
                                     "RFCMP(" + stats.name + 
                                     "): RecvFromCli: RACECOND: id1=" 
                                     + id1 + " id2=" + id2 + 
                                     " rec=" + rec + 
                                     " ts was null then filled" );
                  
               }
            } else {
               DebugPrint.println(DebugPrint.WARN, 
                                  "RFCMP(" + stats.name 
                                  + "): RecvFromCli: Gak! id1=" 
                                  + id1 + " id2=" + id2 
                                  + " BUT, no earinfo!!");
               
               ConfigObject cf = new ConfigObject();
               cf.setProperty("command", "killear");
               cf.setIntProperty("earid", (int)id1);
               sessionMgr.writeControlCommand(cf);
               return;
            }
         }
         
         
         SocketOutputBuffer outBuffer = ts.getOutputBuffer();
         if (outBuffer != null) {
            
            outBuffer.addTunnelMessage(tm);
            
            sessionMgr.incrIn(tm.getAlternateLength(),tm.getLength());
            
            tm = outBuffer.getWaitingTunnelMessage();
            
            while (tm != null) {
               
               try {
                  outBuffer.write(tm.getRecordNumber(), tm.getBuffer(), 
                                  0, tm.getLength());
               } catch(java.lang.Error t) {
                 // Acck! Got an OutOfMemory err, have to add this back to the 
                 //       tunnelmessage list, as we did not write it yet
                 //       (hopefully we did not write anything to the buffer)
                 //   AND we have not done recordDone. So, next send should 
                 //       try to add the message to the Tunnel again
                  outBuffer.addTunnelMessage(tm);
                  DebugPrint.println(DebugPrint.WARN, 
                                     "RFCMP(" + stats.name +
                                     "): RecvFromCli: tried to write rec "+
                                     + tm.getRecordNumber() + 
                                     " got exception");
                  DebugPrint.println(DebugPrint.WARN, 
                                     t);
                  throw t;
               } catch(Throwable gt) {
                 // As above, but not an error, so can't be rethrown
                  outBuffer.addTunnelMessage(tm);
                  DebugPrint.println(DebugPrint.WARN, 
                                     "RFCMP(" + stats.name +
                                     "): RecvFromCli: tried to write rec "+
                                     + tm.getRecordNumber() + 
                                     " got exception");
                  DebugPrint.println(DebugPrint.WARN, 
                                     gt);
                 // Just return as though everything is cool.
                  return;
               }
               
               stats.numTMWritten++;
               stats.totAmtWritten += tm.getLength();
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "RFCMP(" + stats.name + 
                                     "): calling recordDone");
               }
               
               try {
                  outBuffer.recordDone(rec, tm.getLength());
               } catch(Throwable tt) {
                  DebugPrint.println(DebugPrint.WARN, 
                                     "RFCMP(" + stats.name + 
                                     "): RecvFromCli: RecordDone for record" +
                                     tm.getRecordNumber() + " had exception");
               }
               tm = outBuffer.getWaitingTunnelMessage();
            }
         }
         
         if (dologging && stats.reallyDoLogging) {
            ttm = System.currentTimeMillis();
            stats.writeTM += ttm - stats.afterReadAll;
         }
      }   
   }
   
   
   public String processTunnel(String name, 
                               HttpTunnelSession sessionMgr, 
                               InputStream in, 
                               boolean streaming) throws IOException {
      
      String entername = Thread.currentThread().getName();
      
      try {
         String response = "";
         
         RFCStats stats = new RFCStats(name, sessionMgr.getDoLogging());
         
         
         if (dologging && stats.reallyDoLogging) {
            stats.topTM = System.currentTimeMillis();
         }
         
         if (entername != null) {
            if (name != null) {
               name = name + " - " + entername;
               if (name.length() > 50) name = name.substring(0, 50);
            }
         }
         
         if (name != null) Thread.currentThread().setName(name);
         
         while (sessionMgr.keepRunning()) {
            
            DebugPrint.printlnd(DebugPrint.DEBUG3,
                                "ProcessTunnel: top of loop");
                             
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "RFCMP(" + name + "): About to read recnum");
            }
         
            int totlen = 0;
            TunnelMessage tm;
            if (sessionMgr.getProtocolVersion() > 1) {
               tm = new TunnelMessage();
            } else {
               tm = new TunnelMessageV1();
            }
         
            if (dologging && stats.reallyDoLogging) {
               stats.beforeReadAll = System.currentTimeMillis();
            }
         
            if (!tm.readAll(in)) {
               break;
            }
 
            if (dologging && stats.reallyDoLogging) {
               stats.numTMRead++;
               stats.totAmtRead += tm.getLength();
                  
               stats.afterReadAll = System.currentTimeMillis();
               stats.readData += stats.afterReadAll - stats.beforeReadAll;
            }
         
           // If we are streaming, we send ACKS via SendToClientServlet
            if (streaming) {
              // Let otherside know we got this (udp anyone)
               sessionMgr.sendAck(tm);
            } else {
              // Otherwise, (oldway), we are doing POST POST POST, 
              //  return ACKS
               response = response + "recv "       + tm.getEarId() 
                  + " " + tm.getInstId()
                  + " " + tm.getRecordNumberString()
                  + " " + tm.getLength() + "\n";
                                        
               if (response.length() > 8000) {
                  DebugPrint.println(DebugPrint.WARN, 
                                     "RFC: Gak!, response > 8k:\n" + response);
                  response = "";
               }
            }
         

           // Add in overhead now. Body costs added elsewhere
            sessionMgr.incrControlIn(tm.getOverhead());
            
         
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "RFCMP " + name + "): " + tm.toString());
            }
         
            handleTunnelMessage(tm, sessionMgr, stats);
         }
      
         if (dologging && stats.reallyDoLogging) {
            stats.botTM = System.currentTimeMillis();
            sessionMgr.log("InLog: " + sessionMgr.getEdgeId() +
                           " totTm:"    + (stats.botTM-stats.topTM) + 
                           " rdTm:"     + stats.readData      +
                           " wrtTm: "   + stats.writeTM       +
                           " rdAmt: "   + stats.totAmtRead    +
                           " wrtAmt: "  + stats.totAmtWritten +
                           " cntlTm: "  + stats.handleCntlCmd +
                           " #Cntl: "   + stats.numCntlCmds);
         
         }
         return response;
         
      } finally {
         Thread.currentThread().setName(entername);
      }
      
   }   
   
  // Debug
   protected void printRequestParameters(HttpServletRequest req) {
      try { 
         Enumeration headerNames = req.getHeaderNames();
         while (headerNames.hasMoreElements()) {
            String head = (String)headerNames.nextElement();
            Enumeration fieldNames = req.getHeaders(head);
            while (fieldNames.hasMoreElements()) {
               String paramName = (String) head;
               String paramValue = (String)fieldNames.nextElement();
               
              // check for missing parameters (code to be added)
               if (paramValue.length() == 0)
                  DebugPrint.println(DebugPrint.ERROR, 
                                     paramName + " is not specified");
               
               else {
                  DebugPrint.printlnd(DebugPrint.WARN, "Key:Value = " + 
                                      paramName + " = " + paramValue);
               }
            }
         }
      } catch(Exception e) {
         DebugPrint.printlnd("Error in getReqParms: " + e.getMessage());
         DebugPrint.printlnd("    " + e.toString());
      }
   }
   
   public void doTunnel(HttpServletRequest req, 
                        HttpServletResponse resp, 
                        HttpTunnelSession sessionMgr) 
      throws IOException, ServletException {

     // Try a direct connect for perf testing
     /*
      String direct = req.getHeader("dodirect");
      if (direct != null) {
         
         DebugPrint.println("Value of dodirect = " + direct);
         resp.setContentType("text/plain");
         PrintWriter writer = resp.getWriter();
         DirectPort directport = new DirectPort(sessionMgr);
         directport.start();
         writer.println("directport=" + directport.getListenPort());
         writer.close();
         return;
      }
     */
     
      long topTM=0, beforeProcessTM=0, afterProcessTM=0, afterResponseTM=0;
           
      boolean reallyDoLogging = sessionMgr != null ? sessionMgr.getDoLogging()
                                                   : false;
     
     
      
     // DEBUG
      String name   = req.getHeader("putname");
      if (name == null) {
         name = "UnSet";
      }
      
      if (DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "RecvFromClient(" + name + "): Top  Read rec");
      }
      
      if (dologging && reallyDoLogging) {
         topTM = System.currentTimeMillis();
      }
      
      
      int l;
	
      if (dologging && reallyDoLogging) {
         beforeProcessTM = System.currentTimeMillis();
      }
      
      Enumeration enum = req.getHeaders("expect");
      if (enum != null && enum.hasMoreElements() && 
          ((String)enum.nextElement()).equalsIgnoreCase("100-Continue")) {
         DebugPrint.printlnd(DebugPrint.DEBUG, 
                             "Got expect 100 ... Setting 100 status code");
         resp.setStatus(100);
      }
      
      
/*   DEBUG

      printRequestParameters(req);
      
      InputStream is = req.getInputStream();
      byte buf[] = new byte[64*1024];
      long tot = 0;
      long starttime = System.currentTimeMillis();
      DebugPrint.printlnd("Start xfer: " + starttime);
      while(true) {
         l = is.read(buf, 0, buf.length);
         if (l > 0) {
            tot += l;
            if ((tot % (1024*1024)) < ((tot-l) % (1024*1024))) {
               
               long endtime = System.currentTimeMillis();
               DebugPrint.printlnd("End xfer: " + endtime);
               
               long deltatime = endtime - starttime;
               DebugPrint.printlnd("Delta = " + deltatime);
               if (deltatime <= 0) deltatime = 1;
               DebugPrint.printlnd("Tot Bytes = " + tot);
               DebugPrint.printlnd("XferRate (KB/s) = " + ((((tot*1000)/deltatime))/1024));
            }
//            DebugPrint.printlnd(DebugPrint.WARN, "Read bytes: " + l + 
//                                " tot = " + tot + "\n");
//                                
//            DebugPrint.printlnd(DebugPrint.WARN, "Read bytes: " + l + 
//                                " tot = " + tot + "\n" + 
//                                
//                                DebugPrint.showbytes(buf, 0, l));
         } else if (l == 0) {
            DebugPrint.printlnd(DebugPrint.WARN, "Read 0!! Loop");
         } else {
            DebugPrint.printlnd(DebugPrint.WARN, "Read < 0!! Break");
            break;
         }
      }
      long endtime = System.currentTimeMillis();
      DebugPrint.printlnd("End xfer: " + endtime);
      
      long deltatime = endtime - starttime;
      DebugPrint.printlnd("Delta = " + deltatime);
      if (deltatime <= 0) deltatime = 1;
      DebugPrint.printlnd("XferRate (KB/s) = " + ((((tot*1000)/deltatime))/1024));
      
      if (tot > 0) return;
*/
     
      String pinfo = req.getPathInfo();
      boolean streaming = pinfo != null && 
                          pinfo.equalsIgnoreCase("/streaming");
      
      if (streaming) {
         DebugPrint.printlnd(DebugPrint.INFO3, "Streaming ON");
      }
         
      String response = 
         processTunnel(name, sessionMgr, 
                       new RFCInputStream(req.getInputStream()), 
                       streaming);
      
      if (dologging && reallyDoLogging) {
         afterProcessTM = System.currentTimeMillis();
      }

     //  in.close();
      if (DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "RFCMP(" + name + "): Returning to Applet Caller");
      }
      
      resp.setContentType("text/plain");
      resp.setStatus(200);
      PrintWriter writer = resp.getWriter();
      writer.println(response + "\ndone");
      
      if (DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "RFCMP: Return: " + response);
      }
      
      writer.close();
      
      if (dologging && reallyDoLogging) {
         afterResponseTM = System.currentTimeMillis();
         sessionMgr.log("OtLog: "        + sessionMgr.getEdgeId()           +
                        " tottm:"        + (afterResponseTM-topTM)          +
                        " proctm:"       + (afterProcessTM-beforeProcessTM) +
                        " resptm:"       + (afterResponseTM-afterProcessTM));
      }
   }
   
  // Manage incoming Control message, and any that may be queued, and are
  //  in order. SessionManager takes care of the ordering.
   static public void handleControlCommand(TunnelMessage tm, 
                                           HttpTunnelSession sessionMgr) {
      while(tm != null) { 
         sessionMgr.incrControlIn(tm.getLength());
         
         if (DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG2, 
                               "RFCMP: Got control command!");
         }
         
         try {
         
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
                  DebugPrint.println(DebugPrint.DEBUG2, 
                                     "SM: Got a double CntlCmd!\n");
               }
               
               ConfigFile cf = 
                  new ConfigFile(new ByteArrayInputStream(buf, ofs, i-ofs));
            
            
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG2, cf.toString());
               }
               
               String cmd = cf.getProperty("command");
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
                  sessionMgr.cleanupConnection((byte) earid);
               } else if (cmd.equals("serverping")) {
                  DesktopServlet.tunnelPing(sessionMgr, cf);
               } else if (cmd.equals("compressx")) {
                  sessionMgr.setXCompression(true);
               } else if (cmd.equals("nocompressx")) {
                  sessionMgr.setXCompression(false);
               } else if (cmd.equals("compressall")) {
                  sessionMgr.setCompression(true);
               } else if (cmd.equals("nocompressall")) {
                  sessionMgr.setCompression(false);
               } else if (cmd.equals("ping")) {
                  
                  long mil = System.currentTimeMillis();
                  cf.setLongProperty("serverRecv", mil);
                  sessionMgr.writeControlCommand(cf);
                  DebugPrint.println(DebugPrint.INFO4, 
                                     "Ping - RFCMP Millis = " + mil);
               } else if (cmd.equals("consumption")) {
                  int earid    = cf.getIntProperty("earid", 0);
                  int instid   = cf.getIntProperty("instid", 0);
                  int rate     = cf.getIntProperty("rate", 0);
                  sessionMgr.setConsumptionRate((byte) earid, (byte) instid,
                                                rate);
               } else if (cmd.equals("doconsumption")) {
                  boolean onOff = cf.getBoolProperty("onOff", true);
                  sessionMgr.setDoConsumption(onOff);
               } else if (cmd.equals("xon")) {
                  int earid = cf.getIntProperty("earid", 0);
                  int instid = cf.getIntProperty("instid", 0);
                  sessionMgr.setXonOff(true, (byte) earid, (byte) instid);
               } else if (cmd.equals("xoff")) {
                  int earid = cf.getIntProperty("earid", 0);
                  int instid = cf.getIntProperty("instid", 0);
                  sessionMgr.setXonOff(false, (byte) earid, (byte) instid);
               } else if (cmd.equals("rewrap_token")) {
                  String intoken = cf.getProperty("TOKEN");
                  try {
                     String ret=DesktopServlet.rewrapToken(intoken, 
                                                       sessionMgr.desktopID());
                     if (ret != null) {
                        ConfigObject co = new ConfigObject();
                        co.setProperty("COMMAND", "rewrap_token_resp");
                        co.setProperty("TOKEN", ret);
                        sessionMgr.writeControlCommand(co);
                     } else {
                        DebugPrint.println(DebugPrint.WARN, 
                                   "Got rewrap_token, but token is NULL!");
                     }
                  } catch(Throwable tt) {
                     DebugPrint.println(DebugPrint.WARN,
                                        "Error doing rewrapToken");
                     DebugPrint.println(DebugPrint.WARN, tt);
                  }
               } else if (cmd.equals("setdebug")) {
                  int val = cf.getIntProperty("debug", 0);
                  String dp = DesktopServlet.getDesktopProperty(
                     "edodc.debugpanel");
                  if (dp != null && dp.equalsIgnoreCase("true")) {
                     DebugPrint.setLevel(val);
                  } else {
                     DebugPrint.println(DebugPrint.WARN, 
                                        "Ignore DEBUG set request! dp = " + dp);
                  }
               } else if (cmd.equals("newear")) {
               
                 /* A Client asking for a newear ... now thats something. We
                    will allow the creation of an FTP ear if the user is doing
                    hosting. The target machine will be the same hosting
                    machine. Port is 5555
                 */
                  String targethost = cf.getProperty("targethost");
                  int targetport = cf.getIntProperty("targetport", 0);
                  int earid = cf.getIntProperty("earid", -1);
                  String eartype = cf.getProperty("eartype");
                  boolean tellemoff = true;
                  if (eartype != null && eartype.equalsIgnoreCase("FTP")) {
                     tellemoff = !DesktopServlet.createFTPServiceEar(
                                                  sessionMgr.desktopID(), 
                                                  targethost);
                  } 
                  
                  if (tellemoff) {
                     ConfigObject lco = new ConfigObject();
                     lco.setProperty("COMMAND", "showclient");
                     lco.setProperty("MESSAGE", 
                                     "You are not allowed to connect to the specified service type: " + eartype);
                     sessionMgr.writeControlCommand(lco);
                  }
                  
               } else if (cmd.equalsIgnoreCase("createXChannel")) {
               
                 /* Request from client to create an XChannel. This only makes
                    sense in situations where the X protocol can be properly
                    consumed, like DSH or EDU (the latter is the target for
                    this, allowing Education folks to send Whiteboard down).
                 */
                  String passphrase = cf.getProperty("passphrase");
                  if (!DesktopServlet.setXChannelPassphrase(
                         sessionMgr.desktopID(), passphrase)) {
                     ConfigObject lco = new ConfigObject();
                     lco.setProperty("COMMAND", "showclient");
                     lco.setProperty("MESSAGE", 
                                     "You are not enabled to create an XWindows channel");
                     sessionMgr.writeControlCommand(lco);
                  }
                                                       
               } else if (cmd.equals("earcreated")) {
               
                  int earid = cf.getIntProperty("earid", 0);
                  int localp = cf.getIntProperty("localport", -1);
                  String localh = cf.getProperty("localhost", "localhost");
                  TunnelEarInfo tei = 
                     sessionMgr.getTunnelEarInfo((byte) earid);
                     
                  if (tei != null) {
                     if (localp == -1) {
                        DebugPrint.println(DebugPrint.WARN, 
                                           "earCreated: LocalPort not set !! "
                                           + cf.toString());
                     } else {
                        tei.setLocalPort(localp);
                        if (!tei.isICA()) {
                           DesktopManager mgr = 
                              DesktopManager.getDesktopManager();
                           if (mgr != null) {
                              mgr.setRemotePort(sessionMgr.desktopID(), 
                                                localh,
                                                localp);
                           }
                        }
                     }
                     
                     if (DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.DEBUG, 
                                           "EarCreated: " + tei.toString());
                     }
                     
                  } else {
                     DebugPrint.println(DebugPrint.WARN, 
                                        "EarCreated: Bad earid  = " + earid
                                        + cf.toString());
                  }
               } else if (cmd.equals("dump")) {
                  
                  DebugPrint.println(DebugPrint.INFO, 
                     "--------------------- DumpMe ----------------------");
                  DebugPrint.println(DebugPrint.INFO, sessionMgr.toString());
                  DebugPrint.println(DebugPrint.INFO, 
                     "--------------------- DumpAll ----------------------");
                  DebugPrint.println(DebugPrint.INFO, 
                                     ServletSessionManager.toStringS());
               } else if (cmd.equals("NOOP")) {
                  ;
               } else if (cmd.equals("THRUPUT")) {
                 // Just send it back to client
                  sessionMgr.writeControlCommand(cf);
               } else if (cmd.equals("SERVER-THRUPUT")) {
                  int v = cf.getIntProperty("BYTECOUNT", -1);
                  sessionMgr.thruputTest(v);
               } else if (cmd.equals("shutdown")) {
                  DebugPrint.println(DebugPrint.INFO, 
                                     "Client asks for SHUTDOWN. Doit! " + 
                                     (new Date()).toString());
                  sessionMgr.shutdown();
               } else {
                  DebugPrint.println(DebugPrint.WARN, 
                                     "Command from other side was [" 
                                     + cmd + "]");
               }
            }
         } catch (Exception e) {
            DebugPrint.println(DebugPrint.DEBUG, "Error handling ControlCmd:");
            DebugPrint.println(DebugPrint.DEBUG, e);
         }
         
         sessionMgr.setLastControlRecordNumber(tm.getRecordNumber());
         tm = sessionMgr.getWaitingControlMessage();
      }
   }
}
