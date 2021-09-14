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

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrSubmitNewGuiUtils implements EtsCrActionConstants {

	public static final String VERSION = "1.18.1.12";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsCrSubmitNewGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
	}

	/**
			 * To print continue and cancel buttons
			 * @param contnName
			 * @param cancName
			 * @return
			 */

	public String printContCancel(EtsCrProbInfoModel crInfoModel) {

		StringBuffer sbview = new StringBuffer();

		String contnName = "";
		String cancName = "";

		//			states
		int actionstate = crInfoModel.getCurrentActionState();
		int usr1nextstate = crInfoModel.getNextActionState();

		if (usr1nextstate > 0) {

			actionstate = usr1nextstate;
		}

		Global.println("actionstate in print cont button ==" + actionstate);

		switch (actionstate) {

			case NEWINITIAL :
			case VALIDERRCONTDESCR :
			case EDITDESCR :

				contnName = "op_11";
				cancName = "op_12";

				break;

			case CONTDESCR :
			case FILEATTACH :
			case DELETEFILE :
			case EDITFILEATTACH :

				contnName = "op_20";
				cancName = "op_123";

				break;

			case ADDFILEATTACH :

				contnName = "op_26";
				cancName = "op_126";

				break;

		}

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
		 * This method will print submitter details
		 * 
		 */

	public String printSubmitterDetails(EtsCrProbInfoModel crInfoModel) {

		EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();

		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		usr1InfoModel.setCustName(crInfoModel.getCustName());
		usr1InfoModel.setCustEmail(crInfoModel.getCustEmail());
		usr1InfoModel.setCustPhone(crInfoModel.getCustPhone());
		usr1InfoModel.setCustCompany(crInfoModel.getCustCompany());

		return actGuiUtils.printSubmitterDetails(usr1InfoModel);

	}

	/**
			 * 
			 */
	public String printHidCancelActionState(EtsCrProbInfoModel crInfoModel) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"cancelstate\" value=\"" + crInfoModel.getCancelActionState() + "\" />\n");

		return sbsub.toString();
	}

	/***
				 * 
				 * display Edit Issue Btn Comments  tag
				 */

	public String printStepHierchy(String step) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"243\"> ");
		sbview.append("<tr><td  colspan=\"3\"> &nbsp;</td></tr>");
		sbview.append("<tr>");
		sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");

		if (step.equals("1")) {

			sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#0\">Description</a></b></span></span>");

		} else {

			sbview.append("1.&nbsp;Description");
		}

		sbview.append("</td>");

		sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");

		sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

		if (step.equals("2")) {

			sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Attachments</a></b></span>");
		} else {
			sbview.append("2.&nbsp;Attachments");
		}

		sbview.append("</td>");

		sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
		sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

		if (step.equals("3")) {

			sbview.append("3.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#2\">Submission</a></b></span>");
		} else {
			sbview.append("3.&nbsp;Submission");
		}

		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/**
		 * To print the model details for debug
		 */

	public void debugCrInfoModelDetails(EtsCrProbInfoModel crInfoModel) {

		if (crInfoModel != null) {

			int prevstate = crInfoModel.getPreviousActionState();
			int curstate = crInfoModel.getCurrentActionState();
			int nextstate = crInfoModel.getNextActionState();
			int cancelstate = crInfoModel.getCancelActionState();

			Global.println("Current state===" + curstate);
			Global.println("Next state===" + nextstate);
			Global.println("Cancel state===" + cancelstate);

			//			usr info
			String errMsg = AmtCommonUtils.getTrimStr(crInfoModel.getErrMsg());
			Global.println("errMsg===" + errMsg);

			String etsId = AmtCommonUtils.getTrimStr(crInfoModel.getEtsId());
			String pmoId = AmtCommonUtils.getTrimStr(crInfoModel.getPmoId());
			String pmoProjectId = AmtCommonUtils.getTrimStr(crInfoModel.getPmoProjectId());
			String parentPmoId = AmtCommonUtils.getTrimStr(crInfoModel.getParentPmoId());
			int refNo = crInfoModel.getRefNo();

			Global.println("etsId===" + etsId);
			Global.println("pmoId===" + pmoId);
			Global.println("pmoProjectId===" + pmoProjectId);
			Global.println("parentPmoId===" + parentPmoId);
			Global.println("refNo===" + refNo);
			Global.println("stateAction===" + crInfoModel.getStateAction());
			Global.println("statusFlag===" + crInfoModel.getStatusFlag());

			//usr info
			String curIssueCustName = AmtCommonUtils.getTrimStr(crInfoModel.getCustName());
			String curIssueCustEmail = AmtCommonUtils.getTrimStr(crInfoModel.getCustEmail());
			String curIssueCustPhone = AmtCommonUtils.getTrimStr(crInfoModel.getCustPhone());
			String curIssueCustCompany = AmtCommonUtils.getTrimStr(crInfoModel.getCustCompany());

			Global.println("Printing customer profile @@@@@ for CR ");
			Global.println("Cust Name====" + curIssueCustName);
			Global.println("Cust Email====" + curIssueCustEmail);
			Global.println("Cust Phone====" + curIssueCustPhone);
			Global.println("Cust Company====" + curIssueCustCompany);

			//issue descr info
			String severity = "";
			ArrayList prevSevList = crInfoModel.getPrevProbSevList();

			if (prevSevList != null && !prevSevList.isEmpty()) {

				severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

			}

			String probTitle = AmtCommonUtils.getTrimStr(crInfoModel.getProbTitle());
			String probDesc = AmtCommonUtils.getTrimStr(crInfoModel.getProbDesc());

			Global.println("Printing issue descr profile @@@@@ for CR");
			Global.println("severity====" + severity);
			Global.println("probTitle====" + probTitle);
			Global.println("probDesc====" + probDesc);

			String comments = AmtCommonUtils.getTrimStr(crInfoModel.getCommFromCust());
			Global.println("Printing comments @@@@@ for CR");
			Global.println("comments====" + comments);

		} else {

			Global.println("CR model is null @@@@");

		}

	}

	/**
			 * This method will print CR description details
			 * 
			 */

	public String printCrDescripDetails(EtsCrProbInfoModel crInfoModel) {

		StringBuffer sb = new StringBuffer();
		String severity = "";

		ArrayList prevSevList = crInfoModel.getPrevProbSevList();

		if (prevSevList != null && !prevSevList.isEmpty()) {

			severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

		}

		String probTitle = AmtCommonUtils.getTrimStr(crInfoModel.getProbTitle());
		String probDesc = comGuiUtils.formatDescStr(AmtCommonUtils.getTrimStr(crInfoModel.getProbDesc()));

		sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Priority</b>:</td>\n");
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

		return sb.toString();
	}

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param edgeProblemId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public String printFileAttachPageForCr(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.printFileAttachPageForCr(etsIssObjKey, crInfoModel, actionType);

	}

	/**
			 * 
			 * 
			 * @param etsIssObjKey
			 * @param edgeProblemId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String printCRAttachedFilesList(String etsId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.printCRAttachedFilesList(etsId);

	}

	public String getIndepFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.getIndepFileAttachMsgForCR(etsIssObjKey, crInfoModel, actionType);

	}

	/**
			 * To print Severrity types in actions
			 */

	public String printCrSeverity(ArrayList severityList, ArrayList prevSeverityList) {

		StringBuffer sbview = new StringBuffer();

		//			remove ALL value//
		if (severityList != null && !severityList.isEmpty() && severityList.contains("All")) {

			severityList.remove("All");
			severityList.remove("All");

		}

		sbview.append("<select id=\"sev\" name=\"severity\" size=\"\" align=\"left\" class=\"iform\" style=\"width:200px\" width=\"200px\">\n");
		sbview.append("<option value=\"NONE\">Select priority level</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(severityList, prevSeverityList));
		sbview.append("</select>\n");
		return sbview.toString();

	}

} //end of class
