/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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



package oem.edge.ets.fe;

public class HTMLString {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";


	public static final String VERSION = "1.3";
	private String theString;
	public HTMLString(String aString) { this.theString = aString; }
	public String toString() {
    	if (theString==null) theString="";
    	StringBuffer b =new StringBuffer();
    	for (int i=0, j=theString.length();i<j;i++) {
        	String s=theString.substring(i,i+1);
             	if (s.equals("\"")) b.append("&quot;");
        	else if (s.equals("<"))  b.append("&lt;");
        	else if (s.equals(">"))  b.append("&gt;");
        	else if (s.equals("&"))  b.append("&amp;");
        	else                     b.append(s);
    	}
    	return (b.toString());
  	}
  	public static HTMLString getHTML(String s) {
  		return new HTMLString(s);
  	}


}

