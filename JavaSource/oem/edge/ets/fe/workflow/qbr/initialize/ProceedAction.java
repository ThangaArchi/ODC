/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                      */
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


package oem.edge.ets.fe.workflow.qbr.initialize;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.qbr.QbrDAO;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class       : ProceedAction
 * Package     : oem.edge.ets.fe.workflow.qbr.initialize
 * Description : 
 * Date		   : Feb 13, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class ProceedAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(ProceedAction.class);
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if(gatekeeper(mapping, form, request, response)==false)
			return mapping.findForward("badURL");
		if(isMemberOrVisitor(request))
		{
			request.setAttribute("WFerr", "You are not authorized to perform that operation");
			return mapping.findForward("badURL");
		}
		String workflowID = MiscUtils.getPA(request,"workflowID");

		if(request.getAttribute("CURRENT_STAGE")==null
				||((String)request.getAttribute("CURRENT_STAGE")).trim().length()==0
			||((String)request.getAttribute("CURRENT_STAGE")).equalsIgnoreCase(WorkflowConstants.IDENTIFY))
			QbrDAO.moveStage(projectID,workflowID,WorkflowConstants.IDENTIFY,WorkflowConstants.PREPARE,loggedUser);
		
		response.sendRedirect("prepareStage.wss?proj="+projectID+"&tc="+MiscUtils.getTc(projectID,MiscUtils.TC_ASSESSMENT)+"&workflowID="+workflowID);
		return null;
	}
	private boolean gatekeeper(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) {
		
		InitializeVO vo = null;
		if(request==null || response==null)
			return false;
	
		String workflowID = MiscUtils.getPA(request,"workflowID");
		if(projectID==null)
		{
			request.setAttribute("WFerr", "No project ID");
			return false;
		}
		if(workflowID==null && vo.getWorkflowID()==null)
		{
			request.setAttribute("WFerr", "No workflow ID");
			return false;
		}
		//TODO: Checks for whether this is a valid QBR workflow & entitlement checks
		return true;
	}
}

