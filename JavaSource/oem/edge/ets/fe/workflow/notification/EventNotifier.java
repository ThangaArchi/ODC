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


/**
 * Class : EventNotifier Package : oem.edge.ets.fe.workflow.notification
 * Description : Date : Nov 27, 2006
 * 
 * @author : Pradyumna Achar
 */
public class EventNotifier extends Notifier {
	private static Log logger = WorkflowLogger.getLogger(EventNotifier.class);

	private EventNotificationParams p = null;

	DetailsUtils d = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#init(oem.edge.ets.fe.workflow.notification.NotificationParams,
	 *      oem.edge.ets.fe.workflow.dao.DBAccess)
	 */
	public boolean init(NotificationParams params, DBAccess db) {
		super.init(params, db);
		if (!(params instanceof EventNotificationParams)) {
			return false;
		}
		p = (EventNotificationParams) params;
		d = new DetailsUtils();
		d.setProjectID(p.getProjectID());
		d.setWorkflowID(p.getWf_id());
		d.setCcalendarID(p.getCalendarID());				
		d.extractCalendarDetails(db);		
		d.getWFProjectDetails();		
		d.extractWorkflowDetails();		
		return true;
	}

	public boolean send(int recipient_type, String recipient_userid,
			boolean bccMail, DBAccess db) {
		ArrayList temp = new ArrayList();
		//String[] temp2 = recipient_userid.split(recipient_userid,',');
		String[] temp2 = recipient_userid.split(",");
		for (int i = 0; i < temp2.length; i++)
			temp.add(temp2[i]);
		return send(recipient_type, temp, bccMail, db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.notification.NotificationSender#send(int,
	 *      java.util.Collection, boolean)
	 */
	public boolean send(int recipient_type, Collection recipient_userid,
			boolean bccMail, DBAccess db) {
		super.send(recipient_type, recipient_userid, bccMail, db);
		String[] template = null;
		if (p.getEventType() == EVT_WORKFLOW_REMINDER_CREATION) {
			template = getTemplate(TEMPLATE_REMINDER_CREATION, db);
		}
		if (p.getEventType() == EVT_WORKFLOW_REMINDER) {
			template = getTemplate(TEMPLATE_REMINDER, db);
		}
		if (p.getEventType() == EVT_WORKFLOW_MSA_NEXT_DUEDATE_REMINDER) {
			template = getTemplate(TEMPLATE_MSA_NEXT_DUEDATE_REMINDER, db);
		}

		if (template != null && template.length == 2) {
			String subject = fillPlaceholders(template[0]);
			String body = fillPlaceholders(template[1]);
			subject = CharUtils.deHTMLize(subject);
			body = CharUtils.deHTMLize(body);
			doMail(body, subject, from_email, to_email, bccMail);
			return true;
		}
		return false;
	}

	protected String fillPlaceholders(String s) {
		s = super.fillPlaceholders(s);

		s = s.replaceAll("<EVT_SCHEDULE_DATE>", CharUtils.HTMLizeNoNull(er(d
				.getCschedule_date())));
		s = s.replaceAll("<EVT_SCHEDULED_BY>", CharUtils.HTMLizeNoNull(er(d
				.getCscheduled_by())));
		s = s.replaceAll("<EVT_START_TIME>", CharUtils.HTMLizeNoNull(er(d
				.getCstart_time())));
		s = s.replaceAll("<EVT_DURATION>", CharUtils.HTMLizeNoNull(er(d
				.getCduration())));
		s = s.replaceAll("<EVT_SUBJECT>", CharUtils.HTMLizeNoNull(er(d
				.getCsubject())));
		s = s.replaceAll("<EVT_DESCRIPTION>", CharUtils.HTMLizeNoNull(er(d
				.getCdescription())));
		s = s.replaceAll("<CALENDAR_ID>", CharUtils.HTMLizeNoNull(er(d
				.getCcalendarID())));
		s = s.replaceAll("<NEXT_DUE_DATE>", CharUtils.HTMLizeNoNull(er(p.getMsa_next_duedate())));
		s = s.replaceAll("<COMPANY>", CharUtils.HTMLizeNoNull(er(d.getCompany())));
		s = s.replaceAll("<WF_CURR_STAGE_NAME>", CharUtils.HTMLizeNoNull(er(d.getWwf_curr_stage_name())));
		
		
		String temp = d.getCstart_time();
		if(temp!=null)
		{
			String[] temp2 = temp.split(" ");
			if(temp2.length>0)
			{
				temp = temp2[0];
				temp2 = temp.split("-");
				if(temp2.length==3)
				{
					s = s.replaceAll("<MEETING_DATE>", CharUtils.HTMLizeNoNull(er(temp2[1]+"/"+temp2[2]+"/"+temp2[0])));
				}
			}
		}
		return s;
	}
}
