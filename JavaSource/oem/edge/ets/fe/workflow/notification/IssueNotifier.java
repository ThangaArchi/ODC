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
import oem.edge.ets.fe.workflow.util.MiscUtils;
import java.util.Date;

import org.apache.commons.logging.Log;
//
/**
 * Class       : IssueNotifier
 * Package     : oem.edge.ets.fe.workflow.notification
 * Description :
 * Date		   : Nov 15, 2006
 *
 * @author     : Pradyumna Achar
 */
public class IssueNotifier extends Notifier implements NotificationConstants{

	private static Log logger = WorkflowLogger.getLogger(IssueNotifier.class);

	private DetailsUtils d = new DetailsUtils();
	private IssueNotificationParams p = null;
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#init(oem.edge.ets.fe.workflow.notification.NotificationParams, oem.edge.ets.fe.workflow.dao.DBAccess)
	 */
	public boolean init(NotificationParams params, DBAccess db) {
		super.init(params, db);
		if (!(params instanceof IssueNotificationParams)) {
			return false;
		}
		p = (IssueNotificationParams)params;
		d.setIssueID(p.getIssueID());
		d.setWorkflowID(p.getWorkflowID());
		d.setProjectID(p.getProjectID());
		d.extractIssueDetails(db.getConnection());
		d.extractWorkflowDetails(db.getConnection());
		return true;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#send(int, java.util.Collection, boolean)
	 */
	public boolean send(int recipient_type, Collection recipient_userid, boolean bccMail,DBAccess db) {
		super.send(recipient_type,recipient_userid,bccMail,db);
		String[] template = null;
		if(p.getEventType()==EVT_ISSUE_NEW_ISSUE)
		{
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_NEW_ISSUE__CONTACT, db);
			if(recipient_type==RECIPIENT_ISSUE_OWNER)
				template = getTemplate(TEMPLATE_NEW_ISSUE__OWNER, db);
			if(recipient_type==RECIPIENT_ISSUE_GENERAL)
				template = getTemplate(TEMPLATE_NEW_ISSUE__GENERAL, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_NEW_ISSUE__ACCOUNT_CONTACT, db);
		}
		if(p.getEventType()==EVT_ISSUE_ACCEPTED)
		{
			if(recipient_type==RECIPIENT_ISSUE_OWNER)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__OWNER, db);
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__CONTACT, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__ACCOUNT_CONTACT, db);
		}
		if(p.getEventType()==EVT_ISSUE_ACCEPTED_DAEMON)
		{
			if(recipient_type==RECIPIENT_ISSUE_OWNER)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__OWNER_DAEMON, db);
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__CONTACT_DAEMON, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_ACCEPTED__ACCOUNT_CONTACT_DAEMON, db);
		}
		if(p.getEventType()==EVT_ISSUE_REJECTED)
		{
			if(recipient_type==RECIPIENT_ISSUE_OWNER)
				template = getTemplate(TEMPLATE_ISSUE_REJECTED__OWNER, db);
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_REJECTED__CONTACT, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_REJECTED__ACCOUNT_CONTACT, db);
		}
		if(p.getEventType()==EVT_ISSUE_REASSIGN)
		{
			if(recipient_type==RECIPIENT_ISSUE_OWNER)
				template = getTemplate(TEMPLATE_ISSUE_REASSIGNED__OWNER, db);
			if(recipient_type==RECIPIENT_ISSUE_OLD_OWNER)
				template = getTemplate(TEMPLATE_ISSUE_REASSIGNED__OLDOWNER, db);
			if(recipient_type==RECIPIENT_ISSUE_GENERAL)
				template = getTemplate(TEMPLATE_ISSUE_EDITED, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_ISSUE_EDITED, db);
		}
		if(p.getEventType()==EVT_OWNER_ACCEPTS)
		{
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_ACCEPTS__CONTACT, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_ACCEPTS__ACCOUNT_CONTACT, db);
		}
		if(p.getEventType()==EVT_OWNER_ACCEPTS_DAEMON)
		{
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_ACCEPTS__CONTACT_DAEMON, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_ACCEPTS__ACCOUNT_CONTACT_DAEMON, db);
		}
		if(p.getEventType()==EVT_OWNER_REJECTS)
		{
			if(recipient_type==RECIPIENT_ISSUE_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_REJECTS__CONTACT, db);
			if(recipient_type==RECIPIENT_WORKFLOW_ACCOUNT_CONTACT)
				template = getTemplate(TEMPLATE_OWNER_REJECTS__ACCOUNT_CONTACT, db);
		}
		if(template!=null && template.length==2)
		{
			String subject = fillPlaceholders(template[0]);
			String body = fillPlaceholders(template[1]);
			subject = CharUtils.deHTMLize((subject));
			System.out.println("Subject is "+subject);
			body = CharUtils.deHTMLize((body));
			doMail(body,subject,from_email,to_email,bccMail);
			return true;
		}

		return false;
	}
	protected String fillPlaceholders(String s)
	{
		s = super.fillPlaceholders(s);
		s= s.replaceAll("<ISSUE_ID>",CharUtils.HTMLizeNoNull(er(p.getIssueID())));
		s= s.replaceAll("<ISSUE_TITLE>",CharUtils.HTMLizeNoNull(er(d.getIissue_title())));
		s= s.replaceAll("<ISSUE_ID_DISPLAY>",CharUtils.HTMLizeNoNull(er(d.getIissue_id_display())));
		s= s.replaceAll("<ISSUE_TARGET_DATE>",CharUtils.HTMLizeNoNull(MiscUtils.reformatDate(er(d.getItarget_date()))));
		s= s.replaceAll("<ISSUE_INITIAL_TARGET_DATE>",CharUtils.HTMLizeNoNull(er(MiscUtils.reformatDate(d.getIinitial_target_date()))));
		s= s.replaceAll("<ISSUE_STATUS>",CharUtils.HTMLizeNoNull(er(d.getIstatus())));
		s= s.replaceAll("<DATE_ASSIGNED>",CharUtils.HTMLizeNoNull(er((new Date()).toString())));
		s= s.replaceAll("<ISSUE_TYPE>",CharUtils.HTMLizeNoNull(er(d.getIissue_type())));
		s= s.replaceAll("<ISSUE_DESC>",CharUtils.HTMLizeNoNull(er(d.getIissue_desc())));

		String owners  =  "";
		for(int i =0; i<d.getIownerNames().size(); i++)
		owners += "\t"+(i+1)+". "+d.getIownerNames().get(i)+"\n";

		s= s.replaceAll("<ISSUE_OWNERS>",CharUtils.HTMLizeNoNull(er(owners)));
		s= s.replaceAll("<ISSUE_OWNER>",CharUtils.HTMLizeNoNull(er(p.getIssue_owner())));
		s= s.replaceAll("<WORKFLOW_ACCOUNT_CONTACT>",CharUtils.HTMLizeNoNull(er(d.getWacct_contact())));
		s= s.replaceAll("<WORKFLOW_NAME>",CharUtils.HTMLizeNoNull(er(d.getWwf_name())));
		s= s.replaceAll("<WORKFLOW_ID>",CharUtils.HTMLizeNoNull(er(d.getWorkflowID())));
		s= s.replaceAll("<COMMENTS>",CharUtils.HTMLizeNoNull(er(p.getComments())));

		return s;
	}
	public boolean send(int recipient_type, String recipient_userid, boolean bccMail, DBAccess db) {
		ArrayList temp = new ArrayList();
		//String[] temp2 = recipient_userid.split(recipient_userid,',');
		String[] temp2 = recipient_userid.split(",");
		for(int i=0; i<temp2.length; i++)
			temp.add(temp2[i]);
		return send(recipient_type,temp,bccMail,db);
	}
}

