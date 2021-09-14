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
package oem.edge.ets.fe.aic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.ArrayList;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDetailedObj;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;

import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

import oem.edge.decaf.DecafCreateEnt;
import oem.edge.amt.AMTException;
import oem.edge.amt.EntitledStatic;


/**
 * @author vishal
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICWorkspaceDAO  extends ETSWorkspaceDAO{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";

	private static final String CLASS_VERSION = "1.4";
	
	/**
	 * @param con
	 * @param sWorkspaceName
	 * @param sWorkspaceDesc
	 * @param sProjectOrProposal
	 * @param sMainWorkspace
	 * @param sWorkspaceCompany
	 * @return
	 */
	private static String ESSENTIAL_LINKS = "E";

	public static String createAICWorkspace(Connection conn, String sWorkspaceName, String sWorkspaceDesc, String sProjectOrProposal, String sMainWorkspace, String sWorkspaceCompany, String sSector, String sTechnology, String sBrand, String sProcess, String sITAR, String sSubSector, String sWSCategory, String sIbmOnly) throws SQLException, Exception {

		StringBuffer sQuery = new StringBuffer("");
		String sProjectID = "";
		PreparedStatement stmt = null;

		System.out.println("test log-  inside the insert code of DAO for the creation of WS\n");

		try {

			String sParentID = "";

			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sParentID = "0";
			} else {
				sParentID = sMainWorkspace.trim();
			}

			sProjectID = generateUniqueProjectID(sWorkspaceCompany);

			sQuery.append("INSERT INTO ETS.ETS_PROJECTS (PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START,PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID,PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY, INDUSTRY,IS_ITAR, PROJECT_TYPE,BRAND,SCE_SECTOR,SECTOR,PROCESS,SUB_SECTOR,IBM_ONLY,IS_PRIVATE) VALUES ");
			sQuery.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			stmt = conn.prepareStatement(sQuery.toString());

			stmt.setString(1,sProjectID);										// project id
			stmt.setString(2,ETSUtils.escapeString(sWorkspaceDesc));			// project description
			stmt.setString(3,ETSUtils.escapeString(sWorkspaceName));			// project name
			stmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));		// project start date
			stmt.setTimestamp(5,Timestamp.valueOf("2999-12-31 00:00:00.000000000")); //	project end date
			stmt.setString(6,"");												// decaf project name
			stmt.setString(7,sProjectOrProposal);								// project or proposal
			stmt.setString(8,"");												// lotus project id
			stmt.setString(9,"");												// related id
			stmt.setString(10,sParentID);										// parent id
			stmt.setString(11,sWorkspaceCompany);								// company
			stmt.setString(12,"");												// pmo project id
			stmt.setString(13,"Y");												// show issue owner
			stmt.setString(14,"N");												// project status
			stmt.setString(15,"");										// delivery team
			stmt.setString(16,"");											// Geography
			stmt.setString(17,"");										// Industry
			if (sITAR.equalsIgnoreCase("Y")) {
				stmt.setString(18,"Y");											// itar
			} else {
				stmt.setString(18,"N");											// itar
			}
			stmt.setString(19,"AIC"); 											// AIC project
			stmt.setString(20,sBrand); 											// brand type
			stmt.setString(21,sTechnology); 									// technology
			stmt.setString(22,sSector); 										// sector
			stmt.setString(23,sProcess); 										// Process
			stmt.setString(24,sSubSector); 										// Sub Sector
 
			if (sIbmOnly.equalsIgnoreCase("Y")) {								// ibm_only
				stmt.setString(25,"Y");
			} else {
				stmt.setString(25,"N");
			}										
			if (sWSCategory.equalsIgnoreCase("PUBLIC-WORKSPACE")) {
				stmt.setString(26,"A"); 											
			} else if (sWSCategory.equalsIgnoreCase("PRIVATE-WORKSPACE")) {
				stmt.setString(26,"P"); 											// private WS
			} else if (sWSCategory.equalsIgnoreCase("RESTRICTED-WORKSPACE")) {
				stmt.setString(26,"R"); 											// private WS
			} else if (sWSCategory.equalsIgnoreCase("RESTRICTED-TEAMROOM")) {
				stmt.setString(26,"1"); 											// private WS
			} else if (sWSCategory.equalsIgnoreCase("PRIVATE-TEAMROOM")) {
				stmt.setString(26,"2"); 											// private WS
			}else if (sWSCategory.equalsIgnoreCase("WORKFLOW-WORKSPACE")) {
				stmt.setString(26,"A"); 											// private WS
			}//added for Workflow by Ryazuddin on 11/10/2006


			int iResult = stmt.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}

		return sProjectID;
	}
	// populate the madatory links - if none

	public static boolean populateLinksFirstTime(Connection conn, String sProjectId, String sUserId){
		
		boolean hasLinks = false;
		String qry = "select count(*) from ets.quick_links where project_id = '"+sProjectId+"'";
		try {
			String cnt = EntitledStatic.getValue(conn, qry);
			if (Integer.parseInt(cnt)>0) {hasLinks = true;} else { hasLinks = false;}
		}catch (AMTException amtEx){
		}catch (SQLException sqlEx){
		}
		
		if (!hasLinks)
		{
			String qryInsert = "insert into ets.quick_links (project_id, link_id, link_name, link_url, link_type, link_seq, user_doc, display, popup, last_user, last_timestamp) "+
				" select '"+sProjectId+"',link_id, link_name, link_url, link_type, link_seq, user_doc, display, popup, '"+sUserId+"', (current timestamp) from ets.quick_links where project_id = 'AICALL' and link_type like '"+ESSENTIAL_LINKS+"%'";
			try{
				EntitledStatic.safeInsert(conn, qryInsert);
			} catch (SQLException sqlEx){
			}
		}
		return true;
		
	}

	public static boolean updateWorkspaceAccessType(Connection con, String sProjectID, String sFlag) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET IS_PRIVATE = '" + sFlag + "' WHERE PROJECT_ID = '" + sProjectID + "'");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
			
			if (iCount > 0) {
				bSuccess = true;	
			} 
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}

	public static boolean doesUserHaveSalesCollabEntitlement(Connection con, String sUserId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iCount = 0;
		boolean bSub = false;
		
		try {
			boolean isIBMer = ETSUtils.isIBMer(sUserId, con);
			// 6.1 Internal users are not required to have Sales_collab entitlement
			if (isIBMer ) {
				bSub = true;
			} else {
				sQuery.append("SELECT COUNT(ENTITLEMENT) FROM AMT.S_USER_ACCESS_VIEW WHERE USERID IN (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='" + sUserId + "') AND ENTITLEMENT in ('" +Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT +"','" + Defines.COLLAB_CENTER_ENTITLEMENT + "','" + Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT + "','" + Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT + "','" + Defines.COLLAB_CENTER_SALES_ENTITLEMENT + "') WITH UR");
			
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
			
				if (rs.next()) {
					iCount = rs.getInt(1);
				}
				System.out.println("user has got sales Entitlements --" + iCount);
				System.out.println("Entitlements query  " + sQuery);
				if (iCount > 0) {
					bSub = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return bSub;
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

	protected static Vector getAicUserProjects(String userid, Connection conn) throws SQLException {
		PreparedStatement getProjectsSt = conn.prepareStatement("select p.* from ETS.ETS_PROJECTS p, ETS.ETS_USERS u where u.user_id = ? and u.active_flag='A' and p.project_type='AIC' and u.user_project_id = p.project_id and p.project_or_proposal !='M' and p.project_status not in ('A','D') order by p.project_name with ur");

		getProjectsSt.setString(1, userid);
		ResultSet rs = getProjectsSt.executeQuery();

		Vector projects = null;
		projects = getProjs(rs);

		getProjectsSt.close();
		return projects;
	}

	/**
	 * @return
	 */
	public static boolean checkIfWorkspaceNameExists(Connection con, String sProjName) throws SQLException, Exception {
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bExists = false;
		
		try {

			sQuery.append("SELECT COUNT(PROJECT_NAME) FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_NAME) = ? AND PROJECT_TYPE = ? for READ ONLY");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,sProjName.toUpperCase());
			stmt.setString(2, Defines.AIC_WORKSPACE_TYPE);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					bExists = true;
				}
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return bExists;
		
	}


	public static String isProjectIBMOnly(String strProjectId, Connection con)
	throws SQLException, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		String isProjIBMonly = "";
		try {

			sQuery.append("SELECT IBM_ONLY FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ? for READ ONLY");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,strProjectId);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				isProjIBMonly = rs.getString(1).trim();
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return isProjIBMonly;
		
		
		
	}

	public static boolean updateWorkspaceToExternalUsers(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET IBM_ONLY = 'N' WHERE PROJECT_ID = '" + sProjectID + "'");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
			
			if (iCount > 0) {
				bSuccess = true;	
			} 
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}
	
	public static boolean setDefaultAICDecafEntitlementExtUsers(Connection conn, String sEdgeId) 
	{
		boolean bInsertEnt = false;
		ArrayList  entProjList = new ArrayList();
      
		try{
		   	String role_id=EntitledStatic.getValue(conn,"select roles_id from decaf.roles " +
				  "where roles_name='" + Defines.AIC_ENTITLEMENT + "' for read only");
			
		   	entProjList.add(role_id);
         
		   	DecafCreateEnt crtent= new DecafCreateEnt();
		   	crtent.setUserid(sEdgeId);
		   	crtent.setIsEnt(true);
		   	crtent.setEntprojList(entProjList);
		   	int i = crtent.insertEntProj(conn);
		   	System.out.println("i===="+i);
           	if (i==1) {
				bInsertEnt = true;
           	}
		}
		catch(Exception e){
			  e.printStackTrace();
         
		}

		
		return bInsertEnt;
	}
	
	public static boolean syncTeamRoomRoles(Connection con, String strProjectId, String strProjectName, String strCompany) throws SQLException {
		boolean status = false;
		Statement stmt = null;
		String strOwnerProfile = strCompany + " BPS OWNER " + strProjectName;
		String strAuthorProfile = strCompany + " BPS AUTHOR " + strProjectName;
		String strReaderProfile = strCompany + " BPS READER " + strProjectName;
		
		System.out.println("Creating/Sync Teamroom roles");
		// create workspace owner role
		
		StringBuffer sQuery = new StringBuffer("");
			sQuery.append("INSERT INTO ETS.WS_DECAF_MAPPING ");
			sQuery.append("(PROJECT_ID,DATATYPE_NAME,ENTITLEMENT_NAME,ROLE_ID,PROFILE_ID,PROFILE_NAME) values(");
			sQuery.append("'" + strProjectId + "','"+strProjectName+"','"+Defines.BPSOWNER_ENT+"',");
			sQuery.append("(select distinct role_id from ets.ets_roles where project_id='"+strProjectId + "' and role_name ='Workspace Owner'),");
			sQuery.append("'"+ strOwnerProfile + "','"+strOwnerProfile+"')");

		System.out.println("owner query" + sQuery);
		stmt = con.createStatement();
		int iResult = stmt.executeUpdate(sQuery.toString());		
		ETSDBUtils.close(stmt);

		// create workspace manager role
		
		sQuery.setLength(0);
			sQuery.append("INSERT INTO ETS.WS_DECAF_MAPPING ");
			sQuery.append("(PROJECT_ID,DATATYPE_NAME,ENTITLEMENT_NAME,ROLE_ID,PROFILE_ID,PROFILE_NAME) values(");
			sQuery.append("'" + strProjectId + "','"+strProjectName+"','"+Defines.BPSOWNER_ENT+"',");
			sQuery.append("(select distinct role_id from ets.ets_roles where project_id='"+strProjectId + "' and role_name ='Workspace Manager'),");
			sQuery.append("'"+ strOwnerProfile + "','"+strOwnerProfile+"')");

		System.out.println("Manager Query" + sQuery);
		stmt = con.createStatement();
		iResult = stmt.executeUpdate(sQuery.toString());		
		ETSDBUtils.close(stmt);

		// create member role
		
		sQuery.setLength(0);
			sQuery.append("INSERT INTO ETS.WS_DECAF_MAPPING ");
			sQuery.append("(PROJECT_ID,DATATYPE_NAME,ENTITLEMENT_NAME,ROLE_ID,PROFILE_ID,PROFILE_NAME) values(");
			sQuery.append("'" + strProjectId + "','"+strProjectName+"','"+Defines.BPSAUTHOR_ENT+"',");
			sQuery.append("(select distinct role_id from ets.ets_roles where project_id='"+strProjectId + "' and role_name ='Member'),");
			sQuery.append("'"+ strAuthorProfile + "','"+strAuthorProfile+"')");

		System.out.println("Member Query" + sQuery);
		stmt = con.createStatement();
		iResult = stmt.executeUpdate(sQuery.toString());		
		ETSDBUtils.close(stmt);
		
		// create visitor role
		
		sQuery.setLength(0);
			sQuery.append("INSERT INTO ETS.WS_DECAF_MAPPING ");
			sQuery.append("(PROJECT_ID,DATATYPE_NAME,ENTITLEMENT_NAME,ROLE_ID,PROFILE_ID,PROFILE_NAME) values(");
			sQuery.append("'" + strProjectId + "','"+strProjectName+"','"+Defines.BPSREADER_ENT+"',");
			sQuery.append("(select distinct role_id from ets.ets_roles where project_id='"+strProjectId + "' and role_name ='Visitor'),");
			sQuery.append("'"+ strReaderProfile + "','"+strReaderProfile+"')");
			
		System.out.println("Visitor Query" + sQuery);
		stmt = con.createStatement();
		iResult = stmt.executeUpdate(sQuery.toString());		
		ETSDBUtils.close(stmt);

		return true;
	}
	// change workspace owner role to pending for a newly created workspace for Teamrooms
	
	public static boolean updateWrkSpcOwnerStatus(Connection conn, String strProjectId, String irUserId, String strUserStatus) 
	throws SQLException{

		String sQuery =" update ets.ets_users set active_flag='"+strUserStatus + "' where user_project_id='" + strProjectId +"' and user_id='"+ irUserId +"'";
		
		System.out.println("update WO query::" + sQuery);
		Statement stmt = conn.createStatement();
		int iResult = stmt.executeUpdate(sQuery);
		ETSDBUtils.close(stmt);
		
		return true;
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
		populateUserNames(admins,conn);
		if (admins.size() > 0)
			return admins;
		else
			return null;

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

	public static synchronized String[] editPrimaryContact(String projectid, String userid, String updater, Connection conn) throws SQLException {

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
		} else if (rowCount == 0)
			return new String[] { "1", "unknown db2 problem" };
		else
			return new String[] { String.valueOf(rowCount), "unknown db2 problem" };

	}

	/**
	 * @param udObj
	 */
	private static void populateUserName(ETSDetailedObj udObj,Connection conn) {
		try {
			udObj.setUserName(
				ETSUtils.getUsersName(conn, udObj.getUserId()));
		} catch (Exception e) {
			udObj.setUserName(udObj.getUserId());
		}
	}

	/**
	 * @param vtObjects
	 * @return
	 */
	private static Vector populateUserNames(Vector vtObjects, Connection conn) {
		Vector vtPopulatedObjects = new Vector();
		for (int iCounter = 0; iCounter < vtObjects.size(); iCounter++) {
			ETSDetailedObj udObj = (ETSDetailedObj) vtObjects.get(iCounter);
			populateUserName(udObj,conn);
			vtPopulatedObjects.add(udObj);
		}

		return vtPopulatedObjects;
	}


}
