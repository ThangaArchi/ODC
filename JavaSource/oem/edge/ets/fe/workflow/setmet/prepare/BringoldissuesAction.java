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

package oem.edge.ets.fe.workflow.setmet.prepare;

import java.io.IOException;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;


/**
 * Class       : BringoldissuesAction
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class BringoldissuesAction extends WorkflowAction

{

	private static Log logger = WorkflowLogger.getLogger(BringoldissuesAction.class);
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		ListOfExistingIssuesFormBean formBean = (ListOfExistingIssuesFormBean) form;
		ActionForward forward = new ActionForward(mapping.getParameter());
		ListOfExistingIssuesVO vo = null;
		ListOfExistingIssuesBL bl = new ListOfExistingIssuesBL();
		
		request.getSession().removeAttribute("errorMessages");
		String workflowID = request.getParameter("workflowID");
		String tc = request.getParameter("tc");
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
/////////////////////////////////////////////////////////////////////////////////////////
		
		//forward = new ActionForward(forward.getPath()+"?proj="+projectID+"&workflowID="+workflowID+"&tc="+tc,true); 
		if("submit".equalsIgnoreCase(formBean.getAction())){
			vo =(ListOfExistingIssuesVO) formBean.getWorkflowObject();
				vo.setProjectID(formBean.getProj());
				vo.setWorkflowID(formBean.getWorkflowID());
				request.setAttribute("WORKFLOW_ID",vo.getWorkflowID());
				if(isMemberOrVisitor(request))
				{
					forward = mapping.findForward("badURL");
					request.setAttribute("WFerr","You are unauthorized to perform this operation on the Set/Met.");
					try{db.close();}catch(Exception e){System.err.println(e);}
					request.removeAttribute("WFdb");
					return forward;
				}
				bl.setLoggedUser(loggedUser);
				if (!bl.bringOldIssues(vo)) {
					System.out.println("Failure resulted in bringoldissues Action");
					//TODO: failure mapping is not present in the struts-workflow-config.
					
				    forward = mapping.findForward("success");
				} else {
					if(bl.getHasCreatedNewPrepareObj())
					 request.setAttribute("prepare_completed","yes");
					
					forward = mapping.findForward("success");
					System.out.println("bringoldissues: Forward to dddgdgdgdggd"+forward);
				}
				//vo.reset();
		}
		System.out.println(forward);
		try{db.close();}catch(Exception e){System.err.println(e);}
		request.removeAttribute("WFdb");
		return (forward);
	}
}