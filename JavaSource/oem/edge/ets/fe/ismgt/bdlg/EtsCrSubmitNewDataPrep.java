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
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFileAttachUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsCrAttach;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsCrProcessConstants;
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
public class EtsCrSubmitNewDataPrep extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants, EtsCrProcessConstants {

	public static final String VERSION = "1.18.2.4";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;

	/**
	 * @param etsIssObjKey
	 */
	public EtsCrSubmitNewDataPrep(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
		 * To get the Issue from sessn
		 */

	public EtsCrProbInfoModel getCrInfoFromSessn() {

		EtsCrProbInfoModel crInfoModel = getActSessnParams().getSessnCrProbInfoModel("");

		return crInfoModel;

	}

	/**
		 * 
		 * To set the usr1 info into session
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public void setCrInfoIntoSessn(EtsCrProbInfoModel crInfoModel, String uniqObjId) {

		getActSessnParams().setSessnCrProbInfoModel(crInfoModel, uniqObjId);
	}

	/**
	  * This method will model the data required for FE while submittng the problem and send in the usr1 model
	  */

	public EtsCrProbInfoModel getCrInfoKeyDetails() throws SQLException, Exception {

		EtsCrProbInfoModel crKeyModel = new EtsCrProbInfoModel();

		//descModel.set for prev values
		EtsCrProbInfoModel crInfoModel = getCrInfoFromSessn();

		String uniqEtsId = "";

		if (crInfoModel != null) {

			uniqEtsId = AmtCommonUtils.getTrimStr(crInfoModel.getEtsId());

		}

		if (!AmtCommonUtils.isResourceDefined(uniqEtsId)) {

			uniqEtsId = EtsIssFilterUtils.getUniqEdgeProblemId(getEtsIssObjKey().getEs().gUSERN);
		}

		//get the pmo office details

		//get pmo project id, for a given project id
		String pmoProjectId = getEtsIssObjKey().getProj().getPmo_project_id();

		//get pmo id based on pmo proj id
		EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
		ETSPMOffice pmoOffice = crPmoDao.getPMOfficeObjectDetailForCr(pmoProjectId);

		//get Parent PMO ID, where type=CRIFolder
		String parentPmoId = pmoOffice.getPMOID();

		//get Uniq ref no
		int uniqRefNo = EtsIssFilterUtils.getUniqRefNoInt();

		Global.println("Integer Uniq Ref No ====" + uniqRefNo);

		crKeyModel.setEtsId(uniqEtsId);
		crKeyModel.setPmoId(""); //cehck with subu
		crKeyModel.setPmoProjectId(pmoProjectId); //cehck with susbu
		crKeyModel.setParentPmoId(parentPmoId); //parent pmo id
		crKeyModel.setRefNo(uniqRefNo); //cehck with subu
		crKeyModel.setInfoSrcFlag("E"); //from ETS
		crKeyModel.setStateAction(CREATESTATEACTION); //Submitted/Under Review
		//crKeyModel.setProbClass(ETSCHANGESUBTYPE); //Defect
		crKeyModel.setProbClass(ETSPMOCHANGESUBTYPE); //CHANGEREQUEST
		crKeyModel.setCrType(CRTXNTYPE); //CHANGEREQUEST
		crKeyModel.setLastUserId(getEtsIssObjKey().getEs().gUSERN);

		//set in the session

		return crKeyModel;
	}

	/**
		 * This method will model the issue description data 
		 */

	public EtsCrProbInfoModel getCrDescrpDetails() throws SQLException, Exception {

		//current one
		EtsCrProbInfoModel crModel = new EtsCrProbInfoModel();

		//descModel.set for prev values
		EtsCrProbInfoModel prevCrModel = getCrInfoFromSessn();

		//get Key Model

		EtsCrProbInfoModel crKeyModel = getCrInfoKeyDetails();

		//set key details
		crModel.setEtsId(crKeyModel.getEtsId());
		crModel.setPmoId(crKeyModel.getPmoId());
		crModel.setPmoProjectId(crKeyModel.getPmoProjectId());
		crModel.setParentPmoId(crKeyModel.getParentPmoId());
		crModel.setRefNo(crKeyModel.getRefNo());
		crModel.setInfoSrcFlag(crKeyModel.getInfoSrcFlag()); //from ETS
		//static contsnats
		crModel.setStateAction(crKeyModel.getStateAction());
		crModel.setProbClass(crKeyModel.getProbClass());
		crModel.setCrType(crKeyModel.getCrType());
		//	last userid
		crModel.setLastUserId(crKeyModel.getLastUserId());

		///submitter details
		crModel.setCustName(getEtsIssObjKey().getEs().gFIRST_NAME + " " + getEtsIssObjKey().getEs().gLAST_NAME);
		crModel.setCustEmail(getEtsIssObjKey().getEs().gEMAIL);
		crModel.setCustPhone(getEtsIssObjKey().getEs().gPHONE);

		if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I")) {

			crModel.setCustCompany("IBM");

		} else {

			crModel.setCustCompany(getEtsIssObjKey().getEs().gASSOC_COMP);
		}
		crModel.setProbCreator(getEtsIssObjKey().getEs().gUSERN); //here ir userid in CR
		crModel.setCreationDate(EtsIssFilterUtils.getCurDtSqlTimeStamp());

		//descModel.set for current values
		crModel.setProbSevList(getFilterDAO().getSeverityTypes());
		crModel.setProbTitle("");
		crModel.setProbDesc("");

		if (prevCrModel != null) {

			crModel.setPrevProbSevList(prevCrModel.getPrevProbSevList());
			crModel.setProbTitle(prevCrModel.getProbTitle());
			crModel.setProbDesc(prevCrModel.getProbDesc());

		}

		return crModel;

	}

	/**
	  * This method will model the data required for FE while submittng the problem and send in the usr1 model
	  */

	public EtsCrProbInfoModel getNewInitialDetails() throws SQLException, Exception {

		setCrInfoIntoSessn(null, "");

		EtsCrProbInfoModel crInfo = getCrDescrpDetails();

		setCrInfoIntoSessn(crInfo, "");

		//set all states
		crInfo.setCurrentActionState(currentstate);

		int cancelstate = getCancelActionState();
		crInfo.setCancelActionState(cancelstate);

		return crInfo;

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
			 * This method will load step1 details into session and get the step 2 details
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public EtsCrProbInfoModel getNewContDescrDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//get the params model frm the form
		EtsCrProbInfoModel crParamsInfo = parseParams.loadNewScr1ParamsIntoCrModel();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateNewScrn1FormFields(crParamsInfo);

		//print error msg
		Global.println("err msg getNewContDescrDetails()=====" + errMsg);

		//get cancel state or scrn state, if cancelstate==0 means it started from 1st screen
		int cancelstate = getCancelActionState();

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//	add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			crSessnModel.setPrevProbSevList(crParamsInfo.getPrevProbSevList()); //add severity
			crSessnModel.setProbTitle(crParamsInfo.getProbTitle()); //add title
			crSessnModel.setProbDesc(crParamsInfo.getProbDesc()); //add descr

			crSessnModel.setErrMsg(errMsg);
			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(cancelstate); //make cancel state
			crSessnModel.setNextActionState(VALIDERRCONTDESCR);

		} else {

			//add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			///
			crSessnModel.setPrevProbSevList(crParamsInfo.getPrevProbSevList()); //add severity
			crSessnModel.setProbTitle(crParamsInfo.getProbTitle()); //add title
			crSessnModel.setProbDesc(crParamsInfo.getProbDesc()); //add descr

			//

			//upload the updated object into session//
			setCrInfoIntoSessn(crSessnModel, "");

			if (cancelstate == 0) {

				//	add the sub type details/or field lists to the existing one

				crSessnModel.setErrMsg("");
				crSessnModel.setCurrentActionState(currentstate);
				crSessnModel.setCancelActionState(1); //make cancel state=1 i.e increase by 1	
				crSessnModel.setNextActionState(0);

			}

			if (cancelstate > 0) { //if original state is from different screen

				crSessnModel.setErrMsg("");
				crSessnModel.setNextActionState(getNextActionState(cancelstate)); //make next state from where it came from
				crSessnModel.setCurrentActionState(currentstate);
				crSessnModel.setCancelActionState(cancelstate); //make it to one from where it came from

			} //end of cancelstate > 0

		} //end of no err msg

		return crSessnModel;

	}

	public int getNextActionState(int cancelstate) {

		int nextstate = 0;

		switch (cancelstate) {

			case 0 :

				nextstate = NEWINITIAL;

				break;

			case 1 :

				nextstate = CONTDESCR;

				break;

			case 2 :

				nextstate = ADDFILEATTACH;

				break;

		}

		return nextstate;
	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateNewScrn1FormFields(EtsCrProbInfoModel crParamsInfo) throws Exception {

		StringBuffer errsb = new StringBuffer();

		//check for issue severity

		if (EtsIssFilterUtils.isArrayListDefnd(crParamsInfo.getPrevProbSevList())) {

			if (crParamsInfo.getPrevProbSevList().contains("NONE")) {

				errsb.append("Please select priority.");
				errsb.append("<br />");

			}
		}

		//cehck for issue title

		if (!AmtCommonUtils.isResourceDefined(crParamsInfo.getProbTitle())) {

			errsb.append("Please provide title.");
			errsb.append("<br />");

		} else {

			String tempTitle = AmtCommonUtils.getTrimStr(crParamsInfo.getProbTitle());

			if (tempTitle.length() > 125) {

				errsb.append("Please provide maximum of 125 characters for title.");
				errsb.append("<br />");

			}
		}

		//cehck for issue description

		if (!AmtCommonUtils.isResourceDefined(crParamsInfo.getProbDesc())) {

			errsb.append("Please provide description.");
			errsb.append("<br />");

		} else {

			String tempDesc = AmtCommonUtils.getTrimStr(crParamsInfo.getProbDesc());

			if (tempDesc.length() > 1000) {

				errsb.append("Please provide maximum of 1000 characters for description.");
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

	public EtsCrProbInfoModel getCancelDescrDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//get cancelstate
		int cancelstate = getCancelActionState();

		if (cancelstate == 0) {

			//add next cancel state 
			crSessnModel.setNextActionState(MAINPAGE);

		}

		if (cancelstate == 1) {

			//add next cancel state 
			crSessnModel.setNextActionState(CONTDESCR);

		}

		if (cancelstate > 1) {

			//add next cancel state 
			crSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		return crSessnModel;

	}

	/**
		 * This method will retrieve the Model from session
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsCrProbInfoModel getEditIssueDescrDetails() throws SQLException, Exception {

		//get the data from session//
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		int cancelstate = getCancelActionState();

		//set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
		 * 
		 * @return
		 */

	public EtsCrProbInfoModel doFileAttach() {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		Global.println("file desc===" + (String) getEtsIssObjKey().getParams().get("file_desc"));
		Global.println("file name===" + (String) getEtsIssObjKey().getParams().get("upload_file"));
		String etsId = (String) getEtsIssObjKey().getParams().get("etsId");
		Global.println("etsId===" + etsId);

		String fileErrMsg = "";

		if (!AmtCommonUtils.isResourceDefined(fileErrMsg)) {

			String errFile[] = fileUtils.doAttachForCr(getEtsIssObjKey().getRequest(), crSessnModel);
			fileErrMsg = errFile[1];

		}

		int cancelstate = getCancelActionState();

		crSessnModel.setErrMsg(fileErrMsg);
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate); //make cancel state		
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
			 * 
			 * @return
			 */

	public EtsCrProbInfoModel deleteFileAttach() {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		String etsId = crSessnModel.getEtsId();
		int fileNum = getEtsIssObjKey().getFilenum();
		Global.println("etsId===" + etsId);
		Global.println("file num===" + fileNum);

		boolean success = true;

		try {

			EtsCrPmoIssueDocDAO crDocdao = new EtsCrPmoIssueDocDAO();

			crDocdao.deleteAttach(etsId, fileNum);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			success = false;
		}

		int cancelstate = getCancelActionState();

		if (cancelstate == 1) {

			crSessnModel.setErrMsg("");
			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(cancelstate); //make cancel state		
			crSessnModel.setNextActionState(0);

		}

		if (cancelstate > 1) {

			crSessnModel.setErrMsg("");
			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(cancelstate); //retain the old state
			crSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		return crSessnModel;

	}

	/**
				 * This method will retrieve the Model from session
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public EtsCrProbInfoModel getCancelFileAttachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//delete all the files currently attached
		EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
		int deletecount = crIssueDao.deleteAttachWithFileTmpFlg(crSessnModel.getEtsId());

		//get cancelstate
		int cancelstate = getCancelActionState();

		if (cancelstate == 1) {

			//add next cancel state 
			crSessnModel.setNextActionState(MAINPAGE);

		}

		if (cancelstate > 1) {

			//add next cancel state 
			crSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		return crSessnModel;

	}

	/**
		  * 
		  * @return
		  */

	public EtsCrProbInfoModel getContFileattachDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//update files flag form T >> Y
		EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
		int updcount = crIssueDao.updateTempAttachments(crSessnModel.getEtsId());

		int cancelstate = getCancelActionState();

		if (cancelstate == 1) {

			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(2); //increment by 1 to screen -3
			crSessnModel.setNextActionState(0);

		}

		if (cancelstate > 1) {

			crSessnModel.setCurrentActionState(currentstate);
			crSessnModel.setCancelActionState(cancelstate); //retain the old state
			crSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		crSessnModel.setErrMsg("");

		return crSessnModel;

	}

	/**
		 * 
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsCrProbInfoModel getEditFileAttachDetails() throws SQLException, Exception {

		//		get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		//		set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);
		crSessnModel.setNextActionState(0);

		return crSessnModel;

	}

	/**
		 * submit to db
		 */

	public boolean submitCRInfoToDb() {

		boolean successsubmit = false;
		boolean successtxn = false;
		boolean successfiles = false;

		try {

			//		get from the session the latest model
			EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

			///key info
			String etsId = AmtCommonUtils.getTrimStr(crSessnModel.getEtsId());
			String pmoId = AmtCommonUtils.getTrimStr(crSessnModel.getPmoId());
			String pmoProjectId = AmtCommonUtils.getTrimStr(crSessnModel.getPmoProjectId());
			String parentPmoId = AmtCommonUtils.getTrimStr(crSessnModel.getParentPmoId());
			int refNo = crSessnModel.getRefNo();
			String infoSrcFlag = AmtCommonUtils.getTrimStr(crSessnModel.getInfoSrcFlag());

			// step 1: submitter profile//
			String custName = AmtCommonUtils.getTrimStr(crSessnModel.getCustName());
			String custCompany = AmtCommonUtils.getTrimStr(crSessnModel.getCustCompany());
			String custEmail = AmtCommonUtils.getTrimStr(crSessnModel.getCustEmail());
			String custPhone = AmtCommonUtils.getTrimStr(crSessnModel.getCustPhone());
			///
			String stateAction = AmtCommonUtils.getTrimStr(crSessnModel.getStateAction());
			String probCreator = AmtCommonUtils.getTrimStr(crSessnModel.getProbCreator());

			//prob descr		
			String probClass = AmtCommonUtils.getTrimStr(crSessnModel.getProbClass());
			String probTitle = AmtCommonUtils.getTrimStr(crSessnModel.getProbTitle());
			String prevProbSeverity = AmtCommonUtils.getTrimStr((String) crSessnModel.getPrevProbSevList().get(0));
			String cRType = AmtCommonUtils.getTrimStr(crSessnModel.getCrType());
			String probDesc = AmtCommonUtils.getTrimStr(crSessnModel.getProbDesc());

			//comments
			String commFromCust = AmtCommonUtils.getTrimStr(getCommentsStrForAttachedFiles(etsId));

			//NAME AND VAL

			EtsCrDbModel crDbModel = new EtsCrDbModel();

			//key details
			crDbModel.etsId = etsId;
			crDbModel.pmoId = pmoId;
			crDbModel.pmoProjectId = pmoProjectId;
			crDbModel.parentPmoId = parentPmoId;
			crDbModel.refNo = 0;
			crDbModel.infoSrcFlag = infoSrcFlag;

			//submitter profile
			crDbModel.custName = custName;
			crDbModel.custCompany = custCompany;
			crDbModel.custEmail = custEmail;
			crDbModel.custPhone = custPhone;
			crDbModel.stateAction = stateAction;
			crDbModel.probCreator = probCreator;

			//issue desc
			crDbModel.probClass = probClass;
			crDbModel.probTitle = ETSUtils.escapeString(probTitle);
			crDbModel.probSeverity = prevProbSeverity;
			crDbModel.CRType = cRType;
			crDbModel.probDesc = ETSUtils.escapeString(probDesc);

			//commenst
			crDbModel.commFromCust = ETSUtils.escapeString(commFromCust);

			//last user id

			crDbModel.lastUserId = crSessnModel.getLastUserId();

			//txn flag
			crDbModel.statusFlag = "C";

			EtsCrPmoDAO pmoDao = new EtsCrPmoDAO();

			EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

			try {

				successsubmit = pmoDao.addNewCR(crDbModel);

				successtxn = pmoDao.addNewCRTxn(crDbModel);

				docDao.updateAttachFilesWithFlg(crDbModel.etsId, "E");

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return (successsubmit && successtxn);

	}

	public String getCommentsStrForAttachedFiles(String etsId) {

		StringBuffer sb = new StringBuffer();

		EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

		Vector fileList = docDao.getAttachedFiles(etsId);

		int filesize = 0;

		String docName = "";
		String userName = "";
		String lastUserIrId = "";
		String lastTimeStampStr = "";

		if (fileList != null && !fileList.isEmpty()) {

			filesize = fileList.size();
		}

		for (int i = 0; i < filesize; i++) {

			EtsCrAttach attach = (EtsCrAttach) fileList.get(i);

			//

			if (attach != null) {

				docName = attach.getDocName();
				userName = attach.getLastUserName();
				lastUserIrId = attach.getLastUserIrId();
				lastTimeStampStr = attach.getTimeStampString();

			}

			if (i == 0) {

				sb.append("---File attachment(s) in ICC\n");
			}

			//sb.append("Document  " + docName + " has been attached by " + userName + " [IBM ID: " + lastUserIrId + "] on " + lastTimeStampStr + " ");

			if (AmtCommonUtils.isResourceDefined(lastUserIrId)) {

				sb.append("Document  " + docName + " has been attached by " + userName + " [" + lastUserIrId + "] on " + lastTimeStampStr + " ");
				
			} else {

				sb.append("Document  " + docName + " has been attached on " + lastTimeStampStr + " ");
			}
			sb.append("\n");

		}

		return sb.toString();
	}

	/**
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public EtsCrProbInfoModel getSubmitCRDetails() {

		//		get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		boolean successsubmit = submitCRInfoToDb();

		if (!successsubmit) {

			crSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("An error has occured while submitting the change request. Please try after sometime or contact");
			sberr.append(" Customer Connect Help Desk for more help.");

			//set error msg
			getEtsIssObjKey().getRequest().setAttribute("actionerrmsg", sberr.toString());

		} else {

			crSessnModel.setNextActionState(0);

		}

		//		set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);

		return crSessnModel;

	}

	/**
					 * This method will retrieve the Model from session
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsCrProbInfoModel getCancelFinalSubmitDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsCrProbInfoModel crSessnModel = getCrInfoFromSessn();

		//delete all the files currently attached
		EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();
		int deletecount = crIssueDao.deleteAttachWithFileTmpFlg(crSessnModel.getEtsId());

		//delete with Y flag also
		int deletenew = crIssueDao.deleteAttachWithFileFlg(crSessnModel.getEtsId(), "Y");

		//get cancelstate
		int cancelstate = getCancelActionState();

		//add next cancel state 
		crSessnModel.setNextActionState(MAINPAGE);

		//	set all states
		crSessnModel.setErrMsg("");
		crSessnModel.setCurrentActionState(currentstate);
		crSessnModel.setCancelActionState(cancelstate);

		return crSessnModel;

	}

} //end of class
