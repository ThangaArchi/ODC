package oem.edge.ed.odc.dropbox.client.soa;

import java.net.URL;
import java.util.HashMap;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import oem.edge.ed.odc.dsmp.common.DboxException;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;

public interface ConnectionFactory {

  // Returns a descriptive name for proxy type
   public String getName();

  // Sets the entire URL
   public void setURL(URL url)  throws MalformedURLException;
   
  // Sets the sub pieces of the URL (topURL is up to and incl context)
   public void setTopURL(URL url)  throws MalformedURLException;
   public void setURI(String s);
   
   public String getTopURL();
   public String getURI();
   
  // Create a new proxy for the service. URL must already be set (if needed)
   public DropboxAccess getProxy() throws DboxException, RemoteException;
   
  // Set the SessionID info for the proxy
   public void setSessionId(DropboxAccess proxy, HashMap sessionMap)
      throws DboxException, RemoteException;
      
  // Get the SessionID associated with the proxy
   public String getSessionId(DropboxAccess proxy);
}
