package oem.edge.ed.odc.meeting.client;

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
	static public int STOP_SHARING = 26;
	static public int FREEZING = 27;
	static public int CONTROL_EVENT = 28;
	static public int OWNER = 29;
	static public int OWNER_FAILED = 30;
	static public int MODERATOR_EVENT = 31;
	static public int OWNER_EVENT = 32;
	static public int STOP_SHARE_EVENT = 33;
	static public int STOP_SHARE_FAILED = 34;
	static public int MODERATOR = 35;
	static public int MODERATOR_FAILED = 36;
	static public int STOP_SHARE = 37;
	static public int START_SHARE = 38;
	static public int START_SHARE_FAILED = 39;
	static public int START_SHARE_EVENT = 40;
	static public int ACCEPT_CALL = 41;
	static public int ACCEPT_CALL_FAILED = 42;
	static public int ACCEPT_CALL_EVENT = 43;
	static public int URL = 44;
	static public int URL_FAILED = 45;
	static public int OPTIONS = 46;
	static public int OPTIONS_FAILED = 47;
	static public int OPTIONS_EVENT = 48;
	static public int PLACE_CALL = 49;
	static public int PLACE_CALL_FAILED = 50;
	static public int PLACE_CALL_EVENT = 51;
	static public int DEATH = 666;
	public int inviteID;
	public int loginID;
	public int meetingID;
	public int participantID;
	public int toPID;
	public Vector vdata;
	public String message;
	public boolean addOrRemove;
	public String user;
	public String company;
	public String url;
	public String key;
	public String sdata;
/**
 * MeetingEvent constructors
 */
public MeetingEvent(int reason, byte flags, byte handle) {
	super(reason, flags, handle);
}
public MeetingEvent(int reason, byte flags, byte handle, int meetingID) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
}
public MeetingEvent(int reason, byte flags, byte handle, int meetingID, String url) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
	this.url = url;
}
public MeetingEvent(int reason, byte flags, byte handle, int d1, int d2) {
	super(reason, flags, handle);

	if (reason == JOIN)
		this.inviteID = d1;
	else // reason == START or CONTROL
		this.meetingID = d1;

	this.participantID = d2;
}
public MeetingEvent(int reason, byte flags, byte handle, int mID, int fromPID, int toPID) {
	super(reason, flags, handle);
	this.meetingID = mID;
	this.participantID = fromPID;
	this.toPID = toPID;
}
public MeetingEvent(int reason, byte flags, byte handle, int mID, int pID, boolean addOrRemove) {
	super(reason, flags, handle);
	this.meetingID = mID;
	this.participantID = pID;
	this.addOrRemove = addOrRemove;
}
public MeetingEvent(int reason, byte flags, byte handle, int meetingID, String key, String val) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
	this.key = key;
	this.sdata = val;
}
public MeetingEvent(int reason, byte flags, byte handle, int meetingID, int loginID, String user, String company) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
	this.loginID = loginID;
	this.user = user;
	this.company = company;
}
public MeetingEvent(int reason, byte flags, byte handle, int meetingID, int participantID, String user, String company, String salt) {
	super(reason, flags, handle);
	this.meetingID = meetingID;
	this.participantID = participantID;
	this.user = user;
	this.company = company;
	this.sdata = salt;
}
public MeetingEvent(int reason, byte flags, byte handle, int loginID, String user, String company, Vector projects) {
	super(reason, flags, handle);
	this.loginID = loginID;
	this.user = user;
	this.company = company;
	this.vdata = projects;
}
public MeetingEvent(int reason, byte flags, byte handle, String msg) {
	super(reason, flags, handle);
	this.message = msg;
}
public MeetingEvent(int reason, byte flags, byte handle, Vector invitations) {
	super(reason, flags, handle);
	this.vdata = invitations;
}

/*
 * Event descriptor methods.
 */
 
/*
 * Authentication
 */ 
public boolean isLogin() {
	return reason == LOGIN;
}
public boolean isLoginFailed() {
	return reason == LOGIN_FAILED;
}
public boolean isLogout() {
	return reason == LOGOUT;
}
public boolean isLogoutFailed() {
	return reason == LOGOUT_FAILED;
}
public Vector getProjects() {
	if (reason == LOGIN)
		return vdata;

	return null;
}

/*
 * Meeting start, join, leave, end and options
 */
public boolean isStart() {
	return reason == START;
}
public boolean isStartFailed() {
	return reason == START_FAILED;
}
public boolean isJoin() {
	return reason == JOIN;
}
public boolean isJoinFailed() {
	return reason == JOIN_FAILED;
}
public boolean isLeave() {
	return reason == LEAVE;
}
public boolean isLeaveFailed() {
	return reason == LEAVE_FAILED;
}
public boolean isForcedLeave() {
	return reason == FORCED;
}
public boolean isEnd() {
	return reason == END;
}
public boolean isEndFailed() {
	return reason == END_FAILED;
}
public boolean isOptions() {
	return reason == OPTIONS;
}
public boolean isOptionsFailed() {
	return reason == OPTIONS_FAILED;
}
public boolean isOptionsEvent() {
	return reason == OPTIONS_EVENT;
}

/*
 * Invitations
 */
public boolean isInviteAction() {
	return reason == INVITE_ACTION;
}
public boolean isInviteFailed() {
	return reason == INVITE_FAILED;
}
public boolean isDropFailed() {
	return reason == DROP_FAILED;
}
public boolean isGet() {
	return reason == GET;
}
public boolean isGetFailed() {
	return reason == GET_FAILED;
}
public Vector getInvites() {
	if (reason == GET)
		return vdata;

	return null;
}
public boolean isArrivalMessage() {
	return reason == ARRIVE_MSG;
}
public boolean isDepartMessage() {
	return reason == DEPART_MSG;
}

/*
 * Messaging
 */
public boolean isChatFailed() {
	return reason == CHAT_FAILED;
}

/*
 * Owner, moderator, controller
 */
public boolean isOwner() {
	return reason == OWNER;
}
public boolean isOwnerFailed() {
	return reason == OWNER_FAILED;
}
public boolean isOwnerEvent() {
	return reason == OWNER_EVENT;
}
public boolean isModerator() {
	return reason == MODERATOR;
}
public boolean isModeratorFailed() {
	return reason == MODERATOR_FAILED;
}
public boolean isModeratorEvent() {
	return reason == MODERATOR_EVENT;
}
public boolean isControl() {
	return reason == CONTROL;
}
public boolean isControlFailed() {
	return reason == CONTROL_FAILED;
}
public boolean isControlEvent() {
	return reason == CONTROL_EVENT;
}

/*
 * Screen start/stop share, freeze/thaw
 */
public boolean isStartShare() {
	return reason == START_SHARE;
}
public boolean isStartShareFailed() {
	return reason == START_SHARE_FAILED;
}
public boolean isStartShareEvent() {
	return reason == START_SHARE_EVENT;
}
public boolean isStopShare() {
	return reason == STOP_SHARE;
}
public boolean isStopSharing() {
	return reason == STOP_SHARING;
}
public boolean isStopShareFailed() {
	return reason == STOP_SHARE_FAILED;
}
public boolean isStopShareEvent() {
	return reason == STOP_SHARE_EVENT;
}
public boolean isFreeze() {
	return reason == FREEZE;
}
public boolean isFreezeFailed() {
	return reason == FREEZE_FAILED;
}
public boolean isFreezing() {
	return reason == FREEZING;
}
public boolean isThaw() {
	return reason == THAW;
}

/*
 * URL, Calling
 */
public boolean isAcceptCall() {
	return reason == ACCEPT_CALL;
}
public boolean isAcceptCallFailed() {
	return reason == ACCEPT_CALL_FAILED;
}
public boolean isAcceptCallEvent() {
	return reason == ACCEPT_CALL_EVENT;
}
public boolean isPlaceCall() {
	return reason == PLACE_CALL;
}
public boolean isPlaceCallFailed() {
	return reason == PLACE_CALL_FAILED;
}
public boolean isPlaceCallEvent() {
	return reason == PLACE_CALL_EVENT;
}
public boolean isURL() {
	return reason == URL;
}
public boolean isURLFailed() {
	return reason == URL_FAILED;
}

/*
 * Misc.
 */
public boolean isDeath() {
	return reason == DEATH;
}
public String toString() {
	return "MeetingEvent: r=" + reason + ",f=" + flags + ",iID=" + inviteID + ",mID=" +
			meetingID + ",lID=" + loginID + ",pID=" + participantID + ",msg=" + message;
}
}