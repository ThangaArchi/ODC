/* Copyright Header Check */
/* -------------------------------------------------------------------- */
/*                                                                          */
/* OCO Source Materials */
/*                                                                          */
/* Product(s): PROFIT */
/*                                                                          */
/* (C)Copyright IBM Corp. 2005-2008 */
/*                                                                          */
/* All Rights Reserved */
/* US Government Users Restricted Rigts */
/*                                                                          */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the US Copyright Office. */
/*                                                                          */
/* -------------------------------------------------------------------- */
/* Please do not remove any of these commented lines 20 lines */
/* -------------------------------------------------------------------- */
/* Copyright Footer Check */

package oem.edge.ets.fe.workflow.timers;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.common.DbConnect;
import oem.edge.ets.fe.ETSCalendar;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.WorkflowDBException;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueBL;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueDAO;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueVO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.notification.EventNotificationParams;
import oem.edge.ets.fe.workflow.notification.IssueNotificationParams;
import oem.edge.ets.fe.workflow.notification.NotificationConstants;
import oem.edge.ets.fe.workflow.notification.Notifier;
import oem.edge.ets.fe.workflow.notification.NotifierFactory;
import oem.edge.ets.fe.workflow.notification.UnsupportedNotificationException;
import oem.edge.ets.fe.workflow.util.DBHandler;
import oem.edge.ets.fe.workflow.util.HistoryUtils;

import org.apache.commons.logging.Log;

public class EventsReminder implements NotificationConstants {
	private static Log logger = WorkflowLogger.getLogger(EventsReminder.class);

	public boolean closeWFAfterRatingPeriod() {
		System.out.println("Begin closeWFAfterRatingPeriod()");
		logger.debug("Begin closeWFAfterRatingPeriod()");
		DBAccess dbAccess = null;
		DbConnect db = new DbConnect();
		StringBuffer sbuf = new StringBuffer();

		try {
			db.makeConn("etsds");
			sbuf
					.append("select distinct a.project_id, a.wf_id, a.wf_curr_stage_name from ets.wf_def a, ets.WF_STAGE_IDENTIFY_SETMET b ");
			sbuf
					.append("where a.project_id = b.project_id and a.wf_id = b.wf_id ");
			sbuf
					.append("and a.WF_CURR_STAGE_NAME NOT in ('Closed','Cancelled', 'Complete') ");
			sbuf.append("and a.wf_type in ('QBR','SELF ASSESSMENT') ");
			sbuf
					.append("and current date > RATING_PERIOD_TO + 1 months with ur ");

			Vector wf_vec = DBHandler.getVQueryResult(db.conn, sbuf.toString(),
					3);
			String sb1 = new String();
			String[] temp = null;
			String projectID = "";
			String workflowID = "";
			String curr_stage = "";

			dbAccess = new DBAccess();
			for (int i = 0; i < wf_vec.size(); i++) {
				System.out.println("INSIDE LOOP");
				temp = (String[]) wf_vec.elementAt(i);
				projectID = temp[0];
				workflowID = temp[1];
				curr_stage = temp[2];

				sb1 = "update ets.wf_def set WF_CURR_STAGE_NAME = 'Closed' where wf_id = '"
						+ workflowID + "'";
				int updt_cnt = DBHandler.fireUpdate(db.conn, sb1);

				System.out.println("updt_cnt:" + updt_cnt);

				String historyID = HistoryUtils.enterHistory(projectID,
						workflowID, workflowID,
						HistoryUtils.ACTION_GENERIC_SETMET, "SYS_USER",
						"Workflow moved into Closed Stage", dbAccess);
				HistoryUtils.addHistoryField(historyID, projectID,
						"Workflow Stage", curr_stage, "Closed", dbAccess);
				dbAccess.doCommit();
			}
		} catch (SQLException sqlEx) {
			logger.error(sqlEx);
			sqlEx.printStackTrace();
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}

		finally {
			db.closeConn();
			try {
				if (dbAccess != null) {
					dbAccess.doCommit();
					dbAccess.close();
					dbAccess = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN closeWFAfterRatingPeriod()");
			}
		}
		return true;
	}

	public boolean notifyMSAMeetingDueDate() throws Exception {
		System.out.println("Begin notifyMSAMeetingDueDate()");
		logger.debug("Begin notifyMSAMeetingDueDate()");
		boolean status = false;
		DBAccess dbAccess1 = new DBAccess();

		ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
		Enumeration e = rb.getKeys();

		String daysToGoString = rb.getString("ets.msa_meeting_reminder_days")
				.trim();

		try {
			int daysToGo = Integer.parseInt(daysToGoString);

			EventsReminderDAO eventsReminderDAO = new EventsReminderDAO();

			ArrayList events = eventsReminderDAO
					.getMSADueDateMeetings(daysToGo);
			Iterator eventsIterator = events.iterator();
			logger.debug("MSA next due date query are " + events.size());
			while (eventsIterator.hasNext()) {
				Event event = (Event) eventsIterator.next();
				EventNotificationParams eventParams = new EventNotificationParams();
				//eventParams.setCalendarID(event.getCalendar_id());
				eventParams
						.setEventType(NotificationConstants.EVT_WORKFLOW_MSA_NEXT_DUEDATE_REMINDER);
				eventParams.setProjectID(event.getProject_id());
				eventParams.setLoggedUser(event.getScheduledBy());
				eventParams
						.setNotificationType(NotificationConstants.NT_WORKFLOW);
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, 5);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				String due_date = sdf.format(c.getTime());
				logger.debug("MSA next Due Date is " + due_date);
				eventParams.setMsa_next_duedate(due_date);

				String company = DBHandler.getValue(dbAccess1.getConnection(),
						"select company from ets.ets_projects where project_id = '"
								+ event.getProject_id() + "' with ur");
				eventParams.setCompany(company);
				String stage = DBHandler.getValue(dbAccess1.getConnection(),
						"select WF_CURR_STAGE_NAME from ets.wf_def where project_id = '"
								+ event.getProject_id() + "' and wf_id = '"
								+ event.getWf_id() + "' with ur");
				eventParams.setWf_curr_stage_name(stage);
				eventParams.setWf_id(event.getWf_id());

				try {
					logger.debug("Trying to send mail..............");
					Notifier notifier = NotifierFactory
							.getNotificationSender(eventParams);
					notifier.init(eventParams, dbAccess1);
					notifier
							.send(
									NotificationConstants.RECIPIENT_WORKFLOW_ACCOUNT_CONTACT,
									event.getInvitees_id(), false, dbAccess1);
					logger.debug("Email has been sent........");

				} catch (UnsupportedNotificationException unEx) {
					unEx.printStackTrace();
				}
			}
		} catch (SQLException sqlEx) {
			logger.error(sqlEx);
			sqlEx.printStackTrace();
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} finally {
			try {
				if (dbAccess1 != null) {
					dbAccess1.doCommit();
					dbAccess1.close();
					dbAccess1 = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN notifyMSAMeetingDueDate()");
			}
		}
		status = true;
		return status;
	}

	/**
	 * The method gets all the events which are about to happen within one or
	 * three or five days and sends the emails to the events related persons for
	 * the event.
	 * 
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean processAllEvents() {
		System.out.println("Begin processAllEvents()");
		logger.debug("Begin processAllEvents()");
		boolean status = false;
		DBAccess dbAccess1 = new DBAccess();

		ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
		Enumeration e = rb.getKeys();

		//String daysToGoString = "1,3,5";
		String daysToGoString = rb.getString("ets.event_reminder_frequency")
				.trim();

		StringTokenizer tokenizer = new StringTokenizer(daysToGoString, ",");

		try {
			while (tokenizer.hasMoreTokens()) {
				try {
					int daysToGo = Integer.parseInt(tokenizer.nextToken());

					EventsReminderDAO eventsReminderDAO = new EventsReminderDAO();

					ArrayList events = eventsReminderDAO.getEvents(daysToGo);
					Iterator eventsIterator = events.iterator();
					logger.debug("events for this query are " + events.size());
					while (eventsIterator.hasNext()) {
						Event event = (Event) eventsIterator.next();
						EventNotificationParams eventParams = new EventNotificationParams();
						eventParams.setCalendarID(event.getCalendar_id());
						eventParams
								.setEventType(NotificationConstants.EVT_WORKFLOW_REMINDER);
						eventParams.setProjectID(event.getProject_id());
						eventParams.setLoggedUser(event.getScheduledBy());
						eventParams
								.setNotificationType(NotificationConstants.NT_WORKFLOW);

						String company = DBHandler.getValue(dbAccess1
								.getConnection(),
								"select company from ets.ets_projects where project_id = '"
										+ event.getProject_id() + "' with ur");
						eventParams.setCompany(company);
						String stage = DBHandler.getValue(dbAccess1
								.getConnection(),
								"select WF_CURR_STAGE_NAME from ets.wf_def where project_id = '"
										+ event.getProject_id()
										+ "' and wf_id = '" + event.getWf_id()
										+ "' with ur");
						eventParams.setWf_curr_stage_name(stage);
						eventParams.setWf_id(event.getWf_id());

						try {
							logger.debug("Trying to send mail..............");
							Notifier notifier = NotifierFactory
									.getNotificationSender(eventParams);
							notifier.init(eventParams, dbAccess1);
							notifier
									.send(
											NotificationConstants.RECIPIENT_WORKFLOW_GENERAL,
											event.getInvitees_id(), false,
											dbAccess1);
							logger.debug("Email has been sent........");

						} catch (UnsupportedNotificationException unEx) {
							unEx.printStackTrace();
						}
					}
				} catch (SQLException sqlEx) {
					logger.error(sqlEx);
					sqlEx.printStackTrace();
				} catch (Exception ex) {
					logger.error(ex);
					ex.printStackTrace();
				}
				status = true;
			}
		} finally {
			try {
				if (dbAccess1 != null) {
					dbAccess1.doCommit();
					dbAccess1.close();
					dbAccess1 = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN processAllEvents()");
			}
		}
		return status;
	}

	/**
	 * The method processes all the issues which are not accepted by the owner
	 * after the issue has been assigned within five days.
	 * 
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean processAllIssues() throws Exception {
		System.out.println("Begin processAllIssues()");
		logger.debug("Begin processAllIssues()");
		boolean status = false;
		EditIssueBL editIssueBL = new EditIssueBL();
		DBAccess db1 = new DBAccess();
		String temp_issue_id = "";
		String temp_wf_id = "";
		Notifier ntf = new Notifier();
		int i = 0;
		try {
			EditIssueDAO editIssueDAO = new EditIssueDAO();
			EventsReminderDAO eventsReminderDAO = new EventsReminderDAO();

			//update all PAST DUE status to all issues
			eventsReminderDAO.updateIssuePastDueStatus();

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
			Enumeration e = rb.getKeys();

			String auto_assign_days = rb
					.getString("ets.issue_auto_assign_days").trim();

			int assignedDays = Integer.parseInt(auto_assign_days);

			ArrayList issuesList = eventsReminderDAO.getIssues(assignedDays);

			Iterator issuesListIterator = issuesList.iterator();

			while (issuesListIterator.hasNext()) {
				Issue issue = (Issue) issuesListIterator.next();
				String issue_id = issue.getIssueId();
				logger.debug("issue_id===>" + issue_id);

				//editIssueDAO.loggedUser = "jeetrao@us.ibm.com";
				editIssueDAO.loggedUser = issue.getIssueContact();
				EditIssueVO editIssueVO = new EditIssueVO();

				editIssueVO.setUserid(issue.getOwnerId());
				editIssueVO
						.setComment("System made the issue owner status as accepted for "
								+ issue.getOwnerId());
				editIssueVO.setProjectID(issue.getProjectId());
				editIssueVO.setWorkflowID(issue.getWorkflowId());
				editIssueVO.setIssueID(issue.getIssueId());
				editIssueVO
						.setFocalPt(new String[] { issue.getIssueContact() });
				editIssueVO.setDB(db1);

				eventsReminderDAO.updateOwnershipState(issue.getIssueId(),
						issue.getOwnerId());

				HistoryUtils.enterHistory(issue.getProjectId(), issue
						.getWorkflowId(), issue.getIssueId(),
						HistoryUtils.ACTION_ACCEPTED, issue.getIssueContact(),
						editIssueVO.getComment(), db1);

				if (!temp_issue_id.equals(issue.getIssueId())) {
					if (!temp_issue_id.equals("")) {
						logger.debug("temp_issue_id===>" + temp_issue_id);
						i++;
						editIssueDAO.updateStatus(temp_issue_id, db1, issue
								.getWorkflowId());
					}

					IssueNotificationParams params = new IssueNotificationParams();
					params.setProjectID(issue.getProjectId());
					params.setWorkflowID(issue.getWorkflowId());
					//params.setTc("0000");
					params.setIssueID(issue.getIssueId());
					params.setLoggedUser(editIssueDAO.loggedUser);
					ArrayList temp = new ArrayList();
					temp.add(issue.getOwnerId());
					params.setNotificationType(NT_ISSUE);
					params.setEventType(EVT_ISSUE_ACCEPTED_DAEMON);
					ntf = NotifierFactory.getNotificationSender(params);
					ntf.init(params, db1);
					logger.debug("printed the issue id************");

				}
				ntf.send(RECIPIENT_ISSUE_OWNER, issue.getOwnerId(), false, db1);
				temp_issue_id = issue.getIssueId();
				temp_wf_id = issue.getWorkflowId();
			}
			if (!temp_issue_id.equals("")) {
				editIssueDAO.updateStatus(temp_issue_id, db1, temp_wf_id);
				logger
						.debug("here the end of the updations to the issues*******************"
								+ i);
			}

		} catch (SQLException sqlEx) {
			logger.error(sqlEx);
			sqlEx.printStackTrace();
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} finally {
			try {
				if (db1 != null) {
					db1.doCommit();
					db1.close();
					db1 = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN processAllIssues()");
			}
		}
		return status;
	}
}
