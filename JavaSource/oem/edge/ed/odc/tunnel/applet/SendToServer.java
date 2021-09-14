package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import java.util.*;

import com.ibm.as400.webaccess.common.*;

import oem.edge.ed.odc.tunnel.common.*;

public class SendToServer extends Thread implements Sender {
   protected AppletSessionManager sessionMgr;
   protected String uri;
   protected String serverhost;
   protected String myname;
   protected Socket sock = null;
   protected int serverport = -1;
   protected static final boolean dologging = true;
   protected boolean reallyDoLogging = false;
   protected boolean askedToBagout = false;
      
   protected int MAX_RETRY_COUNT = 60*5;    // Retry for up to 5 minutes
   
   public SendToServer() {
   }
   
   public SendToServer(AppletSessionManager cs, String host, 
                       String desktopid, String servletcontext) {
      
      myname = "STS" + cs.nextUnique();
      uri = servletcontext + "/servlet/oem/edge/ed/odc/tunnel/ReceiveFromClient";
      serverhost = host;
      serverport = cs.doSecure()?443:80;

      int idx = host.lastIndexOf(":");
      if (idx >= 0) {
         try {
            serverport = Integer.parseInt(host.substring(idx+1));
         } catch(NumberFormatException e) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "STS: host = " + host + " Has illegal port!");
         }
         serverhost = host.substring(0, idx);
      }
      DebugPrint.printlnd(DebugPrint.INFO4, 
                         "SendToServer: Host=[" + serverhost + 
                         "] serverport=" + serverport);
      sessionMgr = cs;
      setName("STS");
   }
   
   public void bagout() {
      DebugPrint.println(DebugPrint.INFO5, "SendToServer asked to bagout: " + toString());
      askedToBagout = true;
   }
   
   public void run() {
   
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.println(DebugPrint.DEBUG5, 
                            "SendToSrv: " + myname + ": send thread starting");
      }
      
      byte[] b = new byte[16 * 1024];
      URL url = null;
        
      Vector messages = new Vector();
      int errs = 0;
      int connerrs = 0;
      String lastToken = "";
      
      long toploop = 0, housedone = 0, gotoutstream = 0, gotsomedata = 0;
      long alldatawritten = 0, readresponse = 0, parsedresponse = 0;
      long callinggetinput = 0, backfromgetinput = 0, totlen = 0,
         totoverhead=0;
      
     // I am a sender
      sessionMgr.registerSender(this);
      
     // Don't bag out until we have 0 unsent messages.
      while ((!askedToBagout || messages.size() != 0) && 
             sessionMgr.keepRunning()) {
      
         reallyDoLogging = sessionMgr.getDoLogging();
      
//         if (dologging && reallyDoLogging) {
         toploop = System.currentTimeMillis();
//         }
         
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
                                     "SendToServer: MalformedURL???");
                  DebugPrint.println(DebugPrint.ERROR, e);
               }
            }
            
                        
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrv: " + myname + 
                                  ": About to connect");
            }
            if (sessionMgr.doStreaming()) {
            
              // Streaming is now just keepalive with sockets!
               connection = new URLConnection2(url);
               
               
              //((URLConnection2)connection).setSendContentLength(0x7fffffff);
              
            } else {
               connection = url.openConnection();
            }
                        
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrv: " + myname + ": connect done");
            }
            
           // JMC 5/23/01 - Needed to bypass (potential) WAS bug with parms
            connection.setRequestProperty("content-type", 
                                          "application/octet-stream");
                                          
            connection.setRequestProperty("putname",  myname);
            connection.setRequestProperty("compname", thisToken);
            
            
           /*DEBUG@!!!!!!
            connection.setRequestProperty("dodirect", "yes");
           */
            
            connection.setDoOutput(true);
            connection.setUseCaches(false);                        
            
            if (dologging && reallyDoLogging) {
               housedone = System.currentTimeMillis();
            }
                     
            OutputStream out = connection.getOutputStream();
            
            if (dologging && reallyDoLogging) {
               gotoutstream  = System.currentTimeMillis();
            }
            
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrv: " + myname + ": Got Out Buf");
            }
            
               
           /* DEBUG
            {
               out.close();
               InputStream in = connection.getInputStream();
               int inr = 0;
               int totr = 0;
               while((inr = in.read(b, totr, b.length-totr)) > 0) {
                  totr += inr;
               }
               
               String instr = new String(b, 0, totr);
               System.out.println("Return value => " + instr);
               int idx = instr.indexOf('=');
               int port = -1;
               if (idx > 0) {
                  instr = instr.substring(idx+1);
                  System.out.println("DIRECTPORT value = " + instr);
                  idx = instr.indexOf("\n");
                  if (idx > 0) {
                     instr = instr.substring(0, idx);
                  }
                  try {
                     port = Integer.parseInt(instr);
                     System.out.println("portnum = " + port);
                  } catch(NumberFormatException e) {
                     System.out.println("Bad number!");
                     ;
                  }
               } else {
                  System.out.println("Error finding DIRECTPORT value");
               }
               
               Socket socket = new Socket(serverhost, port);
               System.out.println("Nagel = " + socket.getTcpNoDelay());
               socket.setTcpNoDelay(true);
               out = socket.getOutputStream();
               System.out.println("Got DIRECT Output Socket = " + port);
            }
               
            DebugPrint.setDebug(255);
           */
               
            
           // Handle resend of failed messages
            sessionMgr.getNonAckMessages(messages);
            
           // The errs > 500 check added because of a specific instance of
           //  a runaway client. We recycled WAS, and this particular client
           //  just spun forever in our big loop, printing out messages != 0
           //  Only way that could occur is if startingShutdown was true
           //  BUT ... if that was true, the tunnel should have shutdown 
           //  2 seconds later (AppletSessionManager 2 phase shutdown). Must
           //  have gotten an exception in the TimeoutManager thread so we
           //  never got the callback for the 2nd phase shutdown. The messages
           //  retreived from the ill client confirm that when Services ->
           //  disconnect was called, it completed the 2nd phase.
           // 
           //  Code was added to TimeoutManager to handle exceptions better,
           //  but just in case, will limit 500 iterations
            if ((messages.size() != 0 && !sessionMgr.startingShutdown()) ||
                errs > 500) {
                
               errs++;
               if (errs > 10) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     " STS: " + myname + ": Too many connects w/o msg flush (" 
                                     + errs + ") Shutdown tunnel");
                  sessionMgr.shutdown();
                  return;
               }
               
               callinggetinput  = System.currentTimeMillis();
               backfromgetinput = callinggetinput;
               gotsomedata      = callinggetinput;
               
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                  "STS: " + myname + ": Resending " + messages.size() + 
                                  " tunnel messages! (sleep 1) error " + errs);
               for(int i=0 ; i < messages.size(); i++) {
                  TunnelMessage tm = (TunnelMessage)messages.elementAt(i);
                  DebugPrint.println(DebugPrint.ERROR, 
                                     "tm" + i + ": " + tm.toString());
                  tm.write(out);
               }
               try {
                  Thread.sleep(1000);
               } catch (InterruptedException et) {
               }
               
               sessionMgr.flushAcks();
            } else if (messages.size() != 0) {
               errs++;
            } else {
               errs = 0;
            }
            
            connerrs = 0;
            totlen = 0;
            totoverhead = 0;
            while(!askedToBagout && 
                  sessionMgr.keepRunning() && 
                  messages.isEmpty()) {
                  
               int len = 0;
               int uncomplen = 0;
               int overhead = 0;
               
//               if (dologging && reallyDoLogging) {
               callinggetinput = System.currentTimeMillis();
//               }
               
               if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG3, 
                                     "STS: " + myname + 
                                     ": getInputBuffer (blocking): " + callinggetinput);
               }
               
               SocketInputBuffer inputBuffer = 
                  sessionMgr.getInputBuffer(24000);
                  
//               if (dologging && reallyDoLogging) {
               backfromgetinput = System.currentTimeMillis();
//               }
               
                           
              // Heartbeat every 24 seconds to prevent socket shutdown
               if (inputBuffer == null) {
                  ConfigObject o = new ConfigObject();
                  o.setProperty("command", "NOOP");
                  
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG,
                                        "SendToSrv: " + myname + 
                                        ": heartbeat");
                  }
                  
                  sessionMgr.writeControlCommand(o);
                  
                  sessionMgr.flushAcks();
                  
                  inputBuffer = sessionMgr.getInputBuffer(0);
                  
                  if (dologging && reallyDoLogging) {
                     sessionMgr.log("STS: " + myname + ": Adding NOOP");
                  }
                  
               }
               
//               if (dologging && reallyDoLogging) {
               gotsomedata  = System.currentTimeMillis();
//               }
               
               
               SocketInputBuffer lastinpbuf = inputBuffer;
               while (inputBuffer != null) {
               
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG3, 
                                        "SendToSrv: " + myname + 
                                        ": Got Data: writeToStream: " + gotsomedata);
                  }
                  
                  TunnelMessage message = 
                     inputBuffer.generateTunnelMessage();
                                 
                  if (message != null) {
                              
                    // Co-opted Streaming to be using URLConnection2 to do
                    //  good keepalive
                    // if (!sessionMgr.doStreaming()) 
                    
                     messages.addElement(message);
                                 
                     synchronized(out) {
                                 
                        if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                           DebugPrint.println(DebugPrint.DEBUG3, 
                              "STS:write("+myname+"): " +
                              message.toString());
                        }
                                       
                        message.write(out);
                     }
                  }   
                                 
                                 
                  if (message.getEarId() == 0) {
                     overhead  += message.getLength();
                  } else {
                     len       += message.getLength();
                     uncomplen += message.getAlternateLength();
                  }
                  overhead  += message.getOverhead();
                              
                  if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG3, 
                                        "SendToSrv: " + myname + 
                                        ": Done with write: " + 
                                        System.currentTimeMillis());
                  }
                  inputBuffer = sessionMgr.getInputBuffer(0);
                  if (inputBuffer != null && 
                      (inputBuffer == lastinpbuf || len > (150*1024))) {
                     sessionMgr.addInputBuffer(inputBuffer);
                     inputBuffer = null;
                     break;
                  }
                  lastinpbuf = inputBuffer;
               }
               
               sessionMgr.incrControlOut(overhead);
               sessionMgr.incrOut(len, uncomplen);
               
               totlen = len;
               totoverhead = overhead;
                                          
              // Co-opted Streaming to be using URLConnection2 to do
              //  good keepalive
              // if (!sessionMgr.doStreaming()) break;
              
               break;
            }
                        
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3,
                                  "SendToSrv: " + myname + 
                                  ": Finish Send Now: " + 
                                  System.currentTimeMillis());
            }
            out.flush();
            
            InputStream in = null;
            try {
               out.close();
               
            
              // JMC 10/18/00 - Add some retry code. Expect reply from each put
              //                to contain info about each ear/inst/rec/len
               in = connection.getInputStream();
               
//               if (dologging && reallyDoLogging) {
               alldatawritten = System.currentTimeMillis();
//               }
            } catch (IOException ioe) {
            
              // This is a connection error, so reset err!!
               errs = 0;
               throw ioe;
            }
            
            int inr = 0;
            int totr = 0;
            
           // If we are to bag out after this, don't keep socket.
            if (askedToBagout) {
               ((URLConnection2)connection).setKeepAlive(false);
            }
            
            while((inr = in.read(b, totr, b.length-totr)) > 0) {
               totr += inr;
            }
            
            if (dologging && reallyDoLogging) {
               readresponse = System.currentTimeMillis();
            }
            
            if (true /*!sessionMgr.doStreaming()*/) {
               String instr = new String(b, 0, totr);
               StringTokenizer tokit = new StringTokenizer(instr);
               if (instr.indexOf("Invalid Desktop ID/token =") >= 0) {
                  if (!sessionMgr.startingShutdown()) {
                     DebugPrint.printlnd(DebugPrint.ERROR, "STS: " +
                                        " Serverside knows not who we are. Terminating Session.");
                     sessionMgr.shutdown();
                  }
               }
               try {
                  while(tokit.hasMoreTokens()) {
                     String cmd = tokit.nextToken();
                     if (cmd.equalsIgnoreCase("recv")) {
                        String eidS  = tokit.nextToken();
                        String inidS = tokit.nextToken();
                        String recS  = tokit.nextToken();
                        String lenS  = tokit.nextToken();
                        try {
                           byte eid, inid;
                           short rec;
                           int len;
                           eid  = Byte.parseByte(eidS);
                           inid = Byte.parseByte(inidS);
                           rec  = Short.parseShort(recS);
                           len  = Integer.parseInt(lenS);
                           
                           TunnelMessage tm = null;
                                                                
                           if (sessionMgr.getProtocolVersion() > 1) {
                              tm = new TunnelMessage(eid, inid, rec, 
                                                     null,len);
                           } else {
                              tm = new TunnelMessageV1(eid, inid, rec, 
                                                       null,len);
                           }
                           
                           int idx = messages.indexOf(tm);
                           if (idx >= 0) {
                              TunnelMessage ttm = (TunnelMessage)
                                 messages.elementAt(idx);
                              messages.removeElementAt(idx);
                              if (ttm.getLength() != tm.getLength()) {
                                 DebugPrint.printlnd(DebugPrint.WARN, 
                                                    "STS: Hmmm, lens differ: ["
                                                    + ttm.toString() + "] [" 
                                                    + tm.toString() + "]");
                              }
                           } else {
                              DebugPrint.printlnd(DebugPrint.WARN, 
                                            "STS: TM not in messages! instr: "
                                            + instr + "\n tm: "
                                            + tm.toString());
                              DebugPrint.println(DebugPrint.WARN, 
                                 "     Messages = \n" + messages.toString());
                           }
                        } catch (NumberFormatException e) {
                           if (!sessionMgr.startingShutdown()) {
                              DebugPrint.printlnd(DebugPrint.ERROR, 
                                              "STS: Error parsing TM return");
                           }
                        }
                     } else if (cmd.equalsIgnoreCase("done")) {
                        break;
                     } else {
                        throw new NoSuchElementException("Bad return command");
                     }
                  } 
                  
                  if (messages.size() > 0) {
                     DebugPrint.printlnd(myname + ": Messages.size != 0: " + messages.size());
                  }
                                    
               } catch(NoSuchElementException e) {
                  if (!sessionMgr.startingShutdown()) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                        "Bad return from send: Got " + instr 
                                        + " for " + messages);
                  }
                  throw new IOException("Cleanup messages please");
               }
            }
            
               
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "SendToSrv: " + myname + ": SendComplete");
               if (DebugPrint.doDebug() && inr > 0) {
                  DebugPrint.println(DebugPrint.DEBUG5, 
                                     new String(b, 0, inr));
               }
            }
            
            parsedresponse = System.currentTimeMillis();
            
            long delta    = alldatawritten-gotsomedata;
            long datawait = backfromgetinput-callinggetinput;
            long overhead = ((parsedresponse-toploop) - datawait) - delta;
            
            if (dologging && reallyDoLogging) {
               
               
               sessionMgr.log("STS: " + parsedresponse + ": len:" + totlen +
                              " Reuse Conn:" + 
                              ((URLConnection2)connection).usedKeepAlive() + 
                              " byteovrhed:"   + totoverhead +
                              " timeovrhed:"   + overhead +
                              " sndrcvtime:"  + (parsedresponse-gotsomedata) +
                              " Start:"    + (housedone-toploop) + 
                              " ostrm:"    + (gotoutstream-housedone) +
                              " slept:"    + datawait + 
                              " write:"    + delta +
                              " readack:"  + (readresponse-alldatawritten) + 
                              " parseack:" + (parsedresponse-readresponse) +
                              " Bps:" + (((totlen+totoverhead)*1000)/(delta+1)) + 
                              " EBps:" + ((totlen*1000)/(overhead+delta+1)));
               
            }
            
            sessionMgr.adjustTunnelSendRate(delta+overhead, 
                                            totlen+totoverhead, 
                                            datawait, overhead);
                        
         } catch (IOException e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG, e);
            }
            
            connerrs++;
            if (connerrs >= MAX_RETRY_COUNT) {
               if (!sessionMgr.startingShutdown()) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "STS: Too many errors (" 
                                     + connerrs + ") Shutdown tunnel");
                  sessionMgr.shutdown();
               }
               break;
            }
               
            if ((connerrs % 10) == 0 || connerrs == 1) {
               if (!sessionMgr.startingShutdown()) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                     "STS: IOException sending to Server. Sleep and retry " + 
                     (MAX_RETRY_COUNT - connerrs));
               }
            }
            
            try {
               Thread.sleep(1000);
            } catch(InterruptedException ett) {}
           // put up message?  clean up?
         } 
      }
      
      sessionMgr.saveNonAckMessages(messages);
      
     // I am OUT-A-HERE
      sessionMgr.removeSender(this);
      URLConnection2.purgeKeepAlives();
   }
   
   public String toString() {
      return "SendToSrv: " + super.toString();
   }   
}
