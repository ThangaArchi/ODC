package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUserSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
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
/**
 * @author v2phani
 *
 * This class will process command of clicking srch button on filter cnds,when user clicks Work with all existing issues
 */
public class ShowFilterCondsWrkAllGoCmd extends FilterCommandAbsBean implements EtsIssFilterConstants {

	/**
	 * Constructor for ShowFilterCondsWrkAllGo.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ShowFilterCondsWrkAllGoCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}

	/**
	 * key process request method
	 */

	public int processRequest() {

		int processreq = 0;
		int repsize = 0;
		int errsize = 0;
		ArrayList issueRepTabList = new ArrayList();

		try {

			Global.println("entering work all go==");

			//			print flag
			String printRep = AmtCommonUtils.getTrimStr(getRequest().getParameter("prnt"));

			if (!printRep.equals("Y")) {

				ArrayList errMsgList = getFilterBldgBean().getFilterDetails().getErrMsgList();

				if (errMsgList != null) {

					errsize = errMsgList.size();

				}

				Global.println("errsize @@@@@" + errsize + "");

				//only when there are no error msgs///

				if (errsize == 0) {

					issueRepTabList = getFilterBldgBean().getFilterDetails().getIssueReportTabList();

					if (issueRepTabList != null) {

						repsize = issueRepTabList.size();

					}

				} //no err msgs

				//set the bean details into request, even if errors, still we want to go back and load fc page//

				getRequest().setAttribute("etsfilterdets", getFilterBldgBean().getFilterDetails());
				getRequest().setAttribute("issueRepTabList", issueRepTabList);
				getRequest().setAttribute("repsize", new Integer(repsize));

				//process Request success

				processreq = 1;

				//errors show err msg

				if (errsize == 0) {

					if (repsize == 0) { //if no recs, show no rec msg

						processreq = 3;

					}

				} else {

					processreq = ETSISSRPTWALLFC;

				}

			} else { //to print

				Global.println("entering work all go for print==");

				EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(getRequest(), getIssobjkey());
				ArrayList printRepTabList = filterSessnParams.getIssReportTabListFromSessn();
							

				if (EtsIssFilterUtils.isArrayListDefndWithObj(printRepTabList)) {

					repsize = printRepTabList.size();
					Global.println("entering work all go print size==" + repsize);

				}
				getRequest().setAttribute("printIssueRepTabList", printRepTabList);

				//	process Request success

				processreq = 1;

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ReportAllWorkingIssuesCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ReportAllWorkingIssuesCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return processreq;

	}

}
