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


import java.util.ArrayList;
import oem.edge.ets.fe.workflow.core.WorkflowForm;

/**
 *  @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowListForm extends WorkflowForm {
	
	
	private ArrayList workflowList = null;
	private String projectID 	   = null;
	private String tabID		   = null;
	private ArrayList qbrWorkflowList = null; 	//added for 7.1.1 by KP
	private ArrayList saWorkflowList = null; 	//added for 7.1.1 by KP
	
	/**
	 * @return Returns the qbrWorkflowList.
	 */
	public ArrayList getQbrWorkflowList() {
		return qbrWorkflowList;
	}
	/**
	 * @param qbrWorkflowList The qbrWorkflowList to set.
	 */
	public void setQbrWorkflowList(ArrayList qbrWorkflowList) {
		this.qbrWorkflowList = qbrWorkflowList;
	}
	
	
	public ArrayList getSaWorkflowList() {
		return saWorkflowList;
	}
	public void setSaWorkflowList(ArrayList saWorkflowList) {
		this.saWorkflowList = saWorkflowList;
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
	 * @return Returns the tabID.
	 */
	public String getTabID() {
		return tabID;
	}
	/**
	 * @param tabID The tabID to set.
	 */
	public void setTabID(String tabID) {
		this.tabID = tabID;
	}
	public WorkflowListForm(){
		workflowList = new ArrayList();
	}
	
	public void setWorkflowList(ArrayList workflowList){
		this.workflowList = workflowList;
	}
	
	public ArrayList getWorkflowList(){
		return workflowList;
	}

	public void reset(){
		workflowList=null;
	}
}
