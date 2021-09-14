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
import java.util.Vector;

import javax.servlet.http.HttpSession;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Creator;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsCrAttach;
import oem.edge.ets.fe.ismgt.model.EtsCrDbModel;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
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
public class SubmitIssueBdlg extends EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.2";
	private EtsIssParseFormParams parseParams;
	private int currentstate = 0;
	private static final int SUBMIT_NEW_SUCCESS = 1;
	private static final int SUBMIT_NEW_FAILURE = 0;
	private static final int SUBMIT_NEW_DUPLICATE_ERR = -1;

	/**
		 * @param etsIssObjKey
		 */
	public SubmitIssueBdlg(EtsIssObjectKey etsIssObjKey, int currentstate) {
		super(etsIssObjKey);
		this.parseParams = new EtsIssParseFormParams(etsIssObjKey);
		this.currentstate = currentstate;

	}

	/**
		 * This method will model the data required for FE while submittng the problem and send in the usr1 model
		 */

	public EtsIssProbInfoUsr1Model getNewInitialDetails() throws SQLException, Exception {

		setUsr1InfoIntoSessn(null, "");

		EtsIssProbInfoUsr1Model usrInfo1 = getIssueDescrpDetails();

		setUsr1InfoIntoSessn(usrInfo1, "");

		//set all states
		usrInfo1.setCurrentActionState(currentstate);

		//int cancelstate = getCancelActionState();
		//611 change merge description+issue type==issue type
		//so start cancelstate with '1'

		int cancelstate = 1;
		usrInfo1.setCancelActionState(cancelstate);

		//change in workflow
		//set issue types
		usrInfo1.setProbTypeList(getIssueTypeList(usrInfo1));

		//setUsr1InfoIntoSessn(usrInfo1, "");

		return usrInfo1;

	}

	/**
			 * This method will retrieve the Model from session
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getValidErrScrn1Details() throws SQLException, Exception {

		//get from session

		return getUsr1InfoFromSessn();

	}

	
	/**
		 * This method will load step1 details into session and get the step 2 details
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsIssProbInfoUsr1Model getNewContDescrDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadFastScr1ParamsIntoUsr1Model();

		//get the check issues type ibm from form
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usrSessnModel.getChkIssTypeIbmOnly());

		//check for any error msgs from the form model for scrn1
		String errMsg = validateNewScrn1FormFields(usrParamsInfo1) + validateScrn11FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg getNewContDescrDetails()=====" + errMsg);

		//get cancel state or scrn state, if cancelstate==0 means it started from 1st screen
		int cancelstate = getCancelActionState();

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usrSessnModel.getUserType());

		//on err msg, the actions

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//	add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			usrSessnModel.setPrevProbSevList(usrParamsInfo1.getPrevProbSevList()); //add severity
			usrSessnModel.setProbTitle(usrParamsInfo1.getProbTitle()); //add title
			usrSessnModel.setProbDesc(usrParamsInfo1.getProbDesc()); //add descr

			//add the selected severity, title and descr, and problem type to sessn only
			usrSessnModel.setPrevProbTypeList(usrParamsInfo1.getPrevProbTypeList()); //add prob type

			usrSessnModel.setErrMsg(errMsg);
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state
			usrSessnModel.setNextActionState(VALIDERRCONTDESCR);

		} else {

			//611 new st

			//add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			///
			usrSessnModel.setPrevProbSevList(usrParamsInfo1.getPrevProbSevList()); //add severity
			usrSessnModel.setProbTitle(usrParamsInfo1.getProbTitle()); //add title
			usrSessnModel.setProbDesc(usrParamsInfo1.getProbDesc()); //add descr

			//	check whthere user has changed the issue type from the prev value
			boolean isIssTypeChanged = isIssueTypeChanged(usrSessnModel);

			//add the selected severity, title and descr, and problem type to sessn only
			//when there are no err msgs

			usrSessnModel.setPrevProbTypeList(usrParamsInfo1.getPrevProbTypeList()); //add prob type

			//get issue type attributes///
			String prevProbType = (String) usrParamsInfo1.getPrevProbTypeList().get(0);

			EtsIssActionGuiUtils guiUtils = new EtsIssActionGuiUtils();
			EtsDropDownDataBean dropBean = guiUtils.getIssueTypeDropDownAttrib(prevProbType);
			//set issue source
			String issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
			String issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());
			String issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());

			Global.println("issue source in getAddIssueTypeDetails ===" + issueSource);
			Global.println("issue access in getAddIssueTypeDetails ===" + issueAccess);
			Global.println("issue type in getAddIssueTypeDetails ===" + issueType);

			//set issue source/access
			usrSessnModel.setIssueSource(issueSource);
			usrSessnModel.setIssueAccess(issueAccess);
			usrSessnModel.setIssueType(issueType);

			//upload the updated object into session//
			setUsr1InfoIntoSessn(usrSessnModel, "");

			//additional code
			//Probably Phani forgot to add this if condition 
			// similar thing found in ets/bldg/modifydataprep.java line 100
			if (issueAccess.startsWith("IBM")) {
				chkIssTypeIbmOnly = "Y";
				usrSessnModel.setChkIssTypeIbmOnly("Y");
			}
			
			//just get the notify list details
			//set the norify list params
			usrSessnModel.setNotifyList(getNotifyListDetails(chkIssTypeIbmOnly, userType));

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(2); //make cancel state
			usrSessnModel.setNextActionState(ADDFILEATTACH);

		}

		return usrSessnModel;

	}

	
	public int getNextActionState(int cancelstate) {

		int nextstate = 0;

		switch (cancelstate) {

			case 1 :

				nextstate = NEWINITIAL;

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
	public String validateScrn1FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//check for issue severity

		if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevProbSevList())) {

			if (usrParamsInfo1.getPrevProbSevList().contains("NONE")) {

				errsb.append("Please select  issue severity.");
				errsb.append("<br />");

			}
		}

		//cehck for issue title

		if (!AmtCommonUtils.isResourceDefined(usrParamsInfo1.getProbTitle())) {

			errsb.append("Please provide  issue title.");
			errsb.append("<br />");

		} else {

			String tempTitle = AmtCommonUtils.getTrimStr(usrParamsInfo1.getProbTitle());

			if (tempTitle.length() > 25) {

				errsb.append("Please provide maximum of 125 chars for issue title.");
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

				errsb.append("Please provide maximum of 32700 chars for description.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

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
	 * This method will retrieve the Model from session
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsIssProbInfoUsr1Model getEditIssueDescrDetails() throws SQLException, Exception {

		//get the data from session//
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		int cancelstate = getCancelActionState();

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(0);

		return usrSessnModel;

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
		int subtypestate = usrSessnModel.getSubtypeActionState();

		if (cancelstate == 1) {

			//add next cancel state 
			usrSessnModel.setNextActionState(MAINPAGE);

		}

		if (cancelstate > 1) {

			//add next cancel state 
			usrSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		return usrSessnModel;

	}

		


	/**
		 * This method will model the data required for FE while submittng the problem and send in the usr1 model
		 */

	public EtsIssProbInfoUsr1Model getUsr1KeyDetails() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usr1KeyModel = new EtsIssProbInfoUsr1Model();

		//descModel.set for prev values
		EtsIssProbInfoUsr1Model prevUsr1Model = getUsr1InfoFromSessn();

		String uniqEdgeProblemId = "";

		if (prevUsr1Model != null) {

			uniqEdgeProblemId = AmtCommonUtils.getTrimStr(prevUsr1Model.getEdgeProblemId());

		}

		if (!AmtCommonUtils.isResourceDefined(uniqEdgeProblemId)) {

			uniqEdgeProblemId = EtsIssFilterUtils.getUniqEdgeProblemId(getEtsIssObjKey().getEs().gUSERN);
		}

		usr1KeyModel.setApplnId(ETSAPPLNID); //APPLN ID ETS
		usr1KeyModel.setCqTrkId("-");
		usr1KeyModel.setProbClass(ETSISSUESUBTYPE); //probm class=Defect
		usr1KeyModel.setEdgeProblemId(uniqEdgeProblemId);
		usr1KeyModel.setProbState("Submit"); //changed from New -> Submit
		usr1KeyModel.setSeqNo(1);

		//set in the session

		return usr1KeyModel;
	}

	/**
	 * This method will model the issue description data 
	 */

	public EtsIssProbInfoUsr1Model getIssueDescrpDetails() throws SQLException, Exception {

		//current one
		EtsIssProbInfoUsr1Model usr1Model = new EtsIssProbInfoUsr1Model();

		//descModel.set for prev values
		EtsIssProbInfoUsr1Model prevUsr1Model = getUsr1InfoFromSessn();

		//get Key Model

		EtsIssProbInfoUsr1Model usr1KeyModel = getUsr1KeyDetails();

		//set key details
		usr1Model.setApplnId(usr1KeyModel.getApplnId()); //APPLN ID ETS
		usr1Model.setCqTrkId(usr1KeyModel.getCqTrkId());
		usr1Model.setProbClass(usr1KeyModel.getProbClass()); //probm class=Defect
		usr1Model.setEdgeProblemId(usr1KeyModel.getEdgeProblemId());
		usr1Model.setProbState(usr1KeyModel.getProbState());
		usr1Model.setSeqNo(usr1KeyModel.getSeqNo());
		usr1Model.setCreationDate(EtsIssFilterUtils.getCurDtSqlTimeStamp());
		usr1Model.setCustProject(getEtsIssObjKey().getProj().getName());
		usr1Model.setEtsProjId(getEtsIssObjKey().getProj().getProjectId());

		///submitter details

		EtsIssProjectMember projMem = getSubmitterProfile();

		usr1Model.setUserType(projMem.getUserType());
		usr1Model.setCustName(projMem.getUserFullName());
		usr1Model.setCustEmail(projMem.getUserEmail());
		usr1Model.setCustPhone(projMem.getUserContPhone());
		usr1Model.setCustCompany(projMem.getUserCustCompany());
		usr1Model.setProbCreator(projMem.getUserEdgeId());
		usr1Model.setFieldC14(projMem.getUserFirstName());
		usr1Model.setFieldC15(projMem.getUserLastName());

		///////////////////////////
		usr1Model.setFieldC12(String.valueOf(getEtsIssObjKey().getTopCatId()));

		//descModel.set for current values
		usr1Model.setProbSevList(getFilterDAO().getSeverityTypes());
		usr1Model.setProbTitle("");
		usr1Model.setProbDesc("");
		//usr1Model.setProbTypeList(getIssueTypeList());

		if (prevUsr1Model != null) {

			usr1Model.setPrevProbSevList(prevUsr1Model.getPrevProbSevList());
			usr1Model.setProbTitle(prevUsr1Model.getProbTitle());
			usr1Model.setProbDesc(prevUsr1Model.getProbDesc());
			//usr1Model.setPrevProbTypeList(prevUsr1Model.getPrevProbTypeList());

		}

		return usr1Model;

	}

	/**
	 * To get the Issue from sessn
	 */

	public EtsIssProbInfoUsr1Model getUsr1InfoFromSessn() {

		EtsIssProbInfoUsr1Model usr1Model = getActSessnParams().getSessnProbUsr1InfoModel("");

		return usr1Model;

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

	public boolean isIssueTypeChanged(EtsIssProbInfoUsr1Model usr1Model) {

		String problem_type = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("problem_type"));

		ArrayList prevProbTypeList = usr1Model.getPrevProbTypeList();

		String prev_problem_type = "";

		if (EtsIssFilterUtils.isArrayListDefnd(prevProbTypeList)) {

			prev_problem_type = (String) prevProbTypeList.get(0);
		}

		if (!prev_problem_type.equals(problem_type)) {

			return true;
		} else {

			return false;
		}

	}
	
	/**
	 * 
	 * @return
	 */

	public EtsIssProbInfoUsr1Model doFileAttach() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		///get the issue_source flag, if to PMO OR ETS
		String issueSource = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueSource());
		Global.println("issue source in doFileAttach/Issues===" + issueSource);

		Global.println("file desc===" + (String) getEtsIssObjKey().getParams().get("file_desc"));
		Global.println("file name===" + (String) getEtsIssObjKey().getParams().get("upload_file"));
		String edge_problem_id = (String) getEtsIssObjKey().getParams().get("edge_problem_id");
		Global.println("edge problem id===" + edge_problem_id);
		String attch = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("attch"));
		Global.println("attch in ITAR===" + attch);

		String fileErrMsg = "";

		if (!AmtCommonUtils.isResourceDefined(fileErrMsg)) {

			if (!issueSource.equals(ETSPMOSOURCE)) {

				if (!AmtCommonUtils.isResourceDefined(attch)) {

					String errFile[] = getFileAttachUtils().doAttach(getEtsIssObjKey().getRequest());
					fileErrMsg = errFile[1];

				}

			} else {

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
		usrSessnModel.setNextActionState(0);

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

		boolean success = true;
		String issueSource = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueSource());
		String strProjectId = usrSessnModel.getEtsProjId();

		if (!issueSource.equals(ETSPMOSOURCE)) {

			try {

				//6.1.1 migrating to documents repository
				//ETSIssuesManager.deleteAttach("ETS", edge_problem_id, fileNum);

				getFileAttachUtils().deleteIssueFile(strProjectId, edge_problem_id, fileNum);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
				success = false;
			}

		} else {

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

		if (cancelstate == 2) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //make cancel state		
			usrSessnModel.setNextActionState(0);

		}

		if (cancelstate > 2) {

			usrSessnModel.setErrMsg("");
			usrSessnModel.setCurrentActionState(currentstate);
			usrSessnModel.setCancelActionState(cancelstate); //retain the old state
			usrSessnModel.setNextActionState(getNextActionState(cancelstate));

		}

		return usrSessnModel;

	}

	
	

	
	
	/**
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssProbInfoUsr1Model getEditNotifyListDeatils() throws SQLException, Exception {

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		//		get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadFastScr2ParamsIntoUsr1Model();

		//get the check issues type ibm from form
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usrParamsInfo1.getChkIssTypeIbmOnly());

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usrSessnModel.getUserType());
		
		usrSessnModel.setChkIssTypeIbmOnly(chkIssTypeIbmOnly);
	
		
		setUsr1InfoIntoSessn(usrSessnModel, "");
		
		

		//set the norify list params
		usrSessnModel.setNotifyList(getNotifyListDetails(chkIssTypeIbmOnly, userType));
				

		//set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);
		usrSessnModel.setNextActionState(ADDFILEATTACH);

		return usrSessnModel;

	}

	/**
	 * submit to db
	 */

	public int submitUsr1InfoToDb() {

		boolean successsubmit = false;
		int ret = SUBMIT_NEW_FAILURE;

		try {

			//get the final issue details

			EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

			///key info

			String applnId = AmtCommonUtils.getTrimStr(usrSessnModel.getApplnId());
			String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());
			String cqTrkId = AmtCommonUtils.getTrimStr(usrSessnModel.getCqTrkId());
			String probClass = AmtCommonUtils.getTrimStr(usrSessnModel.getProbClass());
			String probState = AmtCommonUtils.getTrimStr(usrSessnModel.getProbState());
			String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId());

			// step 1: submitter profile//

			String custName = AmtCommonUtils.getTrimStr(usrSessnModel.getCustName());
			String custEmail = AmtCommonUtils.getTrimStr(usrSessnModel.getCustEmail());
			String custPhone = AmtCommonUtils.getTrimStr(usrSessnModel.getCustPhone());
			String custCompany = AmtCommonUtils.getTrimStr(usrSessnModel.getCustCompany());
			String problemCreator = AmtCommonUtils.getTrimStr(usrSessnModel.getProbCreator());
			String custProjectName = AmtCommonUtils.getTrimStr(usrSessnModel.getCustProject());

			//prob descr		

			String prevProbSeverity = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevProbSevList().get(0));
			String probTitle = AmtCommonUtils.getTrimStr(usrSessnModel.getProbTitle());
			String probType = AmtCommonUtils.getTrimStr((String) usrSessnModel.getPrevProbTypeList().get(0));
			String probDesc = AmtCommonUtils.getTrimStr(usrSessnModel.getProbDesc());

			//step 2: issue ident
			//parse the prob type to get issue type/issue source
			EtsIssActionGuiUtils actGuiUtil = new EtsIssActionGuiUtils();
			EtsDropDownDataBean dropBean = actGuiUtil.getIssueTypeDropDownAttrib(probType);

			//get the parsed vals
			String issueType = dropBean.getIssueType();
			String issueSource = dropBean.getIssueSource();

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
			String fieldC12Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC12()); //tc
			String fieldC14Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC14()); //first name
			String fieldC15Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC15()); //last name	

			String etsProjectId = AmtCommonUtils.getTrimStr(usrSessnModel.getEtsProjId());

			String issueAccess = "";

			String chkIssIbmOnly = AmtCommonUtils.getTrimStr(usrSessnModel.getChkIssTypeIbmOnly());

			//step 4:
			//split issue access//
			if (chkIssIbmOnly.equals("Y")) {

				issueAccess = "IBM";

			} else {

				issueAccess = "ALL";

			}

			//step 5:
			ArrayList prevNotifyList = usrSessnModel.getPrevNotifyList();
			EtsProjMemberDAO projDao = new EtsProjMemberDAO();
			StringBuffer sbnotify = new StringBuffer();

			if (EtsIssFilterUtils.isArrayListDefnd(prevNotifyList)) {

				//611 filter externals in case of issueAccess == IBM

				ArrayList emailList = projDao.getEmailListFromEdgeIdWithAccess(prevNotifyList, issueAccess);

				//521 req, if ext bhf, add submitter also in notify list
				if (isSubBhfExtUserDefnd()) {

					emailList.add(getEtsIssObjKey().getEs().gEMAIL);
				}

				int mailsize = emailList.size();

				for (int i = 0; i < mailsize; i++) {

					sbnotify.append(emailList.get(i));

					if (i != mailsize - 1) {

						sbnotify.append(",");

					}

				}
			} else {

				ArrayList emailList = new ArrayList();

				//521 req, if ext bhf, add submitter also in notify list
				if (isSubBhfExtUserDefnd()) {

					emailList.add(getEtsIssObjKey().getEs().gEMAIL);
				}

				int mailsize = emailList.size();

				for (int i = 0; i < mailsize; i++) {

					sbnotify.append(emailList.get(i));

					if (i != mailsize - 1) {

						sbnotify.append(",");

					}

				}

			}

			Global.println("mail string ===" + sbnotify.toString());

			//get data_id based on issue type name
			EtsDropDownDAO dropDao = new EtsDropDownDAO();
			String issueTypeId = dropDao.getDataIdFromIssueType(projectId, probClass, issueType);

			//NAME AND VAL

			ETSMWIssue issue = new ETSMWIssue();

			//key details
			issue.application_id = applnId;
			issue.cq_trk_id = cqTrkId;
			issue.edge_problem_id = edgeProblemId;

			//submitter profile
			issue.problem_creator = problemCreator;
			issue.cust_company = custCompany;
			issue.cust_email = custEmail;
			issue.cust_name = custName;
			issue.cust_phone = custPhone;
			issue.cust_project = custProjectName;
			issue.ets_project_id = etsProjectId;

			//issue desc

			issue.problem_class = probClass;
			issue.severity = prevProbSeverity;
			issue.title = ETSUtils.escapeString(probTitle);
			issue.problem_desc = ETSUtils.escapeString(probDesc);
			issue.problem_state = probState;

			//issue ident
			//trim issue type to 50 chars, just before to DB as usr1 limitation is 50 chars
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

			issue.field_C12 = fieldC12Val;
			issue.field_C14 = fieldC14Val;
			issue.field_C15 = fieldC15Val;
			///////////issue access
			issue.issue_access = issueAccess;
			issue.issue_source = issueSource;

			//commenst
			issue.comm_from_cust = "";

			//notification
			issue.ets_cclist = ETSUtils.escapeString(sbnotify.toString());
			//last user id

			issue.last_userid = getEtsIssObjKey().getEs().gUSERN;

			//for ets issues type

			if (getEtsIssObjKey().isProjBladeType()) {

				issue.etsIssuesType = "SUPPORT";

			} else {

				issue.etsIssuesType = "";
			}

			///all NON-PMO ISSUES WOULD FLOW TO CQ.USR1 TABLES, ELSE GO TO ETS.PMO_ISSUE_INFO TABS

			if (!issueSource.equals(ETSPMOSOURCE)) {

				try {

					//successsubmit = ETSIssuesManager.addNewIssue(issue);
					//521 intergration
					if (!ETSIssuesManager.isProblemIdExistsInUsr1(issue.edge_problem_id)) {

						IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
						IssMWProcessor mwproc = createMWproc.factoryMethod(getEtsIssObjKey());
						mwproc.setIssue(issue);
						successsubmit = mwproc.processRequest();

						ret = getRetCode(successsubmit);

					} else {

						ret = SUBMIT_NEW_DUPLICATE_ERR;
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				//submit files only when the user clicks optional submit button

				String op_28 = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("op_28.x"));

				if (AmtCommonUtils.isResourceDefined(op_28)) {

					try {

						//6.1.1 migrtn to docs reps
						//ETSIssuesManager.updateFileFlagInUsr2(edgeProblemId, getEtsIssObjKey().getEs().gUSERN, "E", "Y");

						//any files with T status make them to E
						getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(), usrSessnModel.getEdgeProblemId(), "T", "E");

						//all files from E to Y
						getFileAttachUtils().updateIssueFileStatus(getEtsIssObjKey().getProj().getProjectId(), usrSessnModel.getEdgeProblemId(), "E", "Y");

						//} catch (SQLException e) {
						//	e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
//				Commented by v2sagar for PROBLEM_INFO_USR2
				/*try {

					ETSIssuesManager.updateCqTrackId(edgeProblemId, "-", 1, getEtsIssObjKey().getEs().gUSERN);

				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}*/

			} else { //ELSE GO TO ETS.PMO_ISSUE_INFO TABS

				EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();

				if (!crPmoDao.isEtsIdExistsInIssueInfo(issue.edge_problem_id)) {

					successsubmit = submitIssueInfoToPMODb(issue);

					ret = getRetCode(successsubmit);

				} else {

					ret = SUBMIT_NEW_DUPLICATE_ERR;

				}

			}

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return ret;

	}

	/**
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getSubmitIssueDetails() {

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		int ret = submitUsr1InfoToDb();

		if (ret == SUBMIT_NEW_FAILURE) {

			usrSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("An error has occured while submitting the new issue. Please try after sometime or contact");
			sberr.append(" Customer Connect Help Desk for more help.");

			//set error msg
			getEtsIssObjKey().getRequest().setAttribute("actionerrmsg", sberr.toString());

		}

		if (ret == SUBMIT_NEW_DUPLICATE_ERR) {

			usrSessnModel.setNextActionState(ERRINACTION);

			StringBuffer sberr = new StringBuffer();
			sberr.append("The system finds a duplicate record with the given details. Please click 'Submit a new issue'  under 'Issues/changes' link to submit a new issue.");
			sberr.append("Please click 'Work with all issues' under 'Issues/changes' to view the issues already submitted.");

			//set error msg
			getEtsIssObjKey().getRequest().setAttribute("actionerrmsg", sberr.toString());

		}

		if (ret == SUBMIT_NEW_SUCCESS) {

			usrSessnModel.setNextActionState(0);

		}

		//		set all states
		usrSessnModel.setErrMsg("");
		usrSessnModel.setCurrentActionState(currentstate);
		usrSessnModel.setCancelActionState(cancelstate);

		return usrSessnModel;

	}

	
	/**
					 * 
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public ArrayList getNotifyListDetails(String chkIssTypeIbmOnly, String userType) throws SQLException, Exception {

		//get proj member DAO and get details
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		ArrayList projMemList = projDao.getProjMemberListWithUserType(getEtsIssObjKey().getProj().getProjectId());
		ArrayList userTypeList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			if (!chkIssTypeIbmOnly.equals("Y")) {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);

					if (userType.equals("I") && etsUserType.equals("I")) {

						etsUserNameWithIrId = etsUserNameWithIrId + " *";

					} //if user type is IBM

					userTypeList.add(etsUserEdgeId);
					userTypeList.add(etsUserNameWithIrId);
					userTypeList.add(etsUserType);

				} //end of for

			} // end of for iss type=IBM ONLY !=Y

			else {

				for (int i = 0; i < projsize; i = i + 4) {

					etsUserEdgeId = (String) projMemList.get(i);
					etsUserNameWithIrId = (String) projMemList.get(i + 1);
					etsUserType = (String) projMemList.get(i + 2);

					if (userType.equals("I") && etsUserType.equals("I")) {

						userTypeList.add(etsUserEdgeId);
						userTypeList.add(etsUserNameWithIrId);
						userTypeList.add(etsUserType);

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
	  * submit to ISSUE INFO TO PMO
	  */

	public boolean submitIssueInfoToPMODb(ETSIssue issue) {

		boolean successsubmit = false;
		boolean successtxn = false;
		boolean successfiles = false;

		try {

			////

			//				get pmo project id, for a given project id
			String pmoProjectId = getEtsIssObjKey().getProj().getPmo_project_id();
			String projectId = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getProj().getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(getEtsIssObjKey().getIssueClass());

			//get pmo id based on pmo proj id
			EtsCrPmoDAO crPmoDao = new EtsCrPmoDAO();
			ETSPMOffice pmoOffice = crPmoDao.getPMOfficeObjectDetailForCr(pmoProjectId);

			//get Parent PMO ID, where type=CRIFolder
			String parentPmoId = pmoOffice.getPMOID();

			///

			//comments containing files info not rqeuired by issues
			//String commFromCust = AmtCommonUtils.getTrimStr(getCommentsStrForAttachedFiles(issue.edge_problem_id));
			String commFromCust = "";
			//NAME AND VAL

			EtsCrDbModel crDbModel = new EtsCrDbModel();

			//key details
			crDbModel.etsId = issue.edge_problem_id;
			crDbModel.pmoId = "";
			crDbModel.pmoProjectId = pmoProjectId;
			crDbModel.parentPmoId = parentPmoId;
			crDbModel.refNo = 0;
			crDbModel.infoSrcFlag = "E";

			//submitter profile
			crDbModel.custName = issue.cust_name;
			crDbModel.custCompany = issue.cust_company;
			crDbModel.custEmail = issue.cust_email;
			crDbModel.custPhone = issue.cust_phone;
			crDbModel.stateAction = "Submit";
			crDbModel.probCreator = issue.problem_creator;

			//issue desc
			//crDbModel.probClass = issue.problem_class;
			//class is changed to ISSUE and type contains problem type
			crDbModel.probClass = ETSPMOISSUESUBTYPE;
			crDbModel.probTitle = ETSUtils.escapeString(issue.title);

			String issueSeverity = issue.severity;

			//	remove like 1-,2- from severity 	
			int inx = issueSeverity.indexOf("-");

			//			if (inx != -1) {
			//
			//				issueSeverity = issueSeverity.substring(inx + 1);
			//
			//			}

			//issue ident
			//trim issue type to 50 chars, just before to DB as usr1 limitation is 50 chars
			String issueType = issue.problem_type;

			//get data_id based on issue type name
			EtsDropDownDAO dropDao = new EtsDropDownDAO();
			String issueTypeId = dropDao.getDataIdFromIssueType(projectId, issueClass, issueType);

			crDbModel.probSeverity = issueSeverity;

			if (issueType.length() > 50) {

				issueType = issueType.substring(0, 50);
			}

			crDbModel.CRType = issueType;
			crDbModel.probDesc = ETSUtils.escapeString(issue.problem_desc);
			crDbModel.issueTypeId = issueTypeId;

			//commenst
			crDbModel.commFromCust = ETSUtils.escapeString(commFromCust);

			//last user id

			crDbModel.lastUserId = issue.last_userid;

			crDbModel.issueAccess = issue.issue_access;
			crDbModel.issueSource = issue.issue_source;
			crDbModel.etsCCList = issue.ets_cclist;

			//txn flag
			crDbModel.statusFlag = "C";

			EtsCrPmoDAO pmoDao = new EtsCrPmoDAO();

			EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

			try {

				successsubmit = pmoDao.addNewIssue(crDbModel);

				successtxn = pmoDao.addNewCRTxn(crDbModel);

				Global.println("success submitt===" + successsubmit);

				Global.println("success txn===" + successtxn);

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

				sb.append("--------------------------- File attachment info  ------------------------------\n");
			}

			sb.append("Document&nbsp;:&nbsp;  " + docName + " has been attached by " + userName + " [IBM ID: " + lastUserIrId + "] on " + lastTimeStampStr + " ");
			sb.append("\n");

		}

		return sb.toString();
	}

	/**
	 * 
	 * @author V2PHANI
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */

	/**
						 * 
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public ArrayList getUsersFromProjTeam(String userType) throws SQLException, Exception {

		//get proj member DAO and get details
		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		ArrayList projMemList = projDao.getClientRoleExtListForProject(getEtsIssObjKey().getProj().getProjectId(), getEtsIssObjKey().isProjBladeType());
		ArrayList userTypeList = new ArrayList();

		int projsize = 0;
		String etsUserNameWithIrId = "";
		String etsUserName = "";
		String etsUserEdgeId = "";
		String etsUserType = "";

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			projsize = projMemList.size();

			for (int i = 0; i < projsize; i = i + 4) {

				etsUserEdgeId = (String) projMemList.get(i);
				etsUserNameWithIrId = (String) projMemList.get(i + 1);
				etsUserType = (String) projMemList.get(i + 2);

				userTypeList.add(etsUserEdgeId);
				userTypeList.add(etsUserNameWithIrId);

			} //end of for

		} //if projMemelist is defined

		return userTypeList;

	}

	/**
			 * This method will model the data required for FE while submittng the problem and send in the usr1 model
			 */

	public EtsIssProbInfoUsr1Model getSubmitOnBehalfExtDets() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usrInfo1 = new EtsIssProbInfoUsr1Model();

		usrInfo1.setExtUserList(getUsersFromProjTeam("E"));

		return usrInfo1;

	}

	/**
				 * This method will model the data required for FE while submittng the problem and send in the usr1 model
				 */

	public EtsIssProbInfoUsr1Model getContBehalfExtDets() throws SQLException, Exception {

		//get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadScr00ParamsIntoUsr1Model();

		//check for any error msgs from the form model for scrn1
		String errMsg = validateScrn00FormFields(usrParamsInfo1);

		//print error msg
		Global.println("err msg getContDescrDetails()=====" + errMsg);

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			//	add the selected user from list
			//when there are no err msgs

			EtsIssProbInfoUsr1Model usrInfo1 = new EtsIssProbInfoUsr1Model();

			usrInfo1.setExtUserList(getUsersFromProjTeam("E"));

			usrInfo1.setPrevExtUserList(usrParamsInfo1.getPrevExtUserList()); //add prev user

			usrInfo1.setErrMsg(errMsg);
			usrInfo1.setNextActionState(SUBMIT_ONBEHALF_EXT);

			return usrInfo1;

		} else {

			String extUser = "";

			ArrayList prevExtUserList = usrParamsInfo1.getPrevExtUserList();

			if (prevExtUserList != null && !prevExtUserList.isEmpty()) {

				extUser = (String) prevExtUserList.get(0);

			}

			HttpSession session = getEtsIssObjKey().getRequest().getSession(true);
			session.setAttribute("SUBBHFEXTUSER", extUser);

			EtsIssProbInfoUsr1Model newUsrInfo1 = getNewInitialDetails();
			newUsrInfo1.setPrevExtUserList(usrParamsInfo1.getPrevExtUserList()); //add prev user
			newUsrInfo1.setErrMsg("");
			newUsrInfo1.setNextActionState(NEWINITIAL);
			return newUsrInfo1;

		}

	}

	/**
				 * This method will load step1 details and check for validations
				 * 
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */
	public String validateScrn00FormFields(EtsIssProbInfoUsr1Model usrParamsInfo1) throws SQLException, Exception {

		StringBuffer errsb = new StringBuffer();

		//			check for issue type

		if (EtsIssFilterUtils.isArrayListDefnd(usrParamsInfo1.getPrevExtUserList())) {

			if (usrParamsInfo1.getPrevExtUserList().contains("NONE")) {

				errsb.append("Please select user from list.");
				errsb.append("<br />");

			}
		}

		//get from session
		return errsb.toString();

	}

	/**
	 * returns from session, if any user is selected , on behalf of which the issues/pcr need to be submitted
	 * @return
	 */

	public String getSubBhfExtUser() {

		HttpSession session = getEtsIssObjKey().getRequest().getSession(true);

		String extUser = AmtCommonUtils.getTrimStr((String) session.getAttribute("SUBBHFEXTUSER"));

		return extUser;
	}

	/**
		 * returns from session, if any user is selected , on behalf of which the issues/pcr need to be submitted
		 * @return
		 */

	public boolean isSubBhfExtUserDefnd() {

		String extBhf = AmtCommonUtils.getTrimStr((String) getEtsIssObjKey().getParams().get("extbhf"));

		if (AmtCommonUtils.isResourceDefined(extBhf) && extBhf.equals("1")) {

			return true;
		}

		return false;
	}

	/**
	 * 
	 * @return
	 */

	public EtsIssProjectMember getSubmitterProfile() {

		EtsIssProjectMember projMem = new EtsIssProjectMember();

		try {

			if (isSubBhfExtUserDefnd()) {

				String edgeUserId = getSubBhfExtUser();

				projMem = getSubmitterProfileFromUserId(edgeUserId);

			} else {

				projMem = getSubmitterProfileFromEs(getEtsIssObjKey().getEs());
			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getSubmitter profile in sub new iss", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in getSubmitter profile in sub new iss", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}
		return projMem;

	}

	public int getRetCode(boolean successsubmit) {

		int ret = SUBMIT_NEW_FAILURE;

		if (successsubmit) {

			ret = SUBMIT_NEW_SUCCESS;
		} else {

			ret = SUBMIT_NEW_FAILURE;

		}

		return ret;

	}

	/**
			 * This method will retrieve the Model from session
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getCancelSubmitDetails() throws SQLException, Exception {

		//get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//get cancelstate
		int cancelstate = getCancelActionState();
		int subtypestate = usrSessnModel.getSubtypeActionState();

		//add next cancel state 
		usrSessnModel.setNextActionState(MAINPAGE);

		return usrSessnModel;

	}

	/**
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public EtsIssProbInfoUsr1Model getNotifyDetsSubmit() throws SQLException, Exception {

		//		get from the session the latest model
		EtsIssProbInfoUsr1Model usrSessnModel = getUsr1InfoFromSessn();

		//		get cancelstate
		int cancelstate = getCancelActionState();

		//		get the params model frm the form
		EtsIssProbInfoUsr1Model usrParamsInfo1 = parseParams.loadFastScr3ParamsIntoUsr1Model();

		//get the check issues type ibm from form
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usrParamsInfo1.getChkIssTypeIbmOnly());

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usrSessnModel.getUserType());

		usrSessnModel.setChkIssTypeIbmOnly(chkIssTypeIbmOnly);
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC1List() ) )	
			usrSessnModel.setPrevFieldC1List(usrParamsInfo1.getPrevFieldC1List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC2List() ) )
			usrSessnModel.setPrevFieldC2List(usrParamsInfo1.getPrevFieldC2List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC3List() ) )
			usrSessnModel.setPrevFieldC3List(usrParamsInfo1.getPrevFieldC3List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC4List() ) )
			usrSessnModel.setPrevFieldC4List(usrParamsInfo1.getPrevFieldC4List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC5List() ) )
			usrSessnModel.setPrevFieldC5List(usrParamsInfo1.getPrevFieldC5List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC6List() ) )
			usrSessnModel.setPrevFieldC6List(usrParamsInfo1.getPrevFieldC6List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC7List() ) )
			usrSessnModel.setPrevFieldC7List(usrParamsInfo1.getPrevFieldC7List());
		
		if (EtsIssFilterUtils.isArrayListDefnd( usrParamsInfo1.getPrevFieldC8List() ) )
			usrSessnModel.setPrevFieldC8List(usrParamsInfo1.getPrevFieldC8List());
		

		//set the norify list params
		usrSessnModel.setPrevNotifyList(usrParamsInfo1.getPrevNotifyList());

		setUsr1InfoIntoSessn(usrSessnModel, "");

		return getSubmitIssueDetails();

	}

} //end of class
