package oem.edge.ed.sd;

/**
 * Insert the type's description here.
 * Creation date: (6/20/2001 5:34:53 PM)
 * @author: Administrator
 */
import java.io.*; 
import java.net.*;
import java.security.Security;
import java.net.*;
import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;
class HttpsConnect implements Runnable {
       public java.lang.String Token = null;
       private java.lang.String User = null;
       public java.io.InputStream data = null;
       public java.lang.Thread thread = null;
       public java.lang.String servletUrl;
       private long refreshTime;
       public URLConnection2 urConnect = null;
       private long[] retryTimes= {1000,2000,4000,8000,16000,32000,64000,128000,256000,512000};

	public boolean useFoundryUrl = false;
/**
 * Capacity constructor comment.
 */
public HttpsConnect() {
       super();
       URLConnection2.useKeepAlive(false);
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2002 4:16:20 PM)
 * @return byte[]
 * @param param java.lang.String
 */
public byte[] asciiToBytes(String buf) {
	 int size = buf.length();
      byte[] bytebuf = new byte[size];

      char[] charBuff= new char[size];
      buf.getChars(0,size,charBuff,0); // copy char[]
       
      for (int i = 0; i < size; i++) {
         //bytebuf[i] = (byte)buf.charAt(i);
         bytebuf[i] = (byte) charBuff[i];	    
      }
      return bytebuf;
}
/**
 * Insert the method's description here.
 * Creation date: (11/13/2001 11:35:58 AM)
 * @return boolean
 * @param id1 java.lang.String
 * @param id2 java.lang.String
 */
public boolean compareFiles(String id1, String id2) {
boolean returnValue = false;
try{       
       System.out.println("Inside comparing files");
       URL ur = null;
       if(useFoundryUrl)
        	ur = new URL(servletUrl + "FoundryQueryServlet?token=" + Token + "&id1="+ id1 + "&id2="+ id2);
       else 
       		ur = new URL(servletUrl + "SdQueryServlet?token=" + Token + "&id1="+ id1 + "&id2="+ id2);
              System.out.println("URL:" + ur.toString());       
                    // making the connection
                 //    urConnect = (HttpURLConnection)ur.openConnection();
				urConnect = new URLConnection2(ur);
				//urConnect.connect();
                     // setting the request property to post
                     urConnect.setRequestProperty("method","POST");
                     urConnect.setDoInput(true);
                     urConnect.setUseCaches(false);
                     urConnect.setDefaultUseCaches(false);
				urConnect.connect();
       
                     // checking the header for success
                     int respcode =55;
                     
               if((respcode=urConnect.getStatusInt())!=HttpURLConnection.HTTP_OK){
                       System.out.println("response code:" + respcode);
                       returnValue = false;
                       for(int i =0;i<5; i++){
                              if(respcode != -1){
                                     returnValue = true;
                                    break;
                              }
                             Thread.sleep(100);
                             System.out.println("Retrying Connection : Attempt " + i);
                            urConnect = new URLConnection2(ur);
					urConnect.connect();

                            respcode = urConnect.getStatusInt();
                            System.out.println("response code:" + respcode);
                            //System.out.println("response Message:" + urConnect.getResponseMessage());
                       }
              }
       
                     // getting the input stream  
                     data = urConnect.getInputStream();

              byte[] filebuffer = new byte[64];
              int amount=0;
              String boolvalue ="";       
              
                     while((amount=data.read(filebuffer))!=-1){
                            String temp = new String(filebuffer,0,amount);
                            boolvalue= boolvalue+ temp;
                     }
                     
                     //Closing the connection after completing the file download
                     data.close();
                     System.out.println(boolvalue);
                     returnValue =  new Boolean(boolvalue.trim()).booleanValue();              
              }
              catch( Exception e){
                     System.out.println("In download method in trying to read the inputstream"+e.getMessage());
                     e.printStackTrace();
              }
              System.out.println("returnvalue" + returnValue);       
       return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:30:45 PM)
 * @return boolean
 */
public boolean confirmDownload(String id) {
       
       try{
       URL ur = null;
	   if(useFoundryUrl)
       	 ur = new URL(servletUrl + "FoundryQueryServlet?token=" + getToken() + "&completed=" + id.trim());
       else
       	 ur = new URL(servletUrl + "SdQueryServlet?token=" + getToken() + "&completed=" + id.trim());
       
       
       // making the connection
       urConnect = new URLConnection2(ur);
				//urConnect.connect();

       // setting the request property to post
       urConnect.setRequestProperty("method","POST");
       urConnect.setDoInput(true);
       urConnect.setUseCaches(false);
       urConnect.setDefaultUseCaches(false);
				urConnect.connect();
       
       // checking the header for success
       if(urConnect.getStatusInt()!=HttpURLConnection.HTTP_OK)
              return false;
       
       // getting the input stream  
       data = urConnect.getInputStream();
}
       catch(Exception e){
              // System.out.println("In getOrderlist Method "+ e.getMessage());
              e.printStackTrace();
       }
              return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 10:25:49 AM)
 * @return java.io.InputStream
 */
public InputStream getData() {
       return data;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 6:58:10 PM)
 * @return boolean
 * @param id java.lang.String
 */
public boolean getFile(String id) {
boolean returnValue = true;
try{
       
       String urlString  = null;
       if(useFoundryUrl)
       	 urlString  = servletUrl + "FoundryDownloadServlet?id=" + id.trim() +"&token="+ getToken() +  "&acceptSLA=ACCEPT" + "&rdclient=true";
       else
       	 urlString  = servletUrl + "SdDownloadServlet?id=" + id.trim() +"&token="+ getToken() +  "&acceptSLA=ACCEPT" + "&rdclient=true";
       // urlString.trim();
       System.out.println("URL in getfile 1.9.3: " + urlString);
       URL ur = new URL(urlString);

       // making the connection
       urConnect = new URLConnection2(ur);
				//urConnect.connect();


       // setting the request property to post
       urConnect.setRequestProperty("method","POST");
       urConnect.setDoInput(true);
       urConnect.setUseCaches(false);
       urConnect.setDefaultUseCaches(false);
				urConnect.connect();
       
       // checking the header for success and retrying if no connection was made
              int respcode =55;       
               if((respcode=urConnect.getStatusInt())!=HttpURLConnection.HTTP_OK){
                       System.out.println("response code in getfile:" + respcode);
                       returnValue = false;
                       for(int i =0;i<5; i++){
                              if(respcode != -1){
                                     returnValue = true;
                                    break;
                              }
                             Thread.sleep(100);
                             System.out.println("Retrying Connection : Attempt " + i);
                            urConnect = new URLConnection2(ur);
					urConnect.connect();

                            respcode = urConnect.getStatusInt();
                            System.out.println("response code:" + respcode);
                            //System.out.println("response Message:" + urConnect.getResponseMessage());
                       }
              }
       
       // getting the input stream  
       data = urConnect.getInputStream();
}
       catch(Exception e){
       System.out.println("In getFile Method "+ e.getMessage());
              e.printStackTrace();
              returnValue = false;
       }
              return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 6:58:10 PM)
 * @return boolean
 * @param id java.lang.String
 */
public boolean getFile(String id,String numBytes,String currSize, String actSize,String time,String crc) {
boolean returnValue = true;
       try{
              int retries = 5;
       // id.trim();
       URL ur = null;
       if(useFoundryUrl)
       	 ur = new URL(servletUrl + "FoundryDownloadServlet?token="+ getToken() + "&id=" + id.trim() + "&acceptSLA=ACCEPT"+ "&numBytes="+ numBytes.trim() +"&currSize=" + currSize.trim() +"&actSize=" + actSize.trim()+ "&crc=" + crc.trim()+"&time=" + time.trim() + "&rdclient=true");
       else
       	 ur = new URL(servletUrl + "SdDownloadServlet?token="+ getToken() + "&id=" + id.trim() + "&acceptSLA=ACCEPT"+ "&numBytes="+ numBytes.trim() +"&currSize=" + currSize.trim() +"&actSize=" + actSize.trim()+ "&crc=" + crc.trim()+"&time=" + time.trim() + "&rdclient=true");
       System.out.println("URL in get file "+ur.toString());
       
       // making the connection
       
        urConnect = new URLConnection2(ur);
				//urConnect.connect();

       
       // setting the request property to post
       urConnect.setRequestProperty("method","POST");
       urConnect.setDoInput(true);
       urConnect.setUseCaches(false);
       urConnect.setDefaultUseCaches(false);
				urConnect.connect();

        //checking the header for success
              int respcode =55;       
               if((respcode=urConnect.getStatusInt())!=HttpURLConnection.HTTP_OK){
                       System.out.println("response code:" + respcode);
                       returnValue = false;
                       for(int i =0;i<5; i++){
                              if(respcode != -1){
                                     returnValue = true;
                                    break;
                              }
                             Thread.sleep(100);
                             System.out.println("Retrying Connection : Attempt " + i);
                           	urConnect = new URLConnection2(ur);
					urConnect.connect();

                            respcode = urConnect.getStatusInt();
                            System.out.println("response code:" + respcode);
                            //System.out.println("onnect =(HttpURLConnection) ur.openConnection();response Message:" + urConnect.getResponseMessage());
                            retries++;
                       }
              }
                      //System.out.println("response Message:" + urConnect.getResponseMessage());
                      data = urConnect.getInputStream();
       }
       catch(Exception e){
        System.out.println("In getFile(multiple params) Method "+e.getMessage());
              e.printStackTrace();
              returnValue = false;
       }
        System.out.println(returnValue);
              return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:30:45 PM)
 * @return boolean
 */
public boolean getOrderlist() {
       boolean returnValue = false;
       int tries = 0;
       // System.out.println("in get order list");
       while((tries<5)&&(!returnValue)){
              try{
                     System.out.println("WITH PROXY AUTH");
                     returnValue = true;
                      String auth = null;
                     try{
                     auth=System.getProperty("Proxy-Authorization");
                     System.out.println("NEEDS PROXY");
                     }
                     catch(Exception e ){
	                     System.out.println("Exception thrown while reading property");
	                     e.printStackTrace();
                     }
                  /*System.getProperties().put( "proxySet", "true" );
					 //System.getProperties().put( "proxyHost", "9.50.175.228");
					 //System.getProperties().put( "proxyPort", "1080" );
					

					 
					 //  URL ur1 = new URL(servletUrl + "SdQueryServlet?token=" + getToken());
                  //   URL ur = new URL(ur1.getProtocol(),ur1.getHost(),ur1.getPort(),ur1.getFile());
                    	URL ur1 = new URL("http://www.yahoo.com");
                    	URL ur = new URL(ur1.getProtocol(),ur1.getHost(),ur1.getPort(),ur1.getFile());
                     	
						String password = "jeetrao:appididu";
						System.out.println("Using the Password " + password);
						sun.misc.BASE64Encoder base64 = new sun.misc.BASE64Encoder();
						String encodedPassword = "Basic " + base64.encode(password.getBytes());
						*/
						URL ur = null;
						System.out.println("server url" + servletUrl);
						
						if(useFoundryUrl){
							String urlstring = "\"" +servletUrl + "FoundryQueryServlet?token=" + getToken()+ "\"" ;
							System.out.println("urlstring" + urlstring); 
							ur = new URL(servletUrl + "FoundryQueryServlet?token=" + getToken());
						}
						else{
							String urlstring = "\"" +servletUrl + "SdQueryServlet?token=" + getToken() +"\"";
								System.out.println("urlstring" + urlstring); 
							 ur = new URL(servletUrl + "SdQueryServlet?token=" + getToken());
						}
						System.out.println("URL IN getorderlist " + ur.toString() + ur.getHost());
						urConnect = new URLConnection2(ur);
					//	urConnect.connect();

						if(auth!=null)
						urConnect.setRequestProperty( "Proxy-Authorization", auth);
					//	httpProxyHandShake(urConnect,ur);
                    // making the connection
                   //  urConnect = (HttpURLConnection)ur.openConnection();
                     // setting the request property to post
                     urConnect.setRequestProperty("method","POST");
                     urConnect.setDoInput(true);
                     urConnect.setUseCaches(false);
                     urConnect.setDefaultUseCaches(false);
			urConnect.connect();
       
                     // checking the header for success
                     if(urConnect.getStatusInt()!=HttpURLConnection.HTTP_OK){
                            // System.out.println("in get response code");
                            returnValue= false;
                     }
       
                     // getting the input stream  
                     data = urConnect.getInputStream();
                     // System.out.println("Got input stream");
              }
              catch(Exception e){
                     // System.out.println("In getOrderlist Method "+e.getMessage());
                     e.printStackTrace();
                     returnValue = false;
                     /*try{
                            urConnect.disconnect();
                     }
                     catch(Exception dataclose){
                            // System.out.println("data close Exception");
                            e.printStackTrace();
                     }
                     */
                     try{
                            Thread.sleep(retryTimes[tries]);
                     }
                     catch(InterruptedException E){
                            E.getMessage();
                     }
                     // System.out.println("trying after " + retryTimes[tries]  + "milliseconds");
                     tries++;      
              }
       
       }
       // System.out.println("returning from get order list" + returnValue);
              return returnValue;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 7:07:12 PM)
 */
public boolean getSLA() {
       boolean returnValue =true;
       try{
       URL ur = null;
       if(useFoundryUrl)
       	ur = new URL(servletUrl + "FoundryQueryServlet?token=" + getToken() + "&getSLA=yes");
       else
       	ur = new URL(servletUrl + "SdQueryServlet?token=" + getToken() + "&getSLA=yes");
       
       
       // making the connection
       urConnect = new URLConnection2(ur);
				//urConnect.connect();

       // setting the request property to post
       urConnect.setRequestProperty("method","POST");
       urConnect.setDoInput(true);
       urConnect.setUseCaches(false);
       urConnect.setDefaultUseCaches(false);
				urConnect.connect();
       
       // checking the header for success
       //urConnect.getHeaderField();
       
       // getting the input stream  
       data = urConnect.getInputStream();
}
       catch(Exception e){
              
// System.out.println("In getSLA Method "+ e.getMessage());
              e.printStackTrace();
              returnValue = false;
       }
              return returnValue;
       
       }
/**
 * Insert the method's description here.
 * Creation date: (9/14/2001 12:20:33 PM)
 * @return boolean
 */
synchronized public String getToken() {
       return Token;
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2002 4:09:03 PM)
 * @param sock java.net.HttpURLConnection
 */
public void httpProxyHandShake(HttpURLConnection sock , URL url) {
	try{
	
	String port=new String("1080");
	OutputStream out = new BufferedOutputStream(sock.getOutputStream());
      StringBuffer lines = new StringBuffer(512).append("CONNECT ").
         append(url.getHost()).append(":"+port).append(" HTTP/1.1\r\n");
		sun.misc.BASE64Encoder base64 = new sun.misc.BASE64Encoder();
      String auth = "Basic " + base64.encode(new String("jeetrao:appididu").getBytes());
         
      if (auth == null) {
         auth=System.getProperty("Proxy-Authorization");
      }
      
     /*
      if (auth == null) {
         auth=System.getProperty("proxyAuth");
         if (auth != null) {
            auth = "Basic " + auth;
         }
      }
     */
      if (auth != null) {
         lines.append("Proxy-Authorization: ").append(auth).append("\r\n");
      }
      String useragent = URLConnection.getDefaultRequestProperty("User-Agent");
      if (useragent == null) {
         useragent = "J_J_MoJo_Processing_Agent";
      }
      lines.append("Host: ").append(url.getHost()).append(":").append(port).append("\r\n");
      lines.append("User-Agent: ").append(useragent).append("\r\n");
         
      lines.append("Connection: ").append(true?"Keep-Alive":"Close").append("\r\n\r\n");
         
      
      
      byte[] b=asciiToBytes(lines.toString());
      out.write(b,0,b.length);
      out.flush();
      
      b = new byte[1024];
      InputStream in = sock.getInputStream();
      int r, t=0;
      int lastsrch = 0;
      boolean connOk = false;
      byte lfbyte = (byte)'\n';
      byte crbyte = (byte)'\r';
      while(t < 1024) {
      
         r = in.read(b, t, 1024-t); 
         
         
         if (r > 0) {
            t += r;
         } else {
            throw new IOException("Error doing httpProxyHandshake");
         }
         
         if (!connOk) {
            int clf=0;
            int i;
            for(i=0; i < t; i++) {
               byte cbyte = (clf != 0) ? lfbyte : crbyte;
               if (b[i] == cbyte) {
                  if (++clf == 2) {
                     for(int j=0; j < i; j++) {
                        if (b[j] == (byte)' ') {
                           for(; j < i; j++) {
                              if (b[j] != ' ') {
                                 if (j+3 < i       &&
                                     b[j]   == '2' &&
                                     b[j+1] == '0' &&
                                     b[j+2] == '0' &&
                                     b[j+3] == ' ') {
                                    connOk = true;
                                 }
                              }
                           }
                        }
                     }
                     
                     if (!connOk) {
                        throw new IOException("Error from HTTP Proxy");
                     }
                     
                     if (t == 1024) {
                        i++;
                        t = t-i;
                        System.arraycopy(b, i, b, 0, t);
                     }
                     
                     
                     break;
                  }
               } else {
                  clf=0;
               }
            }
         }
                  
        // If we already have Connection OK, then we skip the rest and
        //  look for \r\n\r\n, which WILL be the last bytes coming, as 
        //  we as the client will start the SSL handshake
         if (connOk) {
            if (t >= 4) {
               if (b[t-4] == crbyte && b[t-3] == lfbyte &&
                   b[t-2] == crbyte && b[t-1] == lfbyte) {
                  return;
               }
               if (t > 512) {
                  System.arraycopy(b, t-3, b, 0, 3);
                  t = 3;
               }
            }
         }

      }
	}
	catch (Exception e){
		e.printStackTrace();
	}
   }
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 5:37:15 PM)
 * @param args java.lang.String[]
 * @exception java.lang.Exception The exception description.
 */
public static void main(String[] args) throws java.lang.Exception {
       //Date currentTime = new Date();
       /*int trythis = 10;
       String tempstring = Integer.toString(trythis);
       System.out.println(tempstring);
       long start = System.currentTimeMillis();
       FileOutputStream timestamp =  null;
       String user;
       String address;
       InputStream fileStream;
       FileOutputStream f =  null;
       HttpsURLConnection uc = null;

       if (args[0].equalsIgnoreCase("P")) {
              user = "mzarnick:mushr00m";
              System.setProperty("socksProxyHost","socks1.server.ibm.com");
              System.out.println("Detected Production");
       }
       else {
              user = "efish20:pr1mate";
              System.setProperty("socksProxyHost","socksa.btv.ibm.com");
              System.out.println("Detected Integration");
       }
       //address = "https://edesign.chips.ibm.com/SD/servlet/SDS/ASICToolkit_10.1.1_aix43_64.tar?id=992974239527";
       address = args[1];

       //set up to handle SSL if necessary
       //System.setProperty("socksProxyHost","socks1.server.ibm.com");
       System.setProperty("socksProxyPort","1080");
       System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
       Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

       //Use our own trust manager so we can trust our temp certificate during testing.//
       com.sun.net.ssl.X509TrustManager tm = new MyX509TrustManager();
       KeyManager []km = null;
       com.sun.net.ssl.TrustManager []tma = {tm};
       SSLContext sc = SSLContext.getInstance("SSL");
       sc.init(km,tma,new java.security.SecureRandom());
       SSLSocketFactory sf1 = sc.getSocketFactory();
       HttpsURLConnection.setDefaultSSLSocketFactory(sf1);

       try {
              System.out.println("Connecting to " + address);
              URL remotesite = new URL(address);
              uc = (HttpsURLConnection)remotesite.openConnection();
              uc.setHostnameVerifier(new MyHostnameVerifier());
              uc.setRequestProperty("Authorization", "Basic " + Base64.encode(user.getBytes()));
              uc.setDoInput(true);
              uc.setUseCaches(false);
              fileStream = uc.getInputStream();
              //f = new FileOutputStream("c:\\test.file");
              f = new FileOutputStream(args[2]);
              timestamp= new FileOutputStream(args[2]+".timestamp");
              byte[] b = new byte[1048576];
              System.out.println("Allocated Buffer");
              int i;
              long t = 0;
              long totalDownload=0;
              System.out.println("Receiving file " + args[2] + ":");
              while ((i = fileStream.read(b)) != -1) {
                     //System.out.println("Amount Read in Current iteration " + i);
                     t += i;
                     totalDownload+=i;
                     //if (t % 1048576 == 0) System.out.print(".");
                     
                     if((System.currentTimeMillis()-start)>300000){
                     Date testTime = new Date();
                     //System.out.println("Total DownLoaded = " + (totalDownload/1048576) + " MB :: " + "Download in Current Interval = " + (t/1048576) + " MB ::"+ " TimeStamp "+ testTime);
                     String buf ="Total DownLoaded = " + (totalDownload/1048576) + " MB :: " + "Download in Current Interval = " + (t/1048576) + " MB ::"+ " TimeStamp "+ testTime + "\n\n";
                     timestamp.write(buf.getBytes());
                     start=System.currentTimeMillis();
                     t=0;
                     }
                     f.write(b,0,i);
                     //System.out.println("Written " + i + "bytes to the file");
              }
              f.close();
              System.out.println("Closing File Output Stream");
              fileStream.close();
              System.out.println("Closing Input stream from the URL");
              uc.disconnect();
              System.out.println("Closing URL Connection");
              System.out.println("Done.");
              Date testTime = new Date();
              String buf1 = "Time Ended :"  + testTime;
              timestamp.write(buf1.getBytes());
       }
       catch (Exception e) {
              if (f != null) f.close();
              else System.out.println("f is null");
              e.printStackTrace();
       }
*/

                     String tokenstring = "323573475:hellotoken";              
                     //System.out.println();
                     int test  = tokenstring.indexOf(":");
                     String temp1 = tokenstring.substring(0,test);
                     System.out.println(temp1);
                            
                     Long refreshTime =new Long(temp1);
                     System.out.println(refreshTime.longValue());
                            String Token = tokenstring.substring(test+1,tokenstring.length());
                            System.out.println(Token);
       
       
       //       temp.get
              
       
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/2001 11:27:17 AM)
 */
public void run() {
       //SDHostingApp.Debug("Entered Refreshing Token","null");
       boolean returnValue = true;
       refreshTime=300; // 5 minutes in seconds.
       String lclToken = getToken();
       while((lclToken!=null) && returnValue){
              try{
                     try {
                            thread.sleep((long)(refreshTime*1000));
                            refreshTime /= 2;
                     }
                     catch (Exception e1) {
                     }
       				 URL ur = null;
       				 if(useFoundryUrl)
            			ur = new URL(servletUrl + "FoundryAuthServlet?token="+ lclToken);
                     else         
       				 	ur = new URL(servletUrl + "SdAuthServlet?token="+ lclToken);
                     //SDHostingApp.Debug("Started Refreshing Token",ur.toString());
                     // making the connection
                     URLConnection2 urConnect = new URLConnection2(ur);
			   //urConnect.connect();

                     //System.out.println("URL CLASS NAME " + c.getClass().toString());
                     //HttpURLConnection urConnect =(HttpURLConnection)c;
                     
                     //SDHostingApp.Debug("opened connection","null");
                     // setting the request property to post
                     urConnect.setRequestProperty("method","POST"); 
                     urConnect.setDoInput(true);
                     urConnect.setUseCaches(false);
                     urConnect.setDefaultUseCaches(false);
			   urConnect.connect();
       
                     // checking the header for success
                     if(urConnect.getStatusInt()!=HttpURLConnection.HTTP_OK){
                            if (refreshTime < 5) {
                                   synchronized(this) {
                                          Token = null;
                                          lclToken = null;
                                   }
                            }
                     }

                     else {
                            // getting the input stream
                            byte[] b = new byte[128];
                            //SDHostingApp.Debug("Getting Input stream","null");
                             InputStream tokenStream= urConnect.getInputStream();
                             //SDHostingApp.Debug("Getting Input stream","null");
                             int read;
                             StringBuffer tokenbuffer = new StringBuffer();
                             
                             while((read=tokenStream.read(b))!=-1)
                                   tokenbuffer.append(new String(b,0,read));
                                   
                            String tokenstring = tokenbuffer.toString();
                            int pos = tokenstring.indexOf(":");
                            Long temp = new Long(tokenstring.substring(0,pos));
                            synchronized (this) {
                                   refreshTime =temp.longValue()/2;
                                   // System.out.println(refreshTime);
                                   Token = (tokenstring.substring(pos+1,tokenstring.length())).trim();
                                   lclToken = Token;
                            }
                            tokenStream.close();
                     }
                     // System.out.println("GOT TOKEN " + Token);       
                     //SDHostingApp.Debug(new String(b),"buffer");
              }
              catch(Exception e){
                     // System.out.println("In Make Connection" + e.getMessage());
              //       e.printStackTrace();
                     returnValue = false;
              }
       }
       //System .out.println("token expired");
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:59:20 PM)
 * @param reftime int
 */
public void setRefreshtime(int reftime) {
       refreshTime = reftime;}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 4:59:20 PM)
 * @param reftime int
 */
public void setRefreshtime(long reftime) {
       refreshTime = reftime;}
/**
 * Insert the method's description here.
 * Creation date: (9/14/2001 12:12:44 PM)
 * @param serverUrl java.net.URL
 */
public void setServerUrl(String url) {
 	     
	servletUrl = url + "/";
       }
/**
 * Insert the method's description here.
 * Creation date: (9/14/2001 12:17:27 PM)
 * @param token java.lang.String
 */
public void setToken(String token) {
       // System.out.println("setting token"+ token);
       Token = token.trim();
       thread = new Thread(this);
       thread.start();
}
/**
 * Insert the method's description here.
 * Creation date: (9/14/2001 12:14:00 PM)
 * @param user java.lang.String
 */
public void setUser(String user) {}
}
