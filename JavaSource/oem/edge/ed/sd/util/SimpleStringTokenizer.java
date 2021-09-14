package oem.edge.ed.sd.util;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
import java.lang.String;
import java.util.Vector;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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


public class SimpleStringTokenizer {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
  boolean countEmptyTokens;
  char    delim;
  int     currentToken;
  String  string;
  Vector  tokens;
		
		
  public SimpleStringTokenizer (String string, char delim, 
				boolean countEmptyTokens) {
    init(string, delim, countEmptyTokens);
  }


  public SimpleStringTokenizer (String string, char delim) {
    init(string, delim, true);
  }

  public void init (String string, char delim, boolean countEmptyTokens) {
    this.string = string;
    this.delim = delim;
    this.countEmptyTokens = countEmptyTokens;
    this.tokens = new Vector();
    setTokens();
  }

  public Vector getTokens () {
    return tokens;
  }
		
  public void setTokens () {
    int start, end, len = string.length();
    start = 0; end = 0;
    currentToken = 0;
    tokens.removeAllElements();
			
    while (start < len) {
      end = string.indexOf(delim, end);

      if (end < 0) {
	end = len;
      }

      if ( !countEmptyTokens && 
	   end == start) {
	start = ++end;
	continue;
      }

      tokens.addElement((String) string.substring(start, end));
      start = ++end;
    }
  }

  public int countTokens () {
    return tokens.size();
  }
		
		
  public boolean hasMoreTokens () {
    return (currentToken < tokens.size());
  }
		
		
  public String nextToken () {
    String retVal;
    if (hasMoreTokens())
      retVal = (String) tokens.elementAt(currentToken++);
    else 
      throw new java.util.NoSuchElementException("No More Tokens");
			 
    return retVal;
  }
}
