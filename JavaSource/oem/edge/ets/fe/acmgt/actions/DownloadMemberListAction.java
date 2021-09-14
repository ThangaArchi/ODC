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

package oem.edge.ets.fe.acmgt.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.dao.WrkSpcInfoDAO;

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
public class DownloadMemberListAction extends Action {

	/**
	 * 
	 */
	public DownloadMemberListAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	{
		Connection conn = null;
		
		Global.println("ENTERING CSV ACTION");

		ActionForward forward = new ActionForward();

		EdgeAccessCntrl es = new EdgeAccessCntrl();
		String lastUserId = es.gIR_USERN;

		DbConnect db = null;

		String action = request.getParameter("action");
		if (action == null || action.trim().equals("")) {
			action = "";
		} else {
			action = action.trim();
		}
		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			conn = WrkSpcTeamUtils.getConnection();
			
			if (es.GetProfile(response, request)) {

				String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

				ETSProj etsProj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, etsProj.getProjectId()).equals(Defines.INVALID_USER)) {
					return new ActionForward("chkUserRole");
				}
				if (action.equals("template")) {
					ArrayList templateList = new ArrayList();
					templateList = getTemplateList();
					
					String templateName = "template.csv";
					Global.println("SETTING IN REQUEST=== DONE");
					//csv name
					request.setAttribute("CSVNAME", templateName);
					//csv list
					request.setAttribute("CSVARRAY", templateList);				

				} else {
					// download Team Members list
					WrkSpcInfoDAO wrkspcDAO = new WrkSpcInfoDAO();
					ArrayList downLoadList = wrkspcDAO
							.getDownLoadAllMembersList(projectidStr);

					if (downLoadList != null) {
						for (int i = 0; i < downLoadList.size(); i++) {
							Global.println("array list iii==" + i);
						}
					}

					String downLoadCsvName = getDownLoadCsvFileName(etsProj);
					Global.println("SETTING IN REQUEST=== DONE");
					//csv name
					request.setAttribute("CSVNAME", downLoadCsvName);
					//csv list
					request.setAttribute("CSVARRAY", downLoadList);				
				}
				
				forward = mapping.findForward("downLoadSuccess");			
				Global.println("SETTING IN REQUEST=== complete");

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in DownloadMemberListAction", lastUserId);
				ETSDBUtils.close(conn);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", lastUserId);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}
			
			return mapping.findForward("error");

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in DownloadMemberListAction", lastUserId);

			if (conn != null) {
				ETSDBUtils.close(conn);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}
			
			return mapping.findForward("error");

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building DownloadMemberListAction", lastUserId);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}
			
			return mapping.findForward("error");

		} finally {

			if (conn != null)
				ETSDBUtils.close(conn);
			conn = null;
		}

		return forward;

	}

	private String getDownLoadCsvFileName(ETSProj  etsProj){

		String csvFileName = "";
		String tempFileName = etsProj.getName();
				
		if(tempFileName.indexOf(':') != -1)  tempFileName = tempFileName.replace(':','-');
		if(tempFileName.indexOf('\\') != -1) tempFileName = tempFileName.replace('\\','-');
		if(tempFileName.indexOf('/') != -1)  tempFileName = tempFileName.replace('/','-');
		if(tempFileName.indexOf('|') != -1)  tempFileName = tempFileName.replace('|','-');
		if(tempFileName.indexOf('?') != -1)  tempFileName = tempFileName.replace('?','-');
		if(tempFileName.indexOf('"') != -1)  tempFileName = tempFileName.replace('"','-');
		
		csvFileName = tempFileName + "_Team_Members.csv";
		
		return csvFileName;
	}
	
	private ArrayList getTemplateList() {
		ArrayList tempList = new ArrayList();
		
		ArrayList headerList = new ArrayList();
		headerList.add("Name(optional)");
		headerList.add("User Id(required)");
		headerList.add("User Email(required)");
		headerList.add("Access Level(required)");
		headerList.add("Job Responsibility(optional)");
		headerList.add("Messenger Id(optional)");
				
		tempList.add(headerList);
		
		return tempList;
	}

}
