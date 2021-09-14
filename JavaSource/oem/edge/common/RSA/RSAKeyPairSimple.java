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

public class RSAKeyPairSimple implements java.io.Serializable, RSAKeyPair {

   private RSAPrivateKey pri;
   private RSAPublicKey pub;

   public RSAKeyPairSimple(RSAPrivateKey pri, RSAPublicKey pub) {
      this.pri = pri;
      this.pub = pub;
   }

   public RSAKeyPairSimple() { }

   public RSAKeyPairSimple(int len) throws Exception {
      initializeKeyPair(len);
   }

   public RSAKeyPairSimple(String f) throws IOException {
      load(f);
   }
   
   public void initializeKeyPair(int len) throws Exception {
      BigInteger p = Prime.randomPrime(len/2, 5);
      BigInteger q = Prime.randomPrime(len/2, 5);
      while(p.equals(q))
         q = Prime.randomPrime(len/2, 5);
      BigInteger n = p.multiply(q);
      BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
      BigInteger e;
      for(e = BigInteger.valueOf(2l); (phi.gcd(e)).intValue() != 1; e = e.add(BigInteger.ONE));
      BigInteger d = (e).modInverse(phi);
      
      pri = new RSAPrivateKeySimple(d, n);
      pub = new RSAPublicKeySimple(e, n);
   }
   
   public void setPublicKey(RSAPublicKey p) {
      pub = p;
   }
   
   public void setPrivateKey(RSAPrivateKey p) {
      pri = p;
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
      
      RSAKeyPairSimple pair = null;
      try {
         Object obj = in.readObject();
         String n = obj.getClass().getName();
         if        (n.equals("oem.edge.common.RSA.RSAPublicKey")) {
            pair = new RSAKeyPairSimple(null, (RSAPublicKey)obj);
         } else if (n.equals("oem.edge.common.RSA.RSAPrivateKey")) {
            pair = new RSAKeyPairSimple((RSAPrivateKey)obj, null);
         } else {
            pair = (RSAKeyPairSimple)obj;
         }
         pub = (RSAPublicKeySimple)pair.getPublicKey();
         pri = (RSAPrivateKeySimple)pair.getPrivateKey();
      } catch(NullPointerException e) {
         throw new IOException("File did not contain expected flat key: " + e.toString());
      } catch(ClassCastException e) {
         throw new IOException("Class loaded NOT expected type: " + e.toString());
      } catch(ClassNotFoundException e) {
         throw new IOException("Class for keypair not found: " + e.toString());
      }
      in.close();
   }

   public void store(String file) throws IOException {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
      out.writeObject(this);
      out.close();
   }
}
