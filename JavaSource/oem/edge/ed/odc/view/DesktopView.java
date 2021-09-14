package oem.edge.ed.odc.view;

import oem.edge.ed.odc.util.*;
import com.ibm.as400.webaccess.common.*;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (7/25/00 10:10:58 AM)
 * @author: Administrator
 */
public class DesktopView {
	private java.lang.String Key = null;
	private java.lang.String Owner = null;
	private java.lang.String Comp = null;
	private java.lang.String Country = null;
	private java.lang.String State = null;
	private java.lang.String EdgeId = null;
	private java.lang.String First = null;
	private java.lang.String Last  = null;
	private java.lang.String EmailAddr = null;
	private java.lang.String Projects  = null;
	private java.lang.String XDisplay = null;
	private java.lang.String XCookie;
	private java.lang.String XAlias = null;
	private java.lang.String Classroom = null;
	private java.sql.Timestamp StartTime = null;
	private java.lang.String RemoteHost = null;
	private int RemotePort = -1;
	private java.lang.String LocalHost = null;
	private int LocalPort = -1;
	private int bumpport = -1;
	private java.lang.String bumphost = null;
        
   public String toString() {
      return "Key    =" + Key   + "\n" +
             "Own    =" + Owner + "\n" +
             "EdgeId =" + EdgeId + "\n" +
             "Company=" + Comp + "\n" +
             "Country=" + Country + "\n" +
             "State  =" + State + "\n" +
             "Class  =" + Classroom + "\n" +
             "First  =" + First + "\n" +
             "Last   =" + Last + "\n" +
             "Email  =" + EmailAddr + "\n" +
             "Project=" + Projects + "\n" +
             "XDisp  =" + XDisplay+ "\n" +
             "XCook  =" + XCookie + "\n" +
             "XAlias =" + XAlias + "\n" +
             "Rhost  =" + RemoteHost+ "\n" +
             "Rport  =" +RemotePort + "\n" +
             "Lhost  =" + LocalHost + "\n" +
             "Lport  =" + LocalPort + "\n" +
             "Bhost  =" + bumphost + "\n" +
             "Bport  =" + bumpport + "\n";
   }
        
public DesktopView() {
   ;
}

public DesktopView(DesktopView dv) {
   copy(dv);
}

public void copy(DesktopView dv) {
   Key = dv.Key;
   Owner = dv.Owner;
   Comp = dv.Comp;
   Country = dv.Country;
   State = dv.State;
   EdgeId = dv.EdgeId;
   First = dv.First;
   Last = dv.Last;
   EmailAddr = dv.EmailAddr;
   Projects = dv.Projects;
   Classroom    = dv.Classroom;
   XDisplay = dv.XDisplay;
   XCookie = dv.XCookie;
   XAlias = dv.XAlias;
   StartTime = dv.StartTime;
   RemoteHost = dv.RemoteHost;
   RemotePort = dv.RemotePort;
   LocalHost = dv.LocalHost;
   LocalPort = dv.LocalPort;
   bumpport = dv.bumpport;
   bumphost = dv.bumphost;
}

/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:14:12 AM)
 * @return java.lang.String
 */
public java.lang.String getKey() {
	return Key;
}
/**
 * Insert the method's description here.
 * Creation date: (9/15/00 11:37:56 AM)
 * @return java.lang.String
 */
public java.lang.String getLocalHost() {
	return LocalHost;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 5:13:03 PM)
 * @return int
 */
public int getLocalPort() {
	return LocalPort;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:15:27 AM)
 * @return java.lang.String
 */
public java.lang.String getOwner() {
	return Owner;
}
public java.lang.String getCompany() {
	return Comp;
}
public java.lang.String getCountry() {
	return Country;
}
public java.lang.String getState() {
	return State;
}
public java.lang.String getEmailAddr() {
	return EmailAddr;
}
public java.lang.String getFirstName() {
	return First;
}
public java.lang.String getLastName() {
	return Last;
}
public java.lang.String getProjects() {
	return Projects;
}
public java.lang.String getClassroom() {
	return Classroom;
}
public java.lang.String getEdgeId() {
	return EdgeId;
}
public java.lang.String getRemoteHost() {
	return RemoteHost;
}
public int getRemotePort() {
	return RemotePort;
}
public java.lang.String getBumpHost() {
   return bumphost;
}
public int getBumpPort() {
   return bumpport;
}
public void setBumpHost(java.lang.String s) {
   bumphost = s;
}
public void setBumpPort(int b) {
   bumpport = b;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:15:58 AM)
 * @return java.sql.Timestamp
 */
public java.sql.Timestamp getStartTime() {
	return StartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:23:14)
 * @return java.lang.String
 */
public java.lang.String getXAlias() {
	return XAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:22:30)
 * @return java.lang.String
 */
public java.lang.String getXCookie() {
	return XCookie;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:22:05)
 * @return java.lang.String
 */
public java.lang.String getXDisplay() {
	return XDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:14:12 AM)
 * @param newKey java.lang.String
 */
public void setKey(java.lang.String newKey) {
	Key = newKey;
}
/**
 * Insert the method's description here.
 * Creation date: (9/15/00 11:37:56 AM)
 * @param newLocalHost java.lang.String
 */
public void setLocalHost(java.lang.String newLocalHost) {
	LocalHost = newLocalHost;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 5:13:03 PM)
 * @param newLocalPort int
 */
public void setLocalPort(int newLocalPort) {
	LocalPort = newLocalPort;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:15:27 AM)
 * @param newOwner java.lang.String
 */
public void setOwner(java.lang.String newOwner) {
	Owner = newOwner;
}
public void setCompany(java.lang.String newComp) {
	Comp = newComp;
}
public void setCountry(java.lang.String newCount) {
	Country = newCount;
}
public void setState(java.lang.String v) {
	State = v;
}
public void setEdgeId(java.lang.String newEdge) {
	EdgeId = newEdge;
}
public void setFirstName(java.lang.String v) {
	First = v;
}
public void setLastName(java.lang.String v) {
	Last = v;
}
public void setEmailAddr(java.lang.String v) {
	EmailAddr = v;
}
public void setProjects(java.lang.String v) {
	Projects = v;
}
public void setClassroom(java.lang.String v) {
	Classroom = v;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:16:44 AM)
 * @param newRemoteHost java.lang.String
 */
public void setRemoteHost(java.lang.String newRemoteHost) {
	RemoteHost = newRemoteHost;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 4:22:54 PM)
 * @param newRemotePort int
 */
public void setRemotePort(int newRemotePort) {
	RemotePort = newRemotePort;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:15:58 AM)
 * @param newStartTime java.sql.Timestamp
 */
public void setStartTime(java.sql.Timestamp newStartTime) {
	StartTime = newStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:23:14)
 * @param newXAlias java.lang.String
 */
public void setXAlias(java.lang.String newXAlias) {
	XAlias = newXAlias;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:22:30)
 * @param newXCookie java.lang.String
 */
public void setXCookie(java.lang.String newXCookie) {
	XCookie = newXCookie;
}
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 11:22:05)
 * @param newXDisplay java.lang.String
 */
public void setXDisplay(java.lang.String newXDisplay) {
	XDisplay = newXDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (9/7/00 4:45:12 PM)
 * @param section com.ibm.edesign.collaboration.util.ConfigObject
 */
public void toConfigObject(ConfigObject obj) {
	obj.setProperty(DesktopCommon.DESKTOP_ID, this.Key);
	obj.setProperty(DesktopCommon.R_HOST, this.RemoteHost);
	if ( this.RemotePort >= 6000 ) {
		obj.setProperty(DesktopCommon.DISPLAY, Integer.toString(this.RemotePort-6000));
	}
}
}
