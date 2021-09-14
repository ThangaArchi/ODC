package oem.edge.ed.odc.dropbox.client.soa;

import java.net.URL;
import java.util.HashMap;
import java.lang.ref.WeakReference;

import java.lang.reflect.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.util.ProxyDebug;

import java.net.MalformedURLException;

public class JAXRPCConnectFactory implements ConnectionFactory {
   
   public class WeakKeyWrapper {
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
   
   class Info {
      public javax.xml.rpc.Service service;
      public String                sessionid;
   }
   
  // This will contain WeakKeyWrapper(proxy) -> Info[service, SessionidString]
   HashMap hashmap = new HashMap();
   
   URL topurl;
   URL url;
   String uri;
   
   public String getName() { return "JAXRPC"; }
   
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
      
      if (uri == null) uri = "DropboxAccessWebSvc";
      
      try {
      
         URL lurl = getURL();
         
        //DropboxAccessServiceLocator loc = new DropboxAccessServiceLocator();
        //DropboxAccessWebSvcSoapBindingStub binding;
        //binding = (DropboxAccessWebSvcSoapBindingStub)loc.getDropboxAccessWebSvc(lurl);
        // Get the service from the stub ... this is impl specific
        //service = binding._getService();
        
        // Use reflection so we can support different stub impls w/o recompile
         Class locclass = 
            Class.forName("oem.edge.ed.odc.dropbox.service.DropboxAccessServiceLocator");
         Object loc = locclass.newInstance();
         
         Method locmeth = locclass.getMethod("getDropboxAccessWebSvc", 
                                             new Class[] { URL.class });
         DropboxAccess binding = 
            (DropboxAccess)locmeth.invoke(loc, new Object[] {lurl});
         
         Method svcmeth = binding.getClass().getMethod("_getService", 
                                                       new Class[0]);
         
        // Get the service from the stub ... this is impl specific
         Info info = new Info();
         info.service = (javax.xml.rpc.Service)svcmeth.invoke(binding, new Object[0]);
         hashmap.put(new WeakKeyWrapper(binding), info);
         
         return (DropboxAccess)binding;
         
      } catch(Exception e) {
         throw new DboxException("Error creating JAXRPC proxy", e);
      }
   }
   
   public Info getInfo(DropboxAccess proxy) {
   
     // HACK ... support the proxy wrapped in DebugProxy wrapper 
      try {
         Object o = java.lang.reflect.Proxy.getInvocationHandler(proxy);      
         if (o instanceof ProxyDebug) {
            proxy = (DropboxAccess)((ProxyDebug)o).getProxiedObject();
         }
      } catch(Exception e) {
      }
   
      Info info = (Info)hashmap.get(new WeakKeyWrapper(proxy));
      return info;
   }
   
  // Set the SessionID info for the proxy
   public void setSessionId(DropboxAccess proxy, 
                            HashMap sessionmap) 
      throws DboxException, RemoteException {
      
      try {
      
         Info info = getInfo(proxy);

        // Now register the SessionIdHandler on the service handler registry
         javax.xml.rpc.handler.HandlerRegistry reg  = 
            info.service.getHandlerRegistry();
            
        // Put the current sessionid in the info struct
         info.sessionid = (String)sessionmap.get(proxy.SessionID);
         
         javax.xml.namespace.QName portname = 
            new javax.xml.namespace.QName("", "DropboxAccessWebSvc");

        // Get current chain
         java.util.List chain;            
         chain = reg.getHandlerChain(portname);
         if (chain == null) chain = new java.util.ArrayList();
         
        // Search for our handler 
         java.util.Iterator it = chain.iterator();
         while(it.hasNext()) {
            javax.xml.rpc.handler.HandlerInfo hi = 
               (javax.xml.rpc.handler.HandlerInfo)it.next();
            
           // If found, remove the entry
            if (JAXRPCSessionIdHandler.class.isAssignableFrom(hi.getHandlerClass())) {
               it.remove();
               break;
            }
         }
         
        // Add new handler with sessionmap as config
         chain.add(new javax.xml.rpc.handler.HandlerInfo(
                      JAXRPCSessionIdHandler.class, sessionmap, null));
         reg.setHandlerChain(portname, chain);
         
      } catch(Exception e) {
         throw new DboxException("Error setting SessionID", e);
      }
   }
   
  // Get the SessionID associated with the proxy
   public String getSessionId(DropboxAccess proxy) {
      String ret = null;
      Info info = getInfo(proxy);
      
      if (info != null) ret = info.sessionid;
      return ret;
   }
}
