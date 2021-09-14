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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssSubscribeIssTypModel;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssTypSubscribeDAO implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.6";

	/**
	 * 
	 */
	public EtsIssTypSubscribeDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
		 * Take all the issue types from drop_down_data table, which are not there in ets.subscribe_issuetype 
		 * @param issTypeModel
		 * @return
		 */

	public ArrayList getSubsAddIssueTypesList(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		ArrayList delList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {

			String qryProjectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String qryIssueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String qryIssueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());

			EtsIssProjectMember submitterProfile = issTypeModel.getSubmitterProfile();
			String qryEdgeUserId = submitterProfile.getUserEdgeId();

			sb.append("select data_id,issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append(" where ");
			sb.append(" issue_class='" + qryIssueClass + "' ");
			sb.append(" and project_id='" + qryProjectId + "' ");
			sb.append(" and active_flag='Y' ");

			if (!qryIssueAccess.equals("IBM")) {

				sb.append(" and issue_access like 'ALL%' ");

			}

			sb.append(" AND DATA_ID NOT IN ");
			sb.append("     (select issue_type_id from ets.subscribe_issuetype ");
			sb.append("        where");
			sb.append("         edge_user_id='" + qryEdgeUserId + "' ");
			sb.append("         and project_id='" + qryProjectId + "' ");
			sb.append("     )");

			sb.append(" order by 2");
			sb.append(" for read only");

			Global.println("ADD SUBS ISSUE TYPE QRY===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setDataId(EtsIssFilterUtils.getTrimStr(rs.getString("DATA_ID")));
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));

					delList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return delList;
	}

	/**
			 * Take all the issue types from drop_down_data table, which are not there in ets.subscribe_issuetype 
			 * @param issTypeModel
			 * @return
			 */

	public ArrayList getSubsDelIssueTypesList(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		ArrayList delList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {

			String qryProjectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String qryIssueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String qryIssueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());

			EtsIssProjectMember submitterProfile = issTypeModel.getSubmitterProfile();
			String qryEdgeUserId = submitterProfile.getUserEdgeId();

			sb.append("select data_id,issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append(" where ");
			sb.append(" issue_class='" + qryIssueClass + "' ");
			sb.append(" and project_id='" + qryProjectId + "' ");
			sb.append(" and active_flag='Y' ");

			if (!qryIssueAccess.equals("IBM")) {

				sb.append(" and issue_access like 'ALL%' ");

			}

			sb.append(" AND DATA_ID  IN ");
			sb.append("     (select issue_type_id from ets.subscribe_issuetype ");
			sb.append("        where");
			sb.append("         edge_user_id='" + qryEdgeUserId + "' ");
			sb.append("         and project_id='" + qryProjectId + "' ");
			sb.append("     )");

			sb.append(" order by 2");
			sb.append(" for read only");

			Global.println("UNSUBS ISSUE TYPE QRY===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setDataId(EtsIssFilterUtils.getTrimStr(rs.getString("DATA_ID")));
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));

					delList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return delList;
	}

	/**
			 * 
			 * @param dropModel
			 * @param conn
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean subsIssueType(ArrayList modelList) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;
		int modelsize = 0;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);

			if (EtsIssFilterUtils.isArrayListDefndWithObj(modelList)) {

				modelsize = modelList.size();
			}

			//insert in a loop

			for (int i = 0; i < modelsize; i++) {

				EtsIssSubscribeIssTypModel subsModel = (EtsIssSubscribeIssTypModel) modelList.get(i);

				if (insertSubsIssType(subsModel, conn)) {

					flag = true;

				}

			} //end of for loop

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

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in subsIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in subsIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
		 * 
		 * @param dropModel
		 * @param conn
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean insertSubsIssType(EtsIssSubscribeIssTypModel subsModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int inscount = 0;
		boolean flag = false;

		try {

			String issueTypeId = subsModel.getIssueTypeId();
			String edgeUserId = subsModel.getEdgeUserId();
			String projectId = subsModel.getProjectId();
			String lastUserId = subsModel.getLastUserId();
			
			Global.println("ISSUTYPEID=="+issueTypeId);
			Global.println("edgeUserId=="+edgeUserId);
			Global.println("projectId=="+projectId);
			Global.println("lastUserId=="+lastUserId);

			sb.append("INSERT INTO " + ISMGTSCHEMA + ".SUBSCRIBE_ISSUETYPE (ISSUE_TYPE_ID, EDGE_USER_ID,PROJECT_ID,LAST_USER,LAST_TIMESTAMP)");
			sb.append("  VALUES(?,?,?,?,current timestamp)");

			Global.println("INSERT SUBS ISSUE TYPE QRY===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, issueTypeId);
			pstmt.setString(2, edgeUserId);
			pstmt.setString(3, projectId);
			pstmt.setString(4, lastUserId);

			if (AmtCommonUtils.isResourceDefined(issueTypeId)) {

				inscount += pstmt.executeUpdate();

			}

			if (inscount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
				 * 
				 * @param dropModel
				 * @param conn
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public boolean unSubsIssueType(ArrayList modelList) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;
		int modelsize=0;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);
			
			if (EtsIssFilterUtils.isArrayListDefndWithObj(modelList)) {

							modelsize = modelList.size();
						}
						
//			insert in a loop

					  for (int i = 0; i < modelsize; i++) {

						  EtsIssSubscribeIssTypModel subsModel = (EtsIssSubscribeIssTypModel) modelList.get(i);			


			if (deleteSubsIssType(subsModel, conn)) {

				flag = true;

			}
			
					  }

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

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in subsIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in subsIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
			 * 
			 * @param dropModel
			 * @param conn
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean deleteSubsIssType(EtsIssSubscribeIssTypModel subsModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int delcount = 0;
		boolean flag = false;

		try {

			String issueTypeId = subsModel.getIssueTypeId();
			String edgeUserId = subsModel.getEdgeUserId();
			String projectId = subsModel.getProjectId();
			String lastUserId = subsModel.getLastUserId();

			sb.append("DELETE FROM " + ISMGTSCHEMA + ".SUBSCRIBE_ISSUETYPE ");
			sb.append("  WHERE");
			sb.append("  EDGE_USER_ID=?");
			sb.append("  AND ISSUE_TYPE_ID=?");
			sb.append("  AND PROJECT_ID=?");

			Global.println("DELETE SUBS ISSUE TYPE QRY===" + sb.toString());

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, edgeUserId);
			pstmt.setString(2, issueTypeId);
			pstmt.setString(3, projectId);

			if (AmtCommonUtils.isResourceDefined(issueTypeId)) {

				delcount += pstmt.executeUpdate();

			}

			if (delcount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
				 * 
				 * @param dropModel
				 * @param conn
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public boolean isUsrSubscrToIssType(EtsIssSubscribeIssTypModel subsModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			if (isUsrSubscrToIssType(subsModel, conn)) {

				flag = true;

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in subsIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in subsIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
				 * Take all the issue types from drop_down_data table, which are not there in ets.subscribe_issuetype 
				 * @param issTypeModel
				 * @return
				 */

	public boolean isUsrSubscrToIssType(EtsIssSubscribeIssTypModel subsModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		boolean flag = false;

		int count = 0;

		String issueTypeId = subsModel.getIssueTypeId();
		String edgeUserId = subsModel.getEdgeUserId();
		String projectId = subsModel.getProjectId();

		sb.append("select count(edge_user_id) from ets.subscribe_issuetype");
		sb.append(" where");
		sb.append(" issue_type_id='" + issueTypeId + "' ");
		sb.append(" and edge_user_id='" + edgeUserId + "' ");
		sb.append(" and project_id='" + projectId + "' ");
		sb.append(" for read only");

		Global.println("COUNT ISSUE SUBSCIRBE TYPE QRY===" + sb.toString());

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count > 0) {

			flag = true;
		}

		return flag;
	}

} //end of class
