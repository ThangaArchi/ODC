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

package oem.edge.ets.fe.workflow.newissue;


import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;



/**
 * Class       : NewIssueFormBean
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewIssueFormBean extends WorkflowForm {

	private static Log logger = WorkflowLogger.getLogger(NewIssueFormBean.class);
	private WorkflowObject workflowObject = new NewIssueVO();

	/**
	 * @param workflowObject The workflowObject to set.
	 */
	public void setWorkflowObject(WorkflowObject workflowObject) {
		this.workflowObject = workflowObject;
	}

	/**
	 * @return Returns the workflowObject.
	 */
	public WorkflowObject getWorkflowObject() {
		return workflowObject;
	}


	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowForm#reset()
	 */
	public void reset() {
		((NewIssueVO)workflowObject).reset();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		
		return null;
	
	}
}
