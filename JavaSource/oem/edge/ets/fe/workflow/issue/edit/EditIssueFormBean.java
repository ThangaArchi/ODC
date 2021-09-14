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



package oem.edge.ets.fe.workflow.issue.edit;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionMapping;




/**
 * Class       : EditIssueFormBean
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Oct 10, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class EditIssueFormBean extends WorkflowForm

{
	private static Log logger = WorkflowLogger.getLogger(EditIssueFormBean.class);
    private WorkflowObject workflowObject = new EditIssueVO();

    /**
     * Get workflowObject
     * @return WorkflowObject
     */
    public WorkflowObject getWorkflowObject() {
    	if(workflowObject==null)
    		workflowObject = new EditIssueVO();
    	return workflowObject;
    }

    /**
     * Set workflowObject
     */
    public void setWorkflowObject(WorkflowObject w) {
        this.workflowObject = w;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

        // Reset values are provided as samples only. Change as appropriate.

        //((EditIssueVO)workflowObject).reset();

    }
    

}
