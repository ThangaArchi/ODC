
package oem.edge.ets_pmo.util;

import java.util.*;

import oem.edge.ets_pmo.util.Base64;
//import com.ibm.xml.dsig.*;
//import org.log4j.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
	private static String CLASS_VERSION = "4.5.1";
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
     
      if (encodedPassword == null) {
          return null;
      }
      
      String decodedPassword = null;
      byte [] passwordInBytes = new byte [4096];

      passwordInBytes = Base64.decode(encodedPassword);
      int passwordLength = passwordInBytes.length;
     
      System.out.println(passwordLength);
      char [] passwordInChars = new char [passwordLength];
      
      for (int i=0; i<passwordLength; i++) {
         passwordInChars[i] = (char) passwordInBytes[i];
      }
      return decodedPassword.valueOf(passwordInChars);
   }
  public byte[] decodeInBytes(String encodedPassword){
  	if(encodedPassword == null) return null;
  	return Base64.decode(encodedPassword);
  }
    // JMC 4/18/02 - Needed this
   public static void main(java.lang.String[] args) {
      Base64Decoder bd = new Base64Decoder();
	//String orig = "e1xydGYxXGFuc2kKe1xmb250dGJsXGYwXGZuaWwgTW9ub3NwYWNlZDt9CgpNYXhpbXVtIFNlY3VyaXR5IG5lZWRlZFxwYXIKfQo=";
	String orig = "e1xydGYxXGFuc2kKe1xmb250dGJsXGYwXGZuaWwgTW9ub3NwYWNlZDt9CgotLS0tLS0tLS0tLS0gQ29tbWVudHMgZW50ZXJlZCBieSBTdWJyYW1hbmlhblN1bmRhcmFtIFtJQk0gSUQ6IHN1YnVAdXMuaWJtLmNvbV0gb24gSmFuIDIwLCAyMDA1IC0tLS0tLS0tLS0tLS0tXHBhcgpQcmVzaWRlbnQgQnVzaCwgYXJyaXZpbmcgdW5kZXIgdW5wcmVjZWRlbnRlZCBzZWN1cml0eSBmb3IgaGlzIHN3ZWFyaW5nIGluIGNlcmVtb255IFRodXJzZGF5LCB3YXMgZXhwZWN0ZWQgdG8gY2FsbCBmb3IgInRoZSBleHBhbnNpb24gb2YgZnJlZWRvbSBpbiBhbGwgdGhlIHdvcmxkIiBkdXJpbmcgaGlzIGluYXVndXJhbCBhZGRyZXNzLlxwYXIKXHBhcgpDcm93ZHMgY2hlZXJlZCBhcyBCdXNoIHRvb2sgaGlzIHBsYWNlIGF0IHRoZSB3ZXN0IGZyb250IG9mIHRoZSBDYXBpdG9sLCBhbWlkIHRvbmVzIG9mICJIYWlsIFRvIFRoZSBDaGllZi4iXHBhcgpccGFyClJlcHVibGljYW4gU2VuLiBUcmVudCBMb3R0IG9mIE1pc3Npc3NpcHBpIG9wZW5lZCB0aGUgY2VyZW1vbnksIHNheWluZyBpdCB3YXMgYSAidGltZSB3aGVuIGFsbCBBbWVyaWNhbnMgY2FuIHVuaXRlLiJccGFyClxwYXIKQnVzaCB3aWxsIGJlIHN3b3JuIGluIGJ5IENoaWVmIEp1c3RpY2UgV2lsbGlhbSBSZWhucXVpc3QsIHdobyB3aWxsIGJlIG1ha2luZyBoaXMgZmlyc3Qgb2ZmaWNpYWwgYXBwZWFyYW5jZSBzaW5jZSBiZWdpbm5pbmcgdHJlYXRtZW50IGZvci!B0aHlyb2lkIGNhbmNlciBpbiBPY3RvYmVyLiAoUmVobnF1aXN0IHJlYWR5KVxwYXIKXHBhcgpFYXJsaWVyIGluIHRoZSBkYXksIEJ1c2ggYXR0ZW5kZWQgY2h1cmNoIHNlcnZpY2VzIHdpdGggaGlzIHdpZmUsIExhdXJhLCBhbmQgdGhlaXIgdHdpbiBkYXVnaHRlcnMsIEplbm5hIGFuZCBCYXJiYXJhLCBhdCBTdC4gSm9obidzIEVwaXNjb3BhbCBDaHVyY2guIFRoZSBSZXYuIEx1aXMgTGVvbiBkZWxpdmVyZWQgYSAxNS1taW51dGUgaG9taWx5LCBzYWlkIGNodXJjaCBkaXJlY3RvciBIYXlkZW4gQnJ5YW4uXHBhcgp9Cg==";
	//String orig ="e1xydGYxXGFuc2kKe1xmb250dGJsXGYwXGZuaWwgTW9ub3NwYWNlZDt9CgoiYXNkZmFzZGZhc2ZkIiAiYXNkZmFzZmQiXHBhcgp9Cg=="; 
  /*  String orig = "UEsDBBQACAAIANxgbDEAAAAAAAAAAAAAAAAKAAAAa2lsbGVkLmxzdAvPzEvJLy9W8AtRMNUzVNBw" + 
							"Ks3MSVEwMjMw0OTlCijKT04tLlawcfbxcAwwNtJzjXC1U8jLL1FIyy/NS+HlAgBQSwcIA1bkIkAA" + 
							"AAA+AAAAUEsBAhQAFAAIAAgA3GBsMQNW5CJAAAAAPgAAAAoAAAAAAAAAAAAAAAAAAAAAAGtpbGxl" + 
							"ZC5sc3RQSwUGAAAAAAEAAQA4AAAAeAAAAAAA" ;
							*/ 
           System.out.println("<============== Base64 Utility Decoder ================>\n");
         String decoded = bd.decode(orig);
         System.out.println("The Decoded [value] : [" + decoded + "]");
     /* if (args.length != 1 || args[0].equals("-?")) {
         System.out.println("<============== Base64Decode ==================>\n");
         System.out.println("Usage : Base64Decoder <string>");
      } else {
         String orig = args[0];
         System.out.println("<============== Base64 Utility Decoder ================>\n");
         String decoded = bd.decode(args[0]);
         System.out.println("The Decoded [value] : [" + decoded + "]");
      } */
      return;
   }
   
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
