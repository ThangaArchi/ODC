package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.RemoveMembrModel;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterRepTabBean;
import oem.edge.ets.fe.ismgt.model.EtsIssLogActionDetails;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.RemindIssueActionModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.ismgt.resources.RemindIssueActionConstants;

import org.apache.commons.logging.Log;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class IssueInfoDAO implements EtsIssFilterConstants, EtsIssueConstants, RemindIssueActionConstants {

	public static final String VERSION = "1.31.1.26";

	private static Log logger = EtsLogger.getLogger(IssueInfoDAO.class);

	/**
	 * Constructor for IssueInfoDAO.
	 */
	public IssueInfoDAO() {
		super();
	}

	/**
	 * This method will query the tables CQ.PROBLEM_INFO_USR1 and takes
	 * the comments,last_action_date,submitter names,last_user from the table
	 * to prepare the log commentary
	 * 
	 */

	public EtsIssLogActionDetails getIssueLogsObj(String edgeProblemId) {

		EtsIssLogActionDetails issLog = new EtsIssLogActionDetails();

		StringBuffer sb = new StringBuffer();
		DbConnect db = null;

		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			issLog = getIssueLogsObj(db.conn, edgeProblemId);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getIssueLogsList", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return issLog;
	}

	/**
	 * This method will query the tables CQ.PROBLEM_INFO_USR1 and takes
	 * the comments,last_action_date,submitter names,last_user from the table
	 * to prepare the log commentary
	 * 
	 */

	public EtsIssLogActionDetails getIssueLogsObj(Connection conn, String edgeProblemId) {

		ArrayList issueLogList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		//DbConnect db = null;
		EtsIssLogActionDetails issLog = new EtsIssLogActionDetails();

		try {

			sb.append("select ");
			sb.append(" a.comm_log from " + ISMGTSCHEMA + ".PROBLEM_INFO_CQ2 a");
			sb.append(" where ");
			sb.append(" a.application_id='" + ETSAPPLNID + "' ");
			sb.append(" and a.edge_problem_id='" + edgeProblemId + "' ");
			sb.append(" with ur ");

			SysLog.log(SysLog.DEBUG, "getIssueLogsClob qry ", "getIssueLogsClob qry =" + sb.toString() + ":");

			String issueCommntsStr = EtsIssFilterUtils.getTrimStr(AmtCommonUtils.getClobValue(conn, sb.toString()));

			issLog.setIssueCommentsLog(issueCommntsStr);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getIssueLogsList", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		}

		return issLog;
	}

	////
	/**
	 * This method will query the tables "+ISMGTSCHEMA+".ETS_OWNER_CQ and takes
	 * owner_id,owner_email from the table
	 * to prepare the owner info
	 */

	public ArrayList getProbOwnerList(String edgeProblemId) throws SQLException, Exception {

		ArrayList ownerInfoList = new ArrayList();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();
			ownerInfoList = getProbOwnerList(conn, edgeProblemId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return ownerInfoList;
	}

	/**
		 * This method will query the tables "+ISMGTSCHEMA+".ETS_OWNER_CQ and takes
		 * owner_id,owner_email from the table
		 * to prepare the owner info
		 */

	public ArrayList getProbOwnerList(Connection conn, String edgeProblemId) throws SQLException, Exception {

		ArrayList ownerInfoList = new ArrayList();

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		try {

			sb.append("select ");
			sb.append(" a.OWNER_ID as ownerid,");
			sb.append(" a.OWNER_EMAIL as owneremail ");
			sb.append(" from");
			sb.append(" " + ISMGTSCHEMA + ".ETS_OWNER_CQ a");
			sb.append(" where ");
			sb.append(" a.edge_problem_id='" + edgeProblemId + "' ");
			sb.append(" for read only ");

			SysLog.log(SysLog.DEBUG, "getOwnerId info ", "getOwnerId info qry =" + sb.toString() + ":");

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
					etsOwnerInfo.setUserEdgeId(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERID")));
					etsOwnerInfo.setUserEmail(EtsIssFilterUtils.getTrimStr(rs.getString("OWNEREMAIL")));

					ownerInfoList.add(etsOwnerInfo);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return ownerInfoList;
	}

	/**
		 * This method will query the tables "+ISMGTSCHEMA+".problem_info_usr1 and takes
		 * ets cc list from the table
		  */

	public String getEtsCCList(String edgeProblemId) throws SQLException, Exception {

		ArrayList ownerInfoList = new ArrayList();

		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String etsCCListStr = "";

		try {

			sb.append("select ");
			sb.append(" a.ETS_CCLIST as cclist ");
			sb.append(" from");
			sb.append(" " + ISMGTSCHEMA + ".problem_info_usr1 a");
			sb.append(" where ");
			sb.append(" a.edge_problem_id='" + edgeProblemId + "' ");
			sb.append(" for read only ");

			SysLog.log(SysLog.DEBUG, "get cclist info ", "get cclist info qry =" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					etsCCListStr = AmtCommonUtils.getTrimStr(rs.getString("CCLIST"));

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return etsCCListStr;
	}

	/**
			 * This method will query the tables ETS.PMO_ISSUE_INFO and takes
			 * ets cc list from the table
			  */

	public String getEtsCCListForPmoIssue(String edgeProblemId) throws SQLException, Exception {

		ArrayList ownerInfoList = new ArrayList();

		StringBuffer sb = new StringBuffer();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String etsCCListStr = "";

		try {

			sb.append("select ");
			sb.append(" a.ETS_CCLIST as cclist ");
			sb.append(" from");
			sb.append(" ETS.PMO_ISSUE_INFO a");
			sb.append(" where ");
			sb.append(" a.ets_id='" + edgeProblemId + "' ");
			sb.append(" for read only ");

			SysLog.log(SysLog.DEBUG, "get cclist info for pmo issues", "get cclist info qry for pmo issues=" + sb.toString() + ":");

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					etsCCListStr = AmtCommonUtils.getTrimStr(rs.getString("CCLIST"));

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return etsCCListStr;
	}

	public boolean isIssueSrcPMO(String edgeProblemId) throws SQLException, Exception {

		int cqcount = 0;
		int pmocount = 0;
		Connection conn = null;
		boolean pmoSource = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			String cqQry = "select count(edge_problem_id) from " + ISMGTSCHEMA + ".problem_info_usr1 where edge_problem_id='" + edgeProblemId + "' for read only";
			String pmoQry = "select count(ets_id) from ets.pmo_issue_info where ets_id='" + edgeProblemId + "' for read only";

			Global.println("PMO COUTN QRY==" + pmoQry);
			Global.println("CQ COUTN QRY==" + cqQry);

			cqcount = AmtCommonUtils.getRecCount(conn, cqQry);
			pmocount = AmtCommonUtils.getRecCount(conn, pmoQry);

			if (pmocount > 0) {

				pmoSource = true;
			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return pmoSource;

	}

	/**
	 * 
	 * @param projectId
	 * @param propMap
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public List getRemindIssueRecs(String projectId, HashMap propMap) throws SQLException, Exception {

		List remindList = new ArrayList();

		Connection conn = null;

		try {

			conn = WrkSpcTeamUtils.getConnection();

			remindList = getRemindIssueRecs(conn, projectId, propMap);

		} finally {

			ETSDBUtils.close(conn);

		}

		return remindList;
	}

	///
	/**
		 * 
		 * @param projectId
		 * @param propMap
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public List getRemindIssueRecs(Connection conn, String projectId, HashMap propMap) throws SQLException, Exception {

		List remindList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		String edgeProblemId = "";
		ETSIssue issue = new ETSIssue();
		String ownerUserId = "";
		EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
		ArrayList ownerList = new ArrayList();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		try {

			///sev-critical//
			sb.append(" select  ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a , ets.problem_info_cq1 b, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.severity='" + SEVERITY_CRITICAL + "' ");
			sb.append(" and b.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_CRITICAL) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.ets_project_id='" + projectId + "' ");
			}
			sb.append(" and a.ets_project_id=d.project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.problem_class='" + ETSISSUESUBTYPE + "' ");

			/////sev-major
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a , ets.problem_info_cq1 b, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.severity='" + SEVERITY_MAJOR + "' ");
			sb.append(" and b.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_MAJOR) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.ets_project_id='" + projectId + "' ");
			}
			sb.append(" and a.ets_project_id=d.project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.problem_class='" + ETSISSUESUBTYPE + "' ");

			///sev-average
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a , ets.problem_info_cq1 b, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.severity='" + SEVERITY_AVERAGE + "' ");
			sb.append(" and b.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_AVERAGE) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.ets_project_id='" + projectId + "' ");
			}
			sb.append(" and a.ets_project_id=d.project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.problem_class='" + ETSISSUESUBTYPE + "' ");

			//sev-minor	
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a , ets.problem_info_cq1 b, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.severity='" + SEVERITY_MINOR + "' ");
			sb.append(" and b.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_MINOR) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.ets_project_id='" + projectId + "' ");
			}
			sb.append(" and a.ets_project_id=d.project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.problem_class='" + ETSISSUESUBTYPE + "' ");

			//enhancement		
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a , ets.problem_info_cq1 b, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.severity='" + SEVERITY_ENHANCE + "' ");
			sb.append(" and b.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_ENHANCE) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.ets_project_id='" + projectId + "' ");
			}

			sb.append(" and a.ets_project_id=d.project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.problem_class='" + ETSISSUESUBTYPE + "' ");

			sb.append(" order by 2  ");
			sb.append(" with ur ");

			logger.debug("GET REMIND ISSUES QRY===" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					RemindIssueActionModel remindModel = new RemindIssueActionModel();

					edgeProblemId = AmtCommonUtils.getTrimStr(rs.getString("PROBLEMID"));
					issue = ETSIssuesManager.getIssue(edgeProblemId, conn);
					ownerUserId = ETSIssuesManager.getOwnerUserId(issue);
					projMem = projDao.getWrkSpcOwnerDetsForProject(conn, issue.ets_project_id);

					///
					remindModel.setProblemId(edgeProblemId);
					remindModel.setIssue(issue);
					remindModel.setMailcount(getUserIdRemindCount(conn, edgeProblemId, ownerUserId));
					remindModel.setProjMem(projMem);
					//add to list

					remindList.add(remindModel);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}
		//remove the duplicate values

		return remindList;
	}

	/**
	 * 
	 * @param conn
	 * @param edgeProblemId
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public int getUserIdRemindCount(Connection conn, String edgeProblemId, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("select mail_count from ets.reminder_log x where x.edge_problem_id='" + edgeProblemId + "' and x.userid='" + userId + "' ");

		int count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;

	}

	/**
	 * 
	 * @param conn
	 * @param edgeProblemId
	 * @param ownerUserId
	 * @param mailcount
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean insertIntoRemindLog(String edgeProblemId, String ownerUserId, int mailcount) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		boolean flag = false;

		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;

		sb.append("INSERT INTO ETS.REMINDER_LOG (EDGE_PROBLEM_ID,USERID,MAIL_COUNT,MAIL_SENT,LAST_USERID,LAST_TIMESTAMP) ");
		sb.append(" values(?,?,?,'Y','RMDJBINS',current timestamp) ");

		try {

			conn = WrkSpcTeamUtils.getConnection();

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, edgeProblemId);
			pstmt.setString(2, ownerUserId);
			pstmt.setInt(3, mailcount);

			if (!isRemindLogExists(conn, edgeProblemId, ownerUserId)) {

				count += pstmt.executeUpdate();

			} else {

				count = updateRemindLog(conn, edgeProblemId, ownerUserId, mailcount);
			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		if (count > 0) {

			flag = true;
		}

		return flag;
	}

	/**
	 * 
	 * @param conn
	 * @param edgeProblemId
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isRemindLogExists(Connection conn, String edgeProblemId, String userId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(EDGE_PROBLEM_ID) from " + ISMGTSCHEMA + ".REMINDER_LOG");
		sb.append(" WHERE");
		sb.append(" EDGE_PROBLEM_ID = '" + edgeProblemId + "' ");
		sb.append(" and USERID = '" + userId + "' ");
		sb.append(" for read only");

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}

	/**
		 * 
		 * @param conn
		 * @param edgeProblemId
		 * @param ownerUserId
		 * @param mailcount
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public int updateRemindLog(Connection conn, String edgeProblemId, String ownerUserId, int mailcount) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;

		sb.append("UPDATE ETS.REMINDER_LOG ");
		sb.append(" set ");
		sb.append(" MAIL_COUNT=?,");
		sb.append(" LAST_USERID='RMDJBUPD',");
		sb.append(" LAST_TIMESTAMP=current timestamp");
		sb.append(" WHERE");
		sb.append(" EDGE_PROBLEM_ID=? ");
		sb.append(" AND USERID=? ");

		try {

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setInt(1, mailcount);
			pstmt.setString(2, edgeProblemId);
			pstmt.setString(3, ownerUserId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
			 * 
			 * @param projectId
			 * @param propMap
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public List getIssuesRecsForRemoveMember(String projectId, String userId) throws SQLException, Exception {

		List remindList = new ArrayList();

		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();

			remindList = getIssuesRecsForRemoveMember(conn, projectId, userId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return remindList;
	}

	///
	/**
		 * 
		 * @param projectId
		 * @param propMap
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public List getIssuesRecsForRemoveMember(Connection conn, String projectId, String userId) throws SQLException, Exception {

		List remList = new ArrayList();
		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		String edgeProblemId = "";
		ETSMWIssue mwIssue = new ETSMWIssue();
		String ownerUserId = "";
		EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
		ArrayList ownerList = new ArrayList();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		try {

			//all recs for which userid is submitter
			sb.append(" select ");
			sb.append(" a.edge_problem_id as problemid, ");
			sb.append(" b.cq_trk_id as issueno, ");
			sb.append(" a.title as title, ");
			sb.append(" c.owner_id as ownerid, ");
			sb.append(" (select rtrim(user_fullname) from amt.users x where x.edge_userid=c.owner_id) as ownername, ");
			sb.append(" a.problem_creator as submitter, ");
			sb.append(" a.cust_name  as submittername, ");
			sb.append(" b.problem_state as status,  ");
			sb.append(" a.problem_state as userlastaction,  ");
			sb.append(" a.issue_source as issuesource, ");
			sb.append(" 'OWNER' as usercat ");
			sb.append(" from ");
			sb.append(" ets.problem_info_usr1 a, ets.problem_info_cq1 b, ets.ets_owner_cq c ");
			sb.append(" where ");
			sb.append(" a.edge_problem_id=b.edge_problem_id ");
			sb.append(" and a.edge_problem_id=c.edge_problem_id ");
			sb.append(" and a.ets_project_id='" + projectId + "' ");
			sb.append(" and b.problem_state NOT IN ('Closed','Withdrawn') ");			
			sb.append("and (c.owner_id=(select edge_userid from amt.users where ir_userid='" + userId + "' ) or a.problem_creator=(select edge_userid from amt.users where ir_userid='" + userId + "'))");
		
			///for which userid is pmo submitter
			sb.append(" union ");
			sb.append(" SELECT ");
			sb.append(" u.ETS_ID as PROBLEMID, ");
			sb.append(" CHAR(u.REF_NO) as issueno, ");
			sb.append(" u.TITLE as TITLE,");
			sb.append(" (select x.edge_userid from amt.users x where x.ir_userid=u.OWNER_IR_ID) as OWNERID, ");
			sb.append(" u.OWNER_NAME as OWNERNAME, ");
			sb.append(" (select x.edge_userid from amt.users x where x.ir_userid=u.SUBMITTER_IR_ID) as submitter,");
			sb.append(" u.SUBMITTER_NAME as submittername, ");
			sb.append(" u.PROBLEM_STATE as status,");
			sb.append(" u.STATE_ACTION as userlastaction,  ");
			sb.append(" u.issue_source as issuesource, ");
			sb.append(" 'PMOOWNER' as usercat");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info u");
			sb.append(" where");
			sb.append(" u.class ='" + ETSPMOISSUESUBTYPE + "' ");
			sb.append(" and u.pmo_project_id =(select PMO_PROJECT_ID from ets.ets_projects where project_id='" + projectId + "' ) ");
			sb.append(" and u.problem_state NOT IN ('Closed','Withdrawn')  ");
			sb.append(" and u.OWNER_IR_ID = '" + userId + "' OR U.SUBMITTER_IR_ID=(select edge_userid from amt.users where ir_userid='" + userId + "') ");
			
			logger.debug("GET PENDING  ISSUES TO REMOVE MEMBER QRY===" + sb.toString());
			
			System.out.println("Query for Remove Member--->>>"+sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsIssFilterRepTabBean issueRepTab = new EtsIssFilterRepTabBean();

					issueRepTab.setIssueZone(EtsIssFilterUtils.getTrimStr(rs.getString("USERCAT")));
					issueRepTab.setIssueProblemId(EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMID")));
					issueRepTab.setIssueCqTrkId(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUENO")));
					issueRepTab.setIssueTitle(EtsIssFilterUtils.getTrimStr(rs.getString("TITLE")));
					issueRepTab.setIssueStatus(EtsIssFilterUtils.getTrimStr(rs.getString("STATUS")));
					issueRepTab.setCurrentOwnerId(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERID")));
					issueRepTab.setCurrentOwnerName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					issueRepTab.setIssueSubmitter(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTER")));
					issueRepTab.setIssueSubmitterName(EtsIssFilterUtils.getTrimStr(rs.getString("SUBMITTERNAME")));
					issueRepTab.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE")));
					issueRepTab.setRefId(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUENO")));

					remList.add(issueRepTab);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}
		//remove the duplicate values		
		 // Print out the elements with Iterator.
		
	
		  for (Iterator it = remList.iterator (); it.hasNext (); ) {
		    System.out.println ("element is " + it.next ());
		  }
		return remList;
	}

	/**
			 * 
			 * @param conn
			 * @param edgeProblemId
			 * @param ownerUserId
			 * @param mailcount
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public int updateUsr1OnRemMembr(Connection conn, String projectId, UserObject oldUserObj, UserObject newUserObj, String lastUserId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;
		String assocCompany = "";

		sb.append("UPDATE ETS.PROBLEM_INFO_USR1 ");
		sb.append(" set ");
		sb.append(" PROBLEM_CREATOR=?, ");
		sb.append(" CUST_NAME=?, ");
		sb.append(" CUST_EMAIL=?, ");
		sb.append(" CUST_PHONE=?, ");
		sb.append(" CUST_COMPANY=?, ");
		sb.append(" LAST_USERID=?,");
		sb.append(" LAST_TIMESTAMP=current timestamp");
		sb.append(" WHERE");
		sb.append(" PROBLEM_CREATOR=? ");
		sb.append(" and ETS_PROJECT_ID=? ");

		try {

			///assoc company
			EtsProjMemberDAO projDao = new EtsProjMemberDAO();
			assocCompany = projDao.getAssocCompany(newUserObj.gUSERN);

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, newUserObj.gUSERN);
			pstmt.setString(2, newUserObj.gUSER_FULLNAME);
			pstmt.setString(3, newUserObj.gEMAIL);
			pstmt.setString(4, newUserObj.gPHONE);
			pstmt.setString(5, assocCompany);
			pstmt.setString(6, lastUserId);
			pstmt.setString(7, oldUserObj.gUSERN);
			pstmt.setString(8, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
				 * 
				 * @param conn
				 * @param edgeProblemId
				 * @param ownerUserId
				 * @param mailcount
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public int updateOwnerCqOnRemMembr(Connection conn, String projectId, UserObject oldUserObj, UserObject newUserObj, String lastUserId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;

		sb.append("UPDATE ETS.ETS_OWNER_CQ ");
		sb.append(" set ");
		sb.append(" OWNER_ID=?, ");
		sb.append(" OWNER_EMAIL=?, ");
		sb.append(" LAST_USERID=?, ");
		sb.append(" LAST_TIMESTAMP=current timestamp ");
		sb.append(" WHERE");
		sb.append(" OWNER_ID=? ");
		sb.append(" and edge_problem_id in (select edge_problem_id from ets.problem_info_usr1 where ets_project_id =? ) ");

		logger.debug("new vals==" + newUserObj.gUSERN);
		logger.debug("new vals==" + newUserObj.gEMAIL);
		logger.debug("new vals==" + oldUserObj.gUSERN);

		try {

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, newUserObj.gUSERN);
			pstmt.setString(2, newUserObj.gEMAIL);
			pstmt.setString(3, lastUserId);
			pstmt.setString(4, oldUserObj.gUSERN);
			pstmt.setString(5, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
					 * 
					 * @param conn
					 * @param edgeProblemId
					 * @param ownerUserId
					 * @param mailcount
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public int updatePmoIssueSubmtrOnRemMembr(Connection conn, String projectId, UserObject oldUserObj, UserObject newUserObj, String lastUserId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;

		sb.append("UPDATE ETS.PMO_ISSUE_INFO ");
		sb.append(" set ");
		sb.append(" SUBMITTER_IR_ID=?, ");
		sb.append(" SUBMITTER_NAME=?, ");
		sb.append(" SUBMITTER_EMAIL=?, ");
		sb.append(" SUBMITTER_PHONE=?, ");
		sb.append(" SUBMITTER_COMPANY=?, ");
		sb.append(" LAST_USERID=?,");
		sb.append(" LAST_TIMESTAMP=current timestamp");
		sb.append(" WHERE");
		sb.append(" SUBMITTER_IR_ID=? ");
		sb.append(" AND PMO_PROJECT_ID=(SELECT PMO_PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ? )");

		try {

			///assoc company
			EtsProjMemberDAO projDao = new EtsProjMemberDAO();
			String assocCompany = projDao.getAssocCompany(newUserObj.gUSERN);

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, newUserObj.gUSERN);
			pstmt.setString(2, newUserObj.gUSER_FULLNAME);
			pstmt.setString(3, newUserObj.gEMAIL);
			pstmt.setString(4, newUserObj.gPHONE);
			pstmt.setString(5, assocCompany);
			pstmt.setString(6, lastUserId);
			pstmt.setString(7, oldUserObj.gUSERN);
			pstmt.setString(8, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
						 * 
						 * @param conn
						 * @param edgeProblemId
						 * @param ownerUserId
						 * @param mailcount
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public int updatePmoIssueOwnerOnRemMembr(Connection conn, String projectId, UserObject oldUserObj, UserObject newUserObj, String lastUserId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		sb.append("UPDATE ETS.PMO_ISSUE_INFO ");
		sb.append(" set ");
		sb.append(" OWNER_IR_ID=?, ");
		sb.append(" OWNER_NAME=?, ");
		sb.append(" LAST_USERID=?,");
		sb.append(" LAST_TIMESTAMP=current timestamp");
		sb.append(" WHERE");
		sb.append(" OWNER_IR_ID=? ");
		sb.append(" AND PMO_PROJECT_ID=(SELECT PMO_PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PROJECT_ID = ? )");

		try {

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, newUserObj.gIR_USERN);
			pstmt.setString(2, newUserObj.gUSER_FULLNAME);
			pstmt.setString(3, lastUserId);
			pstmt.setString(4, oldUserObj.gIR_USERN);
			pstmt.setString(5, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
	 * 
	 */

	public boolean updateIssuesOnRemMembr(String projectId, String oldUserId, String newUserId, String lastUserId) {

		Connection conn = null;
		boolean flg = false;

		int usr1count = 0;
		int ownercount = 0;
		int pmosubcount = 0;
		int pmoowncount = 0;

		int totalcount = 0;

		try {

			conn = ETSDBUtils.getConnection();

			EtsProjMemberDAO projDao = new EtsProjMemberDAO();

			//get old user obj
			UserObject oldUserObj = projDao.getUserObjectIrId(oldUserId);

			//get new user obj
			UserObject newUserObj = projDao.getUserObjectIrId(newUserId);

			//update usr1 
			usr1count += updateUsr1OnRemMembr(conn, projectId, oldUserObj, newUserObj, lastUserId);

			//update owner cq
			ownercount += updateOwnerCqOnRemMembr(conn, projectId, oldUserObj, newUserObj, lastUserId);

			//update pmo submitter
			pmosubcount += updatePmoIssueSubmtrOnRemMembr(conn, projectId, oldUserObj, newUserObj, lastUserId);

			//update pmo owner

			pmoowncount += updatePmoIssueOwnerOnRemMembr(conn, projectId, oldUserObj, newUserObj, lastUserId);
			
			//delete from cc list
			deleteOldUserFromCCList(conn, projectId, oldUserObj,newUserObj,lastUserId);

			//delete from ets issue subscriptions
			deleteSubscriptions(conn, projectId, oldUserObj);

			totalcount = usr1count + ownercount + pmosubcount + pmoowncount;

			flg = true;

		} catch (SQLException sqlEx) {

			if (conn != null) {

				try {

					conn.rollback();

				} catch (SQLException ex) {

					ex.printStackTrace();
				}
			}

			logger.error("SQL Exception in update remove member", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in update remove member", ex);
			ex.printStackTrace();

		} finally {

			ETSDBUtils.close(conn);

		}

		return flg;
	}

	/**
							 * 
							 * @param conn
							 * @param edgeProblemId
							 * @param ownerUserId
							 * @param mailcount
							 * @return
							 * @throws SQLException
							 * @throws Exception
							 */

	public int deleteSubscriptions(Connection conn, String projectId, UserObject oldUserObj) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int count = 0;

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		sb.append("DELETE FROM ETS.SUBSCRIBE_ISSUETYPE ");
		sb.append(" WHERE");
		sb.append(" EDGE_USER_ID=? ");
		sb.append(" AND PROJECT_ID=? ");

		try {

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, oldUserObj.gUSERN);
			pstmt.setString(2, projectId);

			count += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
								 * 
								 * @param conn
								 * @param edgeProblemId
								 * @param ownerUserId
								 * @param mailcount
								 * @return
								 * @throws SQLException
								 * @throws Exception
								 */

	public List getAllCCListForUserId(Connection conn, String projectId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;
		List ccList = new ArrayList();

		sb.append("SELECT EDGE_PROBLEM_ID,ETS_CCLIST FROM ETS.PROBLEM_INFO_USR1 ");
		sb.append(" WHERE ");
		sb.append(" ETS_PROJECT_ID='" + projectId + "' ");
		sb.append(" for read only");

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					RemoveMembrModel remModel = new RemoveMembrModel();
					remModel.setEdgeProblemId(AmtCommonUtils.getTrimStr(rs.getString("EDGE_PROBLEM_ID")));
					remModel.setEtsCcListStr(AmtCommonUtils.getTrimStr(rs.getString("ETS_CCLIST")));
					remModel.setEtsProjectId(projectId);

					ccList.add(remModel);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return ccList;
	}

	/**
								 * 
								 * @param conn
								 * @param edgeProblemId
								 * @param ownerUserId
								 * @param mailcount
								 * @return
								 * @throws SQLException
								 * @throws Exception
								 */

	public int updateEtsCCListWithNewUser(Connection conn, List ccList, String lastUserId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		PreparedStatement pstmt = null;
		int size = 0;
		int count = 0;
		RemoveMembrModel remModel = null;

		String edgeProblemId = "";
		String etsCcListStr = "";
		String etsProjectId = "";

		sb.append("UPDATE ETS.PROBLEM_INFO_USR1 ");
		sb.append(" SET");
		sb.append(" ETS_CCLIST=?,");
		sb.append(" LAST_USERID=?,");
		sb.append(" LAST_TIMESTAMP=current timestamp");
		sb.append(" WHERE");
		sb.append(" EDGE_PROBLEM_ID=? ");
		sb.append(" AND ETS_PROJECT_ID=? ");

		try {

			if (ccList != null && !ccList.isEmpty()) {

				size = ccList.size();
			}

			pstmt = conn.prepareStatement(sb.toString());

			//for loop
			for (int i = 0; i < size; i++) {

				remModel = (RemoveMembrModel) ccList.get(i);

				edgeProblemId = remModel.getEdgeProblemId();
				etsCcListStr = remModel.getEtsCcListStr();
				etsProjectId = remModel.getEtsProjectId();

				pstmt.clearParameters();
				pstmt.setString(1, etsCcListStr);
				pstmt.setString(2, lastUserId);
				pstmt.setString(3, edgeProblemId);
				pstmt.setString(4, etsProjectId);

				count += pstmt.executeUpdate();

			} //end of size

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return count;
	}

	/**
						 * 
						 * @param conn
						 * @param edgeProblemId
						 * @param ownerUserId
						 * @param mailcount
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public List filterEtsCcListWithNewUser(List ccList, UserObject newUserObj, String oldUserEmailId) throws Exception {

		
		List updatedList = new ArrayList();
		ArrayList tempList = new ArrayList();
		String tempStr = "";
		int count = 0;
		int size = 0;
		RemoveMembrModel remModel = null;

		RemoveMembrModel updRemModel = null;
		String edgeProblemId = "";
		String etsCcListStr = "";
		String etsProjectId = "";

		if (ccList != null && !ccList.isEmpty()) {

			size = ccList.size();
		}

		//for loop
		for (int i = 0; i < size; i++) {

			remModel = (RemoveMembrModel) ccList.get(i);

			edgeProblemId = remModel.getEdgeProblemId();
			etsCcListStr = remModel.getEtsCcListStr();
			tempList = AmtCommonUtils.getArrayListFromStringTok(etsCcListStr, ",");
			etsProjectId = remModel.getEtsProjectId();

			//remove old user id email from cc list
			if (tempList.contains(oldUserEmailId)) {

				tempList.remove(oldUserEmailId);
				//donot add new user email, just remove user from subscription
				//tempList.add(newUserObj.gEMAIL);
				
				updRemModel = new RemoveMembrModel();
				updRemModel.setEtsProjectId(etsProjectId);
				updRemModel.setEdgeProblemId(edgeProblemId);
				updRemModel.setEtsCcListStr(EtsIssFilterUtils.getCommSepStrFromStrList(tempList));

				updatedList.add(updRemModel);

			}

		} //end of for

		return updatedList;

	}
	
	public void deleteOldUserFromCCList(Connection conn, String projectId, UserObject oldUserObj,UserObject newUserObj,String lastUserId) throws SQLException,Exception{
		
		//get list
		List ccList = getAllCCListForUserId(conn, projectId);
		
		//filter cc list
		List filterList=filterEtsCcListWithNewUser(ccList, newUserObj,oldUserObj.gEMAIL);
		
		//update with new list
		int count=updateEtsCCListWithNewUser(conn, filterList, lastUserId);
	}

} //end of class
