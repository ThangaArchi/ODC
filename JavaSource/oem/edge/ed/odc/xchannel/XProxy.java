package oem.edge.ed.odc.xchannel;

/**
 * Insert the type's description here.
 * Creation date: (10/1/2003 9:27:24 AM)
 * @author: Administrator
 */
import java.io.*;
import java.net.*;
import java.util.*;

import oem.edge.ed.odc.util.SSLizer;
import oem.edge.ed.odc.tunnel.common.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

public class XProxy {

   public int XProxyStartPort = 7625;
   private int proxyPort = -1;
   private int portToConnectTo_proxy = -1;
   private String cookie = null;
   private String display = null;
   private String passPhrase = null;

   private static Vector ThreadRepository	= null;
	
	
/**
 * XProxy constructor comment.
 */
   public XProxy() {

      super();
   }
/**
 * Insert the method's description here.
 * Creation date: (11/12/2003 4:46:11 PM)
 * @param xsel oem.edge.ed.odc.xchannel.XSelectCall
 */
   public static void addThreadToRepository(XSelectCall xsel) {
     //System.out.println("adding thread to repository");
      if(ThreadRepository == null){
         ThreadRepository	= new Vector();
      }
      ThreadRepository.addElement(xsel);
   }
/**
 * Insert the method's description here.
 * Creation date: (10/3/2003 4:23:27 PM)
 */
   public void begin(ServerSocket sslServSock) {	
      Socket proxy_as_server	;

	
      if(sslServSock == null) System.out.println("Server socket: NULL");
	
      try{
         while(true){	
            proxy_as_server	=	(Socket)(sslServSock.accept());
            proxy_as_server = 
               SSLizer.sslizeSocketAnonymous(proxy_as_server, 
                                    true);
                                                                        
            System.out.println("Accepted connection [ HOST:"+proxy_as_server.getInetAddress().getHostName()+
                               " ] [ PORT: "+proxy_as_server.getPort() + " ]");
            performerProxy(proxy_as_server);
         }
			
      }
      catch(java.io.IOException ioe){
         System.out.println("IOException occured");
         ioe.printStackTrace();
      }
      catch(Exception exc){
         System.out.println("Exception occured");
         exc.printStackTrace();
      }
	
		
	
   }
/**
 * Insert the method's description here.
 * Creation date: (10/3/2003 12:40:04 PM)
 */
   public ServerSocket createServerSocket() {
      ServerSocket servsock = null;
                        
      try {
         int localport = XProxyStartPort;
         int i;
         for(i = 0 ; i < 5 ; i ++)
         {
            try{
               servsock=new ServerSocket(localport);
               localport = servsock.getLocalPort();
               setProxyPort(localport);	
               break;
            }
            catch(IOException ioe){
               localport ++;
            }
         }
         if( i == 5){
            System.out.println("\n\n\t NO PORT AVAILABLE TO START"
                               +	" THE SSL SERVER SOCKET\n\n");
            return null;
         }
         System.out.println("Waiting for client on port#: " + localport + " ...");
      }
      catch(Exception e){
         e.printStackTrace();
      }
      return servsock;
   }
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 10:46:38 AM)
 * @param str java.lang.String
 * @exception java.lang.Exception The exception description.
 */
   static public String getMachineHost(String str) throws java.lang.Exception {
     //parse the fisrt part of the string and send it back aaa:bb..send aaa
      return str.substring(0, str.indexOf(":"));
   }
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 10:48:42 AM)
 * @return java.lang.String
 * @exception java.lang.Exception The exception description.
 */
   static public String getMachinePort(String str) throws java.lang.Exception {
     // aaa.bb return bb
      return str.substring(str.indexOf(":") + 1);
   }
/**
 * Insert the method's description here.
 * Creation date: (10/3/2003 1:00:16 PM)
 * @return int
 */
   public int getProxyPort() {
      return proxyPort;
   }
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 9:29:28 AM)
 * @param args java.lang.String[]
 */
   public static void main(String[] args) {
      XProxy xp = new XProxy();

      ServerSocket servsock;
      Socket proxy_as_server = null;
      Socket proxy_as_client = null;
      if(args.length!=1)
      {
         System.out.println("Usage <XProxy PORT>");
         System.exit(1);
      }
      try{


        //       AppProp = (PropertyResourceBundle)
        //          PropertyResourceBundle.getBundle(propName);
      } catch ( Exception e ) {
         System.out.println("Property file couldn't be found");
      }

      try{
        //getProperties();
            servsock=new ServerSocket(Integer.parseInt(args[0]),5);
            System.out.println(".....Waiting for client");
        //subu ...remember...nobody uses this method..
        //dont waste your time in this method . Instead 
        //work on createServerSocket() and begin()
         while(true)
         {
         
            proxy_as_server	=	(Socket)servsock.accept();
            proxy_as_server = SSLizer.sslizeSocketAnonymous(proxy_as_server, true);
            
            System.out.println("Accepted connection from:"+proxy_as_server.getInetAddress().getHostName()+
                               " at port #: "+proxy_as_server.getPort());
            if(!xp.performerProxy(proxy_as_server)){
               System.out.println("abt to break from the while loop");
               break;
            }

            System.out.println("in while loop");
         }
		
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 10:16:30 AM)
 * @param proxyAsServer java.net.Socket
 */
   public  boolean performerProxy(Socket proxyAsServer) {
      Socket pr =	proxyAsServer;
	
      Socket proxyAsClient = null;
      XSelectCall client = null;
      XSelectCall server = null;
      try{
         InputStream Sin = pr.getInputStream();
         OutputStream Sout = pr.getOutputStream();
			

         String chocchip 	=	this.cookie;
         String xMachineHost	=	getMachineHost(this.display);
			
         if(passPhrase == null){
            System.out.println("passphrase not set locally in XChannel. Exiting the XProxy");
            return false;
         }
			

         proxyAsClient = XSelectCall.shakeHand(pr,xMachineHost, portToConnectTo_proxy, cookie, 
                                               passPhrase);
         if(proxyAsClient != null)
         {
            client = new XSelectCall(Sin,proxyAsClient.getOutputStream(),true);
            XProxy.addThreadToRepository(client);
            server = new XSelectCall(proxyAsClient.getInputStream(),Sout,false);
            XProxy.addThreadToRepository(server);
				
         }
         else
         {
            System.out.println("Client couldn't be trusted.....Handshake Failure");
            pr.close();
         }

			
      }
      catch(Exception e){
         e.printStackTrace();
      }
     /*	try{
        if(client != null)
        client.t.join();
        if(server != null)
        server.t.join();
        }
        catch(InterruptedException ie){
        System.out.println("Main thread interrupted");
        ie.printStackTrace();
        }
     */
      return true;
   }
/**
 * Insert the method's description here.
 * Creation date: (11/12/2003 4:52:22 PM)
 * @return boolean
 * @param xsel oem.edge.ed.odc.xchannel.XSelectCall
 */
   public static boolean removeThreadFromRepository(XSelectCall xsel) {
     //System.out.println("Removing thread from repository");
      boolean rb = false;
      if(ThreadRepository.isEmpty())
         return rb;
      rb	= ThreadRepository.removeElement(xsel);
      if(ThreadRepository.isEmpty()){
        //System.out.println("All threads are removed from the repository...Exiting...");
         System.exit(0);
      }
		
      return rb;
   }
/**
 * Insert the method's description here.
 * Creation date: (10/17/2003 3:29:57 PM)
 * @param cookie java.lang.String
 */
   public void setCookie(String cookie) { this.cookie = cookie;}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2003 3:32:06 PM)
 * @param dis java.lang.String
 */
   public void setDisplay(String dis) { this.display = dis;}
/**
 * Insert the method's description here.
 * Creation date: (10/17/2003 3:46:50 PM)
 * @param passPhrase java.lang.String
 */
   public void setPassPhrase(String passPhrase) {
      this.passPhrase = passPhrase;
   }
/**
 * Insert the method's description here.
 * Creation date: (10/17/2003 3:30:57 PM)
 * @param portToConnect java.lang.String
 */
   public void setPortToConnect(String portToConnect) { 
      try{
         portToConnectTo_proxy = Integer.parseInt(portToConnect);
      }
      catch(NumberFormatException n){
         portToConnectTo_proxy =	Integer.parseInt(portToConnect.substring(0, portToConnect.indexOf(".")));

      }
   }
/**
 * Insert the method's description here.
 * Creation date: (10/3/2003 1:00:16 PM)
 * @param newProxyPort int
 */
   public void setProxyPort(int newProxyPort) {
      proxyPort = newProxyPort;
   }
}
