/*
 * Created on Mar 15, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import oem.edge.ed.odc.util.DBConnection;
import oem.edge.ed.odc.util.DBSource;

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GridAdminClient extends RemoteAdminClient {

	/**
	 * Create a GridAdminClient instance which connects to the specified host
	 * on the default port 9999.
	 * @param serverName name of remote host running the remote admin server
	 */
	public GridAdminClient(String serverName) {
		super(serverName);
	}
	/**
	 * Create a GridAdminClient instance which connects to the specified host
	 * on the specified port.
	 * @param serverName name of remote host running the remote admin server
	 * @param port port number of the remote admin server
	 */
	public GridAdminClient(String serverName, int port) {
		super(serverName, port);
	}
	/**
	 * Create a GridAdminClient instance which connects to the specified host
	 * on the specified port. The connection is strongly authenticated using the
	 * supplied key and trust stores.
	 * @param serverName name of remote host running the remote admin server
	 * @param port port number of the remote admin server
	 * @param keyStore store containing the client's key pair
	 * @param keyStorePW password required to open key store.
	 * @param trustStore store containing the trusted CA for the server's cert
	 * @param trustStorePW password required to open trust store.
	 */
	public GridAdminClient(String serverName, int port, String keyStore, String keyStorePW, String trustStore, String trustStorePW) {
		super(serverName, port, keyStore, keyStorePW, trustStore, trustStorePW);
	}

	/**
	 * Grants a user access to an application.
	 * 
	 * @param user name of the user to be granted.
	 * @param application name of the application to be granted.
	 * @return String null if successful, error message otherwise.
	 */
	public String grantApplicationToUser(String user, String application) {
		// Validate the arguments...
		if (user == null) {
			return "User name is not specified";
		}
		if (application == null) {
			return "Application name is not specified";
		}

		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = null;
		StringBuffer sql = null;

		try {
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			// Create the prepared statement.
			sql = new StringBuffer("insert into edesign.user_appl_remoteviewer ");
			sql.append("(user_name,appl_id,status,is_active,last_timestamp) values(?,");
			sql.append("(select appl_id from edesign.application_remoteviewer ");
			sql.append("where appl_name=? and server_name=?),NULL,'Y',?)");
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,user);
			pstmt.setString(2,application);
			pstmt.setString(3,serverName);
			pstmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));

			// Insert the user, application into the table.
			dbconn.executeUpdate(pstmt);
		}
		catch (Exception e) {
			result = "GridAdminClient.grantApplicationToUser: DB Processing Error - " + e.getMessage();
			System.out.println(result);
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

		return result;
	}
	/**
	 * Revokes a user's access to an application.
	 * 
	 * @param user name of the user to be revoked.
	 * @param application name of the application to be revoked.
	 * @return String null if successful, error message otherwise.
	 */
	public String revokeApplicationFromUser(String user, String application) {
		// Validate the arguments...
		if (user == null) {
			return "User name is not specified";
		}
		if (application == null) {
			return "Application name is not specified";
		}

		// Prepare to connect to the DB.
		DBConnection dbconn = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = null;
		StringBuffer sql = null;

		try {
			// Get a connection to our main DB.
			dbconn = DBSource.getDBConnection("AMT");
			conn = dbconn.getConnection();

			// Create the prepared statement.
			sql = new StringBuffer("delete from edesign.user_appl_remoteviewer ");
			sql.append("where user_name=? and appl_id=");
			sql.append("(select appl_id from edesign.application_remoteviewer ");
			sql.append("where appl_name=? and server_name=?)");
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,user);
			pstmt.setString(2,application);
			pstmt.setString(3,serverName);

			// Insert the user, application into the table.
			if (dbconn.executeUpdate(pstmt) < 1) {
				result = "User " + user + " did not have access to application " + application;
			}
		}
		catch (Exception e) {
			result = "GridAdminClient.revokeApplicationFromUser: DB Processing Error - " + e.getMessage();
			System.out.println(result);
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

		return result;
	}
	/**
	 * Deletes a user on the designated server.
	 * 
	 * @param name name of the user to delete.
	 * @param gridPath path to the grid directories.
	 * @return String null if successful, error message otherwise.
	 */
	public String deleteUser(String name, String gridPath) {
		// Perform the base operation.
		String result = super.deleteUser(name, gridPath);

		// If base operation was a success, then revoke all of the
		// applications granted to this user on the server.
		if (result == null) {
			// Prepare to connect to the DB.
			DBConnection dbconn = null;
			Connection conn = null;
			PreparedStatement pstmt = null;
			StringBuffer sql = null;

			try {
				// Get a connection to our main DB.
				dbconn = DBSource.getDBConnection("AMT");
				conn = dbconn.getConnection();

				// Prepare to delete the user's applications for the server.
				sql = new StringBuffer("delete from edesign.user_appl_remoteviewer where ");
				sql.append("user_name=? and appl_id=(select appl_id from edesign.application_remoteviewer ");
				sql.append("where server_name=?)");
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setString(1,name);
				pstmt.setString(2,serverName);
				
				// Delete the user's applications.
				dbconn.executeUpdate(pstmt);
			}
			catch (Exception e) {
				result = "GridAdminClient.deleteUser: DB Processing Error - " + e.getMessage();
				System.out.println(result);
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
		}

		return result;
	}
}
