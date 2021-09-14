/*
 * Created on Mar 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.vo;

import oem.edge.ed.odc.remoteviewer.actions.ApplicationForm;
// test
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ValueObject;

import org.apache.commons.logging.Log;

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
public class ApplicationVO extends ValueObject{

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	

	private static Log logger = ODCLogger.getLogger(UserProjectVO.class);

	private String userName = "";
	private String applicationId = "";
	private String applicationName = "";
	private String serverName = "";
	private String applicationPath = "";
	private String idPrefix = "";
	private String numberOfUsers = "";
	private String fileSystemType = "";
	private String status = "";
	
	private String isActive = "";
	private String lastTimeStamp = "";
	
	private String errorMessage = "";
	private String sqlMessage = "";
	private String applicationForm = "";
	private String commitStatus = "false";

	public static void main(String[] args) {
	}
	/**
	 * @return
	 */
	public String getApplicationId() {
		return applicationId;
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
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param string
	 */
	public void setApplicationId(String string) {
		applicationId = string;
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
	public void setNumberOfUsers(String string) {
		numberOfUsers = string;
	}

	/**
	 * @param string
	 */
	public void setServerName(String string) {
		serverName = string;
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
	public String getErrorMessage() {
		return errorMessage;
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
	public void setSqlMessage(String string) {
		sqlMessage = string;
	}

	public void setFormtoVO(ApplicationForm af)
	{
		setApplicationId( af.getApplicationId() );
		setApplicationName( af.getApplicationName() );
		setServerName( af.getServerName() );
		setApplicationPath( af.getApplicationPath() );
		setIdPrefix( af.getIdPrefix() );
		setNumberOfUsers( af.getNumberOfUsers() );
		setFileSystemType( af.getFileSystemType() );
		
	}
	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string) {
		userName = string;
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
