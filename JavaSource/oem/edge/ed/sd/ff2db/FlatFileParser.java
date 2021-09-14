package oem.edge.ed.sd.ff2db;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.ed.sd.util.*;
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

/*****************************************************************************/
/* Some comments that apply to all the parsing:                              */
/*      All files and return values are all on one line, and ';' delimited   */
/*      There is a hashtable to determine a role from a given tehcnology     */
/*      The available technologies are:                                      */
/*            CU11 | SA27E | SA12E | SA27 | SA12                             */
/*      A TECH_LIST is a ',' delimited list of TECHNOLOGIES                  */
/*      A RESTRICED_CORE_LIST is a ',' delimited list of restricted cores    */
/*      A NON_STD_DELIVER_LIST is a ',' delimited list.                      */
/*****************************************************************************/

public class FlatFileParser {

	public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
	MessageDisplay message;
	BufferedReader in;
	BufferedWriter out, out1, out2, out3;
	char delim;
	Hashtable techToDK, techToPK, techVersion;

	static Hashtable techMktName; //key is tech
	//new for 5.1.1
	static Hashtable mktTechName; //key is mkt name
	//end of new for 5.1.1

	String line;
	SimpleStringTokenizer tok;

	static final int READ_WRITE_ERROR = 0;
	static final int FILE_NOT_FOUND = 1;
	static final int NO_MORE_TOKENS = 2;
	static final int UNKNOWN_TECHNOLOGY = 3;
	static final int SUCCESS = 4;
	static final int NON_STD_DELIVER_LIST = 5;
	static final int RESTRICTED_CORE_LIST = 6;
	static final int RESTRICTED_BASE_ORD_LIST = 7;
	static final int UNKNOWN_LIST = 8;

	public FlatFileParser(MessageDisplay m) {

		this.message = m;

		delim = ';';
		techToDK = new Hashtable();
		techToPK = new Hashtable();
		techVersion = new Hashtable();
		techMktName = new Hashtable();
		//new for 5.1.1
		mktTechName = new Hashtable();

		/* fill the hashtable */
		techToDK.put("CU11", "CU_11_DK");
		techToDK.put("CU08", "CU_08_DK");
		techToDK.put("SA27E", "SA_27E_DK");
		techToDK.put("SA12E", "SA_12E_DK");
		techToDK.put("SA27", "SA_27_DK");
		techToDK.put("SA12", "SA_12_DK");
		//new for 5.1.1
		techToDK.put("CU65LP", "CU_65LP_DK");
		techToDK.put("CU65HP", "CU_65HP_DK");

		techToPK.put("CU11", "CU_11_PK");
		techToPK.put("CU08", "CU_08_PK");
		techToPK.put("SA27E", "SA_27E_PK");
		techToPK.put("SA12E", "SA_12E_PK");
		techToPK.put("SA27", "SA_27_PK");
		techToPK.put("SA12", "SA_12_PK");
		//new for 5.1.1
		techToPK.put("CU65LP", "CU_65LP_PK");
		techToPK.put("CU65HP", "CU_65HP_PK");

	}

	private void sendErrorMail(Throwable t) {
		FlatFileToDB2Xfer.sendErrorMail(
			"Exception in FlatFileParser",
			"Exception in FlatFileParser: Stacktrace:\n" + getStackTrace(t));
	}

	String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	String executeAndGetStream(String cmd) {

		String inString = "";
		String errString = "";
		int arrSize = 1024;
		byte[] arr = new byte[arrSize];
		int exitValue = -1;
		String[] command = { "/bin/ksh", "-c", cmd };

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

		if (exitValue != 0) {
			message.displayMessage(str, 1);
			return null;
		} else {
			message.displayMessage(str, 3);
			return inString.trim();
		}

	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/*                                                              */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/*   Technology nickname (cu11, sa27e, ...)                     */
	/*   Program Manager name                                       */
	/*   Program Manager phone number                               */
	/*   Program Manager email                                      */
	/*   Version (v1.0, v2.0, ...)                                  */
	/*   FCS date (MM/DD/YY)                                        */
	/*   Technolgy marketing name (CU11, SA27E, ...)                */
	/*   Top level installation directory                           */
	/*   Packet indicator { NEW | OLD }                             */
	/*   Release Manager name                                       */
	/*   Shipper name                                               */
	/*   Shipper  email                                             */
	/*   Edge Indicator { NON_EDGE | EDGE }                         */
	/*   Marketing Name                                             */
	/*                                                              */
	/* This writes out to the output file                           */
	/* TECHNOLOGY                                                   */
	/* ROLE                                                         */
	/* 001                                                          */
	/* VERSION_NO                                                   */
	/* FCS_DATE                                                     */
	/* PM_NAME                                                      */
	/* PM_EMAIL                                                     */
	/****************************************************************/

	public int parseTechReleaseVersionInfo(
		String inputFileName,
		String outputFileName) {

		String skip, pmName, pmEmail, versionNo;
		String fcsDate, technology, outputString, mktName;
		String techType = "";

		try {

			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			while (in.ready()) {

				line = in.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				skip = tok.nextToken();

				pmName = tok.nextToken().trim();
				skip = tok.nextToken();
				pmEmail = tok.nextToken().trim();
				versionNo = tok.nextToken().trim();
				fcsDate = tok.nextToken().trim();

				// should check to see if these names are still consistent with hard coded hash values
				technology = tok.nextToken().trim();

				// the next 6 fields can be skipped

				skip = tok.nextToken();
				skip = tok.nextToken();
				skip = tok.nextToken();
				skip = tok.nextToken();
				skip = tok.nextToken();
				skip = tok.nextToken();

				mktName = tok.nextToken().trim();
				techType = tok.nextToken().trim();

				//new for 6.1.1
				if (!techMktName.containsKey(technology)) {
					//end of new for 6.1.1
					techMktName.put(technology, mktName);
					mktTechName.put(mktName, technology);

				}
				if (!isGoodTechnology(technology))
					continue;

				techVersion.put(technology, versionNo);

				fcsDate = checkDate(fcsDate);

				outputString =
					// (String)techMktName.get(technology) + delim +
	technology + delim +
					//  techToDK.get(technology)          	+ delim +
		//new for 6.1.1
	getDKEntitlement(technology, techType, versionNo) + delim +
					//end of new for 6.1.1
	"001"
		+ delim
		+ versionNo
		+ delim
		+ fcsDate
		+ delim
		+ pmName
		+ delim
		+ pmEmail
		+ delim
		+ techType
		+ delim
		+ "\n";
				//new for 5.1.1
				out.write(outputString, 0, outputString.length());

				outputString =
					// (String)techMktName.get(technology) + delim +
	technology + delim +
					//  techToPK.get(technology)          	+ delim +
		//new for 6.1.1
	getPKEntitlement(technology, techType, versionNo) + delim +
					//end of new for 6.1.1
	"001"
		+ delim
		+ versionNo
		+ delim
		+ fcsDate
		+ delim
		+ pmName
		+ delim
		+ pmEmail
		+ delim
		+ techType
		+ delim
		+ "\n";
				//new for 5.1.1
				out.write(outputString, 0, outputString.length());

			}

			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;
	}

	public String getDKEntitlement(String tech, String type, String ver) {

		if (type.equalsIgnoreCase("IIPMDS"))
			return tech + "_" + ver + "_DK";
		else if (type.equalsIgnoreCase("NON_IIPMDS"))
			return (String) techToDK.get(tech);
		else
			return "";

	}
	public String getPKEntitlement(String tech, String type, String ver) {

		if (type.equalsIgnoreCase("IIPMDS"))
			return tech + "_" + ver + "_PK";
		else if (type.equalsIgnoreCase("NON_IIPMDS"))
			return (String) techToPK.get(tech);
		else
			return "";
	}

	/****************************************************************/
	/* Please read comments at the top of the file                  */
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* USERS_COMPANY                                                */
	/* LOCATION/DIVISION                                            */
	/* CUSTOMER_PROJNAME                                            */
	/* ASIC_CODENAME                                                */
	/* FUTURE_USE                                                   */
	/* TECH_LIST (this is a ';' separeted list of TECHONOLOGY       */
	/*                                                              */
	/* This writes out to the output file                           */
	/*                                                              */
	/* TECHNOLOGY                                                   */
	/* VERSION                                                      */
	/* ASIC_CODENAME                                                */
	/* CUSTOMER_PROJNAME (LOCATION)                                 */
	/* USERS_COMPANY                                                */
	/* CUSTOMER_TYPE    (new for 2.9)                               */
	/*      (taken from customer.list file for 2.9 fix-pack         */
	/****************************************************************/

	public int parseASICCodenameInfo(
		String inputFileName,
		String outputFileName) {

		String skip, usersCompany, asicCodename, techList;
		String technology, version = "", project, location;
		SimpleStringTokenizer techTok;

		// commented out for 2.10
		// String customerType = "REGULAR";
		String customerType;

		String[] inputFiles = { inputFileName, inputFileName + ".EDESIGN" };

		try {

			out = new BufferedWriter(new FileWriter(outputFileName));

			for (int i = 0; i < inputFiles.length; i++) {

				in = new BufferedReader(new FileReader(inputFiles[i]));

				while (in.ready()) {

					line = in.readLine();

					tok = new SimpleStringTokenizer(line, delim);

					/* this happens if there is a blank line at the end of the file! */
					if (!tok.hasMoreTokens())
						continue;

					usersCompany = tok.nextToken().trim();

					if (usersCompany.startsWith("Company")
						|| usersCompany.startsWith("--"))
						continue;

					location = tok.nextToken().trim();
					project = tok.nextToken().trim();
					asicCodename = tok.nextToken().trim();

					// added in 2.9 fix-pack
					customerType = tok.nextToken().trim();

					techList = tok.nextToken().trim();
					techTok = new SimpleStringTokenizer(techList, ',');

					if (!location.equalsIgnoreCase("BLANK"))
						project = project + " (" + location + ")";

					while (techTok.hasMoreTokens()) { //big while

						technology = techTok.nextToken().trim();
						//new for 6.1.1.
						if (technology.indexOf('%') != -1) {
							SimpleStringTokenizer temp =
								new SimpleStringTokenizer(technology, '%');
							if (temp.hasMoreTokens()) {
								technology = temp.nextToken().trim();
								if (isGoodTechnology(technology)) {
									while (temp.hasMoreTokens()) {

										version = temp.nextToken().trim();
										out.write(
											(String) technology
												+ delim
												+ version
												+ delim
												+ asicCodename
												+ delim
												+ project
												+ delim
												+ usersCompany
												+ delim
												+ customerType
												+ delim
												+ "\n");
									}
								}
							}
						}

						//end of new for 6.1.1
						else if (isGoodTechnology(technology)) {
							// need some error checking here
							version = (String) techVersion.get(technology);
							out.write(
								(String) technology
									+ delim
									+ version
									+ delim
									+ asicCodename
									+ delim
									+ project
									+ delim
									+ usersCompany
									+ delim
									+ customerType
									+ delim
									+ "\n");

						} else {
							if (technology.equals("EDESIGN")) {
								version = "v1.0";
								out.write(
									(String) technology
										+ delim
										+ version
										+ delim
										+ asicCodename
										+ delim
										+ project
										+ delim
										+ usersCompany
										+ delim
										+ customerType
										+ delim
										+ "\n");
							} else
								continue;
						}

						// out.write((String)techMktName.get(technology) + delim +

					}

				}

				in.close();

			}

			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* PLATFORM  (Short Name)                                       */
	/* PLATFORM_NAME  (User Name)                                   */
	/* KIT_SIZE (in bytes)                                          */
	/*                                                              */
	/* This writes out to the output file                           */
	/*                                                              */
	/* 'XMX'                                                        */
	/* 'XMX'                                                        */
	/* 'v1.0'                                                       */
	/* PLATFORM                                                     */
	/* PLATFORM_NAME                                                */
	/* KIT_SIZE (in MB)                                             */
	/****************************************************************/

	public int parseXMXInfo(String inputFileName, String outputFileName) {

		String platform, platformName;
		int kitSize;

		try {

			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			while (in.ready()) {

				line = in.readLine();

				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				platform = tok.nextToken().trim();
				platformName = tok.nextToken().trim();
				kitSize = Integer.parseInt(tok.nextToken()) / 1024 / 1024;

				out.write(
					"XMX"
						+ delim
						+ "XMX"
						+ delim
						+ "v1.0"
						+ delim
						+ platform
						+ delim
						+ platformName
						+ delim
						+ String.valueOf(kitSize)
						+ delim
						+ "\n");
			}

			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* PLATFORM                                                     */
	/* VM | NoVM                                                    */
	/* PLATFORM_NAME                                                */
	/* KIT_SIZE (in bytes)                                          */
	/*                                                              */
	/* This writes out to the output file                           */
	/*                                                              */
	/* 'DIESIZER'                                                   */
	/* 'DIESIZER'                                                   */
	/* 'v1.0'                                                       */
	/* PLATFORM/VM or PLATFORM/NoVM                                 */
	/* KIT_SIZE (in MB)                                          */
	/****************************************************************/

	public int parseDieSizerInfo(String inputFileName, String outputFileName) {

		String platform, jvm, platformName, ks;
		int kitSize;

		try {

			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			while (in.ready()) {

				line = in.readLine();

				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				platform = tok.nextToken().trim();
				jvm = tok.nextToken().trim();
				platformName = tok.nextToken().trim();
				ks = tok.nextToken();

				kitSize = Integer.valueOf(ks).intValue();
				kitSize = kitSize / 1024 / 1024;

				out.write(
					"DIESIZER"
						+ delim
						+ "DIESIZER"
						+ delim
						+ "v1.0"
						+ delim
						+ platform
						+ "/"
						+ jvm
						+ delim
						+ platformName
						+ delim
						+ String.valueOf(kitSize)
						+ delim
						+ "\n");
			}

			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* CORE_NAME                                                    */
	/* CORE_TYPE (GA|R - we only care about GA)                     */
	/* CORE_LIB                                                     */
	/* NON_INITIAL_CORE                                             */
	/* note: technology and version are determined from the         */
	/*       input file directory                                   */
	/*                                                              */
	/* This writes out to the output file                           */
	/* ROLE                                                         */
	/* VERSION_NO                                                   */
	/* CORE_NAME                                                    */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/

	public int parseGeneralAvailableCoresInfo(
		String inputFileName,
		String outputFileName) {

		String coreName,
			coreType,
			coreSize,
			coreLibraryGroup,
			libraryGroupList,
			newCore,
			skip;
		TechnologyInfo ti;
		SimpleStringTokenizer libraryGroups;
		;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			/* this should really never fail, but I'll check it anyway */
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				coreName = tok.nextToken().trim();

				coreType = tok.nextToken().trim();
				if (!coreType.equals("GA"))
					continue;

				coreSize = tok.nextToken().trim();
				libraryGroupList = tok.nextToken().trim();

				libraryGroups =
					new SimpleStringTokenizer(libraryGroupList, ',');

				if (tok.hasMoreTokens()
					&& tok.nextToken().trim().equals("NEW"))
					newCore = "Y";
				else
					newCore = "N";

				while (libraryGroups.hasMoreTokens()) {

					coreLibraryGroup = libraryGroups.nextToken().trim();

					out.write(
						(String) ti.getName()
							+ delim
							+ ti.getVersion()
							+ delim
							+ coreName
							+ delim
							+ coreSize
							+ delim
							+ delim
							+ delim
							+ coreLibraryGroup
							+ delim
							+ newCore
							+ delim
							+ "\n");

				}
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	//new for 5.1.1

	public int parseDAModelTypeInfo(
		String inputFileName,
		String outputFileName) {

		String daName, modelTypeList, daType;

		TechnologyInfo ti;
		SimpleStringTokenizer tok;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));
			ti = parseFileNameForTechInfo(inputFileName);

			/* this should really never fail, but I'll check it anyway */
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {

				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;
				daName = tok.nextToken().trim();
				tok.nextToken();
				daType = tok.nextToken().trim();
				tok.nextToken();
				modelTypeList = tok.nextToken().trim();

				out.write(
					(String) ti.getName()
						+ delim
						+ ti.getVersion()
						+ delim
						+ daName
						+ delim
						+ daType
						+ delim
						+ modelTypeList
						+ delim
						+ "\n");
			}

			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;

	}
	//end of new for 5.1.1

	// new for 3.10.1
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TOOL_NAME                                                    */
	/* TOOL_VERSION                                                 */
	/* TOOL_TYPE (Standard | Non-standard-GA | Non-standard-R)      */
	/*           - we only care about Platform-GA, Platform-R)      */
	/* TOOL_SIZE                                                    */
	/* MODEL_TYPE_LIST                                              */
	/* note: technology and version are determined from the         */
	/*       input file directory                                   */
	/*                                                              */
	/* This writes out to the output file platform.mdltype          */
	/* Technology, Version, platform, platform_type, mdl_type       */
	/****************************************************************/

	public int parsePlatformMDLInfo(
		String inputFileName,
		String outputFileName) {

		String toolName, toolType, mdlTypeList, typeInd;
		TechnologyInfo ti;

		SimpleStringTokenizer mdlType;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				toolName = tok.nextToken().trim();
				tok.nextToken();
				toolType = tok.nextToken().trim();
				tok.nextToken();
				mdlTypeList = tok.nextToken().trim();

				if (!toolType.startsWith("Platform"))
					continue;

				if (toolType.endsWith("GA"))
					typeInd = "GA";
				else if (toolType.endsWith("R"))
					typeInd = "RE";
				else
					continue;

				mdlType = new SimpleStringTokenizer(mdlTypeList, ',');

				while (mdlType.hasMoreTokens()) {

					out.write(
						(String) ti.getName()
							+ delim
							+ ti.getVersion()
							+ delim
							+ getPlatformAlias(toolName)
							+ delim
							+ typeInd
							+ delim
							+ mdlType.nextToken().trim()
							+ delim
							+ "\n");
				}
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	private String getPlatformAlias(String platform) {

		if (platform.equals("RS6K"))
			return ("aix");
		else if (platform.equals("RS6K64"))
			return ("aix64");
		else if (platform.equals("RS6K64_51"))
			return ("aix5_64");
		//new for 4.2.1 fixpack
		else if (platform.equals("LIN64OPT"))
			return ("linux_amd64");
		else if (platform.equals("LIN32X86"))
			return ("linux32");
		//end of new for 4.2.1 fixpack
		else
			return platform.toLowerCase();
	}

	//end for new for 3.10.1

	// new for 4.2.1
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TOOL_NAME                                                    */
	/* TOOL_VERSION                                                 */
	/* TOOL_TYPE (Standard | Non-standard-GA | Non-standard-R)      */
	/*           - we only care about Standard                      */
	/* TOOL_SIZE                                                    */
	/* MODEL_TYPE_LIST                                              */
	/* note: technology and version are determined from the         */
	/*       input file directory                                   */
	/*                                                              */
	/* This writes out to the output file platform.mdltype          */
	/* Technology, Version, platform, platform_type, mdl_type       */
	/****************************************************************/

	public int parsePacketMDLInfo(
		String inputFileName,
		String outputFileName) {

		String toolType, mdlTypeList;
		TechnologyInfo ti;
		Vector standardMDL = new Vector();

		SimpleStringTokenizer mdlType;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				tok.nextToken();
				tok.nextToken();
				toolType = tok.nextToken().trim();
				tok.nextToken();
				mdlTypeList = tok.nextToken().trim();

				if (!toolType.equalsIgnoreCase("Standard"))
					continue;

				mdlType = new SimpleStringTokenizer(mdlTypeList, ',');

				while (mdlType.hasMoreTokens()) {

					String mdl = mdlType.nextToken().trim();
					if (standardMDL == null)
						standardMDL.addElement(mdl);

					else if (
						standardMDL != null && !standardMDL.contains(mdl)) {

						standardMDL.addElement(mdl);

						out.write(
							(String) ti.getName()
								+ delim
								+ ti.getVersion()
								+ delim
								+ mdl
								+ delim
								+ toolType
								+ delim
								+ "\n");

					}
				}
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* LIB_GROUP_NAME                                               */
	/* LIB_GROUP_TYPE                                               */
	/****************************************************************/

	public int parsePacketsLIBInfo(
		String inputFileName,
		String outputFileName) {

		String libName, libType;
		TechnologyInfo ti;

		SimpleStringTokenizer mdlType;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				libName = tok.nextToken().trim();
				libType = tok.nextToken().trim();

				out.write(
					(String) ti.getName()
						+ delim
						+ ti.getVersion()
						+ delim
						+ libName
						+ delim
						+ libType
						+ delim
						+ "\n");

			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	//end of new for 4.2.1

	//new for 5.1.1 CU65

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* technology;version;revision                                  */
	/* asiccode;company;proj;location;platformlist;ipversionlist;ipcategorylist                                                                    */
	/*                                                              */
	/****************************************************************/

	public int parseIPEntitlement(
		String inputFileName,
		String outputFileName) {

		String technology = "", versionNo = "", revisionNo = "", mktName;
		String asicCode, company, projName;
		String platformList, ipVersionList, ipCategoryList, skip;
		String ipver, ipcat;
		SimpleStringTokenizer tokip, tokcat, tokpt;

		try {

			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			if (in.ready()) {

				line = in.readLine();
				tok = new SimpleStringTokenizer(line, ';');

				technology = (String) mktTechName.get(tok.nextToken().trim());
				if (!isGoodTechnology(technology))
					return UNKNOWN_TECHNOLOGY;

				versionNo = tok.nextToken().trim();
				revisionNo = tok.nextToken().trim();
			}
			while (in.ready()) {

				line = in.readLine();
				tok = new SimpleStringTokenizer(line, ';');
				if (!tok.hasMoreTokens())
					continue;
				asicCode = tok.nextToken().trim();
				company = tok.nextToken().trim();
				projName = tok.nextToken().trim();
				skip = tok.nextToken();
				platformList = tok.nextToken().trim();
				tokpt = new SimpleStringTokenizer(platformList, ',');
				String plat = "";
				while (tokpt.hasMoreTokens()) {
					String temp = tokpt.nextToken().trim();
					plat = plat + getPlatformAlias(temp) + ",";
				}
				ipVersionList = tok.nextToken().trim();
				ipCategoryList = tok.nextToken().trim();
				tokip = new SimpleStringTokenizer(ipVersionList, ',');
				tokcat = new SimpleStringTokenizer(ipCategoryList, ',');

				while (tokip.hasMoreTokens()) {
					ipver = tokip.nextToken().trim();
					ipcat = tokcat.nextToken().trim();
					out.write(
						asicCode
							+ delim
							+ projName
							+ delim
							+ company
							+ delim
							+ technology
							+ delim
							+ versionNo
							+ delim
							+ plat
							+ delim
							+ revisionNo
							+ delim
							+ ipver
							+ delim
							+ ipcat
							+ delim
							+ "\n");
				}
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	//end of new for 5.1.1

	//new for 6.1.1 CU65
	//TODO
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* revisionname_revisionnumber                                  */
	/*                                                              */
	/****************************************************************/

	public int parseReleasedIPRevisions(
		String inputFileName,
		String outputFileName,String techVer) {

			String technology = "", versionNo = "", revNo = "", revName = "";
			//SimpleStringTokenizer tokip, tokcat, tokpt;

			try {

				in =
					new BufferedReader(
						new FileReader(inputFileName));
				out =
					new BufferedWriter(
						new FileWriter(outputFileName));
				tok = new SimpleStringTokenizer(techVer, '/');
				technology = tok.nextToken().trim();
				technology = technology.substring(0,1) + technology.substring(1,2).toLowerCase() + "-" + technology.substring(2,technology.length());
				versionNo = tok.nextToken().trim();

				while (in.ready()) {

					line = in.readLine();
					tok = new SimpleStringTokenizer(line, '_');
					if (!tok.hasMoreTokens())
						continue;
					Vector tokens = new Vector();
					tokens = tok.getTokens();
					String tok1,tok2;
					tok1 = tokens.get(0).toString();
					int i=1;
					while ( i< tokens.size()-1)
					{
						tok1 = tok1 + "_" + tokens.get(i).toString();
						i++;

					}
					revName = tok1;
					revNo = tokens.get(tokens.size()-1).toString();

					out.write(
						technology
							+ delim
							+ versionNo
							+ delim
							+ revName
							+ delim
							+ revNo
							+ delim
							+ "\n");

				}
				in.close();
				out.close();



		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	//end of new for 6.1.1


	// new for 2.9
	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* TOOL_NAME                                                    */
	/* TOOL_VERSION                                                 */
	/* TOOL_TYPE (Standard | Non-standard-GA | Non-standard-R)      */
	/*                   - we only care about Non-standard-GA)      */
	/* TOOL_SIZE                                                    */
	/* MODEL_TYPE_LIST                                              */
	/* note: technology and version are determined from the         */
	/*       input file directory                                   */
	/*                                                              */
	/* This writes out to the output file                           */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* TOOL_NAME                                                    */
	/* TOOL_SIZE                                                    */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/

	public int parseGeneralAvailableNSDInfo(
		String inputFileName,
		String outputFileName) {

		String toolName, toolType, toolSize, skip;
		TechnologyInfo ti;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				toolName = tok.nextToken().trim();
				tok.nextToken();
				toolType = tok.nextToken().trim();
				toolSize = tok.nextToken().trim();

				if (!toolType.equals("Non-standard-GA"))
					continue;

				out.write(
					(String) ti.getName()
						+ delim
						+ ti.getVersion()
						+ delim
						+ toolName
						+ delim
						+ toolSize
						+ delim
						+ delim
						+ delim
						+ "\n");
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	// new for 2.9 fix-pack

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* BASE_ORD_NAME                                                */
	/* BASE_ORD_TYPE (GA | R - we only care about GA)               */
	/* BASE_ORD_LIB                                                 */
	/* note: technology and version are determined from the         */
	/*       input file directory                                   */
	/*                                                              */
	/* This writes out to the output file                           */
	/* ROLE                                                         */
	/* VERSION_NO                                                   */
	/* BASE_ORD_NAME                                                */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/

	public int parseGaBaseOrdInfo(
		String inputFileName,
		String outputFileName) {

		String baseOrdName, baseOrdType, baseOrdSize, skip, baseInd;
		TechnologyInfo ti;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			/* this should really never fail, but I'll check it anyway */
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				baseOrdName = tok.nextToken().trim();
				baseOrdType = tok.nextToken().trim();
				baseOrdSize = tok.nextToken().trim();

				//new for 3.10.1
				tok.nextToken().trim();
				//  baseInd = tok.nextToken().trim();
				if (tok.hasMoreTokens()
					&& tok.nextToken().trim().equals("NEW"))
					baseInd = "Y";

				else
					baseInd = "N";
				//end of new for 3.10.1

				if (!baseOrdType.equals("GA"))
					continue;

				// out.write((String)techMktName.get(ti.getName()) + delim +
				out.write(
					(String) ti.getName()
						+ delim
						+ ti.getVersion()
						+ delim
						+ baseOrdName
						+ delim
						+ baseOrdSize
						+ delim
						+ delim
						+ delim
						+ baseInd
						+ delim
						+ "\n");
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}
		return SUCCESS;
	}

	/****************************************************************/
	/* Both functions that parse the shipping.customer.data file    */
	/*  are very similar, so this is the function that they both    */
	/*  call to do the real work                                    */
	/****************************************************************/
	public int parseShippingCustomerData(
		String inputFileName,
		String outputFileName,
		int list) {

		String asicCodename,
			nsdList,
			nsdSizeList,
			coreList,
			coreSizeList,
			baseOrdList,
			baseOrdSizeList,
			newCore,
			skip;
		SimpleStringTokenizer listTok, sizeTok;
		TechnologyInfo ti;
		//new for 3.10.1
		String baseOrdFile, baseOrdSize, baseOrdName, newResBase, newBaseInd;
		//end of new for 3.10.1

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			while (in.ready()) {

				line = in.readLine();
				tok = new SimpleStringTokenizer(line, delim);

				/* this happens if there is a blank line at the end of the file! */
				if (!tok.hasMoreTokens())
					continue;

				asicCodename = tok.nextToken().trim();

				if (!isGoodTechnology(ti.getName()))
					continue;

				coreList = tok.nextToken().trim();
				coreSizeList = tok.nextToken().trim();
				nsdList = tok.nextToken().trim();
				nsdSizeList = tok.nextToken().trim();
				baseOrdList = tok.nextToken().trim();
				baseOrdSizeList = tok.nextToken().trim();

				if (list == RESTRICTED_CORE_LIST) {
					listTok = new SimpleStringTokenizer(coreList, ',');
					sizeTok = new SimpleStringTokenizer(coreSizeList, ',');
				} else if (list == NON_STD_DELIVER_LIST) {
					listTok = new SimpleStringTokenizer(nsdList, ',');
					sizeTok = new SimpleStringTokenizer(nsdSizeList, ',');
				} else if (list == RESTRICTED_BASE_ORD_LIST) {
					listTok = new SimpleStringTokenizer(baseOrdList, ',');
					sizeTok = new SimpleStringTokenizer(baseOrdSizeList, ',');
				} else
					return UNKNOWN_LIST;

				if (list == RESTRICTED_CORE_LIST) {
					while (listTok.hasMoreTokens()) {
						String coreName = listTok.nextToken().trim();
						String coreSize = sizeTok.nextToken().trim();
						String coresFile =
							inputFileName.substring(
								0,
								inputFileName.lastIndexOf('/'))
								+ "/shipping.cores.data";
						String libraryGroupList =
							executeAndGetStream(
								"grep '^"
									+ coreName
									+ ";' "
									+ coresFile
									+ " | cut -d ';' -f4");

						if (libraryGroupList == null)
							throw new java.util.NoSuchElementException(
								"No library group entry found for core: "
									+ coreName
									+ " in "
									+ coresFile);

						SimpleStringTokenizer libraryGroups =
							new SimpleStringTokenizer(libraryGroupList, ',');

						String newCoreInd =
							executeAndGetStream(
								"grep '^"
									+ coreName
									+ ";' "
									+ coresFile
									+ " | cut -d ';' -f5");
						if (newCoreInd != null
							&& newCoreInd.trim().equals("NEW"))
							newCore = "Y";
						else
							newCore = "N";

						while (libraryGroups.hasMoreTokens()) {

							String coreLibraryGroup =
								libraryGroups.nextToken().trim();

							out.write(
								(String) (ti.getName())
									+ delim
									+ ti.getVersion()
									+ delim
									+ asicCodename
									+ delim
									+ coreName
									+ delim
									+ coreSize
									+ delim
									+ delim
									+ delim
									+ coreLibraryGroup
									+ delim
									+ newCore
									+ delim
									+ "\n");

						}
					}
				}

				//new for 3.10.1

				else if (list == RESTRICTED_BASE_ORD_LIST) {
					while (listTok.hasMoreTokens()) {
						baseOrdName = listTok.nextToken().trim();
						baseOrdSize = sizeTok.nextToken().trim();
						baseOrdFile =
							inputFileName.substring(
								0,
								inputFileName.lastIndexOf('/'))
								+ "/shipping.base_orderable.data";

						newResBase =
							executeAndGetStream(
								"grep '^"
									+ baseOrdName
									+ ";' "
									+ baseOrdFile
									+ " | cut -d ';' -f4");
						System.out.println("newResBase is " + newResBase);
						if (newResBase != null
							&& newResBase.trim().equals("NEW"))
							newBaseInd = "Y";
						else
							newBaseInd = "N";

						out.write(
							(String) (ti.getName())
								+ delim
								+ ti.getVersion()
								+ delim
								+ asicCodename
								+ delim
								+ baseOrdName
								+ delim
								+ baseOrdSize
								+ delim
								+ delim
								+ delim
								+ newBaseInd
								+ delim
								+ "\n");

					}
				}
				//end of new for 3.10.1
				else {
					while (listTok.hasMoreTokens()) {
						out.write(
							(String) (ti.getName())
								+ delim
								+ ti.getVersion()
								+ delim
								+ asicCodename
								+ delim
								+ listTok.nextToken().trim()
								+ delim
								+ sizeTok.nextToken().trim()
								+ delim
								+ delim
								+ delim
								+ "\n");
					}
				}
			}
			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* ASIC_CODENAME                                                */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* NSD_LIST                                                     */
	/* RESTRICTED_CORE_LIST                                         */
	/* RESTRICTED_BASE_ORD_LIST                                     */
	/*                                                              */
	/* This writes out to the output file                           */
	/* for each CORE in RESTRICTED_CORE_LIST                        */
	/* ASIC_CODENAME                                                */
	/* ROLE                                                         */
	/* VERSION_NO                                                   */
	/* CORE_NAME                                                    */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/
	public int parseRestrictedCoresInfo(
		String inputFileName,
		String outputFileName) {
		return parseShippingCustomerData(
			inputFileName,
			outputFileName,
			RESTRICTED_CORE_LIST);
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* ASIC_CODENAME                                                */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* NSD_LIST                                                     */
	/* RESTRICTED_CORE_LIST                                         */
	/* RESTRICTED_BASE_ORD_LIST                                     */
	/*                                                              */
	/* This writes out to the output file                           */
	/* for each NSD in NSD_LIST                                     */
	/* ASIC_CODENAME                                                */
	/* ROLE                                                         */
	/* VERSION_NO                                                   */
	/* NSD                                                          */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/
	public int parseNonStandardDeliverablesInfo(
		String inputFileName,
		String outputFileName) {
		return parseShippingCustomerData(
			inputFileName,
			outputFileName,
			NON_STD_DELIVER_LIST);
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/* ASIC_CODENAME                                                */
	/* TECHNOLOGY                                                   */
	/* VERSION_NO                                                   */
	/* NSD_LIST                                                     */
	/* RESTRICTED_CORE_LIST                                         */
	/* RESTRICTED_BASE_ORD_LIST                                     */
	/*                                                              */
	/* This writes out to the output file                           */
	/* for each BASE_ORD in RESTRICTED_BASE_ORD_LIST                        */
	/* ASIC_CODENAME                                                */
	/* ROLE                                                         */
	/* VERSION_NO                                                   */
	/* BASE_ORD_NAME                                                    */
	/* followed by the normal delimiter, plus 2 more                */
	/****************************************************************/
	public int parseResBaseOrdInfo(
		String inputFileName,
		String outputFileName) {
		return parseShippingCustomerData(
			inputFileName,
			outputFileName,
			RESTRICTED_BASE_ORD_LIST);
	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* ref_number = Delta Release reference number (CMVC level)     */
	/* reason = reason code for performing Delta Release            */
	/*          {FIX | ENHANCEMENT | NEW | OTHER}        */
	/* type_list = comma delimited list of DR types                 */
	/*             {BASE, CORE, CUSTOMER, SPECIAL}                  */
	/* date_complete = completion date (YYYYMMDD)                   */
	/* time_complete = completion time (HH:MM)                      */
	/* sequence_nbr = delta sequence number (4 digits)              */
	/* model_type_list = comma delimited list of model types        */
	/* lib_group_list = comma delimited list of library groups      */
	/* description = text description of Delta Release              */
	/*               (filtered for double quote, "\n" and ";")      */
	/*                                                              */
	/* This writes out to three output files                        */
	/*                                                              */
	/* note: technology and version are determined from the         */
	/*       input directory name                                   */
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
	/* Delta Reason (1|2|3|4)                                       */
	/* Base Indicator ("Y" if type=BASE, "N" otherwise)             */
	/* Core Indicator ("Y" if type=CORE, "N" otherwise)             */
	/* Customer Indicator ("Y" if type=CUSTOMER, "N" otherwise)     */
	/* Special Indicator ("Y" if type=SPECIAL, "N" otherwise)       */
	/*                                                              */
	/* delta_release_mdl_types                                      */
	/* -----------------------                                      */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Model Type Name                                              */
	/*                                                              */
	/* delta_release_lib_groups                                     */
	/* ------------------------                                     */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* Delta Name                                                   */
	/* Lib Group Name                                               */
	/*                                                              */
	/****************************************************************/

	public int parseDeltaReleaseInfo(
		String inputFileName,
		String outputDirName) {

		String skip,
			version,
			deltaName,
			deltaDate,
			deltaDesc,
			deltaTime,
			deltaSeq,
			deltaDone;
		String reason,
			type,
			type_list,
			model_type_list,
			lib_group_list,
			modelType,
			libGroup;
		String baseInd, coreInd, customerInd, specialInd;
		int deltaReason;
		SimpleStringTokenizer typeTok, modelTok, libTok;
		TechnologyInfo ti;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out1 =
				new BufferedWriter(
					new FileWriter(outputDirName + "/delta_releases"));
			out2 =
				new BufferedWriter(
					new FileWriter(outputDirName + "/delta_release_mdl_types"));
			out3 =
				new BufferedWriter(
					new FileWriter(
						outputDirName + "/delta_release_lib_groups"));

			ti = parseFileNameForTechInfo(inputFileName);

			/* this should really never fail, but I'll check it anyway */
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();

				tok = new SimpleStringTokenizer(line, delim);

				if (!tok.hasMoreTokens())
					continue;

				deltaName = tok.nextToken().trim();
				reason = tok.nextToken().trim();
				type_list = tok.nextToken().trim();
				deltaDate = tok.nextToken().trim();
				deltaTime = tok.nextToken().trim();
				deltaSeq = tok.nextToken().trim();
				model_type_list = tok.nextToken().trim();
				lib_group_list = tok.nextToken().trim();
				deltaDesc = tok.nextToken().trim();
				deltaDone = tok.nextToken().trim();

				if (!deltaDone.equals("DONE"))
					continue;

				deltaDate = Date_YYYYMMDD_2_MMDDYYYY(deltaDate);

				if (reason.equals("FIX"))
					deltaReason = 1;
				else if (reason.equals("ENHANCEMENT"))
					deltaReason = 2;
				else if (reason.equals("NEW"))
					deltaReason = 3;
				else
					deltaReason = 4;

				baseInd = "N";
				coreInd = "N";
				customerInd = "N";
				specialInd = "N";

				typeTok = new SimpleStringTokenizer(type_list, ',');
				while (typeTok.hasMoreTokens()) {
					type = typeTok.nextToken().trim();

					if (type.equals("BASE"))
						baseInd = "Y";
					if (type.equals("CORE"))
						coreInd = "Y";
					if (type.equals("CUSTOMER"))
						customerInd = "Y";
					if (type.equals("SPECIAL"))
						specialInd = "Y";

					continue;
				}

				// out1.write((String)techMktName.get(ti.getName()) + delim +
				out1.write(
					(String) (ti.getName())
						+ delim
						+ ti.getVersion()
						+ delim
						+ deltaName
						+ delim
						+ deltaDate
						+ delim
						+ deltaTime
						+ ":00"
						+ delim
						+ deltaSeq
						+ delim
						+ deltaDesc
						+ delim
						+ deltaReason
						+ delim
						+ baseInd
						+ delim
						+ coreInd
						+ delim
						+ customerInd
						+ delim
						+ specialInd
						+ delim
						+ "\n");

				modelTok = new SimpleStringTokenizer(model_type_list, ',');
				while (modelTok.hasMoreTokens()) {
					modelType = modelTok.nextToken().trim();

					// out2.write((String)techMktName.get(ti.getName()) + delim +
					out2.write(
						(String) (ti.getName())
							+ delim
							+ ti.getVersion()
							+ delim
							+ deltaName
							+ delim
							+ modelType
							+ delim
							+ "\n");

					continue;
				}

				libTok = new SimpleStringTokenizer(lib_group_list, ',');
				while (libTok.hasMoreTokens()) {
					libGroup = libTok.nextToken().trim();

					// out3.write((String)techMktName.get(ti.getName()) + delim +
					out3.write(
						(String) (ti.getName())
							+ delim
							+ ti.getVersion()
							+ delim
							+ deltaName
							+ delim
							+ libGroup
							+ delim
							+ "\n");

					continue;
				}

			}

			in.close();
			out1.close();
			out2.close();
			out3.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* asic codename                                                */
	/* model_type_list = comma delimited list of model types        */
	/* lib_group_list = comma delimited list of library groups      */
	/*                                                              */
	/* This writes out to two output files                          */
	/*                                                              */
	/* note: technology and version are determined from the         */
	/*       input directory name                                   */
	/*                                                              */
	/* orderable_mdl_types                                          */
	/* -------------------                                          */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Model Type Name                                              */
	/*                                                              */
	/* orderable_lib_groups                                         */
	/* --------------------                                         */
	/*                                                              */
	/* Technology                                                   */
	/* Version No                                                   */
	/* ASIC Codename                                                */
	/* Lib Group Name                                               */
	/*                                                              */
	/****************************************************************/

	public int parseOrderableComponentsInfo(
		String inputFileName,
		String outputDirName) {

		String asicCodename,
			model_type_list,
			lib_group_list,
			modelType,
			libGroup;
		SimpleStringTokenizer typeTok, modelTok, libTok;
		TechnologyInfo ti;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out2 =
				new BufferedWriter(
					new FileWriter(outputDirName + "/orderable_mdl_types"));
			out3 =
				new BufferedWriter(
					new FileWriter(outputDirName + "/orderable_lib_groups"));

			ti = parseFileNameForTechInfo(inputFileName);

			// this should really never fail, but I'll check it anyway
			// should also add isGoodVersion() test --
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();

				tok = new SimpleStringTokenizer(line, delim);

				if (!tok.hasMoreTokens())
					continue;

				asicCodename = tok.nextToken().trim();
				model_type_list = tok.nextToken().trim();
				lib_group_list = tok.nextToken().trim();

				modelTok = new SimpleStringTokenizer(model_type_list, ',');
				while (modelTok.hasMoreTokens()) {
					modelType = modelTok.nextToken().trim();

					// out2.write((String)techMktName.get(ti.getName()) + delim +
					out2.write(
						(String) (ti.getName())
							+ delim
							+ ti.getVersion()
							+ delim
							+ asicCodename
							+ delim
							+ modelType
							+ delim
							+ "\n");

					continue;
				}

				libTok = new SimpleStringTokenizer(lib_group_list, ',');
				while (libTok.hasMoreTokens()) {
					libGroup = libTok.nextToken().trim();

					// out3.write((String)techMktName.get(ti.getName()) + delim +
					out3.write(
						(String) (ti.getName())
							+ delim
							+ ti.getVersion()
							+ delim
							+ asicCodename
							+ delim
							+ libGroup
							+ delim
							+ "\n");

					continue;
				}

			}

			in.close();
			out2.close();
			out3.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;

	}

	/****************************************************************/
	/* This expects a file that looks like this                     */
	/*                                                              */
	/* packet_name = packet name                                    */
	/* file_size = size of uncompressed packet (bytes)              */
	/* comp_size = size of compressed packet (bytes)                */
	/* bl_name = name of build list used to create packet           */
	/* who_built = AFS user id of person who created the packet     */
	/* date_built = date stamp of build date (yyyymmdd)             */
	/* time_built = time stamp of build time (hh:mm)                */
	/* delta_rel_number = DR reference number (CMVC level)          */
	/* packet_type = {RNOTE | DATA} - release note or data          */
	/* model_type = model type                                      */
	/* library_group = library group                                */
	/*                                                              */
	/* This writes out to the output file                           */
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
	/****************************************************************/

	public int parseDeltaPacketList(
		String inputFileName,
		String outputFileName) {

		String skip, deltaName, packetName;
		String fileSize, compSize, packetType;
		String modelType, libGroup;
		String coreList, coreName;
		String baseOrdList, baseOrdName;
		SimpleStringTokenizer coreTok;
		SimpleStringTokenizer baseOrdTok;
		TechnologyInfo ti;

		try {
			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new FileWriter(outputFileName));

			ti = parseFileNameForTechInfo(inputFileName);

			/* this should really never fail, but I'll check it anyway */
			if (!isGoodTechnology(ti.getName()))
				return UNKNOWN_TECHNOLOGY;

			while (in.ready()) {
				line = in.readLine();

				tok = new SimpleStringTokenizer(line, delim);

				if (!tok.hasMoreTokens())
					continue;

				packetName = tok.nextToken().trim();
				fileSize = tok.nextToken().trim();
				compSize = tok.nextToken().trim();
				skip = tok.nextToken().trim();
				skip = tok.nextToken().trim();
				skip = tok.nextToken().trim();
				skip = tok.nextToken().trim();
				deltaName = tok.nextToken().trim();
				packetType = tok.nextToken().trim();
				modelType = tok.nextToken().trim();
				libGroup = tok.nextToken().trim();
				coreList = tok.nextToken().trim();
				baseOrdList = tok.nextToken().trim();

				if (packetType.equals("RNOTE"))
					packetType = "1";
				if (packetType.equals("DATA"))
					packetType = "2";
				// Error checking ??

				if (coreList.indexOf(',') >= 0) {
					coreTok = new SimpleStringTokenizer(coreList, ',');
					int coreNameCounter = 1;
					while (coreTok.hasMoreTokens()) {
						coreName = coreTok.nextToken().trim();

						out
							.write(
								(String) (ti.getName())
								+ delim
								+ ti.getVersion()
								+ delim
								+ deltaName
								+ delim
								+ packetName
								+ "-"
								+ coreNameCounter
								+ delim
								+ modelType
								+ delim
								+ libGroup
								+ delim
								+ fileSize
								+ delim
								+ compSize
								+ delim
								+ packetType
								+ delim
								+ coreName
								+ delim
								+ ""
								+ delim
						+ // empty base orderable name
						packetName + delim + //new for 3.7.1
						"\n");

						coreNameCounter++;
					}
				} else if (baseOrdList.indexOf(',') >= 0) {
					baseOrdTok = new SimpleStringTokenizer(baseOrdList, ',');
					int baseOrdNameCounter = 1;
					while (baseOrdTok.hasMoreTokens()) {
						baseOrdName = baseOrdTok.nextToken().trim();

						out
							.write(
								(String) (ti.getName())
								+ delim
								+ ti.getVersion()
								+ delim
								+ deltaName
								+ delim
								+ packetName
								+ "-"
								+ baseOrdNameCounter
								+ delim
								+ modelType
								+ delim
								+ libGroup
								+ delim
								+ fileSize
								+ delim
								+ compSize
								+ delim
								+ packetType
								+ delim
								+ ""
								+ delim
						+ // empty core name
						baseOrdName + delim + packetName + delim +
						//new for 3.7.1
						"\n");

						baseOrdNameCounter++;
					}
				} else {
					coreName = coreList;
					baseOrdName = baseOrdList;

					out
						.write(
							(String) (ti.getName())
							+ delim
							+ ti.getVersion()
							+ delim
							+ deltaName
							+ delim
							+ packetName
							+ "-1"
							+ delim
							+ modelType
							+ delim
							+ libGroup
							+ delim
							+ fileSize
							+ delim
							+ compSize
							+ delim
							+ packetType
							+ delim
							+ coreName
							+ delim
							+ baseOrdName
							+ delim
							+ packetName
							+ delim
					+ //new for 3.7.1
					"\n");
				}

			}

			in.close();
			out.close();

		} catch (Exception ex) {
			System.out.println("Warning: Stacktrace:\n " + getStackTrace(ex));
			sendErrorMail(ex);
			return READ_WRITE_ERROR;
		}

		return SUCCESS;
	}

	/****************************************************************/
	/* Returns true if given tech is a key in the hashtable         */
	/****************************************************************/
	public boolean isGoodTechnology(String technology) {
		boolean foundTech = false;
		Enumeration e = techToDK.keys();
		while (e.hasMoreElements()) {
			if (technology.equals((String) e.nextElement()))
				foundTech = true;
		}
		return foundTech;
	}

	public String parseFileNameForTechName(String fileName) {
		TechnologyInfo ti = parseFileNameForTechInfo(fileName);
		return ti.getName();
	}

	public String parseFileNameForTechVersion(String fileName) {
		TechnologyInfo ti = parseFileNameForTechInfo(fileName);
		return ti.getVersion();
	}

	/****************************************************************/
	/* Returns a TechnologyInfo with the name and version           */
	/* parsed from the fileName given                               */
	/****************************************************************/
	public TechnologyInfo parseFileNameForTechInfo(String fileName) {
		Enumeration enum;
		String techName, techVersion;
		int dash = 0;

		StringTokenizer lineTok = new StringTokenizer(fileName, "/");
		while (lineTok.hasMoreTokens()) {
			enum = techToDK.keys();
			techName = lineTok.nextToken();

			if (isGoodTechnology(techName)) {
				techVersion = lineTok.nextToken();
				return (new TechnologyInfo(techName, techVersion));
			}
		}
		return null;
	}

	public String Date_YYYYMMDD_2_MMDDYYYY(String date) {

		String newDate, month, day, year;

		year = date.substring(0, 4);
		month = date.substring(4, 6);
		day = date.substring(6, 8);

		newDate = month + "/" + day + "/" + year;

		return newDate;
	}

	public String checkDate(String date) {
		if (date.length() == 10)
			return date;

		String newDate, year;
		newDate = date.substring(0, 6);

		year = date.substring(6, 8);
		if (year.compareTo("50") <= 0)
			newDate = newDate + "20" + year;
		else
			newDate = newDate + "19" + year;

		return newDate;
	}

	public String getTechDKName(String techName) {
		return (String) techToDK.get(techName);
	}

	public String getTechPKName(String techName) {
		return (String) techToPK.get(techName);
	}

	public static void main(String[] args) {

		FlatFileParser ffp = new FlatFileParser(null);

		// TechInfo

		ffp.parseTechReleaseVersionInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/TechInfo/last.release.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/technology_version");

		ffp.parseASICCodenameInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/TechInfo/customer.list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/asic_codename");

		// GA CORES

		ffp.parseGeneralAvailableCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/ProductDefinition/shipping.cores.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e/ga_cores");

		ffp.parseGeneralAvailableCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/ProductDefinition/shipping.cores.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e/ga_cores");

		ffp.parseGeneralAvailableCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/ProductDefinition/shipping.cores.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27/ga_cores");

		ffp.parseGeneralAvailableCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/ProductDefinition/shipping.cores.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12/ga_cores");

		ffp.parseGeneralAvailableCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/ProductDefinition/shipping.cores.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11/ga_cores");

		// Restricted CORES

		ffp.parseRestrictedCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e/restricted_cores");

		ffp.parseRestrictedCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e/restricted_cores");

		ffp.parseRestrictedCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12/restricted_cores");

		ffp.parseRestrictedCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27/restricted_cores");

		ffp.parseRestrictedCoresInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11/restricted_cores");

		// Non-Standard Deliverables

		ffp.parseNonStandardDeliverablesInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e/nonstandard_deliverables");

		ffp.parseNonStandardDeliverablesInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e/nonstandard_deliverables");

		ffp.parseNonStandardDeliverablesInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12/nonstandard_deliverables");

		ffp.parseNonStandardDeliverablesInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27/nonstandard_deliverables");

		ffp.parseNonStandardDeliverablesInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/ProductDefinition/shipping.customers.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11/nonstandard_deliverables");

		// Delta Releases

		// SA12E

		ffp.parseDeltaPacketList(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/DeltaReleases/delta_packet_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e/delta_packets");

		ffp.parseOrderableComponentsInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/ProductDefinition/shipping.orderable.components",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e");

		ffp.parseDeltaReleaseInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12E/v12.0/DeltaReleases/delta_releases_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12e");

		// SA27E

		ffp.parseDeltaPacketList(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/DeltaReleases/delta_packet_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e/delta_packets");

		ffp.parseOrderableComponentsInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/ProductDefinition/shipping.orderable.components",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e");

		ffp.parseDeltaReleaseInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27E/v9.0/DeltaReleases/delta_releases_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27e");

		// SA27

		ffp.parseDeltaPacketList(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/DeltaReleases/delta_packet_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27/delta_packets");

		ffp.parseOrderableComponentsInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/ProductDefinition/shipping.orderable.components",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27");

		ffp.parseDeltaReleaseInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA27/v13.0/DeltaReleases/delta_releases_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa27");

		// SA12

		ffp.parseDeltaPacketList(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/DeltaReleases/delta_packet_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12/delta_packets");

		ffp.parseOrderableComponentsInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/ProductDefinition/shipping.orderable.components",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12");

		ffp.parseDeltaReleaseInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/SA12/v15.0/DeltaReleases/delta_releases_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/sa12");

		// CU11

		ffp.parseDeltaPacketList(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/DeltaReleases/delta_packet_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11/delta_packets");

		ffp.parseOrderableComponentsInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/ProductDefinition/shipping.orderable.components",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11");

		ffp.parseDeltaReleaseInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/ASICTECH/CU11/v4.0/DeltaReleases/delta_releases_list",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/cu11");

		// Die Size Estimator

		ffp.parseDieSizerInfo(
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/DieSizer/shipping.diesizer.data",
			"/afs/btv.ibm.com/data/edesign/edsd/testdata/DieSizer/platforms");

	}


}

/*****************************************************************/
/* This is just a small class created so a tech name & version   */
/* could be returned as a single arg from a function             */
/*****************************************************************/
class TechnologyInfo {
	String name, version;

	public TechnologyInfo(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}
