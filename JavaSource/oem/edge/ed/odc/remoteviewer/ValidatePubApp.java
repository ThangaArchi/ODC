package oem.edge.ed.odc.remoteviewer;

import oem.edge.common.cipher.*;
import oem.edge.common.RSA.*;
/**
 * Insert the type's description here.
 * Creation date: (4/10/2003 8:29:22 PM)
 * @author: Administrator
 */
public class ValidatePubApp {
/**
 * ValidatePubApp constructor comment.
 */
public ValidatePubApp() {
	super();
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
   public static void main(java.lang.String[] args) {
     // Insert code to start the application here.
      String decodeFilename = null;
      String token = null;
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("decodekey")) 
            decodeFilename = args[++i];
         else if (args[i].equalsIgnoreCase("token")) 
            token = args[++i];
         else {
            System.out.println("Error@Options: decodekey      [filename]\n" +
                               "         token  [token]\n");
            return;
         }
      }

      ODCipherRSA  lcipher = null;
      try {
         lcipher = new ODCipherRSASimple(decodeFilename); 
      } catch(Throwable t) {
         t.printStackTrace();
         System.out.println("Error@ loading CipherFile! [" + decodeFilename + "]");
      }
      
      if (lcipher==null)
         return;
        
      boolean valid = false;
         
      ODCipherData cd = null;
      try{
         cd = lcipher.decode(token);
           
         if (cd.isCurrent()) {
            valid = true;
         }
      } catch(Exception e) {}
      if(!valid)
         System.out.println("Error@token is invalid");
      else
         System.out.println(cd.getString());
        
   }
}
