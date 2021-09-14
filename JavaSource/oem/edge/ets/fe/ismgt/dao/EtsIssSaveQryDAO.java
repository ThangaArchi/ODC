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

package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssSaveQryModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSaveQryDAO implements EtsIssueConstants, EtsIssFilterConstants {

	public static final String VERSION = "1.13";

	/**
	 * 
	 */
	public EtsIssSaveQryDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 *  To add new save qry 
	 * 
	 */

	public synchronized boolean insertUserSaveQry(EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		boolean flg = false;
		int rowCount = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {

			sb.append("INSERT INTO " + ISMGTSCHEMA + ".ISSUE_SAVE_QUERIES (EDGE_USER_ID, PROJECT_ID, QUERY_VIEW, QUERY_NAME, QUERY_COMMENT, QUERY_SQL, LAST_USER, LAST_TIMESTAMP)");
			sb.append(" VALUES(?,?,?,?,?,?,?,current timestamp)");

			conn = ETSDBUtils.getConnection();

			conn.setAutoCommit(false);

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, AmtCommonUtils.getTrimStr(saveQryModel.getEdgeUserId()));
			pstmt.setString(2, AmtCommonUtils.getTrimStr(saveQryModel.getProjectId()));
			pstmt.setString(3, AmtCommonUtils.getTrimStr(saveQryModel.getQueryView()));
			pstmt.setString(4, AmtCommonUtils.getTrimStr(saveQryModel.getQueryName()));
			pstmt.setString(5, AmtCommonUtils.getTrimStr(saveQryModel.getQueryComment()));
			pstmt.setString(6, AmtCommonUtils.getTrimStr(saveQryModel.getQuerySql()));
			pstmt.setString(7, AmtCommonUtils.getTrimStr(saveQryModel.getLastUserId()));

			if (!isUserSaveQryExists(conn, saveQryModel)) {

				rowCount += pstmt.executeUpdate();

			} else {

				//delete and insert
				deleteUserSaveQry(conn, saveQryModel);

				rowCount += pstmt.executeUpdate();

			}

			conn.commit();
			conn.setAutoCommit(true);

			Global.println("insertUserSaveQry::insertCount=" + saveQryModel.getEdgeUserId() + "::" + saveQryModel.getProjectId() + "::" + saveQryModel.getQueryView() + "::" + rowCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSaveQryDAO/insertUserSaveQry", ETSLSTUSR);

			if (conn != null) {

				try {

					conn.rollback(); //roll back all transactions

				} catch (SQLException sqlEx) {

					sqlEx.printStackTrace();
				}

			}

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			throw se;

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSaveQryDAO/insertUserSaveQry", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			throw ex;

		} finally {

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);
		}

		return false;
	}

	/**
		 *  To add new save qry 
		 * 
		 */

	public boolean isUserSaveQryExists(EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		Connection conn = null;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection();
			flag = isUserSaveQryExists(conn, saveQryModel);

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;
	}

	/**
	 * 
	 * To check for the given user, already some search criteria is there for a given project,query view
	 * @param conn
	 * @param saveQryModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isUserSaveQryExists(Connection conn, EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		String edgeUserId = AmtCommonUtils.getTrimStr(saveQryModel.getEdgeUserId());
		String projectId = AmtCommonUtils.getTrimStr(saveQryModel.getProjectId());
		String queryView = AmtCommonUtils.getTrimStr(saveQryModel.getQueryView());

		sb.append("SELECT COUNT(EDGE_USER_ID) FROM " + ISMGTSCHEMA + ".ISSUE_SAVE_QUERIES");
		sb.append(" WHERE");
		sb.append(" EDGE_USER_ID = '" + edgeUserId + "' ");
		sb.append(" AND PROJECT_ID='" + projectId + "' ");
		sb.append(" AND QUERY_VIEW='" + queryView + "' ");
		sb.append(" for read only");

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			return true;

		}

		return false;

	}

	/**
	 * To deete user save qry for userid/project/query view
	 * @param conn
	 * @param saveQryModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public synchronized int deleteUserSaveQry(Connection conn, EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int deleteCount = 0;

		try {

			String edgeUserId = AmtCommonUtils.getTrimStr(saveQryModel.getEdgeUserId());
			String projectId = AmtCommonUtils.getTrimStr(saveQryModel.getProjectId());
			String queryView = AmtCommonUtils.getTrimStr(saveQryModel.getQueryView());

			sb.append("DELETE FROM " + ISMGTSCHEMA + ".ISSUE_SAVE_QUERIES");
			sb.append(" WHERE");
			sb.append(" EDGE_USER_ID = ? ");
			sb.append(" AND PROJECT_ID=? ");
			sb.append(" AND QUERY_VIEW=? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, edgeUserId);
			pstmt.setString(2, projectId);
			pstmt.setString(3, queryView);

			deleteCount = pstmt.executeUpdate();

			Global.println("deleteUserSaveQry::deleteCount=" + edgeUserId + "::" + projectId + "::" + queryView + "::" + deleteCount);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSaveQryDAO/deleteUserSaveQry", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			throw se;

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSaveQryDAO/deleteUserSaveQry", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			throw ex;

		} finally {

			ETSDBUtils.close(pstmt);

		}

		return deleteCount;
	}

	/**
			 *  To get user save qry 
			 * 
			 */

	public String getUserSaveQry(EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		Connection conn = null;
		String userSaveQry = "";

		try {

			conn = ETSDBUtils.getConnection();
			userSaveQry = getUserSaveQry(conn, saveQryModel);

		} finally {

			ETSDBUtils.close(conn);
		}

		return userSaveQry;
	}

	/**
	 * 
	 * To get the user save qry
	 * @param conn
	 * @param saveQryModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public String getUserSaveQry(Connection conn, EtsIssSaveQryModel saveQryModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		String saveQrySql = "";

		String edgeUserId = AmtCommonUtils.getTrimStr(saveQryModel.getEdgeUserId());
		String projectId = AmtCommonUtils.getTrimStr(saveQryModel.getProjectId());
		String queryView = AmtCommonUtils.getTrimStr(saveQryModel.getQueryView());

		sb.append("SELECT RTRIM(QUERY_SQL) FROM " + ISMGTSCHEMA + ".ISSUE_SAVE_QUERIES");
		sb.append(" WHERE");
		sb.append(" EDGE_USER_ID = '" + edgeUserId + "' ");
		sb.append(" AND PROJECT_ID='" + projectId + "' ");
		sb.append(" AND QUERY_VIEW='" + queryView + "' ");
		sb.append(" for read only");

		saveQrySql = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		return saveQrySql;

	}

} //end of class
