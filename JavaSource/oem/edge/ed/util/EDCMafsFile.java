package oem.edge.ed.util;

import java.lang.*;
import java.io.*;

public class EDCMafsFile {
   protected Process slave = null;
   protected boolean cmdmode = false;

   private final String lib = "afs_AIXNative";
   private static boolean loaded = false;
   
   private boolean verbose = false;
   
   public void setVerbose(boolean v) { verbose = v; }

  /*
  ** All opcodes return either the OPCODE to indicate success, or 0 followed
  **  by a 0 terminated string describing the problem
  **
  ** In addition, GetFileSize returns a 4 byte, bigendian size, and READDATA
  **  returns a a packet [2byte (15bit) size, data]. When size == 0, 
  **  EOF.
  */

  /* OPCODES */
   public static final byte VERIFYAUTH    = (byte)0x78; /* Scell, Suser, Spw */
   public static final byte AFSDO         = (byte)0x79; /* Scell, Suser, Spw */
   public static final byte DOFILEREAD    = (byte)0x7A; /* Sfile, B1id       */
   public static final byte DOFILEWRITE   = (byte)0x7B; /* Sfile, B1id, B1app*/
   public static final byte GETFILESIZE   = (byte)0x7C; /* Sfile             */
   public static final byte AFSDONE       = (byte)0x7D; /*                   */
   public static final byte DOMKDIR       = (byte)0x7E; /* Sfile             */
   public static final byte WRITEEXCLUSIVE= (byte)0x7F; /* Sfile, B1id       */
   public static final byte WRITEDATA     = (byte)0x80; /* B1id, B2size, data*/
   public static final byte ENDWRITE      = (byte)0x81; /* B1id              */
   public static final byte DELETEFILE    = (byte)0x82; /* Sfile             */
   public static final byte DELETEDIRECTORY=(byte)0x83; /* Sfile             */
   public static final byte ENDREAD       = (byte)0x84; /* B1id              */
   public static final byte READDATA      = (byte)0x85; /* B1id              */
   
   
   class afsInputStream extends InputStream {
      OutputStream out = null;
      InputStream  in  = null;
      byte bufarr[] = new byte[32768];
      int cofs = 0;
      int eofs = 0;
      boolean isClosed = false;
      boolean isEOF = false;
      
      public afsInputStream(OutputStream os, InputStream is) {
         out = os;
         in  = is;
      }
      
      public int read() throws IOException {
         byte arr[] = new byte[1];
         int ret = read(arr, 0, 1);
         if (ret == -1) {
            return -1;
         } else {
            return ((int)arr[0]) & 0xff;
         }
      }
      public int read(byte buf[], int ofs, int len) throws IOException {
         synchronized(bufarr) {
            if (isClosed) throw new IOException("stream is closed");
            if (len < 0) throw new IOException("Len is negative!");
            if (eofs < cofs) throw new IOException("insert ptr < extract ptr");
            if (isEOF) return -1;
            
            if (cofs == eofs) {
               cofs = eofs = 0;
               bufarr[0] = READDATA;
               bufarr[1] = (byte)1; // ID
               out.write(bufarr, 0, 2);
               out.flush();
               if (readByte(in) != READDATA) {
                  isClosed = true;
                  throw new IOException(readString(in));
               }
               
               short numbytes = readShort(in);
               if (numbytes <= 0) { 
                  isEOF = true; 
                  return -1;
               } else {
                  readBytes(in, bufarr, 0, numbytes);
                  eofs += numbytes;
               }
            }
            
            if (cofs < eofs) {
               int amt = eofs-cofs;
               if (amt > len) amt = len;
               System.arraycopy(bufarr, cofs, buf, ofs, amt);
               cofs += amt;
               return amt;
            }
            return 0;
         }
      }
      public int read(byte buf[]) throws IOException {
         return read(buf, 0, buf.length);
      }
      
      public void close() throws IOException {
        // Generate close protocol to afsProcessForAction
        
         if (isClosed) throw new IOException("Already closed");
         isClosed = true;
         
        //if (verbose) System.out.println("Sending ENDREAD");
         
         bufarr[0] = ENDREAD;
         bufarr[1] = (byte)0x01; //ID
         out.write(bufarr, 0, 2);
         out.flush();
         if (readByte(in) != ENDREAD) {
            throw new IOException(readString(in));
         }            
      }
      
      public int available() throws IOException {
         synchronized(bufarr) {
            if (isClosed) throw new IOException("stream is closed");
            if (eofs < cofs) throw new IOException("insert ptr < extract ptr");
            return eofs-cofs;
         }
      }
      public boolean markSupported() {
         return false;
      }
      public void mark() {
      }
      public void reset() throws IOException {
         throw new IOException("I SAID mark was not supported");
      }
      
      public long skip(long n) throws IOException {
         
         if (n <= 0) return 0;
         
         synchronized(bufarr) {
         
            int av = available();
            
            if (n <= av) {
               cofs += n;
               return n;
            } else if (av > 0) {
               cofs += av;
               return av;
            }
         }
            
         byte b[] = new byte[(int)n>4096?4096:(int)n];
         long tot = 0;
         while(!isClosed && tot < n) {
            int toread = (int)(n-tot);
            if (toread > b.length) toread = b.length;
            int r = read(b, 0, toread);
            if (r < 0) break;
            tot += r;
         }
         return tot;
      }
   }
   
   class afsOutputStream extends OutputStream {
      OutputStream out = null;
      InputStream  in  = null;
      byte arr[] = new byte[4];
      boolean isclosed = false;
      
      afsOutputStream(OutputStream os, InputStream is) {
         out = os;
         in  = is;
      }
      
      public void close() throws IOException {
        // Generate close protocol to afsProcessForAction
        
         if (isclosed) throw new IOException("Already closed");
         isclosed = true;
         
         arr[0] = ENDWRITE;  
         arr[1] = (byte)0x01; // ID
         out.write(arr, 0, 2);
         out.flush();
         if (readByte(in) != ENDWRITE) {
            throw new IOException(readString(in));
         }            
      }
      
      public void flush() throws IOException {
         out.flush();
      }
      
      public void write(byte b[]) throws IOException {
         write(b, 0, b.length);
      }
      
     /*
     ** Send data to afsProcessForAction using opcode 0x80
     **
     **    format: 0x80, byteid (ignored), 2bytesize, data
     */
      public synchronized void write(byte b[], 
                                     int off, int len) throws IOException {
         if (len < 0) {
            throw new IOException("Negative length not allowed");
         }
       
         int written = 0;
//         System.out.println("Writing len =" + len);
         while(written < len) {
            int towrite = (len - written);
            if (towrite > 0x7fff) towrite = 0x7fff;
            
            arr[0] = (byte)WRITEDATA;  
            arr[1] = (byte)0x01; // ID
            arr[2] = (byte)((towrite >> 8) & 0xff);
            arr[3] = (byte)((towrite)      & 0xff);
//            System.out.println("Writing header towrite=" + towrite);
            out.write(arr, 0, 4);
//            System.out.println("Writing body");
            out.write(b, off + written, towrite);
//            System.out.println("Reading Resp");
            out.flush();
            if (readByte(in) != WRITEDATA) {
               throw new IOException(readString(in));
            }
            
//            System.out.println("Adjust written");
            written += towrite;
         }
      }
      
      
     // You get what you deserve it you use this
      public void write(int ch) throws IOException {
         byte b[] = new byte[1];
         b[0] = (byte)ch;
         write(b, 0, 1);
      }
   }
   
   public int readBytes(InputStream tin, byte buf[], 
                        int ofs, int len) throws IOException {
      int totlen = 0;
      int l = 0;
      while(totlen < len && l >= 0) {
         l=tin.read(buf, totlen, len-totlen);
         if (l > 0) totlen += l;
      }
      
      if (l < 0) throw new IOException("EOF while reading bytes from slave");
      return totlen;
   }
   public String readString(InputStream tin) throws IOException {
      StringBuffer ret = new StringBuffer();
      int totlen = 0;
      int l = 0;
      while(totlen < 32768 && l >= 0) {
         l=tin.read();
         if (l > 0) {
             totlen++;
             ret.append((char)l);
         } else if (l < 0) {
            throw new IOException("EOF while reading string");
         } else {
           //if (verbose) System.out.println("ReadString breaking with len=" + totlen);
            break;
         }
      }
      
     //if (verbose) System.out.println("ReadString returns '" + ret.toString() + "'");
      return ret.toString();
   }
   
   byte usebuf[] = new byte[4];
   public int readInteger(InputStream tin) throws IOException {
      readBytes(tin, usebuf, 0, 4);
                                      
      int ret = ((((int)usebuf[0]) & 0xff) << 24 | 
                 (((int)usebuf[1]) & 0xff) << 16 | 
                 (((int)usebuf[2]) & 0xff) << 8  | 
                 (((int)usebuf[3]) & 0xff));
                 
     //if (verbose) System.out.println("ReadInteger returns " + ret);
      
      return ret;
   }
   
   public short readShort(InputStream tin) throws IOException {
      readBytes(tin, usebuf, 0, 2);
      short ret = (short)(((((int)usebuf[0]) & 0xff) << 8)  | 
                          ((((int)usebuf[1]) & 0xff)));
                          
     //if (verbose) System.out.println("ReadShort returns " + ret);
                          
      return ret;
   }
   public byte readByte(InputStream tin) throws IOException {
      readBytes(tin, usebuf, 0, 1);
      
     //if (verbose) System.out.println("ReadByte returns " + usebuf[0]);
      
      return usebuf[0];
   }
      
   public String getRoutineName() { return "ka_UserAuthenticateGeneral"; }

   public EDCMafsFile() {}

   // getSlave() - returns or create a child process 
   //              running afsProcessForAction
   protected Process getSlave() {
      if (slave == null) {
         try {
            String cmd = new String("afsProcessForAction");
            slave = Runtime.getRuntime().exec(cmd.toString());
            cmdmode = true;
         } catch(IOException e) {
            System.out.println("Error doing afsProcessForAction");
            e.printStackTrace();
         } catch(SecurityException e) {
            System.out.println("Error doing afsProcessForAction");
            e.printStackTrace();
         }
      }
      return slave;
   }

   // localAuth() - obtain an afs token for the current process using 
   //               a native call, ka_UserAuthenticateGeneral.
   native boolean localAuth(String cell, String userid, String passwd);
   
   // afsVerifyAuth() - authenticate a userid and remove its token in
   //                   a child process.
   public boolean afsVerifyAuth(String cell, String userid, String passwd) {
      boolean ans = false;
      Process sl = null;

      if (cell == null | userid == null | passwd == null)
         return ans;

      try{
         sl = Runtime.getRuntime().exec("afsProcessForAction");
      } catch(IOException e) {
            e.printStackTrace();
      } catch(SecurityException e) {
            e.printStackTrace();
      }

      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try {
            out.write((byte)VERIFYAUTH);
            out.write(cell.getBytes());
            out.write((byte)0x0);
            out.write(userid.getBytes());
            out.write((byte)0x0);
            out.write(passwd.getBytes());
            out.write((byte)0x0);
            out.flush();
            ans = (readByte(in) == VERIFYAUTH);
            if (!ans) {
               String s = readString(in);
               if (verbose) System.out.println("VerifyAuth: Failed: " + s);
            }
            sl.destroy();
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ans;
   }

   // afsAuthenticate() - authenicate a userid in this current
   //                     process
   public boolean afsAuthenticate(String cell, String userid, String passwd) {

     boolean ans = false;

     if (cell == null | userid == null | passwd == null)
         return ans;

     loadLibrary();

     if(localAuth(cell, userid, passwd)) {
        ans = true;
     } 
     return ans;
   }

   // afsDo() - authenticate a userid in a child process
   public boolean afsDo(String cell, String userid, String passwd) {

      boolean ans = false;
      if (cell == null | userid == null | passwd == null)
         return ans;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try {
            out.write((byte)AFSDO);
            out.write(cell.getBytes());
            out.write((byte)0x0);
            out.write(userid.getBytes());
            out.write((byte)0x0);
            out.write(passwd.getBytes());
            out.write((byte)0x0);
            out.flush();
            ans = (readByte(in) == AFSDO);
            if (!ans) {
               String s = readString(in);
               if (verbose) System.out.println("AFSDo: Failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ans;
   }

   // doFileRead() - assuming a token is obtain, read a file
   public InputStream doFileRead(String filepath) {
                   
      InputStream ret = null;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            out.write((byte)DOFILEREAD);
            out.write(filepath.getBytes());
            out.write((byte)0x0);  // String Terminator
            out.write((byte)0x1);  // ID
            out.flush();
            
            if (readByte(in) == DOFILEREAD) {
               ret = new afsInputStream(out, in);
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doFileRead failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
      
   // doFileWrite() - assume a token is obtained, writing to a file
   public OutputStream doFileWrite(String filepath, boolean append)  {

      OutputStream ret = null;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            byte op = (byte)(DOFILEWRITE);
            out.write(op);
            out.write(filepath.getBytes());
            out.write((byte)0x0);  // String terminator
            out.write((byte)0x1);  // ID
            out.write((byte)(append?0x01:0x0)); // append
            out.flush();
            if (readByte(in) == op) {
               ret = new afsOutputStream(out, in);
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doFileWrite failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
      
   // doFileWrite() - assume a token is obtained, writing to a file
   public OutputStream doFileWrite(String filepath)  {
      return doFileWrite(filepath, false);
   }
   
   
   // doMkdir() - assume a token is obtained, create Directory
   public boolean doMkdir(String filepath) {

      boolean ret = false;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            out.write((byte)DOMKDIR);
            out.write(filepath.getBytes());
            out.write((byte)0x0);
            out.flush();
            if (readByte(in) == DOMKDIR) {
               ret = true;
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doMkdir failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
   
   // doFileWrite() - assume a token is obtained, writing to a file
   public OutputStream doFileWriteExclusive(String filepath) {

      OutputStream ret = null;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            byte op = (byte)(WRITEEXCLUSIVE);
            out.write(op);
            out.write(filepath.getBytes());
            out.write((byte)0x0);  // String terminator
            out.write((byte)0x1);  // ID
            out.flush();
            if (readByte(in) == op) {
               ret = new afsOutputStream(out, in);
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doFileWriteExcl failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
   
   // getFileSize() - assume a token is obtain, return file size
   public int getFileSize(String reqFile) {

      int ret = -1;
      if (reqFile == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            out.write((byte)GETFILESIZE);
            out.write(reqFile.getBytes());
            out.write((byte)0x0);
            out.flush();
            
            if (readByte(in) != GETFILESIZE) {
               String s = readString(in);
               if (verbose) System.out.println("GETFILESIZE failed: " + s);
            } else {             
               ret = readInteger(in);               
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }

      return ret;
   }

   // doDeleteFile()
   public boolean doDeleteFile(String filepath) {

      boolean ret = false;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            out.write((byte)DELETEFILE);
            out.write(filepath.getBytes());
            out.write((byte)0x0);
            out.flush();
            if (readByte(in) == DELETEFILE) {
               ret = true;
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doDeleteFile failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
   
   // doDeleteFile()
   public boolean doDeleteDirectory(String filepath) {

      boolean ret = false;
      if (filepath == null)
         return ret;

      Process sl = getSlave();
      if (sl != null) {
         OutputStream out = sl.getOutputStream();
         InputStream  in  = sl.getInputStream();
         try { 
            out.write((byte)DELETEDIRECTORY);
            out.write(filepath.getBytes());
            out.write((byte)0x0);
            out.flush();
            if (readByte(in) == DELETEDIRECTORY) {
               ret = true;
            } else {
               String s = readString(in);
               if (verbose) System.out.println("doDeleteDir failed: " + s);
            }
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
      return ret;
   }
   
   // afsDone() - if the child process still around, cleanup
   public void afsDone() {
      if (slave != null) {
         OutputStream out = slave.getOutputStream();
         InputStream  in  = slave.getInputStream();
         try { 
            if (cmdmode) {
               out.write((byte)AFSDONE);
               out.flush();
               readByte(in);
            } 
            out.flush();
            slave.destroy();
         } catch(IOException e) {
            e.printStackTrace();
         }
      }
   }

   // loadLibrary() - load the library which contains the native call
   private boolean loadLibrary() {
      if (!loaded) {
         try {
            System.loadLibrary(lib);
            synchronized(this) {
               loaded = true;
            }
         } catch(UnsatisfiedLinkError e) {
            System.out.println(getRoutineName() + ": Could not load " + lib);
            System.out.println("Exception nofile: " + e.toString());
         } catch(SecurityException e) {
            System.out.println(getRoutineName() + ": Could not load " + lib);
            System.out.println("Exception Security: " + e.toString());
         } catch(Exception e) {
            System.out.println(getRoutineName() + ": Could not load " + lib);
            System.out.println("Exception generic: " + e.toString());
         }
      }
      
      return loaded;
   }

  // a simple test for all the methods above
   public  static void main(String argv[]) {
      EDCMafsFile f = new EDCMafsFile();
      
      f.setVerbose(true);
      
      for(int i=0; i < argv.length; i++) {
         
         if (argv[i].equalsIgnoreCase("getFileSize")) {
            System.out.println(argv[i] + " " + argv[i+1] + " = " +
                               f.getFileSize(argv[i+1]));
            i++;
         } else if (argv[i].equalsIgnoreCase("doMkdir")) {
            System.out.println(argv[i] + " " + argv[i+1] + " = " +
                               f.doMkdir(argv[i+1]));
            i++;
         } else if (argv[i].equalsIgnoreCase("doFileRead")) {
            System.out.println(argv[i] + " " + argv[i+1]);
            try {
               InputStream in = f.doFileRead(argv[i+1]);
               int ch;
               while((ch = in.read()) != -1)
                  System.out.write(ch);
                  
               in.close();
            } catch (IOException e) {
               System.out.println(e);
            }
            i++;
         } else if (argv[i].equalsIgnoreCase("doFileWrite")) {
            System.out.println(argv[i] + " " + argv[i+1] + 
                               " append=" + argv[i+2]);
            try{
               OutputStream out = f.doFileWrite(argv[i+1], 
                                           argv[i+2].equalsIgnoreCase("true"));
               int ch;
               while((ch = System.in.read()) != -1) {
                  out.write(ch);
                  out.flush();
                  System.out.println((char)ch);
               }
               out.flush();
               out.close();
            } catch (IOException e) {
               System.out.println(e);
            }
            i += 2;
         } else if (argv[i].equalsIgnoreCase("lock")) {
            System.out.println(argv[i] + " " + argv[i+1]);
            try{
               OutputStream out = f.doFileWriteExclusive(argv[i+1]);
               if (out != null) {
                  out.close();
               } else {
                  System.out.println("Error getting lock");
               }
            } catch (IOException e) {
               System.out.println(e);
            }
            i += 1;
         } else if (argv[i].equalsIgnoreCase("deletefile")) {
            System.out.println(argv[i] + " " + argv[i+1]);
            System.out.println("Delete of file ok? " + 
                               f.doDeleteFile(argv[i+1]));
            i += 1;
         } else if (argv[i].equalsIgnoreCase("deletedir")) {
            System.out.println(argv[i] + " " + argv[i+1]);
            System.out.println("Delete of dir ok? " + 
                               f.doDeleteDirectory(argv[i+1]));
            i += 1;
         } else if (argv[i].equalsIgnoreCase("afsDo")) {
            System.out.println(argv[i] + " " + 
                               argv[i+1] + ", " + argv[i+2] + ", " + 
                               argv[i+3] + " = " + 
                               f.afsDo(argv[i+1], argv[i+2], argv[i+3]));
            i += 3;
         } else if (argv[i].equalsIgnoreCase("afsVerifyAuth")) {
            System.out.println(argv[i] + " " + 
                               argv[i+1] + ", " + argv[i+2] + ", " + 
                               argv[i+3] + " = " + 
                               f.afsVerifyAuth(argv[i+1], argv[i+2], 
                                               argv[i+3]));
            i += 3;
         } else if (argv[i].equalsIgnoreCase("afsAuthenticate")) {
            System.out.println(argv[i] + " " + 
                               argv[i+1] + ", " + argv[i+2] + ", " + 
                               argv[i+3] + " = " + 
                               f.afsAuthenticate(argv[i+1], argv[i+2], 
                                                 argv[i+3]));
            i += 3;
         } else {
            System.out.println("Error: Unknown command " + argv[i]);
         }         
      }
      f.afsDone();
   }
}
