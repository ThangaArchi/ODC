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
import oem.edge.ets.fe.ismgt.bdlg.DownloadAllIssuesDataPrepRep;
import oem.edge.ets.fe.ismgt.bdlg.FilterDetailsDataPrepAbsBean;
import oem.edge.ets.fe.ismgt.bdlg.FilterDetailsPrepFactory;
import oem.edge.ets.fe.ismgt.bdlg.FilterDetailsPrepFactoryIF;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * @author Dharanendra Prasad
 *
 */
public class EtsCSVDownLoadAllIssuesAction  extends Action implements EtsIssFilterConstants {

	/**
	 * 
	 */
	public EtsCSVDownLoadAllIssuesAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		Global.println("ENTERING CSV ACTION");

		ActionForward forward = new ActionForward();

		EdgeAccessCntrl es = new EdgeAccessCntrl();

		//helper objects//

		EtsIssFilterObjKeyPrep etsKeyObjPrep = null;
		EtsIssFilterObjectKey issobjkey = new EtsIssFilterObjectKey();

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

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, proj.getProjectId()).equals(Defines.INVALID_USER)) {

					return new ActionForward("chkUserRole");

				}

				//prepare iss filter key object
				//		get key object
				etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				issobjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request, es, proj);
				
				DownloadAllIssuesDataPrepRep downloadAllIssuesDataPrepRep = new DownloadAllIssuesDataPrepRep(issobjkey);		
				ArrayList downLoadList = downloadAllIssuesDataPrepRep.getDownloadList();
				
				
				if(downLoadList != null ) {
				
				for(int i=0;i<downLoadList.size();i++){
					
					Global.println("array list iii=="+i);
				}
				
				
				}
				
				String downLoadCsvName = downloadAllIssuesDataPrepRep.getUniqCsvName();
				
				Global.println("SETTING IN REQUEST=== DONE");

				//csv name
				request.setAttribute("CSVNAME", downLoadCsvName);

				//csv list
				request.setAttribute("CSVARRAY", downLoadList);
				
				forward = mapping.findForward("downLoadSuccess");
				
				Global.println("SETTING IN REQUEST=== complete");

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
		
}
