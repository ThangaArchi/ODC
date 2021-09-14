package oem.edge.ed.sd;

import oem.edge.transitivetrust.TransitiveTrust;
import oem.edge.transitivetrust.UserData;
import oem.edge.transitivetrust.TransitiveTrustException;

import oem.edge.common.cipher.ODCrc;
import oem.edge.common.cipher.ODCipher;
import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.common.cipher.CipherException;
import oem.edge.common.cipher.DecodeException;

import oem.edge.common.RSA.RSAKeyPair;

// import oem.edge.ed.util.EDCMafsFile;
import oem.edge.ed.util.PasswordUtils;

import oem.edge.ed.sd.ordproc.Mailer;
import oem.edge.ed.sd.ordproc.EDSDContentDeliveryTable;

import sun.misc.BASE64Decoder;

//import java.sql.DriverManager;
import oem.edge.datasource.DataSourceManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.Vector;
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.Properties;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
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

public class SdUtils {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";

    final static String version = "v042005.1: ";

    final static String helpDesk = "eConnect@us.ibm.com";

    static boolean useDB2 = false;

    static final String PROPS_FILE_TAG = "edsd.properties_file";

    private static Properties props;

    private static String sla; //software license agreement

    private static ODCipher cipher;

    private static String ssoCookieName, ssoCookieDomain;

    private static boolean initialized = false;
    private static boolean authOK      = false;
    private static boolean mailOK      = false;

    private static boolean bypassDispatcher = false;
    static boolean useMultipleThreads = false;

    private static String tablePath, slaPath, cipherPath, propsFilePath;

    private static String ordersFile, completedFile, statsFile;
    private static String bcoUrlPrefix;
    private static String loginServlet;
    //boo 10/12
    private static String odcHOME;

    private static Hashtable ordersTable;
    private static Hashtable filesTable;
    private static Hashtable usersTable;

    private static long ordersLastParsed;
    private static long completedLastParsed;
    private static long propsLastParsed;

    private static Vector runningDownloads = new Vector();

    private static String afsCell, uid, pwd;

    private static long nextAuthenticate;


//    private static EDCMafsFile authenticator = new EDCMafsFile();

    static int blockSizeInKB, numBlocks;

    private static String adminStr;

    private static String[] admins;

    static String hostname;
    static String shortHostname;

    private static String sqlQueryForm;


    static String appletCode, appletCodebase, appletArchive, appletCabbase, helperInstall;


    private static String outFile, errFile, stdOutFile, stdErrFile;

    private static String mailHost1 = "mailgw.chips.ibm.com";
    private static String mailHost2 = "us.ibm.com";

    private static String mailTo = "fyuan@us.ibm.com";

    private static String mailCc = null;

    private static String jdbcDriverClassName, db2Url, db2User, db2Pwd, db2Table;

    private static String dce_monitor = "/var/local/etc/dce_monitor";

    private static boolean enableBandwidthCutoff = false;
  // private static String bandwidthLog = "/afs/chips/data/edgeprod/metrics/MRTG.summary1";
   private static String bandwidthLog = "/web/server_root/datapersist/technologyconnect/sd/metrics/MRTG.summary1";
    private static String bottleneck = "inet-out.chips.ibm.com-7";
    private static int outboundCutoff = 90; //percentage
    private static int totalCutoff = 200; //percentage
    private static String bandwidthCmd = null;

    private static final long bandwidthLogRefreshTime = 5 * 60 * 1000; // 5 minutes

    private static long nextBandwidthCheckTime = 0;
    private static long bandwidthLogLastModified = -2;
    private static boolean allowNewConnection = true;


    static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy-HH:mm:ss");
    static final SimpleDateFormat customerFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm z");    // to show customer expiration date
    static final SimpleDateFormat db2Formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS000");    // to show customer expiration date


    private static final long AUTH_DURATION = 6 * 60 * 60 * 1000; // 6 hours

    static final int TOKEN_DURATION = 20 * 60; // 20 minutes (in seconds)

    // 401 UNAUTHORIZED
    static final int AUTH_NULL               = 0;
    static final int AUTH_FAILED             = 1;
    static final int NOT_ADMIN               = 2;

    // 403 FORBIDDEN
    static final int ID_NULL                 = 3;
    static final int ID_NOT_PARSABLE         = 4;
    static final int ID_NOT_FOUND            = 5;
    static final int ORDER_EXPIRED           = 6;
    static final int MUST_ACCEPT_SLA         = 7;

    // new
    static final int USER_MISMATCH           = 8;
    static final int INVALID_CRC             = 9;
    static final int TOKEN_EXPIRED           = 10;
    static final int SSO_EXPIRED             = 11;


    // 503 SERVICE UNAVAILABLE
    static final int REQUIRED_FIELD_MISSING  = 12;
    static final int INTERNAL_AUTH_FAILURE   = 13;
    static final int FILE_NOT_FOUND          = 14;
    static final int NULL_INPUT_STREAM       = 15;
    static final int NULL_OUTPUT_STREAM      = 16;
    static final int EXCEPTION               = 17;
    static final int INIT_FAILED             = 18;
    static final int NULL_LOGON_CONTEXT      = 19;
    static final int INVALID_LOGON_CONTEXT   = 20;
    static final int INVALID_CODE_FOR_METHOD = 21;
    static final int SERVER_BUSY             = 22;



    private static final String FILE_START   = EDSDContentDeliveryTable.FILE_START;
    private static final String FILE_STOP    = EDSDContentDeliveryTable.FILE_STOP;

    private static final String ORDER_START  = EDSDContentDeliveryTable.ORDER_START;
    private static final String ORDER_STOP   = EDSDContentDeliveryTable.ORDER_STOP;


    static final String FILE_ID         = EDSDContentDeliveryTable.FILE_ID;
    static final String ORDER_ID        = EDSDContentDeliveryTable.ORDER_ID;
    static final String USER_ID         = EDSDContentDeliveryTable.USER_ID;

    // order-specific props
    static final String NUM_FILES       = EDSDContentDeliveryTable.NUM_FILES;
    static final String EMAIL           = EDSDContentDeliveryTable.EMAIL;
    static final String EXPIRATION_TIME = EDSDContentDeliveryTable.EXPIRATION_TIME;

    // file-specific props
    static final String FILE_NAME       = EDSDContentDeliveryTable.FILE_NAME;
    static final String FILE_DESC       = EDSDContentDeliveryTable.FILE_DESC;
    static final String FILE_PATH       = EDSDContentDeliveryTable.FILE_PATH;
    static final String MIME_TYPE       = EDSDContentDeliveryTable.MIME_TYPE;
    static final String NUM             = EDSDContentDeliveryTable.NUM;

    static final String COMPLETED       = "COMPLETED";
    static final String FILES           = "FILES";



    static final String BASIC_ENT       = "BASIC_ENT";
    static final String OWNER_ENT       = "OWNER_ENT";
    static final String PROJECT_ENT     = "PROJECT_ENT";
    static final String PROJECT_FSE_ENT = "PROJECT_FSE_ENT";
    static final String USER_FSE_ENT    = "USER_FSE_ENT";

    // super entitlements
    static final String MASTER_FSE      = "MASTER_FSE";
    static final String TECH_PM         = "TECH_PM";
    static final String EDSGN_ADMIN     = "EDSGN_ADMIN";
    static final String FSE_ADMIN       = "FSE_ADMIN";


    static final String[] SUPER_ENTITLEMENTS = {
                                                MASTER_FSE,
                                                TECH_PM,
                                                EDSGN_ADMIN,
                                                FSE_ADMIN
                                               };



    static synchronized boolean initialize(String file) {

        if(initialized)
            return true;


        try {

            propsFilePath = file;

            hostname = java.net.InetAddress.getLocalHost().getHostName();
            if (hostname.indexOf('.') >=0 )
               shortHostname = hostname.substring(0, hostname.indexOf('.'));
            else
               shortHostname = hostname;

            try {

                File propsFile = new File(propsFilePath);

                if(propsFile.lastModified() <= 0)
                    throw new RuntimeException("Could not find property file: " + propsFilePath);

                if(propsLastParsed != propsFile.lastModified()) {
                    props = new Properties();
                    FileInputStream in = new FileInputStream(propsFile);
                    try {
                        props.load(in);
                    }
                    finally {
                        in.close();
                    }
                }

                outFile     = getRequiredProperty("out_log");
                errFile     = getRequiredProperty("err_log");
                stdOutFile  = getRequiredProperty("std_out");
                stdErrFile  = getRequiredProperty("std_err");

                mailHost1   = getRequiredProperty("mail_host_1");
                mailHost2   = hostname;

                mailTo      = getRequiredProperty("mail_to");


                dce_monitor = props.getProperty("dce_monitor");
		if(dce_monitor != null && dce_monitor.trim().length() == 0)
		    dce_monitor = null;


                adminStr    = getRequiredProperty("admins");

                slaPath     = getRequiredProperty("sla_path");
                cipherPath  = getRequiredProperty("cipher_path");
               //  pwdPath     = getRequiredProperty("pwd_path");
                tablePath   = getRequiredProperty("table_path");

               // afsCell     = getRequiredProperty("afs_cell");
               // uid         = getRequiredProperty("afs_user");


		bcoUrlPrefix   = getRequiredProperty("bco_url_prefix");
                loginServlet = bcoUrlPrefix.substring(0, bcoUrlPrefix.lastIndexOf('/')) + "/LoginServlet.wss";

                appletCode     = getRequiredProperty("applet_code");
                appletCodebase = getRequiredProperty("applet_codebase");
                appletArchive  = getRequiredProperty("applet_archive");
                appletCabbase  = getRequiredProperty("applet_cabbase");
                helperInstall  = getRequiredProperty("helper_install");

		//subu 10/12
				
		odcHOME = getRequiredProperty("odcDESKTOPBASE");
               //end of Subu change

                mailCc = props.getProperty("mail_cc");

                String usedb = props.getProperty("useDB2");
                if(usedb != null && usedb.equalsIgnoreCase("yes"))
                    useDB2 = true;

                if(useDB2) {
                    jdbcDriverClassName = getRequiredProperty("jdbcDriverClassName");
                    db2Url =  getRequiredProperty("db2Url");
                    db2User = getRequiredProperty("db2User");
                    db2Pwd = getRequiredProperty("db2Pwd");
		    db2Table = getRequiredProperty("db2TableName");
                }

		String bandwidthCutoff = props.getProperty("enableBandwidthCutoff");
                if(bandwidthCutoff != null && bandwidthCutoff.equalsIgnoreCase("yes"))
		    enableBandwidthCutoff = true;

		if(enableBandwidthCutoff) {
		    bandwidthLog = getRequiredProperty("mrtg1_file");
		    bottleneck = getRequiredProperty("bottleneck_interface");
		    outboundCutoff = Integer.parseInt(getRequiredProperty("outbound_cutoff"));
		    totalCutoff = Integer.parseInt(getRequiredProperty("total_cutoff"));
		    bandwidthCmd = "grep \'" + bottleneck + "\' " + bandwidthLog + " | cut -d \':\' -f 6,8";
		}


                if(props.getProperty("bypass_dispatcher", "no").trim().equalsIgnoreCase("yes"))
                    bypassDispatcher = true;
                else
                    bypassDispatcher = false;


                if(props.getProperty("useMultipleThreads", "no").trim().equalsIgnoreCase("yes"))
                    useMultipleThreads = true;
                else
                    useMultipleThreads = false;


                blockSizeInKB = Integer.parseInt(props.getProperty("blockSizeInKB", "64"));
		numBlocks = Integer.parseInt(props.getProperty("numBlocks", "8"));


            }
            catch(Throwable t) {
                sendImpAlert("The following exception was thrown while reading property file: " + file + "\nStackTrace:\n" + getStackTrace(t));
                servletAlert("Servlet Initialization Failed", false);
                return false;
            }


            print("Initializing SdUtils", false);


            if(mailCc == null || mailCc.trim().length() == 0)
                mailCc = null;

            if(tablePath.endsWith("/")) {
                ordersFile = tablePath + "orders";
                completedFile = tablePath + "completed";
	        statsFile = tablePath + "download_stats." + shortHostname;
            }
            else {
                ordersFile = tablePath + "/orders";
                completedFile = tablePath + "/completed";
	        statsFile = tablePath + "/download_stats." + shortHostname;
            }

            admins = stringToArray(adminStr);

            sla = readLicenseAgreement(slaPath);

           // pwd = PasswordUtils.getPassword(pwdPath);

            if(useDB2)
              //  db2Pwd = PasswordUtils.getPassword(db2PwdPath);
                 db2Pwd = getRequiredProperty("db2Pwd");
           /*  if( ! authenticate() ) {
                sendImpAlert("Authentication Failure at Servlet Initialization");
                servletAlert("Servlet Initialization Failed", false);
                return false;
                }*/

	    SdMonitor.startSdMonitor(statsFile);

            checkFiles();

            cipher = ODCipherRSAFactory.newFactoryInstance().newInstance(cipherPath);

            ssoCookieName = TransitiveTrust.getCookieSettings().getName();
            if(ssoCookieName == null || ssoCookieName.trim().length() == 0)
                throw new RuntimeException("No configured ssoCookieName in sso properties");

            ssoCookieDomain = TransitiveTrust.getCookieSettings().getDomain();


            formatter.setCalendar(Calendar.getInstance());
            customerFormatter.setCalendar(Calendar.getInstance());
            db2Formatter.setCalendar(Calendar.getInstance());


            if(useDB2) {

                Class.forName(jdbcDriverClassName);

		sqlQueryForm = "<form method=\"POST\" action=\"SdQueryServlet?admin=yes&id=query_db2_NOW\">\n"
				+ "Enter SQL Query Here:\n"
				+ "<input type=\"text\" name=\"sql\" size=\"80\" /> <p></p>\n"
				+ "<input type=\"submit\" value=\"Submit Query\" />\n"
				+ "</form>";

  	    }


            if( ! sendMail("SDS@us.ibm.com", "SD servlets Initialization OK on " + hostname, "The SD servlets initialized successfully on " + hostname + "\n"
                + "SdAuthServlet    : " + SdAuthServlet.version      + "\n"
                + "SdDownloadServlet: " + SdDownloadServlet.version  + "\n"
                + "SdMonitor        : " + SdMonitor.version          + "\n"
                + "SdOptions        : " + SdOptions.version          + "\n"
                + "SdQueryServlet   : " + SdQueryServlet.version     + "\n"
                + "SdReader         : " + SdReader.version           + "\n"
                + "SdUtils          : " + SdUtils.version            + "\n"
                + "SdWriter         : " + SdWriter.version           + "\n" ) )
            {
                servletAlert("Servlet Initialization Failed", false);
                print("ERROR! Mail Failure at Servlet Initialization");
                return false;
            }

            servletAlert("Servlet Initialization OK", true);

            initialized = true;

            return true;

        }
        catch(Throwable t) {

	    String impAlert = "The following exception was thrown while initializing SdUtils:\n\n\n" + getStackTrace(t) + "\n";

            Throwable outerExp = t;
            while(outerExp instanceof TransitiveTrustException) {
                Throwable innerExp = ((TransitiveTrustException)outerExp).getCause();
                if(innerExp != null) {
                    impAlert += "\nEmbedded Exception:\n" + getStackTrace(innerExp) + "\n";
                }
                outerExp = innerExp;
            }

            try {
                sendImpAlert(impAlert);
                servletAlert("Servlet Initialization Failed", false);
                return false;
            }
            catch(Throwable t1) {
                t1.printStackTrace();
                return false;
            }

        }

    }



    static synchronized boolean allowNewConnection() {

	if( ! enableBandwidthCutoff )
		return true;

	long now = System.currentTimeMillis();
	if(now < nextBandwidthCheckTime)
	    return allowNewConnection;

	long bandwidthLogTimestamp = new File(bandwidthLog).lastModified();

	if(bandwidthLogTimestamp == bandwidthLogLastModified)
	    return allowNewConnection;

	if(bandwidthLogTimestamp <=0) {
	    allowNewConnection = true;
	    sendImpAlert("bandwidthLog: " + bandwidthLog + ": File not found");
	    bandwidthLogLastModified = bandwidthLogTimestamp;
	    return allowNewConnection;
	}


	String bottleNeckData = executeAndGetStream(bandwidthCmd);
	// "grep \'" + bottleneck + "\' " + bandwidthLog + " | cut -d \':\' -f 6,8"

	int outboundUtil=0, totalUtil=0;

	try {
	    bottleNeckData = bottleNeckData.trim();
	    int index = bottleNeckData.indexOf(':');
	    outboundUtil = Integer.parseInt(bottleNeckData.substring(0, index));
	    totalUtil = Integer.parseInt(bottleNeckData.substring(index+1));
	}
	catch(Exception e) {
            sendImpAlert("thrown while parsing: " + bottleNeckData + "\n\n" + getStackTrace(e));
	    return true;
	}

	if(outboundUtil < 0 || outboundUtil > 100 || totalUtil < 0 || totalUtil > 200) {
	    sendImpAlert("bandwidthLog: " + bandwidthLog + " has weird data: " + bottleNeckData + ". Allowing connection.");
	    return true;
	}

	if(outboundUtil < outboundCutoff && totalUtil < totalCutoff) {
	    allowNewConnection = true;
	}
	else {
	    allowNewConnection = false;
	    sendImpAlert("Not allowing connection. Current Bandwidth utilization: " + bottleNeckData);
	}

	bandwidthLogLastModified = bandwidthLogTimestamp;
	nextBandwidthCheckTime = bandwidthLogLastModified + bandwidthLogRefreshTime;

	return allowNewConnection;

    }




   static void log(String[] userInfo, String fileOwner, String remoteAddr, String userAgent, String id, String fileName, String orderID, String num, String numFiles, long length, long totalBytesRead, long skipBytes, long xferRate, long readRate, long writeRate, long startXfer, long endXfer, String isDSClient, int numBlocks, int blockSize, int maxBlockReadTime, long totalWaitTime, boolean debugMode, String debugStr) {//new for 5.4.1:fileName


		    String hasCompleted = "N";
		    String user = userInfo[0];
		    String userSum = userInfo[4];

                    if(totalBytesRead == (length - skipBytes)) {

                        hasCompleted = "Y";

                        print(user + " (IP: " + remoteAddr + ") successfully downloaded file ID: " + id + " (" + num + "/" + numFiles + " of order#: " + orderID + ") at " + xferRate + " KiloBytes/Second\n"
                              + "Transferred " + totalBytesRead + " bytes @ Read Rate: " + readRate + "   Write rate: " + writeRate + "   TBW: " + maxBlockReadTime + " ms (" + totalWaitTime + ") (user-agent: " + userAgent + ") " + isDSClient + " " + skipBytes + " " + numBlocks + " " + blockSize + " " + userSum
                              + "\nStart time: " + new Date(startXfer) + "     End Time: " + new Date(endXfer), false);

                        if(debugMode)
                            System.err.println(new Date() + " " + numBlocks + " " + blockSize + " > " + debugStr + "\n");
                    }

                    else {
                        SdUtils.print(user + " (IP: " + remoteAddr + ") downloaded " + totalBytesRead + " out of " + (length - skipBytes) + " bytes of file ID: " + id + " (" + num + "/" + numFiles + " of order#: " + orderID + ") at " + xferRate + " KiloBytes/Second\n"
                              + "Read Rate: " + readRate + "   Write rate: " + writeRate + "   MaxTBW: " + maxBlockReadTime + " ms (" + totalWaitTime + ") (user-agent: " + userAgent + ") " + isDSClient + " " + skipBytes + " " + numBlocks + " " + blockSize + " " + userSum
                              + "\nStart time: " + new Date(startXfer) + "     End Time: " + new Date(endXfer));

                        if(debugMode)
                            System.err.println(new Date() + " " + numBlocks + " " + blockSize + " > " + debugStr + "\n");

                    }

                    updateDB2(user, remoteAddr, id, fileName, userAgent, orderID, num, numFiles, length, totalBytesRead, skipBytes, xferRate, readRate, writeRate, startXfer, endXfer, hasCompleted, isDSClient, hostname, numBlocks, blockSize, maxBlockReadTime);//new for 5.4.1:fileName

            }





   private static void updateDB2(String IR_USERID, String IP, String FILE_ID, String FILE_NAME, String USER_AGENT, String ORDER_ID, String FILE_NUM, String TOTAL_FILE_NUM, long FILE_SIZE, long BYTES_DOWNLOADED, long BYTES_SKIPPED, long DOWNLOAD_RATE, long READ_RATE, long WRITE_RATE, long START_TIME, long END_TIME, String COMPLETED, String DS_CLIENT, String SERVER, int NUM_BLOCKS, int BLOCK_SIZE, int MAX_BLOCK_READ_TIME) {//new for 5.4.1:FILE_NAME

        if( ! useDB2)
            return;

	if(USER_AGENT.length() > 48)
	    USER_AGENT = USER_AGENT.substring(0, 47);

        StringBuffer sqlbuff = new StringBuffer();

        sqlbuff.append ("INSERT INTO ");
        sqlbuff.append (db2Table);

        sqlbuff.append (" ( FILE_ID, FILE_NAME, START_TIME, END_TIME, IR_USERID, IP, USER_AGENT, ORDER_ID, FILE_NUM, TOTAL_FILE_NUM, FILE_SIZE, BYTES_DOWNLOADED, BYTES_SKIPPED, DOWNLOAD_RATE, READ_RATE, WRITE_RATE, COMPLETED, DS_CLIENT, SERVER, NUM_BLOCKS, BLOCK_SIZE, MAX_BLOCK_RD_TIME ) ");

        sqlbuff.append ("VALUES ( ");

        sqlbuff.append (quoteAndDelimit(FILE_ID));
        sqlbuff.append (quoteAndDelimit(FILE_NAME)); //new for 5.4.1
        sqlbuff.append (quoteAndDelimit(SdUtils.db2Formatter.format(new Date(START_TIME))));
        sqlbuff.append (quoteAndDelimit(SdUtils.db2Formatter.format(new Date(END_TIME))));
        sqlbuff.append (quoteAndDelimit(IR_USERID));
        sqlbuff.append (quoteAndDelimit(IP));

        sqlbuff.append (quoteAndDelimit(USER_AGENT));

        sqlbuff.append (quoteAndDelimit(ORDER_ID));
        sqlbuff.append (delimitStr(FILE_NUM));
        sqlbuff.append (delimitStr(TOTAL_FILE_NUM));
        sqlbuff.append (delimit(FILE_SIZE));
        sqlbuff.append (delimit(BYTES_DOWNLOADED));
        sqlbuff.append (delimit(BYTES_SKIPPED));
        sqlbuff.append (delimit(DOWNLOAD_RATE));
        sqlbuff.append (delimit(READ_RATE));
        sqlbuff.append (delimit(WRITE_RATE));
        sqlbuff.append (quoteAndDelimit(COMPLETED));
        sqlbuff.append (quoteAndDelimit(DS_CLIENT));
        sqlbuff.append (quoteAndDelimit(SERVER));

        sqlbuff.append (delimit(NUM_BLOCKS));
        sqlbuff.append (delimit(BLOCK_SIZE));
        sqlbuff.append (MAX_BLOCK_READ_TIME);


        sqlbuff.append (")");

        try {
            int rowcount = execDB2Update(sqlbuff.toString());
            if(rowcount != 1)
                sendImpAlert("got a rowcount of " + rowcount + " while updating DB2 with the following statement: " + sqlbuff.toString());
        }
        catch(SQLException e) {
            sendImpAlert("thrown while updating DB2 with the following statement: " + sqlbuff.toString() + "\n\n" + getStackTrace(e));
        }catch(oem.edge.datasource.ConnectionFailedException ce) {
            sendImpAlert("thrown while updating DB2 with the following statement: " + sqlbuff.toString() + "\n\n" + getStackTrace(ce));
        }

    }




    private static synchronized int execDB2Update(String sql) throws SQLException, oem.edge.datasource.ConnectionFailedException {

      // Connection con = DriverManager.getConnection(db2Url, db2User, db2Pwd);
        Connection con = DataSourceManager.getConnection("sdds");
        Statement stmt = con.createStatement();
        int rowCount = stmt.executeUpdate(sql);
        stmt.close();
        con.close();
        return rowCount;

    }



    static synchronized String execDB2Query(String sql) {

        if( ! useDB2)
            return "NA";

	try {
          //Connection con = DriverManager.getConnection(db2Url, db2User, db2Pwd);   
            Connection con = DataSourceManager.getConnection("sdds");
            Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);
            String resultStr = formatResultSet(sql, rs);
            stmt.close();
            con.close();
            return resultStr;
	}
	catch(SQLException e) {
	    return getStackTrace(e);
	}catch(oem.edge.datasource.ConnectionFailedException ce) {
           return getStackTrace(ce);
        }

    }


    static String formatResultSet(String sql, ResultSet rs) throws SQLException {

	StringBuffer sb = new StringBuffer(sql + "\n\n");

	ResultSetMetaData rsmd = rs.getMetaData();

        int columnCount = rsmd.getColumnCount();

	int[] columnWidth = new int[columnCount + 1];

	for(int i = 1; i <= columnCount; i++) {

	    columnWidth[i] = rsmd.getColumnDisplaySize(i);

	    String columnName = rsmd.getColumnName(i);

	    if(columnName.length() > columnWidth[i])
		columnWidth[i] = columnName.length();

	    sb.append( getFixedLengthStr(columnName, columnWidth[i]) );

	}

	sb.append("\n\n");

       int count = 0;

	while(rs.next()) {

	    count++;

	    for(int i = 1; i <= columnCount; i++)
		sb.append(getFixedLengthStr(rs.getString(i), columnWidth[i]) );

	    sb.append("\n");

	}

	sb.append("\n\n");

	sb.append(count + " record(s) selected.\n");

	return sb.toString();

    }


    static String getFixedLengthStr(String s, int length) {

        StringBuffer sb = new StringBuffer(s);
        for(int i = s.length(); i <= length; i++)
            sb.append(" ");
        return sb.toString();

    }



    private static String squote(String s) {

        return "\'" + s + "\' ";

    }

    private static String quoteAndDelimit(String s) {

        return "\'" + s + "\' , ";

    }

    private static String delimitStr(String s) {

        return s + " , ";

    }

    private static String delimit(long l) {

        return l + " , ";

    }




    static synchronized void addRunningDownload(SdWriter thread) {

	runningDownloads.add(thread);

    }




    static synchronized void removeRunningDownload(SdWriter thread) {

	runningDownloads.remove(thread);

    }





    static synchronized String getRunningDownloads() {

	StringBuffer str = new StringBuffer("<table border=\"border\">");

	str.append("<tr align=\"center\"><th>");
        str.append("IR ID");
        str.append("</th><th>");
        str.append("Entitlement");
        str.append("</th><th>");
        str.append("Order Number");
        str.append("</th><th>");
        str.append("IP Address");
        str.append("</th><th>");
        str.append("email");
        str.append("</th><th>");
        str.append("Smart Download");
        str.append("</th><th>");
        str.append("File Size (MB)");
        str.append("</th><th>");
        str.append("Download Speed (KB/sec)");
        str.append("</th><th>");
        str.append("Minutes Elapsed");
        str.append("</th><th>");
        str.append("Estimated Minutes Left");
        str.append("</th></tr>");

	SdWriter thread;

	Enumeration list = runningDownloads.elements();

	long currentTime = System.currentTimeMillis();

	long milliSecondsElapsed, secondsElapsed, estimatedSecondsLeft, bytesRemaining, totalBytesRead, downloadSpeed;
	String minutesElapsed, estimatedMinutesLeft;

	while(list.hasMoreElements()) {

	    thread = (SdWriter)list.nextElement();

	    totalBytesRead = thread.totalBytesRead;
	    milliSecondsElapsed = currentTime - thread.startXfer;
	    secondsElapsed = milliSecondsElapsed / 1000;
	    downloadSpeed = totalBytesRead / milliSecondsElapsed;

	    bytesRemaining = thread.length - thread.skipBytes - totalBytesRead;
	    estimatedSecondsLeft = secondsElapsed * bytesRemaining / totalBytesRead;

	    minutesElapsed = formatTime(secondsElapsed);
	    estimatedMinutesLeft = formatTime(estimatedSecondsLeft);

	    str.append("<tr align=\"center\"><td>");
            str.append(thread.userInfo[0]);
            str.append("</td><td>");
            str.append(thread.userInfo[1]);
            str.append("</td><td>");
            str.append(thread.orderID);
            str.append("</td><td>");
            str.append(thread.remoteAddr);
            str.append("</td><td>");
            str.append(thread.email);
            str.append("</td><td>");
            str.append(thread.isDSClient);
            str.append("</td><td>");
            str.append(thread.length/(1024*1024));
            str.append("</td><td>");
            str.append(downloadSpeed);
            str.append("</td><td>");
            str.append(minutesElapsed);
            str.append("</td><td>");
            str.append(estimatedMinutesLeft);
            str.append("</td></tr>");

	}

	str.append("</table>");

	return str.toString();

    }


    private static String formatTime(long seconds) {

	long minutes = seconds / 60;
	seconds %= 60;

	if(seconds < 10)
	    return String.valueOf(minutes) + ":0" + String.valueOf(seconds);
	else
	    return String.valueOf(minutes) + ":" + String.valueOf(seconds);

    }



    static void testSendSso(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       try {

            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");

            String irUser = request.getParameter("user");

            if(irUser == null)
                irUser = "fyuan";

            String redirectServer = request.getParameter("redirectServer");

            if(redirectServer == null)
                redirectServer = "edge.ibm.com";


            UserData userData = new UserData(irUser, "/EdesignAuthServlet.wss");

            if(userData == null) {
                String str = "Null returned by UserData constructor for user: " + irUser;
                response.getWriter().println(str);
                sendImpAlert(str);
                return;
            }


            String encodedUserData = TransitiveTrust.encodeUserData(userData, redirectServer);

            if(encodedUserData == null) {
                String str = "Null returned by TransitiveTrust.encodeUserData for user: " + irUser;
                response.getWriter().println(str);
                sendImpAlert(str);
                return;
            }


            Cookie cookie = new Cookie("test" + ssoCookieName, encodedUserData);

            if(cookie == null) {
                String str = "Null returned by Cookie constructor for CookieName: test" + ssoCookieName + " and encodedUserData: " + encodedUserData;
                response.getWriter().println(str);
                sendImpAlert(str);
                return;
            }

            String redirect = getBaseURL(request) + request.getServletPath() + "?admin=yes&id=get_req_headers_NOW";

            cookie.setDomain(ssoCookieDomain);
            cookie.setPath("/");

            response.addCookie(cookie);
            response.sendRedirect(redirect);

        }
        catch(TransitiveTrustException e) {
            String msg = "TransitiveTrustException:\n\n\n" + getStackTrace(e) + "\n";
            Throwable outerExp = e;
            while(outerExp instanceof TransitiveTrustException) {
                Throwable innerExp = ((TransitiveTrustException)outerExp).getCause();
                if(innerExp != null) {
                    msg += "\nEmbedded Exception:\n" + getStackTrace(innerExp) + "\n";
                }
                outerExp = innerExp;
            }
            response.getWriter().println(msg);
        }
        catch(InterruptedException e) {
            response.getWriter().println("InterruptedException:<br />" + getStackTrace(e));
        }

    }





    static String getBaseURL(HttpServletRequest request) {

        if(bypassDispatcher)
            return request.getScheme() + "://" + hostname + request.getContextPath();
        else
            return request.getScheme() + "://" + request.getServerName() + request.getContextPath();

    }



    static String getSLA() {

        return sla + "</body></html>";

    }

    static String encode(String[] userInfo, int duration) {
       try {
          return cipher.encode(duration, userInfo[0] + "^" +  userInfo[1] + "^" +  userInfo[2] + "^" + userInfo[5]).getExportString();
       } catch(CipherException ce) {
          print("Error generating encoded string!", true);
       }
       return null;
    }


    static String encode(String[] userInfo) {
       try {
          return cipher.encode(TOKEN_DURATION, userInfo[0] + "^" +  userInfo[1] + "^" +  userInfo[2] + "^" + userInfo[5]).getExportString();
       } catch(CipherException ce) {
          print("Error generating encoded string!", true);
       }
       return null;
    }


    static String[] parseUserInfo(String userInfo) {

	try {

	    userInfo += " ";

	    int index1 = userInfo.indexOf('^');
	    int index2 = userInfo.indexOf('^', index1+1);
	    int index3 = userInfo.indexOf('^', index2+1);

	    String user = userInfo.substring(0, index1);
	    String ent = userInfo.substring(index1+1, index2);
	    String order = userInfo.substring(index2+1, index3).trim();
	    String email = userInfo.substring(index3+1).trim();
	    String su = "";

	    for(int i=0; i < SUPER_ENTITLEMENTS.length; i++) {
                if(ent.equals(SUPER_ENTITLEMENTS[i])) {
		    su = "su";
		    break;
		}
	    }

	    String userSum = user + "^" + ent + "^" + email + "^" + su;

	    return new String[] {user, ent, order, su, userSum, email};

        }
	catch(Exception e) {
	    sendImpAlert("thrown parsing: " + userInfo + ".\n" + getStackTrace(e));
	    return new String[] {"", "", "", "", "", ""};
	}


    }




    static String[] checkToken(String token, String sourceServlet) throws DecodeException {

        if(token == null) {
            sendAlert(sourceServlet + ": ERROR! Received null token in checkToken()");
            return null;
        }

        ODCipherData data = cipher.decode(token);

        if( ! data.isCurrent()) {
            sendAlert(sourceServlet + ": ERROR! Received expired token in checkToken(). TokenData: " + data.getString() + " CurrentTime: " + new Date() + " TokenExpired: " + new Date(data.getSecondsSince70() * 1000L) );
            return new String[] {"", "", "", "", "", ""};
        }
        else {
	    return parseUserInfo(data.getString());
        }

    }



    static String[] checkSsoCookie(HttpServletRequest request, long fileID) throws InterruptedException, TransitiveTrustException {

        String encodedUserData = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(ssoCookieName)) {
                    encodedUserData = cookies[i].getValue();
                    break;
                }
            }
        }


        if (encodedUserData == null) {
            encodedUserData = request.getParameter("sso");
            if(encodedUserData != null)
                sendImpAlert("Received TransitiveTrust param for fileID: " + fileID);
        }


        if (encodedUserData == null) {
            sendImpAlert("No TransitiveTrust cookie or parameter for fileID: " + fileID);
            return new String[] {null, "", "", "", "", ""};
        }
        else {

            try {

                UserData userData = TransitiveTrust.decodeUserData(encodedUserData);

                if (userData.isValid()) {
                    return parseUserInfo(userData.getId());
                }
                else {
                    sendAlert("ERROR! Expired TransitiveTrust cookie present for fileID: " + fileID + " user: " + userData.getId());
                    return new String[] {"", "", "", "", "", ""};
                }

            }
            catch(TransitiveTrustException e) {
		String msg = "TransitiveTrustException thrown decoding: <" + encodedUserData + ">" + ":\n\n\n" + getStackTrace(e) + "\n";
		Throwable outerExp = e;
		while(outerExp instanceof TransitiveTrustException) {
		    Throwable innerExp = ((TransitiveTrustException)outerExp).getCause();
		    if(innerExp != null) {
		        msg += "\nEmbedded Exception:\n" + getStackTrace(innerExp) + "\n";
		    }
		    outerExp = innerExp;
		}

                sendImpAlert(msg);
                throw e;
            }
            catch(InterruptedException e) {
                sendImpAlert("InterruptedException thrown decoding: <" + encodedUserData + ">");
                throw e;
            }

        }

    }


    static boolean checkCRC(String filePath, int crc, long offset, long length) throws IOException {

        if( ! new File(filePath).isFile())
            throw new IOException(filePath + ": file not found while checking CRC");

        byte[] arr = null;

        FileInputStream in = new FileInputStream(filePath);

        long readTime;
        // MPZ BEGIN
        long checkTime = 0;
        int actualCrc = 0;
        // MPZ END

        try {

            long actuallySkipped = in.skip(offset);

            if(actuallySkipped != offset)
                throw new IOException("Tried to skip " + offset + " bytes. Could only skip " + actuallySkipped + " bytes");

            // MPZ BEGIN
            // arr = new byte[ (int) length ];
            arr = new byte[4096];

            long startRead = System.currentTimeMillis();
            // int bytesRead = in.read(arr);
            int bytesRead = 0;
            ODCrc checker = new ODCrc();
            checker.resetCRC();

            int read = (int) Math.min(4096,length - bytesRead);
            while (read > 0 && (read = in.read(arr,0,read)) > 0) {
                long startCheck = System.currentTimeMillis();
                checker.generateCRC(arr,0,read);
                checkTime += System.currentTimeMillis() - startCheck;
                bytesRead += read;
                read = (int) Math.min(4096,length - bytesRead);
            }

            // readTime = System.currentTimeMillis() - startRead;
            readTime = System.currentTimeMillis() - startRead - checkTime;

            actualCrc = checker.getCRC();
            // MPZ END

            if(bytesRead != length)
                throw new IOException("Tried to read " + length + " bytes. Could: only read " + bytesRead + " bytes");

        }
        finally {
            in.close();
        }

        // MPZ BEGIN
        /*
        long startCheck = System.currentTimeMillis();

        ODCrc checker = new ODCrc();
        checker.generateCRC(arr);
        int actualCrc = checker.getCRC();

        long checkTime = System.currentTimeMillis() - startCheck;
        */
        // MPZ END

        print("Checked " + length + " bytes " + "   readTime: " + readTime + " ms   checkTime: " + checkTime + " ms", false);

        if(actualCrc != crc)
            print("File: " + filePath + ", offset: " + offset + ", length: " + length + ", actualCrc: " + actualCrc + ", clientCrc: " + crc);

        return (actualCrc == crc);

    }



/*
    static boolean checkCRC(String filePath, int crc, long offset, long length) throws IOException {

        if( ! new File(filePath).isFile())
            throw new IOException(filePath + ": file not found while checking CRC");

        byte[] arr = null;

        FileInputStream in = new FileInputStream(filePath);

        long readTime;

        try {

            long actuallySkipped = in.skip(offset);

            if(actuallySkipped != offset)
                throw new IOException("Tried to skip " + offset + " bytes. Could only skip " + actuallySkipped + " bytes");

            arr = new byte[ (int) length ];

            long startRead = System.currentTimeMillis();
            int bytesRead = in.read(arr);
            readTime = System.currentTimeMillis() - startRead;

            if(bytesRead != length)
                throw new IOException("Tried to read " + length + " bytes. Could: only read " + bytesRead + " bytes");

        }
        finally {
            in.close();
        }

        long startCheck = System.currentTimeMillis();

        ODCrc checker = new ODCrc();
        checker.generateCRC(arr);
        int actualCrc = checker.getCRC();

        long checkTime = System.currentTimeMillis() - startCheck;

        print("Checked " + length + " bytes " + "   readTime: " + readTime + " ms   checkTime: " + checkTime + " ms", false);

        if(actualCrc != crc)
            print("File: " + filePath + ", offset: " + offset + ", length: " + length + ", actualCrc: " + actualCrc + ", clientCrc: " + crc);

        return (actualCrc == crc);

    }
*/


    static String getJavascriptLaunchURL(String url, String name) {

        return getJavascriptLaunchURL(url, name, 575, 436);

    }



    static String getJavascriptLaunchURL(String url, String name, int height, int width) {

        name = name.replace(' ', '_');
        return
            " href=\"javascript:;\"" +
            " onclick=\"launch(\'" + url + "\', \'" + name + "\', \'" + height + "\', \'" + width + "\')\"" +
            " onkeypress=\"launch(\'" + url + "\', \'" + name + "\', \'" + height + "\', \'" + width + "\')\"";

    }



    static String getJavascriptLaunchCode() {

        return
            "<script language=\"javascript1.1\" type=\"text/javascript\">\n" +
            "<!--\n" +
            "function launch(url, name, height, width) {\n" +
            "open(" +
            "url" +
            ", " +
            "name" +
            ", \"height=\" + height + \",width=\" + width + \",menubar=no,resizable=yes,status=no,toolbar=no,scrollbars=yes\");\n" +
            "}\n// -->\n</script>\n";

    }


    static String getPrimaryButton(String name, String altName) {

        return "<img src=\"//www.ibm.com/i/v11/buttons/" + name + ".gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"" + altName + "\" />";

    }



    static String getSecondaryButton(String name, String altName) {

        return "<img src=\"//www.ibm.com/i/v11/buttons/" + name + ".gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"" + altName + "\" />";

    }



    static boolean checkAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

         if(authorization == null) {
            sendUnauthorized(request, response, AUTH_NULL);
            return false;
        }

/*
        String[] userAndPassword = decodeAuth(authorization);
        String user = userAndPassword[0];
        String password = userAndPassword[1];

	 if ( ! new EDCMafsFile().afsVerifyAuth(afsCell, user, password) ) {
	if ( ! verifyKlog(afsCell, user, password) ) {
            sendUnauthorized(request, response, AUTH_FAILED);
	    return false;
            }



        if( ! isAdmin(user) ) {
            sendUnauthorized(request, response, NOT_ADMIN);
	    return false;
            }*/


        return true;

    }



    static void handleAdminRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

       if( ! checkAdmin(request, response) )
            return;
      
        String id = request.getParameter("id");

        if(id == null) {
            sendForbidden(request, response, ID_NULL);
            return;
        }


        if(id.equals("sendSsoCookie")) {
            testSendSso(request, response);
            return;
        }
        else if(id.equals("dce_monitor_test")) {

            String str = "";

            if (sendMail("SDS@us.ibm.com", "Please Ignore: dce_monitor test from " + hostname, "This is a dce_monitor test from " + hostname + ". Please ignore"))
                str += "Mail OK ... Sent dce_monitor Mail_Up Alert\n";
            else
                str += "Mail Failure ... Sent dce_monitor Mail_Down Alert\n";


           /*  if(authenticate()) {
                authenticationAlert("Authentication OK", true);
                str += "Servlet Authentication OK ... Sent dce_monitor Servlet_Up Alert\n";
            }
            else {
                str += "Servlet Authentication Failure ... Sent dce_monitor Servlet_Down Alert\n";
                }*/

            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;

        }
        else if(id.equals("get_out_log_NOW")) {
            String str = readLogs(outFile);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_err_log_NOW")) {
            String str = readLogs(errFile);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_std_out_NOW")) {
            String str = readLogs(stdOutFile);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_std_err_NOW")) {
            String str = readLogs(stdErrFile);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_user_info_NOW")) {
            String u = request.getParameter("user");
            String userInfo = "NO INFO AVAILABLE";

            if(u == null)
                 userInfo = SdUtils.getAllUserInfo();
            else
                 userInfo = SdUtils.getUserInfo(u, null);

            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(userInfo);
            return;
        }
        else if(id.equals("get_sql_form_NOW")) {
            String str = formatResponse("SQL Query Form", "SQL Query Form", sqlQueryForm, request);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("query_db2_NOW")) {
	    String sql = request.getParameter("sql");
            String str = execDB2Query(sql);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_props_file_NOW")) {
            String str = readLogs(propsFilePath);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("re_initialize_NOW")) {
            String str = null;
            initialized = false;
            nextAuthenticate = 0;
            if(initialize(propsFilePath))
                str = "Re-Initialized successfully on " + hostname;
            else
                str = "Failed to re-initialize on " + hostname + ". Servlet is Down!!!";
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_running_downloads")) {
            String str = getRunningDownloads();
            str = formatResponse("Running Downloads on " + shortHostname, "Running Downloads on " + shortHostname, str, request);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_init_params_NOW")) {
            String str
                = "table_path       : " + tablePath                  + "\n"
                + "sla_path         : " + slaPath                    + "\n"
                + "cipher_path      : " + cipherPath                 + "\n"
                + "db2Pwd           : " + db2Pwd                     + "\n"

              // + "afs_cell         : " + afsCell                    + "\n"
              //  + "afs_user         : " + uid                        + "\n"

                + "useDB2           : " + useDB2                     + "\n"
                + "jdbcDriverClass  : " + jdbcDriverClassName        + "\n"
                + "db2Url           : " + db2Url                     + "\n"
                + "db2User          : " + db2User                    + "\n"
                + "db2Table         : " + db2Table                   + "\n"

                + "out_log          : " + outFile                    + "\n"
                + "err_log          : " + errFile                    + "\n"
                + "std_out          : " + stdOutFile                 + "\n"
                + "std_err          : " + stdErrFile                 + "\n"

                + "mail_host_1      : " + mailHost1                  + "\n"
                + "mail_host_2      : " + mailHost2                  + "\n"
                + "mail_to          : " + mailTo                     + "\n"
                + "mail_cc          : " + mailCc                     + "\n"

                + "dce_monitor      : " + dce_monitor                + "\n"
                + "admins           : " + adminStr                   + "\n"

                + "applet_code      : " + appletCode                 + "\n"
                + "applet_codebase  : " + appletCodebase             + "\n"
                + "applet_archive   : " + appletArchive              + "\n"
                + "applet_cabbase   : " + appletCabbase              + "\n"
                + "helper_install   : " + helperInstall              + "\n"

		+ "enableCutoff     : " + enableBandwidthCutoff      + "\n"
		+ "bandwidthLog     : " + bandwidthLog               + "\n"
		+ "bottleneck       : " + bottleneck                 + "\n"
		+ "outboundCutoff   : " + outboundCutoff             + "\n"
		+ "totalCutoff      : " + totalCutoff                + "\n"
		+ "allowNewConn     : " + allowNewConnection         + "\n"

                + "blockSizeKB      : " + blockSizeInKB              + "\n"
                + "numBlocks        : " + numBlocks                  + "\n"
                + "useMultipleThreads: " + useMultipleThreads        + "\n"
                + "bypassDispatcher : " + bypassDispatcher           + "\n"
                                                                     + "\n\n\n"
                + "SdAuthServlet    : " + SdAuthServlet.version      + "\n"
                + "SdDownloadServlet: " + SdDownloadServlet.version  + "\n"
                + "SdMonitor        : " + SdMonitor.version          + "\n"
                + "SdOptions        : " + SdOptions.version          + "\n"
                + "SdQueryServlet   : " + SdQueryServlet.version     + "\n"
                + "SdReader         : " + SdReader.version           + "\n"
                + "SdUtils          : " + SdUtils.version            + "\n"
                + "SdWriter         : " + SdWriter.version           + "\n"
                + "propsFilePath    : " + propsFilePath              + "\n"
                + "ssoCookieName    : " + ssoCookieName              + "\n"
                + "ssoCookieDomain  : " + ssoCookieDomain            + "\n"
                + "hostname         : " + hostname                   + "\n"
                + "initialized      : " + initialized                + "\n"
                + "authOK           : " + authOK                     + "\n"
                + "mailOK           : " + mailOK                     + "\n"
                + "ordersLastParsed : " + new Date(ordersLastParsed) + "\n"
                + "nextAuthenticate : " + new Date(nextAuthenticate) + "\n"
            ;

            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else if(id.equals("get_req_headers_NOW")) {


            String str = "";

            java.util.Enumeration e = request.getParameterNames();
            while(e.hasMoreElements()) {
                String s = (String)e.nextElement();
                str += "<strong>Parameter Name: </strong>" + s + "<strong> Value: </strong>" + request.getParameter(s) + ";<br />\n";
            }

            str += "<br /> <br />";

            Cookie[] c = request.getCookies();
            for(int i = 0; i < c.length; i++) {
                str += "<strong>Cookie Name: </strong>" + c[i].getName() + "<strong> Value: </strong>" + c[i].getValue() + "<strong> Domain: </strong>" + c[i].getDomain() + "<strong> Path: </strong>" + c[i].getPath() + "<strong> MaxAge: </strong>" + c[i].getMaxAge() + ";<br />\n";
            }

            str += "<br /> <br />";

            str
                += "<strong>Actual Server: </strong>"
                + hostname + "<br />\n"

                + "<strong>Remote Host: </strong>"
                + request.getRemoteHost() + "<br />\n"

                + "<strong>Remote Addr: </strong>"
                + request.getRemoteAddr() + "<br />\n"

                + "<strong>Remote User: </strong>"
                + request.getRemoteUser() + "<br />\n"

                + "<strong>Server Name: </strong>"
                + request.getServerName() + "<br />\n"

                + "<strong>Server Port: </strong>"
                + request.getServerPort() + "<br />\n"

                + "<strong>Protocol: </strong>"
                + request.getProtocol() + "<br />\n"

                + "<strong>Character Encoding: </strong>"
                + request.getCharacterEncoding() + "<br />\n"

                + "<strong>Content Type: </strong>"
                + request.getContentType() + "<br />\n"

                + "<strong>Request URI: </strong>"
                + request.getRequestURI() + "<br />\n"

                + "<strong>Context Path: </strong>"
                + request.getContextPath() + "<br />\n"

                + "<strong>Servlet Path: </strong>"
                + request.getServletPath() + "<br />\n"

                + "<strong>Path Info: </strong>"
                + request.getPathInfo() + "<br />\n"

                + "<strong>Path Translated: </strong>"
                + request.getPathTranslated() + "<br />\n"

                + "<strong>Query String: </strong>"
                + request.getQueryString() + "<br />\n"

                + "<br /> <br />\n"
                + "<table>\n"
                + "<tr>\n"
                + "<th>Header Name</th><th>Header Value</th></tr>\n";

            Enumeration headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                str
                    += "<tr><td>" + headerName + "</td>"
                    + "<td>" + request.getHeader(headerName) + "</td></tr>";
            }

            str += "</table>";

            str = formatResponse("Request Headers", "Request Headers", str, request);

            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(str);
            return;
        }
        else {
            sendForbidden(request, response, ID_NOT_FOUND);
            return;
        }

    }




    static void returnRemoteHost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String remoteAddr = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();

	String message = "\n\nIf the above is not your IP address, you are probably using a proxy or NAT device to access this site.";

	String responseStr = null;

	if(remoteAddr == null)
	    responseStr = "Unable to detect your hostname";
	else if(remoteHost == null || remoteAddr.equals(remoteHost))
	    responseStr = remoteAddr + message;
	else
	    responseStr = remoteAddr + " (" + remoteHost + ")" + message;


        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(responseStr);
        return;

    }




    private static String[] decodeAuth(String authorization) {

	try {
	    String str = authorization.substring(6).trim();
	    String nameAndPassword = new String(new BASE64Decoder().decodeBuffer(str));
	    int index = nameAndPassword.indexOf(":");
            String[] arr = new String[2];
	    arr[0] = nameAndPassword.substring(0, index);
	    arr[1] = nameAndPassword.substring(index+1);
	    return arr;
	}
	catch(Exception e) {
            print("ERROR! The following exception was while decoding : " + authorization + "\nStackTrace:\n" + getStackTrace(e));
	    return new String[] {"", ""};
	}

    }




  /* static synchronized boolean authenticate() {

        if(System.currentTimeMillis() > nextAuthenticate) {
            if(executeKlog(afsCell, uid, pwd)) {
                authenticationAlert("Authentication OK", true);
                nextAuthenticate = System.currentTimeMillis() + AUTH_DURATION;
                authOK = true;
            }
            else {
		String newPwd = PasswordUtils.getPassword(pwdPath);
                if(executeKlog(afsCell, uid, newPwd)) {
                    authenticationAlert("Authentication OK", true);
                    nextAuthenticate = System.currentTimeMillis() + AUTH_DURATION;
                    authOK = true;
		    pwd = newPwd;
                }
		else {
                    authOK = false;
		}
	    }
        }
        else
            authOK = true;

        if( ! authOK ) {
            initialized = false;
            authenticationAlert("Authentication Failure", false);
        }

        return authOK;

        }*/


    private static boolean executeKlog(String cell, String username, String password) {

        String inString = "";
        String errString = "";
        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;

        String cmd = "/usr/afsws/bin/klog -principal " + username + " -cell " + cell + " -pipe";

        try {
            Process p = Runtime.getRuntime().exec(cmd);

            try {
                BufferedOutputStream out = new BufferedOutputStream(p.getOutputStream());
                out.write(password.getBytes());
                out.close();

                exitValue = p.waitFor();

                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
                int read = 0;
                while(read >= 0) {
                    inString += new String(arr, 0, read);
                    read = in.read(arr, 0, arrSize);
                }
                read = 0;
                while(read >= 0) {
                    errString += new String(arr, 0, read);
                    read = err.read(arr, 0, arrSize);
                }
                in.close();
                err.close();
            }
            catch(Throwable t1) {
                print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t1));
            }



            String str
                = cmd + " returned an exit Value of " + exitValue + "\n"
                + "stdout: " + inString + "\n"
                + "stderr: " + errString + "\n";

            if(exitValue != 0)
                print(str);

        }
        catch(Throwable t) {
            print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t));
        }

	if(exitValue == 0)
	    return true;
	else
	    return false;

    }



    private static boolean verifyKlog(String cell, String username, String password) {

        String inString = "";
        String errString = "";
        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;

        String cmd =
                  "/usr/afsws/bin/pagsh -c /bin/ksh << 'EOF' \n"
                + "/usr/afsws/bin/klog -principal " + username + " -cell " + cell + " -password " + password + "\n"
                + "EOF\n";

        String[] command = {"/bin/ksh", "-c", cmd};


        try {
            Process p = Runtime.getRuntime().exec(command);

            try {
                exitValue = p.waitFor();

                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
                int read = 0;
                while(read >= 0) {
                    inString += new String(arr, 0, read);
                    read = in.read(arr, 0, arrSize);
                }
                read = 0;
                while(read >= 0) {
                    errString += new String(arr, 0, read);
                    read = err.read(arr, 0, arrSize);
                }
                in.close();
                err.close();
            }
            catch(Throwable t1) {
                print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t1));
            }


            String str
                = cmd + " returned an exit Value of " + exitValue + "\n"
                + "stdout: " + inString + "\n"
                + "stderr: " + errString + "\n";

            if(exitValue != 0)
                print(str);

        }
        catch(Throwable t) {
            print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t));
        }

	if(exitValue == 0)
	    return true;
	else
	    return false;

    }



    static void sendUnauthorized(HttpServletRequest request, HttpServletResponse response, int code) throws IOException {

        String message;

        if(code == AUTH_NULL)
            message = "Sorry, you have to enter a valid username/password combination to access this order.\n";

        else if(code == AUTH_FAILED)
            message
                = "Sorry, the username/password combination you entered is invalid for this order.<br />\n"
                + "Remember that both your username and password are case-sensitive.\n";

        else if(code == NOT_ADMIN)
            message
                = "Sorry, you are not authorized to access this information.\n";

        else {
            print("ERROR! Invalid Code: " + code + " for sendUnauthorized()");
            sendServiceUnavailable(request, response, INVALID_CODE_FOR_METHOD);
            return;
        }


        message
            += "<br />Please try again.\n"
            + "<br /> <br />Thank you,<br />\n"
            + "<a href=\"" + loginServlet + "\">ASIC Connect</a>";


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setHeader("WWW-Authenticate", "BASIC realm=\"IBM Customer Connect\"");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println(formatResponse("401 Unauthorized", message, request));

    }




    static void sendForbidden(HttpServletRequest request, HttpServletResponse response, int code) throws IOException {

        sendForbidden(request, response, code, 0L);

    }



    static void sendForbidden(HttpServletRequest request, HttpServletResponse response, int code, long expired) throws IOException {

        String message;

        if(code == TOKEN_EXPIRED) {
            message = "Sorry, your current session has expired. Please log in again.\n";
        }


        else if(code == SSO_EXPIRED) {
            message = "Sorry, your session has expired. Please log in again.\n";
        }


        else if(code == AUTH_NULL) {
            message = "Sorry, you are not authorized to access this site.\n";
        }


        else if(code == AUTH_FAILED) {
            message = "Sorry, you are not authorized to access this order.\n";
        }


        else if(code == USER_MISMATCH) {
            message = "Sorry, you are not authorized to download this order.\n";
        }


        else if(code == ID_NOT_FOUND) {
            message = "Please check the URL you entered.<br />\n"
                    + "Your order may have expired.\n";
        }


        else if(code == ORDER_EXPIRED) {

            String expiredStr = customerFormatter.format(new Date(expired));

            message
                = "Sorry, this order expired on " + expiredStr + "<br />\n"
                + "Please place your order again at the IBM Customer Connect website.\n";
        }


        else if(code == MUST_ACCEPT_SLA) {
            message = "Sorry, you must accept the license agreement to download this order.\n";
        }



        else if(code == INVALID_CRC) {
            message = "The partial file you requested is either corrupted or has changed on our server.<br />\n"
                    + "Please download the entire file again.";
        }


        else if(code == ID_NULL) {
            message = "Please check the URL you entered.\n";
        }


        else if(code == ID_NOT_PARSABLE) {
            message = "Please check the url you entered.\n";
        }

        else {
            print("ERROR! Invalid Code: " + code + " for sendForbidden()");
            sendServiceUnavailable(request, response, INVALID_CODE_FOR_METHOD);
            return;
        }


        message
            += "<br />If you have any questions, please contact <em><a href=\"mailto:" + helpDesk + "\">" + helpDesk + "</a></em>\n"
            + "<br /> <br />Thank you,<br />\n"
            + "<a href=\"" + loginServlet + "\">ASIC Connect</a>";


        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(formatResponse("403 Forbidden", message, request));

    }






    static void sendServiceUnavailable(HttpServletRequest request, HttpServletResponse response, int code) throws IOException {


        String date = formatter.format(new Date());

        String str = "";
        String mailStr = "Internal Server Error: ";


        if(code == INTERNAL_AUTH_FAILURE) {
            str = "AUTH";
            mailStr += "Servlet Authentication Failure";
        }
        else if(code == REQUIRED_FIELD_MISSING) {
            str = "RFM";
            mailStr += "Required Table Field Missing";
        }

        else if(code == FILE_NOT_FOUND) {
            str = "FNF";
            mailStr += "File Not Found";
        }

	else if(code == SERVER_BUSY) {
	    str = "SB";
	}

        else if(code == NULL_INPUT_STREAM) {
            str = "NIS";
            mailStr += "Null Input Stream";
        }

        else if(code == NULL_OUTPUT_STREAM) {
            str = "NOS";
            mailStr += "Null Output Stream";
        }

        else if(code == EXCEPTION) {
            str = "EXP";
            mailStr += "Unexpected Exception";
        }

        else if(code == INIT_FAILED) {
            str = "INIT";
            mailStr += "Initialization Failed";
        }

        else if(code == NULL_LOGON_CONTEXT) {
            str = "NLC";
            mailStr += "Null Logon Context";
        }

        else if(code == INVALID_LOGON_CONTEXT) {
            str = "ILC";
            mailStr += "Invlid Logon Context";
        }

        else if(code == INVALID_CODE_FOR_METHOD) {
            str = "IC";
            mailStr += "Invalid Code For Method";
        }

        else {
            print("ERROR! Invalid Code: " + code + " for sendServiceUnavailable()");
            sendServiceUnavailable(request, response, INVALID_CODE_FOR_METHOD);
            return;
        }


        String message = null;

	if(code == SERVER_BUSY)
	    message
	    = "Our apologies, due to extremely high network traffic, we cannot deliver your file at the current time.<br />\n"
	    + "Please try again later.\n"
            + "<br /> <br />Thank you for your patience,<br />\n"
            + "<a href=\"" + loginServlet + "\">ASIC Connect</a>";

	else
	    message
            = "Our apologies, we are unable to complete your request at the present time.<br />\n"
            + "Please report this error to <em><a href=\"mailto:" + helpDesk + "\">" + helpDesk + "</a></em> with the id: " + str + "-SoftwareDownload-" + date + "\n"
            + "<br /> <br />Thank you,<br />\n"
            + "<a href=\"" + loginServlet + "\">ASIC Connect</a>";


        if(response.isCommitted()) {
            print("Tried to set SC 503 (Code: " + code + ") for already committed response");
        }
        else {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(formatResponse("503 Service Unavailable", message, request));
        }

	if(code != SERVER_BUSY)
            sendErrorMail(mailStr);

    }




    private static String getReturnLink(HttpServletRequest request) {

        String returnLink = null;
        Enumeration e = request.getHeaders("Referer");

        while(e != null && e.hasMoreElements()) {

            returnLink = (String)e.nextElement();

            if(returnLink != null && returnLink.indexOf("/oem/edge/EdesignToolkitOrderServlet.wss") >= 0) {

                if(returnLink.indexOf("EdesignToolkitOrderServlet.wss" + "?") < 0) {
                    int index = returnLink.indexOf("EdesignToolkitOrderServlet.wss");
                    returnLink = returnLink.substring(0, index) + "EdesignInboxServlet.wss?sub_func=account";
                }

                returnLink = "<br /> <br />\n"
                           + "<a href=" + returnLink + ">Return to my IBM Customer Connect Inbox</a>\n"
                           + "<br /> <br />\n";

                return returnLink;

            }

        }

        return "";

    }




    static void sendSLA(HttpServletRequest request, HttpServletResponse response, String requestURI, String id) throws IOException {

        requestURI += "?id=" + id + "&amp;acceptSLA=";
// requestURI += "?id=" + id + "&amp;token=" + encode(userInfo) + "&amp;acceptSLA=";

//        String submitURI = getJavascriptLaunchURL(requestURI + "ACCEPT", "Download") + "; self.close()";
//        String cancelURI = getJavascriptLaunchURL(requestURI + "CANCEL", "Forbidden") + "; self.close()";


        String submitURI = requestURI + "ACCEPT";
        String cancelURI = requestURI + "CANCEL";


        String acceptStr =
            "<table cellspacing=\"50\" align=\"center\"><tr>\n" +
            "<td><a href=\"" + submitURI + "\">" + getPrimaryButton("submit", "Submit") + "</a></td>\n" +
            "<td><a href=\"" + cancelURI + "\">" + getPrimaryButton("cancel", "Cancel") + "</a></td>\n" +
            "</tr></table>\n";


        String str =
            sla +
            getJavascriptLaunchCode() +
            acceptStr +
            "</body></html>";


        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(str);
        return;

    }



    static boolean mailUser(String email, String filename, String orderNo, long length, String queryString) {

        String fileString = null;

        if(filename == null || filename.trim().length() == 0) {
            fileString = " ";
        }
        else {
            filename = filename.substring(filename.indexOf('/') + 1);
            fileString = " (" + filename + ") ";
        }

        String URL = bcoUrlPrefix + queryString;

        StringBuffer s = new StringBuffer();

        s.append("Dear ASIC Connect Customer,\n");
        s.append("\n");

        s.append("We understand that download of your ASIC Connect order # " + orderNo + " \n");
        s.append("may not have completed today.\n");
        s.append("\n");

        s.append("If you have successfully downloaded this file" + fileString + "and it is of the \n");
        s.append("correct size (" + length + " bytes), please disregard this email.\n");
        s.append("\n");

        s.append("For quality assurance purposes, we'd like to better understand why a customer's \n");
        s.append("download does not complete.  Please reply to this note indicating with an 'x' \n");
        s.append("one or more of the following reasons that may apply: \n");
        s.append("\n");

        s.append("____ I interrupted or cancelled the download. \n");
        s.append("____ The download timed out before completing. \n");
        s.append("____ I experienced computer or network problems at my location at the time. \n");
        s.append("____ I have no idea why the download did not complete. \n");
        s.append("____ I hadn't detected any interruption, so I will re-attempt to download. \n");
        s.append("____ Other: ____________________________ \n");
        s.append("\n");

        s.append("If necessary, you can re-attempt to download this file by clicking on the link below:\n");
        s.append(URL + "\n");
        s.append("\n");

        s.append("We apologize for the inconvenience and thank you for your patience.\n");
        s.append("\n");

        s.append("ASIC Connect\n");
        s.append("\n");

        s.append("US and Canada 1.888.220.3343\n");
        s.append("Other geographies 1.507.253.6446\n");


        String subject = "Re: Your ASIC Connect order # " + orderNo;

        return sendMail("IBM Customer Connect <eConnect@us.ibm.com>", email, null, subject, s.toString());

    }



    private static boolean sendErrorMail(String message) {

        String from = "SDS_ERROR@us.ibm.com";

	String shortMsg = message;

	int shortMsgLen = 50;

	if(message.length() > shortMsgLen)
	    shortMsg = message.substring(0, shortMsgLen);

        String subject = "SD err on " + shortHostname + "-" + shortMsg;

        String body
            = "There might be a problem with the oem.edge.ed.sd.SdUtils servlet on " + hostname + "\n"
            + "Please report this error to the contact person for this servlet with the message - " + message
            + "\n\nThank you.";

        return sendMail(from, subject, body);

    }



    private static boolean sendMail(String from, String subject, String body) {

        return sendMail(from, SdUtils.mailTo, SdUtils.mailCc, subject, body);

    }



    private static boolean sendMail(String from, String to, String cc, String subject, String body) {

        long sleepTime = 5*1000;

        int numRetries = 2;

        String[] emailHost;

        if(mailHost2 != null)
            emailHost = new String[] {mailHost1, mailHost2};
        else
            emailHost = new String[] {mailHost1};

        int numHosts = emailHost.length;

        for(int i = 0; i <= numRetries; i++) {

            for(int j = 0; j < numHosts; j++) {

                try {
                    Mailer.sendMail(emailHost[j], from, to, cc, null, null, subject, body);
                    mailAlert("Mail OK", true);
                    mailOK = true;
                    return true;
                }
                catch(Throwable t) {
                    String str =
                        "ERROR! thrown while trying to send email (attempt# "
                        + ( (i*numHosts) + j + 1 )
                        + ")\n"
                        + "StackTrace:\n" + getStackTrace(t) + "\n\n"
                        + "Will Re-try " + ( (numRetries - i) * numHosts + (numHosts - 1 - j) ) + " times";
                    print(str);
                }
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch(Throwable t) {
                String str =
                    "ERROR! thrown while WAITING to re-send email\n"
                    + "StackTrace:\n" + getStackTrace(t);
                print(str);
            }
        }

        mailAlert("Mail Failure", false);
        mailOK = false;

        String str
            = "ERROR! This email could NOT be sent despite "
            + ( (numRetries + 1) * numHosts)
            + " attempts";
        print(str);

        return false;

    }







    private static String formatResponse(String title, String message, HttpServletRequest request) {
        return formatResponse(title, "ASIC Connect", message, request);
    }




    private static String formatResponse(String title, String header, String message, HttpServletRequest request) {

        return
              SdOptions.ibmDocType
            + "\n<head>\n<title>"
            + title
            + "</title>\n"
            + SdOptions.ibmHeader
            + "\n</head>\n\n<body>\n"
            + SdOptions.ibmTopBar
            + "\n<h2>"
            + header
            + "</h2>\n"
            + "<div>\n"
            + message
            + "\n</div>\n"
            + "</body>\n</html>\n";

    }



    static String getFileName(long fileId) throws IOException {

        Hashtable fileEntry = getFileEntry(fileId);

        if(fileEntry == null)
            return null;
        else
            return (String)fileEntry.get(FILE_NAME);

    }



    static String getFileOwner(long fileId) throws IOException {

        Hashtable fileEntry = getFileEntry(fileId);

        if(fileEntry == null)
            return null;
        else
            return (String)fileEntry.get(USER_ID);

    }


    static String getFileOrder(long fileId) throws IOException {

        Hashtable fileEntry = getFileEntry(fileId);

        if(fileEntry == null)
            return null;
        else
            return (String)fileEntry.get(ORDER_ID);

    }




    static HashSet getUserEntry(String userId) throws IOException {

        checkFiles();
        return (HashSet)usersTable.get(userId);

    }



    static Hashtable getFileEntry(long fileId) throws IOException {

        Long fid = new Long(fileId);

        Hashtable fileEntry = (Hashtable)filesTable.get(fid);

        if(fileEntry == null) {
            checkFiles();
            fileEntry = (Hashtable)filesTable.get(fid);
        }

        return fileEntry;

    }


    static Hashtable getOrderEntry(String orderID) throws IOException {

        Hashtable orderEntry = (Hashtable)ordersTable.get(orderID);

        if(orderEntry == null) {
            checkFiles();
            orderEntry = (Hashtable)ordersTable.get(orderID);
        }

        return orderEntry;

    }



    static Hashtable getOrderEntry(long fileId) throws IOException {

        Hashtable fileEntry = getFileEntry(fileId);

        if(fileEntry == null)
            return null;
        else {

            String orderId = (String)fileEntry.get(ORDER_ID);

            if(orderId == null)
                return null;
            else
                return (Hashtable)ordersTable.get(orderId);
        }

    }


    static boolean compareFiles(String id1, String id2) throws IOException {

        Hashtable entry1 = getFileEntry(Long.parseLong(id1));

        if(entry1 == null)
            return false;


        Hashtable entry2 = getFileEntry(Long.parseLong(id2));

        if(entry2 == null)
            return false;

        String path1 = (String)entry1.get(FILE_PATH);
        String path2 = (String)entry2.get(FILE_PATH);

        return ( path1.equals(path2) );

    }



    private static synchronized void checkFiles() throws IOException {

      /*  if( ! authenticate() ) {
            sendImpAlert("SdUtils failed to authenticate");
            throw new IOException("SdUtils failed to authenticate in checkFiles");
            }*/

        long timestamp = new File(ordersFile).lastModified();
        boolean mustParseCompletedFile = false;

        if(timestamp > SdUtils.ordersLastParsed) {
            parseOrdersFile(ordersFile);
            SdUtils.ordersLastParsed = timestamp;

            // mustParseCompletedFile because parsing OrdersFile also updates filesTable
            mustParseCompletedFile = true;
        }


        String[] weeks = getWeeksOfYear();

        timestamp = new File(completedFile + weeks[0]).lastModified();

        if(timestamp > 0) {
            if(mustParseCompletedFile || timestamp > SdUtils.completedLastParsed) {
                parseCompletedFile(completedFile, weeks);
                SdUtils.completedLastParsed = timestamp;
            }
        }
        else {
            timestamp = new File(completedFile + weeks[1]).lastModified();
            if(timestamp > 0) {
                if(mustParseCompletedFile || timestamp > SdUtils.completedLastParsed) {
                    parseCompletedFile(completedFile, weeks);
                    SdUtils.completedLastParsed = timestamp;
                }
            }
        }

    }



    private static String[] getWeeksOfYear() {

        Calendar c = Calendar.getInstance();

        int week = c.get(Calendar.WEEK_OF_YEAR);
        String thisWeek = "." + String.valueOf(week);

        c.add(Calendar.WEEK_OF_YEAR, -1);
        week = c.get(Calendar.WEEK_OF_YEAR);
        String lastWeek = "." + String.valueOf(week);

        return new String[]{thisWeek, lastWeek};

    }



    static String updateCompleted(String user, String id) throws IOException {

      /*  if( ! authenticate() ) {
            sendImpAlert("SdUtils failed to authenticate");
            throw new IOException("SdUtils failed to authenticate in updateCompleted");
            }*/

        long fileID = Long.parseLong(id);

        String fileOwner = getFileOwner(fileID);

        if(fileOwner == null) {
            sendAlert("WARNING! File Not Found for fileID: " + id + " in updateCompleted()");
            return "File Not Found. Status Not Updated!";
        }

        if( ! fileOwner.equals(user) ) {
            sendAlert("WARNING! User mismatch for fileID: " + id + " in updateCompleted()   owner: " + fileOwner + " requestor: " + user);
            return "Not Your File. Status Not Updated!";
        }

        String file = completedFile + getWeeksOfYear()[0];

        id = id + "\n";

        FileOutputStream out = new FileOutputStream(file, true);

        try {
            out.write(id.getBytes());
        }
        finally {
            out.close();
        }

        return "Updated status for fileID: " + id;

    }




    private static void parseCompletedFile(String file, String[] weeks) throws IOException {

        // print("Reading file: " + file, false);

        long start = System.currentTimeMillis();

        Hashtable fileEntry = null;

        String line;

        // read old file, then new file
        String input = readFile(file + weeks[1]) + readFile(file + weeks[0]);

        BufferedReader in = new BufferedReader(new StringReader(input));

        while((line = in.readLine()) != null) {

            line = line.trim();

            if(line.length() == 0)
                continue;

            try {
                fileEntry = (Hashtable)filesTable.get(new Long(line));
            }
            catch(Exception e) {
                print("WARNING: Exception while parsing line: " + line + " in completedFile\n" + getStackTrace(e));
                continue;
            }

            if(fileEntry == null) {
                // print("WARNING: No entry found for completed file: " + line + " in filesTable");
                continue;
            }

            fileEntry.put(COMPLETED, "");

        }

        in.close();

        long time = System.currentTimeMillis() - start;

        print("Loaded completedFile: " + file + " successfully in " + time + " ms", false);

    }



    private static void parseOrdersFile(String file) throws IOException {

        // print("Reading file: " + file, false);

        long start = System.currentTimeMillis();

        Hashtable newOrdersTable = new Hashtable();
        Hashtable newFilesTable = new Hashtable();
        Hashtable newUsersTable = new Hashtable();

        String line;

        String input = readFile(file + ".test") + readFile(file + ".old") + readFile(file);

        BufferedReader in = new BufferedReader(new StringReader(input));

        while((line = in.readLine()) != null) {

            line = line.trim();

            if(line.length() == 0)
                continue;

            if( ! line.equals(ORDER_START) ) {
                print("WARNING: While parsing ordersFile: Expecting: " + ORDER_START + " Encountered: " + line);
                continue;
            }

            Hashtable orderEntry = new Hashtable();
            String orderID = null;
            String userID = null;
            HashSet files = new HashSet();

            while((line = in.readLine()) != null) {

                line = line.trim();

                if(line.length() == 0)
                    continue;
                else if( line.equals(ORDER_STOP) ) {
                    if(orderID == null)
                        throw new RuntimeException("ERROR: While parsing an order, no key: ORDER_ID found");
                    else if(userID == null)
                        throw new RuntimeException("ERROR: While parsing an order: " + orderID +", no key: USER_ID found");
                    else if(files.isEmpty())
                        throw new RuntimeException("ERROR: While parsing an order: " + orderID +", no files found");
                    else {

                        orderEntry.put(FILES, files);

                        if(newOrdersTable.put(orderID, orderEntry) != null)
                            print("WARNING: Encountered order entry with ORDER_ID: " + orderID + " more than once");
                        else {
                            HashSet userEntry = (HashSet) newUsersTable.get(userID);
                            if(userEntry == null) {
                                userEntry = new HashSet();
                                userEntry.add(orderID);
                                newUsersTable.put(userID, userEntry);
                            }
                            else if( ! userEntry.add(orderID) )
                                print("WARNING: This should not happen: While adding to list of orders for a user: Encountered order entry with ORDER_ID: " + orderID + " more than once");
                        }

                        break;
                    }
                }
                else if( line.equals(FILE_START) ) {

                    Hashtable fileEntry = new Hashtable();
                    Long fileID = null;

                    while((line = in.readLine()) != null) {

                        line = line.trim();

                        if(line.length() == 0)
                            continue;
                        else if( line.equals(FILE_STOP) ) {
                            if(fileID == null)
                                throw new RuntimeException("ERROR: While parsing a file for order: " + orderID + ", no key: FILE_ID found");
                            else if(orderID == null)
                                throw new RuntimeException("ERROR: While parsing a file: " + fileID + ", no key: ORDER_ID found");
                            else if(userID == null)
                                throw new RuntimeException("ERROR: While parsing a file: " + fileID + ", no key: USER_ID found");
                            else {

                                fileEntry.put(ORDER_ID, orderID);
                                fileEntry.put(USER_ID, userID);

                                if(newFilesTable.put(fileID, fileEntry) != null)
                                    print("WARNING: While parsing an order: Encountered file entry with FILE_ID: " + fileID + " more than once");
                                else if( ! files.add(fileID) )
                                        print("WARNING: This should not happen: While adding to list of files for an order: Encountered file entry with FILE_ID: " + fileID + " more than once");

                                break;
                            }
                        }
                        else {
                            int index = line.indexOf('=');
                            if( index > 0 && index < (line.length()-1) ) {
                                String key = line.substring(0, index).trim();
                                String value = line.substring(index+1).trim();
                                if(key.equals(FILE_ID)) {
                                    if(fileID == null)
                                        fileID = new Long(value);
                                    else
                                        throw new RuntimeException("ERROR: While parsing a file: Encountered key: " + key + " more than once. Old value: " + fileID + "   New value: " + value);
                                }
                                else if(fileEntry.put(key, value) != null)
                                    print("WARNING: While parsing a file: Encountered key: " + key + " more than once. New value: " + value);
                            }
                            else {
                                print("WARNING: While parsing ordersFile: Expecting: " + FILE_STOP + " or name=value   Encountered: " + line);
                            }
                        }
                    }
                }
                else {
                    int index = line.indexOf('=');
                    if( index > 0 && index < (line.length()-1) ) {
                        String key = line.substring(0, index).trim();
                        String value = line.substring(index+1).trim();
                        if(key.equals(ORDER_ID)) {
                            if(orderID == null)
                                orderID = value;
                            else
                                throw new RuntimeException("ERROR: While parsing a order: Encountered key: " + key + " more than once. Old value: " + orderID + " New value: " + value);
                        }
                        else if(key.equals(USER_ID)) {
                            if(orderEntry.put(key, value) != null)
                                throw new RuntimeException("ERROR: While parsing a order: Encountered key: " + key + " more than once. Old value: " + orderID + " New value: " + value);
                            else
                                userID = value;
                        }
                        else if(orderEntry.put(key, value) != null)
                            print("WARNING: While parsing an order: Encountered key: " + key + " more than once. New value: " + value);
                    }
                    else {
                        print("WARNING: While parsing ordersFile: Expecting: " + ORDER_STOP + ", " + FILE_START + " or name=value   Encountered: " + line);
                    }
                }
            }
        }

        in.close();

        ordersTable = newOrdersTable;
        filesTable = newFilesTable;
        usersTable = newUsersTable;

        long time = System.currentTimeMillis() - start;

        print("Loaded ordersTable: " + file + " successfully in " + time + " ms", false);

    }




    private static String readFile(String filename) throws IOException {

        if(new File(filename).length() <= 0)
            return "";

        String content = "";
        int bytesRead;
        byte[] arr = new byte[10240];

        FileInputStream in = new FileInputStream(filename);

        try {
            while( (bytesRead = in.read(arr) ) >= 0)
                content += new String(arr, 0, bytesRead);
        }
        finally {
            in.close();
        }

        return content;

    }




    private static String readLogs(String filename) throws IOException {

        if(new File(filename).length() <= 0)
            return "Log Is Empty";

        String content = "";
        int bytesRead;
        byte[] arr = new byte[10240];

        FileInputStream in = new FileInputStream(filename);

        try {
            while( (bytesRead = in.read(arr) ) >= 0)
                content += new String(arr, 0, bytesRead);
        }
        finally {
            in.close();
        }

        return content;

    }



    private static String readLicenseAgreement(String filename) throws IOException {

        String content = "";
        int bytesRead;
        byte[] arr = new byte[10240];

        FileInputStream in = new FileInputStream(filename);

        try {
            while( (bytesRead = in.read(arr) ) >= 0)
                content += new String(arr, 0, bytesRead);
        }
        finally {
            in.close();
        }

        content = content.substring(0, content.lastIndexOf("</body>")).trim();

        if(content.length() == 0)
            throw new IOException(filename + ": empty");

        return content;

    }



    private static int execute(String cmd) {

        String inString = "";
        String errString = "";
        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;
        String[] command = {"/bin/ksh", "-c", cmd};

        try {
            Process p = Runtime.getRuntime().exec(command);
            exitValue = p.waitFor();

            try {
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
                int read = 0;
                while(read >= 0) {
                    inString += new String(arr, 0, read);
                    read = in.read(arr, 0, arrSize);
                }
                read = 0;
                while(read >= 0) {
                    errString += new String(arr, 0, read);
                    read = err.read(arr, 0, arrSize);
                }
                in.close();
                err.close();
            }
            catch(Throwable t1) {
                print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t1));
            }


            String str
                = cmd + " returned an exit Value of " + exitValue + "\n"
                + "stdout: " + inString + "\n"
                + "stderr: " + errString + "\n";

            if(exitValue != 0)
                print(str);

        }
        catch(Throwable t) {
            print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t));
        }

        return exitValue;

    }



    private static String executeAndGetStream(String cmd) {

        String inString = "";
        String errString = "";
        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;
        String[] command = {"/bin/ksh", "-c", cmd};

        try {
            Process p = Runtime.getRuntime().exec(command);
            exitValue = p.waitFor();

            try {
                BufferedInputStream in = new BufferedInputStream(p.getInputStream());
                BufferedInputStream err = new BufferedInputStream(p.getErrorStream());
                int read = 0;
                while(read >= 0) {
                    inString += new String(arr, 0, read);
                    read = in.read(arr, 0, arrSize);
                }
                read = 0;
                while(read >= 0) {
                    errString += new String(arr, 0, read);
                    read = err.read(arr, 0, arrSize);
                }
                in.close();
                err.close();
            }
            catch(Throwable t1) {
                print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t1));
            }


            String str
                = cmd + " returned an exit Value of " + exitValue + "\n"
                + "stdout: " + inString + "\n"
                + "stderr: " + errString + "\n";

            if(exitValue != 0)
                print(str);

        }
        catch(Throwable t) {
            print("thrown reading output from: " + cmd + ":\n" + getStackTrace(t));
        }

        return inString;

    }



    private static void servletAlert(String data, boolean isUpAlert) {

        alert("App", data, isUpAlert);

    }


    private static void authenticationAlert(String data, boolean isUpAlert) {

        alert("Service", data, isUpAlert);

    }


    private static void mailAlert(String data, boolean isUpAlert) {

        alert("Service", data, isUpAlert);

    }




    private static void alert(String event, String data, boolean isUpAlert) {

        if(dce_monitor == null)
            return;

        String monitor = "SDS_Servlet";


        if(isUpAlert)
            event += "Up";
        else {
            event += "Down";
            data += " ... more details might be available at: " + errFile;
        }


        String object = SdUtils.hostname;



        String cmd
            = dce_monitor
            + " -m '" + monitor
            + "' -e '" + event
            + "' -o '" + object
            + "' -d '" + data + "'";


        execute(cmd);

    }



    static void sendAlert(String s) {

        print(version + s, true);

    }


    static void sendImpAlert(String s) {

        s = s + " - SdUtils: " + version;

        print(s, true);

        try {
            sendErrorMail(s);
        }
        catch(Throwable t) {
            print("Exception sending error mail. Stacktrace:\n" + getStackTrace(t));
        }

    }



    static void print(String str) {
        print(str, true);
    }


    static void print(String str, String sourceServlet) {
        print(sourceServlet + ": " + str, true);
    }


    static synchronized void print(String str, boolean isError) {

        String date = formatter.format(new Date());

        str = date + " " + str + "\n\n";

        String file;


        if(isError) {
            if(errFile != null)
                file = errFile;
            else {
                System.err.print(str);
                return;
            }
        }
        else {
            if(outFile != null)
                file = outFile;
            else {
                System.out.print(str);
                return;
            }
        }


        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file, true);
            out.write(str.getBytes());
        }
        catch(Throwable t) {
            System.err.println("Exception writing the following to " + file + ":\n" + str + "\nStacktrace:\n" + getStackTrace(t));
        }
        finally {
            try {
                out.close();
            }
            catch(Throwable t) { }
        }
    }




    private static boolean isAdmin(String id) {

        if(admins == null || id == null)
            return false;

        for(int i = 0; i < admins.length; i++)
            if(id.equals(admins[i]))
                return true;

        return false;

    }



    static String getRequiredProperty(String s) {

        String value = SdUtils.props.getProperty(s);

        if(value == null || value.trim().length() == 0)
            throw new RuntimeException("Required property: " + s + " not found (or is blank) in properties file");
        else
            return value.trim();

    }



    static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }



    private static String[] stringToArray(String s) {

        if(s == null || s.trim().length() == 0)
            return null;
        else
            s = s.trim();

        StringTokenizer st = new StringTokenizer(s, ",");
        int numTokens = st.countTokens();
        String[] retArray = new String[numTokens];
        int i = 0;

        while(st.hasMoreTokens())
            retArray[i++] = st.nextToken().trim();

        return retArray;

    }



    private static String quote(String s) {

        return "\"" + s + "\"";

    }


    private static String quote(long l) {

        return quote(String.valueOf(l));

    }



    static String getUserInfo(String user, String order) throws IOException {

        String slaRequired = "no";

        StringBuffer top1 = new StringBuffer();
        top1.append("<?xml version=\"1.0\"?>\n");
        top1.append("<edsd_download_client version=\"2.10\" slaRequired=");

        StringBuffer top2 = new StringBuffer();
        top2.append(">\n");
        top2.append("<order_list>\n");


        StringBuffer bottom = new StringBuffer();
        bottom.append("</order_list>\n");
        bottom.append("</edsd_download_client>\n");

        HashSet userSet = getUserEntry(user);

        if(userSet == null) {
            top1.append(quote(slaRequired));
            top1.append(top2.toString());
            top1.append(bottom.toString());
            return top1.toString();
        }


        StringBuffer buf = new StringBuffer();

        Iterator userEntry = userSet.iterator();


        while(userEntry.hasNext()) {

            String orderID = (String) userEntry.next();

	    if(order != null && ! order.equals(orderID))
		continue;

            Hashtable orderEntry = (Hashtable) ordersTable.get(orderID);

            if(orderEntry == null) {
                print("No entry for orderID: " + orderID + " found in ordersTable, but found in usersTable");
                continue;
            }

            String expiration = (String) orderEntry.get(EXPIRATION_TIME);
	    boolean orderExpired = false;

	    long expTime = Long.parseLong(expiration);
	    if(System.currentTimeMillis() > expTime)
		orderExpired = true;

            buf.append("<order");

            buf.append(" number=");
            buf.append(quote(orderID));

            buf.append(" expiration=");
            buf.append(quote(expiration));

            buf.append(">\n");


            Iterator files = ((HashSet) orderEntry.get(FILES)).iterator();

            while(files.hasNext()) {

                Long fileID = (Long) files.next();

                Hashtable fileEntry = (Hashtable) filesTable.get(fileID);

                if(fileEntry == null) {
                    print("No entry for fileID: " + fileID + " found in filesTable, but found in ordersTable");
                    continue;
                }

                String downloaded = "no";
                if(fileEntry.get(COMPLETED) != null)
                    downloaded = "yes";

                String fileName = (String) fileEntry.get(FILE_NAME);
                String fileDesc = (String) fileEntry.get(FILE_DESC);
                String filePath = (String) fileEntry.get(FILE_PATH);

                if(filePath.indexOf("PreviewKit") != -1)
                    slaRequired = "yes";

                File f = new File(filePath);

                if( ! f.isFile() ) {
                    if( ! orderExpired )
                        print("ERROR! File not found for non-expired order. fileID: " + fileID);
                    continue;
                }

                long fileLastMod = f.lastModified();
                long fileSize = f.length();

                buf.append("<file");

                buf.append(" downloaded=");
                buf.append(quote(downloaded));

                buf.append(" name=");
                buf.append(quote(fileName));

                buf.append(" size=");
                buf.append(quote(fileSize));

                buf.append(" time=");
                buf.append(quote(fileLastMod));

                buf.append(" desc=");
                buf.append(quote(fileDesc));

                buf.append(">\n");

                buf.append(fileID);

                buf.append("\n</file>\n");

            }

            buf.append("</order>\n");

        }

        top1.append(quote(slaRequired));
        top1.append(top2.toString());
        top1.append(buf.toString());
        top1.append(bottom.toString());

        return top1.toString();

    }


    private static String getAllUserInfo() throws IOException {
        // to be filled in later
        return "";
    }

    //Subu 10/12
    public static String getODCURL(HttpServletRequest req) {
       return req.getScheme() + "://" + req.getServerName() + odcHOME;
    }

}


