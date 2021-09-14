package oem.edge.ed.odc.remoteviewer.actions;

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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ed.odc.remoteviewer.vo.ProjectVO;
import oem.edge.ed.odc.remoteviewer.vo.ProjectWithServerVO;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 7 fields on this form:
 * <ul>
 * <li>status - [your comment here]
 * <li>projectName - [your comment here]
 * <li>projectId - [your comment here]
 * <li>serverInstanceId - [your comment here]
 * <li>isActive - [your comment here]
 * <li>gridPath - [your comment here]
 * <li>lastTimeStamp - [your comment here]
 * </ul>
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class ProjectForm extends ActionForm {

	private String status = null;
	private String projectId = null;
	private String projectName = null;
	private String serverInstanceId = null;
	private String gridPath = null;
	private String isActive = null;
	private String lastTimeStamp = null;
	private String serverName = null;
	
	private ProjectVO[] projectVO = new ProjectVO[0];
	
	private String message = "";
	private String errorMessage = "";
	private String moduleLabel = "";
	private String sqlMessage = "";
	private String commitStatus = "false";
	
	private ProjectWithServerVO[] projectWithServerVO = new ProjectWithServerVO[0];
	
	private Object[] projectNames = new Object[0];

	/**
	 * Get status
	 * @return String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set status
	 * @param <code>String</code>
	 */
	public void setStatus(String s) {
		this.status = s;
	}

	/**
	 * Get projectId
	 * @return String
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Set projectId
	 * @param <code>String</code>
	 */
	public void setProjectId(String p) {
		this.projectId = p;
	}

	/**
	 * Get projectName
	 * @return String
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Set projectName
	 * @param <code>String</code>
	 */
	public void setProjectName(String p) {
		this.projectName = p;
	}

	/**
	 * Get serverInstanceId
	 * @return String
	 */
	public String getServerInstanceId() {
		return serverInstanceId;
	}

	/**
	 * Set serverInstanceId
	 * @param <code>String</code>
	 */
	public void setServerInstanceId(String s) {
		this.serverInstanceId = s;
	}

	/**
	 * Get isActive
	 * @return String
	 */
	public String getIsActive() {
		return isActive;
	}

	/**
	 * Set isActive
	 * @param <code>String</code>
	 */
	public void setIsActive(String i) {
		this.isActive = i;
	}

	/**
	 * Get lastTimeStamp
	 * @return String
	 */
	public String getLastTimeStamp() {
		return lastTimeStamp;
	}

	/**
	 * Set lastTimeStamp
	 * @param <code>String</code>
	 */
	public void setLastTimeStamp(String l) {
		this.lastTimeStamp = l;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		status = null;
		projectId = null;
		projectName = null;
		serverInstanceId = null;
		isActive = null;
		lastTimeStamp = null;
		gridPath = null;

		status = null;
		projectId = null;
		projectName = null;
		serverInstanceId = null;
		gridPath = null;
		isActive = null;
		lastTimeStamp = null;
		serverName = null;
	
		message = "";
		errorMessage = "";
		moduleLabel = "";
		sqlMessage = "";
	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	/**
	 * @return
	 */
	public String getGridPath() {
		return gridPath;
	}

	/**
	 * @param string
	 */
	public void setGridPath(String string) {
		gridPath = string;
	}



	public void setProjectWithServerList(Collection colObj)
	{
		try{
			projectWithServerVO = (ProjectWithServerVO[]) colObj.toArray( new ProjectWithServerVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public ProjectWithServerVO[] getProjectWithServerList()
	{
		return projectWithServerVO;
	}

	/**
	 * @return
	 */
	public Object[] getProjectNames() {
		return projectNames;
	}

	/**
	 * @return
	 */
	public ProjectVO[] getProjectVO() {
		return projectVO;
	}

	/**
	 * @param strings
	 */
	public void setProjectNames(String[] strings) {
		projectNames = strings;
	}

	/**
	 * @param projectVOs
	 */
	public void setProjectVO(ProjectVO[] projectVOs) {
		projectVO = projectVOs;
	}

	/**
	 * @return
	 */
	public ProjectWithServerVO[] getProjectWithServerVO() {
		return projectWithServerVO;
	}

	/**
	 * @param serverVOs
	 */
	public void setProjectWithServerVO(ProjectWithServerVO[] serverVOs) {
		projectWithServerVO = serverVOs;
	}

	/**
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param objects
	 */
	public void setProjectNames(Object[] objects) {
		projectNames = objects;
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
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return
	 */
	public String getModuleLabel() {
		return moduleLabel;
	}

	/**
	 * @return
	 */
	public String getSqlMessage() {
		return sqlMessage;
	}

	/**
	 * @param string
	 */
	public void setErrorMessage(String string) {
		errorMessage = string;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * @param string
	 */
	public void setModuleLabel(String string) {
		moduleLabel = string;
	}

	/**
	 * @param string
	 */
	public void setSqlMessage(String string) {
		sqlMessage = string;
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
