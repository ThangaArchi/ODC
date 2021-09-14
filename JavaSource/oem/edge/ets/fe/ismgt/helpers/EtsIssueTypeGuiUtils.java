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
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUpdIssueTypeBdlg;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssueTypeGuiUtils implements EtsIssueConstants, EtsIssFilterConstants {

	public static final String VERSION = "1.12";

	private String RESTRICT_OWNER_STR = "Restrict Ownership to IBM Team members only";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;
	private EtsIssActionGuiUtils actGuiUtils;

	/**
	 * 
	 */
	public EtsIssueTypeGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
		this.actGuiUtils = new EtsIssActionGuiUtils();
	}

	/**
	 * To print the model details for debug
	 */
	public void debugIssTypeModelDetails(EtsIssTypeInfoModel issTypeModel) {

		if (issTypeModel != null) {

			Global.println("PRINTING ISSUE TYPE MODEL DETAILS");

			//usr info
			String curIssueCustName = AmtCommonUtils.getTrimStr(issTypeModel.getSubmitterProfile().getUserFullName());
			String curIssueCustEmail = AmtCommonUtils.getTrimStr(issTypeModel.getSubmitterProfile().getUserEmail());
			String curIssueCustPhone = AmtCommonUtils.getTrimStr(issTypeModel.getSubmitterProfile().getUserContPhone());
			String curIssueCustCompany = AmtCommonUtils.getTrimStr(issTypeModel.getSubmitterProfile().getUserCustCompany());

			Global.println("Printing customer profile at ISS TYPE MODEL@@@@@");
			Global.println("Cust Name====" + curIssueCustName);
			Global.println("Cust Email====" + curIssueCustEmail);
			Global.println("Cust Phone====" + curIssueCustPhone);
			Global.println("Cust Company====" + curIssueCustCompany);

			///ETS_DROPDOWN_DATA///
			String dataId = AmtCommonUtils.getTrimStr(issTypeModel.getDataId());
			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String projectName = AmtCommonUtils.getTrimStr(issTypeModel.getProjectName());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr(issTypeModel.getIssueType());
			String subTypeA = AmtCommonUtils.getTrimStr(issTypeModel.getSubTypeA());
			String subTypeB = AmtCommonUtils.getTrimStr(issTypeModel.getSubTypeB());
			String subTypeC = AmtCommonUtils.getTrimStr(issTypeModel.getSubTypeC());
			String subTypeD = AmtCommonUtils.getTrimStr(issTypeModel.getSubTypeD());
			String issueSource = AmtCommonUtils.getTrimStr(issTypeModel.getIssueSource());
			String issueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());
			String activeFlag = AmtCommonUtils.getTrimStr(issTypeModel.getActiveFlag());

			Global.println("data id===" + dataId);
			Global.println("project Id====" + projectId);
			Global.println("project Name====" + projectName);
			Global.println("issue class====" + issueClass);
			Global.println("issue type====" + issueType);
			Global.println("sub type A====" + subTypeA);
			Global.println("sub type B====" + subTypeB);
			Global.println("sub type C====" + subTypeC);
			Global.println("sub type D====" + subTypeD);
			Global.println("issue source====" + issueSource);
			Global.println("issue access====" + issueAccess);
			Global.println("active Flag====" + activeFlag);

			//owner info
			EtsIssOwnerInfo ownInfo = issTypeModel.getOwnerInfo();
			String ownerUserId = "";
			String ownerEmail = "";

			if (ownInfo != null) {

				ownerUserId = AmtCommonUtils.getTrimStr(ownInfo.getUserEdgeId());
				ownerEmail = AmtCommonUtils.getTrimStr(ownInfo.getUserEmail());
			}

			Global.println("ownerUserId===" + ownerUserId);
			Global.println("ownerEmail===" + ownerEmail);

			///state info

			int curstate = issTypeModel.getCurrentActionState();
			int nextstate = issTypeModel.getNextActionState();
			int cancelstate = issTypeModel.getCancelActionState();

			Global.println("Current state===" + curstate);
			Global.println("Next state===" + nextstate);
			Global.println("Cancel state===" + cancelstate);

			//err msg
			String errMsg = AmtCommonUtils.getTrimStr(issTypeModel.getErrMsg());
			Global.println("errMsg===" + errMsg);

			//////////////end

		} else {

			Global.println("usr1 model is null @@@@");

		}

	}

	/**
	 * 
	 * @param issTypeModel
	 * @return
	 */

	public String printHidCancelActionState(EtsIssTypeInfoModel issTypeModel) {

		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		usr1InfoModel.setCancelActionState(issTypeModel.getCancelActionState());

		return actGuiUtils.printHidCancelActionState(usr1InfoModel);

	}

	/**
	 * 
	 * @param issTypeModel
	 * @return
	 */

	public String printSubmitterDetails(EtsIssTypeInfoModel issTypeModel) {

		EtsIssProbInfoUsr1Model usr1Model = new EtsIssProbInfoUsr1Model();

		EtsIssProjectMember projMem = (EtsIssProjectMember) issTypeModel.getSubmitterProfile();

		usr1Model.setCustName(projMem.getUserFullName());
		usr1Model.setCustEmail(projMem.getUserEmail());
		usr1Model.setCustPhone(projMem.getUserContPhone());
		usr1Model.setCustCompany(projMem.getUserCustCompany());

		StringBuffer sb = new StringBuffer();

		String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());

		sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"30%\"><b>Submitter</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"30%\">" + curIssueCustName + "</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
		sb.append("</tr>\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"30%\"><b>E-mail</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"30%\">" + curIssueCustEmail + "</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();
	}

	/**
				 * To print problem title
				 * 
				 */

	public String printIssueTypeName(String prevIssueTypeName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n");
		sbview.append("<tr><td  valign=\"top\" align=\"\">\n");

		if (!AmtCommonUtils.isResourceDefined(prevIssueTypeName)) {

			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"100\" name=\"issuetypename\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"\" />\n");

		} else {

			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"100\" name=\"issuetypename\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"" + prevIssueTypeName + "\" />\n");

		}

		sbview.append("</td></tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();

	}

	/**
			 * To print Severrity types in actions
			 */

	public String printOwnerList(ArrayList ownerList, ArrayList prevOwnerList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"issueowner\" size=\"\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">Select issue type owner</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(ownerList, prevOwnerList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	public String printBackupOwnerList(ArrayList ownerList, ArrayList prevOwnerList) {

		StringBuffer sbview = new StringBuffer();
				
		sbview.append("<select id=\"sev\" name=\"issuebackupowner\" size=\"\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">Select issue type backup owner</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(ownerList, prevOwnerList));
		sbview.append("</select>\n");
		return sbview.toString();

	}
	
	
	/**
		 * 
		 * @param subName
		 * @param etsIssObjKey
		 * @param edgeProblemId
		 * @return
		 */

	public String printSubmitRetToMainPage(String subName, EtsIssObjectKey etsIssObjKey) {

		return comGuiUtils.printSubmitRetToMainPage(subName, etsIssObjKey);
	}

	/**
			 * 
			 * @param subName
			 * @param etsIssObjKey
			 * @param edgeProblemId
			 * @return
			 */

	public String printContinueRetToMainPage(String contName, EtsIssObjectKey etsIssObjKey) {

		return comGuiUtils.printContinueRetToMainPage(contName, etsIssObjKey);
	}

	/**
	 * 
	 * @param prevIssueAccess
	 * @return
	 */

	public String printIssueAccess(String prevIssueAccess) {

		StringBuffer sbview = new StringBuffer();

		prevIssueAccess = AmtCommonUtils.getTrimStr(prevIssueAccess);

		Global.println("PREV ISSUE ACCESS====" + prevIssueAccess);

		if (prevIssueAccess.equals("Internal")) {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"Internal\" checked=\"checked\" id=\"acc\" />All IBM Team members only&nbsp;");
		} else {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"Internal\" id=\"acc\" />All IBM Team members only&nbsp;");

		}

		if (prevIssueAccess.equals("External")) {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"External\" checked=\"checked\" id=\"acc\"/>All Team members");

		} else {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"External\"  id=\"acc\"/>All Team members");
		}

		return sbview.toString();
	}

	/**
		 * 
		 * @param prevIssueAccess
		 * @return
		 */

	public String printIssueSource(String prevIssueSource, boolean isProjPmoEnabled) {

		StringBuffer sbview = new StringBuffer();

		prevIssueSource = AmtCommonUtils.getTrimStr(prevIssueSource);

		if (isProjPmoEnabled) {

			sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");

			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">Please click the checkbox, if the issue type required to be enabled for PMO. </td>");
			sbview.append("</tr>");

			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

			sbview.append("<tr>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"25%\">");
			sbview.append("<label for=\"pmotype\">&nbsp;<b>PMO type</b>:</label>");
			sbview.append(actGuiUtils.printBlurb("ETSISS_PMOTYPE"));
			sbview.append("</td>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

			if (prevIssueSource.equals(ETSPMOSOURCE)) {

				sbview.append("<input type=\"checkbox\" name=\"issuesource\" value=\"" + ETSPMOSOURCE + "\" checked=\"checked\" id=\"pmotype\" />");

			} else {

				sbview.append("<input type=\"checkbox\" name=\"issuesource\" value=\"" + ETSPMOSOURCE + "\" id=\"pmotype\" />");

			}

			sbview.append("</td>");
			sbview.append("</tr>");

			sbview.append("</table>");

		}

		return sbview.toString();
	}

	/**
	 * 
	 * @param prevIssueSource
	 * @param isBladeTypeProj
	 * @return
	 */

	public String printPrevIssueSource(String prevIssueSource, String pmoProjectId) {

		StringBuffer sbview = new StringBuffer();

		if (AmtCommonUtils.isResourceDefined(pmoProjectId)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><label for=\"pmotype\"><b>PMO Type</b>:</label>");
			sbview.append("</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

			if (prevIssueSource.equals(ETSPMOSOURCE)) {

				sbview.append("Yes");

			} else {

				sbview.append("No");

			}

			sbview.append("</td>");
			sbview.append("</tr>");

			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

		}

		return sbview.toString();

	}

	/**
	 * 
	 * @param prevIssueAccess
	 * @return
	 */
	public String printIssueTypeOwnerShip(String issueAccess, String prevIssueOwnship, boolean isBladeTypeProj) {

		StringBuffer sbview = new StringBuffer();

		prevIssueOwnship = AmtCommonUtils.getTrimStr(prevIssueOwnship);

		Global.println("PRINT PREV ISSUE OWNETSHIP====" + prevIssueOwnship);

		if (!isBladeTypeProj && issueAccess.equals("External")) {

			sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");

			sbview.append("<tr>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
			sbview.append("<label for=\"isstypownship\"><b>" + RESTRICT_OWNER_STR + "</b>:</label>");
			sbview.append(actGuiUtils.printBlurb("ETSISS_OWNSHIP"));
			sbview.append("</td>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");

			if (prevIssueOwnship.equals("Y")) {

				sbview.append("<input type=\"checkbox\" name=\"ownshipinternal\" value=\"Y\" checked=\"checked\" id=\"isstypownship\" />");

			} else {

				sbview.append("<input type=\"checkbox\" name=\"ownshipinternal\" value=\"Y\" id=\"isstypownship\" />");

			}

			sbview.append("</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

			sbview.append("</table>");

		}

		return sbview.toString();
	}

	/**
	 * 
	 * @param issueAccess
	 * @param prevIssueBackupOwnship
	 * @param isBladeTypeProj
	 * @return
	 */
	public String printIssueTypeBackupOwnerShip(String issueAccess, String prevIssueBackupOwnship, boolean isBladeTypeProj) {

		StringBuffer sbview = new StringBuffer();

		prevIssueBackupOwnship = AmtCommonUtils.getTrimStr(prevIssueBackupOwnship);

		Global.println("PRINT PREV ISSUE BACKUP OWNETSHIP====" + prevIssueBackupOwnship);

		if (!isBladeTypeProj && issueAccess.equals("External")) {

			sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");

			sbview.append("<tr>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
			sbview.append("<label for=\"isstypbackupownship\"><b>" + RESTRICT_OWNER_STR + "</b>:</label>");
			sbview.append(actGuiUtils.printBlurb("ETSISS_BACKUP_OWNSHIP"));
			sbview.append("</td>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");

			if (prevIssueBackupOwnship.equals("Y")) {

				sbview.append("<input type=\"checkbox\" name=\"backupownshipinternal\" value=\"Y\" checked=\"checked\" id=\"isstypbackupownship\" />");

			} else {

				sbview.append("<input type=\"checkbox\" name=\"backupownshipinternal\" value=\"Y\" id=\"isstypbackupownship\" />");

			}

			sbview.append("</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

			sbview.append("</table>");

		}

		return sbview.toString();
	}	
	
	/**
	 * To print Severrity types in actions
	 */
	public String printIssueTypeList(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"issuetype\" size=\"\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">Select issue type</option>");
		sbview.append(fltrGuiUtils.printSelectOptions(issueTypeList, prevIssueTypeList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	/**
					 * To print Severrity types in actions
					 */

	public String printIssueTypeListWithVal(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"issuetype\" size=\"\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">Select issue type</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(issueTypeList, prevIssueTypeList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	/**
						 * To print Severrity types in actions
						 */

	public String printIssueTypeListWithValANDName(ArrayList issueTypeList, ArrayList prevIssueTypeList, String selectName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"" + selectName + "\" size=\"\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">Select issue type</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(issueTypeList, prevIssueTypeList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	/**
						 * To print Severrity types in actions
						 */

	public String printMultiIssueTypeListWithValANDName(ArrayList issueTypeList, ArrayList prevIssueTypeList, String selectName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"" + selectName + "\" size=\"5\" multiple=\"multiple\" align=\"left\" class=\"iform\" style=\"width:400px\" width=\"400px\">\n");
		sbview.append("<option value=\"NONE\">--- Select issue type ---</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(issueTypeList, prevIssueTypeList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	/**
				 * 
				 * @param prevIssueAccess
				 * @return
				 */

	public String printPrevIssueTypeOwnerShip(String issueAccess, String prevIssueOwnship, boolean isBladeTypeProj) {

		StringBuffer sbview = new StringBuffer();

		prevIssueOwnship = AmtCommonUtils.getTrimStr(prevIssueOwnship);

		if (!isBladeTypeProj && issueAccess.equals("External")) {

			sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");

			sbview.append("<tr>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
			sbview.append("<b>" + RESTRICT_OWNER_STR + "</b>:");
			sbview.append("</td>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");

			if (prevIssueOwnship.equals("Y")) {

				sbview.append("Yes");

			} else {

				sbview.append("No");

			}

			sbview.append("</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("</table>");

			//sbview.append(actGuiUtils.printEditIssueBtn("op_7031", "Edit issue type ownership"));

			//sbview.append(actGuiUtils.printGreyLine());
			//sbview.append("<br />");

		}

		return sbview.toString();
	}

	/**
	 * 
	 * @param prevIssueAccess
	 * @return
	 */

	public String printPrevIssueTypeBackupOwnerShip(String issueAccess, String prevIssueBackupOwnship, boolean isBladeTypeProj) {
		
		StringBuffer sbview = new StringBuffer();
		prevIssueBackupOwnship = AmtCommonUtils.getTrimStr(prevIssueBackupOwnship);
		
		if(AmtCommonUtils.isResourceDefined(prevIssueBackupOwnship)) {
			if (!isBladeTypeProj && issueAccess.equals("External")) {
				sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sbview.append("<tbody>");
				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
				sbview.append("<b>" + RESTRICT_OWNER_STR + "</b>:");
				sbview.append("</td>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");
				if (prevIssueBackupOwnship.equals("Y")) {
					sbview.append("Yes");
				} else {
					sbview.append("No");
				}
				sbview.append("</td>");
				sbview.append("</tr>");
				sbview.append("<tr>");
				sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
				sbview.append("</tr>");
				sbview.append("</table>");
			}
		}
		
		return sbview.toString();
	}

	
	/**
	 * 
	 * @param prevIssueAccess
	 * @return
	 */
	public String printPrevIssueTypeOwnerShipForEdit(String issueAccess, String prevIssueOwnship, boolean isBladeTypeProj) {

		StringBuffer sbview = new StringBuffer();

		prevIssueOwnship = AmtCommonUtils.getTrimStr(prevIssueOwnship);

		if (!isBladeTypeProj && issueAccess.equals("External")) {

			sbview.append("<tr>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
			sbview.append("<b>" + RESTRICT_OWNER_STR + "</b>:");
			sbview.append("</td>");

			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");

			if (prevIssueOwnship.equals("Y")) {

				sbview.append("Yes");

			} else {

				sbview.append("No");

			}

			sbview.append("</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");

		}

		return sbview.toString();
	}

	/***
	 * 
	 * display Edit Issue Btn Comments  tag
	 */
	public String printStepHierchy(String step, EtsIssObjectKey etsIssObjKey, EtsIssTypeInfoModel issTypeModel) {

		StringBuffer sbview = new StringBuffer();

		String issueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"300\"> ");
		sbview.append("<tr><td  colspan=\"6\"> &nbsp;</td></tr>");
		sbview.append("<tr>");
		sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");
		if (step.equals("1")) {

			sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Security classification</a></b></span></span>");

		} else {

			sbview.append("1.&nbsp;Security classification");
		}

		sbview.append("</td>");

		sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
		sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
		if (step.equals("2")) {

			sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#2\">Issue type owner</a></b></span>");

		} else {

			sbview.append("2.&nbsp;Issue type owner");
		}

		sbview.append("</td>");

		sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
		sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
		if (step.equals("3")) {

			sbview.append("3.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#3\">Submission</a></b></span>");

		} else {

			sbview.append("3.&nbsp;Submission");
		}

		sbview.append("</td>");

		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/**
	 * 
	 * @param issTypeModel
	 * @return
	 */
	public String printPrevOwner(EtsIssTypeInfoModel issTypeModel) {
		
		StringBuffer sbview = new StringBuffer();
		
		EtsIssProjectMember projMem = issTypeModel.getOwnerProfile();

		String ownerIrId = projMem.getUserIrId();
		String ownerName = projMem.getUserFullName();

		String displayStr = ownerName + " " + "[" + ownerIrId + "]";

		sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
		sbview.append("<tbody>");
		sbview.append("<tr>");
		sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type owner</b>:</td>");
		sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + displayStr + "</td>");
		sbview.append("</tr>");
		sbview.append("</table>");
		sbview.append("<br />");

		sbview.append(actGuiUtils.printEditIssueBtn("op_7041", "Edit issue type owner"));

		return sbview.toString();

	}

	
	/**
	 * 
	 * @param issTypeModel
	 * @return
	 */
	public String printPrevBackupOwner(EtsIssTypeInfoModel issTypeModel) {
		
		StringBuffer sbview = new StringBuffer();
		EtsIssProjectMember projMem = issTypeModel.getBackupOwnerProfile(); 
		String backupOwnerIrId = "";
		String backupOwnerName = "";
		if( projMem != null ) {
			backupOwnerIrId = projMem.getUserIrId();
		    backupOwnerName = projMem.getUserFullName();
		}

		if(AmtCommonUtils.isResourceDefined( backupOwnerIrId ) && AmtCommonUtils.isResourceDefined( backupOwnerName ) ) {
			String displayStr = backupOwnerName + " " + "[" + backupOwnerIrId + "]";

			sbview.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type backup owner</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + displayStr + "</td>");
			sbview.append("</tr>");
			sbview.append("</table>");
			sbview.append("<br />");

			sbview.append(actGuiUtils.printEditIssueBtn("op_7042", "Edit issue type backup owner"));			
		}
		
		return sbview.toString();
	}
		
	/**
	 * 
	 * @param issTypeModel
	 * @return
	 */
	public String printIssueTypeDetails(EtsIssTypeInfoModel issTypeModel, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbview = new StringBuffer();

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		String issueAccess = "";
		String ownerShipIbmOnly = "";

		try {

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr( ((String) issTypeModel.getPrevIssueTypeList().get(0)).replaceAll("&quot;","\"") );
			
			EtsIssTypeInfoModel dbIssTypeModel = dropDao.getEtsIssueTypeInfoDetails(projectId, issueClass, issueType);
			issueAccess = AmtCommonUtils.getTrimStr(dbIssTypeModel.getIssueAccess());

			//get the gui vals from AAA:BBB vals of DB

			String securityClassfn = getSecurityClassification(getIssueAccess(issueAccess));
			ownerShipIbmOnly = getIssueOwnerShipIbmOnly(issueAccess);

			///owner profile
			EtsIssProjectMember projMem = dbIssTypeModel.getOwnerProfile();

			String ownerIrId = projMem.getUserIrId();
			String ownerName = projMem.getUserFullName();
			String ownerEmail = projMem.getUserEmail();

			String displayStr = ownerName + " " + "[" + ownerIrId + "]";

			sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + issueType.replaceAll("\"", "&quot;") + "</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Security classification</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + securityClassfn + "</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><span class=\"fnt\">[Access limited to]</span></td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\"></td>");
			sbview.append("</tr>");

			if (!etsIssObjKey.isProjBladeType()) {

				if (!issueAccess.equals("IBM:IBM")) {

					sbview.append("<tr>");
					sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
					sbview.append("</tr>");
					sbview.append("<tr>");
					sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>" + RESTRICT_OWNER_STR + "</b>:</td>");
					sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + ownerShipIbmOnly + "</td>");
					sbview.append("</tr>");

				}

			}
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type owner</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + displayStr + "</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Owner email</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + ownerEmail + "</td>");
			sbview.append("</tr>");
			sbview.append("</table>");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSubmitNewCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSubmitNewCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}
		}

		return sbview.toString();

	}

	/**
		 * 
		 * @param issTypeModel
		 * @return
		 */
	public String printIssueTypeDetailsForEdit(EtsIssTypeInfoModel issTypeModel, EtsIssObjectKey etsIssObjKey) {
		
		StringBuffer sbview = new StringBuffer();

		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		String issueType = "";
		String issueAccess = "";
		String guiIssueAccess = "";

		String ownerShipIbmOnly = "";
		
		try {

			issueType = AmtCommonUtils.getTrimStr((String) issTypeModel.getIssueType());

			issueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getIssueAccess());
			
			

			//get the gui vals from AAA:BBB vals of DB

			guiIssueAccess = AmtCommonUtils.getTrimStr(issTypeModel.getGuiIssueAccess());
			String securityClassfn = getSecurityClassification(issTypeModel.getGuiIssueAccess());

			ownerShipIbmOnly = AmtCommonUtils.getTrimStr(issTypeModel.getOwnerShipInternal());
			

			Global.println("issueOwnerShip in ISS TYPE GUI UTILS===" + ownerShipIbmOnly);

			if (ownerShipIbmOnly.equals("Y")) {

				ownerShipIbmOnly = "Yes";
			} else {

				ownerShipIbmOnly = "No";

			}

			///owner profile
			EtsIssProjectMember projMem = issTypeModel.getOwnerProfile();

			String ownerIrId = AmtCommonUtils.getTrimStr(projMem.getUserIrId());
			String ownerName = AmtCommonUtils.getTrimStr(projMem.getUserFullName());
			String ownerEmail = AmtCommonUtils.getTrimStr(projMem.getUserEmail());

			String displayStr = ownerName + " " + "[" + ownerIrId + "]";

			
			sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + issueType + "</td>");
			sbview.append("</tr>");
			sbview.append("</table>");
			sbview.append("<br />");
			sbview.append(actGuiUtils.printEditIssueBtn("op_1102", "Edit issue type name"));
			sbview.append(actGuiUtils.printGreyLine());

			sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Security classification</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + securityClassfn + "</td>");
			sbview.append("</tr>");
			sbview.append(printAccessLmtdTo());

			sbview.append("</table>");
			sbview.append("<br />");
			sbview.append(actGuiUtils.printEditIssueBtn("op_1103", "Edit security classification"));
			sbview.append(actGuiUtils.printGreyLine());

			sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
			sbview.append("<tbody>");
			
			if (!etsIssObjKey.isProjBladeType()) {

				if (guiIssueAccess.equals("External")) {

					sbview.append("<tr>");
					sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
					sbview.append("</tr>");

					sbview.append("<tr>");
					sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>" + RESTRICT_OWNER_STR + "</b>:</td>");
					sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + ownerShipIbmOnly + "</td>");
					sbview.append("</tr>");

				}

			}
			
			
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type owner</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + displayStr + "</td>");
			sbview.append("</tr>");
			sbview.append("<tr>");
			sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
			sbview.append("</tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Owner email</b>:</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + ownerEmail + "</td>");
			sbview.append("</tr>");
			sbview.append("</table>");
			sbview.append("<br />");
			sbview.append(actGuiUtils.printEditIssueBtn("op_1104", "Edit issue type owner"));
			sbview.append(actGuiUtils.printGreyLine());

			
			//if ( AmtCommonUtils.isResourceDefined(issTypeModel.getBackupOwnershipInternal()  )  )  {			
			if( (issTypeModel.getBackupOwnerProfile() != null)  &&  (AmtCommonUtils.isResourceDefined(issTypeModel.getBackupOwnerProfile().getUserEdgeId()))  ) {
/*
			if (EtsIssFilterUtils.isArrayListDefndWithObj(issTypeModel.getBackupOwnerList())) {			
				
				String backupOwnerShipIbmOnly = "";
				
				if (issTypeModel.getBackupOwnershipInternal().equals("Y")) {

					backupOwnerShipIbmOnly = "Yes";
				} else {

					backupOwnerShipIbmOnly = "No";

				}
			*/
				///backup owner profile
				EtsIssProjectMember bkProjMem = issTypeModel.getBackupOwnerProfile();

				String bkOwnerIrId = AmtCommonUtils.getTrimStr(bkProjMem.getUserIrId());
				String bkOwnerName = AmtCommonUtils.getTrimStr(bkProjMem.getUserFullName());
				String bkOwnerEmail = AmtCommonUtils.getTrimStr(bkProjMem.getUserEmail());

				String bkdisplayStr = bkOwnerName + " " + "[" + bkOwnerIrId + "]";
								
				sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sbview.append("<tbody>");
				sbview.append("<tr>");
				sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
				sbview.append("</tr>");
				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type backup owner</b>:</td>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + bkdisplayStr + "</td>");
				sbview.append("</tr>");
				sbview.append("<tr>");
				sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
				sbview.append("</tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Backup owner email</b>:</td>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\">" + bkOwnerEmail + "</td>");
				sbview.append("</tr>");
				sbview.append("</table>");
				sbview.append("<br />");
				sbview.append(actGuiUtils.printEditIssueBtn("op_1106", "Edit issue type backup owner"));
				sbview.append(actGuiUtils.printGreyLine());											
											
			} else {				
				sbview.append("<br />");
				sbview.append("<table summary=\"issue type details\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sbview.append("<tbody>");
				sbview.append("<tr>");
				sbview.append("<td  colspan=\"2\" height=\"18\">&nbsp;</td>");
				sbview.append("</tr>");
				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><b>Issue type backup owner</b> : </td>");
				sbview.append("<td  valign=\"top\" align=\"left\" width=\"70%\"> Not assigned</td>");
				sbview.append("</tr>");
				sbview.append("</table>");
				sbview.append("<br />");
				sbview.append(actGuiUtils.printEditIssueBtn("op_1106", "Edit issue type backup owner"));
				sbview.append(actGuiUtils.printGreyLine());
				sbview.append("<br />");

			}
						
			
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSubmitNewCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}
		}

		return sbview.toString();

	}

	/**
	 * 
	 * @param contnName
	 * @param cancName
	 * @return
	 */

	public String printContCancel(String contnName, String cancName) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(comGuiUtils.printContinue(contnName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(comGuiUtils.printCancel(cancName));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
	 * 
	 * @param issueAccess
	 * @return
	 */

	public String getIssueAccess(String issueAccess) {

		String localIssueAccess = "";

		if (issueAccess.equals("IBM:IBM") || issueAccess.equals("ALL:IBM") || issueAccess.equals("ALL:EXT")) {

			if (issueAccess.equals("IBM:IBM")) {

				localIssueAccess = "Internal";

			}

			if (issueAccess.equals("ALL:IBM")) {

				localIssueAccess = "External";

			}

			if (issueAccess.equals("ALL:EXT")) {

				localIssueAccess = "External";

			}

		} else {

			localIssueAccess = issueAccess;
		}

		return localIssueAccess;
	}

	/**
		 * 
		 * @param issueAccess
		 * @return
		 */

	public String getSecurityClassification(String issueAccess) {

		String localIssueAccess = "";

		if (issueAccess.equals("Internal")) {

			localIssueAccess = "All IBM Team members only";

		}

		if (issueAccess.equals("External")) {

			localIssueAccess = "All Team members";

		}

		return localIssueAccess;
	}

	/**
	 * 
	 * @param issueAccess
	 * @return
	 */

	public String getIssueOwnerShipIbmOnly(String issueAccess) {

		String ownerShipIbmOnly = "";

		if (issueAccess.equals("IBM:IBM")) {

			ownerShipIbmOnly = "Yes";
		}

		if (issueAccess.equals("ALL:IBM")) {

			ownerShipIbmOnly = "Yes";
		}

		if (issueAccess.equals("ALL:EXT")) {

			ownerShipIbmOnly = "No";
		}

		return ownerShipIbmOnly;
	}

	/**
	 * warn msg
	 */
	public String printWarningMsg(String warnMsg) {

		return comGuiUtils.printWarningMsg(warnMsg);

	}

	/**
		 * warn msg
		 */
	public String printWarningMsgForEditIssue() {

		String warnMsg = "Please note that the changes in issue type access or issue type ownership will be applicable to new issues submitted in this category. Existing issues in this issue category will not be affected by this change.";

		return printWarningMsgForIssueType(warnMsg);

	}

	/**
			 * warn msg
			 */
	public String printWarningMsgForIssueType(String warnMsg) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tbody>\n");
		sb.append("<tr>\n");
		sb.append("<td>\n");
		sb.append("[ ");
		sb.append(printWarningMsg(warnMsg));
		sb.append(" ]");
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");
		return sb.toString();

	}

	/**
	 * 
	 */

	public String getIssueAccessMatrix(String issueAccess, String issueOwnShipOnly, boolean isBladeTypeProj) {

		String accessMatrix = "ALL:IBM";

		//for non-blade type projects
		if (!isBladeTypeProj) {

			if (issueAccess.equals("Internal")) {

				accessMatrix = "IBM:IBM";

			} else { //if not internal

				if (issueOwnShipOnly.equals("Y")) {

					accessMatrix = "ALL:IBM";

				} else {

					accessMatrix = "ALL:EXT";
				}

			} //if not internal

		} else { //for blade type projects

			if (issueAccess.equals("Internal")) {

				accessMatrix = "IBM:IBM";

			} else {

				accessMatrix = "ALL:IBM";

			}

		}

		return accessMatrix;
	}

	/**
	 * @return
	 */
	public String getRESTRICT_OWNER_STR() {
		return RESTRICT_OWNER_STR;
	}

	/**
	 * 
	 * @param issTypeModel
	 * @param etsIssObjKey
	 * @return
	 */

	public String printIssueTypeOwner(EtsIssTypeInfoModel issTypeModel, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"issue desc summary\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
		sb.append("<tbody>");

		sb.append("<tr>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
		sb.append("<span class=\"ast\"><b>*</b></span><label for=\"sev\"><b>Issue type owner</b>:</label>");
		sb.append(actGuiUtils.printBlurb("ETSISS_OWNER"));
		sb.append("</td>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");
		sb.append(printOwnerList(issTypeModel.getOwnerList(), issTypeModel.getPrevOwnerList()));

		sb.append("</td>");
		sb.append("</tr>");

		if (!etsIssObjKey.isProjBladeType() && !issTypeModel.getIssueAccess().equals("Internal")) {

			sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;");
			sb.append("</td>");
			sb.append("</tr>");

			sb.append("<tr>");
			sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\">&nbsp;");
			sb.append("</td>");

			sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
			sb.append("<span class=\"small\">[ <b>*</b> IBM employee ]</span>");
			sb.append("</td>");
			sb.append("</tr>");

		}

		sb.append("</table>");

		return sb.toString();
	}


	public String printIssueTypeBackupOwner(EtsIssTypeInfoModel issTypeModel, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"issue desc summary 2\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
		sb.append("<tbody>");

		sb.append("<tr>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"30%\">");
		sb.append("<span class=\"ast\"></span><label for=\"sev\"><b>Issue type backup owner</b>:</label>");
		sb.append(actGuiUtils.printBlurb("ETSISS_Backup_OWNER"));
		sb.append("</td>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"70%\">");
		//sb.append(printOwnerList(issTypeModel.getOwnerList(), issTypeModel.getPrevOwnerList()));
		sb.append(printBackupOwnerList(issTypeModel.getBackupOwnerList(), issTypeModel.getPrevBackupOwnerList()));
		sb.append("</td>");
		sb.append("</tr>");

		if (!etsIssObjKey.isProjBladeType() && !issTypeModel.getIssueAccess().equals("Internal")) {

			sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;");
			sb.append("</td>");
			sb.append("</tr>");

			sb.append("<tr>");
			sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\">&nbsp;");
			sb.append("</td>");

			sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
			sb.append("<span class=\"small\">[ <b>*</b> IBM employee ]</span>");
			sb.append("</td>");
			sb.append("</tr>");

		}

		sb.append("</table>");

		return sb.toString();
	}
	
	
	
	/**
	 * 
	 * @return
	 */

	public String printAccessLmtdTo() {

		StringBuffer sb = new StringBuffer();

		sb.append("<tr>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"30%\"><span class=\"fnt\">&nbsp;[Access limited to]</span></td>");
		sb.append("<td  valign=\"top\" align=\"left\" width=\"70%\"></td>");
		sb.append("</tr>");

		return sb.toString();
	}

	/**
		 * 
		 * @param issviewrep
		 * @return
		 */

	public String printIssTypeListRepHeader(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();
		String srchByNum = AmtCommonUtils.getTrimStr((String) issobjkey.getParams().get("isssrchnum"));

		sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"760\" border=\"0\">");
		sb.append("<tr>");
		sb.append("<td class=\"small\"><b>Issue type(s) info</b></td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td height=\"21\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "gray_dotted_line.gif\" ");
		sb.append("height=\"1\" width=\"760\" alt=\"\" /></td>");
		sb.append("</tr>");
		sb.append("</table>");

		return sb.toString();

	}

	/**
		 * 
		 * @return
		 */

	public String printIssueTypeInfoList(EtsIssFilterObjectKey issfilterkey, ArrayList issTypeInfoList) {

		StringBuffer sb = new StringBuffer();

		try {

			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			//String opnqual = issopn.substring(0, 1);
			

			int repsize = 0;

			if (issTypeInfoList != null && !issTypeInfoList.isEmpty()) {

				repsize = issTypeInfoList.size();

			}

			String issueType = "";
			String issueAccess = "";
			String issueSource = "";
			String ownerEmail = "";
			String ownerName = "";
			String backupOwnerEmail = "";
			String backupOwnerName = "";

			String bgcolor = "background-color:#eeeeee";

			String tmpIssueType = "";
			boolean chgcolor = true;
			String lastTmpRowColor = "";

			String prnt = AmtCommonUtils.getTrimStr((String) (issfilterkey.getParams()).get("prnt"));

			
			sb.append("<table summary=\"Issues info details\" cellpadding=\"2\" cellspacing=\"0\" width=\"760\" border=\"0\">\n");
			sb.append("<tbody>\n");

			sb.append(printListIssTypeSortHeader(issfilterkey, prnt));

			if (AmtCommonUtils.isResourceDefined(prnt)) {

				sb.append("<tr valign=\"top\">");
				sb.append("<td colspan=\"13\" width=\"760\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"760\" height=\"1\" /></td>");
				sb.append("</tr>");
			}

			for (int r = 0; r < repsize; r++) {

				EtsDropDownDataBean dropBean = (EtsDropDownDataBean) issTypeInfoList.get(r);

				issueType = AmtCommonUtils.getTrimStr(dropBean.getIssueType());
				issueSource = AmtCommonUtils.getTrimStr(dropBean.getIssueSource());
				issueAccess = AmtCommonUtils.getTrimStr(dropBean.getIssueAccess());

				EtsIssOwnerInfo ownerInfo = dropBean.getOwnerInfo();
				
				EtsIssOwnerInfo backupOwnerInfo = dropBean.getBackupOwnerInfo();

				ownerEmail = AmtCommonUtils.getTrimStr(ownerInfo.getUserEmail());
				ownerName = AmtCommonUtils.getTrimStr(ownerInfo.getUserFullName());
				
				backupOwnerEmail = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserEmail());
				backupOwnerName = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserFullName());

				
				if (tmpIssueType.equals(issueType)) {

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

				if (!chgcolor) {

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"90\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				} else {

					sb.append("<td headers=\"issid00\" align=\"left\" valign=\"top\" width=\"90\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueType + "</span></td>");

				}

				sb.append("<td headers=\"issid01\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				sb.append("<td headers=\"issid1\" align=\"left\" valign=\"top\" width=\"130\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + issueAccess + "</span></td>");

				sb.append("<td headers=\"issid2\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				sb.append("<td headers=\"issid3\" align=\"left\" valign=\"top\" width=\"130\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + ownerName + "</span></td>");
				sb.append("<td headers=\"issid4\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				//donot show submitter and owner details if proj is of blade type and customer is external

				sb.append("<td headers=\"issid5\" align=\"left\" valign=\"top\" width=\"130\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + ownerEmail + "</span></td>");
				sb.append("<td headers=\"issid6\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");

				


				sb.append("<td headers=\"issid7\" align=\"left\" valign=\"top\" width=\"130\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + backupOwnerName + "</span></td>");
				sb.append("<td headers=\"issid8\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");


				sb.append("<td headers=\"issid9\" align=\"left\" valign=\"top\" width=\"130\" style=\"height:21\" height=\"21\" ><span class=\"small\">" + backupOwnerEmail + "</span></td>");
				sb.append("<td headers=\"issid10\" align=\"left\" valign=\"top\" width=\"2\" style=\"height:21\" height=\"21\" ><span class=\"small\">&nbsp;</span></td>");
				
				
				
				sb.append("</tr>");

				lastTmpRowColor = bgcolor;

				if (bgcolor.equals("background-color:#eeeeee")) {

					bgcolor = "";

				} else {

					bgcolor = "background-color:#eeeeee";

				}

				tmpIssueType = issueType;

			}

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"13\" width=\"760\"><img src=\"//www.ibm.com/i/c.gif\" alt=\"\" width=\"760\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("<tr valign=\"top\">");
			sb.append("<td colspan=\"13\" width=\"760\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"760\" height=\"1\" /></td>");
			sb.append("</tr>");

			sb.append("</tbody>");
			sb.append("</table>");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsChgOwnerCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

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

	public String printListIssTypeSortHeader(EtsIssFilterObjectKey issfilterkey, String prnt) {

		StringBuffer sb = new StringBuffer();
		int sortstate = 0;

		//////

		//	get common params//
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();

		HashMap propMap = issfilterkey.getPropMap();

		/////////get state///
		sortstate = issfilterkey.getSortState();

		sb.append("<form name=\"filteriss\" method=\"post\" action=\"listIssTypeInfo.wss?linkid=" + linkid + "&opn=1500&tc=" + tc + "&proj=" + etsprojid + "&istyp=" + istype + "\" ");
		sb.append("<tr>\n");

		//for sort by cq trk id//
		sb.append("<th id=\"issid00\" align=\"left\" valign=\"top\" width=\"90\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {

			// sort by title//
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_name", sortstate, SORTLISTISSTYPENAME_A, SORTLISTISSTYPENAME_D));

		}
		/////
		sb.append("<span class=\"small\">Issue type</span></th>\n");
		sb.append("<th id=\"issid01\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		////
		sb.append("<th id=\"issid1\" align=\"left\" valign=\"top\" width=\"130\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {

			// sort by title//
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_access", sortstate, SORTLISTISSTYPEACCESS_A, SORTLISTISSTYPEACCESS_D));

		}
		/////
		sb.append("<span class=\"small\">Issue type access</span></th>\n");
		sb.append("<th id=\"issid2\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid3\" align=\"left\" valign=\"top\" width=\"130\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {
			//	sort by title//
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_owner", sortstate, SORTLISTISSTYPEOWNER_A, SORTLISTISSTYPEOWNER_D));

		}

		sb.append("<span class=\"small\">Owner name</span></th>\n");

		sb.append("<th id=\"issid4\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid5\" align=\"left\" valign=\"top\" width=\"130\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {

			//sort by submitter
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_ownremail", sortstate, SORTLISTISSTYPEOWNEREMAIL_A, SORTLISTISSTYPEOWNEREMAIL_D));

		}

		sb.append("<span class=\"small\">Owner email</span></th>\n");

		sb.append("<th id=\"issid6\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");

		
		
		
		
		
		
		
		sb.append("<th id=\"issid7\" align=\"left\" valign=\"top\" width=\"130\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {
			//	sort by title//
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_backupowner", sortstate, SORTLISTISSTYPEBACKUPOWNER_A, SORTLISTISSTYPEBACKUPOWNER_D));

		}

		sb.append("<span class=\"small\">Backup owner name</span></th>\n");

		sb.append("<th id=\"issid8\" align=\"left\" valign=\"top\" width=\"2\">&nbsp;</th>\n");

		sb.append("<th id=\"issid9\" align=\"left\" valign=\"top\" width=\"130\"> \n");

		if (!AmtCommonUtils.isResourceDefined(prnt)) {

			//sort by submitter
			sb.append(fltrGuiUtils.printSortColumns("issuetype_sort_backupownremail", sortstate, SORTLISTISSTYPEBACKUPOWNEREMAIL_A, SORTLISTISSTYPEBACKUPOWNEREMAIL_D));

		}

		sb.append("<span class=\"small\">Backup owner email</span></th>\n");

		sb.append("<th id=\"issid10\" align=\"left\" valign=\"top\" width=\"2\"></th>\n");
		
		
		
		
		
		
		
		
		sb.append("</tr> \n");

		sb.append("</form>\n");

		return sb.toString();

	}

	/***
			 * 
			 * to print common actions(filter conds, and back ) on no recs pages
			 * 
			 */

	public String printPrinterFriendly(EtsIssFilterObjectKey issfilterkey) {

		String opn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();

		String viewUrl = "listIssTypeInfo.wss?istyp=" + issfilterkey.getProblemType() + "&proj=" + issfilterkey.getProjectId() + "&linkid=" + issfilterkey.getLinkid() + "&opn=1500&actionType=listIssTypeInfo&tc=" + issfilterkey.getTc() + "&prnt=Y";

		return comGuiUtils.printPrinterFriendly(viewUrl);

	}

	/***
			 * 
			 * to print common actions(filter conds, and back ) on no recs pages
			 * 
			 */

	public String printPrinterActionsOnRepPage(EtsIssFilterObjectKey issfilterkey) {

		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"backinfo\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td valign=\"top\" align=\"left\">" + fltrGuiUtils.printCommonBack(issfilterkey) + "</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + printPrinterFriendly(issfilterkey) + "</td> ");
		sb.append("<td valign=\"top\" align=\"left\">" + fltrGuiUtils.printDownLoadPage(issfilterkey) + "</td> ");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();

	}

	/***
			 * print no recs msg
			 */

	public String printNoRecsMsgForListIssTypes(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();

		String istype = issobjkey.getProblemType();
		int state = issobjkey.getState();

		//sb.append("<%-- table 7 for no issues info details info starts --%>");

		sb.append("<table summary=\"No Issues info details\" cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		sb.append("<tbody>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("<tr>");

		sb.append("<td colspan=\"5\"><span style=\"color: #ff0000\">" + issobjkey.getPropMap().get("list.isstypes.norecs.msg") + "</span></td>");
		sb.append("</tr>");

		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"5\"><img src=\"" + Defines.V11_IMAGE_ROOT  + "rules/grey_rule.gif\" alt=\"\" width=\"600\" height=\"1\" /></td>");
		sb.append("</tr>");

		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 7 for no issues info details info end --%>");

		return sb.toString();

	}

	public String printReturnToIssTypePageonErr(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sbsub = new StringBuffer();
		HashMap propMap = issobjkey.getPropMap();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append("<td width=\"18\">\n");
		sbsub.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">");
		sbsub.append("<img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=2200&proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&tc=" + issobjkey.getTc() + "\">Back to manage issue types</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

} //end of class
