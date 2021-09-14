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
 * Class       : NotifierFactory
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class NotifierFactory  implements NotificationConstants {
	private static Log logger = WorkflowLogger.getLogger(NotifierFactory.class);
	
	public static Notifier getNotificationSender(NotificationParams params) throws UnsupportedNotificationException {
		if(params.getNotificationType()==NT_ISSUE)
			return (new IssueNotifier());
		if(params.getNotificationType()==NT_WORKFLOW && params.getEventType()==EVT_WORKFLOW_GENERAL)
			return (new WorkflowNotifier());
		if(params.getNotificationType()==NT_WORKFLOW && params.getEventType()==EVT_WORKFLOW_REMINDER_CREATION)
			return (new EventNotifier());
		if(params.getNotificationType()==NT_WORKFLOW && params.getEventType()==EVT_WORKFLOW_REMINDER)
			return (new EventNotifier());
		if(params.getNotificationType()==NT_WORKFLOW && params.getEventType()==EVT_WORKFLOW_MSA_NEXT_DUEDATE_REMINDER)
			return (new EventNotifier());
		
		throw new UnsupportedNotificationException();
	}
	
}

