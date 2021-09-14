package oem.edge.ed.util;
import oem.edge.common.cipher.*;
import oem.edge.common.RSA.*;
import com.ibm.as400.webaccess.common.*;
import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.net.*;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005,2006                                */
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

public class SearchEtc {

   static
   public Enumeration sortVector(Enumeration e, boolean uniqIt) {
      Vector v = new Vector();
      while(e.hasMoreElements()) {
         v.addElement(e.nextElement());
      }
      sortVector(v, uniqIt);
      return v.elements();
   }
   
   static
   public void sortVector(Vector v, boolean uniqIt) {
      int i,j;
      int count = v.size();
      int end = count;
      boolean swapped = true;
      for (i=0; i<count-1 && swapped; i++) {
        //swapped = false;
         for (j=0; j<end-1; j++) {
            String o1, o2;
            o1 = (String)v.elementAt(j);
            o2 = (String)v.elementAt(j+1);
            if(o2.compareTo(o1) < 0){
               v.setElementAt(o2, j);
               v.setElementAt(o1, j+1);
               swapped = true;
              //System.out.println("Swapping " + o1 + ", " + o2);
            } else if (uniqIt && o1.equals(o2)) {
              //System.out.println("EQUAL UNiqueIt " + o1 + "," + o2);
               v.removeElementAt(j+1);
               count--;
               end--;
              //} else {
              //System.out.println("NOSWAP g " + o1 + ", " + o2);
            }
         }
         end--;
      }
   }

  // Returns true of the char at idx is escaped, false otherwise 
   public static boolean isEscaped(String t, int idx) {
      if (idx <= 0 || t.charAt(idx-1) != '\\') return false;
      
      int i=1;
      idx -= 2;
      while(idx >= 0 && t.charAt(idx) == '\\') {
         i++;
         idx--;
      }
      
     // If number of found slashes is odd, then its escaped
      return (i & 1) != 0;
   }
            
  /**
   ** Escape the string using backslashes (\, % and _) 
   ** If iswild is specified, then convert all unescaped (* and .) to (% and _)
  */
   public static String sqlEscape(String t, boolean iswild) {
      boolean hasWild = false;
   
     // Remove all '\' (escape) chars other than an escaped backslash. 
     // If iswild is false, then also remove escapes from * and .
      int idx=0;
      char ch;
      while((idx=t.indexOf('\\', idx)) >=0) {
         if (t.length() == idx+1           || 
             ((ch=t.charAt(idx+1)) != '\\' &&
              (!iswild || (ch != '*'  && ch != '.')))) {
             
            t = t.substring(0,idx) + t.substring(idx+1);
            
         } else {
            idx+=2;
         }
      }
      
      
     // Escape all % and _ chars
      idx=0;
      while((idx=t.indexOf('%', idx)) >=0) {
         t = t.substring(0, idx) + '\\' + t.substring(idx);
         idx += 2;
      }
         
      idx=0;
      while((idx=t.indexOf('_', idx)) >=0) {
         t = t.substring(0, idx) + '\\' + t.substring(idx);
         idx += 2;
      }
         
         
      if (iswild) {
        // Convert all * -> % and . -> _  unless its escaped
         idx=0;
         while((idx=t.indexOf('*', idx)) >=0) {
            if (!isEscaped(t, idx)) {
               t = t.substring(0, idx) + '%' + t.substring(idx+1);
               hasWild = true;
            }
            idx++;
         }
         
         idx=0;
         while((idx=t.indexOf('.', idx)) >=0) {
            if (!isEscaped(t, idx)) {
               t = t.substring(0, idx) + '_' + t.substring(idx+1);
               hasWild = true;
            }
            idx++;
         }
      }
      
      return t;
   }
   
        
   public static String htmlEscape(String aTagFragment){

      StringBuffer result = null;
 
     // Perhaps we should return "" string if null sent in
      if (aTagFragment == null) return null;
 
      final StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
      char character =  iterator.current();
      int idx     =  0;
      for(;  character != CharacterIterator.DONE;  idx++, character=iterator.next()){
         switch(character) {
            case '<':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&lt;");
               break;
            case '>':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&gt;");
               break;
            case '"':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&quot;");
               break;
            case ';':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&#059;");
               break;
            case '\'':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&#039;");
               break;
            case '\\':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&#092;");
               break;
            case '&':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&amp;");
               break;
            case '/':
               if (result == null) result = new StringBuffer(aTagFragment.substring(0, idx));
               result.append("&#047;");
               break;
            default:
               if (result != null) result.append(character);
               break;
         }
      }
      return (result != null)?result.toString():aTagFragment;
   }   
   
   public static void printStackTrace(Throwable e, PrintStream strm) {
      strm.println(getStackTrace(e));
   }
   
   public static void printStackTrace(Throwable e) {
      printStackTrace(e, System.err);
   }
   
   public static String getStackTrace(Throwable e) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
      PrintStream eout = new PrintStream(baos,true);
      
      e.printStackTrace(eout);
      
      eout.flush();
      return baos.toString();
   }
   
   
   public static Collection randomize(Collection c) {
      ArrayList al = new ArrayList();
      al.addAll(c);
      Collections.shuffle(al);
      return al;
   }
   
   public static ODCipherRSA loadCipherFile(String cipherFile) {
      ODCipherRSA ret = null;
      try {
         
         ret = ODCipherRSAFactory.newFactoryInstance().newInstance();
         ret.load(findFileInClasspath(cipherFile));
      } catch(Throwable t) {
         System.out.println("Error loading CipherFile! [" + cipherFile + "]");
         t.printStackTrace(System.out);
      }
      return ret;
   }

   public static String createToken(ODCipherRSA cipher, 
                                    Hashtable tab, 
                                    int secs) throws Exception {
      ConfigObject co = new ConfigObject();
      Enumeration enum = tab.keys();
      while(enum.hasMoreElements()) {
         String key = (String)enum.nextElement();
         co.setProperty(key, (String)tab.get(key));
      }
      return cipher.encode(secs, co.toString()).getExportString();
   }
   
   public static Hashtable dataFromToken(ODCipherRSA cipher, String token) {
   
      Hashtable ret  = new Hashtable();
      String err     = null;
      String invalid = null;
      try {
         ODCipherData cd = cipher.decode(token);
         
         if (!cd.isCurrent()) {
            err = "Token is expired";
         } else {
            String ldata = cd.getString();
            ConfigObject co = new ConfigObject();
            co.fromString(ldata);
//            System.out.println("TOKEN CONTENTS\n" + co.toString());
            
            Enumeration elements = co.getPropertyNames();
            while(elements.hasMoreElements()) {
               String key = (String)elements.nextElement();
               ret.put(key, co.getProperty(key));
            }
         }
      } catch(DecodeException de) {
         err = "Token invalid: " + token;
         invalid="TRUE:";
         
      } catch(Exception ee) {
         err = "Unknown error";
      }
      
      if (err != null) {
         ret.put("ERROR", err);
      }
      
      if (invalid != null) {
         ret.put("INVALID", invalid);
      }
      
      return ret;
   }
   
   static public String findFileInDirectories(String fname, String dirs) 
      throws IOException {
      
      DebugPrint.println(DebugPrint.INFO4, "Find: " + fname);
      DebugPrint.println(DebugPrint.INFO5, "   searchdirs = " + dirs);
                         
     // Fullpath - This MAY be wrong for Windows where Drive letters are used
     //              sigh ... too bad TODO
      File f = new File(fname);
      if (f.isAbsolute()) {
         DebugPrint.println(DebugPrint.INFO4, "Find -FULLPATH-: " + 
                            f.toString());
         if (f.exists()) {
            return fname;
         } else {
            return null;
         }
      }
      
      if (dirs == null) { 
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Searching directory is NULL! Was searching for " + 
                             fname);
         return null;
      }
      
      StringTokenizer st = new StringTokenizer(dirs, 
                                               File.pathSeparator,
                                               false);
      while(st.hasMoreTokens()) {
         f = new File(st.nextToken(), fname);
         DebugPrint.println(DebugPrint.INFO4, "Find " + f.toString());
         if (f.exists()) {
            
            DebugPrint.println(DebugPrint.INFO4, 
                               "Found file at " + f.toString());
            return f.getCanonicalPath();
         }
      }
      return null;
   }
   
   static public
   String findFileInDirectoriesThenClasspath(String fname,
                                             String dirs) throws IOException {
      String ret = findFileInDirectories(fname, dirs);
      if (ret != null) return ret;
      return findFileInClasspath(fname);
   }
   
   static public String findFileInClasspath(String fname) throws IOException {
      Properties prop = System.getProperties();
      String classpath = prop.getProperty("java.class.path");
      String ret =  findFileInDirectories(fname, classpath);
      
      if (ret == null) {
      
         ret = findFileUsingClassloader(fname);
        /*
        // Only take this method if its different than a regular file OR failed
        //  with above method.
         try {
            java.net.URL uf  = SearchEtc.class.getResource(fname);
            if (uf != null) {
               String us = uf.toString();
               if (!us.startsWith("file:")) {
                  ret = us.substring(5);
               }
            }
         } catch(Exception ee) {
         }
        */
      }
      return ret;
   }
   
   
  // this exists to support unit testing	
   public static String findFileUsingClassloader(String fname) {
      try {
         ClassLoader contextClassLoader = (ClassLoader) AccessController.doPrivileged(
            new PrivilegedAction() {
               public Object run() {
                  return Thread.currentThread().getContextClassLoader();
               }
            });
         
         URL url = getResource(contextClassLoader, fname);
         if (url != null) {
            try {
               File f = new File(URLDecoder.decode(url.getFile())); 
               
               if (f.exists()) {
                  DebugPrint.println(DebugPrint.INFO4, 
                                     "Found file using CL at " + f.toString());
                  return f.getCanonicalPath();
               }
            } catch(Exception ee) {
               DebugPrint.println(DebugPrint.INFO4, 
                                  "Exception while searching for " + fname);
               DebugPrint.println(DebugPrint.INFO4, 
                                  ee);
            }
         }
      } catch(java.lang.SecurityException se) {
         DebugPrint.println(DebugPrint.INFO4, 
                            "Security Exception when searching for " + fname);
      }
      return null;
   }
   
   private static URL getResource(final ClassLoader loader, final String name) {
      return (URL) AccessController.doPrivileged(
         new PrivilegedAction() {
            public Object run() {
               if (loader != null) {
                  return loader.getResource(name);
               } else {
                  return ClassLoader.getSystemResource(name);
               }
            }
         });
   }
   
   
   public static String calculateMD5(File f) throws IOException {
      return calculateMD5(f, -1);
   }
   
   public static String calculateMD5(File f, long inlen) throws IOException {
      return calculateMD5(f, 0, inlen);
   }
   
   public static String calculateMD5(File f, long ofs, long inlen) throws IOException {
      String ret = null;
      byte buf[] = new byte[32768];
      RandomAccessFile fis = new RandomAccessFile(f, "r");
      fis.seek(ofs);
      
      try {
         java.security.MessageDigest digest;
         digest = java.security.MessageDigest.getInstance("MD5");
         
         long len = inlen;
         
         if (len < 0) len = f.length() - ofs;
         
         while(len > 0) {
            int r = (int)(len > buf.length ? buf.length : len);
            r = fis.read(buf, 0, r);
//         System.out.println(showbytes(buf, 0, r));
            if (r == -1) {
               throw new IOException("Ran out of bytes before finished MD5");
            }
            digest.update(buf, 0, r);
            len -= r;
         }
         
         byte arr[] = digest.digest();
         StringBuffer ans = new StringBuffer();
         for(int i=0 ; i < arr.length; i++) {
            String v = Integer.toHexString(((int)arr[i]) & 0xff);
            if (v.length() == 1) ans.append("0");
            ans.append(v);
         }
         
         ret = ans.toString();
      } catch(java.security.NoSuchAlgorithmException ee) {
         throw new IOException("No MD5 Algo found");
      } finally {
         if (fis != null) try { fis.close(); } catch(Exception ee) {}
      }
      
      
      return ret;
   }
   
	private static boolean matchrecurs(String s, int sidx, String splats[], int idx, int num) {
		// If we are past the end, then we are golden
		if (idx >= num && sidx >= s.length())
			return true;

		// If we are past the end, but not finished ... Bzzz
		if (idx >= num || sidx >= s.length()) {
			return false;
		}

		// If this is a splat, work on next item
		if (splats[idx].equals("*")) {
			if (++idx >= num)
				return true;

			while((sidx = s.indexOf(splats[idx],sidx)) >= 0) {
				if (matchrecurs(s, sidx, splats, idx, num)) {
					return true;
				}
				sidx++;
			}
		} else if (splats[idx].equals("?")) {
			sidx++;
			return matchrecurs(s, sidx, splats, idx+1, num);
		} else {
			// Must match in current position
			if (s.indexOf(splats[idx], sidx) == sidx) {
				return matchrecurs(s, sidx + splats[idx].length(), splats, idx+1, num);
			}
		}

		return false;
	}

	public static String[] compilePattern(String pattern) {
		int ret = 0;
		String splats[] = new String[128];
		int preidx = 0;
		int splatsidx = 0;

		// Build splats array (contains null for WILD, and string for FIXED
		while(preidx < pattern.length()) {
			try {
				int idxsplat = pattern.indexOf("*", preidx);
				int idxquest = pattern.indexOf("?", preidx);
				if (idxsplat >= 0 || idxquest >= 0) {

					if (idxquest < 0 || (idxsplat >=0 && idxsplat < idxquest)) {
						if (idxsplat == preidx) {
							if (splatsidx == 0 || !splats[splatsidx-1].equals("*")) {
								splats[splatsidx++] = "*";
							}
						} else {
							splats[splatsidx++] = pattern.substring(preidx, idxsplat);
							splats[splatsidx++] = "*";
						}

						preidx = idxsplat+1;
					} else {
						if (idxquest == preidx) {
							splats[splatsidx++] = "?";
						} else {
							splats[splatsidx++] = pattern.substring(preidx, idxquest);
							splats[splatsidx++] = "?";
						}

						preidx = idxquest+1;
					}
				} else {
					//	 		 Take rest of chars
					splats[splatsidx++] = pattern.substring(preidx);
					break;
				}
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				break;
			} catch(IndexOutOfBoundsException ioobe) {
				break;
			}
		}

		String retsplats[] = new String[splatsidx];
		System.arraycopy(splats, 0, retsplats, 0, splatsidx);
		return retsplats;
	}

	public static boolean matches(String compiledPattern[], String s) {
		return matchrecurs(s, 0, compiledPattern, 0, compiledPattern.length);
	}
}
