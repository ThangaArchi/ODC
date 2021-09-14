package oem.edge.ed.odc.util;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (07/26/00 09:02:04)
 * @author: Administrator
 */
public class ProcessLauncher {
	private static String START_MEETING = "start_meeting";
	private static String END_MEETING = "end_meeting";
	private static String GET_COOKIE = "get_cookie";
	private static String ADD_SESSION = "add_session";
	private static String GET_DISPLAY = "get_display";
	private static String SET_TITLE = "set_title";
	private static String PROG_PATH = "/afs/eda/u/yang/ebiz/cgi-bin/";
	private static String INVOKE = ""; // was "rsh";
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 10:19:31)
 * @return boolean
 * @param host java.lang.String
 * @param viewOwner java.lang.String
 * @param viewDisplay java.lang.String
 * @param meetingCookie int
 * @param meetingAlias java.lang.String
 */
public synchronized boolean addSession(String host, String viewOwner,
					String viewDisplay, String viewCookie, String meetingCookie, String meetingAlias) {
	int exitVal = -1;

	// need to have view's cookie
	StringBuffer command = new StringBuffer(INVOKE);
	if ( INVOKE.indexOf("rsh") >= 0 ) {
		command.append(" ").append(host).append(" -l ").append(viewOwner).append(" ");
	}
	command.append(PROG_PATH).append(ADD_SESSION).append(" ").append(viewDisplay).append(" ");
	command.append(viewCookie).append(" ").append(meetingCookie).append(" ").append(meetingAlias).append(" ").append(viewOwner);
	try {
		Process child = Runtime.getRuntime().exec(command.toString());
		try {
			child.waitFor();
			exitVal = child.exitValue();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
	} catch ( Exception e ) {
		e.printStackTrace();
	}
		
	System.out.println(command + " completed with rc = " + exitVal);

	return exitVal == 0;
}
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 10:09:42)
 * @return boolean
 * @param host java.lang.String
 * @param owner java.lang.String
 * @param port int
 */
public synchronized boolean endMeeting(String host, String owner, String display) {
	int exitVal = -1;	
	boolean rc = false;

	StringBuffer command = new StringBuffer(INVOKE);
	if ( INVOKE.indexOf("rsh") >= 0 ) {
		command.append(" ").append(host).append(" -l ").append(owner).append(" ");
	}
	command.append(PROG_PATH).append(END_MEETING).append(" ").append(display);
	try {
		Process child = Runtime.getRuntime().exec(command.toString());
		child.waitFor();
		exitVal = child.exitValue();
		rc = true;
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	System.out.println(command + " completed with rc = " + exitVal);
	
	return rc;
}
/**
 * Insert the method's description here.
 * Creation date: (07/19/00 15:07:29)
 * @return String
 * @param host String
 * @param owner java.lang.String
 * @param display String
 */
public synchronized String getCookie(String host, String owner, String display) {
	int rc = -1;
	String cookie = null;
	
	StringBuffer command = new StringBuffer(INVOKE);
	if ( INVOKE.indexOf("rsh") >= 0 ) {
		command.append(" ").append(host).append(" -l ").append(owner).append(" ");
	}
	command.append(PROG_PATH).append(GET_COOKIE).append(" ").append(display);
	Process child = null;
	try {
		child = Runtime.getRuntime().exec(command.toString());


		//if ( rc == 0 ) {
			InputStream from_child = child.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(from_child));
			cookie = rd.readLine();
			from_child.close();
			StringTokenizer tokenizer = new StringTokenizer(cookie, " ");
			if ( tokenizer.countTokens() == 3 ) {
				tokenizer.nextToken();
				tokenizer.nextToken();
				cookie = tokenizer.nextToken();
			} else {
				cookie = null;
			}
		//}
	} catch ( Exception e ) {
		e.printStackTrace();
	}

		try {
			child.waitFor();
			rc = child.exitValue();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		
		System.out.println(command + " completed with rc = " + rc);

	System.out.println(display + " cookie = " + cookie);
	
	return (rc==0) ? cookie : null;
}
/**
 * Insert the method's description here.
 * Creation date: (07/19/00 15:07:29)
 * @return String
 * @param host String
 * @param owner java.lang.String
 * @param alias java.lang.String
 */
public synchronized String getDisplay(String host, String owner, String alias) {
	int rc = -1;
	String disp = null;
	
	StringBuffer command = new StringBuffer(INVOKE);
	if ( INVOKE.indexOf("rsh") >= 0 ) {
		command.append(" ").append(host).append(" -l ").append(owner).append(" ");
	}
	command.append(PROG_PATH).append(GET_DISPLAY)
               .append(" ").append(alias).append(" ").append(owner);
	try {
		Process child = Runtime.getRuntime().exec(command.toString());

		try {
			child.waitFor();
			rc = child.exitValue();
		} catch ( InterruptedException e ) {
			e.printStackTrace();
		}
		
		System.out.println(command + " completed with rc = " + rc);

		if ( rc == 0 ) {
			InputStream from_child = child.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(from_child));
			disp = rd.readLine();
			from_child.close();
		}
	} catch ( Exception e ) {
		e.printStackTrace();
	}

	System.out.println("xmx " + alias + " display = " + disp);
	
	return disp;
}
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 09:44:42)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	boolean rc;
	ProcessLauncher mgr = new ProcessLauncher();
	String host = "iceland.fishkill.ibm.com";
	
	// view port
	int host_port = 1;
	
	// get view cookie
	System.out.println("office-view started at :" + host_port);

	String host_cookie = mgr.getCookie(host, "yang", host+":"+host_port);
	System.out.println("office-view's cookie: " + host_cookie);

	// start xmx
	rc = mgr.startMeeting(host, "yang", host+":"+host_port, "change-plan-meeting");
	if ( rc == false ) {
		return;
	} else {
		System.out.println("change-plan-meeting started.");
	}

	// get xmx display
	String disp = mgr.getDisplay(host, "yang", "change-plan-meeting");
	if ( disp != null ) {
		System.out.println("change-plan-meeting's display = " + disp);
	}
	
	// get xmx cookie
	String meeting_cookie = mgr.getCookie(host, "yang", disp);
	System.out.println("change-plan-meeting's cookie: " + meeting_cookie);

	// start another view
	int home_port = 4;
	System.out.println("office-view started at :" + host_port);
	
	String home_cookie = mgr.getCookie(host, "yang", host+":"+home_port);
	System.out.println("remote-view's cookie: " + home_cookie);

	// add the change-plan-meeting to remote-view
	boolean add_rc = mgr.addSession(host, "yang", host+":"+home_port, home_cookie, meeting_cookie, "change-plan-meeting");
	if ( add_rc ) {
		System.out.println("joined change-plan-meeting from home_view at " + host + ":" + home_port);
	} else {
		System.out.println("cannot join change-plan-meeting from home_view.");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setAddSessionProg(String name) {
	ADD_SESSION = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setEndMeetingProg(String name) {
	END_MEETING = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setGetCookieProg(String name) {
	GET_COOKIE = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setGetDisplayProg(String name) {
	GET_DISPLAY = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setInvokeProg(String name) {
	INVOKE = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/31/00 10:45:06)
 * @param path java.lang.String
 */
public static void setProgPath(String path) {
	PROG_PATH = path;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setStartMeetingProg(String name) {
	START_MEETING = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/19/00 15:07:29)
 * @return boolean
 * @param host String
 * @param owner java.lang.String
 * @param display String
 * @param newTitle String
 */
public synchronized boolean setTitle(String host, String owner, String display, String newTitle) {
	int rc = -1;
        int n = 3;
        int i=0;
        String arr[] = new String[n];
	StringBuffer command = new StringBuffer(INVOKE);
        
          /*
	if ( INVOKE.indexOf("rsh") >= 0 ) {
           System.out.println("GAK! Update for INVOKE setting!");
           command.append(" ").append(host).append(" -l ").append(owner).append(" ").append(PROG_PATH);
           command.append(SET_TITLE).append(" ").append(display).append(" ").append(newTitle);
	}
	command.append(PROG_PATH).append(SET_TITLE).append(" ").append(display).append(" ").append(newTitle);
          */
        
        arr[i++] = PROG_PATH + SET_TITLE;
        arr[i++] = display;
        arr[i++] = newTitle;
	try {
          // Process child = Runtime.getRuntime().exec(command.toString());
          Process child = Runtime.getRuntime().exec(arr);

          try {
             child.waitFor();
             rc = child.exitValue();
          } catch ( InterruptedException e ) {
             e.printStackTrace();
          }
          
          System.out.println(command + " completed with rc = " + rc);
          
	} catch ( Exception e ) {
           e.printStackTrace();
	}

	
	return rc==0?true:false;
}
/**
 * Insert the method's description here.
 * Creation date: (07/27/00 16:41:20)
 * @param name java.lang.String
 */
public static void setTitleProg(String name) {
	SET_TITLE = name;
}
/**
 * Insert the method's description here.
 * Creation date: (07/26/00 10:05:05)
 * @return int
 * @param host java.lang.String
 * @param owner java.lang.String
 * @param viewDisplay java.lang.String
 * @param name java.lang.String
 */
public synchronized boolean startMeeting(String host, String owner, String viewDisplay, String name) {
	boolean rc = false;

	StringBuffer command = new StringBuffer(INVOKE);
	if ( INVOKE.indexOf("rsh") >= 0 ) {
		command.append(" ").append(host).append(" -l ").append(owner).append(" ");
	}
	command.append(PROG_PATH).append(START_MEETING);
        command.append(" ").append(viewDisplay).append(" ").append(name);
        command.append(" ").append(owner);
	
	try {
		Process child = Runtime.getRuntime().exec(command.toString());
		// cannot waitFor() it because xmx blocks

		rc = true;
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	System.out.println(command + " started... with rc = " + rc);
        
       /* JMC 5/25/01 - Don't need this!
	if ( rc ) {
		// wait 5 sec. for xmx to start
		int timeout = 1000 * 5;
		try {
			Thread.sleep(timeout);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
       */

	return rc;
}
}
