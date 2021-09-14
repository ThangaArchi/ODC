/*
 * Created on Mar 23, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.vo;

import oem.edge.ed.odc.utils.ValueObject;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProjectWithServerVO extends ValueObject{
	
	private String projectId = "";
	private String projectName = "";
	private String serverInstanceId = "";
	private String status = "";
	private String isActive_InProject = "";
	private String lastTimeStamp_InProject = "";

	private String serverName = "";
	private String gridPath = "";
	private String isActive_InServer = "";
	private String lastTimeStamp_InServer = "";

	private String commitStatus = "false";

	/**
	 * @return
	 */
	public String getProjectId() {
		return projectId;
	}


	/**
	 * @return
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return
	 */
	public String getServerInstanceId() {
		return serverInstanceId;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param string
	 */
	public void setProjectId(String string) {
		projectId = string;
	}

	/**
	 * @param string
	 */
	public void setProjectName(String string) {
		projectName = string;
	}

	/**
	 * @param string
	 */
	public void setServerInstanceId(String string) {
		serverInstanceId = string;
	}

	/**
	 * @param string
	 */
	public void setStatus(String string) {
		status = string;
	}






//////////////////////////////////////////////////////////////

	/**
	 * @return
	 */
	public String getGridPath() {
		return gridPath;
	}

	/**
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param string
	 */
	public void setGridPath(String string) {
		gridPath = string;
	}

	/**
	 * @param string
	 */
	public void setServerName(String string) {
		serverName = string;
	}

	/**
	 * @return
	 */
	public String getIsActive_InProject() {
		return isActive_InProject;
	}

	/**
	 * @return
	 */
	public String getIsActive_InServer() {
		return isActive_InServer;
	}

	/**
	 * @return
	 */
	public String getLastTimeStamp_InProject() {
		return lastTimeStamp_InProject;
	}

	/**
	 * @return
	 */
	public String getLastTimeStamp_InServer() {
		return lastTimeStamp_InServer;
	}

	/**
	 * @param string
	 */
	public void setIsActive_InProject(String string) {
		isActive_InProject = string;
	}

	/**
	 * @param string
	 */
	public void setIsActive_InServer(String string) {
		isActive_InServer = string;
	}

	/**
	 * @param string
	 */
	public void setLastTimeStamp_InProject(String string) {
		lastTimeStamp_InProject = string;
	}

	/**
	 * @param string
	 */
	public void setLastTimeStamp_InServer(String string) {
		lastTimeStamp_InServer = string;
	}

	/**
	 * @return
	 */
	public String getCommitStatus() {
		return commitStatus;
	}

	/**
	 * @param string
	 */
	public void setCommitStatus(String string) {
		commitStatus = string;
	}

}
