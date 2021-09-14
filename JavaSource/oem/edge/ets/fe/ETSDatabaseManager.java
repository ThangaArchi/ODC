/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

//FIX FIX FIX FIX EXPIRE DATE

package oem.edge.ets.fe;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EntitledStatic;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
//Raja comment 6249
//import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;

/**
 * CHANGE HISTORY >>>> 01/12/2004
 * CHANGES BY PHANI
 * 1. the following methods were made public   to be accessible by other sub-packages in ETS
 *     isUserInProject()
 *     getTopCats()
 *     getCat()
 * 2. a final static var was added to get dynamic CMVC extracted version
 *
 * Changes by sandra
 * 3. added ibm_only to
 * 		addcat()
 * 		updatecat()
 * 		updatedocprop()
 * 		getlatestdocs(for externals)
 * 		getprojectmembers checking amt tables
 * 4.new updatecat method with opt param
 *
 */

public class ETSDatabaseManager {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.84";
	public static final String VERSION = "1.84";

	// IMPORTANT - DO NOT CHANGE THESE VARIABLES - EVER !!!
	private static final int MAX_DOC_VERSIONS = 1000;
	private static final int STARTING_DOC_ID = 10000 * MAX_DOC_VERSIONS;
	private static final int MAXIMUM_DOC_ID = 99999 * MAX_DOC_VERSIONS;

	public static final char FALSE_FLAG = '0';
	public static final char TRUE_FLAG = '1';
	public static final char NOT_SET_FLAG = 'x';

	private static final int IS_LATEST_VERSION_FLAG = 0;
	private static final int HAS_PREVIOUS_VERSION_FLAG = 1;

	static {
		if (!Global.loaded)
			Global.Init();
	}

	//spn 0312 projid
	public static synchronized String[] addProjectMember(ETSUser user) throws SQLException, Exception {

		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return addProjectMember(user, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static synchronized String[] addProjectMember(ETSUser user, Connection conn) throws SQLException {
		if (!existsRoleID(user.getRoleId(), conn))
			return new String[] { "-1", "role_id does not exist" };

		if (!existsProjectID(user.getProjectId(), conn))
			return new String[] { "-2", "project_id does not exist" };

		else if (isUserInProject(user.getUserId(), user.getProjectId(), conn))
			return new String[] { "-3", "user_id is already in project" };

		String update = "insert into ETS.ETS_USERS (USER_ID, USER_ROLE_ID, USER_PROJECT_ID,USER_JOB,PRIMARY_CONTACT,LAST_USERID,LAST_TIMESTAMP) values (" + "'" + user.getUserId() + "'," + user.getRoleId() + "," + "'" + user.getProjectId() + "'," + "'" + user.getUserJob() + "'," + "'" + user.getPrimaryContact() + "'," + "'" + user.getLastUserId() + "'," + " current timestamp )";

		Statement statement = conn.createStatement();
		int rowCount = statement.executeUpdate(update);

		try {
			String insert = "insert into ETS.ETS_USER_WS (USER_ID, PROJECT_ID) values (" + "'" + user.getUserId() + "','" + user.getProjectId() + "')";

			statement.executeUpdate(insert);
		} catch (Exception e) {

		}

		statement.close();

		if (rowCount == 1)
			return new String[] { "0", "" };
		else if (rowCount == 0)
			return new String[] { "1", "unknown db2 problem" };
		else
			return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}


//	spn 0312 projid
	  public static synchronized String[] addProjectMemberWithStatus(ETSUser user) throws SQLException, Exception {

		  Connection conn = null;

		  try {
			  conn = ETSDBUtils.getConnection();
			  return addProjectMemberWithStatus(user, conn);
		  } catch (SQLException e) {
			  throw e;
		  } catch (Exception ex) {
			  throw ex;
		  } finally {
			  ETSDBUtils.close(conn);
		  }

	  }


//	spn 0312 projid
	  public static synchronized String[] addProjectMemberWithStatus(ETSUser user, Connection conn) throws SQLException {
		  if (!existsRoleID(user.getRoleId(), conn))
			  return new String[] { "-1", "role_id does not exist" };

		  if (!existsProjectID(user.getProjectId(), conn))
			  return new String[] { "-2", "project_id does not exist" };

		  else if (isUserInProject(user.getUserId(), user.getProjectId(), conn))
			  return new String[] { "-3", "user_id is already in project" };

		  String update = "insert into ETS.ETS_USERS (USER_ID, USER_ROLE_ID, USER_PROJECT_ID,USER_JOB,PRIMARY_CONTACT,LAST_USERID,LAST_TIMESTAMP,ACTIVE_FLAG) values ('" + user.getUserId() + "'," + user.getRoleId() + ",'" + user.getProjectId() + "', '" + user.getUserJob() + "','" + user.getPrimaryContact() + "','" + user.getLastUserId() + "', current timestamp,'"+user.getActiveFlag()+"' )";

		  Statement statement = conn.createStatement();
		  int rowCount = statement.executeUpdate(update);

		  try {
			  String insert = "insert into ETS.ETS_USER_WS (USER_ID, PROJECT_ID) values (" + "'" + user.getUserId() + "','" + user.getProjectId() + "')";

			  statement.executeUpdate(insert);
		  } catch (Exception e) {

		  }

		  statement.close();

		  if (rowCount == 1)
			  return new String[] { "0", "" };
		  else if (rowCount == 0)
			  return new String[] { "1", "unknown db2 problem" };
		  else
			  return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	  }

	//spn 0312 projid
	  public static synchronized String[] delProjectMember(String userid, String projectid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return delProjectMember(userid, projectid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static synchronized String[] delProjectMember(String userid, String projectid, Connection conn) throws SQLException, AMTException {

		if (!existsProjectID(projectid, conn))
			return new String[] { "-2", "project_id does not exist" };

		else if (!isUserInProjectForDel(userid, projectid, conn))
			return new String[] { "-3", "user_id is not in project" };

		//String update = "delete from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID = '" + projectid +"'";

		//PR#YLII653JU3 if delete from main-workspace remove from sub workspace too
		int count = Integer.parseInt(EntitledStatic.getValue(conn, "select count(*) from ets.ets_projects where parent_id = '" + projectid + "'"));
		String update = "";
		String update2 = "";
		if (count > 0) {
			update = "delete from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID ='" + projectid + "'";
			update2 = "delete from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID in (select project_id from ets.ets_projects where parent_id  ='" + projectid + "')";
		} else {
			update = "delete from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID ='" + projectid + "'";
		}

		Statement statement = conn.createStatement();
		int rowCount = statement.executeUpdate(update);
		int rowCount2 = 0;
		if (count > 0) {
			rowCount2 = statement.executeUpdate(update2);
		}

		try { //no sub projects in user_ws table
			String update3 = "delete from ETS.ETS_USER_WS where USER_ID = '" + userid + "' and PROJECT_ID ='" + projectid + "'";
			statement.executeUpdate(update3);
		} catch (Exception e) {
			e.printStackTrace();
		}

		statement.close();

		if (rowCount > 0 || rowCount2 > 0)
			return new String[] { "0", "" };
		else if (rowCount == 0)
			return new String[] { "1", "unknown db2 problem" };
		else
			return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}

	//spn 0312 projid
	public static synchronized String[] updateUserRole(String userid, String projectid, int newRoleId, String job, String updater) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return updateUserRole(userid, projectid, newRoleId, job, updater, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static synchronized String[] updateUserRole(String userid, String projectid, int newRoleId, String job, String updater, Connection conn) throws SQLException {

		if (!existsRoleID(newRoleId, conn))
			return new String[] { "-1", "role_id does not exist" };

		if (!existsProjectID(projectid, conn))
			return new String[] { "-2", "project_id does not exist" };

		else if (!isUserInProject(userid, projectid, conn))
			return new String[] { "-3", "user_id is not in project" };

		String update = "update ETS.ETS_USERS set USER_ROLE_ID = " + newRoleId + ",USER_JOB='" + job + "',LAST_USERID='" + updater + "',LAST_TIMESTAMP=current timestamp where USER_ID = '" + userid + "' and USER_PROJECT_ID = '" + projectid + "'";

		Statement statement = conn.createStatement();
		int rowCount = statement.executeUpdate(update);
		statement.close();

		if (rowCount == 1)
			return new String[] { "0", "" };
		else if (rowCount == 0)
			return new String[] { "1", "unknown db2 problem" };
		else
			return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}

	//spn 0312 projid
	public static boolean isUserInProject(String userid, String projectid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return isUserInProject(userid, projectid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static boolean isUserInProject(String userid, String projectid, Connection conn) throws SQLException {
		boolean exists = false;
		String query = "select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" + userid + "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '" + projectid + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();
		return exists;
	}

	public static boolean isUserInProjectForDel(String userid, String projectid, Connection conn) throws SQLException {
		boolean exists = false;
		String query = "select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID = '" + projectid + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();
		return exists;
	}

	public static boolean existsRoleID(int roleid, Connection conn) throws SQLException {
		boolean exists = false;
		String query = "select distinct ROLE_NAME from ETS.ETS_ROLES where ROLE_ID = " + roleid + " with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();
		return exists;
	}

	//spn 0312 projid
	public static boolean existsProjectID(String projectid, Connection conn) throws SQLException {
		boolean exists = false;
		String query = "select PROJECT_NAME from ETS.ETS_PROJECTS where PROJECT_ID = '" + projectid + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();
		return exists;
	}

	//spn 0312 projid
	public static Vector getAllProjectAdmins(String projectid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return getAllProjectAdmins(projectid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static Vector getAllProjectAdmins(String projectid, Connection conn) throws SQLException {

		//String query = "select USER_ID, USER_ROLE_ID, USER_PROJECT_ID from ETS.ETS_USERS where USER_PROJECT_ID = '" + projectid + "' and USER_ROLE_ID in (select ROLE_ID from ETS.ETS_ROLES where PROJECT_ID = '" + projectid + "' and PRIV_ID = 1 and PRIV_VALUE = 1)";
		String query = "select * from ETS.ETS_USERS where USER_PROJECT_ID = '" + projectid + "' and ACTIVE_FLAG='A' and USER_ROLE_ID in (select ROLE_ID from ETS.ETS_ROLES where PROJECT_ID = '" + projectid + "' and PRIV_ID = 1 and PRIV_VALUE = 1) with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		Vector admins = getUsers(rs);

		rs.close();
		statement.close();

		if (admins.size() > 0)
			return admins;
		else
			return null;

	}

	//assumes only 1 record is present in table not sure if this is correct
	//spn 0312 projid
	public static ETSUser getETSUser(String userid, String projectid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		//Connection connection = dbConnect.conn;
		Connection conn = null;
		ETSUser user = null;

		try {
			conn = ETSDBUtils.getConnection();
			user = getETSUser(userid, projectid, conn);
		} catch (SQLException e) {
			printErr("error= " + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}
			return user;
		}
	//spn 0312 projid
	public static ETSUser getETSUser(String userid, String projectid, Connection conn) throws SQLException {

		PreparedStatement getUserSt = conn.prepareStatement("select * from ETS.ETS_USERS u where u.user_id = ? and u.active_flag='A' and u.user_project_id = ? with ur");
		getUserSt.setString(1, userid);
		getUserSt.setString(2, projectid);
		ResultSet rs = getUserSt.executeQuery();
		ETSUser user = null;
		while (rs.next()) {
			user = getUser(rs);
		}

		rs.close();
		getUserSt.close();
		return user;
	}

	public static ETSUser getETSUserAll(String userid, String projectid, Connection conn) throws SQLException {

		PreparedStatement getUserSt = conn.prepareStatement("select * from ETS.ETS_USERS u where u.user_id = ? and u.user_project_id = ? with ur");
		getUserSt.setString(1, userid);
		getUserSt.setString(2, projectid);
		ResultSet rs = getUserSt.executeQuery();
		ETSUser user = null;
		while (rs.next()) {
			user = getUser(rs);
		}

		rs.close();
		getUserSt.close();
		return user;
	}

	public static ETSUser getPrimaryContact(String projectid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		ETSUser user = null;
		try {
			connection = ETSDBUtils.getConnection();
			user = getPrimaryContact(projectid, connection);
		} catch (SQLException e) {
			printErr("error= " + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return user;
		}
	public static ETSUser getPrimaryContact(String projectid, Connection conn) throws SQLException {

		PreparedStatement getUserSt = conn.prepareStatement("select * from ETS.ETS_USERS u where u.primary_contact = 'Y' and u.user_project_id = ? with ur");
		getUserSt.setString(1, projectid);
		ResultSet rs = getUserSt.executeQuery();
		ETSUser user = null;
		while (rs.next()) {
			user = getUser(rs);
		}

		rs.close();
		getUserSt.close();
		return user;
	}

	public static synchronized String[] editPrimaryContact(String projectid, String userid, String updater) throws SQLException, Exception {
		Connection connection = null;

		try {
			connection = ETSDBUtils.getConnection();
			return editPrimaryContact(projectid, userid, updater, connection);
		} catch (SQLException e) {
			printErr("error= " + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
	}
	public static synchronized String[] editPrimaryContact(String projectid, String userid, String updater, Connection conn) throws SQLException {
		if (!existsProjectID(projectid, conn))
			return new String[] { "-1", "project_id does not exist" };
		else if (!isUserInProject(userid, projectid, conn))
			return new String[] { "-2", "user_id not in project" };

		String update = "update ETS.ETS_USERS set PRIMARY_CONTACT='" + Defines.NO + "',LAST_USERID='" + updater + "',LAST_TIMESTAMP=current timestamp where primary_contact='" + Defines.YES + "' and user_project_id='" + projectid + "'";

		Statement statement = conn.createStatement();
		int rowCount = statement.executeUpdate(update);
		statement.close();

		if (rowCount == 1) {
			update = "update ETS.ETS_USERS set PRIMARY_CONTACT='" + Defines.YES + "',LAST_USERID='" + updater + "',LAST_TIMESTAMP=current timestamp where primary_contact='" + Defines.NO + "' and user_project_id='" + projectid + "' and user_id='" + userid + "'";
			statement = conn.createStatement();
			rowCount = statement.executeUpdate(update);
			statement.close();
			if (rowCount == 1)
				return new String[] { "0", "" };
			else if (rowCount == 0)
				return new String[] { "1", "primary contact not set" };
			else
				return new String[] { String.valueOf(rowCount), "unknown db2 problem" };
		} else if (rowCount == 0){
			// modified by Shanmugam for 7.1.1 manage primary contact
			// start here
			update = "update ETS.ETS_USERS set PRIMARY_CONTACT='" + Defines.YES + "',LAST_USERID='" + updater + "',LAST_TIMESTAMP=current timestamp where primary_contact='" + Defines.NO + "' and user_project_id='" + projectid + "' and user_id='" + userid + "'";
			statement = conn.createStatement();
			rowCount = statement.executeUpdate(update);
			statement.close();
			return new String[] { "0", "" };
//			 end here
	
		}else
			return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}

	public static Vector getUserPrivs(int roleid) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector p = null;
		try {
			connection = ETSDBUtils.getConnection();
			p = getUserPrivs(roleid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return p;
		}
	public static Vector getUserPrivs(int roleid, Connection conn) throws SQLException {
		PreparedStatement getPrivSt = conn.prepareStatement("select priv_id from ETS.ETS_ROLES r where r.role_id = ? and r.priv_value=1 with ur");

		getPrivSt.setInt(1, roleid);
		ResultSet rs = getPrivSt.executeQuery();

		Vector v = new Vector();
		while (rs.next()) {
			v.addElement(new Integer(rs.getInt("priv_id")));
		}

		rs.close();
		getPrivSt.close();
		return v;
	}

	public static Vector getAllUserProjects(String userid) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector projects = null;
		try {
			connection = ETSDBUtils.getConnection();
			projects = getAllUserProjects(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return projects;
		}
	public static Vector getAllUserProjects(String userid, Connection conn) throws SQLException {
		PreparedStatement getProjectsSt = conn.prepareStatement("select p.* from ETS.ETS_PROJECTS p, ETS.ETS_USERS u where u.user_id = ? and u.active_flag='A' and u.user_project_id = p.project_id and p.project_or_proposal !='M' and p.project_status not in ('A','D') order by p.project_name with ur");

		getProjectsSt.setString(1, userid);
		ResultSet rs = getProjectsSt.executeQuery();

		Vector projects = null;
		projects = getProjs(rs);

		rs.close();
		getProjectsSt.close();
		return projects;
	}

	public static Vector getProject(String projectid) throws SQLException, Exception {
		Connection connection = null;
		Vector projects = null;
		try {
			connection = ETSDBUtils.getConnection();
			projects = getProject(projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return projects;
		}
	//spn 0312 projid
	public static Vector getProject(String projectid, Connection conn) throws SQLException {
		PreparedStatement getProjectsSt = conn.prepareStatement("select p.* from ETS.ETS_PROJECTS p where p.project_id = ? with ur");

		getProjectsSt.setString(1, projectid);
		ResultSet rs = getProjectsSt.executeQuery();

		Vector projects = null;
		projects = getProjs(rs);


		rs.close();
		getProjectsSt.close();
		return projects;
	}

	//spn 0312 projid
	public static Vector getProjects(String userid, String projectid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector projects = null;
		try {
			connection = ETSDBUtils.getConnection();
			projects = getProjects(userid, projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return projects;
		}
	//spn 0312 projid
	public static Vector getProjects(String userid, String projectid, Connection conn) throws SQLException {
		PreparedStatement getProjectsSt = conn.prepareStatement("select p.* from ETS.ETS_PROJECTS p, ETS.ETS_USERS u where u.user_id = ? and p.project_id = ? and u.active_flag='A' and u.user_project_id = p.project_id with ur");

		getProjectsSt.setString(1, userid);
		getProjectsSt.setString(2, projectid);
		ResultSet rs = getProjectsSt.executeQuery();

		Vector projects = null;
		projects = getProjs(rs);

		rs.close();
		getProjectsSt.close();
		return projects;
	}

	public static Vector getProjMembers(String projectid) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getProjMembers(projectid, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getProjMembers(String projectid, Connection conn) throws SQLException {
		PreparedStatement getProjMemSt = conn.prepareStatement("select * from ETS.ETS_USERS u, AMT.USERS a where u.user_project_id = ? and u.active_flag='A' and u.user_id = a.ir_userid order by user_fname with ur");

		getProjMemSt.setString(1, projectid);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	//start for ravi
	public static Vector getProjMembers(String projectid, String activeFlag) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getProjMembers(projectid, activeFlag, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getProjMembers(String projectid, String activeFlag, Connection conn) throws SQLException {
		PreparedStatement getProjMemSt = conn.prepareStatement("select * from ETS.ETS_USERS u, AMT.USERS a where u.user_project_id = ? and u.active_flag=? and u.user_id = a.ir_userid order by user_fname with ur");

		getProjMemSt.setString(1, projectid);
		getProjMemSt.setString(2, activeFlag);

		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}
	//for ravi

	public static Vector getProjMembers(String projectid, boolean checkAmtTables) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getProjMembers(projectid, checkAmtTables, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getProjMembers(String projectid, boolean checkAmtTables, Connection conn) throws SQLException {
		PreparedStatement getProjMemSt = conn.prepareStatement("select u.* from ETS.ETS_USERS u, AMT.USERS a where u.user_project_id = ?" + " and u.user_id = a.ir_userid and u.active_flag='A' order by a.USER_FNAME with ur");

		getProjMemSt.setString(1, projectid);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	public static Vector getProjMembersAll(String projectid, boolean checkAmtTables, Connection conn) throws SQLException {
		PreparedStatement getProjMemSt = conn.prepareStatement("select u.* from ETS.ETS_USERS u, AMT.USERS a where u.user_project_id = ?" + " and u.user_id = a.ir_userid order by a.USER_FNAME with ur");

		getProjMemSt.setString(1, projectid);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	public static Vector getProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getProjMembers(projectid, sortby, ad, checkAmtTables, getrolename, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename, Connection conn) throws SQLException {
		String sb = "a.user_fname " + ad + ", a.user_lname";

		if (sortby.equals(Defines.SORT_BY_USERID_STR)) {
			sb = "u.user_id";
		} else if (sortby.equals(Defines.SORT_BY_PROJROLE_STR)) {
			sb = "u.user_job";
		} else if (sortby.equals(Defines.SORT_BY_COMP_STR)) {
			sb = "a.user_fname " + ad + ",a.user_lname";
		} else if (sortby.equals(Defines.SORT_BY_ACCLEV_STR)) {
			sb = "role_name " + ad + ",a.user_fname " + ad + ",a.user_lname";
		}

		PreparedStatement getProjMemSt;

		if (getrolename) {
			getProjMemSt =
				conn.prepareStatement(
					"select u.*,(select distinct r.role_name from ets.ets_roles r where r.role_id=u.user_role_id) as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid  and u.active_flag='A' order by " + sb + " " + ad + " with ur");
		} else {
			getProjMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid and u.active_flag='A' order by " + sb + " " + ad + " with ur");
		}

		getProjMemSt.setString(1, projectid);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getAllUsersData(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	//start 4.5.1
	public static Vector getAllProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getAllProjMembers(projectid, sortby, ad, checkAmtTables, getrolename, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getAllProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename, Connection conn) throws SQLException {
		String sb = "a.user_fname " + ad + ", a.user_lname";

		if (sortby.equals(Defines.SORT_BY_USERID_STR)) {
			sb = "u.user_id";
		} else if (sortby.equals(Defines.SORT_BY_PROJROLE_STR)) {
			sb = "u.user_job";
		} else if (sortby.equals(Defines.SORT_BY_COMP_STR)) {
			sb = "a.user_fname " + ad + ",a.user_lname";
		} else if (sortby.equals(Defines.SORT_BY_ACCLEV_STR)) {
			sb = "role_name " + ad + ",a.user_fname " + ad + ",a.user_lname";
		}

		PreparedStatement getProjMemSt;

		if (getrolename) {
			getProjMemSt = conn.prepareStatement("select u.*,(select distinct r.role_name from ets.ets_roles r where r.role_id=u.user_role_id) as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid  order by " + sb + " " + ad + " with ur");
		} else {
			getProjMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type  " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid order by " + sb + " " + ad + " with ur");
		}

		getProjMemSt.setString(1, projectid);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getAllUsersData(rs);
		rs.close();
		getProjMemSt.close();
		return mems;
	}
	//end 4.5.1

	public static Vector getProjMembersWithOutPriv(String projectid, int privid, boolean checkAmtTables) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getProjMembersWithOutPriv(projectid, privid, checkAmtTables, connection);
		} catch (SQLException e) {
			printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getProjMembersWithOutPriv(String projectid, int privid, boolean checkAmtTables, Connection conn) throws SQLException {
		//select PRIV_VALUE from ETS.ETS_ROLES where PRIV_ID = "+priv+" and ROLE_ID = (select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" + userid + "' and USER_PROJECT_ID = '" + projid + "') with ur");

		PreparedStatement getProjMemSt = conn.prepareStatement("select u.* from ETS.ETS_USERS u, AMT.USERS a where u.user_project_id = ?" + " and u.user_id = a.ir_userid and u.active_flag='A' and u.user_role_id not in (select role_id from ets.ets_roles where priv_id=? and priv_value=1)" + "order by a.USER_FULLNAME with ur");

		getProjMemSt.setString(1, projectid);
		getProjMemSt.setInt(2, privid);

		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	//5.2.1
	public static Vector getRestrictedProjMembers(String projectid, int id, boolean checkAmtTables, boolean isCat) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			if (checkAmtTables)
				members = getRestrictedProjMembers(projectid, id, checkAmtTables, isCat, connection);
			else
				members = getRestrictedProjMembers(projectid, id, isCat, connection);
		} catch (SQLException e) {
			printErr("sqlerror=" + e);
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			printErr("error=" + ex);
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getRestrictedProjMembers(String projectid, int id, boolean checkAmtTables, boolean isCat, Connection conn) throws SQLException {

		String tablestr = "cat";
		if (!isCat)
			tablestr = "doc";

		PreparedStatement getProjMemSt = conn.prepareStatement("select u.* from ETS.ETS_USERS u, AMT.USERS a,ets.ets_private_" + tablestr + " t where u.user_project_id = ?" + " and u.user_id = a.ir_userid and u.active_flag='A' and u.user_id=t.user_id and u.user_project_id=t.project_id and t." + tablestr + "_id=? order by a.USER_FULLNAME with ur");

		getProjMemSt.setString(1, projectid);
		getProjMemSt.setInt(2, id);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		System.out.println("1 MEMS SIZE=" + mems.size());
		return mems;
	}

	public static Vector getRestrictedProjMembers(String projectid, int id, boolean isCat, Connection conn) throws SQLException {

		String tablestr = "cat";
		if (!isCat)
			tablestr = "doc";

		PreparedStatement getProjMemSt = conn.prepareStatement("select u.* from ETS.ETS_USERS u, ets.ets_private_" + tablestr + " t where u.user_project_id = ?" + " and u.active_flag='A' and u.user_id=t.user_id and u.user_project_id=t.project_id and t." + tablestr + "_id=? order by u.user_id with ur");

		getProjMemSt.setString(1, projectid);
		getProjMemSt.setInt(2, id);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getUsers(rs);

		rs.close();
		getProjMemSt.close();
		System.out.println("2 MEMS SIZE=" + mems.size());
		return mems;
	}

	public static Vector getAllDocRestrictedUserIds(String docids, String projectid) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getAllDocRestrictedUserIds(docids, projectid, connection);
		} catch (SQLException e) {
			printErr("sqlerror=" + e);
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			printErr("error=" + ex);
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getAllDocRestrictedUserIds(String docids, String projectid, Connection conn) throws SQLException {

		Statement getProjMemSt = conn.createStatement();

		String query = "select distinct u.user_id from ETS.ETS_USERS u, ets.ets_private_doc t " + " where u.user_project_id = '" + projectid + "'" + " and u.user_id=t.user_id and u.user_project_id=t.project_id and t.doc_id in(" + docids + ") with ur";

		ResultSet rs = getProjMemSt.executeQuery(query);

		Vector mems = new Vector();
		while (rs.next()) {
			mems.addElement(rs.getString("user_id"));
		}

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	public static Vector getRestrictedProjMemberIds(String projectid, int id, boolean isCat) throws SQLException, Exception {
		Connection connection = null;
		Vector members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getRestrictedProjMemberIds(projectid, id, isCat, connection);
		} catch (SQLException e) {
			printErr("sqlerror=" + e);
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			printErr("error=" + ex);
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return members;
		}
	public static Vector getRestrictedProjMemberIds(String projectid, int id, boolean isCat, Connection conn) throws SQLException {

		String tablestr = "cat";
		if (!isCat)
			tablestr = "doc";

		StringBuffer strQuery = new StringBuffer("");
		strQuery.append("select u.user_id from ETS.ETS_USERS u, ets.ets_private_" + tablestr + " t where u.user_project_id = ?" + " and u.active_flag='A' and u.user_id=t.user_id and u.user_project_id=t.project_id and t." + tablestr + "_id=?");
		if (!isCat) {
			strQuery.append(" union ");
			strQuery.append("select u.user_id from ETS.ETS_USERS u, ets.ets_private_" + tablestr + " t, ets.user_groups g where u.user_project_id = ?" + " and u.active_flag='A' and u.user_id=g.user_id and g.group_id=t.group_id and u.user_project_id=t.project_id and t.doc_id=?");
			strQuery.append(" order by user_id with ur");
		}
		else {
			strQuery.append(" order by u.user_id with ur");
		}
		PreparedStatement getProjMemSt = conn.prepareStatement(strQuery.toString());

		getProjMemSt.setString(1, projectid);
		getProjMemSt.setInt(2, id);
		if (!isCat) {
			getProjMemSt.setString(3, projectid);
			getProjMemSt.setInt(4, id);
		}
		ResultSet rs = getProjMemSt.executeQuery();

		Vector mems = new Vector();
		while (rs.next()) {
			mems.addElement(rs.getString("user_id"));
		}

		rs.close();
		getProjMemSt.close();
		return mems;
	}

	public static Vector getTopCats(String projectid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector topcats = null;
		try {
			connection = ETSDBUtils.getConnection();
			topcats = getTopCats(projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return topcats;
		}
	//spn 0312 projid
	public static Vector getTopCats(String projectid, Connection conn) throws SQLException {
		PreparedStatement getTopCatsSt = conn.prepareStatement("select * from ETS.ETS_CAT where project_id = ? and parent_id = 0 order by order with ur");

		getTopCatsSt.setString(1, projectid);
		ResultSet rs = getTopCatsSt.executeQuery();

		Vector topcats = null;
		topcats = getCats(rs);

		rs.close();
		getTopCatsSt.close();
		return topcats;
	}

	public static Vector getSubCats(int parentid, String sortby, String a_d) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector subcats = null;
		try {
			connection = ETSDBUtils.getConnection();
			subcats = getSubCats(parentid, sortby, a_d, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return subcats;
		}
	public static Vector getSubCats(int parentid, String sortby, String a_d, Connection conn) throws SQLException {
		String sb = "c.cat_name";

		if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
			sb = "c.last_timestamp " + a_d + ",c.cat_name";
		} else if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			sb = "c.cat_name";
		} else if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
			sb = "c.user_id " + a_d + ",c.cat_name";
		}

		//PreparedStatement getSubCatsSt = conn.prepareStatement("select * from ETS.ETS_CAT where parent_id=? order by "+sb+" "+a_d+" with ur");
		//getSubCatsSt.setInt(1, parentid);
		//ResultSet rs = getSubCatsSt.executeQuery();

		Statement getSubCatsSt = conn.createStatement();

		String query = "select c.* from ETS.ETS_CAT c where c.parent_id=" + parentid + " order by " + sb + " " + a_d + " with ur";

		/*
		if (!admin){
			query = "select c.*	from ets.ets_cat c where c.parent_id="+parentid+" and (c.isprivate!='1' or c.isprivate is null " +
				" or c.user_id='"+userid+"' or '"+userid+"' in (select d.user_id from ets.ets_private_cat d where d.cat_id=c.cat_id)) " +
				" order by "+sb+" "+a_d+" with ur";
		}*/

		System.out.println("******" + query);
		ResultSet rs = getSubCatsSt.executeQuery(query);

		Vector subcats = new Vector();
		subcats = getCats(rs);

		rs.close();
		getSubCatsSt.close();
		return subcats;
	}

	public static Vector getSubCats(int parentid) throws SQLException {
		Connection connection = null;
		Vector subcats = null;
		try {
			connection = ETSDBUtils.getConnection();
			subcats = getSubCats(parentid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return subcats;
		}
	public static Vector getSubCats(int parentid, Connection conn) throws SQLException {

		Statement getSubCatsSt = conn.createStatement();
		String query = "select * from ETS.ETS_CAT where parent_id=" + parentid + " order by order,cat_name with ur";

		/*
		if (!admin){
			 query = "select c.* from ets.ets_cat c where c.parent_id="+parentid+" and (c.isprivate!='1' or c.isprivate is null " +
				 " or c.user_id='"+userid+"' or '"+userid+"' in (select d.user_id from ets.ets_private_cat d where d.cat_id=c.cat_id)) " +
				 " order by order,cat_name with ur";
		}
		*/
		ResultSet rs = getSubCatsSt.executeQuery(query);

		Vector subcats = null;
		subcats = getCats(rs);

		rs.close();

		getSubCatsSt.close();
		return subcats;
	}

	public static Vector getDocs(int parentid) throws SQLException {
		Connection connection = null;
		Vector docs = null;
		try {
			connection = ETSDBUtils.getConnection();
			docs = getDocs(parentid, " ", true, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return docs;
		}

	public static Vector getDocs(int parentid, String userid, boolean admin) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		Vector docs = null;
		try {
			connection = ETSDBUtils.getConnection();
			docs = getDocs(parentid, userid, admin, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return docs;
		}
	public static Vector getDocs(int parentid, String userid, boolean admin, Connection conn) throws SQLException, Exception {
		PreparedStatement getDocsSt = conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits  from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.cat_id=? and d.doc_id = f.doc_id and delete_flag !='" + TRUE_FLAG + "' order by d.doc_name with ur");

		getDocsSt.setInt(1, parentid);
		//getDocsSt.setString(2,TRUE_FLAG);
		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getLatestDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;
	}

	//called from doc man
	public static Vector getDocs(int parentid, String sortby, String a_d, boolean isAdmin, String userid) throws SQLException {
		Connection connection = null;
		Vector docs = null;
		try {
			connection = ETSDBUtils.getConnection();
			docs = getDocs(parentid, sortby, a_d, isAdmin, userid, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return docs;
		}
	public static Vector getDocs(int parentid, String sortby, String a_d, boolean admin, String userid, Connection conn) throws SQLException, Exception {
		String sb = "d.doc_name";

		if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
			sb = "d.doc_upload_date " + a_d + ",d.doc_name";
		} else if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			sb = "d.doc_name";
		} else if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
			sb = "d.user_id " + a_d + ",d.doc_name";
		}

		/*
		PreparedStatement getDocsSt = conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," +
		"(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " +
		"from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " +
		"where d.cat_id=? and d.doc_id = f.doc_id and delete_flag !='"+TRUE_FLAG+"' order by "+sb+" "+a_d+" with ur");

		getDocsSt.setInt(1, parentid);
		//getDocsSt.setString(2,TRUE_FLAG);
		ResultSet rs = getDocsSt.executeQuery();
		*/

		String query = "select d.*,f.docfile_name,f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.cat_id=" + parentid + " and d.doc_id = f.doc_id and delete_flag !='" + TRUE_FLAG + "' order by " + sb + " " + a_d + " with ur";

		if (!admin) {
			query =
				"select d.*,f.docfile_name,f.docfile_update_date,"
					+ " (select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
					+ " from ETS.ETS_DOC d, ETS.ETS_DOCFILE f"
					+ " where d.cat_id="
					+ parentid
					+ " and d.doc_id = f.doc_id and delete_flag !='"
					+ TRUE_FLAG
					+ "'"
					+ " and (d.isprivate!='1' or d.isprivate is null"
					+ " or d.user_id='"
					+ userid
					+ "' or '"
					+ userid
					+ "' in (select u.user_id from ets.ets_private_doc u where u.doc_id=d.doc_id))"
					+ " order by "
					+ sb
					+ " "
					+ a_d
					+ " with ur";
		}

		System.out.println(query);
		Statement getDocsSt = conn.createStatement();
		ResultSet rs = getDocsSt.executeQuery(query);

		Vector docs = new Vector();
		docs = getLatestDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;
	}

	public static Vector getDocsByDocIdInOrder(Vector doc_ids) throws SQLException, Exception {
		Vector docs = null;
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			docs = getDocsByDocIdInOrder(doc_ids, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}
			return docs;
		}
	public static Vector getDocsByDocIdInOrder(Vector doc_ids, Connection conn) throws SQLException {

		int numDocs = doc_ids.size();
		Vector docs = new Vector(numDocs);

		PreparedStatement getDocsSt = conn.prepareStatement("select d.*, f.docfile_name, f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.doc_id = ? and d.doc_id = f.doc_id and delete_flag!='" + TRUE_FLAG + "' with ur");

		for (int i = 0; i < numDocs; i++) {
			String id = (String) doc_ids.get(i);
			int doc_id = Integer.parseInt(id);
			getDocsSt.setInt(1, doc_id);
			ResultSet rs = getDocsSt.executeQuery();
			if (rs.next()) {
				ETSDoc doc = getDoc(rs);
				docs.add(doc);
			}
			rs.close();
		}

		getDocsSt.close();
		return docs;
	}

	//spn 0312 projid
	public static ETSDoc getDocByIdAndProject(int docid, String projectid) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		ETSDoc doc = null;
		try {
			connection = ETSDBUtils.getConnection();
			doc = getDocByIdAndProject(docid, projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return doc;
		}
	//spn 0312 projid
	public static ETSDoc getDocByIdAndProject(int docid, String projectid, Connection conn) throws SQLException {
		Statement getDocsSt = conn.createStatement();

		ResultSet rs = getDocsSt.executeQuery("select d.*, f.docfile_name, f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.doc_id = " + docid + " and d.project_id = '" + projectid + "' and d.doc_id = f.doc_id and delete_flag!='" + TRUE_FLAG + "' with ur");

		ETSDoc doc = null;
		if (rs.next()) {
			doc = getDoc(rs);
		}

		rs.close();
		getDocsSt.close();

		return doc;
	}

	public static Vector getDocsByAuthor(String userid, String projid) throws SQLException {
		Connection connection = null;
		Vector docs = null;

		try {
			connection = ETSDBUtils.getConnection();
			docs = getDocsByAuthor(userid, projid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return docs;
		}
	public static Vector getDocsByAuthor(String userid, String projid, Connection conn) throws SQLException, Exception {
		PreparedStatement getDocsSt =
			conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.user_id=? and d.doc_id = f.doc_id and DOC_TYPE=? and delete_flag !='" + TRUE_FLAG + "' and d.project_id=? order by d.doc_upload_date desc with ur");

		getDocsSt.setString(1, userid);
		getDocsSt.setInt(2, Defines.DOC);
		//getDocsSt.setString(3,TRUE_FLAG);
		getDocsSt.setString(3, projid);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getLatestDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;
	}

	public static Vector getDocsByAuthor(String author, String userid, String projid, String sortby, String ad, boolean admin) throws SQLException {
		Connection connection = null;
		Vector docs = null;

		try {
			connection = ETSDBUtils.getConnection();
			docs = getDocsByAuthor(author, userid, projid, sortby, ad, admin, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return docs;
		}
	public static Vector getDocsByAuthor(String author, String userid, String projid, String sortby, String ad, boolean admin, Connection conn) throws SQLException, Exception {

		String sb = "d.doc_name";
		if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
			sb = "d.doc_upload_date " + ad + ",d.doc_name";
		} else if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			sb = "d.doc_name";
		}

		/*
		PreparedStatement getDocsSt = conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," +
			"(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " +
			"from ETS.ETS_DOC d, ETS.ETS_DOCFILE f "+
		 	"where d.user_id=? and d.doc_id = f.doc_id and DOC_TYPE=? and delete_flag !='"+TRUE_FLAG+"' and d.project_id=? "+
		 	"order by "+sortby+" "+ad+" with ur");

		getDocsSt.setString(1, userid);
		getDocsSt.setInt(2, Defines.DOC);
		getDocsSt.setString(3,projid);
		*/

		String query =
//			"select d.*,f.docfile_name,f.docfile_update_date,"
			"select d.*,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
//				+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f "
				+ "from ETS.ETS_DOC d  "
				+ "where d.user_id='"
				+ author
//				+ "' and d.doc_id = f.doc_id and DOC_TYPE="
				+ "' and DOC_TYPE="
				+ Defines.DOC
				+ " and delete_flag !='"
				+ TRUE_FLAG
				+ "' "
				+ " and d.project_id='"
				+ projid
				+ "'"
				+ "order by "
				+ sb
				+ " "
				+ ad
				+ " with ur";

		if (!admin) {
			query =
//				"select d.*,f.docfile_name,f.docfile_update_date,"
				"select d.*,"
					+ " (select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
//					+ " from ETS.ETS_DOC d, ETS.ETS_DOCFILE f"
					+ " from ETS.ETS_DOC d"
					+ " where d.user_id='"
					+ author
					+ "'"
//					+ " and d.doc_id = f.doc_id and delete_flag !='"
					+ " and delete_flag !='"
					+ TRUE_FLAG
					+ "'"
					+ "  and DOC_TYPE="
					+ Defines.DOC
					+ " and d.project_id='"
					+ projid
					+ "'"
					+ " and (d.isprivate!='1' or d.isprivate is null"
					+ " or d.user_id='"
					+ userid
					+ "' or '"
					+ userid
					+ "' in (select u.user_id from ets.ets_private_doc u where u.doc_id=d.doc_id))"
					+ " order by "
					+ sb
					+ " "
					+ ad
					+ " with ur";
		}

		Statement getDocsSt = conn.createStatement();
		ResultSet rs = getDocsSt.executeQuery(query);

		Vector docs = null;
		docs = getLatestDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;
	}

	public static ETSCat getCat(int id) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		ETSCat cat = null;
		try {
			connection = ETSDBUtils.getConnection();
			cat = getCat(id, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return cat;
		}
	public static ETSCat getCat(int id, Connection conn) throws SQLException {
		PreparedStatement getCatSt = conn.prepareStatement("select * from ETS.ETS_CAT where cat_id=? with ur");
		getCatSt.setInt(1, id);
		ResultSet rs = getCatSt.executeQuery();
		ETSCat cat = null;

		while (rs.next()) {
			cat = getCat(rs);
		}

		rs.close();
		getCatSt.close();
		return cat;
	}

	public static ETSCat getCat(int id, String projectid) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		ETSCat cat = null;
		try {
			connection = ETSDBUtils.getConnection();
			cat = getCat(id, projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return cat;
		}
	public static ETSCat getCat(int id, String projectid, Connection conn) throws SQLException {
		PreparedStatement getCatSt = conn.prepareStatement("select * from ETS.ETS_CAT where cat_id=? and project_id=? with ur");
		getCatSt.setInt(1, id);
		getCatSt.setString(2, projectid);
		ResultSet rs = getCatSt.executeQuery();
		ETSCat cat = null;

		while (rs.next()) {
			cat = getCat(rs);
		}

		rs.close();
		getCatSt.close();
		return cat;
	}

	private static Vector getUsers(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSUser user = getUser(rs);
			v.addElement(user);
		}
		return v;
	}
	private static ETSUser getUser(ResultSet rs) throws SQLException {
		ETSUser user = new ETSUser();

		user.setUserId(rs.getString("USER_ID"));
		//System.out.println(user.getUserId());
		//spn 0312 projid
		user.setProjectId(rs.getString("USER_PROJECT_ID"));
		user.setRoleId(rs.getInt("USER_ROLE_ID"));
		user.setUserJob(rs.getString("USER_JOB"));
		user.setPrimaryContact(rs.getString("PRIMARY_CONTACT"));
		user.setLastUserId(rs.getString("LAST_USERID"));
		user.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
		user.setActiveFlag(rs.getString("ACTIVE_FLAG"));

		return user;
	}

	private static Vector getAllUsersData(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSUser user = getAllUserData(rs);
			v.addElement(user);
		}
		return v;
	}
	private static ETSUser getAllUserData(ResultSet rs) throws SQLException {
		ETSUser user = new ETSUser();

		user.setUserId(rs.getString("USER_ID"));
		//System.out.println(user.getUserId());
		//spn 0312 projid
		user.setProjectId(rs.getString("USER_PROJECT_ID"));
		user.setRoleId(rs.getInt("USER_ROLE_ID"));
		user.setUserJob(rs.getString("USER_JOB"));
		user.setPrimaryContact(rs.getString("PRIMARY_CONTACT"));
		user.setLastUserId(rs.getString("LAST_USERID"));
		user.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
		user.setActiveFlag(rs.getString("ACTIVE_FLAG"));

		user.setUserName(rs.getString("USER_FNAME") + "&nbsp;" + rs.getString("USER_LNAME"));

		String comp = "";
		if ((rs.getString("user_type")).equalsIgnoreCase("I")) {
			comp = "IBM";
		} else {
			String c = rs.getString("assoc_company");
			if (c != null) {
				comp = c;
			} else {
				comp = "";
			}
		}
		user.setCompany(comp);
		user.setRoleName(rs.getString("role_name"));
		user.setUserType(AmtCommonUtils.getTrimStr(rs.getString("user_type")));

		//System.out.println("usertype= "+rs.getString("user_type")+"  comp="+comp);
		return user;
	}

	private static Vector getProjs(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSProj proj = getProj(rs);
			v.addElement(proj);
		}
		return v;
	}
	private static ETSProj getProj(ResultSet rs) throws SQLException {
		ETSProj proj = new ETSProj();

		String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
		String sProjDesc = ETSUtils.checkNull(rs.getString("PROJECT_DESCRIPTION"));
		String sProjName = ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
		Timestamp tProjStart = rs.getTimestamp("PROJECT_START");
		Timestamp tProjEnd = rs.getTimestamp("PROJECT_END");
		String sDecafProjName = ETSUtils.checkNull(rs.getString("DECAF_PROJECT_NAME"));
		String sProjOrProposal = ETSUtils.checkNull(rs.getString("PROJECT_OR_PROPOSAL"));
		String sLotusProjID = ETSUtils.checkNull(rs.getString("LOTUS_PROJECT_ID"));
		String sRelatedId = ETSUtils.checkNull(rs.getString("RELATED_ID"));
		String sParentId = ETSUtils.checkNull(rs.getString("PARENT_ID"));
		String sCompany = ETSUtils.checkNull(rs.getString("COMPANY"));
		String sPmoProjectId = ETSUtils.checkNull(rs.getString("PMO_PROJECT_ID"));
		String sShowIssueOwner = ETSUtils.checkNull(rs.getString("SHOW_ISSUE_OWNER"));
		String sProjectStatus = ETSUtils.checkNull(rs.getString("PROJECT_STATUS"));

		proj.setProjectId(sProjId);
		proj.setDescription(sProjDesc);
		proj.setName(sProjName);
		proj.setStartDate(tProjStart);
		proj.setEndDate(tProjEnd);
		proj.setDecafProject(sDecafProjName);
		proj.setProjectOrProposal(sProjOrProposal);
		proj.setLotusProject(sLotusProjID);
		proj.setRelatedProjectId(sRelatedId);
		proj.setParent_id(sParentId);
		proj.setCompany(sCompany);
		proj.setPmo_project_id(sPmoProjectId);
		proj.setShow_issue_owner(sShowIssueOwner);
		proj.setProject_status(sProjectStatus);

		return proj;
	}

	private static Vector getCats(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSCat cat = getCat(rs);
			v.addElement(cat);
		}
		return v;
	}
	private static ETSCat getCat(ResultSet rs) throws SQLException {
		ETSCat cat = new ETSCat();

		cat.setId(rs.getInt("CAT_ID"));

		//spn 0312 projid
		cat.setProjectId(rs.getString("PROJECT_ID"));
		cat.setUserId(rs.getString("USER_ID"));
		cat.setName(rs.getString("CAT_NAME"));
		cat.setParentId(rs.getInt("PARENT_ID"));
		cat.setCatType(rs.getInt("CAT_TYPE"));
		cat.setDescription(rs.getString("CAT_DESCRIPTION"));
		cat.setOrder(rs.getInt("ORDER"));
		cat.setViewType(rs.getInt("VIEW_TYPE"));
		cat.setProjDesc(rs.getInt("PROJ_DESC"));
		//cat.setProjMsg(rs.getInt("PROJ_MSG"));
		cat.setPrivs(rs.getString("PRIVS"));
		cat.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
		cat.setIbmOnly(rs.getString("IBM_ONLY"));
		cat.setCPrivate(rs.getString("ISPRIVATE"));
		return cat;
	}

	private static Vector getLatestDocs(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSDoc doc = getDoc(rs);
			if (doc.isLatestVersion()) {
				v.addElement(doc);
			}
		}

		return v;
	}

	public static Vector getDocs(ResultSet rs) throws SQLException {
		Vector v = new Vector();
		while (rs.next()) {
			System.out.println("aaa here");
			ETSDoc doc = getDoc(rs);
			v.addElement(doc);
		}
		return v;
	}
	public static ETSDoc getDoc(ResultSet rs) throws SQLException {
		ETSDoc doc = new ETSDoc();

		doc.setId(rs.getInt("DOC_ID"));
		//spn 0312 projid
		doc.setProjectId(rs.getString("PROJECT_ID"));
		doc.setUserId(rs.getString("USER_ID"));
		doc.setName(rs.getString("DOC_NAME"));
		doc.setCatId(rs.getInt("CAT_ID"));
		doc.setDescription(rs.getString("DOC_DESCRIPTION"));
		doc.setSize(rs.getInt("DOC_SIZE"));
		doc.setUploadDate(rs.getTimestamp("DOC_UPLOAD_DATE"));
		doc.setUpdateDate(rs.getTimestamp("DOC_UPDATE_DATE"));
		doc.setPublishDate(rs.getTimestamp("DOC_PUBLISH_DATE"));

//		System.out.println("FILE NAME===" + rs.getString("DOCFILE_NAME"));
//		doc.setFileName(rs.getString("DOCFILE_NAME"));
//		doc.setFileUpdateDate(rs.getTimestamp("DOCFILE_UPDATE_DATE"));
		doc.setKeywords(rs.getString("DOC_KEYWORDS"));

		doc.setDocType(rs.getInt("DOC_TYPE"));
		doc.setUpdatedBy(rs.getString("UPDATED_BY"));
		doc.setHasPreviousVersion(rs.getString("HAS_PREV_VERSION"));
		doc.setIsLatestVersion(rs.getString("LATEST_VERSION"));
		doc.setLockFinalFlag(rs.getString("LOCK_FINAL_FLAG"));
		doc.setLockedBy(rs.getString("LOCKED_BY"));
		doc.setDeleteFlag(rs.getString("DELETE_FLAG"));
		doc.setDeletedBy(rs.getString("DELETED_BY"));
		//doc.setDeleteDate(rs.getTimestamp("DELETION_DATE"));
		doc.setMeetingId(rs.getString("MEETING_ID"));
		doc.setIbmOnly(rs.getString("IBM_ONLY"));
		doc.setDocHits(rs.getInt("hits"));
		doc.setDPrivate(rs.getString("ISPRIVATE"));

		//doc.setDocStatus(rs.getString("document_status"));
		//doc.setApproverId(rs.getString("approver_id"));
		//doc.setApprovalComments(rs.getString("approval_comments"));
		//doc.setApproveDate(rs.getTimestamp("APPRIVE_DATE"));
		if (rs.getTimestamp("EXPIRY_DATE") != null) {
			System.out.println("timestamp=" + rs.getTimestamp("EXPIRY_DATE"));
			doc.setExpiryDate(rs.getTimestamp("EXPIRY_DATE"));
		} else {
			//FIX FIX FIX FIX EXPIRE DATE
			doc.setExpiryDate(0);
		}

		//WAS THIS BEFORE
		//doc.setExpiryDate(new Timestamp(0));

		//doc.setPrivs(rs.getString("PRIVS"));
		return doc;
	}

	public static synchronized String[] addCat(ETSCat cat) throws SQLException, Exception {
		Connection connection = null;
		//boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			return addCat(cat, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
			//return success;
		}
	}
	public static synchronized String[] addCat(ETSCat cat, Connection conn) throws SQLException {
		//PreparedStatement addCatSt = conn.prepareStatement("insert into ets.ets_cat(cat_id,project_id,user_id,cat_name,parent_id,cat_type,cat_description,order,view_type,proj_desc,privs,last_timestamp) values((select max(cat_id) from ets.ets_cat)+1,?,?,?,?,?,?,?,?,?,?,current timestamp)");
		int catid = getCatId(conn);

		PreparedStatement addCatSt = conn.prepareStatement("insert into ets.ets_cat(cat_id,project_id,user_id,cat_name,parent_id,cat_type,cat_description,order,view_type,proj_desc,privs,ibm_only,isPrivate,last_timestamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp)");

		try {
			//spn 0312 projid
			addCatSt.setInt(1, catid);
			addCatSt.setString(2, cat.getProjectId());
			addCatSt.setString(3, cat.getUserId());
			addCatSt.setString(4, cat.getName());
			addCatSt.setInt(5, cat.getParentId());
			addCatSt.setInt(6, 2);
			addCatSt.setString(7, cat.getDescription());
			addCatSt.setInt(8, cat.getOrder());
			addCatSt.setInt(9, cat.getViewType());
			addCatSt.setInt(10, cat.getProjDesc());
			//addCatSt.setInt(10, cat.getProjMsg());
			addCatSt.setString(11, cat.getPrivsString());
			addCatSt.setString(12, String.valueOf(cat.getIbmOnly()));
			addCatSt.setString(13, cat.getCPrivate());
			addCatSt.executeUpdate();

			addCatSt.close();
			String catidStr = String.valueOf(catid);
			addContentLog(catid, 'C', cat.getProjectId(), Defines.ADD_CAT, cat.getUserId());
			return new String[] { "0", catidStr };
		} catch (SQLException se) {
			printErr("sql error =" + se);
			return new String[] { "1" };
			//return false;
			//throw se;
		}

		finally {

			ETSDBUtils.close(addCatSt);
		}

	}

	private static synchronized int getCatId(Connection conn) throws SQLException {
		int cat_id = 0;
		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("select max(cat_id) as cat_id from ets.ets_cat with ur");

		if (rs.next()) {
			cat_id = rs.getInt("cat_id") + 1;
		}

		rs.close();
		statement.close();
		return cat_id;
	}

	static public boolean addCatResUsers(Vector users, String catid, String projid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addCatResUsers(users, catid, projid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}

	static public boolean addCatResUsers(Vector users, String catid, String projid, Connection conn) throws SQLException {
		try {
			boolean s = true;
			int c = new Integer(catid).intValue();
			for (int i = 0; i < users.size(); i++) {
				s = addCatResUsers((String) users.elementAt(i), c, projid, conn);
			}
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
		return true;
	}

	static public boolean addCatResUsers(String userid, int catid, String projid, Connection conn) throws SQLException {
		Statement statement = null;
		try {
			String qry = "insert into ETS.ETS_PRIVATE_CAT(CAT_ID, USER_ID, PROJECT_ID) " + "values (" + catid + ",'" + userid + "','" + projid + "')";

			statement = conn.createStatement();
			int rowCount = statement.executeUpdate(qry);
			statement.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}

		finally {

					ETSDBUtils.close(statement);
				}
	}

	/*
	static synchronized void deleteCategory(CategoryNode node, Connection connection) throws SQLException
	{
	//PreparedStatement deleteCategoryStatement = connection.prepareStatement("delete from EDESIGN.PAGE_CONTENT where NODE_ID=?");    //@A03 sms

	PreparedStatement deleteCategoryStatement = connection.prepareStatement("delete from EDESIGN.CM_CAT where ID=?");    // 05 sps

	// delete child categories
	Vector categories = getDeleteCategories(node.getID(), false);            // @C01 sms
	for (int i = 0; i<categories.size(); i++)
	        {
	            CategoryNode cat = (CategoryNode)categories.elementAt(i);
		deleteCategory(cat, connection);
	        }

	// delete child documents
	Vector documents = getDocuments(node.getID(), false);            // @C01 sms
	for (int i = 0; i<documents.size(); i++)
	        {
	            DocumentNode doc = (DocumentNode)documents.elementAt(i);
	            deleteDocumentFromDelCat(doc, connection);
	        }

	deleteSubCat(node);

	deleteCategoryStatement.setString(1, node.getID());
	deleteCategoryStatement.executeUpdate();

	deleteCategoryStatement.close();                //@A03 sms
	 }
	*/

	public static synchronized boolean delCat(ETSCat cat, String userid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = delCat(cat, userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean delCat(ETSCat cat, String userid, Connection conn) throws SQLException {
		PreparedStatement delCatSt = conn.prepareStatement("delete from ets.ets_cat where cat_id=?");
		//PreparedStatement delCatSt = conn.prepareStatement("update ets.ets_cat set where cat_id=?");

		try {
			int catid = cat.getId();
			String projid = cat.getProjectId();

			// delete child categories
			Vector categories = getSubCats(cat.getId());
			for (int i = 0; i < categories.size(); i++) {
				ETSCat subcat = (ETSCat) categories.elementAt(i);
				delCat(subcat, userid, conn);
			}

			// delete child documents
			Vector documents = getDocs(cat.getId(), userid, true); // //5.2.1  todo
			for (int i = 0; i < documents.size(); i++) {
				ETSDoc doc = (ETSDoc) documents.elementAt(i);
				delDoc(doc, userid, true, conn);
			}

			delCatSt.setInt(1, cat.getId());
			delCatSt.executeUpdate();

			delCatSt.close();
			addContentLog(catid, 'C', projid, Defines.DEL_CAT, userid);
			/*
			  delCatSt.setInt(1, cat.getId());
			  delCatSt.executeUpdate();
			  delCatSt.close();
			  return true;
			*/
			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			return false;
			//throw se;
		} catch (Exception e) {
			printErr("ex error =" + e);
			return false;
		}

	}

	public static synchronized boolean delDoc(ETSDoc doc, String userid) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = delDoc(doc, userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean delDoc(ETSDoc doc, String userid, Connection conn) throws SQLException {
		try {
			int docid = doc.getId();
			String projid = doc.getProjectId();

			Statement stmt = conn.createStatement();
			//String update = "delete from ETS.ETS_DOC where DOC_ID = " + docid;
			String update = "update ETS.ETS_DOC set latest_version='" + FALSE_FLAG + "', delete_flag='" + TRUE_FLAG + "',deleted_by='" + userid + "',deletion_date=current timestamp where DOC_ID = " + docid;

			int rowCount = stmt.executeUpdate(update);

			if (rowCount != 1) {
				System.out.println("executeUpdate(" + update + ") returned " + rowCount);
				printErr("executeUpdate(" + update + ") returned " + rowCount);
				return false;
			}

			addContentLog(docid, 'D', projid, Defines.DEL_DOC, userid);

			int lowestPossibleID = (docid / MAX_DOC_VERSIONS) * MAX_DOC_VERSIONS;
			int highestPossibleID = lowestPossibleID + MAX_DOC_VERSIONS;

			String query = "select MIN(DOC_ID) as MIN_ID, MAX(DOC_ID) as MAX_ID from ETS.ETS_DOC where DOC_ID >= " + lowestPossibleID + " and DOC_ID < " + highestPossibleID + " and delete_flag!='" + TRUE_FLAG + "' with ur";

			ResultSet rs = stmt.executeQuery(query);

			int minId = -1;
			int maxId = -1;

			if (rs.next()) {
				minId = rs.getInt("MIN_ID");
				maxId = rs.getInt("MAX_ID");
			}

			rs.close();
			stmt.close();

			if (minId == -1) // no other versions, so no doc flags to update
				return true;

			if (minId > docid) {
				if (!updateHasPreviousVersionFlag(minId, FALSE_FLAG, conn)) {
					System.out.println("error in updateHasPreviousVersionFlag: " + minId);
					printErr("error in updateHasPreviousVersionFlag: " + minId);
					return false;
				}
			}

			if (maxId < docid && maxId >= STARTING_DOC_ID) {
				if (!updateIsLatestVersionFlag(maxId, TRUE_FLAG, conn)) {
					System.out.println("error in updateIsLatestVersionFlag: " + maxId);
					printErr("error in updateIsLatestVersionFlag: " + maxId);
					return false;
				}
			}

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean delDoc(ETSDoc doc, boolean delAll, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;

		try {
			connection = ETSDBUtils.getConnection();
			success = delDoc(doc, userid, delAll, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean delDoc(ETSDoc doc, String userid, boolean delAll, Connection conn) throws SQLException {
		Statement stmt = null;
		try {
			int docid = doc.getId();
			String projid = doc.getProjectId();

			int lowID = (docid / MAX_DOC_VERSIONS) * MAX_DOC_VERSIONS;
			int highID = lowID + MAX_DOC_VERSIONS;

			stmt = conn.createStatement();
			String update = "update ETS.ETS_DOC set latest_version='" + FALSE_FLAG + "', delete_flag='" + TRUE_FLAG + "',deleted_by='" + userid + "',deletion_date=current timestamp where DOC_ID >= " + lowID + " and DOC_ID < " + highID;

			int rowCount = stmt.executeUpdate(update);
			if (rowCount <= 0) {
				System.out.println("deldoc executeUpdate(" + update + ") returned " + rowCount);
				printErr("deldoc executeUpdate(" + update + ") returned " + rowCount);
				return false;
			}
			addContentLog(docid, 'D', projid, Defines.DELALL_DOC, userid);

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}

		finally {

			ETSDBUtils.close(stmt);
		}
	}

	public static synchronized boolean deleteProjectPlan(String projectid, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = deleteProjectPlan(projectid, userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean deleteProjectPlan(String projectid, String userid, Connection conn) throws SQLException {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(doc_id) as cnt from ets.ets_doc where DOC_TYPE = " + Defines.PROJECT_PLAN + " and project_id='" + projectid + "' with ur");
			int cnt = 0;
			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();

			if (cnt > 0) {
				String update = "delete from ETS.ETS_DOC where DOC_TYPE = " + Defines.PROJECT_PLAN + " and project_id='" + projectid + "'";
				int rowCount = stmt.executeUpdate(update);

				if (rowCount != 1) {
					System.out.println("delete project plan executeUpdate(" + update + ") returned " + rowCount);
					printErr("delete project plan executeUpdate(" + update + ") returned " + rowCount);
					return false;
				}

				addContentLog(Defines.ID_PROJECT_PLAN, 'D', projectid, Defines.DEL_PROJECT_PLAN, userid);
				stmt.close();
			}

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean deleteActionPlan(String projectid, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = deleteActionPlan(projectid, userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}

	public static synchronized boolean deleteActionPlan(String projectid, String userid, Connection conn) throws SQLException {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(doc_id) as cnt from ets.ets_doc where DOC_TYPE = " + Defines.SETMET_PLAN + " and project_id='" + projectid + "' with ur");
			int cnt = 0;
			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();

			if (cnt > 0) {
				String update = "delete from ETS.ETS_DOC where DOC_TYPE = " + Defines.SETMET_PLAN + " and project_id='" + projectid + "'";
				int rowCount = stmt.executeUpdate(update);

				if (rowCount != 1) {
					System.out.println("delete project plan executeUpdate(" + update + ") returned " + rowCount);
					printErr("delete project plan executeUpdate(" + update + ") returned " + rowCount);
					return false;
				}

				stmt.close();
			}

			addContentLog(Defines.ID_ACTION_PLAN, 'D', projectid, Defines.DEL_ACTION_PLAN, userid);
			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized void updateCatIbmOnly(char ibmonly, String catids, Connection conn) throws SQLException, Exception {
		PreparedStatement upCatSt = conn.prepareStatement("update ets.ets_cat set ibm_only=? where cat_id in (" + catids + ")");

		try {
			upCatSt.setString(1, String.valueOf(ibmonly));

			upCatSt.executeUpdate();
			upCatSt.close();
		} catch (SQLException se) {
			printErr("sql error in updatecatibmonly= " + se);
		}
	}

	public static synchronized void updateDocIbmOnlyPrivate(char ibmonly, String dprivate, String docids, Connection conn) throws SQLException, Exception {
		PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set ibm_only=?,isprivate=? where doc_id in (" + docids + ")");

		try {
			upDocSt.setString(1, String.valueOf(ibmonly));
			upDocSt.setString(2, dprivate);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			printErr("sql error in updatedocibmonly= " + se);
		}
	}
	public static synchronized void updateDocIbmOnly(char ibmonly, String docids, Connection conn) throws SQLException, Exception {
		PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set ibm_only=? where doc_id in (" + docids + ")");

		try {
			upDocSt.setString(1, String.valueOf(ibmonly));

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			printErr("sql error in updatedocibmonly= " + se);
		}
	}

	public static synchronized Vector getAllChildrenIds(int parentid, Vector v, boolean isAdmin, String userid, Connection conn) {
		String cats = "";
		String docs = "";

		try {
			v.addElement(cats);
			v.addElement(docs);

			Vector subcats = getSubCats(parentid);

			if (subcats.size() > 0) {
				for (int i = 0; i < subcats.size(); i++) {
					ETSCat c = (ETSCat) subcats.elementAt(i);
					if (c.getIbmOnly() != Defines.ETS_IBM_CONF && (isAdmin || c.getUserId().equals(userid))) {
						v = getAllSubChildrenIds(c.getId(), v, isAdmin, userid, conn);
						String c_temp = (String) v.elementAt(0);
						c_temp = c_temp + String.valueOf(c.getId()) + ",";
						v.setElementAt(c_temp, 0);
					}
				}
			}

			String d_temp = (String) v.elementAt(1);
			d_temp = getChildDocIds(parentid, d_temp, isAdmin, userid, conn);
			v.setElementAt(d_temp, 1);

		} catch (SQLException se) {
			System.out.println("sql error occurred in getallchildren= " + se);
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println("error occurred in getallchildren= " + e);
		}

		return v;
	}

	public static synchronized Vector getAllSubChildrenIds(int parentid, Vector v, boolean isAdmin, String userid, Connection conn) throws SQLException, Exception {

		try {
			Vector subcats = getSubCats(parentid);

			for (int i = 0; i < subcats.size(); i++) {
				ETSCat c = (ETSCat) subcats.elementAt(i);
				if (c.getIbmOnly() != Defines.ETS_IBM_CONF && (isAdmin || c.getUserId().equals(userid))) {
					v = getAllSubChildrenIds(c.getId(), v, isAdmin, userid, conn);
					String c_temp = (String) v.elementAt(0);
					c_temp = c_temp + String.valueOf(c.getId()) + ",";
					v.setElementAt(c_temp, 0);
				}
			}

			String d_temp = (String) v.elementAt(1);
			d_temp = getChildDocIds(parentid, d_temp, isAdmin, userid, conn);
			v.setElementAt(d_temp, 1);

		} catch (SQLException se) {
			System.out.println("sql error occurred in getAllSubchildren= " + se);
			se.printStackTrace();
		} catch (Exception e) {
			System.out.println("error occurred in getAllSubchildren= " + e);

		}

		return v;
	}

	public static synchronized String getChildDocIds(int parentid, String s, boolean isAdmin, String userid, Connection conn) throws SQLException, Exception {
		String d_temp = s;
		//String d_temp = "";

		Statement getDocsSt = conn.createStatement();

		String query = "select d.doc_id from ETS.ETS_DOC d where d.cat_id=" + parentid + " and d.ibm_only!='" + String.valueOf(Defines.ETS_IBM_CONF) + "' with ur";

		if (!isAdmin) {
			//what about versions?
			query =
				"SELECT d.doc_id FROM   ets.ets_doc d WHERE d.cat_id="
					+ parentid
					+ " and d.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " and d.user_id='"
					+ userid
					+ "'"
					+ " and d.latest_version='"
					+ Defines.TRUE_FLAG
					+ "'"
					+ " OR ("
					+ " and d.cat_id="
					+ parentid
					+ " and d.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " and doc_id IN"
					+ " (select doc_id FROM ets.ets_doc s"
					+ " WHERE EXISTS (SELECT doc_id"
					+ " FROM   ets.ets_doc ss"
					+ " WHERE ss.cat_id="
					+ parentid
					+ " AND   ss.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " AND   ss.user_id='"
					+ userid
					+ "'"
					+ " AND    ss.latest_version='"
					+ Defines.TRUE_FLAG
					+ "'"
					+ " AND    ss.doc_id"
					+ " between (s.doc_id/"
					+ MAX_DOC_VERSIONS
					+ ")*"
					+ MAX_DOC_VERSIONS
					+ ")"
					+ " and  (s.doc_id/"
					+ MAX_DOC_VERSIONS
					+ ")*("
					+ MAX_DOC_VERSIONS
					+ ")+"
					+ (MAX_DOC_VERSIONS - 1)
					+ "))"
					+ ") with ur";
			/*
			query = "select d.doc_id from ETS.ETS_DOC d " +
				" where d.cat_id="+parentid+" " +
				" and d.ibm_only!='"+String.valueOf(Defines.ETS_IBM_CONF)+"' " +
				" and d.user_id='"+userid+"'"+
				" and d.latest_version='"+Defines.TRUE_FLAG+"'"+
				" with ur";

			ResultSet rs = getDocsSt.executeQuery(query);

			Vector v = new Vector();
			while(rs.next()){
				v.addElement(new Integer(rs.getInt("doc_id")));
			}

			for (int i = 0;i<v.size();i++){
				int id = ((Integer)v.elementAt(i)).intValue();

				query = "select d.doc_id from ETS.ETS_DOC d "
				 +" where d.cat_id="+parentid
				 +" and d.ibm_only!='"+String.valueOf(Defines.ETS_IBM_CONF)+"'"
				 +" and d.doc_id >=  (("+id+" / "+MAX_DOC_VERSIONS+") * "+MAX_DOC_VERSIONS+")"
				 +" and d.doc_id <  ((("+id+" / "+MAX_DOC_VERSIONS+") * "+MAX_DOC_VERSIONS+")+"+MAX_DOC_VERSIONS+")"
				 +" with ur";

				rs = getDocsSt.executeQuery(query);

				while(rs.next()){
					System.out.println("docid="+rs.getInt("doc_id"));
					d_temp = d_temp + String.valueOf(rs.getInt("doc_id")) + ",";
				}
				rs.close();
			}*/

		} else {

			ResultSet rs = getDocsSt.executeQuery(query);

			while (rs.next()) {
				System.out.println("docid=" + rs.getInt("doc_id"));
				d_temp = d_temp + String.valueOf(rs.getInt("doc_id")) + ",";
			}
			rs.close();

		}

		System.out.println(d_temp);

		getDocsSt.close();
		return d_temp;

	}

	//SANDRA START HERE
	public static synchronized boolean updateCat(ETSCat cat, boolean isAdmin, String userid, String opt) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {

			//for testing
			//isAdmin = false;

			connection = ETSDBUtils.getConnection();
			success = updateCat(cat, connection);

			if (!opt.equals("1") && !opt.equals("")) {
				String catids = "";
				String docids = "";
				Vector v = new Vector();

				if (opt.equals("2")) { //only docs
					docids = getChildDocIds(cat.getId(), docids, isAdmin, userid, connection);
				} else if (opt.equals("3")) { //propogate to all nodes under this one
					v.addElement(catids);
					v.addElement(docids);
					v = getAllChildrenIds(cat.getId(), v, isAdmin, userid, connection);
					catids = (String) v.elementAt(0);
					docids = (String) v.elementAt(1);
				}

				System.out.println("c: " + catids);
				System.out.println("d: " + docids);

				if (!catids.equals("")) {
					catids = catids.substring(0, catids.length() - 1);
					updateCatIbmOnly(cat.getIbmOnly(), catids, connection);
				}
				if (!docids.equals("")) {
					docids = docids.substring(0, docids.length() - 1);
					updateDocIbmOnly(cat.getIbmOnly(), docids, connection);
				}

				if (!docids.equals("") && cat.getIbmOnly() != Defines.ETS_PUBLIC) {
					Vector vUsers = getAllDocRestrictedUserIds(docids, cat.getProjectId(), connection);
					String userids = "";
					for (int u = 0; u < vUsers.size(); u++) {
						try {
							String irid = (String) vUsers.elementAt(u);
							String edge_userid = AccessCntrlFuncs.getEdgeUserId(connection, irid);
							String decaftype = AccessCntrlFuncs.decafType(edge_userid, connection);
							if (!decaftype.equals("I")) {
								if (!userids.equals(""))
									userids = ",'" + irid + "'";
								else
									userids = "'" + irid + "'";
							}
						} catch (AMTException a) {
							System.out.println("amt exception in getibmmembers err= " + a);
						} catch (SQLException s) {
							System.out.println("sql exception in getibmmembers err= " + s);
						}
					}

					if (!userids.equals("")) {
						updateDocResUsers(docids, cat.getProjectId(), userids, connection);
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}

	public static synchronized boolean updateCat(ETSCat cat) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateCat(cat, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return success;
		}

	public static synchronized boolean updateCat(ETSCat cat, Connection conn) throws SQLException {
		PreparedStatement upCatSt = conn.prepareStatement("update ets.ets_cat set project_id=?,user_id=?,cat_name=?,parent_id=?,cat_type=?,cat_description=?,order=?,view_type=?,proj_desc=?,privs=?,ibm_only=?,isPrivate=? where cat_id=?");

		try {
			//spn 0312 projid
			upCatSt.setString(1, cat.getProjectId());
			upCatSt.setString(2, cat.getUserId());
			upCatSt.setString(3, cat.getName());
			upCatSt.setInt(4, cat.getParentId());
			upCatSt.setInt(5, cat.getCatType());
			upCatSt.setString(6, cat.getDescription());
			upCatSt.setInt(7, cat.getOrder());
			upCatSt.setInt(8, cat.getViewType());
			upCatSt.setInt(9, cat.getProjDesc());
			//upCatSt.setInt(10, cat.getProjMsg());
			upCatSt.setString(10, cat.getPrivsString());
			upCatSt.setString(11, String.valueOf(cat.getIbmOnly()));
			upCatSt.setString(12, cat.getCPrivate());
			upCatSt.setInt(13, cat.getId());

			upCatSt.executeUpdate();
			upCatSt.close();
			addContentLog(cat.getId(), 'C', cat.getProjectId(), Defines.UPDATE_CAT, cat.getUserId());
			return true;
		} catch (SQLException se) {
			se.printStackTrace();
			printErr("sql error =" + se);
			return false;
			//throw se;
		}

	}

	public static synchronized boolean updateDocProp(ETSDoc doc) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateDocProp(doc, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean updateDocProp(ETSDoc doc, boolean changeIbm, boolean changeRes, Vector addAllRes, String add, String remove, Vector vAdd) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			if (changeIbm || (changeRes || doc.IsDPrivate())) {
				success = updateDocProp(doc, changeIbm, changeRes, addAllRes, add, remove, vAdd, connection);
			} else {
				success = updateDocProp(doc, connection);
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean updateDocProp(ETSDoc doc, Connection conn) throws SQLException {
		//PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,doc_size=?,doc_upload_date=?,doc_publish_date=?,doc_update_date=?,meeting_date=? where doc_id=?");
		PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,ibm_only=?,updated_by=?,doc_update_date=current timestamp,expiry_date=?,isprivate=? where doc_id=?");
		try {
			//spn 0312 projid
			upDocSt.setString(1, doc.getProjectId());
			upDocSt.setInt(2, doc.getCatId());
			upDocSt.setString(3, doc.getUserId());
			upDocSt.setString(4, doc.getName());
			upDocSt.setString(5, doc.getDescription());
			upDocSt.setString(6, doc.getKeywords());
			upDocSt.setString(7, String.valueOf(doc.getIbmOnly()));
			upDocSt.setString(8, doc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (doc.getExpiryDate() == 0)
				upDocSt.setTimestamp(9, null);
			else
				upDocSt.setTimestamp(9, new Timestamp(doc.getExpiryDate()));

			upDocSt.setString(10, doc.getDPrivate());
			upDocSt.setInt(11, doc.getId());

			upDocSt.executeUpdate();
			upDocSt.close();
			addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.UPDATE_DOC_PROP, doc.getUserId());
			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			se.printStackTrace();
			return false;
			//throw se;
		}
	}
	public static synchronized boolean updateDocProp(ETSDoc doc, boolean changeIbm, boolean changeRes, Vector allAddResUsers, String add, String remove, Vector vAdd, Connection conn) throws SQLException {
		//PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,doc_size=?,doc_upload_date=?,doc_publish_date=?,doc_update_date=?,meeting_date=? where doc_id=?");
		PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,ibm_only=?,updated_by=?,doc_update_date=current timestamp,expiry_date=?,isprivate=? where doc_id=?");
		try {
			//spn 0312 projid
			upDocSt.setString(1, doc.getProjectId());
			upDocSt.setInt(2, doc.getCatId());
			upDocSt.setString(3, doc.getUserId());
			upDocSt.setString(4, doc.getName());
			upDocSt.setString(5, doc.getDescription());
			upDocSt.setString(6, doc.getKeywords());
			upDocSt.setString(7, String.valueOf(doc.getIbmOnly()));
			upDocSt.setString(8, doc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (doc.getExpiryDate() == 0)
				upDocSt.setTimestamp(9, null);
			else
				upDocSt.setTimestamp(9, new Timestamp(doc.getExpiryDate()));

			upDocSt.setString(10, doc.getDPrivate());
			upDocSt.setInt(11, doc.getId());

			upDocSt.executeUpdate();
			upDocSt.close();
			addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.UPDATE_DOC_PROP, doc.getUserId());

			if (changeRes || doc.IsDPrivate()) {

				if (allAddResUsers.size() > 0 && doc.IsDPrivate()) {
					addDocResUsers(allAddResUsers, String.valueOf(doc.getId()), doc.getProjectId());
				} else
					updateDocResUsers(doc, add, remove, conn);

			}

			updateDocPropPrev(doc, changeRes, remove, vAdd, conn);

			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			se.printStackTrace();
			return false;
			//throw se;
		} catch (Exception e) {
			printErr("exception error =" + e);
			e.printStackTrace();
			return false;
		}
	}

	public static synchronized boolean updateDocProp(ETSDoc doc, boolean changeIbm, boolean changeRes, Vector allAddResUsers, String add, String remove, Vector vAdd, Connection conn, boolean bIsNewUpload) throws SQLException {
		//PreparedStatement upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,doc_size=?,doc_upload_date=?,doc_publish_date=?,doc_update_date=?,meeting_date=? where doc_id=?");
		PreparedStatement upDocSt = null;

		if (bIsNewUpload) {
			upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,ibm_only=?,updated_by=?,doc_update_date=doc_upload_date,expiry_date=?,isprivate=? where doc_id=?");
		} else {
			upDocSt = conn.prepareStatement("update ets.ets_doc set project_id=?,cat_id=?,user_id=?,doc_name=?,doc_description=?,doc_keywords=?,ibm_only=?,updated_by=?,doc_update_date=current timestamp,expiry_date=?,isprivate=? where doc_id=?");
		}

		try {
			//spn 0312 projid
			upDocSt.setString(1, doc.getProjectId());
			upDocSt.setInt(2, doc.getCatId());
			upDocSt.setString(3, doc.getUserId());
			upDocSt.setString(4, doc.getName());
			upDocSt.setString(5, doc.getDescription());
			upDocSt.setString(6, doc.getKeywords());
			upDocSt.setString(7, String.valueOf(doc.getIbmOnly()));
			upDocSt.setString(8, doc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (doc.getExpiryDate() == 0)
				upDocSt.setTimestamp(9, null);
			else
				upDocSt.setTimestamp(9, new Timestamp(doc.getExpiryDate()));

			upDocSt.setString(10, doc.getDPrivate());
			upDocSt.setInt(11, doc.getId());

			upDocSt.executeUpdate();
			upDocSt.close();
			addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.UPDATE_DOC_PROP, doc.getUserId());

			if (changeRes || doc.IsDPrivate()) {

				if (allAddResUsers.size() > 0 && doc.IsDPrivate()) {
					addDocResUsers(allAddResUsers, String.valueOf(doc.getId()), doc.getProjectId());
				} else
					updateDocResUsers(doc, add, remove, conn);

			}

			updateDocPropPrev(doc, changeRes, remove, vAdd, conn);

			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			se.printStackTrace();
			return false;
			//throw se;
		} catch (Exception e) {
			printErr("exception error =" + e);
			e.printStackTrace();
			return false;
		}
	}

	public static synchronized boolean updateDocResUsers(ETSDoc doc, String add, String remove, Connection conn) throws SQLException, Exception {
		String query = "";
		Statement stmt = conn.createStatement();

		System.out.println(add);
		if ((!add.equals("")) && doc.IsDPrivate()) {
			query = "insert into ets.ets_private_doc(doc_id,user_id,project_id) values " + add;
			System.out.println("updateDocResUsers=" + query);
			stmt.executeUpdate(query);
		}
		if (!remove.equals("")) {
			query = "delete from ets.ets_private_doc" + " where doc_id = " + doc.getId() + " and project_id = '" + doc.getProjectId() + "'" + " and user_id in (" + remove + ")";
			System.out.println("updateDocResUsers=" + query);
			stmt.executeUpdate(query);
		}

		stmt.close();

		return true;
	}

	public static synchronized boolean updateDocResUsers(String docids, String projectid, String remove, Connection conn) throws SQLException, Exception {
		String query = "";
		Statement stmt = conn.createStatement();

		/*System.out.println(add);
		if ((!add.equals(""))){
			query = "insert into ets.ets_private_doc(doc_id,user_id,project_id) values "+add;
			System.out.println("updateDocResUsers="+query);
			stmt.executeUpdate(query);
		}*/

		if (!remove.equals("")) {
			query = "delete from ets.ets_private_doc" + " where doc_id in (" + docids + ")" + " and project_id = '" + projectid + "'" + " and user_id in (" + remove + ")";
			System.out.println("updateALLDocResUsers=" + query);
			stmt.executeUpdate(query);
		}

		stmt.close();

		return true;
	}

	public static synchronized boolean updateDocPropPrev(ETSDoc doc, boolean changeRes, String remove, Vector vAdd, Connection conn) throws SQLException, Exception {
		Vector prev = getPreviousVersions(doc.getId());
		String docids = "";
		for (int i = 0; i < prev.size(); i++) {
			ETSDoc pd = (ETSDoc) prev.elementAt(i);
			docids = docids + pd.getId() + ",";
		}

		if (!docids.equals("")) {
			docids = docids.substring(0, docids.length() - 1);
			updateDocIbmOnlyPrivate(doc.getIbmOnly(), doc.getDPrivate(), docids, conn);

			if (changeRes || doc.IsDPrivate()) {
				updateDocPrevResUsers(doc.getId(), prev, docids, doc.getProjectId(), changeRes, remove, vAdd, conn);
			}
		}

		return true;
	}

	public static synchronized boolean updateDocPrevResUsers(int docid, Vector prevdocs, String docids, String projectid, boolean changeRes, String remove, Vector vAdd, Connection conn) throws SQLException, Exception {
		String query = "";
		Statement stmt = conn.createStatement();
		String prevadd = "";

		if (vAdd.size() > 0 && prevdocs.size() > 0) {
			for (int i = 0; i < prevdocs.size(); i++) {
				if (((ETSDoc) prevdocs.elementAt(i)).getId() != docid) {
					for (int j = 0; j < vAdd.size(); j++) {
						if (!prevadd.equals(""))
							prevadd = prevadd + ",(" + ((ETSDoc) prevdocs.elementAt(i)).getId() + ",'" + vAdd.elementAt(j) + "','" + projectid + "')";
						else
							prevadd = "(" + ((ETSDoc) prevdocs.elementAt(i)).getId() + ",'" + vAdd.elementAt(j) + "','" + projectid + "')";
					}
				}

			}
			System.out.println("add=" + prevadd);
			if (!prevadd.equals("")) {
				query = "insert into ets.ets_private_doc(doc_id,user_id,project_id) values " + prevadd;
				System.out.println("updateDocPrevResUsers=" + query);
				stmt.executeUpdate(query);
			}
		}
		System.out.println("remove=" + remove);
		if (!remove.equals("")) {
			query = "delete from ets.ets_private_doc" + " where doc_id in (" + docids + ")" + " and project_id = '" + projectid + "'" + " and user_id in (" + remove + ")";
			System.out.println("updateDocPrevResUsers=" + query);
			stmt.executeUpdate(query);
		}

		stmt.close();

		return true;
	}
	/*
	static synchronized boolean updateDocProp(int docid,char ibmonly,String dprivate,Connection conn) throws SQLException,Exception {
		Vector prev = getPreviousVersions(docid);
		String docids = "";
		for (int i = 0; i < prev.size(); i++){
			ETSDoc pd = (ETSDoc)prev.elementAt(i);
			docids = docids + pd.getId() +",";
		}

		if (!docids.equals("")){
			docids = docids.substring(0,docids.length()-1);
			updateDocIbmOnly(ibmonly,dprivate,docids,conn);
		}


		return true;
	}
	*/

	/////////////////////////////
	// MEMBERS
	/////////////////////////////
	//spn 0312 projid
	public static boolean isProjectAdmin(String userid, String projid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return isProjectAdmin(userid, projid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}

	//spn 0312 projid
	public static boolean isProjectAdmin(String userid, String projid, Connection conn) throws SQLException {

		int priv_value = 0;

		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("select PRIV_VALUE from ETS.ETS_ROLES where PRIV_ID = 1 and ROLE_ID = (select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" + userid + "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '" + projid + "') with ur");

		if (rs.next()) {
			priv_value = rs.getInt("PRIV_VALUE");
		}

		rs.close();
		statement.close();

		return (priv_value == 1);

	}

	public static boolean hasProjectPriv(String userid, String projid, int priv) throws SQLException, Exception {
		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return hasProjectPriv(userid, projid, priv, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	public static boolean hasProjectPriv(String userid, String projid, int priv, Connection conn) throws SQLException {
		int priv_value = 0;
		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("select PRIV_VALUE from ETS.ETS_ROLES where PRIV_ID = " + priv + " and ROLE_ID = (select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '" + userid + "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '" + projid + "') with ur");

		if (rs.next()) {
			priv_value = rs.getInt("PRIV_VALUE");
		}

		rs.close();
		statement.close();

		return (priv_value == 1);
	}

	public static boolean doesRoleHavePriv(int role_id, int priv) throws SQLException, Exception {
		Connection conn = null;
		try {
			conn = ETSDBUtils.getConnection();
			return doesRoleHavePriv(role_id, priv, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(conn);
		}

	}
	public static boolean doesRoleHavePriv(int role_id, int priv, Connection conn) throws SQLException {
		int priv_value = 0;
		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("select PRIV_VALUE from ETS.ETS_ROLES where PRIV_ID = " + priv + " and ROLE_ID= " + role_id + " with ur");

		if (rs.next()) {
			priv_value = rs.getInt("PRIV_VALUE");
		}

		rs.close();
		statement.close();

		return (priv_value == 1);
	}

	public static Vector getPreviousVersions(int docID) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return getPreviousVersions(docID, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}

	// returns all versions of given doc, including current doc
	public static Vector getPreviousVersions(int docID, Connection conn) throws SQLException {

		int lowID = (docID / MAX_DOC_VERSIONS) * MAX_DOC_VERSIONS;
		int highID = lowID + MAX_DOC_VERSIONS;

		String query = "select d.*, f.DOCFILE_NAME, f.DOCFILE_UPDATE_DATE," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.DOC_ID >= " + lowID + " and d.DOC_ID < " + highID + " and d.DOC_ID = f.DOC_ID " + "and delete_flag!='" + TRUE_FLAG + "' order by d.DOC_ID desc with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		Vector allVersions = getDocs(rs);

		rs.close();
		statement.close();

		if (allVersions.size() > 0)
			return allVersions;
		else
			return null;

	}

	//spn 0312 projid
	public static String[] getUserRole(String userid, String projectid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;

		try {
			conn = ETSDBUtils.getConnection();
			return getUserRole(userid, projectid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static String[] getUserRole(String userid, String projectid, Connection conn) throws SQLException {

		String[] userRole = null;
		String role_id = null;

		String query1 = "select distinct ROLE_ID, ROLE_NAME from ETS.ETS_ROLES where PROJECT_ID = '" + projectid + "' and ROLE_ID in (select USER_ROLE_ID from ETS.ETS_USERS where USER_PROJECT_ID = '" + projectid + "' and USER_ID = '" + userid + "') with ur";
		System.out.println(query1);
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query1);

		if (rs.next()) {
			role_id = String.valueOf(rs.getInt("ROLE_ID"));
			String role_name = rs.getString("ROLE_NAME");
			userRole = new String[] { role_id, role_name, null };
		}

		rs.close();

		if (userRole == null)
			return null;

		String query2 = "select p.PRIV_NAME from ETS.ETS_ROLES r, ETS.ETS_PRIV p where r.PROJECT_ID = '" + projectid + "' and r.ROLE_ID = " + role_id + " and r.PRIV_VALUE = 1 and r.PRIV_ID = p.PRIV_ID order by r.PRIV_ID with ur";

		System.out.println(query2);
		rs = statement.executeQuery(query2);

		StringBuffer priv_names = new StringBuffer();
		while (rs.next()) {
			String priv_name = rs.getString(1);
			priv_names.append(priv_name);
			priv_names.append(',');
		}

		rs.close();
		statement.close();

		if (priv_names.length() > 0)
			priv_names.deleteCharAt(priv_names.length() - 1);

		userRole[2] = priv_names.toString();

		return userRole;

	}

	//spn 0312 projid
	public static Vector getRolesPrivs(String projectid) throws SQLException, Exception {

		//DbConnect dbConnect = new DbConnect();
		//dbConnect.makeConn();
		Connection conn = null;
		conn = ETSDBUtils.getConnection();

		try {
			return getRolesPrivs(projectid, conn);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}

	}
	//spn 0312 projid
	public static Vector getRolesPrivs(String projectid, Connection conn) throws SQLException {

		String query1 = "select distinct ROLE_ID, ROLE_NAME from ETS.ETS_ROLES where PROJECT_ID = '" + projectid + "' order by ROLE_ID with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query1);

		Vector v = new Vector();

		while (rs.next()) {
			String role_id = String.valueOf(rs.getInt("ROLE_ID"));
			String role_name = rs.getString("ROLE_NAME");
			v.add(new String[] { role_id, role_name, null, null });
		}

		rs.close();
		statement.close();

		int vSize = v.size();

		if (vSize <= 0)
			return null;

		String query2 = "select p.priv_id,p.PRIV_NAME from ETS.ETS_ROLES r, ETS.ETS_PRIV p where r.PROJECT_ID = '" + projectid + "' and r.ROLE_ID = ? and r.PRIV_VALUE = 1 and r.PRIV_ID = p.PRIV_ID order by r.PRIV_ID with ur";

		PreparedStatement pstmt = conn.prepareStatement(query2);

		for (int i = 0; i < vSize; i++) {
			String[] role = (String[]) v.elementAt(i);
			int role_id = Integer.parseInt(role[0]);
			pstmt.setInt(1, role_id);
			rs = pstmt.executeQuery();

			StringBuffer priv_ids = new StringBuffer();
			StringBuffer priv_names = new StringBuffer();
			while (rs.next()) {
				int privid = rs.getInt(1);
				String priv_name = rs.getString(2);
				priv_ids.append(privid);
				priv_ids.append(',');
				priv_names.append(priv_name);
				priv_names.append(',');
			}

			rs.close();

			if (priv_ids.length() > 0)
				priv_ids.deleteCharAt(priv_ids.length() - 1);

			if (priv_names.length() > 0)
				priv_names.deleteCharAt(priv_names.length() - 1);

			role[2] = priv_names.toString();
			role[3] = priv_ids.toString();
		}

		pstmt.close();

		return v;

	}
	public static String getRoleName(Connection conn, int roleid) throws SQLException {

		String role_name = "";

		String query1 = "select distinct ROLE_NAME from ETS.ETS_ROLES where ROLE_ID=" + roleid + " with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query1);

		if (rs.next()) {
			role_name = rs.getString("ROLE_NAME");
		}

		rs.close();
		statement.close();

		return role_name;

	}

//	spn 0312 projid
	  public static String getRoleName(int roleid) throws SQLException, Exception {

		  //DbConnect dbConnect = new DbConnect();
		  //dbConnect.makeConn();
		  Connection conn = null;
		  conn = ETSDBUtils.getConnection();
		  String roleName="";

		  try {
			roleName= getRoleName(conn,roleid);
		  } catch (SQLException e) {
			  throw e;
		  } catch (Exception ex) {
			  throw ex;
		  } finally {
			  //dbConnect.closeConn();
			  ETSDBUtils.close(conn);
		  }

		  return roleName;

	  }

	  public static synchronized boolean addDocMethod(ETSDoc doc, InputStream inStream) throws SQLException {
		return addDocMethod(doc, inStream, -1);
	}

	  public static synchronized boolean addDocMethod(ETSDoc doc, InputStream inStream, int existingDocID) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		Connection conn = null;
		boolean success = false;
		boolean newdoc = true;
		try {
			//dbConnect.makeConn();
			conn = ETSDBUtils.getConnection();

			int newDocID = -1;
			int mustUpdateIsLatestVersion = -1;

			if (existingDocID > STARTING_DOC_ID) { // update to an existing doc
				newdoc = false;
				newDocID = getUpdateDocID(conn, existingDocID);
				doc.setHasPreviousVersion(TRUE_FLAG);
				mustUpdateIsLatestVersion = newDocID - 1; // doc id of previous latest doc.
				//The IsLatestVersion flag of this doc needs to be changed
			} else { // brand new doc
				newdoc = true;
				newDocID = getNewDocID(conn);
				doc.setHasPreviousVersion(FALSE_FLAG);
			}

			printOut("returned newDocID=" + newDocID); //put here by spn to test

			if (newDocID < STARTING_DOC_ID || newDocID > MAXIMUM_DOC_ID) {
				printErr("out of range value for newDocID");
				return false;
			}

			doc.setId(newDocID);
			doc.setIsLatestVersion(TRUE_FLAG);
			printOut("docid=" + doc.getId());

			success = addDoc(doc, conn);
			if (!success) {
				printErr("error in add doc");
				return false;
			} else if (mustUpdateIsLatestVersion >= STARTING_DOC_ID && mustUpdateIsLatestVersion < MAXIMUM_DOC_ID) {
				// The IsLatestVersion flag of the older version to be changed to false
				success = updateIsLatestVersionFlag(mustUpdateIsLatestVersion, FALSE_FLAG, conn);
				if (!success) {
					printErr("error in updateIsLatestVersionFlag: " + mustUpdateIsLatestVersion);
					return false;
				}
			}

			success = addDocFile(doc, inStream, conn);
			if (!success) {
				printErr("error in add doc file");
				return false;
			}
			if (newdoc) {
				if (doc.getDocType() == Defines.DOC) {
					addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.ADD_DOC, doc.getUserId());
				} else if (doc.getDocType() == Defines.PROJECT_PLAN) {
					addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.ADD_PROJ_PLAN_DOC, doc.getUserId());
				} else if (doc.getDocType() == Defines.MEETING) {
					addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.ADD_MEETING_DOC, doc.getUserId());
				} else if (doc.getDocType() == Defines.EVENT) {
					addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.ADD_EVENT_DOC, doc.getUserId());
				} else if (doc.getDocType() == Defines.SETMET_PLAN) {
					addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.ADD_ACTION_PLAN, doc.getUserId());
				}
			} else {
				addContentLog(doc.getId(), 'D', doc.getProjectId(), Defines.UPDATE_DOC, doc.getUserId());
			}
		} catch (SQLException e) {
			success = false;
			printErr("error= " + getStackTrace(e));
			throw e;
		} catch (Exception ex) {
			success = false;
			printErr("ex error= " + getStackTrace(ex));
			//throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(conn);
		}
			return success;

	}

	private static synchronized int getNewDocID(Connection conn) throws SQLException {

		try {

			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select MAX(DOC_ID) from ETS.ETS_DOC");

			int maxid = -1;

			if (rs.next()) {
				maxid = rs.getInt(1);
			} else {
				printErr("Empty ResultSet for query: select MAX(DOC_ID) from ETS.ETS_DOC");
			}

			printOut("maxid=" + maxid); //spn to test

			rs.close();
			statement.close();

			if (maxid < STARTING_DOC_ID)
				maxid = STARTING_DOC_ID;

			if (maxid >= MAXIMUM_DOC_ID) {
				printErr("Will exceed MAXIMUM_DOC_ID");
				return -1;
			} else {
				return (((maxid / MAX_DOC_VERSIONS) + 1) * MAX_DOC_VERSIONS);
			}

		} catch (SQLException e) {
			printErr("error= " + getStackTrace(e));
			return -1;
		}

	}

	private static synchronized int getUpdateDocID(Connection conn, int existingDocID) throws SQLException {

		try {

			int lowestPossibleID = (existingDocID / MAX_DOC_VERSIONS) * MAX_DOC_VERSIONS;
			int highestPossibleID = lowestPossibleID + MAX_DOC_VERSIONS;

			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery("select MAX(DOC_ID) from ETS.ETS_DOC where DOC_ID >= " + lowestPossibleID + " and DOC_ID < " + highestPossibleID);

			int maxid = -1;
			if (rs.next()) {
				maxid = rs.getInt(1);
			} else {
				printErr("Empty ResultSet for query: select MAX(DOC_ID) from ETS.ETS_DOC");
			}

			rs.close();
			statement.close();

			if (maxid < STARTING_DOC_ID) { // should never happen
				printErr(existingDocID + ": maxid < STARTING_DOC_ID in getUpdateDocID: should never happen");
			} else if (((maxid % MAX_DOC_VERSIONS) + 1) >= MAX_DOC_VERSIONS) { // will exceed MAX_DOC_VERSIONS
				printErr(existingDocID + ": will exceed MAX_DOC_VERSIONS");
			} else if (maxid >= MAXIMUM_DOC_ID) {
				printErr("Will exceed MAXIMUM_DOC_ID");
			} else {
				return (maxid + 1);
			}

			return -1;

		} catch (SQLException e) {
			printErr("error= " + getStackTrace(e));
			return -1;
		}

	}

	private static synchronized boolean addDoc(ETSDoc doc, Connection conn) throws SQLException {
		//DbConnect dbConnect = new DbConnect();
		boolean success = false;
		try {
			//dbConnect.makeConn();
			//Connection conn = dbConnect.conn;
			/*
			PreparedStatement statement = conn.prepareStatement("insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp)");

			statement.setInt(1,doc.getId());
			//spn 0312 projid
			statement.setString(2,doc.getProjectId());
			statement.setInt(3,doc.getCatId());
			statement.setString(4,doc.getUserId());
			statement.setString(5,doc.getName());
			statement.setString(6,doc.getDescription());
			statement.setString(7,getFlagsAndKeywords(doc));
			statement.setInt(8,doc.getSize());
			*/

			//PreparedStatement statement = conn.prepareStatement("insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date, doc_type,updated_by,has_prev_version,latest_version,lock_final_flag,locked_by,delete_flag,deleted_by,meeting_id,ibm_only,document_status,approver_id,approval_comments) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement statement =
				conn.prepareStatement(
					"insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date, doc_type,updated_by,has_prev_version,latest_version,lock_final_flag,locked_by,delete_flag,deleted_by,meeting_id,ibm_only,document_status,approval_comments,expiry_date,self_id,isprivate) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			statement.setInt(1, doc.getId());
			//spn 0312 projid
			statement.setString(2, doc.getProjectId());
			statement.setInt(3, doc.getCatId());
			statement.setString(4, doc.getUserId());
			statement.setString(5, doc.getName());
			statement.setString(6, doc.getDescription());
			statement.setString(7, doc.getKeywords());
			statement.setInt(8, doc.getSize());

			statement.setInt(9, doc.getDocType());
			statement.setString(10, doc.getUpdatedBy());
			statement.setString(11, String.valueOf(doc.getHasPreviousVersion()));
			statement.setString(12, String.valueOf(doc.getIsLatestVersion()));
			statement.setString(13, String.valueOf(doc.getLockFinalFlag()));
			statement.setString(14, doc.getLockedBy());
			statement.setString(15, String.valueOf(doc.getDeleteFlag()));
			statement.setString(16, doc.getDeletedBy());
			//statement.setTimestamp(17,doc.getDeleteDate());
			statement.setString(17, doc.getMeetingId());
			statement.setString(18, String.valueOf(doc.getIbmOnly()));
			statement.setString(19, String.valueOf(doc.getDocStatus()));

			//statement.setString(20,doc.getApproverId());
			statement.setString(20, doc.getApprovalComments());
			//FIX FIX FIX FIX EXPIRE DATE
			if (doc.getExpiryDate() == 0) {
				statement.setTimestamp(21, null);
			} else
				statement.setTimestamp(21, new Timestamp(doc.getExpiryDate()));

			statement.setString(22, doc.getSelfId());
			statement.setString(23, doc.getDPrivate());

			statement.executeUpdate();
			statement.close();
			success = true;
		} catch (SQLException e) {
			success = false;
			printErr("sql error in add doc= " + e);
			throw e;
		} finally {
		}
			return success;
		}

	private static synchronized boolean addDocFile(ETSDoc doc, InputStream inStream, Connection conn) throws SQLException {
		boolean success = false;

		try {

			PreparedStatement statement = conn.prepareStatement("insert into ets.ets_docfile(doc_id,docfile_id,docfile_name,docfile,docfile_size,docfile_update_date) values(?,1,?,?,?,current timestamp)");

			System.out.println("DOC ID DOC ID==" + doc.getId());
			statement.setInt(1, doc.getId());
			statement.setString(2, doc.getFileName());
			statement.setBinaryStream(3, inStream, doc.getSize());
			statement.setInt(4, doc.getSize());
			statement.executeUpdate();
			statement.close();
			success = true;
		} catch (SQLException e) {
			success = false;
			printErr("sql error in add doc file= " + e);
			throw e;
		} finally {
		}
			return success;
		}

	/*
		 static synchronized boolean replaceDocMethod(ETSDoc doc, InputStream inStream, int existingDocID) throws SQLException {
			 Connection conn = null;
			 boolean success = false;
			 boolean newdoc = true;
			 try{
				 conn = ETSDBUtils.getConnection();

				 int mustUpdateIsLatestVersion = -1;

				 doc.setHasPreviousVersion(FALSE_FLAG);
				 doc.setId(existingDocID);
				 doc.setIsLatestVersion(TRUE_FLAG);
				 printOut("docid="+doc.getId());

				 success = replaceDoc(doc,conn);
				 if (!success) {
					 printErr("error in add doc");
					 return false;
				 }
				 success = replaceDocFile(doc,inStream,conn);
				 if (!success){
					 printErr("error in add doc file");
					 return false;
				 }
				 addContentLog(doc.getId(),'D',doc.getProjectId(),Defines.UPDATE_DOC,doc.getUserId());
			 }
			 catch(SQLException e) {
				 success = false;
				 printErr("error= " + getStackTrace(e));
				 throw e;
			 }
			 catch (Exception ex) {
				 success = false;
				 printErr("ex error= " + getStackTrace(ex));
			 }
			 finally{
				 ETSDBUtils.close(conn);
				 return success;
			 }
		 }

		static synchronized boolean replaceDoc(ETSDoc doc, Connection conn){
			boolean success = false;
			try{
				//PreparedStatement statement = conn.prepareStatement("insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date, doc_type,updated_by,has_prev_version,latest_version,lock_final_flag,locked_by,delete_flag,deleted_by,meeting_id,ibm_only,document_status,approver_id,approval_comments) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				PreparedStatement statement = conn.prepareStatement("insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date, doc_type,updated_by,has_prev_version,latest_version,lock_final_flag,locked_by,delete_flag,deleted_by,meeting_id,ibm_only,document_status,approval_comments) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?)");

				statement.setInt(1,doc.getId());
				statement.setString(2,doc.getProjectId());
				statement.setInt(3,doc.getCatId());
				statement.setString(4,doc.getUserId());
				statement.setString(5,doc.getName());
				statement.setString(6,doc.getDescription());
				statement.setString(7,doc.getKeywords());
				statement.setInt(8,doc.getSize());

				statement.setInt(9,doc.getDocType());
				statement.setString(10,doc.getUpdatedBy());
				statement.setString(11,String.valueOf(doc.getHasPreviousVersion()));
				statement.setString(12,String.valueOf(doc.getIsLatestVersion()));
				statement.setString(13,String.valueOf(doc.getLockFinalFlag()));
				statement.setString(14,doc.getLockedBy());
				statement.setString(15,String.valueOf(doc.getDeleteFlag()));
				statement.setString(16,doc.getDeletedBy());
				//statement.setTimestamp(17,doc.getDeleteDate());
				statement.setString(17,doc.getMeetingId());
				statement.setString(18,String.valueOf(doc.getIbmOnly()));
				statement.setString(19,String.valueOf(doc.getDocStatus()));

				//statement.setString(20,doc.getApproverId());
				statement.setString(20,doc.getApprovalComments());


				statement.executeUpdate();
				statement.close();
				success = true;
			}
			catch(SQLException e) {
				success = false;
				printErr("sql error in add doc= "+e);
				throw e;
			}
			finally{
				return success;
			}

		}
	*/

	public static String getFlagsAndKeywords(ETSDoc doc) {
		StringBuffer flagsAndKeywords = new StringBuffer(512);
		for (int i = 0; i < 12; i++) {
			flagsAndKeywords.append(NOT_SET_FLAG);
		}

		flagsAndKeywords.setCharAt(IS_LATEST_VERSION_FLAG, doc.getIsLatestVersion());
		flagsAndKeywords.setCharAt(HAS_PREVIOUS_VERSION_FLAG, doc.getHasPreviousVersion());
		String keywords = doc.getKeywords();
		if (keywords != null)
			flagsAndKeywords.append(keywords);

		return flagsAndKeywords.toString();
	}

	public static synchronized boolean updateIsLatestVersionFlag(int docid, char flag, Connection conn) throws SQLException {
		/*
		String query = "select DOC_KEYWORDS from ETS.ETS_DOC where DOC_ID = " + docid;
		    Statement stmt = conn.createStatement();

		try {
		    ResultSet rs = stmt.executeQuery(query);

		    StringBuffer flagsAndKeywords = null;

		    if(rs.next()) {
		        flagsAndKeywords = new StringBuffer(rs.getString("DOC_KEYWORDS"));
		        rs.close();
		        }
		        else {
		            printErr("Empty ResultSet for query: " + query);
		        rs.close();
		        return false;
		        }

		        for(int i = 0; i < 12; i++) {
		            char c = flagsAndKeywords.charAt(i);
		            if(c != NOT_SET_FLAG && c != TRUE_FLAG && c != FALSE_FLAG) {
		                printErr("flags not set in DOC_KEYWORDS");
			    return false;
		        }
		        }

		    flagsAndKeywords.setCharAt(IS_LATEST_VERSION_FLAG, flag);

		    String update = "update ETS.ETS_DOC set DOC_KEYWORDS = '" + flagsAndKeywords.toString() + "' where DOC_ID = " + docid;
		    int rowsUpdated = stmt.executeUpdate(update);

		    if(rowsUpdated == 1)
		        return true;
		    else {
		        printErr("executeUpdate(" + update + ") returned: " + rowsUpdated);
		        return false;
		    }
		}
		finally {
		    stmt.close();
		}
		*/

		Statement stmt = conn.createStatement();
		try {
			String update = "update ETS.ETS_DOC set LATEST_VERSION = '" + flag + "' where DOC_ID = " + docid;
			int rowsUpdated = stmt.executeUpdate(update);

			if (rowsUpdated == 1)
				return true;
			else {
				printErr("executeUpdate(" + update + ") returned: " + rowsUpdated);
				return false;
			}
		} finally {
			stmt.close();
		}

	}

	public static synchronized boolean updateHasPreviousVersionFlag(int docid, char flag, Connection conn) throws SQLException {
		/*
		String query = "select DOC_KEYWORDS from ETS.ETS_DOC where DOC_ID = " + docid;
		    Statement stmt = conn.createStatement();

		try {
		    ResultSet rs = stmt.executeQuery(query);

		    StringBuffer flagsAndKeywords = null;

		    if(rs.next()) {
		        flagsAndKeywords = new StringBuffer(rs.getString("DOC_KEYWORDS"));
		        rs.close();
		        }
		        else {
		            printErr("Empty ResultSet for query: " + query);
		        rs.close();
		        return false;
		        }

		        for(int i = 0; i < 12; i++) {
		            char c = flagsAndKeywords.charAt(i);
		            if(c != NOT_SET_FLAG && c != TRUE_FLAG && c != FALSE_FLAG) {
		                printErr("flags not set in DOC_KEYWORDS");
			    return false;
		        }
		        }

		        flagsAndKeywords.setCharAt(HAS_PREVIOUS_VERSION_FLAG, flag);

		    String update = "update ETS.ETS_DOC set DOC_KEYWORDS = '" + flagsAndKeywords.toString() + "' where DOC_ID = " + docid;
		    int rowsUpdated = stmt.executeUpdate(update);

		    if(rowsUpdated == 1)
		        return true;
		    else {
		        printErr("executeUpdate(" + update + ") returned: " + rowsUpdated);
		        return false;
		    }
		}
		finally {
		    stmt.close();
		}
		*/

		Statement stmt = conn.createStatement();
		try {
			String update = "update ETS.ETS_DOC set HAS_PREV_VERSION = '" + flag + "' where DOC_ID = " + docid;
			int rowsUpdated = stmt.executeUpdate(update);

			if (rowsUpdated == 1)
				return true;
			else {
				printErr("executeUpdate(" + update + ") returned: " + rowsUpdated);
				return false;
			}
		} finally {
			stmt.close();
		}

	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	public static void printOut(String str) {
		// System.out.println(str);
	}

	public static void printErr(String str) {
		System.err.println(str);
	}

	public static Vector getDocs(Connection conn, int parentid, String sProjectId, int iDocType) throws SQLException {
		PreparedStatement getDocsSt =
			conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f " + "where d.cat_id=? and d.project_id=? and d.doc_type=? and d.doc_id = f.doc_id " + "and delete_flag !='" + TRUE_FLAG + "' order by d.doc_upload_date desc with ur");

		getDocsSt.setInt(1, parentid);
		getDocsSt.setString(2, sProjectId);
		getDocsSt.setInt(3, iDocType);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getLatestDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;
	}

	public static Vector getLatestDocs(Connection conn, String sProjectId, int iMaxRows) throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}
		System.out.println("**00000***************");

		PreparedStatement getDocsSt =
			conn.prepareStatement(
				"select d.*, f.docfile_name, f.docfile_update_date,"
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
					+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = ? "
					+ "and d.latest_version= '"
					+ TRUE_FLAG
					+ "' and d.doc_id = f.doc_id and delete_flag!='"
					+ TRUE_FLAG
					+ "' "
					+ "and doc_type="
					+ Defines.DOC
					+ " order by d.doc_upload_date desc fetch first "
					+ iMaxRows
					+ " rows only with ur");

		getDocsSt.setString(1, sProjectId);

		System.out.println(
			"select d.*, f.docfile_name, f.docfile_update_date,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = '"
				+ sProjectId
				+ "' "
				+ "and d.latest_version= '"
				+ TRUE_FLAG
				+ "' and d.doc_id = f.doc_id and delete_flag!='"
				+ TRUE_FLAG
				+ "' "
				+ "and doc_type="
				+ Defines.DOC
				+ " order by d.doc_upload_date desc fetch first "
				+ iMaxRows
				+ " rows only with ur");

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;

	}
	public static Vector getLatestDocs(Connection conn, String sProjectId, String userid, boolean isExec, int iMaxRows) throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}
		System.out.println("***1111***************");

		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		String privDoc = "";
		if (!isExec)
			privDoc = "and (d.isprivate!='1' or d.isprivate is null or ('" + userid + "' in (select dp.user_id from ets.ets_private_doc dp where dp.doc_id=d.doc_id))) ";

		PreparedStatement getDocsSt =
			conn.prepareStatement(
				"select d.*, f.docfile_name, f.docfile_update_date,"
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
					+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = ? "
					+ "and d.latest_version= '"
					+ TRUE_FLAG
					+ "' and d.doc_id = f.doc_id and delete_flag!='"
					+ TRUE_FLAG
					+ "' "
					+ "and ((date(d.expiry_date)>date('"
					+ sqlToday
					+ "')) or (d.user_id=?) or d.expiry_date=? or d.expiry_date is null) "
					+ privDoc
					+ "and doc_type="
					+ Defines.DOC
					+ " order by d.doc_upload_date desc fetch first "
					+ iMaxRows
					+ " rows only with ur");

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, userid);
		getDocsSt.setTimestamp(3, new Timestamp(0));

		System.out.println(
			"select d.*, f.docfile_name, f.docfile_update_date,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = '"
				+ sProjectId
				+ "' "
				+ "and d.latest_version= '"
				+ TRUE_FLAG
				+ "' and d.doc_id = f.doc_id and delete_flag!='"
				+ TRUE_FLAG
				+ "' "
				+ "and ((date(d.expiry_date)>date('"
				+ sqlToday
				+ "')) or (d.user_id='"
				+ userid
				+ "') or d.expiry_date='"
				+ new Timestamp(0)
				+ "' or d.expiry_date is null) "
				+ privDoc
				+ "and doc_type="
				+ Defines.DOC
				+ " order by d.doc_upload_date desc fetch first "
				+ iMaxRows
				+ " rows only with ur");

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;

	}
	/*  SHOULD NEVER HAVE TO USE THIS
	static Vector getLatestDocs(Connection conn, String sProjectId, int iMaxRows,boolean isInternal,boolean isExec) throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}


	System.out.println("***2222222***************");
		PreparedStatement getDocsSt = conn.prepareStatement("select d.*, f.docfile_name, f.docfile_update_date," +
			"(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " +
			"from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = ? and d.latest_version= '" + TRUE_FLAG + "' " +
			"and ibm_only='"+Defines.ETS_PUBLIC+"' and d.doc_id = f.doc_id and delete_flag!='"+TRUE_FLAG+"' " +
			"and doc_type="+Defines.DOC+" order by d.doc_upload_date desc fetch first " + iMaxRows + " rows only with ur");

		getDocsSt.setString(1, sProjectId);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		getDocsSt.close();
		return docs;

	}*/
	public static Vector getLatestDocs(Connection conn, String sProjectId, String userid, int iMaxRows, boolean isInternal, boolean isExec) throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}
		System.out.println("***333333333333***************");

		//date(t1.due_date)=date('"+sqlToday+"')
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		System.out.println("DATE=" + sqlToday);

		String privDoc = "";
		if (!isExec)
			privDoc = "and (d.isprivate!='1' or d.isprivate is null or ('" + userid + "' in (select dp.user_id from ets.ets_private_doc dp where dp.doc_id=d.doc_id))) ";

		PreparedStatement getDocsSt =
			conn.prepareStatement(
				"select d.*, f.docfile_name, f.docfile_update_date,"
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
					+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = ? and d.latest_version= '"
					+ TRUE_FLAG
					+ "' "
					+ "and ibm_only='"
					+ Defines.ETS_PUBLIC
					+ "' and d.doc_id = f.doc_id and delete_flag!='"
					+ TRUE_FLAG
					+ "' "
					+ "and ((date(d.expiry_date)>date('"
					+ sqlToday
					+ "')) or (d.user_id=?) or date(d.expiry_date)=? or d.expiry_date is null) "
					+ privDoc
					+ "and doc_type="
					+ Defines.DOC
					+ " order by d.doc_upload_date desc fetch first "
					+ iMaxRows
					+ " rows only with ur");

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, userid);
		getDocsSt.setDate(3, new Date((new Timestamp(0)).getTime()));

		System.out.println(
			"select d.*, f.docfile_name, f.docfile_update_date,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = '"
				+ sProjectId
				+ "' and d.latest_version= '"
				+ TRUE_FLAG
				+ "' "
				+ "and ibm_only='"
				+ Defines.ETS_PUBLIC
				+ "' and d.doc_id = f.doc_id and delete_flag!='"
				+ TRUE_FLAG
				+ "' "
				+ "and ((date(d.expiry_date)>date('"
				+ sqlToday
				+ "')) or (d.user_id='"
				+ userid
				+ "') or d.expiry_date="
				+ new Timestamp(0)
				+ " or d.expiry_date is null) "
				+ privDoc
				+ "and doc_type="
				+ Defines.DOC
				+ " order by d.doc_upload_date desc");

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		System.out.println("db vdocs size=" + docs.size());

		rs.close();
		getDocsSt.close();
		return docs;

	}

	public static Vector getDocsForMeetings(Connection conn, String sProjectId, String sMeetingId, String sRepeatID) throws SQLException {

		PreparedStatement getDocsSt =
			conn.prepareStatement(
				"select d.*, f.docfile_name, f.docfile_update_date,"
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits "
					+ "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f "
					+ "where d.project_id = ? and (d.meeting_id= ? or d.meeting_id=?) and d.doc_id = f.doc_id "
					+ "and delete_flag!='"
					+ TRUE_FLAG
					+ "' and doc_type="
					+ Defines.MEETING 
					+ " and itar_upload_status='C'"
					+ " order by d.doc_upload_date desc with ur");

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, sMeetingId);
		getDocsSt.setString(3, sRepeatID);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();

		return docs;

	}

	public static int getUserGuide(int doctype) throws SQLException {
		Connection connection = null;
		int docid = 0;
		try {
			connection = ETSDBUtils.getConnection();
			docid = getUserGuide(doctype, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {

		} finally {
			ETSDBUtils.close(connection);
		}
			return docid;
		}
	public static int getUserGuide(int doctype, Connection conn) throws SQLException, Exception {
		int docid = 0;

		PreparedStatement getDocsSt = conn.prepareStatement("select doc_id from ets.ets_doc where doc_type=? and cat_id=-1 and delete_flag != '" + TRUE_FLAG + "' with ur");

		getDocsSt.setInt(1, doctype);

		ResultSet rs = getDocsSt.executeQuery();
		if (rs.next()) {
			docid = rs.getInt("DOC_ID");
		}

		rs.close();
		getDocsSt.close();
		return docid;
	}

//	spn 0312 projid
	 public  static ETSProj getProjectDetails(String projectidStr) throws SQLException, Exception {
		  //DbConnect dbConnect = new DbConnect();
		  //dbConnect.makeConn();
		  Connection connection = null;
		ETSProj etsProj = null;
		  try {
			  //Raja comment 6249
			  //connection = WrkSpcTeamUtils.getConnection();
			  connection = ETSDBUtils.getConnection();

			etsProj = getProjectDetails(connection,projectidStr);
		  } catch (SQLException e) {
			  throw e;
		  } catch (Exception ex) {
			  throw ex;
		  } finally {
			  //dbConnect.closeConn();
			  ETSDBUtils.close(connection);

		  }

		return etsProj;
	  }

	/**
	 * Method getProjectDetails.
	 * @param projectidStr
	 * @return ETSProj
	 */
	public static ETSProj getProjectDetails(Connection con, String projectidStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		ETSProj projDetails = new ETSProj();

		try {

			sQuery.append("SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START," + "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID," + "PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,IS_ITAR,PROJECT_TYPE,PROCESS " + "FROM ETS.ETS_PROJECTS " + "WHERE PROJECT_ID = '" + projectidStr + "' with ur");

			SysLog.log(SysLog.DEBUG, "ETSProjectServlet::getProjectDetails", "QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sProjDesc = ETSUtils.checkNull(rs.getString("PROJECT_DESCRIPTION"));

				String sProjName = ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
				Timestamp tProjStart = rs.getTimestamp("PROJECT_START");
				Timestamp tProjEnd = rs.getTimestamp("PROJECT_END");
				String sDecafProjName = ETSUtils.checkNull(rs.getString("DECAF_PROJECT_NAME"));
				String sProjOrProposal = ETSUtils.checkNull(rs.getString("PROJECT_OR_PROPOSAL"));
				String sLotusProjID = ETSUtils.checkNull(rs.getString("LOTUS_PROJECT_ID"));
				String sRelatedId = ETSUtils.checkNull(rs.getString("RELATED_ID"));
				String sParentId = ETSUtils.checkNull(rs.getString("PARENT_ID"));
				String sCompany = ETSUtils.checkNull(rs.getString("COMPANY"));
				String sPmoProjectId = ETSUtils.checkNull(rs.getString("PMO_PROJECT_ID"));
				String sShowIssueOwner = ETSUtils.checkNull(rs.getString("SHOW_ISSUE_OWNER"));
				String sProjectStatus = ETSUtils.checkNull(rs.getString("PROJECT_STATUS"));
				String sDelivery = ETSUtils.checkNull(rs.getString("DELIVERY_TEAM"));
				String sGeo = ETSUtils.checkNull(rs.getString("GEOGRAPHY"));
				String sIndustry = ETSUtils.checkNull(rs.getString("INDUSTRY"));
				String sIsITAR = ETSUtils.checkNull(rs.getString("IS_ITAR"));
				String projectType=ETSUtils.checkNull(rs.getString("PROJECT_TYPE"));
				String strProcess = ETSUtils.checkNull(rs.getString("PROCESS"));

				projDetails.setProjectId(sProjId);
				projDetails.setDescription(sProjDesc);
				projDetails.setName(sProjName);
				projDetails.setStartDate(tProjStart);
				projDetails.setEndDate(tProjEnd);
				projDetails.setDecafProject(sDecafProjName);
				projDetails.setProjectOrProposal(sProjOrProposal);
				projDetails.setLotusProject(sLotusProjID);
				projDetails.setRelatedProjectId(sRelatedId);
				projDetails.setParent_id(sParentId);
				projDetails.setCompany(sCompany);
				projDetails.setPmo_project_id(sPmoProjectId);
				projDetails.setShow_issue_owner(sShowIssueOwner);
				projDetails.setProject_status(sProjectStatus);
				projDetails.setProjBladeType(ETSUtils.isEtsProjBladeProject(projectidStr,con));

				projDetails.setDeliveryTeam(sDelivery);
				projDetails.setGeography(sGeo);
				projDetails.setIndustry(sIndustry);

				if (sIsITAR.equalsIgnoreCase("Y")) {
					projDetails.setITAR(true);
				} else {
					projDetails.setITAR(false);
				}

				projDetails.setProjectType(projectType);
				projDetails.setProcess(strProcess);

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return projDetails;
	}

	public static synchronized boolean addContentLog(int node_id, char node_type, String project_id, String action, String user) throws SQLException {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addContentLog(node_id, node_type, project_id, action, user, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			// throw ex;
		} finally {
			//dbConnect.closeConn();
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean addContentLog(int node_id, char node_type, String project_id, String action, String user, Connection conn) throws SQLException {
		PreparedStatement addLogSt = conn.prepareStatement("insert into ets.ets_content_log(timestamp, node_id,node_type,project_id,action,action_by) values(current timestamp,?,?,?,?,?)");

		try {
			addLogSt.setInt(1, node_id);
			addLogSt.setString(2, String.valueOf(node_type));
			addLogSt.setString(3, project_id);
			addLogSt.setString(4, action);
			addLogSt.setString(5, user);
			addLogSt.executeUpdate();

			addLogSt.close();
			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			return false;
		}

	}

	public static synchronized boolean addEmailLog(String mail_type, String key1, String key2, String key3, String project_id, String subject, String to, String cc) throws SQLException {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addEmailLog(mail_type, key1, key2, key3, project_id, subject, to, cc, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			//throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean addEmailLog(String mail_type, String key1, String key2, String key3, String project_id, String subject, String to, String cc, Connection conn) throws SQLException {
		PreparedStatement addLogSt = conn.prepareStatement("insert into ets.ets_email_log(timestamp, mail_type,key1,key2,key3,project_id,subject,to,cc) values(current timestamp,?,?,?,?,?,?,?,?)");

		try {
			addLogSt.setString(1, mail_type);
			addLogSt.setString(2, key1);
			addLogSt.setString(3, key2);
			addLogSt.setString(4, key3);
			addLogSt.setString(5, project_id);
			addLogSt.setString(6, subject);
			addLogSt.setString(7, to);
			addLogSt.setString(8, cc);
			addLogSt.executeUpdate();

			addLogSt.close();
			return true;
		} catch (SQLException se) {
			printErr("sql error =" + se);
			return false;
		}

	}

	public static Vector getUsersByProjectPriv(String projectid, int priv) throws SQLException, Exception {
		Vector v = new Vector();
		Connection connection = null;
		try {
			connection = ETSDBUtils.getConnection();
			v = getUsersByProjectPriv(projectid, priv, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			// throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}

	public static Vector getUsersByProjectPriv(String projectid, int priv, Connection conn) throws SQLException, Exception {

		String query = "select u.* from ets.ets_roles r, ets.ets_users u" + " where r.priv_id = " + priv + " and r.priv_value = 1" + " and u.user_project_id = '" + projectid + "'" + " and u.user_role_id = r.role_id" + " and u.user_project_id = r.project_id" + " and u.active_flag='A'" + " with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		Vector v = getUsers(rs);

		rs.close();
		statement.close();
		return v;
	}

	public static Vector getUsersByProjectPriv(String projectid, int priv, boolean checkamt) throws SQLException, Exception {
		Vector v = new Vector();
		Connection connection = null;
		try {
			connection = ETSDBUtils.getConnection();
			v = getUsersByProjectPriv(projectid, priv, checkamt, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			// throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}

	public static Vector getUsersByProjectPriv(String projectid, int priv, boolean checkamt, Connection conn) throws SQLException, Exception {

		String query = "select u.* from ets.ets_roles r, ets.ets_users u,amt.users a" + " where r.priv_id = " + priv + " and r.priv_value = 1" + " and u.user_project_id = '" + projectid + "'" + " and u.user_role_id = r.role_id" + " and u.user_project_id = r.project_id" + " and u.user_id = a.ir_userid " + " and u.active_flag='A'" + " order by a.USER_FULLNAME" + " with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		Vector v = getUsers(rs);

		rs.close();
		statement.close();
		return v;
	}

	public static Vector getProjectsUserIsOwner(String userid) throws SQLException, Exception {
		Vector v = new Vector();
		Connection connection = null;
		try {
			connection = ETSDBUtils.getConnection();
			v = getProjectsUserIsOwner(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			// throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}

	public static Vector getProjectsUserIsOwner(String userid, Connection conn) throws SQLException, Exception {
		Vector v = new Vector();

		String query = "select r.project_id from ets.ets_roles r, ets.ets_users u" + " where r.priv_id = " + Defines.OWNER + " and r.priv_value = 1" + " and u.user_id = '" + userid + "'" + " and u.active_flag='A'" + " and u.user_role_id = r.role_id with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			v.addElement((String) rs.getString("project_id"));
		}
		rs.close();
		statement.close();
		return v;
	}

	public static List getProjectsEmailIsMember(String email, Connection conn) throws SQLException {
		List l = new ArrayList();
		/*
		String query = "select distinct r.project_id from ets.ets_roles r, ets.ets_users u, amt.users a"
		+ " where r.priv_id = "+ Defines.USER
		+ " and r.priv_value = 1"
		+ " and u.user_id = a.ir_userid"
		+ " and a.user_email = '"+email+"'"
		+ " and u.user_role_id = r.role_id with ur";
		*/

		String query = "select distinct p.project_id from ets.ets_projects p, ets.ets_users u, amt.users a" + " where p.project_id = u.user_project_id" + " and u.user_id = a.ir_userid" + " and u.active_flag='A'" + " and a.user_email = '" + email + "' with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			l.add(rs.getString("project_id"));
		}
		rs.close();
		statement.close();
		return l;
	}

	public static List getProjectsEmailIsOwner(String email, Connection conn) throws SQLException {
		List l = new ArrayList();

		String query = "select distinct r.project_id from ets.ets_roles r, ets.ets_users u, amt.users a" + " where r.priv_id = " + Defines.OWNER + " and r.priv_value = 1" + " and u.user_id = a.ir_userid" + " and a.user_email = '" + email + "'" + " and u.active_flag='A'" + " and u.user_role_id = r.role_id with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		while (rs.next()) {
			l.add(rs.getString("project_id"));
		}
		rs.close();
		statement.close();
		return l;
	}

	public static int getTopCatId(String projectid, int viewtype) throws SQLException, Exception {
		int topcatid = 0;
		Connection connection = null;
		try {
			connection = ETSDBUtils.getConnection();
			topcatid = getTopCatId(projectid, viewtype, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			// throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return topcatid;
		}

	public static int getTopCatId(String projectid, int viewtype, Connection conn) throws SQLException, Exception {
		int topcatid = 0;

		String query = "select cat_id from ets.ets_cat" + " where view_type = " + viewtype + " and project_id = '" + projectid + "'" + " and parent_id=0 with ur";

		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);

		if (rs.next()) {
			topcatid = rs.getInt("cat_id");
		}
		rs.close();
		statement.close();

		return topcatid;
	}

	// 8888888888888888888888888888888888888888888
	public static boolean checkNameParentExists(String cat_name, int parent_id, Connection conn) throws SQLException {
		boolean exists = false;
		String query = "select cat_name from ETS.ETS_CAT where PARENT_ID = " + parent_id + " and cat_name = '" + cat_name + "' with ur";
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(query);
		if (rs.next())
			exists = true;
		rs.close();
		statement.close();
		return exists;
	}

	// --------------------------DEAL TRACKER------------------------------------------------
	/*

		static ETSTask getTask(String taskid, String projectid) throws SQLException {
		   Connection connection = null;
		   ETSTask task = null; //new ETSTask();
		   try{
			   connection = ETSDBUtils.getConnection();
			   task = getTask(taskid, projectid, connection);
		   }
		   catch (SQLException e) {
				e.printStackTrace();
			   throw e;
		   }
		   catch (Exception ex) {
			   //throw ex;
		   }
		   finally{
			   ETSDBUtils.close(connection);
				System.out.print("here in finally  t.getid()="+task.getId());
			   return task;
		   }
	}
	static ETSTask getTask(String taskid, String projectid,Connection conn) throws SQLException {
		   PreparedStatement getTaskSt = conn.prepareStatement("select * from ETS.ETS_TASK_MAIN where task_id=? and project_id=? order by task_id with ur");

		   getTaskSt.setInt(1, Integer.parseInt(taskid));
		   getTaskSt.setString(2, projectid);
		   ResultSet rs = getTaskSt.executeQuery();

		   ETSTask task = null;
		   if (rs.next()){
		   		System.out.print("here in rs next");
			   task = getTask(rs,false);
		   }

		   rs.close();
		   getTaskSt.close();
		   return task;
	}

		static Vector getTasks(String projectid) throws SQLException {
			Connection connection = null;
			Vector tasks = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				tasks= getTasks(projectid, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return tasks;
			}
		}
		static Vector getTasks(String projectid, Connection conn) throws SQLException {
			PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d where d.project_id=? and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  from ETS.ETS_TASK_MAIN t where t.project_id=? order by t.task_id with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
			getTasksSt.setString(3, projectid);
			ResultSet rs = getTasksSt.executeQuery();

			Vector tasks = new Vector();
			tasks = getTasks(rs,true);

			rs.close();
			getTasksSt.close();
			return tasks;
		}
		static Vector getTasks(String projectid,String sortby, String ad) throws SQLException {
			Connection connection = null;
			Vector tasks = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				tasks= getTasks(projectid, sortby, ad, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return tasks;
			}
		}
		static Vector getTasks(String projectid, String sortby, String ad,Connection conn) throws SQLException {
			String sb = "t.task_id";

			if (sortby.equals(Defines.SORT_BY_DT_DATE_STR)){
				sb = "t.due_date";
			}
			else if (sortby.equals(Defines.SORT_BY_DT_SECT_STR)){
				sb = "t.section";
			}
			else if (sortby.equals(Defines.SORT_BY_DT_STATUS_STR)){
				sb = "t.status "+ad+",t.task_id";
			}
			else if (sortby.equals(Defines.SORT_BY_DT_TITLE_STR)){
				sb = "t.title";
			}
			else if (sortby.equals(Defines.SORT_BY_DT_OWNER_STR)){
				sb = "t.owner_id";
			}



			PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d "+
				"where d.project_id=? and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  "+
				"from ETS.ETS_TASK_MAIN t where t.project_id=? order by "+sb+" "+ad+" with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
			getTasksSt.setString(3, projectid);
			ResultSet rs = getTasksSt.executeQuery();

			Vector tasks = new Vector();
			tasks = getTasks(rs,true);

			rs.close();
			getTasksSt.close();
			return tasks;
		}

		static Vector getTasks(String taskids, String projectid) throws SQLException {
			Connection connection = null;
			Vector tasks = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				tasks= getTasks(taskids,projectid, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return tasks;
			}
		}
		static Vector getTasks(String taskids,String projectid, Connection conn) throws SQLException {
			PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d " +
				"where d.project_id=? and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
				"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.task_id in ("+taskids+") order by t.task_id with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
			getTasksSt.setString(3, projectid);
			ResultSet rs = getTasksSt.executeQuery();

			Vector tasks = new Vector();
			tasks = getTasks(rs,true);

			rs.close();
			getTasksSt.close();
			return tasks;
		}






		static Vector getTaskDocs(int taskid,String projectid) throws SQLException {
			Connection connection = null;
			Vector taskdocs = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				taskdocs= getTaskDocs(taskid, projectid,connection);
			}
			catch (SQLException e) {
				System.out.println("sql e = "+e);
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				System.out.println("ex e = "+ex);

				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return taskdocs;
			}
		}
		static Vector getTaskDocs(int taskid, String projectid, Connection conn) throws SQLException {
			//PreparedStatement getTasksSt = conn.prepareStatement("select * from ETS.ETS_DOC where project_id=? and meeting_id=? order by doc_id with ur");
			PreparedStatement getTasksSt = conn.prepareStatement("select d.*,f.docfile_name,f.docfile_update_date," +
				"(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " +
				"from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id=? and meeting_id=? and cat_id=? " +
				"and doc_type=? and d.doc_id = f.doc_id and delete_flag !='"+TRUE_FLAG+"' order by d.doc_name with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(taskid));
			getTasksSt.setInt(3,-3);
			getTasksSt.setInt(4,Defines.TASK);

			ResultSet rs = getTasksSt.executeQuery();

			Vector tasks = new Vector();
			tasks = getDocs(rs);

			rs.close();
			getTasksSt.close();
			return tasks;
		}


		static synchronized String addTask(ETSTask t) throws SQLException {
			Connection connection = null;
			String success = "0";
			try{
				connection = ETSDBUtils.getConnection();
				success= addTask(t, connection);
			}
			catch (SQLException e) {
				throw e;
			}
			catch (Exception ex) {
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static synchronized String addTask(ETSTask t, Connection conn) throws SQLException {
			String success = "0";
			int taskid = getTaskId(t.getProjectId(),conn);

			//boolean dup = isDuplicateTask(t,conn);

			PreparedStatement addTaskSt = conn.prepareStatement("insert into ets.ets_task_main(task_id,project_id,creator_id,created_date,section,title,description,status,due_date,owner_id,work_required,action_required,ibm_only,parent_task_id,company,last_userid,last_timestamp) values(?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp)");


			try{
				addTaskSt.setInt(1, taskid);
				addTaskSt.setString(2,t.getProjectId());
				addTaskSt.setString(3,t.getCreatorId());
				//addTaskSt.setTimestamp(4,t.getCreatedDate());
				addTaskSt.setString(4,t.getSection());
				addTaskSt.setString(5,t.getTitle());
				addTaskSt.setString(6,t.getDescription());
				addTaskSt.setString(7,t.getStatus());
				addTaskSt.setTimestamp(8,t.getTSDueDate());
				addTaskSt.setString(9,t.getOwnerId());
				addTaskSt.setString(10,t.getWorkRequired());
				addTaskSt.setString(11,t.getActionRequired());
				addTaskSt.setString(12,String.valueOf(t.getIbmOnly()));
				addTaskSt.setInt(13,t.getParentTaskId());
				addTaskSt.setString(14,t.getCompany());
				addTaskSt.setString(15,t.getLastUserid());

				addTaskSt.executeUpdate();
				addTaskSt.close();


				String taskidStr = String.valueOf(taskid);
				//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
				success = taskidStr;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				se.printStackTrace();
				return "0";
			}

			return success;
		}
		private static synchronized int getTaskId(String proj_id,Connection conn) throws SQLException {
			int task_id=1;
			Statement statement = conn.createStatement();

			ResultSet rs = statement.executeQuery("select max(task_id) as task_id from ets.ets_task_main where project_id='"+proj_id+"' with ur");

			if(rs.next()) {
				task_id = rs.getInt("task_id") + 1;
			}

			System.out.println("task_id="+task_id);

			rs.close();
			statement.close();
			return task_id;
		}


		static synchronized boolean editTask(ETSTask t) throws SQLException {
			Connection connection = null;
			boolean success = false;
			try{
				connection = ETSDBUtils.getConnection();
				success= editTask(t, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static synchronized boolean editTask(ETSTask t, Connection conn) throws SQLException {
			boolean success = false;

			PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main set section=?,title=?,description=?,status=?,due_date=?,owner_id=?,work_required=?,action_required=?,ibm_only=?,company=?,last_userid=?,last_timestamp=current timestamp where task_id=? and project_id=?");


			try{
				taskSt.setString(1,t.getSection());
				taskSt.setString(2,t.getTitle());
				taskSt.setString(3,t.getDescription());
				taskSt.setString(4,t.getStatus());
				taskSt.setTimestamp(5,t.getTSDueDate());
				taskSt.setString(6,t.getOwnerId());
				taskSt.setString(7,t.getWorkRequired());
				taskSt.setString(8,t.getActionRequired());
				taskSt.setString(9,String.valueOf(t.getIbmOnly()));
				taskSt.setString(10,t.getCompany());
				taskSt.setString(11,t.getLastUserid());
				taskSt.setInt(12,t.getId());
				taskSt.setString(13,t.getProjectId());

				taskSt.executeUpdate();
				taskSt.close();

				//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
				success = true;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				return false;
			}

			return success;
		}


		static public Vector getTaskComments(int taskid,String projectid) throws SQLException {
			Connection connection = null;
			Vector comments = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				comments= getTaskComments(taskid, projectid,connection);
			}
			catch (SQLException e) {
				System.out.println("sql e = "+e);
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				System.out.println("ex e = "+ex);

				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return comments;
			}
		}
		static Vector getTaskComments(int taskid, String projectid, Connection conn) throws SQLException {
			Vector v = new Vector();
			PreparedStatement tasksSt = conn.prepareStatement("select * from ets.ets_task_comments where  task_id=? and project_id=? order by seq_no desc with ur");

			tasksSt.setInt(1, taskid);
			tasksSt.setString(2, projectid);

			ResultSet rs = tasksSt.executeQuery();
			while (rs.next()){
				ETSTaskComment t = new ETSTaskComment();
			  	t.setLastTimestamp(rs.getTimestamp("last_timestamp"));
				t.setLastUserid(rs.getString("last_userid"));
				t.setComment(rs.getString("comments"));

				v.addElement(t);
			}

			rs.close();
			tasksSt.close();
			return v;
		}

		static synchronized int addTaskComment(ETSTaskComment t) throws SQLException {
			Connection connection = null;
			int success = 0;
			try{
				connection = ETSDBUtils.getConnection();
				success= addTaskComment(t, connection);
			}
			catch (SQLException e) {
				success = 0;
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				success = 0;
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static synchronized int addTaskComment(ETSTaskComment t, Connection conn) throws SQLException {
			int success = 0;

			int seqno = getSeqNo(t.getProjectId(),t.getTaskId(),conn);

			PreparedStatement addTaskSt = conn.prepareStatement("insert into ets.ets_task_comments(task_id,project_id,seq_no,comments,last_userid,last_timestamp) values(?,?,?,?,?,current timestamp)");


			try{
				addTaskSt.setInt(1, t.getTaskId());
				addTaskSt.setString(2,t.getProjectId());
				addTaskSt.setInt(3,seqno);
				addTaskSt.setString(4,t.getComment());
				addTaskSt.setString(5,t.getLastUserid());

				addTaskSt.executeUpdate();
				addTaskSt.close();

				//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
				success = seqno;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				se.printStackTrace();
				return 0;
			}

			return success;
		}
		private static synchronized int getSeqNo(String proj_id,int taskid,Connection conn) throws SQLException {
			int seqno=1;
			Statement statement = conn.createStatement();

			ResultSet rs = statement.executeQuery("select max(seq_no) as seqno from ets.ets_task_comments where project_id='"+proj_id+"' and task_id="+taskid+" with ur");

			if(rs.next()) {
				seqno = rs.getInt("seqno") + 1;
			}

			System.out.println("seqno=="+seqno);

			rs.close();
			statement.close();
			return seqno;
		}


		static synchronized boolean delTask(int taskid,String projectid) throws SQLException {
			boolean success = false;
			Connection connection = null;

			try{
				connection = ETSDBUtils.getConnection();
				success= delTask(taskid,projectid, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static synchronized boolean delTask(int taskid, String projectid, Connection conn) throws SQLException {
			boolean success = false;

			try{
				success = delTaskDocs(taskid,projectid,conn);
				if (success){
					success = delTaskComms(taskid,projectid,conn);
					if (success){
						PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_task_main where project_id=? and task_id=?");
						taskSt.setString(1, projectid);
						taskSt.setInt(2, taskid);

						taskSt.executeUpdate();
						taskSt.close();

						//addContentLog(catid,'C',cat.getProjectId(),Defines.ADD_CAT,cat.getUserId());
						success = true;
					}
				}
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				return false;
			}

			return success;
		}

		static synchronized boolean delTaskDocs(int taskid, String projectid, Connection conn) throws SQLException {
			boolean success = false;
			PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_doc where project_id=? and meeting_id=? and cat_id=? and doc_type=?");


			try{
				taskSt.setString(1, projectid);
				taskSt.setString(2, String.valueOf(taskid));
				taskSt.setInt(3,-3);
				taskSt.setInt(4,Defines.TASK);

				taskSt.executeUpdate();
				taskSt.close();

				//addContentLog(catid,'C',cat.getProjectId(),Defines.DEL_DOC,cat.getUserId());
				success = true;
			}
			catch (SQLException se) {
				se.printStackTrace();
				System.out.println("sql error ="+se);
				return false;
			}
			return success;
		}


		static synchronized boolean delTaskComms(int taskid, String projectid, Connection conn) throws SQLException {
			boolean success = false;
			PreparedStatement taskSt = conn.prepareStatement("delete from ets.ets_task_comments where project_id=? and task_id=?");

			try{
				taskSt.setString(1, projectid);
				taskSt.setInt(2, taskid);
				taskSt.executeUpdate();
				taskSt.close();

				//addContentLog(catid,'C',cat.getProjectId(),Defines.DEL_DOC,cat.getUserId());
				success = true;
			}
			catch (SQLException se) {
				se.printStackTrace();
				System.out.println("sql error ="+se);
				return false;
			}
			return success;
		}



		static Vector getDepTasks(int taskid,String projectid) throws SQLException {
			Connection connection = null;
			Vector tasks = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				tasks= getDepTasks(taskid,projectid, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return tasks;
			}
		}
		static Vector getDepTasks(int taskid,String projectid, Connection conn) throws SQLException {
			PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d where d.project_id=? " +
				"and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
				"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.parent_task_id=? order by t.task_id with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
			getTasksSt.setString(3, projectid);
			getTasksSt.setInt(4, taskid);
			ResultSet rs = getTasksSt.executeQuery();

			Vector tasks = new Vector();
			tasks = getTasks(rs,true);

			rs.close();
			getTasksSt.close();
			return tasks;
		}



		static Vector getEligibleTasks(int taskid,boolean isIbmOnly,String projectid,boolean userexternal) throws SQLException, Exception{
			Connection connection = null;
			Vector tasks = new Vector();
			try{
				connection = ETSDBUtils.getConnection();
				tasks= getEligibleTasks(taskid,isIbmOnly,projectid,userexternal,connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return tasks;
			}

		}

		static Vector getEligibleTasks(int taskid,boolean isIbmOnly,String projectid,boolean userexternal,Connection conn) throws SQLException, Exception{
			Vector tasks = new Vector();
			Vector notInIds = getNotEligibleIds(taskid,projectid,conn);
			String sNotIds = "";

			for (int i=0; i< notInIds.size(); i++){
				if(i==0){
					sNotIds = (String)notInIds.elementAt(i);
				}
				else{
					sNotIds = sNotIds + "," +(String)notInIds.elementAt(i);
				}
				System.out.println(i+"::"+sNotIds);
			}

			String setNotIds = "";
			if (!sNotIds.equals("")){
				setNotIds = "and t.task_id not in ("+sNotIds+")";
			}

			String setIbmOnly="";
			if (isIbmOnly && userexternal){
				//error
				setIbmOnly = "t.ibm_only='Q'";
			}
			else if (isIbmOnly && !userexternal){
				setIbmOnly = " and t.ibm_only='"+ETSDatabaseManager.TRUE_FLAG+"' ";
			}
			else if (!isIbmOnly && userexternal){
				setIbmOnly = " and t.ibm_only!='"+ETSDatabaseManager.TRUE_FLAG+"' ";
			}

			PreparedStatement getTasksSt = conn.prepareStatement("select t.*,(select count(*) from ets.ets_doc d where d.project_id=? " +
				"and cast(d.meeting_id as smallint)= t.task_id and cat_id=-3 and doc_type=6 and delete_flag !=?) as cnt  " +
				"from ETS.ETS_TASK_MAIN t where t.project_id=? and t.parent_task_id=?"+setNotIds+setIbmOnly+"order by t.task_id with ur");

			getTasksSt.setString(1, projectid);
			getTasksSt.setString(2, String.valueOf(TRUE_FLAG));
			getTasksSt.setString(3, projectid);
			getTasksSt.setInt(4, 0);
			//getTasksSt.setString(5, sNotIds);

			ResultSet rs = getTasksSt.executeQuery();

			tasks = getTasks(rs,true);

			rs.close();
			getTasksSt.close();
			return tasks;
		}

		static Vector getNotEligibleIds(int taskid,String projectid,Connection conn) throws SQLException, Exception{
			Vector ids = new Vector();
			ETSTask t = getTask(String.valueOf(taskid),projectid,conn);
			//ids.addElement(new Integer(taskid));
			ids.addElement(String.valueOf(taskid));
			if (t.getParentTaskId()!=0){
				System.out.println("here in not eligible ids parent");
				getNotEligibleIdsRec(t.getParentTaskId(),projectid,ids,conn);
			}
			return ids;
		}
		static Vector getNotEligibleIdsRec(int taskid,String projectid,Vector ids,Connection conn) throws SQLException, Exception{

			ETSTask t = getTask(String.valueOf(taskid),projectid,conn);
			//ids.addElement(new Integer(taskid));
			if (!ids.contains(String.valueOf(t.getId()))){
				ids.addElement(String.valueOf(taskid));
				if (t.getParentTaskId()!=0){
					ids=getNotEligibleIdsRec(t.getParentTaskId(),projectid,ids,conn);
				}
			}
			return ids;
		}







		static boolean addDepTasks(String taskid,String dtasks, String projectid) throws SQLException{
			Connection connection = null;
			boolean success = false;
			try{
				connection = ETSDBUtils.getConnection();
				success= addDepTasks(taskid, dtasks, projectid, connection);
			}
			catch (SQLException e) {
				throw e;
			}
			catch (Exception ex) {
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static boolean addDepTasks(String taskid, String dtasks, String projectid,Connection conn) throws SQLException {
			boolean success = false;

			PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main " +
				"set parent_task_id=? where task_id in("+dtasks+") and project_id=?");


			try{
				taskSt.setInt(1,new Integer(taskid).intValue());
				//taskSt.setString(2,dtasks);
				taskSt.setString(2,projectid);
				taskSt.executeUpdate();
				taskSt.close();

				success = true;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				se.printStackTrace();
				return false;
			}

			return success;
		}


		static boolean removeDepTask(int taskid, String projectid) throws SQLException {
			Connection connection = null;
			boolean success = false;
			try{
				connection = ETSDBUtils.getConnection();
				success= removeDepTask(taskid, projectid, connection);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				//throw ex;
			}
			finally{
				ETSDBUtils.close(connection);
				return success;
			}
		}

		static boolean removeDepTask(int taskid,String projectid, Connection conn) throws SQLException {
			boolean success = false;

			PreparedStatement taskSt = conn.prepareStatement("update ets.ets_task_main " +
				"set parent_task_id=? where task_id=? and project_id=?");


			try{
				taskSt.setInt(1,0);
				taskSt.setInt(2,taskid);
				taskSt.setString(3,projectid);

				taskSt.executeUpdate();
				taskSt.close();
				success = true;
			}
			catch (SQLException se) {
				System.out.println("sql error ="+se);
				return false;
			}
			return success;
		}



		private static Vector getTasks(ResultSet rs, boolean includeCnt) throws SQLException {
		 Vector v = new Vector();

		 while (rs.next()) {
				 ETSTask task = getTask(rs, includeCnt);
				 v.addElement(task);
		 }
		 return v;
		}

		 private static ETSTask getTask(ResultSet rs, boolean includeCnt) throws SQLException {
			 ETSTask task = new ETSTask();
			 task.setId(rs.getInt("TASK_ID"));
			 System.out.println("taskid="+task.getId());
			 task.setProjectId(rs.getString("PROJECT_ID"));
			 task.setCreatorId(rs.getString("CREATOR_ID"));
			 task.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
			 task.setSection(rs.getString("SECTION"));
			 task.setTitle(rs.getString("TITLE"));
			 task.setDescription(rs.getString("DESCRIPTION"));
			 task.setStatus(rs.getString("STATUS"));
			 task.setDueDate(rs.getTimestamp("DUE_DATE"));
			 task.setOwnerId(rs.getString("OWNER_ID"));
			 task.setWorkRequired(rs.getString("WORK_REQUIRED"));
			 task.setActionRequired(rs.getString("ACTION_REQUIRED"));
			 task.setIbmOnly(rs.getString("IBM_ONLY"));
			 task.setParentTaskId(rs.getInt("PARENT_TASK_ID"));
			 task.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
			 task.setLastUserid(rs.getString("LAST_USERID"));
			 task.setCompany(rs.getString("COMPANY"));
			 if (includeCnt){
			 	System.out.println("cnt="+rs.getInt("CNT"));
			 	task.setHasDocs(rs.getInt("CNT"));
			 }
			 System.out.println("t.getid in gettaskrs="+task.getId());
			 return task;
		 }



	*/
	//  --------------------------DEAL TRACKER------------------------------------------------

	//	--------------------------TEAM------------------------------------------------
	public static synchronized boolean deleteUserPhoto(String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = deleteUserPhoto(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean deleteUserPhoto(String userid, Connection conn) throws SQLException {
		try {
			String query = "update ets.ets_user_info set user_photo=null where user_id='" + userid + "'";
			Statement s = conn.createStatement();

			s.executeUpdate(query);
			s.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean addUserPhoto(String userid, InputStream inStream, int size) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addUserPhoto(userid, inStream, size, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean addUserPhoto(String userid, InputStream inStream, int size, Connection conn) throws SQLException {
		try {
			//check if exsits first.  if no, insert, else update
			int cnt = 0;
			String query = "select count(*) as cnt from ets.ets_user_info where user_id='" + userid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
			stmt.close();

			PreparedStatement ps;
			if (cnt > 0) {
				ps = conn.prepareStatement("update ets.ets_user_info set user_photo=?,last_timestamp=current timestamp where user_id=?");
				ps.setBinaryStream(1, inStream, size);
				ps.setString(2, userid);
			} else {
				ps = conn.prepareStatement("insert into ets.ets_user_info(user_id,user_photo,last_timestamp) values(?,?,current timestamp)");
				ps.setString(1, userid);
				ps.setBinaryStream(2, inStream, size);
			}

			ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean updateUserPhoto(String userid, InputStream inStream, int size) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateUserPhoto(userid, inStream, size, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean updateUserPhoto(String userid, InputStream inStream, int size, Connection conn) throws SQLException {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("update ets.ets_user_info set user_photo=?,last_timestamp=current timestamp where user_id=?");
			ps.setBinaryStream(1, inStream, size);
			ps.setString(2, userid);

			ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean deleteUserCV(String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = deleteUserCV(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean deleteUserCV(String userid, Connection conn) throws SQLException {
		try {
			String query = "update ets.ets_user_info set user_cv=null where user_id='" + userid + "'";
			Statement s = conn.createStatement();

			s.executeUpdate(query);
			s.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}
	public static synchronized boolean addUserCVSkills(String userid, InputStream inStream, int size, String filename, String skills) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addUserCVSkills(userid, inStream, size, filename, skills, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean addUserCVSkills(String userid, InputStream inStream, int size, String filename, String skills, Connection conn) throws SQLException {
		try {
			//check if exsits first.  if no, insert, else update
			int cnt = 0;
			String query = "select count(*) as cnt from ets.ets_user_info where user_id='" + userid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
			stmt.close();

			PreparedStatement ps;
			if (cnt > 0) {
				ps = conn.prepareStatement("update ets.ets_user_info set user_cv=?,user_cv_filename=?,user_skills=?,last_timestamp=current timestamp where user_id=?");
				ps.setBinaryStream(1, inStream, size);
				ps.setString(2, filename);
				ps.setString(3, skills);
				ps.setString(4, userid);
			} else {
				ps = conn.prepareStatement("insert into ets.ets_user_info(user_id,user_skills,user_cv,user_cv_filename,last_timestamp) values(?,?,?,?,current timestamp)");
				ps.setString(1, userid);
				ps.setString(2, skills);
				ps.setBinaryStream(3, inStream, size);
				ps.setString(4, filename);
			}

			ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean updateUserCVSkills(String userid, InputStream inStream, int size, String filename, String skills) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateUserCVSkills(userid, inStream, size, filename, skills, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean updateUserCVSkills(String userid, InputStream inStream, int size, String filename, String skills, Connection conn) throws SQLException {
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("update ets.ets_user_info set user_cv=?,user_cv_filename=?,user_skills=?,last_timestamp=current timestamp where user_id=?");
			ps.setBinaryStream(1, inStream, size);
			ps.setString(2, filename);
			ps.setString(3, skills);
			ps.setString(4, userid);

			ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean updateUserSkills(String userid, String skills) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateUserSkills(userid, skills, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static synchronized boolean updateUserSkills(String userid, String skills, Connection conn) throws SQLException {
		try {
			//check if exsits first.  if no, insert, else update
			int cnt = 0;
			String query = "select count(*) as cnt from ets.ets_user_info where user_id='" + userid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
			stmt.close();

			PreparedStatement ps;
			if (cnt > 0) {
				ps = conn.prepareStatement("update ets.ets_user_info set user_skills=?,last_timestamp=current timestamp where user_id=?");
				ps.setString(1, skills);
				ps.setString(2, userid);
			} else {
				ps = conn.prepareStatement("insert into ets.ets_user_info(user_id,user_skills,last_timestamp) values(?,?,current timestamp)");
				ps.setString(1, userid);
				ps.setString(2, skills);
			}

			ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static String getUserSkills(String userid) throws SQLException, Exception {
		Connection connection = null;
		String skills = "";
		try {
			connection = ETSDBUtils.getConnection();
			skills = getUserSkills(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return skills;
		}
	public static String getUserSkills(String userid, Connection conn) throws SQLException {
		try {
			String skills = "";
			PreparedStatement ps;
			ps = conn.prepareStatement("select user_skills from ets.ets_user_info where user_id=?");
			ps.setString(1, userid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				skills = rs.getString("user_skills");
				if (skills == null || skills.length() <= 0) {
					skills = "";
				}
			}

			rs.close();
			ps.close();

			return skills;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	//getCVSizeById(userid);
	public static String[] getCVNameSizeById(String userid) throws SQLException, Exception {
		Connection connection = null;
		String[] s = new String[2];

		try {
			connection = ETSDBUtils.getConnection();
			s = getCVNameSizeById(userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return s;
		}

	public static String[] getCVNameSizeById(String userid, Connection conn) throws SQLException {
		try {
			long lsize = 0;
			String size = "0";
			String filename = "";

			PreparedStatement ps;
			ps = conn.prepareStatement("select user_cv,user_cv_filename from ets.ets_user_info where user_id=?");
			ps.setString(1, userid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Blob cv = rs.getBlob("user_cv");
				if (cv == null)
					size = "0";
				else {
					lsize = cv.length();
					size = String.valueOf(lsize);
				}
				filename = rs.getString("user_cv_filename");
			}

			rs.close();
			ps.close();

			String[] s = new String[] { size, filename };
			return s;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean updateUserMessengerID(String userid, String mID) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateUserMessengerID(userid, mID, connection);
			return success;
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
	}

	public static synchronized boolean updateUserMessengerID(String userid, String mID, Connection conn) throws SQLException {
		try {
			int cnt = 0;
			String query = "select count(*) as cnt from ets.ets_user_info where user_id='" + userid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
			stmt.close();

			PreparedStatement ps;
			if (cnt > 0) {
				ps = conn.prepareStatement("update ets.ets_user_info set MESSENGER_ID=?,last_timestamp=current timestamp where user_id=?");
				ps.setString(1, mID);
				ps.setString(2, userid);
			} else {
				ps = conn.prepareStatement("insert into ets.ets_user_info(user_id,MESSENGER_ID,last_timestamp) values(?,?,current timestamp)");
				ps.setString(1, userid);
				ps.setString(2, mID);
			}

			ps.executeUpdate();
			ps.close();

			return true;

		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static synchronized boolean updateNotesMailID(String userid, String nID) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = updateNotesMailID(userid, nID, connection);
			return success;
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
	}

	public static synchronized boolean updateNotesMailID(String userid, String nID, Connection conn) throws SQLException {
		try {
			int cnt = 0;
			String query = "select count(*) as cnt from ets.ets_user_info where user_id='" + userid + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
			stmt.close();

			PreparedStatement ps;
			if (cnt > 0) {
				ps = conn.prepareStatement("update ets.ets_user_info set NOTES_MAIL=?,last_timestamp=current timestamp where user_id=?");
				ps.setString(1, nID);
				ps.setString(2, userid);
			} else {
				ps = conn.prepareStatement("insert into ets.ets_user_info(user_id,NOTES_MAIL,last_timestamp) values(?,?,current timestamp)");
				ps.setString(1, userid);
				ps.setString(2, nID);
			}

			ps.executeUpdate();
			ps.close();

			return true;

		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}


	public static String getUserMessengerID(String userid) throws SQLException, Exception {
		Connection connection = null;
		String msgrID = "";
		try {

			connection = ETSDBUtils.getConnection();
			msgrID = getUserMessengerID(userid,connection);

			return msgrID;
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}

	}

	public static String getUserMessengerID(String userid,Connection conn) throws SQLException {
		try {
			String msgrID = "";
			PreparedStatement ps;
			ps = conn.prepareStatement("select MESSENGER_ID from ets.ets_user_info where user_id=?");

			ps.setString(1, userid);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				msgrID = rs.getString("MESSENGER_ID");
				if (msgrID == null || msgrID.length() <= 0) {
					msgrID = "";
				}
			}

			rs.close();
			ps.close();

			return msgrID;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	//	--------------------------TEAM------------------------------------------------

	// SET MET
	public static Vector getDocsForSetMet(Connection conn, String sProjectId, String sSetMetID) throws SQLException {

		PreparedStatement getDocsSt =
			conn.prepareStatement(
				"select d.*, f.docfile_name, f.docfile_update_date," + "(select count(m.doc_id) from ets.ets_doc_metrics m where d.doc_id=m.doc_id) as hits " + "from ETS.ETS_DOC d, ETS.ETS_DOCFILE f where d.project_id = ? and d.meeting_id= ? and d.doc_id = f.doc_id " + "and delete_flag!='" + TRUE_FLAG + "' and doc_type=" + Defines.SETMET_PLAN + " order by d.doc_upload_date desc with ur");

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, sSetMetID);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);


		rs.close();
		getDocsSt.close();

		return docs;

	}
	// SET MET

	public static Vector getAllCats(String projectid) throws SQLException, Exception {
		Connection connection = null;
		Vector v = null;

		try {
			connection = ETSDBUtils.getConnection();
			v = getAllCats(projectid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}
	public static Vector getAllCats(String projectid, Connection conn) throws SQLException {
		try {
			Vector v = new Vector();

			PreparedStatement ps;
			ps = conn.prepareStatement("select * from ets.ets_cat where project_id=? and view_type=? order by parent_id");
			ps.setString(1, projectid);
			ps.setInt(2, 9);

			ResultSet rs = ps.executeQuery();
			v = getCats(rs);

			rs.close();
			ps.close();

			return v;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}

	}

	public static boolean updateParentId(char node, int docid, int newParentCat, char ibmonly, String projid, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean b = false;

		try {
			connection = ETSDBUtils.getConnection();
			b = updateParentId(node, docid, newParentCat, userid, ibmonly, projid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return b;

	}

	public static boolean updateParentId(char node, int nodeid, int newParentCat, String userid, char ibmonly, String projid, Connection conn) throws SQLException, Exception {
		boolean b = false;

		try {

			PreparedStatement ps;
			String qry = "";

			if (node == Defines.NODE_CAT) {
				if (ibmonly == 'x') {
					qry = "update ets.ets_cat set parent_id=?,user_id=?,last_timestamp=current timestamp where cat_id=?";
					ps = conn.prepareStatement(qry);
					ps.setInt(1, newParentCat);
					ps.setString(2, userid);
					ps.setInt(3, nodeid);
				} else {
					qry = "update ets.ets_cat set parent_id=?,user_id=?,ibm_only=?,last_timestamp=current timestamp where cat_id=?";
					ps = conn.prepareStatement(qry);
					ps.setInt(1, newParentCat);
					ps.setString(2, userid);
					ps.setString(3, String.valueOf(ibmonly));
					ps.setInt(4, nodeid);

				}
			} else {
				int lowestPossibleID = (nodeid / MAX_DOC_VERSIONS) * MAX_DOC_VERSIONS;
				int highestPossibleID = lowestPossibleID + MAX_DOC_VERSIONS;
				System.out.println("lpid=" + lowestPossibleID);
				System.out.println("hid=" + highestPossibleID);
				if (ibmonly == 'x') {
					qry = "update ets.ets_doc set cat_id=?, updated_by=?,doc_update_date=current timestamp where doc_id>=? and doc_id<?";
					ps = conn.prepareStatement(qry);
					ps.setInt(1, newParentCat);
					ps.setString(2, userid);
					ps.setInt(3, lowestPossibleID);
					ps.setInt(4, highestPossibleID);
				} else {
					qry = "update ets.ets_doc set cat_id=?,ibm_only=?,updated_by=?,doc_update_date=current timestamp where doc_id>=? and doc_id<?";
					ps = conn.prepareStatement(qry);
					ps.setInt(1, newParentCat);
					ps.setString(2, String.valueOf(ibmonly));
					ps.setString(3, userid);
					ps.setInt(4, lowestPossibleID);
					ps.setInt(5, highestPossibleID);
				}
			}

			int res = ps.executeUpdate();

			if (res >= 1) {
				b = true;
			} else {
				b = false;
			}

			ps.close();

			boolean isAdmin = true; //to get all nodes

			if (ibmonly != 'x' && node == Defines.NODE_CAT) {
				String catids = "";
				String docids = "";
				Vector v = new Vector();

				v.addElement(catids);
				v.addElement(docids);
				v = getAllChildrenIds(nodeid, v, isAdmin, userid, conn);
				catids = (String) v.elementAt(0);
				docids = (String) v.elementAt(1);

				System.out.println("c: " + catids);
				System.out.println("d: " + docids);

				if (!catids.equals("")) {
					catids = catids.substring(0, catids.length() - 1);
					updateCatIbmOnly(ibmonly, catids, conn);
				}
				if (!docids.equals("")) {
					docids = docids.substring(0, docids.length() - 1);
					updateDocIbmOnly(ibmonly, docids, conn);
				}

				if (!docids.equals("") && ibmonly != Defines.ETS_PUBLIC) {
					Vector vUsers = getAllDocRestrictedUserIds(docids, projid, conn);
					String userids = "";
					for (int u = 0; u < vUsers.size(); u++) {
						try {
							String irid = (String) vUsers.elementAt(u);
							String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, irid);
							String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
							if (!decaftype.equals("I")) {
								if (!userids.equals(""))
									userids = ",'" + irid + "'";
								else
									userids = "'" + irid + "'";
							}
						} catch (AMTException a) {
							System.out.println("amt exception in getibmmembers err= " + a);
						} catch (SQLException s) {
							System.out.println("sql exception in getibmmembers err= " + s);
						}
					}

					if (!userids.equals("")) {
						updateDocResUsers(docids, projid, userids, conn);
					}
				}
			} else if (ibmonly != 'x' && node == Defines.NODE_DOC) {
				Vector prev = getPreviousVersions(nodeid);
				String docids = "";
				for (int i = 0; i < prev.size(); i++) {
					ETSDoc pd = (ETSDoc) prev.elementAt(i);
					docids = docids + pd.getId() + ",";
				}

				Vector vUsers = getAllDocRestrictedUserIds(docids, projid, conn);
				String userids = "";
				for (int u = 0; u < vUsers.size(); u++) {
					try {
						String irid = (String) vUsers.elementAt(u);
						String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, irid);
						String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
						if (!decaftype.equals("I")) {
							if (!userids.equals(""))
								userids = ",'" + irid + "'";
							else
								userids = "'" + irid + "'";
						}
					} catch (AMTException a) {
						System.out.println("amt exception in getibmmembers err= " + a);
					} catch (SQLException s) {
						System.out.println("sql exception in getibmmembers err= " + s);
					}
				}

				if (!userids.equals("")) {
					updateDocResUsers(docids, projid, userids, conn);
				}
			}

		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		} catch (Exception e) {
			System.out.println("ex error =" + getStackTrace(e));
			//throw e;
		}

		return b;
	}

	public static boolean updateDocStatus(int docid, String projectid, String status, String comm, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;

		try {
			connection = ETSDBUtils.getConnection();
			success = updateDocStatus(docid, projectid, status, comm, userid, connection);

		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static boolean updateDocStatus(int docid, String projectid, String status, String comm, String userid, Connection conn) throws SQLException {
		try {

			boolean success = false;

			PreparedStatement ps;
			String stmt = "";
			if (status.equals(String.valueOf(Defines.DOC_APPROVED))) {
				//stmt = "update ets.ets_doc set document_status=?,approver_id=?,approval_comments=?,approve_date=current timestamp " +
				//	"where project_id=? and doc_id=?";
				stmt = "update ets.ets_doc set document_status=?,approval_comments=?,approve_date=current timestamp " + "where project_id=? and doc_id=?";

			} else if (status.equals(String.valueOf(Defines.DOC_SUB_APP))) {
				//stmt = "update ets.ets_doc set document_status=?, approver_id=? where project_id=? and doc_id=?";
				stmt = "update ets.ets_doc set document_status=?,approval_comments=? where project_id=? and doc_id=?";
			}

			ps = conn.prepareStatement(stmt);

			ps.setString(1, status);
			//ps.setString(2,userid);
			ps.setString(2, comm); //should be "" for subapp
			ps.setString(3, projectid);
			ps.setInt(4, docid);

			int cnt = ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}

	}

	public static boolean updateDocPubStatus(int docid, String projectid, String userid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;

		try {
			connection = ETSDBUtils.getConnection();
			success = updateDocPubStatus(docid, projectid, userid, connection);

		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}
	public static boolean updateDocPubStatus(int docid, String projectid, String userid, Connection conn) throws SQLException {
		try {

			boolean success = false;

			PreparedStatement ps;
			//ps = conn.prepareStatement("update ets.ets_doc set document_status=?,approver_id=?,approval_comments=?,approve_date=current timestamp " +
			//	"where project_id=? and doc_id=?");

			ps = conn.prepareStatement("update ets.ets_doc set document_status=?,updated_by=?,doc_publish_date=current timestamp " + "where project_id=? and doc_id=?");

			ps.setString(1, String.valueOf(Defines.DOC_PUBLISH));
			ps.setString(2, userid);
			ps.setString(3, projectid);
			ps.setInt(4, docid);

			int cnt = ps.executeUpdate();
			ps.close();

			return true;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}

	}

	public static Vector getAllDocMetrics(String projectid, String sortby, String ad) throws SQLException, Exception {
		Connection connection = null;
		Vector v = new Vector();

		try {
			connection = ETSDBUtils.getConnection();
			v = getAllDocMetrics(projectid, sortby, ad, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}
	public static Vector getAllDocMetrics(String projectid, String sortby, String a_d, Connection conn) throws SQLException, Exception {
		Vector v = new Vector();

		String sb = "d.doc_name";

		if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
			sb = "d.doc_upload_date " + a_d + ",d.doc_name";
		} else if (sortby.equals(Defines.SORT_BY_SIZE_STR)) {
			sb = "d.doc_size";
		} else if (sortby.equals(Defines.SORT_BY_HITS_STR)) {
			sb = "hits " + a_d + ",d.doc_name";
		}

		try {
			PreparedStatement ps;
			ps =
				conn.prepareStatement(
					"select d.*,f.docfile_name, f.docfile_update_date,(select count(m.doc_id) from ets.ets_doc_metrics m " + "where m.doc_id=d.doc_id) as hits from ets.ets_doc d,ets.ets_docfile f where d.project_id=? and d.latest_version='1' " + "and d.delete_flag !='" + TRUE_FLAG + "' and d.doc_id=f.doc_id and d.doc_type=" + Defines.DOC + " order by " + sb + " " + a_d + " with ur");

			ps.setString(1, projectid);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ETSDoc doc = getDoc(rs);
				//doc.setDocHits(rs.getInt("hits"));
				v.addElement(doc);
			}

			rs.close();
			ps.close();

			return v;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	public static Vector getDocMetrics(int docid, String projectid, String sortby, String ad) throws SQLException, Exception {
		Connection connection = null;
		Vector v = new Vector();

		try {
			connection = ETSDBUtils.getConnection();
			v = getDocMetrics(docid, projectid, ad, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}
	public static Vector getDocMetrics(int docid, String projectid, String ad, Connection conn) throws SQLException, Exception {
		Vector v = new Vector();

		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("select m.* from ets.ets_doc_metrics m where " + "m.project_id=? and m.doc_id=? order by m.timestamp " + ad + " with ur");

			ps.setString(1, projectid);
			ps.setInt(2, docid);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ETSUser u = new ETSUser();
				u.setUserId(rs.getString("user_id"));
				u.setLastTimestamp(rs.getTimestamp("timestamp"));

				v.addElement(u);
			}

			rs.close();
			ps.close();

			return v;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	//FOR BLADE CENTER
	public static Vector getUserPrivs(String projectid, String userid) throws SQLException, Exception {
		Connection connection = null;
		Vector v = new Vector();

		try {
			connection = ETSDBUtils.getConnection();
			v = getUserPrivs(projectid, userid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return v;
		}
	public static Vector getUserPrivs(String projectid, String userid, Connection conn) throws Exception {
		Vector v = new Vector();
		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("select r.priv_id from ets.ets_roles r, ets.ets_users u " + "where u.user_role_id=r.role_id and r.priv_value=1 and u.user_project_id=? " + "and u.user_id=? with ur");

			ps.setString(1, projectid);
			ps.setString(2, userid);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				v.addElement(new Integer(rs.getInt("priv_id")));
			}

			rs.close();
			ps.close();

			return v;
		} catch (SQLException e) {
			System.out.println("sql error =" + getStackTrace(e));
			throw e;
		}

	}

	//restricted documents

 static public boolean addDocResUsers(Vector users, String docid, String projid) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;
		try {
			connection = ETSDBUtils.getConnection();
			success = addDocResUsers(users, docid, projid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;
		}

	static public boolean addDocResUsers(Vector users, String docid, String projid, Connection conn) throws SQLException {
		boolean s = true;

		try {
			int c = new Integer(docid).intValue();
			for (int i = 0; i < users.size(); i++) {
				s = addDocResUsers((String) users.elementAt(i), c, projid, conn);
			}
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
		return s;
	}

	static public boolean addDocResUsers(String userid, int docid, String projid, Connection conn) throws SQLException {
		try {
			String qry = "insert into ETS.ETS_PRIVATE_DOC(DOC_ID, USER_ID, PROJECT_ID, ACCESS_TYPE) " + "values (" + docid + ",'" + userid + "','" + projid + "', '" + Defines.DOC_READ_ACCESS + "')";

			Statement statement = conn.createStatement();
			int rowCount = statement.executeUpdate(qry);
			statement.close();

			return true;
		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	static public Vector getDocComments(int docid, String projid) throws SQLException, Exception {
		Connection connection = null;

		Vector comms = new Vector();
		try {
			connection = ETSDBUtils.getConnection();
			comms = getDocComments(docid, projid, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return comms;

	}

	static public Vector getDocComments(int docid, String projid, Connection conn) throws SQLException {
		Vector v = new Vector();
		try {
			String qry = "select * from ets.ets_doc_comments where doc_id=" + docid + " and project_id='" + projid + "' order by last_timestamp desc";

			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(qry);

			while (rs.next()) {
				ETSDocComment dc = new ETSDocComment();
				dc.setId(docid);
				dc.setProjectId(projid);
				dc.setUserId(rs.getString("user_id"));
				dc.setComment(rs.getString("comment"));
				dc.setCommentDate(rs.getTimestamp("last_timestamp"));
				v.addElement(dc);
			}

			rs.close();
			statement.close();

		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}

		return v;

	}

	static public boolean addDocComment(ETSDocComment c) throws SQLException, Exception {
		Connection connection = null;
		boolean success = false;

		try {
			connection = ETSDBUtils.getConnection();
			success = addDocComment(c, connection);
		} catch (SQLException e) {
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
		}
			return success;

	}

	static public boolean addDocComment(ETSDocComment c, Connection conn) throws SQLException {
		boolean success = false;

		try {
			PreparedStatement ps;
			ps = conn.prepareStatement("insert into ets.ets_doc_comments(doc_id,project_id,comment,user_id,last_timestamp)" + "values (?,?,?,?,current timestamp)");

			ps.setInt(1, c.getId());
			ps.setString(2, c.getProjectId());
			ps.setString(3, c.getComment());
			ps.setString(4, c.getUserId());

			int rowCnt = ps.executeUpdate();
			ps.close();

			if (rowCnt == 1) {
				success = true;
			}

		} catch (SQLException e) {
			printErr("sql error =" + getStackTrace(e));
			throw e;
		}

		return success;

	}

	public static ETSProj getProjectDetsFromProjName(String projectName) throws SQLException, Exception {
			Connection connection = null;
			ETSProj etsProj = new ETSProj();
			try {
				//Raja comment 6249
				//connection = WrkSpcTeamUtils.getConnection();
				connection = ETSDBUtils.getConnection();

				etsProj = getProjectDetsFromProjName(projectName, connection);
			} catch (SQLException e) {
				throw e;
			} catch (Exception ex) {
				throw ex;
			} finally {
				//dbConnect.closeConn();
				ETSDBUtils.close(connection);

			}

		return etsProj;
		}
		//spn 0312 projid
		public static ETSProj getProjectDetsFromProjName(String projectName,Connection conn) throws SQLException,Exception {

			return getProjectDetails(conn,AmtCommonUtils.getValue(conn,"select project_id from ets.ets_projects where project_name='"+AmtCommonUtils.getTrimStr(projectName)+"' with ur"));

		}


//		start 4.5.1
		  public static Vector getSelectedProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename,String selectdUsersStr) throws SQLException, Exception {
			  Connection connection = null;
			  Vector members = null;
			  try {
				  connection = ETSDBUtils.getConnection();
				  members = getSelectedProjMembers(projectid, sortby, ad, checkAmtTables, getrolename, connection,selectdUsersStr);
			  } catch (SQLException e) {
				  printErr("error=" + e);
				  throw e;
			  } catch (Exception ex) {
				  throw ex;
			  } finally {
				  ETSDBUtils.close(connection);
			  }
				  return members;
		  }
		  public static Vector getSelectedProjMembers(String projectid, String sortby, String ad, boolean checkAmtTables, boolean getrolename, Connection conn,String selectdUsersStr) throws SQLException {
			  String sb = "a.user_fname " + ad + ", a.user_lname";

			  if (sortby.equals(Defines.SORT_BY_USERID_STR)) {
				  sb = "u.user_id";
			  } else if (sortby.equals(Defines.SORT_BY_PROJROLE_STR)) {
				  sb = "u.user_job";
			  } else if (sortby.equals(Defines.SORT_BY_COMP_STR)) {
				  sb = "a.user_fname " + ad + ",a.user_lname";
			  } else if (sortby.equals(Defines.SORT_BY_ACCLEV_STR)) {
				  sb = "role_name " + ad + ",a.user_fname " + ad + ",a.user_lname";
			  }

			  PreparedStatement getProjMemSt;
			  System.out.println("projectid str==="+projectid);

			  if (getrolename) {
				  getProjMemSt = conn.prepareStatement("select u.*,(select distinct r.role_name from ets.ets_roles r where r.role_id=u.user_role_id) as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid  and u.user_id IN ( "+selectdUsersStr+" ) order by " + sb + " " + ad + " with ur");
			  } else {
				  getProjMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company " + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?" + " and u.user_id = a.ir_userid and a.edge_userid = d.userid and u.user_id IN ( "+selectdUsersStr+" ) order by " + sb + " " + ad + " with ur");
			  }

			  getProjMemSt.setString(1, projectid);
			  ResultSet rs = getProjMemSt.executeQuery();

			  Vector mems = null; //new Vector();
			  mems = getAllUsersData(rs);

			  rs.close();
			  getProjMemSt.close();
			  return mems;
		  }
		  //end 4.5.1


}//end of class
