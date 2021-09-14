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

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : Row
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class Row extends WorkflowObject {

	private static Log logger = WorkflowLogger.getLogger(Row.class);
	private String styleID = null;
private String issueID = null;
private String issueTitle = null;
private String issueOwner= null;
private String issueStatus = null;
private String dateOpened = null;
private String workflowStatus = null;
private String workflowType = null;
private String isAlreadyPresent = null;

Row()
{
    //issueOwner = new ArrayList();
}

/**
 * @return Returns the isAlreadyPresent.
 */
public String getIsAlreadyPresent() {
    return isAlreadyPresent;
}
/**
 * @param isAlreadyPresent The isAlreadyPresent to set.
 */
public void setIsAlreadyPresent(String isAlreadyPresent) {
    this.isAlreadyPresent = isAlreadyPresent;
}
/**
 * @return Returns the dateOpened.
 */
public String getDateOpened() {
	return dateOpened;
}
/**
 * @param dateOpened The dateOpened to set.
 */
public void setDateOpened(String dateOpened) {
	this.dateOpened = dateOpened;
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
/**
 * @return Returns the issueOwner.
 */
public String getIssueOwner() {
	return issueOwner;
}
/**
 * @param issueOwner The issueOwner to set.
 */
public void setIssueOwner(String issueOwner) {
	this.issueOwner = issueOwner;
}
/**
 * @return Returns the issueStatus.
 */
public String getIssueStatus() {
	return issueStatus;
}
/**
 * @param issueStatus The issueStatus to set.
 */
public void setIssueStatus(String issueStatus) {
	this.issueStatus = issueStatus;
}
/**
 * @return Returns the issueTitle.
 */
public String getIssueTitle() {
	return issueTitle;
}
/**
 * @param issueTitle The issueTitle to set.
 */
public void setIssueTitle(String issueTitle) {
	this.issueTitle = issueTitle;
}
/**
 * @return Returns the styleID.
 */
public String getStyleID() {
	return styleID;
}
/**
 * @param styleID The styleID to set.
 */
public void setStyleID(String styleID) {
	this.styleID = styleID;
}
/**
 * @return Returns the workflowStatus.
 */
public String getWorkflowStatus() {
	return workflowStatus;
}
/**
 * @param workflowStatus The workflowStatus to set.
 */
public void setWorkflowStatus(String workflowStatus) {
	this.workflowStatus = workflowStatus;
}
/**
 * @return Returns the workflowType.
 */
public String getWorkflowType() {
	return workflowType;
}
/**
 * @param workflowType The workflowType to set.
 */
public void setWorkflowType(String workflowType) {
	this.workflowType = workflowType;
}
}
