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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSStringUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsCrAttach;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrPmoIssueDocDAO implements EtsIssFilterConstants, EtsCrActionConstants {

	public static final String VERSION = "1.33";

	/**
	 * 
	 */
	public EtsCrPmoIssueDocDAO() {
		super();

	}

	public int getAttachmentCount(String etsId) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		int selectCount = 0;

		try {

			conn = ETSDBUtils.getConnection();

			sb.append(" SELECT  max(DOC_NO)+1 FROM ETS.PMO_ISSUE_DOC ");
			sb.append(" WHERE  ");
			sb.append(" ETS_ID='" + etsId + "' ");
			sb.append(" FOR    READ ONLY");

			Global.println("attach count issue docs qry===" + sb.toString());

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {
					selectCount = rs.getInt(1);
				}

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getAttachmentCount", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getAttachmentCount", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return selectCount;

	}

	public int getAttachmentCountForView(String etsId) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		int selectCount = 0;

		try {

			conn = ETSDBUtils.getConnection();

			sb.append(" SELECT  count(ETS_ID) FROM ETS.PMO_ISSUE_DOC ");
			sb.append(" WHERE  ");
			sb.append(" ETS_ID='" + etsId + "' ");
			sb.append(" FOR    READ ONLY");

			Global.println("attach count for view// issue docs qry===" + sb.toString());

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {
					selectCount = rs.getInt(1);
				}

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getAttachmentCountForView", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getAttachmentCountForView", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return selectCount;

	}

	public int getCRAttachmentCountWithFlag(String etsId, String infoSrcFlag) throws Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		int selectCount = 0;

		try {

			conn = ETSDBUtils.getConnection();

			sb.append(" SELECT  count(ETS_ID) FROM ETS.PMO_ISSUE_DOC ");
			sb.append(" WHERE  ");
			sb.append(" ETS_ID='" + etsId + "' ");
			sb.append(" AND INFO_SRC_FLAG= '" + infoSrcFlag + "' ");
			sb.append(" FOR    READ ONLY");

			Global.println("attach count for view// getCRAttachmentCountForPrevFiles docs qry===" + sb.toString());

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {
					selectCount = rs.getInt(1);
				}

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getCRAttachmentCountForPrevFiles", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getCRAttachmentCountForPrevFiles", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return selectCount;

	}

	public synchronized boolean createCRAttachment(EtsCrAttach attach) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;
		boolean success = false;

		try {
			conn = ETSDBUtils.getConnection();

			conn.setAutoCommit(false);
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO ETS.PMO_ISSUE_DOC (ETS_ID,PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,DOC_NAME,DOC_TYPE,DOC_MIME,DOC_NO,DOC_DESC,DOC_BLOB,INFO_SRC_FLAG, LAST_USERID, LAST_TIMESTAMP ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,current timestamp)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			pstmt.setString(1, ETSStringUtils.trim(attach.getEtsId()));
			pstmt.setString(2, ETSStringUtils.trim(attach.getPmoId()));
			pstmt.setString(3, ETSStringUtils.trim(attach.getPmoProjectId()));
			pstmt.setString(4, ETSStringUtils.trim(attach.getParentPmoId()));
			pstmt.setString(5, ETSStringUtils.trim(attach.getDocName()));
			pstmt.setInt(6, attach.getDocType());
			pstmt.setString(7, ETSStringUtils.trim(attach.getFileMime()));
			pstmt.setInt(8, attach.getDocNo());
			pstmt.setString(9, ETSDBUtils.escapeString(ETSStringUtils.trim(attach.getDocDesc())));

			ByteArrayInputStream bi = new ByteArrayInputStream(attach.getFileData(), 0, (int) attach.getFileSize());
			pstmt.setBinaryStream(10, bi, (int) attach.getFileSize());

			pstmt.setString(11, ETSStringUtils.trim(attach.getInfoSrcFlag()));
			pstmt.setString(12, ETSStringUtils.trim(attach.getLastUserId()));

			//check if doc already exists, otw delete and insert new one with the same name

			int deletecount = 0;

			if (isDocAlreadyExists(attach.getEtsId(), attach.getDocName())) {

				deletecount = deleteAttachWithFileName(attach.getEtsId(), attach.getDocName());
			}

			pstmt.execute();

			count++;
			success = true;

			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException se) {

			try {
				conn.rollback();
			} catch (SQLException e) {

				se.printStackTrace();
			}

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/createCRAttachment", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/createCRAttachment", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		return success;

	}

	public synchronized int deleteAttach(String etsId, int fileNo) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.PMO_ISSUE_DOC WHERE  ETS_ID =? AND DOC_NO=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);
			pstmt.setInt(2, fileNo);

			deleteCount = pstmt.executeUpdate();

			Global.println("file deleted for etsId::fileNo::deleteCount=" + etsId + "::" + fileNo + "::" + deleteCount);

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
			ETSDBUtils.close(dbConnection);
		}

		return deleteCount;
	}

	public synchronized int deleteAttachWithFileTmpFlg(String etsId) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.PMO_ISSUE_DOC WHERE  ETS_ID =? AND INFO_SRC_FLAG=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);
			pstmt.setString(2, "T");

			deleteCount = pstmt.executeUpdate();

			Global.println("file deleted for deleteAttachWithNewFlg etsId::fileNo::deleteCount=" + etsId + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deleteAttachWithFileNewFlg", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/deleteAttachWithFileNewFlg", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return deleteCount;
	}

	public Vector getAttachedFiles(String etsId) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Vector aList = new Vector();
		EtsCrAttach attach = null;

		String pmoId = "";
		String pmoProjectId = "";
		String parentPmoId = "";
		String docName = "";
		int docType = 0;
		String docDesc = "";
		String infoSrcFlag = "";
		String attachedBy = "";
		long docSize = 0;
		String docMime = "";
		int docNo = 0;
		String lastTimeStampStr = "";
		String irUserId = "";
		String userName = "";

		try {

			conn = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT  ");
			sb.append(" A.ETS_ID, A.PMO_ID, A.PMO_PROJECT_ID, A.PARENT_PMO_ID, A.DOC_NAME, A.DOC_TYPE, A.DOC_MIME,A.DOC_NO,");
			sb.append(" A.DOC_DESC, A.DOC_BLOB, LENGTH(A.DOC_BLOB) as DOCSIZE,A.INFO_SRC_FLAG, A.LAST_USERID, A.LAST_TIMESTAMP,");
			sb.append(" B.IR_USERID,B.USER_FULLNAME ");
			sb.append(" FROM ETS.PMO_ISSUE_DOC A, AMT.USERS B ");
			sb.append(" WHERE ");
			sb.append(" A.ETS_ID=? ");
			sb.append(" AND A.LAST_USERID=B.EDGE_USERID ");
			sb.append(" ORDER BY A.LAST_TIMESTAMP");
			//CSR UR
			//sb.append(" FOR READ ONLY");
			sb.append(" WITH UR");

			Global.println("Qry for getting attached files list===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();

			pstmt.setString(1, etsId);

			rs = pstmt.executeQuery();

			if (rs != null) {

				while (rs.next()) {

					attach = new EtsCrAttach();

					pmoId = AmtCommonUtils.getTrimStr(rs.getString("PMO_ID"));
					pmoProjectId = AmtCommonUtils.getTrimStr(rs.getString("PMO_PROJECT_ID"));
					parentPmoId = AmtCommonUtils.getTrimStr(rs.getString("PARENT_PMO_ID"));
					docName = AmtCommonUtils.getTrimStr(rs.getString("DOC_NAME"));
					docType = rs.getInt("DOC_TYPE");
					docDesc = AmtCommonUtils.getTrimStr(rs.getString("DOC_DESC"));
					infoSrcFlag = AmtCommonUtils.getTrimStr(rs.getString("INFO_SRC_FLAG"));
					attachedBy = AmtCommonUtils.getTrimStr(rs.getString("LAST_USERID"));
					docSize = rs.getInt("DOCSIZE");
					docMime = AmtCommonUtils.getTrimStr(rs.getString("DOC_MIME"));
					docNo = rs.getInt("DOC_NO");
					lastTimeStampStr = AmtCommonUtils.getTrimStr(rs.getString("LAST_TIMESTAMP"));
					irUserId = AmtCommonUtils.getTrimStr(rs.getString("IR_USERID"));
					userName = AmtCommonUtils.getTrimStr(rs.getString("USER_FULLNAME"));

					attach.setEtsId(etsId);
					attach.setPmoId(pmoId);
					attach.setPmoProjectId(pmoProjectId);
					attach.setParentPmoId(parentPmoId);
					attach.setDocName(docName);
					attach.setDocType(docType);
					attach.setDocDesc(docDesc);
					attach.setInfoSrcFlag(infoSrcFlag);
					attach.setFileSize(docSize);
					attach.setFileMime(docMime);
					attach.setDocNo(docNo);
					attach.setLastUserId(attachedBy);
					attach.setLastUserIrId(irUserId);
					attach.setLastUserName(userName);
					attach.setTimeStampString(EtsIssFilterUtils.formatDate(lastTimeStampStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy"));

					aList.add(attach);
				}

			} //if rs != null

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		return aList;
	}

	public void viewCRAttach(HttpServletResponse res, String etsId, int fileNo) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection dbConnection = null;

		try {

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT   DOC_MIME, LENGTH(DOC_BLOB), DOC_NAME, DOC_BLOB ");
			sb.append(" FROM     ETS.PMO_ISSUE_DOC ");
			sb.append(" WHERE    ");
			sb.append(" ETS_ID=? ");
			sb.append(" AND  DOC_NO=? ");
			sb.append(" FOR READ ONLY");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.setString(1, ETSStringUtils.trim(etsId));
			pstmt.setInt(2, fileNo);

			rset = pstmt.executeQuery();

			if (rset != null) {

				while (rset.next()) {

					String mime = rset.getString(1);
					mime = ETSStringUtils.trim(mime);

					int length = rset.getInt(2);

					res.setContentLength(length);

					String filename = rset.getString(3);
					filename = ETSStringUtils.trim(filename);
					String browserMime = mime;
					
					Global.println("BROWSER MIME TYPE FOR CR==="+browserMime);

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

			}

		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

	}

	public synchronized int deleteAttachWithFileName(String etsId, String docName) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.PMO_ISSUE_DOC WHERE  ETS_ID =? AND DOC_NAME=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);
			pstmt.setString(2, docName);

			deleteCount = pstmt.executeUpdate();

			Global.println("file deleted for etsId::docName::deleteCount=" + etsId + "::" + docName + "::" + deleteCount);

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
			ETSDBUtils.close(dbConnection);
		}

		return deleteCount;
	}

	public boolean isDocAlreadyExists(String etsId, String docName) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		int selectCount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection();

			sb.append(" SELECT  count(ETS_ID) FROM ETS.PMO_ISSUE_DOC ");
			sb.append(" WHERE  ");
			sb.append(" ETS_ID='" + etsId + "' ");
			sb.append(" AND DOC_NAME='" + docName + "' ");
			sb.append(" FOR    READ ONLY");

			Global.println("isDocAlreadyExists// isDocAlreadyExists qry===" + sb.toString());

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				if (rs.next()) {

					selectCount = rs.getInt(1);

				}

			}

			if (selectCount > 0) {
				flag = true;

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/isDocAlreadyExists", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/isDocAlreadyExists", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}
		return flag;

	}

	public synchronized int updateTempAttachments(String etsId) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int updateCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("UPDATE ETS.PMO_ISSUE_DOC SET INFO_SRC_FLAG='Y' WHERE  ETS_ID =? AND INFO_SRC_FLAG=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);
			pstmt.setString(2, "T");

			updateCount = pstmt.executeUpdate();

			Global.println("files updatedfor etsId::docName::updateCount=" + etsId + ":" + updateCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/updateTempAttachments", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/updateTempAttachments", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return updateCount;
	}

	public synchronized int updateAttachFilesWithFlg(String etsId, String flag) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("UPDATE ETS.PMO_ISSUE_DOC SET INFO_SRC_FLAG = ? WHERE  ETS_ID =? ");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, flag);
			pstmt.setString(2, etsId);

			deleteCount = pstmt.executeUpdate();

			Global.println("file deleted for updateAttachFilesWithFlg etsId::fileNo::deleteCount=" + etsId + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/updateAttachFilesWithFlg", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/updateAttachFilesWithFlg", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return deleteCount;
	}
	
	public synchronized int updateAttachFilesWithNewFlg(String etsId, String oldflag,String newflag) throws SQLException, Exception {

			Connection dbConnection = null;
			PreparedStatement pstmt = null;
			int deleteCount = 0;

			try {

				etsId = ETSStringUtils.trim(etsId);

				dbConnection = ETSDBUtils.getConnection();

				StringBuffer sb = new StringBuffer(150);
				sb.append("UPDATE ETS.PMO_ISSUE_DOC SET INFO_SRC_FLAG = ? WHERE  ETS_ID =? and INFO_SRC_FLAG= ?");

				pstmt = dbConnection.prepareStatement(sb.toString());

				pstmt.clearParameters();

				pstmt.setString(1, newflag);
				pstmt.setString(2, etsId);
				pstmt.setString(3, oldflag);

				deleteCount = pstmt.executeUpdate();

				Global.println("file deleted for updateAttachFilesWithNewFlg etsId::fileNo::deleteCount=" + etsId + "::" + deleteCount);

			} catch (SQLException se) {

				AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/updateAttachFilesWithNewFlg", ETSLSTUSR);

				if (se != null) {
					SysLog.log(SysLog.ERR, this, se);
					se.printStackTrace();

				}

			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/updateAttachFilesWithNewFlg", ETSLSTUSR);

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();

				}

			} finally {

				ETSDBUtils.close(pstmt);
				ETSDBUtils.close(dbConnection);
			}

			return deleteCount;
		}


	public synchronized int deleteAttachWithFileFlg(String etsId, String fileFlag) throws SQLException, Exception {

		Connection dbConnection = null;
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			etsId = ETSStringUtils.trim(etsId);

			dbConnection = ETSDBUtils.getConnection();

			StringBuffer sb = new StringBuffer(150);
			sb.append("DELETE FROM ETS.PMO_ISSUE_DOC WHERE  ETS_ID =? AND INFO_SRC_FLAG=?");

			pstmt = dbConnection.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, etsId);
			pstmt.setString(2, fileFlag);

			deleteCount = pstmt.executeUpdate();

			Global.println("file deleted for deleteAttachWithFileFlg etsId::fileNo::deleteCount=" + etsId + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/deleteAttachWithFileFlg", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/deleteAttachWithFileFlg", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(dbConnection);
		}

		return deleteCount;
	}
	
	public Vector getAttachedFilesWithSrcFlg(String etsId,String infoSrcFlag) {

			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Vector aList = new Vector();
			EtsCrAttach attach = null;

			String pmoId = "";
			String pmoProjectId = "";
			String parentPmoId = "";
			String docName = "";
			int docType = 0;
			String docDesc = "";
			String attachedBy = "";
			long docSize = 0;
			String docMime = "";
			int docNo = 0;
			String lastTimeStampStr = "";
			String irUserId = "";
			String userName = "";

			try {

				conn = ETSDBUtils.getConnection();

				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT  ");
				sb.append(" A.ETS_ID, A.PMO_ID, A.PMO_PROJECT_ID, A.PARENT_PMO_ID, A.DOC_NAME, A.DOC_TYPE, A.DOC_MIME,A.DOC_NO,");
				sb.append(" A.DOC_DESC, A.DOC_BLOB, LENGTH(A.DOC_BLOB) as DOCSIZE,A.INFO_SRC_FLAG, A.LAST_USERID, A.LAST_TIMESTAMP ");
				//sb.append(" B.IR_USERID,B.USER_FULLNAME ");
				sb.append(" FROM ETS.PMO_ISSUE_DOC A");				//sb.append(	", AMT.USERS B ");
				sb.append(" WHERE ");
				sb.append(" A.ETS_ID=? ");
				sb.append(" AND A.INFO_SRC_FLAG=? ");
				//sb.append(" AND A.LAST_USERID=B.EDGE_USERID ");
				sb.append(" ORDER BY A.LAST_TIMESTAMP");
				//CSR UR
				//sb.append(" FOR READ ONLY");
				sb.append(" WITH UR");

				Global.println("Qry for getting attached files list===" + sb.toString());

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.clearParameters();

				pstmt.setString(1, etsId);
				pstmt.setString(2, infoSrcFlag);

				rs = pstmt.executeQuery();

				if (rs != null) {

					while (rs.next()) {

						attach = new EtsCrAttach();

						pmoId = AmtCommonUtils.getTrimStr(rs.getString("PMO_ID"));
						pmoProjectId = AmtCommonUtils.getTrimStr(rs.getString("PMO_PROJECT_ID"));
						parentPmoId = AmtCommonUtils.getTrimStr(rs.getString("PARENT_PMO_ID"));
						docName = AmtCommonUtils.getTrimStr(rs.getString("DOC_NAME"));
						docType = rs.getInt("DOC_TYPE");
						docDesc = AmtCommonUtils.getTrimStr(rs.getString("DOC_DESC"));
						//infoSrcFlag = AmtCommonUtils.getTrimStr(rs.getString("INFO_SRC_FLAG"));
						attachedBy = AmtCommonUtils.getTrimStr(rs.getString("LAST_USERID"));
						docSize = rs.getInt("DOCSIZE");
						docMime = AmtCommonUtils.getTrimStr(rs.getString("DOC_MIME"));
						docNo = rs.getInt("DOC_NO");
						lastTimeStampStr = AmtCommonUtils.getTrimStr(rs.getString("LAST_TIMESTAMP"));
						//irUserId = AmtCommonUtils.getTrimStr(rs.getString("IR_USERID"));
						//userName = AmtCommonUtils.getTrimStr(rs.getString("USER_FULLNAME"));

						attach.setEtsId(etsId);
						attach.setPmoId(pmoId);
						attach.setPmoProjectId(pmoProjectId);
						attach.setParentPmoId(parentPmoId);
						attach.setDocName(docName);
						attach.setDocType(docType);
						attach.setDocDesc(docDesc);
						attach.setInfoSrcFlag(infoSrcFlag);
						attach.setFileSize(docSize);
						attach.setFileMime(docMime);
						attach.setDocNo(docNo);
						attach.setLastUserId(attachedBy);
						//attach.setLastUserIrId(irUserId);
						//attach.setLastUserName(userName);
						attach.setTimeStampString(EtsIssFilterUtils.formatDate(lastTimeStampStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy"));

						aList.add(attach);
					}

				} //if rs != null

			} catch (SQLException se) {

				AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

				if (se != null) {
					SysLog.log(SysLog.ERR, this, se);
					se.printStackTrace();

				}

			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();

				}

			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(pstmt);
				ETSDBUtils.close(conn);
			}

			return aList;
		}
		
		
	public Vector getAttachedFilesWithSrcFlgForComments(String etsId,String infoSrcFlag) {

				Connection conn = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				Vector aList = new Vector();
				EtsCrAttach attach = null;

				String pmoId = "";
				String pmoProjectId = "";
				String parentPmoId = "";
				String docName = "";
				int docType = 0;
				String docDesc = "";
				String attachedBy = "";
				long docSize = 0;
				String docMime = "";
				int docNo = 0;
				String lastTimeStampStr = "";
				String irUserId = "";
				String userName = "";
				EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

				try {

					conn = ETSDBUtils.getConnection();

					StringBuffer sb = new StringBuffer();
					sb.append(" SELECT  ");
					sb.append(" A.ETS_ID, A.PMO_ID, A.PMO_PROJECT_ID, A.PARENT_PMO_ID, A.DOC_NAME, A.DOC_TYPE, A.DOC_MIME,A.DOC_NO,");
					sb.append(" A.DOC_DESC, A.DOC_BLOB, LENGTH(A.DOC_BLOB) as DOCSIZE,A.INFO_SRC_FLAG, A.LAST_USERID, A.LAST_TIMESTAMP ");
					sb.append(" FROM ETS.PMO_ISSUE_DOC A");
					sb.append(" WHERE ");
					sb.append(" A.ETS_ID=? ");
					sb.append(" AND A.INFO_SRC_FLAG=? ");
					sb.append(" ORDER BY A.LAST_TIMESTAMP");
					//CSR UR
					sb.append(" FOR READ ONLY");
					

					Global.println("Qry for getting attached files list in comments===" + sb.toString());

					pstmt = conn.prepareStatement(sb.toString());
					pstmt.clearParameters();

					pstmt.setString(1, etsId);
					pstmt.setString(2, infoSrcFlag);

					rs = pstmt.executeQuery();

					if (rs != null) {

						while (rs.next()) {

							attach = new EtsCrAttach();

							pmoId = AmtCommonUtils.getTrimStr(rs.getString("PMO_ID"));
							pmoProjectId = AmtCommonUtils.getTrimStr(rs.getString("PMO_PROJECT_ID"));
							parentPmoId = AmtCommonUtils.getTrimStr(rs.getString("PARENT_PMO_ID"));
							docName = AmtCommonUtils.getTrimStr(rs.getString("DOC_NAME"));
							docType = rs.getInt("DOC_TYPE");
							docDesc = AmtCommonUtils.getTrimStr(rs.getString("DOC_DESC"));
							attachedBy = AmtCommonUtils.getTrimStr(rs.getString("LAST_USERID"));
							docSize = rs.getInt("DOCSIZE");
							docMime = AmtCommonUtils.getTrimStr(rs.getString("DOC_MIME"));
							docNo = rs.getInt("DOC_NO");
							lastTimeStampStr = AmtCommonUtils.getTrimStr(rs.getString("LAST_TIMESTAMP"));
							
							//get the user object info,rpm fp 521
							UserObject userObj = new UserObject();
							userObj = projMemDao.getUserObject(attachedBy);
							
							irUserId = AmtCommonUtils.getTrimStr(userObj.gIR_USERN);
							userName = AmtCommonUtils.getTrimStr(userObj.gUSER_FULLNAME);

							attach.setEtsId(etsId);
							attach.setPmoId(pmoId);
							attach.setPmoProjectId(pmoProjectId);
							attach.setParentPmoId(parentPmoId);
							attach.setDocName(docName);
							attach.setDocType(docType);
							attach.setDocDesc(docDesc);
							attach.setInfoSrcFlag(infoSrcFlag);
							attach.setFileSize(docSize);
							attach.setFileMime(docMime);
							attach.setDocNo(docNo);
							attach.setLastUserId(attachedBy);
							attach.setLastUserIrId(irUserId);
							attach.setLastUserName(userName);
							attach.setTimeStampString(EtsIssFilterUtils.formatDate(lastTimeStampStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy"));

							aList.add(attach);
						}

					} //if rs != null

				} catch (SQLException se) {

					AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

					if (se != null) {
						SysLog.log(SysLog.ERR, this, se);
						se.printStackTrace();

					}

				} catch (Exception ex) {

					AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrPmoIssueDocDAO/getAttachedFiles", ETSLSTUSR);

					if (ex != null) {
						SysLog.log(SysLog.ERR, this, ex);
						ex.printStackTrace();

					}

				} finally {
					ETSDBUtils.close(rs);
					ETSDBUtils.close(pstmt);
					ETSDBUtils.close(conn);
				}

				return aList;
			}

} //end of class
