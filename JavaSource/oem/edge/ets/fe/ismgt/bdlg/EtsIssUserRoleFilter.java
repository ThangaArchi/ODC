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

import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETS_Workflow;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserActionsModel;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssUserRoleFilter {

	public static final String VERSION = "1.3.1.29";

	/**
	 * get user/roles matrix
	 * 
	 * @return
	 * @throws java.sql.SQLException
	 * @throws Exception
	 */

	public EtsIssUserRolesModel getUserRoleMatrix(EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserRolesModel usrRolesModel = new EtsIssUserRolesModel();

		String projectId = etsIssObjKey.getProj().getProjectId();

		boolean isProjBladeType = etsIssObjKey.isProjBladeType();

		usrRolesModel = getUserRoleMatrix(etsIssObjKey.getEs(), projectId, etsIssObjKey.isShowIssueOwner(), isProjBladeType);

		return usrRolesModel;

	}

	/**
		 * get user/roles matrix
		 * 
		 * @return
		 * @throws java.sql.SQLException
		 * @throws Exception
		 */

	public EtsIssUserRolesModel getUserRoleMatrix(EtsIssFilterObjectKey etsIssFilterKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserRolesModel usrRolesModel = new EtsIssUserRolesModel();

		String projectId = etsIssFilterKey.getProj().getProjectId();

		boolean isProjBladeType = etsIssFilterKey.isProjBladeType();

		usrRolesModel = getUserRoleMatrix(etsIssFilterKey.getEs(), projectId, etsIssFilterKey.isShowIssueOwner(), isProjBladeType);

		return usrRolesModel;

	}

	/**
		 * get user/roles matrix
		 * 
		 * @return
		 * @throws java.sql.SQLException
		 * @throws Exception
		 */

	public EtsIssUserRolesModel getUserRoleMatrix(EdgeAccessCntrl es, String projectId, boolean showIssueOwner, boolean isProjBladeType) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserRolesModel usrRolesModel = new EtsIssUserRolesModel();

		usrRolesModel = prepareUserRoleMatrix(es, projectId, showIssueOwner, isProjBladeType);

		return usrRolesModel;

	}

	/**
		 * get user/actions matrix
		 * 
		 * @return
		 * @throws java.sql.SQLException
		 * @throws Exception
		 */

	public EtsIssUserActionsModel getUserActionMatrix(EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionsModel = new EtsIssUserActionsModel();

		//get view data prep bean
		EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(etsIssObjKey, 60);

		//get user info model
		EtsIssProbInfoUsr1Model usr1InfoModel = viewDataPrep.getIssueViewDetails();

		//get user actions matrix
		usrActionsModel = getUserActionMatrix(usr1InfoModel, etsIssObjKey);

		return usrActionsModel;

	}

	/**
			 * get user/actions matrix
			 * 
			 * @return
			 * @throws java.sql.SQLException
			 * @throws Exception
			 */

	public EtsIssUserActionsModel getUserActionMatrixForPMO(EtsIssProbInfoUsr1Model usr1InfoModel, EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionsModel = new EtsIssUserActionsModel();

		//get user actions matrix
		usrActionsModel = prepareUserActionMatrixForPMO(usr1InfoModel, etsIssObjKey);

		return usrActionsModel;

	}

	/**
			 * get user/actions matrix
			 * 
			 * @return
			 * @throws java.sql.SQLException
			 * @throws Exception
			 */

	public EtsIssUserActionsModel getUserActionMatrixForPMO(EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionsModel = new EtsIssUserActionsModel();

		//get view data prep bean
		EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(etsIssObjKey, 60);

		//get user info model
		EtsIssProbInfoUsr1Model usr1InfoModel = viewDataPrep.getIssueViewDetails();

		//get user actions matrix
		usrActionsModel = getUserActionMatrixForPMO(usr1InfoModel, etsIssObjKey);

		return usrActionsModel;

	}

	/**
			 * get user/actions matrix
			 * 
			 * @return
			 * @throws java.sql.SQLException
			 * @throws Exception
			 */

	public EtsIssUserActionsModel getUserActionMatrix(EtsIssProbInfoUsr1Model usr1InfoModel, EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionsModel = new EtsIssUserActionsModel();

		//prepare user/actions matrix
		usrActionsModel = prepareUserActionMatrix(usr1InfoModel, etsIssObjKey);

		return usrActionsModel;

	}

	/**
	 * This method will contain all the busniess rules
	 * to users/roles and problem states/actions
	 * and users/actions matrix derived based on above roles/states
	 * 
	 * 
	 * @param usr1InfoModel
	 * @return
	 * @throws java.sql.SQLException
	 * @throws Exception
	 */

	private EtsIssUserActionsModel prepareUserActionMatrix(EtsIssProbInfoUsr1Model usr1InfoModel, EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionModel = new EtsIssUserActionsModel();

		//		//		initialize the valid model with all false initailly
		//		usrActionModel.setUsrSubmitIssue(false);
		//		usrActionModel.setUsrModifyIssue(false);
		//		usrActionModel.setUsrResolveIssue(false);
		//		usrActionModel.setUsrRejectIssue(false);
		//		usrActionModel.setUsrCloseIssue(false);
		//		usrActionModel.setUsrCommentIssue(false);
		//		usrActionModel.setUsrChangeOwner(false);
		//		usrActionModel.setUsrWithDraw(false);
		//
		//		//		get ETS ISS USER/ ROLES MATRIX
		//		EtsIssUserRolesModel usrRolesModel = getUserRoleMatrix(etsIssObjKey);
		//
		//		//get the states actions
		//		String projectId = etsIssObjKey.getProj().getProjectId();
		//		String edgeProblemId = AmtCommonUtils.getTrimStr(usr1InfoModel.getEdgeProblemId());
		//		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		//		ArrayList ownerIdList = usr1InfoModel.getOwnerList();
		//		String probCreator = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbCreator());
		//
		//		//get user roles
		//		String checkUserRole = EtsIssFilterUtils.checkUserRole(etsIssObjKey.getEs(), projectId);
		//
		//		//various roles		
		//		boolean isOwner = false;
		//		boolean ownerSubmitter = false;
		//		boolean isSubmitter = false;
		//		boolean actionavailable = true;
		//
		//		//get the project type, if it is of blade type
		//		boolean isProjBladeType = false;
		//		isProjBladeType = etsIssObjKey.getProj().isProjBladeType();
		//
		//		//et the logged in user type I or E
		//		boolean isUsrInternal = false;
		//		isUsrInternal = usrRolesModel.isUsrInternal();
		//
		//		int usr_seq_no = usr1InfoModel.getUsr_seq_no();
		//		int cq_seq_no = usr1InfoModel.getCq_seq_no();
		//
		//		///user specific
		//
		//		//check if the logged in user is owner
		//		if (ownerIdList != null && !ownerIdList.isEmpty()) {
		//
		//			if (!isProjBladeType) { //if not blade project
		//
		//				if (ownerIdList.contains(etsIssObjKey.getEs().gUSERN)) {
		//
		//					isOwner = true;
		//
		//				}
		//
		//			}
		//
		//			{ //if blade project
		//
		//				if (ownerIdList.contains(etsIssObjKey.getEs().gUSERN) && isUsrInternal) {
		//
		//					isOwner = true;
		//
		//				}
		//
		//			}
		//
		//		}
		//
		//		//	check if the logged in user is submitter
		//		if (probCreator.equals(etsIssObjKey.getEs().gUSERN)) {
		//
		//			isSubmitter = true;
		//		}
		//
		//		//check if the logged in user is both submitter && owner
		//		if (isOwner && isSubmitter) {
		//
		//			ownerSubmitter = true;
		//		}
		//
		//		//	state specific
		//
		//		if (probState.equals("Submit") || usr_seq_no > cq_seq_no) {
		//
		//			actionavailable = false;
		//
		//		}
		//
		//		//		special case of submitter == owner
		//
		//		Global.println("owner submiiter===" + ownerSubmitter);
		//
		//		//first set action availabel
		//
		//		usrActionModel.setActionavailable(actionavailable);
		//
		//		//get submit issue from user/roles matrix
		//		usrActionModel.setUsrSubmitIssue(usrRolesModel.isUsrSubmitIssue());
		//
		//		//if action available && not A VSISTOR/EXECUTIVE entitlement
		//
		//		if (actionavailable && !usrRolesModel.isUsrVisitor()) {
		//
		//			if (isSubmitter || isOwner || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//				//for modify issue//
		//
		//				if (probState.equals("Assigned") || probState.equals("Open") || probState.equals("Rejected")) {
		//
		//					usrActionModel.setUsrModifyIssue(true);
		//
		//				}
		//
		//			}
		//
		//			//for resolve issue//
		//
		//			if (probState.equals("Assigned") || probState.equals("Open") || probState.equals("Rejected")) {
		//
		//				if (isOwner || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//					usrActionModel.setUsrResolveIssue(true);
		//
		//				}
		//
		//			}
		//
		//			//for reject && close issue//
		//
		//			if (probState.equals("Resolved")) {
		//
		//				if (isSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//					usrActionModel.setUsrRejectIssue(true);
		//					usrActionModel.setUsrCloseIssue(true);
		//
		//				}
		//
		//			}
		//
		//			//for change owner//
		//
		//			if (etsIssObjKey.isShowIssueOwner()) {
		//
		//				if (!probState.equals("Submit") && !probState.equals("Closed") && !probState.equals("Withdrawn") && cq_seq_no > usr_seq_no) {
		//
		//					if (isOwner || ownerSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//						usrActionModel.setUsrChangeOwner(true);
		//
		//					}
		//				}
		//			} else {
		//
		//				if (!probState.equals("Submit") && !probState.equals("Closed") && !probState.equals("Withdrawn") && cq_seq_no > usr_seq_no) {
		//
		//					if (isOwner || ownerSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//						usrActionModel.setUsrChangeOwner(true);
		//
		//					}
		//
		//				}
		//			}
		//
		//			//withdraw action
		//
		//			if (isSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {
		//
		//				//for withdrwa action//
		//
		//				if (!probState.equals("Closed") && !probState.equals("Withdrawn")) {
		//
		//					usrActionModel.setUsrWithDraw(true);
		//
		//				}
		//
		//			}
		//
		//			//for comment issue
		//			usrActionModel.setUsrCommentIssue(true);
		//
		//		}

		usrActionModel = (EtsIssUserActionsModel) new ETS_Workflow().getActionsforCurrentUser(usr1InfoModel.getProbState(), etsIssObjKey);
		//return (EtsIssUserActionsModel)new ETS_Workflow().getActionsforCurrentUser(usr1InfoModel.getProbState(),etsIssObjKey);

		Global.println("USR ACTION MODEL.SUBMIT NEW ISSUE===" + usrActionModel.isUsrSubmitIssue());
		Global.println("USR ACTION MODEL.VIEW ISSUE===" + usrActionModel.isUsrViewIssue());
		Global.println("USR ACTION MODEL.MODIFY ISSUE===" + usrActionModel.isUsrModifyIssue());
		Global.println("USR ACTION MODEL.RESOLVE ISSUE===" + usrActionModel.isUsrResolveIssue());
		Global.println("USR ACTION MODEL.REJECT ISSUE===" + usrActionModel.isUsrRejectIssue());
		Global.println("USR ACTION MODEL.CLOSE ISSUE===" + usrActionModel.isUsrCloseIssue());
		Global.println("USR ACTION MODEL.COMMENT ISSUE===" + usrActionModel.isUsrCommentIssue());
		Global.println("USR ACTION MODEL.CHANGE OWNER===" + usrActionModel.isUsrChangeOwner());
		Global.println("USR ACTION MODEL.USER SUBSCRIBE===" + usrActionModel.isUsrSubscribe());

		return usrActionModel;

	}

	/**
		 * This method will contain all the busniess rules
		 * to users/roles 
		 * @param usr1InfoModel
		 * @return
		 * @throws java.sql.SQLException
		 * @throws Exception
		 */

	private EtsIssUserRolesModel prepareUserRoleMatrix(EdgeAccessCntrl es, String projectId, boolean showIssueOwner, boolean isProjBladeType) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserRolesModel validUsrModel = new EtsIssUserRolesModel();

		//initialize the valid model with all false initailly
		validUsrModel.setUsrSubmitIssue(false);
		validUsrModel.setUsrReqIssueType(false);
		validUsrModel.setShowOwnerName(false);
		validUsrModel.setUsrVisitor(true);
		validUsrModel.setUsrInternal(false);
		validUsrModel.setBladeUsrInt(false);

		//get user roles
		String checkUserRole = EtsIssFilterUtils.checkUserRole(es, projectId);
		Global.println("check user role==" + checkUserRole);

		//get the blade project type

		//to check the decaf user type
		if (es.gDECAFTYPE.equals("I")) {

			validUsrModel.setUsrInternal(true);

		}

		//to check the blade user type and has valid role
		if (isProjBladeType) {

			if (es.gDECAFTYPE.equals("I") && !checkUserRole.equals(Defines.INVALID_USER)) {

				validUsrModel.setBladeUsrInt(true);

			}

		}

		//various roles		

		boolean isUserIssVisitor = true;

		///COMMENTED FROM 521 ONWARDS confirm with JV

		if (!EtsIssFilterUtils.isUserIssViewOnly(es, projectId)) {

			isUserIssVisitor = false;

		}

		//if the project is of blade type, then neglect the role of visitor, so that they could take actions

		if (isProjBladeType) {

			if (!validUsrModel.isBladeUsrInt() && !checkUserRole.equals(Defines.INVALID_USER)) {

				isUserIssVisitor = false;

			}

		}

		if (validUsrModel.isBladeUsrInt() && !checkUserRole.equals(Defines.WORKSPACE_OWNER) && !checkUserRole.equals(Defines.WORKSPACE_MANAGER) && !checkUserRole.equals(Defines.ETS_ADMIN)) {

			isUserIssVisitor = false;

		}

		if (checkUserRole.equals(Defines.ETS_EXECUTIVE)) {

			isUserIssVisitor = true;

		}

		//now set all the flags 

		//set user visitor
		validUsrModel.setUsrVisitor(isUserIssVisitor);

		Global.println("USER VISITOR ROLE ONLY===" + isUserIssVisitor);

		// Rule for submit issue >>	user should not have view access

		if (!isUserIssVisitor) {

			validUsrModel.setUsrSubmitIssue(true);

		}

		//for request new issue type

		//request to create new issues type only for Super Workspace Admin, Workspace Owner,Manager//
		if (checkUserRole.equals(Defines.ETS_ADMIN) || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.WORKFLOW_ADMIN) ) {

			validUsrModel.setUsrReqIssueType(true);

		}

		//show owner name

		//show only if owner defined && need to be shown

		if (showIssueOwner) {

			if (es.gDECAFTYPE.equals("I")) {

				validUsrModel.setShowOwnerName(true);

			}

		} //show only when owner need to be shown

		else {

			if (es.gDECAFTYPE.equals("I")) {

				if (checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

					validUsrModel.setShowOwnerName(true);

				}

			}

		}

		Global.println("USR MODEL.SUBMIT NEW ISSUE===" + validUsrModel.isUsrSubmitIssue());
		Global.println("USR MODEL.REQUEST ISSUE TYPE===" + validUsrModel.isUsrReqIssueType());
		Global.println("USR MODEL.SHOW OWNER NAME===" + validUsrModel.isShowOwnerName());
		Global.println("USR MODEL.USER VISITOR===" + validUsrModel.isUsrVisitor());
		Global.println("USR MODEL.USER INTERNAL===" + validUsrModel.isUsrInternal());
		Global.println("USR MODEL.BLADE USER INTERNAL===" + validUsrModel.isBladeUsrInt());
		Global.println("USR MODEL.BLADE PROJECT YES OR NO===" + isProjBladeType);

		return validUsrModel;

	}

	/**
		 * This method will contain all the busniess rules
		 * to users/roles and problem states/actions
		 * and users/actions matrix derived based on above roles/states
		 * 
		 * 
		 * @param usr1InfoModel
		 * @return
		 * @throws java.sql.SQLException
		 * @throws Exception
		 */

	private EtsIssUserActionsModel prepareUserActionMatrixForPMO(EtsIssProbInfoUsr1Model usr1InfoModel, EtsIssObjectKey etsIssObjKey) throws java.sql.SQLException, Exception {

		//valid user model
		EtsIssUserActionsModel usrActionModel = new EtsIssUserActionsModel();

		//		initialize the valid model with all false initailly
		usrActionModel.setUsrSubmitIssue(false);
		usrActionModel.setUsrModifyIssue(false);
		usrActionModel.setUsrResolveIssue(false);
		usrActionModel.setUsrRejectIssue(false);
		usrActionModel.setUsrCloseIssue(false);
		usrActionModel.setUsrCommentIssue(false);
		usrActionModel.setUsrChangeOwner(false);
		usrActionModel.setUsrViewIssue(false);

		//		get ETS ISS USER/ ROLES MATRIX
		EtsIssUserRolesModel usrRolesModel = getUserRoleMatrix(etsIssObjKey);

		//get the states actions
		String projectId = etsIssObjKey.getProj().getProjectId();
		String edgeProblemId = AmtCommonUtils.getTrimStr(usr1InfoModel.getEdgeProblemId());
		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		ArrayList ownerIdList = usr1InfoModel.getOwnerList();
		String probCreator = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbCreator());

		//get user roles
		String checkUserRole = EtsIssFilterUtils.checkUserRole(etsIssObjKey.getEs(), projectId);

		//various roles		
		boolean isOwner = false;
		boolean ownerSubmitter = false;
		boolean isSubmitter = false;
		boolean actionavailable = false;

		String cqTrkId = AmtCommonUtils.getTrimStr(usr1InfoModel.getCqTrkId());
		String txnStatusFlag = AmtCommonUtils.getTrimStr(usr1InfoModel.getTxnStatusFlag());
		String infoSrcFlag = AmtCommonUtils.getTrimStr(usr1InfoModel.getInfoSrcFlag());
		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());

		Global.println("PMO ID====" + cqTrkId);
		Global.println("TXN STATUS FLAG===" + txnStatusFlag);

		boolean isProjBladeType = false;
		isProjBladeType = etsIssObjKey.getProj().isProjBladeType();

		///user specific
		boolean isUsrInternal = false;
		isUsrInternal = usrRolesModel.isUsrInternal();

		//check if the logged in user is owner
		//check if the logged in user is owner
		if (ownerIdList != null && !ownerIdList.isEmpty()) {

			if (!isProjBladeType) { //if not blade project

				if (ownerIdList.contains(etsIssObjKey.getEs().gUSERN)) {

					isOwner = true;

				}

			}

			{ //if blade project

				if (ownerIdList.contains(etsIssObjKey.getEs().gUSERN) && isUsrInternal) {

					isOwner = true;

				}

			}

		} //end of owner list

		//	check if the logged in user is submitter
		if (probCreator.equals(etsIssObjKey.getEs().gUSERN)) {

			isSubmitter = true;
		}

		//check if the logged in user is both submitter && owner
		if (isOwner && isSubmitter) {

			ownerSubmitter = true;
		}

		//	state specific

		if (!infoSrcFlag.equals("P")) { //for PCR from ETS

			if (AmtCommonUtils.isResourceDefined(cqTrkId) && (txnStatusFlag.equals("A") || txnStatusFlag.equals("N"))) {

				actionavailable = true;

			}

		} else {

			if (AmtCommonUtils.isResourceDefined(cqTrkId) && !AmtCommonUtils.isResourceDefined(txnStatusFlag)) {

				actionavailable = true;

			} else if (AmtCommonUtils.isResourceDefined(cqTrkId) && AmtCommonUtils.isResourceDefined(txnStatusFlag) && (txnStatusFlag.equals("A") || txnStatusFlag.equals("N"))) {

				actionavailable = true;

			}
		}

		//		special case of submitter == owner

		Global.println("owner submiiter IN PMO ===" + ownerSubmitter);

		Global.println("action available IN PMO===" + actionavailable);

		//first set action availabel

		usrActionModel.setActionavailable(actionavailable);

		//get submit issue from user/roles matrix
		usrActionModel.setUsrSubmitIssue(usrRolesModel.isUsrSubmitIssue());

		//if action available && not A VSISTOR/EXECUTIVE entitlement

		if (actionavailable && !usrRolesModel.isUsrVisitor()) {

			if (isSubmitter || isOwner || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

				//for modify issue//

				if (probState.equals("Assigned") || probState.equals("Open") || probState.equals("Rejected")) {

					usrActionModel.setUsrModifyIssue(true);

				}

			}

			//for resolve issue//

			if (probState.equals("Assigned") || probState.equals("Open") || probState.equals("Rejected") || probState.equals("Modified")) {

				if (isOwner || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

					usrActionModel.setUsrResolveIssue(true);

				}

			}

			//for reject && close issue in PMO//
			//for issues fr PMO office, since the submitter profile wonot be perfect
			//add WORKSPACE_OWNER to take reject/close action

			if (probState.equals("Resolved")) {

				if (isSubmitter || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

					usrActionModel.setUsrRejectIssue(true);
					usrActionModel.setUsrCloseIssue(true);

				}

			}

			//for change owner//

			if (etsIssObjKey.isShowIssueOwner()) {

				if (!probState.equals("Submit") && !probState.equals("Closed")) {

					if (isOwner || ownerSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

						usrActionModel.setUsrChangeOwner(true);

					}
				}
			} else {

				if (!probState.equals("Submit") && !probState.equals("Closed")) {

					if (isOwner || ownerSubmitter || checkUserRole.equals(Defines.WORKSPACE_OWNER) || checkUserRole.equals(Defines.WORKSPACE_MANAGER) || checkUserRole.equals(Defines.ETS_ADMIN)) {

						usrActionModel.setUsrChangeOwner(true);

					}

				}
			}

			//for comment issue
			usrActionModel.setUsrCommentIssue(true);

		}

		//to regulate view issue access start

		if (!isUsrInternal) {

			if (issueAccess.equals("ALL")) {

				usrActionModel.setUsrViewIssue(true);

			} else {

				usrActionModel.setUsrViewIssue(false);
			}
			
		} else {
			
			usrActionModel.setUsrViewIssue(true);

		}
		
//		to regulate view issue access end

		Global.println("USR ACTION MODEL.SUBMIT NEW ISSUE===" + usrActionModel.isUsrSubmitIssue());
		Global.println("USR ACTION MODEL.MODIFY ISSUE===" + usrActionModel.isUsrModifyIssue());
		Global.println("USR ACTION MODEL.RESOLVE ISSUE===" + usrActionModel.isUsrResolveIssue());
		Global.println("USR ACTION MODEL.REJECT ISSUE===" + usrActionModel.isUsrRejectIssue());
		Global.println("USR ACTION MODEL.CLOSE ISSUE===" + usrActionModel.isUsrCloseIssue());
		Global.println("USR ACTION MODEL.COMMENT ISSUE===" + usrActionModel.isUsrCommentIssue());
		Global.println("USR ACTION MODEL.CHANGE OWNER===" + usrActionModel.isUsrChangeOwner());
		Global.println("USR ACTION MODEL.VIEW ISSUE===" + usrActionModel.isUsrViewIssue());

		return usrActionModel;

	}

} //end of class
