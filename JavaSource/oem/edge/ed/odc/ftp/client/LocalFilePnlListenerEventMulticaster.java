package oem.edge.ed.odc.ftp.client;

/**
 * This is the event multicaster class to support the oem.edge.ed.odc.ftp.client.LocalFilePnlListenerEventMulticaster interface.
 */
public class LocalFilePnlListenerEventMulticaster extends java.awt.AWTEventMulticaster implements oem.edge.ed.odc.ftp.client.LocalFilePnlListener {
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
public static oem.edge.ed.odc.ftp.client.LocalFilePnlListener add(oem.edge.ed.odc.ftp.client.LocalFilePnlListener a, oem.edge.ed.odc.ftp.client.LocalFilePnlListener b) {
	return (oem.edge.ed.odc.ftp.client.LocalFilePnlListener)addInternal(a, b);
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
protected java.util.EventListener remove(oem.edge.ed.odc.ftp.client.LocalFilePnlListener oldl) {
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
public static oem.edge.ed.odc.ftp.client.LocalFilePnlListener remove(oem.edge.ed.odc.ftp.client.LocalFilePnlListener l, oem.edge.ed.odc.ftp.client.LocalFilePnlListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof LocalFilePnlListenerEventMulticaster)
		return (oem.edge.ed.odc.ftp.client.LocalFilePnlListener)((oem.edge.ed.odc.ftp.client.LocalFilePnlListenerEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void uploadBtn_actionPerformed(java.util.EventObject newEvent) {
	((oem.edge.ed.odc.ftp.client.LocalFilePnlListener)a).uploadBtn_actionPerformed(newEvent);
	((oem.edge.ed.odc.ftp.client.LocalFilePnlListener)b).uploadBtn_actionPerformed(newEvent);
}
}
