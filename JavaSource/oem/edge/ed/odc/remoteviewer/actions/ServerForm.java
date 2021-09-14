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

import oem.edge.ed.odc.remoteviewer.vo.ServerVO;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 6 fields on this form:
 * <ul>
 * <li>status - [your comment here]
 * <li>serverInstanceId - [your comment here]
 * <li>isActive - [your comment here]
 * <li>gridPath - [your comment here]
 * <li>lastTimeStamp - [your comment here]
 * <li>serverName - [your comment here]
 * </ul>
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class ServerForm extends ActionForm {

	private String status = null;
	private String serverInstanceId = null;
	private String gridPath = null;
	private String isActive = null;
	private String serverName = null;
	private String lastTimeStamp = null;
	private String commitStatus = "false";
	
	private ServerVO[] serverVO = new ServerVO[0];

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
	
	
	

	public void setServerList(Collection colObj)
	{
		try{
		
			serverVO = (ServerVO[]) colObj.toArray( new ServerVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public ServerVO[] getServerList()
	{
		return serverVO;
	}

	

	public void setServerListUnique(Collection colObj)
	{
		try{
		
			serverVO = (ServerVO[]) colObj.toArray( new ServerVO[colObj.size()]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}

	public ServerVO[] getServerListUnique()
	{
		return serverVO;
	}

	
	
	
	/**
	 * @return
	 */
	public ServerVO[] getServerVO() {
		return serverVO;
	}

	/**
	 * @param serverVOs
	 */
	public void setServerVO(ServerVO[] serverVOs) {
		serverVO = serverVOs;
	}

	
	
	
	

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		status = null;
		serverInstanceId = null;
		gridPath = null;
		isActive = null;
		serverName = null;
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
