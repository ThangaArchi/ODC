package oem.edge.ed.odc.rmviewer.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ed.odc.rmviewer.vo.UserProjectVO;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 7 fields on this form:
 * <ul>
 * <li>status - [your comment here]
 * <li>projectId - [your comment here]
 * <li>projectName - [your comment here]
 * <li>isActive - [your comment here]
 * <li>gridPath - [your comment here]
 * <li>userName - [your comment here]
 * <li>lastTimeStamp - [your comment here]
 * </ul>
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class UserProjectForm extends ActionForm {

	private String status = null; 
	private String projectName = null;
	private String projectId = null;
	private String gridPath = null;
	private String isActive = null; 
	private String userName = null; 
	private String lastTimeStamp = null;
	private String listPage = "true";

	private String message = "";
	private String errorMessage = "";
	private String moduleLabel = "";
	private String sqlMessage = "";

	private String serverInstanceId = "";
	private String serverName = "";
	private String commitStatus = "false";
	
	private UserProjectVO[] userProjectVO = new UserProjectVO[0];
	private UserProjectVO[] userNonProjectVO = new UserProjectVO[0];

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
	 * Get gridPath
	 * @return String
	 */
	public String getGridPath() {
		return gridPath;
	}

	/**
	 * Set gridPath
	 * @param <code>String</code>
	 */
	public void setGridPath(String g) {
		this.gridPath = g;
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
		gridPath = null;
		isActive = null;
		userName = null;
		lastTimeStamp = null;

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
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param string
	 */
	public void setProjectName(String string) {
		projectName = string;
	}

	public void setUserOnly(Collection colObj)
	{
		try{
			userProjectVO = (UserProjectVO[]) colObj.toArray( new UserProjectVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserProjectVO[] getUserOnly()
	{
		return userProjectVO;
	}
	/**
	 * @return
	 */
	public UserProjectVO[] getUserNonProjectVO() {
		return userNonProjectVO;
	}

	/**
	 * @return
	 */
	public UserProjectVO[] getUserProjectVO() {
		return userProjectVO;
	}

	/**
	 * @param projectVOs
	 */
	public void setUserNonProjectVO(UserProjectVO[] projectVOs) {
		userNonProjectVO = projectVOs;
	}

	/**
	 * @param projectVOs
	 */
	public void setUserProjectVO(UserProjectVO[] projectVOs) {
		userProjectVO = projectVOs;
	}




























	public void setUserProjectList(Collection colObj)
	{
		try{
			userProjectVO = (UserProjectVO[]) colObj.toArray( new UserProjectVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserProjectVO[] getUserProjectList()
	{
		return userProjectVO;
	}




	public void setUserNonProjectList(Collection colObj)
	{
		try{
			userNonProjectVO = (UserProjectVO[]) colObj.toArray( new UserProjectVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public UserProjectVO[] getUserNonProjectList()
	{
		return userNonProjectVO;
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
	public String getServerInstanceId() {
		return serverInstanceId;
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
	public void setServerInstanceId(String string) {
		serverInstanceId = string;
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