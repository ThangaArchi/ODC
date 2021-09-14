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

public class Event {
	private String calendar_id = "";

	private String meetingDate = "";

	private String scheduledBy = "";

	private String project_id = "";

	private String scheduleDate = "";

	private String invitees_id = "";

	private char notify_type = 'X';

	private String wf_id = "";

	public String getInvitees_id() {
		return invitees_id;
	}

	public void setInvitees_id(String invitees_id) {
		this.invitees_id = invitees_id;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getScheduledBy() {
		return scheduledBy;
	}

	public void setScheduledBy(String scheduledBy) {
		this.scheduledBy = scheduledBy;
	}

	public String getCalendar_id() {
		return calendar_id;
	}

	public void setCalendar_id(String calendar_id) {
		this.calendar_id = calendar_id;
	}

	public String getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}

	public char getNotify_type() {
		return notify_type;
	}

	public void setNotify_type(char notify_type) {
		this.notify_type = notify_type;
	}
	/**
	 * @return Returns the wf_id.
	 */
	public String getWf_id() {
		return wf_id;
	}
	/**
	 * @param wf_id The wf_id to set.
	 */
	public void setWf_id(String wf_id) {
		this.wf_id = wf_id;
	}
}
