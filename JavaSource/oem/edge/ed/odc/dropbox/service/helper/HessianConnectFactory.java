package oem.edge.ed.odc.dropbox.service.helper;


import java.net.URL;
import java.util.HashMap;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;

import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.IOException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import com.caucho.hessian.client.HessianProxyFactory;
import oem.edge.ed.odc.util.ProxyDebug;
import oem.edge.ed.odc.util.ProxyDebugInterface;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
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
 * Create an instance of this Factory class used to create and manage DropboxAccess
 *  proxies which use the Hessian protocol to communicate with the dropbox. 
 */
public class HessianConnectFactory implements ConnectionFactory {

   protected class WeakKeyWrapper {
      protected WeakReference key;
      
      public WeakKeyWrapper(Object inkey) {
         key = new WeakReference(inkey);
      }
      
      public Object getKey() { return key.get(); }
      
     // Referencial equals only
      public boolean equals(Object inkey) {
         if (inkey != null && inkey instanceof WeakKeyWrapper) {
            inkey = ((WeakKeyWrapper)inkey).getKey();
         }
         
         if (inkey == key.get()) {
            return true;
         }
         return false;
      }
      
      public int hashCode() {
         if (key == null) return 0;
            return key.get().hashCode();
      }
   }
   
   
   HashMap hashmap = new HashMap();
   
   URL topurl;
   URL url;
   String uri;
   
   public String getName() { return "Hessian"; }
   
   public void setURL(URL url) throws MalformedURLException {
      this.url = new URL(url.toExternalForm());
   }
   public void setTopURL(URL url) throws MalformedURLException {
      this.topurl = new URL(url.toExternalForm());
      this.url = null;
   }
   public String getTopURL() {
      if (topurl != null) return topurl.toExternalForm();
      return null;
   }
   public void setURI(String s) {
      this.uri = s;
      this.url = null;
   }
   public String getURI() {
      return uri;
   }
   
   public URL getURL() throws MalformedURLException {
      if (url != null) return new URL(url.toExternalForm());
      if (topurl == null || uri == null) {
         throw new MalformedURLException("TOP URL or URI not set");
      }
      return new URL(topurl.toString() + "/" + uri);
   }
   
  // Create a new proxy for the service. URL must already be set (if needed)
   public DropboxAccess getProxy() throws DboxException, RemoteException {
      
      if (uri == null) uri = "DboxService";
      
      try {
         URL lurl = getURL();
         HessianProxyFactory factory;
         
        //
        // Create the HessianProxyFactory and overload the createConnection routine 
        //  to slide in the HostnameVerifier allowing operation in test envs where
        //  the site certificate has a mismatching hostname
        //
         factory = new HessianProxyFactory() {
               protected URLConnection createConnection(URL url)  throws IOException {
                  URLConnection urlconn = super.createConnection(url);
                  HttpConnection.modifyConnection(urlconn);
                  return urlconn;
               }
            };
            
         factory.setOverloadEnabled(true);
         
         
        //System.out.println("Returning factory from: " + lurl.toString());
         DropboxAccess proxy = (DropboxAccess)factory.create(DropboxAccess.class, 
                                                             lurl.toExternalForm());
                                                             
        // Wrap it in a ProxyDebug, and add this factory instance into map
         ProxyDebug dprox = new ProxyDebug(proxy);
         
         dprox.setProxiedInfo("FACTORY", this);
         proxy = (DropboxAccess)dprox.makeProxy();
                                                             
        // Limit the output type (name) to DropboxAccess ... avoid HessianProxy ...
         dprox.setName(DropboxAccess.class);
         
         hashmap.put(new WeakKeyWrapper(proxy), new WeakReference(factory));
         
         return proxy;
         
      } catch(Exception e) {
         throw new DboxException("Error creating Hessian proxy", e);
      }
   }
   
   protected HessianProxyFactory getFactory(DropboxAccess proxy) {
   
      HessianProxyFactory factory = null;      
      
      WeakReference weakref = (WeakReference)hashmap.get(new WeakKeyWrapper(proxy)); 
      factory = (HessianProxyFactory)weakref.get();
      
      return factory;
   }
   
  // Set the SessionID info for the proxy
   public void setSessionId(DropboxAccess proxy, 
                            HashMap sessionmap) 
      throws DboxException, RemoteException {
      
      try {
         HessianProxyFactory factory = getFactory(proxy);
         factory.setHeader("dboxsessionid", sessionmap.get(proxy.SessionID));
      } catch(Exception e) {
         throw new DboxException("Error setting SessionID", e);
      }
   }
   
  // Get the SessionID associated with the proxy
   public String getSessionId(DropboxAccess proxy) {
      String ret = null;
      HessianProxyFactory factory = getFactory(proxy);
      if (factory != null) {
         ret = (String)factory.getHeader("dboxsessionid");
      }
      return ret;
   }
}
