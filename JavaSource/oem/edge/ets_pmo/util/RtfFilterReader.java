package oem.edge.ets_pmo.util;
import java.io.*;
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
* The workaround is to filter out a section of the RFT document
* which causes an exception during parsing it in MS Word 2002. 
* java.lang.NullPointerException: 
* at javax.swing.text.rtf.RTFReader$StylesheetDestination$StyleDefiningDestination.close(RTFReader.java:924)
* at javax.swing.text.rtf.RTFReader.setRTFDestination(RTFReader.java:254)
* This section has no
* impact on the display of the document, it just contains some
* meta information used by MS Word 2002.
*/

public class RtfFilterReader extends FilterReader {
	private static String CLASS_VERSION = "4.5.1";
/** Search string for start of the section. */
private static final String SEC_START = "{\\stylesheet";  /*    "{\\*\\ts"    */

/** Search string for end of the section. */
private static final String SEC_END = "}}";                        /*     "}"           */

/** Locale store for the document data. */
private final StringBuffer strBuf = new StringBuffer();

/**
* Wrapper for the input stream used by the RTF parser.<br>
* Here the complete document will be loaded into a string buffer
* and the section causes the problems will be deleted.<br>
* <br>
*
* @param in Stream reader for the document (e.g. {@link FileReader}).
*
* @throws IOException in case of I/O errors during document loading.
*/
public RtfFilterReader( final Reader in ) throws IOException {

super( in );

int numchars;
final char[] tmpbuf = new char[2048];

// read the whole document into StringBuffer

do {
numchars = in.read( tmpbuf, 0, tmpbuf.length );

if ( numchars != -1 ) {
strBuf.append( tmpbuf, 0, numchars );
}

} while ( numchars != -1 );

// finally delete the problem making section
deleteStylesheet();
}

/**
* Deletion of the prblematic section.
*
*/
private void deleteStylesheet() {

// find start of the section
String str = strBuf.toString();
final int start = str.indexOf( SEC_START );

if ( start == -1 ) {
// section not contained, so just return ...
return;
}

// find end of section
final int end = str.indexOf( SEC_END, start );

// delete section
strBuf.delete( start, end + 2 );
}


/**
* Read characters into a portion of an array.<br>
* The data given back will be provided from local StringBuffer
* which contains the whole document.
*
* @param buf Destination buffer.
* @param off Offset at which to start storing characters -
* <srong>NOT RECOGNIZED HERE.</strong>.
* @param len Maximum number of characters to read.
*
* @return The number of characters read, or -1 if the end of the
* stream has been reached
*
* @exception IOException If an I/O error occurs
*/
public int read( final char[] buf, final int off, final int len ) throws IOException {

if ( strBuf.length() == 0 ) {
// if buffer is empty end of document is reached
return -1;
}

// fill destination array

int byteCount = 0;
for (; byteCount < len; byteCount++) {

if ( byteCount == strBuf.length() ) {
// end reached, stop filling
break;
}

// copy data to destination array
buf[byteCount] = strBuf.charAt( byteCount );
}

// delete to copied data from local store
strBuf.delete( 0, byteCount );

return byteCount;
}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
