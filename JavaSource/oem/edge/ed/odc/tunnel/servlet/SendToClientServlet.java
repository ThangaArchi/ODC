package oem.edge.ed.odc.tunnel.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ed.odc.tunnel.common.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.cntl.*;
import oem.edge.ed.odc.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004,2006                                */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

class SendToClientTO extends Timeout {
   String id;
   public SendToClientTO(long delta, String inid) {
      super(delta, "STCTO:" + inid, null);  // null means call Timeout on TO
      id = inid;
   }
   
   public void tl_process(Timeout to) {
      SessionManager sessionMgr 
         = ServletSessionManager.getSession(id, false);
      if (sessionMgr != null) {
         DebugPrint.println(DebugPrint.INFO, 
                            "STC: STCTO Timeout: Shutdown id[" + id + "] " +
                            (new Date()).toString());
         sessionMgr.shutdown();
      }
   }
}

public class SendToClientServlet extends HttpTunnelServlet implements Sender {

   static private final int NO_ACTIVITY_SHUTDOWN      = 5 * 60 * 1000;
   static private final int NO_DATA_ACTIVITY_SHUTDOWN = 24 * 60 * 60 * 1000;

   static int noActivityShutdown     = NO_ACTIVITY_SHUTDOWN;
   static int noDataActivityShutdown = NO_DATA_ACTIVITY_SHUTDOWN;
   
  // Spent a few fruitless hours chasing my tail in that
  // WS 3.5 dispatches multiple threads in the same object instance 
  // This should have been obvious, but hey, its a Monday.
   static private long nextid = 1;
   static private Boolean syncit = new Boolean(true);
   static protected long getId() {
      synchronized (syncit) {
         return nextid++;
      }
   }
   
   static public void addTunnelTimeout(String id) {
      TimeoutManager toMgr = TimeoutManager.getGlobalManager();
      toMgr.removeTimeout("STCTO:"+id);
      toMgr.addTimeout(new SendToClientTO(noActivityShutdown, id));
   }
   
  // Fill this in when we support more than 1 sender to client 
   public void bagout() {
      DebugPrint.printlnd(DebugPrint.ERROR, 
                          "ZOINKS! SendToClientServlet told BAGOUT!! Ignore");
   }

   public void doTunnel(HttpServletRequest req, 
                        HttpServletResponse resp, 
                        HttpTunnelSession sessionMgr) 
      throws IOException, ServletException {
	  
      
      resp.setContentType("application/octet-stream");
      
      String docontent = 
         DesktopServlet.getDesktopProperty("edodc.doServletContentLength");
      if (docontent == null || docontent.equalsIgnoreCase("true")) {
         resp.setContentLength(0x7fffffff);
      }
	  
      OutputStream out = resp.getOutputStream();
      out.flush();
      
     // Can't add new senders from server!
      sessionMgr.setAutoAdjustSenders(false);

      long myid = getId();
      int ignsz = 0;
      
      Enumeration replayMessages;
      DebugPrint.println(DebugPrint.INFO4, 
                         "STC: Start of Servlet About to synch " + myid);
      synchronized (sessionMgr.getSyncServlets()) {
      
        // Get our shutdown/timeout values
         try {
            ignsz = Integer.parseInt(
               DesktopServlet.getDesktopProperty("edodc.downFlushSize", "0"));
         } catch(Throwable tt){}
      
        // Get our shutdown/timeout values
         try {
            noActivityShutdown = Integer.parseInt(
               DesktopServlet.getDesktopProperty("edodc.inactivityTimeout"))
               * 1000;
         } catch(Throwable tt){}
            
         try {
            noDataActivityShutdown = Integer.parseInt(
              DesktopServlet.getDesktopProperty("edodc.dataInactivityTimeout"))
              * 1000;
         } catch(Throwable tt){}
   
         sessionMgr.currentSendToClientID(myid);
         
        // JMC 1/16/01 - Add timeout to cleanup aborted startups
         addTunnelTimeout(sessionMgr.desktopID());
         
        // JMC 1/26/01 - TODOTODOTODO
        //
        // If we have saved messages, just replay them If none were lost, then
        //  its a waste of bandwidth, but I'm not adding checking software
        //  today (just too much to do)
        //
        // Biiinnggg 2/5/01 - Added acking from client to remove saved msgs
        //                    Put a govenor in place stop collection of
        //                    data until acks arrive (so not rotating anymore)
         replayMessages = sessionMgr.getSavedMessages();
         boolean hasmore = replayMessages.hasMoreElements();
         if (!hasmore) {
            replayMessages = null;
         }
         DebugPrint.println(DebugPrint.INFO4, 
                            "STC start: " + (new Date()).toString()
                            + " Save Messages = " + hasmore);
      }
      DebugPrint.println(DebugPrint.INFO4, 
                         "STC: Start of Servlet Finished with Sync " + myid);
                         
      sessionMgr.registerSender(this);
      
      int len = 0;
      
     // lastIn will be total (data + control)
      long lastIn   = sessionMgr.getTotIn();
      long lastData = sessionMgr.getDataOut() + sessionMgr.getDataIn();
      long lastTime = System.currentTimeMillis();
      long lastBigTime = lastTime;
      
     // Was 40 has a write delay at times ... till we get a patch, do the nasty
      boolean doWAS40FlushWorkaround = 
         DesktopServlet.getDesktopProperty("edodc.doWAS40FlushWorkaround", 
                                           "FALSE").equalsIgnoreCase("true");
      DebugPrint.printlnd(DebugPrint.INFO4, 
                          "SendToClient: doWAS40FlushWorkaround = " + 
                          doWAS40FlushWorkaround);
      
     // This flag will flop around based on whether we have flushed a NOOP
     //  out or not. If last thing we sent was NOOP, then will be FLASE. This
     //  flag will cause doFlushWorkaround to change accordingly.
      boolean alreadyFlushed = false;
      
      while(len >= 0 && sessionMgr.keepRunning()) try {
         len = 0;
         
         boolean doFlushWorkaround = doWAS40FlushWorkaround && !alreadyFlushed;
         
         SocketInputBuffer inputBuffer = null;
         
         TunnelMessage message = null;
         boolean prob = true;
         try { 
            boolean saveMessage = true;
            
            if (replayMessages != null) {
               saveMessage = false;
               message = (TunnelMessage)replayMessages.nextElement();
               DebugPrint.println(DebugPrint.INFO4, 
                                  "STC: Replaying Message [" + 
                                  message.toString() + "]");
                                  
               if (!replayMessages.hasMoreElements()) {
                  replayMessages = null;
               }
               
            } else {
            
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "STC: Check ACK threshold " + myid);
               }
               
               
              // If we would wait AND we had some acks to flush, 
              //  Make sure we pass back acks to otherside so we don't
              //  deadlock
               if (sessionMgr.wouldWaitOnAckThreshold() && 
                   sessionMgr.flushAcks()) {
                  message = 
                     sessionMgr.getControlBuffer().generateTunnelMessage();
                  if (message != null) {
                     sessionMgr.saveMessage(message);
                  }
               }
               
               if (message == null) {
                  if (!doFlushWorkaround && sessionMgr.waitOnAckThreshold()) {
                     DebugPrint.println(DebugPrint.INFO4, 
                                        "STC: exceeded ACK thresh! Back " 
                                        + myid);
                     
                  } else {
                     
                     int sleeptime = 30000;
                     
                     if (doFlushWorkaround) {
                        sleeptime = 250;
                     } else {
                        alreadyFlushed = false;
                     }
                     
                     if (!doFlushWorkaround ||
                         !sessionMgr.wouldWaitOnAckThreshold()) {
                        
                        if (DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG5, 
                                              "STC: calling getInputBuffer(" + 
                                              sleeptime + ") " + myid);
                        }
                        
                        inputBuffer = sessionMgr.getInputBuffer(sleeptime);
                     } else {
                        if (DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG5, 
                                              "STC: Doing FLUSHWorkaround, and wouldWaitOnACkThreshold");
                        }
                     }
                  }
               }
               
               if (inputBuffer != null) {
                  
                  if (DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "STC: about to Sync after InpAvail " 
                                        + myid);
                  }
                  
                 // Make sure we are the do'er here. If not, just bag out
                  synchronized (sessionMgr.getSyncServlets()) {
                     if (sessionMgr.currentSendToClientID() == myid) {
                        message = inputBuffer.generateTunnelMessage();
                        if (message != null) {
                        
                          /*
                           DebugPrint.println(DebugPrint.INFO2, "STC : " + 
                                              System.currentTimeMillis());
                          */
                        
                           sessionMgr.saveMessage(message);
                        }
                     } else {
                        sessionMgr.addInputBuffer(inputBuffer);
                        DebugPrint.println(DebugPrint.INFO4, 
                                           "STC: " + (new Date()).toString() +
                                           " Another STC has started! Bagout ["
                                           + myid + "] "
                                           + sessionMgr.desktopID());
                        return;
                     }
                  }
                  
                  if (DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "STC: DONE Sync after InpAvail " 
                                        + myid);
                  }
               }
            }
                                 
           // For now, we want to send a NOOP type message every time
           // we have nothing else to say. This should ensure that we 
           // send a message at least every 30 seconds or so. That will
           // keep our health checker alive on the client end in case 
           // server pings are off.
            if (message == null && (doWAS40FlushWorkaround || true) ) {
               alreadyFlushed = true;
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.printlnd(DebugPrint.DEBUG3, 
                                   "STC: Sending WAS40FlushWorkaround FLUSH");
               }
               
               synchronized (sessionMgr.getSyncServlets()) {
                  if (sessionMgr.currentSendToClientID() == myid) {
                     message = 
                        sessionMgr.getControlBuffer().generateIgnoredTunnelMessage(ignsz);
                     if (message != null) {
                        sessionMgr.saveMessage(message);
                     }
                  }
               }
            }
                                 
            if (message != null) {
                              
               message.write(out);
                  
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     "STC write: " + message.toString());
               }
               
               len = message.getLength();
               if (message.getEarId() == 0) {
                  sessionMgr.incrControlOut(message.getOverhead()+len);
               } else {
                  sessionMgr.incrOut(len, message.getAlternateLength());
                  sessionMgr.incrControlOut(message.getOverhead());
               }
            }
            
              /* Old way
               len = inputBuffer.writeToStream(out, "STC");
               DebugPrint.println("SendToClient: wrote " + len);
              */
            prob = false;
               
         } catch (IOException e) {
            DebugPrint.println(DebugPrint.ERROR, "STC: Except ==>");
            DebugPrint.println(DebugPrint.ERROR, e);
         } catch (OutOfMemoryError e) {
            DebugPrint.println(DebugPrint.ERROR, "STC: Except (do GC) ==>");
            DebugPrint.println(DebugPrint.ERROR, e);
            System.gc();
         } 
         
         if (prob) {
            DebugPrint.println(DebugPrint.WARN, 
                               "STC: " + (new Date()).toString() +
                               "  Exception caused exit " +
                               sessionMgr.desktopID());
                               
            synchronized (sessionMgr.getSyncServlets()) {
               if (sessionMgr.currentSendToClientID() == myid) {
               
                 // JMC 1/16/01 - Add timeout to cleanup aborted startups
                  addTunnelTimeout(sessionMgr.desktopID());
                  DebugPrint.println(DebugPrint.WARN, 
                                     "STC:  Inst reattach Timeout");
               } else {
                  DebugPrint.println(DebugPrint.WARN, 
                         "STC: New do'er already running! Just bag out [" 
                                     + myid + "]");
               }
            }
            return;
         } 
             
         if (message == null) {
            ConfigObject cf = new ConfigObject();
            cf.setProperty("command", "NOOP");
            
            if (DebugPrint.doDebug()) {
               DebugPrint.printlnd(DebugPrint.DEBUG3, 
                          "STC: Sending NoActivity Ping");
            }
            
            sessionMgr.writeControlCommand(cf);
         }
         
         long thisTime = System.currentTimeMillis();
         
         if (thisTime >= (lastTime + 60000)) {
            long thisIn = sessionMgr.getTotIn();
            if (thisIn <= lastIn) {
               DebugPrint.println(DebugPrint.INFO, 
                                  "STC: Tunnel has RFC inactivity " 
                                  + (new Date()).toString());
               DebugPrint.println(DebugPrint.INFO, 
                                  "STC: thisTime=" + (thisTime/1000) + 
                                  " lastTime=" + (lastTime/1000)     +
                                  " delta=" + ((thisTime-lastTime)/1000));
               DebugPrint.println(DebugPrint.INFO, 
                                  "STC: thisIn=" + thisIn + 
                                  " lastIn="    + lastIn +
                                  " delta=" + (thisIn-lastIn));
            } else {
            
              // Re-up the timeout
               addTunnelTimeout(sessionMgr.desktopID());
            }
            lastIn = thisIn;
            lastTime = thisTime;
            
            long thisData = sessionMgr.getDataOut() + sessionMgr.getDataIn();
            if (thisData == lastData) {
               if (thisTime >= lastBigTime + noDataActivityShutdown) {
                  DebugPrint.println(DebugPrint.WARN, 
                                     "STC: Tunnel shutting down due to DATA inactivity");
                  try {
                     
                     ConfigObject co = new ConfigObject();
                     co.setProperty("COMMAND", "showclient");
                     co.setProperty("MESSAGE", 
                                    "Connection is being shutdown due to " +
                                    "prolonged inactivity"); 
                     sessionMgr.writeControlCommand(co);
                     
                     
                    // Let other side know we are shutting them down.
                     SocketInputBuffer b = sessionMgr.getInputBufferNoblock();
                     while(b != null) {
                        TunnelMessage msg = b.generateTunnelMessage();
                        if (msg != null) {
                           message.write(out);
                        }
                        b = sessionMgr.getInputBufferNoblock();
                     }
                     Thread.sleep(5000);
                  } catch(IOException ioe) {
                  } catch(InterruptedException ioe) {
                  }
                  sessionMgr.shutdown();
                  break;
               }
            } else {
               lastData = thisData;
               lastBigTime = thisTime;
            }
         }
      } finally {
         sessionMgr.removeSender(this);
         try { out.close(); } catch(IOException e1) {}
      }
      
      if (sessionMgr.keepRunning()) {
      
         String id = sessionMgr.desktopID();
         DebugPrint.println(DebugPrint.WARN, 
                            "SendToClient: Shutdown Tunnel, improper exit!");
         sessionMgr.shutdown();
      }
   }
}
