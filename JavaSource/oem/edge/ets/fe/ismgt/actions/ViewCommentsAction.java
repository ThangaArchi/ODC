/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssViewDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.MessgFormatUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ViewCommentsAction extends Action implements EtsIssFilterConstants, EtsIssueActionConstants {

	private static Log logger = EtsLogger.getLogger(ViewCommentsAction.class);

	/**
	 * 
	 */
	public ViewCommentsAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();

		EdgeAccessCntrl es = new EdgeAccessCntrl();

		//helper objects//

		EtsIssActionObjKeyPrep etsKeyObjPrep = null;
		EtsIssFilterObjKeyPrep etsFilterKeyPrep = null;

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

				//get params
				params = AmtCommonUtils.getServletParameters(request);

				String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

				//get link id
				String sLink = AmtCommonUtils.getTrimStr(request.getParameter("linkid"));

				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn, projectidStr);

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, projectidStr).equals(Defines.INVALID_USER)) {

					return new ActionForward("chkUserRole");

				}

				//check for deleted projects
				if (proj.getProject_status().equalsIgnoreCase("D")) {

					return new ActionForward("chkUserRole");

				}

				int TopCatId = 0;

				String strCatId = AmtCommonUtils.getTrimStr(request.getParameter("tc"));

				if (AmtCommonUtils.isResourceDefined(strCatId)) {

					TopCatId = Integer.parseInt(strCatId);
				}

				//				prepare iss filter key object
				//get key object
				etsFilterKeyPrep = new EtsIssFilterObjKeyPrep(request, es);
				EtsIssFilterObjectKey issFilterObjkey = etsFilterKeyPrep.getEtsIssFilterObjKey(request, es, proj);

				///set amthf in session	
				EtsAmtHfBean amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
				request.setAttribute("etsamthf", amtHf);

				//prepare iss filter key object
				//get key object
				etsKeyObjPrep = new EtsIssActionObjKeyPrep(params, proj, TopCatId, es, sLink, request, response);
				EtsIssObjectKey issObjkey = etsKeyObjPrep.getEtsIssActionObjKey();

				///CORE FUNCTION
				int processrequest = processRequest(request, response, params, issObjkey);

				if (logger.isDebugEnabled()) {

					logger.debug("PROCESS REQUEST===" + processrequest);
				}

				String processRequest = getForwardMap(processrequest);

				request.setAttribute("issfilterkey", issFilterObjkey);

				forward = mapping.findForward(processRequest);


				if (logger.isDebugEnabled()) {

					logger.debug("FINAL FORWARD VIEW COMMENTS ACTION===" + forward);
				}

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in ViewCommentsAction", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				logger.error("AMT Exception in ViewIssueAction",amtException);
				innerException.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ViewCommentsAction", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				logger.error("SQLException in ViewIssueAction",se);
				se.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building ViewCommentsAction", ETSLSTUSR);

			if (ex != null) {
				logger.error("Exception in ViewIssueAction",ex);
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

	/**
		 * key process request method
		 */

	public int processRequest(HttpServletRequest request, HttpServletResponse response, Hashtable params, EtsIssObjectKey issObjkey) {

		int curstate = 0;
		int processreq = 0; //process failure

		try {

			String edgeProblemId = AmtCommonUtils.getTrimStr(request.getParameter("edge_problem_id"));

			EtsIssViewDataPrep viewDataPrep = new EtsIssViewDataPrep(issObjkey, curstate);
			
			String commLog=AmtCommonUtils.getTrimStr(viewDataPrep.getIssueViewDetails().getCommentLogStr());
			
			String formatComLogStr = "";
			
			try {
				
			
				formatComLogStr = formatMsgString(commLog);
			
			}
			
			catch(Exception e) {
				
				e.printStackTrace();
				
				formatComLogStr=commLog;
			}

			request.setAttribute("issuelogdets", formatComLogStr);

			processreq = 1; //process success
		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ViewCommentsAction", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 10011";
			request.setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ViewCommentsAction", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Search By  Issue ID : RC 10012";
			request.setAttribute("actionerrmsg", errMsg);

		}

		curstate = processreq;

		return curstate;

	}

	String getForwardMap(int processrequest) {

		String forward = "";

		if (logger.isDebugEnabled()) {

			logger.debug("process request in forward map===" + processrequest);
		}

		switch (processrequest) {

			case 0 :

			case FATALERROR :

			case ERRINACTION :

				forward = "errorInCommLog";

				break;

			case 1 :

				forward = "showIssueLog";

				break;

		}

		if (logger.isDebugEnabled()) {

			logger.debug("ffff forward===" + forward);
		}

		return forward;
	}

	/**
	 * 
	 * @param oldStr
	 * @return
	 */

	public String formatMsgString(String oldStr) throws Exception{

	
		return MessgFormatUtils.getFormatComments(oldStr);

	}

	
	
} //end of class
