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
package oem.edge.ets.fe.teamgroup;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author vishal
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GroupsMailHandler {

	private static Log logger = EtsLogger.getLogger(GroupsMailHandler.class);
	public static final String VERSION = "1.1";

	/**
	 *
	 */
	public GroupsMailHandler() {
	}


	public  boolean sendMailToGOOnEditGrp(ETSProj Project, ETSGroup grp, ETSUserDetails editor) {

		boolean sent = false;

		try {
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(Project.getProjectType());
		    String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }
			
			String mailSubj = "Group has been edited in an IBM " + unBrandedprop.getAppName() + " workspace.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("The following group properties have been updated for a group you own in an IBM "+ strCustConnect + unBrandedprop.getAppName() + ":\n\n");
			message.append("Workspace    : " + Project.getName() + "\n");
			message.append("Group Name   : " + grp.getGroupName() + "\n");
			message.append("Edited by    : " + editor.getLastName() + "," + editor.getFirstName() + "\n\n");

			message.append("No further action is required. If you do not agree with this edit,\n");
			message.append("you can click on the link below, log-in, and edit the group. You may\n");
			message.append("also want to appropriately contact the person who made the edit.\n\n");
			
			int intTcId = ETSDatabaseManager.getTopCatId(Project.getProjectId(),Defines.TEAM_VT); 
			message.append(oem.edge.common.Global.getUrl("ets/displayGroupList.wss?linkid=" 
					+ unBrandedprop.getLinkID() + "&proj=" + Project.getProjectId() 
					+ "&tc=" + intTcId + "&cc=" + intTcId + "\n\n"));
			
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
			
			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(editor.getEMail());
			mail.setReplyTo(editor.getEMail());
			mail.setTo(grp.getGroupOwner());
			mail.setSubject(mailSubj);
			mail.setMessage(message.toString());

			sent = ETSUtils.sendEmail(mail);

		} catch (Exception ex) {
			logger.error("General Exception in sendMailToGOOnEditGrp@ GroupsMailHandler", ex);
			ex.printStackTrace();

		}
		return sent;

	}

	public  boolean sendMailToGOOnChangeGrpOwner(ETSProj Project, ETSGroup grp, ETSUserDetails editor, String strNewGO) {

		boolean sent = false;

		try {
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(Project.getProjectType());
		    String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			String mailSubj = "Group owner has been changed in an IBM " + unBrandedprop.getAppName() + " workspace.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n");
			message.append("The following group properties have been updated for a group you own in an IBM " + strCustConnect + unBrandedprop.getAppName() + ":\n\n");
			message.append("Workspace    : " + Project.getName() + "\n\n");
			message.append("Group Name   : " + grp.getGroupName() + "\n\n");
			message.append("Edited by    : " + editor.getLastName() + "," + editor.getFirstName() + "\n");
			message.append("Last Group Owner : " + grp.getUserName() + "\n\n");
			message.append("New Group Owner  : " + strNewGO + "\n\n");
			
			message.append("No further action is required. To access this particular workspace, click on the below URL and log-in:\n\n");
			
			message.append(oem.edge.common.Global.getUrl("ets/ETSProjectsServlet.wss?linkid=" 
					+ unBrandedprop.getLinkID() + "&proj=" + Project.getProjectId() + "\n\n"));
			
			
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
			
			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(editor.getEMail());
			mail.setReplyTo(editor.getEMail());
			mail.setTo(grp.getGroupOwner());
			mail.setSubject(mailSubj);
			mail.setMessage(message.toString());

			sent = ETSUtils.sendEmail(mail);

		} catch (Exception ex) {
			logger.error("General Exception in sendMailToGOOnChangeGrpOwner@ GroupsMailHandler", ex);
			ex.printStackTrace();

		}
		return sent;

	}

	public  boolean sendMailToGOOnDelGroup(ETSProj Project, ETSGroup grp, ETSUserDetails editor) {

		boolean sent = false;

		try {
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(Project.getProjectType());
		    String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }

			String mailSubj = "Group has been removed in an IBM " + unBrandedprop.getAppName() + " workspace.";

			StringBuffer message = new StringBuffer();
			message.append("\n");
			message.append("Hello,\n\n");
			message.append("The following group that you own has been deleted in an IBM " + strCustConnect + unBrandedprop.getAppName() + ":\n\n");
			message.append("Workspace    : " + Project.getName() + "\n\n");
			message.append("Group Name   : " + grp.getGroupName() + "\n\n");
			message.append("Deleted by    : " + editor.getLastName() + "," + editor.getFirstName() + "\n");
			
			message.append("No further action is required. To access this particular workspace, click on the below URL and log-in:\n\n");
			
			message.append(oem.edge.common.Global.getUrl("ets/ETSProjectsServlet.wss?linkid=" 
					+ unBrandedprop.getLinkID() + "&proj=" + Project.getProjectId() + "\n\n"));
			
			
			message.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
			
			ETSMail mail = new ETSMail();
			mail.setBcc("");
			mail.setCc("");
			mail.setFrom(editor.getEMail());
			mail.setReplyTo(editor.getEMail());
			mail.setTo(grp.getGroupOwner());
			mail.setSubject(mailSubj);
			mail.setMessage(message.toString());

			sent = ETSUtils.sendEmail(mail);

		} catch (Exception ex) {
			logger.error("General Exception in sendMailToGOOnDelGroup@ GroupsMailHandler", ex);
			ex.printStackTrace();

		}
		return sent;

	}

} //end of class
