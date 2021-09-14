package oem.edge.ed.odc.ftp.client;

import java.util.EventListener;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 10:44:09 AM)
 * @author: Mike Zarnick
 */
public class FTPEventMulticaster implements FTPListener, FTPStatusListener {
	protected final EventListener a, b;
/**
 * DSMPEventMulticaster constructor comment.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected FTPEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	this.a = a;
	this.b = b;
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return FTPListener
 * @param a FTPListener
 * @param b FTPListener
 */
public static FTPListener addFTPListener(FTPListener a, FTPListener b) {
	return (FTPListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return FTPStatusListener
 * @param a FTPStatusListener
 * @param b FTPStatusListener
 */
public static FTPStatusListener addFTPStatusListener(FTPStatusListener a, FTPStatusListener b) {
	return (FTPStatusListener) addInternal(a,b);
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
	return new FTPEventMulticaster(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e FTPEvent
 */
public void ftpAction(FTPEvent e) {
	((FTPListener)a).ftpAction(e);
	((FTPListener)b).ftpAction(e);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e FTPStatusEvent
 */
public void ftpStatusAction(FTPStatusEvent e) {
	((FTPStatusListener)a).ftpStatusAction(e);
	((FTPStatusListener)b).ftpStatusAction(e);
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
 * @return FTPListener
 * @param l FTPListener
 * @param oldl FTPListener
 */
public static FTPListener removeFTPListener(FTPListener l, FTPListener oldl) {
	return (FTPListener) removeInternal(l,oldl);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return FTPStatusListener
 * @param l FTPStatusListener
 * @param oldl FTPStatusListener
 */
public static FTPStatusListener removeFTPStatusListener(FTPStatusListener l, FTPStatusListener oldl) {
	return (FTPStatusListener) removeInternal(l,oldl);
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
	} else if (l instanceof FTPEventMulticaster) {
	    return ((FTPEventMulticaster)l).remove(oldl);
	} else {
	    return l;		// it's not here
	}
}
}
