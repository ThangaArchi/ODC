package oem.edge.ed.util;

import java.lang.*;
import java.io.*;
import java.lang.reflect.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

public class EDCMgsaFile extends EDCMafsFile {
   
   
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
      
   public EDCMgsaFile() { super(); }
   
   
   public boolean authenticate(String cell, String userid, String passwd) {
     return afsVerifyAuth(cell, userid, passwd);
   }
   public boolean afsAuthenticate(String cell, String userid, String passwd) {
      return afsVerifyAuth(cell, userid, passwd);
   }

   public boolean afsDo(String cell, String userid, String passwd) {
      return afsAuthenticate(cell, userid, passwd);
   }

   public boolean afsVerifyAuth(String cell, String username, String password) {

      String inString = "";
      String errString = "";
      int arrSize = 1024;
      byte[] arr = new byte[arrSize];
      int exitValue = -1;

      
     if (cell == null | username == null | password == null) return false;
      
      String cmd = "gsa_login -c " + cell + " -p " + username;
      
      try {
      
         Process p = Runtime.getRuntime().exec(cmd);

         try {
            BufferedOutputStream out = new BufferedOutputStream(p.getOutputStream());
            out.write(password.getBytes());
            out.write("\n".getBytes());
            out.close();

            exitValue = p.waitFor();

            BufferedInputStream in = new BufferedInputStream(p.getInputStream());
            BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
            int read = 0;
            while(read >= 0) {
               inString += new String(arr, 0, read);
               read = in.read(arr, 0, arrSize);
            }
            read = 0;
            while(read >= 0) {
               errString += new String(arr, 0, read);
               read = err.read(arr, 0, arrSize);
            }
            in.close();
            err.close();
            
         } catch(Throwable t1) {
            System.out.println("Thrown reading output from : " + cmd + ":");
            t1.printStackTrace(System.out);
         }


         if(exitValue != 0) {
            String str
               = cmd + " returned an exit Value of " + exitValue + "\n"
               + "stdout: " + inString + "\n"
               + "stderr: " + errString;

            System.out.println(str);
         }

      } catch(Throwable t) {
         System.out.println("Thrown reading output from : " + cmd + ":");
         t.printStackTrace(System.out);
      }

      if(exitValue == 0)
         return true;
      else
         return false;

   }
   
   public InputStream doFileRead(String filepath) {
                   
      try {
         return new FileInputStream(filepath);
      } catch(Exception e) {
         e.printStackTrace(System.out);
         return null;
      }
   }
      
   public OutputStream doFileWrite(String filepath, boolean append) {
                   
      try {
         return new FileOutputStream(filepath, append);
      } catch(Exception e) {
         e.printStackTrace(System.out);
         return null;
      }
   }
      
   public OutputStream doFileWrite(String filepath)  {
      return doFileWrite(filepath, false);
   }
   
   public boolean doMkdir(String filepath) {

      try {
         File f = new File(filepath);
         return f.mkdir();
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }
      return false;
   }
   
   // Will only work for Java 1.4
   public OutputStream doFileWriteExclusive(String filepath) {

      FileOutputStream fos = null;
      try {
         fos = new FileOutputStream(filepath);
         
        // FileChannel channel = fos.getChannel();
        // FileLock fl = channel.lock();
         
        // Crap ... I compile with 1.3, so use reflection to get at this
         Class parms[] = new Class[0];
         Method getchmeth = 
            FileOutputStream.class.getMethod("getChannel", parms);
         Object[] args = new Object[0];
         Object channel = getchmeth.invoke(fos, args);
         
         Method lockmeth = channel.getClass().getMethod("tryLock", parms);
         Object ret = lockmeth.invoke(channel, args);
         if (ret == null) {
            fos.close();
            fos = null;
         }
         
         return fos;
      } catch(Exception e) {
         try {
            fos.close();
         } catch(Exception ee) {}
      }
      
      return null;
   }
   
   // getFileSize() - assume a token is obtain, return file size
   public int getFileSize(String reqFile) {

      int ret = -1;
      
      try {
         File f = new File(reqFile);
         if (f.exists()) {
            ret = (int)f.length();
         }
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }

      return ret;
   }

   public boolean doDeleteFile(String filepath) {

      boolean ret = false;
      
      try {
         File f = new File(filepath);
         return f.delete();
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }

      return ret;
   }
   
   public boolean doDeleteDirectory(String filepath) {

      boolean ret = false;
      
      try {
         File f = new File(filepath);
         return f.delete();
      } catch(Exception e) {
         e.printStackTrace(System.out);
      }

      return ret;
   }
   
   // afsDone() - if the child process still around, cleanup
   public void afsDone() {
   }

  // a simple test for all the methods above
   public  static void main(String argv[]) {
      EDCMgsaFile f = new EDCMgsaFile();
      
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
               if (out == null) {
                  System.out.println("Error opening file for exclusive write: " +
                                    argv[i+1]);
               } else {
                  int ch;
                  while((ch = System.in.read()) != -1) {
                     out.write(ch);
                     out.flush();
                     System.out.println((char)ch);
                  }
                  out.flush();
                  out.close();
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
