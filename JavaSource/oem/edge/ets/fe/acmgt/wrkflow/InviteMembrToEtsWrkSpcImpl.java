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
package oem.edge.ets.fe.acmgt.wrkflow;

import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.amt.UserObject;
import oem.edge.ets.fe.acmgt.actions.InviteMembrToWrkSpcIF;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcMailHandler;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserIccStatusModel;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InviteMembrToEtsWrkSpcImpl extends AddMembrToEtsWrkSpcImpl implements InviteMembrToWrkSpcIF {

	private AddMembrToWrkSpcDAO addWrkSpcDao;
	private InvMembrToWrkSpcDAO invWrkSpcDao;
	private WrkSpcInfoDAO wrkspcInfoDao;
	private static Log logger = EtsLogger.getLogger(InviteMembrToEtsWrkSpcImpl.class);
	public static final String VERSION = "1.8";

	/**
	 * 
	 */
	public InviteMembrToEtsWrkSpcImpl() {
		super();
		addWrkSpcDao = new AddMembrToWrkSpcDAO();
		invWrkSpcDao = new InvMembrToWrkSpcDAO();
		wrkspcInfoDao = new WrkSpcInfoDAO();

		// TODO Auto-generated constructor stub
	}

	public WrkSpcTeamActionsOpModel inviteMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel) throws SQLException, Exception {

		WrkSpcTeamActionsOpModel actOpModel = new WrkSpcTeamActionsOpModel();

		String userId = actInpModel.getUserId();
		String wrkSpcId = actInpModel.getWrkSpcId();
		String wrkSpcType = actInpModel.getWrkSpcType();
		String requestorId = actInpModel.getRequestorId();
		String userStatus = actInpModel.getUserStatus();

		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		String wrkSpcOwnerId = WrkSpcTeamUtils.getOwnerIdForProject(wrkSpcId);

			//check if the user is defined in WORKSPACE
			if (isUserDefndInWrkSpc(wrkSpcId, userId)) {
	
				logger.debug("USERID::" + userId + " already defined in workspace" + wrkSpcId);
	
				actOpModel.setRetCode("USER_DEFND_IN_WRKSPC");
				actOpModel.setRetCodeMsg((String) getWrkSpcPropMap("wrkspc").get("USER_DEFND_IN_WRKSPC"));
	
				//update status as SUCCESS , as userid is already in workspace
				boolean success = updateInviteStatus(userId, wrkSpcId, "S");
	
				// TODO send email to invite/wo check with laxman
	
			} else { //if user id is not defined in ETS, then check in AMT-ICC to check if the user has an entry
	
				//check if the user is defined in AMT
				//if defined make a request for entitlement
				if (isUserDefndinICC(userId)) {
	
					logger.debug("USERID::" + userId + " defined in ICC");
	
					UserIccStatusModel iccStatModel = addWrkSpcDao.getUserIdStatusInICC(userId);
	
					int uidcount = iccStatModel.getAmtuidcount();
					int emailcount = iccStatModel.getAmtemailcount();
					int decafidcount = iccStatModel.getDecafidcount();
	
					logger.debug("IR USERID COUNT:" + userId + " defined in ICC::" + uidcount);
					logger.debug("USERID EMAIL COUNT:" + userId + " defined in ICC::" + emailcount);
					logger.debug("DECAF USERID COUNT:" + userId + " defined in DECAF::" + decafidcount);
	
					if (uidcount == 1) {
	
						if (decafidcount == 0) {
	
							logger.debug("USERID::" + userId + " DEFINED IN ICC/DECAF ID COUNT==0.Once again updating profile into ICC");
	
							addWrkSpcDao.syncUser(userId);
	
						}
	
						//add member to wrkspc
						actOpModel = processAddMemberToWrkSpc(actInpModel);
	
					} else if (emailcount == 1) {
	
						actInpModel.setUserId(getIRUserIdFromEmail(userId));
	
						if (decafidcount == 0) {
	
							logger.debug("USERID::" + userId + " DEFINED IN ICC/DECAF ID COUNT==0.Once again updating profile into ICC");
	
							addWrkSpcDao.syncUser(getIRUserIdFromEmail(userId));
	
						}
						//add member to wrkspc
						actOpModel = processAddMemberToWrkSpc(actInpModel);
	
					} else if (emailcount > 1) {
	
						mailHandler.sendMailToWOOnMultipleIds(userId, wrkSpcOwnerId, wrkSpcId);
					}
	
				} else { //when the user is not defined in AMT
	
					logger.debug("USERID::" + userId + " NOT defined in ICC. checking in UD");
	
					//if id exists in IR
	
					if (isUserIdDefndInUD(userId)) {
	
						logger.debug("USERID::" + userId + " DEFINED IN UD.Loading into ICC");
	
						addWrkSpcDao.syncUser(userId);
	
						logger.debug("USERID::" + userId + " DEFINED IN UD.Loaded into ICC and adding to wrkspace");
						//add member to wrkspc
						actOpModel = processAddMemberToWrkSpc(actInpModel);
	
					} else { //if userid not defined in UD
	
						logger.debug("USERID::" + userId + " NOT DEFINED IN UD. Try Next Time!!!");
	
					}
	
				} //if user is not defined in ICC
	
			} //IF user not defined in ETS
		
		return actOpModel;

	}

	public void inviteMemberSendAccessMail(WrkSpcTeamActionsInpModel actInpModel) throws SQLException, Exception {
		
		String userId = actInpModel.getUserId();
		String wrkSpcId = actInpModel.getWrkSpcId();
		String wrkSpcType = actInpModel.getWrkSpcType();
		String requestorId = actInpModel.getRequestorId();
		WrkSpcMailHandler mailHandler = new WrkSpcMailHandler();
		String wrkSpcOwnerId = WrkSpcTeamUtils.getOwnerIdForProject(wrkSpcId);
	
		//Send Emails to invited users once they are active
		//Check user defined in workspace
		if (isUserDefndInWrkSpc(wrkSpcId,userId)) {
			logger.debug("USERID::" + userId + " defined in workspace" + wrkSpcId);
			boolean userActiveStatus = chkUserWrkspcStatus(wrkSpcId,userId);
			if (userActiveStatus) {
				//update status as SUCCESS , as userid is activated in workspace
				logger.debug("USERID::" + userId + " DEFINED IN WRKSPC. STATUS active, Sending mail to user.");
				boolean success = updateInviteStatus(userId, wrkSpcId, "S");
				mailHandler.sendMailToInvitedUserOnActivation(userId, wrkSpcOwnerId, wrkSpcId);
			} else {
				logger.debug("USERID::" + userId + " DEFINED IN WRKSPC. STATUS not active");
			}
		} else {
			logger.debug("USERID::" + userId + " registered in ICC . but user has not been added to Wrkspc.");
		}
	

	}
	/**
	 * 
	 * @param userId
	 * @param projectId
	 * @param invStatus
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean updateInviteStatus(String userId, String projectId, String invStatus) throws SQLException, Exception {

		//if defined in wrk spc, set the status flag in INVITE_STATUS table='S'
		UserInviteStatusModel invStatModel = new UserInviteStatusModel();
		invStatModel.setUserId(userId);
		invStatModel.setWrkSpcId(projectId);
		invStatModel.setInviteStatus(invStatus); //ETS ALREADY IN WORKSPACE, SO SUCCESS!!
		boolean success = invWrkSpcDao.updInviteStatusTab(invStatModel);

		return success;
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public String getIRUserIdFromEmail(String userId) throws SQLException, Exception {

		ArrayList idList = addWrkSpcDao.getMultiIDDetails(userId);
		UserObject uo = (UserObject) idList.get(0);
		String mailIRUserId = uo.gIR_USERN;

		return mailIRUserId;
	}

	/**
	 * 
	 * @param userId
	 * @param wrkspcId
	 * @return
	 * @throws SQLException
	 */

	public boolean chkUserWrkspcStatus(String wrkspcId, String userId) throws SQLException{
		//returns true for active users
		boolean userActiveStatus = false;
		String userStatus = wrkspcInfoDao.getUserWrkspcStatus(wrkspcId,userId);
		if (userStatus.equals("A")){
			userActiveStatus = true;
		}
		
		return userActiveStatus;
	}

} //end of class
