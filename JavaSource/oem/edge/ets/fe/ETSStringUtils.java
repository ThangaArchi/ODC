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

/**
 * A class for various string utilities.
 * Creation date: (5/5/2002 10:59:04 PM)
 * @author: Sacheendra Rao [v2sach@us.ibm.com]
 */

import java.math.BigDecimal;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

public class ETSStringUtils {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";


	private static final String CQ_CLASS_VERSION = "3.4.1";
	private static final SimpleDateFormat timestampFormat =
		new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	/**
	 * StringUtils constructor comment.
	 */
	public ETSStringUtils() {
		super();
	}
	/**
	 * Converts ',",\,NUL into sql equivalents \',\",\\,\NUL from a source
	 * string and returns it.
	 * (new line to break)
	 * Creation date: (5/6/2002 11:15:35 PM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 */

	public static String addSlashes(String source) {
		if (source == null) {
			return "";
		}
		StringBuffer dest = new StringBuffer(source.length());
		for (int i = 0; i < source.length(); i++) {
			char c;
			c = source.charAt(i);
			if (c == '"') {
				dest.append("\\\"");
			} else if (c == '\'') {
				dest.append("\\\'");
			} else if (c == '\\') {
				dest.append("\\\\");
			} else if (
				(c == 'N')
					&& ((i + 2) < source.length())
					&& (source.charAt(i + 1) == 'U')
					&& (source.charAt(i + 2) == 'L')) {
				dest.append("\\N");
			} else {
				dest.append(c);
			}
		}
		return dest.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/25/2002 9:48:46 AM)
	 * @return java.lang.String
	 * @param ref java.lang.String
	 * @param text java.lang.String
	 */
	public String ahref(String ref, String text) {
		return "<a href=\"" + ref + "\">" + text + "</a>";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/25/2002 9:52:11 AM)
	 * @return java.lang.String
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param ref java.lang.String
	 * @param text java.lang.String
	 */
	public String ahref(
		javax.servlet.http.HttpServletRequest req,
		String ref,
		String text) {
		return "<a href=\""
			+ req.getContextPath()
			+ "/"
			+ ref
			+ "\">"
			+ text
			+ "</a>";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/9/2002 12:21:24 AM)
	 * @return java.util.List
	 * @param str java.lang.String[]
	 */
	public static List arr2List(String[] str) {

		ArrayList aList = new ArrayList();

		for (int i = 0; i < str.length; i++) {
			aList.add(str[i]);
		}
		return aList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/2002 12:50:29 AM)
	 * @return java.lang.String
	 * @param b boolean
	 */
	public static String booleanToString(boolean b) {
		if (b)
			return "y";
		else
			return "n";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/24/2002 3:48:43 PM)
	 * @return java.util.Collection
	 * @param aList java.util.List
	 */
	public static Collection checkDuplicateList(List aList) {

		ArrayList newList = new ArrayList();

		for (Iterator i = aList.iterator(); i.hasNext();) {
			String tmp = (String) i.next();
			tmp = ETSStringUtils.trim(tmp);
			if (!newList.contains(tmp)) {
				newList.add(tmp);
			}
		}
		return newList;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:44:41 AM)
	 * @return java.lang.String
	 * @param date java.util.Date
	 */
	public static String dateToString(Date date) {
		return (DateFormat.getDateInstance().format(date));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:42:40 AM)
	 * @return java.lang.String
	 * @param date java.util.Date
	 */
	public static String dateToTimestamp(Date date) {
		if (date == null) {
			return (null);
		} else {
			return (timestampFormat.format(date));
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 11:23:49 AM)
	 * @return double
	 * @param number java.lang.String
	 */
	public static String displaySize(String number) {

		double displaySize = 0;
		String displayBytes = "bytes";
		StringBuffer sb = new StringBuffer();

		double size = Double.valueOf(number).doubleValue();

		if (size >= 1024 && size < (1024 * 1024)) {
			displaySize = size / 1024;
			displayBytes = " KB";
		} else if (size >= (1024 * 1024)) {
			displaySize = size / (1024 * 1024);
			displayBytes = " MB";
		} else {
			displaySize = size;
			displayBytes = " bytes";
		}

		java.math.BigDecimal bd = new java.math.BigDecimal(displaySize);
		bd = bd.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
		double dno = bd.doubleValue();

		sb.append(dno + "");
		sb.append("");
		sb.append(displayBytes);

		return sb.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 2:47:28 PM)
	 * @return java.lang.String
	 * @param str java.lang.String
	 */
	public static String displayWithComma(String str) {

		str = ETSStringUtils.trim(str);

		int len = str.length();
		String finalString = "";

		StringBuffer sbc = new StringBuffer();
		int sr = 1;

		if (len > 3) {
			for (int i = len; i > 0; i--) {
				char c = str.charAt(i - 1);
				sbc.append(c);
				if (sr % 3 == 0) {
					if (i - 1 > 0) {
						sbc.append(",");
					}
				}
				sr++;
			}
			sbc.reverse();
			finalString = sbc.toString();
		} else {
			finalString = str;
		}

		return ETSStringUtils.trim(finalString);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 7:22:56 PM)
	 * @return java.lang.String
	 * @param dateString java.lang.String
	 * @param currFormat java.lang.String
	 * @param newFormat java.lang.String
	 */
	public static String formatDate(
		String dateString,
		String currFormat,
		String newFormat)
		throws Exception {

		if (dateString != null)
			dateString = dateString.trim();

		if (currFormat != null)
			currFormat = currFormat.trim();
		else
			currFormat = "yyyy-MM-dd hh:mm:ss.000000";

		if (newFormat != null)
			newFormat = newFormat.trim();
		else
			newFormat = "MMM d, yyyy";

		String dateStringFinal = "";

		java.text.SimpleDateFormat dateFormat =
			new java.text.SimpleDateFormat(newFormat);
		java.text.SimpleDateFormat dateFormatOld =
			new java.text.SimpleDateFormat(currFormat);
		dateFormatOld.setLenient(true);
		dateFormat.setLenient(true);

		// Display date in proper date format.
		try {
			java.util.Date myDate =
				(java.util.Date) dateFormatOld.parse(dateString);
			dateStringFinal = dateFormat.format(myDate);

		} catch (Exception ex) {
			dateStringFinal = dateString;
		}

		return dateStringFinal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/23/2002 8:08:50 PM)
	 * @return java.lang.String
	 */
	public static String getClassVersion() {
		return CQ_CLASS_VERSION;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:42:32 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param start java.lang.String
	 * @param end java.lang.String
	 */
	public static String getTextBetween(
		String source,
		String start,
		String end) {
		int mark1 = source.indexOf(start);
		if (mark1 > -1) {
			mark1 += start.length();
			int mark2 = source.indexOf(end, mark1);
			if (mark2 > -1)
				return source.substring(mark1, mark2);
		}
		return null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:36:20 AM)
	 * @return boolean
	 * @param checkString java.lang.String
	 */
	public static boolean hasUppercaseLetters(String checkString) {
		String testString = checkString.toLowerCase();
		return !testString.equals(checkString);
	}
	/**
	    * Returns the final substring of a string with as many of the final
	    * characters as possible, up to a given length. For instance,
	    * <code>lastSubstring ("hello", 4)</code> will return
	    * <code>"ello"</code>, but lastSubstring ("hello", 8) will return
	    * <code>"hello"</code>. If the whole string is to be returned,
	    * the given parameter is returned rather than a copy.
	    *
	    * @param s String to take the last part of. Must not be null.
	    * @param x maximum size of string to return
	    *
	    * @throws IllegalArgumentException if x is negative
	    */

	public static String lastSubstring(String s, int x) {
		if (x < 0)
			throw new IllegalArgumentException(
				"Invalid parameter to lastSubstring: " + x + " is negative.");

		int l = s.length();

		if (l <= x)
			return s;

		return s.substring(l - x, l);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/23/2002 6:16:13 PM)
	 * @return java.lang.String[]
	 * @param aList java.util.List
	 */
	public static String[] list2Array(List aList) {

		int length = aList.size();
		String[] sarr = new String[length];
		int k = 0;

		for (Iterator i = aList.iterator(); i.hasNext();) {
			String tmp = (String) i.next();
			tmp = ETSStringUtils.trim(tmp);
			sarr[k] = tmp;
			k++;

		}

		return sarr;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 4:57:33 PM)
	 * @return java.lang.String
	 * @param l java.util.List
	 * @param seperator java.lang.String
	 */
	public static String listToString(List l, String seperator) {
		String s = "";
		for (int i = 0; i < l.size(); i++) {
			s = s + (String) l.get(i);
			if (i != l.size() - 1)
				s = s + seperator;
		}

		return s;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:38:36 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param remove java.lang.String
	 */
	public static String removeAllStrings(String source, String remove) {
		for (int index = source.indexOf(remove);
			index > -1;
			index = source.indexOf(remove))
			source =
				source.substring(0, index)
					+ source.substring(index + remove.length());

		return source;
	}
	/**
	 * Remove all occurrences of escape characters.
	 * Creation date: (5/28/2002 12:46:38 AM)
	 * @return java.lang.String
	 * @param s java.lang.String
	 * @param escapeChar char
	 */
	public static String removeEscapes(String s, char escapeChar) {

		StringBuffer result = new StringBuffer();
		StringCharacterIterator it = new StringCharacterIterator(s);

		char c = it.first();
		while (c != CharacterIterator.DONE) {
			if (c == escapeChar) {
				c = it.next();
				if (c != CharacterIterator.DONE) {
					result.append(c);
					c = it.next();
				}
			} else {
				result.append(c);
				c = it.next();
			}

		}

		return (result.toString());
	}
	/**
	 * Converts sql \',\",\\,\NUL  into ',",\,NUL from a source
	 * string and returns it.
	 * (new line to break)
	 * Creation date: (5/6/2002 11:17:52 PM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 */

	public static String removeSlashes(String source) {
		if (source == null) {
			return "";
		}
		StringBuffer dest = new StringBuffer(source.length());
		for (int i = 0; i < source.length(); i++) {
			char c;
			c = source.charAt(i);
			if ((c == '\\')
				&& (i + 1 < source.length())
				&& (source.charAt(i + 1) == '\'')) {
				dest.append("\'");
				i++;
			} else if (
				(c == '\\')
					&& (i + 1 < source.length())
					&& (source.charAt(i + 1) == '\"')) {
				dest.append("\"");
				i++;
			} else if (
				(c == '\\')
					&& ((i + 3) < source.length())
					&& (source.charAt(i + 1) == 'N')
					&& (source.charAt(i + 2) == 'U')
					&& (source.charAt(i + 3) == 'L')) {
				dest.append("N");
				i++;
			} else if (
				(c == '\\')
					&& (i + 1 < source.length())
					&& (source.charAt(i + 1) == '\\')) {
				dest.append("\\");
				i++;
			} else {
				dest.append(c);
			}
		}
		return dest.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:38:06 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param remove java.lang.String
	 */
	public static String removeString(String source, String remove) {
		if (source != null) {
			int index = source.indexOf(remove);
			if (index > -1)
				return source.substring(0, index)
					+ source.substring(index + remove.length());
			else
				return source;
		} else {
			return "";
		}
	}
	/**
	     * Returns a String with all occurrences of <code>from</code>
	     * within <code>orig</code> replaced with <code>to</code>. If
	     * <code>orig</code> contains no occurrences of
	     * <code>from</code>, or if <code>from</code> is equal to
	     * <code>to</code>, <code>orig</code> itself is returned rather
	     * than a copy being made. None of the parameters should be
	     * null.
	     *
	     * @param orig the original String. Must not be null.
	     * @param from the String to replace within <code>orig</code>.
	     * Must not be null.
	     * @param to the String to replace <code>from</code> with. Must
	     * not be null.
	     *
	     * @returns a version of <code>orig</code> with all occurrences
	     * of <code>from</code> being replaced with <code>to</code>.
	     *
	     * @throws IllegalArgumentException if <code>from</code> is empty
	     */

	public static String replace(String orig, String from, String to) {
		int fromLength = from.length();

		if (fromLength == 0)
			throw new IllegalArgumentException("String to be replaced must not be empty");

		int start = orig.indexOf(from);
		if (start == -1)
			return orig;

		boolean greaterLength = (to.length() >= fromLength);

		StringBuffer buffer;
		// If the "to" parameter is longer than (or
		// as long as) "from", the final length will
		// be at least as large
		if (greaterLength) {
			if (from.equals(to))
				return orig;
			buffer = new StringBuffer(orig.length());
		} else {
			buffer = new StringBuffer();
		}

		char[] origChars = orig.toCharArray();

		int copyFrom = 0;
		while (start != -1) {
			buffer.append(origChars, copyFrom, start - copyFrom);
			buffer.append(to);
			copyFrom = start + fromLength;
			start = orig.indexOf(from, copyFrom);
		}
		buffer.append(origChars, copyFrom, origChars.length - copyFrom);

		return buffer.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:37:21 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param Old java.lang.String
	 * @param New java.lang.String
	 */
	public static String replaceAllStringsIgnoreCase(
		String source,
		String Old,
		String New) {
		String lowSource = source.toLowerCase();
		String lowOld = Old.toLowerCase();
		String restSource = source;
		source = "";
		for (int index = lowSource.indexOf(lowOld);
			index > -1;
			index = lowSource.indexOf(lowOld)) {
			source = source + (restSource.substring(0, index) + New);
			restSource = restSource.substring(index + Old.length());
			lowSource = restSource.toLowerCase();
		}

		return source + restSource;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:39:14 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param startOld java.lang.String
	 * @param endOld java.lang.String
	 * @param startNew java.lang.String
	 * @param endNew java.lang.String
	 * @param ignoreCase boolean
	 */
	public static String replaceAllStringsWithWildcard(
		String source,
		String startOld,
		String endOld,
		String startNew,
		String endNew,
		boolean ignoreCase) {
		StringBuffer buffer = new StringBuffer();
		//if (startOld.equals(endOld))
		//throw new Exception("as");
		if (!ignoreCase) {
			StringTokenizer tokenizer = new StringTokenizer(source, startOld);
			if (tokenizer.countTokens() == 1 && source.startsWith(startOld)) {
				if (source.indexOf(endOld) > startOld.length())
					source = replaceString(source, endOld, endNew);
			} else {
				while (tokenizer.hasMoreElements()) {
					String tempToken = tokenizer.nextToken();
					if (tempToken.indexOf(endOld) > -1)
						buffer.append(
							startNew
								+ replaceString(tempToken, endOld, endNew));
					else
						buffer.append(tempToken);
				}
			}
		} else {
			String lowSource = source.toLowerCase();
			String lowStartOld = startOld.toLowerCase();
			String lowEndOld = endOld.toLowerCase();
			String restSource = source;
			source = "";
			for (int index = lowSource.indexOf(lowStartOld); index > -1;) {
				int nextStart =
					lowSource.indexOf(
						lowStartOld,
						index + lowStartOld.length());
				int endPoint = lowSource.indexOf(lowEndOld);
				if (nextStart < 0)
					if (endPoint < 0) {
						return source + restSource;
					} else {
						source =
							source
								+ (restSource.substring(0, index) + startNew);
						restSource =
							restSource.substring(index + startOld.length());
						lowSource = restSource.toLowerCase();
						endPoint = lowSource.indexOf(lowEndOld);
						source =
							source
								+ (restSource.substring(0, endPoint) + endNew);
						restSource =
							restSource.substring(endPoint + endOld.length());
						return source + restSource;
					}
				if (nextStart > endPoint) {
					source =
						source + (restSource.substring(0, index) + startNew);
					restSource =
						restSource.substring(index + startOld.length());
					lowSource = restSource.toLowerCase();
					endPoint = lowSource.indexOf(lowEndOld);
					source =
						source + (restSource.substring(0, endPoint) + endNew);
					restSource =
						restSource.substring(endPoint + endOld.length());
					lowSource = restSource.toLowerCase();
					index = lowSource.indexOf(lowStartOld);
				} else {
					source = source + restSource.substring(0, nextStart);
					restSource = restSource.substring(nextStart);
					lowSource = restSource.toLowerCase();
					index = lowSource.indexOf(lowStartOld);
				}
			}

			return source + restSource;
		}
		return buffer.toString();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:41:20 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param Old java.lang.String
	 * @param New java.lang.String
	 */
	public static String replaceString(String source, String Old, String New) {
		int index = source.indexOf(Old);
		if (index > -1)
			return String.valueOf(
				(new StringBuffer(String.valueOf(source.substring(0, index))))
					.append(New)
					.append(source.substring(index + Old.length())));
		else
			return source;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:36:54 AM)
	 * @return java.lang.String
	 * @param source java.lang.String
	 * @param Old java.lang.String
	 * @param New java.lang.String
	 */
	public static String replaceStringIgnoreCase(
		String source,
		String Old,
		String New) {
		String tempSource = source.toLowerCase();
		String tempOld = Old.toLowerCase();
		int index = tempSource.indexOf(tempOld);
		if (index > -1)
			return String.valueOf(
				(new StringBuffer(String.valueOf(source.substring(0, index))))
					.append(New)
					.append(source.substring(index + Old.length())));
		else
			return source;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/13/2002 6:37:32 PM)
	 * @return java.lang.String
	 * @param string java.lang.String
	 */
	public static String sentenceCaseString(String string) {

		String newString = "";

		newString = string.toLowerCase();

		return toUpperCaseAt(newString, 0);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/6/2002 1:59:10 AM)
	 * @return java.lang.String[]
	 * @param str java.lang.String
	 * @param delim java.lang.String
	 */
	public static String[] split(String str, String delim) {
		Vector v = new Vector();
		for (StringTokenizer tokenizer = new StringTokenizer(str, delim);
			tokenizer.hasMoreTokens();
			v.addElement(tokenizer.nextToken()));
		String ret[] = new String[v.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = (String) v.elementAt(i);

		return ret;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 4:56:55 PM)
	 * @return java.lang.String[]
	 * @param s java.lang.String
	 */
	public static String[] stringToArray(String s) {
		return stringToArray(s, " ");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 4:55:38 PM)
	 * @return java.lang.String[]
	 * @param s java.lang.String
	 * @param seperator java.lang.String
	 */
	public static String[] stringToArray(String s, String seperator) {
		List _tokens = stringToList(s, seperator);
		String tokens[] = new String[_tokens.size()];
		for (int i = 0; i < tokens.length; i++)
			tokens[i] = (String) _tokens.get(i);

		return tokens;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 4:56:08 PM)
	 * @return java.util.List
	 * @param s java.lang.String
	 * @param seperator java.lang.String
	 */
	public static int stringToArraySize(String s, String seperator) {
		if (s.indexOf(seperator) == -1) {
			ArrayList a = new ArrayList();
			a.add(s);
			return a.size();
		}
		StringTokenizer tokenizer = new StringTokenizer(s, seperator);
		ArrayList tokens = new ArrayList();
		for (;
			tokenizer.hasMoreTokens();
			tokens.add(ETSStringUtils.trim(tokenizer.nextToken())));
		return tokens.size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/16/2002 12:50:49 AM)
	 * @return boolean
	 * @param str java.lang.String
	 */

	public static boolean stringToBoolean(String str) {
		return str != null && str.equals("y");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:45:11 AM)
	 * @return java.util.Date
	 * @param s java.lang.String
	 * @exception java.text.ParseException The exception description.
	 */
	public static Date stringToDate(String s) throws java.text.ParseException {
		return (DateFormat.getDateInstance().parse(s));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/8/2002 4:56:08 PM)
	 * @return java.util.List
	 * @param s java.lang.String
	 * @param seperator java.lang.String
	 */
	public static List stringToList(String s, String seperator) {
		if (s.indexOf(seperator) == -1) {
			ArrayList a = new ArrayList();
			a.add(s);
			return a;
		}
		StringTokenizer tokenizer = new StringTokenizer(s, seperator);
		ArrayList tokens = new ArrayList();
		for (;
			tokenizer.hasMoreTokens();
			tokens.add(ETSStringUtils.trim(tokenizer.nextToken())));
		return tokens;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:45:41 AM)
	 * @return java.util.Date
	 * @param s java.lang.String
	 */
	public static Date timestampToDate(String s) {
		return null;
	}
	/**
	 * Replace character at given index with the same character to upper case
	 * Creation date: (6/13/2002 6:35:30 PM)
	 * @return java.lang.String
	 * @param oldString java.lang.String
	 * @param index int
	 */
	public static String toUpperCaseAt(String oldString, int index) {
		int length = oldString.length();
		String newString = "";

		if (index >= length || index < 0) {
			throw new StringIndexOutOfBoundsException(
				"Index "
					+ index
					+ " is out of bounds for string length "
					+ length);
		}

		//get upper case replacement
		String upper = String.valueOf(oldString.charAt(index)).toUpperCase();

		//avoid index out of bounds
		String paddedString = oldString + " ";

		//get reusable parts
		String beforeIndex = paddedString.substring(0, index);
		String afterIndex = paddedString.substring(index + 1);

		//generate new String - remove padding spaces
		newString = (beforeIndex + upper + afterIndex).substring(0, length);

		return newString;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/5/2002 10:59:20 PM)
	 * @return java.lang.String
	 * @param s java.lang.String
	 */
	public static String trim(String s) {
		return s == null ? "" : s.trim();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/5/2002 10:59:20 PM)
	 * @return java.lang.String
	 * @param s java.lang.String
	 */
	public static String trim(String s, int size) {

		String newString = "";
		if (s == null) {
			newString = "";
		} else {
			newString = ETSStringUtils.trim(s);
		}

		if (newString.length() > size) {
			newString = newString.substring(0, size);
		}

		return newString;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/5/2002 10:59:20 PM)
	 * @return java.lang.String
	 * @param s java.lang.String
	 */
	public static String trimWithDash(String s) {

		String newString = "";
		if (s == null) {
			newString = "-";
		} else {
			newString = ETSStringUtils.trim(s);
		}

		if (newString.equals("")) {
			newString = "-";
		}

		return newString;

	}
}
