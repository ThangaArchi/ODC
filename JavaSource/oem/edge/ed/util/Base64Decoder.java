package oem.edge.ed.util;

import java.util.*;
//import com.ibm.xml.dsig.*;
//import org.log4j.*;

/**
 *
 * The Base64Decoder is a support class used to decode a string stored in
 * Base64 to a normal character string. It is specifically used in IBMLink2000
 * for passwords stored in config files.
 *
 * This class uses the base64 decoder code that comes with the XML Security Suite
 * package xss4j obtained from alphaWorks, url is http://monet.trl.ibm.com/xss4j/
 *
 * @author Phyllis Leung
 *
 */
public class Base64Decoder {

	/**
	 * Properties file handle
	 */
	static PropertyResourceBundle propBundle = null;
	/**
	 	 * Tracer handle
	 	 */
	//static Category cat = Category.getInstance(Base64Decoder.class.getName());
	/**
	 * Tracer configuration file name
	 */
	static String traceConfigFile = null;
	/*
	static { 
	    try {
	    // Get the generic information from LinkBase properties file
	        propBundle = (PropertyResourceBundle) ResourceBundle.getBundle("LinkBase");
	    	traceConfigFile = propBundle.getString("TRACE_CONFIG_FILE");
	    	
	    	if(traceConfigFile == null) {
	    	  System.out.println("trace config file cannot found in properties file");
	}
	    	else
	      PropertyConfigurator.configure(traceConfigFile);
	} catch (MissingResourceException me) {
	    	System.out.println("trace config file cannot found in properties file"+ me);
	 	    }
	}
	*/
/**
 * Base64Decoder constructor comment.
 */
public Base64Decoder() {
	super();
}
/**
 * Decodes a java String that is encoded with Base64 algorithm
 *
 * @return java.lang.String
 */
   public String decode(String encodedPassword) {
      
     //cat.info("Entering decode()");
      if (encodedPassword == null) {
        //cat.error("The input encoded password is null! Cannot proceed");
         return null;
      }
      
     //cat.info("Entering Base64Decoder.decode()");
      
      String decodedPassword = null;
      byte [] passwordInBytes = new byte [4096];
      passwordInBytes = Base64.decode(encodedPassword);
      int passwordLength = passwordInBytes.length;
     //cat.debug("Password in bytes length is : " + passwordLength);
      
      char [] passwordInChars = new char [passwordLength];
      
      for (int i=0; i<passwordLength; i++) {
         passwordInChars[i] = (char) passwordInBytes[i];
        //cat.debug("passwordInBytes["+ i +"] = " + passwordInBytes[i] + ", passwordInChars["+ i +"] = " + passwordInChars[i]);
      }
      
     //cat.info("Exiting decode()");
      
      return decodedPassword.valueOf(passwordInChars);
   }
   
  // JMC 4/18/02 - Needed this
   public static void main(java.lang.String[] args) {
      Base64Decoder bd = new Base64Decoder();
      
      if (args.length != 1 || args[0].equals("-?")) {
         System.out.println("<============== Base64Decode ==================>\n");
         System.out.println("Usage : Base64Decoder <string>");
      } else {
         String orig = args[0];
         System.out.println("<============== Base64 Utility Decoder ================>\n");
         String decoded = bd.decode(args[0]);
         System.out.println("The Decoded [value] : [" + decoded + "]");
      } 
      return;
   }
   
}
