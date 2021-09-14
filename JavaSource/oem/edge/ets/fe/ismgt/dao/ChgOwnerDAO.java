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
import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Creator;
import oem.edge.ets.fe.ismgt.model.EtsChgOwnerInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ChgOwnerDAO implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.21";

	/**
	 * 
	 */
	public ChgOwnerDAO() {
		super();

	}

	public synchronized boolean insertCqOwnerUsr(EtsChgOwnerInfoModel ownerInfoNewModel,EtsIssObjectKey etsIssObjKey) throws SQLException, Exception {

		boolean flg = false;
		Connection conn = null;
		StringBuffer sb = new StringBuffer();

		//
		String edgeProblemId = "";
		int seqNo = 4;
		String newOwnerEdgeId = "";
		String newOwnerEmailId = "";
		int rowCount = 0;
		boolean usr1dets = false;

		try {

			conn = ETSDBUtils.getConnection();

			newOwnerEdgeId = ownerInfoNewModel.getOwnerInfo().getUserEdgeId();
			newOwnerEmailId = ownerInfoNewModel.getOwnerInfo().getUserEmail();

			//set conn commit false
			conn.setAutoCommit(false);

			//	update usr1 detaisl
			flg = updateProbUsr1Dets(ownerInfoNewModel, conn,etsIssObjKey);

			//set conn to committ

			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ChgOwnerDAO@@insertCqOwnerUsr", ETSLSTUSR);

			try {

				conn.rollback();
			} catch (SQLException ex) {

				ex.printStackTrace();
			}

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);
		}

		if (rowCount > 0) {

			flg = true;
		}

		return flg;

	}

	public boolean updateProbUsr1Dets(EtsChgOwnerInfoModel ownerInfoNewModel, Connection conn, EtsIssObjectKey etsIssObjKey) throws SQLException, Exception {

		boolean successsubmit = false;

		//get edge problem id
		String edgeProblemId = ownerInfoNewModel.getEdgeProblemId();
		String commentsMsg = "";

		try {

			EtsIssOwnerInfo ownerInfo = ownerInfoNewModel.getOwnerInfo();

			String currentOwnerFullName = ownerInfo.getUserFullName();
			String currentOwnerIbmId = ownerInfo.getUserIrId();

			commentsMsg = "Owner changed to " + currentOwnerFullName + "[IBM ID: " + currentOwnerIbmId + "]";

			//	get the issue details from DB	
			ETSMWIssue issue =  ETSIssuesManager.getMWIssue(edgeProblemId);

			//		NAME AND VAL

			//ETSMWIssue issue = new ETSMWIssue();

			//key details

			issue.seq_no = issue.seq_no + 4; //seq by 4

			issue.problem_state = "Changeowner"; //latest user action

			issue.comm_from_cust = commentsMsg;

			issue.field_C14 = ownerInfoNewModel.getLastUserFirstName();
			issue.field_C15 = ownerInfoNewModel.getLastUserLastName();

			//last user id

			issue.last_userid = ownerInfoNewModel.getLastUserId();
			issue.ownerInfo=ownerInfo;

			try {

				//successsubmit = ETSIssuesManager.updateActionStateWithPtmt(issue,conn);
				IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
				IssMWProcessor mwproc = createMWproc.factoryMethod(etsIssObjKey);
				mwproc.setIssue(issue);
				successsubmit = mwproc.processRequest();

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			Commented by v2sagar for PROBLEM_INFO_USR2
				/*
			try {

				ETSIssuesManager.updateCqTrackId(edgeProblemId, issue.cq_trk_id, 1, ownerInfoNewModel.getLastUserId(), conn);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}*/

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return successsubmit;

	}

} //end of class
