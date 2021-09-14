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
 * Created on Nov 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.validate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.common.Validator;

import oem.edge.ets.fe.workflow.setmet.document.SetMetDocumentStageObject;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CompleteWorkflowAction extends WorkflowAction {

		 /* (non-Javadoc)
		  * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
		  */
		 public ActionForward executeWorkflow(ActionMapping mapping,
		 		 		 WorkflowForm form, HttpServletRequest request,
		 		 		 HttpServletResponse response) throws IOException, ServletException {
		 		 // TODO Auto-generated method stub
		 		 boolean completed = false;
		 		 String workflowID =(String) request.getParameter("workflowID");
		 		 String wf_type = request.getParameter("wf_type");
		 		 String authorized = "false";
		 		 
		 		if(!"SETMET".equalsIgnoreCase(wf_type)){
		 		        if("I".equalsIgnoreCase(ValidateDocumentStageDAO.getScorecardStatus(workflowID))){
		 		        	    String msg = "To close the workflow,complete the scorecard";
		 		        		return new ActionForward("/issuelist.wss?message="+msg+"&authorized="+authorized+"&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc+"&linkid=1k0000");	
		 		        }
		 		}
		 		 
		 		 
		 		 if(!isMemberOrVisitor(request)){
		 		 		     
		 		 		 		 authorized="true";
		 		 		 		 Validator validator = Validator.getInstance();
		 		 		 		 validator.validateDocumentStage(projectID,workflowID,WorkflowConstants.STAGECOMPLETED,loggedUser);
		 		 		 		 completed = true;
		 		 		 		 
		 		  		     
		 		 }else{
		 		 		  completed = false;
		 		 		  authorized = "false";
		 		 }
		 		 if(!completed)
		 		 		 return new ActionForward("/issuelist.wss?authorized="+authorized+"&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc);
		 		 else
		 		 {
		 		 	if("SETMET".equalsIgnoreCase(wf_type))
		 		 		 return new ActionForward("/showstage.wss?action=viewsetmet&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc+"&linkid=1k0000");
		 		 	else
		 		 		 return new ActionForward("/stageViewQBR.wss?wf_type="+wf_type+"&action=viewQBR&workflowID="+workflowID+"&proj="+projectID+"&tc="+tc+"&linkid=1k0000");
		 		 }
		 		 		 
		 }

}