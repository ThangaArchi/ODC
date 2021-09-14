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

package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSubscrIssueBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.6";
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsIssSubscrIssueBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
	 * 
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsIssProbInfoUsr1Model subscribeIssue() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrInfo1 = new EtsIssProbInfoUsr1Model();

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		String currentEmailId = getEtsIssObjKey().getEs().gEMAIL;

		StringBuffer sb = new StringBuffer();

		if (!isIssueSrcPMO(edgeProblemId)) {

			ETSMWIssue currentIssue = new ETSMWIssue();

			currentIssue = ETSIssuesManager.getMWIssue(edgeProblemId);

			String notifyStr = currentIssue.ets_cclist;

			if (AmtCommonUtils.isResourceDefined(notifyStr)) {

				sb.append("," + currentEmailId);
				notifyStr = notifyStr + sb.toString();
			} else {

				sb.append(currentEmailId);
				notifyStr = sb.toString();
			}

			//set
			currentIssue.ets_cclist = notifyStr;
			currentIssue.last_userid = getEtsIssObjKey().getEs().gUSERN;
			currentIssue.edge_problem_id = edgeProblemId;

			boolean successsubmit = ETSIssuesManager.updateNotifyList(currentIssue);

			if (successsubmit) {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(0);

			} else {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(ERRINACTION);
			}

		} else {

			ETSIssue pmoIssue = new ETSIssue();

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
			pmoIssue = crPmoDao.getPMOIssueInfoModel(edgeProblemId); //get details from PMO	

			String notifyStr = pmoIssue.ets_cclist;
			notifyStr = notifyStr + sb.toString();

			//	set
			pmoIssue.ets_cclist = notifyStr;
			pmoIssue.last_userid = getEtsIssObjKey().getEs().gUSERN;
			pmoIssue.edge_problem_id = edgeProblemId;

			boolean successsubmit = crPmoDao.updateNotifyList(pmoIssue);

			if (successsubmit) {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(0);

			} else {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(ERRINACTION);
			}

		}

		return usrInfo1;

	}

	/**
		 * 
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model unSubscribeIssue() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrInfo1 = new EtsIssProbInfoUsr1Model();

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		String currentEmailId = getEtsIssObjKey().getEs().gEMAIL;

		StringBuffer sb = new StringBuffer();

		if (!isIssueSrcPMO(edgeProblemId)) {

			ETSMWIssue currentIssue = new ETSMWIssue();

			currentIssue = ETSIssuesManager.getMWIssue(edgeProblemId);

			String notifyStr = currentIssue.ets_cclist;

			if (AmtCommonUtils.isResourceDefined(notifyStr)) {

				//notify list
				String notifylist = AmtCommonUtils.getTrimStr(currentIssue.ets_cclist);
				ArrayList prevNotifyList = new ArrayList();
				prevNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(notifylist, ",");

				if (prevNotifyList.contains(currentEmailId)) {

					prevNotifyList.remove(currentEmailId);
				}

				notifyStr = EtsIssFilterUtils.getCommSepStrFromStrList(prevNotifyList);

			}

			//set
			currentIssue.ets_cclist = notifyStr;
			currentIssue.last_userid = getEtsIssObjKey().getEs().gUSERN;
			currentIssue.edge_problem_id = edgeProblemId;

			boolean successsubmit = ETSIssuesManager.updateNotifyList(currentIssue);

			if (successsubmit) {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(0);

			} else {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(ERRINACTION);
			}

		} else {

			ETSIssue pmoIssue = new ETSIssue();

			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
			pmoIssue = crPmoDao.getPMOIssueInfoModel(edgeProblemId); //get details from PMO	

			//notify list
			String notifyStr = AmtCommonUtils.getTrimStr(pmoIssue.ets_cclist);

			if (AmtCommonUtils.isResourceDefined(notifyStr)) {

				ArrayList prevNotifyList = new ArrayList();
				prevNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(notifyStr, ",");

				if (prevNotifyList.contains(currentEmailId)) {

					prevNotifyList.remove(currentEmailId);
				}

				notifyStr = EtsIssFilterUtils.getCommSepStrFromStrList(prevNotifyList);

			}

			//	set
			pmoIssue.ets_cclist = notifyStr;
			pmoIssue.last_userid = getEtsIssObjKey().getEs().gUSERN;
			pmoIssue.edge_problem_id = edgeProblemId;

			boolean successsubmit = crPmoDao.updateNotifyList(pmoIssue);

			if (successsubmit) {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(0);

			} else {

				//set all states
				usrInfo1.setErrMsg("");
				usrInfo1.setCurrentActionState(currentstate);
				usrInfo1.setCancelActionState(0);
				usrInfo1.setNextActionState(ERRINACTION);
			}

		}

		return usrInfo1;

	}

} //end of class
