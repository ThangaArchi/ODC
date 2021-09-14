package oem.edge.common.perform;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.zip.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class EdgeServletFuncs
{
  private static boolean useGzipFlag=true;
  public static void setGzipFlag(boolean value)
  { useGzipFlag=value; }
  public static boolean getGzipFlag() 
  { return useGzipFlag; }

  /**
     get a gzip PrintWriter if possible
  */
    public static PrintWriter getWriter(HttpServletRequest request,
HttpServletResponse response) throws IOException {
        if (useGzipFlag==false) { return response.getWriter(); }
            
	PrintWriter out=null;
	String encodeHead = request.getHeader("Accept-Encoding");
	String encodeParam= request.getParameter("encoding");

	// Check if browser supports gzip compression.
	if ((encodeHead != null) && (encodeHead.indexOf("gzip") != -1) &&
	    (!"none".equals(encodeParam)) ) {
	    if (encodeHead.indexOf("x-gzip") != -1)
		response.setHeader("Content-Encoding", "x-gzip");
	    else 
		response.setHeader("Content-Encoding", "gzip");
		
	    out = new PrintWriter(new GZIPOutputStream(response.getOutputStream()), false);
	    
	} 
	else {
	    out = response.getWriter();
	}
        
	return out;
    } // method getWriter

  /**
     get a gzip OutputStream if possible
  */
    public static OutputStream getOutputStream(HttpServletRequest request,
HttpServletResponse response) throws IOException {
        if (useGzipFlag==false) { return response.getOutputStream(); } 
	OutputStream out=null;
	String encodeHead = request.getHeader("Accept-Encoding");
	String encodeParam= request.getParameter("encoding");

	// Check if browser supports gzip compression.
	if ((encodeHead != null) && (encodeHead.indexOf("gzip") != -1) &&
	    (!"none".equals(encodeParam)) ) {
	    if (encodeHead.indexOf("x-gzip") != -1)
		response.setHeader("Content-Encoding", "x-gzip");
	    else 
		response.setHeader("Content-Encoding", "gzip");
		
	    out = new GZIPOutputStream(response.getOutputStream());
	    
	} 
	else {
	    out = response.getOutputStream();
	}
	return out;
    } // method getOutputStream

 /**
    get Hostname
  */
    public static String getHostname()
    {
	InetAddress inet 	= null;
	String str="";
	try {
	    inet 	= InetAddress.getLocalHost();
		str = inet.getHostName();
	}
	catch (UnknownHostException e) {
	    str="";
	}
	return str;
    }

 /**
    get SessionID
  */
    public static String getSid(HttpServletRequest request)
    {
    HttpSession session = request.getSession(false);
    String sessID=( session == null )? "" : session.getId();
    return sessID;
    }


    /**
       asciiToBytes
     */
   public static byte[] asciiToBytes(String buf) {
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
       encodeUrl
    */
    public static String encodeUrl(String path, String dlim)
    {
         StringTokenizer line = new StringTokenizer(path, dlim, true);
         StringBuffer buf = new StringBuffer();
         String token;
         while (line.hasMoreTokens())
         {
            token = line.nextToken();
            if (token.equals(dlim))
	    buf.append(token);
	else
	    buf.append(URLEncoder.encode(token));
         }
         return buf.toString();
    }

    /**
       decodeUrl
    */
         public static String decodeUrl(String path, String dlim)
    {
         StringTokenizer line = new StringTokenizer(path, dlim, true);
         StringBuffer buf = new StringBuffer();
         String token;
         try {
	     while (line.hasMoreTokens())
		 {
		     token = line.nextToken();
		      if (token.equals(dlim))
 			 buf.append(token);
 		     else
 			 buf.append(URLDecoder.decode(token));
		 }
	 }
         catch (Exception e)
	     {
		 e.printStackTrace();
	     }
         return buf.toString();
    } 


}
