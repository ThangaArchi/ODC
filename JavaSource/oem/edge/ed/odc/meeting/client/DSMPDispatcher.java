package oem.edge.ed.odc.meeting.client;

import java.io.*;
import java.awt.AWTEventMulticaster;
import java.net.*;
import java.util.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.meeting.common.*;

/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 9:24:38 AM)
 * @author: Mike Zarnick
 */
public class DSMPDispatcher extends DSMPSTDispatchBase {
	private DSMPSTSocketHandler handler;
	private PresenceListener presenceListener = null;
	private ImageListener imageListener = null;
	private MessageListener messageListener = null;
	private MeetingListener meetingListener = null;
	private GroupListener groupListener = null;
	public int loginID = -1;
	public int meetingID = -1;
	public int inviteID = -1;
	public int particpantID = -1;
	public String user = null;
	public String company = null;
	public String password = null;
	public boolean callEnabled = false;
	public boolean isOwner = false;
	public boolean isModerator = false;
	public boolean isSharing = false;
/**
 * DSMPDispatcher constructor comment.
 */
public DSMPDispatcher(String host, int port) throws IOException, UnknownHostException {
	super();
	handler = new DSMPSTSocketHandler(host,port,this);
	setDebug(false);
}
/**
 * DSMPDispatcher constructor comment.
 */
public DSMPDispatcher(Socket s) throws IOException, UnknownHostException {
	super();
	handler = new DSMPSTSocketHandler(s,this);
	setDebug(false);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l GroupListener
 */
public synchronized void addGroupListener(GroupListener l) {
	if (l == null)
		return;

	groupListener = DSMPEventMulticaster.addGroupListener(groupListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l ImageListener
 */
public synchronized void addImageListener(ImageListener l) {
	if (l == null)
		return;

	imageListener = DSMPEventMulticaster.addImageListener(imageListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l MeetingListener
 */
public synchronized void addMeetingListener(MeetingListener l) {
	if (l == null)
		return;

	meetingListener = DSMPEventMulticaster.addMeetingListener(meetingListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l MessageListener
 */
public synchronized void addMessageListener(MessageListener l) {
	if (l == null)
		return;

	messageListener = DSMPEventMulticaster.addMessageListener(messageListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 9:57:22 AM)
 * @param l PresenceListener
 */
public synchronized void addPresenceListener(PresenceListener l) {
	if (l == null)
		return;

	presenceListener = DSMPEventMulticaster.addPresenceListener(presenceListener,l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 10:31:55 PM)
 * @param p DSMPProto
 */
public void dispatchProtocol(DSMPBaseProto p) {
	handler.sendProtocolPacket(p);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param msg String
 */
public void fireArriveMessage(String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.ARRIVE_MSG,(byte) 0,(byte) 0,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:57:57 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireAssignOwnershipReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OWNER,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireAssignOwnershipReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OWNER_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireChatMessageEvent(DSMPHandler h, byte flags, byte handle, int mID, int pID, int toID, String msg, boolean unicast) {
	//super.fireChatMessageEvent(h,flags,handle,mID,pID,toID,msg,unicast);
	fireMessageEvent(new MessageEvent(flags,handle,pID,toID,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireChatMessageReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireChatMessageReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.CHAT_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireControlEvent(DSMPHandler h, byte flags, byte handle, int mID, int pID) {
	//super.fireControlEvent(h,flags,handle,mID,pID);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.CONTROL_EVENT,flags,handle,mID,pID));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a CreateGroup reply. This is a possible response to protocol
 * generated by the GroupGenerator.createGroup method.
 */
public void fireCreateGroupReply(DSMPHandler h, byte flags, byte handle) {
	fireGroupEvent(new GroupEvent(GroupEvent.CREATEGROUP,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a CreateGroup reply. This is a possible response to protocol
 * generated by the GroupGenerator.createGroup method.
 */
public void fireCreateGroupReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireGroupEvent(new GroupEvent(GroupEvent.CREATEGROUP_FAILED,flags,handle,errorStr));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireCreateInvitationReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireCreateInvitationReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.INVITE_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a DeleteGroup reply. This is a possible response to protocol
 * generated by the GroupGenerator.deleteGroup method.
 */
public void fireDeleteGroupReply(DSMPHandler h, byte flags, byte handle) {
	fireGroupEvent(new GroupEvent(GroupEvent.DELETEGROUP,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a DeleteGroup reply. This is a possible response to protocol
 * generated by the GroupGenerator.deleteGroup method.
 */
public void fireDeleteGroupReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireGroupEvent(new GroupEvent(GroupEvent.DELETEGROUP_FAILED,flags,handle,errorStr));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param msg String
 */
public void fireDepartMessage(String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.DEPART_MSG,(byte) 0,(byte) 0,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireDropInviteeReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireDropInviteeReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.DROP_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:37:48 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param iID int
 * @param pID int
 * @param lID int
 * @param name java.lang.String
 */
public void fireDroppedEvent(DSMPHandler h, byte flags, byte handle, int mID, int iID, int pID, boolean iDrop, boolean pDrop) {
	//super.fireDroppedEvent(h,flags,handle,mID,iID,pID,iDrop,pDrop);
	firePresenceEvent(new PresenceEvent(PresenceEvent.DROP,flags,handle,mID,iID,pID,-1,(byte) 0,null));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param participantID int
 */
public void fireEndMeetingEvent(DSMPHandler h, byte flags, byte handle, int meetingID) {
	//super.fireEndMeetingEvent(h,flags,handle,meetingID);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.END,flags,handle,meetingID));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireEndMeetingReply(DSMPHandler h, byte flags, byte handle) {
	//super.fireEndMeetingReply(h,flags,handle);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.END,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireEndMeetingReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireEndMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.END_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 */
public void fireForcedLeaveMeeting() {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.FORCED,(byte) 0,(byte) 0));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 8:32:20 PM)
 * @param hand DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 */
public void fireFrameEndEvent(DSMPHandler hand,byte flags,byte handle,int meetingid) {
	fireImageEvent(new ImageEvent(ImageEvent.UPDATE_END,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 8:32:20 PM)
 * @param hand DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param x short
 * @param y short
 * @param w short
 * @param h short
 * @param c boolean
 * @param buf byte[]
 */
public void fireFrameUpdateEvent(DSMPHandler hand, byte flags, byte handle, int mID, short x, short y, short w, short h, boolean c, byte[] buf, int ofs, int len) {
	//super.fireFrameUpdateEvent(hand,flags,handle,mID,x,y,w,h,c,buf);
	fireImageEvent(new ImageEvent(ImageEvent.UPDATE,flags,handle,x,y,w,h,buf,ofs,len));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireFrozenModeError(DSMPHandler h, byte flags, byte handle, int meetingID, short errcode, String msg) {
	//super.fireJoinMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.FREEZE_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireFrozenModeEvent(DSMPHandler h, byte flags, byte handle, int meetingID, boolean frozen) {
	//super.fireJoinMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(frozen ? MeetingEvent.FREEZE : MeetingEvent.THAW,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:24:37 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param v java.util.Vector
 */
public void fireGetAllMeetingsReply(DSMPHandler h, byte flags, byte handle, Vector v) {
	//super.fireGetAllMeetingsReply(h,flags,handle,v);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.GET,flags,handle,v));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireGetAllMeetingsReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireGetAllMeetingsReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.GET_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:06:27 AM)
 * @param e GroupEvent
 */
protected void fireGroupEvent(GroupEvent e) {
	if (groupListener != null)
		groupListener.groupEvent(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:06:27 AM)
 * @param e ImageEvent
 */
protected void fireImageEvent(ImageEvent e) {
	if (imageListener != null)
		imageListener.imageChanged(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 8:45:01 PM)
 * @param hand DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param x short
 * @param y short
 * @param w short
 * @param h short
 */
public void fireImageResizeEvent(DSMPHandler hand, byte flags, byte handle, int mID, short x, short y, short w, short h) {
	//super.fireImageResizeEvent(hand,flags,handle,mID,x,y,w,h);
	fireImageEvent(new ImageEvent(ImageEvent.RESIZE,flags,handle,x,y,w,h,null,0,0));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:37:48 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param iID int
 * @param pID int
 * @param lID int
 * @param name java.lang.String
 */
public void fireJoinedMeetingEvent(DSMPHandler h, byte flags, byte handle, int mID, int iID, int pID, int lID, byte inviteType, String name) {
	//super.fireJoinedMeetingEvent(h,flags,handle,mID,iID,pID,lID,name);
	firePresenceEvent(new PresenceEvent(PresenceEvent.ARRIVE,flags,handle,mID,iID,pID,lID,inviteType,name));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:46:14 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingid int
 * @param participantID int
 */
public void fireJoinMeetingReply(DSMPHandler h, byte flags, byte handle, int iID, int pID) {
	//super.fireJoinMeetingReply(h,flags,handle,iID,pID);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.JOIN,flags,handle,iID,pID));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireJoinMeetingReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireJoinMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.JOIN_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param participantID int
 */
public void fireKeyUpdateEvent(DSMPHandler h, byte flags, byte handle, 
                                  int meetingid,
                                  boolean pressRelease,
                                  boolean keyCodeOrChar,
                                  short x, short y, 
                                  int javakeysym,
                                  int participantid) {
	//super.fireKeyUpdateEvent(h,flags,handle,meetingID,pressRelease,keyCodeOrChar,x,y,javakeysym,participantid);
	fireImageEvent(new ImageEvent(ImageEvent.KEY,flags,handle,x,y,javakeysym,participantid));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param participantID int
 */
public void fireLeaveMeetingReply(DSMPHandler h, byte flags, byte handle) {
	//super.fireLeaveMeetingReply(h,flags,handle);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.LEAVE,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireLeaveMeetingReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireLeaveMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.LEAVE_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:46:14 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param inviteid int
 * @param v java.util.Vector
 */
public void fireLoginReply(DSMPHandler h, byte flags, byte handle, int loginID, String user, String company, Vector v) {
	//super.fireLoginReply(h,flags,handle,loginID,v);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.LOGIN,flags,handle,loginID,user,company,v));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireLoginReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireLoginReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.LOGIN_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireLogoutReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireLogoutReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.LOGOUT_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:06:27 AM)
 * @param e MeetingEvent
 */
protected void fireMeetingEvent(MeetingEvent e) {
	if (meetingListener != null)
		meetingListener.meetingAction(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:06:27 AM)
 * @param e MessageEvent
 */
protected void fireMessageEvent(MessageEvent e) {
	if (messageListener != null)
		messageListener.message(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireModeratorChangeEvent(DSMPHandler h, byte flags, byte handle, int mID, int fromPID, int toPID) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.MODERATOR_EVENT,flags,handle,mID,fromPID,toPID));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:57:57 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireModifyControlReply(DSMPHandler h, byte flags, byte handle) {
	//super.fireLogoutReply(h,flags,handle);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.CONTROL,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireModifyControlReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireLogoutReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.CONTROL_FAILED,flags,handle,msg));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a ModifyGroupAcl reply. This is a possible response to protocol
 * generated by the GroupGenerator methods addGroupAccessAcl, addGroupMemberAcl, removeGroupAccessAcl
 * and removeGroupMemberAcl.
 */
public void fireModifyGroupAclReply(DSMPHandler h, byte flags, byte handle) {
	fireGroupEvent(new GroupEvent(GroupEvent.MODIFYGROUPACL,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a ModifyGroupAcl reply. This is a possible response to protocol
 * generated by the GroupGenerator methods addGroupAccessAcl, addGroupMemberAcl, removeGroupAccessAcl
 * and removeGroupMemberAcl.
 */
public void fireModifyGroupAclReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireGroupEvent(new GroupEvent(GroupEvent.MODIFYGROUPACL_FAILED,flags,handle,errorStr));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a ModifyGroupAttribute reply. This is a possible response to protocol
 * generated by the GroupGenerator.modifyGroupAttribute method.
 */
public void fireModifyGroupAttributeReply(DSMPHandler h, byte flags, byte handle) {
	fireGroupEvent(new GroupEvent(GroupEvent.MODIFYGROUPATTR,flags,handle));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a ModifyGroupAttribute reply. This is a possible response to protocol
 * generated by the GroupGenerator.modifyGroupAttribute method.
 */
public void fireModifyGroupAttributeReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireGroupEvent(new GroupEvent(GroupEvent.MODIFYGROUPATTR_FAILED,flags,handle,errorStr));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param participantID int
 */
public void fireMouseUpdateEvent(DSMPHandler h, byte flags, byte handle, 
                                    int meetingid,
                                    boolean buttonEvent, 
                                    boolean pressRelease,
                                    boolean realMotion,
                                    byte button, short x, short y, 
                                    int participantid) {
	//super.fireStartMeetingReply(h,flags,handle,meetingID,participantID);
	fireImageEvent(new ImageEvent(ImageEvent.MOUSE,flags,handle,x,y,button,participantid));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:37:48 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param inviteID int
 * @param project boolean
 * @param name java.lang.String
 */
public void fireNewInvitationEvent(DSMPHandler h, byte flags, byte handle, int meetingID, int inviteID, byte inviteType, String name) {
	//super.fireNewInvitationEvent(h,flags,handle,meetingID,inviteID,project,name);
	firePresenceEvent(new PresenceEvent(PresenceEvent.INVITE,flags,handle,meetingID,inviteID,-1,-1,inviteType,name));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireOwnershipChangeEvent(DSMPHandler h, byte flags, byte handle, int mID, int pID, boolean addOrRemove) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OWNER_EVENT,flags,handle,mID,pID,addOrRemove));
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:06:27 AM)
 * @param e PresenceEvent
 */
protected void firePresenceEvent(PresenceEvent e) {
	if (presenceListener != null)
		presenceListener.presenceChanged(e);
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a QueryGroups reply. This is a possible response to protocol
 * generated by the GroupGenerator.queryGroups method.
 */
public void fireQueryGroupsReply(DSMPHandler h, byte flags, byte handle, boolean includesMembers, boolean includesAccess, Vector vec) {
	fireGroupEvent(new GroupEvent(GroupEvent.QUERYGROUPS,flags,handle,includesMembers,includesAccess,vec));
}
/**
 * This is a service method. Applications should not call this method (should be declared protected).
 * <p>
 * This method fires a GroupEvent for a QueryGroups reply. This is a possible response to protocol
 * generated by the GroupGenerator.queryGroups method.
 */
public void fireQueryGroupsReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireGroupEvent(new GroupEvent(GroupEvent.QUERYGROUPS_FAILED,flags,handle,errorStr));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 10:24:08 PM)
 * @param h DSMPHandler
 */
public void fireShutdownEvent(DSMPHandler h) {
	//super.fireShutdownEvent(h);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.DEATH,(byte) 0,(byte) 0));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param meetingID int
 * @param participantID int
 */
public void fireStartMeetingReply(DSMPHandler h, byte flags, byte handle, int meetingID, int participantID) {
	//super.fireStartMeetingReply(h,flags,handle,meetingID,participantID);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.START,flags,handle,meetingID,participantID));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireStartMeetingReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	//super.fireStartMeetingReplyError(h,flags,handle,errcode,msg);
	fireMeetingEvent(new MeetingEvent(MeetingEvent.START_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireStartShareEvent(DSMPHandler h, byte flags, byte handle, int mID) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.START_SHARE_EVENT,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:57:57 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireStartShareReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.START_SHARE,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireStartShareReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.START_SHARE_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:02:42 PM)
 */
public void fireStopShare() {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.STOP_SHARING,(byte) 0,(byte) 0));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:25:35 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param mID int
 * @param pID int
 * @param toID int
 * @param msg java.lang.String
 * @param unicast boolean
 */
public void fireStopShareEvent(DSMPHandler h, byte flags, byte handle, int mID) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.STOP_SHARE_EVENT,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:57:57 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireStopShareReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.STOP_SHARE,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireStopShareReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.STOP_SHARE_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:57:57 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 */
public void fireTransferModeratorReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.MODERATOR,flags,handle));
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 3:55:41 PM)
 * @param h DSMPHandler
 * @param flags byte
 * @param handle byte
 * @param errcode short
 * @param msg java.lang.String
 */
public void fireTransferModeratorReplyError(DSMPHandler h, byte flags, byte handle, short errcode, String msg) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.MODERATOR_FAILED,flags,handle,msg));
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l GroupListener
 */
public synchronized void removeGroupListener(GroupListener l) {
	if (l == null)
		return;

	groupListener = DSMPEventMulticaster.removeGroupListener(groupListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l ImageListener
 */
public synchronized void removeImageListener(ImageListener l) {
	if (l == null)
		return;

	imageListener = DSMPEventMulticaster.removeImageListener(imageListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l MeetingListener
 */
public synchronized void removeMeetingListener(MeetingListener l) {
	if (l == null)
		return;

	meetingListener = DSMPEventMulticaster.removeMeetingListener(meetingListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l MessageListener
 */
public synchronized void removeMessageListener(MessageListener l) {
	if (l == null)
		return;

	messageListener = DSMPEventMulticaster.removeMessageListener(messageListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:55:36 AM)
 * @param l PresenceListener
 */
public synchronized void removePresenceListener(PresenceListener l) {
	if (l == null)
		return;

	presenceListener = DSMPEventMulticaster.removePresenceListener(presenceListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 10:21:57 PM)
 */
public void shutdown() {
	handler.shutdown();
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireAcceptCallEvent(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, int, int, java.lang.String, java.lang.String)
 */
public void fireAcceptCallEvent(DSMPHandler h, byte flags, byte handle, int meetingid, int acceptingLoginId, String acceptingUserid, String acceptingCompany) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.ACCEPT_CALL_EVENT,flags,handle,meetingid,acceptingLoginId,acceptingUserid,acceptingCompany));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireAcceptCallReply(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte)
 */
public void fireAcceptCallReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.ACCEPT_CALL,flags,handle));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireAcceptCallReplyError(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, short, java.lang.String)
 */
public void fireAcceptCallReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.ACCEPT_CALL_FAILED,flags,handle,errorStr));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireGetMeetingURLReply(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, int, java.lang.String)
 */
public void fireGetMeetingURLReply(DSMPHandler h, byte flags, byte handle, int meetingid, String URL) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.URL,flags,handle,meetingid,URL));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireGetMeetingURLReplyError(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, short, java.lang.String)
 */
public void fireGetMeetingURLReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.URL_FAILED,flags,handle,errorStr));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireMeetingOptionEvent(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, int, java.lang.String, java.lang.String)
 */
public void fireMeetingOptionEvent(DSMPHandler h, byte flags, byte handle, int meetingid, String key, String val) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OPTIONS_EVENT,flags,handle,meetingid,key,val));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#firePlaceCallEvent(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, int, int, java.lang.String, java.lang.String, java.lang.String)
 */
public void firePlaceCallEvent(DSMPHandler h, byte flags, byte handle, int meetingid, int fromPartid, String fromUserid, String fromCompany, String salt) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.PLACE_CALL_EVENT,flags,handle,meetingid,fromPartid,fromUserid,fromCompany,salt));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#firePlaceCallReply(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte)
 */
public void firePlaceCallReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.PLACE_CALL,flags,handle));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#firePlaceCallReplyError(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, short, java.lang.String)
 */
public void firePlaceCallReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.PLACE_CALL_FAILED,flags,handle,errorStr));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireSetMeetingOptionsReply(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte)
 */
public void fireSetMeetingOptionsReply(DSMPHandler h, byte flags, byte handle) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OPTIONS,flags,handle));
}
/* (non-Javadoc)
 * @see oem.edge.ed.odc.meeting.common.DSMPSTDispatchBase#fireSetMeetingOptionsReplyError(oem.edge.ed.odc.meeting.common.DSMPHandler, byte, byte, short, java.lang.String)
 */
public void fireSetMeetingOptionsReplyError(DSMPHandler h, byte flags, byte handle, short errorcode, String errorStr) {
	fireMeetingEvent(new MeetingEvent(MeetingEvent.OPTIONS_FAILED,flags,handle,errorStr));
}
}
