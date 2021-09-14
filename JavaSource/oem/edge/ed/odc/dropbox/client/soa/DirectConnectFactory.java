package oem.edge.ed.odc.dropbox.client.soa;

import java.net.URL;
import java.util.HashMap;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;

public class DirectConnectFactory implements ConnectionFactory {
   
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
         DropboxAccess dropbox = (DropboxAccess)das.newInstance();
         
         Method m = das.getMethod("useDirect", new Class[] { boolean.class });
         m.invoke(dropbox, new Object[] { new Boolean(true) });
         return dropbox;
      } catch(Exception e) {
         throw new DboxException("Error creating direct proxy", e);
      }
   }
   
  // Set the SessionID info for the proxy
   public void setSessionId(DropboxAccess proxy, 
                            HashMap sessionmap) 
      throws DboxException, RemoteException {
      
      try {
         Class das = Class.forName("oem.edge.ed.odc.dropbox.server.DropboxAccessSrv");
         if (das != null && das.isInstance(proxy)) {
            Method m = das.getMethod("setThreadSessionID", new Class[] { String.class });
            m.invoke(proxy, new Object[] { sessionmap.get(proxy.SessionID) });
         }
      } catch(Exception e) {
         throw new DboxException("Error setting SessionID", e);
      }
   }
   
  // Set the SessionID info for the proxy
   public String getSessionId(DropboxAccess proxy) {
      String ret = null;
      try {
         Class das = Class.forName("oem.edge.ed.odc.dropbox.server.DropboxAccessSrv");
         if (das != null && das.isInstance(proxy)) {
            Method m = das.getMethod("getThreadSessionID", new Class[0]);
            ret = (String)m.invoke(proxy, new Object[0]);
         }
      } catch(Exception e) {
      }
      return ret;
   }
   
}
