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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.notification.EventNotificationParams;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.Notifier;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;

import org.apache.commons.logging.Log;

/**
 * Class : EventDetailsBL Package :
 * oem.edge.ets.fe.workflow.eventdetailspopupwindow Description :
 * 
 * @author Pradyumna Achar
 */
public class EventDetailsBL {

	private static Log logger = WorkflowLogger.getLogger(EventDetailsBL.class);

	public boolean setEventDetails(WorkflowEventDetailsVO vo, String loggedUser) {

		EventDetailsDAO dao = new EventDetailsDAO();
		
		if(!dao.saveWorkflowObject(vo, loggedUser))
			//return false; //FAILURE NOT HANDLED YET.
			return true;
		
			
		String title = vo.getTitle();
		String description = vo.getDesc();
		String month = vo.getMonth()[0];
		String day = vo.getDay()[0];
		String year = vo.getYear()[0];
		String minute = vo.getMin()[0];
		String hour = vo.getHour()[0];
		String ampm = vo.getAmpm()[0];
		String repeatsFor = vo.getRepeatsFor()[0];
		ArrayList to = new ArrayList();
		System.out.println(".....All members checkbox = "
				+ vo.getAppliesToAll());
		System.out.println(".....Selected Members are:");
		String rlist = "";
		if (vo.getTeamMembers() == null) {
			System.out.println("Nobody was selected.");

		} else {
			for (int i = 0; i < vo.getTeamMembers().length; i++) {
				if (rlist == "") {
					rlist = vo.getTeamMembers()[i];
				} else {
					rlist += "," + vo.getTeamMembers()[i];
				}
				System.out.println("........." + vo.getTeamMembers()[i]);
				to.add(vo.getTeamMembers()[i]);
			}
		}
		System.out.println(".....Notify via email checkbox = "
				+ vo.getNotifyEmail());
		boolean sendEmail = false;
		if (vo.getNotifyEmail() != null && rlist!=null && rlist.length()!=0) {
			if (vo.getNotifyEmail().trim().equals("on")) {
				sendEmail = true;
			} else {
				System.out.println("no emails to be sent");
			}
		} else {
			System.out.println("no emails to be sent");
		}
		if(!sendEmail)return true;
		boolean bccEmail = true;

		if (vo.getEmailOption() == null) {
			System.out
					.println(".....None of the email notification options were selected.");

		} else {
			System.out.println(".....Email notification option = "
					+ vo.getEmailOption());
			if (vo.getEmailOption().equals("to"))
				bccEmail = false;
		}
		
		DBAccess db = null;
		try {
			db = new DBAccess();

			EventNotificationParams params = new EventNotificationParams();
			params.setProjectID(vo.getProjectID());
			params.setWf_id(vo.getWorkflowID());
			params.setTc("0000");
			params.setCalendarID(dao.getCalendarID());
			params.setLoggedUser(loggedUser);
			params.setNotificationType(NotificationConstants.NT_WORKFLOW);
			params.setEventType(NotificationConstants.EVT_WORKFLOW_REMINDER_CREATION);
			Notifier n = NotifierFactory.getNotificationSender(params);

			n.init(params, db);
			n.send(NotificationConstants.RECIPIENT_WORKFLOW_GENERAL, to, bccEmail,
					db);
			db.close();
			db = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db = null;
			}
		}

		/*
		 * ////MAIL SEND PROC//// SMTPMail mailer = new SMTPMail();
		 * mailer.setOriginator(loggedUser);
		 * 
		 * DBAccess db = null; try{ db = new DBAccess(); ETSUserDetails u = new
		 * ETSUserDetails(); u.setWebId(loggedUser);
		 * u.extractUserDetails(db.getConnection()); loggedUser = u.getEMail();
		 * db.close(); db = null; } catch (Exception e) { e.printStackTrace();
		 * }finally{ if(db!=null) { try { db.close(); } catch (Exception e) {
		 * e.printStackTrace(); } db=null; } }
		 * 
		 * String text = "*** WORKFLOW EVENT NOTIFICATION ***\n" +
		 * "----------------------------------------------------------------\n" +
		 * "\n" + "A new workflow event was created.\n" + "Details of the event
		 * follow:\n" +
		 * "----------------------------------------------------------------\n" +
		 * "TITLE : " + title + "\nDESCRIPTION :\n" + description + "\n" + "DATE
		 * AND TIME : " + month + " " + day + " " + year + " " + "at " + hour +
		 * ":" + minute + " " + ampm + "\n"; if (repeatsFor.trim() != "0") text +=
		 * "EVENT REPEATS FOR " + repeatsFor + " DAYS.\n"; text +=
		 * "----------------------------------------------------------------\n";
		 * text += "Mail notification generated on " + (new
		 * java.util.Date()).toGMTString();
		 * 
		 * System.out.println(text); mailer.setSubject("Workflow Event
		 * Notification");
		 * 
		 * mailer.setMailText(text);
		 * 
		 * System.out.println(rlist); if (sendEmail) {
		 * 
		 * if (bccEmail) { System.out.println("Will send bcc email");
		 * mailer.setBCCs(rlist); } else { System.out.println("Will send TO
		 * email"); mailer.setRecipients(rlist); } if (rlist.trim().length() !=
		 * 0) try { //mailer.send();
		 * NotificationMsgHelper.sendEMail(loggedUser,StringUtil.EMPTY_STRING,StringUtil.EMPTY_STRING,rlist,Global.mailHost,text,"Workflow
		 * Event Notification",loggedUser); } catch (MessagingException e) {
		 * 
		 * e.printStackTrace(); return false; } catch(Exception e) {
		 * e.printStackTrace(); return false; } }
		 * 
		 * ////MAIL SENT////
		 */

		//// BEGIN INSERTION OF THE EVENT INTO THE CALENDAR DATABASE ////
		
		//// END INSERTION OF THE EVENT INTO THE CALENDAR DATABASE ////

		System.out.println("Exit BL with true");
		return true;

	}
}
