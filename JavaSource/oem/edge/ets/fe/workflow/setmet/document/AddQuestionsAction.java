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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.documents.DocumentException;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;



public class AddQuestionsAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	private Log logger  = WorkflowLogger.getLogger(AddQuestionsAction.class);
	 
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		ScorecardBL		  			 object=  new ScorecardBL();
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		document.setProjectID(fBean.getProj());
		ArrayList questions = new ArrayList();
		ArrayList scorecard = new ArrayList();
		Scorecard objScorecard = fBean.getScorecard();
		String matrixID = request.getParameter("mid");
		String workflowType = request.getParameter("wf_type");
		objScorecard.setMatrixID(matrixID);
		objScorecard.setProjectID(projectID);
		objScorecard.setOverallcomment(fBean.getComment());				
		String workflowID = request.getParameter("workflowID");
		fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));
		objScorecard.setWorkflowId(workflowID);
		String updateStatus = null;
		boolean canAccess = canAccess(request, "SCORECARD", "ADDQUESTIONS");
		if(canAccess)
		{
		try
		{
			
			document.setWorkflowID(fBean.getWorkflowID());
		    document.setClientName(company);
			document.setLastUsr(loggedUser);
			
			fBean.setIncomplete("");
			fBean.setComplete("");
			fBean.setScoreMatrixStatus("");
			String questionScore = fBean.getAddQuestionScore();
			
			logger.debug("the value of the requestor is"+loggedUser);
			logger.debug("The company name is"+company);
			
			String questionDesc = fBean.getAddQuestionDesc();
			logger.debug("questionScore====>"+questionScore);
			
			if(questionScore.equals("Select a rating"));
			{
				fBean.setAddQuestionScore("0");
			}
					
			Question question = new Question();
			question.setQues_desc(questionDesc);
			question.setLast_userID(loggedUser);
			question.setCompany(company);
			question.setLast_scored_quarter(scorecardDAO.getQuarter());
			question.setLast_scored_year(scorecardDAO.getYear());
			question.setLast_timestamp(new Timestamp(System.currentTimeMillis()));
			question.setWorkflowType(workflowType);
			String maxQuestionID = scorecardDAO.getMaxQuestionID();
			question.setQuestion_id(maxQuestionID);
			logger.debug("fBean.getQuestion()==>"+questionDesc);
			
			ActionErrors pdErrors = null;
			ArrayList errors = new ArrayList();
			
			pdErrors =
				validate(
					scorecardDAO,
					fBean
					);
			
			if (pdErrors.size() > 0) {
				
				logger.debug("pdErrors.size()==>"+pdErrors.size());
				
				Iterator actionMessages = pdErrors.get();
				errors.removeAll(errors);
				while(actionMessages.hasNext())
				{
					ActionMessage actionMessage = (ActionMessage)actionMessages.next();
					String message = actionMessage.getKey();
					errors.add(message);
					logger.debug("actionMessage==>"+message);
				}
								
				fBean.setErrors(errors);
				
				return new ActionForward("/addNewQuestionsDisplayAction.wss?workflowID="+fBean.getWorkflowID()+"&proj="+projectID+"&tc="+tc+"&mid="+matrixID);
			}
			
			fBean.setErrors(new ArrayList());
					
			if(!scorecardDAO.isQuestionExists(questionDesc, company))
			{
				
				logger.debug(new Boolean(scorecardDAO.addQuestion(question)).toString());
				objScorecard.setQuestionID(maxQuestionID);
				objScorecard.setRating(questionScore);
				logger.debug("fBean.getAddQuestionScore()==>"+questionScore);
				
				logger.debug("scorecardDAO.addScorecardQuestion()==>"+scorecardDAO.addScorecardQuestion(objScorecard));
				logger.debug("matrixID===>"+matrixID);
				
				ArrayList scoringMatricesList = scorecardDAO.getScoringMatrices(objScorecard);
				//scoringMatricesList.remove(matrixID);				
				
				Iterator scoringMatricesIterator = scoringMatricesList.iterator();
				
				while(scoringMatricesIterator.hasNext())
					{
					
					String tempMatrixID = (String)scoringMatricesIterator.next();
					logger.debug("Trying to update the scoring matrices"+tempMatrixID);
					if(!tempMatrixID.equals(matrixID))
						{	
							objScorecard.setMatrixID(tempMatrixID);
							objScorecard.setRating("0");
							logger.debug("tempScorecard.getMatrixID()===>"+tempMatrixID);
							logger.debug("scorecardDAO.addScorecardQuestion()==>"+scorecardDAO.addScorecardQuestion(objScorecard));
						}
					}	
			}
			else
			{
				logger.debug("The question is already exists");
			}
			
					
			request.setAttribute("mid", fBean.getScorecard().getMatrixID());
			
			logger.debug("fBean.getScorecard().getMatrixID()==>"+fBean.getScorecard().getMatrixID());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			request.setAttribute("WORKFLOWTYPE",workflowType);
			return mapping.findForward("success");
		}
		else
		{
			return mapping.findForward("failure");
		}
	}
	/**
	 * @param udForm
	 * @param bIsUpdateDocProp
	 * @return
	 */
	private ActionErrors validate(
		ScorecardDAO udDAO,
		ScorecardFormBean udForm)
		{
		ActionErrors pdErrors = new ActionErrors();

		// Check question description 
		if (StringUtil.isNullorEmpty(udForm.getAddQuestionDesc())){
			//|| udForm.getAddQuestionDesc().length() > DocConstants.MAX_NAME_LENGTH) {
			pdErrors.add(
				"Question Description Error",
				new ActionMessage("Question description is empty", "1") );
		} 
		
		/*
		 * 
		 int score = Integer.parseInt(udForm.getAddQuestionScore());
		if(score == 0 )
		{
			pdErrors.add(
					"Question Rating Error",
					new ActionMessage("Question rating should be greater than or equal to one", "2"));
		}
		*/
		return pdErrors;
	}

}
