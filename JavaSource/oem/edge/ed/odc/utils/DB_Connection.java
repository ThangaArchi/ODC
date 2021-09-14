/*
 * Created on Mar 16, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;


import java.util.Properties;


/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DB_Connection {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	

	private static Log logger = ODCLogger.getLogger(DB_Connection.class);

//	public  static final String DATASOURCE = "AMT";
	public  static final String DATASOURCE = "etsds";
	public  static final String JNDI_LOOKUP = "jdbc/"+DATASOURCE ;

	static InitialContext ic = null;
	static DataSource ds = null;
	static Connection con = null;
	static PreparedStatement ps = null;
	
	public DB_Connection(){
	}

	public static Connection getConnection(){
		try {
			ic = new InitialContext();
			ds =     (javax.sql.DataSource)ic.lookup( JNDI_LOOKUP );
			con = ds.getConnection();
		}
		catch (NamingException nam_ex)
		{
			logger.error(nam_ex);
			nam_ex.printStackTrace();
		}
		catch (SQLException sql_ex)
		{
			logger.error(sql_ex);
			sql_ex.printStackTrace();
		}
		catch(Exception e)
		{
			logger.error(e);
			e.printStackTrace();
		}
		return con;
	}


	public void insert()
	{
		String value = "val";
		String is_active = "Y";
		try {
String ws="insert into odc.project_remoteviewer values (?, ?, ?, ?, ?, ?)";
ps = con.prepareStatement(ws);

ps.clearParameters();
ps.setString(1, value+1 );
ps.setString(2, value+2 );
ps.setString(3, value+3 );
ps.setString(4, value+4 );
ps.setString(5, is_active );
ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()) );

int x = ps.executeUpdate();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	
	

	/*
	Common method to close connection object.
	*/
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException x) {
				logger.info( "ODCDBUtils:close()"+ x.toString() );
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
				logger.info( "ODCDBUtils:close()"+ x.toString() );
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
				logger.info( "ODCDBUtils:close()"+ x.toString() );
			}
		}

	}

	
	/*
	Returns class version of the class.
	*/
	public static String getClassVersion() {
		return CLASS_VERSION;
	}



	public static void main(String[] args) {
		System.out.println("Before...");
		Connection con = DB_Connection.getConnection();
		System.out.println("After...");
	}
}
