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

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class CreateScorecardAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private static Log logger = WorkflowLogger.getLogger(CreateScorecardAction.class);
	
	
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ArrayList questionList = new ArrayList();
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		
		ScorecardDAO           scorecardDAO= new ScorecardDAO(); 
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		
		document.setProjectID(fBean.getProj());
		document.setWorkflowID(fBean.getWorkflowID());
	    document.setClientName(company);
		document.setLastUsr(loggedUser);
		
		logger.debug("the value of the requestor is"+loggedUser);
		logger.debug("The company name is"+company);
		boolean canAccess = canAccess(request, "SCORECARD", "CREATE");
		Scorecard objScorecard = new Scorecard();
		
		
		String workflowID = request.getParameter("workflowID");
		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));
		
		objScorecard.setProjectID(fBean.getProj());
		objScorecard.setWorkflowId(fBean.getWorkflowID());
		objScorecard.setLast_userId(loggedUser);
		
		if(canAccess)
		{
			//fBean.setClientAttendees(new ArrayList());
			
			String clientAttendee = fBean.getClientAttendee();
			try
			{
			questionList = scorecardDAO.getBaseQuestions(objScorecard);
			questionList.addAll(scorecardDAO.getLocalQuestions(company));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			logger.debug("questionList==>"+questionList);
			
			for(int i = 0; i < questionList.size(); i++)
			{
				Question question = (Question)questionList.get(i);
				question.setScore(new Integer(0).toString());
				logger.debug("question.getScore()===>"+question.getScore());
				
			}
					
			Iterator questionsIter = questionList.iterator();
			while(questionsIter.hasNext())
			{
				Question question = (Question) questionsIter.next();
				String tempQuestionRating = question.getScore();
				if(tempQuestionRating.equals("Select a rating"))
				{
					tempQuestionRating ="0";
					question.setScore(tempQuestionRating);
				}
				logger.debug("question.getScore()====>"+question.getScore());
				
			}
			
			try
			{
				String matrixID = request.getParameter("mid");
				
				if(matrixID == null || matrixID.equals(""))
				{
					matrixID = fBean.getMatrixID();
					request.setAttribute("mid", matrixID);
				}
				System.out.println("matrixID"+matrixID);
				fBean.setMatrixID(matrixID);
				logger.debug("clientAttendee====>"+clientAttendee);
				
				String user_id = scorecardDAO.getClientAttendeeID(clientAttendee);
				logger.debug("user_id ====>"+user_id);
				
				if(!scorecardDAO.isClientAttendeeExists(user_id, fBean.getProj(), fBean.getWorkflowID()))
				{
				logger.debug("scorecardDAO.addScorecardAttendee()==>"+
						scorecardDAO.addScorecardAttendee(fBean.getProj(), fBean.getWorkflowID(),user_id));
				}
				Scorecard scorecard = new Scorecard();				
				scorecard.setMatrixID(matrixID);
				scorecard.setProjectID(fBean.getProj());
				scorecard.setWorkflowId(fBean.getWorkflowID());
				scorecard.setScoredBy(user_id);
				scorecard.setScorecardStatus('I');
				scorecard.setLast_userId(loggedUser);
				
				request.setAttribute("mid", scorecard.getMatrixID());
				
				System.out.println("scorecard.getMatrixID()==>"+scorecard.getMatrixID());
				scorecard = scorecardDAO.addScoringMatrix(scorecard);
				scorecard.setQuestions(questionList);
				
				if(!scorecard.getMatrixID().equals(null))
					{
						logger.debug(scorecardDAO.addScore(scorecard));
					}
				
				
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			
			return mapping.findForward("success");
		}
		else
		{
			return mapping.findForward("failure");
		}
	}
	
	private Question getDefaultQuestion(Question question)
	{
		question.setCompany("");
		question.setLast_scored_quarter(0);
		question.setLast_scored_year(0);
		question.setLast_timestamp(new java.sql.Timestamp(System.currentTimeMillis()));
		question.setLast_userID("");
		question.setQues_desc("");
		question.setQuestion_id("");
		question.setQuestion_type("");
		question.setScore("0");
		question.setVersion(0);
		
		return question;
	}
	
}
