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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : WorkflowEventDetailsBean
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class WorkflowEventDetailsBean extends WorkflowForm

{
	private static Log logger = WorkflowLogger.getLogger(WorkflowEventDetailsBean.class);
	private WorkflowObject workflowObject = new WorkflowEventDetailsVO(); 
    
	
	
	/**
	 * @return Returns the workflowObject.
	 */
	public WorkflowObject getWorkflowObject() {
		return workflowObject;
	}
	/**
	 * @param workflowObject The workflowObject to set.
	 */
	public void setWorkflowObject(WorkflowObject workflowObject) {
		this.workflowObject = workflowObject;
	}
    public void reset() {
    	((WorkflowEventDetailsVO)(workflowObject)).reset();
    }
  
}
