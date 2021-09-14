package oem.edge.ed.odc.dsmp.common;

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

/*
** Test application to get a handle on Java socket performance
**
** And the performance of our Proto Framework
*/
import java.io.*;
import java.net.*;
import java.lang.*;
import oem.edge.ed.odc.tunnel.common.*;

public class testsocket extends DSMPDispatchBase implements Runnable {

   Socket  sock          = null;
   boolean useRealSocket = true;
   boolean useSSL        = false;
   boolean isClient      = false;
   long    xferLen       = 50*1024*1024;
   long    curLen        = 0;
   String  machine       = null;
   int     port          = 4000;
   String  thefile       = null;
   InputStream  fis      = null;
   
  // This is debug/special code added to figure out wht Mozilla upload is
  //  5MB/s while mine is 500KB/s, and IE is 50KB/s
   boolean    uploadtest = false;
   
   public static void main(String args[]) {
      testsocket ts = new testsocket();
      ts.rmain(args);
   }
   public void rmain(String args[]) {
      if (args.length == 0) {
         isClient = false;
      } else {
         try {
            for(int i=0; i < args.length; i++) {
               if        (args[i].equalsIgnoreCase("-realsockets")) {
                  useRealSocket = true;
               } else if (args[i].equalsIgnoreCase("-framework")) {
                  useRealSocket = false;
               } else if (args[i].equalsIgnoreCase("-ssl")) {
                  useRealSocket = true;
                  useSSL = true;
               } else if (args[i].equalsIgnoreCase("-server")) {
                  isClient = false;
               } else if (args[i].equalsIgnoreCase("-file")) {
                  thefile = args[++i];
               } else if (args[i].equalsIgnoreCase("-debug")) {
                  setDebug(true);
               } else if (args[i].equalsIgnoreCase("-uploadtest")) {
                  uploadtest = true;
               } else if (args[i].equalsIgnoreCase("-client")) {
                  isClient = true;
                  machine = args[++i];
                  port    = Integer.parseInt(args[++i]);
               } else if (args[i].equalsIgnoreCase("-len")) {
                  xferLen = Long.parseLong(args[++i]);
                  System.out.println("xferLen = " + xferLen);
                  thefile = null;
               } else {
                  System.out.println("Invalid argument: " + args[i]);
                  System.out.println(
                     "-realsockets          : use real sockets (default)\n"  + 
                     "-framework            : use Proto framework\n"         + 
                     "-client [host] [port] : be the connector\n"            +
                     "-server               : duh! (default)\n"              +
                     "-len                  : Len to xfer (50Meg default)\n" +
                     "-file fname           : Transfer file contents\n");
                  System.exit(4);
               }
            }
         } catch(Exception e) {
            e.printStackTrace(System.out);
         }
      }
      
      
      System.out.println("xferLen = " + xferLen);
      System.out.println("thefile = " + thefile);
      if (!isClient) {
         if (useRealSocket) {
            doRealServer();
         } else {
            doFrameworkServer();
         }
      } else {
         if (useRealSocket) {
            doRealClient();
         } else {
            doFrameworkClient();
         }
      }
   }
   
   final byte UPLOAD            = (byte)1;
   final byte DOWNLOAD          = (byte)2;
   final byte LOAD_COMPLETE     = (byte)3;
   final byte PACKET            = (byte)4;
   
   protected long starttime=0, endtime=0;
   protected boolean gotreply = false;
   protected byte buf[] = new byte[50*1024];
   protected DSMPBaseHandler globalhandler = null;
   
   void doFrameworkClient() {
      
      try {
         
         if (thefile != null) {
            System.out.println("opening file " + thefile);
            File theFile = new File(thefile);
            xferLen = theFile.length();
            try {
               fis = new FileInputStream(thefile);
            } catch(Exception ee) {
               System.out.println("Error opening " + thefile);
            }
         }
         
         DSMPBaseHandler handler = new DSMPSocketHandler(machine, port, this);
         DSMPBaseProto proto = new DSMPBaseProto(UPLOAD, (byte)1, (byte)0);
         proto.appendLong(xferLen);
         handler.sendProtocolPacket(proto);
         
         System.out.println("Upload ...");
            
         starttime = System.currentTimeMillis();
            
         long clen = xferLen;
         while(clen > 0) {
            int rlen = (int)((clen > buf.length)?buf.length:clen);
           /*
            proto = new DSMPBaseProto((byte)PACKET, (byte)0, (byte)0, 
                                      buf, 0, rlen);
           */
            proto = new DSMPBaseProto((byte)PACKET, (byte)0, (byte)0);
            if (fis != null) {
               rlen = fis.read(buf, 0, rlen);
            }
            proto.appendData(buf, 0, rlen);
            handler.sendProtocolPacket(proto);
            clen -= rlen;
         }
            
         System.out.println("Done Upload wait for fini ...");
         
         synchronized(this) {
            while(!gotreply) {
               try { 
                  System.out.print(".");
                  wait(1000);
               } catch(InterruptedException ie) {}
            }
         }
         
         gotreply = false;
         
         System.out.println("... Got fini");
         
         endtime = System.currentTimeMillis();
         long deltatime = endtime - starttime;
         
         System.out.println("Upload complete: Delta(ms): " + deltatime + 
                            " Bytes: " + xferLen + 
                            " MB/s: " + ((xferLen*1000)/deltatime));
         
         
         System.out.println("Writing download ...");
         proto = new DSMPBaseProto(DOWNLOAD, (byte)1, (byte)0);
         proto.appendLong(xferLen);
         handler.sendProtocolPacket(proto);
         
         starttime = System.currentTimeMillis();
            
         System.out.println("... done");
            
         synchronized(this) {
            while(!gotreply) {
               try { 
                  System.out.println(".");
                  wait();
               } catch(InterruptedException ie) {}
            }
         }
            
         endtime = System.currentTimeMillis();
         deltatime = endtime - starttime;
         System.out.println("Download complete: Delta(ms): " + deltatime + 
                            " Bytes: " + xferLen + 
                            " MB/s: " + ((xferLen*1000)/deltatime));
            
      } catch(Exception ee) {
         System.out.println("Exception processing client side");
         ee.printStackTrace(System.out);
      }
      
      System.exit(0);
   }
   
   void doFrameworkServer() {
      
      ServerSocket socket = null;
      for (int i=0; i<100; i++) {
         try {
            socket = new ServerSocket(port);
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
            globalhandler = new DSMPSocketHandler(sock, this);
                
         } catch (IOException e){			
            e.printStackTrace(System.out);
         }
      }
      
      try {socket.close();} catch(Throwable tt) {}
   }
   
   
  // Here is where we handle the data
   public void dispatchProtocolI(DSMPBaseProto proto, 
                                 DSMPBaseHandler handler, 
                                 boolean doDispatch) 
      throws InvalidProtocolException {
      
      if (!doDispatch) return;
      
      byte opcode = proto.getOpcode();
      switch(opcode) {
         case UPLOAD: {
            if (isClient) {
               System.out.println("Huh? Client gets UPLOAD opcode!!");
            } else {
               xferLen = proto.getLong();
               curLen  = 0;
               starttime = System.currentTimeMillis();
               System.out.println("Got Upload");
            }
            break;
         }
         case DOWNLOAD: {
            if (isClient) {
               System.out.println("Huh? Client gets DOWNLOAD opcode!!");
            } else {
               if (thefile != null) {
                  File theFile = new File(thefile);
                  xferLen = theFile.length();
                  try {
                     fis = new FileInputStream(thefile);
                  } catch(Exception ee) {
                     System.out.println("Error opening " + thefile);
                  }
               }
            
              //xferLen = proto.getLong();
               System.out.println("Got Download");
               new Thread(this).start();
            }
            break;
         }
         case LOAD_COMPLETE: {
            System.out.println("Got Complete");
            if (isClient) {
               synchronized(this) {
                  endtime = System.currentTimeMillis();
                  long deltatime = endtime - starttime;
                  System.out.println("Job complete: Delta(ms): " + deltatime + 
                                     " Bytes: " + xferLen + 
                                     " MB/s: " + 
                                     ((xferLen*1000)/deltatime));
                  notifyAll();
               }
            } else {
               endtime = System.currentTimeMillis();
               long deltatime = endtime - starttime;
               System.out.println("Job complete: Delta(ms): " + deltatime + 
                                  " Bytes: " + xferLen + 
                                  " MB/s: " + 
                                  ((xferLen*1000)/deltatime));
            }
            gotreply = true;
            break;
         }
         case PACKET: {
            curLen += proto.getNonHeaderSize();
            if (curLen >= xferLen) {
               if (curLen > xferLen) {
                  System.out.println("Got more data than expected. Abort");
                  System.exit(8);
               } else {
                  
                  proto = new DSMPBaseProto((byte)LOAD_COMPLETE, (byte)0,
                                            (byte)0);
                  handler.sendProtocolPacket(proto);
                  if (isClient) {
                     synchronized(this) {
                        endtime = System.currentTimeMillis();
                        long deltatime = endtime - starttime;
                        System.out.println("Job complete: Delta(ms): " + 
                                           deltatime + 
                                           " Bytes: " + xferLen + 
                                           " MB/s: " + 
                                           ((xferLen*1000)/deltatime));
                        notifyAll();
                     }
                  } else {
                     endtime = System.currentTimeMillis();
                     long deltatime = endtime - starttime;
                     System.out.println("Job complete: Delta(ms): " + 
                                        deltatime + 
                                        " Bytes: " + xferLen + 
                                        " MB/s: " + 
                                        ((xferLen*1000)/deltatime));
                  }
               }
            }
            break;
         }
         default: {
            System.out.println("Bad opcode: " + opcode);
            break;
         }
      }
   }
   
   
   public void run() {
   
      DSMPBaseProto proto;
      curLen  = xferLen;
      starttime = System.currentTimeMillis();
      long tot = 0;
      try {
         while(curLen > 0) {
            int rlen = (int)((curLen > buf.length)?buf.length:curLen);
           /*
             proto = new DSMPBaseProto((byte)PACKET, (byte)0, (byte)0, 
             buf, 0, rlen);
           */
            proto = new DSMPBaseProto((byte)PACKET, (byte)0, (byte)0);
            proto = new DSMPBaseProto((byte)PACKET, (byte)0, (byte)0);
            if (fis != null) {
               rlen = fis.read(buf, 0, rlen);
            }
            proto.appendData(buf, 0, rlen);
            globalhandler.sendProtocolPacket(proto);
            curLen -= rlen;
            tot += rlen;
            if (tot > xferLen) {
               System.out.println("GAK != lens");
            }
         }
         
         System.out.println("Done Download wait for fini ...");
         
         endtime = System.currentTimeMillis();
         long deltatime = endtime - starttime;
         System.out.println("Interm: Delta(ms): " + deltatime + 
                            " Bytes: " + xferLen + 
                            " MB/s: " + 
                            ((xferLen*1000)/deltatime));
      } catch(Exception ee) {
         System.out.println("Exception while running: ");
         ee.printStackTrace(System.out);
      }
   }
   
   
   public void doRealServer() {
	  
      ServerSocket socket = null;
      int port = 4000;
      for (int i=0; i<100; i++) {
         try {
            socket = new ServerSocket(port);
            port = socket.getLocalPort();
            break;
         } catch (IOException ioe) {
         }
      }
      
      if (port == 0) { 
         System.out.println("Could not get local port");
         System.exit(3);
      }
      
      System.out.println("Server socket: port = " + port);
		
      while (true) {
         try {
            sock = socket.accept();
            if (sock == null) {
               System.out.println("TT: Got a NULL Socket EXIT!"); 
            }	
            
            if (useSSL) {
               try {
                  sock = URLConnection2.sslizeSocket(sock, true);
                  break;
               } catch(IOException ioe) {
                  ioe.printStackTrace(System.out);
                  try {sock.close();} catch(Throwable tt) {}
               }
            } else {
               break;
            }
         } catch (IOException e){			
            System.out.println("Error accepting socket!!");
            e.printStackTrace(System.out);
         }
      }
      
      
      try {socket.close();} catch(Throwable tt) {}
      
      if (sock != null) {
         System.out.println("Connect accepted");
         try {
            ObjectInputStream  is = 
               new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream os = 
               new ObjectOutputStream(sock.getOutputStream());
         
            while (true) {
            
               System.out.println("Reading Boolean ...");
               Boolean upOrDown = (Boolean)is.readObject();
               System.out.println("... Got " + upOrDown.toString());
               System.out.println("Reading Long ...");
               Long l = (Long)is.readObject();
               System.out.println("... Got " + l.toString());
               
               long clen = l.longValue();
               
               starttime = System.currentTimeMillis();
               
               while(clen > 0) {
                  int rlen = (int)((clen > buf.length)?buf.length:clen);
                  if (upOrDown.booleanValue()) {
                     int nread = is.read(buf, 0, rlen);
                     if (nread > 0) {
                        clen -= nread;
                     } else if (nread < 0) {
                        System.out.println("Got -1 ... should not happen! Gak!");
                        System.exit(4);
                     }
                  } else {
                     os.write(buf, 0, rlen);
                     clen -= rlen;
                     os.flush();
                  }
               }
               
               if (upOrDown.booleanValue()) {
                  System.out.println("Writing finish boolean ...");
                  os.writeObject(new Boolean(true));
                  os.flush();
                  System.out.println("... done");
               }
               
               endtime = System.currentTimeMillis();
               long deltatime = endtime - starttime;
               System.out.println("Job complete: Delta(ms): " + deltatime + 
                                  " Bytes: " + l.longValue() + 
                                  " MB/s: " + ((l.longValue()*1000)/deltatime));
               
            }
         } catch(Exception ee) {
            System.out.println("Exception processing Serverstuff!");
            ee.printStackTrace(System.out);
         }
      }
   }
      
   public void doRealClient() {
	  
      
      System.out.println("Connecting to " + machine + ":" + port);
		
      try {
      
         try {
            byte buf[] = new byte[50*1024];
                        
            if (uploadtest) {
               String proto = "http";
               if (port == 443) proto = "https";
               String us="/cc/servlet/oem/edge/ed/odc/tunnel/ReceiveFromClient/MP";
               URL url = new URL(proto + "://" + machine + ":" + port + us);
               URLConnection2 connection = new URLConnection2(url);
               connection.setSendContentLength(0x7fffffff);
               connection.setDoOutput(true);
               connection.setUseCaches(false);                        
               OutputStream os2use = connection.getOutputStream();

              //OutputStream os2use = sock.getOutputStream();               
              
               String s = "";
               s += "POST /cc/servlet/oem/edge/ed/odc/tunnel/ReceiveFromClient/MP HTTP/1.1\r\n";
               s += "Host: " + machine + ":" + port + "\r\n";
//               s += "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.4) Gecko/20030624 Netscape/7.1 (ax)\r\n";
//               s += "User-Agent: jojoMcman\r\n";
//               s += "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,video/x-mng,image/png,image/jpeg,image/gif;q=0.2,*/*;q=0.1\r\n";
//               s += "Accept-Language: en-us,en;q=0.5\r\n";
//               s += "Accept-Encoding: gzip,deflate\r\n";
//               s += "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n";
//               s += "Keep-Alive: 300\r\n";
//               s += "Connection: keep-alive\r\n";
//               s += "Referer: http://iceland:2000/try.html\r\n";
               s += "Content-Type: application/octet-stream\r\n";
//               s += "Content-Type: multipart/form-data; boundary=---------------------------24464570528145\r\n";
//               s += "Content-Length: 20243339\r\n\r\n";
               s += "Content-Length: 2147483647\r\n\r\n";
//               s += "-----------------------------24464570528145\r\n";
//               s += "Content-Disposition: form-data; name=\"filename\"; filename=\"SDKJava40.exe\"\r\n";
//               s += "Content-Type: application/x-msdownload\r\n\r\n";
               os2use.write(s.getBytes());
               os2use.flush();
               long clen = xferLen;
               while(clen > 0) {
                  int rlen = (int)((clen > buf.length)?buf.length:clen);
                  os2use.write(buf, 0, rlen);
                  clen -= rlen;
                  os2use.flush();
               }
               System.out.println("Done");
              // sock.close();
               return;
            }
            
            
            if (useSSL) {
               
               sock = new Socket(machine, port);
               try {
                  sock=URLConnection2.sslizeSocket(sock, false);
               } catch(IOException ioe) {
                  ioe.printStackTrace(System.out);
                  try {sock.close();} catch(Throwable tt) {}
                  sock = null;
               }
               if (sock == null) throw new IOException("Can't do SSL connect");
            } else {
               sock = new Socket(machine, port);
            }
            
      
            System.out.println("... Connected");
            
            ObjectOutputStream os = 
               new ObjectOutputStream(sock.getOutputStream());
            ObjectInputStream  is = 
               new ObjectInputStream(sock.getInputStream());
               
            
            Boolean upDown  = new Boolean(true);
            Long    longlen = new Long(xferLen);
            
            os.writeObject(upDown);
            System.out.println("Writing Boolean ...");
            os.writeObject(longlen);
            System.out.println("Writing Long ...");
            os.flush();
            
            System.out.println("Upload ...");
            
            starttime = System.currentTimeMillis();
            
            long clen = xferLen;
            while(clen > 0) {
               int rlen = (int)((clen > buf.length)?buf.length:clen);
               os.write(buf, 0, rlen);
               clen -= rlen;
               os.flush();
            }
            
            System.out.println("Done Upload wait for fini ...");
            is.readObject();
            
            System.out.println("... Got fini");
            
            endtime = System.currentTimeMillis();
            long deltatime = endtime - starttime;
            
            System.out.println("Upload complete: Delta(ms): " + deltatime + 
                               " Bytes: " + xferLen + 
                               " MB/s: " + ((xferLen*1000)/deltatime));
            
            
            
            System.out.println("Writing download ...");
            upDown  = new Boolean(false);
            os.writeObject(upDown);
            os.writeObject(longlen);
            os.flush();
            
            System.out.println("... done");
            
            starttime = System.currentTimeMillis();
            
            clen = xferLen;
            while(clen > 0) {
               int rlen = (int)((clen > buf.length)?buf.length:clen);
               int nread = is.read(buf, 0, rlen);
               if (nread > 0) {
                  clen -= nread;
               } else if (nread < 0) {
                  System.out.println("Got -1 ... should not happen! Gak!");
                  break;
               }
            }
            
            endtime = System.currentTimeMillis();
            deltatime = endtime - starttime;
            System.out.println("Download complete: Delta(ms): " + deltatime + 
                               " Bytes: " + xferLen + 
                               " MB/s: " + ((xferLen*1000)/deltatime));
            
         } catch(Exception ee) {
            System.out.println("Exception processing Serverstuff!");
            ee.printStackTrace(System.out);
         }
      } catch(Exception oee) {
         System.out.println("Exception processing Serverstuff!");
         oee.printStackTrace(System.out);
      }
   }      
}      
