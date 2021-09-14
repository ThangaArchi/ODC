package oem.edge.ed.odc.tunnel.common;
import java.io.*;
import java.net.*;
import java.util.*;
import netscape.security.*;
import com.ms.security.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.util.*;

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

public class Misc {
   public static boolean useURLConnection2 = true;
  //public static void setKeyRing() {
     /* 3/1/06 - ditching jskit finally
     // Need this early to create the URL
      try {
         com.ibm.net.www.https.SecureGlue.setKeyRing("EDesignKeyring", 
                                                     "0x120163");
      } catch(ClassNotFoundException e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Misc.setKeyRing: Keyring not found! No HTTPS!");
      } catch(ClassFormatError e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                      "Misc.setKeyRing: Class Format Error!! Go with it!");
      } catch(Exception e) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                      "Misc.setKeyRing: Unknown problem!");
      }      
     */
  // }
   
   public static void setCommonInfo() {
     //Misc.setKeyRing();
      
      String auth = System.getProperty("proxyAuth");
      
      ConfigFile cfg = new ConfigFile();
      try {
         cfg.load("edesign.ini");
         
         if (auth == null) {
            auth = cfg.getProperty("proxyAuth");
         }
         
      } catch (Exception e) {}
      
      if (auth != null) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Proxy-Authentication = " + auth);
         URLConnection.setDefaultRequestProperty("Proxy-Authorization",
                                                 "Basic " + auth);
         Properties p = System.getProperties();
         p.put("Proxy-Authorization", "Basic " + auth);
         
         System.setProperties(p);
      }
   }
   
   
  // OLD ... left for Mike until he changes
   public static String[] getConnectInfo(String user, String pw, 
                                         String op,   String url) {
      int idx = url.indexOf(".chips."); 
      if (idx >= 0) {
         return getConnectInfoBTV(user, pw, op, null);
      }
      return getConnectInfoNJ(user, pw, op, null, url);
   }
   
   public static String[] getConnectInfo(String user, String pw, 
                                         String op,   String scope,
                                         String url) {
      int idx = url.indexOf(".chips."); 
      if (idx >= 0) {
         return getConnectInfoBTV(user, pw, op, scope);
      }
      return getConnectInfoNJ(user, pw, op, scope, url);
   }
   
   public static int opFromCommand(String op) {
      int scint = 0;
      
      try { 
         scint = Integer.parseInt(op);
      } catch(NumberFormatException nfe) {}
      
      if (scint > 0) {
         ;
      } else if (op.equals("ODC")) {
         scint = 1;
      } else if (op.equals("DSH")) {
         scint = 2;
      } else if (op.equals("EDU")) {
         scint = 3;
      } else if (op.equals("IMS")) {
         scint = 4;
      } else if (op.equals("DGP") || op.equals("NEWODC")) {
         scint = 5;
      } else if (op.equals("STM")) {
         scint = 6;
      } else if (op.equals("XFR")) {
         scint = 7;
      } else if (op.equals("FDR")) {
         scint = 8;
      } else if (op.equals("CON")) {
         scint = 10;
      } else if (op.equals("DST")) {
         scint = 20;
      } else if (op.equals("EDT")) {
         scint = 30;
      } else if (op.equals("STT")) {
         scint = 60;
      }
      return scint;
   }
   
   public static String[] getConnectInfoBTV(String user, String pw, 
                                            String cmd, String scope){
      return getConnectInfoBTV(user, pw, opFromCommand(cmd), scope, 
                               null, null, null);}
   
   public static String[] getConnectInfoBTV(String user, String pw, 
                                            int op, String scope) {
      return getConnectInfoBTV(user, pw, op, scope, null, null, null);
   }
   public static String[] getConnectInfoBTV(String user, String pw, 
                                            String cmd, String scope,
                                            String topurl, String irservlet,
                                            String redirServlet) {
      return getConnectInfoBTV(user, pw, opFromCommand(cmd), scope, 
                               topurl, irservlet, redirServlet);
   }
   public static String[] getConnectInfoBTV(String user, String pw, 
                                            int op, String scope,
                                            String topurl, String irservlet,
                                            String redirServlet) {
                                               
      String irservletNew = "servlet/oem/edge/login.jsp";
      
     // To support the OLD way
      if (irservlet != null && !irservlet.equals(irservletNew)) {
         return getConnectInfoBTVOLD(user, pw, op, scope, topurl, 
                                     irservlet, redirServlet);
      }
      
      if (topurl == null) {
         topurl = "https://www-306.ibm.com:443";
      }
      
      
      if (irservlet == null) {
         irservlet = "servlet/oem/edge/login.jsp";
      }
      if (redirServlet == null) {
         redirServlet =  "servlet/oem/edge/EdesignServicesServlet.wss";
      }
      
      String jcheckServlet = "servlet/oem/edge/j_security_check";
                                            
      try {
      
         Misc.setCommonInfo();
         
         
        //-----------------------------------------------------
        // Go to the page we want, and get the cookie for redir
        //-----------------------------------------------------
         
         String redir = redirServlet + "?servicesop=" + op;
         if (scope != null && scope.trim().length() != 0) {
            redir += "&scope=" + URLEncoder.encode(scope);
         }
                  
         String urlString = topurl + "/" + redir;
         
        // HACK22
        // This stinks ... need to trick things cause Servlet engine does NOT
        //   want to learn about HTTPS!!
         boolean ishttps = false;
         URL url = null;
         if (urlString.indexOf("https") == 0) {
            urlString = "http" + urlString.substring(5);
            ishttps = true;
            
            url = new URL(urlString);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlString = "http://" + host + ":" + port ;
            if (file != null) urlString +=  file;
            DebugPrint.println(DebugPrint.DEBUG, "ans = " + urlString);
            
         }
         
         url = new URL(urlString);
         
         URLConnection2 connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         connection.connect();
         
         InputStream in = null;
         
        // Read and build a cookie response so the session works
         String k;
         int i=0;
         String cookie="";
         while((k=connection.getHeaderFieldKey(i)) != null) {
            String v = connection.getHeaderField(i++);
            if (k.equalsIgnoreCase("Set-Cookie")) {
               int semi = v.indexOf(";");
               if (semi >= 0) v = v.substring(0, semi);
               if (cookie.length() != 0) {
                  cookie += "; ";
               }
               
               int eq = v.indexOf('=');
               if (eq >= 0) {
                  String key = v.substring(0, eq);
                  String val = v.substring(eq+1);
                  v = key + "=" + val; //URLEncoder.encode(val);
               }
               
               cookie += v;
            }
         }
         
         String urlYuk;
         urlYuk = connection.getHeaderField("location");
         try {connection.disconnect();} catch(IOException dontcare){}
         if (urlYuk == null) {
            return null;
         }
         
        // If they are not having us go to login page ... bag out
         if (urlYuk.indexOf(irservlet) < 0) {
            DebugPrint.printlnd(DebugPrint.WARN,
                                "Misc:BTV: Not going to " + irservlet + "!: "
                                + urlYuk);
            return null;
         }
         
         
        //-----------------------------------------------------
        // K, should be telling me to go to login.jsp doit
        //-----------------------------------------------------
         
        // HACK22 
         ishttps = false;
         if (urlYuk.indexOf("https") == 0) {
            urlYuk = "http" + urlYuk.substring(5);
            ishttps = true;
            
            url = new URL(urlYuk);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlYuk = "http://" + host + ":" + port;
            if (file != null) urlYuk +=  file;
            DebugPrint.println(DebugPrint.DEBUG, "yuk = " + urlYuk);
         }
         
         url = new URL(urlYuk);
         
         connection = new URLConnection2(url);
         
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         if (cookie.length() > 0) {
            connection.setRequestProperty("cookie", cookie);
         }
         connection.setDoOutput(false);
         connection.connect();
         
         in = connection.getInputStream();
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=br.readLine()) != null) {
              ; //System.out.println(line);
            }
         } catch(Throwable tt) {
            tt.printStackTrace(System.out);
            return null;
         }
         in.close();
         
        // If he is trying to send us somewhere welse, we truely don't know
        //  whats going on ... bag out
         urlYuk = connection.getHeaderField("location");
         if (urlYuk != null) {
            return null;
         }
        
        // Read and build a cookie response so the session works
         i=0;
         while((k=connection.getHeaderFieldKey(i)) != null) {
            String v = connection.getHeaderField(i++);
            if (k.equalsIgnoreCase("Set-Cookie")) {
               int semi = v.indexOf(";");
               if (semi >= 0) v = v.substring(0, semi);
               
               int eq = v.indexOf('=');
               if (eq >= 0) {
                  String key = v.substring(0, eq);
                  String val = v.substring(eq+1);
                  v = key + "=" + val; //URLEncoder.encode(val);
               }
               
               if (cookie.length() != 0) {
                  cookie += "; ";
               }
               cookie += v;
            }
         }
         
        //-----------------------------------------------------
        // K, should go to j_security_check (the post)
        //-----------------------------------------------------
         
         urlYuk = topurl + "/" + jcheckServlet;
         
        // HACK22 
         ishttps = false;
         if (urlYuk.indexOf("https") == 0) {
            urlYuk = "http" + urlYuk.substring(5);
            ishttps = true;
            
            url = new URL(urlYuk);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlYuk = "http://" + host + ":" + port;
            if (file != null) urlYuk +=  file;
            DebugPrint.println(DebugPrint.DEBUG, "yuk = " + urlYuk);
         }
         
         url = new URL(urlYuk);
         
         connection = new URLConnection2(url);
         
         connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
         
         connection.setDoOutput(true);
         connection.setUseCaches(false);
         if (cookie.length() > 0) {
            connection.setRequestProperty("cookie", cookie);
         }
         
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         connection.connect();
         
        // Set the body to the form data
         OutputStream out = connection.getOutputStream();
         String data = 
         "j_security_check=/j_security_check&j_username=" +
         URLEncoder.encode(user) + "&j_password=" +
         URLEncoder.encode(pw) + "&junkvar=junkval";
         out.write(data.getBytes());
         out.flush();
         out.close();
         
         urlYuk = connection.getHeaderField("location");
         
        // Read and build a cookie response so the session works
         i=0;
         while((k=connection.getHeaderFieldKey(i)) != null) {
            String v = connection.getHeaderField(i++);
            if (k.equalsIgnoreCase("Set-Cookie")) {
               int semi = v.indexOf(";");
               if (semi >= 0) v = v.substring(0, semi);
               
               int eq = v.indexOf('=');
               if (eq >= 0) {
                  String key = v.substring(0, eq);
                  String val = v.substring(eq+1);
                  v = key + "=" + val; //URLEncoder.encode(val);
               }
               
               if (cookie.length() != 0) {
                  cookie += "; ";
               }
               cookie += v;
            }
         }
         
         in = connection.getInputStream();
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=br.readLine()) != null) {
               ; //System.out.println(line);
            }
         } catch(Throwable tt) {
            tt.printStackTrace(System.out);
            return null;
         }
         in.close();               
         
         try {connection.disconnect();} catch(IOException dontcare){}
         
        //-----------------------------------------------------
        // K, should be telling me to go to my Original URL now
        //-----------------------------------------------------
         
        // If they are not having us go to login page ... bag out
         if (urlYuk == null) {
            DebugPrint.printlnd(DebugPrint.WARN,
                                "Misc:BTV: Not redirected! NULL: ");
            return null;
         }
         if (urlYuk.indexOf(redirServlet) < 0) {
            DebugPrint.printlnd(DebugPrint.WARN,
                                "Misc:BTV: Not going to redir!: " + 
                                urlYuk);
            return null;
         }
//         urlYuk = topurl + "/" + redirServlet;
         
        // HACK22 
         ishttps = false;
         if (urlYuk.indexOf("https") == 0) {
            urlYuk = "http" + urlYuk.substring(5);
            ishttps = true;
            
            url = new URL(urlYuk);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlYuk = "http://" + host + ":" + port;
            if (file != null) urlYuk +=  file;
            DebugPrint.println(DebugPrint.DEBUG, "yuk = " + urlYuk);
         }
         
         url = new URL(urlYuk);
         
         connection = new URLConnection2(url);
         
         connection.setDoOutput(false);
         connection.setUseCaches(false);
         if (cookie.length() > 0) {
            connection.setRequestProperty("cookie", cookie);
         }
         
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         connection.connect();
         
         urlYuk = connection.getHeaderField("location");
         
         try {connection.disconnect();} catch(IOException dontcare){}
         
         if (urlYuk != null) {
            String ret[] = null;
            int idx = urlYuk.indexOf("compname=");
            if (idx >= 0) {
               String token = urlYuk.substring(idx+9);
               idx = token.indexOf("&");
               if (idx >=0) {
                  token = token.substring(0, idx);
               }
               idx = urlYuk.indexOf("://");
               if (idx >= 0) {
                  idx = urlYuk.indexOf("/servlet", idx+3);
                  if (idx >= 0) {
                     ret = new String[2];
                     ret[0] = urlYuk.substring(0, idx);
                     ret[1] = token;
                  }
               }
               return ret;
            }
         } 
        
      } catch(Exception ee) {
         System.out.println("Error getting Connect Info");
         ee.printStackTrace(System.out);
      }
      return null;
   }
   
   public static String[] getConnectInfoBTVOLD(String user, String pw, 
                                               int op, String scope,
                                               String topurl, String irservlet,
                                               String redirServlet) {
                                            
      if (topurl == null) {
         topurl = "https://www-306.ibm.com:443";
      }
      if (irservlet == null) {
         irservlet = "servlet/oem/edge/IRServlet.wss";
      }
      if (redirServlet == null) {
         redirServlet =  "servlet/oem/edge/EdesignServicesServlet.wss";
      }
                                            
      try {
      
         Misc.setCommonInfo();
         
         String urlYuk;
         String redir = topurl + "/" + redirServlet + "?servicesop=" + op;
         
         if (scope != null && scope.trim().length() != 0) {
            redir += "&scope=" + URLEncoder.encode(scope);
         }
         
         urlYuk = irservlet + 
               "?userid=" + URLEncoder.encode(user)   +
               "&pass="   + URLEncoder.encode(pw) +
               "&url="    + URLEncoder.encode(redir);
         
         String urlString = topurl;
         
        // HACK22
        // This stinks ... need to trick things cause Servlet engine does NOT
        //   want to learn about HTTPS!!
         boolean ishttps = false;
         URL url = null;
         if (urlString.indexOf("https") == 0) {
            urlString = "http" + urlString.substring(5);
            ishttps = true;
            
            url = new URL(urlString);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlString = "http://" + host + ":" + port;
         }
         
         url = new URL(urlString + "/" + urlYuk);
         
         URLConnection2 connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
//         connection.setRequestProperty("userid", URLEncoder.encode(user));
//         connection.setRequestProperty("pass", URLEncoder.encode(pw));
//         connection.setRequestProperty("url", redir);
//         connection.setDoOutput(true);
         
         connection.connect();
//         connection.getOutputStream().close();
         
         InputStream in = null;
        /*
          in = connection.getInputStream();
          
          try {
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String line;
          while((line=br.readLine()) != null) {
          System.out.println(line);
          }
          } catch(Throwable tt) {
          tt.printStackTrace(System.out);
          br.close();
          return null;
          }
          in.close();
        */
         
        // Read and build a cookie response so the session works
         String k;
         int i=0;
         String cookie="";
         while((k=connection.getHeaderFieldKey(i)) != null) {
            String v = connection.getHeaderField(i++);
            if (k.equalsIgnoreCase("Set-Cookie")) {
               int semi = v.indexOf(";");
               if (semi >= 0) v = v.substring(0, semi);
               if (cookie.length() != 0) {
                  cookie += ";";
               }
               cookie += v;
            }
         }
         
         urlYuk = connection.getHeaderField("location");
         try {connection.disconnect();} catch(IOException dontcare){}
         if (urlYuk == null) {
            return null;
         }
         
//         if (cookie.length() != 0) {
//            urlYuk += "&cookie=" + URLEncoder.encode(cookie);
//         }
         
        // HACK22 
         ishttps = false;
         if (urlYuk.indexOf("https") == 0) {
            urlYuk = "http" + urlYuk.substring(5);
            ishttps = true;
            
            url = new URL(urlYuk);
            
            String host = url.getHost();
            int    port = url.getPort();
            String file = url.getFile();
            DebugPrint.println(DebugPrint.DEBUG, "host = " + host);
            DebugPrint.println(DebugPrint.DEBUG, "port = " + port);
            DebugPrint.println(DebugPrint.DEBUG, "file = " + file);
            if (port == -1 || port == 80) port = 443;
            urlYuk = "http://" + host + ":" + port;
            if (file != null) urlYuk +=  file;
            DebugPrint.println(DebugPrint.DEBUG, "yuk = " + urlYuk);
         }
         
         url = new URL(urlYuk);
         
         connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         
        // HACK22 
         if (ishttps) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         if (cookie.length() > 0) {
            connection.setRequestProperty("cookie", cookie);
         }
         connection.connect();
         
         urlYuk = connection.getHeaderField("location");
         
         connection.disconnect();
         if (urlYuk != null) {
            String ret[] = null;
            int idx = urlYuk.indexOf("compname=");
            if (idx >= 0) {
               String token = urlYuk.substring(idx+9);
               idx = token.indexOf("&");
               if (idx >=0) {
                  token = token.substring(0, idx);
               }
               idx = urlYuk.indexOf("://");
               if (idx >= 0) {
                  idx = urlYuk.indexOf("/servlet", idx+3);
                  if (idx >= 0) {
                     ret = new String[2];
                     ret[0] = urlYuk.substring(0, idx);
                     ret[1] = token;
                  }
               }
               return ret;
            }
         } 
        /*
         in = connection.getInputStream();
         
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=br.readLine()) != null) {
               System.out.println(line);
            }
            if (line == null) return null;
         } catch(Throwable tt) {
            tt.printStackTrace(System.out);
            return null;
         }
        */
      } catch(Exception ee) {
         System.out.println("Error getting Connect Info");
         ee.printStackTrace(System.out);
      }
      return null;
   }
   
   public static String[] getConnectInfoNJ(String user, String pw, 
                                           String op, String scope) {
      return getConnectInfoNJ(user, pw, op, scope,
                              "https://edesign.ihost.util.com/ets");  
   }
   public static String[] getConnectInfoNJ(String user, String pw, 
                                           String op, String scope,
                                           String urlIn) {
      return getConnectInfoNJ(user, pw, op, scope, urlIn, null);
   }
   public static String[] getConnectInfoNJ(String user, String pw, 
                                           String op, String scope,
                                           String urlIn, String kv) {
      try {
      
         if (useURLConnection2) {
            Misc.setCommonInfo();
         }
      
         String urlYuk;
         String redir = urlIn + 
                  "/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=" + op;
                  
         if (scope != null && scope.trim().length() != 0) {
            redir += "&scope=" + URLEncoder.encode(scope);
         }
         
         if (kv != null) {
            int idx = kv.indexOf("=");
            if (idx > 0) {
               redir += "&" + kv.substring(0, idx+1) + 
                  URLEncoder.encode(kv.substring(idx+1));
            }
         }
         
         urlYuk = "/servlet/oem/edge/ed/odc/desktop/loginpage2?userid=" + 
            URLEncoder.encode(user) + "&password=" + URLEncoder.encode(pw) + 
            "&redirect=" + URLEncoder.encode(redir);
            
         
         String urlString = urlIn;
         
         URL url = null;
         url = new URL(urlString + urlYuk);
      
         URLConnection connection = null;
         URLConnection2 c2 = null;
         if (useURLConnection2) {
            c2 = new URLConnection2(url);
            c2.setKeepAlive(false);
            connection = c2;
         } else {
            connection = url.openConnection();
         }
         connection.setUseCaches(false);
         connection.connect();
         
         InputStream in = null;
        /*
         in = connection.getInputStream();
         
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=br.readLine()) != null) {
               System.out.println(line);
            }
         } catch(Throwable tt) {
            tt.printStackTrace(System.out);
            br.close();
            return null;
         }
         in.close();
        */
         
        // Read and build a cookie response so the session works
         String k;
         int i=0;
         String cookie="";
         while((k=connection.getHeaderFieldKey(i)) != null) {
            String v = connection.getHeaderField(i++);
            if (k.equalsIgnoreCase("Set-Cookie")) {
               int semi = v.indexOf(";");
               if (semi >= 0) v = v.substring(0, semi);
               if (cookie.length() != 0) {
                  cookie += ";";
               }
               cookie += v;
            }
         }
         
         urlYuk = connection.getHeaderField("location");
         
         if (useURLConnection2) {
            try {c2.disconnect();} catch(IOException dontcare){}
         }
         
//         if (cookie.length() != 0) {
//            urlYuk += "&cookie=" + URLEncoder.encode(cookie);
//         }
         
         if (urlYuk == null) {
            return null;
         }
         
         url = new URL(urlYuk);
         
         if (useURLConnection2) {
            c2 = new URLConnection2(url);
            c2.setKeepAlive(false);
            connection = c2;
         } else {
            connection = url.openConnection();
         }
         connection = new URLConnection2(url);
         connection.setUseCaches(false);
         if (cookie.length() > 0) {
            connection.setRequestProperty("cookie", cookie);
         }
         connection.connect();
         
         urlYuk = connection.getHeaderField("location");
         
         if (useURLConnection2) {
            try {c2.disconnect();} catch(IOException dontcare){}
         }
         
         if (urlYuk != null) {
            String ret[] = null;
            int idx = urlYuk.indexOf("compname=");
            if (idx >= 0) {
               String token = urlYuk.substring(idx+9);
               idx = token.indexOf("&");
               if (idx >=0) {
                  token = token.substring(0, idx);
               }
               idx = urlYuk.indexOf("://");
               if (idx >= 0) {
                  idx = urlYuk.indexOf("/servlet", idx+3);
                  if (idx >= 0) {
                     ret = new String[2];
                     ret[0] = urlYuk.substring(0, idx);
                     ret[1] = token;
                  }
               }
               return ret;
            }
         } 
        /*
         in = connection.getInputStream();
         
         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line=br.readLine()) != null) {
               System.out.println(line);
            }
            if (line == null) return null;
         } catch(Throwable tt) {
            tt.printStackTrace(System.out);
            return null;
         }
        */
      } catch(Exception ee) {
         System.out.println("Error getting Connect Info");
         ee.printStackTrace(System.out);
      }
      return null;
   }
   
   public static String getConnectInfoGeneric(String user, String pw, 
                                              String op, String scope,
                                              String urlIn) {
      return getConnectInfoGeneric(user, pw, op, scope, urlIn, null);
   }
   public static String getConnectInfoGeneric(String user, String pw, 
                                              String op, String scope,
                                              String urlIn,
                                              String kv) {
      try {
      
         String r1[] = getConnectInfoNJ(user, pw, op, scope, urlIn, kv);
         if (r1 != null) return r1[1];
      
         Misc.setCommonInfo();
      
         String urlYuk = "/servlet/oem/edge/ed/odc/desktop/tokengen" + 
         "?op=" + URLEncoder.encode(op) + 
         "&userid=" + URLEncoder.encode(user) + 
         "&password=" + URLEncoder.encode(pw);
         
         if (kv != null) {
            int idx = kv.indexOf("=");
            if (idx > 0) {
               urlYuk += "&" + kv.substring(0, idx+1) + 
                  URLEncoder.encode(kv.substring(idx+1));
            }
         }
         
         if (scope != null && scope.trim().length() != 0) {
            urlYuk += "&scope=" + URLEncoder.encode(scope);
         }
         
         String urlString = urlIn;
         
         URL url = null;
         url = new URL(urlString + urlYuk);
      
         URLConnection2 connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         connection.setUseCaches(false);
         connection.connect();
         
         InputStream in = null;
         
         String compname = connection.getHeaderField("compname");
         try {connection.disconnect();} catch(IOException dontcare){}
         if (compname != null) {
            return compname;
         } 
      } catch(Exception ee) {
         System.out.println("Error getting Connect Info");
         ee.printStackTrace(System.out);
      }
      return null;
   }
   
   public static boolean getConnectInfoQP(String user, String pw) {
      return getConnectInfoQP(user, pw,
                       "https://qp.research.ibm.com/QuickPlace/space/Main.nsf");
   }
   public static boolean getConnectInfoQP(String user, String pw, 
                                          String urlIn) {
      try {
      
         Misc.setCommonInfo();
      
         String upw = user + ":" + pw;
         
       
         String b64 = oem.edge.ed.util.Base64.encode(upw.getBytes());
      
         String urlString = urlIn;
         boolean httpsHack = false;
         
        // HACK22
        // This stinks ... need to trick things cause Servlet engine does NOT
        //   want to learn about HTTPS!!
         if (urlString.startsWith("https:")) {
            urlString = "http" + urlIn.substring(5);
            httpsHack = true;
         }
         
         URL url = new URL(urlString);
         URLConnection2 connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         
        // HACK22 
         if (httpsHack) {
            connection.setProtocol("https");
         }
         
         connection.setUseCaches(false);
         connection.connect();
      
         int status1 = connection.getStatusInt();
         
         connection.disconnect();
         
         connection = new URLConnection2(url);
         connection.setKeepAlive(false);
         
        // HACK22 
         if (httpsHack) {
            connection.setProtocol("https");
         }
         connection.setUseCaches(false);
         
         connection.setRequestProperty("Authorization", "Basic " + b64);
         connection.connect();
         
         DebugPrint.println(DebugPrint.DEBUG, "Status1 = " + status1);
         DebugPrint.println(DebugPrint.DEBUG, "Status = "  +
                            connection.getStatusInt());
         if ((status1 == 403 || status1 == 401) && 
             connection.getStatusInt() < 400) {
            return true;
         }
         
      } catch(Exception ee) {
         System.out.println("Error getting Connect Info");
         ee.printStackTrace(System.out);
      }
      return false;
   }
   
   static public void main(String args[]) {
      
      if (args[0].equals("dump")) {      
         try {
      
            Misc.setCommonInfo();
            
            URL url = null;
            url = new URL(args[1]);
      
            URLConnection2 connection = new URLConnection2(url);
            connection.setKeepAlive(false);
            connection.setUseCaches(false);
            connection.connect();
            
            InputStream in = null;
            in = connection.getInputStream();
            
            try {
               BufferedReader br = new BufferedReader(new InputStreamReader(in));
               String line;
               while((line=br.readLine()) != null) {
                  System.out.println(line);
               }
            } catch(Throwable tt) {
               tt.printStackTrace(System.out);
               return;
            }
            in.close();
         } catch(Exception ee) { ee.printStackTrace(System.out); }
         
      } else if (args[0].equals("qp")) {
         System.out.println("QP auth = " + getConnectInfoQP(args[1], args[2]));
      } else if (args[0].equals("btv")) {
         System.out.println("gcibtv auth = " + 
                            getConnectInfoBTV(args[1], args[2], args[3],
                                              args[4]));
      } else {
         System.out.println("gci auth = " + 
                            getConnectInfoGeneric(args[0], args[1], args[2], 
                                                  args[3], args[4]));
      }
   }
}
