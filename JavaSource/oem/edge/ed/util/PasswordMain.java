package oem.edge.ed.util;

/**
 * Insert the type's description here.
 * Creation date: (9/27/00 1:29:00 PM)
 * @author: Administrator
 */
//import com.ibm.xml.dsig.Base64.*;
//import ibmlink.link2.support.Base64Decoder;
//import ibmlink.link2.support.Base64Encoder;
class PasswordMain {
	static private String encoded_password = null;
	static private String decoded_password = null;
	static private Base64Decoder tb = new Base64Decoder();
	static private Base64Encoder be = new Base64Encoder();
/**
 * PasswordMain constructor comment.
 */
public PasswordMain() {
	super();
}
/**
 * This is the main method to invoke the base64 methods to
 * encode or decode a string
 *
 * @param args java.lang.String[] : an array of command-line arguments
 * 
 */
public static void main(java.lang.String[] args) {
	Base64Decoder tb = new Base64Decoder();
	Base64Encoder be = new Base64Encoder();
	PasswordUtils handle = new PasswordUtils();	
		
	if (args.length < 1) {
		System.out.println("<============== Base64 Utility Error ==================>\n");
		System.out.println("Error! Base64 utility expects 2 parameters");
		System.out.println("Syntax : base64util <encode/decode> <string>");
	} else {
		if (args[0].compareTo("encode") == 0) {
			String orig_password = args[2];
			//encoded_password = be.encode(orig_password);
			System.out.println("<============== Base64 Utility Encoder ================>\n");
			//System.out.println("The encoded password is : " + encoded_password);
			handle.putPassword(args[1],orig_password);
		} else
			if (args[0].compareTo("decode") == 0) {
				String encoded_password = args[1];
				//decoded_password = tb.decode(encoded_password);
				System.out.println("<============== Base64 Utility Decoder ================>\n");
				//System.out.println("The decoded password is : " + decoded_password);
				String pw = handle.getPassword(args[1]);
				System.out.println("Returned PW =   " + pw);
				
			} else {
				System.out.println("<============== Base64 Utility Error ==================>\n");
				System.out.println("Unknown keyword : " + args[0]);
				System.out.println("Syntax : base64util <encode/decode> <string>");
			}
	}
	System.out.println("<====================== End ===========================>\n");
	return;
}






	
	
}
