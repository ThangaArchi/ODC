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
 * Created on Sep 4, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.core;

import org.apache.struts.action.ActionForm;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class WorkflowForm extends ActionForm {
	 protected String action  				 	 = null;
	 protected WorkflowObject workflowObject	 = null;
	 protected String workflowStageId  	    	 = null;
	 protected String workflowID			 	 = null;
	 protected String proj						 = null;
	 protected String tc						 = null;
	
	 //Added by KP
	 private Object preloadBean = null;
	 public Object getPreloadBean() {
		return preloadBean;
	 }
	 public void setPreloadBean(Object preloadBean) {
		this.preloadBean = preloadBean;
	 }
	 //End of KP's addition
	 
	 
	/**
	 * @return Returns the proj.
	 */
	public String getProj() {
		return proj;
	}
	/**
	 * @param proj The proj to set.
	 */
	public void setProj(String proj) {
		this.proj = proj;
	}
	/**
	 * @return Returns the tc.
	 */
	public String getTc() {
		return tc;
	}
	/**
	 * @param tc The tc to set.
	 */
	public void setTc(String tc) {
		this.tc = tc;
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
	 * @return Returns the workflowObject.
	 */
	public WorkflowObject getWorkflowObject() {
		return workflowObject;
	}
	/**
	 * @param workflowObject The workflowObject to set.
	 */
	public void setWorkflowObject(WorkflowObject workflowObject) {
		this.workflowObject = workflowObject;
	}
	/**
	 * @return Returns the workflowStageId.
	 */
	public String getWorkflowStageId() {
		return workflowStageId;
	}
	/**
	 * @param workflowStageId The workflowStageId to set.
	 */
	public void setWorkflowStageId(String workflowStageId) {
		this.workflowStageId = workflowStageId;
	}
		/**
	     * Sets the action which is used in the navigation flow
	     *
	     * @param   action  String
	     */
	    public void setAction(String action){
	        this.action     = action;
	    }

	    /**
	     * Returns the action which is used in the navigation flow
	     *
	     * @return  String
	     */
	    public String getAction(){
	        return action;
	    }

		/**
		 * reset all the data
		 *
		 */
		public void reset(){
			action		= null;
		}
}
