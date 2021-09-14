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

package oem.edge.ets.fe.workflow.notifypopup;

import java.util.ArrayList;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.Notifier;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;
import oem.edge.ets.fe.workflow.notification.WorkflowNotificationParams;

import org.apache.commons.logging.Log;


/**
 * Class       : NotifyPopupBL
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NotifyPopupBL extends WorkflowObject {

	private static Log logger = WorkflowLogger.getLogger(NotifyPopupBL.class);
	private String recipients= "";
	private String body = "";
	private ErrorObj eo = null;
	public boolean sendNotification(NotifyPopupVO vo,String loggedUser )
	{
		WorkflowNotificationParams params = new WorkflowNotificationParams();
		System.out.println("[NOTIFYPOPUPBL]Setting projectID to  "+vo.getProjectID());
		params.setProjectID(vo.getProjectID());
		params.setWorkflowID(vo.getWorkflowID());
		params.setTc("0000");
		params.setLoggedUser(loggedUser);
		params.setNotificationType(NotificationConstants.NT_WORKFLOW);
		params.setEventType(NotificationConstants.EVT_WORKFLOW_GENERAL);
		
		params.setComments(vo.getComments());
		System.out.println(params.getComments());
		ArrayList to = new ArrayList();
	 	for(int i = 0; i<vo.getPeopleToBeNotified().length; i++)
	 		to.add(vo.getPeopleToBeNotified()[i]);
	 	
		DBAccess db = null;
		try{
			db = new DBAccess();
			Notifier n = NotifierFactory.getNotificationSender(params);
			n.init(params, db);
			System.out.println("inited Emailer");
			n.send(NotificationConstants.RECIPIENT_WORKFLOW_GENERAL,to,false,db);
			db.close();
			db = null;
		} catch (Exception e) {
			System.out.println(e);
		}finally{
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}
		return true;
		/*
		if(vo.getPeopleToBeNotified()!=null)
		 {
			DBAccess db = null;
			try{
				db = new DBAccess();
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId(loggedUser);
				u.extractUserDetails(db.getConnection());
				loggedUser = u.getEMail();
				db.close();
				db = null;
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(db!=null)
				{
					try {
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					db=null;
				}
			}
		 	for(int i = 0; i<vo.getPeopleToBeNotified().length; i++)
		 	{	
		 		if(i==0)
		 		{
		 			recipients = vo.getPeopleToBeNotified()[0];
		 		}else
		 		{
		 			recipients = recipients + ","+vo.getPeopleToBeNotified()[i];
		 		}
		 	}
		 	String comments = vo.getComments();
		 	if(comments==null)comments="";
		 	body = "* * * *   W O R K F L O W     N O T I F I C A T I O N   * * * *\n" +
		 			"\n" +
		 			comments;
		 	body+="\n----------------------------------------------------------------\n";
			body += "Mail notification generated on "+(new java.util.Date()).toGMTString();
			
			SMTPMail mailer = new SMTPMail();
			mailer.setSubject("Workflow Notification");
			mailer.setMailText(body);
        	mailer.setOriginator(loggedUser);
			mailer.setRecipients(recipients);
			try{
			//mailer.send();
				NotificationMsgHelper.sendEMail(loggedUser,StringUtil.EMPTY_STRING,StringUtil.EMPTY_STRING,recipients,Global.mailHost,body,"Workflow Notification",loggedUser);
			}catch(Exception e)
			{
				
				 eo = new ErrorObj();
				eo.setErrorText(e.toString());
				
				System.err.println("Message sending failed. "+e);
				e.printStackTrace();
				return false;
			}
			System.out.println("Mail sent to "+recipients);
		 }
		return true;
		*/
	}

	/**
	 * @return Returns the error object.
	 */
	public ErrorObj getEo() {
		return eo;
	}
}
