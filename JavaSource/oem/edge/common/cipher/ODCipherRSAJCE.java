package oem.edge.common.cipher;

import java.util.*;
import java.math.BigInteger;
import oem.edge.common.RSA.*;
import java.io.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.*;

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

/*
** Module:      ODCipherRSAJCE
**
** Purpose:     Generates an ASCII string fit for inclusion in a URL which 
**              is time encoded, includes a CRC, from a byte array or String.
**
** 6/02/99 - Took from proframe base, and included in XMX
** 2/10/00 - Updated algo with rand and CRC (crc from ProFrame).
** 5/04/01 - convert to Java for use with ODC
** 5/24/01 - Rip out Hill Cipher and use RSA routines from Navneet to do 
**           encyption
** 3/27/06 - Switch to JCE RSA
*/

public class ODCipherRSAJCE implements ODCipherRSA {

   protected RSAKeyPairJCE pair = null;
      
  // I now create the secret on the fly everytime so we don't have
  //  matching data if the same cipher object is used multiple times
  //  on nearly the same data. RSA does a good job of "randomizing"
  //  the resultant data. The AES algo does NOT do a good job. Seems
  //  to be done on blocks of 16 bytes (perhaps). Anyway, even
  //  adding the expiration time to front of secret data, the tail
  //  was identical from run to run. No Good. Just bite the bullet 
  //  and generate a new secret key each time. Actually much more
  //  secure. 
  //
  //    "AES Secret key and AES cipher objects used to encode."
  //    " If I create them on the fly, it takes WAY too long"
  //
   SecretKey     secretkeyEncode       = null;
   byte          secretrawEncode[]     = null;
   Cipher     secretcipherEncode       = null;
   Cipher     secretcipherDecode       = null;
   KeyGenerator           keygen       = null;
   
   static final String SECRET_ALGO     = "AES";
   static final int    SECRET_KEYLEN   = 128;
   
   public ODCipherRSAJCE() {
      try {
         pair = new RSAKeyPairJCE(1024);
      } catch(Exception e) {}
   }
   
   public ODCipherRSAJCE(String f) throws IOException {
      pair = new RSAKeyPairJCE(f);
   }
   
   public ODCipherRSAJCE(int len) throws Exception {
      pair = new RSAKeyPairJCE(len);
   }
   
   public ODCipherRSAJCE(RSAKeyPair p) {
      if (p != null) {
         pair = (RSAKeyPairJCE)new RSAKeyPairJCE(p.getPrivateKey(), p.getPublicKey());
      } else {
         try {
            pair = new RSAKeyPairJCE(1024);
         } catch(Exception e) {}
      }
   }
   
  // This is to keep the user from ever touching a keypair if they don't want to
  //  Which could be a good idea, cause the Cipher instance has to go with a keypair
   public void load(String f) throws IOException {
      pair = new RSAKeyPairJCE(f);
   }
   
   
   public void store(String f) throws IOException {
      pair.store(f);
   }
   
   public RSAKeyPair getKeyPair() {
      return new RSAKeyPairJCE(pair.getPrivateKey(), pair.getPublicKey());
   }
   
   public void setKeyPair(RSAKeyPair kp) {
      pair = (RSAKeyPairJCE)kp;
   }
   
   public void initializeKeyPair(int len) throws CipherException {
      try {
         pair = new RSAKeyPairJCE(len);
      } catch(Exception e) {
         throw new CipherException("Error initializing keypair: " + e.toString());
      }
   }
   
   public ODCipherData encode(int advanceSecs, 
                              String toencode) throws CipherException {
      byte bytes[] = toencode.getBytes();
      return encode(advanceSecs, bytes, 0, bytes.length);
   }
   public ODCipherData encode(int advanceSecs, 
                              byte toencode[]) throws CipherException {
      return encode(advanceSecs, toencode, 0, toencode.length);
   }
   
   public ODCipherData encode(int advanceSecs, byte toencode[], 
                              int ofs, int len) throws CipherException {
      
      int tlen;
      int i;
      
      long millis = System.currentTimeMillis();
      long secs   = advanceSecs + (millis/1000);
      
     // if the data + my other overhead is > 120 (even 5 less than 
     // that to be save), then have to use secret as well
      boolean dosecret = len > (120-6-5);
         
      byte timearr[] = new byte[6];
      
     /*
       fmt: Bytes as the crow flies
       
       - First 2 bytes are IN THE CLEAR little endian XORed with 0xFF
       - Next 5 bytes are secs since 70 GMT that token expires (Big endian)
       - Next 1 byte is 0 if using secret key, otherwise its the real data
       - Next n bytes is the secret key   OR  the real data
       - Next m bytes is data encoded with secret key (if using secrets)
       
       0       : pd1   UNENCODED
       1       : pd0   UNENCODED
       2       : 0|1   if using secret, otherwise data in RSA
       3       : t0    
       4       : t1    
       5       : t2
       6       : t3
       7       : t4
       8       : skd0  secret key OR real data
       8+n     : skdn
       8+n+1+m : ECm   Only used if secret key 
     */
      
      ByteArrayOutputStream ans = new ByteArrayOutputStream();
      
      Cipher cp = null;
      try {
      
      
        // Get public get and init cp cipher to use it to encode
         cp = pair.getEncoder();
         
        // Place holder for in the clear size
         ans.write(timearr, 0, 2);
         
         timearr[0] = (byte)(dosecret?0:1);
         
         timearr[1] = (byte)(secs >> 32);
         timearr[2] = (byte)(secs >> 24);
         timearr[3] = (byte)(secs >> 16);
         timearr[4] = (byte)(secs >>  8);
         timearr[5] = (byte)secs;
         
        // Write timearr info and secretkey 
         ans.write(cp.update(timearr));
                 
         int sz = 0;
                  
         if (dosecret) {
            synchronized (this) {
               if (secretcipherEncode == null) {
                  keygen = KeyGenerator.getInstance(SECRET_ALGO);
                  keygen.init(SECRET_KEYLEN);
                  secretcipherEncode  = Cipher.getInstance(SECRET_ALGO);
               }
               
               secretkeyEncode     = keygen.generateKey();
               secretrawEncode     = secretkeyEncode.getEncoded();
         
               ans.write(cp.doFinal(secretrawEncode));
               
               sz = ans.size()-2;
               
              // Now write real data encoded with secret key
               secretcipherEncode.init(Cipher.ENCRYPT_MODE, secretkeyEncode);
               ans.write(secretcipherEncode.doFinal(toencode, ofs, len));
            }
         } else {
            ans.write(cp.doFinal(toencode, ofs, len));
            sz = ans.size()-2;
         }
         
         ans.close();
         
         byte arr[] = ans.toByteArray();
         
         arr[0] = (byte)((sz ^ 0xFF)  & 0xFF);
         arr[1] = (byte)(((sz >> 8) ^ 0xFF) & 0xFF);
         return new ODCipherData(arr, 0, arr.length, (int)secs);
         
      } catch (NoSuchAlgorithmException ex) {
         throw new CipherException("RSA encryption failed: RSA cipher not found!");
      } catch (NoSuchPaddingException ex) {
         throw new CipherException("RSA encryption failed: Invalid padding.");
      } catch(CipherException ex) {
         throw ex;
      } catch(Exception ex) {
         throw new CipherException("RSA encryption failed: Exception: " + ex.toString());
      } finally {
         try { pair.returnEncoder(cp); } catch(Exception eee) {}
      }
   }
   
   public ODCipherData decode(String todecode) throws DecodeException {
      try {
        //System.out.println("About to decode import string [" + todecode + "]");
         BigInteger bi = new BigInteger(todecode, 16);
         return decode(bi.toByteArray());
      } catch(NumberFormatException ne) {
         throw new DecodeException("Invalid String to decode");
      }
   }
   public ODCipherData decode(byte inarr[]) throws DecodeException {
      return decode(inarr, 0, inarr.length);
   }
   
   public ODCipherData decode(byte inarr[], int ofs, int len) throws DecodeException {
      try {
         long secs;
         
         if (inarr.length < ofs+len) {
            throw new DecodeException("Provided array for decode smaller than ofs/len");
         }
         
         if (len < 2) {
            throw new DecodeException("Not enough bytes for decode");
         }
         
         PrivateKey rsaPriv = null;
         Cipher cp = Cipher.getInstance("RSA");
         rsaPriv = ((RSAPrivateKeyJCE)(pair.getPrivateKey())).getPrivateKey();
         cp.init(Cipher.DECRYPT_MODE, rsaPriv);
         
        // Load len of RSA encoded data, little endian
         int rsalen = ((((int)(inarr[ofs+1]) << 8) ^ 0xFF00) & 0xFF00) | 
                      ((((int)inarr[ofs]) ^ 0xFF) & 0xFF);
         
        // Load in RSA encoded data
         byte pwid[] = cp.doFinal(inarr, ofs+2, rsalen);
         
         if (len < rsalen+2) {
            throw new DecodeException("Not enough bytes for decode");
         }
         
         if (pwid.length < 6) {
            throw new DecodeException("Invalid Encoded Content");
         }
         
         boolean dosecret = pwid[0] == 0;
         
        // Read in the secs since 70 for expiration
         secs = ((pwid[1] << 32) & 0xff00000000L) | 
                ((pwid[2] << 24) & 0x00ff000000L) | 
                ((pwid[3] << 16) & 0x0000ff0000L) |  
                ((pwid[4] <<  8) & 0x000000ff00L) |
                ((pwid[5]      ) & 0x00000000ffL);
     
         ODCipherData cdata = new ODCipherData();
         if (dosecret) {
           // Create keyspec from raw data
            SecretKeySpec keySpec = new SecretKeySpec(pwid, 6, pwid.length-6, SECRET_ALGO);
            synchronized(this) {
               if (secretcipherDecode == null) {
                  secretcipherDecode = Cipher.getInstance(SECRET_ALGO);
               }
               secretcipherDecode.init(Cipher.DECRYPT_MODE, keySpec);
               pwid = secretcipherDecode.doFinal(inarr, ofs+rsalen+2, 
                                                 (len - rsalen) - 2);
            }
            
           // Until we change the API to be long for secs, cast to int
            cdata.setValues(pwid, 0, pwid.length, (int)secs);
         } else {
            cdata.setValues(pwid, 6, pwid.length-6, (int)secs);
         }
         
         return cdata;
         
      } catch (NoSuchAlgorithmException ex) {
         throw new DecodeException("RSA decryption failed: RSA cipher not found!");
      } catch (NoSuchPaddingException ex) {
         throw new DecodeException("RSA decryption failed: Invalid padding.");
      } catch (DecodeException ex) {
         throw ex;
      } catch (Exception ex) {
         
         DecodeException ee = new DecodeException("Unknown error decoding array: "
                                                  + ex.toString());
         ee.initCause(ex);
         throw ee;
                                         
      }
   }   
}
