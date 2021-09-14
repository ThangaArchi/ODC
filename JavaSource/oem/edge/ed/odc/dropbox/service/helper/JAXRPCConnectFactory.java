package oem.edge.ed.odc.dropbox.service.helper;

import java.net.URL;
import java.util.HashMap;
import java.lang.ref.WeakReference;

import java.lang.reflect.*;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;

import oem.edge.ed.odc.util.ProxyDebug;
import oem.edge.ed.odc.util.ProxyDebugInterface;

import java.net.MalformedURLException;

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
 *  proxies which use the JAXRPC protocol to communicate with the dropbox. 
 * <p>
 * When the factory is asked to getProxy(), it will load the class specified
 *  via getLocatorClassName. It will invoke the method taking a single parameter
 *  of URL, whose name is obtained via getBindMethodName.  The proxy that is 
 * and will then invoke the getDropboxAccessWebSvc to access the binding (proxy).
 * from that binding process MUST support a method taking no parameters and
 * returning the javax.xml.rpc.Service associated with the proxy.
 * The Service object is used to inject the SessionID into the Soap envelope for
 * each service invocation.
 *
 * <pre>
 *    Defaults:
        getPortName          = "DropboxAccessWebSvc"
        getBindMethodName    = "getDropboxAccessWebSvc"
        getServiceMethodName = "_getService"
        getLocatorClassName  = "oem.edge.ed.odc.dropbox.service.DropboxAccessServiceLocator"
 *      
 * </pre>
 */
public class JAXRPCConnectFactory implements ConnectionFactory {
   
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
   
   class Info {
      public javax.xml.rpc.Service service;
      public String                sessionid;
      public String                portName;
   }
   
  // This will contain WeakKeyWrapper(proxy) -> Info[service, SessionidString, PortName]
   HashMap hashmap = new HashMap();
   
   URL topurl;
   URL url;
   String uri;
   
   String portName          = "DropboxAccessWebSvc";
   String bindMethodName    = "getDropboxAccessWebSvc";
   String serviceMethodName = "_getService";
   String locatorClassName  = "oem.edge.ed.odc.dropbox.service.DropboxAccessServiceLocator";
   
  /**
   * Set the port name value to connect to the dropbox
   * @param v  port name value to connect to dropbox
   */
   public void   setPortName(String v)          { portName = v;             }
  /**
   * Get the port name value to connect to the dropbox
   * @return  port name value to connect to dropbox
   */
   public String getPortName()                  { return portName;          }
  /**
   * Set the method name used to obtain the proxy from the locator
   * @param v  method name used to obtain the proxy from the locator
   */
   public void   setBindMethodName(String v)    { bindMethodName = v;       }
  /**
   * Get the method name used to obtain the proxy from the locator
   * @return  method name used to obtain the proxy from the locator
   */
   public String getBindMethodName()            { return bindMethodName;    }
  /**
   * Set the method name used to obtain the Service object from the bound proxy
   * @param v method name used to obtain the Service object from the bound proxy
   */
   public void   setServiceMethodName(String v) { serviceMethodName = v;    }
  /**
   * Get the method name used to obtain the Service object from the bound proxy
   * @return method name used to obtain the Service object from the bound proxy
   */
   public String getServiceMethodName()         { return serviceMethodName; }
  /**
   * Set the class name for the locator class which will create the DropboxAccess proxy
   * @param v  class name for the locator class which will create the DropboxAccess proxy
   */
   public void   setLocatorClassName(String v)  { locatorClassName = v;     }
  /**
   * Get the class name for the locator class which will create the DropboxAccess proxy
   * @return class name for the locator class which will create the DropboxAccess proxy
   */
   public String getLocatorClassName()          { return locatorClassName;  } 
   
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
      
      if (uri == null) uri = getPortName(); //"DropboxAccessWebSvc";
      
      try {
      
         URL lurl = getURL();
         
        //DropboxAccessServiceLocator loc = new DropboxAccessServiceLocator();
        //DropboxAccessWebSvcSoapBindingStub binding;
        //binding = (DropboxAccessWebSvcSoapBindingStub)loc.getDropboxAccessWebSvc(lurl);
        // Get the service from the stub ... this is impl specific
        //service = binding._getService();
        
        // Use reflection so we can support different stub impls w/o recompile
         Class locclass = 
            Class.forName(locatorClassName);
         Object loc = locclass.newInstance();
         
         Method locmeth = locclass.getMethod(getBindMethodName(), 
                                             new Class[] { URL.class });
         DropboxAccess binding = 
            (DropboxAccess)locmeth.invoke(loc, new Object[] {lurl});
         
         Method svcmeth = binding.getClass().getMethod(getServiceMethodName(),
                                                       new Class[0]);
         
        // Get the service from the stub ... this is impl specific
         Info info     = new Info();
         info.service  = (javax.xml.rpc.Service)svcmeth.invoke(binding, new Object[0]);
         info.portName = getPortName();
         
        // Wrap it in a ProxyDebug, and add this factory instance into map
         ProxyDebug dprox = new ProxyDebug(binding);
         dprox.setProxiedInfo("FACTORY", this);
         binding = (DropboxAccess)dprox.makeProxy();
         
         hashmap.put(new WeakKeyWrapper(binding), info);
         
         return binding;
         
      } catch(Exception e) {
         throw new DboxException("Error creating JAXRPC proxy", e);
      }
   }
   
   protected Info getInfo(DropboxAccess proxy) {
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
            new javax.xml.namespace.QName("", info.portName);

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
