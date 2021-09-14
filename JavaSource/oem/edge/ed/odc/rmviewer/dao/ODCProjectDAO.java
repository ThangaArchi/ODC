/*
 * Created on Mar 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.rmviewer.vo.ProjectVO;
import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;
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
public class ODCProjectDAO
{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ODCProjectDAO.class);

	PreparedStatement ps = null;
	String strSQL="";
	public ValueObject insert(ValueObject projectVO)
	{
		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;

		PropertyTester sqlCode;
		ProjectVO objProjectVO = (ProjectVO) projectVO;
		try {

			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			try {
				//     sqlCode = PropertyTester.getInstance();
				//    strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCProjectDAO.insert");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
 			String value = "val";
			String is_active = "Y";
			ps = conn.prepareStatement(strSQL);
			
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
			finally
			{
				try {
					if (ps != null) {
						ps.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException ex) {
					logger.error(ex);
					ex.printStackTrace();
				}
			}
			return objProjectVO;
	}
	

	public static void main(String[] args) {
		//ODCProjectDAO prj = new ODCProjectDAO();
		//prj.insert();
	}
}