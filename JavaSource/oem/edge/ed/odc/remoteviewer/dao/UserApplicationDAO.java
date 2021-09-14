/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import oem.edge.ed.odc.remoteviewer.actions.UserApplicationForm;
import oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO;
import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;
//import oem.edge.ed.odc.utils.DB_Connection;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ODCUtils;
import oem.edge.ed.odc.utils.PropertyTester;

import org.apache.commons.logging.Log;


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
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserApplicationDAO implements IUserApplicationDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(UserApplicationDAO.class);

	public Collection findAllUserApplication() throws Exception
	{
		logger.info("-> findAllUserApplication()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userAppCollection = new ArrayList();
		UserApplicationVO objUserApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();
			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.findAllUserApplication");
			ps = conn.prepareStatement(strSQL);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserApplicationVO = new UserApplicationVO();

				objUserApplicationVO.setUserName( rs.getString("USER_NAME") );
				objUserApplicationVO.setApplicationId(  rs.getString("APPL_ID") );
				objUserApplicationVO.setApplicationName( rs.getString("APPL_NAME") );
				objUserApplicationVO.setServerName( rs.getString("SERVER_NAME") );
				objUserApplicationVO.setApplicationPath( rs.getString("APPL_PATH") );
				objUserApplicationVO.setIdPrefix( rs.getString("ID_PREFIX") );
				objUserApplicationVO.setNumberOfUsers( rs.getString("NUMBEROF_USERS") );
				objUserApplicationVO.setFileSystemType( rs.getString("FILESYSTEM_TYPE") );

				userAppCollection.add( objUserApplicationVO );
			}

		} catch (SQLException e1) {
			logger.info(e1.getMessage());
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		logger.info("<- findAllUserApplication()");
		return userAppCollection;
	}





	public Collection findSingleUserApplication(UserApplicationForm objUserApplicationForm) throws Exception
	{
		logger.info("-> findAllUserApplication()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userAppCollection = new ArrayList();
		UserApplicationVO objUserApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();
			String user_name = objUserApplicationForm.getUserName();
			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.findSingleUserApplication");
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, user_name);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserApplicationVO = new UserApplicationVO();

				objUserApplicationVO.setUserName( rs.getString("USER_NAME") );
				objUserApplicationVO.setApplicationId(  rs.getString("APPL_ID") );
				objUserApplicationVO.setApplicationName( rs.getString("APPL_NAME") );
				objUserApplicationVO.setServerName( rs.getString("SERVER_NAME") );
				objUserApplicationVO.setApplicationPath( rs.getString("APPL_PATH") );
				objUserApplicationVO.setIdPrefix( rs.getString("ID_PREFIX") );
				objUserApplicationVO.setNumberOfUsers( rs.getString("NUMBEROF_USERS") );
				objUserApplicationVO.setFileSystemType( rs.getString("FILESYSTEM_TYPE") );

				userAppCollection.add( objUserApplicationVO );
			}
		} catch (SQLException e1) {
			logger.info(e1.getMessage());
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		logger.info("<- findAllUserApplication()");
		return userAppCollection;
	}





	public Collection findSingleUserNonApplication(UserApplicationForm objUserApplicationForm) throws Exception
	{
		logger.info("-> findSingleUserNonApplication()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userNonAppCollection = new ArrayList();
		UserApplicationVO objUserApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();
			String user_name = objUserApplicationForm.getUserName();
			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.findSingleUserNonApplication");
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, user_name);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserApplicationVO = new UserApplicationVO();

				//objUserApplicationVO.setUserName( rs.getString("USER_NAME") );
				objUserApplicationVO.setApplicationId(  rs.getString("APPL_ID") );
				objUserApplicationVO.setApplicationName( rs.getString("APPL_NAME") );
				objUserApplicationVO.setServerName( rs.getString("SERVER_NAME") );
				objUserApplicationVO.setApplicationPath( rs.getString("APPL_PATH") );
				objUserApplicationVO.setIdPrefix( rs.getString("ID_PREFIX") );
				objUserApplicationVO.setNumberOfUsers( rs.getString("NUMBEROF_USERS") );
				objUserApplicationVO.setFileSystemType( rs.getString("FILESYSTEM_TYPE") );

				userNonAppCollection.add( objUserApplicationVO );
			}
		} catch (SQLException e1) {
			logger.info(e1.getMessage());
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		logger.info("<- findSingleUserNonApplication()");
		return userNonAppCollection;
	}





	public Collection findAllUserOnly() throws Exception
	{
		logger.info("-> findAllUserOnly()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userAppCollection = new ArrayList();
		UserApplicationVO objUserApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.findAllUserOnly");

			ps = conn.prepareStatement(strSQL);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserApplicationVO = new UserApplicationVO();

				objUserApplicationVO.setUserName( rs.getString("USER_NAME") );

				userAppCollection.add( objUserApplicationVO );
			}

		} catch (SQLException e1) {
			logger.info(e1.getMessage());
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		logger.info("<- findAllUserOnly()");
		return userAppCollection;
	}
	
	
	




	public Collection findSingleUserNonApplication() throws Exception {
		// Auto-generated method stub
		return null;
	}





	public UserApplicationVO insert(UserApplicationVO objUserApplicationVO_p) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		UserApplicationVO objUserApplicationVO = (UserApplicationVO) objUserApplicationVO_p;
		objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be add. (this application may not exist. please try again...)");
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.insert");
		
			ps = conn.prepareStatement(strSQL);
			
			String uniqId = ODCUtils.getUniqueId();
			String ts = String.valueOf( new Timestamp( System.currentTimeMillis() ) );
			String is_active = "Y";
			
			ps.setString(1, objUserApplicationVO.getUserName() );
			ps.setString(2, objUserApplicationVO.getApplicationId() );
			ps.setString(3, "DONE" );
			ps.setString(4, is_active );
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()) );
			
			int x = ps.executeUpdate();
			if(x >= 1)
			{
				objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] has been added successfully"); 
			}
			else
			{
				objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be added. (this application may not exist. please try again)");
			}
		} catch (SQLException e1) {
			logger.info(e1.getMessage());
			objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be add. (this application may not exist. please try again...)");
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		return objUserApplicationVO;
	}


	public UserApplicationVO deleteUserApplication(UserApplicationVO objUserApplicationVO_p) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		UserApplicationVO objUserApplicationVO = (UserApplicationVO) objUserApplicationVO_p;
		objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be deleted. [this application may not exist. please try again]");
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserApplicationDAO.delete");
//strSQL = "DELETE FROM ODC.USER_APPL_REMOTEVIEWER WHERE USER_NAME=? AND APPL_ID=?";
		
			ps = conn.prepareStatement(strSQL);
			
			String uniqId = ODCUtils.getUniqueId();
			String ts = String.valueOf( new Timestamp( System.currentTimeMillis() ) );
			String is_active = "Y";
			
			ps.setString(1, objUserApplicationVO.getUserName() );
			ps.setString(2, objUserApplicationVO.getApplicationId() );
			
			int x = ps.executeUpdate();
			if(x >= 1)
			{
				objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] has been deleted successfully"); 
			}
			else
			{
				objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be deleted. (this application may not exist. please try again)");
			}
		} catch (SQLException e1) {
			objUserApplicationVO.setSqlMessage("Application ["+ objUserApplicationVO.getApplicationName() +"] can not be deleted. [this application may not exist. please try again]");
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					dbconn.returnConnection(conn);
				}
			} catch (SQLException ex) {
				logger.error(ex);
				ex.printStackTrace();
			}
		}
		return objUserApplicationVO;
	}
}