package oem.edge.ed.odc.dropbox.service.helper;


import java.net.URL;
import java.util.HashMap;
import java.lang.reflect.*;

import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.IOException;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2007                                         */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * Used to materialze URL connections. Applies Hostname verifier and
 *  proxy authorization logic
 * 
 */
public class HttpConnection {

  // Control installation of HostnameVerifier
   static boolean allowHostMismatch = false;
   static boolean doMismatchSet     = false;
   static Boolean lock = new Boolean(true);
   
   public static void setAllowHostnameMismatch(boolean v) {
      synchronized(lock) {
         allowHostMismatch = v;
         doMismatchSet = true;
      }
   }
   
   public static boolean getAllowHostnameMismatch() { return allowHostMismatch; }
   
   public static void modifyConnection(URLConnection urlconn) {
      try {
         boolean lallowHostMismatch;
         
        // If the var was not set programatically ... look in System property
         synchronized(lock) {
            if (!doMismatchSet) {
               doMismatchSet = true;
               String dhv = System.getProperty("DropboxHostnameVerifier");
               if (dhv != null) {
                  allowHostMismatch = dhv.equalsIgnoreCase("true");
               }
            }
            lallowHostMismatch = allowHostMismatch;
         }
         
         if (lallowHostMismatch) {
            Class httpsconnclass = 
               Class.forName("javax.net.ssl.HttpsURLConnection");
           // If we have an HttpsURLConnection class derivative
            if (httpsconnclass.isAssignableFrom(urlconn.getClass())) {
               
               Class chv_class = 
                  Class.forName("oem.edge.ed.JSSE.ConfigurableHostnameVerifier");
               Object chv = chv_class.newInstance();
               
               Method shv_meth = 
                  httpsconnclass.getMethod("setHostnameVerifier", 
                                           new Class[] { 
                                              Class.forName("javax.net.ssl.HostnameVerifier")
                                           });
               shv_meth.invoke(urlconn, new Object[] { chv });
            }
         }
      } catch(Throwable tt) {
         System.out.println("Exception while trying to add in HostVerifier");
         tt.printStackTrace(System.out);
      }
      
                  
     // To support HTTP Proxy with Basic Authentication, must get the
     //  base64 encoded userid/password into connection request. 
     // This SHOULD have been supportable OUTSIDE of the app! Dropbox
     // clients DO set proxyHost and proxyPort as well as proxyAuth properties
     // The two former SHOULD get this going thru the proxy, the latter needs
     // our direct attention here.
      try {
         String auth = 
            URLConnection.getDefaultRequestProperty("Proxy-Authorization");
         if (auth == null) {
            auth=System.getProperty("Proxy-Authorization");
         }
         
         if (auth == null) {
            auth=System.getProperty("proxyAuth");
            if (auth != null) {
               auth = "Basic " + auth;
            }
         }
         
         if (auth != null) {
            urlconn.setRequestProperty("Proxy-Authorization", 
                                       auth);
         }
      } catch(Exception eee) {
         System.out.println("Exception while trying to setup Basic Authentication");
         eee.printStackTrace(System.out);
      }
   }
}
