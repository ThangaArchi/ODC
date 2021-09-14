/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.notification;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;

//
/**
 * Class       : NotificationParams
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class NotificationParams {
	private static Log logger = WorkflowLogger.getLogger(NotificationParams.class);
	
	
	//Parameters that are required for every notification
	private int notificationType = NotificationConstants.NT_NOTHING;
	private int eventType = NotificationConstants.EVT_NOTHING;
	private String projectID = null;	
	private String loggedUser = null;
	private String tc = null;
	
	//required for some
	private String comments = null;
	
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	
	/**
	 * @return Returns the eventType.
	 */
	public int getEventType() {
		return eventType;
	}
	/**
	 * @param eventType The eventType to set.
	 */
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return Returns the notificationType.
	 */
	public int getNotificationType() {
		return notificationType;
	}
	/**
	 * @param notificationType The notificationType to set.
	 */
	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	/**
	 * @return Returns the loggedUser.
	 */
	public String getLoggedUser() {
		return loggedUser;
	}
	/**
	 * @param loggedUser The loggedUser to set.
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}
	/**
	 * @return Returns the tc.
	 */
	public String getTc() {
		return tc;
	}
	/**
	 * @param tc The tc to set.
	 */
	public void setTc(String tc) {
		this.tc = tc;
	}
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
}

