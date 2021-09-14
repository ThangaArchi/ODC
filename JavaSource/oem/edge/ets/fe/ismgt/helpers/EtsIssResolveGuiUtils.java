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

import java.util.HashMap;

import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssResolveGuiUtils {

	public static final String VERSION = "1.33";

	/**
	 * 
	 */
	public EtsIssResolveGuiUtils() {
		super();
		// 
	}

	/**
			 * 
			 * 
			 * @return
			 */

	public String printAnchorLinksForResolve(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbview = new StringBuffer();

		int actionKey = etsIssObjKey.getActionkey();

		String checkUserRole = ETSUtils.checkUserRole(etsIssObjKey.getEs(), etsIssObjKey.getProj().getProjectId());

		sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
		sbview.append("<tr>");
		sbview.append("<td>");
		sbview.append("<a href=\"#0\">Description</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		if (actionKey == 3 || actionKey == 5) {
			sbview.append("<a href=\"#2\">Attachments</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		}
		if (actionKey == 3) {

			sbview.append("<a href=\"#5\">Resolution</a>");

		} else {

			sbview.append("<a href=\"#5\">Comments</a>");

		}
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();
	}

	public String printTitleandHeaderMsg(EtsIssObjectKey etsIssObjKey) {

		String scrn1Msg = "";
		String headerMsg = "";

		//		comments label
		String comLabel = "Comments";

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();

		if (actionKey == 3) {

			headerMsg = (String) propMap.get("issue.act.resolve.title");
			scrn1Msg = (String) propMap.get("issue.act.resolve.title.msg");

		}

		if (actionKey == 5) {

			headerMsg = (String) propMap.get("issue.act.reject.title");
			scrn1Msg = (String) propMap.get("issue.act.reject.title.msg");

		}

		if (actionKey == 6) {

			headerMsg = (String) propMap.get("issue.act.close.title");
			scrn1Msg = (String) propMap.get("issue.act.close.title.msg");

		}

		if (actionKey == 19) {

			headerMsg = (String) propMap.get("issue.act.comment.title");
			scrn1Msg = (String) propMap.get("issue.act.comment.title.msg");

		}

		if (actionKey == 23) {

			headerMsg = (String) propMap.get("issue.act.withdraw.title");
			scrn1Msg = (String) propMap.get("issue.act.withdraw.title.msg");

		}

		EtsIssActionGuiUtils actguiutil = new EtsIssActionGuiUtils();

		String titleMsg = actguiutil.printSubTitleProcMsg(headerMsg, scrn1Msg);

		return titleMsg;

	}

	public String printCommLabel(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String comLabel = "Comments";

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();

		if (actionKey == 3) {

			comLabel = (String) propMap.get("issue.act.resolve.comnt.label");

		}

		if (actionKey == 5) {

			comLabel = (String) propMap.get("issue.act.reject.comnt.label");
		}

		if (actionKey == 6) {

			comLabel = (String) propMap.get("issue.act.close.comnt.label");

		}

		if (actionKey == 19) {

			comLabel = (String) propMap.get("issue.act.comment.comnt.label");
		}

		if (actionKey == 23) {

			comLabel = (String) propMap.get("issue.act.withdraw.comnt.label");

		}

		return comLabel;

	}

	public String printCommLabelMsg(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String comntMsg = "Please provide comments below.";

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();

		if (actionKey == 3) {

			comntMsg = (String) propMap.get("issue.act.resolve.comnt.msg");

		}

		if (actionKey == 5) {

			comntMsg = (String) propMap.get("issue.act.reject.comnt.msg");
		}

		if (actionKey == 6) {

			comntMsg = (String) propMap.get("issue.act.close.comnt.msg");

		}

		if (actionKey == 19) {

			comntMsg = (String) propMap.get("issue.act.comment.comnt.msg");
		}

		if (actionKey == 23) {

			comntMsg = (String) propMap.get("issue.act.withdraw.comnt.msg");

		}

		return comntMsg;

	}

	public String printCommntsSectHeader(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String comntHeader = "Resolution (mandatory)";

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();

		if (actionKey == 3) {

			comntHeader = (String) propMap.get("issue.act.resolve.sect.header");

		} else {

			comntHeader = (String) propMap.get("issue.act.comment.sect.header");

		}
		return comntHeader;

	}

	/**
	 * to print comment messg for blade comments
	 * 
	 * @param etsIssObjKey
	 * @return
	 */

	public String printBladeCommentMsg(EtsIssObjectKey etsIssObjKey) {

		//comments label
		String bladeComMsg = "";
		StringBuffer sb = new StringBuffer();

		int actionKey = etsIssObjKey.getActionkey();
		HashMap propMap = etsIssObjKey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		bladeComMsg = (String) propMap.get("issue.blade.comm.msg");

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

			if (usrRolesModel.isBladeUsrInt()) {

				return "";
				
			} else {

				return sb.toString();
			}

		}

	}

} //end of class
