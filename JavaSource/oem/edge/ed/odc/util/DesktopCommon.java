package oem.edge.ed.odc.util;

/**
 * Defines the keywords to be used to write data in .ini format
 * Creation date: (07/17/00 15:22:40)
 * @author: D.Y.Yang
 */
public class DesktopCommon {

	// desktop.ini keywords
	public final static java.lang.String INVITATIONS = "invitations";
	public final static java.lang.String INVITATION = "invitation";
	public final static java.lang.String OWNER = "owner";
	public final static java.lang.String EMAILADDR = "EmailAddr";
	public final static java.lang.String FIRST = "FIRST";
	public final static java.lang.String LAST = "LAST";
	public final static java.lang.String EDGEOWNER = "edgeowner";
	public final static java.lang.String START = "start-time";
	public final static java.lang.String ID = "id";
	public final static java.lang.String STATUS = "status";
	public final static java.lang.String INVITEE = "invitee";
	public final static java.lang.String NAME = "name";
	public final static java.lang.String ALIAS = "alias";
	public final static java.lang.String COOKIE = "cookie";
	public final static java.lang.String COMPACT_LOGIN = "compact-login";
	public final static java.lang.String R_HOST = "remote-host";
	public final static java.lang.String L_HOST = "local-host";
	public final static java.lang.String L_PORT = "local-port";
	public final static java.lang.String BUMPHOST = "bump-host";
	public final static java.lang.String BUMPPORT = "bump-port";
	public final static java.lang.String PASSPHRASE = "passphrase";
	public final static java.lang.String NEWTOKEN = "new-token";
	public final static java.lang.String TOKENID = "compname";
        
	public final static java.lang.String ANSWER  = "ANSWER";
	public final static java.lang.String WHOAMI_CMD  = "WHOAMI";
	public final static java.lang.String QUERY_IBM_CMD = "query-ibm-id";
	public final static java.lang.String QUERY_SAME_COMP_CMD 
                                                       = "query-same-company";
	public final static java.lang.String MAKEXCHANNEL_CMD 
                                                       = "make-x-channel";

	// invitation states
	public final static int TRANSITION = 0; // in transition: from sender to receiver
	public final static int PENDING = 1;    // delivered
	public final static int ACCEPTED = 2;   // accepted
	public final static int REJECTED = 3;   // rejected
	public final static int OFFLINE = 4;    // user does not have a desktop

	// servlet commands
	public final static java.lang.String START_CMD = "Login";
	public final static java.lang.String STARTHOSTING_CMD = "HostingLogin";
	public final static java.lang.String STARTEDUCATION_CMD = "EducationLogin";
	public final static java.lang.String STARTFTP_CMD = "Pftp";
	public final static java.lang.String DOWNLOAD_APPLICATION_CMD = "DownloadApplication";
	public final static java.lang.String BIND_CMD = "BindDesktop";
	public final static java.lang.String START_XMX_CMD = "StartXmx";
	public final static java.lang.String END_XMX_CMD = "EndXmx";
	public final static java.lang.String SEND_INVITATION_CMD = "Invite";
	public final static java.lang.String ACCEPT_INVITATION_CMD = "Accept";
	public final static java.lang.String REJECT_INVITATION_CMD = "Reject";
	public final static java.lang.String DROPPED_PARTICIPANT_CMD = "Dropped";
	public final static java.lang.String REFRESH_CMD = "Refresh";
	public final static java.lang.String GET_VIEWER_CMD = "GetViewer";
	public final static java.lang.String END_MEETING_CMD = "EndMeeting";
	public final static java.lang.String NOTIFY_CMD = "Notify";

	// servlet request parameters: reuse the keywords defined for .ini
	// public final static java.lang.String NAME = "name";
	// public final static java.lang.String ID = "id";
	// public final static java.lang.String ALIAS = "alias";
	// public final static java.lang.String COOKIE = "cookie";
	// public final static java.lang.String OWNER = "owner";
	// public final static java.lang.String INVITEE = "invitee";
	public final static java.lang.String DESKTOP_ID = "desktopid";
	public final static java.lang.String DISPLAY = "display";

	// user registration
	public final static java.lang.String U_ID = "U_NAME";
	public final static java.lang.String HOST = "COMP_SERVER";
	public final static java.lang.String F_NAME = "F_NAME";
	public final static java.lang.String MI = "MI";
	public final static java.lang.String L_NAME = "L_NAME";
	public final static java.lang.String COMPANY = "COMPANY";
	public final static java.lang.String COUNTRY = "COUNTRY";
	public final static java.lang.String E_MAIL = "E_MAIL";
	public final static java.lang.String PASSWD = "PASSWD";

	// ini keywords not used in phase 1
	public final static java.lang.String MEETINGS = "meetings";
	public final static java.lang.String MEETING = "meeting";
	public final static java.lang.String VIEWS = "views";
	public final static java.lang.String VIEW = "view";
	public final static java.lang.String INVITEES = "invitees";
	public final static java.lang.String PARTICIPANTS = "participants";
	public final static java.lang.String USERS = "online-users";

	public final static java.lang.String URL = "url";

	// servlet commands not used in phase 1
	public final static java.lang.String ADD_VIEW_CMD = "AddView";
	public final static java.lang.String DROP_VIEW_CMD = "DropView";
	public final static java.lang.String HOST_MEETING_CMD = "HostMeeting";
	public final static java.lang.String REJOIN_MEETING_CMD = "RejoinMeeting";

	public final static java.lang.String JOIN_SESSION_CMD = "JoinSession";
	public final static java.lang.String DROP_SESSION_CMD = "DropSession";
	public final static java.lang.String WITHDRAW_INVITATION_CMD = "Drop";
}
