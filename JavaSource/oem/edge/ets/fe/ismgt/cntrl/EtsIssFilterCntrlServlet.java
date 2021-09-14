package oem.edge.ets.fe.ismgt.cntrl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
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
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.actions.FilterCommandFactory;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssCommonSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
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
 * @version 	1.0
 * @author
 */
public class EtsIssFilterCntrlServlet extends HttpServlet implements EtsIssFilterConstants {

	public static final String VERSION = "1.44";

	/**
	 * Init method to set any config 
	 */

	public void init(ServletConfig config) throws ServletException {

	}

	/**
	 * This service method is the core method which will do the following actions in ETS Issues/Changes Filter
	 * 1.get the state of the process
	 * 2.routes to resp. Command object
	 * 3. prepares the key object that contains various key values required across issues filtereing
	 * 3.creates es.GetProfile
	 * 4.creates AmtHeaderFooter
	 */

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Hashtable params = new Hashtable(); //for params
		EdgeAccessCntrl es = es = new EdgeAccessCntrl();
		DbConnect db = null;

		ArrayList blnkList = new ArrayList(); //blank array list
		EtsIssFilterObjectKey issobjkey = new EtsIssFilterObjectKey();

		//helper objects//
		EtsIssCommonSessnParams etsCommonParams = null;
		EtsIssFilterObjKeyPrep etsKeyObjPrep = null;
		EtsAmtHfBean amtHf = null;

		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			db = new DbConnect();
			db.makeConn(ETSDATASRC);
			
			
			String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

			if (es.GetProfile(response, request)) {



				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn, projectidStr);

								
				// if not superadmin and not executive and not member, then redirect the user to landing page.
						// changed for 4.4.1
				if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.INVALID_USER)) {
					response.sendRedirect("ETSConnectServlet.wss");
					return;
				}


				//get session
				HttpSession session = request.getSession(true);

				//get key object
				etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				issobjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request, es,proj);
				request.setAttribute("issfilterkey", issobjkey);
				request.setAttribute("issviewrep", issobjkey.getRepViewBean());
				request.setAttribute("issviewfc", issobjkey.getFcViewBean());
				request.setAttribute("proj", projectidStr);
				request.getSession().setAttribute("proj", projectidStr);

				//initialize the common params bean//
				etsCommonParams = new EtsIssCommonSessnParams(session, issobjkey.getProjectId());
				ETSProj etsProj = etsCommonParams.getEtsProj();
				EtsPrimaryContactInfo etsContInfo = etsCommonParams.getEtsContInfo();

				///set amthf in session	
				amtHf = new EtsAmtHfBean(request, response, issobjkey);
				//etsCommonParams.setIssueAmtHf(amtHf);
				request.setAttribute("etsamthf", amtHf);

				//get command factory and assign toit
				FilterCommandFactory commFac = new FilterCommandFactory();
				int processreq = commFac.createFilterCommand(request, response, issobjkey).processRequest();

				//dispatch to suitable view
				EtsIssFilterDispatchCntrl dispatchCntrl = new EtsIssFilterDispatchCntrl(request, response, issobjkey);
				dispatchCntrl.dispatchRequest(processreq);

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in EtsIssFilterCntrlServlet", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			log("AMT Exception at " + amtException.getErrorLocation());
			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsIssFilterCntrlServlet", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsIssFilterCntrlServlet", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

	} //end of service

} //end of class
