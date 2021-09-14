/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.setmet.summary.issues;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
//TODO: 00 Not yet uploaded in CMVC

/**
 * Class       : IssueSummaryAction
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.issues
 * Description : 
 * Date		   : Dec 7, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueSummaryAction extends WorkflowAction{
	private static Log logger = WorkflowLogger.getLogger(IssueSummaryAction.class);

	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ActionForward forward = null;
		
		String workflowID = request.getParameter("workflowID");

		DBAccess db = null;
		try{
			db = new DBAccess();
			if(!MiscUtils.isValidWorkflow(projectID, workflowID,db))
				forward=mapping.findForward("badURL");
			db.close();
			db=null;
		}catch(Exception e){
			db = null;
			forward=mapping.findForward("badURL");
		}
		if(forward!=null)return forward;
		IssueSummary s = new IssueSummary(projectID,workflowID); 
		try{
			 request.setAttribute("s",s);
			 request.setAttribute("loggedUser", loggedUser);
			 Format f = new SimpleDateFormat("E, dd MMMM, yyyy");
			 String date = (f.format(new Date()));
			 request.setAttribute("today",date);
			 forward = mapping.findForward("issueSummary");
			
		}catch(Exception e){
			forward = mapping.findForward("badURL");
		}
		return forward;
	}
}

