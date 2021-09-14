/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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
import javax.servlet.http.HttpSession;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssRemoveCustFieldBdlg;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUpdateCustFieldBdlg;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssCommonSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * @author DHARANENDRA PRASAD
 *
 */
public class EtsUpdateCustFieldAction extends Action implements EtsIssFilterConstants, EtsIssueActionConstants {

	public EtsUpdateCustFieldAction() {
		super();
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
				String opp = AmtCommonUtils.getTrimStr(request.getParameter("op"));

				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn, projectidStr);

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, proj.getProjectId()).equals(Defines.INVALID_USER)) {
					return new ActionForward("chkUserRole");
				}

				//				check for deleted projects
				if (proj.getProject_status().equalsIgnoreCase("D")) {

					return new ActionForward("chkUserRole");

				}

				String actiontype = AmtCommonUtils.getTrimStr((String) params.get("actionType"));

				ETSHeaderFooter headerfooter = new ETSHeaderFooter();
				headerfooter.init(request, response);

				ETSCat topCat = headerfooter.getTopCat();

				int topCatId = topCat.getId();

				EtsIssActionObjKeyPrep etsActKeyPrep = new EtsIssActionObjKeyPrep(params, proj, topCatId, es, sLink, request, response);
				EtsIssObjectKey etsIssObjKey = etsActKeyPrep.getEtsIssActionObjKey();

				//get session
				HttpSession session = request.getSession(true);
				request.setAttribute("issactionobjkey", etsIssObjKey);

				//		helper objects//
				EtsIssCommonSessnParams etsCommonParams = null;
				EtsAmtHfBean amtHf = null;

				//prepare iss filter key object
				//get key object
				etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				EtsIssFilterObjectKey issFilterObjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request, es, proj);

				//		initialize the common params bean//
				etsCommonParams = new EtsIssCommonSessnParams(session, proj.getProjectId());
				ETSProj etsProj = etsCommonParams.getEtsProj();
				EtsPrimaryContactInfo etsContInfo = etsCommonParams.getEtsContInfo();

				///set amthf in session	
				amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
				request.setAttribute("etsamthf", amtHf);

				request.setAttribute("issfilterkey", issFilterObjkey);

				///
				int processrequest = processRequest(etsIssObjKey);

				Global.println("PROCESS REQUEST===" + processrequest);

				String processRequest = getForwardMap(processrequest);

				forward = mapping.findForward(processRequest);

				Global.println("FINAL FORWARD IN ADD ISSUE TYPE ACTION===" + forward);

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in EtsIssFilterCntrlServlet", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssFilterCntrlServlet", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsIssFilterCntrlServlet", ETSLSTUSR);

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
	public int getUpdateCustFieldSubActionState(EtsIssObjectKey etsIssObjKey) {

		int state = 2430;

		String op = (String) etsIssObjKey.getParams().get("op");
		String userType = etsIssObjKey.getEs().gDECAFTYPE;

		if (AmtCommonUtils.isResourceDefined(op)) {

			if (op.equals("2430")) {

				state = UPDATECUSTFIELD1STPAGE;
				
			} 

		}
		

		String op_2431 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("op_2431.x"));

		if (AmtCommonUtils.isResourceDefined(op_2431)) {

			state = UPDATECUSTFIELDCONTINUE;

		}

		String op_2432 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("op_2432.x"));

		if (AmtCommonUtils.isResourceDefined(op_2432)) {

			state = UPDATECUSTFIELDCANCEL;

		}
		

		String op_2433 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("op_2433.x"));

		if (AmtCommonUtils.isResourceDefined(op_2433)) {

			state = UPDATECUSTFIELDEXTCONTINUE;

		}

		String op_2434 = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("op_2434.x"));

		if (AmtCommonUtils.isResourceDefined(op_2434)) {

			state = UPDATECUSTFIELDSUBMIT;

		}

		return state;

	}

	/**
		 * key process request method
		 */

	public int processRequest(EtsIssObjectKey etsIssObjKey) {

		int curstate = 0;
		int nextstate = 0;

		try {

		EtsCustFieldInfoModel custFieldModel = getCustFieldInfoDets(etsIssObjKey);

			curstate = getUpdateCustFieldSubActionState(etsIssObjKey);
			//get next state

			if (custFieldModel != null) {					
				nextstate = custFieldModel.getNextActionState();
			}
			String opp = "";
			int op = 0;
			if(etsIssObjKey.getRequest().getParameter("op") != null) {
				opp = etsIssObjKey.getRequest().getParameter("op").toString();
				op  =	Integer.parseInt(opp);
			}
			
			//			create not for visitir or executive

			if (EtsIssFilterUtils.isUserIssViewOnly(etsIssObjKey.getEs(), etsIssObjKey.getProj().getProjectId())) {

				nextstate = FATALERROR;
				String errMsg = (String) etsIssObjKey.getPropMap().get("issues.invalid.user.msg");
				etsIssObjKey.getRequest().setAttribute("actionerrmsg", errMsg);
			}

			SysLog.log(SysLog.DEBUG, "CURRENT state in processRequest", "CURRENT state in processRequest ===" + curstate + "");

			SysLog.log(SysLog.DEBUG, "NEXT state in processRequest", "NEXT state in processRequest ===" + nextstate + "");

			if (nextstate > 0) {
				curstate = nextstate;				
			}
			
			if(op  >  0 )
				curstate =	op;
			
			
			if (custFieldModel != null) {

				//set the bean details into request//
			    custFieldModel.setNextActionState(curstate);
				etsIssObjKey.getRequest().setAttribute("custFieldModel", custFieldModel);

			} else {

				curstate = FATALERROR;
				String errMsg = "Usr1Info Model is null to request create new issue type. The session might have been idle for long time. Please try again : RC 710";
				etsIssObjKey.getRequest().setAttribute("actionerrmsg", errMsg);
			}
			

			if (curstate == ERRINACTION) {

				String errMsg = (String) etsIssObjKey.getPropMap().get("issue.act.std.err.msg");

				etsIssObjKey.getRequest().setAttribute("actionerrmsg", errMsg);

			}

			//process Request success

			//processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsUpdateCustFieldAction.processRequest()", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "SQL Exception in Update Custom Field ";
			etsIssObjKey.getRequest().setAttribute("actionerrmsg", errMsg);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsUpdateCustFieldAction.processRequest()", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

			curstate = ERRINACTION;
			String errMsg = "Exception in Update Custom Field ";
			etsIssObjKey.getRequest().setAttribute("actionerrmsg", errMsg);

		}		
		return curstate;
	}

	String getForwardMap(int processrequest) {

		String forward = "";

		Global.println("process request in forward map===" + processrequest);

		switch (processrequest) {

			case UPDATECUSTFIELD1STPAGE :

				forward = "updateCustField1stPage";

				break;

			case UPDATECUSTFIELDCONTINUE :

				forward = "updateCustField2ndPage";

				break;

			case UPDATECUSTFIELDBACK :

				forward = "updateCustField1stPage";

				break;				
												
			case UPDATECUSTFIELDCANCEL :

				forward = "updateCustField1stPage";

				break;
				
			case UPDATECUSTFIELDSUBMIT :

				forward = "updateCustField3rdPage";

				break;
				
			case ERRINACTION :

				forward = "updateCustFieldFail";

				break;

		}

		Global.println("ffff forward===" + forward);
		return forward;
	}

	
	/**
	 * This method will determine the state of the sub-action, then calls the suitable
	 * method of BDLG and gets the data
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsCustFieldInfoModel getCustFieldInfoDets(EtsIssObjectKey etsIssObjKey) throws SQLException, Exception {
	
		EtsCustFieldInfoModel custFieldModel = new EtsCustFieldInfoModel();
		
		int state = getUpdateCustFieldSubActionState(etsIssObjKey);
		
		Global.println("current state in getCustFieldInfoDets()===" + state);
		
		EtsIssUpdateCustFieldBdlg updateCustFieldDataPrep = new EtsIssUpdateCustFieldBdlg(etsIssObjKey, state);
		
		switch (state) {
		
			case UPDATECUSTFIELD1STPAGE :
		
				custFieldModel = updateCustFieldDataPrep.getFirstPageDets();
		
				break;
		
			case UPDATECUSTFIELDCONTINUE :
		
				custFieldModel = updateCustFieldDataPrep.getContUpdateCustFieldDetails();
		
				break;
				
			case UPDATECUSTFIELDSUBMIT :

				custFieldModel = updateCustFieldDataPrep.getSubmitUpdateCustFieldDetails();
			
				break;
							
		}
		
		return custFieldModel;
	
	}
		
}
