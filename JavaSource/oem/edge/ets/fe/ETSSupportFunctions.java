package oem.edge.ets.fe;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;
import oem.edge.common.Global;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
public class ETSSupportFunctions {
	static public void close(Statement stmtx, ResultSet rsx) {
		if (rsx != null)
			try {
				rsx.close();
				rsx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (stmtx != null)
			try {
				stmtx.close();
				stmtx = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	public static Vector getValues(Connection conn, String qry) throws SQLException {
		Vector v = new Vector();
		Statement stmtx = null;
		ResultSet rsx = null;
		try {
			stmtx = conn.createStatement();
			rsx = stmtx.executeQuery(qry);
			while (rsx.next()) {
				String s = rsx.getString(1);
				if (s == null)
					s = "";
				else
					s = s.trim();
				v.addElement(s);
			}
		} finally {
			close(stmtx, rsx);
		}
		return v;
	}
	public static String getValue(Connection conn, String qry) throws SQLException {
		String ret = "";
		Statement stmtx = null;
		ResultSet rsx = null;
		try {
			stmtx = conn.createStatement();
			rsx = stmtx.executeQuery(qry);
			if (rsx.next()) {
				ret = rsx.getString(1);
				if (ret == null)
					ret = "";
				else
					ret = ret.trim();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return ret;
	}
	static public int[] getYears(int before, int after) {
		int a[] = new int[before + after + 1];
		Calendar cal = Calendar.getInstance();
		int y1 = cal.get(Calendar.YEAR);
		for (int i = y1 - before; i <= y1 + after; i++)
			a[i + before - y1] = i;
		return a;
	}
	static public int fireUpdate(Connection con, String qry) throws SQLException {
		System.out.println(qry);
		Statement pstmt = null;
		int ret = -1;
		try {
			pstmt = con.createStatement();
			ret = pstmt.executeUpdate(qry);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ret;
	}
	static public int countQry(Connection con, String qry) throws SQLException, Exception {
		try {
			String no = getValue(con, qry);
			return Integer.parseInt(no);
		} catch (Exception e) {
			return 0;
		}
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
	public static ArrayList getCSVStringTokens(ArrayList data, int idx, boolean quote) {
			int pass= 0;
			ArrayList v= new ArrayList();
			int mode= -1; // -1 = start 	,0 append till , 1 append till ",
			boolean more= true;
			StringBuffer x= null;
			while (more) {
				if (pass == 0) {
					v.add("0");
					x= new StringBuffer("");
				}
				more= false;
				String in= (String)data.get(idx + pass);
				if (in == null)
					return v;
				boolean nextpresent= true;
				System.out.println(in);
				for (int i= 0; i < in.length(); i++) {
					char c= in.charAt(i);
					if (mode == -1) {
						if (c == '\"')
							mode= 1;
						else if (c == ',') // empty!! 
							v.add("");
						else {
							x.append(c);
							mode= 0;
						}
					} else if (mode == 0) {
						if (c == ',') {
							mode= -1;
							if (quote)
								v.add(Global.getQString(x.toString()).trim());
							else
								v.add(x.toString().trim());
							x= new StringBuffer("");
							nextpresent= true;
						} else
							x.append(c);
					} else if (mode == 1) {
						if (c == '"' && ((i + 1) >= in.length() || in.charAt(i + 1) == ',')) {
							if ((i + 1) >= in.length())
								nextpresent= false; // last element is with "
							i= i + 1; // skip for ,
							mode= -1;
							if (quote)
								v.add(Global.getQString(x.toString()).trim());
							else
								v.add(x.toString().trim());
							x= new StringBuffer("");
						} else {
							if (c == '"' && in.charAt(i + 1) == '"')
								i= i + 1; // skip for ""			
							x.append(c);
						}
					}
				}
				if (mode == 1) {
					more= true;
					pass= pass + 1;
					v.set(0, String.valueOf(pass));
					x.append('\n');
				} else if (nextpresent) {
					if (quote)
						v.add(Global.getQString(x.toString()).trim());
					else
						v.add(x.toString().trim());
				}
			}
			return v;
		}
	public static ArrayList getCSVStringTokens1(String in) {
		ArrayList v = new ArrayList();
		if (in == null)
			return v;
		int mode = -1; // -1 = start 	,0 append till , 1 append till ",
		boolean nextpresent = true;
		System.out.println(in);
		StringBuffer x = new StringBuffer("");
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (mode == -1) {
				if (c == '\"')
					mode = 1;
				else if (c == ',') // empty!! 
					v.add("");
				else {
					x.append(c);
					mode = 0;
				}
			} else if (mode == 0) {
				if (c == ',') {
					mode = -1;
					v.add(Global.getQString(x.toString()).trim());
					x = new StringBuffer("");
					nextpresent = true;
				} else
					x.append(c);
			} else if (mode == 1) {
				if (c == '"' && ((i + 1) >= in.length() || in.charAt(i + 1) == ',')) {
					if ((i + 1) >= in.length())
						nextpresent = false; // last element is with "
					i = i + 1; // skip for ,
					mode = -1;
					v.add(Global.getQString(x.toString()).trim());
					x = new StringBuffer("");
				} else {
					if (c == '"' && in.charAt(i + 1) == '"')
						i = i + 1; // skip for ""			
					x.append(c);
				}
			}
		}
		if (nextpresent)
			v.add(Global.getQString(x.toString()).trim());
		return v;
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
	public static float parseFloat(String s) {
		return parseFloat(s, (float) 0.0);
	}
	public static float parseFloat(String s, float def) {
		float ret = def;
		try {
			ret = Float.parseFloat(s);
		} catch (Exception e) {
			ret = def;
		}
		return ret * (float) 1.1;
	}
	public static double parseDouble(String s, double def) {
		double ret = def;
		try {
			ret = Double.parseDouble(s);
		} catch (Exception e) {
			ret = def;
		}
		return ret;
	}
	public static double getStringVectorToAvgFloat(Vector v) {
		double ret = 0;
		if (v == null || v.size() == 0)
			return -1;
		int size = v.size();
		for (int i = 0; i < size; i++) {
			double d = parseDouble((String) v.elementAt(i), 0);
			ret = ret + d;
		}
		return ret / size;
	}
}
