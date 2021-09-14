/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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
package oem.edge.ets.fe.workflow.setmet;

import java.util.ArrayList;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.util.MiscUtils;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowListAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

			WorkflowListForm  listForm = (WorkflowListForm) form;
			SetMetBL businessObj = new SetMetBL();

			boolean canAccess = canAccess(request,"SETMET","CREATE");


			if(canAccess)
				request.setAttribute("ACCESS_DENIED","false");
			else
				request.setAttribute("ACCESS_DENIED","true");


			ArrayList al  =  businessObj.getWorkflowList(projectID,"SETMET");//modified for 7.1.1 by KP
		    listForm.setWorkflowList(al);

		    //Support for QBR, 7.1.1
		    al = businessObj.getWorkflowList(projectID,"QBR");
		    listForm.setQbrWorkflowList(al);
		    
		    //Support for Self-Assessment, 7.1.1
		    al = businessObj.getWorkflowList(projectID,"SELF ASSESSMENT");
		    listForm.setSaWorkflowList(al);
		    
		    if(MiscUtils.getTc(projectID,"Self-Assessment").length()!=0)
		    	request.setAttribute("selfAssessmentTab", " ");

		    return mapping.findForward("workflowList");
	}

}
