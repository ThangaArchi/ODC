package oem.edge.ets_pmo.util;

import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.io.*;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TextToRTFConverter {
	private static String CLASS_VERSION = "4.5.1";
static Logger logger = Logger.getLogger(TextToRTFConverter.class);

	public static void main(String[] args) {
		
			TextToRTFConverter conv =  new TextToRTFConverter();
			Base64Encoder b = new Base64Encoder();
			System.out.println("RTF value : " + conv.convertTextToRTF("Hello how are you"));
			System.out.println("Encoded value : " + b.encode(conv.convertTextToRTF("Hello how are you")));
		
	}
	
	public String convertTextToRTF(String str) {
		ByteArrayOutputStream bouts = null;
		
		try{
			  RTFEditorKit kit = new RTFEditorKit();

              Document doc = kit.createDefaultDocument();

              bouts = new ByteArrayOutputStream();

              doc.insertString(0, str, null);

              kit.write(bouts, doc, 0, doc.getLength());
		}
		catch(BadLocationException bad){ 
					logger.debug("BadLocation exception caused while coverting the following text to rtf : " + str);
			 } 
		catch(IOException ioe){ 
					logger.debug("IOException caused while coverting the following text to rtf : " + str);
				}
				
			return bouts.toString();	
		
	}
	
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
