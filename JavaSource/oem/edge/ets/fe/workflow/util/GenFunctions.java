/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
/*
 * Created on Nov 12, 2005
 * @author v2sathis@us.ibm.com
 */
package oem.edge.ets.fe.workflow.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.common.Global;

/**
 * @author v2deepak
 *  
 */
public class GenFunctions {
	public static Hashtable static_space = new Hashtable();

	public static void errorMsg(PrintWriter out, String line, int flag) {
		out.println("<br /><br />");
		if (flag == 1)
			out.println("Authorization not allowed");
		if (flag != 0)
			out.println("<form>");
		out
				.println("<table summary=\"\" border=\"0\"><tr><td header=\"\" valign=\"top\" align=\"center\">&nbsp;");
		out.println("</td><td  header=\"\" valign=\"top\" align=\"left\">");
		out.println(line);
		out
				.println("<br /><br />For 24 hour support - please call (US) 1-888-220-3343 or Email: eConnect@us.ibm.com");
		out
				.println("<br /><br /><a href=\"javascript:void (window.history.back())\"><img border=\"0\" name=\"Cancel\" src=\"//www.ibm.com/i/v14/buttons/cancel.gif\" height=\"21\" width=\"120\" align=\"middle\" alt=\"Back\"/></a><br />");
		out.println("</td></tr></table>");
		if (flag != 0)
			out.println("</form>");
		out.println("<br /><br />    ");
	}

	public static Vector getVStringTokens(String val, String token) {
		Vector v = new Vector();
		if (val == null)
			return v;
		StringTokenizer st = new StringTokenizer(val, token);
		while (st.hasMoreTokens()) {
			String str_val = st.nextToken();
			v.addElement(str_val.trim());
		}
		return v;
	}

	public static ArrayList getStringTokens(String val, String token) {
		ArrayList v = new ArrayList();
		if (val == null)
			return v;
		StringTokenizer st = new StringTokenizer(val, token);
		while (st.hasMoreTokens()) {
			String str_val = st.nextToken();
			v.add(str_val.trim());
		}
		return v;
	}

	public static String getQuoteString(Vector v) {
		if (v == null || v.size() == 0)
			return "''";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append("'").append((String) v.elementAt(i)).append("'");
		}
		return sb.toString();
	}

	public static String getNonQuoteString(Vector v) {
		if (v == null || v.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append((String) v.elementAt(i));
		}
		return sb.toString();
	}

	public static StringBuffer printSelect(Connection conn, String qry,
			String name, String vals, boolean single, String extra)
			throws SQLException {
		Vector v = DBHandler.getVQueryResult(conn, qry, 2);
		return printSelect(v, name, vals, single, extra, "", "iform", false);
	}

	public static void printSelect(PrintWriter out, Connection conn,
			String qry, String name, String vals, boolean single, String extra)
			throws SQLException {
		Vector v = DBHandler.getVQueryResult(conn, qry, 2);
		printSelect(out, v, name, vals, single, extra, "", "iform", 4, null);
	}

	public static void printSelect(PrintWriter out, Vector data, String name,
			String vals, boolean single, String extra, String js, String form) {
		GenFunctions.printSelect(out, data, name, vals, single, extra, js,
				form, 4, null);
	}

	public static void printSelect(PrintWriter out, Vector data, String name,
			String vals, boolean single, String extra, String js, String form,
			int size, String ignore) {
		printSelect(out, data, name, vals, single, extra, js, form, size,
				ignore, false);
	}

	public static void printSelect(PrintWriter out, Vector data, String name,
			String vals, boolean single, String extra, String js, String form,
			int size, String ignore, boolean one) {
		Vector ignorev = new Vector();
		if (ignore != null)
			ignorev = getVStringTokens(ignore, ",");
		if (vals == null)
			vals = "";
		boolean ele = false;
		if (data.size() > 0 || extra != null) {
			if (single)
				out.println("<select id=\"" + name + "\" class=\"" + form
						+ "\" size=\"1\" name=\"" + name + "\" " + js + ">");
			else
				out.println("<select id=\"" + name + "\" class=\"" + form
						+ "\" size=\"" + size + "\" name=\"" + name
						+ "\" multiple=\"multiple\" " + js + ">");
			if (extra != null) {
				if (vals.indexOf(extra) != -1)
					out.println("<option value=\"" + extra + "\" selected>"
							+ extra + "</option>");
				else
					out.println("<option value=\"" + extra + "\" >" + extra
							+ "</option>");
			}
			ele = true;
		}
		for (int i = 0; i < data.size(); i++) {
			String v_vals[] = null;
			String s = null;
			if (one) {
				s = (String) data.elementAt(i);
			} else {
				v_vals = (String[]) data.elementAt(i);
				s = v_vals[0];
				s = (s == null) ? "-" : s.trim();
			}
			if (ignorev.contains(s))
				continue;
			String s1 = (one) ? s : v_vals[1];
			s1 = (s1 == null) ? "No value" : s1.trim();
			if ((single && vals.equals(s))
					|| (!single && vals.indexOf(s) != -1))
				out.println("<option value=\"" + s + "\" selected>" + s1
						+ "</option>");
			else
				out.println("<option value=\"" + s + "\">" + s1 + "</option>");
		}
		if (ele == false)
			out.println("No value(s) to select");
		else
			out.println("</select>");
	}

	public static void printradio(PrintWriter out, Vector data, String name,
			String vals, String extra, String js, String form, int size,
			String ignore, boolean one, String brbegin) {
		Vector ignorev = new Vector();
		if (ignore != null)
			ignorev = getVStringTokens(ignore, ",");
		if (vals == null)
			vals = "";
		boolean ele = false;
		if (data.size() > 0 || extra != null) {
			if (extra != null) {
				if (vals.indexOf(extra) != -1)
					out.println("<input type=\"radio\" id=\"" + name
							+ "\" class=\"" + form + "\" name=\"" + name
							+ "\" " + js + " value=\"" + extra
							+ "\" checked=\"checked\" >" + extra);
				else
					out
							.println("<input type=\"radio\" id=\"" + name
									+ "\" class=\"" + form + "\" name=\""
									+ name + "\" " + js + " value=\"" + extra
									+ "\" >" + extra);
			}
			ele = true;
		}
		int totalsize = data.size() + (extra != null ? 1 : 0);
		for (int i = 0; i < data.size(); i++) {
			String v_vals[] = null;
			String s = null;
			if (one) {
				s = (String) data.elementAt(i);
			} else {
				v_vals = (String[]) data.elementAt(i);
				s = v_vals[0];
				s = (s == null) ? "-" : s.trim();
			}
			if (ignorev.contains(s))
				continue;
			String s1 = (one) ? s : v_vals[1];
			s1 = (s1 == null) ? "No value" : s1.trim();
			if (vals.equals(s))
				out.println("<input type=\"radio\" id=\"" + s + "\" class=\""
						+ form + "\" name=\"" + s + "\" " + js + " value=\""
						+ s + "\" checked=\"checked\" >" + s1);
			else
				out.println("<input type=\"radio\" id=\"" + s + "\" class=\""
						+ form + "\" name=\"" + s + "\" " + js + " value=\""
						+ s + "\" disabled=\"true\">" + s1);
			if (size == -1)
				out.println("<br />" + brbegin);
		}
		if (ele == false)
			out.println("No value(s) to select");
		else
			out.println("</select>");
	}

	public static StringBuffer printSelect(Vector data, String name,
			String vals, boolean single, String extra, String js, String form,
			boolean one) {
		StringBuffer sb = new StringBuffer();
		if (vals == null)
			vals = "";
		boolean ele = false;
		if (data.size() > 0 || extra != null) {
			if (single)
				sb.append("<select id=\"" + name + "\" class=\"" + form
						+ "\" size=\"1\" name=\"" + name + "\" " + js + ">");
			else
				sb.append("<select id=\"" + name + "\" class=\"" + form
						+ "\" size=\"4\" name=\"" + name
						+ "\" multiple=\"multiple\" " + js + ">");
			if (extra != null) {
				if (vals.indexOf(extra) != -1)
					sb.append("<option value=\"" + extra + "\" selected>"
							+ extra + "</option>");
				else
					sb.append("<option value=\"" + extra + "\" >" + extra
							+ "</option>");
			}
			ele = true;
		}
		for (int i = 0; i < data.size(); i++) {
			String v_vals[] = null;
			String s = null;
			if (one) {
				s = (String) data.elementAt(i);
			} else {
				v_vals = (String[]) data.elementAt(i);
				s = v_vals[0];
				s = (s == null) ? "-" : s.trim();
			}
			String s1 = (one) ? s : v_vals[1];
			s1 = (s1 == null) ? "No value" : s1.trim();
			if ((single && vals.equals(s))
					|| (!single && vals.indexOf(s) != -1))
				sb.append("<option value=\"" + s + "\" selected>" + s1
						+ "</option>");
			else
				sb.append("<option value=\"" + s + "\">" + s1 + "</option>");
		}
		if (ele == false)
			sb.append("No value(s) to select");
		else
			sb.append("</select>");
		return sb;
	}

	public static String LogErrMsg(SQLException e, Connection con, String qry,
			String frm) {
		try {
			con.rollback();
		} catch (SQLException x) {
			e.printStackTrace();
		}
		try {
			con.setAutoCommit(true);
		} catch (SQLException x) {
			e.printStackTrace();
		}
		String Msg = e.getMessage().trim();
		String opMsg = "Internal Problem Encountered:\\n\\nPlease contact Help Desk with Error Code: WEB "
				+ e.getSQLState() + "::" + e.getErrorCode() + "\\n";
		if (Msg.length() > 500)
			Msg = Msg.substring(0, 499);
		if (qry.length() > 1000)
			qry = qry.substring(0, 999);
		qry = Global.getQString(qry);
		Msg = Global.getQString(Msg);
		String query = "insert into edgegen.db_error (err_id,err_state,err_code,err_form,err_query,err_message) values (generate_unique(),'"
				+ e.getSQLState()
				+ "',"
				+ e.getErrorCode()
				+ ",'"
				+ frm
				+ "','" + qry + "','" + Msg + "')";
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			con.commit();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return opMsg;
	}

	static public String getDate() {
		java.util.TimeZone tz = java.util.TimeZone.getDefault();
		java.util.Calendar cal = new java.util.GregorianCalendar(tz);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MMM-dd-yyyy_HH:mm:ss");
		String datestr = sdf.format(cal.getTime());
		return datestr;
	}

	static public String getDate1() {
		java.util.TimeZone tz = java.util.TimeZone.getDefault();
		java.util.Calendar cal = new java.util.GregorianCalendar(tz);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MMM_dd_yyyy_HH_mm_ss");
		String datestr = sdf.format(cal.getTime());
		return datestr;
	}

	public static String getString(String a) {
		boolean first = true;
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < a.length(); i++) {
			String s = a.substring(i, i + 1);
			b.append(s);
			if (s.equals(">")) {
				b.append("\r\n");
				if (first) {
					b.append("<!DOCTYPE SCRXML SYSTEM \"quest.dtd\">\r\n");
					first = false;
				}
			}
		}
		return (b.toString());
	}

	public static Object getStoredValue(String id) {
		return static_space.get(id);
	}

	public static void storeValue(String id, Object value) {
		static_space.put(id, value);
	}

	public static String getKeyVals(Connection conn, String parm)
			throws SQLException {
		return getKeyVals(conn, parm, null);
	}

	public static String getKeyVals(Connection conn, String parm, String sec)
			throws SQLException {
		String key = "KEY_" + parm + (sec == null ? "NOSEC" : sec);
		String ret = (String) getStoredValue(key);
		if (ret == null) {
			Vector v = DBHandler
					.getValues(conn,
							"select value || sec_value1 from amt.key where parm='"
									+ parm
									+ "' "
									+ (sec == null ? "" : "and sec_parm1='"
											+ sec + "'")
									+ " order by key_id for read only");
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < v.size(); i++) {
				sb.append((String) v.elementAt(i));
			}
			ret = sb.toString();
			storeValue(key, ret);
		}
		return ret;
	}

	public static void dispSelBox(Connection conn, PrintWriter out,
			String form, String avlBox, String selBox, String strAvl,
			String fldv, boolean noneFlag, boolean quoteFlag) throws Exception,
			SQLException {
		//////////////////////////////////////
		// script to move the values across //
		//////////////////////////////////////
		out
				.println("<script type=\"text/javascript\" language=\"javascript\">");
		out.println("function MoveRight" + avlBox + "()	{");
		out.println("var b = document.selapp." + selBox + ".length;");
		out.println("for (var j = 0; j < document.selapp." + avlBox
				+ ".length; j++){");
		out.println("if (document.selapp." + avlBox + ".options[j].selected){");
		out.println("var str = document.selapp." + avlBox
				+ ".options[j].value;");
		out
				.println("var txt = document.selapp." + avlBox
						+ ".options[j].text;");
		out.println("document.selapp." + avlBox + ".options[j] = null;");
		out.println("j=j-1;");
		out.println("document.selapp." + selBox
				+ ".options[b] = new Option(txt, str);");
		out.println("b = b + 1;");
		out.println("}");
		out.println("}");
		out.println("var a = document.selapp." + avlBox + ".length;");
		out.println("for (var j = 0; j < a; j++)	{");
		out.println("if (document.selapp." + avlBox + ".options[j] == null)	{");
		out.println("if (j + 1 < a) {");
		out.println("var str = document.selapp." + avlBox
				+ ".options[j + 1].value;");
		out.println("var txt = document.selapp." + avlBox
				+ ".options[j + 1].text;");
		out.println("document.selapp." + avlBox
				+ ".options[j] = new Option(txt, str);");
		out.println("document.selapp." + avlBox + ".options[j + 1] = null;");
		out.println("}");
		out.println("}");
		out.println("}");
		out.println("for (var k = 0; k < document.selapp." + selBox
				+ ".length; k++) {");
		out.println("document.selapp." + selBox
				+ ".options[k].selected = true;");
		out.println("}");
		out.println("}");
		out.println("function MoveLeft" + selBox + "() {");
		out.println("var b = document.selapp." + avlBox + ".length;");
		out.println("for (var j = 0; j < document.selapp." + selBox
				+ ".length; j++){");
		out.println("if (document.selapp." + selBox + ".options[j].selected){");
		out.println("var str = document.selapp." + selBox
				+ ".options[j].value;");
		out
				.println("var txt = document.selapp." + selBox
						+ ".options[j].text;");
		out.println("document.selapp." + selBox + ".options[j] = null;");
		out.println("j=j-1;");
		out.println("document.selapp." + avlBox
				+ ".options[b] = new Option(txt, str);");
		out.println("b = b + 1;");
		out.println("}");
		out.println("}");
		out.println("var a = document.selapp." + selBox + ".length;");
		out.println("for (var j = 0; j < a; j++){");
		out.println("if (document.selapp." + selBox + ".options[j] == null)	{");
		out.println("if (j + 1 < a){");
		out.println("var str = document.selapp." + selBox
				+ ".options[j + 1].value;");
		out.println("var txt = document.selapp." + selBox
				+ ".options[j + 1].text;");
		out.println("document.selapp." + selBox
				+ ".options[j] = new Option(txt, str);");
		out.println("document.selapp." + selBox + ".options[j + 1] = null;");
		out.println("}");
		out.println("}");
		out.println("}");
		out.println("for (var k = 0; k < document.selapp." + selBox
				+ ".length; k++) {");
		out.println("document.selapp." + selBox
				+ ".options[k].selected = true;");
		out.println("}");
		out.println("}");
		out.println("</script>");
		///////////////////////////////////////////
		// End of Script //////////////////////////
		///////////////////////////////////////////
		out
				.println("<label for=\"avlb\"></label><select id=\"avlb\" name=\""
						+ avlBox
						+ "\" multiple=\"multiple\" size=\"6\" width=\"200\" class=\"iform\">");
		if (noneFlag) {
			out.println("<option value=\"NONE\">-----NONE-----</option>");
		}
		if (fldv == null)
			fldv = "";
		Statement stmtAvl = null;
		ResultSet rsAvl = null;
		try {
			stmtAvl = conn.createStatement();
			rsAvl = stmtAvl.executeQuery(strAvl);
			boolean valf = false;
			while (rsAvl.next()) {
				valf = true;
				String val = rsAvl.getString(1).trim();
				String name = rsAvl.getString(2).trim();
				if (quoteFlag) {
					if (fldv.indexOf(val) == -1)
						out.println("<option value=\"'" + val + "'\">" + name
								+ "</option>");
				} else {
					if (fldv.indexOf(val) == -1)
						out.println("<option value=\"" + val + "\">" + name
								+ "</option>");
				}
			}
			if (!valf)
				out
						.println("<option value=\"'NONEVAL'\">----No Values----</option>");
		} catch (SQLException e) {
			LogErrMsg(e, conn, strAvl, "");
			throw e;
		} finally {
			if (rsAvl != null)
				try {
					rsAvl.close();
				} catch (SQLException ex) {
				}
			if (stmtAvl != null)
				try {
					stmtAvl.close();
				} catch (SQLException ex) {
				}
		}
		out.println("</select>");
		out.println("</td>");
		out.println("<td headers=\"\"  valign=\"center\" align=\"middle\">");
		out.println("<a href=\"javascript:MoveRight" + avlBox + "()\">");
		out
				.println("<img border=\"0\" name=\"Right\" src=\""
						+ Global.WebRoot
						+ "/images/arrow_rd.gif\" width=\"21\" height=\"21\" align=\"middle\" alt=\"right\" /></a>");
		out.println("<br />");
		out.println("<a href=\"javascript:MoveLeft" + selBox + "()\">");
		out
				.println("<img border=\"0\" name=\"Left\" src=\""
						+ Global.WebRoot
						+ "/images/arrow_lt.gif\" width=\"21\" height=\"21\" align=\"middle\" alt=\"left\" /></a>");
		out.println("</td>");
		out.println("<td headers=\"\"  valign=\"center\" align=\"left\">");
		out
				.println("<label for=\"selb\"></label><select id=\"selb\" name=\""
						+ selBox
						+ "\" multiple=\"multiple\" size=\"6\" width=\"200\" class=\"iform\">");
		if (noneFlag) {
			out.println("<option value=\"NONE\">-----NONE-----</option>");
		}
		if (!fldv.equals("")) {
			Statement stmtSel = null;
			ResultSet rsSel = null;
			try {
				stmtSel = conn.createStatement();
				rsSel = stmtSel.executeQuery(strAvl);
				while (rsSel.next()) {
					String val = rsSel.getString(1).trim();
					String name = rsSel.getString(2).trim();
					if (quoteFlag) {
						if (fldv.indexOf(val) != -1)
							out.println("<option value=\"'" + val
									+ "'\" SELECTED>" + name + "</option>");
					} else {
						if (fldv.indexOf(val) != -1)
							out.println("<option value=\"" + val
									+ "\" selected>" + name + "</option>");
					}
				}
			} catch (SQLException e) {
				LogErrMsg(e, conn, strAvl, "");
				throw e;
			} finally {
				if (rsSel != null)
					try {
						rsSel.close();
					} catch (SQLException ex) {
					}
				if (stmtSel != null)
					try {
						stmtSel.close();
					} catch (SQLException ex) {
					}
			}
		}
		out.println("</select>");
		//out.println("</td>");
	}

	public static int parseInt(String s, int def) {
		int ret = def;
		try {
			ret = Integer.parseInt(s);
		} catch (Exception e) {
			ret = def;
		}
		return ret;
	}

	public static String trim(String in) {
		return trim(in, "");
	}

	public static String trim(String in, String def) {
		if (in == null)
			return def;
		else
			return in.trim();
	}

	public static String buildInClause(String[] array) {
		StringBuffer sql = new StringBuffer();
		//sql.append(" IN (");

		for (int i = 0; i < array.length; i++) {

			String tmp = array[i];
			if (tmp != null)
				tmp = tmp.trim();

			sql.append(" '" + tmp + "' ");
			if (i < array.length - 1)
				sql.append(", ");
		}

		//sql.append(")");
		return sql.toString();
	}

	public static String buildInClause(Vector vStr) {
		StringBuffer sql = new StringBuffer();
		//sql.append(" IN (");

		for (int i = 0; i < vStr.size(); i++) {

			String tmp = (String) vStr.get(i);
			if (tmp != null)
				tmp = tmp.trim();

			sql.append(" '" + tmp + "' ");
			if (i < vStr.size() - 1)
				sql.append(", ");
		}

		//sql.append(")");
		return sql.toString();
	}

}
