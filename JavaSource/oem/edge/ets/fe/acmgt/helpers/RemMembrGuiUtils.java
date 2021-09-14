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

package oem.edge.ets.fe.acmgt.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.UserObject;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.acmgt.model.RemoveMembrModel;
import oem.edge.ets.fe.clientvoice.CVMemberDetail;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSTask;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssCommonGuiUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterRepTabBean;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemMembrGuiUtils {

	private EtsIssFilterGuiUtils filterUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	private static Log logger = EtsLogger.getLogger(RemMembrGuiUtils.class);

	public static final String VERSION = "1.3";

	/**
	 * 
	 */
	public RemMembrGuiUtils() {
		super();
		filterUtils = new EtsIssFilterGuiUtils();
		comGuiUtils = new EtsIssCommonGuiUtils();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param userObj
	 * @param subWsList
	 * @return
	 */

		public String printSubWSList(EtsIssFilterObjectKey issfilterkey,UserObject userObj,ArrayList subWsList) {

			StringBuffer sbsub = new StringBuffer();
			String msgStr = "Please remove this user from the above subworkspace(s) before removing from current workspace.";
			
			String etsprojid = issfilterkey.getProjectId();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
		    String cc = issfilterkey.getCc();
		       
				sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sbsub.append("<tr>");
				sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">The user, <b>"+ userObj.gUSER_FULLNAME  + " [" + userObj.gIR_USERN + "]" + "</b>, already exists in the following subworkspace(s) associated with this workspace.</td>\n");
				sbsub.append("</tr>\n");
				sbsub.append("<tr><td>&nbsp;</td></tr>\n");
				sbsub.append("<tr>");
				sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\"><b>Subworkspace(s):&nbsp;</b>");
				for(int i=0; i<subWsList.size(); i++){
					if(i == 0){
						sbsub.append(subWsList.get(i)+"</td></tr>\n");
					}else{
						sbsub.append("<tr><td>&nbsp;</td></tr>\n");
						sbsub.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+ subWsList.get(i) +"</td></tr>\n");		
					}
				}
				sbsub.append("<tr><td>&nbsp;</td></tr>\n");
				sbsub.append("<tr>");
				sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">" + msgStr + "</td>\n");
				sbsub.append("</tr>");
				sbsub.append("<tr><td>&nbsp;</td></tr>\n");
				sbsub.append("</table>\n");
				
			    sbsub.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
				sbsub.append("<tr><td ><img src=\"" + Defines.V11_IMAGE_ROOT + "/rules/gray_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td></tr>");
				sbsub.append("</table> ");
				
				sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sbsub.append("<tr><td>&nbsp;</td></tr>\n");
				sbsub.append("<tr>");
				sbsub.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
				sbsub.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&cc=" + cc + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
				sbsub.append("</td> ");
				sbsub.append("<td  align=\"left\" valign=\"top\">");
				sbsub.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&cc=" + cc + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
				sbsub.append("</td> ");
				sbsub.append("</tr>");
				sbsub.append("</table>\n");
			
			return sbsub.toString();

		}

		/**
		 * 
		 * @return
		 */

	public String printPendingIssues(EtsIssFilterObjectKey issfilterkey, ArrayList issueRepTabList, ArrayList userList, ArrayList prevUserList) {

		StringBuffer sb = new StringBuffer();

		logger.debug("enter pending issues");

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (issueRepTabList != null && !issueRepTabList.isEmpty()) {

				repsize = issueRepTabList.size();

			}

			logger.debug("issue size==" + repsize);

			String issueProblemId = "";
			String issueCqTrkId = "";
			String issueTitle = "";
			String issueType = "";
			String issueSeverity = "";
			String issueStatus = "";
			String issueSubmitter = "";
			String issueSubmitterName = "";
			String issueLastTime = "";
			String issueCurOwnerName = "";
			String issueCurOwnerId = "";
			String refId = "";

			String bgcolor = "background-color:#eeeeee";

			String tmpIssueProblemId = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";
			int count = 0;

			if (repsize > 0) {

				sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");
				sb.append("<tr><td><b>Pending issues for this user</b></td></tr>\n");
				sb.append("<tr><td>&nbsp;</td></tr>\n");
				sb.append("<tr><td>Following issues are owned/submitted by this user.</td></tr>\n");
				sb.append("</table>\n");

				sb.append(comGuiUtils.printGreyLine());

				sb.append("<br />");

				sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");

				sb.append(printIssuesHeader(issfilterkey));

				for (int r = 0; r < repsize; r++) {

					EtsIssFilterRepTabBean etsreptab = (EtsIssFilterRepTabBean) issueRepTabList.get(r);

					issueProblemId = etsreptab.getIssueProblemId();
					issueCqTrkId = etsreptab.getIssueCqTrkId();
					refId = etsreptab.getRefId();
					issueTitle = etsreptab.getIssueTitle();
					issueType = etsreptab.getIssueType();
					issueSeverity = etsreptab.getIssueSeverity();
					issueStatus = etsreptab.getIssueStatus();
					issueSubmitter = etsreptab.getIssueSubmitter();
					issueSubmitterName = etsreptab.getIssueSubmitterName();
					issueLastTime = etsreptab.getIssueLastTime();
					issueCurOwnerName = etsreptab.getCurrentOwnerName();
					issueCurOwnerId = etsreptab.getCurrentOwnerId();

					if (tmpIssueProblemId.equals(issueProblemId)) {

						chgcolor = false;

					} else {

						chgcolor = true;

					}

					if (!chgcolor) {

						bgcolor = lastTmpRowColor;

					}

					if (bgcolor.equals("background-color:#eeeeee")) {

						sb.append("<tr style=" + bgcolor + ">");

					} else {

						sb.append("<tr>\n");

					}

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + refId + "</span></td>");

					sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"100\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueTitle + "</span></td>");

					sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					if (usrRolesModel.isShowOwnerName()) {

						sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + filterUtils.printUserInfoPopup(issueCurOwnerId, issueCurOwnerName, "cown") + "</span></td>");

					}

					sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					//donot show submitter and owner details if proj is of blade type and customer is external

					sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + filterUtils.printUserInfoPopup(issueSubmitter, issueSubmitterName, "sub") + "</span></td>");
					sb.append("<td headers=\"issid6\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid7\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"85\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueStatus + "</span></td>");

					sb.append("</tr>");

					lastTmpRowColor = bgcolor;

					if (bgcolor.equals("background-color:#eeeeee")) {

						bgcolor = "";

					} else {

						bgcolor = "background-color:#eeeeee";

					}

					tmpIssueProblemId = issueProblemId;
					count++;

				}

				sb.append("</tbody>");
				sb.append("</table>");

				sb.append("<br />");

				String msgStr = "Select the new owner/submitter for these issue(s):";

				sb.append(printUserList(userList, prevUserList, msgStr, "issueuser"));

			} //only if repsize > 0

		} catch (Exception ex) {

			logger.error("Exception printing pending issue recs", ex);
			ex.printStackTrace();

		} finally {

		}

		return sb.toString();

	}

	/**
		 * 
		 * prints sort header
		 * @param issviewrep
		 * @param etsIssObjKey
		 * @return
		 */

	public String printIssuesHeader(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		///
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		sb.append("<tr>\n");
		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");
		sb.append("<span class=\"small\">Issue ID</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		////title
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"100\"> \n");
		sb.append("<span class=\"small\">Title</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		//show issue owner

		if (usrRolesModel.isShowOwnerName()) {

			sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"120\"> \n");
			sb.append("<span class=\"small\">Owner</span></th>\n");
			sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		} //show issue owner

		sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Submitter</span></th>\n");
		sb.append("<th id=\"issid6\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		sb.append("<th id=\"issid7\" align=\"left\" valign=\"top\" width=\"85\" nowrap=\"nowrap\"> \n");
		sb.append("<span class=\"small\">Status</span></th>\n");
		sb.append("</tr> \n");

		return sb.toString();

	}

	/**
		 * 
		 * @param userList
		 * @param prevUserList
		 * @param propMsg
		 * @param taskName
		 * @return
		 */

	public String printUserList(ArrayList userList, ArrayList prevUserList, String propMsg, String taskName) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr>");
		sbsub.append("<td   width=\"150\" height=\"18\" ><label for=\"pdesc\">" + propMsg + "</label></td>\n");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"450\">\n");
		sbsub.append("<select id=\"pdesc\"  size=\"1\" name=\"" + taskName + "\"  align=\"left\" class=\"iform\" style=\"width:450px\" width=\"450px\"  >\n");
		sbsub.append(filterUtils.printSelectOptionsWithValue(userList, prevUserList));
		sbsub.append("</select>\n");
		sbsub.append("</td>\n");

		sbsub.append("</table>\n");

		return sbsub.toString();

	}

	/**
			 * 
			 * prints sort header
			 * @param issviewrep
			 * @param etsIssObjKey
			 * @return
			 */

	public String printTasksHeader() {

		StringBuffer sb = new StringBuffer();

		sb.append("<tr>\n");
		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"50\"> \n");
		sb.append("<span class=\"small\">Task no</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");
		////title
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"100\"> \n");
		sb.append("<span class=\"small\">Title</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		//show issue owner

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Owner</span></th>\n");
		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Submitter</span></th>\n");
		sb.append("<th id=\"issid6\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		sb.append("<th id=\"issid7\" align=\"left\" valign=\"top\" width=\"85\" nowrap=\"nowrap\"> \n");
		sb.append("<span class=\"small\">Status</span></th>\n");
		sb.append("</tr> \n");

		return sb.toString();

	}

	/**
			 * 
			 * @return
			 */

	public String printPendingTasks(EtsIssFilterObjectKey issfilterkey, ArrayList taskRepTabList, ArrayList userList, ArrayList prevUserList) {

		StringBuffer sb = new StringBuffer();

		logger.debug("enter pending tasks");

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (taskRepTabList != null && !taskRepTabList.isEmpty()) {

				repsize = taskRepTabList.size();

			}

			logger.debug("tasks size==" + repsize);

			int taskId = 0;
			String taskTitle = "";
			String taskStatus = "";
			String taskCreatorId = "";
			String taskCreatorName = "";
			String taskOwnerName = "";
			String taskOwnerId = "";

			String bgcolor = "background-color:#eeeeee";

			int tmpTaskId = 0;
			boolean chgcolor = true;
			String lastTmpRowColor = "";
			int count = 0;

			if (repsize > 0) {

				sb.append("<table summary=\"Tasks info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");
				sb.append("<tr><td><b>Pending tasks for this user</b></td></tr>\n");
				sb.append("<tr><td>&nbsp;</td></tr>\n");
				sb.append("<tr><td>Following tasks are owned/submitted by this user.</td></tr>\n");
				sb.append("</table>\n");

				sb.append(comGuiUtils.printGreyLine());

				sb.append("<br />");

				sb.append("<table summary=\"Tasks info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");

				sb.append(printTasksHeader());

				for (int r = 0; r < repsize; r++) {

					ETSTask udTask = (ETSTask) taskRepTabList.get(r);

					taskId = udTask.getId();
					taskTitle = udTask.getTitle();
					taskStatus = udTask.getStatus();
					taskCreatorId = udTask.getCreatorId();
					taskCreatorName = udTask.getCreatorName();

					taskOwnerName = udTask.getOwnerName();
					taskOwnerId = udTask.getOwnerId();

					if (tmpTaskId == taskId) {

						chgcolor = false;

					} else {

						chgcolor = true;

					}

					if (!chgcolor) {

						bgcolor = lastTmpRowColor;

					}

					if (bgcolor.equals("background-color:#eeeeee")) {

						sb.append("<tr style=" + bgcolor + ">");

					} else {

						sb.append("<tr>\n");

					}

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"50\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + taskId + "</span></td>");

					sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"100\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + taskTitle + "</span></td>");

					sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					if (usrRolesModel.isShowOwnerName()) {

						sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + filterUtils.printUserInfoPopup(taskOwnerId, taskOwnerName, "cown") + "</span></td>");

					}

					sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					//donot show submitter and owner details if proj is of blade type and customer is external

					sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + filterUtils.printUserInfoPopup(taskCreatorId, taskCreatorName, "sub") + "</span></td>");
					sb.append("<td headers=\"issid6\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid7\" nowrap=\"nowrap\" align=\"left\" valign=\"top\" width=\"85\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + taskStatus + "</span></td>");

					sb.append("</tr>");

					lastTmpRowColor = bgcolor;

					if (bgcolor.equals("background-color:#eeeeee")) {

						bgcolor = "";

					} else {

						bgcolor = "background-color:#eeeeee";

					}

					tmpTaskId = taskId;
					count++;

				}

				sb.append("</tbody>");
				sb.append("</table>");

				sb.append("<br />");

				String msgStr = "Select the new owner/submitter for these task(s):";

				sb.append(printUserList(userList, prevUserList, msgStr, "taskuser"));

			} //only if repsize > 0

		} catch (Exception ex) {

			logger.error("Exception printing pending issue recs", ex);
			ex.printStackTrace();

		} finally {

		}

		return sb.toString();

	}

	//client

	/**
				 * 
				 * prints sort header
				 * @param issviewrep
				 * @param etsIssObjKey
				 * @return
				 */

	public String printClientHeader() {

		StringBuffer sb = new StringBuffer();

		sb.append("<tr>\n");

		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Client voice</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Type</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"120\"> \n");
		sb.append("<span class=\"small\">Step</span></th>\n");
		sb.append("</tr> \n");

		return sb.toString();

	}

	/**
			 * 
			 * @return
			 */

	public String printPendingClients(EtsIssFilterObjectKey issfilterkey, ArrayList clientRepTabList, ArrayList userList, ArrayList prevUserList) {

		StringBuffer sb = new StringBuffer();

		logger.debug("enter pending clients");

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			int srt = issfilterkey.getSortState();

			int repsize = 0;

			if (clientRepTabList != null && !clientRepTabList.isEmpty()) {

				repsize = clientRepTabList.size();

			}

			logger.debug("clients size==" + repsize);

			String cvName = "";
			String cvType = "";
			String cvStep = "";

			String bgcolor = "background-color:#eeeeee";

			String tmpCvName = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";
			int count = 0;

			if (repsize > 0) {

				sb.append("<table summary=\"Client info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");
				sb.append("<tr><td><b>Pending Client Voice steps for this user</b></td></tr>\n");
				sb.append("<tr><td>&nbsp;</td></tr>\n");
				sb.append("<tr><td>Following steps are owned by this user.</td></tr>\n");
				sb.append("</table>\n");

				sb.append(comGuiUtils.printGreyLine());

				sb.append("<br />");

				sb.append("<table summary=\"Client info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">\n");
				sb.append("<tbody>\n");

				sb.append(printClientHeader());

				for (int r = 0; r < repsize; r++) {

					CVMemberDetail cvDetail = (CVMemberDetail) clientRepTabList.get(r);

					cvName = cvDetail.getName();
					cvType = cvDetail.getType();
					cvStep = cvDetail.getStep();

					if (tmpCvName.equals(cvName)) {

						chgcolor = false;

					} else {

						chgcolor = true;

					}

					if (!chgcolor) {

						bgcolor = lastTmpRowColor;

					}

					if (bgcolor.equals("background-color:#eeeeee")) {

						sb.append("<tr style=" + bgcolor + ">");

					} else {

						sb.append("<tr>\n");

					}

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + cvName + "</span></td>");

					sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + cvType + "</span></td>");

					sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

					sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"120\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + cvStep + "</span></td>");

					sb.append("</tr>");

					lastTmpRowColor = bgcolor;

					if (bgcolor.equals("background-color:#eeeeee")) {

						bgcolor = "";

					} else {

						bgcolor = "background-color:#eeeeee";

					}

					tmpCvName = cvName;
					count++;

				}

				sb.append("</tbody>");
				sb.append("</table>");

				sb.append("<br />");

				String msgStr = "Select the new owner/submitter for these step(s):";

				sb.append(printUserList(userList, prevUserList, msgStr, "clientuser"));

			} //only if repsize > 0

		} catch (Exception ex) {

			logger.error("Exception printing pending issue recs", ex);
			ex.printStackTrace();

		} finally {

		}

		return sb.toString();

	}

	/**
		 * 
		 * @param userList
		 * @param prevUserList
		 * @param propMsg
		 * @param taskName
		 * @return
		 */

	public String printUserDets(UserObject userObj) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr>");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"50\"><b>User:</b></td>\n");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"450\">" + userObj.gIR_USERN + " (" + userObj.gUSER_FULLNAME + ")" + "</td>");
		sbsub.append("</tr>\n");
		sbsub.append("</table>\n");

		return sbsub.toString();

	}

	/**
	 * 
	 * @param userList
	 * @param prevUserList
	 * @return
	 */

	public String printPrimaryContact(ArrayList userList, ArrayList prevUserList,String primaryContact) {

		StringBuffer sbsub = new StringBuffer();

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();

		if (primaryContact.equals("Y")) {

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sbsub.append("<tr>");
			sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">The user you are trying to remove is the primary contact for this workspace.</td>\n");
			sbsub.append("</tr>\n");
			sbsub.append("</table>\n");

			sbsub.append("<br />\n");

			String msgStr = "Please assign new primary contact:";

			sbsub.append(printUserList(userList, prevUserList, msgStr, "primuser"));

		}

		return sbsub.toString();

	}

	/**
	 * 
	 * @param issfilterkey
	 * @param nextpg
	 * @return
	 */

	public String printCommonHidVars(EtsIssFilterObjectKey issfilterkey, String nextpg) {

		StringBuffer buf = new StringBuffer();

		String etsprojid = issfilterkey.getProjectId();

		String tc = issfilterkey.getTc();

		String uid = AmtCommonUtils.getTrimStr((String) issfilterkey.getParams().get("uid"));

		buf.append("<input type=\"hidden\" name=\"proj\" value=\"" + etsprojid + "\" />");
		buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + tc + "\" />");
		buf.append("<input type=\"hidden\" name=\"action\" value=\"delmember\" />");
		buf.append("<input type=\"hidden\" name=\"delmember\" value=\"sub\" />");
		buf.append("<input type=\"hidden\" name=\"nextpg\" value=\"" + nextpg + "\" />");
		buf.append("<input type=\"hidden\" name=\"uid\" value=\"" + uid + "\" />");
		buf.append("<input type=\"hidden\" name=\"linkid\" value=\"" + issfilterkey.getLinkid() + "\" />");

		return buf.toString();

	}

	/**
	 * 
	 * @param remModel
	 * @param issfilterkey
	 * @return
	 */

	public String printErrMsg(RemoveMembrModel remModel, EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbsub = new StringBuffer();

		String etsprojid = issfilterkey.getProjectId();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();

		//for all sub app msg
		StringBuffer subAppMsg = new StringBuffer();
		//total result msg
		StringBuffer remMsg = new StringBuffer();

		//get msgs

		if (!remModel.isChangePrimCntct()) {

			subAppMsg.append(remModel.getPrimCntctErrStr());
			subAppMsg.append("<br />");

		}

		if (!remModel.isChangeIssues()) {

			subAppMsg.append(remModel.getIssueErrMsgStr());
			subAppMsg.append("<br />");

		}

		if (!remModel.isChangeTasks()) {

			subAppMsg.append(remModel.getTaskErrMsgStr());
			subAppMsg.append("<br />");

		}

		if (!remModel.isChangeClients()) {

			subAppMsg.append(remModel.getClientErrMsgStr());
			subAppMsg.append("<br />");

		}

		remMsg.append(remModel.getRemoveMembrMsg());

		//show error msg

		if (AmtCommonUtils.isResourceDefined(subAppMsg.toString())) {

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sbsub.append("<tr>");
			sbsub.append("<td valign=\"top\" align=\"left\"  ><span style=\"color:#ff3333\">" + subAppMsg.toString() + "</span></td>\n");

			sbsub.append("</tr>\n");
			sbsub.append("</table>\n");

			sbsub.append("<br />\n");

		}

		//show reg msg
		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr>");
		sbsub.append("<td valign=\"top\" align=\"left\"  >" + remMsg.toString() + "</td>\n");
		sbsub.append("</tr>\n");
		sbsub.append("</table>\n");

		sbsub.append("<br />\n");

		sbsub.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sbsub.append("<tr>");
		sbsub.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		sbsub.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&cc=" + tc + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		sbsub.append("</td> ");
		sbsub.append("<td  align=\"left\" valign=\"top\">");
		sbsub.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&cc=" + tc + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		sbsub.append("</td> ");
		sbsub.append("</tr>");
		sbsub.append("</table> ");
		return sbsub.toString();
	}
	
	
	/**
			 * To print continue and cancel buttons
			 * @param contnName
			 * @param cancName
			 * @return
			 */

		public String printSubmitCancel(EtsIssFilterObjectKey issfilterkey,String contnName, String cancName) {

			StringBuffer sbview = new StringBuffer();
			///
			String etsprojid = issfilterkey.getProjectId();
						
						String tc = issfilterkey.getTc();
						String linkid = issfilterkey.getLinkid();
			////			
			

			sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
			sbview.append("<tr>\n");
			sbview.append("<td width=\"130\" align=\"left\">\n");
			sbview.append(comGuiUtils.printSubmit(contnName));
			sbview.append("</td>\n");
			sbview.append("<td align=\"left\">\n");
			//sbview.append(printCancel(cancName));
			sbview.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			sbview.append("<td  align=\"right\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + etsprojid + "&tc=" + tc + "&cc=" + tc + "&linkid=" + linkid + "\" class=\"fbox\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a>&nbsp;</td>");
			sbview.append("<td  align=\"left\"><a href=\"ETSProjectsServlet.wss?action=delmember&proj=" + etsprojid + "&tc=" + tc + "&cc=" + tc + "&linkid=" + linkid + "\" class=\"fbox\">Cancel</a></td>");
			sbview.append("</tr></table>");
			sbview.append("</td>\n");
			sbview.append("</tr>\n");
			sbview.append("</table>\n");

			return sbview.toString();
		}


} //end of class
