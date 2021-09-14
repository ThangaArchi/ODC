package oem.edge.ed.odc.meeting.client;

import oem.edge.ed.odc.dsmp.common.GroupInfo;
import oem.edge.ed.odc.dsmp.common.DSMPBaseProto;
import java.lang.reflect.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import oem.edge.ed.odc.dsmp.client.*;
import oem.edge.ed.odc.meeting.common.*;
import oem.edge.ed.odc.dsmp.client.BuddyMgr;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
/**
 * Insert the type's description here.
 * Creation date: (2/28/2003 2:48:41 PM)
 * @author: Mike Zarnick
 */
public class MeetingViewer extends JFrame implements Runnable, DocumentListener, MenuChangeListener, GroupListener, MeetingListener, MessageListener {
	private MeetingViewerState mvState = new MeetingViewerState();
	private String screen = ":0";
	private String serverName = null;
	private int serverPort = 0;
	private int meetingID = -1;
	private String meetingUser = null;
	private String meetingPW = null;
	private String meetingName = null;
	private boolean autoShare = false;
	private boolean titleSet = false;
	private DSMPDispatcher dispatcher = null;
	private BScraper bscraper = null;
	private Vector projects = null;
	private Vector groups = new Vector();
	private Vector users = null;
	private Vector invites = null;
	private Vector wm = null;
	private Vector callQueue = new Vector();
	private int debugPwdCnt = 0;
	private char[] debugPwd = new char[5];
	private boolean isWin = false;
	private boolean isTunnel = false;
	private boolean scanScreen;
	private boolean leaving = false;
	private boolean isAdjusting = false;
	private boolean qna = false;
	private boolean incontrol = true;
	private Thread timer = null;
	private boolean timerPopped = false;
	private Vector passKeys = new Vector();
	private JTextComponent editTarget;
	private boolean canPaste = false;
	private boolean imageRealized = false;
	private BuddyMgr buddyMgr = new BuddyMgr();
	public static Frame initDlg;  //  @jve:visual-info  decl-index=0 visual-constraint="488,30"
	public static Label initLbl;
	private JMenuItem exitMI = null;
	private JMenu fileM = null;  // @jve:visual-info  decl-index=0 visual-constraint="825,828"
	private JMenuItem loginMI = null;
	private JMenuItem logoutMI = null;
	private JMenuBar meetingViewer2JMenuBar = null;
	private JMenuItem saveAsMI = null;
	private JMenuItem tunnelMI = null;
	private JMenuItem attendMI = null;
	private JMenuItem clearMI = null;
	private JCheckBoxMenuItem compressMI = null;
	private JMenuItem copyMI = null;
	private JMenuItem cutMI = null;
	private JMenu debugM = null;  // @jve:visual-info  decl-index=0 visual-constraint="814,34"
	private JCheckBoxMenuItem debugMI = null;
	private JMenuItem desktopMI = null;
	private JMenu editM = null;  // @jve:visual-info  decl-index=0 visual-constraint="830,693"
	private JMenuItem endMI = null;
	private JCheckBoxMenuItem fitImageMI = null;
	private JCheckBoxMenuItem gridMI = null;
	private JMenuItem inviteMI = null;
	private JSeparator viewSep = null;
	private JMenuItem leaveMI = null;
	private JMenu meetingM = null;  // @jve:visual-info  decl-index=0 visual-constraint="811,394"
	private JMenuItem pasteMI = null;
	private JMenuItem resumeMI = null;
	private JMenu shareM = null;  // @jve:visual-info  decl-index=0 visual-constraint="811,195"
	private JMenuItem startMI = null;
	private JMenuItem stopMI = null;
	private JCheckBoxMenuItem timeMI = null;
	private JMenu viewM = null;  // @jve:visual-info  decl-index=0 visual-constraint="818,1027"
	private JMenuItem windowMI = null;
	private JButton attendCanBtn = null;
	private JList attendLB = null;
	private JButton attendOkBtn = null;
	private JScrollPane attendSP = null;
	private JButton inviteCanBtn = null;
	private JButton inviteOkBtn = null;
	private JButton loginCanBtn = null;
	private JButton loginOkBtn = null;
	private JButton startCanBtn = null;
	private JButton startOkBtn = null;
	private JTextField startTitleTF = null;
	private JButton transferCanBtn = null;
	private JList transferLB = null;
	private JButton transferOkBtn = null;
	private JScrollPane transferSP = null;
	private JButton windowCanBtn = null;
	private JList windowLB = null;
	private JButton windowOkBtn = null;
	private JButton windowPickBtn = null;
	private JScrollPane windowSP = null;
	private JPanel attendBtnPnl = null;
	private JLabel attendLbl = null;
	private JPanel inviteBtnPnl = null;
	private JButton inviteUserDelBtn = null;
	private JTextField inviteUserTF = null;
	private JPanel loginPnl = null;
	private JLabel loginPwdLbl = null;
	private JPasswordField loginPwdTF = null;
	private JLabel loginUserLbl = null;
	private JTextField loginUserTF = null;
	private JLabel screenLbl = null;
	private JPanel screenPnl = null;
	private JTextField screenTF = null;
	private JRadioButton secConfCB = null;
	private JRadioButton secOtherCB = null;
	private JTextField secOtherTF = null;
	private JRadioButton secUnclassCB = null;
	private JLabel securityLbl = null;
	private JPanel securityPnl = null;
	private JPanel startBtnPnl = null;
	private JLabel startTitleLbl = null;
	private JPanel startTitlePnl = null;
	private JPanel transferBtnPnl = null;
	private JLabel transferLbl = null;
	private JPanel windowBtnPnl = null;
	private JLabel windowLbl = null;
	private JPanel msgPnl = null;
	private JButton msgSendBtn = null;
	private JScrollPane msgSP = null;
	private JTextArea msgTA = null;
	private JTextField msgTF = null;
	private JButton tbAttendBtn = null;
	private JButton tbCopyBtn = null;
	private JButton tbCutBtn = null;
	private JButton tbEndBtn = null;
	private JButton tbExitBtn = null;
	private JButton tbInviteBtn = null;
	private JButton tbLeaveBtn = null;
	private JButton tbLogoffBtn = null;
	private JButton tbLogonBtn = null;
	private JButton tbPasteBtn = null;
	private JButton tbResumeBtn = null;
	private JButton tbSaveBtn = null;
	private JButton tbShDskBtn = null;
	private JButton tbShWinBtn = null;
	private JButton tbStartBtn = null;
	private JButton tbStopBtn = null;
	private ImagePanel imagePnl = null;
	private JScrollPane imageSP = null;
	private PresencePanel presencePnl = null;
	private JPanel startCP = null;
	private JSeparator logSep = null;
	private DefaultListModel attendLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="299,2351"
	private JButton questionNoBtn = null;
	private JScrollPane questionSP = null;
	private JTextArea questionTA = null;
	private JButton questionYesBtn = null;
	private DefaultListModel transferLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="313,1150"
	private DefaultListModel windowLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="407,1418"
	private JFileChooser fileChooser = null;  //  @jve:visual-info  decl-index=0 visual-constraint="449,2070"
	private JButton debugCanBtn = null;
	private JButton debugOkBtn = null;
	private JTextField debugTF = null;
	private JLabel debugLbl = null;
	private JPanel debugPnl = null;
	private JCheckBoxMenuItem sendTimeMI = null;
	private JMenu keyM = null;
	private JRadioButtonMenuItem keyNormalMI = null;
	private JRadioButtonMenuItem keyRefineMI = null;
	private JRadioButtonMenuItem keyStrictMI = null;
	private JDialog debugDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="38,2531"
	private JPanel debugCP = null;
	private JDialog attendDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="35,2265"
	private JPanel attendCP = null;
	private JDialog inviteDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="39,2684"
	private JPanel inviteCP = null;
	private JDialog loginDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="276,2366"
	private JPanel loginCP = null;
	private JDialog questionDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="29,1891"
	private JPanel questionCP = null;
	private JDialog startDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="28,1589"
	private JDialog transferDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="23,1055"
	private JPanel transferCP = null;
	private JDialog windowDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="26,1320"
	private JPanel windowCP = null;
	private JPanel meetingViewerCP = null;
	private JPanel imageCP = null;
	private JFrame imageFrame = null;  //  @jve:visual-info  decl-index=0 visual-constraint="21,747"
	private JMenuBar imageFrameJMenuBar = null;
	private JMenuItem groupMI = null;
	private ManageGroups manageGroups = null;  //  @jve:visual-info  decl-index=0 visual-constraint="449,1541"
	private JButton inviteGroupQryBtn = null;
	private JList inviteLB = null;
	private DefaultListModel inviteLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="387,2855"
	private JScrollPane inviteSP = null;
	private JLabel inviteLbl1 = null;
	private JLabel inviteLbl2 = null;
	private JLabel inviteLbl3 = null;
	private JLabel inviteLbl4 = null;
	private JLabel inviteLbl5 = null;
	private JPanel invitePnl1 = null;
	private JPanel invitePnl2 = null;
	private JLabel grpQueryCompanyLbl = null;
	private JPanel grpQueryCP = null;
	private JDialog grpQueryDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="41,3062"
	private JList grpQueryLB = null;
	private JLabel grpQueryLbl1 = null;
	private JLabel grpQueryLbl2 = null;
	private JLabel grpQueryLbl3 = null;
	private DefaultListModel grpQueryLM = null;  //  @jve:visual-info  decl-index=0 visual-constraint="444,3237"
	private JLabel grpQueryNameLbl = null;
	private JButton grpQueryOkBtn = null;
	private JLabel grpQueryOwnerLbl = null;
	private JScrollPane grpQuerySP = null;
	private JButton inviteUserOkBtn = null;
	private JSeparator editSep = null;
	private JSeparator groupSep = null;
	private JSeparator meetingSep = null;
	private JSeparator passSep = null;
	private JSeparator saveSep = null;
	private JSeparator shareSep = null;
	private JMenuItem freezeMI = null;
	private JMenuItem passMI = null;
	private JMenuItem takeMI = null;
	private JButton tbFreezeBtn = null;
	private JButton tbPassBtn = null;
	private JButton tbTakeBtn = null;
	private JLabel connectedLbl = null;
	private JLabel meetingLbl = null;
	private JMenuItem viewEndMI = null;
	private JMenuItem viewLeaveMI = null;
	private JCheckBoxMenuItem resizeImageMI = null;
	private JPanel toolbarPnl = null;
	private JSplitPane meetingSP = null;
	private javax.swing.JDialog urlDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="41,3440"
	private javax.swing.JPanel urlCP = null;
	private javax.swing.JCheckBox urlPasswordCB = null;
	private javax.swing.JCheckBox urlMeetingCB = null;
	private javax.swing.JCheckBox urlSendCB = null;
	private javax.swing.JLabel urlPasswordLbl = null;
	private javax.swing.JLabel urlMeetingLbl = null;
	private javax.swing.JPanel urlBtnPnl = null;
	private javax.swing.JButton urlOkBtn = null;
	private javax.swing.JButton urlCanBtn = null;
	private javax.swing.JMenuItem urlMI = null;
	private javax.swing.JDialog showUrlDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="421,3175"
	private javax.swing.JPanel showUrlCP = null;
	private javax.swing.JButton showUrlBtn = null;
	private javax.swing.JScrollPane showUrlSP = null;
	private javax.swing.JTextArea showUrlTA = null;
	private javax.swing.JLabel showUrlLbl = null;
	private javax.swing.JDialog callDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="425,3439"
	private javax.swing.JPanel callCP = null;
	private javax.swing.JLabel callLbl1 = null;
	private javax.swing.JComboBox callUserCB = null;
	private javax.swing.JLabel callLbl2 = null;
	private javax.swing.JPasswordField callPassTF = null;
	private javax.swing.JPanel callBtnPnl = null;
	private javax.swing.JButton callOkBtn = null;
	private javax.swing.JButton callCanBtn = null;
	private javax.swing.JMenuItem callMI = null;
	private javax.swing.JDialog callingDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="59,3681"
	private javax.swing.JPanel callingCP = null;
	private javax.swing.JLabel callingLbl = null;
	private javax.swing.JButton callingCanBtn = null;
	private javax.swing.JPanel startPwdPnl = null;
	private javax.swing.JLabel startPwdLbl = null;
	private javax.swing.JCheckBox startCallCB = null;
	private javax.swing.JTextField startPwdTF = null;
	private javax.swing.JDialog settingsDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="375,2685"
	private javax.swing.JPanel settingsCP = null;
	private javax.swing.JLabel settingsPwdLbl = null;
	private javax.swing.JTextField settingsPwdTF = null;
	private javax.swing.JCheckBox settingsCallCB = null;
	private javax.swing.JPanel settingsBtnPnl = null;
	private javax.swing.JButton settingsOkBtn = null;
	private javax.swing.JButton settingsCanBtn = null;
	private javax.swing.JMenuItem settingsMI = null;  //  @jve:visual-info  decl-index=0 visual-constraint="487,233"
	private javax.swing.JDialog callerDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="382,3678"
	private javax.swing.JPanel callerCP = null;
	private javax.swing.JLabel callerLbl1 = null;
	private javax.swing.JLabel callerUserLbl = null;
	private javax.swing.JLabel callerLbl2 = null;
	private javax.swing.JLabel callerCompanyLbl = null;
	private javax.swing.JPanel callerBtnPnl = null;
	private javax.swing.JButton callerOkBtn = null;
	private javax.swing.JButton callerIgnoreBtn = null;
	private javax.swing.JButton callerDisableBtn = null;
	private javax.swing.JLabel callerQueueLbl = null;
	private javax.swing.JLabel transferDurationLbl = null;
	private javax.swing.JComboBox transferDurationCB = null;
	private javax.swing.JPanel transferDurationPnl = null;
	private javax.swing.JPanel transferDurPnl = null;
	private javax.swing.JLabel transferDurLbl = null;
	private javax.swing.JComboBox transferDurCB = null;
	private javax.swing.JDialog urlWarnDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="300,1056"
	private javax.swing.JPanel urlWarnCP = null;
	private javax.swing.JTextArea urlWarnTA = null;
	private javax.swing.JPanel urlWarnBtnPnl = null;
	private javax.swing.JButton urlWarnOkBtn = null;
	private javax.swing.JButton urlWarnEnableBtn = null;
	private javax.swing.JButton urlWarnCanBtn = null;
	private javax.swing.JMenuItem createIconMI = null;
/**
 * MeetingViewer2 constructor comment.
 */
public MeetingViewer() {
	super();
	initialize();
}
/**
 * MeetingViewer2 constructor comment.
 * @param title java.lang.String
 */
public MeetingViewer(String title) {
	super(title);
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:21:48 AM)
 * @param name java.lang.String
 * @param lm javax.swing.ListModel
 */
public void addNameToList(Buddy buddy, DefaultListModel lm) {
	int i = 0;
	int j = 0;
	Enumeration e = lm.elements();

	// Step through the list to find the place to add the name.
	while (e.hasMoreElements()) {
		Buddy lBuddy = (Buddy) e.nextElement();
		if (lBuddy.type != buddy.type) {
			j = (lBuddy.type < buddy.type) ? - 1: 1;
		}
		else {
			j = lBuddy.name.compareTo(buddy.name);
		}

		// Current name bigger than new name, insert here.
		if (j > 0) {
			lm.insertElementAt(buddy,i);
			return;
		}
		// Name already in list, check type it.
		else if (j == 0) {
			// Current type is bigger than new type, insert here.
			if (lBuddy.type > buddy.type) {
				lm.insertElementAt(buddy,i);
				return;
			}
			// Name of this type already in list, ignore it.
			else if (lBuddy.type == buddy.type) {
				return;
			}
			// Maybe the next spot.
			else {
				i++;
			}
		}
		// Maybe the next spot.
		else {
			i++;
		}
	}
	// New name goes at end of list.
	lm.addElement(buddy);
}
/**
 * Comment
 */
public void adjustEditMenu(CaretEvent e) {
	// Caret adjusted in one of the text areas. The text area will
	// become the target of the edit menu items.
	editTarget = (JTextComponent) e.getSource();

	// Adjust menu items according to text selection. Paste is managed
	// by the cut/copy methods and by window activation. Paste on the
	// Send pop-up is used as the reference.
	boolean enabled = e.getDot() != e.getMark();

	if (editTarget == getMsgTA()) {
		getCutMI().setEnabled(false);
		getCopyMI().setEnabled(enabled);
		getPasteMI().setEnabled(false);
		getTbCutBtn().setEnabled(false);
		getTbCopyBtn().setEnabled(enabled);
		getTbPasteBtn().setEnabled(false);
	}
	else if (editTarget == getMsgTF()) {
		getCutMI().setEnabled(enabled);
		getCopyMI().setEnabled(enabled);
		getPasteMI().setEnabled(canPaste);
		getTbCutBtn().setEnabled(enabled);
		getTbCopyBtn().setEnabled(enabled);
		getTbPasteBtn().setEnabled(canPaste);
	}
}
/**
 * Comment
 */
public void adjustPaste() {
	boolean canPaste = false;

	try {
		canPaste = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this) != null;
	}
	catch (Exception e) {
		// Couldn't get to the clipboard... oh well.
	}

	if (editTarget == getMsgTF()) {
		getPasteMI().setEnabled(canPaste);
		getTbPasteBtn().setEnabled(canPaste);
	}
}
/**
 * Comment
 */
public void answerYes() {
	qna = true;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 8:40:36 PM)
 */
public boolean askQuestion(String title, String question) {
	getQuestionDlg().setTitle(title);
	getQuestionTA().setText(question);
	getTransferDurationPnl().setVisible(false);
	getQuestionYesBtn().setText("Force");
	getQuestionNoBtn().setText("Cancel");
	getQuestionDlg().validate();
	qna = false;
	try {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				getQuestionDlg().setLocationRelativeTo(MeetingViewer.this);
				getQuestionDlg().setVisible(true);
			}
		});
	}
	catch (Exception e) {
	}
	return qna;
}
/**
 * Comment
 */
public void attendListChg() {
	getAttendOkBtn().setEnabled(getAttendLB().getSelectedIndex() != -1);
}
/**
 * Comment
 */
public void attendMeeting() {
	// Can't start or attend a meeting now.
	getAttendMI().setEnabled(false);
	getCallMI().setEnabled(false);
	getStartMI().setEnabled(false);
	getTbAttendBtn().setEnabled(false);
	getTbStartBtn().setEnabled(false);

	// Dispose the attend dialog.
	getAttendDlg().setVisible(false);

	getImagePnl().setDispatcher(dispatcher);
	getPresencePnl().setDispatcher(dispatcher);
	getPresencePnl().setEnablement(2);
	dispatcher.addMessageListener(this);

	int i = getAttendLB().getSelectedIndex();
	DSMPMeeting m = (DSMPMeeting) invites.elementAt(i);
	dispatcher.meetingID = m.getMeetingId();
	dispatcher.inviteID = m.getInviteId();
	dispatcher.dispatchProtocol(DSMPGenerator.joinMeeting((byte) 0,dispatcher.meetingID));

	getMeetingLbl().setText(m.getTitle() + " - may include ["  + m.getClassification() + "] information");
	getImageFrame().setTitle(m.getTitle() + " - " + dispatcher.user + " - may include ["  + m.getClassification() + "] information");
}

/**
 * Comment
 */
public void call() {
	if (! getCallOkBtn().isEnabled()) {
		return;
	}

	// Can't start or attend a meeting now.
	getAttendMI().setEnabled(false);
	getCallMI().setEnabled(false);
	getStartMI().setEnabled(false);
	getTbAttendBtn().setEnabled(false);
	getTbStartBtn().setEnabled(false);

	// Dispose the call dialog.
	getCallDlg().setVisible(false);

	dispatcher.addMessageListener(this);

	String name = (String) getCallUserCB().getSelectedItem();
	char[] pwd = getCallPassTF().getPassword();

	String password = null;
	if (pwd != null && pwd.length > 0) password = new String(pwd);

	DSMPProto p = DSMPGenerator.placeCall((byte) 0, name, password);
	dispatcher.dispatchProtocol(p);

	synchronized(mvState) {
		mvState.state = MeetingViewerState.CALL_WINDOW;
	}

	getCallingDlg().setLocationRelativeTo(MeetingViewer.this);
	getCallingDlg().show();
}

/**
 * Comment
 */
public void autoCall() {
	// Can't auto call at startup?
	if (meetingID < 0 && meetingUser == null) {
		return;
	}

	// Can't start or attend a meeting now.
	getAttendMI().setEnabled(false);
	getCallMI().setEnabled(false);
	getStartMI().setEnabled(false);
	getTbAttendBtn().setEnabled(false);
	getTbStartBtn().setEnabled(false);

	dispatcher.addMessageListener(this);

	DSMPProto p;
	
	if (meetingID < 0) {
		p = DSMPGenerator.placeCall((byte) 0, meetingUser, meetingPW);
	}
	else {
		p = DSMPGenerator.placeCall((byte) 0, meetingID, meetingPW);
	}

	dispatcher.dispatchProtocol(p);

	synchronized(mvState) {
		mvState.state = MeetingViewerState.CALL_WINDOW;
	}

	SwingUtilities.invokeLater(new Runnable() {
		public void run() {
			getCallingDlg().setLocationRelativeTo(MeetingViewer.this);
			getCallingDlg().show();
		}
	});
}

/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(String[] args) {
	String user = null;
	String pw = null;
	boolean maximize = false;
	boolean iconize = false;

	// Determine our platform.
	isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);

	String token = null;

	getMeetingViewer2JMenuBar().remove(getDebugM());
	if (isWin) {
		getStartCP().remove(getScreenPnl());
	}

	String line = null;
	BufferedReader rdr = null;
	int i = 0;

	try {
		if (args.length == 0) {
			rdr = new BufferedReader(new InputStreamReader(System.in));
			line = rdr.readLine();
		}
		else {
			line = args[i++];
		}
	
		while (line != null) {
			line = line.trim();
			String LINE = line.toUpperCase();
			if (LINE.startsWith("-TOKEN")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 6) arg = line.substring(6).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-TOKEN requires an value.");
					return;
				}
				else {
					token = arg;
				}
			} else if (LINE.startsWith("-USER")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 5) arg = line.substring(5).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-USER requires an value.");
					return;
				}
				else {
					user = arg;
				}
			} else if (LINE.startsWith("-PW")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 3) arg = line.substring(3).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-PW requires an value.");
					return;
				}
				else {
					pw = arg;
				}
			} else if (LINE.startsWith("-MEETINGID")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 10) arg = line.substring(10).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}

				if (arg == null || arg.length() == 0) {
					syntax("-MEETINGID requires an value.");
					return;
				}
				else {
					try {
						meetingID = Integer.parseInt(arg);
					}
					catch (NumberFormatException e) {
						syntax("Meeting ID is not a valid integer.");
						return;
					}
				}
			} else if (LINE.startsWith("-MEETINGUSER")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 12) arg = line.substring(12).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-MEETINGUSER requires an value.");
					return;
				}
				else {
					meetingUser = arg;
				}
			} else if (LINE.startsWith("-MEETINGPW")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 10) arg = line.substring(10).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-MEETINGPW requires an value.");
					return;
				}
				else {
					meetingPW = arg;
				}
			} else if (LINE.startsWith("-MEETING")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 8) arg = line.substring(8).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-MEETING requires an value.");
					return;
				}
				else {
					meetingName = arg;
				}
			} else if (LINE.startsWith("-HOST")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 5) arg = line.substring(5).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-HOST requires an value.");
					return;
				}
				else {
					serverName = arg;
				}
			} else if (LINE.startsWith("-PORT")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 5) arg = line.substring(5).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-PORT requires an value.");
					return;
				}
				else {
					try {
						serverPort = Integer.parseInt(arg);
					}
					catch (NumberFormatException e) {
						syntax("Port is not a valid integer.");
						return;
					}
				}
			} else if (LINE.startsWith("-DISPLAY")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 8) arg = line.substring(8).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-DISPLAY requires an value.");
					return;
				}
				else {
					screen = arg;
				}
			} else if (line.equalsIgnoreCase("-maximize")) {
				maximize = true;
			} else if (line.equalsIgnoreCase("-iconize")) {
				iconize = true;
			} else if (line.equalsIgnoreCase("-limitchat")) {
				getPresencePnl().setLimitChatter(true);
			} else if (line.equalsIgnoreCase("-autoshare")) {
				autoShare = true;
			} else if (line.equalsIgnoreCase("-debug")) {
				getMeetingViewer2JMenuBar().add(getDebugM());
			} else if (LINE.startsWith("-title")) {
				String arg = null;
				if (args.length == 0) {
					if (line.length() > 6) arg = line.substring(6).trim();
				}
				else if (i < args.length) {
						arg = args[i++];
				}
	
				if (arg == null || arg.length() == 0) {
					syntax("-title requires an value.");
					return;
				}
				else {
					setTitle(arg);
					titleSet = true;
				}
			} else if (line.equalsIgnoreCase("-the_end")) {
				// caboose, ignore it...
			} else {
				syntax("Bad parm = '" + line + "'");
				return;
			}
	
			if (line.equalsIgnoreCase("-THE_END")) {
				line = null;
			}
			else if (args.length == 0) {
				line = rdr.readLine();
			}
			else if (i < args.length) {
				line = args[i++];
			}
			else {
				line = null;
			}
		}
	}
	catch (Exception e) {
		System.out.println("Warning: error occurred while reading parameters!");
		System.out.println(e.getMessage());
	}

	if (serverName == null || serverName.length() == 0 || serverPort < 0) {
		syntax("Server address and port is required!");
		return;
	}

	if (token != null) {
		isTunnel = true;
		getFileM().remove(getLoginMI());
		getFileM().remove(getLogoutMI());
		getFileM().remove(getLogSep());
		getTbLogonBtn().setVisible(false);
		getTbLogoffBtn().setVisible(false);
		GridBagLayout lm = (GridBagLayout) getToolbarPnl().getLayout();
		GridBagConstraints c = lm.getConstraints(getTbSaveBtn());
		c.insets.left = 20;
		lm.setConstraints(getTbSaveBtn(),c);
		getToolbarPnl().doLayout();
	}
	else {
		getFileM().remove(getTunnelMI());
		getFileM().validate();
	}

	resetToLoggedOut();

	/** Connect to DSMPAllocationServer to check the Meeting Server Availability.
	  * Get the Meeting Server PORT to connect.
	  *
	  * Added By: Jyoti S. Panda
	  * Start code.....
	  *

	Socket sock2allocator = null;
	try{
		sock2allocator = new Socket(serverName,serverPort);
		sock2allocator.setSoTimeout(30000); // Time out in 30 Sec.

		DataInputStream din = new DataInputStream(sock2allocator.getInputStream());
		serverPort = din.readInt();
		if (serverPort > 65535)	{
			System.exit(1);
		}
	}
	catch (SocketTimeoutException toe) {
			System.out.println("MeetingViewer: TimeOut Reached \n");
			System.exit(1);
	}
	catch (NumberFormatException nfe) {
			// MeetingViewer: PORT is not a valid integer.
			System.exit(1);
	}
	catch (IOException ioe) {
		  System.exit(1);
	}
	catch (Exception e) {
		  System.exit(1);
	}
	try {sock2allocator.close();} catch(Throwable tt) {}

	/* end of code By: Jyoti S. Panda
	*/

	initLbl.setText("Running...");
	if (initDlg != null)
		initDlg.setVisible(false);

	setSize(330,650);
	cornerWindow(this);
	setDefaultSplit();

	// Hide the tunnel (use standard out to communicate w/ tunnel).
	if (isTunnel) {
		System.out.println("tunnel command: hide");
	}

	if (maximize || iconize) {
		try {
			Toolkit tk = Toolkit.getDefaultToolkit();
			Class TOOLKIT = tk.getClass();
			Class FRAME = getClass();
			Class[] PARMS = { Integer.TYPE };
			Method setExtendedState = FRAME.getMethod("setExtendedState",PARMS);
			Method isFrameStateSupported = TOOLKIT.getMethod("isFrameStateSupported",PARMS);
			Field MAXBOTH = FRAME.getField("MAXIMIZED_BOTH");
			Field ICONIFIED = FRAME.getField("ICONIFIED");
			Object[] parms = new Object[1];
			if (maximize) {
				try {
					boolean supported = false;
					parms[0] = MAXBOTH.get(this);
					Object rv = isFrameStateSupported.invoke(tk,parms);
					if (rv != null) {
						supported = ((Boolean) rv).booleanValue();
					}
					if (supported) {
						setExtendedState.invoke(this,parms);
					}
					else {
						System.out.println("JVM does not support maximize of window.");
					}
				}
				catch (Exception e1) {
					System.out.println("Could not maximize window.");
				}
			}
			if (iconize) {
				try {
					boolean supported = false;
					parms[0] = ICONIFIED.get(this);
					setExtendedState.invoke(this,parms);
					Object rv = isFrameStateSupported.invoke(tk,parms);
					if (rv != null) {
						supported = ((Boolean) rv).booleanValue();
					}
					if (supported) {
						setExtendedState.invoke(this,parms);
					}
					else {
						System.out.println("JVM does not support iconify of window.");
					}
				}
				catch (Exception e1) {
					System.out.println("Could not iconify window.");
				}
			}
		}
		catch (Exception e) {
			System.out.println("JVM does not support maximizing or iconifying of window.");
		}
	}

	
	if (token != null) {
		try {
			dispatcher = new DSMPDispatcher(serverName,serverPort);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to connect to server at port provided.");
			SwingUtilities.invokeLater(new ErrorRunner(this,"Bad server/port provided.","No connection"));
			return;
		}

		dispatcher.addMeetingListener(this);
		dispatcher.addGroupListener(this);

		DSMPProto p = DSMPGenerator.loginToken((byte) 0,token);
		dispatcher.dispatchProtocol(p);
	}
	else if (user != null && pw != null) {
		try {
			dispatcher = new DSMPDispatcher(serverName,serverPort);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to connect to server at port provided.");
			SwingUtilities.invokeLater(new ErrorRunner(this,"Bad server/port provided.","No connection"));
			return;
		}

		dispatcher.addMeetingListener(this);
		dispatcher.addGroupListener(this);

		getLoginMI().setEnabled(false);
		getTbLogonBtn().setEnabled(false);

		DSMPProto p = DSMPGenerator.loginUserPW((byte) 0,user,pw);
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public void begin(String token, Socket s) {
	// Determine our platform.
	isWin = (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1);
	getDebugM().setVisible(false);

	isTunnel = true;
	getFileM().remove(getLoginMI());
	getFileM().remove(getLogoutMI());
	getFileM().remove(getLogSep());
	getTunnelMI().setEnabled(true);
	getTbLogonBtn().setVisible(false);
	getTbLogoffBtn().setVisible(false);
	GridBagLayout lm = (GridBagLayout) getToolbarPnl().getLayout();
	GridBagConstraints c = lm.getConstraints(getTbSaveBtn());
	c.insets.left = 20;
	lm.setConstraints(getTbSaveBtn(),c);
	getToolbarPnl().doLayout();

	resetToLoggedOut();

	/** Connect to DSMPAllocationServer to check the Meeting Server Availability.
	  * Get the Meeting Server PORT to connect.
	  *
	  * Added By: Jyoti S. Panda
	  * Start code.....
	  *

	Socket sock2allocator = null;
	try{
		sock2allocator = new Socket(serverName,serverPort);
		sock2allocator.setSoTimeout(30000); // Time out in 30 Sec.

		DataInputStream din = new DataInputStream(sock2allocator.getInputStream());
		serverPort = din.readInt();
		if (serverPort > 65535)	{
			System.exit(1);
		}
	}
	catch (SocketTimeoutException toe) {
			System.exit(1);
	}
	catch (NumberFormatException nfe) {
			System.exit(1);
	}
	catch (IOException ioe) {
		  System.exit(1);
	}
	catch (Exception e) {
		  System.exit(1);
	}
	try {sock2allocator.close();} catch(Throwable tt) {}

	/* end of code By: Jyoti S. Panda
	*/

	initLbl.setText("Running...");
	if (initDlg != null)
		initDlg.setVisible(false);

	setSize(330,650);
	cornerWindow(this);
	setDefaultSplit();

	// Hide the tunnel (use standard out to communicate w/ tunnel).
	System.out.println("tunnel command: hide");

	try {
		dispatcher = new DSMPDispatcher(s);
	}
	catch (Exception e) {
		e.printStackTrace();
		System.out.println("Unable to connect to socket provided.");
		SwingUtilities.invokeLater(new ErrorRunner(this,"Bad socket provided.","No connection"));
		return;
	}

	dispatcher.addMeetingListener(this);
	dispatcher.addGroupListener(this);

	DSMPProto p = DSMPGenerator.loginToken((byte) 0,token);
	dispatcher.dispatchProtocol(p);
}
/**
 * Comment
 */
public static void centerWindow(Window w) {
	// Center the window on the screen.
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = w.getSize();

	int x = (scrSize.width - winSize.width) / 2;
	int y = (scrSize.height - winSize.height) / 2;

	int dx = x + winSize.width - scrSize.width;
	if (dx > 0)
		x -= dx;

	if (x < 0)
		x = 0;

	int dy = y + winSize.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	w.setLocation(x,y);
	w.setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 4:25:52 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void changedUpdate(DocumentEvent e) {
	if (e.getDocument() == getLoginUserTF().getDocument() ||
		e.getDocument() == getLoginPwdTF().getDocument()) {
		textChgLogin();
	}
	else if (e.getDocument() == getStartTitleTF().getDocument() ||
			e.getDocument() == getSecOtherTF().getDocument() ||
			e.getDocument() == getScreenTF().getDocument()) {
		textChgStart();
	}
	else if (e.getDocument() == getInviteUserTF().getDocument()) {
		textChgUserInvite();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 8:40:36 PM)
 */
public boolean confirmTransfer() {
	getQuestionDlg().setTitle("Confirm transfer");
	getQuestionTA().setText("You are transferring the control of your workstation to another person. " +
		"Many company policies require that you supervise the activities " +
		"of this person while they are in control of your workstation. Therefore, the " +
		"system will prompt you periodically to confirm your presence.\n\n" +
		"Press the OK button to complete the transfer of control.");
	getTransferDurationPnl().setVisible(true);
	getQuestionYesBtn().setText("OK");
	getQuestionNoBtn().setText("Cancel");
	getQuestionDlg().validate();
	qna = false;
	try {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				getQuestionDlg().setLocationRelativeTo(MeetingViewer.this);
				getQuestionDlg().setVisible(true);
			}
		});
	}
	catch (Exception e) {
	}
	return qna;
}
/**
 * connEtoC65:  (TimeMI.item.itemStateChanged(java.awt.event.ItemEvent) --> MeetingViewer.doTime()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC65(java.awt.event.ItemEvent arg1) {
	try {
		this.doTime();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public static void cornerWindow(Window w) {
	// Position the window in the upper right corner on the screen.
	Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension winSize = w.getSize();

	int x = scrSize.width - winSize.width - 5;
	int y = 5;

	if (x < 0)
		x = 0;

	int dy = y + winSize.height - scrSize.height;
	if (dy > 0)
		y -= dy;

	if (y < 0)
		y = 0;

	w.setLocation(x,y);
	w.setVisible(true);
}
/**
 * Comment
 */
public void debug() {
	String code = getDebugTF().getText();

	if (code == null || (! code.equals("DUFUS")))
		return;

	getDebugDlg().dispose();
	getMeetingViewer2JMenuBar().add(getDebugM());
	getMeetingViewer2JMenuBar().validate();
}
/**
 * Comment
 */
public void doAttendMtg() {
	dispatcher.dispatchProtocol(DSMPGenerator.getAllMeetings((byte) 0));
}
/**
 * Comment
 */
public void doClear() {
	if (editTarget != null)
		editTarget.setText("");
}
/**
 * Comment
 */
public void doCompress() {
	getImagePnl().setCompress(getCompressMI().getState());
}
/**
 * Comment
 */
public void doCopy() {
	if (editTarget != null) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(editTarget.getSelectedText());
		c.setContents(s,s);
		canPaste = true;
		getPasteMI().setEnabled(editTarget == getMsgTF() ? true : false);
	}
}
/**
 * Comment
 */
public void doCut() {
	if (editTarget != null) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection s = new StringSelection(editTarget.getSelectedText());
		c.setContents(s,s);
		canPaste = true;
		getPasteMI().setEnabled(editTarget == getMsgTF() ? true : false);
		String text = editTarget.getText();
		text = text.substring(0,editTarget.getSelectionStart()) + text.substring(editTarget.getSelectionEnd());
		editTarget.setText(text);
	}
}
/**
 * Comment
 */
public void doDebug() {
	if (dispatcher != null)
		dispatcher.setDebug(getDebugMI().getState());
}
/**
 * Comment
 */
public void doDebugMenu() {
	if (getMeetingViewer2JMenuBar().getComponentIndex(getDebugM()) != -1) {
		getMeetingViewer2JMenuBar().remove(getDebugM());
	}
	else {
		getDebugDlg().setLocationRelativeTo(this);
		getDebugDlg().setVisible(true);
	}
}
/**
 * Comment
 */
public void doDrawGrid() {
	getImagePnl().setDrawGrid(getGridMI().getState());
}
/**
 * Comment
 */
public void doEndMtg() {
	if (JOptionPane.showConfirmDialog(this,"Are you sure?","End meeting",JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
		return;

	getEndMI().setEnabled(false);
	getViewEndMI().setEnabled(false);
	getInviteMI().setEnabled(false);
	getTbEndBtn().setEnabled(false);
	getTbInviteBtn().setEnabled(false);

	if (getStopMI().isEnabled())
		doStopShare();

	dispatcher.dispatchProtocol(DSMPGenerator.endMeeting((byte) 0,dispatcher.meetingID));
}
/**
 * Comment
 */
public void doFitImage() {
	getImagePnl().setFitImage(getFitImageMI().isSelected());
	getResizeImageMI().setEnabled(! getFitImageMI().isSelected());
}
/**
 * Comment
 */
public void doFreeze() {
	// Reclaim control if we do not have it.
	if (! incontrol) {
		getTbTakeBtn().setEnabled(false);
		getTakeMI().setEnabled(false);
		DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,dispatcher.meetingID,dispatcher.particpantID);
		dispatcher.dispatchProtocol(p);
	}

	// Tell the scraper to take a snapshot and pause.
	getImagePnl().pause();

	// Show the image window.
	getImageFrame().setLocation(5,5);
	getImageFrame().setVisible(true);
	getImageFrame().toFront();

	// Broadcast now in frozen mode.
	DSMPProto p = DSMPGenerator.frozenMode(dispatcher.meetingID,true);
	dispatcher.dispatchProtocol(p);
}
/**
 * Comment
 */
public void doGroups() {
	getManageGroups().doGroups(this);
}
/**
 * Comment
 */
public void doInvite() {
	// Reclaim control if we do not have it.
	if (! incontrol && dispatcher.isModerator) {
		getTbTakeBtn().setEnabled(false);
		getTakeMI().setEnabled(false);
		DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,dispatcher.meetingID,dispatcher.particpantID);
		dispatcher.dispatchProtocol(p);
	}

	// Need to query user's accessible groups first.
	DSMPBaseProto p = DSMPGenerator.queryGroups((byte) 2,false,false,false,null);
	dispatcher.dispatchProtocol(p);
}
/**
 * Comment
 */
public void doInviteShow() {
	// Load the buddy list.
	users = buddyMgr.getBuddyList();

	// Populate the from listbox.
	getInviteLM().clear();
	Enumeration e = users.elements();
	while (e.hasMoreElements()) {
		Buddy buddy = new Buddy((String) e.nextElement(),Buddy.USER);
		addNameToList(buddy,getInviteLM());
	}
	e = groups.elements();
	while (e.hasMoreElements()) {
		Buddy buddy = new Buddy((String) e.nextElement(),Buddy.GROUP);
		addNameToList(buddy,getInviteLM());
	}
	e = projects.elements();
	while (e.hasMoreElements()) {
		Buddy buddy = new Buddy((String) e.nextElement(),Buddy.PROJECT);
		addNameToList(buddy,getInviteLM());
	}

	// Prepare the invite window.
	getInviteUserTF().setText("");
	getInviteLB().clearSelection();
	getInviteUserDelBtn().setEnabled(false);
	getInviteGroupQryBtn().setEnabled(false);
	getInviteUserOkBtn().setEnabled(false);
	getInviteOkBtn().setEnabled(false);

	// Show the invite window.
	getInviteDlg().setLocationRelativeTo(this);
	getInviteDlg().setVisible(true);
}
/**
 * Comment
 */
public void doLeaveMtg() {
	getLeaveMI().setEnabled(false);
	getViewLeaveMI().setEnabled(false);
	getTbLeaveBtn().setEnabled(false);
	leaving = true;

	dispatcher.dispatchProtocol(DSMPGenerator.leaveMeeting((byte) 0,dispatcher.meetingID));
}
/**
 * Comment
 */
public void doLogin() {
	getLoginOkBtn().setEnabled(false);
	getLoginPwdTF().setText("");
	getLoginDlg().setLocationRelativeTo(this);
	getLoginDlg().setVisible(true);
}
/**
 * Comment
 */
public void doLoginFocus() {
	if (getLoginUserTF().getText() != null && getLoginUserTF().getText().length() > 0)
		getLoginPwdTF().requestFocus();
	else
		getLoginUserTF().requestFocus();
}
/**
 * Comment
 */
public void doLogout() {
	getLogoutMI().setEnabled(false);
	getTbLogoffBtn().setEnabled(false);

	dispatcher.dispatchProtocol(DSMPGenerator.logout((byte) 0));
}
/**
 * Comment
 */
public void doPassCtrl() {
	// We are in control?
	if (incontrol) {
		synchronized (mvState) {
			if (mvState.state != MeetingViewerState.READY)
				return;

			mvState.state = MeetingViewerState.CONTROL_WINDOW;

			getTransferLM().removeAllElements();
			passKeys.removeAllElements();

			Enumeration keys = getPresencePnl().getDataModel().getPresentUserKeys();
			while (keys.hasMoreElements()) {
				Integer pid = (Integer) keys.nextElement();
				if (pid.intValue() != dispatcher.particpantID) {
					String u = getPresencePnl().getDataModel().getUserName(pid.intValue());
					passKeys.addElement(pid);
					getTransferLM().addElement(u);
				}
			}

			getTransferOkBtn().setEnabled(false);
			getTransferDlg().setLocationRelativeTo(this);
		}
		getTransferDlg().setVisible(true);
	}

		// Another user is controlling at this time. Take it back.
	else {
		// Remove that control.
		DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,dispatcher.meetingID,dispatcher.particpantID);
		dispatcher.dispatchProtocol(p);

		// Disable the take control menu and button.
		getTbTakeBtn().setEnabled(false);
		getTakeMI().setEnabled(false);
	}
}
/**
 * Comment
 */
public void doPaste() {
	if (editTarget != null) {
		Transferable t = null;
		
		try {
			t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
		}
		catch (Exception e) {
			// Couldn't get to the clipboard... oh well.
		}

		if (t != null) {
			try {
				String text = (String) t.getTransferData(DataFlavor.stringFlavor);
				String text2 = editTarget.getText();
				text2 = text2.substring(0,editTarget.getSelectionStart()) + text + text2.substring(editTarget.getSelectionEnd());
				editTarget.setText(text2);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
/**
 * Comment
 */
public void doQueryLeave() {
	if (JOptionPane.showConfirmDialog(getImageFrame(),"Are you sure?","Leave meeting",JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
		return;

	doLeaveMtg();
}
/**
 * Comment
 */
public void doResizeImage() {
	getImagePnl().setAutoResizeImage(getResizeImageMI().isSelected());
}
/**
 * Comment
 */
public void doResume() {
	getImageFrame().setVisible(false);

	DSMPProto p = DSMPGenerator.frozenMode(dispatcher.meetingID,false);
	dispatcher.dispatchProtocol(p);

	getImagePnl().resume();
}
/**
 * Comment
 */
public void doSave() {
	if (getFileChooser().showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
		File textFile = getFileChooser().getSelectedFile();

		try {
			FileWriter f = new FileWriter(textFile);
			getMsgTA().write(f);
			f.close();
		}
		catch (IOException e) {
			SwingUtilities.invokeLater(new ErrorRunner(this,e.getMessage(),"Unable to save"));
		}
	}
}
/**
 * Comment
 */
public void doSendTime() {
	getImagePnl().setDoSendTime(getSendTimeMI().getState());
}
/**
 * Comment
 */
public void doShareWindow() {
	synchronized (mvState) {
		if (dispatcher.isSharing)
			return;

		if (mvState.state != MeetingViewerState.READY)
			return;

		mvState.state = MeetingViewerState.QUERY_WINDOW;

		getWindowLM().removeAllElements();
		wm = bscraper.getToplevelWindows();

		for (int i = 0; i < wm.size(); i++) {
			Integer id = (Integer) wm.elementAt(i);
			String title = bscraper.getWindowTitle(id.intValue());
			getWindowLM().addElement(title);
		}

		getWindowLB().setSelectedIndex(0);
		getWindowOkBtn().setEnabled(true);

		getWindowDlg().setLocationRelativeTo(this);
	}

	getWindowDlg().setVisible(true);
}
/**
 * Comment
 */
public void doStartMtg() {
	getSecConfCB().setSelected(true);
	getSecUnclassCB().setSelected(false);
	getSecOtherCB().setSelected(false);
	/**
		Below line added by Aswath on 12/22/03 to clear
		the Title text field on Cancel the 'Start meeting'
	*/
	getSecOtherTF().setText("");
	getSecOtherTF().setEnabled(false);
	getStartTitleTF().setText("");
	getStartPwdTF().setText("");
	getScreenTF().setText(screen);
	getStartOkBtn().setEnabled(false);
	getStartDlg().setLocationRelativeTo(this);
	getStartDlg().setVisible(true);
}
/**
 * Comment
 */
public void doStopShare() {
	// Reclaim control if we do not have it.
	/*
	if (! incontrol) {
		getModTbNoTransferBtn().setEnabled(false);
		getModTakeMI().setEnabled(false);
		DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,dispatcher.meetingID,dispatcher.particpantID);
		dispatcher.dispatchProtocol(p);
	}

	// Swap all the menu items and tool buttons.
	getDesktopMI().setEnabled(true);
	getWindowMI().setEnabled(true);
	getStopMI().setEnabled(false);
	getResumeMI().setEnabled(false);
	getShareM().removeAll();
	getShareM().add(getDesktopMI());
	getShareM().add(getWindowMI());
	getTbShDskBtn().setVisible(true);
	getTbShWinBtn().setVisible(true);
	getTbStopBtn().setVisible(false);
	getTbResumeBtn().setVisible(false);
	getToolbarPnl().doLayout();

	// Leave freeze mode, if necessary.
	if (getImagePnl().isPaused()) {
		DSMPProto p = DSMPGenerator.frozenMode(dispatcher.meetingID,false);
		dispatcher.dispatchProtocol(p);
	}

	// Pause the scraper and stop the image panel.
	bscraper.pause();
	getImagePnl().setBScraper(null);

	// Re-display the main window, if necessary.
	if (getModFrame().isVisible()) {
		getModFrame().setVisible(false);
		setVisible(true);
	}
	*/

	// Frozen?
	if (getImagePnl().isPaused()) {
		getImageFrame().setVisible(false);
	}

	// Pause the scraper and stop the image panel.
	bscraper.pause();
	getImagePnl().setBScraper(null);

	DSMPProto p = DSMPGenerator.stopSharing((byte) 0,dispatcher.meetingID);
	dispatcher.dispatchProtocol(p);
}
/**
 * Comment
 */
public void doTime() {
	if (getTimeMI().getState())
		getImagePnl().timeReceive();
	else {
		long t = getImagePnl().getReceiveTime();
		System.out.println("Image updates received and processed in " + t + "ms.");
	}
}
/**
 * Comment
 */
public void exit(boolean isDeath) {
	// Check to see if we are hosting a meeting?
	if (! getEndMI().isEnabled() ||
		JOptionPane.showConfirmDialog(this,"End this meeting and exit?","Confirm exit",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
		if (isTunnel) {
			if (isDeath) {
				System.out.println("tunnel command: show");
			}
			else {
				System.out.println("tunnel command: app ended");
			}
		}
		System.exit(0);
	}
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getAttendBtnPnl() {
	if (attendBtnPnl == null) {
		try {
			attendBtnPnl = new javax.swing.JPanel();
			attendBtnPnl.setName("AttendBtnPnl");
			attendBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAttendOkBtn = new java.awt.GridBagConstraints();
			constraintsAttendOkBtn.gridx = 0; constraintsAttendOkBtn.gridy = 0;
			constraintsAttendOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getAttendBtnPnl().add(getAttendOkBtn(), constraintsAttendOkBtn);

			java.awt.GridBagConstraints constraintsAttendCanBtn = new java.awt.GridBagConstraints();
			constraintsAttendCanBtn.gridx = 1; constraintsAttendCanBtn.gridy = 0;
			constraintsAttendCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getAttendBtnPnl().add(getAttendCanBtn(), constraintsAttendCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendBtnPnl;
}
/**
 * Return the AttendCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getAttendCanBtn() {
	if (attendCanBtn == null) {
		try {
			attendCanBtn = new javax.swing.JButton();
			attendCanBtn.setName("AttendCanBtn");
			attendCanBtn.setText("Cancel");
			attendCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getAttendDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendCanBtn;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getAttendCP() {
	if (attendCP == null) {
		try {
			attendCP = new javax.swing.JPanel();
			attendCP.setName("AttendCP");
			attendCP.setLayout(new java.awt.GridBagLayout());
			attendCP.setBounds(510, 2176, 205, 241);

			java.awt.GridBagConstraints constraintsAttendLbl = new java.awt.GridBagConstraints();
			constraintsAttendLbl.gridx = 0; constraintsAttendLbl.gridy = 0;
			constraintsAttendLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAttendLbl.insets = new java.awt.Insets(5, 5, 2, 5);
			getAttendCP().add(getAttendLbl(), constraintsAttendLbl);

			java.awt.GridBagConstraints constraintsAttendSP = new java.awt.GridBagConstraints();
			constraintsAttendSP.gridx = 0; constraintsAttendSP.gridy = 1;
			constraintsAttendSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAttendSP.weightx = 1.0;
			constraintsAttendSP.weighty = 1.0;
			constraintsAttendSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getAttendCP().add(getAttendSP(), constraintsAttendSP);

			java.awt.GridBagConstraints constraintsAttendBtnPnl = new java.awt.GridBagConstraints();
			constraintsAttendBtnPnl.gridx = 0; constraintsAttendBtnPnl.gridy = 2;
			constraintsAttendBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAttendBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getAttendCP().add(getAttendBtnPnl(), constraintsAttendBtnPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendCP;
}
/**
 * Return the Attend1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getAttendDlg() {
	if (attendDlg == null) {
		try {
			attendDlg = new javax.swing.JDialog(this);
			attendDlg.setName("AttendDlg");
			attendDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			attendDlg.setBounds(28, 1141, 205, 241);
			attendDlg.setModal(true);
			attendDlg.setTitle("Attend a meeting");
			getAttendDlg().setContentPane(getAttendCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendDlg;
}
/**
 * Return the AttendLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getAttendLB() {
	if (attendLB == null) {
		try {
			attendLB = new javax.swing.JList();
			attendLB.setName("AttendLB");
			attendLB.setBounds(0, 0, 160, 120);
			attendLB.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			attendLB.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					try {
						attendListChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendLB;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getAttendLbl() {
	if (attendLbl == null) {
		try {
			attendLbl = new javax.swing.JLabel();
			attendLbl.setName("AttendLbl");
			attendLbl.setText("Select the meeting to attend:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendLbl;
}
/**
 * Return the AttendLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getAttendLM() {
	if (attendLM == null) {
		try {
			attendLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendLM;
}
/**
 * Return the AttendMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getAttendMI() {
	if (attendMI == null) {
		try {
			attendMI = new javax.swing.JMenuItem();
			attendMI.setName("AttendMI");
			attendMI.setMnemonic('A');
			attendMI.setText("Attend a meeting");
			attendMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK, false));
			attendMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doAttendMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendMI;
}
/**
 * Return the AttendOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getAttendOkBtn() {
	if (attendOkBtn == null) {
		try {
			attendOkBtn = new javax.swing.JButton();
			attendOkBtn.setName("AttendOkBtn");
			attendOkBtn.setText("Attend");
			attendOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						attendMeeting();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendOkBtn;
}
/**
 * Return the AttendSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getAttendSP() {
	if (attendSP == null) {
		try {
			attendSP = new javax.swing.JScrollPane();
			attendSP.setName("AttendSP");
			getAttendSP().setViewportView(getAttendLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return attendSP;
}
/**
 * Return the ClearMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getClearMI() {
	if (clearMI == null) {
		try {
			clearMI = new javax.swing.JMenuItem();
			clearMI.setName("ClearMI");
			clearMI.setMnemonic('l');
			clearMI.setText("Clear");
			clearMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.Event.CTRL_MASK, false));
			clearMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doClear();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return clearMI;
}
/**
 * Return the CompressMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getCompressMI() {
	if (compressMI == null) {
		try {
			compressMI = new javax.swing.JCheckBoxMenuItem();
			compressMI.setName("CompressMI");
			compressMI.setSelected(true);
			compressMI.setText("Compress");
			compressMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doCompress();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return compressMI;
}
/**
 * Return the ConnectedLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getConnectedLbl() {
	if (connectedLbl == null) {
		try {
			connectedLbl = new javax.swing.JLabel();
			connectedLbl.setName("ConnectedLbl");
			connectedLbl.setText("Not connected.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return connectedLbl;
}
/**
 * Return the CopyMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCopyMI() {
	if (copyMI == null) {
		try {
			copyMI = new javax.swing.JMenuItem();
			copyMI.setName("CopyMI");
			copyMI.setMnemonic('o');
			copyMI.setText("Copy");
			copyMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK, false));
			copyMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCopy();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return copyMI;
}
/**
 * Return the CutMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCutMI() {
	if (cutMI == null) {
		try {
			cutMI = new javax.swing.JMenuItem();
			cutMI.setName("CutMI");
			cutMI.setMnemonic('C');
			cutMI.setText("Cut");
			cutMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK, false));
			cutMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCut();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return cutMI;
}
/**
 * Return the DebugCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDebugCanBtn() {
	if (debugCanBtn == null) {
		try {
			debugCanBtn = new javax.swing.JButton();
			debugCanBtn.setName("DebugCanBtn");
			debugCanBtn.setText("Cancel");
			debugCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getDebugDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugCanBtn;
}
/**
 * Return the JDialogContentPane5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDebugCP() {
	if (debugCP == null) {
		try {
			debugCP = new javax.swing.JPanel();
			debugCP.setName("DebugCP");
			debugCP.setLayout(new java.awt.GridBagLayout());
			debugCP.setBounds(49, 2577, 197, 125);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.insets = new java.awt.Insets(5, 5, 0, 5);
			getDebugCP().add(getDebugLbl(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsDebugTF = new java.awt.GridBagConstraints();
			constraintsDebugTF.gridx = 0; constraintsDebugTF.gridy = 1;
			constraintsDebugTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDebugTF.weightx = 1.0;
			constraintsDebugTF.insets = new java.awt.Insets(5, 5, 5, 5);
			getDebugCP().add(getDebugTF(), constraintsDebugTF);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(5, 5, 5, 5);
			getDebugCP().add(getDebugPnl(), constraintsJPanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugCP;
}
/**
 * Return the Debug1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getDebugDlg() {
	if (debugDlg == null) {
		try {
			debugDlg = new javax.swing.JDialog(this);
			debugDlg.setName("DebugDlg");
			debugDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			debugDlg.setBounds(35, 926, 197, 125);
			debugDlg.setModal(true);
			debugDlg.setTitle("Enable Debug");
			getDebugDlg().setContentPane(getDebugCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugDlg;
}
/**
 * Return the DebugM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getDebugM() {
	if (debugM == null) {
		try {
			debugM = new javax.swing.JMenu();
			debugM.setName("DebugM");
			debugM.setText("Debug");
			debugM.add(getDebugMI());
			debugM.add(getTimeMI());
			debugM.add(getCompressMI());
			debugM.add(getGridMI());
			debugM.add(getSendTimeMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugM;
}
/**
 * Return the DebugMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getDebugMI() {
	if (debugMI == null) {
		try {
			debugMI = new javax.swing.JCheckBoxMenuItem();
			debugMI.setName("DebugMI");
			debugMI.setText("Debug");
			debugMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doDebug();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugMI;
}
/**
 * Return the DebugOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDebugOkBtn() {
	if (debugOkBtn == null) {
		try {
			debugOkBtn = new javax.swing.JButton();
			debugOkBtn.setName("DebugOkBtn");
			debugOkBtn.setText("Ok");
			debugOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						debug();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugOkBtn;
}
/**
 * Return the DebugTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getDebugTF() {
	if (debugTF == null) {
		try {
			debugTF = new javax.swing.JTextField();
			debugTF.setName("DebugTF");
			debugTF.setToolTipText("Enter code as requested by support");
			debugTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						debug();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugTF;
}
/**
 * Return the DesktopMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDesktopMI() {
	if (desktopMI == null) {
		try {
			desktopMI = new javax.swing.JMenuItem();
			desktopMI.setName("DesktopMI");
			desktopMI.setMnemonic(java.awt.event.KeyEvent.VK_D);
			desktopMI.setText("Share my desktop");
			desktopMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.ALT_MASK, false));
			desktopMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						shareDesktop();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return desktopMI;
}
/**
 * Return the EditM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getEditM() {
	if (editM == null) {
		try {
			editM = new javax.swing.JMenu();
			editM.setName("EditM");
			editM.setText("Edit");
			editM.add(getCutMI());
			editM.add(getCopyMI());
			editM.add(getPasteMI());
			editM.add(getEditSep());
			editM.add(getClearMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return editM;
}
/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getEditSep() {
	if (editSep == null) {
		try {
			editSep = new javax.swing.JSeparator();
			editSep.setName("EditSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return editSep;
}
/**
 * Return the EndMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getEndMI() {
	if (endMI == null) {
		try {
			endMI = new javax.swing.JMenuItem();
			endMI.setName("EndMI");
			endMI.setMnemonic('E');
			endMI.setText("End this meeting");
			endMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK, false));
			endMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doEndMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return endMI;
}
/**
 * Return the ExitMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getExitMI() {
	if (exitMI == null) {
		try {
			exitMI = new javax.swing.JMenuItem();
			exitMI.setName("ExitMI");
			exitMI.setToolTipText("Exit this application");
			exitMI.setText("Exit");
			exitMI.setMnemonic(java.awt.event.KeyEvent.VK_E);
			exitMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						exit(false);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return exitMI;
}
/**
 * Return the FileChooser property value.
 * @return javax.swing.JFileChooser
 */
private javax.swing.JFileChooser getFileChooser() {
	if (fileChooser == null) {
		try {
			fileChooser = new javax.swing.JFileChooser();
			fileChooser.setName("FileChooser");
			fileChooser.setBounds(547, 2934, 405, 244);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return fileChooser;
}
/**
 * Return the FileM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getFileM() {
	if (fileM == null) {
		try {
			fileM = new javax.swing.JMenu();
			fileM.setName("FileM");
			fileM.setText("File");
			fileM.add(getLoginMI());
			fileM.add(getLogoutMI());
			fileM.add(getLogSep());
			fileM.add(getSaveAsMI());
			fileM.add(getSaveSep());
			fileM.add(getGroupMI());
			fileM.add(getCreateIconMI());
			fileM.add(getTunnelMI());
			fileM.add(getGroupSep());
			fileM.add(getExitMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return fileM;
}
/**
 * Return the FitImageMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getFitImageMI() {
	if (fitImageMI == null) {
		try {
			fitImageMI = new javax.swing.JCheckBoxMenuItem();
			fitImageMI.setName("FitImageMI");
			fitImageMI.setText("Fit Image");
			fitImageMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.Event.CTRL_MASK, false));
			fitImageMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doFitImage();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return fitImageMI;
}
/**
 * Return the ModFreezeMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFreezeMI() {
	if (freezeMI == null) {
		try {
			freezeMI = new javax.swing.JMenuItem();
			freezeMI.setName("FreezeMI");
			freezeMI.setMnemonic('F');
			freezeMI.setText("Freeze picture");
			freezeMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.Event.ALT_MASK, false));
			freezeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doFreeze();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return freezeMI;
}
/**
 * Return the GridMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getGridMI() {
	if (gridMI == null) {
		try {
			gridMI = new javax.swing.JCheckBoxMenuItem();
			gridMI.setName("GridMI");
			gridMI.setText("Draw grid");
			gridMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doDrawGrid();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return gridMI;
}
/**
 * Return the GroupMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getGroupMI() {
	if (groupMI == null) {
		try {
			groupMI = new javax.swing.JMenuItem();
			groupMI.setName("GroupMI");
			groupMI.setText("Groups...");
			groupMI.setActionCommand("Groups");
			groupMI.setMnemonic(java.awt.event.KeyEvent.VK_G);
			groupMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doGroups();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return groupMI;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getGroupSep() {
	if (groupSep == null) {
		try {
			groupSep = new javax.swing.JSeparator();
			groupSep.setName("GroupSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return groupSep;
}
/**
 * Return the GrpQueryCompanyLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryCompanyLbl() {
	if (grpQueryCompanyLbl == null) {
		try {
			grpQueryCompanyLbl = new javax.swing.JLabel();
			grpQueryCompanyLbl.setName("GrpQueryCompanyLbl");
			grpQueryCompanyLbl.setText("JLabel15");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryCompanyLbl;
}
/**
 * Return the GrpQueryCP property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getGrpQueryCP() {
	if (grpQueryCP == null) {
		try {
			grpQueryCP = new javax.swing.JPanel();
			grpQueryCP.setName("GrpQueryCP");
			grpQueryCP.setLayout(new java.awt.GridBagLayout());
			grpQueryCP.setBounds(44, 2869, 353, 347);

			java.awt.GridBagConstraints constraintsGrpQueryLbl1 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl1.gridx = 0; constraintsGrpQueryLbl1.gridy = 0;
			constraintsGrpQueryLbl1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl1.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl1(), constraintsGrpQueryLbl1);

			java.awt.GridBagConstraints constraintsGrpQueryNameLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryNameLbl.gridx = 1; constraintsGrpQueryNameLbl.gridy = 0;
			constraintsGrpQueryNameLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryNameLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryNameLbl(), constraintsGrpQueryNameLbl);

			java.awt.GridBagConstraints constraintsGrpQueryLbl2 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl2.gridx = 0; constraintsGrpQueryLbl2.gridy = 1;
			constraintsGrpQueryLbl2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl2.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl2(), constraintsGrpQueryLbl2);

			java.awt.GridBagConstraints constraintsGrpQueryOwnerLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryOwnerLbl.gridx = 1; constraintsGrpQueryOwnerLbl.gridy = 1;
			constraintsGrpQueryOwnerLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryOwnerLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryOwnerLbl(), constraintsGrpQueryOwnerLbl);

			java.awt.GridBagConstraints constraintsGrpQueryLbl3 = new java.awt.GridBagConstraints();
			constraintsGrpQueryLbl3.gridx = 0; constraintsGrpQueryLbl3.gridy = 2;
			constraintsGrpQueryLbl3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryLbl3.insets = new java.awt.Insets(5, 5, 0, 4);
			getGrpQueryCP().add(getGrpQueryLbl3(), constraintsGrpQueryLbl3);

			java.awt.GridBagConstraints constraintsGrpQueryCompanyLbl = new java.awt.GridBagConstraints();
			constraintsGrpQueryCompanyLbl.gridx = 1; constraintsGrpQueryCompanyLbl.gridy = 2;
			constraintsGrpQueryCompanyLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGrpQueryCompanyLbl.insets = new java.awt.Insets(5, 0, 0, 5);
			getGrpQueryCP().add(getGrpQueryCompanyLbl(), constraintsGrpQueryCompanyLbl);

			java.awt.GridBagConstraints constraintsGrpQuerySP = new java.awt.GridBagConstraints();
			constraintsGrpQuerySP.gridx = 0; constraintsGrpQuerySP.gridy = 3;
			constraintsGrpQuerySP.gridwidth = 0;
			constraintsGrpQuerySP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGrpQuerySP.weightx = 1.0;
			constraintsGrpQuerySP.weighty = 1.0;
			constraintsGrpQuerySP.insets = new java.awt.Insets(5, 5, 5, 5);
			getGrpQueryCP().add(getGrpQuerySP(), constraintsGrpQuerySP);

			java.awt.GridBagConstraints constraintsGrpQueryOkBtn = new java.awt.GridBagConstraints();
			constraintsGrpQueryOkBtn.gridx = 0; constraintsGrpQueryOkBtn.gridy = 4;
			constraintsGrpQueryOkBtn.gridwidth = 0;
			constraintsGrpQueryOkBtn.insets = new java.awt.Insets(0, 5, 5, 5);
			getGrpQueryCP().add(getGrpQueryOkBtn(), constraintsGrpQueryOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryCP;
}
/**
 * Return the GrpQueryDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getGrpQueryDlg() {
	if (grpQueryDlg == null) {
		try {
			grpQueryDlg = new javax.swing.JDialog(getInviteDlg());
			grpQueryDlg.setName("GrpQueryDlg2");
			grpQueryDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			grpQueryDlg.setBounds(977, 1918, 353, 347);
			grpQueryDlg.setTitle("Group Member List");
			grpQueryDlg.setContentPane(getGrpQueryCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryDlg;
}
/**
 * Return the GrpQueryLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getGrpQueryLB() {
	if (grpQueryLB == null) {
		try {
			grpQueryLB = new javax.swing.JList();
			grpQueryLB.setName("GrpQueryLB");
			grpQueryLB.setBounds(0, 0, 160, 120);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryLB;
}
/**
 * Return the GrpQueryLbl1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl1() {
	if (grpQueryLbl1 == null) {
		try {
			grpQueryLbl1 = new javax.swing.JLabel();
			grpQueryLbl1.setName("GrpQueryLbl1");
			grpQueryLbl1.setText("Group:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryLbl1;
}
/**
 * Return the GrpQueryLbl2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl2() {
	if (grpQueryLbl2 == null) {
		try {
			grpQueryLbl2 = new javax.swing.JLabel();
			grpQueryLbl2.setName("GrpQueryLbl2");
			grpQueryLbl2.setText("Owner:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryLbl2;
}
/**
 * Return the GrpQueryLbl3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryLbl3() {
	if (grpQueryLbl3 == null) {
		try {
			grpQueryLbl3 = new javax.swing.JLabel();
			grpQueryLbl3.setName("GrpQueryLbl3");
			grpQueryLbl3.setText("Company:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryLbl3;
}
/**
 * Return the GrpQueryLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getGrpQueryLM() {
	if (grpQueryLM == null) {
		try {
			grpQueryLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryLM;
}
/**
 * Return the GrpQueryNameLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryNameLbl() {
	if (grpQueryNameLbl == null) {
		try {
			grpQueryNameLbl = new javax.swing.JLabel();
			grpQueryNameLbl.setName("GrpQueryNameLbl");
			grpQueryNameLbl.setText("JLabel10");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryNameLbl;
}
/**
 * Return the GrpQueryOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getGrpQueryOkBtn() {
	if (grpQueryOkBtn == null) {
		try {
			grpQueryOkBtn = new javax.swing.JButton();
			grpQueryOkBtn.setName("GrpQueryOkBtn");
			grpQueryOkBtn.setText("Close");
			grpQueryOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getGrpQueryDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryOkBtn;
}
/**
 * Return the GrpQueryOwnerLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getGrpQueryOwnerLbl() {
	if (grpQueryOwnerLbl == null) {
		try {
			grpQueryOwnerLbl = new javax.swing.JLabel();
			grpQueryOwnerLbl.setName("GrpQueryOwnerLbl");
			grpQueryOwnerLbl.setText("JLabel13");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQueryOwnerLbl;
}
/**
 * Return the GrpQuerySP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getGrpQuerySP() {
	if (grpQuerySP == null) {
		try {
			grpQuerySP = new javax.swing.JScrollPane();
			grpQuerySP.setName("GrpQuerySP");
			getGrpQuerySP().setViewportView(getGrpQueryLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return grpQuerySP;
}
/**
 * Return the JFrameContentPane2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getImageCP() {
	if (imageCP == null) {
		try {
			imageCP = new javax.swing.JPanel();
			imageCP.setName("ImageCP");
			imageCP.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsImageSP = new java.awt.GridBagConstraints();
			constraintsImageSP.gridx = 0; constraintsImageSP.gridy = 0;
			constraintsImageSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsImageSP.weightx = 1.0;
			constraintsImageSP.weighty = 1.0;
			getImageCP().add(getImageSP(), constraintsImageSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return imageCP;
}
/**
 * Return the JFrame1 property value.
 * @return javax.swing.JFrame
 */
private javax.swing.JFrame getImageFrame() {
	if (imageFrame == null) {
		try {
			imageFrame = new javax.swing.JFrame();
			imageFrame.setName("ImageFrame");
			imageFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			imageFrame.setBounds(30, 1002, 605, 284);
			imageFrame.setJMenuBar(getImageFrameJMenuBar());
			getImageFrame().setContentPane(getImageCP());
			imageFrame.addWindowListener(new java.awt.event.WindowAdapter() {   
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						imageRealized();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				} 
				public void windowClosing(java.awt.event.WindowEvent e) {    
					try {
						doQueryLeave();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return imageFrame;
}
/**
 * Return the ImageFrameJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getImageFrameJMenuBar() {
	if (imageFrameJMenuBar == null) {
		try {
			imageFrameJMenuBar = new javax.swing.JMenuBar();
			imageFrameJMenuBar.setName("ImageFrameJMenuBar");
			imageFrameJMenuBar.add(getViewM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return imageFrameJMenuBar;
}
/**
 * Return the ImagePnl property value.
 * @return oem.edge.ed.odc.meeting.client2.ImagePanel
 */
private ImagePanel getImagePnl() {
	if (imagePnl == null) {
		try {
			imagePnl = new oem.edge.ed.odc.meeting.client.ImagePanel();
			imagePnl.setName("ImagePnl");
			imagePnl.setLocation(0, 0);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return imagePnl;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getImageSP() {
	if (imageSP == null) {
		try {
			imageSP = new javax.swing.JScrollPane();
			imageSP.setName("ImageSP");
			getImageSP().setViewportView(getImagePnl());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return imageSP;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInviteBtnPnl() {
	if (inviteBtnPnl == null) {
		try {
			inviteBtnPnl = new javax.swing.JPanel();
			inviteBtnPnl.setName("InviteBtnPnl");
			inviteBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInviteOkBtn = new java.awt.GridBagConstraints();
			constraintsInviteOkBtn.gridx = 0; constraintsInviteOkBtn.gridy = 0;
			constraintsInviteOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getInviteBtnPnl().add(getInviteOkBtn(), constraintsInviteOkBtn);

			java.awt.GridBagConstraints constraintsInviteCanBtn = new java.awt.GridBagConstraints();
			constraintsInviteCanBtn.gridx = 1; constraintsInviteCanBtn.gridy = 0;
			constraintsInviteCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getInviteBtnPnl().add(getInviteCanBtn(), constraintsInviteCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteBtnPnl;
}
/**
 * Return the InviteCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteCanBtn() {
	if (inviteCanBtn == null) {
		try {
			inviteCanBtn = new javax.swing.JButton();
			inviteCanBtn.setName("InviteCanBtn");
			inviteCanBtn.setText("Close");
			inviteCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getInviteDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteCanBtn;
}
/**
 * Return the JDialogContentPane1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInviteCP() {
	if (inviteCP == null) {
		try {
			inviteCP = new javax.swing.JPanel();
			inviteCP.setName("InviteCP");
			inviteCP.setLayout(new java.awt.GridBagLayout());
			inviteCP.setBounds(505, 2547, 263, 240);

			java.awt.GridBagConstraints constraintsInviteBtnPnl = new java.awt.GridBagConstraints();
			constraintsInviteBtnPnl.gridx = -1; constraintsInviteBtnPnl.gridy = 5;
			constraintsInviteBtnPnl.gridwidth = 0;
			constraintsInviteBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInviteBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getInviteCP().add(getInviteBtnPnl(), constraintsInviteBtnPnl);

			java.awt.GridBagConstraints constraintsJLabel = new java.awt.GridBagConstraints();
			constraintsJLabel.gridx = 0; constraintsJLabel.gridy = 0;
			constraintsJLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel.insets = new java.awt.Insets(5, 5, 0, 5);
			getInviteCP().add(getInviteLbl1(), constraintsJLabel);

			java.awt.GridBagConstraints constraintsInviteUserTF = new java.awt.GridBagConstraints();
			constraintsInviteUserTF.gridx = 0; constraintsInviteUserTF.gridy = 1;
			constraintsInviteUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInviteUserTF.weightx = 1.0;
			constraintsInviteUserTF.insets = new java.awt.Insets(0, 5, 0, 5);
			getInviteCP().add(getInviteUserTF(), constraintsInviteUserTF);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = -1; constraintsJPanel2.gridy = 4;
			constraintsJPanel2.gridwidth = 0;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(0, 5, 0, 5);
			getInviteCP().add(getInvitePnl1(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = -1; constraintsJPanel3.gridy = 2;
			constraintsJPanel3.gridwidth = 0;
			constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel3.weightx = 1.0;
			constraintsJPanel3.insets = new java.awt.Insets(5, 5, 0, 5);
			getInviteCP().add(getInvitePnl2(), constraintsJPanel3);

			java.awt.GridBagConstraints constraintsInviteUserOkBtn = new java.awt.GridBagConstraints();
			constraintsInviteUserOkBtn.gridx = 1; constraintsInviteUserOkBtn.gridy = 1;
			constraintsInviteUserOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getInviteCP().add(getInviteUserOkBtn(), constraintsInviteUserOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteCP;
}
/**
 * Return the Invite1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getInviteDlg() {
	if (inviteDlg == null) {
		try {
			inviteDlg = new javax.swing.JDialog(this);
			inviteDlg.setName("InviteDlg");
			inviteDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			inviteDlg.setBounds(541, 1143, 300, 350);
			inviteDlg.setModal(true);
			inviteDlg.setTitle("Invite users or projects");
			getInviteDlg().setContentPane(getInviteCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteDlg;
}
/**
 * Return the InviteGroupQryBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteGroupQryBtn() {
	if (inviteGroupQryBtn == null) {
		try {
			inviteGroupQryBtn = new javax.swing.JButton();
			inviteGroupQryBtn.setName("InviteGroupQryBtn");
			inviteGroupQryBtn.setToolTipText("View information about selected group");
			inviteGroupQryBtn.setText("");
			inviteGroupQryBtn.setForeground(java.awt.Color.black);
			inviteGroupQryBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/query.gif")));
			inviteGroupQryBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			inviteGroupQryBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						inviteQueryGroup();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteGroupQryBtn;
}
/**
 * Return the ProjectLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getInviteLB() {
	if (inviteLB == null) {
		try {
			inviteLB = new javax.swing.JList();
			inviteLB.setName("InviteLB");
			inviteLB.setBounds(0, 0, 160, 120);
			inviteLB.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			inviteLB.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					try {
						inviteListChg(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLB;
}
/**
 * Return the InviteUserLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getInviteLM() {
	if (inviteLM == null) {
		try {
			inviteLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLM;
}
/**
 * Return the InviteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInviteMI() {
	if (inviteMI == null) {
		try {
			inviteMI = new javax.swing.JMenuItem();
			inviteMI.setName("InviteMI");
			inviteMI.setMnemonic('I');
			inviteMI.setText("Invite...");
			inviteMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK, false));
			inviteMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteMI;
}
/**
 * Return the InviteOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteOkBtn() {
	if (inviteOkBtn == null) {
		try {
			inviteOkBtn = new javax.swing.JButton();
			inviteOkBtn.setName("InviteOkBtn");
			inviteOkBtn.setText("Invite");
			inviteOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						invite(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteOkBtn;
}
/**
 * Return the ProjectSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getInviteSP() {
	if (inviteSP == null) {
		try {
			inviteSP = new javax.swing.JScrollPane();
			inviteSP.setName("InviteSP");
			inviteSP.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getInviteSP().setViewportView(getInviteLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteSP;
}
/**
 * Return the UserDelBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteUserDelBtn() {
	if (inviteUserDelBtn == null) {
		try {
			inviteUserDelBtn = new javax.swing.JButton();
			inviteUserDelBtn.setName("InviteUserDelBtn");
			inviteUserDelBtn.setToolTipText("Drop selected user from my favorites");
			inviteUserDelBtn.setText("");
			inviteUserDelBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			inviteUserDelBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			inviteUserDelBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/deluser.gif")));
			inviteUserDelBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			inviteUserDelBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						inviteDelUser();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteUserDelBtn;
}
/**
 * Return the InviteUserOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getInviteUserOkBtn() {
	if (inviteUserOkBtn == null) {
		try {
			inviteUserOkBtn = new javax.swing.JButton();
			inviteUserOkBtn.setName("InviteUserOkBtn");
			inviteUserOkBtn.setText("Invite");
			inviteUserOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						invite(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteUserOkBtn;
}
/**
 * Return the ToUserTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getInviteUserTF() {
	if (inviteUserTF == null) {
		try {
			inviteUserTF = new javax.swing.JTextField();
			inviteUserTF.setName("InviteUserTF");
			inviteUserTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						invite(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteUserTF;
}
/**
 * Return the JLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInviteLbl1() {
	if (inviteLbl1 == null) {
		try {
			inviteLbl1 = new javax.swing.JLabel();
			inviteLbl1.setName("JLabel");
			inviteLbl1.setText("Enter a Customer Connect ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLbl1;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDebugLbl() {
	if (debugLbl == null) {
		try {
			debugLbl = new javax.swing.JLabel();
			debugLbl.setName("JLabel1");
			debugLbl.setText("Enter activation code:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugLbl;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInviteLbl2() {
	if (inviteLbl2 == null) {
		try {
			inviteLbl2 = new javax.swing.JLabel();
			inviteLbl2.setName("JLabel2");
			inviteLbl2.setText("Or select a ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLbl2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInviteLbl3() {
	if (inviteLbl3 == null) {
		try {
			inviteLbl3 = new javax.swing.JLabel();
			inviteLbl3.setName("JLabel3");
			inviteLbl3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/buddy.gif")));
			inviteLbl3.setText("user, ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLbl3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInviteLbl4() {
	if (inviteLbl4 == null) {
		try {
			inviteLbl4 = new javax.swing.JLabel();
			inviteLbl4.setName("JLabel4");
			inviteLbl4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/group.gif")));
			inviteLbl4.setText("group, or");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLbl4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInviteLbl5() {
	if (inviteLbl5 == null) {
		try {
			inviteLbl5 = new javax.swing.JLabel();
			inviteLbl5.setName("JLabel5");
			inviteLbl5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/dsmp/client/project.gif")));
			inviteLbl5.setText("project:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return inviteLbl5;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDebugPnl() {
	if (debugPnl == null) {
		try {
			debugPnl = new javax.swing.JPanel();
			debugPnl.setName("JPanel1");
			debugPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDebugOkBtn = new java.awt.GridBagConstraints();
			constraintsDebugOkBtn.gridx = 0; constraintsDebugOkBtn.gridy = 0;
			constraintsDebugOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			debugPnl.add(getDebugOkBtn(), constraintsDebugOkBtn);

			java.awt.GridBagConstraints constraintsDebugCanBtn = new java.awt.GridBagConstraints();
			constraintsDebugCanBtn.gridx = 1; constraintsDebugCanBtn.gridy = 0;
			constraintsDebugCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			debugPnl.add(getDebugCanBtn(), constraintsDebugCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return debugPnl;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInvitePnl1() {
	if (invitePnl1 == null) {
		try {
			invitePnl1 = new javax.swing.JPanel();
			invitePnl1.setName("JPanel2");
			invitePnl1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsInviteUserDelBtn = new java.awt.GridBagConstraints();
			constraintsInviteUserDelBtn.gridx = 1; constraintsInviteUserDelBtn.gridy = 0;
			constraintsInviteUserDelBtn.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsInviteUserDelBtn.weighty = 1.0;
			constraintsInviteUserDelBtn.insets = new java.awt.Insets(0, 2, 2, 0);
			getInvitePnl1().add(getInviteUserDelBtn(), constraintsInviteUserDelBtn);

			java.awt.GridBagConstraints constraintsInviteGroupQryBtn = new java.awt.GridBagConstraints();
			constraintsInviteGroupQryBtn.gridx = 1; constraintsInviteGroupQryBtn.gridy = 1;
			constraintsInviteGroupQryBtn.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsInviteGroupQryBtn.weighty = 1.0;
			constraintsInviteGroupQryBtn.insets = new java.awt.Insets(2, 2, 0, 0);
			getInvitePnl1().add(getInviteGroupQryBtn(), constraintsInviteGroupQryBtn);

			java.awt.GridBagConstraints constraintsInviteSP = new java.awt.GridBagConstraints();
			constraintsInviteSP.gridx = 0; constraintsInviteSP.gridy = 0;
constraintsInviteSP.gridheight = 0;
			constraintsInviteSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsInviteSP.weightx = 1.0;
			constraintsInviteSP.weighty = 1.0;
			getInvitePnl1().add(getInviteSP(), constraintsInviteSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return invitePnl1;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getInvitePnl2() {
	if (invitePnl2 == null) {
		try {
			invitePnl2 = new javax.swing.JPanel();
			invitePnl2.setName("JPanel3");
			invitePnl2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			getInvitePnl2().add(getInviteLbl2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 1; constraintsJLabel3.gridy = 0;
			getInvitePnl2().add(getInviteLbl3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 2; constraintsJLabel4.gridy = 0;
			getInvitePnl2().add(getInviteLbl4(), constraintsJLabel4);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 3; constraintsJLabel5.gridy = 0;
			constraintsJLabel5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel5.weightx = 1.0;
			getInvitePnl2().add(getInviteLbl5(), constraintsJLabel5);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return invitePnl2;
}
/**
 * Return the JSeparator4 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getViewSep() {
	if (viewSep == null) {
		try {
			viewSep = new javax.swing.JSeparator();
			viewSep.setName("JSeparator4");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return viewSep;
}
/**
 * Return the KeyM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getKeyM() {
	if (keyM == null) {
		try {
			keyM = new javax.swing.JMenu();
			keyM.setName("KeyM");
			keyM.setToolTipText("How key strokes are sent when in control");
			keyM.setText("Key Interpretation");
			keyM.add(getKeyNormalMI());
			keyM.add(getKeyRefineMI());
			keyM.add(getKeyStrictMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return keyM;
}
/**
 * Return the KeyNormalMI property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
private javax.swing.JRadioButtonMenuItem getKeyNormalMI() {
	if (keyNormalMI == null) {
		try {
			keyNormalMI = new javax.swing.JRadioButtonMenuItem();
			keyNormalMI.setName("KeyNormalMI");
			keyNormalMI.setSelected(true);
			keyNormalMI.setToolTipText("As interpreted by Java");
			keyNormalMI.setText("Normal");
			keyNormalMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						keyStateChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return keyNormalMI;
}
/**
 * Return the KeyRefineMI property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
private javax.swing.JRadioButtonMenuItem getKeyRefineMI() {
	if (keyRefineMI == null) {
		try {
			keyRefineMI = new javax.swing.JRadioButtonMenuItem();
			keyRefineMI.setName("KeyRefineMI");
			keyRefineMI.setToolTipText("Printables as chars, all others as codes");
			keyRefineMI.setText("Refined");
			keyRefineMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						keyStateChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return keyRefineMI;
}
/**
 * Return the KeyStrictMI property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
private javax.swing.JRadioButtonMenuItem getKeyStrictMI() {
	if (keyStrictMI == null) {
		try {
			keyStrictMI = new javax.swing.JRadioButtonMenuItem();
			keyStrictMI.setName("KeyStrictMI");
			keyStrictMI.setToolTipText("All as codes");
			keyStrictMI.setText("Strict");
			keyStrictMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						keyStateChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return keyStrictMI;
}
/**
 * Return the LeaveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLeaveMI() {
	if (leaveMI == null) {
		try {
			leaveMI = new javax.swing.JMenuItem();
			leaveMI.setName("LeaveMI");
			leaveMI.setMnemonic('L');
			leaveMI.setText("Leave this meeting");
			leaveMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.Event.CTRL_MASK, false));
			leaveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLeaveMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return leaveMI;
}
/**
 * Return the LoginCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginCanBtn() {
	if (loginCanBtn == null) {
		try {
			loginCanBtn = new javax.swing.JButton();
			loginCanBtn.setName("LoginCanBtn");
			loginCanBtn.setToolTipText("Press to cancel login");
			loginCanBtn.setText("Cancel");
			loginCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getLoginDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginCanBtn;
}
/**
 * Return the JDialogContentPane2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLoginCP() {
	if (loginCP == null) {
		try {
			loginCP = new javax.swing.JPanel();
			loginCP.setName("LoginCP");
			loginCP.setLayout(new java.awt.GridBagLayout());
			loginCP.setBounds(34, 1871, 204, 133);

			java.awt.GridBagConstraints constraintsLoginUserLbl = new java.awt.GridBagConstraints();
			constraintsLoginUserLbl.gridx = 0; constraintsLoginUserLbl.gridy = 0;
			constraintsLoginUserLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLoginUserLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getLoginCP().add(getLoginUserLbl(), constraintsLoginUserLbl);

			java.awt.GridBagConstraints constraintsLoginUserTF = new java.awt.GridBagConstraints();
			constraintsLoginUserTF.gridx = 1; constraintsLoginUserTF.gridy = 0;
			constraintsLoginUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLoginUserTF.weightx = 1.0;
			constraintsLoginUserTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getLoginCP().add(getLoginUserTF(), constraintsLoginUserTF);

			java.awt.GridBagConstraints constraintsLoginPwdLbl = new java.awt.GridBagConstraints();
			constraintsLoginPwdLbl.gridx = 0; constraintsLoginPwdLbl.gridy = 1;
			constraintsLoginPwdLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLoginPwdLbl.insets = new java.awt.Insets(5, 10, 0, 0);
			getLoginCP().add(getLoginPwdLbl(), constraintsLoginPwdLbl);

			java.awt.GridBagConstraints constraintsLoginPwdTF = new java.awt.GridBagConstraints();
			constraintsLoginPwdTF.gridx = 1; constraintsLoginPwdTF.gridy = 1;
			constraintsLoginPwdTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLoginPwdTF.weightx = 1.0;
			constraintsLoginPwdTF.insets = new java.awt.Insets(5, 5, 0, 10);
			getLoginCP().add(getLoginPwdTF(), constraintsLoginPwdTF);

			java.awt.GridBagConstraints constraintsLoginPnl = new java.awt.GridBagConstraints();
			constraintsLoginPnl.gridx = 0; constraintsLoginPnl.gridy = 2;
			constraintsLoginPnl.gridwidth = 0;
			constraintsLoginPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLoginPnl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsLoginPnl.weightx = 1.0;
			constraintsLoginPnl.weighty = 1.0;
			constraintsLoginPnl.insets = new java.awt.Insets(10, 10, 10, 10);
			getLoginCP().add(getLoginPnl(), constraintsLoginPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginCP;
}
/**
 * Return the Login1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getLoginDlg() {
	if (loginDlg == null) {
		try {
			loginDlg = new javax.swing.JDialog(this);
			loginDlg.setName("LoginDlg");
			loginDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			loginDlg.setBounds(29, 1842, 204, 133);
			loginDlg.setModal(true);
			loginDlg.setTitle("Login");
			getLoginDlg().setContentPane(getLoginCP());
			loginDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowActivated(java.awt.event.WindowEvent e) {    
					try {
						doLoginFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginDlg;
}
/**
 * Return the LoginMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLoginMI() {
	if (loginMI == null) {
		try {
			loginMI = new javax.swing.JMenuItem();
			loginMI.setName("LoginMI");
			loginMI.setText("Login...");
			loginMI.setMnemonic(java.awt.event.KeyEvent.VK_L);
			loginMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLogin();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginMI;
}
/**
 * Return the LoginOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginOkBtn() {
	if (loginOkBtn == null) {
		try {
			loginOkBtn = new javax.swing.JButton();
			loginOkBtn.setName("LoginOkBtn");
			loginOkBtn.setToolTipText("Press to login");
			loginOkBtn.setText("Ok");
			loginOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						login();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginOkBtn;
}
/**
 * Return the JPanel21 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLoginPnl() {
	if (loginPnl == null) {
		try {
			loginPnl = new javax.swing.JPanel();
			loginPnl.setName("LoginPnl");
			loginPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLoginOkBtn = new java.awt.GridBagConstraints();
			constraintsLoginOkBtn.gridx = 0; constraintsLoginOkBtn.gridy = 0;
			constraintsLoginOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getLoginPnl().add(getLoginOkBtn(), constraintsLoginOkBtn);

			java.awt.GridBagConstraints constraintsLoginCanBtn = new java.awt.GridBagConstraints();
			constraintsLoginCanBtn.gridx = 1; constraintsLoginCanBtn.gridy = 0;
			constraintsLoginCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getLoginPnl().add(getLoginCanBtn(), constraintsLoginCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginPnl;
}
/**
 * Return the PwdLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLoginPwdLbl() {
	if (loginPwdLbl == null) {
		try {
			loginPwdLbl = new javax.swing.JLabel();
			loginPwdLbl.setName("LoginPwdLbl");
			loginPwdLbl.setText("Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginPwdLbl;
}
/**
 * Return the PwdTF property value.
 * @return javax.swing.JPasswordField
 */
private javax.swing.JPasswordField getLoginPwdTF() {
	if (loginPwdTF == null) {
		try {
			loginPwdTF = new javax.swing.JPasswordField();
			loginPwdTF.setName("LoginPwdTF");
			loginPwdTF.setToolTipText("Enter your password");
			loginPwdTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						login();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginPwdTF;
}
/**
 * Return the UserLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLoginUserLbl() {
	if (loginUserLbl == null) {
		try {
			loginUserLbl = new javax.swing.JLabel();
			loginUserLbl.setName("LoginUserLbl");
			loginUserLbl.setText("User ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginUserLbl;
}
/**
 * Return the UserTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getLoginUserTF() {
	if (loginUserTF == null) {
		try {
			loginUserTF = new javax.swing.JTextField();
			loginUserTF.setName("LoginUserTF");
			loginUserTF.setToolTipText("Enter your user ID");
			loginUserTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getLoginPwdTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return loginUserTF;
}
/**
 * Return the LogoutMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getLogoutMI() {
	if (logoutMI == null) {
		try {
			logoutMI = new javax.swing.JMenuItem();
			logoutMI.setName("LogoutMI");
			logoutMI.setText("Logout");
			logoutMI.setMnemonic(java.awt.event.KeyEvent.VK_O);
			logoutMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLogout();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return logoutMI;
}
/**
 * Return the LogSep property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getLogSep() {
	if (logSep == null) {
		try {
			logSep = new javax.swing.JSeparator();
			logSep.setName("LogSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return logSep;
}
/**
 * Return the ManageGroups property value.
 * @return oem.edge.ed.odc.meeting.client.ManageGroups
 */
private ManageGroups getManageGroups() {
	if (manageGroups == null) {
		try {
			manageGroups = new oem.edge.ed.odc.meeting.client.ManageGroups(this);
			manageGroups.setSize(417, 500);
			manageGroups.setName("ManageGroups");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return manageGroups;
}
/**
 * Return the MeetingLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getMeetingLbl() {
	if (meetingLbl == null) {
		try {
			meetingLbl = new javax.swing.JLabel();
			meetingLbl.setName("MeetingLbl");
			meetingLbl.setText("Not attending a meeting.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingLbl;
}
/**
 * Return the MeetingM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getMeetingM() {
	if (meetingM == null) {
		try {
			meetingM = new javax.swing.JMenu();
			meetingM.setName("MeetingM");
			meetingM.setText("Meeting");
			meetingM.add(getAttendMI());
			meetingM.add(getCallMI());
			meetingM.add(getStartMI());
			meetingM.add(getLeaveMI());
			meetingM.add(getEndMI());
			meetingM.add(getMeetingSep());
			meetingM.add(getInviteMI());
			meetingM.add(getUrlMI());
			meetingM.add(getSettingsMI());
			meetingM.add(getKeyM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingM;
}
/**
 * Return the JSeparator7 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getMeetingSep() {
	if (meetingSep == null) {
		try {
			meetingSep = new javax.swing.JSeparator();
			meetingSep.setName("MeetingSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingSep;
}
/**
 * Return the MeetingViewer2JMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getMeetingViewer2JMenuBar() {
	if (meetingViewer2JMenuBar == null) {
		try {
			meetingViewer2JMenuBar = new javax.swing.JMenuBar();
			meetingViewer2JMenuBar.setName("MeetingViewer2JMenuBar");
			meetingViewer2JMenuBar.add(getFileM());
			meetingViewer2JMenuBar.add(getEditM());
			meetingViewer2JMenuBar.add(getMeetingM());
			meetingViewer2JMenuBar.add(getShareM());
			meetingViewer2JMenuBar.add(getDebugM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingViewer2JMenuBar;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getMeetingViewerCP() {
	if (meetingViewerCP == null) {
		try {
			meetingViewerCP = new javax.swing.JPanel();
			meetingViewerCP.setName("MeetingViewerCP");
			meetingViewerCP.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsToolbarPnl = new java.awt.GridBagConstraints();
			constraintsToolbarPnl.gridx = 1; constraintsToolbarPnl.gridy = 0;
			constraintsToolbarPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsToolbarPnl.insets = new java.awt.Insets(2, 2, 4, 2);
			getMeetingViewerCP().add(getToolbarPnl(), constraintsToolbarPnl);

			java.awt.GridBagConstraints constraintsConnectedLbl = new java.awt.GridBagConstraints();
			constraintsConnectedLbl.gridx = 1; constraintsConnectedLbl.gridy = 1;
			constraintsConnectedLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsConnectedLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsConnectedLbl.insets = new java.awt.Insets(0, 2, 4, 2);
			getMeetingViewerCP().add(getConnectedLbl(), constraintsConnectedLbl);

			java.awt.GridBagConstraints constraintsMeetingLbl = new java.awt.GridBagConstraints();
			constraintsMeetingLbl.gridx = 1; constraintsMeetingLbl.gridy = 2;
			constraintsMeetingLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeetingLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMeetingLbl.insets = new java.awt.Insets(0, 2, 4, 2);
			getMeetingViewerCP().add(getMeetingLbl(), constraintsMeetingLbl);

			java.awt.GridBagConstraints constraintsSplitPane = new java.awt.GridBagConstraints();
			constraintsSplitPane.gridx = 0; constraintsSplitPane.gridy = 3;
			constraintsSplitPane.gridwidth = 2;
			constraintsSplitPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSplitPane.weightx = 1.0;
			constraintsSplitPane.weighty = 1.0;
			constraintsSplitPane.insets = new java.awt.Insets(0, 2, 2, 2);
			getMeetingViewerCP().add(getMeetingSP(), constraintsSplitPane);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingViewerCP;
}
/**
 * Return the MsgPnl property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getMsgPnl() {
	if (msgPnl == null) {
		try {
			msgPnl = new javax.swing.JPanel();
			msgPnl.setName("MsgPnl");
			msgPnl.setPreferredSize(new java.awt.Dimension(753, 110));
			msgPnl.setLayout(new java.awt.GridBagLayout());
			msgPnl.setMinimumSize(new java.awt.Dimension(54, 110));
			msgPnl.setMaximumSize(new java.awt.Dimension(2147483647, 110));

			java.awt.GridBagConstraints constraintsMsgSP = new java.awt.GridBagConstraints();
			constraintsMsgSP.gridx = 0; constraintsMsgSP.gridy = 0;
			constraintsMsgSP.gridwidth = 0;
			constraintsMsgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMsgSP.weightx = 1.0;
			constraintsMsgSP.weighty = 1.0;
			getMsgPnl().add(getMsgSP(), constraintsMsgSP);

			java.awt.GridBagConstraints constraintsMsgTF = new java.awt.GridBagConstraints();
			constraintsMsgTF.gridx = 0; constraintsMsgTF.gridy = 1;
			constraintsMsgTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMsgTF.weightx = 1.0;
			constraintsMsgTF.insets = new java.awt.Insets(2, 0, 0, 0);
			getMsgPnl().add(getMsgTF(), constraintsMsgTF);

			java.awt.GridBagConstraints constraintsMsgSendBtn = new java.awt.GridBagConstraints();
			constraintsMsgSendBtn.gridx = 1; constraintsMsgSendBtn.gridy = 1;
			constraintsMsgSendBtn.insets = new java.awt.Insets(2, 5, 0, 0);
			getMsgPnl().add(getMsgSendBtn(), constraintsMsgSendBtn);
			msgPnl.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Message Center", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(102,102,153)));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return msgPnl;
}
/**
 * Return the MsgSendBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMsgSendBtn() {
	if (msgSendBtn == null) {
		try {
			msgSendBtn = new javax.swing.JButton();
			msgSendBtn.setName("MsgSendBtn");
			msgSendBtn.setToolTipText("Send the message to all participants");
			msgSendBtn.setText("Send");
			msgSendBtn.setMargin(new java.awt.Insets(2, 5, 2, 5));
			msgSendBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return msgSendBtn;
}
/**
 * Return the MsgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getMsgSP() {
	if (msgSP == null) {
		try {
			msgSP = new javax.swing.JScrollPane();
			msgSP.setName("MsgSP");
			getMsgSP().setViewportView(getMsgTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return msgSP;
}
/**
 * Return the MsgTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMsgTA() {
	if (msgTA == null) {
		try {
			msgTA = new javax.swing.JTextArea();
			msgTA.setName("MsgTA");
			msgTA.setLineWrap(true);
			msgTA.setToolTipText("Messages seen by all participants");
			msgTA.setWrapStyleWord(true);
			msgTA.setBounds(0, 0, 160, 120);
			msgTA.setEditable(false);
			msgTA.addCaretListener(new javax.swing.event.CaretListener() { 
				public void caretUpdate(javax.swing.event.CaretEvent e) {    
					try {
						adjustEditMenu(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return msgTA;
}
/**
 * Return the MsgTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getMsgTF() {
	if (msgTF == null) {
		try {
			msgTF = new javax.swing.JTextField();
			msgTF.setName("MsgTF");
			msgTF.setToolTipText("Type a message to send to all participants");
			msgTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sendMsg(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
			msgTF.addCaretListener(new javax.swing.event.CaretListener() { 
				public void caretUpdate(javax.swing.event.CaretEvent e) {    
					try {
						adjustEditMenu(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return msgTF;
}
/**
 * Return the ModPassMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPassMI() {
	if (passMI == null) {
		try {
			passMI = new javax.swing.JMenuItem();
			passMI.setName("PassMI");
			passMI.setMnemonic('P');
			passMI.setText("Pass control");
			passMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.ALT_MASK, false));
			passMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPassCtrl();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return passMI;
}
/**
 * Return the JSeparator5 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getPassSep() {
	if (passSep == null) {
		try {
			passSep = new javax.swing.JSeparator();
			passSep.setName("PassSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return passSep;
}
/**
 * Return the PasteMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPasteMI() {
	if (pasteMI == null) {
		try {
			pasteMI = new javax.swing.JMenuItem();
			pasteMI.setName("PasteMI");
			pasteMI.setMnemonic('P');
			pasteMI.setText("Paste");
			pasteMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK, false));
			pasteMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPaste();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return pasteMI;
}
/**
 * Return the PresencePanel property value.
 * @return oem.edge.ed.odc.meeting.client2.PresencePanel
 */
private PresencePanel getPresencePnl() {
	if (presencePnl == null) {
		try {
			presencePnl = new oem.edge.ed.odc.meeting.client.PresencePanel();
			presencePnl.setName("PresencePnl");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return presencePnl;
}
/**
 * Return the JDialogContentPane4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getQuestionCP() {
	if (questionCP == null) {
		try {
			questionCP = new javax.swing.JPanel();
			questionCP.setName("QuestionCP");
			questionCP.setLayout(new java.awt.GridBagLayout());
			questionCP.setBounds(36, 2162, 347, 252);

			java.awt.GridBagConstraints constraintsQuestionYesBtn = new java.awt.GridBagConstraints();
			constraintsQuestionYesBtn.gridx = 0; constraintsQuestionYesBtn.gridy = 2;
			constraintsQuestionYesBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsQuestionYesBtn.weightx = 1.0;
			constraintsQuestionYesBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			java.awt.GridBagConstraints constraintsQuestionNoBtn = new java.awt.GridBagConstraints();
			constraintsQuestionNoBtn.gridx = 1; constraintsQuestionNoBtn.gridy = 2;
			constraintsQuestionNoBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsQuestionNoBtn.weightx = 1.0;
			constraintsQuestionNoBtn.insets = new java.awt.Insets(5, 5, 5, 5);
			java.awt.GridBagConstraints constraintsQuestionSP = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints42 = new java.awt.GridBagConstraints();
			consGridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints42.weighty = 0.0D;
			consGridBagConstraints42.weightx = 0.0D;
			consGridBagConstraints42.gridy = 1;
			consGridBagConstraints42.gridx = 0;
			consGridBagConstraints42.gridwidth = 0;
			consGridBagConstraints42.insets = new java.awt.Insets(0,5,5,5);
			constraintsQuestionSP.gridx = 0; constraintsQuestionSP.gridy = 0;
			constraintsQuestionSP.gridwidth = 0;
			constraintsQuestionSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsQuestionSP.weightx = 1.0;
			constraintsQuestionSP.weighty = 1.0;
			constraintsQuestionSP.insets = new java.awt.Insets(5, 5, 5, 5);
			questionCP.add(getQuestionYesBtn(), constraintsQuestionYesBtn);
			questionCP.add(getQuestionNoBtn(), constraintsQuestionNoBtn);
			getQuestionCP().add(getQuestionSP(), constraintsQuestionSP);
			questionCP.add(getTransferDurationPnl(), consGridBagConstraints42);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionCP;
}
/**
 * Return the Question1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getQuestionDlg() {
	if (questionDlg == null) {
		try {
			questionDlg = new javax.swing.JDialog(this);
			questionDlg.setName("QuestionDlg");
			questionDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			questionDlg.setBounds(33, 2159, 347, 252);
			questionDlg.setModal(true);
			questionDlg.setTitle("Question");
			getQuestionDlg().setContentPane(getQuestionCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionDlg;
}
/**
 * Return the QuestionNoBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getQuestionNoBtn() {
	if (questionNoBtn == null) {
		try {
			questionNoBtn = new javax.swing.JButton();
			questionNoBtn.setName("QuestionNoBtn");
			questionNoBtn.setText("Cancel");
			questionNoBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getQuestionDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionNoBtn;
}
/**
 * Return the QuestionSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getQuestionSP() {
	if (questionSP == null) {
		try {
			questionSP = new javax.swing.JScrollPane();
			questionSP.setName("QuestionSP");
			getQuestionSP().setViewportView(getQuestionTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionSP;
}
/**
 * Return the QuestionTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getQuestionTA() {
	if (questionTA == null) {
		try {
			questionTA = new javax.swing.JTextArea();
			questionTA.setName("QuestionTA");
			questionTA.setLineWrap(true);
			questionTA.setWrapStyleWord(true);
			questionTA.setBackground(java.awt.SystemColor.control);
			questionTA.setBounds(0, 0, 160, 120);
			questionTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionTA;
}
/**
 * Return the QuestionYesBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getQuestionYesBtn() {
	if (questionYesBtn == null) {
		try {
			questionYesBtn = new javax.swing.JButton();
			questionYesBtn.setName("QuestionYesBtn");
			questionYesBtn.setText("Force");
			questionYesBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						answerYes();
						getQuestionDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return questionYesBtn;
}
/**
 * Return the ResizeImageMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getResizeImageMI() {
	if (resizeImageMI == null) {
		try {
			resizeImageMI = new javax.swing.JCheckBoxMenuItem();
			resizeImageMI.setName("ResizeImageMI");
			resizeImageMI.setSelected(true);
			resizeImageMI.setText("Auto-Resize Image");
			resizeImageMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK, false));
			resizeImageMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doResizeImage();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return resizeImageMI;
}
/**
 * Return the ResumeMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getResumeMI() {
	if (resumeMI == null) {
		try {
			resumeMI = new javax.swing.JMenuItem();
			resumeMI.setName("ResumeMI");
			resumeMI.setMnemonic('R');
			resumeMI.setText("Resume sharing");
			resumeMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.ALT_MASK, false));
			resumeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doResume();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return resumeMI;
}
/**
 * Return the SaveAsMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSaveAsMI() {
	if (saveAsMI == null) {
		try {
			saveAsMI = new javax.swing.JMenuItem();
			saveAsMI.setName("SaveAsMI");
			saveAsMI.setText("Save As...");
			saveAsMI.setMnemonic(java.awt.event.KeyEvent.VK_S);
			saveAsMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doSave();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return saveAsMI;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSaveSep() {
	if (saveSep == null) {
		try {
			saveSep = new javax.swing.JSeparator();
			saveSep.setName("SaveSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return saveSep;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getScreenLbl() {
	if (screenLbl == null) {
		try {
			screenLbl = new javax.swing.JLabel();
			screenLbl.setName("ScreenLbl");
			screenLbl.setText("Screen address:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return screenLbl;
}
/**
 * Return the JPanel7 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getScreenPnl() {
	if (screenPnl == null) {
		try {
			screenPnl = new javax.swing.JPanel();
			screenPnl.setName("ScreenPnl");
			screenPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsScreenLbl = new java.awt.GridBagConstraints();
			constraintsScreenLbl.gridx = 0; constraintsScreenLbl.gridy = 0;
			constraintsScreenLbl.insets = new java.awt.Insets(0, 0, 0, 10);
			getScreenPnl().add(getScreenLbl(), constraintsScreenLbl);

			java.awt.GridBagConstraints constraintsScreenTF = new java.awt.GridBagConstraints();
			constraintsScreenTF.gridx = 1; constraintsScreenTF.gridy = 0;
			constraintsScreenTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsScreenTF.weightx = 1.0;
			getScreenPnl().add(getScreenTF(), constraintsScreenTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return screenPnl;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getScreenTF() {
	if (screenTF == null) {
		try {
			screenTF = new javax.swing.JTextField();
			screenTF.setName("ScreenTF");
			screenTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startMeeting();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return screenTF;
}
/**
 * Return the SecConfCB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getSecConfCB() {
	if (secConfCB == null) {
		try {
			secConfCB = new javax.swing.JRadioButton();
			secConfCB.setName("SecConfCB");
			secConfCB.setText("Confidential");
			secConfCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						secStateChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return secConfCB;
}
/**
 * Return the SecOtherCB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getSecOtherCB() {
	if (secOtherCB == null) {
		try {
			secOtherCB = new javax.swing.JRadioButton();
			secOtherCB.setName("SecOtherCB");
			secOtherCB.setText("Other:");
			secOtherCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						secStateChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return secOtherCB;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getSecOtherTF() {
	if (secOtherTF == null) {
		try {
			secOtherTF = new javax.swing.JTextField();
			secOtherTF.setName("SecOtherTF");
			secOtherTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startMeeting();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return secOtherTF;
}
/**
 * Return the SecUnclassCB property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getSecUnclassCB() {
	if (secUnclassCB == null) {
		try {
			secUnclassCB = new javax.swing.JRadioButton();
			secUnclassCB.setName("SecUnclassCB");
			secUnclassCB.setText("Unclassified");
			secUnclassCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						secStateChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return secUnclassCB;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSecurityLbl() {
	if (securityLbl == null) {
		try {
			securityLbl = new javax.swing.JLabel();
			securityLbl.setName("SecurityLbl");
			securityLbl.setText("Security Classification:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return securityLbl;
}
/**
 * Return the JPanel6 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSecurityPnl() {
	if (securityPnl == null) {
		try {
			securityPnl = new javax.swing.JPanel();
			securityPnl.setName("SecurityPnl");
			securityPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSecurityLbl = new java.awt.GridBagConstraints();
			constraintsSecurityLbl.gridx = 0; constraintsSecurityLbl.gridy = 0;
			constraintsSecurityLbl.insets = new java.awt.Insets(0, 0, 0, 10);
			getSecurityPnl().add(getSecurityLbl(), constraintsSecurityLbl);

			java.awt.GridBagConstraints constraintsSecConfCB = new java.awt.GridBagConstraints();
			constraintsSecConfCB.gridx = 1; constraintsSecConfCB.gridy = 0;
			constraintsSecConfCB.gridwidth = 0;
			constraintsSecConfCB.anchor = java.awt.GridBagConstraints.WEST;
			getSecurityPnl().add(getSecConfCB(), constraintsSecConfCB);

			java.awt.GridBagConstraints constraintsSecUnclassCB = new java.awt.GridBagConstraints();
			constraintsSecUnclassCB.gridx = 1; constraintsSecUnclassCB.gridy = 1;
			constraintsSecUnclassCB.gridwidth = 0;
			constraintsSecUnclassCB.anchor = java.awt.GridBagConstraints.WEST;
			getSecurityPnl().add(getSecUnclassCB(), constraintsSecUnclassCB);

			java.awt.GridBagConstraints constraintsSecOtherCB = new java.awt.GridBagConstraints();
			constraintsSecOtherCB.gridx = 1; constraintsSecOtherCB.gridy = 2;
			constraintsSecOtherCB.anchor = java.awt.GridBagConstraints.WEST;
			getSecurityPnl().add(getSecOtherCB(), constraintsSecOtherCB);

			java.awt.GridBagConstraints constraintsSecOtherTF = new java.awt.GridBagConstraints();
			constraintsSecOtherTF.gridx = 2; constraintsSecOtherTF.gridy = 2;
			constraintsSecOtherTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSecOtherTF.weightx = 1.0;
			getSecurityPnl().add(getSecOtherTF(), constraintsSecOtherTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return securityPnl;
}
/**
 * Return the SendTimeMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getSendTimeMI() {
	if (sendTimeMI == null) {
		try {
			sendTimeMI = new javax.swing.JCheckBoxMenuItem();
			sendTimeMI.setName("SendTimeMI");
			sendTimeMI.setText("Send times");
			sendTimeMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doSendTime();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return sendTimeMI;
}
/**
 * Return the ShareM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getShareM() {
	if (shareM == null) {
		try {
			shareM = new javax.swing.JMenu();
			shareM.setName("ShareM");
			shareM.setText("Share");
			shareM.add(getDesktopMI());
			shareM.add(getWindowMI());
			shareM.add(getStopMI());
			shareM.add(getShareSep());
			shareM.add(getPassMI());
			shareM.add(getTakeMI());
			shareM.add(getPassSep());
			shareM.add(getFreezeMI());
			shareM.add(getResumeMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return shareM;
}
/**
 * Return the JSeparator6 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getShareSep() {
	if (shareSep == null) {
		try {
			shareSep = new javax.swing.JSeparator();
			shareSep.setName("ShareSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return shareSep;
}
/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
private javax.swing.JSplitPane getMeetingSP() {
	if (meetingSP == null) {
		try {
			meetingSP = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			meetingSP.setName("SplitPane");
			meetingSP.setDividerLocation(500);
			getMeetingSP().add(getPresencePnl(), "top");
			getMeetingSP().add(getMsgPnl(), "bottom");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return meetingSP;
}
/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getStartBtnPnl() {
	if (startBtnPnl == null) {
		try {
			startBtnPnl = new javax.swing.JPanel();
			startBtnPnl.setName("StartBtnPnl");
			startBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsStartOkBtn = new java.awt.GridBagConstraints();
			constraintsStartOkBtn.gridx = 0; constraintsStartOkBtn.gridy = 0;
			constraintsStartOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getStartBtnPnl().add(getStartOkBtn(), constraintsStartOkBtn);

			java.awt.GridBagConstraints constraintsStartCanBtn = new java.awt.GridBagConstraints();
			constraintsStartCanBtn.gridx = 1; constraintsStartCanBtn.gridy = 0;
			constraintsStartCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getStartBtnPnl().add(getStartCanBtn(), constraintsStartCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startBtnPnl;
}
/**
 * Return the StartCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getStartCanBtn() {
	if (startCanBtn == null) {
		try {
			startCanBtn = new javax.swing.JButton();
			startCanBtn.setName("StartCanBtn");
			startCanBtn.setText("Cancel");
			startCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getStartDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startCanBtn;
}
/**
 * Return the JDialogContentPane4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getStartCP() {
	if (startCP == null) {
		try {
			startCP = new javax.swing.JPanel();
			startCP.setName("StartCP");
			startCP.setLayout(new java.awt.GridBagLayout());
			startCP.setBounds(449, 1858, 339, 212);

			java.awt.GridBagConstraints constraintsTitlePnl = new java.awt.GridBagConstraints();
			constraintsTitlePnl.gridx = 0; constraintsTitlePnl.gridy = 0;
			constraintsTitlePnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTitlePnl.insets = new java.awt.Insets(5, 5, 0, 5);
			getStartCP().add(getStartTitlePnl(), constraintsTitlePnl);

			java.awt.GridBagConstraints constraintsSecurityPnl = new java.awt.GridBagConstraints();
			constraintsSecurityPnl.gridx = 0; constraintsSecurityPnl.gridy = 1;
			constraintsSecurityPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSecurityPnl.insets = new java.awt.Insets(5, 5, 0, 5);
			getStartCP().add(getSecurityPnl(), constraintsSecurityPnl);

			java.awt.GridBagConstraints constraintsScreenPnl = new java.awt.GridBagConstraints();
			constraintsScreenPnl.gridx = 0; constraintsScreenPnl.gridy = 4;
			constraintsScreenPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsScreenPnl.insets = new java.awt.Insets(5, 5, 0, 5);
			java.awt.GridBagConstraints constraintsStartBtnPnl = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints71 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints101 = new java.awt.GridBagConstraints();
			consGridBagConstraints71.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints71.weighty = 0.0D;
			consGridBagConstraints71.weightx = 0.0D;
			consGridBagConstraints71.gridy = 2;
			consGridBagConstraints71.gridx = 0;
			consGridBagConstraints71.insets = new java.awt.Insets(5,5,0,5);
			consGridBagConstraints101.gridy = 3;
			consGridBagConstraints101.gridx = 0;
			consGridBagConstraints101.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints101.insets = new java.awt.Insets(5,5,0,5);
			constraintsStartBtnPnl.gridx = 0; constraintsStartBtnPnl.gridy = 5;
			constraintsStartBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartBtnPnl.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsStartBtnPnl.weightx = 1.0;
			constraintsStartBtnPnl.weighty = 1.0;
			constraintsStartBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			startCP.add(getScreenPnl(), constraintsScreenPnl);
			startCP.add(getStartBtnPnl(), constraintsStartBtnPnl);
			startCP.add(getStartPwdPnl(), consGridBagConstraints71);
			startCP.add(getStartCallCB(), consGridBagConstraints101);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startCP;
}
/**
 * Return the Start1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getStartDlg() {
	if (startDlg == null) {
		try {
			startDlg = new javax.swing.JDialog(this);
			startDlg.setName("StartDlg");
			startDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			startDlg.setBounds(454, 1842, 339, 263);
			startDlg.setModal(true);
			startDlg.setTitle("Start a meeting");
			getStartDlg().setContentPane(getStartCP());
			startDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowActivated(java.awt.event.WindowEvent e) {    
					try {
						getStartTitleTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startDlg;
}
/**
 * Return the StartMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStartMI() {
	if (startMI == null) {
		try {
			startMI = new javax.swing.JMenuItem();
			startMI.setName("StartMI");
			startMI.setMnemonic('S');
			startMI.setText("Start a meeting");
			startMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK, false));
			startMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doStartMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startMI;
}
/**
 * Return the StartOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getStartOkBtn() {
	if (startOkBtn == null) {
		try {
			startOkBtn = new javax.swing.JButton();
			startOkBtn.setName("StartOkBtn");
			startOkBtn.setText("Start");
			startOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startMeeting();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startOkBtn;
}
/**
 * Return the StopMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStopMI() {
	if (stopMI == null) {
		try {
			stopMI = new javax.swing.JMenuItem();
			stopMI.setName("StopMI");
			stopMI.setText("Stop sharing");
			stopMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.ALT_MASK, false));
			stopMI.setMnemonic(java.awt.event.KeyEvent.VK_S);
			stopMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doStopShare();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return stopMI;
}
/**
 * Return the ModTakeMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getTakeMI() {
	if (takeMI == null) {
		try {
			takeMI = new javax.swing.JMenuItem();
			takeMI.setName("TakeMI");
			takeMI.setMnemonic('T');
			takeMI.setText("Take control");
			takeMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.Event.ALT_MASK, false));
			takeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPassCtrl();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return takeMI;
}
/**
 * Return the TbAttendBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbAttendBtn() {
	if (tbAttendBtn == null) {
		try {
			tbAttendBtn = new javax.swing.JButton();
			tbAttendBtn.setName("TbAttendBtn");
			tbAttendBtn.setToolTipText("Attend a meeting");
			tbAttendBtn.setText("");
			tbAttendBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbAttendBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/attend.gif")));
			tbAttendBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbAttendBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbAttendBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doAttendMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbAttendBtn;
}
/**
 * Return the TbCopyBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbCopyBtn() {
	if (tbCopyBtn == null) {
		try {
			tbCopyBtn = new javax.swing.JButton();
			tbCopyBtn.setName("TbCopyBtn");
			tbCopyBtn.setToolTipText("Copy chat text to clipboard");
			tbCopyBtn.setText("");
			tbCopyBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbCopyBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/copy.gif")));
			tbCopyBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbCopyBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbCopyBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCopy();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbCopyBtn;
}
/**
 * Return the TbCutBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbCutBtn() {
	if (tbCutBtn == null) {
		try {
			tbCutBtn = new javax.swing.JButton();
			tbCutBtn.setName("TbCutBtn");
			tbCutBtn.setToolTipText("Cut chat text to clipboard");
			tbCutBtn.setText("");
			tbCutBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbCutBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/cut.gif")));
			tbCutBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbCutBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbCutBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doCut();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbCutBtn;
}
/**
 * Return the TbEndBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbEndBtn() {
	if (tbEndBtn == null) {
		try {
			tbEndBtn = new javax.swing.JButton();
			tbEndBtn.setName("TbEndBtn");
			tbEndBtn.setToolTipText("End current meeting");
			tbEndBtn.setText("");
			tbEndBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbEndBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/end.gif")));
			tbEndBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbEndBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbEndBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doEndMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbEndBtn;
}
/**
 * Return the TbExitBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbExitBtn() {
	if (tbExitBtn == null) {
		try {
			tbExitBtn = new javax.swing.JButton();
			tbExitBtn.setName("TbExitBtn");
			tbExitBtn.setToolTipText("Exit");
			tbExitBtn.setText("");
			tbExitBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbExitBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/exit.gif")));
			tbExitBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbExitBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbExitBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						exit(false);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbExitBtn;
}
/**
 * Return the ModTbFreezeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbFreezeBtn() {
	if (tbFreezeBtn == null) {
		try {
			tbFreezeBtn = new javax.swing.JButton();
			tbFreezeBtn.setName("TbFreezeBtn");
			tbFreezeBtn.setToolTipText("Freeze screen to telestrate");
			tbFreezeBtn.setText("");
			tbFreezeBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbFreezeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/freeze.gif")));
			tbFreezeBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbFreezeBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbFreezeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doFreeze();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbFreezeBtn;
}
/**
 * Return the TbInviteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbInviteBtn() {
	if (tbInviteBtn == null) {
		try {
			tbInviteBtn = new javax.swing.JButton();
			tbInviteBtn.setName("TbInviteBtn");
			tbInviteBtn.setToolTipText("Send invitations");
			tbInviteBtn.setText("");
			tbInviteBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbInviteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/invite.gif")));
			tbInviteBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbInviteBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbInviteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doInvite();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbInviteBtn;
}
/**
 * Return the TbLeaveBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbLeaveBtn() {
	if (tbLeaveBtn == null) {
		try {
			tbLeaveBtn = new javax.swing.JButton();
			tbLeaveBtn.setName("TbLeaveBtn");
			tbLeaveBtn.setToolTipText("Leave current meeting");
			tbLeaveBtn.setText("");
			tbLeaveBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbLeaveBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/leave.gif")));
			tbLeaveBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbLeaveBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbLeaveBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLeaveMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbLeaveBtn;
}
/**
 * Return the TbLogoffBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbLogoffBtn() {
	if (tbLogoffBtn == null) {
		try {
			tbLogoffBtn = new javax.swing.JButton();
			tbLogoffBtn.setName("TbLogoffBtn");
			tbLogoffBtn.setToolTipText("Logoff");
			tbLogoffBtn.setText("");
			tbLogoffBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbLogoffBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/logoff.gif")));
			tbLogoffBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbLogoffBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbLogoffBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLogout();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbLogoffBtn;
}
/**
 * Return the TbLogonBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbLogonBtn() {
	if (tbLogonBtn == null) {
		try {
			tbLogonBtn = new javax.swing.JButton();
			tbLogonBtn.setName("TbLogonBtn");
			tbLogonBtn.setToolTipText("Logon");
			tbLogonBtn.setText("");
			tbLogonBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbLogonBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/logon.gif")));
			tbLogonBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbLogonBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbLogonBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
			tbLogonBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLogin();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbLogonBtn;
}
/**
 * Return the ModTbTransferBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbPassBtn() {
	if (tbPassBtn == null) {
		try {
			tbPassBtn = new javax.swing.JButton();
			tbPassBtn.setName("TbPassBtn");
			tbPassBtn.setToolTipText("Pass control");
			tbPassBtn.setText("");
			tbPassBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbPassBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/ctrl.gif")));
			tbPassBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbPassBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbPassBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPassCtrl();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbPassBtn;
}
/**
 * Return the TbPasteBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbPasteBtn() {
	if (tbPasteBtn == null) {
		try {
			tbPasteBtn = new javax.swing.JButton();
			tbPasteBtn.setName("TbPasteBtn");
			tbPasteBtn.setToolTipText("Paste from clipboard");
			tbPasteBtn.setText("");
			tbPasteBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbPasteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/paste.gif")));
			tbPasteBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbPasteBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbPasteBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPaste();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbPasteBtn;
}
/**
 * Return the TbResumeBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbResumeBtn() {
	if (tbResumeBtn == null) {
		try {
			tbResumeBtn = new javax.swing.JButton();
			tbResumeBtn.setName("TbResumeBtn");
			tbResumeBtn.setToolTipText("Resume sharing");
			tbResumeBtn.setText("");
			tbResumeBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbResumeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/resume.gif")));
			tbResumeBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbResumeBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbResumeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doResume();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbResumeBtn;
}
/**
 * Return the TbSaveBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbSaveBtn() {
	if (tbSaveBtn == null) {
		try {
			tbSaveBtn = new javax.swing.JButton();
			tbSaveBtn.setName("TbSaveBtn");
			tbSaveBtn.setToolTipText("Save chat text");
			tbSaveBtn.setText("");
			tbSaveBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbSaveBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/save.gif")));
			tbSaveBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbSaveBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbSaveBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doSave();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbSaveBtn;
}
/**
 * Return the TbShDskBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbShDskBtn() {
	if (tbShDskBtn == null) {
		try {
			tbShDskBtn = new javax.swing.JButton();
			tbShDskBtn.setName("TbShDskBtn");
			tbShDskBtn.setToolTipText("Share my desktop");
			tbShDskBtn.setText("");
			tbShDskBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbShDskBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/shdesk.gif")));
			tbShDskBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbShDskBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbShDskBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						shareDesktop();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbShDskBtn;
}
/**
 * Return the TbShWinBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbShWinBtn() {
	if (tbShWinBtn == null) {
		try {
			tbShWinBtn = new javax.swing.JButton();
			tbShWinBtn.setName("TbShWinBtn");
			tbShWinBtn.setToolTipText("Share a window");
			tbShWinBtn.setText("");
			tbShWinBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbShWinBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/shwin.gif")));
			tbShWinBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbShWinBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbShWinBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doShareWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbShWinBtn;
}
/**
 * Return the TbStartBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbStartBtn() {
	if (tbStartBtn == null) {
		try {
			tbStartBtn = new javax.swing.JButton();
			tbStartBtn.setName("TbStartBtn");
			tbStartBtn.setToolTipText("Start a meeting");
			tbStartBtn.setText("");
			tbStartBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbStartBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/start.gif")));
			tbStartBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbStartBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbStartBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doStartMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbStartBtn;
}
/**
 * Return the TbStopBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbStopBtn() {
	if (tbStopBtn == null) {
		try {
			tbStopBtn = new javax.swing.JButton();
			tbStopBtn.setName("TbStopBtn");
			tbStopBtn.setToolTipText("Stop sharing");
			tbStopBtn.setText("");
			tbStopBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbStopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/stop.gif")));
			tbStopBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbStopBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbStopBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doStopShare();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbStopBtn;
}
/**
 * Return the ModTbNoTransferBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTbTakeBtn() {
	if (tbTakeBtn == null) {
		try {
			tbTakeBtn = new javax.swing.JButton();
			tbTakeBtn.setName("TbTakeBtn");
			tbTakeBtn.setToolTipText("Take control");
			tbTakeBtn.setText("");
			tbTakeBtn.setMaximumSize(new java.awt.Dimension(22, 22));
			tbTakeBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/oem/edge/ed/odc/meeting/client/noctrl.gif")));
			tbTakeBtn.setPreferredSize(new java.awt.Dimension(22, 22));
			tbTakeBtn.setMinimumSize(new java.awt.Dimension(22, 22));
			tbTakeBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPassCtrl();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tbTakeBtn;
}
/**
 * Return the TimeMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getTimeMI() {
	if (timeMI == null) {
		try {
			timeMI = new javax.swing.JCheckBoxMenuItem();
			timeMI.setName("TimeMI");
			timeMI.setText("Time updates");
			timeMI.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						doTime();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return timeMI;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStartTitleLbl() {
	if (startTitleLbl == null) {
		try {
			startTitleLbl = new javax.swing.JLabel();
			startTitleLbl.setName("TitleLbl");
			startTitleLbl.setText("Title:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startTitleLbl;
}
/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getStartTitlePnl() {
	if (startTitlePnl == null) {
		try {
			startTitlePnl = new javax.swing.JPanel();
			startTitlePnl.setName("TitlePnl");
			startTitlePnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTitleLbl = new java.awt.GridBagConstraints();
			constraintsTitleLbl.gridx = 0; constraintsTitleLbl.gridy = 0;
			constraintsTitleLbl.insets = new java.awt.Insets(0, 0, 0, 10);
			getStartTitlePnl().add(getStartTitleLbl(), constraintsTitleLbl);

			java.awt.GridBagConstraints constraintsTitleTF = new java.awt.GridBagConstraints();
			constraintsTitleTF.gridx = 1; constraintsTitleTF.gridy = 0;
			constraintsTitleTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTitleTF.weightx = 1.0;
			getStartTitlePnl().add(getStartTitleTF(), constraintsTitleTF);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startTitlePnl;
}
/**
 * Return the TitleTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStartTitleTF() {
	if (startTitleTF == null) {
		try {
			startTitleTF = new javax.swing.JTextField();
			startTitleTF.setName("TitleTF");
			startTitleTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startMeeting();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return startTitleTF;
}
/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getToolbarPnl() {
	if (toolbarPnl == null) {
		try {
			toolbarPnl = new javax.swing.JPanel();
			toolbarPnl.setName("ToolbarPnl");
			toolbarPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTbLogonBtn = new java.awt.GridBagConstraints();
			constraintsTbLogonBtn.gridx = 0; constraintsTbLogonBtn.gridy = 0;
			getToolbarPnl().add(getTbLogonBtn(), constraintsTbLogonBtn);

			java.awt.GridBagConstraints constraintsTbLogoffBtn = new java.awt.GridBagConstraints();
			constraintsTbLogoffBtn.gridx = 1; constraintsTbLogoffBtn.gridy = 0;
			getToolbarPnl().add(getTbLogoffBtn(), constraintsTbLogoffBtn);

			java.awt.GridBagConstraints constraintsTbSaveBtn = new java.awt.GridBagConstraints();
			constraintsTbSaveBtn.gridx = 2; constraintsTbSaveBtn.gridy = 0;
			getToolbarPnl().add(getTbSaveBtn(), constraintsTbSaveBtn);

			java.awt.GridBagConstraints constraintsTbCopyBtn = new java.awt.GridBagConstraints();
			constraintsTbCopyBtn.gridx = 3; constraintsTbCopyBtn.gridy = 0;
			constraintsTbCopyBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbCopyBtn(), constraintsTbCopyBtn);

			java.awt.GridBagConstraints constraintsTbCutBtn = new java.awt.GridBagConstraints();
			constraintsTbCutBtn.gridx = 4; constraintsTbCutBtn.gridy = 0;
			getToolbarPnl().add(getTbCutBtn(), constraintsTbCutBtn);

			java.awt.GridBagConstraints constraintsTbPasteBtn = new java.awt.GridBagConstraints();
			constraintsTbPasteBtn.gridx = 5; constraintsTbPasteBtn.gridy = 0;
			getToolbarPnl().add(getTbPasteBtn(), constraintsTbPasteBtn);

			java.awt.GridBagConstraints constraintsTbAttendBtn = new java.awt.GridBagConstraints();
			constraintsTbAttendBtn.gridx = 6; constraintsTbAttendBtn.gridy = 0;
			constraintsTbAttendBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbAttendBtn(), constraintsTbAttendBtn);

			java.awt.GridBagConstraints constraintsTbStartBtn = new java.awt.GridBagConstraints();
			constraintsTbStartBtn.gridx = 7; constraintsTbStartBtn.gridy = 0;
			getToolbarPnl().add(getTbStartBtn(), constraintsTbStartBtn);

			java.awt.GridBagConstraints constraintsTbLeaveBtn = new java.awt.GridBagConstraints();
			constraintsTbLeaveBtn.gridx = 8; constraintsTbLeaveBtn.gridy = 0;
			constraintsTbLeaveBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbLeaveBtn(), constraintsTbLeaveBtn);

			java.awt.GridBagConstraints constraintsTbEndBtn = new java.awt.GridBagConstraints();
			constraintsTbEndBtn.gridx = 9; constraintsTbEndBtn.gridy = 0;
			getToolbarPnl().add(getTbEndBtn(), constraintsTbEndBtn);

			java.awt.GridBagConstraints constraintsTbInviteBtn = new java.awt.GridBagConstraints();
			constraintsTbInviteBtn.gridx = 10; constraintsTbInviteBtn.gridy = 0;
			constraintsTbInviteBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbInviteBtn(), constraintsTbInviteBtn);

			java.awt.GridBagConstraints constraintsTbShDskBtn = new java.awt.GridBagConstraints();
			constraintsTbShDskBtn.gridx = 11; constraintsTbShDskBtn.gridy = 0;
			constraintsTbShDskBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbShDskBtn(), constraintsTbShDskBtn);

			java.awt.GridBagConstraints constraintsTbShWinBtn = new java.awt.GridBagConstraints();
			constraintsTbShWinBtn.gridx = 12; constraintsTbShWinBtn.gridy = 0;
			getToolbarPnl().add(getTbShWinBtn(), constraintsTbShWinBtn);

			java.awt.GridBagConstraints constraintsTbStopBtn = new java.awt.GridBagConstraints();
			constraintsTbStopBtn.gridx = 13; constraintsTbStopBtn.gridy = 0;
			constraintsTbStopBtn.insets = new java.awt.Insets(0, 3, 0, 20);
			getToolbarPnl().add(getTbStopBtn(), constraintsTbStopBtn);

			java.awt.GridBagConstraints constraintsTbFreezeBtn = new java.awt.GridBagConstraints();
			constraintsTbFreezeBtn.gridx = 14; constraintsTbFreezeBtn.gridy = 0;
			constraintsTbFreezeBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbFreezeBtn(), constraintsTbFreezeBtn);

			java.awt.GridBagConstraints constraintsTbResumeBtn = new java.awt.GridBagConstraints();
			constraintsTbResumeBtn.gridx = 15; constraintsTbResumeBtn.gridy = 0;
			constraintsTbResumeBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbResumeBtn(), constraintsTbResumeBtn);

			java.awt.GridBagConstraints constraintsTbPassBtn = new java.awt.GridBagConstraints();
			constraintsTbPassBtn.gridx = 16; constraintsTbPassBtn.gridy = 0;
			constraintsTbPassBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbPassBtn(), constraintsTbPassBtn);

			java.awt.GridBagConstraints constraintsTbTakeBtn = new java.awt.GridBagConstraints();
			constraintsTbTakeBtn.gridx = 17; constraintsTbTakeBtn.gridy = 0;
			constraintsTbTakeBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbTakeBtn(), constraintsTbTakeBtn);

			java.awt.GridBagConstraints constraintsTbExitBtn = new java.awt.GridBagConstraints();
			constraintsTbExitBtn.gridx = 18; constraintsTbExitBtn.gridy = 0;
			constraintsTbExitBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTbExitBtn.weightx = 1.0;
			constraintsTbExitBtn.insets = new java.awt.Insets(0, 3, 0, 0);
			getToolbarPnl().add(getTbExitBtn(), constraintsTbExitBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return toolbarPnl;
}
/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getTransferBtnPnl() {
	if (transferBtnPnl == null) {
		try {
			transferBtnPnl = new javax.swing.JPanel();
			transferBtnPnl.setName("TransferBtnPnl");
			transferBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTransferOkBtn = new java.awt.GridBagConstraints();
			constraintsTransferOkBtn.gridx = 0; constraintsTransferOkBtn.gridy = 0;
			constraintsTransferOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getTransferBtnPnl().add(getTransferOkBtn(), constraintsTransferOkBtn);

			java.awt.GridBagConstraints constraintsTransferCanBtn = new java.awt.GridBagConstraints();
			constraintsTransferCanBtn.gridx = 1; constraintsTransferCanBtn.gridy = 0;
			constraintsTransferCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getTransferBtnPnl().add(getTransferCanBtn(), constraintsTransferCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferBtnPnl;
}
/**
 * Return the TransferCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTransferCanBtn() {
	if (transferCanBtn == null) {
		try {
			transferCanBtn = new javax.swing.JButton();
			transferCanBtn.setName("TransferCanBtn");
			transferCanBtn.setText("Cancel");
			transferCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						transferCanWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferCanBtn;
}
/**
 * Return the JDialogContentPane3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getTransferCP() {
	if (transferCP == null) {
		try {
			transferCP = new javax.swing.JPanel();
			transferCP.setName("TransferCP");
			transferCP.setLayout(new java.awt.GridBagLayout());
			transferCP.setBounds(48, 1503, 205, 241);

			java.awt.GridBagConstraints constraintsTransferLbl = new java.awt.GridBagConstraints();
			constraintsTransferLbl.gridx = 0; constraintsTransferLbl.gridy = 0;
			constraintsTransferLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTransferLbl.insets = new java.awt.Insets(5, 5, 2, 5);
			java.awt.GridBagConstraints constraintsTransferSP = new java.awt.GridBagConstraints();
			constraintsTransferSP.gridx = 0; constraintsTransferSP.gridy = 1;
			constraintsTransferSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTransferSP.weightx = 1.0;
			constraintsTransferSP.weighty = 1.0;
			constraintsTransferSP.insets = new java.awt.Insets(0, 5, 0, 5);
			java.awt.GridBagConstraints constraintsTransferBtnPnl = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints111 = new java.awt.GridBagConstraints();
			consGridBagConstraints111.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints111.weighty = 0.0D;
			consGridBagConstraints111.weightx = 1.0;
			consGridBagConstraints111.gridy = 2;
			consGridBagConstraints111.gridx = 0;
			constraintsTransferLbl.gridwidth = 0;
			constraintsTransferSP.gridwidth = 0;
			constraintsTransferBtnPnl.gridx = 0; constraintsTransferBtnPnl.gridy = 3;
			constraintsTransferBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTransferBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			constraintsTransferBtnPnl.gridwidth = 0;
			consGridBagConstraints111.insets = new java.awt.Insets(5,5,0,5);
			transferCP.add(getTransferLbl(), constraintsTransferLbl);
			transferCP.add(getTransferSP(), constraintsTransferSP);
			transferCP.add(getTransferBtnPnl(), constraintsTransferBtnPnl);
			transferCP.add(getTransferDurPnl(), consGridBagConstraints111);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferCP;
}
/**
 * Return the Transfer1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getTransferDlg() {
	if (transferDlg == null) {
		try {
			transferDlg = new javax.swing.JDialog(this);
			transferDlg.setName("TransferDlg");
			transferDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			transferDlg.setBounds(27, 1484, 253, 241);
			transferDlg.setModal(true);
			transferDlg.setTitle("Transfer Control");
			getTransferDlg().setContentPane(getTransferCP());
			transferDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowClosing(java.awt.event.WindowEvent e) {    
					try {
						transferCanWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferDlg;
}
/**
 * Return the TransferLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getTransferLB() {
	if (transferLB == null) {
		try {
			transferLB = new javax.swing.JList();
			transferLB.setName("TransferLB");
			transferLB.setBounds(0, 0, 160, 120);
			transferLB.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			transferLB.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					try {
						transferListChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferLB;
}
/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getTransferLbl() {
	if (transferLbl == null) {
		try {
			transferLbl = new javax.swing.JLabel();
			transferLbl.setName("TransferLbl");
			transferLbl.setText("Select user to receive control:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferLbl;
}
/**
 * Return the TransferLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getTransferLM() {
	if (transferLM == null) {
		try {
			transferLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferLM;
}
/**
 * Return the TransferOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTransferOkBtn() {
	if (transferOkBtn == null) {
		try {
			transferOkBtn = new javax.swing.JButton();
			transferOkBtn.setName("TransferOkBtn");
			transferOkBtn.setText("Transfer");
			transferOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						transferControl();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferOkBtn;
}
/**
 * Return the TransferSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getTransferSP() {
	if (transferSP == null) {
		try {
			transferSP = new javax.swing.JScrollPane();
			transferSP.setName("TransferSP");
			getTransferSP().setViewportView(getTransferLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return transferSP;
}
/**
 * Return the TunnelMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getTunnelMI() {
	if (tunnelMI == null) {
		try {
			tunnelMI = new javax.swing.JMenuItem();
			tunnelMI.setName("TunnelMI");
			tunnelMI.setText("Show Tunnel Window");
			tunnelMI.setMnemonic(java.awt.event.KeyEvent.VK_T);
			tunnelMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					System.out.println("tunnel command: show");
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return tunnelMI;
}
/**
 * Return the ViewEndMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getViewEndMI() {
	if (viewEndMI == null) {
		try {
			viewEndMI = new javax.swing.JMenuItem();
			viewEndMI.setName("ViewEndMI");
			viewEndMI.setMnemonic('E');
			viewEndMI.setText("End this meeting");
			viewEndMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK, false));
			viewEndMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doEndMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return viewEndMI;
}
/**
 * Return the LeaveMI1 property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getViewLeaveMI() {
	if (viewLeaveMI == null) {
		try {
			viewLeaveMI = new javax.swing.JMenuItem();
			viewLeaveMI.setName("ViewLeaveMI");
			viewLeaveMI.setMnemonic('L');
			viewLeaveMI.setText("Leave this meeting");
			viewLeaveMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.Event.CTRL_MASK, false));
			viewLeaveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doLeaveMtg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return viewLeaveMI;
}
/**
 * Return the ViewM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getViewM() {
	if (viewM == null) {
		try {
			viewM = new javax.swing.JMenu();
			viewM.setName("ViewM");
			viewM.setText("View");
			viewM.add(getFitImageMI());
			viewM.add(getResizeImageMI());
			viewM.add(getViewSep());
			viewM.add(getViewLeaveMI());
			viewM.add(getViewEndMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return viewM;
}
/**
 * Return the JPanel111 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getWindowBtnPnl() {
	if (windowBtnPnl == null) {
		try {
			windowBtnPnl = new javax.swing.JPanel();
			windowBtnPnl.setName("WindowBtnPnl");
			windowBtnPnl.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsWindowOkBtn = new java.awt.GridBagConstraints();
			constraintsWindowOkBtn.gridx = 0; constraintsWindowOkBtn.gridy = 0;
			constraintsWindowOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getWindowBtnPnl().add(getWindowOkBtn(), constraintsWindowOkBtn);

			java.awt.GridBagConstraints constraintsWindowCanBtn = new java.awt.GridBagConstraints();
			constraintsWindowCanBtn.gridx = 1; constraintsWindowCanBtn.gridy = 0;
			constraintsWindowCanBtn.insets = new java.awt.Insets(0, 5, 0, 5);
			getWindowBtnPnl().add(getWindowCanBtn(), constraintsWindowCanBtn);

			java.awt.GridBagConstraints constraintsWindowPickBtn = new java.awt.GridBagConstraints();
			constraintsWindowPickBtn.gridx = 2; constraintsWindowPickBtn.gridy = 0;
			constraintsWindowPickBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getWindowBtnPnl().add(getWindowPickBtn(), constraintsWindowPickBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowBtnPnl;
}
/**
 * Return the WindowCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getWindowCanBtn() {
	if (windowCanBtn == null) {
		try {
			windowCanBtn = new javax.swing.JButton();
			windowCanBtn.setName("WindowCanBtn");
			windowCanBtn.setText("Cancel");
			windowCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						shareCanWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowCanBtn;
}
/**
 * Return the JDialogContentPane31 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getWindowCP() {
	if (windowCP == null) {
		try {
			windowCP = new javax.swing.JPanel();
			windowCP.setName("WindowCP");
			windowCP.setLayout(new java.awt.GridBagLayout());
			windowCP.setBounds(457, 1503, 320, 241);

			java.awt.GridBagConstraints constraintsWindowLbl = new java.awt.GridBagConstraints();
			constraintsWindowLbl.gridx = 0; constraintsWindowLbl.gridy = 0;
			constraintsWindowLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWindowLbl.insets = new java.awt.Insets(5, 5, 2, 5);
			getWindowCP().add(getWindowLbl(), constraintsWindowLbl);

			java.awt.GridBagConstraints constraintsWindowSP = new java.awt.GridBagConstraints();
			constraintsWindowSP.gridx = 0; constraintsWindowSP.gridy = 1;
			constraintsWindowSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsWindowSP.weightx = 1.0;
			constraintsWindowSP.weighty = 1.0;
			constraintsWindowSP.insets = new java.awt.Insets(0, 5, 0, 5);
			getWindowCP().add(getWindowSP(), constraintsWindowSP);

			java.awt.GridBagConstraints constraintsWindowBtnPnl = new java.awt.GridBagConstraints();
			constraintsWindowBtnPnl.gridx = 0; constraintsWindowBtnPnl.gridy = 2;
			constraintsWindowBtnPnl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsWindowBtnPnl.insets = new java.awt.Insets(5, 5, 5, 5);
			getWindowCP().add(getWindowBtnPnl(), constraintsWindowBtnPnl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowCP;
}
/**
 * Return the Window1Dlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getWindowDlg() {
	if (windowDlg == null) {
		try {
			windowDlg = new javax.swing.JDialog(this);
			windowDlg.setName("WindowDlg");
			windowDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			windowDlg.setBounds(455, 1485, 320, 241);
			windowDlg.setModal(true);
			windowDlg.setTitle("Share a window");
			getWindowDlg().setContentPane(getWindowCP());
			windowDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowClosing(java.awt.event.WindowEvent e) {    
					try {
						shareCanWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowDlg;
}
/**
 * Return the WindowLB property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getWindowLB() {
	if (windowLB == null) {
		try {
			windowLB = new javax.swing.JList();
			windowLB.setName("WindowLB");
			windowLB.setBounds(0, 0, 160, 120);
			windowLB.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			windowLB.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					try {
						windowListChg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowLB;
}
/**
 * Return the JLabel111 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getWindowLbl() {
	if (windowLbl == null) {
		try {
			windowLbl = new javax.swing.JLabel();
			windowLbl.setName("WindowLbl");
			windowLbl.setText("Select the window to share:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowLbl;
}
/**
 * Return the WindowLM property value.
 * @return javax.swing.DefaultListModel
 */
private javax.swing.DefaultListModel getWindowLM() {
	if (windowLM == null) {
		try {
			windowLM = new javax.swing.DefaultListModel();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowLM;
}
/**
 * Return the WindowMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getWindowMI() {
	if (windowMI == null) {
		try {
			windowMI = new javax.swing.JMenuItem();
			windowMI.setName("WindowMI");
			windowMI.setMnemonic(java.awt.event.KeyEvent.VK_W);
			windowMI.setText("Share a window...");
			windowMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.Event.ALT_MASK, false));
			windowMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doShareWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowMI;
}
/**
 * Return the WindowOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getWindowOkBtn() {
	if (windowOkBtn == null) {
		try {
			windowOkBtn = new javax.swing.JButton();
			windowOkBtn.setName("WindowOkBtn");
			windowOkBtn.setText("OK");
			windowOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						shareSelectWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowOkBtn;
}
/**
 * Return the WindowPickBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getWindowPickBtn() {
	if (windowPickBtn == null) {
		try {
			windowPickBtn = new javax.swing.JButton();
			windowPickBtn.setName("WindowPickBtn");
			windowPickBtn.setText("Pick window");
			windowPickBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						sharePointWindow();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowPickBtn;
}
/**
 * Return the WindowSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getWindowSP() {
	if (windowSP == null) {
		try {
			windowSP = new javax.swing.JScrollPane();
			windowSP.setName("WindowSP");
			getWindowSP().setViewportView(getWindowLB());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return windowSP;
}
/**
 * Insert the method's description here.
 * Creation date: (12/1/2004 2:31:09 PM)
 * @param e oem.edge.ed.odc.meeting.client.GroupEvent
 */
public void groupEvent(GroupEvent e) {
	if (e.isQueryGroups() || e.isQueryGroupsFailed()) {
		if (e.handle == (byte) 2) {
			groups.removeAllElements();

			// Got groups? if so, load up the from group list.
			if (e.isQueryGroups()) {
				Vector grps = e.vectorData;
				for (int i = 0; i < grps.size(); i++) {
					GroupInfo gInfo = (GroupInfo) grps.elementAt(i);
					groups.addElement(gInfo.getGroupName());
				}
			}
			// No groups, error.
			else {
				SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Query Groups Failed"));
			}

			// QueryGroups from doInvite. Call doInviteShow.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					doInviteShow();
				}
			});
		}
		else if (e.handle == (byte) 3) {
			if (e.isQueryGroups()) {
				// Should have just 1 group.
				GroupInfo gInfo = (GroupInfo) e.vectorData.elementAt(0);

				getGrpQueryNameLbl().setText(gInfo.getGroupName());
				getGrpQueryOwnerLbl().setText(gInfo.getGroupOwner());
				getGrpQueryCompanyLbl().setText(gInfo.getGroupCompany());

				if (gInfo.getGroupMembersValid()) {
					Enumeration members = gInfo.getGroupMembers().elements();
					while (members.hasMoreElements()) {
						BuddyMgr.addNameToList((String) members.nextElement(),getGrpQueryLM());
					}
				}
			}
			else {
				getGrpQueryNameLbl().setText("No information available");
				getGrpQueryOwnerLbl().setText("No information available");
				getGrpQueryCompanyLbl().setText("No information available");
			}

			getGrpQueryDlg().setTitle("Group Information");
			getGrpQueryDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			getGrpQueryDlg().getGlassPane().setVisible(false);
		}
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Comment
 */
public void imageRealized() {
	imageRealized = true;
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("MeetingViewer2");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Web Conference");
		setSize(442, 697);
		setJMenuBar(getMeetingViewer2JMenuBar());
		setContentPane(getMeetingViewerCP());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowClosing(java.awt.event.WindowEvent e) {    
				try {
					exit(false);
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			} 
			public void windowActivated(java.awt.event.WindowEvent e) {    
				try {
					adjustPaste();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		getInviteLB().setModel(getInviteLM());
		getAttendLB().setModel(getAttendLM());
		getTransferLB().setModel(getTransferLM());
		getWindowLB().setModel(getWindowLM());
		getGrpQueryLB().setModel(getGrpQueryLM());
		getImagePnl().setPresencePanel(getPresencePnl());
		getImagePnl().setParentFrame(getImageFrame());

		// Register for document events on the various text fields.
		getInviteUserTF().getDocument().addDocumentListener(this);
		getLoginUserTF().getDocument().addDocumentListener(this);
		getLoginPwdTF().getDocument().addDocumentListener(this);
		getStartTitleTF().getDocument().addDocumentListener(this);
		getSecOtherTF().getDocument().addDocumentListener(this);
		getScreenTF().getDocument().addDocumentListener(this);

		// Setup a MouseAdapter to handle double clicks on the various lists.
		MouseAdapter ma = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					if (e.getSource() == getAttendLB())
						attendMeeting();
					else if (e.getSource() == getTransferLB())
						transferControl();
					else if (e.getSource() == getWindowLB())
						shareSelectWindow();
					else if (e.getSource() == getInviteLB())
						invite(new ActionEvent(e.getSource(),0,"list"));
				}
			}
		};
		getAttendLB().addMouseListener(ma);
		getTransferLB().addMouseListener(ma);
		getWindowLB().addMouseListener(ma);
		getInviteLB().addMouseListener(ma);

		// Create the button group for the security items.
		ButtonGroup bg = new ButtonGroup();
		bg.add(getSecConfCB());
		bg.add(getSecUnclassCB());
		bg.add(getSecOtherCB());

		// Create the button group for the key menu items
		bg = new ButtonGroup();
		bg.add(getKeyNormalMI());
		bg.add(getKeyRefineMI());
		bg.add(getKeyStrictMI());

		// Register with the presence panel for its menu events.
		getPresencePnl().addMenuChangeListener(this);

		// Get the BuddyMgr to the ManageGroups object.
		getManageGroups().setBuddyMgr(buddyMgr);

		// Setup the cell renderer for the invite list.
		BuddyListCellRenderer br = new BuddyListCellRenderer();
		getInviteLB().setCellRenderer(br);

		// Setup keyboard action for ctrl-shift-D.
		getMeetingViewerCP().registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doDebugMenu();
				}
			},KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH,KeyEvent.CTRL_MASK,false),JComponent.WHEN_IN_FOCUSED_WINDOW);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 4:25:52 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void insertUpdate(DocumentEvent e) {
	if (e.getDocument() == getLoginUserTF().getDocument() ||
		e.getDocument() == getLoginPwdTF().getDocument()) {
		textChgLogin();
	}
	else if (e.getDocument() == getStartTitleTF().getDocument() ||
			e.getDocument() == getSecOtherTF().getDocument() ||
			e.getDocument() == getScreenTF().getDocument()) {
		textChgStart();
	}
	else if (e.getDocument() == getInviteUserTF().getDocument()) {
		textChgUserInvite();
	}
}
/**
 * Comment
 */
public void invite(ActionEvent e) {
	// User pressed enter in text field, but no text, ignore.
	if (e.getSource() == getInviteUserTF() && ! getInviteUserOkBtn().isEnabled()) {
		return;
	}

	// Came from text field invite button?
	if (e.getSource() == getInviteUserOkBtn() || e.getSource() == getInviteUserTF()) {
		String text = getInviteUserTF().getText().trim();
		StringTokenizer list = new StringTokenizer(text," ,\t\r\n\f");
		
		while (list.hasMoreTokens()) {
			String name = list.nextToken();

			if (! users.contains(name)) {
				users.addElement(name);
			}

			Buddy buddy = new Buddy(name);
			addNameToList(buddy,getInviteLM());

			DSMPBaseProto p = DSMPGenerator.createInvitation((byte) 0,dispatcher.meetingID,DSMPGenerator.STATUS_NONE,false,name);
			dispatcher.dispatchProtocol(p);
		}

		getInviteUserTF().setText("");
		getInviteUserOkBtn().setEnabled(false);

		saveBuddyList();
	}

	// Came from list button or double click on list.
	else {
		Object[] invitees = getInviteLB().getSelectedValues();

		for (int i = 0; i < invitees.length; i++) {
			Buddy buddy = (Buddy) invitees[i];

			DSMPBaseProto p;

			if (buddy.type == Buddy.USER) {
				p = DSMPGenerator.createInvitation((byte) 0,dispatcher.meetingID,DSMPGenerator.STATUS_NONE,false,buddy.name);
			}
			else if (buddy.type == Buddy.GROUP) {
				p = DSMPGenerator.createInvitation((byte) 0,dispatcher.meetingID,DSMPGenerator.STATUS_GROUP,false,buddy.name);
			}
			else {
				p = DSMPGenerator.createInvitation((byte) 0,dispatcher.meetingID,DSMPGenerator.STATUS_PROJECT,false,buddy.name);
			}

			dispatcher.dispatchProtocol(p);
		}

		getInviteLB().clearSelection();
		getInviteOkBtn().setEnabled(false);
	}
}
/**
 * Comment
 */
public void inviteDelUser() {
	Object[] objs = getInviteLB().getSelectedValues();

	for (int i = 0; i < objs.length; i++) {
		Buddy b = (Buddy) objs[i];
		getInviteLM().removeElement(b);
		users.removeElement(b.name);
	}

	getInviteLB().clearSelection();

	// Update gui elements
	textChgUserInvite();
	getInviteUserDelBtn().setEnabled(false);

	saveBuddyList();
}
/**
 * Comment
 */
public void inviteListChg() {
	return;
}
/**
 * Comment
 */
public void inviteListChg(ListSelectionEvent e) {
	Object[] invitees = getInviteLB().getSelectedValues();

	if (invitees.length == 1) {
		Buddy buddy = (Buddy) invitees[0];
		getInviteGroupQryBtn().setEnabled(buddy.type == Buddy.GROUP);
	}
	else {
		getInviteGroupQryBtn().setEnabled(false);
	}

	boolean allUsers = true;
	for (int i = 0; (i < invitees.length) && allUsers; i++) {
		Buddy buddy = (Buddy) invitees[i];
		if (buddy.type != Buddy.USER) {
			allUsers = false;
		}
	}
	getInviteUserDelBtn().setEnabled(allUsers);

	getInviteOkBtn().setEnabled(invitees.length > 0);
}
/**
 * Comment
 */
public void inviteQueryGroup() {
	Buddy group = (Buddy) getInviteLB().getSelectedValue();

	if (group != null) {
		// Show the dialog to indicate we're working on it...
		getGrpQueryNameLbl().setText("");
		getGrpQueryOwnerLbl().setText("");
		getGrpQueryCompanyLbl().setText("");
		getGrpQueryLM().clear();
		getGrpQueryDlg().setTitle("Querying group, please wait...");
		getGrpQueryDlg().getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getGrpQueryDlg().getGlassPane().setVisible(true);
		getGrpQueryDlg().setLocationRelativeTo(getInviteDlg());
		getGrpQueryDlg().setVisible(true);

		DSMPBaseProto p = DSMPGenerator.queryGroups((byte) 3,false,true,false,group.name);
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Comment
 */
public void keyStateChanged() {
	System.out.println("key menu selected");
	if (getKeyNormalMI().isSelected()) {
		getImagePnl().setKeyEventMode(ImagePanel.NORMAL_KEY);
	}
	else if (getKeyRefineMI().isSelected()) {
		getImagePnl().setKeyEventMode(ImagePanel.MIXED_KEY);
	}
	else {
		getImagePnl().setKeyEventMode(ImagePanel.STRICT_KEY);
	}
}
/**
 * Comment
 */
public void login() {
	if (! getLoginOkBtn().isEnabled())
		return;

	try {
		dispatcher = new DSMPDispatcher(serverName,serverPort);
		dispatcher.setDebug(getDebugMI().getState());
	}
	catch (UnknownHostException e) {
		e.printStackTrace();
		System.out.println("Unknown host: " + serverName);
		System.out.println("MeetingViewer host port [display]");
		SwingUtilities.invokeLater(new ErrorRunner(this,"Unknown host: " + serverName,"No connection"));
		return;
	}
	catch (IOException ioe) {
		ioe.printStackTrace();
		System.out.println("Unable to connect to server " + serverName + " at " + serverPort + ".");
		System.out.println("MeetingViewer host port [display]");
		SwingUtilities.invokeLater(new ErrorRunner(this,"Unable to connect to server " + serverName + " at " + serverPort + ".","No connection"));
		return;
	}

	dispatcher.addMeetingListener(this);
	dispatcher.addGroupListener(this);

	//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	getLoginDlg().setVisible(false);
	getLoginMI().setEnabled(false);
	getTbLogonBtn().setEnabled(false);

	DSMPProto p = DSMPGenerator.loginUserPW((byte) 0,getLoginUserTF().getText(),new String(getLoginPwdTF().getPassword()));
	dispatcher.dispatchProtocol(p);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		initDlg = new Frame("Conference");
		initDlg.setLayout(new BorderLayout());
		initLbl = new Label("Initializing...");
		initLbl.setAlignment(Label.CENTER);
		initDlg.add(initLbl,"Center");
		Dimension s = new Dimension(200,100);
		initDlg.pack();
		initDlg.setSize(new java.awt.Dimension(154,114));

		centerWindow(initDlg);

		MeetingViewer aMeetingViewer = new MeetingViewer();
		aMeetingViewer.begin(args);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.awt.Frame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:47:50 PM)
 * @param e MeetingEvent
 */
public void meetingAction(MeetingEvent e) {
	if (e.isLogin()) {
		// Update project list and store login ID.
		projects = e.getProjects();
		dispatcher.loginID = e.loginID;
		dispatcher.user = e.user;
		dispatcher.company = e.company;

		resetToLoggedIn();

		if (meetingID >= 0 || meetingUser != null) {
			autoCall();
		}
		else if (meetingName != null) {
			// Can't start or attend a meeting now.
			getAttendMI().setEnabled(false);
			getCallMI().setEnabled(true);
			getStartMI().setEnabled(false);
			getTbAttendBtn().setEnabled(false);
			getTbStartBtn().setEnabled(false);

			dispatcher.dispatchProtocol(DSMPGenerator.getAllMeetings((byte) 1));
		}
	}
	else if (e.isLoginFailed()) {
		projects = null;
		getLoginMI().setEnabled(true);
		getTbLogonBtn().setEnabled(true);
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Login failed"));
	}
	else if (e.isLogout()) {
		resetToLoggedOut();
	}
	else if (e.isLogoutFailed()) {
		getLogoutMI().setEnabled(true);
		getTbLogoffBtn().setEnabled(true);
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Logout failed"));
	}
	else if (e.isGet()) {
		if (e.handle == (byte) 1) {
			invites = e.getInvites();
			int mIndex = -1;

			if (invites != null && invites.size() > 0) {
				for (int i = 0; i < invites.size() && mIndex == -1; i++) {
					DSMPMeeting m = (DSMPMeeting) invites.elementAt(i);
					if (m.getTitle().equals(meetingName)) {
						mIndex = i;
					}
				}
			}

			if (mIndex != -1) {
				getImagePnl().setDispatcher(dispatcher);
				getPresencePnl().setDispatcher(dispatcher);
				getPresencePnl().setEnablement(2);
				dispatcher.addMessageListener(this);

				DSMPMeeting m = (DSMPMeeting) invites.elementAt(mIndex);
				dispatcher.meetingID = m.getMeetingId();
				dispatcher.inviteID = m.getInviteId();
				dispatcher.dispatchProtocol(DSMPGenerator.joinMeeting((byte) 0,dispatcher.meetingID));
				getMeetingLbl().setText(m.getTitle() + " - may include ["  + m.getClassification() + "] information");
				getImageFrame().setTitle(m.getTitle() + " - " + dispatcher.user + " - may include ["  + m.getClassification() + "] information");
			}
			else {
				// Can start or attend a meeting now.
				getAttendMI().setEnabled(true);
				getCallMI().setEnabled(true);
				getStartMI().setEnabled(true);
				getTbAttendBtn().setEnabled(true);
				getTbStartBtn().setEnabled(true);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(MeetingViewer.this,"The meeting requested is not available.","No invitation available",JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		else if (e.handle == (byte) 2) {
			invites = e.getInvites();
			int mIndex = -1;

			if (invites != null && invites.size() > 0) {
				for (int i = 0; i < invites.size() && mIndex == -1; i++) {
					DSMPMeeting m = (DSMPMeeting) invites.elementAt(i);
					if (m.getMeetingId() == dispatcher.meetingID) {
						mIndex = i;
					}
				}
			}

			if (mIndex != -1) {
				getImagePnl().setDispatcher(dispatcher);
				getPresencePnl().setDispatcher(dispatcher);
				getPresencePnl().setEnablement(2);

				DSMPMeeting m = (DSMPMeeting) invites.elementAt(mIndex);
				dispatcher.inviteID = m.getInviteId();
				dispatcher.dispatchProtocol(DSMPGenerator.joinMeeting((byte) 0,dispatcher.meetingID));
				getMeetingLbl().setText(m.getTitle() + " - may include ["  + m.getClassification() + "] information");
				getImageFrame().setTitle(m.getTitle() + " - " + dispatcher.user + " - may include ["  + m.getClassification() + "] information");
			}
			else {
				// Can start or attend a meeting now.
				getAttendMI().setEnabled(true);
				getCallMI().setEnabled(true);
				getStartMI().setEnabled(true);
				getTbAttendBtn().setEnabled(true);
				getTbStartBtn().setEnabled(true);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(MeetingViewer.this,"The meeting requested is not available.","No invitation available",JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		else {
			invites = e.getInvites();

			if (invites != null && invites.size() > 0) {
				getAttendLM().removeAllElements();
				Enumeration p = invites.elements();
				StringBuffer b = new StringBuffer();
				while (p.hasMoreElements()) {
					DSMPMeeting m = (DSMPMeeting) p.nextElement();
					b.setLength(0);
					b.append(m.getTitle());
					if (m.isProject()) b.append("[P]");
					if (m.isGroup()) b.append("[G]");
					b.append(" (");
					b.append(m.getClassification());
					b.append(") - ");
					b.append(m.getOwner());
					getAttendLM().addElement(b.toString());
				}

				getAttendOkBtn().setEnabled(false);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getAttendDlg().setLocationRelativeTo(MeetingViewer.this);
						getAttendDlg().setVisible(true);
					}
				});
			}
			else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(MeetingViewer.this,"You currently have no meeting invitations.","No invitations available",JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
	}
	else if (e.isGetFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Get invites failed"));
	}
	else if (e.isJoin() || e.isStart()) {
		if (e.isStart()) {
			dispatcher.meetingID = e.meetingID;
			if (dispatcher.callEnabled) {
				DSMPProto p = DSMPGenerator.setMeetingOption((byte) 0,e.meetingID,
													DSMPGenerator.COLDCALL_OPTION,"true");
				dispatcher.dispatchProtocol(p);
			}
		}
		dispatcher.particpantID = e.participantID;
		dispatcher.isOwner = false;
		dispatcher.isModerator = false;

		// Set Menu Items
		getSaveAsMI().setEnabled(true);

		getCutMI().setEnabled(false);
		getCopyMI().setEnabled(false);
		getPasteMI().setEnabled(canPaste);
		getClearMI().setEnabled(true);

		getLeaveMI().setEnabled(true);
		getViewLeaveMI().setEnabled(true);
		getEndMI().setEnabled(false);
		getViewEndMI().setEnabled(false);
		getInviteMI().setEnabled(false);
		getKeyM().setEnabled(false);

		// Set the right toolbar features visible.
		getTbAttendBtn().setVisible(false);
		getTbStartBtn().setVisible(false);
		getTbLeaveBtn().setVisible(true);
		getTbEndBtn().setVisible(true);
		getToolbarPnl().doLayout();

		// Set the right toolbar features enabled.
		getTbSaveBtn().setEnabled(true);
		getTbCopyBtn().setEnabled(false);
		getTbCutBtn().setEnabled(false);
		getTbPasteBtn().setEnabled(canPaste);
		getTbLeaveBtn().setEnabled(true);
		getTbEndBtn().setEnabled(false);

		// Set window features.
		getMsgTA().setEnabled(true);
		getMsgTF().setEnabled(true);
		getMsgSendBtn().setEnabled(true);
		editTarget = getMsgTF();

		// Set the GUI state.
		synchronized (mvState) {
			mvState.state = MeetingViewerState.READY;
		}
	}
	else if (e.isJoinFailed()) {
		resetToLoggedIn();

		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Join meeting failed"));
	}
	else if (e.isLeave() || e.isForcedLeave()) {
		if (e.isForcedLeave() && leaving)
			return;

		resetToLoggedIn();

		if (e.isForcedLeave())
			SwingUtilities.invokeLater(new ErrorRunner(this,"The moderator has removed you from the meeting.","Meeting closed"));

		leaving = false;
	}
	else if (e.isLeaveFailed()) {
		getLeaveMI().setEnabled(true);
		getViewLeaveMI().setEnabled(true);
		getTbLeaveBtn().setEnabled(true);
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Leave meeting failed"));
		leaving = false;
	}
	else if (e.isStartFailed()) {
		resetToLoggedIn();

		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Start meeting failed"));
	}
	else if (e.isEnd()) {
		resetToLoggedIn();
	}
	else if (e.isEndFailed()) {
		getEndMI().setEnabled(true);
		getViewEndMI().setEnabled(true);
		getInviteMI().setEnabled(true);
		getTbEndBtn().setEnabled(true);
		getTbInviteBtn().setEnabled(true);

		if (bscraper != null && bscraper.scrapingEnabled()) {
			getDesktopMI().setEnabled(true);
			getWindowMI().setEnabled(true);
			getTbShDskBtn().setEnabled(true);
			getTbShWinBtn().setEnabled(true);
		}
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Leave meeting failed"));
	}
	else if (e.isURL()) {
		final String url = e.url;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getShowUrlTA().setText(url);
				getShowUrlTA().setSelectionStart(0);
				getShowUrlTA().setSelectionEnd(url.length());
				
				getShowUrlDlg().setLocationRelativeTo(MeetingViewer.this);
				getShowUrlDlg().show();
			}
		});
	}
	else if (e.isURLFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Get URL failed"));
	}
	else if (e.isPlaceCallFailed()) {
		// Our call to an owner failed...
		synchronized(mvState) {
			if (mvState.state == MeetingViewerState.CALL_WINDOW) {
				resetToLoggedIn();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getCallingDlg().dispose();
						JOptionPane.showMessageDialog(MeetingViewer.this,"Call could not be completed.","Error",JOptionPane.ERROR_MESSAGE);
					}
				});
				mvState.state = MeetingViewerState.READY;
			}
		}
	}
	else if (e.isPlaceCallEvent()) {
		// Someone is calling us...
		synchronized(callQueue) {
			// Queue the caller.
			callQueue.addElement(e);
			
			// If this is the only caller, launch the dialog. If not,
			// the dialog will relaunch itself with the next caller.
			if (callQueue.size() == 1) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MeetingEvent e = (MeetingEvent) callQueue.elementAt(0);
						getCallerUserLbl().setText(e.user);
						getCallerCompanyLbl().setText(e.company);
						getCallerQueueLbl().setText("");
						getCallerDlg().setLocationRelativeTo(MeetingViewer.this);
						getCallerDlg().show();
					}
				});
			}
			else {
				getCallerQueueLbl().setText((callQueue.size()-1) + " callers in the queue");
			}
		}
	}
	else if (e.isAcceptCall()) {
		// Call was accepted...
		synchronized(callQueue) {
			// Remove caller from queue and display next caller, if any.
			callQueue.removeElementAt(0);
			if (callQueue.size() > 0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MeetingEvent e = (MeetingEvent) callQueue.elementAt(0);
						getCallerUserLbl().setText(e.user);
						getCallerCompanyLbl().setText(e.company);
						if (callQueue.size() == 1) {
							getCallerQueueLbl().setText("");
						}
						else {
							getCallerQueueLbl().setText((callQueue.size()-1) + " callers in the queue");
						}
						getCallerDlg().setLocationRelativeTo(MeetingViewer.this);
						getCallerDlg().show();
					}
				});
			}
		}
	}
	else if (e.isAcceptCallFailed()) {
		// Our accept of the call failed...
		synchronized(callQueue) {
			try {
				SwingUtilities.invokeAndWait(new ErrorRunner(this,e.message,"Accept call failed"));
			}
			catch (Exception ex) {
			}

			// Remove caller from queue and display next caller, if any.
			callQueue.removeElementAt(0);
			if (callQueue.size() > 0) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MeetingEvent e = (MeetingEvent) callQueue.elementAt(0);
						getCallerUserLbl().setText(e.user);
						getCallerCompanyLbl().setText(e.company);
						if (callQueue.size() == 1) {
							getCallerQueueLbl().setText("");
						}
						else {
							getCallerQueueLbl().setText((callQueue.size()-1) + " callers in the queue");
						}
						getCallerDlg().setLocationRelativeTo(MeetingViewer.this);
						getCallerDlg().show();
					}
				});
			}
		}
	}
	else if (e.isAcceptCallEvent()) {
		// Our call was successfully accepted by the owner
		synchronized(mvState) {
			if (mvState.state == MeetingViewerState.CALL_WINDOW) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						getCallingDlg().dispose();
					}
				});
				mvState.state = MeetingViewerState.READY;
			}
		}
		dispatcher.meetingID = e.meetingID;
		dispatcher.dispatchProtocol(DSMPGenerator.getAllMeetings((byte) 2));
	}
	else if (e.isOptions()) {
		if (e.handle == (byte) 1) {
			callQueue.removeAllElements();
		}
	}
	else if (e.isOptionsFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Save option failed"));
		if (e.handle == (byte) 1) {
			callQueue.removeAllElements();
		}
	}
	else if (e.isOptionsEvent()) {
		if (e.key.equals(DSMPGenerator.MEETINGPASSWORD_OPTION)) {
			dispatcher.password = e.sdata;
		}
		else if (e.key.equals(DSMPGenerator.COLDCALL_OPTION)) {
			dispatcher.callEnabled = e.sdata.equalsIgnoreCase("true");
		}
	}
	else if (e.isInviteFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Invite failed"));
	}
	else if (e.isChatFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Chat failed"));
	}
	else if (e.isControlEvent()) {
		// May need to synchronize on mvState and check for CONTROL_PASSING state.

		// Control has been transfered.

		// Only need to handle this if we are moderating the meeting and we are sharing.
		if (dispatcher.isModerator && dispatcher.isSharing) {
			// Control is back to me?
			if (e.participantID == dispatcher.particpantID) {
				incontrol = true;

				getPassMI().setEnabled(true);
				getTakeMI().setEnabled(false);
				getTbPassBtn().setVisible(true);
				getTbPassBtn().setEnabled(true);
				getTbTakeBtn().setVisible(false);
				getToolbarPnl().doLayout();

				// If we timed out, ask if ok to renew control.
				if (timer != null && timerPopped && renewQuestion()) {
					// Don't want the user pressing the pass control button.
					getPassMI().setEnabled(false);
					getTbPassBtn().setEnabled(false);

					// Force the transfer of control now.
					int i = getTransferLB().getSelectedIndex();
					Integer pid = (Integer) passKeys.elementAt(i);
					DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,true,dispatcher.meetingID,pid.intValue());
					dispatcher.dispatchProtocol(p);

					// Start the timer which will take control away from the user.
					timer = new Thread(this);
					timer.start();
				}
				else {
					Thread oldtimer = timer;
					timer = null;
					if (oldtimer != null) {
						oldtimer.interrupt();
					}
				}
			}
			// Control is to someone else.
			else {
				incontrol = false;
				
				getPassMI().setEnabled(false);
				getTakeMI().setEnabled(true);
				getTbPassBtn().setVisible(false);
				getTbTakeBtn().setVisible(true);
				getTbTakeBtn().setEnabled(true);
				getToolbarPnl().doLayout();
			}
		}
	}
	else if (e.isControl()) {
	}
	else if (e.isControlFailed()) {
		// May need to synchronize on mvState and check for CONTROL_PASSING state.

		// Need to confirm the transfer?
		if ((e.flags & 0x02) != 0) {
			// Confirm the transfer...
			if (confirmTransfer()) {
				// Force the transfer of control now.
				int i = getTransferLB().getSelectedIndex();
				Integer pid = (Integer) passKeys.elementAt(i);
				DSMPProto p = DSMPGenerator.modifyControl((byte) 0,true,true,dispatcher.meetingID,pid.intValue());
				dispatcher.dispatchProtocol(p);

				// Start the timer which will take control away from the user.
				timer = new Thread(this);
				timer.start();
			}
			else {
				// Reset gui elements.
				if (incontrol) {
					getTbPassBtn().setEnabled(true);
					getPassMI().setEnabled(true);
				}
				else {
					getTbTakeBtn().setEnabled(true);
					getTakeMI().setEnabled(true);
				}
			}
		}
		else {
			// Swap the transfer and no transfer buttons.
			if (incontrol) {
				getTbPassBtn().setEnabled(true);
				getPassMI().setEnabled(true);

				// Started a time? kill it.
				Thread oldtimer = timer;
				timer = null;
				if (oldtimer != null) {
					oldtimer.interrupt();
				}
			}
			else {
				getTbTakeBtn().setEnabled(true);
				getTakeMI().setEnabled(true);
			}

			SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Control transfer failed"));
		}
	}
	else if (e.isOwnerEvent()) {
		// Ownership change, is it about us?
		if (e.participantID == dispatcher.particpantID) {
			dispatcher.isOwner = e.addOrRemove;

			if (dispatcher.isOwner) {
				// Became an owner!!!

				// Can send invitations.
				getPresencePnl().setEnablement(1);
				getInviteMI().setEnabled(true);
				getTbInviteBtn().setEnabled(true);

				// Can end the meeting.
				getEndMI().setEnabled(true);
				getViewEndMI().setEnabled(true);
				getSettingsMI().setEnabled(true);
				getUrlMI().setEnabled(true);
				getTbEndBtn().setEnabled(true);
			}
			else {
				// Not an owner anymore, back to a lowly participant.
				getPresencePnl().setEnablement(2);
				getInviteMI().setEnabled(false);
				getTbInviteBtn().setEnabled(false);
				getEndMI().setEnabled(false);
				getViewEndMI().setEnabled(false);
				getSettingsMI().setEnabled(false);
				getUrlMI().setEnabled(false);
				getTbEndBtn().setEnabled(false);
			}
		}
	}
	else if (e.isModeratorEvent()) {
		// We are going to become moderator, but we already are, ignore it.
		if (dispatcher.particpantID == e.toPID && dispatcher.isModerator) {
			if (e.participantID != e.toPID) {
				System.out.println("Becoming moderator from someone else, but I am already moderator!");
			}
			return;
		}

		// Moderator change. Are we losing moderator status?
		if (dispatcher.particpantID == e.participantID) {
			dispatcher.isModerator = false;
			// If we are losing moderator, and not getting it back (initial moderator event)
			if (e.participantID != e.toPID)
				SwingUtilities.invokeLater(new ErrorRunner(this,"You are no longer the moderator!","Moderator Changed"));

			// No more sharing.
			getTbShDskBtn().setEnabled(false);
			getTbShWinBtn().setEnabled(false);
			getDesktopMI().setEnabled(false);
			getWindowMI().setEnabled(false);

			// Do we have a scraper?
			if (bscraper != null && bscraper.scrapingEnabled())
				bscraper.disconnect();
		}

		// Are we becoming the moderator?
		if (dispatcher.particpantID == e.toPID) {
			dispatcher.isModerator = true;
			// Alert participant they are the moderator, from and to match on meeting start.
			if (e.participantID != e.toPID)
				SwingUtilities.invokeLater(new ErrorRunner(this,"You are now the moderator!","Moderator Changed"));

			// Fire up a scraper.
			if (bscraper == null)
				bscraper = BScraper.createInstance();

			if (bscraper.scrapingEnabled()) {
				try {
					bscraper.connect(screen,false,false);
				}
				catch (BScraper.PossibleMissingDependencies e1) {
					if (askQuestion("Question",e1.getMessage())) {
						try {
							bscraper.connect(screen,false,true);
						}
						catch (Exception e3) {
							SwingUtilities.invokeLater(new ErrorRunner(this,e3.getMessage(),"Share Error!"));
							return;
						}
					}
					else
						return;
				}
				catch (BScraper.MissingDependencies e2) {
					SwingUtilities.invokeLater(new ErrorRunner(this,e2.getMessage(),"Share Error!"));
					return;
				}

				// Scraper is running, ok to share.

				// Enable toolbar buttons and menu items.
				getTbShDskBtn().setEnabled(true);
				getTbShWinBtn().setEnabled(true);
				getDesktopMI().setEnabled(true);
				getWindowMI().setEnabled(true);

				if (autoShare) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							shareDesktop();
						}
					});
					autoShare = false;
				}
			}
		}
	}
	else if (e.isStartShareFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message,"Start Share Failed"));
	}
	else if (e.isStartShareEvent()) {
		if (! dispatcher.isModerator) {
			getImageFrame().setLocation(5,5);
			getImageFrame().setVisible(true);
		}
	}
	else if (e.isStopShareEvent()) {
		if (dispatcher.isModerator) {
			synchronized (mvState) {
				// If we gave up control, we should have already gotten it back...
				if (! incontrol) {
					System.out.println("MeetingViewer.meetingAction: Ack! StopShareEvent and not in control!!!!");
				}

				// If we are sharing, we need to stop it.
				if (dispatcher.isSharing) {
					stopShare();
				}

				// See if the GUI was doing something we need to stop.
				if (mvState.state == MeetingViewerState.CONTROL_WINDOW) {
					getTransferDlg().setVisible(false);
				}
				else if (mvState.state == MeetingViewerState.QUERY_WINDOW) {
					getWindowDlg().setVisible(false);
				}

				mvState.state = MeetingViewerState.READY;
			}
		}
		else {
			getImageFrame().setVisible(false);
		}
	}
	else if (e.isFreezing()) {
		postMessage(getMsgTA(),"Preparing image, please wait...");
	}
	else if (e.isFreeze()) {
		if (dispatcher.isModerator && dispatcher.isSharing) {
			getFreezeMI().setEnabled(false);
			getResumeMI().setEnabled(true);

			getTbFreezeBtn().setVisible(false);
			getTbFreezeBtn().setEnabled(false);
			getTbResumeBtn().setVisible(true);
			getTbResumeBtn().setEnabled(true);
			getToolbarPnl().doLayout();
		}

		String title = getImageFrame().getTitle();
		if (! title.endsWith(" - frozen"))
			getImageFrame().setTitle(title + " - frozen");
	}
	else if (e.isThaw()) {
		if (dispatcher.isModerator && dispatcher.isSharing) {
			getFreezeMI().setEnabled(true);
			getResumeMI().setEnabled(false);

			getTbFreezeBtn().setVisible(true);
			getTbFreezeBtn().setEnabled(true);
			getTbResumeBtn().setVisible(false);
			getTbResumeBtn().setEnabled(false);
			getToolbarPnl().doLayout();
		}

		String title = getImageFrame().getTitle();
		if (title.endsWith(" - frozen"))
			getImageFrame().setTitle(title.substring(0,title.indexOf(" - frozen")));
	}
	else if (e.isFreezeFailed()) {
		SwingUtilities.invokeLater(new ErrorRunner(this,e.message + ", no telepoint available.","Freeze failed"));
	}
	else if (e.isInviteAction()) {
		doInvite();
	}
	else if (e.isStopSharing()) {
		doStopShare();
	}
	else if (e.isDeath()) {
		resetToLoggedOut();
		if (isTunnel) {
			exit(true);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/2003 11:15:12 AM)
 * @param e oem.edge.ed.odc.dsmp.common.MenuChangeEvent
 */
public void menuChange(MenuChangeEvent e) {
	if (e.isAddMenu()) {
		getMeetingViewer2JMenuBar().add(e.menu);
	}
	else if (e.isRemoveMenu()) {
		getMeetingViewer2JMenuBar().remove(e.menu);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2002 3:59:29 PM)
 * @param e MessageEvent
 */
public void message(MessageEvent e) {
	// Only broadcast messages are ours.
	if (! e.isUnicast()) {
		String msg = getPresencePnl().getDataModel().getUserName(e.fromID) + ": " + e.message;
		postMessage(getMsgTA(),msg);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/25/2003 12:59:06 PM)
 * @param area javax.swing.JTextArea
 * @param msg java.lang.String
 */
public void postMessage(JTextArea area, String msg) {
	int caret = area.getText().length();

	if (caret > 0) {
		area.append("\n");
		caret++;
	}

	area.append(msg);
	area.setCaretPosition(caret);
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/2003 4:25:52 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void removeUpdate(DocumentEvent e) {
	if (e.getDocument() == getLoginUserTF().getDocument() ||
		e.getDocument() == getLoginPwdTF().getDocument()) {
		textChgLogin();
	}
	else if (e.getDocument() == getStartTitleTF().getDocument() ||
			e.getDocument() == getSecOtherTF().getDocument() ||
			e.getDocument() == getScreenTF().getDocument()) {
		textChgStart();
	}
	else if (e.getDocument() == getInviteUserTF().getDocument()) {
		textChgUserInvite();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/2002 8:40:36 PM)
 */
public boolean renewQuestion() {
	String name = (String) getTransferLB().getSelectedValue();
	getQuestionDlg().setTitle("Renew control transfer");
	getQuestionTA().setText("Remote control of this workstation has expired.\n\n" +
		"Press the OK button to transfer control to " + name + " again.");
	getTransferDurationPnl().setVisible(true);
	getQuestionYesBtn().setText("OK");
	getQuestionNoBtn().setText("Cancel");
	getQuestionDlg().validate();
	qna = false;
	try {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				getQuestionDlg().setLocationRelativeTo(MeetingViewer.this);
				getQuestionDlg().setVisible(true);
			}
		});
	}
	catch (Exception e) {
	}
	return qna;
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 11:39:39 AM)
 */
private void resetToLoggedIn() {
	// Reset components if meeting just ended.
	if (bscraper != null) {
		getImagePnl().setBScraper(null);
		if (bscraper.scrapingEnabled())
			bscraper.disconnect();
		bscraper = null;
	}
	if (dispatcher != null) {
		getImagePnl().setDispatcher(null);
		getPresencePnl().setDispatcher(null);
		getManageGroups().setDispatcher(dispatcher);
		getGroupMI().setEnabled(true);
		dispatcher.removeMessageListener(this);

		dispatcher.meetingID = -1;
		dispatcher.inviteID = -1;
		dispatcher.particpantID = -1;
	}
	else {
		// Hmmm... Logged in but no dispatcher? Odd.
		getGroupMI().setEnabled(false);
		getManageGroups().setDispatcher(null);
	}

	if (getImageFrame().isShowing()) {
		getImageFrame().setVisible(false);
	}

	// Setup menu features
	getLoginMI().setEnabled(false);
	getLogoutMI().setEnabled(true);
	getSaveAsMI().setEnabled(false);

	getCutMI().setEnabled(false);
	getCopyMI().setEnabled(false);
	getPasteMI().setEnabled(false);
	getClearMI().setEnabled(false);

	getAttendMI().setEnabled(true);
	getCallMI().setEnabled(true);
	getStartMI().setEnabled(true);
	getLeaveMI().setEnabled(false);
	getViewLeaveMI().setEnabled(false);
	getEndMI().setEnabled(false);
	getViewEndMI().setEnabled(false);
	getInviteMI().setEnabled(false);
	getSettingsMI().setEnabled(false);
	getUrlMI().setEnabled(false);
	getKeyM().setEnabled(false);

	getDesktopMI().setEnabled(false);
	getWindowMI().setEnabled(false);
	getStopMI().setEnabled(false);
	getPassMI().setEnabled(false);
	getTakeMI().setEnabled(false);
	getFreezeMI().setEnabled(false);
	getResumeMI().setEnabled(false);

	// Disable global message features
	editTarget = null;
	getMsgTA().setText("");
	getMsgTA().setEnabled(false);
	getMsgTF().setEnabled(false);
	getMsgSendBtn().setEnabled(false);

	// Set toolbar features visible.
	if (! isTunnel) {
		getTbLogonBtn().setVisible(false);
		getTbLogoffBtn().setVisible(true);
	}

	getTbAttendBtn().setVisible(true);
	getTbStartBtn().setVisible(true);
	getTbLeaveBtn().setVisible(false);
	getTbEndBtn().setVisible(false);
	getTbInviteBtn().setVisible(true);

	getTbShDskBtn().setVisible(true);
	getTbShWinBtn().setVisible(true);
	getTbStopBtn().setVisible(false);
	getTbPassBtn().setVisible(true);
	getTbTakeBtn().setVisible(false);
	getTbFreezeBtn().setVisible(true);
	getTbResumeBtn().setVisible(false);
	getToolbarPnl().doLayout();

	// Set toolbar features enabled.
	getTbLogoffBtn().setEnabled(true);
	getTbSaveBtn().setEnabled(false);
	getTbCopyBtn().setEnabled(false);
	getTbCutBtn().setEnabled(false);
	getTbPasteBtn().setEnabled(false);

	getTbAttendBtn().setEnabled(true);
	getTbStartBtn().setEnabled(true);
	getTbInviteBtn().setEnabled(false);

	getTbShDskBtn().setEnabled(false);
	getTbShWinBtn().setEnabled(false);
	getTbPassBtn().setEnabled(false);
	getTbFreezeBtn().setEnabled(false);

	// Set status labels.
	if (dispatcher.company != null) {
		getConnectedLbl().setText("Connected as " + dispatcher.user + " from " + dispatcher.company);
	}
	else {
		getConnectedLbl().setText("Connected as " + dispatcher.user);
	}
	getMeetingLbl().setText("Not in a meeting.");
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/2002 11:39:39 AM)
 */
private void resetToLoggedOut() {
	// Reset the dispatcher and scraper.
	if (bscraper != null) {
		getImagePnl().setBScraper(null);
		if (bscraper.scrapingEnabled())
			bscraper.disconnect();
		bscraper = null;
	}
	if (dispatcher != null) {
		getPresencePnl().setDispatcher(null);
		getImagePnl().setDispatcher(null);
		getManageGroups().setDispatcher(null);
		dispatcher.removeGroupListener(this);
		dispatcher.removeMeetingListener(this);
		dispatcher = null;
	}

	// Clear out the project list.
	projects = null;

	// Setup menu features
	getLoginMI().setEnabled(true);
	getLogoutMI().setEnabled(false);
	getSaveAsMI().setEnabled(false);
	getGroupMI().setEnabled(false);

	getCutMI().setEnabled(false);
	getCopyMI().setEnabled(false);
	getPasteMI().setEnabled(false);
	getClearMI().setEnabled(false);

	getAttendMI().setEnabled(false);
	getCallMI().setEnabled(false);
	getStartMI().setEnabled(false);
	getLeaveMI().setEnabled(false);
	getViewLeaveMI().setEnabled(false);
	getEndMI().setEnabled(false);
	getViewEndMI().setEnabled(false);
	getInviteMI().setEnabled(false);
	getSettingsMI().setEnabled(false);
	getUrlMI().setEnabled(false);
	getKeyM().setEnabled(false);

	getDesktopMI().setEnabled(false);
	getWindowMI().setEnabled(false);
	getStopMI().setEnabled(false);
	getPassMI().setEnabled(false);
	getTakeMI().setEnabled(false);
	getFreezeMI().setEnabled(false);
	getResumeMI().setEnabled(false);

	// Disable global message features
	editTarget = null;
	getMsgTA().setText("");
	getMsgTA().setEnabled(false);
	getMsgTF().setEnabled(false);
	getMsgSendBtn().setEnabled(false);

	// Set toolbar features visible.
	if (! isTunnel) {
		getTbLogonBtn().setVisible(true);
		getTbLogoffBtn().setVisible(false);
	}

	getTbAttendBtn().setVisible(true);
	getTbStartBtn().setVisible(true);
	getTbLeaveBtn().setVisible(false);
	getTbEndBtn().setVisible(false);
	getTbInviteBtn().setVisible(true);

	getTbShDskBtn().setVisible(true);
	getTbShWinBtn().setVisible(true);
	getTbStopBtn().setVisible(false);
	getTbPassBtn().setVisible(true);
	getTbTakeBtn().setVisible(false);
	getTbFreezeBtn().setVisible(true);
	getTbResumeBtn().setVisible(false);
	getToolbarPnl().doLayout();

	// Set toolbar features enabled.
	getTbLogonBtn().setEnabled(true);
	getTbSaveBtn().setEnabled(false);
	getTbCopyBtn().setEnabled(false);
	getTbCutBtn().setEnabled(false);
	getTbPasteBtn().setEnabled(false);

	getTbAttendBtn().setEnabled(false);
	getTbStartBtn().setEnabled(false);
	getTbInviteBtn().setEnabled(false);

	getTbShDskBtn().setEnabled(false);
	getTbShWinBtn().setEnabled(false);
	getTbPassBtn().setEnabled(false);
	getTbFreezeBtn().setEnabled(false);

	// Set status labels.
	getConnectedLbl().setText("Not connected.");
	getMeetingLbl().setText("Not in a meeting.");
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:24:44 PM)
 */
public void run() {
	// Determine duration.
	Integer i = (Integer) getTransferDurationCB().getSelectedItem();
	long duration = (i == null) ? 5 : i.intValue();
	duration *= 60000;

	// Remember start time.
	timerPopped = false;
	long ct = System.currentTimeMillis();
	long dt = 0;

	// Sleep for 5 minutes.
	while (Thread.currentThread() == timer && dt < duration) {
		try {
			Thread.sleep(duration - dt);
		}
		catch (Exception e) {
		}
		dt = System.currentTimeMillis() - ct;
	}

	// Slept for 5 minutes and we are still timing.
	if (Thread.currentThread() == timer) {
		// Reclaim control.
		timerPopped = true;
		doPassCtrl();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/2003 10:01:03 AM)
 */
public void saveBuddyList() {
	Vector bl = new Vector();
	Enumeration e = users.elements();
	while (e.hasMoreElements()) {
		bl.addElement(e.nextElement());
	}
	buddyMgr.setBuddyList(bl);
}
/**
 * Comment
 */
public void secStateChg() {
	getSecOtherTF().setEnabled(getSecOtherCB().isSelected());
	textChgStart();
}
/**
 * Comment
 */
public void sendMsg(ActionEvent e) {
	String msg = getMsgTF().getText();

	if (msg == null || msg.length() == 0)
		return;

	DSMPProto p = DSMPGenerator.chatBroadcast((byte) 0,dispatcher.meetingID,msg);
	dispatcher.dispatchProtocol(p);
	getMsgTF().setText("");
	msg = getPresencePnl().getDataModel().getUserName(dispatcher.particpantID) + ": " + msg;
	postMessage(getMsgTA(),msg);
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/2004 2:10:00 PM)
 */
public void setDefaultSplit() {
	int x = getMeetingSP().getSize().height;
	x = 5*x/8;
	getMeetingSP().setDividerLocation(x);
}
/**
 * Comment
 */
public void shareCanWindow() {
	synchronized (mvState) {
		mvState.state = MeetingViewerState.READY;
		getWindowDlg().setVisible(false);
	}
}
/**
 * Comment
 */
public void shareDesktop() {
	synchronized (mvState) {
		if (dispatcher.isSharing)
			return;

		if (mvState.state != MeetingViewerState.READY)
			return;

		DSMPProto p = DSMPGenerator.startSharing((byte) 0,dispatcher.meetingID);
		dispatcher.dispatchProtocol(p);

		dispatcher.isSharing = true;

		// Prepare the main window.
		getDesktopMI().setEnabled(false);
		getWindowMI().setEnabled(false);
		getStopMI().setEnabled(true);
		getPassMI().setEnabled(true);
		getFreezeMI().setEnabled(true);

		// Set up the toolbar.
		getTbShDskBtn().setVisible(false);
		getTbShWinBtn().setVisible(false);
		getTbStopBtn().setVisible(true);
		getToolbarPnl().doLayout();
		getTbPassBtn().setEnabled(true);
		getTbFreezeBtn().setEnabled(true);

		// Begin scraping the desktop.
		bscraper.configureToDesktop();
		bscraper.resume();
		getImagePnl().setBScraper(bscraper);

		postMessage(getMsgTA(),"Sharing desktop");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2002 11:36:07 AM)
 * @param e java.awt.event.ActionEvent
 */
public void sharePointWindow() {
	synchronized (mvState) {
		if (mvState.state != MeetingViewerState.QUERY_WINDOW)
			return;

		mvState.state = MeetingViewerState.READY;

		// Hide the window dialog and the main window.
		getWindowDlg().setVisible(false);
		setVisible(false);

		// Let the user point to the window with the mouse.
		int i = bscraper.selectWindow();

		// User didn't pick a window? Reshow window dialog and main window.
		if (i == 0) {
			SwingUtilities.invokeLater(new ErrorRunner(this,"Window could not be identified.","Invalid window"));
			setVisible(true);
			getWindowDlg().setVisible(true);
			return;
		}

		setVisible(true);

		DSMPProto p = DSMPGenerator.startSharing((byte) 0,dispatcher.meetingID);
		dispatcher.dispatchProtocol(p);

		dispatcher.isSharing = true;

		// Prepare the main window.
		getDesktopMI().setEnabled(false);
		getWindowMI().setEnabled(false);
		getStopMI().setEnabled(true);
		getPassMI().setEnabled(true);
		getFreezeMI().setEnabled(true);

		getTbShDskBtn().setVisible(false);
		getTbShWinBtn().setVisible(false);
		getTbStopBtn().setVisible(true);
		getToolbarPnl().doLayout();
		getTbPassBtn().setEnabled(true);
		getTbFreezeBtn().setEnabled(true);

		// Begin scraping the selected window or the desktop, if picked.
		if (bscraper.getDesktopWindow() == i) {
			bscraper.configureToDesktop();
			postMessage(getMsgTA(),"Sharing desktop");
		}
		else {
			bscraper.configureToWindow(i);
			postMessage(getMsgTA(),"Sharing " + bscraper.getWindowTitle(i));
		}
		bscraper.resume();
		getImagePnl().setBScraper(bscraper);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/2002 11:36:07 AM)
 * @param e java.awt.event.ActionEvent
 */
public void shareSelectWindow() {
	synchronized (mvState) {
		if (mvState.state != MeetingViewerState.QUERY_WINDOW)
			return;

		mvState.state = MeetingViewerState.READY;

		DSMPProto p = DSMPGenerator.startSharing((byte) 0,dispatcher.meetingID);
		dispatcher.dispatchProtocol(p);

		dispatcher.isSharing = true;

		// Hide the window dialog.
		getWindowDlg().setVisible(false);

		// Prepare the main window.
		getDesktopMI().setEnabled(false);
		getWindowMI().setEnabled(false);
		getStopMI().setEnabled(true);
		getPassMI().setEnabled(true);
		getFreezeMI().setEnabled(true);

		getTbShDskBtn().setVisible(false);
		getTbShWinBtn().setVisible(false);
		getTbStopBtn().setVisible(true);
		getToolbarPnl().doLayout();
		getTbPassBtn().setEnabled(true);
		getTbFreezeBtn().setEnabled(true);

		// Begin scraping the selected window.
		int i = getWindowLB().getSelectedIndex();
		Integer id = (Integer) wm.elementAt(i);
		bscraper.configureToWindow(id.intValue());
		bscraper.resume();
		getImagePnl().setBScraper(bscraper);

		postMessage(getMsgTA(),"Sharing " + getWindowLB().getSelectedValue());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2002 9:52:26 PM)
 * @param args java.lang.String[]
 */
public static void start(String token, Socket socket) throws Exception {
	if (token == null || token.trim().length() == 0)
		throw new Exception("token not provided");

	if (socket == null)
		throw new Exception("socket not provided.");

	initDlg = new Frame("Conference");
	initDlg.setLayout(new BorderLayout());
	initLbl = new Label("Initializing...");
	initLbl.setAlignment(Label.CENTER);
	initDlg.add(initLbl,"Center");
	Dimension s = new Dimension(200,100);
	initDlg.pack();
	initDlg.setSize(s);

	centerWindow(initDlg);

	MeetingViewer aMeetingViewer = new MeetingViewer();
	aMeetingViewer.begin(token,socket);
}
/**
 * Comment
 */
public void startMeeting() {
	if (! getStartOkBtn().isEnabled())
		return;

	// Can't start or attend a meeting now.
	getAttendMI().setEnabled(false);
	getCallMI().setEnabled(false);
	getStartMI().setEnabled(false);
	getTbAttendBtn().setEnabled(false);
	getTbStartBtn().setEnabled(false);

	getStartDlg().setVisible(false);

	// Need to realize the image panel and then hide it.
	if (! imageRealized) {
		getImageFrame().setVisible(true);
		getImageFrame().setVisible(false);
	}

	getPresencePnl().setDispatcher(dispatcher);
	getPresencePnl().setEnablement(2);
	getImagePnl().setDispatcher(dispatcher);
	dispatcher.addMessageListener(this);

	String localScreen = getScreenTF().getText();
	if (localScreen != null && localScreen.trim().length() > 0)
		screen = localScreen;

	String classify;
	if (getSecConfCB().isSelected())
		classify = getSecConfCB().getText();
	else if (getSecUnclassCB().isSelected())
		classify = getSecUnclassCB().getText();
	else
		classify = getSecOtherTF().getText();

	dispatcher.password = getStartPwdTF().getText();
	if (dispatcher.password != null && dispatcher.password.length() == 0) {
		dispatcher.password = null;
	}

	dispatcher.callEnabled = getStartCallCB().isSelected(); 

	DSMPProto p = DSMPGenerator.startMeeting((byte) 0,getStartTitleTF().getText(),dispatcher.password,classify);
	dispatcher.dispatchProtocol(p);

	getMeetingLbl().setText(getStartTitleTF().getText() + " - may include [" + classify + "] information");
	getImageFrame().setTitle(getStartTitleTF().getText() + " - " + dispatcher.user + " - may include [" + classify + "] information");
}
/**
 * Comment
 */
public void stopShare() {
	// Swap all the menu items and tool buttons.
	// Prepare the main window.
	getDesktopMI().setEnabled(true);
	getWindowMI().setEnabled(true);
	getStopMI().setEnabled(false);
	getPassMI().setEnabled(false);
	getFreezeMI().setEnabled(false);

	// Set up the toolbar.
	getTbShDskBtn().setVisible(true);
	getTbShWinBtn().setVisible(true);
	getTbStopBtn().setVisible(false);
	getToolbarPnl().doLayout();
	getTbPassBtn().setEnabled(false);
	getTbFreezeBtn().setEnabled(false);

	// Pause the scraper and stop the image panel.
	bscraper.pause();
	getImagePnl().setBScraper(null);

	// Tell moderator sharing is stopped.
	postMessage(getMsgTA(),"Sharing stopped.");

	dispatcher.isSharing = false;
}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2004 1:13:25 PM)
 * @param msg java.lang.String
 */
public void syntax(String msg) {
	System.out.println(msg);
	System.out.println("MeetingViewer -HOST host -PORT port [-DISPLAY display] [-TOKEN token]");
	System.out.println("              [-MEETING name] [-USER user] [-PW password]");
	System.out.println("              [-MEETINGID num] [-MEETINGUSER user] [-MEETINGPW password]");
	System.out.println("              [-TOKEN token] [-collapseToolbar] [-collapsePresence] [-collapseMessages] [-collapseAll]");
	System.out.println("              [-maximize] [-iconize] [-limitchat] [-autoshare] -[title title]");
}
/**
 * Comment
 */
public void textChgLogin() {
	String user = getLoginUserTF().getText();
	String pwd = new String(getLoginPwdTF().getPassword());

	getLoginOkBtn().setEnabled(user != null && user.length() > 0 &&
		pwd != null && pwd.length() > 0);
}
/**
 * Comment
 */
public void textChgStart() {
	String sec = getSecOtherTF().getText();
	String title = getStartTitleTF().getText();
	String screen = getScreenTF().getText();

	getStartOkBtn().setEnabled(title != null && title.trim().length() > 0 &&
		screen != null && screen.trim().length() > 0 &&
		(! getSecOtherCB().isSelected() || (sec != null && sec.trim().length() > 0)));
}
/**
 * Comment
 */
public void textChgUserInvite() {
	String user = getInviteUserTF().getText();

	getInviteUserOkBtn().setEnabled(user != null && user.trim().length() > 0);

	return;
}
/**
 * Comment
 */
public void transferCanWindow() {
	synchronized (mvState) {
		mvState.state = MeetingViewerState.READY;
		getTransferDlg().setVisible(false);
	}
}
/**
 * Comment
 */
public void transferControl() {
	synchronized (mvState) {
		if (mvState.state != MeetingViewerState.CONTROL_WINDOW)
			return;

		mvState.state = MeetingViewerState.READY;

		// Hide the transfer dialog.
		getTransferDlg().setVisible(false);

		// Disable the passing buttons.
		getTbPassBtn().setEnabled(false);
		getPassMI().setEnabled(false);

		// Get the user which was selected.
		int i = getTransferLB().getSelectedIndex();
		Integer pid = (Integer) passKeys.elementAt(i);
		DSMPProto p;
		if (getTransferDurCB().getSelectedIndex() == 0) {
			p = DSMPGenerator.modifyControl((byte) 0,true,false,dispatcher.meetingID,pid.intValue());
		}
		else {
			p = DSMPGenerator.modifyControl((byte) 0,true,true,dispatcher.meetingID,pid.intValue());
			getTransferDurationCB().setSelectedIndex(getTransferDurCB().getSelectedIndex()-1);

			// Start the timer which will take control away from the user.
			timer = new Thread(this);
			timer.start();
		}
		dispatcher.dispatchProtocol(p);
	}
}
/**
 * Comment
 */
public void transferListChg() {
	getTransferOkBtn().setEnabled(getTransferLB().getSelectedIndex() != -1);
}
/**
 * Comment
 */
public void windowListChg() {
	getWindowOkBtn().setEnabled(getWindowLB().getSelectedIndex() != -1);
}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUrlCP() {
		if(urlCP == null) {
			urlCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints6 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
			consGridBagConstraints5.gridy = 3;
			consGridBagConstraints5.gridx = 0;
			consGridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints5.insets = new java.awt.Insets(0,25,0,5);
			consGridBagConstraints4.gridy = 1;
			consGridBagConstraints4.gridx = 0;
			consGridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints4.insets = new java.awt.Insets(0,25,0,5);
			consGridBagConstraints1.gridy = 0;
			consGridBagConstraints1.gridx = 0;
			consGridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints1.insets = new java.awt.Insets(5,5,0,5);
			consGridBagConstraints2.gridy = 2;
			consGridBagConstraints2.gridx = 0;
			consGridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints2.insets = new java.awt.Insets(3,5,0,5);
			consGridBagConstraints3.gridy = 4;
			consGridBagConstraints3.gridx = 0;
			consGridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints6.weighty = 1.0;
			consGridBagConstraints6.weightx = 1.0;
			consGridBagConstraints6.gridy = 5;
			consGridBagConstraints6.gridx = 0;
			consGridBagConstraints6.anchor = java.awt.GridBagConstraints.SOUTH;
			consGridBagConstraints6.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints3.insets = new java.awt.Insets(3,5,0,5);
			consGridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			urlCP.setLayout(new java.awt.GridBagLayout());
			urlCP.add(getUrlPasswordCB(), consGridBagConstraints1);
			urlCP.add(getUrlMeetingCB(), consGridBagConstraints2);
			urlCP.add(getUrlSendCB(), consGridBagConstraints3);
			urlCP.add(getUrlPasswordLbl(), consGridBagConstraints4);
			urlCP.add(getUrlMeetingLbl(), consGridBagConstraints5);
			urlCP.add(getUrlBtnPnl(), consGridBagConstraints6);
		}
		return urlCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getUrlDlg() {
		if(urlDlg == null) {
			urlDlg = new javax.swing.JDialog(this);
			urlDlg.setContentPane(getUrlCP());
			urlDlg.setSize(315, 206);
			urlDlg.setTitle("Get URL...");
			urlDlg.setModal(true);
			urlDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return urlDlg;
	}
	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getUrlPasswordCB() {
		if(urlPasswordCB == null) {
			urlPasswordCB = new javax.swing.JCheckBox();
			urlPasswordCB.setText("Include meeting password");
		}
		return urlPasswordCB;
	}
	/**
	 * This method initializes jCheckBox1
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getUrlMeetingCB() {
		if(urlMeetingCB == null) {
			urlMeetingCB = new javax.swing.JCheckBox();
			urlMeetingCB.setText("Include meeting ID");
		}
		return urlMeetingCB;
	}
	/**
	 * This method initializes jCheckBox2
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getUrlSendCB() {
		if(urlSendCB == null) {
			urlSendCB = new javax.swing.JCheckBox();
			urlSendCB.setText("Send e-mail to invitees");
		}
		return urlSendCB;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getUrlPasswordLbl() {
		if(urlPasswordLbl == null) {
			urlPasswordLbl = new javax.swing.JLabel();
			urlPasswordLbl.setText("(Enables automated entry into meeting)");
		}
		return urlPasswordLbl;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getUrlMeetingLbl() {
		if(urlMeetingLbl == null) {
			urlMeetingLbl = new javax.swing.JLabel();
			urlMeetingLbl.setText("(URL is valid for this meeting only)");
		}
		return urlMeetingLbl;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUrlBtnPnl() {
		if(urlBtnPnl == null) {
			urlBtnPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();
			consGridBagConstraints7.gridy = 0;
			consGridBagConstraints7.gridx = 0;
			consGridBagConstraints8.gridy = 0;
			consGridBagConstraints8.gridx = 1;
			consGridBagConstraints8.insets = new java.awt.Insets(0,5,0,0);
			consGridBagConstraints7.insets = new java.awt.Insets(0,0,0,5);
			urlBtnPnl.setLayout(new java.awt.GridBagLayout());
			urlBtnPnl.add(getUrlOkBtn(), consGridBagConstraints7);
			urlBtnPnl.add(getUrlCanBtn(), consGridBagConstraints8);
		}
		return urlBtnPnl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUrlOkBtn() {
		if(urlOkBtn == null) {
			urlOkBtn = new javax.swing.JButton();
			urlOkBtn.setText("OK");
			urlOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUrlDlg().dispose();
					if (! dispatcher.callEnabled &&
						! getUrlPasswordCB().isSelected() &&
						getUrlMeetingCB().isSelected()) {
						getUrlWarnDlg().setLocationRelativeTo(MeetingViewer.this);
						getUrlWarnDlg().setVisible(true);
					}
					else {
						DSMPProto p = DSMPGenerator.getMeetingURL((byte) 0,
											getUrlPasswordCB().isSelected(),
											getUrlMeetingCB().isSelected(),
											getUrlSendCB().isSelected(),
											dispatcher.meetingID);
						dispatcher.dispatchProtocol(p);
					}
				}
			});
		}
		return urlOkBtn;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUrlCanBtn() {
		if(urlCanBtn == null) {
			urlCanBtn = new javax.swing.JButton();
			urlCanBtn.setText("Cancel");
			urlCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUrlDlg().dispose();
				}
			});
		}
		return urlCanBtn;
	}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getUrlMI() {
		if(urlMI == null) {
			urlMI = new javax.swing.JMenuItem();
			urlMI.setText("Get URL...");
			urlMI.setMnemonic(java.awt.event.KeyEvent.VK_G);
			urlMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.Event.CTRL_MASK, false));
			urlMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (dispatcher.password == null || dispatcher.password.length() == 0) {
						getUrlPasswordCB().setText("Include meeting password (not set)");
						getUrlPasswordCB().setSelected(false);
						getUrlPasswordCB().setEnabled(false);
						getUrlPasswordLbl().setEnabled(false);
					}
					else {
						getUrlPasswordCB().setText("Include meeting password");
						getUrlPasswordCB().setSelected(true);
						getUrlPasswordCB().setEnabled(true);
						getUrlPasswordLbl().setEnabled(true);
					}
					
					getUrlMeetingCB().setSelected(false);
					getUrlSendCB().setSelected(false);
					
					getUrlDlg().setLocationRelativeTo(MeetingViewer.this);
					getUrlDlg().show();
				}
			});
		}
		return urlMI;
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getShowUrlCP() {
		if(showUrlCP == null) {
			showUrlCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints12 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints11 = new java.awt.GridBagConstraints();
			consGridBagConstraints10.gridy = 2;
			consGridBagConstraints10.gridx = 0;
			consGridBagConstraints10.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints11.weighty = 1.0;
			consGridBagConstraints11.weightx = 1.0;
			consGridBagConstraints11.gridy = 1;
			consGridBagConstraints11.gridx = 0;
			consGridBagConstraints11.insets = new java.awt.Insets(0,5,0,5);
			consGridBagConstraints12.gridy = 0;
			consGridBagConstraints12.gridx = 0;
			consGridBagConstraints12.insets = new java.awt.Insets(5,5,2,5);
			consGridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			showUrlCP.setLayout(new java.awt.GridBagLayout());
			showUrlCP.add(getShowUrlBtn(), consGridBagConstraints10);
			showUrlCP.add(getShowUrlSP(), consGridBagConstraints11);
			showUrlCP.add(getShowUrlLbl(), consGridBagConstraints12);
		}
		return showUrlCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getShowUrlDlg() {
		if(showUrlDlg == null) {
			showUrlDlg = new javax.swing.JDialog(this);
			showUrlDlg.setContentPane(getShowUrlCP());
			showUrlDlg.setSize(317, 231);
			showUrlDlg.setTitle("Meeting URL");
			showUrlDlg.setModal(true);
			showUrlDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return showUrlDlg;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getShowUrlBtn() {
		if(showUrlBtn == null) {
			showUrlBtn = new javax.swing.JButton();
			showUrlBtn.setText("Close");
			showUrlBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getShowUrlDlg().dispose();
				}
			});
		}
		return showUrlBtn;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getShowUrlSP() {
		if(showUrlSP == null) {
			showUrlSP = new javax.swing.JScrollPane();
			showUrlSP.setViewportView(getShowUrlTA());
		}
		return showUrlSP;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getShowUrlTA() {
		if(showUrlTA == null) {
			showUrlTA = new javax.swing.JTextArea();
			showUrlTA.setEditable(false);
			showUrlTA.setLineWrap(true);
		}
		return showUrlTA;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getShowUrlLbl() {
		if(showUrlLbl == null) {
			showUrlLbl = new javax.swing.JLabel();
			showUrlLbl.setText("Use ctrl-c to copy URL to clipboard:");
		}
		return showUrlLbl;
	}
	/**
	 * This method initializes jContentPane1
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getCallCP() {
		if(callCP == null) {
			callCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints14 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints13 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints15 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints17 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints16 = new java.awt.GridBagConstraints();
			consGridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints14.weightx = 1.0;
			consGridBagConstraints14.gridy = 1;
			consGridBagConstraints14.gridx = 1;
			consGridBagConstraints15.gridy = 2;
			consGridBagConstraints15.gridx = 1;
			consGridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints16.weightx = 1.0;
			consGridBagConstraints16.gridy = 3;
			consGridBagConstraints16.gridx = 1;
			consGridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints15.insets = new java.awt.Insets(0,5,2,5);
			consGridBagConstraints14.insets = new java.awt.Insets(0,10,5,5);
			consGridBagConstraints16.insets = new java.awt.Insets(0,10,0,5);
			consGridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints17.weighty = 1.0;
			consGridBagConstraints17.weightx = 1.0;
			consGridBagConstraints17.gridy = 4;
			consGridBagConstraints17.gridx = 1;
			consGridBagConstraints17.anchor = java.awt.GridBagConstraints.SOUTH;
			consGridBagConstraints13.gridy = 0;
			consGridBagConstraints13.gridx = 1;
			consGridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints13.insets = new java.awt.Insets(5,5,2,5);
			consGridBagConstraints17.insets = new java.awt.Insets(5,5,5,5);
			callCP.setLayout(new java.awt.GridBagLayout());
			callCP.add(getCallLbl1(), consGridBagConstraints13);
			callCP.add(getCallUserCB(), consGridBagConstraints14);
			callCP.add(getCallLbl2(), consGridBagConstraints15);
			callCP.add(getCallPassTF(), consGridBagConstraints16);
			callCP.add(getCallBtnPnl(), consGridBagConstraints17);
		}
		return callCP;
	}
	/**
	 * This method initializes jDialog1
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getCallDlg() {
		if(callDlg == null) {
			callDlg = new javax.swing.JDialog(this);
			callDlg.setContentPane(getCallCP());
			callDlg.setSize(299, 200);
			callDlg.setTitle("Call...");
			callDlg.setModal(true);
			callDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return callDlg;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallLbl1() {
		if(callLbl1 == null) {
			callLbl1 = new javax.swing.JLabel();
			callLbl1.setText("Select the user to call:");
		}
		return callLbl1;
	}
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getCallUserCB() {
		if(callUserCB == null) {
			callUserCB = new javax.swing.JComboBox();
			callUserCB.setEditable(true);
			callUserCB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					String name = (String) getCallUserCB().getSelectedItem();
					getCallOkBtn().setEnabled(name != null && name.length() > 0);
				}
			});
			ComboBoxEditor e = callUserCB.getEditor();
			if (e != null) {
				Component ed = e.getEditorComponent();
				if (ed != null && ed instanceof JTextField) {
					JTextField tf = (JTextField) ed;
					tf.getDocument().addDocumentListener(new DocumentListener() {
						public void changedUpdate(DocumentEvent e) {
							event(e);
						}
						public void insertUpdate(DocumentEvent e) {
							event(e);
						}
						public void removeUpdate(DocumentEvent e) {
							event(e);
						}
						private void event(DocumentEvent e) {
							JTextField tf = (JTextField) callUserCB.getEditor().getEditorComponent();
							String name = tf.getText();
							getCallOkBtn().setEnabled(name != null && name.trim().length() > 0);
						}
					});
				}
			}
		}
		return callUserCB;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallLbl2() {
		if(callLbl2 == null) {
			callLbl2 = new javax.swing.JLabel();
			callLbl2.setText("Password (if provided):");
		}
		return callLbl2;
	}
	/**
	 * This method initializes jPasswordField
	 * 
	 * @return javax.swing.JPasswordField
	 */
	private javax.swing.JPasswordField getCallPassTF() {
		if(callPassTF == null) {
			callPassTF = new javax.swing.JPasswordField();
			callPassTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					call();
				}
			});
		}
		return callPassTF;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getCallBtnPnl() {
		if(callBtnPnl == null) {
			callBtnPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints18 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints19 = new java.awt.GridBagConstraints();
			consGridBagConstraints18.gridy = 0;
			consGridBagConstraints18.gridx = 0;
			consGridBagConstraints18.insets = new java.awt.Insets(0,0,0,5);
			consGridBagConstraints19.gridy = 0;
			consGridBagConstraints19.gridx = 1;
			consGridBagConstraints19.insets = new java.awt.Insets(0,5,0,0);
			callBtnPnl.setLayout(new java.awt.GridBagLayout());
			callBtnPnl.add(getCallOkBtn(), consGridBagConstraints18);
			callBtnPnl.add(getCallCanBtn(), consGridBagConstraints19);
		}
		return callBtnPnl;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallOkBtn() {
		if(callOkBtn == null) {
			callOkBtn = new javax.swing.JButton();
			callOkBtn.setText("Call");
			callOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					call();
				}
			});
		}
		return callOkBtn;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallCanBtn() {
		if(callCanBtn == null) {
			callCanBtn = new javax.swing.JButton();
			callCanBtn.setText("Cancel");
			callCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getCallDlg().dispose();
				}
			});
		}
		return callCanBtn;
	}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCallMI() {
		if(callMI == null) {
			callMI = new javax.swing.JMenuItem();
			callMI.setText("Call...");
			callMI.setMnemonic(java.awt.event.KeyEvent.VK_C);
			callMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK, false));
			callMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Vector bl = buddyMgr.getBuddyList();
					getCallUserCB().removeAllItems();
					getCallUserCB().addItem("");
					
					Enumeration names = bl.elements();
					while (names.hasMoreElements()) {
						String name = (String) names.nextElement();
						getCallUserCB().addItem(name);
					}
					
					getCallPassTF().setText(null);
					getCallOkBtn().setEnabled(false);
					getCallDlg().setLocationRelativeTo(MeetingViewer.this);
					getCallDlg().show();
				}
			});
		}
		return callMI;
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getCallingCP() {
		if(callingCP == null) {
			callingCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints51 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints41 = new java.awt.GridBagConstraints();
			consGridBagConstraints51.gridy = 1;
			consGridBagConstraints51.gridx = 0;
			consGridBagConstraints51.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints41.gridy = 0;
			consGridBagConstraints41.gridx = 0;
			consGridBagConstraints41.insets = new java.awt.Insets(5,5,5,5);
			callingCP.setLayout(new java.awt.GridBagLayout());
			callingCP.add(getCallingLbl(), consGridBagConstraints41);
			callingCP.add(getCallingCanBtn(), consGridBagConstraints51);
		}
		return callingCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getCallingDlg() {
		if(callingDlg == null) {
			callingDlg = new javax.swing.JDialog(this);
			callingDlg.setContentPane(getCallingCP());
			callingDlg.setSize(232, 151);
			callingDlg.setTitle("Calling...");
			callingDlg.setModal(true);
			callingDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			callingDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowClosed(java.awt.event.WindowEvent e) {    
					synchronized(mvState) {
						if (mvState.state == MeetingViewerState.CALL_WINDOW) {
							resetToLoggedIn();
							mvState.state = MeetingViewerState.READY;
						}
					}
				}
			});
		}
		return callingDlg;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallingLbl() {
		if(callingLbl == null) {
			callingLbl = new javax.swing.JLabel();
			callingLbl.setText("Waiting for answer...");
		}
		return callingLbl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallingCanBtn() {
		if(callingCanBtn == null) {
			callingCanBtn = new javax.swing.JButton();
			callingCanBtn.setText("Cancel");
			callingCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getCallingDlg().dispose();
				}
			});
		}
		return callingCanBtn;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getStartPwdPnl() {
		if(startPwdPnl == null) {
			startPwdPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints81 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints121 = new java.awt.GridBagConstraints();
			consGridBagConstraints81.gridy = 0;
			consGridBagConstraints81.gridx = 0;
			consGridBagConstraints81.insets = new java.awt.Insets(0,0,0,10);
			consGridBagConstraints121.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints121.weightx = 1.0;
			consGridBagConstraints121.gridy = 0;
			consGridBagConstraints121.gridx = 1;
			startPwdPnl.setLayout(new java.awt.GridBagLayout());
			startPwdPnl.add(getStartPwdLbl(), consGridBagConstraints81);
			startPwdPnl.add(getStartPwdTF(), consGridBagConstraints121);
		}
		return startPwdPnl;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getStartPwdLbl() {
		if(startPwdLbl == null) {
			startPwdLbl = new javax.swing.JLabel();
			startPwdLbl.setText("Password:");
		}
		return startPwdLbl;
	}
	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getStartCallCB() {
		if(startCallCB == null) {
			startCallCB = new javax.swing.JCheckBox();
			startCallCB.setText("Enable cold calling");
			startCallCB.setToolTipText("Allow owner to accept calls (no password required)");
		}
		return startCallCB;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getStartPwdTF() {
		if(startPwdTF == null) {
			startPwdTF = new javax.swing.JTextField();
			startPwdTF.setToolTipText("Allows a caller to enter meeting unassisted");
		}
		return startPwdTF;
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getSettingsCP() {
		if(settingsCP == null) {
			settingsCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints141 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints131 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints151 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints161 = new java.awt.GridBagConstraints();
			consGridBagConstraints161.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints161.weighty = 1.0;
			consGridBagConstraints161.weightx = 1.0;
			consGridBagConstraints161.gridy = 2;
			consGridBagConstraints161.gridx = 0;
			consGridBagConstraints161.gridwidth = 0;
			consGridBagConstraints161.insets = new java.awt.Insets(0,5,5,5);
			consGridBagConstraints161.anchor = java.awt.GridBagConstraints.SOUTH;
			consGridBagConstraints141.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints141.weightx = 1.0;
			consGridBagConstraints141.gridy = 0;
			consGridBagConstraints141.gridx = 1;
			consGridBagConstraints141.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints151.gridy = 1;
			consGridBagConstraints151.gridx = 0;
			consGridBagConstraints151.gridwidth = 0;
			consGridBagConstraints151.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints151.insets = new java.awt.Insets(0,5,5,5);
			consGridBagConstraints131.gridy = 0;
			consGridBagConstraints131.gridx = 0;
			consGridBagConstraints131.insets = new java.awt.Insets(5,5,5,5);
			settingsCP.setLayout(new java.awt.GridBagLayout());
			settingsCP.add(getSettingsPwdLbl(), consGridBagConstraints131);
			settingsCP.add(getSettingsPwdTF(), consGridBagConstraints141);
			settingsCP.add(getSettingsCallCB(), consGridBagConstraints151);
			settingsCP.add(getSettingsBtnPnl(), consGridBagConstraints161);
		}
		return settingsCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getSettingsDlg() {
		if(settingsDlg == null) {
			settingsDlg = new javax.swing.JDialog(this);
			settingsDlg.setContentPane(getSettingsCP());
			settingsDlg.setSize(295, 227);
			settingsDlg.setTitle("Settings");
		}
		return settingsDlg;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getSettingsPwdLbl() {
		if(settingsPwdLbl == null) {
			settingsPwdLbl = new javax.swing.JLabel();
			settingsPwdLbl.setText("Password:");
		}
		return settingsPwdLbl;
	}
	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getSettingsPwdTF() {
		if(settingsPwdTF == null) {
			settingsPwdTF = new javax.swing.JTextField();
			settingsPwdTF.setToolTipText("Allows a caller to enter meeting unassisted");
		}
		return settingsPwdTF;
	}
	/**
	 * This method initializes jCheckBox1
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getSettingsCallCB() {
		if(settingsCallCB == null) {
			settingsCallCB = new javax.swing.JCheckBox();
			settingsCallCB.setText("Enable cold calling");
			settingsCallCB.setToolTipText("Allow owner to accept calls (no password required)");
		}
		return settingsCallCB;
	}
	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getSettingsBtnPnl() {
		if(settingsBtnPnl == null) {
			settingsBtnPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints181 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints171 = new java.awt.GridBagConstraints();
			consGridBagConstraints181.gridy = 0;
			consGridBagConstraints181.gridx = 1;
			consGridBagConstraints171.gridy = 0;
			consGridBagConstraints171.gridx = 0;
			consGridBagConstraints171.insets = new java.awt.Insets(0,0,0,5);
			consGridBagConstraints181.insets = new java.awt.Insets(0,5,0,0);
			settingsBtnPnl.setLayout(new java.awt.GridBagLayout());
			settingsBtnPnl.add(getSettingsOkBtn(), consGridBagConstraints171);
			settingsBtnPnl.add(getSettingsCanBtn(), consGridBagConstraints181);
		}
		return settingsBtnPnl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getSettingsOkBtn() {
		if(settingsOkBtn == null) {
			settingsOkBtn = new javax.swing.JButton();
			settingsOkBtn.setText("Save");
			settingsOkBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String oldPwd = dispatcher.password;
					if (oldPwd == null) oldPwd = "";
					String newPwd = getSettingsPwdTF().getText();
					if (newPwd == null) newPwd = "";

					if (! oldPwd.equals(newPwd)) {
						if (newPwd.length() == 0) newPwd = null;
						DSMPProto p = DSMPGenerator.setMeetingOption((byte) 0,dispatcher.meetingID,
																	DSMPGenerator.MEETINGPASSWORD_OPTION,
																	newPwd);
						dispatcher.dispatchProtocol(p);
					}
					
					if (dispatcher.callEnabled != getSettingsCallCB().isSelected()) {
						DSMPProto p = DSMPGenerator.setMeetingOption((byte) 0,dispatcher.meetingID,
																	DSMPGenerator.COLDCALL_OPTION,
																	getSettingsCallCB().isSelected() ? "true" : "false");
						dispatcher.dispatchProtocol(p);
					}

					getSettingsDlg().dispose();
				}
			});
		}
		return settingsOkBtn;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getSettingsCanBtn() {
		if(settingsCanBtn == null) {
			settingsCanBtn = new javax.swing.JButton();
			settingsCanBtn.setText("Cancel");
			settingsCanBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getSettingsDlg().dispose();
				}
			});
		}
		return settingsCanBtn;
	}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getSettingsMI() {
		if(settingsMI == null) {
			settingsMI = new javax.swing.JMenuItem();
			settingsMI.setText("Settings...");
			settingsMI.setMnemonic(java.awt.event.KeyEvent.VK_E);
			settingsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.Event.CTRL_MASK, false));
			settingsMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getSettingsPwdTF().setText(dispatcher.password);
					getSettingsCallCB().setSelected(dispatcher.callEnabled);
					
					getSettingsDlg().setLocationRelativeTo(MeetingViewer.this);
					getSettingsDlg().show();
				}
			});
		}
		return settingsMI;
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getCallerCP() {
		if(callerCP == null) {
			callerCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints191 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints20 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints21 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints22 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints110 = new java.awt.GridBagConstraints();
			consGridBagConstraints110.gridy = 2;
			consGridBagConstraints110.gridx = 0;
			consGridBagConstraints110.gridwidth = 0;
			consGridBagConstraints110.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints110.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints110.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints191.gridy = 0;
			consGridBagConstraints191.gridx = 0;
			consGridBagConstraints191.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints191.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints20.gridy = 0;
			consGridBagConstraints20.gridx = 1;
			consGridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints21.gridy = 1;
			consGridBagConstraints21.gridx = 0;
			consGridBagConstraints21.insets = new java.awt.Insets(0,5,5,5);
			consGridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints26.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints26.weighty = 1.0;
			consGridBagConstraints26.weightx = 1.0;
			consGridBagConstraints26.gridy = 3;
			consGridBagConstraints26.gridx = 0;
			consGridBagConstraints26.gridwidth = 0;
			consGridBagConstraints26.insets = new java.awt.Insets(0,5,5,5);
			consGridBagConstraints26.anchor = java.awt.GridBagConstraints.SOUTH;
			consGridBagConstraints22.gridy = 1;
			consGridBagConstraints22.gridx = 1;
			consGridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints22.insets = new java.awt.Insets(0,0,5,5);
			callerCP.setLayout(new java.awt.GridBagLayout());
			callerCP.add(getCallerLbl1(), consGridBagConstraints191);
			callerCP.add(getCallerUserLbl(), consGridBagConstraints20);
			callerCP.add(getCallerLbl2(), consGridBagConstraints21);
			callerCP.add(getCallerCompanyLbl(), consGridBagConstraints22);
			callerCP.add(getCallerBtnPnl(), consGridBagConstraints26);
			callerCP.add(getCallerQueueLbl(), consGridBagConstraints110);
		}
		return callerCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getCallerDlg() {
		if(callerDlg == null) {
			callerDlg = new javax.swing.JDialog(this);
			callerDlg.setContentPane(getCallerCP());
			callerDlg.setSize(296, 166);
			callerDlg.setTitle("Incoming Call");
			callerDlg.setModal(true);
			callerDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return callerDlg;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallerLbl1() {
		if(callerLbl1 == null) {
			callerLbl1 = new javax.swing.JLabel();
			callerLbl1.setText("Caller:");
		}
		return callerLbl1;
	}
	/**
	 * This method initializes jLabel1
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallerUserLbl() {
		if(callerUserLbl == null) {
			callerUserLbl = new javax.swing.JLabel();
			callerUserLbl.setText("JLabel");
		}
		return callerUserLbl;
	}
	/**
	 * This method initializes jLabel2
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallerLbl2() {
		if(callerLbl2 == null) {
			callerLbl2 = new javax.swing.JLabel();
			callerLbl2.setText("Company:");
		}
		return callerLbl2;
	}
	/**
	 * This method initializes jLabel3
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallerCompanyLbl() {
		if(callerCompanyLbl == null) {
			callerCompanyLbl = new javax.swing.JLabel();
			callerCompanyLbl.setText("JLabel");
		}
		return callerCompanyLbl;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getCallerBtnPnl() {
		if(callerBtnPnl == null) {
			callerBtnPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints28 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints27 = new java.awt.GridBagConstraints();
			consGridBagConstraints28.gridy = 0;
			consGridBagConstraints28.gridx = 1;
			consGridBagConstraints28.insets = new java.awt.Insets(0,5,0,5);
			consGridBagConstraints29.gridy = 0;
			consGridBagConstraints29.gridx = 2;
			consGridBagConstraints29.insets = new java.awt.Insets(0,5,0,0);
			consGridBagConstraints27.gridy = 0;
			consGridBagConstraints27.gridx = 0;
			consGridBagConstraints27.insets = new java.awt.Insets(0,0,0,5);
			callerBtnPnl.setLayout(new java.awt.GridBagLayout());
			callerBtnPnl.add(getCallerOkBtn(), consGridBagConstraints27);
			callerBtnPnl.add(getCallerIgnoreBtn(), consGridBagConstraints28);
			callerBtnPnl.add(getCallerDisableBtn(), consGridBagConstraints29);
		}
		return callerBtnPnl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallerOkBtn() {
		if(callerOkBtn == null) {
			callerOkBtn = new javax.swing.JButton();
			callerOkBtn.setText("Accept");
			callerOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Dispose dialog, leave caller in queue, removed during processing
					// of reply to accept.
					getCallerDlg().dispose();
					MeetingEvent me = (MeetingEvent) callQueue.elementAt(0);
					DSMPProto p = DSMPGenerator.acceptCall((byte) 0,me.meetingID,me.participantID,me.sdata);
					dispatcher.dispatchProtocol(p);
				}
			});
		}
		return callerOkBtn;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallerIgnoreBtn() {
		if(callerIgnoreBtn == null) {
			callerIgnoreBtn = new javax.swing.JButton();
			callerIgnoreBtn.setText("Ignore");
			callerIgnoreBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getCallerDlg().dispose();
					synchronized(callQueue) {
						// Remove caller from queue and display next caller, if any.
						callQueue.removeElementAt(0);
						if (callQueue.size() > 0) {
							MeetingEvent me = (MeetingEvent) callQueue.elementAt(0);
							getCallerUserLbl().setText(me.user);
							getCallerCompanyLbl().setText(me.company);
							if (callQueue.size() == 1) {
								getCallerQueueLbl().setText("");
							}
							else {
								getCallerQueueLbl().setText((callQueue.size()-1) + " callers in the queue");
							}
							getCallerDlg().setLocationRelativeTo(MeetingViewer.this);
							getCallerDlg().show();
						}
					}
				}
			});
		}
		return callerIgnoreBtn;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getCallerDisableBtn() {
		if(callerDisableBtn == null) {
			callerDisableBtn = new javax.swing.JButton();
			callerDisableBtn.setText("Disable All");
			callerDisableBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getCallerDlg().dispose();
					// Turn off cold calling. Don't remove current caller, do this at reply
					// to meeting settings.
					DSMPProto p = DSMPGenerator.setMeetingOption((byte) 1,dispatcher.meetingID,
													DSMPGenerator.COLDCALL_OPTION,"false");
					dispatcher.dispatchProtocol(p);
				}
			});
		}
		return callerDisableBtn;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getCallerQueueLbl() {
		if(callerQueueLbl == null) {
			callerQueueLbl = new javax.swing.JLabel();
			callerQueueLbl.setText("JLabel");
		}
		return callerQueueLbl;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getTransferDurationLbl() {
		if(transferDurationLbl == null) {
			transferDurationLbl = new javax.swing.JLabel();
			transferDurationLbl.setText("Duration (in minutes):");
		}
		return transferDurationLbl;
	}
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getTransferDurationCB() {
		if(transferDurationCB == null) {
			transferDurationCB = new javax.swing.JComboBox();
			transferDurationCB.setPreferredSize(new java.awt.Dimension(65,24));
			transferDurationCB.addItem(new Integer(5));
			transferDurationCB.addItem(new Integer(10));
			transferDurationCB.addItem(new Integer(15));
			transferDurationCB.addItem(new Integer(30));
		}
		return transferDurationCB;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getTransferDurationPnl() {
		if(transferDurationPnl == null) {
			transferDurationPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints61 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints52 = new java.awt.GridBagConstraints();
			consGridBagConstraints61.insets = new java.awt.Insets(0,0,0,0);
			consGridBagConstraints61.fill = java.awt.GridBagConstraints.NONE;
			consGridBagConstraints61.weightx = 1.0;
			consGridBagConstraints61.gridy = 0;
			consGridBagConstraints61.gridx = 1;
			consGridBagConstraints61.anchor = java.awt.GridBagConstraints.WEST;
			consGridBagConstraints52.insets = new java.awt.Insets(0,0,0,5);
			consGridBagConstraints52.gridy = 0;
			consGridBagConstraints52.gridx = 0;
			transferDurationPnl.setLayout(new java.awt.GridBagLayout());
			transferDurationPnl.add(getTransferDurationLbl(), consGridBagConstraints52);
			transferDurationPnl.add(getTransferDurationCB(), consGridBagConstraints61);
		}
		return transferDurationPnl;
	}
	/**
	 * This method initializes TransferDurPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getTransferDurPnl() {
		if(transferDurPnl == null) {
			transferDurPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints62 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints43 = new java.awt.GridBagConstraints();
			consGridBagConstraints62.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints62.weightx = 1.0;
			consGridBagConstraints62.gridy = 0;
			consGridBagConstraints62.gridx = 1;
			consGridBagConstraints43.gridy = 0;
			consGridBagConstraints43.gridx = 0;
			consGridBagConstraints43.insets = new java.awt.Insets(0,0,0,3);
			transferDurPnl.setLayout(new java.awt.GridBagLayout());
			transferDurPnl.add(getTransferDurLbl(), consGridBagConstraints43);
			transferDurPnl.add(getTransferDurCB(), consGridBagConstraints62);
		}
		return transferDurPnl;
	}
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getTransferDurLbl() {
		if(transferDurLbl == null) {
			transferDurLbl = new javax.swing.JLabel();
			transferDurLbl.setText("Duration (in minutes):");
		}
		return transferDurLbl;
	}
	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getTransferDurCB() {
		if(transferDurCB == null) {
			transferDurCB = new javax.swing.JComboBox();
			transferDurCB.addItem("No limit");
			transferDurCB.addItem(new Integer(5));
			transferDurCB.addItem(new Integer(10));
			transferDurCB.addItem(new Integer(15));
			transferDurCB.addItem(new Integer(30));
		}
		return transferDurCB;
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUrlWarnCP() {
		if(urlWarnCP == null) {
			urlWarnCP = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints122 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints112 = new java.awt.GridBagConstraints();
			consGridBagConstraints122.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints122.weighty = 0.0D;
			consGridBagConstraints122.weightx = 1.0;
			consGridBagConstraints122.gridy = 2;
			consGridBagConstraints122.gridx = 0;
			consGridBagConstraints122.insets = new java.awt.Insets(5,5,5,5);
			consGridBagConstraints112.fill = java.awt.GridBagConstraints.BOTH;
			consGridBagConstraints112.weighty = 1.0;
			consGridBagConstraints112.weightx = 1.0;
			consGridBagConstraints112.gridy = 0;
			consGridBagConstraints112.gridx = 0;
			consGridBagConstraints112.insets = new java.awt.Insets(5,5,0,5);
			urlWarnCP.setLayout(new java.awt.GridBagLayout());
			urlWarnCP.add(getUrlWarnTA(), consGridBagConstraints112);
			urlWarnCP.add(getUrlWarnBtnPnl(), consGridBagConstraints122);
		}
		return urlWarnCP;
	}
	/**
	 * This method initializes jDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private javax.swing.JDialog getUrlWarnDlg() {
		if(urlWarnDlg == null) {
			urlWarnDlg = new javax.swing.JDialog(this);
			urlWarnDlg.setContentPane(getUrlWarnCP());
			urlWarnDlg.setSize(321, 235);
			urlWarnDlg.setTitle("Warning");
			urlWarnDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			urlWarnDlg.setModal(true);
		}
		return urlWarnDlg;
	}
	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private javax.swing.JTextArea getUrlWarnTA() {
		if(urlWarnTA == null) {
			urlWarnTA = new javax.swing.JTextArea();
			urlWarnTA.setLineWrap(true);
			urlWarnTA.setText("You are requesting a URL for this specific meeting, but have not included a password and have not enabled cold calling. As a result, only invited users will be able to use this URL successfully. Press Ok to continue anyway. Press Enable to enable cold calling to allow anyone to use this URL (an owner will be prompted to allow the requesting user to enter the meeting).");
			urlWarnTA.setWrapStyleWord(true);
			urlWarnTA.setEditable(false);
		}
		return urlWarnTA;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getUrlWarnBtnPnl() {
		if(urlWarnBtnPnl == null) {
			urlWarnBtnPnl = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints132 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints142 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints152 = new java.awt.GridBagConstraints();
			consGridBagConstraints132.gridy = 0;
			consGridBagConstraints132.gridx = 0;
			consGridBagConstraints132.insets = new java.awt.Insets(0,0,0,5);
			consGridBagConstraints142.gridy = 0;
			consGridBagConstraints142.gridx = 1;
			consGridBagConstraints142.insets = new java.awt.Insets(0,5,0,5);
			consGridBagConstraints152.gridy = 0;
			consGridBagConstraints152.gridx = 2;
			consGridBagConstraints152.insets = new java.awt.Insets(0,5,0,0);
			urlWarnBtnPnl.setLayout(new java.awt.GridBagLayout());
			urlWarnBtnPnl.add(getUrlWarnOkBtn(), consGridBagConstraints132);
			urlWarnBtnPnl.add(getUrlWarnEnableBtn(), consGridBagConstraints142);
			urlWarnBtnPnl.add(getUrlWarnCanBtn(), consGridBagConstraints152);
		}
		return urlWarnBtnPnl;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUrlWarnOkBtn() {
		if(urlWarnOkBtn == null) {
			urlWarnOkBtn = new javax.swing.JButton();
			urlWarnOkBtn.setText("Ok");
			urlWarnOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUrlWarnDlg().dispose();
					DSMPProto p = DSMPGenerator.getMeetingURL((byte) 0,
										getUrlPasswordCB().isSelected(),
										getUrlMeetingCB().isSelected(),
										getUrlSendCB().isSelected(),
										dispatcher.meetingID);
					dispatcher.dispatchProtocol(p);
				}
			});
		}
		return urlWarnOkBtn;
	}
	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUrlWarnEnableBtn() {
		if(urlWarnEnableBtn == null) {
			urlWarnEnableBtn = new javax.swing.JButton();
			urlWarnEnableBtn.setText("Enable");
			urlWarnEnableBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUrlWarnDlg().dispose();
					DSMPProto p = DSMPGenerator.setMeetingOption((byte) 0,dispatcher.meetingID,
										DSMPGenerator.COLDCALL_OPTION,"true");
					dispatcher.dispatchProtocol(p);
					p = DSMPGenerator.getMeetingURL((byte) 0,
										getUrlPasswordCB().isSelected(),
										getUrlMeetingCB().isSelected(),
										getUrlSendCB().isSelected(),
										dispatcher.meetingID);
					dispatcher.dispatchProtocol(p);
				}
			});
		}
		return urlWarnEnableBtn;
	}
	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getUrlWarnCanBtn() {
		if(urlWarnCanBtn == null) {
			urlWarnCanBtn = new javax.swing.JButton();
			urlWarnCanBtn.setText("Cancel");
			urlWarnCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getUrlWarnDlg().dispose();
				}
			});
		}
		return urlWarnCanBtn;
	}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCreateIconMI() {
		if(createIconMI == null) {
			createIconMI = new javax.swing.JMenuItem();
			createIconMI.setText("Create Icon...");
			createIconMI.setToolTipText("Create a Windows Desktop Icon");
			createIconMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int response = JOptionPane.showConfirmDialog(MeetingViewer.this,
										"Create a Web Conferences icon on your desktop?",
										"Create Icon",
										JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE);

					if (response == JOptionPane.YES_OPTION) {
						// Get the start script to be used by the icon.
						File script = new File("startds.js");
						
						if (script.exists()) {
							// Current directory is the absolute path of the script
							// trimmed of the ending "\startds.js";
							String cd = script.getAbsolutePath();
							cd = cd.substring(0,cd.length()-11);

							try {
								// Create the JScript file to execute.
								FileWriter js = new FileWriter("conficon.js");
								js.write("try {\n");
								js.write("  var WSO = new ActiveXObject(\"WScript.shell\");\n");
								js.write("  var tenv = WSO.Environment(\"Process\");\n");
								js.write("  var desktoppath = WSO.SpecialFolders.Item(\"Desktop\");\n");
								js.write("  var link = WSO.CreateShortcut(desktoppath + \"\\\\ICC Web Conferences.lnk\");\n");
								js.write("  link.WorkingDirectory = \"");

								// Need working directory to have \\ separaters.
								StringTokenizer t = new StringTokenizer(cd,"\\");
								js.write(t.nextToken());
								while (t.hasMoreTokens()) {
									js.write("\\\\");
									js.write(t.nextToken());
								}

								js.write("\";\n");
								js.write("  link.TargetPath = \"wscript.exe\";\n");
								js.write("  link.Arguments = \"startds.js -conf\";\n");
								js.write("  link.Description = \"Launch IBM Customer Connect Web Conferences\";\n");
								js.write("  link.IconLocation = tenv(\"SystemRoot\") + \"\\\\system32\\\\SHELL32.dll, 12\";\n");
								js.write("  link.WindowStyle = \"1\";\n");
								js.write("  link.Save();\n");
								js.write("} catch (e) {\n");
								js.write("  WScript.Echo(\"Error encountered while creating desktop shortcut.\\nUnexpected error: \" + e.message);\n");
								js.write("  WScript.QUIT(10);\n");
								js.write("}\n");
								js.write("WScript.Echo(\"Web Conferences icon added to your desktop!\");\n");
								js.write("WScript.QUIT(0);\n");
								js.close();
		
								// Create the icon.
								Process p = Runtime.getRuntime().exec("wscript conficon.js");
							}
							catch (IOException e1) {
								System.out.println(e1.getMessage());
								JOptionPane.showMessageDialog(MeetingViewer.this,
													"An error occurred while preparing to create icon.",
													"Error",
													JOptionPane.ERROR_MESSAGE);
							}
						}
						else {
							JOptionPane.showMessageDialog(MeetingViewer.this,
												"A required script, startds.js, is missing.",
												"Error",
												JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return createIconMI;
	}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
