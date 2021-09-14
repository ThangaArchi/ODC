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



/**
 * Class       : NotificationConstants
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public interface NotificationConstants {
	//	Notification Types
	public static final int NT_NOTHING = 0; 
	public static final int NT_ISSUE = 1;
	public static final int NT_WORKFLOW = 2;
	public static final int NT_SCORECARD = 3;
	public static final int NT_IDENTIFY_STAGE = 4;
	public static final int NT_PREPARE_STAGE = 5;
	
	//Event types
	public static final int EVT_NOTHING = 100;
	
	public static final int EVT_WORKFLOW_AUTHENTICATION = 201;
	public static final int EVT_WORKFLOW_STATE_CHANGE = 202;
	public static final int EVT_WORKFLOW_NEW_SETMET = 203;
	public static final int EVT_WORKFLOW_GENERAL = 204;
	public static final int EVT_WORKFLOW_REMINDER = 205;
	public static final int EVT_WORKFLOW_REMINDER_CREATION = 206;
	public static final int EVT_WORKFLOW_MSA_NEXT_DUEDATE_REMINDER = 207;
	
	public static final int EVT_ISSUE_NEW_ISSUE = 301;
	public static final int EVT_ISSUE_ACCEPTED = 302;
	public static final int EVT_ISSUE_REJECTED = 303;
	public static final int EVT_ISSUE_MODIFICATION = 304;
	public static final int EVT_ISSUE_COMPLETED = 305;
	public static final int EVT_ISSUE_CANCELLED = 306;
	public static final int EVT_ISSUE_REASSIGN = 307;
	public static final int EVT_OWNER_ACCEPTS = 308;
	public static final int EVT_OWNER_REJECTS = 309;
	public static final int EVT_ISSUE_ACCEPTED_DAEMON = 310;
	public static final int EVT_OWNER_ACCEPTS_DAEMON = 311;
	
	/**
	 * @deprecated Use EVT_WORKFLOW_REMINDER instead
	 */
	public static final int EVT_REMINDER = EVT_WORKFLOW_REMINDER;
	
	
	//Recipient types
	public static final int RECIPIENT_NOTHING = 1000;
	
	public static final int RECIPIENT_WORKFLOW_GENERAL = 2001;
	public static final int RECIPIENT_WORKFLOW_ACCOUNT_CONTACT = 2002;
	
	public static final int RECIPIENT_ISSUE_OWNER = 3001;
	public static final int RECIPIENT_ISSUE_CONTACT = 3002;
	public static final int RECIPIENT_ISSUE_GENERAL = 3003;
	public static final int RECIPIENT_ISSUE_OLD_OWNER = 3004;
}

