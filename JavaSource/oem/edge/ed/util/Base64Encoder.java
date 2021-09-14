package oem.edge.ed.util;

import java.util.*;
//import com.ibm.xml.dsig.*;
//import org.log4j.*;

/**
 *
 * The Base64Encoder is a support class used to encode a noraml character string 
 * to Base64. It is specifically used in IBMLink2000 for passwords stored in config
 * files so passwords will not be stored in the clear.
 *
 * This class uses the base64 encoder code that comes with the XML Security Suite
 * package xss4j obtained from alphaWorks, url is http://monet.trl.ibm.com/xss4j/
 *
 * @author Phyllis Leung
 *
 */
public class Base64Encoder {

	/**
	 * Properties file handle
	 */
	static PropertyResourceBundle propBundle = null;
	/**
	 * Tracer handle
	 */
	//static Category cat = Category.getInstance(Base64Encoder.class.getName());
	/**
	 * Tracer configuration filename
	 */
	static String traceConfigFile = null;

	/*
	static { 
	    try {
	    // Get the properties information from LinkBase properties
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
 * Base64Encoder constructor comment.
 */
public Base64Encoder() {
	super();
}
/**
 * Encodes a java String using Base64 algorithm and returns the
 * encoded string to the caller
 *
 * @param pw java.lang.String
 * @param encoded_pw java.lang.String
 */
   public String encode(String password) {
      
     //cat.info("Entering encode()");
      
      if (password == null) {
        //cat.error("The input password is null! Cannot proceed");
         return null;
      }
      
     //cat.info("Entering Base64Encoder.encode()");
      
      String encodedPassword = null;
      
      int passwordLength = password.length();
      char [] passwordInCharArray = password.toCharArray();
      
      byte [] passwordInBytes = new byte[passwordLength];
      
      for (int i=0; i<passwordLength; i++) {
         passwordInBytes [i] = (byte) passwordInCharArray[i];
        //cat.debug("password["+ i + "] = "+passwordInCharArray[i]+", passwordInBytes[" + i + "] = "+passwordInBytes[i]);
      }
      
      encodedPassword = Base64.encode(passwordInBytes);
      
     //cat.debug("Encoded password is : " + encodedPassword);
     //cat.info("Exiting encode()");
      
      return encodedPassword;
   }
   
  // JMC 4/18/02 - Needed this
   public static void main(java.lang.String[] args) {
      Base64Encoder be = new Base64Encoder();
      
      if (args.length != 1 || args[0].equals("-?")) {
         System.out.println("<============== Base64Encode ==================>\n");
         System.out.println("Usage : Base64Encoder <string>");
      } else {
         String orig = args[0];
         System.out.println("<============== Base64 Utility Encoder ================>\n");
         String encoded = be.encode(args[0]);
         System.out.println("The encoded [value] : [" + encoded + "]");
      } 
      return;
   }
}
