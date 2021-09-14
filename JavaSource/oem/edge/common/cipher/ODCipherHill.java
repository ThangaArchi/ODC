package oem.edge.common.cipher;

import java.util.*;
import java.io.FileInputStream;

/*
** Module:      ODCipher
**
** Purpose:     Generates an ASCII string fit for inclusion in a URL which is time 
**              encoded, includes a CRC, from a byte array or String.
**
** Note: Brion Kellor (IBM endicott) is the author of the pw generation algo
**       (to my knowledge). 
** 6/02/99 - Took from proframe base, and included in XMX
** 2/10/00 - Updated algo with rand and CRC (crc from ProFrame).
** 5/04/01 - convert to Java for use with ODC
*/

/*
** Hill cypher routine mod 256
**
**        +-        -+
**        |  7    4  |
** A   =  |  4    5  |
**        +-        -+
**
**        +-        -+
**  -1    |  5   -4  |   /
** A   =  | -4    7  |  /  19
**        +-        -+
**
**    det(A) = da = 19
**    inverse da mod 256 = 27
**
**          +-        -+
**  -1      |  5   -4  |
** A   = 27 | -4    7  |  mod 256
**          +-        -+
**
**            +-        -+                     +-        -+
**  -1        | 135  -108|                     | 135  148 |
** A   =      |-108   189|   (mod 256) ==>     | 148  189 |
**            +-        -+                     +-        -+
**
**  So, to encypher (x,y) we apply  x' = ( 7x  +  4y) mod 256
**                                  y' = ( 4x  +  5y) mod 256
**
**      to decypher (x,y) we apply  x' = (135x + 148y) mod 256
**                                  y' = (148x + 189y) mod 256
**
*/

public class ODCipherHill implements ODCipher {
   ODCrc crcTable = new ODCrc();
   Random rand = new Random();
   public ODCipherHill() {
      crcTable.setDefaultTable();
   }
   
   static public void main(String args[]) {
      ODCipherHill od = new ODCipherHill();
      od.domain(args);
   }
   public void domain(String args[]) {
      System.out.println("Starting " + (new Date()).toString());
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("rand")) {
            try {
               int n=Integer.parseInt(args[++i]);
               System.out.println("RandNumbers Seed = " + n);
               rand.setSeed(n);
               for(int j=0; j < 8; j++) {
                  System.out.print("[" + rand.nextInt() + "] ");
               }
               System.out.println("");
            } catch (Throwable t) {}
         } else if (args[i].equalsIgnoreCase("crc")) {
            crcTable.resetCRC();
            crcTable.generateCRC(args[++i]);
            int lcrc = crcTable.getCRC();
            System.out.println("CRC for [" + args[i] + "] = " + lcrc);
         } else if (args[i].equalsIgnoreCase("filecrc")) {
            try {
               FileInputStream f = new FileInputStream(args[++i]);
               int r;
               byte buf[] = new byte[4096];
               crcTable.resetCRC();
               while ((r=f.read(buf)) > 0) {
                  crcTable.generateCRC(buf, 0, r);
               }
               int lcrc = crcTable.getCRC();
               System.out.println("CRC for file [" + args[i] + "] = " + lcrc);
            } catch(Throwable t) {
               System.out.println("Error generating CRC for file " 
                                  + args[i] + " was " + t.getMessage());
            }
         } else if (args[i].equalsIgnoreCase("encode")) {
            try {
               int secs = Integer.parseInt(args[++i]); i++;
               ODCipherData d = encode(secs, args[i]);
               System.out.println("Encode[" + args[i] + "] = [" + d.getExportString() + 
                                  "] Expires [" +
                                  (new Date(((long)d.getSecondsSince70())*1000)).toString() 
                                  + "]");
            } catch(Throwable t) { t.printStackTrace();}
         } else if (args[i].equalsIgnoreCase("decode")) {
            try {
               ODCipherData d = decode(args[++i]);
               System.out.println("Decode[" + args[i] + "] = [" + d.getString() + 
                                  "] Expires [" +
                                  (new Date(((long)d.getSecondsSince70())*1000)).toString() 
                                  + "]");
            } catch(DecodeException de) {
               System.out.println("Error decoding => " + de.getMessage());
            }
         } else {
            System.out.println("Options: rand [seed] | crc [string] | encode secs [string] | decode [string]");
         }
      }
   }
   

/*
** applyHill:
**
**    Apply hill matrix to incoming bytes. Cipher applied in place. Assume the 
**    number of bytes is even.
**
**    To be correct, we should check sizes ... TODO
*/
   void applyHill(byte inOut[], int len) { 
      for(int i=0; i < len;) {
         int y, x = inOut[i];
         if (x < 0) x += 256;
         y = inOut[i+1];
         if (y < 0) y += 256;
         inOut[i++] = (byte)((7*x + 4*y) & 0xff);
         inOut[i++] = (byte)((4*x + 5*y) & 0xff);
      }
   }

/*
** applyInverseHill:
**
**    We always assume the encoded strlen is even
*/
   void applyInverseHill(byte inOut[], int len) {
      for(int i=0; i < len;) {
         int x = inOut[i];   if (x < 0) x += 256;
         int y = inOut[i+1]; if (y < 0) y += 256;
         inOut[i++] = (byte)((135*x  + 148*y) & 0xff);
         inOut[i++] = (byte)((148*x +  189*y)  & 0xff);
      }
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
      
      int l;
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
       then 'applied' to each nonCRC byte in the mess. Then, the hill cipher 
       is applied.
       
       0    : CRC4 
       1    : CRC3
       2    : CRC2
       3    : CRC1
       4    : t0  ^ rand
       5    : t1  ^ rand
       6    : t2  ^ rand
       7    : t3  ^ rand
       8    : ifOdd ^ rand
       9    : EC1 ^ rand
       10+n : ECn ^ rand
     */
      
      tlen = len + 9;
      
     /* To keep encoded string even (as Hill likes it) just incr to include
        '\0' or not as needed 
     */
      byte ifOdd = (byte)0;
      if ((tlen & 1) != 0) { ifOdd++; tlen++; }
      
      byte pwid[] = new byte[tlen];
      pwid[tlen-1] = 0;
      pwid[4] = t0;
      pwid[5] = t1;
      pwid[6] = t2;
      pwid[7] = t3;
      pwid[8] = ifOdd;
      System.arraycopy(toencode, ofs, pwid, 9, len);
      
      crcTable.resetCRC();
      crcTable.generateCRC(pwid, 4, tlen-4);
      crc = crcTable.getCRC();
     // System.out.println("CRC = " + crc);
      
      pwid[0] = (byte)(crc >> 24);
      pwid[1] = (byte)(crc >> 16);
      pwid[2] = (byte)(crc >> 8);
      pwid[3] = (byte)(crc);
      
      rand.setSeed(crc);
      for(i=4; i < tlen; i++) {
         int r = rand.nextInt();
         pwid[i] ^= (byte)(r);
      }
      
      applyHill(pwid, tlen);
      
     /*
      StringBuffer sb = new StringBuffer(tlen*2);
      for(i=0; i < tlen; i++) {
         sb.append(Character.forDigit(((pwid[i] >> 4) & 0xf), 16));
         sb.append(Character.forDigit(((pwid[i])      & 0xf), 16));
      }
     */
      return new ODCipherData(pwid, 0, tlen, secs);
   }
   
   public ODCipherData decode(String todecode) throws DecodeException {
      
      int l = todecode.length();
      if ((l & 1) != 0 || l == 0) {
         throw new DecodeException("Invalid String");
      }
      byte pwid[] = new byte[l/2];
      for(int i=0; i < l; i+=2) {
         int n = ((Character.digit(todecode.charAt(i),  16)) << 4) | 
                 (Character.digit(todecode.charAt(i+1), 16));
         pwid[i/2] = (byte)n;
      }
      return decode(pwid);
   }   
   
   public ODCipherData decode(byte pwid[]) throws DecodeException {
      return decode(pwid, 0, pwid.length);
   }
   
   public ODCipherData decode(byte pwid[], int ofs, int len) throws DecodeException {
      int crc, checkCrc, secs, i;
      int nlen = len;
      
      if (nlen < 9 || (nlen&1) != 0) {
         throw new DecodeException("Too Short, or not Even!");
      }
      
      byte npwid[] = new byte[len];
      System.arraycopy(pwid, ofs, npwid, 0, len);
      pwid = npwid;
      
      applyInverseHill(pwid, nlen);
     
      crc = ((pwid[0] << 24) & 0xff000000) | 
            ((pwid[1] << 16) & 0x00ff0000) |  
            ((pwid[2] <<  8) & 0x0000ff00) |
            ((pwid[3]      ) & 0x000000ff);
              
      rand.setSeed(crc);
      for(i=4; i < nlen; i++) {
         pwid[i] ^= (byte)rand.nextInt();
      }
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
     
      int tl = nlen-9;
      if (pwid[8] != 0) tl--;
      
      ODCipherData cdata = new ODCipherData();
      cdata.setValues(pwid, 9, tl, secs);
      
      return cdata;
   }
}
