package oem.edge.common.cipher;

import java.util.*;
import java.math.BigInteger;
import oem.edge.common.RSA.*;
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

/*
** Module:      ODCipherRSA
**
** Purpose:     Generates an ASCII string fit for inclusion in a URL which 
**              is time encoded, includes a CRC, from a byte array or String.
**
** 6/02/99 - Took from proframe base, and included in XMX
** 2/10/00 - Updated algo with rand and CRC (crc from ProFrame).
** 5/04/01 - convert to Java for use with ODC
** 5/24/01 - Rip out Hill Cipher and use RSA routines from Navneet to do 
**           encyption
**
** Note: None of the BigInteger's enciphered may be Neg ...just doesn't 
**       work ... so keep those numbers positive
*/

public class ODCipherRSASimple implements ODCipherRSA {

   protected ODCrc crcTable = new ODCrc();
   protected RSAKeyPairSimple pair = null;
   protected Random rand = new Random();
   
   public ODCipherRSASimple() throws Exception {
      crcTable.setDefaultTable();
      pair = new RSAKeyPairSimple();
      pair.initializeKeyPair(128);
   }
   
   public ODCipherRSASimple(RSAKeyPairSimple p) {
      crcTable.setDefaultTable();
      pair = p;
   }
   
   public ODCipherRSASimple(String f) throws IOException {
      crcTable.setDefaultTable();
      pair = new RSAKeyPairSimple(f);
   }
   
   public ODCipherRSASimple(int len) throws Exception {
      crcTable.setDefaultTable();
      pair = new RSAKeyPairSimple(len);
   }
   
   public void load(String f) throws IOException {
      pair = new RSAKeyPairSimple(f);
   }
   
   public void store(String f) throws IOException {
      pair.store(f);
   }
   
   public void initializeKeyPair(int len) throws CipherException {
      try {
         pair = new RSAKeyPairSimple(len);
      } catch(Exception e) {
         throw new CipherException("Error initializing keypair: " + e.toString());
      }
   }
   
   public void setKeyPair(RSAKeyPair kp) {
      pair = (RSAKeyPairSimple)kp;
   }
   
   public RSAKeyPair getKeyPair() {
      return pair;
   }
   
   public ODCipherData encode(int advanceSecs, String toencode) throws CipherException {
      byte bytes[] = toencode.getBytes();
      return encode(advanceSecs, bytes, 0, bytes.length);
   }
   
   public ODCipherData encode(int advanceSecs, byte toencode[]) throws CipherException {
      return encode(advanceSecs, toencode, 0, toencode.length);
   }
   
   public ODCipherData encode(int advanceSecs, byte toencode[], 
                              int ofs, int len) throws CipherException {
      
      int tlen;
      int crc;
      byte t0, t1, t2, t3;
      int i;
      
      long millis = System.currentTimeMillis();
      int secs = advanceSecs + (int)(millis/1000);
      
      t0 = (byte)(secs >> 24);
      t1 = (byte)(secs >> 16);
      t2 = (byte)(secs >> 8);
      t3 = (byte)(secs);
      
     /*
       fmt: Bytes as the crow flies
       
       Seed for Random Values is the CRC of toencode string. Rand number is 
       then 'applied' to each nonCRC byte in the mess. Then, the RSA algo
       is applied. The rand ensures the entire sting changes when just one 
       piece of info changes.  
       
       Actually, RSA doesn't need RAND to do a good job of confusing the
       data, so its left out (ie rand == 0).
       
       0    : CRC4 
       1    : CRC3
       2    : CRC2
       3    : CRC1
       4    : t0  ^ rand
       5    : t1  ^ rand
       6    : t2  ^ rand
       7    : t3  ^ rand
       8    : EC0 ^ rand
       8+n  : ECn ^ rand
       
       After encryption, the following is true:
       
       
       2 bytes of rand-yuk ... throw away
       
       packet-len:
       
          01-7f (1 byte  of len)
          80-ff (3 bytes of len)       
       
       packet-data: packet-len bytes long
       
       Full message can be made up of many packets
       
     */
      
      tlen = len + 8;
      
      byte pwid[] = new byte[tlen];
      pwid[4] = t0;
      pwid[5] = t1;
      pwid[6] = t2;
      pwid[7] = t3;
      System.arraycopy(toencode, ofs, pwid, 8, len);
      
      crcTable.resetCRC();
      crcTable.generateCRC(pwid, 4, tlen-4);
      crc = crcTable.getCRC();
     // System.out.println("CRC = " + crc);
      
      pwid[0] = (byte)(crc >> 24);
      pwid[1] = (byte)(crc >> 16);
      pwid[2] = (byte)(crc >> 8);
      pwid[3] = (byte)(crc);
      
     //BigInteger gg = new BigInteger(pwid);
     //System.out.println("Debug: " + gg.toString(16));
      
     // RSA doesn't need a good randomizing
     //rand.setSeed(crc);
     // for(i=4; i < tlen; i++) {
     //    int r = rand.nextInt();
     //    pwid[i] ^= (byte)(r);
     // }
      
     // Get max bytes per encrypt chunk
      int bytesz = (((RSAPublicKeySimple)(pair.getPublicKey())).getKeyLength()/8)-1;
      if (bytesz <= 1) {
         throw new NullPointerException("Bitsize of key too Small!");
      }
      byte arr[] = new byte[bytesz];
      ByteArrayOutputStream ans = new ByteArrayOutputStream();
      
     // Make sure the data starts with a non-0 positive value
      byte arr3[] = new byte[3];
      rand.nextBytes(arr3);
      arr3[0] |= 1;
      arr3[0] &= 0x7f;
      ans.write(arr3, 0, 2);
      
      for(i=0; i < tlen; ) {
         if (i+bytesz-1 > tlen) {
            bytesz = tlen-i+1;
            arr = new byte[bytesz];
         }
         
        // Ensure each byte enciphered is POSITIVE!
         arr[0] = 0x29;
         System.arraycopy(pwid, i, arr, 1, bytesz-1);
                  
         BigInteger bi = new BigInteger(arr);
         
        //System.out.println("Debug: Before " + bi.toString(16));
         
         bi = ((RSAPublicKeySimple)(pair.getPublicKey())).encipher(bi);
         
        //System.out.println("Debug: After " + bi.toString(16));
         
         try {
            byte biarr[] = bi.toByteArray();
            int l = biarr.length;
           //System.out.println("bilen = " + l);
            if (l > 127) {
               arr3[0] = (byte)(0x80 | (l >> 16));
               arr3[1] = (byte)(l >> 8);
               arr3[2] = (byte)l;
               ans.write(arr3);
            } else {
               ans.write((byte)l);
            }
            ans.write(biarr);
         } catch (IOException e) {
            throw new CipherException("Error with .write!?!");
         }
         
        // One of the bytes is the Positive insurance byte
         i+= bytesz-1;
      }
      
      arr = ans.toByteArray();
      
      return new ODCipherData(arr, 0, arr.length, secs);
   }
   
   public ODCipherData decode(String todecode) throws DecodeException {
      try {
         BigInteger bi = new BigInteger(todecode, 16);
         return decode(bi.toByteArray());
      } catch(NumberFormatException ne) {
         throw new DecodeException("Invalid String");
      }
   }
   
   public ODCipherData decode(byte inarr[]) throws DecodeException {
      return decode(inarr, 0, inarr.length);
   }
   public ODCipherData decode(byte inarr[], int ofs, int len) throws DecodeException {
   
      int crc, checkCrc, secs, i;
      byte arr[];
      
     /*
      // Get max bytes per decrypt chunk
      int bytesz = (priv.getKeyLength()/8)-1;
      if (bytesz <= 0) {
      throw new DecodeException("Bitsize of key too Small!");
      }
      byte arr[] = new byte[bytesz];
     */
      
      ByteArrayOutputStream ans = new ByteArrayOutputStream();
      
      for(i=2; i < len; ) {
         
         int l = (0xff & inarr[i+ofs]);
         if ((l & 0x80) != 0) {
            if (i+2 >= len) {
               throw new DecodeException("Bad length specifier");
            }
            
            l = (((inarr[i+ofs]) & 0x7f) << 16) | ((inarr[i+1+ofs] & 0xff) << 8) 
                                                | ((inarr[i+2+ofs] & 0xff));
            i+=3;
         } else {
            i++;
         }
         
        //System.out.println("Encoded Len = " + l + "amt left = " + tlen-i));
        
         if (l == 0) {
            throw new DecodeException("0 encoded length");
         }
         if (i+l > len) {
            throw new DecodeException("Bad encoded length");
         }
      
         arr = new byte[l];
         System.arraycopy(inarr, i+ofs, arr, 0, l);
         
         BigInteger bi = new BigInteger(arr);
        //System.out.println("Debug: Before " + bi.toString(16));
         
         bi = ((RSAPrivateKeySimple)(pair.getPrivateKey())).decipher(bi);
         
        //System.out.println("Debug: After " + bi.toString(16));
         
         arr = bi.toByteArray();
         ans.write(arr, 1, arr.length-1);
         i += l;
      }
      
      byte pwid[] = ans.toByteArray();
      if (pwid.length < 8) {
         throw new DecodeException("Invalid Encoded Content");
      }
      
     //BigInteger gg = new BigInteger(pwid);
     //System.out.println("Debug: " + gg.toString(16));
      
      crc = ((pwid[0] << 24) & 0xff000000) | 
            ((pwid[1] << 16) & 0x00ff0000) |  
            ((pwid[2] <<  8) & 0x0000ff00) |
            ((pwid[3]      ) & 0x000000ff);
              
      int nlen = pwid.length;
      
     // RSA doesn't need a good randomizing
     //rand.setSeed(crc);
     //for(i=4; i < nlen; i++) {
     //    pwid[i] ^= (byte)rand.nextInt();
     // }
      crcTable.resetCRC();
      crcTable.generateCRC(pwid, 4, nlen-4);
      checkCrc=crcTable.getCRC();
      if (checkCrc != crc) {
        // System.out.println("CRC chk failed! " + crc + " " + checkCrc);
         throw new DecodeException("CRC Failure!");
      }
      
      secs = ((pwid[4] << 24) & 0xff000000) | 
             ((pwid[5] << 16) & 0x00ff0000) |  
             ((pwid[6] <<  8) & 0x0000ff00) |
             ((pwid[7]      ) & 0x000000ff);
     
      ODCipherData cdata = new ODCipherData();
      cdata.setValues(pwid, 8, nlen-8, secs);
      
      return cdata;
   }
}
