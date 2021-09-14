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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.remoteviewer.vo.ProjectVO;
import oem.edge.ed.odc.remoteviewer.vo.ProjectWithServerVO;
import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;
//import oem.edge.ed.odc.utils.DB_Connection;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.PropertyTester;
import oem.edge.ed.odc.utils.SQLCode;
import oem.edge.ed.odc.utils.ValueObject;
import oem.edge.ed.odc.utils.ODCUtils;

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
public class ProjectDAO implements IProjectDAO{

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ProjectDAO.class);

	ValueObject projectVO = null;

	
	public ValueObject insert(ValueObject projectVO) throws Exception
	{
		logger.info("-> ProjectDAO: insert()");
		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ProjectVO objProjectVO = (ProjectVO) projectVO;
		objProjectVO.setSqlMessage("[ "+ objProjectVO.getProjectName() +" can not be created.....!!!. (please try again) ]" );
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();
			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.insert");
		
			String value = "val-";
			String is_active = "Y";
			ps = conn.prepareStatement(strSQL);
			
			String uniqId = ODCUtils.getUniqueId();
			String ts = String.valueOf( new Timestamp( System.currentTimeMillis() ) );
			
			ps.setString(1, uniqId );
			ps.setString(2, objProjectVO.getProjectName() );
			ps.setString(3, objProjectVO.getServerInstanceId() );
			ps.setString(4, "DONE" );
			ps.setString(5, is_active );
			ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()) );
			
			int x = ps.executeUpdate();
			if(x >= 1){
				objProjectVO.setSqlMessage("[ "+ objProjectVO.getProjectName() +" created successfully ]" );
			}else {
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be created.....!!!. (please try again) ]" );
			}

			objProjectVO.setProjectId( uniqId );
			objProjectVO.setProjectName( objProjectVO.getProjectName() );
			objProjectVO.setServerInstanceId( uniqId );
			objProjectVO.setStatus( "DONE" );
			objProjectVO.setIsActive( is_active );
			objProjectVO.setLastTimeStamp(  ts );
			
		} catch (SQLException ex) {
			logger.error(ex);
			ex.printStackTrace();
			objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be created.....!!!. (please try again) ]" );
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
		logger.info("<- ProjectDAO:insert");
		return objProjectVO;
	}
	









	public ValueObject deleteUserProject(ValueObject pValueObject) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ProjectVO objProjectVO = (ProjectVO) pValueObject;
		objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.deleteUserProject");
//strSQL = "DELETE FROM ODC.USER_PROJECT_REMOTEVIEWER WHERE PROJECT_ID=?";		
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, objProjectVO.getProjectId() );
			
			int x = ps.executeUpdate();
			if(x>=1){
				//objProjectVO = (ProjectVO) deleteProject(objProjectVO);
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" deleted successfully ]" );
			}else{
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
			}

			objProjectVO.setProjectName( objProjectVO.getProjectName() );

			// JV combining two methods for now, because the transaction stuff is mixing with Joe's transaction stuff

			if (ps != null) {
				ps.close();
			}

			sqlCode = PropertyTester.getInstance();

			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.delete");
		
			ps = conn.prepareStatement(strSQL);
			
			ps.setString(1, objProjectVO.getProjectId() );
			
			x = ps.executeUpdate();
			if(x>=1){
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" deleted successfully ]" );
			}else{
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
			}

			objProjectVO.setProjectName( objProjectVO.getProjectName() );




			
		} catch (SQLException ex) {
			logger.error(ex);
			ex.printStackTrace();
			objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
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
			return objProjectVO;
	}
	









	public ValueObject deleteProject(ValueObject pValueObject) throws Exception {

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ProjectVO objProjectVO = (ProjectVO) pValueObject;
		objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

			strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.delete");
		
			ps = conn.prepareStatement(strSQL);
			
			ps.setString(1, objProjectVO.getProjectId() );
			
			int x = ps.executeUpdate();
			if(x>=1){
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" deleted successfully ]" );
			}else{
				objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
			}

			objProjectVO.setProjectName( objProjectVO.getProjectName() );
		} catch (SQLException ex) {
			logger.error(ex);
			ex.printStackTrace();
			objProjectVO.setSqlMessage("[ "+objProjectVO.getProjectName() +" can not be deleted.....!!!. (please try again) ]" );
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
			return objProjectVO;
	}




	public Collection findAllProjects() throws Exception
	{
		logger.info("-> findAllProjects()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		Collection prjCollection = new ArrayList();
		PropertyTester sqlCode;
		ProjectWithServerVO objProjectServerVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ProjectDAO.allProjectServer");
//strSQL = "SELECT * FROM ODC.USER_PROJECT_REMOTEVIEWER    WHERE PROJECT_ID      IN (SELECT PROJECT_ID FROM ODC.APPLICATION_REMOTEVIEWER WHERE PROJECT_ID=?  AND IS_ACTIVE='Y')   FOR READ ONLY WITH UR";
			
			ps = conn.prepareStatement(strSQL);
//ps.setString(1, "Y");
			rs = ps.executeQuery();
			while(rs.next())
			{
				objProjectServerVO = new ProjectWithServerVO();
				
				objProjectServerVO.setProjectId( rs.getString("PROJECT_ID") );
				objProjectServerVO.setServerInstanceId(rs.getString("SERVER_INSTANCE_ID") );
				objProjectServerVO.setProjectName( rs.getString("PROJECT_NAME") );
				objProjectServerVO.setServerName( rs.getString("SERVER_NAME") );
				objProjectServerVO.setGridPath( rs.getString("GRID_PATH") );
				
				prjCollection.add( objProjectServerVO );
			}

		} catch (SQLException ex) {
			logger.error(ex);
			ex.printStackTrace();
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
					System.out.println(sqle);
				}
			}
			logger.info("<- findAllProjects()");
			return prjCollection;
	}
	

	/**
	 * @return
	 */
	public ValueObject getProjectVO() {
		return projectVO;
	}

	/**
	 * @param object
	 */
	public void setProjectVO(ValueObject object) {
		ProjectVO objProjectVO = (ProjectVO) projectVO;
	}


	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IProjectDAO#findByPrimaryKey(oem.edge.ed.odc.utils.ValueObject)
	 */
	public ValueObject findByPrimaryKey(ValueObject objValueObject) throws Exception {
			if (logger.isInfoEnabled()) {
				logger.info("-> findByPrimaryKey");
			}
					
			ProjectVO objProjectVO = (ProjectVO) objValueObject;
			ProjectVO anotherProjectVO = null;
			ArrayList projVOCollection = new ArrayList();
	
			// Prepare to connect to the DB.
			DBConnection dbconn = null;	
			Connection conn = null;
	
			PreparedStatement preparedStatement = null;
			ResultSet rs = null;
			try {

				// Get a connection to our main DB.
				dbconn = DBSource.getDBConnection("AMT");
				conn = dbconn.getConnection();
//				conn = DB_Connection.getConnection();

				PropertyTester sqlCode = PropertyTester.getInstance();
				
				String strSQL =	sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.findByPrimaryKey");
				preparedStatement = conn.prepareStatement(strSQL.toString());
				preparedStatement.setString(1, objProjectVO.getProjectId());

				rs = preparedStatement.executeQuery();

				boolean recordExists = false;
			
				while (rs.next()) {
					recordExists = true;
					anotherProjectVO = new ProjectVO();
					anotherProjectVO.setProjectId( rs.getString("PROJECT_ID") );
					anotherProjectVO.setProjectName( rs.getString("PROJECT_NAME") );
					anotherProjectVO.setServerInstanceId( rs.getString("SERVER_INSTANCE_ID") );
					anotherProjectVO.setStatus( rs.getString("STATUS") );
					anotherProjectVO.setIsActive( rs.getString("IS_ACTIVE") );
					//anotherProjectVO.setLastTimeStamp( rs.getString("LAST_TIMESTAMP") );
					
					projVOCollection .add(anotherProjectVO);
				}
				if(!recordExists)
				{
					logger.info("No record Found...................!!!!");
				}

			} catch (SQLException ex) {			
				logger.error(ex);
				ex.printStackTrace();
			} finally {
				try {
	
					if (rs != null) {
						rs.close();
						rs = null;
					}
					if (preparedStatement != null) {
						preparedStatement.close();
						preparedStatement = null;
					}
					if (conn != null) {
						dbconn.returnConnection(conn);
						conn = null;
					}
				} catch (SQLException ex) {
					logger.error(ex);
					ex.printStackTrace();
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- findByPrimaryKey");
			}
			return anotherProjectVO;
	}
}