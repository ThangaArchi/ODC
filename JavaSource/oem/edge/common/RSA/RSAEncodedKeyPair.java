package oem.edge.common.RSA;

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

public class RSAEncodedKeyPair implements java.io.Serializable {

   protected byte privkey[];
   protected byte pubkey [];

   public RSAEncodedKeyPair(byte privkey[], byte pubkey[]) {
      this.privkey = privkey;
      this.pubkey  = pubkey;
   }
   
   public byte[] getPublicKey()  { return pubkey;  }
   public byte[] getPrivateKey() { return privkey; }
}
