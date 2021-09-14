package oem.edge.ed.odc.remoteviewer;
import oem.edge.ed.util.*;
import java.util.*;
public class Base64EncoderForPubApp {
   public static void main(java.lang.String[] args) {
      Base64Encoder be = new Base64Encoder();
      
      if (args.length != 1 || args[0].equals("-?")) {
         System.out.println("<============== Base64Encode ==================>\n");
         System.out.println("Usage : Base64Encoder <string>");
      } else {
         String orig = args[0];
         String encoded = be.encode(args[0]);
         System.out.println(encoded);
      } 
      return;
   }
}
