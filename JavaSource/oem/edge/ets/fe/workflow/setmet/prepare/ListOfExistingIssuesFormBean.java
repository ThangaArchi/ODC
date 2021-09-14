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

package oem.edge.ets.fe.workflow.setmet.prepare;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : ListOfExistingIssuesFormBean
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class ListOfExistingIssuesFormBean extends WorkflowForm

{
	private static Log logger = WorkflowLogger.getLogger(ListOfExistingIssuesFormBean.class);
    private WorkflowObject workflowObject = new ListOfExistingIssuesVO();

    /**
     * Get workflowObject
     */
    public WorkflowObject getWorkflowObject() {
        return workflowObject;
    }

    /**
     * Set workflowObject
     * @param <code>WorkflowObject</code>
     */
    public void setWorkflowObject(WorkflowObject w) {
        this.workflowObject = (ListOfExistingIssuesVO)w;
    }

    
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        // Reset values are provided as samples only. Change as appropriate.

        ((ListOfExistingIssuesVO)(workflowObject)).reset();

    }
}
