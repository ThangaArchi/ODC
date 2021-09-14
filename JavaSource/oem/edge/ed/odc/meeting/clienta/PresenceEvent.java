package oem.edge.ed.odc.meeting.clienta;

import oem.edge.ed.odc.dsmp.common.*;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 9:52:01 AM)
 * @author: Mike Zarnick
 */
public class PresenceEvent extends DSMPEvent {
	static public int INVITE = 0;
	static public int ARRIVE = 1;
	static public int DROP = 2;
	public int meetingID;
	public int participantID;
	public int inviteID;
	public int loginID;
	public char[] name;
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:27:22 PM)
 * @param type int
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param inviteID int
 * @param participantID int
 * @param loginID int
 * @param name java.lang.String
 */
public PresenceEvent(int type, byte flags, byte handle, int mID, int iID, int pID, int lID, char[] name) {
	super(type,flags,handle);
	this.meetingID = mID;
	this.inviteID = iID;
	this.participantID = pID;
	this.loginID = lID;
	this.name = name;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isArrival() {
	return (reason == ARRIVE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isDeparture() {
	return isDrop() && ((flags & 0x02) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isDrop() {
	return (reason == DROP);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isInvite() {
	return (reason == INVITE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isInviteDeparture() {
	return isDrop() && ((flags & 0x01) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isOwner() {
	return isArrival() && ((flags & 0x02) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isProjectArrival() {
	return isArrival() && ((flags & 0x01) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isProjectInvite() {
	return isInvite() && (flags == 1);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isUserArrival() {
	return isArrival() && ((flags & 0x01) == 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isUserDeparture() {
	return isDrop() && ((flags & 0x02) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:29:38 PM)
 * @return boolean
 */
public boolean isUserInvite() {
	return isInvite() && (flags == 0);
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 11:45:47 AM)
 */
public String toString() {
	return "PresenceEvent: r=" + reason + ",f=" + flags + ",iID=" + inviteID + ",mID=" +
			meetingID + ",lID=" + loginID + ",pID=" + participantID + ",name=" + name;
}
}
