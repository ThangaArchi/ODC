package oem.edge.ets.fe;

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

/**
 * @author  Navneet Gupta (navneet@us.ibm.com)
 * @since   custcont.3.7.1
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import com.ibm.hrl.juru.Index;
import com.ibm.hrl.juru.StemmedLanguage;
import com.ibm.hrl.juru.StringDocumentProperty;
import com.ibm.hrl.juru.TextFileDocument;
import com.ibm.hrl.utils.HTMLParse.HTMLParse;

public class ETSSearchCreateIndex {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.44";
	public static final String LAST_UPDATE = "10/27/06 13:21:59";

	static final boolean DEBUG = false;
	static final boolean MAIL_INFO = false;

	static final int oneMinute = 60 * 1000;
	static final int sleepInterval = 15 * oneMinute;

	static final int logInterval = 60 * oneMinute;
	private static long lastLogTime;

	static final int HTML_PARSE_FACTOR = 5;
	static final int PDF_PARSE_FACTOR = 5;

	static final char SPACE = ' ';
	private static final char DELIM = '^';

	private static InternetAddress mailFrom;
	private static InternetAddress[] mailTo;

	private static Class thisClass;
	private static String longName;
	private static String shortName;
	private static String hostname;

	private static String mailHost;

	protected final File tempDir;

	private static String outLog, errLog;

	private String db2Url;
	private Properties db2Props;

	private final String indexDirectory;
	private final File indexPointer;
	private final File indexInfo;
	private final File lastUpdateFile;

	private String indexDirInUse, indexDirNotInUse;

	private String lastVersion = null;

	private final String[][] TABLES = { { "ETS.ETS_DOC", "DOC_UPDATE_DATE" }, {
			"ETS.ETS_DOCFILE", "DOCFILE_UPDATE_DATE" }, {
			"ETS.ETS_CALENDAR", "START_TIME" }, {
			"ETS.ETS_CAT" }, {
			"ETS.PROBLEM_INFO_USR1" }, {
			"ETS.PROBLEM_INFO_CQ1" }, {
			"ETS.PROBLEM_INFO_CQ2" }, {
			"ETS.PMO_ISSUE_INFO" }, {
			"ETS.PMO_ISSUE_DOC" }, {
			"ETS.ETS_QBR_MAIN" }, {
			"ETS.ETS_QBR_EXP" }, {
			"ETS.ETS_SELF_MAIN" }, {
			"ETS.ETS_SELF_EXP" }
	};

	private Timestamp[] lastTimestamp = new Timestamp[TABLES.length];
	private Timestamp[] newTimestamp = new Timestamp[TABLES.length];
	private int[] lastNumRecords = new int[TABLES.length];
	private int[] newNumRecords = new int[TABLES.length];

	private static StringBuffer errorBuffer = new StringBuffer(512);
	private static StringBuffer infoBuffer = new StringBuffer(512);

	private long unparsedHtmlLength, parsedHtmlLength;
	private long unparsedPdfLength, parsedPdfLength;
	private long sizeDocs, sizeFiles;
	private int numDocs, numFiles;

	private static final short MAX_COMMENTS_LENGTH = 128;

	private static final String EXTRACTION_DENIED_LONG =
		"You do not have permission to extract text";
	private static final String EXTRACTION_DENIED_SHORT =
		"Content extraction not allowed";

	public static void main(String[] args) {
		try {
			System.setProperty("log4j.defaultInitOverride", "true");
			BasicConfigurator.configure();
			Category.getRoot().setLevel(Level.WARN);

			new ETSSearchCreateIndex().run();
		} catch (Throwable t) {
			logError("Fatal error in ETSSearchCreateIndex", t);
		}
	}

	private ETSSearchCreateIndex() throws Exception {

		mailTo = InternetAddress.parse("navneet@us.ibm.com", false);
		mailHost = "us.ibm.com";

		thisClass = this.getClass();
		longName = thisClass.getName();
		shortName = longName.substring(longName.lastIndexOf('.') + 1);

		hostname = InetAddress.getLocalHost().getHostName();

		mailFrom = new InternetAddress("ets@us.ibm.com", "ETSDaemon");

		ResourceBundle gwaProps =
			ResourceBundle.getBundle("oem.edge.common.gwa");

		String edgelogDir = gwaProps.getString("gwa.edge_log_dir");

		if (edgelogDir == null) {
			throwInitException("Missing key: gwa.edge_log_dir in oem.edge.common.gwa.properties");
		} else {
			edgelogDir = edgelogDir.trim();
		}

		if (!edgelogDir.endsWith(File.separator)) {
			edgelogDir += File.separator;
		}

		indexDirectory = edgelogDir + "ets_search" + File.separator;

		String logDir = indexDirectory + "log" + File.separator;

		if (!new File(logDir).isDirectory()) {
			throwInitException(logDir + ": directory not found");
		}

		outLog = logDir + "java_" + shortName + ".out";
		errLog = logDir + "java_" + shortName + ".err";

		if (!printOut("Starting " + longName)) {
			throwInitException(outLog + ": unable to write to file");
		}

		String tempDirPath = indexDirectory + "tmp" + File.separator;
		tempDir = new File(tempDirPath);

		mailHost = gwaProps.getString("gwa.mailHost").trim();

		db2Url = gwaProps.getString("gwa.mail_connect_string").trim();
		String user = gwaProps.getString("gwa.db2usr").trim();
		String password = gwaProps.getString("gwa.db2pw").trim();

		db2Props = new Properties();
		db2Props.setProperty("user", user);
		db2Props.setProperty("password", password);
		db2Props.setProperty("fullyMaterializeLobData", "false");

		String driverClassName = "COM.ibm.db2.jdbc.app.DB2Driver";
		String flag = gwaProps.getString("gwa.driver");
		if (flag != null && flag.trim().equalsIgnoreCase("net")) {
			driverClassName = "COM.ibm.db2.jdbc.net.DB2Driver";
		}

		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			logError("ClassNotFound: " + driverClassName, e);
			Class.forName("com.ibm.db2.jcc.DB2Driver");
		}

		Connection conn = null;
		try {
			Driver driver = DriverManager.getDriver(db2Url);
			conn = makeConn();
			DatabaseMetaData md = conn.getMetaData();
			StringBuffer dbInfo = new StringBuffer();
			dbInfo.append("\nDatabase product name: ");
			dbInfo.append(md.getDatabaseProductName());
			dbInfo.append("\nDatabase product version: ");
			dbInfo.append(md.getDatabaseProductVersion());
			dbInfo.append("\nJDBC driver name: ");
			dbInfo.append(md.getDriverName());
			dbInfo.append("\nJDBC driver version: ");
			dbInfo.append(md.getDriverVersion());
			dbInfo.append("\nJDBC driver class: ");
			dbInfo.append(driver.getClass().getName());
			dbInfo.append("\n");
			printOut(dbInfo.toString());
		} catch (Exception e) {
			printErr("", e);
		} finally {
			close(conn);
		}

		String index1 = indexDirectory + "index1";
		String index2 = indexDirectory + "index2";

		indexPointer = new File(indexDirectory + "indexInUse");
		lastUpdateFile = new File(indexDirectory + "lastUpdate");
		indexInfo = new File(indexDirectory + "indexInfo");

		if (indexPointer.isFile()) {
			FileInputStream in = new FileInputStream(indexPointer);
			indexDirInUse = readInputStream(in).trim();
		}

		if (lastUpdateFile.isFile()
			&& (indexDirInUse.equals(index1) || indexDirInUse.equals(index2))) {

			FileInputStream in = new FileInputStream(lastUpdateFile);
			String lastUpdateStr = readInputStream(in).trim();
			parseLastUpdateFile(lastUpdateStr);
		}

		if (indexDirInUse == null
			|| (!indexDirInUse.equals(index1) && !indexDirInUse.equals(index2))) {
			indexDirInUse = index2;
		}

		if (indexDirInUse.equals(index1))
			indexDirNotInUse = index2;
		else
			indexDirNotInUse = index1;

		printOut("Initialized " + longName);

	}

	private void throwInitException(String message) {
		printErr(message);
		throw new RuntimeException(message);
	}

	private void run() throws Exception {
		while (true) {
			if (haveTablesChanged()) {
				createIndex();
				lastLogTime = System.currentTimeMillis();
			} else {
				long currentTime = System.currentTimeMillis();
				if ((currentTime - lastLogTime) > logInterval) {
					printOut("No new updates");
					lastLogTime = currentTime;
				}
			}

			Thread.sleep(sleepInterval);
		}
	}

	private void updateExtractedFiles() throws SQLException {

		printOut("Starting updateExtractedFiles()");

		Connection conn1 = null;
		Connection conn2 = null;

		Statement stmt = null;
		PreparedStatement query = null;
		PreparedStatement update = null;

		try {

			conn1 = makeConn();

			stmt = conn1.createStatement();

			ArrayList newFiles = new ArrayList();

			String str =
				"select DOC_ID, DOCFILE_ID, DOCFILE_NAME, LENGTH(DOCFILE) as FILE_LEN, DOCFILE_UPDATE_DATE"
					+ " from ETS.ETS_DOCFILE"
					+ " where LOWER(DOCFILE_NAME) like '%.pdf'"
					+ " and DOCFILE is not null"
					+ " and(DOC_ID, DOCFILE_ID, DOCFILE_UPDATE_DATE) not in"
					+ " (select DOC_ID, DOCFILE_ID, DOCFILE_UPDATE_DATE from ETS.EXTRACTED_FILES)"
					+ " with ur";

			ResultSet rs = stmt.executeQuery(str);

			while (rs.next()) {
				ETSFileInfo file = new ETSFileInfo();
				file.docId = rs.getInt("DOC_ID");
				file.docfileId = rs.getInt("DOCFILE_ID");
				file.fileName = rs.getString("DOCFILE_NAME");
				file.fileSize = rs.getInt("FILE_LEN");
				file.timestamp = rs.getTimestamp("DOCFILE_UPDATE_DATE");
				newFiles.add(file);
			}

			close(rs);

			if (newFiles.isEmpty()) {
				printOut("No new or updated pdf files");
				return;
			}

			str =
				"delete from ETS.EXTRACTED_FILES where"
					+ " (DOC_ID, DOCFILE_ID, DOCFILE_UPDATE_DATE) not in "
					+ " (select DOC_ID, DOCFILE_ID, DOCFILE_UPDATE_DATE from ETS.ETS_DOCFILE)";

			int deleteCount = stmt.executeUpdate(str);

			printOut("Deleted extracted content of " + deleteCount + " files");

			close(stmt);
			stmt = null;

			printOut(
				"Starting to extract content from "
					+ newFiles.size()
					+ " files");

			conn2 = makeConn();

			query =
				conn1.prepareStatement(
					"select DOCFILE from ETS.ETS_DOCFILE where DOC_ID = ? and DOCFILE_ID = ? with ur");

			update =
				conn2.prepareStatement(
					"insert into ETS.EXTRACTED_FILES"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			// DOC_ID, DOCFILE_ID, DOCFILE_NAME, CONTENT, DOCFILE_SIZE, EXTFILE_SIZE, COMMENTS, DOCFILE_UPDATE_DATE

			Iterator iter = newFiles.iterator();
			int insertCount = 0;

			while (iter.hasNext()) {
				ETSFileInfo file = (ETSFileInfo) iter.next();

				printOut(
					"Extracting contents of: "
						+ file.docId
						+ ":"
						+ file.docfileId
						+ ":"
						+ file.fileSize);

				query.setInt(1, file.docId);
				query.setInt(2, file.docfileId);

				rs = query.executeQuery();

				if (rs.next()) {
					StringBuffer comments = new StringBuffer();
					byte[] text =
						extractPdfText(
							rs,
							comments,
							file.docId + ":" + file.docfileId,
							file.fileName);

					update.setInt(1, file.docId);
					update.setInt(2, file.docfileId);
					update.setString(3, file.fileName);
					update.setBytes(4, text);
					update.setInt(5, file.fileSize);
					update.setInt(6, text.length);
					update.setString(7, comments.toString());
					update.setTimestamp(8, file.timestamp);

					int rowCount = update.executeUpdate();

					if (rowCount == 1) {
						insertCount++;
					} else {
						addError(
							"insert into ETS.EXTRACTED_FILES returned: "
								+ rowCount
								+ ". docid:"
								+ file.docId
								+ " docfileid:"
								+ file.docfileId
								+ " name: "
								+ file.fileName);
					}
				} else {
					addError(
						"No result for select FILE query. docid:"
							+ file.docId
							+ " docfileid:"
							+ file.docfileId
							+ " name: "
							+ file.fileName);
				}

			}

			printOut("Extracted content from " + insertCount + " files");

		} finally {
			close(stmt);
			close(query);
			close(update);
			close(conn1);
			close(conn2);
		}

	}

	private byte[] extractPdfText(
		ResultSet rs,
		StringBuffer comments,
		String id,
		String name) {

		PDFParser parser = null;

		try {

			InputStream in = rs.getBinaryStream(1);
			parser = new PDFParser(in);
			parser.setTempDirectory(tempDir);

			parser.parse();

			in.close();
			close(rs);

			PDDocument document = parser.getPDDocument();

			return new PDFTextStripper().getText(document).getBytes();

		} catch (Exception e) {
			comments.append(getComments(e, id, name));
			return new byte[0];
		} finally {
			if (parser != null) {
				try {
					parser.getDocument().close();
				} catch (Throwable t) {
				}
			}
		}

	}

	private String getComments(Exception e, String id, String name) {

		if (EXTRACTION_DENIED_LONG.equals(e.getMessage())) {
			addInfo(
				EXTRACTION_DENIED_SHORT + " for id:" + id + " name: " + name);

			return EXTRACTION_DENIED_SHORT;
		} else {
			addError("Exception parsing file. id:" + id + " name: " + name, e);

			String comments = e.toString();

			if (comments.length() > MAX_COMMENTS_LENGTH) {
				return comments.substring(0, MAX_COMMENTS_LENGTH);
			} else {
				return comments;
			}
		}

	}

	private void createIndex() throws Exception {

		printOut("Starting createIndex()");

		long startTime = System.currentTimeMillis();

		cleanupTempDir();

		updateExtractedFiles();

		resetVariables();

		StringBuffer indexInfoBuffer = new StringBuffer();

		Index indexObj = getNewIndex();

		try {
			printOut("Indexing documents");
			indexDocuments(indexObj, indexInfoBuffer);

			printOut("Indexing files");
			indexFiles(indexObj, indexInfoBuffer);

			printOut("Indexing categories");
			indexCategories(indexObj, indexInfoBuffer);

			printOut("Indexing issues and feedback");
			indexIssuesAndFeedback(indexObj, indexInfoBuffer);

			printOut("Indexing change requests");
			indexChangeRequests(indexObj, indexInfoBuffer);

			printOut("Indexing set-met reviews");
			indexSetMetReviews(indexObj, indexInfoBuffer);

			printOut("Indexing self assessment reviews");
			indexSelfAssessmentReviews(indexObj, indexInfoBuffer);

			printOut("Indexing calendar");
			indexCalendar(indexObj, indexInfoBuffer);
		} catch (Exception e) {
			printOut(indexInfoBuffer.toString());
			throw e;
		}

		wrapup(indexObj, indexInfoBuffer.toString());

		long endTime = System.currentTimeMillis();

		StringBuffer statusBuffer = new StringBuffer();

		statusBuffer.append("numDocs: ");
		statusBuffer.append(numDocs);
		statusBuffer.append("\n");
		statusBuffer.append("numFiles: ");
		statusBuffer.append(numFiles);
		statusBuffer.append("\n");
		statusBuffer.append("sizeDocs: ");
		statusBuffer.append(sizeDocs);
		statusBuffer.append("\n");
		statusBuffer.append("sizeFiles: ");
		statusBuffer.append(sizeFiles);
		statusBuffer.append("\n\n");

		statusBuffer.append("parsedPdfLength: ");
		statusBuffer.append(parsedPdfLength);
		statusBuffer.append("\n");
		statusBuffer.append("parsedHtmlLength: ");
		statusBuffer.append(parsedHtmlLength);
		statusBuffer.append("\n\n");

		statusBuffer.append("Created index in ");
		statusBuffer.append((endTime - startTime) / 60000);
		statusBuffer.append(" minutes.\n\n");

		addInfo(statusBuffer);

		logError();
		logInfo();
	}

	private void indexDocuments(Index indexObj, StringBuffer indexInfoBuffer)
		throws Exception {

		String type = null;

		Connection conn1 = null;
		Connection conn2 = null;

		Statement stmt = null;
		PreparedStatement ancestorsStmt = null;
		PreparedStatement userNamePstmt = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			String query =
				"select DOC_ID, PROJECT_ID, CAT_ID, USER_ID, DOC_NAME,"
					+ " DOC_DESCRIPTION, DOC_KEYWORDS, DOC_UPDATE_DATE, DOC_TYPE"
					+ " from ETS.ETS_DOC"
					+ " where DELETE_FLAG != '"
					+ Defines.TRUE_FLAG
					+ "' with ur";

			ResultSet rs = stmt.executeQuery(query);

			ArrayList docs = new ArrayList();

			while (rs.next()) {

				ETSFileInfo doc = new ETSFileInfo();

				try {
					doc.docId = rs.getInt("DOC_ID");
					doc.projectId = rs.getString("PROJECT_ID");
					doc.parentId = rs.getInt("CAT_ID");
					doc.userId = rs.getString("USER_ID");
					doc.title = rs.getString("DOC_NAME");
					doc.description = rs.getString("DOC_DESCRIPTION");
					doc.keywords = rs.getString("DOC_KEYWORDS");
					doc.timestamp = rs.getTimestamp("DOC_UPDATE_DATE");
					doc.docType = rs.getInt("DOC_TYPE");

					docs.add(doc);
				} catch (Exception e) {
					logError(
						"ERROR getting doc properties ID: "
							+ doc.docId,
						e);
					throw e;
				}
			}
			close(rs);
			rs = null;
			close(stmt);
			stmt = null;

			if (docs.isEmpty()) {
				printOut("No documents to index");
				return;
			}

			conn2 = makeConn();

			ancestorsStmt =
				conn1.prepareStatement(
					"select PARENT_ID from ETS.ETS_CAT where CAT_ID = ? with ur");

			userNamePstmt = getUserNamePstmt(conn2);
			TreeMap userNameCache = new TreeMap();

			Iterator iter = docs.iterator();

			while (iter.hasNext()) {

				ETSFileInfo doc = (ETSFileInfo) iter.next();

				if (doc.docType == Defines.DOC) {
					type = ETSSearchResult.TYPE_DOC_STR;
				} else if (doc.docType == Defines.PROJECT_PLAN) {
					// type = ETSSearchResult.TYPE_PROJECT_PLAN_STR;
					// project plan always has exactly one file and no metadata
					// so we index it in indexFiles() instead of here
					continue;
				} else {
					continue;
				}

				String id = type + "-" + doc.docId;

				String[] ancestors = new String[0];
				StringBuffer metadata = new StringBuffer(1024);

				try {
					append(metadata, repeatString(doc.title, 10));
					append(metadata, doc.description);
					append(metadata, repeatString(doc.keywords, 3));

					append(metadata, doc.userId);
					append(
						metadata,
						getUserName(
							doc.userId,
							true,
							userNameCache,
							userNamePstmt));

					if (doc.docType == Defines.DOC) {
						ancestors =
							getAncestors(doc.parentId, ancestorsStmt, type);
					} else {
						printErr(
							"Unexpected DOC_TYPE: "
								+ doc.docType
								+ " for docID: "
								+ doc.docId);
					}

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty(
						"ID",
						new StringDocumentProperty(String.valueOf(doc.docId)));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(doc.projectId));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(doc.title));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(doc.timestamp)));

					tfd.addProperty(
						"PARENT_ID",
						new StringDocumentProperty(
							String.valueOf(doc.parentId)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { doc.projectId });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(doc.title);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(doc.projectId);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');

					numDocs++;
					sizeDocs += metadata.length();
				}

			}

		} finally {
			close(stmt);
			close(ancestorsStmt);
			close(userNamePstmt);
			close(conn1);
			close(conn2);
		}
	}

	private void indexFiles(Index indexObj, StringBuffer indexInfoBuffer)
	throws Exception {
		
		String type = null;
		
		Connection conn1 = null;
		Connection conn2 = null;
		Connection conn3 = null;
		
		Statement stmt = null;
		PreparedStatement ancestorsStmt = null;
		PreparedStatement filesStmt = null;
		PreparedStatement pdfFilesStmt = null;
		
		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();
			
			String query =
				"select d.DOC_ID, d.PROJECT_ID, d.CAT_ID, d.USER_ID, d.DOC_NAME,"
				+ " d.DOC_DESCRIPTION, d.DOC_KEYWORDS, d.DOC_TYPE,"
				+ " f.DOCFILE_ID, f.DOCFILE_NAME, LENGTH(f.DOCFILE) as FILE_LEN, f.DOCFILE_UPDATE_DATE"
				+ " from ETS.ETS_DOC d, ETS.ETS_DOCFILE f"
				+ " where d.DOC_ID = f.DOC_ID"
				+ " and d.DELETE_FLAG != '"
				+ Defines.TRUE_FLAG
				+ "' with ur";
			
			ResultSet rs = stmt.executeQuery(query);
			
			ArrayList files = new ArrayList();
			
			while (rs.next()) {
				
				ETSFileInfo file = new ETSFileInfo();
				
				try {
					file.docId = rs.getInt("DOC_ID");
					file.projectId = rs.getString("PROJECT_ID");
					file.parentId = rs.getInt("CAT_ID");
					file.userId = rs.getString("USER_ID");
					file.title = rs.getString("DOC_NAME");
					file.description = rs.getString("DOC_DESCRIPTION");
					file.keywords = rs.getString("DOC_KEYWORDS");
					file.docType = rs.getInt("DOC_TYPE");
					file.docfileId = rs.getInt("DOCFILE_ID");
					file.fileName = rs.getString("DOCFILE_NAME");
					file.fileSize = rs.getInt("FILE_LEN");
					file.timestamp = rs.getTimestamp("DOCFILE_UPDATE_DATE");
					
					files.add(file);
				} catch (Exception e) {
					logError(
							"ERROR getting file properties ID: "
							+ file.docId
							+ ":"
							+ file.docfileId,
							e);
					throw e;
				}
			}
			close(rs);
			rs = null;
			close(stmt);
			stmt = null;
			
			if (files.isEmpty()) {
				printOut("No files to index");
				return;
			}
			
			conn2 = makeConn();
			conn3 = makeConn();
			
			ancestorsStmt =
				conn1.prepareStatement(
				"select PARENT_ID from ETS.ETS_CAT where CAT_ID = ? with ur");
			
			filesStmt =
				conn2.prepareStatement(
				"select DOCFILE as DATA from ETS.ETS_DOCFILE where DOC_ID = ? and DOCFILE_ID = ? with ur");
			
			pdfFilesStmt =
				conn3.prepareStatement(
				"select CONTENT as DATA from ETS.EXTRACTED_FILES where DOC_ID = ? and DOCFILE_ID = ? with ur");
			
			Iterator iter = files.iterator();
			
			while (iter.hasNext()) {
				
				ETSFileInfo file = (ETSFileInfo) iter.next();
				
				if (file.docType == Defines.DOC) {
					type = ETSSearchResult.TYPE_DOC_STR;
				} else if (file.docType == Defines.PROJECT_PLAN) {
					type = ETSSearchResult.TYPE_PROJECT_PLAN_STR;
				} else {
					continue;
				}
				
				String id = type + "-" + file.docId + ":" + file.docfileId;
				
				String[] ancestors = new String[0];
				int contentLength = 0;
				
				try {
					StringBuffer content = new StringBuffer(1024);
					
					append(content, repeatString(file.fileName, 10));
					append(content, getFilenameWithoutExt(file.fileName));
					
					if (file.docType == Defines.DOC) {
						ancestors =
							getAncestors(file.parentId, ancestorsStmt, type);
					} else if (file.docType == Defines.PROJECT_PLAN) {
						append(content, " project plan ");
						append(content, file.userId);
						append(content, repeatString(file.title, 10));
						append(content, file.description);
						append(content, repeatString(file.keywords, 3));

						ancestors =
							new String[] { type, ETSSearchResult.TYPE_DOC_STR };
					} else {
						printErr(
								"Unexpected DOC_TYPE: "
								+ file.docType
								+ " for docID: "
								+ file.docId
								+ " docfileID: "
								+ file.docfileId);
					}
					
					contentLength =
						getFileContents(
								file.docId,
								file.docfileId,
								file.fileName,
								content,
								filesStmt,
								pdfFilesStmt);
					
					TextFileDocument tfd = new TextFileDocument(id);
					
					tfd.addProperty(
							"ID",
							new StringDocumentProperty(String.valueOf(file.docId)));
					
					tfd.addProperty(
							"DOCFILE_ID",
							new StringDocumentProperty(
									String.valueOf(file.docfileId)));
					
					tfd.addProperty("TYPE", new StringDocumentProperty(type));
					
					tfd.addProperty(
							"PROJECT_ID",
							new StringDocumentProperty(file.projectId));
					
					tfd.addProperty(
							"NAME",
							new StringDocumentProperty(file.title));
					
					tfd.addProperty(
							"DATE",
							new StringDocumentProperty(formatDate(file.timestamp)));
					
					tfd.addProperty(
							"FILE_NAME",
							new StringDocumentProperty(file.fileName));
					
					tfd.addProperty(
							"FILE_SIZE",
							new StringDocumentProperty(
									formatFileSize(file.fileSize)));
					
					tfd.addProperty(
							"PARENT_ID",
							new StringDocumentProperty(
									String.valueOf(file.parentId)));
					
					indexObj.addDocument(
							tfd,
							content.toString(),
							ancestors,
							new String[] { file.projectId });
					
				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(file.fileName);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(file.projectId);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(contentLength);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
					
					numFiles++;
					sizeFiles += contentLength;
				}
				
			}
			
		} finally {
			close(stmt);
			close(ancestorsStmt);
			close(filesStmt);
			close(pdfFilesStmt);
			close(conn1);
			close(conn2);
			close(conn3);
		}
	}

	private void indexCategories(Index indexObj, StringBuffer indexInfoBuffer)
		throws Exception {

		String type = ETSSearchResult.TYPE_CAT_STR;

		Connection conn1 = null;
		Connection conn2 = null;
		Connection conn3 = null;
		Statement stmt = null;
		PreparedStatement ancestorsStmt = null;
		PreparedStatement userNamePstmt = null;
		ResultSet rs = null;

		try {
			conn1 = makeConn();
			ancestorsStmt =
				conn1.prepareStatement(
					"select PARENT_ID from ETS.ETS_CAT where CAT_ID = ? with ur");

			conn2 = makeConn();
			stmt = conn2.createStatement();

			conn3 = makeConn();
			userNamePstmt = getUserNamePstmt(conn3);
			TreeMap userNameCache = new TreeMap();

			String query =
				"select CAT_ID, PROJECT_ID, USER_ID, CAT_NAME, PARENT_ID, LAST_TIMESTAMP"
					+ " from ETS.ETS_CAT"
					+ " where CAT_TYPE > 0"
					+ " with ur";

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				int catId = rs.getInt("CAT_ID");
				String projectId = rs.getString("PROJECT_ID");
				String userId = rs.getString("USER_ID");
				String catName = rs.getString("CAT_NAME");
				int parentId = rs.getInt("PARENT_ID");
				Timestamp lastTimestamp = rs.getTimestamp("LAST_TIMESTAMP");

				if (catName == null || catName.trim().length() == 0) {
					continue;
				}

				String id = type + "-" + catId;

				String[] ancestors = new String[0];
				StringBuffer metadata = new StringBuffer();

				try {
					append(metadata, catName);

					append(metadata, userId);
					append(
						metadata,
						getUserName(
							userId,
							true,
							userNameCache,
							userNamePstmt));

					ancestors =
						getAncestors(
							parentId,
							ancestorsStmt,
							ETSSearchResult.TYPE_DOC_STR);

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty(
						"ID",
						new StringDocumentProperty(String.valueOf(catId)));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(projectId));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(catName));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(lastTimestamp)));

					tfd.addProperty(
						"PARENT_ID",
						new StringDocumentProperty(String.valueOf(parentId)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { projectId });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(catName);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(projectId);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
		} finally {
			close(rs);
			close(stmt);
			close(ancestorsStmt);
			close(userNamePstmt);
			close(conn1);
			close(conn2);
			close(conn3);
		}
	}

	private void indexIssuesAndFeedback(
		Index indexObj,
		StringBuffer indexInfoBuffer)
		throws Exception {

		String type = ETSSearchResult.TYPE_ISSUE_STR;

		Connection conn1 = null;
		Connection conn2 = null;
		Connection conn3 = null;
		Connection conn4 = null;
		Connection conn5 = null;
		Connection conn6 = null;
		Statement stmt = null;
		PreparedStatement fileListStmt = null;
		PreparedStatement filesStmt = null;
		PreparedStatement pdfFilesStmt = null;
		PreparedStatement cq2Pstmt = null;
		PreparedStatement userNamePstmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			conn2 = makeConn();
			cq2Pstmt =
				conn2.prepareStatement(
					"select COMM_LOG"
						+ " from ETS.PROBLEM_INFO_CQ2"
						+ " where EDGE_PROBLEM_ID = ?"
						+ " with ur");

			conn3 = makeConn();
			fileListStmt =
				conn3.prepareStatement(
					"select f.DOC_ID, f.DOCFILE_ID, f.DOCFILE_NAME, f.FILE_DESCRIPTION" +
					" from ETS.ETS_DOC d, ETS.ETS_DOCFILE f" +
					" where d.ISSUE_ID = ?" +
					" and d.DOC_ID = f.DOC_ID" +
					" and f.FILE_STATUS != 'T'" + // FILE_STATUS 'T' = temporary file
					" and d.DELETE_FLAG != '" +
					Defines.TRUE_FLAG +
					"' with ur");

			conn4 = makeConn();
			filesStmt =
				conn4.prepareStatement(
					"select DOCFILE as DATA from ETS.ETS_DOCFILE where DOC_ID = ? and DOCFILE_ID = ? with ur");

			conn5 = makeConn();
			pdfFilesStmt =
				conn5.prepareStatement(
					"select CONTENT as DATA from ETS.EXTRACTED_FILES where DOC_ID = ? and DOCFILE_ID = ? with ur");
			
			String query =
				"select cq1.EDGE_PROBLEM_ID, cq1.CQ_TRK_ID, cq1.PROBLEM_STATE,"
					+ " u1.PROBLEM_CREATOR, u1.CUST_NAME, u1.CUST_EMAIL, u1.CUST_COMPANY, u1.CUST_PROJECT,"
					+ " u1.PROBLEM_CLASS, u1.TITLE, u1.SEVERITY,"
					+ " u1.PROBLEM_TYPE, u1.SUBTYPE_A, u1.SUBTYPE_B, u1.SUBTYPE_C, u1.SUBTYPE_D, u1.PROBLEM_DESC,"
					+ " u1.LAST_TIMESTAMP, u1.TEST_CASE, u1.ETS_PROJECT_ID"
					+ " from ETS.PROBLEM_INFO_CQ1 cq1, ETS.PROBLEM_INFO_USR1 u1"
					+ " where cq1.EDGE_PROBLEM_ID = u1.EDGE_PROBLEM_ID"
					+ " with ur";

			conn6 = makeConn();
			userNamePstmt = getUserNamePstmt(conn6);
			TreeMap userNameCache = new TreeMap();

			HashSet idSet = new HashSet();

			rs = stmt.executeQuery(query);

			while (rs.next()) {

				String EDGE_PROBLEM_ID = rs.getString("EDGE_PROBLEM_ID").trim();
				String CQ_TRK_ID = rs.getString("CQ_TRK_ID");

				String id = type + "-" + EDGE_PROBLEM_ID;

				if (!idSet.add(EDGE_PROBLEM_ID)) {
					printOut(
						"Discarding CQ_TRK_ID: "
							+ CQ_TRK_ID
							+ " due to duplicate EDGE_PROBLEM_ID: "
							+ EDGE_PROBLEM_ID);
					continue;
				}

				String ETS_PROJECT_ID = null;
				StringBuffer metadata = new StringBuffer(1024);
				String[] ancestors = new String[] { type };
				
				int contentLength = 0;

				try {
					String PROBLEM_STATE = rs.getString("PROBLEM_STATE");
					String PROBLEM_CREATOR = rs.getString("PROBLEM_CREATOR");
					String CUST_NAME = rs.getString("CUST_NAME");
					String CUST_EMAIL = rs.getString("CUST_EMAIL");
					String CUST_COMPANY = rs.getString("CUST_COMPANY");
					String CUST_PROJECT = rs.getString("CUST_PROJECT");
					String PROBLEM_CLASS = rs.getString("PROBLEM_CLASS");
					String TITLE = rs.getString("TITLE");
					String SEVERITY = rs.getString("SEVERITY");
					String PROBLEM_TYPE = rs.getString("PROBLEM_TYPE");
					String SUBTYPE_A = rs.getString("SUBTYPE_A");
					String SUBTYPE_B = rs.getString("SUBTYPE_B");
					String SUBTYPE_C = rs.getString("SUBTYPE_C");
					String SUBTYPE_D = rs.getString("SUBTYPE_D");
					String PROBLEM_DESC = rs.getString("PROBLEM_DESC");
					Timestamp LAST_TIMESTAMP =
						rs.getTimestamp("LAST_TIMESTAMP");
					String TEST_CASE = rs.getString("TEST_CASE");
					ETS_PROJECT_ID = rs.getString("ETS_PROJECT_ID");

					if (PROBLEM_CLASS == null) {
						printOut(
							"Discarding EDGE_PROBLEM_ID: "
								+ EDGE_PROBLEM_ID
								+ " due to unsupported PROBLEM_CLASS: "
								+ PROBLEM_CLASS);
						continue;
					} else {
						PROBLEM_CLASS = PROBLEM_CLASS.trim();
						if (PROBLEM_CLASS.equalsIgnoreCase("Defect")) {
							append(metadata, "issue");
						} else if (
							PROBLEM_CLASS.equalsIgnoreCase("Feedback")) {
							append(metadata, "feedback");
						} else if (
							PROBLEM_CLASS.equalsIgnoreCase("Change")
								|| PROBLEM_CLASS.equalsIgnoreCase("Question")) {
							continue;
						} else {
							printOut(
								"Discarding EDGE_PROBLEM_ID: "
									+ EDGE_PROBLEM_ID
									+ " due to unsupported PROBLEM_CLASS: "
									+ PROBLEM_CLASS);
							continue;
						}
					}

					append(metadata, CQ_TRK_ID);
					append(metadata, TITLE);
					append(metadata, PROBLEM_DESC);
					append(metadata, PROBLEM_TYPE);
					append(metadata, SUBTYPE_A);
					append(metadata, SUBTYPE_B);
					append(metadata, SUBTYPE_C);
					append(metadata, SUBTYPE_D);
					append(metadata, PROBLEM_CLASS);
					append(metadata, PROBLEM_CREATOR);
					append(metadata, CUST_NAME);
					append(metadata, CUST_EMAIL);
					append(metadata, CUST_COMPANY);
					append(metadata, CUST_PROJECT);
					append(metadata, PROBLEM_STATE);
					append(metadata, SEVERITY);
					append(metadata, TEST_CASE);

					append(metadata, PROBLEM_CREATOR);
					append(
						metadata,
						getUserName(
							PROBLEM_CREATOR,
							false,
							userNameCache,
							userNamePstmt));

					cq2Pstmt.setString(1, EDGE_PROBLEM_ID);
					rs2 = cq2Pstmt.executeQuery();
					while (rs2.next()) {
						String COMM_LOG = rs2.getString("COMM_LOG");
						append(metadata, COMM_LOG);
					}
					close(rs2);
					rs2 = null;

					fileListStmt.setString(1, EDGE_PROBLEM_ID);
					rs2 = fileListStmt.executeQuery();
					while (rs2.next()) {
						int DOC_ID = rs2.getInt("DOC_ID");
						int DOCFILE_ID = rs2.getInt("DOCFILE_ID");
						String DOCFILE_NAME = rs2.getString("DOCFILE_NAME");
						String FILE_DESCRIPTION = rs2.getString("FILE_DESCRIPTION");

						append(metadata, DOCFILE_NAME);
						append(metadata, getFilenameWithoutExt(DOCFILE_NAME));
						append(metadata, FILE_DESCRIPTION);
						
						contentLength
						+= getFileContents(
								DOC_ID,
								DOCFILE_ID,
								DOCFILE_NAME,
								metadata,
								filesStmt,
								pdfFilesStmt);
					}
					close(rs2);
					rs2 = null;

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty(
						"ID",
						new StringDocumentProperty(EDGE_PROBLEM_ID));

					tfd.addProperty(
						"CQ_TRK_ID",
						new StringDocumentProperty(CQ_TRK_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(ETS_PROJECT_ID));

					tfd.addProperty("NAME", new StringDocumentProperty(TITLE));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(LAST_TIMESTAMP)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { ETS_PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(CQ_TRK_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(ETS_PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(contentLength);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
		} finally {
			close(rs);
			close(rs2);
			close(stmt);
			close(fileListStmt);
			close(filesStmt);
			close(pdfFilesStmt);
			close(cq2Pstmt);
			close(userNamePstmt);
			close(conn1);
			close(conn2);
			close(conn3);
			close(conn4);
			close(conn5);
			close(conn6);
		}
	}

	private void indexChangeRequests(
		Index indexObj,
		StringBuffer indexInfoBuffer)
		throws Exception {

		String type = ETSSearchResult.TYPE_CHANGE_REQ_STR;

		Connection conn1 = null;
		Connection conn2 = null;
		Connection conn3 = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement userNamePstmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			conn2 = makeConn();
			pstmt =
				conn2.prepareStatement(
					"select DOC_NAME, DOC_MIME, DOC_DESC, DOC_BLOB"
						+ " from ETS.PMO_ISSUE_DOC"
						+ " where ETS_ID = ?"
						+ " with ur");

			String query =
				"select proj.PROJECT_ID, pmo.ETS_ID, SUBMITTER_NAME, SUBMITTER_COMPANY, SUBMITTER_EMAIL,"
					+ " STATE_ACTION, SUBMITTER_IR_ID, pmo.CLASS, pmo.TITLE, SEVERITY, pmo.TYPE, DESCRIPTION,"
					+ " COMM_FROM_CUST, OWNER_IR_ID, OWNER_NAME, pmo.LAST_TIMESTAMP, PROBLEM_STATE"
					+ " from ETS.ETS_PROJECTS proj, ETS.PMO_ISSUE_INFO pmo"
					+ " where length(pmo.PMO_PROJECT_ID) > 0"
					+ " and proj.PROJECT_STATUS != '"
					+ Defines.WORKSPACE_DELETE
					+ "' and pmo.PMO_PROJECT_ID = proj.PMO_PROJECT_ID"
					+ " with ur";

			conn3 = makeConn();
			userNamePstmt = getUserNamePstmt(conn3);
			TreeMap userNameCache = new TreeMap();

			rs = stmt.executeQuery(query);

			while (rs.next()) {
				String PROJECT_ID = rs.getString("PROJECT_ID").trim();
				String ETS_ID = rs.getString("ETS_ID").trim();

				String id = type + "-" + ETS_ID;

				String TITLE = null;
				StringBuffer metadata = new StringBuffer(1024);
				String[] ancestors =
					new String[] { type, ETSSearchResult.TYPE_ISSUE_STR };

				try {
					String SUBMITTER_NAME = rs.getString("SUBMITTER_NAME");
					String SUBMITTER_COMPANY =
						rs.getString("SUBMITTER_COMPANY");
					String SUBMITTER_EMAIL = rs.getString("SUBMITTER_EMAIL");
					String STATE_ACTION = rs.getString("STATE_ACTION");
					String SUBMITTER_IR_ID = rs.getString("SUBMITTER_IR_ID");
					String CLASS = rs.getString("CLASS");
					TITLE = rs.getString("TITLE");
					String SEVERITY = rs.getString("SEVERITY");
					String TYPE = rs.getString("TYPE");
					String DESCRIPTION = rs.getString("DESCRIPTION");
					String COMM_FROM_CUST = rs.getString("COMM_FROM_CUST");
					String OWNER_IR_ID = rs.getString("OWNER_IR_ID");
					String OWNER_NAME = rs.getString("OWNER_NAME");
					Timestamp LAST_TIMESTAMP =
						rs.getTimestamp("LAST_TIMESTAMP");
					String PROBLEM_STATE = rs.getString("PROBLEM_STATE");

					append(metadata, "change request");
					append(metadata, TITLE);
					append(metadata, DESCRIPTION);
					append(metadata, COMM_FROM_CUST);
					append(metadata, CLASS);
					append(metadata, TYPE);
					append(metadata, PROBLEM_STATE);
					append(metadata, STATE_ACTION);
					append(metadata, SEVERITY);
					append(metadata, SUBMITTER_IR_ID);
					append(metadata, SUBMITTER_NAME);
					append(metadata, SUBMITTER_COMPANY);
					append(metadata, SUBMITTER_EMAIL);
					append(metadata, OWNER_IR_ID);
					append(metadata, OWNER_NAME);

					append(metadata, SUBMITTER_IR_ID);
					append(
						metadata,
						getUserName(
							SUBMITTER_IR_ID,
							false,
							userNameCache,
							userNamePstmt));

					pstmt.setString(1, ETS_ID);
					rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						String DOC_NAME = rs2.getString("DOC_NAME");
						String DOC_MIME = rs2.getString("DOC_MIME");
						String DOC_DESC = rs2.getString("DOC_DESC");

						append(metadata, DOC_NAME);
						append(metadata, DOC_DESC);

						if (DOC_MIME != null) {
							if (DOC_MIME.equals("text/html")) {
								getHtmlFileContents(
									rs2,
									"DOC_BLOB",
									metadata,
									ETS_ID,
									DOC_NAME);
							} else if (DOC_MIME.equals("text/plain")) {
								getPlainTextFileContents(
									rs2,
									"DOC_BLOB",
									metadata,
									ETS_ID,
									DOC_NAME);
							}
						}
					}
					close(rs2);
					rs2 = null;

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty("ID", new StringDocumentProperty(ETS_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(PROJECT_ID));

					tfd.addProperty("NAME", new StringDocumentProperty(TITLE));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(LAST_TIMESTAMP)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(TITLE);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
		} finally {
			close(rs);
			close(rs2);
			close(stmt);
			close(pstmt);
			close(userNamePstmt);
			close(conn1);
			close(conn2);
			close(conn3);
		}
	}

	private void indexSetMetReviews(
		Index indexObj,
		StringBuffer indexInfoBuffer)
		throws Exception {

		String type = ETSSearchResult.TYPE_SETMET_STR;

		Connection conn1 = null;
		Connection conn2 = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			String query =
				"select EXPECT_DESCRIPTION, EXP_ACTION, COMMENTS"
					+ " from ETS.ETS_QBR_EXP"
					+ " where QBR_ID = ? and PROJECT_ID = ?"
					+ " with ur";
			conn2 = makeConn();
			pstmt = conn2.prepareStatement(query);

			query =
				"select QBR_ID, PROJECT_ID, QBR_NAME,"
					+ " CLIENT_IR_ID, ETS_PRACTICE, ETS_BSE, STATE, INTERVIEW_BY, LAST_TIMESTAMP"
					+ " from ETS.ETS_QBR_MAIN"
					+ " with ur";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String QBR_ID = rs.getString("QBR_ID").trim();
				String PROJECT_ID = rs.getString("PROJECT_ID").trim();
				String QBR_NAME = rs.getString("QBR_NAME");

				String id = type + "-" + QBR_ID + "-" + PROJECT_ID;

				StringBuffer metadata = new StringBuffer();
				String[] ancestors = new String[] { type };

				try {
					String CLIENT_IR_ID = rs.getString("CLIENT_IR_ID");
					String ETS_PRACTICE = rs.getString("ETS_PRACTICE");
					String ETS_BSE = rs.getString("ETS_BSE");
					String STATE = rs.getString("STATE");
					String INTERVIEW_BY = rs.getString("INTERVIEW_BY");
					Timestamp LAST_TIMESTAMP =
						rs.getTimestamp("LAST_TIMESTAMP");

					append(metadata, "Set Met Review");
					append(metadata, QBR_NAME);
					append(metadata, CLIENT_IR_ID);
					append(metadata, ETS_PRACTICE);
					append(metadata, ETS_BSE);
					append(metadata, STATE);
					append(metadata, INTERVIEW_BY);

					pstmt.setString(1, QBR_ID);
					pstmt.setString(2, PROJECT_ID);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						String EXPECT_DESCRIPTION =
							rs2.getString("EXPECT_DESCRIPTION");
						String EXP_ACTION = rs2.getString("EXP_ACTION");
						String COMMENTS = rs2.getString("COMMENTS");

						append(metadata, EXPECT_DESCRIPTION);
						append(metadata, EXP_ACTION);
						append(metadata, COMMENTS);
					}
					close(rs2);

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty("ID", new StringDocumentProperty(QBR_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(PROJECT_ID));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(QBR_NAME));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(LAST_TIMESTAMP)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(QBR_NAME);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
		} finally {
			close(rs);
			close(stmt);
			close(pstmt);
			close(conn1);
			close(conn2);
		}
	}

	private void indexSelfAssessmentReviews(
		Index indexObj,
		StringBuffer indexInfoBuffer)
		throws Exception {

		String type = ETSSearchResult.TYPE_SELF_ASSESSMENT_STR;

		Connection conn1 = null;
		Connection conn2 = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			String query =
				"select MEMBER_IR_ID, COMMENTS"
					+ " from ETS.ETS_SELF_EXP"
					+ " where SELF_ID = ? and PROJECT_ID = ?"
					+ " with ur";
			conn2 = makeConn();
			pstmt = conn2.prepareStatement(query);

			query =
				"select SELF_ID, PROJECT_ID, SELF_NAME,"
					+ " SELF_PM, SELF_PLAN_OWNER, STATE, LAST_TIMESTAMP"
					+ " from ETS.ETS_SELF_MAIN"
					+ " with ur";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String SELF_ID = rs.getString("SELF_ID").trim();
				String PROJECT_ID = rs.getString("PROJECT_ID").trim();
				String SELF_NAME = rs.getString("SELF_NAME");

				String id = type + "-" + SELF_ID + "-" + PROJECT_ID;

				StringBuffer metadata = new StringBuffer();
				String[] ancestors = new String[] { type };

				try {
					String SELF_PM = rs.getString("SELF_PM");
					String SELF_PLAN_OWNER = rs.getString("SELF_PLAN_OWNER");
					String STATE = rs.getString("STATE");
					Timestamp LAST_TIMESTAMP =
						rs.getTimestamp("LAST_TIMESTAMP");

					append(metadata, "Self Assessment Review");
					append(metadata, SELF_NAME);
					append(metadata, SELF_PM);
					append(metadata, SELF_PLAN_OWNER);
					append(metadata, STATE);

					pstmt.setString(1, SELF_ID);
					pstmt.setString(2, PROJECT_ID);
					ResultSet rs2 = pstmt.executeQuery();
					while (rs2.next()) {
						String MEMBER_IR_ID = rs2.getString("MEMBER_IR_ID");
						String COMMENTS = rs2.getString("COMMENTS");

						append(metadata, MEMBER_IR_ID);
						append(metadata, COMMENTS);
					}
					close(rs2);

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty("ID", new StringDocumentProperty(SELF_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(PROJECT_ID));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(SELF_NAME));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(LAST_TIMESTAMP)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(SELF_NAME);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(0);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
		} finally {
			close(rs);
			close(stmt);
			close(pstmt);
			close(conn1);
			close(conn2);
		}
	}

	private void indexCalendar(Index indexObj, StringBuffer indexInfoBuffer)
		throws Exception {

		String type = null;

		Connection conn1 = null;
		Statement stmt = null;
		ResultSet rs = null;

		Connection conn2 = null;
		Connection conn3 = null;
		Connection conn4 = null;
		Connection conn5 = null;
		PreparedStatement filesStmt = null;
		PreparedStatement pdfFilesStmt = null;
		PreparedStatement docStmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement userNamePstmt = null;

		try {
			conn1 = makeConn();
			stmt = conn1.createStatement();

			conn2 = makeConn();
			filesStmt =
				conn2.prepareStatement(
					"select DOCFILE as DATA from ETS.ETS_DOCFILE where DOC_ID = ? and DOCFILE_ID = ? with ur");

			conn3 = makeConn();
			pdfFilesStmt =
				conn3.prepareStatement(
					"select CONTENT as DATA from ETS.EXTRACTED_FILES where DOC_ID = ? and DOCFILE_ID = ? with ur");

			conn4 = makeConn();
			docStmt =
				conn4.prepareStatement(
					"select d.DOC_ID, d.DOC_NAME, d.DOC_DESCRIPTION, d.DOC_KEYWORDS, f.DOCFILE_ID, f.DOCFILE_NAME"
						+ " from ETS.ETS_DOC d left outer join ETS.ETS_DOCFILE f"
						+ " on d.DOC_ID = f.DOC_ID"
						+ " where d.MEETING_ID = ?"
						+ " and d.DELETE_FLAG != '"
						+ Defines.TRUE_FLAG
						+ "' with ur");

			String query =
				"select CALENDAR_ID, PROJECT_ID, CALENDAR_TYPE, SCHEDULED_BY, START_TIME,"
					+ " SUBJECT, DESCRIPTION, INVITEES_ID, CC_LIST"
					+ " from ETS.ETS_CALENDAR"
					+ " where REPEAT_ID is null or length(REPEAT_ID) = 0"
					+ " with ur";

			conn5 = makeConn();
			userNamePstmt = getUserNamePstmt(conn5);
			TreeMap userNameCache = new TreeMap();

			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String CALENDAR_ID = rs.getString("CALENDAR_ID").trim();
				String PROJECT_ID = rs.getString("PROJECT_ID").trim();
				String CALENDAR_TYPE = rs.getString("CALENDAR_TYPE").trim();
				String longType = null;
				String[] ancestors = null;

				if (CALENDAR_TYPE.equalsIgnoreCase("M")) {
					type = ETSSearchResult.TYPE_MEETING_STR;
					ancestors = new String[] { type };
					longType = "meeting";
				} else if (CALENDAR_TYPE.equalsIgnoreCase("E")) {
					type = ETSSearchResult.TYPE_EVENT_STR;
					ancestors =
						new String[] { type, ETSSearchResult.TYPE_MAIN_STR };
					longType = "event";
				} else if (CALENDAR_TYPE.equalsIgnoreCase("A")) {
					type = ETSSearchResult.TYPE_ALERT_STR;
					ancestors =
						new String[] { type, ETSSearchResult.TYPE_MAIN_STR };
					longType = "alert message";
				} else {
					printErr("Unknown calendar type: " + CALENDAR_TYPE);
					continue;
				}

				String id = type + "-" + CALENDAR_ID;

				StringBuffer metadata = new StringBuffer();
				int contentLength = 0;
				StringBuffer docs = new StringBuffer();

				try {
					String SCHEDULED_BY = rs.getString("SCHEDULED_BY");
					Timestamp START_TIME = rs.getTimestamp("START_TIME");
					String SUBJECT = rs.getString("SUBJECT");
					String DESCRIPTION = rs.getString("DESCRIPTION");
					String INVITEES_ID = rs.getString("INVITEES_ID");
					String CC_LIST = rs.getString("CC_LIST");

					append(metadata, longType);
					append(metadata, CALENDAR_ID);
					append(metadata, SUBJECT);
					append(metadata, DESCRIPTION);
					append(metadata, SCHEDULED_BY);
					append(metadata, INVITEES_ID);
					append(metadata, CC_LIST);

					append(metadata, SCHEDULED_BY);
					append(
						metadata,
						getUserName(
							SCHEDULED_BY,
							true,
							userNameCache,
							userNamePstmt));

					if (type.equals(ETSSearchResult.TYPE_MEETING_STR)) {
						docStmt.setString(1, CALENDAR_ID);
						ResultSet rs2 = docStmt.executeQuery();
						while (rs2.next()) {
							int DOC_ID = rs2.getInt("DOC_ID");
							String DOC_NAME = rs2.getString("DOC_NAME");
							String DOC_DESCRIPTION =
								rs2.getString("DOC_DESCRIPTION");
							String DOC_KEYWORDS = rs2.getString("DOC_KEYWORDS");
							int DOCFILE_ID = rs2.getInt("DOCFILE_ID");
							String DOCFILE_NAME = rs2.getString("DOCFILE_NAME");

							append(metadata, repeatString(DOC_NAME, 10));
							append(metadata, DOC_DESCRIPTION);
							append(metadata, repeatString(DOC_KEYWORDS, 3));
							
							if(DOCFILE_ID > 0){
								docs.append(DOCFILE_ID);
								docs.append(',');
								
								append(metadata, DOCFILE_NAME);
								append(
										metadata,
										getFilenameWithoutExt(DOCFILE_NAME));
								
								contentLength
								+= getFileContents(
										DOC_ID,
										DOCFILE_ID,
										DOCFILE_NAME,
										metadata,
										filesStmt,
										pdfFilesStmt);
							}
						}
						close(rs2);
					}

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty(
						"ID",
						new StringDocumentProperty(CALENDAR_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(PROJECT_ID));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(SUBJECT));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(START_TIME)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(docs);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(contentLength);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}
			close(rs);

			query =
				"select distinct REPEAT_ID from ETS.ETS_CALENDAR"
					+ " where length(REPEAT_ID) > 0"
					+ " with ur";
			ArrayList repeatIds = new ArrayList();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String REPEAT_ID = rs.getString("REPEAT_ID");
				repeatIds.add(REPEAT_ID);
			}
			close(rs);
			stmt.close();

			query =
				"select CALENDAR_ID, PROJECT_ID, SCHEDULED_BY, START_TIME,"
					+ " SUBJECT, DESCRIPTION, INVITEES_ID, CC_LIST"
					+ " from ETS.ETS_CALENDAR"
					+ " where REPEAT_ID = ?"
					+ " with ur";

			Timestamp now = new Timestamp(System.currentTimeMillis());
			type = ETSSearchResult.TYPE_MEETING_STR;

			pstmt = conn1.prepareStatement(query);
			Iterator iter = repeatIds.iterator();
			while (iter.hasNext()) {

				String REPEAT_ID = (String) iter.next();

				Timestamp START_TIME = null;
				String CALENDAR_ID = null;
				String id = type + "-" + REPEAT_ID;
				String PROJECT_ID = null;
				String SUBJECT = null;
				StringBuffer metadata = new StringBuffer();
				String[] ancestors = new String[] { type };
				int contentLength = 0;
				StringBuffer docs = new StringBuffer();

				append(metadata, "meeting");

				try {
					pstmt.setString(1, REPEAT_ID);
					rs = pstmt.executeQuery();
					while (rs.next()) {
						String calID = rs.getString("CALENDAR_ID").trim();
						PROJECT_ID = rs.getString("PROJECT_ID").trim();
						String SCHEDULED_BY = rs.getString("SCHEDULED_BY");
						Timestamp ts = rs.getTimestamp("START_TIME");
						String subject = rs.getString("SUBJECT");
						String DESCRIPTION = rs.getString("DESCRIPTION");
						String INVITEES_ID = rs.getString("INVITEES_ID");
						String CC_LIST = rs.getString("CC_LIST");

						if (isMoreCurrent(ts, START_TIME, now)) {
							START_TIME = ts;
							CALENDAR_ID = calID;
							SUBJECT = subject;
						}

						append(metadata, subject);
						append(metadata, DESCRIPTION);
						append(metadata, SCHEDULED_BY);
						append(metadata, INVITEES_ID);
						append(metadata, CC_LIST);

						docStmt.setString(1, calID);
						ResultSet rs2 = docStmt.executeQuery();
						while (rs2.next()) {
							int DOC_ID = rs2.getInt("DOC_ID");
							String DOC_NAME = rs2.getString("DOC_NAME");
							String DOC_DESCRIPTION =
								rs2.getString("DOC_DESCRIPTION");
							String DOC_KEYWORDS = rs2.getString("DOC_KEYWORDS");
							int DOCFILE_ID = rs2.getInt("DOCFILE_ID");
							String DOCFILE_NAME = rs2.getString("DOCFILE_NAME");

							append(metadata, repeatString(DOC_NAME, 10));
							append(metadata, DOC_DESCRIPTION);
							append(metadata, repeatString(DOC_KEYWORDS, 3));
							
							if(DOCFILE_ID > 0){
								docs.append(DOCFILE_ID);
								docs.append(',');
								
								append(metadata, DOCFILE_NAME);
								append(
										metadata,
										getFilenameWithoutExt(DOCFILE_NAME));
								
								contentLength
								+= getFileContents(
										DOC_ID,
										DOCFILE_ID,
										DOCFILE_NAME,
										metadata,
										filesStmt,
										pdfFilesStmt);
							}
						}
						close(rs2);
					}
					close(rs);

					docStmt.setString(1, REPEAT_ID);
					ResultSet rs2 = docStmt.executeQuery();
					while (rs2.next()) {
						int DOC_ID = rs2.getInt("DOC_ID");
						String DOC_NAME = rs2.getString("DOC_NAME");
						String DOC_DESCRIPTION =
							rs2.getString("DOC_DESCRIPTION");
						String DOC_KEYWORDS = rs2.getString("DOC_KEYWORDS");
						int DOCFILE_ID = rs2.getInt("DOCFILE_ID");
						String DOCFILE_NAME = rs2.getString("DOCFILE_NAME");

						append(metadata, repeatString(DOC_NAME, 10));
						append(metadata, DOC_DESCRIPTION);
						append(metadata, repeatString(DOC_KEYWORDS, 3));
						
						if(DOCFILE_ID > 0){
							docs.append(DOCFILE_ID);
							docs.append(',');
							
							append(metadata, DOCFILE_NAME);
							append(metadata, getFilenameWithoutExt(DOCFILE_NAME));
							
							contentLength
							+= getFileContents(
									DOC_ID,
									DOCFILE_ID,
									DOCFILE_NAME,
									metadata,
									filesStmt,
									pdfFilesStmt);
						}
					}
					close(rs2);

					id = type + "-" + CALENDAR_ID;

					TextFileDocument tfd = new TextFileDocument(id);

					tfd.addProperty(
						"ID",
						new StringDocumentProperty(CALENDAR_ID));

					tfd.addProperty("TYPE", new StringDocumentProperty(type));

					tfd.addProperty(
						"PROJECT_ID",
						new StringDocumentProperty(PROJECT_ID));

					tfd.addProperty(
						"NAME",
						new StringDocumentProperty(SUBJECT));

					tfd.addProperty(
						"DATE",
						new StringDocumentProperty(formatDate(START_TIME)));

					indexObj.addDocument(
						tfd,
						metadata.toString(),
						ancestors,
						new String[] { PROJECT_ID });

				} catch (Exception e) {
					indexInfoBuffer.append("!!! ERROR !!!\n");
					logError("ERROR adding: " + id, e);
					throw e;
				} finally {
					indexInfoBuffer.append("REPEAT-");
					indexInfoBuffer.append(id);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(docs);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(PROJECT_ID);
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(metadata.length());
					indexInfoBuffer.append(':');
					indexInfoBuffer.append(contentLength);
					indexInfoBuffer.append(':');
					for (int i = 0; i < ancestors.length; i++) {
						indexInfoBuffer.append(ancestors[i]);
						indexInfoBuffer.append(',');
					}
					indexInfoBuffer.append('\n');
				}
			}

		} finally {
			close(rs);
			close(stmt);
			close(pstmt);
			close(docStmt);
			close(filesStmt);
			close(pdfFilesStmt);
			close(userNamePstmt);
			close(conn1);
			close(conn2);
			close(conn3);
			close(conn4);
			close(conn5);
		}
	}

	private int getFileContents(
		int docId,
		int docfileId,
		String fileName,
		StringBuffer metadata,
		PreparedStatement filesStmt,
		PreparedStatement pdfFilesStmt)
		throws SQLException {

		boolean hasSearchableFile = false;
		ResultSet rs = null;

		try {

			if (isPdf(fileName)) {
				pdfFilesStmt.setInt(1, docId);
				pdfFilesStmt.setInt(2, docfileId);
				rs = pdfFilesStmt.executeQuery();
				hasSearchableFile = true;
			} else if (isHtml(fileName) || isPlainText(fileName)) {
				filesStmt.setInt(1, docId);
				filesStmt.setInt(2, docfileId);
				rs = filesStmt.executeQuery();
				hasSearchableFile = true;
			}

			if (hasSearchableFile) {
				if (!rs.next()) {
					print(
						"No file (ITAR) for DOC_ID: "
							+ docId
							+ " DOCFILE_ID: "
							+ docfileId
							+ " fileName: "
							+ fileName
							+ "\n",
						false);
				} else {
					if (isPdf(fileName) || isPlainText(fileName)) {
						return getPlainTextFileContents(
							rs,
							"DATA",
							metadata,
							docId + ":" + docfileId,
							fileName);
					} else if (isHtml(fileName)) {
						return getHtmlFileContents(
							rs,
							"DATA",
							metadata,
							docId + ":" + docfileId,
							fileName);
					} else {
						logError("Trying to parse content for unsupported filetype: should never happen");
					}
				}
			}

		} finally {
			close(rs);
		}

		return 0;
	}

	private boolean isMoreCurrent(
		Timestamp ts,
		Timestamp existing,
		Timestamp now) {

		if (existing == null
			|| (ts.after(existing) && ts.before(now))
			|| (ts.before(existing) && ts.after(now))) {
			return true;
		} else {
			return false;
		}
	}

	private void append(StringBuffer buffer, String str) {
		if (str != null && str.length() > 0) {
			buffer.append(str);
			buffer.append(' ');
		}
	}

	private void append(StringBuffer buffer, StringBuffer str) {
		if (str != null && str.length() > 0) {
			buffer.append(str);
			buffer.append(' ');
		}
	}

	private void parseLastUpdateFile(String lastUpdateStr) {
		try {
			if (lastUpdateStr.length() == 0) {
				return;
			}

			StringTokenizer tokenizer =
				new StringTokenizer(lastUpdateStr, String.valueOf(DELIM));

			lastVersion = tokenizer.nextToken();

			for (int i = 0; i < TABLES.length; i++) {
				String timestamp = tokenizer.nextToken();
				if (timestamp != null && !timestamp.equals("null")) {
					lastTimestamp[i] = Timestamp.valueOf(timestamp);
				}
				lastNumRecords[i] = Integer.parseInt(tokenizer.nextToken());
			}
		} catch (NoSuchElementException e) {
			printOut(
				"NoSuchElementException parsing lastUpdateStr: "
					+ lastUpdateStr
					+ ".");
		} catch (Exception e) {
			printErr(
				"Exception parsing lastUpdateStr: " + lastUpdateStr + ".",
				e);
		}
	}

	void wrapup(Index indexObj, String indexInfoStr) throws IOException {
		printOut("Starting index.build()");
		indexObj.build();
		printOut("Finished index.build()");
		indexObj.close();

		String tmp = indexDirInUse;
		indexDirInUse = indexDirNotInUse;
		indexDirNotInUse = tmp;

		writeFile(indexPointer, indexDirInUse);

		lastVersion = VERSION_SID;

		StringBuffer lastUpdateBuffer = new StringBuffer();
		lastUpdateBuffer.append(lastVersion);
		lastUpdateBuffer.append(DELIM);

		for (int i = 0; i < TABLES.length; i++) {
			lastTimestamp[i] = newTimestamp[i];
			lastNumRecords[i] = newNumRecords[i];

			lastUpdateBuffer.append(newTimestamp[i]);
			lastUpdateBuffer.append(DELIM);
			lastUpdateBuffer.append(newNumRecords[i]);
			lastUpdateBuffer.append(DELIM);
		}

		writeFile(lastUpdateFile, lastUpdateBuffer.toString());
		writeFile(indexInfo, indexInfoStr);
	}

	private boolean haveTablesChanged() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = makeConn();
			stmt = conn.createStatement();

			boolean[] tableChanged = new boolean[TABLES.length];

			for (int i = 0; i < TABLES.length; i++) {
				String columnName = "LAST_TIMESTAMP";
				if (TABLES[i].length > 1) {
					columnName = TABLES[i][1];
				}

				rs =
					stmt.executeQuery(
						"select MAX("
							+ columnName
							+ "), COUNT(*) from "
							+ TABLES[i][0]
							+ " with ur");
				rs.next();
				newTimestamp[i] = rs.getTimestamp(1);
				newNumRecords[i] = rs.getInt(2);
				close(rs);
				rs = null;

				tableChanged[i] =
					newTimestamp[i] != null
						&& (!newTimestamp[i].equals(lastTimestamp[i])
							|| newNumRecords[i] != lastNumRecords[i]);

				if (tableChanged[i]) {
					printOut(TABLES[i][0] + " has changed");
				}
			}

			String thisVersion = VERSION_SID;
			boolean versionChanged = !thisVersion.equals(lastVersion);
			if (versionChanged) {
				printOut("code version has changed");
			}

			for (int i = 0; i < TABLES.length; i++) {
				if (tableChanged[i]) {
					return true;
				}
			}
			return versionChanged;
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
	}

	Index getNewIndex() throws IOException {
		File dir = new File(indexDirNotInUse);

		if (!dir.isDirectory()) {
			throw new IOException(
				indexDirNotInUse + " is not a valid directory");
		}

		if (!deleteFileStructure(dir, false, "docs")) {
			throw new IOException(
				"Unable to delete contents of: " + indexDirNotInUse);
		}

		StemmedLanguage language = new StemmedLanguage();
		language.unfilterStopwords();
		language.extractOriginalWords();

		return Index.createIndex(dir, language);
	}

	private boolean deleteFileStructure(
		File f,
		boolean deleteRoot,
		String exemptDir) {

		if (f.isDirectory()) {
			File[] children = f.listFiles();
			for (int i = 0; i < children.length; i++) {
				if (!deleteFileStructure(children[i], true, exemptDir)) {
					return false;
				}
			}
		}

		if (deleteRoot) {
			return f.delete()
				|| (f.isDirectory() && f.getName().equals(exemptDir));
		} else {
			return true;
		}

	}

	void cleanupTempDir() throws IOException {
		if (!tempDir.isDirectory()) {
			throw new IOException(tempDir + " is not a valid directory");
		}

		String[] files = tempDir.list();

		if (files.length != 0) {

			logError(
				"The folowing temp files in "
					+ tempDir
					+ " were not cleaned-up in the previous run: "
					+ getArrayContents(files));

			if (!deleteFileStructure(tempDir, false, null)) {
				throw new IOException(
					"Unable to delete contents of: " + tempDir);
			}

		}
	}

	String getArrayContents(String[] arr) {
		if (arr == null)
			return null;

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			buffer.append(arr[i]);
			buffer.append(' ');
		}
		return buffer.toString();
	}

	String[] getAncestors(
		int catId,
		PreparedStatement ancestorsStmt,
		String root)
		throws SQLException {

		ArrayList ancestors = new ArrayList();
		ancestors.add(String.valueOf(catId));

		while (catId > 0) {
			ancestorsStmt.setInt(1, catId);
			ResultSet rs = ancestorsStmt.executeQuery();

			if (rs.next()) {
				catId = rs.getInt("PARENT_ID");
				close(rs);
			} else {
				close(rs);
				break;
			}

			String catIdStr = String.valueOf(catId);
			if (!ancestors.contains(catIdStr)) {
				ancestors.add(catIdStr);
			} else {
				addError(
					"DOC_ID: "
						+ ancestors.get(0)
						+ " has mutiple instances of "
						+ catIdStr
						+ " in its ancestral chain!");
				break;
			}
		}

		/*
		String root = (String) ancestors.get(ancestors.size() - 1);
		if (!root.equals("0")) {
			ancestors.add("0");
		}
		*/

		if (root != null) {
			ancestors.add(root);
		}

		String[] ancestorsArr = new String[ancestors.size()];

		return (String[]) ancestors.toArray(ancestorsArr);

	}

	void resetVariables() {
		numDocs = 0;
		numFiles = 0;
		sizeDocs = sizeFiles = 0;
		unparsedHtmlLength = unparsedPdfLength = 0;

		// set to 1 to avoid division by 0 exception
		parsedHtmlLength = parsedPdfLength = 1;
	}

	String formatDate(Date date) {
		return String.valueOf(date.getTime());
	}

	String formatFileSize(int size) {
		return String.valueOf(size);
	}

	int getPlainTextFileContents(
		ResultSet rs,
		String columnName,
		StringBuffer contentBuffer,
		String id,
		String name)
		throws SQLException {

		byte[] arr = rs.getBytes(columnName);
		if (arr == null || arr.length == 0) {
			return 0;
		}

		String contents = new String(arr);
		contentBuffer.append(contents);
		int len = contents.length();
		parsedPdfLength += len;
		return len;
	}

	int getHtmlFileContents(
		ResultSet rs,
		String columnName,
		StringBuffer contentBuffer,
		String id,
		String name)
		throws SQLException {

		byte[] arr = rs.getBytes(columnName);
		if (arr == null || arr.length == 0) {
			return 0;
		}

		String contents = new String(arr);
		StringBuffer tmpBuffer = new StringBuffer(contents);

		unparsedHtmlLength += contents.length();
		int initialLength = contentBuffer.length();

		try {

			HTMLParse parser = new HTMLParse(tmpBuffer);

			String extractedTitle = parser.getTitle();

			if (extractedTitle != null) {
				extractedTitle = extractedTitle.trim();
				contentBuffer.append(repeatString(extractedTitle, 5));
			}

			contentBuffer.append(repeatString(parser.getStrongText(), 3));
			contentBuffer.append(repeatString(parser.getMidText(), 2));
			contentBuffer.append(parser.getText());

			int len = contentBuffer.length() - initialLength;
			parsedHtmlLength += len;
			return len;

		} catch (Exception e) {
			addError(
				"ERROR parsing contents of HTML document ID: "
					+ id
					+ " name: "
					+ name,
				e);
			contentBuffer.append(tmpBuffer);
			return tmpBuffer.length();
		}
	}

	private int readCharacterStream(
		Reader reader,
		StringBuffer content,
		int bufferSize)
		throws IOException {

		int totalCharsRead = 0;
		int charsRead = 0;
		char[] arr = new char[bufferSize];

		while ((charsRead = reader.read(arr)) > 0) {
			content.append(arr, 0, charsRead);
			totalCharsRead += charsRead;
		}
		reader.close();

		return totalCharsRead;
	}

	private int readInputStream(
		InputStream in,
		StringBuffer content,
		int bufferSize)
		throws IOException {

		return readCharacterStream(
			new InputStreamReader(in),
			content,
			bufferSize);
	}

	private String readInputStream(InputStream in) throws IOException {
		StringBuffer content = new StringBuffer(64);
		readInputStream(in, content, 64);
		return content.toString();
	}

	private void writeFile(File f, String contents) throws IOException {
		FileWriter writer = new FileWriter(f);
		writer.write(contents);
		writer.close();
	}

	StringBuffer repeatString(String str, int numTimesRepeat) {

		if (str == null)
			return new StringBuffer(SPACE);

		StringBuffer buffer =
			new StringBuffer(((str.length() + 1) * numTimesRepeat) + 1);

		buffer.append(SPACE);

		for (int i = 0; i < numTimesRepeat; i++) {
			buffer.append(str);
			buffer.append(SPACE);
		}

		return buffer;

	}

	private String getFilenameWithoutExt(String filename) {
		if (filename != null) {
			int firstDot = filename.indexOf('.');
			if (firstDot > 0 && firstDot < filename.length()) {
				return filename.substring(0, firstDot);
			}
		}

		return "";
	}

	boolean isSearchable(String file_name) {
		return isPdf(file_name) || isHtml(file_name) || isPlainText(file_name);
	}

	boolean isPlainText(String file_name) {
		if (file_name == null) {
			return false;
		} else {
			file_name = file_name.toLowerCase();
		}

		if (file_name.endsWith(".txt"))
			return true;
		else
			return false;
	}

	boolean isHtml(String file_name) {
		if (file_name == null) {
			return false;
		} else {
			file_name = file_name.toLowerCase();
		}

		if (file_name.endsWith(".html") || file_name.endsWith(".htm"))
			return true;
		else
			return false;
	}

	boolean isPdf(String file_name) {
		if (file_name == null) {
			return false;
		} else {
			file_name = file_name.toLowerCase();
		}

		if (file_name.endsWith(".pdf"))
			return true;
		else
			return false;
	}

	static boolean printOut(String str) {
		String s = VERSION_SID + ": " + new Date() + ": " + str + "\n";
		return print(s, false);
	}

	static boolean printErr(String str) {
		String s = VERSION_SID + ": " + new Date() + ": " + str + "\n";
		return print(s, true);
	}

	static boolean printErr(String str, Throwable t) {
		return printErr(str + "\nStacktrace:\n" + getStackTrace(t));
	}

	static void logInfo(String str) {
		String s = VERSION_SID + ": " + new Date() + ": " + str + "\n";
		print(s, false);

		if (MAIL_INFO) {
			sendMail(
				hostname + ": " + "SearchCreateIndex info",
				hostname + ": " + s);
		}
	}

	static void logError(String str) {
		String s = VERSION_SID + ": " + new Date() + ": " + str + "\n";
		print(s, true);
		sendMail(
			hostname + ": " + "ERROR in Search Create Index",
			hostname + ": " + s);
	}

	static void logError(String str, Throwable t) {
		logError(str + "\nStacktrace:\n" + getStackTrace(t));
	}

	static void addInfo(StringBuffer sb) {
		addInfo(sb.toString());
	}

	static void addInfo(String str) {
		String s = VERSION_SID + ": " + new Date() + ": \n" + str + "\n";
		print(s, false);
		if (MAIL_INFO) {
			infoBuffer.append(s);
		}
	}

	static void logInfo() {
		if (MAIL_INFO && infoBuffer.length() > 0) {
			sendMail(
				hostname + ": " + "SearchCreateIndex info",
				hostname + ": " + infoBuffer.toString());
			infoBuffer = new StringBuffer(512);
		}
	}

	static void addError(String str) {
		String s = VERSION_SID + ": " + new Date() + ": " + str + "\n";
		print(s, true);
		errorBuffer.append(s);
	}

	static void addError(String str, Throwable t) {
		String s =
			VERSION_SID
				+ ": "
				+ new Date()
				+ ": "
				+ str
				+ "\nStacktrace:\n"
				+ getStackTrace(t)
				+ "\n";
		print(s, true);
		errorBuffer.append(s);
	}

	static void logError() {
		if (errorBuffer.length() > 0) {
			sendMail(
				hostname + ": " + "ERROR in Search Create Index",
				hostname + ": " + errorBuffer.toString());
			errorBuffer = new StringBuffer(512);
		}
	}

	private static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	private static boolean print(String s, boolean error) {

		String logFile = outLog;
		if (error) {
			logFile = errLog;
		}

		if (logFile == null) {
			System.err.println(s);
		} else {
			FileOutputStream out = null;

			try {
				out = new FileOutputStream(logFile, true);
				out.write(s.getBytes());
				return true;
			} catch (Throwable t) {
				System.err.println(
					"Exception writing the following to "
						+ logFile
						+ ":\n"
						+ s
						+ "\nStacktrace:\n"
						+ getStackTrace(t));
			} finally {
				try {
					out.close();
				} catch (Throwable t) {
				}
			}

		}

		return false;
	}

	private static boolean sendMail(String subject, String text) {
		try {

			Properties props = new Properties();
			props.put("mail.smtp.host", mailHost);

			javax.mail.Session session =
				javax.mail.Session.getDefaultInstance(props, null);

			MimeMessage msg = new MimeMessage(session);

			if (mailFrom != null) {
				msg.setFrom(mailFrom);
			}

			msg.setRecipients(Message.RecipientType.TO, mailTo);
			msg.setSubject(subject);
			msg.setText(text);

			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(msg);
			transport.close();

			return true;

		} catch (Throwable t) {
			printErr("Exception sending mail with subject: " + subject, t);
			return false;
		}
	}

	Connection makeConn() throws SQLException {
		return DriverManager.getConnection(db2Url, db2Props);
	}

	void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				printErr("SQLException", e);
			}
		}
	}

	void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				printErr("SQLException", e);
			}
		}
	}

	void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				printErr("SQLException", e);
			}
		}
	}

	static PreparedStatement getUserNamePstmt(Connection conn)
		throws SQLException {

		return conn.prepareStatement(
			"select USER_FULLNAME from AMT.USERS"
				+ " where IR_USERID = ? or EDGE_USERID = ?"
				+ " with ur");
	}

	static String getUserName(
		String userid,
		boolean isIRuserid,
		Map cache,
		PreparedStatement pstmt)
		throws SQLException {

		if (userid == null) {
			return "N.A.";
		}

		String userName = (String) cache.get(isIRuserid + userid);
		if (userName != null) {
			return userName;
		}

		ResultSet rs = null;
		try {
			if (isIRuserid) {
				pstmt.setString(1, userid);
				pstmt.setString(2, "<<DuMmY>>");
			} else {
				pstmt.setString(1, "<<DuMmY>>");
				pstmt.setString(2, userid);
			}

			rs = pstmt.executeQuery();
			if (rs.next()) {
				userName = rs.getString(1);
			}
		} finally {
			rs.close();
		}

		if (userName != null) {
			userName = userName.trim();
		} else {
			userName = userid;
		}

		cache.put(isIRuserid + userid, userName);
		return userName;
	}

	private class ETSFileInfo {
		int docId;
		int docfileId;
		String fileName;
		int fileSize;
		Timestamp timestamp;

		String projectId;
		int parentId;
		String title;
		String description;
		String keywords;
		int docType;
		String userId;
	}

}
