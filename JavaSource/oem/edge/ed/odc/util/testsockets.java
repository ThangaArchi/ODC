package oem.edge.ed.odc.util;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.net.*;
import java.lang.reflect.*;

// Stick to 1.3
//import javax.net.*;
//import javax.net.ssl.*;

public class testsockets {

   void doClient() throws Exception {
     //SocketFactory sockfac = null;
      if (layer) {
        //sockfac = SocketFactory.getDefault();
         sock = new Socket(machine, port);
         
      } else {
         if (useSSL) {
           //sockfac = SSLSocketFactory.getDefault();
            System.out.println("useSSL only supported for layered");
            System.exit(3);
            //sock = new Socket(machine, port);
         } else {
           //sockfac = SocketFactory.getDefault();
            sock = new Socket(machine, port);
         }
      }
      
     //sock = sockfac.createSocket(machine, port);
      
      if (layer) {
      
         if (useSSL) {
         
           /*
            SSLSocketFactory sockfactory = 
               (SSLSocketFactory)SSLSocketFactory.getDefault();
            sslsock = (SSLSocket)sockfactory.createSocket(sock, 
                                                sock.getLocalAddress().getHostName(), 
                                                sock.getLocalPort(), true);
            sock = sslsock;
            sslsock.setUseClientMode(true);
            sslsock.setNeedClientAuth(false);
            sslsock.startHandshake();
           */
            
            KeystoreInfo ksi = new KeystoreInfo("sftpclient.jks",  "changeit");
            KeystoreInfo tsi = new KeystoreInfo("commontrust.jks", "changeit");
            sock = SSLizer.sslizeSocket(sock, false, true, ksi, tsi);
         }
      }
   }

   void doServer() throws Exception {
      
      ServerSocket socket = null;
     //ServerSocketFactory sockfac = null;
      if (!layer) {
         if (useSSL) {
            System.out.println("useSSL only supported for layered");
            System.exit(3);
         }
        /*
         if (useSSL) sockfac = SSLServerSocketFactory.getDefault();
         else        sockfac = ServerSocketFactory.getDefault();
        */
      } else {
        //sockfac = ServerSocketFactory.getDefault();
      }
      for (int i=0; i<100; i++) {
         try {
           //socket = sockfac.createServerSocket(port, 1);
            socket = new ServerSocket(port, 1);
            port = socket.getLocalPort();
            break;
         } catch (IOException ioe) {
            port++;
         }
      }
   
      System.out.println("Listening on port " + port);
                         
      while (true) {
         try {
            sock = socket.accept();
            if (sock == null) {
               System.out.println("accept: Got a NULL Socket Done!"); 
               break;
            }	
            
            System.out.println("Connected!");
            break;
                
         } catch (IOException e){			
            e.printStackTrace(System.out);
         }
      }
      
      try {socket.close();} catch(Throwable tt) {}
      
      
      if (sock != null) {
         System.out.println("Sock is good");
         
         if (layer) {
            
            System.out.println("Using Layered SSL");
           //OSocket osock = OSocket.socketpair();
            SocketPair sp = new SocketPair();
            System.out.println("Get Allocated Socket");
            Socket s1 = sp.getServerAllocatedSocket();
            System.out.println("Get Connected Socket");
            Socket s2 = sp.getConnectingSocket();
            System.out.println("Link them");
            new ShuttleService(sock, s1).start();
            new ShuttleService(s1, sock).start();
            
            sock = s2;
            
            if (useSSL) {
              // Layered with SSL
            
               KeystoreInfo ksi = new KeystoreInfo("sftpserver.jks",  "changeit");
               KeystoreInfo tsi = new KeystoreInfo("commontrust.jks", "changeit");
               sock = SSLizer.sslizeSocket(sock, true, true, ksi, tsi);
               
              /*
               SSLSocketFactory sockfactory = 
               
                  (SSLSocketFactory)SSLSocketFactory.getDefault();
               sslsock = (SSLSocket)sockfactory.createSocket(s2, 
                                                             sock.getLocalAddress().getHostName(), 
                                                             sock.getLocalPort(), true);
               sock = sslsock;
               sslsock.setUseClientMode(false);
               sslsock.setNeedClientAuth(true);
               sslsock.startHandshake();
               System.out.println("Done with Handshake");
              */
            }
         }
      }
   }
   
   boolean   isClient = true;
   boolean   layer    = false;
   boolean   useSSL   = false;
   String    thefile  = null;
   long      xferLen  = -1;
   Socket    sock     = null;
  //SSLSocket sslsock  = null;
   int       port     = 0;
   String    machine  = null;
   
   void rmain(String args[]) throws Exception {
      if (args.length == 0) {
         isClient = false;
      } else {
         for(int i=0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-ssl")) {
               useSSL = true;
            } else if (args[i].equalsIgnoreCase("-layer")) {
               layer = true;
            } else if (args[i].equalsIgnoreCase("-server")) {
               isClient = false;
            } else if (args[i].equalsIgnoreCase("-file")) {
               thefile = args[++i];
            } else if (args[i].equalsIgnoreCase("-port")) {
               port = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-client")) {
               isClient = true;
               machine = args[++i];
               port    = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-len")) {
               xferLen = Long.parseLong(args[++i]);
               System.out.println("xferLen = " + xferLen);
               thefile = null;
            }
         }
      }
      
      if (isClient) {
         doClient();
         OutputStream os = sock.getOutputStream();
         String buf = "This is the data to send. Isn't it nice?\n";
         System.out.println("Writing data");
         os.write(buf.getBytes());
         System.out.println("Done writing data");
         os.close();
      } else {
         doServer();
         InputStream is = sock.getInputStream();
         byte buf[] = new byte[1024];
         System.out.println("Outside: Reading data");
         while(true) {
            System.out.println("Prior to read");
            int r = is.read(buf, 0, buf.length);
            System.out.println("back from read" + r);
            if (r >= 0) {
               System.out.print(new String(buf, 0, r));
               System.out.flush();
            } else {
               break;
            }
         }
      }
      System.out.println("Exitting");
      sock.close();
   }

   
   public static void main(String args[]) {
      testsockets ts = new testsockets();
      
      try {
         ts.rmain(args);
      } catch(Exception e) {
         System.out.println("Exception was " + e.toString());
         e.printStackTrace(System.out);
      }
   }
}
