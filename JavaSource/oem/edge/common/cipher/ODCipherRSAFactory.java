package oem.edge.common.cipher;

import oem.edge.common.RSA.*;
import java.util.*;
import java.io.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
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
 * Factory class for ODCipherRSA instances. There is a preferred default which is
 *  set when a factory class is created via the newFactoryInstance method. That 
 *  default is overridden by<br />
 *<ul>
 *<li>  calling the newFactoryInstance method taking the class string
 *<li>  or, if using the default newInstanceMethod, then the system property 
 *      ODCIPHERCLASS is checked, and if it has a value, that will be used for the
 *      factory instance.
 *</ul>
 */
public class ODCipherRSAFactory {

  // Default ODCiperRSA class returned from factory
   String rsaclass = "oem.edge.common.cipher.ODCipherRSAJCE";
   
   protected void setRSAClassname(String s) { rsaclass = s; }

  /**
   * Generate a new instance of a factory
   */
   public static ODCipherRSAFactory newFactoryInstance() {
      ODCipherRSAFactory ret = new ODCipherRSAFactory();
      try {
         String cls = System.getProperty("ODCIPHERCLASS");
         if (cls != null) ret.setRSAClassname(cls);
      } catch(Exception e) {}
      return ret;
   }
   
  /**
   * Generate a new instance of a factory
   */
   public static ODCipherRSAFactory newFactoryInstance(String cls) {
      ODCipherRSAFactory ret = newFactoryInstance();
      if (cls != null) ret.setRSAClassname(cls);
      return ret;
   }
   
   public ODCipherRSA newInstance() throws Exception {
      return (ODCipherRSA)Class.forName(rsaclass).newInstance();
   }

   public ODCipherRSA newInstance(int len) throws Exception {
      ODCipherRSA c = (ODCipherRSA)Class.forName(rsaclass).newInstance();
      c.initializeKeyPair(len);
      return c;
   }
   
   public ODCipherRSA newInstance(String f) throws Exception {
      ODCipherRSA c = (ODCipherRSA)Class.forName(rsaclass).newInstance();
      c.load(f);
      return c;
   }
   
   public static void main(String args[]) {
      String jceclass    = "oem.edge.common.cipher.ODCipherRSAJCE";
      String simpleclass = "oem.edge.common.cipher.ODCipherRSASimple";
   
      try {
         System.out.println("RSACipher main Starting " + (new Date()).toString());
      
         ODCipherRSA cipher = ODCipherRSAFactory.newFactoryInstance().newInstance();
         cipher.initializeKeyPair(1024);
      
         for(int i=0; i < args.length; i++) {
            if        (args[i].equalsIgnoreCase("JCE")) {
               cipher = ODCipherRSAFactory.newFactoryInstance(jceclass).newInstance();
            } else if (args[i].equalsIgnoreCase("Simple")) {
               cipher = ODCipherRSAFactory.newFactoryInstance(simpleclass).newInstance();
            } else if (args[i].equalsIgnoreCase("storeprivkey")) {
               try {
                  RSAKeyPair old = cipher.getKeyPair();
                  RSAPublicKey pub = old.getPublicKey();
                  old.setPublicKey(null);
                  cipher.setKeyPair(old);
                  cipher.store(args[++i]);
                  old.setPublicKey(pub);
                  cipher.setKeyPair(old);
               } catch(Exception e) {
                  System.out.println("Error storing to file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("storepubkey")) {
               try {
                  RSAKeyPair old = cipher.getKeyPair();
                  RSAPrivateKey pri = old.getPrivateKey();
                  old.setPrivateKey(null);
                  cipher.setKeyPair(old);
                  cipher.store(args[++i]);
                  old.setPrivateKey(pri);
                  cipher.setKeyPair(old);
               } catch(Exception e) {
                  System.out.println("Error storing to file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("storekeys")) {
               try {
                  cipher.getKeyPair().store(args[++i]);
               } catch(Exception e) {
                  System.out.println("Error storing to file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("setkeylen")) {
               try {
                  int len = Integer.parseInt(args[++i]);
                  cipher.initializeKeyPair(len);
                  System.out.println("Key generated with length " + len);
               } catch (Throwable t) {
                  System.out.println("setkeylen requires an int keylen parameter!");
                  return;
               }
            } else if (args[i].equalsIgnoreCase("loadkeys")) {
               try {
                  cipher.load(args[++i]);
               } catch(Exception e) {
                  System.out.println("Error loading from file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("loadprivkey")) {
               try {
                  RSAKeyPair old = cipher.getKeyPair();
                  cipher.load(args[++i]);
                  old.setPrivateKey(cipher.getKeyPair().getPrivateKey());
                  cipher.setKeyPair(old);
               } catch(Exception e) {
                  System.out.println("Error loading from file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("loadpubkey")) {
               try {
                  RSAKeyPair old = cipher.getKeyPair();
                  cipher.load(args[++i]);
                  old.setPublicKey(cipher.getKeyPair().getPublicKey());
                  cipher.setKeyPair(old);
               } catch(Exception e) {
                  System.out.println("Error loading from file " + args[i]);
                  e.printStackTrace(System.out);
               }
            } else if (args[i].equalsIgnoreCase("encode")) {
               try {
                  int secs = Integer.parseInt(args[++i]); i++;
                  ODCipherData d = cipher.encode(secs, args[i]);
                  System.out.println("Encode[" + args[i] + "] = [" + d.getExportString() + 
                                     "] Expires [" +
                                     (new Date(((long)d.getSecondsSince70())*1000)).toString() 
                                     + "]");
                  d = cipher.decode(d.getExportString());
                  if (!d.getString().equals(args[i])) {
                     throw new Exception("Strings don't equal on decode!");
                  }
                                  
               } catch(Throwable t) { t.printStackTrace();}
            } else if (args[i].equalsIgnoreCase("decode")) {
               try {
                  ODCipherData d = cipher.decode(args[++i]);
                  System.out.println("Decode[" + args[i] + "] = [" + d.getString() + 
                                     "] Expires [" +
                                     (new Date(((long)d.getSecondsSince70())*1000)).toString() 
                                     + "]");
               } catch(DecodeException de) {
                  System.out.println("Error decoding => " + de.getMessage());
               }
            } else {
               System.out.println("Options: JCE          <generates JCE based cipher>\n" +
                                  "         Simple       <generates OLD cipher>\n" +
                                  "         encode       [secs] [string]\n" +
                                  "         decode       [string]\n" +
                                  "         setkeylen    [length]\n" +
                                  "         loadkeys     [keypairfilename]\n" +
                                  "         storekeys    [keypairfilename]\n" +
                                  "         loadprivkey  [privkeyfilename]\n" +
                                  "         storeprivkey [privkeyfilename]\n" +
                                  "         loadpubkey   [pubkeyfilename]\n" +
                                  "         storepubkey  [pubkeyfilename]\n");
            }
         }
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }
   }
}
