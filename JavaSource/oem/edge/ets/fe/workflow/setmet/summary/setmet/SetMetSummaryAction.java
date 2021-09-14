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


package oem.edge.ets.fe.workflow.setmet.summary.setmet;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : SetMetSummaryAction
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.setmet
 * Description : 
 * Date		   : Nov 21, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class SetMetSummaryAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(SetMetSummaryAction.class);
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ActionForward forward = null;
		
		SetMetSummary s = null;
		String proj = request.getParameter("proj");
		String workflowID = request.getParameter("workflowID");
		try{
	     s = new SetMetSummary(proj, workflowID,company);
		 request.setAttribute("s",s);
		 request.setAttribute("loggedUser", loggedUser);
		 Format f = new SimpleDateFormat("E, dd MMMM, yyyy");
		 String date = (f.format(new Date()));
		 request.setAttribute("today",date);
		 forward = mapping.findForward("viewSetMetSummary");
		}catch(Exception e){
			forward = mapping.findForward("badURL");
		}
		return forward;
	}

}

