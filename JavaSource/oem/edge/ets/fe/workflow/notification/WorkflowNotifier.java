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
import java.util.Collection;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import org.apache.commons.logging.Log;

//TODO: 00 Not yet uploaded in CMVC
//
/**
 * Class       : WorkflowNotifier
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description : 
 * Date		   : Nov 23, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class WorkflowNotifier extends Notifier  implements NotificationConstants{

	private static Log logger = WorkflowLogger.getLogger(IssueNotifier.class);
	
	private DetailsUtils d = new DetailsUtils();
	private WorkflowNotificationParams p = null;
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#init(oem.edge.ets.fe.workflow.notification.NotificationParams, oem.edge.ets.fe.workflow.dao.DBAccess)
	 */
	public boolean init(NotificationParams params, DBAccess db) {
		super.init(params, db);
		if (!(params instanceof WorkflowNotificationParams)) {
			return false;
		}
		p = (WorkflowNotificationParams)params;
		System.out.println("[WORKFLOWNOTIFIER::INIT]"+p.getComments());
		d.setWorkflowID(p.getWorkflowID());
		d.setProjectID(p.getProjectID());
		d.extractWorkflowDetails(db.getConnection());
		return true;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#send(int, java.util.Collection, boolean)
	 */
	public boolean send(int recipient_type, Collection recipient_userid, boolean bccMail,DBAccess db) {
		super.send(recipient_type,recipient_userid,bccMail,db);
		String[] template = null;
		if(p.getEventType()==EVT_WORKFLOW_GENERAL)
		{
			template = getTemplate(TEMPLATE_WORKFLOW_GENERAL, db);
		}

		if(template!=null && template.length==2)
		{
			String subject = fillPlaceholders(template[0]);
			String body = fillPlaceholders(template[1]);
			subject = CharUtils.deHTMLize(subject);
			body = CharUtils.deHTMLize(body);
			doMail(body,subject,from_email,to_email,bccMail);
			return true;
		}
	
		return false;
	}
	protected String fillPlaceholders(String s)
	{
		s = super.fillPlaceholders(s);
		
		s= s.replaceAll("<WORKFLOW_ACCOUNT_CONTACT>",CharUtils.HTMLizeNoNull(er(d.getWacct_contact())));
		s= s.replaceAll("<WORKFLOW_NAME>",CharUtils.HTMLizeNoNull(er(d.getWwf_name())));
		s= s.replaceAll("<WORKFLOW_ID>",CharUtils.HTMLizeNoNull(er(d.getWorkflowID())));
		s= s.replaceAll("<COMMENTS>",CharUtils.HTMLizeNoNull(er(p.getComments())));
		
		return s;
	}

	public boolean send(int recipient_type, String recipient_userid, boolean bccMail, DBAccess db) {
		ArrayList temp = new ArrayList();
		String[] temp2 = recipient_userid.split(recipient_userid,',');
		for(int i=0; i<temp2.length; i++)
			temp.add(temp2[i]);
		return send(recipient_type,temp,bccMail,db);
	}
}

