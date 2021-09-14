package oem.edge.ed.odc.dsmp.client;

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
public class DSMPBaseMulticaster implements MenuChangeListener {
	protected final EventListener a, b;
/**
 * DSMPEventMulticaster constructor comment.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected DSMPBaseMulticaster(java.util.EventListener a, java.util.EventListener b) {
	this.a = a;
	this.b = b;
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
	return new DSMPBaseMulticaster(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:50:58 AM)
 * @return MenuChangeListener
 * @param a MenuChangeListener
 * @param b MenuChangeListener
 */
public static MenuChangeListener addMenuChangeListener(MenuChangeListener a, MenuChangeListener b) {
	return (MenuChangeListener) addInternal(a,b);
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 11:07:51 AM)
 * @param e MenuChangeEvent
 */
public void menuChange(MenuChangeEvent e) {
	((MenuChangeListener)a).menuChange(e);
	((MenuChangeListener)b).menuChange(e);
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
 * Creation date: (8/2/2002 10:50:22 AM)
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static EventListener removeInternal(EventListener l, EventListener oldl) {
	if (l == oldl || l == null) {
	    return null;
	} else if (l instanceof DSMPBaseMulticaster) {
	    return ((DSMPBaseMulticaster)l).remove(oldl);
	} else {
	    return l;		// it's not here
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/23/2002 10:52:54 AM)
 * @return MenuChangeListener
 * @param l MenuChangeListener
 * @param oldl MenuChangeListener
 */
public static MenuChangeListener removeMenuChangeListener(MenuChangeListener l, MenuChangeListener oldl) {
	return (MenuChangeListener) removeInternal(l,oldl);
}
}
