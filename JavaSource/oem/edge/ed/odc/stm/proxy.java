/* **********************************
******* auth -Subu Sundaram**********
*************************************
*/
//"Usage < Server IP><Proxy PORT><server port>"
package oem.edge.ed.odc.stm;

import java.io.*;
import java.net.*;
import java.util.*;
import oem.edge.ed.util.EDCMafsFile;
import oem.edge.ed.util.PasswordUtils;

public class proxy{


public static EDCMafsFile authenticator = new EDCMafsFile();
static long AUTH_DURATION = 4 * 60 * 60 * 1000  ;
static long nextAuthenticate = 0;
static String proxyPath="";
static String afsCell;
static String afsUid ;
static String afsPwdPath;
static String rmHost ;
static String rmPort ;
static String rmPath ;
private static java.util.PropertyResourceBundle AppProp  = null;
private final static java.lang.String propName = "STMProxyProperties";

static public String getProperty(String in) {
      String ret = null;
      if (AppProp != null) {
         try {
            ret = AppProp.getString(in);
         } catch(Throwable t) {
         }
      }
      return ret;
   }

public static void getProperties(){
		   rmPath = getProperty("RMPath");
		   rmHost = getProperty("RMHost");
		   rmPort = getProperty("RMPort");
		   afsCell = getProperty("AFSCell");
		   afsUid = getProperty("AFSUid");
		   afsPwdPath = getProperty("AFSPwdDir");
		   	

}

public static synchronized boolean authenticate() {
	boolean authOK = false;
//	if(System.currentTimeMillis() > nextAuthenticate) {
            if(authenticator.afsAuthenticate(afsCell, afsUid, PasswordUtils.getPassword(afsPwdPath))) {
                System.out.println("Authentication OK");
                nextAuthenticate = System.currentTimeMillis() + AUTH_DURATION;
                authOK = true;
            }
            else
                authOK = false;
 //       }
 //      else
 //         authOK = true;

        if( ! authOK ) {
           System.out.println("Authentication Failure");
        }

        return authOK;
}

 public static void performerProxy(Socket proxyAsServer) 
 	{
	Socket proxyAsClient = null;
	try{
	InputStream Sin = proxyAsServer.getInputStream();
	OutputStream Sout = proxyAsServer.getOutputStream();
	AuthenticateUser au =new AuthenticateUser();
	proxyAsClient = SelectCall.ShakeHand(proxyAsServer,rmHost, rmPort, au);
	if(proxyAsClient != null)
	{
	SelectCall client = new SelectCall(Sin,proxyAsClient.getOutputStream(),true, au);
	SelectCall server = new SelectCall(proxyAsClient.getInputStream(),Sout,false, au);
	}
	else
		{
		System.out.println("Client couldn't be trusted.....Handshake Failure");
		proxyAsServer.close();
		}
	}
	catch(Exception e){
	}

	}


public static void main(String[] args){
	ServerSocket servsock;
	Socket proxy_as_server = null;
	Socket proxy_as_client = null;
	if(args.length!=1)
		{
			System.out.println("Usage <Proxy PORT>");
			System.exit(1);
		}
    try{


         AppProp = (PropertyResourceBundle)
            PropertyResourceBundle.getBundle(propName);
      } catch ( Exception e ) {
		System.out.println("Property file couldn't be found");
		}

    try{
	getProperties();
//	String StopExecCommand = rmPath + "/stopServer " + rmPath + rmPath ;
	String StopExecCommand =  getProperty("RMStopServerDir") + "/stopServer.sh " + rmPath ;
	String StartExecCommand = rmPath + "/Bin/rmserver " + rmPath + "/rmserver.cfg" ; 
        try{
	     System.out.println(" Stopping the" +
				" RealServer if running.........."); 
	     Process stop = Runtime.getRuntime().exec(StopExecCommand);
	   }
	catch(Exception e){
	     e.printStackTrace();
	     System.out.println(" Error: Could not Stop " +
				" the RealServer process");
	 }
	try{
	     System.out.println(" Starting the RealServer.....");
       	     Process start =Runtime.getRuntime().exec(StartExecCommand);
	    }
	catch(Exception e){
	     e.printStackTrace();
	     System.out.println(" Error: Starting the Real" +
				"Server process");
	}

	servsock=new ServerSocket(Integer.parseInt(args[0]));
	System.out.println(".....Waiting for client");
	while(true)
		{
		proxy_as_server=servsock.accept();
		System.out.println("Accepted connection from:"+proxy_as_server.getInetAddress().getHostName()+" at port #: "+proxy_as_server.getPort());
		
		System.out.println("....Authenticating........");
		authenticate();
/*		try{
		FileOutputStream fos =new FileOutputStream(new File("//afs//btv//u//edcqdev//output1.txt"));
		fos.write(new String("authenticated").getBytes() );
		fos.close();
		}
		catch(Exception e){e.printStackTrace();}
*/
		performerProxy(proxy_as_server);
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}
}

}
