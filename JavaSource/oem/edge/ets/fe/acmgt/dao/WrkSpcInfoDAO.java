/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcInfoDAO {

	private static Log logger = EtsLogger.getLogger(WrkSpcInfoDAO.class);

	public static final String VERSION = "1.16";

	/**
	 * 
	 */
	public WrkSpcInfoDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static ETSProj getProjectDetails(String projectidStr) throws SQLException, Exception {

		Connection connection = null;
		ETSProj etsProj = null;

		try {

			connection = WrkSpcTeamUtils.getConnection();
			etsProj = ETSDatabaseManager.getProjectDetails(connection, projectidStr);

		} catch (SQLException e) {

			throw e;

		} catch (Exception ex) {

			throw ex;

		} finally {

			ETSDBUtils.close(connection);

		}

		return etsProj;
	}

	/**
	 * 
	 * @param projectid
	 * @param priv
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static Vector getUsersByProjectPriv(String projectid, int priv) throws SQLException, Exception {

		Vector v = new Vector();
		Connection connection = null;

		try {

			connection = WrkSpcTeamUtils.getConnection();

			v = ETSDatabaseManager.getUsersByProjectPriv(projectid, priv, connection);

		} catch (SQLException e) {

			throw e;

		} catch (Exception ex) {

			throw ex;

		} finally {

			ETSDBUtils.close(connection);

		}

		return v;
	}
	/**
	 * 
	 * @param user
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	//	spn 0312 projid
	public static synchronized String[] addProjectMemberWithStatus(ETSUser user) throws SQLException, Exception {

		Connection conn = null;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			return ETSDatabaseManager.addProjectMemberWithStatus(user, conn);

		} catch (SQLException e) {

			throw e;

		} catch (Exception ex) {

			throw ex;

		} finally {

			ETSDBUtils.close(conn);
		}

	}

	public static String getUserRole(String userId, String projectId) throws SQLException, Exception {

		Connection conn = null;
		String userRole = Defines.INVALID_USER;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			userRole = ETSUtils.getUserRole(userId, projectId, conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return userRole;

	}

	public boolean updateWrkSpcUsers() throws SQLException, AMTException, Exception {

		Connection conn = null;
		boolean flag = false;
		try {

			conn = WrkSpcTeamUtils.getConnection();

			// REJECT

			logger.debug("REJECT UPDATE FOR ETS PROJ");
			// users without entitlement for ETS PROJ
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='R' where a.user_id not in (select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" + Defines.ETS_ENTITLEMENT + "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" + Defines.ETS_WORKSPACE_TYPE + "' and IS_ITAR != 'Y' OR IS_ITAR IS NULL) ");

			logger.debug("REJECT UPDATE FOR ETS ITAR PROJ");

			//users without entitlement for ETS ITAR
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='R' where a.user_id not in (select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" + Defines.ITAR_ENTITLEMENT + "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" + Defines.ETS_WORKSPACE_TYPE + "' and IS_ITAR = 'Y' ) ");

			logger.debug("REJECT UPDATE FOR AIC PROJ");

			//users without entitlement for AIC
			// changes for 6.1 , no need to check for internal users, check for externals only
			EntitledStatic.fireUpdate(
				conn, 
				"update ets.ets_users a set a.active_flag='R' where a.user_id not in (select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" 
					+ Defines.AIC_ENTITLEMENT 
					+ "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" 
					+ Defines.AIC_WORKSPACE_TYPE 
					+ "' and is_private not in ('"+Defines.AIC_IS_PRIVATE_TEAMROOM + "','"+ Defines.AIC_IS_RESTRICTED_TEAMROOM + "'))"
					// new condition added for excluding internal users
					+ " and a.user_id not in (select e.ir_userid from amt.users e, decaf.users f, ets.ets_users g "
					+ " where g.user_id = e.ir_userid and e.edge_userid = f.userid and f.user_type ='I')" );

			logger.debug("PENDING UPDATE FOR ETS PROJ");

			//PENDING

			//users with pending entitlement for ETS PROJ
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='P' where a.user_id in (select b.ir_userid from amt.users b, decaf.users c, decaf.req_approval_details e , decaf.req_approval_tracking z where e.req_serial_id = z.req_serial_id and z.appr_result in ('P','O') and b.edge_userid = c.userid and e.appr_result in ('P','A','I') and c.decaf_id=e.decaf_id and e.access_id = (select project_id from decaf.project where project_name = '"
					+ Defines.REQUEST_PROJECT
					+ "')) and a.user_project_id in ( select project_id from ets.ets_projects where project_type='"
					+ Defines.ETS_WORKSPACE_TYPE
					+ "' and IS_ITAR != 'Y' OR IS_ITAR IS NULL)");

			logger.debug("PENDING UPDATE FOR ETS ITAR");

			//users with pending entitlement for ETS ITAR
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='P' where a.user_id in (select b.ir_userid from amt.users b, decaf.users c, decaf.req_approval_details e , decaf.req_approval_tracking z where e.req_serial_id = z.req_serial_id and z.appr_result in ('P','O') and b.edge_userid = c.userid and e.appr_result in ('P','A','I') and c.decaf_id=e.decaf_id and e.access_id = (select project_id from decaf.project where project_name = '"
					+ Defines.ITAR_PROJECT
					+ "')) and a.user_project_id in ( select project_id from ets.ets_projects where project_type='"
					+ Defines.ETS_WORKSPACE_TYPE
					+ "' and IS_ITAR = 'Y')");

			logger.debug("PENDING UPDATE FOR AIC PROJ");

			//users with pending entitlement for AIC PROJ
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='P' where a.user_id in (select b.ir_userid from amt.users b, decaf.users c, decaf.req_approval_details e , decaf.req_approval_tracking z where e.req_serial_id = z.req_serial_id and z.appr_result in ('P','O') and b.edge_userid = c.userid and e.appr_result in ('P','A','I') and c.decaf_id=e.decaf_id and e.access_id = (select roles_id from decaf.roles where roles_name = '"
					+ Defines.AIC_ENTITLEMENT
					+ "')) and a.user_project_id in ( select project_id from ets.ets_projects where project_type='"
					+ Defines.AIC_WORKSPACE_TYPE
					+ "' and is_private not in ('"+Defines.AIC_IS_PRIVATE_TEAMROOM + "','"+ Defines.AIC_IS_RESTRICTED_TEAMROOM + "'))");

			//APPROVE 
			logger.debug("APPR  UPDATE FOR ETS PROJ");
			//users APPR with entitlement for ETS PROJ
			EntitledStatic.fireUpdate(
				conn,
				"update ets.ets_users a set a.active_flag='A' where a.user_id in ( select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" + Defines.ETS_ENTITLEMENT + "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" + Defines.ETS_WORKSPACE_TYPE + "' and IS_ITAR != 'Y' OR IS_ITAR IS NULL)");

			logger.debug("APPR  UPDATE FOR ETS ITAR");
			//users APPR with entitlement for ETS ITAR
			EntitledStatic.fireUpdate(conn, "update ets.ets_users a set a.active_flag='A' where a.user_id in ( select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" + Defines.ITAR_ENTITLEMENT + "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" + Defines.ETS_WORKSPACE_TYPE + "' and IS_ITAR = 'Y')");

			logger.debug("APPR  UPDATE FOR AIC PROJ");
			//users APPR with entitlement for AIC PROJ
			EntitledStatic.fireUpdate(conn, "update ets.ets_users a set a.active_flag='A' where a.user_id in ( select b.ir_userid from amt.users b, amt.s_user_access_view c where b.edge_userid = c.userid and entitlement='" + Defines.AIC_ENTITLEMENT + "') and a.user_project_id in ( select project_id from ets.ets_projects where project_type='" + Defines.AIC_WORKSPACE_TYPE + "' and is_private not in ('"+Defines.AIC_IS_PRIVATE_TEAMROOM + "','"+ Defines.AIC_IS_RESTRICTED_TEAMROOM + "'))");

		} finally {

			ETSDBUtils.close(conn);

		}

		flag = true;
		return flag;

	}

	public static boolean isWrkSpcMgrDefnd(Connection conn, String projectId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("select count(USER_ID) from ets.ets_users");
		sb.append("  where user_project_id='" + projectId + "' ");
		sb.append("  and user_role_id = ");
		sb.append("   (select role_id from ets.ets_roles");
		sb.append("                  where project_id='" + projectId + "' and role_name='Workspace Manager' fetch first 1 row only) ");

		int count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;
		}

		return false;
	}

	public static boolean isWrkSpcMgrDefnd(String projectId) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			flag = isWrkSpcMgrDefnd(conn, projectId);

		} catch (SQLException e) {

			throw e;

		} catch (Exception ex) {

			throw ex;

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
	 * 
	 * @param conn
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static String getMgrRolesId(Connection conn, String projectId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("select role_id from ets.ets_roles");
		sb.append("  where project_id='" + projectId + "' and role_name='Workspace Manager' fetch first 1 row only ");

		String roleId = AmtCommonUtils.getValue(conn, sb.toString());

		return roleId;
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public static String getMgrRolesId(String projectId) throws SQLException, Exception {

		Connection conn = null;
		String roleId = "";

		try {

			conn = WrkSpcTeamUtils.getConnection();

			roleId = getMgrRolesId(conn, projectId);

		} catch (SQLException e) {

			throw e;

		} catch (Exception ex) {

			throw ex;

		} finally {

			ETSDBUtils.close(conn);
		}

		return roleId;

	}

	/**
	 * 
	 * @param conn
	 * @param userid
	 * @param Project
	 * @param lastIrUserId
	 * @return
	 */

	public boolean deleteMember(Connection conn, String userid, ETSProj Project, String lastIrUserId) throws SQLException, Exception {

		boolean flag = false;

		String[] res = ETSDatabaseManager.delProjectMember(userid, Project.getProjectId(), conn);
		String success = res[0];

		if (success.equals("0")) {

			flag = true;
			Metrics.appLog(conn, lastIrUserId, WrkSpcTeamUtils.getMetricsLogMsg(Project, "Team_Delete"));

		}

		return flag;

	}

	/**
	 * 
	 * @param userid
	 * @param Project
	 * @param lastIrUserId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean deleteMember(String userid, ETSProj Project, String lastIrUserId) {

		boolean flag = false;

		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();

			flag = deleteMember(conn, userid, Project, lastIrUserId);
			
			//delete from groups alsoe
			if(flag) {
				
				deleteFromGroups(conn,userid,Project.getProjectId());
			}

		} catch (SQLException sqlEx) {

			if (conn != null) {

				try {

					conn.rollback();

				} catch (SQLException ex) {

					ex.printStackTrace();
				}
			}

			logger.error("SQL Exception in  remove member", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in  remove member", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;
	}

	/**
	 * 
	 * @author V2PHANI
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

	public boolean deleteFromGroups(Connection conn, String userId, String projectId) throws SQLException {

		boolean flag = false;
		StringBuffer sb = new StringBuffer();

		sb.append("delete from ets.user_groups ");
		sb.append(" where ");
		sb.append(" user_id=? ");
		sb.append(" and group_id in ");
		sb.append(" (select group_id from ets.groups where project_id = ? ) ");

		PreparedStatement pstmt = null;
		int count = 0;

		try {

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, userId);
			pstmt.setString(2, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		flag = true;

		return flag;
	}


	public Vector getSubWrkSpcsForUser(String projId,String userId){
		Connection conn = null;
		String pstmtQuery = "";
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Vector subWSforUsrVect = new Vector();
				
		try{
			conn = ETSDBUtils.getConnection();
			pstmtQuery = "SELECT PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? for READ ONLY";
			pstmt = conn.prepareStatement(pstmtQuery);
			pstmt.setString(1, projId);
			pstmt.setString(2, Defines.WORKSPACE_DELETE);
			rset = pstmt.executeQuery();
			
			while (rset.next()) {
				String sSubWorkspaceProjectID = rset.getString(1);
				if(ETSDatabaseManager.isUserInProject(userId,sSubWorkspaceProjectID,conn)){
					subWSforUsrVect.addElement(sSubWorkspaceProjectID);
				}	
			}
		}catch (SQLException sqlEx) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
   			 }
			logger.error("SQL Exception in  getSubWrkSpcsForUser", sqlEx);
			sqlEx.printStackTrace();
		} catch (Exception ex) {
			logger.error("Exception in  getSubWrkSpcsForUser", ex);
			ex.printStackTrace();	
		} finally {
			
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(rset);
			ETSDBUtils.close(conn);
		}
		
		return subWSforUsrVect;
	}

	public String getSubWsName(String projId) throws SQLException, Exception {

			Connection conn = null;

			try {

				conn = WrkSpcTeamUtils.getConnection();

				ETSProj etsProj = ETSDatabaseManager.getProjectDetails(conn,projId);
				String projName = etsProj.getName();
				return projName;

			} catch (SQLException e) {

				throw e;

			} catch (Exception ex) {

				throw ex;

			} finally {

				ETSDBUtils.close(conn);
			}

		}

	public String getUserWrkspcStatus(String projId, String userId) throws SQLException {
		Connection conn = null;
		String pstmtQuery = "";
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String userStatus = "";
		
		try {
			conn = WrkSpcTeamUtils.getConnection();
			pstmtQuery = "SELECT ACTIVE_FLAG FROM ETS.ETS_USERS WHERE USER_PROJECT_ID = ? AND USER_ID = ? for READ ONLY";
			pstmt = conn.prepareStatement(pstmtQuery);
			pstmt.setString(1, projId);
			pstmt.setString(2, userId);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				userStatus = rset.getString(1);
			}
		} catch(SQLException e) {
			logger.error("SQL Exception in  getUserWrkspcStatus", e);
			e.printStackTrace();
		} catch(Exception e){
			logger.error("Exception in  getUserWrkspcStatus", e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(rset);
			ETSDBUtils.close(conn);
		}
		
		return userStatus;
	}
	/*
	 * Returns user list for download Members
	 */
	
	public ArrayList getDownLoadAllMembersList(String projId) {
		ArrayList downLoadList = new ArrayList();
		String sQuery = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		ArrayList headerList = new ArrayList();
		headerList.add("Name");
		headerList.add("User Id");
		headerList.add("User Email");
		headerList.add("Access Level");
		headerList.add("Job Responsibility");
		headerList.add("Messenger Id");
		//headerList.add("Timezone");
		
		// Add Header to the download List
		downLoadList.add(headerList);

		//Query for getting Proj Members
		sQuery  = " select u.*,(select distinct r.role_name " + 
				  " from ets.ets_roles r where r.role_id=u.user_role_id) as role_name, " +
				  " a.user_fname, a.user_lname, a.user_email, d.user_type, " +
				  " d.assoc_company, d.user_type , " +
				  " i.notes_mail, i.messenger_id " +
				  " from ETS.ETS_USERS u left outer join ETS.ETS_USER_INFO i " + 
				  "	on u.user_id = i.user_id, " +
				  " AMT.USERS a, " +
				  " decaf.users d " +
				  " where u.user_project_id = ? " +
				  " and u.user_id = a.ir_userid " +
				  " and a.edge_userid = d.userid " +
				  " and u.active_flag='A' " +
				  " order by u.user_id with ur " ; 
		logger.debug("Query for Get All Members List for Download::" + sQuery);
		try {
			conn = ETSDBUtils.getConnection();
			pstmt = conn.prepareStatement(sQuery);
			pstmt.setString(1, projId);
			rset = pstmt.executeQuery();
			while (rset.next()) {
				String sUserName = ETSUtils.checkNull(rset.getString("user_fname")) + ETSUtils.checkNull(rset.getString("user_lname"));
				String sUserId = ETSUtils.checkNull(rset.getString("user_id"));
				String sUserEmail = ETSUtils.checkNull(rset.getString("user_email"));
				String sAccessLevel = ETSUtils.checkNull(rset.getString("role_name"));
				String sJobResponsibility = ETSUtils.checkNull(rset.getString("user_job"));
				String sMessengerId = ETSUtils.checkNull(rset.getString("messenger_id"));
				//String sTimeZone = ETSUtils.checkNull(rset.getString("user_timezone"));
				
				ArrayList templist = new ArrayList();
				templist.add(sUserName);
				templist.add(sUserId);
				templist.add(sUserEmail);
				templist.add(sAccessLevel);
				templist.add(sJobResponsibility);
				templist.add(sMessengerId);
			//	templist.add(sTimeZone);
				
				// add user(tempList) to userList
				downLoadList.add(templist);
			}
		} catch(SQLException e) {
			logger.error("SQL Exception in  getAllProjMemberDetails", e);
			e.printStackTrace();
		} catch(Exception e){
			logger.error("Exception in  getAllProjMemberDetails", e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(rset);
			ETSDBUtils.close(conn);
		}

		return downLoadList;
	}
} //end of class
