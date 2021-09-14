package oem.edge.common.RSA;

import java.security.*;
import java.security.spec.*;
import java.io.IOException;

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

public class RSAPublicKeyJCE implements RSAPublicKey {
   
   PublicKey pubkey;

   public RSAPublicKeyJCE(byte enc[]) throws IOException {
      setEncodedKey(enc);
   }
   
   public RSAPublicKeyJCE(PublicKey p) {
      pubkey = p;
   }
   
   public RSAPublicKeyJCE() {}
   
   public PublicKey getPublicKey() { return pubkey; }
   
   public void setEncodedKey(byte enc[]) throws IOException {
      try {
         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(enc);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         pubkey =  keyFactory.generatePublic(keySpec);
      } catch(Exception e) {
         throw new IOException(e.getClass().getName() + ": " + e.getMessage());
      }
   }
}
