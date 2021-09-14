package oem.edge.ets_pmo.util;
import javax.swing.text.rtf.*;
import java.io.*;
import java.util.*;
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

public class RtfTest2002 {
	private static String CLASS_VERSION = "4.5.1";
public static void main(String agrv[]){

FileReader reader = null;
RtfFilterReader inDocument = null;
try{
reader = new FileReader(agrv[0]);
String str = readFromString(reader);
StringReader strReader = new StringReader(str);

//inDocument = new RtfFilterReader( reader );
inDocument = new RtfFilterReader( strReader );

}
catch(FileNotFoundException fnfe){
fnfe.printStackTrace();
}
catch(IOException ioe){
ioe.printStackTrace();
}
javax.swing.text.DefaultStyledDocument doc = new javax.swing.text.DefaultStyledDocument();
try{
new javax.swing.text.rtf.RTFEditorKit().read(inDocument,doc,0);  // (reader,doc,0);
System.out.println(doc.getText(0,doc.getLength()));
}
catch(javax.swing.text.BadLocationException ble){
ble.printStackTrace();
}
catch(IOException ioe){
ioe.printStackTrace();
}


}

public static String readFromString(FileReader in) throws IOException{
	StringBuffer strBuf = new StringBuffer();
	int numchars;
	final char[] tmpbuf = new char[2048];

// read the whole document into StringBuffer

do {
numchars = in.read( tmpbuf, 0, tmpbuf.length );

if ( numchars != -1 ) {
strBuf.append( tmpbuf, 0, numchars );
}

} while ( numchars != -1 );
	
return strBuf.toString();
}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
