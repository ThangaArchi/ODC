package oem.edge.ed.odc.remoteviewer;
import oem.edge.ed.util.*;
import java.util.*;
public class Base64DecoderForPubApp {

   public static void main(java.lang.String[] args) {
      Base64Decoder bd = new Base64Decoder();
	String test = null;	
   try {
 	int tobeRead = System.in.available();
 	byte[] buf = new byte[tobeRead];
 	System.in.read(buf);
 	test = new String(buf);
	}
 catch (Exception e) {
	 e.printStackTrace();
	} 

 
      if (test == null) {
         System.out.println("<============== Base64Decode ==================>\n");
         System.out.println("Usage : Base64Decoder <string>");
      } else {
         String decoded = bd.decode(test);
         System.out.println(decoded);
      } 
      return;
   }
   
}
