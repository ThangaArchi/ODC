package oem.edge.ed.odc.dropbox.service.helper;

import java.net.URL;
import java.util.HashMap;

import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;

import oem.edge.ed.odc.util.ProxyDebug;
import oem.edge.ed.odc.util.ProxyDebugInterface;
import oem.edge.ed.odc.util.ProxyDebugPrePost;

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
 *  proxies which use the DIRECT access method to communicate with the dropbox. This
 *  is not a remote access scheme, and is primarily for debug at this point.
 *
 */
public class DirectConnectFactory implements ConnectionFactory {

   HashMap hashmap = new HashMap();
   
   
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
   
  // This proxy will ensure that
   protected class SessionIdHandler implements ProxyDebugPrePost {
      String sessionid;
      Method setMeth;
      public SessionIdHandler() {
      
         try {
            Class das =
               Class.forName("oem.edge.ed.odc.dropbox.server.DropboxAccessSrv");
            setMeth   = das.getMethod("setThreadSessionID", new Class[] { String.class });
         } catch(Exception e) {
         }
      }
      public void setSessionId(String s) {
         sessionid = s;
      }
      public String getSessionId() {
         return sessionid;
      }
      
      public void preCall(Object obj) {
         try {
           //System.out.println("Precall invoking sessionid of " + sessionid);
            setMeth.invoke(obj, new Object[] { sessionid });
         } catch(Exception e) {
            e.printStackTrace(System.out);
         }
      }
      
      public void postCall(Object obj) {
         try {
           //System.out.println("Postcall invoking sessionid of null");
            setMeth.invoke(obj, new Object[] { null });
         } catch(Exception e) {
            e.printStackTrace(System.out);
         }
      }
   }
   
  // These are NOOPs for direct
   public void setURL(URL url)  throws MalformedURLException {
   }
   public void setTopURL(URL url)  throws MalformedURLException {
   }
   public String getTopURL() {
      return null;
   }
   public void setURI(String s) {
   }
   public String getURI() {
      return null;
   }
   
   public String getName() { return "Direct"; }
   
  // Create a new proxy for the service. URL must already be set (if needed)
   public DropboxAccess getProxy() throws DboxException, RemoteException {
      try {
         Class das =
            Class.forName("oem.edge.ed.odc.dropbox.server.DropboxAccessSrv");
            
         Method getSingleton = das.getMethod("getSingleton", null);
         
         DropboxAccess dropbox = (DropboxAccess)getSingleton.invoke(null, null);
         
        // Wrap it in a ProxyDebug, and add this factory instance into map
         ProxyDebug dprox = new ProxyDebug(dropbox);
         dprox.setProxiedInfo("FACTORY", this);
         
        // We add this pre/post caller stuff to get the sessionid set/reset
        //  for each call
         SessionIdHandler hand = new SessionIdHandler();
         dprox.addPrePostCaller(hand);
         
         dropbox = (DropboxAccess)dprox.makeProxy();
         
         hashmap.put(new WeakKeyWrapper(dropbox), new WeakReference(hand));
         
         return dropbox;
      } catch(Exception e) {
         throw new DboxException("Error creating direct proxy", e);
      }
   }
   
   protected SessionIdHandler getHandler(DropboxAccess proxy) {
   
      SessionIdHandler ret = null;      
      
      WeakReference weakref = (WeakReference)hashmap.get(new WeakKeyWrapper(proxy)); 
      ret = (SessionIdHandler)weakref.get();
      
      return ret;
   }
   
   
  // Set the SessionID info for the proxy
   public void setSessionId(DropboxAccess proxy, 
                            HashMap sessionmap) 
      throws DboxException, RemoteException {
      
      try {
         SessionIdHandler hand = getHandler(proxy);
         hand.setSessionId((String)sessionmap.get(proxy.SessionID));
      } catch(Exception e) {
         throw new DboxException("Error setting SessionID", e);
      }
   }
   
  // Get the SessionID associated with the proxy
   public String getSessionId(DropboxAccess proxy) {
      String ret = null;
      SessionIdHandler hand = getHandler(proxy);
      if (hand != null) {
         ret = (String)hand.getSessionId();
      }
      return ret;
   }
   
}
