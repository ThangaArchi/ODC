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

import oem.edge.amt.AmtCommonUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssCreateIssTypeGuiUtils {

	public static final String VERSION = "1.35";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsIssCreateIssTypeGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
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

			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"50\" name=\"issuetypename\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"\" />\n");

		} else {

			sbview.append("<input id=\"tl\" align=\"left\" class=\"iform\" maxlength=\"50\" name=\"issuetypename\" size=\"35\" src=\"\" type=\"text\" style=\"width:323px\" width=\"323px\" value=\"" + prevIssueTypeName + "\" />\n");

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

		sbview.append("<select id=\"sev\" name=\"issueowner\" size=\"\" align=\"left\" class=\"iform\" style=\"width:323px\" width=\"323px\">\n");
		sbview.append("<option value=\"NONE\">Select issue owner</option>");
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

		if (prevIssueAccess.equals("Internal")) {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"Internal\" checked=\"checked\" id=\"sev\" />Internal &nbsp;");
		} else {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"Internal\" id=\"sev\" />Internal &nbsp;");

		}

		if (prevIssueAccess.equals("External")) {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"External\" checked=\"checked\" id=\"sev\"  />External");

		} else {

			sbview.append("<input type=\"radio\" name=\"issueaccess\" value=\"External\"  id=\"sev\"  />External");
		}

		return sbview.toString();
	}

} //end of class
