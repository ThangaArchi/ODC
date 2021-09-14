/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe;

import java.sql.Connection;
import java.util.ResourceBundle;

import oem.edge.common.SysLog;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.ubp.ETSUserRequestDAO;

import org.apache.commons.logging.Log;

public class ETSBoardingUtils {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	public static final String VERSION = "1.24";
	private static Log logger = EtsLogger.getLogger(ETSBoardingUtils.class);

	public static boolean sendManagerEMail(ETSUserDetails u, String sendTo, ETSProj project, String sMgrEmail) {
		return sendManagerEMail(u, sendTo, project, sMgrEmail, "");
	}
	public static boolean sendManagerEMail(ETSUserDetails u, String sendTo, ETSProj project, String sMgrEmail, String comments) {

		boolean sent = false;
		Connection conn = null;

		try {

			ETSProperties props = new ETSProperties();

			conn = WrkSpcTeamUtils.getConnection();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandProps(project.getProjectType());
			
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			java.util.List projIdList = ETSDatabaseManager.getProjectsEmailIsOwner(sendTo, conn);

			String subj = "IBM " + unBrandedprop.getAppName() + " - Access Request from " + u.getFirstName() + " " + u.getLastName() + " at " + u.getCompany();

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("You have been identified as the Proposal/Project Owner for the following\n");
			message.append("request for access to an IBM "+ strCustConnect + unBrandedprop.getAppName() + ". Please process accordingly by clicking\n");
			message.append("on the link(s) below, verifying the user information, and choosing to accept or reject their\n");
			message.append("request. If you do not have access to this project, please forward this to " + unBrandedprop.getAdminEmailID() + ".\n\n");

			message.append("Requested Project or Proposal:  " + project.getName() + "\n\n");
			
			//6.3 Request from Athar to give the correct URL for add Member Request.
			ETSUserRequestDAO uarDAO = new ETSUserRequestDAO();
			int requestId = uarDAO.getRequestIdForAccesssWrkspcReq(project.getName(),project.getProjectType(),u.getWebId());
			if (requestId == 0) {
				message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "\n\n"));
			} else if (project.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)){
				message.append(oem.edge.common.Global.getUrl("ets/" + "AICProcReqAccessServlet.wss?linkid=" +unBrandedprop.getLinkID()+ "&option=showreq&requestid=" + requestId + "&ibmid=" + u.getWebId() +"\n\n"));
			} else {
				message.append(oem.edge.common.Global.getUrl("ets/" + "ETSProcReqAccessServlet.wss?linkid=" +unBrandedprop.getLinkID()+ "&option=showreq&requestid=" + requestId + "&ibmid=" + u.getWebId() +"\n\n"));
			}
			// ETSProcReqAccessServlet.wss?linkid=251000&option=showreq&requestid=47&ibmid=amakathi@in.ibm.com
			//message.append("Click a link below for the workspace that matches the request.\n\n");
			//for (int i = 0; i < projIdList.size(); i++) {
			//
			//    String projId = (String) projIdList.get(i);
			//
			//    ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projId);
			//
			//    // get top cat for team tab for the project
			//    int iTCForTeam = ETSDatabaseManager.getTopCatId(projId,Defines.TEAM_VT);
			//
			//    message.append(proj.getName() + "\n");
			//  message.append(oem.edge.common.Global.getUrl("ets/ETSProjectsServlet.wss?action=viewaccreqs&proj=" + proj.getProjectId() + "&tc=" + String.valueOf(iTCForTeam) + "&linkid=" + Defines.LINKID + "\n\n"));
			//}

			message.append(getRequestInfoText(u, project.getName(), sMgrEmail, comments));
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(u.getEMail(), sendTo, "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
			// ??
		} finally {
			ETSDBUtils.close(conn);
		}
		return sent;
	}
	public static boolean sendMemberEMail(ETSUserDetails u, String sendTo, String project, String sMgrEmail) {
		return sendMemberEMail(u, sendTo, project, sMgrEmail, "");
	}
	public static boolean sendMemberEMail(ETSUserDetails u, String sendTo, String project, String sMgrEmail, String comments) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);

			String subj = "Access Request to " + unBrandedprop.getAppName() + " from " + u.getFirstName() + " " + u.getLastName() + " at " + u.getCompany();

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("You have been identified as the IBM contact for the following request for access\n");
			message.append("to " + unBrandedprop.getAppName() + ". Please process accordingly by selecting the link provided below.\n");
			message.append("If you do not have access to this workspace, please forward this to " + unBrandedprop.getAdminEmailID() + ".\n\n");

			message.append("Requested Project or Proposal:  " + project + "\n\n");
			message.append("Click the link below for " + unBrandedprop.getAppName() + ", select the workspace that matches the request\n");
			message.append("and then do a \"Request add members\" under team tab.\n\n");

			message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "?linkid=" + unBrandedprop.getLinkID() + "&pghead=" + unBrandedprop.getHtmlPgHead() + "&pgtitle=" + unBrandedprop.getHtmlPgTitle() + "\n\n"));

			message.append(getRequestInfoText(u, project, sMgrEmail, comments));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(u.getEMail(), sendTo, "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;
	}
	public static boolean sendAdminEMail(ETSUserDetails u, String project, String sMgrEmail) {
		return sendAdminEMail(u, project, sMgrEmail, "");
	}
	public static boolean sendAdminEMail(ETSUserDetails u, String project, String sMgrEmail, String comments) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);

			String subj = "Access Request to " + unBrandedprop.getAppName() + " from " + u.getFirstName() + " " + u.getLastName() + " at " + u.getCompany();

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("This request has been submitted but it cannot be processed automatically.\n");
			message.append("The IBM Contact identified is not a known workspace owner nor a member of any workspace.\n");
			message.append("Investigate IBM contact given (find the person, are they in " + unBrandedprop.getBrandAbrvn() + ", etc).\n");
			message.append("Then work with proposal/project representatives to determine how to process this request.\n\n");

			message.append(getRequestInfoText(u, project, sMgrEmail, comments));

			String adminEMail = props.getAdminEmail();

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(u.getEMail(), props.getAdminEmail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;
	}

	public static boolean sendAcceptEMail(ETSUserDetails u, ETSAccessRequest ar, String project, String actor, String reply, String sMgrEmail) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);
			
		    String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			String subj = "IBM "+ strCustConnect + unBrandedprop.getAppName() + " access has been approved.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("You have been approved to access the following workspace on IBM Customer Connect:\n\n");
			message.append("Workspace: " + project + "\n\n");
			message.append("IBM " + unBrandedprop.getAppName() + " enables clients of IBM " + unBrandedprop.getBrandExpsn() + " (" + unBrandedprop.getBrandAbrvn() + ") and IBM team members to collaborate on proposals and project information.\n");
			message.append("" + unBrandedprop.getAppName() + " provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.\n");

			message.append("Just click the URL below to begin.\n");
			message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "?linkid=" + unBrandedprop.getLinkID() + "&pghead=" + unBrandedprop.getHtmlPgHead() + "&pgtitle=" + unBrandedprop.getHtmlPgTitle() + "\n\n"));

			if (reply != null && reply.length() > 0) {
				message.append("The following additional information has been provided from the approver.\n");
				message.append("Approver: " + actor + "\n");
				message.append("Comments: " + reply + "\n\n");
			}

			message.append(getRequestInfoTextApprove(u, ar.getProjName(), sMgrEmail));
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(ar.getMgrEMail(), u.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;

	}
	public static boolean sendAcceptEMailPendEnt(ETSUserDetails u, ETSAccessRequest ar, String project, String actor, String reply, String sMgrEmail) {
		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);
						
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			String subj = "IBM "+ strCustConnect + unBrandedprop.getAppName() + " access in progress";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("The workspace owner has approved your access to the following workspace at IBM "+ strCustConnect + unBrandedprop.getAppName() + ". Access to this workspace is currently pending the approval of your manager or point-of-contact.\n");
			message.append("Workspace: " + project + "\n\n");
			message.append("Your access will be active once your manager or point-of-contact approves. Click on the link below to check the status of this request.\n");
			message.append(oem.edge.common.Global.getUrl("DecafReportNewServlet.wss?decafid=" + u.getDecafId() + "&opt=97&linkid=172000\n\n"));
			message.append("IBM " + unBrandedprop.getAppName() + " enables clients of IBM "+ strCustConnect + unBrandedprop.getBrandExpsn() + " ("+ unBrandedprop.getBrandAbrvn() + ") and IBM team members to collaborate on proposals and project information.\n");
			message.append("" + unBrandedprop.getAppName() + " provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.\n");

			message.append("Just click the URL below to begin.\n");
			message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "?linkid=" + unBrandedprop.getLinkID() + "&pghead=" + unBrandedprop.getHtmlPgHead() + "&pgtitle=" + unBrandedprop.getHtmlPgTitle() + "\n\n"));

			if (reply != null && reply.length() > 0) {
				message.append("The following additional information has been provided from the approver.\n");
				message.append("Approver: " + actor + "\n");
				message.append("Comments: " + reply + "\n\n");
			}

			message.append(getRequestInfoTextApprove(u, ar.getProjName(), sMgrEmail));
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(ar.getMgrEMail(), u.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;

	}
	public static boolean sendAddrChkEMail(ETSUserDetails u, ETSAccessRequest ar, String project, String actor, String reply, String sMgrEmail) {

		boolean sent = false;
		String changeProfileURL = null;

		try {

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);

			try {

				ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
				changeProfileURL = resBundle.getString("etsChangeProfileURL");
			} catch (Exception ex) { /* ignore, leave blank */
				ex.printStackTrace();
				SysLog.log(SysLog.ERR, "*** ERROR ***", "Unable to find property: etsChangeProfileURL in gwa.properties. Setting blanlk. Please update this property.");
			}

			String subj = "Your access request for a workspace on " + unBrandedprop.getAppName() + " is on hold due to invalid address.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Your address in IBM user profile is either empty or is not valid. The workspace\n");
			message.append("owner wont be able to approve your request to the following workspace until you update\n");
			message.append("your profile.\n\n");
			message.append("Requested workspace: " + project + "\n\n");
			if (changeProfileURL != null) {
				message.append("Click on the following URL to update your IBM profile:\n");
				message.append(changeProfileURL + "\n\n");
			}

			message.append(getRequestInfoTextApprove(u, ar.getProjName(), sMgrEmail));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(ar.getMgrEMail(), u.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, ar.getMgrEMail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;
	}

	public static boolean sendRejectEMail(ETSUserDetails u, ETSAccessRequest ar, String project, String actor, String reply, String sMgrEmail) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);

			String subj = "Your access request for a workspace on " + unBrandedprop.getAppName() + " has not been approved.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("You access request for the following workspace has not been approved.\n\n");
			message.append("Requested workspace: " + project + "\n\n");
			if (reply != null && reply.length() > 0) {
				message.append("The following additional information has been provided from the approver.\n");
				message.append("Approver: " + actor + "\n");
				message.append("Comments: " + reply + "\n\n");
			}
			message.append(getRequestInfoTextApprove(u, ar.getProjName(), sMgrEmail));

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			sent = ETSUtils.sendEMail(ar.getMgrEMail(), u.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;
	}

	private static StringBuffer getRequestInfoText(ETSUserDetails u, String project, String sMgrEmail) {
		return getRequestInfoText(u, project, sMgrEmail, "");
	}

	private static StringBuffer getRequestInfoText(ETSUserDetails u, String project, String sMgrEmail, String comments) {
		StringBuffer message = new StringBuffer();

		message.append("Request Information\n");
		message.append("User ID:                    " + u.getWebId() + "\n");
		message.append("User Name:                  " + u.getFirstName() + " " + u.getLastName() + "\n");
		message.append("IBM Contact e-mail:         " + sMgrEmail + "\n");
		message.append("Project/Proposal Requested: " + project + "\n");
		message.append("User email:                 " + u.getEMail() + "\n");
		message.append("User Company:               " + u.getCompany() + "\n");
		message.append("User Address:               " + u.getStreetAddress() + "\n");
		if (!comments.equals("")) {
			message.append("Additional Comments:        " + comments + "\n");
		}
		return message;
	}

	private static StringBuffer getRequestInfoTextApprove(ETSUserDetails u, String project, String sMgrEmail) {

		StringBuffer message = new StringBuffer();

		message.append("Request Information\n");
		message.append("User ID:                    " + u.getWebId() + "\n");
		message.append("User Name:                  " + u.getFirstName() + " " + u.getLastName() + "\n");
		message.append("Workspace owner e-mail:     " + sMgrEmail + "\n");
		message.append("Project/Proposal Requested: " + project + "\n");
		message.append("User email:                 " + u.getEMail() + "\n");
		message.append("User Company:               " + u.getCompany() + "\n");
		message.append("User Address:               " + u.getStreetAddress() + "\n");

		return message;

	}
	public static boolean sendAdminWkspcEMail(ETSUserDetails u, String project, String text, String comments) {

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			String subj = "New workspace request for " + project;

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("A new workspace has been requested by:\n");
			message.append("User name:      ").append(u.getFirstName()).append(" ").append(u.getLastName()).append("\n");
			message.append("User ID:        ").append(u.getWebId()).append("\n");
			message.append("User e-mail:    ").append(u.getEMail()).append("\n");
			message.append("User address:   ").append(u.getStreetAddress()).append("\n");

			String adminEMail = props.getAdminEmail();

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());
			message.append(text).append("\n\n");
			if (!comments.equals("")) {
				message.append("Additional comments from the requestor:\n").append(comments);
			}
			sent = ETSUtils.sendEMail(u.getEMail(), props.getAdminEmail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, u.getEMail());

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;
	}
	public static boolean sendFwdEmailToWo(ETSUserDetails u, String project, String fwdBy, String comments, String fwdEmail, String woEmail) {
		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(project);

			String subj = u.getFirstName() + " " + u.getLastName() + " request to " + unBrandedprop.getAppName() + "";

			StringBuffer message = new StringBuffer();
			message.append("Hello,");
			message.append("\n");
			message.append("You have been identified as the owner of an " + unBrandedprop.getAppName() + " workspace.\n");
			message.append("A member of the workspace has forwarded this request to you for your approval.\n");
			message.append("\n");
			message.append("1. View the request information below.\n");
			message.append("2. Accept or reject the request by going to " + unBrandedprop.getAppName() + "\n");
			message.append(oem.edge.common.Global.getUrl("ets/" + unBrandedprop.getLandingPageURL() + "")).append("\n\n");
			message.append(getRequestInfoTextForWO(u, project, fwdBy, comments));
			message.append("\n\nThank you for your attention to this matter.");

			SysLog.log(SysLog.DEBUG, message, "email message: " + message.toString());

			String from = fwdEmail; // fwd by email
			String to = woEmail; // wo email

			sent = ETSUtils.sendEMail(from, to, "", oem.edge.common.Global.mailHost, message.toString(), subj, from);

		} catch (Exception ex) {
			ex.printStackTrace();
			SysLog.log(SysLog.ERR, "*** ERROR ***", ex);
		}
		return sent;

	}
	private static StringBuffer getRequestInfoTextForWO(ETSUserDetails u, String project, String sMgrEmail, String comments) {
		StringBuffer message = new StringBuffer();

		message.append("Request Information\n");
		message.append("Forwaded by:                " + sMgrEmail + "\n");
		message.append("Requested Project/Proposal: " + project + "\n");
		message.append("User Name:                  " + u.getFirstName() + " " + u.getLastName() + "\n");
		message.append("User ID:                    " + u.getWebId() + "\n");
		message.append("User email:                 " + u.getEMail() + "\n");
		message.append("User Address:               " + u.getStreetAddress() + "\n");
		message.append("User Company:               " + u.getCompany() + "\n");
		if (!comments.equals("")) {
			message.append("Additional Comments:        " + comments + "\n");
		}
		return message;
	}

	public static boolean sendMailToReqstorInvOnEntReqst(ETSUserDetails userDets, ETSUserDetails reqDets, ETSProj etsProj, String entitlement) {

		StringBuffer message = new StringBuffer();

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(etsProj.getProjectId());

			String subj = "User access for invited user for a workspace on " + unBrandedprop.getAppName() + ".";

			message.append("\n");
			message.append("The following user has been requested the required entitlement to access workspace. Here are the details:\n\n");
			message.append("The user will be able to access the workspace, once the entitlement request has been approved.\n\n");
			message.append("Request Information\n");
			message.append("Requested workspace:        " + etsProj.getName() + "\n\n");
			message.append("Requested by:               " + reqDets.getEMail() + "\n");
			message.append("Requested Entitlement:      " + entitlement + "\n");
			message.append("User Name:                  " + userDets.getFirstName() + " " + userDets.getLastName() + "\n");
			message.append("User ID:                    " + userDets.getWebId() + "\n");
			message.append("User email:                 " + userDets.getEMail() + "\n");
			message.append("User Address:               " + userDets.getStreetAddress() + "\n");
			message.append("User Company:               " + userDets.getCompany() + "\n");

			logger.debug("sendMailToReqstorInvOnEntReqst::email message::: " + message.toString());

			sent = ETSUtils.sendEMail(props.getAdminEmail(), reqDets.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, props.getAdminEmail());

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("*** ERROR ***", ex);
		}
		return sent;

	}

	public static boolean sendMailToReqstorInvOnAccessToWrkSpc(ETSUserDetails ownerDets, ETSUserDetails userDets, ETSUserDetails reqDets, ETSProj etsProj, String entitlement) {

		// This email is sent when an error occurs while adding user.
		StringBuffer message = new StringBuffer();

		boolean sent = false;

		try {

			ETSProperties props = new ETSProperties();

			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(etsProj.getProjectId());

			String subj = "User access for invited user for a workspace on " + unBrandedprop.getAppName() + ".";

			message.append("\n");
			message.append("The following user has requested to access workspace - " + etsProj.getName() + ". There is an error while adding user to the workspace. ");
			message.append("Please delete the existing request for the user and try adding the user again from team tab. Please contact workspace owner or admin.");
			message.append("Here are the details:\n\n");
			message.append("Request Information\n");
			message.append("Requested workspace:        " + etsProj.getName() + "\n\n");
			message.append("Requested by:               " + reqDets.getEMail() + "\n");
			message.append("User Name:                  " + userDets.getFirstName() + " " + userDets.getLastName() + "\n");
			message.append("User ID:                    " + userDets.getWebId() + "\n");
			message.append("User email:                 " + userDets.getEMail() + "\n");
			message.append("User Address:               " + userDets.getStreetAddress() + "\n");
			message.append("User Company:               " + userDets.getCompany() + "\n");

			logger.debug("sendMailToReqstorInvOnAccess::email message::: " + message.toString());

			sent = ETSUtils.sendEMail(ownerDets.getEMail(), reqDets.getEMail(), "", oem.edge.common.Global.mailHost, message.toString(), subj, ownerDets.getEMail());

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("*** ERROR ***", ex);
		}
		return sent;

	}

} //end of class
