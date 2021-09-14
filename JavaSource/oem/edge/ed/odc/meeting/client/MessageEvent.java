package oem.edge.ed.odc.meeting.client;

import oem.edge.ed.odc.dsmp.common.*;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 11:25:45 AM)
 * @author: Mike Zarnick
 */
public class MessageEvent extends DSMPEvent {
	public int fromID;
	public int toID;
	public String message;
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 9:42:58 PM)
 * @param flags byte
 * @param handle byte
 * @param fromID int
 * @param toID int
 * @param message char[]
 */
public MessageEvent(byte flags, byte handle, int fromID, int toID, String message) {
	super(0,flags,handle);
	this.fromID = fromID;
	this.toID = toID;
	this.message = message;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 9:46:03 PM)
 * @return boolean
 */
public boolean isUnicast() {
	return (flags != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2002 11:45:47 AM)
 */
public String toString() {
	return "MessageEvent: flag=" + flags + ",from=" + fromID + ",to=" + toID + ",msg=" + message;
}
}
