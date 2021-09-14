package oem.edge.ed.odc.meeting.server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;


public class httpServer extends Thread {
   
   int port = 8080;
   int serverport = 5000;
   
   
   public httpServer(int p, int np) {
      port = p;
      serverport = np;
   }
   
   public static void startServer(int p, int newport) {
      
      new httpServer(p, newport).start();
   }
   
   public  void run() {
      try {
         
         ServerSocket server_socket;
         server_socket = new ServerSocket(port);
         System.out.println("httpServer running on port " + 
                            server_socket.getLocalPort());
         
        // server infinite loop
         while(true) {
            Socket socket = server_socket.accept();
            System.out.println("New connection accepted " +
                               socket.getInetAddress() +
                               ":" + socket.getPort());
            
           // Construct handler to process the HTTP request message.
            try {
               httpRequestHandler request = 
                  new httpRequestHandler(socket, serverport);
              // Create a new thread to process the request.
               Thread thread = new Thread(request);
               
              // Start the thread.
               thread.start();
            }
            catch(Exception e) {
               System.out.println(e);
            }
         }
      }
      catch (IOException e) {
         System.out.println(e);
      }
   }

   public static void main(String args[]) {
      
      int p;
      try { 
         p = Integer.parseInt(args[0]);
      }
      catch (Exception e) {
         p = 1500;
      }
      
      new httpServer(p, 5000).run();
   }	
}


 
class httpRequestHandler implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;
    InputStream input;
    OutputStream output;
    BufferedReader br;
    int serverport = 5000;

    // Constructor
    public httpRequestHandler(Socket socket, int sp) throws Exception 
    {
        this.serverport = sp;
	this.socket = socket;
	this.input = socket.getInputStream();
	this.output = socket.getOutputStream();
	this.br = 
	    new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    // Implement the run() method of the Runnable interface.
    public void run()
    {
	try {
	    processRequest();
	}
	catch(Exception e) {
	    System.out.println(e);
	}
    }
    
    private void processRequest() throws Exception
    {
       Hashtable hash = new Hashtable();
	while(true) {
	    String headerLine = br.readLine();
	    System.out.println(headerLine);
	    if(headerLine.equals(CRLF) || headerLine.equals("")) break;
            
            String k, v;
            k = v= null;
            int i1 = headerLine.indexOf(' ');
            int i2 = headerLine.indexOf(':');
            if (i1 > 0) { 
               k=headerLine.substring(0, i1);
               v=headerLine.substring(i1+1);
            }
            if (i2 > 0 && (i2 < i1 || i1 < 0)) {
               k=headerLine.substring(0, i2);
               v=headerLine.substring(i2+1);
            }
            
            k = k.toUpperCase();
            hash.put(k, v);
        }
            
            
        boolean inline = false;
        boolean docab  = false;
        
        String GETV = (String)hash.get("GET");
        if(GETV != null) {
           StringTokenizer s = new StringTokenizer(GETV);
           String fileName = s.nextToken();
           fileName = "." + fileName ;
           
           if        (fileName.equals("./")) {
              fileName = null;
              inline = true;
           } else if (fileName.equals("./CALLMEACAB")) {
              fileName = null;
              inline   = true;
              docab    = true;
           } else if (fileName.equals("./DSMP.jar") || 
                      fileName.equals("./DSMP.cab")) {
              ;
           } else if (fileName.endsWith(".html") ||
                      fileName.endsWith(".gif")) {
              ;
           } else {
              System.out.print("Asked for FILE: " + fileName);
              System.out.println("  ... DISALLOWED!");
              fileName = null;
           }
           
           
          // Open the requested file.
           FileInputStream fis = null ;
           boolean fileExists = true ;
           try 
           {
              System.out.println("Looking for file [" + fileName + "]");
              fis = new FileInputStream( fileName ) ;
           } 
           catch ( FileNotFoundException e ) 
           {
              System.out.println("File Not Found [" + fileName + "]");
              fileExists = false ;
           }
           catch ( NullPointerException e ) 
           {
              System.out.println("NULL file [" + fileName + "]");
              fileExists = false ;
           }
           
          // Construct the response message.
//           String serverLine = "Server: IBM_HTTP_Server/1.5 Apache/1.3.12 (Unix)" + CRLF + "Last-Modified: Wed, 11 Sep 2002 19:08:33 GMT" + CRLF + "Date: Wed, 11 Sep 2002 19:11:59 GMT" + CRLF;
           String serverLine = "Server: JoeServer/1.1" + CRLF;
           String statusLine = null;
           String contentTypeLine = null;
           String entityBody = null;
           String contentLengthLine = null;
           if ( fileExists ) {
              statusLine = "HTTP/1.1 200 OK" + CRLF ;
              contentTypeLine = "Content-type: " + 
                 contentType( fileName ) + CRLF ;
              contentLengthLine = "Content-Length: " 
                 + (new Integer(fis.available())).toString() 
                 + CRLF;
           } else if (inline) {
              statusLine = "HTTP/1.1 200 OK" + CRLF ;
              contentTypeLine = "Content-type: text/html" + CRLF ;
              
              String browser = (String)hash.get("USER-AGENT");
              boolean isIE = (browser != null && browser.indexOf("MSIE") >= 0);
              String cmac = "http://" + ((String)hash.get("HOST")).trim() + 
                            "/CALLMEACAB";
              
              if (docab) {
                 entityBody = 
                    "<" + "html lang=\"en-us\">\n<applet alt=\"none\" "   +
                    "code=\"MeetingApplet\" " + "codebase=. " +
                    "width=\"200\" height=\"200\">\n" +
                    "<param name=\"cabbase\" value=\"DSMP.cab\">\n" +
                    "<param name=\"port\" value=\"" + serverport + "\">\n" +
                    "<param name=\"ISIE\" value=\"YES\">\n" +
                    "</applet></html>\n" + CRLF;
              } else {
                 entityBody = 
                    "<" + "html lang=\"en-us\">\n<applet alt=\"none\" "   +
                    "code=\"MeetingApplet\" " + "codebase=. archive=\"DSMP.jar\" " +
                    "width=\"200\" height=\"200\">" +
                    
                    "<param name=\"port\" value=\"" + 
                    serverport + "\">" +
                    
                    "<param name=\"ISIE\" value=\"" + 
                    (isIE?"YES":"NO") + "\">\n" + 
                    
                    "<param name=\"TOCAB\" value=\"" + cmac + "\">\n" + 
                    
                    "</applet></html>\n" + CRLF;
              }
                 
              contentLengthLine = "Content-length: " + 
                 entityBody.getBytes().length + CRLF;
              System.out.println("Returning:\n" + entityBody);
           } else {
              statusLine = "HTTP/1.0 404 Not Found" + CRLF ;
              contentTypeLine = "text/html" ;
              entityBody = "<" + "html>" + 
                 "<head><title>404 Not Found</title></head>" +
                 "<body>404 Not Found" 
                 +"<br />usage:http://yourHostName:port/"
                 +"fileName.html</body></html>" ;
           }
           
          // Send the status line.
           output.write(statusLine.getBytes());
           
          // Send the server line.
           output.write(serverLine.getBytes());
           
          // Send the content type line.
           output.write(contentTypeLine.getBytes());
           
          // Send the Content-Length
           if (contentLengthLine != null)
              output.write(contentLengthLine.getBytes());
           
          // Send a blank line to indicate the end of the header lines.
           output.write(("Connection: close" + CRLF).getBytes());
           
          // Send a blank line to indicate the end of the header lines.
           output.write(CRLF.getBytes());
           
          // Send the entity body.
           if (fileExists)
           {
              sendBytes(fis, output) ;
              fis.close();
           }
           else
           {
              output.write(entityBody.getBytes());
           }
           
        }
        try {
           output.close();
           br.close();
           input.close();
           socket.close();
	}
	catch(Exception e) {
           e.printStackTrace(System.out);
        }
    }
   
    
    private static void sendBytes(FileInputStream fis, OutputStream os) 
	throws Exception
    {
	// Construct a 1K buffer to hold bytes on their way to the socket.
	byte[] buffer = new byte[1024] ;
	int bytes = 0 ;
	
	// Copy requested file into the socket's output stream.
	while ((bytes = fis.read(buffer)) != -1 ) 
	    {
		os.write(buffer, 0, bytes);
	    }
    }
    
    private static String contentType(String fileName)
    {
	if (fileName.endsWith(".htm") || fileName.endsWith(".html"))
	    {
		return "text/html";
	    }
	
	return "";
	
    }
    
}
