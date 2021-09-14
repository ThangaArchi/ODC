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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.prepare;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IssueList extends WorkflowObject {
	
	private static Log logger = WorkflowLogger.getLogger(IssueList.class);
       private String issueTitle 	= null;
       private String issueOwner	= null;
       private String issueStatus	= null;       
       private String dateOpened		= null;
       private String workflowStatus 		= null;
       
   	private ArrayList IssueTitlelist = null;
	private ArrayList IssueOwnerlist = null;
	private ArrayList IssueStatuslist = null;
	private ArrayList DateOpenedlist = null;
	private ArrayList WorkflowStatuslist = null;
       
       
	/**
	 * @param issueTitle
	 * @param issueOwner
	 * @param issueStatus
	 * @param dateOpened
	 * @param workflowStatus
	 */
	public IssueList(String issueTitle, String issueOwner, String issueStatus,
			String dateOpened, String workflowStatus) {
	
		this.issueTitle = issueTitle;
		this.issueOwner = issueOwner;
		this.issueStatus = issueStatus;
		this.dateOpened = dateOpened;
		this.workflowStatus = workflowStatus;
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
	
	/**
	 * @return Returns the dateOpenedlist.
	 */
	public ArrayList getDateOpenedlist() {
		return DateOpenedlist;
	}
	/**
	 * @param dateOpenedlist The dateOpenedlist to set.
	 */
	public void setDateOpenedlist(ArrayList dateOpenedlist) {
		DateOpenedlist = dateOpenedlist;
	}
	/**
	 * @return Returns the issueOwnerlist.
	 */
	public ArrayList getIssueOwnerlist() {
		return IssueOwnerlist;
	}
	/**
	 * @param issueOwnerlist The issueOwnerlist to set.
	 */
	public void setIssueOwnerlist(ArrayList issueOwnerlist) {
		IssueOwnerlist = issueOwnerlist;
	}
	/**
	 * @return Returns the issueStatuslist.
	 */
	public ArrayList getIssueStatuslist() {
		return IssueStatuslist;
	}
	/**
	 * @param issueStatuslist The issueStatuslist to set.
	 */
	public void setIssueStatuslist(ArrayList issueStatuslist) {
		IssueStatuslist = issueStatuslist;
	}
	/**
	 * @return Returns the issueTitlelist.
	 */
	public ArrayList getIssueTitlelist() {
		return IssueTitlelist;
	}
	/**
	 * @param issueTitlelist The issueTitlelist to set.
	 */
	public void setIssueTitlelist(ArrayList issueTitlelist) {
		IssueTitlelist = issueTitlelist;
	}
	/**
	 * @return Returns the workflowStatuslist.
	 */
	public ArrayList getWorkflowStatuslist() {
		return WorkflowStatuslist;
	}
	/**
	 * @param workflowStatuslist The workflowStatuslist to set.
	 */
	public void setWorkflowStatuslist(ArrayList workflowStatuslist) {
		WorkflowStatuslist = workflowStatuslist;
	}
   }
