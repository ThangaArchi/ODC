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
 * Created on Nov 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowMatrixObject extends WorkflowObject{

	private String[] workspaceslist = null;
	private String[] brandlist = null;
	private String[] processlist = null;
	private String[] businesssectorlist = null;
	private String[] scesectorlist = null;
	
	public WorkflowMatrixObject(){
		workspaceslist = new String[]{"All Values"};
		brandlist= new String[]{"All Values"};
		processlist= new String[]{"All Values"};
		businesssectorlist= new String[]{"All Values"};
		scesectorlist= new String[]{"All Values"};
	}
	/**
	 * @return Returns the brandlist.
	 */
	public String[] getBrandlist() {
		return brandlist;
	}
	/**
	 * @param brandlist The brandlist to set.
	 */
	public void setBrandlist(String[] brandlist) {
		this.brandlist = brandlist;
	}
	/**
	 * @return Returns the businesssectorlist.
	 */
	public String[] getBusinesssectorlist() {
		return businesssectorlist;
	}
	/**
	 * @param businesssectorlist The businesssectorlist to set.
	 */
	public void setBusinesssectorlist(String[] businesssectorlist) {
		this.businesssectorlist = businesssectorlist;
	}
	/**
	 * @return Returns the processlist.
	 */
	public String[] getProcesslist() {
		return processlist;
	}
	/**
	 * @param processlist The processlist to set.
	 */
	public void setProcesslist(String[] processlist) {
		this.processlist = processlist;
	}
	/**
	 * @return Returns the scesectorlist.
	 */
	public String[] getScesectorlist() {
		return scesectorlist;
	}
	/**
	 * @param scesectorlist The scesectorlist to set.
	 */
	public void setScesectorlist(String[] scesectorlist) {
		this.scesectorlist = scesectorlist;
	}
	/**
	 * @return Returns the workspaceslist.
	 */
	public String[] getWorkspaceslist() {
		return workspaceslist;
	}
	/**
	 * @param workspaceslist The workspaceslist to set.
	 */
	public void setWorkspaceslist(String[] workspaceslist) {
		this.workspaceslist = workspaceslist;
	}
}
