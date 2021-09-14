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

import oem.edge.ed.odc.remoteviewer.vo.ApplicationVO;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 11 fields on this form:
 * <ul>
 * <li>idPrefix - [your comment here]
 * <li>applicationId - [your comment here]
 * <li>serverName - [your comment here]
 * <li>numberOfUsers - [your comment here]
 * <li>gridPath - [your comment here]
 * <li>status - [your comment here]
 * <li>fileSystemType - [your comment here]
 * <li>lastTimeStamp - [your comment here]
 * <li>applicationPath - [your comment here]
 * <li>applicationName - [your comment here]
 * <li>isActive - [your comment here]
 * </ul>
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class ApplicationForm extends ActionForm {

	private String idPrefix = null;
	private String applicationId = null;
	private String serverName = null;
	private String numberOfUsers = null;
	private String gridPath = null;
	private String fileSystemType = null;
	private String status = null;
	private String applicationPath = null;
	private String lastTimeStamp = null;
	private String applicationName = null;
	private String isActive = null;
	
	private String message = "";
	private String errorMessage = "";
	private String sqlMessage = "";
	private String operation = "";
	private String commitStatus = "false";

	private ApplicationVO[] applicationVO = new ApplicationVO[0];

	/**
	 * Get idPrefix
	 * @return String
	 */
	public String getIdPrefix() {
		return idPrefix;
	}

	/**
	 * Set idPrefix
	 * @param <code>String</code>
	 */
	public void setIdPrefix(String i) {
		this.idPrefix = i;
	}

	/**
	 * Get applicationId
	 * @return String
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * Set applicationId
	 * @param <code>String</code>
	 */
	public void setApplicationId(String a) {
		this.applicationId = a;
	}

	/**
	 * Get serverName
	 * @return String
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Set serverName
	 * @param <code>String</code>
	 */
	public void setServerName(String s) {
		this.serverName = s;
	}

	/**
	 * Get numberOfUsers
	 * @return String
	 */
	public String getNumberOfUsers() {
		return numberOfUsers;
	}

	/**
	 * Set numberOfUsers
	 * @param <code>String</code>
	 */
	public void setNumberOfUsers(String n) {
		this.numberOfUsers = n;
	}

	/**
	 * Get fileSystemType
	 * @return String
	 */
	public String getFileSystemType() {
		return fileSystemType;
	}

	/**
	 * Set fileSystemType
	 * @param <code>String</code>
	 */
	public void setFileSystemType(String f) {
		this.fileSystemType = f;
	}

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
	 * Get applicationPath
	 * @return String
	 */
	public String getApplicationPath() {
		return applicationPath;
	}

	/**
	 * Set applicationPath
	 * @param <code>String</code>
	 */
	public void setApplicationPath(String a) {
		this.applicationPath = a;
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

	/**
	 * Get applicationName
	 * @return String
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * Set applicationName
	 * @param <code>String</code>
	 */
	public void setApplicationName(String a) {
		this.applicationName = a;
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
	


	public void setApplicationList(Collection colObj)
	{
		try{
			applicationVO = (ApplicationVO[]) colObj.toArray( new ApplicationVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public ApplicationVO[] getApplicationList()
	{
		return applicationVO;
	}

	

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		idPrefix = null;
		applicationId = null;
		serverName = null;
		numberOfUsers = null;
		fileSystemType = null;
		status = null;
		applicationPath = null;
		lastTimeStamp = null;
		applicationName = null;
		isActive = null;
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

	/**
	 * @return
	 */
	public ApplicationVO[] getApplicationVO() {
		return applicationVO;
	}

	/**
	 * @param applicationVOs
	 */
	public void setApplicationVO(ApplicationVO[] applicationVOs) {
		applicationVO = applicationVOs;
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
	public void setSqlMessage(String string) {
		sqlMessage = string;
	}

	/**
	 * @return
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param string
	 */
	public void setOperation(String string) {
		operation = string;
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
