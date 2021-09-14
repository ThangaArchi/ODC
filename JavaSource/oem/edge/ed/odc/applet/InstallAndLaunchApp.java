package oem.edge.ed.odc.applet;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import sun.misc.Cleaner;

import netscape.security.PrivilegeManager;

import com.ms.security.PermissionID;
import com.ms.security.PolicyEngine;

/**
 * Insert the type's description here.
 * Creation date: (01/19/01 3:07:06 PM)
 * @author: Michael Zarnick
 */
public class InstallAndLaunchApp extends Applet implements Runnable {
	private boolean isJava1 = true;
	private boolean firstTime = true;
	private boolean stopSearch = false;
	private boolean updatedIni = false;
	private String lastJreValidated = null;
	private int installStatus = 0; // 0 - not installed, 1 - installed, 2 - user declined.
	private ConfigFile versionStamps = null;
	static public int INIVERSION = 15;
	static public String DSC_FILE = "EDODCTunnelClient.jar";
	static public String SCRIPT_FILE = "csccinfo.js";
	static public String LDSSCRIPT_FILE = "launchds.js";
	static public String SOD_FILE = "ibmsod.jar";
	static public String DSMP_FILE = "DSMP.jar";
	static public String IM1_FILE = "CommRes.jar";
	static public String IM2_FILE = "STComm20.jar";
	static public String XML_FILE = "ibmxml.jar";
	static public String ICA1_FILE = "JICA-coreN.jar";
	static public String ICA2_FILE = "JICA-configN.jar";
	static public String ICA3_FILE = "JICAJ.jar";
	static public String WIN_FILE = "odc-win32-x86.zip";
	static public String AIX_GZFILE = "odc-aix-ppc.tar.gz";
	static public String LINUX_GZFILE = "odc-linux-x86.tar.gz";
	static public String LWIN_FILE = "startds.exe";
	static public String LUNIX_FILE = "startds.sh";
	static public String WIN1_LIB = "XScraper.dll";
	static public String WIN2_LIB = "XSMouse32.dll";
	static public String AIX_LIB = "libXScraper-AIX.so";
	static public String LIN_LIB = "libXScraper-Linux86.so";
	static public String SUN1_LIB = "libXScraper-SunOS.so";
	static public String SUN2_LIB = "libXScraper-SunOS-green.so";
	static public String HPUX1_LIB = "libXScraper-HP-UX.sl";
	static public String HPUX2_LIB = "libXScraper-HP-UX-green.sl";
	static private SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
	static private String GetFileAnyPath = "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/";
	private File ini = null;
	private File instini = null;
	private boolean updateINI = false;
	private ConfigFile cfg = null;
	private ConfigFile instcfg = null;
	private int runMode = 0;
	private boolean msgDlgOk = false;
	private boolean debug = false;
	private String debugApp = null;
	private String command = null;
	private boolean isSD = false;
	private boolean isSOD = false;
	private boolean isIM = false;
	private boolean isODC = false;
	private boolean isEDU = false;
	private boolean isNEWODC = false;
	private boolean isDSH = false;
	private boolean isDBOX = false;
	private String tunnelcommand = null;
	private String callmtg = null;
	private String callusr = null;
	private String callpwd = null;
	private String token = null;
	private String hostingmachine = null;
	private String context = null;
	private String nostream = null;
	private String sdtoken = null;
	private String url = null;
	private String appletUrl = null;
	private String project = null;
	public java.lang.String plat = null;
	private boolean platformUnsupported = false;
	private int foundJRE = 2;
	private boolean foundICA = false;
	private boolean foundDSC = false;
	private boolean foundSOD = false;
	private boolean foundDSMP = false;
	private boolean foundIM = false;
	private boolean foundXML = false;
	private boolean foundLAUNCH = false;
	private boolean foundLIB = false;
	private boolean foundSCRIPT = false;
	private boolean foundLDS = false;
	private TextArea ivjStatusTA = null;
	private Panel ivjContentsPane = null;
	private Label ivjInstallDirLbl = null;
	private TextField ivjInstallDirTF = null;
	private Dialog ivjSaveDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="31,575"
	private Panel ivjContentsPane1 = null;
	private Button ivjMsgCanBtn = null;
	private Dialog ivjMsgDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="35,1269"
	private Label ivjMsgLbl1 = null;
	private Label ivjMsgLbl2 = null;
	private Button ivjMsgOkBtn = null;
	private Panel ivjPanel1 = null;
	private Panel ivjPanel3 = null;
	private Panel ivjContentsPane3 = null;
	private Button ivjInstallCanlBtn = null;
	private Button ivjInstallOkBtn = null;
	private Button ivjLicCanBtn = null;
	private Label ivjLicLbl = null;
	private Button ivjLicOkBtn = null;
	private TextArea ivjLicTA = null;
	private Panel ivjPanel = null;
	private Dialog ivjLicDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="34,829"
	private Label ivjLicTitleLbl = null;
	private FileDialog ivjFileDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="440,20"
	private Frame ivjConsole = null;  // @jve:visual-info  decl-index=0 visual-constraint="36,181"
	private Button ivjConsoleOkBtn = null;
	private Panel ivjContentsPane5 = null;
	private ProgressBar ivjProgressBar = null;
	private Label ivjStatusLbl = null;
	private Panel ivjContentsPane6 = null;
	private Checkbox ivjInstallCB = null;
	private CheckboxGroup ivjInstallCBG = null;  // @jve:visual-info  decl-index=0 visual-constraint="541,1507"
	private Button ivjInstallJRECanlBtn = null;
	private Button ivjInstallJREOkBtn = null;
	private Label ivjLabel1 = null;
	private Checkbox ivjNoInstallCB = null;
	private Panel ivjPanel31 = null;
	private Dialog ivjInstallJreDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="36,1511"
	private Button ivjBrowseBtn = null;
	private Label ivjJreLbl = null;
	private TextField ivjJreTF = null;
	private Label ivjJreValidLbl = null;
	private TextArea ivjJreTA = null;
	private Panel ivjContentsPane2 = null;
	private Dialog ivjPersJreDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="37,2017"
	private Button ivjPersJreOkBtn = null;
	private TextArea ivjPersJreTA = null;
	private Panel ivjContentsPane4 = null;
	private Button ivjDetailsBtn = null;
	private Button ivjSearchCanBtn = null;
	private Dialog ivjSearchDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="36,2331"
	private Label ivjSearchLbl = null;
	private Label ivjJreInfoLbl = null;
	private Button ivjJreSearchBtn = null;
	private Panel ivjContentsPane21 = null;
	private Button ivjJreUpgradeCanBtn = null;
	private Checkbox ivjJreUpgradeCB = null;
	private Dialog ivjJreUpgradeDlg = null;  // @jve:visual-info  decl-index=0 visual-constraint="481,2017"
	private Button ivjJreUpgradeOkBtn = null;
	private TextArea ivjJreUpgradeTA = null;

public static String[] platform() {
	String[] result = new String[2];

	// Determine the platform we are on...
	String platform = System.getProperty("os.name") + " " + System.getProperty("os.version") +
				", Arch: " + System.getProperty("os.arch");
	platform = platform.toUpperCase();

	// Setting the platform extension for future use. 
	String plat;
	if (platform.indexOf("WIN") != -1) 
		plat ="WIN";
	else if (platform.indexOf("AIX") != -1)
		plat= "AIX";
	else if (platform.indexOf("SUN") != -1 && platform.indexOf("SPARC") != -1)
		plat = "SUNSP";
	else if (platform.indexOf("LINUX") != -1 && platform.indexOf("86") != -1)
		plat = "LIN";
	// While we don't deliver a JRE for HP or Mac, we do recognize them
	// as a known platform and provide a web conference scraper for them.
	else if (platform.indexOf("HP-UX") != -1)
		plat = "HPUX";
	else if (platform.indexOf("MAC OS X") != -1)
		plat = "MACOSX";
	// For all others, it is pot-luck. We fabricate a platform name. We'll
	// insist that the platform provide the JRE and we'll assume it to
	// be a linux variant. uname will need to match our platform name in
	// order for startds.sh to launch successfully.
	else {
		String os = System.getProperty("os.name").toUpperCase();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < os.length(); i++) {
			if (Character.isLetterOrDigit(os.charAt(i)))
				sb.append(os.charAt(i));
		}
		plat = sb.toString();
	}

	result[0] = platform;
	result[1] = plat;

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (02/15/01 2:11:09 PM)
 */
public void chmod(String perms, String f[]) throws Exception {
	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalExecAccess");
			PolicyEngine.assertPermission(PermissionID.EXEC);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			throw e;
		}
	}

	String[] app = new String[2];
	app[0] = "/bin/sh";
	app[1] = "-s";

	Process p = Runtime.getRuntime().exec(app);

	PrintStream in = new PrintStream(p.getOutputStream());
	for (int i = 0; i < f.length; i++)
		in.println("chmod " + perms + " " + f[i]);
	in.println("echo DONE");
	in.close();
	
	BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
	String line = rdr.readLine();
	while (line != null && ! line.startsWith("DONE")) {
		line = rdr.readLine();
	}
	rdr.close();
}
/**
 * Insert the method's description here.
 * Creation date: (02/15/01 2:11:09 PM)
 */
public void chmod(String perms,String f) throws Exception {
	String[] fa = new String[1];
	fa[0] = f;
	chmod(perms,fa);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 */
public int chooseJRE(String installDir,boolean isOptional) {
	// Set up the JRE selection dialog.
	if (platformUnsupported) {
		getInstallCB().setEnabled(false);
		getInstallCB().setState(false);
		getNoInstallCB().setState(true);
		getJreLbl().setEnabled(true);
		getJreTF().setEnabled(true);
		getBrowseBtn().setEnabled(true);
		getJreInfoLbl().setEnabled(true);
		getJreSearchBtn().setEnabled(true);
		getInstallJREOkBtn().setEnabled(false);
	}
	else {
		getInstallCB().setEnabled(true);
		getNoInstallCB().setState(false);
		getInstallCB().setState(true);
		getJreLbl().setEnabled(false);
		getJreTF().setEnabled(false);
		getBrowseBtn().setEnabled(false);
		getJreInfoLbl().setEnabled(false);
		getJreSearchBtn().setEnabled(false);
		getInstallJREOkBtn().setEnabled(true);
	}

	// Locate a JRE for this platform and pre-fill JreTF.
	boolean localJreFound = false;

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalFileRead");
			PolicyEngine.assertPermission(PermissionID.USERFILEIO);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return 0;
		}
	}

	if (plat.equalsIgnoreCase("WIN")) {
		File java = new File("c:\\Program Files\\Java");
		if (java.exists()) {
			File found = searchWin(java);
			if (found != null) {
				localJreFound = true;
				getJreTF().setText(found.getPath());
			}
		}
	}
	else {
		File binjava = new File("/bin/java");
		File ubinjava = new File("/usr/bin/java");

		if (binjava.exists() && validateJRE(binjava.getPath()) == 0) {
			localJreFound = true;
			getJreTF().setText(binjava.getPath());
		}
		else if (ubinjava.exists() && validateJRE(ubinjava.getPath()) == 0) {
			localJreFound = true;
			getJreTF().setText(ubinjava.getPath());
		}
	}

	if (localJreFound && ! isOptional) {
		getInstallCB().setState(false);
		getNoInstallCB().setState(true);
		getJreLbl().setEnabled(true);
		getJreTF().setEnabled(true);
		getBrowseBtn().setEnabled(true);
		getJreInfoLbl().setEnabled(true);
		getJreSearchBtn().setEnabled(true);
		getInstallJREOkBtn().setEnabled(true);
	}

	// Update message text. If isOptional, then we are here because we can deliver an
	// updated JRE to replace our previously installed JRE.
	if (isOptional) {
		getJreTA().setText("An update to the previously installed JRE is available.");
		getJreTA().append(" To install this update, select the \"Install the supplied JRE\" checkbox above.");
		getJreTA().append(" To use your own JRE, select the");
		getJreTA().append(" \"Use the following JRE\" checkbox above and follow the instructions below.");
	}
	else {
		getJreTA().setText("A Java 5 Runtime Environment (or higher) is required.");

		if (platformUnsupported)
			getJreTA().append(" IBM does not provide a JRE for your platform.");
		else {
			getJreTA().append(" IBM provides a JRE for your platform. To install the supplied JRE,");
			getJreTA().append(" select the \"Install the supplied JRE\" checkbox above. To use your own JRE, select the");
			getJreTA().append(" \"Use the following JRE\" checkbox above and follow the instructions below.");
		}
	}

	if (localJreFound) {
		getJreTA().append("\n\nA quick search found a valid JRE at the location specified above.");
		getJreTA().append(" To use this JRE, push the Ok button.");
		getJreTA().append(" To locate a different JRE,");
		getJreTA().append(" push the Browse button to select the JRE or enter its path above.");
	}
	else if (plat.indexOf("WIN") != -1) {
		getJreTA().append("\n\nA quick search was unable to locate a JRE on your machine. If you");
		getJreTA().append(" know the location of a JRE (the executable), push the Browse button to select it or enter");
		getJreTA().append(" its path above. It is usually named java.exe.");
		getJreTA().append(" Alternatively, push the Search button to have the installer search drive C for a JRE.");
		getJreTA().append(" If search is unsuccessful and you need help to locate or install a JRE for your");
		getJreTA().append(" platform, contact your company's support personnel.");
	}
	else {
		getJreTA().append("\n\nA quick search was unable to locate a JRE on your machine. If you");
		getJreTA().append(" know the location of a JRE (the executable), push the Browse button to select it or enter");
		getJreTA().append(" its path above. It is usually named java.");
		getJreTA().append(" Alternatively, push the Search button to have the installer search /usr, /bin & /opt for a JRE.");
		getJreTA().append(" If search is unsuccessful and you need help to locate or install a JRE for your");
		getJreTA().append(" platform, contact your company's support personnel.");
	}

	// Now prompt for which JRE to use.
	setLocation2(getInstallJreDlg());
	getInstallJreDlg().show();

	if (! msgDlgOk)
		return 0;
		
	if (getNoInstallCB().getState()) {
		cfg.setProperty(plat+"JREPath",getJreTF().getText());

		if (plat.indexOf("WIN") != -1) {
			if (! getJreTF().getText().endsWith("java.exe") &&
				! getJreTF().getText().endsWith("jre.exe")) {
				cfg.setProperty("WINUSECLASSPATH","YES");
				getPersJreTA().setText("The JRE you selected has been validated, however, the JRE is not");
				getPersJreTA().append(" one which is familiar. As a result, the CLASSPATH environment");
				getPersJreTA().append(" will be set when the JRE is invoked. The WINUSECLASSPATH=YES statement in");
				getPersJreTA().append(" edesign.ini controls this behaviour. If the application does not");
				getPersJreTA().append(" start when you dismiss this panel, run your JRE from a DOS window");
				getPersJreTA().append(" to determine which option is used to set the class path. Edit");
				getPersJreTA().append(" edesign.ini, remove the WINUSECLASSPATH statement and add the");
				getPersJreTA().append(" statement WINJREPARMS=-mx32M -cp where -cp is substituted for the");
				getPersJreTA().append(" appropriate option.");
				setLocation2(getPersJreDlg());
				getPersJreDlg().setVisible(true);
			}
		}
		else {
			if (! getJreTF().getText().endsWith("java") &&
				! getJreTF().getText().endsWith("jre")) {
				cfg.setProperty(plat+"USECLASSPATH","YES");
				getPersJreTA().setText("The JRE you selected has been validated, however, the JRE is not");
				getPersJreTA().append(" one which is familiar. As a result, the CLASSPATH environment");
				getPersJreTA().append(" will be set when the JRE is invoked. The " + plat + "USECLASSPATH=YES statement in");
				getPersJreTA().append(" edesign.ini controls this behaviour. If the application does not");
				getPersJreTA().append(" start when you dismiss this panel, run your JRE from a shell window");
				getPersJreTA().append(" to determine which option is used to set the class path. Edit");
				getPersJreTA().append(" edesign.ini, remove the " + plat +"USECLASSPATH statement and add the");
				getPersJreTA().append(" statement " + plat + "JREPARMS=-mx32M -cp where -cp is substituted for the");
				getPersJreTA().append(" appropriate option.");
				setLocation2(getPersJreDlg());
				getPersJreDlg().setVisible(true);
			}
		}

		return 1;
	}

	return 2;
}
/**
 * Insert the method's description here.
 * Creation date: (01/30/01 9:09:07 AM)
 * @param msg java.lang.String
 */
private void debug(String msg) {
	//if (debug) System.out.println(msg);
	System.out.println(msg);
}
/**
 * Insert the method's description here.
 * Creation date: (9/6/2001 11:07:15 AM)
 */
public void deniedAccess() {
	getStatusTA().append("\n\nYou have denied this applet's access to your workstation.");
	getStatusTA().append(" If this was in error, please reload this page to try again.");
	getStatusTA().append(" Otherwise, contact support for assistance.");
	//getStatusTA().append(" To install and launch CSDS without the assistance of this");
	//getStatusTA().append(" applet, follow the instructions below.");
	getStatusLbl().setText("Processing stopped, access is denied.");
}
/**
 * Insert the method's description here.
 * Creation date: (12/2/2003 4:06:56 PM)
 */
public void doCanDelete() throws Exception {
	getStatusLbl().setText("Cleaning up old JRE files...");
	getStatusTA().append("\n\n--- Cleaning up old installation files (" + getDate() + ")");

	// Get the CanDelete section.
	File cleanup = new File(ini.getParent(),"cleanup.ini");
	ConfigFile cleanupCfg = new ConfigFile();
	Vector deleteVector = new Vector();

	if (cleanup.exists()) {
		if (cleanup.canRead()) {
			cleanupCfg.load(cleanup.getPath());
			deleteVector = cleanupCfg.getSection("CanDelete");
		}
		else {
			debug("No permission to read JRE Cleanup information file");
			debug("File is " + cleanup.toString());
		}
	}

	// Get the proper location for the JRE.
	String jreFolderName = null;
	String jrePath = null;
	if (plat.equalsIgnoreCase("WIN")) {
		jreFolderName= "WINJRE";
		jrePath = "WINJREPATH";
	}
	else if (plat.equalsIgnoreCase("AIX")){
		jreFolderName= "AIXJRE";
		jrePath = "AIXJREPATH";
	}
	else {
		jreFolderName= "LINJRE";
		jrePath = "LINJREPATH";
	}

	// Get the install directory
	String installDir = instcfg.getProperty("InstallPath");

	// Something to try to delete?
	if (deleteVector.size() > 0) {
		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalFileWrite");
				PolicyEngine.assertPermission(PermissionID.USERFILEIO);
			}
			catch (Exception e1) {
				debug(e1.getMessage());
				deniedAccess();
				throw new Exception("\n\nInstallation stopped.");
			}
		}

		// Should be only 1 section, but we'll watch for more.
		int i = 0;
		while (i < deleteVector.size()) {
			// Get the next section.
			ConfigSection delete = (ConfigSection) deleteVector.elementAt(i);

			// Every property name in this section is a directory to be
			// deleted in the installation directory.
			Enumeration dirs = delete.getPropertyNames();
			while (dirs.hasMoreElements()) {
				// Get a directory to remove
				String dir = (String) dirs.nextElement();
				dir = delete.getProperty(dir);

				// Only attempt to clean up dirs for this platform
				if (dir.startsWith(jreFolderName)) {
					// Remove the directory
					getStatusTA().append("\nRemoving old JRE directory: " + dir);
					File DIR = new File(installDir,dir);
					removeFile(DIR);

					// Succeeded in removing the directory?
					if (! DIR.exists()) {
						delete.removeProperty(dir);
					}
				}
			}

			// If all the directories were removed, then this section
			// is no longer needed.
			if (! delete.getPropertyNames().hasMoreElements())
				deleteVector.removeElementAt(i);
			// Otherwise, we keep it and move to the next one.
			else
				i++;
		}

		// If all the directories got removed, we can remove the entire
		// cleanup.ini file. Otherwise, we need to save the ini file as there are
		// still directories to delete.
		if (deleteVector.size() == 0) {
			cleanup.delete();
		}
		else {
			cleanupCfg.store(cleanup.getPath());
		}
	}

	// Do we need to try to relocate the JRE to the proper place?
	String jre = cfg.getProperty(jrePath,null);
	int fsPos = (jre == null) ? -1 : jre.indexOf(File.separatorChar);

	// Only if it is not in its primary location
	if (jre != null && jre.startsWith(jreFolderName) && fsPos != jreFolderName.length()) {
		String dir = jre.substring(0,fsPos);

		getStatusTA().append("\nMoving JRE from " + dir + " to " + jreFolderName);

		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalFileWrite");
				PolicyEngine.assertPermission(PermissionID.USERFILEIO);
			}
			catch (Exception e1) {
				debug(e1.getMessage());
				deniedAccess();
				throw new Exception("\n\nInstallation stopped.");
			}
		}

		// Files to where the JRE is and should be.
		File jreDir = new File(installDir,jreFolderName);
		File curJreDir = new File(installDir,jre.substring(0,fsPos));

		// If proper location doesn't exist and current does...
		if (! jreDir.exists()) {
			if (curJreDir.exists()) {
				// Try to rename it.
				if (curJreDir.renameTo(jreDir)) {
					// Renamed, Update the property in the ini file.
					getStatusTA().append("\nMove complete.");
					cfg.setProperty(jrePath,jreFolderName + jre.substring(fsPos));

					try {
						getStatusTA().append("\nConfiguration 1 of 1: ");
						getStatusTA().append(ini.getName());
						getStatusTA().append(" file");
						cfg.store(ini.toString());
						getStatusTA().append(" ...saved.");
					}
					catch (Exception e1) {
						getStatusTA().append(" ...NOT saved.");
						debug(e1.getMessage());
						throw new Exception("\n\nInstallation stopped.");
					}
				}
				else {
					getStatusTA().append("\nMove failed: unable to rename directory.");
				}
			}
			else {
				getStatusTA().append("\nMove failed: " + dir + " does not exist.");
			}
		}
		else {
			getStatusTA().append("\nMove failed: " + jreFolderName + " already exists.");
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (01/24/01 1:44:23 PM)
 */
public void doInstall() {
	getStatusLbl().setText("Prepare for client software install...");
	getStatusTA().append("\n\n--- Installing (" + getDate() + ")");

	String installDir = null;
	File dir = null;
	int jreChoice = 0;

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalFileAccess");
			PolicyEngine.assertPermission(PermissionID.USERFILEIO);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return;
		}
	}

	try {
		// Obtain the installation point.
		getStatusTA().append("\nGetting the directory in which software will be installed");

		boolean done = false;
		getInstallDirTF().setText(instini.getParent() + File.separator + "dsc");
		while (! done) {
			getInstallOkBtn().requestFocus();
			setLocation2(getSaveDlg());
			getSaveDlg().show();

			installDir = getInstallDirTF().getText();
			if (msgDlgOk && installDir != null && installDir.trim().length() != 0) {
				dir = new File(installDir);
				if (! dir.exists()) {
					getMsgDlg().setTitle("Create directory");
					getMsgLbl1().setText(dir.getPath());
					getMsgLbl2().setText("Directory does not exist, Ok to create it?");
					getMsgOkBtn().requestFocus();
					setLocation2(getMsgDlg());
					getMsgDlg().show();

					if (msgDlgOk) {
						if (! dir.mkdirs()) {
							getMsgDlg().setTitle("Create directory failed");
							getMsgLbl1().setText("The installation directory could not be created.");
							getMsgLbl2().setText("Check the path and try again.");
							getMsgCanBtn().setVisible(false);
							getMsgOkBtn().requestFocus();
							setLocation2(getMsgDlg());
							getMsgDlg().show();
							getMsgCanBtn().setVisible(true);
						}
						else {
							done = true;
							instcfg.setProperty("InstallPath",dir.getPath());
							getStatusTA().append(" ...done");
						}
					}
				}
				else if (! dir.isDirectory()) {
					getMsgDlg().setTitle("Path not directory!");
					getMsgLbl1().setText("The installation path specified is not a directory.");
					getMsgLbl2().setText("Check the path and try again.");
					getMsgCanBtn().setVisible(false);
					getMsgOkBtn().requestFocus();
					setLocation2(getMsgDlg());
					getMsgDlg().show();
					getMsgCanBtn().setVisible(true);
				}
				else {
					getStatusTA().append(" ...done");
					instcfg.setProperty("InstallPath",dir.getPath());

					// Directory selected already exists.
					getStatusTA().append("\nValidating installation");
					if (validateInstall(installDir)) {
						// Update the ini file with the DSC location.
						getStatusTA().append(" ...valid.\nFound software in " + installDir);
						ini = new File(dir,"edesign.ini");

						try {
							// Update the installation.
							doCanDelete();
							boolean updated = updateInstall();

							try {
								if (updated) {
									// Need to add the following properties:
									// They instruct the startds program on which class to
									// invoke and what the classpath should be. There is no
									// ICA class/classpath, it is handled internally by the
									// startds program. There is no hosting class/classpath,
									// hosting uses tunnel/ica commands to start.
									cfg.setProperty("NEWODCCLASS","oem.edge.ed.odc.meeting.client.MeetingViewer");
									cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE + ";" + DSC_FILE);
									cfg.setProperty("TUNNELCLASS","oem.edge.ed.odc.applet.LaunchApp");
									cfg.setProperty("TUNNELCLASSPATH",SOD_FILE + ";" + DSC_FILE);
									cfg.setProperty("IMCLASS","oem.edge.ed.odc.applet.SametimeClient");
									cfg.setProperty("IMCLASSPATH",IM1_FILE + ";" + IM2_FILE + ";" + DSC_FILE);
									cfg.setProperty("SDCLASS","oem.edge.ed.sd.SDHostingApp1");
									cfg.setProperty("SDCLASSPATH",XML_FILE + ";" + DSC_FILE);
									cfg.setProperty("DROPCMDLINECLASSPATH",DSC_FILE);
									cfg.setProperty("DROPCMDLINECLASS","oem.edge.ed.odc.dropbox.client.DropboxCmdline");
									cfg.setProperty("XFRCLASSPATH",DSC_FILE);
									cfg.setProperty("XFRCLASS","oem.edge.ed.odc.dropbox.client.DropBox");
									cfg.setProperty("ICA1CLASSPATH",ICA3_FILE);
									cfg.setProperty("ICA1CLASS","com.citrix.JICA");
									cfg.setProperty("ICA2CLASSPATH",ICA1_FILE + ";" + ICA2_FILE);
									cfg.setProperty("ICA2CLASS","com.citrix.JICA");
									cfg.setIntProperty("INIVERSION",INIVERSION);
	
									getStatusTA().append("\n\n--- Configuring (" + getDate() + ")");

									getStatusTA().append("\nConfiguration 1 of 2: ");
									getStatusTA().append(ini.getName());
									getStatusTA().append(" file");
									cfg.store(ini.toString());
									getStatusTA().append(" ...saved.");
									getStatusTA().append("\nConfiguration 2 of 2: ");
								}
								else
									getStatusTA().append("\nConfiguration 1 of 1: ");

								getStatusTA().append(instini.getName());
								getStatusTA().append(" file");
								instcfg.store(instini.toString());
								getStatusTA().append(" ...saved.");
							}
							catch (Exception e) {
								debug(e.getMessage());
								getStatusTA().append(" ...NOT saved.\nConfiguration has failed.");
								getStatusLbl().setText("Installation stopped, view console.");
								return;
							}

							launchApp();

							return;
						}
						catch (Exception e) {
							getStatusTA().append(e.getMessage());
							if (! e.getMessage().endsWith("Installation Stopped."))
								getStatusTA().append("\n\nUpdate failed.\nAsking for permission to overwrite directory.");
							else {
								debug("Exception in updateInstall(): " + e.getMessage());
								e.printStackTrace();
								getStatusLbl().setText("Installation stopped, view console.");
								return;
							}
						}
					}
					else
						getStatusTA().append(" ...not valid.\nAsking for permission to overwrite directory.");

					getMsgDlg().setTitle("Directory exists");
					getMsgLbl1().setText(dir.getPath());
					getMsgLbl2().setText("Directory already exists, Ok to overwrite?");
					getMsgOkBtn().requestFocus();
					setLocation2(getMsgDlg());
					getMsgDlg().show();

					done = msgDlgOk;
				}
			}
			else {
				installDir = null;
				done = true;
				getStatusTA().append(" ...cancelled.");
			}
		}

		if (installDir == null) {
			getStatusTA().append("\nInstallation stopped.");
			getStatusLbl().setText("Installation stopped, view console.");
			return;
		}

		getProgressBar().setCompletion(35);

		// Need socks info.
		ConfigMgr mgr = new ConfigMgr(cfg);
		
		if (! mgr.changeConnectivityWithResponse(this)) {
			getStatusTA().append("\nUser cancelled, installation stopped.");
			getStatusLbl().setText("Installation stopped, view console.");
			return;
		}

		getProgressBar().setCompletion(50);

		jreChoice = chooseJRE(installDir,false);

		if (jreChoice == 0) {
			getStatusTA().append("\nUser cancelled, installation stopped.");
			getStatusLbl().setText("Installation stopped, view console.");
			return;
		}
	}
	catch (Exception e) {
		getStatusTA().append("\n" + e.getMessage());
		getStatusTA().append("\nInstallation stopped.");
		getStatusLbl().setText("Installation stopped, view console.");
		return;
	}

	getProgressBar().setCompletion(65);

	// Obtain license permissions...

	// License the client
	if (! licenseDSC()) {
		getStatusTA().append("\n\nInstallation stopped.");
		getStatusLbl().setText("Installation stopped, view console.");
		return;
	}
	
	getProgressBar().setCompletion(90);

	// License the ICA client
	if (isDSH | isODC | isEDU) {
		if (! licenseICA()) {
			getStatusTA().append("\n\nInstallation stopped.");
			getStatusLbl().setText("Installation stopped, view console.");
			return;
		}
	}
	
	getProgressBar().setCompletion(100);

	try {
		// Ready to begin install into path...
		getStatusLbl().setText("Installing software...");
		getProgressBar().setCompletion(0);

		getStatusTA().append("\nInstalling software in " + dir.getPath());
		ini = new File(dir,"edesign.ini");

		// Count the number of components to be installed.
		int i = 1;
		int j = 2; // Tunnel & launch code.
		if (isDBOX && plat.equals("WIN")) j += 2; // dropboxftp and cmdline clients
		if (isIM) j++;
		if (isSOD) j++;
		if (isSD) j++;
		if (isNEWODC) {
			j++; // for base DSMP.jar...
			// for native scraper lib files...
			if (plat.equals("WIN") ||
				plat.equals("AIX") ||
				plat.equals("SUNSP") ||
				plat.equals("LIN") ||
				plat.equals("HPUX")) {
				j++;
			}
			if (plat.equals("WIN")) j++; // cmdline client.
		}
		if (isDSH | isODC | isEDU) j++;
		if (jreChoice == 2) j++;

		// Install the Client
		installComponent(false,i++,j,"Client software for customer connect",DSC_FILE,"DSCVER",getVersionStamp("DSCVER"));

		// Install the Launch Code
		getProgressBar().setCompletion(0);
		installLaunch(plat.equals("WIN"),false,i++,j);

		// Install dropbox client software.
		if (isDBOX) {
			// On Windows?
			if (plat.equals("WIN")) {
				// Install dropboxftp client.
				getProgressBar().setCompletion(0);
				installComponent(false,i++,j,"Client software for customer connect - dropboxftp (co-req)",SCRIPT_FILE,"SCRIPTVER",getVersionStamp("SCRIPTVER"));
				// Install cmdline client.
				getProgressBar().setCompletion(0);
				installComponent(false,i++,j,"Client software for customer connect - cmdline (co-req)",LDSSCRIPT_FILE,"LDSVER",getVersionStamp("LDSVER"));
			}

			// Set the dropbox URL for cmdline.
			cfg.setProperty("DBOXURL",url);
		}

		// Install the SOD Client
		if (isSOD) {
			getProgressBar().setCompletion(0);
			installComponent(false,i++,j,"Client software for customer connect - on-Demand (co-req)",SOD_FILE,"SODVER",getVersionStamp("SODVER"));
		}
		
		// Install the IM Client
		if (isIM) {
			String[] im = new String[2];
			im[0] = IM1_FILE;
			im[1] = IM2_FILE;

			getProgressBar().setCompletion(0);
			installComponent(false,i++,j,"Client software for customer connect - IM (co-req)",im,"IMVER",getVersionStamp("IMVER"));
		}
		
		// Install the SD Client
		if (isSD) {
			getProgressBar().setCompletion(0);
			installComponent(false,i++,j,"Client software for customer connect - SD (co-req)",XML_FILE,"XMLVER",getVersionStamp("XMLVER"));
		}
		
		// Install the web conference software.
		if (isNEWODC) {
			// Install the base software.
			getProgressBar().setCompletion(0);
			installComponent(false,i++,j,"Client software for customer connect - DSMP (co-req)",DSMP_FILE,"DSMPVER",getVersionStamp("DSMPVER"));

			// Install the scraper.
			String libName = null;
			String[] lib = null;
			String libVersion = null;
			String platLibVersion = null;

			if (plat.equals("WIN")) {
				lib = new String[2];
				lib[0] = WIN1_LIB;
				lib[1] = WIN2_LIB;
				libVersion = getVersionStamp("LIBWINVER");
				platLibVersion = "LIBWINVER";
			}
			else if (plat.equals("AIX")) {
				libName = AIX_LIB;
				libVersion = getVersionStamp("LIBAIXVER");
				platLibVersion = "LIBAIXVER";
			}
			else if (plat.equals("SUNSP")) {
				lib = new String[2];
				lib[0] = SUN1_LIB;
				lib[1] = SUN2_LIB;
				libVersion = getVersionStamp("LIBSUNSPVER");
				platLibVersion = "LIBSUNSPVER";
			}
			else if (plat.equals("LIN")) {
				libName = LIN_LIB;
				libVersion = getVersionStamp("LIBLINVER");
				platLibVersion = "LIBLINVER";
			}
			else if (plat.equals("HPUX")) {
				lib = new String[2];
				lib[0] = HPUX1_LIB;
				lib[1] = HPUX2_LIB;
				libVersion = getVersionStamp("LIBHPUXVER");
				platLibVersion = "LIBHPUXVER";
			}

			if (libName != null) {
				getProgressBar().setCompletion(0);
				installLib(false,i++,j,"Client software for customer connect - Scraper (co-req)",libName,platLibVersion,libVersion);
			}
			else if (lib != null) {
				getProgressBar().setCompletion(0);
				installLib(false,i++,j,"Client software for customer connect - Scraper (co-req)",lib,platLibVersion,libVersion);
			}

			// On Windows? Install cmdline client.
			if (plat.equals("WIN")) {
				getProgressBar().setCompletion(0);
				installComponent(false,i++,j,"Client software for customer connect - cmdline (co-req)",LDSSCRIPT_FILE,"LDSVER",getVersionStamp("LDSVER"));
			}

			// Set the web conference URL for cmdline.
			cfg.setProperty("CONFURL",url);
		}
		
		// Install the ICA Client
		if (isDSH | isODC | isEDU) {
			String[] ica = new String[3];
			ica[0] = ICA1_FILE;
			ica[1] = ICA2_FILE;
			ica[2] = ICA3_FILE;

			getProgressBar().setCompletion(0);
			installComponent(false,i++,j,"ICA client (co-req)",ica,"ICAVER",getVersionStamp("ICAVER"));
		}

		// Install the JRE
		if (jreChoice == 2) {
			getProgressBar().setCompletion(0);
			installJRE(false,i,j);
		}

		getStatusTA().append("\n\nInstallation complete.");
	}
	catch (Exception e) {
		getStatusTA().append("\n\n" + e.getMessage());
		e.printStackTrace();
		getStatusTA().append("\nInstallation stopped.");
		//getInstallBtn().setEnabled(true);
		getStatusLbl().setText("Installation stopped, view console.");
		return;
	}

	// Update the ini file with the DSC location.
	try {
		// Need to add the following properties:
		// They instruct the startds program on which class to
		// invoke and what the classpath should be. There is no
		// ICA class/classpath, it is handled internally by the
		// startds program. There is no hosting class/classpath,
		// hosting uses tunnel/ica commands to start.
		cfg.setProperty("NEWODCCLASS","oem.edge.ed.odc.meeting.client.MeetingViewer");
		cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE + ";" + DSC_FILE);
		cfg.setProperty("TUNNELCLASS","oem.edge.ed.odc.applet.LaunchApp");
		cfg.setProperty("TUNNELCLASSPATH",SOD_FILE + ";" + DSC_FILE);
		cfg.setProperty("IMCLASS","oem.edge.ed.odc.applet.SametimeClient");
		cfg.setProperty("IMCLASSPATH",IM1_FILE + ";" + IM2_FILE + ";" + DSC_FILE);
		cfg.setProperty("SDCLASS","oem.edge.ed.sd.SDHostingApp1");
		cfg.setProperty("SDCLASSPATH",XML_FILE + ";" + DSC_FILE);
		cfg.setProperty("DROPCMDLINECLASSPATH",DSC_FILE);
		cfg.setProperty("DROPCMDLINECLASS","oem.edge.ed.odc.dropbox.client.DropboxCmdline");
		cfg.setProperty("XFRCLASSPATH",DSC_FILE);
		cfg.setProperty("XFRCLASS","oem.edge.ed.odc.dropbox.client.DropBox");
		cfg.setProperty("ICA1CLASSPATH",ICA3_FILE);
		cfg.setProperty("ICA1CLASS","com.citrix.JICA");
		cfg.setProperty("ICA2CLASSPATH",ICA1_FILE + ";" + ICA2_FILE);
		cfg.setProperty("ICA2CLASS","com.citrix.JICA");
		cfg.setIntProperty("INIVERSION",INIVERSION);

		getStatusLbl().setText("Configuring software...");
		getStatusTA().append("\n\n--- Configuring (" + getDate() + ")");

		if (instcfg != cfg) {
			getStatusTA().append("\nConfiguration 1 of 2: ");
			getStatusTA().append(ini.getName());
			getStatusTA().append(" file");
			cfg.store(ini.toString());
			getStatusTA().append(" ...saved.");
			getStatusTA().append("\nConfiguration 2 of 2: ");
		}
		else
			getStatusTA().append("\nConfiguration 1 of 1: ");
	
		getStatusTA().append(instini.getName());
		getStatusTA().append(" file");
		instcfg.store(instini.toString());
		getStatusTA().append(" ...saved.");
	}
	catch (Exception e) {
		debug(e.getMessage());
		getStatusTA().append(" ...NOT saved.\nConfiguration has failed.");
		//getInstallBtn().setEnabled(true);
		getStatusLbl().setText("Installation stopped, view console.");
		return;
	}

	// Launch the application.
	launchApp();
}
/**
 * Download the specified file to the specified directory.
 */
public void downloadFile(String fileName, File dir, int base, int top) throws Exception {
	debug("downloadFile: processing " + fileName);

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalFileAccess");
			PolicyEngine.assertPermission(PermissionID.USERFILEIO);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			throw e;
		}
	}

	File file = new File(dir,fileName);
	FileOutputStream fileOut = null;
	long fs = 0;

	URLConnection conn = null;
	InputStream in = null;
	long length = 0;
	byte[] header = new byte[2];
	byte[] buffer = new byte[4096];

	// If the file already exists, we'll try to resume.
	if (file.exists() && file.isFile()) {
		debug("downloadFile: file exists, attempting restart");

		FileInputStream fileIn = new FileInputStream(file);

		int crc = 0;
		try {
			ODCrc checker = new ODCrc();
			checker.resetCRC();

			int read = 0;
			while ((read = fileIn.read(buffer,0,buffer.length)) != -1) {
				checker.generateCRC(buffer,0,read);
				fs += read;
			}

			crc = checker.getCRC();
		}
		finally {
			fileIn.close();
		}

		debug("downloadFile: file crc is " + crc);
		debug("downloadFile: file size is " + fs);

		URL url = new URL(appletUrl + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName + "?size=" + fs + "&crc=" + crc);
		conn = url.openConnection();

		debug("downloadFile: URL connected.");

		// setting the request property to post
		conn.setRequestProperty("method","POST");
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);

		debug("downloadFile: post request sent");

		length = fs + conn.getContentLength();

		debug("downloadFile: response content length is " + length);

		in = conn.getInputStream();

		if (in != null)
			debug("downloadFile: got input stream");
		else
			debug("downloadFile: no input stream available");

		int sz = in.read(header);

		if (sz == 2)
			debug("downloadFile: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
		else if (sz == 1)
			debug("downloadFile: response is '" + (char) header[0] + "' or " + header[0]);
		else
			debug("downloadFile: no response available");

		//check the header for success
		if (sz != 2 || (header[0] == 'N' && header[1] == 'O')) {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String error = rdr.readLine();

			debug("downloadFile: error text is '" + error + "'");

			debug("downloadFile: restart failed, retrying with no restart");

			url = new URL(appletUrl + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName);
			conn = url.openConnection();

			debug("downloadFile: URL connected.");

			// setting the request property to post
			conn.setRequestProperty("method","POST");
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);

			debug("downloadFile: post request sent");

			length = fs + conn.getContentLength();

			debug("downloadFile: response content length is " + length);

			in = conn.getInputStream();

			if (in != null)
				debug("downloadFile: got input stream");
			else
				debug("downloadFile: no input stream available");

			sz = in.read(header);

			if (sz == 2)
				debug("downloadFile: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
			else if (sz == 1)
				debug("downloadFile: response is '" + (char) header[0] + "' or " + header[0]);
			else
				debug("downloadFile: no response available");

			// If server didn't respond or sent "NO" as its first 2 bytes of content...
			if (sz != 2 || (header[0] == 'N' && header[1] == 'O')) {
				rdr = new BufferedReader(new InputStreamReader(in));
				error = rdr.readLine();

				debug("downloadFile: error text is '" + error + "'");

				debug("downloadFile: servlet refused to download file");

				throw new Exception("Servlet refused to download file:" + error);
			}

			fs = 0;
			fileOut = new FileOutputStream(file);
		}
		else
			fileOut = new FileOutputStream(file.getPath(),true);
	}

	// File does not exist, start from beginning.
	else {
		debug("downloadFile: file does not exist");

		URL url = new URL(appletUrl + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName);
		conn = url.openConnection();

		debug("downloadFile: URL connected.");

		// setting the request property to post
		conn.setRequestProperty("method","POST");
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);

		debug("downloadFile: post request sent");

		length = fs + conn.getContentLength();

		debug("downloadFile: response content length is " + length);

		in = conn.getInputStream();

		if (in != null)
			debug("downloadFile: got input stream");
		else
			debug("downloadFile: no input stream available");

		int sz = in.read(header);

		if (sz == 2)
			debug("downloadFile: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
		else if (sz == 1)
			debug("downloadFile: response is '" + (char) header[0] + "' or " + header[0]);
		else
			debug("downloadFile: no response available");

		//check the header for success
		if (sz != 2 || (header[0] == 'N' && header[1] == 'O')) {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String error = rdr.readLine();

			debug("downloadFile: error text is '" + error + "'");

			debug("downloadFile: servlet refused to download file");

			throw new Exception("Servlet refused to download file:" + error);
		}

		fileOut = new FileOutputStream(file);
	}

	debug("downloadFile: receiving data");

	int len;
	if (length > 0)
		length -= 2; // eliminate the 2 byte status.

	int delta = top - base;
	if (length > 0)
		getProgressBar().setCompletion(base + ((int) (fs*delta/length)));
	else
		getProgressBar().setCompletion(0);
	while ((len = in.read(buffer,0,4096)) != -1) {
		fileOut.write(buffer,0,len);
		fs += len;
		if (length > 0)
			getProgressBar().setCompletion(base + ((int) (fs*delta/length)));
	}

	getProgressBar().setCompletion(100);
	in.close();
	fileOut.close();

	debug("downloadFile: done with file " + fileName);
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 10:11:10 AM)
 * @return boolean
 * @param filename java.lang.String
 */
public boolean fileExists(String filename) {
	debug("Checking for " + filename);
	File file = new File(filename);
	if (! file.exists() || ! file.canRead()) {
		//getStatusTA().append("\nUnable to find program specified in Customer Information File.");
		debug("File is not found or can not be read.");
		return false;
	}
	else {
		debug("File is found.");
		return true;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/2001 9:32:06 AM)
 * @param rdr java.io.BufferedReader
 */
public void formatLicenseTA(URL url) throws IOException {
	// Connect to the URL.
	URLConnection conn = url.openConnection();

	debug("formatLicenseTA: URL connected.");

	// setting the request property to post
	conn.setRequestProperty("method","POST");
	conn.setDoInput(true);
	conn.setUseCaches(false);
	conn.setDefaultUseCaches(false);

	debug("formatLicenseTA: post request sent");

	int length = conn.getContentLength();

	int i = 0;
	String hField = conn.getHeaderField(i);
	while (hField != null) {
		debug("formatLicenseTA: header field " + conn.getHeaderFieldKey(i) + " is " + hField);
		i++;
		hField = conn.getHeaderField(i);
	}

	debug("formatLicenseTA: response content length is " + length);

	InputStream in = conn.getInputStream();

	if (in != null)
		debug("formatLicenseTA: got input stream");
	else
		debug("formatLicenseTA: no input stream available");

	byte[] header = new byte[2];
	int sz = in.read(header);

	if (sz == 2)
		debug("formatLicenseTA: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
	else if (sz == 1)
		debug("formatLicenseTA: response is '" + (char) header[0] + "' or " + header[0]);
	else
		debug("formatLicenseTA: no response available");

	//check the header for success
	if (sz != 2 || (header[0] == 'N' && header[1] == 'O')) {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
		String error = rdr.readLine();
		debug("formatLicenseTA: error text is '" + error + "'");
		debug("formatLicenseTA: servlet refused to download file");
		throw new IOException();
	}

	// Clear the existing text...
	getLicTA().setText("");

	// When true, duplicate blank lines are ignored.
	boolean wasBlank = true;

	// Read the first line of the text.
	StringBuffer line = new StringBuffer();

	int c = in.read();
	while (c != -1 && c != '\n' && c != '\r') {
		line.append((char) c);
		c = in.read();
	}

	while (line.length() > 0 || c != -1) {
		// Trim leading and trailing blanks.
		String lineString = line.toString().trim();

		// Line is blank?
		if (lineString.length() == 0) {
			// Previous line was not blank?
			if (! wasBlank) {
				getLicTA().append("\n");
				wasBlank = true;
			}
		}

		// Line is not blank.
		else {
			// This line was not blank.
			wasBlank = false;
			getLicTA().append(lineString);
			getLicTA().append("\n");
		}

		// Clear last line.
		line.setLength(0);

		// More to read?
		if (c != -1) {
			// Get 1st char of next line of text.
			int c1 = c;
			c = in.read();

			// If CRLF combo, ignore this character.
			if ((c == '\n' || c == '\r') && c != c1)
				c = in.read();

			while (c != -1 && c != '\n' && c != '\r') {
				line.append((char) c);
				c = in.read();
			}
		}
	}

	in.close();
}
/**
 * Returns information about this applet.
 * @return a string of information about this applet
 */
public String getAppletInfo() {
	return "ODC - InstallAndLaunchApp\n" + 
		"\n" + 
		"Examines user's machine to locate the installed launcher.\n" + 
		"If found, the ODC/DSH application is launched. If not, the user\n" + 
		"is invited to install the ODC/DSH application. If the user chooses\n" + 
		"to not install the ODC/DSH application, the ODC/DSH applet is launched.\n" + 
		"\n" + 
		"Copyright 2001 International Business Machines Corp\n" + 
		"";
}
/**
 * Return the BrowseBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getBrowseBtn() {
	if (ivjBrowseBtn == null) {
		try {
			ivjBrowseBtn = new java.awt.Button();
			ivjBrowseBtn.setName("BrowseBtn");
			ivjBrowseBtn.setLabel("Browse");
			ivjBrowseBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						locateJre();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBrowseBtn;
}
/**
 * Return the Console property value.
 * @return java.awt.Frame
 */
private java.awt.Frame getConsole() {
	if (ivjConsole == null) {
		try {
			ivjConsole = new java.awt.Frame();
			ivjConsole.setName("Console");
			ivjConsole.setLayout(new java.awt.BorderLayout());
			ivjConsole.setBounds(36, 181, 520, 283);
			ivjConsole.setTitle("Details");
			getConsole().add(getContentsPane5(), "Center");
			ivjConsole.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowClosing(java.awt.event.WindowEvent e) {    
					try {
						getConsole().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConsole;
}
/**
 * Return the ConsoleOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getConsoleOkBtn() {
	if (ivjConsoleOkBtn == null) {
		try {
			ivjConsoleOkBtn = new java.awt.Button();
			ivjConsoleOkBtn.setName("ConsoleOkBtn");
			ivjConsoleOkBtn.setBackground(java.awt.SystemColor.control);
			ivjConsoleOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjConsoleOkBtn.setLabel("Ok");
			ivjConsoleOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getConsole().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjConsoleOkBtn;
}
/**
 * Return the ContentsPane property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane() {
	if (ivjContentsPane == null) {
		try {
			ivjContentsPane = new java.awt.Panel();
			ivjContentsPane.setName("ContentsPane");
			ivjContentsPane.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsInstallDirLbl = new java.awt.GridBagConstraints();
			constraintsInstallDirLbl.gridx = 0; constraintsInstallDirLbl.gridy = 0;
			constraintsInstallDirLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInstallDirLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInstallDirLbl.insets = new java.awt.Insets(10, 10, 3, 10);
			getContentsPane().add(getInstallDirLbl(), constraintsInstallDirLbl);

			java.awt.GridBagConstraints constraintsInstallDirTF = new java.awt.GridBagConstraints();
			constraintsInstallDirTF.gridx = 0; constraintsInstallDirTF.gridy = 1;
			constraintsInstallDirTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInstallDirTF.weightx = 1.0;
			constraintsInstallDirTF.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane().add(getInstallDirTF(), constraintsInstallDirTF);

			java.awt.GridBagConstraints constraintsPanel3 = new java.awt.GridBagConstraints();
			constraintsPanel3.gridx = 0; constraintsPanel3.gridy = 2;
			constraintsPanel3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel3.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPanel3.weightx = 1.0;
			constraintsPanel3.weighty = 1.0;
			constraintsPanel3.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane().add(getPanel3(), constraintsPanel3);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane;
}
/**
 * Return the ContentsPane1 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane1() {
	if (ivjContentsPane1 == null) {
		try {
			ivjContentsPane1 = new java.awt.Panel();
			ivjContentsPane1.setName("ContentsPane1");
			ivjContentsPane1.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane1.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsMsgLbl1 = new java.awt.GridBagConstraints();
			constraintsMsgLbl1.gridx = 0; constraintsMsgLbl1.gridy = 0;
			constraintsMsgLbl1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMsgLbl1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMsgLbl1.insets = new java.awt.Insets(10, 10, 3, 10);
			getContentsPane1().add(getMsgLbl1(), constraintsMsgLbl1);

			java.awt.GridBagConstraints constraintsMsgLbl2 = new java.awt.GridBagConstraints();
			constraintsMsgLbl2.gridx = 0; constraintsMsgLbl2.gridy = 1;
			constraintsMsgLbl2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMsgLbl2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMsgLbl2.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane1().add(getMsgLbl2(), constraintsMsgLbl2);

			java.awt.GridBagConstraints constraintsPanel1 = new java.awt.GridBagConstraints();
			constraintsPanel1.gridx = 0; constraintsPanel1.gridy = 2;
			constraintsPanel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel1.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPanel1.weightx = 1.0;
			constraintsPanel1.weighty = 1.0;
			constraintsPanel1.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane1().add(getPanel1(), constraintsPanel1);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane1;
}
/**
 * Return the ContentsPane2 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane2() {
	if (ivjContentsPane2 == null) {
		try {
			ivjContentsPane2 = new java.awt.Panel();
			ivjContentsPane2.setName("ContentsPane2");
			ivjContentsPane2.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane2.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsPersJreOkBtn = new java.awt.GridBagConstraints();
			constraintsPersJreOkBtn.gridx = 0; constraintsPersJreOkBtn.gridy = 1;
			constraintsPersJreOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane2().add(getPersJreOkBtn(), constraintsPersJreOkBtn);

			java.awt.GridBagConstraints constraintsPersJreTA = new java.awt.GridBagConstraints();
			constraintsPersJreTA.gridx = 0; constraintsPersJreTA.gridy = 0;
			constraintsPersJreTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPersJreTA.weightx = 1.0;
			constraintsPersJreTA.weighty = 1.0;
			constraintsPersJreTA.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane2().add(getPersJreTA(), constraintsPersJreTA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane2;
}
/**
 * Return the ContentsPane21 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane21() {
	if (ivjContentsPane21 == null) {
		try {
			ivjContentsPane21 = new java.awt.Panel();
			ivjContentsPane21.setName("ContentsPane21");
			ivjContentsPane21.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane21.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsJreUpgradeOkBtn = new java.awt.GridBagConstraints();
			constraintsJreUpgradeOkBtn.gridx = 0; constraintsJreUpgradeOkBtn.gridy = 2;
			constraintsJreUpgradeOkBtn.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJreUpgradeOkBtn.weightx = 1.0;
			constraintsJreUpgradeOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane21().add(getJreUpgradeOkBtn(), constraintsJreUpgradeOkBtn);

			java.awt.GridBagConstraints constraintsJreUpgradeTA = new java.awt.GridBagConstraints();
			constraintsJreUpgradeTA.gridx = 0; constraintsJreUpgradeTA.gridy = 0;
			constraintsJreUpgradeTA.gridwidth = 0;
			constraintsJreUpgradeTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJreUpgradeTA.weightx = 1.0;
			constraintsJreUpgradeTA.weighty = 1.0;
			constraintsJreUpgradeTA.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane21().add(getJreUpgradeTA(), constraintsJreUpgradeTA);

			java.awt.GridBagConstraints constraintsJreUpgradeCanBtn = new java.awt.GridBagConstraints();
			constraintsJreUpgradeCanBtn.gridx = 1; constraintsJreUpgradeCanBtn.gridy = 2;
			constraintsJreUpgradeCanBtn.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJreUpgradeCanBtn.weightx = 1.0;
			constraintsJreUpgradeCanBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane21().add(getJreUpgradeCanBtn(), constraintsJreUpgradeCanBtn);

			java.awt.GridBagConstraints constraintsJreUpgradeCB = new java.awt.GridBagConstraints();
			constraintsJreUpgradeCB.gridx = 0; constraintsJreUpgradeCB.gridy = 1;
			constraintsJreUpgradeCB.gridwidth = 0;
			constraintsJreUpgradeCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJreUpgradeCB.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane21().add(getJreUpgradeCB(), constraintsJreUpgradeCB);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane21;
}
/**
 * Return the ContentsPane3 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane3() {
	if (ivjContentsPane3 == null) {
		try {
			ivjContentsPane3 = new java.awt.Panel();
			ivjContentsPane3.setName("ContentsPane3");
			ivjContentsPane3.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane3.setBackground(java.awt.SystemColor.window);
			ivjContentsPane3.setForeground(java.awt.SystemColor.windowText);

			java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
			constraintsPanel.gridx = 0; constraintsPanel.gridy = 3;
			constraintsPanel.gridwidth = 0;
			constraintsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPanel.weightx = 1.0;
			constraintsPanel.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane3().add(getPanel(), constraintsPanel);

			java.awt.GridBagConstraints constraintsLicLbl = new java.awt.GridBagConstraints();
			constraintsLicLbl.gridx = 0; constraintsLicLbl.gridy = 1;
			constraintsLicLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLicLbl.insets = new java.awt.Insets(5, 10, 0, 10);
			getContentsPane3().add(getLicLbl(), constraintsLicLbl);

			java.awt.GridBagConstraints constraintsLicTA = new java.awt.GridBagConstraints();
			constraintsLicTA.gridx = 0; constraintsLicTA.gridy = 2;
			constraintsLicTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLicTA.weightx = 1.0;
			constraintsLicTA.weighty = 1.0;
			constraintsLicTA.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane3().add(getLicTA(), constraintsLicTA);

			java.awt.GridBagConstraints constraintsLicTitleLbl = new java.awt.GridBagConstraints();
			constraintsLicTitleLbl.gridx = 0; constraintsLicTitleLbl.gridy = 0;
			constraintsLicTitleLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLicTitleLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane3().add(getLicTitleLbl(), constraintsLicTitleLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane3;
}
/**
 * Return the ContentsPane4 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane4() {
	if (ivjContentsPane4 == null) {
		try {
			ivjContentsPane4 = new java.awt.Panel();
			ivjContentsPane4.setName("ContentsPane4");
			ivjContentsPane4.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsSearchLbl = new java.awt.GridBagConstraints();
			constraintsSearchLbl.gridx = 0; constraintsSearchLbl.gridy = 0;
			constraintsSearchLbl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsSearchLbl.weightx = 1.0;
			constraintsSearchLbl.weighty = 1.0;
			constraintsSearchLbl.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane4().add(getSearchLbl(), constraintsSearchLbl);

			java.awt.GridBagConstraints constraintsSearchCanBtn = new java.awt.GridBagConstraints();
			constraintsSearchCanBtn.gridx = 0; constraintsSearchCanBtn.gridy = 1;
			constraintsSearchCanBtn.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane4().add(getSearchCanBtn(), constraintsSearchCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane4;
}
/**
 * Return the ContentsPane5 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane5() {
	if (ivjContentsPane5 == null) {
		try {
			ivjContentsPane5 = new java.awt.Panel();
			ivjContentsPane5.setName("ContentsPane5");
			ivjContentsPane5.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane5.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsConsoleOkBtn = new java.awt.GridBagConstraints();
			constraintsConsoleOkBtn.gridx = 0; constraintsConsoleOkBtn.gridy = 1;
			constraintsConsoleOkBtn.insets = new java.awt.Insets(0, 10, 10, 10);
			getContentsPane5().add(getConsoleOkBtn(), constraintsConsoleOkBtn);

			java.awt.GridBagConstraints constraintsStatusTA = new java.awt.GridBagConstraints();
			constraintsStatusTA.gridx = 0; constraintsStatusTA.gridy = 0;
			constraintsStatusTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStatusTA.weightx = 1.0;
			constraintsStatusTA.weighty = 1.0;
			constraintsStatusTA.insets = new java.awt.Insets(10, 10, 5, 10);
			getContentsPane5().add(getStatusTA(), constraintsStatusTA);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane5;
}
/**
 * Return the ContentsPane6 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getContentsPane6() {
	if (ivjContentsPane6 == null) {
		try {
			ivjContentsPane6 = new java.awt.Panel();
			ivjContentsPane6.setName("ContentsPane6");
			ivjContentsPane6.setLayout(new java.awt.GridBagLayout());
			ivjContentsPane6.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsPanel31 = new java.awt.GridBagConstraints();
			constraintsPanel31.gridx = 0; constraintsPanel31.gridy = 7;
			constraintsPanel31.gridwidth = 0;
			constraintsPanel31.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPanel31.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsPanel31.insets = new java.awt.Insets(10, 10, 10, 10);
			getContentsPane6().add(getPanel31(), constraintsPanel31);

			java.awt.GridBagConstraints constraintsInstallCB = new java.awt.GridBagConstraints();
			constraintsInstallCB.gridx = 0; constraintsInstallCB.gridy = 1;
			constraintsInstallCB.gridwidth = 0;
			constraintsInstallCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInstallCB.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane6().add(getInstallCB(), constraintsInstallCB);

			java.awt.GridBagConstraints constraintsNoInstallCB = new java.awt.GridBagConstraints();
			constraintsNoInstallCB.gridx = 0; constraintsNoInstallCB.gridy = 2;
			constraintsNoInstallCB.gridwidth = 0;
			constraintsNoInstallCB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNoInstallCB.insets = new java.awt.Insets(0, 10, 0, 10);
			getContentsPane6().add(getNoInstallCB(), constraintsNoInstallCB);

			java.awt.GridBagConstraints constraintsLabel1 = new java.awt.GridBagConstraints();
			constraintsLabel1.gridx = 0; constraintsLabel1.gridy = 0;
			constraintsLabel1.gridwidth = 0;
			constraintsLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabel1.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane6().add(getLabel1(), constraintsLabel1);

			java.awt.GridBagConstraints constraintsJreLbl = new java.awt.GridBagConstraints();
			constraintsJreLbl.gridx = 0; constraintsJreLbl.gridy = 4;
			constraintsJreLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJreLbl.insets = new java.awt.Insets(0, 20, 0, 0);
			getContentsPane6().add(getJreLbl(), constraintsJreLbl);

			java.awt.GridBagConstraints constraintsJreTF = new java.awt.GridBagConstraints();
			constraintsJreTF.gridx = 1; constraintsJreTF.gridy = 4;
			constraintsJreTF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJreTF.weightx = 1.0;
			getContentsPane6().add(getJreTF(), constraintsJreTF);

			java.awt.GridBagConstraints constraintsBrowseBtn = new java.awt.GridBagConstraints();
			constraintsBrowseBtn.gridx = 2; constraintsBrowseBtn.gridy = 4;
			constraintsBrowseBtn.insets = new java.awt.Insets(0, 5, 0, 10);
			getContentsPane6().add(getBrowseBtn(), constraintsBrowseBtn);

			java.awt.GridBagConstraints constraintsJreTA = new java.awt.GridBagConstraints();
			constraintsJreTA.gridx = 0; constraintsJreTA.gridy = 6;
			constraintsJreTA.gridwidth = 0;
			constraintsJreTA.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJreTA.weightx = 1.0;
			constraintsJreTA.weighty = 1.0;
			constraintsJreTA.insets = new java.awt.Insets(10, 10, 0, 10);
			getContentsPane6().add(getJreTA(), constraintsJreTA);

			java.awt.GridBagConstraints constraintsJreValidLbl = new java.awt.GridBagConstraints();
			constraintsJreValidLbl.gridx = 1; constraintsJreValidLbl.gridy = 5;
			constraintsJreValidLbl.gridwidth = 0;
			constraintsJreValidLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJreValidLbl.insets = new java.awt.Insets(0, 0, 0, 10);
			getContentsPane6().add(getJreValidLbl(), constraintsJreValidLbl);

			java.awt.GridBagConstraints constraintsJreSearchBtn = new java.awt.GridBagConstraints();
			constraintsJreSearchBtn.gridx = 2; constraintsJreSearchBtn.gridy = 3;
			constraintsJreSearchBtn.insets = new java.awt.Insets(0, 5, 3, 10);
			getContentsPane6().add(getJreSearchBtn(), constraintsJreSearchBtn);

			java.awt.GridBagConstraints constraintsJreInfoLbl = new java.awt.GridBagConstraints();
			constraintsJreInfoLbl.gridx = 0; constraintsJreInfoLbl.gridy = 3;
			constraintsJreInfoLbl.gridwidth = 2;
			constraintsJreInfoLbl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJreInfoLbl.insets = new java.awt.Insets(0, 20, 3, 0);
			getContentsPane6().add(getJreInfoLbl(), constraintsJreInfoLbl);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjContentsPane6;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 9:14:27 AM)
 * @return java.lang.String
 */
private String getDate() {
	return formatter.format(new Date());
}
/**
 * Return the Button1 property value.
 * @return java.awt.Button
 */
private java.awt.Button getDetailsBtn() {
	if (ivjDetailsBtn == null) {
		try {
			ivjDetailsBtn = new java.awt.Button();
			ivjDetailsBtn.setName("DetailsBtn");
			ivjDetailsBtn.setBackground(java.awt.SystemColor.control);
			ivjDetailsBtn.setForeground(java.awt.SystemColor.controlText);
			ivjDetailsBtn.setLabel("View details");
			ivjDetailsBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getConsole().show();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDetailsBtn;
}
/**
 * Return the FileDialog property value.
 * @return java.awt.FileDialog
 */
private java.awt.FileDialog getFileDlg() {
	if (ivjFileDlg == null) {
		try {
			ivjFileDlg = new java.awt.FileDialog(new java.awt.Frame());
			ivjFileDlg.setName("FileDlg");
			ivjFileDlg.setLayout(null);
			ivjFileDlg.setTitle("Select JRE Executable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFileDlg;
}
/**
 * Return the CancelBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getInstallCanlBtn() {
	if (ivjInstallCanlBtn == null) {
		try {
			ivjInstallCanlBtn = new java.awt.Button();
			ivjInstallCanlBtn.setName("InstallCanlBtn");
			ivjInstallCanlBtn.setBackground(java.awt.SystemColor.control);
			ivjInstallCanlBtn.setForeground(java.awt.SystemColor.controlText);
			ivjInstallCanlBtn.setLabel("Cancel");
			ivjInstallCanlBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getSaveDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallCanlBtn;
}
/**
 * Return the InstallCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getInstallCB() {
	if (ivjInstallCB == null) {
		try {
			ivjInstallCB = new java.awt.Checkbox();
			ivjInstallCB.setName("InstallCB");
			ivjInstallCB.setLabel("Install the supplied JRE");
			ivjInstallCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						installChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallCB;
}
/**
 * Return the InstallCBG property value.
 * @return java.awt.CheckboxGroup
 */
private java.awt.CheckboxGroup getInstallCBG() {
	if (ivjInstallCBG == null) {
		try {
			ivjInstallCBG = new java.awt.CheckboxGroup();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallCBG;
}
/**
 * Return the InstallDirLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getInstallDirLbl() {
	if (ivjInstallDirLbl == null) {
		try {
			ivjInstallDirLbl = new java.awt.Label();
			ivjInstallDirLbl.setName("InstallDirLbl");
			ivjInstallDirLbl.setText("Enter the directory path where the software is to be installed:");
			ivjInstallDirLbl.setBackground(java.awt.SystemColor.window);
			ivjInstallDirLbl.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallDirLbl;
}
/**
 * Return the InstallDirTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getInstallDirTF() {
	if (ivjInstallDirTF == null) {
		try {
			ivjInstallDirTF = new java.awt.TextField();
			ivjInstallDirTF.setName("InstallDirTF");
			ivjInstallDirTF.setBackground(java.awt.SystemColor.window);
			ivjInstallDirTF.setForeground(java.awt.SystemColor.windowText);
			ivjInstallDirTF.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getSaveDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallDirTF;
}
/**
 * Return the InstallJRECanlBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getInstallJRECanlBtn() {
	if (ivjInstallJRECanlBtn == null) {
		try {
			ivjInstallJRECanlBtn = new java.awt.Button();
			ivjInstallJRECanlBtn.setName("InstallJRECanlBtn");
			ivjInstallJRECanlBtn.setBackground(java.awt.SystemColor.control);
			ivjInstallJRECanlBtn.setForeground(java.awt.SystemColor.controlText);
			ivjInstallJRECanlBtn.setLabel("Cancel");
			ivjInstallJRECanlBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getInstallJreDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallJRECanlBtn;
}
/**
 * Return the InstallJREDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getInstallJreDlg() {
	if (ivjInstallJreDlg == null) {
		try {
			ivjInstallJreDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjInstallJreDlg.setName("InstallJreDlg");
			ivjInstallJreDlg.setResizable(true);
			ivjInstallJreDlg.setLayout(new java.awt.BorderLayout());
			ivjInstallJreDlg.setBounds(36, 1511, 454, 431);
			ivjInstallJreDlg.setModal(true);
			ivjInstallJreDlg.setTitle("Select JRE");
			getInstallJreDlg().add(getContentsPane6(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallJreDlg;
}
/**
 * Return the InstallJREOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getInstallJREOkBtn() {
	if (ivjInstallJREOkBtn == null) {
		try {
			ivjInstallJREOkBtn = new java.awt.Button();
			ivjInstallJREOkBtn.setName("InstallJREOkBtn");
			ivjInstallJREOkBtn.setBackground(java.awt.SystemColor.control);
			ivjInstallJREOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjInstallJREOkBtn.setLabel("Ok");
			ivjInstallJREOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getInstallJreDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallJREOkBtn;
}
/**
 * Return the OkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getInstallOkBtn() {
	if (ivjInstallOkBtn == null) {
		try {
			ivjInstallOkBtn = new java.awt.Button();
			ivjInstallOkBtn.setName("InstallOkBtn");
			ivjInstallOkBtn.setBackground(java.awt.SystemColor.control);
			ivjInstallOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjInstallOkBtn.setLabel("Ok");
			ivjInstallOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getSaveDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjInstallOkBtn;
}
/**
 * Return the Label2 property value.
 * @return java.awt.Label
 */
private java.awt.Label getJreInfoLbl() {
	if (ivjJreInfoLbl == null) {
		try {
			ivjJreInfoLbl = new java.awt.Label();
			ivjJreInfoLbl.setName("JreInfoLbl");
			ivjJreInfoLbl.setText("Enter JRE path, Browse to select, or Search to find:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreInfoLbl;
}
/**
 * Return the JreLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getJreLbl() {
	if (ivjJreLbl == null) {
		try {
			ivjJreLbl = new java.awt.Label();
			ivjJreLbl.setName("JreLbl");
			ivjJreLbl.setText("JRE Path:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreLbl;
}
/**
 * Return the Button2 property value.
 * @return java.awt.Button
 */
private java.awt.Button getJreSearchBtn() {
	if (ivjJreSearchBtn == null) {
		try {
			ivjJreSearchBtn = new java.awt.Button();
			ivjJreSearchBtn.setName("JreSearchBtn");
			ivjJreSearchBtn.setLabel("Search");
			ivjJreSearchBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						startSearch();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreSearchBtn;
}
/**
 * Return the TextArea1 property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getJreTA() {
	if (ivjJreTA == null) {
		try {
			ivjJreTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjJreTA.setName("JreTA");
			ivjJreTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreTA;
}
/**
 * Return the JreTF property value.
 * @return java.awt.TextField
 */
private java.awt.TextField getJreTF() {
	if (ivjJreTF == null) {
		try {
			ivjJreTF = new java.awt.TextField();
			ivjJreTF.setName("JreTF");
			ivjJreTF.addTextListener(new java.awt.event.TextListener() { 
				public void textValueChanged(java.awt.event.TextEvent e) {    
					try {
						jreTextChanged();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreTF;
}
/**
 * Return the JreUpgradeCanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getJreUpgradeCanBtn() {
	if (ivjJreUpgradeCanBtn == null) {
		try {
			ivjJreUpgradeCanBtn = new java.awt.Button();
			ivjJreUpgradeCanBtn.setName("JreUpgradeCanBtn");
			ivjJreUpgradeCanBtn.setLabel("Ignore");
			ivjJreUpgradeCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getJreUpgradeDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreUpgradeCanBtn;
}
/**
 * Return the JreUpgradeCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getJreUpgradeCB() {
	if (ivjJreUpgradeCB == null) {
		try {
			ivjJreUpgradeCB = new java.awt.Checkbox();
			ivjJreUpgradeCB.setName("JreUpgradeCB");
			ivjJreUpgradeCB.setLabel("Remind me again in the future");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreUpgradeCB;
}
/**
 * Return the JreUpgradeDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getJreUpgradeDlg() {
	if (ivjJreUpgradeDlg == null) {
		try {
			ivjJreUpgradeDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjJreUpgradeDlg.setName("JreUpgradeDlg");
			ivjJreUpgradeDlg.setLayout(new java.awt.BorderLayout());
			ivjJreUpgradeDlg.setBounds(481, 2017, 364, 238);
			ivjJreUpgradeDlg.setModal(true);
			ivjJreUpgradeDlg.setTitle("JRE Upgrade Recommended");
			getJreUpgradeDlg().add(getContentsPane21(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreUpgradeDlg;
}
/**
 * Return the JreUpgradeOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getJreUpgradeOkBtn() {
	if (ivjJreUpgradeOkBtn == null) {
		try {
			ivjJreUpgradeOkBtn = new java.awt.Button();
			ivjJreUpgradeOkBtn.setName("JreUpgradeOkBtn");
			ivjJreUpgradeOkBtn.setLabel("Upgrade");
			ivjJreUpgradeOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getJreUpgradeDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreUpgradeOkBtn;
}
/**
 * Return the JreUpgradeTA property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getJreUpgradeTA() {
	if (ivjJreUpgradeTA == null) {
		try {
			ivjJreUpgradeTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjJreUpgradeTA.setName("JreUpgradeTA");
			ivjJreUpgradeTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreUpgradeTA;
}
/**
 * Return the JreValidLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getJreValidLbl() {
	if (ivjJreValidLbl == null) {
		try {
			ivjJreValidLbl = new java.awt.Label();
			ivjJreValidLbl.setName("JreValidLbl");
			ivjJreValidLbl.setText("Enter full path to JRE executable.");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJreValidLbl;
}
/**
 * Return the Label1 property value.
 * @return java.awt.Label
 */
private java.awt.Label getLabel1() {
	if (ivjLabel1 == null) {
		try {
			ivjLabel1 = new java.awt.Label();
			ivjLabel1.setName("Label1");
			ivjLabel1.setText("A Java Runtime Environment (JRE) is required:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLabel1;
}
/**
 * Return the LicCanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getLicCanBtn() {
	if (ivjLicCanBtn == null) {
		try {
			ivjLicCanBtn = new java.awt.Button();
			ivjLicCanBtn.setName("LicCanBtn");
			ivjLicCanBtn.setBackground(java.awt.SystemColor.control);
			ivjLicCanBtn.setForeground(java.awt.SystemColor.controlText);
			ivjLicCanBtn.setLabel("No");
			ivjLicCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
						getLicDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicCanBtn;
}
/**
 * Return the LicenseDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getLicDlg() {
	if (ivjLicDlg == null) {
		try {
			ivjLicDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjLicDlg.setName("LicDlg");
			ivjLicDlg.setLayout(new java.awt.BorderLayout());
			ivjLicDlg.setBackground(java.awt.SystemColor.window);
			ivjLicDlg.setBounds(34, 829, 416, 347);
			ivjLicDlg.setModal(true);
			ivjLicDlg.setTitle("License Agreement");
			getLicDlg().add(getContentsPane3(), "Center");
			ivjLicDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						getLicDlg().toFront();
						getLicTA().setCaretPosition(0);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicDlg;
}
/**
 * Return the LicLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getLicLbl() {
	if (ivjLicLbl == null) {
		try {
			ivjLicLbl = new java.awt.Label();
			ivjLicLbl.setName("LicLbl");
			ivjLicLbl.setText("Will you agree to the following License Agreement:");
			ivjLicLbl.setBackground(java.awt.SystemColor.window);
			ivjLicLbl.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicLbl;
}
/**
 * Return the LicOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getLicOkBtn() {
	if (ivjLicOkBtn == null) {
		try {
			ivjLicOkBtn = new java.awt.Button();
			ivjLicOkBtn.setName("LicOkBtn");
			ivjLicOkBtn.setBackground(java.awt.SystemColor.control);
			ivjLicOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjLicOkBtn.setLabel("Yes");
			ivjLicOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						okCanPushed(e);
						getLicDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicOkBtn;
}
/**
 * Return the LicTA property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getLicTA() {
	if (ivjLicTA == null) {
		try {
			ivjLicTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjLicTA.setName("LicTA");
			ivjLicTA.setBackground(java.awt.SystemColor.window);
			ivjLicTA.setEditable(false);
			ivjLicTA.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicTA;
}
/**
 * Return the Label1 property value.
 * @return java.awt.Label
 */
private java.awt.Label getLicTitleLbl() {
	if (ivjLicTitleLbl == null) {
		try {
			ivjLicTitleLbl = new java.awt.Label();
			ivjLicTitleLbl.setName("LicTitleLbl");
			ivjLicTitleLbl.setText("Product");
			ivjLicTitleLbl.setBackground(java.awt.SystemColor.window);
			ivjLicTitleLbl.setForeground(java.awt.SystemColor.windowText);
			ivjLicTitleLbl.setAlignment(java.awt.Label.CENTER);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLicTitleLbl;
}
/**
 * Return the MsgCanBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getMsgCanBtn() {
	if (ivjMsgCanBtn == null) {
		try {
			ivjMsgCanBtn = new java.awt.Button();
			ivjMsgCanBtn.setName("MsgCanBtn");
			ivjMsgCanBtn.setBackground(java.awt.SystemColor.control);
			ivjMsgCanBtn.setForeground(java.awt.SystemColor.controlText);
			ivjMsgCanBtn.setLabel("Cancel");
			ivjMsgCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
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
	return ivjMsgCanBtn;
}
/**
 * Return the MsgDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getMsgDlg() {
	if (ivjMsgDlg == null) {
		try {
			ivjMsgDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjMsgDlg.setName("MsgDlg");
			ivjMsgDlg.setLayout(new java.awt.BorderLayout());
			ivjMsgDlg.setBounds(35, 1269, 303, 172);
			ivjMsgDlg.setModal(true);
			getMsgDlg().add(getContentsPane1(), "Center");
			ivjMsgDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						getMsgDlg().toFront();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgDlg;
}
/**
 * Return the MsgLbl1 property value.
 * @return java.awt.Label
 */
private java.awt.Label getMsgLbl1() {
	if (ivjMsgLbl1 == null) {
		try {
			ivjMsgLbl1 = new java.awt.Label();
			ivjMsgLbl1.setName("MsgLbl1");
			ivjMsgLbl1.setText("Label1");
			ivjMsgLbl1.setBackground(java.awt.SystemColor.window);
			ivjMsgLbl1.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgLbl1;
}
/**
 * Return the MsgLbl2 property value.
 * @return java.awt.Label
 */
private java.awt.Label getMsgLbl2() {
	if (ivjMsgLbl2 == null) {
		try {
			ivjMsgLbl2 = new java.awt.Label();
			ivjMsgLbl2.setName("MsgLbl2");
			ivjMsgLbl2.setText("Label1");
			ivjMsgLbl2.setBackground(java.awt.SystemColor.window);
			ivjMsgLbl2.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMsgLbl2;
}
/**
 * Return the MsgOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getMsgOkBtn() {
	if (ivjMsgOkBtn == null) {
		try {
			ivjMsgOkBtn = new java.awt.Button();
			ivjMsgOkBtn.setName("MsgOkBtn");
			ivjMsgOkBtn.setBackground(java.awt.SystemColor.control);
			ivjMsgOkBtn.setForeground(java.awt.SystemColor.controlText);
			ivjMsgOkBtn.setLabel("Ok");
			ivjMsgOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						okCanPushed(e);
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
 * Return the NoInstallCB property value.
 * @return java.awt.Checkbox
 */
private java.awt.Checkbox getNoInstallCB() {
	if (ivjNoInstallCB == null) {
		try {
			ivjNoInstallCB = new java.awt.Checkbox();
			ivjNoInstallCB.setName("NoInstallCB");
			ivjNoInstallCB.setLabel("Use the following JRE:");
			ivjNoInstallCB.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					try {
						noInstallChanged(e);
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjNoInstallCB;
}
/**
 * Return the Panel property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel() {
	if (ivjPanel == null) {
		try {
			ivjPanel = new java.awt.Panel();
			ivjPanel.setName("Panel");
			ivjPanel.setLayout(new java.awt.GridBagLayout());
			ivjPanel.setBackground(java.awt.SystemColor.window);
			ivjPanel.setForeground(java.awt.SystemColor.windowText);

			java.awt.GridBagConstraints constraintsLicOkBtn = new java.awt.GridBagConstraints();
			constraintsLicOkBtn.gridx = 0; constraintsLicOkBtn.gridy = 0;
			constraintsLicOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel().add(getLicOkBtn(), constraintsLicOkBtn);

			java.awt.GridBagConstraints constraintsLicCanBtn = new java.awt.GridBagConstraints();
			constraintsLicCanBtn.gridx = 1; constraintsLicCanBtn.gridy = 0;
			constraintsLicCanBtn.weighty = 1.0;
			getPanel().add(getLicCanBtn(), constraintsLicCanBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel;
}
/**
 * Return the Panel1 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel1() {
	if (ivjPanel1 == null) {
		try {
			ivjPanel1 = new java.awt.Panel();
			ivjPanel1.setName("Panel1");
			ivjPanel1.setLayout(new java.awt.GridBagLayout());
			ivjPanel1.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsMsgCanBtn = new java.awt.GridBagConstraints();
			constraintsMsgCanBtn.gridx = 1; constraintsMsgCanBtn.gridy = 0;
			getPanel1().add(getMsgCanBtn(), constraintsMsgCanBtn);

			java.awt.GridBagConstraints constraintsMsgOkBtn = new java.awt.GridBagConstraints();
			constraintsMsgOkBtn.gridx = 0; constraintsMsgOkBtn.gridy = 0;
			constraintsMsgOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel1().add(getMsgOkBtn(), constraintsMsgOkBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel1;
}
/**
 * Return the Panel3 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel3() {
	if (ivjPanel3 == null) {
		try {
			ivjPanel3 = new java.awt.Panel();
			ivjPanel3.setName("Panel3");
			ivjPanel3.setLayout(new java.awt.GridBagLayout());
			ivjPanel3.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsInstallOkBtn = new java.awt.GridBagConstraints();
			constraintsInstallOkBtn.gridx = 0; constraintsInstallOkBtn.gridy = 0;
			constraintsInstallOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel3().add(getInstallOkBtn(), constraintsInstallOkBtn);

			java.awt.GridBagConstraints constraintsInstallCanlBtn = new java.awt.GridBagConstraints();
			constraintsInstallCanlBtn.gridx = 1; constraintsInstallCanlBtn.gridy = 0;
			getPanel3().add(getInstallCanlBtn(), constraintsInstallCanlBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel3;
}
/**
 * Return the Panel31 property value.
 * @return java.awt.Panel
 */
private java.awt.Panel getPanel31() {
	if (ivjPanel31 == null) {
		try {
			ivjPanel31 = new java.awt.Panel();
			ivjPanel31.setName("Panel31");
			ivjPanel31.setLayout(new java.awt.GridBagLayout());
			ivjPanel31.setBackground(java.awt.SystemColor.window);

			java.awt.GridBagConstraints constraintsInstallJREOkBtn = new java.awt.GridBagConstraints();
			constraintsInstallJREOkBtn.gridx = 0; constraintsInstallJREOkBtn.gridy = 0;
			constraintsInstallJREOkBtn.insets = new java.awt.Insets(0, 0, 0, 10);
			getPanel31().add(getInstallJREOkBtn(), constraintsInstallJREOkBtn);

			java.awt.GridBagConstraints constraintsInstallJRECanlBtn = new java.awt.GridBagConstraints();
			constraintsInstallJRECanlBtn.gridx = 1; constraintsInstallJRECanlBtn.gridy = 0;
			getPanel31().add(getInstallJRECanlBtn(), constraintsInstallJRECanlBtn);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPanel31;
}
/**
 * Return the PersJreDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getPersJreDlg() {
	if (ivjPersJreDlg == null) {
		try {
			ivjPersJreDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjPersJreDlg.setName("PersJreDlg");
			ivjPersJreDlg.setLayout(new java.awt.BorderLayout());
			ivjPersJreDlg.setBounds(37, 2017, 364, 238);
			ivjPersJreDlg.setModal(true);
			ivjPersJreDlg.setTitle("JRE Information");
			getPersJreDlg().add(getContentsPane2(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPersJreDlg;
}
/**
 * Return the PersJreOkBtn property value.
 * @return java.awt.Button
 */
private java.awt.Button getPersJreOkBtn() {
	if (ivjPersJreOkBtn == null) {
		try {
			ivjPersJreOkBtn = new java.awt.Button();
			ivjPersJreOkBtn.setName("PersJreOkBtn");
			ivjPersJreOkBtn.setLabel("Ok");
			ivjPersJreOkBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getPersJreDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPersJreOkBtn;
}
/**
 * Return the PersJreTA property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getPersJreTA() {
	if (ivjPersJreTA == null) {
		try {
			ivjPersJreTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjPersJreTA.setName("PersJreTA");
			ivjPersJreTA.setEditable(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPersJreTA;
}
/**
 * Return the ProgressBar property value.
 * @return oem.edge.ed.odc.applet.ProgressBar
 */
private ProgressBar getProgressBar() {
	if (ivjProgressBar == null) {
		try {
			ivjProgressBar = new oem.edge.ed.odc.applet.ProgressBar();
			ivjProgressBar.setName("ProgressBar");
			ivjProgressBar.setBackground(java.awt.SystemColor.window);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProgressBar;
}
/**
 * Return the SaveDlg property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getSaveDlg() {
	if (ivjSaveDlg == null) {
		try {
			ivjSaveDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjSaveDlg.setName("SaveDlg");
			ivjSaveDlg.setResizable(true);
			ivjSaveDlg.setLayout(new java.awt.BorderLayout());
			ivjSaveDlg.setBounds(31, 575, 446, 172);
			ivjSaveDlg.setModal(true);
			ivjSaveDlg.setTitle("Specify installation directory");
			getSaveDlg().add(getContentsPane(), "Center");
			ivjSaveDlg.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowOpened(java.awt.event.WindowEvent e) {    
					try {
						getSaveDlg().toFront();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSaveDlg;
}
/**
 * Return the Button3 property value.
 * @return java.awt.Button
 */
private java.awt.Button getSearchCanBtn() {
	if (ivjSearchCanBtn == null) {
		try {
			ivjSearchCanBtn = new java.awt.Button();
			ivjSearchCanBtn.setName("SearchCanBtn");
			ivjSearchCanBtn.setLabel("Cancel");
			ivjSearchCanBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						getSearchDlg().dispose();
					} catch (java.lang.Throwable ivjExc) {
						handleException(ivjExc);
					}
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSearchCanBtn;
}
/**
 * Return the Dialog1 property value.
 * @return java.awt.Dialog
 */
private java.awt.Dialog getSearchDlg() {
	if (ivjSearchDlg == null) {
		try {
			ivjSearchDlg = new java.awt.Dialog(new java.awt.Frame());
			ivjSearchDlg.setName("SearchDlg");
			ivjSearchDlg.setLayout(new java.awt.BorderLayout());
			ivjSearchDlg.setBackground(java.awt.SystemColor.window);
			ivjSearchDlg.setBounds(36, 2331, 430, 164);
			ivjSearchDlg.setModal(true);
			ivjSearchDlg.setTitle("Searching for JRE");
			getSearchDlg().add(getContentsPane4(), "Center");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSearchDlg;
}
/**
 * Return the Label3 property value.
 * @return java.awt.Label
 */
private java.awt.Label getSearchLbl() {
	if (ivjSearchLbl == null) {
		try {
			ivjSearchLbl = new java.awt.Label();
			ivjSearchLbl.setName("SearchLbl");
			ivjSearchLbl.setAlignment(java.awt.Label.LEFT);
			ivjSearchLbl.setText("Scanning...");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjSearchLbl;
}
/**
 * Return the StatusLbl property value.
 * @return java.awt.Label
 */
private java.awt.Label getStatusLbl() {
	if (ivjStatusLbl == null) {
		try {
			ivjStatusLbl = new java.awt.Label();
			ivjStatusLbl.setName("StatusLbl");
			ivjStatusLbl.setText("Initializing...");
			ivjStatusLbl.setBackground(java.awt.SystemColor.window);
			ivjStatusLbl.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusLbl;
}
/**
 * Return the TextArea1 property value.
 * @return java.awt.TextArea
 */
private java.awt.TextArea getStatusTA() {
	if (ivjStatusTA == null) {
		try {
			ivjStatusTA = new java.awt.TextArea("", 0, 0, java.awt.TextArea.SCROLLBARS_VERTICAL_ONLY);
			ivjStatusTA.setName("StatusTA");
			ivjStatusTA.setBackground(java.awt.SystemColor.window);
			ivjStatusTA.setEditable(false);
			ivjStatusTA.setForeground(java.awt.SystemColor.windowText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStatusTA;
}
/**
 * Insert the method's description here.
 * Creation date: (1/22/2003 4:41:18 PM)
 * @return java.lang.String
 * @param name java.lang.String
 */
public String getVersionStamp(String name) {
	return versionStamps.getProperty(name,null);
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
 * Initializes the applet.
 */
public void init() {
	try {
		super.init();
		setName("InstallAndLaunchApp");
		setLayout(new java.awt.GridBagLayout());
		setBackground(java.awt.SystemColor.window);
		setSize(400, 85);

		java.awt.GridBagConstraints constraintsProgressBar = new java.awt.GridBagConstraints();
		constraintsProgressBar.gridx = -1; constraintsProgressBar.gridy = 1;
		constraintsProgressBar.gridwidth = 0;
		constraintsProgressBar.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsProgressBar.weightx = 1.0;
		constraintsProgressBar.ipady = 10;
		constraintsProgressBar.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getProgressBar(), constraintsProgressBar);

		java.awt.GridBagConstraints constraintsStatusLbl = new java.awt.GridBagConstraints();
		constraintsStatusLbl.gridx = -1; constraintsStatusLbl.gridy = 0;
		constraintsStatusLbl.gridwidth = 0;
		constraintsStatusLbl.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(getStatusLbl(), constraintsStatusLbl);

		java.awt.GridBagConstraints constraintsDetailsBtn = new java.awt.GridBagConstraints();
		constraintsDetailsBtn.gridx = 0; constraintsDetailsBtn.gridy = 2;
		constraintsDetailsBtn.weightx = 1.0;
		constraintsDetailsBtn.insets = new java.awt.Insets(5, 5, 0, 0);
		add(getDetailsBtn(), constraintsDetailsBtn);

		getInstallCB().setCheckboxGroup(getInstallCBG());
		getNoInstallCB().setCheckboxGroup(getInstallCBG());

		// Determine which version of Java we are dealing with...
		String java = System.getProperty("java.version");
		debug("init: java version is " + java);

		boolean done = false;
		StringTokenizer s = new StringTokenizer(java);

		while (! done && s.hasMoreTokens()) {
			String t = s.nextToken();

			try {
				int major = 0;
				int minor = 0;

				int dot = t.indexOf('.');
				int dot2 = t.indexOf('.',dot+1);

				debug("init: dot is " + dot + "; dot2 is " + dot2);
				major = Integer.parseInt(t.substring(0,dot).trim());
				if (dot2 != -1)
					minor = Integer.parseInt(t.substring(dot+1,dot2));
				else
					minor = Integer.parseInt(t.substring(dot+1));

				if (major > 1 || (major == 1 && minor > 1))
					isJava1 = false;

				done = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Determine if this is a cab, and we need a jar...
		String isie = this.getParameter("ISIE");
		String tocab = this.getParameter("TOCAB");

		if (isJava1 && isie != null && isie.equals("YES") && tocab != null) {
			debug("init: IE and java 1, we need a cab");
			URL url = new URL(tocab);
			debug("init: @ " + url);
			this.getAppletContext().showDocument(url);
			firstTime = false;
			return;
		}

		// Set progress to 0.
		getProgressBar().setCompletion(0);

		// Get InstallAndLaunchApp applet parameters...
		String param = getParameter("DEBUG");
		if (param != null) this.debug = true;
		debug("init: debug is " + this.debug);

		this.command = getParameter("-CH_TUNNELCOMMAND");
		debug("init: command (-CH_TUNNELCOMMAND) is " + this.command);

		if (this.command == null ) {
			this.command = getParameter("COMMAND");
			debug("init: command (COMMAND) is " + this.command);
		}

		this.isSOD = this.command != null && this.command.equalsIgnoreCase("FDR");
		this.isDBOX = this.command != null && this.command.equalsIgnoreCase("XFR");
		this.isSD = this.command != null && this.command.equalsIgnoreCase("SD");
		this.isIM = this.command != null && this.command.equalsIgnoreCase("IMS");
		this.isODC = this.command != null && this.command.equalsIgnoreCase("ODC");
		this.isEDU = this.command != null && this.command.equalsIgnoreCase("EDU");
		this.isNEWODC = this.command != null && this.command.equalsIgnoreCase("NEWODC");
		this.isDSH = this.command != null && this.command.equalsIgnoreCase("DSH");

		this.url = getParameter("-URL");
		debug("init: url parameter is " + this.url);

		// Get the LaunchApp application parameters (DSH & ODC)
		param = getParameter("-CH_DEBUG");
		if (param != null) this.debugApp = "-CH_DEBUG";
		debug("init: debugApp is " + this.debugApp);

		this.hostingmachine = getParameter("-CH_HOSTINGMACHINE");
		debug("init: hostingmachine is " + this.hostingmachine);
		this.tunnelcommand = getParameter("-CH_TUNNELCOMMAND");
		debug("init: tunnelcommand is " + this.tunnelcommand);
		this.token = getParameter("-CH_TOKEN");
		this.project = getParameter("-CH_PROJECT");
		this.callmtg = getParameter("-CH_CALL_MEETINGID");
		this.callusr = getParameter("-CH_CALL_USERID");
		this.callpwd = getParameter("-CH_CALL_PASSWORD");
		this.context = getParameter("-CH_SERVLETCONTEXT");
		debug("init: context is " + this.context);
		param = getParameter("-CH_NOSTREAM");
		if (param != null) this.nostream = "-CH_NOSTREAM";
		debug("init: nostream is " + this.nostream);

		// Get the SDApp application parameters (SWD)
		this.sdtoken = getParameter("-SD_TOKEN");

		// For SD, it uses the ODC app server for applet work.
		// So, url will be set, but will be for the SD app server.
		// Use the doc to get the right url for the applet to use to get files.
		if (url != null && !this.isSD)// boo 10/05
			this.appletUrl = url;
		else {
			URL docBase = this.getDocumentBase();
			this.appletUrl = docBase.getProtocol() + "://" + docBase.getHost();
			if (docBase.getPort() != -1)
				this.appletUrl += ":" + docBase.getPort();
			if (this.context != null)
				this.appletUrl += this.context;
			if (url == null)
				this.url = this.appletUrl;
		}
		debug("init: appletUrl is " + this.appletUrl);
		debug("init: url is " + this.url);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void installChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getJreLbl().setEnabled(false);
		getJreTF().setEnabled(false);
		getBrowseBtn().setEnabled(false);
		getJreValidLbl().setEnabled(false);
		getJreInfoLbl().setEnabled(false);
		getJreSearchBtn().setEnabled(false);

		getInstallJREOkBtn().setEnabled(true);
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installComponent(boolean update,int cnt, int tot, String compName, String[] fileName, String versionName, String version) throws Exception {
	// Install the DSC Client
	String installPath = instcfg.getProperty("InstallPath");
	File dir = new File(installPath);

	if (update) {
		getStatusLbl().setText("Updating software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nUpdate " + cnt + " of " + tot + ": " + compName);
	}
	else {
		getStatusLbl().setText("Installing software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nInstall " + cnt + " of " + tot + ": " + compName);
	}

	int base = 0;
	int delta = 100 / fileName.length;
	int top = 100 - (delta * (fileName.length - 1));
	for (int i = 0; i < fileName.length; i++) {
		getStatusTA().append("\n>");
		getStatusTA().append(fileName[i]);

		downloadFile(fileName[i],dir,base,top);

		getStatusTA().append(" ...Done");
		base = top;
		top += delta;
	}

	// Post component completion.
	getStatusTA().append("\n");
	getStatusTA().append(compName);
	getStatusTA().append(" installation complete.");

	// Update config.
	cfg.setProperty(versionName,version);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installComponent(boolean update, int cnt, int tot, String compName, String fileName, String versionName, String version) throws Exception {
	String[] f = new String[1];
	f[0] = fileName;
	installComponent(update,cnt,tot,compName,f,versionName,version);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installJRE(boolean update, int cnt, int tot) throws Exception {
	// Prepare to install
	String jreVersion = null;
	String platJREversion = null;
	String zipName = null;
	boolean isZip = false;

	if (plat.equals("WIN")) {
		zipName = WIN_FILE;
		jreVersion = getVersionStamp("JREWINVER");
		platJREversion = "WINJREversion";
		isZip = true;
	}
	else if (plat.equals("AIX")) {
		zipName = AIX_GZFILE;
		jreVersion = getVersionStamp("JREAIXVER");
		platJREversion = "AIXJREversion";
	}
	else { // it is LINUX x86.
		zipName = LINUX_GZFILE;
		jreVersion = getVersionStamp("JRELINVER");
		platJREversion = "LINJREversion";
	}

	String installPath = instcfg.getProperty("InstallPath");
	File dir = new File(installPath);

	if (update) {
		getStatusLbl().setText("Updating software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nUpdate " + cnt + " of " + tot + ": Java Runtime Environment (pre-req)");
	}
	else {
		getStatusLbl().setText("Installing software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nInstall " + cnt + " of " + tot + ": Java Runtime Environment (pre-req)");
	}
	getStatusTA().append("\nDownloading Java Runtime Environment");

	downloadFile(zipName,dir,0,100);

	getStatusTA().append(" ...done.\nUnpacking Java Runtime Environment:");

	unpackJRE(zipName,dir,isZip);

	getStatusTA().append("\nJava Runtime Environment installation complete.");

	// Update config.
	cfg.setProperty(platJREversion,jreVersion);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installLaunch(boolean isWin, boolean update, int cnt, int tot) throws Exception {
	// Install the Launch Code
	String installPath = instcfg.getProperty("InstallPath");
	File dir = new File(installPath);

	if (update) {
		getStatusLbl().setText("Updating software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nUpdate " + cnt + " of " + tot + ": Client launcher (pre-req)");
	}
	else {
		getStatusLbl().setText("Installing software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nInstall " + cnt + " of " + tot + ": Client launcher (pre-req)");
	}

	if (isWin) {
		getStatusTA().append("\n>");
		getStatusTA().append(LWIN_FILE);
		downloadFile(LWIN_FILE,dir,0,100);
	}
	else {
		getStatusTA().append("\n>");
		getStatusTA().append(LUNIX_FILE);
		downloadFile(LUNIX_FILE,dir,0,100);
		chmod("755",dir + File.separator + LUNIX_FILE);
	}

	// Post component completion.
	getStatusTA().append(" ...Done\n");
	getStatusTA().append("Client launcher installation complete.");

	// Update config.
	cfg.setProperty("LAUNCHVER",getVersionStamp("LAUNCHVER"));
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installLib(boolean update,int cnt, int tot, String compName, String[] fileName, String versionName, String version) throws Exception {
	// Install the DSC Client
	String installPath = instcfg.getProperty("InstallPath");
	File dir = new File(installPath);

	if (update) {
		getStatusLbl().setText("Updating software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nUpdate " + cnt + " of " + tot + ": " + compName);
	}
	else {
		getStatusLbl().setText("Installing software (" + cnt + " of " + tot + ")");
		getStatusTA().append("\nInstall " + cnt + " of " + tot + ": " + compName);
	}

	int base = 0;
	int delta = 100 / fileName.length;
	int top = 100 - (delta * (fileName.length - 1));
	for (int i = 0; i < fileName.length; i++) {
		getStatusTA().append("\n>");
		getStatusTA().append(fileName[i]);

		downloadFile(fileName[i],dir,base,top);
		if (! fileName[i].endsWith(".dll"))
			chmod("750",installPath + File.separator + fileName[i]);

		getStatusTA().append(" ...Done");
		base = top;
		top += delta;
	}

	// Post component completion.
	getStatusTA().append("\n");
	getStatusTA().append(compName);
	getStatusTA().append(" installation complete.");

	// Update config.
	cfg.setProperty(versionName,version);
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public void installLib(boolean update, int cnt, int tot, String compName, String fileName, String versionName, String version) throws Exception {
	String[] f = new String[1];
	f[0] = fileName;
	installLib(update,cnt,tot,compName,f,versionName,version);
}
/**
 * Comment
 */
public void jreTextChanged() {
	if (! getNoInstallCB().getState())
		return;

	String jrePath = getJreTF().getText();

	boolean enabled = false;

	if (jrePath.trim().length() != 0) {
		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalFileAccess");
				PolicyEngine.assertPermission(PermissionID.USERFILEIO);
			}
			catch (Exception e) {
				debug(e.getMessage());
				deniedAccess();
				return;
			}
		}

		try {
			File f = new File(jrePath);
			if (f.exists() && f.isFile()) {
				if (jrePath.equals(lastJreValidated)) {
					enabled = true;
					getJreValidLbl().setText("JRE is valid");
					getJreValidLbl().setForeground(Color.black);
				}
				else {
					getJreValidLbl().setText("Verifying JRE...");
					getJreValidLbl().setForeground(Color.black);
					int valid = validateJRE(jrePath);
					enabled = valid == 0;
					if (enabled) {
						getJreValidLbl().setText("JRE is valid");
						getJreValidLbl().setForeground(Color.black);
					}
					else {
						if (valid == 2) {
							getJreValidLbl().setText("JRE is not valid");
							getJreValidLbl().setForeground(Color.red);
						}
						else {
							getJreValidLbl().setText("JRE is not supported");
							getJreValidLbl().setForeground(Color.blue);
							enabled = true;

							// prompt java 2 warning here.
							getPersJreTA().setText("The JRE you have selected is an older version. Our software is no " +
								"longer supported on this older JRE. While you may use the selected JRE, " +
								"some features of our software may be unavailable and may not work properly. We " +
								"strongly recommend that you use a version 1.5 JRE (Java 5) or higher.");
							setLocation2(getPersJreDlg());
							getPersJreDlg().setVisible(true);
						}
					}
				}
			}
			else if (f.exists()) {
				getJreValidLbl().setText("Enter full path to JRE executable.");
				getJreValidLbl().setForeground(Color.red);
			}
			else {
				getJreValidLbl().setText("File not found!");
				getJreValidLbl().setForeground(Color.red);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	else {
		getJreValidLbl().setText("Enter full path to JRE executable.");
		getJreValidLbl().setForeground(Color.black);
	}

	getInstallJREOkBtn().setEnabled(enabled);

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (01/24/01 1:40:17 PM)
 */
public void launchApp() {
	getStatusLbl().setText("Starting client software...");
	getProgressBar().setCompletion(90);
	getStatusTA().append("\n\n--- Launching (" + getDate() + ")");
	getStatusTA().append("\nStarting Client Software for Customer Connect");

	String pgmDir = instcfg.getProperty("InstallPath");

	String app;
	if (plat.equals("WIN"))
		app = pgmDir + File.separator + LWIN_FILE;
	else
		app = pgmDir + File.separator + LUNIX_FILE;

	debug("app is " + app);

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalExecAccess");
			PolicyEngine.assertPermission(PermissionID.EXEC);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return;
		}
	}

	getProgressBar().setCompletion(95);
	try {
		Process p = Runtime.getRuntime().exec(app);
		PrintStream in = new PrintStream(p.getOutputStream());

		InputStream err = p.getErrorStream();
		InputStream out = p.getInputStream();
		Thread errRun = new Thread(new PipeReader("StdErr",err,debug),"ErrorRunner");
		Thread outRun = new Thread(new PipeReader("StdOut",out,debug),"OutputRunner");
		errRun.start();
		outRun.start();

		String parm = null;

		debug("Writing parms to app:");
		if (isSD) {
			in.println(this.command);
			debug("--> " + this.command);
		}
		else if (isDBOX) {
			in.println("XFR");
			debug("--> XFR");
			in.println("-CH_NOUPDATE");
			debug("--> -CH_NOUPDATE");
		}
		else {
			in.println("TUNNEL");
			debug("--> TUNNEL");
			in.println("-CH_NOUPDATE");
			debug("--> -CH_NOUPDATE");
		}

		in.println("-URL " + url);
		debug("--> -URL " + url);

		if (this.nostream != null) {
			in.println(this.nostream);
			debug("--> " + this.nostream);
		}

		if (this.debugApp != null) {
			in.println(this.debugApp);
			debug("--> " + this.debugApp);
		}

		if (! isDBOX && this.tunnelcommand != null) {
			in.println("-CH_TUNNELCOMMAND " + this.tunnelcommand);
			debug("--> -CH_TUNNELCOMMAND " + this.tunnelcommand);
		}

		if (this.project != null) {
			in.println("-CH_PROJECT " + this.project);
			debug("--> -CH_PROJECT " + this.project);
		}

		if (this.hostingmachine != null) {
			in.println("-CH_HOSTINGMACHINE " + this.hostingmachine);
			debug("--> -CH_HOSTINGMACHINE " + this.hostingmachine);
		}

		if (this.token != null) {
			in.println("-CH_TOKEN " + this.token);
			debug("--> -CH_TOKEN " + this.token);
		}

		if (this.callmtg != null) {
			in.println("-CH_CALL_MEETINGID " + this.callmtg);
			debug("--> -CH_CALL_MEETINGID " + this.callmtg);
		}

		if (this.callusr != null) {
			in.println("-CH_CALL_USERID " + this.callusr);
			debug("--> -CH_CALL_USERID " + this.callusr);
		}

		if (this.callpwd != null) {
			in.println("-CH_CALL_PASSWORD " + this.callpwd);
			debug("--> -CH_CALL_PASSWORD " + this.callpwd);
		}

		if (this.sdtoken != null) {
			in.println("-SD_TOKEN " + this.sdtoken);
			debug("-SD_TOKEN " + this.sdtoken);
		}

		in.println("-THE_END");
		debug("-THE_END");

		in.flush();
		in.close();

		System.out.println("stdin of process is now closed.");

		getStatusTA().append(" ...started.");
		getStatusLbl().setText("Client software started.");
		getProgressBar().setCompletion(100);

		// Prepare to close our applet window.
		try {
			// We need the JSObject class and its getWindow(Applet) and eval(String) methods
			Class js = Class.forName("netscape.javascript.JSObject");
			Class[] gwArgs = { Applet.class };
			Class[] evalArgs = { String.class };
			Method getWindow = js.getMethod("getWindow",gwArgs);
			Method eval = js.getMethod("eval",evalArgs);

			
			Object[] gwParms = { this };
			Object[] evalParms = { "if (self.name == \"Services\") self.setTimeout(\"self.close()\",3000);" };
			Object jsWindow = getWindow.invoke(null,gwParms);
			eval.invoke(jsWindow,evalParms);
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("Class not found: " + cnfe.getMessage());
		}
		catch (NoSuchMethodException nsme) {
			System.out.println("No such method: " + nsme.getMessage());
		}
		catch (IllegalAccessException iae) {
			System.out.println("Illegal access exception: " + iae.getMessage());
		}
		catch (IllegalArgumentException iarge) {
			System.out.println("Illegal argument exception: " + iarge.getMessage());
		}
		catch (InvocationTargetException ite) {
			System.out.println("Invocation target exception: " + ite.getMessage());
		}
	}
	catch (Exception e) {
		debug(e.getMessage());
		getStatusTA().append(" ...NOT started.");
		getStatusLbl().setText("Client software NOT started.");
		getProgressBar().setCompletion(100);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public boolean licenseDSC() {
	// Display the Design Solutions Client license agreement.
	try {
		URL lic = new URL(appletUrl + GetFileAnyPath + "internal.lic");
		debug("Getting license file " + lic.toString());

		formatLicenseTA(lic);
	}
	catch (Exception e1) {
		e1.printStackTrace();
		getStatusTA().append("\n\nThe Client Software for Customer Connect License Agreement ");
		getStatusTA().append("can not be obtained from the server. Contact Support for assistance.");
		return false;
	}

	getLicOkBtn().requestFocus();
	getLicTitleLbl().setText("Client Software for Customer Connect");
	setLocation2(getLicDlg());
	getLicDlg().show();

	if (! msgDlgOk) {
		getStatusTA().append("\n\nUser did not accept the CSDS License Agreement.\nPush Install to reconsider.");
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/2001 1:18:05 PM)
 * @param showLicense boolean
 */
public boolean licenseICA() {
	// Display the Citrix license agreement.
	try {
		URL lic = new URL(appletUrl + GetFileAnyPath + "odc-citrix.lic");
		debug("Getting license file " + lic.toString());

		formatLicenseTA(lic);
	}
	catch (Exception e1) {
		e1.printStackTrace();
		getStatusTA().append("\n\nThe Citrix ICA Client License Agreement ");
		getStatusTA().append("can not be obtained from the server. Contact Support for assistance.");
		return false;
	}

	getLicOkBtn().requestFocus();
	getLicTitleLbl().setText("Citrix ICA Client");
	setLocation2(getLicDlg());
	getLicDlg().show();

	if (! msgDlgOk) {
		getStatusTA().append("\n\nUser did not accept the ICA License Agreement.\nPush Install to reconsider.");
		return false;
	}

	return true;
}
/**
 * Comment
 */
public void locateJre() {
	getFileDlg().setTitle("Select JRE Executable");
	getFileDlg().setMode(FileDialog.LOAD);
	setLocation2(getFileDlg());
	getFileDlg().setVisible(true);

	String file = getFileDlg().getFile();
	if (file != null) {
		String directory = getFileDlg().getDirectory();
		if (directory != null)
			getJreTF().setText(directory + file);
		else
			getJreTF().setText(file);
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Frame frame = new java.awt.Frame();
		InstallAndLaunchApp aInstallAndLaunchApp;
		Class iiCls = Class.forName("oem.edge.ed.odc.applet.InstallAndLaunchApp");
		ClassLoader iiClsLoader = iiCls.getClassLoader();
		aInstallAndLaunchApp = (InstallAndLaunchApp)java.beans.Beans.instantiate(iiClsLoader,"oem.edge.ed.odc.applet.InstallAndLaunchApp");
		frame.add("Center", aInstallAndLaunchApp);
		frame.setSize(aInstallAndLaunchApp.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of java.applet.Applet");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void noInstallChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) {
		getJreLbl().setEnabled(true);
		getJreTF().setEnabled(true);
		getBrowseBtn().setEnabled(true);
		getJreValidLbl().setEnabled(true);
		getJreInfoLbl().setEnabled(true);
		getJreSearchBtn().setEnabled(true);

		jreTextChanged();
	}

	return;
}
/**
 * Comment
 */
public void okCanPushed(ActionEvent e) {
	if (e.getSource() == getMsgOkBtn() ||
		e.getSource() == getLicOkBtn() ||
		e.getSource() == getInstallJREOkBtn() ||
		e.getSource() == getInstallOkBtn() ||
		e.getSource() == getInstallDirTF() ||
		e.getSource() == getJreUpgradeOkBtn())
		msgDlgOk = true;
	else
		msgDlgOk = false;

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2003 12:38:04 PM)
 * @return boolean
 */
public boolean promptJreUpgrade(boolean theirJRE) {
	String prompt;

	getPersJreTA().setText("The JRE you have selected is an older version. Our software is no " +
			"longer supported on this older JRE. While you may use the selected JRE, " +
			"some features of our software will be unavailable. We strongly recommend that you " +
			"use a version 1.5 JRE (Java 5) or higher.");
	if (theirJRE) {
		prompt = "The JRE you have provided is an older version. Our software is no longer " +
				"supported on this older JRE. We strongly recommend that you upgrade to a " +
				"version 1.5 JRE (Java 5) or higher. To do so, press the upgrade button. You will then " +
				"be able to select a new JRE.";
	}
	else {
		prompt = "The JRE we previously delivered with our software is now outdated. Our software " +
				"is no longer supported on this older JRE. We strongly " +
				"recommend that you upgrade to a version 1.5 JRE (Java 5) or higher. We may provide " +
				"a Java 5 JRE for " +
				"you. To upgrade your JRE, press the upgrade button. You will then be able to " +
				"install our Java 5 JRE or select your own.";
	}

	getJreUpgradeTA().setText(prompt);
	getJreUpgradeCB().setState(true);
	setLocation2(getJreUpgradeDlg());
	getJreUpgradeDlg().setVisible(true);

	if (! msgDlgOk && ! getJreUpgradeCB().getState()) {
		// update ini file with JRENOPROMPT=true to not prompt.
		cfg.setBoolProperty("JRENOPROMPT",true);
		// how to save?
	}

	return msgDlgOk;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2002 10:10:56 AM)
 * @param d java.io.File
 */
public void removeFile(File d) {
	if (d.isDirectory()) {
		String[] s = d.list();
		for (int i = 0; i < s.length; i++) {
			File f = new File(d,s[i]);
			removeFile(f);
		}
	}

	if (! d.delete())
		System.out.println("Unable to delete " + d.getPath());
}
/**
 * Contains the thread execution loop.
 */
public void run() {
	
	switch (runMode) {
		
		case 0:
			platformUnsupported = false;	
			String home = null;
			String directory = null;

			// Update message to search for installed app.
			getStatusLbl().setText("Checking for installed client software...");
			getStatusTA().setText("--- Pre-install checking (" + getDate() + ")");
			getStatusTA().append("\nChecking for Client Software for Customer Connect installation:");

			// Determine the platform we are on...
			String[]  result = platform();
			getStatusTA().append("\nPlatform is " + result[0]);
			plat = result[1];

			// Validate the platform before we do anything else.
			// Must be Windows, AIX, or linux x86. We only deploy JREs
			// for those. For all others, a JRE will need to exist.
			if (result[0].indexOf("WIN") == -1 &&
				result[0].indexOf("AIX") == -1 &&
				(result[0].indexOf("LINUX") == -1 || result[0].indexOf("86") == -1)) {
				platformUnsupported = true; 
			}

			// For AIX, we only have a JRE for 5.2 and higher.
			if (plat.equals("AIX")) {
				platformUnsupported = ! validateAIX();
			}

			getProgressBar().setCompletion(5);

			// Request privilege to read config file in user's home.
			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalPropertyRead");
					PrivilegeManager.enablePrivilege("UniversalFileRead");
					PolicyEngine.assertPermission(PermissionID.PROPERTY);
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				// User rejected the right to gain access. Treat as run applet.
				catch (Exception e) {
					debug(e.getMessage());
					deniedAccess();
					return;
				}
			}

			// Get location of config file.
			home = System.getProperty("user.home");
			debug("user.home = " + home);
			instcfg = new ConfigFile();
			cfg = new ConfigFile();

			// Check for config file and load it.
			instini = new File(home + File.separator + "edesign.ini");
			getStatusTA().append("\nChecking for " + instini.toString());
			if (instini.exists())
				if (instini.canRead())
					try {
						debug("Loading ini file.");
						instcfg.load(instini.toString());

						if (! twistINI())
							return;

						// Now, get the new installpath variable.
						debug("Getting install path.");
						directory = instcfg.getProperty("InstallPath");

						if (directory != null) {
							// Prepare to validate the installation.
							getStatusTA().append(" ...found.\nValidating installation");

							getProgressBar().setCompletion(15);
							boolean iniLoaded = true;

							// Need to load the second ini file.
							if (! directory.equals(home)) {
								ini = new File(directory,"edesign.ini");

								if (ini.exists())
									if (ini.canRead())
										cfg.load(ini.toString());
									else {
										debug("No permission to read Customer Information File ");
										debug("File is " + ini.toString());
										iniLoaded = false;
									}
								else {
									debug(ini.getPath() + " not found.");
									iniLoaded = false;
								}
							}
							else {
								ini = instini;
								cfg = instcfg;
							}

							if (iniLoaded) {
								int currentIniVersion = cfg.getIntProperty("INIVERSION",0);
								twistINI2();
								if (cfg.getIntProperty("INIVERSION",0) != currentIniVersion)
									updatedIni = true;

								if (validateInstall(directory)) {
									getStatusTA().append(" ...valid.");
									installStatus = 1;
								}
								else
									getStatusTA().append(" ...not valid.");
							}
							else
								getStatusTA().append(" ...not valid.");
						}
					}
					catch (IOException e) {
						// Corrupted ini file. What to do? Treat it as not installed.
						getStatusTA().append(" ...damaged.");
					}
		
				else
					getStatusTA().append(" ...no access.");
			else
				getStatusTA().append(" ...not found.");

			getProgressBar().setCompletion(30);

			// Revert privileges to read config file in user's home.
			if (isJava1) {
				try {
					PrivilegeManager.revertPrivilege("UniversalPropertyRead");
					PrivilegeManager.revertPrivilege("UniversalFileRead");
					PolicyEngine.revertPermission(PermissionID.PROPERTY);
					PolicyEngine.revertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
				}
			}

			// Go get the version information...
			try {
				debug("run: get DSCVersionStamps from server");

				URL url = new URL(appletUrl + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/DSCVersionStamps");
				URLConnection conn = url.openConnection();

				debug("run: URL connected.");

				// setting the request property to post
				conn.setRequestProperty("method","POST");
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setDefaultUseCaches(false);

				debug("run: post request sent");

				// Check connection status.
				InputStream in = conn.getInputStream();

				if (in != null)
					debug("run: got input stream");
				else
					debug("run: no input stream available");

				byte[] header = new byte[2];
				int sz = in.read(header);

				if (sz == 2)
					debug("run: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
				else if (sz == 1)
					debug("run: response is '" + (char) header[0] + "' or " + header[0]);
				else
					debug("run: no response available");

				// If server didn't respond or sent "NO" as its first 2 bytes of content...
				if (sz != 2 || (header[0] == 'N' && header[1] == 'O')) {
					BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
					String error = rdr.readLine();

					debug("run: error text is '" + error + "'");

					debug("run: servlet refused to download file");

					throw new Exception("Servlet refused to download file:" + error);
				}

				debug("run: receiving data");

				versionStamps = new ConfigFile();
				versionStamps.load(in);

				debug("run: received DSCVersionStamps from server");
			}
			catch (Exception e) {
				debug("run: exception loading version stamps: " + e.getMessage());
				e.printStackTrace();
			}

			// Not currently installed?
			if (installStatus == 0) {
				// Offer to install it.
				getStatusTA().append("\n\nInstallation required:");
				getStatusTA().append("\n Client software for customer connect");
				if (isIM) getStatusTA().append("\n Client software for customer connect - IM (co-req)");
				if (isSOD) getStatusTA().append("\n Client software for customer connect - on-Demand (co-req)");
				if (isSD) getStatusTA().append("\n Client software for customer connect - SD (co-req)");
				if (isNEWODC) {
					getStatusTA().append("\n Client software for customer connect - DSMP (co-req)");
					getStatusTA().append("\n Client software for customer connect - Scraper (co-req)");
				}
				if (isDSH | isODC | isEDU) getStatusTA().append("\n ICA client (co-req)");
				getStatusTA().append("\n Client launcher (pre-req)");
				getStatusTA().append("\n Java Runtime Environment (pre-req)");
				doInstall();
			}

			// Currently installed?
			else {
				// Launch ODC application.
				getStatusTA().append("\nFound software in " + directory);

				boolean updated = false;
				boolean delayFailure = false;

				try {
					doCanDelete();
					updated = updateInstall();
				}
				catch (Exception e) {
					debug("Exception in updateInstall(): " + e.getMessage());
					e.printStackTrace();
					getStatusTA().append("\n\n" + e.getMessage());
					if (! e.getMessage().endsWith("Installation stopped."))
						getStatusTA().append("\nInstallation stopped.");
					getStatusLbl().setText("Installation stopped, view console.");
					return;
				}

				if (updated || updatedIni) {
					// Updated the installation.
					getStatusLbl().setText("Configuring software...");
					getStatusTA().append("\n\n--- Configuring (" + getDate() + ")");
					if (isJava1) {
						try {
							PrivilegeManager.enablePrivilege("UniversalFileWrite");
							PolicyEngine.assertPermission(PermissionID.USERFILEIO);
						}
						catch (Exception e1) {
							debug(e1.getMessage());
							deniedAccess();
							return;
						}
					}

					try {
						getStatusTA().append("\nConfiguration 1 of 1: ");
						getStatusTA().append(ini.getName());
						getStatusTA().append(" file");
						cfg.store(ini.toString());
						getStatusTA().append(" ...saved.");
					}
					catch (Exception e1) {
						getStatusTA().append(" ...NOT saved.");
						debug(e1.getMessage());
						getStatusLbl().setText("Installation stopped, view console.");
						return;
					}
				}

				if (delayFailure) {
					getStatusLbl().setText("Manual intervention required, view console.");
					getProgressBar().setCompletion(90);
					return;
				}

				launchApp();
			}
			break;
		case 1:
			while (! getSearchDlg().isShowing()) {
				try { Thread.sleep(100); }
				catch (Exception e) {}
			}

			// Request privilege to read config file in user's home.
			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileRead");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				// User rejected the right to gain access. Treat as run applet.
				catch (Exception e) {
					debug(e.getMessage());
					deniedAccess();
					return;
				}
			}

			File jre = null;
			if (plat.equals("WIN")) {
				jre = searchWin(new File("C:\\"));
			}
			else {
				jre = searchUnix(new File("/usr"));
				if (jre == null && ! stopSearch) jre = searchUnix(new File("/bin"));
				if (jre == null && ! stopSearch) jre = searchUnix(new File("/opt"));
			}

			getSearchCanBtn().setLabel("Ok");
			if (jre == null)
				getSearchLbl().setText("JRE not found.");
			else {
				getJreTF().setText(jre.getPath());
				getSearchDlg().dispose();
			}
			break;
		case 2:
			// Install the ODC application here...
			doInstall();
			break;
		default:
			System.out.println("Unknown run mode " + runMode + " in run().");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 3:11:32 PM)
 * @return java.io.File
 * @param dir java.io.File
 */
public File searchUnix(File dir) {
	if (dir.isDirectory()) {
		getSearchLbl().setText("Scanning " + dir.getPath() + "...");
		String[] entries = dir.list();
		for (int i = 0; i < entries.length && ! stopSearch; i++) {
			File file = new File(dir,entries[i]);
			if (file.isDirectory()) {
				if (file.canRead()) {
					File f = searchUnix(file);
					if (f != null)
						return f;
				}
			}
			else if (entries[i].equals("jre") || entries[i].equals("java")) {
				if (validateJRE(file.getPath()) == 0)
					return file;
			}
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 3:11:32 PM)
 * @return java.io.File
 * @param dir java.io.File
 */
public File searchWin(File dir) {
	if (dir.isDirectory()) {
		getSearchLbl().setText("Scanning " + dir.getPath() + "...");
		String[] entries = dir.list();
		for (int i = 0; i < entries.length && ! stopSearch; i++) {
			File file = new File(dir,entries[i]);
			if (file.isDirectory()) {
				if (file.canRead()) {
					File f = searchWin(file);
					if (f != null)
						return f;
				}
			}
			else if (entries[i].equals("jre.exe") || entries[i].equals("java.exe")) {
				if (validateJRE(file.getPath()) == 0)
					return file;
			}
		}
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (01/30/01 4:14:57 PM)
 * @param dlg java.awt.Dialog
 */
private void setLocation2(Dialog dlg) {
	Point aLocScr = new Point(0,0);
	Dimension aSize = Toolkit.getDefaultToolkit().getScreenSize();

	try {
		aLocScr = getLocationOnScreen();
		aSize = getSize();
	}
	catch (Exception e) {
	}

	Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();

	Point dLoc = new Point();
	Dimension dSize = dlg.getSize();

	dLoc.x = aLocScr.x + aSize.width / 2 - dSize.width / 2;
	dLoc.y = aLocScr.y + aSize.height / 2 - dSize.height / 2;

	// if too far right, slide it left...
	int i = sSize.width - (dLoc.x + dSize.width);
	if (i < 0)
		dLoc.x += i;
	// if too far left, slide it right...
	if (dLoc.x < 0)
		dLoc.x = 0;

	// if too far down, slide it up...
	i = sSize.height - (dLoc.y + dSize.height);
	if (i < 0)
		dLoc.y += i;

	// if too far up, slide it down...
	if (dLoc.y < 0)
		dLoc.y = 0;

	dlg.setLocation(dLoc);
}
/**
 * Insert the method's description here.
 * Creation date: (04/05/01 1:32:21 PM)
 * @param width int
 * @param height int
 */
public void setSize(int width, int height) {
	super.setSize(width,height);
	validate();
}
/**
 * Starts up the thread.
 */
public void start() {
	if (firstTime) {
		firstTime = false;

		Thread t = new Thread(this,"Applet Runner");
		runMode = 0;
		t.start();
	}
}
/**
 * Comment
 */
public void startSearch() {
	runMode = 1;
	stopSearch = false;
	Thread t = new Thread(this);
	t.start();

	getSearchCanBtn().setLabel("Cancel");
	setLocation2(getSearchDlg());
	getSearchDlg().show();

	if (getSearchCanBtn().getLabel().equals("Cancel"))
		stopSearch = true;
}
/**
 * Terminates the thread and leaves it for garbage collection.
 */
public void stop() {
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2002 10:05:08 AM)
 */
public boolean twistINI() {
	// Need to twist a 1st generation installation and ini file?
	String directory = instcfg.getProperty("ODCInstallPath",null);
	if (directory != null) {
		// See if it is ok to update the installation.
		getMsgDlg().setTitle("Previous install detected");
		getMsgLbl1().setText("A mandatory update is required for the current installation.");
		getMsgLbl2().setText("Push Ok to continue to update the installation.");
		getMsgOkBtn().requestFocus();
		setLocation2(getMsgDlg());
		getMsgDlg().show();

		if (! msgDlgOk) {
			getStatusTA().append("\n\nCustomer declined update. Processing stopped...");
			getStatusLbl().setText("Checking is stopped.");
			return false;
		}

		updateINI = true;

		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalFileAccess");
				PolicyEngine.assertPermission(PermissionID.USERFILEIO);
			}
			catch (Exception e) {
				debug(e.getMessage());
				deniedAccess();
				return false;
			}
		}

		try {
			debug("1st generation twist being performed.");

			// Fix the ini file contents.
			instcfg.setProperty("InstallPath",directory);
			instcfg.setProperty((plat+"JRE"+"version"),instcfg.getProperty("JREVER"));

			// renaming the jre folder to a platform specific folder name
			File jre = new File(directory,"jre");
			File platjre = new File(directory,plat+"JRE");
			jre.renameTo(platjre);

			//  Handling the windows platform
			if (! plat.equalsIgnoreCase("WIN"))
				instcfg.setProperty((plat+"JRE"+"Path"),(plat+"JRE"+File.separator+"bin"+File.separator+"jre"));
			else {
				instcfg.setProperty((plat+"JRE"+"Path"),(plat+"JRE"+File.separator+"bin"+File.separator+"jre.exe"));
				File dsbat = new File(directory,"startds.bat");
				File dsexe = new File(directory,"startds.exe");
				dsbat.renameTo(dsexe);  // rename bat to exe
				File icabat = new File(directory,"startica.bat");
				icabat.delete();
			}

			instcfg.removeProperty("ODCINSTALLPATH");
			instcfg.removeProperty("JREVER");
			instcfg.removeProperty("ODCINSTALLSTATUS");
			instcfg.removeProperty("ODCSTREAM");
			instcfg.removeProperty("ODCPROTOCOL");
			instcfg.removeProperty("ODCHOST");
			instcfg.removeProperty("ODCPORT");

			debug("1st generation twist done.");
		}
		catch (Exception e) {
			debug(e.getMessage());
			return false;
		}
	}

	// Need to twist a 2nd generation ini file?
	// if install and home directories are different and the ini file
	// in the home directory contains other variables, need to split it
	// into 2 ini files: one in home and one in installation.
	String home = System.getProperty("user.home");
	directory = instcfg.getProperty("InstallPath",null);
	if (directory != null && ! directory.equals(home) && instcfg.getProperty("DSCVER",null) != null) {
		if (! updateINI) {
			// See if it is ok to update the installation.
			getMsgDlg().setTitle("Previous install detected");
			getMsgLbl1().setText("A mandatory update is required for the current installation.");
			getMsgLbl2().setText("Push Ok to continue to update the installation.");
			getMsgOkBtn().requestFocus();
			setLocation2(getMsgDlg());
			getMsgDlg().show();

			if (! msgDlgOk) {
				getStatusTA().append("\n\nCustomer declined update. Processing stopped...");
				getStatusLbl().setText("Checking is stopped.");
				return false;
			}
		}

		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalFileAccess");
				PolicyEngine.assertPermission(PermissionID.USERFILEIO);
			}
			catch (Exception e) {
				debug(e.getMessage());
				deniedAccess();
				return false;
			}
		}

		try {
			debug("2nd generation twist being performed.");

			// Fix the ini file contents.
			String installPath = instcfg.getProperty("InstallPath");
			File instPath = new File(installPath);
			if (instPath.exists() && instPath.isDirectory()) {
				debug("install point " + installPath + " exists, creating edesign.ini");
				instcfg.removeProperty("INSTALLPATH");
				instcfg.removeProperty("ODCINSTALLSTATUS");
				instcfg.store(installPath + File.separator + "edesign.ini");
				debug("rebuild " + instini.toString());
				instcfg = new ConfigFile();
				instcfg.setProperty("InstallPath",installPath);
				instcfg.store(instini.toString());
			}
			else {
				debug("install point " + installPath + " does not exist, ignoring edesign.ini");
				instcfg = new ConfigFile();
			}

			debug("2nd generation twist done.");
		}
		catch (Exception e) {
			debug(e.getMessage());
			return false;
		}
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 8:19:15 AM)
 */
public void twistINI2() {
	int iniVersion = cfg.getIntProperty("INIVERSION",0);

	if (iniVersion == 0) {
		// Fix the installation directory ini file contents.
		String installPath = instcfg.getProperty("InstallPath");
		String jrePath = cfg.getProperty("HP10JREPATH",null);

		if (jrePath != null) {
			cfg.setProperty("HPUXJREPATH",installPath + File.separator + jrePath);
			cfg.removeProperty("HP10JREPATH");
			cfg.removeProperty("HP10JREVERSION");
		}

		jrePath = cfg.getProperty("HP11JREPATH",null);

		if (jrePath != null) {
			cfg.setProperty("HPUXJREPATH",installPath + File.separator + jrePath);
			cfg.removeProperty("HP11JREPATH");
			cfg.removeProperty("HP11JREVERSION");
		}

		jrePath = cfg.getProperty("SUN86JREPATH",null);

		if (jrePath != null) {
			cfg.setProperty("SUNOSJREPATH",installPath + File.separator + jrePath);
			cfg.removeProperty("SUN86JREPATH");
			cfg.removeProperty("SUN86JREVERSION");
		}

		iniVersion = 1;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 1) {
		// Need to add the following properties:
		// They instruct the startds program on which class to
		// invoke and what the classpath should be. There is no
		// ICA class/classpath, it is handled internally by the
		// startds program. There is no hosting class/classpath,
		// hosting uses tunnel/ica commands to start.
		cfg.setProperty("NEWODCCLASS","oem.edge.ed.odc.meeting.client.MeetingViewer");
		cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE);
		cfg.setProperty("TUNNELCLASS","oem.edge.ed.odc.applet.LaunchApp");
		cfg.setProperty("TUNNELCLASSPATH",DSC_FILE);
		cfg.setProperty("IMCLASS","oem.edge.ed.odc.applet.SametimeClient");
		cfg.setProperty("IMCLASSPATH",IM1_FILE + ";" + IM2_FILE + ";" + DSC_FILE);
		cfg.setProperty("SDCLASS","oem.edge.ed.sd.SDHostingApp1");
		cfg.setProperty("SDCLASSPATH",XML_FILE + ";" + DSC_FILE);

		iniVersion = 2;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 2) {
		// Introduced a swing version of conferences.
		cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE);

		iniVersion = 3;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 3) {
		// Introduced services on demand jar.
		cfg.setProperty("TUNNELCLASSPATH",SOD_FILE + ";" + DSC_FILE);

		iniVersion = 4;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 4) {
		// Introduced services on demand jar.
		cfg.setProperty("DROPCMDLINECLASSPATH",DSC_FILE);
		cfg.setProperty("DROPCMDLINECLASS","oem.edge.ed.odc.dropbox.client.DropboxCmdline");

		iniVersion = 5;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 5) {
		// Enable reload of new dropboxftp.bat and csccinfo.js
		String installPath = instcfg.getProperty("InstallPath");
		File f1 = new File(installPath,"dropboxftp.bat");
		File f2 = new File(installPath,"csccinfo.js");

		if (f1.exists() || f2.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to redeploy dropboxftp.bat and csccinfo.js (INIVERSION 6)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f1.delete();
				f2.delete();
			}
		}

		iniVersion = 6;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 6) {
		cfg.removeProperty("JRENOPROMPT");
		iniVersion = 7;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 7) {
		// Enable reload of new dropboxftp.bat and dropboxftp.sh
		String installPath = instcfg.getProperty("InstallPath");
		File f1 = new File(installPath,"dropboxftp.bat");
		File f2 = new File(installPath,"dropboxftp.sh");

		if (f1.exists() || f2.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to redeploy dropboxftp.bat and csccinfo.js (INIVERSION 6)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f1.delete();
				f2.delete();
			}
		}

		iniVersion = 8;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 8) {
		// Introduced a swing version of conferences.
		cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE + ";" + DSC_FILE);

		iniVersion = 9;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 9) {
		// Enable reload of new dropboxftp shell
		String installPath = instcfg.getProperty("InstallPath");
		File f1 = new File(installPath,"dropboxftp");

		if (f1.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to redeploy dropboxftp (INIVERSION 10)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f1.delete();
			}
		}

		iniVersion = 10;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 10) {
		// Enable reload of new dropboxftp shell
		String installPath = instcfg.getProperty("InstallPath");
		File f1 = new File(installPath,"startdsc.sh");

		if (f1.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to redeploy startdsc.sh (INIVERSION 11)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f1.delete();
			}
		}

		iniVersion = 11;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 11) {
		// Introduced java 14 based ICA client (so 2 ICA clients).
		cfg.setProperty("ICA1CLASSPATH",ICA3_FILE);
		cfg.setProperty("ICA1CLASS","com.citrix.JICA");
		cfg.setProperty("ICA2CLASSPATH",ICA1_FILE + ";" + ICA2_FILE);
		cfg.setProperty("ICA2CLASS","com.citrix.JICA");

		iniVersion = 12;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 12) {
		// Introduced dropbox service based client.
		cfg.setProperty("XFRSVCCLASSPATH",DSC_FILE);
		cfg.setProperty("XFRSVCCLASS","oem.edge.ed.odc.dropbox.client.soa.DropBox");

		iniVersion = 13;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 13) {
		// Replaced tunnel dropbox with dropbox service based client permanently.
		cfg.removeProperty("XFRSVCCLASSPATH");
		cfg.removeProperty("XFRSVCCLASS");
		cfg.setProperty("XFRCLASSPATH",DSC_FILE);
		cfg.setProperty("XFRCLASS","oem.edge.ed.odc.dropbox.client.DropBox");
		
		iniVersion = 14;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}

	if (iniVersion == 14) {
		// Support Java 1.3 as the minimum, swing jar no longer needed.
		cfg.setProperty("XFRCLASSPATH",DSC_FILE);
		cfg.setProperty("NEWODCCLASSPATH",DSMP_FILE + ";" + DSC_FILE);
		cfg.setProperty("TUNNELCLASSPATH",SOD_FILE + ";" + DSC_FILE);
		cfg.setProperty("DROPCMDLINECLASSPATH",DSC_FILE);
		
		// Not using swing.jar anymore or verify.class
		String installPath = instcfg.getProperty("InstallPath");
		File f = new File(installPath,"swing.jar");

		if (f.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to clean-up swing.jar (INIVERSION 15)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f.delete();
			}
		}

		f = new File(installPath,"verify.class");

		if (f.exists()) {
			boolean okToWrite = true;

			if (isJava1) {
				try {
					PrivilegeManager.enablePrivilege("UniversalFileAccess");
					PolicyEngine.assertPermission(PermissionID.USERFILEIO);
				}
				catch (Exception e) {
					debug(e.getMessage());
					System.out.println("User denied write access, unable to clean-up verify.class (INIVERSION 15)");
					okToWrite = false;
				}
			}

			if (okToWrite) {
				f.delete();
			}
		}
		
		cfg.removeProperty("SWINGVER");

		// Updated the java version required, ensure that the user is prompted at least
		// once.
		cfg.removeProperty("JRENOPROMPT");

		iniVersion = INIVERSION;
		cfg.setIntProperty("INIVERSION",iniVersion);
	}
}
/**
 * Update the JRE installation in directory specified, if necessary.
 */
public void unpackJRE(String zipName, File dir, boolean isZip) throws Exception {
	String jreFolderName = null;
	String platJREpath = null;
	String relJREpath = null;

	if (plat.equalsIgnoreCase("WIN")) {
		jreFolderName= "WINJRE";
		platJREpath = "WINJREpath";
		relJREpath = "jre" + File.separator + "bin" +  File.separator + "java.exe";
	}
	else if (plat.equalsIgnoreCase("AIX")) {
		jreFolderName= "AIXJRE";
		platJREpath = "AIXJREpath";
		relJREpath = "jre" + File.separator + "bin" +  File.separator + "java";
	}
	else { // it is LINUX x86
		jreFolderName= "LINJRE";
		platJREpath = "LINJREpath";
		relJREpath = "jre" + File.separator + "bin" +  File.separator + "java";
	}

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalFileAccess");
			PolicyEngine.assertPermission(PermissionID.USERFILEIO);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			throw e;
		}
	}

	// Remove existing JRE for this platform.
	File jreRenameTo =  new File(dir,jreFolderName);

	if (jreRenameTo.exists())
		removeFile(jreRenameTo);
	else
		System.out.println(jreRenameTo.getName() + " does not need to be deleted.");

	File zip = new File(dir,zipName);

	// is JRE in a zip file.
	if (isZip) {
		int len = 0;
		byte[] buffer = new byte[4096];

		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zip));

		ZipEntry zipEntry = null;
		while ((zipEntry = zipIn.getNextEntry()) != null) {
			getStatusTA().append("\n>" + zipEntry.getName());
			// Patch '/' to localized file separator.
			String name = zipEntry.getName().replace('/',File.separatorChar);
			File file =  new File(dir,name);
			// Entry is for a directory.
			if (zipEntry.isDirectory()) {
				if (file.exists())
					debug("Directory " + file.getPath() + "already exists.");
				else if(! file.mkdir()) {
					debug("Unable to create directory " + file.getPath());
					zipIn.close();
					throw new Exception();
				}
			}
			// Entry is for a file.
			else {
				File fileDir = new File(file.getParent());
				if (! fileDir.exists()) {
					if (! fileDir.mkdirs()) {
						debug("Unable to create directory " + fileDir.getPath());
					}
				}
				if (file.exists()) {
					if (! file.delete())
						debug("Unable to remove existing file " + file.getPath());
				}

				FileOutputStream fileOut = new FileOutputStream(file);
				while ((len = zipIn.read(buffer,0,buffer.length)) > 0) {
					fileOut.write(buffer,0,len);
				}
				fileOut.close();
			}

			getStatusTA().append(" ...Done");
		}

		zipIn.close();

		zip.delete();
	}

	// JRE is a gzip'd tar file.
	else {
		// Uncompress gzip file and delete it.
		GZIPInputStream zipIn = new GZIPInputStream(new FileInputStream(zip));
		String tarName = zipName.substring(0,zipName.length()-3);
		File tar = new File(dir,tarName);
		FileOutputStream tarOut = new FileOutputStream(tar);
		debug("Decompressing " + zipName + " to " + tarName);

		byte[] buffer = new byte[4096];

		int i = 0;
		while ((i = zipIn.read(buffer,0,4096)) != -1) {
			tarOut.write(buffer,0,i);
		}

		zipIn.close();
		tarOut.close();

		debug("Decompress complete, deleting " + zipName);
		zip.delete();

		if (isJava1) {
			try {
				PrivilegeManager.enablePrivilege("UniversalExecAccess");
				PolicyEngine.assertPermission(PermissionID.EXEC);
			}
			catch (Exception e) {
				debug(e.getMessage());
				deniedAccess();
				throw e;
			}
		}

		boolean failure = false;

		String[] args = new String[2];
		args[0] = "/bin/sh";
		args[1] = "-s";

		try {
			debug("Unpacking " + tarName);
			
			Process p = Runtime.getRuntime().exec(args);
			PrintStream out = new PrintStream(p.getOutputStream());

			debug("Install directory is " + dir);
			out.println("cd " + dir);
			out.println("tar -xvf ." + File.separator + tarName);
			out.println("if [ $? -ne 0 ]");
			out.println("then");
			out.println("echo \"Problems while extracting JRE:\"");
			out.println("echo \"End of problem.\" >&2");
			out.println("fi");
			out.println("echo \"Done extracting JRE.\"");
			out.close();

			BufferedReader rdr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = rdr.readLine();
			while (line != null && ! line.startsWith("Done extracting JRE.")) {
				getStatusTA().append("\n>");
				getStatusTA().append(line);
				if (line.equals("Problems while extracting JRE:"))
					failure = true;
				else
					getStatusTA().append("...Done");

				line = rdr.readLine();
			}
			rdr.close();

			if (failure) {
				rdr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				line = rdr.readLine();
				while (line != null  && ! line.startsWith("End of problem.")) {
					getStatusTA().append("\n");
					getStatusTA().append(line);
					line = rdr.readLine();
				}
				rdr.close();
			}
		}
		catch (Exception e) {
			debug("Exception in unpacking thread: " + e.getMessage());
		}

		debug("Done unpacking, deleting " + tarName);
		tar.delete();

		if (failure)
			throw new Exception("Could not extract JRE.");
	}

	// Need to rename the jre directory for various Platforms.
	debug("unpackJre: rename jre directory");
	File jreDir =  new File(dir,"jre");

	// Locate the "CanDelete" section in the cleanup.ini file. We
	// will add to it any directory we try to rename to that exists
	// (fails). If the section does not exist, a new one is created.
	int renameCnt = 0;
	ConfigSection delete = new ConfigSection("CanDelete");
	File cleanup = new File(ini.getParent(),"cleanup.ini");
	ConfigFile cleanupCfg = new ConfigFile();
	Vector deleteVector = new Vector();

	if (cleanup.exists()) {
		if (cleanup.canRead()) {
			cleanupCfg.load(cleanup.getPath());
			deleteVector = cleanupCfg.getSection(delete.getName());

			if (deleteVector.size() > 0) {
				delete = (ConfigSection) deleteVector.elementAt(0);
				cleanupCfg.removeSection(delete);
			}
		}
		else {
			debug("No permission to read JRE Cleanup information file");
			debug("File is " + cleanup.toString());
		}
	}

	// Attempt to rename jre to the base name.
	String curJreFolderName = jreFolderName;	
	boolean renamed = jreDir.renameTo(jreRenameTo);

	// If the rename failed, work our way through alternative
	// directory names, until we succeed. Each failure is added
	// to the "CanDelete" section.
	while (! renamed) {
		debug("unpackJre: rename to " + curJreFolderName + " failed");

		delete.setProperty(curJreFolderName,curJreFolderName);
		renameCnt++;
		curJreFolderName = jreFolderName + "." + renameCnt;

		jreRenameTo = new File(dir,curJreFolderName);

		renamed = jreDir.renameTo(jreRenameTo);
	}

	debug("unpackJre: rename to " + curJreFolderName + " succeeded");

	// If we can delete some directories, add the "CanDelete" section.
	if (delete.getPropertyNames().hasMoreElements()) {
		cleanupCfg.addSection(delete);
		cleanupCfg.store(cleanup.getPath());
	}
	// If not, then delete the cleanup.ini file if it existed.
	else if (cleanup.exists()) {
		cleanup.delete();
	}

	// Add the relative path to the JRE executable.
	String path = curJreFolderName + File.separator + relJREpath;
	cfg.setProperty(platJREpath,path);
}
/**
 * Insert the method's description here.
 * Creation date: (04/05/01 2:09:38 PM)
 * @return boolean
 * @param avail java.lang.String
 * @param installed java.lang.String
 */
public int updateCheck(String compName, String avail, String installed) {
	if (avail == null || installed == null) {
		getStatusTA().append("\n");
		getStatusTA().append(compName);

		if (installed == null)
			getStatusTA().append("\n  Not installed.");
		else {
			getStatusTA().append("\n  Installed:                 ");
			getStatusTA().append(installed);
		}

		getStatusTA().append("\n  Mandatory update.");

		return 2;
	}

	StringTokenizer av = new StringTokenizer(avail,".");
	StringTokenizer in = new StringTokenizer(installed,".");

	boolean done = false;
	int result = 0;
	int i = 0;

	while (! done) {
		i++;
		if (av.hasMoreTokens())
			if (in.hasMoreTokens()) {
				String aver = av.nextToken();
				String iver = in.nextToken();
				int a = Integer.parseInt(aver);
				int b = Integer.parseInt(iver);
				result = a - b;
				if (result != 0)
					done = true;
			}
			else {
				result = 1;
				done = true;
			}
		else
			if (in.hasMoreTokens()) {
				result = -1;
				done = true;
			}
			else {
				result = 0;
				done = true;
			}
	}

	getStatusTA().append("\n");
	getStatusTA().append(compName);
	getStatusTA().append("\n  Installed:                 ");
	getStatusTA().append(installed);

	// Upgrading?
	if (result > 0) {
		if (i < 4) result = 2;
		else result = 1;
	}
	// Downgrading?
	else if (result < 0) {
		if (i < 4)
			result = 2;
		else
			result = 0;
	}

	if (result == 2)
		getStatusTA().append("\n  Mandatory update:          ");
	else if (result == 1)
		getStatusTA().append("\n  Optional update available: ");
	else
		getStatusTA().append("\n  No update needed.");

	if (result > 0)
		getStatusTA().append(avail);
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (04/05/01 9:48:25 AM)
 * @return boolean
 */
public boolean updateInstall() throws Exception {
	getStatusLbl().setText("Checking for software updates...");
	
	boolean updated = false;

	// Things to update.
	int updateJRE = 0;
	int updateDSC = 0;
	int updateSOD = 0;
	int updateDSMP = 0;
	int updateSCRIPT = 0;
	int updateLDS = 0;
	int updateLIB = 0;
	int updateIM = 0;
	int updateXML = 0;
	int updateICA = 0;
	int updateLaunch = 0;
	String jreVersion = null;
	boolean isWin = false;
	String versionProperty = plat + "JREversion";

	getStatusTA().append("\n");

	String version;

	// Determine if we need to update any DSC client software.
	if (foundDSC) version = cfg.getProperty("DSCVER",null);
	else version = null;
	updateDSC = updateCheck("Client software for customer connect",getVersionStamp("DSCVER"),version);

	// Determine if we need to update any dropbox software.
	if (isDBOX) {
		// On Windows?
		if (plat.equals("WIN")) {
			// Check for updates to dropboxftp and cmdline scripts.
			if (foundSCRIPT) version = cfg.getProperty("SCRIPTVER",null);
			else version = null;
			updateSCRIPT = updateCheck("Client software for customer connect - dropboxftp",getVersionStamp("SCRIPTVER"),version);

			if (foundLDS) version = cfg.getProperty("LDSVER",null);
			else version = null;
			updateLDS = updateCheck("Client software for customer connect - cmdline",getVersionStamp("LDSVER"),version);
		}

		// Update dropbox URL for cmdline?
		String DBOXURL = cfg.getProperty("DBOXURL",null);
		if (DBOXURL == null || ! DBOXURL.equals(url)) {
			cfg.setProperty("DBOXURL",url);
			updated = true;
		}
	}

	// Determine if we need to update any SOD client software.
	if (isSOD) {
		if (foundSOD) version = cfg.getProperty("SODVER",null);
		else version = null;
		updateSOD = updateCheck("Client software for customer connect - on-Demand",getVersionStamp("SODVER"),version);
	}

	// Determine if we need to update any ODC client software.
	if (isNEWODC) {
		// On Windows?
		if (plat.equals("WIN")) {
			// Check for updates to cmdline scripts.
			if (foundLDS) version = cfg.getProperty("LDSVER",null);
			else version = null;
			updateLDS = updateCheck("Client software for customer connect - cmdline",getVersionStamp("LDSVER"),version);
		}

		// Update web conference URL for cmdline?
		String CONFURL = cfg.getProperty("CONFURL",null);
		if (CONFURL == null || ! CONFURL.equals(url)) {
			cfg.setProperty("CONFURL",url);
			updated = true;
		}

		// Check for updates to base web conference software.
		if (foundDSMP) version = cfg.getProperty("DSMPVER",null);
		else version = null;
		updateDSMP = updateCheck("Client software for customer connect - DSMP",getVersionStamp("DSMPVER"),version);

		// Need to update the Scraper Client?
		String libVersion = null;
		boolean platHasScraper = true;
		if (plat.equals("WIN")) {
			libVersion = getVersionStamp("LIBWINVER");
			version = cfg.getProperty("LIBWINVER",null);
		}
		else if (plat.equals("AIX")) {
			libVersion = getVersionStamp("LIBAIXVER");
			version = cfg.getProperty("LIBAIXVER",null);
		}
		else if (plat.equals("SUNSP")) {
			libVersion = getVersionStamp("LIBSUNSPVER");
			version = cfg.getProperty("LIBSUNSPVER",null);
		}
		else if (plat.equals("LIN")) {
			libVersion = getVersionStamp("LIBLINVER");
			version = cfg.getProperty("LIBLINVER",null);
		}
		else if (plat.equals("HPUX")) {
			libVersion = getVersionStamp("LIBHPUXVER");
			version = cfg.getProperty("LIBHPUXVER",null);
		}
		else if (plat.equals("MACOSX")) { // will be adding a scraper for mac
			libVersion = null;
			version = null;
			platHasScraper = false;
		}
		else {
			libVersion = null;
			version = null;
			platHasScraper = false;
		}
		if (! foundLIB)
			version = null;
		if (platHasScraper)
			updateLIB = updateCheck("Client software for customer connect - Scraper",libVersion,version);
	}

	// Determine if we need to update any IM client software.
	if (isIM) {
		if (foundIM) version = cfg.getProperty("IMVER",null);
		else version = null;
		updateIM = updateCheck("Client software for customer connect - IM",getVersionStamp("IMVER"),version);
	}

	// Determine if we need to update any SD client software.
	if (isSD) {
		if (foundXML) version = cfg.getProperty("XMLVER",null);
		else version = null;
		updateXML = updateCheck("Client software for customer connect - SD",getVersionStamp("XMLVER"),version);
	}

	// Determine if we need to update the ICA client.
	if (isDSH | isODC | isEDU) {
		if (foundICA) version = cfg.getProperty("ICAVER",null);
		else version = null;
		updateICA = updateCheck("ICA client",getVersionStamp("ICAVER"),version);
	}

	// Determine if we need to update the launch script.
	if (foundLAUNCH) version = cfg.getProperty("LAUNCHVER",null);
	else version = null;
	updateLaunch = updateCheck("Client launcher",getVersionStamp("LAUNCHVER"),version);

	// Determine if we need to update the JRE.

	// Get known JRE information
	String jrePathS = cfg.getProperty(plat+"JREpath",null);
	System.out.println("jrePathS: " + jrePathS);
	File jrePath = null;
	if (jrePathS != null)
		jrePath = new File(jrePathS);

	// Unsupported JRE platform, no JRE or user's own JRE?
	if (platformUnsupported || jrePathS == null || jrePath.isAbsolute()) {
		System.out.println("Unsupported platform, no JRE or absolute.");
		updateJRE = (foundJRE == 0) ? 0 : 2;
	}

	// JRE found and it is from us.
	else {
		if (foundJRE != 2) version = cfg.getProperty(versionProperty,null);
		else version = null;

		if (plat.equals("WIN")) {
			jreVersion = getVersionStamp("JREWINVER");
			isWin = true;
		}
		else if (plat.equals("AIX"))
			jreVersion = getVersionStamp("JREAIXVER");
		else // it is LINUX
			jreVersion = getVersionStamp("JRELINVER");

		// Determine if we need to update the JRE.
		updateJRE = updateCheck("Java Runtime Environment",jreVersion,version);
	}

	System.out.println("updateJRE: " + updateJRE);

	getProgressBar().setCompletion(40);

	// Nothing to update, just return false.
	if (updateJRE == 0 && updateICA == 0 && updateDSC == 0 && updateLaunch == 0 && updateSOD == 0 &&
		updateDSMP == 0 && updateIM == 0 && updateXML == 0 && updateLIB == 0 &&
		updateSCRIPT == 0 && updateLDS == 0) {
		if (updated) {
			getStatusTA().append("\n\nConfiguration required.");
		}
		else {
			getStatusTA().append("\n\nNo installation or configuration required.");
		}
		return updated;
	}

	// If no update is critical, we ask the user, otherwise we just do it...
	if (updateJRE != 2 && updateICA != 2 && updateDSC != 2 && updateLaunch != 2 && updateSOD != 2 &&
		updateDSMP != 2 && updateIM != 2 && updateXML != 2 && updateLIB != 2 &&
		updateSCRIPT != 2 && updateLDS != 2) {
		getStatusTA().append("\n\nPrompting user to install optional update(s)");

		// Ask the user if it is ok to update the installation.
		getMsgDlg().setTitle("Software Updates");
		getMsgLbl1().setText("A software update is available. Select Yes");
		getMsgLbl2().setText("to update or No to continue without.");
		getMsgOkBtn().setLabel("Yes");
		getMsgCanBtn().setLabel("No");
		getMsgOkBtn().requestFocus();
		setLocation2(getMsgDlg());
		getMsgDlg().show();
		getMsgCanBtn().setLabel("Cancel");
		getMsgOkBtn().setLabel("Ok");

		if (! msgDlgOk) {
			getStatusTA().append(" ...declined.");
			if (updated) {
				getStatusTA().append("\n\nConfiguration required.");
			}
			else {
				getStatusTA().append("\n\nNo installation or configuration required.");
			}
			return updated;
		}

		getStatusTA().append(" ...approved.");
	}

	if (updateJRE == 2 || updateICA == 2 || updateDSC == 2 || updateLaunch == 2 || updateSOD == 2 ||
		updateDSMP == 2 || updateIM == 2 || updateXML == 2 || updateLIB == 2 ||
		updateSCRIPT == 2 || updateLDS == 2) {
		getStatusTA().append("\n\nInstallation required:");
		if (updateDSC == 2) getStatusTA().append("\n Client software for customer connect");
		if (updateSCRIPT == 2) getStatusTA().append("\n Client software for customer connect - dropboxftp (co-req)");
		if (updateLDS == 2) getStatusTA().append("\n Client software for customer connect - cmdline (co-req)");
		if (updateSOD == 2) getStatusTA().append("\n Client software for customer connect - on-Demand (co-req)");
		if (updateDSMP == 2) getStatusTA().append("\n Client software for customer connect - DSMP (co-req)");
		if (updateLIB == 2) getStatusTA().append("\n Client software for customer connect - Scraper (co-req)");
		if (updateIM == 2) getStatusTA().append("\n Client software for customer connect - IM (co-req)");
		if (updateXML == 2)  getStatusTA().append("\n Client software for customer connect - SD (co-req)");
		if (updateICA == 2) getStatusTA().append("\n ICA client (co-req)");
		if (updateLaunch == 2) getStatusTA().append("\n Client launcher (pre-req)");
		if (updateJRE == 2) getStatusTA().append("\n Java Runtime Environment (pre-req)");
	}

	if (updateJRE == 1 || updateICA == 1 || updateDSC == 1 || updateLaunch == 1 || updateSOD == 1 ||
		updateDSMP == 1 || updateIM == 1 || updateXML == 1 || updateLIB == 1 ||
		updateSCRIPT == 1 || updateLDS == 1) {
		getStatusTA().append("\n\nOptional install(s) request by user:");
		if (updateDSC == 1) getStatusTA().append("\n Client software for customer connect");
		if (updateSCRIPT == 1) getStatusTA().append("\n Client software for customer connect - dropboxftp (co-req)");
		if (updateLDS == 1) getStatusTA().append("\n Client software for customer connect - cmdline (co-req)");
		if (updateSOD == 1) getStatusTA().append("\n Client software for customer connect - on-Demand (co-req)");
		if (updateDSMP == 1) getStatusTA().append("\n Client software for customer connect - DSMP (co-req)");
		if (updateLIB == 1) getStatusTA().append("\n Client software for customer connect - Scraper (co-req)");
		if (updateIM == 1) getStatusTA().append("\n Client software for customer connect - IM (co-req)");
		if (updateXML == 1)  getStatusTA().append("\n Client software for customer connect - SD (co-req)");
		if (updateICA == 1) getStatusTA().append("\n ICA client (co-req)");
		if (updateLaunch == 1) getStatusTA().append("\n Client launcher (pre-req)");
		if (updateJRE == 1) getStatusTA().append("\n Java Runtime Environment (pre-req)");
	}

	getStatusLbl().setText("Preparing software updates...");

	getProgressBar().setCompletion(50);

	int jreChoice = 0;

	// Choose a JRE if necessary.
	if (updateJRE != 0) {
		System.out.println("Need to update JRE.");
		System.out.println("foundJRE: " + foundJRE);

		// If we found a 1.1.8 jre, then ask to upgrade it...
		if (foundJRE == 1) {
			boolean noPrompt = cfg.getBoolProperty("JRENOPROMPT",false);
			System.out.println("noPrompt: " + noPrompt);
			if (noPrompt || ! promptJreUpgrade(jrePath.isAbsolute())) {
				updateJRE = 0;
			}
		}

		System.out.println("updateJRE: " + updateJRE);
		
		// Still have an update available?
		if (updateJRE != 0) {
			String installPath = instcfg.getProperty("InstallPath");
			// If we found a valid JRE, but an update is available, then the
			// valid JRE was delivered by us, and we have an update to it.
			boolean isOptional = foundJRE == 0;
			if ((jreChoice = chooseJRE(installPath,isOptional)) == 0)
				throw new Exception("\n\nInstallation stopped.");
		}
	}

	getProgressBar().setCompletion(70);

	// Need to show DSC license?
	if ((updateDSC != 0 || updateSOD != 0 || updateLaunch != 0 || updateDSMP != 0 || updateIM != 0 || updateXML != 0 || updateLIB != 0 || updateSCRIPT != 0 || updateLDS != 0) &&
		! foundDSC && ! foundSOD && ! foundSCRIPT && ! foundLDS && ! foundDSMP && ! foundIM && ! foundXML && ! foundLAUNCH && ! foundLIB) {
		if (! licenseDSC())
			throw new Exception("\n\nInstallation stopped.");
	}

	getProgressBar().setCompletion(90);

	// Update the ICA client if necessary.
	if (updateICA != 0 && ! foundICA) {
		if (! licenseICA())
			throw new Exception("\n\nInstallation stopped.");
	}

	getProgressBar().setCompletion(100);

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalFileAccess");
			PolicyEngine.assertPermission(PermissionID.USERFILEIO);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return false;
		}
	}

	getStatusTA().append("\n\n--- Installing (" + getDate() + ")");
	int i = 1;
	int j = 0;
	if (updateDSC > 0) j++;
	if (updateSOD > 0) j++;
	if (updateSCRIPT > 0) j++;
	if (updateLDS > 0) j++;
	if (updateDSMP > 0) j++;
	if (updateLIB > 0) j++;
	if (updateIM > 0) j++;
	if (updateXML > 0) j++;
	if (updateICA > 0) j++;
	if (updateLaunch > 0) j++;
	if (updateJRE > 0 && jreChoice == 2) j++;

	// Update the DSC client if necessary.
	if (updateDSC != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect",DSC_FILE,"DSCVER",getVersionStamp("DSCVER"));
	}

	// Update the dboxftp client if necessary.
	if (updateSCRIPT != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - dropboxftp (co-req)",SCRIPT_FILE,"SCRIPTVER",getVersionStamp("SCRIPTVER"));
	}

	// Update the cmdline client if necessary.
	if (updateLDS != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - cmdline (co-req)",LDSSCRIPT_FILE,"LDSVER",getVersionStamp("LDSVER"));
	}

	// Update the SOD client if necessary.
	if (updateSOD != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - on-Demand (co-req)",SOD_FILE,"SODVER",getVersionStamp("SODVER"));
	}

	// Update the IM client if necessary.
	if (updateIM != 0) {
		String[] im = new String[2];
		im[0] = IM1_FILE;
		im[1] = IM2_FILE;

		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - IM (co-req)",im,"IMVER",getVersionStamp("IMVER"));
	}

	// Update the SD client if necessary.
	if (updateXML != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - SD (co-req)",XML_FILE,"XMLVER",getVersionStamp("XMLVER"));
	}

	// Update the DSMP client if necessary.
	if (updateDSMP != 0) {
		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"Client software for customer connect - DSMP (co-req)",DSMP_FILE,"DSMPVER",getVersionStamp("DSMPVER"));
	}

	// Update the Scraper client if necessary.
	if (updateLIB != 0) {
		// Install the Scraper Client
		String libName = null;
		String[] lib = null;
		String libVersion = null;
		String platLibVersion = null;

		if (plat.equals("WIN")) {
			lib = new String[2];
			lib[0] = WIN1_LIB;
			lib[1] = WIN2_LIB;
			libVersion = getVersionStamp("LIBWINVER");
			platLibVersion = "LIBWINVER";
		}
		else if (plat.equals("AIX")) {
			libName = AIX_LIB;
			libVersion = getVersionStamp("LIBAIXVER");
			platLibVersion = "LIBAIXVER";
		}
		else if (plat.equals("SUNSP")) {
			lib = new String[2];
			lib[0] = SUN1_LIB;
			lib[1] = SUN2_LIB;
			libVersion = getVersionStamp("LIBSUNSPVER");
			platLibVersion = "LIBSUNSPVER";
		}
		else if (plat.equals("LIN")) {
			libName = LIN_LIB;
			libVersion = getVersionStamp("LIBLINVER");
			platLibVersion = "LIBLINVER";
		}
		else { // it is HPUX...
			lib = new String[2];
			lib[0] = HPUX1_LIB;
			lib[1] = HPUX2_LIB;
			libVersion = getVersionStamp("LIBHPUXVER");
			platLibVersion = "LIBHPUXVER";
		}

		if (libName != null) {
			getProgressBar().setCompletion(0);
			installLib(true,i++,j,"Client software for customer connect - Scraper (co-req)",libName,platLibVersion,libVersion);
		}
		else {
			getProgressBar().setCompletion(0);
			installLib(true,i++,j,"Client software for customer connect - Scraper (co-req)",lib,platLibVersion,libVersion);
		}
	}

	// Update the ICA client if necessary.
	if (updateICA != 0) {
		String[] ica = new String[3];
		ica[0] = ICA1_FILE;
		ica[1] = ICA2_FILE;
		ica[2] = ICA3_FILE;

		getProgressBar().setCompletion(0);
		installComponent(true,i++,j,"ICA client (co-req)",ica,"ICAVER",getVersionStamp("ICAVER"));
	}

	// Update the launch script if necessary.
	if (updateLaunch != 0) {
		getProgressBar().setCompletion(0);
		installLaunch(plat.equals("WIN"),true,i++,j);
	}

	// Update the JRE if necessary.
	if (updateJRE != 0 && jreChoice == 2) {
		getProgressBar().setCompletion(0);
		installJRE(true,i,j);
	}

	getStatusTA().append("\n\nInstallation complete.");

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 2:26:12 PM)
 * @return boolean
 */
public boolean validateInstall(String directory) throws IOException {
	// Check the status of the installed files.
	boolean found = false;

	String path = directory + File.separator;
	if (plat.equals("WIN"))
		path += LWIN_FILE;
	else
		path += LUNIX_FILE;
	found |= (foundLAUNCH = fileExists(path));

	path = cfg.getProperty(plat+"JREpath",null);
	if (path != null) {
		File jrePath = new File(path);
		if (! jrePath.isAbsolute())
			path = directory + File.separator + path;
		foundJRE = 2;
		if (fileExists(path))
			foundJRE = validateJRE(path);
	}
	else
		foundJRE = 2;
	found |= (foundJRE != 2);

	path = directory + File.separator + DSC_FILE;
	found |= (foundDSC = fileExists(path));

	path = directory + File.separator + DSMP_FILE;
	found |= (foundDSMP = fileExists(path));

	path = directory + File.separator + SCRIPT_FILE;
	found |= (foundSCRIPT = fileExists(path));

	path = directory + File.separator + LDSSCRIPT_FILE;
	found |= (foundLDS = fileExists(path));

	path = directory + File.separator + SOD_FILE;
	found |= (foundSOD = fileExists(path));

	path = directory + File.separator + IM1_FILE;
	String path2 = directory + File.separator + IM2_FILE;
	found |= (foundIM = (fileExists(path) && fileExists(path2)));

	path = directory + File.separator + XML_FILE;
	found |= (foundXML = fileExists(path));

	path = directory + File.separator + ICA1_FILE;
	path2 = directory + File.separator + ICA2_FILE;
	String path3 = directory + File.separator + ICA3_FILE;
	found |= (foundICA = (fileExists(path) && fileExists(path2) && fileExists(path3)));

	if (plat.equals("WIN")) {
		path = directory + File.separator + WIN1_LIB;
		path2 = directory + File.separator + WIN2_LIB;
		found |= (foundLIB = (fileExists(path) && fileExists(path2)));
	}
	else if (plat.equals("AIX")) {
		path = directory + File.separator + AIX_LIB;
		found |= (foundLIB = fileExists(path));
	}
	else if (plat.equals("SUNSP")) {
		path = directory + File.separator + SUN1_LIB;
		path2 = directory + File.separator + SUN2_LIB;
		found |= (foundLIB = (fileExists(path) && fileExists(path2)));
	}
	else if (plat.equals("LIN")) {
		path = directory + File.separator + LIN_LIB;
		found |= (foundLIB = fileExists(path));
	}
	else if (plat.equals("HPUX")) {
		path = directory + File.separator + HPUX1_LIB;
		path2 = directory + File.separator + HPUX2_LIB;
		found |= (foundLIB = (fileExists(path) && fileExists(path2)));
	}
	else {
		foundLIB = false;
	}

	return found;
}
public boolean validateAIX() {
	try {
		System.out.println("Validating AIX");
		String[] args = new String[2];
		args[0] = "/usr/bin/oslevel";
		args[1] = "-r";

		Process p = Runtime.getRuntime().exec(args);

		MultiPipeOutputStream out = new MultiPipeOutputStream();
		MultiPipeOutputStream err = new MultiPipeOutputStream(out);
		MultiPipeInputStream in = new MultiPipeInputStream(out);

		Thread pOut = new Thread(new PipeReader(p.getInputStream(),out));
		Thread pErr = new Thread(new PipeReader(p.getErrorStream(),err));
		pOut.start();
		pErr.start();

		for (int i = 0; i < 25; i++) {
			if (in.available() >= 2)
				i = 25;
			else {
				System.out.println("No validation text available, waiting...");
				try {
					Thread.sleep(1000);
				}
				catch (Exception e1) {}
			}
		}

		byte[] b = new byte[in.available()];
		in.read(b);

		String text = new String(b);
		System.out.println("Validation text: " + text);
		if (text.charAt(0) != '5') {
			return false;
		}
		if (text.charAt(1) != '2' &&
			text.charAt(1) != '3' &&
			text.charAt(1) != '4' &&
			text.charAt(1) != '5') {
			return false;
		}

		in.close();
		p.destroy();
		return true;
	}
	catch (IOException e) {
		System.out.println(e.getMessage());
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 11:10:18 AM)
 * @return boolean
 * @param path java.lang.String
 */
public int validateJRE(String path) {
	int valid = 2;

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalExecAccess");
			PolicyEngine.assertPermission(PermissionID.EXEC);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return valid;
		}
	}

	// Create the JavaVersionVerifier.class
	try {
		PrivilegeManager.enablePrivilege("UniversalFileAccess");
		PolicyEngine.assertPermission(PermissionID.FILEIO);
		String installPath = instcfg.getProperty("InstallPath");
		InputStream in = getClass().getResourceAsStream("/JavaVersionVerifier.mass");
		File verify = new File(installPath,"JavaVersionVerifier.class");
		FileOutputStream out = new FileOutputStream(verify);
		int b = in.read();
		while (b != -1) {
			out.write(b);
			b = in.read();
		}
		out.close();
	}
	catch (IOException e) {
		System.out.println("Failed to create JavaVersionVerifier.class, verification of JRE will fail.");
	}

	try {
		System.out.println("Validating JRE");
		String[] args = null;
		String[] env = null;

		if (path.endsWith("jre") || path.endsWith("jre.exe")) {
			args = new String[4];
			args[0] = path;
			args[1] = "-cp";
			args[2] = instcfg.getProperty("InstallPath");
			args[3] = "JavaVersionVerifier";
		}
		else {
			args = new String[2];
			args[0] = path;
			args[1] = "JavaVersionVerifier";
			env = new String[1];
			env[0] = "CLASSPATH=" + instcfg.getProperty("InstallPath");
		}

		Process p;
		
		if (env == null)
			p = Runtime.getRuntime().exec(args);
		else
			p = Runtime.getRuntime().exec(args,env);

		MultiPipeOutputStream out = new MultiPipeOutputStream();
		MultiPipeOutputStream err = new MultiPipeOutputStream(out);
		MultiPipeInputStream in = new MultiPipeInputStream(out);

		Thread pOut = new Thread(new PipeReader(p.getInputStream(),out));
		Thread pErr = new Thread(new PipeReader(p.getErrorStream(),err));
		pOut.start();
		pErr.start();

		for (int i = 0; i < 25; i++) {
			if (in.available() >= 3)
				i = 25;
			else {
				System.out.println("No validation text available, waiting...");
				try {
					Thread.sleep(1000);
				}
				catch (Exception e1) {}
			}
		}

		byte[] b = new byte[in.available()];
		in.read(b);

		String text = new String(b);
		System.out.println("Validation text: " + text);
		if (text.startsWith("1.5") ||
			text.startsWith("1.6"))
			valid = 0;
		else if (text.startsWith("1.3") ||
				text.startsWith("1.4"))
			valid = 1;

		in.close();
		p.destroy();
	}
	catch (IOException e) {
		System.out.println(e.getMessage());
	}

	// HPUX didn't validate, we'll try it with -green option.
	if (valid == 2 && plat.equals("HPUX"))
		return validateJREHPUX(path);

	if (valid == 0) {
		lastJreValidated = path;
		System.out.println("Successful Java 5+ validation.");
	}
	else if (valid == 1)
		System.out.println("Successful Java 1.3+ validation.");
	else
		System.out.println("Unsuccessful validation");

	if (valid != 2) {
		if (cfg.getProperty("HPPARMS",null) != null) {
			cfg.removeProperty("HPPARMS");
			updatedIni = true;
		}
	}

	return valid;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 11:10:18 AM)
 * @return boolean
 * @param path java.lang.String
 */
public int validateJREHPUX(String path) {
	int valid = 2;

	if (isJava1) {
		try {
			PrivilegeManager.enablePrivilege("UniversalExecAccess");
			PolicyEngine.assertPermission(PermissionID.EXEC);
		}
		catch (Exception e) {
			debug(e.getMessage());
			deniedAccess();
			return valid;
		}
	}

	try {
		System.out.println("Validating JRE with -green option");
		String[] args = null;
		String[] env = null;

		if (path.endsWith("jre") || path.endsWith("jre.exe")) {
			args = new String[5];
			args[0] = path;
			args[1] = "-green";
			args[2] = "-cp";
			args[3] = instcfg.getProperty("InstallPath");
			args[4] = "JavaVersionVerifier";
		}
		else {
			args = new String[3];
			args[0] = path;
			args[1] = "-green";
			args[2] = "JavaVersionVerifier";
			env = new String[1];
			env[0] = "CLASSPATH=" + instcfg.getProperty("InstallPath");
		}

		Process p;
		
		if (env == null)
			p = Runtime.getRuntime().exec(args);
		else
			p = Runtime.getRuntime().exec(args,env);

		MultiPipeOutputStream out = new MultiPipeOutputStream();
		MultiPipeOutputStream err = new MultiPipeOutputStream(out);
		MultiPipeInputStream in = new MultiPipeInputStream(out);

		Thread pOut = new Thread(new PipeReader(p.getInputStream(),out));
		Thread pErr = new Thread(new PipeReader(p.getErrorStream(),err));
		pOut.start();
		pErr.start();

		for (int i = 0; i < 25; i++) {
			if (in.available() >= 3)
				i = 25;
			else {
				try {
					Thread.sleep(1000);
				}
				catch (Exception e1) {}
			}
		}

		byte[] b = new byte[in.available()];
		in.read(b);

		String text = new String(b);
		System.out.println("Validation text: " + text);
		if (text.startsWith("1.5") ||
			text.startsWith("1.6"))
			valid = 0;
		else if (text.startsWith("1.3") ||
				text.startsWith("1.4"))
			valid = 1;

		in.close();
		p.destroy();
	}
	catch (IOException e) {
		System.out.println(e.getMessage());
	}

	if (valid == 0) {
		lastJreValidated = path;
		System.out.println("Successful Java 5+ validation.");
	}
	else if (valid == 1)
		System.out.println("Successful Java 1.3+ validation.");
	else
		System.out.println("Unsuccessful validation");

	if (valid != 2) {
		if (cfg.getProperty("HPPARMS",null) == null) {
			cfg.setProperty("HPPARMS","-green");
			updatedIni = true;
		}
	}

	return valid;
}
}  // @jve:visual-info  decl-index=0 visual-constraint="20,20"
