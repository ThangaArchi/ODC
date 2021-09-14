package oem.edge.ets.fe;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2000 - 2001                                       */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/*																			 */
/*	File Name 	: 	ETSDbUtils												 */
/*	Release		:	3.10.1													 */
/*	Description	:	Has common SQL Utilities								 */
/*	Created By	: 	Sathish													 */
/*	Date		:	09/22/2003												 */
/*****************************************************************************/
/*****************************************************************************/
/*  Change Log 	: 	New File												 */
/*****************************************************************************/

/**
 * @author: Sathish
 */


import java.util.*;
import java.sql.*;
import oem.edge.common.*;


public class ETSDBUtils {
	
	// class version
	private static final String CLASS_VERSION = "3.2.3";

	/*
	Common method to close connection object.
	*/
	public static void close(Connection conn) {
	    if (conn != null) {
	        try {
	            conn.close();
	            conn = null;
	        } catch (SQLException x) {
	        	SysLog.log(SysLog.ERR,"ETSDBUtils:close()",x.toString());
	        }
	    }
	}
    

	/*
	Common method to close ResultSet object.
	*/
	public static void close(ResultSet rs) {
	    if (rs != null) {
	        try {
	            rs.close();
	            rs = null;
	        } catch (SQLException x) {
	        	SysLog.log(SysLog.ERR,"ETSDBUtils:close()",x.toString());
	        }
	    }
	}

	
	/*
	Common method to close Statement object.
	*/
	public static void close(Statement stmt) {
	    if (stmt != null) {
	        try {
	            stmt.close();
	            stmt = null;
	        } catch (SQLException x) {
	        	SysLog.log(SysLog.ERR,"ETSDBUtils:close()",x.toString());
	        }
	    }
	
	}

	/*
	Returns class version of the class.
	*/
	public static String getClassVersion() {
		return CLASS_VERSION;
	}

	
	/*
	Gets a new connection object.
	*/
	public static Connection getConnection() throws SQLException, Exception {
	
	    Connection connection = null;
	    DbConnect db = null;
	
	    try {
	    	
	        if (!Global.loaded) {
	            Global.Init();
	        }
	
            db = new DbConnect();
            db.makeConn(getDataSource());
            connection = db.conn;
	
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    	throw e;
	    } catch (Exception ex) {
	    	ex.printStackTrace();
            throw ex; 
	    }
	
	    return connection;
	}
	
	
	/*
	Gets a new connection object.
	*/
	public static Connection getConnection(String sDataSource) throws SQLException, Exception {
	
	    Connection connection = null;
	    DbConnect db = null;
	
	    try {
	
	        if (!Global.loaded) {
	            Global.Init();
	        }
	
            db = new DbConnect();
            db.makeConn(sDataSource);
            connection = db.conn;
	
	    } catch (SQLException e) {
	    	throw e;
	    } catch (Exception ex) {
            throw ex; 
	    }
	
	    return connection;
	}	
	
	
	private static String getDataSource() throws Exception {
		
		String sDataSource = "";
		
		try {
	
			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
			Enumeration e = rb.getKeys();
			
			sDataSource = rb.getString("ets.datasource").trim();
			if (sDataSource == null || sDataSource.trim().equals("")) {
				sDataSource = "";
			} else {
				sDataSource = sDataSource.trim();
			}		
			
		} catch (Exception e) {
			throw e;
		}
		
		return sDataSource;
	}
	public static String escapeString(String str) {
		return escapeString(str, 0xf423f);
	}
	public static String escapeString(String str, int length) {

		if (str == null)
			return "";
		if (str.length() > length)
			str = str.substring(0, length);
		StringTokenizer st = new StringTokenizer(str, "'");
		StringBuffer buffer = null;
		for (; st.hasMoreTokens(); buffer.append(st.nextToken()))
			if (buffer == null)
				buffer = new StringBuffer(str.length() + 20);
			else
				buffer.append("''");

		if (buffer == null)
			return str;
		else
			return buffer.toString();
	}


}


