package oem.edge.ed.odc.dropbox.server;

import java.io.*;
import java.util.*;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/**
 * @author v2murali
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DropboxFileMD5  implements MessageDigestI , Serializable {

   DropboxMD5State	state;
  
   static byte	padding[] = {
      (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
   };

   private static final char[] HEX_CHARS = {'0', '1', '2', '3','4', '5', '6', '7',
                                            '8', '9', 'a', 'b','c', 'd', 'e', 'f',};

  
  /**
   * Initialize DropboxMD5 internal state.object can be reused just by
   * calling Init after every Final 
   */
   public synchronized void Init () {
      state = new DropboxMD5State();
   }


  /**
   * Class constructor
   */
   public DropboxFileMD5 () {
      this.Init();
   }
  
   public DropboxFileMD5 (DropboxMD5State s) {
      this.state = new DropboxMD5State(s);
   }
  
   public void setMD5State(DropboxMD5State state){
      this.state = new DropboxMD5State(state);
   }
   public DropboxMD5State getMD5State(){
      return this.state;
   }
  
   public boolean supportsStoreAndLoad(){
      return true;
   }
  
   public byte[] digest() {	
      return Final();
   }
   public byte[] digest(byte[] input){
      update(input);
      return Final();
   }
   public int digest(byte[] output, int ofs, int len) throws Exception {
   
      byte arr[] =  Final();
      if (arr.length > len) throw new Exception("Output buffer too small. Need " + 
                                                arr.length + " bytes");
      System.arraycopy(arr, 0, output, ofs, len);
      
      return len;
   }
   
   public String getAlgorithm(){
      return "MD5";
   }
   public void reset(){
      Init();
   }

   public Object clone() throws CloneNotSupportedException{
      DropboxFileMD5 o = (DropboxFileMD5)super.clone();
      o.state = new DropboxMD5State(state);
      return o;
   }
  
  // Note, our home grown md5 is about 30% faster than the java 1.4 version
   public static void main(String[] args){
      try{
      
         MessageDigestI out = new DropboxFileMD5();
  		  	
         Hashtable params = getArgsHashWithDelim(args,"-");  	
  		
         String impl = (String)params.get("impl");
         String filename = (String)params.get("file");
         String fileabort =(String)params.get("fileabort");
         String fileresume = (String)params.get("fileresume");
         String maxsize = (String)params.get("internalcheck");
         
         if (impl != null && impl.equalsIgnoreCase("api")) {
            
            out = (MessageDigestI)
               Class.forName("oem.edge.ed.odc.dropbox.server.ApiMessageDigest").newInstance();
         }
		
         long offset = (fileresume != null) ? Long.parseLong(fileresume) : 0 ; 
		
         if(filename != null){
            System.out.println("File name = "+filename+"\n\n");
            File file = new File(filename);
            
            long len = file.length();
            long max_data = (fileabort != null) ? Long.parseLong(fileabort) : len ;
            
            if (max_data > len) throw new Exception("-fileabort > file length!");
            
            long st = System.currentTimeMillis();
            
            RandomAccessFile rf = new RandomAccessFile(file, "r");
            rf.seek(offset);
            
            byte[] buf = new byte[32768];
            long tot=0;
            while(tot < max_data) {
               long rl = max_data-tot;
               if (rl > buf.length) rl = buf.length;
               int r = rf.read(buf, 0, (int)rl);
               if (r == -1) throw new Exception("Ran out of bytes after " + tot);
               out.update(buf, 0, r);	
               tot += r;
            }
            
            long et = System.currentTimeMillis();
            long delt = et - st;
			
            String implString = out.hashAsString();	
			
            System.out.println("" + delt + " ms: MD5 value from implementation = " + 
                               implString); 
            rf.close();
            
         } else {
            System.out.println("<<<<<<<  No Arguments passed  >>>>>>>");
         }
	  	
      } catch(Exception ex) {
         ex.printStackTrace();
      }
   }



  /**	
   * Updates hash with the bytebuffer given (using at maximum length bytes from
   * that buffer)
   *
   * @param stat	Which state is updated
   * @param buffer	Array of bytes to be hashed
   * @param offset	Offset to buffer array
   * @param length	Use at maximum `length' bytes (absolute
   *			maximum is buffer.length)
   */
   public void update(DropboxMD5State stat, byte buffer[], int offset, int length) {
      int index, partlen, i, start;

     /* Length can be told to be shorter, but not inter */
      if ((length - offset) > buffer.length)
         length = buffer.length - offset;

     /* compute number of bytes mod 64 */
      index = (int) (stat.count & 0x3f);
      stat.count += length;

      partlen = 64 - index;

      if (length >= partlen) {

         int[] decode_buf = new int[16];
         if (partlen == 64) {
            partlen = 0;
         } else {
            for (i = 0; i < partlen; i++)
               stat.buffer[i + index] = buffer[i + offset];
            Transform(stat, stat.buffer, 0, decode_buf);
         }
         for (i = partlen;(i + 63) < length; i += 64) {
            Transform(stat, buffer, i + offset, decode_buf);
         }

         index = 0;
      } else
         i = 0;

      if (i < length) {
         start = i;
         for (; i < length; i++) {
            stat.buffer[index + i - start] = buffer[i + offset];
         }
      }
   }


   public void update (byte buffer[], int offset, int length) {
      update(this.state, buffer, offset, length);
   }

   public void update (byte buffer[], int length) {
      update(this.state, buffer, 0, length);
   }

 
   public void update (byte buffer[]) {
      update(buffer, 0, buffer.length);
   }


   public void update (byte buf) {
      byte buffer[] = new byte[1];
      buffer[0] = buf;

      update(buffer, 0, 1);
   }
  

   public void update (String s) {
      byte chars[] = s.getBytes();
      update(chars, 0, chars.length);
   }

  /**
   * Returns array of bytes (16 bytes) representing hash as of the
   * current state of this object.
   *
   * @return	Array of 16 bytes, the hash of all updated bytes
   */
   public synchronized byte[] Final() {
      byte	bits[];
      int		index, padlen;
      DropboxMD5State	fin;
      
      fin = new DropboxMD5State(state);
      
      int[] count_ints = {(int) (fin.count << 3), (int) (fin.count >> 29)};
      bits = Encode(count_ints, 8);
      
      index = (int) (fin.count & 0x3f);
      padlen = (index < 56) ? (56 - index) : (120 - index);
      
      update(fin, padding, 0, padlen);
      update(fin, bits, 0, 8);	
      
      return Encode(fin.state, 16);
   }    

   private byte[] Encode(int input[], int len) {
      int i, j;
      byte out[];
      out = new byte[len];
	
      for (i = j = 0; j < len; i++, j += 4) {
         out[j] = (byte) (input[i] & 0xff);
         out[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
         out[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
         out[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
      }
	
      return out;
   }

   public static String asHex (byte hash[]) {
      char buf[] = new char[hash.length * 2];
      for (int i = 0, x = 0; i < hash.length; i++) {
         buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
         buf[x++] = HEX_CHARS[hash[i] & 0xf];
      }
      return new String(buf);
   }

   public String asHex () {
      return asHex(this.Final());
   }
  
   public void stateFromString(String s) throws Exception {
      int l = s.length();
      
     // If its odd size or 0, complain
      if ((l & 1) != 0 || l == 0) {
         throw new Exception("Invalid size for incoming state string");
      }
      
      byte barr[] = new byte[l*2];
      char carr[] = s.toLowerCase().toCharArray();
      
     // Load up string data into byte array
      for(int i=0, j=0; i < l; i=(((++j & 1) == 0)?i+1:i)) {
         switch(carr[j]) {
            case '0': barr[i] = (byte)((barr[i] << 4) | (byte) 0);  break;
            case '1': barr[i] = (byte)((barr[i] << 4) | (byte) 1);  break;
            case '2': barr[i] = (byte)((barr[i] << 4) | (byte) 2);  break;
            case '3': barr[i] = (byte)((barr[i] << 4) | (byte) 3);  break;
            case '4': barr[i] = (byte)((barr[i] << 4) | (byte) 4);  break;
            case '5': barr[i] = (byte)((barr[i] << 4) | (byte) 5);  break;
            case '6': barr[i] = (byte)((barr[i] << 4) | (byte) 6);  break;
            case '7': barr[i] = (byte)((barr[i] << 4) | (byte) 7);  break;
            case '8': barr[i] = (byte)((barr[i] << 4) | (byte) 8);  break;
            case '9': barr[i] = (byte)((barr[i] << 4) | (byte) 9);  break;
            case 'a': barr[i] = (byte)((barr[i] << 4) | (byte)10);  break;
            case 'b': barr[i] = (byte)((barr[i] << 4) | (byte)11);  break;
            case 'c': barr[i] = (byte)((barr[i] << 4) | (byte)12);  break;
            case 'd': barr[i] = (byte)((barr[i] << 4) | (byte)13);  break;
            case 'e': barr[i] = (byte)((barr[i] << 4) | (byte)14);  break;
            case 'f': barr[i] = (byte)((barr[i] << 4) | (byte)15);  break;
            default:
               throw new Exception("Invalid data format");
         }
      }
      stateFromBytes(barr, 0, barr.length);
   }
  
   public String stateToString() throws Exception {
      return asHex(stateToBytes());
   }
  
   public void stateFromBytes(byte[] buf, int ofs, int len) throws Exception {
      InputStream in = new ByteArrayInputStream(buf, ofs, len);
      ObjectInputStream obIn = new ObjectInputStream(in);
      state = (DropboxMD5State) obIn.readObject();
      
      obIn.close();
   }
  
   public byte[] stateToBytes() throws Exception {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(b);
      out.writeObject(state);
      
      out.close();
      
      byte [] dataAsByteArray = b.toByteArray();
      
      return dataAsByteArray;
   }
   
   public String hashAsString() {
      return asHex();
   }

   private void Transform (DropboxMD5State state, byte buffer[], int shift, int[] decode_buf) {
      int	
         a = state.state[0],
         b = state.state[1],
         c = state.state[2],
         d = state.state[3],
         x[] = decode_buf;

      Decode(buffer, shift, decode_buf);
    
     /* Round 1 */
      a += ((b & c) | (~b & d)) + x[ 0] + 0xd76aa478; /* 1 */
      a = ((a << 7) | (a >>> 25)) + b;
      d += ((a & b) | (~a & c)) + x[ 1] + 0xe8c7b756; /* 2 */
      d = ((d << 12) | (d >>> 20)) + a;
      c += ((d & a) | (~d & b)) + x[ 2] + 0x242070db; /* 3 */
      c = ((c << 17) | (c >>> 15)) + d;
      b += ((c & d) | (~c & a)) + x[ 3] + 0xc1bdceee; /* 4 */
      b = ((b << 22) | (b >>> 10)) + c;

      a += ((b & c) | (~b & d)) + x[ 4] + 0xf57c0faf; /* 5 */
      a = ((a << 7) | (a >>> 25)) + b;
      d += ((a & b) | (~a & c)) + x[ 5] + 0x4787c62a; /* 6 */
      d = ((d << 12) | (d >>> 20)) + a;
      c += ((d & a) | (~d & b)) + x[ 6] + 0xa8304613; /* 7 */
      c = ((c << 17) | (c >>> 15)) + d;
      b += ((c & d) | (~c & a)) + x[ 7] + 0xfd469501; /* 8 */
      b = ((b << 22) | (b >>> 10)) + c;

      a += ((b & c) | (~b & d)) + x[ 8] + 0x698098d8; /* 9 */
      a = ((a << 7) | (a >>> 25)) + b;
      d += ((a & b) | (~a & c)) + x[ 9] + 0x8b44f7af; /* 10 */
      d = ((d << 12) | (d >>> 20)) + a;
      c += ((d & a) | (~d & b)) + x[10] + 0xffff5bb1; /* 11 */
      c = ((c << 17) | (c >>> 15)) + d;
      b += ((c & d) | (~c & a)) + x[11] + 0x895cd7be; /* 12 */
      b = ((b << 22) | (b >>> 10)) + c;

      a += ((b & c) | (~b & d)) + x[12] + 0x6b901122; /* 13 */
      a = ((a << 7) | (a >>> 25)) + b;
      d += ((a & b) | (~a & c)) + x[13] + 0xfd987193; /* 14 */
      d = ((d << 12) | (d >>> 20)) + a;
      c += ((d & a) | (~d & b)) + x[14] + 0xa679438e; /* 15 */
      c = ((c << 17) | (c >>> 15)) + d;
      b += ((c & d) | (~c & a)) + x[15] + 0x49b40821; /* 16 */
      b = ((b << 22) | (b >>> 10)) + c;
    
    
     /* Round 2 */
      a += ((b & d) | (c & ~d)) + x[ 1] + 0xf61e2562; /* 17 */
      a = ((a << 5) | (a >>> 27)) + b;
      d += ((a & c) | (b & ~c)) + x[ 6] + 0xc040b340; /* 18 */
      d = ((d << 9) | (d >>> 23)) + a;
      c += ((d & b) | (a & ~b)) + x[11] + 0x265e5a51; /* 19 */
      c = ((c << 14) | (c >>> 18)) + d;
      b += ((c & a) | (d & ~a)) + x[ 0] + 0xe9b6c7aa; /* 20 */
      b = ((b << 20) | (b >>> 12)) + c;

      a += ((b & d) | (c & ~d)) + x[ 5] + 0xd62f105d; /* 21 */
      a = ((a << 5) | (a >>> 27)) + b;
      d += ((a & c) | (b & ~c)) + x[10] + 0x02441453; /* 22 */
      d = ((d << 9) | (d >>> 23)) + a;
      c += ((d & b) | (a & ~b)) + x[15] + 0xd8a1e681; /* 23 */
      c = ((c << 14) | (c >>> 18)) + d;
      b += ((c & a) | (d & ~a)) + x[ 4] + 0xe7d3fbc8; /* 24 */
      b = ((b << 20) | (b >>> 12)) + c;

      a += ((b & d) | (c & ~d)) + x[ 9] + 0x21e1cde6; /* 25 */
      a = ((a << 5) | (a >>> 27)) + b;
      d += ((a & c) | (b & ~c)) + x[14] + 0xc33707d6; /* 26 */
      d = ((d << 9) | (d >>> 23)) + a;
      c += ((d & b) | (a & ~b)) + x[ 3] + 0xf4d50d87; /* 27 */
      c = ((c << 14) | (c >>> 18)) + d;
      b += ((c & a) | (d & ~a)) + x[ 8] + 0x455a14ed; /* 28 */
      b = ((b << 20) | (b >>> 12)) + c;

      a += ((b & d) | (c & ~d)) + x[13] + 0xa9e3e905; /* 29 */
      a = ((a << 5) | (a >>> 27)) + b;
      d += ((a & c) | (b & ~c)) + x[ 2] + 0xfcefa3f8; /* 30 */
      d = ((d << 9) | (d >>> 23)) + a;
      c += ((d & b) | (a & ~b)) + x[ 7] + 0x676f02d9; /* 31 */
      c = ((c << 14) | (c >>> 18)) + d;
      b += ((c & a) | (d & ~a)) + x[12] + 0x8d2a4c8a; /* 32 */
      b = ((b << 20) | (b >>> 12)) + c;
    
    
     /* Round 3 */
      a += (b ^ c ^ d) + x[ 5] + 0xfffa3942;      /* 33 */
      a = ((a << 4) | (a >>> 28)) + b;
      d += (a ^ b ^ c) + x[ 8] + 0x8771f681;      /* 34 */
      d = ((d << 11) | (d >>> 21)) + a;
      c += (d ^ a ^ b) + x[11] + 0x6d9d6122;      /* 35 */
      c = ((c << 16) | (c >>> 16)) + d;
      b += (c ^ d ^ a) + x[14] + 0xfde5380c;      /* 36 */
      b = ((b << 23) | (b >>> 9)) + c;
    
      a += (b ^ c ^ d) + x[ 1] + 0xa4beea44;      /* 37 */
      a = ((a << 4) | (a >>> 28)) + b;
      d += (a ^ b ^ c) + x[ 4] + 0x4bdecfa9;      /* 38 */
      d = ((d << 11) | (d >>> 21)) + a;
      c += (d ^ a ^ b) + x[ 7] + 0xf6bb4b60;      /* 39 */
      c = ((c << 16) | (c >>> 16)) + d;
      b += (c ^ d ^ a) + x[10] + 0xbebfbc70;      /* 40 */
      b = ((b << 23) | (b >>> 9)) + c;
    
      a += (b ^ c ^ d) + x[13] + 0x289b7ec6;      /* 41 */
      a = ((a << 4) | (a >>> 28)) + b;
      d += (a ^ b ^ c) + x[ 0] + 0xeaa127fa;      /* 42 */
      d = ((d << 11) | (d >>> 21)) + a;
      c += (d ^ a ^ b) + x[ 3] + 0xd4ef3085;      /* 43 */
      c = ((c << 16) | (c >>> 16)) + d;
      b += (c ^ d ^ a) + x[ 6] + 0x04881d05;      /* 44 */
      b = ((b << 23) | (b >>> 9)) + c;
    
      a += (b ^ c ^ d) + x[ 9] + 0xd9d4d039;      /* 33 */
      a = ((a << 4) | (a >>> 28)) + b;
      d += (a ^ b ^ c) + x[12] + 0xe6db99e5;      /* 34 */
      d = ((d << 11) | (d >>> 21)) + a;
      c += (d ^ a ^ b) + x[15] + 0x1fa27cf8;      /* 35 */
      c = ((c << 16) | (c >>> 16)) + d;
      b += (c ^ d ^ a) + x[ 2] + 0xc4ac5665;      /* 36 */
      b = ((b << 23) | (b >>> 9)) + c;
    

     /* Round 4 */
      a += (c ^ (b | ~d)) + x[ 0] + 0xf4292244; /* 49 */
      a = ((a << 6) | (a >>> 26)) + b;
      d += (b ^ (a | ~c)) + x[ 7] + 0x432aff97; /* 50 */
      d = ((d << 10) | (d >>> 22)) + a;
      c += (a ^ (d | ~b)) + x[14] + 0xab9423a7; /* 51 */
      c = ((c << 15) | (c >>> 17)) + d;
      b += (d ^ (c | ~a)) + x[ 5] + 0xfc93a039; /* 52 */
      b = ((b << 21) | (b >>> 11)) + c;

      a += (c ^ (b | ~d)) + x[12] + 0x655b59c3; /* 53 */
      a = ((a << 6) | (a >>> 26)) + b;
      d += (b ^ (a | ~c)) + x[ 3] + 0x8f0ccc92; /* 54 */
      d = ((d << 10) | (d >>> 22)) + a;
      c += (a ^ (d | ~b)) + x[10] + 0xffeff47d; /* 55 */
      c = ((c << 15) | (c >>> 17)) + d;
      b += (d ^ (c | ~a)) + x[ 1] + 0x85845dd1; /* 56 */
      b = ((b << 21) | (b >>> 11)) + c;

      a += (c ^ (b | ~d)) + x[ 8] + 0x6fa87e4f; /* 57 */
      a = ((a << 6) | (a >>> 26)) + b;
      d += (b ^ (a | ~c)) + x[15] + 0xfe2ce6e0; /* 58 */
      d = ((d << 10) | (d >>> 22)) + a;
      c += (a ^ (d | ~b)) + x[ 6] + 0xa3014314; /* 59 */
      c = ((c << 15) | (c >>> 17)) + d;
      b += (d ^ (c | ~a)) + x[13] + 0x4e0811a1; /* 60 */
      b = ((b << 21) | (b >>> 11)) + c;

      a += (c ^ (b | ~d)) + x[ 4] + 0xf7537e82; /* 61 */
      a = ((a << 6) | (a >>> 26)) + b;
      d += (b ^ (a | ~c)) + x[11] + 0xbd3af235; /* 62 */
      d = ((d << 10) | (d >>> 22)) + a;
      c += (a ^ (d | ~b)) + x[ 2] + 0x2ad7d2bb; /* 63 */
      c = ((c << 15) | (c >>> 17)) + d;
      b += (d ^ (c | ~a)) + x[ 9] + 0xeb86d391; /* 64 */
      b = ((b << 21) | (b >>> 11)) + c;

      state.state[0] += a;
      state.state[1] += b;
      state.state[2] += c;
      state.state[3] += d;
   }


   private void Decode (byte buffer[], int shift, int[] out) {
     

      out[0] = ((int) (buffer[shift] & 0xff)) |
         (((int) (buffer[shift + 1] & 0xff)) << 8) |
         (((int) (buffer[shift + 2] & 0xff)) << 16) |
         (((int)  buffer[shift + 3]) << 24);
      out[1] = ((int) (buffer[shift + 4] & 0xff)) |
         (((int) (buffer[shift + 5] & 0xff)) << 8) |
         (((int) (buffer[shift + 6] & 0xff)) << 16) |
         (((int)  buffer[shift + 7]) << 24);
      out[2] = ((int) (buffer[shift + 8] & 0xff)) |
         (((int) (buffer[shift + 9] & 0xff)) << 8) |
         (((int) (buffer[shift + 10] & 0xff)) << 16) |
         (((int)  buffer[shift + 11]) << 24);
      out[3] = ((int) (buffer[shift + 12] & 0xff)) |
         (((int) (buffer[shift + 13] & 0xff)) << 8) |
         (((int) (buffer[shift + 14] & 0xff)) << 16) |
         (((int)  buffer[shift + 15]) << 24);
      out[4] = ((int) (buffer[shift + 16] & 0xff)) |
         (((int) (buffer[shift + 17] & 0xff)) << 8) |
         (((int) (buffer[shift + 18] & 0xff)) << 16) |
         (((int)  buffer[shift + 19]) << 24);
      out[5] = ((int) (buffer[shift + 20] & 0xff)) |
         (((int) (buffer[shift + 21] & 0xff)) << 8) |
         (((int) (buffer[shift + 22] & 0xff)) << 16) |
         (((int)  buffer[shift + 23]) << 24);
      out[6] = ((int) (buffer[shift + 24] & 0xff)) |
         (((int) (buffer[shift + 25] & 0xff)) << 8) |
         (((int) (buffer[shift + 26] & 0xff)) << 16) |
         (((int)  buffer[shift + 27]) << 24);
      out[7] = ((int) (buffer[shift + 28] & 0xff)) |
         (((int) (buffer[shift + 29] & 0xff)) << 8) |
         (((int) (buffer[shift + 30] & 0xff)) << 16) |
         (((int)  buffer[shift + 31]) << 24);
      out[8] = ((int) (buffer[shift + 32] & 0xff)) |
         (((int) (buffer[shift + 33] & 0xff)) << 8) |
         (((int) (buffer[shift + 34] & 0xff)) << 16) |
         (((int)  buffer[shift + 35]) << 24);
      out[9] = ((int) (buffer[shift + 36] & 0xff)) |
         (((int) (buffer[shift + 37] & 0xff)) << 8) |
         (((int) (buffer[shift + 38] & 0xff)) << 16) |
         (((int)  buffer[shift + 39]) << 24);
      out[10] = ((int) (buffer[shift + 40] & 0xff)) |
         (((int) (buffer[shift + 41] & 0xff)) << 8) |
         (((int) (buffer[shift + 42] & 0xff)) << 16) |
         (((int)  buffer[shift + 43]) << 24);
      out[11] = ((int) (buffer[shift + 44] & 0xff)) |
         (((int) (buffer[shift + 45] & 0xff)) << 8) |
         (((int) (buffer[shift + 46] & 0xff)) << 16) |
         (((int)  buffer[shift + 47]) << 24);
      out[12] = ((int) (buffer[shift + 48] & 0xff)) |
         (((int) (buffer[shift + 49] & 0xff)) << 8) |
         (((int) (buffer[shift + 50] & 0xff)) << 16) |
         (((int)  buffer[shift + 51]) << 24);
      out[13] = ((int) (buffer[shift + 52] & 0xff)) |
         (((int) (buffer[shift + 53] & 0xff)) << 8) |
         (((int) (buffer[shift + 54] & 0xff)) << 16) |
         (((int)  buffer[shift + 55]) << 24);
      out[14] = ((int) (buffer[shift + 56] & 0xff)) |
         (((int) (buffer[shift + 57] & 0xff)) << 8) |
         (((int) (buffer[shift + 58] & 0xff)) << 16) |
         (((int)  buffer[shift + 59]) << 24);
      out[15] = ((int) (buffer[shift + 60] & 0xff)) |
         (((int) (buffer[shift + 61] & 0xff)) << 8) |
         (((int) (buffer[shift + 62] & 0xff)) << 16) |
         (((int)  buffer[shift + 63]) << 24);
      
      
   }
 

   public static Hashtable getArgsHashWithDelim(String args[], 
                                                String delim) throws Exception {
      String pStr = "";
      for(int i = 0; i < args.length; i++)
         pStr += args[i] + " ";
		
      Hashtable retHash = new Hashtable();
      StringTokenizer st1 = new StringTokenizer(pStr, delim);
      String key = null, val = null;
      while(st1.hasMoreTokens()) {
         String s1 = st1.nextToken();
         StringTokenizer st2 = new StringTokenizer(s1);
         if(st2.hasMoreTokens()) {
            key = st2.nextToken().trim();
            val = s1.substring(key.length()).trim();
            retHash.put(key, val);
         }
      }
      return retHash;
	
   }


}


