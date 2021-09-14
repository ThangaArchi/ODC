package oem.edge.ed.JSSE;

import javax.net.ssl.*;

/**
 * Implements the HostnameVerifier interface to allow us to contorl if we will accept
 *  a certificate which has a hostname that does not match the actual URL hostname.
 *<p>
 * The default operation of this class is to simply return true (accept). The intention
 *  is that this class will be expanded (if needed) to allow configuration of which 
 *  certificates "names" can be accepted ...
 */
public class ConfigurableHostnameVerifier implements HostnameVerifier {
   boolean simpleVerify = true;
   boolean simpleValue  = true;
   public boolean verify(String hostname, SSLSession session) {
      if (simpleVerify) return simpleValue;
      
      return false;
   }
}
