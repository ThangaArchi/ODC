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
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Creator;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssDynFieldDataModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssStaticSubmFormDetails;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssModifyDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.40";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;

	/**
	 * Constructor
	 */
	public EtsIssModifyDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
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

		Global.println("edge problem id in getProblemInfoDetailsFromDb/Modify===" + edgeProblemId);

		EtsIssProbInfoUsr1Model usr1InfoModel = getIssueViewDetailsAbs(edgeProblemId);

		String cqTrkId = usr1InfoModel.getCqTrkId();
		String issueAccess = usr1InfoModel.getIssueAccess();
		String issueSource = usr1InfoModel.getIssueSource();

		//set all flag to all old files
		//	set N flag for all old files
		//6.1.1 doc migrtn
		//ETSIssuesManager.updateFileNewFlag(edgeProblemId, cqTrkId, getEtsIssObjKey().getEs().gUSERN);
		
		//
		getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(), usr1InfoModel.getEdgeProblemId(), "N") ;

		String chkIssTypeIbmOnly = "N";

		if (issueAccess.equals("IBM")) {

			chkIssTypeIbmOnly = "Y";
		}

		//	prob type list
		String prob_type = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueType());
		ArrayList prevProbTypeList = new ArrayList();
		String probTypeDelimitStr = prob_type + "$" + issueSource + "$" + issueAccess;
		Global.println("new prob type in Modify issues reframing..===" + probTypeDelimitStr);
		prevProbTypeList.add(probTypeDelimitStr);
		////

		usr1InfoModel.setPrevProbTypeList(prevProbTypeList);

		//issue type list
		ArrayList prevIssueTypeList = new ArrayList();
		prevIssueTypeList.add(prob_type);
		usr1InfoModel.setPrevIssueTypeList(prevIssueTypeList);

		//////////////////
		usr1InfoModel.setChkIssTypeIbmOnly(chkIssTypeIbmOnly);

		return usr1InfoModel;
	}

	/**
		 * To get the details from DB
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model getIssueIdentfDetailsFromDb() throws SQLException, Exception {

		//	create a new model
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getProblemInfoDetailsFromDb===" + edgeProblemId);

		//get the issue details from DB

		ETSIssue currentIssue = ETSIssuesManager.getIssue(edgeProblemId);

		//prev field lists
		ArrayList prevFieldC1List = new ArrayList();
		ArrayList prevFieldC2List = new ArrayList();
		ArrayList prevFieldC3List = new ArrayList();
		ArrayList prevFieldC4List = new ArrayList();
		ArrayList prevFieldC5List = new ArrayList();
		ArrayList prevFieldC6List = new ArrayList();
		ArrayList prevFieldC7List = new ArrayList();
		ArrayList prevFieldC8List = new ArrayList();

		String prevFieldC1Val = AmtCommonUtils.getTrimStr(currentIssue.field_C1);
		String prevFieldC2Val = AmtCommonUtils.getTrimStr(currentIssue.field_C2);
		String prevFieldC3Val = AmtCommonUtils.getTrimStr(currentIssue.field_C3);
		String prevFieldC4Val = AmtCommonUtils.getTrimStr(currentIssue.field_C4);
		String prevFieldC5Val = AmtCommonUtils.getTrimStr(currentIssue.field_C5);
		String prevFieldC6Val = AmtCommonUtils.getTrimStr(currentIssue.field_C6);
		String prevFieldC7Val = AmtCommonUtils.getTrimStr(currentIssue.field_C7);
		String prevFieldC8Val = AmtCommonUtils.getTrimStr(currentIssue.field_C8);
		String testcase = AmtCommonUtils.getTrimStr(currentIssue.test_case);

		//add to lists
		prevFieldC1List.add(prevFieldC1Val);
		prevFieldC2List.add(prevFieldC2Val);
		prevFieldC3List.add(prevFieldC3Val);
		prevFieldC4List.add(prevFieldC4Val);
		prevFieldC5List.add(prevFieldC5Val);
		prevFieldC6List.add(prevFieldC6Val);
		prevFieldC7List.add(prevFieldC7Val);
		prevFieldC8List.add(prevFieldC8Val);
		
		//
		ArrayList prevSubTypeAList = new ArrayList();
		ArrayList prevSubTypeBList = new ArrayList();
		ArrayList prevSubTypeCList = new ArrayList();
		ArrayList prevSubTypeDList = new ArrayList();

		//SUBTYPES
		//prev vals always user reference name
		String prevSubTypeAVal = AmtCommonUtils.getTrimStr(currentIssue.subTypeA);
		String prevSubTypeBVal = AmtCommonUtils.getTrimStr(currentIssue.subTypeB);
		String prevSubTypeCVal = AmtCommonUtils.getTrimStr(currentIssue.subTypeC);
		String prevSubTypeDVal = AmtCommonUtils.getTrimStr(currentIssue.subTypeD);

		//add vals to lists
		prevSubTypeAList.add(prevSubTypeAVal);
		prevSubTypeBList.add(prevSubTypeBVal);
		prevSubTypeCList.add(prevSubTypeCVal);
		prevSubTypeDList.add(prevSubTypeDVal);

		//ISSUE HIDDEN
		String issueSource = AmtCommonUtils.getTrimStr(currentIssue.issue_source);
		String issueAccess = AmtCommonUtils.getTrimStr(currentIssue.issue_access);

		//	prob type list
		String prob_type = AmtCommonUtils.getTrimStr(currentIssue.problem_type);
		ArrayList prevProbTypeList = new ArrayList();
		String probTypeDelimitStr = prob_type + "$" + issueSource + "$" + issueAccess;
		Global.println("new prob type in Modify issues reframing..===" + probTypeDelimitStr);
		prevProbTypeList.add(probTypeDelimitStr);
		////

		usr1InfoModel.setPrevProbTypeList(prevProbTypeList);

		//set field c1..c8 and testcase
		usr1InfoModel.setPrevFieldC1List(prevFieldC1List);
		usr1InfoModel.setPrevFieldC2List(prevFieldC2List);
		usr1InfoModel.setPrevFieldC3List(prevFieldC3List);
		usr1InfoModel.setPrevFieldC4List(prevFieldC4List);
		usr1InfoModel.setPrevFieldC5List(prevFieldC5List);
		usr1InfoModel.setPrevFieldC6List(prevFieldC6List);
		usr1InfoModel.setPrevFieldC7List(prevFieldC7List);
		usr1InfoModel.setPrevFieldC8List(prevFieldC8List);
		usr1InfoModel.setTestCase(testcase);

		//set sub types
		usr1InfoModel.setPrevSubTypeAList(prevSubTypeAList);
		usr1InfoModel.setPrevSubTypeBList(prevSubTypeBList);
		usr1InfoModel.setPrevSubTypeCList(prevSubTypeCList);
		usr1InfoModel.setPrevSubTypeDList(prevSubTypeDList);

		//set issue source and access
		usr1InfoModel.setIssueSource(issueSource);
		usr1InfoModel.setIssueAccess(issueAccess);
		usr1InfoModel.setIssueType(prob_type);
		//issue type list
		ArrayList prevIssueTypeList = new ArrayList();
		prevIssueTypeList.add(prob_type);
		usr1InfoModel.setPrevIssueTypeList(prevIssueTypeList);

		//////////////////

		return usr1InfoModel;
	}

	/**
			 * This method will model the data required for FE while modifying the problem and send in the usr1 model
			 * in 1st page
			 */

	public EtsIssProbInfoUsr1Model getFirstPageDets() throws SQLException, Exception {

		//		get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getFirstPageDetsForModify===" + edgeProblemId);

		//	set prev one to null
		setUsr1InfoIntoSessn(null, edgeProblemId);

		//get the prob info dets from DB		
		EtsIssProbInfoUsr1Model usrInfo1 = getProblemInfoDetailsFromDb();

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

	/**
		 * This method will retrieve the Model from session
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model getEditIssueDescrDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//descModel.set for prev and current  values
		usrSessnModel.setProbSevList(getFilterDAO().getSeverityTypes());

		// 

		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(0);

		return usrSessnModel;

	}

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getNewContDescrDetails() throws SQLException, Exception {

		//			get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadNewScr1ParamsIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateNewScrn1FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg Modify/getNewContDescrDetails ()=====" + errMsg);

		//get cancel state or scrn state, 
		int cancelstate = getCancelActionState();

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//	add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			usrSessnModel.setPrevProbSevList(usrParamsInfo1.getPrevProbSevList()); //add severity
			usrSessnModel.setProbTitle(usrParamsInfo1.getProbTitle()); //add title
			usrSessnModel.setProbDesc(usrParamsInfo1.getProbDesc()); //add descr

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state
			usrSessnModel.setNextActionState(MODIFYVALIDERRCONTDESCR);

		} else {

			//add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			///
			usrSessnModel.setPrevProbSevList(usrParamsInfo1.getPrevProbSevList()); //add severity
			usrSessnModel.setProbTitle(usrParamsInfo1.getProbTitle()); //add title
			usrSessnModel.setProbDesc(usrParamsInfo1.getProbDesc()); //add descr

			//

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//always take to first modify page

			usrSessnModel.setErrMsg("");
			usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state always to scrn 5
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from

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
	public String validateNewScrn1FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//check for issue severity

		if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevProbSevList())) {

			if (usrParamsInfo1.getPrevProbSevList().contains("NONE")) {

				errsb.append("Please select issue severity.");
				errsb.append("<br />");

			}
		}

		//cehck for issue title

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getProbTitle())) {

			errsb.append("Please provide issue title.");
			errsb.append("<br />");

		} else {

			String tempTitle = AmtCommonUtils.getTrimStr(usrParamsInfo1.getProbTitle());

			if (tempTitle.length() > 125) {

				errsb.append("Please provide maximum of 125 characters for issue title.");
				errsb.append("<br />");

			}
		}

		//cehck for issue description

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getProbDesc())) {

			errsb.append("Please provide issue description.");
			errsb.append("<br />");

		} else {

			String tempDesc = AmtCommonUtils.getTrimStr(usrParamsInfo1.getProbDesc());

			if (tempDesc.length() > 32700) {

				errsb.append("Please provide maximum of 32700 characters for description.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

	}


	public String validateModifyEditCustomFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//check for custom fields value
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC1DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC1List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC1DispName() + " ." );
				errsb.append("<br />");

			}
		}

		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC2DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC2List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC2DispName() + " ." );
				errsb.append("<br />");

			}
		}		
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC3DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC3List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC3DispName() + " ." );
				errsb.append("<br />");

			}
		}		
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC4DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC4List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC4DispName() + " ." );
				errsb.append("<br />");

			}
		}

		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC5DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC5List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC5DispName() + " ." );
				errsb.append("<br />");

			}
		}		
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC6DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC6List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC6DispName() + " ." );
				errsb.append("<br />");

			}
		}		
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC7DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC7List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC7DispName() + " ." );
				errsb.append("<br />");

			}
		}		
		
		if (EtsIssFilterUtils.isStringDefnd(usrParamsInfo1.getFieldC8DispName()) ) {
			
			if ( ! EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getFieldC8List() ) ) {

				errsb.append("Please enter the value for " + usrParamsInfo1.getFieldC8DispName() + " ." );
				errsb.append("<br />");

			}
		}				
		
		
		//get from session
		return errsb.toString();

	}
	
	
	
	/**
			 * This method will retrieve the Model from session
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getCancelDescrDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get cancelstate
		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state always to scrn 5
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from

		return usrSessnModel;

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
		 * @return
		 */

	public EtsIssProbInfoUsr1Model doFileAttach() {

		

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
			
			if(!AmtCommonUtils.isResourceDefined(attch)) {

			String errFile[] = getFileAttachUtils().doAttach(getEtsIssObjKey().getRequest());
			fileErrMsg = errFile[1];
			
			}

		}

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg(fileErrMsg);
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(MODIFYFILEATTACH);

		return usrSessnModel;

	}

	/**
			 * 
			 * @return
			 */

	public EtsIssProbInfoUsr1Model deleteFileAttach() {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		String edge_problem_id = usrSessnModel.getEdgeProblemId();
		int fileNum = getEtsIssObjKey().getFilenum();
		Global.println("edge problem id===" + edge_problem_id);
		Global.println("file num===" + fileNum);
		String strProjectId=usrSessnModel.getEtsProjId();

		boolean success = true;

		try {

			//6.1.1 migrating to documents repository
			//ETSIssuesManager.deleteAttach("ETS", edge_problem_id, fileNum);
			
			getFileAttachUtils().deleteIssueFile(strProjectId, edge_problem_id, fileNum);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			success = false;
		}

		int cancelstate = getCancelActionState();

		if (cancelstate == 4) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
			usrSessnModel.setNextActionState(MODIFYDELETEFILE);

		}

		if (cancelstate > 4) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //retain the old state
			usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

		}

		return usrSessnModel;

	}

	/**
		  * 
		  * @return
		  */

	public EtsIssProbInfoUsr1Model getContFileattachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//update files flag from T > Y
		//ETSIssuesManager.updateFileFlagInUsr2(usrSessnModel.getEdgeProblemId(), getEtsIssObjKey().getEs().gUSERN, "T", "Y");
		//6.1.1 migrn to doc repository
		getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(),usrSessnModel.getEdgeProblemId(),"T","Y");

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate + 1); //always to state of modify main page==5
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

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

		//		get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		Global.println("edge problem id in getIssueViewDetailsRefreshFiles===" + edgeProblemId);

		//delete all the files,with Y flag
		//int deletecount = ETSIssuesManager.deleteAttachWithNewFlg(ETSAPPLNID, edgeProblemId);
		
		//6.1.1 migrn to doc repository
		getFileAttachUtils().deleteIssueFilesWithoutStatus(getEtsIssObjKey().getProj().getProjectId(),edgeProblemId,"N");

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate + 1); //always to state of modify main page==5
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

		return usrSessnModel;

	}

	/**
			 * This method will retrieve the Model from session
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getEditNotifyListDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		// if issue access governs the notify list, irrespective of whatever issue type gets
		//selected

		String chkIssTypeIbmOnly = usrSessnModel.getChkIssTypeIbmOnly();

		//to get prev notify list from email list of addresses, there are some chances of not matching
		//edge_ids/with email addresses, as there are chances of having more ids with the same email id

		ArrayList prevNotifyList = usrSessnModel.getPrevNotifyList();

		//descModel.set for prev and current  values
		//set the norify list params
		usrSessnModel.setNotifyList(getNotifyListDetails(chkIssTypeIbmOnly));
		usrSessnModel.setPrevNotifyList(prevNotifyList);

		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(0);

		return usrSessnModel;

	}

	/**
			  * 
			  * @return
			  */

	public EtsIssProbInfoUsr1Model getContNotifyListDetails() {

		//	get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//	get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr7ParamsIntoUsr1Model();

		//get the prev selected notify list
		usrSessnModel.setPrevNotifyList(usrParamsInfo1.getPrevNotifyList());

		//upload the updated object into session//
		setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //always to state of modify main page==5
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

		return usrSessnModel;

	}

	/**
			  * 
			  * @return
			  */

	public EtsIssProbInfoUsr1Model getCancNotifyListDetails() {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //always to state of modify main page==5
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

		return usrSessnModel;

	}

	/**
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public ArrayList getNotifyListDetails(String chkIssTypeIbmOnly) throws SQLException, Exception {

		//get proj member DAO and get details
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		ArrayList projMemList = projDao.getProjMemberListWithUserType(getEtsIssObjKey().getProj().getProjectId());
		ArrayList userTypeList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";
		String etsUserEmail = "";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			if (!chkIssTypeIbmOnly.equals("Y")) {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);
					etsUserEmail = (String) projMemList.get(i + 3);

					if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

						etsUserNameWithIrId = etsUserNameWithIrId + " *";

					} //if user type is IBM

					userTypeList.add(etsUserEmail);
					userTypeList.add(etsUserNameWithIrId);

				} //end of for

			} // end of for iss type=IBM ONLY !=Y

			else {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);
					etsUserEmail = (String) projMemList.get(i + 3);

					if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I") && etsUserType.equals("I")) {

						userTypeList.add(etsUserEmail);
						userTypeList.add(etsUserNameWithIrId);

					} //if user type is IBM

				} //end of for

			} //end of check iss ibm type=Y

		} //if projMemelist is defined

		return userTypeList;

	}

	public int getCancelActionState() {

		String strcancelstate = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("cancelstate"));
		int cancelstate = 0;

		if (AmtCommonUtils.isResourceDefined(strcancelstate)) {

			cancelstate = Integer.parseInt(strcancelstate);
		}
		return cancelstate;

	}

	public int getSubtypeActionState() {

		String strsubtypestate = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("subtypestate"));
		int subtypestate = 0;

		if (AmtCommonUtils.isResourceDefined(strsubtypestate)) {

			subtypestate = Integer.parseInt(strsubtypestate);
		}
		return subtypestate;

	}

	/**
				 * This method will retrieve the Model from session
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public EtsIssProbInfoUsr1Model getModEditIssueIdentfDetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state	

		if (!isAtLeastPrevOneSubTypeDefnd(usrSessnModel) && !isAtLeastOnePrevFieldValListExists(usrSessnModel)) {

			Global.println("no sub type defined");

			//	set issue types
			usrSessnModel.setProbTypeList(getIssueTypeList(usrSessnModel));

			usrSessnModel.setNextActionState(MODIFYISSUEIDENTFDEFAULT);
			usrSessnModel.setSubtypeActionState(0);

		} else {

			Global.println("sub type defined");
			usrSessnModel = prepPrevFieldListModelFromDb(usrSessnModel);

			usrSessnModel.setNextActionState(MODIFYEDITISSUEIDENTF);
			usrSessnModel.setSubtypeActionState(1000);
		}

		//

		//get from session
		return usrSessnModel;

	}

	boolean isAtLeastPrevOneSubTypeDefnd(EtsIssProbInfoUsr1Model usr1InfoModel) {

		//		curr lists
		ArrayList prevSubTypeAValList = null;

		if (usr1InfoModel != null) {

			prevSubTypeAValList = usr1InfoModel.getPrevSubTypeAList();

		}

		int subadispsize = 0;

		boolean subtypedefnd = false;

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList)) {

			subadispsize = prevSubTypeAValList.size();

		}

		if (subadispsize > 0) {

			String prevSubTypeAVal = (String) prevSubTypeAValList.get(0);

			if (AmtCommonUtils.isResourceDefined(prevSubTypeAVal)) {

				subtypedefnd = true;

			}

		} //end of size==0

		return subtypedefnd;
	} //end of method

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getModEditSubTypeADetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(0);

		//get from session
		return usrSessnModel;

	}

	/**
				 * This method will load step1 details into session and get the step 2 details
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public EtsIssProbInfoUsr1Model getModEditSubTypeBDetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(0);

		//get from session
		return usrSessnModel;

	}

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getModEditSubTypeCDetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(0);

		//get from session
		return usrSessnModel;

	}

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getModEditSubTypeDDetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
		usrSessnModel.setNextActionState(0);

		//get from session
		return usrSessnModel;

	}

	/**
				 * to get value array list
				 * 
				 */

	public ArrayList getFieldValueList(ArrayList dropValList, String qual) {

		///
		ArrayList filDropValList = new ArrayList();
		String fieldValue = "";
		String subTypeA = "";
		String subTypeB = "";
		String subTypeC = "";
		String subTypeD = "";

		int size = 0;

		if (dropValList != null && !dropValList.isEmpty()) {

			size = dropValList.size();

		}

		for (int i = 0; i < size; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropValList.get(i);

			fieldValue = AmtCommonUtils.getTrimStr(dropBean.getFieldValue());
			subTypeA = AmtCommonUtils.getTrimStr(dropBean.getSubTypeA());
			subTypeB = AmtCommonUtils.getTrimStr(dropBean.getSubTypeB());
			subTypeC = AmtCommonUtils.getTrimStr(dropBean.getSubTypeC());
			subTypeD = AmtCommonUtils.getTrimStr(dropBean.getSubTypeD());

			if (qual.equals("fieldValue")) {

				if (AmtCommonUtils.isResourceDefined(fieldValue) && !filDropValList.contains(fieldValue)) {

					filDropValList.add(fieldValue);

				}

			} else if (qual.equals(STDSUBTYPE_A)) {

				if (AmtCommonUtils.isResourceDefined(subTypeA) && !filDropValList.contains(subTypeA)) {

					filDropValList.add(subTypeA);

				}

			} else if (qual.equals(STDSUBTYPE_B)) {

				if (AmtCommonUtils.isResourceDefined(subTypeB) && !filDropValList.contains(subTypeB)) {

					filDropValList.add(subTypeB);

				}

			} else if (qual.equals(STDSUBTYPE_C)) {

				if (AmtCommonUtils.isResourceDefined(subTypeC) && !filDropValList.contains(subTypeC)) {

					filDropValList.add(subTypeC);

				}

			} else if (qual.equals(STDSUBTYPE_D)) {

				if (AmtCommonUtils.isResourceDefined(subTypeD) && !filDropValList.contains(subTypeD)) {

					filDropValList.add(subTypeD);

				}

			}

		}

		return filDropValList;

	}

	/**
			 * 
			 * 
			 * @param usr1Model
			 * @return
			 */

	public boolean isAtLeastOneFieldValListExists(EtsIssProbInfoUsr1Model usr1Model) {

		ArrayList subTypeAList = null;

		if (usr1Model != null) {

			subTypeAList = usr1Model.getSubTypeAList();

		}

		if (EtsIssFilterUtils.isArrayListDefnd(subTypeAList)) {

			if (!subTypeAList.contains("NONE")) {

				return true;

			}
		}

		return false;
	}

	/**
				 * 
				 * 
				 * @param usr1Model
				 * @return
				 */

	public boolean isAtLeastOnePrevFieldValListExists(EtsIssProbInfoUsr1Model usr1Model) {

		ArrayList prevSubTypeAList = null;

		if (usr1Model != null) {

			prevSubTypeAList = usr1Model.getPrevSubTypeAList();

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

			if (!prevSubTypeAList.contains("NONE")) {

				return true;

			}
		}

		return false;
	}

	/**
		 * 
		 * 
		 * @param usr1Model
		 * @return
		 */

	public boolean isIssueTypeChanged(EtsIssProbInfoUsr1Model usr1Model) throws Exception {

		String problem_type = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("problem_type"));

		ArrayList prevProbTypeList = usr1Model.getPrevProbTypeList();

		ArrayList prevIssueTypeList = usr1Model.getPrevIssueTypeList();

		//prev prob type

		String prevProbType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(prevProbTypeList)) {

			prevProbType = AmtCommonUtils.getTrimStr((String) prevProbTypeList.get(0));

		}

		//prev issue type

		String prevIssueType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(prevIssueTypeList)) {

			prevIssueType = AmtCommonUtils.getTrimStr((String) prevIssueTypeList.get(0));

		}

		//get the $ stuff for current problem type

		EtsIssActionGuiUtils guiUtils = new EtsIssActionGuiUtils();
		EtsDropDownDataBean dropBean = guiUtils.getIssueTypeDropDownAttrib(problem_type);

		//set issue source
		String issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
		String issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());
		String issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

		if (!prevIssueType.equals(issueType)) {

			return true;
		} else {

			return false;
		}

	}

	/**
			 * To print Issue identification model
			 */

	public EtsIssProbInfoUsr1Model getModGoIssueTypeDetails(EtsIssProbInfoUsr1Model usr1PrevModel) throws SQLException, Exception {

		//get data prep bean
		EtsIssNewDropDownDataPrepBean newDataPrepBean = new EtsIssNewDropDownDataPrepBean(getEtsIssObjKey());

		//prepare the static data lists for ths given project id
		EtsIssStaticSubmFormDetails staticFormDets = newDataPrepBean.prepareStaticDropDownDets(usr1PrevModel);

		//		//curr lists
		ArrayList fieldC1ValList = getFieldValueList(staticFormDets.getFieldC1ValList(), "fieldValue");
		ArrayList fieldC2ValList = getFieldValueList(staticFormDets.getFieldC2ValList(), "fieldValue");
		ArrayList fieldC3ValList = getFieldValueList(staticFormDets.getFieldC3ValList(), "fieldValue");
		ArrayList fieldC4ValList = getFieldValueList(staticFormDets.getFieldC4ValList(), "fieldValue");
		ArrayList fieldC5ValList = getFieldValueList(staticFormDets.getFieldC5ValList(), "fieldValue");
		ArrayList fieldC6ValList = getFieldValueList(staticFormDets.getFieldC6ValList(), "fieldValue");
		ArrayList fieldC7ValList = getFieldValueList(staticFormDets.getFieldC7ValList(), "fieldValue");

		//for field c1
		usr1PrevModel.setFieldC1RefName(staticFormDets.getFieldC1RefName());
		usr1PrevModel.setFieldC1DispName(staticFormDets.getFieldC1DispName());
		usr1PrevModel.setFieldC1List(fieldC1ValList);
		usr1PrevModel.setPrevFieldC1List(staticFormDets.getPrevFieldC1ValList());

		//		for field c2
		usr1PrevModel.setFieldC2RefName(staticFormDets.getFieldC2RefName());
		usr1PrevModel.setFieldC2DispName(staticFormDets.getFieldC2DispName());
		usr1PrevModel.setFieldC2List(fieldC2ValList);
		usr1PrevModel.setPrevFieldC2List(staticFormDets.getPrevFieldC2ValList());

		//		for field c3
		usr1PrevModel.setFieldC3RefName(staticFormDets.getFieldC3RefName());
		usr1PrevModel.setFieldC3DispName(staticFormDets.getFieldC3DispName());
		usr1PrevModel.setFieldC3List(fieldC3ValList);
		usr1PrevModel.setPrevFieldC3List(staticFormDets.getPrevFieldC3ValList());

		//		for field c4
		usr1PrevModel.setFieldC4RefName(staticFormDets.getFieldC4RefName());
		usr1PrevModel.setFieldC4DispName(staticFormDets.getFieldC4DispName());
		usr1PrevModel.setFieldC4List(fieldC4ValList);
		usr1PrevModel.setPrevFieldC4List(staticFormDets.getPrevFieldC4ValList());

		//		for field c5
		usr1PrevModel.setFieldC5RefName(staticFormDets.getFieldC5RefName());
		usr1PrevModel.setFieldC5DispName(staticFormDets.getFieldC5DispName());
		usr1PrevModel.setFieldC5List(fieldC5ValList);
		usr1PrevModel.setPrevFieldC5List(staticFormDets.getPrevFieldC5ValList());

		//		for field c6
		usr1PrevModel.setFieldC6RefName(staticFormDets.getFieldC6RefName());
		usr1PrevModel.setFieldC6DispName(staticFormDets.getFieldC6DispName());
		usr1PrevModel.setFieldC6List(fieldC6ValList);
		usr1PrevModel.setPrevFieldC6List(staticFormDets.getPrevFieldC6ValList());

		//		for field c7
		usr1PrevModel.setFieldC7RefName(staticFormDets.getFieldC7RefName());
		usr1PrevModel.setFieldC7DispName(staticFormDets.getFieldC7DispName());
		usr1PrevModel.setFieldC7List(fieldC7ValList);
		usr1PrevModel.setPrevFieldC7List(staticFormDets.getPrevFieldC7ValList());

		//get the dyn model
		EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_A);

		ArrayList subTypeAValList = dynFieldModel.getFieldValList();

		ArrayList dispSubTypeAValList = getFieldValueList(subTypeAValList, STDSUBTYPE_A);

		if (!dispSubTypeAValList.contains(ETSDEFAULTCQ)) {

			usr1PrevModel.setSubTypeARefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeADispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeAList(dispSubTypeAValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeAList(dynFieldModel.getPrevFieldValList()); //prev vals

		} else {

			usr1PrevModel.setSubTypeARefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeADispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeAList(getDefualtCqList());
			usr1PrevModel.setPrevSubTypeAList(dynFieldModel.getPrevFieldValList()); //prev vals
		}

		return usr1PrevModel;

	}

	/**
			 * To print Issue identification model
			 */

	public EtsIssProbInfoUsr1Model loadGoSubTypeADetails(EtsIssProbInfoUsr1Model usr1PrevModel) throws SQLException, Exception {

		//get data prep bean
		EtsIssNewDropDownDataPrepBean newDataPrepBean = new EtsIssNewDropDownDataPrepBean(getEtsIssObjKey());

		//get the dyn model
		EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_B);

		ArrayList subTypeBValList = dynFieldModel.getFieldValList();

		ArrayList dispSubTypeBValList = getFieldValueList(subTypeBValList, STDSUBTYPE_B);

		if (!dispSubTypeBValList.contains(ETSDEFAULTCQ)) {

			usr1PrevModel.setSubTypeBRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeBDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeBList(dispSubTypeBValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeBList(dynFieldModel.getPrevFieldValList()); //prev vals

		} else {

			usr1PrevModel.setSubTypeBRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeBDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeBList(getDefualtCqList());
			usr1PrevModel.setPrevSubTypeBList(dynFieldModel.getPrevFieldValList()); //prev vals
		}

		return usr1PrevModel;

	}

	/**
				 * To print Issue identification model
				 */

	public EtsIssProbInfoUsr1Model loadGoSubTypeBDetails(EtsIssProbInfoUsr1Model usr1PrevModel) throws SQLException, Exception {

		//get data prep bean
		EtsIssNewDropDownDataPrepBean newDataPrepBean = new EtsIssNewDropDownDataPrepBean(getEtsIssObjKey());

		//get the dyn model
		EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_C);

		ArrayList subTypeCValList = dynFieldModel.getFieldValList();

		ArrayList dispSubTypeCValList = getFieldValueList(subTypeCValList, STDSUBTYPE_C);

		if (!dispSubTypeCValList.contains(ETSDEFAULTCQ)) {

			usr1PrevModel.setSubTypeCRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeCDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeCList(dispSubTypeCValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeCList(dynFieldModel.getPrevFieldValList()); //prev vals

		} else {

			usr1PrevModel.setSubTypeCRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeCDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeCList(getDefualtCqList());
			usr1PrevModel.setPrevSubTypeCList(dynFieldModel.getPrevFieldValList()); //prev vals
		}

		return usr1PrevModel;

	}

	/**
					 * To print Issue identification model
					 */

	public EtsIssProbInfoUsr1Model loadGoSubTypeCDetails(EtsIssProbInfoUsr1Model usr1PrevModel) throws SQLException, Exception {

		//get data prep bean
		EtsIssNewDropDownDataPrepBean newDataPrepBean = new EtsIssNewDropDownDataPrepBean(getEtsIssObjKey());

		//get the dyn model
		EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_D);

		ArrayList subTypeDValList = dynFieldModel.getFieldValList();

		ArrayList dispSubTypeDValList = getFieldValueList(subTypeDValList, STDSUBTYPE_D);

		if (!dispSubTypeDValList.contains(ETSDEFAULTCQ)) {

			usr1PrevModel.setSubTypeDRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeDDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeDList(dispSubTypeDValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeDList(dynFieldModel.getPrevFieldValList()); //prev vals

		} else {

			usr1PrevModel.setSubTypeDRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeDDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeDList(getDefualtCqList());
			usr1PrevModel.setPrevSubTypeDList(dynFieldModel.getPrevFieldValList()); //prev vals
		}

		return usr1PrevModel;

	}

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getModGoSubTypeADetails() throws SQLException, Exception {

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//	get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		// get the selected params
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr2ParamsIntoUsr1Model();

		//set error msgs if any from screen1
		String errMsg = validateScrn2FormFields(usrParamsInfo1, usrSessnModel);

		Global.println("err msg in getModGoSubTypeADetails()===" + errMsg);

		//get the cancelstate
		int cancelstate = getCancelActionState();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(MODIFYADDISSUETYPE);

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeADefnd())) {

				//add the prev sub type A list
				usrSessnModel.setPrevSubTypeAList(usrParamsInfo1.getPrevSubTypeAList());
				usrSessnModel.setSubTypeADefnd(usrParamsInfo1.getSubTypeADefnd());

			}

			return usrSessnModel;

		} else {

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeADefnd())) {

				//add the prev sub type A list
				usrSessnModel.setPrevSubTypeAList(usrParamsInfo1.getPrevSubTypeAList());
				usrSessnModel.setSubTypeADefnd(usrParamsInfo1.getSubTypeADefnd());

			}

			//upload the updated object with subtype-A into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//set the state details
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(0);
			usrSessnModel.setSubtypeActionState(1001);

			//get from updated one with details, no need to override prev lists in modify, to overcome
			//cancel problem in modify, so that old values are not lost or overriden
			EtsIssProbInfoUsr1Model usrNewInfo1 = loadGoSubTypeADetails(setGoSubTypeADefaultLists(usrSessnModel));
			usrNewInfo1.setErrMsg("");

			return usrNewInfo1;

		}
		///

	}

	/**
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsIssProbInfoUsr1Model getModGoSubTypeBDetails() throws SQLException, Exception {

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//	get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		// loaded with params
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr3ParamsIntoUsr1Model();

		// set error msgs if any from screen1
		String errMsg = validateScrn2FormFields(usrParamsInfo1, usrSessnModel);

		Global.println("err msg getModGoSubTypeBDetails()===" + errMsg);

		//get cancel state
		int cancelstate = getCancelActionState();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(MODIFYGOSUBTYPEA);

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeBDefnd())) {
				//	add the prev sub type B list
				usrSessnModel.setPrevSubTypeBList(usrParamsInfo1.getPrevSubTypeBList());
				usrSessnModel.setSubTypeBDefnd(usrParamsInfo1.getSubTypeBDefnd());

			}

			return usrSessnModel;

		} else {

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeBDefnd())) {
				//	add the prev sub type B list
				usrSessnModel.setPrevSubTypeBList(usrParamsInfo1.getPrevSubTypeBList());
				usrSessnModel.setSubTypeBDefnd(usrParamsInfo1.getSubTypeBDefnd());

			}

			//upload the updated object with subtype-B into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//set the state			
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(0);
			usrSessnModel.setSubtypeActionState(1002);

			//get from updated one session the issue ident details
			EtsIssProbInfoUsr1Model usrNewInfo1 = loadGoSubTypeBDetails(setGoSubTypeBDefaultLists(usrSessnModel));
			usrNewInfo1.setErrMsg("");

			//
			return usrNewInfo1;

		}

	}

	/**
				 * This method will load step1 details into session and get the step 2 details
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public EtsIssProbInfoUsr1Model getModGoSubTypeCDetails() throws SQLException, Exception {

		//get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		///laoded with params
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr4ParamsIntoUsr1Model();

		//
		//		set error msgs if any from screen1
		String errMsg = validateScrn2FormFields(usrParamsInfo1, usrSessnModel);

		System.out.println("err msg in getGoSubTypeCDetails()===" + errMsg);

		int cancelstate = getCancelActionState();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(MODIFYGOSUBTYPEB);

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeCDefnd())) {

				//	add the prev sub type C list
				usrSessnModel.setPrevSubTypeCList(usrParamsInfo1.getPrevSubTypeCList());
				usrSessnModel.setSubTypeCDefnd(usrParamsInfo1.getSubTypeCDefnd());

			}

			return usrSessnModel;

		} else {

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeCDefnd())) {

				//	add the prev sub type C list
				usrSessnModel.setPrevSubTypeCList(usrParamsInfo1.getPrevSubTypeCList());
				usrSessnModel.setSubTypeCDefnd(usrParamsInfo1.getSubTypeCDefnd());

			}

			//upload the updated object with subtype-C into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//set all the states

			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(0);
			usrSessnModel.setSubtypeActionState(1003);

			//get from updated data for model
			EtsIssProbInfoUsr1Model usrNewInfo1 = loadGoSubTypeCDetails(setGoSubTypeCDefaultLists(usrSessnModel));
			usrNewInfo1.setErrMsg("");

			return usrNewInfo1;

		}

	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateScrn2FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1, EtsIssProbInfoUsr1Model usrSessnModel) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//get any field values if any

		String defSubTypeAVal = usrParamsInfo1.getSubTypeADefnd();
		String defSubTypeBVal = usrParamsInfo1.getSubTypeBDefnd();
		String defSubTypeCVal = usrParamsInfo1.getSubTypeCDefnd();
		String defSubTypeDVal = usrParamsInfo1.getSubTypeDDefnd();

		///to check if they are defined atleast
		String defFieldC1 = usrParamsInfo1.getFieldC1Defnd();
		String defFieldC2 = usrParamsInfo1.getFieldC2Defnd();
		String defFieldC3 = usrParamsInfo1.getFieldC3Defnd();
		String defFieldC4 = usrParamsInfo1.getFieldC4Defnd();
		String defFieldC5 = usrParamsInfo1.getFieldC5Defnd();
		String defFieldC6 = usrParamsInfo1.getFieldC6Defnd();
		String defFieldC7 = usrParamsInfo1.getFieldC7Defnd();
		String deftestcase = usrParamsInfo1.getFieldC1Defnd();

		ArrayList prevFieldC1ValList = usrParamsInfo1.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usrParamsInfo1.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usrParamsInfo1.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usrParamsInfo1.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usrParamsInfo1.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usrParamsInfo1.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usrParamsInfo1.getPrevFieldC7List();
		String testcase = usrParamsInfo1.getTestCase();

		ArrayList prevSubTypeAList = usrParamsInfo1.getPrevSubTypeAList();
		ArrayList prevSubTypeBList = usrParamsInfo1.getPrevSubTypeBList();
		ArrayList prevSubTypeCList = usrParamsInfo1.getPrevSubTypeCList();
		ArrayList prevSubTypeDList = usrParamsInfo1.getPrevSubTypeDList();

		//check for sub type a

		if (AmtCommonUtils.isResourceDefined(defSubTypeAVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

				if (prevSubTypeAList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getSubTypeADispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for sub type b

		if (AmtCommonUtils.isResourceDefined(defSubTypeBVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

				if (prevSubTypeBList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getSubTypeBDispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for sub type c

		if (AmtCommonUtils.isResourceDefined(defSubTypeCVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

				if (prevSubTypeCList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getSubTypeCDispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for sub type d

		if (AmtCommonUtils.isResourceDefined(defSubTypeDVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

				if (prevSubTypeDList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getSubTypeDDispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c1

		if (AmtCommonUtils.isResourceDefined(defFieldC1)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList)) {

				if (prevFieldC1ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC1DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c2

		if (AmtCommonUtils.isResourceDefined(defFieldC2)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)) {

				if (prevFieldC2ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC2DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c3

		if (AmtCommonUtils.isResourceDefined(defFieldC3)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)) {

				if (prevFieldC3ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC3DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c4

		if (AmtCommonUtils.isResourceDefined(defFieldC4)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)) {

				if (prevFieldC4ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC4DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c5

		if (AmtCommonUtils.isResourceDefined(defFieldC5)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)) {

				if (prevFieldC5ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC5DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c6

		if (AmtCommonUtils.isResourceDefined(defFieldC6)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)) {

				if (prevFieldC6ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC6DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for field c7

		if (AmtCommonUtils.isResourceDefined(defFieldC7)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)) {

				if (prevFieldC7ValList.contains("NONE")) {

					errsb.append("Please select a " + usrSessnModel.getFieldC7DispName() + " ");
					errsb.append("<br />");

				}

			}

		}

		//		check for test case

		if (AmtCommonUtils.isResourceDefined(deftestcase)) {

			if (!AmtCommonUtils.isResourceDefined(testcase)) {

				errsb.append("Please provide test case");
				errsb.append("<br />");

			} else {

				String tempTestCase = AmtCommonUtils.getTrimStr(testcase);

				if (tempTestCase.length() > 256) {

					errsb.append("Please provide maximum of 256 characters for Test case.");
					errsb.append("<br />");

				}
			}

		}

		//get from session
		return errsb.toString();

	}

	/**
			 * To invalidate on Continue on 1st screen
			 */

	private EtsIssProbInfoUsr1Model setGoSubTypeADefaultLists(EtsIssProbInfoUsr1Model usr1Model) {

		//defs of field c1.. c7 are blank;
		usr1Model.setFieldC1Defnd("");
		usr1Model.setFieldC2Defnd("");
		usr1Model.setFieldC3Defnd("");
		usr1Model.setFieldC4Defnd("");
		usr1Model.setFieldC5Defnd("");
		usr1Model.setFieldC6Defnd("");
		usr1Model.setFieldC7Defnd("");
		usr1Model.setTestCaseDefnd("");

		//but blank lists for field c1..7
		usr1Model.setPrevFieldC1List(getBlankList());
		usr1Model.setPrevFieldC2List(getBlankList());
		usr1Model.setPrevFieldC3List(getBlankList());
		usr1Model.setPrevFieldC4List(getBlankList());
		usr1Model.setPrevFieldC5List(getBlankList());
		usr1Model.setPrevFieldC6List(getBlankList());
		usr1Model.setPrevFieldC7List(getBlankList());
		usr1Model.setTestCase("");

		//

		usr1Model.setSubTypeBDefnd("");
		usr1Model.setSubTypeCDefnd("");
		usr1Model.setSubTypeDDefnd("");

		//for sub types

		usr1Model.setPrevSubTypeBList(getBlankList());
		usr1Model.setPrevSubTypeCList(getBlankList());
		usr1Model.setPrevSubTypeDList(getBlankList());

		return usr1Model;

	}

	/**
			 * To invalidate on Continue on 1st screen
			 */

	private EtsIssProbInfoUsr1Model setGoSubTypeBDefaultLists(EtsIssProbInfoUsr1Model usr1Model) {

		//defs of field c1.. c7 are blank;
		usr1Model.setFieldC1Defnd("");
		usr1Model.setFieldC2Defnd("");
		usr1Model.setFieldC3Defnd("");
		usr1Model.setFieldC4Defnd("");
		usr1Model.setFieldC5Defnd("");
		usr1Model.setFieldC6Defnd("");
		usr1Model.setFieldC7Defnd("");
		usr1Model.setTestCaseDefnd("");

		//but blank lists for field c1..7
		usr1Model.setPrevFieldC1List(getBlankList());
		usr1Model.setPrevFieldC2List(getBlankList());
		usr1Model.setPrevFieldC3List(getBlankList());
		usr1Model.setPrevFieldC4List(getBlankList());
		usr1Model.setPrevFieldC5List(getBlankList());
		usr1Model.setPrevFieldC6List(getBlankList());
		usr1Model.setPrevFieldC7List(getBlankList());
		usr1Model.setTestCase("");

		//

		usr1Model.setSubTypeCDefnd("");
		usr1Model.setSubTypeDDefnd("");

		//for sub types

		usr1Model.setPrevSubTypeCList(getBlankList());
		usr1Model.setPrevSubTypeDList(getBlankList());

		return usr1Model;

	}

	/**
			 * To invalidate on Continue on 1st screen
			 */

	private EtsIssProbInfoUsr1Model setGoSubTypeCDefaultLists(EtsIssProbInfoUsr1Model usr1Model) {

		//defs of field c1.. c7 are blank;
		usr1Model.setFieldC1Defnd("");
		usr1Model.setFieldC2Defnd("");
		usr1Model.setFieldC3Defnd("");
		usr1Model.setFieldC4Defnd("");
		usr1Model.setFieldC5Defnd("");
		usr1Model.setFieldC6Defnd("");
		usr1Model.setFieldC7Defnd("");
		usr1Model.setTestCaseDefnd("");

		//but blank lists for field c1..7
		usr1Model.setPrevFieldC1List(getBlankList());
		usr1Model.setPrevFieldC2List(getBlankList());
		usr1Model.setPrevFieldC3List(getBlankList());
		usr1Model.setPrevFieldC4List(getBlankList());
		usr1Model.setPrevFieldC5List(getBlankList());
		usr1Model.setPrevFieldC6List(getBlankList());
		usr1Model.setPrevFieldC7List(getBlankList());
		usr1Model.setTestCase("");

		//

		usr1Model.setSubTypeDDefnd("");

		//for sub types

		usr1Model.setPrevSubTypeDList(getBlankList());

		return usr1Model;

	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateScrn11FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//			check for issue type

		if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevProbTypeList())) {

			if (usrParamsInfo1.getPrevProbTypeList().contains("NONE")) {

				errsb.append("Please select issue type.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

	}

	/**
		 * This method will load step1 details into session and get the step 2 details
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsIssProbInfoUsr1Model getModAddIssueTypeDetails() throws SQLException, Exception {

		//	get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr11ParamsIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn11FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg Modiy Issue/getModAddIssueTypeDetails()=====" + errMsg);

		//get cancel state or scrn state, if cancelstate==0 means it started from 1st screen
		int cancelstate = getCancelActionState();

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//	add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			//		set issue types
			usrSessnModel.setProbTypeList(getIssueTypeList(usrSessnModel));

			usrSessnModel.setPrevProbTypeList(usrParamsInfo1.getPrevProbTypeList()); //add prob type

			//ArrayList prevIssueTypeList = new ArrayList();
			usrSessnModel.setPrevIssueTypeList(usrParamsInfo1.getPrevProbTypeList());

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state
			usrSessnModel.setNextActionState(MODIFYVALIDERRADDISSTYPE);

			return usrSessnModel;

		} else {

			//	check whthere user has changed the issue type from the prev value
			boolean isIssTypeChanged = isIssueTypeChanged(usrSessnModel);

			//add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			usrSessnModel.setPrevProbTypeList(usrParamsInfo1.getPrevProbTypeList()); //add prob type

			//			get issue type attributes///
			String prevProbType = (String) usrParamsInfo1.getPrevProbTypeList().get(0);

			EtsIssActionGuiUtils guiUtils = new EtsIssActionGuiUtils();
			EtsDropDownDataBean dropBean = guiUtils.getIssueTypeDropDownAttrib(prevProbType);

			//set issue source
			String issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
			String issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());
			String issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

			Global.println("issue source in getAddIssueTypeDetails/Modify issue ===" + issueSource);
			Global.println("issue access in getAddIssueTypeDetails/Modify issue ===" + issueAccess);
			Global.println("issue type in getAddIssueTypeDetails/Modify issue ===" + issueType);

			//			set issue source/access
			usrSessnModel.setIssueSource(issueSource);
			usrSessnModel.setIssueType(issueType);
			//usrSessnModel.setIssueAccess(issueAccess); //donot add, as notify list gets affected

			//add issue type to list
			ArrayList prevIssueTypeList = new ArrayList();
			prevIssueTypeList.add(issueType);
			usrSessnModel.setPrevIssueTypeList(prevIssueTypeList);

			//issue access wonot get changed

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			//if (isIssTypeChanged) { //take user through the ident process, and after that take user to original state

			//add the sub type details/or field lists to the existing one, no necessary to override old vals
			//
			EtsIssProbInfoUsr1Model usrNewInfo1 = getModGoIssueTypeDetails(setContDescrDefaultLists(usrSessnModel));

			////

			if (!isAtLeastOneSubTypeDefnd(usrNewInfo1)) {

				usrNewInfo1.setErrMsg("");
				usrNewInfo1.setCurrentActionState(currentstate);
				usrNewInfo1.setCancelActionState(cancelstate); //make cancel state=5 always
				usrNewInfo1.setNextActionState(MODIFYISSUEFIRSTPAGE); //go to modify issue main page

			} else {

				usrNewInfo1.setErrMsg("");
				usrNewInfo1.setCurrentActionState(currentstate);
				usrNewInfo1.setCancelActionState(cancelstate); //make cancel state=1 i.e increase by 1	
				usrNewInfo1.setNextActionState(0);
			}

			////

			return usrNewInfo1;

			//} 

			/*else {
			
				EtsIssProbInfoUsr1Model usrNewInfo1 = getUsr1InfoFromSessn();
				usrNewInfo1.setErrMsg("");
				usrNewInfo1.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state from where it came from
				usrNewInfo1.setCurrentActionState(currentstate);
				usrNewInfo1.setCancelActionState(cancelstate); //make it to one from where it came from
				return usrNewInfo1;
			
			}*/

		} //end of no err msg

	}

	boolean isAtLeastOneSubTypeDefnd(EtsIssProbInfoUsr1Model usr1InfoModel) {

		//		curr lists
		ArrayList dispSubTypeAValList = null;

		if (usr1InfoModel != null) {

			dispSubTypeAValList = usr1InfoModel.getSubTypeAList();

		}

		usr1InfoModel.getSubTypeAList();

		int subadispsize = 0;

		boolean subtypedefnd = false;

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeAValList)) {

			subadispsize = dispSubTypeAValList.size();
		}

		if (subadispsize > 0) {

			String dispSubTypeAVal = (String) dispSubTypeAValList.get(0);

			if (AmtCommonUtils.isResourceDefined(dispSubTypeAVal)) {

				subtypedefnd = true;

			}

		} //end of size==0

		return subtypedefnd;
	} //end of method

	/**
		 * To get the final details of issue identification
		 * 
		 */

	public EtsIssProbInfoUsr1Model goModContIssueIdentDetails() throws SQLException, Exception {

		//		get edge_problem_id from href
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));

		//	get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		///laoded with params
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr5ParamsIntoUsr1Model();

		///
		//get issue type attributes///
		String prevProbType = (String) usrSessnModel.getPrevProbTypeList().get(0);

		EtsIssActionGuiUtils guiUtils = new EtsIssActionGuiUtils();
		EtsDropDownDataBean dropBean = guiUtils.getIssueTypeDropDownAttrib(prevProbType);
		//set issue source
		String issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
		String issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());
		String issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

		Global.println("issue source in goModContIssueIdentDetails ===" + issueSource);
		Global.println("issue access in goModContIssueIdentDetails ===" + issueAccess);
		Global.println("issue type in goModContIssueIdentDetails ===" + issueType);

		//set issue source/access
		usrSessnModel.setIssueSource(issueSource);
		usrSessnModel.setIssueType(issueType);

		//add issue type to list
		ArrayList prevIssueTypeList = new ArrayList();
		prevIssueTypeList.add(issueType);
		usrSessnModel.setPrevIssueTypeList(prevIssueTypeList);

		//set error msgs if any from screen1
		String errMsg = validateScrn2FormFields(usrParamsInfo1, usrSessnModel);

		Global.println("err msg in goModContIssueIdentDetails() ===" + errMsg);

		int cancelstate = getCancelActionState();
		int subtypestate = getSubtypeActionState();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//set here also///
			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeADefnd())) {

				//add the prev sub type A list
				usrSessnModel.setPrevSubTypeAList(usrParamsInfo1.getPrevSubTypeAList());
				usrSessnModel.setSubTypeADefnd(usrParamsInfo1.getSubTypeADefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeBDefnd())) {
				//	add the prev sub type B list
				usrSessnModel.setPrevSubTypeBList(usrParamsInfo1.getPrevSubTypeBList());
				usrSessnModel.setSubTypeBDefnd(usrParamsInfo1.getSubTypeBDefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeCDefnd())) {

				//	add the prev sub type C list
				usrSessnModel.setPrevSubTypeCList(usrParamsInfo1.getPrevSubTypeCList());
				usrSessnModel.setSubTypeCDefnd(usrParamsInfo1.getSubTypeCDefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeDDefnd())) {

				//	add the prev sub type D list
				usrSessnModel.setPrevSubTypeDList(usrParamsInfo1.getPrevSubTypeDList());
				usrSessnModel.setSubTypeDDefnd(usrParamsInfo1.getSubTypeDDefnd());

			}

			///field c1..c7, testcase//

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC1Defnd())) {

				usrSessnModel.setFieldC1Defnd(usrParamsInfo1.getFieldC1Defnd());
				usrSessnModel.setPrevFieldC1List(usrParamsInfo1.getPrevFieldC1List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC2Defnd())) {

				usrSessnModel.setFieldC2Defnd(usrParamsInfo1.getFieldC2Defnd());
				usrSessnModel.setPrevFieldC2List(usrParamsInfo1.getPrevFieldC2List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC3Defnd())) {

				usrSessnModel.setFieldC3Defnd(usrParamsInfo1.getFieldC3Defnd());
				usrSessnModel.setPrevFieldC3List(usrParamsInfo1.getPrevFieldC3List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC4Defnd())) {

				usrSessnModel.setFieldC4Defnd(usrParamsInfo1.getFieldC4Defnd());
				usrSessnModel.setPrevFieldC4List(usrParamsInfo1.getPrevFieldC4List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC5Defnd())) {

				usrSessnModel.setFieldC5Defnd(usrParamsInfo1.getFieldC5Defnd());
				usrSessnModel.setPrevFieldC5List(usrParamsInfo1.getPrevFieldC5List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC6Defnd())) {

				usrSessnModel.setFieldC6Defnd(usrParamsInfo1.getFieldC6Defnd());
				usrSessnModel.setPrevFieldC6List(usrParamsInfo1.getPrevFieldC6List());
			}
			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC7Defnd())) {

				usrSessnModel.setFieldC7Defnd(usrParamsInfo1.getFieldC7Defnd());
				usrSessnModel.setPrevFieldC7List(usrParamsInfo1.getPrevFieldC7List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getTestCaseDefnd())) {

				usrSessnModel.setTestCaseDefnd(usrParamsInfo1.getTestCaseDefnd());
				usrSessnModel.setTestCase(usrParamsInfo1.getTestCase());

			}

			///////////////end

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);

			if (subtypestate == 0) {

				usrSessnModel.setNextActionState(MODIFYADDISSUETYPE);
			}

			if (subtypestate == 1000) {

				usrSessnModel.setNextActionState(MODIFYEDITISSUEIDENTF);
			}

			if (subtypestate == 1001) {

				usrSessnModel.setNextActionState(MODIFYGOSUBTYPEA);
			}
			if (subtypestate == 1002) {

				usrSessnModel.setNextActionState(MODIFYGOSUBTYPEB);
			}

			if (subtypestate == 1003) {

				usrSessnModel.setNextActionState(MODIFYGOSUBTYPEC);
			}

			return usrSessnModel;

		} else {

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeADefnd())) {

				//add the prev sub type A list
				usrSessnModel.setPrevSubTypeAList(usrParamsInfo1.getPrevSubTypeAList());
				usrSessnModel.setSubTypeADefnd(usrParamsInfo1.getSubTypeADefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeBDefnd())) {
				//	add the prev sub type B list
				usrSessnModel.setPrevSubTypeBList(usrParamsInfo1.getPrevSubTypeBList());
				usrSessnModel.setSubTypeBDefnd(usrParamsInfo1.getSubTypeBDefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeCDefnd())) {

				//	add the prev sub type C list
				usrSessnModel.setPrevSubTypeCList(usrParamsInfo1.getPrevSubTypeCList());
				usrSessnModel.setSubTypeCDefnd(usrParamsInfo1.getSubTypeCDefnd());

			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getSubTypeDDefnd())) {

				//	add the prev sub type D list
				usrSessnModel.setPrevSubTypeDList(usrParamsInfo1.getPrevSubTypeDList());
				usrSessnModel.setSubTypeDDefnd(usrParamsInfo1.getSubTypeDDefnd());

			}

			///field c1..c7, testcase//

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC1Defnd())) {

				usrSessnModel.setFieldC1Defnd(usrParamsInfo1.getFieldC1Defnd());
				usrSessnModel.setPrevFieldC1List(usrParamsInfo1.getPrevFieldC1List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC2Defnd())) {

				usrSessnModel.setFieldC2Defnd(usrParamsInfo1.getFieldC2Defnd());
				usrSessnModel.setPrevFieldC2List(usrParamsInfo1.getPrevFieldC2List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC3Defnd())) {

				usrSessnModel.setFieldC3Defnd(usrParamsInfo1.getFieldC3Defnd());
				usrSessnModel.setPrevFieldC3List(usrParamsInfo1.getPrevFieldC3List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC4Defnd())) {

				usrSessnModel.setFieldC4Defnd(usrParamsInfo1.getFieldC4Defnd());
				usrSessnModel.setPrevFieldC4List(usrParamsInfo1.getPrevFieldC4List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC5Defnd())) {

				usrSessnModel.setFieldC5Defnd(usrParamsInfo1.getFieldC5Defnd());
				usrSessnModel.setPrevFieldC5List(usrParamsInfo1.getPrevFieldC5List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC6Defnd())) {

				usrSessnModel.setFieldC6Defnd(usrParamsInfo1.getFieldC6Defnd());
				usrSessnModel.setPrevFieldC6List(usrParamsInfo1.getPrevFieldC6List());
			}
			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getFieldC7Defnd())) {

				usrSessnModel.setFieldC7Defnd(usrParamsInfo1.getFieldC7Defnd());
				usrSessnModel.setPrevFieldC7List(usrParamsInfo1.getPrevFieldC7List());
			}

			if (AmtCommonUtils.isResourceDefined(usrParamsInfo1.getTestCaseDefnd())) {

				usrSessnModel.setTestCaseDefnd(usrParamsInfo1.getTestCaseDefnd());
				usrSessnModel.setTestCase(usrParamsInfo1.getTestCase());

			}

			String dfcqSubTypeA = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("DFCQ" + STDSUBTYPE_A));
			String dfcqSubTypeB = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("DFCQ" + STDSUBTYPE_B));
			String dfcqSubTypeC = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("DFCQ" + STDSUBTYPE_C));
			String dfcqSubTypeD = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("DFCQ" + STDSUBTYPE_D));

			ArrayList dfcqList = new ArrayList();
			dfcqList.add(ETSDEFAULTCQ);

			if (AmtCommonUtils.isResourceDefined(dfcqSubTypeA)) {

				usrSessnModel.setPrevSubTypeAList(dfcqList);
			}

			if (AmtCommonUtils.isResourceDefined(dfcqSubTypeB)) {

				usrSessnModel.setPrevSubTypeBList(dfcqList);
			}

			if (AmtCommonUtils.isResourceDefined(dfcqSubTypeC)) {

				usrSessnModel.setPrevSubTypeCList(dfcqList);
			}

			if (AmtCommonUtils.isResourceDefined(dfcqSubTypeD)) {

				usrSessnModel.setPrevSubTypeDList(dfcqList);
			}

			//upload the updated object with subtype-C into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);

			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //retain the old state
			usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

			usrSessnModel.setErrMsg("");

			return usrSessnModel;
		} //end of else

	}

	/**
		 * To invalidate on Continue on 1st screen
		 */

	private EtsIssProbInfoUsr1Model setContDescrDefaultLists(EtsIssProbInfoUsr1Model usr1Model) {

		//defs of field c1.. c7 are blank;
		usr1Model.setFieldC1Defnd("");
		usr1Model.setFieldC2Defnd("");
		usr1Model.setFieldC3Defnd("");
		usr1Model.setFieldC4Defnd("");
		usr1Model.setFieldC5Defnd("");
		usr1Model.setFieldC6Defnd("");
		usr1Model.setFieldC7Defnd("");
		usr1Model.setTestCaseDefnd("");

		//but blank lists for field c1..7
		usr1Model.setPrevFieldC1List(getBlankList());
		usr1Model.setPrevFieldC2List(getBlankList());
		usr1Model.setPrevFieldC3List(getBlankList());
		usr1Model.setPrevFieldC4List(getBlankList());
		usr1Model.setPrevFieldC5List(getBlankList());
		usr1Model.setPrevFieldC6List(getBlankList());
		usr1Model.setPrevFieldC7List(getBlankList());
		usr1Model.setTestCase("");

		//
		usr1Model.setSubTypeADefnd("");
		usr1Model.setSubTypeBDefnd("");
		usr1Model.setSubTypeCDefnd("");
		usr1Model.setSubTypeDDefnd("");

		//for sub types
		usr1Model.setPrevSubTypeAList(getBlankList());
		usr1Model.setPrevSubTypeBList(getBlankList());
		usr1Model.setPrevSubTypeCList(getBlankList());
		usr1Model.setPrevSubTypeDList(getBlankList());

		return usr1Model;

	}

	public EtsIssProbInfoUsr1Model prepPrevFieldListModelFromDb(EtsIssProbInfoUsr1Model usr1PrevModel) throws SQLException, Exception {

		//		get data prep bean
		EtsIssNewDropDownDataPrepBean newDataPrepBean = new EtsIssNewDropDownDataPrepBean(getEtsIssObjKey());

		//		prev lists
		ArrayList prevFieldC1ValList = usr1PrevModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1PrevModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1PrevModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1PrevModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1PrevModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1PrevModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1PrevModel.getPrevFieldC7List();

		ArrayList prevSubTypeAList = usr1PrevModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBList = usr1PrevModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCList = usr1PrevModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDList = usr1PrevModel.getPrevSubTypeDList();

		//		//prev field c1..c7 vals
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";

		//prev sub types
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";

		//		sub type a..d
		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

			prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) prevSubTypeAList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

			prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) prevSubTypeBList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

			prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) prevSubTypeCList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

			prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) prevSubTypeDList.get(0));

		}

		//prev fields c1..c7

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList)) {

			prevFieldC1Val = AmtCommonUtils.getTrimStr((String) prevFieldC1ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)) {

			prevFieldC2Val = AmtCommonUtils.getTrimStr((String) prevFieldC2ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)) {

			prevFieldC3Val = AmtCommonUtils.getTrimStr((String) prevFieldC3ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)) {

			prevFieldC4Val = AmtCommonUtils.getTrimStr((String) prevFieldC4ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)) {

			prevFieldC5Val = AmtCommonUtils.getTrimStr((String) prevFieldC5ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)) {

			prevFieldC6Val = AmtCommonUtils.getTrimStr((String) prevFieldC6ValList.get(0));

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)) {

			prevFieldC7Val = AmtCommonUtils.getTrimStr((String) prevFieldC7ValList.get(0));

		}

		String testcase = AmtCommonUtils.getTrimStr(usr1PrevModel.getTestCase());

		//for consistencey , methods to build sub types are also called, even though they are not reqd at this point
		//impact is minimal
		if (AmtCommonUtils.isResourceDefined(prevSubTypeAVal) && !prevSubTypeAVal.equals(ETSDEFAULTCQ)) {

			//	get the dyn model
			EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_A);

			ArrayList subTypeAValList = dynFieldModel.getFieldValList();

			ArrayList dispSubTypeAValList = getFieldValueList(subTypeAValList, STDSUBTYPE_A);

			usr1PrevModel.setSubTypeARefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeADispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeAList(dispSubTypeAValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeAList(dynFieldModel.getPrevFieldValList()); //prev vals

		}

		//for B
		if (AmtCommonUtils.isResourceDefined(prevSubTypeBVal) && !prevSubTypeBVal.equals(ETSDEFAULTCQ)) {

			//	get the dyn model
			EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_B);

			ArrayList subTypeBValList = dynFieldModel.getFieldValList();

			ArrayList dispSubTypeBValList = getFieldValueList(subTypeBValList, STDSUBTYPE_B);

			usr1PrevModel.setSubTypeBRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeBDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeBList(dispSubTypeBValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeBList(dynFieldModel.getPrevFieldValList()); //prev vals

		}

		//		for C
		if (AmtCommonUtils.isResourceDefined(prevSubTypeCVal) && !prevSubTypeCVal.equals(ETSDEFAULTCQ)) {

			//	get the dyn model
			EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_C);

			ArrayList subTypeCValList = dynFieldModel.getFieldValList();

			ArrayList dispSubTypeCValList = getFieldValueList(subTypeCValList, STDSUBTYPE_C);

			usr1PrevModel.setSubTypeCRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeCDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeCList(dispSubTypeCValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeCList(dynFieldModel.getPrevFieldValList()); //prev vals

		}

		//		for D
		if (AmtCommonUtils.isResourceDefined(prevSubTypeDVal) && !prevSubTypeDVal.equals(ETSDEFAULTCQ)) {

			//	get the dyn model
			EtsIssDynFieldDataModel dynFieldModel = newDataPrepBean.getDynamicFieldDataModel(usr1PrevModel, STDSUBTYPE_D);

			ArrayList subTypeDValList = dynFieldModel.getFieldValList();

			ArrayList dispSubTypeDValList = getFieldValueList(subTypeDValList, STDSUBTYPE_D);

			usr1PrevModel.setSubTypeDRefName(dynFieldModel.getFieldRefName());
			usr1PrevModel.setSubTypeDDispName(dynFieldModel.getFieldDispName());
			usr1PrevModel.setSubTypeDList(dispSubTypeDValList); //adding the val lists to existing one only
			usr1PrevModel.setPrevSubTypeDList(dynFieldModel.getPrevFieldValList()); //prev vals

		}

		//		prepare the static data lists for ths given project id
		EtsIssStaticSubmFormDetails staticFormDets = newDataPrepBean.prepareStaticDropDownDets(usr1PrevModel);

		//		//curr lists
		ArrayList fieldC1ValList = getFieldValueList(staticFormDets.getFieldC1ValList(), "fieldValue");
		ArrayList fieldC2ValList = getFieldValueList(staticFormDets.getFieldC2ValList(), "fieldValue");
		ArrayList fieldC3ValList = getFieldValueList(staticFormDets.getFieldC3ValList(), "fieldValue");
		ArrayList fieldC4ValList = getFieldValueList(staticFormDets.getFieldC4ValList(), "fieldValue");
		ArrayList fieldC5ValList = getFieldValueList(staticFormDets.getFieldC5ValList(), "fieldValue");
		ArrayList fieldC6ValList = getFieldValueList(staticFormDets.getFieldC6ValList(), "fieldValue");
		ArrayList fieldC7ValList = getFieldValueList(staticFormDets.getFieldC7ValList(), "fieldValue");

		//for field c1
		usr1PrevModel.setFieldC1RefName(staticFormDets.getFieldC1RefName());
		usr1PrevModel.setFieldC1DispName(staticFormDets.getFieldC1DispName());
		usr1PrevModel.setFieldC1List(fieldC1ValList);
		usr1PrevModel.setPrevFieldC1List(staticFormDets.getPrevFieldC1ValList());

		//		for field c2
		usr1PrevModel.setFieldC2RefName(staticFormDets.getFieldC2RefName());
		usr1PrevModel.setFieldC2DispName(staticFormDets.getFieldC2DispName());
		usr1PrevModel.setFieldC2List(fieldC2ValList);
		usr1PrevModel.setPrevFieldC2List(staticFormDets.getPrevFieldC2ValList());

		//		for field c3
		usr1PrevModel.setFieldC3RefName(staticFormDets.getFieldC3RefName());
		usr1PrevModel.setFieldC3DispName(staticFormDets.getFieldC3DispName());
		usr1PrevModel.setFieldC3List(fieldC3ValList);
		usr1PrevModel.setPrevFieldC3List(staticFormDets.getPrevFieldC3ValList());

		//		for field c4
		usr1PrevModel.setFieldC4RefName(staticFormDets.getFieldC4RefName());
		usr1PrevModel.setFieldC4DispName(staticFormDets.getFieldC4DispName());
		usr1PrevModel.setFieldC4List(fieldC4ValList);
		usr1PrevModel.setPrevFieldC4List(staticFormDets.getPrevFieldC4ValList());

		//		for field c5
		usr1PrevModel.setFieldC5RefName(staticFormDets.getFieldC5RefName());
		usr1PrevModel.setFieldC5DispName(staticFormDets.getFieldC5DispName());
		usr1PrevModel.setFieldC5List(fieldC5ValList);
		usr1PrevModel.setPrevFieldC5List(staticFormDets.getPrevFieldC5ValList());

		//		for field c6
		usr1PrevModel.setFieldC6RefName(staticFormDets.getFieldC6RefName());
		usr1PrevModel.setFieldC6DispName(staticFormDets.getFieldC6DispName());
		usr1PrevModel.setFieldC6List(fieldC6ValList);
		usr1PrevModel.setPrevFieldC6List(staticFormDets.getPrevFieldC6ValList());

		//		for field c7
		usr1PrevModel.setFieldC7RefName(staticFormDets.getFieldC7RefName());
		usr1PrevModel.setFieldC7DispName(staticFormDets.getFieldC7DispName());
		usr1PrevModel.setFieldC7List(fieldC7ValList);
		usr1PrevModel.setPrevFieldC7List(staticFormDets.getPrevFieldC7ValList());

		return usr1PrevModel;
	}

	/**
		 * This method will retrieve the Model from session
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model getModEditIssueTypeDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//	set issue types
		usrSessnModel.setProbTypeList(getIssueTypeList(usrSessnModel));

		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(MODIFYISSUEIDENTFDEFAULT);
		usrSessnModel.setSubtypeActionState(0); //set to 0 from 1000 

		return usrSessnModel;

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */	
	public EtsIssProbInfoUsr1Model getModEditCustFields() throws SQLException, Exception {
						
		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(MODIFYEDITCUSTOMFIELDS);
		usrSessnModel.setSubtypeActionState(0); //set to 0 from 1000 

		return usrSessnModel;

	}

	public EtsIssProbInfoUsr1Model getModEditCustFieldsCont() throws SQLException, Exception {

		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("edge_problem_id"));				
		
		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();
	
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadModifyEditCustFieldsIntoUsr1Model();
		
		//check for any error msgs from the form model for scrn1
		String errMsg = validateModifyEditCustomFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg Modify/getNewContDescrDetails ()=====" + errMsg);
		
		int cancelstate = getCancelActionState();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC1List() )) 
				usrSessnModel.setPrevFieldC1List(usrParamsInfo1.getPrevFieldC1List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC2List())) 
				usrSessnModel.setPrevFieldC2List(usrParamsInfo1.getPrevFieldC2List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC3List())) 
				usrSessnModel.setPrevFieldC3List(usrParamsInfo1.getPrevFieldC3List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC4List())) 
				usrSessnModel.setPrevFieldC4List(usrParamsInfo1.getPrevFieldC4List());			

			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC5List() )) 
				usrSessnModel.setPrevFieldC5List(usrParamsInfo1.getPrevFieldC5List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC6List())) 
				usrSessnModel.setPrevFieldC6List(usrParamsInfo1.getPrevFieldC6List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC7List())) 
				usrSessnModel.setPrevFieldC7List(usrParamsInfo1.getPrevFieldC7List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC8List())) 
				usrSessnModel.setPrevFieldC8List(usrParamsInfo1.getPrevFieldC8List());			

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state
			usrSessnModel.setNextActionState(MODIFYVALIDERRCONTCUSTFIELD);

		}  else {
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC1List() )) 
				usrSessnModel.setPrevFieldC1List(usrParamsInfo1.getPrevFieldC1List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC2List())) 
				usrSessnModel.setPrevFieldC2List(usrParamsInfo1.getPrevFieldC2List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC3List())) 
				usrSessnModel.setPrevFieldC3List(usrParamsInfo1.getPrevFieldC3List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC4List())) 
				usrSessnModel.setPrevFieldC4List(usrParamsInfo1.getPrevFieldC4List());			

			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC5List() )) 
				usrSessnModel.setPrevFieldC5List(usrParamsInfo1.getPrevFieldC5List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC6List())) 
				usrSessnModel.setPrevFieldC6List(usrParamsInfo1.getPrevFieldC6List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC7List())) 
				usrSessnModel.setPrevFieldC7List(usrParamsInfo1.getPrevFieldC7List());
			
			if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevFieldC8List())) 
				usrSessnModel.setPrevFieldC8List(usrParamsInfo1.getPrevFieldC8List());			

			
			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, edgeProblemId);
			
			
			//set all states
			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate);
			usrSessnModel.setNextActionState(MODIFYEDITCUSTOMFIELDSCONT);
			usrSessnModel.setSubtypeActionState(0); //set to 0 from 1000 
			

		} //end of no err msg
		return usrSessnModel;		
	}	
	
	
	
	/**
	 * This method will retrieve the Model from session
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsIssProbInfoUsr1Model getModCancAddIssueTypeDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state always to scrn 5
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from
		return usrSessnModel;

	}

	/**
					 * This method will retrieve the Model from session
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsIssProbInfoUsr1Model getModCancelSubTypeDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get cancelstate
		int cancelstate = getCancelActionState();

		int subtypestate = getSubtypeActionState();

		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);

		if (subtypestate == 0) {

			usrSessnModel.setNextActionState(MODIFYISSUEIDENTFDEFAULT);
		}

		if (subtypestate == 1000) {

			usrSessnModel.setNextActionState(MODIFYEDITISSUEIDENTF);
		}

		if (subtypestate == 1001) {

			usrSessnModel.setNextActionState(MODIFYGOSUBTYPEA);
		}
		if (subtypestate == 1002) {

			usrSessnModel.setNextActionState(MODIFYGOSUBTYPEB);
		}

		if (subtypestate == 1003) {

			usrSessnModel.setNextActionState(MODIFYGOSUBTYPEC);
		}

		return usrSessnModel;

	}

	/**
				 * This method will retrieve the Model from session
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public EtsIssProbInfoUsr1Model getModCancelIssueIdentDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state always to scrn 5
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from
		return usrSessnModel;

	}

	/**
					 * This method will retrieve the Model from session
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsIssProbInfoUsr1Model getModUnifiedCancel() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the old issue identf details from db//
		EtsIssProbInfoUsr1Model dbModel = getIssueIdentfDetailsFromDb();

		//////////////////////

		//prev field lists
		ArrayList prevFieldC1List = new ArrayList();
		ArrayList prevFieldC2List = new ArrayList();
		ArrayList prevFieldC3List = new ArrayList();
		ArrayList prevFieldC4List = new ArrayList();
		ArrayList prevFieldC5List = new ArrayList();
		ArrayList prevFieldC6List = new ArrayList();
		ArrayList prevFieldC7List = new ArrayList();
		ArrayList prevFieldC8List = new ArrayList();

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC1List())) {

			prevFieldC1List = dbModel.getPrevFieldC1List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC2List())) {

			prevFieldC2List = dbModel.getPrevFieldC2List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC3List())) {

			prevFieldC3List = dbModel.getPrevFieldC3List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC4List())) {

			prevFieldC4List = dbModel.getPrevFieldC4List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC5List())) {

			prevFieldC5List = dbModel.getPrevFieldC5List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC6List())) {

			prevFieldC6List = dbModel.getPrevFieldC6List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC7List())) {

			prevFieldC7List = dbModel.getPrevFieldC7List();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevFieldC8List())) {

			prevFieldC8List = dbModel.getPrevFieldC8List();
		}
		//
		ArrayList prevSubTypeAList = new ArrayList();
		ArrayList prevSubTypeBList = new ArrayList();
		ArrayList prevSubTypeCList = new ArrayList();
		ArrayList prevSubTypeDList = new ArrayList();

		//add vals to lists

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevSubTypeAList())) {

			prevSubTypeAList = dbModel.getPrevSubTypeAList();

		}
		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevSubTypeBList())) {

			prevSubTypeBList = dbModel.getPrevSubTypeBList();

		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevSubTypeCList())) {

			prevSubTypeCList = dbModel.getPrevSubTypeCList();

		}

		if (EtsIssFilterUtils.isArrayListDefnd(dbModel.getPrevSubTypeDList())) {

			prevSubTypeDList = dbModel.getPrevSubTypeDList();

		}

		//set prob type
		usrSessnModel.setPrevProbTypeList(dbModel.getPrevProbTypeList());

		//set field c1..c7 and testcase
		usrSessnModel.setPrevFieldC1List(prevFieldC1List);
		usrSessnModel.setPrevFieldC2List(prevFieldC2List);
		usrSessnModel.setPrevFieldC3List(prevFieldC3List);
		usrSessnModel.setPrevFieldC4List(prevFieldC4List);
		usrSessnModel.setPrevFieldC5List(prevFieldC5List);
		usrSessnModel.setPrevFieldC6List(prevFieldC6List);
		usrSessnModel.setPrevFieldC7List(prevFieldC7List);
		usrSessnModel.setPrevFieldC8List(prevFieldC8List);
		usrSessnModel.setTestCase(AmtCommonUtils.getTrimStr(dbModel.getTestCase()));

		//set sub types
		usrSessnModel.setPrevSubTypeAList(prevSubTypeAList);
		usrSessnModel.setPrevSubTypeBList(prevSubTypeBList);
		usrSessnModel.setPrevSubTypeCList(prevSubTypeCList);
		usrSessnModel.setPrevSubTypeDList(prevSubTypeDList);

		//set issue source and access

		//ISSUE HIDDEN

		usrSessnModel.setIssueSource(AmtCommonUtils.getTrimStr(dbModel.getIssueSource()));
		usrSessnModel.setIssueType(AmtCommonUtils.getTrimStr(dbModel.getIssueType()));
		usrSessnModel.setPrevIssueTypeList(dbModel.getPrevIssueTypeList());

		//////////////////////

		int cancelstate = getCancelActionState();

		usrSessnModel.setErrMsg("");
		usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE); //make next state always to scrn 5
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from
		return usrSessnModel;

	}

	/**
		 * submit to db
		 */

	public boolean submitUsr1InfoToDb() {

		boolean successsubmit = false;

		try {

			//get the final issue details

			EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

			///key info

			String applnId = AmtCommonUtils.getTrimStr(usrSessnModel.getApplnId());
			String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());
			String cqTrkId = AmtCommonUtils.getTrimStr(usrSessnModel.getCqTrkId());
			String probClass = AmtCommonUtils.getTrimStr(usrSessnModel.getProbClass());
			String probState = AmtCommonUtils.getTrimStr(usrSessnModel.getProbState());
			int seq_no = usrSessnModel.getSeqNo();
			String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId());

			//prob descr		
			String prevProbSeverity = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevProbSevList().get(0));
			String probTitle = AmtCommonUtils.getTrimStr(usrSessnModel.getProbTitle());
			String probType = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevProbTypeList().get(0));
			String probDesc = AmtCommonUtils.getTrimStr(usrSessnModel.getProbDesc());

			//step 2: issue ident
			//parse the prob type to get issue type/issue source
			//EtsIssActionGuiUtils actGuiUtil = new EtsIssActionGuiUtils();
			//EtsDropDownDataBean dropBean = actGuiUtil.getIssueTypeDropDownAttrib(probType);

			//get the parsed vals
			//String issueType = dropBean.getIssueType();
			//String issueSource = dropBean.getIssueSource();
			String issueType = usrSessnModel.getIssueType();
			String issueSource = usrSessnModel.getIssueSource();

			//dyn vals//
			String prevSubTypeAVal = "";
			String prevSubTypeBVal = "";
			String prevSubTypeCVal = "";
			String prevSubTypeDVal = "";

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevSubTypeAList())) {

				prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevSubTypeAList().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevSubTypeBList())) {

				prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevSubTypeBList().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevSubTypeCList())) {

				prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevSubTypeCList().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevSubTypeDList())) {

				prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevSubTypeDList().get(0));

			}

			//static vals//
			String prevFieldC1Val = "";
			String prevFieldC2Val = "";
			String prevFieldC3Val = "";
			String prevFieldC4Val = "";
			String prevFieldC5Val = "";
			String prevFieldC6Val = "";
			String prevFieldC7Val = "";
			String prevFieldC8Val = "";

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC1List())) {

				prevFieldC1Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC1List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC2List())) {

				prevFieldC2Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC2List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC3List())) {

				prevFieldC3Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC3List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC4List())) {

				prevFieldC4Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC4List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC5List())) {

				prevFieldC5Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC5List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC6List())) {

				prevFieldC6Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC6List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC7List())) {

				prevFieldC7Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC7List().get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(usrSessnModel.getPrevFieldC8List())) {

				prevFieldC8Val = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevFieldC8List().get(0));

			}
			
			String testCase = AmtCommonUtils.getTrimStr(usrSessnModel.getTestCase());

			//

			String fieldC14Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC14()); //first name
			String fieldC15Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC15()); //last name	

			//step 5:
			ArrayList prevNotifyList = usrSessnModel.getPrevNotifyList();

			ArrayList dupNotifyList = new ArrayList();

			//to remove duplicates//
			if (EtsIssFilterUtils.isArrayListDefnd(prevNotifyList)) {

				int mailsize = prevNotifyList.size();

				for (int i = 0; i < mailsize; i++) {

					if (!dupNotifyList.contains(prevNotifyList.get(i)))
						dupNotifyList.add(prevNotifyList.get(i));
				}
			}

			StringBuffer sbnotify = new StringBuffer();

			if (EtsIssFilterUtils.isArrayListDefnd(dupNotifyList)) {

				int dupsize = dupNotifyList.size();

				for (int i = 0; i < dupsize; i++) {

					sbnotify.append(dupNotifyList.get(i));

					if (i != dupsize - 1) {

						sbnotify.append(",");

					}

				}
			}

			String comments = usrSessnModel.getCommFromCust();

			Global.println("mail string in modify===" + sbnotify.toString());

			//			get data_id based on issue type name
			EtsDropDownDAO dropDao = new EtsDropDownDAO();
			String issueTypeId = dropDao.getDataIdFromIssueType(projectId, probClass, issueType);

			//NAME AND VAL

			ETSMWIssue issue = getETSMWIssueFromInfoModel(usrSessnModel);

			//key details
			issue.application_id = applnId;
			issue.cq_trk_id = cqTrkId;
			issue.edge_problem_id = edgeProblemId;
			issue.seq_no = seq_no + 4; //seq by 4

			//issue desc
			issue.severity = prevProbSeverity;
			issue.title = ETSUtils.escapeString(probTitle);
			Global.println("1ST DESC=="+probDesc);
			issue.problem_desc = ETSUtils.escapeString(probDesc);
			issue.problem_state = "Modify";

			//issue ident
			//			trim issue type to 50 chars, just before to DB as usr1 limitation is 50 chars
			if (issueType.length() > 50) {

				issueType = issueType.substring(0, 50);
			}
			issue.problem_type = issueType;
			issue.issueTypeId = issueTypeId;

			///dyn vals//
			issue.subTypeA = prevSubTypeAVal;
			issue.subTypeB = prevSubTypeBVal;
			issue.subTypeC = prevSubTypeCVal;
			issue.subTypeD = prevSubTypeDVal;

			////static vals
			issue.field_C1 = prevFieldC1Val;
			issue.field_C2 = prevFieldC2Val;
			issue.field_C3 = prevFieldC3Val;
			issue.field_C4 = prevFieldC4Val;
			issue.field_C5 = prevFieldC5Val;
			issue.field_C6 = prevFieldC6Val;
			issue.field_C7 = prevFieldC7Val;
			issue.field_C8 = prevFieldC8Val;
			issue.test_case = ETSUtils.escapeString(testCase);

			issue.field_C14 = getEtsIssObjKey().getEs().gFIRST_NAME;
			issue.field_C15 = getEtsIssObjKey().getEs().gLAST_NAME;
			///////////issue access
			issue.issue_source = issueSource;

			//commenst
			issue.comm_from_cust = ETSUtils.escapeString(comments);

			//notification
			issue.ets_cclist = ETSUtils.escapeString(sbnotify.toString());
			//last user id

			issue.last_userid = getEtsIssObjKey().getEs().gUSERN;

			Global.println("issue serevrity==" + issue.severity);
			Global.println("issue title==" + issue.title);
			Global.println("PROB DESC==="+issue.problem_desc);

			try {

				IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
				IssMWProcessor mwproc = createMWproc.factoryMethod(getEtsIssObjKey());
				mwproc.setIssue(issue);
				successsubmit = mwproc.processRequest();

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			Commented by v2sagar for PROBLEM_INFO_USR2
			/*try {

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
		String errMsg = validateCommentsFormFields(usrParamsInfo1);

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
			usrSessnModel.setNextActionState(MODIFYISSUEFIRSTPAGE);

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
	public String validateCommentsFormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//cehck for comments

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getCommFromCust())) {

			errsb.append("Please provide comments.");
			errsb.append("<br />");

		} else {

			String tempDesc = AmtCommonUtils.getTrimStr(usrParamsInfo1.getCommFromCust());

			if (tempDesc.length() > 32700) {

				errsb.append("Please provide maximum of 32700 characters for comments.");
				errsb.append("<br />");

			}
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

	public EtsIssProbInfoUsr1Model getSubmitModIssueDetails() {

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		boolean successsubmit = submitUsr1InfoToDb();

		if (!successsubmit) {

			usrSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("An error has occured while modifying the issue. Please try after sometime or contact");
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

} //end of class
