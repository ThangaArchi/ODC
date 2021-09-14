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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.AICUserDecafRole;
import oem.edge.ets.fe.documents.common.StringUtil;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TeamroomDAO {

	private static Log logger = LogFactory.getLog(TeamroomDAO.class);
	
	public Vector getAllTeamRoomProjects(Connection conn)
	throws SQLException, Exception {
		Vector projList = new Vector();
		
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			sQuery.append("SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START," + "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID," + "PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,IS_ITAR,PROJECT_TYPE,IS_PRIVATE " + "FROM ETS.ETS_PROJECTS " 
					+ "WHERE is_private in ('" + Defines.AIC_IS_PRIVATE_TEAMROOM + "','" + Defines.AIC_IS_RESTRICTED_TEAMROOM + "') "
					+ " and project_type = 'AIC' and project_status!='" + Defines.WORKSPACE_DELETE + "' with ur");

			SysLog.log(SysLog.DEBUG, "TeamroomDAO::getAllTeamRoomProjects", "QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				ETSProj projDetails = new ETSProj();
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
				String wrkspcPrivateType=ETSUtils.checkNull(rs.getString("IS_PRIVATE"));

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
				projDetails.setProjBladeType(false);
				projDetails.setDeliveryTeam(sDelivery);
				projDetails.setGeography(sGeo);
				projDetails.setIndustry(sIndustry);
				projDetails.setIsPrivate(wrkspcPrivateType);

				if (sIsITAR.equalsIgnoreCase("Y")) {
					projDetails.setITAR(true);
				} else {
					projDetails.setITAR(false);
				}
				
				projDetails.setProjectType(projectType);
				
				projList.add(projDetails);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		return projList;
	}
	
	public Vector getALLActiveUsersForWrkspc(Connection conn, String projectId)
	throws SQLException, Exception {
		Vector userList = new Vector();
		//get All the active , non active users from ETS.ETS_users table
		userList = ETSDatabaseManager.getAllProjMembers(projectId,"","asc",false,false,conn);
		
		return userList;
		
	}
	
	public Vector getAllUsersWithTeamroomEntData(Connection conn, String projId) {
		Vector vtActiveOwnerEntitledUsers = null;
		Vector vtActiveAuthorEntitledUsers = null;
		Vector vtActiveReaderEntitledUsers = null;
		Vector vtAllUsers = new Vector();
		
		try {
		
			vtActiveOwnerEntitledUsers = getUsersWithTeamroomEntData(conn,projId,Defines.BPSOWNER_ENT);
			vtActiveAuthorEntitledUsers = getUsersWithTeamroomEntData(conn,projId,Defines.BPSAUTHOR_ENT);
			vtActiveReaderEntitledUsers = getUsersWithTeamroomEntData(conn,projId,Defines.BPSREADER_ENT);
			
			//add distinct owners to list
			vtAllUsers.addAll(vtActiveOwnerEntitledUsers); 
			// Add distinct authors to list
			vtAllUsers = getDistinctActiveUserList(vtAllUsers,vtActiveAuthorEntitledUsers);
			// Add distinct readers to list
			vtAllUsers = getDistinctActiveUserList(vtAllUsers,vtActiveReaderEntitledUsers);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return vtAllUsers;
		
	}
	public Vector getUsersWithTeamroomEntData(Connection conn, String projId, String strEntitlement)
	throws SQLException, Exception {
		Vector userList = new Vector();
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		


		sQuery.append("Select distinct map.project_id,map.entitlement_name,map.datatype_name, map.role_id,"
						+ " map.profile_name ,map.profile_id, users.ir_userid"
						+ " from ets.ws_decaf_mapping map , amt.user_project_view proj, amt.users users"
						+ " where project_id='" + projId + "' "
						+ " and proj.datatype1='BPSTeams'"
						+ " and map.entitlement_name = proj.entitlement"
						+ " and map.datatype_name = proj.datatype_val1" 
						+ " and proj.userid = users.edge_userid"
						+ " and map.entitlement_name = '" + strEntitlement + "' "
						+ " and map.role_id not in (select role_id from ets.ets_roles where project_id='"+ projId+"' and role_name = 'Workspace Owner' )"
						
						+ " Union "
				
						+ " Select distinct map.project_id,map.entitlement_name,map.datatype_name, map.role_id,"
						+ " map.profile_name ,map.profile_id, users.ir_userid"
						+ " from ets.ws_decaf_mapping map , amt.usr_ent_detail_view ent, amt.users users"
						+ " where project_id='" + projId + "' "
						+ " and ent.datatype1='BPSTeams'"
						+ " and map.entitlement_name = ent.entitlement"
						+ " and map.datatype_name = ent.datatype_val1" 
						+ " and ent.userid = users.edge_userid"
						+ " and map.entitlement_name = '" + strEntitlement + "' "
						+ " and map.role_id not in (select role_id from ets.ets_roles where project_id='"+ projId+"' and role_name = 'Workspace Owner' )"
						+ " order by ir_userid with ur ");

		SysLog.log(SysLog.DEBUG, "TeamroomDAO::getUsersWithTeamroomEntData", "QUERY : " + sQuery.toString());

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				AICUserDecafRole userDets = new AICUserDecafRole();
				
				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sDatatypeName = ETSUtils.checkNull(rs.getString("DATATYPE_NAME"));
				String sEntitlementName = ETSUtils.checkNull(rs.getString("ENTITLEMENT_NAME"));
				int iRoleId = rs.getInt("ROLE_ID");
				String sProfileName = ETSUtils.checkNull(rs.getString("PROFILE_NAME"));
				String sProfileId = ETSUtils.checkNull(rs.getString("PROFILE_ID"));
				String sUserId = ETSUtils.checkNull(rs.getString("IR_USERID"));

				userDets.setProjectId(sProjId);
				userDets.setDatatypeName(sDatatypeName);
				userDets.setEntitlementName(sEntitlementName);
				userDets.setRoleId(iRoleId);
				userDets.setDecafProfileName(sProfileName);
				userDets.setDecafProfileId(sProfileId);
				userDets.setUserId(sUserId);
			
				userList.add(userDets);
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return userList;
	}
	
	/**
	 * Add new users to the ETS.ETS_USERS table
	 * @param conn
	 * @param strAddUsers
	 * @return
	 * @throws SQLException
	 */
	public boolean addUsersToWrkspc(Connection conn, String strAddUsers)
	throws SQLException {

		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtAddWrkspcUsers = conn.createStatement();
		
		strQuery = "insert into ets.ets_users (user_id,user_role_id,user_project_id,"
			+ "user_job,primary_contact,ibm_only,last_userid,last_timestamp,active_flag)"
			+ " values "
			+ strAddUsers ;
		
		if (logger.isDebugEnabled()) {
			logger.debug("Insert query for ETS.ETS_USERS::" + strQuery);
		}
		
		stmtAddWrkspcUsers.executeUpdate(strQuery);
		stmtAddWrkspcUsers.close();
		return true;
	}

	/**
	 * Delete the users who have lost the entitlement for the teamroom
	 * @param conn
	 * @param strProjId
	 * @param strRemoveUsers
	 * @return
	 * @throws SQLException
	 */
	public boolean deleteUsersFrmWrkspc(Connection conn, String strProjId, String strRemoveUsers)
	throws SQLException {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtDelWrkspcUsers = conn.createStatement();
		
		strQuery = " delete from ets.ets_users where user_project_id='"+ strProjId 
				+ "' and user_id in (" + strRemoveUsers
				+ ") and user_role_id not in ( select role_id from ets.ets_roles " 
				+ " where project_id = '" + strProjId + "' and role_name = 'Workspace Owner')";

		
		if (logger.isDebugEnabled()) {
			logger.debug("delete users query for ETS.ETS_USERS::" + strQuery);
		}
		
		stmtDelWrkspcUsers.executeUpdate(strQuery);
		stmtDelWrkspcUsers.close();
		return true;
	}
	
	public boolean updateUserRolesInWrkspc(Connection conn, String strProjId, String strUserId, int intRoleId) 
	throws SQLException {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdWrkspcUsers = conn.createStatement();
		
		strQuery = " update ets.ets_users set user_role_id = " + intRoleId
					+ " , active_flag='A' "
					+ " where user_project_id='"+ strProjId 
					+ "' and user_id ='" + strUserId 
					+ "' and user_role_id not in ( select role_id from ets.ets_roles " 
					+ " where project_id = '" + strProjId + "' and role_name = 'Workspace Owner')";

		
		if (logger.isDebugEnabled()) {
			logger.debug("update user query for ETS.ETS_USERS::" + strQuery);
		}
		
		stmtUpdWrkspcUsers.executeUpdate(strQuery);
		stmtUpdWrkspcUsers.close();
		return true;
	}
	
	/**
	 * Add the elements from New list if they are not present in Old List
	 * @param vtOldSyncList
	 * @param vtNewList
	 * @return
	 */
	public Vector getDistinctActiveUserList(Vector vtOldSyncList,Vector vtNewList) {
		
		Vector vtUserList = new Vector();
		
		//get the list of distinct Users
		for (int i=0; i<vtOldSyncList.size(); i++){
			AICUserDecafRole userDets = (AICUserDecafRole) vtOldSyncList.elementAt(i);
			vtUserList.add(userDets.getUserId());
		}
		
		for (int i=0; i<vtNewList.size(); i++){
			AICUserDecafRole newUserDets = (AICUserDecafRole) vtNewList.elementAt(i);
			if (!vtUserList.contains(newUserDets.getUserId())) {
				vtOldSyncList.add(vtNewList.elementAt(i));
			}
		}
		
		return vtOldSyncList;
	}
	/**
	 * method returns the Owners for a given projectId
	 * @param conn
	 * @param strProjectId
	 * @return
	 * @throws Exception
	 */
	public Vector getExistingWrkspcOwners(Connection conn, String strProjectId) 
	throws Exception {
		Vector vtUsers = new Vector();
		String sQuery = " select distinct user_id from ets.ets_users where user_project_id = '" + strProjectId +"' " 
						+ " and user_role_id in ( select role_id from ets.ets_roles where project_id='"+ strProjectId + "' and role_name = 'Workspace Owner' ) ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sQuery);
		
		while (rs.next()) {
			vtUsers.add(rs.getString("user_id"));
		}
		
		return vtUsers;
	}
	
	public boolean updateWrkspcOwner(Connection conn, String strProjectId, String strUserId, String strUserStatus)
		throws Exception{
		String sQuery =" update ets.ets_users set active_flag='"+strUserStatus + "' where user_project_id='" + strProjectId +"' and user_id='"+ strUserId +"'";
		
		logger.debug("update WO query::" + sQuery);
		Statement stmt = conn.createStatement();
		int iResult = stmt.executeUpdate(sQuery);
		ETSDBUtils.close(stmt);
		
		return true;

	}
}
