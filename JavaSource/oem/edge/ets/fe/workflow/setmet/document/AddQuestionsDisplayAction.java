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


public class AddQuestionsDisplayAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	
	private static Log logger =	 WorkflowLogger.getLogger(AddQuestionsDisplayAction.class);
	
	/*
	 * protected boolean canAccess(HttpServletRequest pdRequest,String resource, String action){
 		boolean canAccess = false;
 		if("SETMET".equalsIgnoreCase(resource)){
 			if("CREATE".equalsIgnoreCase(action)){
 		 			if(isWspaceOwner(pdRequest) || isWspaceMgr(pdRequest))
 		 				canAccess = true;
 			}else{
 				if(isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest))
		 				canAccess = true;
 			}
 		}
 		if("SCORECARD".equalsIgnoreCase(resource)){
 			  if("CREATE".equalsIgnoreCase(action) || "EDIT".equalsIgnoreCase(action) || "ADDQUESTIONS".equalsIgnoreCase(action)){
 			  	if(isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest))
	 				canAccess = true;
 			  }
 			  if("CREATEBASETEMPLATE".equalsIgnoreCase(action) || "EDITBASETEMPLATE".equalsIgnoreCase(action)){
 			  	if(isSuperAdmin(pdRequest) || isWorkflowAdmin(pdRequest))
 			  		canAccess = true;
 			  }
 		}
 		if("ISSUES".equalsIgnoreCase(resource)){
 			 if("CREATE".equalsIgnoreCase(action)){
 			 	if(isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest))
	 				canAccess = true;
 			 }
 		}
 		
 		return canAccess;
 	}
 	
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		ScorecardFormBean 			 fBean = (ScorecardFormBean)form;
		ScorecardDAO scorecardDAO = new ScorecardDAO();
		ScorecardBL		  			 object=  new ScorecardBL();
		SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		document.setProjectID(fBean.getProj());
		ArrayList questions = new ArrayList();
		//ArrayList scorecard = new ArrayList();
		Scorecard objScorecard = new Scorecard();
		
		String updateStatus = null;
		String mID = request.getParameter("mid");
		logger.debug("mid==>"+mID);
		
		boolean canAccess = canAccess(request, "SCORECARD", "ADDQUESTIONS");
		
		/*
		 * 
		 if(canAccess)
			request.setAttribute("ACCESS_DENIED", "false");
		 else
			request.setAttribute("ACCESS_DENIED", "true");
		*/
		
		if(canAccess)
		{
			try
			{
			
			//document.setWorkflowID(fBean.getWorkflowID());
		    document.setClientName(company);
			document.setLastUsr(loggedUser);
			
			
			fBean.setIncomplete("");
			fBean.setComplete("");
			fBean.setAddQuestionDesc("");
			fBean.setAddQuestionScore("");
			
			
			ArrayList errorsArrayList = fBean.getErrors();
			ArrayList tempArrayList = new ArrayList();
			
			for(int i = 0; i<errorsArrayList.size();i++)
			{
				String errorString = (String)errorsArrayList.get(0);
				errorString="";
				tempArrayList.add(errorString);
			}
			fBean.setErrors(tempArrayList);
			logger.debug("the value of the requestor is"+loggedUser);
			logger.debug("The company name is"+company);
			
			questions = fBean.getQuestions();
			String workflowID = request.getParameter("workflowID");
			fBean.setWorkflowName(scorecardDAO.getWorkflowName(projectID,workflowID));
			
			fBean.setCurrentStage(ScorecardDAO.getWorkflowCurrentStage(fBean.getProj(), workflowID));
			
			objScorecard.setWorkflowId(workflowID);
			objScorecard.setProjectID(projectID);
			objScorecard.setMatrixID(mID);
					
			logger.debug("projectID===>"+objScorecard.getProjectID());
			logger.debug("workflowID==>"+objScorecard.getWorkflowId());		
			logger.debug("MatrixID====>"+objScorecard.getMatrixID());
			
			String workflowName = scorecardDAO.getWorkflowName(projectID,workflowID);
			String scoredBy = scorecardDAO.getScoredBy(request.getParameter("mid"));
			logger.debug("ScoredBy===>"+scoredBy);
			logger.debug("WorkflowName==>"+workflowName);
			
			fBean.setWorkflowName(workflowName);
			fBean.setScorer(scoredBy);
			fBean.setScorecard(objScorecard);		
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
	
}
