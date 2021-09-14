/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import oem.edge.ed.odc.rmviewer.actions.UserProjectForm;
import oem.edge.ed.odc.rmviewer.vo.UserProjectVO;
import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;
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
public class UserProjectDAO implements IUserProjectDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(UserProjectDAO.class);

	public Collection findAllUserProject() throws Exception
	{
		logger.info("-> findAllUserProject()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userPrjoectCollection = new ArrayList();
		UserProjectVO objUserProjectVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();
			//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.findAllUserProject");
			ps = conn.prepareStatement(strSQL);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserProjectVO = new UserProjectVO();

					/*				objUserProjectVO.setUserName( rs.getString("USER_NAME") );
									objUserProjectVO.setApplicationId(  rs.getString("APPL_ID") );
									objUserProjectVO.setApplicationName( rs.getString("APPL_NAME") );
									objUserProjectVO.setServerName( rs.getString("SERVER_NAME") );
									objUserProjectVO.setApplicationPath( rs.getString("APPL_PATH") );
									objUserProjectVO.setIdPrefix( rs.getString("ID_PREFIX") );
									objUserProjectVO.setNumberOfUsers( rs.getString("NUMBEROF_USERS") );
									objUserProjectVO.setFileSystemType( rs.getString("FILESYSTEM_TYPE") );
					*/
				userPrjoectCollection.add( objUserProjectVO );
			}

		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
		}
		logger.info("<- findAllUserProject()");
		return userPrjoectCollection;
	}





	public Collection findSingleUserProject(UserProjectForm objUserProjectForm) throws Exception
	{
		logger.info("-> findAllUserProject()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userPrjoectCollection  = new ArrayList();
		UserProjectVO objUserProjectVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();
			String user_name = objUserProjectForm.getUserName();

//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.findSingleUserProject");
//strSQL = "SELECT  USRPRJ.USER_NAME,  PRJ.PROJECT_ID, PRJ.PROJECT_NAME,  PRJ.SERVER_INSTANCE_ID, SRV.SERVER_NAME, SRV.GRID_PATH    FROM ODC.USER_PROJECT_REMOTEVIEWER USRPRJ, ODC.PROJECT_REMOTEVIEWER PRJ, ODC.SERVERS_REMOTEVIEWER SRV  WHERE USRPRJ.PROJECT_ID = PRJ.PROJECT_ID AND USER_NAME=? AND PRJ.SERVER_INSTANCE_ID=SRV.SERVER_INSTANCE_ID FOR READ ONLY WITH UR";
//strSQL = "SELECT  PRJ.PROJECT_ID,    PRJ.PROJECT_NAME,   PRJ.SERVER_INSTANCE_ID, SRV.SERVER_NAME, SRV.GRID_PATH FROM ODC.PROJECT_REMOTEVIEWER PRJ, ODC.SERVERS_REMOTEVIEWER SRV WHERE PRJ.PROJECT_ID IN ( SELECT PROJECT_ID FROM ODC.USER_PROJECT_REMOTEVIEWER WHERE USER_NAME=?) AND PRJ.SERVER_INSTANCE_ID = SRV.SERVER_INSTANCE_ID  FOR READ ONLY WITH UR";

			ps = conn.prepareStatement(strSQL);
			ps.setString(1, user_name);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserProjectVO = new UserProjectVO();

				objUserProjectVO.setUserName( user_name );
				objUserProjectVO.setProjectId( rs.getString("PROJECT_ID") );
				objUserProjectVO.setProjectName( rs.getString("PROJECT_NAME") );
				objUserProjectVO.setServerInstanceId( rs.getString("SERVER_INSTANCE_ID") );
				objUserProjectVO.setServerName( rs.getString("SERVER_NAME") );
				objUserProjectVO.setGridPath( rs.getString("GRID_PATH") );
								
				userPrjoectCollection .add( objUserProjectVO );
			}
		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
		}
		logger.info("<- findAllUserProject()");
		return userPrjoectCollection ;
	}





	public Collection findSingleUserNonProject(UserProjectForm objUserProjectForm) throws Exception
	{
		logger.info("-> findSingleUserNonProject()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		PropertyTester sqlCode;
		Collection userNonAppCollection = new ArrayList();
		UserProjectVO objUserProjectVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();
			String user_name = objUserProjectForm.getUserName();

//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.findSingleUserNonProject");
//strSQL = "SELECT PRJ.PROJECT_ID, PRJ.PROJECT_NAME, PRJ.SERVER_INSTANCE_ID FROM ODC.PROJECT_REMOTEVIEWER PRJ	WHERE PRJ.PROJECT_ID NOT IN 	     ( SELECT PROJECT_ID FROM ODC.USER_PROJECT_REMOTEVIEWER WHERE USER_NAME=? ) FOR READ ONLY WITH UR";
//strSQL = "SELECT    PRJ.PROJECT_ID,    PRJ.PROJECT_NAME,   PRJ.SERVER_INSTANCE_ID, SRV.SERVER_NAME, SRV.GRID_PATH FROM ODC.PROJECT_REMOTEVIEWER PRJ, ODC.SERVERS_REMOTEVIEWER SRV WHERE PRJ.PROJECT_ID NOT IN ( SELECT PROJECT_ID FROM ODC.USER_PROJECT_REMOTEVIEWER WHERE USER_NAME=?) AND PRJ.SERVER_INSTANCE_ID = SRV.SERVER_INSTANCE_ID  FOR READ ONLY WITH UR";

			ps = conn.prepareStatement(strSQL);
			ps.setString(1, user_name);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserProjectVO = new UserProjectVO();

				objUserProjectVO.setUserName( user_name );
				objUserProjectVO.setProjectId( rs.getString("PROJECT_ID") );
				objUserProjectVO.setProjectName( rs.getString("PROJECT_NAME") );
				objUserProjectVO.setServerInstanceId( rs.getString("SERVER_INSTANCE_ID") );
				objUserProjectVO.setServerName( rs.getString("SERVER_NAME") );
				objUserProjectVO.setGridPath( rs.getString("GRID_PATH") );

				userNonAppCollection.add( objUserProjectVO );
			}
		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				logger.error(sqle);
				sqle.printStackTrace();
			}
		}
		logger.info("<- findSingleUserNonProject()");
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
		Collection userPrjoectCollection  = new ArrayList();
		UserProjectVO objUserProjectVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();

//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.findAllUserOnly");
//strSQL = "SELECT DISTINCT (USER_NAME) FROM ODC.USER_PROJECT_REMOTEVIEWER FOR READ ONLY WITH UR";
			
			ps = conn.prepareStatement(strSQL);
			rs = ps.executeQuery();
			while(rs.next())
			{
				objUserProjectVO = new UserProjectVO();

				objUserProjectVO.setUserName( rs.getString("USER_NAME") );

				userPrjoectCollection .add( objUserProjectVO );
			}

		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle1) {
				logger.error(sqle1);
				sqle1.printStackTrace();
			}
		}
		logger.info("<- findAllUserOnly()");
		return userPrjoectCollection ;
	}
	
	
	




	public Collection findSingleUserNonProject() throws Exception {
		// Auto-generated method stub
		return null;
	}





	public UserProjectVO insert(UserProjectVO objUserProjectVO_p) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		UserProjectVO objUserProjectVO = (UserProjectVO) objUserProjectVO_p;
		objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be add. (this Project may not exist. please try again...)");
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();

//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.insert");
//strSQL = "INSERT INTO ODC.USER_PROJECT_REMOTEVIEWER VALUES(?, ?, ?, ?, ?)";		
			ps = conn.prepareStatement(strSQL);
			
			String uniqId = ODCUtils.getUniqueId();
			String ts = String.valueOf( new Timestamp( System.currentTimeMillis() ) );
			String is_active = "Y";
			
			ps.setString(1, objUserProjectVO.getUserName() );
			ps.setString(2, objUserProjectVO.getProjectId() );
			ps.setString(3, "DONE" );
			ps.setString(4, is_active );
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()) );
			
			int x = ps.executeUpdate();
			if(x >= 1)
			{
				objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] has been added successfully"); 
			}else{
				objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be added. (this Project may not exist. please try again)");
			}
		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
			objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be add. (this Project may not exist. please try again...)");
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
		}
		return objUserProjectVO;
	}


	public UserProjectVO deleteUserProject(UserProjectVO objUserProjectVO_p) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		UserProjectVO objUserProjectVO = (UserProjectVO) objUserProjectVO_p;
		objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be deleted. [this Project may not exist. please try again]");
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//sqlCode = PropertyTester.getInstance();

//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCUserProjectDAO.delete");
//strSQL = "DELETE FROM ODC.USER_PROJECT_REMOTEVIEWER WHERE USER_NAME=? AND PROJECT_ID=?";
		
			ps = conn.prepareStatement(strSQL);
			
			String uniqId = ODCUtils.getUniqueId();
			String ts = String.valueOf( new Timestamp( System.currentTimeMillis() ) );
			String is_active = "Y";
			
			ps.setString(1, objUserProjectVO.getUserName() );
			ps.setString(2, objUserProjectVO.getProjectId() );
			
			int x = ps.executeUpdate();
			if(x >= 1)
			{
				objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] has been deleted successfully"); 
//				objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be deleted. [this Project may not exist. please try again]");
			} else {
				objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be deleted. (this Project may not exist. please try again)");
			}
		} catch (SQLException e1) {
			logger.error(e1);
			e1.printStackTrace();
			objUserProjectVO.setSqlMessage("Project ["+ objUserProjectVO.getProjectName() +"] can not be deleted. [this Project may not exist. please try again]");
		}
		finally
		{
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
		}
		return objUserProjectVO;
	}
}