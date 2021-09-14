package oem.edge.ed.odc.tunnel.servlet;

import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.cntl.*;
import oem.edge.ed.odc.util.*;
import oem.edge.common.cipher.*;

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

class HttpTunSesTO extends Timeout {
   String id;
   public HttpTunSesTO(long delta, String inid) {
      super(delta, "HTSTO:" + inid, null);  // null means call Timeout on TO
      id = inid;
   }
   
   public void tl_process(Timeout to) {
      HttpTunnelSession sessionMgr 
         = (HttpTunnelSession)ServletSessionManager.getSession(id, false);
      if (sessionMgr != null) {
         sessionMgr.refreshToken();
      }
   }
}

public class HttpTunnelSession extends SessionManager {
   private Hashtable sessionData;
   private long lastAccess;
   private long created;
      
   private long curid = 0;
   
   private Object userdata = null;
   
   private ODCipher cipher = null;
   private String edgeid = null;
   private String ipaddress = null;
   
   public Object getUserData()         { return userdata; }
   public void   setUserData(Object v) { userdata = v;    }
   
   public HttpTunnelSession() {
	  sessionData = new Hashtable();
	  accessed();
	  created = lastAccess;
	  
	 // System.out.println("HttpTunnSess: HACK! Add TunnelEarInfo for testing");
	 // System.out.println("HttpTunnSess: decathlon:0");
	 // registerTunnelEar(new TunnelEarInfo((byte)1, "cappuccino", 6000));
   }   
   
   public synchronized void refreshToken() {
      if (cipher == null) {
         cipher = DesktopServlet.copyCipher();
      }
      TimeoutManager toMgr = TimeoutManager.getGlobalManager();
      toMgr.removeTimeout("HTSTO:" + desktopID());
      toMgr.addTimeout(
         new HttpTunSesTO(DesktopServlet.LOCAL_CIPHER_TIMEOUT_SECS*500,
                          desktopID()));
      
      String token = null;
      try {
         token = cipher.encode(DesktopServlet.LOCAL_CIPHER_TIMEOUT_SECS,
                               desktopID()).getExportString();
      } catch(CipherException ce) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error encoding new token");
         DebugPrint.printlnd(DebugPrint.ERROR, ce);
         return;
      }
      setToken(token);
      
      ConfigObject cf = new ConfigObject();
      cf.setProperty("command", "newtoken");
      cf.setProperty("token", token);
      writeControlCommand(cf);

   }
   
   public String getEdgeId()           { return edgeid; }
   public void   setEdgeId(String eid) { edgeid = eid;  }
   
   public String getIPAddress()          { return ipaddress; }
   public void   setIPAddress(String ip) { ipaddress = ip;   }
   
   public void currentSendToClientID(long inid) { curid = inid; }
   public long currentSendToClientID()          { return curid; }
   
   
   public Object getSyncServlets() { return messages; }
   
   private void accessed() {
	  lastAccess = new Date().getTime();
   }   
   private Enumeration getKeys() {
	  accessed();
	  return sessionData.keys();
   }   
   public Object getValue(String key) {
	  accessed();
	  return sessionData.get(key);
   }   
   public void putValue(String key, Object value) {
	  sessionData.put(key, value);
	  accessed();
   }   
   public void removeValue(String key) {
	  sessionData.remove(key);
	  accessed();
   }   
            
   public void shutdown() {
   
      HttpTunnelSession session = 
         ServletSessionManager.removeSession(desktopID());
         
      if (session != null) {
      
        // Shutdown the tunnel proper
         super.shutdown();
         
         synchronized(messages) {
            messages.notifyAll();
         }
      }
   }
      
  /*
   
     // Little bit of ring-around-the-rosie ... We always tell desktop
     // to 'destroy'. This prints stats, cleans up DB2, and then tells
     // US to shutdown. By the time we return here from first call, shutdown
     // will already be complete. SessionManager knows, and will not do things
     // twice.
      DesktopManager mgr = DesktopManager.getDesktopManager();
      DebugPrint.println(DebugPrint.INFO, 
                         "HttpTunnelSession: Destroying desktop for key " 
                         + desktopID()
                         + " = " + mgr.destroyDesktop(desktopID()));
      
      ServletSessionManager.removeSession(desktopID());
      synchronized(messages) {
         messages.notifyAll();
      }
      
   }
  */
}
