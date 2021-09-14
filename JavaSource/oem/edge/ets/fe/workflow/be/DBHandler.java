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
package oem.edge.ets.fe.workflow.be;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
/**
 * @author v2deepak
 *
 */
public class DBHandler {
	static public int safeInsert(Connection con, String qry)
			throws SQLException {
			Statement pstmt = null;
			int ret = 0;
			try {
				System.out.println("Query:" + qry);
				pstmt = con.createStatement();
				ret = pstmt.executeUpdate(qry);
			} catch (SQLException e) {
				if (e.getErrorCode() != -803) {
					throw e;
				}
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
	public static Hashtable getHVQueryResults(Connection conn, String qry, int vars) throws SQLException {
		///  A,B1,B3   A,C1,C2  X,C1,C5  X,D1,D5  --> HS key A,X  with vectors
		Hashtable hs= new Hashtable();
		Statement stmtx= null;
		ResultSet rsx= null;
		try {
			System.out.println(qry);
			String prev= "";
			Vector v= null;
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			while (rsx.next()) {
				String x= rsx.getString(1).trim();
				if (!x.equals(prev)) {
					if (v != null)
						hs.put(prev, v);
					v= new Vector();
					prev= x;
				}
				String[] rec= new String[vars - 1];
				for (int i= 1; i < vars; i++)
					rec[i - 1]= GenFunctions.trim(rsx.getString(i + 1));
				v.addElement(rec);
			}
			if (v != null)
				hs.put(prev, v);
		} catch (SQLException e) {
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return hs;
	}
	public static Vector getValues(Connection conn, String qry) throws SQLException {
		return getValues(conn,qry,true);
	}
	public static Vector getValues(Connection conn, String qry,boolean print) throws SQLException {
		Vector v= new Vector();
		Statement stmtx= null;
		ResultSet rsx= null;
		if (print) System.out.println(qry);
		try {
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			while (rsx.next()) {
				String s= rsx.getString(1);
				s= (s == null) ? "" : s.trim();
				v.addElement(s);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return v;
	}
	public static Hashtable getHQueryResult(Connection conn, String qry) throws SQLException {
		Hashtable hs= new Hashtable();
		Statement stmtx= null;
		ResultSet rsx= null;
		try {
			System.out.println(qry);
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			while (rsx.next()) {
				String x= rsx.getString(1).trim();
				String s= rsx.getString(2);
				if (s == null)
					s= "";
				else
					s= s.trim();
				hs.put(x, s);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return hs;
	}
	public static Vector getVQueryResult(Connection conn, String qry, int vars) throws SQLException {
		Vector v= new Vector();
		Statement stmtx= null;
		ResultSet rsx= null;
		try {
			System.out.println(qry);
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			while (rsx.next()) {
				String[] rec= new String[vars];
				for (int i= 0; i < vars; i++) {
					String s= rsx.getString(i + 1);
					rec[i]= (s == null) ? "" : s.trim();
				}
				v.addElement(rec);
			}
		} catch (SQLException e) {
			GenFunctions.LogErrMsg(e, conn, qry, "");
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return v;
	}
	public static String getValue(Connection conn, String qry) throws SQLException {
		return getValue(conn,qry,true);
	}
	public static String getValue(Connection conn, String qry,boolean print) throws SQLException {
		String ret= "";
		Statement stmtx= null;
		ResultSet rsx= null;
		if (print) System.out.println(qry);
		try {
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			if (rsx.next()) {
				ret= rsx.getString(1);
				ret= ret == null ? "" : ret.trim();
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return ret;
	}
	static public int fireUpdate(Connection con, String qry) throws SQLException {
		return fireUpdate(con,qry,true);
	}
	static public int fireUpdate(Connection con, String qry,boolean print) throws SQLException {
		if (print) System.out.println(qry);
		Statement pstmt= null;
		int ret= -1;
		try {
			pstmt= con.createStatement();
			ret= pstmt.executeUpdate(qry);
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
	static public int countQry(Connection con, String qry,boolean print) throws SQLException, Exception {
		try {
			String no= getValue(con, qry,print);
			return Integer.parseInt(no);
		} catch (Exception e) {
			return 0;
		}
	}
	public static String getClobValue(Connection conn, String qry, String dir) throws SQLException {
		String ret= "", s= "";
		Statement stmtx= null;
		ResultSet rsx= null;
		try {
			System.out.println(qry);
			stmtx= conn.createStatement();
			rsx= stmtx.executeQuery(qry);
			if (rsx.next()) {
				Clob clob= rsx.getClob(1);
				if (clob != null) {
					long clobLen= clob.length();
					if (clobLen < Integer.MAX_VALUE) {
						s= clob.getSubString(1, (new Long(clobLen)).intValue());
					}
				}
				int idx= s.indexOf("<SCRXML>");
				if (idx != -1) {
					if (!dir.equals(""))
						ret= s.substring(0, idx) + dir + s.substring(idx);
					else
						ret= s;
				} else
					ret= s;
			}
		} catch (SQLException e) {
			GenFunctions.LogErrMsg(e, conn, qry, "");
			throw e;
		} finally {
			close(stmtx, rsx);
		}
		return ret;
	}
	public static void setClobValue(Connection conn, String qry, String s) throws SQLException {
		PreparedStatement stmtx= null;
		try {
			stmtx= conn.prepareStatement(qry);
			stmtx.setString(1, s);
			stmtx.executeUpdate();
		} catch (SQLException e) {
			GenFunctions.LogErrMsg(e, conn, qry, "");
			throw e;
		} finally {
			stmtx.close();
		}
	}
	public static void close(Statement stmtx, ResultSet rsx) {
		if (rsx != null)
			try {
				rsx.close();
				rsx= null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (stmtx != null)
			try {
				stmtx.close();
				stmtx= null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	public static void saveRecord(Connection conn, String tableName, ArrayList values) throws SQLException, Exception {
		Statement stmt= null;
		ResultSet rset= null;
		int iNoofColumns= 0;
		String sColumnName= null;
		String sColumnValue= null;
		StringBuffer sColumnNameInQry= new StringBuffer();
		StringBuffer sColumnValueInQry= new StringBuffer();
		StringBuffer sColumnUpdateQry= new StringBuffer();
		StringBuffer sWhereQry= new StringBuffer();
		String updateValues= null;
		String whereValues= null;
		String updateQry= null;
		boolean quoteRequired= false;
		int coltype, isNull= 0;
		ArrayList pk= getPrimaryKeys(conn, tableName);
		try {
			stmt= conn.createStatement();
			rset= stmt.executeQuery("select * from  " + tableName + " fetch first 1 rows only with ur");
			ResultSetMetaData rsmeta= rset.getMetaData();
			iNoofColumns= rsmeta.getColumnCount();
			for (int i= 1; i <= iNoofColumns; i++) {
				sColumnName= rsmeta.getColumnName(i);
				sColumnValue= (String)values.get(i - 1);
				sColumnValue= (sColumnValue == null) ? "" : sColumnValue.trim();
				coltype= rsmeta.getColumnType(i);
				quoteRequired=
					!(coltype == Types.FLOAT
						|| coltype == Types.INTEGER
						|| coltype == Types.DECIMAL
						|| coltype == Types.DOUBLE
						|| coltype == Types.NUMERIC
						|| coltype == Types.REAL
						|| coltype == Types.SMALLINT);
				isNull= rsmeta.isNullable(i);
				if ((isNull == ResultSetMetaData.columnNullable)
					&& (coltype == Types.TIMESTAMP || coltype == Types.DATE || coltype == Types.CLOB || coltype == Types.BLOB))
					sColumnValue= "null";
				else if (isNull == ResultSetMetaData.columnNullable && !quoteRequired && sColumnValue.equals(""))
					sColumnValue= "null";
				else if (quoteRequired)
					sColumnValue= "'" + sColumnValue + "'";
				if (!pk.contains(sColumnName))
					sColumnUpdateQry.append(sColumnName + "=" + sColumnValue + ",");
				else
					sWhereQry.append(sColumnName + "=" + sColumnValue + " and ");
				if (i == 1) {
					sColumnNameInQry.append(sColumnName);
					sColumnValueInQry.append(sColumnValue);
				} else {
					sColumnNameInQry.append("," + sColumnName);
					sColumnValueInQry.append("," + sColumnValue);
				}
			}
			if (sColumnUpdateQry.length() > 1)
				updateValues= sColumnUpdateQry.substring(0, sColumnUpdateQry.length() - 1);
			if (sWhereQry.length() > 4)
				whereValues= sWhereQry.substring(0, sWhereQry.length() - 4);
			boolean found= countQry(conn, "select * from  " + tableName + " where " + whereValues + " with ur",true) > 0;
			String query= null;
			if (found)
				query= "update " + tableName + " set " + updateValues + " where " + whereValues;
			else
				query= "insert into " + tableName + " (" + sColumnNameInQry.toString() + ") values (" + sColumnValueInQry.toString() + ")";
			fireUpdate(conn, query);
		} finally {
			close(stmt, rset);
		}
	}
	public static Hashtable getTableValues(String query, Connection conn) throws SQLException, Exception {
		Hashtable ht= new Hashtable();
		Statement stmt= null;
		ResultSet rset= null;
		int iNoofColumns= 0;
		String sColumnName= "";
		String sColumnValue= "";
		ArrayList colnameList= new ArrayList();
		try {
			stmt= conn.createStatement();
			rset= stmt.executeQuery(query);
			System.out.println("TEXTQRY: " + query);
			ResultSetMetaData rsmeta= rset.getMetaData();
			iNoofColumns= rsmeta.getColumnCount();
			String tableName= rsmeta.getTableName(1);
			String schemaName= rsmeta.getSchemaName(1);
			String fulltable= tableName;
			Vector encryptedFields= new Vector();
			for (int i= 1; i <= iNoofColumns; i++)
				colnameList.add(rsmeta.getColumnName(i));
			System.out.println("TEXT: " + tableName + "\t" + schemaName + "\t" + encryptedFields);
			if (rset.next()) {
				for (int i= 0; i < iNoofColumns; i++) {
					sColumnName= (String)colnameList.get(i);
					if (sColumnName != null && (!sColumnName.equals("")))
						sColumnValue= rset.getString(sColumnName);
					if (sColumnValue == null)
						sColumnValue= "";
					System.out.println("COLUMN: " + sColumnName);
					ht.put(sColumnName.toLowerCase(), sColumnValue);
				}
			}
		} finally {
			close(stmt, rset);
		}
		return ht;
	}
	static ArrayList getPrimaryKeys(Connection conn, String tablename) throws SQLException {
		ArrayList pk= new ArrayList();
		ResultSet rsetdb= null;
		DatabaseMetaData dbmeta= null;
		dbmeta= conn.getMetaData();
		int idx= tablename.indexOf(".");
		rsetdb= dbmeta.getPrimaryKeys(null, tablename.substring(0, idx).toUpperCase(), tablename.substring(idx + 1).toUpperCase());
		while (rsetdb.next()) {
			pk.add(rsetdb.getString(4));
		}
		System.out.println(pk);
		return pk;
	}
	public static void copySchema(Connection conn, String source, String dest, Vector tablelist) throws SQLException {
		conn.commit();
		conn.setAutoCommit(false);
		try {
			for (int i= 0; i < tablelist.size(); i++) {
				String table= (String)tablelist.elementAt(i);
				System.out.println("deleteing old data");
				fireUpdate(conn, "delete from " + dest + "." + table);
				System.out.println("copy new data");
				fireUpdate(conn, "insert into " + dest + "." + table + " select * from " + source + "." + table);
			}
			conn.commit();
			System.out.println("SuccessFul");
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}
}
