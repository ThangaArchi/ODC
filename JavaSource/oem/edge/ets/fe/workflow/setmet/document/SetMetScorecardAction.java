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

/*
* Created on Sep 29, 2006
*
* TODO To change the template for this generated file go to
* Window - Preferences - Java - Code Style - Code Templates
*/
package oem.edge.ets.fe.workflow.setmet.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowException;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.OrderedMap;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
* @author Administrator
*
* TODO To change the template for this generated type comment go to
* Window - Preferences - Java - Code Style - Code Templates
*/
public class SetMetScorecardAction extends WorkflowAction {

		 
		 private static Log logger		 =		 		 WorkflowLogger.getLogger(SetMetScorecardAction.class);
		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		  */
		 public ActionForward executeWorkflow(ActionMapping mapping,
		 		 		 WorkflowForm form, HttpServletRequest request,
		 		 		 HttpServletResponse response) throws IOException, ServletException {
		 	    
		 		 HttpSession session = request.getSession(true);
		 	
		 		 HashMap actions = new HashMap();
		 		 actions.put("score","/editScorecardDisplayAction.wss");
		 		 actions.put("list","/showstage.wss");
		 		 actions.put("issue","/issuelist.wss");
		 		 actions.put("history","/viewScorecardHistoryAction.wss");
		 		 actions.put("respondent","/createScorecardDisplayAction.wss");
		 		 actions.put("qbrlist","/stageViewQBR.wss");
		 		 actions.put("qbrscore","/editQbrSelfScorecardAction.wss");
		 		 
		 		 ScorecardFormBean 		 	 fBean = (ScorecardFormBean)form;
		 		 ScorecardBL		 		 object=  new ScorecardBL();
		 		 SetMetDocumentStageObject document =  new SetMetDocumentStageObject();
		 		 document.setProjectID(fBean.getProj());
		 		 	 		 
		 		 String workflowType = request.getParameter("wf_type");
		 		 if(workflowType==null || "null".equalsIgnoreCase(workflowType)){
		 		 	workflowType="setmet";
		 		 }
		 		 String overallAvg = "0"; 
		 		
		 		 
		 		 String url = "?wf_type="+workflowType+"&workflowID="+fBean.getWorkflowID()+"&proj="+fBean.getProj()+"&tc="+fBean.getTc()+"&mid="+request.getParameter("mid");
		 		 
		 		 document.setWorkflowID(fBean.getWorkflowID());
		         document.setClientName(company);
		 		 document.setLastUsr(loggedUser);
		 		 document.setWorkflowType(workflowType);
		 		 
		 		 
		 		 
		 		 
		 		 logger.debug("the value of the requestor is"+loggedUser);
		 		 logger.debug("The company name is"+company);
		 		 
		 		 String workflowName = object.getWorkflowName(fBean.getProj(),fBean.getWorkflowID());
		 		 request.setAttribute("WORKFLOW_NAME",workflowName);
		 		 ScorecardDAO dao = new ScorecardDAO();
		 		 
		 		 
		 		 if(workflowType == null || (workflowType!=null && "SETMET".equalsIgnoreCase(workflowType))){	 		 		 		 
		 		 	 overallAvg = String.valueOf(dao.getOverallClientAverage(fBean.getProj(),fBean.getWorkflowID(),company));		 		 	 		 		 		 
		 		 }else{
		 		 	 //overallAvg = String.valueOf(dao.getOverallClientAverage(fBean.getProj(),fBean.getWorkflowID(),company));
		 		 	 overallAvg = String.valueOf(dao.getAverage(document));
		 		 }

		 		 request.setAttribute("OVERALL_AVG",overallAvg);
		 		 
		 		 boolean createMatrix = false;
		 		 
		 		 if("qbrlist".equalsIgnoreCase(fBean.getAction()) ||"qbrscore".equalsIgnoreCase(fBean.getAction()) ||"history".equalsIgnoreCase(fBean.getAction()) || "issue".equalsIgnoreCase(fBean.getAction())|| "score".equalsIgnoreCase(fBean.getAction())|| "list".equalsIgnoreCase(fBean.getAction())|| "respondent".equalsIgnoreCase(fBean.getAction())){
		 		 	   session.setAttribute("DOCUMENT_PAGE_DATA",fBean);
		 		 	   String address = ((String)actions.get(fBean.getAction()))+url;
		 		       return new ActionForward(address);  
		 		 }
		 		 
		 		 
		 		 
		 		 if(fBean.getAction()==null && object.isValidStage(fBean.getProj(),fBean.getWorkflowID())){
		 		 		  createMatrix = true;
		 		 	  try{
		 		 	  			
		 		 	  			document = object.getWorkflowObject(document,createMatrix);
		 		 	  			
		 		 	  			ArrayList scorecard = new ArrayList();
		 		 	  			scorecard = document.getScorecard();
		 		 	  			fBean.setScorers(scorecard);

		 		 	  			Scorecard objScorecard = (Scorecard)scorecard.get(0);
		 		 	  			if("CLIENT LEVEL".equalsIgnoreCase(objScorecard.getScorerID())){
		 		 	  				  request.setAttribute("SCORING_LEVEL","CLIENT");
		 		 	  			}else{
		 		 	  				  request.setAttribute("SCORING_LEVEL","ATTENDEE");
		 		 	  			}
		 		 	  			
		 		 	  			OrderedMap questionsMap = objScorecard.getQestRating();
		 		 	  			
		 		 	  		    if(objScorecard.getMeetingDate()!=null){
		 		 	  		    	String d = objScorecard.getMeetingDate();
		 		 	  		    	fBean.setYr(d.substring(0,4));
		 		 	  		    	fBean.setMonth(d.substring(5,7));
		 		 	  		    	fBean.setDate(d.substring(8,10));
		 		 	  		    }else{
		 		 	  		    	  Calendar cal = Calendar.getInstance();
		 		 	  		    	  cal.setTime(new java.util.Date(System.currentTimeMillis()));
		 		 	  		    	  fBean.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));
		 		 	  		    	  fBean.setYr(String.valueOf(cal.get(Calendar.YEAR)));
		 		 	  		    	  fBean.setDate(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
		 		 	  		    }
	 		 	  			
		 		 	  			
		 		 	  			ArrayList questions = questionsMap.getKeys(questionsMap);
		 		 	  			fBean.setQuestions(questions);
		 		 	  			fBean.setOverallcomments("");
		 		 	  			fBean.setSummaryreport("");
		 		 	  			fBean.setReporttitle("");
		 		 	  			
		 		 	  			// The StageID stored is ther prepare stage ID
		 		 	  			
		 		 	  			
		 		 	  			document.setStageID(MiscUtils.getWorkflowStageID(document.getProjectID(),document.getWorkflowID(),WorkflowConstants.PREPARE));
		 		 	  			
		 		 	  			boolean changes = object.trackWorkflowStageChange(document);
		 		 	  			if(!changes)
		 		 	  				throw new WorkflowException ("Problem in recording the workflowstage history");
		 		 	  }catch(WorkflowException we){
		 		 	  	logger.debug("Exception in setmetscorecardaction",we);
		 		 	  }
		 		 		 
		 		 }else{
		 		    ArrayList scorecard  = new ArrayList();
		 		    document = object.getWorkflowObject(document,createMatrix);
		 		    scorecard = document.getScorecard();
		 		    ScorecardFormBean sessBean = (ScorecardFormBean)session.getAttribute("DOCUMENT_PAGE_DATA");
		 		   		 		    
		 		   if(sessBean==null|| "Documentsetmet".equalsIgnoreCase(fBean.getAction()))
		 		    	  sessBean = new ScorecardFormBean();
		 		   
		 		    Scorecard objScorecard = null;
		 		    objScorecard = new Scorecard();
		 		    if(scorecard.size()>0){
		 		    	objScorecard = (Scorecard)scorecard.get(0);
		 		    	if("CLIENT LEVEL".equalsIgnoreCase(objScorecard.getScoredBy())){
		 	  				  request.setAttribute("SCORING_LEVEL","CLIENT");
		 	  			}else{
		 	  				  request.setAttribute("SCORING_LEVEL","ATTENDEE");
		 	  			}	
		 		    }
		 		    
	 	  			
	  			if(objScorecard.getMeetingDate()!=null){
	  				String d = objScorecard.getMeetingDate();
	  				fBean.setYr(d.substring(0,4));
	  				if(d.substring(5,7).startsWith("0"))
	  					fBean.setMonth(d.substring(6,7));
	  				else
	  					fBean.setMonth(d.substring(5,7));
	  				
	  				if(d.substring(8,10).startsWith("0"))
	  					fBean.setDate(d.substring(9,10));
	  				else
	  					fBean.setDate(d.substring(8,10));
  				
	  			}else{
	  				if(sessBean.getMonth()!=null && sessBean.getMonth().length()>0){
	  					fBean.setMonth(sessBean.getMonth());
	  					fBean.setYr(sessBean.getYr());
	  					fBean.setDate(sessBean.getDate());
	  				}else{
	  					  Calendar cal = Calendar.getInstance();
	 	  		    	  cal.setTime(new java.util.Date(System.currentTimeMillis()));
	 	  		    	  fBean.setMonth(String.valueOf(cal.get(Calendar.MONTH)+1));
	 	  		    	  fBean.setYr(String.valueOf(cal.get(Calendar.YEAR)));
	 	  		    	  fBean.setDate(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
	 	  		
	  				}
	  				
	  			}
		 		    fBean.setScorers(scorecard);
		 		   
		 		    if(objScorecard.getOverallcomment()!=null &&(objScorecard.getOverallcomment()).length()>0)
		 		    	fBean.setOverallcomments(objScorecard.getOverallcomment());
		 		    else
		 		    	fBean.setOverallcomments(sessBean.getOverallcomments());
		 		    
		 		   if(objScorecard.getSummaryreport()!=null &&(objScorecard.getSummaryreport()).length()>0)
		 		   		fBean.setSummaryreport(objScorecard.getSummaryreport());
		 		   else
		 		   		fBean.setSummaryreport(sessBean.getSummaryreport());
		 		 
		 		   if(objScorecard.getReporttitle()!=null &&(objScorecard.getReporttitle()).length()>0)
		 		   		fBean.setReporttitle(objScorecard.getReporttitle());
		 		   else
		 		   		fBean.setReporttitle(sessBean.getReporttitle());
		 		   
		 		   if(objScorecard.getOverallrating()!=null &&(objScorecard.getOverallrating()).length()>0)
		 		   		fBean.setOverallrating(objScorecard.getOverallrating());
		 		   else
	 		    		fBean.setOverallrating(sessBean.getOverallrating());
		 		 }
		 		 
			 		if(workflowType == null || (workflowType!=null && "SETMET".equalsIgnoreCase(workflowType))){
			 			boolean canAccess = canAccess(request, "SCORECARD", "EDIT");
			 			if(canAccess){
			 				 request.setAttribute("ACCESS_DENIED","false");
			 			}else{
			 				request.setAttribute("ACCESS_DENIED","true");
			 			}
			 		    return mapping.findForward("attendees");
			 		}else{
			 			return mapping.findForward("selfqbr");
			 		}

		 }

}
