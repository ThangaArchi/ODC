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
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.PrintFriendlyHeaderFooter;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.ismgt.dao.CommonInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssCommonGuiUtils implements EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.24.2.23";

	/**
	 * 
	 */
	public EtsIssCommonGuiUtils() {
		super();
	}

	/**
		 * This method will print Section Headers required for each of section in blue
		 */

	public String printSectnHeader(String headerName) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tbody>\n");
		sb.append("<tr>\n");
		sb.append("<td class=\"tblue\" height=\"18\">&nbsp;" + headerName + "</td>\n");
		sb.append("</tr>\n");
		sb.append("</tbody>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/**
				 * print grey line
				 */

	public String printBlackLine(String ht) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"443\">");
		sbview.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/black_rule.gif\" alt=\"\" width=\"443\" height=\"" + ht + "\" /></td></tr>");
		sbview.append("</table> ");

		return sbview.toString();

	}

	/**
	 * 
	 * @param height
	 * @return
	 */

	public String printBlankLine(String height) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
		sbview.append("<tr>\n");
		sbview.append("<td  width=\"443\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"" + height + "\" alt=\"\" />\n");
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
	 * 
	 * @param helpStr
	 * @return
	 */

	public String printBlurb(String helpStr) {

		StringBuffer sb = new StringBuffer();

		String s = AmtCommonUtils.getTrimStr(helpStr);

		sb.append("&nbsp;&nbsp;<a name=\"BLB" + s.trim() + "\" href=\"#BLB" + s.trim() + "\" ");
		sb.append(" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=" + s.trim() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450');return false\" ");
		sb.append(" onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=" + s.trim() + "','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\" ><img border=\"0\" name=\"Help\" src=\"" + Global.WebRoot + "/images/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");

		return sb.toString();
	}

	/**
	 * 
	 * @param cancName
	 * @return
	 */

	public String printCancel(String cancName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td  width=\"25\" valign=\"middle\" align=\"left\">\n");
		sbview.append("<input type=\"image\" name=\"" + cancName + "\" src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" />\n");
		sbview.append("</td>\n");
		sbview.append("<td  align=\"left\">Cancel</td>\n");
		sbview.append("</tr></table>\n");

		return sbview.toString();
	}

	/**
	 * 
	 * @param subName
	 * @return
	 */

	public String printSubmit(String subName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<input type=\"image\" name=\"" + subName + "\" src=\"" + Defines.BUTTON_ROOT  + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" />");

		return sbview.toString();

	}

	/**
		 * 
		 * @param contnName
		 * @return
		 */

	public String printContinue(String contnName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<input type=\"image\" name=\"" + contnName + "\" src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" />");

		return sbview.toString();
	}

	/**
		 * To print continue and cancel buttons
		 * @param contnName
		 * @param cancName
		 * @return
		 */

	public String printContCancel(String contnName, String cancName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(printContinue(contnName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(printCancel(cancName));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/***
			 * 
			 * display Edit Issue Btn Comments  tag
			 */

	public String printEditIssueBtn(String imgName, String editBtnLabel) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\"> ");
		sbview.append("<tr>");
		sbview.append("<td  align=\"left\">");
		sbview.append("<input type=\"image\"  name=\"" + imgName + "\" src=\"" + Defines.BUTTON_ROOT  + "arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"" + editBtnLabel + "\" />");
		sbview.append("</td>");
		sbview.append("<td  valign=\"middle\" align=\"left\" nowrap=\"nowrap\">");
		sbview.append("&nbsp;<b>" + editBtnLabel + "<b>");
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("<tr><td  align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"3\" width=\"21\" alt=\"\" /></td></tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/***
				 * 
				 * display Edit Issue Btn Comments  tag
				 */

	public String printForwardActnBtn(String imgName, String editBtnLabel) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\"> ");
		sbview.append("<tr>");
		sbview.append("<td  align=\"left\">");
		sbview.append("<input type=\"image\"  name=\"" + imgName + "\" src=\"" + Defines.BUTTON_ROOT  + "arrow_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"" + editBtnLabel + "\" />");
		sbview.append("</td>");
		sbview.append("<td  valign=\"middle\" align=\"left\" nowrap=\"nowrap\">");
		sbview.append("&nbsp;<b>" + editBtnLabel + "<b>");
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("<tr><td  align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"3\" width=\"21\" alt=\"\" /></td></tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/**
			 * print grey line
			 */

	public String printGreyLine() {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sbview.append("<tr><td ><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"443\" height=\"1\" /></td></tr>");
		sbview.append("</table> ");

		return sbview.toString();

	}

	/**
			 * to print mandatory  msg tables
			 */

	public String printMandtMsg() {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
		sbview.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"443\" height=\"1\" /></td></tr>");
		sbview.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>");
		sbview.append("<tr>");
		sbview.append("<td  colspan=\"2\" valign=\"top\" align=\"left\">");
		sbview.append("<span class=\"small\">[ Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory. ]</span>");
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/**
		 * 
		 */

	public String printProcessMsg(String procMsg) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
		sbview.append("<tr>\n");
		sbview.append("<td  height=\"18\" width=\"443\">\n");
		sbview.append(procMsg);
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("<tr><td >&nbsp;</td></tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();

	}

	public String printReturnToMainPage(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append(
			"<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp="
				+ etsIssObjKey.getIstyp()
				+ "&opn=10"
				+ "&proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\""
				+ "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	public String printReturnToMainPageWithCancel(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"15%\">");
		sbsub.append("<tr>");
		sbsub.append(
			"<td  ><a href=\"EtsIssFilterCntrlServlet.wss?istyp="
				+ etsIssObjKey.getIstyp()
				+ "&opn="
				+ etsIssObjKey.getFilopn()
				+ "&proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a>&nbsp;</td>");
		sbsub.append("<td  ><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=" + etsIssObjKey.getFilopn() + "&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\">Cancel</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");

		return sbsub.toString();

	}

	public String printReturnToViewPage(EtsIssObjectKey etsIssObjKey, String edgeProblemId) {

		StringBuffer sbsub = new StringBuffer();

		String srt = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("srt"));

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"15%\">");
		sbsub.append("<tr>");
		sbsub.append(
			"<td  ><a href=\"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&sc=0&actionType=viewIssue&op=61&edge_problem_id="
				+ edgeProblemId
				+ "&srt="
				+ srt
				+ "&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a>&nbsp;</td>");
		sbsub.append(
			"<td  ><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&sc=0&actionType=viewIssue&op=61&srt=" + srt + "&edge_problem_id=" + edgeProblemId + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "\">Cancel</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");

		return sbsub.toString();

	}

	public String printReturnToViewPage(EtsIssObjectKey etsIssObjKey, String edgeProblemId, String retName) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"15%\">");
		sbsub.append("<tr>");
		sbsub.append(
			"<td  ><a href=\"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&sc=0&actionType=viewIssue&op=61&edge_problem_id="
				+ edgeProblemId
				+ "&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\""
				+ retName
				+ "\" /></a>&nbsp;</td>");
		sbsub.append("<td  ><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&sc=0&actionType=viewIssue&op=61&edge_problem_id=" + edgeProblemId + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "\">" + retName + "</a></td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");

		return sbsub.toString();

	}

	/**
			 * To print the error msg 
			 */

	public String printStdErrMsg(String errMsg) {

		StringBuffer sbview = new StringBuffer();

		if (AmtCommonUtils.isResourceDefined(errMsg)) {

			sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" >");
			sbview.append("<span style=\"color:#ff3333\">" + errMsg + "</span>");
			sbview.append("</td>");
			sbview.append("</tr>");
			//sbview.append("<tr><td>&nbsp;</td></tr>");
			sbview.append("</table>");
		}

		return sbview.toString();

	}

	/**
			 * To print the text area
			 */

	public String printStdTextArea(String fieldId, String txtAreaName, String prevTextAreaVal) {

		StringBuffer sbview = new StringBuffer();

		if (!AmtCommonUtils.isResourceDefined(prevTextAreaVal)) {

			sbview.append("<textarea id=\"" + fieldId + "\"  name=\"" + txtAreaName + "\" size=\"1000\" rows=\"9\" cols=\"35\"  class=\"iform\" style=\"width:323px\" width=\"323px\" >\n");
			sbview.append("</textarea>\n");

		} else {

			sbview.append("<textarea id=\"" + fieldId + "\"  name=\"" + txtAreaName + "\" size=\"1000\" rows=\"9\" cols=\"35\"  class=\"iform\" style=\"width:323px\" width=\"323px\" >\n");
			sbview.append(prevTextAreaVal);
			sbview.append("</textarea>\n");
		}

		return sbview.toString();

	}

	/**
				 * To print the text area
				 */

	public String printStdCommentsTextArea(String fieldId, String txtAreaName, String prevTextAreaVal) {

		StringBuffer sbview = new StringBuffer();

		if (!AmtCommonUtils.isResourceDefined(prevTextAreaVal)) {

			sbview.append("<textarea id=\"" + fieldId + "\"  name=\"" + txtAreaName + "\"   size=\"1000\"  rows=\"" + COMMENT_ROWS + "\" cols=\"" + COMMENT_COLS + "\"  class=\"iform\" style=\"width:600px\" width=\"600px\" >\n");
			sbview.append("</textarea>\n");

		} else {

			sbview.append("<textarea id=\"" + fieldId + "\"  name=\"" + txtAreaName + "\"  size=\"1000\"  rows=\"" + COMMENT_ROWS + "\" cols=\"" + COMMENT_COLS + "\"  class=\"iform\" style=\"width:600px\" width=\"600px\" >\n");
			sbview.append(prevTextAreaVal);
			sbview.append("</textarea>\n");
		}

		return sbview.toString();

	}

	/**
			 * To print continue and cancel buttons
			 * @param contnName
			 * @param cancName
			 * @return
			 */

	public String printSubmitCancel(String subName, String cancName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(printSubmit(subName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(printCancel(cancName));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
				 * To print continue and cancel buttons
				 * @param contnName
				 * @param cancName
				 * @return
				 */

	public String printSubmitRetToView(String subName, EtsIssObjectKey etsIssObjKey, String edgeProblemId) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(printSubmit(subName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(printReturnToViewPage(etsIssObjKey, edgeProblemId));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
					 * To print continue and cancel buttons
					 * @param contnName
					 * @param cancName
					 * @return
					 */

	public String printSubmitRetToMainPage(String subName, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(printSubmit(subName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(printReturnToMainPageWithCancel(etsIssObjKey));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
						 * To print continue and cancel buttons
						 * @param contnName
						 * @param cancName
						 * @return
						 */

	public String printContinueRetToMainPage(String contName, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(printContinue(contName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(printReturnToMainPageWithCancel(etsIssObjKey));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
		 * 
		 * @param subtitle
		 * @return
		 */

	public String printSubTitle(String subtitle) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"443\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td  height=\"18\" width=\"443\" class=\"subtitle\">" + subtitle + "</td>\n");
		sbview.append("</tr>\n");
		sbview.append("<tr><td >&nbsp;</td></tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
			 * to get issue type label
			 */
	public String getDefaultIssueTypeLabel(EtsIssObjectKey etsIssObjKey) {

		HashMap defLabelMap = etsIssObjKey.getFormLabelMap();

		return getDefaultIssueTypeLabel(defLabelMap);
	}

	/**
		 * to get issue type label
		 */
	public String getDefaultSubTypeStr(EtsIssObjectKey etsIssObjKey, String stdSubType, String defaultStdSubType) {

		HashMap defLabelMap = etsIssObjKey.getFormLabelMap();

		return getDefaultSubTypeStr(defLabelMap, stdSubType, defaultStdSubType);
	}

	/**
			 * to get issue val for a given pattern
			 * token format >> 	DATA_ID $ ISSUE_TYPE $ ISSUE_SOURCE $ ISSUE_ACCESS $ SUBTYPE_A
			 */

	public String getDelimitIssueVal(String prob_type) throws Exception {

		return EtsIssFilterUtils.getDelimitIssueVal(prob_type);

	}

	/**
			 * 
			 * To parse the problem type and returns the issue type
			 * @param prob_type
			 * @return
			 * @throws Exception
			 */

	public EtsDropDownDataBean getIssueTypeDropDownAttrib(String prob_type) throws Exception {

		return EtsIssFilterUtils.getIssueTypeDropDownAttrib(prob_type);

	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @return
	 */

	public String printSecureContentHeader(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sHeader = new StringBuffer();
		ETSProj etsProj = etsIssObjKey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(etsProj.getProjectType().trim());
		
		//v2sagar
		String sDate = "";
		sDate=UserProfileTimeZone.getUTCHeaderDate();

		boolean isITAR = etsIssObjKey.getProj().isITAR();
		
		if(etsProj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)){
				if((etsIssObjKey.getEs().gDECAFTYPE.trim().toUpperCase().equals("I")) && (isProjectIBMOnly(etsProj.getProjectId()).equalsIgnoreCase("N"))){ 
					sHeader.append("<table><tr><td align=\"right\"><span style=\"color:#006400;font-size:10\"><b>Note: This workspace may also be used by external customers.</b></span></td></tr></table>");
				}
		}
		
		sHeader.append("<table><tr><td align=\"right\" ><a href=\"" + Defines.SERVLET_PATH + prop.getLandingPageURL() + "?linkid=" + prop.getLinkID() + "\">Switch workspace</a></td></tr></table>");

		//v2sagar remove the hard coded path...
		
		//sHeader.append("<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:9080/technologyconnect/ets/js/UserTimeZoneCookie.js\"></script>");
	
		sHeader.append("<script type=\"text/javascript\" language=\"javascript\" src=\""+Global.WebRoot+"/js/UserTimeZoneCookie.js\"></script>");

		sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
	
		sHeader.append("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><" +
			"table summary=\"\" border=\"0\" width=\"100%\">" +
			"<tr>" +
			"<td headers=\"\" width=\"60%\">" + etsIssObjKey.getEs().gIR_USERN + "</td>" );

		//Server's Time				
		/*sHeader.append("<td headers=\"\" width=\"40%\" align=\"right\"><div id=\"sTime\">" + sDate + "<a href=\""
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone\" target=\"new\" onclick=\"window.open('"
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\" onkeypress=\"window.open('"
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\">" 
				+ "<img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Timezone help\" border=\"0\" /></a></div></td>");
				*/
		sHeader.append("<td headers=\"\" width=\"25%\" align=\"right\"><div id=\"sTime\">" + sDate +"</div></td>");
		sHeader.append("<script type=\"text/javascript\"  language=\"javascript\">"); 
		sHeader.append("var newTime;");		
		sHeader.append("  newTime =firstTime();");		
		sHeader.append("document.getElementById(\"sTime\").innerHTML = newTime;");
		sHeader.append("</script>");
		
		sHeader.append("</tr>" +
			"</table>" +
			"</td>");
//till here v2sagar

				
		if (isITAR) {
              
			sHeader.append("<td class=\"small\" align=\"left\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"16\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" /></td>");
			sHeader.append("<td  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">");
			sHeader.append("<span style=\"color:#ff3333\">This workspace contains ITAR unclassified data</span></a>");
			sHeader.append("</td>");
			sHeader.append("</tr>");
			sHeader.append("</table>");
			sHeader.append("</td>");

			}else{

			sHeader.append("<td width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			sHeader.append("<td class=\"small\" align=\"right\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td>");
			sHeader.append("<td  width=\"90\" align=\"right\">Secure content</td>");
			sHeader.append("</tr></table>");
			sHeader.append("</td>");
		  }
		  
		  sHeader.append("</tr>");
		  sHeader.append("</table>");
				
		return sHeader.toString();
	}

	/**
		 * 
		 * @param etsIssObjKey
		 * @return
		 */

	public String printSecureContentHeader(EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sHeader = new StringBuffer();

		//v2sagar
		String sDate = "";
		sDate=UserProfileTimeZone.getUTCHeaderDate();

		boolean isITAR = issfilterkey.getProj().isITAR();
				
		ETSProj etsProj = issfilterkey.getProj();
		UnbrandedProperties prop = PropertyFactory.getProperty(etsProj.getProjectType().trim());
		
		if(etsProj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)){
				if((issfilterkey.getEs().gDECAFTYPE.trim().toUpperCase().equals("I")) && (isProjectIBMOnly(etsProj.getProjectId()).equalsIgnoreCase("N"))){ 
					sHeader.append("<table><tr><td align=\"right\"><span style=\"color:#006400;font-size:10\"><b>Note: This workspace may also be used by external customers.</b></span></td></tr></table>");
				}
		}
		
		sHeader.append("<table><tr><td align=\"right\" ><a href=\"" + Defines.SERVLET_PATH + prop.getLandingPageURL() + "?linkid=" + prop.getLinkID() + "\">Switch workspace</a></td></tr></table>");

		
		
		//v2sagar remove the hard coded path...
		
		//sHeader.append("<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:9080/technologyconnect/ets/js/UserTimeZoneCookie.js\"></script>");
	
		sHeader.append("<script type=\"text/javascript\" language=\"javascript\" src=\""+Global.WebRoot+"/js/UserTimeZoneCookie.js\"></script>");

		sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
	
		sHeader.append("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><" +
			"table summary=\"\" border=\"0\" width=\"100%\">" +
			"<tr>" +
			"<td headers=\"\" width=\"60%\">" + issfilterkey.getEs().gIR_USERN + "</td>" );

	//Server's Time				
		/*sHeader.append("<td headers=\"\" width=\"40%\" align=\"right\"><div id=\"sTime\">" + sDate + "<a href=\""
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone\" target=\"new\" onclick=\"window.open('"
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\" onkeypress=\"window.open('"
				+ Defines.SERVLET_PATH
				+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=400,height=400,left=300,top=150'); return false;\">" 
				+ "<img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Timezone help\" border=\"0\" /></a></div></td>");
				*/
		sHeader.append("<td headers=\"\" width=\"25%\" align=\"right\"><div id=\"sTime\">" + sDate +"</div></td>");
		sHeader.append("<script type=\"text/javascript\"  language=\"javascript\">"); 
		sHeader.append("var newTime;");		
		sHeader.append("  newTime =firstTime();");		
		sHeader.append("document.getElementById(\"sTime\").innerHTML = newTime;");
		sHeader.append("</script>");

		sHeader.append("</tr>" +
			"</table>" +
			"</td>");
//till here v2sagar

		sHeader.append("<td width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		
		if (isITAR) {

			sHeader.append("<td class=\"small\" align=\"left\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"16\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" /></td>");
			sHeader.append("<td  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=itar&proj_type=" + etsProj.getProjectType() + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">");
			sHeader.append("<span style=\"color:#ff3333\">This workspace contains ITAR unclassified data</span></a>");
			sHeader.append("</td>");
			sHeader.append("</tr>");
			sHeader.append("</table>");
			sHeader.append("</td>");

		 }else {

			sHeader.append("<td class=\"small\" align=\"right\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td>");
			sHeader.append("<td  width=\"90\" align=\"right\">Secure content</td>");
			sHeader.append("</tr></table>");
			sHeader.append("</td>");

		}
		
		sHeader.append("</tr>");
		sHeader.append("</table>");
				
		return sHeader.toString();
	}

	/**
		 * 
		 * @param etsIssObjKey
		 * @return
		 */

	public String printSecureContentFooter(boolean isITAR) {

		StringBuffer sHeader = new StringBuffer();

		if (!isITAR) {

			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"16\" align=\"left\">\n");
			sHeader.append("<img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" />");
			sHeader.append("</td>");
			sHeader.append("<td  align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span>");
			sHeader.append("</td></tr></table>");

		}

		return sHeader.toString();
	}

	public String printBookMarkStr(String sCaption, String sAnchor, boolean printbm) {

		return ETSUtils.getBookMarkString(sCaption, sAnchor, printbm);
	}

	/**
				 * to get issue type label
				 */
	public String getDefaultIssueTypeLabel(HashMap defLabelMap) {

		String issueTypeLabel = DEFUALTSTDISSUETYPE;

		if (EtsIssFilterUtils.isHashMapDefnd(defLabelMap)) {

			issueTypeLabel = (String) defLabelMap.get(STDISSUETYPE);

			if (!AmtCommonUtils.isResourceDefined(issueTypeLabel)) {

				issueTypeLabel = DEFUALTSTDISSUETYPE;
			}

		}

		return issueTypeLabel;
	}

	/**
		 * to get issue type label
		 */
	public String getDefaultSubTypeStr(HashMap defLabelMap, String stdSubType, String defaultStdSubType) {

		String issueTypeLabel = defaultStdSubType;

		if (EtsIssFilterUtils.isHashMapDefnd(defLabelMap)) {

			issueTypeLabel = (String) defLabelMap.get(stdSubType);

			if (!AmtCommonUtils.isResourceDefined(issueTypeLabel)) {

				issueTypeLabel = defaultStdSubType;
			}

		}

		return issueTypeLabel;
	}

	public String printPrinterFriendlyHeader(String sPageTitle) {

		PrintFriendlyHeaderFooter printHead = new PrintFriendlyHeaderFooter();

		return printHead.printerFriendlyHeader(sPageTitle);
	}

	public String printPrinterFriendlyFooter() {

		PrintFriendlyHeaderFooter printHead = new PrintFriendlyHeaderFooter();

		return printHead.printerFriendlyFooter();
	}

	/**
		 * print the common hidden vars reqd for EtsProjectServlet
		 */

	public String printCommonHidVars(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"proj\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getProj().getProjectId()) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"tc\" value=\"" + AmtCommonUtils.getTrimStr((String) String.valueOf(etsIssObjKey.getTopCatId())) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"linkid\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getSLink()) + "\"" + " />\n");
		sbsub.append("<input type=\"hidden\" name=\"actionType\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("actionType")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"edge_problem_id\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edge_problem_id")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"etsId\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("etsId")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"flop\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("flop")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"istyp\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("istyp")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"extbhf\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf")) + "\" />\n");
		sbsub.append("<input type=\"hidden\" name=\"srt\" value=\"" + AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("srt")) + "\" />\n");
		
		return sbsub.toString();
	}

	/**
			 * 
			 */
	public String printHidCancelActionState(EtsIssProbInfoUsr1Model usr1InfoModel) {

		return printHidCancelActionState(usr1InfoModel.getCancelActionState());
	}

	/**
	  * 
	   */
	public String printHidCancelActionState(int cancelActionState) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"cancelstate\" value=\"" + cancelActionState + "\" />\n");

		return sbsub.toString();
	}

	/**
			 * 
			 */

	public String printHidSubtypeActionState(EtsIssProbInfoUsr1Model usr1InfoModel) {

		return printHidSubtypeActionState(usr1InfoModel.getSubtypeActionState());
	}

	/**
				 * 
				 */

	public String printHidSubtypeActionState(int subtypeActionState) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"subtypestate\" value=\"" + subtypeActionState + "\" />\n");

		return sbsub.toString();
	}

	/**
		 * This method will print submitter details
		 * 
		 */

	public String printActionerDetails(String actionerName, EtsIssProjectMember projMember) {

		StringBuffer sb = new StringBuffer();

		String curIssueCustName = AmtCommonUtils.getTrimStr(projMember.getUserFullName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(projMember.getUserEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(projMember.getUserContPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(projMember.getUserCustCompany());

		sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>" + actionerName + "</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustName + "</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
		sb.append("</tr>\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>E-mail</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustEmail + "</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();
	}

	public boolean isStrResourceDefnd(String str) {

		return AmtCommonUtils.isResourceDefined(str);
	}

	public String getTrimStr(String str) {

		return AmtCommonUtils.getTrimStr(str);
	}

	/***
			 * 
			 * to print common actions(filter conds, and back ) on no recs pages
			 * 
			 */

	public String printPrinterFriendly(String viewUrl) {

		return printPrinterFriendly(viewUrl, "Printable version");

	}

	/***
				 * 
				 * to print common actions(filter conds, and back ) on no recs pages
				 * 
				 */

	public String printPrinterFriendly(String viewUrl, String printName) {

		StringBuffer sb = new StringBuffer();

		sb.append("<a  href=\"" + viewUrl + "\" target=\"new\" \n");
		sb.append(" onclick=\"window.open('" + viewUrl + "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=625,height=600,left=387,top=207'); return false;\" \n");
		sb.append(" onkeypress=\"window.open('" + viewUrl + "','Services','toolbar=1,scrollbars=1,location=0,statusbar=1,menubar=1,resizable=1,width=625,height=600,left=387,top=207'); return false;\"> \n");
		sb.append("<img src=\"" + Defines.ICON_ROOT  + "printer.gif\" alt=\"Printable version\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" />\n");
		sb.append("" + printName + "\n");
		sb.append("</a>");

		return sb.toString();

	}

	/***
				 * 
				 * to print common actions(filter conds, and back ) on no recs pages
				 * 
				 */

	public String printDownLoadLink(String viewUrl) {

		StringBuffer sb = new StringBuffer();

		sb.append("<a  href=\"" + viewUrl + "\" > \n");
		sb.append("<img src=\"" + Defines.ICON_ROOT  + "dn.gif\" alt=\"Download version\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" />\n");
		sb.append("Download\n");
		sb.append("</a>");

		return sb.toString();

	}

	public String printNotAuthReturnToViewPage(EtsIssObjectKey etsIssObjKey, String edgeProblemId) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"175\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append(
			"<td  ><a href=\"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&sc=0&actionType=viewIssue&op=60&edge_problem_id="
				+ edgeProblemId
				+ "&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\"><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" border=\"0\" height=\"16\" width=\"16\" alt=\"\" /></a>&nbsp;</td>");
		sbsub.append(
			"<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&sc=0&actionType=viewIssue&op=61&edge_problem_id=" + edgeProblemId + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "\" class=\"fbox\">Return to Issue details</a></td>");
		sbsub.append("</tr>");
		sbsub.append("<tr><td>&nbsp;</td></tr></table>");
		sbsub.append("</table>");

		return sbsub.toString();

	}

	public String printReturnToMainPageonErr(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append(
			"<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp="
				+ etsIssObjKey.getIstyp()
				+ "&opn=10"
				+ "&proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\""
				+ "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/**
	 * 
	 * @param issFilterObjkey
	 * @return
	 */

	public String printReturnToMainPageonErr(EtsIssFilterObjectKey issFilterObjkey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append("<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issFilterObjkey.getIstyp() + "&opn=10" + "&proj=" + issFilterObjkey.getProj().getProjectId() + "&tc=" + issFilterObjkey.getTc() + "&linkid=" + issFilterObjkey.getLinkid() + "\"" + "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + issFilterObjkey.getIstyp() + "&opn=10&proj=" + issFilterObjkey.getProj().getProjectId() + "&tc=" + issFilterObjkey.getTc() + "&linkid=" + issFilterObjkey.getLinkid() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/**
			 * This method wraps the String and pads that string with spaces.
			 * Creation date: (10/28/01 4:35:24 PM)
			 * @return java.lang.String
			 * @param sInString java.lang.String
			 * @exception java.lang.Exception The exception description.
			 */
	public String formatDescStr(String sInString) {

		StringBuffer sb = new StringBuffer();

		try {

			sb.append(ETSUtils.formatDescStr(sInString));

		} catch (Exception ex) {

			ex.printStackTrace();
		}
		return sb.toString();

	}

	public String printRetToViewPgAndIssues(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		String edgeProblemId = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("edge_problem_id"));

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");

		sbsub.append(
			"<td width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&sc=0&actionType=viewIssue&op=61&edge_problem_id="
				+ edgeProblemId
				+ "&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\"><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" border=\"0\" height=\"16\" width=\"16\" alt=\"Return to issue\" /></a>&nbsp;</td>");
		sbsub.append("<td valign=\"middle\" ><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&sc=0&actionType=viewIssue&op=61&edge_problem_id=" + edgeProblemId + "&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "\">Return to issue</a></td>");
		sbsub.append(
			"<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp="
				+ etsIssObjKey.getIstyp()
				+ "&opn=10"
				+ "&proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "\""
				+ "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/**
		 * warn msg
		 */
	public String printWarningMsg(String warnMsg) {

		StringBuffer sb = new StringBuffer();

		sb.append("<span class=\"small\"><span style=\"color:#ff0000\">\n");
		sb.append(warnMsg);
		sb.append("</span>\n");

		return sb.toString();

	}

	/**
			 * returns from session, if any user is selected , on behalf of which the issues/pcr need to be submitted
			 * @return
			 */

	public boolean isSubBhfExtUserDefnd(EtsIssObjectKey etsIssObjKey) {

		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		if (AmtCommonUtils.isResourceDefined(extBhf) && extBhf.equals("1")) {

			return true;
		}

		return false;
	}

	/**
	 * To show ITAR wrng msg, across issue actions
	 * @param etsIssObjKey
	 * @return
	 */

	public String printITARWrngMsg(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String itarWrngMsg = "";
		StringBuffer sb = new StringBuffer();
		boolean isITAR = etsIssObjKey.getProj().isITAR();

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();

		itarWrngMsg = (String) propMap.get("issue.itar.wrng.msg");

		if (isITAR) { //if ITAR show wrng msg

			////print blade comment msg
			sb.append("<table summary=\"welcome\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
			//sb.append("<tr><td >&nbsp;</td></tr>");
			sb.append("<tr>");
			sb.append("<td  height=\"18\" width=\"600\"><span style=\"color:#ff0000\">" + itarWrngMsg + "</span>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br />");

			
		}

		return sb.toString();

	}
	
	
	/**
		 * To show ITAR wrng msg, across issue actions
		 * @param etsIssObjKey
		 * @return
		 */

		public String getITARWrngMsg(EtsIssObjectKey etsIssObjKey) {

			//comments label
			String itarWrngMsg = "";
			StringBuffer sb = new StringBuffer();
			boolean isITAR = etsIssObjKey.getProj().isITAR();

			int actionKey = etsIssObjKey.getActionkey();
			HashMap propMap = etsIssObjKey.getPropMap();

			itarWrngMsg = (String) propMap.get("issue.itar.wrng.msg");

			if (isITAR) { //if ITAR show wrng msg

				sb.append(itarWrngMsg);

			}

			return sb.toString();

		}
	
	
	/**
	 * 
	 * @param etsIssObjKey
	 * @return
	 */

	public String printReturnToMainPageWithCurState(EtsIssObjectKey etsIssObjKey) {

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
				+ "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + etsIssObjKey.getIstyp() + "&opn=" + etsIssObjKey.getFilopn() + "&srt=" + srt + "&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&flop=" + etsIssObjKey.getFilopn() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */

	public String printReturnToMainPageWithCurState(EtsIssFilterObjectKey issfilterkey) {

		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		String opnqual = issopn.substring(0, 1);
		int srt = issfilterkey.getSortState();

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append("<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + istype + "&opn=" + issopn + "&proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&srt=" + srt + "\" >" + "<img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=" + istype + "&opn=" + issopn + "&proj=" + etsprojid + "&tc=" + tc + "&linkid=" + linkid + "&srt=" + srt + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	/**
		 * 
		 * @param cancName
		 * @return
		 */

	public String printBack(String backName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td  width=\"25\" valign=\"middle\" align=\"left\">\n");
		sbview.append("<input type=\"image\" name=\"" + backName + "\" src=\"" + Defines.BUTTON_ROOT  + "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Back\" border=\"0\" />\n");
		sbview.append("</td>\n");
		sbview.append("<td  align=\"left\">Back to issues/changes</td>\n");
		sbview.append("</tr></table>\n");
		sbview.append("</form>");

		return sbview.toString();
	}
	
	
	public String isProjectIBMOnly(String strProjectId) {
		
	   String ibmOnly = "F";
	   
       try{
       	
       		CommonInfoDAO infoDAO = new CommonInfoDAO();
	 		ibmOnly = infoDAO.isProjectIBMOnly(strProjectId);
	 	
       }catch(SQLException e){
		e.printStackTrace();
	   }catch(Exception e){
       	e.printStackTrace();
       }
	    return ibmOnly;
	    
	}   
	         
	
} //end of class
