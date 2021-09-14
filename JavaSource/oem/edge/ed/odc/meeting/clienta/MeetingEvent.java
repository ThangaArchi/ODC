package oem.edge.ed.odc.meeting.clienta;

import java.util.Vector;
import oem.edge.ed.odc.dsmp.common.*;

/**
 * Insert the type's description here.
 * Creation date: (7/28/2002 12:59:26 PM)
 * @author: Mike Zarnick
 */
public class MeetingEvent extends DSMPEvent {
	static public int LOGIN = 0;
	static public int LOGIN_FAILED = 1;
	static public int LOGOUT = 2;
	static public int LOGOUT_FAILED = 3;
	static public int START = 4;
	static public int START_FAILED = 5;
	static public int LEAVE = 6;
	static public int LEAVE_FAILED = 7;
	static public int END = 8;
	static public int END_FAILED = 9;
	static public int GET = 10;
	static public int GET_FAILED = 11;
	static public int INVITE_FAILED = 12;
	static public int JOIN = 13;
	static public int JOIN_FAILED = 14;
	static public int DROP_FAILED = 15;
	static public int CHAT_FAILED = 16;
	static public int INVITE_ACTION = 17;
	static public int FORCED = 18;
	static public int ARRIVE_MSG = 19;
	static public int DEPART_MSG = 20;
	static public int CONTROL = 21;
	static public int CONTROL_FAILED = 22;
	static public int FREEZE = 23;
	static public int THAW = 24;
	static public int FREEZE_FAILED = 25;
	static public int STOP_SHARE = 26;
	static public int FREEZING = 27;
	static public int DEATH = 666;
	public int inviteID;
	public int loginID;
	public int meetingID;
	public int participantID;
	public Vector vdata;
	public String message;
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle) {
	super(reason, flags, handle);
}
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle, int meetingID) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
}
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle, int d1, int d2) {
	super(reason, flags, handle);

	if (reason == JOIN)
		this.inviteID = d1;
	else // reason == START or CONTROL
		this.meetingID = d1;

	this.participantID = d2;
}
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle, int loginID, Vector projects) {
	super(reason, flags, handle);
	this.loginID = loginID;
	this.vdata = projects;
}
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle, String msg) {
	super(reason, flags, handle);
	this.message = msg;
}
/**
 * MeetingEvent constructor comment.
 * @param f byte
 * @param h byte
 */
public MeetingEvent(int reason, byte flags, byte handle, Vector invitations) {
	super(reason, flags, handle);
	this.loginID = loginID;
	this.vdata = invitations;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:12:47 PM)
 * @return java.util.Vector
 */
public Vector getInvites() {
	if (reason == GET)
		return vdata;

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:12:47 PM)
 * @return java.util.Vector
 */
public Vector getProjects() {
	if (reason == LOGIN)
		return vdata;

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isArrivalMessage() {
	return reason == ARRIVE_MSG;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isChatFailed() {
	return reason == CHAT_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isControl() {
	return reason == CONTROL;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isControlFailed() {
	return reason == CONTROL_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isDeath() {
	return reason == DEATH;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isDepartMessage() {
	return reason == DEPART_MSG;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isDropFailed() {
	return reason == DROP_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isEnd() {
	return reason == END;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isEndFailed() {
	return reason == END_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isForcedLeave() {
	return reason == FORCED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isFreeze() {
	return reason == FREEZE;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isFreezeFailed() {
	return reason == FREEZE_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isFreezing() {
	return reason == FREEZING;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isGet() {
	return reason == GET;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isGetFailed() {
	return reason == GET_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isInviteAction() {
	return reason == INVITE_ACTION;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isInviteFailed() {
	return reason == INVITE_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isJoin() {
	return reason == JOIN;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isJoinFailed() {
	return reason == JOIN_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isLeave() {
	return reason == LEAVE;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isLeaveFailed() {
	return reason == LEAVE_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isLogin() {
	return reason == LOGIN;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isLoginFailed() {
	return reason == LOGIN_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isLogout() {
	return reason == LOGOUT;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isLogoutFailed() {
	return reason == LOGOUT_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isStart() {
	return reason == START;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isStartFailed() {
	return reason == START_FAILED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:03 PM)
 * @return boolean
 */
public boolean isStopShare() {
	return reason == STOP_SHARE;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:53:35 PM)
 * @return boolean
 */
public boolean isThaw() {
	return reason == THAW;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 11:45:47 AM)
 */
public String toString() {
	return "MeetingEvent: r=" + reason + ",f=" + flags + ",iID=" + inviteID + ",mID=" +
			meetingID + ",lID=" + loginID + ",pID=" + participantID + ",msg=" + message;
}
}
