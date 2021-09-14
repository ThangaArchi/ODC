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

package oem.edge.ets.fe.workflow.notifypopup;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;



/**
 * Class       : NotifyPopupFormBean
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NotifyPopupFormBean extends WorkflowForm

{
	private static Log logger = WorkflowLogger.getLogger(NotifyPopupFormBean.class);	 
	private NotifyPopupVO workflowObject = new NotifyPopupVO();
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowForm#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		workflowObject.reset();
		super.reset();
	}
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
		this.workflowObject = (NotifyPopupVO)workflowObject;
	}
}
