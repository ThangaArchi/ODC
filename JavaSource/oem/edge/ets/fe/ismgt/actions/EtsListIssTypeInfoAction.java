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
import java.util.ArrayList;
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
import oem.edge.ets.fe.ismgt.bdlg.EtsListIssTypesInfoBdlg;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUserSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
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
public class EtsListIssTypeInfoAction extends Action implements EtsIssFilterConstants, EtsIssueActionConstants {

	private static Log logger = EtsLogger.getLogger(EtsListIssTypeInfoAction.class);

	/**
	 * 
	 */
	public EtsListIssTypeInfoAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();

		EdgeAccessCntrl es = new EdgeAccessCntrl();

		//helper objects//

		EtsIssFilterObjKeyPrep etsKeyObjPrep = null;

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

				//				check for deleted projects
				if (proj.getProject_status().equalsIgnoreCase("D")) {

					return new ActionForward("chkUserRole");

				}

				//prepare iss filter key object
				//get key object
				etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				EtsIssFilterObjectKey issFilterObjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request, es, proj);

				///CORE FUNCTION
				int processrequest = processRequest(issFilterObjkey, request, response, params);

				if (logger.isDebugEnabled()) {

					logger.debug("PROCESS REQUEST===" + processrequest);
				}

				String processRequest = getForwardMap(processrequest);

				///set amthf in session	
				EtsAmtHfBean amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
				request.setAttribute("etsamthf", amtHf);

				request.setAttribute("issfilterkey", issFilterObjkey);

				forward = mapping.findForward(processRequest);

				if (logger.isDebugEnabled()) {

					logger.debug("FINAL FORWARD IN SRCH BY NUM ACTION===" + forward);
				}

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in EtsIssSrchByNumAction", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSrchByNumAction", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsIssSrchByNumAction", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
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
				 * To get the sub state of the given actions
				 */

	public int getListIssTypesSubActionState(Hashtable params) {

		int state = 1500;

		String opn = AmtCommonUtils.getTrimStr((String) params.get("opn"));

		if (AmtCommonUtils.isResourceDefined(opn)) {

			state = 1500;

		}

		return state;

	}

	/**
			 * This method will determine the state of the sub-action, then calls the suitable
			 * method of BDLG and gets the data
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */
	public ArrayList getIssTypeInfoList(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issFilterObjkey, Hashtable params) throws SQLException, Exception {

		String projectId = AmtCommonUtils.getTrimStr((String) params.get("proj"));

		EtsListIssTypesInfoBdlg issTypeBdlg = new EtsListIssTypesInfoBdlg(request, response, issFilterObjkey);

		ArrayList issTypeList = issTypeBdlg.getIssueTypeOwnerInfoList();

		return issTypeList;

	}

	/**
		 * key process request method
		 */

	public int processRequest(EtsIssFilterObjectKey issFilterObjkey, HttpServletRequest request, HttpServletResponse response, Hashtable params) {

		int curstate = 0;
		int nextstate = 0;

		String projId = "";
		String tcId = "";
		String edgeProblemId = "";
		String linkId = "";
		String prnt = "";
		int listsize = 0;

		try {

			//ArrayList issTypeList = getIssTypeInfoList(request, response, issFilterObjkey, params);
			prnt = AmtCommonUtils.getTrimStr((String) params.get("prnt"));

			if (!prnt.equals("Y")) {
				
				ArrayList issTypeList = getIssTypeInfoList(request, response, issFilterObjkey, params);

				request.setAttribute("issTypeInfoList", issTypeList);

				if (EtsIssFilterUtils.isArrayListDefndWithObj(issTypeList)) {

					listsize = issTypeList.size();

				}

				if (listsize == 0) {

					curstate = 0;
				}

				if (listsize > 0) {

					curstate = 1;

				}

			} // not equals to print option

			else {

				
				EtsIssFilterUserSessnParams filterSessnParams = new EtsIssFilterUserSessnParams(request, issFilterObjkey);
				ArrayList printIssTypeInfoList = filterSessnParams.getIssTypInfoTabListFromSessn();

				request.setAttribute("printIssTypeInfoList", printIssTypeInfoList);

				curstate = 2;
			}

			if (logger.isDebugEnabled()) {

				logger.debug("CUR STATE in LIST INFO ACTION===" + curstate);

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssSrchByNumAction", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Submit issue : RC 8011";
			request.setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsIssSrchByNumAction", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Search By  Issue ID : RC 8012";
			request.setAttribute("actionerrmsg", errMsg);

		}

		return curstate;

	}

	String getForwardMap(int processrequest) {

		String forward = "";

		if (logger.isDebugEnabled()) {

			logger.debug("process request in forward map===" + processrequest);
		}

		switch (processrequest) {

			case 0 :

				forward = "listNoRecs";

				break;

			case 1 :

				forward = "listIssTypes";

				break;

			case 2 :

				forward = "printListIssTypes";

				break;

			case FATALERROR :

			case ERRINACTION :

				break;

		}

		if (logger.isDebugEnabled()) {

			logger.debug("ffff forward===" + forward);
		}

		return forward;
	}

}
