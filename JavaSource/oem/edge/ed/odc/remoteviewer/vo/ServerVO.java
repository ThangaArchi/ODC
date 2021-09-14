/*
 * Created on Mar 20, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.vo;

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.utils.ODCLogger;
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
public class ServerVO extends ValueObject{

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	

	private static Log logger = ODCLogger.getLogger(ServerVO.class);
	
	private String serverInstanceId = "";
	private String serverName = "";
	private String gridPath = "";
	private String status = "";
	
	private String isActive = "";
	private String lastTimeStamp = "";

	private String commitStatus = "false";

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
