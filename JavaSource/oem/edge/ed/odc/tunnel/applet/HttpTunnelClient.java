package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.*;
import java.awt.event.*;
import java.awt.*;
import netscape.security.*;
import com.ms.security.*;
import oem.edge.ed.odc.tunnel.common.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.util.*;

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

class PingTO extends Timeout {
   private SessionManager sm = null;
   public PingTO(long delta, SessionManager inSM) {
      super(delta, "CLPing:" + inSM.desktopID(), 
            null);  // null means call Timeout on TO
      sm = inSM;
   }
   
   public void tl_process(Timeout to) {
      DebugPrint.printlnd("Queuing PING");
      ConfigFile cf = new ConfigFile();
      long tt = System.currentTimeMillis();
      cf.setProperty("command", "ping");
      cf.setLongProperty("clientSend", tt);
      sm.writeControlCommand(cf);
      
      int pd = sm.getPingDelay();
      if (sm.keepRunning() && pd > 0) {
         TimeoutManager toMgr = TimeoutManager.getGlobalManager();
         toMgr.removeTimeout("CLPing:" + sm.desktopID());
         toMgr.addTimeout(new PingTO(pd, sm));
      }
   }
}


public class HttpTunnelClient extends Applet 
   implements ItemListener, SenderFactory {

   
   class VerifyTO extends Timeout {
      long lastdatain = 0;
      long delta = 0;
      public VerifyTO(long delta) {
         super(delta, "VerifyTO:" + sessionMgr.desktopID(), 
               null);  // null means call Timeout on TO
               
         this.delta = delta;
         if (receivePipe != null) {
            lastdatain = receivePipe.getLastDataInTime();
         }
      }
      
      public void tl_process(Timeout to) {
         DebugPrint.printlnd(DebugPrint.DEBUG, 
                             "HTC:VerifyTO: Checking our tunnelthread health");
         
         if (sessionMgr.keepRunning()) {
         
            
            
            if (receivePipe != null && 
                receivePipe.getLastDataInTime() == lastdatain) {
                
               DebugPrint.printlnd(DebugPrint.INFO, 
                                   "HTC:VerifyTO: Receiver health suspect");
               receivePipe.forceExit();
               receivePipe = null;
            }
         
            if (receivePipe == null) {
               
               DebugPrint.printlnd(DebugPrint.INFO, 
                                   "HTC:VerifyTO: Starting new Receiver");
               
              // Build one receiver
               receivePipe = new ReceiveFromServer(sessionMgr, 
                                                   tunnelHost, 
                                                   sessionMgr.desktopID(),
                                                   servletcontext); 
               receivePipe.start();
               sessionMgr.addThread(receivePipe);
            }
            
            TimeoutManager toMgr = TimeoutManager.getGlobalManager();
            toMgr.removeTimeout("VerifyTO:" + sessionMgr.desktopID());
            toMgr.addTimeout(new VerifyTO(delta));
         }
      }
   }
   
   private byte nextEarId = 1;
  /*
   private Panel mypanel;
   private Frame myframe;
  */
   private String tunnelHost;
   private String desktopid;
   protected AppletSessionManager sessionMgr = new AppletSessionManager();
   protected ReceiveFromServer receivePipe = null;

   private long    lastcycletime = 0;
   private boolean cycledebug    = false;
   private int     lastlevel     = DebugPrint.INFO4;
   
   protected int numsenders = 1;
   
   private String servletcontext = "";
   
   private boolean uploadstreaming = false;
   
   protected boolean inited = false;
            
   public HttpTunnelClient() 
      throws IOException {
   }   
   public HttpTunnelClient(String tunnelHost, String desktopid) 
      throws IOException {
      localinit(tunnelHost, desktopid, "", 1);
      begin();
   }   
   public  byte getNextEarId()	{
      return nextEarId++;	
   }   
   public AppletSessionManager getSessionManager() {
      return sessionMgr;	
   }   
   public  void init()	{
	  
      try {
         
         DebugPrint.setInAnApplet(true);

        /*
         mypanel = this;
        */

         sessionMgr.doSecure(getCodeBase().getProtocol().equals("https"));

         DebugPrint.printlnd("Secure 443 protocol = " + sessionMgr.doSecure());
		 
         String hs = getParameter("redirectHost");
         int port = sessionMgr.doSecure()?443:80;
		 
         if (hs == null){
            hs   = getCodeBase().getHost();
            port = getCodeBase().getPort();
            if (port != -1) {
               hs = hs + ":" + port;
            }
         } else {
            DebugPrint.printlnd("Client redirect to " + hs);
         }

         sessionMgr.host(hs);		 
		 
         String did = getParameter("desktopid");
         String streaming = getParameter("streaming");
         if (streaming != null && streaming.equalsIgnoreCase("true")) {
            sessionMgr.doStreaming(true);
         } else {
            sessionMgr.doStreaming(false);
         }

        // Applet should ALWAYS have this off, as we need socks code to make
        //  things work, and that has been omitted for now
         sessionMgr.doStreaming(false);

         String compname = getParameter("compname");
         if (compname != null) {
            sessionMgr.setToken(compname);
         }
         
         String debugpanel = getParameter("debugpanel");
         if (debugpanel != null && debugpanel.equalsIgnoreCase("true")) {
            sessionMgr.doDebug(true);
         }

         sessionMgr.setViewerPath(getParameter("viewerpath"));

         if (sessionMgr.doDebug()) {
            this.resize(300, 200);
         }
		 
         localinit(hs, did, "", 1);
         begin();

      } catch (Exception e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                            "Error initializing tunnel applet =>");
         DebugPrint.println(DebugPrint.ERROR, e);
      }
   }   
   
   public String getLoginToken() {
      return sessionMgr.getLoginToken();
   }
   
   public void startFTP(String mach) {
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "newear");
      cf.setProperty("eartype", "ftp");
      cf.setProperty("targethost", mach);
      sessionMgr.writeControlCommand(cf);
   }
   
   static String UD = "";
   
   public void forceConnectionRestart() {
      URLConnection2.purgeKeepAlives();
      
      receivePipe.setKeepRunning(false);      
      receivePipe.forceExit();
      receivePipe = new ReceiveFromServer(sessionMgr, 
                                          tunnelHost, 
                                          sessionMgr.desktopID(),
                                          servletcontext); 
      receivePipe.start();
      sessionMgr.addThread(receivePipe);
   }
   
   public void itemStateChanged(ItemEvent e) {

      String lab = e.getItem().toString();

      boolean state = e.getStateChange() == ItemEvent.SELECTED?true:false;
      
     /*
      if (lab.equals("ServerDebug")) {
         ConfigFile cf = new ConfigFile();
         cf.setProperty("command", "setdebug");
         cf.setIntProperty("debug", state?1:0);
         sessionMgr.writeControlCommand(cf);	   
         } else */ 
      if (lab.equalsIgnoreCase("Dump")) {
         System.out.println("--------------- Dump Info ------------------");
         System.out.println(sessionMgr.toString());
         
         String mach = null;
         
         TunnelEarInfo te = null;
         Enumeration enum = sessionMgr.getTunnelEarInfos();
         while(enum.hasMoreElements()) {
            te = (TunnelEarInfo)enum.nextElement();
            if (te.getEarType().equalsIgnoreCase("ICA")) {
               mach = te.getHost();
            }
         }
         
        /*
         ConfigObject cf = new ConfigObject();
         cf.setProperty("command", "dump");
         sessionMgr.writeControlCommand(cf);
        */
      } else if (lab.equalsIgnoreCase("NewSend")) {
         newSender(sessionMgr);
      } else if (lab.equalsIgnoreCase("KeepAlive")) {
         URLConnection2.useKeepAlive(state);
         if (state) {
            System.out.println("Keepalives ON");
         } else {
            System.out.println("Purging KeepAlive pool. Keepalives OFF");
            System.out.println("Number of purged connections = " + 
                               URLConnection2.purgeKeepAlives());
         }
      } else if (lab.equalsIgnoreCase("ForceDrop")) {
         
         System.out.println("Dropping ReceiveFromSrv");
         receivePipe.setKeepRunning(false);
         sessionMgr.setDoLogging(!sessionMgr.getDoLogging());
         DebugPrint.println("Logging set to " + sessionMgr.getDoLogging());
      } else if (lab.equalsIgnoreCase("Debug")) {
         int level = DebugPrint.INFO;
         if (DebugPrint.getLevel() < DebugPrint.DEBUG) 
            level = DebugPrint.DEBUG;
         else if (DebugPrint.getLevel() < DebugPrint.DEBUG2) 
            level = DebugPrint.DEBUG2;
         else if (DebugPrint.getLevel() < DebugPrint.DEBUG3) 
            level = DebugPrint.DEBUG3;
         else if (DebugPrint.getLevel() < DebugPrint.DEBUG4) 
            level = DebugPrint.DEBUG4;
         else if (DebugPrint.getLevel() < DebugPrint.DEBUG5) 
            level = DebugPrint.DEBUG5;
         DebugPrint.setLevel(level);
         DebugPrint.printlnd("New debug level = " + level);
      } else if (lab.equalsIgnoreCase("Verbose")) {
         int level = DebugPrint.INFO;
         String n = "INFO";
         if (state) {
            level = DebugPrint.DEBUG;  n = "DEBUG";
            
            Runtime rt = Runtime.getRuntime();
            long tm = rt.totalMemory();
            long fm = rt.freeMemory();
            System.out.println("   preGC   MemoryUsage Total: " + tm + 
                               " Free: "  + fm + 
                               " Used: "  + (tm-fm));
            rt.gc();
            long ptm = rt.totalMemory();
            long pfm = rt.freeMemory();
            System.out.println("   postGC  MemoryUsage Total: " + ptm + 
                               " Free: "  + pfm + 
                               " Used: "  + (ptm-pfm));
            
            System.out.println("   delta   MemoryUsage Total: " + (ptm-tm) + 
                               " Free: "  + (pfm-fm) + 
                               " Used: "  + ((ptm-pfm)-(tm-fm)));
            
         }
         DebugPrint.setLevel(level);
         DebugPrint.printlnd("New Verbosity level = " + level);
         
      } else if (lab.equalsIgnoreCase("ResetSenders")) {
         sessionMgr.forceSenderSize(1, true);
         DebugPrint.printlnd("SenderInfo reset");
      } else if (lab.equalsIgnoreCase("DumpSenders")) {
         DebugPrint.printlnd(sessionMgr.getSenderInfo());
      } else if (lab.equalsIgnoreCase("AutoAdjustSenders")) {
         sessionMgr.setAutoAdjustSenders(!sessionMgr.getAutoAdjustSenders());
         DebugPrint.printlnd("AutoAdjustSenders = " + 
                             sessionMgr.getAutoAdjustSenders());
      } else if (lab.equalsIgnoreCase("TunnelSendRate")) {
         sessionMgr.setDoTunnelSendRate(!sessionMgr.getDoTunnelSendRate());
         DebugPrint.printlnd("Do Tunnel Send Rate = " + 
                             sessionMgr.getDoTunnelSendRate());
      } else if (lab.equalsIgnoreCase("Ping")) {
      
         sessionMgr.flushAcks();
         sessionMgr.printlog();
      
         ConfigFile cf = new ConfigFile();
         long tt = System.currentTimeMillis();
         cf.setProperty("command", "ping");
         
         cf.setLongProperty("clientSend", tt);
         if (DebugPrint.getLevel() >= DebugPrint.ERROR) {
            cf.setProperty("UD", UD);
            if (UD.length() >= 500) UD = "";
            else                    UD += "Another UD Entry|";
         }
         sessionMgr.writeControlCommand(cf);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
            DebugPrint.printlnd(DebugPrint.DEBUG, 
                               "Sending ping at " + tt);
         } else {
            DebugPrint.printlnd("Queue ping request");
         }
         
      } else if (lab.equalsIgnoreCase("Auto Ping")) {
         if (state) {
            sessionMgr.setPingDelay(10*1000);
            DebugPrint.printlnd("Auto ping enabled");
            TimeoutManager toMgr = TimeoutManager.getGlobalManager();
            toMgr.addTimeout(new PingTO(1, sessionMgr));      
         } else {
            sessionMgr.setPingDelay(-1);
            DebugPrint.printlnd("Auto ping disabled");
         }
      } else if (lab.equalsIgnoreCase("Compression")) {
         
         ConfigFile cf = new ConfigFile();
         if (sessionMgr.getProtocolVersion() > 1) {
            sessionMgr.setXCompression(state);
            sessionMgr.setRemoteXCompression(state);
            sessionMgr.setCompression(state);
            sessionMgr.setRemoteCompression(state);
            System.out.println("Compression " + (state?"ON":"OFF"));
         } else {
            DebugPrint.printlnd("Hey, its silly to change compression. V1!");
         }
      } else if (lab.equalsIgnoreCase("Normalization")) { 
         if (sessionMgr.getProtocolVersion() > 1) {
            ConfigFile cf = new ConfigFile();
            sessionMgr.setDoConsumption(state);
            cf.setBoolProperty("onOff", sessionMgr.getDoConsumption());
            cf.setProperty("command", "doconsumption");
            sessionMgr.writeControlCommand(cf);
            System.out.println("Normalization " + 
                               (sessionMgr.getDoConsumption() ?
                               "Consumption Enabled"         :
                               "Consumptions Disabled"));
         } else {
            DebugPrint.printlnd("Hey, its silly to change normalization. V1!");
         } 
      } else if (lab.equalsIgnoreCase("help") || lab.equals("?")) { 
         DebugPrint.println("-------------------------------------------");
         DebugPrint.println("-               TunnelCommands            -");
         DebugPrint.println("-------------------------------------------");
         DebugPrint.println("dump, newsend, keepalive, forcedrop,");
         DebugPrint.println("debug, verbose, resetsenders, dumpsenders,");
         DebugPrint.println("autoadjustsenders, tunnelsendrate, ping");
         DebugPrint.println("auto ping, compression, normalization");
                  
      } else {
         DebugPrint.printlnd("Unknown event [" + lab + "]");
      }
	
   }   
   
   public void thruputTestUpload(int v) {
      sessionMgr.thruputTest(v);
   }
   
   public void thruputTestDownload(int v) {
      ConfigObject co = new ConfigObject();
      co.setProperty("COMMAND", "SERVER-THRUPUT");
      co.setIntProperty("BYTECOUNT", v);
      sessionMgr.writeControlCommand(co);
   }
   
   public void localinit(String thost, String did, String servletctxt, int numsendersV)
      throws IOException {
	  
     // TunnelClient just manages the Post/Get threads which are the tunnel. 
     // The desktopid is used on the post/get to identify our entire session. 
     // Individual data items flowing across the post/get describe the 
     // destination port id as a host/port pair as well as an
     // instance in that space.

      DebugPrint.setClientSide(true);
      sessionMgr.setName("CLIENT");
      sessionMgr.addThread(Thread.currentThread());
      desktopid = did;
      sessionMgr.desktopID(did);
      sessionMgr.applet(this);
      tunnelHost = thost;
      servletcontext = servletctxt;
      numsenders = numsendersV;
   }
   
   public void begin() throws IOException {
   
      synchronized(this) {
         if (inited) return;
         inited = true;
      }
   
      DebugPrint.printlnd("Starting tunnel: " + 
                          ((numsenders > 1)?"" + numsenders + " senders":""));
         
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.printlnd(DebugPrint.DEBUG, 
                             "localinit: Tunnel to " + tunnelHost 
                             + " for desktopid = "   + desktopid);
      }
      
     // Allows sessionManager to add senders at will
      sessionMgr.setSenderFactory(this);
						 
     // Build one receiver
      receivePipe = new ReceiveFromServer(sessionMgr, 
                                          tunnelHost, 
                                          desktopid, 
                                          servletcontext); 
      receivePipe.start();
      sessionMgr.addThread(receivePipe);
	   
     // Build multiple posters
      for(int i=0; i < numsenders; i++) {
         SendToServer sendPipe;
         if (uploadstreaming) {
            sendPipe = new SendToServerStreaming(sessionMgr, 
                                                 tunnelHost, 
                                                 desktopid, 
                                                 servletcontext);
         } else {
            sendPipe = new SendToServer(sessionMgr, 
                                        tunnelHost, 
                                        desktopid, 
                                        servletcontext);
         }
        //sendPipe.connect();
         sendPipe.start();
         sessionMgr.addThread(sendPipe);
      }
      
      TimeoutManager toMgr = TimeoutManager.getGlobalManager();
      toMgr.removeTimeout("VerifyTO:" + sessionMgr.desktopID());
     // Check tunnel each minute
      toMgr.addTimeout(new VerifyTO(1000*60));
      
     /*
      if (sessionMgr.doDebug()) {
         Checkbox c;
         mypanel.setLayout(new FlowLayout());
         mypanel.add(c=new Checkbox("Debug"));       c.addItemListener(this);
         mypanel.add(c=new Checkbox("ServerDebug")); c.addItemListener(this);
         mypanel.add(c=new Checkbox("Dump"));        c.addItemListener(this);
         mypanel.add(c=new Checkbox("NewSend"));     c.addItemListener(this);
         mypanel.add(c=new Checkbox("ForceDrop"));   c.addItemListener(this);
         mypanel.add(c=new Checkbox("Ping"));        c.addItemListener(this);
      }
     */  
   }   
   
  // Used by SessionManager to create a new send pipe
   public void newSender(SessionManager sm) {
      DebugPrint.println(DebugPrint.INFO2, "Adding new sender: " + 
                         (sessionMgr.getNumSenders()+1));
                         
      SendToServer sendPipe;
      if (uploadstreaming) {
         sendPipe = new SendToServerStreaming(sessionMgr, 
                                              tunnelHost, 
                                              desktopid, 
                                              servletcontext);
      } else {
         sendPipe = new SendToServer(sessionMgr, 
                                     tunnelHost, 
                                     desktopid, 
                                     servletcontext);
      }
      
      sendPipe.start();	        
   }
   
   public static HttpTunnelClient createTunnel(String[] args,ActionListener listener) 
      throws Exception {
      
      URL url            = null;
      String urlString   = null;
      String host        = null;
      int port           = -1;
      String proto       = null;
      String desktopid   = null;
      String loginFile   = "/servlet/oem/edge/ed/odc/desktop/login";
      
      String runica      = null;
      boolean hostingParm= false;
      boolean panel      = false;
      boolean stream     = true;
      String tunnelcommand  = null;
      String hostingmachine = null;
      String project        = null;
      String token          = null;
      
      String servletcontext = "";
      
     // Need this early to create the URL
     //Misc.setKeyRing();
      
      boolean zway = true;
      for (int i=0; i < args.length; i++) {
         
         if (args[i].equalsIgnoreCase("-debug")) {
            try {
               if (i+1 >= args.length) {
                  throw new NumberFormatException();
               }
               DebugPrint.setLevel(Integer.parseInt(args[++i]));
            } catch (NumberFormatException e) {
               System.err.println("Parameter -debug requires a number value");
               throw new Exception("2");
            }
         } else if (args[i].equalsIgnoreCase("-url")) {
            if (i+1 >= args.length) {
               System.err.println(
                  "Parameter -url requires a valid URL value");
               throw new Exception("2");
            }
            urlString = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_tunnelcommand")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -ch_tunnelcommand requires a value");
               throw new Exception("2");
            }
            tunnelcommand = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_hostingmachine")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -ch_hostingmachine requires a value");
               throw new Exception("2");
            }
            hostingmachine = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_project")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -ch_project requires a value");
               throw new Exception("2");
            }
            project = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_token")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -ch_token requires a value");
               throw new Exception("2");
            }
            token = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_port")) {
            try {
               if (i+1 >= args.length) {
                  throw new NumberFormatException();
               }
               port = Integer.parseInt(args[++i]);
            } catch (NumberFormatException e) {
               System.err.println("Parameter -port requires a number value");
               throw new Exception("2");
            }
         } else if (args[i].equalsIgnoreCase("-ch_host")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -host requires a value");
               throw new Exception("2");
            }
            host = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_context")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -ch_context requires a value");
               throw new Exception("2");
            }
            servletcontext = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_loginPath")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -loginPath requires a value");
               throw new Exception("2");
            }
            loginFile = args[++i];
         } else if (args[i].equalsIgnoreCase("-ch_proto")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -proto requires a value");
               throw new Exception("2");
            }
            proto = args[++i];
         } else if (args[i].equalsIgnoreCase("-runica")) {
            if (i+1 >= args.length) {
               System.err.println("Parameter -runica requires a value");
               throw new Exception("2");
            }
            runica = args[++i];
         } else if (args[i].equalsIgnoreCase("-panel")) {
            panel = true;
         } else if (args[i].equalsIgnoreCase("-nostream")) {
            stream = false;
         } else if (args[i].equalsIgnoreCase("-ch_nostream")) {
            stream = false;
         } else if (args[i].equalsIgnoreCase("-nozway")) {
            zway = false;
         } else {
            System.out.println("Bad parm [" + i + "] = '" + args[i] + "'\n");
            System.out.println("Command syntax:");
            System.out.println("  java oem.edge.ed.odc.tunnel.applet.HttpTunnelClient <options>\n");
            System.out.println();
            System.out.print("options:\n\n");
            System.out.println("\t-url     URL  - remote host URL");
            System.out.println("\t-runica  path - fullpath to ica startup");
            System.out.println("\t-ch_proto   [http | https]");
            System.out.println("\t-ch_port    remoteportnum");
            System.out.println("\t-ch_host    remotehostname");
            System.out.println("\t-ch_token   identifier");
            System.out.println("\t-debug   debugmask\n");
            System.out.println("\t-ch_nostream - turn off stayalive\n");
            System.out.println("\t-ch_hostingmachine mach - name of hmach\n");
            System.out.println("\t-ch_project proj        - name of proj\n");
            System.out.println("\t-ch_tunnelcommand cmd - why started\n");
         }
      }
      
      if (DebugPrint.getLevel() > DebugPrint.INFO4) {
         System.out.println("TunnelClient Startup parameters: ");
         for(int kk = 0; kk < args.length; kk++) {
            System.out.println("Args[" + kk + "] = " + args[kk]);
         }
      }
      
      if (tunnelcommand == null) {
         System.err.println("-ch_tunnelcommand MUST be specified");
         throw new Exception("2");
      }
      
      String urlYuk = null;
         
      String commanddesc = "none";
      urlYuk = servletcontext+loginFile;
      if (tunnelcommand.equalsIgnoreCase("ODC")) {
         commanddesc = "Conferencing";
      } else if (tunnelcommand.equalsIgnoreCase("NEWODC")) {
         commanddesc = "Conferencing";
      } else if (tunnelcommand.equalsIgnoreCase("DSH")) {
         hostingParm = true;
         commanddesc = "Hosting";
      } else if (tunnelcommand.equalsIgnoreCase("EDU")) {
         hostingParm = true;
         commanddesc = "Classrooms";
      } else if (tunnelcommand.equalsIgnoreCase("FTP")) {
         commanddesc = "File Tranfer";
      } else if (tunnelcommand.equalsIgnoreCase("IMS")) {
         commanddesc = "Instant Messaging";
      } else if (tunnelcommand.equalsIgnoreCase("STM")) {
         commanddesc = "Real Media";
      } else if (tunnelcommand.equalsIgnoreCase("XFR")) {
         commanddesc = "2-Way File Transfer";
      } else if (tunnelcommand.equalsIgnoreCase("FDR")) {
         commanddesc = "Grid Submission";
      } else if (tunnelcommand.equalsIgnoreCase("DOC")) {
         commanddesc = "DesktopOnCall";
      } else {
         System.err.println("Invalid value for -tunnelcommand: " + 
                            tunnelcommand);
         throw new Exception("2");
      }
      
      try {
      
         HttpTunnelClient client = new HttpTunnelClient();
         
         if (urlString == null) {
            urlString = proto + "://" + host;
            if (port != -1) urlString = urlString + ":" + port;
         } else {
            try {
               int idx = urlString.lastIndexOf("://");
               if (idx >= 0) { 
                  idx = urlString.indexOf("/", idx+3);
                  if (idx >= 0) {
                     servletcontext = urlString.substring(idx);
                     urlString = urlString.substring(0, idx);
                  }
               }
            } catch(Exception ee) {}
         }
         
         if (servletcontext == null) {
            servletcontext = "";
         }
         
         
         urlYuk +=  "?" + URLEncoder.encode("compact-login") 
                        + "=" 
                        + URLEncoder.encode("true");         
                        
         urlYuk +=  "&" + URLEncoder.encode("tunnelprotover") 
                        + "=" 
                        + URLEncoder.encode(
                              "" + client.sessionMgr.getProtocolVersion()); 
                        
                              
         if (token != null) {
            urlYuk += "&" + URLEncoder.encode("compname") 
                          + "=" 
                          + URLEncoder.encode(token);
         } else {
            DebugPrint.printlnd(DebugPrint.ERROR, 
               "HttpTunnelClient: No token specified for connect!");
         }
         
         if (hostingmachine != null) {
            urlYuk += "&" + URLEncoder.encode("HOSTINGMACHINE") 
                          + "=" 
                          + URLEncoder.encode(hostingmachine);
         }
         
        // Note, project value is already URLEncoded
         if (project != null) {
            urlYuk += "&" + URLEncoder.encode("PROJECT") 
                          + "=" 
                          + project;
         }
         
         if (DebugPrint.isEnabled) {
            DebugPrint.printlnd(DebugPrint.DEBUG, 
                               "About to create URL = " + 
                                urlString + servletcontext + urlYuk);
         }
         
         url = new URL(urlString + servletcontext + urlYuk);
         
         client.sessionMgr.setICAApplicationPath(runica);
         client.sessionMgr.addActionListener(listener);
         
         client.sessionMgr.setZWay(false /*zway*/); // never use zway anymore
         client.sessionMgr.setHosting(hostingParm);
         client.sessionMgr.setTunnelCommand(tunnelcommand);
         
         if (token != null) {
            client.sessionMgr.setLoginToken(token);
            client.sessionMgr.setToken(token);
         }
         
        /*
         client.myframe = new Frame();
         client.mypanel = new Panel();
         client.myframe.add("Center", client.mypanel);
        */
         
         client.sessionMgr.doDebug(panel);
         
         DebugPrint.setInAnApplet(false);
         
         client.sessionMgr.doSecure(url.getProtocol().equals("https"));
         
         DebugPrint.printlnd("Secure 443 protocol = " + 
                            client.sessionMgr.doSecure());
         
         byte buf[] = new byte[32*1024];
            
         String auth = System.getProperty("proxyAuth");
         
         ConfigFile cfg = new ConfigFile();
         int FTPSENDERS = 4;
         boolean uploadstreamingAllowed = true;
         int upflushsz = -1;
         try {
            cfg.load("edesign.ini");
            
            if (auth == null) {
               auth = cfg.getProperty("proxyAuth");
            }
            int dbg = cfg.getIntProperty("debug", -1);
            if (dbg >= 0) DebugPrint.setLevel(dbg);
            
            boolean ka = cfg.getBoolProperty("keepalive", true);
            URLConnection2.useKeepAlive(ka);
            DebugPrint.println(DebugPrint.DEBUG, "Keepalive = " + ka);
            
            
            int rwin = cfg.getIntProperty("recvwindow", -1);
            int swin = cfg.getIntProperty("sendwindow", -1);
            FTPSENDERS = cfg.getIntProperty("FTPSENDERS", FTPSENDERS);
            
            uploadstreamingAllowed = 
               cfg.getBoolProperty("ALLOWUPLOADSTREAMING", true);

            
            upflushsz = cfg.getIntProperty("UPFLUSHSIZE", -1);
            
            int maxsenders = cfg.getIntProperty("MAXSENDERS", 3);
            client.sessionMgr.setMaxSenders(maxsenders);
            
            if (rwin != -1) URLConnection2.setOverrideReceiveWindow(rwin);
            if (swin != -1) URLConnection2.setOverrideSendWindow(swin);
            
            DebugPrint.println(DebugPrint.DEBUG, "Doing Module Debug Setup");
            Hashtable ht = new Hashtable();
            Enumeration penum = cfg.getPropertyNames();
            while(penum.hasMoreElements()) {
               String s = (String)penum.nextElement();
               ht.put(s, cfg.getProperty(s));
               DebugPrint.println(DebugPrint.DEBUG, "  " + s + " = " + 
                                  cfg.getProperty(s));
            }
            DebugPrint.initCheckModules(ht);
            
            DebugPrint.println(DebugPrint.DEBUG, 
                               "Back from Module Setup. Enabled = " + 
                               DebugPrint.getModuleChecking());
            
         } catch (Exception e) {
            ;
         }
         
         if (auth != null) {
            DebugPrint.println(DebugPrint.INFO4, 
                               "Proxy-Authentication = " + auth);
            URLConnection.setDefaultRequestProperty("Proxy-Authorization",
                                                     "Basic " + auth);
            Properties p = System.getProperties();
            p.put("Proxy-Authorization", "Basic " + auth);
            
            System.setProperties(p);
         }
         
        // JMC 9/6/01 - Use URLConnection2 so Proxy support switching 
        //              works
         URLConnection connection = null;
         
         if (stream) {
            int pka = URLConnection2.purgeKeepAlives();
            DebugPrint.printlnd(DebugPrint.DEBUG, 
                               "Purging keepalives: " + pka);
            connection = new URLConnection2(url);
         } else {
            connection = url.openConnection();
         }
         
         connection.setUseCaches(false);
         connection.connect();
         
         InputStream in = connection.getInputStream();
         
         String protoverS = connection.getHeaderField("tunnelprotover");
         if (protoverS == null) {
            DebugPrint.printlnd("My proto version = " + 
                                client.sessionMgr.getProtocolVersion() + 
                                ", using ver 1");
            client.sessionMgr.setProtocolVersion(1);
         } else {
            int protover = -1;
            try {
               protover = Integer.parseInt(protoverS);
            } catch(Throwable tt) {
            }
            if (protover < 1 || 
                protover > client.sessionMgr.getProtocolVersion()) {
               throw new Exception("Bad Protocol match Me: " + 
                                   client.sessionMgr.getProtocolVersion() +
                                   " Srv: " + protover);
            } else {
               if (protover != client.sessionMgr.getProtocolVersion()){
                  DebugPrint.printlnd("Client version = " + 
                                      client.sessionMgr.getProtocolVersion() + 
                                      " Server version = " + 
                                      protover + ". Using server version");
               } else {
                  DebugPrint.printlnd("Protocol Version = " + protover);
               }
               
               client.sessionMgr.setProtocolVersion(protover);
            }
         }
         
        // If we are allowed to do uploadstreaming, lets do so!
         String ts = connection.getHeaderField("uploadstreaming");
         client.uploadstreaming = ts != null && ts.equalsIgnoreCase("true");
         if (!uploadstreamingAllowed && client.uploadstreaming) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Upload streaming is allowed by server, but " +
                                "local installation disallows it!");
            client.uploadstreaming = false;
         }
         
         String upflushS = connection.getHeaderField("upflushsize");
         if (upflushS != null) {
            try {
               int upflushsznew = Integer.parseInt(upflushS);
               if (upflushsz == -1) upflushsz = upflushsznew;
               else {
                  DebugPrint.printlnd(DebugPrint.WARN,
                                      "Upflushsize in edesign.ini overrides " +
                                      " server value (" + upflushsz + " vs " +
                                      upflushsznew + ")");
               }
            } catch(NumberFormatException nfe) {
               DebugPrint.printlnd(DebugPrint.WARN,
                                   "Received non-numerical upflush value of " 
                                   + upflushS);
            }
         }
         
         if (upflushsz > -1) {
            client.getSessionManager().setUpFlushSize(upflushsz);
         }
         
         desktopid = connection.getHeaderField("desktopid");
         token = connection.getHeaderField("compname");
         if (token != null) {
            client.sessionMgr.setToken(token);
         } else {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                      "HttpTunnelClient: No updated token value upon Login!");
         }
         
         String initpgmstr = connection.getHeaderField("InitialProgram");
         client.sessionMgr.setInitProgram(initpgmstr);
            
         String redirectHost = connection.getHeaderField("redirectHost");
         if (redirectHost != null) {
            DebugPrint.printlnd("Redirecting to " + redirectHost);
            proto = url.getProtocol();
            port  = url.getPort();
            urlString = proto + "://" + redirectHost;
            if (port != -1) urlString += ":" + port;
            urlString += servletcontext;
            url = new URL(DebugPrint.urlRewrite(urlString + urlYuk, 
                                                desktopid));
         }
            
         String hs = url.getHost();
         port = url.getPort();
         
         if (port != -1) {
            hs = hs + ":" + port;
         }
         client.sessionMgr.host(hs); 
         
         int totr = 0;
         while(totr < buf.length) {
            int br=in.read(buf, totr, buf.length-totr);
            if (br <= 0) break;
            totr += br;
         }
         
         if (desktopid == null) {
            String emsg = new String(buf);
            if (emsg.indexOf("Token expired!") >= 0) {
               
               DebugPrint.printlnd(DebugPrint.ERROR, 
                             "-----\nERROR: Launch of " + 
                             commanddesc.toLowerCase() +
                             " session failed.\n" +
                             "CAUSE: Design services launch token expired.\n" +
                             "ACTION: Close this window, then Click on the " + 
                             commanddesc.toLowerCase() + 
                             " link to launch again.");
               throw new Exception("token-expired");
            } else {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                  "DesktopID not obtained. Tunnel aborted =>");
               DebugPrint.println(DebugPrint.ERROR, emsg);
            }
            throw new Exception("2");
         }
      
         client.sessionMgr.doStreaming(stream);
         
         boolean xferBased = tunnelcommand.equalsIgnoreCase("FTP") || 
                             tunnelcommand.equalsIgnoreCase("XFR");
         if (xferBased) {
            client.sessionMgr.setAutoAdjustSenders(false);         
            client.sessionMgr.setDoTunnelSendRate(false);         
         } 
         
           // If we are streaming upload, 1 sender, no autoadjust
         if (client.uploadstreaming) {
            FTPSENDERS = 1;
            client.sessionMgr.setAutoAdjustSenders(false);         
         }
         
         client.localinit(client.sessionMgr.host(), desktopid, 
                          servletcontext, (xferBased)? FTPSENDERS : 1);

        /*
         client.myframe.setSize(600,400);
         if (client.sessionMgr.doDebug()) client.myframe.show();
        */
         
         return client;
         
      } catch (Exception e) {
         String m = e.getMessage();
         
         if (m != null && !m.equals("1") && !m.equals("2") && !m.equals("3")) {
         
            if (m.equals("token-expired")) throw e;
            
            DebugPrint.printlnd(DebugPrint.ERROR, 
                               "HttpTunnelClient: Error starting ...");
            DebugPrint.println(DebugPrint.ERROR, e);
         }
         throw e;
      }
   }   
   
   public static void main(String[] args) {
      try {
         createTunnel(args,null).begin();
        
      }
      catch (Exception e) {
         e.printStackTrace();
         if (e.getMessage().equals("2")) {
            System.exit(2);
          }
          else {
            System.exit(3);
	  }
      }
   }   
   
}
