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
public class ProjectWithServerForm extends ActionForm {

	private String status = null;
	private String projectId = null;
	private String projectName = null;
	private String serverInstanceId = null;
	private String gridPath = null;
	private String isActive = null;
	private String lastTimeStamp = null;
	private ProjectVO[] projectVO = new ProjectVO[0];
	private String commitStatus = "false";

	private ProjectWithServerVO[] projectWithServerVO = new ProjectWithServerVO[0];
	
	private Object[] projectNames = new Object[0];


	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		status = null;
		projectId = null;
		projectName = null;
		serverInstanceId = null;
		isActive = null;
		lastTimeStamp = null;
		gridPath = null;
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

	public void setProjectList(Collection colObj) {
		try{
			projectWithServerVO = (ProjectWithServerVO[]) colObj.toArray( new ProjectWithServerVO[colObj.size()]);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public ProjectWithServerVO[] getProjectList()
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
	public String getGridPath() {
		return gridPath;
	}

	/**
	 * @return
	 */
	public String getIsActive() {
		return isActive;
	}

	/**
	 * @return
	 */
	public String getLastTimeStamp() {
		return lastTimeStamp;
	}

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
	public void setGridPath(String string) {
		gridPath = string;
	}

	/**
	 * @param string
	 */
	public void setIsActive(String string) {
		isActive = string;
	}

	/**
	 * @param string
	 */
	public void setLastTimeStamp(String string) {
		lastTimeStamp = string;
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
	 * @param objects
	 */
	public void setProjectNames(Object[] objects) {
		projectNames = objects;
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
