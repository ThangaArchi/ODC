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

public class RSAPrivateKeyJCE implements RSAPrivateKey {
   
   PrivateKey privkey;

   public RSAPrivateKeyJCE(byte enc[]) throws IOException {
      setEncodedKey(enc);
   }
   public RSAPrivateKeyJCE(PrivateKey p) {
      privkey = p;
   }
   
   public RSAPrivateKeyJCE() {}
   
   public PrivateKey getPrivateKey() { return privkey; }
   
   public void setEncodedKey(byte enc[]) throws IOException {
      try {
         PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(enc);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         privkey =  keyFactory.generatePrivate(keySpec);
      } catch(Exception e) {
         e.printStackTrace(System.out);
         throw new IOException(e.getClass().getName() + ": " + e.toString());
      }
   }
}
