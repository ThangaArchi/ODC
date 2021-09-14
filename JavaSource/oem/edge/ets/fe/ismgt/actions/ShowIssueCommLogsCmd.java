package oem.edge.ets.fe.ismgt.actions;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.bdlg.*;
import oem.edge.ets.fe.ismgt.helpers.*;
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
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ShowIssueCommLogsCmd extends FilterCommandAbsBean implements EtsIssFilterConstants{
	
	public static final String VERSION = "1.11";

	/**
	 * Constructor for ShowIssueCommLogs.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ShowIssueCommLogsCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}
	
	
	/**
	 * key process request method
	 */
	
	public int processRequest()  {
		
		int processreq = 0; //process failure
		
		try {
		
		String edgeProblemId = AmtCommonUtils.getTrimStr(getRequest().getParameter("edge_problem_id"));
		EtsIssLogsDataPrepBean logBean = new EtsIssLogsDataPrepBean(edgeProblemId);
		
		EtsPopUpBean etsPop = new EtsPopUpBean();
		//initialize page  title and page header
		etsPop.init("Issue Logs Commentary", "Issue Logs Commentary");

		getRequest().setAttribute("etspopup", etsPop);
		getRequest().setAttribute("etslogdet", logBean.createIssueLogs());
		
		processreq=1;//process success
		
		
		}
		
		catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ShowIssueCommLogsCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ShowIssueCommLogsCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}
		
		
		return processreq;	
		
		
	}

}

