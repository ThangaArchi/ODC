package oem.edge.ed.sd.ff2db;

/*                    Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                         */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import oem.edge.ed.sd.util.*;

import com.ibm.mq.*;
import javax.mail.*;

import oem.edge.ed.sd.mq.MQSend;

import oem.edge.ed.sd.ordproc.Mailer;

//import oem.edge.ed.util.EDCMafsFile;  // for AFS authentication
import oem.edge.ed.util.PasswordUtils;

public class FlatFileToDB2Xfer {

	public final static String Copyright = "(C) Copyright IBM Corp. 2004, 2005";
	private static final boolean debug = false;

	private static final String mqDelimiter = "^\n";

	private static final SimpleDateFormat alertDate =
		new SimpleDateFormat("MM/dd/yyyy");

	static FlatFileToDB2Table ff2db;
	static MessageDisplay message;
	static FlatFileParser ffp;

	static ResourceBundle ff2dbRB, db2RB;

	boolean useMQ = true;
	Hashtable fileToTableHash, tableToCorrelIDHash;
	Hashtable techInfoFilesHaveChanged,
		techVerFilesHaveChanged,
		ipVerFilesHaveChanged;
	Hashtable fpdkFilesHaveChanged,
		dszFilesHaveChanged,
		xmxFilesHaveChanged,
		grmacFilesHaveChanged,
		efpgaFilesHaveChanged;
	String edHome, asicDKMailTo, mqRBName;
	Vector techInfoFiles, techVerFiles, techNamesAndVersions;
	Vector ipVerFiles, ipNamesAndVersions; //new for 5.1.1
	Vector fpdkFiles, dszFiles, xmxFiles, grmacFiles, efpgaFiles;

	boolean xfrPlatforms;

	//new for 3.7.1
	boolean xfrPlatformsPatch;
	boolean xfrPatchInfo;

	static final String FROM_ERROR = "eConnect@us.ibm.com";

	// Hard code ordering media options -- automate later
	static final int PREVIEW_MEDIA = 3;
	static final int DESIGN_MEDIA = 3;
	static final int DELTA_MEDIA = 2;
	static final int RAMRA_MEDIA = 2;
	static final int TOOL_MEDIA = 2;

	static String errMailTo = "fyuan@us.ibm.com";
	static String errMailCc = "";

	public FlatFileToDB2Xfer(String props[]) {

		mqRBName = props[2];

		try {
			ff2dbRB = ResourceBundle.getBundle(props[0]);
		} catch (java.util.MissingResourceException mre) {
			mre.printStackTrace();
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Fatal error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(mre));
			System.exit(-1);
		}

		try {
			db2RB = ResourceBundle.getBundle(props[1]);
		} catch (java.util.MissingResourceException mre) {
			mre.printStackTrace();
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Fatal error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(mre));
			System.exit(-1);
		}

		String cell, userid, pass, mq, HOME_DIR;

		asicDKMailTo = ff2dbRB.getString("asic_dk_mailto").trim();

		String tmpErrMailTo = ff2dbRB.getString("EDSD_DEV_1");
		String tmpErrMailCc = ff2dbRB.getString("EDSD_DEV_2");

		if (tmpErrMailTo != null && tmpErrMailTo.trim().length() != 0)
			errMailTo = tmpErrMailTo.trim();

		if (tmpErrMailCc != null && tmpErrMailCc.trim().length() != 0)
			errMailCc = tmpErrMailCc.trim();

		edHome = ff2dbRB.getString("edesign_home").trim();
		//  cell         = ff2dbRB.getString("cell").trim();
		HOME_DIR = ff2dbRB.getString("HOME_DIR").trim();
		//  userid       = ff2dbRB.getString("userid").trim();
		mq = ff2dbRB.getString("useMQ").trim();

		if (mq.equals("false"))
			useMQ = false;

		if (!edHome.endsWith("/"))
			edHome += "/";

		message =
			new MessageDisplay(
				ff2dbRB,
				"FlatFileToDB2Xfer",
				MessageDisplay.LOG_AND_DISPLAY,
				"ff2db");

		if (HOME_DIR == null || HOME_DIR.length() == 0)
			HOME_DIR = "";
		else if (!HOME_DIR.endsWith("/"))
			HOME_DIR += "/";

		// get AFS authentication
		//  EDCMafsFile authenticator = new EDCMafsFile();
		//   pass = PasswordUtils.getPassword(HOME_DIR + "."+cell);

		// message.displayMessage(cell + " " + userid + " " + pass,3);
		//   message.displayMessage(cell + " " + userid, 3);

		/*  if (!authenticator.afsAuthenticate(cell, userid, pass)) {
		     message.displayMessage("Could not get AFS authentication for: " + cell, 1);
		     System.exit(-1);
		     }*/

		alertDate.setLenient(false);

		ff2db = new FlatFileToDB2Table(db2RB, ff2dbRB);
		ffp = new FlatFileParser(message);
		ffp.parseTechReleaseVersionInfo(
			edHome + "TechInfo/last.release.data",
			edHome + "TechInfo/technology_version");

		initHashtables();
	}

	void lock() {
		File lockFile = new File(edHome + ".lock");
		if (lockFile.exists()) {
			message.displayMessage("Error: .lock file found.\nExiting.\n\n", 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Lock file found",
				"The .lock file was found in "
					+ edHome
					+ ".  If you get this message more than once, make sure the file "
					+ " is being properly deleted on exit.");
			System.exit(1);
		} else {
			/* It'd be nice to use the createNewFile() call, but we're only on Java 1.1.8 */
			try {
				FileWriter out = new FileWriter(lockFile);
				out.write(' ');
				out.close();
			} catch (java.io.IOException ioe) {
				message.displayMessage(
					"Error: Stacktrace: " + getStackTrace(ioe),
					1);
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"FlatFileToDB2Xfer: Fatal error",
					"FlatFileToDB2Xfer: Error: Stacktrace: "
						+ getStackTrace(ioe));
			}
		}

	}

	public void unlock() {
		File lockFile = new File(edHome + ".lock");
		lockFile.delete();
	}

	protected void finalize() {
		File lockFile = new File(edHome + ".lock");
		if (lockFile.exists())
			lockFile.delete();
	}

	public void initHashtables() {

		// All files which are to be tracked by this daemon

		// For files which reside in the TechInfo directory
		techInfoFiles = new Vector();

		techInfoFiles.addElement("last.release.data");
		techInfoFiles.addElement("customer.types");
		techInfoFiles.addElement("customer.list");
		techInfoFiles.addElement("halt_shipping");
		techInfoFiles.addElement("kit_info");
		techInfoFiles.addElement("all.releases.data"); // Trigger to read latest.release.data to send out New Release Alert.

		// For files which are Tech and Ver dependent
		techVerFiles = new Vector();

		techVerFiles.addElement("Readmes/PreviewKit");
		// Order of Readme files is important
		techVerFiles.addElement("Readmes/DesignKit");
		techVerFiles.addElement("Readmes/ToolKit");
		techVerFiles.addElement("Readmes/DeltaReleases");
		techVerFiles.addElement("ToolKit/platforms");
		techVerFiles.addElement("ProductDefinition/shipping.cores.data");

		// new for 2.9
		techVerFiles.addElement("ProductDefinition/shipping.tools.data");

		//new for 4.2.1
		techVerFiles.addElement("ProductDefinition/shipping.libgroups.data");

		// new for 2.9 fix-pack
		techVerFiles.addElement(
			"ProductDefinition/shipping.base_orderable.data");

		techVerFiles.addElement("ProductDefinition/shipping.customers.data");
		techVerFiles.addElement(
			"ProductDefinition/shipping.orderable.components");
		techVerFiles.addElement("DeltaReleases/delta_releases_list");
		techVerFiles.addElement("DeltaReleases/delta_packet_list");

		//new for 3.10.1
		techVerFiles.addElement("PKReleaseNote");
		techVerFiles.addElement("DKReleaseNote");
		//end of new for 3.10.1

		//new for 5.1.1, CU65
		ipVerFiles = new Vector();
		ipVerFiles.addElement(
			"ProductDefinition/iipmds_customer_ip_entitlements");
		ipVerFiles.addElement("ProductDefinition/iipmds_ipDef_data");
		//new for 5.4.1
		ipVerFiles.addElement("ProductDefinition/released.ip.revisions");
		//new for 6.1.1
		ipVerFiles.addElement("ToolKit/platforms");
		ipVerFiles.addElement("PKReleaseNote");
		ipVerFiles.addElement("Readmes/PreviewKit");
		// Order of Readme files is important
		ipVerFiles.addElement("Readmes/DesignKit");
		ipVerFiles.addElement("Readmes/ToolKit");
		//end of new for 5.1.1

		// For Tool Kit specific files (e.g. Full PD Kit, DieSizer, XMX, RAM/RA Compiler)

		fpdkFiles = new Vector();
		fpdkFiles.addElement("Readmes/FullPDKit");
		//   fpdkFiles.addElement("platforms");

		//new for 3.7.1
		fpdkFiles.addElement("platforms_patch");

		xmxFiles = new Vector();
		xmxFiles.addElement("Readmes/XMX");
		xmxFiles.addElement("shipping.xmx.data");

		dszFiles = new Vector();
		dszFiles.addElement("Readmes/DieSizer");
		dszFiles.addElement("shipping.diesizer.data");

		grmacFiles = new Vector();
		grmacFiles.addElement("Readmes/Help");
		grmacFiles.addElement("Readmes/FAQ");
		grmacFiles.addElement("Readmes/WhatsNew");
		grmacFiles.addElement("memdb.xml");

		//new for 4.5.1
		efpgaFiles = new Vector();
		efpgaFiles.addElement("efpga.xml");
		//end of new for 4.5.1

		// What files have changed ?
		// Initially, mark all files as not changed
		techInfoFilesHaveChanged = new Hashtable();
		techVerFilesHaveChanged = new Hashtable();

		//new for 5.1.1
		ipVerFilesHaveChanged = new Hashtable();
		//end of new for 5.1.1

		fpdkFilesHaveChanged = new Hashtable();
		xmxFilesHaveChanged = new Hashtable();
		dszFilesHaveChanged = new Hashtable();
		grmacFilesHaveChanged = new Hashtable();
		//new for 4.5.1
		efpgaFilesHaveChanged = new Hashtable();
		//end of new for 4.5.1

		int i, numFiles;

		numFiles = techInfoFiles.size();
		for (i = 0; i < numFiles; i++)
			techInfoFilesHaveChanged.put(
				(String) techInfoFiles.elementAt(i),
				new Boolean(false));

		numFiles = techVerFiles.size();
		for (i = 0; i < numFiles; i++)
			techVerFilesHaveChanged.put(
				(String) techVerFiles.elementAt(i),
				new Vector());

		//new for 5.1.1
		numFiles = ipVerFiles.size();
		for (i = 0; i < numFiles; i++)
			ipVerFilesHaveChanged.put(
				(String) ipVerFiles.elementAt(i),
				new Vector());
		//end of new for 5.1.1
		numFiles = fpdkFiles.size();
		for (i = 0; i < numFiles; i++) {
			fpdkFilesHaveChanged.put(
				(String) fpdkFiles.elementAt(i),
				new Boolean(false));
		}

		numFiles = xmxFiles.size();
		for (i = 0; i < numFiles; i++) {
			xmxFilesHaveChanged.put(
				(String) xmxFiles.elementAt(i),
				new Boolean(false));
		}

		numFiles = dszFiles.size();
		for (i = 0; i < numFiles; i++) {
			dszFilesHaveChanged.put(
				(String) dszFiles.elementAt(i),
				new Boolean(false));
		}

		numFiles = grmacFiles.size();
		for (i = 0; i < numFiles; i++) {
			grmacFilesHaveChanged.put(
				(String) grmacFiles.elementAt(i),
				new Boolean(false));
		}

		//new for 4.5.1
		numFiles = efpgaFiles.size();
		for (i = 0; i < numFiles; i++) {
			efpgaFilesHaveChanged.put(
				(String) efpgaFiles.elementAt(i),
				new Boolean(false));
		}
		//end of new for 4.5.1
		// List all DB2 tables to be populated
		// Associate flat files to DB2 tables
		// If any of these files change, the associated table must be updated

		Vector tables;
		fileToTableHash = new Hashtable();

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.TECHNOLOGY_VERSION_TABLE);
		fileToTableHash.put("last.release.data", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.CUSTOMER_TYPES_TABLE);
		fileToTableHash.put("customer.types", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.ASIC_CODENAME_TABLE);
		fileToTableHash.put("customer.list", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.HALT_SHIPPING_TABLE);
		fileToTableHash.put("halt_shipping", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.KIT_INFO_TABLE);
		fileToTableHash.put("kit_info", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE);
		fileToTableHash.put("Readmes/PreviewKit", tables);
		fileToTableHash.put("Readmes/DesignKit", tables);
		fileToTableHash.put("Readmes/ToolKit", tables);
		fileToTableHash.put("Readmes/DeltaReleases", tables);

		// For RAM/RA Compiler (in the Grmac directory)
		fileToTableHash.put("Readmes/Help", tables);
		fileToTableHash.put("Readmes/FAQ", tables);
		fileToTableHash.put("Readmes/WhatsNew", tables);
		fileToTableHash.put("memdb.xml", tables);
		//new for 4.5.1 For EFPGA (in the Efpga dir)
		fileToTableHash.put("efpga.xml", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.TOOL_DOCUMENT_TABLE);
		fileToTableHash.put("Readmes/FullPDKit", tables);

		// to be de-commented for 2.10
		// fileToTableHash.put("Readmes/XMX", tables);

		fileToTableHash.put("Readmes/DieSizer", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.PLATFORMS_TABLE);
		fileToTableHash.put("ToolKit/platforms", tables); // ToolKit
		//  fileToTableHash.put("platforms", tables);			// FullPDKit
		fileToTableHash.put("shipping.diesizer.data", tables); // DieSizer

		// new for 3.7.1
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.PLATFORMS_PATCH_TABLE);
		tables.addElement(FlatFileToDB2Table.PATCH_INFO_TABLE);
		fileToTableHash.put("platforms_patch", tables);

		// to be de-commented for 2.10
		// fileToTableHash.put("shipping.xmx.data", tables);		// XMX

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.DELTA_RELEASES_TABLE);
		tables.addElement(FlatFileToDB2Table.DELTA_RELEASE_MDL_TYPES_TABLE);
		tables.addElement(FlatFileToDB2Table.DELTA_RELEASE_LIB_GROUPS_TABLE);
		fileToTableHash.put("DeltaReleases/delta_releases_list", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.ORDERABLE_MDL_TYPES_TABLE);
		tables.addElement(FlatFileToDB2Table.ORDERABLE_LIB_GROUPS_TABLE);
		fileToTableHash.put(
			"ProductDefinition/shipping.orderable.components",
			tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.DELTA_PACKETS_TABLE);
		//new for 3.7.1
		tables.addElement(FlatFileToDB2Table.DELTA_RELEASE_NOTES_TABLE);
		//end of new for 3.7.1
		fileToTableHash.put("DeltaReleases/delta_packet_list", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.GA_CORES_TABLE);
		fileToTableHash.put("ProductDefinition/shipping.cores.data", tables);

		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.RESTRICTED_CORES_TABLE);
		tables.addElement(FlatFileToDB2Table.NON_STANDARD_DELIVERABLES_TABLE);
		tables.addElement(FlatFileToDB2Table.RESTRICTED_BASE_ORD_TABLE);
		//new for 5.1.1
		tables.addElement(FlatFileToDB2Table.DA_MODELTYPES_TABLE);
		//end of new for 5.1.1
		fileToTableHash.put(
			"ProductDefinition/shipping.customers.data",
			tables);

		// new for 2.9
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.NSD_GA_TABLE);
		//new for 3.10.1
		tables.addElement(FlatFileToDB2Table.PLATFORM_MDL_TYPES_TABLE);
		//end of new for 3.10.1
		//new for 4.2.1
		tables.addElement(FlatFileToDB2Table.PACKETS_MDL_TYPE_TABLE);
		//end of new for 4.2.1
		//new for 5.1.1
		tables.addElement(FlatFileToDB2Table.DA_MODELTYPES_TABLE);
		//end of new for 5.1.1
		fileToTableHash.put("ProductDefinition/shipping.tools.data", tables);

		// new for 2.9 fix-pack
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.GA_BASE_ORD_TABLE);
		fileToTableHash.put(
			"ProductDefinition/shipping.base_orderable.data",
			tables);

		//new for 3.10.1
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.RELEASE_NOTES_TABLE);
		fileToTableHash.put("PKReleaseNote", tables);
		fileToTableHash.put("DKReleaseNote", tables);
		//end of new for 3.10.1

		//new for 4.2.1
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.PACKETS_LIB_GROUP_TABLE);
		fileToTableHash.put(
			"ProductDefinition/shipping.libgroups.data",
			tables);
		//end of new for 4.2.1

		//new for 5.1.1 CU65
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.IP_ENTITLEMENT_TABLE);
		fileToTableHash.put(
			"ProductDefinition/iipmds_customer_ip_entitlements",
			tables);
		//end of new for 5.1.1
		//new for 5.4.1
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.IP_CONTENT_TABLE);
		fileToTableHash.put("ProductDefinition/iipmds_ipDef_data", tables);
		//end of new for 5.4.1
		//	   new for 6.1.1
		tables = new Vector();
		tables.addElement(FlatFileToDB2Table.REVISION_LIST_TABLE);
		tables.addElement(FlatFileToDB2Table.REVISION_RELNOTES_TABLE);
		fileToTableHash.put("ProductDefinition/released.ip.revisions", tables);
		//end of new for 6.1.1

		// MQ Correlation IDs used by the BTVtoBLDdb2TableXfer script

		tableToCorrelIDHash = new Hashtable();

		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DELTA_RELEASES_TABLE,
			"EDES_DELTA_REL");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DELTA_RELEASE_MDL_TYPES_TABLE,
			"EDES_DEL_REL_MDL");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DELTA_RELEASE_LIB_GROUPS_TABLE,
			"EDES_DEL_REL_LIB");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.ORDERABLE_MDL_TYPES_TABLE,
			"EDES_ORD_MDL_TYP");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.ORDERABLE_LIB_GROUPS_TABLE,
			"EDES_ORD_LIB_GRP");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DELTA_PACKETS_TABLE,
			"EDES_DEL_PACK");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.NON_STANDARD_DELIVERABLES_TABLE,
			"EDES_NSTD_DEL");

		// new for 2.9
		tableToCorrelIDHash.put(FlatFileToDB2Table.NSD_GA_TABLE, "EDES_NSD_GA");

		// new for 2.9 fix-pack
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.GA_BASE_ORD_TABLE,
			"EDES_GA_BASE_ORD");

		// new for 2.9 fix-pack
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.RESTRICTED_BASE_ORD_TABLE,
			"EDES_RES_BASE_ORD");

		tableToCorrelIDHash.put(
			FlatFileToDB2Table.RESTRICTED_CORES_TABLE,
			"EDES_RES_CORES");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.GA_CORES_TABLE,
			"EDES_GA_CORES");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.PLATFORMS_TABLE,
			"EDES_PLAT_CAT");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE,
			"EDES_TECH_DOC");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.TOOL_DOCUMENT_TABLE,
			"EDES_TOOL_DOC");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.ASIC_CODENAME_TABLE,
			"EDES_ASIC_CODE");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.CUSTOMER_TYPES_TABLE,
			"EDES_CUST_TYPES");

		tableToCorrelIDHash.put(
			FlatFileToDB2Table.HALT_SHIPPING_TABLE,
			"EDES_HALT_SHIP");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.KIT_INFO_TABLE,
			"EDES_KIT_INFO");

		tableToCorrelIDHash.put(
			FlatFileToDB2Table.TECHNOLOGY_VERSION_TABLE,
			"EDES_TECH_VER");

		// new for 3.7.1
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.PLATFORMS_PATCH_TABLE,
			"EDES_TOOLKIT_PATCH");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DELTA_RELEASE_NOTES_TABLE,
			"EDES_RELEASE_NOTES");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.PATCH_INFO_TABLE,
			"EDES_PATCH_INFO");
		// end of new for 3.7.1

		// new for 3.10.1
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.RELEASE_NOTES_TABLE,
			"EDES_MAJOR_RELEASE_NOTES");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.PLATFORM_MDL_TYPES_TABLE,
			"EDES_PLATFORM_MDL_TYPES");
		// end of new for 3.10.1

		//new for 5.1.1
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.IP_ENTITLEMENT_TABLE,
			"EDES_IP_ENTITLEMENT");
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.DA_MODELTYPES_TABLE,
			"EDES_DA_MODELTYPES");
		//end of new for 5.1.1

		//new for 5.4.1
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.IP_CONTENT_TABLE,
			"EDES_IP_CONTENT");
		//end of new for 5.4.1
		//new for 6.1.1
		tableToCorrelIDHash.put(
			FlatFileToDB2Table.REVISION_LIST_TABLE,
			"EDES_REVISION_LIST");
		tableToCorrelIDHash.put(
					FlatFileToDB2Table.REVISION_RELNOTES_TABLE,
					"EDES_REVISION_RELNOTES");
		//end for 6.1.1

	}

	public void determineTechNamesAndVersions() {

		String nameVersion;
		String type;
		techNamesAndVersions = new Vector();
		ipNamesAndVersions = new Vector(); //new for 5.1.1

		try {

			String line, name, version, junk;
			SimpleStringTokenizer tok;

			BufferedReader in =
				new BufferedReader(
					new FileReader(edHome + "TechInfo/technology_version"));

			while (in.ready()) {

				line = in.readLine().trim();
				tok = new SimpleStringTokenizer(line, ';');

				name = tok.nextToken();
				tok.nextToken();
				tok.nextToken();
				version = tok.nextToken();

				//new for 5.1.1
				tok.nextToken();
				tok.nextToken();
				tok.nextToken();
				type = tok.nextToken().trim();
				//end of new for 5.1.1

				nameVersion = name + "/" + version;
				//new for 5.1.1
				if (type.equalsIgnoreCase("IIPMDS")) {
					if (!stringAlreadyExists(nameVersion, ipNamesAndVersions))
						ipNamesAndVersions.addElement(nameVersion);
				} else {
					if (!stringAlreadyExists(nameVersion,
						techNamesAndVersions))
						techNamesAndVersions.addElement(nameVersion);
				}
				//end of new for 5.1.1
			}

		} catch (java.io.IOException ioe) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(ioe),
				1);
		} catch (java.util.NoSuchElementException nse) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(nse),
				1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Fatal error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(nse));
			System.exit(1);
		}
	}

	public void fillInHashtables() {

		checkTechInfoFiles();

		determineTechNamesAndVersions();

		// this might be empty if technology_version file wasn't found
		if (!techNamesAndVersions.isEmpty()) {
			checkTechVerFiles();
		}
		//new for 5.1.1
		if (!ipNamesAndVersions.isEmpty()) {
			checkIPVerFiles();
			
		}
		//end of new for 5.1.1
		// Any file in the Tools (FullPDKit, XMX, DieSizer, Grmac, Efpga) directory been updated ?
		checkToolkitFiles();

	}
	

	public void checkTechInfoFiles() {

		boolean update = false;
		StatusFile sf = new StatusFile(edHome + "TechInfo/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(techInfoFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "TechInfo/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}
		
		////////////  Rel7.1 Changes - Start ////
		//Get the keys in the status.file
		//If a new trigger has been added to the dir and it is not in the status.file, add it to status.file.
		Hashtable statusFileDetails = sf.getStatusFileDetails();
		//message.displayMessage("statusFileDetails = " + statusFileDetails, 1);
				
		Enumeration keys = statusFileDetails.keys();
    	String statusFileKeyPath = null;
    	while (keys.hasMoreElements()) {
    	    String key = (String) keys.nextElement();
    	    int keyIndex = key.lastIndexOf("/");
    	    statusFileKeyPath = key.substring(0,keyIndex + 1);
    	    break;
    	}
		
		/////////// 7.1 Changes - End /////
		basePath = edHome + "TechInfo/";

		int numFiles = techInfoFiles.size(), i;

		for (i = 0; i < numFiles; i++) {

			fileName = (String) techInfoFiles.elementAt(i);
			message.displayMessage("statusFileKeyPath + fileName  = " + statusFileKeyPath + fileName, 1);
			/////   7.1 //////
			//check if a new file has been added to the dir
			if( statusFileKeyPath != null && !statusFileDetails.containsKey(statusFileKeyPath + fileName) )
			{
				sf.addKeyToStatusFile( statusFileKeyPath + fileName );
				techInfoFilesHaveChanged.put(fileName, new Boolean(true));
				message.displayMessage("statusFileDetails after adding new status file = " + sf.getStatusFileDetails(), 1);
				update = true;
			}
			else if (sf.hasFileChanged(basePath + fileName)) {
				techInfoFilesHaveChanged.put(fileName, new Boolean(true));
				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);

				if (fileName.equals("customer.list")) {
					techInfoFilesHaveChanged.put(
						"customer.types",
						new Boolean(true));
					ffp.parseASICCodenameInfo(
						basePath + fileName,
						basePath + "asic_codename");
				}

			}
		}

		if (update)
		{
			sf.updateTechInfoStatusFile();
			checkFilesInStatusFile(sf);
		}
		message.displayMessage(" StatusFile log " + sf.getStatusFileLog(), 1);

	}

	public void checkTechVerFiles() {

		int numFiles = techVerFiles.size(), i, j;
		int numTVs = techNamesAndVersions.size();
		StatusFile sf;
		String nameVersion, currFile;
		Vector techsVec, sfVec = new Vector();

		for (i = 0; i < numTVs; i++) {
			sf =
				new StatusFile(
					edHome
						+ "ASICTECH/"
						+ (String) techNamesAndVersions.elementAt(i)
						+ "/");
			sfVec.addElement(sf);
			message.displayMessage(
				"Creating ststus file: "
					+ (String) techNamesAndVersions.elementAt(i));
		}

		for (i = 0; i < numFiles; i++) {
			currFile = (String) techVerFiles.elementAt(i);
			for (j = 0; j < numTVs; j++) {
				nameVersion = (String) techNamesAndVersions.elementAt(j);
				sf = (StatusFile) sfVec.elementAt(j);
				if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
					sf.initFile(techVerFiles);
				if (sf.getStatus() != StatusFile.STATUS_GOOD) {
					message.displayMessage(
						"Error: could not open / "
							+ "create status file: "
							+ edHome
							+ nameVersion
							+ "/status.file.",
						1);
					continue;
				}

				String filePath =
					edHome + "ASICTECH/" + nameVersion + "/" + currFile;

				if (sf.hasFileChanged(filePath)) {

					techsVec = (Vector) techVerFilesHaveChanged.get(currFile);

					if (techsVec == null)
						techsVec = new Vector();

					else if (
						!stringAlreadyExists(convertToReadableName(nameVersion),
						techsVec)) {
						techsVec.addElement(convertToReadableName(nameVersion));
						techVerFilesHaveChanged.put(currFile, techsVec);
						message.displayMessage("New: " + filePath, 1);
					}
				}
			}
		}

		for (i = 0; i < numTVs; i++) {
			sf = (StatusFile) sfVec.elementAt(i);
			message.displayMessage("Updating: " + sf.getFileName());
			sf.updateFile();
			checkFilesInStatusFile(sf);
		}

	}

	//new for 5.1.1
	public void checkIPVerFiles() {

		int numFiles = ipVerFiles.size(), i, j;
		int numTVs = ipNamesAndVersions.size();
		StatusFile sf;
		String nameVersion, currFile;
		Vector ipsVec, sfVec = new Vector();

		for (i = 0; i < numTVs; i++) {
			sf =
				new StatusFile(
					edHome
						+ "ASICTECH/"
						+ (String) ipNamesAndVersions.elementAt(i)
						+ "/");
			sfVec.addElement(sf);
			message.displayMessage(
				"Creating status file: "
					+ (String) ipNamesAndVersions.elementAt(i));
		}

		for (i = 0; i < numFiles; i++) {
			currFile = (String) ipVerFiles.elementAt(i);
			for (j = 0; j < numTVs; j++) {
				nameVersion = (String) ipNamesAndVersions.elementAt(j);
				sf = (StatusFile) sfVec.elementAt(j);
				if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
					sf.initFile(ipVerFiles);
				if (sf.getStatus() != StatusFile.STATUS_GOOD) {
					message.displayMessage(
						"Error: could not open / "
							+ "create status file: "
							+ edHome
							+ nameVersion
							+ "/status.file.",
						1);
					continue;
				}

				String filePath =
					edHome + "ASICTECH/" + nameVersion + "/" + currFile;
				
				
			
				if (sf.hasFileChanged(filePath)) {

					ipsVec = (Vector) ipVerFilesHaveChanged.get(currFile);

					if (ipsVec == null)
						ipsVec = new Vector();

					else if (
						!stringAlreadyExists(convertToReadableName(nameVersion),
						ipsVec)) {
						ipsVec.addElement(convertToReadableName(nameVersion));
						ipVerFilesHaveChanged.put(currFile, ipsVec);
						message.displayMessage("New: " + filePath, 1);
					}
				}
			}
		}

		for (i = 0; i < numTVs; i++) {
			sf = (StatusFile) sfVec.elementAt(i);
			message.displayMessage("Updating: " + sf.getFileName());
			sf.updateFile();
		 //   checkFilesInStatusFile(sf);
		}

	}
	//end of new for 5.1.1

	void checkReleaseNotes(String path) {

		File releaseNoteDir;
		String outputFile;
		Vector rnoteList;
		boolean rs = false;
		boolean doGenerate = false;
		try {

			releaseNoteDir = new File(path);

			outputFile = releaseNoteDir + "/release_note";
			String[] fileList = releaseNoteDir.list();
			rnoteList = new Vector();
			for (int j = 0; j < fileList.length; j++) { //for fileList

				if (fileList[j].endsWith("RNotes")) { //endswith

					doGenerate = true;

					rnoteList.addElement(fileList[j]);
					String tempname = releaseNoteDir + "/" + fileList[j];
					File tempFile = new File(tempname);
					String newname =
						releaseNoteDir + "/ReleaseNotes/" + fileList[j];
					File newFile = new File(newname);
					if (tempFile.renameTo(newFile)) {
						message.displayMessage(
							"Renamed " + tempname + " to " + newname,
							2);

					} else {
						String errMes =
							"Could not rename " + tempname + " to " + newname;
						message.displayMessage(errMes, 1);
						sendEmailMessage(
							FROM_ERROR,
							errMailTo,
							errMailCc,
							"FlatFileToDB2Xfer: Could not rename file",
							errMes);
					}

				}

			}
			if (doGenerate) {
				message.displayMessage(
					"new Release Notes available under " + path,
					1);
				rs = generateRNoteList(outputFile, rnoteList);
				if (rs) {
					message.displayMessage(outputFile, 1);
					ff2db.transferToReleaseNotesTable(new File(outputFile));
				}
			}
		} catch (Exception e) {
			message.displayMessage("Error: Stacktrace: " + getStackTrace(e), 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(e));
		}
	}

	void checkPatchInfoFiles() {

		boolean doGenerate = false;
		boolean rs = false;
		String fileName, basePath, outputFile;
		File PatchInfoDir;
		Vector patchinfoList;

		try {
			basePath = edHome + "FullPDKit/";

			PatchInfoDir = new File(basePath);
			outputFile = PatchInfoDir + "/patch_info";
			String[] fileList = PatchInfoDir.list();
			patchinfoList = new Vector();

			for (int j = 0; j < fileList.length; j++) { //for fileList

				if (fileList[j].endsWith("_file")) { //endswith

					doGenerate = true;

					patchinfoList.addElement(fileList[j]);
					String tempname = basePath + "/" + fileList[j];
					File tempFile = new File(tempname);
					String newname = PatchInfoDir + "/PatchInfo/" + fileList[j];
					File newFile = new File(newname);
					if (tempFile.renameTo(newFile)) {
						message.displayMessage(
							"Renamed " + tempname + " to " + newname,
							2);
					} else {
						String errMes =
							"Could not rename " + tempname + " to " + newname;
						message.displayMessage(errMes, 1);
						sendEmailMessage(
							FROM_ERROR,
							errMailTo,
							errMailCc,
							"FlatFileToDB2Xfer: Could not rename file",
							errMes);
					}
				}
			}

			if (doGenerate)
				rs = generatePatchInfoFile(outputFile, patchinfoList);

			if (rs) {

				ff2db.transferToPatchInfoTable(new File(outputFile));
				this.xfrPatchInfo = true;
			}

			// System.out.println("generate file successfully");
		} catch (Exception e) {
			message.displayMessage("Error: Stacktrace: " + getStackTrace(e), 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(e));
		}
	}

	void checkToolkitFiles() {

		checkFullPDKitFiles();

		// to be de-commented for 2.10
		// checkXMXFiles();

		checkDieSizerFiles();
		checkGrmacFiles();
		//new for 4.5.1
		checkEfpgaFiles();
	}

	void checkFullPDKitFiles() {

		boolean update = false;

		StatusFile sf = new StatusFile(edHome + "FullPDKit/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(fpdkFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "FullPDKit/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}

		basePath = edHome + "FullPDKit/";

		int numFiles = fpdkFiles.size(), i;
		for (i = 0; i < numFiles; i++) {
			fileName = (String) fpdkFiles.elementAt(i);
			if (sf.hasFileChanged(basePath + fileName)) {
				fpdkFilesHaveChanged.put(fileName, new Boolean(true));

				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);
			}
		}

		if (update)
			sf.updateFile();

		checkFilesInStatusFile(sf);
	}

	void checkXMXFiles() {

		boolean update = false;
		StatusFile sf = new StatusFile(edHome + "XMX/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(xmxFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "XMX/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}

		basePath = edHome + "XMX/";

		int numFiles = xmxFiles.size(), i;
		for (i = 0; i < numFiles; i++) {
			fileName = (String) xmxFiles.elementAt(i);
			if (sf.hasFileChanged(basePath + fileName)) {
				xmxFilesHaveChanged.put(fileName, new Boolean(true));
				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);
			}
		}

		if (update)
			sf.updateFile();

		checkFilesInStatusFile(sf);
	}

	void checkDieSizerFiles() {

		boolean update = false;
		StatusFile sf = new StatusFile(edHome + "DieSizer/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(dszFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "DieSizer/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}

		basePath = edHome + "DieSizer/";

		int numFiles = dszFiles.size(), i;
		for (i = 0; i < numFiles; i++) {
			fileName = (String) dszFiles.elementAt(i);
			if (sf.hasFileChanged(basePath + fileName)) {
				dszFilesHaveChanged.put(fileName, new Boolean(true));
				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);
			}
		}

		if (update)
			sf.updateFile();

		checkFilesInStatusFile(sf);
	}

	void checkGrmacFiles() {

		boolean update = false;
		StatusFile sf = new StatusFile(edHome + "Grmac/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(grmacFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "Grmac/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}

		basePath = edHome + "Grmac/";

		int numFiles = grmacFiles.size(), i;
		for (i = 0; i < numFiles; i++) {
			fileName = (String) grmacFiles.elementAt(i);
			if (sf.hasFileChanged(basePath + fileName)) {
				grmacFilesHaveChanged.put(fileName, new Boolean(true));
				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);
			}
		}

		if (update)
			sf.updateFile();

		checkFilesInStatusFile(sf);
	}

	//new for 4.5.1
	void checkEfpgaFiles() {

		boolean update = false;
		StatusFile sf = new StatusFile(edHome + "Efpga/");
		String fileName, basePath;

		if (sf.getStatus() == StatusFile.STATUS_FILE_NOT_FOUND)
			sf.initFile(efpgaFiles);

		if (sf.getStatus() != StatusFile.STATUS_GOOD) {
			String mes =
				"Error: could not open / create status "
					+ "file: "
					+ edHome
					+ "Efpga/status.file";
			message.displayMessage(mes, 1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: Error",
				mes);
		}

		basePath = edHome + "Efpga/";

		int numFiles = efpgaFiles.size(), i;
		for (i = 0; i < numFiles; i++) {
			fileName = (String) efpgaFiles.elementAt(i);
			if (sf.hasFileChanged(basePath + fileName)) {
				efpgaFilesHaveChanged.put(fileName, new Boolean(true));
				update = true;
				message.displayMessage("New: " + basePath + fileName, 1);
			}
		}

		if (update)
			sf.updateFile();

		checkFilesInStatusFile(sf);
	}
	//end of new for 4.5.1

	// if any files have timestamp 0, the errMailTo should know about it

	void checkFilesInStatusFile(StatusFile sf) {

		Vector badFiles = sf.getFilesWithZeroTimestamp();

		if (!badFiles.isEmpty()) {
			String mailfrom = ff2dbRB.getString("mailfrom").trim(), subject;

			subject = "The following files are missing from " + edHome + "\n";
			for (int i = 0; i < badFiles.size(); i++)
				subject += "\t" + (String) badFiles.elementAt(i) + "\n";

			message.displayMessage(subject, 1);
			sendEmailMessage(
				mailfrom,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer Error: Missing files",
				subject);
		}
	}

	public void updateFilesAndTransferTables() {

		// the order in which the following lines are changed is important
		updateAndTransferTechInfoFiles();
		updateAndTransferTechReadmeFiles();
		updateAndTransferToolkitFiles();
		updateAndTransferPlatformFiles();

	}

	void updateAndTransferPlatformFiles() {

		if (this.xfrPlatforms) {

			// Full PD Kit
			// No need to parse. platforms file is the source data.
			//  ff2db.transferToPlatformsTable(edHome + "FullPDKit/platforms");
			//  message.displayMessage("Updated: " + FlatFileToDB2Table.PLATFORMS_TABLE + " from " + edHome + "FullPDKit/platforms", 1);

			// DieSizer
			ffp.parseDieSizerInfo(
				edHome + "DieSizer/shipping.diesizer.data",
				edHome + "DieSizer/platforms");
			ff2db.transferToPlatformsTable(edHome + "DieSizer/platforms");
			message.displayMessage(
				"Updated: "
					+ FlatFileToDB2Table.PLATFORMS_TABLE
					+ " from "
					+ edHome
					+ "DieSizer/platforms",
				1);

			/* to be de-commented for 2.10
			// XMX
			ffp.parseXMXInfo(edHome + "XMX/shipping.xmx.data", edHome + "XMX/platforms");
			ff2db.transferToPlatformsTable(edHome + "XMX/platforms");
			message.displayMessage("Updated: " + FlatFileToDB2Table.PLATFORMS_TABLE + " from " + edHome + "XMX/platforms", 1);
			*/

			// Toolkits
			int numTechNames = techNamesAndVersions.size();
			for (int i = 0; i < numTechNames; i++) {

				String basePath =
					edHome
						+ "ASICTECH/"
						+ techNamesAndVersions.elementAt(i)
						+ "/";

				// no need to parse file as it is already in required format
				ff2db.transferToPlatformsTable(basePath + "ToolKit/platforms");
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.PLATFORMS_TABLE
						+ " from "
						+ basePath
						+ "ToolKit/platforms",
					1);
			}
			//new for 5.1.1
			int numIPNames = ipNamesAndVersions.size();
			for (int i = 0; i < numIPNames; i++) {

				String basePath =
					edHome
						+ "ASICTECH/"
						+ ipNamesAndVersions.elementAt(i)
						+ "/";
				ff2db.transferToPlatformsTable(basePath + "ToolKit/platforms");
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.PLATFORMS_TABLE
						+ " from "
						+ basePath
						+ "ToolKit/platforms",
					1);
			}
			//end of new for 5.1.1
		}

		//new for 3.7.1 -- platforms_patch file
		if (this.xfrPlatformsPatch) {
			ff2db.transferToPlatformsPatchTable(
				edHome + "FullPDKit/platforms_patch");
			message.displayMessage(
				"Updated: "
					+ FlatFileToDB2Table.PLATFORMS_PATCH_TABLE
					+ " from "
					+ edHome
					+ "FullPDKit/platforms_patch",
				1);

		}
		//always check PatchInfo description files in case there are new staged files without update platforms_patch file
		checkPatchInfoFiles();
		//end of new for 3.7.1

	}

	void updateAndTransferTechReadmeFiles() {

		boolean generated = false;
		int i, space;
		Enumeration enum;
		String name, fileName, val, techVerName, techName, verName, baseDir;
		Vector readMes = new Vector();
		Vector allChangedTechs = new Vector(), techs;
		Vector allChangedRN = new Vector();

		enum = techVerFilesHaveChanged.keys();

		while (enum.hasMoreElements()) {
			name = (String) enum.nextElement();
			if (name.indexOf("Readmes") >= 0) {

				techs = (Vector) techVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					for (i = 0; i < techs.size(); i++) {
						val = (String) techs.elementAt(i);
						if (!stringAlreadyExists(val, allChangedTechs))
							allChangedTechs.addElement(
								(String) techs.elementAt(i));
					}
				}
			}
			// new for 3.10.1
			else if (name.indexOf("ReleaseNote") >= 0) {

				techs = (Vector) techVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					for (i = 0; i < techs.size(); i++) {
						val = (String) techs.elementAt(i);
						if (!stringAlreadyExists(val, allChangedRN))
							allChangedRN.addElement(
								(String) techs.elementAt(i));
						message.displayMessage((String) techs.elementAt(i), 1);
					}
				}
			}
			// end of new for 3.10.1

		}
		//new for 5.1.1
		enum = ipVerFilesHaveChanged.keys();

		while (enum.hasMoreElements()) {
			name = (String) enum.nextElement();
			if (name.indexOf("Readmes") >= 0) {

				techs = (Vector) ipVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					for (i = 0; i < techs.size(); i++) {
						val = (String) techs.elementAt(i);
						if (!stringAlreadyExists(val, allChangedTechs))
							allChangedTechs.addElement(
								(String) techs.elementAt(i));
					}
				}
			} else if (name.indexOf("PKReleaseNote") >= 0) {

				techs = (Vector) ipVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					for (i = 0; i < techs.size(); i++) {
						val = (String) techs.elementAt(i);
						if (!stringAlreadyExists(val, allChangedRN))
							allChangedRN.addElement(
								(String) techs.elementAt(i));
						message.displayMessage((String) techs.elementAt(i), 1);
					}
				}
			}
			// end of new for 3.10.1

		}
		//end of new for 5.1.1

		if (allChangedTechs.isEmpty() && allChangedRN.isEmpty()) {
			return;
		}

		if (!allChangedTechs.isEmpty()) {
			for (i = 0; i < techVerFiles.size(); i++) {

				name = (String) techVerFiles.elementAt(i);
				if (name.startsWith("Readmes")) {
					fileName = name.substring(name.lastIndexOf('/') + 1);
					readMes.addElement(fileName);
				}
			}

			for (i = 0; i < allChangedTechs.size(); i++) {
				techVerName = (String) allChangedTechs.elementAt(i);
				space = techVerName.indexOf(" ");
				techName = techVerName.substring(0, space);
				verName =
					techVerName.substring(space + 1, techVerName.length());

				baseDir = edHome + "ASICTECH/" + techName + "/" + verName + "/";
				generated =
					generateTechnologyDocument(
						techName,
						verName,
						baseDir + "technology_document",
						readMes);
				if (generated) {
					ff2db.transferToTechnologyDocumentTable(
						new File(baseDir + "technology_document"));
					message.displayMessage(
						"Updated: "
							+ FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE
							+ " from "
							+ baseDir
							+ "technology_document",
						1);
				}
			}
		}

		//new for 3.10.1
		if (allChangedRN.isEmpty()) {
			return;
		}

		for (i = 0; i < allChangedRN.size(); i++) {
			techVerName = (String) allChangedRN.elementAt(i);
			space = techVerName.indexOf(" ");
			techName = techVerName.substring(0, space);
			verName = techVerName.substring(space + 1, techVerName.length());
			baseDir = edHome + "ASICTECH/" + techName + "/" + verName + "/";
			ff2db.transferToMajorReleaseNoteTable(techName, verName, baseDir);
			message.displayMessage(
				"Updated: "
					+ FlatFileToDB2Table.RELEASE_NOTES_TABLE
					+ " from "
					+ baseDir,
				1);

		}
		// end of new for 3.10.1         

	}

	void updateAndTransferToolkitFiles() {
		updateAndTransferGrmacFiles();
		uptdateAndTransferFullPDKitFiles();
		updateAndTransferDieSizerFiles();
		//new for 4.5.1
		updateAndTransferEfpgaFiles();
	}

	void uptdateAndTransferFullPDKitFiles() {

		Boolean changed;
		boolean generated;
		boolean xfrReadmes = false;
		Enumeration enum = fpdkFilesHaveChanged.keys();
		String fileName, basePath, name;
		Vector readMes;

		int numToolkitFiles = fpdkFiles.size(), i;
		basePath = edHome + "FullPDKit/";

		readMes = new Vector();

		for (i = 0; i < numToolkitFiles; i++) {

			name = (String) fpdkFiles.elementAt(i);
			changed = (Boolean) fpdkFilesHaveChanged.get(name);

			if (name.startsWith("Readmes")) { // Readme files only

				fileName = name.substring(name.lastIndexOf('/') + 1);
				readMes.addElement(fileName);

				if (changed.booleanValue())
					xfrReadmes = true;
			}

			if (name.equals("platforms") && changed.booleanValue())
				this.xfrPlatforms = true;
			// new for 3.7.1
			if (name.equals("platforms_patch") && changed.booleanValue())
				this.xfrPlatformsPatch = true;
		}

		if (xfrReadmes) {

			/** Don't uncomment
			generated = generateTechnologyDocument
				("FULL_PDK", "v1.0", basePath + "technology_document", readMes);
			
			if (generated) {
			    ff2db.transferToTechnologyDocumentTable
					(new File(basePath + "technology_document"));
			message.displayMessage("Updated: " + FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE + " from " + basePath + "technology_document", 1);
			}
			**/

			generated =
				generateToolDocument(
					"FULL_PDK",
					basePath + "tool_document",
					readMes);

			if (generated) {
				ff2db.transferToToolDocumentTable(
					new File(basePath + "tool_document"));
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.TOOL_DOCUMENT_TABLE
						+ " from "
						+ basePath
						+ "tool_document",
					1);
			}
		}

	}

	void updateAndTransferGrmacFiles() {

		boolean generated, xfrGrmac = false;
		Boolean changed;
		Enumeration enum = grmacFilesHaveChanged.keys();
		String fileName, basePath, name;
		Vector readMes;

		int numToolkitFiles = grmacFiles.size(), i;
		basePath = edHome + "Grmac/";

		readMes = new Vector();
		for (i = 0; i < numToolkitFiles; i++) {

			name = (String) grmacFiles.elementAt(i);
			changed = (Boolean) grmacFilesHaveChanged.get(name);
			if (changed.booleanValue())
				xfrGrmac = true;

			fileName = name.substring(name.lastIndexOf('/') + 1);
			readMes.addElement(fileName);
		}

		if (xfrGrmac) {

			generated =
				generateTechnologyDocument(
					"RAMRA",
					"v1.0",
					basePath + "technology_document",
					readMes);

			if (generated) {
				ff2db.transferToTechnologyDocumentTable(
					new File(basePath + "technology_document"));
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE
						+ " from "
						+ basePath
						+ "technology_document",
					1);
			}
		}
	}

	//new for 4.5.1
	void updateAndTransferEfpgaFiles() {

		boolean generated, xfrEfpga = false;
		Boolean changed;
		Enumeration enum = efpgaFilesHaveChanged.keys();
		String fileName, basePath, name;
		Vector readMes;

		int numToolkitFiles = efpgaFiles.size(), i;
		basePath = edHome + "Efpga/";

		readMes = new Vector();
		message.displayMessage("inside updateandtransferefpga", 1);
		for (i = 0; i < numToolkitFiles; i++) {

			name = (String) efpgaFiles.elementAt(i);
			message.displayMessage("name " + name, 1);
			changed = (Boolean) efpgaFilesHaveChanged.get(name);
			if (changed.booleanValue())
				xfrEfpga = true;
			fileName = name.substring(name.lastIndexOf('/') + 1);
			message.displayMessage("fileName " + fileName + " " + xfrEfpga, 1);
			readMes.addElement(fileName);

		}

		if (xfrEfpga) {

			generated =
				generateTechnologyDocument(
					"EFPGA",
					"v1.0",
					basePath + "technology_document",
					readMes);

			if (generated) {
				ff2db.transferToTechnologyDocumentTable(
					new File(basePath + "technology_document"));
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE
						+ " from "
						+ basePath
						+ "technology_document",
					1);
			}
		}
	}
	//end of new for 4.5.1

	void updateAndTransferXMXFiles() {

		boolean generated, xfrReadmes = false;
		Boolean changed;
		Enumeration enum = xmxFilesHaveChanged.keys();
		String fileName, basePath, name;
		Vector readMes;

		int numToolkitFiles = xmxFiles.size(), i;
		basePath = edHome + "XMX/";

		readMes = new Vector();

		for (i = 0; i < numToolkitFiles; i++) {

			name = (String) xmxFiles.elementAt(i);
			changed = (Boolean) xmxFilesHaveChanged.get(name);

			if (name.startsWith("Readmes")) { // Readme files only

				fileName = name.substring(name.lastIndexOf('/') + 1);
				readMes.addElement(fileName);

				if (changed.booleanValue())
					xfrReadmes = true;
			}

			if (name.equals("shipping.xmx.data") && changed.booleanValue())
				this.xfrPlatforms = true;
		}

		if (xfrReadmes) {

			generated =
				generateToolDocument(
					"XMX",
					basePath + "tool_document",
					readMes);

			if (generated) {
				ff2db.transferToToolDocumentTable(
					new File(basePath + "tool_document"));
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.TOOL_DOCUMENT_TABLE
						+ " from "
						+ basePath
						+ "tool_document",
					1);
			}
		}

	}

	void updateAndTransferDieSizerFiles() {

		boolean generated, xfrReadmes = false;
		Boolean changed;
		Enumeration enum = dszFilesHaveChanged.keys();
		String fileName, basePath, name;
		Vector readMes;

		int numToolkitFiles = dszFiles.size(), i;
		basePath = edHome + "DieSizer/";

		readMes = new Vector();

		for (i = 0; i < numToolkitFiles; i++) {

			name = (String) dszFiles.elementAt(i);
			changed = (Boolean) dszFilesHaveChanged.get(name);

			if (name.startsWith("Readmes")) { // Readme files only

				fileName = name.substring(name.lastIndexOf('/') + 1);
				readMes.addElement(fileName);

				if (changed.booleanValue())
					xfrReadmes = true;
			}

			if (name.equals("shipping.diesizer.data")
				&& changed.booleanValue())
				this.xfrPlatforms = true;
		}

		if (xfrReadmes) {

			generated =
				generateToolDocument(
					"DIESIZER",
					basePath + "tool_document",
					readMes);

			if (generated) {
				ff2db.transferToToolDocumentTable(
					new File(basePath + "tool_document"));
				message.displayMessage(
					"Updated: "
						+ FlatFileToDB2Table.TOOL_DOCUMENT_TABLE
						+ " from "
						+ basePath
						+ "tool_document",
					1);
			}
		}

	}

	private String getMktName(String technology) {

		String mktName = (String) FlatFileParser.techMktName.get(technology);

		if (mktName == null)
			return technology;
		else
			return mktName;

	}

	String quoteStr(String s) {

		return "\"" + s + "\", ";

	}

	String getMimeType(String filename) {

		if (filename != null
			&& (filename.endsWith(".pdf") || filename.endsWith(".PDF")))
			return "PDF";

		else
			return "HTML";

	}

	String getDocString(String filename) {

		return quoteStr(filename) + quoteStr("Y");

	}

	String getDummyDocString() {

		return quoteStr("DUMMY") + quoteStr("N");

	}

	String getDummyDocString(boolean size, boolean media) {

		StringBuffer sb = new StringBuffer();

		sb.append(quoteStr("DUMMY"));

		if (size)
			sb.append("0, ");

		sb.append(quoteStr("HTML"));

		if (media)
			sb.append("0, ");

		return sb.toString();

	}

	String getDocString(
		String directory,
		String filename,
		int mediaType,
		boolean size,
		boolean media) {

		StringBuffer sb = new StringBuffer();

		File f = new File(directory + filename);

		File canonicalFile = null;

		try {
			canonicalFile = new File(f.getCanonicalPath());
		} catch (IOException e) {
			canonicalFile = f;
		}

		String abbFileName = canonicalFile.getName();

		long sizeInfo = canonicalFile.length() / 1024;

		sb.append(quoteStr(filename));

		if (size)
			sb.append(sizeInfo + ", ");

		sb.append(quoteStr(getMimeType(abbFileName)));

		if (media)
			sb.append(mediaType + ", ");

		return sb.toString();

	}

	boolean generateTechnologyDocument(
		String techName,
		String techVersion,
		String outFileName,
		Vector readMes) {

		StringBuffer line = new StringBuffer("\"");
		int i;

		try {
			BufferedWriter out =
				new BufferedWriter(new FileWriter(outFileName));

			String readmesDir = new File(outFileName).getParent() + "/Readmes/";

			line.append(getMktName(techName));
			line.append("\",\"");
			line.append(techVersion);
			line.append("\",");

			if (techName.equals("RAMRA"))
				line.append(
					getDocString(
						readmesDir,
						(String) readMes.elementAt(0),
						0,
						true,
						true)
						+ getDocString(
							readmesDir,
							(String) readMes.elementAt(1),
							0,
							false,
							true)
						+ getDummyDocString(false, false)
						+ getDocString(
							readmesDir,
							(String) readMes.elementAt(2),
							0,
							false,
							true)
						+ getDocString(readmesDir, "RR.HTML", 2, true, true)
						+ getDocString((String) readMes.elementAt(3)));
			//new for 4.5.1
			else if (techName.equals("EFPGA"))
				line.append(
					getDummyDocString(true, true)
						+ getDummyDocString(false, true)
						+ getDummyDocString(false, false)
						+ getDummyDocString(false, true)
						+ getDummyDocString(true, true)
						+ getDocString((String) readMes.elementAt(0)));
			//end of new for 4.5.1

			else {

				int[] mediaOptions = null;
				if (new File(readmesDir + "media.options").isFile())
					mediaOptions =
						readMediaOptions(readmesDir + "media.options");
				if (mediaOptions == null)
					//new for 3.7.1
					mediaOptions = new int[] { 2, 2, 2 };
				// mediaOptions = new int[] {3, 3, 2}; comment out for 3.7.1

				line.append(
					getDocString(
						readmesDir,
						(String) readMes.elementAt(0),
						mediaOptions[0],
						true,
						true)
						+ getDocString(
							readmesDir,
							(String) readMes.elementAt(1),
							mediaOptions[1],
							false,
							true)
						+ getDocString(
							readmesDir,
							(String) readMes.elementAt(2),
							0,
							false,
							false)
						+ getDocString(
							readmesDir,
							(String) readMes.elementAt(3),
							mediaOptions[2],
							false,
							true)
						+ getDummyDocString(true, true)
						+ getDummyDocString());

			}

			line.append("\"A\",\"");

			// Append username and timestamp

			line.append(System.getProperty("user.name"));
			line.append("\",\"");

			Calendar cal = Calendar.getInstance();
			StringBuffer timeDate = new StringBuffer();
			int month, day, hour, min, sec, milli;
			timeDate.append(cal.get(Calendar.YEAR));
			timeDate.append("-");
			month = cal.get(Calendar.MONTH) + 1;
			if (month < 10)
				timeDate.append("0");
			timeDate.append(month);
			timeDate.append("-");

			day = cal.get(Calendar.DATE);
			if (day < 10)
				timeDate.append("0");
			timeDate.append(day);
			timeDate.append("-");

			hour = cal.get(Calendar.HOUR);
			if (cal.get(Calendar.AM_PM) == Calendar.PM)
				hour += 12;

			else if (hour < 10)
				timeDate.append("0");

			timeDate.append(hour);
			timeDate.append(".");

			min = cal.get(Calendar.MINUTE);
			if (min < 10)
				timeDate.append("0");
			timeDate.append(min);
			timeDate.append(".");

			sec = cal.get(Calendar.SECOND);
			if (sec < 10)
				timeDate.append("0");
			timeDate.append(sec);
			timeDate.append(".");

			milli = cal.get(Calendar.MILLISECOND);
			if (milli < 10)
				timeDate.append("00");
			else if (milli < 100)
				timeDate.append("0");
			timeDate.append(milli);
			timeDate.append("000");

			line.append(timeDate.toString());
			line.append("\"");

			out.write(line.toString());
			out.close();
		} catch (java.io.IOException ioe) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(ioe),
				1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(ioe));
			return false;
		}

		message.displayMessage("Wrote " + outFileName, 2);
		return true;
	}

	boolean generateRNoteList(String outFileName, Vector RNoteList) {

		StringBuffer line = null;
		int i;
		int numRN = RNoteList.size();

		try {
			BufferedWriter out =
				new BufferedWriter(new FileWriter(outFileName));

			for (i = 0; i < numRN; i++) { //for loop
				line = new StringBuffer("\"");
				//assumption : release notes name is named by replacing packet name's tar.gz with "RNotes"
				String packet_name = (String) RNoteList.elementAt(i);
				packet_name =
					packet_name.substring(0, packet_name.indexOf("RNote"));
				line.append(packet_name + "tar.gz");
				System.out.println(packet_name);
				line.append("\",\"");
				line.append(RNoteList.elementAt(i));
				line.append("\",");
				line.append("\"A\",\"");

				// Append username and timestamp

				line.append(System.getProperty("user.name"));
				System.out.println(System.getProperty("user.name"));
				line.append("\",\"");

				Calendar cal = Calendar.getInstance();
				StringBuffer timeDate = new StringBuffer();
				int month, day, hour, min, sec, milli;
				timeDate.append(cal.get(Calendar.YEAR));
				timeDate.append("-");
				month = cal.get(Calendar.MONTH) + 1;
				if (month < 10)
					timeDate.append("0");
				timeDate.append(month);
				timeDate.append("-");

				day = cal.get(Calendar.DATE);
				if (day < 10)
					timeDate.append("0");
				timeDate.append(day);
				timeDate.append("-");

				hour = cal.get(Calendar.HOUR);
				if (cal.get(Calendar.AM_PM) == Calendar.PM)
					hour += 12;

				else if (hour < 10)
					timeDate.append("0");

				timeDate.append(hour);
				timeDate.append(".");

				min = cal.get(Calendar.MINUTE);
				if (min < 10)
					timeDate.append("0");
				timeDate.append(min);
				timeDate.append(".");

				sec = cal.get(Calendar.SECOND);
				if (sec < 10)
					timeDate.append("0");
				timeDate.append(sec);
				timeDate.append(".");

				milli = cal.get(Calendar.MILLISECOND);
				if (milli < 10)
					timeDate.append("00");
				else if (milli < 100)
					timeDate.append("0");
				timeDate.append(milli);
				timeDate.append("000");

				line.append(timeDate.toString());
				System.out.println(timeDate.toString());
				line.append("\"");
				System.out.println(line.toString());
				out.write(line.toString());
				out.newLine();
			}
			out.close();
		} catch (java.io.IOException ioe) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(ioe),
				1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(ioe));
			return false;
		}

		message.displayMessage("Wrote " + outFileName, 2);
		return true;
	}

	boolean generatePatchInfoFile(String outFileName, Vector FileList) {

		StringBuffer line = null;
		int i;
		int numRN = FileList.size();

		try {
			BufferedWriter out =
				new BufferedWriter(new FileWriter(outFileName));

			for (i = 0; i < numRN; i++) { //for loop
				line = new StringBuffer("\"");
				line.append(FileList.elementAt(i));
				System.out.println(FileList.elementAt(i));
				line.append("\",\"");
				line.append(FileList.elementAt(i));
				line.append("\",");
				line.append("\"A\",\"");

				// Append username and timestamp

				line.append(System.getProperty("user.name"));
				System.out.println(System.getProperty("user.name"));
				line.append("\",\"");

				Calendar cal = Calendar.getInstance();
				StringBuffer timeDate = new StringBuffer();
				int month, day, hour, min, sec, milli;
				timeDate.append(cal.get(Calendar.YEAR));
				timeDate.append("-");
				month = cal.get(Calendar.MONTH) + 1;
				if (month < 10)
					timeDate.append("0");
				timeDate.append(month);
				timeDate.append("-");

				day = cal.get(Calendar.DATE);
				if (day < 10)
					timeDate.append("0");
				timeDate.append(day);
				timeDate.append("-");

				hour = cal.get(Calendar.HOUR);
				if (cal.get(Calendar.AM_PM) == Calendar.PM)
					hour += 12;

				else if (hour < 10)
					timeDate.append("0");

				timeDate.append(hour);
				timeDate.append(".");

				min = cal.get(Calendar.MINUTE);
				if (min < 10)
					timeDate.append("0");
				timeDate.append(min);
				timeDate.append(".");

				sec = cal.get(Calendar.SECOND);
				if (sec < 10)
					timeDate.append("0");
				timeDate.append(sec);
				timeDate.append(".");

				milli = cal.get(Calendar.MILLISECOND);
				if (milli < 10)
					timeDate.append("00");
				else if (milli < 100)
					timeDate.append("0");
				timeDate.append(milli);
				timeDate.append("000");

				line.append(timeDate.toString());
				System.out.println(timeDate.toString());
				line.append("\"");
				System.out.println(line.toString());
				out.write(line.toString());
				out.newLine();
			}
			out.close();
		} catch (java.io.IOException ioe) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(ioe),
				1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(ioe));
			return false;
		}

		message.displayMessage("Wrote " + outFileName, 2);
		return true;
	}

	private int[] readMediaOptions(String inputFile) {

		try {
			String contents = readFile(inputFile);
			StringTokenizer st = new StringTokenizer(contents, ",");
			int designKitMedia = Integer.parseInt(st.nextToken());
			int previewKitMedia = Integer.parseInt(st.nextToken());
			int deltaReleaseMedia = Integer.parseInt(st.nextToken());
			return new int[] {
				designKitMedia,
				previewKitMedia,
				deltaReleaseMedia };
		} catch (Exception e) {
			return null;
		}

	}

	boolean generateToolDocument(
		String techName,
		String outFileName,
		Vector readMes) {

		StringBuffer line = new StringBuffer("\"");
		int i;

		try {
			BufferedWriter out =
				new BufferedWriter(new FileWriter(outFileName));

			String readmesDir = new File(outFileName).getParent() + "/Readmes/";

			line.append(getMktName(techName));
			line.append("\",");

			if (techName.equals("FULL_PDK")
				|| techName.equals("DIESIZER")
				|| techName.equals("XMX"))
				line.append(
					getDocString(
						readmesDir,
						(String) readMes.elementAt(0),
						2,
						true,
						true));

			line.append("\"A\",\"");

			// Append username and timestamp

			line.append(System.getProperty("user.name"));
			line.append("\",\"");

			Calendar cal = Calendar.getInstance();
			StringBuffer timeDate = new StringBuffer();
			int month, day, hour, min, sec, milli;
			timeDate.append(cal.get(Calendar.YEAR));
			timeDate.append("-");
			month = cal.get(Calendar.MONTH) + 1;
			if (month < 10)
				timeDate.append("0");
			timeDate.append(month);
			timeDate.append("-");

			day = cal.get(Calendar.DATE);
			if (day < 10)
				timeDate.append("0");
			timeDate.append(day);
			timeDate.append("-");

			hour = cal.get(Calendar.HOUR);
			if (cal.get(Calendar.AM_PM) == Calendar.PM)
				hour += 12;

			else if (hour < 10)
				timeDate.append("0");

			timeDate.append(hour);
			timeDate.append(".");

			min = cal.get(Calendar.MINUTE);
			if (min < 10)
				timeDate.append("0");
			timeDate.append(min);
			timeDate.append(".");

			sec = cal.get(Calendar.SECOND);
			if (sec < 10)
				timeDate.append("0");
			timeDate.append(sec);
			timeDate.append(".");

			milli = cal.get(Calendar.MILLISECOND);
			if (milli < 10)
				timeDate.append("00");
			else if (milli < 100)
				timeDate.append("0");
			timeDate.append(milli);
			timeDate.append("000");

			line.append(timeDate.toString());
			line.append("\"");

			out.write(line.toString());
			out.close();
		} catch (java.io.IOException ioe) {
			message.displayMessage(
				"Error: Stacktrace: " + getStackTrace(ioe),
				1);
			sendEmailMessage(
				FROM_ERROR,
				errMailTo,
				errMailCc,
				"FlatFileToDB2Xfer: error",
				"FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(ioe));
			return false;
		}

		message.displayMessage("Wrote " + outFileName, 2);
		return true;
	}

	void updateAndTransferTechInfoFiles() {

		Boolean changed;
		Enumeration enum;
		String fileName,
			key,
			value,
			basePath,
			inputFileName,
			outputFileName,
			table;
		Vector techs, ips;
		int numInfoFiles, numVerFiles, i, j;
		long startTime = 0;

		enum = techInfoFilesHaveChanged.keys();
		numInfoFiles = techInfoFiles.size();

		basePath = edHome + "TechInfo/";

		boolean customerTypesUpdated = false;

		for (i = 0; i < numInfoFiles; i++) {

			fileName = (String) techInfoFiles.elementAt(i);
			changed = (Boolean) techInfoFilesHaveChanged.get(fileName);

			if (changed.booleanValue()) {

				if (fileName.equals("last.release.data")) {
					message.displayMessage(
						"Populating TECHNOLOGY_VERSION Table",
						1);
					ff2db.transferToTechnologyVersionTable(
						basePath + "technology_version");
					//new for 4.5.1 fixpack per Joe Crichton
					if (!customerTypesUpdated) {
						message.displayMessage(
							"Populating CUSTOMER_TYPES Table",
							1);
						ff2db.transferToCustomerTypesTable(
							basePath + "customer.types");
						customerTypesUpdated = true;
					}

					message.displayMessage("Populating ASIC_CODENAME Table", 1);
					ff2db.transferToASICCodenameTable(
						basePath + "asic_codename");
					//end of new for 4.5.1

				} else if (
					fileName.equals("customer.types")
						&& !customerTypesUpdated) {
					message.displayMessage(
						"Populating CUSTOMER_TYPES table",
						1);
					ff2db.transferToCustomerTypesTable(
						basePath + "customer.types");
					customerTypesUpdated = true;
				} else if (fileName.equals("customer.list")) {

					if (!customerTypesUpdated) {
						message.displayMessage(
							"Populating CUSTOMER_TYPES Table",
							1);
						ff2db.transferToCustomerTypesTable(
							basePath + "customer.types");
						customerTypesUpdated = true;
					}

					message.displayMessage("Populating ASIC_CODENAME Table", 1);
					ff2db.transferToASICCodenameTable(
						basePath + "asic_codename");
				} else if (fileName.equals("halt_shipping")) {
					message.displayMessage("Populating HALT_SHIPPING Table", 1);
					ff2db.transferToHaltShippingTable(
						basePath + "halt_shipping");
				} else if (fileName.equals("kit_info")) {
					message.displayMessage("Populating KIT_INFO Table", 1);
					ff2db.transferToKitInfoTable(basePath + "kit_info");
				}

			}
		}

		int numTechNames = techNamesAndVersions.size();

		numVerFiles = techVerFiles.size();

		// System.out.println("debug 0: # tech version files = " + numVerFiles);
		// System.out.println("debug 0: # tech names = " + numTechNames);

		for (i = 0; i < numVerFiles; i++) {

			fileName = (String) techVerFiles.elementAt(i);
			techs = (Vector) techVerFilesHaveChanged.get(fileName);

			// System.out.println("debug 1: " + fileName + " -- techs: " + techs.toString());

			if (fileName.indexOf("Readmes") == -1) {
				// Readme files are dealt elsewhere

				if (!techs.isEmpty()) {

					for (j = 0; j < numTechNames; j++) {

						basePath =
							edHome
								+ "ASICTECH/"
								+ techNamesAndVersions.elementAt(j)
								+ "/";

						// System.out.println("debug 2: " + basePath);

						if (fileName
							.equals("ProductDefinition/shipping.cores.data")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseGeneralAvailableCoresInfo(
								basePath + fileName,
								basePath + "ProductDefinition/ga_cores");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToGACoresTable(
								basePath + "ProductDefinition/ga_cores");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.GA_CORES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/ga_cores",
								1);
						}

						// new for 2.9
						if (fileName
							.equals("ProductDefinition/shipping.tools.data")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseGeneralAvailableNSDInfo(
								basePath + fileName,
								basePath + "ProductDefinition/ga_nsd");
							//new for 3.10.1
							ffp.parsePlatformMDLInfo(
								basePath + fileName,
								basePath + "ProductDefinition/platform.mdl");
							//end of new for 3.10.1
							//new for 4.2.1
							ffp.parsePacketMDLInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/packet_mdl.standard");
							//end of new for 4.2.1

							//new for 5.1.1
							ffp.parseDAModelTypeInfo(
								basePath + fileName,
								basePath + "ProductDefinition/da_modeltype");

							//end of new for 5.1.1
							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToNSD_GATable(
								basePath + "ProductDefinition/ga_nsd");
							//new for 3.10.1
							ff2db.transferToPlatformMDLTypesTable(
								basePath + "ProductDefinition/platform.mdl");
							//end of new for 3.10.1

							//new for 4.2.1
							ff2db.transferToPacketMDLTypeTable(
								basePath
									+ "ProductDefinition/packet_mdl.standard");
							//end of new for 4.2.1

							//new for 5.1.1
							ff2db.transferToDAModelTypeTable(
								basePath + "ProductDefinition/da_modeltype");
							//end of new for 5.1.1
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.NSD_GA_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/ga_nsd",
								1);

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.PLATFORM_MDL_TYPES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/platform.mdl",
								1);

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.PACKETS_MDL_TYPE_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/packet_mdl.standard",
								1);

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.DA_MODELTYPES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/da_modeltype",
								1);
						}

						// new for 2.9 fix-pack
						if (fileName
							.equals("ProductDefinition/shipping.base_orderable.data")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseGaBaseOrdInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/ga_base_orderables");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToGaBaseOrdTable(
								basePath
									+ "ProductDefinition/ga_base_orderables");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.GA_BASE_ORD_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/ga_base_orderables",
								1);
						} else if (
							fileName.equals(
								"ProductDefinition/shipping.customers.data")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseRestrictedCoresInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/restricted_cores");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToRestrictedCoresTable(
								basePath
									+ "ProductDefinition/restricted_cores");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.RESTRICTED_CORES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/restricted_cores",
								1);

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseNonStandardDeliverablesInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/nonstandard_deliverables");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToNonStandardDeliverablesTable(
								basePath
									+ "ProductDefinition/nonstandard_deliverables");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.NON_STANDARD_DELIVERABLES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/nonstandard_deliverables",
								1);

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseResBaseOrdInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/res_base_orderables");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToResBaseOrdTable(
								basePath
									+ "ProductDefinition/res_base_orderables");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.RESTRICTED_BASE_ORD_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/res_base_orderables",
								1);
							//new for 5.1.1 fixpack
							ff2db.transferToDAModelTypeTable(
								basePath + "ProductDefinition/da_modeltype");
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.DA_MODELTYPES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/da_modeltype",
								1);
							//end of new for 5.1.1 fixpack
						} else if (
							fileName.equals(
								"ProductDefinition/shipping.orderable.components")) {

							// System.out.println("parseOrderableComponentsInfo " + basePath + fileName);

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseOrderableComponentsInfo(
								basePath + fileName,
								basePath + "ProductDefinition");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToOrderableMdlTypesTable(
								basePath
									+ "ProductDefinition/orderable_mdl_types");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.ORDERABLE_MDL_TYPES_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/orderable_mdl_types",
								1);

							ff2db.transferToOrderableLibGroupsTable(
								basePath
									+ "ProductDefinition/orderable_lib_groups");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.ORDERABLE_LIB_GROUPS_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/orderable_lib_groups",
								1);
						}

						//new for 4.2.1

						else if (
							fileName.equals(
								"ProductDefinition/shipping.libgroups.data")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								basePath + " " + fileName,
								1);
							ffp.parsePacketsLIBInfo(
								basePath + fileName,
								basePath
									+ "ProductDefinition/shipping.libgroups");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToPacketsLIBGroupTable(
								basePath
									+ "ProductDefinition/shipping.libgroups");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.PACKETS_LIB_GROUP_TABLE
									+ " from "
									+ basePath
									+ "ProductDefinition/shipping.libgroups",
								1);

						}

						//end of new for 4.2.1
						//new for 5.1.1 CU65

						//end of new for 5.1.1

						else if (
							fileName.equals(
								"DeltaReleases/delta_releases_list")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseDeltaReleaseInfo(
								basePath + fileName,
								basePath + "DeltaReleases");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToDeltaReleasesTable(
								basePath + "DeltaReleases/delta_releases");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.DELTA_RELEASES_TABLE
									+ " from "
									+ basePath
									+ "DeltaReleases/delta_releases",
								1);

							ff2db.transferToDeltaReleaseMdlTypesTable(
								basePath
									+ "DeltaReleases/delta_release_mdl_types");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.DELTA_RELEASE_MDL_TYPES_TABLE
									+ " from "
									+ basePath
									+ "DeltaReleases/delta_release_mdl_types",
								1);

							ff2db.transferToDeltaReleaseLibGroupsTable(
								basePath
									+ "DeltaReleases/delta_release_lib_groups");
							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}
							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table
										.DELTA_RELEASE_LIB_GROUPS_TABLE
									+ " from "
									+ basePath
									+ "DeltaReleases/delta_release_lib_groups",
								1);
						} else if (
							fileName.equals(
								"DeltaReleases/delta_packet_list")) {

							if (debug) {
								startTime = System.currentTimeMillis();
							}

							ffp.parseDeltaPacketList(
								basePath + fileName,
								basePath + "DeltaReleases/delta_packets");

							if (debug) {
								message.displayMessage(
									"Parsing: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
								startTime = System.currentTimeMillis();
							}

							ff2db.transferToDeltaPacketsTable(
								basePath + "DeltaReleases/delta_packets");

							if (debug) {
								message.displayMessage(
									"DB2xfer: "
										+ ((System.currentTimeMillis()
											- startTime)
											/ 1000)
										+ " secs",
									1);
							}

							message.displayMessage(
								"Updated: "
									+ FlatFileToDB2Table.DELTA_PACKETS_TABLE
									+ " from "
									+ basePath
									+ "DeltaReleases/delta_packets",
								1);
							//new for 3.7.1

							checkReleaseNotes(basePath + "DeltaReleases");

						} else if (fileName.equals("ToolKit/platforms")) {

							this.xfrPlatforms = true;

						}
					} //end of numTechNames loop
				} //end of !tech.isEmpty()
			} //end of (if (Readme == -1)
		} //end of numVerFiles loop

		//new for 5.1.1 loop for IP
		int numIPNames = ipNamesAndVersions.size();
		numVerFiles = ipVerFiles.size();
		for (i = 0; i < numVerFiles; i++) {
			fileName = (String) ipVerFiles.elementAt(i);
			ips = (Vector) ipVerFilesHaveChanged.get(fileName);
			if (!ips.isEmpty()) {

				for (j = 0; j < numIPNames; j++) {

					basePath =
						edHome
							+ "ASICTECH/"
							+ ipNamesAndVersions.elementAt(j)
							+ "/";
					if (fileName
						.equals("ProductDefinition/iipmds_customer_ip_entitlements")) {

						if (debug) {
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(basePath + " " + fileName, 1);
						ffp.parseIPEntitlement(
							basePath + fileName,
							basePath
								+ "ProductDefinition/customer.entitlement");

						if (debug) {
							message.displayMessage(
								"Parsing: "
									+ ((System.currentTimeMillis() - startTime)
										/ 1000)
									+ " secs",
								1);
							startTime = System.currentTimeMillis();
						}
						ff2db.transferToIPEntitlementTable(
							basePath
								+ "ProductDefinition/customer.entitlement");
						if (debug) {
							message.displayMessage(
								"DB2xfer: "
									+ ((System.currentTimeMillis() - startTime)
										/ 1000)
									+ " secs",
								1);
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(
							"Updated: "
								+ FlatFileToDB2Table.IP_ENTITLEMENT_TABLE
								+ " from "
								+ basePath
								+ "ProductDefinition/iipmds_customer_ip_entitlements",
							1);

					}
					//new for 5.4.1 & modified for 6.1
					else if (
						fileName.equals(
							"ProductDefinition/iipmds_ipDef_data")) {
						if (debug) {
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(basePath + " " + fileName, 1);

						ff2db.transferToIPContentTable(
							basePath + "ProductDefinition/iipmds_ipDef_data",(String)ipNamesAndVersions.elementAt(j));
						if (debug) {
							message.displayMessage(
								"DB2xfer: "
									+ ((System.currentTimeMillis() - startTime)
										/ 1000)
									+ " secs",
								1);
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(
							"Updated: "
								+ FlatFileToDB2Table.IP_CONTENT_TABLE
								+ " from "
								+ basePath
								+ "ProductDefinition/iipmds_ipDef_data",
							1);
					}
					//new for 6.1.1
					else if (
						fileName.equals(
							"ProductDefinition/released.ip.revisions")) {
						if (debug) {
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(basePath + " " + fileName, 1);
						ffp.parseReleasedIPRevisions(
							basePath + fileName,
							basePath
								+ "ProductDefinition/technology.revision.list",(String) ipNamesAndVersions.elementAt(j));

						ff2db.transferToRevisionListTable(
							basePath
								+ "ProductDefinition/technology.revision.list");
						ff2db.transferToRevisionRelNotesTable(basePath + "ProductDefinition/",(String) ipNamesAndVersions.elementAt(j));
						if (debug) {
							message.displayMessage(
								"DB2xfer: "
									+ ((System.currentTimeMillis() - startTime)
										/ 1000)
									+ " secs",
								1);
							startTime = System.currentTimeMillis();
						}
						message.displayMessage(
							"Updated: "
								+ FlatFileToDB2Table.REVISION_LIST_TABLE
								+ " from "
								+ basePath
								+ "ProductDefinition/released.ip.revisions",
							1);
					}
					//end of new for 6.1.1
					else if (fileName.equals("ToolKit/platforms"))
						this.xfrPlatforms = true;
				}
			}
		}

		//end of new for 5.1.1
	}

	static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	static void sendErrorMail(String subject, String body) {
		sendEmailMessage(
			FROM_ERROR,
			errMailTo,
			errMailCc,
			null,
			null,
			subject,
			body,
			2);
	}

	static void sendEmailMessage(
		String from,
		String to,
		String cc,
		String subject,
		String body) {
		sendEmailMessage(from, to, cc, null, null, subject, body, 2);
	}

	static void sendEmailMessage(
		String from,
		String to,
		String cc,
		String bcc,
		String replyTo,
		String subject,
		String body,
		int numRetries) {

		String mailString =
			"FROM: "
				+ from
				+ "\nTO: "
				+ to
				+ "\nSUBJECT: "
				+ subject
				+ "\nBODY: "
				+ body;

		if (from == null
			|| to == null
			|| body == null
			|| from.length() == 0
			|| to.length() == 0
			|| body.length() == 0) {
			message.displayMessage(
				"A required field is missing or blank in the following email:\n"
					+ mailString,
				1);
			return;
		}

		long sleepTime = 5 * 1000;

		boolean mailSent = false;

		String[] emailHost =
			{
				ff2dbRB.getString("EMAIL_HOST_NAME").trim(),
				ff2dbRB.getString("BACKUP_1_EMAIL_HOST_NAME").trim(),
				ff2dbRB.getString("BACKUP_2_EMAIL_HOST_NAME").trim()};
		int numHosts = emailHost.length;

		mailBlock : for (int i = 0; i <= numRetries; i++) {

			for (int j = 0; j < numHosts; j++) {

				try {
					message.displayMessage(
						"Sending mail to: " + to + " with subject: " + subject,
						3);
					Mailer.sendMail(
						emailHost[j],
						from,
						to,
						cc,
						bcc,
						replyTo,
						subject,
						body);
					mailSent = true;
					break mailBlock;
				} catch (Throwable t) {
					String str =
						"thrown while trying to send email (attempt# "
							+ ((i * numHosts) + j + 1)
							+ ")\n\n"
							+ "StackTrace:\n"
							+ getStackTrace(t)
							+ "\n\n"
							+ "Will Re-try "
							+ ((numRetries - i) * numHosts + (numHosts - 1 - j))
							+ " times\n\n"
							+ "This error was thrown at: "
							+ new Date();
					message.displayMessage(str, 1);
				}
			}

			try {
				Thread.sleep(sleepTime);
			} catch (Throwable t) {
				String str =
					"thrown while WAITING to re-send email\n"
						+ "StackTrace:\n"
						+ getStackTrace(t)
						+ "\n\n"
						+ "This error was thrown at: "
						+ new Date();
				message.displayMessage(str, 1);
			}
		}

		if (mailSent)
			message.displayMessage("sent mail", 3);
		else {
			String str =
				"ERROR: The following email could NOT be sent despite "
					+ ((numRetries + 1) * numHosts)
					+ " attempts:\n"
					+ mailString;

			message.displayMessage(str, 1);
		}
	}

	private String readFile(String filename) throws IOException {

		String content = "";
		int bytesRead = 0;
		byte[] arr = new byte[2048];

		FileInputStream in = new FileInputStream(filename);
		while (bytesRead >= 0) {
			content += new String(arr, 0, bytesRead);
			bytesRead = in.read(arr);
		}
		in.close();

		return content.trim();

	}

	int execute(String cmd) throws IOException, InterruptedException {

		String inString = "";
		String errString = "";
		int arrSize = 1024;
		byte[] arr = new byte[arrSize];
		int exitValue = -1;
		String[] command = { "/bin/ksh", "-c", cmd };

		Process p = Runtime.getRuntime().exec(command);
		exitValue = p.waitFor();

		try {
			BufferedInputStream in =
				new BufferedInputStream(p.getInputStream());
			BufferedInputStream err =
				new BufferedInputStream(p.getErrorStream());
			int read = 0;
			while (read >= 0) {
				inString += new String(arr, 0, read);
				read = in.read(arr, 0, arrSize);
			}
			read = 0;
			while (read >= 0) {
				errString += new String(arr, 0, read);
				read = err.read(arr, 0, arrSize);
			}
			in.close();
			err.close();
		} catch (Throwable t) {
			message.displayMessage(
				"thrown reading output from: "
					+ cmd
					+ "\nStacktrace:\n"
					+ getStackTrace(t));
		}

		String str =
			cmd
				+ " returned an exit Value of "
				+ exitValue
				+ "\n"
				+ "stdout: "
				+ inString
				+ "\n"
				+ "stderr: "
				+ errString
				+ "\n";

		if (exitValue != 0)
			message.displayMessage(str, 1);
		else
			message.displayMessage(str, 3);

		return exitValue;

	}

	String executeAndGetStream(String cmd) {
		return executeAndGetStream(cmd, false);
	}

	String executeAndGetStream(String cmd, boolean BTVtoBLDdb2Xfer) {

		String inString = "";
		String errString = "";
		int arrSize = 1024;
		byte[] arr = new byte[arrSize];
		int exitValue = -1;
		String[] command = { "/bin/ksh", "-c", cmd };
		long startTime = System.currentTimeMillis();

		try {
			Process p = Runtime.getRuntime().exec(command);
			exitValue = p.waitFor();

			BufferedInputStream in =
				new BufferedInputStream(p.getInputStream());
			BufferedInputStream err =
				new BufferedInputStream(p.getErrorStream());

			int read = 0;
			while (read >= 0) {
				inString += new String(arr, 0, read);
				read = in.read(arr, 0, arrSize);
			}

			read = 0;
			while (read >= 0) {
				errString += new String(arr, 0, read);
				read = err.read(arr, 0, arrSize);
			}

			in.close();
			err.close();
		} catch (IOException ioe) {
			message.displayMessage(
				"IOException executing " + cmd + ":\n" + getStackTrace(ioe),
				1);
			exitValue = -1;
		} catch (InterruptedException ie) {
			message.displayMessage(
				"InterruptedException executing "
					+ cmd
					+ ":\n"
					+ getStackTrace(ie),
				1);
			exitValue = -1;
		}

		long execTime = System.currentTimeMillis() - startTime;

		String str =
			cmd
				+ " returned an exit Value of "
				+ exitValue
				+ " after "
				+ execTime
				+ " ms \n"
				+ "stdout: "
				+ inString
				+ "\n"
				+ "stderr: "
				+ errString
				+ "\n";

		if (exitValue != 0) {
			message.displayMessage(str, 1);
			if (BTVtoBLDdb2Xfer)
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"BTVtoBLDdb2Xfer.SCR error",
					str);
			return null;
		} else if (BTVtoBLDdb2Xfer) {
			String numRowsExported = "NA";

			int index1 = inString.indexOf("Number of rows exported:");
			if (index1 >= 0) {
				int index2 = inString.indexOf(':', index1);
				int index3 = inString.indexOf('\n', index2);
				numRowsExported = inString.substring(index2 + 1, index3).trim();
			} else {
				index1 = inString.indexOf("Number of rows exported");
				if (index1 >= 0) {
					index1 = inString.indexOf('(', index1);
					int index2 = inString.indexOf(':', index1);
					int index3 = inString.indexOf('\n', index2);
					numRowsExported =
						inString.substring(index1 + 1, index1 + 2)
							+ inString.substring(index2 + 1, index3).trim();
				}
				index1 =
					inString.indexOf("Number of rows exported", index1 + 1);
				if (index1 >= 0) {
					index1 = inString.indexOf('(', index1);
					int index2 = inString.indexOf(':', index1);
					int index3 = inString.indexOf('\n', index2);
					numRowsExported += " "
						+ inString.substring(index1 + 1, index1 + 2)
						+ inString.substring(index2 + 1, index3).trim();
				}
			}

			message.displayMessage(
				padString(numRowsExported, 6)
					+ " rows exported in "
					+ padString(String.valueOf(execTime), 5)
					+ " ms by: "
					+ cmd,
				1);
			return numRowsExported;
		} else {
			message.displayMessage(str, 3);
			return inString.trim();
		}

	}

	private String padString(String s, int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = s.length(); i < length; i++)
			sb.append(" ");
		return sb.toString() + s;
	}

	boolean sendAlerts() {

		int space;
		Boolean bval;
		BufferedReader in;
		Enumeration enum;
		String fileName, subject, to, from, replyto, techNameVersion;
		String tech = "";
		String version = "";
		StringBuffer body;
		Vector techs;
		StringBuffer tech_entitlement = null;

		try {

			to = ff2dbRB.getString("mailto").trim();
			from = ff2dbRB.getString("mailfrom").trim();

			enum = techInfoFilesHaveChanged.keys();

			while (enum.hasMoreElements()) {

				fileName = (String) enum.nextElement();

				//Trigger to send new release alert
				if (fileName.equals("all.releases.data")) {

					bval = (Boolean) techInfoFilesHaveChanged.get(fileName);
					
					message.displayMessage("Info: In sendAlerts() for New Release Alert ", 1);
					
					if (bval.booleanValue()) {

						String dataFileName = edHome + "TechInfo/latest.release.data";
						File dataFile = new File(dataFileName);
						if (!dataFile.exists())
							continue;

						//File contents
						/*
						 * TECHNOLOGY;Cu-65HP;
                         * LIBRARY_RELEASE;rel3.0;
						 */

						in = new BufferedReader(new FileReader(dataFile));
						message.displayMessage("Info: In sendAlerts() After reading latest.release.data file ", 1);
						
						tech = in.readLine(); 						
						if( tech != null && !tech.equals("") )
						{
							int beginIndex = tech.indexOf(";");
							int lastIndex = tech.lastIndexOf(";");
							tech = tech.substring(beginIndex + 1, lastIndex);
							message.displayMessage("Info: Technology =  " + tech, 1);
						}
						else
						{
							//Send Email to developer that technology is missing
							message.displayMessage("Info: Invalid Technology =  " + tech, 1);
							continue;
						}
						
						version = in.readLine();
						
						if( version != null && !version.equals("") )
						{
							int beginIndex = version.indexOf(";");
							int lastIndex = version.lastIndexOf(";");
							version = version.substring(beginIndex + 1, lastIndex );
							message.displayMessage("Info: Version =  " + version, 1);
						}
						else
						{
							//Send Email to developer that technology is missing
							message.displayMessage("Info: Invalid Version =  " + version, 1);
							continue;
						}
						in.close();//close the latest.release.data input stream
						
						//Now read latest.release.template to get the email contents
						/*
						    Dear ASIC Connect customer,
							
							The design kit for release %LIBRARY_RELEASE% of %TECHNOLOGY% technology is available
							for download on IBM Customer Connect.
							
							You can order this design kit by clicking on the following URL:
							%LINK_DK%
							
							Thank You,
							ASIC Connect
							
							
							Please do not reply to this e-mail message. If you do not wish to
							receive this message in the future, please go to the following URL:
							%LINK_PREF%
							and set "Major Release Alert" to "No"
                         */
						StringBuffer latestReleaseOut = new StringBuffer();
						
						dataFileName = edHome + "TechInfo/latest.release.template";
						dataFile = new File(dataFileName);
						if (!dataFile.exists())
							continue;
						in = new BufferedReader(new FileReader(dataFile));

						subject = in.readLine();//SUBJECT=IBM Customer Connect: New Technology Release From IBM ASICS
						latestReleaseOut.append(subject + "\n");
						
						body = new StringBuffer();

						while (in.ready())
							body.append(in.readLine() + "\n");
						in.close();
						
						//Remove SUBJECT=
						if( subject != null && !subject.equals("") )
						{
							subject = subject.substring(subject.indexOf("=") + 1);
							message.displayMessage("Info: SUBJECT in latest.release.template =  " + subject, 1);
						}
						else
						{
							message.displayMessage("Info: Invalid SUBJECT =  " + subject, 1);
						}
						
						String emailBody = "";
						if( body != null && !body.equals("") )
						{				
							String libRelease = body.toString().replaceAll("%LIBRARY_RELEASE%", version);
							emailBody = libRelease.replaceAll("%TECHNOLOGY%",tech);
							latestReleaseOut.append(emailBody);
						}
					    String code = "TECHNOLOGY=" + tech + ";" + "VERSION=" + version + ";";		
						// to remove mqDelimiter

						/*
						            body.append("\n\n");
						            body.append("Please do not reply to this e-mail message. If you do not wish to\n");
						            body.append("receive this message in the future, please go to the following URL:\n");
						            body.append(PROFILE_SERVLET + "?sub_func=account\n");
						            body.append("and set \"Major Release Alert\" to \"No\" \n");
						            */

						/*
						body.append("   1. Login to the Edge at http://www.ibm.com/edge \n");
						body.append("   2. Select Custom Logic (ASIC) under the Design Solutions section \n");
						body.append("   3. Select \"My Account\" \n");
						body.append("   4. Select \"Profile\" \n");
						body.append("   5. Set \"Major Release Alert\" to \"No\" \n");
						*/

						StringBuffer mqBody = new StringBuffer();

						mqBody.append("CODE=");
						mqBody.append(code);
						mqBody.append(mqDelimiter);

						mqBody.append("FROM=");
						mqBody.append("eConnect@us.ibm.com");
						mqBody.append(mqDelimiter);

						mqBody.append("REPLYTO=");
						mqBody.append("eConnect@us.ibm.com");
						mqBody.append(mqDelimiter);

						mqBody.append("SUBJECT=");
						mqBody.append(subject);
						mqBody.append(mqDelimiter);

						mqBody.append("MESSAGE=");
						mqBody.append(emailBody);
						mqBody.append(mqDelimiter);

						if (useMQ) {

							MQSend edmq = new MQSend(mqRBName);

							try {
								edmq.sendMQMessage(
									"EDQ3_NEW_DK",
									null,
									mqBody.toString(),
									true);
								message.displayMessage(
									"\nNew Design Kit Alerts for "
										+ tech
										+ " sent",
									1);
							} catch (Throwable t) {
								message.displayMessage(
									"Error sending EDQ3_NEW_DK MQ message",
									1);
							}

						}

						// sendEmailMessage(FROM_ERROR, errMailTo, errMailCc, subject, body.toString());
						message.displayMessage(
							"MQ Content:\n" + mqBody.toString(),
							3);

						File newDataFile =
							new File(
								edHome + "TechInfo/latest.release.data." + tech + "." + version);
						BufferedWriter out = new BufferedWriter(new FileWriter(newDataFile));
						out.write(latestReleaseOut.toString());
						message.displayMessage(" After writing latest.release.data" + tech + "." + version ,1);
						out.close();
					}
				}
			}

			enum = techVerFilesHaveChanged.keys();

			while (enum.hasMoreElements()) {

				fileName = (String) enum.nextElement();

				if (fileName.equals("DeltaReleases/delta_packet_list")) {

					techs = (Vector) techVerFilesHaveChanged.get(fileName);

					for (int i = 0; i < techs.size(); i++) {

						techNameVersion = (String) techs.elementAt(i);
						space = techNameVersion.indexOf(" ");

						tech = techNameVersion.substring(0, space);
						version =
							techNameVersion.substring(
								space + 1,
								techNameVersion.length());

						String baseDir =
							edHome
								+ "ASICTECH/"
								+ tech
								+ "/"
								+ version
								+ "/DeltaReleases/";
						String[] list = new File(baseDir).list();
						if (list == null)
							continue;

						for (int j = 0; j < list.length; j++) {



/*							Patch: CE070394.v7_core060919
							Date: 09/19/2006
							Description: CU08 model kit updates have been released. For more information concerning this delta release, pleas
							e log onto IBM Customer Connect to view the release notes for the delta and to place your order as needed.
*/
							if (list[j].startsWith("delta_alert.")) {

								/*
								Patch:         SE120118.v12_D9584
								Date:          10/17/2001
								Description:   UNILR/UNIIB - Added noise to core
								*/

								String delText = readFile(baseDir + list[j]);

								int patchIndex = delText.indexOf("Patch:") + 6;
								int dateIndex =
									delText.indexOf("Date:", patchIndex) + 5;
								int descIndex =
									delText.indexOf("Description:", dateIndex);

								String delDate =
									delText
										.substring(dateIndex, descIndex)
										.trim();

								String delDesc =
									delText.substring(descIndex + 12).trim();

								delDesc = delDesc.replace('^', ' ');
								// to remove mqDelimiter

								String DRname = list[j].substring(12);
								// portion of string after delta_alert

								Date d = null;
								try {
									d = alertDate.parse(delDate);
								} catch (ParseException pe) {
									String msg =
										"Invalid date for delta alert. Technology: "
											+ tech
											+ " Delta: "
											+ DRname
											+ " Date: "
											+ delDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for delta alert",
										msg);
									continue;
								}

								long oneDay = 24 * 3600000;
								long daysAhead = 2;
								long daysBehind = 30;
								long timeNow = System.currentTimeMillis();
								long timeSent = d.getTime();

								if (timeNow - timeSent
									> (daysBehind * oneDay)) {
									String msg =
										"Invalid date for delta alert. Technology: "
											+ tech
											+ " Delta: "
											+ DRname
											+ " Date: "
											+ delDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for delta alert",
										msg);
									continue;
								}

								if (timeSent - timeNow
									> (daysAhead * oneDay)) {
									String msg =
										"Invalid date for delta alert. Technology: "
											+ tech
											+ " Delta: "
											+ DRname
											+ " Date: "
											+ delDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for delta alert",
										msg);
									continue;
								}

								String destFile =
									baseDir + list[j] + ".ASIC_CODENAMES";

								String asicCodenames = "";

								try {
									String cmd =
										ff2dbRB
											.getString("genDeltaDistList")
											.trim()
											+ " "
											+ "ICCPROD"
											+ " "
											+ tech
											+ " "
											+ version
											+ " "
											+ DRname
											+ " "
											+ destFile;

									message.displayMessage(
										"executing:\n" + cmd,
										2);
									int ev = execute(cmd);

									if (ev == 0) {
										String line;
										in =
											new BufferedReader(
												new FileReader(destFile));
										while ((line = in.readLine())
											!= null) {
											line = line.trim();
											if (line.length() == 0)
												continue;
											else
												asicCodenames += line + ",";
										}

										in.close();

										new File(destFile).delete();

									} else if (ev == 1) {
										message.displayMessage(
											"Error: "
												+ cmd
												+ " returned 1: error determining the list of customers to notify",
											1);
										continue;
									} else if (ev == 2) {
										message.displayMessage(
											"Error: "
												+ cmd
												+ " returned 2: error in command line parameters",
											1);
										continue;
									} else {
										message.displayMessage(
											"Error: "
												+ cmd
												+ " returned "
												+ ev
												+ ": unknown exit value",
											1);
										continue;
									}

									if (asicCodenames.trim().length() == 0) {
										new File(baseDir + list[j]).delete();
										message.displayMessage(
											cmd + " returned NO asic codenames",
											2);
										continue;
									}
								} catch (Exception e) {
									message.displayMessage(
										"Exception generating asic codenames for delta alert. Stacktrace:\n"
											+ getStackTrace(e),
										1);
									continue;
								}

								// new for 2.9
								int[] deltaTypeAndReason =
									getDeltaTypeAndReason(
										DRname,
										baseDir + "delta_releases_list");
								if (deltaTypeAndReason == null)
									continue;

								StringBuffer mqBody = new StringBuffer();

								mqBody.append("ASIC_CODENAMES=");
								mqBody.append(asicCodenames);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_TYPE=");
								mqBody.append(deltaTypeAndReason[0]);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_REASON=");
								mqBody.append(deltaTypeAndReason[1]);
								mqBody.append(mqDelimiter);

								mqBody.append("CODE=");
								mqBody.append(ffp.getTechDKName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("TECHNOLOGY=");
								mqBody.append(getMktName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("VERSION_NO=");
								mqBody.append(version);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_NAME=");
								mqBody.append(DRname);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_DESCRIPTION=");
								mqBody.append(delDesc);
								mqBody.append(mqDelimiter);

								mqBody.append("RELEASE_DATE=");
								mqBody.append(delDate);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_LINK=");
								mqBody.append(mqDelimiter);

								if (useMQ) {
									MQSend edmq = new MQSend(mqRBName);
									try {
										edmq.sendMQMessage(
											"EDQ3_NEW_DR",
											null,
											mqBody.toString(),
											true);
										new File(baseDir + list[j]).delete();
										message.displayMessage(
											"\nDelta Release Alerts for "
												+ tech
												+ " "
												+ version
												+ " "
												+ DRname
												+ " (type: "
												+ deltaTypeAndReason[0]
												+ ") (reason: "
												+ deltaTypeAndReason[1]
												+ ") sent to: "
												+ asicCodenames,
											1);
									} catch (Throwable t) {
										message.displayMessage(
											"Error sending EDQ3_NEW_DR MQ message",
											1);
									}
								}

								message.displayMessage(
									"MQ Content:\n" + mqBody.toString(),
									3);

							} else if (list[j].startsWith("core_alert.")) {

								/*
								Core: xyz
								Date: 10/12/2001
								Asic_Codenames: abc,hyt,hjaj...
								*/

								String delText = readFile(baseDir + list[j]);

								int coreIndex = delText.indexOf("Core:") + 5;
								int dateIndex =
									delText.indexOf("Date:", coreIndex);

								String coreName =
									delText
										.substring(coreIndex, dateIndex)
										.trim();

								dateIndex += 5;
								int codenameIndex =
									delText.indexOf(
										"Asic_Codenames:",
										dateIndex);

								String coreDate =
									delText
										.substring(dateIndex, codenameIndex)
										.trim();
								String asicCodenames =
									delText
										.substring(codenameIndex + 15)
										.trim();

								Date d = null;
								try {
									d = alertDate.parse(coreDate);
								} catch (ParseException pe) {
									String msg =
										"Invalid date for core alert. Technology: "
											+ tech
											+ " Core: "
											+ coreName
											+ " Date: "
											+ coreDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for core alert",
										msg);
									continue;
								}

								long oneDay = 24 * 3600000;
								long daysAhead = 2;
								long daysBehind = 30;
								long timeNow = System.currentTimeMillis();
								long timeSent = d.getTime();

								if (timeNow - timeSent
									> (daysBehind * oneDay)) {
									String msg =
										"Invalid date for core alert. Technology: "
											+ tech
											+ " Core: "
											+ coreName
											+ " Date: "
											+ coreDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for core alert",
										msg);
									continue;
								}

								if (timeSent - timeNow
									> (daysAhead * oneDay)) {
									String msg =
										"Invalid date for core alert. Technology: "
											+ tech
											+ " Core: "
											+ coreName
											+ " Date: "
											+ coreDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for core alert",
										msg);
									continue;
								}

								StringBuffer mqBody = new StringBuffer();

								mqBody.append("ASIC_CODENAMES=");
								mqBody.append(asicCodenames);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_TYPE=");
								mqBody.append(2);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_REASON=");
								mqBody.append(0);
								mqBody.append(mqDelimiter);

								mqBody.append("CODE=");
								mqBody.append(ffp.getTechDKName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("TECHNOLOGY=");
								mqBody.append(getMktName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("VERSION_NO=");
								mqBody.append(version);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_NAME=");
								mqBody.append(coreName);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_DESCRIPTION=");
								mqBody.append(mqDelimiter);

								mqBody.append("RELEASE_DATE=");
								mqBody.append(coreDate);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_LINK=");
								mqBody.append(mqDelimiter);

								if (useMQ) {
									MQSend edmq = new MQSend(mqRBName);
									try {
										edmq.sendMQMessage(
											"EDQ3_NEW_DR",
											null,
											mqBody.toString(),
											true);
										new File(baseDir + list[j]).delete();
										message.displayMessage(
											"\nCore Alerts for "
												+ tech
												+ " "
												+ version
												+ " "
												+ coreName
												+ " (type: 2) (reason: 0) sent to: "
												+ asicCodenames,
											1);
									} catch (Throwable t) {
										message.displayMessage(
											"Error sending EDQ3_NEW_DR MQ message",
											1);
									}
								}

								message.displayMessage(
									"MQ Content:\n" + mqBody.toString(),
									3);

							} else if (list[j].startsWith("base_ord_alert.")) {

								/*
								Base Orderable: Beta Test - CLKPULSE9
								Date: 12/10/2002
								Asic_Codenames: CODE_NAME_1,CODE_NAME_2,CODE_NAME_3,CODE_NAME_4,CODE_NAME_5,
								*/

								String delText = readFile(baseDir + list[j]);

								int baseOrdIndex =
									delText.indexOf("Base Orderable:") + 15;
								int dateIndex =
									delText.indexOf("Date:", baseOrdIndex);

								String baseOrdName =
									delText
										.substring(baseOrdIndex, dateIndex)
										.trim();

								dateIndex += 5;
								int codenameIndex =
									delText.indexOf(
										"Asic_Codenames:",
										dateIndex);

								String baseOrdDate =
									delText
										.substring(dateIndex, codenameIndex)
										.trim();
								String asicCodenames =
									delText
										.substring(codenameIndex + 15)
										.trim();

								Date d = null;
								try {
									d = alertDate.parse(baseOrdDate);
								} catch (ParseException pe) {
									String msg =
										"Invalid date for baseOrd alert. Technology: "
											+ tech
											+ " Base Orderable: "
											+ baseOrdName
											+ " Date: "
											+ baseOrdDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for baseOrd alert",
										msg);
									continue;
								}

								long oneDay = 24 * 3600000;
								long daysAhead = 2;
								long daysBehind = 30;
								long timeNow = System.currentTimeMillis();
								long timeSent = d.getTime();

								if (timeNow - timeSent
									> (daysBehind * oneDay)) {
									String msg =
										"Invalid date for baseOrd alert. Technology: "
											+ tech
											+ " Base Orderable: "
											+ baseOrdName
											+ " Date: "
											+ baseOrdDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for baseOrd alert",
										msg);
									continue;
								}

								if (timeSent - timeNow
									> (daysAhead * oneDay)) {
									String msg =
										"Invalid date for baseOrd alert. Technology: "
											+ tech
											+ " Base Orderable: "
											+ baseOrdName
											+ " Date: "
											+ baseOrdDate;
									message.displayMessage(msg, 1);
									sendEmailMessage(
										FROM_ERROR,
										"v2ravi@us.ibm.com",
										"fyuan@us.ibm.com",
										"Invalid date for baseOrd alert",
										msg);
									continue;
								}

								StringBuffer mqBody = new StringBuffer();

								mqBody.append("ASIC_CODENAMES=");
								mqBody.append(asicCodenames);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_TYPE=");
								mqBody.append(1);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_REASON=");
								mqBody.append(0);
								mqBody.append(mqDelimiter);

								mqBody.append("CODE=");
								mqBody.append(ffp.getTechDKName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("TECHNOLOGY=");
								mqBody.append(getMktName(tech));
								mqBody.append(mqDelimiter);

								mqBody.append("VERSION_NO=");
								mqBody.append(version);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_NAME=");
								mqBody.append(baseOrdName);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_DESCRIPTION=");
								mqBody.append(mqDelimiter);

								mqBody.append("RELEASE_DATE=");
								mqBody.append(baseOrdDate);
								mqBody.append(mqDelimiter);

								mqBody.append("DELTA_LINK=");
								mqBody.append(mqDelimiter);

								if (useMQ) {
									MQSend edmq = new MQSend(mqRBName);
									try {
										edmq.sendMQMessage(
											"EDQ3_NEW_DR",
											null,
											mqBody.toString(),
											true);
										new File(baseDir + list[j]).delete();
										message.displayMessage(
											"\nBase Orderable Alerts for "
												+ tech
												+ " "
												+ version
												+ " "
												+ baseOrdName
												+ " (type: 2) (reason: 0) sent to: "
												+ asicCodenames,
											1);
									} catch (Throwable t) {
										message.displayMessage(
											"Error sending EDQ3_NEW_DR MQ message",
											1);
									}
								}

								message.displayMessage(
									"MQ Content:\n" + mqBody.toString(),
									3);

							}
						}
					}
				}
			}
			
			
			Vector ips = new Vector();
			int numInfoFiles, numVerFiles, nv, ni;
			int numIPNames = ipNamesAndVersions.size();
			numVerFiles = ipVerFiles.size();
			for (nv = 0; nv < numVerFiles; nv++)
			{
				fileName = (String) ipVerFiles.elementAt(nv);
				ips = (Vector) ipVerFilesHaveChanged.get(fileName);
				if ( !ips.isEmpty() && ( fileName.equals("ProductDefinition/released.ip.revisions") ) ) 
				{
     				for (ni = 0; ni < numIPNames; ni++) 
					{

						
								//new for 6.3 - Revision Alerts for IIPDMS and new techs
					
					
								//Loop through ipNamesandVersions and determine if Revisions dir contains any revision_alert files
								//If it does, than get asic code names from getDeltaDataList
								//Create MQ Message and send it to the front end
								//Delete or rename the revision_alert files after processing
							String ipNameandVersion = (String) ipNamesAndVersions.elementAt(ni);
							
							String ipName = "";
							String ipVersion = "";
							
							int indexTemp = ipNameandVersion.indexOf("/");
							ipName = ipNameandVersion.substring(0,indexTemp);
							ipVersion = ipNameandVersion.substring(indexTemp+1);
							
							
							String revisionAlertsPath = edHome + "ASICTECH/" + ipNameandVersion + "/" + "Revisions" + "/";
							
						    // message.displayMessage("Info: Revision Alerts path " + revisionAlertsPath,1);
							
							String[] revisionAlertFileNames = new File(revisionAlertsPath).list();
							if (revisionAlertFileNames == null)
							{
								message.displayMessage(	"Info:  No revision alert files found in " + revisionAlertsPath,1);
							    continue;
							}
							
					for (int j = 0; j < revisionAlertFileNames.length; j++) 
					{
								if (revisionAlertFileNames[j].startsWith("revision_alert.")) 
								{
										String revisionAlertName =  revisionAlertFileNames[j].substring(15);
										
									    String asicCodeNamesFile = revisionAlertsPath + revisionAlertFileNames[j] + ".ASIC_CODENAMES";
		                                
		                                String asicCodeNames = "";
		                                
									    
									    String revisionContents = readFile(revisionAlertsPath + revisionAlertFileNames[j] );
										
										message.displayMessage("Info: Revision Alert contents " + revisionContents,1);
										
										int revisionIndex = revisionContents.indexOf("Dip Revision:");
										
										int dateIndex =  revisionContents.indexOf("Release Date:");
										
										int mkIndex = revisionContents.indexOf("Affected Model Kit Components:");
										
										int initReleaseIndex = revisionContents.indexOf("Initial Dip Release:");
										
										int descIndex =  revisionContents.indexOf("Description:", dateIndex);
		
										String revision = revisionContents.substring(revisionIndex + 13 ,dateIndex).trim();	
										
									    message.displayMessage("Info: revision " + revision,1);
										
										
									    String releaseDate = revisionContents.substring(dateIndex + 13,mkIndex).trim();
										
										
									    message.displayMessage("Info: releaseDate " + releaseDate,1);
										
										
									    String componentList = revisionContents.substring(mkIndex + 30, initReleaseIndex).trim();
									
										
									    message.displayMessage("Info: componentList " + componentList,1);
										
									    String revisionList = revisionContents.substring(revisionIndex + 13, dateIndex).trim();
										
										
									    message.displayMessage("Info: revisionList " + revisionList,1);
										
										String initialDipRelease = revisionContents.substring(initReleaseIndex + 20, descIndex).trim();
										
									    message.displayMessage("Info: initialDipRelease " + initialDipRelease,1);
									    
									    String revisionDesc = revisionContents.substring(descIndex + 12 ).trim();
									    
									    message.displayMessage("Info: revisionDesc " + revisionDesc,1);
		
									   // to remove mqDelimiter     
									    
									    
									Date d = null;
									try {
										d = alertDate.parse(releaseDate);
									} catch (ParseException pe) {
										String msg =
											"Invalid date for revision alert. Technology: "
												+ ipName 
												+ " " + ipVersion
												+ " Revision Name : "
												+ revisionAlertName
												+ " Date: "
												+ releaseDate;
										message.displayMessage(msg, 1);
										sendEmailMessage(
											FROM_ERROR,
											"v2ravi@us.ibm.com",
											"fyuan@us.ibm.com",
											"Invalid date for revision alert",
											msg);
										continue;
									}

									long oneDay = 24 * 3600000;
									long daysAhead = 2;
									long daysBehind = 30;
									long timeNow = System.currentTimeMillis();
									long timeSent = d.getTime();

									if (timeNow - timeSent
										> (daysBehind * oneDay)) {
										String msg =
											"Invalid date for revision alert. Technology: "
											+ ipName 
											+ " " + ipVersion
												+ " Revision: "
												+ revisionAlertName
												+ " Date: "
												+ releaseDate;
										message.displayMessage(msg, 1);
										sendEmailMessage(
											FROM_ERROR,
											"v2ravi@us.ibm.com",
											"fyuan@us.ibm.com",
											"Invalid date for revision alert",
											msg);
										continue;
									}

									if (timeSent - timeNow
										> (daysAhead * oneDay)) {
										String msg =
											"Invalid date for revision alert. Technology: "
											    + ipName 
										        + " " + ipVersion
												+ " Revision: "
												+ revisionAlertName
												+ " Date: "
												+ releaseDate;
										message.displayMessage(msg, 1);
										sendEmailMessage(
											FROM_ERROR,
											"v2ravi@us.ibm.com",
											"fyuan@us.ibm.com",
											"Invalid date for revision alert",
											msg);
										continue;
									}

                             boolean doAsicCodenamesExist = false;
									    try {
												String cmd = ff2dbRB.getString("genDeltaDistList").trim()
												            + " " 
												            + "ICCPROD"
															+ " "
															+ ipName
															+ " "
															+ ipVersion
															+ " "
															+ revisionAlertName
															+ " "
															+ asicCodeNamesFile;
		
												message.displayMessage("executing:\n" + cmd,2);
												
												int ev = execute(cmd);
		
												if (ev == 0) 
												{
													String line;
													in = new BufferedReader( new FileReader(asicCodeNamesFile));
													while ((line = in.readLine()) != null) 
													{
														line = line.trim();
														if (line.length() == 0)
															continue;
														else
															asicCodeNames += line + ",";
													}
		
													in.close();
		
													new File(asicCodeNamesFile).delete();
		
												} else if (ev == 1) {
													message.displayMessage(	"Error: " + cmd + " returned 1: error determining the list of customers to notify",1);
													continue;
												} else if (ev == 2) {
													message.displayMessage(	"Error: " + cmd + " returned 2: error in command line parameters",1);
													continue;
												} else {
													message.displayMessage(	"Error: " + cmd + " returned " + ev + ": unknown exit value",1);
							                        continue;
												}
		
												if (asicCodeNames.trim().length() == 0) {
													new File(revisionAlertsPath + revisionAlertFileNames[j]).delete();
													message.displayMessage(	cmd + " returned no asic codenames",1);
													continue;
												}
												else
												{
													doAsicCodenamesExist = true;
												}
										} catch (Exception e) {
											message.displayMessage("Exception generating asic codenames for revision alert. Stacktrace:\n" + getStackTrace(e), 1);
										   continue;
									    }
								///Create message //////////////
								StringBuffer mqMessage = new StringBuffer();
								mqMessage.append("Initial Dip Release: " + initialDipRelease + "\n" );
								mqMessage.append("Release Date: " + releaseDate + "\n");
								mqMessage.append("Description: " + revisionDesc + "\n");
								
								
								    		
								//Create MQ message
								   StringBuffer mqBody = new StringBuffer();
								
									mqBody.append("ASIC_CODENAMES=");
									mqBody.append(asicCodeNames);
									mqBody.append(mqDelimiter);
					
									mqBody.append("TECHNOLOGY=");
									mqBody.append(getMktName(ipName) );
									mqBody.append(mqDelimiter);
					
									mqBody.append("VERSION=");
									mqBody.append(ipVersion);
									mqBody.append(mqDelimiter);
					
									mqBody.append("COMPONENT_LIST=");
									mqBody.append(componentList);
									mqBody.append(mqDelimiter);
					
									mqBody.append("REVISION_LIST=");
									mqBody.append(revisionList);
									mqBody.append(mqDelimiter);
					
									mqBody.append("FROM=");
									mqBody.append("eConnect@us.ibm.com");
									mqBody.append(mqDelimiter);
					
									mqBody.append("REPLYTO=");
									mqBody.append("eConnect@us.ibm.com");
									mqBody.append(mqDelimiter);
					
									mqBody.append("SUBJECT=");
									mqBody.append("ASIC Connect: New Revisions/IP available");
									mqBody.append(mqDelimiter);
					
									mqBody.append("MESSAGE=");
									mqBody.append(mqMessage);
									mqBody.append(mqDelimiter);
									
									if (useMQ && doAsicCodenamesExist ) {
										MQSend edmq = new MQSend(mqRBName);
										try {
											edmq.sendMQMessage("EDQ3_NEW_REVISION",null,mqBody.toString(),true);
											new File(revisionAlertsPath + revisionAlertFileNames[j]).delete();
											//message.displayMessage("Revision Alert for tech " + tech + " version " + " asic codenames " + asicCodeNames + " revision alert " + revisionAlertName,1);
											message.displayMessage("MQ Content:\n" + mqBody.toString(),1);
										} catch (Throwable t) {
											message.displayMessage(
												"Error sending EDQ3_NEW_REVISION message",
												1);
										}
									}	
									else
									{
										message.displayMessage("No MQ Message to write for revision alert " + revisionAlertName + ".  The asic codenames are " + asicCodeNames,1);		
									}
								} // if file name starts with revision_alert
						}//close revisionAlertFileNames.length for loop
				}//close for loop
		  	}//close ips.isEmpty()
		 }//close numVerFiles	
									
			
			//new for 3.7.1--send out new toolkit and new toolkit patch alerts

			String alertDir = edHome + "FullPDKit/";
			String[] filelist = new File(alertDir).list();

			for (int i = 0; i < filelist.length; i++) { //for

				if (filelist[i].startsWith("toolkit_alert")) { //if

					/*
					  Tech: CU11
					  Ver: v11.0
					  Date: 5/28/03
					  Subject: IBM Customer Connect: new Toolkit release available
					  body text...
					*/

					String alertText = readFile(alertDir + filelist[i]);
					int techIndex = alertText.indexOf("Tech:") + 5;
					int verIndex = alertText.indexOf("Ver:", techIndex) + 4;
					int dateIndex = alertText.indexOf("Date:", verIndex) + 5;
					int subjectIndex =
						alertText.indexOf("Subject:", dateIndex) + 8;
					// int msgIndex = alertText.indexOf("Message:", subjectIndex)+8;

					tech = alertText.substring(techIndex, verIndex - 4).trim();
					if (tech.indexOf(",") != -1) {
						SimpleStringTokenizer tok =
							new SimpleStringTokenizer(tech, ',');
						tech_entitlement = new StringBuffer();
						while (tok.hasMoreTokens()) {
							String t = tok.nextToken().trim();
							tech_entitlement.append(ffp.getTechDKName(t));
							tech_entitlement.append(",");

						}
						tech_entitlement.deleteCharAt(
							tech_entitlement.length() - 1);
					} else {

						tech_entitlement = new StringBuffer();
						tech_entitlement.append(ffp.getTechDKName(tech));
					}

					String ver =
						alertText.substring(verIndex, dateIndex - 5).trim();
					String rdate =
						alertText.substring(dateIndex, subjectIndex - 8).trim();
					subject = alertText.substring(subjectIndex).trim();
					// String msg = alertText.substring(msgIndex);

					System.out.println(
						tech + " " + ver + " " + rdate + " " + subject);
					Date d = null;
					try {
						d = alertDate.parse(rdate);
					} catch (ParseException pe) {
						String msg =
							"Invalid date for new toolkit alert during parse. Technology: "
								+ tech_entitlement
								+ " Alert: "
								+ filelist[i]
								+ " Date: "
								+ rdate;
						message.displayMessage(msg, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for new toolkit during parse",
							msg);
						continue;
					}

					long oneDay = 24 * 3600000;
					long daysAhead = 2;
					long daysBehind = 30;
					long timeNow = System.currentTimeMillis();
					long timeSent = d.getTime();

					if (timeNow - timeSent > (daysBehind * oneDay)) {
						String msg =
							"Invalid date for new toolkit alert: old alert. Technology: "
								+ tech_entitlement
								+ ", Alert: "
								+ filelist[i]
								+ ", Date: "
								+ rdate;
						message.displayMessage(msg, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for new toolkit alert: old alert",
							msg);
						continue;
					}

					if (timeSent - timeNow > (daysAhead * oneDay)) {
						String msg =
							"Invalid date for new toolkit alert: too earlier. Technology: "
								+ tech_entitlement
								+ " Alert: "
								+ filelist[i]
								+ " Date: "
								+ rdate;
						message.displayMessage(msg, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for baseOrd alert: too earlier",
							msg);
						continue;
					}
					body = new StringBuffer();
					body.append("Dear IBM Customer Connect User,\n");
					body.append("\n");
					body.append("The new Toolkit release for version ");
					body.append(ver);
					body.append(" of technology ");
					body.append(tech);
					body.append(" is available\n");
					body.append("for download on IBM Customer Connect.\n");
					body.append("\n");
					body.append("Release Date: ");
					body.append(rdate);

					StringBuffer mqBody = new StringBuffer();

					mqBody.append("TECHNOLOGY=");
					mqBody.append(tech_entitlement.toString());
					mqBody.append(mqDelimiter);

					mqBody.append("SUBJECT=");
					mqBody.append(subject);
					mqBody.append(mqDelimiter);

					mqBody.append("MESSAGE=");
					mqBody.append(body.toString());
					mqBody.append(mqDelimiter);

					System.out.println(mqBody.toString());

					if (useMQ) {

						MQSend edmq = new MQSend(mqRBName);
						try {
							edmq.sendMQMessage(
								"EDQ3_NEW_TK",
								null,
								mqBody.toString(),
								true);
							new File(alertDir + filelist[i]).delete();
							message.displayMessage(
								"\nNew Toolkit alert for "
									+ tech
									+ " "
									+ ver
									+ " send out.",
								1);
						} catch (Throwable t) {
							message.displayMessage(
								"Error sending EDQ3_NEW_TK",
								1);
						}
					}
					message.displayMessage(
						"MQ Content:\n" + mqBody.toString(),
						3);

				} else if (
					filelist[i].startsWith("toolkitpatch_alert")) { //else if

					/*
					  Tech: CU11
					  Ver: v11.0
					  Date: 5/28/03
					  ToolKit: 
					  Platform:
					  Description: ...
					*/

					String alertText = readFile(alertDir + filelist[i]);
					System.out.println(alertText);
					int techIndex = alertText.indexOf("Tech:") + 5;
					int verIndex = alertText.indexOf("Ver:", techIndex) + 4;
					int dateIndex = alertText.indexOf("Date:", verIndex) + 5;
					int tkIndex = alertText.indexOf("ToolKit:", dateIndex) + 8;
					int platformIndex =
						alertText.indexOf("Platform:", tkIndex) + 9;
					System.out.println("platformindex " + platformIndex);
					int msgIndex =
						alertText.indexOf("Description:", platformIndex) + 12;

					tech = alertText.substring(techIndex, verIndex - 4).trim();
					System.out.println(tech);
					String ver =
						alertText.substring(verIndex, dateIndex - 5).trim();
					System.out.println(ver);
					String rdate =
						alertText.substring(dateIndex, tkIndex - 8).trim();
					System.out.println(
						rdate + " " + tkIndex + " " + platformIndex);
					String toolkit =
						alertText.substring(tkIndex, platformIndex - 9).trim();
					System.out.println(toolkit);
					String platform =
						alertText
							.substring(platformIndex, msgIndex - 12)
							.trim();
					System.out.println(platform);
					String msg = alertText.substring(msgIndex);
					if (msg.length() > 4 * 1024) {
						String err =
							"Invalid description length for new toolkit patch alert during parse. Technology: "
								+ tech
								+ ", Alert: "
								+ filelist[i];
						message.displayMessage(err, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid description length for new toolkit patch during parse",
							err);
						continue;
					}
					System.out.println(msg);
					System.out.println(
						tech
							+ " "
							+ ver
							+ " "
							+ rdate
							+ " "
							+ toolkit
							+ " "
							+ platform
							+ " "
							+ msg);
					Date d = null;
					try {
						d = alertDate.parse(rdate);
					} catch (ParseException pe) {
						String err =
							"Invalid date for new toolkit patch alert during parse. Technology: "
								+ tech
								+ ", Alert: "
								+ filelist[i]
								+ ", Date: "
								+ rdate;
						message.displayMessage(err, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for new toolkit patch during parse",
							err);
						continue;
					}

					long oneDay = 24 * 3600000;
					long daysAhead = 2;
					long daysBehind = 30;
					long timeNow = System.currentTimeMillis();
					long timeSent = d.getTime();

					if (timeNow - timeSent > (daysBehind * oneDay)) {
						String err =
							"Invalid date for new toolkit patch alert : old alert. Technology: "
								+ tech
								+ " Alert "
								+ filelist[i]
								+ " Date: "
								+ rdate;
						message.displayMessage(err, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for new toolkit alert : old alert",
							err);
						continue;
					}

					if (timeSent - timeNow > (daysAhead * oneDay)) {
						String err =
							"Invalid date for new toolkit patch alert : too earlier. Technology: "
								+ tech
								+ " Alert: "
								+ filelist[i]
								+ " Date: "
								+ rdate;
						message.displayMessage(err, 1);
						sendEmailMessage(
							FROM_ERROR,
							"hammer@us.ibm.com",
							"fyuan@us.ibm.com",
							"Invalid date for baseOrd alert : too earlier",
							err);
						continue;
					}

					StringBuffer mqBody = new StringBuffer();

					mqBody.append("TECHNOLOGY=");
					mqBody.append(tech);
					mqBody.append(mqDelimiter);

					mqBody.append("VERSION_NO=");
					mqBody.append(ver);
					mqBody.append(mqDelimiter);

					mqBody.append("RELEASE_DATE=");
					mqBody.append(rdate);
					mqBody.append(mqDelimiter);

					mqBody.append("TOOLKIT_NAME=");
					mqBody.append(toolkit);
					mqBody.append(mqDelimiter);

					mqBody.append("PLATFORM=");
					mqBody.append(platform);
					mqBody.append(mqDelimiter);

					mqBody.append("PATCH_DESC=");
					mqBody.append(msg);
					mqBody.append(mqDelimiter);

					System.out.println(mqBody.toString());

					if (useMQ) {

						MQSend edmq = new MQSend(mqRBName);
						try {
							edmq.sendMQMessage(
								"EDQ3_NEW_TOOL_PATCH",
								null,
								mqBody.toString(),
								true);
							new File(alertDir + filelist[i]).delete();

							message.displayMessage(
								"\nNew Toolkit Patch alert for "
									+ tech
									+ " "
									+ ver
									+ " send out.",
								1);
						} catch (Throwable t) {
							message.displayMessage(
								"Error sending EDQ3_NEW_TOOL_PATCH",
								1);
						}
					}
					message.displayMessage(
						"MQ Content:\n" + mqBody.toString(),
						3);

				}
			}

			//end of new for 3.7.1
		} catch (Throwable t) {
			message.displayMessage("Warning: " + t, 1);
			message.displayMessage("Warning: couldn't send email", 1);
			return false;
		}

		return true;
	}

	// new for 2.9
	private int[] getDeltaTypeAndReason(String deltaName, String file) {

		String deltaType =
			executeAndGetStream(
				"grep" + " " + deltaName + " " + file + " | cut -d ';' -f 3");

		if (deltaType == null) {
			message.displayMessage(
				"No deltaType for " + deltaName + " in " + file);
			return null;
		}

		String deltaReason =
			executeAndGetStream(
				"grep" + " " + deltaName + " " + file + " | cut -d ';' -f 2");

		if (deltaReason == null) {
			message.displayMessage(
				"No deltaReason for " + deltaName + " in " + file);
			return null;
		}

		int[] typeAndReason = { -1, -1 };
		String[] TYPES = { "BASE", "CORE", "CUSTOMER", "SPECIAL" };
		String[] REASONS =
			{ "FIX", "ENHANCEMENT", "NEW", "OTHER", "QUALIFICATION" };

		for (int i = 0; i < TYPES.length; i++) {
			if (deltaType.equals(TYPES[i])) {
				typeAndReason[0] = i + 1;
				break;
			}
		}

		if (typeAndReason[0] <= 0) {
			message.displayMessage(
				"Invaid deltaType: "
					+ deltaType
					+ " for "
					+ deltaName
					+ " in "
					+ file);
			return null;
		}

		for (int i = 0; i < REASONS.length; i++) {
			if (deltaReason.equals(REASONS[i])) {
				typeAndReason[1] = i + 1;
				break;
			}
		}

		if (typeAndReason[1] <= 0) {
			message.displayMessage(
				"Invaid deltaReason: "
					+ deltaReason
					+ " for "
					+ deltaName
					+ " in "
					+ file);
			return null;
		}

		return typeAndReason;

	}

	boolean stringAlreadyExists(String str, Vector vec) {
		int size = vec.size();
		String val;

		for (int i = 0; i < size; i++) {
			val = (String) vec.elementAt(i);
			if (val.equals(str))
				return true;
		}
		return false;
	}

	public void displayFileToTableHash() {

		message.displayMessage("***File -> Table Hash***", 3);
		Enumeration enum = fileToTableHash.keys();
		String key, value;
		Vector tables;

		while (enum.hasMoreElements()) {
			key = (String) enum.nextElement();
			message.displayMessage("Key: " + key, 3);
			tables = (Vector) fileToTableHash.get(key);

			for (int i = 0; i < tables.size(); i++) {
				value = (String) tables.elementAt(i);
				message.displayMessage("\tValue: " + value, 3);
			}

		}
	}

	public void displayTechVersions() {
		message.displayMessage("***Tech / Version Pairs***", 3);
		int numTechs = techNamesAndVersions.size();
		for (int i = 0; i < numTechs; i++) {
			message.displayMessage(
				"\t" + (String) techNamesAndVersions.elementAt(i),
				3);
		}

	}

	public void displayHash() {

		message.displayMessage("***File Hash***", 3);
		int i, numFiles;
		Enumeration enum;
		String key;
		Vector techsVec;
		Boolean bval;

		numFiles = techInfoFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) techInfoFiles.elementAt(i);
			bval = (Boolean) techInfoFilesHaveChanged.get(key);
			if (bval.booleanValue())
				message.displayMessage(
					"Key: TechInfo/"
						+ key
						+ "\n\tValue: "
						+ bval.booleanValue(),
					3);
		}

		numFiles = fpdkFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) fpdkFiles.elementAt(i);
			bval = (Boolean) fpdkFilesHaveChanged.get(key);
			if (bval.booleanValue())
				message.displayMessage(
					"Key: FullPDKit/"
						+ key
						+ "\n\tValue: "
						+ bval.booleanValue(),
					3);

		}

		/* to be de-commented for 2.10
		numFiles = xmxFiles.size();
		for (i = 0; i < numFiles; i++) {
		    key = (String) xmxFiles.elementAt(i);
		    bval = (Boolean) xmxFilesHaveChanged.get(key);
			if (bval.booleanValue())
		    message.displayMessage("Key: XMX/" + key + "\n\tValue: " + 
		                           bval.booleanValue(), 3);
		
		}
		*/

		numFiles = grmacFiles.size();
		numFiles = dszFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) dszFiles.elementAt(i);
			bval = (Boolean) dszFilesHaveChanged.get(key);
			if (bval.booleanValue())
				message.displayMessage(
					"Key: DieSizer/"
						+ key
						+ "\n\tValue: "
						+ bval.booleanValue(),
					3);

		}

		numFiles = grmacFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) grmacFiles.elementAt(i);
			bval = (Boolean) grmacFilesHaveChanged.get(key);
			if (bval.booleanValue())
				message.displayMessage(
					"Key: Grmac/" + key + "\n\tValue: " + bval.booleanValue(),
					3);

		}

		//new for 4.5.1
		numFiles = efpgaFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) efpgaFiles.elementAt(i);
			bval = (Boolean) efpgaFilesHaveChanged.get(key);
			if (bval.booleanValue())
				message.displayMessage(
					"Key: Efpga/" + key + "\n\tValue: " + bval.booleanValue(),
					3);

		}
		//end of new for 4.5.1

		numFiles = techVerFiles.size();
		for (i = 0; i < numFiles; i++) {
			key = (String) techVerFiles.elementAt(i);
			techsVec = (Vector) techVerFilesHaveChanged.get(key);
			message.displayMessage("Key: " + key, 3);
			if (techsVec.isEmpty()) {
				message.displayMessage("\tValue: Empty", 3);
			}
			for (int j = 0; j < techsVec.size(); j++) {
				message.displayMessage(
					"\tValue: " + (String) techsVec.elementAt(j),
					3);
			}
		}
	}

	public String convertToReadableName(String techVersion) {
		String name, version;
		int slash = techVersion.indexOf("/");
		name = techVersion.substring(0, slash);
		version = techVersion.substring(slash + 1, techVersion.length());
		return name + " " + version;
	}

	public void clearTables() {

		Boolean hval;
		String key, sval, fileName;
		Vector tables, techs, hasChanged, ips;

		int numTechInfoFiles = techInfoFiles.size();
		int numTechVerFiles = techVerFiles.size();
		int numIPVerFiles = ipVerFiles.size(); //new for 5.1.1
		int numToolkitFiles, i, j;

		Enumeration enum = tableToCorrelIDHash.keys();

		// Change in code logic -- Still some errors -- fix them !!
		// Due to the new foreign key constraints, all
		// tables will be deleted, whether changed or not
		// Need to revisit this implementation and find a
		// more efficient scheme

		boolean clearAll = false;

		// New Logic
		// All tables, except TOOL_DOCUMENT, have FK constraint
		// on TECHNOLOGY_MASTER table (driven by last.release.data)
		// Check to see if last.release.data was updated
		// If so, then clear all tables, incl TOOL_DOCUMENT

		for (i = 0; i < numTechInfoFiles; i++) {

			fileName = (String) techInfoFiles.elementAt(i);

			if (fileName.equals("last.release.data")) {
				hval = (Boolean) techInfoFilesHaveChanged.get(fileName);
				if (hval.booleanValue()) {
					clearAll = true;
					break;
				}
			}
		}

		if (clearAll) {

			while (enum.hasMoreElements()) {
				sval = (String) enum.nextElement();
				message.displayMessage("Clear table: " + sval, 1);

				//new for 3.7.1. don't delete delta_release_notes and path_info table
				if (sval.equals("DELTA_RELEASE_NOTES"))
					// ff2db.clearBTVTable(sval);
					message.displayMessage("Not delete table: " + sval, 1);
				else if (sval.equals("PATCH_INFO"))
					//ff2db.clearBTVTable(sval);
					message.displayMessage("Not delete table: " + sval, 1);
				//new for 3.10.1
				else if (sval.equals("RELEASE_NOTES"))
					message.displayMessage("Not delete table: " + sval, 1);
				//end of new for 3.10.1
				else if (sval.equals("REVISION_RELNOTES"))
					message.displayMessage("Not delete table: " + sval, 1);
				//added for 6.1

				else
					ff2db.deleteBTVTable(sval);
			}

			// Must be the last table to be deleted
			message.displayMessage(
				"Clear table: " + FlatFileToDB2Table.TECHNOLOGY_MASTER_TABLE,
				1);
			ff2db.deleteBTVTable(FlatFileToDB2Table.TECHNOLOGY_MASTER_TABLE);
		} else { // Delete only those tables which have changed

			for (i = 0; i < numTechInfoFiles; i++) {
				key = (String) techInfoFiles.elementAt(i);
				hval = (Boolean) techInfoFilesHaveChanged.get(key);

				if (hval.booleanValue()) {
					tables = (Vector) (fileToTableHash.get(key));
					
					if(tables != null && !tables.isEmpty() )
					{
							for (j = 0; j < tables.size(); j++) {
							sval = (String) tables.elementAt(j);
	
							message.displayMessage("Clear table: " + sval, 1);
							ff2db.deleteBTVTable(sval);
						}
					}
				}
			}

			for (i = 0; i < numTechVerFiles; i++) {

				fileName = (String) techVerFiles.elementAt(i);
				techs = (Vector) techVerFilesHaveChanged.get(fileName);

				if (!techs.isEmpty()) {

					tables = (Vector) (fileToTableHash.get(fileName));

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						//new for 3.7.1. don't delete delta_release_notes table
						if (sval.equals("DELTA_RELEASE_NOTES"))
							//ff2db.clearBTVTable(sval);
							message.displayMessage(
								"Not delete table: " + sval,
								1);
						//new for 3.10.1
						else if (sval.equals("RELEASE_NOTES"))
							message.displayMessage(
								"Not delete table: " + sval,
								1);
						//end of new for 3.10.1
						else
							ff2db.deleteBTVTable(sval);
					}

				}
			}

			//new for 5.1.1
			for (i = 0; i < numIPVerFiles; i++) {

				fileName = (String) ipVerFiles.elementAt(i);
				ips = (Vector) ipVerFilesHaveChanged.get(fileName);

				if (!ips.isEmpty()) {

					tables = (Vector) (fileToTableHash.get(fileName));

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						if (sval.equals("REVISION_RELNOTES"))
							message.displayMessage("Not delete table: " + sval, 1);
										//added for 6.1
						else
							ff2db.deleteBTVTable(sval);
					}

				}
			}
			//end of new for 5.1.1

			numToolkitFiles = fpdkFiles.size();

			for (i = 0; i < numToolkitFiles; i++) {

				key = (String) fpdkFiles.elementAt(i);
				hval = (Boolean) fpdkFilesHaveChanged.get(key);

				if (hval.booleanValue()) {
					tables = (Vector) fileToTableHash.get(key);

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						//new for 3.7.1
						// if (sval.equals("PATCH_INFO"))
						//    ff2db.clearBTVTable(sval);
						// else 
						if (!sval.equals("PATCH_INFO"))
							ff2db.deleteBTVTable(sval);
						// ff2db.clearBTVTable("PATCH_INFO");
					}
				}
			}

			/* to be de-commented for 2.10
			numToolkitFiles = xmxFiles.size();
			
			for (i = 0; i < numToolkitFiles; i++) {
			
			  key = (String) xmxFiles.elementAt(i);
			  hval = (Boolean) xmxFilesHaveChanged.get(key);
			
			  if (hval.booleanValue()) {
			
			      tables = (Vector) fileToTableHash.get(key);
			
			      for (j = 0; j < tables.size(); j++) {
			          sval = (String) tables.elementAt(j);
			          message.displayMessage("Clear table: " + sval, 1);
			          ff2db.deleteBTVTable(sval);
			      }
			  }
			}
			*/

			numToolkitFiles = dszFiles.size();

			for (i = 0; i < numToolkitFiles; i++) {

				key = (String) dszFiles.elementAt(i);
				hval = (Boolean) dszFilesHaveChanged.get(key);

				if (hval.booleanValue()) {

					tables = (Vector) fileToTableHash.get(key);

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						ff2db.deleteBTVTable(sval);
					}
				}
			}

			numToolkitFiles = grmacFiles.size();

			for (i = 0; i < numToolkitFiles; i++) {

				key = (String) grmacFiles.elementAt(i);
				hval = (Boolean) grmacFilesHaveChanged.get(key);

				if (hval.booleanValue()) {
					tables = (Vector) fileToTableHash.get(key);

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						ff2db.deleteBTVTable(sval);
					}
				}
			}

			//new for 4.5.1
			numToolkitFiles = efpgaFiles.size();

			for (i = 0; i < numToolkitFiles; i++) {

				key = (String) efpgaFiles.elementAt(i);
				hval = (Boolean) efpgaFilesHaveChanged.get(key);

				if (hval.booleanValue()) {
					tables = (Vector) fileToTableHash.get(key);

					for (j = 0; j < tables.size(); j++) {
						sval = (String) tables.elementAt(j);
						message.displayMessage("Clear table: " + sval, 1);
						ff2db.deleteBTVTable(sval);
					}
				}
			}
			//end of new for 4.5.1

		} // else
	}

	public void BTVtoBLDxfer() {
		String script = getBTVtoBLDxferScriptName();
		BTVtoBLDxferReadmeFiles(script);
		BTVtoBLDxferDataFiles(script);
	}

	String getBTVtoBLDxferScriptName() {
		return ff2dbRB.getString("BTVtoBLDdb2Xfer_script").trim();
	}

	public void BTVtoBLDxferReadmeFiles(String org_script) {

		boolean somethingChanged = false;
		boolean releaseNotesChanged = false;
		Boolean bval;
		Enumeration enum;
		Runtime r;
		Process p;
		String key, val, scriptLine, table, cid, name;
		Vector techs;
		String script = null;

		enum = techVerFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {

			name = (String) enum.nextElement();
			if ((name.indexOf("Readmes") != -1)) {
				techs = (Vector) techVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					somethingChanged = true;
					break;
				}
			}
			//new for 3.10.1
			else if ((name.indexOf("ReleaseNote") != -1)) {

				techs = (Vector) techVerFilesHaveChanged.get(name);
				if (!techs.isEmpty()) {
					releaseNotesChanged = true;
					message.displayMessage("release notes is changed", 1);
					break;
				}
			}
			//end of new for 3.10.1
		}

		enum = grmacFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {

			System.out.println(enum.toString());
			name = (String) enum.nextElement();
			System.out.println(name);
			System.out.println(grmacFilesHaveChanged.toString());
			bval = (Boolean) grmacFilesHaveChanged.get(name);
			if (bval.booleanValue()) {
				somethingChanged = true;
				break;
			}
		}

		//new for 4.5.1
		enum = efpgaFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {

			System.out.println(enum.toString());
			name = (String) enum.nextElement();
			System.out.println(name);
			System.out.println(efpgaFilesHaveChanged.toString());
			bval = (Boolean) efpgaFilesHaveChanged.get(name);
			if (bval.booleanValue()) {
				somethingChanged = true;
				break;
			}
		}
		//end of new for 4.5.1

		//new for 3.10.1
		if (releaseNotesChanged) {

			script =
				org_script + " " + FlatFileToDB2Table.RELEASE_NOTES_TABLE + " ";
			// script += (String) tableToCorrelIDHash.get(FlatFileToDB2Table.RELEASE_NOTES_TABLE);
			script += " R";

			message.displayMessage(
				"Transferring RELEASE_NOTES_TABLE from BTV -> BLD",
				1);

			try {
				message.displayMessage("Running script: " + script, 1);
				String numRowsExported = executeAndGetStream(script, true);
			} catch (Exception e) {
				message.displayMessage(
					"Error: Stacktrace: " + getStackTrace(e),
					1);
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"FlatFileToDB2Xfer: Fatal error",
					"FlatFileToDB2Xfer: Error: Stacktrace: "
						+ getStackTrace(e));
				System.exit(-1);
			}

		}
		//end of new for 3.10.1

		if (somethingChanged) {
			script =
				org_script
					+ " "
					+ FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE
					+ " ";
			//  script += (String) tableToCorrelIDHash.get(FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE);
			script += " R";

			message.displayMessage(
				"Transferring TECHNOLOGY_DOCUMENT_TABLE from BTV -> BLD",
				2);

			try {
				message.displayMessage("Running script: " + script, 2);
				String numRowsExported = executeAndGetStream(script, true);
			} catch (Exception e) {
				message.displayMessage(
					"Error: Stacktrace: " + getStackTrace(e),
					1);
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"FlatFileToDB2Xfer: Fatal error",
					"FlatFileToDB2Xfer: Error: Stacktrace: "
						+ getStackTrace(e));
				System.exit(-1);
			}

		}

		somethingChanged = false;

		enum = fpdkFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {

			name = (String) enum.nextElement();
			if ((name.indexOf("Readmes") != -1)) {
				bval = (Boolean) fpdkFilesHaveChanged.get(name);
				if (bval.booleanValue()) {
					somethingChanged = true;
					break;
				}
			}
		}

		/* to be de-commented for 2.10
		enum = xmxFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {
		
		    name = (String) enum.nextElement();
		    if ( (name.indexOf("Readmes") != -1) ) {
		            bval = (Boolean) xmxFilesHaveChanged.get(name);
		            if (bval.booleanValue()) {
		                somethingChanged = true;
		                break;
		            }
		    }
		}
		*/

		enum = dszFilesHaveChanged.keys();
		while (!somethingChanged && enum.hasMoreElements()) {

			name = (String) enum.nextElement();
			if ((name.indexOf("Readmes") != -1)) {
				bval = (Boolean) dszFilesHaveChanged.get(name);
				if (bval.booleanValue()) {
					somethingChanged = true;
					break;
				}
			}
		}

		if (somethingChanged) {
			script =
				org_script + " " + FlatFileToDB2Table.TOOL_DOCUMENT_TABLE + " ";
			// script += (String) tableToCorrelIDHash.get(FlatFileToDB2Table.TOOL_DOCUMENT_TABLE);
			script += " R";

			message.displayMessage(
				"Transferring TOOL_DOCUMENT_TABLE from BTV -> BLD",
				2);

			try {
				message.displayMessage("Running script: " + script, 2);
				String numRowsExported = executeAndGetStream(script, true);
			} catch (Exception e) {
				message.displayMessage(
					"Error: Stacktrace: " + getStackTrace(e),
					1);
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"FlatFileToDB2Xfer: Fatal error",
					"FlatFileToDB2Xfer: Error: Stacktrace: "
						+ getStackTrace(e));
				System.exit(-1);
			}

		}
	}

	public void BTVtoBLDxferDataFiles(String script) {

		Boolean bval;
		Runtime r;
		Process p;
		String key, val, scriptLine, table, cid;
		Vector techs, vec, ips;
		boolean transferredPlatformsTable = false;

		int numTechInfoFiles = techInfoFiles.size();
		int numTechVerFiles = techVerFiles.size();
		int numIPVerFiles = ipVerFiles.size(); //new for 5.1.1
		int numToolFiles, i, j;

		for (i = 0; i < numTechInfoFiles; i++) {

			key = (String) techInfoFiles.elementAt(i);
			bval = (Boolean) techInfoFilesHaveChanged.get(key);

			if (bval.booleanValue()) {
				vec = (Vector) fileToTableHash.get(key);
				if( vec != null && !vec.isEmpty() )
				{
				for (j = 0; j < vec.size(); j++) {

					table = (String) vec.elementAt(j);
					cid = (String) tableToCorrelIDHash.get(table);

					scriptLine = script + " " + table + " T";
					//            + cid
					//   " T";

					message.displayMessage(
						"Transferring " + table + " from BTV -> BLD",
						2);

					try {
						message.displayMessage(
							"Running script: " + scriptLine,
							2);
						String numRowsExported =
							executeAndGetStream(scriptLine, true);
					} catch (Exception e) {
						message.displayMessage(
							"Error: Stacktrace: " + getStackTrace(e),
							1);
						sendEmailMessage(
							FROM_ERROR,
							errMailTo,
							errMailCc,
							"FlatFileToDB2Xfer: Fatal error",
							"FlatFileToDB2Xfer: Error: Stacktrace: "
								+ getStackTrace(e));
						System.exit(-1);
					}
				}
				}
			}
		}

		for (i = 0; i < numTechVerFiles; i++) {

			key = (String) techVerFiles.elementAt(i);
			if (key.indexOf("Readme") == -1
				&& key.indexOf("ReleaseNote") == -1) {
				// no readmes or MajorReleaseNote!

				techs = (Vector) techVerFilesHaveChanged.get(key);
				if (!techs.isEmpty()) {
					vec = (Vector) fileToTableHash.get(key);

					for (j = 0; j < vec.size(); j++) {
						scriptLine = script + " ";
						table = (String) vec.elementAt(j);

						if (transferredPlatformsTable
							&& table.equals(FlatFileToDB2Table.PLATFORMS_TABLE)) {
							if (debug) {
								message.displayMessage(
									"NOT transferring " + table,
									1);
							}
							continue;
						}

						cid = (String) tableToCorrelIDHash.get(table);
						scriptLine += table + " ";
						// scriptLine += cid;
						//new for 3.7.1
						if (table
							.equals(
								FlatFileToDB2Table.DELTA_RELEASE_NOTES_TABLE))
							scriptLine += " R";
						else
							//end of new for 3.7.1   
							scriptLine += " T";

						message.displayMessage(
							"Transferring " + table + " from BTV -> BLD",
							2);

						try {
							message.displayMessage(
								"Running script: " + scriptLine,
								2);
							String numRowsExported =
								executeAndGetStream(scriptLine, true);
							if (numRowsExported != null
								&& table.equals(
									FlatFileToDB2Table.PLATFORMS_TABLE)) {
								transferredPlatformsTable = true;
							}
						} catch (Exception e) {
							message.displayMessage(
								"Error: Stacktrace: " + getStackTrace(e),
								1);
							sendEmailMessage(
								FROM_ERROR,
								errMailTo,
								errMailCc,
								"FlatFileToDB2Xfer: Fatal error",
								"FlatFileToDB2Xfer: Error: Stacktrace: "
									+ getStackTrace(e));
							System.exit(-1);
						}
					}
				}
			}
		}

		//new for 5.1.1

		for (i = 0; i < numIPVerFiles; i++) {

			key = (String) ipVerFiles.elementAt(i);

			ips = (Vector) ipVerFilesHaveChanged.get(key);
			if (!ips.isEmpty()) {
				vec = (Vector) fileToTableHash.get(key);

				for (j = 0; j < vec.size(); j++) {
					scriptLine = script + " ";
					table = (String) vec.elementAt(j);
					//  cid = (String) tableToCorrelIDHash.get(table);
					scriptLine += table + " ";
					if (table.equals(FlatFileToDB2Table.RELEASE_NOTES_TABLE)
						|| table.equals(
							FlatFileToDB2Table.TECHNOLOGY_DOCUMENT_TABLE))
						scriptLine += " R";
					else
						scriptLine += " T";

					message.displayMessage(
						"Transferring " + table + " from BTV -> BLD",
						1);

					try {
						message.displayMessage(
							"Running script: " + scriptLine,
							1);
						String numRowsExported =
							executeAndGetStream(scriptLine, true);
					} catch (Exception e) {
						message.displayMessage(
							"Error: Stacktrace: " + getStackTrace(e),
							1);
						sendEmailMessage(
							FROM_ERROR,
							errMailTo,
							errMailCc,
							"FlatFileToDB2Xfer: Fatal error",
							"FlatFileToDB2Xfer: Error: Stacktrace: "
								+ getStackTrace(e));
						System.exit(-1);
					}
				}
			}
		}

		//end of new for 5.1.1

		numToolFiles = fpdkFiles.size();

		for (i = 0; i < numToolFiles; i++) {

			key = (String) fpdkFiles.elementAt(i);
			if (key.indexOf("Readme") == -1) { // no readmes!

				bval = (Boolean) fpdkFilesHaveChanged.get(key);

				if (bval.booleanValue()) {

					vec = (Vector) fileToTableHash.get(key);

					for (j = 0; j < vec.size(); j++) {

						scriptLine = script + " ";
						table = (String) vec.elementAt(j);

						if (transferredPlatformsTable
							&& table.equals(FlatFileToDB2Table.PLATFORMS_TABLE)) {
							if (debug) {
								message.displayMessage(
									"NOT transferring " + table,
									1);
							}
							continue;
						}
						if (!xfrPatchInfo
							&& table.equals(
								FlatFileToDB2Table.PATCH_INFO_TABLE)) {
							if (debug) {
								message.displayMessage(
									"NOT tansferring " + table,
									1);
							}
							continue;
						}

						cid = (String) tableToCorrelIDHash.get(table);
						scriptLine += table + " ";
						// scriptLine += cid;
						scriptLine += " T";

						message.displayMessage(
							"Transferring " + table + " from BTV -> BLD",
							2);

						try {
							message.displayMessage(
								"Running script: " + scriptLine,
								2);
							String numRowsExported =
								executeAndGetStream(scriptLine, true);
							if (numRowsExported != null
								&& table.equals(
									FlatFileToDB2Table.PLATFORMS_TABLE)) {
								transferredPlatformsTable = true;
							}

						} catch (Exception e) {
							message.displayMessage(
								"Error: Stacktrace: " + getStackTrace(e),
								1);
							sendEmailMessage(
								FROM_ERROR,
								errMailTo,
								errMailCc,
								"FlatFileToDB2Xfer: Fatal error",
								"FlatFileToDB2Xfer: Error: Stacktrace: "
									+ getStackTrace(e));
							System.exit(-1);
						}
					}
				}
			}
		}

		/* to be de-commented for 2.10
		numToolFiles = xmxFiles.size();
		
		for (i = 0; i < numToolFiles; i++) {
		
		    key = (String) xmxFiles.elementAt(i);
		    if (key.indexOf("Readme") == -1) {                 // no readmes!
		
		        bval = (Boolean) xmxFilesHaveChanged.get(key);
		
		        if (bval.booleanValue()) {
		
		            vec = (Vector) fileToTableHash.get(key);
		    
		            for (j = 0; j < vec.size(); j++) {
		
		                scriptLine = script + " ";
		                table = (String) vec.elementAt(j);
		
			if(transferredPlatformsTable && table.equals(FlatFileToDB2Table.PLATFORMS_TABLE)) {
			    if(debug) {
		                	message.displayMessage("NOT transferring " + table, 1);
			    }
			    continue;
			}
		
		                cid = (String) tableToCorrelIDHash.get(table);
		                scriptLine += table + " ";
		                scriptLine += cid;
		                scriptLine += " T";
		
		                message.displayMessage("Transferring " + table + " from BTV -> BLD", 2);
		
		
		                try {
		                    message.displayMessage("Running script: " + scriptLine, 2);
		                    String numRowsExported = executeAndGetStream(scriptLine, true);
		                    if (numRowsExported != null && table.equals(FlatFileToDB2Table.PLATFORMS_TABLE)) {
				transferredPlatformsTable = true;
			    }
		                }
		                catch (Exception e) {
		                    message.displayMessage("Error: Stacktrace: " + getStackTrace(e), 1);
		                    sendEmailMessage(FROM_ERROR, errMailTo, errMailCc, "FlatFileToDB2Xfer: Fatal error", "FlatFileToDB2Xfer: Error: Stacktrace: " + getStackTrace(e));
		                    System.exit(-1);
		                }
		            }
		        }
		    }
		}
		*/
		//new for 3.7.1 -- for PATCH_INFO only
		if (!this.xfrPlatformsPatch && this.xfrPatchInfo) {
			scriptLine = script + " ";
			table = FlatFileToDB2Table.PATCH_INFO_TABLE;

			cid = (String) tableToCorrelIDHash.get(table);
			scriptLine += table + " ";
			// scriptLine += cid;
			scriptLine += " T";

			message.displayMessage(
				"Transferring " + table + " from BTV -> BLD",
				2);

			try {
				message.displayMessage("Running script: " + scriptLine, 2);
				String numRowsExported = executeAndGetStream(scriptLine, true);

			} catch (Exception e) {
				message.displayMessage(
					"Error: Stacktrace: " + getStackTrace(e),
					1);
				sendEmailMessage(
					FROM_ERROR,
					errMailTo,
					errMailCc,
					"FlatFileToDB2Xfer: Fatal error",
					"FlatFileToDB2Xfer: Error: Stacktrace: "
						+ getStackTrace(e));
				System.exit(-1);

			}

		}
		//end of new for 3.7.1

		numToolFiles = dszFiles.size();

		for (i = 0; i < numToolFiles; i++) {

			key = (String) dszFiles.elementAt(i);
			if (key.indexOf("Readme") == -1) { // no readmes!

				bval = (Boolean) dszFilesHaveChanged.get(key);

				if (bval.booleanValue()) {

					vec = (Vector) fileToTableHash.get(key);

					for (j = 0; j < vec.size(); j++) {

						scriptLine = script + " ";
						table = (String) vec.elementAt(j);

						if (transferredPlatformsTable
							&& table.equals(FlatFileToDB2Table.PLATFORMS_TABLE)) {
							if (debug) {
								message.displayMessage(
									"NOT transferring " + table,
									1);
							}
							continue;
						}

						cid = (String) tableToCorrelIDHash.get(table);
						scriptLine += table + " ";
						//  scriptLine += cid;
						scriptLine += " T";

						message.displayMessage(
							"Transferring " + table + " from BTV -> BLD",
							2);

						try {
							message.displayMessage(
								"Running script: " + scriptLine,
								2);
							String numRowsExported =
								executeAndGetStream(scriptLine, true);
							if (numRowsExported != null
								&& table.equals(
									FlatFileToDB2Table.PLATFORMS_TABLE)) {
								transferredPlatformsTable = true;
							}
						} catch (Exception e) {
							message.displayMessage(
								"Error: Stacktrace: " + getStackTrace(e),
								1);
							sendEmailMessage(
								FROM_ERROR,
								errMailTo,
								errMailCc,
								"FlatFileToDB2Xfer: Fatal error",
								"FlatFileToDB2Xfer: Error: Stacktrace: "
									+ getStackTrace(e));
							System.exit(-1);
						}
					}
				}
			}
		}
	}

	public void showStartMessage() {
		message.displayMessage("FlatFileToDB2Xfer begin: " + new Date(), 1);
	}

	public void showEndMessage() {
		message.displayMessage(
			"FlatFileToDB2Xfer end:   "
				+ new Date()
				+ "\n-----------------------------------------------------\n",
			1);
	}

	public static String getDateAndTime() {
		StringBuffer dateTime = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int hour, min;

		dateTime.append(cal.get(Calendar.MONTH) + 1);
		dateTime.append("/");
		dateTime.append(cal.get(Calendar.DATE));
		dateTime.append("/");
		dateTime.append(cal.get(Calendar.YEAR));
		dateTime.append(" ");

		hour = cal.get(Calendar.HOUR);
		if (hour == 0)
			hour = 12;

		dateTime.append(hour);
		dateTime.append(":");
		min = cal.get(Calendar.MINUTE);

		if (min < 10)
			dateTime.append("0");

		dateTime.append(min);

		if (cal.get(Calendar.AM_PM) == Calendar.AM)
			dateTime.append(" AM");
		else
			dateTime.append(" PM");

		return dateTime.toString();
	}

	public static void main(String[] args) {

		if (args.length != 3) {
			MessageDisplay.displayMessage(
				"\nWhoops!  You didn't supply the properties files.\n"
					+ "Usage: java FlatFileToDB2Xfer <ff2db> <db2> <mq>\n");
			System.exit(1);
		}

		FlatFileToDB2Xfer ffx = new FlatFileToDB2Xfer(args);

		ffx.showStartMessage();
		ffx.fillInHashtables();
		// Check all flat files to see which ones have changed
		ffx.displayTechVersions();
		// ffx.displayFileToTableHash();
		ffx.displayHash();
		ffx.clearTables(); // Clear DB2 tables affected by changed flat files
		ffx.updateFilesAndTransferTables(); // Insert new info into tables
		try {
			ff2db.conn.close();
		} catch (java.sql.SQLException e) {
			message.displayMessage("Error: Stacktrace: " + getStackTrace(e), 1);
		}
		ffx.BTVtoBLDxfer(); // Transfer modified DB2 tables from BTV to BLD
		ffx.sendAlerts(); // Handle any alerts
		ffx.showEndMessage();
	}
}
