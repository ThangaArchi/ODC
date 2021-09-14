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

package oem.edge.ets.fe.wspace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSWorkspaceDAO {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.20";


	private static Log logger = EtsLogger.getLogger(ETSWorkspaceDAO.class);
	
	/**
	 * @return
	 */
	public static Vector getMainWorkspaces(Connection con, String sProjType, String sIRId, boolean bAdmin) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vProjects = new Vector();
		
		try {
			if (bAdmin) {
				sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY FROM ETS.ETS_PROJECTS WHERE PARENT_ID='0' AND PROJECT_OR_PROPOSAL = '" + sProjType + "' AND PROJECT_STATUS NOT IN ('A','D') AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' ORDER BY PROJECT_NAME for READ ONLY");
			} else {
				sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY FROM ETS.ETS_PROJECTS WHERE PARENT_ID='0' AND PROJECT_OR_PROPOSAL = '" + sProjType + "' AND PROJECT_STATUS NOT IN ('A','D') AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sIRId + "' AND ACTIVE_FLAG IN ('A')) ORDER BY PROJECT_NAME for READ ONLY");
			}
			
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				ETSProj proj = new ETSProj();
				
				proj.setProjectId(rs.getString("PROJECT_ID"));
				proj.setName(rs.getString("PROJECT_NAME"));
				proj.setCompany(rs.getString("COMPANY"));
				
				vProjects.addElement(proj);
				
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
		
		return vProjects;
		
	}

	/**
	 * @return
	 */
	public static Vector getAllInternalUsersWithEntitlement(Connection con, String sEntitlement) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vUsers = new Vector();
		
		try {
			
			sQuery.append("SELECT A.IR_USERID,LTRIM(RTRIM(A.USER_LNAME)) || ', ' || LTRIM(RTRIM(A.USER_FNAME)),A.USER_EMAIL FROM AMT.USERS A, DECAF.USERS B WHERE A.EDGE_USERID IN (SELECT I.USERID FROM AMT.S_USER_ACCESS_VIEW I WHERE I.ENTITLEMENT LIKE '%" + sEntitlement + "%') AND A.EDGE_USERID = B.USERID AND B.USER_TYPE = 'I' ORDER BY 2 for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				String sID = rs.getString(1);
				String sName = rs.getString(2);
				String sEmail = rs.getString(3);
				
				ETSUser user = new ETSUser();
				
				user.setUserName(sName);
				user.setUserId(sID);
				
				vUsers.addElement(user);
				
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
		
		return vUsers;
		
	}

	/**
	 * @return
	 */
	public static Vector getInternalUsersInWorkspace(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector vUsers = new Vector();
		
		try {
			
			sQuery.append("SELECT A.IR_USERID,LTRIM(RTRIM(A.USER_LNAME)) || ', ' || LTRIM(RTRIM(A.USER_FNAME)),A.USER_EMAIL FROM AMT.USERS A, DECAF.USERS B WHERE A.IR_USERID IN (SELECT P.USER_ID FROM ETS.ETS_USERS P WHERE P.USER_PROJECT_ID = '" + sProjectID + "' AND P.ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND A.EDGE_USERID = B.USERID AND B.USER_TYPE = 'I' ORDER BY 2 for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				String sID = rs.getString(1);
				String sName = rs.getString(2);
				String sEmail = rs.getString(3);
				
				ETSUser user = new ETSUser();
				
				user.setUserName(sName);
				user.setUserId(sID);
				
				vUsers.addElement(user);
				
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
		
		return vUsers;
		
	}

	/**
	 * @return
	 */
	public static String getWorkspaceName(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		String sProjectName = "";
		
		
		try {
			
			sQuery.append("SELECT PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PROJECT_ID='" + sProjectID + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			while (rs.next()) {
				
				sProjectName = rs.getString(1);
				
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
		
		return sProjectName;
		
	}

	/**
	 * @param con
	 * @param sWorkspaceName
	 * @param sWorkspaceDesc
	 * @param sProjectOrProposal
	 * @param sMainWorkspace
	 * @param sWorkspaceCompany
	 * @return
	 */
	public static String createWorkspace(Connection conn, String sWorkspaceName, String sWorkspaceDesc, String sProjectOrProposal, String sMainWorkspace, String sWorkspaceCompany, String sDelivery, String sGeo, String sIndustry, String sITAR, String sWorkspaceType) throws SQLException, Exception {
		
		StringBuffer sQuery = new StringBuffer("");
		String sProjectID = "";
		PreparedStatement stmt = null;
		
		
		try {
			
			String sParentID = "";
			
			if (sMainWorkspace == null || sMainWorkspace.trim().equals("")) {
				sParentID = "0";
			} else {
				sParentID = sMainWorkspace.trim();
			}
			
			sProjectID = generateUniqueProjectID(sWorkspaceCompany);
		
			sQuery.append("INSERT INTO ETS.ETS_PROJECTS (PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,PROJECT_START,PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,LOTUS_PROJECT_ID,RELATED_ID,PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY, INDUSTRY,IS_ITAR, PROJECT_TYPE) VALUES ");
			sQuery.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	
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
			stmt.setString(15,sDelivery);										// delivery team
			stmt.setString(16,sGeo);											// Geography
			stmt.setString(17,sIndustry);										// Industry
			if (sITAR.equalsIgnoreCase("Y")) {
				stmt.setString(18,"Y");											// itar
			} else {
				stmt.setString(18,"N");											// itar
			}
			stmt.setString(19,sWorkspaceType);									// ETS Workspace Type
	
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

	/**
	 * @param conn
	 * @param sProjectID
	 * @return
	 */
	public static boolean createRoles(Connection conn, String sProjectID,String sProjectType,String sLastUserID) throws SQLException, Exception {
		
		Statement stmt = null;
		boolean bSuccess = false;
		
		try {
			
			// create workspace owner role
			
			for (int i = 1; i < 9; i++) {
				
				StringBuffer sQuery = new StringBuffer("");
				
				if (i == 1) {
					sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
					sQuery.append("(select max(role_id) from ets.ets_roles)+1," + i + ",'Workspace Owner',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
				} else {
					sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
					sQuery.append("(select max(role_id) from ets.ets_roles)," + i + ",'Workspace Owner',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
				}
	
				stmt = conn.createStatement();
				
				int iResult = stmt.executeUpdate(sQuery.toString());
				
				ETSDBUtils.close(stmt);
				
			}
			
			
			// create workspace manager role
			
			for (int i = 1; i < 8; i++) {
				
				StringBuffer sQuery = new StringBuffer("");
				
				if (i == 1) {
					sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
					sQuery.append("(select max(role_id) from ets.ets_roles)+1," + i + ",'Workspace Manager',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
				} else {
					sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
					sQuery.append("(select max(role_id) from ets.ets_roles)," + i + ",'Workspace Manager',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
				}
	
				stmt = conn.createStatement();
				
				int iResult = stmt.executeUpdate(sQuery.toString());
				
				ETSDBUtils.close(stmt);
				
			}

			// create memeber role

			StringBuffer sQuery = new StringBuffer("");
				
			sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
			sQuery.append("(select max(role_id) from ets.ets_roles)+1,2,'Member',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
	
			stmt = conn.createStatement();
				
			int iResult = stmt.executeUpdate(sQuery.toString());
				
			ETSDBUtils.close(stmt);
			
			// create client role if workspace being created is client voice.

			if (sProjectType.trim().equalsIgnoreCase("C")) {
				
				sQuery.setLength(0);
					
				sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
				sQuery.append("(select max(role_id) from ets.ets_roles)+1,10,'Client',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
		
				stmt = conn.createStatement();
					
				iResult = stmt.executeUpdate(sQuery.toString());
					
				ETSDBUtils.close(stmt);
			}
			

			// create visitor role

			sQuery.setLength(0);
				
			sQuery.append("INSERT INTO ETS.ETS_ROLES (ROLE_ID,PRIV_ID,ROLE_NAME,PRIV_VALUE,PROJECT_ID,LAST_USERID,LAST_TIMESTAMP) values(");
			sQuery.append("(select max(role_id) from ets.ets_roles)+1,9,'Visitor',1,'" + sProjectID + "','" + sLastUserID + "',current timestamp)");
	
			stmt = conn.createStatement();
				
			iResult = stmt.executeUpdate(sQuery.toString());
				
			ETSDBUtils.close(stmt);

			bSuccess = true;		
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}

	/**
	 * @param conn
	 * @param sProjectID
	 * @return
	 */
	public static boolean createWorkspaceOwner(Connection conn, String sProjectID,String sWorkspaceOwner, String sLastUserID) throws SQLException, Exception {
		
		Statement stmt = null;
		boolean bSuccess = false;
		
		try {
			

			StringBuffer sQuery = new StringBuffer("");
			
			sQuery.append("INSERT INTO ETS.ETS_USERS (USER_ID,USER_PROJECT_ID,USER_ROLE_ID,USER_JOB,PRIMARY_CONTACT,LAST_USERID,LAST_TIMESTAMP,ACTIVE_FLAG) VALUES ");
			sQuery.append("('" + sWorkspaceOwner + "','" + sProjectID + "',(SELECT ROLE_ID FROM ETS.ETS_ROLES WHERE PROJECT_ID='" + sProjectID + "' AND PRIV_ID=8),'Project Manager','Y','" + sLastUserID + "',current timestamp,'" + Defines.USER_ENTITLED + "')");
			
			System.out.println("QUERY : -------> " + sQuery.toString());
			
			stmt = conn.createStatement();
			
			int iResult = stmt.executeUpdate(sQuery.toString());
	
			bSuccess = true;		
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}

	/**
	 * @param conn
	 * @param sProjectID
	 * @return
	 */
	public static boolean createTab(Connection conn, String sProjectID, int iViewType, int iOrder, String sLastUserID) throws SQLException, Exception {
		
		Statement stmt = null;
		boolean bSuccess = false;
		
		try {
			
			StringBuffer sQuery = new StringBuffer("");
			
			sQuery.append("INSERT INTO ETS.ETS_CAT (CAT_ID,CAT_NAME,CAT_TYPE,CAT_DESCRIPTION,PARENT_ID,PROJECT_ID,ORDER,VIEW_TYPE,IBM_ONLY,USER_ID,LAST_TIMESTAMP,PRIVS) VALUES (");
			
			if (iViewType == Defines.MAIN_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Main',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.MEETINGS_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Meetings',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.DOCUMENTS_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Documents',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.ISSUES_CHANGES_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Issues/changes',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			}else if (iViewType == Defines.WorkFlow_Maintab) {//Code added for workflow by Ryazuddin on 11/10/2006
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'WorkFlow main',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			}else if (iViewType == Defines.WorkFlow_Assessment) {//Code added for workflow by Ryazuddin on 11/10/2006
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Assessment',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.TEAM_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Team',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.CONTRACTS_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Contracts',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.SETMET_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Set/Met Reviews',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.ABOUT_SETMET_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'About Set/Met',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.FEEDBACK_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Feedback',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.SELF_ASSESSMENT_VT) {
				// set privilages as ibm only
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Self Assessment',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'" + Defines.IBM_ONLY + "')");
			} else if (iViewType == Defines.ASIC_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'ASIC',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			} else if (iViewType == Defines.SURVEY_VT) {
				sQuery.append("(SELECT MAX(CAT_ID) FROM ETS.ETS_CAT)+1,'Survey',0,'',0,'" + sProjectID + "'," + iOrder + "," + iViewType + ",'0','" + sLastUserID + "',current timestamp,'')");
			}
				
			stmt = conn.createStatement();
			
			int iResult = stmt.executeUpdate(sQuery.toString());
	
			bSuccess = true;		
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}

	/**
	 * @param conn
	 * @param sProjectID
	 * @return
	 */
	public static boolean updateTabOrderType(Connection conn, String sProjectID, int iCatId, int iOrder, String sLastUserID) throws SQLException, Exception {
		
		Statement stmt = null;
		boolean bSuccess = false;
		
		try {
			
			StringBuffer sQuery = new StringBuffer("");
			
			sQuery.append("UPDATE ETS.ETS_CAT SET ORDER = " + iOrder + ",LAST_TIMESTAMP=current timestamp WHERE PROJECT_ID = '" + sProjectID + "' AND CAT_ID = " + iCatId);
				
			stmt = conn.createStatement();
			
			int iResult = stmt.executeUpdate(sQuery.toString());
	
			bSuccess = true;		
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}

	/**
	 * @param conn
	 * @param sProjectID
	 * @return
	 */
	public static boolean deleteTab(Connection conn, String sProjectID, int iCatId) throws SQLException, Exception {
		
		Statement stmt = null;
		boolean bSuccess = false;
		
		try {
			
			StringBuffer sQuery = new StringBuffer("");
			
			sQuery.append("DELETE FROM ETS.ETS_CAT WHERE PROJECT_ID = '" + sProjectID + "' AND CAT_ID = " + iCatId);
				
			stmt = conn.createStatement();
			
			int iResult = stmt.executeUpdate(sQuery.toString());
	
			bSuccess = true;		
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
	}
	
	public static synchronized String generateUniqueProjectID(String sCompany) throws SQLException, Exception {

		String sUniqueId = "";
		String sTemp = "";
		String sPrefix = "";
		
		Long lDate = new Long(System.currentTimeMillis());

		//sUniqueId = sPrefix + "-" + lDate;
		sUniqueId = lDate.toString();

		return sUniqueId;
	}

	/**
	 * @param con
	 * @param proj
	 * @param string
	 * @return
	 */
	public static int getWorkspaceOwnerRoleID(Connection con, String sProjectID, String sUserID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iRoleID = 0;
		
		try {
			
			sQuery.append("SELECT USER_ROLE_ID FROM ETS.ETS_USERS WHERE USER_PROJECT_ID = '" + sProjectID + "' AND USER_ID = '" + sUserID + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				iRoleID = rs.getInt("USER_ROLE_ID");
				
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
		
		return iRoleID;
	}

	/**
	 * @param con
	 * @param string
	 * @param sWorkspaceOwner
	 * @param iWorkspaceOwnerRoleID
	 * @return
	 */
	public static boolean assignNewWorkspaceOwner(Connection con, String sProjectID, String sWorkspaceOwner, int iWorkspaceOwnerRoleID) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_USERS SET USER_ROLE_ID = " + iWorkspaceOwnerRoleID + " WHERE USER_ID = '" + sWorkspaceOwner + "' AND USER_PROJECT_ID = '" + sProjectID + "'");
			
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

	/**
	 * @param con
	 * @param string
	 * @param sWorkspaceOwner
	 * @param roleid
	 * @return
	 */
	public static boolean assignRoleToOldWorkspaceOwner(Connection con, String sProjectID, String sOldWorkspaceOwner, int roleid) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_USERS SET USER_ROLE_ID = " + roleid + " WHERE USER_ID = '" + sOldWorkspaceOwner + "' AND USER_PROJECT_ID = '" + sProjectID + "'");
			
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

	/**
	 * @param con
	 * @param string
	 * @return
	 */
	public static boolean updateWorkspaceStatus(Connection con, String sProjectID, String sFlag) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET PROJECT_STATUS = '" + sFlag + "' WHERE PROJECT_ID = '" + sProjectID + "'");
			
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

	/**
	 * @param con
	 * @param string
	 * @return
	 */
	public static boolean archiveWorkspaceRoles(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		Statement stmt1 = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			// this will update the workspace owner, manager and member
			sQuery.append("UPDATE ETS.ETS_ROLES SET PRIV_ID = 9 WHERE PRIV_ID = 2 AND PROJECT_ID = '" + sProjectID + "'");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
			
			if (iCount > 0) {
				// delete off the other privilages if there are any
				sQuery.setLength(0);
				sQuery.append("DELETE FROM ETS.ETS_ROLES WHERE PRIV_ID IN (1,3,4,5,6,7,8) AND PROJECT_ID = '" + sProjectID + "'");
			
				stmt1 = con.createStatement();
				iCount = stmt1.executeUpdate(sQuery.toString());
				
				if (iCount > 0) {
					bSuccess = true;
				}
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

	/**
	 * @param con
	 * @param string
	 * @return
	 */
	public static int checkSubWorkspaces(Connection con, String sProjectId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iCount = 0;
		
		try {
			
			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PARENT_ID = '" + sProjectId + "' AND PROJECT_STATUS NOT IN ('" + Defines.WORKSPACE_DELETE + "') for READ ONLY");
			//sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PARENT_ID = '" + sProjectId + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				iCount = rs.getInt(1);
				
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
		
		return iCount;
	}

	/**
	 * @param con
	 * @param string
	 * @param sWorkspaceName
	 */
	public static boolean updateProposalToProject(Connection con, String sProjectID, String sWorkspaceName) throws SQLException, Exception {
		
		PreparedStatement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;

		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET PROJECT_NAME = ?,PROJECT_OR_PROPOSAL = ? WHERE PROJECT_ID = ?");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,ETSUtils.escapeString(sWorkspaceName));
			stmt.setString(2,"P");
			stmt.setString(3,sProjectID);
			
			iCount = stmt.executeUpdate();
			
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
	
	public static boolean isSubWorkspace(Connection con, String sParentProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iCount = 0;
		boolean bSub = false;
		
		try {
			
			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = '" + sParentProjectID + "' for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				iCount = rs.getInt(1);
			}
			
			if (iCount > 0) {
				bSub = true;
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

	/**
	 * @param con
	 * @param string
	 */
	public static boolean deleteSubWorkspaces(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET PROJECT_STATUS = 'D' WHERE PARENT_ID = '" + sProjectID + "'");
			
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

	/**
	 * @param con
	 * @param string
	 */
	public static boolean deleteContractsForProject(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			/**
			 * 1. Delete the documents if there are any for the meetings
			 * 2. Delete the meetings.
			 */
			
			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID IN (SELECT DOC_ID FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT CHAR(TASK_ID) FROM ETS.ETS_TASK_MAIN WHERE PROJECT_ID = '" + sProjectID + "'))");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());

			sQuery.setLength(0);
			ETSDBUtils.close(stmt);
				
			// delete the doc info from doc table...
			sQuery.append("DELETE FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT CHAR(TASK_ID) FROM ETS.ETS_TASK_MAIN WHERE PROJECT_ID = '" + sProjectID + "')");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());

			sQuery.setLength(0);
			ETSDBUtils.close(stmt);
				
			// delete the TASK COMMENTS 
			sQuery.append("DELETE FROM ETS.ETS_TASK_COMMENTS WHERE TASK_ID IN (SELECT TASK_ID FROM ETS.ETS_TASK_MAIN WHERE PROJECT_ID = '" + sProjectID + "')");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
			

			sQuery.setLength(0);
			ETSDBUtils.close(stmt);
				
			// delete the TASKS 
			sQuery.append("DELETE FROM ETS.ETS_TASK_MAIN WHERE PROJECT_ID = '" + sProjectID + "'");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
			
			bSuccess = true;
		
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

	/**
	 * @param con
	 * @param string
	 */
	public static boolean deleteMeetingsForProject(Connection con, String sProjectID) throws SQLException, Exception {
		
		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			/**
			 * 1. Delete the documents if there are any for the meetings
			 * 2. Delete the meetings.
			 */
			
			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID IN (SELECT DOC_ID FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT CALENDAR_ID FROM ETS.ETS_CALENDAR WHERE PROJECT_ID = '" + sProjectID + "' AND CALENDAR_TYPE='M'))");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
	
			sQuery.setLength(0);
			ETSDBUtils.close(stmt);

			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID IN (SELECT DOC_ID FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT REPEAT_ID FROM ETS.ETS_CALENDAR WHERE PROJECT_ID = '" + sProjectID + "' AND CALENDAR_TYPE='M' AND REPEAT_TYPE !='N'))");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
	
			sQuery.setLength(0);
			ETSDBUtils.close(stmt);

			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT CALENDAR_ID FROM ETS.ETS_CALENDAR WHERE PROJECT_ID = '" + sProjectID + "' AND CALENDAR_TYPE='M')");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
	
			sQuery.setLength(0);
			ETSDBUtils.close(stmt);
				

			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_DOC WHERE PROJECT_ID = '" + sProjectID + "' AND MEETING_ID IN (SELECT REPEAT_ID FROM ETS.ETS_CALENDAR WHERE PROJECT_ID = '" + sProjectID + "' AND CALENDAR_TYPE='M' AND REPEAT_TYPE !='N')");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
	
			sQuery.setLength(0);
			ETSDBUtils.close(stmt);

			// delete the documents...
			sQuery.append("DELETE FROM ETS.ETS_CALENDAR WHERE PROJECT_ID = '" + sProjectID + "' AND CALENDAR_TYPE='M')");
			
			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());
	
			sQuery.setLength(0);
			ETSDBUtils.close(stmt);
			
			bSuccess = true;
		
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

	public static boolean doesUserHaveEntitlement(Connection con, String sUserId, String sEntitlement) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iCount = 0;
		boolean bSub = false;
		
		try {
			boolean isIBMer = ETSUtils.isIBMer(sUserId, con);
			// 6.1 Internal users are not required to have Sales_collab entitlement
			if (isIBMer && sEntitlement.equalsIgnoreCase(Defines.COLLAB_CENTER_ENTITLEMENT)) {
				bSub = true;
			} else {

				sQuery.append("SELECT COUNT(ENTITLEMENT) FROM AMT.S_USER_ACCESS_VIEW WHERE USERID IN (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='" + sUserId + "') AND ENTITLEMENT = '" + sEntitlement + "' WITH UR");
			
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
			
				if (rs.next()) {
					iCount = rs.getInt(1);
				}
			
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

	/**
	 * @param con
	 * @param sProjectId
	 * @param sUserId
	 * @return
	 */
	public static boolean insertWorkspaceIntoPreference(Connection conn, String sProjectId, String sUserId) throws SQLException, Exception {
		
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement stmt = null;
		boolean bSuccess = false;
		
		try {
			
			sQuery.append("INSERT INTO ETS.ETS_USER_WS (USER_ID,PROJECT_ID) VALUES ");
			sQuery.append("(?,?)");
	
			stmt = conn.prepareStatement(sQuery.toString());
	
			stmt.setString(1,sUserId);										
			stmt.setString(2,sProjectId);									

			int iResult = stmt.executeUpdate();
			
			if (iResult > 0) {
				bSuccess = true;
			}
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}
		
		return bSuccess;
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
			stmt.setString(2, Defines.ETS_WORKSPACE_TYPE);
			
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


	public static boolean isMemberOfWorkspace(Connection con, String sProjectId, String sUserId) throws SQLException, Exception {
		
		boolean bIsMember = false;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		
		try {

			sQuery.append("SELECT USER_ROLE_ID FROM ETS.ETS_USERS WHERE USER_ID = ? AND USER_PROJECT_ID = ? AND ACTIVE_FLAG = ? for READ ONLY");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,sUserId);
			stmt.setString(2,sProjectId);
			stmt.setString(3, Defines.USER_ENTITLED);
			
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				bIsMember = true;
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
		
		
		return bIsMember;
				
	}

	/**
	 * @return
	 */
	public static Vector getPMOID(Connection con, String RPMProjectCode) throws SQLException, Exception {
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		Vector listCode = new Vector();
		
		try {
			
			sQuery.append("SELECT DISTINCT PMO_ID FROM ETS.ETS_PMO_MAIN WHERE RPM_PROJECT_CODE = ? for READ ONLY");
			
			stmt = con.prepareStatement(sQuery.toString());
			stmt.setString(1,RPMProjectCode);
			
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				String sID = rs.getString(1);
				listCode.addElement(sID);
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
		
		return listCode;
		
	}

	/**
	 * @param con
	 * @param string
	 * @return
	 */
	public static boolean updateLotusID(Connection con, String project_id, String lotus_id, String pmo_id) throws SQLException, Exception {
		
		PreparedStatement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET LOTUS_PROJECT_ID = ?, PMO_PROJECT_ID = ? WHERE PROJECT_ID = ?");
			
			stmt = con.prepareStatement(sQuery.toString());
			
			stmt.setString(1,lotus_id);
			stmt.setString(2,pmo_id);
			stmt.setString(3,project_id);
			
			iCount = stmt.executeUpdate();
			
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

	/**
	 * @param con
	 * @param string
	 * @return
	 */
	public static boolean updateIssuesWithRPM(Connection con, ETSProj proj, String proj_owner_id, String last_user_id) throws SQLException, Exception {
		
		boolean bSuccess = false;
		
		try {
			
			EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

			String dataId = EtsIssFilterUtils.getUniqRefNoStr("DT");

			EtsIssOwnerInfo ownerInfoObj = new EtsIssOwnerInfo();

			String ownerEdgeId="";
			String ownerEmail="";

			String owner_edge_id = ETSUtils.getUserEdgeIdFromAMT(con,proj_owner_id);
			ownerInfoObj.setUserEdgeId(owner_edge_id);
			ownerInfoObj.setUserEmail(ETSUtils.getUserEmail(con,proj_owner_id));

			String lastUserId = last_user_id;

			dropModel.setDataId(dataId);
			dropModel.setProjectId(proj.getProjectId());
			dropModel.setProjectName(proj.getName());
			dropModel.setIssueClass(EtsIssFilterConstants.ETSISSUESUBTYPE);
			dropModel.setIssueType("RPM");
			dropModel.setSubTypeA("");
			dropModel.setSubTypeB("");
			dropModel.setSubTypeC("");
			dropModel.setSubTypeD("");
			dropModel.setIssueSource(EtsIssueConstants.ETSPMOSOURCE);
			dropModel.setIssueAccess("ALL:IBM");
			dropModel.setIssueEtsR1("");
			dropModel.setIssueEtsR2("");
			dropModel.setActiveFlag("Y");
			dropModel.setLastUserId(lastUserId);
			dropModel.setOwnerInfo(ownerInfoObj);

			EtsDropDownDAO issueDAO = new EtsDropDownDAO();
			bSuccess = issueDAO.addIssueType(dropModel);
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return bSuccess;
	}

	/**
	 * @return
	 */
	public static boolean isPMOProjectIDAlreadyAssigned(Connection con, String pmo_project_id) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean assigned = false;
		
		try {
			
			sQuery.append("SELECT PROJECT_ID FROM ETS.ETS_PROJECTS WHERE pmo_project_id = '" + pmo_project_id + "' AND PROJECT_STATUS NOT IN ('" + Defines.WORKSPACE_DELETE + "') for READ ONLY");
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());
			
			if (rs.next()) {
				assigned = true;
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
		
		return assigned;
		
	}

	/**
	 * @param con
	 * @param sProjectID
	 * @param workspaceName
	 * @return boolean
	 * @throws SQLException
	 * @throws Exception
	 */

	public static boolean updateWorkspaceName(Connection con, String sProjectID, String workspaceName, String workspaceDesc) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET PROJECT_NAME = '" + workspaceName+ "', PROJECT_DESCRIPTION = '" + workspaceDesc + "' WHERE PROJECT_ID = '" + sProjectID + "'");
			
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

	/**
	 * @param con
	 * @param sProjectID
	 * @param workspaceName
	 * @return boolean
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean updateGeoAndOther(Connection con, String sProjectID, String geography, String delteam, String industry) throws SQLException, Exception {
		
		Statement stmt = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		boolean bSuccess = false;
		int iCount = 0;
		
		try {
			
			sQuery.append("UPDATE ETS.ETS_PROJECTS SET GEOGRAPHY = '" + geography+ "', DELIVERY_TEAM = '" + delteam + "', INDUSTRY = '" + industry + "' WHERE PROJECT_ID = '" + sProjectID + "'");
			
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

}
