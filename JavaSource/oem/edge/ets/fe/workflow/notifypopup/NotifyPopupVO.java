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

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : NotifyPopupVO
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NotifyPopupVO extends WorkflowObject {

	private static Log logger = WorkflowLogger.getLogger(NotifyPopupVO.class);	
	private String comments = null;

    private String submitButton = null;

    private String cancelButton = null;

    private String[] peopleToBeNotified = null;
    
    private String workflowID = null;
    
    /**
     * Get comments
     * @return String
     */
    public String getComments() {
        return comments;
    }

    /**
     * Set comments
     * @param <code>String</code>
     */
    public void setComments(String c) {
        this.comments = c;
    }

    /**
     * Get submitButton
     * @return String
     */
    public String getSubmitButton() {
        return submitButton;
    }

    /**
     * Set submitButton
     * @param <code>String</code>
     */
    public void setSubmitButton(String s) {
        this.submitButton = s;
    }

    /**
     * Get cancelButton
     * @return String
     */
    public String getCancelButton() {
        return cancelButton;
    }

    /**
     * Set cancelButton
     * @param <code>String</code>
     */
    public void setCancelButton(String c) {
        this.cancelButton = c;
    }

    /**
     * Get peopleToBeNotified
     * @return String[]
     */
    public String[] getPeopleToBeNotified() {
        return peopleToBeNotified;
    }

    /**
     * Set peopleToBeNotified
     * @param <code>String[]</code>
     */
    public void setPeopleToBeNotified(String[] p) {
        this.peopleToBeNotified = p;
    }

    public void reset() {

        comments = null;
        submitButton = null;
        cancelButton = null;
        peopleToBeNotified = null;
        workflowID =null;
    }

  

	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	/**
	 * @param workflowID The workflowID to set.
	 */
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
}
