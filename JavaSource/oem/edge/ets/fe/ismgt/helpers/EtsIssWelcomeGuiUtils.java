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

import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssWelcomeGuiUtils implements EtsIssFilterConstants {

	public static final String VERSION = "1.40";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsIssWelcomeGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();

	}

	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public String printWelcomeIssues(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		boolean bladeUsrInt = false;

		try {

			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			sbview.append("<table summary=\"issues\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");

			//create not for visitir or executive

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\">\n");
				sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitIssue&op=1&istyp=iss&flop=1\">\n");
				sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.issue.newissue.linktext") + "\" \n");
				sbview.append("align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
				sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitIssue&op=1&istyp=iss&flop=1\">" + propMap.get("filter.issue.newissue.linktext") + "\n");
				sbview.append("</a>\n");
				sbview.append("</td>\n");
				sbview.append("</tr>\n");

				//submit new issue for external customer on behalf of them and this feature can be done only
				//by internals

				if (usrRolesModel.isUsrInternal()) {

					EtsProjMemberDAO projDao = new EtsProjMemberDAO();
					ArrayList userList = projDao.getClientRoleExtListForProject(etsprojid, issfilterkey.isProjBladeType());

					int isize = 0;

					if (EtsIssFilterUtils.isArrayListDefnd(userList)) {

						isize = userList.size();
					}

					//show this option only when there are atleast 1 ext user under a given non-blade project
					//this rule is not applicable fro blade type project

					if (isize > 0) {

						sbview.append("<tr>\n");
						sbview.append("<td  valign=\"top\">\n");
						sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitIssue&op=3&istyp=iss&flop=1&extbhf=1\">\n");
						sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.issue.newissue.onbehalfext.linktext") + "\" \n");
						sbview.append("align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
						sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitIssue&op=3&istyp=iss&flop=1&extbhf=1\">" + propMap.get("filter.issue.newissue.onbehalfext.linktext") + "\n");
						sbview.append("</a>\n");
						sbview.append("</td>\n");
						sbview.append("</tr>\n");

					}

				}

			}

			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append("<tr>\n");
				sbview.append("<td valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=20&tc=" + tc + "\">\n");
				sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.issue.wrkall.linktext") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> <a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=20&tc=" + tc + "\">" + propMap.get("filter.issue.wrkall.linktext") + "\n");
				sbview.append("</a> \n");
				sbview.append("</td>\n");
				sbview.append("</tr>\n");

			} else {

				sbview.append("<tr>\n");
				sbview.append("<td valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=20&tc=" + tc + "\">\n");
				sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.issue.wrkall.linktext.viewrole") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> <a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=20&tc=" + tc + "\">" + propMap.get("filter.issue.wrkall.linktext.viewrole") + "\n");
				sbview.append("</a> \n");
				sbview.append("</td>\n");
				sbview.append("</tr>\n");

			}

			//		create not for visitir or executive
			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=30&tc=" + tc + "\">\n");
				sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("filter.issue.isub.linktext") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=30&tc=" + tc + "\">" + propMap.get("filter.issue.isub.linktext") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td>\n");
				sbview.append("</tr>\n");

				if (!issfilterkey.isProjBladeType()) {

					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=40&tc=" + tc + "\">\n");
					sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
					sbview.append("alt=\"" + propMap.get("filter.issue.assignme.linktext") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
					sbview.append("border=\"0\" /></a> \n");
					sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=40&tc=" + tc + "\">" + propMap.get("filter.issue.assignme.linktext") + " \n");
					sbview.append("</a> \n");
					sbview.append("</td> \n");
					sbview.append("</tr> \n");

				} else { //external blade customers cannot be issue owners

					if (bladeUsrInt) {

						sbview.append("<tr>\n");
						sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=40&tc=" + tc + "\">\n");
						sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
						sbview.append("alt=\"" + propMap.get("filter.issue.assignme.linktext") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
						sbview.append("border=\"0\" /></a> \n");
						sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=40&tc=" + tc + "\">" + propMap.get("filter.issue.assignme.linktext") + " \n");
						sbview.append("</a> \n");
						sbview.append("</td> \n");
						sbview.append("</tr> \n");

					}

				}

			}

			//manage issue types, add,delete and edit			
			if (usrRolesModel.isUsrReqIssueType()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=2200&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("setup.isstypes.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=2200&tc=" + tc + "\">" + propMap.get("setup.isstypes.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
			}
				////Added  by Prasad... Manage Custom Fields.....
			if (usrRolesModel.isUsrReqIssueType()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=2400&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("setup.custom.field.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=iss&opn=2400&tc=" + tc + "\">" + propMap.get("setup.custom.field.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
			}

			//subscribe issue types
			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"subsIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&actionType=subsIssType&istyp=iss&opn=1200&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("manage.isstypes.subs") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"subsIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&actionType=subsIssType&istyp=iss&opn=1200&tc=" + tc + "\">" + propMap.get("manage.isstypes.subs") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
			}

			
			if (!usrRolesModel.isUsrVisitor()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"downLoadAllIssueCsv.wss?proj=" + etsprojid + "&linkid=" + linkid + "&dwnld=Y&istyp=iss&opn=20&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("download.all.issues.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"downLoadAllIssueCsv.wss?proj=" + etsprojid + "&linkid=" + linkid + "&dwnld=Y&istyp=iss&opn=20&tc=" + tc + "\">" + propMap.get("download.all.issues.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
			}
			
			
			
			///
			sbview.append("</table> \n");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeIssues", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();
	}

	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public String printWelcomeChanges(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String projOrProposal = issfilterkey.getProj().getProjectOrProposal();
		String pmoProjectId = issfilterkey.getProj().getPmo_project_id();

		//to show CR or not
		String showCr = (String) propMap.get("pmo.chg.req.show");

		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		try {

			if (projOrProposal.equals("P")) { //for projects ony

				if (showCr.equals("Y")) { //show CR

					if (AmtCommonUtils.isResourceDefined(pmoProjectId)) { //for PMO PROJECT ID defined

						sbview.append(comGuiUtils.printSectnHeader("Changes requests"));

						sbview.append("<table summary=\"welcome changes\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");

						//	create not for visitir or executive

						if (!usrRolesModel.isUsrVisitor()) {

							sbview.append("<tr>\n");
							sbview.append("<td valign=\"top\"> \n");
							sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitChange&op=1&istyp=chg&flop=1\">\n");
							sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.chgreq.newchgreq.linktext") + "\" \n");
							sbview.append("align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a> \n");
							sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=submitChange&op=1&istyp=chg&flop=1\">" + propMap.get("filter.chgreq.newchgreq.linktext") + " \n");
							sbview.append("</a>  \n");
							sbview.append("</td> \n");
							sbview.append("</tr> \n");

						}

						if (!usrRolesModel.isUsrVisitor()) {

							sbview.append("<tr> \n");
							sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=20&tc=" + tc + "\"> \n");
							sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\"  \n");
							sbview.append("alt=\"" + propMap.get("filter.chgreq.wrkall.linktext") + "\" align=\"top\" width=\"16\" height=\"16\"  \n");
							sbview.append("border=\"0\" /></a>  \n");
							sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=20&tc=" + tc + "\">" + propMap.get("filter.chgreq.wrkall.linktext") + "\n");
							sbview.append("</a>  \n");
							sbview.append("</td>  \n");
							sbview.append("</tr>  \n");

						} else {

							sbview.append("<tr> \n");
							sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=20&tc=" + tc + "\"> \n");
							sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\"  \n");
							sbview.append("alt=\"" + propMap.get("filter.chgreq.wrkall.linktext.viewrole") + "\" align=\"top\" width=\"16\" height=\"16\"  \n");
							sbview.append("border=\"0\" /></a>  \n");
							sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=20&tc=" + tc + "\">" + propMap.get("filter.chgreq.wrkall.linktext.viewrole") + "\n");
							sbview.append("</a>  \n");
							sbview.append("</td>  \n");
							sbview.append("</tr>  \n");

						}

						//	not for visitir or executive

						if (!usrRolesModel.isUsrVisitor()) {

							sbview.append("<tr>  \n");
							sbview.append("<td  valign=\"top\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=30&tc=" + tc + "\"> \n");
							sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\"  \n");
							sbview.append("alt=\"" + propMap.get("filter.chgreq.isub.linktext") + "\" align=\"top\" width=\"16\" height=\"16\"  \n");
							sbview.append("border=\"0\" /></a>   \n");
							sbview.append("<a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsprojid + "&linkid=" + linkid + "&istyp=chg&opn=30&tc=" + tc + "\">\n");
							sbview.append("" + propMap.get("filter.chgreq.isub.linktext") + "</a>   \n");
							sbview.append("</td>  \n");
							sbview.append("</tr>  \n");



						}

						sbview.append("</table>  \n");

					} //pmo project id

				} //for CR

			} //only for projects, not for proposals/feedback

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeChanges", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();
	}

	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public String printWelcomeFeedBack(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		try {

			if (!issfilterkey.isProjBladeType()) { //show only if it is not blade project

				EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();

				sbview.append(actGuiUtils.printSectnHeader("Feedback"));

				sbview.append("<table summary=\"feedback links\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">\n");

				//		create not for visitir or executive

				if (!usrRolesModel.isUsrVisitor()) {

					sbview.append("<tr>\n");
					sbview.append("<td valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback\"> \n");
					sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.fdbk.submit.your") + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback\">" + propMap.get("filter.fdbk.submit.your") + "\n");
					sbview.append("</a> \n");
					sbview.append("</td>\n");
					sbview.append("</tr>\n");

					sbview.append("<tr>\n");
					sbview.append("<td valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=myfeedback\">\n");
					sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.fdbk.submit.byme") + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=myfeedback\">" + propMap.get("filter.fdbk.submit.byme") + "\n");
					sbview.append("</a> \n");
					sbview.append("</td>\n");
					sbview.append("</tr>\n");

				}

				if (!usrRolesModel.isUsrVisitor()) {

					sbview.append("<tr>\n");
					sbview.append("<td valign=\"top\">\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=allfeedback\"> \n");
					sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.fdbk.submit.byteam") + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=allfeedback\">" + propMap.get("filter.fdbk.submit.byteam") + "\n");
					sbview.append("</a> \n");
					sbview.append("</td>\n");
					sbview.append("</tr>\n");

				} else {

					sbview.append("<tr>\n");
					sbview.append("<td valign=\"top\">\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=allfeedback\"> \n");
					sbview.append("<img src=\"" + Defines.ICON_ROOT  + "fw.gif\" alt=\"" + propMap.get("filter.fdbk.submit.byteam.viewrole") + "\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a>\n");
					sbview.append("<a href=\"ETSProjectsServlet.wss?proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&actionType=feedback&subactionType=allfeedback\">" + propMap.get("filter.fdbk.submit.byteam.viewrole") + "\n");
					sbview.append("</a> \n");
					sbview.append("</td>\n");
					sbview.append("</tr>\n");

				}

				sbview.append("</table>\n");

			} //show only if it is not blade project

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeFeedBack", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();

	}

	/**
		 * 
		 * @param issfilterkey
		 * @return
		 */

	public String printWelcomeIssueTypes(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();

		try {

			sbview.append("<table summary=\"issues\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");

			//create not for visitir or executive

			//request to create new issues type only for Super Workspace Admin, Workspace Owner//
			if (usrRolesModel.isUsrReqIssueType()) {

				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"addIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=700&actionType=addIssType&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("add.newissue.type") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"addIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=700&actionType=addIssType&tc=" + tc + "\">" + propMap.get("add.newissue.type") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");

				///update issue type
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"updIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=1100&actionType=updIssType&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("upd.issue.type") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"updIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=1100&actionType=updIssType&tc=" + tc + "\">" + propMap.get("upd.issue.type") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");

				///delete issue type
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"delIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=900&actionType=delIssType&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("del.issue.type") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"delIssType.wss?proj=" + etsprojid + "&linkid=" + linkid + "&op=900&actionType=delIssType&tc=" + tc + "\">" + propMap.get("del.issue.type") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");

				//list all issue type info
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"listIssTypeInfo.wss?istyp=iss&proj=" + etsprojid + "&linkid=" + linkid + "&opn=1500&actionType=listIssTypeInfo&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("list.issue.types.info") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"listIssTypeInfo.wss?istyp=iss&proj=" + etsprojid + "&linkid=" + linkid + "&opn=1500&actionType=listIssTypeInfo&tc=" + tc + "\">" + propMap.get("list.issue.types.info") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");

			}

			///
			sbview.append("</table> \n");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeIssues", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();
	}

	/**
		 * 
		 * @param issfilterkey
		 * @return
		 */

	public String printSrchByNum(EtsIssFilterObjectKey issfilterkey, String srchErrMsg) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		boolean bladeUsrInt = false;

		try {

			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			String srchMsg = (String) propMap.get("filter.welc.srchiss.msg");

			sbview.append("<form method=\"post\" name=\"issuesform\" action=\"issSrchByNum.wss\" >");

			sbview.append("<input type=\"hidden\" name=\"proj\" value=\"" + AmtCommonUtils.getTrimStr((String) issfilterkey.getProj().getProjectId()) + "\" />\n");
			sbview.append("<input type=\"hidden\" name=\"tc\" value=\"" + AmtCommonUtils.getTrimStr(tc) + "\" />\n");
			sbview.append("<input type=\"hidden\" name=\"linkid\" value=\"" + AmtCommonUtils.getTrimStr(linkid) + "\"" + " />\n");
			sbview.append("<input type=\"hidden\" name=\"actionType\" value=\"searchByNum\" />\n");
			sbview.append("<input type=\"hidden\" name=\"istyp\" value=\"iss\" />\n");
			sbview.append("<input type=\"hidden\" name=\"opn\" value=\"20\" />\n");

			sbview.append("<table summary=\"issues\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");

			if (AmtCommonUtils.isResourceDefined(srchErrMsg)) {

				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"60%\" height=\"18\">&nbsp;\n");
				sbview.append("</td>\n");

				sbview.append("<td  valign=\"top\" align=\"left\" width=\"10%\" height=\"18\">\n");
				sbview.append("</td>\n");

				sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\" height=\"18\">\n");
				sbview.append("<span style=\"color:#ff3333\">" + srchErrMsg + "</span>");
				sbview.append("</td>");

				sbview.append("</tr>");
			}

			sbview.append("<tr >\n");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"60%\" height=\"18\">\n");
			sbview.append(srchMsg);
			sbview.append("</td>\n");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"10%\" height=\"18\">\n");
			sbview.append("</td>\n");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\" height=\"18\">\n");

			sbview.append("<table summary=\"issues\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n");
			sbview.append("<tr height=\"18\">\n");
			sbview.append("<td height=\"18\" nowrap=\"nowrap\" >\n");
			sbview.append("<label for=\"tl\"><b>Issue ID</b>:&nbsp;&nbsp;&nbsp;</label>");
			sbview.append("</td>\n");
			sbview.append("<td height=\"18\" nowrap=\"nowrap\">\n");
			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"10\" name=\"isssrchnum\" size=\"10\" src=\"\" type=\"text\" style=\"width:60px\" width=\"60px\" value=\"\" />\n");
			sbview.append("</td>\n");
			sbview.append("<td height=\"18\" nowrap=\"nowrap\">&nbsp;</td>\n");
			sbview.append("<td height=\"18\" nowrap=\"nowrap\">\n");
			sbview.append("&nbsp;&nbsp;<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/search_t.gif\" alt=\"Search\" name=\"srchissueid\" value=\"Search\" />\n");
			sbview.append("</td>\n");
			sbview.append("</tr>");
			sbview.append("<tr>\n");
			sbview.append("<td  valign=\"top\" align=\"left\" colspan=\"4\"><span class=\"small\">[ maximum 10 characters ]</span></td>");
			sbview.append("</tr>");
			sbview.append("</table>");

			sbview.append("</td>");

			sbview.append("</tr>\n");

			//			sbview.append("<tr>");
			//			sbview.append("<td colspan=\"2\" height=\"18\">&nbsp;</td>");
			//			sbview.append("</tr>");

			///
			sbview.append("</table> \n");
			sbview.append("</form>\n");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeIssues", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return sbview.toString();
	}

} //end of class
