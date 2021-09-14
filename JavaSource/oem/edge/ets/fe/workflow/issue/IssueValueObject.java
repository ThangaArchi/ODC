/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
/*
 * Created on Sep 6, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.issue;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IssueValueObject extends WorkflowObject{

	private String IssueTitle = null;
	//private String IssueOwner = null;
	private String IssueStatus = null;
	private String DateOpened = null;
	private String WorkflowStatus = null;
	
	private ArrayList IssueFocalpoint = null;
	private ArrayList IssueType = null;
	private ArrayList IssueOwner = null;
	private ArrayList Notification = null;
	
		
	/**
	 * @return Returns the dateOpened.
	 */
	public String getDateOpened() {
		return DateOpened;
	}
	/**
	 * @param dateOpened The dateOpened to set.
	 */
	public void setDateOpened(String dateOpened) {
		DateOpened = dateOpened;
	}
	/**
	 * @return Returns the issueFocalpoint.
	 */
	public ArrayList getIssueFocalpoint() {
		return IssueFocalpoint;
	}
	/**
	 * @param issueFocalpoint The issueFocalpoint to set.
	 */
	public void setIssueFocalpoint(ArrayList issueFocalpoint) {
		IssueFocalpoint = issueFocalpoint;
	}
	/**
	 * @return Returns the issueOwner.
	 */
	public ArrayList getIssueOwner() {
		return IssueOwner;
	}
	/**
	 * @param issueOwner The issueOwner to set.
	 */
	public void setIssueOwner(ArrayList issueOwner) {
		IssueOwner = issueOwner;
	}
	/**
	 * @return Returns the issueStatus.
	 */
	public String getIssueStatus() {
		return IssueStatus;
	}
	/**
	 * @param issueStatus The issueStatus to set.
	 */
	public void setIssueStatus(String issueStatus) {
		IssueStatus = issueStatus;
	}
	/**
	 * @return Returns the issueTitle.
	 */
	public String getIssueTitle() {
		return IssueTitle;
	}
	/**
	 * @param issueTitle The issueTitle to set.
	 */
	public void setIssueTitle(String issueTitle) {
		IssueTitle = issueTitle;
	}
	/**
	 * @return Returns the issueType.
	 */
	public ArrayList getIssueType() {
		return IssueType;
	}
	/**
	 * @param issueType The issueType to set.
	 */
	public void setIssueType(ArrayList issueType) {
		IssueType = issueType;
	}
	/**
	 * @return Returns the notification.
	 */
	public ArrayList getNotification() {
		return Notification;
	}
	/**
	 * @param notification The notification to set.
	 */
	public void setNotification(ArrayList notification) {
		Notification = notification;
	}
	/**
	 * @return Returns the workflowStatus.
	 */
	public String getWorkflowStatus() {
		return WorkflowStatus;
	}
	/**
	 * @param workflowStatus The workflowStatus to set.
	 */
	public void setWorkflowStatus(String workflowStatus) {
		WorkflowStatus = workflowStatus;
	}
}
