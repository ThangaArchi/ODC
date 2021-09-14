package oem.edge.ed.JSSE;

import javax.net.ssl.*;
import java.security.cert.*;

public class SSLAnonTrustManager implements X509TrustManager {

   public void checkClientTrusted(X509Certificate[] chain, 
                                  String authType) throws CertificateException {
      ;
   }
   
   public void checkServerTrusted(X509Certificate[] chain, 
                                  String authType) throws CertificateException {
      ;
   }
   public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
   }
}
