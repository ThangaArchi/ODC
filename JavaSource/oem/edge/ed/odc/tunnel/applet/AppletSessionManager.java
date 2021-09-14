package oem.edge.ed.odc.tunnel.applet;

import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.applet.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

class ASMShutdownTO extends Timeout {
   private SessionManager sm = null;
   public ASMShutdownTO(long delta, SessionManager inSM) {
      super(delta, "ASMSHUTDOWN:" + inSM.desktopID(), null);  // null means call Timeout on TO
      sm = inSM;
   }
   
   public void tl_process(Timeout to) {
      DebugPrint.printlnd("ASM: Local shutdown initiated");
      sm.shutdown();
   }
}

public class AppletSessionManager extends SessionManager {
   protected String viewerpath = "/servlet/oem/edge/edodc/desktop/getviewer";
   protected String icaapppath = null;
   protected boolean zway = false;
   protected boolean doShutdownTimeout = true;
   protected String initpgm   = null;
   protected String tunnelcmd = null;
   protected String xaddr = null;
   protected String logintoken = null;
   
  // UpflushSize used by Streaming sender
   protected int    upFlushSize = 3*1024;
   public  int getUpFlushSize()      { return upFlushSize; }
   public void setUpFlushSize(int v) { upFlushSize = v;    }
   
   public void setZWay   (boolean in)     { zway    = in;  }
   public void setInitProgram(String s)   { initpgm = s;   }
   public void setTunnelCommand(String s) { tunnelcmd = s; }
      
   public String getXDisplayAddress() { return xaddr; }
   
  // This is used by the NON-Streaming method (SendToServer)
   Vector savedmessages = new Vector();
   public void saveNonAckMessages(Vector msgs) {
      synchronized (savedmessages) {
         Enumeration enum = msgs.elements();
         while(enum.hasMoreElements()) {
            savedmessages.addElement(enum.nextElement());
         }
         savedmessages.removeAllElements();
      }
   }
   
   public void saveNonAckMessage(TunnelMessage tm) {
      synchronized (savedmessages) {
         savedmessages.addElement(tm);
      }
   }
   
   public Vector getNonAckMessages(Vector msgs) {
      Vector ret  = msgs;
      if (ret == null) ret = new Vector();
      synchronized (savedmessages) {
         Enumeration enum = savedmessages.elements();
         while(enum.hasMoreElements()) {
            msgs.addElement(enum.nextElement());
         }
      }
      return ret;
   }
   
   
   public void shutdown() {
   
     // JMC 3/5/01 - Do 2 phase shutdown, delay so shutdown ctl cmd is flushed
      if (doShutdownTimeout) {
         startingShutdown(true);
         doShutdownTimeout = false;
         
         DebugPrint.printlnd("ASM: Sending shutdown command");
         
         com.ibm.as400.webaccess.common.ConfigObject o = 
            new com.ibm.as400.webaccess.common.ConfigObject();
         o.setProperty("command", "shutdown");
         writeControlCommand(o);
         
        // JMC 1/16/01 - Add timeout to finish in phase 2
         TimeoutManager toMgr = TimeoutManager.getGlobalManager();
         toMgr.addTimeout(new ASMShutdownTO(2*1000, this));
         return;
      }
   
      super.shutdown();
      
      DebugPrint.printlnd(DebugPrint.INFO, "Shutdown complete");
      
     /* Mike removes the actionListener, so we have a window where this is
         wrong (when another tunnel is being started while the previous is
         being shutdown ... just don't exit.
      if (!DebugPrint.inAnApplet() && actionListener == null) {
         DebugPrint.printlnd("TunnelClient: Exiting");
         System.exit(2);
      }
     */
   }
   
  // Called when ICA connection terminates
   public void mainConnectionComplete() {
      if (!startingShutdown()) {
         DebugPrint.printlnd(
            "ASM: Main connection terminated. Begin Shutdown");
         shutdown();
      }
   }
   
   public void setLoginToken(String s) {
      logintoken = s;
   }
   public String getLoginToken() {
      return logintoken ;
   }
   
   
   public void setViewerPath(String viewpath) {
      if (viewpath != null) viewerpath = viewpath;
   }
   
   public void setICAApplicationPath(String p) { icaapppath = p; }
   public String getICAApplicationPath() { return icaapppath; }
   
   public void handleICALaunch(HttpTunnelThread tt) 
      throws UnknownHostException, MalformedURLException  {
      
      int lport = tt.getLocalPort();
      String myhost = InetAddress.getLocalHost().getHostAddress();
      
      if (tt.isICA() && DebugPrint.inAnApplet()) {
         String token = getToken();
         if (token == null) token = "invalid";
                                       
         String u =       (doSecure() ? "https://" : "http://") + 
                           host() + viewerpath + "?" 
                           + URLEncoder.encode("desktopid") + "=" 
                           + URLEncoder.encode(desktopID()) + "&" 
                           + URLEncoder.encode("compname") + "=" 
                           + URLEncoder.encode(token) + "&" 
                           + URLEncoder.encode("earid") + "=" 
                           + URLEncoder.encode("" + (int) tt.getEarId()) 
                           + "&" + URLEncoder.encode("host") + "=" 
                          //+ URLEncoder.encode("localhost") + "&" 
                           + URLEncoder.encode(myhost) + "&" 
                           + URLEncoder.encode("port") + "=" 
                           + URLEncoder.encode("" + lport);
         
         
         URL url = new URL(DebugPrint.urlRewrite(u, desktopID()));
         
        /*
          + "/" 
          + tt.getEarId() 
          + "/"
         //+ InetAddress.getLocalHost().getHostName()
         +"localhost" 
         + "/" 
         + lport);
        */
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.printlnd(DebugPrint.DEBUG, 
                               "Starting ICA: " + url.toString());
         }
         
         applet().getAppletContext().showDocument(url, doDebug()?"_blank":"_self");
      } else if (tt.isICA()) {
      
         if (zway) {
            DebugPrint.println(
               "Starting ICA, connecting to port " + lport);
            Thread p = new ICAThread(myhost,lport,desktopID());
            p.setName("ICAThread");
            p.start();
         } else {

            try {
               Runtime runtime = Runtime.getRuntime();
               
               /*String appArgs[] = new String[hosting?4:5];
               int i = 0;
               appArgs[i++] = icaapppath;
               appArgs[i++] = "-ica";
               appArgs[i++] = myhost;
               appArgs[i++] = String.valueOf(lport);
               
              // If Hosting, don't send in a desktop ID
               if (!hosting) {
                  appArgs[i++] = initpgm;
               } */

               Properties props = System.getProperties();
               Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
               int h, w;
               
               h = ss.height - 30;
               w = ss.width  - 20;
               if (w > 1200) w = 1200;
               if (h > 1000) h = 1000;
               
               String[] appArgs = new String[2];
               appArgs[0] = "ICA";
               appArgs[1] = "ClassParms=\"" + "-address:" + myhost + 
                            " -ICAPortNumber:" + String.valueOf(lport) +
                            " -width:" +w + " -height:" + h;
                            
              // " -Border:off -Start:auto -End:manual " +
              // " -user.wfclient.keyboardlayout:US -EndSessionTimeout:0" +
              // " -width:" + w + " -height:" + h; // + " -desiredColor:4";

               if (tunnelcmd.equalsIgnoreCase("EDU")) {
                  appArgs[1] += " -title:Classroom_service"; // + tt.getHost();
                  initpgm = null;
               } else if (tunnelcmd.equalsIgnoreCase("DSH")) {
                  appArgs[1] += " -title:Hosting_service"; // + tt.getHost();
                  initpgm = null;
               } else {
                  appArgs[1] += " -title:Conferencing_service";
               }
               
               if (initpgm != null) {
                  appArgs[1] += " -InitialProgram:" + initpgm;
               }

               appArgs[1] += "\"";
               
               DebugPrint.printlnd(DebugPrint.INFO5, "Executing " + appArgs[0] + " " + appArgs[1]);
               // Process p = runtime.exec(appArgs);
               Process p = runtime.exec(icaapppath);
               PrintStream in = new PrintStream(p.getOutputStream());

               for (int i = 0; i < appArgs.length; i++)
                   in.println(appArgs[i]);
               in.close();

               InputStream err = p.getErrorStream();
               InputStream out = p.getInputStream();
               Thread errRun = new Thread(new PipeReader("StdErr",err));
               Thread outRun = new Thread(new PipeReader("StdOut",out));
               errRun.setName("StdErrPipeReader - ICALaunch");
               errRun.start();
               outRun.setName("StdOutPipeReader - ICALaunch");
               outRun.start();
               
         
            } catch(IOException e) {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Error starting ICA! Fix!");
            }
         }
      } else if(tt.isIM()){
         System.out.println("Dont know parameters for startds.exe....in IM.....");
      } else if(tt.isRM()) {
         String RealURL="C://Program Files/Real/RealPlayer/realplay.exe "+"rtsp://localhost:"+tt.getLocalPort()+"/"+tt.getOtherInfo();
         System.out.println(RealURL+"...in RM....."+tt.getOtherInfo().trim());
         try{
            Process p =Runtime.getRuntime().exec(RealURL);
         }
         catch(Exception e){}
         
      } else if (tt.getEarType().equalsIgnoreCase("X")) {
         fireActionEvent("XEarCreated");
         xaddr="localhost:" + (tt.getLocalPort()-6000);
      }
   }

   public void handleLaunch(TunnelEarInfo tt) 
      throws UnknownHostException, MalformedURLException  {
      
      if (tt.isICA()) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchICA",tunnelcmd,initpgm,tt));
      } else if(tt.isIM()){
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchIM",tt));
         //System.out.println("Dont know parameters for startds.exe....in IM.....");
      } else if(tt.isRM()) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchRM",tt));
      } else if(tt.isFTP()) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchFTP",tt));
      } else if(tt.isXFR()) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchXFR",tt));
      } else if(tt.isFDR()) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchFDR",tt));
      } else if (tt.getEarType().equalsIgnoreCase("X")) {
         fireActionEvent("XEarCreated");
         xaddr="localhost:" + (tt.getLocalPort()-6000);
      } else if (tt.getEarType().equalsIgnoreCase("NEWODC")) {
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchNEWODC",tt));
      } else if (tt.getEarType().toUpperCase().startsWith("DESKTOPONCALLSERVICE")) {
         if (tt.getEarType().substring(20).equals("8")) {
            fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"LaunchDOC",tt));
         }
      } else { 
         fireActionEvent(new TunnelEvent(this,ActionEvent.ACTION_PERFORMED,"Launch" + tt.getEarType().toUpperCase(),tt));
      }

//      else {
//         System.out.println("Gak! AppletSM asked to handleLaunch for Unknown eartype: " + tt.toString());
//      }
   }
}
   
