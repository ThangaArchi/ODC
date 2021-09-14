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

import oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 5 fields on this form:
 * <ul>
 * <li>status - [your comment here]
 * <li>isActive - [your comment here]
 * <li>userName - [your comment here]
 * <li>lastTimeStamp - [your comment here]
 * <li>applicationId - [your comment here]
 * </ul>
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class UserApplicationForm extends ActionForm {

	private String status = null;
	private String isActive = null;
	private String userName = null;
	private String applicationId = null;
	private String lastTimeStamp = null;

	private String applicationName = "";
	private String serverName = "";
	private String applicationPath = "";
	private String idPrefix = "";
	private String numberOfUsers = "";
	private String fileSystemType = "";
	private String listPage = "true";
	
	private String message = "";
	private String errorMessage = "";
	private String moduleLabel = "";
	private String sqlMessage = "";
	private String commitStatus = "false";
	
	private UserApplicationVO[] userApplicationVO = new UserApplicationVO[0];
	private UserApplicationVO[] userNonApplicationVO = new UserApplicationVO[0];

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
	 * Get userName
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set userName
	 * @param <code>String</code>
	 */
	public void setUserName(String u) {
		this.userName = u;
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
	 * @return
	 */
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * @return
	 */
	public String getApplicationPath() {
		return applicationPath;
	}

	/**
	 * @return
	 */
	public String getFileSystemType() {
		return fileSystemType;
	}

	/**
	 * @return
	 */
	public String getIdPrefix() {
		return idPrefix;
	}

	/**
	 * @return
	 */
	public String getNumberOfUsers() {
		return numberOfUsers;
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
	public void setApplicationName(String string) {
		applicationName = string;
	}

	/**
	 * @param string
	 */
	public void setApplicationPath(String string) {
		applicationPath = string;
	}

	/**
	 * @param string
	 */
	public void setFileSystemType(String string) {
		fileSystemType = string;
	}

	/**
	 * @param string
	 */
	public void setIdPrefix(String string) {
		idPrefix = string;
	}

	/**
	 * @param string
	 */
	public void setNumberOfUsers(String string) {
		numberOfUsers = string;
	}

	/**
	 * @param string
	 */
	public void setServerName(String string) {
		serverName = string;
	}



	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		status = null;
		isActive = null;
		userName = null;
		applicationId = null;
		lastTimeStamp = null;


		String applicationName = "";
		String serverName = "";
		String applicationPath = "";
		String idPrefix = "";
		String numberOfUsers = "";
		String fileSystemType = "";
		String message = "";
		String errorMessage = "";
		UserApplicationVO[] userApplicationVO = null;
		UserApplicationVO[] userNonApplicationVO = null;
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
	public UserApplicationVO[] getUserApplicationVO() {
		return userApplicationVO;
	}

	/**
	 * @param applicationVOs
	 */
	public void setUserApplicationVO(UserApplicationVO[] applicationVOs) {
		userApplicationVO = applicationVOs;
	}

	public void setUserApplicationList(Collection colObj)
	{
		try{
			userApplicationVO = (UserApplicationVO[]) colObj.toArray( new UserApplicationVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserApplicationVO[] getUserApplicationList()
	{
		return userApplicationVO;
	}




	public void setUserNonApplicationList(Collection colObj)
	{
		try{
			userNonApplicationVO = (UserApplicationVO[]) colObj.toArray( new UserApplicationVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserApplicationVO[] getUserNonApplicationList()
	{
		return userNonApplicationVO;
	}









	public void setUserOnly(Collection colObj)
	{
		try{
			userApplicationVO = (UserApplicationVO[]) colObj.toArray( new UserApplicationVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserApplicationVO[] getUserOnly()
	{
		return userApplicationVO;
	}
	/**
	 * @return
	 */
	public UserApplicationVO[] getUserNonApplicationVO() {
		return userNonApplicationVO;
	}

	/**
	 * @param applicationVOs
	 */
	public void setUserNonApplicationVO(UserApplicationVO[] applicationVOs) {
		userNonApplicationVO = applicationVOs;
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
	 * @return
	 */
	public String getModuleLabel() {
		return moduleLabel;
	}

	/**
	 * @param string
	 */
	public void setModuleLabel(String string) {
		moduleLabel = string;
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

	/**
	 * @return
	 */
	public String getListPage() {
		return listPage;
	}

	/**
	 * @param string
	 */
	public void setListPage(String string) {
		listPage = string;
	}

}
