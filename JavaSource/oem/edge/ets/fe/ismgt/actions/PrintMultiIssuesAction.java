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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssViewDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PrintMultiIssuesAction extends Action implements EtsIssFilterConstants {

	private static Log logger = EtsLogger.getLogger(PrintMultiIssuesAction.class);

	public static final String VERSION = "1.3";

	/**
	 * 
	 */
	public PrintMultiIssuesAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		ActionErrors errors = new ActionErrors();

		logger.debug("entered print action");

		EdgeAccessCntrl es = new EdgeAccessCntrl();

		//helper objects//

		EtsIssFilterObjKeyPrep etsFilterKeyObjPrep = null;
		EtsIssFilterObjectKey issFilterObjkey = new EtsIssFilterObjectKey();

		Hashtable params = new Hashtable(); //for params
		DbConnect db = null;

		

		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			if (es.GetProfile(response, request)) {

				String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn, projectidStr);

				//get params
				params = AmtCommonUtils.getServletParameters(request);

				//get link id
				String sLink = AmtCommonUtils.getTrimStr(request.getParameter("linkid"));

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, proj.getProjectId()).equals(Defines.INVALID_USER)) {

					return new ActionForward("chkUserRole");

				}

				String actiontype = AmtCommonUtils.getTrimStr((String) params.get("actionType"));

				ETSHeaderFooter headerfooter = new ETSHeaderFooter();
				headerfooter.init(request, response);

				ETSCat topCat = headerfooter.getTopCat();

				int topCatId = topCat.getId();

				//prepare iss filter key object
				//get key object
				etsFilterKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				issFilterObjkey = etsFilterKeyObjPrep.getEtsIssFilterObjKey(request, es, proj);

				EtsIssActionObjKeyPrep etsActKeyPrep = new EtsIssActionObjKeyPrep(params, proj, topCatId, es, sLink, request, response);
				EtsIssObjectKey etsIssObjKey = etsActKeyPrep.getEtsIssActionObjKey();

				//get the issue details list
				List selectedList = getSelectedIssuesList(request);
				List detsList = getIssueDetsList(selectedList, etsIssObjKey);
				request.setAttribute("detsList", detsList);

				///set amthf in request	
				EtsAmtHfBean amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
				request.setAttribute("etsamthf", amtHf);

				request.setAttribute("issfilterkey", issFilterObjkey);

				request.setAttribute("issactionobjkey", etsIssObjKey);

				int repsize = 0;
				String selectedVals = "";

				if (selectedList != null && !selectedList.isEmpty()) {

					repsize = selectedList.size();
				}

				String bkUrl = request.getParameter("bkUrl");

				logger.debug("err url===" + bkUrl);

				if (repsize > 0) {

					selectedVals=getFormValStr(selectedList);
					request.setAttribute("selectedIds", selectedVals);

					//Write logic determining how the user should be forwarded.
					forward = mapping.findForward("success");

				} else {

					if (AmtCommonUtils.isResourceDefined(bkUrl)) {

						response.sendRedirect(bkUrl);

					} else {

						//Write logic determining how the user should be forwarded.
						forward = mapping.findForward("noprintrecs");

					}

				}

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in PrintMultiIssueAction", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				logger.error("AMTException in printMultiIssuesAction", innerException);
				innerException.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in PrintMultiIssueAction", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				logger.error("SQLException in printMultiIssuesAction", se);
				se.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building PrintMultiIssueAction", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in printMultiIssuesAction", ex);
				ex.printStackTrace();

			}

			return mapping.findForward("error");

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return forward;

	}

	/*
	 * get all issue details
	 */

	public List getSelectedIssuesList(HttpServletRequest request) throws SQLException, Exception {

		List selectedList = new ArrayList();

		String selectedIds[] = request.getParameterValues("issueList");

		selectedList = AmtCommonUtils.getArrayListFromArray(selectedIds);

		return selectedList;
	}

	/*
		 * get all issue details
		 */

	public List getIssueDetsList(List selectedList, EtsIssObjectKey etsIssObjKey) throws SQLException, Exception {

		List detList = new ArrayList();
		String edgeProblemId = "";

		EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(etsIssObjKey, 60);

		int size = 0;

		if (selectedList != null && !selectedList.isEmpty()) {

			size = selectedList.size();

		}

		for (int i = 0; i < size; i++) {

			edgeProblemId = (String) selectedList.get(i);

			detList.add(viewDataPrep.getIssueViewDetailsWithId(edgeProblemId));
		}

		return detList;
	}

	private String getFormValStr(List arryList) {

		StringBuffer sb = new StringBuffer();

		if (arryList != null && !arryList.isEmpty() && arryList.size() > 0) {
			for (int i = 0; i < arryList.size(); i++) {
				if (i == 0) {
					sb.append((String) arryList.get(i));
				} else {
					sb.append(",");
					sb.append((String) arryList.get(i));

				}

			}
		} else {
			sb.append("");
		}

		return sb.toString();

	}

} //end of class
