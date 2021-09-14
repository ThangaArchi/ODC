package oem.edge.ed.odc.tunnel.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import oem.edge.common.cipher.*;
import oem.edge.ed.odc.cntl.*;

import oem.edge.ed.odc.tunnel.common.*;

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

abstract public class HttpTunnelServlet extends HttpServlet {
   private final static int startPort = 6000;
   private final static int maxPort = 100;
   
  // getCipher ensures we have the same key as Desktop/Mainline
   protected ODCipher cipher = null;
   synchronized ODCipher getCipher() {
      if (cipher == null) {
         cipher = DesktopServlet.copyCipher();
      }
      return cipher;
   }
		
   public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
	  
      processRequest(req, resp);
   }   
   public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
	  
      processRequest(req, resp);
   }   
   abstract protected void doTunnel(HttpServletRequest req, 
                                    HttpServletResponse resp, 
                                    HttpTunnelSession tunnelSession)
      throws IOException, ServletException;
   private void processRequest(HttpServletRequest req, 
                               HttpServletResponse resp)
      throws ServletException, IOException {
	  
      try {
         String pathInfo = req.getPathInfo();
         String qs = req.getQueryString();
		 
         HttpSession sest = req.getSession(false);
         if (sest == null) {
            DebugPrint.printlnd(DebugPrint.INFO3,
                                "Session for tunnel request is Invalid " +
                                req.getRequestURI());
            resp.setStatus(403);
            resp.setContentType("text/plain");
            PrintWriter writer = resp.getWriter();
            writer.println("Session is invalid");
            writer.close();
            return;
         }
         
         String desktopid = "";
         
         desktopid = sest.getId();
         
         if (false && DebugPrint.doDebug()) {
         
         
            String turl = req.getRequestURI();
            
            if (qs != null) {
               turl = turl + "?" + qs;
            }
            
            String didv = (String)sest.getAttribute("did");
            DebugPrint.println(DebugPrint.DEBUG, 
                               "In URL: [" + turl + "]\n" +
                               "DID   : [" + didv + "]\n" +
                               "encode: [" + resp.encodeURL(turl) + "]\n" +
                               "redir : [" + resp.encodeRedirectURL(turl) + 
                               "]");
    
            DebugPrint.println(DebugPrint.DEBUG, 
                               "Servlet path in = " + pathInfo);
         }
         
        /*
         String desktopid = req.getParameter("did");
         if (desktopid == null) {
            int idx = qs.indexOf("did=");
            if (idx == 0 || (idx > 0 && qs.charAt(idx-1) == '&')) {
               int idxend = qs.indexOf('&', idx);
               if (idxend < 0) desktopid = qs.substring(idx+9);
               else            desktopid = qs.substring(idx+9, idxend);
            }
         }
         
         if (desktopid == null) {
            pathInfo = pathInfo.trim();
            StringTokenizer st = new StringTokenizer(pathInfo, "/");
            desktopid = st.nextToken();
         }
        */ 
        
         if (DebugPrint.doDebug()) {
            DebugPrint.println(DebugPrint.DEBUG5,
                               "... DESKTOPID in = " + desktopid);
         }
         
        // validate path first
		 
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktopid, false); 
            
         String token = null;
         if (session != null && DesktopServlet.doTokenChecking()) {
         
            token = req.getParameter("compname");
            
            if (DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG5, 
                                  "Token from getParm = " + token);
            }
            
           // getParameter is BROKEN!
            if (token == null) {
               try {
                  
                  if (DebugPrint.doDebug()) {
                     DebugPrint.println(DebugPrint.DEBUG5, 
                                        "QS = " + qs);
                  }
                                     
                  int idx = qs.indexOf("compname=");
                  if (idx == 0 || (idx > 0 && qs.charAt(idx-1) == '&')) {
                     int idxend = qs.indexOf('&', idx);
                     if (idxend < 0) token = qs.substring(idx+9);
                     else            token = qs.substring(idx+9, idxend);
                  }
               } catch (Throwable tte) { 
                  token = null;
               }
            }
            
            if (token == null) {
               token = "Token not provided!";
               session = null;
            } else if (!token.equals(session.getToken())) {
               try {
                  ODCipherData cd = getCipher().decode(token);
                  if (!cd.isCurrent()) {
                     token = "Token expired!";
                     session = null;
                  } else {
                     String thisToken = cd.getString();
                     if (!thisToken.equals(desktopid)) {
                        token = "Invalid encoded yuk!";
                        session = null;
                     }
                  }
               } catch(DecodeException de) {
                  session = null;
                  token = "Token invalid!";
               }
            }
         } else {
            token = "DesktopID does not map to valid Session";
         }
		 
		 
        /*
          TunnelSocket tunnelSocket = 
          (TunnelSocket)session.getValue("X-server"); 
			
          if (tunnelSocket == null) {
          System.out.println("creating new socket");
          Socket socket = null;
          for (int i=0; i<maxPort; i++) {
          try {
          socket = new Socket(xHost, port); 
          socket.setTcpNoDelay(true);
          System.out.println("socket opened at " 
          + Integer.toString(port));
          break;
          }
          catch (IOException ioe) {
          port++;
          }
          }
				
          if (port >= startPort+maxPort) {
          throw new IOException("Unable to find server port");
          }
			
          tunnelSocket = new TunnelSocket(socket);
          session.putValue("X-server", tunnelSocket);
          }
        */
         if (session == null) {
            resp.setStatus(403);
            resp.setContentType("text/plain");
            PrintWriter writer = resp.getWriter();
            writer.println("Invalid Desktop ID/token = " + desktopid);
            DebugPrint.println(DebugPrint.ERROR,
                               "Invalid desktopid/token => " + 
                               desktopid + '/' + token);
         } else {
            doTunnel(req, resp, session);
         }
      } catch (Throwable e) {
             
         DebugPrint.println(DebugPrint.ERROR, 
                            "HttpTunSrv: Uncaught throwable: ");
         DebugPrint.println(DebugPrint.ERROR, e);
                 
                 
        // shut down sockets?
      }		
   }   
}
