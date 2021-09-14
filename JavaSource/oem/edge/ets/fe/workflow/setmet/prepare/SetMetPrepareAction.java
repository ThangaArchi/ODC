/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.prepare;

import java.util.ArrayList;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetPrepareAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		    System.out.println("***********After the continue button ****************");
		
		    SetMetPrepareForm  issueListForm = (SetMetPrepareForm) form;
		    	    		
			SetMetPrepareBL businessObj = new SetMetPrepareBL();
			
			 ArrayList arrayList   = businessObj.getpreviousIssueList(projectID);	
			 
			 
			 issueListForm.setList(arrayList) ;
			 System.out.println("*******Size of issueListForm.getList().size() the array list is ***********"+issueListForm.getList().size());
		    /*try{
		    	PropertyUtils.copyProperties(issueListForm,valobj);
		    }catch (Exception e){
		    	e.getMessage();
		    }*/
		   
		   // issueListForm.setIssueList(al);
		    return mapping.findForward("previousIssue");
		    
	}

}
