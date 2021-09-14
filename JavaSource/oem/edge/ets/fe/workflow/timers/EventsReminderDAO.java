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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;

import oem.edge.common.DbConnect;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.dao.DBHelperException;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueDAO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.DBHandler;
import oem.edge.ets.fe.workflow.util.HistoryUtils;

public class EventsReminderDAO {
	private static Log logger = WorkflowLogger
			.getLogger(EventsReminderDAO.class);

	public boolean updateIssuePastDueStatus() throws SQLException, Exception {
		DbConnect db = null;
		DBAccess db1 = null;
		try {
			db1 = new DBAccess();
			db = new DbConnect();
			db.makeConn("etsds");
			String s1 = "select distinct a.issue_id, b.wf_id, b.project_id from ets.wf_issue a, ets.wf_issue_wf_map b where a.issue_id = b.issue_id and status in ('NOT PAST DUE') and current_date > target_date with ur";

			Vector vec = DBHandler.getVQueryResult(db.conn, s1, 3);
			String s[] = null;
			for (int i = 0; i < vec.size(); i++) {
				s = (String[]) vec.elementAt(i);
				System.out.println("issue_id:" + s[0] + " wf_id:" + s[1] + " project_id:" + s[2]);
				EditIssueDAO editIssueDAO = new EditIssueDAO();
				editIssueDAO.updateStatus(s[0], db1, s[1]);
				String histID = HistoryUtils.enterHistory(s[2],s[1],s[0],HistoryUtils.ACTION_CREATE,"SYS_USERID","issue status set to PAST DUE by the SYSTEM",db1);
				db1.doCommit();
			}
		} finally {
			try {
				db.closeConn();
				if (db1 != null) {
					db1.doCommit();
					db1.close();
					db1 = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN getMSADueDateMeetings()");
			}
		}
		return true;
	}

	public ArrayList getMSADueDateMeetings(int daysToGo) throws SQLException {
		ArrayList eventsList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select c.project_id, "
				+ "c.acct_contact, c.wf_id from "
				+ "ets.wf_def c, ets.WF_STAGE_DOCUMENT_SETMET d where "
				+ "c.project_id = d.project_id " + "and c.wf_id = d.wf_id "
				+ "and c.wf_type = 'SELF ASSESSMENT' "
				+ "and int(d.NEXT_MEETING_DATE - current date) = " + daysToGo
				+ " with ur";

		logger.debug("MSA next meetings due date Query===>" + selectQuery);
		System.out
				.println("MSA next meetings due date Query===>" + selectQuery);
		try {
			con = dbAccess.getConnection();
			stmt = con.prepareStatement(selectQuery);

			resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				logger.debug("In the while loop***************");
				Event event = new Event();
				//String calendar_id = resultSet.getString("calendar_id");
				//event.setCalendar_id(calendar_id);
				//String schedule_date = resultSet.getString("schedule_date");
				//event.setScheduleDate(schedule_date);
				//String scheduled_by = resultSet.getString("SCHEDULED_BY");
				//event.setScheduledBy(scheduled_by);
				String project_id = resultSet.getString("project_id");
				event.setProject_id(project_id);
				String acct_contact = resultSet.getString("acct_contact");
				event.setInvitees_id(acct_contact);
				event.setScheduledBy(acct_contact);
				//String notifyType = resultSet.getString("notify_type");
				event.setWf_id(resultSet.getString("wf_id"));
				event.setNotify_type('N');
				eventsList.add(event);
				logger.debug("In the while loop***************");
			}

		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (stmt != null)
					stmt.close();
				if (dbAccess != null) {
					dbAccess.doCommit();
					dbAccess.close();
					dbAccess = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN getMSADueDateMeetings()");
			}
		}
		logger.debug("getMSADueDateMeetings() method is completed ");
		return eventsList;
	}

	/**
	 * The method returns all the events which are about to happen wiuthin given
	 * days as parameters daysToGo
	 * 
	 * @param daysToGo
	 * @return java.util.ArrayList
	 * @throws SQLException
	 */
	public synchronized ArrayList getEvents(int daysToGo) throws SQLException {
		ArrayList eventsList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select calendar_id, schedule_date, SCHEDULED_BY, a.project_id, "
				+ "invitees_id, notify_type, a.wf_id from "
				+ "ets.ets_calendar a, ets.wf_def c where "
				+ "a.project_id = c.project_id and a.wf_id = c.wf_id "
				+ "and calendar_type = 'E' and (CANCEL_FLAG = 'N' OR CANCEL_FLAG is null) "
				+ "and int(date(start_time)- current date) = "
				+ daysToGo
				+ " with ur";

		logger.debug("eventsQuery===>" + selectQuery);
		System.out.println("eventsQuery===>" + selectQuery);
		try {
			con = dbAccess.getConnection();
			stmt = con.prepareStatement(selectQuery);

			resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				logger.debug("In the while loop***************");
				Event event = new Event();
				String calendar_id = resultSet.getString("calendar_id");
				event.setCalendar_id(calendar_id);
				String schedule_date = resultSet.getString("schedule_date");
				event.setScheduleDate(schedule_date);
				String scheduled_by = resultSet.getString("SCHEDULED_BY");
				event.setScheduledBy(scheduled_by);
				String project_id = resultSet.getString("project_id");
				event.setProject_id(project_id);
				String invitees_id = resultSet.getString("invitees_id");
				event.setInvitees_id(invitees_id);
				String notifyType = resultSet.getString("notify_type");
				event.setWf_id(resultSet.getString("wf_id"));

				char notify_type = 'N';
				if (notifyType != null) {
					notify_type = notifyType.toCharArray()[0];
					event.setNotify_type(notify_type);
				}
				eventsList.add(event);
				logger.debug("In the while loop***************");
			}

		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (stmt != null)
					stmt.close();
				if (dbAccess != null) {
					dbAccess.doCommit();
					dbAccess.close();
					dbAccess = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN getEvents()");
			}
		}
		logger.debug("getEvents() method is completed ");
		return eventsList;
	}

	/**
	 * The method returns all the events in the form collection objects of type
	 * Event
	 * 
	 * @param issueAssignedDaysAgo
	 * @return java.util.ArrayList
	 * @throws SQLException
	 */
	public synchronized ArrayList getIssues(int issueAssignedDaysAgo)
			throws SQLException {
		ArrayList issuesList = new ArrayList();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String selectQuery = "select a.issue_id, b.owner_id, c.project_id, "
				+ "c.wf_id, a.issue_contact, b.assigned_date   "
				+ "from ets.wf_issue a, ets.wf_issue_owner b, ets.wf_issue_wf_map c "
				+ "where a.status in ('ASSIGNED','NOT PAST DUE','PAST DUE') "
				+ "and a.issue_id=b.issue_id and c.issue_id=a.issue_id "
				+ "and b.ownership_state is null "
				+ "and (current date - b.assigned_date) >= "
				+ issueAssignedDaysAgo + " with ur";

		logger.debug("Issues select query===>" + selectQuery);
		System.out.println("Issues select query===>" + selectQuery);

		try {
			con = dbAccess.getConnection();
			resultSet = con.prepareStatement(selectQuery).executeQuery();

			while (resultSet.next()) {

				Issue issue = new Issue();
				String issue_id = resultSet.getString("issue_id");
				issue.setIssueId(issue_id);
				String owner_id = resultSet.getString("owner_id");
				issue.setOwnerId(owner_id);
				String project_id = resultSet.getString("project_id");
				issue.setProjectId(project_id);
				String wf_id = resultSet.getString("wf_id");
				issue.setWorkflowId(wf_id);
				String issue_contact = resultSet.getString("issue_contact");
				issue.setIssueContact(issue_contact);
				issuesList.add(issue);
			}
			logger.debug("issuesList==>" + issuesList.size());
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (stmt != null)
					stmt.close();
				if (dbAccess != null) {
					dbAccess.doCommit();
					dbAccess.close();
					dbAccess = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN getIssues()");
			}
		}
		logger.debug("getIssues() method is completed ");
		return issuesList;
	}

	/**
	 * The method updates the ownership state of an issue owner to accepted
	 * 
	 * @param issueId
	 * @param ownerId
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean updateOwnershipState(String issueId, String ownerId)
			throws SQLException {
		boolean status = false;
		Connection con = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		DBAccess dbAccess = new DBAccess();

		String updateQuery = "update ets.wf_issue_owner set ownership_state='ACCEPTED' where issue_id='"
				+ issueId
				+ "' and owner_id='"
				+ ownerId
				+ "' and ownership_state is null";

		logger.debug("eventsQuery===>" + updateQuery);
		try {
			con = dbAccess.getConnection();
			stmt = con.createStatement();

			int rowsAffected = stmt.executeUpdate(updateQuery);
			if (rowsAffected >= 0) {
				status = true;
				con.commit();
			}
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (stmt != null)
					stmt.close();
				if (dbAccess != null) {
					dbAccess.doCommit();
					dbAccess.close();
					dbAccess = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception IN updateOwnershipState()");
			}
		}
		logger.debug("updateOwnershipState() method is completed ");
		return status;
	}
}
