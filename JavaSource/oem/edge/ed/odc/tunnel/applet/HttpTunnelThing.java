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

public class HttpTunnelThing {										 
   private String desktopid;
   protected AppletSessionManager sessionMgr = new AppletSessionManager();
   public int init(Applet app) {
      try {
         
         DebugPrint.setInAnApplet(true);
         sessionMgr.applet(app);
         sessionMgr.doCombo(true);
         sessionMgr.doSecure(app.getCodeBase().getProtocol().equals("https"));
         System.out.println("Secure 443 protocol = " + sessionMgr.doSecure());
         String hs = app.getParameter("tunnelwebhost");
         int port = sessionMgr.doSecure() ? 443 : 80;
         if (hs == null) {
            hs = app.getCodeBase().getHost();
            port = app.getCodeBase().getPort();
            if (port != -1) {
               hs = hs + ":" + port;
            }
         }
         sessionMgr.host(hs);
         String combo = app.getParameter("combo");
         if (combo != null && combo.equals("true")) {
            sessionMgr.doCombo(true);
         }
         String did = app.getParameter("desktopid");
         String streaming = app.getParameter("streaming");
         if (streaming != null && streaming.equalsIgnoreCase("true")) {
            sessionMgr.doStreaming(true);
         } else {
            sessionMgr.doStreaming(false);
         }
         localinit(hs, did);
         String earidS = app.getParameter("earid");
         int earid = -1;
         if (earidS != null) {
            try {
               earid = Integer.getInteger(earidS).intValue();
            } catch (Exception e) {
            }
         }
         if (earid == -1) {
            earid = sessionMgr.getNextEarId();
         }
         
        // Create local socket, load ICA with appropriate parms set
         HttpTunnelThread ica = new HttpTunnelThread(sessionMgr, "ica", (byte) earid, "UnknownHost", -1, 0, null);
         ica.informEarCreated();
         
         sessionMgr.registerTunnelEar(ica);
         int lport = ica.getLocalPort();
         System.out.println("Starting ICA local ear = " + lport + " Add code to do showDocument!");
         return earid;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return -1;
   }
   public void localinit(String thost, String did)
      throws IOException {
      
     // TunnelClient just manages the Post/Get threads which are the tunnel. 
     // The desktopid is used on the post/get to identify our entire session. 
     // Individual data items flowing across the post/get describe the 
     // destination port id as a host/port pair as well as an
     // instance in that space.
      
      DebugPrint.setClientSide(true);
     /*
      try {
         com.ibm.net.www.https.SecureGlue.setKeyRing("EDesignKeyring", "0x120163");
      } catch(ClassNotFoundException e) {
         System.out.println("HttpTunnelThing: Yikes ... Keyring not found!");
      } catch(ClassFormatError e) {
         System.out.println("HttpTunnelThing: Class Format Error!! Go with it!");
      }
     */
      
      sessionMgr.addThread(Thread.currentThread());
      desktopid = did;
      sessionMgr.desktopID(did);
      String tunnelHost = thost;
      
      DebugPrint.println("localinit: Tunnel to " + tunnelHost 
                         + " for desktopid = "   + desktopid);
      
     // Build one receiver
      ReceiveFromServer receivePipe = new ReceiveFromServer(sessionMgr, 
                                                            tunnelHost, 
                                                            desktopid, null);
      receivePipe.start();
      sessionMgr.addThread(receivePipe);
      
     // Build multiple posters
      for(int i=0; i < 1; i++) {
         SendToServer sendPipe = new SendToServer(sessionMgr, 
                                                  tunnelHost, 
                                                  desktopid, null);
        //sendPipe.connect();
         sendPipe.start();
         sessionMgr.addThread(sendPipe);
      }
   }               
}
