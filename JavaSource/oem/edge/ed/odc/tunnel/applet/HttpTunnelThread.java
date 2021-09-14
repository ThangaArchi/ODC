package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import java.util.*;
import netscape.security.*;
import com.ms.security.*;
import oem.edge.ed.odc.tunnel.common.*;

public class HttpTunnelThread extends TunnelEarInfo implements Runnable {
   private ServerSocket socket;
   private Thread mythread = null;
   private boolean localShutdown = false;
   
   public HttpTunnelThread(SessionManager cs, String et, byte id, 
                           String remoteHost, int remotePort, 
                           int lPort, String otherinfo) throws java.io.IOException {
						   
      super(cs, et, id, remoteHost, remotePort, otherinfo);
	  
      if (DebugPrint.inAnApplet()) {
         try {
            Class.forName("netscape.security.PrivilegeManager");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Netscape PrivMgr is FOUND");
            }
//		    PrivilegeManager.enablePrivilege("UniversalFileAccess");
	 	 
//		    PrivilegeManager.enablePrivilege("UniversalConnect");
            PrivilegeManager.enablePrivilege("UniversalListen");
            PrivilegeManager.enablePrivilege("UniversalAccept");
// 		    PrivilegeManager.enablePrivilege("SuperUser");		    
         } catch (Exception e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Netscape PrivMgr NOT found or NOT granted");
            }
         }

         try {
            Class.forName("com.ms.security.PermissionID");
            Class.forName("com.ms.security.PolicyEngine");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Explorer PrivInfo is FOUND");
            }
            PolicyEngine.assertPermission(PermissionID.NETIO);
         } catch (Exception e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd(
                  "Explorer PrivInfo is NOT found or NOT granted");
            }
         }		  
      }


      localport = lPort;
      sessionMgr = cs;

     // Run threadless, and without socket if -1 for port. Used in Direct ICA
      if (lPort == -1) {
         return;
      }
	  
      for (int i=0; i<100; i++) {
         try {
            socket = new ServerSocket(localport);
            localport = socket.getLocalPort();
            break;
         } catch (IOException ioe) {
            localport++;
         }
      }
		
      Thread thread = new Thread(this);
      sessionMgr.addThread(thread);
      thread.start();
   }      
      
   public TunnelSocket makeTunnelSocket(Socket sock) throws IOException {
      
      TunnelSocket tunnelSocket = super.makeTunnelSocket(sock);

      sessionMgr.addInputBuffer(tunnelSocket.getInputBuffer());
	
      return tunnelSocket;
   }
   
   public void run() {
     //
     // Job of the tunnel thread is to watch local listen socket for new 
     // connections. When this occurs, create a new TunnelSocket, with 
     // associated id, and register it with the SessionManager
     //
      DebugPrint.printlnd(DebugPrint.INFO, 
                          "Accept thread started. Listening on port " + 
                          getLocalPort());
      mythread = Thread.currentThread();
      if (DebugPrint.inAnApplet()) {
         try {
            Class.forName("netscape.security.PrivilegeManager");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Netscape PrivMgr is FOUND");
            }
//		    PrivilegeManager.enablePrivilege("UniversalFileAccess");
	 	 
//		    PrivilegeManager.enablePrivilege("UniversalConnect");
            PrivilegeManager.enablePrivilege("UniversalListen");
            PrivilegeManager.enablePrivilege("UniversalAccept");
// 		    PrivilegeManager.enablePrivilege("SuperUser");		    
         } catch (Exception e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Netscape PrivMgr NOT found or NOT granted");
            }
         }

         try {
            Class.forName("com.ms.security.PermissionID");
            Class.forName("com.ms.security.PolicyEngine");
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd("Explorer PrivInfo is FOUND");
            }
            PolicyEngine.assertPermission(PermissionID.NETIO);
         } catch (Exception e) {
            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.printlnd(
                  "Explorer PrivInfo is NOT found or NOT granted");
            }
         }		  
      }
	  
      while (sessionMgr.keepRunning() && !localShutdown) {
         try {
            Socket sock = socket.accept();
            if (sock == null) {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                   "TT: Got a NULL Socket SLEEP!"); 
               break;
            }	
                
            TunnelSocket ts = makeTunnelSocket(sock);		
                
            if (isICA()) {
               ts.setCallMainConnComplete(true);
               DebugPrint.printlnd(DebugPrint.INFO, 
                             "ICA Serversock clampdown ... Mission complete");
               mythread = null;
               return;
            } else if (isNewODC()) {
               ts.setCallMainConnComplete(true);
            } else if (isXFR()) {
               ts.setCallMainConnComplete(true);
            } else if (isFDR()) {
               ts.setCallMainConnComplete(true);
            }
                
         } catch (IOException e){			
           // what should we do here?
            if (!localShutdown) {
               DebugPrint.printlnd(DebugPrint.ERROR, e);
            }
         }
      }
      
     // Hmmm ... Jeff Staten's machine seems to hang on this close, but 
     //  since we are our own thread, we should be ok
      try {socket.close();} catch(Throwable tt) {}
   }         
   public void shutdown() {
	
      DebugPrint.printlnd(DebugPrint.INFO, 
                         "Closing listening socket: " + getLocalPort());
      localShutdown = true;
      if (mythread != null && mythread != Thread.currentThread()) {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.println("TT: Shutdown KILL THREAD SLEEP: " + 
                               mythread.toString());
         }
         mythread.interrupt();
      }
	
     /* This was hanging Jeff's machine ... don't know why. Just punt
      try {
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.printlnd("TT: Shutdown About to Close");
         }
         socket.close();
         if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
            DebugPrint.printlnd("TT: Ok, close complete");
         }
      } catch(Throwable e) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "TunnelThread: IOException on shutdown close");
         DebugPrint.println(DebugPrint.DEBUG, e);
      }
     */
      
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.printlnd("TT: calling super shutdown");
      }
      super.shutdown();
      if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
         DebugPrint.printlnd("TT: calling super shutdown");
      }
   }
   public String toString() {
      return "TunnelThread: LocalPort=" + localport + "\n\t" + 
             super.toString();
   }   
}
