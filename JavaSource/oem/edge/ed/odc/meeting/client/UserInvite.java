package oem.edge.ed.odc.meeting.client;

/**
 * Insert the type's description here.
 * Creation date: (3/3/2003 1:15:41 PM)
 * @author: Mike Zarnick
 */
public class UserInvite extends Invite {
	public boolean present;
	public int participantID;
	public int nickLength;
	public boolean inControl = false;
	public boolean isModerator = false;
	public boolean isOwner = false;
	public boolean isProjUser = false;
/**
 * UserInvitee constructor comment.
 * @param n char[]
 * @param id int
 */
public UserInvite(String u, int inviteID, int partID, boolean p) {
	super(u,inviteID);
	this.participantID = partID;
	this.present = p;
}
}
