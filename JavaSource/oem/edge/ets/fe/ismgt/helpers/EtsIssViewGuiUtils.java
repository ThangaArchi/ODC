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

package oem.edge.ets.fe.ismgt.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUserRoleFilter;
import oem.edge.ets.fe.ismgt.dao.EtsIssHistoryDAO;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssHistoryModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserActionsModel;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssViewGuiUtils implements EtsIssueConstants, EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.24.2.30";

	private EtsIssCommonGuiUtils comGuiUtils;
	private EtsIssFilterGuiUtils filGuiUtils;
	private EtsIssActionGuiUtils actGuiUtils;

	/**
	 * 
	 */
	public EtsIssViewGuiUtils() {
		super();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
		this.filGuiUtils = new EtsIssFilterGuiUtils();
		this.actGuiUtils = new EtsIssActionGuiUtils();

	}

	/**
			 * 
			 * 
			 * @param etsIssObjKey
			 * @param usr1InfoModel
			 * @return
			 */

	public String printChkIssTypeIbmOnlyForView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbsub = new StringBuffer();
		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());

		if (etsIssObjKey.getEs().gDECAFTYPE.equals("I")) { //only for internals

			Global.println("issueAccess in print check Iss Type Ibm Only in View==" + issueAccess);

			//	print section header
			sbsub.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step4")));

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

			sbsub.append("<tr valign=\"top\" >");

			if (issueAccess.equals("IBM")) {

				sbsub.append("<td valign=\"top\" width=\"20%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"80%\" >IBM team member only</td>");

			} else {

				sbsub.append("<td valign=\"top\" width=\"20%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"80%\" >Any team member in workspace can access this issue.</td>");

			}
			sbsub.append("</tr>");
			//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");
			sbsub.append("</table>");

			sbsub.append("<br />");

		}

		return sbsub.toString();

	}

	/**
				 * 
				 * since there will be mutiple entries with same suer email in amt.users, the first name will be shown
				 * @param etsIssObjKey
				 * @param usr1InfoModel
				 * @param propMap
				 * @return
				 */

	public String printPrevNotifyListWithView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws SQLException, Exception {

		StringBuffer sbsub = new StringBuffer();

		//get roles model
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();

		String propMsg = "";

		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		//get the display members
		ArrayList projMemList = projMemDao.getProjMemberListFromEmailId(prevNotifyList, etsIssObjKey.getProj().getProjectId());

		int notsize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			notsize = projMemList.size();

		}

		sbsub.append(actGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step5")));
		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		propMsg = getDynNotifyListMsgForView(notsize / 2);
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
		sbsub.append(propMsg);
		sbsub.append("</td></tr>\n");
		sbsub.append("</table>\n");
		if (notsize > 0) {
			sbsub.append("<br />\n");
			sbsub.append(comGuiUtils.printGreyLine());
		}
		sbsub.append("<br />\n");

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		sbsub.append("<tr>\n");

		if (notsize > 0) {

			sbsub.append("<td  colspan=\"2\" height=\"18\" ><b>Notification list<b/></td></tr>\n");

		}

		for (int i = 0; i < notsize; i = i + 2) {

			sbsub.append("<tr valign=\"top\" >\n");
			sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">\n");
			sbsub.append(projMemList.get(i + 1));
			sbsub.append("</td>\n");

			sbsub.append("</tr>\n");

		}

		sbsub.append("</table>\n");

		if (!etsIssObjKey.isProjBladeType()) {

			return sbsub.toString();

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				return sbsub.toString();

			}
		}

		return "";

	}

	/**
		 * 
		 * @param notifycount
		 * @return
		 */

	public String getDynNotifyListMsgForView(int notifycount) {

		StringBuffer sbfile = new StringBuffer();

		if (notifycount == 0) {

			sbfile.append("Currently, there are no members on issue notification list.  ");
		}

		if (notifycount == 1) {

			sbfile.append("Currently, there is " + notifycount + " member on issue notification list. They will also be notified when any action is taken on this issue. ");
			//sbfile.append("Currently, there is " + notifycount + " member on issue notification list.  ");
		}

		if (notifycount > 1) {

			sbfile.append("Currently, there are " + notifycount + " members on issue notification list. They will also be notified when any action is taken on this issue. ");
			//sbfile.append("Currently, there are " + notifycount + " members on issue notification list. ");

		}

		return sbfile.toString();

	}

	/**
		 * To Print commentary log
		 * 
		 */

	public String printCommentaryLog(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		if (!issueSource.equals(ETSPMOSOURCE)) {

			return printGenCommentaryLog(etsIssObjKey, usr1InfoModel, propMap);

		} else {

			return printPMOIssueCommentaryLog(etsIssObjKey, usr1InfoModel, propMap);
		}

	}

	/**
	 * To Print commentary log
	 * 
	 */

	public String printGenCommentaryLog(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbview = new StringBuffer();

		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		String userLastAction = AmtCommonUtils.getTrimStr(usr1InfoModel.getLastAction());
		String comm_from_cust = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommFromCust());
		String comm_log_string = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommentLogStr());

		int usr_seq_no = usr1InfoModel.getUsr_seq_no();
		int cq_seq_no = usr1InfoModel.getCq_seq_no();

		Global.println("Last Modified Comments log string in View  Issue ==" + comm_from_cust);

		//		logs in new////

		if (!probState.equals("Submit")) {

			if (!AmtCommonUtils.isResourceDefined(comm_log_string)) {

				comm_log_string = "---Commentary log not initialized---";
				//comm_log_string = "";
			}

			if (usr_seq_no > cq_seq_no) {

				sbview.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"0\"  width=\"600\">");
				sbview.append("<tr>");
				sbview.append("<td valign=\"top\" width=\"30%\"><b>Last action being processed </b>:</td><td valign=\"top\" width=\"70%\">" + userLastAction + "</td>");
				sbview.append("</tr>");
				sbview.append("</table> \n");

				sbview.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"0\"  width=\"600\">");
				sbview.append("<tr>");
				sbview.append("<td valign=\"top\" width=\"30%\"> ");
				sbview.append("<b>Last comment</b>:");
				sbview.append("</td>");
				sbview.append("<td valign=\"top\" width=\"70%\">");
				sbview.append("<pre>");
				sbview.append(comm_from_cust);
				sbview.append("</pre>");
				sbview.append("</td>");
				sbview.append("</tr>");
				sbview.append("<tr>");
				sbview.append("<td><p /></td>");
				sbview.append("</tr>");
				sbview.append("</table> \n");

			}

			sbview.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
			sbview.append("	      <tr> \n");
			sbview.append("	        <td  valign=\"top\" align=\"left\">\n");
			sbview.append("	          <textarea id=\"commu1\" name=\"asic_comm_log\"  rows=\"" + COMMENT_ROWS + "\" cols=\"" + COMMENT_COLS + "\" class=\"iform\"  readonly=\"readonly\" />\n");
			sbview.append("" + comm_log_string + "</textarea>\n");
			sbview.append("	        </td>\n");
			sbview.append("      </tr> \n");
			sbview.append("    </table> \n");

		} else {

			sbview.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
			sbview.append("	      <tr> \n");
			sbview.append("	        <td  valign=\"top\" align=\"left\">\n");
			sbview.append("---Commentary log not initialized---");
			sbview.append("	        </td>\n");
			sbview.append("      </tr> \n");
			sbview.append("    </table> \n");

		}

		return sbview.toString();

	}

	/**
		 * To Print commentary log
		 * 
		 */

	public String printPMOIssueCommentaryLog(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbview = new StringBuffer();

		//get pmo id
		String pmoId = AmtCommonUtils.getTrimStr(usr1InfoModel.getCqTrkId());
		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		String userLastAction = AmtCommonUtils.getTrimStr(usr1InfoModel.getLastAction());
		String comm_from_cust = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommFromCust());
		String comm_log_string = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommentLogStr());
		String infoSrcFlag = AmtCommonUtils.getTrimStr(usr1InfoModel.getInfoSrcFlag());
		String txnStatusFlag = AmtCommonUtils.getTrimStr(usr1InfoModel.getTxnStatusFlag());

		Global.println("Last Modified Comments log string in View  Issue ==" + comm_from_cust);
		Global.println("PROBLEM STATE IN PMO ===" + probState);
		Global.println("USER LAST ACTION ===" + userLastAction);

		//		logs in new////

		if (!probState.equals("Submit")) {

			if (!AmtCommonUtils.isResourceDefined(comm_log_string)) {

				comm_log_string = "---Commentary log not initialized---";
				//comm_log_string = "";
			}

			if (AmtCommonUtils.isResourceDefined(pmoId)) {

				sbview.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"0\"  width=\"600\">");
				sbview.append("<tr>");
				sbview.append("<td valign=\"top\" width=\"30%\"><b>Last action being processed </b>:</td><td valign=\"top\" width=\"70%\">" + userLastAction + "</td>");
				sbview.append("</tr>");
				sbview.append("</table> \n");

				//52 fixpk
				//				sbview.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"0\"  width=\"600\">");
				//				sbview.append("<tr>");
				//				sbview.append("<td valign=\"top\" width=\"30%\"> ");
				//				sbview.append("<b>Last comment</b>:");
				//				sbview.append("</td>");
				//				sbview.append("<td valign=\"top\" width=\"70%\">");
				//				sbview.append("<pre>");
				//				sbview.append(comm_from_cust);
				//				sbview.append("</pre>");
				//				sbview.append("</td>");
				//				sbview.append("</tr>");
				//				sbview.append("<tr>");
				//				sbview.append("<td><p /></td>");
				//				sbview.append("</tr>");
				//				sbview.append("</table> \n");

			}

			sbview.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
			sbview.append("	      <tr> \n");
			sbview.append("	        <td  valign=\"top\" align=\"left\">\n");
			sbview.append("	          <textarea id=\"commu1\" name=\"asic_comm_log\"  rows=\"" + "10" + "\" cols=\"" + "70" + "\" class=\"iform\"  readonly=\"readonly\" />\n");
			sbview.append("" + comm_log_string + "</textarea>\n");
			sbview.append("	        </td>\n");
			sbview.append("      </tr> \n");
			sbview.append("    </table> \n");

		} else {

			sbview.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
			sbview.append("	      <tr> \n");
			sbview.append("	        <td  valign=\"top\" align=\"left\">\n");
			sbview.append("---Commentary log not initialized---");
			sbview.append("	        </td>\n");
			sbview.append("      </tr> \n");
			sbview.append("    </table> \n");

		}

		return sbview.toString();

	}

	public String printActionsInView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		boolean isProjBladeType = etsIssObjKey.getProj().isProjBladeType();

		if (!issueSource.equals(ETSPMOSOURCE)) {

			return printActionsInViewForGen(etsIssObjKey, usr1InfoModel, propMap);

		} else {

			return printActionsInViewForPMO(etsIssObjKey, usr1InfoModel, propMap);
		}

	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 * @throws Exception
	 */

	public String printActionsInViewForGen(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();

		String edgeProblemId = usr1InfoModel.getEdgeProblemId();

		boolean actionavailable = false;

		try {

			//get USER/ROLES HELPER
			EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();

			//get user/roles matrix
			EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(etsIssObjKey);

			//get user/actions matrix
			EtsIssUserActionsModel usrActionsModel = userFilter.getUserActionMatrix(usr1InfoModel, etsIssObjKey);

			String rejectMsg = (String) propMap.get("issue.act.view.msg3");

			String srt = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("srt"));

			//show actions any user in project, except with WORKSPACE_VISITOR entitlement//

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step7")));

				sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");

				if (!usrActionsModel.isActionavailable()) {

					String viewInProcessMsg = "";

					if (!usr1InfoModel.isIssueTypIdActive()) {

						viewInProcessMsg = (String) propMap.get("issues.act.view.isstype.inactive.msg");

					} else {

						viewInProcessMsg = (String) propMap.get("issues.act.view.inprocess.msg");

					}

					sbview.append("<tr> ");
					sbview.append("<td  valign=\"top\" width=\"600\">");
					sbview.append("Note : " + viewInProcessMsg + " ");
					sbview.append("</td> ");
					sbview.append("</tr> ");
					sbview.append("<tr> ");
					sbview.append("<td  valign=\"top\" width=\"600\">");
					sbview.append("</td> ");
					sbview.append("</tr> ");

				} else {

					//				if (probState.equals("Rejected")) {
					//
					//					sbview.append("<tr> ");
					//					sbview.append("<td  valign=\"top\" width=\"600\">");
					//					sbview.append(rejectMsg);
					//					sbview.append("</td> ");
					//					sbview.append("</tr> ");
					//					sbview.append("<tr><td></td>");
					//					sbview.append("</tr>");
					//					sbview.append("</table>");
					//					sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
					//
					//				}

					if (usrActionsModel.isUsrResolveIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=resolveIssue&op=500&srt="
								+ srt
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append(
							"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=resolveIssue&op=500&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Resolve</a></td>");
						sbview.append("</tr>");

					}

					if (usrActionsModel.isUsrModifyIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=modifyIssue&op=300&srt="
								+ srt
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=modifyIssue&op=300&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Modify</a></td>");
						sbview.append("</tr>");

					}

					//only submitter/work space manager can close/reject issues

					if (usrActionsModel.isUsrRejectIssue()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=rejectIssue&op=500&srt="
								+ srt
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=rejectIssue&op=500&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Reject</a></td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=closeIssue&op=500&srt="
								+ srt
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=closeIssue&op=500&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Close</a></td>");
						sbview.append("</tr>");

					}

					sbview.append("<tr>");
					sbview.append("<td>");
					sbview.append("</td>");
					sbview.append("</tr>");
					sbview.append("<tr>");
					sbview.append(
						"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
							+ etsIssObjKey.getProj().getProjectId()
							+ "&tc="
							+ etsIssObjKey.getTopCatId()
							+ "&linkid="
							+ etsIssObjKey.getSLink()
							+ "&actionType=commentIssue&op=500&srt="
							+ srt
							+ "&istyp="
							+ etsIssObjKey.getIstyp()
							+ "&flop="
							+ etsIssObjKey.getFilopn()
							+ ""
							+ "&edge_problem_id="
							+ edgeProblemId
							+ "\""
							+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
					sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentIssue&op=500&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Comment</a></td>");
					sbview.append("</tr>");

					///withdrawaction////
					if (usrActionsModel.isUsrWithDraw()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=withDrwIssue&op=500&srt="
								+ srt
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append(
							"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=withDrwIssue&op=500&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Withdraw</a></td>");
						sbview.append("</tr>");

					}

					String chgOwnerMsg = (String) propMap.get("issue.view.chgown.msg");

					//show only when need to be shown

					if (usrActionsModel.isUsrChangeOwner()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=chgOwner&coop=800&srt="
								+ srt
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ "&flop="
								+ etsIssObjKey.getFilopn()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append(
							"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=chgOwner&coop=800&srt=" + srt + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Change owner</a></td>");
						sbview.append("</tr>");

					} // is Show Owner true
					sbview.append("</table>");

					if (usrActionsModel.isUsrSubscribe()) {

						//if the user is subscribed to issue type, show the messg
						if (usr1InfoModel.isUsrIssTypSubscribe() && usr1InfoModel.isUsrIssueSubscribe()) {

							String subsIssTypeMsg = (String) propMap.get("issues.act.view.subs.isstype");

							sbview.append("<br />");
							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append("</td>");
							sbview.append("</tr>");
							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append(comGuiUtils.printWarningMsg(subsIssTypeMsg));
							sbview.append("</td>");
							sbview.append("</tr>");

							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append("</td>");
							sbview.append("</tr>");
							sbview.append("</table>");

							//
							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
							sbview.append("<tr>");
							sbview.append(
								"<td colspan=\"1\" width=\"18\"><a href=\"subsIssType.wss?proj="
									+ etsIssObjKey.getProj().getProjectId()
									+ "&tc="
									+ etsIssObjKey.getTopCatId()
									+ "&linkid="
									+ etsIssObjKey.getSLink()
									+ "&actionType=subsIssType&opn=1200&srt="
									+ srt
									+ "&istyp="
									+ "&istyp=iss"
									+ "\""
									+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
							sbview.append("<td align=\"left\"><a href=\"subsIssType.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=subsIssType&opn=1200&srt=" + srt + "&istyp=iss\">Unsubscribe to issue type</a></td>");
							sbview.append("</tr>");

							sbview.append("</table>");

						}

						//if user is not subscribed to issue type

						if (!usr1InfoModel.isUsrIssTypSubscribe()) {

							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");

							if (usr1InfoModel.isUsrIssueSubscribe()) {

								sbview.append("<tr>");
								sbview.append("<td>");
								sbview.append("</td>");
								sbview.append("</tr>");

								sbview.append("<tr>");
								sbview.append(
									"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
										+ etsIssObjKey.getProj().getProjectId()
										+ "&tc="
										+ etsIssObjKey.getTopCatId()
										+ "&linkid="
										+ etsIssObjKey.getSLink()
										+ "&actionType=subscrIssue&op=1300&srt="
										+ srt
										+ "&istyp="
										+ etsIssObjKey.getIstyp()
										+ "&flop="
										+ etsIssObjKey.getFilopn()
										+ ""
										+ "&edge_problem_id="
										+ edgeProblemId
										+ "\""
										+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
								sbview.append(
									"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj="
										+ etsIssObjKey.getProj().getProjectId()
										+ "&tc="
										+ etsIssObjKey.getTopCatId()
										+ "&linkid="
										+ etsIssObjKey.getSLink()
										+ "&actionType=subscrIssue&op=1300&srt="
										+ srt
										+ "&istyp="
										+ etsIssObjKey.getIstyp()
										+ "&flop="
										+ etsIssObjKey.getFilopn()
										+ "&edge_problem_id="
										+ edgeProblemId
										+ "\">Subscribe to this issue</a></td>");
								sbview.append("</tr>");

							} else {

								sbview.append("<tr>");
								sbview.append(
									"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
										+ etsIssObjKey.getProj().getProjectId()
										+ "&tc="
										+ etsIssObjKey.getTopCatId()
										+ "&linkid="
										+ etsIssObjKey.getSLink()
										+ "&actionType=unSubscrIssue&op=1301&srt="
										+ srt
										+ "&istyp="
										+ etsIssObjKey.getIstyp()
										+ "&flop="
										+ etsIssObjKey.getFilopn()
										+ ""
										+ "&edge_problem_id="
										+ edgeProblemId
										+ "\""
										+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
								sbview.append(
									"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj="
										+ etsIssObjKey.getProj().getProjectId()
										+ "&tc="
										+ etsIssObjKey.getTopCatId()
										+ "&linkid="
										+ etsIssObjKey.getSLink()
										+ "&actionType=unSubscrIssue&op=1301&srt="
										+ srt
										+ "&istyp="
										+ etsIssObjKey.getIstyp()
										+ "&flop="
										+ etsIssObjKey.getFilopn()
										+ "&edge_problem_id="
										+ edgeProblemId
										+ "\">Unsubscribe to this issue</a></td>");
								sbview.append("</tr>");

							} //unsubsc

							sbview.append("</table>");

						} //if the user is not subscribed to issue type

					} // if usr can subscribes

					//////////////1s

				}

				///end phani #1////////////////

				//sbview.append("</table>");

				sbview.append("<br />");

			} //show not to workspace visitor

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();

	}

	/**
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 * @throws Exception
		 */

	public String printActionsInViewForPMO(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();

		String edgeProblemId = usr1InfoModel.getEdgeProblemId();

		boolean actionavailable = false;

		try {

			//get USER/ROLES HELPER
			EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();

			//get user/roles matrix
			EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(etsIssObjKey);

			//get user/actions matrix
			EtsIssUserActionsModel usrActionsModel = userFilter.getUserActionMatrixForPMO(usr1InfoModel, etsIssObjKey);

			String rejectMsg = (String) propMap.get("issue.act.view.msg3");

			//show actions any user in project, except with WORKSPACE_VISITOR entitlement//

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step7")));

				sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");

				if (!usrActionsModel.isActionavailable()) {

					String viewInProcessMsg = (String) propMap.get("issues.act.view.inprocess.msg");

					sbview.append("<tr> ");
					sbview.append("<td  valign=\"top\" width=\"600\">");
					sbview.append("Note : " + viewInProcessMsg + " ");
					sbview.append("</td> ");
					sbview.append("</tr> ");

				} else {

					if (usrActionsModel.isUsrResolveIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=resolveIssue&op=500&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=resolveIssue&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Resolve</a></td>");
						sbview.append("</tr>");

					}

					//only submitter/work space manager can close/reject issues

					if (usrActionsModel.isUsrRejectIssue()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=rejectIssue&op=500&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=rejectIssue&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Reject</a></td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append(
							"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
								+ etsIssObjKey.getProj().getProjectId()
								+ "&tc="
								+ etsIssObjKey.getTopCatId()
								+ "&linkid="
								+ etsIssObjKey.getSLink()
								+ "&actionType=closeIssue&op=500&flop="
								+ etsIssObjKey.getFilopn()
								+ "&istyp="
								+ etsIssObjKey.getIstyp()
								+ ""
								+ "&edge_problem_id="
								+ edgeProblemId
								+ "\""
								+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=closeIssue&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Close</a></td>");
						sbview.append("</tr>");

					}

					sbview.append("<tr>");
					sbview.append("<td>");
					sbview.append("</td>");
					sbview.append("</tr>");
					sbview.append("<tr>");
					sbview.append(
						"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
							+ etsIssObjKey.getProj().getProjectId()
							+ "&tc="
							+ etsIssObjKey.getTopCatId()
							+ "&linkid="
							+ etsIssObjKey.getSLink()
							+ "&actionType=commentIssue&op=500&istyp="
							+ etsIssObjKey.getIstyp()
							+ "&flop="
							+ etsIssObjKey.getFilopn()
							+ ""
							+ "&edge_problem_id="
							+ edgeProblemId
							+ "\""
							+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
					sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentIssue&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&edge_problem_id=" + edgeProblemId + "\">Comment</a></td>");
					sbview.append("</tr>");

					//////////////1s

				}

				///end phani #1////////////////

				sbview.append("</table>");

				sbview.append("<br />");

			} //show not to workspace visitor

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();

	}

	/**
	 * 
	 * 
	 * @return
	 */

	public String printAnchorLinksForView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbview = new StringBuffer();

		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		int usr_seq_no = usr1InfoModel.getUsr_seq_no();
		int cq_seq_no = usr1InfoModel.getCq_seq_no();

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		String checkUserRole = ETSUtils.checkUserRole(etsIssObjKey.getEs(), etsIssObjKey.getProj().getProjectId());

		//check for history
		ArrayList histList = usr1InfoModel.getHistList();
		int histsize = 0;

		if (histList != null && !histList.isEmpty()) {

			histsize = histList.size();
		}

		sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
		sbview.append("<tr>");
		sbview.append("<td>");
		sbview.append("<a href=\"#0\">Description</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#1\">Identification</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#2\">Attachments</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#3\">Custom fields</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		if (etsIssObjKey.getEs().gDECAFTYPE.equals("I")) { //only for internals

			sbview.append("<a href=\"#4\">Security classification</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		}

		if (!etsIssObjKey.isProjBladeType()) {

			sbview.append("<a href=\"#5\">Notification list</a>&nbsp;&nbsp;|&nbsp;&nbsp;");

			if (histsize > 0) {

				sbview.append("<a href=\"#8\">History</a>&nbsp;&nbsp;|&nbsp;&nbsp;");

			}

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sbview.append("<a href=\"#5\">Notification list</a>&nbsp;&nbsp;|&nbsp;&nbsp;");

				if (histsize > 0) {

					sbview.append("<a href=\"#8\">History</a>&nbsp;&nbsp;|&nbsp;&nbsp;");

				}

			}

		}

		sbview.append("<a href=\"#6\">Commentary log</a>");
		//show actions
		//if (!probState.equals("New") && cq_seq_no > usr_seq_no) {

		if (!checkUserRole.equals(Defines.WORKSPACE_VISITOR) && !checkUserRole.equals(Defines.INVALID_USER)) {
			sbview.append("&nbsp;&nbsp;|&nbsp;&nbsp;<a href=\"#7\">Actions</a>");
		}

		//	}
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();
	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String printCustFieldsForView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws SQLException, Exception {

		StringBuffer sbview = new StringBuffer();
		int counter = 0;
		// custom  field c1..c8 vals
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";
		String prevFieldC8Val = "";
		
		// custom  field c1..c8 label
		String FieldC1Label = "";
		String FieldC2Label = "";
		String FieldC3Label = "";
		String FieldC4Label = "";
		String FieldC5Label = "";
		String FieldC6Label = "";
		String FieldC7Label = "";
		String FieldC8Label = "";

		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();
		ArrayList prevFieldC8ValList = usr1InfoModel.getPrevFieldC8List();


		//cust fields c1..c8

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList) && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC1DispName()  ) ) {
			prevFieldC1Val = (String) prevFieldC1ValList.get(0);
			FieldC1Label =  usr1InfoModel.getFieldC1DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC2DispName()  ) ) {
			prevFieldC2Val = (String) prevFieldC2ValList.get(0);
			FieldC2Label =  usr1InfoModel.getFieldC2DispName();			
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC3DispName()  ) ) {
			prevFieldC3Val = (String) prevFieldC3ValList.get(0);
			FieldC3Label =  usr1InfoModel.getFieldC3DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC4DispName()  ) ) {
			prevFieldC4Val = (String) prevFieldC4ValList.get(0);
			FieldC4Label =  usr1InfoModel.getFieldC4DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC5DispName()  ) ) {
			prevFieldC5Val = (String) prevFieldC5ValList.get(0);
			FieldC5Label =  usr1InfoModel.getFieldC5DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC6DispName()  ) ) {
			prevFieldC6Val = (String) prevFieldC6ValList.get(0);
			FieldC6Label =  usr1InfoModel.getFieldC6DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC7DispName()  ) ) {
			prevFieldC7Val = (String) prevFieldC7ValList.get(0);
			FieldC7Label =  usr1InfoModel.getFieldC7DispName();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC8ValList)  && AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC8DispName()  ) ) {
			prevFieldC8Val = (String) prevFieldC8ValList.get(0);
			FieldC8Label =  usr1InfoModel.getFieldC8DispName();
		}

		sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

		///cust fields vals
		if (AmtCommonUtils.isResourceDefined(prevFieldC1Val) && AmtCommonUtils.isResourceDefined(FieldC1Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC1Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC1Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC2Val) && AmtCommonUtils.isResourceDefined(FieldC2Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC2Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC2Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC3Val) && AmtCommonUtils.isResourceDefined(FieldC3Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC3Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC3Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC4Val) && AmtCommonUtils.isResourceDefined(FieldC4Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC4Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC4Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC5Val) && AmtCommonUtils.isResourceDefined(FieldC5Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC5Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC5Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC6Val) && AmtCommonUtils.isResourceDefined(FieldC6Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC6Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC6Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC7Val) && AmtCommonUtils.isResourceDefined(FieldC7Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC7Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC7Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC8Val) && AmtCommonUtils.isResourceDefined(FieldC8Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC8Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC8Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}		
		
	
		if(counter == 0 ) {
			sbview.append("<tr><td  valign=\"top\" width =\"100%\" align=\"left\"> \n");
			sbview.append("Currently, there are no custom fields added to this workspace.");
			sbview.append("</td>\n");
			sbview.append("</tr>\n");
			
		}	
		
		sbview.append("</table>");

		
		return sbview.toString();

	}


	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */	
	public String printIssTypeDynvalsForView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws SQLException, Exception {

		StringBuffer sbview = new StringBuffer();

		////prev field c1..c7 vals
		/* ******
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";
		****** */
		//prev sub types
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";	
			
		/*  ****************8
		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();
		*******  */
		String testcase = AmtCommonUtils.getTrimStr(usr1InfoModel.getTestCase());

		ArrayList prevSubTypeAList = usr1InfoModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBList = usr1InfoModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCList = usr1InfoModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDList = usr1InfoModel.getPrevSubTypeDList();

		ArrayList prevProbTypeList = usr1InfoModel.getPrevProbTypeList();
		String issueType = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueType());

		//sub type a..d
		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

			prevSubTypeAVal = (String) prevSubTypeAList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

			prevSubTypeBVal = (String) prevSubTypeBList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

			prevSubTypeCVal = (String) prevSubTypeCList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

			prevSubTypeDVal = (String) prevSubTypeDList.get(0);

		}

		//prev fields c1..c7
		/*  ***************************************
		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList)) {

			prevFieldC1Val = (String) prevFieldC1ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)) {

			prevFieldC2Val = (String) prevFieldC2ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)) {

			prevFieldC3Val = (String) prevFieldC3ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)) {

			prevFieldC4Val = (String) prevFieldC4ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)) {

			prevFieldC5Val = (String) prevFieldC5ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)) {

			prevFieldC6Val = (String) prevFieldC6ValList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)) {

			prevFieldC7Val = (String) prevFieldC7ValList.get(0);

		}
		***********************  */
		
		sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

		//for non -pmo issues
		if (!isIssueSrcPMO(usr1InfoModel.getEdgeProblemId())) {

			if (!usr1InfoModel.isIssueTypIdActive()) {

				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultIssueTypeLabel(etsIssObjKey) + "</b><span class=\"small\"><span style=\"color:#ff0000\">*</span>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + issueType + "</td>");
				sbview.append("</tr>");

			} else {

				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultIssueTypeLabel(etsIssObjKey) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + issueType + "</td>");
				sbview.append("</tr>");

			}

		}
		// for non-pmo issues

		else {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultIssueTypeLabel(etsIssObjKey) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + issueType + "</td>");
			sbview.append("</tr>");
		}

		///////////

		//dyn vals//

		if (AmtCommonUtils.isResourceDefined(prevSubTypeAVal) && !prevSubTypeAVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_A, DEFUALTSTDSUBTYPE_A) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevSubTypeAVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeBVal) && !prevSubTypeBVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_B, DEFUALTSTDSUBTYPE_B) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevSubTypeBVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeCVal) && !prevSubTypeCVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_C, DEFUALTSTDSUBTYPE_C) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevSubTypeCVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeDVal) && !prevSubTypeDVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_D, DEFUALTSTDSUBTYPE_D) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevSubTypeDVal + "</td>");
			sbview.append("</tr>");

		}

		///static vals
		/*  **********************************
		if (AmtCommonUtils.isResourceDefined(prevFieldC1Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC1NAME, DEFUALTSTDFIELDC1NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC1Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC2Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC2NAME, DEFUALTSTDFIELDC2NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC2Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC3Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC3NAME, DEFUALTSTDFIELDC3NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC3Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC4Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC4NAME, DEFUALTSTDFIELDC4NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC4Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC5Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC5NAME, DEFUALTSTDFIELDC5NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC5Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC6Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC6NAME, DEFUALTSTDFIELDC6NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC6Val + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC7Val)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDFIELDC7NAME, DEFUALTSTDFIELDC7NAME) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"80%\">" + prevFieldC7Val + "</td>");
			sbview.append("</tr>");

		}
		*******************************  */
		if (AmtCommonUtils.isResourceDefined(testcase)) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"20%\"> ");
			sbview.append("<b>Test case</b>:");
			sbview.append("</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"80%\">");
			sbview.append("<pre>");
			sbview.append(testcase);
			sbview.append("</pre>");
			sbview.append("</td>");
			sbview.append("</tr>");

		}

		sbview.append("</table>");
		//sbview.append("<br />");

		//		for non -pmo issues
		if (!isIssueSrcPMO(usr1InfoModel.getEdgeProblemId())) {

			if (!usr1InfoModel.isIssueTypIdActive()) {

				String warnMsg = (String) propMap.get("issues.act.view.isstype.inactive.wrng");

				warnMsg = "* - " + warnMsg;

				sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" >[" + comGuiUtils.printWarningMsg(warnMsg) + "]</td>");
				sbview.append("</tr>");
				sbview.append("</table>");

			}

		}

		return sbview.toString();

	}


	/**
			 * This method will print submitter details
			 * 
			 */

	public String printIssueDescripDetailsForView(EtsIssProbInfoUsr1Model usr1Model, EtsIssObjectKey etsIssObjKey) throws Exception {

		StringBuffer sb = new StringBuffer();
		String severity = "";

		try {

			severity = getIssueSeverity(usr1Model);

			String cqTrkId = AmtCommonUtils.getTrimStr(usr1Model.getCqTrkId());
			String probTitle = AmtCommonUtils.getTrimStr(usr1Model.getProbTitle());
			String probDesc = comGuiUtils.formatDescStr(AmtCommonUtils.getTrimStr(usr1Model.getProbDesc()));
			String probState = AmtCommonUtils.getTrimStr(usr1Model.getProbState());
			String submissionDate = AmtCommonUtils.getTrimStr(usr1Model.getSubmissionDate());

			//get owner name
			ArrayList ownerNameList = usr1Model.getOwnerNameList();

			//get seq no
			int cq_seq_no = usr1Model.getCq_seq_no();
			int usr_seq_no = usr1Model.getUsr_seq_no();

			String ownerName = getOwnerName(usr1Model);

			sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			//sb.append("<tr><td colspan=\"5\">&nbsp;</td></tr>\n");

			sb.append("<tr>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Title</b>:</td>\n");
			sb.append("<td  valign=\"top\" width =\"40%\" align=\"left\">" + probTitle + "</td>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Date submitted</b>:</td>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\">" + submissionDate + "</td>\n");
			sb.append("</tr>\n");
			sb.append("</table>\n");

			sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			sb.append("<tr>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Severity</b>:</td>\n");
			sb.append("<td  valign=\"top\" width =\"40%\" align=\"left\">" + severity + "</td>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Status</b>:</td>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\">" + probState + "</td>\n");
			sb.append("</tr>\n");
			sb.append("</table>\n");

			///show only if owner defined && need to be shown

			//		get user/roles matrix

			EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

			if (usrRolesModel.isShowOwnerName()) {

				if (AmtCommonUtils.isResourceDefined(ownerName)) {

					sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
					sb.append("<tr>\n");
					sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Owner</b>:</td>\n");
					sb.append("<td  valign=\"top\" width =\"80%\" align=\"left\">" + ownerName + "</td>\n");
					sb.append("</tr>\n");
					sb.append("</table>\n");

				}

			} //show only when owner need to be shown

			else {

			}

			sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			sb.append("<tr>\n");
			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Description</b>:</td>\n");
			sb.append("<td  valign=\"top\" width =\"80%\" align=\"left\">\n");
			sb.append("<pre>");
			sb.append(probDesc);
			sb.append("</pre>");
			sb.append("</td>\n");
			sb.append("</tr>\n");

			sb.append("</table>\n");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printIssueDescripDetailsForView", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sb.toString();
	}

	/**
		 * This method will print submitter details
		 * 
		 */

	public String printSubmitterDetailsForView(EtsIssObjectKey issactionobjkey, EtsIssProbInfoUsr1Model usr1Model) {

		StringBuffer sb = new StringBuffer();
		EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();
		HashMap propMap = issactionobjkey.getPropMap();

		String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());
		String curIssueSubmitterEdgeId = AmtCommonUtils.getTrimStr(usr1Model.getProbCreator());

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = issactionobjkey.getUsrRolesModel();

		if (!issactionobjkey.isProjBladeType()) {

			sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Submitter</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustName + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Company</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustCompany + "</td>\n");
			sb.append("</tr>\n");
			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>E-mail</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustEmail + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Phone</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustPhone + "</td>\n");
			sb.append("</tr>\n");
			sb.append("</table>\n");

			sb.append("<br />\n");
			sb.append(actGuiUtils.printGreyLine());
			sb.append("<br />\n");

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");

				sb.append("<tr >\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Submitter</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustName + "</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Company</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustCompany + "</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
				sb.append("<tr >\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>E-mail</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustEmail + "</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Phone</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustPhone + "</td>\n");
				sb.append("</tr>\n");
				sb.append("</table>\n");

				sb.append("<br />\n");
				sb.append(actGuiUtils.printGreyLine());
				sb.append("<br />\n");

			} else {

				if (issactionobjkey.getEs().gUSERN.equals(curIssueSubmitterEdgeId)) {

					sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
					sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
					sb.append("<tr >\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Submitter</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustName + "</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Company</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustCompany + "</td>\n");
					sb.append("</tr>\n");
					sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
					sb.append("<tr >\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>E-mail</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustEmail + "</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Phone</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustPhone + "</td>\n");
					sb.append("</tr>\n");
					sb.append("</table>\n");

					sb.append("<br />\n");
					sb.append(actGuiUtils.printGreyLine());
					sb.append("<br />\n");

				} else {

				}

			}

		}

		return sb.toString();
	}

	/**
	 *  This method will print history of an issue, for a given edge problem if
	 * @param histList
	 * @return
	 */

	public String printIssueHistory(EtsIssObjectKey issactionobjkey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sb = new StringBuffer();

		int hsize = 0;

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = issactionobjkey.getUsrRolesModel();

		try {

			ArrayList histList = usr1InfoModel.getHistList();

			if (EtsIssFilterUtils.isArrayListDefndWithObj(histList)) {

				hsize = histList.size();
			}

			if (!Global.loaded) {

				Global.Init();
			}

			String dateTimeStr = "";
			String formatTimeStr = "";
			String actionTakenBy = "";
			String action = "";
			String issueNewState = "";

			if (hsize > 0) {

				sb.append("<form method=\"post\" name=\"issuesform\" action=\"ETSProjectsServlet.wss\"  >");

				sb.append(comGuiUtils.printCommonHidVars(issactionobjkey));

				sb.append(comGuiUtils.printHidCancelActionState(usr1InfoModel));

				sb.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step8")));
				sb.append("<br />");
				sb.append("<table summary=\"issue history\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");

				//print dynamic action header
				sb.append(printSortItemsHeader(issactionobjkey));

				for (int i = 0; i < hsize; i++) {

					EtsIssHistoryModel histModel = (EtsIssHistoryModel) histList.get(i);

					dateTimeStr = histModel.getActionTs();
					formatTimeStr = EtsIssFilterUtils.formatDate(dateTimeStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy  hh:mm");
					actionTakenBy = histModel.getUserName();
					action = histModel.getActionName();
					issueNewState = histModel.getIssueState();

					sb.append("	            <tr height=\"18\"> \n");
					sb.append("	              <td headers=\"atcol2\"  align=\"left\" bgcolor=\"#ffffff\" width=\"20%\" > \n");
					sb.append("&nbsp;" + formatTimeStr + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\"  align=\"left\" bgcolor=\"#ffffff\" width=\"35%\"> \n");
					sb.append("&nbsp;" + actionTakenBy + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\"  align=\"left\" bgcolor=\"#ffffff\" width=\"25%\"> \n");
					sb.append("&nbsp;" + action + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol5\"  align=\"left\" bgcolor=\"#ffffff\" width=\"20%\"> \n");
					sb.append("&nbsp;" + issueNewState + " \n");
					sb.append("	              </td> \n");
					sb.append("	            </tr> \n");

				} //end of for

				sb.append("</table>");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");
				sb.append(" </form> \n");

			} //end of if hsize > 0

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printIssueHistory", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		if (!issactionobjkey.isProjBladeType()) {

			return sb.toString();

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				return sb.toString();

			}
		}

		return "";

	}

	/**
		 * 
		 * @param edgeProblemId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 * get history list
		 */

	public String printSortItemsHeader(EtsIssObjectKey issactionobjkey) throws SQLException, Exception {

		int state = 0;

		String hist_sort_dtime_A = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_dtime_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_A)) {

			state = SORTHISTDATETIME_A;

		}

		String hist_sort_dtime_D = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_dtime_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_D)) {

			state = SORTHISTDATETIME_D;

		}

		String hist_sort_actionby_A = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_actionby_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_A)) {

			state = SORTHISTACTIONBY_A;

		}

		String hist_sort_actionby_D = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_actionby_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_D)) {

			state = SORTHISTACTIONBY_D;

		}

		String hist_sort_actionname_A = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_actionname_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_A)) {

			state = SORTHISTACTIONNAME_A;

		}

		String hist_sort_actionname_D = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_actionname_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_D)) {

			state = SORTHISTACTIONNAME_D;

		}

		String hist_sort_issuestate_A = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_issuestate_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_A)) {

			state = SORTHISTISSUESTATE_A;

		}

		String hist_sort_issuestate_D = AmtCommonUtils.getTrimStr((String) issactionobjkey.getParams().get("hist_sort_issuestate_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_D)) {

			state = SORTHISTISSUESTATE_D;

		}

		Global.println("SORT STATE IN printSortItemsHeader====" + state);

		StringBuffer sb = new StringBuffer();

		sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
		sb.append("	            <tr height=\"18\"> \n");

		///for date timestamp///
		sb.append("	              <th id=\"atcol2\" height=\"18\" align=\"left\" bgcolor=\"#999966\" width=\"20%\" > \n");

		switch (state) {

			case 0 :

				sb.append("	               <input type=\"image\" name=\"hist_sort_dtime_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

				break;

			case SORTHISTDATETIME_A :

				sb.append("	               <input type=\"image\" name=\"hist_sort_dtime_D\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_up.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in ascending order\" /> \n");

				break;

			case SORTHISTDATETIME_D :

				sb.append("	               <input type=\"image\" name=\"hist_sort_dtime_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

				break;

			default :

				sb.append("	               <input type=\"image\" name=\"hist_sort_dtime_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

		}

		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Date and time</span></b> \n");
		sb.append("	              </th> \n");

		///for user_name
		sb.append("	              <th id=\"atcol3\"  height=\"18\" align=\"left\" bgcolor=\"#999966\" width=\"35%\"> \n");

		switch (state) {

			case 0 :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionby_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

			case SORTHISTACTIONBY_A :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionby_D\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_up.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in ascending order\" /> \n");

				break;

			case SORTHISTACTIONBY_D :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionby_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

				break;

			default :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionby_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

		}

		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Action taken by</span></b> \n");
		sb.append("	              </th> \n");

		///action 
		sb.append("	              <th id=\"atcol4\"  height=\"18\" align=\"left\" bgcolor=\"#999966\" width=\"25%\"> \n");

		switch (state) {

			case 0 :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionname_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

			case SORTHISTACTIONNAME_A :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionname_D\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_up.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in ascending order\" /> \n");

				break;

			case SORTHISTACTIONNAME_D :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionname_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

				break;

			default :

				sb.append("	               <input type=\"image\" name=\"hist_sort_actionname_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

		}

		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Action</span></b> \n");
		sb.append("	              </th> \n");

		//new issue state
		sb.append("	              <th id=\"atcol5\"  height=\"18\" align=\"left\" bgcolor=\"#999966\" width=\"20%\"> \n");

		switch (state) {

			case 0 :

				sb.append("	               <input type=\"image\" name=\"hist_sort_issuestate_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

			case SORTHISTISSUESTATE_A :

				sb.append("	               <input type=\"image\" name=\"hist_sort_issuestate_D\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_up.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in ascending order\" /> \n");

				break;

			case SORTHISTISSUESTATE_D :

				sb.append("	               <input type=\"image\" name=\"hist_sort_issuestate_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_down.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Sorted in descending order\" /> \n");

				break;

			default :

				sb.append("	               <input type=\"image\" name=\"hist_sort_issuestate_A\" align=\"top\" src=\"" + Global.WebRoot + "/images/edesign_updown.gif\" height=\"18\" width=\"18\" border=\"0\" alt=\"Click To Sort\" /> \n");

				break;

		}

		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;New state</span></b> \n");
		sb.append("	              </th> \n");
		sb.append("	            </tr> \n");

		return sb.toString();

	}

	public String printStdViewActionsAtEnd(EtsIssObjectKey etsIssObjKey, EtsIssFilterObjectKey issfilterkey, String edgeProblemId) {

		StringBuffer sbsub = new StringBuffer();
		String srt = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("srt"));

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append(
			"<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp="
				+ etsIssObjKey.getIstyp()
				+ "&opn="
				+ etsIssObjKey.getFilopn()
				+ "&proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&srt="
				+ srt
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\""
				+ "><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=" + etsIssObjKey.getFilopn() + "&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&srt=" + srt + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("<td valign=\"top\" align=\"left\">" + filGuiUtils.printPrinterFriendly(issfilterkey, edgeProblemId) + "</td> ");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	public String printCommLogLink(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbsub = new StringBuffer();

		String comm_log_Str = usr1InfoModel.getCommentLogStr();

		String viewUrl = "showCommLog.wss?edge_problem_id=" + usr1InfoModel.getEdgeProblemId() + "&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "";

		if (AmtCommonUtils.isResourceDefined(comm_log_Str)) {

			sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			sbsub.append("<tr><td>&nbsp;</td></tr>");
			sbsub.append("<tr>");
			sbsub.append("<td valign=\"top\" align=\"left\">" + comGuiUtils.printPrinterFriendly(viewUrl, "Print Commentary log") + "</td> ");
			sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		}

		return sbsub.toString();

	}

	/**
		 *  This method will print history of an issue, for a given edge problem if
		 * with no sort capabilities
		 * @param histList
		 * @return
		 */

	public String printIssueHistoryWithNoSort(EtsIssObjectKey issactionobjkey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sb = new StringBuffer();

		int hsize = 0;

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = issactionobjkey.getUsrRolesModel();

		try {

			ArrayList histList = usr1InfoModel.getHistList();

			if (EtsIssFilterUtils.isArrayListDefndWithObj(histList)) {

				hsize = histList.size();
			}

			if (!Global.loaded) {

				Global.Init();
			}

			String dateTimeStr = "";
			String formatTimeStr = "";
			String actionTakenBy = "";
			String action = "";
			String issueNewState = "";

			if (hsize > 0) {

				//sb.append("<form method=\"post\" name=\"issuesform\" action=\"ETSProjectsServlet.wss\"  >");

				sb.append(comGuiUtils.printCommonHidVars(issactionobjkey));

				sb.append(comGuiUtils.printHidCancelActionState(usr1InfoModel));

				sb.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step8")));
				sb.append("<br />");
				sb.append("<table summary=\"issue history\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");

				//print dynamic action header
				sb.append(printSortItemsHeader(issactionobjkey));

				for (int i = 0; i < hsize; i++) {

					EtsIssHistoryModel histModel = (EtsIssHistoryModel) histList.get(i);

					dateTimeStr = histModel.getActionTs();
					formatTimeStr = EtsIssFilterUtils.formatDate(dateTimeStr, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy  hh:mm");
					actionTakenBy = histModel.getUserName();
					action = histModel.getActionName();
					issueNewState = histModel.getIssueState();

					sb.append("	            <tr height=\"18\"> \n");
					sb.append("	              <td headers=\"atcol2\"  align=\"left\" bgcolor=\"#ffffff\" width=\"20%\" > \n");
					sb.append("&nbsp;" + formatTimeStr + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\"  align=\"left\" bgcolor=\"#ffffff\" width=\"35%\"> \n");
					sb.append("&nbsp;" + actionTakenBy + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\"  align=\"left\" bgcolor=\"#ffffff\" width=\"25%\"> \n");
					sb.append("&nbsp;" + action + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol5\"  align=\"left\" bgcolor=\"#ffffff\" width=\"20%\"> \n");
					sb.append("&nbsp;" + issueNewState + " \n");
					sb.append("	              </td> \n");
					sb.append("	            </tr> \n");

				} //end of for

				sb.append("</table>");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");
				//sb.append(" </form> \n");

			} //end of if hsize > 0

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printIssueHistory", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		if (!issactionobjkey.isProjBladeType()) {

			return sb.toString();

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				return sb.toString();

			}
		}

		return "";

	}

	public String printActionsInViewWithNoLinks(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		if (!issueSource.equals(ETSPMOSOURCE)) {

			return printActionsInViewWithNoLinksForGen(etsIssObjKey, usr1InfoModel, propMap);

		} else {

			return printActionsInViewWithNoLinksForPMO(etsIssObjKey, usr1InfoModel, propMap);

		}

	}

	/**
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 * @throws Exception
		 */

	public String printActionsInViewWithNoLinksForGen(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();

		String edgeProblemId = usr1InfoModel.getEdgeProblemId();

		boolean actionavailable = false;

		try {

			//get USER/ROLES HELPER
			EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();

			//get user/roles matrix
			EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(etsIssObjKey);

			//get user/actions matrix
			EtsIssUserActionsModel usrActionsModel = userFilter.getUserActionMatrix(usr1InfoModel, etsIssObjKey);

			String rejectMsg = (String) propMap.get("issue.act.view.msg3");

			//show actions any user in project, except with WORKSPACE_VISITOR entitlement//

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step7")));

				sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");

				if (!usrActionsModel.isActionavailable()) {

					String viewInProcessMsg = "";

					if (!usr1InfoModel.isIssueTypIdActive()) {

						viewInProcessMsg = (String) propMap.get("issues.act.view.isstype.inactive.msg");

					} else {

						viewInProcessMsg = (String) propMap.get("issues.act.view.inprocess.msg");

					}

					sbview.append("<tr> ");
					sbview.append("<td  valign=\"top\" width=\"600\">");
					sbview.append("Note : " + viewInProcessMsg + " ");
					sbview.append("</td> ");
					sbview.append("</tr> ");

				} else {

					//				if (probState.equals("Rejected")) {
					//
					//					sbview.append("<tr> ");
					//					sbview.append("<td  valign=\"top\" width=\"600\">");
					//					sbview.append(rejectMsg);
					//					sbview.append("</td> ");
					//					sbview.append("</tr> ");
					//					sbview.append("<tr><td></td>");
					//					sbview.append("</tr>");
					//					sbview.append("</table>");
					//					sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
					//
					//				}

					if (usrActionsModel.isUsrResolveIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Resolve</td>");
						sbview.append("</tr>");

					}

					if (usrActionsModel.isUsrModifyIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Modify</td>");
						sbview.append("</tr>");

					}

					//only submitter/work space manager can close/reject issues

					if (usrActionsModel.isUsrRejectIssue()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Reject</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Close</td>");
						sbview.append("</tr>");

					}

					sbview.append("<tr>");
					sbview.append("<td>");
					sbview.append("</td>");
					sbview.append("</tr>");
					sbview.append("<tr>");
					sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					sbview.append("<td align=\"left\">Comment</td>");
					sbview.append("</tr>");

					//for withdraw issue 

					if (usrActionsModel.isUsrWithDraw()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Withdraw</td>");
						sbview.append("</tr>");

					}

					String chgOwnerMsg = (String) propMap.get("issue.view.chgown.msg");

					//show only when need to be shown

					if (usrActionsModel.isUsrChangeOwner()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Change owner</td>");
						sbview.append("</tr>");

					} // is Show Owner true
					sbview.append("</table>");

					if (usrActionsModel.isUsrSubscribe()) {

						//if the user is subscribed to issue type, show the messg
						if (usr1InfoModel.isUsrIssTypSubscribe() && usr1InfoModel.isUsrIssueSubscribe()) {

							String subsIssTypeMsg = (String) propMap.get("issues.act.view.subs.isstype");

							sbview.append("<br />");
							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append("</td>");
							sbview.append("</tr>");
							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append(comGuiUtils.printWarningMsg(subsIssTypeMsg));
							sbview.append("</td>");
							sbview.append("</tr>");

							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append("</td>");
							sbview.append("</tr>");
							sbview.append("</table>");

							//
							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
							sbview.append("<tr>");
							sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
							sbview.append("<td align=\"left\">Unsubscribe to issue type</td>");
							sbview.append("</tr>");

						}

						//if user is not subscribed to issue type

						if (!usr1InfoModel.isUsrIssTypSubscribe()) {

							sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
							sbview.append("<tr>");
							sbview.append("<td>");
							sbview.append("</td>");
							sbview.append("</tr>");

							if (usr1InfoModel.isUsrIssueSubscribe()) {

								sbview.append("<tr>");
								sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
								sbview.append("<td align=\"left\">Subscribe to this issue</td>");
								sbview.append("</tr>");

							} else {

								sbview.append("<tr>");
								sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
								sbview.append("<td align=\"left\">Unsubscribe to this issue</td>");
								sbview.append("</tr>");

							} //unsubsc

							sbview.append("</table>");

						} //if not subscribed to issue type

					} // is Show Owner true

					//////////////1s

					//////////////1s

				}

				///end phani #1////////////////

				//sbview.append("</table>");

				sbview.append("<br />");

			} //show not to workspace visitor

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();

	}

	/**
			 * 
			 * @param etsIssObjKey
			 * @param usr1InfoModel
			 * @param propMap
			 * @return
			 * @throws Exception
			 */

	public String printActionsInViewWithNoLinksForPMO(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();

		String edgeProblemId = usr1InfoModel.getEdgeProblemId();

		boolean actionavailable = false;

		try {

			//get USER/ROLES HELPER
			EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();

			//get user/roles matrix
			EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(etsIssObjKey);

			//get user/actions matrix
			EtsIssUserActionsModel usrActionsModel = userFilter.getUserActionMatrixForPMO(usr1InfoModel, etsIssObjKey);

			String rejectMsg = (String) propMap.get("issue.act.view.msg3");

			//show actions any user in project, except with WORKSPACE_VISITOR entitlement//

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.view.step7")));

				sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");
				sbview.append("<tr><td></td>");
				sbview.append("</tr>");

				if (!usrActionsModel.isActionavailable()) {

					String viewInProcessMsg = (String) propMap.get("issues.act.view.inprocess.msg");

					sbview.append("<tr> ");
					sbview.append("<td  valign=\"top\" width=\"600\">");
					sbview.append("Note : " + viewInProcessMsg + " ");
					sbview.append("</td> ");
					sbview.append("</tr> ");

				} else {

					if (usrActionsModel.isUsrResolveIssue()) {

						sbview.append("<tr><td></td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Resolve</td>");
						sbview.append("</tr>");

					}

					//only submitter/work space manager can close/reject issues

					if (usrActionsModel.isUsrRejectIssue()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");

						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Reject</td>");
						sbview.append("</tr>");

					}

					if (usrActionsModel.isUsrCloseIssue()) {

						sbview.append("<tr>");
						sbview.append("<td>");
						sbview.append("</td>");
						sbview.append("</tr>");
						sbview.append("<tr>");
						sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						sbview.append("<td align=\"left\">Close</td>");
						sbview.append("</tr>");

					}

					sbview.append("<tr>");
					sbview.append("<td>");
					sbview.append("</td>");
					sbview.append("</tr>");
					sbview.append("<tr>");
					sbview.append("<td colspan=\"1\" width=\"18\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					sbview.append("<td align=\"left\">Comment</td>");
					sbview.append("</tr>");

					String chgOwnerMsg = (String) propMap.get("issue.view.chgown.msg");

					//////////////1s

				}

				///end phani #1////////////////

				sbview.append("</table>");

				sbview.append("<br />");

			} //show not to workspace visitor

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssViewGuiUtils", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();

	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @return
	 */

	public String printAllRtfs(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sb = new StringBuffer();

		EtsCrViewGuiUtils crViewUtils = new EtsCrViewGuiUtils();

		//pcr properties
		HashMap pcrPropMap = etsIssObjKey.getPcrPropMap();

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		//rtf map
		HashMap rtfMap = usr1InfoModel.getRtfMap();

		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		String pmo_cri_RTF_DynStr = "";
		String pmo_cri_RTF_Id = "";

		String crRftModelAliasName = "";
		String crRftModelBlobStr = "";
		String pmoId = "";

		//get the total count of RTFS from property file, that need to be displayed
		String strRtfCount = AmtCommonUtils.getTrimStr((String) pcrPropMap.get("ets_pmo_iss.RTF.NoOfRTFs"));
		int intRtfCount = 0;

		if (AmtCommonUtils.isResourceDefined(strRtfCount)) {

			intRtfCount = Integer.parseInt(strRtfCount);
		}

		if (rtfMap != null && !rtfMap.isEmpty()) {

			for (int i = 1; i <= intRtfCount; i++) {

				pmo_cri_RTF_DynStr = "ets_pmo_iss.RTF." + i;
				pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

				Global.println("pmo_cri_RTF_DynStr==" + pmo_cri_RTF_DynStr);
				Global.println("pmo_cri_RTF_Id==" + pmo_cri_RTF_Id);

				if (AmtCommonUtils.isResourceDefined(pmo_cri_RTF_Id)) {

					crRtfModel = (EtsCrRtfModel) rtfMap.get(pmo_cri_RTF_Id);

				} else {

					crRtfModel = null;
				}

				///

				if (crRtfModel != null) {

					Global.println("crRtfModel NOT NULL FOR==" + pmo_cri_RTF_Id);

					crRftModelAliasName = crRtfModel.getRtfAliasName();
					crRftModelBlobStr = crRtfModel.getRtfBlobStr();
					pmoId = crRtfModel.getPmoId();

					Global.println("crRftModel alias name==" + crRftModelAliasName);
					Global.println("crRftModel Blob Str==" + crRftModelBlobStr);

					//print RTFs except description and comments

					if (i != 1 && i != 7) {

						sb.append(crViewUtils.printRtf(crRftModelAliasName, crRftModelBlobStr, pmoId));

					}

				} //crRftModel != null

			} //for loop

		} //rtfMap != null

		if (issueSource.equals(ETSPMOSOURCE)) {

			//sb.append("<br />");

			return sb.toString();

		} else {

			return "";
		}

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

		int state = 0;
		String sortColumn = "ACTION_TS";
		String sortOrder = "desc";

		String hist_sort_dtime_A = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_dtime_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_A)) {

			state = SORTHISTDATETIME_A;

		}

		String hist_sort_dtime_D = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_dtime_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_D)) {

			state = SORTHISTDATETIME_D;

		}

		String hist_sort_actionby_A = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_actionby_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_A)) {

			state = SORTHISTACTIONBY_A;

		}

		String hist_sort_actionby_D = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_actionby_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_D)) {

			state = SORTHISTACTIONBY_D;

		}

		String hist_sort_actionname_A = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_actionname_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_A)) {

			state = SORTHISTACTIONNAME_A;

		}

		String hist_sort_actionname_D = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_actionname_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_D)) {

			state = SORTHISTACTIONNAME_D;

		}

		String hist_sort_issuestate_A = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_issuestate_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_A)) {

			state = SORTHISTISSUESTATE_A;

		}

		String hist_sort_issuestate_D = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("hist_sort_issuestate_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_D)) {

			state = SORTHISTISSUESTATE_D;

		}

		Global.println("SORT STATE IN ISSUE HISTORY IN DATA PREP BEAN====" + state);

		switch (state) {

			case 0 :

				sortColumn = "ACTION_TS";
				sortOrder = "DESC";

				break;

			case SORTHISTDATETIME_A :

				sortColumn = "ACTION_TS";
				sortOrder = "";

				break;

			case SORTHISTDATETIME_D :

				sortColumn = "ACTION_TS";
				sortOrder = "DESC";

				break;

			case SORTHISTACTIONBY_A :

				sortColumn = "USERNAME";
				sortOrder = "";

				break;

			case SORTHISTACTIONBY_D :

				sortColumn = "USERNAME";
				sortOrder = "DESC";

				break;

			case SORTHISTACTIONNAME_A :

				sortColumn = "ACTION_NAME";
				sortOrder = "";

				break;

			case SORTHISTACTIONNAME_D :

				sortColumn = "ACTION_NAME";
				sortOrder = "DESC";

				break;

			case SORTHISTISSUESTATE_A :

				sortColumn = "ISSUE_STATE";
				sortOrder = "";

				break;

			case SORTHISTISSUESTATE_D :

				sortColumn = "ISSUE_STATE";
				sortOrder = "DESC";

				break;

			default :

				sortColumn = "ACTION_TS";
				sortOrder = "DESC";

				break;
		}

		//	add history of issue
		EtsIssHistoryDAO hisDao = new EtsIssHistoryDAO();
		ArrayList histList = hisDao.getIssueHistoryObjList(edgeProblemId, sortColumn, sortOrder, issueSource);

		return histList;

	}

	/**
	 * 
	 * @param edgeProblemId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isIssueSrcPMO(String edgeProblemId) throws SQLException, Exception {

		IssueInfoDAO infoDao = new IssueInfoDAO();

		return infoDao.isIssueSrcPMO(edgeProblemId);

	}

	/**
	 * 
	 * @param usr1InfoModel
	 * @return
	 */

	public String printScrn1Msg(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer scrSb = new StringBuffer();
		String refNo = AmtCommonUtils.getTrimStr(usr1InfoModel.getRefNo());

		if (AmtCommonUtils.isResourceDefined(refNo) && !refNo.equals("0")) {

			scrSb.append("Here are the issue details for ");

			if (usr1InfoModel.isIssueSrcPMO()) {

				scrSb.append("Reference Number");

			} else {

				scrSb.append("ID");

			}

			scrSb.append("  : " + refNo + " ");

		} else {

			scrSb.append("Here are the issue details");
		}

		return scrSb.toString();
	}

	/**
		 * 
		 * @param commLog
		 * @return
		 */

	public String printCompleteComments(String commLog) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		sb.append("<tr>");
		sb.append("<td >" + MessgFormatUtils.getFormatComments(AmtCommonUtils.getTrimStr(commLog)) + "</td>");
		sb.append("</tr>");
		sb.append("</table>");

		return sb.toString();
	}

	/**
	 * 
	 * @param issfilterkey
	 * @param usr1InfoModel
	 * @return
	 */

	public String printMultiIssueDetails(EtsIssObjectKey issObjKey, EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sb = new StringBuffer();

		String cqTrkId = AmtCommonUtils.getTrimStr(usr1InfoModel.getCqTrkId());
		String probTitle = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbTitle());
		String issueType = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueType());
		String severity = AmtCommonUtils.getTrimStr(getIssueSeverity(usr1InfoModel));
		String dateSubmitted = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getSubmissionDate());
		String submitter = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getCustName());
		String custCompany = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getCustCompany());
		String custEmail = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getCustEmail());
		String custPhone = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getCustPhone());
		String probState = AmtCommonUtils.getTrimStr((String) usr1InfoModel.getProbState());
		String ownerName = AmtCommonUtils.getTrimStr(getOwnerName(usr1InfoModel));
		String description = usr1InfoModel.getProbDesc();
		
		//String commLog = getFormattedCommLog(usr1InfoModel);
		//String commLog = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommentLogStr());
		String commLog = usr1InfoModel.getCommentLogStr();
		String comm = convertNewLines(commLog);
		String comm_log_string = AmtCommonUtils.getTrimStr(usr1InfoModel.getCommentLogStr());
		     
		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		sb.append("<tr>");
		sb.append("<td><b>Issue ID</b>:</td>");
		sb.append("<td>" + cqTrkId + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Title</b>:</td>");
		sb.append("<td>" + probTitle + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Issue type</b>:</td>");
		sb.append("<td>" + issueType + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Severity</b>:</td>");
		sb.append("<td>" + severity + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Date submitted</b>:</td>");
		sb.append("<td>" + dateSubmitted + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Submitter/Company</b>:</td>");
		sb.append("<td>" + submitter + " / " + custCompany + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>E-mail/phone</b>:</td>");
		sb.append("<td>" + custEmail + " / " + custPhone + "</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><b>Status</b>:</td>");
		sb.append("<td>" + probState + "</td>");
		sb.append("</tr>");

		EtsIssUserRolesModel usrRolesModel = issObjKey.getUsrRolesModel();

		if (usrRolesModel.isShowOwnerName()) {

			if (AmtCommonUtils.isResourceDefined(ownerName)) {
				sb.append("<tr>");
				sb.append("<td><b>Owner</b>:</td>");
				sb.append("<td>" + ownerName + "</td>");
				sb.append("</tr>");

			}

		}
		sb.append("<tr>");
		sb.append("<td colspan=\"2\"><b>Description</b>:</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		//sb.append("<td colspan=\"2\"><pre>" + description + "</pre></td>");
		//surya 
		sb.append("<td colspan=\"2\">");
		sb.append("<div align=\"justify\">"+ convertDesc(description) + "</div>");
		sb.append("</td>"); //surya
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan=\"2\">&nbsp;</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td colspan=\"2\" ><b>Commentary log</b>:</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		//sb.append("<td colspan=\"2\"> " + printMultiFormatComments(commLog) + "</td>");
		//sb.append("<td colspan=\"2\"><pre> " + commLog + "</pre></td>"); //surya
		sb.append("<td colspan=\"2\">");
		//sb.append("<div align=\"justify\"> "+ commLog +"</div>");
	    sb.append("<div align=\"justify\"> "+ comm +"</div>");//surya
		sb.append("</td>"); 
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br /><br />");
		sb.append("<img alt=\"\" height=\"4\" src=\"//www.ibm.com/i/v14/rules/dgray_rule.gif\" width=\"600\"/>");
		sb.append("<br /><br />");

		return sb.toString();

	}
	
	private String convertDesc(String strInput) {
		int length = strInput.length();
		if (strInput.indexOf("\n") == -1) {
			return strInput;
		}
		StringBuffer strBuffer = new StringBuffer("");
		for (int i=0; i < length; i++) {
			char c = strInput.charAt(i);
			
				if (c == '\n') {
					strBuffer.append("<br />");
				}
				else {
					strBuffer.append(c);
				}
		
		}
		return strBuffer.toString();
	}
	
	private String convertNewLines(String strInput) {
		int length = strInput.length();
		if (strInput.indexOf("\n") == -1) {
		return strInput;
		}
		StringBuffer strBuffer = new StringBuffer("");
		int count = 0;
		for (int i=0; i < length; i++) {
		char c = strInput.charAt(i);

		if (c == '\n') 
		{
		if(count==0){
		strBuffer.append("<br />");}
		count++;
		
		}
		else 
		{
			if(count > 0 && count < 3)
				{
					for( int j=1; j<count; j++){
						strBuffer.append("<br />");
					}
				}
			else if(count> 3) {
				strBuffer.append("<br /><br />");
				}
			strBuffer.append(c);
			count=0;
			}
		}
		return strBuffer.toString();
		}
		

	/**
	 * 
	 * @param usr1Model
	 * @return
	 */

	public String getIssueSeverity(EtsIssProbInfoUsr1Model usr1Model) {

		ArrayList prevSevList = usr1Model.getPrevProbSevList();
		String severity = "";

		if (prevSevList != null && !prevSevList.isEmpty()) {

			severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

		}

		//remove like 1-,2- from severity 	
		int inx = severity.indexOf("-");

		if (inx != -1) {

			severity = severity.substring(inx + 1);

		}

		return severity;

	}

	/**
	 * 
	 * @param usr1InfoModel
	 * @return
	 */

	public String getOwnerName(EtsIssProbInfoUsr1Model usr1InfoModel) {

		//		get owner name
		ArrayList ownerNameList = usr1InfoModel.getOwnerNameList();

		String ownerName = "";

		if (EtsIssFilterUtils.isArrayListDefnd(ownerNameList)) {

			ownerName = (String) ownerNameList.get(0);
		}

		return ownerName;
	}

	/**
			 * 
			 * @param commLog
			 * @return
			 */

	public String printMultiFormatComments(String commLog) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		sb.append("<tr>");
		sb.append("<td >" + MessgFormatUtils.getMultiIssueFormatComments(AmtCommonUtils.getTrimStr(commLog)) + "</td>");
		sb.append("</tr>");
		sb.append("</table>");

		return sb.toString();
	}

	public String printMultiBkActionUrl(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sb = new StringBuffer();

		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		int srt = issfilterkey.getSortState();
		int flop = issfilterkey.getFlopstate();

		sb.append("<form name=\"bkrep\" method=\"post\" action=\"EtsIssFilterCntrlServlet.wss?istyp=" + istype + "&opn=" + issopn + "&proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&flop=" + flop + "&srt=" + srt + "\" >");

		return sb.toString();

	}

} //end of class
