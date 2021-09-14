package oem.edge.common.RSA;
import java.math.BigInteger;
import java.io.*;

import java.security.*;
import javax.crypto.*;

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

public class RSAKeyPairJCE implements RSAKeyPair {

   private RSAPrivateKeyJCE pri;
   private RSAPublicKeyJCE pub;

   private Cipher pubcipher  = null;
   private Cipher privcipher = null;

   public RSAKeyPairJCE(RSAPrivateKey pri, RSAPublicKey pub) {
      this.pri = (RSAPrivateKeyJCE)pri;
      this.pub = (RSAPublicKeyJCE)pub;
   }

   public RSAKeyPairJCE() { }
   
   public RSAKeyPairJCE(int len) throws Exception {
      initializeKeyPair(len);
   }
   
   public RSAKeyPairJCE(String f) throws IOException {
      load(f);
   }

   
   public synchronized Cipher getEncoder() throws Exception {
      Cipher ret = null;
      if (pubcipher == null) {
         ret = Cipher.getInstance("RSA");
         ret.init(Cipher.ENCRYPT_MODE, pub.getPublicKey());
      } else {
         ret = pubcipher;
         pubcipher = null;
         ret.init(Cipher.ENCRYPT_MODE, pub.getPublicKey());
      }
      return ret;
   }

   
   public synchronized void returnEncoder(Cipher cp) throws Exception {
      if (pubcipher == null) pubcipher = cp;
   }
   
   
   public synchronized Cipher getDecoder() throws Exception {
      Cipher ret = null;
      if (privcipher == null) {
         ret = Cipher.getInstance("RSA");
         ret.init(Cipher.DECRYPT_MODE, pri.getPrivateKey());
      } else {
         ret = privcipher;
         privcipher = null;
         ret.init(Cipher.DECRYPT_MODE, pri.getPrivateKey());
      }
      return ret;
   }

   
   public synchronized void returnDecoder(Cipher cp) throws Exception {
      if (privcipher == null) privcipher = cp;
   }
   
   public void initializeKeyPair(int len) throws Exception {
      KeyPairGenerator rsaKeyPairGen;
      KeyPair rsaKeyPair;
      PublicKey rsaPub;
      PrivateKey rsaPriv;
      Cipher cp = null;
       
      try {
         rsaKeyPairGen = KeyPairGenerator.getInstance("RSA");
         rsaKeyPairGen.initialize(len);
         rsaKeyPair = rsaKeyPairGen.generateKeyPair();
         if (rsaKeyPair == null) {
            throw new Exception("Key generation failure. KeyPair was not generated.");
         }
        // get RSA public key.
         rsaPub = rsaKeyPair.getPublic();
          
        // get RSA private key.
         rsaPriv = rsaKeyPair.getPrivate();
          
         pri  = new RSAPrivateKeyJCE(rsaPriv);
         pub  = new RSAPublicKeyJCE(rsaPub);
      } catch (NoSuchAlgorithmException ex) {
         throw new Exception("Error generating keypair: Algo RSA not found");
      } catch(Exception e) {
         throw new Exception("Error generating keypair: Exception occured: " + 
                             e.toString());
      }
   }

   public void setPublicKey(RSAPublicKey p) {
      pub = (RSAPublicKeyJCE)p;
      pubcipher = null;
   }
    
   public void setPrivateKey(RSAPrivateKey p) {
      pri = (RSAPrivateKeyJCE)p;
   }
    
   public RSAPrivateKey getPrivateKey() {
      return this.pri;
   }

   public RSAPublicKey getPublicKey() {
      return this.pub;
   }

   public void load(String file) throws IOException {
      ObjectInputStream in = 
         new ObjectInputStream(new FileInputStream(file));
           
      RSAKeyPair pair = null;
      try {
         Object obj = in.readObject();
         RSAEncodedKeyPair encpair = (RSAEncodedKeyPair)obj;
         byte pubkeybytes[]  = encpair.getPublicKey();
         byte privkeybytes[] = encpair.getPrivateKey();
         if (pubkeybytes != null) {
            pub = new RSAPublicKeyJCE(pubkeybytes);
         }
         if (privkeybytes != null) {
            pri = new RSAPrivateKeyJCE(privkeybytes);
         }
           
      } catch(Exception e) {
         e.printStackTrace(System.out);
         throw new IOException("Error while loading key from store");
      }
      in.close();
   }

   
   public void store(String file) throws IOException {
      byte privenc[] = null;
      byte pubenc [] = null;
      
      if (pri != null) privenc = pri.getPrivateKey().getEncoded();
      if (pub != null) pubenc  = pub.getPublicKey().getEncoded();
      
      RSAEncodedKeyPair encpair = new RSAEncodedKeyPair(privenc, pubenc);
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
      out.writeObject(encpair);
      out.close();
   }
}
