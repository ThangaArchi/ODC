/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
 * Created on Sep 11, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.core;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WorkflowStage extends WorkflowObject {
     protected String stageID = null;
     protected String workflowID = null;
     
     protected String workflowStatus = null;
     protected String className		 = null;
     protected String workflowType   = null;
     
     public abstract String getStageName();
     public abstract String getNextStage();
     public abstract String getWorkflowType();
     
     /**
      * Set the workflow type.
      * @param type
      */
     
     public void setWorkflowType(String type){
  	   workflowType = type;
     }
	
	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}
	public String getStageID() {
		return stageID;
	}
	/**
	 * @param stageID The stageID to set.
	 */
	public void setStageID(String stageID) {
		this.stageID = stageID;
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
	 * @return Returns the className.
	 */
	
}
