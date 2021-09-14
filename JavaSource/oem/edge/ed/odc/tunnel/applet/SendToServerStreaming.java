package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import java.util.*;

import com.ibm.as400.webaccess.common.*;

import oem.edge.ed.odc.tunnel.common.*;

public class SendToServerStreaming extends SendToServer {
   
  // WAS 4.0.x seems to allow ContentLength streaming, but buffers
  //  on 2kish boundry. Well, then, when we have nothing interesting
  //  to say, we write a 3k chaser. Keeps us up to speed!
   int flushsize = 3*1024;
   
   public SendToServerStreaming(AppletSessionManager cs, String host, 
                                String desktopid, String servletcontext) {
      super();
      
      myname = "STSMP" + cs.nextUnique();
      
     // Streaming
      uri = servletcontext + 
         "/servlet/oem/edge/ed/odc/tunnel/ReceiveFromClient/streaming";  
      serverhost = host;
      serverport = cs.doSecure()?443:80;

      int idx = host.lastIndexOf(":");
      if (idx >= 0) {
         try {
            serverport = Integer.parseInt(host.substring(idx+1));
         } catch(NumberFormatException e) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "STSMP: host = " + host + " Has illegal port!");
         }
         serverhost = host.substring(0, idx);
      }
      DebugPrint.printlnd(DebugPrint.INFO4, 
                         "SendToServerMP: Host=[" + serverhost + 
                         "] serverport=" + serverport);
      sessionMgr = cs;
      flushsize = cs.getUpFlushSize();
      setName("STSMP");
   }
   
   void flushAll(OutputStream out) throws IOException {
     // Make sure we are flushed
      sessionMgr.flushAcks();
      
      oem.edge.ed.odc.tunnel.common.SessionManager.SocketSMInputBuffer 
         controlbuf = sessionMgr.getControlBuffer();
         
      sessionMgr.removeBufferFromReserve(controlbuf);
      TunnelMessage message = controlbuf.generateTunnelMessage();
      sessionMgr.saveMessage(message);
      writeMessage(message, out, -1);
      message = controlbuf.generateIgnoredTunnelMessage(flushsize);
      sessionMgr.saveMessage(message);
      writeMessage(message, out, -1);
   }
   
   public void run() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SendToSrvMP: " + myname + ": send thread starting");
      }
        
      byte[] b = new byte[16 * 1024];
      URL url = null;
        
      int errs = 0;
      int connerrs = 0;
      String lastToken = "";
      
     // I am a sender
      sessionMgr.registerSender(this);
      
      while (!askedToBagout && sessionMgr.keepRunning()) {
      
         reallyDoLogging = sessionMgr.getDoLogging();
      
         URLConnection connection = null;
         
         try {
                
            String thisToken = sessionMgr.getToken();
            if (url == null || 
                (thisToken != null && !thisToken.equals(lastToken))) {
            
               try {
               
                  String did = sessionMgr.desktopID();
                  String str = DebugPrint.urlRewrite(uri + "?compname=" 
                                                     + sessionMgr.getToken()
                                                     + "&did=" + did, did);
                     
                //String str = 
                //     DebugPrint.urlRewrite(uri, sessionMgr.desktopID());
                     
                  url = new URL(sessionMgr.doSecure() ? "https" : "http", 
                                serverhost, serverport, str);
                  
                  lastToken = thisToken;
               } catch (MalformedURLException e) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "SendToServerMP: MalformedURL???");
                  DebugPrint.println(DebugPrint.ERROR, e);
               }
            }
            
                        
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrvMP: " + myname + 
                                  ": About to connect");
            }
            if (sessionMgr.doStreaming()) {
            
              // Streaming is now just keepalive with sockets!
               connection = new URLConnection2(url);
               
              // Doing this will get us the actual OutputStream rather than
              //  a local write till finish stream. Potentially add a new
              //  method to URLConnection2 to allow this w/o contentlen set.
              //((URLConnection2)connection).shouldSendContentLength(false);
              /*
              ** The Latest (9/30/03) is that Websphere seems to stream the 
              **  data to the client, as long as enough data is sent. The 
              **  content-length IS required. If not sent, we get into an
              **  expect: 100-continue type thing which I don't yet know
              **  how to deal with. 
              */
              ((URLConnection2)connection).setSendContentLength(0x7fffffff);
              
            } else {
               DebugPrint.printlnd("Streaming is NOT enabled, and I am S2SMP! Need to use streaming. Done");
               return;
            }
                        
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrvMP: " + myname + ": connect done");
            }
            
           // Setup multipart form request
            String oboundry = "1Tef0xD";
           /* Multipart did not help ... omit for now
            connection.setRequestProperty("content-type", 
                                          "multipart/form-data, boundary=" + 
                                          oboundry);
           */

           // Add in some other house keeping info
            connection.setRequestProperty("putname",  myname);
            connection.setRequestProperty("compname", thisToken);
            
            connection.setDoOutput(true);
            connection.setUseCaches(false);                        
            
            OutputStream out = connection.getOutputStream();
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrvMP: " + myname + ": Got Out Buf");
            }
            
               
           // For each set of tunnelmessages sent, we make a new mixed 
           //  multipart section. The thought is httpserver/WAS will allow
           //  this thru to the servlet right away ... AND we can come back
           //  again and again. Hence, upbound streaming
            String iboundry            = "2Tef0xD";
            String iboundrystart       = "--" + iboundry;
            byte iboundrystart_bytes[] = iboundrystart.getBytes();
            String iboundryend   = iboundrystart + "--";
            
            byte contentdisp_bytes[]   = 
               "Content-disposition: attachment; filename=".getBytes();
            byte contenttype_bytes[]   = 
               "Content-type: application/octet-stream".getBytes();
            byte contentencode_bytes[] = 
               "Content-Transfer-Encoding: binary".getBytes();
  
            
            byte CRLF[] = new byte[2];
            CRLF[0] = 13;
            CRLF[1] = 10;
            
            String s;
           /* Multipart did not help ... omit for now
            s="--" + oboundry;
            out.write(s.getBytes());
            out.write(CRLF);
            s="content-disposition: form-data; name=\"tunnelupload\"";
            out.write(s.getBytes());
            out.write(CRLF);
            s="Content-type: multipart/mixed, boundary=" + iboundry;
            out.write(s.getBytes());
            out.write(CRLF);
            out.write(CRLF);
            out.flush();
           */
            
           // Handle resend of non-acked messages. If this is a 2nd/Nth sender 
           //  added, this will cause these messages to be resent in error. 
           //  The servlet side will ignore them
            sessionMgr.getSavedMessages();
            Enumeration replayMessages = sessionMgr.getSavedMessages();
            boolean hasmore = replayMessages.hasMoreElements();
            if (!hasmore) {
               replayMessages = null;
            } else if (!sessionMgr.startingShutdown()){
               if (++errs > 10) {
               
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      " STSMP: " + myname + 
                                      ": Too many connects w/o msg flush (" 
                                      + errs + ") Shutdown tunnel");
                  sessionMgr.shutdown();
                  return;
               }
            }
            
           
            DebugPrint.println(DebugPrint.INFO4, 
                               "STSMP: " + myname + ": start: " + 
                               (new Date()).toString() + 
                               " Save Messages = " + hasmore + 
                               " Errors = " + errs);
            
            connerrs = 0;
            long totlen = 0;
            long totoverhead = 0;
            long pass=1;
            boolean flushed = false;
            
           // Nagel Algo delay value (or 0 to disable)
            int nagelDelay = 60;
            
            while(!askedToBagout && sessionMgr.keepRunning()) {
                  
               int len = 0;
               int uncomplen = 0;
               int overhead = 0;
               
               long callinggetinput = System.currentTimeMillis();
               

               /* Multipart did not help ... omit for now
              // Start new file upload
               out.write(iboundrystart_bytes);
               out.write(CRLF);
               out.write(contentdisp_bytes);
               out.write(("f"+pass++).getBytes());
               out.write(CRLF);
               out.write(contenttype_bytes);
               out.write(CRLF);
               out.write(contentencode_bytes);
               out.write(CRLF);
               out.write(CRLF);
              */
               
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG3, 
                                     "STSMP: " + myname + 
                                     ": getInputBuffer (blocking): " + callinggetinput);
               }
               
              // This is what we want to write
               TunnelMessage message = null;
               
               
              // If we are over on the ack thresh, flushacks, and flush
               if (sessionMgr.wouldWaitOnAckThreshold()) {
                  
                  DebugPrint.printlnd(DebugPrint.DEBUG, "STSMP: wouldWaitOnAck");
                                      
                 // If we just flushed some ACKS, or we are not flushed
                  if (sessionMgr.flushAcks() || !flushed) {
                     DebugPrint.printlnd(DebugPrint.DEBUG, "... Flushing");
                     flushAll(out);
                     flushed = true;
                  } else {
                     DebugPrint.printlnd(DebugPrint.DEBUG, "... NO FLUSH");
                     ;
                  }
               
                 // Now make the blocking call
                  if (sessionMgr.waitOnAckThreshold()) {
                     DebugPrint.println(DebugPrint.INFO4, 
                                        "STSMP: exceeded ACK thresh! Back " 
                                        + myname);
                     continue;
                  }
               }
               
               
              // If we are not replaying this message, we need to save it
               boolean saveMessage = true;
               
              // Resend messages that where not yet acked when I started
              //  this latest connection
               if (replayMessages != null) {
                 // nextElement MAY fail if we have multiple senders. 
                  try {
                     message = (TunnelMessage)replayMessages.nextElement();
                     DebugPrint.println(DebugPrint.INFO4, 
                                        "STC: Replaying Message [" + 
                                        message.toString() + "]");
                  
                     if (!replayMessages.hasMoreElements()) {
                        replayMessages = null;
                     }
                  } catch(Exception e) {}
                  saveMessage = false;
                  flushed = false;
               }
               
//               message = 
//                  sessionMgr.getControlBuffer().
//                  generateIgnoredTunnelMessage(50000);
//               saveMessage = false;
               
               long priortowait  = System.currentTimeMillis();
               
               
              // Nothing so far, 
               if (message == null) {
               
                 /*
                 ** Finally, lets get'er'done
                 */
                 
                 // Get a nagel delayed buffer
                  SocketInputBuffer inputBuffer = null;
//                  DebugPrint.printlnd(DebugPrint.WARN, 
//                                      "getInputBuffer: " + nagelDelay);
                  inputBuffer = sessionMgr.getInputBuffer(nagelDelay);
                  
                 // If no buffer, then we are going to sleep to get it,
                 //  we need to flush
                  if (inputBuffer == null) {
                     if (!flushed) {
                        flushAll(out);
                        flushed = true;
//System.out.print('.');
//                        DebugPrint.printlnd(DebugPrint.WARN, 
//                                            "NULL inputbuf, flush");
                     }
//                     DebugPrint.printlnd(DebugPrint.WARN, 
//                                         "Block getInputBuffer");
                     inputBuffer = sessionMgr.getInputBuffer(24000);
//                     DebugPrint.printlnd(DebugPrint.WARN, 
//                                         "Back blocked getInputBuffer");
                  }
                  
                 // Heartbeat every 24 seconds to prevent socket shutdown
                  if (inputBuffer == null) {
                     ConfigObject o = new ConfigObject();
                     o.setProperty("command", "NOOP");
                     
                     if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                        DebugPrint.println(DebugPrint.DEBUG,
                                           "SendToSrvMP: " + myname + 
                                           ": heartbeat");
                     } else {
//                        DebugPrint.printlnd(DebugPrint.WARN, 
//                                            "HeartBeat");
                        ;
                     }
                  
                     sessionMgr.writeControlCommand(o);
                  
                     sessionMgr.flushAcks();
                  
                     inputBuffer = sessionMgr.getInputBuffer(0);
                  
                     if (dologging && reallyDoLogging) {
                        sessionMgr.log("STSMP: " + myname + ": Adding NOOP");
                     }
                  }
                  
                  if (inputBuffer != null) {
                     message = inputBuffer.generateTunnelMessage();
                  }
               }
               
              // If we write a message here, we are not flushed anymore
               if (message != null) {
                  if (saveMessage) {
                     sessionMgr.saveMessage(message);
                  }
                  writeMessage(message, out, -1);
                  flushed = false;
               }
            }
         } catch (IOException e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, e);
            }
            
            connerrs++;
            if (connerrs >= MAX_RETRY_COUNT) {
               if (!sessionMgr.startingShutdown()) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "STSMP: Too many errors (" 
                                     + connerrs + ") Shutdown tunnel");
                  sessionMgr.shutdown();
               }
               break;
            }
               
            if ((connerrs % 10) == 0 || connerrs == 1) {
               if (!sessionMgr.startingShutdown()) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                     "STSMP: IOException sending to Server. Sleep and retry " + 
                     (MAX_RETRY_COUNT - connerrs));
               }
            }
            
            try {
               Thread.sleep(1000);
            } catch(InterruptedException ett) {}
           // put up message?  clean up?
         } 
      }
      
     // I am OUT-A-HERE
      sessionMgr.removeSender(this);
      URLConnection2.purgeKeepAlives();
   }
   
   
   void writeMessage(TunnelMessage message, 
                     OutputStream out,
                     long priortowait) throws IOException {
                     
      long gotsomedata = System.currentTimeMillis();
      int  overhead    = 0;
      int  len         = 0;
      int  uncomplen   = 0;
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, 
                            "SendToSrvMP: " + myname + 
                            ": Got Data: writeToStream: " + gotsomedata);
      }
                              
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG3, 
                            "STSMP:write("+myname+"): " +
                            message.toString());
      } else {
//         DebugPrint.println(DebugPrint.WARN,
//                            "STSMP:write("+myname+"): " +
//                            message.toString());
         ;
      }
      
      message.write(out);
                                 
      long donewithwrite = System.currentTimeMillis(); 
      
      if (message.getEarId() == 0) {
         overhead  += message.getLength();
      } else {
         len       += message.getLength();
         uncomplen += message.getAlternateLength();
      }
      overhead  += message.getOverhead();
      
      long waitdelta     = gotsomedata-priortowait;
      long writedelta    = donewithwrite-gotsomedata;
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG2, 
                            "SendToSrvMP: " + myname + 
                            ": Done with Write: " + writedelta + 
                          (priortowait== -1?(" waitdelt = " + waitdelta):"") +
                            " len=" + (len+overhead));
//      } else {
//        DebugPrint.printlnd(DebugPrint.WARN, "Done with Write: " + 
//         writedelta + " waitdelt = " + waitdelta);
         ;
      }
      
      
      if (priortowait == -1) waitdelta = 0;
      
     // FIX THIS
      sessionMgr.adjustTunnelSendRate(writedelta,
                                      len+overhead, 
                                      waitdelta, 0);
      
      sessionMgr.incrControlOut(overhead);
      sessionMgr.incrOut(len, uncomplen);
   }

   
   public String toString() {
      return "SendToSrvMP: " + super.toString();
   }   
}
