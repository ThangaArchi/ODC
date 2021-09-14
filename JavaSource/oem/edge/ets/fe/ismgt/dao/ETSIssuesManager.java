package oem.edge.ets.fe.ismgt.dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSStringUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
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

public class ETSIssuesManager implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.41";

	static {
		if (!Global.loaded)
			Global.Init();
	}

	public static synchronized boolean addNewIssue(ETSIssue issue) throws SQLException, Exception {
		return addNewIssue(issue, (Connection) null);
	}
	
	public static boolean isProblemIdExistsInUsr1(String edgeProblemId) throws SQLException, Exception {

		boolean flg=true;
		Connection conn = null;
			
		try {
			
		conn = ETSDBUtils.getConnection();
		flg=isProblemIdExistsInUsr1(edgeProblemId, conn); 

			
		} finally {

			ETSDBUtils.close(conn);
		}

			return flg;

		}

	public static boolean isProblemIdExistsInUsr1(String edgeProblemId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(EDGE_PROBLEM_ID) from " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1");
		sb.append(" WHERE");
		sb.append(" APPLICATION_ID='ETS' ");
		sb.append(" and EDGE_PROBLEM_ID = '" + edgeProblemId + "' ");
		sb.append(" for read only");

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}
	public static synchronized boolean addNewIssue(ETSIssue issue, Connection conn) throws SQLException, Exception {

		boolean flg = false;
		PreparedStatement pstmt = null;
		int rowCount = 0;

		try {

			String sql =
				"insert into "
					+ ISMGTSCHEMA
					+ ".PROBLEM_INFO_USR1"
					+ " (APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID, PROBLEM_STATE, SEQ_NO, PROBLEM_CREATOR, CREATION_DATE, CUST_NAME, CUST_EMAIL, CUST_PHONE, CUST_COMPANY, CUST_PROJECT, PROBLEM_CLASS, TITLE, SEVERITY, PROBLEM_TYPE, PROBLEM_DESC, LAST_USERID, LAST_TIMESTAMP, "
					+ " FIELD_C1,FIELD_C2,FIELD_C3,FIELD_C4,FIELD_C5,FIELD_C6,FIELD_C7,FIELD_C8,ETS_CCLIST,ETS_PROJECT_ID,ISSUE_ACCESS,ISSUE_SOURCE,SUBTYPE_A,SUBTYPE_B,SUBTYPE_C,SUBTYPE_D,TEST_CASE,FIELD_C14,FIELD_C15,FIELD_C12,ETS_ISSUES_TYPE,ISSUE_TYPE_ID )"
					+ " VALUES (?, ?, ?, ?, ?, ?, current timestamp, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.clearParameters();

			String userid = issue.problem_creator;

			//5.2.1 ext bhf
			String lastUserId = issue.last_userid;

			pstmt.setString(1, "ETS");
			pstmt.setString(2, AmtCommonUtils.getTrimStr(issue.edge_problem_id));
			pstmt.setString(3, "-");
			pstmt.setString(4, "Submit");
			pstmt.setInt(5, 1);
			pstmt.setString(6, AmtCommonUtils.getTrimStr(userid));
			// pstmt.setTimestamp(7, current timestamp);
			pstmt.setString(7, AmtCommonUtils.getTrimStr(issue.cust_name));
			pstmt.setString(8, AmtCommonUtils.getTrimStr(issue.cust_email));
			pstmt.setString(9, AmtCommonUtils.getTrimStr(issue.cust_phone));
			pstmt.setString(10, AmtCommonUtils.getTrimStr(issue.cust_company));
			pstmt.setString(11, AmtCommonUtils.getTrimStr(issue.cust_project));
			pstmt.setString(12, AmtCommonUtils.getTrimStr(issue.problem_class));
			pstmt.setString(13, AmtCommonUtils.getTrimStr(issue.title));
			pstmt.setString(14, AmtCommonUtils.getTrimStr(issue.severity));
			pstmt.setString(15, AmtCommonUtils.getTrimStr(issue.problem_type));
			pstmt.setString(16, AmtCommonUtils.getTrimStr(issue.problem_desc));
			pstmt.setString(17, AmtCommonUtils.getTrimStr(lastUserId));
			pstmt.setString(18, AmtCommonUtils.getTrimStr(issue.field_C1));
			pstmt.setString(19, AmtCommonUtils.getTrimStr(issue.field_C2));
			pstmt.setString(20, AmtCommonUtils.getTrimStr(issue.field_C3));
			pstmt.setString(21, AmtCommonUtils.getTrimStr(issue.field_C4));
			pstmt.setString(22, AmtCommonUtils.getTrimStr(issue.field_C5));
			pstmt.setString(23, AmtCommonUtils.getTrimStr(issue.field_C6));
			pstmt.setString(24, AmtCommonUtils.getTrimStr(issue.field_C7));
			pstmt.setString(25, AmtCommonUtils.getTrimStr(issue.field_C8));
			pstmt.setString(26, AmtCommonUtils.getTrimStr(issue.ets_cclist));
			pstmt.setString(27, AmtCommonUtils.getTrimStr(issue.ets_project_id));
			pstmt.setString(28, AmtCommonUtils.getTrimStr(issue.issue_access));
			pstmt.setString(29, AmtCommonUtils.getTrimStr(issue.issue_source));
			pstmt.setString(30, AmtCommonUtils.getTrimStr(issue.subTypeA));
			pstmt.setString(31, AmtCommonUtils.getTrimStr(issue.subTypeB));
			pstmt.setString(32, AmtCommonUtils.getTrimStr(issue.subTypeC));
			pstmt.setString(33, AmtCommonUtils.getTrimStr(issue.subTypeD));
			pstmt.setString(34, AmtCommonUtils.getTrimStr(issue.test_case));
			///////////fxpk1////////////
			pstmt.setString(35, AmtCommonUtils.getTrimStr(issue.field_C14)); //first name
			pstmt.setString(36, AmtCommonUtils.getTrimStr(issue.field_C15)); //last name
			pstmt.setString(37, AmtCommonUtils.getTrimStr(issue.field_C12)); //tc
			pstmt.setString(38, AmtCommonUtils.getTrimStr(issue.etsIssuesType)); //SUPPORT OR ??
			pstmt.setString(39, AmtCommonUtils.getTrimStr(issue.issueTypeId)); //ISSUE TYPE ID ??
			

			if (!isProblemIdExistsInUsr1(issue.edge_problem_id, conn)) {

				rowCount += pstmt.executeUpdate();

			}
			
			
			

		} catch (SQLException e) {
			flg = false;
			printErr(getStackTrace(e));
			throw e;
		} catch (Exception e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;
		} finally {

			ETSDBUtils.close(pstmt);

		}
		
		if(rowCount > 0) {
	

		flg = true;
		
		}
		return flg;

	}
	
	public static synchronized boolean deleteFromUsr1(Connection conn,String applicationId, String edgeProblemId) throws SQLException, Exception {

			
			PreparedStatement pstmt = null;
			int deleteCount = 0;
			boolean flag=false;

			try {

				
				edgeProblemId = ETSStringUtils.trim(edgeProblemId);

				StringBuffer sb = new StringBuffer(150);
				sb.append("DELETE FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1 WHERE APPLICATION_ID=? AND EDGE_PROBLEM_ID =? ");

				pstmt = conn.prepareStatement(sb.toString());

				pstmt.clearParameters();
				pstmt.setString(1, applicationId);
				pstmt.setString(2, edgeProblemId);
				

				deleteCount += pstmt.executeUpdate();

				Global.println("rec deleted for edgeProblemId::usr1=" + edgeProblemId + "::" +  deleteCount);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, "exception in delete attach", e);
				e.printStackTrace();
				
			} finally {
				
				ETSDBUtils.close(pstmt);
				
			}
			
			if(deleteCount >0) {
				
				flag=true;
			}
			return flag;
		}


	public static synchronized int updateFileNewFlag(String edge_problem_id, String cq_trk_id, String userid) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int updateCount = 0;
		int selectCount = 0;

		try {

			dbConnection = ETSDBUtils.getConnection();

			String appId = ETSStringUtils.trim("ETS");
			String epId = ETSStringUtils.trim(edge_problem_id);
			String cqId = ETSStringUtils.trim(cq_trk_id);

			StringBuffer sb = new StringBuffer(300);
			sb.append(" UPDATE " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" SET    FILE_NEW_FLG=? ,LAST_USERID =?, LAST_TIMESTAMP=current timestamp ");
			sb.append(" WHERE  APPLICATION_ID=? ");
			sb.append(" AND    EDGE_PROBLEM_ID = ? ");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, "N");
			pstmt.setString(2, ETSStringUtils.trim(userid));

			pstmt.setString(3, appId);
			pstmt.setString(4, epId);

			updateCount += pstmt.executeUpdate();

		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return updateCount;

	}
//	Commented by v2sagar for PROBLEM_INFO_USR2
/*
	public static int updateCqTrackId(String edge_problem_id, String cq_trk_id, int seq_no, String userid) throws SQLException, Exception {

		Connection dbConnection = null;
		int updateCount = 0;
		int selectCount = 0;

		try {

			dbConnection = ETSDBUtils.getConnection();

			updateCount += updateCqTrackId(edge_problem_id, cq_trk_id, seq_no, userid, dbConnection);

		} finally {

			ETSDBUtils.close(dbConnection);
		}

		return updateCount;

	}
	*/
//	Commented by v2sagar for PROBLEM_INFO_USR2
/*
	public static int updateCqTrackId(String edge_problem_id, String cq_trk_id, int seq_no, String userid, Connection dbConnection) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		int updateCount = 0;
		int selectCount = 0;

		try {

			String appId = "ETS";
			String epId = edge_problem_id;
			String cqId = cq_trk_id;

			selectCount = getAttachmentCount(edge_problem_id, null);

			if (selectCount > 0) {

				StringBuffer sb = new StringBuffer();
				sb.append(" UPDATE  " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
				sb.append(" SET     CQ_TRK_ID=? ,SEQ_NO=? ,LAST_USERID =?, LAST_TIMESTAMP=current timestamp ");
				sb.append(" WHERE   APPLICATION_ID=?");
				sb.append(" AND     EDGE_PROBLEM_ID = ? ");

				pstmt = dbConnection.prepareStatement(sb.toString());

				pstmt.clearParameters();
				pstmt.setString(1, cq_trk_id);
				pstmt.setInt(2, seq_no);
				pstmt.setString(3, userid);
				pstmt.setString(4, appId);
				pstmt.setString(5, epId);

				updateCount += pstmt.executeUpdate();
			}

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return updateCount;

	}*/

	public static synchronized boolean createAttachment(ETSIssueAttach attach, Connection dbConnection) {

		PreparedStatement pstmt = null;
		int count = 0;
		boolean success = false;

		try {
			if (dbConnection == null) {
				//dbConnect = new DbConnect();
				//dbConnect.makeConn();
				//conn = dbConnect.conn;
				dbConnection = ETSDBUtils.getConnection();
			}

			StringBuffer sb = new StringBuffer(250);
			sb.append("INSERT INTO " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 (APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID, SEQ_NO, FILE_NO, FILE_NAME, FILE_DESC, FILE_MIME, FILE_DATA, FILE_SIZE, FILE_NEW_FLG, LAST_USERID, LAST_TIMESTAMP ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,current timestamp)");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, ETSStringUtils.trim(attach.getApplicationId()));
			pstmt.setString(2, ETSStringUtils.trim(attach.getEdgeProblemId()));
			pstmt.setString(3, ETSStringUtils.trim(attach.getCqTrackId()));
			//NEW-3.2.3
			pstmt.setInt(4, attach.getSeqNo());
			pstmt.setInt(5, attach.getFileNo());
			pstmt.setString(6, ETSStringUtils.trim(attach.getFileName()));
			pstmt.setString(7, ETSDBUtils.escapeString(ETSStringUtils.trim(attach.getFileDesc())));

			pstmt.setString(8, ETSStringUtils.trim(attach.getFileMime(), 50));

			ByteArrayInputStream bi = new ByteArrayInputStream(attach.getFileData(), 0, (int) attach.getFileSize());
			pstmt.setBinaryStream(9, bi, (int) attach.getFileSize());

			pstmt.setInt(10, (int) attach.getFileSize());
			pstmt.setString(11, ETSStringUtils.trim(attach.getFileNewFlag()));
			pstmt.setString(12, ETSStringUtils.trim(attach.getUser()));
			//  pstmt.setString(13, current_timestamp);

			pstmt.execute();

			count++;
			success = true;

		} catch (SQLException e) {
			System.out.println(e.getMessage());

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return success;

	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static  Hashtable  getCustFieldLabels(String projectId) throws SQLException, Exception {
		Connection con=null;
		Hashtable htCustFields = new Hashtable();
		
		try {
			
			con = ETSDBUtils.getConnection();
		
			htCustFields = getCustFieldLabels(projectId, con);
						
			}
					
		finally {
				
			
			ETSDBUtils.close(con);
			
				
			}
		return htCustFields;
		
	}
	
	/**
	 * 
	 * @param projectId
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static  Hashtable  getCustFieldLabels(String projectId, Connection conn) throws SQLException, Exception {
		//DbConnect dbConnect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Hashtable htCustFields = new Hashtable();
		try {
			String sql = "select FIELD_ID, FIELD_LABEL from " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS where PROJECT_ID = '" + projectId + "' with ur";
			stmt = conn.createStatement();			
			rs = stmt.executeQuery(sql);
			String custFieldInx = "";
			while(rs.next()) {
				custFieldInx = "FIELD_C" + rs.getString("FIELD_ID");
				htCustFields.put(custFieldInx, rs.getString("FIELD_LABEL"));
			}
			rs.close();
			stmt.close();

			return htCustFields;

		} catch (SQLException e) {
			printErr(getStackTrace(e));
			throw e;
		} catch (Exception e) {
			printErr(getStackTrace(e));
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);			
		}
		
	}
	
	
	public static ETSIssue getIssue(String edge_problem_id) throws SQLException, Exception {
		
		Connection con=null;
		ETSIssue issue=new ETSIssue();
		
		try {
			
			con = ETSDBUtils.getConnection();
		
			issue=getIssue(edge_problem_id, con);
						
			}
					
		finally {
				
			
			ETSDBUtils.close(con);
			
				
			}
		return issue;
	}

	public static ETSMWIssue getMWIssue(String edge_problem_id) throws SQLException, Exception {
		Connection con=null;
		ETSMWIssue mwIssue=new ETSMWIssue();
		
		try {
			
			con = ETSDBUtils.getConnection();
		
			mwIssue=getMWIssue(edge_problem_id, con);
						
			}
					
		finally {
				
			
			ETSDBUtils.close(con);
			
				
			}
		return mwIssue;
	}
	public static ETSIssue getIssue(String edge_problem_id, Connection conn) throws SQLException, Exception {

		//DbConnect dbConnect = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		

		try {
			

			String sql = "select CQ_TRK_ID from " + ISMGTSCHEMA + ".PROBLEM_INFO_CQ1 where APPLICATION_ID = 'ETS' and EDGE_PROBLEM_ID = '" + edge_problem_id + "' for read only";

			stmt = conn.createStatement();
			rs1 = stmt.executeQuery(sql);

			boolean getCQ2 = false;
			if (rs1.next())
				getCQ2 = true;
			rs1.close();

			if (!getCQ2) {
				sql =
					"select usr1.APPLICATION_ID, usr1.EDGE_PROBLEM_ID, usr1.CQ_TRK_ID, usr1.PROBLEM_STATE as PROBLEMSTATE, usr1.SEQ_NO, usr1.PROBLEM_CLASS, usr1.TITLE, usr1.SEVERITY, usr1.PROBLEM_TYPE, usr1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
						+ " usr1.SUBTYPE_A,usr1.SUBTYPE_B,usr1.SUBTYPE_C,usr1.SUBTYPE_D,usr1.FIELD_C1,usr1.FIELD_C2,usr1.FIELD_C3,usr1.FIELD_C4,usr1.FIELD_C5,usr1.FIELD_C6,usr1.FIELD_C7, usr1.FIELD_C8, usr1.FIELD_C12,usr1.FIELD_C14,usr1.FIELD_C15,usr1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,usr1.ISSUE_ACCESS,usr1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
						+ " from "
						+ ISMGTSCHEMA
						+ ".PROBLEM_INFO_USR1 usr1"
						+ " where usr1.APPLICATION_ID = 'ETS'"
						+ " and usr1.EDGE_PROBLEM_ID = '"
						+ edge_problem_id
						+ "'"
						+ " for read only";
			} else {

				int usr1_seq_no = getUsr1Seq_no(conn,edge_problem_id);

				int cq1_seq_no = getCq1Seq_no(conn,edge_problem_id);

				if (cq1_seq_no > usr1_seq_no) {
					sql =
						"select cq1.APPLICATION_ID, cq1.EDGE_PROBLEM_ID, cq1.CQ_TRK_ID, cq1.PROBLEM_STATE as PROBLEMSTATE, cq1.SEQ_NO, cq1.PROBLEM_CLASS, cq1.TITLE, cq1.SEVERITY, cq1.PROBLEM_TYPE, cq1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
							+ " cq1.SUBTYPE_A,cq1.SUBTYPE_B,cq1.SUBTYPE_C,cq1.SUBTYPE_D,cq1.FIELD_C1,cq1.FIELD_C2,cq1.FIELD_C3,cq1.FIELD_C4,cq1.FIELD_C5,cq1.FIELD_C6,cq1.FIELD_C7,cq1.FIELD_C8,cq1.FIELD_C12,cq1.FIELD_C14,cq1.FIELD_C15,cq1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,cq1.ISSUE_ACCESS,cq1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
							+ " from "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_USR1 usr1, "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_CQ1 cq1"
							+ " where usr1.APPLICATION_ID = 'ETS'"
							+ " and cq1.APPLICATION_ID = 'ETS'"
							+ " and usr1.EDGE_PROBLEM_ID = '"
							+ edge_problem_id
							+ "'"
							+ " and usr1.EDGE_PROBLEM_ID = cq1.EDGE_PROBLEM_ID"
							+ " for read only";

				} else {

					sql =
						"select usr1.APPLICATION_ID, usr1.EDGE_PROBLEM_ID, usr1.CQ_TRK_ID, 'In Process' as PROBLEMSTATE, usr1.SEQ_NO, usr1.PROBLEM_CLASS, usr1.TITLE, usr1.SEVERITY, usr1.PROBLEM_TYPE, usr1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
							+ " usr1.SUBTYPE_A,usr1.SUBTYPE_B,usr1.SUBTYPE_C,usr1.SUBTYPE_D,usr1.FIELD_C1,usr1.FIELD_C2,usr1.FIELD_C3,usr1.FIELD_C4,usr1.FIELD_C5,usr1.FIELD_C6,usr1.FIELD_C7,usr1.FIELD_C8,usr1.FIELD_C12,usr1.FIELD_C14,usr1.FIELD_C15,usr1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,usr1.ISSUE_ACCESS,usr1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
							+ " from "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_USR1 usr1"
							+ " where usr1.APPLICATION_ID = 'ETS'"
							+ " and usr1.EDGE_PROBLEM_ID = '"
							+ edge_problem_id
							+ "'"
							+ " for read only";

				}
			}

			Global.println("view details qry in view issue/view change===" + sql);

			rs = stmt.executeQuery(sql);

			ETSIssue issue = null;
			if (rs.next())
				issue = getIssue(conn,rs, getCQ2);

			rs.close();
			stmt.close();

			return issue;

		} catch (SQLException e) {
			/*if (dbConnect != null) {
			    dbConnect.removeConn(e);
			    dbConnect = null;
			}*/
			printErr(getStackTrace(e));
			throw e;
		} catch (Exception e) {
			printErr(getStackTrace(e));
			throw e;
		} finally {
			/*if (dbConnect != null) {
			    dbConnect.closeConn();
			}*/
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			
			
		}

	}

	public static ETSMWIssue getMWIssue(String edge_problem_id, Connection conn) throws SQLException, Exception {

		//DbConnect dbConnect = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		

		try {
			

			String sql = "select CQ_TRK_ID from " + ISMGTSCHEMA + ".PROBLEM_INFO_CQ1 where APPLICATION_ID = 'ETS' and EDGE_PROBLEM_ID = '" + edge_problem_id + "' for read only";

			stmt = conn.createStatement();
			rs1 = stmt.executeQuery(sql);

			boolean getCQ2 = false;
			if (rs1.next())
				getCQ2 = true;
			rs1.close();

			if (!getCQ2) {
				sql =
					"select usr1.APPLICATION_ID, usr1.EDGE_PROBLEM_ID, usr1.CQ_TRK_ID, usr1.PROBLEM_STATE as PROBLEMSTATE, usr1.SEQ_NO, usr1.PROBLEM_CLASS, usr1.TITLE, usr1.SEVERITY, usr1.PROBLEM_TYPE, usr1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
						+ " usr1.SUBTYPE_A,usr1.SUBTYPE_B,usr1.SUBTYPE_C,usr1.SUBTYPE_D,usr1.FIELD_C1,usr1.FIELD_C2,usr1.FIELD_C3,usr1.FIELD_C4,usr1.FIELD_C5,usr1.FIELD_C6,usr1.FIELD_C7,usr1.FIELD_C12,usr1.FIELD_C14,usr1.FIELD_C15,usr1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,usr1.ISSUE_ACCESS,usr1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
						+ " from "
						+ ISMGTSCHEMA
						+ ".PROBLEM_INFO_USR1 usr1"
						+ " where usr1.APPLICATION_ID = 'ETS'"
						+ " and usr1.EDGE_PROBLEM_ID = '"
						+ edge_problem_id
						+ "'"
						+ " for read only";
			} else {

				int usr1_seq_no = getUsr1Seq_no(conn,edge_problem_id);

				int cq1_seq_no = getCq1Seq_no(conn,edge_problem_id);

				if (cq1_seq_no > usr1_seq_no) {
					sql =
						"select cq1.APPLICATION_ID, cq1.EDGE_PROBLEM_ID, cq1.CQ_TRK_ID, cq1.PROBLEM_STATE as PROBLEMSTATE, cq1.SEQ_NO, cq1.PROBLEM_CLASS, cq1.TITLE, cq1.SEVERITY, cq1.PROBLEM_TYPE, cq1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
							+ " cq1.SUBTYPE_A,cq1.SUBTYPE_B,cq1.SUBTYPE_C,cq1.SUBTYPE_D,cq1.FIELD_C1,cq1.FIELD_C2,cq1.FIELD_C3,cq1.FIELD_C4,cq1.FIELD_C5,cq1.FIELD_C6,cq1.FIELD_C7,cq1.FIELD_C12,cq1.FIELD_C14,cq1.FIELD_C15,cq1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,cq1.ISSUE_ACCESS,cq1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
							+ " from "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_USR1 usr1, "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_CQ1 cq1"
							+ " where usr1.APPLICATION_ID = 'ETS'"
							+ " and cq1.APPLICATION_ID = 'ETS'"
							+ " and usr1.EDGE_PROBLEM_ID = '"
							+ edge_problem_id
							+ "'"
							+ " and usr1.EDGE_PROBLEM_ID = cq1.EDGE_PROBLEM_ID"
							+ " for read only";

				} else {

					sql =
						"select usr1.APPLICATION_ID, usr1.EDGE_PROBLEM_ID, usr1.CQ_TRK_ID, 'In Process' as PROBLEMSTATE, usr1.SEQ_NO, usr1.PROBLEM_CLASS, usr1.TITLE, usr1.SEVERITY, usr1.PROBLEM_TYPE, usr1.PROBLEM_DESC, usr1.PROBLEM_CREATOR, usr1.CREATION_DATE, usr1.CUST_NAME, usr1.CUST_EMAIL, usr1.CUST_PHONE, usr1.CUST_COMPANY, usr1.CUST_PROJECT, usr1.COMM_FROM_CUST, usr1.LAST_USERID, "
							+ " usr1.SUBTYPE_A,usr1.SUBTYPE_B,usr1.SUBTYPE_C,usr1.SUBTYPE_D,usr1.FIELD_C1,usr1.FIELD_C2,usr1.FIELD_C3,usr1.FIELD_C4,usr1.FIELD_C5,usr1.FIELD_C6,usr1.FIELD_C7,usr1.FIELD_C12,usr1.FIELD_C14,usr1.FIELD_C15,usr1.TEST_CASE,usr1.ETS_CCLIST,usr1.ETS_PROJECT_ID,usr1.ISSUE_ACCESS,usr1.ISSUE_SOURCE,usr1.ETS_ISSUES_TYPE,usr1.ISSUE_TYPE_ID "
							+ " from "
							+ ISMGTSCHEMA
							+ ".PROBLEM_INFO_USR1 usr1"
							+ " where usr1.APPLICATION_ID = 'ETS'"
							+ " and usr1.EDGE_PROBLEM_ID = '"
							+ edge_problem_id
							+ "'"
							+ " for read only";

				}
			}

			Global.println("view details qry in view issue/view change===" + sql);

			rs = stmt.executeQuery(sql);

			ETSMWIssue issue = null;
			if (rs.next())
				issue = getMWIssue(conn,rs, getCQ2);

			rs.close();
			stmt.close();

			return issue;

		} catch (SQLException e) {
			/*if (dbConnect != null) {
				dbConnect.removeConn(e);
				dbConnect = null;
			}*/
			printErr(getStackTrace(e));
			throw e;
		} catch (Exception e) {
			printErr(getStackTrace(e));
			throw e;
		} finally {
			/*if (dbConnect != null) {
				dbConnect.closeConn();
			}*/
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
						
		}

	}
	//Commented by v2sagar for PROBLEM_INFO_USR2
/*
	public static int getAttachmentCount(String edge_problem_id, Connection conn) throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int selectCount = 0;
		boolean isCqId = false;

		try {
			conn = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append(" SELECT  max(file_no)+1 FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" WHERE  APPLICATION_ID=? ");
			sb.append(" AND    EDGE_PROBLEM_ID =? ");
			sb.append(" FOR    READ ONLY");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, "ETS");
			pstmt.setString(2, edge_problem_id);
			rset = pstmt.executeQuery();

			if (rset.next()) {
				selectCount = rset.getInt(1);
			}

		} catch (SQLException e) {
			printErr(getStackTrace(e));
			throw e;
		} catch (Exception e) {
			printErr(getStackTrace(e));
			throw e;
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}
		return selectCount;

	}*/

	



	public static Vector getAttachedFiles(String edge_problem_id, Connection dbConnection) {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Vector aList = null;
		ETSIssueAttach attach = null;

		try {
			if (dbConnection == null)
				dbConnection = ETSDBUtils.getConnection();

			String applicationId = "ETS";

			aList = new Vector();

			StringBuffer sb = new StringBuffer(300);
			sb.append(" SELECT APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID, FILE_NO, FILE_NAME, FILE_DESC, ");
			sb.append(" FILE_MIME, FILE_SIZE, FILE_NEW_FLG, LAST_USERID, LAST_TIMESTAMP");
			sb.append(" FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" WHERE APPLICATION_ID=? ");
			sb.append(" AND EDGE_PROBLEM_ID=? ");
			sb.append(" ORDER BY LAST_TIMESTAMP");
			sb.append(" FOR READ ONLY");

			Global.println("Qry for getting attached files list===" + sb.toString());

			pstmt = dbConnection.prepareStatement(sb.toString());
			pstmt.clearParameters();

			pstmt.setString(1, applicationId);
			pstmt.setString(2, edge_problem_id);

			rset = pstmt.executeQuery();

			while (rset.next()) {

				attach = new ETSIssueAttach();

				String appId = rset.getString(1).trim(); //APPLICATION_ID
				appId = appId.trim();

				String edgeProbId = rset.getString(2); //EDGE_PROBLEM_ID
				edgeProbId = ETSStringUtils.trim(edgeProbId);

				String cqId = rset.getString(3); //CQ_TRK_ID
				cqId = ETSStringUtils.trim(cqId);

				int fileNom = rset.getInt(4); //FILE_NO

				String fileName = rset.getString(5); //FILE_NAME
				fileName = ETSStringUtils.trim(fileName);

				String fileDesc = rset.getString(6); //FILE_DESC
				fileDesc = ETSStringUtils.trim(fileDesc);

				String fileMime = rset.getString(7); //FILE_MIME
				fileMime = ETSStringUtils.trim(fileMime);

				String tmp = rset.getString(8); //FILE_SIZE
				tmp = ETSStringUtils.trim(tmp);
				long fileSize = 0L;
				if (!tmp.equals("")) {
					fileSize = Long.valueOf(tmp).longValue();
				}

				String newFg = rset.getString(9); //FILE_NEW_FLG
				newFg = ETSStringUtils.trim(newFg);

				String attachedBy = rset.getString(10); //LAST_USERID
				attachedBy = ETSStringUtils.trim(attachedBy);

				String attachedDate = rset.getString(11); //LAST_TIMESTAMP
				attachedDate = ETSStringUtils.trim(attachedDate);

				attach.setApplicationId(appId);
				attach.setEdgeProblemId(edgeProbId);
				attach.setCqTrackId(cqId);
				attach.setFileNo(fileNom);
				attach.setFileName(fileName);
				attach.setFileDesc(fileDesc);
				attach.setFileMime(fileMime);
				attach.setFileSize(fileSize);
				attach.setFileNewFlag(newFg);
				attach.setUser(attachedBy);
				attach.setTimeStampString(attachedDate);

				aList.add(attach);
			}

		} catch (Exception E) {
			E.printStackTrace();
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return aList;
	}

	public static Vector getIssueTypes(String project, Connection dbConnection) {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Vector aList = null;
		try {
			if (dbConnection == null)
				dbConnection = ETSDBUtils.getConnection();

			String applicationId = "ETS";

			aList = new Vector();

			StringBuffer sb = new StringBuffer(300);
			sb.append(" SELECT DISTINCT subtype_b");
			sb.append(" FROM " + ISMGTSCHEMA + ".PROBLEM_ID_DATA ");
			sb.append(" WHERE APPLICATION_ID=? ");
			sb.append(" AND SUBTYPE_A=? ");
			sb.append(" AND PROBLEM_TYPE=? ");
			sb.append(" AND ACTIVE_FLAG=? ");
			sb.append(" FOR READ ONLY");

			pstmt = dbConnection.prepareStatement(sb.toString());
			pstmt.clearParameters();

			pstmt.setString(1, "ETS");
			pstmt.setString(2, "Defect");
			pstmt.setString(3, project);
			pstmt.setString(4, "Y");

			rset = pstmt.executeQuery();

			while (rset.next()) {

				String issueType = rset.getString(1);

				aList.addElement(issueType);
			}

		} catch (Exception E) {
			E.printStackTrace();
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return aList;
	}

	public static Vector getChangeTypes(String project, Connection dbConnection) {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Vector aList = null;
		try {
			if (dbConnection == null)
				dbConnection = ETSDBUtils.getConnection();

			String applicationId = "ETS";

			aList = new Vector();

			StringBuffer sb = new StringBuffer(300);
			sb.append(" SELECT DISTINCT subtype_b");
			sb.append(" FROM " + ISMGTSCHEMA + ".PROBLEM_ID_DATA ");
			sb.append(" WHERE APPLICATION_ID=? ");
			sb.append(" AND SUBTYPE_A=? ");
			sb.append(" AND PROBLEM_TYPE=? ");
			sb.append(" AND ACTIVE_FLAG=? ");
			sb.append(" FOR READ ONLY");

			pstmt = dbConnection.prepareStatement(sb.toString());
			pstmt.clearParameters();

			pstmt.setString(1, "ETS");
			pstmt.setString(2, "Change");
			pstmt.setString(3, project);
			pstmt.setString(4, "Y");

			rset = pstmt.executeQuery();

			while (rset.next()) {

				String issueType = rset.getString(1);

				aList.addElement(issueType);
			}

		} catch (Exception E) {
			E.printStackTrace();
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return aList;
	}

	public static int getUsr1Seq_no(String edge_problem_id) {
		int seq_no = 0;
		Connection conn = null;
		
		try {
			
			conn=ETSDBUtils.getConnection();
			
			seq_no=getUsr1Seq_no(conn,edge_problem_id);
		
		} catch (SQLException e) {
		
			printErr(getStackTrace(e));
		} catch (Exception e) {
			printErr(getStackTrace(e));
		} finally {
		
			ETSDBUtils.close(conn);
		}

		return seq_no;

	}
	
	///
	public static int getUsr1Seq_no(Connection conn,String edge_problem_id) throws SQLException {
			int seq_no = 0;
			
			Statement stmt = null;
			ResultSet rs = null;
			try {
				String sql = "select SEQ_NO from " + ISMGTSCHEMA + ".problem_info_usr1 where edge_problem_id = '" + edge_problem_id + "' for read only";
				

				stmt = conn.createStatement();

				rs = stmt.executeQuery(sql);

				if (rs != null) {

					if (rs.next()) {

						seq_no = rs.getInt("seq_no");

					}

				}

			}  finally {
				
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				
			}

			return seq_no;

		}

	
	
	///

	private static ETSIssue getIssue(Connection conn,ResultSet rs, boolean getCQ2) throws SQLException, Exception {

		ETSIssue issue = new ETSIssue();

		issue.application_id = EtsIssFilterUtils.getTrimStr(rs.getString("APPLICATION_ID"));
		issue.edge_problem_id = EtsIssFilterUtils.getTrimStr(rs.getString("EDGE_PROBLEM_ID"));
		issue.cq_trk_id = EtsIssFilterUtils.getTrimStr(rs.getString("CQ_TRK_ID"));
		//issue.problem_state = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_STATE"));
		issue.problem_state = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
		issue.seq_no = rs.getInt("SEQ_NO");
		issue.problem_class = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_CLASS"));
		issue.title = EtsIssFilterUtils.getTrimStr(rs.getString("TITLE"));
		issue.severity = EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY"));
		issue.problem_type = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_TYPE"));
		issue.problem_desc = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_DESC"));
		issue.problem_creator = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_CREATOR"));
		issue.submitDateStr = EtsIssFilterUtils.getTrimStr(rs.getString("CREATION_DATE"));
		issue.creation_date = rs.getDate("CREATION_DATE");
		issue.cust_name = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_NAME"));
		issue.cust_email = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_EMAIL"));
		issue.cust_phone = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_PHONE"));
		issue.cust_company = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_COMPANY"));
		issue.cust_project = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_PROJECT"));
		issue.comm_from_cust = EtsIssFilterUtils.getTrimStr(rs.getString("COMM_FROM_CUST"));
		issue.last_userid = EtsIssFilterUtils.getTrimStr(rs.getString("LAST_USERID"));
		///4.2.1////////////////
		issue.subTypeA = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_A"));
		issue.subTypeB = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_B"));
		issue.subTypeC = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_C"));
		issue.subTypeD = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_D"));
		issue.field_C1 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C1"));
		issue.field_C2 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C2"));
		issue.field_C3 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C3"));
		issue.field_C4 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C4"));
		issue.field_C5 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C5"));
		issue.field_C6 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C6"));
		issue.field_C7 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C7"));
		issue.field_C8 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C8"));
		issue.field_C12=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C12"));
		issue.field_C14=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C14"));
		issue.field_C15=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C15"));
		issue.test_case = EtsIssFilterUtils.getTrimStr(rs.getString("TEST_CASE"));
		issue.ets_cclist = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_CCLIST"));
		issue.ets_project_id = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_PROJECT_ID"));
		issue.issue_access = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_ACCESS"));
		issue.issue_source = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_SOURCE"));
		issue.etsIssuesType = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_ISSUES_TYPE"));
		issue.issueTypeId=EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_TYPE_ID"));
		///////////////////////

		if (getCQ2) {
			//issue.rootcause = EtsIssFilterUtils.getTrimStr(rs.getString("ROOTCAUSE"));
			//issue.field_r1 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_R1"));

			//get owner info list
			IssueInfoDAO issInfoDao = new IssueInfoDAO();
			ArrayList ownInfoList = issInfoDao.getProbOwnerList(conn,issue.edge_problem_id);
			issue.probOwnerList = ownInfoList;

		}

		return issue;
	}

	private static ETSMWIssue getMWIssue(Connection conn,ResultSet rs, boolean getCQ2) throws SQLException, Exception {

		ETSMWIssue issue = new ETSMWIssue();

		issue.application_id = EtsIssFilterUtils.getTrimStr(rs.getString("APPLICATION_ID"));
		issue.edge_problem_id = EtsIssFilterUtils.getTrimStr(rs.getString("EDGE_PROBLEM_ID"));
		issue.cq_trk_id = EtsIssFilterUtils.getTrimStr(rs.getString("CQ_TRK_ID"));
		//issue.problem_state = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_STATE"));
		issue.problem_state = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
		issue.seq_no = rs.getInt("SEQ_NO");
		issue.problem_class = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_CLASS"));
		issue.title = EtsIssFilterUtils.getTrimStr(rs.getString("TITLE"));
		issue.severity = EtsIssFilterUtils.getTrimStr(rs.getString("SEVERITY"));
		issue.problem_type = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_TYPE"));
		issue.problem_desc = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_DESC"));
		issue.problem_creator = EtsIssFilterUtils.getTrimStr(rs.getString("PROBLEM_CREATOR"));
		issue.submitDateStr = EtsIssFilterUtils.getTrimStr(rs.getString("CREATION_DATE"));
		issue.creation_date = rs.getDate("CREATION_DATE");
		issue.cust_name = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_NAME"));
		issue.cust_email = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_EMAIL"));
		issue.cust_phone = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_PHONE"));
		issue.cust_company = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_COMPANY"));
		issue.cust_project = EtsIssFilterUtils.getTrimStr(rs.getString("CUST_PROJECT"));
		issue.comm_from_cust = EtsIssFilterUtils.getTrimStr(rs.getString("COMM_FROM_CUST"));
		issue.last_userid = EtsIssFilterUtils.getTrimStr(rs.getString("LAST_USERID"));
		///4.2.1////////////////
		issue.subTypeA = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_A"));
		issue.subTypeB = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_B"));
		issue.subTypeC = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_C"));
		issue.subTypeD = EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPE_D"));
		issue.field_C1 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C1"));
		issue.field_C2 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C2"));
		issue.field_C3 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C3"));
		issue.field_C4 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C4"));
		issue.field_C5 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C5"));
		issue.field_C6 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C6"));
		issue.field_C7 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C7"));
		issue.field_C12=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C12"));
		issue.field_C14=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C14"));
		issue.field_C15=EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_C15"));
		issue.test_case = EtsIssFilterUtils.getTrimStr(rs.getString("TEST_CASE"));
		issue.ets_cclist = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_CCLIST"));
		issue.ets_project_id = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_PROJECT_ID"));
		issue.issue_access = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_ACCESS"));
		issue.issue_source = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_SOURCE"));
		issue.etsIssuesType = EtsIssFilterUtils.getTrimStr(rs.getString("ETS_ISSUES_TYPE"));
		issue.issueTypeId=EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_TYPE_ID"));
		///////////////////////

		if (getCQ2) {
			//issue.rootcause = EtsIssFilterUtils.getTrimStr(rs.getString("ROOTCAUSE"));
			//issue.field_r1 = EtsIssFilterUtils.getTrimStr(rs.getString("FIELD_R1"));

			//get owner info list
			IssueInfoDAO issInfoDao = new IssueInfoDAO();
			ArrayList ownInfoList = issInfoDao.getProbOwnerList(conn,issue.edge_problem_id);
			issue.probOwnerList = ownInfoList;

		}

		return issue;
	}

	private static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	private static void printOut(String str) {
		// System.out.println(str);
	}

	private static void printErr(String str) {
		System.err.println(str);
	}

	public static void downloadAttach(HttpServletResponse res, String edge_problem_id, int fileNo) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection dbConnection = null;

		try {

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT   FILE_MIME, LENGTH(FILE_DATA), FILE_NAME, FILE_DATA ");
			sb.append(" FROM     " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" WHERE    APPLICATION_ID=? ");
			sb.append(" AND      EDGE_PROBLEM_ID=? ");
			sb.append(" AND      FILE_NO=? ");
			sb.append(" FOR READ ONLY");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.setString(1, "ETS");
			pstmt.setString(2, edge_problem_id);
			pstmt.setInt(3, fileNo);

			rset = pstmt.executeQuery();

			while (rset.next()) {

				String mime = rset.getString(1);
				if (mime == null)
					mime = "";
				else
					mime = mime.trim();

				int length = rset.getInt(2);

				res.setContentLength(length);

				String filename = rset.getString(3);
				if (filename == null)
					filename = "";
				else
					filename = filename.trim();

				res.setHeader("Content-Disposition", "attachment; filename=".concat(String.valueOf(filename)));
				res.setHeader("Content-Type", "application/octet-stream");
				res.setContentLength(length);

				ServletOutputStream out1 = res.getOutputStream();
				InputStream input = rset.getBinaryStream(4);

				byte buf[] = new byte[512];
				int n = 0;
				int total = 0;

				while ((n = input.read(buf)) > 0) {
					total += n;
					out1.write(buf, 0, n);
					out1.flush();
				}

				input.close();

			}

		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

	}

	public static void viewAttach(HttpServletResponse res, String edge_problem_id, int fileNo) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection dbConnection = null;

		try {

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT   FILE_MIME, LENGTH(FILE_DATA), FILE_NAME, FILE_DATA ");
			sb.append(" FROM     " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" WHERE    APPLICATION_ID=? ");
			sb.append(" AND      EDGE_PROBLEM_ID=? ");
			sb.append(" AND      FILE_NO=? ");
			sb.append(" FOR READ ONLY");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.setString(1, ETSStringUtils.trim("ETS"));
			pstmt.setString(2, ETSStringUtils.trim(edge_problem_id));
			pstmt.setInt(3, fileNo);

			rset = pstmt.executeQuery();

			while (rset.next()) {

				String mime = rset.getString(1);
				mime = ETSStringUtils.trim(mime);

				int length = rset.getInt(2);

				res.setContentLength(length);

				String filename = rset.getString(3);
				filename = ETSStringUtils.trim(filename);
				String browserMime = mime;

				Global.println("BROWSER MIME TYPE FOR ISSUES===" + browserMime);

				res.setContentType(browserMime);
				res.setContentLength(length);

				ServletOutputStream out1 = res.getOutputStream();
				InputStream input = rset.getBinaryStream(4);

				byte buf[] = new byte[512];
				int n = 0;
				int total = 0;

				while ((n = input.read(buf)) > 0) {
					total += n;
					out1.write(buf, 0, n);
					out1.flush();
				}

				input.close();

			}

		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

	}
	public static synchronized int deleteAttach(String applicationId, String edgeProblemId, int fileNo) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			applicationId = "ETS";
			edgeProblemId = ETSStringUtils.trim(edgeProblemId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 WHERE APPLICATION_ID=? AND EDGE_PROBLEM_ID =? AND FILE_NO=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, applicationId);
			pstmt.setString(2, edgeProblemId);
			pstmt.setInt(3, fileNo);

			deleteCount += pstmt.executeUpdate();

			Global.println("file deleted for edgeProblemId::fileNo::deleteCount=" + edgeProblemId + "::" + fileNo + "::" + deleteCount);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, "exception in delete attach", e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}
		return deleteCount;
	}

	public static String getActionUsr1(String edge_problem_id) {

		String state = "New";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			String sql = "select problem_state from " + ISMGTSCHEMA + ".problem_info_usr1 where edge_problem_id=" + "'" + edge_problem_id + "' for read only";
			conn = ETSDBUtils.getConnection();

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs != null) {

				if (rs.next()) {

					state = AmtCommonUtils.getTrimStr(rs.getString("problem_state"));

				}

			}

		} catch (SQLException e) {
			/*if (dbConnect != null) {
			dbConnect.removeConn(e);
			    dbConnect = null;
			}*/
			printErr(getStackTrace(e));
		} catch (Exception e) {
			printErr(getStackTrace(e));
		} finally {
			/*if (dbConnect != null) {
			    dbConnect.closeConn();
			}*/
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return state;

	}

	public static String getComm_log(String edge_problem_id) {

		String comm_log = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "select COMM_LOG from " + ISMGTSCHEMA + ".problem_info_cq2 where edge_problem_id=" + "'" + edge_problem_id + "' for read only";
			conn = ETSDBUtils.getConnection();

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			if (rs != null) {
				if (rs.next()) {

					comm_log = AmtCommonUtils.getTrimStr(rs.getString("COMM_LOG"));
					//comm_log = rs.getClob("COMM_LOG");	

				}

			}

		} catch (SQLException e) {
			/*if (dbConnect != null) {
			dbConnect.removeConn(e);
			    dbConnect = null;
			}*/
			printErr(getStackTrace(e));
		} catch (Exception e) {
			printErr(getStackTrace(e));
		} finally {
			/*if (dbConnect != null) {
			    dbConnect.closeConn();
			}*/
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		return comm_log;

	}

	public static int getCq1Seq_no(String edge_problem_id) {
		int seq_no = 0;
		Connection conn = null;
		
		try {
			
			conn=ETSDBUtils.getConnection();	
					 
		    seq_no=getCq1Seq_no(conn,edge_problem_id);
					
					
		} catch (SQLException e) {
		
			printErr(getStackTrace(e));
		} catch (Exception e) {
			printErr(getStackTrace(e));
		} finally {
		
			ETSDBUtils.close(conn);
		}

		return seq_no;

	}
	
	///
	public static int getCq1Seq_no(Connection conn,String edge_problem_id) throws SQLException{
			int seq_no = 0;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				String sql = "select SEQ_NO from " + ISMGTSCHEMA + ".problem_info_cq1 where edge_problem_id = '" + edge_problem_id + "' for read only";
				

				stmt = conn.createStatement();

				rs = stmt.executeQuery(sql);

				if (rs != null) {

					if (rs.next()) {

						seq_no = rs.getInt("seq_no");

					}

				}

			}  finally {
				
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
				
			}

			return seq_no;

		}
	
	//

	public static synchronized boolean modifyIssueWithPstmt(ETSIssue issue) throws SQLException, Exception {
		return modifyIssueWithPstmt(issue, (Connection) null);
	}
	public static synchronized boolean modifyIssueWithPstmt(ETSIssue issue, Connection conn) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		boolean flg = false;
		StringBuffer sb = new StringBuffer();
		int rowCount=0;

		try {

			////
			sb.append("update " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1 set ");
			sb.append(" CQ_TRK_ID = ?, ");
			sb.append(" SEQ_NO = ?, ");
			sb.append(" PROBLEM_STATE = ?,");
			sb.append(" SEVERITY = ?, ");
			sb.append(" TITLE = ?, ");
			sb.append(" PROBLEM_DESC = ?, ");
			sb.append(" PROBLEM_TYPE = ?, ");
			sb.append(" ISSUE_SOURCE = ?, ");
			sb.append(" SUBTYPE_A =?, ");
			sb.append(" SUBTYPE_B =?, ");
			sb.append(" SUBTYPE_C =?, ");
			sb.append(" SUBTYPE_D =?, ");
			sb.append(" FIELD_C1 =?, ");
			sb.append(" FIELD_C2 =?, ");
			sb.append(" FIELD_C3 =?, ");
			sb.append(" FIELD_C4 =?, ");
			sb.append(" FIELD_C5 =?, ");
			sb.append(" FIELD_C6 =?, ");
			sb.append(" FIELD_C7 =?, ");
			sb.append(" FIELD_C8 =?, ");
			sb.append(" FIELD_C14=?, ");
			sb.append(" FIELD_C15=?,  ");
			sb.append(" TEST_CASE = ?, ");
			sb.append(" ETS_CCLIST = ?, ");
			sb.append(" COMM_FROM_CUST = ?, ");
			sb.append(" ISSUE_TYPE_ID = ?, " );
			sb.append(" LAST_USERID= ?, ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" WHERE ");
			sb.append(" EDGE_PROBLEM_ID = ? ");
			sb.append(" and APPLICATION_ID = '" + ETSAPPLNID + "' ");

			pstmt = conn.prepareStatement(sb.toString());

			/////
			pstmt.clearParameters();
			pstmt.setString(1, issue.cq_trk_id);
			pstmt.setInt(2, issue.seq_no);
			pstmt.setString(3, issue.problem_state);
			pstmt.setString(4, issue.severity);
			pstmt.setString(5, issue.title);
			pstmt.setString(6, issue.problem_desc);
			pstmt.setString(7, issue.problem_type);
			pstmt.setString(8, issue.issue_source);
			pstmt.setString(9, issue.subTypeA);
			pstmt.setString(10, issue.subTypeB);
			pstmt.setString(11, issue.subTypeC);
			pstmt.setString(12, issue.subTypeD);
			pstmt.setString(13, issue.field_C1);
			pstmt.setString(14, issue.field_C2);
			pstmt.setString(15, issue.field_C3);
			pstmt.setString(16, issue.field_C4);
			pstmt.setString(17, issue.field_C5);
			pstmt.setString(18, issue.field_C6);
			pstmt.setString(19, issue.field_C7);
			pstmt.setString(20, issue.field_C8);
			pstmt.setString(21, issue.field_C14);
			pstmt.setString(22, issue.field_C15);
			pstmt.setString(23, issue.test_case);
			pstmt.setString(24, issue.ets_cclist);
			pstmt.setString(25, issue.comm_from_cust);
			pstmt.setString(26,issue.issueTypeId);
			pstmt.setString(27, issue.last_userid);
			pstmt.setString(28, issue.edge_problem_id);

			rowCount += pstmt.executeUpdate();

		} catch (SQLException e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} catch (Exception e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} finally {

			ETSDBUtils.close(pstmt);

		}
		
		if(rowCount >0) {
		
		flg = true;
		
		}

		return flg;
	}

	/**
		 * update only required fields 
		 * 
		 */

	public static synchronized boolean updateCommentsWithPtmt(ETSIssue issue) throws SQLException, Exception {
		return updateCommentsWithPtmt(issue, (Connection) null);
	}

	/**
	 * update only required fields 
	 * 
	 */

	public static synchronized boolean updateCommentsWithPtmt(ETSIssue issue, Connection conn) throws SQLException, Exception {

		boolean flg = false;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int rowCount=0;

		try {

			sb.append("update " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1 set ");
			sb.append(" CQ_TRK_ID = ?, ");
			sb.append(" SEQ_NO = ?, ");
			sb.append(" PROBLEM_STATE  = ?, ");
			sb.append(" FIELD_C14      = ?, ");
			sb.append(" FIELD_C15      = ?, ");
			sb.append(" COMM_FROM_CUST = ?, ");
			sb.append(" LAST_USERID    = ?, ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" WHERE ");
			sb.append(" APPLICATION_ID = 'ETS' ");
			sb.append(" and EDGE_PROBLEM_ID = ? ");

			Global.println("updateCommentsWithPtmt qry===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, issue.cq_trk_id);
			pstmt.setInt(2, issue.seq_no);
			pstmt.setString(3, issue.problem_state);
			pstmt.setString(4, issue.field_C14);
			pstmt.setString(5, issue.field_C15);
			pstmt.setString(6, issue.comm_from_cust);
			pstmt.setString(7, issue.last_userid);
			pstmt.setString(8, issue.edge_problem_id);

			rowCount += pstmt.executeUpdate();

		} catch (SQLException e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} catch (Exception e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} finally {

			ETSDBUtils.close(pstmt);

		}
		
		if(rowCount >0) {
			
			flg = true;
		
		}
		return flg;
	}

	public static synchronized int deleteAttachWithNewFlg(String applicationId, String edgeProblemId) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			applicationId = "ETS";
			edgeProblemId = ETSStringUtils.trim(edgeProblemId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 WHERE APPLICATION_ID=? AND EDGE_PROBLEM_ID =? AND FILE_NEW_FLG != 'N'");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, applicationId);
			pstmt.setString(2, edgeProblemId);

			deleteCount += pstmt.executeUpdate();

			Global.println("file deleted in deleteAttachWithNewFlg for edgeProblemId::fileNo::deleteCount=" + edgeProblemId + "::" + "::" + deleteCount);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, "exception in delete attach", e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}
		return deleteCount;
	}

	public static synchronized int updateFileFlagInUsr2(String edge_problem_id, String userid, String oldFileFlag, String newFileFlag) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int updateCount = 0;
		int selectCount = 0;

		try {

			dbConnection = ETSDBUtils.getConnection();

			String appId = ETSStringUtils.trim("ETS");
			String epId = ETSStringUtils.trim(edge_problem_id);

			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" SET    FILE_NEW_FLG=? ,LAST_USERID =?, LAST_TIMESTAMP=current timestamp ");
			sb.append(" WHERE  APPLICATION_ID=? ");
			sb.append(" AND    EDGE_PROBLEM_ID = ? ");
			sb.append(" AND    FILE_NEW_FLG = ? ");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, newFileFlag);
			pstmt.setString(2, ETSStringUtils.trim(userid));
			pstmt.setString(3, appId);
			pstmt.setString(4, epId);
			pstmt.setString(5, oldFileFlag);

			updateCount += pstmt.executeUpdate();

		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return updateCount;

	}

	public static synchronized int deleteAttachWithFileFlg(String applicationId, String edgeProblemId, String fileFlag) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			applicationId = "ETS";
			edgeProblemId = ETSStringUtils.trim(edgeProblemId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 WHERE APPLICATION_ID=? AND EDGE_PROBLEM_ID =? AND FILE_NEW_FLG = ?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, applicationId);
			pstmt.setString(2, edgeProblemId);
			pstmt.setString(3, fileFlag);

			deleteCount += pstmt.executeUpdate();

			Global.println("file deleted in deleteAttachWithNewFlg for edgeProblemId::fileNo::deleteCount=" + edgeProblemId + "::" + "::" + deleteCount);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, "exception in delete attach", e);
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}
		return deleteCount;
	}

	public static Vector getAttachedFilesWithFlag(String edge_problem_id, String fileFlag) {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Vector aList = null;
		ETSIssueAttach attach = null;
		Connection dbConnection = null;

		try {

			dbConnection = ETSDBUtils.getConnection();

			String applicationId = "ETS";

			aList = new Vector();

			StringBuffer sb = new StringBuffer(300);
			sb.append(" SELECT APPLICATION_ID, EDGE_PROBLEM_ID, CQ_TRK_ID, FILE_NO, FILE_NAME, FILE_DESC, ");
			sb.append(" FILE_MIME, FILE_SIZE, FILE_NEW_FLG, LAST_USERID, LAST_TIMESTAMP");
			sb.append(" FROM " + ISMGTSCHEMA + ".PROBLEM_INFO_USR2 ");
			sb.append(" WHERE APPLICATION_ID=? ");
			sb.append(" AND EDGE_PROBLEM_ID=? ");
			sb.append(" AND FILE_NEW_FLG=? ");
			sb.append(" ORDER BY LAST_TIMESTAMP");
			sb.append(" FOR READ ONLY");

			Global.println("Qry for getting attached files list===" + sb.toString());

			pstmt = dbConnection.prepareStatement(sb.toString());
			pstmt.clearParameters();

			pstmt.setString(1, applicationId);
			pstmt.setString(2, edge_problem_id);
			pstmt.setString(3, fileFlag);

			rset = pstmt.executeQuery();

			while (rset.next()) {

				attach = new ETSIssueAttach();

				String appId = rset.getString(1).trim(); //APPLICATION_ID
				appId = appId.trim();

				String edgeProbId = rset.getString(2); //EDGE_PROBLEM_ID
				edgeProbId = ETSStringUtils.trim(edgeProbId);

				String cqId = rset.getString(3); //CQ_TRK_ID
				cqId = ETSStringUtils.trim(cqId);

				int fileNom = rset.getInt(4); //FILE_NO

				String fileName = rset.getString(5); //FILE_NAME
				fileName = ETSStringUtils.trim(fileName);

				String fileDesc = rset.getString(6); //FILE_DESC
				fileDesc = ETSStringUtils.trim(fileDesc);

				String fileMime = rset.getString(7); //FILE_MIME
				fileMime = ETSStringUtils.trim(fileMime);

				String tmp = rset.getString(8); //FILE_SIZE
				tmp = ETSStringUtils.trim(tmp);
				long fileSize = 0L;
				if (!tmp.equals("")) {
					fileSize = Long.valueOf(tmp).longValue();
				}

				String newFg = rset.getString(9); //FILE_NEW_FLG
				newFg = ETSStringUtils.trim(newFg);

				String attachedBy = rset.getString(10); //LAST_USERID
				attachedBy = ETSStringUtils.trim(attachedBy);

				String attachedDate = rset.getString(11); //LAST_TIMESTAMP
				attachedDate = ETSStringUtils.trim(attachedDate);

				attach.setApplicationId(appId);
				attach.setEdgeProblemId(edgeProbId);
				attach.setCqTrackId(cqId);
				attach.setFileNo(fileNom);
				attach.setFileName(fileName);
				attach.setFileDesc(fileDesc);
				attach.setFileMime(fileMime);
				attach.setFileSize(fileSize);
				attach.setFileNewFlag(newFg);
				attach.setUser(attachedBy);
				attach.setTimeStampString(attachedDate);

				aList.add(attach);
			}

		} catch (Exception E) {
			E.printStackTrace();
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return aList;
	}

	/**
		 * update only required fields 
		 * 
		 */

	public static synchronized boolean updateActionStateWithPtmt(ETSIssue issue, Connection conn) throws SQLException, Exception {

		//DbConnect dbConnect = null;
		boolean flg = false;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int rowCount=0;

		try {

			sb.append("update " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1 set ");
			sb.append(" CQ_TRK_ID = ?, ");
			sb.append(" SEQ_NO = ?, ");
			sb.append(" PROBLEM_STATE  = ?, ");
			sb.append(" FIELD_C14      = ?, ");
			sb.append(" FIELD_C15      = ?, ");
			sb.append(" COMM_FROM_CUST = ?, ");
			sb.append(" LAST_USERID    = ?, ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" WHERE ");
			sb.append(" APPLICATION_ID = 'ETS' ");
			sb.append(" and EDGE_PROBLEM_ID = ? ");

			Global.println("updateCommentsWithPtmt qry===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, issue.cq_trk_id);
			pstmt.setInt(2, issue.seq_no);
			pstmt.setString(3, issue.problem_state);
			pstmt.setString(4, issue.field_C14);
			pstmt.setString(5, issue.field_C15);
			pstmt.setString(6, issue.comm_from_cust);
			pstmt.setString(7, issue.last_userid);
			pstmt.setString(8, issue.edge_problem_id);

			rowCount += pstmt.executeUpdate();

		} catch (SQLException e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} catch (Exception e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} finally {

			ETSDBUtils.close(pstmt);

		}

		if(rowCount >0) {
		
		flg = true;
		
		}
		return flg;
	}

	/**
	 * 
	 * @param issue
	 * @param conn
	 * @return
	 */

	public static boolean updateNotifyList(ETSMWIssue issue) throws SQLException, Exception {

		//		DbConnect dbConnect = null;
		boolean flg = false;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		Connection conn=null;
		int rowCount = 0;

		try {

			conn = ETSDBUtils.getConnection();
			
			String etsCcLisStr = AmtCommonUtils.getTrimStr(issue.ets_cclist);
			String lastUserId = AmtCommonUtils.getTrimStr(issue.last_userid);
			String edgeProblemId = AmtCommonUtils.getTrimStr(issue.edge_problem_id);

			sb.append("update " + ISMGTSCHEMA + ".PROBLEM_INFO_USR1 set ");
			sb.append(" ETS_CCLIST=?, ");
			sb.append(" LAST_USERID  = ?, ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" WHERE ");
			sb.append(" APPLICATION_ID = 'ETS' ");
			sb.append(" and EDGE_PROBLEM_ID = ? ");

			Global.println("update CCLIST WithPtmt qry===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsCcLisStr);
			pstmt.setString(2, lastUserId);
			pstmt.setString(3, edgeProblemId);

			rowCount += pstmt.executeUpdate();

		} catch (SQLException e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} catch (Exception e) {

			flg = false;
			printErr(getStackTrace(e));
			throw e;

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		if(rowCount >0) {
			flg = true;
		}
		return flg;

	}
	
	public static String getOwnerUserId(ETSIssue mwIssue) {
		ArrayList ownerList = new ArrayList();
		String ownerUserId="";
		EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
		
		try {
		
		ownerList = mwIssue.probOwnerList;
		
		if(ownerList!=null && !ownerList.isEmpty()) {
		
		etsOwnerInfo=(EtsIssOwnerInfo)ownerList.get(0);
		
		ownerUserId=etsOwnerInfo.getUserEdgeId();
		
		}
		
							
		}
		
		catch(Exception e) {
			
			e.printStackTrace();
		}
							
		return ownerUserId;
	}
	
	public static String getOwnerEmailId(ETSIssue mwIssue) {
			ArrayList ownerList = new ArrayList();
			String ownerEmailId="";
			EtsIssOwnerInfo etsOwnerInfo = new EtsIssOwnerInfo();
		
			try {
		
			ownerList = mwIssue.probOwnerList;
		
			if(ownerList!=null && !ownerList.isEmpty()) {
		
			etsOwnerInfo=(EtsIssOwnerInfo)ownerList.get(0);
		
			ownerEmailId=etsOwnerInfo.getUserEmail();
		
			}
		
							
			}
		
			catch(Exception e) {
			
				e.printStackTrace();
			}
							
			return ownerEmailId;
		}

} //end of class
