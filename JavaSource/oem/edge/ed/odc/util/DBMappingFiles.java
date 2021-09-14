/*
 * Created on Mar 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import oem.edge.ed.util.SearchEtc;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DBMappingFiles extends MappingFiles {
	/**
	 * @param props
	 */
	public DBMappingFiles(ReloadingProperty props) {
		super(props);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ed.odc.util.MappingFiles#getHostingMachines(java.lang.String, java.lang.String)
	 */
	public Vector getHostingMachines(String uname, String company) {
		Vector result = super.getHostingMachines(uname, company);
		boolean addedEntry = false;

		if (result == null) result = new Vector();

		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sql = null;

		try {
			System.out.println("DBMappingFiles: query for uname " + uname);
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			// Prepare to query the user's applications.
			sql = new StringBuffer("select a.appl_name,a.server_name from ");
			sql.append("edesign.application_remoteviewer a,edesign.user_appl_remoteviewer u ");
			sql.append("where u.user_name=? and u.appl_id=a.appl_id");
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,uname);

			// Query the user's applications.
			ResultSet rs = dbconn.executeQuery(pstmt);
			while (rs.next()) {
				String app = rs.getString(1);
				String host = rs.getString(2);
			System.out.println("DBMappingFiles: app " + app + " host " + host);
				if (app != null && host != null) {
					addedEntry = true;
					result.addElement(app + "@" + host);
				}
			}
		}
		catch (Exception e) {
			System.out.println("DBMappingFiles.getHostingMachines: DB Processing Error - " + e.getMessage());
			e.printStackTrace();

			dbconn.destroyConnection(conn);
			conn = null;
			pstmt = null;
		}
		finally {
			dbconn.returnConnection(conn);

			if (pstmt != null) {
				try {
					pstmt.close();
				}
				catch(Exception e) {
				}
			}
		}

		if (addedEntry) {
			SearchEtc.sortVector(result,true);
		}

		return result;
	}

}
