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

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsChgOwnerGuiUtils {

	public static final String VERSION = "1.22";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsChgOwnerGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();

	}

	/***
	  * 
	  * display step hierchy
	  */

	public String printStepHierchy(String step) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"243\"> ");
		sbview.append("<tr><td  colspan=\"3\"> &nbsp;</td></tr>");
		sbview.append("<tr>");
		sbview.append("<td  align=\"left\" nowrap=\"nowrap\">");

		if (step.equals("1")) {

			sbview.append("1.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#0\">Change owner</a></b></span></span>");

		} else {

			sbview.append("1.&nbsp;Change owner");
		}

		sbview.append("</td>");

		sbview.append("<td width=\"16\"><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");

		sbview.append("<td   align=\"left\" nowrap=\"nowrap\">");

		if (step.equals("2")) {

			sbview.append("2.&nbsp;<span style=\"color:#ff0000\"><b><a href=\"#1\">Submission</a></b></span>");
		} else {
			sbview.append("2.&nbsp;Submission");
		}

		sbview.append("</td>");

		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();

	}

	/**
			 * This method will print submitter details
			 * 
			 */

	public String printOwnerDetails(String actionerName, EtsIssOwnerInfo ownerInfo) {

		StringBuffer sb = new StringBuffer();

		String curIssueCustName = AmtCommonUtils.getTrimStr(ownerInfo.getUserFullName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(ownerInfo.getUserEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(ownerInfo.getUserContPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(ownerInfo.getUserCustCompany());

		EtsIssProjectMember projMem = new EtsIssProjectMember();
		projMem.setUserFullName(curIssueCustName);
		projMem.setUserEmail(curIssueCustEmail);
		projMem.setUserContPhone(curIssueCustPhone);
		projMem.setUserCustCompany(curIssueCustCompany);

		return comGuiUtils.printActionerDetails(actionerName, projMem);

	}

	/**
				 * This method will print submitter details
				 * 
				 */

	public String printSubmitterDetails(String actionerName, EtsIssProjectMember projMem) {

		return comGuiUtils.printActionerDetails(actionerName, projMem);

	}

	/**
				 * To print Severrity types in actions
				 */

	public String printOwnerList(ArrayList ownerList, ArrayList prevOwnerList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"sev\" name=\"issueowner\" size=\"\" align=\"left\" class=\"iform\" style=\"width:323px\" width=\"323px\">\n");
		sbview.append("<option value=\"NONE\">Select issue owner</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(ownerList, prevOwnerList));
		sbview.append("</select>\n");
		return sbview.toString();

	}
	
	public String printSubmitRetToView(String subName, EtsIssObjectKey etsIssObjKey, String edgeProblemId) {
		
		return comGuiUtils.printSubmitRetToView(subName, etsIssObjKey, edgeProblemId);
	}
	
	

} //end of class
