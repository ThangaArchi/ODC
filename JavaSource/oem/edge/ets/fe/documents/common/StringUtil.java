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

package oem.edge.ets.fe.documents.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provide common String Utilities
 * @author v2srikau
 */
public class StringUtil {

	public static final String SELECTED = "SELECTED";
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	public static final String EMPTY_STRING = "";
	public static final String SPACE = " ";
	public static final String COMMA = ",";
	public static final String FLAG_TRUE = "true";
	public static final String FLAG_FALSE = "false";

	/**
	 * Check whether the Input String is Null OR an Empty String
	 * @param strInput Input String to be checked
	 * @return Null or Empty status flag
	 */
	public static boolean isNullorEmpty(String strInput) {
		boolean bIsNullOrEmpty =
			((strInput == null)
				|| (strInput.trim().length() == 0)
				|| strInput.equalsIgnoreCase("NULL"));
		return bIsNullOrEmpty;
	}


	/**
	 * Check whether the Input String is Null OR an Empty String
	 * @param strInput Input String to be checked
	 * @return Null or Empty status flag
	 */
	public static boolean isNullorEmpty(String[] strArrayInput) {
		boolean bIsNullOrEmpty = ( (strArrayInput == null) || (strArrayInput.length == 0) ); // || strArrayInput.equalsIgnoreCase("NULL"));
		return bIsNullOrEmpty;
	}

	/**
	 * @param strInput
	 * @return
	 */
	public static String trim(String strInput) {
		if (isNullorEmpty(strInput)) {
			return EMPTY_STRING;
		}
		return strInput.trim();
	}

	/**
	 * @param dtDate
	 * @return
	 */
	public static String formatDate(Date dtDate) {
		String strFormattedDate = null;
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		strFormattedDate = df.format(dtDate);
		return strFormattedDate;
	}

	/**
	 * @param dtDate
	 * @return
	 */
	public static String formatMediumDate(Date dtDate) {
		String strFormattedDate = null;
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		strFormattedDate = df.format(dtDate);
		return strFormattedDate;
	}

	/**
	 * @param lDate
	 * @return
	 */
	public static String formatMediumDate(long lDate) {
		String strFormattedDate = null;
		Date dtDate = new Date(lDate);
		return formatMediumDate(dtDate);
	}

	/**
	 * This method wraps the String and pads that string with spaces.
	 * Creation date: (10/28/01 4:35:24 PM)
	 * @return java.lang.String
	 * @param sInString java.lang.String
	 * @exception java.lang.Exception The exception description.
	 */
	public static String formatEmailStr(String sInString) throws java.lang.Exception {

		if (sInString == null || sInString.length() == 0) {
			return "";
		}
		StringBuffer sOut = new StringBuffer();
		boolean bBreakFlag = false;
		boolean bNewLine = false;

		try {

			byte sTemp[] = sInString.getBytes();

			for (int i = 0; i < sTemp.length; i++) {
				if (sTemp[i] == (byte) '\n' || sTemp[i] == (byte) '\r') {
					sTemp[i] = (byte) ' ';
				}
			}

			sInString = new String(sTemp);

			if (sInString.length() > 48) {
				for (int i = 0; i < sInString.length(); i++) {
					if (i % 40 == 0) {
						if (i > 39) {
							bBreakFlag = true;
						}
					}
					if (bBreakFlag) {
						if (sInString.substring(i, i + 1).equals(",") || sInString.substring(i, i + 1).equals(" ") || sInString.substring(i, i + 1).equals(";")) {
							sOut.append(sInString.substring(i, i + 1));
							sOut.append("\n                  ");
							bBreakFlag = false;
							bNewLine = true;
						} else {
							sOut.append(sInString.substring(i, i + 1));
						}
					} else {
						if (bNewLine) {
							sOut.append(sInString.substring(i, i + 1).trim());
							bNewLine = false;
						} else {
							sOut.append(sInString.substring(i, i + 1));
						}
					}
				}
			} else {
				sOut.append(sInString);
			}

		} catch (Exception e) {
			sOut.setLength(0);
			sOut.append(sInString);
		} finally {
			
		}
			return sOut.toString();

	}
	
	/**
	 * @param strInput
	 * @return
	 */
	public static String convertLBtoBR(String strInput) {
	    StringBuffer strBuffer = new StringBuffer(EMPTY_STRING);
	    if (isNullorEmpty(strInput)) {
	        return strInput;
	    }
	    else {
	        char ch = 0;
	        for(int i=0; i < strInput.length(); i++) {
	            if ((ch = strInput.charAt(i)) == '\n') {
	                strBuffer.append("<br />");
	            }
	            else {
	                strBuffer.append(ch);
	            }
	        }
	    }
	    return strBuffer.toString();
	}
	
	/**
	 * @param strInput
	 * @return
	 */
	public static String removeLineBreaks(String strInput) {
	    StringBuffer strBuffer = new StringBuffer(EMPTY_STRING);
	    if (isNullorEmpty(strInput)) {
	        return strInput;
	    }
	    else {
	        char ch = 0;
	        for(int i=0; i < strInput.length(); i++) {
	            if ((ch = strInput.charAt(i)) == '\n') {
	            }
	            else {
	                strBuffer.append(ch);
	            }
	        }
	    }
	    return strBuffer.toString();
	}
}