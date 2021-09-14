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

package oem.edge.ets.fe.ismgt.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSStringUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.RemindIssueActionModel;
import oem.edge.ets.fe.ismgt.resources.EtsCrProcessConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.RemindIssueActionConstants;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMOffice;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrPmoDAO implements EtsIssFilterConstants, EtsCrProcessConstants, RemindIssueActionConstants {

	public static final String VERSION = "1.14.2.8";

	private static Log logger = EtsLogger.getLogger(EtsCrPmoDAO.class);

	/**
	 * 
	 */
	public EtsCrPmoDAO() {
		super();

	}

	public ETSPMOffice getPMOfficeObjectDetailForCr(String etsPmoProjectId) throws SQLException, Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		ETSPMOffice pmOffice = new ETSPMOffice();

		try {

			sb.append("SELECT ");
			sb.append(" PMO_ID ");
			sb.append(" FROM ");
			sb.append(" ETS.ETS_PMO_MAIN");
			sb.append(" WHERE");
			sb.append(" TYPE='" + CRPMOTYPE + "' ");
			sb.append(" AND PMO_PROJECT_ID='" + etsPmoProjectId + "' ");
			sb.append(" AND PARENT_PMO_ID='" + etsPmoProjectId + "' ");
			sb.append(" for READ ONLY");

			Global.println("PMO PARENT ID QRY====" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {

					pmOffice.setPMOID(AmtCommonUtils.getTrimStr(rs.getString("PMO_ID")));

				}

			} //if r ! null

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return pmOffice;

	}

	public synchronized boolean addNewCR(EtsCrDbModel crDbModel) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {

			conn = ETSDBUtils.getConnection();

			sb.append("INSERT INTO ETS.PMO_ISSUE_INFO ( ETS_ID, PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, REF_NO, ");
			sb.append(" INFO_SRC_FLAG, SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE, STATE_ACTION, ");
			sb.append(" SUBMITTER_IR_ID, SUBMISSION_DATE,CLASS, TITLE, SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, LAST_USERID, LAST_TIMESTAMP ) ");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current timestamp, ?, ?, ?, ?, ?, ?, ?, current timestamp)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, crDbModel.etsId);
			pstmt.setString(2, crDbModel.pmoId);
			pstmt.setString(3, crDbModel.pmoProjectId);
			pstmt.setString(4, crDbModel.parentPmoId);
			pstmt.setInt(5, crDbModel.refNo);
			pstmt.setString(6, crDbModel.infoSrcFlag);
			pstmt.setString(7, crDbModel.custName);
			pstmt.setString(8, crDbModel.custCompany);
			pstmt.setString(9, crDbModel.custEmail);
			pstmt.setString(10, crDbModel.custPhone);
			pstmt.setString(11, crDbModel.stateAction);
			pstmt.setString(12, crDbModel.probCreator);
			pstmt.setString(13, crDbModel.probClass);
			pstmt.setString(14, crDbModel.probTitle);
			pstmt.setString(15, crDbModel.probSeverity);
			pstmt.setString(16, crDbModel.CRType);
			pstmt.setString(17, crDbModel.probDesc);
			pstmt.setString(18, crDbModel.commFromCust);
			pstmt.setString(19, crDbModel.lastUserId);

			int rowCount = 0;

			if (!isEtsIdExistsInIssueInfo(crDbModel.etsId, conn)) {

				rowCount = pstmt.executeUpdate();

			} else {

				//delete and insert
				deletePmoIssue(conn, crDbModel.etsId);

				rowCount = pstmt.executeUpdate();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		flg = true;
		return flg;

	}

	public boolean isEtsIdExistsInIssueInfo(String etsId) throws SQLException, Exception {

		boolean flg = true;
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();
			flg = isEtsIdExistsInIssueInfo(etsId, conn);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flg;

	}

	public boolean isEtsIdExistsInIssueInfo(String etsId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(ETS_ID) from ETS.PMO_ISSUE_INFO");
		sb.append(" WHERE");
		sb.append(" ETS_ID = '" + etsId + "' ");
		sb.append(" for read only");

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}

	public synchronized int deletePmoIssue(Connection conn, String etsId) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.PMO_ISSUE_INFO WHERE  ETS_ID =? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);

			deleteCount = pstmt.executeUpdate();

			Global.println("deletePmoIssue::deleteCount=" + etsId + "::" + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deleteAttach", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/deleteAttach", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return deleteCount;
	}

	public synchronized int deleteIssueTxn(Connection conn, String etsId) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.ETS_PMO_TXN WHERE  TXN_ID =? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);

			deleteCount = pstmt.executeUpdate();

			Global.println("deletePmoTxn::deleteCount=" + etsId + "::" + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deleteIssueTxn", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/deleteIssueTxn", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return deleteCount;
	}

	public boolean isEtsIdExistsInIssueTxn(String etsId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(TXN_ID) from ETS.ETS_PMO_TXN");
		sb.append(" WHERE");
		sb.append(" TXN_ID = '" + etsId + "' ");
		sb.append(" for read only");

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}

	public synchronized boolean addNewCRTxn(EtsCrDbModel crDbModel) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int rowcount = 0;

		try {

			conn = ETSDBUtils.getConnection();

			rowcount = addNewCRTxn(conn, crDbModel);

			if (rowcount > 0) {

				flg = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		flg = true;
		return flg;

	}

	public synchronized int addNewCRTxn(Connection conn, EtsCrDbModel crDbModel) throws SQLException, Exception {

		boolean flg = false;

		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		String uniqId = "";
		int rowCount = 0;

		try {

			uniqId = EtsIssFilterUtils.getUniqEdgeProblemId(crDbModel.lastUserId);

			sb.append("INSERT INTO ETS.ETS_PMO_TXN ( TXN_ID, PMO_PROJECT_ID, ID, TYPE, FLAG,LAST_TIMESTAMP ) ");
			sb.append(" VALUES (?, ?, ?, ?, ?, current timestamp)");

			Global.println("INSERT INTO TXN TABLE QRY===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, uniqId);
			pstmt.setString(2, crDbModel.pmoProjectId);
			pstmt.setString(3, crDbModel.etsId);
			pstmt.setString(4, crDbModel.CRType);
			pstmt.setString(5, crDbModel.statusFlag);

			if (!isEtsIdExistsInIssueTxn(crDbModel.etsId, conn)) {

				rowCount = pstmt.executeUpdate();

			} else {

				//delete and insert
				deleteIssueTxn(conn, crDbModel.etsId);

				rowCount = pstmt.executeUpdate();

			}

		} finally {

			ETSDBUtils.close(pstmt);

		}

		flg = true;
		return rowCount;

	}

	/**
	 * 
	 * @param etsId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsCrDbModel getPCRInfoModel(String etsId) throws SQLException, Exception {

		EtsCrDbModel crDbModel = new EtsCrDbModel();
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String pmoId = "";

		try {

			sb.append(" select ");
			sb.append(" a.pmo_id as PMOID,  ");
			sb.append(" a.pmo_project_id as PMOPROJID,");
			sb.append(" a.parent_pmo_id as PARENTPMOID,");
			sb.append(" a.ref_no as refno,");
			sb.append(" a.info_src_flag as infosrcflag,");
			sb.append(" a.submitter_name as submittername,  ");
			sb.append(" a.submitter_company as submittercompany, ");
			sb.append(" a.submitter_email as submitteremail, ");
			sb.append(" a.submitter_phone as submitterphone, ");
			sb.append(" a.state_action as userlastaction, ");
			sb.append(" a.submitter_ir_id as submitteririd, ");
			sb.append(" a.submission_date as submissiondate, ");
			sb.append(" a.class as class, ");
			sb.append(" a.title as title, ");
			sb.append(" a.severity as severity, ");
			sb.append(" a.type as crtype, ");
			sb.append(" a.description as description, ");
			sb.append(" a.comm_from_cust as comments, ");
			sb.append(" a.owner_ir_id as owneririd, ");
			sb.append(" a.owner_name as ownername, ");
			sb.append(" a.problem_state as curprobstate ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a ");
			sb.append(" where ");
			sb.append(" a.ets_id='" + etsId + "'  ");
			sb.append(" for read only");

			Global.println("getPCRInfoModel PCR INFO QRY====" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					crDbModel.etsId = etsId;
					pmoId = AmtCommonUtils.getTrimStr(rs.getString("PMOID"));
					crDbModel.pmoId = pmoId;
					crDbModel.pmoProjectId = AmtCommonUtils.getTrimStr(rs.getString("PMOPROJID"));
					crDbModel.parentPmoId = AmtCommonUtils.getTrimStr(rs.getString("PARENTPMOID"));
					crDbModel.refNo = rs.getInt("REFNO");
					crDbModel.infoSrcFlag = rs.getString("INFOSRCFLAG");
					crDbModel.custName = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERNAME"));
					crDbModel.custCompany = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERCOMPANY"));
					crDbModel.custEmail = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTEREMAIL"));
					crDbModel.custPhone = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERPHONE"));
					crDbModel.stateAction = AmtCommonUtils.getTrimStr(rs.getString("USERLASTACTION"));
					crDbModel.probCreator = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERIRID"));
					crDbModel.creationDateStr = AmtCommonUtils.getTrimStr(rs.getString("SUBMISSIONDATE"));
					crDbModel.probClass = AmtCommonUtils.getTrimStr(rs.getString("CLASS"));
					crDbModel.probTitle = AmtCommonUtils.getTrimStr(rs.getString("TITLE"));
					crDbModel.probSeverity = AmtCommonUtils.getTrimStr(rs.getString("SEVERITY"));
					crDbModel.CRType = AmtCommonUtils.getTrimStr(rs.getString("CRTYPE"));
					crDbModel.probDesc = AmtCommonUtils.getTrimStr(rs.getString("DESCRIPTION"));
					crDbModel.commFromCust = AmtCommonUtils.getTrimStr(rs.getString("COMMENTS"));
					crDbModel.ownerIrId = AmtCommonUtils.getTrimStr(rs.getString("OWNERIRID"));
					crDbModel.ownerName = AmtCommonUtils.getTrimStr(rs.getString("OWNERNAME"));
					crDbModel.probState = AmtCommonUtils.getTrimStr(rs.getString("CURPROBSTATE"));

				}

			} //if r ! null

			//get the updated status flag//

			if (crDbModel.infoSrcFlag.equals("P")) {

				//crDbModel.statusFlag = "A";

				crDbModel.statusFlag = getPmoCrTxnFlag(etsId);
			} else {

				crDbModel.statusFlag = getPmoCrTxnFlag(etsId);
			}

			//set all RTF LIST objects
			crDbModel.rtfList = getCrRTFList(pmoId);

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return crDbModel;

	}

	/**
		 * 
		 * @param pmoId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public ArrayList getCrRTFList(String pmoId) throws SQLException, Exception {

		ArrayList rtfList = new ArrayList();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			rtfList = getCrRTFList(conn, pmoId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return rtfList;

	}

	/**
	 * 
	 * @param pmoId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public ArrayList getCrRTFList(Connection conn, String pmoId) throws SQLException, Exception {

		ArrayList rtfList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;

		ETSPMODao pmoDao = new ETSPMODao();

		String pmoProjId = "";
		int rtfId = 1;
		String rtfBlobStr = "";
		String rtfName = "";
		String rtfAliasName = "";

		try {

			sb.append(" select ");
			sb.append(" pmo_id as pmoid, ");
			sb.append(" pmo_project_id as pmoprojid, ");
			sb.append(" rtf_id as rtfid, ");
			sb.append(" rtf_name as rtfname, ");
			sb.append(" rtf_alias_name as aliasname, ");
			sb.append(" length(rtf_blob) as rtflength, ");
			sb.append(" rtf_blob as rtfblob ");
			sb.append(" from  ");
			sb.append(" ets.ets_pmo_rtf ");
			sb.append(" where ");
			sb.append(" parent_pmo_id='" + pmoId + "'  ");
			sb.append(" for read only  ");

			Global.println("getPCRInfoModel PCR INFO QRY====" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

					pmoProjId = AmtCommonUtils.getTrimStr(rs.getString("PMOPROJID"));
					rtfId = rs.getInt("RTFID");

					crRtfModel.setPmoId(AmtCommonUtils.getTrimStr(rs.getString("PMOID")));
					crRtfModel.setPmoProjId(pmoProjId);
					crRtfModel.setRtfId(rtfId);
					crRtfModel.setRtfName(AmtCommonUtils.getTrimStr(rs.getString("RTFNAME")));
					crRtfModel.setRtfAliasName(AmtCommonUtils.getTrimStr(rs.getString("ALIASNAME")));
					crRtfModel.setRtfLength(rs.getInt("RTFLENGTH"));

					InputStream input = rs.getBinaryStream("RTFBLOB");

					crRtfModel.setRtfBlobStr(getBlobStr(input));

					////
					rtfList.add(crRtfModel);

				}

			} //if r ! null

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return rtfList;

	}

	/**
			 * 
			 * @param pmoId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsCrRtfModel getCrRTF(String pmoId) throws SQLException, Exception {

		Connection conn = null;
		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			crRtfModel = getCrRTF(conn, pmoId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return crRtfModel;

	}

	/**
		 * 
		 * @param pmoId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsCrRtfModel getCrRTF(Connection conn, String pmoId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;

		ETSPMODao pmoDao = new ETSPMODao();

		String pmoProjId = "";
		int rtfId = 1;
		String rtfBlobStr = "";

		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		try {

			sb.append(" select ");
			sb.append(" pmo_project_id as pmoprojid, ");
			sb.append(" rtf_id as rtfid, ");
			sb.append(" rtf_name as rtfname, ");
			sb.append(" rtf_alias_name as aliasname, ");
			sb.append(" length(rtf_blob) as rtflength, ");
			sb.append(" rtf_blob as rtfblob ");
			sb.append(" from  ");
			sb.append(" ets.ets_pmo_rtf ");
			sb.append(" where ");
			sb.append(" pmo_id='" + pmoId + "'  ");
			sb.append(" for read only  ");

			Global.println("getCrRTF PCR INFO QRY====" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					pmoProjId = AmtCommonUtils.getTrimStr(rs.getString("PMOPROJID"));
					rtfId = rs.getInt("RTFID");

					crRtfModel.setPmoId(pmoId);
					crRtfModel.setPmoProjId(pmoProjId);
					crRtfModel.setRtfId(rtfId);
					crRtfModel.setRtfName(AmtCommonUtils.getTrimStr(rs.getString("RTFNAME")));
					crRtfModel.setRtfAliasName(AmtCommonUtils.getTrimStr(rs.getString("ALIASNAME")));
					crRtfModel.setRtfLength(rs.getInt("RTFLENGTH"));

					InputStream input = rs.getBinaryStream("RTFBLOB");

					crRtfModel.setRtfBlobStr(getBlobStr(input));

				}

			} //if r ! null

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return crRtfModel;

	}

	public String getBlobStr(InputStream input) throws IOException {

		String sReturn = "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte buf[] = new byte[512];
		int n = 0;
		int total = 0;

		if (input != null) {

			while ((n = input.read(buf)) > 0) {
				total += n;
				out.write(buf, 0, n);
				out.flush();
			}
			input.close();

		} //if input not null

		sReturn = out.toString();

		Global.println("BLOB STR VALUE===" + sReturn);

		return sReturn;

	}

	public synchronized int updateCrTxn(String etsId, String flag) throws SQLException, Exception {

		Connection conn = null;
		Statement stmt = null;
		StringBuffer sb = new StringBuffer();
		int updcount = 0;

		try {

			sb.append("UPDATE ETS.ETS_PMO_TXN ");
			sb.append(" set FLAG='" + flag + "' ,");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" where");
			sb.append(" txn_id='" + etsId + "' ");

			Global.println("updateCrTxn qry===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			stmt = conn.createStatement();
			updcount += stmt.executeUpdate(sb.toString());

		} finally {

			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return updcount;
	}

	public synchronized int updateCrComments(EtsCrProbInfoModel crInfoModel) throws SQLException, Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int updcount = 0;

		String etsId = "";
		String comments = "";
		String lastUserId = "";

		try {

			etsId = AmtCommonUtils.getTrimStr(crInfoModel.getEtsId());
			lastUserId = AmtCommonUtils.getTrimStr(crInfoModel.getLastUserId());
			comments = AmtCommonUtils.getTrimStr(crInfoModel.getCommFromCust());

			sb.append("UPDATE ETS.PMO_ISSUE_INFO ");
			sb.append(" set ");
			sb.append(" COMM_FROM_CUST=?, ");
			sb.append(" LAST_USERID=?,  ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" where");
			sb.append(" ets_id=? ");

			Global.println("updateCrComments qry===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, ETSUtils.escapeString(comments));
			pstmt.setString(2, lastUserId);
			pstmt.setString(3, etsId);

			updcount += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		return updcount;
	}

	public synchronized int updateIssueComments(EtsCrProbInfoModel crInfoModel) throws SQLException, Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int updcount = 0;

		String etsId = "";
		String comments = "";
		String lastUserId = "";
		String problemState = "";

		try {

			etsId = AmtCommonUtils.getTrimStr(crInfoModel.getEtsId());
			lastUserId = AmtCommonUtils.getTrimStr(crInfoModel.getLastUserId());
			comments = AmtCommonUtils.getTrimStr(crInfoModel.getCommFromCust());
			problemState = AmtCommonUtils.getTrimStr(crInfoModel.getProbState());

			sb.append("UPDATE ETS.PMO_ISSUE_INFO ");
			sb.append(" set ");
			sb.append(" STATE_ACTION=?, ");
			sb.append(" PROBLEM_STATE=?, ");
			sb.append(" COMM_FROM_CUST=?, ");
			sb.append(" LAST_USERID=?,  ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" where");
			sb.append(" ets_id=? ");

			Global.println("updateCrComments qry===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, problemState);
			pstmt.setString(2, problemState);
			pstmt.setString(3, ETSUtils.escapeString(comments));
			pstmt.setString(4, lastUserId);
			pstmt.setString(5, etsId);

			updcount += pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		return updcount;
	}

	public synchronized int updateCrCommentsRtf(EtsCrProbInfoModel crInfoModel, HashMap pcrPropMap) throws SQLException, Exception {

		int updcount = 0;

		try {

			String pmo_cri_RTF_DynStr = "ets_pmo_cri.RTF.9"; //for comments
			String pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

			Global.println("RTF ID=====" + pmo_cri_RTF_Id);

			updcount = updateCommentsRtf(crInfoModel, pmo_cri_RTF_Id);

		} finally {

		}

		Global.println("update count of RTF table ===" + updcount);

		return updcount;
	}

	public synchronized int updateIssCommentsRtf(EtsCrProbInfoModel crInfoModel, HashMap pcrPropMap) throws SQLException, Exception {

		int updcount = 0;

		try {

			String pmo_cri_RTF_DynStr = "ets_pmo_iss.RTF.7"; //for comments
			String pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

			Global.println("RTF ID=====" + pmo_cri_RTF_Id);

			updcount = updateCommentsRtf(crInfoModel, pmo_cri_RTF_Id);

		} finally {

		}

		Global.println("update count of RTF table ===" + updcount);

		return updcount;
	}

	public synchronized int updateCommentsRtf(EtsCrProbInfoModel crInfoModel, String pmo_cri_RTF_Id) throws SQLException, Exception {

		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		int updcount = 0;

		String etsId = "";
		String pmoId = "";

		String newCommentStr = "";
		String comments = "";
		String lastUserId = "";

		String commentsRtf = "";
		int intRtfId = 0;

		try {

			Global.println("RTF ID=====" + pmo_cri_RTF_Id);

			if (AmtCommonUtils.isResourceDefined(pmo_cri_RTF_Id)) {

				intRtfId = Integer.parseInt(pmo_cri_RTF_Id);
			}

			etsId = AmtCommonUtils.getTrimStr(crInfoModel.getEtsId());
			pmoId = AmtCommonUtils.getTrimStr(crInfoModel.getPmoId());
			lastUserId = AmtCommonUtils.getTrimStr(crInfoModel.getLastUserId());
			comments = AmtCommonUtils.getTrimStr(crInfoModel.getCommFromCust());

			HashMap rtfMap = crInfoModel.getRtfMap();

			EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

			if (rtfMap != null) {

				Global.println("$$$$$$$rtf MAP NOT NULL");

				crRtfModel = (EtsCrRtfModel) rtfMap.get(pmo_cri_RTF_Id);

				if (crRtfModel != null) {

					commentsRtf = AmtCommonUtils.getTrimStr(crRtfModel.getRtfBlobStr());

					Global.println("COMMENTS RTF===" + commentsRtf);

				}

			}

			if (AmtCommonUtils.isResourceDefined(commentsRtf)) {

				newCommentStr = commentsRtf + "\n" + comments;

				newCommentStr = ETSUtils.escapeString(newCommentStr);

			} else {

				newCommentStr = comments;

				newCommentStr = ETSUtils.escapeString(newCommentStr);

			}

			Global.println("COMMENTS STR===" + newCommentStr);
			Global.println("PARENT PMO ID===" + pmoId);
			Global.println("RTF ID=====" + pmo_cri_RTF_Id);

			sb.append("UPDATE ETS.ETS_PMO_RTF ");
			sb.append(" set ");
			sb.append(" RTF_BLOB=? ,");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" where");
			sb.append(" pmo_id=(select pmo_id from ETS.ETS_PMO_RTF WHERE PARENT_PMO_ID=? AND RTF_ID=? ) ");

			Global.println("updateCrCommentsRtf qry===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			pstmt = conn.prepareStatement(sb.toString());

			///

			ByteArrayInputStream bi = getBlobFromCommentsStr(newCommentStr);

			pstmt.clearParameters();
			pstmt.setBinaryStream(1, bi, newCommentStr.length());
			pstmt.setString(2, pmoId);
			pstmt.setInt(3, intRtfId);

			updcount += pstmt.executeUpdate();

			Global.println("RTF updateCrCommentsRtf COUNT===" + updcount);

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		Global.println("update count of RTF table ===" + updcount);

		return updcount;
	}

	public ByteArrayInputStream getBlobFromCommentsStr(String commentsStr) {

		ByteArrayInputStream bi = new ByteArrayInputStream(commentsStr.getBytes(), 0, commentsStr.length());

		return bi;
	}

	/**
		 * 
		 * @param etsId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public String getPmoCrTxnFlag(String etsId) throws SQLException, Exception {

		return getPmoTxnFlag(etsId, "CHANGEREQUEST");

	}

	/**
			 * 
			 * @param etsId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String getPmoCrTxnFlag(Connection conn, String etsId) throws SQLException, Exception {

		return getPmoTxnFlag(conn, etsId, "CHANGEREQUEST");

	}

	/**
				 * 
				 * @param etsId
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public String getPmoIssTxnFlag(Connection conn, String etsId) throws SQLException, Exception {

		return getPmoTxnFlag(conn, etsId, "ISSUE");

	}

	/**
			 * 
			 * @param etsId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String getPmoIssTxnFlag(String etsId) throws SQLException, Exception {

		return getPmoTxnFlag(etsId, "ISSUE");

	}

	/**
				 * 
				 * @param etsId
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public String getPmoTxnFlag(String etsId, String transType) throws SQLException, Exception {

		Connection conn = null;
		String statusFlag = "";

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			statusFlag = getPmoTxnFlag(conn, etsId, transType);

		} finally {

			ETSDBUtils.close(conn);

		}

		return statusFlag;

	}

	/**
			 * 
			 * @param etsId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String getPmoTxnFlag(Connection conn, String etsId, String transType) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		String statusFlag = "";

		try {

			sb.append(" select ");
			sb.append(" b.flag as statusflag, ");
			sb.append(" b.last_timestamp as lasttimestamp ");
			sb.append(" from ");
			sb.append(" ets.ets_pmo_txn b  ");
			sb.append(" where ");
			sb.append(" b.id='" + etsId + "'  ");
			//sb.append(" and b.type='" + transType + "' "); //not rqeuired 
			sb.append(" order by last_timestamp desc");
			sb.append(" for read only");

			Global.println("getPmoCrTxnFlag PCR INFO QRY====" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {

					statusFlag = AmtCommonUtils.getTrimStr(rs.getString("STATUSFLAG"));

				}

			} //if r ! null

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return statusFlag;

	}

	public synchronized int deletePrevandInsertNewCrTxn(EtsCrDbModel crDbModel) throws SQLException, Exception {

		Connection conn = null;
		int delcount = 0;
		String etsId = "";
		int addcount = 0;

		try {

			etsId = crDbModel.etsId;

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);

			//delete old trans
			delcount = deletePrevCrTxn(conn, etsId);

			//insert a new txn
			addcount = addNewCRTxn(conn, crDbModel);

			conn.commit();

			conn.setAutoCommit(true);

		} catch (SQLException se) {

			if (conn != null) {

				try {

					conn.rollback();

				} catch (SQLException sqlEx) {
					sqlEx.printStackTrace();
				}
			}

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deletePrevandInsertNewCrTxn", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return addcount;

	}

	public synchronized int deletePrevCrTxn(Connection conn, String etsId) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.ETS_PMO_TXN WHERE  ID =? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);

			deleteCount = pstmt.executeUpdate();

			Global.println("deletePrevCrTxn::deleteCount=" + etsId + "::" + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deletePrevCrTxn", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/deletePrevCrTxn", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return deleteCount;
	}

	public synchronized boolean addNewIssue(EtsCrDbModel crDbModel) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {

			conn = ETSDBUtils.getConnection();

			sb.append("INSERT INTO ETS.PMO_ISSUE_INFO ( ETS_ID, PMO_ID, PMO_PROJECT_ID, PARENT_PMO_ID, REF_NO,");
			sb.append(" INFO_SRC_FLAG, SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL, SUBMITTER_PHONE, STATE_ACTION, ");
			sb.append(" SUBMITTER_IR_ID, SUBMISSION_DATE,CLASS, TITLE, SEVERITY, TYPE, DESCRIPTION, COMM_FROM_CUST, LAST_USERID, LAST_TIMESTAMP,ISSUE_ACCESS,ISSUE_SOURCE,ETS_CCLIST,PROBLEM_STATE,ISSUE_TYPE_ID ) ");
			sb.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current timestamp, ?, ?, ?, ?, ?, ?, ?, current timestamp,?,?,?,?,?)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, crDbModel.etsId);
			pstmt.setString(2, crDbModel.pmoId);
			pstmt.setString(3, crDbModel.pmoProjectId);
			pstmt.setString(4, crDbModel.parentPmoId);
			pstmt.setInt(5, crDbModel.refNo);
			pstmt.setString(6, crDbModel.infoSrcFlag);
			pstmt.setString(7, crDbModel.custName);
			pstmt.setString(8, crDbModel.custCompany);
			pstmt.setString(9, crDbModel.custEmail);
			pstmt.setString(10, crDbModel.custPhone);
			pstmt.setString(11, crDbModel.stateAction);
			pstmt.setString(12, crDbModel.probCreator);
			pstmt.setString(13, crDbModel.probClass);
			pstmt.setString(14, crDbModel.probTitle);
			pstmt.setString(15, crDbModel.probSeverity);
			pstmt.setString(16, crDbModel.CRType);
			pstmt.setString(17, crDbModel.probDesc);
			pstmt.setString(18, crDbModel.commFromCust);
			pstmt.setString(19, crDbModel.lastUserId);
			pstmt.setString(20, crDbModel.issueAccess);
			pstmt.setString(21, crDbModel.issueSource);
			pstmt.setString(22, crDbModel.etsCCList);
			pstmt.setString(23, crDbModel.stateAction);
			pstmt.setString(24, crDbModel.issueTypeId);

			int rowCount = 0;

			if (!isEtsIdExistsInIssueInfo(crDbModel.etsId, conn)) {

				rowCount += pstmt.executeUpdate();

			} else {

				//delete and insert
				deletePmoIssue(conn, crDbModel.etsId);

				rowCount += pstmt.executeUpdate();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		flg = true;
		return flg;

	}

	/**
			 * 
			 * @param etsId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public ETSIssue getPMOIssueInfoModel(String etsId) throws SQLException, Exception {

		ETSIssue issue = new ETSIssue();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();
			issue = getPMOIssueInfoModel(conn, etsId);
		} finally {

			ETSDBUtils.close(conn);
		}

		return issue;

	}

	/**
		 * 
		 * @param etsId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public ETSIssue getPMOIssueInfoModel(Connection conn, String etsId) throws SQLException, Exception {

		ETSIssue issue = new ETSIssue();
		EtsCrDbModel crDbModel = new EtsCrDbModel();
		StringBuffer sb = new StringBuffer();
		Statement stmt = null;
		ResultSet rs = null;
		String pmoId = "";

		try {

			sb.append(" select ");
			sb.append(" a.pmo_id as PMOID,  ");
			sb.append(" a.pmo_project_id as PMOPROJID,");
			sb.append(" a.parent_pmo_id as PARENTPMOID,");
			sb.append(" a.ref_no as refno,");
			sb.append(" a.info_src_flag as infosrcflag,");
			sb.append(" a.submitter_name as submittername,  ");
			sb.append(" a.submitter_company as submittercompany, ");
			sb.append(" a.submitter_email as submitteremail, ");
			sb.append(" a.submitter_phone as submitterphone, ");
			sb.append(" a.state_action as userlastaction, ");
			sb.append(" a.submitter_ir_id as submitteririd, ");
			sb.append(" a.submission_date as submissiondate, ");
			sb.append(" a.class as class, ");
			sb.append(" a.title as title, ");
			sb.append(" a.severity as severity, ");
			sb.append(" a.type as crtype, ");
			sb.append(" a.description as description, ");
			sb.append(" a.comm_from_cust as comments, ");
			sb.append(" a.owner_ir_id as owneririd, ");
			sb.append(" a.owner_name as ownername, ");
			sb.append(" a.issue_access as issueaccess, ");
			sb.append(" a.issue_source as issuesource, ");
			sb.append(" a.ets_cclist as etscclist, ");
			sb.append(" a.problem_state as curprobstate, ");
			sb.append(" a.last_userid as lastuserid, ");
			sb.append(" a.issue_type_id as issuetypeid");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a ");
			sb.append(" where ");
			sb.append(" a.ets_id='" + etsId + "'  ");
			sb.append(" for read only");

			Global.println("getPCRInfoModel PCR INFO QRY====" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					crDbModel.etsId = etsId;
					pmoId = AmtCommonUtils.getTrimStr(rs.getString("PMOID"));
					crDbModel.pmoId = pmoId;
					crDbModel.pmoProjectId = AmtCommonUtils.getTrimStr(rs.getString("PMOPROJID"));
					crDbModel.parentPmoId = AmtCommonUtils.getTrimStr(rs.getString("PARENTPMOID"));
					crDbModel.refNo = rs.getInt("REFNO");
					crDbModel.infoSrcFlag = rs.getString("INFOSRCFLAG");
					crDbModel.custName = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERNAME"));
					crDbModel.custCompany = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERCOMPANY"));
					crDbModel.custEmail = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTEREMAIL"));
					crDbModel.custPhone = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERPHONE"));
					crDbModel.stateAction = AmtCommonUtils.getTrimStr(rs.getString("USERLASTACTION"));
					crDbModel.probCreator = AmtCommonUtils.getTrimStr(rs.getString("SUBMITTERIRID"));
					crDbModel.creationDateStr = AmtCommonUtils.getTrimStr(rs.getString("SUBMISSIONDATE"));
					crDbModel.probClass = AmtCommonUtils.getTrimStr(rs.getString("CLASS"));
					crDbModel.probTitle = AmtCommonUtils.getTrimStr(rs.getString("TITLE"));
					crDbModel.probSeverity = AmtCommonUtils.getTrimStr(rs.getString("SEVERITY"));
					crDbModel.CRType = AmtCommonUtils.getTrimStr(rs.getString("CRTYPE"));
					crDbModel.probDesc = AmtCommonUtils.getTrimStr(rs.getString("DESCRIPTION"));
					crDbModel.commFromCust = AmtCommonUtils.getTrimStr(rs.getString("COMMENTS"));
					crDbModel.ownerIrId = AmtCommonUtils.getTrimStr(rs.getString("OWNERIRID"));
					crDbModel.ownerName = AmtCommonUtils.getTrimStr(rs.getString("OWNERNAME"));
					crDbModel.issueAccess = AmtCommonUtils.getTrimStr(rs.getString("ISSUEACCESS"));
					crDbModel.issueSource = AmtCommonUtils.getTrimStr(rs.getString("ISSUESOURCE"));
					crDbModel.etsCCList = AmtCommonUtils.getTrimStr(rs.getString("ETSCCLIST"));
					crDbModel.lastUserId = AmtCommonUtils.getTrimStr(rs.getString("LASTUSERID"));
					crDbModel.probState = AmtCommonUtils.getTrimStr(rs.getString("CURPROBSTATE"));
					crDbModel.issueTypeId = AmtCommonUtils.getTrimStr(rs.getString("ISSUETYPEID"));
				}

			} //if r ! null

			//get the updated status flag//

			if (crDbModel.infoSrcFlag.equals("P")) {

				//crDbModel.statusFlag = "A";

				crDbModel.statusFlag = getPmoIssTxnFlag(conn, etsId);
			} else {

				crDbModel.statusFlag = getPmoIssTxnFlag(conn, etsId);
			}

			//set all RTF LIST objects
			crDbModel.rtfList = getCrRTFList(conn, pmoId);

			//set all infor from EtsCrInfoDbModel >> EtsIssue transform  st//
			issue.application_id = ETSAPPLNID;
			issue.edge_problem_id = crDbModel.etsId;
			issue.cq_trk_id = crDbModel.pmoId;
			issue.refNo = crDbModel.refNo;
			issue.problem_state = crDbModel.probState;
			issue.seq_no = crDbModel.refNo;
			issue.problem_class = crDbModel.probClass;
			issue.title = crDbModel.probTitle;
			issue.severity = crDbModel.probSeverity;
			issue.problem_type = crDbModel.CRType; //issue type
			issue.problem_desc = crDbModel.probDesc;
			issue.problem_creator = crDbModel.probCreator;
			issue.submitDateStr = crDbModel.creationDateStr;
			issue.cust_name = crDbModel.custName;
			issue.cust_email = crDbModel.custEmail;
			issue.cust_phone = crDbModel.custPhone;
			issue.cust_company = crDbModel.custCompany;
			issue.cust_project = crDbModel.pmoProjectId;
			issue.comm_from_cust = crDbModel.commFromCust;
			issue.last_userid = crDbModel.lastUserId;
			issue.ets_cclist = crDbModel.etsCCList;
			issue.issue_access = crDbModel.issueAccess;
			issue.issue_source = crDbModel.issueSource;

			//owner id list
			ArrayList ownerIdList = new ArrayList();
			ownerIdList.add(crDbModel.ownerIrId);

			//owner name list
			ArrayList ownerNameList = new ArrayList();
			ownerNameList.add(crDbModel.ownerName);

			issue.ownerIdList = ownerIdList;
			issue.ownerNameList = ownerNameList;

			issue.infoSrcFlag = crDbModel.infoSrcFlag; //only in modeling test_case === info src flag 

			///////////////////////
			issue.userLastAction = crDbModel.stateAction;
			issue.issueTypeId = crDbModel.issueTypeId;

			///set information to end/////

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return issue;

	}

	/**
		 * 
		 * @param issue
		 * @param conn
		 * @return
		 */

	public boolean updateNotifyList(ETSIssue issue) throws SQLException, Exception {

		//		DbConnect dbConnect = null;
		boolean flg = false;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection();

			String etsCcLisStr = AmtCommonUtils.getTrimStr(issue.ets_cclist);
			String lastUserId = AmtCommonUtils.getTrimStr(issue.last_userid);
			String edgeProblemId = AmtCommonUtils.getTrimStr(issue.edge_problem_id);

			sb.append("update ETS.PMO_ISSUE_INFO set ");
			sb.append(" ETS_CCLIST=?, ");
			sb.append(" LAST_USERID  = ?, ");
			sb.append(" last_timestamp=current timestamp ");
			sb.append(" WHERE ");
			sb.append(" ETS_ID = ? ");

			Global.println("update CCLIST WithPtmt qry===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsCcLisStr);
			pstmt.setString(2, lastUserId);
			pstmt.setString(3, edgeProblemId);

			int rowCount = pstmt.executeUpdate();

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);

		}

		flg = true;
		return flg;

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

	public List getRemindPmoIssueRecs(String projectId, HashMap propMap) throws SQLException, Exception {

		List remindList = new ArrayList();
		Connection conn = null;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			remindList = getRemindPmoIssueRecs(conn, "", propMap);

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

	public List getRemindPmoIssueRecs(Connection conn, String projectId, HashMap propMap) throws SQLException, Exception {

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
			sb.append(" a.ETS_ID as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.severity='" + SEVERITY_CRITICAL + "' ");
			sb.append(" and a.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_CRITICAL) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.PMO_PROJECT_ID=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
			}
			sb.append(" and a.pmo_project_id=d.pmo_project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.class='" + ETSPMOISSUESUBTYPE + "' ");

			/////sev-major
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.ETS_ID as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.severity='" + SEVERITY_MAJOR + "' ");
			sb.append(" and a.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_MAJOR) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.PMO_PROJECT_ID=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
			}
			sb.append(" and a.pmo_project_id=d.pmo_project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.class='" + ETSPMOISSUESUBTYPE + "' ");
			///sev-average
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.ETS_ID as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.severity='" + SEVERITY_AVERAGE + "' ");
			sb.append(" and a.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_AVERAGE) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.PMO_PROJECT_ID=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
			}
			sb.append(" and a.pmo_project_id=d.pmo_project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.class='" + ETSPMOISSUESUBTYPE + "' ");

			//sev-minor	
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.ETS_ID as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.severity='" + SEVERITY_MINOR + "' ");
			sb.append(" and a.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_MINOR) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.PMO_PROJECT_ID=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
			}
			sb.append(" and a.pmo_project_id=d.pmo_project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.class='" + ETSPMOISSUESUBTYPE + "' ");

			//enhancement		
			sb.append(" union ");
			sb.append(" select  ");
			sb.append(" a.ETS_ID as problemid, ");
			sb.append(" a.severity as severity ");
			sb.append(" from ");
			sb.append(" ets.pmo_issue_info a, ets.ets_projects d ");
			sb.append(" where ");
			sb.append(" a.severity='" + SEVERITY_ENHANCE + "' ");
			sb.append(" and a.problem_state in ('Assigned','Rejected') ");
			sb.append(" and a.last_timestamp <= current timestamp - " + propMap.get(REMIND_TIME_STR + SEVERITY_ENHANCE) + " ");
			if (AmtCommonUtils.isResourceDefined(projectId)) {

				sb.append(" and a.PMO_PROJECT_ID=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
			}

			sb.append(" and a.pmo_project_id=d.pmo_project_id ");
			sb.append(" and d.project_status NOT IN ('A','D') ");
			sb.append(" and a.class='" + ETSPMOISSUESUBTYPE + "' ");

			sb.append(" order by 2  ");
			sb.append(" with ur ");

			logger.debug("GET REMIND PMO ISSUES QRY===" + sb.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					RemindIssueActionModel remindModel = new RemindIssueActionModel();

					edgeProblemId = AmtCommonUtils.getTrimStr(rs.getString("PROBLEMID"));
					issue = getPMOIssueInfoModel(conn, edgeProblemId);
					ownerUserId = getOwnerUserId(conn, issue);
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
	 * @param issue
	 * @return
	 */

	public String getOwnerUserId(Connection conn, ETSIssue issue) {

		ArrayList ownerIdList = new ArrayList();
		String ownerUserId = "";
		String irUserId = "";
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		try {

			ownerIdList = issue.ownerIdList;

			if (ownerIdList != null && !ownerIdList.isEmpty()) {

				irUserId = (String) ownerIdList.get(0);

				if (AmtCommonUtils.isResourceDefined(irUserId)) {

					ownerUserId = projDao.getUserObjectIrId(conn, irUserId).gUSERN;
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return ownerUserId;
	}

	/**
		 * 
		 * @param issue
		 * @return
		 */

	public String getOwnerUserId(ETSIssue issue) {

		String ownerUserId = "";
		Connection conn = null;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			ownerUserId = getOwnerUserId(conn, issue);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			ETSDBUtils.close(conn);
		}

		return ownerUserId;
	}

	/**
	 * 
	 * @param issue
	 * @return
	 */

	public String getOwnerEmailId(Connection conn, ETSIssue issue) {

		ArrayList ownerIdList = new ArrayList();
		String ownerUserEmail = "";
		String irUserId = "";
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		try {

			ownerIdList = issue.ownerIdList;

			if (ownerIdList != null && !ownerIdList.isEmpty()) {

				irUserId = (String) ownerIdList.get(0);

				if (AmtCommonUtils.isResourceDefined(irUserId)) {

					ownerUserEmail = projDao.getUserObjectIrId(conn, irUserId).gEMAIL;
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return ownerUserEmail;
	}

	/**
		   * 
		   * @param projectId
		   * @param propMap
		   * @return
		   * @throws SQLException
		   * @throws Exception
		   */

	public String getOwnerEmailId(ETSIssue issue) {

		String ownerEmailId = "";
		Connection conn = null;

		try {

			conn = WrkSpcTeamUtils.getConnection();
			ownerEmailId = getOwnerEmailId(conn, issue);

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			ETSDBUtils.close(conn);
		}

		return ownerEmailId;

	}

} //end of class
