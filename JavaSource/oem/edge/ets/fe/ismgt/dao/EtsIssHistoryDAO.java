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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssHistoryModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssHistoryDAO implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.9.1.16";

	/**
	 * 
	 */
	public EtsIssHistoryDAO() {
		super();

	}

	/**
	 * to get the Issue History object list from DB
	 * 
	 */

	public ArrayList getIssueHistoryObjList(String edgeProblemId, String sortColumn, String sortOrder, String issueSource) throws SQLException, Exception {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		ArrayList histList = new ArrayList();
		String chkIssueSource = "";

		try {

			chkIssueSource = AmtCommonUtils.getTrimStr(issueSource);

			if (AmtCommonUtils.isResourceDefined(chkIssueSource)) {

				if (chkIssueSource.equals(ETSPMOSOURCE)) {

					sb.append("SELECT A.ACTION_TS,A.USER_NAME AS USERNAME,A.ACTION_NAME,A.ISSUE_STATE");
					sb.append(" FROM ");
					sb.append(" ETS.PMO_ISSUE_HISTORY A");

				} else {

					sb.append("SELECT A.ACTION_TS,A.USER_NAME,A.ACTION_NAME,A.ISSUE_STATE,");
					sb.append(" (SELECT RTRIM(X.USER_FULLNAME) FROM AMT.USERS X WHERE X.EDGE_USERID=A.USER_NAME) as USERNAME");
					sb.append(" FROM ");
					sb.append(" "+ISMGTSCHEMA+".ISSUE_HISTORY A");
				}

			} else {

				sb.append("SELECT A.ACTION_TS,A.USER_NAME,A.ACTION_NAME,A.ISSUE_STATE,");
				sb.append(" (SELECT RTRIM(X.USER_FULLNAME) FROM AMT.USERS X WHERE X.EDGE_USERID=A.USER_NAME) as USERNAME");
				sb.append(" FROM ");
				sb.append(" "+ISMGTSCHEMA+".ISSUE_HISTORY A");

			}

			sb.append(" WHERE");
			sb.append(" A.EDGE_PROBLEM_ID='" + edgeProblemId + "' ");

			sb.append(" ORDER BY ");

			if (AmtCommonUtils.isResourceDefined(sortColumn)) {

				sb.append(" " + sortColumn + " ");

			} else {

				sb.append(" ACTION_TS ");
			}

			if (AmtCommonUtils.isResourceDefined(sortOrder)) {

				sb.append(" " + sortOrder + " ");

			}

			sb.append(" WITH UR");

			Global.println("HISTORY QUERY FOR VIEW ISSUE====" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsIssHistoryModel histModel = new EtsIssHistoryModel();

					histModel.setActionTs(AmtCommonUtils.getTrimStr(rs.getString("ACTION_TS")));
					histModel.setUserName(AmtCommonUtils.getTrimStr(rs.getString("USERNAME")));
					histModel.setActionName(AmtCommonUtils.getTrimStr(rs.getString("ACTION_NAME")));
					histModel.setIssueState(AmtCommonUtils.getTrimStr(rs.getString("ISSUE_STATE")));

					histList.add(histModel);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return histList;

	}

} //end of class
