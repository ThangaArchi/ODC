package oem.edge.ed.odc.cntl;

import java.net.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.dropbox.client.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.ftp.common.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class WebDropbox {
   protected String xfrhost = null;
   protected int    xfrport = -1;
   protected sftpDropbox sftpdbox = null;
   
   public WebDropbox(String host, int port) {
      xfrhost = host;
      xfrport = port;
      sftpdbox = new sftpDropbox(false, false);   // No debug, No stdout redir
      sftpdbox.setClientType("web");
   }
   
   
   public void process(javax.servlet.http.HttpServletRequest req, 
                       javax.servlet.http.HttpServletResponse res,
                       String token, String scope) throws Exception {
                          
     // Parse the scope
      if (scope == null || 
          (scope.toLowerCase().indexOf("webdropbox") != 0 && 
           scope.toLowerCase().indexOf("webox") != 0)) {
         throw new Exception("WebDropbox: Bad SCOPE passed " + scope);
      }
      
      Hashtable items = new Hashtable();
      
      StringTokenizer stok = new StringTokenizer(scope, ":");
      
     // Skip webdropbox prefix
      if (stok.hasMoreTokens()) stok.nextToken();
      
     // Get rest of parms
      while(stok.hasMoreTokens()) {
         items.put(stok.nextToken().toLowerCase(), stok.nextToken());
      }
      
      String op     = (String)items.get("op");
      String pkgS   = (String)items.get("pkgid");
      String fileS  = (String)items.get("fileid");
      long   pkgid  = -1;
      long   fileid = -1;
      
      if (pkgS  == null) pkgS  = (String)items.get("p");
      if (fileS == null) fileS = (String)items.get("f");
      
      if (op == null || (!op.equalsIgnoreCase("download") &&
                         !op.equalsIgnoreCase("d"))) {
         throw new Exception("WebDropbox: Bad opcode " + op);
      }
                          
      if (pkgS == null || fileS == null) {
         throw new Exception("WebDropbox: Bad pkg/file parms" + pkgS + 
                             " " + fileS);
      }
      
     // Will throw exception if not number
      pkgid  = Long.parseLong(pkgS);
      fileid = Long.parseLong(fileS);
      
      sftpdbox.connect(xfrhost, xfrport);
      
      sftpdbox.login(token);
      
      FileInfo finfo = sftpdbox.queryFile(fileid);
      
      Operation operation = sftpdbox.downloadFile(pkgid, fileid);
      
      long totToXfer = operation.getToXfer();
      
      int sz;
      byte buf[] = new byte[32768];
      
      res.setContentType("application/download");
      res.setHeader("Content-Disposition","attachment;filename=\"" + 
                     finfo.getFileName() + "\"");
                     
     // Set content length if its 2GIG or less (setContentLen is Int!!)
      if (totToXfer >= 0 && totToXfer <= 0x7fffffff) {
         res.setContentLength((int)totToXfer);
      } else {
        // Set size directly ... hope this works
         res.setHeader("Content-Length", "" + totToXfer);
      }
      
      OutputStream out = res.getOutputStream();
      while((sz=sftpdbox.readFileData(operation, buf, 0, buf.length)) > 0) {
         out.write(buf, 0, sz);
      }
   }
   
   public void disconnect() {
      try {
         sftpdbox.disconnect();
      } catch(Exception e) {
      }
   }
}
