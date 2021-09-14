package oem.edge.ed.odc.tunnel.servlet;

import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ServletSessionManager {
   private static Hashtable sessionTable = new Hashtable();
	
   static public HttpTunnelSession getSession(String id, boolean create) {
	  HttpTunnelSession session = null;
	  

	  synchronized(sessionTable) {
		 session = (HttpTunnelSession)sessionTable.get(id);
	     if ((session == null) && (create)) {
             
		    DebugPrint.println(DebugPrint.INFO, 
                                       "HttpTunSes: creating new session: " 
                                       + id);
                                       
		    session = new HttpTunnelSession();
		    session.desktopID(id);
		    sessionTable.put(id, session);
                    session.refreshToken();
	     }
	  }
	  return session;
   }   
   static public Enumeration getSessions() {
      return sessionTable.keys();
   }   
   static public HttpTunnelSession removeSession(String id) {
	  HttpTunnelSession session = null;
	  synchronized(sessionTable) {
		 session = (HttpTunnelSession)sessionTable.remove(id);
	  }
	  return session;
   }   
   static public String toStringS() {
	   return sessionTable.toString();
   }   
}
