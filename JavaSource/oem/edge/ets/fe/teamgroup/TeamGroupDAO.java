/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
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

package oem.edge.ets.fe.teamgroup;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
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
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDetailedObj;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;

import org.apache.commons.logging.Log;

/**
 * This is the DATA ACCESS Class for the E&TS Team Groups Module
 * @author vishal
 */
public class TeamGroupDAO {

	/** Stores the Logging object */
	private Log m_pdLog = EtsLogger.getLogger(TeamGroupDAO.class);

	/** Stores the Connection Object */
	private Connection m_pdConnection = null;

	/**
	 * @throws Exception
	 */
	public void prepare() throws Exception {
		Connection pdConn = ETSDBUtils.getConnection();
		setConnection(pdConn);
	}

	/**
	 * Closes and releases a connection
	 */
	public void cleanup() throws SQLException {
		if (m_pdConnection != null && !m_pdConnection.isClosed()) {
			m_pdConnection.close();
		}
		m_pdConnection = null;
	}
	/**
	 * @return
	 */
	public Connection getConnection() {
		return m_pdConnection;
	}

	/**
	 * @param connection
	 */
	public void setConnection(Connection pdConnection) {
		m_pdConnection = pdConnection;
	}

	/**
	 * @param pdCon
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public ETSProj getProjectDetails(String strProjectID)
		throws SQLException, Exception {

		Statement stmtProjDetails = null;
		ResultSet rsProjDetails = null;
		StringBuffer strQuery = new StringBuffer(StringUtil.EMPTY_STRING);
		ETSProj udProjDetails = new ETSProj();

		strQuery.append(
			"SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,"
				+ "PROJECT_START,"
				+ "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,"
				+ "LOTUS_PROJECT_ID,RELATED_ID,"
				+ "PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,"
				+ "PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,"
				+ "IS_ITAR, PROJECT_TYPE "
				+ "FROM ETS.ETS_PROJECTS "
				+ "WHERE PROJECT_ID = '"
				+ strProjectID
				+ "' with ur");

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug("QUERY : " + strQuery.toString());
		}

		stmtProjDetails = m_pdConnection.createStatement();
		rsProjDetails = stmtProjDetails.executeQuery(strQuery.toString());

		if (rsProjDetails.next()) {

			String sIsITAR =
				ETSUtils.checkNull(rsProjDetails.getString("IS_ITAR"));

			udProjDetails.setProjectId(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_ID")));
			udProjDetails.setDescription(
				ETSUtils.checkNull(
					rsProjDetails.getString("PROJECT_DESCRIPTION")));
			udProjDetails.setName(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_NAME")));
			udProjDetails.setStartDate(
				rsProjDetails.getTimestamp("PROJECT_START"));
			udProjDetails.setEndDate(rsProjDetails.getTimestamp("PROJECT_END"));
			udProjDetails.setDecafProject(
				ETSUtils.checkNull(
					rsProjDetails.getString("DECAF_PROJECT_NAME")));
			udProjDetails.setProjectOrProposal(
				ETSUtils.checkNull(
					rsProjDetails.getString("PROJECT_OR_PROPOSAL")));
			udProjDetails.setLotusProject(
				ETSUtils.checkNull(
					rsProjDetails.getString("LOTUS_PROJECT_ID")));
			udProjDetails.setRelatedProjectId(
				ETSUtils.checkNull(rsProjDetails.getString("RELATED_ID")));
			udProjDetails.setParent_id(
				ETSUtils.checkNull(rsProjDetails.getString("PARENT_ID")));
			udProjDetails.setCompany(
				ETSUtils.checkNull(rsProjDetails.getString("COMPANY")));
			udProjDetails.setPmo_project_id(
				ETSUtils.checkNull(rsProjDetails.getString("PMO_PROJECT_ID")));
			udProjDetails.setShow_issue_owner(
				ETSUtils.checkNull(
					rsProjDetails.getString("SHOW_ISSUE_OWNER")));
			udProjDetails.setProject_status(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_STATUS")));
			udProjDetails.setProjBladeType(
				ETSUtils.isEtsProjBladeProject(strProjectID));

			udProjDetails.setDeliveryTeam(
				ETSUtils.checkNull(rsProjDetails.getString("DELIVERY_TEAM")));
			udProjDetails.setGeography(
				ETSUtils.checkNull(rsProjDetails.getString("GEOGRAPHY")));
			udProjDetails.setIndustry(
				ETSUtils.checkNull(rsProjDetails.getString("INDUSTRY")));

			if (sIsITAR.equalsIgnoreCase(GroupConstants.IND_YES)) {
				udProjDetails.setITAR(true);
			} else {
				udProjDetails.setITAR(false);
			}

			udProjDetails.setProjectType(
				rsProjDetails.getString("PROJECT_TYPE"));

		}

		return udProjDetails;
	}

	public Vector getGroupsMembers(
		String strProjectId,
		String[] strGroupIds)
		throws SQLException, Exception {
			
			Vector vt_Groups = new Vector();
			for (int i=0; i < strGroupIds.length; i++) {
				ETSGroup group = new ETSGroup();
				group = getGroupByIDAndProject(strProjectId,strGroupIds[i]);
				Vector vt_grpMemmbers = null;
				vt_grpMemmbers = getGroupMembers(strProjectId,strGroupIds[i],"","ASC",false);
				group.setGroupMembers(vt_grpMemmbers);
				vt_Groups.add(group);
			}
			
			return vt_Groups;
		}

	/**
	 * @param iParentId
	 * @param strSortBy
	 * @param strSortOrder
	 * @param bIsAdmin
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public Vector getAllGroups(
		String strProjectId,
		String strSortBy,
		String strSortOrder,
		boolean bIsAdmin,
		String strUserId)
		throws SQLException, Exception {
		String strBuffer = "name";

		if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strBuffer = "last_timestamp " + strSortOrder + ",name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			strBuffer = "name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			strBuffer = "owner " + strSortOrder + ",name";
		}
 
		String strQuery = 
			"select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp "
				+ "from ETS.GROUPS g "
				+ "where g.project_id= '"
				+ strProjectId
				+ "' "
				+ " order by "
				+ strBuffer
				+ " "
				+ strSortOrder
				+ " with ur";

		if (!bIsAdmin) {
			strQuery = "(select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp " +
					 "from ETS.GROUPS g " +
					 "where g.project_id= '" +
					 strProjectId +
					 "' and g.type in ('" + 
					 GroupConstants.ETS_PUBLIC + "','" +
					 GroupConstants.ETS_IBM_ONLY +
					 "' ))" +
					 "union (" +
					 " select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp" + 
					" from ETS.GROUPS g " +
					" where g.project_id='" +  
					strProjectId  +"' " +
					" and g.type in ('" + GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP + "','" +
					GroupConstants.ETS_PRIVATE_PUBLIC_GROUP + "')" +
					" and (g.owner = '" + strUserId + "'" +
					" or g.group_id in ( select u.group_id from ets.user_groups u " +
					" where u.user_id='" + strUserId + "'" +
					" and u.group_id = g.group_id ))) " +
					" order by " +
					 strBuffer +
					 " " +
					 strSortOrder +
					 " with ur ";
					
					
		}
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		Statement stmtGetGroups = m_pdConnection.createStatement();
		ResultSet rsGetGroups = stmtGetGroups.executeQuery(strQuery);

		Vector groups = new Vector();

		while (rsGetGroups.next() != false) {
			ETSGroup udGroup = new ETSGroup();
			udGroup = getGroup(rsGetGroups);
			groups.add(udGroup);
		}
		rsGetGroups.close();
		stmtGetGroups.close();
		return populateUserNames(groups);
	}

	/**
	 * Method returns all the group where the WO/WM can add the users.
	 * @param iParentId
	 * @param strSortBy
	 * @param strSortOrder
	 * @param bIsAdmin
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public Vector getAllGroupsForEdit(
		String strProjectId,
		String strSortBy,
		String strSortOrder,
		boolean bIsAdmin,
		String strUserId)
		throws SQLException, Exception {
		String strBuffer = "name";

		if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strBuffer = "last_timestamp " + strSortOrder + ",name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			strBuffer = "name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			strBuffer = "owner " + strSortOrder + ",name";
		}
 
		String strQuery = "select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp " +
					 "from ETS.GROUPS g " +
					 "where g.project_id= '" +
					 strProjectId +
					 "' and (g.type in ('" + GroupConstants.ETS_IBM_ONLY + "','" + GroupConstants.ETS_PUBLIC + "')" +
					 " or (g.type in ('" + GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP + "','" + GroupConstants.ETS_PRIVATE_PUBLIC_GROUP + "')" +
					 " and g.owner = '" + strUserId + "'))" +
					 " and g.name !='" + Defines.GRP_ALL_USERS + "' " +
					 " order by " +
					 strBuffer +
					 " " +
					 strSortOrder +
					 " with ur";
					
					
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		Statement stmtGetGroups = m_pdConnection.createStatement();
		ResultSet rsGetGroups = stmtGetGroups.executeQuery(strQuery);

		Vector groups = new Vector();

		while (rsGetGroups.next() != false) {
			ETSGroup udGroup = new ETSGroup();
			udGroup = getGroup(rsGetGroups);
			groups.add(udGroup);
		}
		rsGetGroups.close();
		stmtGetGroups.close();
		return populateUserNames(groups);
	}

	/**
	 * @param rsGroup
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSGroup getGroup(ResultSet rsGroup) throws SQLException {
		ETSGroup udGroup = new ETSGroup();

		udGroup.setGroupId(rsGroup.getString("GROUP_ID"));
		udGroup.setProjectId(rsGroup.getString("PROJECT_ID"));
		udGroup.setGroupOwner(rsGroup.getString("OWNER"));
		udGroup.setUserId(rsGroup.getString("OWNER"));
		udGroup.setGroupName(rsGroup.getString("NAME"));
		udGroup.setGroupDescription(rsGroup.getString("DESCRIPTION"));
		
		// The security classification & group type are saved in the same column in db
		if (rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP) || 
				rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_PUBLIC_GROUP)) {
			udGroup.setGroupType("PRIVATE");
			if (rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_PUBLIC_GROUP)) {
				udGroup.setGroupSecurityClassification("0");
			} else {
				udGroup.setGroupSecurityClassification("1");
			}
		} else {
			udGroup.setGroupType("PUBLIC");
			udGroup.setGroupSecurityClassification(rsGroup.getString("TYPE"));
		}
		udGroup.setLastTimestamp(rsGroup.getTimestamp("LAST_TIMESTAMP"));

		return udGroup;
	}

	/**
	 * @param strUserId
	 * @param strProjId
	 * @param iPrivilige
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean hasProjectPriv(
		String strUserId,
		String strProjId,
		int iPrivilige)
		throws SQLException {
		int iPrivValue = 0;
		Statement stmtPrivilige = m_pdConnection.createStatement();

		ResultSet rsPrivilige =
			stmtPrivilige.executeQuery(
				"select PRIV_VALUE from ETS.ETS_ROLES "
					+ "where PRIV_ID = "
					+ iPrivilige
					+ " and ROLE_ID = ("
					+ "select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '"
					+ strUserId
					+ "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '"
					+ strProjId
					+ "') with ur");

		if (rsPrivilige.next()) {
			iPrivValue = rsPrivilige.getInt("PRIV_VALUE");
		}

		rsPrivilige.close();
		stmtPrivilige.close();

		return (iPrivValue == 1);
	}

	/**
	 * @param strOwnerID
	 * @param iID
	 * @param strProjectID
	 * @param strUserRole
	 * @param isSuperAdmin
	 * @param isExecutive
	 * @param isViewOnly
	 * @param bIsCat
	 * @param strUserID
	 * @return
	 */
	public boolean isAuthorized(
		String strOwnerID,
		int iID,
		String strProjectID,
		String strUserRole,
		boolean isSuperAdmin,
		boolean isExecutive,
		boolean isViewOnly,
		boolean bIsCat,
		String strUserID) {

		if (isViewOnly
			&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
				|| isSuperAdmin
				|| isExecutive
				|| strOwnerID.equals(strUserID)))
			return true;
		else if (
			(!isViewOnly)
				&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
					|| isSuperAdmin
					|| strOwnerID.equals(strUserID)))
			return true;
		return false;
	}


	/**
	 * @param udGroup
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean delGroup(String strProjectId, String[] strGroupIds, String strUserId)
		throws SQLException {

		int index;
		boolean bSuccess = false;
		for (index = 0 ; index < strGroupIds.length; index ++)
		{
			bSuccess = delGroup(strProjectId, strGroupIds[index], strUserId);
		}
		return true;
	}

	public synchronized boolean delGroup(String strProjectId, String strGroupID, String strUserId)
		throws SQLException {
		try {


			Statement stmtDelGroupMembers = m_pdConnection.createStatement();
			String strDelMembersSQL =
				"delete from ETS.USER_GROUPS where group_id = '"
					+ strGroupID
					+ "' ";
			int iRowCount = stmtDelGroupMembers.executeUpdate(strDelMembersSQL);

			Statement stmtDelGroup = m_pdConnection.createStatement();
			//String update = "delete from ETS.ETS_DOC where DOC_ID = " + docid;
			String strUpdateSQL =
				"delete from ETS.GROUPS where group_id = '"
					+ strGroupID
					+ "' ";
			int iRowCounter = stmtDelGroup.executeUpdate(strUpdateSQL);

			if (iRowCounter != 1) {
				m_pdLog.error(
					"executeUpdate("
						+ strUpdateSQL
						+ ") returned "
						+ iRowCount);
				return false;
			}

			stmtDelGroupMembers.close();
			stmtDelGroup.close();


			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param t
	 * @return
	 */
	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	/**
	 * @param strUserID
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjects(String strUserID, String strProjectID)
		throws SQLException {
		PreparedStatement stmtProjects =
			m_pdConnection.prepareStatement(
				"select p.* from ETS.ETS_PROJECTS p, ETS.ETS_USERS u "
					+ "where u.user_id = ? and p.project_id = ? "
					+ "and u.active_flag='A' "
					+ "and u.user_project_id = p.project_id with ur");

		stmtProjects.setString(1, strUserID);
		stmtProjects.setString(2, strProjectID);
		ResultSet rsProjects = stmtProjects.executeQuery();

		Vector projects = null;
		projects = getProjs(rsProjects);

		stmtProjects.close();
		return projects;
	}

	/**
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProject(String strProjectID) throws SQLException {
		PreparedStatement stmtProjects =
			m_pdConnection.prepareStatement(
				"select p.* from ETS.ETS_PROJECTS p "
					+ "where p.project_id = ? with ur");

		stmtProjects.setString(1, strProjectID);
		ResultSet rsProjects = stmtProjects.executeQuery();

		Vector vtProjects = null;
		vtProjects = getProjs(rsProjects);

		stmtProjects.close();
		return vtProjects;
	}

	/**
	 * @param rsProjects
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getProjs(ResultSet rsProjects) throws SQLException {
		Vector vtProjects = new Vector();

		while (rsProjects.next()) {
			ETSProj proj = getProj(rsProjects);
			vtProjects.addElement(proj);
		}
		return vtProjects;
	}

	/**
	 * @param rsProject
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private ETSProj getProj(ResultSet rsProject) throws SQLException {
		ETSProj udProject = new ETSProj();

		String sProjId = ETSUtils.checkNull(rsProject.getString("PROJECT_ID"));
		String sProjDesc =
			ETSUtils.checkNull(rsProject.getString("PROJECT_DESCRIPTION"));
		String sProjName =
			ETSUtils.checkNull(rsProject.getString("PROJECT_NAME"));
		Timestamp tProjStart = rsProject.getTimestamp("PROJECT_START");
		Timestamp tProjEnd = rsProject.getTimestamp("PROJECT_END");
		String sDecafProjName =
			ETSUtils.checkNull(rsProject.getString("DECAF_PROJECT_NAME"));
		String sProjOrProposal =
			ETSUtils.checkNull(rsProject.getString("PROJECT_OR_PROPOSAL"));
		String sLotusProjID =
			ETSUtils.checkNull(rsProject.getString("LOTUS_PROJECT_ID"));
		String sRelatedId =
			ETSUtils.checkNull(rsProject.getString("RELATED_ID"));
		String sParentId = ETSUtils.checkNull(rsProject.getString("PARENT_ID"));
		String sCompany = ETSUtils.checkNull(rsProject.getString("COMPANY"));
		String sPmoProjectId =
			ETSUtils.checkNull(rsProject.getString("PMO_PROJECT_ID"));
		String sShowIssueOwner =
			ETSUtils.checkNull(rsProject.getString("SHOW_ISSUE_OWNER"));
		String sProjectStatus =
			ETSUtils.checkNull(rsProject.getString("PROJECT_STATUS"));

		udProject.setProjectId(sProjId);
		udProject.setDescription(sProjDesc);
		udProject.setName(sProjName);
		udProject.setStartDate(tProjStart);
		udProject.setEndDate(tProjEnd);
		udProject.setDecafProject(sDecafProjName);
		udProject.setProjectOrProposal(sProjOrProposal);
		udProject.setLotusProject(sLotusProjID);
		udProject.setRelatedProjectId(sRelatedId);
		udProject.setParent_id(sParentId);
		udProject.setCompany(sCompany);
		udProject.setPmo_project_id(sPmoProjectId);
		udProject.setShow_issue_owner(sShowIssueOwner);
		udProject.setProject_status(sProjectStatus);

		return udProject;
	}



	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId) throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_USERS "
					+ "where user_project_id = ? "
					+ "and active_flag in ('A','P','R') "
					+ "order by user_id with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtMembers = null; //new Vector();
		vtMembers = getUsers(rsProjMembers);
		stmtProjMembers.close();
		return populateUserNames(vtMembers);
	}

	public List getProjMembersList(String strProjectId, boolean bCheckAmtTables)
		throws Exception {
			List lstprojMembers = new ArrayList();
			Vector vtprojmembers = getProjMembers(strProjectId,bCheckAmtTables);
			for (int u=0; u < vtprojmembers.size(); u++) {
				lstprojMembers.add(vtprojmembers.elementAt(u));
			}
			return lstprojMembers;
		}
	/**
	 * @param strProjectId
	 * @param bCheckAmtTables
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId, boolean bCheckAmtTables)
		throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select u.*, a.user_fname, a.user_lname "
					+ "from ETS.ETS_USERS u, AMT.USERS a "
					+ "where u.user_project_id = ?"
					+ " and u.user_id = a.ir_userid "
					+ "and u.active_flag in ('A','P','R') "
					+ "order by a.USER_FULLNAME with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtProjMembers = getUsers(rsProjMembers);
		stmtProjMembers.close();
		return vtProjMembers;
	}

	/**
	 * @param rsUsers
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getUsers(ResultSet rsUsers) throws SQLException {
		Vector v = new Vector();

		while (rsUsers.next()) {
			ETSUser udUser = getUser(rsUsers);
			v.addElement(udUser);
		}
		return v;
	}

	/**
	 * @param rsUser
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private ETSUser getUser(ResultSet rsUser) throws SQLException {
		ETSUser udUser = new ETSUser();

		udUser.setUserId(rsUser.getString("USER_ID"));
		//m_pdLog.debug(user.getUserId());
		//spn 0312 projid
		udUser.setProjectId(rsUser.getString("USER_PROJECT_ID"));
		udUser.setRoleId(rsUser.getInt("USER_ROLE_ID"));
		udUser.setUserJob(rsUser.getString("USER_JOB"));
		udUser.setPrimaryContact(rsUser.getString("PRIMARY_CONTACT"));
		udUser.setLastUserId(rsUser.getString("LAST_USERID"));
		udUser.setLastTimestamp(rsUser.getTimestamp("LAST_TIMESTAMP"));
		udUser.setActiveFlag(rsUser.getString("ACTIVE_FLAG"));

		// Set the User Name as well. This will avoid unnecessary round-trips to
		// the database to get the Full Name
		try {
			udUser.setUserName(
				StringUtil.trim(rsUser.getString("USER_FNAME"))
					+ StringUtil.SPACE
					+ StringUtil.trim(rsUser.getString("USER_LNAME")));
		} catch (Exception e) {
			// DO NOTHING
		}

		return udUser;
	}


	/**
	 * @param udObj
	 */
	private void populateUserName(ETSDetailedObj udObj) {
		try {
			udObj.setUserName(
				ETSUtils.getUsersName(m_pdConnection, udObj.getUserId()));
		} catch (Exception e) {
			udObj.setUserName(udObj.getUserId());
		}
	}

	/**
	 * @param vtObjects
	 * @return
	 */
	private Vector populateUserNames(Vector vtObjects) {
		Vector vtPopulatedObjects = new Vector();
		for (int iCounter = 0; iCounter < vtObjects.size(); iCounter++) {
			ETSDetailedObj udObj = (ETSDetailedObj) vtObjects.get(iCounter);
			populateUserName(udObj);
			vtPopulatedObjects.add(udObj);
		}

		return vtPopulatedObjects;
	}

	/**
	 * @param vtUsers
	 * @return
	 */
	public Vector getIBMMembers(Vector vtUsers) {
		Vector vtIBMMembers = new Vector();

		if (vtUsers == null) {
			return new Vector();
		}
		for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
			ETSUser udMember = (ETSUser) vtUsers.elementAt(iCounter);
			try {
				String edge_userid =
					AccessCntrlFuncs.getEdgeUserId(
						m_pdConnection,
						udMember.getUserId());
				String decaftype =
					AccessCntrlFuncs.decafType(edge_userid, m_pdConnection);
				if (decaftype.equals("I")) {
					vtIBMMembers.addElement(udMember);
				}
			} catch (AMTException a) {
				m_pdLog.error("amt error=", a);
			} catch (SQLException s) {
				m_pdLog.error("sql error=", s);
			}
		}

		return vtIBMMembers;
	}



	/**
	 * @param projectId
	 * @return
	 * @throws SQLException
	 */
	public EtsPrimaryContactInfo getProjContactInfo(String projectId)
		throws SQLException {

		ArrayList contList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();

		Statement stmt = null;
		ResultSet rs = null;

		sb.append("select a.ir_userid as iruserid,");
		sb.append("a.user_fullname as userfullname,");
		sb.append("a.user_email as useremail,a.user_phone as userphone");
		sb.append("   from amt.users a, ets.ets_users b ");
		sb.append("   where ");
		sb.append("   a.ir_userid=b.user_id");
		sb.append("   and   b.primary_contact='Y' ");
		sb.append("   and   b.user_project_id='" + projectId + "'");
		sb.append("  with ur");

		stmt = m_pdConnection.createStatement();
		rs = stmt.executeQuery(sb.toString());

		if (rs != null) {
			while (rs.next()) {

				etsContInfo.setUserIrId(
					AmtCommonUtils.getTrimStr(rs.getString("IRUSERID")));
				etsContInfo.setUserFullName(
					AmtCommonUtils.getTrimStr(rs.getString("USERFULLNAME")));
				etsContInfo.setUserEmail(
					AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL")));
				etsContInfo.setUserContPhone(
					AmtCommonUtils.getTrimStr(rs.getString("USERPHONE")));

			}

		}

		return etsContInfo;

	}

	/**
	 * @param strProjectId
	 * @param strName
	 * @return
	 * @throws SQLException
	 */
	public int getGroupByNameAndProject(String strProjectId, String strName)
		throws SQLException {

		int iCount = 0;
		String strQuery =
			"SELECT COUNT(*) AS COUNT FROM ETS.GROUPS "
				+ "WHERE NAME = ? AND PROJECT_ID = ? ";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}

		PreparedStatement stmtGetGroups =
			m_pdConnection.prepareStatement(strQuery);
		stmtGetGroups.setString(1, strName);
		stmtGetGroups.setString(2, strProjectId);
		ResultSet rsGetGroups = stmtGetGroups.executeQuery();

		if (rsGetGroups.next()) {
			iCount = rsGetGroups.getInt("COUNT");
		}

		rsGetGroups.close();
		stmtGetGroups.close();
		return iCount;
	}

	/**
	 * @param strProjectId
	 * @param strName
	 * @return
	 * @throws SQLException
	 */
	public ETSGroup getGroupByIDAndProject(String strProjectId, String strGroupId)
		throws SQLException {

		int iCount = 0;
		String strQuery =
			"SELECT g.* FROM ETS.GROUPS g "
				+ "WHERE GROUP_ID = ? AND PROJECT_ID = ? ";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}

		PreparedStatement stmtGroupDetails =
			m_pdConnection.prepareStatement(strQuery);
		stmtGroupDetails.setString(1, strGroupId);
		stmtGroupDetails.setString(2, strProjectId);
		ResultSet rsGroupDetails = stmtGroupDetails.executeQuery();

		ETSGroup udGroup = null;
		if (rsGroupDetails.next()) {
			udGroup = getGroup(rsGroupDetails);
		}

		rsGroupDetails.close();
		stmtGroupDetails.close();
		populateUserName(udGroup);
		return udGroup;
	}

	/**
	 * @param strProjectId
	 * @param strName
	 * @return
	 * @throws SQLException
	 */
	public Vector getGroupByIDAndProject(String strProjectId, String[] strGroupId)
		throws SQLException {
		Vector vt_Groups = new Vector();
		int index;
		for (index=0; index < strGroupId.length; index++){
			ETSGroup udGroup = getGroupByIDAndProject(strProjectId,strGroupId[index]);
			if (udGroup != null) {
				vt_Groups.add(udGroup);
			}
		}

		return populateUserNames(vt_Groups);

	}
	/**
	 * @param strProjectId
	 * @param iViewType
	 * @return
	 * @throws SQLException
	 */
	public int getTopCatId(String strProjectId, int iViewType)
		throws SQLException {

		int iTopCatID = 0;

		String query =
			"select cat_id from ets.ets_cat"
				+ " where view_type = "
				+ iViewType
				+ " and project_id = '"
				+ strProjectId
				+ "'"
				+ " and parent_id=0 with ur";

		Statement stmtTopCat = m_pdConnection.createStatement();
		ResultSet rsTopCat = stmtTopCat.executeQuery(query);

		if (rsTopCat.next()) {
			iTopCatID = rsTopCat.getInt("cat_id");
		}
		rsTopCat.close();
		stmtTopCat.close();

		return iTopCatID;
	}

	
	public List getGroupMembersList(String projectId, String strGroupId,String strSortBy, String ad, boolean bRole) 
		throws SQLException,Exception {
			List lstGroupMembers = new ArrayList();
			Vector vt_grpMembers = new Vector();
			vt_grpMembers = getGroupMembers(projectId,strGroupId,strSortBy,ad,bRole);
			for (int i=0; i < vt_grpMembers.size(); i++) {
				lstGroupMembers.add(vt_grpMembers.elementAt(i));
			}
			
			return lstGroupMembers;
	}
	
	public Vector getGroupMembers(String projectId, String strGroupId,String strSortBy, String ad, boolean bRole) 
	throws SQLException,Exception {
		Connection  connection = null;
		Vector grpMembers = null;
		try {
			connection = ETSDBUtils.getConnection();
			grpMembers = getGroupMembers(projectId,strGroupId,strSortBy,ad,bRole,connection);
			
		} catch (SQLException e) {
		//printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			ETSDBUtils.close(connection);
			
		}
		return grpMembers;
	}	

	public Vector getGroupMembers(String projectId, String strGroupId,String strSortBy, String ad, boolean bRole, Connection conn) 
	throws SQLException {
		String sb = "a.user_fname " + Defines.SORT_ASC_STR + ", a.user_lname";

		ETSGroup grp = getGroupByIDAndProject(projectId,strGroupId);
		PreparedStatement getGrpMemSt;

		if (grp.getGroupName().equalsIgnoreCase(Defines.GRP_ALL_USERS)) {
			getGrpMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type  "
					 + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?"
					 + " and u.user_id = a.ir_userid and a.edge_userid = d.userid and u.active_flag='A' order by "
					 + sb + " " + Defines.SORT_ASC_STR + " with ur");

			getGrpMemSt.setString(1, projectId);

		} else {
			getGrpMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type  "
				 + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?"
				 + " and u.user_id = a.ir_userid and u.user_id in ( select user_id from ETS.USER_GROUPS where group_id = ? )and a.edge_userid = d.userid order by "
				 + sb + " " + Defines.SORT_ASC_STR + " with ur");

			getGrpMemSt.setString(1, projectId);
			getGrpMemSt.setString(2, strGroupId);

		}
		ResultSet rs = getGrpMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = getAllUsersData(rs);
		getGrpMemSt.close();
		return populateUserNames(mems);
		
	}	
		
	public List getAvailableMembersList(String projectId, String strGroupId) throws SQLException, Exception {
		Connection connection = null;
		List members = null;
		try {
			connection = ETSDBUtils.getConnection();
			members = getAvailableMembersList(projectId, strGroupId, connection);
		} catch (SQLException e) {
			//printErr("error=" + e);
			throw e;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (connection != null) {
				ETSDBUtils.close(connection);
			}
		}
		return members;
	}
	/*
	 *  Return Members who do not belong to a group 
	 */
	public List getAvailableMembersList(String projectid, String groupid, Connection conn) throws SQLException {
		String sb = "a.user_fname " + Defines.SORT_ASC_STR + ", a.user_lname";

		PreparedStatement getAvailMemSt;

		getAvailMemSt = conn.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type  "
				 + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?"
				 + " and u.user_id = a.ir_userid and u.active_flag in ('A','P','R') and u.user_id not in ( select user_id from ETS.USER_GROUPS where group_id = ? )and a.edge_userid = d.userid order by "
				 + sb + " " + Defines.SORT_ASC_STR + " with ur");
		
		getAvailMemSt.setString(1, projectid);
		getAvailMemSt.setString(2, groupid);
		ResultSet rs = getAvailMemSt.executeQuery();

		Vector mems = null; //new Vector();
		mems = populateUserNames(getAllUsersData(rs));
		getAvailMemSt.close();
		List lstMembers = new ArrayList();
		for (int u=0; u < mems.size(); u++) {
			lstMembers.add(mems.elementAt(u));
		}
		return lstMembers;
	}

	private Vector getAllUsersData(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSUser user = getAllUserData(rs);
			v.addElement(user);
		}
		return v;
		
	}
	private ETSUser getAllUserData(ResultSet rs) throws SQLException {
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

	/* 
	 * Need to generate the new GroupID
	 */
	

	public static synchronized String getUniqueGroupID() throws SQLException, Exception {

		String sUniqueId = "";
		String sTemp = "";
		String sPrefix = "";
		
		Long lDate = new Long(System.currentTimeMillis());

		//sUniqueId = sPrefix + "-" + lDate;
		sUniqueId = lDate.toString();

		return sUniqueId;
	}

	/*
	 * Add Group defination in the db 
	 * @author vishal
	 */
	 public synchronized boolean addGroup(ETSGroup udGroup) throws SQLException, Exception {
	 	boolean bSuccess = false ;
		try {

			PreparedStatement stmtInsGrp =
				m_pdConnection.prepareStatement(
					"insert into ETS.GROUPS (GROUP_ID, NAME, DESCRIPTION, TYPE, PROJECT_ID, OWNER, LAST_TIMESTAMP )" 
	 				+ " values (?,?,?,?,?,?,current timestamp)" );
			
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("Adding New Group properties, GrpId - " + udGroup.getGroupId());
			}				
			stmtInsGrp.setString(1,udGroup.getGroupId());
			stmtInsGrp.setString(2,StringUtil.trim(udGroup.getGroupName()));
			stmtInsGrp.setString(3,udGroup.getGroupDescription());
			if (udGroup.getGroupType().equals("PRIVATE")) {
				if (udGroup.getGroupSecurityClassification().equals("0")) {
					stmtInsGrp.setString(4,GroupConstants.ETS_PRIVATE_PUBLIC_GROUP);
				} else {
					stmtInsGrp.setString(4,GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP);
				}
			} else {
				stmtInsGrp.setString(4,udGroup.getGroupSecurityClassification());
			}
			//stmtInsGrp.setString(4,udGroup.getGroupType());
			stmtInsGrp.setString(5,udGroup.getProjectId());
			stmtInsGrp.setString(6,udGroup.getGroupOwner());		

			stmtInsGrp.executeUpdate();
			stmtInsGrp.close();
			bSuccess = true;
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			bSuccess = false;
			m_pdLog.error("sql error in create group= " + e);
			throw e;
		} finally {
			
		}
		return bSuccess;
	}
	 
	/*
	  *  Updates the Group Properties
	  * @author vishal
	  */
	public synchronized boolean updateGroupProperties (ETSGroup udGroup) throws SQLException {
		boolean bSuccess = false ;
		String strGrpType = "";
		try {

			PreparedStatement stmtUpdGrp =
				m_pdConnection.prepareStatement(
					"update ETS.GROUPS set TYPE=?, PROJECT_ID=?, LAST_TIMESTAMP=current timestamp" 
					+ " where GROUP_ID=? ");
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("Updating Group properties, GrpId - " + udGroup.getGroupId());
			}		
			if (udGroup.getGroupType().equals("PRIVATE")) {
				if (udGroup.getGroupSecurityClassification().equals("0")) {
					strGrpType = GroupConstants.ETS_PRIVATE_PUBLIC_GROUP;
				} else {
					strGrpType = GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP;
				}
			} else  {
				strGrpType = udGroup.getGroupSecurityClassification();
			}
			stmtUpdGrp.setString(1,strGrpType);
			stmtUpdGrp.setString(2,udGroup.getProjectId());
			//stmtUpdGrp.setString(3,udGroup.getGroupOwner());		
			stmtUpdGrp.setString(3,udGroup.getGroupId());
			
			stmtUpdGrp.executeUpdate();
			stmtUpdGrp.close();
			bSuccess = true;
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			bSuccess = false;
			m_pdLog.error("sql error in update group properties= " + e);
			throw e;
		} finally {
			
		}
		return bSuccess;
	}
	
	public synchronized boolean updateGroupOwner(String strProjectId, String strGrpId, String strUserId) 
	throws SQLException {
		boolean bSuccess = false ;
		String strGrpType = "";
		try {

			PreparedStatement stmtUpdGrp =
				m_pdConnection.prepareStatement(
					"update ETS.GROUPS set OWNER = ? , LAST_TIMESTAMP=current timestamp" 
					+ " where GROUP_ID=? ");
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("Updating Group properties, GrpId - " + strGrpId);
			}		
			stmtUpdGrp.setString(1,strUserId);
			stmtUpdGrp.setString(2,strGrpId);
			
			stmtUpdGrp.executeUpdate();
			stmtUpdGrp.close();
			bSuccess = true;
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			bSuccess = false;
			m_pdLog.error("sql error in update group Owner= " + e);
			throw e;
		} finally {
			
		}
		return bSuccess;
		
	}
	
	public synchronized boolean addNewMemberToGrp (
		String strProjId,
		String strGrpId,
		String strNewUserId,
		String strLastUserId) 
		throws SQLException {
		
		boolean status = false;

		PreparedStatement getGrpMemSt;

		getGrpMemSt = m_pdConnection.prepareStatement("select count(*) as COUNT  "
				 + "from ETS.USER_GROUPS u where group_id = ? and user_id = ?" );
		
		getGrpMemSt.setString(1, strGrpId);
		getGrpMemSt.setString(2, strNewUserId);
		ResultSet rs = getGrpMemSt.executeQuery();
		int iCount =0;

		if (rs.next()) {
			iCount = rs.getInt("COUNT");
		}
		System.out.println("Add Mem to Group - count for user -" + strNewUserId + "::"+ iCount);
		if (iCount == 0) {
			PreparedStatement addMemSt;
			addMemSt = m_pdConnection.prepareStatement("insert into ets.user_groups values "
					+ "(?,?,?,current timestamp)" );
			addMemSt.setString(1,strGrpId);
			addMemSt.setString(2,strNewUserId);
			addMemSt.setString(3,strLastUserId);
			
			addMemSt.executeUpdate();
			addMemSt.close();
			status = true;
		} else {
			status = true;
		}

		getGrpMemSt.close();
		return status;
	}
	/*
	 * Adds the members of the group in the USER_GROUPS table
	*/
	public synchronized boolean updateGroupMembersList (
		String strAddUsers, 
		String strRemoveUsers,
		ETSGroup udGroup) 	
		throws SQLException, Exception {
		
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdGrpUsers = m_pdConnection.createStatement();


		if (!strAddUsers.equals(StringUtil.EMPTY_STRING))
		{
			strQuery = "insert into ets.user_groups (group_id,user_id, last_userid,last_timestamp) "
					+ "values "
					+ strAddUsers;
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("Insert users to Grp : " + strQuery.toString());
			}
			stmtUpdGrpUsers.executeUpdate(strQuery);
		}


		if (!strRemoveUsers.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.user_groups"
				+ " where group_id ='"
				+ udGroup.getGroupId()
				+ "'"
				+ " and user_id in ("
				+ strRemoveUsers
				+ ")";
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("Remove Users frm Grp : " + strQuery.toString());
			}

			stmtUpdGrpUsers.executeUpdate(strQuery);
		}

		stmtUpdGrpUsers.close();

		return true;
	}
	/*
	 * Method Return the vector of Groups for the given user-id is having membership  
	 * @author vishal
	 * @param strProjectId
	 * @param strUserId
	 * @return
	 * @throws SQLException
	 */
	public Vector getGroupsForUser ( 
		String strProjectId, 
		String strUserId,
		Connection conn ) 
		throws SQLException, Exception {
		
			Vector vtGroups = new Vector();
			PreparedStatement getGrpForMember;

			getGrpForMember = conn.prepareStatement( " select grp.* from ETS.GROUPS grp, ETS.USER_GROUPS usr " 
					+ " where grp.project_id = ?  and grp.group_id = usr.group_id "
					+ " and usr.user_id = ? order by grp.name with ur ");
			PreparedStatement getAvailMemSt;

			getGrpForMember.setString(1, strProjectId);
			getGrpForMember.setString(2, strUserId);
			ResultSet rs = getGrpForMember.executeQuery();

			while (rs.next() != false) {
				ETSGroup udGroup = new ETSGroup();
				udGroup = getGroup(rs);
				vtGroups.add(udGroup);
			}
			rs.close();
			getGrpForMember.close();
			return vtGroups;

	}
	
	/*
	 * creates string for the grpList for a given user
	 * @author vishal
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */	
	public String getGrpListStringForUser(String projectid ,String userid, Connection conn) 
	throws SQLException , Exception {
		
		String strGrpNameString = "";
		Vector vtGrps = null;
		String strGrpname;
		vtGrps = getGroupsForUser(projectid,userid,conn);

		// add All users groups to the user groups List
		int iAllGrpCount = getGroupByNameAndProject(projectid,"All Users");
		if (iAllGrpCount > 0) {
			strGrpNameString = Defines.GRP_ALL_USERS;
		}

		for (int iCounter=0; iCounter < vtGrps.size(); iCounter++) {
			strGrpname = ((ETSGroup)vtGrps.elementAt(iCounter)).getGroupName();
			if (strGrpNameString.equals(StringUtil.EMPTY_STRING)) {
				strGrpNameString = strGrpname;
			} else {
				strGrpNameString = strGrpNameString + ", " + strGrpname;
			}
		}
		
		
		return strGrpNameString;
	}
	
	public Vector getAllDocsForGrp(
		String strProjectId,
		String strGrpIdString,
		String strSortBy,
		String strSortOrder, 
		boolean bIsAdmin,
		String strUserId)
		throws SQLException, Exception {
		
		String strBuffer = "d.doc_name";

		if (strSortBy.equals(Defines.SORT_BY_CUSTOM_STR)) {
			strBuffer = "d.doc_upload_date desc, d.user_id asc, upper(d.doc_name)";
		} else if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strBuffer = "d.doc_upload_date " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			strBuffer = "d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			strBuffer = "d.user_id " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_UPDATE_DATE_STR)) {
			strBuffer = "d.doc_update_date " + strSortOrder + ",d.doc_name";
		}

		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		String strQuery = "select distinct d.DOC_ID,d.PROJECT_ID,d.USER_ID,d.DOC_NAME,d.CAT_ID," 
				+ " d.DOC_SIZE,d.DOC_UPLOAD_DATE,d.DOC_UPDATE_DATE,d.DOC_PUBLISH_DATE,d.DOC_TYPE," 
				+ " d.UPDATED_BY,d.HAS_PREV_VERSION,d.LATEST_VERSION,d.LOCK_FINAL_FLAG,d.LOCKED_BY," 
				+ " d.DELETE_FLAG,d.DELETED_BY,d.MEETING_ID,d.IBM_ONLY,d.ISPRIVATE,d.IBM_CONF,d.EXPIRY_DATE"
				+ " from ETS.ETS_DOC d, ETS.ETS_PRIVATE_DOC p "
				+ " where d.project_id='" 
				+ strProjectId
				+ "' and d.project_id = p.project_id "
				+ " and d.doc_id = p.doc_id"
				+ " and p.group_id in (" + strGrpIdString + ")"
				+ " and d.issue_id is null and itar_upload_status !='P'"
				+ " and cat_id > 0 and delete_flag !='"
				+ DocConstants.TRUE_FLAG 
				+ "' and d.latest_version='"
				+ Defines.TRUE_FLAG
				+ "' "
				+ "and ((date(d.expiry_date)>date('"
				+ sqlToday
				+ "')) or d.expiry_date=? "
				+ "or d.expiry_date is null) "
				+ " order by " 
				+ strBuffer + " "
				+ strSortOrder 
				+ " with ur";

		if (!bIsAdmin) {
			strQuery = "select distinct d.DOC_ID,d.PROJECT_ID,d.USER_ID,d.DOC_NAME,d.CAT_ID," 
					+ " d.DOC_SIZE,d.DOC_UPLOAD_DATE,d.DOC_UPDATE_DATE,d.DOC_PUBLISH_DATE,d.DOC_TYPE," 
					+ " d.UPDATED_BY,d.HAS_PREV_VERSION,d.LATEST_VERSION,d.LOCK_FINAL_FLAG,d.LOCKED_BY," 
					+ " d.DELETE_FLAG,d.DELETED_BY,d.MEETING_ID,d.IBM_ONLY,d.ISPRIVATE,d.IBM_CONF,d.EXPIRY_DATE"
					+ " from ETS.ETS_DOC d, ETS.ETS_PRIVATE_DOC p " 
					+ " where d.project_id='"
					+ strProjectId
					+ "' and d.doc_id = p.doc_id "
					+ " and d.project_id = p.project_id "
					+ " and p.group_id in (" + strGrpIdString + ")"
					+ " and d.issue_id is null and itar_upload_status !='P'"
					+ " and cat_id > 0 and delete_flag !='"
					+ DocConstants.TRUE_FLAG
					+ "' and d.latest_version='"
					+ Defines.TRUE_FLAG
					+ "' "
					+ "and ((date(d.expiry_date)>date('"
					+ sqlToday
					+ "')) or d.expiry_date=? "
					+ "or d.expiry_date is null) "
					+ " and (d.isprivate!='1' or d.isprivate is null"
					+ " or d.user_id='" 
					+ strUserId 
					+ "' or '" 
					+ strUserId
					+ "' in ((select u.user_id from ets.ets_private_doc u "
					+ "where u.doc_id=d.doc_id) " 					
					+ "union"
					+ "(select u.user_id from ets.user_groups u where group_id in "
					+ "(select pd.group_id from ets.ets_private_doc pd where pd.doc_id = d.doc_id)) )) "
					+ " order by " 
					+ strBuffer
					+ " " + strSortOrder + " with ur";
		}

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		PreparedStatement stmtGetDocs = m_pdConnection.prepareStatement(strQuery);
		stmtGetDocs.setTimestamp(1, new Timestamp(0));
		ResultSet rsGetDocs = stmtGetDocs.executeQuery();

		Vector docs = new Vector();
		docs = populateUserNames(getDocs(rsGetDocs));

		rsGetDocs.close();
		stmtGetDocs.close();
		return docs;
	}

	/**
	 * @param rsDocs
	 * @return
	 * @throws SQLException
	 *             In case of database errors
	 */
	private Vector getDocs(ResultSet rsDocs) throws SQLException {
		Vector vtDocs = new Vector();

		while (rsDocs.next()) {
			ETSDoc doc = getDoc(rsDocs);
			vtDocs.addElement(doc);
		}

		return vtDocs;
	}

	/**
	 * @param rsDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDoc(ResultSet rsDoc) throws SQLException {
		ETSDoc udDoc = new ETSDoc();

		udDoc.setId(rsDoc.getInt("DOC_ID"));
		udDoc.setProjectId(rsDoc.getString("PROJECT_ID"));
		udDoc.setUserId(rsDoc.getString("USER_ID"));
		udDoc.setName(rsDoc.getString("DOC_NAME"));
		udDoc.setCatId(rsDoc.getInt("CAT_ID"));
		udDoc.setSize(rsDoc.getInt("DOC_SIZE"));
		udDoc.setUploadDate(rsDoc.getTimestamp("DOC_UPLOAD_DATE"));
		udDoc.setUpdateDate(rsDoc.getTimestamp("DOC_UPDATE_DATE"));
		udDoc.setPublishDate(rsDoc.getTimestamp("DOC_PUBLISH_DATE"));

		udDoc.setDocType(rsDoc.getInt("DOC_TYPE"));
		udDoc.setUpdatedBy(rsDoc.getString("UPDATED_BY"));
		udDoc.setHasPreviousVersion(rsDoc.getString("HAS_PREV_VERSION"));
		udDoc.setIsLatestVersion(rsDoc.getString("LATEST_VERSION"));
		udDoc.setLockFinalFlag(rsDoc.getString("LOCK_FINAL_FLAG"));
		udDoc.setLockedBy(rsDoc.getString("LOCKED_BY"));
		udDoc.setDeleteFlag(rsDoc.getString("DELETE_FLAG"));
		udDoc.setDeletedBy(rsDoc.getString("DELETED_BY"));
		udDoc.setMeetingId(rsDoc.getString("MEETING_ID"));
		udDoc.setIbmOnly(rsDoc.getString("IBM_ONLY"));
		//udDoc.setDocHits(rsDoc.getInt("hits"));

		udDoc.setDPrivate(rsDoc.getString("ISPRIVATE"));
		udDoc.setDPrivateEdit(rsDoc.getString("ISPRIVATE"));

		udDoc.setIBMConfidential(rsDoc.getString("IBM_CONF"));

		if (rsDoc.getTimestamp("EXPIRY_DATE") != null) {
			udDoc.setExpiryDate(rsDoc.getTimestamp("EXPIRY_DATE"));
		} else {
			udDoc.setExpiryDate(0);
		}

		return udDoc;
	}

	public int getAllDocsCountForGrp(
			String strProjectId,
			String strGrpIdString)
	throws SQLException, Exception {
		
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		int iNumberOfDocs = 0;
				
		String strQuery = "select count(distinct d.doc_id) as count" 
			+ " from ETS.ETS_DOC d, ETS.ETS_PRIVATE_DOC p "
			+ " where d.project_id='" 
			+ strProjectId
			+ "' and d.project_id =p.project_id "
			+ " and d.doc_id = p.doc_id "
			+ " and d.issue_id is null and itar_upload_status !='P'"
			+ " and cat_id > 0 and delete_flag !='"
			+ DocConstants.TRUE_FLAG 
			+ "' and d.latest_version='"
			+ Defines.TRUE_FLAG
			+ "' "
			+ "and ((date(d.expiry_date)>date('"
			+ sqlToday
			+ "')) or d.expiry_date=? "
			+ "or d.expiry_date is null) "
			+ " and p.group_id in (" + strGrpIdString + ")" 
			+ " with ur";
				
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		
		PreparedStatement stmtGetDocs = m_pdConnection.prepareStatement(strQuery);

		stmtGetDocs.setTimestamp(1, new Timestamp(0));

		ResultSet rs = stmtGetDocs.executeQuery();

		if (rs.next()) {
			iNumberOfDocs = rs.getInt("count");
		}
		m_pdLog.debug("Total no of Docs using selected grps::" + iNumberOfDocs);
		rs.close();
		stmtGetDocs.close();
		return iNumberOfDocs;
	}

	public boolean chkGrpUsedForDocNotification(
		String strProjectId,
		Vector vtGroups) 
		throws SQLException, Exception {
		
		boolean status = false;
		
		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		int iNumberOfDocs = 0;
		
		String strQuery = "select count(distinct d.doc_id) as count "
			+ "from ETS.ETS_DOC d, ETS.ETS_DOC_NOTIFY n "
			+ "where d.project_id='" 
			+ strProjectId
			+ "' and d.doc_id = n.doc_id " 
			+ " and d.issue_id is null and itar_upload_status !='P'"
			+ " and cat_id > 0 and delete_flag !='"
			+ DocConstants.TRUE_FLAG 
			+ "' and d.latest_version='"
			+ Defines.TRUE_FLAG
			+ "' "
			+ "and ((date(d.expiry_date)>date('"
			+ sqlToday
			+ "')) or d.expiry_date=? "
			+ "or d.expiry_date is null) "
			+ " and n.notify_all_flag = 'N' "
			+ " and n.group_id like ? "
			+ " with ur";
		
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		
		PreparedStatement stmtGetDocs = m_pdConnection.prepareStatement(strQuery);

		for (int iCount=0;iCount< vtGroups.size(); iCount++) {
			
			String strGrpId = ((ETSGroup)vtGroups.elementAt(iCount)).getGroupId();
			stmtGetDocs.clearParameters();
			stmtGetDocs.setTimestamp(1, new Timestamp(0));
			stmtGetDocs.setString(2, "%" + strGrpId + "%");

			ResultSet rs = stmtGetDocs.executeQuery();

			if (rs.next()) {
				iNumberOfDocs = rs.getInt("count");
				if (iNumberOfDocs > 0 ) {
					status = true;
				}
			}
			rs.close();
		}
		m_pdLog.debug("selected grps used for doc notifation::" + status);
		
		stmtGetDocs.close();
		return status;
		
	}
	
	/**
	 * @param strGroupName
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public ETSGroup getGroupDetailsByNameAndProject(
	        String strGroupName, String strProjectId) throws SQLException {
		ETSGroup udGroup = null;

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.GROUPS WHERE PROJECT_ID=? AND NAME = ? WITH UR");
		stmtGroups.setString(1, strProjectId);
		stmtGroups.setString(2, strGroupName);
		ResultSet rsGroups = stmtGroups.executeQuery();

		if (rsGroups.next()) {
			udGroup = getGroup(rsGroups);
		}

		rsGroups.close();
		stmtGroups.close();
		return udGroup;
	}

}
