package oem.edge.ed.odc.model;

import oem.edge.ed.odc.util.*;
/**
 * Insert the type's description here.
 * Creation date: (08/25/00 08:53:03)
 * @author: Administrator
 */
public class Invitation {
	private java.lang.String Id = null;
	private java.lang.String FromDesktop = null;
	private java.lang.String ToDesktop = null;
	private java.sql.Timestamp StartTime;
	private java.lang.String Title;
	private int State = DesktopCommon.TRANSITION;
/**
 * Invitation constructor comment.
 */
public Invitation() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:56:18)
 * @return java.lang.String
 */
public java.lang.String getFromDesktop() {
	return FromDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:55:03)
 * @return java.lang.String
 */
public java.lang.String getId() {
	return Id;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 12:02:30)
 * @return java.sql.Timestamp
 */
public java.sql.Timestamp getStartTime() {
	return StartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/00 2:45:51 PM)
 * @return int
 */
public int getState() {
	return State;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 12:05:25)
 * @return java.lang.String
 */
public java.lang.String getTitle() {
	return Title;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:57:21)
 * @return java.lang.String
 */
public java.lang.String getToDesktop() {
	return ToDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:56:18)
 * @param newFromDesktop java.lang.String
 */
public void setFromDesktop(java.lang.String newFromDesktop) {
	FromDesktop = newFromDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:55:03)
 * @param newId java.lang.String
 */
public void setId(java.lang.String newId) {
	Id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 12:02:30)
 * @param newStartTime java.sql.Timestamp
 */
public void setStartTime(java.sql.Timestamp newStartTime) {
	StartTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/00 2:45:51 PM)
 * @param newState int
 */
public void setState(int newState) {
	State = newState;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 12:05:25)
 * @param newTitle java.lang.String
 */
public void setTitle(java.lang.String newTitle) {
	Title = newTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (08/25/00 11:57:21)
 * @param newToDesktop java.lang.String
 */
public void setToDesktop(java.lang.String newToDesktop) {
	ToDesktop = newToDesktop;
}
}
