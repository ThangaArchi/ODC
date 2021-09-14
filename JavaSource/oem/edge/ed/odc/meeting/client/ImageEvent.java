package oem.edge.ed.odc.meeting.client;

import oem.edge.ed.odc.dsmp.common.*;
/**
 * Insert the type's description here.
 * Creation date: (7/23/2002 11:12:34 AM)
 * @author: Mike Zarnick
 */
public class ImageEvent extends DSMPEvent {
	static public int RESIZE = 0;
	static public int RESIZE_FAILED = 1;
	static public int UPDATE = 2;
	static public int UPDATE_FAILED = 3;
	static public int UPDATE_END = 9;
	static public int MOUSE = 4;
	static public int MOUSE_FAILED = 5;
	static public int KEY = 6;
	static public int KEY_FAILED = 7;
	static public int FULL_IMAGE = 8;
	public int x;
	public int y;
	public int button;
	public int key;
	public int width;
	public int height;
	public byte[] pixels;
	public int offset;
	public int length;
	public int participantID;
	public String message;
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:12:52 PM)
 * @param type int
 * @param flags byte
 * @param handle byte
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 * @param pixels int[]
 */
public ImageEvent(int type, byte flags, byte handle) {
	super(type,flags,handle);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:12:52 PM)
 * @param type int
 * @param flags byte
 * @param handle byte
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 * @param pixels int[]
 */
public ImageEvent(int type, byte flags, byte handle, int x, int y, int keyOrButton, int participantID) {
	super(type,flags,handle);
	this.x = x;
	this.y = y;
	this.participantID = participantID;
	if (type == MOUSE)
		this.button = keyOrButton;
	else
		this.key = keyOrButton;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:12:52 PM)
 * @param type int
 * @param flags byte
 * @param handle byte
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 * @param pixels int[]
 */
public ImageEvent(int type, byte flags, byte handle, int x, int y, int width, int height, byte[] pixels, int off, int len) {
	super(type,flags,handle);
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.pixels = pixels;
	this.offset = off;
	this.length = len;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:12:52 PM)
 * @param type int
 * @param flags byte
 * @param handle byte
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 * @param pixels int[]
 */
public ImageEvent(int type, byte flags, byte handle, String msg) {
	super(type,flags,handle);
	this.message = msg;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isFullImage() {
	return (reason == FULL_IMAGE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isKey() {
	return (reason == KEY);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isKeyCode() {
	return isKey() && ((flags & 0x02) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isKeyFailed() {
	return (reason == KEY_FAILED);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isKeyPress() {
	return isKey() && ((flags & 0x01) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isMouse() {
	return (reason == MOUSE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isMouseButton() {
	return isMouse() && ((flags & 0x01) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isMouseFailed() {
	return (reason == MOUSE_FAILED);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isMousePress() {
	return isMouseButton() && ((flags & 0x02) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isMouseReal() {
	return isMouse() && ((flags & 0x04) != 0);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isResize() {
	return (reason == RESIZE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isResizeFailed() {
	return (reason == RESIZE_FAILED);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isUpdate() {
	return (reason == UPDATE);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isUpdateEnd() {
	return (reason == UPDATE_END);
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2002 12:21:59 PM)
 * @return boolean
 */
public boolean isUpdateFailed() {
	return (reason == UPDATE_FAILED);
}
}
