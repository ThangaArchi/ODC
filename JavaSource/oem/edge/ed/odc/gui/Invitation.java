package oem.edge.ed.odc.gui;

import java.util.*;

public class Invitation
{
	// DYY: state is an int -- see DesktopCommon
	//      otherwise, you need to conver the int to string
	private String calledBy;
	private String name;
	private String id;
	private String startTime;
	private int state;
	private String sentto;
	private String cookie;
	private String alias;
	private String display;
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 2:15:08 AM)
 */
public Invitation() {}
public Invitation(String name, String calledBy, String id, String startTime,int state,String sentto,String cookie, String alias) {
	this.calledBy = calledBy;
	this.name = name;
	this.id = id;
	this.startTime = startTime;
	this.state = state;
	this.sentto = sentto;
	this.cookie = cookie;
	this.alias = alias;
	System.out.println("Invitation Constructor");
}
public Invitation(String name, String calledBy, String id, String startTime,int state,String sentto,String cookie, String alias, String display) {
	this.calledBy = calledBy;
	this.name = name;
	this.id = id;
	this.startTime = startTime;
	this.state = state;
	this.sentto = sentto;
	this.cookie = cookie;
	this.alias = alias;
	this.display = display;
	System.out.println("Invitation Constructor");
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:43:19 PM)
 * @return java.lang.String
 */
public String getAlias() {
	return alias;
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:41:19 PM)
 * @return java.lang.String
 */
public String getCookie() {
	return cookie;
}
/**
 * Insert the method's description here.
 * Creation date: (9/9/00 4:01:49 AM)
 * @return java.lang.String
 */
public String getDisplay() {
	return display;
}
	public String getId()
	{
		return id;
	}
	public String getMeetingHost()
	{
		return calledBy;
	}
	public String getName()
	{
		return name;
	}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 12:09:38 AM)
 * @return java.lang.String
 */
public String getSentTo() {
	return sentto;
}
	public String getStartTime()
	{
		return startTime;
	}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 12:09:02 AM)
 * @return java.lang.String
 */
public int getState() {
	return state;
}
}
