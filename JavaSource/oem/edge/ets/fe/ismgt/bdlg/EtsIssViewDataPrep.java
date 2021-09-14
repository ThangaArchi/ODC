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
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsIssTypSubscribeDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsCrViewGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssSubscribeIssTypModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssViewDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.19.1.28";
	private int currentstate = 0;

	/**
	 * 
	 */
	public EtsIssViewDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
		 * 
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsIssProbInfoUsr1Model getIssueViewDetails() throws SQLException, Exception {

		//	get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("EDGE PROBLEM ID====" + edgeProblemId);

		return getIssueViewDetailsWithId(edgeProblemId);
	}
	
	
	/**
			 * 
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
		public EtsIssProbInfoUsr1Model getIssueViewDetailsWithId(String edgeProblemId) throws SQLException, Exception {

			
			Global.println("EDGE PROBLEM ID====" + edgeProblemId);

			if (!isIssueSrcPMO(edgeProblemId)) {

				return getIssueViewDetailsAbs(edgeProblemId);

			} else {

				return getPMOIssueViewDetails(edgeProblemId);
			}

		}


	/**
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssProbInfoUsr1Model getIssueViewDetailsRefreshFiles() throws SQLException, Exception {

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getIssueViewDetailsRefreshFiles===" + edgeProblemId);
		
		String strProjectId=getEtsIssObjKey().getProj().getProjectId();

		if (!isIssueSrcPMO(edgeProblemId)) {

			//	delete all the files,with Y flag
			//int deletecount = ETSIssuesManager.deleteAttachWithNewFlg(ETSAPPLNID, edgeProblemId);
			//6.1.1 migrn to doc repository
			getFileAttachUtils().deleteIssueFilesWithoutStatus(strProjectId,edgeProblemId,"N");

			return getIssueViewDetailsAbs(edgeProblemId);

		} else {

			//delete PMO temp files  also

			return getPMOIssueViewDetails(edgeProblemId);
		}

	}

	/**
		 * 
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsIssProbInfoUsr1Model getPMOIssueViewDetails(String edgeProblemId) throws SQLException, Exception {

		//	create a new model
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();
		
		String projectId=AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId());

		Global.println("edge problem id in getPMOIssueViewDetails===" + edgeProblemId);

		String op = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("op"));

		if (op.equals("60")) {

			//update all old files with O flag

			//update files flag form T >> Y >>E
			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
			int updcount = crIssueDao.updateAttachFilesWithFlg(edgeProblemId, "O");

		}

		//get the issue details from DB
		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
		ETSIssue currentIssue = crPmoDao.getPMOIssueInfoModel(edgeProblemId); //get details from PMO
		currentIssue.ets_project_id = getEtsIssObjKey().getProj().getProjectId();
		String cq_trk_id = AmtCommonUtils.getTrimStr(currentIssue.cq_trk_id);
		//521 fix pk
		String refNo = AmtCommonUtils.getTrimStr(""+currentIssue.refNo);

		//test_case === info_src_flag in only modellling
		String infoSrcFlag = currentIssue.infoSrcFlag;

		///get txn flag
		String txnStatusFlag = crPmoDao.getPmoIssTxnFlag(edgeProblemId);

		//get RTF list
		ArrayList rtfList = crPmoDao.getCrRTFList(cq_trk_id);

		//		get RTF MAP
		HashMap rtfMap = getRtfMap(rtfList);

		////////get updated desciption from RTF
		EtsCrViewGuiUtils crViewUtils = new EtsCrViewGuiUtils();
		String rtfDesc = crViewUtils.getRtfValue(getEtsIssObjKey(), rtfMap, "ets_pmo_iss.RTF.1");
		String rtfComments = crViewUtils.getRtfValue(getEtsIssObjKey(), rtfMap, "ets_pmo_iss.RTF.7");

		Global.println("RTF DESC in PMO VIEW ISSUES DETS=====" + rtfDesc);

		Global.println("RTF COMMENTS in PMO VIEW ISSUES DETS=====" + rtfComments);

		String probCreator = AmtCommonUtils.getTrimStr(currentIssue.problem_creator);
		String custName = AmtCommonUtils.getTrimStr(currentIssue.cust_name);
		String custEmail = AmtCommonUtils.getTrimStr(currentIssue.cust_email);
		String custPhone = AmtCommonUtils.getTrimStr(currentIssue.cust_phone);
		String custCompany = AmtCommonUtils.getTrimStr(currentIssue.cust_company);
		String submiDateStr = AmtCommonUtils.getTrimStr(currentIssue.submitDateStr);
		String dateString = EtsIssFilterUtils.formatDate(submiDateStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy");

		String prob_class = AmtCommonUtils.getTrimStr(currentIssue.problem_class);

		////prob severity
		String prob_severity = AmtCommonUtils.getTrimStr(currentIssue.severity);
		ArrayList prevSevList = new ArrayList(); //add severity to list
		prevSevList.add(prob_severity);

		//////
		String prob_title = AmtCommonUtils.getTrimStr(currentIssue.title);

		//prob type list
		//String prob_type = AmtCommonUtils.getTrimStr(currentIssue.problem_type);

		//		prob type list
		String issueTypeId = AmtCommonUtils.getTrimStr(currentIssue.issueTypeId);
		//
		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		String prob_type = dropDao.getSTDPMOIssueType(projectId);

		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(prob_type);
		////

		String prob_desc = AmtCommonUtils.getTrimStr(currentIssue.problem_desc);

		//if RTF DESC IS THERE, THEN SHOW THAT, INSTEAD OF DESC FROM PMO_ISSUE_INFO TABLE
		if (AmtCommonUtils.isResourceDefined(rtfDesc)) {

			prob_desc = rtfDesc;

		}

		Global.println("FINAL PROBLEM DESC in PMO VIEW ISSUES DETS=====" + prob_desc);

		String prob_state = AmtCommonUtils.getTrimStr(currentIssue.problem_state);

		//get the last user action;

		String userLastAction = AmtCommonUtils.getTrimStr(currentIssue.userLastAction);

		//		//user last action, for prob state
		//		if (getPMOIssueProbStateList().contains(prob_state)) {
		//
		//			userLastAction = getStateActionMapping(prob_state);
		//
		//		} else {
		//
		//			userLastAction = prob_state;
		//		}

		//ISSUE HIDDEN
		String issueSource = AmtCommonUtils.getTrimStr(currentIssue.issue_source);
		//always override with ETSPMO, as sometimes, for issues created at PMO Office
		//wonot get issue_source column, 
		issueSource = ETSPMOSOURCE;
		String issueAccess = AmtCommonUtils.getTrimStr(currentIssue.issue_access);

		//notify list
		String notifylist = AmtCommonUtils.getTrimStr(currentIssue.ets_cclist);
		ArrayList prevNotifyList = new ArrayList();
		prevNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(notifylist, ",");

		//get comments logs
		String comm_from_cust = AmtCommonUtils.getTrimStr(currentIssue.comm_from_cust);

		//trim the ---- before comments///
		//		int lastInx=comm_from_cust.lastIndexOf("-");
		//		
		//		if(lastInx != -1 ) {
		//			
		//			if(AmtCommonUtils.isResourceDefined(comm_from_cust)) {
		//				
		//				comm_from_cust=comm_from_cust.substring(lastInx+1);
		//			}
		//			
		//			
		//		}

		boolean isOwner = false;

		////if owner defined true///
		ArrayList ownerIdList = currentIssue.ownerIdList;
		ArrayList ownerNameList = currentIssue.ownerNameList;

		if (ownerIdList != null && !ownerIdList.isEmpty()) {

			if (ownerIdList.contains(getEtsIssObjKey().getEs().gUSERN)) {

				isOwner = true;

			}

		}

		//	only for ISSUES from ETS, not from PMO 
		if (!infoSrcFlag.equals("P")) { //for ISSUE from ETS

			//get updated state action if txn flag=N/T
			prob_state = getUpdatedStateAction(cq_trk_id, userLastAction, prob_state, txnStatusFlag);

		} else { //for issues from PMO

			if (AmtCommonUtils.isResourceDefined(cq_trk_id) && !AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

			} else if (AmtCommonUtils.isResourceDefined(cq_trk_id) && AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

				//	get updated state action if txn flag=N/T
				prob_state = getUpdatedStateAction(cq_trk_id, userLastAction, prob_state, txnStatusFlag);

			}
		}

		//get the commentary log fix for PMO
		String comm_log_string = AmtCommonUtils.getTrimStr(rtfComments);

		//get history list
		ArrayList histList = getHistoryList(getEtsIssObjKey(), edgeProblemId, issueSource);

		//set all key params to usr1InfoModel
		usr1InfoModel.setApplnId(ETSAPPLNID);
		usr1InfoModel.setEdgeProblemId(edgeProblemId);
		usr1InfoModel.setCqTrkId(cq_trk_id);
		usr1InfoModel.setRefNo(refNo);
		usr1InfoModel.setProbClass(prob_class);
		usr1InfoModel.setIssueSrcPMO(isIssueSrcPMO(edgeProblemId));

		//assign submitter profile
		usr1InfoModel.setProbCreator(probCreator);
		usr1InfoModel.setCustName(custName);
		usr1InfoModel.setCustEmail(custEmail);
		usr1InfoModel.setCustPhone(custPhone);
		usr1InfoModel.setCustCompany(custCompany);
		usr1InfoModel.setSubmissionDate(dateString);

		//set descr params to model
		usr1InfoModel.setPrevProbSevList(prevSevList);
		usr1InfoModel.setProbTitle(prob_title);
		usr1InfoModel.setPrevProbTypeList(prevProbTypeList);
		usr1InfoModel.setProbDesc(prob_desc);
		usr1InfoModel.setProbState(prob_state);

		//set issue source and access
		usr1InfoModel.setIssueSource(issueSource);
		usr1InfoModel.setIssueAccess(issueAccess);
		usr1InfoModel.setIssueType(prob_type);

		//set notify list
		usr1InfoModel.setPrevNotifyList(prevNotifyList);

		//set user last action
		usr1InfoModel.setLastAction(userLastAction);

		//set comm log
		usr1InfoModel.setCommentLogStr(comm_log_string);

		//set commnts frm customer
		usr1InfoModel.setCommFromCust(comm_from_cust);

		//set owner id list
		usr1InfoModel.setOwnerList(ownerIdList);

		//set owner names list
		usr1InfoModel.setOwnerNameList(ownerNameList);

		//set history list
		usr1InfoModel.setHistList(histList);

		//set info src flag
		usr1InfoModel.setInfoSrcFlag(infoSrcFlag);

		//set txn flag
		usr1InfoModel.setTxnStatusFlag(txnStatusFlag);

		//set rtf map
		usr1InfoModel.setRtfMap(rtfMap);

		if (prevNotifyList.contains(getEtsIssObjKey().getEs().gEMAIL)) {

			usr1InfoModel.setUsrIssueSubscribe(false);
		} else {

			usr1InfoModel.setUsrIssueSubscribe(true);

		}

		

		usr1InfoModel.setIssueTypeId(issueTypeId);

		//
		EtsIssSubscribeIssTypModel subsModel = new EtsIssSubscribeIssTypModel();
		subsModel.setIssueTypeId(issueTypeId);
		subsModel.setEdgeUserId(getEtsIssObjKey().getEs().gUSERN);
		subsModel.setProjectId(getEtsIssObjKey().getProj().getProjectId());

		//
		EtsIssTypSubscribeDAO subsDao = new EtsIssTypSubscribeDAO();
		usr1InfoModel.setUsrIssTypSubscribe(subsDao.isUsrSubscrToIssType(subsModel));

		return usr1InfoModel;

	}

} //end of class
