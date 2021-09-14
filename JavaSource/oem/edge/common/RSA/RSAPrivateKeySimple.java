package oem.edge.common.RSA;
import java.math.BigInteger;
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

public class RSAPrivateKeySimple implements java.io.Serializable, RSAPrivateKey {

  // d: private exponent
  // n: modulus
   private BigInteger d, n;
   

   public RSAPrivateKeySimple(BigInteger d, BigInteger n) {
      this.d = d;
      this.n = n;
   }

   public RSAPrivateKeySimple() { }


   public BigInteger decipher(BigInteger c) {
      return c.modPow(d,n);
   }
    

   public BigInteger sign(BigInteger message) {
      if(message.bitLength() >= getKeyLength()) {
         System.err.println("WARNING: message length >= modulus length. Returning zero");
         return BigInteger.ZERO;
      }
      else
         return message.modPow(d,n);
   }


   public static RSAPrivateKey load(String file) throws IOException {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
      RSAPrivateKey key = null;
      try {
         key = (RSAPrivateKey)in.readObject();
      }
      catch(ClassNotFoundException e) {
         e.printStackTrace();
      }
      in.close();
      return key;
   }


   public void store(String file) throws IOException {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
      out.writeObject(this);
      out.close();
   }


   public int getKeyLength() {
      return n.bitLength();
   }


   public String toString() {
      return ("Modulus: " + n + "\nPrivate Exponent: " + d);
   }
}
