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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class CreateScorecardDisplayAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private static Log logger = WorkflowLogger.getLogger(CreateScorecardDisplayAction.class);
	
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		ArrayList scorecard = new ArrayList();
		ArrayList questionList = new ArrayList();
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		
		ScorecardDAO           scorecardDAO= new ScorecardDAO(); 
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		
		document.setProjectID(fBean.getProj());
		document.setWorkflowID(fBean.getWorkflowID());
	    document.setClientName(company);
		document.setLastUsr(loggedUser);
			
		String workflowID = request.getParameter("workflowID");
		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));
		
		
		logger.debug("the value of the requestor is"+loggedUser);
		logger.debug("The company name is"+company);
		
		boolean canAccess = canAccess(request, "SCORECARD", "CREATE");
		
		if(canAccess)
		{
			Scorecard objScorecard = new Scorecard();
			
			objScorecard.setProjectID(fBean.getProj());
			objScorecard.setWorkflowId(fBean.getWorkflowID());
			objScorecard.setLast_userId(loggedUser);
			
			if(fBean.getAction()==null){
				  
				
				scorecard = fBean.getScorers();
				logger.debug(scorecard);
			
				try
				{
				String matrixID = ETSCalendar.getNewCalendarId();
				fBean.setMatrixID(matrixID);
				System.out.println("matrixID"+ matrixID);
				request.setAttribute("mid", matrixID);
				
				
				ArrayList companyClientAttendees = scorecardDAO.getClientAttendees(company);
				
				
				ArrayList companyRespondents = scorecardDAO.getClientRespondents(company);
				logger.debug("companyCompanyRespondents======>"+companyRespondents);
				ArrayList companyClientRespondents = scorecardDAO.getClientRespondents(objScorecard);
				logger.debug("companyClientRespondents======>"+companyClientRespondents);
				ArrayList projectClientRespondents = getRemainedRespondents(companyRespondents, 
																					companyClientRespondents);
				
				logger.debug("companyClientRespondents======>"+projectClientRespondents);
				
				ArrayList projectClientAttendees = scorecardDAO.getClientAttendees(objScorecard);
				
				logger.debug(new Boolean(companyClientAttendees.removeAll(projectClientAttendees)).toString());
				
				logger.debug("companyClientAttendees===>"+companyClientAttendees);
				
				fBean.setClientAttendees(companyClientAttendees);
								
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}	
			return mapping.findForward("success");
		}
		else
		{
			return mapping.findForward("failure");
		}
	}
	public ArrayList getRemainedRespondents(ArrayList companyRespondents, ArrayList addedRespondents)
	{
		if(companyRespondents.size() > addedRespondents.size())
		{
			Iterator companyRespondentsIterator = companyRespondents.iterator();
			while(companyRespondentsIterator.hasNext())
			{
				ClientAttendee clientAttendee = (ClientAttendee)companyRespondentsIterator.next();
				String client_id = clientAttendee.getClientId();
				for(int i = 0; i < addedRespondents.size(); i++ )
				{
					ClientAttendee clntAttendee = (ClientAttendee)addedRespondents.get(i);
					if(client_id.equals(clntAttendee.getClientId()))
					{
						companyRespondentsIterator.remove();
					}
				}
			}
		}
		else
		{
			
		}
		
		return companyRespondents;
	}
}
