package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
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
 * This command bean executes the required process/beans to generate the report
 * of Issues assigned to me
 */

public class ReportAsgnMeIssuesCmd extends FilterCommandAbsBean implements EtsIssFilterConstants {

	public static final String VERSION = "1.10";

	/**
	 * Constructor for ReportAllAsgnMeIssuesCmd.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ReportAsgnMeIssuesCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}

	/**
	 * key process request method
	 */

	public int processRequest() {

		int processreq = 0;
		int repsize = 0;

		try {

			//	sprint flag
			String printRep = AmtCommonUtils.getTrimStr(getRequest().getParameter("prnt"));

			if (!printRep.equals("Y")) {

				ArrayList issueRepTabList = getFilterBldgBean().getFilterDetails().getIssueReportTabList();

				if (issueRepTabList != null) {

					repsize = issueRepTabList.size();

				}

				//set the bean details into request//
				getRequest().setAttribute("etsfilterdets", getFilterBldgBean().getFilterDetails());
				getRequest().setAttribute("issueRepTabList", issueRepTabList);
				getRequest().setAttribute("repsize", new Integer(repsize));

				//process Request success

				processreq = 1;

				//if no recs, show no rec msg
				if (repsize == 0) {

					processreq = 3;

				}

			} else {//print

				EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(getRequest(), getIssobjkey());
				ArrayList printRepTabList = filterSessnParams.getIssReportTabListFromSessn();

				if (EtsIssFilterUtils.isArrayListDefndWithObj(printRepTabList)) {

					repsize = printRepTabList.size();

				}
				getRequest().setAttribute("printIssueRepTabList", printRepTabList);

				//	process Request success

				processreq = 1;

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ReportAllAsgnMeIssuesCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ReportAllAsgnMeIssuesCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return processreq;

	}

}
