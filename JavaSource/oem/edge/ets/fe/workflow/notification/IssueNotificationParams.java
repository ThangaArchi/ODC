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
 * Class       : IssueNotificationParams
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class IssueNotificationParams extends WorkflowNotificationParams{
	private static Log logger = WorkflowLogger.getLogger(IssueNotificationParams.class);
	
	private String issueID = null;
	private String issue_owner = null;
	
	
	public String getIssue_owner() {
		return issue_owner;
	}
	public void setIssue_owner(String issue_owner) {
		this.issue_owner = issue_owner;
	}
/**
	 * @return Returns the issueID.
	 */
	public String getIssueID() {
		return issueID;
	}
	/**
	 * @param issueID The issueID to set.
	 */
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
}

