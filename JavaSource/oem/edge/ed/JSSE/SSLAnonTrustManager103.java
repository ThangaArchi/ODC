package oem.edge.ed.JSSE;

import com.ibm.net.ssl.*;
import java.security.cert.*;
public class SSLAnonTrustManager103 implements X509TrustManager {

   public boolean isServerTrusted(X509Certificate[] chain) {
      
      return true;
   }
   
   public boolean isClientTrusted(X509Certificate[] chain) {
      return true;
   }
   public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
   }
}
