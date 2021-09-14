/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2005                                     */
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
import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.*;


/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSearchByNumDAO implements EtsIssFilterConstants {

	public static final String VERSION = "1.2";
	private static Log logger = EtsLogger.getLogger(EtsIssSearchByNumDAO.class);

	/**
	 * 
	 */
	public EtsIssSearchByNumDAO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @author V2PHANI
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

	/**
	 * To get rec count for srch num
	 */

	public int getRecCountForSrchNum(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int pmocount = 0;
		int gencount = 0;

		gencount = getRecCountForSrchNumForGen(conn, searchNum, projectId);

		pmocount = getRecCountForSrchNumForPMO(conn, searchNum, projectId);

		if (pmocount == 0 && gencount == 0) {

			return 0;

		} else if (pmocount == 0 && gencount == 1) {

			return 1;

		} else if (pmocount == 1 && gencount == 0) {

			return 1;

		} else if (pmocount == 1 && gencount == 1) {

			return 2;

		} else if (pmocount > 1 || gencount > 1) {

			return 2;
		}

		return 0;
	}

	/**
		 * To get rec count for srch num
		 */

	public int getRecCountForSrchNumForGen(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(edge_problem_id) from ets.problem_info_cq1 ");
		sb.append(" where");
		sb.append(" cq_trk_id like '%" + searchNum + "%' ");
		sb.append(" and project_id='" + projectId + "' ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("getRecCountForSrchNumForGen===" + sb.toString());
		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;
	}

	/**
		 * To get rec count for srch num
		 */

	public int getRecCountForSrchNumForPMO(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append(" select count(ets_id) from ets.pmo_issue_info ");
		sb.append(" where");
		sb.append(" char(ref_no) like '%" + searchNum + "%' ");
		sb.append(" and pmo_project_id=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("getRecCountForSrchNumForPMO===" + sb.toString());
		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		return count;
	}

	/**
	 * To get rec count for srch num
	 */

	public int getRecCountForSrchNum(String searchNum, String projectId) throws SQLException, Exception {

		Connection conn = null;

		int count = 0;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			count = getRecCountForSrchNum(conn, searchNum, projectId);

		} finally {

			ETSDBUtils.close(conn);

		}

		return count;
	}

	/**
		 * To get rec count for srch num
		 */

	public boolean isSearchNumPMO(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append(" select count(ets_id) from ets.pmo_issue_info ");
		sb.append(" where char(ref_no) like '%" + searchNum + "%' ");
		sb.append(" and pmo_project_id=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("isSearchNumPMO qry===" + sb.toString());
		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count == 1) {

			return true;

		} else {

			return false;
		}

	}

	/**
			 * To get rec count for srch num
			 */

	public boolean isSearchNumReg(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		int count = 0;

		sb.append("select count(edge_problem_id) from ets.problem_info_cq1 ");
		sb.append(" where cq_trk_id like '%" + searchNum + "%' ");
		sb.append(" and project_id='" + projectId + "' ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("isSearchNumReg qry===" + sb.toString());
		}

		count = AmtCommonUtils.getRecCount(conn, sb.toString());

		if (count == 1) {

			return true;

		} else {

			return false;
		}

	}

	/**
		 * To get rec count for srch num
		 */

	public String getProblemIdForSrchNum(String searchNum, String projectId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		String edgeProblemId = "";

		Connection conn = null;

		int count = 0;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			if (!isSearchNumPMO(conn, searchNum, projectId)) {

				edgeProblemId = getProblemIdForSrchNumFromReg(conn, searchNum, projectId);

			} else {

				edgeProblemId = getProblemIdForSrchNumFromPMO(conn, searchNum, projectId);
			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return edgeProblemId;
	}

	/**
	 * To get rec count for srch num
	 */

	public String getProblemIdForSrchNumFromReg(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		String edgeProblemId = "";

		sb.append("select edge_problem_id from ets.problem_info_cq1 ");
		sb.append(" where cq_trk_id like '%" + searchNum + "%' ");
		sb.append(" and project_id='" + projectId + "' ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("getProblemIdForSrchNumFromReg qry===" + sb.toString());
		}

		edgeProblemId = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		return edgeProblemId;
	}

	/**
		 * To get rec count for srch num
		 */

	public String getProblemIdForSrchNumFromPMO(Connection conn, String searchNum, String projectId) throws SQLException {

		StringBuffer sb = new StringBuffer();
		String etsId = "";

		sb.append(" select ets_id from ets.pmo_issue_info ");
		sb.append(" where char(ref_no) like '%" + searchNum + "%' ");
		sb.append(" and pmo_project_id=(select pmo_project_id from ets.ets_projects where project_id='" + projectId + "') ");
		sb.append(" for read only");

		if (logger.isDebugEnabled()) {

			logger.debug("getProblemIdForSrchNumFromPMO qry===" + sb.toString());
		}

		etsId = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		return etsId;
	}

} //end of class
