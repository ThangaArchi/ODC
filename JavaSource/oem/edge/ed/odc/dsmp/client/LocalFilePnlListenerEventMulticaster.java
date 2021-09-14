package oem.edge.ed.odc.dsmp.client;
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
 * This is the event multicaster class to support the LocalFilePnlListenerEventMulticaster interface.
 */
public class LocalFilePnlListenerEventMulticaster extends java.awt.AWTEventMulticaster implements LocalFilePnlListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected LocalFilePnlListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 * @param a oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 * @param b oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 */
public static LocalFilePnlListener add(LocalFilePnlListener a, LocalFilePnlListener b) {
	return (LocalFilePnlListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new LocalFilePnlListenerEventMulticaster(a, b);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 */
protected java.util.EventListener remove(LocalFilePnlListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}
/**
 * Remove listener to support multicast events.
 * @return oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 * @param l oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 * @param oldl oem.edge.ed.odc.ftp.client.LocalFilePnlListener
 */
public static LocalFilePnlListener remove(LocalFilePnlListener l, LocalFilePnlListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof LocalFilePnlListenerEventMulticaster)
		return (LocalFilePnlListener)((LocalFilePnlListenerEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void uploadBtn_actionPerformed(java.util.EventObject newEvent) {
	((LocalFilePnlListener)a).uploadBtn_actionPerformed(newEvent);
	((LocalFilePnlListener)b).uploadBtn_actionPerformed(newEvent);
}
}
