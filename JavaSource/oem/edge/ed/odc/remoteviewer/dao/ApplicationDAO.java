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

import oem.edge.ed.odc.remoteviewer.actions.ApplicationForm;
import oem.edge.ed.odc.remoteviewer.vo.ApplicationVO;
import oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO;
//import oem.edge.ed.odc.utils.DB_Connection;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ODCUtils;
import oem.edge.ed.odc.utils.PropertyTester;
import oem.edge.ed.odc.utils.ValueObject;
import org.apache.commons.logging.Log;

import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;


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
public class ApplicationDAO implements IApplicationDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ApplicationDAO.class);


	public static void main(String[] args) {
	}

	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#insert(oem.edge.ed.odc.utils.ValueObject)
	 */
	public ApplicationVO insert(ValueObject objODCApplicationVO) {
		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = (ApplicationVO) objODCApplicationVO;
		try {
			
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCApplicationDAO.insert");
//strSQL = "INSERT INTO ODC.APPLICATION_REMOTEVIEWER VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
			String value = "val-";
			String is_active = "Y";
			ps = conn.prepareStatement(strSQL);
			
			objApplicationVO.setApplicationId( ODCUtils.getUniqueId() );
			objApplicationVO.setStatus( "DONE" );
			objApplicationVO.setIsActive( is_active );
			objApplicationVO.setLastTimeStamp( String.valueOf( new Timestamp( System.currentTimeMillis()) )  );
			
			ps.setString(1, objApplicationVO.getApplicationId() );
			ps.setString(2, objApplicationVO.getApplicationName() );
			ps.setString(3, objApplicationVO.getServerName() );
			ps.setString(4, objApplicationVO.getApplicationPath() );
			ps.setString(5, objApplicationVO.getIdPrefix() );
			ps.setInt(6, (Integer.parseInt(objApplicationVO.getNumberOfUsers()) ) );
			ps.setString(7, objApplicationVO.getFileSystemType() );
			ps.setString(8, objApplicationVO.getStatus() );
			ps.setString(9, objApplicationVO.getIsActive() );
			ps.setTimestamp(10, (Timestamp.valueOf(objApplicationVO.getLastTimeStamp()) ) );
			
			int x = ps.executeUpdate();
			System.out.println("~~~~~~~~~~>"+x);
			
			if(x >= 1){
				objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" created successfully ]" );
			}else {
				objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be created.....!!!. (please try again) ]" );
			}

		} catch (SQLException e1) {
			System.out.println("~~~~>"+e1);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be created.....!!!. (please try again) ]" );
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("~~~~>"+e);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be created.....!!!. (please try again) ]" );
			e.printStackTrace();
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
		return objApplicationVO;
	}
	
	










	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#deleteApplication(oem.edge.ed.odc.utils.ValueObject)
	 */
	public ApplicationVO deleteApplication(ValueObject objODCApplicationVO) {
		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = (ApplicationVO) objODCApplicationVO;
		objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" deleted successfully ]" );
		try {
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection(); 
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCApplicationDAO.delete");
//strSQL = "DELETE FROM ODC.APPLICATION_REMOTEVIEWER WHERE APPL_ID=?";
		
			ps = conn.prepareStatement(strSQL);
			
			ps.setString(1, objApplicationVO.getApplicationId() );

			int x = ps.executeUpdate();
			System.out.println("~~~~~~~>"+x);
			
			if(x >= 1){
				objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" deleted successfully ]" );
			}else {
				objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			}

		} catch (SQLException e1) {
			System.out.println("~~~~>"+e1);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("~~~~>"+e);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			e.printStackTrace();
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
		return objApplicationVO;
	}




	










	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#deleteApplication(oem.edge.ed.odc.utils.ValueObject)
	 */
	public ApplicationVO deleteUserApplication(ValueObject objODCApplicationVO) {
		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = (ApplicationVO) objODCApplicationVO;
		objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" deleted successfully ]" );
		try {
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCApplicationDAO.deleteUserApplication");
//strSQL = "DELETE FROM ODC.USER_APPL_REMOTEVIEWER WHERE APPL_ID=?";
		
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, objApplicationVO.getApplicationId() );
			
			int x = ps.executeUpdate();
			System.out.println("~~~~~~~~>"+x);
			
			if(x >= 1){
				//objApplicationVO = deleteApplication( objApplicationVO );
				objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" deleted successfully ]" );
			}else {
				objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			}
			
			objApplicationVO.setApplicationName( objApplicationVO.getApplicationName() );

			// thanga combining two methods for now, because the transaction stuff is mixing with Joe's transaction stuff

			if (ps != null) {
				ps.close();
			}

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCApplicationDAO.delete");
//	strSQL = "DELETE FROM ODC.APPLICATION_REMOTEVIEWER WHERE APPL_ID=?";
					
			ps = conn.prepareStatement(strSQL);
						
			ps.setString(1, objApplicationVO.getApplicationId() );

			int y = ps.executeUpdate();
			System.out.println("~~~~~~~>"+y);
						
			if(y >= 1){
				objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" deleted successfully ]" );
			}else {
				objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			}
			objApplicationVO.setApplicationName( objApplicationVO.getApplicationName() );


		} catch (SQLException e1) {
			System.out.println("~~~~>"+e1);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("~~~~>"+e);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be deleted.....!!!. (please try again) ]" );
			e.printStackTrace();
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
		return objApplicationVO;
	}













	
	










	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#editApplication(oem.edge.ed.odc.utils.ValueObject)
	 */
	public ApplicationVO editUserApplication(ValueObject objODCApplicationVO) {
		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;

		PreparedStatement ps = null;
		String strSQL="";
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = (ApplicationVO) objODCApplicationVO;
		objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" edited successfully ]" );
		try {
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCApplicationDAO.editApplication");
//strSQL = "UPDATE ODC.APPLICATION_REMOTEVIEWER SET APPL_NAME=?, SERVER_NAME=?, APPL_PATH=?, ID_PREFIX=?, NUMBEROF_USERS=?, FILESYSTEM_TYPE=? WHERE APPL_ID=?";
		
			ps = conn.prepareStatement(strSQL);
			
			ps.setString(1, objApplicationVO.getApplicationName() );
			ps.setString(2, objApplicationVO.getServerName() );
			ps.setString(3, objApplicationVO.getApplicationPath() );
			ps.setString(4, objApplicationVO.getIdPrefix() );
			ps.setString(5, objApplicationVO.getNumberOfUsers() );
			ps.setString(6, objApplicationVO.getFileSystemType() );

			ps.setString(7, objApplicationVO.getApplicationId() );

			int x = ps.executeUpdate();
			System.out.println("~~~~~~~>"+x);
			
			if(x >= 1){
				objApplicationVO.setSqlMessage("[ "+ objApplicationVO.getApplicationName() +" edited successfully ]" );
			}else {
				objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be edited.....!!!. (please try again) ]" );
			}

		} catch (SQLException e1) {
			System.out.println("~~~~>"+e1);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be edited.....!!!. (please try again) ]" );
			e1.printStackTrace();
		} catch(Exception e){
			System.out.println("~~~~~>"+e);
			objApplicationVO.setSqlMessage("[ "+objApplicationVO.getApplicationName() +" can not be edited.....!!!. (please try again) ]" );
			e.printStackTrace();
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
		return objApplicationVO;
	}














	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#findAllApplications()
	 */
	public Collection findAllApplications() {
		logger.info("-> findAllApplications()");


		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		Collection appCollection = new ArrayList();
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ApplicationDAO.allApplication");
			
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, "Y");
			rs = ps.executeQuery();
			while(rs.next())
			{
				objApplicationVO = new ApplicationVO();
				objApplicationVO.setApplicationId( rs.getString("APPL_ID") );
				objApplicationVO.setApplicationName( rs.getString("APPL_NAME") );
				objApplicationVO.setServerName( rs.getString("SERVER_NAME") );
				objApplicationVO.setApplicationPath( rs.getString("APPL_PATH") );
				objApplicationVO.setIdPrefix( rs.getString("ID_PREFIX") );
				objApplicationVO.setNumberOfUsers( rs.getString("NUMBEROF_USERS") );
				objApplicationVO.setFileSystemType( rs.getString("FILESYSTEM_TYPE") );
				objApplicationVO.setStatus( rs.getString("STATUS") );
				objApplicationVO.setIsActive( rs.getString("IS_ACTIVE") );
				objApplicationVO.setLastTimeStamp( rs.getString("LAST_UPDATE") );

				appCollection.add( objApplicationVO );
			}

		} catch (SQLException e1) {
			System.out.println("~~~~>"+e1);
			System.out.println(e1);
			e1.printStackTrace();
		} catch (Exception e1) {
			System.out.println("~~~~>"+e1);
			e1.printStackTrace();
		}finally {
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
		logger.info("<- findAllApplications()");
		return appCollection;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO#findSingleUserApplication(oem.edge.ed.odc.remoteviewer.actions.ApplicationForm)
	 */
	public Collection findSingleApplication_User(ApplicationForm objApplicationForm) {
		logger.info("-> findSingleUserApplication()");


		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		Collection userAppCollection = new ArrayList();
		PropertyTester sqlCode;
		ApplicationVO objApplicationVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();
//			conn = DB_Connection.getConnection();

			sqlCode = PropertyTester.getInstance();

strSQL = sqlCode.getSQLStatement("ODC.DAO.ApplicationDAO.allUserApplication");
//strSQL = "SELECT * FROM ODC.USER_APPL_REMOTEVIEWER    WHERE APPL_ID      IN (SELECT APPL_ID FROM ODC.APPLICATION_REMOTEVIEWER WHERE APPL_ID=?  AND IS_ACTIVE='Y')   FOR READ ONLY WITH UR";

			ps = conn.prepareStatement(strSQL);
			ps.setString(1, objApplicationForm.getApplicationId() );
			rs = ps.executeQuery();
			while(rs.next())
			{
				objApplicationVO = new ApplicationVO();
				objApplicationVO.setUserName( rs.getString("USER_NAME") );

				userAppCollection.add( objApplicationVO );
			}

		} catch (SQLException e1) {
			System.out.println(e1);
			e1.printStackTrace();
		} catch (Exception e1) {
			System.out.println(e1);
			e1.printStackTrace();
		}finally {
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
		logger.info("<- findSingleUserApplication()");
		return userAppCollection;
	}

}
