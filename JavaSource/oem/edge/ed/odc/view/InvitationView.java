package oem.edge.ed.odc.view;

import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.util.*;
/**
 * Insert the type's description here.
 * Creation date: (07/20/00 17:01:45)
 * @author: Administrator
 */
public class InvitationView {
	private java.lang.String Id = "";
	private String Invitor = "";
	private java.lang.String MeetingName = "";
	private java.lang.String Invitee = "";
	private java.lang.String XCookie = "";
	private java.lang.String XAlias = "";
	private int State = DesktopCommon.TRANSITION;
   private java.sql.Timestamp StartTime = null;
	private java.lang.String FromDesktop="";
	private java.lang.String Display="";
	private java.lang.String ToDesktop = "";
	private java.lang.String Company = "";
	private java.lang.String Country = "";
/**
 * Invitation constructor comment.
 */
public InvitationView() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/00 4:02:25 PM)
 * @return java.lang.String
 */
public java.lang.String getDisplay() {
	return Display;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/00 9:08:27 PM)
 * @return java.lang.String
 */
public java.lang.String getFromDesktop() {
	return FromDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:47:39)
 * @return java.lang.String
 */
public java.lang.String getId() {
	return Id;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:44:01)
 * @return java.lang.String
 */
public java.lang.String getInvitee() {
	return Invitee;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:05:22)
 * @return String
 */
public String getInvitor() {
	return Invitor;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:25:13)
 * @return java.lang.String
 */
public java.lang.String getMeetingName() {
	return MeetingName;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:27:41)
 * @return java.sql.Timestamp
 */
public java.sql.Timestamp getStartTime() {
	return StartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:46:33)
 * @return int
 */
public int getState() {
	return State;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:47:55 PM)
 * @return java.lang.String
 */
public java.lang.String getToDesktop() {
	return ToDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:45:44)
 * @return java.lang.String
 */
public java.lang.String getXAlias() {
	return XAlias;
}
public java.lang.String getCompany() {
	return Company;
}
public java.lang.String getCountry() {
	return Country;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:45:11)
 * @return java.lang.String
 */
public java.lang.String getXCookie() {
	return XCookie;
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/00 4:02:25 PM)
 * @param newDisplay java.lang.String
 */
public void setDisplay(java.lang.String newDisplay) {
	Display = newDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (8/26/00 9:08:27 PM)
 * @param newFromDesktop java.lang.String
 */
public void setFromDesktop(java.lang.String newFromDesktop) {
	FromDesktop = newFromDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:47:39)
 * @param newKey java.lang.String
 */
public void setId(java.lang.String newId) {
	Id = newId;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:44:01)
 * @param newInvitee java.lang.String
 */
public void setInvitee(java.lang.String newInvitee) {
	Invitee = newInvitee;
}
public void setCompany(java.lang.String newComp) {
	Company = newComp;
}
public void setCountry(java.lang.String newCount) {
	Country = newCount;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:05:22)
 * @param newInvitor String
 */
public void setInvitor(String newInvitor) {
	Invitor = newInvitor;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:25:13)
 * @param newMeetingName java.lang.String
 */
public void setMeetingName(java.lang.String newMeetingName) {
	MeetingName = newMeetingName;
}
/**
 * Insert the method's description here.
 * Creation date: (07/20/00 17:27:41)
 * @param newStartTime java.sql.Timestamp
 */
public void setStartTime(java.sql.Timestamp newStartTime) {
	StartTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:46:33)
 * @param newState int
 */
public void setState(int newState) {
	State = newState;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/00 4:47:55 PM)
 * @param newToDesktop java.lang.String
 */
public void setToDesktop(java.lang.String newToDesktop) {
	ToDesktop = newToDesktop;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:45:44)
 * @param newXAlias java.lang.String
 */
public void setXAlias(java.lang.String newXAlias) {
	XAlias = newXAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:45:11)
 * @param newXCookie java.lang.String
 */
public void setXCookie(java.lang.String newXCookie) {
	XCookie = newXCookie;
}
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 17:19:51)
 * @return com.ibm.edesign.collaboration.util.ConfigSection
 */
public ConfigSection toConfigSection() {
	ConfigSection section = new ConfigSection(DesktopCommon.INVITATION);

	section.setProperty(DesktopCommon.ID, this.Id);

	section.setProperty(DesktopCommon.OWNER, this.Invitor);

	section.setProperty(DesktopCommon.INVITEE, this.Invitee);

	section.setIntProperty(DesktopCommon.STATUS, this.State);

	section.setProperty(DesktopCommon.START, StartTime.toString());

	section.setProperty(DesktopCommon.NAME, this.MeetingName);
	
	section.setProperty(DesktopCommon.COOKIE, this.XCookie);

	section.setProperty(DesktopCommon.ALIAS, this.XAlias);

	section.setProperty(DesktopCommon.DISPLAY, this.Display);
	section.setProperty(DesktopCommon.COMPANY, this.Company);
	section.setProperty(DesktopCommon.COUNTRY, this.Country);
	
	return section;
}
}
