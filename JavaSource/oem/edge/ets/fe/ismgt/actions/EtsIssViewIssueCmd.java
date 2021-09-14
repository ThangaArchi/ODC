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

package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssViewDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssViewIssueCmd extends EtsIssActionCmdAbs implements EtsIssFilterConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.37";

	/**
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsIssViewIssueCmd(EtsIssObjectKey issobjkey) {
		super(issobjkey);

	}

	/**
		 * key process request method
		 */

	public int processRequest() {

		int curstate = 0;
		int nextstate = 0;
		String notAuthMsg = "";
		EtsIssProbInfoUsr1Model usr1InfoModel = new EtsIssProbInfoUsr1Model();

		try {

			if (!getUserActionModel().isUsrViewIssue()) {
				//if (!true) {

				nextstate = ACTION_NOTAUTHORIZED;
				//notAuthMsg = (String) getIssobjkey().getPropMap().get("issues.act.view.notauth.msg");
				notAuthMsg=getViewIssueErrMsg();	
				getIssobjkey().getRequest().setAttribute("notauthmsg", notAuthMsg);
				getIssobjkey().getRequest().setAttribute("fromviewissue", "Y");

			} else {

				//get next state

				usr1InfoModel = getProbInfoUsr1Dets();

				curstate = getViewIssueSubActionState();

				if (usr1InfoModel != null) {

					nextstate = usr1InfoModel.getNextActionState();

				}

				SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest in View Issue ", "CURRENT state in processRequest in View Issue ===" + curstate + "");

				SysLog.log(SysLog.DEBUG, "NEXT state in processRequest in View Issue", "NEXT state in processRequest in View Issue ===" + nextstate + "");

				if (usr1InfoModel != null) {

					//set the bean details into request//
					getIssobjkey().getRequest().setAttribute("usr1InfoModel", usr1InfoModel);

				} else {

					curstate = FATALERROR;
					String errMsg = "Usr1Info Model is null in View issue. The session might have been idle for long time. Please try again : RC 60";
					getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);
				}

			}

			if (nextstate > 0) {

				curstate = nextstate;

			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssViewIssueCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in View issue : RC 61";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssViewIssueCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in View issue : RC 62";
			getIssobjkey().getRequest().setAttribute("actionerrmsg", errMsg);

		}

		return curstate;

	}

	/**
		 * This method will determine the state of the sub-action, then calls the suitable
		 * method of BDLG and gets the data
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */
	public EtsIssProbInfoUsr1Model getProbInfoUsr1Dets() throws SQLException, Exception {

		EtsIssProbInfoUsr1Model usr1Dets = new EtsIssProbInfoUsr1Model();

		int state = getViewIssueSubActionState();

		Global.println("current state in getProbInfoUsr1Dets in View issue()===" + state);

		EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(getIssobjkey(), state);

		switch (state) {

			case VIEWISSUEDETS :

				usr1Dets = viewDataPrep.getIssueViewDetails();

				break;

			case VIEWISSUEDETSREFRESHFILES :

				usr1Dets = viewDataPrep.getIssueViewDetailsRefreshFiles();

				break;

		}

		//		print details
		EtsIssActionGuiUtils guiUtil = new EtsIssActionGuiUtils();
		guiUtil.debugUsr1ModelDetails(usr1Dets);

		return usr1Dets;

	}

	/**
		* To get the sub state of the given actions
			 */

	public int getViewIssueSubActionState() {

		int state = 60;

		String op = (String) getIssobjkey().getParams().get("op");

		String userType = getIssobjkey().getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("60")) {

				state = VIEWISSUEDETS;

			}

			if (op.equals("61")) {

				state = VIEWISSUEDETSREFRESHFILES;

			}

		}

		String hist_sort_dtime_A = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_dtime_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_A)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_dtime_D = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_dtime_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_dtime_D)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_actionby_A = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_actionby_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_A)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_actionby_D = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_actionby_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionby_D)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_actionname_A = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_actionname_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_A)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_actionname_D = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_actionname_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_actionname_D)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_issuestate_A = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_issuestate_A.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_A)) {

			state = VIEWISSUEDETS;

		}

		String hist_sort_issuestate_D = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("hist_sort_issuestate_D.x"));

		if (AmtCommonUtils.isResourceDefined(hist_sort_issuestate_D)) {

			state = VIEWISSUEDETS;

		}

		return state;
	}
	
	/**
	 * 
	 * @return
	 */

	private String getViewIssueErrMsg() {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"general info\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("Sorry, you are not authorized to view this issue. There are several senarios that could cause this:");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("- if you cannot access the workspace at all, you should contact the workspace owner to be added as a member.");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("- if you cannot access this particular issue, contact the workspace owner to determine if the issue is restricted.");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();
	}

} //end of class
