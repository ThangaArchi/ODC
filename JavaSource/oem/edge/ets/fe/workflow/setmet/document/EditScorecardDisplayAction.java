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


public class EditScorecardDisplayAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private static Log logger = WorkflowLogger.getLogger(EditScorecardDisplayAction.class);


	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
			{
		String mID = request.getParameter("mid");
		logger.debug("mid==>"+mID);

		ArrayList scorecard = new ArrayList();
		ArrayList questionList = new ArrayList();
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardBL		  			 object=  new ScorecardBL();
		ScorecardDAO           scorecardDAO= new ScorecardDAO();
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		String workflowID = request.getParameter("workflowID");
		String scoredBy = scorecardDAO.getScoredBy(mID);
		logger.debug("ScoredBy===>"+scoredBy);
		fBean.setScorer(scoredBy);

		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));

		document.setProjectID(fBean.getProj());
		document.setWorkflowID(fBean.getWorkflowID());
	    document.setClientName(company);
		document.setLastUsr(loggedUser);
		Scorecard objScorecard = new Scorecard();
		logger.debug("the value of the requestor is"+loggedUser);
		logger.debug("The company name is"+company);
		boolean canAccess = canAccess(request, "SCORECARD", "EDIT");

		String accessible = new Boolean(canAccess).toString();
		fBean.setAccessible(accessible);
		
		fBean.setFirstQuarter("First Quarter");
		fBean.setSecondQuarter("Second Quarer");
		fBean.setThirdQuarter("ThirdQuarter");
		fBean.setFourthQuarter("FourthQuarter");

			try
			{
				fBean.setWorkflowName(scorecardDAO.getWorkflowName(projectID,workflowID));
				objScorecard.setMatrixID(mID);
				objScorecard.setProjectID(fBean.getProj());
				objScorecard.setWorkflowId(fBean.getWorkflowID());

				int intCurrentYear = scorecardDAO.getYear();

				String currentYear = new Integer(intCurrentYear).toString();

				System.out.println("current year==>"+currentYear);


				int currentQuarter = scorecardDAO.getQuarter();
				System.out.println("current quarter"+currentQuarter);

				questionList = scorecardDAO.getBaseQuestions(objScorecard);

				ArrayList localRatingQuestionsList = scorecardDAO.getLocalQuestions(objScorecard, company);

				questionList.addAll(localRatingQuestionsList);

				fBean.setScoreMatrixStatus(scorecardDAO.getScorecardStatus(objScorecard));

				ArrayList workflowDefs = scorecardDAO.getWorkflowDefinitions(fBean.getProj(), workflowID);
				
				System.out.println(workflowDefs);

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

				String userID = scorecardDAO.getScoredBy(fBean.getProj(),workflowID, mID);

				ArrayList questionRatingList = scorecardDAO.getRespondentQuestionsList(fBean.getProj(), company, workflowIds, userID);

			fBean.setRatingQuestions(questionRatingList);
			fBean.setQuestions(questionList);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}


			return mapping.findForward("success");

	}

}
