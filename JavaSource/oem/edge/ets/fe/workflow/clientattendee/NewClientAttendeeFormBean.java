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

package oem.edge.ets.fe.workflow.clientattendee;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;


/**
 * Class       : NewClientAttendeeFormBean
 * Package     : oem.edge.ets.fe.workflow.clientattendee
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewClientAttendeeFormBean extends WorkflowForm

{
	WorkflowObject workflowObject = new NewClientAttendeeVO();
	
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
		this.workflowObject = (NewClientAttendeeVO)workflowObject;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
        ((NewClientAttendeeVO)workflowObject).reset();
        

    }
}
