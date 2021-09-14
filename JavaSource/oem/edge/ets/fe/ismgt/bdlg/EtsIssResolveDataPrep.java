/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Creator;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.pmo.ETSPMOffice;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssResolveDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.17.1.30";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;

	/**
	 * Constructor
	 */
	public EtsIssResolveDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
		 * To get the details from DB
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model getProblemInfoDetailsFromDb() throws SQLException, Exception {

		
		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getProblemInfoDetailsFromDb/Resolve===" + edgeProblemId);

		//get the issue details from DB
		
		EtsIssProbInfoUsr1Model usr1InfoModel = getIssueViewDetailsAbs(edgeProblemId);
		
		String cqTrkId=usr1InfoModel.getCqTrkId();

		String dynActionState = getLatestProbState(getEtsIssObjKey());

		if (dynActionState.equals("Resolve") || dynActionState.equals("Reject")) {

			//6.1.1 docs migrtn
			//ETSIssuesManager.updateFileNewFlag(edgeProblemId, cqTrkId, getEtsIssObjKey().getEs().gUSERN);
			//
			getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(), usr1InfoModel.getEdgeProblemId(), "N") ;


		}

				
		return usr1InfoModel;
	}

	/**
				 * This method will model the data required for FE while modifying the problem and send in the usr1 model
				 * in 1st page
				 */

	public EtsIssProbInfoUsr1Model getFirstPageDets() throws SQLException, Exception {

		//		get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getFirstPageDetsForResolve===" + edgeProblemId);

		//	set prev one to null
		setUsr1InfoIntoSessn(null, edgeProblemId);

		EtsIssProbInfoUsr1Model usrInfo1 = new EtsIssProbInfoUsr1Model();

		if (!isIssueSrcPMO(edgeProblemId)) { //for regular issues

			//get the prob info dets from DB		
			usrInfo1 = getProblemInfoDetailsFromDb();

		} else {

			usrInfo1 = getPMOProblemInfoDetails(edgeProblemId);

		}

		//set the CURRENT one with new one
		setUsr1InfoIntoSessn(usrInfo1, edgeProblemId);

		int cancelstate = 5; //starting page for modify issue

		//set all states
		usrInfo1.setErrMsg("");
		usrInfo1.setCurrentActionState(currentstate);
		usrInfo1.setCancelActionState(cancelstate);
		usrInfo1.setNextActionState(0);

		return usrInfo1;

	}

	/**
				 * This method will retrieve the Model from session
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public EtsIssProbInfoUsr1Model getEditFileAttachDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		// 
		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate - 1);
		usrSessnModel.setNextActionState(0);

		return usrSessnModel;

	}

	/**
			 * 
			 * To set the usr1 info into session
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public void setUsr1InfoIntoSessn(EtsIssProbInfoUsr1Model usr1Model, String uniqObjId) {

		getActSessnParams().setSessnProbUsr1InfoModel(usr1Model, uniqObjId);
	}

	/**
		 * To get the Issue from sessn
		 */

	public EtsIssProbInfoUsr1Model getUsr1InfoFromSessn() {

		//	get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		EtsIssProbInfoUsr1Model usr1Model = getActSessnParams().getSessnProbUsr1InfoModel(edgeProblemId);

		return usr1Model;

	}

	public int getCancelActionState() {

		String strcancelstate = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("cancelstate"));
		int cancelstate = 0;

		if (AmtCommonUtils.isResourceDefined(strcancelstate)) {

			cancelstate = Integer.parseInt(strcancelstate);
		}
		return cancelstate;

	}

	/**
			  * 
			  * @return
			  */

	public EtsIssProbInfoUsr1Model getContFileattachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		if (!isIssueSrcPMO(usrSessnModel.getEdgeProblemId())) { //for regular issues

			//update files flag from T > Y
			//ETSIssuesManager.updateFileFlagInUsr2(usrSessnModel.getEdgeProblemId(), getEtsIssObjKey().getEs().gUSERN, "T", "Y");
			//6.1.1 migrn to doc repository
			getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(),usrSessnModel.getEdgeProblemId(),"T","Y");

		} else { //for PMO ISSUES

			//	update files flag form T >> Y >>E
			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
			int updcount = crIssueDao.updateAttachFilesWithNewFlg(usrSessnModel.getEdgeProblemId(), "T", "Y");
		}

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate + 1); //always to state of modify main page==5
		usrSessnModel.setNextActionState(RESOLVEISSUEFIRSTPAGE);

		return usrSessnModel;

	}

	/**
				  * 
				  * @return
				  */

	public EtsIssProbInfoUsr1Model getCancFileattachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		//	get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getIssueViewDetailsRefreshFiles===" + edgeProblemId);

		int deletecount = 0;

		if (!isIssueSrcPMO(edgeProblemId)) { //for regular issues

			//delete all the files,with Y flag
			//deletecount = ETSIssuesManager.deleteAttachWithNewFlg(ETSAPPLNID, edgeProblemId);
			//6.1.1 migrn to doc repository
			getFileAttachUtils().deleteIssueFilesWithoutStatus(getEtsIssObjKey().getProj().getProjectId(),edgeProblemId,"N");

		} else {

			///FOR PMO ISSUES

			//delete all the files currently attached
			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
			deletecount = crIssueDao.deleteAttachWithFileTmpFlg(edgeProblemId);

		}

		Global.println("deletecount in View issue details===" + deletecount);

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate + 1); //always to state of modify main page==5
		usrSessnModel.setNextActionState(RESOLVEISSUEFIRSTPAGE);

		return usrSessnModel;

	}

	/**
				 * 
				 * @return
				 */

	public EtsIssProbInfoUsr1Model deleteFileAttach() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		String edge_problem_id = usrSessnModel.getEdgeProblemId();
		int fileNum = getEtsIssObjKey().getFilenum();
		Global.println("edge problem id===" + edge_problem_id);
		Global.println("file num===" + fileNum);
		String strProjectId=usrSessnModel.getEtsProjId();

		boolean success = true;

		if (!isIssueSrcPMO(edge_problem_id)) { //for regular issues

			try {
				
				
				//6.1.1 migrating to documents repository
				//ETSIssuesManager.deleteAttach("ETS", edge_problem_id, fileNum);
				
				getFileAttachUtils().deleteIssueFile(strProjectId, edge_problem_id, fileNum);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
				success = false;
			}

		} else { //for pmo issues

			try {

				EtsCrPmoIssueDocDAO crDocdao = new EtsCrPmoIssueDocDAO();

				crDocdao.deleteAttach(edge_problem_id, fileNum);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
				success = false;
			}
		}

		int cancelstate = getCancelActionState();

		if (cancelstate == 4) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
			usrSessnModel.setNextActionState(RESOLVEDELETEFILE);

		}

		if (cancelstate > 4) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //retain the old state
			usrSessnModel.setNextActionState(RESOLVEISSUEFIRSTPAGE);

		}

		return usrSessnModel;

	}

	/**
			 * 
			 * @return
			 */

	public EtsIssProbInfoUsr1Model doFileAttach() throws SQLException, Exception {

		
		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		Global.println("file desc===" + (String) getEtsIssObjKey().getParams().get("file_desc"));
		Global.println("file name===" + (String) getEtsIssObjKey().getParams().get("upload_file"));
		String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		Global.println("edge problem id===" + edge_problem_id);
		String attch=AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("attch"));
		Global.println("attch in ITAR===" + attch);

		String fileErrMsg = "";

		if (!AmtCommonUtils.isResourceDefined(fileErrMsg)) {

			if (!isIssueSrcPMO(edge_problem_id)) { //for regular issues
				
				if(!AmtCommonUtils.isResourceDefined(attch)) {

				String errFile[] = getFileAttachUtils().doAttach(getEtsIssObjKey().getRequest());
				fileErrMsg = errFile[1];
				
				}

			} else { //forPMO issues

				//				get pmo project id, for a given project id
				String pmoProjectId = getEtsIssObjKey().getProj().getPmo_project_id();

				//get pmo id based on pmo proj id
				EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
				ETSPMOffice pmoOffice = crPmoDao.getPMOfficeObjectDetailForCr(pmoProjectId);

				//get Parent PMO ID, where type=CRIFolder
				String parentPmoId = pmoOffice.getPMOID();

				EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();
				crInfoModel.setEtsId(usrSessnModel.getEdgeProblemId());
				crInfoModel.setPmoId("");
				crInfoModel.setPmoProjectId(pmoProjectId);
				crInfoModel.setParentPmoId(parentPmoId);
				crInfoModel.setLastUserId(getEtsIssObjKey().getEs().gUSERN);

				String errFile[] = getFileAttachUtils().doAttachForCr(getEtsIssObjKey().getRequest(), crInfoModel);
				fileErrMsg = errFile[1];

			}

		}

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg(fileErrMsg);
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(RESOLVEFILEATTACH);

		return usrSessnModel;

	}

	/**
					 * This method will load step1 details into session and get the step 2 details
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */
	public EtsIssProbInfoUsr1Model getContCommentsDetails() throws SQLException, Exception {

		//			get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadCommentsIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateCommentsFormFields(usrParamsInfo1, getEtsIssObjKey());

		//print error msg
		Global.println("err msg Modify/getContCommentsDetails ()=====" + errMsg);

		//get cancel state or scrn state, 
		int cancelstate = getCancelActionState();

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setCommFromCust(usrParamsInfo1.getCommFromCust()); //add comments

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state
			usrSessnModel.setNextActionState(RESOLVEISSUEFIRSTPAGE);

		} else {

			//add the comments
			//when there are no err msgs

			///
			usrSessnModel.setCommFromCust(usrParamsInfo1.getCommFromCust()); //add comments

			//

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//submit details to DB

			usrSessnModel = getSubmitModIssueDetails();

		} //end of no err msg

		return usrSessnModel;

	}

	/**
						 * This method will load step1 details and check for validations
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */
	public String validateCommentsFormFields(EtsIssProbInfoUsr1Model usrParamsInfo1, EtsIssObjectKey issObjKey) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		int actionKey = issObjKey.getActionkey();

		//cehck for comments

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getCommFromCust())) {

			if (actionKey == 3) {

				errsb.append("Please provide resolution.");
				errsb.append("<br />");

			} else {

				errsb.append("Please provide comments.");
				errsb.append("<br />");

			}

		} else {

			String tempDesc = AmtCommonUtils.getTrimStr(usrParamsInfo1.getCommFromCust());

			if (tempDesc.length() > 32700) {

				if (actionKey == 3) {

					errsb.append("Please provide maximum of 32700 characters for resolution.");
					errsb.append("<br />");

				} else {

					errsb.append("Please provide maximum of 32700 characters for comments.");
					errsb.append("<br />");

				}

			} //end of temp desc > 32690 

		}

		//get from session
		return errsb.toString();

	}

	/**
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsIssProbInfoUsr1Model getSubmitModIssueDetails() throws SQLException, Exception {

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());

		boolean successsubmit = false;

		if (!isIssueSrcPMO(edgeProblemId)) { //for regular issues

			successsubmit = submitUsr1InfoToDbForGenIssues();

		} else {

			successsubmit = submitUsr1InfoToDbForPMO();

		}

		if (!successsubmit) {

			usrSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("An error has occured while processing the issue. Please try after sometime or contact");
			sberr.append(" Customer Connect Help Desk for more help.");

			//set error msg
			getEtsIssObjKey().getRequest().setAttribute("actionerrmsg", sberr.toString());

		} else {

			usrSessnModel.setNextActionState(0);

		}

		//		set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);

		return usrSessnModel;

	}

	/**
			 * submit to db
			 */

	public boolean submitUsr1InfoToDbForGenIssues() {

		boolean successsubmit = false;

		try {

			//get the final issue details

			EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

			///key info

			String applnId = AmtCommonUtils.getTrimStr(usrSessnModel.getApplnId());
			String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());
			String cqTrkId = AmtCommonUtils.getTrimStr(usrSessnModel.getCqTrkId());
			String probState = AmtCommonUtils.getTrimStr(usrSessnModel.getProbState());
			int seq_no = usrSessnModel.getSeqNo();
			String comments = usrSessnModel.getCommFromCust();

			//override with user action
			probState = getLatestProbState(getEtsIssObjKey());

			//NAME AND VAL

			ETSMWIssue issue = getETSMWIssueFromInfoModel(usrSessnModel);

			//key details
			issue.application_id = applnId;
			issue.cq_trk_id = cqTrkId;
			issue.edge_problem_id = edgeProblemId;
			issue.seq_no = seq_no + 4; //seq by 4

			issue.problem_state = probState; //latest user action

			issue.field_C14 = getEtsIssObjKey().getEs().gFIRST_NAME;
			issue.field_C15 = getEtsIssObjKey().getEs().gLAST_NAME;

			//commenst
			issue.comm_from_cust = ETSUtils.escapeString(comments);

			//last user id

			issue.last_userid = getEtsIssObjKey().getEs().gUSERN;

			

			try {

				//successsubmit = ETSIssuesManager.updateCommentsWithPtmt(issue);
				IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
				IssMWProcessor mwproc = createMWproc.factoryMethod(getEtsIssObjKey());
				mwproc.setIssue(issue);
				successsubmit = mwproc.processRequest();

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//	Commented by v2sagar for PROBLEM_INFO_USR2
			/*
			try {				
				ETSIssuesManager.updateCqTrackId(edgeProblemId, cqTrkId, 1, getEtsIssObjKey().getEs().gUSERN);

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

	private String getLatestProbState(EtsIssObjectKey issObjKey) {

		String probState = "Comment";

		int actionKey = issObjKey.getActionkey();

		if (actionKey == 3) {

			probState = "Resolve";

		}

		if (actionKey == 5) {

			probState = "Reject";

		}

		if (actionKey == 6) {

			probState = "Close";

		}

		if (actionKey == 19) {

			probState = "Comment";

		}

		if (actionKey == 23) {

			probState = "Withdraw";

		}

		return probState;
	}

	public boolean submitUsr1InfoToDbForPMO() throws SQLException, Exception {

		//		get the final issue details

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		///key info

		String applnId = AmtCommonUtils.getTrimStr(usrSessnModel.getApplnId());
		String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());
		String cqTrkId = AmtCommonUtils.getTrimStr(usrSessnModel.getCqTrkId());
		String probState = AmtCommonUtils.getTrimStr(usrSessnModel.getProbState());
		int seq_no = usrSessnModel.getSeqNo();
		String comments = usrSessnModel.getCommFromCust();

		//override with user action
		probState = getLatestProbState(getEtsIssObjKey());

		//		get the issue details from DB
		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();

		//get RTF list
		ArrayList rtfList = crPmoDao.getCrRTFList(cqTrkId);

		//		get RTF MAP
		HashMap rtfMap = getRtfMap(rtfList);

		//NAME AND VAL

		EtsCrPmoDAO crDao = new EtsCrPmoDAO();

		//		NAME AND VAL

		EtsCrDbModel crDbModel = new EtsCrDbModel();
		//		get pmo project id, for a given project id
		String pmoProjectId = getEtsIssObjKey().getProj().getPmo_project_id();

		//	get pmo id based on pmo proj id

		ETSPMOffice pmoOffice = crPmoDao.getPMOfficeObjectDetailForCr(pmoProjectId);

		//get Parent PMO ID, where type=CRIFolder
		String parentPmoId = pmoOffice.getPMOID();

		//key details
		crDbModel.etsId = usrSessnModel.getEdgeProblemId();
		crDbModel.pmoId = usrSessnModel.getCqTrkId();
		crDbModel.pmoProjectId = pmoProjectId;
		crDbModel.parentPmoId = parentPmoId;
		crDbModel.refNo = seq_no + 4;
		crDbModel.infoSrcFlag = "E";
		crDbModel.CRType = "ISSUE";

		//last user id

		crDbModel.lastUserId = usrSessnModel.getLastUserId();

		//txn flag
		crDbModel.statusFlag = "U";

		//add comments eneterd by whom on what time??
		StringBuffer comsb = new StringBuffer();
		String userName = getEtsIssObjKey().getEs().gFIRST_NAME + " "+ getEtsIssObjKey().getEs().gLAST_NAME;
		String lastUserIrId = getEtsIssObjKey().getEs().gIR_USERN;
		String dateString = AmtCommonUtils.getDateString("MMM d, yyyy");

		////521 fp
		//comsb.append("--- Comments entered by " + userName + " [IBM ID: " + lastUserIrId + "] on " + dateString + " --------------\n");
		if(AmtCommonUtils.isResourceDefined(lastUserIrId)) {
		
		comsb.append("--- Comment by " + userName + " [" + lastUserIrId + "] on " + dateString + " \n");
		
		}
		//comsb.append("<br /><br />");

		String commFromCust = comsb.toString() + usrSessnModel.getCommFromCust() + "\n";

		///////////
		EtsCrProbInfoModel crInfoModel = new EtsCrProbInfoModel();

		crInfoModel.setEtsId(usrSessnModel.getEdgeProblemId());
		crInfoModel.setPmoId(usrSessnModel.getCqTrkId());
		crInfoModel.setLastUserId(usrSessnModel.getLastUserId());
		crInfoModel.setCommFromCust(commFromCust);
		crInfoModel.setProbState(probState);
		crInfoModel.setRtfMap(rtfMap);

		////////////

		//update pmo_issue_info
		int updinfocount = crDao.updateIssueComments(crInfoModel);

		//	update pmo_rtf
		int updrtfcount = crDao.updateIssCommentsRtf(crInfoModel, getEtsIssObjKey().getPcrPropMap());

		//update txn table
		int txncount = crDao.deletePrevandInsertNewCrTxn(crDbModel);

		Global.println("comments count==" + updinfocount);
		Global.println("comments RTF count==" + updrtfcount);
		Global.println("tnx count count==" + txncount);

		if (updinfocount > 0 && updrtfcount > 0 && txncount > 0) {

			///temp only///

			//if (updinfocount > 0 && txncount > 0) {

			return true;
		}

		return false;

	}

	/**
			 * 
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	/*public EtsIssProbInfoUsr1Model getPMOProblemInfoDetails(String edgeProblemId) throws SQLException, Exception {
	
		//	create a new model
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();
	
		Global.println("edge problem id in getPMOIssueViewDetails===" + edgeProblemId);
	
		//get the issue details from DB
		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
		ETSIssue currentIssue = crPmoDao.getPMOIssueInfoModel(edgeProblemId); //get details from PMO
		currentIssue.ets_project_id = getEtsIssObjKey().getProj().getProjectId();
		String cq_trk_id = AmtCommonUtils.getTrimStr(currentIssue.cq_trk_id);
	
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
		String rtfDesc = crViewUtils.getRtfValue(getEtsIssObjKey(), rtfMap, "ets_pmo_cri.RTF.0");
		String rtfComments = crViewUtils.getRtfValue(getEtsIssObjKey(), rtfMap, "ets_pmo_cri.RTF.9");
	
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
		String prob_type = AmtCommonUtils.getTrimStr(currentIssue.problem_type);
		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(prob_type);
		////
	
		String prob_desc = AmtCommonUtils.getTrimStr(currentIssue.problem_desc);
		String prob_state = AmtCommonUtils.getTrimStr(currentIssue.problem_state);
	
		//		get the last user action;
		String userLastAction = prob_state;
	
		//ISSUE HIDDEN
		String issueSource = AmtCommonUtils.getTrimStr(currentIssue.issue_source);
		//always override with ETSPMO, as sometimes, for issues created at PMO Office
		//wonot get issue_source column, 
		issueSource=ETSPMOSOURCE;
		String issueAccess = AmtCommonUtils.getTrimStr(currentIssue.issue_access);
	
		//notify list
		String notifylist = AmtCommonUtils.getTrimStr(currentIssue.ets_cclist);
		ArrayList prevNotifyList = new ArrayList();
		prevNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(notifylist, ",");
	
		//get comments logs
		String comm_from_cust = AmtCommonUtils.getTrimStr(currentIssue.comm_from_cust);
	
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
			prob_state = getUpdatedStateAction(cq_trk_id, prob_state, txnStatusFlag);
	
		} else { //for issues from PMO
	
			if (AmtCommonUtils.isResourceDefined(cq_trk_id) && !AmtCommonUtils.isResourceDefined(txnStatusFlag)) {
	
			} else if (AmtCommonUtils.isResourceDefined(cq_trk_id) && AmtCommonUtils.isResourceDefined(txnStatusFlag)) {
	
				//	get updated state action if txn flag=N/T
				prob_state = getUpdatedStateAction(cq_trk_id, prob_state, txnStatusFlag);
	
			}
		}
	
		//get the commentary log fix for PMO
		String comm_log_string = AmtCommonUtils.getTrimStr(rtfComments);
	
		//set all key params to usr1InfoModel
		usr1InfoModel.setApplnId(ETSAPPLNID);
		usr1InfoModel.setEdgeProblemId(edgeProblemId);
		usr1InfoModel.setCqTrkId(cq_trk_id);
		usr1InfoModel.setProbClass(prob_class);
	
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
	
		///
	
		//issue type list
		usr1InfoModel.setPrevIssueTypeList(prevProbTypeList);
	
		///
	
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
	
		//set info src flag
		usr1InfoModel.setInfoSrcFlag(infoSrcFlag);
	
		//set txn flag
		usr1InfoModel.setTxnStatusFlag(txnStatusFlag);
	
		//		set first name and last names
		usr1InfoModel.setFieldC14(getEtsIssObjKey().getEs().gFIRST_NAME); //set first names
		usr1InfoModel.setFieldC15(getEtsIssObjKey().getEs().gLAST_NAME); //set last names
	
		usr1InfoModel.setLastUserId(getEtsIssObjKey().getEs().gUSERN); //last user id
		
		//usr1InfoModel.setRtfMap(rtfMap);
	
		return usr1InfoModel;
	
	}*/

	public EtsIssProbInfoUsr1Model getPMOProblemInfoDetails(String edgeProblemId) throws SQLException, Exception {

		//		create a new model
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		Global.println("edge problem id in getPMOIssueViewDetails===" + edgeProblemId);

		EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(getEtsIssObjKey(), currentstate);

		usr1InfoModel = viewDataPrep.getPMOIssueViewDetails(edgeProblemId);

		//		set first name and last names
		usr1InfoModel.setFieldC14(getEtsIssObjKey().getEs().gFIRST_NAME); //set first names
		usr1InfoModel.setFieldC15(getEtsIssObjKey().getEs().gLAST_NAME); //set last names

		usr1InfoModel.setLastUserId(getEtsIssObjKey().getEs().gUSERN); //last user id
		
		//set comments to ""
		usr1InfoModel.setCommFromCust("");

		//usr1InfoModel.setRtfMap(rtfMap);

		return usr1InfoModel;

	}

} //end of class
