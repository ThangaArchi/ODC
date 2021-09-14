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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.ismgt.resources.EtsPcrResource;

/**
 * @author v2phani
 * This class will contain all the useful maethods, to print
 * various steps of Issue submission, ie.Description/Identification/
 *  Issue Type/Security/Files/Notification 
 * 
 */
public class EtsIssActionGuiUtils implements EtsIssueConstants, EtsIssueActionConstants, EtsIssFilterConstants {

	public static final String VERSION = "1.26.2.12";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;
	/**
	 * Constructor for EtsIssActionGuiUtils.
	 */
	public EtsIssActionGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
	}

	/**
	 * This method will print Section Headers required for each of section in blue
	 */

	public String printSectnHeader(String headerName) {

		return comGuiUtils.printSectnHeader(headerName);

	}

	/**
	 * This method will print submitter details
	 * 
	 */

	public String printSubmitterDetails(EtsIssProbInfoUsr1Model usr1Model) {

		StringBuffer sb = new StringBuffer();

		String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());

		sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Submitter</b>:</td>\n");
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

	/**
		 * This method will print submitter details
		 * 
		 */

	public String printIssueDescripDetails(EtsIssProbInfoUsr1Model usr1Model) {

		StringBuffer sb = new StringBuffer();
		String severity = "";

		try {

			ArrayList prevSevList = usr1Model.getPrevProbSevList();

			if (prevSevList != null && !prevSevList.isEmpty()) {

				severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

			}

			//		remove like 1-,2- from severity 	
			int inx = severity.indexOf("-");

			if (inx != -1) {

				severity = severity.substring(inx + 1);

			}

			String probTitle = AmtCommonUtils.getTrimStr(usr1Model.getProbTitle());
			String probDesc = comGuiUtils.formatDescStr((usr1Model.getProbDesc()));
			
			sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Severity</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"75%\">" + severity + "</td>\n");
			sb.append("</tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Title</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"75%\">" + probTitle + "</td>\n");
			sb.append("</tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Description</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"75%\"><pre>" + probDesc + "</pre></td>\n");
			sb.append("</tr>\n");

			sb.append("</table>\n");

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return sb.toString();
	}

	/**
		 * To print Severrity types in actions
		 */

	public String printIssueSeverity(ArrayList severityList, ArrayList prevSeverityList) {

		StringBuffer sbview = new StringBuffer();

		//			remove ALL value//
		if (severityList != null && !severityList.isEmpty() && severityList.contains("All")) {

			severityList.remove("All");
			severityList.remove("All");
		}

		sbview.append("<select id=\"sev\" name=\"severity\" size=\"\" align=\"left\" class=\"iform\" style=\"width:200px\" width=\"200px\">\n");
		sbview.append("<option value=\"NONE\">Select severity level</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(severityList, prevSeverityList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

	/**
		 * to print mandatory  msg tables
		 */

	public String printMandtMsg() {

		return comGuiUtils.printMandtMsg();

	}

	/**
		 * To print the error msg 
		 */

	public String printStdErrMsg(String errMsg) {

		return comGuiUtils.printStdErrMsg(errMsg);

	}

	/**
		 * To print problem title
		 * 
		 */

	public String printProbTitle(String prevValTitle) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n");
		sbview.append("<tr><td  valign=\"top\" align=\"\">\n");

		/*if (!AmtCommonUtils.isResourceDefined(prevValTitle)) {
		
			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"125\" name=\"title\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"\" />\n");
		
		} else {
		
			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"125\" name=\"title\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"" + prevValTitle + "\" />\n");
		
		}*/

		if (!AmtCommonUtils.isResourceDefined(prevValTitle)) {

			sbview.append("<textarea id=\"tl\"  name=\"title\" size=\"125\" rows=\"1\" cols=\"1\"  class=\"iform\" style=\"width:323px\" width=\"323px\" >\n");
			sbview.append("</textarea>\n");

		} else {

			sbview.append("<textarea id=\"tl\"  name=\"title\" size=\"125\" rows=\"1\" cols=\"1\"  class=\"iform\" style=\"width:323px\" width=\"323px\" >\n");
			sbview.append(prevValTitle);
			sbview.append("</textarea>\n");
		}

		sbview.append("</td></tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();

	}

	/**
		 * To print the text area
		 */

	public String printStdTextArea(String fieldId, String txtAreaName, String prevTextAreaVal) {

		return comGuiUtils.printStdTextArea(fieldId, txtAreaName, prevTextAreaVal);
	}

	/**
	 * To print issue types for a given change/defect and project
	 */

	public String printIssueTypes(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"lblisstype\" name=\"problem_type\" size=\"\" align=\"left\" class=\"iform\" style=\"width:200px\" width=\"200px\" >\n");
		sbview.append("<option value=\"NONE\">Select an issue type</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(issueTypeList, prevIssueTypeList));
		sbview.append("</select>\n");

		return sbview.toString();
	}

	/**
		 * print grey line
		 */

	public String printGreyLine() {

		return comGuiUtils.printGreyLine();

	}

	/**
	 * 
	 * @param ht
	 * @return
	 */

	public String printBlackLine(String ht) {

		return comGuiUtils.printBlackLine(ht);

	}

	/**
	 * 
	 * @param contnName
	 * @return
	 */

	public String printContinue(String contnName) {

		return comGuiUtils.printContinue(contnName);
	}

	/**
	 * 
	 * 
	 * @param cancName
	 * @return
	 */
	public String printCancel(String cancName) {

		return comGuiUtils.printCancel(cancName);
	}

	/**
	 * 
	 * 
	 * @param subName
	 * @return
	 */

	public String printSubmit(String subName) {

		return comGuiUtils.printSubmit(subName);

	}

	/**
	 * To print continue and cancel buttons
	 * @param contnName
	 * @param cancName
	 * @return
	 */

	public String printContCancel(String contnName, String cancName) {

		return comGuiUtils.printContCancel(contnName, cancName);
	}

	/**
		 * To print continue and cancel buttons
		 * @param contnName
		 * @param cancName
		 * @return
		 */

	public String printSubmitCancel(String subName, String cancName) {

		return comGuiUtils.printSubmitCancel(subName, cancName);
	}

	/**
	 * To print continue and cancel buttons
	 * @param contnName
	 * @param cancName
	 * @return
	 */

	public String printContCancel(int state) {

		StringBuffer sbview = new StringBuffer();

		String contnName = "";
		String cancName = "";

		Global.println("state in print cont button ==" + state);

		switch (state) {

			case NEWINITIAL :

			case VALIDERRCONTDESCR :

				//case CONTDESCR :

			case EDITDESCR :

				contnName = "op_11";
				cancName = "op_12";

				break;

			case CONTDESCR :
			case ADDISSUETYPE :
			case VALIDERRADDISSTYPE :
			case EDITISSUETYPE :
			case EDITISSUEIDENTF :

				contnName = "op_28";
				cancName = "op_128";

				break;

			case GOSUBTYPEA :
			case EDITSUBTYPEA :

				contnName = "op_141";
				cancName = "op_121";
				break;

			case GOSUBTYPEB :
			case EDITSUBTYPEB :

				contnName = "op_142";
				cancName = "op_121";
				break;

			case GOSUBTYPEC :
			case EDITSUBTYPEC :

				contnName = "op_143";
				cancName = "op_121";
				break;

			case CONTIDENTF :

				contnName = "op_16";
				cancName = "op_122";

				break;

			case VALIDERRCONTIDENTF :

				contnName = "op_16";
				cancName = "op_122";

				break;

			case ADDFILEATTACH :
			case FILEATTACH :
			case DELETEFILE :
			case EDITFILEATTACH :

				contnName = "op_20";
				cancName = "op_123";

				break;

			case ADDACCESSCNTRL :
			case EDITACCESSCNTRL :

				contnName = "op_22";
				cancName = "op_124";

				break;

			case ADDNOTIFYLIST :
			case ADDNOTIFYLISTFOREXT :
			case EDITNOTIFYLIST :

				contnName = "op_24";
				cancName = "op_125";

			case ISSUEFINALSUBMIT :
			case ISSUEFINALCANCEL :

				contnName = "op_26";
				cancName = "op_126";

				break;

		}

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

	/**
		 * To print continue and cancel buttons
		 * @param contnName
		 * @param cancName
		 * @return
		 */

	public String printContCancel(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbview = new StringBuffer();

		String contnName = "";
		String cancName = "";

		//			states
		int actionstate = usr1InfoModel.getCurrentActionState();
		int usr1nextstate = usr1InfoModel.getNextActionState();

		if (usr1nextstate > 0) {

			actionstate = usr1nextstate;
		}

		Global.println("actionstate in print cont button ==" + actionstate);
		
		System.out.println("Action State in EtsIssActionGuiUtils--->>>"+actionstate);

		switch (actionstate) {

			case NEWINITIAL :

			case VALIDERRCONTDESCR :

				//case CONTDESCR :

			case EDITDESCR :

				//case EDITISSUETYPE :

				contnName = "op_11";
				cancName = "op_12";

				break;

			case CONTDESCR :
			case ADDISSUETYPE :
			case VALIDERRADDISSTYPE :
			case EDITISSUETYPE :
			case EDITISSUEIDENTF :

				contnName = "op_28";
				cancName = "op_128";

				break;

			case GOSUBTYPEA :
			case EDITSUBTYPEA :

				contnName = "op_141";
				cancName = "op_121";
				break;

			case GOSUBTYPEB :
			case EDITSUBTYPEB :

				contnName = "op_142";
				cancName = "op_121";
				break;

			case GOSUBTYPEC :
			case EDITSUBTYPEC :

				contnName = "op_143";
				cancName = "op_121";
				break;

			case CONTIDENTF :

				contnName = "op_16";
				cancName = "op_122";

				break;

			case VALIDERRCONTIDENTF :

				contnName = "op_16";
				cancName = "op_122";

				break;

			case ADDFILEATTACH :
			case FILEATTACH :
			case DELETEFILE :
			case EDITFILEATTACH :

				contnName = "op_20";
				cancName = "op_123";

				break;

			case ADDACCESSCNTRL :
			case EDITACCESSCNTRL :

				contnName = "op_22";
				cancName = "op_124";

				break;

			case ADDNOTIFYLIST :
			case ADDNOTIFYLISTFOREXT :
			case EDITNOTIFYLIST :

				contnName = "op_24";
				cancName = "op_125";

				break;

			case ISSUEFINALSUBMIT :
			case ISSUEFINALCANCEL :

				contnName = "op_26";
				cancName = "op_126";

				break;

		}

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		System.out.println("Continue Name is--->>>"+contnName);
		sbview.append(printContinue(contnName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		System.out.println("Cancel Name is--->>>"+cancName);
		sbview.append(printCancel(cancName));		
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
	  * To print blurb
	  */

	public String printBlurb(String helpStr) {

		return comGuiUtils.printBlurb(helpStr);
	}

	/***
		 * 
		 * display Edit Issue Btn Comments  tag
		 */

	public String printEditIssueBtn(String imgName, String editBtnLabel) {

		return comGuiUtils.printEditIssueBtn(imgName, editBtnLabel);

	}

	/**
	 * print the common hidden vars reqd for EtsProjectServlet
	 */

	public String printCommonHidVars(EtsIssObjectKey etsIssObjKey) {

		return comGuiUtils.printCommonHidVars(etsIssObjKey);

	}

	/**
		 * to get issue type label
		 */
	public String getDefaultIssueTypeLabel(EtsIssObjectKey etsIssObjKey) {

		return comGuiUtils.getDefaultIssueTypeLabel(etsIssObjKey);
	}

	/**
	 * to get issue type label
	 */
	public String getDefaultSubTypeStr(EtsIssObjectKey etsIssObjKey, String stdSubType, String defaultStdSubType) {

		return comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, stdSubType, defaultStdSubType);
	}

	/**
	 * To get the label for Sub Type A
	 * @param etsIssObjKey
	 * @return
	 */

	public String getLabelForSubTypeA(EtsIssObjectKey etsIssObjKey) {

		return getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_A, DEFUALTSTDSUBTYPE_A);

	}

	/**
			 * To get the label for Sub Type A
			 * @param etsIssObjKey
			 * @return
			 */

	public String getLabelForSubTypeB(EtsIssObjectKey etsIssObjKey) {

		return getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_B, DEFUALTSTDSUBTYPE_B);

	}

	/**
			 * To get the label for Sub Type A
			 * @param etsIssObjKey
			 * @return
			 */

	public String getLabelForSubTypeC(EtsIssObjectKey etsIssObjKey) {

		return getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_C, DEFUALTSTDSUBTYPE_C);

	}

	/**
			 * To get the label for Sub Type A
			 * @param etsIssObjKey
			 * @return
			 */

	public String getLabelForSubTypeD(EtsIssObjectKey etsIssObjKey) {

		return getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_D, DEFUALTSTDSUBTYPE_D);

	}

	/**
		 * to print the common select for subm forms
		 * 
		 */
	public String printDynSubmFrmListBox(String listBoxDispName, String listBoxRefName, ArrayList dropValList, ArrayList prevDropValList) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><span class=\"ast\"><b>*</b></span><label for=\"lb" + listBoxRefName + "\"><b>" + listBoxDispName + "</b>:</label></td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
		sbsub.append("<select id=\"lb" + listBoxRefName + "\" name=\"" + listBoxRefName + "\" size=\"\" align=\"left\" class=\"iform\" style=\"width:150px\" width=\"150px\" >");
		sbsub.append("<option value=\"NONE\">Select " + listBoxDispName + " </option>\n");
		sbsub.append(fltrGuiUtils.printSelectOptions(dropValList, prevDropValList));
		sbsub.append("</select>");
		sbsub.append("<input type=\"hidden\" name=\"def" + listBoxRefName + "\" value=\"Y\" />");
		sbsub.append("<input type=\"hidden\" name=\"hiddisp" + listBoxRefName + "\"  value=\"" + listBoxDispName + "\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		return sbsub.toString();

	}

	/**
		 * 
		 * To parse the problem type and returns the issue type
		 * @param prob_type
		 * @return
		 * @throws Exception
		 */

	public EtsDropDownDataBean getIssueTypeDropDownAttrib(String prob_type) throws Exception {

		return comGuiUtils.getIssueTypeDropDownAttrib(prob_type);

	}

	/**
		 * to get issue val for a given pattern
		 * token format >> 	DATA_ID $ ISSUE_TYPE $ ISSUE_SOURCE $ ISSUE_ACCESS $ SUBTYPE_A
		 */

	public String getDelimitIssueVal(String prob_type) throws Exception {

		return comGuiUtils.getDelimitIssueVal(prob_type);

	}

	/**
	 * To print dynamic problem submission
	 * 
	 * @param usr1Model
	 * @return
	 */

	public String printDynamicProblemSubmForms(EtsIssProbInfoUsr1Model usr1InfoModel) throws Exception {

		StringBuffer sbsub = new StringBuffer();

		//states
		int actionstate = usr1InfoModel.getCurrentActionState();
		int usr1nextstate = usr1InfoModel.getNextActionState();

		if (usr1nextstate > 0) {

			actionstate = usr1nextstate;
		}

		String defSubTypeAVal = usr1InfoModel.getSubTypeADefnd();
		String defSubTypeBVal = usr1InfoModel.getSubTypeBDefnd();
		String defSubTypeCVal = usr1InfoModel.getSubTypeCDefnd();
		String defSubTypeDVal = usr1InfoModel.getSubTypeDDefnd();

		String editSubTypeAVal = usr1InfoModel.getEditSubTypeADefnd();
		String editSubTypeBVal = usr1InfoModel.getEditSubTypeBDefnd();
		String editSubTypeCVal = usr1InfoModel.getEditSubTypeCDefnd();
		String editSubTypeDVal = usr1InfoModel.getEditSubTypeDDefnd();

		//			/curr Ref vals
		String subTypeARefName = usr1InfoModel.getSubTypeARefName();
		String subTypeBRefName = usr1InfoModel.getSubTypeBRefName();
		String subTypeCRefName = usr1InfoModel.getSubTypeCRefName();
		String subTypeDRefName = usr1InfoModel.getSubTypeDRefName();

		///curr Disp vals
		String subTypeADispName = usr1InfoModel.getSubTypeADispName();
		String subTypeBDispName = usr1InfoModel.getSubTypeBDispName();
		String subTypeCDispName = usr1InfoModel.getSubTypeCDispName();
		String subTypeDDispName = usr1InfoModel.getSubTypeDDispName();

		//curr lists
		ArrayList dispSubTypeAValList = usr1InfoModel.getSubTypeAList();
		ArrayList dispSubTypeBValList = usr1InfoModel.getSubTypeBList();
		ArrayList dispSubTypeCValList = usr1InfoModel.getSubTypeCList();
		ArrayList dispSubTypeDValList = usr1InfoModel.getSubTypeDList();

		//prev lists
		ArrayList prevSubTypeAValList = usr1InfoModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBValList = usr1InfoModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCValList = usr1InfoModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDValList = usr1InfoModel.getPrevSubTypeDList();

		//			/sizes of arraylists//
		int subadispsize = 0;
		int subbdispsize = 0;
		int subcdispsize = 0;
		int subddispsize = 0;

		//
		int nextstate = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeAValList)) {

			subadispsize = dispSubTypeAValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeBValList)) {

			subbdispsize = dispSubTypeBValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeCValList)) {

			subcdispsize = dispSubTypeCValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeDValList)) {

			subddispsize = dispSubTypeDValList.size();
		}

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		Global.println("action state in print Dynamic sub forms==" + actionstate);

		switch (actionstate) {

			//case CONTDESCR :

			case ADDISSUETYPE :

			case EDITSUBTYPEA :

				if (subadispsize > 0) {

					if (!dispSubTypeAValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeADispName, subTypeARefName, dispSubTypeAValList, prevSubTypeAValList));
						nextstate = GOSUBTYPEA;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeARefName, ETSDEFAULTCQ));
						nextstate = CONTIDENTF;

					}

				} else {

					nextstate = CONTIDENTF;

				}

				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case GOSUBTYPEA :

			case EDITSUBTYPEB :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subbdispsize > 0) {

					if (!dispSubTypeBValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeBDispName, subTypeBRefName, dispSubTypeBValList, prevSubTypeBValList));
						nextstate = GOSUBTYPEB;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeBRefName, ETSDEFAULTCQ));
						nextstate = CONTIDENTF;

					}

				} else {

					nextstate = CONTIDENTF;

				}

				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case GOSUBTYPEB :

			case EDITSUBTYPEC :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subcdispsize > 0) {

					if (!dispSubTypeCValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeCDispName, subTypeCRefName, dispSubTypeCValList, prevSubTypeCValList));
						nextstate = GOSUBTYPEC;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeCRefName, ETSDEFAULTCQ));
						nextstate = CONTIDENTF;

					}

				} else {

					nextstate = CONTIDENTF;

				}
				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case GOSUBTYPEC :

			case EDITSUBTYPED :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {
					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCValList) && !prevSubTypeCValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeCDispName, "op_153", prevSubTypeCValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subddispsize > 0) {

					if (!dispSubTypeDValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeDDispName, subTypeDRefName, dispSubTypeDValList, prevSubTypeDValList));
						nextstate = CONTIDENTF;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeDRefName, ETSDEFAULTCQ));
						nextstate = CONTIDENTF;

					}

				} else {

					//check ??? why this is reqd??
					//sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
					nextstate = CONTIDENTF;

				}
				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case EDITISSUEIDENTF :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCValList) && !prevSubTypeCValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeCDispName, "op_153", prevSubTypeCValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDValList) && !prevSubTypeDValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListBox(subTypeDDispName, subTypeDRefName, dispSubTypeDValList, prevSubTypeDValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				boolean showstaticforms = false;

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC1List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC2List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC3List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC4List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC5List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC6List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC7List())) {

					showstaticforms = true;
				}

				if (AmtCommonUtils.isResourceDefined(usr1InfoModel.getTestCase())) {

					showstaticforms = true;
				}

				if (showstaticforms) {
					sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				nextstate = CONTIDENTF;

				break;

		} //end of switch

		sbsub.append("</table>\n");

		sbsub.append(printGreyLine());

		sbsub.append("<br />");

		Global.println("Next state in printDynamic sub forms===" + nextstate);

		sbsub.append(printContCancel(nextstate));

		return sbsub.toString();

	}

	/**
		 * to print the common select for subm forms for static lists
		 * 
		 */
	public String printStaticSubmFrmListBox(String listBoxDispName, String listBoxRefName, ArrayList dropValList, ArrayList prevDropValList) {

		StringBuffer sbsub = new StringBuffer();
		EtsIssFilterGuiUtils guiUtil = new EtsIssFilterGuiUtils();

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><span class=\"ast\"><b>*</b></span><label for=\"lb" + listBoxRefName + "\"><b>" + listBoxDispName + "</b>:</label></td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
		sbsub.append("<select id=\"lb" + listBoxRefName + "\" name=\"" + listBoxRefName + "\" size=\"\" align=\"left\" class=\"iform\" style=\"width:150px\" width=\"150px\" >");
		sbsub.append("<option value=\"NONE\">Select " + listBoxDispName + " </option>\n");
		sbsub.append(guiUtil.printSelectOptions(dropValList, prevDropValList));
		sbsub.append("</select>");
		sbsub.append("<input type=\"hidden\" name=\"def" + listBoxRefName + "\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		return sbsub.toString();

	}

	/***
		 * this method will contain the html tags to print issue submission forms
		 * 
		 */

	String printStaticProblemSubmForms(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbsub = new StringBuffer();

		///curr Ref vals
		String fieldC1RefName = usr1InfoModel.getFieldC1RefName();
		String fieldC2RefName = usr1InfoModel.getFieldC2RefName();
		String fieldC3RefName = usr1InfoModel.getFieldC3RefName();
		String fieldC4RefName = usr1InfoModel.getFieldC4RefName();
		String fieldC5RefName = usr1InfoModel.getFieldC5RefName();
		String fieldC6RefName = usr1InfoModel.getFieldC6RefName();
		String fieldC7RefName = usr1InfoModel.getFieldC7RefName();

		///curr Disp vals
		String fieldC1DispName = usr1InfoModel.getFieldC1DispName();
		String fieldC2DispName = usr1InfoModel.getFieldC2DispName();
		String fieldC3DispName = usr1InfoModel.getFieldC3DispName();
		String fieldC4DispName = usr1InfoModel.getFieldC4DispName();
		String fieldC5DispName = usr1InfoModel.getFieldC5DispName();
		String fieldC6DispName = usr1InfoModel.getFieldC6DispName();
		String fieldC7DispName = usr1InfoModel.getFieldC7DispName();

		//curr lists
		ArrayList fieldC1ValList = usr1InfoModel.getFieldC1List();
		ArrayList fieldC2ValList = usr1InfoModel.getFieldC2List();
		ArrayList fieldC3ValList = usr1InfoModel.getFieldC3List();
		ArrayList fieldC4ValList = usr1InfoModel.getFieldC4List();
		ArrayList fieldC5ValList = usr1InfoModel.getFieldC5List();
		ArrayList fieldC6ValList = usr1InfoModel.getFieldC6List();
		ArrayList fieldC7ValList = usr1InfoModel.getFieldC7List();

		//prev lists
		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();

		//test case value
		String testCaseVal = usr1InfoModel.getTestCase();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");

		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC1ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC1DispName, fieldC1RefName, fieldC1ValList, prevFieldC1ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC2ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC2DispName, fieldC2RefName, fieldC2ValList, prevFieldC2ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC3ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC3DispName, fieldC3RefName, fieldC3ValList, prevFieldC3ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC4ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC4DispName, fieldC4RefName, fieldC4ValList, prevFieldC4ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC5ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC5DispName, fieldC5RefName, fieldC5ValList, prevFieldC5ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC6ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC6DispName, fieldC6RefName, fieldC6ValList, prevFieldC6ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC7ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC7DispName, fieldC7RefName, fieldC7ValList, prevFieldC7ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		sbsub.append(printTestCase(testCaseVal));

		

		sbsub.append("</table>");

		return sbsub.toString();
	}

	/***
		 * 
		 * to print Issue description
		 * 
		 */

	public String printTestCase(String prevTestCaseVal) {

		StringBuffer sbsub = new StringBuffer();

		String test_case = "";

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\">");
		sbsub.append("<span class=\"ast\"><b>*</b></span><label for=\"pdesc\"><b>Test case</b>:</label>");
		sbsub.append("</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

		sbsub.append(printStdTextArea("lbltcase", "testcase", prevTestCaseVal));
		sbsub.append("<input type=\"hidden\" name=\"deftestcase\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		sbsub.append("<tr>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\">&nbsp;</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\"><span");
		sbsub.append("class=\"small\">[ maximum 1000 characters ]</span></td>");
		sbsub.append("</tr>");

		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		return sbsub.toString();
	}

	/**
		 * to print the common select for subm forms
		 * 
		 */
	String printDynSubmFrmListSelecText(String listBoxDispName, String listBoxRefName, ArrayList prevSubTypeValList) throws Exception {

		StringBuffer sbsub = new StringBuffer();
		String dispValue = "";

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeValList)) {

			dispValue = (String) prevSubTypeValList.get(0);

		}

		sbsub.append("<tr >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>" + listBoxDispName + "</b>:</td>");

		//
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

		////////////////////////////
		//inner table 1//
		sbsub.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"323\">");
		sbsub.append("<tr>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"150\" >" + dispValue + " ");
		sbsub.append("</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"5\">&nbsp;</td>");

		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"168\">");

		//inner table 2//
		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"75%\">");
		sbsub.append("<tr>");
		sbsub.append("<td  align=\"left\" >");
		sbsub.append("<input type=\"image\"  name =\"" + listBoxRefName + "\" src=\"" + Defines.BUTTON_ROOT  + "arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Edit " + listBoxDispName + "\" />");
		sbsub.append("</td>");
		sbsub.append("<td   align=\"left\" >");
		sbsub.append("<b>Edit " + listBoxDispName + "</b> ");
		sbsub.append("</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");
		///inner 2 tab end//
		sbsub.append("</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");
		///inner 1 tab end//

		//
		sbsub.append("<input type=\"hidden\" name=\"edit" + listBoxRefName + "\"  value=\"" + dispValue + "\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		///////////////////////////////

		return sbsub.toString();

	}

	/**
	 * To print the standard issue and issue sub types and field c1..7 and test case
	 * if they are defined
	 * 
	 */

	public String printScrn3FinalIssueIdentDetails(EtsIssProbInfoUsr1Model usrParamsInfo1, EtsIssObjectKey issactionobjkey) throws Exception {

		StringBuffer sbsub = new StringBuffer();

		//		

		ArrayList prevFieldC1ValList = usrParamsInfo1.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usrParamsInfo1.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usrParamsInfo1.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usrParamsInfo1.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usrParamsInfo1.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usrParamsInfo1.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usrParamsInfo1.getPrevFieldC7List();
		String testcase = usrParamsInfo1.getTestCase();

		ArrayList prevSubTypeAList = usrParamsInfo1.getPrevSubTypeAList();
		ArrayList prevSubTypeBList = usrParamsInfo1.getPrevSubTypeBList();
		ArrayList prevSubTypeCList = usrParamsInfo1.getPrevSubTypeCList();
		ArrayList prevSubTypeDList = usrParamsInfo1.getPrevSubTypeDList();

		ArrayList prevProbTypeList = usrParamsInfo1.getPrevProbTypeList();
		String issueType = AmtCommonUtils.getTrimStr(usrParamsInfo1.getIssueType());

		//		get any field values if any

		String defSubTypeAVal = usrParamsInfo1.getSubTypeADefnd();
		String defSubTypeBVal = usrParamsInfo1.getSubTypeBDefnd();
		String defSubTypeCVal = usrParamsInfo1.getSubTypeCDefnd();
		String defSubTypeDVal = usrParamsInfo1.getSubTypeDDefnd();

		///to check if they are defined atleast
		String defFieldC1 = usrParamsInfo1.getFieldC1Defnd();
		String defFieldC2 = usrParamsInfo1.getFieldC2Defnd();
		String defFieldC3 = usrParamsInfo1.getFieldC3Defnd();
		String defFieldC4 = usrParamsInfo1.getFieldC4Defnd();
		String defFieldC5 = usrParamsInfo1.getFieldC5Defnd();
		String defFieldC6 = usrParamsInfo1.getFieldC6Defnd();
		String defFieldC7 = usrParamsInfo1.getFieldC7Defnd();
		String deftestcase = usrParamsInfo1.getFieldC1Defnd();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

		//check for prob type

		if (EtsIssFilterUtils.isArrayListDefnd(prevProbTypeList)) {

			if (!prevProbTypeList.contains("NONE") && !prevProbTypeList.contains("")) {

				sbsub.append("<tr>\n");
				sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + getDefaultIssueTypeLabel(issactionobjkey) + "</b>:</td>\n");
				sbsub.append("<td   align=\"left\" width=\"75%\">\n");
				//sbsub.append("" + getDelimitIssueVal((String) prevProbTypeList.get(0)) + "");
				sbsub.append(issueType);
				sbsub.append("</td>\n");
				sbsub.append("</tr>\n");
				//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

			}

		}

		//		check for sub type a

		if (AmtCommonUtils.isResourceDefined(defSubTypeAVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

				if (!prevSubTypeAList.contains("NONE") && !prevSubTypeAList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getSubTypeADispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevSubTypeAList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for sub type b
		if (AmtCommonUtils.isResourceDefined(defSubTypeBVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

				if (!prevSubTypeBList.contains("NONE") && !prevSubTypeBList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getSubTypeBDispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevSubTypeBList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for sub type c
		if (AmtCommonUtils.isResourceDefined(defSubTypeCVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

				if (!prevSubTypeCList.contains("NONE") && !prevSubTypeCList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getSubTypeCDispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevSubTypeCList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for sub type d
		if (AmtCommonUtils.isResourceDefined(defSubTypeDVal)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

				if (!prevSubTypeDList.contains("NONE") && !prevSubTypeDList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getSubTypeDDispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevSubTypeDList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c1
		if (AmtCommonUtils.isResourceDefined(defFieldC1)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList)) {

				if (!prevFieldC1ValList.contains("NONE") && !prevFieldC1ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC1DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC1ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c2
		if (AmtCommonUtils.isResourceDefined(defFieldC2)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)) {

				if (!prevFieldC2ValList.contains("NONE") && !prevFieldC2ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC2DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC2ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c3
		if (AmtCommonUtils.isResourceDefined(defFieldC3)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)) {

				if (!prevFieldC3ValList.contains("NONE") && !prevFieldC3ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC3DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC3ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c4
		if (AmtCommonUtils.isResourceDefined(defFieldC4)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)) {

				if (!prevFieldC4ValList.contains("NONE") && !prevFieldC4ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC4DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC4ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c5
		if (AmtCommonUtils.isResourceDefined(defFieldC5)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)) {

				if (!prevFieldC5ValList.contains("NONE") && !prevFieldC5ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC5DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC5ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c6

		if (AmtCommonUtils.isResourceDefined(defFieldC6)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)) {

				if (!prevFieldC6ValList.contains("NONE") && !prevFieldC6ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC6DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC6ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for field c7

		if (AmtCommonUtils.isResourceDefined(defFieldC7)) {

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)) {

				if (!prevFieldC7ValList.contains("NONE") && !prevFieldC7ValList.contains("")) {

					sbsub.append("<tr>\n");
					sbsub.append("<td   align=\"left\" width=\"25%\"><b>" + usrParamsInfo1.getFieldC7DispName() + "</b>:</td>\n");
					sbsub.append("<td   align=\"left\" width=\"75%\">\n");
					sbsub.append("" + prevFieldC7ValList.get(0) + "");
					sbsub.append("</td>\n");
					sbsub.append("</tr>\n");
					//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

				}

			}

		}

		//		check for test case

		if (AmtCommonUtils.isResourceDefined(deftestcase)) {

			if (AmtCommonUtils.isResourceDefined(testcase)) {

				sbsub.append("<tr>\n");
				sbsub.append("<td   align=\"left\" width=\"25%\"><b>Test case</b>:</td>\n");
				sbsub.append("<td   align=\"left\" width=\"75%\">\n");
				sbsub.append("<pre>");
				sbsub.append("" + testcase + "");
				sbsub.append("</pre>");
				sbsub.append("</td>\n");
				sbsub.append("</tr>\n");
				//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");

			}

		}

		//		sbsub.append("<tr>\n");
		//		sbsub.append("<td   align=\"left\" width=\"443\" >\n");
		//		sbsub.append(printEditIssueBtn("op_17", "Edit issue identification"));
		//		sbsub.append("</td>\n");
		//		sbsub.append("</tr>\n");
		sbsub.append("</table>\n");

		return sbsub.toString();

	}

	/**
	 * To print the model details for debug
	 */

	public void debugUsr1ModelDetails(EtsIssProbInfoUsr1Model usr1Model) {

		if (usr1Model != null) {

			int prevstate = usr1Model.getPreviousActionState();
			int curstate = usr1Model.getCurrentActionState();
			int nextstate = usr1Model.getNextActionState();
			int cancelstate = usr1Model.getCancelActionState();
			int subtypestate = usr1Model.getSubtypeActionState();

			Global.println("Current state===" + curstate);
			Global.println("Next state===" + nextstate);
			Global.println("Cancel state===" + cancelstate);
			Global.println("subtypestate===" + subtypestate);

			//			usr info
			String errMsg = AmtCommonUtils.getTrimStr(usr1Model.getErrMsg());
			Global.println("errMsg===" + errMsg);

			String edge_problem_id = AmtCommonUtils.getTrimStr(usr1Model.getEdgeProblemId());
			Global.println("edge_problem_id===" + edge_problem_id);
			//usr info
			String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
			String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
			String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
			String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());

			Global.println("Printing customer profile @@@@@");
			Global.println("Cust Name====" + curIssueCustName);
			Global.println("Cust Email====" + curIssueCustEmail);
			Global.println("Cust Phone====" + curIssueCustPhone);
			Global.println("Cust Company====" + curIssueCustCompany);

			//issue descr info
			String severity = "";
			ArrayList prevSevList = usr1Model.getPrevProbSevList();

			if (prevSevList != null && !prevSevList.isEmpty()) {

				severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

			}

			String probTitle = AmtCommonUtils.getTrimStr(usr1Model.getProbTitle());
			String probDesc = AmtCommonUtils.getTrimStr(usr1Model.getProbDesc());
			String issueTypeId = AmtCommonUtils.getTrimStr(usr1Model.getIssueTypeId());

			Global.println("Printing issue descr profile @@@@@");
			Global.println("severity====" + severity);
			Global.println("probTitle====" + probTitle);
			Global.println("probDesc====" + probDesc);
			Global.println("ISSUE TYPE ID====" + issueTypeId);

			Global.println("Printing issue identf profile @@@@@");

			ArrayList prevFieldC1ValList = usr1Model.getPrevFieldC1List();
			ArrayList prevFieldC2ValList = usr1Model.getPrevFieldC2List();
			ArrayList prevFieldC3ValList = usr1Model.getPrevFieldC3List();
			ArrayList prevFieldC4ValList = usr1Model.getPrevFieldC4List();
			ArrayList prevFieldC5ValList = usr1Model.getPrevFieldC5List();
			ArrayList prevFieldC6ValList = usr1Model.getPrevFieldC6List();
			ArrayList prevFieldC7ValList = usr1Model.getPrevFieldC7List();
			String testcase = AmtCommonUtils.getTrimStr(usr1Model.getTestCase());

			ArrayList prevSubTypeAList = usr1Model.getPrevSubTypeAList();
			ArrayList prevSubTypeBList = usr1Model.getPrevSubTypeBList();
			ArrayList prevSubTypeCList = usr1Model.getPrevSubTypeCList();
			ArrayList prevSubTypeDList = usr1Model.getPrevSubTypeDList();

			ArrayList prevProbTypeList = usr1Model.getPrevProbTypeList();

			//display names///
			String subtypeADispName = AmtCommonUtils.getTrimStr(usr1Model.getSubTypeADispName());
			String subtypeBDispName = AmtCommonUtils.getTrimStr(usr1Model.getSubTypeBDispName());
			String subtypeCDispName = AmtCommonUtils.getTrimStr(usr1Model.getSubTypeCDispName());
			String subtypeDDispName = AmtCommonUtils.getTrimStr(usr1Model.getSubTypeDDispName());

			String fieldC1DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC1DispName());
			String fieldC2DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC2DispName());
			String fieldC3DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC3DispName());
			String fieldC4DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC4DispName());
			String fieldC5DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC5DispName());
			String fieldC6DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC6DispName());
			String fieldC7DispName = AmtCommonUtils.getTrimStr(usr1Model.getFieldC7DispName());

			//check for prob type

			if (EtsIssFilterUtils.isArrayListDefnd(prevProbTypeList)) {

				Global.println("issue type val====" + prevProbTypeList.get(0));

			}

			//		check for sub type a

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

				Global.println("sub type-A type name====" + subtypeADispName);
				Global.println("sub type-A type val====" + prevSubTypeAList.get(0));

			}

			//		check for sub type b

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

				Global.println("sub type-B type name====" + subtypeBDispName);
				Global.println("sub type-B type val====" + prevSubTypeBList.get(0));

			}

			//		check for sub type c

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

				Global.println("sub type-C type name====" + subtypeCDispName);
				Global.println("sub type-C type val====" + prevSubTypeCList.get(0));

			}

			//		check for sub type d

			if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

				Global.println("sub type-D type name====" + subtypeDDispName);
				Global.println("sub type-D type val====" + prevSubTypeDList.get(0));

			}

			//		check for field c1

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList)) {

				Global.println("Field C1 type name====" + fieldC1DispName);
				Global.println("Field C1 val====" + prevFieldC1ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList)) {

				Global.println("Field C2 type name====" + fieldC2DispName);
				Global.println("Field C2 val====" + prevFieldC2ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)) {

				Global.println("Field C3 type name====" + fieldC3DispName);
				Global.println("Field C3 val====" + prevFieldC3ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList)) {

				Global.println("Field C4 type name====" + fieldC4DispName);
				Global.println("Field C4 val====" + prevFieldC4ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList)) {

				Global.println("Field C5 type name====" + fieldC5DispName);
				Global.println("Field C5 val====" + prevFieldC5ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList)) {

				Global.println("Field C6 type name====" + fieldC6DispName);
				Global.println("Field C6 val====" + prevFieldC6ValList.get(0));

			}

			if (EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList)) {

				Global.println("Field C7 type name====" + fieldC7DispName);
				Global.println("Field C7 val====" + prevFieldC7ValList.get(0));

			}

			//		check for test case

			if (AmtCommonUtils.isResourceDefined(testcase)) {

				Global.println("test case====" + testcase);

			}

			String issueSource = AmtCommonUtils.getTrimStr(usr1Model.getIssueSource());
			String issueAccess = AmtCommonUtils.getTrimStr(usr1Model.getIssueAccess());

			Global.println("issue  Source====" + issueSource);
			Global.println("issue  Access====" + issueAccess);

			String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usr1Model.getChkIssTypeIbmOnly());
			Global.println("chkIssTypeIbmOnly====" + chkIssTypeIbmOnly);

			Global.println("ETS ISSUES TYPES===" + AmtCommonUtils.getTrimStr(usr1Model.getEtsIssuesType()));

			//////////////end

		} else {

			Global.println("usr1 model is null @@@@");

		}

	}

	/**
	 * 
	 */

	public String printPrevStaticFormsList(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbsub = new StringBuffer();

		boolean showstaticforms = false;

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC1List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC2List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC3List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC4List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC5List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC6List())) {

			showstaticforms = true;
		}

		if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC7List())) {

			showstaticforms = true;
		}

		if (AmtCommonUtils.isResourceDefined(usr1InfoModel.getTestCase())) {

			showstaticforms = true;
		}

		if (showstaticforms) {
			sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
		}

		return sbsub.toString();

	}

	/**
		 * 
		 */
	public String printHidCancelActionState(EtsIssProbInfoUsr1Model usr1InfoModel) {

		return comGuiUtils.printHidCancelActionState(usr1InfoModel);

	}

	/**
		 * 
		 */

	public String printHidSubtypeActionState(EtsIssProbInfoUsr1Model usr1InfoModel) {

		return comGuiUtils.printHidSubtypeActionState(usr1InfoModel);

	}

		

	
	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */

	public String printAccessCntrl(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sb = new StringBuffer();
		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());
		String propAccMsg = "";

		//5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		//show this option only for IBM Internals//

		if (userType.equals("I")) {

			if (issueAccess.equals("ALL:EXT")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg5");

				//print section header
				sb.append(printSectnHeader((String) propMap.get("issue.act.step4")));

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  colspan=\"2\"  width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				/*sb.append("<tr valign=\"top\" >\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>IBM only</b>:</td>\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");
				
				if (chkIssTypeIbmOnly.equals("Y")) {
				
					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" checked=\"checked\" class=\"iform\" />\n");
				
				} else {
				
					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" />\n");
				
				}
				sb.append("</td>\n");
				sb.append("</tr>\n");*/
				//sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

			if (issueAccess.equals("IBM:IBM")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg51");

				//print section header
				sb.append(printSectnHeader((String) propMap.get("issue.act.step4")));

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  width=\"600\" colspan=\"2\" ><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr valign=\"top\" >\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>IBM only</b>:</td>\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");
				sb.append("<input type=\"checkbox\" name=\"chkissibmonly1\" value=\"Y\" id=\"lblchkibm\" checked=\"checked\" class=\"iform\"  disabled=\"disabled\" />\n");
				sb.append("<input type=\"hidden\" name=\"chkissibmonly\" value=\"Y\"  />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

			if (issueAccess.equals("ALL:IBM")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg52");

				//	print section header
				sb.append(printSectnHeader((String) propMap.get("issue.act.step4")));

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  width=\"600\" colspan=\"2\" ><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr valign=\"top\" >\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>IBM only</b>:</td>\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");

				if (chkIssTypeIbmOnly.equals("Y")) {

					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" checked=\"checked\" class=\"iform\" />\n");

				} else {

					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" />\n");

				}
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

			sb.append(printBlankLine("4"));

		} //show only for internals

		return sb.toString();

	}

	/**
	 * 
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @return
	 */

	public String printChkIssTypeIbmOnly(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbsub = new StringBuffer();
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		if (userType.equals("I")) { //only for internals

			Global.println("chkIssTypeIbmOnly in print check Iss Type Ibm Only==" + chkIssTypeIbmOnly);

			//	print section header
			sbsub.append(printSectnHeader((String) propMap.get("issue.act.step4")));

			sbsub.append("<br />");

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			//sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

			sbsub.append("<tr valign=\"top\" >");

			if (chkIssTypeIbmOnly.equals("Y")) {

				sbsub.append("<td valign=\"top\" width=\"25%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"75%\" >IBM team member only</td>");

			} else {

				sbsub.append("<td valign=\"top\" width=\"25%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"75%\" >Any team member in workspace can access this issue.</td>");

			}
			sbsub.append("</tr>");
			sbsub.append("</table>");

			sbsub.append("<br />");

			//print edit restrict button
			sbsub.append(printEditIssueBtn("op_23", "Edit security classification"));
			sbsub.append(printGreyLine());
			sbsub.append("<br />");

		}

		return sbsub.toString();

	}

	/**
	 * 
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */

	public String printNotifyList(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbsub = new StringBuffer();

		ArrayList notifyList = usr1InfoModel.getNotifyList();
		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();
		String chkIssIbmType = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());
		String propMsg = "";
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		if (userType.equals("I")) { //only for internals
			//	print section header as step 5
			sbsub.append(printSectnHeader((String) propMap.get("issue.act.step5")));

		} else {

			//	print section header as step 4 for externals
			sbsub.append(printSectnHeader((String) propMap.get("issue.act.step4ex")));

		}

		//get prop mgs
		propMsg = (String) propMap.get("issue.act.new.msg6");

		//String userType = etsIssObjKey.getEs().gDECAFTYPE;

		Global.println("NOTIFY LIST USER TYPE===" + userType);

		//print notify list
		sbsub.append(printNotifyList(notifyList, prevNotifyList, propMsg, userType, chkIssIbmType));

		//5.2.1 chk if it is for blade project
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		if (!etsIssObjKey.isProjBladeType()) {

			return sbsub.toString();

		} else {

			if (!extBhf.equals("1")) { //if non ext bhf case

				if (usrRolesModel.isBladeUsrInt()) {

					return sbsub.toString();

				} else {

					return "";
				}

			} else { //if ext bhf case, the user should not see notify list at all

				return "";

			}

		} //end of proj blade type

		//return sbsub.toString();

	}
	/**
	 * 
	 * @param notifyList
	 * @param prevNotifyList
	 * @param propMsg
	 * @param userType
	 * @param chkIssIbmType
	 * @return
	 */

	public String printNotifyList(ArrayList notifyList, ArrayList prevNotifyList, String propMsg, String userType, String chkIssIbmType) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
		sbsub.append(propMsg);
		sbsub.append("</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" ><span class=\"small\">[ To select more than one name, hold Ctrl and click the names. ]</span></td></tr>\n");
		sbsub.append("<tr valign=\"top\" >\n");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"443\">\n");
		sbsub.append("<select id=\"ff5\" multiple=\"multiple\" size=\"5\" name=\"notifylist\"  align=\"left\" class=\"iform\" style=\"width:443px\" width=\"443px\"  >\n");
		sbsub.append(fltrGuiUtils.printSelectOptionsWithValue(notifyList, prevNotifyList));
		sbsub.append("</select>\n");
		sbsub.append("</td>\n");
		sbsub.append("</tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		if (userType.equals("I") && !chkIssIbmType.equals("Y")) {

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
			sbsub.append("<b>* IBM employee</b>");
			sbsub.append("</td>\n");
			sbsub.append("</tr>\n");

		}

		sbsub.append("</table>\n");

		return sbsub.toString();

	}

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 */

	public String printPrevNotifyList(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws SQLException, Exception {

		StringBuffer sbsub = new StringBuffer();

		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();

		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		String propMsg = "";

		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		//get the display members
		ArrayList projMemList = projMemDao.getProjMemberListFromEdgeId(prevNotifyList);

		//5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		if (userType.equals("I")) { //only for internals
			//	print section header as step 5
			sbsub.append(printSectnHeader((String) propMap.get("issue.act.step5")));

		} else {

			//	print section header as step 4 for externals
			sbsub.append(printSectnHeader((String) propMap.get("issue.act.step4ex")));

		}

		int notsize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			notsize = projMemList.size();

		}

		sbsub.append("<br />\n");
		propMsg = getDynNotifyListMsg(notsize);
		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
		sbsub.append(propMsg);
		sbsub.append("</td></tr>\n");
		sbsub.append("</table>\n");

		if (notsize > 0) {

			sbsub.append("<br />\n");
			sbsub.append(printGreyLine());

		}
		sbsub.append("<br />\n");

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		sbsub.append("<tr>\n");

		if (notsize > 0) {

			sbsub.append("<td  colspan=\"2\" height=\"18\" ><b>Notification list<b/></td></tr>\n");

		}

		for (int i = 0; i < notsize; i++) {

			sbsub.append("<tr valign=\"top\" >\n");
			sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">\n");
			sbsub.append(projMemList.get(i));
			sbsub.append("</td>\n");

			sbsub.append("</tr>\n");

		}

		sbsub.append("</table>\n");

		sbsub.append("<br />");
		sbsub.append(printEditIssueBtn("op_25", "Edit notification list"));

		sbsub.append("<br />");

		sbsub.append(printGreyLine());

		sbsub.append("<br />");

		//5.2.1	chk if it is for blade project
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		if (!etsIssObjKey.isProjBladeType()) {

			return sbsub.toString();

		} else { //if blade project type

			if (!extBhf.equals("1")) { //if not ext bhf

				if (usrRolesModel.isBladeUsrInt()) {

					return sbsub.toString();

				} else {

					return "";
				}

			} else { //if ext bhf

				return "";

			}

		} //end of blade proj type

		//return sbsub.toString();

	}

	public String printStdEndActionsForNewIssue(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbsub = new StringBuffer();

		String probClass = usr1InfoModel.getProbClass();

		//		5.2.1 chk if it is for blade project
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		Global.println("EXT BHF====" + extBhf);

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");

		if (probClass.equals("Change")) {

			sbsub.append("<td width=\"18\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitChange&op=1&istyp=chg&flop=1" + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
			sbsub.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitChange&op=1&istyp=chg&flop=1" + "\" class=\"fbox\">Submit change request</a></td>");

		} else {

			if (!extBhf.equals("1")) { //if not ext bhf

				sbsub.append("<td width=\"18\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitIssue&op=1&istyp=iss&flop=1" + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbsub.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitIssue&op=1&istyp=iss&flop=1" + "\" class=\"fbox\">Submit new issue</a></td>");

			} else { //if ext bhf

				sbsub.append("<td width=\"18\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitIssue&op=3&istyp=iss&flop=1&extbhf=1" + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbsub.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitIssue&op=3&istyp=iss&flop=1&extbhf=1" + "\" class=\"fbox\">Submit a new issue for externals</a></td>");

			}

		}

		sbsub.append("<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "\"" + "><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	public String printStdEndActionsForNewCR(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");

		sbsub.append("<td width=\"18\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitChange&op=1&istyp=chg&flop=1" + "\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=submitChange&op=1&istyp=chg&flop=1" + "\" class=\"fbox\">Submit change request</a></td>");

		sbsub.append("<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "\"" + "><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}

	public String printReturnToMainPage(EtsIssObjectKey etsIssObjKey) {

		return comGuiUtils.printReturnToMainPage(etsIssObjKey);

	}

	public String getDynNotifyListMsg(int notifycount) {

		StringBuffer sbfile = new StringBuffer();

		if (notifycount == 0) {

			sbfile.append("Currently, there are no members on issue notification list. You can add or delete members to this list before submitting the issue.");
		}

		/*if (notifycount == 1) {
		
			sbfile.append("Currently, there is " + notifycount + " member on issue notification list. You can add or delete members to this list before submitting the issue. They will also be notified when any action is taken on this issue.");
		}
		
		if (notifycount > 1) {
		
			sbfile.append("Currently, there are " + notifycount + " members on issue notification list. You can add or delete members to this list before submitting the issue. They will also be notified when any action is taken on this issue.");
		
		}*/

		if (notifycount >= 1) {

			sbfile.append("Currently, you have listed the following team members to receive notification of the issue and any actions that might be taken on it. You can add or delete names before submitting the issue.");

		}

		return sbfile.toString();

	}

	/***
			 * 
			 * display Edit Issue Btn Comments  tag
			 */

	public String printStepHierchy(String step, EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbview = new StringBuffer();

		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		//get the user type
		String userType = usr1InfoModel.getUserType();

		if (userType.equals("I")) {

			sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> ");
			sbview.append("<tr><td  colspan=\"6\"> &nbsp;</td></tr>");
			sbview.append("<tr>");
			sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");
			if (step.equals("1")) {

				sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#0\">Description</a></b></span></span>");

			} else {

				sbview.append("1.&nbsp;Description");
			}

			sbview.append("</td>");
			sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
			if (step.equals("2")) {

				sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Identification</a></b></span>");

			} else {

				sbview.append("2.&nbsp;Identification");
			}

			sbview.append("</td>");
			sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");

			sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

			if (step.equals("3")) {

				sbview.append("3.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#2\">Attachments</a></b></span>");
			} else {
				sbview.append("3.&nbsp;Attachments");
			}

			sbview.append("</td>");

			sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
			if (step.equals("4")) {

				sbview.append("4.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#3\">Security classification</a></b></span>");
			} else {
				sbview.append("4.&nbsp;Security classification");
			}

			sbview.append("</td>");

			sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

			if (step.equals("5")) {

				sbview.append("5.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#4\">Notification</a></b></span>");
			} else {
				sbview.append("5.&nbsp;Notification");
			}

			sbview.append("</td>");
			//sbview.append("</tr>");
			//sbview.append("</table>");

			//sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> ");
			//sbview.append("<tr>");
			sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

			if (step.equals("6")) {

				sbview.append("6.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#5\">Submission</a></b></span>");
			} else {
				sbview.append("6.&nbsp;Submission");
			}

			sbview.append("</td>");
			sbview.append("</tr>");
			sbview.append("</table>");

		} else { //for externals

			//			start blade logic

			if (!etsIssObjKey.isProjBladeType()) {

				sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> ");
				sbview.append("<tr><td  colspan=\"5\"> &nbsp;</td></tr>");
				sbview.append("<tr>");
				sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");
				if (step.equals("1")) {

					sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#0\">Description</a></b></span>");

				} else {

					sbview.append("1.Description");
				}

				sbview.append("</td>");
				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
				if (step.equals("2")) {

					sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Identification</a></b></span>");

				} else {

					sbview.append("2.Identification");
				}

				sbview.append("</td>");
				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");

				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

				if (step.equals("3")) {

					sbview.append("3.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#2\">Attachments</a></b></span>");
				} else {
					sbview.append("3.Attachments");
				}

				sbview.append("</td>");

				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

				if (step.equals("5")) {

					sbview.append("4.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#4\">Notification</a></b></span>");
				} else {
					sbview.append("4.Notification");
				}

				sbview.append("</td>");

				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

				if (step.equals("6")) {

					sbview.append("5.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#5\">Submission</a></b></span>");
				} else {
					sbview.append("5.Submission");
				}

				sbview.append("</td>");

				sbview.append("</tr>");
				sbview.append("</table>");

			} else { //blade logic

				sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"343\"> ");
				sbview.append("<tr><td  colspan=\"7\"> &nbsp;</td></tr>");
				sbview.append("<tr>");
				sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");
				if (step.equals("1")) {

					sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#0\">Description</a></b></span>");

				} else {

					sbview.append("1.Description");
				}

				sbview.append("</td>");
				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");
				if (step.equals("2")) {

					sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Identification</a></b></span>");

				} else {

					sbview.append("2.Identification");
				}

				sbview.append("</td>");
				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");

				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

				if (step.equals("3")) {

					sbview.append("3.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#2\">Attachments</a></b></span>");
				} else {
					sbview.append("3.Attachments");
				}

				sbview.append("</td>");

				sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

				if (step.equals("6")) {

					sbview.append("4.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#5\">Submission</a></b></span>");
				} else {
					sbview.append("4.Submission");
				}

				sbview.append("</td>");

				sbview.append("</tr>");
				sbview.append("</table>");

			}

		}

		return sbview.toString();

	}

	/***
			* 
			* To print Tab index, print Contact Mod and messg
			*/

	public String printCommonIssHeaderWithStepHier(EtsAmtHfBean etsamthf, EtsIssObjectKey etsIssObjKey, String headMsg, String step) {

		StringBuffer sb = new StringBuffer();

		//get secure header
		sb.append(comGuiUtils.printSecureContentHeader(etsIssObjKey));

		sb.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sb.append("<tbody>");
		sb.append("<tr valign=\"top\">");
		//sb.append("<%-- print tabs --%>");

		sb.append("<td width=\"443\" valign=\"top\">" + etsamthf.getTabIndex() + "");
		//sb.append(printStepHierchy(step, etsIssObjKey));

		sb.append("<br />");

		//sb.append("<%-- table 2 for general info contact module start --%>");

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td height=\"18\" width=\"443\">" + headMsg + "</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 2 for general info contact module end --%>");
		sb.append("</td>");

		sb.append("<td width=\"7\"><img alt=\"\" src=\"//www.ibm.com/i/c.gif\" width=\"7\" height=\"1\" /></td>");

		sb.append("<td width=\"150\" valign=\"top\">" + etsamthf.getPrimaryContactModule() + "</td>");

		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		//sb.append("<%-- table 1 for tabs/right hand contact module end  --%>");

		return sb.toString();

	}

	/**
	 * 
	 * @param subtitle
	 * @return
	 */

	public String printSubTitle(String subtitle) {

		return comGuiUtils.printSubTitle(subtitle);
	}

	/**
	 * 
	 */

	public String printProcessMsg(String procMsg) {

		return comGuiUtils.printProcessMsg(procMsg);

	}

	public String printSubTitleProcMsg(String subtitle, String procMsg) {

		return printSubTitle(subtitle) + printProcessMsg(procMsg);
	}

	public String printSubTitleProcMsgBookMark(String sCaption, String sAnchor, boolean printbm, String procMsg) {

		return comGuiUtils.printBookMarkStr(sCaption, sAnchor, printbm) + printProcessMsg(procMsg);
	}

	/**
	 * 
	 * @param height
	 * @return
	 */

	public String printBlankLine(String height) {

		return comGuiUtils.printBlankLine(height);
	}

	/***
			 * 
			 * to print Issue description
			 * 
			 */

	public String printComments(String comLabelName, String prevComments) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sbsub.append("<tbody>");
		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		sbsub.append("<span class=\"ast\"><b>*</b></span><label for=\"pdesc\"><b>" + comLabelName + "</b>:</label>");
		sbsub.append("</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>\n");
		
		
		sbsub.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		sbsub.append(comGuiUtils.printStdCommentsTextArea("lblcomments", "comments", prevComments));
		sbsub.append("<input type=\"hidden\" name=\"defcomments\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		//		sbsub.append("<tr>");
		//		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		//		sbsub.append("<span class=\"small\">[ maximum 1000 characters ]</span>" +"");
		//		sbsub.append("</td>");
		//		sbsub.append("</tr>");

		sbsub.append("<tr><td  colspan=\"1\" height=\"18\" >&nbsp;</td></tr>\n");

		sbsub.append("</table>\n");

		return sbsub.toString();
	}

	public String printCommentsForCommments(String comLabelName, String prevComments) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"tab info\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
		sbsub.append("<tbody>");
		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		sbsub.append("<span class=\"ast\"><b>*</b></span><label for=\"pdesc\"><b>" + comLabelName + "</b>:</label>");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		sbsub.append(comGuiUtils.printStdCommentsTextArea("lblcomments", "comments", prevComments));
		sbsub.append("<input type=\"hidden\" name=\"defcomments\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		//			sbsub.append("<tr>");
		//			sbsub.append("<td  valign=\"top\" align=\"left\" width=\"100%\">");
		//			sbsub.append("<span class=\"small\">[ maximum 32650 characters ]</span>" +"");
		//			sbsub.append("</td>");
		//			sbsub.append("</tr>");

		sbsub.append("<tr><td  colspan=\"1\" height=\"18\" >&nbsp;</td></tr>\n");

		sbsub.append("</table>\n");

		return sbsub.toString();
	}

	/**
		 * to print the hidden default cq param
		 * 
		 */
	String printHidDefualtCq(String listBoxRefName, String hidDefCqVal) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"DFCQ" + listBoxRefName + "\" value=\"" + hidDefCqVal + "\" />");

		return sbsub.toString();

	}

	/**
	 * 
	 * @param printer friendly header
	 * @return
	 */

	public String printPrinterFriendlyHeader(String sPageTitle) {

		return comGuiUtils.printPrinterFriendlyHeader(sPageTitle);
	}

	/**
		 * 
		 * @param printer friendly footer
		 * @return
		 */

	public String printPrinterFriendlyFooter() {

		return comGuiUtils.printPrinterFriendlyFooter();
	}

	/**
	 * 
	 * @param edgeProblemId
	 * @return
	 */

	public String getEtsCCListStr(String edgeProblemId) {

		IssueInfoDAO issueDao = new IssueInfoDAO();

		String etsCCListStr = "";
		StringBuffer sbnotify = new StringBuffer();

		try {

			etsCCListStr = issueDao.getEtsCCList(edgeProblemId);

			Global.println("getEtsCCListStr ===" + etsCCListStr);

			ArrayList emailList = AmtCommonUtils.getArrayListFromStringTok(etsCCListStr, ",");

			int mailsize = emailList.size();

			for (int i = 0; i < mailsize; i++) {

				sbnotify.append(emailList.get(i));

				if (i != mailsize - 1) {

					sbnotify.append(", ");

				}

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getEtsCCListStr", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in getEtsCCListStr", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		Global.println("sb notify string ===" + sbnotify.toString());
		return sbnotify.toString();
	}

	public String getFinalActionMsg(EtsIssProbInfoUsr1Model usr1InfoModel, EtsIssObjectKey issactionobjkey) {

		StringBuffer sb = new StringBuffer();

		int actionKey = issactionobjkey.getActionkey();

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		String notifyListStr = "";
		boolean emailFlag=false;

		if (!issueSource.equals(ETSPMOSOURCE)) {

			notifyListStr = getEtsCCListStr(usr1InfoModel.getEdgeProblemId());

		} else {

			notifyListStr = getEtsCCListStrForPmoIssue(usr1InfoModel.getEdgeProblemId());

		}

		if (!AmtCommonUtils.isResourceDefined(notifyListStr)) {

			notifyListStr = "";
		}

		String emailId = issactionobjkey.getEs().gEMAIL;

		//for issue submitted ,the email can be either himself, or bhf of ext, so take email from usr1, cust_email,instead of frm session
		String custEmail ="";		
	

		if (actionKey == 1) {

			custEmail = usr1InfoModel.getCustEmail();
			emailFlag=true;							
			if(ETSUtils.isServerResponse()==true)
			{
				sb.append("You have successfully submitted the issue. Notification regarding this issue has been sent ");
			}
			
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully submitted the issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
						sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(custEmail,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}	
		}

		if (actionKey == 2) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully modified this issue. Notification regarding this action will be sent ");
			}
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully modified this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}	
		}

		if (actionKey == 3) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully resolved this issue. Notification regarding this action will be sent ");
			}
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully resolved this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}
		}

		if (actionKey == 5) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully rejected this issue. Notification regarding this action will be sent " + emailId + "");
			}
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully rejected this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}

		}

		if (actionKey == 6) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully closed this issue. Notification regarding this action will be sent ");
			}
			
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully closed this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}


		}

		if (actionKey == 19) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully added comments to this issue. Notification regarding this action will be sent ");
			}
			
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully added comments to this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}

		}

		if (actionKey == 23) {
			if(ETSUtils.isServerResponse()==true){
				sb.append("You have successfully withdrawn this issue. Notification regarding this action will be sent ");
			}
			if(ETSUtils.isServerResponse()==false)
			{	
				sb=new StringBuffer();
				sb.append("You have successfully withdrawn this issue.<br />");
				sb.append("\n");
				sb.append("<span style=\"color:#ff0000\">An error occured while sending notification </span><br />");
				if (notifyListStr != null && notifyListStr.length() > 0) {										
					sb.append("<span style=\"color:#ff0000\"> " + getUniqueEmailList(emailId,notifyListStr) + "</span> ");					
				}
				sb.append("<span style=\"color:#ff0000\">Notify these users separately for accessing the issue: "+getIssueId(usr1InfoModel.getEdgeProblemId())+" from workspace: "+usr1InfoModel.getCustProject()+"</span>");
			}

		}

		if (notifyListStr != null && notifyListStr.length() > 0) {			
		
			if((emailFlag==false)&&(ETSUtils.isServerResponse()==true))//this flag is for checking wheater its emailId or custEmail based on above condition.
			{													
				sb.append( getUniqueEmailList(emailId,notifyListStr) + " ");								
			}
			else if((emailFlag==true)&&(ETSUtils.isServerResponse()==true))
			{
				sb.append( getUniqueEmailList(custEmail,notifyListStr) + " ");				
			}
		}

		if(ETSUtils.isServerResponse()==true)
		{
			sb.append(" momentarily.");
		}		
		return sb.toString();

	}

	/**
		 * 
		 * @param edgeProblemId
		 * @return
		 */

	public String getEtsCCListStrForPmoIssue(String edgeProblemId) {

		IssueInfoDAO issueDao = new IssueInfoDAO();

		String etsCCListStr = "";
		StringBuffer sbnotify = new StringBuffer();

		try {

			etsCCListStr = issueDao.getEtsCCListForPmoIssue(edgeProblemId);

			Global.println("getEtsCCListStr for PMO ISSUES===" + etsCCListStr);

			ArrayList emailList = AmtCommonUtils.getArrayListFromStringTok(etsCCListStr, ",");

			int mailsize = emailList.size();

			for (int i = 0; i < mailsize; i++) {

				sbnotify.append(emailList.get(i));

				if (i != mailsize - 1) {

					sbnotify.append(", ");

				}

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in getEtsCCListStr", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in getEtsCCListStr", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		Global.println("sb notify string ===" + sbnotify.toString());
		return sbnotify.toString();
	}
	/**
	 * 
	 * @return
	 */

	public ArrayList getPMOIssueActionList() {

		ArrayList pmoActionList = new ArrayList();
		pmoActionList.add("Submit");
		pmoActionList.add("Modify");
		pmoActionList.add("Resolve");
		pmoActionList.add("Comment");
		pmoActionList.add("Reject");
		pmoActionList.add("Close");
		pmoActionList.add("Changeowner");

		return pmoActionList;

	}

	/**
		 * 
		 * @return
		 */

	public ArrayList getPMOIssueProbStateList() {

		ArrayList pmoStateList = new ArrayList();

		pmoStateList.add("Open");
		pmoStateList.add("Modified");
		pmoStateList.add("Resolved");
		pmoStateList.add("Rejected");
		pmoStateList.add("Closed");

		return pmoStateList;

	}

	/**
		 * get the static action/map 
		 * @return
		 */

	public HashMap getStateActionMap() {

		HashMap stateActMap = new HashMap();

		stateActMap.put("Open", "Submit");
		stateActMap.put("Modified", "Comment");
		stateActMap.put("Resolved", "Resolve");
		stateActMap.put("Rejected", "Reject");
		stateActMap.put("Closed", "Close");

		return stateActMap;
	}

	/**
		 * get state/action mapping
		 * @param probState
		 * @return
		 */

	public String getStateActionMapping(String probState) {

		String action = "";

		if (AmtCommonUtils.isResourceDefined(probState)) {

			action = (String) getStateActionMap().get(probState);
		}

		return action;

	}

	/**
				 * 
				 * @param stateAction
				 * @param txnFlag
				 * @return
				 */

	public String getUpdatedStateAction(String cqTrkId, String stateAction, String problemState, String txnFlag) {

		//			set the PCR properties in key obj in form of HaspMap//
		HashMap pcrPropMap = EtsPcrResource.getInstance().getPcrPropMap();

		return getUpdatedStateAction(pcrPropMap, cqTrkId, stateAction, problemState, txnFlag);

	}

	/**
	 * if cq trk id is not asigned, the final state is 'Submit'
	 * if cq trk id is assigned, and if txn flag status is not A/N/T, and if the state action is not Submit, then the
	 * final state is 'In process' otw, 'Submit'
	 * if cq trk assigned and if txn flag status is A, then final state is PROBLEM_STATE, otw show NACK/TIMED OUT states
	 */

	public String getUpdatedStateAction(HashMap pcrPropMap, String cqTrkId, String stateAction, String problemState, String txnFlag) {

		String resultState = "Submit";

		Global.println("state action===" + stateAction);

		//if cq trk id is NOT defined
		if (!AmtCommonUtils.isResourceDefined(cqTrkId)) {

			resultState = "Submit";

		} else { //combinations, if cq trk id is defined

			//if txn is in process, for first Submit action
			//if txn is in process, for remaining states, the state will be In Process							

			if (!txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_ACKED_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

				if (!stateAction.equals("Submit")) {

					resultState = "In Process";

				} else {

					resultState = "Submit";
				}

			} else { //if it falls into any A/N/T

				if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_ACKED_STATE"))) {

					resultState = problemState;

				} else if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {

					resultState = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.staction");

				} else if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

					resultState = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.staction");

				}

			}

		} //end of cq trk id not defined

		Global.println("resultState in updated getUpdated resultAction===" + resultState);

		return resultState;
	}

	/**
					 * 
					 * @param stateAction
					 * @param txnFlag
					 * @return
					 */

	public String getUpdatedStateActionForCR(String cqTrkId, String stateAction, String problemState, String txnFlag) {

		//			set the PCR properties in key obj in form of HaspMap//
		HashMap pcrPropMap = EtsPcrResource.getInstance().getPcrPropMap();

		return getUpdatedStateActionForCR(pcrPropMap, cqTrkId, stateAction, problemState, txnFlag);

	}

	/**
		 * if cq trk id is not asigned, the final state is 'Submit'
		 * if cq trk id is assigned, and if txn flag status is not A/N/T, and if the state action is not Submit, then the
		 * final state is 'In process' otw, 'Submit'
		 * if cq trk assigned and if txn flag status is A, then final state is PROBLEM_STATE, otw show NACK/TIMED OUT states
		 */

	public String getUpdatedStateActionForCR(HashMap pcrPropMap, String cqTrkId, String stateAction, String problemState, String txnFlag) {

		String resultState = "Under Review";

		Global.println("state action===" + stateAction);

		//if cq trk id is NOT defined
		if (!AmtCommonUtils.isResourceDefined(cqTrkId)) {

			resultState = "Under Review";

		} else { //combinations, if cq trk id is defined

			//if txn is in process, for first Submit action
			//if txn is in process, for remaining states, the state will be In Process							

			if (!txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_ACKED_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE")) && !txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

				if (!stateAction.equals("Under Review")) {

					resultState = "In Process";

				} else {

					resultState = "Under Review";
				}

			} else { //if it falls into any A/N/T

				if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_ACKED_STATE"))) {

					resultState = problemState;

				} else if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE"))) {

					resultState = (String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.staction");

				} else if (txnFlag.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE"))) {

					resultState = (String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.staction");

				}

			}

		} //end of cq trk id not defined

		Global.println("resultState in updated getUpdated resultAction===" + resultState);

		return resultState;
	}

	/**
		 * to print comment messg for blade comments
		 * 
		 * @param etsIssObjKey
		 * @return
		 */

	public String printBladeDescrMsg(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String bladeComMsg = "";
		StringBuffer sb = new StringBuffer();

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		bladeComMsg = (String) propMap.get("issue.blade.descr.msg");

		////print blade comment msg
		sb.append("<table summary=\"welcome\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");
		//sb.append("<tr><td >&nbsp;</td></tr>");
		sb.append("<tr>");
		sb.append("<td  height=\"18\" width=\"600\"><span style=\"color:#ff0000\">" + bladeComMsg + "</span>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("<br />");

		if (!etsIssObjKey.isProjBladeType()) {

			return "";
		} else {

			//for ext bhf
			if (comGuiUtils.isSubBhfExtUserDefnd(etsIssObjKey)) {

				return sb.toString();

			} else {//not ext bhf

				if (usrRolesModel.isBladeUsrInt()) {

					return "";

				} else {

					return sb.toString();
				}

			} //end of sub on bhf ext

		} //end of non-blade proj

	} //end of method

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 */

	public String printExtUsersList(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbsub = new StringBuffer();

		ArrayList extUserList = usr1InfoModel.getExtUserList();
		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();

		String propMsg = "";
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		sbsub.append(printSectnHeader((String) propMap.get("issue.act.new.onbehalf.step0")));

		//get prop mgs
		propMsg = (String) propMap.get("issue.act.new.onbehalf.msg0");

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		//sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		//sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
		//sbsub.append(propMsg);
		//sbsub.append("</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr valign=\"top\" >\n");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"443\">\n");
		sbsub.append("<select id=\"ff0\" name=\"subbhfextuser\"  align=\"left\" class=\"iform\" style=\"width:443px\" width=\"443px\"  >\n");
		sbsub.append("<option value=\"NONE\">Select user</option>");
		sbsub.append(fltrGuiUtils.printSelectOptionsWithValue(extUserList, prevNotifyList));
		sbsub.append("</select>\n");
		sbsub.append("</td>\n");
		sbsub.append("</tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("</table>\n");

		return sbsub.toString();

	}
	
	public String getUniqueEmailList(String emailId,String emailIds) {
		
		String[] nums = emailIds.split(",");
		String returnIds = "";
		
		for (int i = 0; i < nums.length; i++)
		{		
			  if (! emailId.equalsIgnoreCase(nums[i]))
				{
			  		returnIds +=   nums[i]+",";					
				}
		}
	
		returnIds=" to "+emailIds;

		return returnIds;

	}
	
	public String printHidSubBhfExtUser(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sb = new StringBuffer();

		String extUser = "";

		ArrayList prevExtUserList = usr1InfoModel.getPrevExtUserList();

		if (prevExtUserList != null && !prevExtUserList.isEmpty()) {

			extUser = (String) prevExtUserList.get(0);
		}

		sb.append("<input type=\"hidden\" name=\"subbhfextuser\" value=\"" + extUser + "\" />");

		return sb.toString();

	}
	public static String getIssueId(String edge_problem_id)
	{	
		String sqlQry = "select cq_trk_id from ets.problem_info_cq1 where edge_problem_id= '"+edge_problem_id+"' for READ ONLY";		

		Statement  stmt		= null;
		ResultSet  rset		= null;
		Connection conn		= null;
		String issueId	= "";

		try
		{
			conn = ETSDBUtils.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlQry);

			while (rset.next())
			{
				issueId = rset.getString("cq_trk_id");
	
			}
		}
		catch (Exception sqlEx)
		{
			sqlEx.printStackTrace();
		}
		finally
		{
			ETSDBUtils.close(rset);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);			
		}
		return issueId;
	}
	
//Added by v2sagar for Cancel button links
	public String printIssueCancel(EtsIssObjectKey etsIssObjKey,String cancName) {

		StringBuffer sbview = new StringBuffer();		
		sbview.append("<td  width=\"25\" valign=\"middle\"  align=\"left\">\n");
		sbview.append("<input type=\"image\" name=\"" + cancName + "\" src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" />\n");
		sbview.append("</td>\n");
		sbview.append("<td  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&istyp=iss&opn=10 \">Cancel</a></td>\n");
		
		return sbview.toString();
	}
	
	public String printContinueCancel(EtsIssObjectKey etsIssObjKey,String contnName,String cancName) {

		StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
        sBuffer.append("<tr>");
        sBuffer.append("<td width=\"130\" align=\"left\"><input type=\"image\" name=\"" + contnName + "\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></td>");
		sBuffer.append("<td align=\"left\">" +
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
				"<tr>" +
				"<td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\">" +
				"<input type=\"image\" name=\"" + cancName +"\" src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&&istyp=iss&opn=10 \">Cancel</a></td></tr></table></td>");
        sBuffer.append("</tr>");
        sBuffer.append("</table>");
        sBuffer.append("</td></tr>");

        sBuffer.append("</table>");


		return sBuffer.toString();
	}
	
	public String printSubmitCancelLink(EtsIssObjectKey etsIssObjKey,String subName, String cancName) {

		StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
        sBuffer.append("<tr>");
        sBuffer.append("<td width=\"130\" align=\"left\"><input type=\"image\" name=\"" + subName + "\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" /></td>");
		sBuffer.append("<td align=\"left\">" +
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
				"<tr>" +
				"<td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\">" +
				"<input type=\"image\" name=\"" + cancName +"\" src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&&istyp=iss&opn=10 \">Cancel</a></td></tr></table></td>");
        sBuffer.append("</tr>");
        sBuffer.append("</table>");
        sBuffer.append("</td></tr>");

        sBuffer.append("</table>");


		return sBuffer.toString();
	}



} //end of class
