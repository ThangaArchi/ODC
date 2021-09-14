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

import oem.edge.ed.odc.tunnel.common.URLConnection2;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URL;
import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (9/28/2004 1:28:50 PM)
 * @author: 
 */
public class CodeUpdater {
	public static int GOOD = 0;
	public static int UPDATE = 1;
	public static int FAIL = 2;
	static String bannerTop  = "************************************************************";
	static String bannerLine = "*                                                          *";
	private FileWriter log = null;
	private String urlHead;
	private SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private Date DATE = new Date();
/**
 * CodeUpdater constructor comment.
 */
public CodeUpdater(String urlHead) {
	super();
	this.urlHead = urlHead;
}
/**
 * Download the specified file to the specified directory.
 */
private void downloadFile(String fileName, File dir) throws Exception {
	log("download " + fileName + " to " + dir.toString());

	File file = new File(dir,fileName);
	FileOutputStream fileOut = null;
	long fs = 0;

	URLConnection2 conn = null;
	InputStream in = null;
	long length = 0;
	byte[] header = new byte[2];
	byte[] buffer = new byte[4096];

	// If the file already exists, we'll try to resume.
	if (file.exists() && file.isFile()) {
		log("file exists, attempting restart");

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

		log("file crc is " + crc);
		log("file size is " + fs);

		URL url = new URL(urlHead + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName + "?size=" + fs + "&crc=" + crc);
		conn = new URLConnection2(url);

		log("URL connected.");

		// setting the request property to post
		conn.setRequestProperty("method","POST");
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);

		log("post request sent");

		length = fs + conn.getContentLength();

		log("response content length is " + length);

		in = conn.getInputStream();

		if (in != null)
			log("got input stream");
		else
			log("no input stream available");

		int sz = in.read(header);

		if (sz == 2)
			log("response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
		else if (sz == 1)
			log("response is '" + (char) header[0] + "' or " + header[0]);
		else
			log("no response available");

		//check the header for success
		if (sz != 2 || header[0] != 'O' || header[1] != 'K') {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String error = rdr.readLine();

			log("error text is '" + error + "'");

			log("restart failed, retrying with no restart");

			url = new URL(urlHead + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName);
			conn = new URLConnection2(url);

			log("URL connected.");

			// setting the request property to post
			conn.setRequestProperty("method","POST");
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);

			log("post request sent");

			length = fs + conn.getContentLength();

			log("response content length is " + length);

			in = conn.getInputStream();

			if (in != null)
				log("got input stream");
			else
				log("no input stream available");

			sz = in.read(header);

			if (sz == 2)
				log("response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
			else if (sz == 1)
				log("response is '" + (char) header[0] + "' or " + header[0]);
			else
				log("no response available");

			// If server didn't respond or sent "NO" as its first 2 bytes of content...
			if (sz != 2 || header[0] != 'O' || header[1] != 'K') {
				rdr = new BufferedReader(new InputStreamReader(in));
				error = rdr.readLine();

				log("error text is '" + error + "'");

				log("servlet refused to download file");

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
		log("file does not exist");

		URL url = new URL(urlHead + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/" + fileName);
		conn = new URLConnection2(url);

		log("URL connected.");

		// setting the request property to post
		conn.setRequestProperty("method","POST");
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);

		log("post request sent");

		length = fs + conn.getContentLength();

		log("response content length is " + length);

		in = conn.getInputStream();

		if (in != null)
			log("got input stream");
		else
			log("no input stream available");

		int sz = in.read(header);

		if (sz == 2)
			log("response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
		else if (sz == 1)
			log("response is '" + (char) header[0] + "' or " + header[0]);
		else
			log("no response available");

		//check the header for success
		if (sz != 2 || header[0] != 'O' || header[1] != 'K') {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String error = rdr.readLine();

			log("error text is '" + error + "'");

			log("servlet refused to download file");

			throw new Exception("Servlet refused to download file:" + error);
		}

		fileOut = new FileOutputStream(file);
	}

	log("receiving data");

	int len;
	if (length > 0)
		length -= 2; // eliminate the 2 byte status.

	if (length > 0)
		emitPercent((int) (fs*100/length));
	else
		emitPercent(0);
	while ((len = in.read(buffer,0,4096)) != -1) {
		fileOut.write(buffer,0,len);
		fs += len;
		if (length > 0)
			emitPercent((int) (fs*100/length));
	}

	emitPercent(100);
	in.close();
	fileOut.close();

	log("done with file " + fileName);
}
/**
 * Insert the method's description here.
 * Creation date: (10/1/2004 2:01:52 PM)
 * @param msg java.lang.String
 */
public void emitDetailText(String msg) {
	System.out.println(msg);
}
/**
 * Insert the method's description here.
 * Creation date: (10/1/2004 2:01:52 PM)
 * @param msg java.lang.String
 */
public void emitFailureText(String title, String msg) {
	StringBuffer b = new StringBuffer(60);

	// Construct title line.
	b.append(bannerTop);
	b.setCharAt(3,' ');
	b.replace(4,4+title.length(),title);
	b.setCharAt(4+title.length(),' ');
	emitDetailText(b.toString());

	// Construct body.
	emitDetailText(bannerLine);

	int i = 0;
	while (i < msg.length()) {
		b.setLength(0);
		b.append(bannerLine);
		int j = msg.length() < i + 56 ? msg.length() : msg.lastIndexOf(' ',i+56) + 1;
		b.replace(2,2+j-i,msg.substring(i,j));
		i = j;
		emitDetailText(b.toString());
	}

	emitDetailText(bannerLine);
	emitDetailText(bannerTop);

	try {
		Thread.sleep(7000);
	} catch (Exception e) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/1/2004 2:01:52 PM)
 * @param msg java.lang.String
 */
public void emitPercent(int pct) {
}
/**
 * Insert the method's description here.
 * Creation date: (10/1/2004 2:01:52 PM)
 * @param msg java.lang.String
 */
public void emitStatusText(String msg) {
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 10:11:10 AM)
 * @return boolean
 * @param filename java.lang.String
 */
private boolean fileExists(String filename) {
	File file = new File(filename);

	if (! file.exists() || ! file.canRead()) {
		log(filename + " is not found or can not be read.");
		return false;
	}
	else {
		log(filename + " is found.");
		return true;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2004 1:32:49 PM)
 * @return oem.edge.ed.odc.applet.ConfigFile
 */
private ConfigFile loadVersionStamps() {
	ConfigFile versionStamps = null;

	// Go get the version information...
	try {
		log("loadVersionStamps: get DSCVersionStamps from server");

		URL url = new URL(urlHead + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/DSCVersionStamps");
		URLConnection2 conn = new URLConnection2(url);

		log("loadVersionStamps: URL connected.");

		// setting the request property to post
		conn.setRequestProperty("method","POST");
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setDefaultUseCaches(false);

		log("loadVersionStamps: post request sent");

		// Check connection status.
		InputStream in = conn.getInputStream();

		if (in != null)
			log("loadVersionStamps: got input stream");
		else
			log("loadVersionStamps: no input stream available");

		byte[] header = new byte[2];
		int sz = in.read(header);

		if (sz == 2)
			log("loadVersionStamps: response is '" + (char) header[0] + (char) header[1] + "' or " + header[0] + " and " + header[1]);
		else if (sz == 1)
			log("loadVersionStamps: response is '" + (char) header[0] + "' or " + header[0]);
		else
			log("loadVersionStamps: no response available");

		// If server didn't respond or sent "NO" as its first 2 bytes of content...
		if (sz != 2 || header[0] != 'O' || header[1] != 'K') {
			BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
			String error = rdr.readLine();

			log("loadVersionStamps: error text is '" + error + "'");

			log("loadVersionStamps: servlet refused to download file");

			throw new Exception("Servlet refused to download file:" + error);
		}

		log("loadVersionStamps: receiving data");

		versionStamps = new ConfigFile();
		versionStamps.load(in);

		log("loadVersionStamps: received DSCVersionStamps from server");
	}
	catch (Exception e) {
		log("loadVersionStamps: exception: " + e.getMessage());
	}

	return versionStamps;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2004 2:17:55 PM)
 * @param msg java.lang.String
 */
private void log(String msg) {
	try {
		// Haven't started logging yet?
		if (log == null) {
			// Define the log file.
			File updateLog = new File("update.log");

			// Get its initial size.
			long fs = updateLog.length();

			// If more than 20KB, then prune it back to less than 10KB.
			if (fs > 20480) {
				byte[] data = new byte[10240];
				FileInputStream in = new FileInputStream(updateLog);

				// Read in the last 10KB.
				in.skip(fs-10240);
				int i = in.read(data);
				in.close();

				// Read in 10KB?
				if (i == 10240) {
					// Find next line start and write out remaining.
					FileOutputStream out = new FileOutputStream("update.log",false);
					for (i = 0; i < 10240 && data[i] != '\n'; i++);
					i++;
					out.write(data,i,10240 - i);
					out.close();
				}
			}

			// Define the log writer.
			log = new FileWriter("update.log",true);
		}

		// Write the timestamp and message.
		DATE.setTime(System.currentTimeMillis());
		
		log.write(FORMAT.format(DATE));
		log.write(": ");	
		log.write(msg);
		log.write('\n');
		log.flush();
	}
	catch (IOException e) {
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/6/2004 9:36:43 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	if (args.length != 1) {
		System.out.println("CodeUpdater urlhead");
		System.exit(0);
	}

	CodeUpdater c = new CodeUpdater(args[0]);

	System.exit(c.update());
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2004 1:30:06 PM)
 * @return boolean
 */
public int update() {
	// Initial Setup
	boolean updatePending = false;
	String installed = null;
	String avail = null;

	// Prepare to check for system updates.
	emitStatusText("Checking for software updates.");
	emitDetailText("Checking for software updates.");
	log("Checking for software updates...");

	// Load version stamps.
	ConfigFile versions = loadVersionStamps();
	if (versions == null) {
		emitStatusText("Update failed: view details for more information!");
		emitFailureText("NOTE - NO Version Checking Possible",
			"The server DSCVersionStamps file was not obtainable. " +
			"Version checking is NOT possible! Software MAY be out of " +
			"date! Check update.log, located in the install point, " +
			"for more information");

		return FAIL;
	}

	// Load edesign.ini file.
	ConfigFile ini = new ConfigFile();
	try {
		ini.load("edesign.ini");
	}
	catch (IOException e) {
		emitStatusText("Update failed: view details for more information!");
		emitFailureText("NOTE - NO Version Checking Possible",
			"The edesign.ini file did not load. Version checking is " +
			"NOT possible! Software MAY be out of date! Check " +
			"update.log, located in the install point, for more information.");
		log("Unable to load edesign.ini file.");
		log("Exception: " + e.getMessage());

		return FAIL;
	}

	// Check the JVM.
	String plat[] = InstallAndLaunchApp.platform();

	emitStatusText("Validating the Java Runtime Environment...");
	emitDetailText("Validating the Java Runtime Environment...");
	log("Validating the Java Runtime Environment...");

	String jrePath = ini.getProperty(plat[1]+"JREPATH",null);
	if (jrePath != null) {
		int valid = validateJRE(jrePath,plat[1].equals("HPUX"));
		if (valid != 0) {
			if (valid == 1) {
				emitStatusText("JRE upgrade recommended!");
				emitFailureText("JRE - Update strongly recommended",
					"The Java Runtime Environment that you have selected is an " +
					"older version. Our software is no longer supported on " +
					"on this older JRE. While you may use the selected JRE, " +
					"some features of our software may not work and may be " +
					"unavailable. We strongly recommend that you use a version " +
					"1.5 JRE (Java 5) or higher.\n\n" +
					"For assistance, please launch this application from our " +
					"website as you had done initially.");
			}
			else {
				emitStatusText("Update failed: JRE upgrade required!");
				emitFailureText("JRE - Update required",
					"The Java Runtime Environment that you have selected is an " +
					"older version. Our software is no longer supported on " +
					"on this older JRE and will not operate. You are required " +
					"to upgrade the JRE. We strongly recommend that you use a " +
					"version 1.5 JRE (Java 5) or higher.\n\n" +
					"For assistance, please launch this application from our " +
					"website as you had done initially.");

				return FAIL;
			}
		}
	}

	// Load the updates edesign.ini, if any.
	File updates = new File("updates");
	File uIni = new File(updates,"edesign.ini.new");
	ConfigFile updateIni = new ConfigFile();
	if (uIni.exists()) {
		try {
			updateIni.load(uIni.getPath());
		}
		catch (IOException e) {
			log("could not load updates/edesign.ini.new, replacing.");
			updateIni = new ConfigFile();
		}
	}

	// The following updates may be needed:
	int updateDSC = 0;
	int updateSCRIPT = 0;
	int updateLDS = 0;
	int updateDSMP = 0;
	int updateLIB = 0;
	int updateLaunch = 0;

	// Determine if we need to update any DSC client software.
	if (fileExists(InstallAndLaunchApp.DSC_FILE)) {
		installed = updateIni.getProperty("DSCVER",null);
		if (installed == null) {
			installed = ini.getProperty("DSCVER",null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty("DSCVER",null);
		updateDSC = updateCheck("Client software for customer connect",avail,installed);
	}

	// Determine if we need to update the dropboxftp script file.
	if (fileExists(InstallAndLaunchApp.SCRIPT_FILE)) {
		installed = updateIni.getProperty("SCRIPTVER",null);
		if (installed == null) {
			installed = ini.getProperty("SCRIPTVER",null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty("SCRIPTVER",null);
		updateSCRIPT = updateCheck("Client software for customer connect - dropboxftp",avail,installed);
	}

	// Determine if we need to update the cmdline script file.
	if (fileExists(InstallAndLaunchApp.LDSSCRIPT_FILE)) {
		installed = updateIni.getProperty("LDSVER",null);
		if (installed == null) {
			installed = ini.getProperty("LDSVER",null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty("LDSVER",null);
		updateLDS = updateCheck("Client software for customer connect - cmdline",avail,installed);
	}

	// Determine if we need to update the web conference software.
	if (fileExists(InstallAndLaunchApp.DSMP_FILE)) {
		installed = updateIni.getProperty("DSMPVER",null);
		if (installed == null) {
			installed = ini.getProperty("DSMPVER",null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty("DSMPVER",null);
		updateDSMP = updateCheck("Client software for customer connect - DSMP",avail,installed);
	}

	// Determine if we need to update the scraper?
	String libName = null;
	String[] libNames = null;
	String libPropName = null;

	if (plat[1].equals("WIN")) {
		libNames = new String[2];
		libNames[0] = InstallAndLaunchApp.WIN1_LIB;
		libNames[1] = InstallAndLaunchApp.WIN2_LIB;
		libPropName = "LIBWINVER";
	}
	else if (plat[1].equals("AIX")) {
		libName = InstallAndLaunchApp.AIX_LIB;
		libPropName = "LIBAIXVER";
	}
	else if (plat[1].equals("SUNSP")) {
		libNames = new String[2];
		libNames[0] = InstallAndLaunchApp.SUN1_LIB;
		libNames[1] = InstallAndLaunchApp.SUN2_LIB;
		libPropName = "LIBSUNSPVER";
	}
	else if (plat[1].equals("LIN")) {
		libName = InstallAndLaunchApp.LIN_LIB;
		libPropName = "LIBLINVER";
	}
	else if (plat[1].equals("HPUX")) {
		libNames = new String[2];
		libNames[0] = InstallAndLaunchApp.HPUX1_LIB;
		libNames[1] = InstallAndLaunchApp.HPUX2_LIB;
		libPropName = "LIBHPUXVER";
	}

	if ((libName != null && fileExists(libName)) ||
		(libNames != null && (fileExists(libNames[0]) || fileExists(libNames[1])))) {
		installed = updateIni.getProperty(libPropName,null);
		if (installed == null) {
			installed = ini.getProperty(libPropName,null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty(libPropName,null);
		updateLIB = updateCheck("Client software for customer connect - Scraper",avail,installed);
	}

	// Determine if we need to update the launch script.
	String launchName = InstallAndLaunchApp.LUNIX_FILE;
	if (plat[1].equals("WIN")) {
		launchName = InstallAndLaunchApp.LWIN_FILE;
	}

	if (fileExists(launchName)) {
		installed = updateIni.getProperty("LAUNCHVER",null);
		if (installed == null) {
			installed = ini.getProperty("LAUNCHVER",null);
		}
		else {
			updatePending = true;
		}
		avail = versions.getProperty("LAUNCHVER",null);
		updateLaunch = updateCheck("Client software for customer connect - Client launcher",avail,installed);
	}

	// Nothing to update, just return false.
	if (updateDSC == 0 && updateSCRIPT == 0 && updateLDS == 0 &&
		updateDSMP == 0 && updateLIB == 0 && updateLaunch == 0) {
		if (updatePending) {
			emitStatusText("Update failed: view details for more information!");
			emitFailureText("NOTE - Installation of Updates Pending",
				"Previously downloaded updates have not been completely " +
				"applied. Likely, you need to close other instances of the " +
				"application to allow the updates to be applied.");
			log("Software updates are pending.");
			return UPDATE;
		}
		else {
			emitStatusText("No software updates available.\n");
			emitDetailText("No software updates available.\n");
			log("No software updates available.");
			return GOOD;
		}
	}

	// Create updates directory...
	if (updates.exists()) {
		if (! updates.isDirectory()) {
			emitStatusText("Update failed: view details for more information!");
			emitFailureText("NOTE - NO Installation of Updates Possible",
				"The updates directory could not be created in the install " +
				"point. A file by that name already exists. Your software " +
				"is out of date! Check update.log, located in the install " +
				"point, for more information.");
			log("can not create updates directory, a file exists by that name.");
			log(updates.getPath());
			return FAIL;
		}
	}
	else if (! updates.mkdir()) {
		emitStatusText("Update failed: view details for more information!");
		emitFailureText("NOTE - NO Installation of Updates Possible",
			"The updates directory could not be created in the install " +
			"point. A request to make directory failed. Your software " +
			"is out of date! Check update.log, located in the install " +
			"point, for more information.");
		log("can not create updates directory, mkdir failed.");
		log(updates.getPath());
		return FAIL;
	}

	// Download DSC updates.
	if (updateDSC != 0) {
		if (updateFile(InstallAndLaunchApp.DSC_FILE,"Client software for customer connect",updates)) {
			avail = versions.getProperty("DSCVER");
			updateIni.setProperty("DSCVER",avail);
		}
		else {
			return FAIL;
		}
	}

	// Download dropboxftp updates.
	if (updateSCRIPT != 0) {
		if (updateFile(InstallAndLaunchApp.SCRIPT_FILE,"Client software for customer connect - dropboxftp",updates)) {
			avail = versions.getProperty("SCRIPTVER");
			updateIni.setProperty("SCRIPTVER",avail);
		}
		else {
			return FAIL;
		}
	}

	// Download cmdline updates.
	if (updateLDS != 0) {
		if (updateFile(InstallAndLaunchApp.LDSSCRIPT_FILE,"Client software for customer connect - cmdline",updates)) {
			avail = versions.getProperty("LDSVER");
			updateIni.setProperty("LDSVER",avail);
		}
		else {
			return FAIL;
		}
	}

	// Download DSMP updates.
	if (updateDSMP != 0) {
		if (updateFile(InstallAndLaunchApp.DSMP_FILE,"Client software for customer connect - DSMP",updates)) {
			avail = versions.getProperty("DSMPVER");
			updateIni.setProperty("DSMPVER",avail);
		}
		else {
			return FAIL;
		}
	}

	// Download LIB updates.
	if (updateLIB != 0) {
		if (libName != null) {
			if (updateFile(libName,"Client software for customer connect - Scraper",updates)) {
				avail = versions.getProperty(libPropName);
				updateIni.setProperty(libPropName,avail);
			}
			else {
				return FAIL;
			}
		}
		else {
			if (updateFile(libNames[0],"Client software for customer connect - Scraper 1 of 2",updates) &&
				updateFile(libNames[1],"Client software for customer connect - Scraper 2 of 2",updates)) {
				avail = versions.getProperty(libPropName);
				updateIni.setProperty(libPropName,avail);
			}
			else {
				return FAIL;
			}
		}
	}

	// Download Launch updates.
	if (updateLaunch != 0) {
		if (updateFile(launchName,"Client software for customer connect - Client launcher",updates)) {
			avail = versions.getProperty("LAUNCHVER");
			updateIni.setProperty("LAUNCHVER",avail);
		}
		else {
			return FAIL;
		}
	}

	try {
		updateIni.store(uIni.getPath());
	}
	catch (IOException e) {
		emitStatusText("Update failed: view details for more information!");
		emitFailureText("NOTE - NO Installation of Updates Possible",
			"The update configuration file could not be saved. Your " +
			"software is out of date! Check update.log, located in the " +
			"install point, for more information.");
		log("Unable to save updates/edesign.ini.new file.");
		log(uIni.getPath());
		log("Exception: " + e.getMessage());

		return FAIL;
	}

	return UPDATE;
}
private boolean updateFile(String fileName,String compName,File dir) {
	try {
		emitStatusText("Download '" + compName + "'");
		emitDetailText("Download '" + compName + "'");
		downloadFile(fileName,dir);
		emitDetailText("Download completed.");
	}
	catch (Exception e) {
		emitStatusText("Update failed: view details for more information!");
		emitFailureText("NOTE - NO Installation of Updates Possible",
			"The update file could not be downloaded successfully. " +
			"Your software is out of date! Check update.log, located " +
			"in the install point, for more information.");
		log("download of " + fileName + " failed: " + e.getMessage());
		return false;
	}
	
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (04/05/01 2:09:38 PM)
 * @return boolean
 * @param avail java.lang.String
 * @param installed java.lang.String
 */
private int updateCheck(String compName, String avail, String installed) {
	if (avail == null || installed == null) {
		emitDetailText(compName);

		if (installed == null)
			emitDetailText("  Not installed.");
		else {
			emitDetailText("  Installed:  " + installed);
		}
		if (avail == null)
			emitDetailText("  Mandatory update.");
		else
			emitDetailText("  Mandatory update:  " + avail);

		if (installed == null)
			log(compName + ": update required (not installed)");
		else
			log(compName + ": update required");

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

	emitDetailText(compName);
	emitDetailText("  Installed:  " + installed);

	if (result > 0) {
		if (i < 4) result = 2;
	}
	else if (result < 0) {
		if (i < 4)
			result = 2;
		else
			result = 0;
	}

	if (result == 2) {
		emitDetailText("  Mandatory update:  " + avail);
		log(compName + ": update required " +  installed + " " + avail);
	}
	else if (result == 1) {
		emitDetailText("  Optional update available: " + avail);
	}
	else {
		emitDetailText("  No update needed.");
		log(compName + ": no update needed.");
	}

	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 11:10:18 AM)
 * @return boolean
 * @param path java.lang.String
 */
public int validateJRE(String path, boolean isHPUX) {
	int valid = 2;

	// Create the JavaVersionVerifier.class
	try {
		InputStream in = getClass().getResourceAsStream("/JavaVersionVerifier.class");
		File verify = new File("JavaVersionVerifier.class");
		FileOutputStream out = new FileOutputStream(verify);
		int b = in.read();
		while (b != -1) {
			out.write(b);
			b = in.read();
		}
		out.close();
	}
	catch (IOException e) {
		log("Failed to create JavaVersionVerifier.class, verification of JRE will fail.");
		return valid;
	}

	try {
		log("Validating JRE");
		String[] args = null;
		String[] env = null;

		if (path.endsWith("jre") || path.endsWith("jre.exe")) {
			args = new String[4];
			args[0] = path;
			args[1] = "-cp";
			args[2] = ".";
			args[3] = "JavaVersionVerifier";
		}
		else {
			args = new String[2];
			args[0] = path;
			args[1] = "JavaVersionVerifier";
			env = new String[1];
			env[0] = "CLASSPATH=.";
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
				log("No validation text available, waiting...");
				try {
					Thread.sleep(1000);
				}
				catch (Exception e1) {}
			}
		}

		byte[] b = new byte[in.available()];
		in.read(b);

		String text = new String(b);
		log("Validation text: " + text);
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
		log(e.getMessage());
	}

	// HPUX didn't validate, we'll try it with -green option.
	if (valid == 2 && isHPUX)
		return validateJREHPUX(path);

	if (valid == 0) {
		log("Successful Java 5+ validation.");
	}
	else if (valid == 1)
		log("Successful Java 1.3+ validation.");
	else
		log("Unsuccessful validation");

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

	try {
		log("Validating JRE with -green option");
		String[] args = null;
		String[] env = null;

		if (path.endsWith("jre") || path.endsWith("jre.exe")) {
			args = new String[5];
			args[0] = path;
			args[1] = "-green";
			args[2] = "-cp";
			args[3] = ".";
			args[4] = "JavaVersionVerifier";
		}
		else {
			args = new String[3];
			args[0] = path;
			args[1] = "-green";
			args[2] = "JavaVersionVerifier";
			env = new String[1];
			env[0] = "CLASSPATH=.";
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
		log("Validation text: " + text);
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
		log("Successful Java 5+ validation.");
	}
	else if (valid == 1)
		log("Successful Java 1.3+ validation.");
	else
		log("Unsuccessful validation");

	return valid;
}
}
