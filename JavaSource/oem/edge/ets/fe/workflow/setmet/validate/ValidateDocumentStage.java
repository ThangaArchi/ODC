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
 * Created on Nov 1, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.validate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import oem.edge.ets.fe.workflow.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.common.Validator;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValidateDocumentStage extends WorkflowAction {

	
	private static Log logger	=		WorkflowLogger.getLogger(ValidateDocumentStage.class);
		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		  * 
		  */
		 public ActionForward executeWorkflow(ActionMapping mapping,
		 		 		 WorkflowForm form, HttpServletRequest request,
		 		 		 HttpServletResponse response) throws IOException, ServletException {

		 		 String workflowID =(String) request.getParameter("workflowID");
		 		 String fromPage   = (String)request.getParameter("fromPage");
		 		 String workflowType = request.getParameter("wf_type");
		 		 ValidateObject vo = new ValidateObject();
		 		 ScorecardDAO dao= new ScorecardDAO();
		 		 
		 		 String date    = (String) request.getParameter("date");
 		 		 String month = (String) request.getParameter("month");
 		 		 String year  = (String) request.getParameter("yr");
 		 		 String currStage = ScorecardDAO.getWorkflowCurrentStage(projectID,workflowID);
 		 		 
 		 		 
 		 		 
 		 		 
 		 		 
 		 		 boolean canAccess =isMemberOrVisitor(request);
		 		 String authorized = "false";
		 		 String completed = "false";
		 		 int canPass		 		  = 0;
		 		 String message="";
 		 		 
		 		Date meetDate = null;
		 		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");                 
		 		String dt = null;
		 		 try{
		 		 		  if(date!=null && month!=null && year !=null){
		 		 		  	        dt  = month+"/"+date+"&"+year;
		 		 		  	        
		 		 		  	        meetDate = sdf.parse(month+"/"+date+"/"+year);
		 		 		  }
		 		 }catch(Exception formatException){

		 		 		  logger.error("Exception in parsing the date",formatException);
		 		 }

		 		 
		 		 
		 		 System.out.println("the value of the $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$dt is "+dt);
		 		 
		 		 if(!canAccess){
		 		 	 authorized = "true";
		 		 	 
		 		 }else{
  		 		 	 message="You are not authorized to move the workflow to the next stage";
			 		 request.setAttribute("MESSAGE",message);
			 		 request.setAttribute("ACCESS",authorized);
		 	 	 	 return mapping.findForward("failure");
		 		 	  
		 		 }
		 		  
 		 		 if(!WorkflowConstants.DOCUMENT.equalsIgnoreCase(currStage)){
 		 		 	
 		 		 	return new ActionForward("/issuelist.wss?WF_TYPE="+workflowType+"&authorized="+authorized+"&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc);
 		 		 }
 		 		  
 		 		                 
		 		 
		 		 vo.setComment(request.getParameter("overallcomments"));
		 		 vo.setReporttitle(request.getParameter("reporttitle"));
		 		 vo.setRating("0");
		 		 vo.setNxtDate(dt);
		 		 vo.setProjectID(projectID);
		 		 vo.setWorkflowID(workflowID);
		 		 vo.setSummary(request.getParameter("summaryreport"));
		 		 		 		 
		 		 Date currDate = new Date(System.currentTimeMillis());
		 		if(!MiscUtils.isValidDate(year,month,date)){
		 			
		 			 message="Invalid next meeting date";
			 		 request.setAttribute("MESSAGE",message);
			 		 request.setAttribute("ACCESS",authorized);
		 	 	 	 return mapping.findForward("failure");
		 		}
              if( (meetDate!=null && (currDate.compareTo(meetDate)==1))){
              		message="Next meeting date must be a future date";
              		request.setAttribute("MESSAGE",message);
              		request.setAttribute("ACCESS",authorized);
              		return mapping.findForward("failure");
              }
		 		
		 		if(!"SETMET".equalsIgnoreCase(wf_type) && vo.getComment()!=null && vo.getComment().length()>1024){
		 			 message="Overall Comments cannot exceed 1024 characters";
			 		 request.setAttribute("MESSAGE",message);
			 		 request.setAttribute("ACCESS",authorized);
		 	 	 	 return mapping.findForward("failure");
		 		}
		 		
		 		if( !"SETMET".equalsIgnoreCase(wf_type) && vo.getReporttitle()!=null && vo.getReporttitle().length()>=255){
 		 			 message="Report Title cannot exceed 255 characters";
			 		 request.setAttribute("MESSAGE",message);
			 		 request.setAttribute("ACCESS",authorized);
		 	 	 	 return mapping.findForward("failure");
		 		}
		 		if(!"SETMET".equalsIgnoreCase(wf_type) && vo.getSummary()!=null && vo.getSummary().length()>1024){
 		 			message= workflowType + " Summary cannot exceed 1024 characters";
 			 		request.setAttribute("MESSAGE",message);
			 	    request.setAttribute("ACCESS",authorized);
		 	 	 	return mapping.findForward("failure");
		 		}
		 				 				 		 
		 		 if(!canAccess){
		 		 		 		 authorized="true";
		 		 		 		 Validator validator = Validator.getInstance();
		 		 		 		 canPass = validator.validateDocumentStage(projectID,workflowID,WorkflowConstants.VALIDATE,loggedUser);
		 		 		 		 switch(canPass){
		 		 		 		     case 1: //message="Document stage issues are yet to be accepted";
											 canPass= 3; // This Check is not reqd..So directly assigning canPass = 3.
		 		 		 		             break;
		 		 		 		     case 2: message="Document stage scorecards are yet to be completed";
		 		 		 		     		 break;
		 		 		 		     case 0: message="Document stage scorecards and issues are yet to be completed to proceed to next stage";
		 		 		 		             break;
		 		 		 		     case 3:
		 		 		 		             break; 	
		 		 		 		     default:message = "Atleast one issue has be to recorded and assigned";
		 		 		 		 }
		 		 }else{
		 		 		    authorized="false";
		 		 }
		 		 
		 		 
		 		 
		 		 
		 		 request.setAttribute("MESSAGE",message);
		 		 request.setAttribute("ACCESS",authorized);
		 		 
		 		 System.out.println("the valie of canPass is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+canPass);
		 		 
		 		 if(canPass==3){
		 		 	
		 		 		if(dt!=null){
		 		 			
	 		 		      if(!ValidateDocumentStageDAO.updateNextMeetingDate(vo)){
		 		 	 	 	 	message="Problem in updating the next meeting date";
		 		 	 	 	 	return mapping.findForward("failure");		 		 	       
		 		 	 	 	}
		 		 		
		 		 		}
		 		 				 		 		 		 	 
		 		 	     if (ValidateDocumentStageDAO.insertAverageScores(projectID,workflowID,loggedUser)){
		 		 	     	System.out.println("the valie of canPass is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& CanPass is here");	     	        
		 		 	     	        if(ValidateDocumentStageDAO.getCurrentStage(projectID,workflowID)){
		 		 	     	        	ValidateDocumentStageDAO.generateStageID(projectID,workflowID,loggedUser);		 		 	     	        	
		 		 	     	        }		 		 	     	
		 		 	     	     	return new ActionForward("/issuelist.wss?WF_TYPE="+workflowType+"&message="+message+"&authorized="+authorized+"&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc);
		 		 	     }
		 		 	     else{
		 		 	     	message="Problem in Averaging the scores";
		 		 	     	return mapping.findForward("failure");		 	
		 		 	     	}
		 		 }
	 		 		   return mapping.findForward("failure");
		 }

}