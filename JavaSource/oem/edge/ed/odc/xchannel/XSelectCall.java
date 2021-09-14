package oem.edge.ed.odc.xchannel;

/**
 * Insert the type's description here.
 * Creation date: (10/1/2003 10:56:26 AM)
 * @author: Administrator
 */
import java.net.*;
import java.io.*;

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

public class XSelectCall implements Runnable {
   private boolean isFromClient;
   private InputStream in;
   private OutputStream out;
   private int SIZE = 0x3fff;
   byte buffer[];
   private boolean conn = false;
   Thread t;

/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 12:12:16 PM)
 */

   public XSelectCall() {}
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 12:15:56 PM)
 * @param in java.io.InputStream
 * @param out java.io.OutputStream
 * @param isFromClient boolean
 */
   public XSelectCall(InputStream in, OutputStream out, boolean isFromClient) {
      this.in				=	in;
      this.out			=	out;
      buffer				=	new byte[SIZE];
      this.isFromClient	=	isFromClient;

      conn				=	true; 
      t					= 	new Thread(this);
      t.start();

	
	
   }
  /**
   * When an object implementing interface <code>Runnable</code> is used 
   * to create a thread, starting the thread causes the object's 
   * <code>run</code> method to be called in that separately executing 
   * thread. 
   * <p>
   * The general contract of the method <code>run</code> is that it may 
   * take any action whatsoever.
   *
   * @see     java.lang.Thread#run()
   */
   public void run() {
      try{
         int len=0;
         int totCount=len;

         String str=null;
         int noOfCntrlCommands=0;
         int bytesSnooped=0;
         String protostr=null;
         String seqstr=null;

         while(conn){

            len=in.read(buffer,0,buffer.length);
		

            if( isFromClient && len>0)
            {
              //Do we need any code here???
    
            }

   
            if(len>=0){
               totCount+=len;
               out.write(buffer,0,len);
		
               out.flush();
            }
            else break;

         }
/* i am outside the while loop */
         if(len<0 ) {
	  
            in.close();
            out.close();
	 }
      }
      catch( IOException ioe){
        /*try{
          if(in != null){
         //	System.out.println("closing in");
        //			in.close();
        in=null;
        }
        if(out != null){
       //	System.out.println("closing out"); 
       out.close();
       out=null;
       }*/
					
         return;
        /*}
          catch(Exception e){}*/
      }
      catch( Exception e){
         e.printStackTrace();
      }
      finally{
         XProxy.removeThreadFromRepository(this);
      }
   }
/**
 * Insert the method's description here.
 * Creation date: (10/1/2003 11:00:09 AM)
 * @return java.net.Socket
 * @param cookie java.lang.String
 * @param pass java.lang.String
 */
   public static Socket shakeHand(Socket proxyAsServer, String host,
                                  int port, String cookie, String pass) {
      if(host == null | port == -1 | cookie == null | pass == null | proxyAsServer == null) return null;

      Socket skt		=	null;
      int len			=	-1;
      byte[] lenbyte	=	new byte[2];
      try{
         InputStream in		=	proxyAsServer.getInputStream();
         OutputStream out	=	proxyAsServer.getOutputStream();
         in.read(lenbyte);

        //************************************COOKIE HANDLING***********************************

           //FORMING THE COOKIE LENGTH FROM THE FIRST 2 BYTES
            int cookieLength	=	(lenbyte[0]	<<	8)	|	lenbyte[1];

            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "XSelectCall: JOE COOKIE LEN: " + cookieLength);
            }
			
           //CREATING JOE'S COOKIE FROM PASSING THE COOKIELENGTH IN THE ARRAY AND READING THE VALUE IN IT
            byte cookieFromJoe[]		=	new byte[cookieLength];
            in.read(cookieFromJoe);

           //RETRIEVING BYTES FROM SUBU COOKIE
		
            byte b[] = cookie.getBytes();

            if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
               DebugPrint.println(DebugPrint.DEBUG3, 
                                  "XSelectCall: SUBU COOKIE LEN: " + b.length);
            }
           //COMPARING SUBU COOKIE'S WITH JOE'S COOKIE
            if(b.length	==	cookieLength){
               for(int i = 0 ; i < cookieLength; i ++){
                  if(b[i]	!=	cookieFromJoe[i]) {
                     System.out.println( " COOKIE MISMATCH ...");
                     return skt;//skt is null here
                  }
               }	
            }
            else	{	
               System.out.println(" COOKIE LENGTH MISMATCH ...");
               return skt;//skt is null
            }
           //*************************PASSPHRASE HANDLING********************************

                                                byte []subupass			=	pass.getBytes();
                                                byte subupassLength[]	=	new byte[2];
                                                int subupassL			=	subupass.length;

			
                                                if (DebugPrint.isEnabled && DebugPrint.doDebug()) {
                                                   DebugPrint.println(DebugPrint.DEBUG3, 
                                                                      "XSelectCall: SUBU PWD LEN: " + subupassL +
                                                                      " PHRASE: " + pass);
                                                }
                                                subupassLength[0]		=	(byte)((subupassL	>>	8)	&	0x0000FF00)	;
                                                subupassLength[1]		=	(byte)((subupassL)			&	0x000000FF)	;

                                               // SENDING THE SUBU PASSPHRASE LENGTH IN THE FIRST 2 BYTES
                                                out.write(subupassLength);

                                               //SENDING THE SUBU PASSPHRASE IN THE NEXT FEW BYTES
                                                out.write(subupass);
			
                                               // FINISHED THE HANDSHAKE PROCESS SUCCESFULLY. WILL CREATE THE CLIENT SOCKET
                                                System.out.println("CLIENT SOCKET ClampDown...\n\t HOST: " + host + " PORT: " + port); 
                                                skt	=	new Socket(host, 6000 + port);	
											 	
      }
      catch(Exception e){	System.out.println("SHAKEHAND EXCEPTION ...");	e.printStackTrace();}	
      return skt;
   }
}
