package oem.edge.ed.util;

import java.io.*;
import java.util.*;
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
//import com.ibm.xml.dsig.Base64.*;
//import ibmlink.link2.support.Base64Decoder;
//import ibmlink.link2.support.Base64Encoder;

/**
 * Base64Utility is a utility to encode and/or decode a string
 * one way or another.
 *
 * 
 */
public class PasswordUtils {
   static private String encoded_password = null;
   static private String decoded_password = null;
   static private Base64Decoder tb = new Base64Decoder();
   static private Base64Encoder be = new Base64Encoder();
/**
 * TestBase64 constructor comment.
 */
   public PasswordUtils() {
      super();
   }
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 11:10:28 AM)
 * @return java.lang.String
 */

   public static String getPassword(String fileseg) {

      String pwfile = fileseg;
      
      if (!((new File(fileseg)).isAbsolute())) {
         if (fileseg.equals("HOME")) {
            pwfile = System.getProperty("user.home");
         } else {
            pwfile = System.getProperty("user.home") + '/' + fileseg;
         }
      }     
      
      String s = null;
      String decoded_password = null;
      try {
        
         String tlocal = pwfile + "/...";
         File tfile = new File(tlocal);
           
        // Windows does NOT like a file called '...' sign, so support it
         if (!tfile.exists() || !tfile.isFile()) {
            tlocal = pwfile + "/.dotdot";
         }
           
         FileInputStream in = new FileInputStream(tlocal);
         int n;
         while ((n = in.available()) > 0) {
            byte[] b = new byte[n];
            int result = in.read(b);
            if (result == -1)
               break;
            s = new String(b);
         }
         decoded_password = tb.decode(s);
         in.close();
      } catch (IOException e) {
         System.out.println(e);
         e.printStackTrace(System.out);
      }
      return decoded_password;
   }
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 11:45:31 AM)
 * @param param java.lang.String
 */
   public static void putPassword(String fileseg, String text) {
      String pwfile = null;
      encoded_password = be.encode(text);
      if (fileseg.substring(0, 1).equals("/")) {
         pwfile = fileseg + "/...";
      }
      if (fileseg.equals("HOME")) {
         pwfile = System.getProperty("user.home") + "/...";
      }
      if (!(fileseg.substring(0, 1).equals("/")) && !(fileseg.equals("HOME"))) {
         pwfile = System.getProperty("user.home") + "/" + fileseg + "/...";
      }
      try {
         PrintWriter out = new PrintWriter(new FileOutputStream(pwfile));
         out.println(encoded_password);
         out.close();
      } catch (IOException e) {
         System.out.println(e);
         e.printStackTrace(System.out);
      }
   }
}
