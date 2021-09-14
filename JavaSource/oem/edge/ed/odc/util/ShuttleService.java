package oem.edge.ed.odc.util;

import java.io.*;
import java.net.*;
import java.lang.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005,2006	   	                         */ 
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
   
/*
 * This class is charged with simply reading the input stream, and writing to the
 *  output stream. 
 */
public class ShuttleService extends Thread {
   InputStream  is = null;
   OutputStream os = null;
   public ShuttleService(InputStream inIs, Socket inSock) throws IOException {
      is = inIs;
      os = inSock.getOutputStream();
   }
   public ShuttleService(InputStream inIs, OutputStream inOs) {
      is = inIs;
      os = inOs;
   }
   public ShuttleService(Socket inSock, OutputStream inOs) throws IOException {
      is = inSock.getInputStream();
      os = inOs;
   }
   public ShuttleService(Socket inSock1, Socket inSock2) throws IOException {
      is = inSock1.getInputStream();
      os = inSock2.getOutputStream();
   }
      
   public void run() {
      try {
         byte buf[] = new byte[16384];
         while(true) {
            int r = is.read(buf);
            if (r > 0) os.write(buf, 0, r);
            else break;
         }
            
         os.close();
      } catch (Exception e) {
        // Don't really care about IOExceptions here ... 
        //System.out.println("readWrite exception: " + e.toString());
        //e.printStackTrace(System.out);
           
         try { os.close(); } catch(IOException ioe) {}

      }
      System.out.println("ShuttleDone");
   }
}
