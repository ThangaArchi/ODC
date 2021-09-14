package oem.edge.ed.odc.tunnel.applet;

import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import oem.edge.ed.odc.tunnel.common.URLConnection2;

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

public class testapp extends Applet {
   static public void main(String argv[]) {
      try {
         byte crlf[] = new byte[2];
         crlf[0] = 13;
         crlf[1] = 10;
         String serverhost = "w3.ibm.com";
         int serverport = 443;
         
         if (argv.length == 0) {
            System.out.println("Usage: testapp host port [URL | URLSSL | SSL]");
            return;
         }
         
         if (argv.length > 0) {
            serverhost = argv[0];
         }
         if (argv.length > 1) {
            try {
               serverport = Integer.parseInt(argv[1]);
            } catch (NumberFormatException nfe) {
            }
         }
         String uri = "/";
         Socket sock = null;
                
         InputStream in = null;
         URL url = null;
         URLConnection connection = null;
                
         System.out.println("Trying server = " + serverhost + " port = " + serverport);
         if (argv.length > 2 && argv[2].indexOf("URL") >= 0) {
            boolean doSecure = argv[2].indexOf("SSL") >= 0;
                   
            System.out.println("Doing URL");
                      
            try {
               url = new URL((doSecure?"https://":"http://") + serverhost + ":" + serverport);
            } catch (MalformedURLException e) {
               e.printStackTrace();
            }
            System.out.println("open");
            connection = new URLConnection2(url);
//            connection = url.openConnection();
           //connection.setDoOutput(true);
           //connection.setDoInput(true);
            System.out.println("cache");
            connection.setUseCaches(false);
            System.out.println("conn");
            connection.connect();
            System.out.println("getin");
            in=connection.getInputStream();
                   
         } else {
            sock = new Socket(serverhost, serverport);
            if (argv.length > 2 && argv[2].equalsIgnoreCase("SSL")) {
               sock = URLConnection2.sslizeSocket(sock, true);
            }
            OutputStream out = sock.getOutputStream();
            in = sock.getInputStream();
            PrintWriter w = new PrintWriter(out);
            w.print("GET " + uri + " HTTP/1.0");
            w.flush();
            out.write(crlf);
            w.print("User-Agent: J_J_MoJo_Processing");
            w.flush();
            out.write(crlf);
            w.print("Connection: Keep-Alive");
            w.flush();
            out.write(crlf);
            w.print("Host: " + serverhost + ":" + serverport);
            w.flush();
            out.write(crlf);
            out.write(crlf);
            out.flush();
         }
         System.out.println("About to read");
         int l;
         byte buf[] = new byte[1024];
         while ((l = in.read(buf)) >= 0) {
            System.out.println("Got len=" + l);
            System.out.write(buf, 0, l);
         }
      } catch (IOException e) {
         e.printStackTrace();
        // put up message?  clean up?
      }
      return;
   }
}
