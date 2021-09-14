package oem.edge.ed.odc.util;

import java.lang.reflect.*;
import java.lang.*;
import java.net.*;
import java.io.*;


                    import java.security.Provider;
                    import java.security.Security;
                    import java.util.Set;
                    import java.util.Iterator;

/* SSLize the passed in socket. 
**
**  The socket will be setup in the Server role if  'server' is true, 
**   Client role otherwise.
**
**  If in the Server role, needClientAuth will dictate whether the client 
**   must authenticate with a valid/trusted certificate
**
**  If either ksi or tsi is non-null, then that object describes the keystore
**   or truststore info repectively (file and password). If absent, then the
**   default info is used as described by JSSE (properties OR defaults).
**
** Note: Reflection is used to access JSSE, if not found, NoSSLProvider exception
**       is raised.
**
*/
public class SSLizer {

   static protected boolean canDoJSSE    = false;
   static protected boolean havechecked  = false;
   static protected boolean reentryCheck = false;
   static protected Boolean lock         = new Boolean(true);
   static protected final String anonkeystore   
                 = "oem/edge/ed/odc/util/anonkeystore.jks";
   static protected final String anontruststore 
                 = "oem/edge/ed/odc/util/anontruststore.jks";
                 
   static protected String SSLSocketFactoryClassName    = null;
   static protected String SSLContextClassName          = null;
   static protected String KeyManagerArrayClassName     = null;
   static protected String TrustManagerArrayClassName   = null;
   static protected String KeyManagerFactoryClassName   = null;
   static protected String TrustManagerFactoryClassName = null;
   static protected String SecureRandomClassName        = null;
   static protected String SSLAnonTrustManagerClassName = null;
   static protected String SSLSocketClassName           = null;
   static protected String KeyStoreClassName            = null;
   
   
   static protected final String V14_SSLSocketFactoryClassName  
                 = "javax.net.ssl.SSLSocketFactory"; 
   static protected final String V14_SSLContextClassName        
                 = "javax.net.ssl.SSLContext"; 
   static protected final String V14_KeyManagerArrayClassName   
                 = "[Ljavax.net.ssl.KeyManager;"; 
   static protected final String V14_TrustManagerArrayClassName 
                 = "[Ljavax.net.ssl.TrustManager;"; 
   static protected final String V14_KeyManagerFactoryClassName 
                 = "javax.net.ssl.KeyManagerFactory";
   static protected final String V14_TrustManagerFactoryClassName 
                 = "javax.net.ssl.TrustManagerFactory";
   static protected final String V14_SecureRandomClassName      
                 = "java.security.SecureRandom"; 
   static protected final String V14_SSLAnonTrustManagerClassName 
                 = "oem.edge.ed.JSSE.SSLAnonTrustManager";
   static protected final String V14_SSLSocketClassName         
                 = "javax.net.ssl.SSLSocket";
   static protected final String V14_KeyStoreClassName
                 = "java.security.KeyStore";
                 
   static protected final String V103_SSLSocketFactoryClassName  
                 = "javax.net.ssl.SSLSocketFactory"; 
   static protected final String V103_SSLContextClassName        
                 = "com.ibm.net.ssl.SSLContext"; 
   static protected final String V103_KeyManagerArrayClassName   
                 = "[Lcom.ibm.net.ssl.KeyManager;"; 
   static protected final String V103_TrustManagerArrayClassName 
                 = "[Lcom.ibm.net.ssl.TrustManager;"; 
   static protected final String V103_KeyManagerFactoryClassName   
                 = "com.ibm.net.ssl.KeyManagerFactory";
   static protected final String V103_TrustManagerFactoryClassName 
                 = "com.ibm.net.ssl.TrustManagerFactory";
   static protected final String V103_SecureRandomClassName      
                 = "java.security.SecureRandom"; 
   static protected final String V103_SSLAnonTrustManagerClassName 
                 = "oem.edge.ed.JSSE.SSLAnonTrustManager103";
   static protected final String V103_SSLSocketClassName         
                 = "javax.net.ssl.SSLSocket";
   static protected final String V103_JSSEProviderClassName 
                 = "com.ibm.jsse.JSSEProvider";
   static protected final String V103_KeyStoreClassName
                 = "java.security.KeyStore";
   
  // For now, if we can load the SSLSocketFactory, thats good enough for me.
  //  We really need to see if there is a provider as well.
   static public boolean supportsAnonymousJSSE() {
      if (!havechecked) {
         synchronized(lock) {
            
           // Close the window ...
            if (havechecked) return canDoJSSE;
            
           // If we are doing the dance trying to figure it out, say we can do it
            if (reentryCheck) return true;
            
            try {
               reentryCheck = true;
               canDoJSSE = false;
               
               
              // Debug 
              /*
                Provider[] providers = Security.getProviders();
                for (int i = 0; i < providers.length; i++) {
                Provider provider = providers[i];
                
                System.out.println("Provider name: " + provider.getName());
                System.out.println("Provider information: " + provider.getInfo());
                System.out.println("Provider version: " + provider.getVersion());
                Set entries = provider.entrySet();
                Iterator iterator = entries.iterator();
                while (iterator.hasNext()) {
                System.out.println("Property entry: " + iterator.next());
                }
                }
              */
               System.out.println("SSLize Socket: Checking for valid JSSE environment");
               
              // Check 1.4+ JSSE first
               SSLSocketFactoryClassName    = V14_SSLSocketFactoryClassName;
               SSLContextClassName          = V14_SSLContextClassName;
               KeyManagerArrayClassName     = V14_KeyManagerArrayClassName;
               TrustManagerArrayClassName   = V14_TrustManagerArrayClassName;
               KeyManagerFactoryClassName   = V14_KeyManagerFactoryClassName;
               TrustManagerFactoryClassName = V14_TrustManagerFactoryClassName;
               SecureRandomClassName        = V14_SecureRandomClassName;
               SSLAnonTrustManagerClassName = V14_SSLAnonTrustManagerClassName;
               SSLSocketClassName           = V14_SSLSocketClassName;
               KeyStoreClassName            = V14_KeyStoreClassName;
               
               try {
                  System.out.println("SSLize Socket: Checking for 1.4+");
                  sslizeSocketAnonymous(null, false);
                  canDoJSSE = true;
                  System.out.println("Successful! Doing JSSE1.4+");
               } catch(SSLStartupException sse) {
                  System.out.print("SSLize Socket: Failed ...");
               }
               
              // If we did not find above, now look for 1.0.3 IBM 
               if (!canDoJSSE) {
                  SSLSocketFactoryClassName  = V103_SSLSocketFactoryClassName;
                  SSLContextClassName        = V103_SSLContextClassName;
                  KeyManagerArrayClassName   = V103_KeyManagerArrayClassName;
                  TrustManagerArrayClassName = V103_TrustManagerArrayClassName;
                  TrustManagerFactoryClassName = V103_TrustManagerFactoryClassName;
                  KeyManagerFactoryClassName = V103_KeyManagerFactoryClassName;
                  SecureRandomClassName      = V103_SecureRandomClassName;
                  SSLAnonTrustManagerClassName = V103_SSLAnonTrustManagerClassName;
                  SSLSocketClassName         = V103_SSLSocketClassName;
                  KeyStoreClassName          = V103_KeyStoreClassName;
                  try {
                     System.out.println(" Check 1.0.3 No Providers");
                     sslizeSocketAnonymous(null, false);
                     canDoJSSE = true;
                     System.out.println("Successful! Doing JSSE 1.0.3 No Provider added");
                  } catch(SSLStartupException sue) {
                     System.out.print("SSLize Socket: Failed ...");
                  }
               }
               
               if (!canDoJSSE) {
                  try {
                     System.out.println(" Check 1.0.3 w/ " +
                                        V103_JSSEProviderClassName + " added");
                     Class jspc = Class.forName(V103_JSSEProviderClassName);
                     Object pi  = jspc.newInstance();
                     java.security.Security.addProvider((Provider)pi);
                     sslizeSocketAnonymous(null, false);
                     canDoJSSE = true;
                     System.out.println("Successful! Doing JSSE 1.0.3 WITH JSSE Provider added");
                  } catch(Throwable t) {
                     System.out.println("SSLize Socket: Failed ... try sslight");
                  }
               }
               
            } finally {
              // Mark that we HAVE checked things out and reset the reentryCheck
              //  In a finally just in case we get an unexpected exception. We want
              //  to try the check just once
               havechecked = true;
               reentryCheck = false;
            }
         }
      }
      return canDoJSSE;
   }
      
   static public 
   Socket sslizeSocketAnonymous(Socket s, 
                                boolean server) throws SSLStartupException {
      
      KeystoreInfo anonksi, anontsi;                                
      
     // Sigh ... having a 'large' (21k) static array is problematic for 
     //  java init (methods must be < 64k && this drives it WELL past that)
     //  so, no static info ... suck it from a file
      
      ClassLoader cl = SSLizer.class.getClassLoader();
      anonksi = new KeystoreInfo(cl.getResourceAsStream(anonkeystore),
                                 "changeit");
      anontsi = new KeystoreInfo(cl.getResourceAsStream(anontruststore),
                                 "changeit");
      return sslizeSocket(s, server, false, anonksi, anontsi, true);
   }
      
   static
   public void dump(Object o) {
      dump(o.getClass());
   }
   static
   public void dump(Class c) {
      System.out.println("Dump for class '" + c.getName() + "'");
      System.out.println("\nParentage:");
      Class pc = c;
      String pad = "-- ";
      while(pc != null) {
         System.out.println(pad + pc.getName());
         pc = pc.getSuperclass();
         pad = "--" + pad;
         
      }
      
      System.out.println("\nMethods:");
      Method[] m = c.getDeclaredMethods();
      for(int i=0; i < m.length; i++) {
         System.out.print(m[i].getName() + "(");
         Class [] p = m[i].getParameterTypes();
         for(int j=0; j < p.length; j++) {
            if (j > 0) System.out.print(", ");
            System.out.print(p[j].getName());
         }
         System.out.println(")");
      }
   }
         
   static 
   public Socket sslizeSocket(Socket s, 
                              boolean server, 
                              boolean needClientAuth,
                              KeystoreInfo ksi,
                              KeystoreInfo tsi) throws SSLStartupException {
      return sslizeSocket(s, server, needClientAuth, ksi, tsi, false);
   }
   static 
   public Socket sslizeSocket(Socket s, 
                              boolean server, 
                              boolean needClientAuth,
                              KeystoreInfo ksi,
                              KeystoreInfo tsi, 
                              boolean anon) throws SSLStartupException {
      
      Socket ret = null;
      
     // Got to call this to get vectors filled in
      supportsAnonymousJSSE();
      
      try {
         Class sfc      = Class.forName(SSLSocketFactoryClassName);
         Class scc      = Class.forName(SSLContextClassName);
         Class[] cparms = new Class[] { };
         Object[] parms = null;
            
         Method sf_gdm  = sfc.getMethod("getDefault", cparms);
         cparms         = new Class[]  { java.net.Socket.class, String.class, 
                                         Integer.TYPE, Boolean.TYPE };
         Method sf_csm  = sfc.getMethod("createSocket", cparms);
            
        // Invoke static method to get fac
         Object sslfac; 
         if (ksi != null || tsi != null) {
            Object ksm[] = null;
            Object tsm[] = null;
            if (ksi != null) {
               ksm = getKeyManagers(ksi.getKeystoreStream(),
                                    ksi.getKeystorePassword());
            }
            if (tsi != null) {
               tsm = getTrustManagers(tsi.getKeystoreStream(),
                                      tsi.getKeystorePassword());
            }
            
            cparms         = new Class[]  { String.class };
            Method sc_gim  = scc.getMethod("getInstance", cparms);
            parms          = new Object[] { "SSL" };
            Object sslctxt = sc_gim.invoke(null, parms);
               
            Class kmac     = Class.forName(KeyManagerArrayClassName);
            Class tmac     = Class.forName(TrustManagerArrayClassName);
            Class srac     = Class.forName(SecureRandomClassName);
            cparms         = new Class[]  {kmac, tmac, srac};
            
           // If its anonymous, load our trustmanager
            if (anon) {
               Class mtmc = Class.forName(SSLAnonTrustManagerClassName);
               Object arr[] = (Object[])java.lang.reflect.Array.newInstance(mtmc, 1);
               if (arr != null && mtmc != null) {
                  java.lang.reflect.Array.set(arr, 0, mtmc.newInstance());
                  tsm = arr;
               } else {
                  System.out.println("SSLizer: anon specifed and AnonTrustManager not found!");
               }
            }
               
            parms          = new Object[] {ksm, tsm, null};
            Method sc_im   = scc.getMethod("init", cparms);
            sc_im.invoke(sslctxt, parms);
               
            cparms         = new Class[]  { };
            parms          = new Object[] { };
            Method sc_gsf  = scc.getMethod("getSocketFactory", cparms);
            sslfac         = sc_gsf.invoke(sslctxt, parms);
         } else {
            parms          = new Object[] {};
            sslfac         = sf_gdm.invoke(null, parms);
         }
            
        // If we are here checking if we SHOULD be here, and we made it this far,
        //  then it looks like we have a provider, and all is good in the world.
         if (reentryCheck) return null;
         
         parms             = new Object[] { s, s.getLocalAddress().getHostName(), 
                                            new Integer(s.getLocalPort()), 
                                            Boolean.TRUE};
         ret = (Socket)sf_csm.invoke(sslfac, parms);
            
         Class  ssc        = Class.forName(SSLSocketClassName);
         
         cparms            = new Class[]  { Boolean.TYPE };
         parms             = new Object[] { new Boolean(needClientAuth) };
         Method ss_sncam   = ssc.getMethod("setNeedClientAuth", cparms);
         ss_sncam.invoke(ret, parms);
            
         parms             = new Object[] { new Boolean(!server) };
         Method ss_sucmm   = ssc.getMethod("setUseClientMode", cparms);
         ss_sucmm.invoke(ret, parms);
            
         cparms            = new Class[]  { };
         parms             = new Object[] { };
         Method ss_shm     = ssc.getMethod("startHandshake", cparms);
         ss_shm.invoke(ret, parms);
            
      } catch (Throwable e) {
        // Be verbose if we are not checking things out ourselves
         if (!reentryCheck) {
            System.out.println("Error SSLizing socket");
            e.printStackTrace(System.out);
         }
         throw new SSLStartupException("Error doing SSLize");
      }
      return ret;
   }
      
  // TRY to load an SSL TrustManager list which uses the specified 
  //  truststore/pw combo
   static
   protected Object[] getStatedManagers(boolean tOrK, InputStream store,
                                        String pw) throws Throwable {
                                           
      Class  xmfc     = Class.forName(tOrK ?
                                      TrustManagerFactoryClassName :
                                      KeyManagerFactoryClassName);
      
      Class cparms[]  = new Class[]  { };
      Object parms[]  = new Object[] { };
      Method xmf_da   = xmfc.getMethod("getDefaultAlgorithm", cparms);
      String alg      = (String)xmf_da.invoke(null, parms);
         
      cparms          = new Class[]  { String.class };
      Method xmf_gi   = xmfc.getMethod("getInstance", cparms);
      parms           = new Object[] { alg };
      Object xmFact   = xmf_gi.invoke(null, parms);
         
      Class ksc       = Class.forName(KeyStoreClassName);
      cparms          = new Class[]  { String.class };
      Method ks_gi    = ksc.getMethod("getInstance", cparms);
      parms           = new Object[] { "jks" };
      Object ks       = ks_gi.invoke(null, parms);
         
         
      char pwarr[]    = pw.toCharArray();
      cparms          = new Class[]  { InputStream.class, pwarr.getClass() };
      Method ks_load  = ksc.getMethod("load", cparms);
      parms           = new Object[] { store, pwarr };
      ks_load.invoke(ks, parms);
         
      Method xmf_init = null;         
      if (tOrK) {
         cparms          = new Class[]  { ksc };
         xmf_init       = xmfc.getMethod("init", cparms);
         parms           = new Object[] { ks };
      } else {
         cparms          = new Class[]  { ksc, pwarr.getClass() };
         xmf_init        = xmfc.getMethod("init", cparms);
         parms           = new Object[] { ks, pwarr };
      }
      
      xmf_init.invoke(xmFact, parms);
         
      cparms          = new Class[]  { };
      parms           = new Object[] { };
      Method xmf_gtm  = xmfc.getMethod(tOrK?"getTrustManagers":"getKeyManagers",
                                       cparms);
      Object ret[]    = (Object[])xmf_gtm.invoke(xmFact, parms);
      return ret;
   }
  
  // TRY to load an SSL TrustManager list which uses the specified 
  //  truststore/pw combo
   static 
   protected Object[] getKeyManagers(InputStream store, String pw) throws Throwable {
      return getStatedManagers(false, store, pw);
   }
   static
   protected Object[] getTrustManagers(InputStream store,
                                       String pw) throws Throwable {
      return getStatedManagers(true, store, pw);
   }      
}
