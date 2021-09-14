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


package oem.edge.ets.fe.workflow.notification;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;

//
/**
 * Class       : WorkflowNotificationParams
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class WorkflowNotificationParams extends NotificationParams{
	private static Log logger = WorkflowLogger.getLogger(WorkflowNotificationParams.class);
	private String workflowID = null;
	private String wf_type_text = null;
	private String wf_type = null;
	
	
	
	public String getWf_type() {
		return wf_type;
	}
	public void setWf_type(String wf_type) {
		this.wf_type = wf_type;
	}
	public String getWf_type_text() {
		return wf_type_text;
	}
	public void setWf_type_text(String wf_type_text) {
		this.wf_type_text = wf_type_text;
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

