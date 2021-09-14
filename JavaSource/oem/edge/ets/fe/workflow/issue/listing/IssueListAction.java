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


package oem.edge.ets.fe.workflow.issue.listing;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.newissue.NewIssueBL;
import oem.edge.ets.fe.workflow.newissue.NewIssueVO;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class       : IssueListAction
 * Package     : oem.edge.ets.fe.workflow.issue.listing
 * Description : 
 * Date		   : Oct 10, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueListAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(IssueListAction.class);
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		ActionForward forward = new ActionForward();
		String proj = request.getParameter("proj");
		String tc = request.getParameter("tc");
		String workflowID = request.getParameter("workflowID");
		request.setAttribute("proj", proj);
		request.setAttribute("tc", tc);
		request.setAttribute("workflowID", workflowID);
		System.out.println("proj: " + proj);
		System.out.println("tc: " + tc);
		System.out.println("workflowID: " + workflowID);
		
		MiscUtils.setDB(request);
		DBAccess db = (DBAccess)request.getAttribute("WFdb");
///////////////////////////////////////////////////////////////////////////////////////
		boolean badFlag = false;
		if(projectID == null || !MiscUtils.isValidProject(projectID,db))
		{
		request.setAttribute("WFerr","Bad project ID");
		forward = mapping.findForward("badURL");
		badFlag = true;
		}
		else
		{
		if(workflowID == null || !MiscUtils.isValidWorkflow(projectID, workflowID,db))
		{
		request.setAttribute("WFerr","Bad workflow ID");
		forward = mapping.findForward("badURL");
		badFlag = true;
		}
		}
		
		if(tc == null)
		{
		request.setAttribute("WFerr","Bad top category");
		forward = mapping.findForward("badURL");
		badFlag = true;
		}
		if(badFlag == true)
		{
			try{db.close();}catch(Exception e){System.err.println(e);}
			request.removeAttribute("WFdb");
		return forward;
		}
		System.out.println("forward="+forward);
////////////////////////////////////////////////////////////////////////////////////////		/

		
		

		if(request.getParameter("extend")==null)
			request.getSession().removeAttribute("errorMessages");
		System.out.println("In IssueListAction");
		if(form==null || form.getAction()==null || form.getAction().length()==0)
		{
			NewIssueVO tempVO = new NewIssueVO();
			tempVO.setProjectID(projectID);
			tempVO.setWorkflowID(workflowID);
			tempVO.setDB(db);
			if(canAccess(request,"ISSUES","CREATE")&& !(new NewIssueBL()).isIssueQuotaExhausted(wf_type, tempVO))
				request.setAttribute("perm_create_issue","yes");
			forward = mapping.findForward("issues");
		}
		else
		{
			if(form.getAction().equalsIgnoreCase("submit"))
				forward = mapping.findForward("newIssue");
		}
		try{db.close();}catch(Exception e){System.err.println(e);}
		request.removeAttribute("WFdb");
		return forward;
		
	}

}

