package oem.edge.ed.sd.ff2db;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/*******************************************************/
/* File: FlatFileToDB2Table.java                       */
/* Author: Athar Tayyab / Jesse Vitrone                */
/* Date: 8/31/00                                       */
/* Updates:                                            */
/*      01/07/01                                       */
/*      03/14/01                                       */
/* SET TABS to 4 for proper viewing/printing           */
/*******************************************************/

import java.util.Date;
import java.util.Calendar;
import java.util.Vector;
import java.text.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import java.io.*;

import oem.edge.ed.sd.util.*;

import java.sql.*;

import COM.ibm.db2.app.*; // StoredProc and associated classes

// import COM.ibm.db2.jdbc.net.*;  // DB2 UDB JDBC classes

// May not need it ??
// import ibm.scofor.*;
// For use of logging functions
// import ibm.sdonwww.phase2.EdgeLog;

/*****************************************************************************/
/* Some comments that apply to all the parsing:                              */
/*      All files and return values are all on one line, and ';' delimited   */
/*      The available technologies are:                                      */
/*            CU11 | SA27E | SA12E | SA27 | SA12 | CMOS5SE                   */
/*****************************************************************************/

public class FlatFileToDB2Table {

	public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
	private static final boolean debug = true;
	static final int MAX_BATCH_COUNT = 1000;

	BufferedReader flatFile;
	char delim;
	MessageDisplay message;
	String line;
	SimpleStringTokenizer tok;

	String URL,
		db2User,
		password,
		jdbcDriverClassName,
		db2SchemaName,
		databaseName,
		db2userid,
		db2password,
		uid,
		pwd;

	//	COM.ibm.db2.jdbc.app.DB2Connection conn;
	Connection conn;
	Statement sqlStatement;
	PreparedStatement preparedStmt;

	static final SimpleDateFormat db2Formatter =
		new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS000");
	static final SimpleDateFormat dateFormatter =
		new SimpleDateFormat("MM/dd/yy");
	static final SimpleDateFormat timeFormatter =
		new SimpleDateFormat("HH:mm:ss");
	static final int READ_WRITE_ERROR = 0;
	static final int FILE_NOT_FOUND = 1;
	static final int NO_MORE_TOKENS = 2;
	static final int UNKNOWN_TECHNOLOGY = 3;
	static final int SUCCESS = 4;
	static final int FAILURE = -1;
	static final int UNKNOWN_LIST = 7;

	static final String SCHEMA = "EDESIGN";

	static final String TECHNOLOGY_MASTER_TABLE = "TECHNOLOGY_MASTER";
	static final String TECHNOLOGY_VERSION_TABLE = "TECHNOLOGY_VERSION";
	static final String ASIC_CODENAME_TABLE = "ASIC_CODENAME";
	static final String TECHNOLOGY_DOCUMENT_TABLE = "TECHNOLOGY_DOCUMENT";
	static final String TOOL_DOCUMENT_TABLE = "TOOL_DOCUMENT";
	static final String PLATFORMS_TABLE = "PLATFORMS_NEW";

	// new for 3.7.1
	static final String PLATFORMS_PATCH_TABLE = "TOOLKIT_PATCH";
	static final String PATCH_INFO_TABLE = "PATCH_INFO";
	static final String DELTA_RELEASE_NOTES_TABLE = "DELTA_RELEASE_NOTES";
	// end of new for 3.7.1

	static final String GA_CORES_TABLE = "GA_CORES_NEW";
	static final String DELTA_RELEASES_TABLE = "DELTA_RELEASES";
	static final String DELTA_RELEASE_MDL_TYPES_TABLE =
		"DELTA_RELEASE_MDL_TYPES";
	static final String DELTA_RELEASE_LIB_GROUPS_TABLE =
		"DELTA_RELEASE_LIB_GROUPS";
	static final String DELTA_PACKETS_TABLE = "DELTA_PACKETS";
	static final String ORDERABLE_MDL_TYPES_TABLE = "ORDERABLE_MDL_TYPES";
	static final String ORDERABLE_LIB_GROUPS_TABLE = "ORDERABLE_LIB_GROUPS";
	static final String RESTRICTED_CORES_TABLE = "RESTRICTED_CORES_NEW";
	static final String NON_STANDARD_DELIVERABLES_TABLE =
		"NONSTANDARD_DELIVERABLES";

	static final String KIT_INFO_TABLE = "KIT_INFO";
	static final String HALT_SHIPPING_TABLE = "HALT_SHIPPING";

	// new for 2.9
	static final String NSD_GA_TABLE = "NSD_GA";
	static final String CUSTOMER_TYPES_TABLE = "CUSTOMER_TYPES";

	// new for 2.9 fix-pack
	static final String GA_BASE_ORD_TABLE = "GA_BASE_ORD";
	static final String RESTRICTED_BASE_ORD_TABLE = "RESTRICTED_BASE_ORD";

	// new for 3.10.1
	static final String RELEASE_NOTES_TABLE = "RELEASE_NOTES";
	static final String PLATFORM_MDL_TYPES_TABLE = "PLATFORM_MDL_TYPES";
	// end of new for 3.10.1

	//new for 4.2.1
	static final String PACKETS_MDL_TYPE_TABLE = "PACKETS_MDL_TYPE";
	static final String PACKETS_LIB_GROUP_TABLE = "PACKETS_LIB_GROUP";
	//end of new for 4.2.1

	//new for 5.1.1
	static final String IP_ENTITLEMENT_TABLE = "IP_ENTITLEMENT";

	static final String DA_MODELTYPES_TABLE = "DA_MODELTYPES";
	//end of new for 5.1.1

	//new for 5.4.1
	static final String IP_CONTENT_TABLE = "IP_CONTENT";
	//end of new for 5.4.1

	//	new for 6.1.1
	static final String REVISION_RELNOTES_TABLE = "REVISION_RELNOTES";
	static final String REVISION_LIST_TABLE = "REVISION_LIST";
	//end of new for 6.1.1

	public FlatFileToDB2Table(ResourceBundle db2RB, ResourceBundle ff2dbRB) {

		delim = ';';
		this.message =
			new MessageDisplay(
				ff2dbRB,
				"FlatFileToDB2Table",
				MessageDisplay.LOG_AND_DISPLAY,
				"ff2db");

		db2Formatter.setCalendar(Calendar.getInstance());

		try { // Read DB2 specifics

			URL = db2RB.getString("db2connect").trim();
			db2User = db2RB.getString("db2userid").trim();
			password = db2RB.getString("db2password").trim();
			jdbcDriverClassName = db2RB.getString("jdbcDriverClassName").trim();
			db2SchemaName = db2RB.getString("db2SchemaName").trim();
			db2userid = db2RB.getString("db2userid").trim();
			db2password = db2RB.getString("db2password").trim();
			databaseName = db2RB.getString("databaseName").trim();
			uid = db2RB.getString("userid").trim();
			pwd = db2RB.getString("password").trim();
			message.displayMessage(
				"User "
					+ db2User
					+ " PWD "
					+ " "
					+ URL
					+ " "
					+ jdbcDriverClassName
					+ " "
					+ db2SchemaName
					+ " "
					+ databaseName
					+ " "
					+ uid
					+ " ",
				1);
		} catch (Exception e) {
			e.printStackTrace();
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(e),
				1);
			sendErrorMail(e);
			System.exit(-1);
		}

		try { // Connect to DB2 server

			Driver driver =
				(Driver) Class.forName(jdbcDriverClassName).newInstance();
			DriverManager.registerDriver(driver);
			if (db2User == null) {
				// conn = (COM.ibm.db2.jdbc.app.DB2Connection) DriverManager.getConnection(URL);
				conn = DriverManager.getConnection(URL);
			} else {
				// conn = (COM.ibm.db2.jdbc.app.DB2Connection) DriverManager.getConnection(URL, db2User, password);
				conn = DriverManager.getConnection(URL, db2User, password);
				System.out.println("Hooray!!");
			}
			// sqlStatement = (COM.ibm.db2.jdbc.app.DB2Statement) conn.createStatement();
			sqlStatement = conn.createStatement();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			System.exit(-5);
		}

		// 		whichTable.put(TECHNOLOGY_VERSION_TABLE, "EDESIGN.TECHNOLOGY_VERSION");
		// 		whichTable.put(ASIC_CODENAME_TABLE, "EDESIGN.ASIC_CODENAME");
		// 		whichTable.put(TECHNOLOGY_DOCUMENT_TABLE, "EDESIGN.TECHNOLOGY_DOCUMENT_TABLE");
		// 		whichTable.put(GA_CORES_TABLE, "EDESIGN.GA_CORES");
		// 		whichTable.put(DELTA_RELEASES_TABLE, "EDESIGN.DELTA_RELEASES");
		// 		whichTable.put(RESTRICTED_CORES_TABLE, "EDESIGN.RESCTRICTED_CORES");
		// 		whichTable.put(NON_STANDARD_DELIVERABLES_TABLE, "EDESIGN.NON_STANDARD_DELIVERABLES");
	}

	private void sendErrorMail(Throwable t) {
		FlatFileToDB2Xfer.sendErrorMail(
			"Exception in FlatFileToDB2Table",
			"Exception in FlatFileToDB2Table: Stacktrace:\n"
				+ getStackTrace(t));
	}

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
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

	private String getMktName(String technology) {

		String mktName = (String) FlatFileParser.techMktName.get(technology);

		if (mktName == null)
			return technology;
		else
			return mktName;

	}

	public int transferToTechnologyDocumentTable(File tech_doc) {

		StringBuffer sBuff = new StringBuffer("connect to ");
		sBuff.append(databaseName);
		sBuff.append(" user ");
		sBuff.append(db2userid);
		sBuff.append(" using ");
		sBuff.append(db2password);
		//	sBuff.append(db2SchemaName);
		sBuff.append(";\n");
		sBuff.append("import from ");
		sBuff.append(tech_doc.getPath());
		sBuff.append(" of del lobs from ");
		sBuff.append(tech_doc.getParent());
		// sBuff.append("/ modified by lobsinfile insert into EDESIGN.TECHNOLOGY_DOCUMENT;\n");
		sBuff.append(
			"/Readmes/ modified by lobsinfile insert into EDESIGN.TECHNOLOGY_DOCUMENT;\n");
		sBuff.append("connect reset;");

		String script = new String(tech_doc.getParent() + "/tech_doc_script");

		message.displayMessage("db2 -tvf " + script, 2);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(script));
			out.write(sBuff.toString());
			out.close();

			String cmdLine = "db2 -tvf " + script;
			message.displayMessage("Executing: " + cmdLine, 2);
			int exitVal = execute(cmdLine);

			if (exitVal != 0)
				message.displayMessage(
					"Error: " + cmdLine + " returned " + exitVal,
					1);
		} catch (Exception e) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(e),
				1);
			sendErrorMail(e);
			return FAILURE;
		}

		return SUCCESS;
	}

	//new for 3.10.1

	public int transferToMajorReleaseNoteTable(
		String tech,
		String version,
		String filepath) {

		String technology;
		try {
			String timestamp = db2Formatter.format(new Date());
			technology = getMktName(tech);
			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("DELETE FROM EDESIGN.RELEASE_NOTES ");
			sqlbuff.append(
				"WHERE TECHNOLOGY= '"
					+ technology
					+ "' AND VERSION_NO= '"
					+ version
					+ "'");
			message.displayMessage(sqlbuff.toString(), 1);
			sqlStatement.executeUpdate(sqlbuff.toString());

			// message.displayMessage("here "+technology, 1);
			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.RELEASE_NOTES ( TECHNOLOGY, VERSION_NO, KIT_TYPE, RELEASE_NOTE, ADD_DEL_INDICATOR,LAST_USERID, LAST_TIMESTAMP) VALUES ('"
						+ technology
						+ "', '"
						+ version
						+ "',?,?, 'A','"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			String[] filetype = { "PK", "DK" };
			for (int i = 0; i < filetype.length; i++) {

				pstmt.setString(1, filetype[i]);

				File rel_note =
					new File(filepath + filetype[i] + "ReleaseNote");
				InputStream in = new FileInputStream(rel_note);
				pstmt.setBinaryStream(2, in, (int) rel_note.length());
				pstmt.executeUpdate();
			}

		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}
		return SUCCESS;
	}
	//end of new for 3.10.1

	//new for 3.7.1
	public int transferToReleaseNotesTable(File release_note) {

		StringBuffer sBuff = new StringBuffer("connect to ");
		sBuff.append(databaseName);
		sBuff.append(" user ");
		sBuff.append(db2userid);
		sBuff.append(" using ");
		sBuff.append(db2password);
		//	sBuff.append(db2SchemaName);
		sBuff.append(";\n");
		sBuff.append("import from ");
		sBuff.append(release_note.getPath());
		sBuff.append(" of del lobs from ");
		sBuff.append(release_note.getParent());
		// sBuff.append("/ modified by lobsinfile insert into EDESIGN.TECHNOLOGY_DOCUMENT;\n");
		sBuff.append(
			"/ReleaseNotes/ modified by lobsinfile insert_update into EDESIGN.DELTA_RELEASE_NOTES;\n");
		sBuff.append("connect reset;");

		String script =
			new String(release_note.getParent() + "/release_note_script");

		message.displayMessage("db2 -tvf " + script, 2);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(script));
			out.write(sBuff.toString());
			out.close();

			String cmdLine = "db2 -tvf " + script;
			message.displayMessage("Executing: " + cmdLine, 2);
			int exitVal = execute(cmdLine);

			if (exitVal != 0)
				message.displayMessage(
					"Error: " + cmdLine + " returned " + exitVal,
					1);
		} catch (Exception e) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(e),
				1);
			sendErrorMail(e);
			return FAILURE;
		}

		return SUCCESS;
	}

	public int transferToPatchInfoTable(File patch_info) {

		StringBuffer sBuff = new StringBuffer("connect to ");
		sBuff.append(databaseName);
		sBuff.append(" user ");
		sBuff.append(db2userid);
		sBuff.append(" using ");
		sBuff.append(db2password);
		//	sBuff.append(db2SchemaName);
		sBuff.append(";\n");
		sBuff.append("import from ");
		sBuff.append(patch_info.getPath());
		sBuff.append(" of del lobs from ");
		sBuff.append(patch_info.getParent());
		// sBuff.append("/ modified by lobsinfile insert into EDESIGN.TECHNOLOGY_DOCUMENT;\n");
		sBuff.append(
			"/PatchInfo/ modified by lobsinfile insert_update into EDESIGN.PATCH_INFO;\n");
		sBuff.append("connect reset;");

		String script =
			new String(patch_info.getParent() + "/patch_info_script");

		message.displayMessage("db2 -tvf " + script, 2);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(script));
			out.write(sBuff.toString());
			out.close();

			String cmdLine = "db2 -tvf " + script;
			message.displayMessage("Executing: " + cmdLine, 2);
			int exitVal = execute(cmdLine);

			if (exitVal != 0)
				message.displayMessage(
					"Error: " + cmdLine + " returned " + exitVal,
					1);
		} catch (Exception e) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(e),
				1);
			sendErrorMail(e);
			return FAILURE;
		}

		return SUCCESS;

	}

	//end of new for 3.7.1

	public int transferToToolDocumentTable(File tool_doc) {

		StringBuffer sBuff = new StringBuffer("connect to ");
		sBuff.append(databaseName);
		sBuff.append(" user ");
		sBuff.append(db2userid);
		sBuff.append(" using ");
		sBuff.append(db2password);
		sBuff.append(";\n");
		sBuff.append("import from ");
		/*                       Copyright Header Check                             */
		/*   --------------------------------------------------------------------   */
		/*                                                                          */
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
		sBuff.append(tool_doc.getPath());
		sBuff.append(" of del lobs from ");
		sBuff.append(tool_doc.getParent());
		sBuff.append(
			"/Readmes/ modified by lobsinfile insert into EDESIGN.TOOL_DOCUMENT;\n");
		sBuff.append("connect reset;");

		String script = new String(tool_doc.getParent() + "/tool_doc_script");

		message.displayMessage("db2 -tvf " + script, 2);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(script));
			out.write(sBuff.toString());
			out.close();

			String cmdLine = "db2 -tvf " + script;
			message.displayMessage("Executing: " + cmdLine, 2);
			int exitVal = execute(cmdLine);

			if (exitVal != 0)
				message.displayMessage(
					"Error: " + cmdLine + " returned " + exitVal,
					1);
		} catch (Exception e) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(e),
				1);
			sendErrorMail(e);
			return FAILURE;
		}

		return SUCCESS;
	}

	public int deleteBTVTable(String tableName) {

		try {
			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("DELETE FROM ");
			sqlbuff.append(SCHEMA + ".");
			sqlbuff.append(tableName);
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* ROLE                                                         */
	/* VERSION_COUNT                                                */
	/* VERSION_NO                                                   */
	/* FCS_DATE                                                     */
	/* PM_NAME                                                      */
	/* PM_EMAIL                                                     */
	/****************************************************************/

	public int transferToTechnologyVersionTable(String inputFileName) {

		String pmName, pmEmail, versionNo, vc, fcsDate, technology, role;
		String prevTech, dontCare, prevVer;
		int versionCount;

		// The TECHNOLOGY_MASTER table must be initialized FIRST
		// as it is the master table for all foreign keys

		try {

			prevTech = "";
			prevVer = "";

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				tok.nextToken();
				tok.nextToken();
				versionNo = tok.nextToken().trim();

				// Cannot insert same technology and version into Technology_Master
				// Table, since it is the master table for all foreign keys.
				// Assume input flatFile is sorted by technology and does not have
				// duplicate technology/version pairs
				message.displayMessage(
					technology
						+ " "
						+ versionNo
						+ " "
						+ " "
						+ prevTech
						+ " "
						+ prevVer,
					1);
				if (technology.equals(prevTech) && versionNo.equals(prevVer))
					continue;
				else {
					prevTech = technology;
					prevVer = versionNo; //new for 6.1.1
				}

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 1);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();

		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		// Insert rows for FULL_PDK, DIESIZER, XMX, and RAMRA, EFPGA each time this table is updated
		// Insert rows for EDESIGN each time this table is updated

		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append(
				"VALUES ( 'FULL_PDK', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 1);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append(
				"VALUES ( 'DIESIZER', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append("VALUES ( 'XMX', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append(
				"VALUES ( 'RAMRA', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}
		//new for 4.5.1
		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append(
				"VALUES ( 'EFPGA', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}
		//end of new for 4.5.1

		try {

			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_MASTER ");
			sqlbuff.append(
				"VALUES ( 'EDESIGN', 'v1.0', user, current timestamp )");
			message.displayMessage(sqlbuff.toString(), 2);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		// Now the Technology_Version Table ...

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));
			String techType = ""; //new for 5.1.1
			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				role = tok.nextToken().trim();
				vc = tok.nextToken().trim();
				versionNo = tok.nextToken().trim();
				fcsDate = tok.nextToken().trim();
				Date fcsDateObject = dateFormatter.parse(fcsDate);
				java.sql.Date sqlDate =
					new java.sql.Date(fcsDateObject.getTime());

				pmName = tok.nextToken().trim();
				pmEmail = tok.nextToken().trim();
				techType = tok.nextToken().trim(); //new for 5.1.1

				versionCount = Integer.valueOf(vc).intValue();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.TECHNOLOGY_VERSION ");

				sqlbuff.append(
					"(TECHNOLOGY, ROLE, VERSION_COUNT, VERSION_NO, FCS_DATE, PM_NAME, PM_EMAIL, LAST_USERID, LAST_TIMESTAMP, TECH_TYPE)VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + role + "',");
				sqlbuff.append("" + versionCount + ",");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + sqlDate + "',");
				sqlbuff.append("'" + pmName + "',");
				sqlbuff.append("'" + pmEmail + "',");
				sqlbuff.append("user, current timestamp, '" + techType + "')");
				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		String f = null;
		try {
			f =
				inputFileName.substring(0, inputFileName.lastIndexOf('/'))
					+ "/.tmp";
			FileOutputStream out = new FileOutputStream(f);
			//   out.write(pwd.getBytes());
			out.write(password.getBytes());
			out.close();
		} catch (Throwable t) {
			message.displayMessage(
				"Error writing to " + f + ": Stacktrace: \n" + getStackTrace(t),
				1);
		}

		return SUCCESS;

	}

	public int transferToHaltShippingTable(String inputFileName) {

		try {

			String technology, versionNumber, kitType, description;

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = tok.nextToken().trim();
				versionNumber = tok.nextToken().trim();
				kitType = tok.nextToken().trim();
				description = tok.nextToken().trim().replace('^', '\n');

				if (!kitType.equals("DK")
					&& !kitType.equals("PK")
					&& !kitType.equals("DR")
					&& !kitType.equals("MEM")
					&& !kitType.equals("FPD")
					&& !kitType.equals("SUPER_ONLY")
					&& !kitType.equals("EFPGA")) {
					message.displayMessage(
						"kitType field for entry: "
							+ technology
							+ " "
							+ versionNumber
							+ " "
							+ kitType
							+ " is invalid. Not propagating entry",
						1);
					continue;
				}

				if (description.length() > 1024) {
					message.displayMessage(
						"Description field for entry: "
							+ technology
							+ " "
							+ versionNumber
							+ " "
							+ kitType
							+ " exceeds 1024 characters. Trimming to 1024 characters.",
						1);
					description = description.substring(0, 1023);
				}

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.HALT_SHIPPING ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, KIT_TYPE, DESCRIPTION, LAST_USERID, LAST_TIMESTAMP ) ");
				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNumber + "',");
				sqlbuff.append("'" + kitType + "',");
				sqlbuff.append("'" + description + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();

		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	public int transferToKitInfoTable(String inputFileName) {

		try {

			String technology, versionNumber, kitType, description;

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = tok.nextToken().trim();
				versionNumber = tok.nextToken().trim();
				kitType = tok.nextToken().trim();
				description = tok.nextToken().trim().replace('^', '\n');

				if (!kitType.equals("DK")
					&& !kitType.equals("PK")
					&& !kitType.equals("DR")
					&& !kitType.equals("MEM")
					&& !kitType.equals("FPD")) {
					message.displayMessage(
						"kitType field for entry: "
							+ technology
							+ " "
							+ versionNumber
							+ " "
							+ kitType
							+ " is invalid. Not propagating entry",
						1);
					continue;
				}

				if (description.length() > 1024) {
					message.displayMessage(
						"Description field for entry: "
							+ technology
							+ " "
							+ versionNumber
							+ " "
							+ kitType
							+ " exceeds 1024 characters. Trimming to 1024 characters.",
						1);
					description = description.substring(0, 1023);
				}

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.KIT_INFO ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, KIT_TYPE, DESCRIPTION, LAST_USERID, LAST_TIMESTAMP ) ");
				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNumber + "',");
				sqlbuff.append("'" + kitType + "',");
				sqlbuff.append("'" + description + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());

			}

			flatFile.close();

		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;

	}

	//new for 5.1.1
	public int transferToDAModelTypeTable(String inputFileName) {

		String tech, ver, daName, ModelType, daType, asicCodeList = "";
		String temp = "";
		ResultSet rset = null;
		String techType = new String();
		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) { //while

				asicCodeList = "";
				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				tech = getMktName(tok.nextToken().trim());
				ver = tok.nextToken().trim();
				daName = tok.nextToken().trim();
				daType = tok.nextToken().trim();
				ModelType = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append(
					"SELECT TECH_TYPE FROM EDESIGN.TECHNOLOGY_VERSION WHERE TECHNOLOGY=");

				sqlbuff.append("'" + tech + "' AND VERSION_NO=");
				sqlbuff.append("'" + ver + "'");

				message.displayMessage(sqlbuff.toString(), 1);
				rset = sqlStatement.executeQuery(sqlbuff.toString());

				if (rset.next())
					techType = rset.getString("TECH_TYPE");
				if (techType == null) {
					techType = new String();
					techType = "";
				}

				if (daType.equalsIgnoreCase("Non-standard-R")
					|| daType.equalsIgnoreCase("Platform-R")) {
					sqlbuff = new StringBuffer();
					sqlbuff.append(
						"SELECT DISTINCT ASIC_CODENAME FROM EDESIGN.NONSTANDARD_DELIVERABLES WHERE TECHNOLOGY=");
					sqlbuff.append("'" + tech + "' AND VERSION_NO=");
					sqlbuff.append("'" + ver + "' AND NSD_NAME=");
					sqlbuff.append("'" + daName + "'");
					message.displayMessage(sqlbuff.toString(), 1);
					rset = sqlStatement.executeQuery(sqlbuff.toString());

					while (rset.next()) {
						temp = rset.getString("ASIC_CODENAME");
						asicCodeList = asicCodeList + temp + ",";
					}
				}

				sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.DA_MODELTYPES ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, TECH_TYPE, DA_TOOL, DA_TYPE, MODEL_TYPES, ASIC_CODENAME, LAST_USERID, LAST_TIMESTAMP ) ");
				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + tech + "',");
				sqlbuff.append("'" + ver + "',");
				sqlbuff.append("'" + techType + "',");
				sqlbuff.append("'" + daName + "',");
				sqlbuff.append("'" + daType + "',");
				sqlbuff.append("'" + ModelType + "',");
				sqlbuff.append("'" + asicCodeList + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;

	}
	//end of new for 5.1.1

	public int transferToCustomerTypesTable(String inputFileName) {

		String customerType,
			baseModelKit,
			toolkits,
			gaCores,
			resCores,
			gaNsds,
			resNsds,
			gaBaseOrd,
			resBaseOrd;

		try {

			// old hard-coded table entry
			// String tmp="INSERT INTO EDESIGN.CUSTOMER_TYPES VALUES ( 'REGULAR','Y','Y','Y','Y','Y','Y','Y','Y',user, current timestamp )";

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				customerType = tok.nextToken().trim();
				baseModelKit = tok.nextToken().trim();
				toolkits = tok.nextToken().trim();
				gaCores = tok.nextToken().trim();
				resCores = tok.nextToken().trim();
				gaNsds = tok.nextToken().trim();
				resNsds = tok.nextToken().trim();
				gaBaseOrd = tok.nextToken().trim();
				resBaseOrd = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.CUSTOMER_TYPES ");

				sqlbuff.append(
					"( CUSTOMER_TYPE, BASE_MODEL_KIT, TOOLKITS, GA_CORES, RES_CORES, GA_NSD, RES_NSD, GA_BASE_ORD, RES_BASE_ORD, LAST_USERID, LAST_TIMESTAMP ) ");
				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + customerType + "',");
				sqlbuff.append("'" + baseModelKit + "',");
				sqlbuff.append("'" + toolkits + "',");
				sqlbuff.append("'" + gaCores + "',");
				sqlbuff.append("'" + resCores + "',");
				sqlbuff.append("'" + gaNsds + "',");
				sqlbuff.append("'" + resNsds + "',");
				sqlbuff.append("'" + gaBaseOrd + "',");
				sqlbuff.append("'" + resBaseOrd + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* ASIC_CODENAME                                                */
	/* USERS_COMPANY                                                */
	/****************************************************************/

	public int transferToASICCodenameTable(String inputFileName) {

		String technology,
			asicCodename,
			usersCompany,
			versionNo,
			projectName,
			customerType;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.ASIC_CODENAME (TECHNOLOGY, VERSION_NO, ASIC_CODENAME, USERS_COMPANY, CUSTOMER_PROJNAME, CUSTOMER_TYPE, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					technology = getMktName(tok.nextToken().trim());
					versionNo = tok.nextToken().trim();
					System.out.println(
						"WHAT HAPPEN " + technology + " " + versionNo);
					asicCodename = tok.nextToken().trim();
					projectName = tok.nextToken().trim();
					usersCompany = tok.nextToken().trim();
					customerType = tok.nextToken().trim();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, asicCodename);
					pstmt.setString(4, usersCompany);
					pstmt.setString(5, projectName);
					pstmt.setString(6, customerType);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (BatchUpdateException ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);

			}
			message.displayMessage(
				"Error: Stacktrace: \n" + ex.getNextException(),
				1);
			//  message.displayMessage("Error: Stacktrace: \n" + getStackTrace(ex), 1);
			sendErrorMail(ex);
			return FAILURE;
		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* CORE_NAME                                                    */
	/* CORE_DESC                                                    */
	/* CORE_DETAIL                                                  */
	/****************************************************************/

	public int transferToGACoresTable(String inputFileName) {

		String technology, versionNo, cs;
		String coreName, coreDesc, coreDetail, coreLibraryGroup, newCore;
		int coreSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				// this happens if there is a blank line at the end of the file!

				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				coreName = tok.nextToken().trim();
				cs = tok.nextToken().trim();
				coreDesc = tok.nextToken().trim();
				coreDesc = handleQuotes(coreDesc);
				coreDetail = tok.nextToken().trim();
				coreDetail = handleQuotes(coreDetail);
				coreLibraryGroup = tok.nextToken().trim();
				newCore = tok.nextToken().trim();

				coreSize = Integer.valueOf(cs).intValue();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.GA_CORES_NEW ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, CORE_NAME, CORE_DESC, CORE_SIZE, LIB_GROUP_NAME, NON_INITIAL_CORE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + coreName + "',");
				sqlbuff.append("'" + coreDesc + "',");
				sqlbuff.append("" + coreSize + ",");
				sqlbuff.append("'" + coreLibraryGroup + "',");
				sqlbuff.append("'" + newCore + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	// new for 2.9 fix-pack

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* BASE_ORD_NAME                                                */
	/* BASE_ORD_DESC                                                */
	/* BASE_ORD_DETAIL                                              */
	/****************************************************************/

	public int transferToGaBaseOrdTable(String inputFileName) {

		String technology, versionNo;
		String baseOrdName, baseOrdDesc, baseOrdDetail, baseOrdInd;
		int baseOrdSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				// this happens if there is a blank line at the end of the file!

				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				baseOrdName = tok.nextToken().trim();
				baseOrdSize = Integer.parseInt(tok.nextToken().trim());
				baseOrdDesc = tok.nextToken().trim();
				baseOrdDesc = handleQuotes(baseOrdDesc);
				baseOrdDetail = tok.nextToken().trim();
				baseOrdDetail = handleQuotes(baseOrdDetail);

				//new for 3.10.1
				baseOrdInd = tok.nextToken().trim();
				//end of new for 3.10.1

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.GA_BASE_ORD ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, BASE_ORD_NAME, BASE_ORD_DESC, BASE_ORD_SIZE, NEW_BASE_IND, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + baseOrdName + "',");
				sqlbuff.append("'" + baseOrdDesc + "',");
				sqlbuff.append("" + baseOrdSize + ",");
				//new for 3.10.1
				sqlbuff.append("'" + baseOrdInd + "',");
				//end of new for 3.10.1

				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	//new for 3.10.1

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* TOOL_NAME                                                    */
	/* TOOL_TYPE (GA or RE)                                         */
	/* MDL_TYPE                                                     */
	/* followed by the normal delimiter                             */
	/****************************************************************/

	public int transferToPlatformMDLTypesTable(String inputFileName) {

		String technology, versionNo;
		String toolName, toolType, mdlType;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				toolName = tok.nextToken().trim();
				toolType = tok.nextToken().trim();
				mdlType = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.PLATFORM_MDL_TYPES ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, PLATFORM, PLATFORM_TYPE, MDL_TYPE_NAME, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + toolName + "',");
				sqlbuff.append("'" + toolType + "',");
				sqlbuff.append("'" + mdlType + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}
	// end of new for 3.10.1

	//new for 4.2.1

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* MDL_TYPE_NAME                                                */
	/* MDL_TYPE (Standard)                                          */
	/* followed by the normal delimiter                             */
	/****************************************************************/

	public int transferToPacketMDLTypeTable(String inputFileName) {

		String technology, versionNo;
		String toolType, mdlTypeName;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				mdlTypeName = tok.nextToken().trim();
				toolType = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.PACKETS_MDL_TYPE ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, MDL_TYPE_NAME, MDL_TYPE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + mdlTypeName + "',");
				sqlbuff.append("'" + toolType + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	//new for 5.1.1 CU65

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology;version;revision                                  */
	/* asiccode;company;proj;location;platformlist;ipversionlist;ipcategorylist                                                                    */
	/*                                                              */
	/****************************************************************/

	public int transferToIPEntitlementTable(String inputFileName) {

		String technology, versionNo, revisionNo;
		String asicCode, company, projName;
		String platformList, ipVersionList, ipCategoryList, skip;
		String ipver, ipcat;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');
				if (!tok.hasMoreTokens())
					continue;
				asicCode = tok.nextToken().trim();
				company = tok.nextToken().trim();
				projName = tok.nextToken().trim();
				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				platformList = tok.nextToken().trim();
				revisionNo = tok.nextToken().trim();
				ipver = tok.nextToken().trim();
				ipcat = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.IP_ENTITLEMENT ");

				sqlbuff.append(
					"( ASIC_CODENAME, CUSTOMER_PROJNAME, USER_COMPANY, TECHNOLOGY, VERSION_NO, PLATFORMS, REVISION_NO, IPVERSION, IPCATEGORY, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + asicCode + "',");
				sqlbuff.append("'" + projName + "',");
				sqlbuff.append("'" + company + "',");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + platformList + "',");
				sqlbuff.append("'" + revisionNo + "',");
				sqlbuff.append("'" + ipver + "',");
				sqlbuff.append("'" + ipcat + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 1);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}
	//end of new for 5.1.1

	//new for 5.4.1 CU65 IP Content

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology;version;revision                                  */
	/* asiccode;company;proj;location;platformlist;ipversionlist;ipcategorylist                                                                    */
	/*                                                              */
	/****************************************************************/

	public int transferToIPContentTable(String inputFileName, String techVer) {

		String ipTechnology, ipVersion, ipName, ipCat, ipContent;
		tok = new SimpleStringTokenizer(techVer, '/');
		ipTechnology = tok.nextToken().trim();
		ipTechnology =
			ipTechnology.substring(0, 1)
				+ ipTechnology.substring(1, 2).toLowerCase()
				+ "-"
				+ ipTechnology.substring(2, ipTechnology.length());

		ipVersion = tok.nextToken().trim();

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');
				if (!tok.hasMoreTokens())
					continue;

				ipName = tok.nextToken().trim();
				ipCat = tok.nextToken().trim();
				ipContent = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.IP_CONTENT ");

				sqlbuff.append(
					"(TECHNOLOGY, VERSION_NO, IP_NAME, IP_CATEGORY, IP_CONTENT, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + ipTechnology + "',");
				sqlbuff.append("'" + ipVersion + "',");
				sqlbuff.append("'" + ipName + "',");
				sqlbuff.append("'" + ipCat + "',");
				sqlbuff.append("'" + ipContent + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 1);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}
	//end of new for 5.4.1
	//	new for 6.1.1 Cu REVISION_LIST
	// TODO
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology;version;revisionName;revisionNumber               */
	/*  The rest of the file contains the release note text complete with html tags                                     */
	/*                                                              */
	/****************************************************************/

	public int transferToRevisionListTable(String inputFileName) {

		String revTechnology, revName, revNo, revVersion;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');
				if (!tok.hasMoreTokens())
					continue;
				revTechnology = tok.nextToken().trim();
				revVersion = tok.nextToken().trim();
				revName = tok.nextToken().trim();
				revNo = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.REVISION_LIST ");

				sqlbuff.append(
					"(TECHNOLOGY, VERSION_NO, REVISION_NAME, REVISION_NO, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + revTechnology + "',");
				sqlbuff.append("'" + revVersion + "',");
				sqlbuff.append("'" + revName + "',");
				sqlbuff.append("'" + revNo + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 1);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}
	//	new for 6.1.1 Cu REVISION_RELNOTES
	// TODO
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* revisionName;revisionNum;releaseDate;                        */
	/*  The rest of the file contains the release note text complete with html tags    */
	/*  The Table also gets the technology from REVISION_LIST table    */
	/****************************************************************/
	public int transferToRevisionRelNotesTable(
		String baseofFileName,
		String techVer) {

		String revTechnology,
			revName = "",
			revNo = "",
			relDate = "",
			revDesp = "";
		String ipTechnology, ipVersion;
		ResultSet rset = null;
		PreparedStatement updstmt = null;
		Vector revname = new Vector();
		Vector revno = new Vector();
		String inputFileName;
		tok = new SimpleStringTokenizer(techVer, '/');
		ipTechnology = tok.nextToken().trim();
		ipTechnology =
			ipTechnology.substring(0, 1)
				+ ipTechnology.substring(1, 2).toLowerCase()
				+ "-"
				+ ipTechnology.substring(2, ipTechnology.length());
		ipVersion = tok.nextToken().trim();

		try {

			StringBuffer sqlbuff = new StringBuffer();

			sqlbuff.append(
				"SELECT DISTINCT REVISION_NAME,REVISION_NO FROM EDESIGN.REVISION_LIST WHERE TECHNOLOGY=");
			sqlbuff.append("'" + ipTechnology + "' AND VERSION_NO=");
			sqlbuff.append("'" + ipVersion + "' with ur ");

			message.displayMessage(sqlbuff.toString(), 1);

			rset = sqlStatement.executeQuery(sqlbuff.toString());

			
			while (rset.next()) {
				revname.addElement(rset.getString("REVISION_NAME"));
				revno.addElement(rset.getString("REVISION_NO"));
			}
			//Append the path by taking the revision name and  revision number
			 
			for( int i =0 ; i < revname.size(); i++ ) {
				inputFileName =
					baseofFileName
						+ revname.get(i)
						+ "_"
						+ revno.get(i)
						+ ".relnote";
				boolean exists = (new File(inputFileName)).exists();

				if (exists) { //if file exists then insertion is performed in the revision_relnotes table
					revDesp = "";
					flatFile =
						new BufferedReader(new FileReader(inputFileName));
					if (flatFile.ready()) {

						line = flatFile.readLine();
						tok = new SimpleStringTokenizer(line, ';');

						revName = tok.nextToken().trim();
						revNo = tok.nextToken().trim();
						relDate = tok.nextToken().trim();

						while (flatFile.ready()) {
							String lineDesp = flatFile.readLine();
							revDesp = revDesp + lineDesp;
						}
						revDesp = revDesp.replaceAll("'", "''");
					}

					queryToDeleteIfRowExists(ipTechnology, revName, revNo);
					StringBuffer sqlbuff1 = new StringBuffer();
					sqlbuff1.append("INSERT INTO EDESIGN.REVISION_RELNOTES ");
					sqlbuff1.append(
						"(TECHNOLOGY, REVISION_NAME, REVISION_NO, RELEASE_DATE,REVISION_DESP, ");
					sqlbuff1.append("LAST_USERID, LAST_TIMESTAMP ) ");
					sqlbuff1.append("VALUES ( ");
					sqlbuff1.append("'" + ipTechnology + "',");
					sqlbuff1.append("'" + revName + "',");
					sqlbuff1.append("'" + revNo + "',");
					sqlbuff1.append("'" + relDate + "',");
					sqlbuff1.append("'" + revDesp + "',");
					sqlbuff1.append("user, current timestamp )");
					message.displayMessage(sqlbuff1.toString(), 1);
					updstmt = conn.prepareStatement(sqlbuff1.toString());
					updstmt.executeUpdate();
					
					flatFile.close();
					File tobeDeleted = new File(inputFileName);
					boolean success = tobeDeleted.delete();

					if (!success) {
						message.displayMessage("Deletion failed", 1);
					}
				}
			}

		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			if (rset != null)
				try {
					rset.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (updstmt != null)
				try {
					updstmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}

		return SUCCESS;
	}
	public void queryToDeleteIfRowExists(
		String tech,
		String revName,
		String revNo) {
		ResultSet rset1 = null;
		PreparedStatement delstmt = null;
		try {

			StringBuffer sqlSelect = new StringBuffer();

			sqlSelect.append(
				"SELECT * FROM EDESIGN.REVISION_RELNOTES WHERE TECHNOLOGY =");
			sqlSelect.append("'" + tech + "' AND REVISION_NAME = ");
			sqlSelect.append("'" + revName + "' AND REVISION_NO = ");
			sqlSelect.append("'" + revNo + "' WITH UR");
			rset1 = sqlStatement.executeQuery(sqlSelect.toString());
			while (rset1.next()) {
				StringBuffer sqlDelete = new StringBuffer();
				sqlDelete.append(
					"DELETE FROM EDESIGN.REVISION_RELNOTES WHERE TECHNOLOGY =");
				sqlDelete.append("'" + tech + "' AND REVISION_NAME = ");
				sqlDelete.append("'" + revName + "' AND REVISION_NO = ");
				sqlDelete.append("'" + revNo + "' WITH UR");
				delstmt = conn.prepareStatement(sqlDelete.toString());
				delstmt.executeUpdate();
				System.out.println("deleted");
			}

		} catch (Exception ex) {

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);

		} finally {
			if (rset1 != null)
				try {
					rset1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (delstmt != null)
				try {
					delstmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}

	}
	//end of new for 6.1.1
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* LIB_GROUP_NAME                                               */
	/* LIB_GROUP_TYPE                                               */
	/* followed by the normal delimiter                             */
	/****************************************************************/

	public int transferToPacketsLIBGroupTable(String inputFileName) {

		String technology, versionNo;
		String libType, libName;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				libName = tok.nextToken().trim();
				libType = tok.nextToken().trim();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.PACKETS_LIB_GROUP ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, LIB_GROUP_NAME, LIB_GROUP_TYPE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + libName + "',");
				sqlbuff.append("'" + libType + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	//end of new for 4.2.1

	// new for 2.9
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* TOOL_NAME                                                    */
	/* TOOL_SIZE                                                    */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/

	public int transferToNSD_GATable(String inputFileName) {

		String technology, versionNo;
		String toolName, toolDesc, toolDetail;
		int toolSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				toolName = tok.nextToken().trim();
				toolSize = Integer.valueOf(tok.nextToken().trim()).intValue();

				toolDesc = tok.nextToken().trim();
				toolDesc = handleQuotes(toolDesc);
				toolDetail = tok.nextToken().trim();
				toolDetail = handleQuotes(toolDetail);

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.NSD_GA ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, NSD_NAME, NSD_DESC, NSD_SIZE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + toolName + "',");
				sqlbuff.append("'" + toolDesc + "',");
				sqlbuff.append("" + toolSize + ",");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* ASIC_CODENAME                                                */
	/* BASE_ORD_NAME                                                */
	/* BASE_ORD_DESC                                                */
	/* BASE_ORD_DETAIL                                              */
	/****************************************************************/

	public int transferToResBaseOrdTable(String inputFileName) {

		String technology,
			versionNo,
			asicCodename,
			baseOrdName,
			baseOrdDesc,
			baseOrdDetail,
			newbaseOrd;
		int baseOrdSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				asicCodename = tok.nextToken().trim();
				baseOrdName = tok.nextToken().trim();
				baseOrdSize = Integer.parseInt(tok.nextToken().trim());
				baseOrdDesc = tok.nextToken().trim();
				baseOrdDesc = handleQuotes(baseOrdDesc);
				baseOrdDetail = tok.nextToken().trim();
				baseOrdDetail = handleQuotes(baseOrdDetail);
				//new for 3.10.1
				newbaseOrd = tok.nextToken().trim();
				//end of new for 3.10.1
				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.RESTRICTED_BASE_ORD ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, ASIC_CODENAME, BASE_ORD_NAME, BASE_ORD_DESC, BASE_ORD_SIZE, NEW_BASE_IND, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + asicCodename + "',");
				sqlbuff.append("'" + baseOrdName + "',");
				sqlbuff.append("'" + baseOrdDesc + "',");
				sqlbuff.append("" + baseOrdSize + ",");

				//new for 3.10.1
				sqlbuff.append("'" + newbaseOrd + "',");
				//end of new for 3.10.1

				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* ASIC_CODENAME                                                */
	/* CORE_NAME                                                    */
	/* CORE_DESC                                                    */
	/* CORE_DETAIL                                                  */
	/****************************************************************/

	public int transferToRestrictedCoresTable(String inputFileName) {

		String technology,
			versionNo,
			asicCodename,
			coreName,
			coreDesc,
			coreDetail,
			coreLibraryGroup,
			newCore,
			cs;
		int coreSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				asicCodename = tok.nextToken().trim();
				coreName = tok.nextToken().trim();
				cs = tok.nextToken().trim();
				coreDesc = tok.nextToken().trim();
				coreDesc = handleQuotes(coreDesc);
				coreDetail = tok.nextToken().trim();
				coreDetail = handleQuotes(coreDetail);
				coreLibraryGroup = tok.nextToken().trim();
				newCore = tok.nextToken().trim();

				coreSize = Integer.valueOf(cs).intValue();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.RESTRICTED_CORES_NEW ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, ASIC_CODENAME, CORE_NAME, CORE_DESC, CORE_SIZE, LIB_GROUP_NAME, NON_INITIAL_CORE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + asicCodename + "',");
				sqlbuff.append("'" + coreName + "',");
				sqlbuff.append("'" + coreDesc + "',");
				sqlbuff.append("" + coreSize + ",");
				sqlbuff.append("'" + coreLibraryGroup + "',");
				sqlbuff.append("'" + newCore + "',");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* ASIC_CODENAME                                                */
	/* NSD_NAME                                                     */
	/* NSD_DESC                                                     */
	/* NSD_DETAIL                                                   */
	/****************************************************************/

	public int transferToNonStandardDeliverablesTable(String inputFileName) {

		String technology, versionNo, asicCodename;
		String nsdName, nsdDesc, nsdDetail, ns;
		int nsdSize;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				asicCodename = tok.nextToken().trim();
				nsdName = tok.nextToken().trim();
				ns = tok.nextToken().trim();
				nsdDesc = tok.nextToken().trim();
				nsdDesc = handleQuotes(nsdDesc);
				nsdDetail = tok.nextToken().trim();
				nsdDetail = handleQuotes(nsdDetail);

				nsdSize = Integer.valueOf(ns).intValue();

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.NONSTANDARD_DELIVERABLES ");

				sqlbuff.append(
					"( TECHNOLOGY, VERSION_NO, ASIC_CODENAME, NSD_NAME, NSD_DESC, NSD_SIZE, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + asicCodename + "',");
				sqlbuff.append("'" + nsdName + "',");
				sqlbuff.append("'" + nsdDesc + "',");
				sqlbuff.append("" + nsdSize + ",");
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_releases                                               */
	/* --------------                                               */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Delta Date (MM/DD/YYYY)                                      */
	/* Delta Time (HH:MM:00)                                        */
	/* Delta Sequence Number (nnnn)                                 */
	/* Delta Description                                            */
	/* Delta Reason {1|2|3|4}                                       */
	/* Base Indicator ("Y" if type=BASE, "N" otherwise}             */
	/* Core Indicator ("Y" if type=CORE, "N" otherwise}             */
	/* Customer Indicator ("Y" if type=CUSTOMER, "N" otherwise}     */
	/* Special Indicator ("Y" if type=SPECIAL, "N" otherwise}       */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASES                                               */
	/* --------------                                               */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* DELTA_DATE                     DATE                      4   */
	/* DELTA_TIME                     TIME                      3   */
	/* DELTA_SEQ                      SMALLINT                  2   */
	/* DELTA_DESC                     VARCHAR                1024   */
	/* DELTA_SIZE                     INTEGER                   4   */
	/* DELTA_REASON                   INTEGER                   4   */
	/* BASE_IND                       CHARACTER                 1   */
	/* CORE_IND                       CHARACTER                 1   */
	/* CUSTOMER_IND                   CHARACTER                 1   */
	/* SPECIAL_IND                    CHARACTER                 1   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToDeltaReleasesTable(String inputFileName) {

		String technology = null,
			versionNo = null,
			deltaName,
			deltaDate,
			deltaDesc,
			dsz,
			dsq;
		String baseInd, coreInd, customerInd, specialInd, deltaTime, reason;
		int deltaSize, deltaSeq, deltaReason;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.DELTA_RELEASES (TECHNOLOGY, VERSION_NO, DELTA_NAME, DELTA_DATE, DELTA_TIME, DELTA_SEQ, DELTA_DESC, DELTA_SIZE, DELTA_REASON, BASE_IND, CORE_IND, CUSTOMER_IND, SPECIAL_IND, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					deltaName = tok.nextToken().trim();
					deltaDate = tok.nextToken().trim();
					deltaTime = tok.nextToken().trim();
					dsq = tok.nextToken().trim();
					deltaDesc = tok.nextToken().trim();
					deltaDesc = handleQuotes(deltaDesc);
					reason = tok.nextToken().trim();
					baseInd = tok.nextToken().trim();
					coreInd = tok.nextToken().trim();
					customerInd = tok.nextToken().trim();
					specialInd = tok.nextToken().trim();

					deltaSeq = Integer.valueOf(dsq).intValue();
					deltaReason = Integer.valueOf(reason).intValue();
					deltaSize = 0;
					Date deltaDateObject = dateFormatter.parse(deltaDate);
					java.sql.Date sqlDate =
						new java.sql.Date(deltaDateObject.getTime());
					Date deltaTimeObject = timeFormatter.parse(deltaTime);
					java.sql.Time sqlTime =
						new java.sql.Time(deltaTimeObject.getTime());
					/*
									//convert the date from mm/dd/yyyy to yyyy-mm-dd
									String dat,sat;
									dat = deltaDate.substring(6,10);
									sat = deltaDate.substring(0,2);
									dat = dat.concat("-");
									dat = dat.concat(sat);
									dat = dat.concat("-");
									sat = deltaDate.substring(3,5);
									dat = dat.concat(sat);
					*/
					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, deltaName);
					//pstmt.setString(4, dat);

					pstmt.setDate(4, sqlDate);
					//pstmt.setString(5, deltaTime);
					pstmt.setTime(5, sqlTime);
					pstmt.setInt(6, deltaSeq);
					pstmt.setString(7, deltaDesc);
					pstmt.setInt(8, deltaSize);
					pstmt.setInt(9, deltaReason);
					pstmt.setString(10, baseInd);
					pstmt.setString(11, coreInd);
					pstmt.setString(12, customerInd);
					pstmt.setString(13, specialInd);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (BatchUpdateException bexp) {
			try {
				conn.rollback();
			} catch (SQLException sqlexp) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqlexp),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + bexp.getNextException(),
				1);
			sendErrorMail(bexp);
			return FAILURE;
		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_release_mdl_types                                      */
	/* -----------------------                                      */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Model Type Name                                              */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASE_MDL_TYPES                                      */
	/* -----------------------                                      */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToDeltaReleaseMdlTypesTable(String inputFileName) {

		String technology = null, versionNo = null, deltaName, mdlType;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.DELTA_RELEASE_MDL_TYPES (TECHNOLOGY, VERSION_NO, DELTA_NAME, MDL_TYPE_NAME, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					deltaName = tok.nextToken().trim();
					mdlType = tok.nextToken().trim();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, deltaName);
					pstmt.setString(4, mdlType);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_release_lib_groups                                     */
	/* ------------------------                                     */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Lib Group Name                                               */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASE_LIB_GROUPS                                     */
	/* ------------------------                                     */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToDeltaReleaseLibGroupsTable(String inputFileName) {

		String technology = null, versionNo = null, deltaName, libGroup;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.DELTA_RELEASE_LIB_GROUPS (TECHNOLOGY, VERSION_NO, DELTA_NAME, LIB_GROUP_NAME, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					deltaName = tok.nextToken().trim();
					libGroup = tok.nextToken().trim();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, deltaName);
					pstmt.setString(4, libGroup);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* orderable_mdl_types                                          */
	/* -------------------                                          */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Model Type Name                                              */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* ORDERABLE_MDL_TYPES                                          */
	/* -------------------                                          */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* ASIC_CODENAME                  VARCHAR                  20   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToOrderableMdlTypesTable(String inputFileName) {

		String technology = null, versionNo = null, asicCodename, mdlType;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.ORDERABLE_MDL_TYPES (TECHNOLOGY, VERSION_NO, ASIC_CODENAME, MDL_TYPE_NAME, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					asicCodename = tok.nextToken().trim();
					mdlType = tok.nextToken().trim();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, asicCodename);
					pstmt.setString(4, mdlType);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* orderable_lib_groups                                         */
	/* --------------------                                         */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Lib Group Name                                               */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* ORDERABLE_LIB_GROUPS                                         */
	/* --------------------                                         */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* ASIC_CODENAME                  VARCHAR                  20   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToOrderableLibGroupsTable(String inputFileName) {

		String technology = null, versionNo = null, asicCodename, libGroup;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.ORDERABLE_LIB_GROUPS (TECHNOLOGY, VERSION_NO, ASIC_CODENAME, LIB_GROUP_NAME, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					asicCodename = tok.nextToken().trim();
					libGroup = tok.nextToken().trim();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, asicCodename);
					pstmt.setString(4, libGroup);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology                                                   */
	/* version number                                               */
	/* delta_rel_number = DR reference number (CMVC level)          */
	/* packet_name = packet name                                    */
	/* model_type = model type                                      */
	/* library_group = library group                                */
	/* file_size = size of uncompressed packet (bytes)              */
	/* comp_size = size of compressed packet (bytes)                */
	/* packet_type = {1 | 2} - release note or data                 */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* PACKET_NAME                    VARCHAR                  80   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* FILE_SIZE                      INTEGER                   4   */
	/* COMPRESSED_SIZE                INTEGER                   4   */
	/* PACKET_TYPE {1, 2}             INTEGER                   4   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int transferToDeltaPacketsTable(String inputFileName) {

		String technology = null,
			versionNo = null,
			deltaName,
			packetName,
			modelType,
			libGroup,
			packetRealName;
		String fs, cs, pt, coreName, baseOrdName;
		int fileSize = 0, compSize = 0, packetType = 0;

		int sampleReturnCode = -9;
		int totalUpdateCount = 0;
		int numBatches = 0;

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			conn.setAutoCommit(false);

			String timestamp = db2Formatter.format(new Date());

			PreparedStatement pstmt =
				conn.prepareStatement(
					"INSERT INTO EDESIGN.DELTA_PACKETS (TECHNOLOGY, VERSION_NO, DELTA_NAME, PACKET_NAME, MDL_TYPE_NAME, LIB_GROUP_NAME, FILE_SIZE, COMPRESSED_SIZE, PACKET_TYPE, CORE_NAME, BASE_ORD_NAME, PACKET_REALNAME, LAST_USERID, LAST_TIMESTAMP) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '"
						+ db2User
						+ "', '"
						+ timestamp
						+ "')");

			while (flatFile.ready()) {

				int batchCount = 0;

				while (batchCount < MAX_BATCH_COUNT && flatFile.ready()) {

					line = flatFile.readLine();
					tok = new SimpleStringTokenizer(line, ';');

					if (!tok.hasMoreTokens())
						continue;

					if (technology == null) {
						technology = getMktName(tok.nextToken().trim());
						versionNo = tok.nextToken().trim();
					} else {
						tok.nextToken();
						tok.nextToken();
					}

					deltaName = tok.nextToken().trim();
					packetName = tok.nextToken().trim();
					modelType = tok.nextToken().trim();
					libGroup = tok.nextToken().trim();
					fs = tok.nextToken().trim();
					cs = tok.nextToken().trim();
					pt = tok.nextToken().trim();
					coreName = tok.nextToken().trim();
					baseOrdName = tok.nextToken().trim();
					packetRealName = tok.nextToken().trim();

					fileSize = Integer.valueOf(fs).intValue();
					compSize = Integer.valueOf(cs).intValue();
					packetType = Integer.valueOf(pt).intValue();

					pstmt.setString(1, technology);
					pstmt.setString(2, versionNo);
					pstmt.setString(3, deltaName);
					pstmt.setString(4, packetName);
					pstmt.setString(5, modelType);
					pstmt.setString(6, libGroup);
					pstmt.setInt(7, fileSize);
					pstmt.setInt(8, compSize);
					pstmt.setInt(9, packetType);
					pstmt.setString(10, coreName);
					pstmt.setString(11, baseOrdName);
					pstmt.setString(12, packetRealName);

					pstmt.addBatch();
					batchCount++;

				}

				if (batchCount > 0) {
					int[] updateCount = pstmt.executeBatch();
					conn.commit();
					if (updateCount.length != batchCount)
						throw new RuntimeException(
							"Batch count: "
								+ batchCount
								+ " Update count: "
								+ updateCount.length);
					else {
						totalUpdateCount += updateCount.length;
						numBatches++;
						sampleReturnCode = updateCount[0];
					}
				}

			}

			pstmt.close();
			flatFile.close();
			if (debug)
				message.displayMessage(
					"Inserted "
						+ totalUpdateCount
						+ " rows in "
						+ numBatches
						+ " batches (RC: "
						+ sampleReturnCode
						+ ")",
					1);

		} catch (Exception ex) {
			try {
				conn.rollback();
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}

			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException sqle) {
				message.displayMessage(
					"Error: Stacktrace: \n" + getStackTrace(sqle),
					1);
			}
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* Kit_Type                                                     */
	/* Technology                                                   */
	/* Version_No                                                   */
	/* Platform                                                     */
	/* Platform_Name                                                */
	/* Kit_Size   
	 * SUSPEND
	 * ToolkitComponent
	 * ToolkitFlag                                                  */
	/****************************************************************/

	public int transferToPlatformsTable(String inputFileName) {

		String kitType,
			technology,
			versionNo,
			ks,
			nonActiveFlag,
			nonActiveReason;
		String platform, platformName, patchName;
		int kitSize;
		
		String toolkitComponent = "";
		String toolkitFlag = "";

		try {

			flatFile = new BufferedReader(new FileReader(inputFileName));

			while (flatFile.ready()) {

				line = flatFile.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				kitType = tok.nextToken().trim();
				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				platform = tok.nextToken().trim();
				platformName = tok.nextToken().trim();
				ks = tok.nextToken().trim();

				kitSize = Integer.valueOf(ks).intValue();

				//new for 4.4.1
				if (kitType.equalsIgnoreCase("TOOL"))
					patchName = tok.nextToken().trim();
				else
					patchName = "";
				//end of new for 4.4.1

				nonActiveFlag = "N";
				nonActiveReason = "";
				boolean isSuspend = false;
				if (tok.hasMoreTokens()) {
					String tempToken = tok.nextToken().trim();
					if (tempToken.equals("SUSPEND")) {
						nonActiveFlag = "Y";
						nonActiveReason = tok.nextToken().trim();
						isSuspend = true;
					}
					else
					{
						toolkitComponent = tempToken;
					} 
				}

				if( tok.hasMoreTokens() && !isSuspend )
				{
					toolkitFlag = tok.nextToken().trim();
				}
				else
				{
					toolkitFlag = "N";
				}

				StringBuffer sqlbuff = new StringBuffer();

				sqlbuff.append("INSERT INTO EDESIGN.PLATFORMS_NEW ");

				sqlbuff.append(
					"( KIT_TYPE, TECHNOLOGY, VERSION_NO, PLATFORM, PLATFORM_NAME, KIT_SIZE, NON_ACTIVE_FLAG, NON_ACTIVE_REASON, PATCH_NAME, ");
				
				sqlbuff.append("TOOLKIT_COMPONENT, TOOLKIT_FLAG, ");
				sqlbuff.append("LAST_USERID, LAST_TIMESTAMP ) ");

				sqlbuff.append("VALUES ( ");
				sqlbuff.append("'" + kitType + "',");
				sqlbuff.append("'" + technology + "',");
				sqlbuff.append("'" + versionNo + "',");
				sqlbuff.append("'" + platform + "',");
				sqlbuff.append("'" + platformName + "',");
				sqlbuff.append("" + kitSize + ",");
				sqlbuff.append("'" + nonActiveFlag + "',");
				sqlbuff.append("'" + nonActiveReason + "',");
				//new for 4.4.1
				sqlbuff.append("'" + patchName + "',");
				//end of new for 4.4.1
				sqlbuff.append("'" + toolkitComponent  + "',");
				sqlbuff.append("'" + toolkitFlag  + "',");
				
				sqlbuff.append("user, current timestamp )");

				message.displayMessage(sqlbuff.toString(), 2);
				sqlStatement.executeUpdate(sqlbuff.toString());
			}

			flatFile.close();
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* New for 3.7.1                                                */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* Kit_Type                                                     */
	/* Technology                                                   */
	/* Version_No                                                   */
	/* Platform                                                     */
	/* Patch_Name                                                   */
	/* Platform_Name                                                */
	/* Kit_Size                                                     */
	/* Model_Delta                                                  */
	/* Date                                                         */
	/****************************************************************/

	public int transferToPlatformsPatchTable(String inputFileName) { //method

		String kitType,
			technology,
			versionNo,
			ks,
			nonActiveFlag,
			nonActiveReason;
		String platform,
			tk_platform,
			platformName,
			patchName,
			model_delta,
			patchdate;
		int kitSize;

		try { //try

			flatFile = new BufferedReader(new FileReader(inputFileName));

			String timestamp = db2Formatter.format(new Date());

			StringBuffer sqlbuff = new StringBuffer();

			sqlbuff.append("INSERT INTO EDESIGN.TOOLKIT_PATCH ");
			sqlbuff.append(
				"( KIT_TYPE, TECHNOLOGY, VERSION_NO, TOOLKIT_PLATFORM, PLATFORM, PATCH_NAME, PLATFORM_NAME, KIT_SIZE, MODEL_DELTA, DATE, ");
			sqlbuff.append(
				"LAST_USERID, LAST_TIMESTAMP ) VALUES (?,?,?,?,?,?,?,?,?,?,'"
					+ db2User
					+ "', '"
					+ timestamp
					+ "')");

			PreparedStatement pstmt = conn.prepareStatement(sqlbuff.toString());

			while (flatFile.ready()) { //while loop

				line = flatFile.readLine();

				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				kitType = tok.nextToken().trim();
				technology = getMktName(tok.nextToken().trim());
				versionNo = tok.nextToken().trim();
				tk_platform = tok.nextToken().trim();
				platform = tok.nextToken().trim();
				patchName = tok.nextToken().trim();
				platformName = tok.nextToken().trim();
				ks = tok.nextToken().trim();
				model_delta = tok.nextToken().trim();
				patchdate = tok.nextToken().trim();
				kitSize = Integer.valueOf(ks).intValue();
				Date patchDateObject = dateFormatter.parse(patchdate);
				java.sql.Date sqlpatchDate =
					new java.sql.Date(patchDateObject.getTime());
				/*
							//convert the date from mm/dd/yyyy to yyyy-mm-dd
											String dat,sat;
											dat = patchdate.substring(6,10);
											sat = patchdate.substring(0,2);
											dat = dat.concat("-");
											dat = dat.concat(sat);
											dat = dat.concat("-");
											sat = patchdate.substring(3,5);
								dat = dat.concat(sat);
				*/

				pstmt.setString(1, kitType);
				pstmt.setString(2, technology);
				pstmt.setString(3, versionNo);
				pstmt.setString(4, tk_platform);
				pstmt.setString(5, platform);
				pstmt.setString(6, patchName);
				pstmt.setString(7, platformName);
				pstmt.setInt(8, kitSize);
				pstmt.setString(9, model_delta);
				pstmt.setDate(10, sqlpatchDate);
				message.displayMessage(sqlbuff.toString(), 1);
				System.out.println(
					kitType
						+ " "
						+ technology
						+ " "
						+ versionNo
						+ " "
						+ tk_platform
						+ " "
						+ platform
						+ " "
						+ patchName
						+ " "
						+ platformName
						+ " "
						+ kitSize
						+ " "
						+ model_delta
						+ " "
						+ patchdate);
				pstmt.executeUpdate();
			}

			pstmt.close();
			flatFile.close();
			message.displayMessage(sqlbuff.toString(), 1);
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);

			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/

	String handleQuotes(String s) {

		StringBuffer sb = new StringBuffer(s);
		String newS;
		int q;

		/* change all the double quotes to single quotes */
		q = s.indexOf("\"");
		while (q != -1) {
			sb.setCharAt(q, '\'');
			q = s.indexOf("\"", q + 1);
		}

		/* put a single quote in front of each single quote */
		newS = sb.toString();
		q = newS.indexOf("'");
		while (q != -1) {
			sb.insert(q, '\'');
			newS = sb.toString();
			q = newS.indexOf("'", q + 2);
		}
		return newS;
	}

	/****************************************************************/

	public int deleteTechnologyDocumentTable() { //

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.TECHNOLOGY_DOCUMENT");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	public int clearBTVTable(String tableName) { //clear

		try {
			StringBuffer sqlbuff = new StringBuffer();
			sqlbuff.append("UPDATE ");
			sqlbuff.append(SCHEMA + ".");
			sqlbuff.append(tableName);
			sqlbuff.append(" SET ADD_DEL_INDICATOR = 'N'");
			message.displayMessage(sqlbuff.toString(), 1);
			sqlStatement.executeUpdate(sqlbuff.toString());
		} catch (Exception ex) {
			message.displayMessage(
				"Error: Stacktrace: \n" + getStackTrace(ex),
				1);
			sendErrorMail(ex);
			return FAILURE;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* ROLE                                                         */
	/* VERSION_COUNT                                                */
	/* VERSION_NO                                                   */
	/* FCS_DATE                                                     */
	/* PM_NAME                                                      */
	/* PM_EMAIL                                                     */
	/****************************************************************/

	public int deleteTechnologyVersionTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.TECHNOLOGY_VERSION");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		// NOTE: Technology_Master table MUST be deleted LAST, after all other
		// tables tied to it with foreign keys have been deleted

		try {
			sqlStatement.executeUpdate("DELETE FROM EDESIGN.TECHNOLOGY_MASTER");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* ASIC_CODENAME                                                */
	/* USERS_COMPANY                                                */
	/****************************************************************/

	public int deleteASICCodenameTable() {

		try {
			sqlStatement.executeUpdate("DELETE FROM EDESIGN.ASIC_CODENAME");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* CORE_NAME                                                    */
	/* CORE_DESC                                                    */
	/* CORE_DETAIL                                                  */
	/****************************************************************/

	public int deleteGACoresTable() {

		try {
			sqlStatement.executeUpdate("DELETE FROM EDESIGN.GA_CORES_NEW");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* ASIC_CODENAME                                                */
	/* CORE_NAME                                                    */
	/* CORE_DESC                                                    */
	/* CORE_DETAIL                                                  */
	/****************************************************************/

	public int deleteRestrictedCoresTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.RESTRICTED_CORES_NEW");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* ASIC_CODENAME                                                */
	/* NSD_NAME                                                     */
	/* NSD_DESC                                                     */
	/* NSD_DETAIL                                                   */
	/****************************************************************/

	public int deleteNonStandardDeliverablesTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.NONSTANDARD_DELIVERABLES");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_releases                                               */
	/* --------------                                               */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Delta Date (MM/DD/YYYY)                                      */
	/* Delta Time (HH:MM:00)                                        */
	/* Delta Sequence Number (nnnn)                                 */
	/* Delta Description                                            */
	/* Delta Reason {1|2|3|4}                                       */
	/* Base Indicator ("Y" if type=BASE, "N" otherwise}             */
	/* Core Indicator ("Y" if type=CORE, "N" otherwise}             */
	/* Customer Indicator ("Y" if type=CUSTOMER, "N" otherwise}     */
	/* Special Indicator ("Y" if type=SPECIAL, "N" otherwise}       */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASES                                               */
	/* --------------                                               */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* DELTA_DATE                     DATE                      4   */
	/* DELTA_TIME                     TIME                      3   */
	/* DELTA_SEQ                      SMALLINT                  2   */
	/* DELTA_DESC                     VARCHAR                1024   */
	/* DELTA_SIZE                     INTEGER                   4   */
	/* DELTA_REASON                   INTEGER                   4   */
	/* BASE_IND                       CHARACTER                 1   */
	/* CORE_IND                       CHARACTER                 1   */
	/* CUSTOMER_IND                   CHARACTER                 1   */
	/* SPECIAL_IND                    CHARACTER                 1   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteDeltaReleasesTable() {

		try {
			sqlStatement.executeUpdate("DELETE FROM EDESIGN.DELTA_RELEASES");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_release_mdl_types                                      */
	/* -----------------------                                      */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Model Type Name                                              */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASE_MDL_TYPES                                      */
	/* -----------------------                                      */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteDeltaReleaseMdlTypesTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.DELTA_RELEASE_MDL_TYPES");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* delta_release_lib_groups                                     */
	/* ------------------------                                     */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Lib Group Name                                               */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* DELTA_RELEASE_LIB_GROUPS                                     */
	/* ------------------------                                     */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteDeltaReleaseLibGroupsTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.DELTA_RELEASE_LIB_GROUPS");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* orderable_mdl_types                                          */
	/* -------------------                                          */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Model Type Name                                              */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* ORDERABLE_MDL_TYPES                                          */
	/* -------------------                                          */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* ASIC_CODENAME                  VARCHAR                  20   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteOrderableMdlTypesTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.ORDERABLE_MDL_TYPES");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* orderable_lib_groups                                         */
	/* --------------------                                         */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Lib Group Name                                               */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* ORDERABLE_LIB_GROUPS                                         */
	/* --------------------                                         */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* ASIC_CODENAME                  VARCHAR                  20   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteOrderableLibGroupsTable() {

		try {
			sqlStatement.executeUpdate(
				"DELETE FROM EDESIGN.DELTA_RELEASE_LIB_GROUPS");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology                                                   */
	/* version number                                               */
	/* delta_rel_number = DR reference number (CMVC level)          */
	/* packet_name = packet name                                    */
	/* model_type = model type                                      */
	/* library_group = library group                                */
	/* file_size = size of uncompressed packet (bytes)              */
	/* comp_size = size of compressed packet (bytes)                */
	/* packet_type = {1 | 2} - release note or data                 */
	/*                                                              */
	/* This inserts into a table that looks like this               */
	/*                                                              */
	/* TECHNOLOGY                     VARCHAR                  20   */
	/* VERSION_NO                     VARCHAR                  10   */
	/* DELTA_NAME                     VARCHAR                  64   */
	/* PACKET_NAME                    VARCHAR                  80   */
	/* MDL_TYPE_NAME                  VARCHAR                  25   */
	/* LIB_GROUP_NAME                 VARCHAR                  50   */
	/* FILE_SIZE                      INTEGER                   4   */
	/* COMPRESSED_SIZE                INTEGER                   4   */
	/* PACKET_TYPE {1, 2}             INTEGER                   4   */
	/* LAST_USERID                    VARCHAR                  31   */
	/* LAST_TIMESTAMP                 TIMESTAMP                10   */
	/*                                                              */
	/****************************************************************/

	public int deleteDeltaPacketsTable() {

		try {
			sqlStatement.executeUpdate("DELETE FROM EDESIGN.DELTA_PACKETS");
		} catch (Exception ex) {
			ex.printStackTrace();
			message.displayMessage(ex.toString(), 1);
			sendErrorMail(ex);
			// log.writeDebugMsg(ex.getMessage(), 2);
			return FAILURE;
		}

		return SUCCESS;
	}

	public static void main(String[] args) {

		ResourceBundle ff2dbRB =
			ResourceBundle.getBundle("oem.edge.ed.sd.edesign_edsd_ff2db");
		ResourceBundle db2RB =
			ResourceBundle.getBundle("oem.edge.ed.sd.edesign_edsd_db2");

		FlatFileToDB2Table ff2db = new FlatFileToDB2Table(db2RB, ff2dbRB);

		ff2db.deleteGACoresTable();
		ff2db.deleteRestrictedCoresTable();
		ff2db.deleteNonStandardDeliverablesTable();
		ff2db.deleteDeltaPacketsTable();
		ff2db.deleteDeltaReleaseMdlTypesTable();
		ff2db.deleteOrderableMdlTypesTable();
		ff2db.deleteDeltaReleaseLibGroupsTable();
		ff2db.deleteOrderableLibGroupsTable();
		ff2db.deleteDeltaReleasesTable();
		ff2db.deleteTechnologyDocumentTable();

		// This method should be called LAST since it will delete the
		// master table for all foreign keys
		ff2db.deleteASICCodenameTable();
		ff2db.deleteTechnologyVersionTable();

		// This method should be called FIRST since it will create keys
		// used as foreign keys by all other tables
		ff2db.transferToTechnologyVersionTable("testdata/technology_version");
		ff2db.transferToASICCodenameTable("testdata/asic_codename");

		ff2db.transferToGACoresTable("testdata/ga_cores");
		ff2db.transferToRestrictedCoresTable("testdata/restricted_cores");
		ff2db.transferToNonStandardDeliverablesTable(
			"testdata/nonstandard_deliverables");
		ff2db.transferToDeltaReleasesTable("testdata/delta_releases");
		ff2db.transferToDeltaReleaseMdlTypesTable(
			"testdata/delta_release_mdl_types");
		ff2db.transferToOrderableMdlTypesTable("testdata/orderable_mdl_types");
		ff2db.transferToDeltaReleaseLibGroupsTable(
			"testdata/delta_release_lib_groups");
		ff2db.transferToOrderableLibGroupsTable(
			"testdata/orderable_lib_groups");
		ff2db.transferToDeltaPacketsTable("testdata/delta_packets");

		// ff2db.transferToTechnologyDocumentTable ("testdata/technology_document");

	}

}
