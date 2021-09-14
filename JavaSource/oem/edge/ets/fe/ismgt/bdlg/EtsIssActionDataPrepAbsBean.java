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
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.dao.EtsIssTypSubscribeDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.dao.FilterDAOAbs;
import oem.edge.ets.fe.ismgt.dao.FilterDAOFactory;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFileAttachUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssTypeSessionParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssViewGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssueActionSessnParams;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssSubscribeIssTypModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class EtsIssActionDataPrepAbsBean implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.19.1.30";

	private EtsIssObjectKey etsIssObjKey;
	private EtsIssueActionSessnParams actSessnParams;
	private EtsIssTypeSessionParams issTypeSessnParams;
	private EtsIssFileAttachUtils fileAttchUtils;

	/**
	 * Constructor for FilterDetailsDataPrepAbsBean.
	 */
	public EtsIssActionDataPrepAbsBean(EtsIssObjectKey etsIssObjKey) {
		super();
		this.etsIssObjKey = etsIssObjKey;
		this.actSessnParams = new EtsIssueActionSessnParams(etsIssObjKey);
		this.issTypeSessnParams = new EtsIssTypeSessionParams(etsIssObjKey);
		this.fileAttchUtils = new EtsIssFileAttachUtils();

	}

	/**
		 * This method will prepare the submitter profile model
		 */

	//	public EtsIssProbInfoUsr1Model getSubmitterProfile() throws Exception {
	//
	//		EtsIssProbInfoUsr1Model usr1Model = new EtsIssProbInfoUsr1Model();
	//
	////		usr1Model.setCustName(etsIssObjKey.getEs().gFIRST_NAME + " " + etsIssObjKey.getEs().gLAST_NAME);
	////		usr1Model.setCustEmail(etsIssObjKey.getEs().gEMAIL);
	////		usr1Model.setCustPhone(etsIssObjKey.getEs().gPHONE);
	////		usr1Model.setCustCompany(etsIssObjKey.getEs().gASSOC_COMP);
	////		usr1Model.setProbCreator(etsIssObjKey.getEs().gUSERN);
	////		usr1Model.setCreationDate(EtsIssFilterUtils.getCurDtSqlTimeStamp());
	////		usr1Model.setCustProject(etsIssObjKey.getProj().getName());
	////		usr1Model.setEtsProjId(etsIssObjKey.getProj().getProjectId());
	////		usr1Model.setFieldC14(etsIssObjKey.getEs().gFIRST_NAME);
	////		usr1Model.setFieldC15(etsIssObjKey.getEs().gLAST_NAME);
	////		usr1Model.setFieldC12(String.valueOf(etsIssObjKey.getTopCatId()));
	//
	//		return usr1Model;
	//	}

	/**
	 * @return
	 */
	public EtsIssObjectKey getEtsIssObjKey() {
		return etsIssObjKey;
	}

	/**
	 * @return
	 */
	public EtsIssueActionSessnParams getActSessnParams() {
		return actSessnParams;
	}

	/**
		 * @return
		 */
	public EtsIssTypeSessionParams getIssueTypeSessnParams() {
		return issTypeSessnParams;
	}

	/***
		 * to get suitable DAO for the business objects
		 * 
		 */

	public FilterDAOAbs getFilterDAO() throws Exception {

		FilterDAOFactory daoFac = new FilterDAOFactory();

		FilterDAOAbs daoObj = daoFac.createIssueActionDAO(etsIssObjKey);

		return daoObj;

	}

	/**
		 * To return the issue types for a given project and defect/change 
		 *
		 * @return ArrayList
		 */

	public ArrayList getIssueTypeList(EtsIssProbInfoUsr1Model usr1SessnModel) throws SQLException, Exception {

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		//get the drop list
		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		//
		String qryIssueSource = AmtCommonUtils.getTrimStr(usr1SessnModel.getIssueSource());

		Global.println("qry issue source in ABS bean==" + qryIssueSource);

		///get params
		dropModel.setProjectId(getEtsIssObjKey().getProj().getProjectId());
		dropModel.setIssueClass(ETSISSUESUBTYPE);
		dropModel.setActiveFlag("Y");
		dropModel.setIssueSource(qryIssueSource);

		//521 xhange for submit on bhf
		//String userType = getEtsIssObjKey().getEs().gDECAFTYPE;
		String userType=usr1SessnModel.getUserType();

		if (userType.equals("I")) {

			dropModel.setIssueAccess("IBM");
		} else {

			dropModel.setIssueAccess("ALL");

		}

		ArrayList dropList = dropDao.getIssueTypes(dropModel);
		ArrayList issueTyepList = new ArrayList();

		int size = 0;

		if (dropList != null && !dropList.isEmpty()) {

			size = dropList.size();

		}

		String dataId = "";
		String issueType = "";
		String issueSource = ""; //CQ or Other
		String issueAccess = ""; //ISSUE access
		String dispVal = "";

		for (int i = 0; i < size; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropList.get(i);

			issueType = dropBean.getIssueType();
			issueSource = dropBean.getIssueSource();
			issueAccess = dropBean.getIssueAccess();

			if (!AmtCommonUtils.isResourceDefined(issueType)) {

				issueType = ETSNOVAL;

			}

			if (!AmtCommonUtils.isResourceDefined(issueSource)) {

				issueSource = ETSNOVAL;
			}

			if (!AmtCommonUtils.isResourceDefined(issueAccess)) {

				issueAccess = ETSNOVAL;
			}

			dispVal = issueType + "$" + issueSource + "$" + issueAccess;

			issueTyepList.add(dispVal);
			issueTyepList.add(issueType);

		}

		return issueTyepList;
	}

	/**
	 * 
	 * @return blank Array List
	 */

	public ArrayList getBlankList() {

		return new ArrayList();
	}

	/**
		 * to get issue val for a given pattern
		 * token format >> 	DATA_ID $ ISSUE_TYPE $ ISSUE_SOURCE $ ISSUE_ACCESS $ SUBTYPE_A
		 */

	public String getDelimitIssueVal(String prob_type) throws Exception {

		return EtsIssFilterUtils.getDelimitIssueVal(prob_type);

	}

	/**
			 * 
			 * @return Default CQ Array List
			 */

	public ArrayList getDefualtCqList() {

		//def cq list
		ArrayList defltCqList = new ArrayList();
		defltCqList.add(ETSDEFAULTCQ);

		return defltCqList;
	}

	public boolean isIssueSrcPMO(String edgeProblemId) throws SQLException, Exception {

		EtsIssViewGuiUtils guiUtils = new EtsIssViewGuiUtils();

		return guiUtils.isIssueSrcPMO(edgeProblemId);

	}

	/**
	 * 
	 * @param stateAction
	 * @param txnFlag
	 * @return
	 */

	//	public String getUpdatedStateAction(String stateAction, String txnFlag) {
	//
	//		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();
	//
	//		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {
	//
	//			stateAction = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.staction");
	//
	//		} else if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {
	//
	//			stateAction = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.staction");
	//
	//		}
	//
	//		Global.println("stateAction in updated getUpdatedStateAction===" + stateAction);
	//
	//		return stateAction;
	//	}

	/**
		 * 
		 * @param stateAction
		 * @param txnFlag
		 * @return
		 */

	public String getUpdatedStateAction(String cqTrkId, String stateAction, String problemState, String txnFlag) {

		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getUpdatedStateAction(pcrPropMap, cqTrkId, stateAction, problemState, txnFlag);

	}

	/**
			 * 
			 * @param stateAction
			 * @param txnFlag
			 * @return
			 */

	public String getUpdatedStateActionForCR(String cqTrkId, String stateAction, String problemState, String txnFlag) {

		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getUpdatedStateActionForCR(pcrPropMap, cqTrkId, stateAction, problemState, txnFlag);

	}

	/**
		 * 
		 * @param rtfList
		 * @return
		 */

	public HashMap getRtfMap(ArrayList rtfList) {

		HashMap rtfMap = new HashMap();
		int rtfsize = 0;

		String rtfAliasName = "";
		String rtfBlobStr = "";
		String strRtfId = "";
		int intRtfId = 0;

		if (EtsIssFilterUtils.isArrayListDefndWithObj(rtfList)) {

			rtfsize = rtfList.size();
		}

		for (int i = 0; i < rtfsize; i++) {

			Global.println("RTF MAP PUT");

			EtsCrRtfModel crRtfModel = (EtsCrRtfModel) rtfList.get(i);

			rtfAliasName = crRtfModel.getRtfAliasName();
			rtfBlobStr = crRtfModel.getRtfBlobStr();
			intRtfId = crRtfModel.getRtfId();
			strRtfId = "" + intRtfId;
			strRtfId = AmtCommonUtils.getTrimStr(strRtfId);

			Global.println("rtf Aliad Name==" + strRtfId);

			rtfMap.put(strRtfId, crRtfModel);

		}

		return rtfMap;

	}

	/**
	 * 
	 * @param txnFlag
	 * @return
	 */

	public String getErrMsg(String txnFlag) {

		HashMap pcrPropMap = getEtsIssObjKey().getPcrPropMap();

		String errMsg = "";

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {

			errMsg = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.msg");
		}

		if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

			errMsg = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.msg");
		}

		Global.println("ERror Msg in updated Error Msg===" + errMsg);

		return errMsg;
	}

	/**
	 * get state/action mapping
	 * @param probState
	 * @return
	 */

	public String getStateActionMapping(String probState) {

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getStateActionMapping(probState);

	}

	/**
	 * get the static action/map 
	 * @return
	 */

	public HashMap getStateActionMap() {

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getStateActionMap();

	}

	/**
	 * return issue action list
	 * @return
	 */

	public ArrayList getPMOIssueActionList() {

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getPMOIssueActionList();

	}

	/**
	 * return prob state list
	 * @return
	 */

	public ArrayList getPMOIssueProbStateList() {

		EtsIssActionGuiUtils issActGuiUtils = new EtsIssActionGuiUtils();

		return issActGuiUtils.getPMOIssueProbStateList();

	}

	/**
	 * To get submiitter profile from es
	 */

	public EtsIssProjectMember getSubmitterProfileFromEs(EdgeAccessCntrl es) {

		EtsIssProjectMember projMem = new EtsIssProjectMember();
		//	/submitter details
		projMem.setUserFullName(getEtsIssObjKey().getEs().gFIRST_NAME + " " + getEtsIssObjKey().getEs().gLAST_NAME);
		projMem.setUserEmail(getEtsIssObjKey().getEs().gEMAIL);
		projMem.setUserContPhone(getEtsIssObjKey().getEs().gPHONE);
		if (getEtsIssObjKey().getEs().gDECAFTYPE.equals("I")) {

			projMem.setUserCustCompany("IBM");

		} else {

			projMem.setUserCustCompany(getEtsIssObjKey().getEs().gASSOC_COMP);

		}
		projMem.setUserEdgeId(getEtsIssObjKey().getEs().gUSERN);

		projMem.setUserFirstName(getEtsIssObjKey().getEs().gFIRST_NAME);
		projMem.setUserLastName(getEtsIssObjKey().getEs().gLAST_NAME);
		projMem.setUserType(getEtsIssObjKey().getEs().gDECAFTYPE);

		return projMem;
	}

	/**
	 * To get sub prof from edge user id
	 * @param edgeUserId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsIssProjectMember getSubmitterProfileFromUserId(String edgeUserId) throws SQLException, Exception {

		EtsIssProjectMember projMem = new EtsIssProjectMember();

		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		UserObject userObj = projMemDao.getUserObject(edgeUserId);

		String assocComp = projMemDao.getAssocCompany(edgeUserId);

		String decafUserType = projMemDao.getDecafUserType(edgeUserId);

		projMem.setUserFullName(userObj.gFIRST_NAME + " " + userObj.gLAST_NAME);
		projMem.setUserEmail(userObj.gEMAIL);
		projMem.setUserContPhone(userObj.gPHONE);
		projMem.setUserType(decafUserType);
		projMem.setUserFirstName(userObj.gFIRST_NAME);
		projMem.setUserLastName(userObj.gLAST_NAME);

		if (decafUserType.equals("I")) {

			projMem.setUserCustCompany("IBM");

		} else {

			projMem.setUserCustCompany(assocComp);

		}
		projMem.setUserEdgeId(userObj.gUSERN);
		projMem.setUserType(decafUserType);
		return projMem;
	}

	/**
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssProbInfoUsr1Model getIssueViewDetailsAbs(String edgeProblemId) throws SQLException, Exception {
		
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();
		
		try {
		
	
		Global.println("edge problem id in getIssueViewDetails===" + edgeProblemId);

		//get the issue details from DB
		
		Hashtable  htCustFieldLabel = ETSIssuesManager.getCustFieldLabels(getEtsIssObjKey().getProj().getProjectId());
		
		ETSIssue currentIssue = ETSIssuesManager.getIssue(edgeProblemId);
		String probCreator = AmtCommonUtils.getTrimStr(currentIssue.problem_creator);
		String custName = AmtCommonUtils.getTrimStr(currentIssue.cust_name);
		String custEmail = AmtCommonUtils.getTrimStr(currentIssue.cust_email);
		String custPhone = AmtCommonUtils.getTrimStr(currentIssue.cust_phone);
		String custCompany = AmtCommonUtils.getTrimStr(currentIssue.cust_company);
		String custProject = AmtCommonUtils.getTrimStr(currentIssue.cust_project);
		String submiDateStr = AmtCommonUtils.getTrimStr(currentIssue.submitDateStr);
		String dateString = EtsIssFilterUtils.formatDate(submiDateStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy");

		String cq_trk_id = AmtCommonUtils.getTrimStr(currentIssue.cq_trk_id);
		String prob_class = AmtCommonUtils.getTrimStr(currentIssue.problem_class);

		////prob severity
		String prob_severity = AmtCommonUtils.getTrimStr(currentIssue.severity);
		ArrayList prevSevList = new ArrayList(); //add severity to list
		prevSevList.add(prob_severity);

		//////
		String prob_title = AmtCommonUtils.getTrimStr(currentIssue.title);

		//prob type list
		String issueTypeId = AmtCommonUtils.getTrimStr(currentIssue.issueTypeId);
		//
		EtsDropDownDAO dropDao = new EtsDropDownDAO();
		String prob_type = dropDao.getIssueTypeFromDataId(issueTypeId);

		//String prob_type = AmtCommonUtils.getTrimStr(currentIssue.problem_type);
		ArrayList prevProbTypeList = new ArrayList();
		prevProbTypeList.add(prob_type);
		////

		String prob_desc = AmtCommonUtils.getTrimStr(currentIssue.problem_desc);
		String prob_state = AmtCommonUtils.getTrimStr(currentIssue.problem_state);

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

		//notify list
		String notifylist = AmtCommonUtils.getTrimStr(currentIssue.ets_cclist);
		ArrayList prevNotifyList = new ArrayList();
		prevNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(notifylist, ",");

		//get seq no for usr1 and cq1

		int usr_seq_no = ETSIssuesManager.getUsr1Seq_no(edgeProblemId);
		int cq_seq_no = ETSIssuesManager.getCq1Seq_no(edgeProblemId);

		//get comments logs
		String comm_from_cust = AmtCommonUtils.getTrimStr(currentIssue.comm_from_cust);

		//
		String etsIssuesType = AmtCommonUtils.getTrimStr(currentIssue.etsIssuesType);

		//
		String etsProjectId = AmtCommonUtils.getTrimStr(currentIssue.ets_project_id);

		boolean isOwner = false;

		////if owner defined true///
		ArrayList ownerInfoList = currentIssue.probOwnerList;
		ArrayList ownerIdList = new ArrayList();

		int ownerinfosize = 0;

		if (EtsIssFilterUtils.isArrayListDefndWithObj(ownerInfoList)) {

			ownerinfosize = ownerInfoList.size();

			for (int ii = 0; ii < ownerinfosize; ii++) {

				EtsIssOwnerInfo etsOwnerInfo = (EtsIssOwnerInfo) ownerInfoList.get(ii);
				ownerIdList.add(etsOwnerInfo.getUserEdgeId()); //since edgeid was set

			}

		}

		//get the owner names
		ArrayList ownerNameList = new ArrayList();

		if (EtsIssFilterUtils.isArrayListDefnd(ownerIdList)) {

			EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

			ownerNameList = projMemDao.getProjMemberListNamesFromEdgeId(ownerIdList);

		} //for owner names

		if (ownerIdList.contains(getEtsIssObjKey().getEs().gUSERN)) {

			isOwner = true;

		}

		//get the commentary log
		String comm_log_string = AmtCommonUtils.getTrimStr(ETSIssuesManager.getComm_log(currentIssue.edge_problem_id));

		//get the last user action;
		String userLastAction = AmtCommonUtils.getTrimStr(ETSIssuesManager.getActionUsr1(currentIssue.edge_problem_id));
		
		///
		boolean isIssueTypeActive = dropDao.isIssueTypeIdActive(issueTypeId);
		
		//
		String refNo = cq_trk_id;

		//set all key params to usr1InfoModel
		usr1InfoModel.setApplnId(ETSAPPLNID);
		usr1InfoModel.setEdgeProblemId(edgeProblemId);
		usr1InfoModel.setCqTrkId(cq_trk_id);
		usr1InfoModel.setRefNo(refNo);
		usr1InfoModel.setUsr_seq_no(usr_seq_no);
		usr1InfoModel.setCq_seq_no(cq_seq_no);
		usr1InfoModel.setProbClass(prob_class);
		usr1InfoModel.setIssueSrcPMO(isIssueSrcPMO(edgeProblemId));

		//assign submitter profile
		usr1InfoModel.setProbCreator(probCreator);
		usr1InfoModel.setCustName(custName);
		usr1InfoModel.setCustEmail(custEmail);
		usr1InfoModel.setCustPhone(custPhone);
		usr1InfoModel.setCustCompany(custCompany);
		usr1InfoModel.setSubmissionDate(dateString);
		usr1InfoModel.setCustProject(custProject);

		//set descr params to model
		usr1InfoModel.setPrevProbSevList(prevSevList);
		usr1InfoModel.setProbTitle(prob_title);
		usr1InfoModel.setPrevProbTypeList(prevProbTypeList);
		usr1InfoModel.setProbDesc(prob_desc);
		usr1InfoModel.setProbState(prob_state);

		
		///////////////// Custom Fields Label ////////////
		
		if(htCustFieldLabel.size() > 0) {
			
			if(htCustFieldLabel.get("FIELD_C1") != null ) {
				usr1InfoModel.setFieldC1DispName(htCustFieldLabel.get("FIELD_C1").toString());
			}
			if(htCustFieldLabel.get("FIELD_C2") != null ) {
				usr1InfoModel.setFieldC2DispName(htCustFieldLabel.get("FIELD_C2").toString());
			}
			if(htCustFieldLabel.get("FIELD_C3") != null ) {
				usr1InfoModel.setFieldC3DispName(htCustFieldLabel.get("FIELD_C3").toString());
			}
			if(htCustFieldLabel.get("FIELD_C4") != null ) {
				usr1InfoModel.setFieldC4DispName(htCustFieldLabel.get("FIELD_C4").toString());
			}
			if(htCustFieldLabel.get("FIELD_C5") != null ) {
				usr1InfoModel.setFieldC5DispName(htCustFieldLabel.get("FIELD_C5").toString());
			}
			if(htCustFieldLabel.get("FIELD_C6") != null ) {
				usr1InfoModel.setFieldC6DispName(htCustFieldLabel.get("FIELD_C6").toString());
			}
			if(htCustFieldLabel.get("FIELD_C7") != null ) {
				usr1InfoModel.setFieldC7DispName(htCustFieldLabel.get("FIELD_C7").toString());
			}
			if(htCustFieldLabel.get("FIELD_C8") != null ) {
				usr1InfoModel.setFieldC8DispName(htCustFieldLabel.get("FIELD_C8").toString());
			}
		}
		///////////////// Custom Fields Label ////////////
				
		
		
		
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

		//set notify list
		usr1InfoModel.setPrevNotifyList(prevNotifyList);

		//set user last action
		usr1InfoModel.setLastAction(userLastAction);

		//set comm log
		usr1InfoModel.setCommentLogStr(comm_log_string);

		//set commnts frm customer
		//usr1InfoModel.setCommFromCust(comm_from_cust);

		//set owner id list
		usr1InfoModel.setOwnerList(ownerIdList);

		//set owner names list
		usr1InfoModel.setOwnerNameList(ownerNameList);

		ArrayList histList = getHistoryList(getEtsIssObjKey(), edgeProblemId, issueSource);

		//set history list
		usr1InfoModel.setHistList(histList);

		usr1InfoModel.setEtsIssuesType(etsIssuesType);

		usr1InfoModel.setEtsProjId(etsProjectId);

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
		
		//set issue type id active
		usr1InfoModel.setIssueTypIdActive(isIssueTypeActive);
		
		//set user type//
		usr1InfoModel.setUserType(getEtsIssObjKey().getEs().gDECAFTYPE);
		
		EtsIssActionGuiUtils guiUtils = new EtsIssActionGuiUtils();
		Global.println("CEHCK THE INFO MODEL START");
		guiUtils.debugUsr1ModelDetails(usr1InfoModel);
		
		}
		
		catch(SQLException sqlEx) {
			
			sqlEx.printStackTrace();
			throw sqlEx;
			
		}
		
		catch(Exception ex) {
			
			ex.printStackTrace();
			throw ex;
		}

		return usr1InfoModel;
	}

	/**
				 * 
				 * @param edgeProblemId
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 * get history list
				 */

	public ArrayList getHistoryList(EtsIssObjectKey etsIssObjKey, String edgeProblemId, String issueSource) throws SQLException, Exception {

		EtsIssViewGuiUtils viewGuiUtils = new EtsIssViewGuiUtils();
		ArrayList histList = viewGuiUtils.getHistoryList(getEtsIssObjKey(), edgeProblemId, issueSource);

		return histList;

	}

	/**
	 * 
	 */

	public ETSMWIssue getETSMWIssueFromInfoModel(EtsIssProbInfoUsr1Model usrSessnModel) throws Exception {

		ETSMWIssue issue = new ETSMWIssue();

		try {

			///key info

			String applnId = AmtCommonUtils.getTrimStr(usrSessnModel.getApplnId());
			String edgeProblemId = AmtCommonUtils.getTrimStr(usrSessnModel.getEdgeProblemId());
			String cqTrkId = AmtCommonUtils.getTrimStr(usrSessnModel.getCqTrkId());
			String probClass = AmtCommonUtils.getTrimStr(usrSessnModel.getProbClass());
			String probState = AmtCommonUtils.getTrimStr(usrSessnModel.getProbState());
			int seq_no = usrSessnModel.getSeqNo();

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

			String issueType = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueType());
			String issueSource = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueSource());
			String issueTypeId = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueTypeId());

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

			String testCase = AmtCommonUtils.getTrimStr(usrSessnModel.getTestCase());

			//
			String fieldC12Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC12()); //tc
			String fieldC14Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC14()); //first name
			String fieldC15Val = AmtCommonUtils.getTrimStr(usrSessnModel.getFieldC15()); //last name	

			String etsProjectId = AmtCommonUtils.getTrimStr(usrSessnModel.getEtsProjId());

			String issueAccess = AmtCommonUtils.getTrimStr(usrSessnModel.getIssueAccess());

			//step 5:
			ArrayList prevNotifyList = usrSessnModel.getPrevNotifyList();

			String notifyStr = getNotifyStrForModifyIssue(prevNotifyList);

			String comments = usrSessnModel.getCommFromCust();

			String etsIssuesType = usrSessnModel.getEtsIssuesType();

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
			issue.problem_type = issueType;
			issue.issueTypeId=issueTypeId;

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
			issue.test_case = ETSUtils.escapeString(testCase);

			issue.field_C12 = fieldC12Val;
			issue.field_C14 = fieldC14Val;
			issue.field_C15 = fieldC15Val;
			///////////issue access
			issue.issue_access = issueAccess;
			issue.issue_source = issueSource;

			//commenst
			issue.comm_from_cust = ETSUtils.escapeString(comments);

			//notification
			issue.ets_cclist = ETSUtils.escapeString(notifyStr);
			//last user id

			issue.last_userid = getEtsIssObjKey().getEs().gUSERN;

			issue.etsIssuesType = etsIssuesType;

		} catch (Exception ex) {

			throw ex;
		}

		return issue;

	}

	public String getNotifyStrForModifyIssue(ArrayList prevNotifyList) {

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

		Global.println("mail string in modify===" + sbnotify.toString());

		return sbnotify.toString();

	}
	
	/**
		 * 
		 * @return
		 */
		public EtsIssFileAttachUtils getFileAttachUtils() {
		
			return fileAttchUtils;
		}

} //end of class
