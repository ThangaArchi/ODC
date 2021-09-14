package oem.edge.ed.odc.applet;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

//import com.ibm.sslight.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.tunnel.applet.*;

/**
 * Insert the type's description here.
 * Creation date: (8/22/2001 9:10:08 AM)
 * @author: Mike Zarnick
 */
public class LaunchApp extends JFrame implements Runnable, DocumentListener {

	class MsgRouter extends Thread {
		MultiPipeOutputStream out = null;
		MultiPipeInputStream in = null;
		MsgRouter(MultiPipeOutputStream out) {
			this.out = out;
		}
		MsgRouter(MultiPipeInputStream in) {
			this.in = in;
		}
		public void run() {
			int i = 0;
			int j = 0;

			try {
				if (in == null)
					in = new MultiPipeInputStream(out);

				BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
				PrintWriter fo = null;

				String line;

				while ((line = rdr.readLine()) != null) {
					boolean useFile = getWriteFileMI().getState();

					// Process tunnel command requests from the launched application
					if (line.startsWith("tunnel command: ")) {
						String command = line.substring(16);
						if (command.equals("show")) {
							LaunchApp.this.setVisible(true);
							LaunchApp.this.toFront();
						}
						else if (command.equals("hide")) {
							LaunchApp.this.setVisible(false);
						}
						else if (command.equals("app ended")) {
							// Application ended. Set systemExiting so that when tunnel ends,
							// we exit.
							systemExiting = true;
						}
					}
					else {
						if (! useFile) {
							if (fo != null) {
								fo.close();
								fo = null;
							}
	
							try {
								j = getMsgTA().getDocument().getLength();
	
								if (j > 1048576) {
									int k = getMsgTA().getLineOfOffset(524288);
									k = getMsgTA().getLineEndOffset(k);
									getMsgTA().replaceRange("...\n" + f.format(new Date()) + "\n",0,k);
									j = getMsgTA().getDocument().getLength();
								}
	
								i = getMsgTA().getCaretPosition();
	
								getMsgTA().append(line);
								getMsgTA().append("\n");
	
								if (i == j) {
									j += line.length() + 1;
									getMsgTA().setCaretPosition(j);
								}
							}
							catch (Exception e) {
								getMsgTA().setText("...error pruning text...\n" + f.format(new Date()) + "\n");
							}
						}
	
						if (useFile) {
							if (fo == null)
								fo = new PrintWriter(new FileOutputStream("debug.out"));
	
							fo.println(line);
							fo.flush();
						}
					}
				}
				rdr.close();

				if (fo != null)
					fo.close();
			}
			catch (Exception e) {
				getMsgTA().append("Fatal error in MsgRouter: " + e.getMessage());
			}
		}
	}
	protected boolean inapplet = false;  // JMC
	static private String NOUPDATE_OPT = "-CH_NOUPDATE";
	static private String DEBUG_OPT = "-CH_DEBUG";
	static private String NOSTR_OPT = "-CH_NOSTREAM";
	static private String TOKEN_OPT = "-CH_TOKEN";
	static private String HOSTM_OPT = "-CH_HOSTINGMACHINE";
	static private String CMD_OPT = "-CH_TUNNELCOMMAND";
	static private String PROJ_OPT = "-CH_PROJECT";
	static private String CALLMTG_OPT = "-CH_CALL_MEETINGID";
	static private String CALLUSER_OPT = "-CH_CALL_USERID";
	static private String CALLPASS_OPT = "-CH_CALL_PASSWORD";
	static private SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.US);
	private boolean isWin;
	private boolean login = false;
	private String callmeeting = null;
	private String calluser = null;
	private String callpassword = null;
	private String token = null;
	private String title = null;
	private int debugPwd = 0;
	private File textFile = null;
	private boolean isDOCTunnelCommand = false; //subu 04/18/03
	private String CertPWD = null; // subu 04/18/03
	private String[]  tunnelArgs = null;
	private long tstStart;
	private long tstMidStart;
	private long tstChunkSize;
	private long tstTotalSize;
	private int tstPhase = 0;
	private boolean systemExiting = false;
	private Thread statsThread = null;
	private Thread speedThread = null;
	private JMenuItem ivjAboutMI = null;
	private JMenuItem ivjClearMI = null;
	private JMenu ivjDebugM = null;  // @jve:visual-info  decl-index=0 visual-constraint="574,119"
	private JMenuItem ivjDropMI = null;
	private JMenuItem ivjDumpMI = null;
	private JMenuItem ivjExitMI = null;
	private JMenu ivjFileM = null;  // @jve:visual-info  decl-index=0 visual-constraint="560,735"
	private JMenu ivjHelpM = null;  // @jve:visual-info  decl-index=0 visual-constraint="575,34"
	private JPanel ivjJFrameContentPane = null;
	private JPanel ivjJPanel1 = null;
	private JScrollPane ivjJScrollPane1 = null;
	private JSeparator ivjJSeparator1 = null;
	private JSeparator ivjJSeparator2 = null;
	private JSeparator ivjJSeparator3 = null;
	private JSeparator ivjJSeparator4 = null;
	private JMenuBar ivjLaunchDSCJMenuBar = null;
	private JMenuItem ivjPingMI = null;
	private JMenuItem ivjSaveAsMI = null;
	private JMenuItem ivjSaveMI = null;
	private JMenuItem ivjSenderMI = null;
	private JMenuItem ivjSocksMI = null;
	private JMenuItem ivjStartMI = null;
	private JMenu ivjStatsM = null;  // @jve:visual-info  decl-index=0 visual-constraint="574,338"
	private JCheckBoxMenuItem ivjStatsMI = null;
	private JMenuItem ivjStopMI = null;
	private JMenu ivjTunnelM = null;  // @jve:visual-info  decl-index=0 visual-constraint="563,536"
	private JCheckBoxMenuItem ivjVerboseMI = null;
	private JFileChooser ivjFileDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="348,849"
	private AppletSessionManager ivjManager = null;  // @jve:visual-info  decl-index=0 visual-constraint="589,905"
	private JTextArea ivjMsgTA = null;
	private HttpTunnelClient ivjTunnel = null;  // @jve:visual-info  decl-index=0 visual-constraint="590,972"
	private AboutPnl ivjAboutPnl = null;
	private PerformanceIndicator ivjPerformanceIndicator = null;
	private JCheckBoxMenuItem ivjAutoPingMI = null;
	private JMenuItem ivjCitrixMI = null;
	private JCheckBoxMenuItem ivjCompressMI = null;
	private ConfigMgr ivjConfigMgr = null;  // @jve:visual-info  decl-index=0 visual-constraint="741,654"
	private JMenuItem ivjFtpMI = null;
	private JSeparator ivjJSeparator5 = null;
	private JSeparator ivjJSeparator6 = null;
	private JCheckBoxMenuItem ivjKeepAliveMI = null;
	private JMenu ivjLevelM = null;
	private JCheckBoxMenuItem ivjNormalMI = null;
	private JMenuItem ivjRealMI = null;
	private JMenuItem ivjResetMI = null;
	private JMenu ivjSpeedM = null;
	private JMenuItem ivjDebug2MI = null;
	private JMenuItem ivjDebug3MI = null;
	private JMenuItem ivjDebug4MI = null;
	private JMenuItem ivjDebug5MI = null;
	private JMenuItem ivjDebugMI = null;
	private JMenuItem ivjInfo2MI = null;
	private JMenuItem ivjInfo3MI = null;
	private JMenuItem ivjInfo4MI = null;
	private JMenuItem ivjInfo5MI = null;
	private JMenuItem ivjInfoMI = null;
	private JSeparator ivjJSeparator8 = null;
	private JMenuItem ivjNetworkMI = null;
	private JMenuItem ivjTunnelMI = null;
	private JCheckBoxMenuItem ivjWriteFileMI = null;
	private BorderFactory ivjBorderFactory = null;  // @jve:visual-info  decl-index=0 visual-constraint="66,452"
	private JSeparator ivjFtpSep = null;
	private JPanel ivjJPanel2 = null;
	private JButton ivjLoginCanBtn = null;
	private JButton ivjLoginOkBtn = null;
	private JLabel ivjPwdLbl = null;
	private JPasswordField ivjPwdTF = null;
	private JLabel ivjUserLbl = null;
	private JTextField ivjUserTF = null;
	private JLabel ivjLoginLbl = null;
	private JMenuItem ivjCreateXChannelMI = null;
	private JButton ivjCertOkBtn = null;
	private JPasswordField ivjCertPF = null;
	private JButton ivjJButton1 = null;
	private JLabel ivjJLabel1 = null;
	private JLabel ivjJLabel11 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel ivjJLabel3 = null;
	private JLabel ivjJLabel4 = null;
	private JPanel ivjJPanel3 = null;
	private JScrollPane ivjMsgDlgSP = null;
	private JTextArea ivjMsgDlgTA = null;
	private JButton ivjMsgOkBtn = null;
	private JButton ivjXChanCanBtn = null;
	private JButton ivjXChanOkBtn = null;
	private JPasswordField ivjXChanPF = null;
	private JPanel ivjAboutCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="43,835"
	private JPanel ivjCertCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="477,1117"
	private JPanel ivjLoginCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="48,1123"
	private JPanel ivjMsgCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="54,1381"
	private JPanel ivjPwdSubCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="47,1768"
	private JPanel ivjXChanCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="479,1439"
	private JDialog ivjAboutDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="26,1272"
	private JDialog ivjCertDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="272,644"
	private JDialog ivjLoginDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="21,644"
	private JDialog ivjMsgDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="21,837"
	private JDialog ivjPwdSubDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="237,1097"
	private JDialog ivjXChanDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="579,1097"
	private JButton ivjDebugCanBtn = null;
	private JDialog ivjDebugDlg = null;  //  @jve:visual-info  decl-index=0 visual-constraint="25,1098"
	private JButton ivjDebugOkBtn = null;
	private JTextField ivjDebugTF = null;
	private JLabel ivjJLabel12 = null;
	private JPanel ivjJPanel11 = null;
	private JPanel ivjDebugCP = null;  // @jve:visual-info  decl-index=0 visual-constraint="489,1779"
	private JPanel ivjJPanel4 = null;
	private ActionListener managerActionListener;
	private javax.swing.JMenuItem closeMI = null;
/**
 * LaunchDSC constructor comment.
 */
public LaunchApp() {
	super();
	initialize();
}
/**
 * LaunchDSC constructor comment.
 * @param title java.lang.String
 */
public LaunchApp(String title) {
	super(title);
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public void begin(String[] args) {
	// Show the about panel...
	displayAbout();

	// Hide the Start Xterm button if on a windows platform.	
	isWin = System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1;

	// Show the frame centered on the screen.
	Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize();
	Rectangle win = getBounds();
	setLocation((screen.width - win.width) / 2, (screen.height - win.height) / 2);
	setVisible(true);
	//getStatsMI().setState(true);

	// Insert code to start the application here.
	String url = null;
	boolean nostream = false;
	boolean doUpdates = true;
	boolean debug = false;
	String path = null;
	String display = null;
	String tunnelcommand = null;
	String hostingmachine = null;
	String project = null;

	// Route stdout and stderr to the message textarea.
	MultiPipeOutputStream stdout = new MultiPipeOutputStream();
	PrintStream stdoutStream = new PrintStream(stdout);
	System.setOut(stdoutStream);
	MultiPipeOutputStream stderr = new MultiPipeOutputStream();
	PrintStream stderrStream = new PrintStream(stderr);
	System.setErr(stderrStream);

	try {
		MultiPipeInputStream stdin = new MultiPipeInputStream();
		stdout.connect(stdin);
		stderr.connect(stdin);
		Thread outMsgRouter = new MsgRouter(stdin);
		outMsgRouter.start();
	}
	catch (Exception e) {
		Thread outMsgRouter = new MsgRouter(stdout);
		outMsgRouter.start();
	}

	System.out.println(f.format(new Date()));

	try {
		System.out.println("Reading program parameters:");

		// Process the parameters...
		BufferedReader rdr = null;

		// We are being invoked as a helper application. Open the data file
		// and read in the first line (which was destined to the launch code).
		if (!inapplet) { // JMC
			if (args.length > 0) {
				FileReader file = new FileReader(args[0]);
				rdr = new BufferedReader(file);
				rdr.readLine();
			}

			// We are being launched by the signed applet. Attach standard input to
			// the reader.
			else {
				InputStreamReader data = new InputStreamReader(System.in);
				rdr = new BufferedReader(data);
			}
		}

		// JMC next 4 was      String line = rdr.readLine();
		String line;                         // JMC
		int nexti = 0;                       // JMC
		if (inapplet) line = args[nexti++];  // JMC
		else          line = rdr.readLine(); // JMC

		while (line != null) {
			line = line.trim();
			System.out.println(line);
			if (line.startsWith("-URL")) {
				String arg = null;
				if (line.length() > 4) arg = line.substring(4).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter -URL requires a valid URL value.");
					return;
				}

				url = arg;
			} else if (line.equals(NOSTR_OPT)) {
				nostream = true;
			} else if (line.equals(NOUPDATE_OPT)) {
				doUpdates = false;
			} else if (line.equals(DEBUG_OPT)) {
				debug = true;
			} else if (line.startsWith(CMD_OPT)) {
				String arg = null;
				if (line.length() > CMD_OPT.length()) arg = line.substring(CMD_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + CMD_OPT + " requires a value.");
					return;
				}

				tunnelcommand = arg;
			} else if (line.startsWith(HOSTM_OPT)) {
				String arg = null;
				if (line.length() > HOSTM_OPT.length()) arg = line.substring(HOSTM_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + HOSTM_OPT + " requires a value.");
					return;
				}

				hostingmachine = arg;
			} else if (line.startsWith(TOKEN_OPT)) {
				String arg = null;
				if (line.length() > TOKEN_OPT.length()) arg = line.substring(TOKEN_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + TOKEN_OPT + " requires a value.");
					return;
				}

				token = arg;
			} else if (line.startsWith(PROJ_OPT)) {
				String arg = null;
				if (line.length() > PROJ_OPT.length()) arg = line.substring(PROJ_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + PROJ_OPT + " requires a value.");
					return;
				}

				project = arg;
			} else if (line.startsWith(CALLMTG_OPT)) {
				String arg = null;
				if (line.length() > CALLMTG_OPT.length()) arg = line.substring(CALLMTG_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + CALLMTG_OPT + " requires a value.");
					return;
				}

				callmeeting = arg;
			} else if (line.startsWith(CALLPASS_OPT)) {
				String arg = null;
				if (line.length() > CALLPASS_OPT.length()) arg = line.substring(CALLPASS_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + CALLPASS_OPT + " requires a value.");
					return;
				}

				callpassword = arg;
			} else if (line.startsWith(CALLUSER_OPT)) {
				String arg = null;
				if (line.length() > CALLUSER_OPT.length()) arg = line.substring(CALLUSER_OPT.length()).trim();
				if (arg == null || arg.length() == 0) {
					syntax("Parameter " + CALLUSER_OPT + " requires a value.");
					return;
				}

				calluser = arg;
			} else if (line.startsWith("-THE_END")) {
				// Caboose, ignore it.
			} else {
				syntax("Bad parm = '" + line + "'\n");
				return;
			}

			if (line.startsWith("-THE_END"))
				line = null;
			else if (inapplet) //JMC
				line = args[nexti++]; //JMC
			else
				line = rdr.readLine();
		}
	}
	catch (Exception e) {
		System.out.println("Warning: error occurred while reading parameter file!");
		System.out.println(e.getMessage());
	}

	// Remove debug menu item?
	if (! debug) {
		getLaunchDSCJMenuBar().remove(getDebugM());
		validate();
	}

	// Check for updates and possibly restart?
	if (doUpdates) {
		CodeUpdaterGui c = new CodeUpdaterGui(this,url);
		int result = c.update();
		if (result != CodeUpdaterGui.GOOD) {
			System.exit(result == CodeUpdaterGui.UPDATE ? 200 : 1);
		}
	}

	// We need the ini file. Can't leave home without it...

	// Open the ini file to extract anything else we need:
	try {
		// JMC, check inapplet
		setConfigMgr(new ConfigMgr(inapplet?null:"edesign.ini",this)); //JMC
	}
	catch (Exception e) {
		syntax("Unable to read file edesign.ini!");
		e.printStackTrace(System.out);   // JMC
		return;
	}

	if (!inapplet) { // JMC
		// Create the ICA template file
		try {
			createIcaTemplate();
		}
		catch (Exception e) {
			syntax("Unable to create ICA template file template.ica!");
			return;
		}

		// Create the command-line startup files.
		try {
			if (isWin) {
				createDboxBat();
				createCmdlineBat();
				createCmdlineScript();
			}
			else {
				createDboxShell();
				createCmdlineShell();
			}
		}
		catch (Exception e) {
			syntax("Unable to create Dropbox CMD line shell!");
			return;
		}
	}

	// If the ODC server url was not provided, get it from the ini file.
	if (url == null) {
		syntax("DSC server -URL URL not specified!");
		return;
	}

	boolean isDSH = tunnelcommand.equalsIgnoreCase("DSH");
	boolean isEDU = tunnelcommand.equalsIgnoreCase("EDU");
	boolean isXFR = tunnelcommand.equalsIgnoreCase("XFR");
	boolean isNEWODC = tunnelcommand.equalsIgnoreCase("NEWODC");

	// Set the right name...
	if (tunnelcommand.equalsIgnoreCase("ODC"))
		title = "Web Conference - ";

	else if (isNEWODC)
		title = "Web Conference Multiplatform - ";

	else if (isEDU)
		title = "Classrooms - ";

	else if (tunnelcommand.equalsIgnoreCase("IM"))
		title = "Instant Messaging - ";

	else if (tunnelcommand.equalsIgnoreCase("RM"))
		title = "Media Streamer - ";

	else if (tunnelcommand.equalsIgnoreCase("DOC")){
		isDOCTunnelCommand = true;
		title = "DesktopOnCall - ";
	}

	else if (isXFR)
		title = "Drop Box Service - ";

	else if (tunnelcommand.equalsIgnoreCase("FDR"))
		title = "Verification Service - ";

	else
		title = "Hosting - ";

	getCloseMI().setEnabled(isXFR || isNEWODC);

	if (! isDSH || ! isEDU) {
		if (! isDSH) {
			getTunnelM().remove(getFtpMI());
		}
		if (! isEDU){
			getTunnelM().remove(getCreateXChannelMI());
		}
		if (! isDSH && ! isEDU) {
			getTunnelM().remove(getFtpSep());
		}
		getTunnelM().validate();
	}

	setTitle(title + "Disconnected");

	// Ok, now create the tunnel launch arguments.
	int i = 6;
	if (nostream) i++;
	if (hostingmachine != null) i += 2;
	if (project != null) i += 2;

	tunnelArgs = new String[i];
	i = 0;

	tunnelArgs[i++] = "-URL";
	tunnelArgs[i++] = url;
	tunnelArgs[i++] = CMD_OPT;
	tunnelArgs[i++] = tunnelcommand;
	tunnelArgs[i++] = TOKEN_OPT;
	tunnelArgs[i++] = token;
	if (hostingmachine != null) {
		tunnelArgs[i++] = HOSTM_OPT;
		tunnelArgs[i++] = hostingmachine;
	}
	if (project != null) {
		tunnelArgs[i++] = PROJ_OPT;
		tunnelArgs[i++] = project;
	}
	if (nostream)
		tunnelArgs[i++] = NOSTR_OPT;

	// Launch the tunnel.
	startTunnel();
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/2004 3:22:02 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void changedUpdate(DocumentEvent e) {
	textUpdate(e);
}
/**
 * Comment
 */
public void clearText() {
	getMsgTA().setText("");
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public static void createDboxBat() throws IOException {
	File f = new File("dropboxftp.bat");

	if (! f.exists()) {
		FileWriter out = new FileWriter(f);
		PrintWriter print = new PrintWriter(out);
		print.println("@ECHO OFF");
		print.println("REM");
		print.println("REM Launch script for Dropbox cmdline client");
		print.println("REM");
		print.println("REM   Requires INSTALLPOINT to be set to a valid/customized CSCC installation");
		print.println("REM   Also uses cscript ");
		print.println("");
		print.println("REM============== CUSTOMIZE INSTALLPOINT ======================");
		print.println("REM Install point should point to valid CSCC installation");
		print.println("REM============================================================");
		print.println("set INSTALLPOINT=" + System.getProperty("user.dir"));
		print.println("");
		print.println("REM");
		print.println("REM Try to find a file that does not exist");
		print.println("REM");
		print.println("set mycallable=%TEMP%\\cscc_%RANDOM%.bat");
		print.println("if not exist \"%mycallable%\" goto restart");
		print.println("set mycallable=%TEMP%\\cscc_%RANDOM%.bat");
		print.println("if not exist \"%mycallable%\" goto restart");
		print.println("set mycallable=%TEMP%\\cscc_%RANDOM%.bat");
		print.println("");
		print.println(":restart");
		print.println("");
		print.println("REM");
		print.println("REM See if any updates need to be applied");
		print.println("REM");
		print.println("if not exist \"%INSTALLPOINT%\\updates\\edesign.ini.new\" goto noupdates");
		print.println("");
		print.println("echo Applying updates");
		print.println("");
		print.println("copy /Y \"%INSTALLPOINT%\\updates\\*.*\" \"%INSTALLPOINT%\"");
		print.println("");
		print.println("if errorlevel 1 goto updatefailed");
		print.println("");
		print.println("type \"%INSTALLPOINT%\\edesign.ini\" >\"%INSTALLPOINT%\\edesign.ini.update\"");
		print.println("type \"%INSTALLPOINT%\\edesign.ini.new\" >>\"%INSTALLPOINT%\\edesign.ini.update\"");
		print.println("copy /Y \"%INSTALLPOINT%\\edesign.ini.update\" \"%INSTALLPOINT%\\edesign.ini\"");
		print.println("");
		print.println("if errorlevel 1 goto updatefailed");
		print.println("");
		print.println("del \"%INSTALLPOINT%\\edesign.ini.new\"");
		print.println("del \"%INSTALLPOINT%\\edesign.ini.update\"");
		print.println("del /Q \"%INSTALLPOINT%\\updates\\*.*\"");
		print.println("");
		print.println("echo Updates applied.");
		print.println("");
		print.println("goto noupdates");
		print.println("");
		print.println(":updatefailed");
		print.println("");
		print.println("echo Files that need to be updated are locked.");
		print.println("echo Please close other instances of Dropbox applications to");
		print.println("echo enable updates to be applied.");
		print.println("");
		print.println(":noupdates");
		print.println("");
		print.println("cscript //nologo \"%INSTALLPOINT%\\csccinfo.js\" \"%INSTALLPOINT%\\edesign.ini\" > \"%mycallable%\"");
		print.println("");
		print.println("if exist \"%mycallable%\" call \"%mycallable%\" %1 %2 %3 %4 %5 %6 %7 %8 %9");
		print.println("");
		print.println("if errorlevel 200 goto restart");
		print.println("");
		print.println("if exist \"%mycallable%\" del \"%mycallable%\"");
		print.close();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public static void createDboxShell() throws IOException {
	File f = new File("dropboxftp");

	if (! f.exists()) {
		FileWriter out = new FileWriter(f);
		PrintWriter print = new PrintWriter(out);
		print.println("#!/bin/sh");
		print.println("#");
		print.println("# dropboxftp startup");
		print.println("#");
		print.println("");
		print.println("#");
		print.println("# Legacy");
		print.println("#");
		print.println("setenv() { eval $1=\"$2\"; export $1; }");
		print.println("");
		print.println("#");
		print.println("# Use envvars to callout actual binary. Since we have to assume");
		print.println("#  Bourne Shell, can't simply unalias (that is the issue here ...");
		print.println("#  someone has rm aliased to have confirmations ... sigh)");
		print.println("#");
		print.println("setenv GREP_CMD grep");
		print.println("setenv SED_CMD  sed");
		print.println("");
		print.println("if   [ -x /bin/grep ] ; then");
		print.println("  setenv GREP_CMD   /bin/grep");
		print.println("elif [ -x /usr/bin/grep ] ; then");
		print.println("  setenv GREP_CMD   /usr/bin/grep");
		print.println("fi");
		print.println("");
		print.println("if   [ -x /bin/sed ] ; then");
		print.println("  setenv SED_CMD   /bin/sed");
		print.println("elif [ -x /usr/bin/sed ] ; then");
		print.println("  setenv SED_CMD   /usr/bin/sed");
		print.println("fi");		print.println("");
		print.println("# Determine the program installation point.");
		print.println("");
		print.println("# Absolute path is available? Remove the trailing name.");
		print.println("PGM=$0");
		print.println("");
		print.println("# if [ \"x${PGM##/*}\" = \"x\" ]");
		print.println("echo $PGM | $GREP_CMD \"^/\" >/dev/null");
		print.println("if [ $? = 0 ]");
		print.println("then");
		print.println("  # InstallPoint=${PGM%/*}");
		print.println("  InstallPoint=`echo $PGM | $SED_CMD 's!/[^/]*$!!'`");
		print.println("");
		print.println("# Relative path has no directory? Use current directory.");
		print.println("else");
		print.println("  # if [ \"${PGM%/*}\" = \"$PGM\" ]");
		print.println("  echo $PGM | $GREP_CMD \"/\" >/dev/null");
		print.println("  if [ $? != 0 ]");
		print.println("  then");
		print.println("    InstallPoint=`pwd`");
		print.println("");
		print.println("  # Relative path includes a directory.");
		print.println("  else");
		print.println("    CWD=`pwd`");
		print.println("	   # cd \"${PGM%/*}\"");
		print.println("	   cd `echo $PGM | $SED_CMD 's!/[^/]*$!!'`");
		print.println("	   InstallPoint=`pwd`");
		print.println("	   cd \"$CWD\"");
		print.println("  fi");
		print.println("fi");
		print.println("");
		print.println("$InstallPoint/startdsc.sh -dropboxftp $*");

		print.close();

		String[] app = new String[2];
		app[0] = "/bin/sh";
		app[1] = "-s";

		Process p = Runtime.getRuntime().exec(app);

		PrintStream in = new PrintStream(p.getOutputStream());
		in.println("chmod 755 " + f.toString());
		in.println("echo DONE");
		in.close();
		
		BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = rdr.readLine();
		while (line != null && ! line.startsWith("DONE")) {
			line = rdr.readLine();
		}
		rdr.close();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public void createIcaTemplate() throws IOException {
	File template = new File("template.ica");

	if (! template.exists()) {
		FileWriter out = new FileWriter(template);
		PrintWriter print = new PrintWriter(out);
		print.println(";********************************************************************");
		print.println(";");
		print.println("; Client workstation configurations for Citrix ICA Clients");
		print.println(";");
		print.println("; You may make any other alterations, To restore this file to its");
		print.println("; original form, delete it and restart the Hosting, Conference or");
		print.println("; Classroom service software.");
		print.println(";");
		print.println(";********************************************************************");
		print.println("[ApplicationServers]");
		print.println("REPLACETITLE=");
		print.println("");
		print.println("[REPLACETITLE]");
		print.println("DesiredVRES=REPLACEHEIGHT");
		print.println("DesiredHRES=REPLACEWIDTH");
		print.println("Address=REPLACEADDR");
		print.println("ICAPortNumber=REPLACEICAPORT");
		print.println("InitialProgram=REPLACEINITIALPGM");
		print.println("WinStationDriver=ICA 3.0");
		print.println("TransportDriver=TCP/IP");
		print.println("AudioBandwidthLimit=1");
		print.println("ClientAudio=Off");
		print.println("UseDefaultSettingForColormap=No");
		print.println("UseFullScreen=No");
		print.println("UseAlternateAddress=0");
		print.println("KeyboardTimer=100");
		print.println("MouseTimer=50");
		print.println("DesiredColor=2");
		print.println("TcpBrowserAddress=");
		print.println("DesiredWinType=8");
		print.println("TWIMode=On");
		print.println("");
		print.println("[WFClient]");
		print.println("Version = 2");
		print.println("KeyboardLayout = (User Profile)");
		print.println("KeyboardMappingFile = automatic.kbd");
		print.println("KeyboardDescription = Automatic (User Profile)");
		print.println("ClientDrive=Off");
		print.println("ClientPrinter=Off");
		print.println("ClientManagement=Off");
		print.println("ClientComm=Off");
		print.println("MouseDoubleClickTimer=");
		print.println("MouseDoubleClickWidth=");
		print.println("MouseDoubleClickHeight=");
		print.println("PersistentCacheEnabled=On");
		print.println("PersistentCacheSize=42935633");
		print.println("PersistentCacheMinBitmap=8192");
		print.println("PersistentCachePath=");
		print.println("");
		print.println("[Thinwire3.0]");
		print.println("UseServerRedraw=Yes");
		print.println("AllowBackingStore=Yes");
		print.println("");
		print.println("[Xdpy - The XFree86 Project, Inc]");
		print.println("All=Caps Scroll Num");
		print.println("");
		print.println("[Xdpy - X11/NeWS - Sun Microsystems Inc.]");
		print.println("All=Caps Scroll Num");
		print.println("");
		print.println("[Xdpy - DECWINDOWS Digital Equipment Corporation Digital UNIX V4.0]");
		print.println("All=Caps Scroll Num");
		print.println("");
		print.println("[Xdpy - The Santa Cruz Operation]");
		print.println("6000-=Caps Scroll Num");
		print.println("");
		print.println("[Xdpy - Silicon Graphics]");
		print.println("5000-=Caps Scroll Num");
		print.close();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public static void createCmdlineBat() throws IOException {
	File f = new File("startdsc.bat");

	if (! f.exists()) {
		FileWriter out = new FileWriter(f);
		PrintWriter print = new PrintWriter(out);
		print.println("@ECHO OFF");
		print.println("REM");
		print.println("REM Launch script for Dropbox and Web Conference GUI clients");
		print.println("REM");
		print.println("REM   Requires INSTALLPOINT to be set to a valid/customized CSDS installation");
		print.println("REM   Also uses cscript ");
		print.println("");
		print.println("REM============== CUSTOMIZE INSTALLPOINT ======================");
		print.println("REM Install point should point to valid CSDS installation");
		print.println("REM============================================================");
		print.println("set INSTALLPOINT=" + System.getProperty("user.dir"));
		print.println("");
		print.println("cscript //nologo \"%INSTALLPOINT%\\startds.js\" %*");
		print.close();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public static void createCmdlineScript() throws IOException {
	File f = new File("startds.js");

	if (! f.exists()) {
		FileWriter out = new FileWriter(f);
		PrintWriter print = new PrintWriter(out);
		print.println("//");
		print.println("// Wrapper script to apply updates and invoke launchds as needed");
		print.println("//");
		print.println("");
		print.println("// Are we running in command line mode?");		print.println("var PROC = WScript.FullName;");
		print.println("var isCmdLine = false;");
		print.println("");
		print.println("if (PROC.lastIndexOf(\"cscript.exe\") == PROC.length - 11) {");
		print.println("  isCmdLine = true;");
		print.println("}");
		print.println("");
		print.println("// Determine installation point from script location.");
		print.println("var scriptPath = WScript.ScriptFullName;");
		print.println("var installDir = scriptPath.substring(0,scriptPath.lastIndexOf(WScript.ScriptName));");
		print.println("var updatesDir = \"updates\";");
		print.println("var updatesIni = updatesDir + \"\\\\edesign.ini.new\";");
		print.println("");		print.println("// Prep the arguments for the launch script");
		print.println("var args = WScript.Arguments;");
		print.println("");
		print.println("// Get a shell to run the launcher and change to installation directory");
		print.println("var shell = WScript.CreateObject(\"WScript.Shell\");");
		print.println("shell.CurrentDirectory = installDir;");
		print.println("");
		print.println("// Now, install updates and launch function until no updates are available.");
		print.println("var FSO = new ActiveXObject(\"Scripting.FileSystemObject\");");
		print.println("var rc;");
		print.println("");
		print.println("do {");
		print.println("  // Updates to install?");
		print.println("  if (FSO.FileExists(updatesIni)) {");
		print.println("    // Copy the updates to the current (installation) directory.");
		print.println("    if (isCmdLine) WScript.echo(\"Applying new updates.\");");
		print.println("    var updates = FSO.GetFolder(updatesDir);");
		print.println("    var list = new Enumerator(updates.Files);");
		print.println("    while (! list.atEnd()) {");
		print.println("      var file = list.item();");
		print.println("      file.Copy(file.Name,true);");
		print.println("      list.moveNext();");
		print.println("    }");
		print.println("");
		print.println("    // Apply the update ini file to the real ini file.");
		print.println("");
		print.println("    // Make a copy of the ini file.");
		print.println("    var iniFile = FSO.GetFile(\"edesign.ini\");");
		print.println("	   iniFile.Copy(\"edesign.ini.update\");");
		print.println("");
		print.println("    // Append the updates to the end.");
		print.println("    if (isCmdLine) WScript.echo(\"Updating edesign.ini file.\");");
		print.println("    var iniUpdate = FSO.GetFile(\"edesign.ini.update\");");
		print.println("    var iniNew = FSO.GetFile(\"edesign.ini.new\");");
		print.println("    var writer = iniUpdate.OpenAsTextStream(8); // For appending.");
		print.println("    var reader = iniNew.OpenAsTextStream(1); // For reading.");
		print.println("");
		print.println("    while (! reader.AtEndOfStream) {");
		print.println("      writer.WriteLine(reader.ReadLine());");
		print.println("    }");
		print.println("");
		print.println("    reader.close();");
		print.println("    writer.close();");
		print.println("");
		print.println("    // Make the copy permanent.");
		print.println("    iniFile.Delete(true);");
		print.println("    iniUpdate.Move(\"edesign.ini\");");
		print.println("");
		print.println("    // Remove the temporary ini file.");
		print.println("    if (isCmdLine) WScript.echo(\"Updates applied, cleaning up and restarting.\");");
		print.println("    iniNew.Delete(true);");
		print.println("");
		print.println("    // Remove the files in the updates directory.");
		print.println("    list = new Enumerator(updates.Files);");
		print.println("    while (! list.atEnd()) {");
		print.println("      var file = list.item();");
		print.println("      file.Delete(true);");
		print.println("      list.moveNext();");
		print.println("    }");
		print.println("  }");
		print.println("");
		print.println("  // Start the app launcher.");
		print.println("");
		print.println("  // Load and evaluate launchds.js. It is expected to be JScript and to define LaunchDS()");
		print.println("  // which takes a WScript.Arguments object as its only parameter.");
		print.println("  var launcher = FSO.OpenTextFile(\"launchds.js\",1);");
		print.println("  var content = launcher.ReadAll();");
		print.println("  eval(content);");
		print.println("  rc = LaunchDS(args);");
		print.println("} while (rc == 200);");
		print.close();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 2:09:47 PM)
 */
public static void createCmdlineShell() throws IOException {
	File f = new File("startdsc.sh");

	if (! f.exists()) {
		FileWriter out = new FileWriter(f);
		PrintWriter print = new PrintWriter(out);
		print.println("#!/bin/sh");
		print.println("#");
		print.println("# dropboxftp, dropbox GUI, web conferences cmdline startup");
		print.println("#");
		print.println("");
		print.println("#");
		print.println("# Legacy");
		print.println("#");
		print.println("setenv() { eval $1=\"$2\"; export $1; }");
		print.println("");
		print.println("#");
		print.println("# Use envvars to callout actual binary. Since we have to assume");
		print.println("#  Bourne Shell, can't simply unalias (that is the issue here ...");
		print.println("#  someone has rm aliased to have confirmations ... sigh)");
		print.println("#");
		print.println("setenv GREP_CMD grep");
		print.println("setenv SED_CMD  sed");
		print.println("");
		print.println("if   [ -x /bin/grep ] ; then");
		print.println("  setenv GREP_CMD   /bin/grep");
		print.println("elif [ -x /usr/bin/grep ] ; then");
		print.println("  setenv GREP_CMD   /usr/bin/grep");
		print.println("fi");
		print.println("");
		print.println("if   [ -x /bin/sed ] ; then");
		print.println("  setenv SED_CMD   /bin/sed");
		print.println("elif [ -x /usr/bin/sed ] ; then");
		print.println("  setenv SED_CMD   /usr/bin/sed");
		print.println("fi");
		print.println("");
		print.println("# Determine the program installation point.");
		print.println("");
		print.println("# Absolute path is available? Remove the trailing name.");
		print.println("PGM=$0");
		print.println("");
		print.println("# if [ \"x${PGM##/*}\" = \"x\" ]");
		print.println("echo $PGM | $GREP_CMD \"^/\" >/dev/null");
		print.println("if [ $? = 0 ]");
		print.println("then");
		print.println("  # InstallPoint=${PGM%/*}");
		print.println("  InstallPoint=`echo $PGM | $SED_CMD 's!/[^/]*$!!'`");
		print.println("");
		print.println("# Relative path has no directory? Use current directory.");
		print.println("else");
		print.println("  # if [ \"${PGM%/*}\" = \"$PGM\" ]");
		print.println("  echo $PGM | $GREP_CMD \"/\" >/dev/null");
		print.println("  if [ $? != 0 ]");
		print.println("  then");
		print.println("    InstallPoint=`pwd`");
		print.println("");
		print.println("  # Relative path includes a directory.");
		print.println("  else");
		print.println("    CWD=`pwd`");
		print.println("	   # cd \"${PGM%/*}\"");
		print.println("	   cd `echo $PGM | $SED_CMD 's!/[^/]*$!!'`");
		print.println("	   InstallPoint=`pwd`");
		print.println("	   cd \"$CWD\"");
		print.println("  fi");
		print.println("fi");
		print.println("");
		print.println("CWD=`pwd`");
		print.println("");
		print.println("RC=200");
		print.println("");
		print.println("while [ $RC = 200 ]");
		print.println("do");
		print.println("  cd $InstallPoint");
		print.println("  if [ -f updates/edesign.ini.new ]");
		print.println("  then");
		print.println("    echo Applying updates.");
		print.println("");
		print.println("    cp updates/* .");
		print.println("");
		print.println("    if [ $? = 0 ]");
		print.println("    then");
		print.println("      cat edesign.ini >edesign.ini.update");
		print.println("      cat edesign.ini.new >>edesign.ini.update");
		print.println("      cp edesign.ini.update edesign.ini");
		print.println("");
		print.println("      if [ $? = 0 ]");
		print.println("      then");
		print.println("        rm edesign.ini.new");
		print.println("        rm edesign.ini.update");
		print.println("        rm updates/*");
		print.println("");
		print.println("        echo Updates applied.");
		print.println("      else");
		print.println("        echo Files that need to be updated are locked.");
		print.println("        echo Please close other instances of Dropbox applications to");
		print.println("        echo enable updates to be applied.");
		print.println("      fi");
		print.println("    else");
		print.println("      echo Files that need to be updated are locked.");
		print.println("      echo Please close other instances of Dropbox applications to");
		print.println("      echo enable updates to be applied.");
		print.println("    fi");
		print.println("  fi");
		print.println("  cd $CWD");
		print.println("");
		print.println("  $InstallPoint/startds.sh $*");
		print.println("  RC=$?");
		print.println("done");

		print.close();

		String[] app = new String[2];
		app[0] = "/bin/sh";
		app[1] = "-s";

		Process p = Runtime.getRuntime().exec(app);

		PrintStream in = new PrintStream(p.getOutputStream());
		in.println("chmod 755 " + f.toString());
		in.println("echo DONE");
		in.close();
		
		BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = rdr.readLine();
		while (line != null && ! line.startsWith("DONE")) {
			line = rdr.readLine();
		}
		rdr.close();
	}
}
/**
 * Comment
 */
public void debug() {
	String code = getDebugTF().getText();

	if (code == null || code.length() != 5 || (! code.equals("DUFUS") && ! token.startsWith(code)))
		return;

	getDebugDlg().dispose();
	getLaunchDSCJMenuBar().add(getDebugM());
	validate();
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayAbout() {
	getAboutDSC().setCompName("Conferencing/Hosting Services");

	Window w = new Window(this);
	w.setLayout(new BorderLayout());
	w.setSize(450,350);
	w.add(getAboutDSC(), "Center");

	// Center the window
	Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize();
	Rectangle win = w.getBounds();
	w.setLocation((screen.width - win.width) / 2,(screen.height - win.height) / 2);
	w.setVisible(true);

	try {
		Thread.sleep(3000);
	}
	catch (Exception e) {
		System.out.println(e);
	}
	w.setVisible(false);
	w.dispose();

	/*About about = new About(this);
	about.display();
	about.invisible(3000);*/
}
/**
 * Insert the method's description here.
 * Creation date: (03/07/01 2:22:27 PM)
 */
public void displayAboutDlg() {
	// Ensure the AboutPnl is 525x425.
	getAboutDlg().setSize(525,425);

	// Center the window
	Point winPos = getLocation();
	Dimension winSize = getSize();
	Dimension dlgSize = getAboutDlg().getSize();
	getAboutDlg().setLocation(winPos.x + (winSize.width - dlgSize.width) / 2,
								winPos.y + (winSize.height - dlgSize.height) / 2);
	getAboutDlg().setVisible(true);
}
/**
 * Comment
 */
public void doClose() {
	// If we can close, then just make ourselves invisible, otherwise we have to exit.
	if (getCloseMI().isEnabled()) {
		setVisible(false);
	}
	else {
		doExit();
	}
}
/**
 * Comment
 */
public void doDebugMenu() {
	if (getLaunchDSCJMenuBar().getComponentIndex(getDebugM()) != -1) {
		getLaunchDSCJMenuBar().remove(getDebugM());
		validate();
		repaint();
	}
	else {
		getDebugDlg().setLocationRelativeTo(this);
		getDebugDlg().setVisible(true);
	}
}
/**
 * Comment
 */
public void doDrop() {
	if (getTunnel() != null)
		getTunnel().itemStateChanged(new ItemEvent(getDebugMI(),ItemEvent.ITEM_STATE_CHANGED,"ForceDrop",0));
}
/**
 * Comment
 */
public void doDump() {
	if (getTunnel() != null)
		getTunnel().itemStateChanged(new ItemEvent(getDebugMI(),ItemEvent.ITEM_STATE_CHANGED,"Dump",0));
}
/**
 * Comment
 */
public void doExit() {
	// If the tunnel is running, we have to ask the user.
	if (getStopMI().isEnabled()) {
		if (JOptionPane.showConfirmDialog(this,"Stop the connection and exit?","Stop and exit?",JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;
	}

	doTerminate();
}
/**
 * Comment
 */
public void doFtp() {
	int i;
	for (i = 0; i < tunnelArgs.length && ! tunnelArgs[i].equals(HOSTM_OPT); i++);
	if (i < tunnelArgs.length)
		getTunnel().startFTP(tunnelArgs[++i]);
	else
		System.out.println("Error starting FTP! Host name not found.");
}
/**
 * Insert the method's description here.
 * Creation date: (1/7/2003 12:37:39 PM)
 */
public void doLogin() {
	boolean cancelled = false;
	boolean logged = false;

	while (! cancelled && ! logged) {
		// Center and show the login dialog.
		login = false;
		
		getLoginDlg().setLocationRelativeTo(this);
		getLoginDlg().setVisible(true);

		if (login) {
			String id = getUserTF().getText().trim();
			String pwd = new String(getPwdTF().getPassword());
			System.out.println("Contacting login server...");
			String creds = Misc.getConnectInfoGeneric(id,pwd,tunnelArgs[3],null,tunnelArgs[1]);

			if (creds != null) {
				System.out.println("Logged in.");
				// plug in new token.
				tunnelArgs[5] = creds;
				logged = true;
				startTunnel();
			}
			else {
				System.out.println("Login failed.");
				getLoginLbl().setText("Invalid ID or pwd");
			}
		}
		else {
			cancelled = true;
			getStartMI().setEnabled(true);
		}
	}
}
/**
 * Comment
 */
public void doLoginFocus() {
	String user = getUserTF().getText();

	if (user != null && user.length() > 0) {
		char[] pwd = getPwdTF().getPassword();

		if (pwd != null) {
			getPwdTF().setSelectionStart(0);
			getPwdTF().setSelectionEnd(pwd.length);
		}

		getPwdTF().requestFocus();
	}
	else {
		getUserTF().requestFocus();
	}
}
/**
 * Comment
 */
public void doNewSender() {
	if (getTunnel() != null)
		getTunnel().itemStateChanged(new ItemEvent(getDebugMI(),ItemEvent.ITEM_STATE_CHANGED,"NewSend",0));
}
/**
 * Comment
 */
public void doPing() {
	if (getTunnel() != null)
		getTunnel().itemStateChanged(new ItemEvent(getDebugMI(),ItemEvent.ITEM_STATE_CHANGED,"Ping",0));
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/2003 1:28:02 PM)
 * @param e oem.edge.ed.odc.tunnel.applet.TunnelEvent
 */
public void doStartDOC(TunnelEvent e) {
	try{
		Process p =Runtime.getRuntime().exec("C:\\Program Files\\Internet Explorer\\IEXPLORE.exe  http://localhost:8089");
		}
	catch(Exception ex){System.out.println("couldn't start the IE process");}
		
	}
public void doStartDropBox(TunnelEvent e) {
	setTitle(title + "Connected.");

	TunnelEarInfo tt = (TunnelEarInfo) e.parm1;
	Socket socket = tt.generatePairedSocket("Drop Box");

	try {
		Class mv = Class.forName("oem.edge.ed.odc.dropbox.client.DropBox");
		Class[] parms = { String.class, Socket.class, SessionManager.class };
		Method mvstart = mv.getMethod("start",parms);
		Object[] args = { getManager().getLoginToken(), socket, getManager() };
		mvstart.invoke(null,args);
	} catch(Exception e1) {
		System.out.println("Error starting Drop Box!");
		e1.printStackTrace();
	}
}
public void doStartFTP(TunnelEvent e) {
	setTitle(title + "Connected.");

	TunnelEarInfo tt = (TunnelEarInfo) e.parm1;
	Socket socket = tt.generatePairedSocket("FTP Server");

	try {
		Class mv = Class.forName("oem.edge.ed.odc.ftp.client.FileTransfer");
		Class[] parms = { Socket.class };
		Method mvstart = mv.getMethod("start",parms);
		Object[] args = { socket };
		mvstart.invoke(null,args);
	} catch(Exception e1) {
		System.out.println("Error starting FTP!");
		e1.printStackTrace();
	}
}
public void doStartGrid(TunnelEvent e) {
	setTitle(title + "Connected.");

	TunnelEarInfo tt = (TunnelEarInfo) e.parm1;
	Socket socket = tt.generatePairedSocket("Grid Drop Box");

	try {
		Class mv = Class.forName("oem.edge.ed.odc.verify.client.VerificationClient");
		Class[] parms = { String.class, Socket.class };
		Method mvstart = mv.getMethod("start",parms);
		Object[] args = { getManager().getLoginToken(), socket };
		mvstart.invoke(null,args);
	} catch(Exception e1) {
		System.out.println("Error starting Verification client!");
		e1.printStackTrace();
	}
}
public void doStartICA(TunnelEvent e) {
	setTitle(title + "Connected.");

	String tunnelCmd = (String) e.parm1;
	String initpgm = (String) e.parm2;
	HttpTunnelThread tt = (HttpTunnelThread) e.parm3;

	try {
		int lport = tt.getLocalPort();
		String myhost = InetAddress.getLocalHost().getHostAddress();

		Runtime runtime = Runtime.getRuntime();

		Properties props = System.getProperties();
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		int h, w;

		h = ss.height - 30;
		w = ss.width  - 20;
		if (w > 1200) w = 1200;
		if (h > 1000) h = 1000;

		String[] appArgs = new String[2];
		
		// Determine JRE level and use ICA1 (Java 1.3 or lower) or ICA2 (Java 1.4 or higher).
		if (verifyJava14Plus()) {
			appArgs[0] = "ICA2";
		}
		else {
			appArgs[0] = "ICA1";
		}
		appArgs[1] = "ClassParms=\"" + "-address:" + myhost + " -ICAPortNumber:" + String.valueOf(lport) +
			" -width:" + w + " -height:" + h;

		if (tunnelCmd.equalsIgnoreCase("EDU"))
			appArgs[1] += " -title:ClassroomDesktop";
		else if (tunnelCmd.equalsIgnoreCase("DSH"))
			appArgs[1] += " -title:HostingDesktop";
		else
			appArgs[1] += " -title:WebConferenceDesktop";

		if (initpgm != null) {
			if (isWin) {
				appArgs[1] += " -InitialProgram:" + initpgm;
			}
			else {
				// Preserve any double-quotes in initial program by putting 3 back-slashes in front.
				StringBuffer sb = new StringBuffer(initpgm);
				int i = 0;
				while (i < sb.length()) {
					// Find a double-quote.
					while (i < sb.length() && sb.charAt(i) != '"') i++;
					// Found one? put 3 back-slashes in front and move on to
					// the character after the double-quote.
					if (i < sb.length()) {
						sb.insert(i,"\\\\\\");
						i += 4;
					} 
				}
				appArgs[1] += " -InitialProgram:" + sb.toString();
			}
		}

		appArgs[1] += "\"";

		Process p = runtime.exec(System.getProperty("user.dir") + File.separator + "startds." + (isWin ? "exe" : "sh"));

		InputStream err = p.getErrorStream();
		InputStream out = p.getInputStream();
		Thread errRun = new Thread(new PipeReader("ICAerr",err));
		Thread outRun = new Thread(new PipeReader("ICAout",out));
		errRun.start();
		outRun.start();

		PrintStream in = new PrintStream(p.getOutputStream());

		for (int i = 0; i < appArgs.length; i++)
			in.println(appArgs[i]);
		if (isWin)
			in.println("-THE_END");
		in.close();
	} catch(Exception e1) {
		System.out.println("Error starting ICA!");
		e1.printStackTrace();
	}
}
public void doStartIM(TunnelEvent e) {
	setTitle(title + "Connected.");

	HttpTunnelThread tt = (HttpTunnelThread) e.parm1;

	try {
		int lport = tt.getLocalPort();

		Process p = Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "startds." + (isWin ? "exe" : "sh"));

		InputStream err = p.getErrorStream();
		InputStream out = p.getInputStream();
		Thread errRun = new Thread(new PipeReader("IMerr",err));
		Thread outRun = new Thread(new PipeReader("IMout",out));
		errRun.start();
		outRun.start();

		PrintStream in = new PrintStream(p.getOutputStream());

		in.println("IM");
		in.println(lport);
		if (isWin)
			in.println("-THE_END");
		in.close();
	} catch(Exception e1) {
		System.out.println("Error starting IM!");
		e1.printStackTrace();
	}
}
public void doStartNewODC(TunnelEvent e) {
	setTitle(title + "Connected.");

	TunnelEarInfo tt = (TunnelEarInfo) e.parm1;
	Socket socket = tt.generatePairedSocket("ODC Server");

	try {
		Class mv = Class.forName("oem.edge.ed.odc.meeting.client.MeetingViewer");
		Class[] parms = { String.class, Socket.class, Frame.class };
		Method mvstart = mv.getMethod("start",parms);
		Object[] args = { getManager().getLoginToken(), socket, this };
		mvstart.invoke(null,args);
	} catch(Exception e1) {
		System.out.println("Error starting ODC!");
		e1.printStackTrace();
	}
}
public void doStartNewODC2(TunnelEvent e) {
	setTitle(title + "Connected.");

	HttpTunnelThread tt = (HttpTunnelThread) e.parm1;

	try {
		int lport = tt.getLocalPort();

		Process p = Runtime.getRuntime().exec(System.getProperty("user.dir") + File.separator + "startds." + (isWin ? "exe" : "sh"));

		InputStream err = p.getErrorStream();
		InputStream out = p.getInputStream();
		Thread errRun = new Thread(new PipeReader(null,err));
		Thread outRun = new Thread(new PipeReader(null,out));
		errRun.start();
		outRun.start();

		PrintStream in = new PrintStream(p.getOutputStream());

		in.println("NEWODC");
		in.println("-HOST localhost");
		in.println("-PORT " + lport);
		in.println("-TOKEN " + getManager().getLoginToken());

		String display = getConfigMgr().getCfg().getProperty("DSMPDISPLAY",null);
		if (display != null) in.println("-DISPLAY " + display);

		if (callmeeting != null) in.println("-MEETINGID " + callmeeting);
		if (calluser != null) in.println("-MEETINGUSER " + calluser);
		if (callpassword != null) in.println("-MEETINGPW " + callpassword);

		if (isWin)
			in.println("-THE_END");
		in.close();
	} catch(Exception e1) {
		System.out.println("Error starting NewODC!");
		e1.printStackTrace();
	}
}
public void doStartRM(TunnelEvent e) {
	setTitle(title + "Connected.");

	HttpTunnelThread tt = (HttpTunnelThread) e.parm1;

	try {
		Runtime runtime = Runtime.getRuntime();

		String appArgs = "rtsp://localhost:" + tt.getLocalPort() + "/" + tt.getOtherInfo();

		boolean startdsOnWin = false;
		String realpath = getConfigMgr().getCfg().getProperty("REALPATH",null);

		if (realpath == null) {
			if (isWin) {
				startdsOnWin = true;
				realpath = System.getProperty("user.dir") + File.separator + "startds.exe";
			}
			else
				getConfigMgr().changeReal();
		}

		if (realpath == null) {
			System.out.println("Real Media Player path is not available. Unable to start Real Player.");
			return;
		}

		Process p;
		
		if (startdsOnWin) {
			p = runtime.exec(realpath);

			InputStream err = p.getErrorStream();
			InputStream out = p.getInputStream();
			Thread errRun = new Thread(new PipeReader("RMerr",err));
			Thread outRun = new Thread(new PipeReader("RMout",out));
			errRun.start();
			outRun.start();

			PrintStream in = new PrintStream(p.getOutputStream());
			in.println("RM");
			in.println(appArgs);
			in.println("-THE_END");
			in.close();
		}
		else {
			p = runtime.exec(realpath + " " + appArgs);
		}
	} catch(IOException e1) {
		System.out.println("Error starting Real Media Player!");
		e1.printStackTrace();
	}
}
/**
 * Comment
 */
public void doTerminate() {
	// If Stop menu item is enabled, we are supposed to
	// kill the Tunnel and then exit.
	if (getStopMI().isEnabled()) {
		systemExiting = true;
		System.out.println("Stopping CSDS, please wait...");
		stopTunnel();
		return;
	}

	dispose();

	System.exit(0);
}
/**
 * Comment
 */
public void doXChannel(ActionEvent actionEvent) {
	getXChanPF().setText(null);
	getXChanOkBtn().setEnabled(false);
	getXChanDlg().setLocationRelativeTo(this);
	getXChanDlg().setVisible(true);
}
/**
 * Return the JDialogContentPane3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getAboutCP() {
	if (ivjAboutCP == null) {
		try {
			ivjAboutCP = new javax.swing.JPanel();
			ivjAboutCP.setName("AboutCP");
			ivjAboutCP.setLayout(new java.awt.BorderLayout());
			ivjAboutCP.setBounds(43, 835, 317, 222);
			getAboutCP().add(getAboutDSC(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAboutCP;
}
/**
 * Return the JDialog15 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getAboutDlg() {
	if (ivjAboutDlg == null) {
		try {
			ivjAboutDlg = new javax.swing.JDialog(this);
			ivjAboutDlg.setName("AboutDlg");
			ivjAboutDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjAboutDlg.setBounds(30, 808, 400, 222);
			ivjAboutDlg.setModal(true);
			ivjAboutDlg.setTitle("CSDS Release Information");
			getAboutDlg().setContentPane(getAboutCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAboutDlg;
}
/**
 * Return the AboutDSC property value.
 * @return oem.edge.ed.odc.applet.AboutDSC
 */
private AboutPnl getAboutDSC() {
	if (ivjAboutPnl == null) {
		try {
			ivjAboutPnl = new oem.edge.ed.odc.applet.AboutPnl();
			ivjAboutPnl.setName("AboutPnl");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAboutPnl;
}
/**
 * Return the AboutMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getAboutMI() {
	if (ivjAboutMI == null) {
		try {
			ivjAboutMI = new javax.swing.JMenuItem();
			ivjAboutMI.setName("AboutMI");
			ivjAboutMI.setText("About");
			ivjAboutMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						displayAboutDlg();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAboutMI;
}
/**
 * Return the AutoPingMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getAutoPingMI() {
	if (ivjAutoPingMI == null) {
		try {
			ivjAutoPingMI = new javax.swing.JCheckBoxMenuItem();
			ivjAutoPingMI.setName("AutoPingMI");
			ivjAutoPingMI.setText("Auto Ping");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjAutoPingMI;
}
/**
 * Return the BorderFactory1 property value.
 * @return javax.swing.BorderFactory
 */
private javax.swing.BorderFactory getBorderFactory() {
	return ivjBorderFactory;
}
/**
 * Return the JDialogContentPane2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getCertCP() {
	if (ivjCertCP == null) {
		try {
			ivjCertCP = new javax.swing.JPanel();
			ivjCertCP.setName("CertCP");
			ivjCertCP.setLayout(new java.awt.GridBagLayout());
			ivjCertCP.setBounds(477, 1117, 253, 167);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.insets = new java.awt.Insets(10, 10, 10, 5);
			getCertCP().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsCertPF = new java.awt.GridBagConstraints();
			constraintsCertPF.gridx = 1; constraintsCertPF.gridy = 0;
			constraintsCertPF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCertPF.weightx = 1.0;
			constraintsCertPF.insets = new java.awt.Insets(10, 0, 10, 10);
			getCertCP().add(getCertPF(), constraintsCertPF);

			java.awt.GridBagConstraints constraintsCertOkBtn = new java.awt.GridBagConstraints();
			constraintsCertOkBtn.gridx = 0; constraintsCertOkBtn.gridy = 1;
			constraintsCertOkBtn.gridwidth = 0;
			constraintsCertOkBtn.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsCertOkBtn.weighty = 1.0;
			constraintsCertOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getCertCP().add(getCertOkBtn(), constraintsCertOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCertCP;
}
/**
 * Return the JDialog1 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getCertDlg() {
	if (ivjCertDlg == null) {
		try {
			ivjCertDlg = new javax.swing.JDialog(this);
			ivjCertDlg.setName("CertDlg");
			ivjCertDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjCertDlg.setBounds(40, 1689, 253, 167);
			ivjCertDlg.setModal(true);
			getCertDlg().setContentPane(getCertCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCertDlg;
}
/**
 * Return the CertOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getCertOkBtn() {
	if (ivjCertOkBtn == null) {
		try {
			ivjCertOkBtn = new javax.swing.JButton();
			ivjCertOkBtn.setName("CertOkBtn");
			ivjCertOkBtn.setText("Ok");
			ivjCertOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getCertDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCertOkBtn;
}
/**
 * Return the CertPF property value.
 * @return javax.swing.JPasswordField
 */
private javax.swing.JPasswordField getCertPF() {
	if (ivjCertPF == null) {
		try {
			ivjCertPF = new javax.swing.JPasswordField();
			ivjCertPF.setName("CertPF");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCertPF;
}
/**
 * Return the CitrixMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCitrixMI() {
	if (ivjCitrixMI == null) {
		try {
			ivjCitrixMI = new javax.swing.JMenuItem();
			ivjCitrixMI.setName("CitrixMI");
			ivjCitrixMI.setText("Citrix ICA...");
			ivjCitrixMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getConfigMgr().changeCitrix();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCitrixMI;
}
/**
 * Return the ClearMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getClearMI() {
	if (ivjClearMI == null) {
		try {
			ivjClearMI = new javax.swing.JMenuItem();
			ivjClearMI.setName("ClearMI");
			ivjClearMI.setText("Clear log");
			ivjClearMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						clearText();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjClearMI;
}
/**
 * Return the ServerDbgMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getCompressMI() {
	if (ivjCompressMI == null) {
		try {
			ivjCompressMI = new javax.swing.JCheckBoxMenuItem();
			ivjCompressMI.setName("CompressMI");
			ivjCompressMI.setSelected(true);
			ivjCompressMI.setText("Compression");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCompressMI;
}
/**
 * Return the ConfigMgr property value.
 * @return oem.edge.ed.odc.applet.ConfigMgr
 */
private ConfigMgr getConfigMgr() {
	return ivjConfigMgr;
}
/**
 * Return the CreateXChannelMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getCreateXChannelMI() {
	if (ivjCreateXChannelMI == null) {
		try {
			ivjCreateXChannelMI = new javax.swing.JMenuItem();
			ivjCreateXChannelMI.setName("CreateXChannelMI");
			ivjCreateXChannelMI.setText("Create X-Channel");
			ivjCreateXChannelMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doXChannel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCreateXChannelMI;
}
/**
 * Return the Debug2MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDebug2MI() {
	if (ivjDebug2MI == null) {
		try {
			ivjDebug2MI = new javax.swing.JMenuItem();
			ivjDebug2MI.setName("Debug2MI");
			ivjDebug2MI.setText("Debug 2");
			ivjDebug2MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebug2MI;
}
/**
 * Return the Debug3MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDebug3MI() {
	if (ivjDebug3MI == null) {
		try {
			ivjDebug3MI = new javax.swing.JMenuItem();
			ivjDebug3MI.setName("Debug3MI");
			ivjDebug3MI.setText("Debug 3");
			ivjDebug3MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebug3MI;
}
/**
 * Return the Debug4MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDebug4MI() {
	if (ivjDebug4MI == null) {
		try {
			ivjDebug4MI = new javax.swing.JMenuItem();
			ivjDebug4MI.setName("Debug4MI");
			ivjDebug4MI.setText("Debug 4");
			ivjDebug4MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebug4MI;
}
/**
 * Return the Debug5MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDebug5MI() {
	if (ivjDebug5MI == null) {
		try {
			ivjDebug5MI = new javax.swing.JMenuItem();
			ivjDebug5MI.setName("Debug5MI");
			ivjDebug5MI.setText("Debug 5");
			ivjDebug5MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebug5MI;
}
/**
 * Return the DebugCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDebugCanBtn() {
	if (ivjDebugCanBtn == null) {
		try {
			ivjDebugCanBtn = new javax.swing.JButton();
			ivjDebugCanBtn.setName("DebugCanBtn");
			ivjDebugCanBtn.setText("Cancel");
			ivjDebugCanBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjDebugCanBtn;
}
/**
 * Return the JDialogContentPane5 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getDebugCP() {
	if (ivjDebugCP == null) {
		try {
			ivjDebugCP = new javax.swing.JPanel();
			ivjDebugCP.setName("DebugCP");
			ivjDebugCP.setLayout(new java.awt.GridBagLayout());
			ivjDebugCP.setBounds(489, 1779, 197, 125);

			java.awt.GridBagConstraints constraintsJLabel12 = new java.awt.GridBagConstraints();
			constraintsJLabel12.gridx = 0; constraintsJLabel12.gridy = 0;
			constraintsJLabel12.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel12.insets = new java.awt.Insets(5, 5, 0, 5);
			getDebugCP().add(getJLabel12(), constraintsJLabel12);

			java.awt.GridBagConstraints constraintsDebugTF = new java.awt.GridBagConstraints();
			constraintsDebugTF.gridx = 0; constraintsDebugTF.gridy = 1;
			constraintsDebugTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDebugTF.weightx = 1.0;
			constraintsDebugTF.insets = new java.awt.Insets(5, 5, 5, 5);
			getDebugCP().add(getDebugTF(), constraintsDebugTF);

			java.awt.GridBagConstraints constraintsJPanel11 = new java.awt.GridBagConstraints();
			constraintsJPanel11.gridx = 0; constraintsJPanel11.gridy = 2;
			constraintsJPanel11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel11.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel11.weightx = 1.0;
			constraintsJPanel11.weighty = 1.0;
			constraintsJPanel11.insets = new java.awt.Insets(5, 5, 5, 5);
			getDebugCP().add(getJPanel11(), constraintsJPanel11);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebugCP;
}
/**
 * Return the DebugDlg property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getDebugDlg() {
	if (ivjDebugDlg == null) {
		try {
			ivjDebugDlg = new javax.swing.JDialog(this);
			ivjDebugDlg.setName("DebugDlg");
			ivjDebugDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjDebugDlg.setBounds(494, 1765, 197, 125);
			ivjDebugDlg.setModal(true);
			ivjDebugDlg.setTitle("Enable Debug");
			getDebugDlg().setContentPane(getDebugCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebugDlg;
}
/**
 * Return the DebugM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getDebugM() {
	if (ivjDebugM == null) {
		try {
			ivjDebugM = new javax.swing.JMenu();
			ivjDebugM.setName("DebugM");
			ivjDebugM.setText("Debug");
			ivjDebugM.add(getLevelM());
			ivjDebugM.add(getKeepAliveMI());
			ivjDebugM.add(getJSeparator5());
			ivjDebugM.add(getDumpMI());
			ivjDebugM.add(getSenderMI());
			ivjDebugM.add(getDropMI());
			ivjDebugM.add(getJSeparator6());
			ivjDebugM.add(getCompressMI());
			ivjDebugM.add(getNormalMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebugM;
}
/**
 * Return the DebugMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDebugMI() {
	if (ivjDebugMI == null) {
		try {
			ivjDebugMI = new javax.swing.JMenuItem();
			ivjDebugMI.setName("DebugMI");
			ivjDebugMI.setText("Debug");
			ivjDebugMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDebugMI;
}
/**
 * Return the DebugOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getDebugOkBtn() {
	if (ivjDebugOkBtn == null) {
		try {
			ivjDebugOkBtn = new javax.swing.JButton();
			ivjDebugOkBtn.setName("DebugOkBtn");
			ivjDebugOkBtn.setText("Ok");
			ivjDebugOkBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjDebugOkBtn;
}
/**
 * Return the DebugTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getDebugTF() {
	if (ivjDebugTF == null) {
		try {
			ivjDebugTF = new javax.swing.JTextField();
			ivjDebugTF.setName("DebugTF");
			ivjDebugTF.setToolTipText("Enter code as requested by support");
			ivjDebugTF.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjDebugTF;
}
/**
 * Return the DropMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDropMI() {
	if (ivjDropMI == null) {
		try {
			ivjDropMI = new javax.swing.JMenuItem();
			ivjDropMI.setName("DropMI");
			ivjDropMI.setText("Force Drop");
			ivjDropMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doDrop();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDropMI;
}
/**
 * Return the DumpMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getDumpMI() {
	if (ivjDumpMI == null) {
		try {
			ivjDumpMI = new javax.swing.JMenuItem();
			ivjDumpMI.setName("DumpMI");
			ivjDumpMI.setText("Dump");
			ivjDumpMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doDump();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDumpMI;
}
/**
 * Return the ExitMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getExitMI() {
	if (ivjExitMI == null) {
		try {
			ivjExitMI = new javax.swing.JMenuItem();
			ivjExitMI.setName("ExitMI");
			ivjExitMI.setText("Exit");
			ivjExitMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doExit();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjExitMI;
}
/**
 * Return the FileDlg property value.
 * @return javax.swing.JFileChooser
 */
private javax.swing.JFileChooser getFileDlg() {
	if (ivjFileDlg == null) {
		try {
			ivjFileDlg = new javax.swing.JFileChooser();
			ivjFileDlg.setName("FileDlg");
			ivjFileDlg.setBounds(29, 522, 317, 227);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileDlg;
}
/**
 * Return the FileM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getFileM() {
	if (ivjFileM == null) {
		try {
			ivjFileM = new javax.swing.JMenu();
			ivjFileM.setName("FileM");
			ivjFileM.setText("File");
			ivjFileM.add(getClearMI());
			ivjFileM.add(getJSeparator4());
			ivjFileM.add(getSaveMI());
			ivjFileM.add(getSaveAsMI());
			ivjFileM.add(getJSeparator3());
			ivjFileM.add(getCloseMI());
			ivjFileM.add(getExitMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileM;
}
/**
 * Return the FtpMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getFtpMI() {
	if (ivjFtpMI == null) {
		try {
			ivjFtpMI = new javax.swing.JMenuItem();
			ivjFtpMI.setName("FtpMI");
			ivjFtpMI.setText("FTP to Host");
			ivjFtpMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doFtp();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFtpMI;
}
/**
 * Return the JSeparator7 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getFtpSep() {
	if (ivjFtpSep == null) {
		try {
			ivjFtpSep = new javax.swing.JSeparator();
			ivjFtpSep.setName("FtpSep");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFtpSep;
}
/**
 * Return the HelpM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getHelpM() {
	if (ivjHelpM == null) {
		try {
			ivjHelpM = new javax.swing.JMenu();
			ivjHelpM.setName("HelpM");
			ivjHelpM.setText("Help");
			ivjHelpM.add(getAboutMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjHelpM;
}
/**
 * Return the Info2MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInfo2MI() {
	if (ivjInfo2MI == null) {
		try {
			ivjInfo2MI = new javax.swing.JMenuItem();
			ivjInfo2MI.setName("Info2MI");
			ivjInfo2MI.setText("Info 2");
			ivjInfo2MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInfo2MI;
}
/**
 * Return the Info3MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInfo3MI() {
	if (ivjInfo3MI == null) {
		try {
			ivjInfo3MI = new javax.swing.JMenuItem();
			ivjInfo3MI.setName("Info3MI");
			ivjInfo3MI.setText("Info 3");
			ivjInfo3MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInfo3MI;
}
/**
 * Return the Info4MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInfo4MI() {
	if (ivjInfo4MI == null) {
		try {
			ivjInfo4MI = new javax.swing.JMenuItem();
			ivjInfo4MI.setName("Info4MI");
			ivjInfo4MI.setText("Info 4");
			ivjInfo4MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInfo4MI;
}
/**
 * Return the Info5MI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInfo5MI() {
	if (ivjInfo5MI == null) {
		try {
			ivjInfo5MI = new javax.swing.JMenuItem();
			ivjInfo5MI.setName("Info5MI");
			ivjInfo5MI.setText("Info 5");
			ivjInfo5MI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInfo5MI;
}
/**
 * Return the InfoMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getInfoMI() {
	if (ivjInfoMI == null) {
		try {
			ivjInfoMI = new javax.swing.JMenuItem();
			ivjInfoMI.setName("InfoMI");
			ivjInfoMI.setText("Info");
			ivjInfoMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						setDebugLevel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInfoMI;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Ok");
			ivjJButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getPwdSubDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());
			ivjJFrameContentPane.setBackground(new java.awt.Color(204,204,204));

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel1.ipady = 85;
			constraintsJPanel1.insets = new java.awt.Insets(5, 5, 0, 5);
			getJFrameContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanel4 = new java.awt.GridBagConstraints();
			constraintsJPanel4.gridx = 0; constraintsJPanel4.gridy = 1;
			constraintsJPanel4.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel4.weightx = 1.0;
			constraintsJPanel4.weighty = 1.0;
			constraintsJPanel4.insets = new java.awt.Insets(5, 5, 5, 5);
			getJFrameContentPane().add(getJPanel4(), constraintsJPanel4);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Certificate Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel11() {
	if (ivjJLabel11 == null) {
		try {
			ivjJLabel11 = new javax.swing.JLabel();
			ivjJLabel11.setName("JLabel11");
			ivjJLabel11.setText("X-Channel Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel11;
}
/**
 * Return the JLabel12 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("Enter activation code:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("The password has been submitted to the server.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Please start the script in the Citrix session to");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("continue with authentication.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());
			ivjJPanel1.setBackground(new java.awt.Color(204,204,204));

			java.awt.GridBagConstraints constraintsPerformanceIndicator = new java.awt.GridBagConstraints();
			constraintsPerformanceIndicator.gridx = 0; constraintsPerformanceIndicator.gridy = 1;
			constraintsPerformanceIndicator.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPerformanceIndicator.weightx = 1.0;
			constraintsPerformanceIndicator.weighty = 1.0;
			constraintsPerformanceIndicator.insets = new java.awt.Insets(0, 5, 5, 5);
			getJPanel1().add(getPerformanceIndicator(), constraintsPerformanceIndicator);
			ivjJPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Connection Activity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(102,102,153)));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel11() {
	if (ivjJPanel11 == null) {
		try {
			ivjJPanel11 = new javax.swing.JPanel();
			ivjJPanel11.setName("JPanel11");
			ivjJPanel11.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDebugOkBtn = new java.awt.GridBagConstraints();
			constraintsDebugOkBtn.gridx = 0; constraintsDebugOkBtn.gridy = 0;
			constraintsDebugOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel11().add(getDebugOkBtn(), constraintsDebugOkBtn);

			java.awt.GridBagConstraints constraintsDebugCanBtn = new java.awt.GridBagConstraints();
			constraintsDebugCanBtn.gridx = 1; constraintsDebugCanBtn.gridy = 0;
			constraintsDebugCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel11().add(getDebugCanBtn(), constraintsDebugCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel11;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLoginOkBtn = new java.awt.GridBagConstraints();
			constraintsLoginOkBtn.gridx = 0; constraintsLoginOkBtn.gridy = 0;
			constraintsLoginOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel2().add(getLoginOkBtn(), constraintsLoginOkBtn);

			java.awt.GridBagConstraints constraintsLoginCanBtn = new java.awt.GridBagConstraints();
			constraintsLoginCanBtn.gridx = 1; constraintsLoginCanBtn.gridy = 0;
			constraintsLoginCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel2().add(getLoginCanBtn(), constraintsLoginCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsXChanOkBtn = new java.awt.GridBagConstraints();
			constraintsXChanOkBtn.gridx = 0; constraintsXChanOkBtn.gridy = 0;
			constraintsXChanOkBtn.insets = new java.awt.Insets(0, 0, 0, 5);
			getJPanel3().add(getXChanOkBtn(), constraintsXChanOkBtn);

			java.awt.GridBagConstraints constraintsXChanCanBtn = new java.awt.GridBagConstraints();
			constraintsXChanCanBtn.gridx = 1; constraintsXChanCanBtn.gridy = 0;
			constraintsXChanCanBtn.insets = new java.awt.Insets(0, 5, 0, 0);
			getJPanel3().add(getXChanCanBtn(), constraintsXChanCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 0;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.insets = new java.awt.Insets(0, 5, 5, 5);
			getJPanel4().add(getJScrollPane1(), constraintsJScrollPane1);
			ivjJPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED), "Messages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			getJScrollPane1().setViewportView(getMsgTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the JSeparator3 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator3() {
	if (ivjJSeparator3 == null) {
		try {
			ivjJSeparator3 = new javax.swing.JSeparator();
			ivjJSeparator3.setName("JSeparator3");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator3;
}
/**
 * Return the JSeparator4 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator4() {
	if (ivjJSeparator4 == null) {
		try {
			ivjJSeparator4 = new javax.swing.JSeparator();
			ivjJSeparator4.setName("JSeparator4");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator4;
}
/**
 * Return the JSeparator5 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator5() {
	if (ivjJSeparator5 == null) {
		try {
			ivjJSeparator5 = new javax.swing.JSeparator();
			ivjJSeparator5.setName("JSeparator5");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator5;
}
/**
 * Return the JSeparator6 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator6() {
	if (ivjJSeparator6 == null) {
		try {
			ivjJSeparator6 = new javax.swing.JSeparator();
			ivjJSeparator6.setName("JSeparator6");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator6;
}
/**
 * Return the JSeparator8 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getJSeparator8() {
	if (ivjJSeparator8 == null) {
		try {
			ivjJSeparator8 = new javax.swing.JSeparator();
			ivjJSeparator8.setName("JSeparator8");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJSeparator8;
}
/**
 * Return the DebugMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getKeepAliveMI() {
	if (ivjKeepAliveMI == null) {
		try {
			ivjKeepAliveMI = new javax.swing.JCheckBoxMenuItem();
			ivjKeepAliveMI.setName("KeepAliveMI");
			ivjKeepAliveMI.setSelected(true);
			ivjKeepAliveMI.setText("KeepAlive");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjKeepAliveMI;
}
/**
 * Return the LaunchDSCJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
private javax.swing.JMenuBar getLaunchDSCJMenuBar() {
	if (ivjLaunchDSCJMenuBar == null) {
		try {
			ivjLaunchDSCJMenuBar = new javax.swing.JMenuBar();
			ivjLaunchDSCJMenuBar.setName("LaunchDSCJMenuBar");
			ivjLaunchDSCJMenuBar.add(getFileM());
			ivjLaunchDSCJMenuBar.add(getTunnelM());
			ivjLaunchDSCJMenuBar.add(getStatsM());
			ivjLaunchDSCJMenuBar.add(getDebugM());
			ivjLaunchDSCJMenuBar.add(getHelpM());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLaunchDSCJMenuBar;
}
/**
 * Return the LevelM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getLevelM() {
	if (ivjLevelM == null) {
		try {
			ivjLevelM = new javax.swing.JMenu();
			ivjLevelM.setName("LevelM");
			ivjLevelM.setText("Level");
			ivjLevelM.add(getInfoMI());
			ivjLevelM.add(getInfo2MI());
			ivjLevelM.add(getInfo3MI());
			ivjLevelM.add(getInfo4MI());
			ivjLevelM.add(getInfo5MI());
			ivjLevelM.add(getDebugMI());
			ivjLevelM.add(getDebug2MI());
			ivjLevelM.add(getDebug3MI());
			ivjLevelM.add(getDebug4MI());
			ivjLevelM.add(getDebug5MI());
			ivjLevelM.add(getJSeparator8());
			ivjLevelM.add(getWriteFileMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLevelM;
}
/**
 * Return the LoginCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginCanBtn() {
	if (ivjLoginCanBtn == null) {
		try {
			ivjLoginCanBtn = new javax.swing.JButton();
			ivjLoginCanBtn.setName("LoginCanBtn");
			ivjLoginCanBtn.setText("Cancel");
			ivjLoginCanBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjLoginCanBtn;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getLoginCP() {
	if (ivjLoginCP == null) {
		try {
			ivjLoginCP = new javax.swing.JPanel();
			ivjLoginCP.setName("LoginCP");
			ivjLoginCP.setLayout(new java.awt.GridBagLayout());
			ivjLoginCP.setBounds(48, 1123, 217, 145);

			java.awt.GridBagConstraints constraintsUserLbl = new java.awt.GridBagConstraints();
			constraintsUserLbl.gridx = 0; constraintsUserLbl.gridy = 1;
			constraintsUserLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUserLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getLoginCP().add(getUserLbl(), constraintsUserLbl);

			java.awt.GridBagConstraints constraintsPwdLbl = new java.awt.GridBagConstraints();
			constraintsPwdLbl.gridx = 0; constraintsPwdLbl.gridy = 2;
			constraintsPwdLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPwdLbl.insets = new java.awt.Insets(10, 10, 0, 0);
			getLoginCP().add(getPwdLbl(), constraintsPwdLbl);

			java.awt.GridBagConstraints constraintsUserTF = new java.awt.GridBagConstraints();
			constraintsUserTF.gridx = 1; constraintsUserTF.gridy = 1;
			constraintsUserTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUserTF.weightx = 1.0;
			constraintsUserTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getLoginCP().add(getUserTF(), constraintsUserTF);

			java.awt.GridBagConstraints constraintsPwdTF = new java.awt.GridBagConstraints();
			constraintsPwdTF.gridx = 1; constraintsPwdTF.gridy = 2;
			constraintsPwdTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPwdTF.weightx = 1.0;
			constraintsPwdTF.insets = new java.awt.Insets(10, 5, 0, 10);
			getLoginCP().add(getPwdTF(), constraintsPwdTF);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 3;
			constraintsJPanel2.gridwidth = 0;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel2.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(10, 10, 10, 10);
			getLoginCP().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsLoginLbl = new java.awt.GridBagConstraints();
			constraintsLoginLbl.gridx = 0; constraintsLoginLbl.gridy = 0;
			constraintsLoginLbl.gridwidth = 0;
			constraintsLoginLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLoginLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getLoginCP().add(getLoginLbl(), constraintsLoginLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginCP;
}
/**
 * Return the JDialog14 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getLoginDlg() {
	if (ivjLoginDlg == null) {
		try {
			ivjLoginDlg = new javax.swing.JDialog(this);
			ivjLoginDlg.setName("LoginDlg");
			ivjLoginDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjLoginDlg.setBounds(35, 1116, 217, 175);
			ivjLoginDlg.setModal(true);
			ivjLoginDlg.setTitle("Login");
			getLoginDlg().setContentPane(getLoginCP());
			ivjLoginDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
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
	return ivjLoginDlg;
}
/**
 * Return the LoginLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLoginLbl() {
	if (ivjLoginLbl == null) {
		try {
			ivjLoginLbl = new javax.swing.JLabel();
			ivjLoginLbl.setName("LoginLbl");
			ivjLoginLbl.setText("Authentication required.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoginLbl;
}
/**
 * Return the LoginOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getLoginOkBtn() {
	if (ivjLoginOkBtn == null) {
		try {
			ivjLoginOkBtn = new javax.swing.JButton();
			ivjLoginOkBtn.setName("LoginOkBtn");
			ivjLoginOkBtn.setText("Ok");
			ivjLoginOkBtn.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjLoginOkBtn;
}
/**
 * Return the Manager property value.
 * @return oem.edge.ed.odc.tunnel.applet.AppletSessionManager
 */
private oem.edge.ed.odc.tunnel.applet.AppletSessionManager getManager() {
	return ivjManager;
}
/**
 * Return the JDialogContentPane1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getMsgCP() {
	if (ivjMsgCP == null) {
		try {
			ivjMsgCP = new javax.swing.JPanel();
			ivjMsgCP.setName("MsgCP");
			ivjMsgCP.setLayout(new java.awt.GridBagLayout());
			ivjMsgCP.setBounds(54, 1381, 302, 240);

			java.awt.GridBagConstraints constraintsMsgOkBtn = new java.awt.GridBagConstraints();
			constraintsMsgOkBtn.gridx = 0; constraintsMsgOkBtn.gridy = 1;
			constraintsMsgOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getMsgCP().add(getMsgOkBtn(), constraintsMsgOkBtn);

			java.awt.GridBagConstraints constraintsMsgDlgSP = new java.awt.GridBagConstraints();
			constraintsMsgDlgSP.gridx = 0; constraintsMsgDlgSP.gridy = 0;
			constraintsMsgDlgSP.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMsgDlgSP.weightx = 1.0;
			constraintsMsgDlgSP.weighty = 1.0;
			constraintsMsgDlgSP.insets = new java.awt.Insets(10, 10, 10, 10);
			getMsgCP().add(getMsgDlgSP(), constraintsMsgDlgSP);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgCP;
}
/**
 * Return the JDialog13 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getMsgDlg() {
	if (ivjMsgDlg == null) {
		try {
			ivjMsgDlg = new javax.swing.JDialog(this);
			ivjMsgDlg.setName("MsgDlg");
			ivjMsgDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjMsgDlg.setBounds(36, 1372, 302, 240);
			ivjMsgDlg.setTitle("Message from server!");
			getMsgDlg().setContentPane(getMsgCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgDlg;
}
/**
 * Return the MsgDlgSP property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getMsgDlgSP() {
	if (ivjMsgDlgSP == null) {
		try {
			ivjMsgDlgSP = new javax.swing.JScrollPane();
			ivjMsgDlgSP.setName("MsgDlgSP");
			getMsgDlgSP().setViewportView(getMsgDlgTA());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgDlgSP;
}
/**
 * Return the MsgDlgTA property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMsgDlgTA() {
	if (ivjMsgDlgTA == null) {
		try {
			ivjMsgDlgTA = new javax.swing.JTextArea();
			ivjMsgDlgTA.setName("MsgDlgTA");
			ivjMsgDlgTA.setBounds(0, 0, 160, 120);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgDlgTA;
}
/**
 * Return the MsgOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getMsgOkBtn() {
	if (ivjMsgOkBtn == null) {
		try {
			ivjMsgOkBtn = new javax.swing.JButton();
			ivjMsgOkBtn.setName("MsgOkBtn");
			ivjMsgOkBtn.setText("Close");
			ivjMsgOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getMsgDlgTA().setText("");
						getMsgDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgOkBtn;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMsgTA() {
	if (ivjMsgTA == null) {
		try {
			ivjMsgTA = new javax.swing.JTextArea();
			ivjMsgTA.setName("MsgTA");
			ivjMsgTA.setBounds(0, 0, 160, 120);
			ivjMsgTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgTA;
}
/**
 * Return the NetworkMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getNetworkMI() {
	if (ivjNetworkMI == null) {
		try {
			ivjNetworkMI = new javax.swing.JMenuItem();
			ivjNetworkMI.setName("NetworkMI");
			ivjNetworkMI.setText("Network");
			ivjNetworkMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						speedTestNetwork();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNetworkMI;
}
/**
 * Return the NormalMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getNormalMI() {
	if (ivjNormalMI == null) {
		try {
			ivjNormalMI = new javax.swing.JCheckBoxMenuItem();
			ivjNormalMI.setName("NormalMI");
			ivjNormalMI.setSelected(true);
			ivjNormalMI.setText("Normalization");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNormalMI;
}
/**
 * Return the PerformanceIndicator property value.
 * @return oem.edge.ed.odc.applet.PerformanceIndicator
 */
private PerformanceIndicator getPerformanceIndicator() {
	if (ivjPerformanceIndicator == null) {
		try {
			ivjPerformanceIndicator = new oem.edge.ed.odc.applet.PerformanceIndicator();
			ivjPerformanceIndicator.setName("PerformanceIndicator");
			ivjPerformanceIndicator.setOutfg(new java.awt.Color(0,255,0));
			ivjPerformanceIndicator.setInfg(new java.awt.Color(255,255,0));
			ivjPerformanceIndicator.setBackground(java.awt.Color.black);
			ivjPerformanceIndicator.setForeground(java.awt.Color.white);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPerformanceIndicator;
}
/**
 * Return the PingMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getPingMI() {
	if (ivjPingMI == null) {
		try {
			ivjPingMI = new javax.swing.JMenuItem();
			ivjPingMI.setName("PingMI");
			ivjPingMI.setText("Ping");
			ivjPingMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doPing();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPingMI;
}
/**
 * Return the PwdLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPwdLbl() {
	if (ivjPwdLbl == null) {
		try {
			ivjPwdLbl = new javax.swing.JLabel();
			ivjPwdLbl.setName("PwdLbl");
			ivjPwdLbl.setText("Password:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPwdLbl;
}
/**
 * Return the JDialogContentPane4 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPwdSubCP() {
	if (ivjPwdSubCP == null) {
		try {
			ivjPwdSubCP = new javax.swing.JPanel();
			ivjPwdSubCP.setName("PwdSubCP");
			ivjPwdSubCP.setLayout(new java.awt.GridBagLayout());
			ivjPwdSubCP.setBounds(47, 1768, 327, 148);

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 0; constraintsJLabel2.gridy = 0;
			constraintsJLabel2.insets = new java.awt.Insets(10, 10, 2, 10);
			getPwdSubCP().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsJLabel3 = new java.awt.GridBagConstraints();
			constraintsJLabel3.gridx = 0; constraintsJLabel3.gridy = 1;
			constraintsJLabel3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel3.insets = new java.awt.Insets(2, 10, 2, 10);
			getPwdSubCP().add(getJLabel3(), constraintsJLabel3);

			java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
			constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 3;
			constraintsJButton1.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJButton1.weighty = 1.0;
			constraintsJButton1.insets = new java.awt.Insets(0, 10, 10, 10);
			getPwdSubCP().add(getJButton1(), constraintsJButton1);

			java.awt.GridBagConstraints constraintsJLabel4 = new java.awt.GridBagConstraints();
			constraintsJLabel4.gridx = 0; constraintsJLabel4.gridy = 2;
			constraintsJLabel4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel4.insets = new java.awt.Insets(2, 10, 10, 10);
			getPwdSubCP().add(getJLabel4(), constraintsJLabel4);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPwdSubCP;
}
/**
 * Return the JDialog12 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getPwdSubDlg() {
	if (ivjPwdSubDlg == null) {
		try {
			ivjPwdSubDlg = new javax.swing.JDialog(this);
			ivjPwdSubDlg.setName("PwdSubDlg");
			ivjPwdSubDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjPwdSubDlg.setBounds(44, 2186, 327, 148);
			ivjPwdSubDlg.setTitle("Password Submited");
			getPwdSubDlg().setContentPane(getPwdSubCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPwdSubDlg;
}
/**
 * Return the PwdTF property value.
 * @return javax.swing.JPasswordField
 */
private javax.swing.JPasswordField getPwdTF() {
	if (ivjPwdTF == null) {
		try {
			ivjPwdTF = new javax.swing.JPasswordField();
			ivjPwdTF.setName("PwdTF");
			ivjPwdTF.addActionListener(new java.awt.event.ActionListener() { 
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
	return ivjPwdTF;
}
/**
 * Return the RealMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getRealMI() {
	if (ivjRealMI == null) {
		try {
			ivjRealMI = new javax.swing.JMenuItem();
			ivjRealMI.setName("RealMI");
			ivjRealMI.setText("Real Player...");
			ivjRealMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getConfigMgr().changeReal();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRealMI;
}
/**
 * Return the ResetMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getResetMI() {
	if (ivjResetMI == null) {
		try {
			ivjResetMI = new javax.swing.JMenuItem();
			ivjResetMI.setName("ResetMI");
			ivjResetMI.setText("Reset");
			ivjResetMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getPerformanceIndicator().clear();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjResetMI;
}
/**
 * Return the SaveAsMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSaveAsMI() {
	if (ivjSaveAsMI == null) {
		try {
			ivjSaveAsMI = new javax.swing.JMenuItem();
			ivjSaveAsMI.setName("SaveAsMI");
			ivjSaveAsMI.setText("Save As...");
			ivjSaveAsMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						saveText(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSaveAsMI;
}
/**
 * Return the SaveMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSaveMI() {
	if (ivjSaveMI == null) {
		try {
			ivjSaveMI = new javax.swing.JMenuItem();
			ivjSaveMI.setName("SaveMI");
			ivjSaveMI.setText("Save");
			ivjSaveMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						saveText(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSaveMI;
}
/**
 * Return the SenderMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSenderMI() {
	if (ivjSenderMI == null) {
		try {
			ivjSenderMI = new javax.swing.JMenuItem();
			ivjSenderMI.setName("SenderMI");
			ivjSenderMI.setText("New Sender");
			ivjSenderMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						doNewSender();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSenderMI;
}
/**
 * Return the SocksMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getSocksMI() {
	if (ivjSocksMI == null) {
		try {
			ivjSocksMI = new javax.swing.JMenuItem();
			ivjSocksMI.setName("SocksMI");
			ivjSocksMI.setText("Connectivity...");
			ivjSocksMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getConfigMgr().changeConnectivity();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSocksMI;
}
/**
 * Return the SpeedM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getSpeedM() {
	if (ivjSpeedM == null) {
		try {
			ivjSpeedM = new javax.swing.JMenu();
			ivjSpeedM.setName("SpeedM");
			ivjSpeedM.setText("Speed Test");
			ivjSpeedM.add(getTunnelMI());
			ivjSpeedM.add(getNetworkMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSpeedM;
}
/**
 * Return the StartMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStartMI() {
	if (ivjStartMI == null) {
		try {
			ivjStartMI = new javax.swing.JMenuItem();
			ivjStartMI.setName("StartMI");
			ivjStartMI.setText("Connect");
			ivjStartMI.setEnabled(false);
			ivjStartMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startTunnel();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStartMI;
}
/**
 * Return the StatsM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getStatsM() {
	if (ivjStatsM == null) {
		try {
			ivjStatsM = new javax.swing.JMenu();
			ivjStatsM.setName("StatsM");
			ivjStatsM.setText("Stats");
			ivjStatsM.add(getPingMI());
			ivjStatsM.add(getResetMI());
			ivjStatsM.add(getSpeedM());
			ivjStatsM.add(getJSeparator1());
			ivjStatsM.add(getVerboseMI());
			ivjStatsM.add(getStatsMI());
			ivjStatsM.add(getAutoPingMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatsM;
}
/**
 * Return the StatsMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getStatsMI() {
	if (ivjStatsMI == null) {
		try {
			ivjStatsMI = new javax.swing.JCheckBoxMenuItem();
			ivjStatsMI.setName("StatsMI");
			ivjStatsMI.setText("Log Statistics");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatsMI;
}
/**
 * Return the StopMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getStopMI() {
	if (ivjStopMI == null) {
		try {
			ivjStopMI = new javax.swing.JMenuItem();
			ivjStopMI.setName("StopMI");
			ivjStopMI.setText("Disconnect");
			ivjStopMI.setEnabled(false);
			ivjStopMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						stopTunnel();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStopMI;
}
/**
 * Return the Tunnel property value.
 * @return oem.edge.ed.odc.tunnel.applet.HttpTunnelClient
 */
private oem.edge.ed.odc.tunnel.applet.HttpTunnelClient getTunnel() {
	return ivjTunnel;
}
/**
 * Return the TunnelM property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getTunnelM() {
	if (ivjTunnelM == null) {
		try {
			ivjTunnelM = new javax.swing.JMenu();
			ivjTunnelM.setName("TunnelM");
			ivjTunnelM.setText("Service");
			ivjTunnelM.add(getStartMI());
			ivjTunnelM.add(getStopMI());
			ivjTunnelM.add(getFtpSep());
			ivjTunnelM.add(getFtpMI());
			ivjTunnelM.add(getCreateXChannelMI());
			ivjTunnelM.add(getJSeparator2());
			ivjTunnelM.add(getSocksMI());
			ivjTunnelM.add(getCitrixMI());
			ivjTunnelM.add(getRealMI());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTunnelM;
}
/**
 * Return the TunnelMI property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getTunnelMI() {
	if (ivjTunnelMI == null) {
		try {
			ivjTunnelMI = new javax.swing.JMenuItem();
			ivjTunnelMI.setName("TunnelMI");
			ivjTunnelMI.setText("Tunnel");
			ivjTunnelMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						speedTestTunnel();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTunnelMI;
}
/**
 * Return the UserLbl property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUserLbl() {
	if (ivjUserLbl == null) {
		try {
			ivjUserLbl = new javax.swing.JLabel();
			ivjUserLbl.setName("UserLbl");
			ivjUserLbl.setText("User ID:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserLbl;
}
/**
 * Return the UserTF property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getUserTF() {
	if (ivjUserTF == null) {
		try {
			ivjUserTF = new javax.swing.JTextField();
			ivjUserTF.setName("UserTF");
			ivjUserTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getPwdTF().requestFocus();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUserTF;
}
/**
 * Return the VerboseMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getVerboseMI() {
	if (ivjVerboseMI == null) {
		try {
			ivjVerboseMI = new javax.swing.JCheckBoxMenuItem();
			ivjVerboseMI.setName("VerboseMI");
			ivjVerboseMI.setText("Verbose");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjVerboseMI;
}
/**
 * Return the WriteFileMI property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
private javax.swing.JCheckBoxMenuItem getWriteFileMI() {
	if (ivjWriteFileMI == null) {
		try {
			ivjWriteFileMI = new javax.swing.JCheckBoxMenuItem();
			ivjWriteFileMI.setName("WriteFileMI");
			ivjWriteFileMI.setText("Write to File");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjWriteFileMI;
}
/**
 * Return the XChanCanBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getXChanCanBtn() {
	if (ivjXChanCanBtn == null) {
		try {
			ivjXChanCanBtn = new javax.swing.JButton();
			ivjXChanCanBtn.setName("XChanCanBtn");
			ivjXChanCanBtn.setText("Cancel");
			ivjXChanCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getXChanDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjXChanCanBtn;
}
/**
 * Return the JDialogContentPane21 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getXChanCP() {
	if (ivjXChanCP == null) {
		try {
			ivjXChanCP = new javax.swing.JPanel();
			ivjXChanCP.setName("XChanCP");
			ivjXChanCP.setLayout(new java.awt.GridBagLayout());
			ivjXChanCP.setBounds(479, 1439, 253, 167);

			java.awt.GridBagConstraints constraintsJLabel11 = new java.awt.GridBagConstraints();
			constraintsJLabel11.gridx = 0; constraintsJLabel11.gridy = 0;
			constraintsJLabel11.insets = new java.awt.Insets(10, 10, 10, 5);
			getXChanCP().add(getJLabel11(), constraintsJLabel11);

			java.awt.GridBagConstraints constraintsXChanPF = new java.awt.GridBagConstraints();
			constraintsXChanPF.gridx = 1; constraintsXChanPF.gridy = 0;
			constraintsXChanPF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsXChanPF.weightx = 1.0;
			constraintsXChanPF.insets = new java.awt.Insets(10, 0, 10, 10);
			getXChanCP().add(getXChanPF(), constraintsXChanPF);

			java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
			constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 1;
			constraintsJPanel3.gridwidth = 0;
			constraintsJPanel3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanel3.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsJPanel3.weightx = 1.0;
			constraintsJPanel3.weighty = 1.0;
			constraintsJPanel3.insets = new java.awt.Insets(0, 10, 10, 10);
			getXChanCP().add(getJPanel3(), constraintsJPanel3);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjXChanCP;
}
/**
 * Return the JDialog11 property value.
 * @return javax.swing.JDialog
 */
private javax.swing.JDialog getXChanDlg() {
	if (ivjXChanDlg == null) {
		try {
			ivjXChanDlg = new javax.swing.JDialog(this);
			ivjXChanDlg.setName("XChanDlg");
			ivjXChanDlg.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			ivjXChanDlg.setBounds(40, 1942, 253, 167);
			ivjXChanDlg.setTitle("Create X-Channel");
			ivjXChanDlg.setModal(true);
			getXChanDlg().setContentPane(getXChanCP());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjXChanDlg;
}
/**
 * Return the XChanOkBtn property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getXChanOkBtn() {
	if (ivjXChanOkBtn == null) {
		try {
			ivjXChanOkBtn = new javax.swing.JButton();
			ivjXChanOkBtn.setName("XChanOkBtn");
			ivjXChanOkBtn.setText("Ok");
			ivjXChanOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						xchannel(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjXChanOkBtn;
}
/**
 * Return the XChanPF property value.
 * @return javax.swing.JPasswordField
 */
private javax.swing.JPasswordField getXChanPF() {
	if (ivjXChanPF == null) {
		try {
			ivjXChanPF = new javax.swing.JPasswordField();
			ivjXChanPF.setName("XChanPF");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjXChanPF;
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
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("LaunchDSC");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setJMenuBar(getLaunchDSCJMenuBar());
		setSize(503, 453);
		setTitle("Client Software for Design Solutions");
		setContentPane(getJFrameContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				try {
					doClose();
				} catch (java.lang.Throwable ivjExc) {
					handleException(ivjExc);
				}
			}
		});

		// Action listener for manager
		managerActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				managerAction(e);
			}
		};

		// Setup keyboard action for ctrl-shift-D.
		getJFrameContentPane().registerKeyboardAction(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doDebugMenu();
				}
			},KeyStroke.getKeyStroke(KeyEvent.VK_D,KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK,false),JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Monitor the login fields.
		getUserTF().getDocument().addDocumentListener(this);
		getPwdTF().getDocument().addDocumentListener(this);

		// Monitor the certificate password field.
		getCertPF().getDocument().addDocumentListener(this);

		// Monitor the XChannel password field.
		getXChanPF().getDocument().addDocumentListener(this);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/2004 3:22:02 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void insertUpdate(DocumentEvent e) {
	textUpdate(e);
}
/**
 * Comment
 */
public void login() {
	if (getLoginOkBtn().isEnabled()) {
		login = true;
		getLoginDlg().dispose();
	}
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args) {
	// Create the frame and start it up.
	LaunchApp app = new LaunchApp();
	app.begin(args);
}
/**
 * Comment
 */
public void managerAction(ActionEvent e) {
	if (e.getActionCommand().equalsIgnoreCase("RewrappedToken")) {
		token = getManager().getToken();
		tunnelArgs[5] = getManager().getLoginToken();
	}
	else if (e.getActionCommand().equalsIgnoreCase("TunnelShutdown")) {
		tunnelStopped();
	}
	else if (e.getActionCommand().equalsIgnoreCase("XEarCreated")) {
		// Do nothing, perhaps a pop-up that X port is established.
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchNEWODC")) {
		doStartNewODC2((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchICA")) {
		doStartICA((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchIM")) {
		doStartIM((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchRM")) {
		doStartRM((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchFTP")) {
		doStartFTP((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchXFR")) {
		doStartDropBox((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchFDR")) {
		doStartGrid((TunnelEvent) e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("THRUPUT-CLIENT") ||
			e.getActionCommand().equalsIgnoreCase("THRUPUT-SERVER")) {
		speedTestTunnel();
	}
	else if (e.getActionCommand().equalsIgnoreCase("ConnectivityOK")) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(LaunchApp.this,"Connectivity to host is OK!","Test Results",JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	else if (e.getActionCommand().equalsIgnoreCase("ConnectivityChanged")) {
		//System.err.println("Calling forceConnectionRestart()");
		HttpTunnelClient t = getTunnel();
		if (t != null)
			t.forceConnectionRestart();
		//System.err.println("Returned from forceConnectionRestart()");
	}
	else if (e.getActionCommand().equalsIgnoreCase("LaunchDOC")) {
		doStartDOC((TunnelEvent)e);
	}
	else if (e.getActionCommand().equalsIgnoreCase("MsgFromServer")) {
		String msg = (String) ((TunnelEvent) e).parm1;

		// Set message
		if (getMsgDlgTA().getText().length() > 0)
			getMsgDlgTA().append("\n");
		getMsgDlgTA().append(msg);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Center the window
				if (! getMsgDlg().isShowing()) {
					getMsgDlg().setLocationRelativeTo(LaunchApp.this);
				}
				getMsgDlg().setVisible(true);
				getMsgDlg().toFront();
			}
		});
	}
	else {
		System.out.println("Unknown ActionEvent from SessionMananger: " + e.getActionCommand());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/2004 3:22:02 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void removeUpdate(DocumentEvent e) {
	textUpdate(e);
}
/**
 * Insert the method's description here.
 * Creation date: (03/02/01 12:55:28 PM)
 */
public void run() {
	if (Thread.currentThread() != statsThread) {
		try {
			// start testing.
			getTunnelMI().setEnabled(false);
			getNetworkMI().setEnabled(false);
			tstChunkSize = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_INITSIZE",10240);
			long tstTimeLimit = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_TIME",20);
			System.out.println("Performing network speed test (" + tstTimeLimit + "seconds to completion)");
			tstTimeLimit = tstTimeLimit * 500; // time * 1000 / 2
			tstTotalSize = 0;
			tstStart = System.currentTimeMillis();
			long dl = tstChunkSize;
			long bps = 0;
			byte b[] = new byte[4096];

			// download testing.
			while (dl > 0) {
				System.out.println("Downloading " + tstChunkSize/1024 + "KB.");
				long midStart = System.currentTimeMillis();

				// Connect to download servlet and request tstChunkSize.
				String url = DebugPrint.urlRewrite(tunnelArgs[1] + "/servlet/oem/edge/ed/odc/desktop/speedtest?download=" + tstChunkSize + "&compname=" + getManager().getToken(),getManager().desktopID());
				URL testUrl = new URL(url);
				URLConnection2 testConn = new URLConnection2(testUrl);
				testConn.setUseCaches(false);
				testConn.setDefaultUseCaches(false);

				String response = testConn.getHeaderField("errorstring");
				if (response != null)
					throw new Exception(response);

				// Read data from servlet.
				InputStream testIn = testConn.getInputStream();
				long tot = 0;
				int amt = 0;
				while ((amt = testIn.read(b)) != -1)
					tot += amt;
				testIn.close();

				if (tot != tstChunkSize) {
					System.out.println();
					throw new Exception("Data receive mismatch.");
				}

				long ct = System.currentTimeMillis();
				long dt = (ct - midStart);
				if (dt <= 0) dt = 1;

				bps = (tstChunkSize * 1000) / dt;
				System.out.println("Interim result: downloaded " + tstChunkSize/1024 + "KB at "  + bps/1024 + "KB/s.");

				tstTotalSize += tstChunkSize;
				bps = (tstTotalSize * 1000) / (ct - tstStart);
				long timeLeft = tstStart + tstTimeLimit - ct;
				dl = bps * timeLeft / 1000;
				tstChunkSize = Math.min(tstChunkSize * 10,dl);
			}

			//System.out.println("Started: " + tstStart + "  Ended: " + ct);
			System.out.println("Downloaded " + tstTotalSize/1024 + "KB at " + bps/1024 + "KB/s.");

			tstChunkSize = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_INITSIZE",10240);;
			tstTotalSize = 0;
			tstStart = System.currentTimeMillis();
			dl = tstChunkSize;

			// upload testing.
			while (dl > 0) {
				System.out.println("Uploading " + tstChunkSize/1024 + "KB.");
				long midStart = System.currentTimeMillis();

				// Connect to upload servlet and request tstChunkSize.
				String url = DebugPrint.urlRewrite(tunnelArgs[1] + "/servlet/oem/edge/ed/odc/desktop/speedtest?upload=" + tstChunkSize + "&compname=" + getManager().getToken(),getManager().desktopID());
				URL testUrl = new URL(url);
				URLConnection2 testConn = new URLConnection2(testUrl);
				testConn.setDoInput(true);
				testConn.setDoOutput(true);
				testConn.setUseCaches(false);
				testConn.setDefaultUseCaches(false);
				testConn.setSendContentLength((int) tstChunkSize);
				testConn.setRequestProperty("Content-Type","application/octet-stream");

				// Send data to servlet.
				OutputStream testOut = testConn.getOutputStream();
				long tot = tstChunkSize;
				int amt = (int) Math.min(tot,b.length);
				while (tot > 0) {
					testOut.write(b,0,amt);
					tot -= amt;
					amt = (int) Math.min(tot,b.length);
				}
				testOut.close();

				// Get confirmation from servlet.
				String response = testConn.getHeaderField("errorstring");
				if (response != null)
					throw new Exception(response);

				response = testConn.getHeaderField("bytecount");
				if (response == null || Integer.parseInt(response) != tstChunkSize)
					throw new Exception("Data send mismatch");

				long ct = System.currentTimeMillis();
				long dt = ct - midStart;
				if (dt <= 0) dt = 1;

				bps = (tstChunkSize * 1000) / dt;
				System.out.println("Interim result: uploaded " + tstChunkSize/1024 + "KB at "  + bps/1024 + "KB/s.");

				tstTotalSize += tstChunkSize;
				bps = (tstTotalSize * 1000) / (ct - tstStart);
				long timeLeft = tstStart + tstTimeLimit - ct;
				dl = bps * timeLeft / 1000;
				tstChunkSize = Math.min(tstChunkSize * 10,dl);
			}

			//System.out.println("Started: " + tstStart + "  Ended: " + ct);
			System.out.println("Uploaded " + tstTotalSize/1024 + "KB at " + bps/1024 + "KB/s.");
		}
		catch (Exception e) {
			System.out.println("Network speed test error:" + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("Network speed test completed.");
		speedThread = null;
		getTunnelMI().setEnabled(true);
		getNetworkMI().setEnabled(true);
		return;
	}

	SessionManager mgr = null;
	SMStats statsTot = null;
	StringBuffer time = new StringBuffer();
	int i = 0;
	SimpleDateFormat F = new SimpleDateFormat("HH:mm:ss",Locale.US);
	getPerformanceIndicator().clear();

	while (Thread.currentThread() == statsThread) {
		if (mgr == null)
			mgr = getManager();

		if (mgr != null) {
			final SMStats statsCur = mgr.getSinceLastStats();

			// Update the status fields.
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable () {
				public void run() {
			getPerformanceIndicator().setRate(statsCur.getTotUncompressedIn(),statsCur.getTotUncompressedOut(),statsCur.getTotIn()+statsCur.getTotOut());
				}
			});
		}
		catch (Exception e1) {
			System.out.println("Dropbox.dbAction: Swing.i&W failed(1): " + e1.getMessage());
			System.out.println("Attempting thread risky version.");

			getPerformanceIndicator().setRate(statsCur.getTotUncompressedIn(),statsCur.getTotUncompressedOut(),statsCur.getTotIn()+statsCur.getTotOut());
		}

			// If this is a 60 second interval, update the log.
			if (statsTot != null && i == 60) {
				long total = statsTot.getTotIn() + statsTot.getTotOut();
				long sessionTotal = mgr.getTotIn() + mgr.getTotOut();

				if (getStatsMI().getState())
					System.out.println(F.format(new Date()) + " Data (Bpm) - I/O: " +
						statsTot.getTotIn() + "/" + statsTot.getTotOut() +
						" Tot: " + total + " Avg Bps: " + total/60 +
						" SessTot: " + sessionTotal);

				statsTot.reset();
				i = 0;
			}

			// Otherwise, if this is the first time, create a total stats object.
			else if (statsTot == null)
				statsTot = new SMStats(statsCur);

			// Otherwise, cumulate stats for 1 minute.
			else
				statsTot.addMerge(statsCur);

			mgr.resetSinceLastStats();
		}

		if (Thread.currentThread() == statsThread) {
			try { Thread.sleep(1000); }
			catch (Exception e) {};
			i++;
		}
	}
}
/**
 * Comment
 */
public void saveText(ActionEvent e) {
	if (e.getSource() == getSaveAsMI() || textFile == null) {
		if (getFileDlg().showSaveDialog(this) ==  JFileChooser.APPROVE_OPTION)
			textFile = getFileDlg().getSelectedFile();
	}

	if (textFile != null) {
		try {
			FileWriter f = new FileWriter(textFile);
			PrintWriter p = new PrintWriter(f);
			getMsgTA().write(p);
			p.close();
		}
		catch (IOException e1) {
			JOptionPane.showMessageDialog(this,e1.getMessage(),"Save Failed",JOptionPane.ERROR_MESSAGE);
		}
	}
}
/**
 * Set the ConfigMgr to a new value.
 * @param newValue oem.edge.ed.odc.applet.ConfigMgr
 */
private void setConfigMgr(ConfigMgr newValue) {
	if (ivjConfigMgr != newValue) {
		try {
			ivjConfigMgr = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Comment
 */
public void setDebugLevel(ActionEvent e) {
	if (e.getSource() == getInfoMI())
		DebugPrint.setLevel(DebugPrint.INFO);
	else if (e.getSource() == getInfo2MI())
		DebugPrint.setLevel(DebugPrint.INFO2);
	else if (e.getSource() == getInfo3MI())
		DebugPrint.setLevel(DebugPrint.INFO3);
	else if (e.getSource() == getInfo4MI())
		DebugPrint.setLevel(DebugPrint.INFO4);
	else if (e.getSource() == getInfo5MI())
		DebugPrint.setLevel(DebugPrint.INFO5);
	else if (e.getSource() == getDebugMI())
		DebugPrint.setLevel(DebugPrint.DEBUG);
	else if (e.getSource() == getDebug2MI())
		DebugPrint.setLevel(DebugPrint.DEBUG2);
	else if (e.getSource() == getDebug3MI())
		DebugPrint.setLevel(DebugPrint.DEBUG3);
	else if (e.getSource() == getDebug4MI())
		DebugPrint.setLevel(DebugPrint.DEBUG4);
	else
		DebugPrint.setLevel(DebugPrint.DEBUG5);

	return;
}
/**
 * Set the Tunnel to a new value.
 * @param newValue oem.edge.ed.odc.tunnel.applet.HttpTunnelClient
 */
private void setTunnel(oem.edge.ed.odc.tunnel.applet.HttpTunnelClient newValue) {
	if (ivjTunnel != newValue) {
		try {
			ivjTunnel = newValue;
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	};
}
/**
 * Comment
 */
public void speedTestNetwork() {
	speedThread = new Thread(this);
	speedThread.start();
}
/**
 * Comment
 */
public void speedTestTunnel() {
	switch(tstPhase) {
		case 0: // start testing.
			getTunnelMI().setEnabled(false);
			getNetworkMI().setEnabled(false);
			long tstTimeLimit = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_TIME",20);
			System.out.println("Performing tunnel speed test (" + tstTimeLimit + "seconds to completion)");
			tstTimeLimit = tstTimeLimit * 500; // time * 1000 / 2
			tstChunkSize = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_INITSIZE",10240);
			tstTotalSize = 0;
			tstPhase = 1;
			tstStart = System.currentTimeMillis();
			tstMidStart = tstStart;
			getTunnel().thruputTestDownload((int) tstChunkSize);
			System.out.println("Downloading " + tstChunkSize/1024 + "KB.");
			break;

		case 1: // download testing.
			long ct = System.currentTimeMillis();
			long dt = ct - tstMidStart;
			if (dt <= 0) dt = 1;

			long bps = (tstChunkSize * 1000) / dt;
			System.out.println("Interim result: downloaded " + tstChunkSize/1024 + "KB at "  + bps/1024 + "KB/s.");

			tstTotalSize += tstChunkSize;
			bps = (tstTotalSize * 1000) / (ct - tstStart);
			tstTimeLimit = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_TIME",20);
			tstTimeLimit = tstTimeLimit * 500; // time * 1000 / 2
			long timeLeft = tstStart + tstTimeLimit - ct;
			long dl = bps * timeLeft / 1000;

			if (dl > 0) {
				tstMidStart = ct;
				tstChunkSize = Math.min(tstChunkSize * 10,dl);
				getTunnel().thruputTestDownload((int) tstChunkSize);
				System.out.println("Downloading " + tstChunkSize/1024 + "KB.");
			}
			else {
				//System.out.println("Started: " + tstStart + "  Ended: " + ct);
				System.out.println("Downloaded " + tstTotalSize/1024 + "KB at " + bps/1024 + "KB/s.");
				tstChunkSize = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_INITSIZE",10240);
				tstTotalSize = 0;
				tstPhase = 2;
				tstStart = System.currentTimeMillis();
				tstMidStart = tstStart;
				getTunnel().thruputTestUpload((int) tstChunkSize);
				System.out.println("Uploading " + tstChunkSize/1024 + "KB.");
			}
			break;

		case 2: // upload testing.
			ct = System.currentTimeMillis();
			dt = ct - tstMidStart;
			if (dt <= 0) dt = 1;

			bps = (tstChunkSize * 1000) / dt;
			System.out.println("Interim result: uploaded " + tstChunkSize/1024 + "KB at "  + bps/1024 + "KB/s.");

			tstTotalSize += tstChunkSize;
			bps = (tstTotalSize * 1000) / (ct - tstStart);
			tstTimeLimit = getConfigMgr().getCfg().getIntProperty("SPEEDTEST_TIME",20);
			tstTimeLimit = tstTimeLimit * 500; // time * 1000 / 2
			timeLeft = tstStart + tstTimeLimit - ct;
			dl = bps * timeLeft / 1000;

			if (dl > 0) {
				tstMidStart = ct;
				tstChunkSize = Math.min(tstChunkSize * 10,dl);
				getTunnel().thruputTestUpload((int) tstChunkSize);
				System.out.println("Uploading " + tstChunkSize/1024 + "KB.");
			}
			else {
				//System.out.println("Started: " + tstStart + "  Ended: " + ct);
				System.out.println("Uploaded " + tstTotalSize/1024 + "KB at " + bps/1024 + "KB/s.");
				System.out.println("Tunnel speed test completed.");
				tstPhase = 0;
				getTunnelMI().setEnabled(true);
				getNetworkMI().setEnabled(true);
			}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (03/09/01 8:49:07 AM)
 */
public void startTunnel() {
	// If we have no token, then we need to get the ID and PWD from the user
	// to obtain a token. doLogin will call us once a valid token is obtained.
	if (tunnelArgs[5] == null) {
		getLoginLbl().setText("");
		doLogin();
		return;
	}

	// Desktop-on-call.
	if (isDOCTunnelCommand == true) {
		CertPWD = null;
		getCertPF().setText("");
		getCertOkBtn().setEnabled(false);
		getCertDlg().setLocationRelativeTo(this);
		getCertDlg().setVisible(true);
	}
/*
	if (CertPWD != null) {
		String keyringClassname = "NIIIPKeyRing";
		try {
			SSLightKeyRing ring  = null;
			SSLContext myctx = new SSLContext();
			ring = (SSLightKeyRing) Class.forName(keyringClassname).newInstance();
			String keydata = ring.getKeyRingData();
			myctx.importKeyRings(keydata, CertPWD);
		}
		// catch(IOException ioe){
		catch (Exception e) {
			System.out.println("cert not opened.");
			System.out.println(e.getMessage());
			startTunnel();
			return;
		}
		//catch(Exception e){
		//	System.out.println("cert not opened..Exception");
		//}

		try {
			URLConnection2.setKeyringClassName(keyringClassname);
			URLConnection2.setKeyringPassword(CertPWD);
		}
		catch (Exception e) {
			if (CertPWD != null) {
				System.out.println("Incorrect password for the certificate...");
				return;
			}
		}
	}
*/
	try {
		setTitle(title + "Connecting");
		
		// Start the tunnel and enable Stop if it starts.
		setTunnel(HttpTunnelClient.createTunnel(tunnelArgs,managerActionListener));
		
		// If createTunnel didn't throw an exception...
		ivjManager = getTunnel().getSessionManager();
		getTunnel().begin();

		// Ok, enable the various menu items.
		getStartMI().setEnabled(false);
		getStopMI().setEnabled(true);
		getFtpMI().setEnabled(true);
		getCreateXChannelMI().setEnabled(true);
		getTunnelMI().setEnabled(true);
		getNetworkMI().setEnabled(true);
		getVerboseMI().addItemListener(getTunnel());
		getAutoPingMI().addItemListener(getTunnel());
		getKeepAliveMI().addItemListener(getTunnel());
		getCompressMI().addItemListener(getTunnel());
		getNormalMI().addItemListener(getTunnel());

		// Fire up the statistics thread.
		statsThread = new Thread(this);
		statsThread.start();
	}
	catch (Exception e) {
		setTitle(title + "Disconnected");

		// Tunnel failed to start. Message window has reason.
		if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("token-expired")) {
			// Token expired. Need to log in again.
			getLoginLbl().setText("Authentication expired");
			doLogin();
		}
		else {
			// Enable Start button (shouldn't have to, but...)
			getStartMI().setEnabled(true);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (03/09/01 8:49:07 AM)
 */
public void stopTunnel() {
	// Stop the tunnel.
	getTunnel().getSessionManager().shutdown();
	getStopMI().setEnabled(false);
	getFtpMI().setEnabled(false);
	getCreateXChannelMI().setEnabled(false);

	// Shouldn't need to enable Start, we'll get an event when the tunnel ends.
	// getStartMI().setEnabled(true);

	// Stop the statistics thread.
	statsThread.interrupt();
	statsThread = null;

	// Stop any speed test currently running.
	if (speedThread != null) {
		speedThread.interrupt();
		speedThread = null;
	}

	getTunnelMI().setEnabled(false);
	getNetworkMI().setEnabled(false);
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 8:29:12 AM)
 * @param msg java.lang.String
 */
public void syntax(String msg) {
	if (msg != null)
		System.err.println(msg);

	System.out.println("Command syntax:");
	System.out.println("  java oem.edge.ed.odc.applet.LaunchApp <parameter file>");
	System.out.println("");
	System.out.println("where <parameter file> is a file which contains:\n");
	System.out.println("\tSELFSTART");
	System.out.println("\t-URL <URL> where <URL> is the url of the web server");
	System.out.println("\t" + CMD_OPT + " <CMD> where <CMD> is DSH, DGP, or ODC");
	System.out.println("\t" + HOSTM_OPT + " <MAC> where <MAC> is the hosting machine to connect to (DSH only)\n");
	System.out.println("and <parameter file> may optionally contain:\n");
	System.out.println("\t" + TOKEN_OPT + " <ID> where <ID> is a valid login token");
	System.out.println("\t" + DEBUG_OPT);
	System.out.println("\t" + NOSTR_OPT);
	System.out.println("\t" + NOUPDATE_OPT);

	System.err.println("Unable to start Client Software for Customer Connect.");
}
/**
 * Comment
 */
public void textChgCert() {
	char[] pwd = getCertPF().getPassword();
	CertPWD = pwd == null ? null : new String(pwd);

	getCertOkBtn().setEnabled(CertPWD != null && CertPWD.length() > 0);
}
/**
 * Comment
 */
public void textChgLogin() {
	String user = getUserTF().getText();
	char[] PWD = getPwdTF().getPassword();
	String pwd = PWD == null ? null : new String(PWD);

	getLoginOkBtn().setEnabled(user != null && user.length() > 0 &&
		pwd != null && pwd.length() > 0);
}
/**
 * Comment
 */
public void textChgXChan() {
	char[] PWD = getXChanPF().getPassword();
	String pwd = PWD == null ? null : new String(PWD);

	getXChanOkBtn().setEnabled(pwd != null && pwd.length() > 0);
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/2004 3:22:02 PM)
 * @param e javax.swing.event.DocumentEvent
 */
public void textUpdate(DocumentEvent e) {
	if (e.getDocument() == getUserTF().getDocument()) {
		textChgLogin();
	}
	else if (e.getDocument() == getPwdTF().getDocument()) {
		textChgLogin();
	}
	else if (e.getDocument() == getCertPF().getDocument()) {
		textChgCert();
	}
	else if (e.getDocument() == getXChanPF().getDocument()) {
		textChgXChan();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (03/09/01 8:49:07 AM)
 */
public void tunnelStopped() {
	setTitle(title + "Disconnected");

	// Tunnel stopped, set menu items appropriately.
	getStopMI().setEnabled(false);
	getFtpMI().setEnabled(false);
	getStartMI().setEnabled(true);
	getVerboseMI().removeItemListener(getTunnel());
	getAutoPingMI().removeItemListener(getTunnel());
	getKeepAliveMI().removeItemListener(getTunnel());
	getCompressMI().removeItemListener(getTunnel());
	getNormalMI().removeItemListener(getTunnel());
	setTunnel(null);
	ivjManager = null;

	// If the system is exiting, the tunnel stopped because
	// the user asked it to during exit. Just call doTerminate again.
	if (systemExiting)
		doTerminate();

	// If we are not displayed, show it...
	if (! isVisible()) {
		setVisible(true);
	}

	// If the statistics thread is going, then we didn't
	// stop the tunnel, so we should stop the stats anyway.
	if (statsThread != null) {
		statsThread.interrupt();
		statsThread = null;
	}
}
public boolean verifyJava14Plus() {
	// Ok, we got in. that means it is at least a java engine.
	String version = "1.0";
	boolean isJava14orHigher = false;

	// Now, delineate 1.1 and/or 1.2
	try {
		Class FILE = Class.forName("java.io.File");
		Method DOE = FILE.getMethod("deleteOnExit",null);
		if (DOE != null)
			version = "1.2";
	}
	catch (NoSuchMethodException e1) {
		// Reflection exists, but no deleteOnExit
		version = "1.1";
	}
	catch (ClassNotFoundException e2) {
		// Reflection does not exist, must be 1.0
	}

	// Now, if 1.2, try for 1.3
	if (version.equals("1.2")) {
		try {
			Class TIMER = Class.forName("java.util.Timer");
			if (TIMER != null)
				version = "1.3";
		}
		catch (ClassNotFoundException e1) {
			// java.util.Timer does not exist, must be 1.2
		}
	}

	// Now, if 1.3, try for 1.4
	if (version.equals("1.3")) {
		try {
			Class SA = Class.forName("java.net.SocketAddress");
			if (SA != null) {
				version = "1.4";
				isJava14orHigher = true;
			}
		}
		catch (ClassNotFoundException e1) {
			// java.net.SocketAddress does not exist, must be 1.3
		}
	}

	return isJava14orHigher;
}
/**
 * Comment
 */
public void xchannel(ActionEvent actionEvent) {
	getXChanDlg().dispose();

	char[] PWD = getXChanPF().getPassword();
	String pwd = PWD == null ? null : new String(PWD);

	com.ibm.as400.webaccess.common.ConfigObject cf = new com.ibm.as400.webaccess.common.ConfigObject();
	cf.setProperty("command", "createXChannel");
	cf.setProperty("PassPhrase", pwd);  // JMC
	getManager().writeControlCommand(cf);

	getPwdSubDlg().setLocationRelativeTo(this);
	getPwdSubDlg().setVisible(true);
}
	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCloseMI() {
		if(closeMI == null) {
			closeMI = new javax.swing.JMenuItem();
			closeMI.setText("Close Window");
			closeMI.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						doClose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		}
		return closeMI;
	}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
