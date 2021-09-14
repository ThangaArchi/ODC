package oem.edge.ed.odc.meeting.client;

/**
 * Insert the type's description here.
 * Creation date: (3/3/2003 1:13:32 PM)
 * @author: Mike Zarnick
 */
public class Invite {
	public String name;
	public int inviteID;
/**
 * Invitee constructor comment.
 */
public Invite(String n, int id) {
	this.name = n;
	this.inviteID = id;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2003 9:28:41 AM)
 * @return java.lang.String
 */
public String toString() {
	return name;
}
}
