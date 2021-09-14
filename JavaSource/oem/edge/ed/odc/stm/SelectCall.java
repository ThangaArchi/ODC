package oem.edge.ed.odc.stm;

import java.io.*;
import java.net.*;
import java.util.*;

public class SelectCall implements Runnable{

	private int SIZE=0x3fff;
	private InputStream in;
	private OutputStream out;
	byte buffer[];
	static byte bufferFirst[] = null;

	private boolean isFromClient;
	private boolean conn = false;
	static String urlstr=null;

	static String contentStr=null;
	static String lookFor = "rtsp://localhost:";
	AuthenticateUser auth=null;

public SelectCall(InputStream in,OutputStream out,boolean isFromClient, AuthenticateUser auth)		{
			this.in = in;
			this.out = out;
			buffer = new byte[SIZE];
			this.auth = auth;
			conn = true;
			this.isFromClient = isFromClient;

			new Thread(this).start();
			

		}


public static Socket ShakeHand(Socket proxyAsServer, String rmHost, String rmPort, AuthenticateUser auth){

	Socket skt= null;

	int len = -1;
	try{

		InputStream Input = proxyAsServer.getInputStream();;
		DataInputStream dis = new DataInputStream(Input);
		DataOutputStream dos = new DataOutputStream(proxyAsServer.getOutputStream());

		String syn = dis.readUTF();

		System.out.println("Received:" + syn);


  		if(syn.equalsIgnoreCase("Connect to the 45th Galaxy"))
  			{

  				dos.writeUTF("No I want Blue tie");
			}
		else
			{
				return null;
			}

		System.out.println("HANDSHAKE SUCCESS");
		String path = dis.readUTF();
		System.out.println("Received:" + path);
		auth.setMediaPath(path);
		auth.setTrust(true);


		while(len <= 0){

			len =Input.available();

		}
		if(len > 300){
			skt = new Socket(rmHost,Integer.parseInt(rmPort));

		}

	 }
	 catch(Exception e){
		 e.printStackTrace();
	 }

	return skt;


}

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
    str=new String(buffer);
    int index=0;
    while( index < len ){
	index=str.indexOf(lookFor, index);
	if(index != -1){
	String method = new String(buffer,index-5,5).trim();
        protostr=str.substring(str.indexOf("RTSP",index),str.indexOf("CSeq",index)).trim();
	seqstr=str.substring(str.indexOf("CSeq",index),str.indexOf(10,str.indexOf("CSeq",index))).trim();

        if((str.substring(index+21, index+22)).equalsIgnoreCase("/")){
	auth.checkAuthenticity(str.substring(index+22,str.indexOf("/",index+22)), protostr, seqstr);
		}
	index +=  lookFor.length();
	}
	else break;

    }//end while

  }

   if(!isFromClient && !auth.Authenticate()){
     StringBuffer mybuffer=new StringBuffer(auth.getProtocol()+" "+auth.getUnAuthCode()+"\n");
     mybuffer.append(auth.getSequence());
     System.out.println("User not authenticated...Sending "+mybuffer);
     out.write(new String(mybuffer).getBytes());
     out.flush();
     conn=false;
    }
   if(len>=0){
		totCount+=len;
		out.write(buffer,0,len);
		out.flush();
   	     }
    else break;

   }

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
				ioe.printStackTrace(); 
		   /*}
		catch(Exception e){}*/
				}
	catch( Exception e){
				e.printStackTrace();
			 }
	}

}


