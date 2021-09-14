package oem.edge.common.cipher;

import oem.edge.common.RSA.*;
import java.util.*;

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

//
// The purpose of this testcase is to get a feeling for the overhead when 
//  encoding and decoding data using the ODCipherRSA class.  
//
// The new way and the old way show some data points. 
//
//   NumBytesIn  NumBytesOut  msebcode msdecode
//
// New way  using JCE
//
//   bytes   60 genbytes =  130 time 3 edtime 52
//   bytes  130 genbytes =  274 time 6 edtime 54
//   bytes  300 genbytes =  434 time 3 edtime 52
//   bytes 1000 genbytes = 1138 time 4 edtime 52
//
// Old way  using our own RSA based on BigInteger math
//
//   bytes   60 genbytes =  133 time 1 edtime  182
//   bytes  130 genbytes =  198 time 2 edtime  365
//   bytes  300 genbytes =  396 time 2 edtime  549
//   bytes 1000 genbytes = 1053 time 6 edtime 1508
//
//  Remember, since we use this to encodecreate a token, which is a string,
//   the encoded sizes are effectively doubled. So, the new way is a 
//   bit worse for the wear size wise.
//
//  The net result is:  encoding is about 2 times slower with the new way,
//   but decoding remains flat at around 50ms for the new way, while it
//   starts at 180ms at its best, and degrades quickly as the sizes grow
//
public class testcipher {
   
   public static void main(String args[]) {
   
      try {
         testcipher o = new testcipher();
         
         System.out.println("------ JCE ------\n");
         ODCipherRSA cipher = new ODCipherRSAJCE(1024);
         o.runtest(cipher);
         System.out.println("\n------ Simple ------\n");
         cipher = new ODCipherRSASimple(1024);
         o.runtest(cipher);
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }
   }
   
   public  void runtest(ODCipherRSA cipher) throws Exception {
      
      byte arr[] = new byte[2048];
      Random rand = new Random();
      rand.nextBytes(arr);
      
     // Do initial encode to get all RSA code loaded for a fair fight
      cipher.encode(100, arr, 0, 10);
         
      long stime = System.currentTimeMillis();
      int totgenbytes = 0;
      int totbytes = 0;
      StringBuffer sb = new StringBuffer();
      for(int i=10; i < 1024; i+=10) {
         long lstime = System.currentTimeMillis();
         ODCipherData cd = cipher.encode(100, arr, 0, i);
         totgenbytes += cd.getBytes().length;
         totbytes += i;
         
         long letime = System.currentTimeMillis();
         cipher.decode(cd.getBytes());
         long ledtime = System.currentTimeMillis();
         sb.append("\n  bytes " + i + " genbytes = " + cd.getBytes().length + 
                   " time " + (letime-lstime) +
                   " etime " + (letime-stime) +
                   " edtime " + (ledtime-letime));
                   
      }
      long etime = System.currentTimeMillis();
      
      System.out.println(sb.toString());
      
      System.out.println("Total gen time  was " + (etime - stime) + "ms");
      System.out.println("Total gen bytes was " + totbytes);
      System.out.println("Bloat factor    was " + (((float)totgenbytes)/totbytes));
      
      
      stime = System.currentTimeMillis();
      totgenbytes = 0;
      totbytes = 0;
      sb = new StringBuffer();
      for(int i=1; i < 100; i++) {
         long lstime = System.currentTimeMillis();
         ODCipherData cd = cipher.encode(100, arr, 0, 120);
         totgenbytes += cd.getBytes().length;
         totbytes += 120;
         
         long letime = System.currentTimeMillis();
         
         cipher.decode(cd.getBytes());
         long ledtime = System.currentTimeMillis();
         sb.append("\n  bytes " + i + " genbytes = " + cd.getBytes().length + 
                   " time " + (letime-lstime) +
                   " etime " + (letime-stime) +
                   " edtime " + (ledtime-letime));
      }
      etime = System.currentTimeMillis();
      
      System.out.println(sb.toString());
      
      System.out.println("Total gen time  was " + (etime - stime) + "ms");
      System.out.println("Total gen bytes was " + totbytes);
      System.out.println("Bloat factor    was " + (((float)totgenbytes)/totbytes));
      
   }
}
