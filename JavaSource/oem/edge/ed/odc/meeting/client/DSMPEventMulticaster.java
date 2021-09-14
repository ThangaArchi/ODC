package oem.edge.ed.odc.meeting.client;

import java.util.*;
import oem.edge.ed.odc.dsmp.client.*;

/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 10:44:09 AM)
 * @author: Mike Zarnick
 */
public class DSMPEventMulticaster extends DSMPBaseMulticaster implements PresenceListener, ImageListener, MeetingListener, MessageListener, GroupListener {
/**
 * DSMPEventMulticaster constructor comment.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected DSMPEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return GroupListener
 * @param a GroupListener
 * @param b GroupListener
 */
public static GroupListener addGroupListener(GroupListener a, GroupListener b) {
	return (GroupListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return ImageListener
 * @param a ImageListener
 * @param b ImageListener
 */
public static ImageListener addImageListener(ImageListener a, ImageListener b) {
	return (ImageListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 10:50:22 AM)
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static EventListener addInternal(EventListener a, EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new DSMPEventMulticaster(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return MeetingListener
 * @param a MeetingListener
 * @param b MeetingListener
 */
public static MeetingListener addMeetingListener(MeetingListener a, MeetingListener b) {
	return (MeetingListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return MessageListener
 * @param a MessageListener
 * @param b MessageListener
 */
public static MessageListener addMessageListener(MessageListener a, MessageListener b) {
	return (MessageListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return PresenceListener
 * @param a PresenceListener
 * @param b PresenceListener
 */
public static PresenceListener addPresenceListener(PresenceListener a, PresenceListener b) {
	return (PresenceListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e GroupEvent
 */
public void groupEvent(GroupEvent e) {
	((GroupListener)a).groupEvent(e);
	((GroupListener)b).groupEvent(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e ImageEvent
 */
public void imageChanged(ImageEvent e) {
	((ImageListener)a).imageChanged(e);
	((ImageListener)b).imageChanged(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	((MeetingListener)a).meetingAction(e);
	((MeetingListener)b).meetingAction(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e MessageEvent
 */
public void message(MessageEvent e) {
	((MessageListener)a).message(e);
	((MessageListener)b).message(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e PresenceEvent
 */
public void presenceChanged(PresenceEvent e) {
	((PresenceListener)a).presenceChanged(e);
	((PresenceListener)b).presenceChanged(e);
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 11:21:34 AM)
 * @return java.util.EventListener
 * @param oldl java.util.EventListener
 */
protected EventListener remove(EventListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	EventListener a2 = removeInternal(a, oldl);
	EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b) {
	    return this;	// it's not here
	}
	return addInternal(a2, b2);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return GroupListener
 * @param l GroupListener
 * @param oldl GroupListener
 */
public static GroupListener removeGroupListener(GroupListener l, GroupListener oldl) {
	return (GroupListener) removeInternal(l,oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return ImageListener
 * @param l ImageListener
 * @param oldl ImageListener
 */
public static ImageListener removeImageListener(ImageListener l, ImageListener oldl) {
	return (ImageListener) removeInternal(l,oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 10:50:22 AM)
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static EventListener removeInternal(EventListener l, EventListener oldl) {
	if (l == oldl || l == null) {
	    return null;
	} else if (l instanceof DSMPEventMulticaster) {
	    return ((DSMPEventMulticaster)l).remove(oldl);
	} else {
	    return l;		// it's not here
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return MeetingListener
 * @param l MeetingListener
 * @param oldl MeetingListener
 */
public static MeetingListener removeMeetingListener(MeetingListener l, MeetingListener oldl) {
	return (MeetingListener) removeInternal(l,oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return MessageListener
 * @param l MessageListener
 * @param oldl MessageListener
 */
public static MessageListener removeMessageListener(MessageListener l, MessageListener oldl) {
	return (MessageListener) removeInternal(l,oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return PresenceListener
 * @param l PresenceListener
 * @param oldl PresenceListener
 */
public static PresenceListener removePresenceListener(PresenceListener l, PresenceListener oldl) {
	return (PresenceListener) removeInternal(l,oldl);
}
}
