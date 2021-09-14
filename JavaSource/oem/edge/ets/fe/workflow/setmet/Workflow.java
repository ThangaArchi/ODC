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

/*
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 *  @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Workflow extends WorkflowObject {
       private String projectID 	= null;
       private String workflowName	= null;
       private String workflowID	= null;       
       private String quarter		= null;
       private String status 		= null;
       private String meetingDate   = null;
       private String workflowOwner =null;
       
      /* public Workflow(String a,String b,String c,String d){
       	workflowName = a;
       	quarter      = b;
       	meetingDate  = c;
       	status 		 = d;
       }*/
       
 


	/**
	 * @return Returns the meetingDate.
	 */
	public String getMeetingDate() {
		return meetingDate;
	}
	/**
	 * @param meetingDate The meetingDate to set.
	 */
	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the quarter.
	 */
	public String getQuarter() {
		return quarter;
	}
	/**
	 * @param quarter The quarter to set.
	 */
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
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
	/**
	 * @return Returns the workflowName.
	 */
	public String getWorkflowName() {
		return workflowName;
	}
	/**
	 * @param workflowName The workflowName to set.
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	/**
	 * @return Returns the workflowOwner.
	 */
	public String getWorkflowOwner() {
		return workflowOwner;
	}
	/**
	 * @param workflowOwner The workflowOwner to set.
	 */
	public void setWorkflowOwner(String workflowOwner) {
		this.workflowOwner = workflowOwner;
	}
   }
