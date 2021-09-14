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
package oem.edge.ets.fe.workflow.setmet.prepare;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetPrepareValueObject extends WorkflowObject{
	private String issueTitle = null;
	private String issueOwner = null;
	private String issueStatus = null;
	private String dateOpened = null;
	private String workflowStatus = null;
	
	
	
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
}
