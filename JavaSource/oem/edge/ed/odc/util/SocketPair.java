package oem.edge.ed.odc.util;

import java.net.*;
import java.io.*;
import java.lang.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006 		                         */ 
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
 * This class provides an analog to the socketpair routine found in Unix. Its 
 *  needed when an interface ONLY operates on a socket, and all you have are
 *  Input/Output streams (launching from inetd, and trying to SSLize the 
 *  connection)
 */
public class SocketPair implements Runnable {
   int state = 0;
   Socket allocatedSocket  = null;
   Socket connectingSocket = null;
   ServerSocket srvsocket  = null;
   public Socket getConnectingSocket() throws IOException {
      
      synchronized(this) {
         
         if (connectingSocket != null) return connectingSocket;
            
         if (state == 0) {
//            System.out.println("gsas: State == 0");
            new Thread(this).start();
//            System.out.println("gsas: Thread started");
            
            while(state < 2) {
//               System.out.println("gsas: state = " + state);
               try {
                  wait();
               } catch(InterruptedException iex) {
               }
//               System.out.println("gsas: back from wait = " + state);
            }
               
            if (state == 2) {
                  
               if (srvsocket != null) {
//                  SocketFactory sockfac = SocketFactory.getDefault();
//                  connectingSocket = 
//                     sockfac.createSocket("localhost",//InetAddress.getLocalHost(), 
//                                          srvsocket.getLocalPort());
                  connectingSocket = new Socket(InetAddress.getLocalHost(),
                                                srvsocket.getLocalPort());
                     
                  while(state < 3) {
//                     System.out.println("gsas: state = " + state);
                     try {
                        wait();
                     } catch(InterruptedException iex) {
                     }
//                     System.out.println("gsas: back from wait = " + state);
                  }
               }
            }
               
               
            if (allocatedSocket != null && connectingSocket != null) {
               return connectingSocket;
            }
         }
         throw new IOException("Error making localhost connection");
      }
   }
      
   public Socket getServerAllocatedSocket() throws IOException {
      getConnectingSocket();
      if (allocatedSocket == null) {
         throw new IOException("Error making localhost connection");
      }
      return allocatedSocket;
   }
      
   public void run() {
      
//      System.out.println("rt: top state = " + state);
      
      state = 1;
      ServerSocket socket = null;
      
// ServerSocketFactory is a 1.4ism ... skip for now
//      ServerSocketFactory sockfac = null;
//      sockfac = ServerSocketFactory.getDefault();
         
      try {
         synchronized(this) {
            state = 2;
//            srvsocket = sockfac.createServerSocket(0, 1);
            srvsocket = new ServerSocket(0, 1);
//            System.out.println("rt: about to notifyAll = " + state);
            notifyAll();
//            System.out.println("rt: done with notifyAll = " + state);
         }
            
//         System.out.println("rt: Before accept = " + state);
         allocatedSocket = srvsocket.accept();
//         System.out.println("rt: Back from accept = " + state);
         state = 3;
            
         srvsocket.close();
         srvsocket = null;
      } catch (IOException ioe) {
         srvsocket = null;
         allocatedSocket = null;
         state = 99;
      }
         
      synchronized(this) {
         notifyAll();
      }
   }
      
  // Decouple the sockets from the object so they are not finalized
   public void decouple() {
      allocatedSocket = null;
      connectingSocket = null;
         
   }
      
   protected void finalize() throws Throwable {
      close();
      super.finalize();
   }
      
  // Close both sockets ... no errors
   public void close() {
      try {
         allocatedSocket.close();
      } catch(Exception e1) {}
      try {
         connectingSocket.close();
      } catch(Exception e1) {}
   }
}
