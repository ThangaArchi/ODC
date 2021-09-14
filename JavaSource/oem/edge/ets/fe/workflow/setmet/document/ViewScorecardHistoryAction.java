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


public class ViewScorecardHistoryAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private Log logger = WorkflowLogger.getLogger(ViewScorecardHistoryAction.class);
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
	
		ArrayList questionList = new ArrayList();
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		int intCurrentYear = scorecardDAO.getYear();
		String workflowID = request.getParameter("workflowID");
		
		fBean.setFirstQuarter("First Quarter");
		fBean.setSecondQuarter("Second Quarer");
		fBean.setThirdQuarter("ThirdQuarter");
		fBean.setFourthQuarter("FourthQuarter");
		
		String currentYear = new Integer(intCurrentYear).toString();
		
		System.out.println("current year==>"+currentYear);
		
		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));		
		int currentQuarter = scorecardDAO.getQuarter();
		System.out.println("current quarter"+currentQuarter);
		
		
		document.setProjectID(fBean.getProj());
		document.setWorkflowID(fBean.getWorkflowID());
	    document.setClientName(company);
		document.setLastUsr(loggedUser);
		
		Scorecard scorecard = new Scorecard();
		scorecard.setMatrixID("");
		scorecard.setProjectID(fBean.getProj());
		scorecard.setWorkflowId(fBean.getWorkflowID());
		
		System.out.println("the value of the requestor is"+loggedUser);
		System.out.println("The company name is"+company);
		ArrayList workflowDefs = scorecardDAO.getWorkflowDefinitions(fBean.getProj(), workflowID);
		logger.debug("WorkflowDefinitions===>"+workflowDefs);
		
		//fBean = setQuarterStrings(fBean, intCurrentYear, currentQuarter, workflowDefs);
		
		for(int i = 0; i < workflowDefs.size(); i++)
		{
			WorkflowDefinition workflowDef = (WorkflowDefinition)workflowDefs.get(i);
			
			if(i==0)
			{
				fBean.setFirstQuarter("Q"+workflowDef.getQuarter()+" "+workflowDef.getYear()+" "+workflowDef.getMeetingDate());
			}
			if(i==1)
			{
				fBean.setSecondQuarter("Q"+workflowDef.getQuarter()+" "+workflowDef.getYear()+" "+workflowDef.getMeetingDate());
			}
			if(i==2)
			{
				fBean.setThirdQuarter("Q"+workflowDef.getQuarter()+" "+workflowDef.getYear()+" "+workflowDef.getMeetingDate());
			}
			if(i==3)
			{
				fBean.setFourthQuarter("Q"+workflowDef.getQuarter()+" "+workflowDef.getYear()+" "+workflowDef.getMeetingDate());
			}
		}
		
		ArrayList workflowIds = new ArrayList();
		
		for(int i = 0; i <workflowDefs.size(); i++)
		{
			WorkflowDefinition workflowDef = (WorkflowDefinition)workflowDefs.get(i);
			workflowIds.add(workflowDef.getWorkflowID());
		}
		
		questionList = scorecardDAO.getQuestionsList(scorecard, company, workflowIds);
		 
		
		fBean.setQuestions(questionList);
		return mapping.findForward("success");
	}
		
}
	

