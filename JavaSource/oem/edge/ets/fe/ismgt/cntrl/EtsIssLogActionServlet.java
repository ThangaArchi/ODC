package oem.edge.ets.fe.ismgt.cntrl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ismgt.actions.FilterCommandFactory;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
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
 * This servlet will popup in a separate window the logs of actions
 * taken on a given issue by a series of actors on issues
 *
 */
public class EtsIssLogActionServlet extends HttpServlet implements EtsIssFilterConstants {

	public static final String VERSION = "1.10";

	public void init(ServletConfig config) throws ServletException {

	}

	/**
	 * This service method is the core method which will do the following actions in ETS Issues Logs
	
	 * 
	 */

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Hashtable params = new Hashtable(); //for params
		EdgeAccessCntrl es = es = new EdgeAccessCntrl();
		int state = 110;
		EtsIssFilterObjectKey issobjkey = new EtsIssFilterObjectKey();
		
		DbConnect db = null;
		
		//String projectidStr = request.getParameter("proj");
		String projectidStr = (String)request.getSession().getAttribute("proj");

		try {
			
			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			//get servlet state
			state = getServletState(request);

			if (!Global.loaded) {
				Global.Init();
			}

			//get the form params
			params = AmtCommonUtils.getServletParameters(request);

			
			
			if (es.GetProfile(response, request)) {
				//comment this for users who dont have ETS_PROJECTS Entitlement v2sagar

				/*if (!es.Qualify("ETS_PROJECTS", "tg_member=MD")) {
					System.out.println("Doesnt have proper previlages------------------>>>>>>>");
					response.sendRedirect("ETSConnectServlet.wss");
					return;
				}*/					
								
				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn,projectidStr);

				//get key object
				issobjkey = getEtsIssFilterObjKey(request,es,proj);
				request.setAttribute("issfilterkey", issobjkey);

				//get command factory and assign toit
				FilterCommandFactory commFac = new FilterCommandFactory();
				int processreq = commFac.createFilterCommand(request, response, issobjkey).processRequest();

				//dispatch to suitable view
				EtsIssFilterDispatchCntrl dispatchCntrl = new EtsIssFilterDispatchCntrl(request, response, issobjkey);
				dispatchCntrl.dispatchRequest(processreq);

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			log("AMT Exception at " + amtException.getErrorLocation());
			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssLogActionServlet", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsIssLogActionServlet", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {
			
			if (db != null)
			db.closeConn();

		}

	} //end of service

	/***
	 * to return the state of the servlet
	 * 
	 */

	private int getServletState(HttpServletRequest request) {

		//get opn
		String opn = AmtCommonUtils.getTrimStr(request.getParameter("opn"));
		int state = 110;

		//get the  opn in int
		if (opn != null && !opn.equals("")) {
			state = Integer.parseInt(opn); //get the  state of the Servlet, i.e state=operation
		}

		if (opn.equals("")) {
			state = 110;
		}

		if (!AmtCommonUtils.isResourceDefined(opn)) {

			opn = "110";
		}

		return state;
	} //end of method

	/**
	 * get Issue Filter Object Key
	 */

	private EtsIssFilterObjectKey getEtsIssFilterObjKey(HttpServletRequest request, EdgeAccessCntrl es,ETSProj proj) throws SQLException,Exception{

		EtsIssFilterObjKeyPrep etsKeyObjPrep = null;
		EtsIssFilterObjectKey issFilterObjkey = new EtsIssFilterObjectKey();

		//get key object
		etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
		issFilterObjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request,es,proj);
	

		return issFilterObjkey;

	}

}

