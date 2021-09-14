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
package oem.edge.ets.fe.acmgt.helpers;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.acmgt.resources.WrkSpcInviteJobProps;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WrkSpcMailHandler {

	private String INVJOB_ADMIN_MAILID;
	private String INVJOB_ICCSUP_MAILID;
	private String INVJOB_DEVSUP_MAILID;
	private String INVJOB_MAIL_FROM;
	private String INVJOB_MAIL_SUBJHEAD;
	private String INVJOB_DEVMAIL_FLAG;
	private int INVJOB_MAIL_ERR_COUNT;
	private int INVJOB_MAIL_SUCC_COUNT;

	private static Log logger = EtsLogger.getLogger(WrkSpcMailHandler.class);
	public static final String VERSION = "1.8";

	/**
	 * 
	 */
	public WrkSpcMailHandler() {
		super();
		loadJobparams();
	}

	public void loadJobparams() {

		HashMap propMap = new HashMap();
		try {

			propMap = WrkSpcInviteJobProps.getInstance().getInvJobPropMap();

			INVJOB_ADMIN_MAILID = (String) propMap.get("invjob.admin.mailid");
			INVJOB_ICCSUP_MAILID = (String) propMap.get("invjob.iccsup.mailid");
			INVJOB_DEVSUP_MAILID = (String) propMap.get("invjob.devsup.mailid");
			INVJOB_MAIL_FROM = (String) propMap.get("invjob.mail.from");
			INVJOB_MAIL_SUBJHEAD = (String) propMap.get("invjob.mail.subjhead");
			INVJOB_DEVMAIL_FLAG = (String) propMap.get("invjob.devmail.flag");
			String sErrCount = (String) propMap.get("invjob.mail.err.count");
			String succCount = (String) propMap.get("invjob.mail.succ.count");

			if (AmtCommonUtils.isResourceDefined(sErrCount)) {

				INVJOB_MAIL_ERR_COUNT = Integer.parseInt(sErrCount);
			}

			if (AmtCommonUtils.isResourceDefined(succCount)) {

				INVJOB_MAIL_SUCC_COUNT = Integer.parseInt(succCount);
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	/**
		 * This method will send the mails if any error occurs
		 * Creation date: (12/15/2001 6:44:54 PM)
		 */

	public void sendMailOnErrorToSupp(String mailSubj, String mailMessg) {

		try {

			if (INVJOB_DEVMAIL_FLAG.equals("Y")) {

				ETSMail mail = new ETSMail();
				mail.setBcc("");
				mail.setCc("");
				mail.setFrom(INVJOB_MAIL_FROM);
				mail.setReplyTo("");
				mail.setTo(INVJOB_DEVSUP_MAILID);
				mail.setSubject(mailSubj);
				mail.setMessage(mailMessg);

				ETSUtils.sendEmail(mail);

			}

			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(INVJOB_MAIL_FROM);
			mail.setReplyTo("");
			mail.setTo(INVJOB_ICCSUP_MAILID);
			mail.setSubject(mailSubj);
			mail.setMessage(mailMessg);

			ETSUtils.sendEmail(mail);

		} catch (java.net.UnknownHostException err) {
			logger.error("UnknownHostException in sendMailOnErrorToSupp at WrkSpcMailHandler", err);
			err.printStackTrace();

		} catch (IOException err) {
			logger.error("IOException in sendMailOnErrorToSupp  @ WrkSpcMailHandler", err);
			err.printStackTrace();

		} catch (Exception err) {
			logger.error("General Exception in sendMailOnErrorToSupp  @ WrkSpcMailHandler", err);
			err.printStackTrace();

		}

	}

	public  boolean sendMailToWOOnAddMembr(WrkSpcTeamObjKey teamObjKey) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			String wrkSpcName = teamObjKey.getWrkSpc().getName();
			String userWebId = teamObjKey.getEtsUserIdDets().getWebId();
			String userEmailId = teamObjKey.getEtsUserIdDets().getEMail();
			String ownerEmailId = teamObjKey.getWrkSpcOwnrDets().getEMail();

			String mailSubj = "User has been added to E&TS Connect Workspace successfully.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("The following user has been approved to access the following workspace on E&TS Connect:\n\n");
			message.append("Workspace: " + wrkSpcName + "\n\n");
			message.append("IBM ID   :" + userWebId + "\n\n");
			message.append("Email ID :" + userEmailId + "\n\n");

			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(props.getAdminEmail());
			mail.setReplyTo("");
			mail.setTo(ownerEmailId);
			mail.setSubject(mailSubj);
			mail.setMessage(message.toString());

			sent = ETSUtils.sendEmail(mail);

		} catch (Exception ex) {
			logger.error("General Exception in sendMailToWOOnAddMembr@ WrkSpcMailHandler", ex);
			ex.printStackTrace();

		}
		return sent;

	}

	public  boolean sendMailToWOOnMultipleIds(String userId, String wrkSpcOwnrId, String wrkSpcId) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();
			AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();

			//get the project details
			ETSProj wrkSpcDets = WrkSpcInfoDAO.getProjectDetails(wrkSpcId);

			//get the wrkspc owner details
			ETSUserDetails wrkSpcOwnerDets = wrkSpcDao.getUserDetails(wrkSpcOwnrId);

			//get multi user dets
			ArrayList userList = wrkSpcDao.getMultiIDDetails(userId);
			
			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandProps(wrkSpcDets.getProjectType());
			
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }
		    int intTcId = ETSDatabaseManager.getTopCatId(wrkSpcDets.getProjectId(),Defines.TEAM_VT);
			int usize = 0;
			
			if (userList != null && !userList.isEmpty()) {

				usize = userList.size();
			}

			String wrkSpcName = wrkSpcDets.getName();
			String userWebId = "";
			String userEmailId = "";

			UserObject userDet = new UserObject();

			String mailSubj = "Multiple IBM IDs are found while trying to add invited user to " + wrkSpcName+".";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("Multiple IBM IDs were found when trying to add a user you invited to the following workspace on IBM "+ strCustConnect + unBrandedprop.getAppName() + ". Please go the “Add Member” area within the “Team” tab of this workspace and specify which ID you want to have access. Your invited user will not get access until you take this action.\n\n");
			message.append("Workspace: " + wrkSpcName + "\n\n");

			for (int i = 0; i < usize; i++) {

				userDet = (UserObject) userList.get(i);
				message.append("Member IBM ID(" +i+")   :" + userDet.gIR_USERN + "\n\n");
				message.append("Member Email ID(" +i+") :" + userDet.gEMAIL + "\n\n");

			}
			
			message.append(oem.edge.common.Global.getUrl("ets/" + "displayAddMembrInput.wss?action=admin_add&proj=" + wrkSpcDets.getProjectId() + "&tc=" + intTcId + "&cc=" + intTcId + "&linkid=" + unBrandedprop.getLinkID() + "\\n\n"));
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
			
			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(props.getAdminEmail());
			mail.setReplyTo("");
			mail.setTo(wrkSpcOwnerDets.getEMail());
			mail.setSubject(mailSubj);
			mail.setMessage(message.toString());

			sent = ETSUtils.sendEmail(mail);

		} catch (SQLException ex) {
			logger.error("SQL Exception in sendMailToWOOnAddMembr@ WrkSpcMailHandler", ex);
			ex.printStackTrace();

		} catch (Exception ex) {
			logger.error("General Exception in sendMailToWOOnAddMembr@ WrkSpcMailHandler", ex);
			ex.printStackTrace();

		}
		return sent;

	}

	public boolean sendMailToInvitedUserOnActivation(String userId, String wrkSpcOwnrId, String projectId) {
		boolean sent = false;

		try {
			ETSProperties props = new ETSProperties();
	
			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projectId);
			AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();

			//get the project details
			ETSProj proj = WrkSpcInfoDAO.getProjectDetails(projectId);

			//get the wrkspc owner details
			ETSUserDetails wrkSpcOwnerDets = wrkSpcDao.getUserDetails(wrkSpcOwnrId);
	
			String subj = "" + unBrandedprop.getAppName() + " access is complete";
	
			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("Your access to the " + proj.getName() + " following workspace at " + unBrandedprop.getAppName() + " is complete. Please contact me if you have any issues accessing this workspace.\n");
			message.append("\n\n");
			message.append("IBM " + unBrandedprop.getAppName() + " enables clients of IBM Engineering and Technology Services (E&TS) and IBM team members to collaborate on proposals and project information.\n");
			message.append("" + unBrandedprop.getAppName() + " provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.\n");
	
			message.append("Just click the URL below to begin.\n");
			message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "?linkid=" + unBrandedprop.getLinkID() + "&proj=" + proj.getProjectId() + "&pghead=" + unBrandedprop.getHtmlPgHead() + "&pgtitle=" + unBrandedprop.getHtmlPgTitle() + "\n\n"));
			
			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());
	
			sent = ETSUtils.sendEMail(wrkSpcOwnerDets.getEMail(), userId, wrkSpcOwnerDets.getEMail(), oem.edge.common.Global.mailHost, message.toString(), subj, wrkSpcOwnerDets.getEMail());
	
		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}	
		return sent;
	}
} //end of class
