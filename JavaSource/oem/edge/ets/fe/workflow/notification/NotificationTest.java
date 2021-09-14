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

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;

//
/**
 * Class       : NotificationTest
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 15, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class NotificationTest {
public static void sendDummyMail()
{
	
	IssueNotificationParams params = new IssueNotificationParams();
	params.setProjectID("1162933926032");
	params.setWorkflowID("1163085484081-4770");
	params.setTc("2992");
	params.setIssueID("1163485537046-7744");
	params.setLoggedUser("k.p.achar@in.ibm.com");
	params.setNotificationType(NotificationConstants.NT_ISSUE);
	params.setEventType(NotificationConstants.EVT_ISSUE_NEW_ISSUE);
	
	ArrayList issue_owners = new ArrayList();
	issue_owners.add("k.p.achar@in.ibm.com");
	issue_owners.add("kesavankutty@in.ibm.com");
	issue_owners.add("jeetrao@us.ibm.com");
	System.out.println("Sending dummy mail....");
	DBAccess db = null;
	try{
	db = new DBAccess();
	
			try {
				Notifier n = NotifierFactory.getNotificationSender(params);
				n.init(params, db);
				System.out.println("inited");
				n.send(NotificationConstants.RECIPIENT_ISSUE_OWNER,issue_owners,false,db);
				System.out.println("sent");
			} catch (UnsupportedNotificationException e) {
				e.printStackTrace();
			}
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	finally{
		if(db!=null)
		{
			db=null;
			try{
			db.close();
			}catch(Exception e){}
		}
	}
}
}

