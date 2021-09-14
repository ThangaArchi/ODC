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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.rmviewer.vo.ProjectWithServerVO;
import oem.edge.ed.odc.rmviewer.vo.ServerVO;
import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;
import oem.edge.ed.odc.utils.DB_Connection;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.PropertyTester;
import oem.edge.ed.odc.utils.ValueObject;

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
public class ServerDAO implements IServerDAO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ServerDAO.class);



	public static void main(String[] args) {
	}

	public ValueObject insert(ValueObject pValueObject) throws Exception {
		return pValueObject;
	}

	public ValueObject findByPrimaryKey(ValueObject objValueObject) throws Exception {
		return objValueObject;
	}

	public Collection findServersByProjectId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findServersByProjectName(ValueObject objSerVO) throws Exception {
			logger.info("-> findServersByProjectName()");
	
			// Prepare to connect to the DB.
			DBConnection dbconn = null;	
			Connection conn = null;
	
			PreparedStatement ps = null;
			ResultSet rs  = null;
			String strSQL="";
			Collection servCollection = new ArrayList();
			PropertyTester sqlCode;
			ServerVO anotherObjSerVO = (ServerVO) objSerVO;
			try {

				// Get a connection to our main DB.
				dbconn = DBSource.getDBConnection("AMT");
				conn = dbconn.getConnection();

				//     sqlCode = PropertyTester.getInstance();

				//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCServerDAO.findServersByProjectName");
				Statement stm = conn.createStatement();
				rs = stm.executeQuery(strSQL);

				while(rs.next())
				{
					anotherObjSerVO.setServerInstanceId( rs.getString("SERVER_INSTANCE_ID") );
					anotherObjSerVO.setServerName( rs.getString("SERVER_NAME") );
					anotherObjSerVO.setGridPath( rs.getString("GRID_PATH") );
					anotherObjSerVO.setStatus( rs.getString("STATUS") );
					anotherObjSerVO.setIsActive( rs.getString("IS_ACTIVE") );
					anotherObjSerVO.setLastTimeStamp( rs.getString("LAST_TIMESTAMP") );
					
					servCollection.add( anotherObjSerVO );
				}

			}catch (SQLException ex){
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
						conn.close();
					}
				} catch (SQLException ex)
				{
					logger.error(ex);
					ex.printStackTrace();
				}
			}
			logger.info("<- findServersByProjectName()");
			return servCollection;
	}










	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.rmviewer.dao.IServerDAO#findAllServer()
	 */
	public Collection findAllServer() throws Exception {
		logger.info("-> findAllServer()");

		// Prepare to connect to the DB.
		DBConnection dbconn = null;	
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs  = null;
		String strSQL="";
		Collection srvCollection = new ArrayList();
		PropertyTester sqlCode;
		ServerVO objServerVO = null;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			//     sqlCode = PropertyTester.getInstance();
			//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCServerDAO.findAllServer");
			ps = conn.prepareStatement(strSQL);
			ps.setString(1, "Y");
			rs = ps.executeQuery();
			while(rs.next())
			{
				objServerVO = new ServerVO();
				
				objServerVO.setServerInstanceId(rs.getString("SERVER_INSTANCE_ID") );
				objServerVO.setServerName( rs.getString("SERVER_NAME") );
				objServerVO.setGridPath( rs.getString("GRID_PATH") );
				
				srvCollection.add( objServerVO );
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
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println(sqle);
			}
		}
		logger.info("<- findAllServer()");
		return srvCollection;
	}
}