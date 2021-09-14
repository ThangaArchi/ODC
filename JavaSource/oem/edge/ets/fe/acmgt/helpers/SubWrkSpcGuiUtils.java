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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.amt.*;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.bdlg.SubWrkSpcProcBdlg;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterGuiUtils;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SubWrkSpcGuiUtils {

	private static Log logger = EtsLogger.getLogger(SubWrkSpcGuiUtils.class);
	public static final String VERSION = "1.2";

	/**
	 * 
	 */
	public SubWrkSpcGuiUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String printUserList(ArrayList prevUserList, String sProjId, String parentProjId, String topCatStr, String linkid) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		SubWrkSpcProcBdlg procBdlg = new SubWrkSpcProcBdlg();

		ArrayList userList = procBdlg.getActiveMemList(parentProjId, sProjId);

		int usize = 0;

		if (userList != null && !userList.isEmpty()) {

			usize = userList.size();
		}

		if (usize > 0) {

			sb.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"600\"> ");

			sb.append("<tr>");
			sb.append("<td headers=\"\" colspan=\"3\" valign=\"top\">Please select the users from the list and click 'Continue'.</td>");
			sb.append("</tr>");

			// divider
			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			sb.append("</tr>");
		

			sb.append("</table>");

		}

		if (usize > 0) {

			sb.append(printUserList(userList, prevUserList));

		} else {

			sb.append("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"443\">");
			sb.append("<tr valign=\"top\">");
			sb.append("<td  align=\"left\">");
			sb.append("Currently, there are no members available to be added to sub workspace. Please click 'Back to team listing'. ");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");

		}

		sb.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"600\"> ");

		// divider
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"3\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
		if (usize > 0) {
			sb.append("<td width=\"140\" align=\"left\"><input type=\"image\" name=\"sbwspc_add_cont1\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			sb.append("<td  align=\"left\" width=\"70\">&nbsp;</td>");
			sb.append("<td  align=\"left\">&nbsp;</td>");
		}
		sb.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		sb.append("</td> ");
		sb.append("<td  align=\"left\" valign=\"top\" >");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + topCatStr + "&cc=" + topCatStr + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		sb.append("</td> ");
		sb.append("</td></tr></table>");

		sb.append("<br /><br />");

		return sb.toString();

	}

	public String printUserList(ArrayList userList, ArrayList prevUserList) {

		StringBuffer sbsub = new StringBuffer();

		EtsIssFilterGuiUtils fltrGuiUtils = new EtsIssFilterGuiUtils();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");

		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr>\n");
		sbsub.append("<td nowrap=\"nowrap\"  valign=\"top\" width=\"100\" >&nbsp;</td>");
		sbsub.append("<td  height=\"18\" ><span class=\"small\">[ The list shows only those users who are not part of subworkspace. ]</span></td></tr>\n");
		sbsub.append("<tr valign=\"top\" >\n");
		sbsub.append("<td nowrap=\"nowrap\"  valign=\"top\" width=\"100\" ><b><label for=\"label_id\">IBM ID(s):</label></b></td>");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"500\">\n");
		sbsub.append("<select id=\"label_id\" multiple=\"multiple\" size=\"5\" name=\"userlist\"  align=\"left\" class=\"iform\" style=\"width:500px\" width=\"500px\"  >\n");
		sbsub.append(fltrGuiUtils.printSelectOptionsWithValue(userList, prevUserList));
		sbsub.append("</select>\n");
		sbsub.append("</td>\n");
		sbsub.append("</tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr>\n");
		sbsub.append("<td nowrap=\"nowrap\"  valign=\"top\" width=\"100\" >&nbsp;</td>");
		sbsub.append("<td  height=\"18\" ><span class=\"small\">[ To select more than one name, hold Ctrl and click the names. ]</span></td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("</table>\n");

		return sbsub.toString();

	}

	public String printUserDetails(ArrayList selectUserList, String projectId, int TopCatId, String linkid, ArrayList prevUserInfoList, String errMsg) {

		StringBuffer buf = new StringBuffer();
		int size = 0;
		int psize = 0;
		String bgcolor = "background-color:#eeeeee";

		boolean prevuserinfo = false;

		String defPrivilege="";
		String prevPrivilege = "";
		String prevJobResp = "";

		if (selectUserList != null && !selectUserList.isEmpty()) {

			size = selectUserList.size();
		}

		if (prevUserInfoList != null && !prevUserInfoList.isEmpty()) {

			psize = prevUserInfoList.size();
		}

		if (psize == size) {

			prevuserinfo = true;
		}

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			buf.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");

			buf.append("<tr>");
			buf.append("<td  colspan=\"2\" valign=\"top\" >&nbsp;</td>");
			buf.append("</tr>");

			buf.append("<tr>");
			buf.append("<td  colspan=\"2\" valign=\"top\" ><span style=\"color:#ff3333\">" + errMsg + "</span></td>");
			buf.append("</tr>");

			buf.append("<tr>");
			buf.append("<td  colspan=\"2\" valign=\"top\" >&nbsp;</td>");
			buf.append("</tr>");

			buf.append("</table>");

		}

		//		alerts
		buf.append("<form name=\"AddMember\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");
		buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\"><tr>");
		buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

		buf.append("<tr>");
		buf.append("<td>");
		buf.append("Please assign required privilege to the user(s) and click 'Submit' to add users to the workspace.");
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");

		buf.append("<br />");
		buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\"><tr>");

		buf.append("<tr><td colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

		buf.append("<tr>");
		buf.append("<th id=\"list_name\" align=\"left\" valign=\"bottom\" height=\"16\">Name</th>");
		buf.append("<th id=\"list_blnk1\" align=\"left\" valign=\"bottom\" height=\"16\">&nbsp;</th>");
		buf.append("<th id=\"list_id\" align=\"left\" valign=\"bottom\" height=\"16\">Userid</th>");
		buf.append("<th id=\"list_blnk2\" align=\"left\" valign=\"bottom\" height=\"16\">&nbsp;</th>");
		buf.append("<th id=\"list_priv\" align=\"left\" valign=\"bottom\" height=\"16\">Privilege</th>");
		buf.append("<th id=\"list_blnk3\" align=\"left\" valign=\"bottom\" height=\"16\">&nbsp;</th>");
		buf.append("<th id=\"list_job\" align=\"left\" valign=\"bottom\" height=\"16\">Job responsibility</th>");

		buf.append("</tr>");

		int incr = 0;

		ETSUser prevMemb = new ETSUser();

		for (int i = 0; i < size; i++) {
			//get amt information
			ETSUser memb = (ETSUser) selectUserList.get(i);

			if (prevuserinfo) {

				prevMemb = (ETSUser) prevUserInfoList.get(i);
				prevPrivilege = AmtCommonUtils.getTrimStr(prevMemb.getRoleName());
				prevJobResp = AmtCommonUtils.getTrimStr(prevMemb.getUserJob());
			}

			String username = memb.getUserName();

			if (bgcolor.equals("background-color:#eeeeee")) {

				buf.append("<tr style=\"background-color:#eeeeee\" >");

			} else {

				buf.append("<tr>");
			}

			buf.append("<td headers=\"list_name\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
			buf.append("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=memberdetails&uid=" + memb.getUserId() + "&proj=" + projectId + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><span class=\"small\">" + username + "</span></a>");
			buf.append("</td>");

			buf.append("<td headers=\"list_blnk1\" height=\"17\" align=\"left\" valign=\"top\">&nbsp;</td>");

			buf.append("<td headers=\"list_id\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
			buf.append(memb.getUserId());
			buf.append("</td>");

			buf.append("<td headers=\"list_blnk2\" height=\"17\" align=\"left\" valign=\"top\">&nbsp;</td>");

			buf.append("<td headers=\"list_priv\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
			buf.append("<input type=\"hidden\" name=\"swuid" + i + "\" value=\"" + memb.getUserId() + "\" />");
			
			defPrivilege=AmtCommonUtils.getTrimStr(memb.getRoleName());
			
			
			//if (!prevuserinfo) {
				buf.append(printPrivileges(projectId, defPrivilege, memb.getUserType(), AmtCommonUtils.getTrimStr("" + i)));
//			} else {
//
//				buf.append(printPrivileges(projectId, AmtCommonUtils.getTrimStr(prevMemb.getRoleName()), memb.getUserType(), AmtCommonUtils.getTrimStr("" + i)));
//			}
			buf.append("</td>");

			buf.append("<td headers=\"list_blnk3\" height=\"17\" align=\"left\" valign=\"top\">&nbsp;</td>");

			buf.append("<td headers=\"list_job\" class=\"small\" height=\"17\" align=\"left\" valign=\"top\" >");
			buf.append(printStdText("joblblid" + i, memb.getUserId(), "jobresp" + i, prevJobResp));
			buf.append("</td>");

			buf.append("</tr>");

			if (bgcolor.equals("background-color:#eeeeee")) {

				bgcolor = "";

			} else {

				bgcolor = "background-color:#eeeeee";

			}

			incr++;

		} //end of count

		buf.append("<tr valign=\"top\">");
		buf.append("<td colspan=\"7\">&nbsp;</td>");
		buf.append("</tr>");

		// divider
		buf.append("<tr valign=\"top\">");
		buf.append("<td colspan=\"7\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td>");
		buf.append("</tr>");
		buf.append("<tr valign=\"top\">");
		buf.append("<td colspan=\"7\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		buf.append("</tr>");
		buf.append("</table>");

		buf.append("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"600\"><tr>");
		buf.append("<td  width=\"140\" align=\"left\"><input type=\"image\" name=\"sbwspc_add_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
		buf.append("<td  align=\"left\" width=\"70\">&nbsp;</td>");
		buf.append("<td  width=\"25\" valign=\"middle\" align=\"left\">\n");
		buf.append("<input type=\"image\" name=\"sbwspc_add_back\" src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Back\" border=\"0\" />\n");
		buf.append("</td>\n");
		buf.append("<td  align=\"left\">Back</td>\n");
		buf.append("<td  align=\"left\">&nbsp;</td>");
		buf.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + projectId + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		buf.append("</td> ");
		buf.append("<td  align=\"left\" valign=\"top\" >");
		buf.append("<a href=\"ETSProjectsServlet.wss?proj=" + projectId + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		buf.append("</td> ");
		buf.append("</td></tr></table>");

		buf.append("<br /><br />");

		buf.append("<input type=\"hidden\" name=\"uidcount\" value=\"" + incr + "\" />");
		buf.append("<input type=\"hidden\" name=\"proj\" value=\"" + projectId + "\" />");
		buf.append("<input type=\"hidden\" name=\"tc\" value=\"" + TopCatId + "\" />");
		buf.append("<input type=\"hidden\" name=\"linkid\" value=\"" + linkid + "\" />");
		buf.append("<input type=\"hidden\" name=\"selec_userlist\" value=\"" + linkid + "\" />");
		buf.append("<input type=\"hidden\" name=\"action\" value=\"subwrkspc_add\" />");
		buf.append("</form>");

		return buf.toString();
	}

	public String printPrivileges(String projectId, String prevRoles, String userType, String iter) {

		StringBuffer sb = new StringBuffer();
		SubWrkSpcProcBdlg wrkSpcBdlg = new SubWrkSpcProcBdlg();

		try {

			Vector r = ETSDatabaseManager.getRolesPrivs(projectId);

			ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean();

			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"150\" border=\"0\">");

			for (int i = 0; i < r.size(); i++) {
				String[] rp = (String[]) r.elementAt(i);
				int roleid = (new Integer(rp[0])).intValue();
				String rolename = rp[1];
				//String privs = rp[2];
				String privids = rp[3];

				//if (!userType.equals("I") || wrkSpcBdlg.isWrkSpcMgrDefnd(projectId)) {
				if (!userType.equals("I")) {

					if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.ADMIN)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.MANAGE_USERS))) {
						sb.append("<tr>");
						sb.append("<td align=\"left\" width=\"3%\" >");
						if (prevRoles.equals(rolename)) {

							sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles" + iter + "\" value=\"" + roleid + "\" checked=\"checked\" />");
						} else {

							sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles" + iter + "\" value=\"" + roleid + "\"  />");
						}

						sb.append("</td>");
						sb.append("<td align=\"left\" width=\"97%\" ><label for=\"role_" + i + "\">" + rolename + "</label></td>");
						sb.append("</tr>");

						sb.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
					}

				} //end of I

				else {

					if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER))) {
						sb.append("<tr>");
						sb.append("<td align=\"left\" width=\"3%\" >");
						if (prevRoles.equals(rolename)) {

							sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles" + iter + "\" value=\"" + roleid + "\" checked=\"checked\" />");
						} else {

							sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles" + iter + "\" value=\"" + roleid + "\"  />");
						}

						sb.append("</td>");
						sb.append("<td align=\"left\" width=\"97%\" ><label for=\"role_" + i + "\">" + rolename + "</label></td>");
						sb.append("</tr>");
						sb.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
					}
				}
			} //END OF loop

			sb.append("</table>");

		} catch (SQLException sqlEx) {

			logger.error("SQLException in printPrivileges", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in printPrivileges", ex);
			ex.printStackTrace();

		}

		return sb.toString();

	}

	public String printStdText(String fieldId, String userId, String txtFldName, String prevValue) {

		StringBuffer sb = new StringBuffer();

		sb.append(AmtErrorHandler.printImgLabel(fieldId, "Job Responsibility for Userid " + userId));
		sb.append("<input type=\"text\" id=\"" + fieldId + "\"  name=\"" + txtFldName + "\" value=\"" + prevValue + "\" size=\"35\" maxlength=\"64\"  class=\"iform\" style=\"width:100px\" width=\"100px\" >\n");

		return sb.toString();
	}

	public String printConfirmPage(String sProjId, int TopCatId, String linkid, String msg, boolean succ) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		sb.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"600\"> ");

		sb.append("<tr>");
		if (succ) {

			sb.append("<td  colspan=\"3\" valign=\"top\">" + msg + "</td>");
		} else {
			sb.append("<td  colspan=\"3\" valign=\"top\"><span style=\"color:#ff3333\">" + msg + "</span></td>");
		}
		sb.append("</tr>");

		// divider
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		sb.append("</tr>");

		sb.append("</table>");

		sb.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"600\"> ");

		// divider
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"3\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" height=\"1\" width=\"600\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("<table summary=\"\" border=\"0\" cellpadding\"0\" cellspacing=\"0\" width=\"443\"><tr>");
		sb.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		sb.append("</td> ");
		sb.append("<td  align=\"left\" valign=\"top\" >");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + sProjId + "&tc=" + TopCatId + "&cc=" + TopCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		sb.append("</td> ");
		sb.append("</td></tr></table>");

		sb.append("<br /><br />");

		return sb.toString();

	}

} //end of class
