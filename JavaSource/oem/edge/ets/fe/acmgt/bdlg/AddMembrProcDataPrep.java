/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSAccessRequest;
import oem.edge.ets.fe.ETSBoardingUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.acmgt.actions.UserEntitlementsMgrIF;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.IccWrkSpcStatusModel;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.model.UserWrkSpcStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AddMembrProcDataPrep {

	private static Log logger = EtsLogger.getLogger(AddMembrProcDataPrep.class);
	public static final String VERSION = "1.10";

	/**
	 * 
	 */
	public AddMembrProcDataPrep() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param ownerDets
	 * @param reqUserDets
	 * @return
	 */

	public WrkSpcTeamActionsOpModel requestMultiPocForWO(ETSUserDetails ownerDets, ETSUserDetails reqUserDets) {

		return requestMultiPocForWO(ownerDets.getWebId(), reqUserDets.getWebId());

	}

	/**
	 * 
	 * @param sIRUserId
	 * @param reqsIRUserId
	 * @return
	 */

	public WrkSpcTeamActionsOpModel requestMultiPocForWO(String sIRUserId, String reqsIRUserId) {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();
		UserEntitlementsMgrIF entReqUtilsIF = new UserEntReqUtilsImpl();

		actOpModel.setRetCode("MULTIPOC_NOT DEFINED_FOR_WO");
		actOpModel.setRetCodeMsg("MutiPOC entitlement not defined for WO.");

		DecafEntAccessObj decafEntAccObj = new DecafEntAccessObj();
		decafEntAccObj.setEntName(Defines.MULTIPOC);

		logger.debug("requesting MULTI-POC process starts in requestMultiPocForWO");

		boolean multipocreq = entReqUtilsIF.requestEntitlementToUser(sIRUserId, reqsIRUserId, decafEntAccObj);

		logger.debug("multipocreq in requestMultiPocForWO==" + multipocreq);

		if (!multipocreq) {

			actOpModel.setRetCode("MULTIPOC_REQUEST_FORWO_FAILED");
			actOpModel.setRetCodeMsg("MultiPOC request for WO failed.");

		} else {

			actOpModel.setRetCode("MULTIPOC_REQUESTED_FOR_WO_SUCCESS");
			actOpModel.setRetCodeMsg("MultiPOC requested for WO successfully.");
		}

		return actOpModel;

	}

	public boolean isUserHasReqdEntForWrkSpc(String userId, ETSProj wrkSpc) throws SQLException, Exception {

		UserEntitlementsMgrIF entMgrIF = new UserEntReqUtilsImpl();

		//
		String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(wrkSpc);

		logger.debug("reqstdEntitlement in isUserHasReqdEntForWrkSpc ==" + reqstdEntitlement);

		return entMgrIF.isUserHasEntitlement(userId, reqstdEntitlement);

	}

	/**
	 * 
	 */

	public boolean isUserHasPendEntForWrkSpc(String userId, ETSProj wrkSpc) throws SQLException, Exception {

		UserEntitlementsMgrIF entMgrIF = new UserEntReqUtilsImpl();

		String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(wrkSpc);

		logger.debug("reqstdProject in isUserHasReqdEntForWrkSpc ==" + reqstdProject);

		return entMgrIF.isUserHasPendingEntitlement(userId, reqstdProject);

	}

	/**
	 * 
	 * @param userId
	 * @param wrkSpc
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public UserIccStatusModel getUserEntStatusForWrkSpc(String userId, ETSProj wrkSpc) throws SQLException, Exception {

		UserIccStatusModel iccStatModel = new UserIccStatusModel();
		iccStatModel.setBEntitled(isUserHasReqdEntForWrkSpc(userId, wrkSpc));
		iccStatModel.setBHasPendEtitlement(isUserHasPendEntForWrkSpc(userId, wrkSpc));

		return iccStatModel;
	}

	/**
	 * 
	 * @param wrkSpc
	 * @param sIRUserId
	 * @param sLogMsg
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean LogIntoMetrics(ETSProj wrkSpc, String sIRUserId, String sLogMsg) throws SQLException, Exception {

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		return wrkSpcDao.LogIntoMetrics(sIRUserId, WrkSpcTeamUtils.getMetricsLogMsg(wrkSpc, sLogMsg));

	}

	/**
		 * 
		 * @param actInpModel
		 * @param teamObjKey
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean addMemberToEtsUsersTab(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception {

		boolean flag = false;
		String sJob = "";

		ETSUser new_user = new ETSUser();
		new_user.setUserId(teamObjKey.getSIRUserId());
		new_user.setRoleId(actInpModel.getRoleId());
		new_user.setProjectId(actInpModel.getWrkSpcId());
		new_user.setUserJob(sJob);
		new_user.setPrimaryContact(Defines.NO);
		new_user.setLastUserId(teamObjKey.getReqstrIRUserId());
		new_user.setActiveFlag(actInpModel.getUserStatus());

		String[] res = WrkSpcInfoDAO.addProjectMemberWithStatus(new_user);

		String success = res[0];

		//log into metrics
		LogIntoMetrics(teamObjKey.getWrkSpc(), teamObjKey.getReqstrIRUserId(), "Team_Add");

		if (success.equals("0")) {

			flag = true;
		}

		return flag;
	}

	/**
	 * 
	 * @param actInpModel
	 * @param teamObjKey
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean submitEntitlementRequest(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception {

		boolean flag = false;

		ETSUserAccessRequest reqAccess = new ETSUserAccessRequest();
		reqAccess.setTmIrId(teamObjKey.getSIRUserId());
		reqAccess.setPmIrId(teamObjKey.getWrkSpcOwnrId());

		if (AmtCommonUtils.isResourceDefined(actInpModel.getUserAssignCompany())) {

			reqAccess.setUserCompany(actInpModel.getUserAssignCompany());

		} else {

			reqAccess.setUserCompany(teamObjKey.getEtsUserIdDets().getCompany());
		}

		if (AmtCommonUtils.isResourceDefined(actInpModel.getUserAssignCountry())) { // set the country

			reqAccess.setUserCountry(actInpModel.getUserAssignCountry());

		} else {

			reqAccess.setUserCountry(teamObjKey.getEtsUserIdDets().getCountryCode());
		}

		reqAccess.setDecafEntitlement(WrkSpcTeamUtils.getWrkSpcReqEntitlement(teamObjKey.getWrkSpc()));
		reqAccess.setProjectName(WrkSpcTeamUtils.getWrkSpcReqProject(teamObjKey.getWrkSpc()));
		reqAccess.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(teamObjKey.getWrkSpc().getProjectType()));

		ETSStatus status = new ETSStatus();

		if (teamObjKey.getWrkSpc().getProjectType().equals(Defines.ETS_WORKSPACE_TYPE)) {

			status = reqAccess.requestProject();
		}

		if (teamObjKey.getWrkSpc().getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)) {

			status = reqAccess.requestEntitlement(WrkSpcTeamUtils.getWrkSpcReqEntitlementObj(teamObjKey.getWrkSpc()));
		}

		logger.debug("ETS STATUS CODE AddMembrProcDataPrep::: submitEntitlementRequest==" + status.getErrCode());

		if (status.getErrCode() == 0) {

			flag = true;

		}

		return flag;

	}

	/**
	 * 
	 * @param actInpModel
	 * @param teamObjKey
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public IccWrkSpcStatusModel submitEntAddMembrToWrkSpc(WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception {

		String userId = teamObjKey.getSIRUserId(); //IR USER ID

		//check if userid is entitled/pending entitled
		UserIccStatusModel userStatModel = getUserEntStatusForWrkSpc(userId, teamObjKey.getWrkSpc());

		boolean bEntitled = userStatModel.isBEntitled();
		boolean bHasPendEtitlement = userStatModel.isBHasPendEtitlement();

		IccWrkSpcStatusModel compStatModel = new IccWrkSpcStatusModel();

		UserWrkSpcStatusModel wrkSpcStatModel = new UserWrkSpcStatusModel();

		logger.debug("bEntitled:::submitEntAddMembrToWrkSpc==" + bEntitled);
		logger.debug("bHasPendEtitlement:::submitEntAddMembrToWrkSpc==" + bHasPendEtitlement);

		//internal user
		// user not entitled, request project for him..

		if (bEntitled) {

			// since the user has an entitlement, just add the member
			// to the workspace 
			actInpModel.setUserStatus("A");
			wrkSpcStatModel.setAddMembrToWrkSpc(addMemberToEtsUsersTab(actInpModel, teamObjKey));

		} else {

			if (!bEntitled && !bHasPendEtitlement) {

				userStatModel.setBReqstEntitlement(submitEntitlementRequest(actInpModel, teamObjKey));

				logger.debug("setBReqstEntitlement:::submitEntAddMembrToWrkSpc==" + userStatModel.isBReqstEntitlement());

				if (userStatModel.isBReqstEntitlement()) {

					//add member to ETS.ETS_USERS
					actInpModel.setUserStatus("P");
					wrkSpcStatModel.setAddMembrToWrkSpc(addMemberToEtsUsersTab(actInpModel, teamObjKey));

				}
			} 

		}//user does not have entitlements

		logger.debug("setAddMembrToWrkSpc:::submitEntAddMembrToWrkSpc==" + wrkSpcStatModel.isAddMembrToWrkSpc());

		compStatModel.setUsrIccStatModel(userStatModel);
		compStatModel.setWrkSpcStatModel(wrkSpcStatModel);

		return compStatModel;

	}

	/**
		 * 
		 * @param compStatModel
		 * @param actInpModel
		 * @param teamObjKey
		 * @return
		 */

	public WrkSpcTeamActionsOpModel sendMailProcess(IccWrkSpcStatusModel compStatModel, WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		UserIccStatusModel userStatModel = compStatModel.getUsrIccStatModel();
		UserWrkSpcStatusModel wrkSpcStatModel = compStatModel.getWrkSpcStatModel();

		boolean bEntitled = userStatModel.isBEntitled();
		boolean bHasPendEtitlement = userStatModel.isBHasPendEtitlement();

		//extract user dets
		ETSUserDetails etsUserDets = teamObjKey.getEtsUserIdDets();

		String reqstEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(teamObjKey.getWrkSpc());

		ETSAccessRequest ar = new ETSAccessRequest();
		ar.setProjName(teamObjKey.getWrkSpc().getName());
		ar.setMgrEMail(teamObjKey.getReqUserIdDets().getEMail());

		if (!bEntitled && !bHasPendEtitlement) {

			if (userStatModel.isBReqstEntitlement()) {

				logger.debug("SEND ACCEPT EMAIL PENDING ENT IN sendMailProcess TO/FROM===" + etsUserDets.getEMail() + "::" + teamObjKey.getReqUserIdDets().getEMail());

				ETSBoardingUtils.sendAcceptEMailPendEnt(etsUserDets, ar, ar.getProjName(), "", "", teamObjKey.getReqUserIdDets().getEMail());

				actOpModel.setRetCode("REQUESTED_APPROVED");

				if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_EXTERNAL) {

					if (etsUserDets.getPocEmail().equals(teamObjKey.getReqUserIdDets().getEMail())) {

						logger.debug("POC EMAIL OF USERID EQUALS REQUESTOR EMAIL ID");

						actOpModel.setRetCodeMsg("The user has been added to the workspace successfully. The corresponding entitlement to access E&TS Connect has been requested for the user.");

					} else {

						actOpModel.setRetCodeMsg("The user has been added to the workspace successfully. The corresponding entitlement to access E&TS Connect has been requested for the user and is pending their IBM contact.");
					}

				}

				if (etsUserDets.getUserType() == etsUserDets.USER_TYPE_INTERNAL) {

					logger.debug("REQUEST SUCCESS AND PENDING FOR INTERNALS WITH MANAGER");

					actOpModel.setRetCodeMsg("The user has been added to the workspace successfully. The corresponding entitlement to access E&TS Connect has been requested for the user and is pending with their manager.");

				}

				//send mail to requstor//
				logger.debug("START SENDING MAIL TO REQUESTOR AFTER ENT REQUEST");
				ETSBoardingUtils.sendMailToReqstorInvOnEntReqst(teamObjKey.getEtsUserIdDets(), teamObjKey.getReqUserIdDets(), teamObjKey.getWrkSpc(), reqstEntitlement);
				logger.debug("END SENDING MAIL TO REQUESTOR AFTER ENT REQUEST");
			}

		} else {

			if (wrkSpcStatModel.isAddMembrToWrkSpc()) {

				logger.debug("email sending on successful add sendMailProcess==" + etsUserDets.getEMail() + "::" + teamObjKey.getReqUserIdDets().getEMail());

				ETSBoardingUtils.sendAcceptEMail(etsUserDets, ar, ar.getProjName(), "", "", teamObjKey.getReqUserIdDets().getEMail());

				actOpModel.setRetCode("USER_ADDED");
				actOpModel.setRetCodeMsg("The user has been added to the workspace successfully.");

			}
		}

		if (!wrkSpcStatModel.isAddMembrToWrkSpc()) {

			logger.debug("ERROR ON ADDING MEMBER FOR USERID::PROJECTID==" + etsUserDets.getWebId() + "::" + teamObjKey.getWrkSpcId());

			actOpModel.setRetCode("ERROR_ADD_MEMBER");

			actOpModel.setRetCodeMsg("An error occured when adding the user to workspace. Please try again later.");

			//send mail to requstor//
			logger.debug("START SENDING MAIL TO REQUESTOR AFTER ADD MEMBER TO WRKSPC");
			ETSBoardingUtils.sendMailToReqstorInvOnAccessToWrkSpc(teamObjKey.getWrkSpcOwnrDets(), teamObjKey.getEtsUserIdDets(), teamObjKey.getReqUserIdDets(), teamObjKey.getWrkSpc(), reqstEntitlement);
			logger.debug("END SENDING MAIL TO REQUESTOR AFTER ADD MEMBER TO WRKSPC");

		}

		return actOpModel;

	}

	/**
			 * 
			 * @param compStatModel
			 * @param actInpModel
			 * @param teamObjKey
			 * @return
			 */

	public WrkSpcTeamActionsOpModel updateInviteStatTab(IccWrkSpcStatusModel compStatModel, WrkSpcTeamActionsInpModel actInpModel, WrkSpcTeamObjKey teamObjKey) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		UserIccStatusModel userStatModel = compStatModel.getUsrIccStatModel();
		UserWrkSpcStatusModel wrkSpcStatModel = compStatModel.getWrkSpcStatModel();

		boolean bEntitled = userStatModel.isBEntitled();
		boolean bHasPendEtitlement = userStatModel.isBHasPendEtitlement();
		boolean bAddMembrToWrkSpc = wrkSpcStatModel.isAddMembrToWrkSpc();

		InvMembrToWrkSpcDAO invDao = new InvMembrToWrkSpcDAO();

		UserInviteStatusModel invStatModel = WrkSpcTeamUtils.transFormToInvStatModel(actInpModel);

		actOpModel.setRetCode("UPD_INVITE_TAB");

		if (!bEntitled && !bHasPendEtitlement) {

			if (userStatModel.isBReqstEntitlement()) {

				invStatModel.setInviteStatus("R");

				boolean ret = invDao.updInviteStatusTab(invStatModel);

				if (ret) {

					actOpModel.setRetCodeMsg("INVITE TABLE UPDATE WITH STATUS -- R");
				} else {
					actOpModel.setRetCodeMsg("INVITE TABLE UPDATE NOT SUCCESSFUL");
				}

			}

		} else {

			if (wrkSpcStatModel.isAddMembrToWrkSpc()) {

				invStatModel.setInviteStatus("S");

				boolean ret = invDao.updInviteStatusTab(invStatModel);

				if (ret) {

					actOpModel.setRetCodeMsg("INVITE TABLE UPDATE WITH STATUS -- S");

				} else {

					actOpModel.setRetCodeMsg("INVITE TABLE UPDATE NOT SUCCESSFUL");
				}

			}

		}

		return actOpModel;

	}

	/**
				 * 
				 * @param compStatModel
				 * @param actInpModel
				 * @param teamObjKey
				 * @return
				 */

	public WrkSpcTeamActionsOpModel updateInviteStatTab(WrkSpcTeamActionsInpModel actInpModel, String status) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		InvMembrToWrkSpcDAO invDao = new InvMembrToWrkSpcDAO();

		UserInviteStatusModel invStatModel = WrkSpcTeamUtils.transFormToInvStatModel(actInpModel);

		actOpModel.setRetCode("UPD_INVITE_TAB");

		invStatModel.setInviteStatus(status);

		boolean ret = invDao.updInviteStatusTab(invStatModel);

		if (ret) {

			actOpModel.setRetCodeMsg("INVITE TABLE UPDATE WITH STATUS -- " + status);
		} else {
			actOpModel.setRetCodeMsg("INVITE TABLE UPDATE NOT SUCCESSFUL");
		}

		return actOpModel;

	}

} //end of class
