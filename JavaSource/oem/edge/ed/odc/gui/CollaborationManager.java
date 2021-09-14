package oem.edge.ed.odc.gui;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.*;
import com.ibm.as400.webaccess.common.*;

class ConfirmSendDialog extends Dialog implements ActionListener {
   Frame f;
   private CollaborationManager cm;
   protected Button dialogOKButton;
   protected Button dialogDeclineButton;
   protected Panel buttonPanel;
   protected Label newInvitationLabel;
   
   public ConfirmSendDialog(Frame inF, CollaborationManager inCM) {
      super(inF, "Confirm Invitation to NON-IBMer");
       
      f = inF;
      cm = inCM;
      System.out.println("In New Meeting Dialog");
      setSize(400, 100);
      setBackground(Color.lightGray);
      setLocation(cm.getXLOC(), cm.getYLOC());
      newInvitationLabel = new Label(
         "Confirm or Cancel sending invitation to NON-IBMer: " +
         cm.getLastInvitee());
         
      dialogOKButton = new Button("Confirm");
      dialogOKButton.addActionListener(this);
      dialogDeclineButton = new Button("Cancel");
      dialogDeclineButton.addActionListener(this);
      buttonPanel = new Panel();
      buttonPanel.setLayout(new GridLayout(1, 2, 0, 0));
      buttonPanel.add(dialogOKButton);
      buttonPanel.add(dialogDeclineButton);
      add(newInvitationLabel, "North");
      add(buttonPanel, "South");
      show();
   }
/**
 * Insert the method's description here.
 * Creation date: (7/21/00 8:55:59 PM)
 */
   public void actionPerformed(ActionEvent e) {
      String s = null;
      if ((e.getSource() == dialogOKButton)) {
         cm.sendInvitationI();
         dispose();
      }
      if ((e.getSource() == dialogDeclineButton)) {
         s = "Cancel invitation to NON-IBMer " + cm.getLastInvitee();
         cm.appendDeclineText(s);
         dispose();
      }
   }
}

public class CollaborationManager extends Frame implements ActionListener, TextListener, Runnable {
	private String ProgPath;
	private GridBagConstraints gbConstraints; 
	private GridBagLayout gbLayout;
	private Thread t;
	private String remotehost;
	private String localport;
	private boolean writelocalport = true;
	private String host;
	final boolean DEBUG = true; // DEBUG
	private DesktopCommon common = null;
	private ProcessLauncher launcher = null;
	private String xmxCookie;
	private String icaCookie;
	private String xmxPort;
	private int xloc = 300;
	private int yloc = 300;
	private String tempURL; 
	private String DisplayPort;
	private String ICAPort;   
	private String DesktopID;
	private String aliasName;
	private String meetingTitle;
	private String inviteeName;
	private TextArea eventTextArea;
	private TextField inviteeTextField;
	private TextField titleTextField;
	protected Button sendButton;
	protected Button logoutButton;
	protected Button viewButton;
	protected Label invitationLabel;
	private TextField statusField;
	private String invitationName;
	private String invitationOwner;
	private String invitationID;
	private String invitationStart;
	private String invitationURLString;
	private String invitationBaseURL;
	private int invitationState;
	private String invitationSentTo;
	private String invitationCookie;
	private String invitationAlias;
	private String invitationDisplay;
	private String viewURLString;
	private String viewBaseURL;
	private String whoIam; //	Vector invitations;
        
       // JMC 11/15/00 
        private final java.lang.String ResourceName = "edesign_edodc_desktop";
        private java.util.PropertyResourceBundle AppProp = null;
              
        
	Vector invitations;
/**
 * The Collaboration Manager Constructor creates the GUI window components
 * starts XMX
 * gets the XMX Disay Port
 * gets XMX cookie
 * gets ICA cookie
 * and then builds the window
 * Creation date: (8/9/00 2:00:26 AM)
 */

   public String getLastInvitee() { 
      return inviteeName;
   }
   
public CollaborationManager(String displayport, String icaport, String desktopid, String host, String tempurl, String progpath) {
	super("Meeting Manager");
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent event) {
			dispose();
			System.exit(0);
		}
	});
        
       // Get application properties
        try {
           AppProp = (PropertyResourceBundle)
              PropertyResourceBundle.getBundle(ResourceName);
        } catch ( Exception e ) {
           System.out.println("Error reading ResourceBundle for " + ResourceName);
           e.printStackTrace();
        }
        
	this.ICAPort = icaport;
	this.DisplayPort = displayport;
	this.DesktopID = desktopid;
	this.host = host;
	this.tempURL = tempurl;
	this.ProgPath = progpath;
	setTitle("Meeting Manager");
	Label inviteeLabel = new Label("Invitee");
        
       /*
	Label titleLabel = new Label("Meeting Title");
	titleTextField = new TextField();
	titleTextField.addActionListener(this);
	titleTextField.setBackground(Color.lightGray);
	titleTextField.addTextListener(this);
	titleTextField.setColumns(30);
       */
        
	inviteeTextField = new TextField();
	inviteeTextField.addActionListener(this);
	inviteeTextField.setBackground(Color.lightGray);
	inviteeTextField.addTextListener(this);
	inviteeTextField.setColumns(40);
	sendButton = new Button("Send");
	sendButton.addActionListener(this);
	sendButton.setEnabled(false);
	logoutButton = new Button("Logout");
	logoutButton.addActionListener(this);
	eventTextArea = new TextArea();
	eventTextArea.setBackground(Color.lightGray);
	TextArea acceptedTextArea = new TextArea();
	boolean noedit = false;
	eventTextArea.setEditable(noedit);
        
//	Panel titlePanel = new Panel();
	Panel buttonPanel = new Panel();
	Panel inviteePanel = new Panel();
        
       /*
	titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	titlePanel.add(titleLabel);
	titlePanel.add(titleTextField);
       */
        
	inviteePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	//inviteePanel.setLayout(new BorderLayout());
	inviteePanel.add(inviteeLabel);
	inviteePanel.add(inviteeTextField);
	inviteePanel.add(eventTextArea);
	//inviteePanel.add(acceptedTextArea);
	buttonPanel.add(sendButton);
	buttonPanel.add(logoutButton);
	// DYY: disable it
	sendButton.setEnabled(false);
	////	add(titlePanel, "North");
	////	add(inviteePanel, "Center");
	////	add(buttonPanel, "South");



	///////////////////////////////////////////////////////////////////
	gbLayout = new GridBagLayout();
	setLayout(gbLayout);
	gbConstraints = new GridBagConstraints();
	sendButton.setEnabled(false);
	gbConstraints.anchor = GridBagConstraints.EAST;
	gbConstraints.fill = GridBagConstraints.BOTH;
	gbConstraints.insets = (new Insets(10, 10, 10, 10));
	gbConstraints.weightx = 1000;
	gbConstraints.weightx = 1000;
//	addComponent(titlePanel, 0, 1, 1, 1);
	addComponent(inviteePanel, 3, 1, 1, 1);
	gbConstraints.weighty = 5;
	addComponent(eventTextArea, 10, 1, 1, 3);
	addComponent(buttonPanel, 14, 1, 1, 3);
	setSize(550, 380);
	///////////////////////////////////////////////////////////////////////
	//	setSize(500, 300);
	setBackground(Color.gray);
	t = new Thread(this);
	whoIam = System.getProperty("user.name");
	//String urlname =System.getenv();//  System.getProperty("theurlname");
	//System.out.println("urlname===========   " + System.getenv());
	// Get the URL to replace tempURL from properties	
	//String host = "trainman.fishkill.ibm.com"; // machine name
	//Construct the aliasName
	long mills = System.currentTimeMillis();
	Long amills = new Long(mills);
	String x = amills.toString(mills);
	aliasName = "meeting" + x;
	// The DesktopCommon class contains all of the constants used in the ini file and comman names
	// ised to call the servlet. 
	common = new DesktopCommon();
	// The ProcessLauncher class contains the interfaces for the native afs calls.
	launcher = new ProcessLauncher();

	// Set the ProgPath
	if (ProgPath != null) {
		launcher.setProgPath(ProgPath);
		System.out.println("Setting the ProgPath");
	}

	
	// Start a meeting
	launcher.startMeeting(host, whoIam, getDisplayPort(), aliasName);

	// get the Display///////////////////////////////////////////
	int displayCtr = 0;
	while (xmxPort == null && displayCtr < 3) {
		xmxPort = launcher.getDisplay(host, whoIam, aliasName);
		System.out.println("getDisplay try number " + displayCtr);
		if (xmxPort == null) {
			displayCtr++;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
		} else {
			System.out.println("displayCnt   " + displayCtr);
			System.out.println("xmxPort   " + xmxPort);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			break;
		}
		//if (displayCtr == 1) {xmxPort = "1223";}
		if (displayCtr == 2) {
			System.out.println("Exiting because there is no display port");
			System.exit(1);
		}
	}
	/////////////////////////////////////////////////////////////////////////////
	//xmxPort = "12"; //TEMP

	// get xmx Cookie///////////////////////////////////////////
	displayCtr = 0;
	System.out.println("before xmx Cokie loop");
	while (xmxCookie == null && displayCtr < 3) {
		System.out.println("in xmx Cookie loop");
		xmxCookie = launcher.getCookie(host, whoIam, xmxPort);
		if (xmxCookie == null) {
			displayCtr++;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
		} else {
			System.out.println("displayCnt   " + displayCtr);
			System.out.println("xmxCookie   " + xmxCookie);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			break;
		}
		if (displayCtr == 2) {
			System.out.println("Exiting because there is no xmx Cookie");
			System.exit(1);
		}
	}
	/////////////////////////////////////////////////////////////////////////////

	// get ica Cookie///////////////////////////////////////////
	displayCtr = 0;
	System.out.println("before ica Cokie loop");
	while (icaCookie == null && displayCtr < 3) {
		System.out.println("in ica Cookie loop");
		icaCookie = launcher.getCookie(host, whoIam, getDisplayPort());
		if (icaCookie == null) {
			displayCtr++;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
		} else {
			System.out.println("displayCnt   " + displayCtr);
			System.out.println("icaCookie   " + icaCookie);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			break;
		}
		if (displayCtr == 2) {
			System.out.println("Exiting because there is no ica Cookie");
			System.exit(1);
		}
	}
	/////////////////////////////////////////////////////////////////////////////
	// Get xmx Cookie
	//	xmxCookie = launcher.getCookie(host, whoIam, xmxPort);
	//	System.out.println("xmxCookie    " + xmxCookie);
	//xmxCookie = "12335";  //TEMP
	// Get citrix Cookie
	//	icaCookie = launcher.getCookie(host, whoIam, getDisplayPort());
	//	System.out.println("icaPort   " + icaCookie);


	createInitialMeeting();
	show();
	t.start();
}
/**
 * Handles the processng of the send button, logout button, and text areas   
 * Creation date: (7/21/00 2:46:10 AM)
 */
public void actionPerformed(ActionEvent e) {
	// The send invitation button was presed

	if (e.getSource() == sendButton) {
        
          //	meetingTitle = titleTextField.getText();
		inviteeName = inviteeTextField.getText();
		//Record the invitation request in the TextArea
//		eventTextArea.append("Invitation Sent for MEETING: " + meetingTitle + " TO: " + inviteeName + "\n");
          //	eventTextArea.append("Invitation Sent TO: " + inviteeName + "\n");
		inviteeTextField.setText(null);
		sendButton.setEnabled(false);
//		launcher.setTitle(host, whoIam, xmxPort, meetingTitle);
		sendInvitation(); // sends info to servlet
		// Set meeting title in the XMX Session
		System.out.println("after INVITATION is SENT");
		//launcher.setTitle(host, whoIam, xmxPort, meetingTitle);
	} else
		if (e.getSource() == logoutButton) {
			System.out.println("Logout");
			launcher.endMeeting(host, whoIam, xmxPort);
			try {
				URL url = new URL(tempURL + common.END_MEETING_CMD);
				HttpMessage msg = new HttpMessage(url);
				Properties props = new Properties();
				props.put(common.DESKTOP_ID, DesktopID); // DesktopID
				msg.sendGetMessage(props);
			} catch (MalformedURLException ee) {
				System.out.println("MalformedURLException thrown in actionPerformed:" + tempURL.toString());
                                ee.printStackTrace();
			} catch (IOException eee) {
				System.out.println("IOException thrown in actionPerformed");
			}
			System.exit(0);
		} else
                   if (/*e.getSource() == titleTextField ||*/ e.getSource() == inviteeTextField) {
                           if (/*titleTextField.getText().length() > 0 &&*/ inviteeTextField.getText().length() > 0)
					
					//sendButton.setEnabled(true);
					///////////////////////////////////////////////////////
					{
//					System.out.println("Title Length   " + titleTextField.getText().length());
					System.out.println("Invitee Length   " + inviteeTextField.getText().length());
//					meetingTitle = titleTextField.getText();
					inviteeName = inviteeTextField.getText();
					//Record the invitation request in the TextArea
//					eventTextArea.append("Invitation Sent for MEETING: " + meetingTitle + " TO: " + inviteeName + "\n");
                                       // eventTextArea.append("Invitation Sent TO: " + inviteeName + "\n");
					inviteeTextField.setText(null);
					sendButton.setEnabled(false);
					sendInvitation(); // sends info to servlet
					// Set meeting title in the XMX Session
//					launcher.setTitle(host, whoIam, xmxPort, meetingTitle);
				}
				///////////////////////////////////////////////////////
			}
}
/**
 * Insert the method's description here.
 * Creation date: (9/13/00 10:09:53 PM)
 */

public void addComponent(Component c, int row, int column, int width, int height) {
	gbConstraints.gridx = column;
	gbConstraints.gridy = row;
	gbConstraints.gridwidth = width;
	gbConstraints.gridheight = height;
	gbLayout.setConstraints(c, gbConstraints);
	add(c);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/00 2:15:24 AM)
 * @param meetID java.lang.String
 * @param userID java.lang.String
 */
public void addUserRequest(String meetID, String userID) {
	System.out.println("send an invitation to a meeting");
	//String temp = meetingBaseURL + "/" + INVITE;
	try {
		URL url = new URL(tempURL);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		props.put("meeting", meetID);
		props.put("user", userID);
		msg.sendGetMessage(props);
	} catch (Exception e) {
	};
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 11:11:12 PM)
 */
public void appendAcceptText(String s) {
	//Font newFont = new Font("TimesRoman",Font.PLAIN,12);
	//eventTextArea.setFont(newFont);
	eventTextArea.append(s + "\n");
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 11:11:12 PM)
 */
public void appendDeclineText(String s) {
	//	Font newFont = new Font("TimesRoman",Font.BOLD,12);
	//	eventTextArea.setFont(newFont);
	eventTextArea.append(s + "\n");
}
/**
 * Make an initial call to the servlet when a meeting is created.
 * Creation date: (8/24/00 8:21:18 PM)
 */
public void createInitialMeeting() {
	System.out.println("initial call to servlet when a meeting is created");
	try {
		URL url = new URL(tempURL + common.BIND_CMD);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		/// temp
		// xmxCookie = "12142weererer";
		// xmxPort = "1233";
		//
		props.put(common.DESKTOP_ID, DesktopID); // DesktopID
		props.put(common.OWNER, whoIam); // User Name
		props.put(common.COOKIE, xmxCookie);
		props.put(common.DISPLAY, xmxPort);
		props.put(common.ALIAS, aliasName);
		InputStream in = msg.sendGetMessage(props);
		getLocalPortFromINI(in);
		//msg.sendGetMess(props);
		System.out.println("URL in create meeting" + url);
	} catch (MalformedURLException e) {
                System.out.println("MalformedURLException thrown in create Meeting:" + tempURL.toString());
	} catch (IOException e) {
		System.out.println("IOException thrown in create Meeting");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/00 12:46:03 AM)
 */
public void createInvitation(String name, String owner, String start, String ID, int state, String sentto, String cookie, String alias) {
	Invitation invitation = new Invitation(name, owner, ID, start, state, sentto, cookie, alias);
	invitations.addElement(invitation);
	// if (invitationState.equals("pending")) {
	if ( state == DesktopCommon.PENDING ) {
		xloc = xloc + 50;
		yloc = yloc + 50;
		Frame f = new Frame();
		f.setTitle("A");
		InvitationDialog nm = new InvitationDialog(f, this, invitation);
	}
	// if (invitation.getState().equals("accept")) {
	if ( state == DesktopCommon.ACCEPTED ) {
		appendAcceptText(invitation.getSentTo() + " ACCEPTED an invitation for a meeting called by " + invitation.getMeetingHost());
	}
	// if (invitation.getState().equals("rejected")) {
	else if ( state == DesktopCommon.REJECTED ) {
		appendDeclineText(invitation.getSentTo() + " REJECTED an invitation for a meeting called by " + invitation.getMeetingHost());
	} else if ( state == DesktopCommon.OFFLINE ) {
		appendDeclineText("Cannot invite " + invitation.getSentTo() + ": user is not online.");

	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/00 12:46:03 AM)
 */
public void createInvitation(String name, String owner, String start, String ID, int state, String sentto, String cookie, String alias, String display) {
	Invitation invitation = new Invitation(name, owner, ID, start, state, sentto, cookie, alias, display);
	invitations.addElement(invitation);
	if (state == DesktopCommon.PENDING) {
		xloc = xloc + 50;
		yloc = yloc + 50;
		Frame f = new Frame();
		f.setTitle("A");
		InvitationDialog nm = new InvitationDialog(f, this, invitation);
	}
	if (state == DesktopCommon.ACCEPTED) {
		appendAcceptText(invitation.getSentTo() + " ACCEPTED an invitation for a meeting called by " + invitation.getMeetingHost());
		//	    getLauncher().addSession(getHost(), getWhoIam(),invitation.getDisplay(),invitation.getCookie(), icaCookie, invitation.getAlias());
	} else
		if (state == DesktopCommon.REJECTED) {
			appendDeclineText(invitation.getSentTo() + " REJECTED an invitation for a meeting called by " + invitation.getMeetingHost());
		} else
			if (state == DesktopCommon.OFFLINE) {
				appendDeclineText("Cannot invite " + invitation.getSentTo() + ": user is not online.");
			}
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:06:44 PM)
 */
public void createMeeting(String name, String owner, String start, String id, String invitees, String participants) {
/*
	System.out.println("Create a Meeting");
	String hostMeeting = "NO";
	if (owner.equals(whoIam))
		hostMeeting = "YES";
	Vector invited = new Vector();
	Vector participating = new Vector();
	Vector aaa = new Vector();
	StringTokenizer st = new StringTokenizer(invitees, "|");
	int count = st.countTokens();
	for (int i = 0; i < count; i++) {
		invited.addElement(st.nextToken());
	}
	StringTokenizer st2 = new StringTokenizer(participants, "|");
	int count2 = st2.countTokens();
	for (int i = 0; i < count2; i++) {
		participating.addElement(st2.nextToken());
	}

	//////
	boolean matched = false;
	for (int i = 0; i < count; i++) {
		String a = (String) invited.elementAt(i);
		for (int j = 0; j < count2; j++) {
			if (a.equals(participating.elementAt(j))) {
				invited.removeElementAt(i);
				count--;
				break;
			}
		}
	}
	//////

	//System.out.println("tokens: " + count);
	//	Meeting meeting = new Meeting(name, start, id, invited, participating,hostMeeting);
	//	meetings.addElement(meeting);
*/
	}
/**
 * Insert the method's description here.
 * Creation date: (7/26/00 10:09:20 PM)
 */
public void createURL(String urlString) {
	try {
		System.out.println("IN Create URL");
		System.out.println("URL String " + urlString);
		////URL url = getCodeBase();
		/////String s = (url.toString() + ":" + url.getPort() + urlString);
		//String s = (url.toString() + ":" + url.getPort() + urlString);
		//////System.out.println("String s   " + s);
	} catch (Exception e) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/00 10:09:20 PM)
 */
public void createURL(String urlString, String type) {
	try {
		System.out.println("IN Create URL");
		System.out.println("URL String " + urlString);
		////URL url = getCodeBase();
		if (type.equals("M")) {
			//	meetingBaseURL = (url.toString() + ":" + url.getPort() + urlString);
			//meetingBaseURL = (meetingURLString) ;
			//System.out.println("urlString in createURL = " + urlString);
			//System.out.println("meetingBaseURL in createURL = " + meetingBaseURL);
		}
		if (type.equals("V")) {
			//	viewBaseURL = (url.toString() + ":" + url.getPort() + urlString);
			System.out.println("viewBaseURL = " + viewBaseURL);
		}
		if (type.equals("I")) {
			//	invitationBaseURL = (url.toString() + ":" + url.getPort() + urlString);
			System.out.println("invitationBaseURL = " + invitationBaseURL);
		}
	} catch (Exception e) {
	}
}
/**
 * Create the Poll request to be sent to the servlet
 * Creation date: (8/24/00 11:19:17 PM)
 */
public InputStream doPoll() {
	// System.out.println("polling call to servlet from polling thread ");
	InputStream in = null;
	try {
		//System.out.println("In doPoll");
		URL url = new URL(tempURL + common.REFRESH_CMD);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		props.put(common.DESKTOP_ID, DesktopID); // DesktopID
		//System.out.println("before sendGetMessage");
		
		in = msg.sendGetMessage(props);
		//System.out.println("after sendGetMessage");
		//msg.sendPostMess(props);
		//msg.sendGetMess(props);
		//Exit because the user name is unknown to the server
		//System.out.println("response   " +  msg.getResponse());
		//if (msg.getResponse() == "exit") System.out.println("EXiting");
		
		// System.out.println("URL=====  " + url);
	} catch (MalformedURLException e) {
		System.out.println("MalformedURLException thrown in doPoll");
	} catch (IOException e) {
		System.out.println("IOException thrown in doPoll");
	}
	return in;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 10:04:08 PM)
 */
public String getDesktopID() {
	return DesktopID;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 8:02:53 PM)
 * @return java.lang.String
 */
public String getDisplayPort() {
	return DisplayPort;
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:28:11 PM)
 */
public String getHost() {
	return host;
	}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:37:28 PM)
 * @return java.lang.String
 */
public String getICACookie() {
	return icaCookie;
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:38:34 PM)
 * @return java.lang.String
 */
public String getICAPort() {
	return ICAPort;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 10:02:28 PM)
 */

public void getINI(InputStream in) {
	String sectionName;
	Vector v;
	Vector vv;
	Vector w;
	Vector ww;
	Vector x;
	Vector xx;
       //System.out.println("In GetINI");
	//meetings = new Vector();
	invitations = new Vector();
	//views = new Vector();
	try {
		ConfigFile cfile = null; 
		if (in == null) {
			System.out.println("InputStream is null");
			System.exit(1);
			//return;
		}
		//cfile = new ConfigFile("e:\\desktop.ini");
		// cfile = new ConfigFile("/afs/eda/u/dziedzic/public/desktop.ini");
		else
			cfile = new ConfigFile(in);
               //	System.out.println("in ConfigFIle");
		//	ConfigFile cfile = new ConfigFile("/afs/eda/u/shingte/public_html/demo/meeting/desktop.ini");

		// DYY: cannot assume a section always exists; otherwise: nullPointerException
		/*
		v = cfile.getSection("MEETINGS");
		//meetingURLString = cfile.getProperty("url");
		//System.out.println("Meeting URL: " + meetingURLString);
		for (int i = 0; i < v.size(); i++) {
		System.out.println("size:  " + v.elementAt(i));
		ConfigSection section = (ConfigSection) v.elementAt(i);
		//meetingURLString = section.getProperty("url");
		//System.out.println("Meeting URL: " + meetingURLString);
		vv = section.getSection("MEETING");
		for (int j = 0; j < vv.size(); j++) {
		ConfigSection sec = (ConfigSection) vv.elementAt(j);
		//	meetingName = sec.getProperty("name");
		//	meetingOwner = sec.getProperty("owner");
		//	meetingStart = sec.getProperty("start-time");
		//	meetingID = sec.getProperty("id");
		//	meetingInvitees = sec.getProperty("invitees");
		//	meetingParticipants = sec.getProperty("participants");
		//createMeeting(meetingName, meetingOwner, meetingStart, meetingID, meetingInvitees, meetingParticipants);
		}
		}
		//
		
		
		w = cfile.getSection("VIEWS");
		for (int i = 0; i < w.size(); i++) {
		System.out.println("size:  " + w.elementAt(i));
		ConfigSection section = (ConfigSection) w.elementAt(i);
		viewURLString = section.getProperty("url");
		System.out.println("View URL: " + viewURLString);
		ww = section.getSection("VIEW");
		for (int j = 0; j < ww.size(); j++) {
		ConfigSection sec = (ConfigSection) ww.elementAt(j);
		//viewName = sec.getProperty("name");
		//viewOwner = sec.getProperty("remote-host");
		//viewID = sec.getProperty("id");
		//viewStart = sec.getProperty("start-time");
		//				System.out.println("View Name:  " + viewName);
		//		System.out.println("View Owner:  " + viewOwner);
		//System.out.println("View ID:  " + viewID);
		//System.out.println("View Start:  " + viewStart);
		//createView(viewName, viewOwner, viewStart, viewID);
		}
		}
		*/
		//
		localport = cfile.getProperty(common.DISPLAY);
		remotehost = cfile.getProperty(common.R_HOST);
                if (remotehost == null) {
                   remotehost = "localhost";
                }
                
		if (localport == null) writelocalport = false;
			else writelocalport = true;
		if (writelocalport) {
			writelocalport = false;
			eventTextArea.append("To display an X Application within this meeting," + "\n");
			eventTextArea.append("set your DISPLAY envinronment variable " + " on\nyour BROWSER host as follows:\n\n");
			eventTextArea.append("    export DISPLAY=localhost" + ":" + localport + "   # ksh syntax\n");
			eventTextArea.append("    setenv DISPLAY localhost" + ":" + localport + "   # csh syntax\n\n");
			System.out.println("localPort    " + localport);
		}
		int j = 0;
		x = cfile.getSection(common.INVITATIONS);
		if (x != null) {
			for (int i = 0; i < x.size(); i++) {
				//	System.out.println("size:  " + v.elementAt(i));
				ConfigSection section = (ConfigSection) x.elementAt(i);
				invitationURLString = section.getProperty("url");
				System.out.println("Invitation URL: " + invitationURLString);
				xx = section.getSection(common.INVITATION);
				for (j = 0; j < xx.size(); j++) {
					// System.out.println("j" + j);
					System.out.println("xx.size " + xx.size());
					ConfigSection sec = (ConfigSection) xx.elementAt(j);
					System.out.println("sec:  " + sec);
					invitationName = sec.getProperty(common.NAME);
					System.out.println("Invite Name:  " + invitationName);
					invitationOwner = sec.getProperty(common.OWNER);
					System.out.println("Invite Owner:  " + invitationOwner);
					invitationID = sec.getProperty(common.ID);
					System.out.println("Invite ID:  " + invitationID);
					invitationStart = sec.getProperty(common.START);
					System.out.println("Invite Start:  " + invitationStart);
					invitationState = sec.getIntProperty(common.STATUS, common.PENDING);
					System.out.println("Invite State:  " + invitationState);
					invitationSentTo = sec.getProperty(common.INVITEE);
					System.out.println("Invite Send To:  " + invitationSentTo);
					invitationCookie = sec.getProperty(common.COOKIE);
					System.out.println("Invite Cookie:  " + invitationCookie);
					invitationAlias = sec.getProperty(common.ALIAS);
					System.out.println("Invite Alias:  " + invitationAlias);
					invitationDisplay = sec.getProperty(common.DISPLAY);
					System.out.println("Invite Display:  " + invitationDisplay);
					//setInvitationLabel();
					createInvitation(invitationName, invitationOwner, invitationStart, invitationID, invitationState, invitationSentTo, invitationCookie, invitationAlias, invitationDisplay);
				}
				//invitationLabel.setText("           " + j + " Invitations Waiting");
			}
		}
		//
		//	
	} catch (Exception e) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:06:00 PM)
 * @return com.ibm.edesign.collaboration.util.ProcessLauncher
 */
public ProcessLauncher getLauncher() {
	return launcher;
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/00 2:00:18 AM)
 */
public void getLocalPortFromINI(InputStream in) {
	try {
		ConfigFile cfile;
		if (in == null) {
			//	     cfile = new ConfigFile("e:\\desktop.ini");
			System.out.println("InputStream is null");
			System.exit(1);
			//return;
		} else {
			System.out.println("Reading DesktopID");
			cfile = new ConfigFile(in);
			DesktopID = cfile.getProperty(common.DESKTOP_ID);
			tempURL = cfile.getProperty(common.URL);
			System.out.println("******  New URL = " + tempURL);
			System.out.println("******  NEW DESKTOP_ID = " + DesktopID);
		}
		//		eventTextArea.append("In order to set X Applications to run within this meeting" +"\n");
		//	eventTextArea.append("set your DISPLAY to Port  " + localPort  +"\n");
		//	eventTextArea.append("EXAMPLE:    export DISPLAY=machineName:"+localPort +"\n");
		//		System.out.println("localPort    " + localPort );
	} catch (Exception e) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 11:32:17 PM)
 */
public String getURL() {
	return tempURL; }
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 8:30:19 PM)
 * @return java.lang.String
 */
public String getWhoIam() {
	return whoIam;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 11:04:25 PM)
 * @return int
 */
public int getXLOC() {
	return xloc;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 11:04:51 PM)
 * @return int
 */
public int getYLOC() {
	return yloc;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/00 2:00:26 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	String DisplayPort = null;
	String ICAPort = null;
	String DesktopID = null;
	String Host = null;
	String TempURL = null;
	String ProgPath = null;
        
        try {
           com.ibm.net.www.https.SecureGlue.setKeyRing("EDesignKeyring", "0x120163");
        } catch(ClassNotFoundException e) {
           System.out.println("CollaborationMgr: Yikes ... Keyring not found!");
        }
        
	/////////////////////////////////
	//Exit if there is an uneven numbr of parameters
	//System.out.println("Length of Parms in:  " + args.length);
	if (args.length % 2 != 0) {
		System.out.println("Exiting due to incorrect parameter specification");
		System.exit(1);
	}
	for (int i = 0; i < args.length; i++) {
		if (args[i].equals("+DISPLAYPORT")) {
			DisplayPort = args[i + 1];
			if (DisplayPort.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
		if (args[i].equals("+ICAPORT")) {
			ICAPort = args[i + 1];
			if (ICAPort.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
		if (args[i].equals("+DESKTOPID")) {
			DesktopID = args[i + 1];
			if (DesktopID.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
		if (args[i].equals("+HOST")) {
			Host = args[i + 1];
			if (Host.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
		if (args[i].equals("+URL")) {
			TempURL = args[i + 1];
			if (TempURL.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
		if (args[i].equals("+PROGPATH")) {
			ProgPath = args[i + 1];
			if (ProgPath.substring(0, 1).equals("+")) {
				System.out.println("Exiting because there are two adjacent parameters");
				System.exit(1);
			}
		}
	}
	System.out.println("DisplayPort  " + DisplayPort);
	System.out.println("ICAPort " + ICAPort);
	System.out.println("DesktopID  " + DesktopID);
	System.out.println("Host  " + Host);
	System.out.println("TempURL  " + TempURL);
	System.out.println("ProgPath  " + ProgPath);
	if (DisplayPort == null) {
		System.out.println("Exiting because the Display Port is null");
		System.exit(1);
	}
	if (ICAPort == null) {
		System.out.println("Exiting because the ICA Port is null");
		System.exit(1);
	}
	if (DesktopID == null) {
		System.out.println("Exiting because the DesktopID is null");
		System.exit(1);
	}
	if (Host == null) {
		System.out.println("Exiting because the Host is null");
		System.exit(1);
	}
	if (TempURL == null) {
		System.out.println("Exiting because the TempURL is null");
		System.exit(1);
	}
	if (ProgPath == null) {
		System.out.println("Program Path not specified use the default");
	}
	//	String DisplayPort = args[0];
	//	String ICAPort = args[1];
	//	String DesktopID = args[2];
	//	String Host = args[3];
	//	String TempURL = args[4];
	//	String ProgPath = args[5];
	new CollaborationManager(DisplayPort, ICAPort, DesktopID, Host, TempURL, ProgPath);
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/00 9:28:50 PM)
 */
public void newMeetingRequest(String newMeet) {
	//System.out.println("BASE MEETING  " + meetingBaseURL);
	//System.out.println(meetingBaseURL + "/" + CREATE + "MEETING=" + newMeet);
	//String temp = meetingBaseURL + "/" + CREATE;
	//System.out.println("temp in newMeetingRequest  " + temp);
	System.out.println("//////////////////////////////////////");
	System.out.println("Start New Meeting XMX (Daves API) ");
	System.out.println("Refresh ");
	System.out.println("//////////////////////////////////////");
	try {
		byte b[] = null;
		URL url = new URL(tempURL);
		HttpMessage msg = new HttpMessage(url);
		Properties props = new Properties();
		props.put("newmeeting", newMeet);
		InputStream in = msg.sendGetMessage(props);
		System.out.println("IN      " + in.toString());
		int aa = in.read(b);
		System.out.println("aa      " + aa);
	} catch (IOException e) {
	};
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/00 1:32:52 AM)
 */
void newMethod() {
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 9:14:49 PM)
 */
public void run() {
	while (true) {
		//System.out.println("IN RUN");
		InputStream in = doPoll();
		// DYY: if-else block added for testing
		// should just call getINI!
		// if ( in == null ) {
		//	try {
		//		Thread.sleep(500);
		//	} catch ( Exception e ) {
		//		e.printStackTrace();
		//	}
		// } else {
                
                getINI(in);
                
               /* JMC 11/10/00 - Go back to true polling! Doing the wait
                                 in the servlet was just too costly resource
                                 wise (looks like 50 concurrent servlets was
                                 the max), so just check for new stuff 
                                 every so often
                                 
                                 Also, we were not correctly cleaning up 
                                 the servlet when the Meeting Manager went
                                 down. Just punt.
               */
                
               	try {
                   Thread.sleep(5000);
                } catch ( Exception e ) {
                   e.printStackTrace();
                }
                
                
                
		// }
	}
}
/**
 * Send an Invitation request to the Servlet
 * Creation date: (8/24/00 10:53:46 PM)
 */
public void sendInvitation() {
        
   try {
      String s = AppProp.getString("edodc.nonIBMPrefix");
      if (s == null) {
         sendInvitationI();
      } else if (inviteeName.indexOf(s) == 0) {
         new ConfirmSendDialog(new Frame(), this);
      } else {
         sendInvitationI();
      }
   } catch(Throwable t) {
      System.out.println("Error accessing nonIBMPrefix, just complete invite");
      sendInvitationI();
   }
}

/**
 * Send an Invitation request to the Servlet
 * Creation date: (8/24/00 10:53:46 PM)
 */
public void sendInvitationI() {
        
   
   System.out.println("invitation request to servlet");
   
  // JMC 11/15/00 - Moved here from actionPerformed
   eventTextArea.append("Invitation Sent TO: " + inviteeName + "\n");
   
   try {
      URL url = new URL(tempURL + common.SEND_INVITATION_CMD);
      HttpMessage msg = new HttpMessage(url);
      Properties props = new Properties();
      props.put(common.DESKTOP_ID, DesktopID); // DesktopID
//		props.put(common.NAME, meetingTitle); // Meeting Title
      props.put(common.NAME, whoIam); // Meeting Title
      props.put(common.INVITEE, inviteeName); // Person being Invited
      msg.sendGetMessage(props);
     //msg.sendGetMess(props);
      System.out.println("URL" + url);
   } catch (MalformedURLException e) {
      System.out.println("MalformedURLException thrown");
   } catch (IOException e) {
      e.printStackTrace();
   }
  //set title
}

/**
 * Insert the method's description here.
 * Creation date: (8/18/00 8:09:36 PM)
 */
public void setInvitationLabel() {
	boolean a = true;
	invitationLabel.setVisible(a);
	invitationLabel.setForeground(Color.red);
	Font f = new Font("Times New Roman", Font.BOLD, 14);
	invitationLabel.setFont(f);
}
/**
 * Insert the method's description here.
 * Creation date: (08/29/00 10:23:19)
 * @param event java.awt.event.TextEvent
 */
public void textValueChanged(TextEvent e) {
   if (/* e.getSource()==titleTextField ||*/ e.getSource()==inviteeTextField ) {
      if (/* titleTextField.getText().length()>0 && */inviteeTextField.getText().length()>0 ) {
         sendButton.setEnabled(true);
      }
   }
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/00 7:34:23 PM)
 * @param text java.lang.String
 */
public void updateStatusField(String text) {
	statusField.setText(text);
}
}
