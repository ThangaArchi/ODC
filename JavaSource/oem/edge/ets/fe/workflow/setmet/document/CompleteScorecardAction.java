/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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


package oem.edge.ets.fe.workflow.setmet.document;

/**
 * @author Amar
 *
 * 
 */


import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class CompleteScorecardAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private static Log logger = WorkflowLogger.getLogger(CompleteScorecardAction.class);
	
	
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		ScorecardBL		  			 object=  new ScorecardBL();
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		String workflowType = request.getParameter("wf_type");
		document.setProjectID(fBean.getProj());
		ArrayList questions = new ArrayList();
		ArrayList scorecard = new ArrayList();
		Scorecard objScorecard = fBean.getScorecard();
		String updateStatus = null;
		boolean canAccess = canAccess(request, "SCORECARD", "EDIT");
		if(canAccess)
		{
			try
			{
			
			document.setWorkflowID(fBean.getWorkflowID());
		    document.setClientName(company);
			document.setLastUsr(loggedUser);
			
			logger.debug("the value of the requestor is"+loggedUser);
			logger.debug("The company name is"+company);
			
			
			questions = fBean.getQuestions();
			objScorecard.setWorkflowId(fBean.getWorkflowID());
			objScorecard.setProjectID(fBean.getProj());
			
			objScorecard.setMatrixID(request.getParameter("mid"));
			
			String workflowID = request.getParameter("workflowID");
			fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));
			
			logger.debug("1"+objScorecard.getProjectID());
			logger.debug("2"+objScorecard.getWorkflowId());
			logger.debug("3"+objScorecard.getMatrixID());
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			
			logger.debug("scorecardDAO.updateScorecard(objScorecard)==>" +scorecardDAO.updateScorecard(objScorecard));
			if(!"SetMet".equalsIgnoreCase(workflowType)){
				return new ActionForward("/documentStage.wss?wf_type="+workflowType+"&workflowID="+objScorecard.getWorkflowId()+"&proj="+objScorecard.getProjectID()+"&tc="+tc);
				
			}
			return mapping.findForward("success");		
		}else
		{
			return mapping.findForward("failure");
		}
	}
}
