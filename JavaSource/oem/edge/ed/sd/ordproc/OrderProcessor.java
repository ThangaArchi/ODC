package oem.edge.ed.sd.ordproc;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//                     
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

//import oem.edge.ed.util.EDCMafsFile;
//import oem.edge.ed.util.PasswordUtils;

import oem.edge.ed.sd.mq.MQReceive;
import oem.edge.ed.sd.mq.MQSend;

import com.ibm.mq.MQException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Calendar;
import java.util.Vector;
import java.text.*;
import java.io.*;

public class OrderProcessor {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private static final boolean sendBccForCDmails = true;

    public static final String ignoreUserid = "health2-";

    static final int PING_ERR  =-2;
    static final int ERR       =-1;
    static final int WARN      = 0;
    static final int V_IMP     = 1;
    static final int IMP       = 2;
    static final int NOT_IMP   = 3;
    static final int HEARTBEAT = 9;
    static final int PING      =10;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("MMddyy");               // to rename old log files
    private static final SimpleDateFormat hbFormatter = new SimpleDateFormat("MM/dd/yy-HH:mm:ss");  // for heartbeat and ping

    private static final long AFS_AUTH_DURATION = 12 * 60 * 60 * 1000;   // 12 hours

    private static boolean MAIL_ON_ERROR;
    private static long HEARTBEAT_INTERVAL, lastHeartbeat, receivedCOA, receivedCOD, receivedPing, receivedPong, pingPong;
    private static int ACCEPTABLE_PING_RT_TIME;
    private static String hostname = "";           // will be determined at startup
    private static String currentOrdersDir, completedOrdersDir, shipInfoPath;

    private static Vector pingVector = new Vector();

    private static String mailHost1, mailHost2, mailHost3;

  //  private static boolean AFSauthenticated = false; // whether authentication has been done
    private static boolean lastPingReceived;         // whether authentication has been done

    private static boolean useDB2 = false;

    private static String jdbcDriverClassName, db2Url, db2User, db2Pwd;


    static final String[] CORR_ID = {
        "EDQ1_ORDER_DK", 
        "EDQ1_ORDER_PK", 
        "EDQ1_ORDER_DR", 
        "EDQ1_ORDER_PDK", 
        "EDQ1_LIC_REQ_TK", 
        "EDQ1_LIC_REQ_PDK",
        "EDQ1_LIC_REQ_XMX",

        "EDQ1_ORDER_DSE",
        "EDQ1_ORDER_XMX",
        "EDQ1_ORDER_MEM",
        "EDQ1_AREA_MEM",
        "EDQ1_REPORT_MEM",

        "EDQ1_PING",

	"EDQ1_DK_INITIAL",
	"EDQ1_DR_INITIAL",
        "EDQ1_ORDER_FPGA",  //new for 4.5.1
       //new for 5.1.1
        "EDQ1_IPDK_INITIAL",
        "EDQ1_ORDER_IPDK"
       //end of new for 5.1.1
    };

    static final String[] orderNames = {
        "Design Kit",
        "Preview Kit",
        "Delta Release",
        "Toolkit Fix",
        "License for ToolKit",
        "License for Full PD Kit",
        "License for X Collaborative Tool",

        "DieSizer",
        "X Collaborative Tool",
        "Memory Compiler",
        "Memory Area",
        "Memory Report",

        "Ping",

	"Initial Design Kit",
	"Initial Delta Release",
        "Efpga",  //new for 4.5.1
       //new for 5.1.1
        "Initial IP Design Kit",
        "IP Design Kit"
       //end of new for 5.1.1
    };
    
    static final int DESIGN       = 0;
    static final int PREVIEW      = 1;
    static final int DELTA        = 2;
    static final int FULLPD       = 3;
    static final int LIC_TOOL     = 4;
    static final int LIC_FULLPD   = 5;
    static final int LIC_XMX      = 6;

    static final int DSE          = 7;
    static final int XMX          = 8;
    static final int MEM          = 9;
    static final int AREA_MEM     = 10;
    static final int REPORT_MEM   = 11;

    static final int GET_PING     = 12;

    static final int DK_INIT      = 13;
    static final int DR_INIT      = 14;
    static final int EFPGA        = 15; // new for 4.5.1
    static final int IPDK_INIT    = 16;  // new for 5.1.1 
    static final int IPDESIGN     = 17;   //new for 5.1.1

    private static String HOME_DIR;       // for path to password
    private static String INSTALL_DIR;    // for executables, property and log files
    private static String EDESIGN_HOME_DIR;   // BTV staging area
    private static String CHIPS_DL_DIR;   // BTV staging area
    private static String LOGS_DIR;   // absolute path of directory where logs will be kept

    private static String MAIN_LOG_FILE;
    private static String PERF_LOG_FILE;
    private static String ERR_LOG_FILE;
    private static String MQ_LOG_FILE;
    private static String HEARTBEAT_LOG_FILE;
    private static String PING_LOG_FILE;
    
    

    private static int DISPLAY_LEVEL;
    
   private static Properties defaultProps = new Properties();
    private static Properties props = new Properties();

    private static Properties defaultMqProps = new Properties();
    private static Properties mqProps = new Properties();

    private static MQReceive mqRecv;
    private static MQSend mqSend;
	
	static String dropboxURL;
    private static void loadDefaultProperties() {

        // properties set by install script
        defaultProps.put("HOME_DIR", "/web/ibm/edesign/software-delivery");
        defaultProps.put("INSTALL_DIR", "/web/ibm/edesign/software-delivery/EDSDinstall");

        defaultProps.put("TESTING", "no");             // {"yes", "no"}

        // flag signalling OrderProcessor to cleanup and stop
        defaultProps.put("HALT_FLAG", "/web/ibm/edesign/software-delivery/EDSDinstall/bin/oem/edge/ed/sd/logs/HALT.OrderProcessor");


        // afs user ids
        defaultProps.put("USERID_1", "edxfrprd");
        defaultProps.put("USERID_2", "edxfrprd");
        defaultProps.put("USERID_3", "edlicgen");


        defaultProps.put("EMAIL_HOST_NAME", "westreplay.us.ibm.com");
        defaultProps.put("BACKUP_1_EMAIL_HOST_NAME", "euhdb1a.btv.ibm.com");
        defaultProps.put("BACKUP_2_EMAIL_HOST_NAME", "us.ibm.com");


        defaultProps.put("EDSD_DEV_1", "fyuan@us.ibm.com");

        // remove default 2 so we can prevent sending mails if needed
        defaultProps.put("EDSD_DEV_2", "");


        // directories
        defaultProps.put("EDESIGN_HOME_DIR", "/web/server_root/datapersist/technologyconnect/sd/staging");
        defaultProps.put("CHIPS_DL_DIR", "/web/server_root/datapersist/technologyconnect/sd/download");

        defaultProps.put("DR_PACKETS_DIR", "/web/server_root/datapersist/asicpatch2");

        defaultProps.put("LOGS_DIR", "/web/server_root/datapersist/technologyconnect/sd/staging/LogDir/OrderProcessor");
        // absolute path of directory where logs will be kept


        // scripts
        defaultProps.put("AUTO_LIC_SCRIPT", "/afs/eda/common/httpd/1.3/cgi-bin/auto_lic.pl");

        defaultProps.put("SHIPIT_SCRIPT", "/web/server_root/datapersist/technologyconnect/sd/aim/edesign/bin/shipit");
        defaultProps.put("MK_SIZE_SCRIPT", "/web/server_root/datapersist/technologyconnect/sd/aim/edesign/bin/mkSize");
        defaultProps.put("GET_TOOLS_SCRIPT", "/web/server_root/datapersist/technologyconnect/sd/aim/edesign/bin/getTools");
        defaultProps.put("COPY_ORDER_SCRIPT", "/web/server_root/datapersist/technologyconnect/sd/aim/edesign/bin/copyOrder");
        defaultProps.put("MERGE_TOOLS_SCRIPT", "/web/server_root/datapersist/technologyconnect/sd/aim/edesign/bin/mergeToolMts");
        defaultProps.put("CHECK_AFS_SPACE_SCRIPT", "/web/ibm/edesign/software-delivery/EDSDinstall/bin/oem/edge/ed/sd/CheckAFSspace.sh");
        defaultProps.put("CREATE_DR_SCRIPT", "/web/ibm/edesign/software-delivery/EDSDinstall/bin/oem/edge/ed/sd/mkDeltaRelease.sh");

       //  defaultProps.put("URL_PREFIX", "https://edesign.chips.ibm.com/SD/servlet/SDS");
        defaultProps.put("URL_PREFIX", "https://technologyconnect/sd/servlet/SDS");
        defaultProps.put("SD_TABLE_PATH", "/web/server_root/datapersist/technologyconnect/sd/download/edesign.edsd.contentdelivery.prod");
        defaultProps.put("SHIP_INFO_PATH", "/web/server_root/datapersist/technologyconnect/sd/staging/LogDir/OrderProcessor/shipInfo.out");

        defaultProps.put("CHIPS_CUSTOM_BINS_DIR", "/web/server_root/datapersist/technologyconnect/sd/download/CustomBins");  // # bins for custom kits
        defaultProps.put("NUM_CHIPS_CUSTOM_BINS", "1");  // # bins for custom kits

        defaultProps.put("BTV_CUSTOM_BIN_DIR", "/web/server_root/data/technologyconnect/sd/staging/CustomBin");  // # bins for custom kits





        defaultProps.put("DISPLAY_LEVEL", "1");        // {"0", "1", "2", "3"}
        defaultProps.put("SEND_MQ_OR_EMAIL", "mq");    // {"mq", "email", "both"}
        defaultProps.put("MAIL_ON_ERROR", "yes");      // {"yes", "no"}
        defaultProps.put("HEARTBEAT_INTERVAL", "5");   // minutes
        defaultProps.put("ACCEPTABLE_PING_RT_TIME", "10");   // seconds



        // email properties
        defaultProps.put("EMAIL_NUM_RETRIES", "10");



        defaultProps.put("FROM_ID", "IBM Customer Connect <eConnect@us.ibm.com>");
        defaultProps.put("REPLY_TO", "eConnect@us.ibm.com");




        // Used in 3 places:
        //   1. While waiting for model kit to show up after shipit returns 1 or 2
        //   2. While waiting for shipping.STOPPED to dissapear for Preview Kit Order
        //   3. When all CHIPS bins (for model kit orders) are locked or full
        defaultProps.put("SHIPIT_WAIT_TIME", "30");   // minutes



        // how long to wait before sending emails after shipit returns 1 or 2
        defaultProps.put("CRITICAL_DAYS_1", "1");
        defaultProps.put("CRITICAL_DAYS_2", "7");
        defaultProps.put("CRITICAL_DAYS_3", "14");



        // info for getting tokens
        defaultProps.put("CELL_1", "btv.ibm.com");
        defaultProps.put("CELL_2", "chips.ibm.com");
        defaultProps.put("CELL_3", "eda.fishkill.ibm.com");

        defaultProps.put("USE_SMART_DOWNLOAD", "yes");

        defaultProps.put("USE_AUTO_LICENSE_GENERATION", "yes");

        defaultProps.put("XMX_LIC_MAIL_TO", "lauraeng@us.ibm.com");
        defaultProps.put("XMX_LIC_MAIL_CC", "waiki@us.ibm.com");

        // mq props
        defaultMqProps.put("ed.MQSeries.Queue.From.Name", "EDESIGN.EDGE_TO_MD");
        defaultMqProps.put("ed.MQSeries.Queue.To.Name",   "EDESIGN.MD_TO_EDGE");
        defaultMqProps.put("ed.MQSeries.Queue2.To.Name",   "EDESIGN.GA.MD_TO_EDGE");

        defaultMqProps.put("ed.MQSeries.Queue.Manager.Name", "BTVEDPRD");
        defaultMqProps.put("ed.MQSeries.Queue.Manager.ReplyTo.Name", "BTVEDPRD");
        defaultMqProps.put("ed.MQSeries.Channel.Name", "SYSTEM.DEF.SVRCONN");
        defaultMqProps.put("ed.MQSeries.Host.Name", "btvedprd.btv.ibm.com");
        defaultMqProps.put("ed.MQSeries.Port", "1414");

        defaultMqProps.put("ed.MQSeries.expiry", "10");       // minutes
        defaultMqProps.put("ed.MQSeries.waitInterval", "60"); // seconds
        defaultMqProps.put("ed.MQSeries.sleepTime", "60");    // seconds
        defaultMqProps.put("ed.MQSeries.numRetries", "5");
        defaultMqProps.put("ed.MQSeries.displayLevel", "1"); // (<= 0 : silent), (> 0 : normal)

        // MQ error codes to retry on
        defaultMqProps.put("ed.MQSeries.nonFatalErrorCodes", "2009, 2059, 2162, 2195");

        defaultProps.put("useDB2", "no");
        defaultProps.put("jdbcDriverClassName", "COM.ibm.db2.jdbc.app.DB2Driver");

        defaultProps.put("db2Url", "");
        defaultProps.put("db2User", "");
        defaultProps.put("db2Pwd", "");
    }

    
    
    public static void main(String[] args) {

        if(args.length != 2) {
            System.out.println("Usage: java OrderProcessor <ordproc properties file> <mq properties file>");
            return;
        }

        loadDefaultProperties();

        try {
            String ordprocPropertiesFilename = args[0];
            String mqPropertiesFilename = args[1];

            File propsFile = new File(ordprocPropertiesFilename);
            long propsLastModified = propsFile.lastModified();
            FileInputStream ordFis = new FileInputStream(propsFile);
            OrderProcessor.props.load(ordFis);
            ordFis.close();


            File mqPropsFile = new File(mqPropertiesFilename);
            long mqPropsLastModified = mqPropsFile.lastModified();
            FileInputStream mqFis = new FileInputStream(mqPropsFile);
            OrderProcessor.mqProps.load(mqFis);
            mqFis.close();


            if(getProperty("MAIL_ON_ERROR").equalsIgnoreCase("no") || getProperty("MAIL_ON_ERROR").equalsIgnoreCase("false"))
                MAIL_ON_ERROR = false;
            else
                MAIL_ON_ERROR = true;

            mailHost1 = OrderProcessor.props.getProperty("EMAIL_HOST_NAME");
            mailHost2 = OrderProcessor.props.getProperty("BACKUP_1_EMAIL_HOST_NAME");
            mailHost3 = OrderProcessor.props.getProperty("BACKUP_2_EMAIL_HOST_NAME");
            
            dropboxURL = OrderProcessor.props.getProperty("ED_DROPBOX_URL");

            File haltFlag = new File(getProperty("HALT_FLAG"));  // filename, appearance of which indicated the daemon to halt

            HEARTBEAT_INTERVAL = Long.parseLong(getProperty("HEARTBEAT_INTERVAL")) * 60 * 1000;
            ACCEPTABLE_PING_RT_TIME = Integer.parseInt(getProperty("ACCEPTABLE_PING_RT_TIME")) * 1000;
            DISPLAY_LEVEL = Integer.parseInt(getProperty("DISPLAY_LEVEL"));
            HOME_DIR     = getProperty("HOME_DIR");
            INSTALL_DIR  = getProperty("INSTALL_DIR");
            EDESIGN_HOME_DIR = getProperty("EDESIGN_HOME_DIR");
            CHIPS_DL_DIR = getProperty("CHIPS_DL_DIR");
            LOGS_DIR = getProperty("LOGS_DIR");
            defaultProps.put("CHIPS_CUSTOM_BINS_DIR", OrderProcessor.props.getProperty("CHIPS_CUSTOM_BINS_DIR")); 
        
            if( ! HOME_DIR.endsWith("/"))
                HOME_DIR += "/";

            if( ! INSTALL_DIR.endsWith("/"))
                INSTALL_DIR  += "/";

            if( ! EDESIGN_HOME_DIR.endsWith("/"))
                EDESIGN_HOME_DIR += "/";

            if( ! CHIPS_DL_DIR.endsWith("/"))
                CHIPS_DL_DIR += "/";


            MAIN_LOG_FILE      = LOGS_DIR + "/OrderProcessorLog";
            PERF_LOG_FILE      = LOGS_DIR + "/PerformanceLog";
            ERR_LOG_FILE       = LOGS_DIR + "/ErrorLog";
            MQ_LOG_FILE        = LOGS_DIR + "/MQLog";
            HEARTBEAT_LOG_FILE = LOGS_DIR + "/HeartbeatLog";
            PING_LOG_FILE      = LOGS_DIR + "/BldToBtvLog";

            currentOrdersDir = LOGS_DIR + "/CurrentOrders/";
            completedOrdersDir = LOGS_DIR + "/CompletedOrders/";
            
            // get the system hostname
            try {
                hostname = java.net.InetAddress.getLocalHost().getHostName();
                if (hostname.indexOf('.') >= 0)
                   hostname = hostname.substring(0, hostname.indexOf('.'));
            }
            catch(java.net.UnknownHostException e) { }

            /*
                int hnArrSize = 512;
                byte[] hnArr = new byte[hnArrSize];
                Process hnP = Runtime.getRuntime().exec("/usr/bin/hostname -s");
                hnP.waitFor();
                InputStream hnIn = hnP.getInputStream();
                int hnRead = 0;
                while(hnRead >= 0) {
                    hostname += new String(hnArr, 0, hnRead);
                    hnRead = hnIn.read(hnArr, 0, hnArrSize);
                }
                hnIn.close();
                hostname = hostname.trim();
            */



           /*   String PASSWD_1 = PasswordUtils.getPassword(HOME_DIR + "." + getProperty("CELL_1"));
            String PASSWD_2 = PasswordUtils.getPassword(HOME_DIR + "." + getProperty("CELL_2"));

            EDCMafsFile authenticator = new EDCMafsFile();

            if( ! authenticator.afsAuthenticate(getProperty("CELL_1"), getProperty("USERID_1"), PASSWD_1))
                handleException("Could not get AFS authentication for user_id: " + getProperty("USERID_1") + " for cell: " + getProperty("CELL_1"));
            
            if( ! authenticator.afsAuthenticate(getProperty("CELL_2"), getProperty("USERID_2"), PASSWD_2))
                handleException("Could not get AFS authentication for user_id: " + getProperty("USERID_2") + " for cell: " + getProperty("CELL_2"));
           */

            int permittedAfsFailures = 10;
            int numAfsFailures = 0;


            String usedb = props.getProperty("useDB2");
            if(usedb != null && usedb.equalsIgnoreCase("yes"))
                useDB2 = true;

            if(useDB2) {
                jdbcDriverClassName = getProperty("jdbcDriverClassName");
                db2Url =  getProperty("db2Url");
                db2User = getProperty("db2User");
                db2Pwd = getProperty("db2PwdPath");
            }



            File f = new File(LOGS_DIR);
            if( ! f.isDirectory())
                if( ! f.mkdirs())
                    handleException("Directory: " + f.toString() + " could not be created");


            MAIN_LOG_FILE      = LOGS_DIR + "/OrderProcessorLog";
            PERF_LOG_FILE      = LOGS_DIR + "/PerformanceLog";
            ERR_LOG_FILE       = LOGS_DIR + "/ErrorLog";
            MQ_LOG_FILE        = LOGS_DIR + "/MQLog";
            HEARTBEAT_LOG_FILE = LOGS_DIR + "/HeartbeatLog";
            PING_LOG_FILE      = LOGS_DIR + "/BldToBtvLog";

            currentOrdersDir = LOGS_DIR + "/CurrentOrders/";
            completedOrdersDir = LOGS_DIR + "/CompletedOrders/";

           //  long lastAuthenticatedTime = System.currentTimeMillis();
           // OrderProcessor.AFSauthenticated = true;


            // the lines below are required as otherwise it formats the date in Pacific Time???
            formatter.setCalendar(Calendar.getInstance());
            hbFormatter.setCalendar(Calendar.getInstance());


            updateDailyLogs(null);


            shipInfoPath = getProperty("SHIP_INFO_PATH");

           //  print("Authenticated for BTV and CHIPS cells...Starting OrderProcessor on " + hostname + " at " + new Date(), V_IMP);
            print("\n" + hbFormatter.format(new Date()) + " " + "STARTING OrderProcessor", HEARTBEAT);

            lastHeartbeat = System.currentTimeMillis();
            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            String date = formatter.format(new Date());


            MQHandler.setProperties(LOGS_DIR, defaultProps, defaultMqProps, EDESIGN_HOME_DIR, CHIPS_DL_DIR);
            Mutex.initialize(getProperty("CHIPS_CUSTOM_BINS_DIR"), Integer.parseInt(getProperty("NUM_CHIPS_CUSTOM_BINS")), getProperty("CHECK_AFS_SPACE_SCRIPT"), (200*1024*1024) );
            EDSDContentDeliveryTable.setConfigPath(getProperty("SD_TABLE_PATH"));


            OrderProcessor.mqRecv = new MQReceive(OrderProcessor.mqProps, OrderProcessor.defaultMqProps, MQ_LOG_FILE);
            OrderProcessor.mqSend = new MQSend(OrderProcessor.mqProps, OrderProcessor.defaultMqProps, MQ_LOG_FILE);

            lastHeartbeat = System.currentTimeMillis();
            sendMQMessage("EDQ3_PING", String.valueOf(lastHeartbeat), String.valueOf(lastHeartbeat), true);
            pingVector.add(new Long(lastHeartbeat));
            lastPingReceived = false;
            receivedCOA = receivedCOD = receivedPing = receivedPong = pingPong = -1;

            processIncompleteOrders();  // processes orders which were not finished during last run

            // MAIN LOOP
            while( ! haltFlag.exists()) {

                OrderProcessor.mqRecv.recieveMQMessage();
                
                if((System.currentTimeMillis() - lastHeartbeat) > HEARTBEAT_INTERVAL) {

		    processShipInfo();

                    print(hbFormatter.format(new Date(lastHeartbeat)) 
                          + " "
                          + pingPong
                          + " "
                          + receivedPing
                          + " "
                          + receivedPong
                          + " "
                          + receivedCOA
                          + " "
                          + receivedCOD
                                       , PING);


                    if( ! lastPingReceived )
                        sendPingAlert(lastHeartbeat);


                    if( ! pingVector.isEmpty() ) {

                        long cutoff = System.currentTimeMillis() - (12 * 60 * 60 * 1000);    // 12 hours ago
                        long thisPing = 0;
                        Vector toRemove = new Vector(); 

                        for(int i = 0; i < pingVector.size(); i++) {
                            thisPing = ( (Long) (pingVector.get(i)) ).longValue();
                            if(thisPing < cutoff) {
                                sendSeriousPingAlert(thisPing);
                                toRemove.add(new Long(thisPing));
                            }
                        }

                        for(int i = 0; i < toRemove.size(); i++)
                            pingVector.remove(toRemove.get(i));

                    }


                    if(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != dayOfYear) {
                        updateDailyLogs(date);
                        dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                        date = formatter.format(new Date());
                    }

                    
                   //  print(hbFormatter.format(new Date()) + " " + "lockedBins: " + Mutex.getLockedBinsList(), HEARTBEAT);
                    lastHeartbeat = System.currentTimeMillis();
                    sendMQMessage("EDQ3_PING", String.valueOf(lastHeartbeat), String.valueOf(lastHeartbeat),  true);
                    pingVector.add(new Long(lastHeartbeat));
                    lastPingReceived = false;
                    receivedCOA = receivedCOD = receivedPing = receivedPong = pingPong = -1;


                    checkRetryDir(LOGS_DIR + "/retry/");


                    if(propsLastModified != propsFile.lastModified()) {

                        propsLastModified = propsFile.lastModified();

                        print("ordprocPropsFile has new time stamp. Loading new properties at " + new Date(), V_IMP);


                        OrderProcessor.props = new Properties();
                        ordFis = new FileInputStream(propsFile);
                        OrderProcessor.props.load(ordFis);
                        ordFis.close();

                        HEARTBEAT_INTERVAL = Long.parseLong(getProperty("HEARTBEAT_INTERVAL")) * 60 * 1000;
                        DISPLAY_LEVEL = Integer.parseInt(getProperty("DISPLAY_LEVEL"));

                        if(getProperty("MAIL_ON_ERROR").equalsIgnoreCase("no") || getProperty("MAIL_ON_ERROR").equalsIgnoreCase("false"))
                            MAIL_ON_ERROR = false;
                        else
                            MAIL_ON_ERROR = true;

                    }


                    if(mqPropsLastModified != mqPropsFile.lastModified()) {

                        mqPropsLastModified = mqPropsFile.lastModified();

                        print("mqPropsFile has new time stamp. Loading new properties at " + new Date(), V_IMP);
                    
                        OrderProcessor.mqProps = new Properties();
                        mqFis = new FileInputStream(mqPropsFile);
                        OrderProcessor.mqProps.load(mqFis);
                        mqFis.close();

                        if(OrderProcessor.mqRecv != null)
                            OrderProcessor.mqRecv.cleanup();

                        OrderProcessor.mqSend = new MQSend(OrderProcessor.mqProps, OrderProcessor.defaultMqProps, MQ_LOG_FILE);
                        OrderProcessor.mqRecv = new MQReceive(OrderProcessor.mqProps, OrderProcessor.defaultMqProps, MQ_LOG_FILE);

                    }

                    
                   /*    if((System.currentTimeMillis() - lastAuthenticatedTime) > AFS_AUTH_DURATION) {//getToken again

                        boolean success = true;


                        if( ! authenticator.afsAuthenticate(getProperty("CELL_1"), getProperty("USERID_1"), PASSWD_1)) {
                            String newPswd = PasswordUtils.getPassword(HOME_DIR + "." + getProperty("CELL_1"));
                            if( ! authenticator.afsAuthenticate(getProperty("CELL_1"), getProperty("USERID_1"), newPswd)) {
                                success = false;
                                handleMinorException("Could not get AFS authentication for user_id: " + getProperty("USERID_1") + " for cell: " + getProperty("CELL_1"));
			    }
                            else {
				PASSWD_1 = newPswd;
                                print("Got AFS authentication for user_id: " + getProperty("USERID_1") + " for cell: " + getProperty("CELL_1") + " at: " + new Date() + " (new)", V_IMP);
			    }
                        }
                        else
                            print("Got AFS authentication for user_id: " + getProperty("USERID_1") + " for cell: " + getProperty("CELL_1") + " at: " + new Date(), V_IMP);
                    

                        if( ! authenticator.afsAuthenticate(getProperty("CELL_2"), getProperty("USERID_2"), PASSWD_2)) {
                            String newPswd = PasswordUtils.getPassword(HOME_DIR + "." + getProperty("CELL_2"));
                            if( ! authenticator.afsAuthenticate(getProperty("CELL_2"), getProperty("USERID_2"), newPswd)) {
                                success = false;
                                handleMinorException("Could not get AFS authentication for user_id: " + getProperty("USERID_2") + " for cell: " + getProperty("CELL_2"));
			    }
                            else {
				PASSWD_2 = newPswd;
                                print("Got AFS authentication for user_id: " + getProperty("USERID_2") + " for cell: " + getProperty("CELL_2") + " at: " + new Date() + " (new)", V_IMP);
			    }
                        }
                        else
                            print("Got AFS authentication for user_id: " + getProperty("USERID_2") + " for cell: " + getProperty("CELL_2") + " at: " + new Date(), V_IMP);


                        if(success) {
                            numAfsFailures = 0; 
                            lastAuthenticatedTime += AFS_AUTH_DURATION;
                        }
                        else if(numAfsFailures < permittedAfsFailures) {
                            numAfsFailures++;
                        }
                        else {
                            handleException("Could not get AFS authentication despite " + numAfsFailures + " retries");
                        }
                    }*/
                }
            }

            print(hbFormatter.format(new Date(lastHeartbeat))
                  + " "
                  + pingPong
                  + " "
                  + receivedPing
                  + " "
                  + receivedPong
                  + " "
                  + receivedCOA
                  + " "
                  + receivedCOD
                               , PING);

            haltFlag.delete();
        }
        catch(HandledException e) { }

        catch (Throwable e) {
            handleException(e, "About to exit OrderProcessor...");
        }

        finally {

            if(OrderProcessor.mqRecv != null)
                OrderProcessor.mqRecv.cleanup();
            print("Cleaning up and exiting OrderProcessor at " + new Date(), V_IMP);

/*
try {
  execute("/usr/bin/rm -f " + LOGS_DIR + "/MEM_ORDERS/*");
}
catch(Throwable t2) { }
*/

            System.exit(0);

        }
    }


    public static void logReport(String correlationId, String messageId, String feedback) {

        try {
            if(correlationId.equals("EDQ3_PING")) { 
                if(Long.parseLong(messageId) == lastHeartbeat) {
                    if(feedback.equals("COA"))
                        receivedCOA = System.currentTimeMillis() - lastHeartbeat;
                    else if(feedback.equals("COD"))
                        receivedCOD = System.currentTimeMillis() - lastHeartbeat;
                    else
                        handleMinorException("Received unexpected feedback: " + feedback + " for correlationId: " + correlationId + " and messageId: " + messageId);
                }
                else
                    sendPingFeedbackAlert(Long.parseLong(messageId), System.currentTimeMillis(), feedback);
            }
            else {
                String str = feedback + " for " + correlationId + " received at " + new Date() + "\n\n";
// Thread.sleep(1000);
                String filename = getOrderFileName(messageId, LOGS_DIR + "/OrderLogs/");
                print("WWWWfilename "+filename, V_IMP);

/*
int tries = 0;
do {
Thread.yield();
filename = getOrderFileName(messageId, LOGS_DIR + "/OrderLogs/");
tries++;
}
while(filename == null && tries < 10);
*/

                FileOutputStream out = new FileOutputStream(filename, true);
                out.write(str.getBytes());
                out.close();
            }
        }
        catch(Exception e) {
            handleMinorException(e, "thrown processing " + feedback + " for CorrelID: " + correlationId + " MessageID: " + messageId);
        }

    }



    public static void saveOrder(String[]  msgArr) throws IOException {

        String correlationId = msgArr[0];
        String messageId = msgArr[1];
        String msgString = msgArr[2].trim();
                
        int correlationCode = getCorrelationCode(correlationId);

        if(correlationCode == -1)
            handleMinorException(
                                 "MQMessage received with invalid correlationId: " + correlationId + "\n"
                                 + "Message was as follows: " + msgString
                                 );
        else if(msgString.length() == 0)
            handleMinorException(
                                 "MQMessage received with no content.\n" 
                                 + "CorrelationId: " + correlationId + "\n"
                                 );
        else if(correlationCode == GET_PING) {
            try {
                long t3 = System.currentTimeMillis();
                StringTokenizer st = new StringTokenizer(msgString, ",");
                if(st.countTokens() != 2)
                    throw new RuntimeException("Incorrect number of tokens: " + st.countTokens() + " in PING\n"
                                               + "MQ Content: " + msgString);
                long t1 = Long.parseLong(st.nextToken().trim());
                long t2 = Long.parseLong(st.nextToken().trim());
                long t4 = t3 - t1;

                pingVector.remove(new Long(t1));

                if(t1 == lastHeartbeat) {
                    receivedPing = t2 - t1;
                    receivedPong = t3 - t2;
                    pingPong = receivedPing + receivedPong;
                    lastPingReceived = true;
                }
                else
                    print(hbFormatter.format(new Date(t1)) + " " + t4 + " " + (t2 - t1) + " " + (t3 - t2) + " -1 -1", PING_ERR);


                if( t4 > ACCEPTABLE_PING_RT_TIME ) {
                    sendPingAlert(t1, t3);
                }

            }
            catch(Exception e) {
                handleMinorException(e, "thrown processing PING");
            }
        }
        else {

	    msgString += "\n";

            if(correlationCode == AREA_MEM || correlationCode == REPORT_MEM) {
                String orderFileName = messageId + "." + correlationId;
                FileOutputStream order = new FileOutputStream(currentOrdersDir + orderFileName);
                order.write(msgString.toString().getBytes());
                order.close();

                new MQHandler(msgString, messageId, correlationCode, OrderProcessor.props, OrderProcessor.mqProps).start();

                print("Received order (# "
                      + messageId
                      + ") from " 
                      + "NOT_DEFINED"
                      + " for "
                      + orderNames[correlationCode]
                      + " at "
                      + new Date()
                          , V_IMP);
            }
	    else if(correlationCode == DK_INIT || correlationCode == DR_INIT || correlationCode == IPDK_INIT) {
                Properties msg = new Properties();
                ByteArrayInputStream msgInput = new ByteArrayInputStream(msgString.getBytes());
                msg.load(msgInput);
                msgInput.close();
                messageId = msg.getProperty("MESSAGE_ID");
                if(messageId != null)
                    messageId = messageId.trim();

                if(messageId == null || messageId.length() == 0) {
                    print("WARNING! Cherry-picking order without MESSAGE_ID received. Sending email with contents", WARN);

                    String subject = "WARNING! Cherry-picking order without MESSAGE_ID received";
                    String content
                        = "An order (CORR_ID: "
                        + correlationId
                        + ") without an MESSAGE_ID field was received at "
                        + new Date() + "\n\n"
                        + "This order will NOT be processed by exec_OrderProcessor\n\n"
                        + "The text of the order is as follows:\n"
                        + msgString + "\n\n"
                        + "Please follow your usual problem resolution procedure";

                    mail(subject, content);
                    return;
                }


                String orderFileName = messageId + "." + correlationId;
                FileOutputStream order = new FileOutputStream(currentOrdersDir + orderFileName);
                order.write(msgString.toString().getBytes());
                order.close();

                new MQHandler(msg, messageId, correlationCode, OrderProcessor.props, OrderProcessor.mqProps).start();
                print("Received order (# "
                      + messageId
                      + ") from "
                      + msg.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                      + " for "
                      + orderNames[correlationCode]
                      + " at "
                      + new Date()
                          , V_IMP);
            }
            else {
                Properties msg = new Properties();
                ByteArrayInputStream msgInput = new ByteArrayInputStream(msgString.getBytes());
                msg.load(msgInput);
                msgInput.close();
                String orderNumber = msg.getProperty("ORDER_NUMBER");
                if(orderNumber != null)
                    orderNumber = orderNumber.trim();

                    
                if(orderNumber == null || orderNumber.length() == 0) {
                    print("WARNING! Order without ORDER_NUMBER received. Sending email with contents", WARN);
                            
                    String subject = "WARNING! Order without ORDER_NUMBER received";
                    String content 
                        = "An order (CORR_ID: "
                        + correlationId
                        + ") without an ORDER_NUMBER field was received at "
                        + new Date() + "\n\n"
                        + "This order will NOT be processed by exec_OrderProcessor\n\n"
                        + "The text of the order is as follows:\n"
                        + msgString + "\n\n"
                        + "Please follow your usual problem resolution procedure";
                        
                    mail(subject, content);
                    return;
                }



                String filename = getOrderFileName(orderNumber, LOGS_DIR + "/OrderLogs/");

                if(filename != null) {

                    if(orderNumber.length() < 20) {   // if length is less than 20, it is definitely a duplicate order 
                        print("ERROR! Duplicate order with orderNo: " + orderNumber + " received. OrderLogs directory already contains: " + filename, ERR);

                        String subject = "ERROR! Duplicate order received";
                        String content
                            = "A duplicate order (CORR_ID: "
                            + correlationId
                            + ") for ORDER_NUMBER: " + orderNumber + " was received at "
                            + new Date() + "OrderLogs directory already contains: " + filename + "\n\n"
                            + "This order will NOT be processed by exec_OrderProcessor\n\n"
                            + "The text of the order is as follows:\n"
                            + msgString + "\n\n"
                            + "Please follow your usual problem resolution procedure";

                        mail(subject, content);
                        return;
                    }
                    else {
                        print("WARNING! Potential duplicate order with orderNo: " + orderNumber + " received. OrderLogs directory already contains: " + filename, WARN);

                        String subject = "WARNING! Potential duplicate order received";
                        String content
                            = "A potential duplicate order (CORR_ID: "
                            + correlationId
                            + ") for ORDER_NUMBER: " + orderNumber + " was received at "
                            + new Date() + "OrderLogs directory already contains: " + filename + "\n\n"
                            + "Please follow your usual problem resolution procedure";

                        mail(subject, content);
                    }
                }


                String orderFileName = orderNumber + "." + correlationId;
                FileOutputStream order = new FileOutputStream(currentOrdersDir + orderFileName);
                order.write(msgString.toString().getBytes());
                order.close();
                            
                new MQHandler(msg, orderNumber, correlationCode, OrderProcessor.props, OrderProcessor.mqProps).start();

		if( ! orderNumber.startsWith(ignoreUserid) )
                   print("Received order (# "
                      + orderNumber
                      + ") from " 
                      + msg.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                      + " for "
                      + orderNames[correlationCode]
                      + " at "
                      + new Date()
                          , V_IMP);

            }
        }
    }
    


    private static void processShipInfo() {

	try {

	    if(new File(shipInfoPath).length() == 0)
		return;

	    String tmpShipInfoFile = shipInfoPath + ".tmp";

	    if( ! new File(shipInfoPath).renameTo(new File(tmpShipInfoFile)) ) {
		handleMinorException("Rename " + shipInfoPath + " to " + tmpShipInfoFile + " failed");
		return;
	    }
	    
	    String line = null;

	    BufferedReader in = new BufferedReader(new FileReader(tmpShipInfoFile));

	    while((line = in.readLine()) != null) {

		line = line.trim();

		if(line.length() == 0)
		    continue;
		
		StringTokenizer st = new StringTokenizer(line, " ");

                boolean sentInfo = false;
	    
		try {
		    sentInfo = sendShipInfo(st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken(), st.nextToken());
		}
		catch(Exception e) {
		    handleMinorException("thrown by sendShipInfo while processing " + line + "\n"
					 + getStackTrace(e));
		}

                if( ! sentInfo ) {

                    try {
                        line += "\n";
                        FileOutputStream out = new FileOutputStream(shipInfoPath + ".err", true);
                        out.write(line.getBytes());
                        out.close();
                    }
                    catch(Exception ioe) {
		        handleMinorException("thrown while appending " + line + " to file: " + shipInfoPath + ".err\n"
				   	     + getStackTrace(ioe));
                    }

                }

	    }
	    
	    in.close();

	    if( ! new File(tmpShipInfoFile).delete() )
		handleMinorException("Delete " + tmpShipInfoFile + " failed");

	}
	catch(Exception e1) {
	    handleMinorException("thrown while processing " + shipInfoPath + "\n"
				 + getStackTrace(e1));
	}
    }
    
    

    private static boolean sendShipInfo(String orderNumber, String date, String carrier, String track, String status, String emailTo) {

	if(orderNumber == null || orderNumber.trim().length() == 0 || date == null || date.trim().length() == 0 || carrier == null || carrier.trim().length() == 0 || track == null || track.trim().length() == 0 || status == null || emailTo == null) {
	    handleMinorException("One or more of the following required values is empty: " + orderNumber + " " + date + " " + carrier + " " + track);
	    return false;
	}

	Date d = null;
	try {
	    SimpleDateFormat sourceDate = new SimpleDateFormat("MM/dd/yyyy");
	    sourceDate.setLenient(false);
	    d = sourceDate.parse(date);
	}
	catch(ParseException pe) {
	    handleMinorException("Date: " + date + " is invalid or in incorrect format");
	    return false;
	}



	boolean airborne = false;
	boolean international = false;

	String airborneLink = "http://track.airborne.com/atrknav.asp?ShipmentNumber=" + track;

	if(carrier.equalsIgnoreCase("AB")) {
	    airborne = true;
	    airborneLink = "http://track.airborne.com/atrknav.asp?ShipmentNumber=" + track;
	}
	else if(carrier.equalsIgnoreCase("INT"))
	    international = true;

        String orderFileName = getOrderFileName(orderNumber, completedOrdersDir);

	if(orderFileName == null) {
            handleMinorException("No matches for orderNumber: " + orderNumber + " and directory: " + completedOrdersDir);
            return false;
        }

	String correlationId = orderFileName.substring(orderFileName.lastIndexOf('.') + 1);

	int orderType = getCorrelationCode(correlationId);

	if(orderType < 0) {
	    handleMinorException("Invalid Order Type: " + correlationId + " while processing CD ship info");
            return false;
	}

	Properties msg = new Properties();

	try {
	    FileInputStream in = new FileInputStream(orderFileName);
	    msg.load(in);
	    in.close();
	}
	catch(Exception e) {
	    handleMinorException(e, "thrown loading incomplete order to Properties object");
            return false;
	}

        String technology = msg.getProperty("TECHNOLOGY", "");
	String edgeUserid = msg.getProperty("EDGE_USERID", "");
	String orderBy =  msg.getProperty("ORDER_BY", "");

	// String notification = msg.getProperty("NOTIFICATION", "");
	String notification = "email";
        String ccList = null;
	boolean additionalRecipient = true;


        if( emailTo.equals("NA") ) {
	    additionalRecipient = false;
	    emailTo = msg.getProperty("E_MAIL", "");

	    String fseEmail = msg.getProperty("FSE_EMAIL_1", "");

	    if(fseEmail.trim().length() == 0)
	        ccList = msg.getProperty("CC_LIST", "");
	    else
	        ccList = fseEmail + "," + msg.getProperty("CC_LIST", "");
	}


	String from = OrderProcessor.props.getProperty("FROM_ID", "");
	String replyTo = OrderProcessor.props.getProperty("REPLY_TO", "");
        String subject = null;

        if(status.equals("DONE"))
            subject = "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNumber + " has been shipped";
	else
            subject = "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNumber + " is being processed";


        StringBuffer output = new StringBuffer();    

        output.append("EDGE_USERID = " + edgeUserid + "^");

        output.append("ORDER_NUMBER = " + orderNumber + "^");

        output.append("PREFERENCE = " + notification + "^");
        
        output.append("FROM = " + from + "^");
        
        output.append("REPLYTO = " + replyTo + "^");

	output.append("CC_LIST = " + ccList + "^");

        output.append("SUBJECT = " + subject + "^");

	String message = "NONE";
	String messageHTML = "";

        if(status.equals("DONE")) {

          if(notification.equalsIgnoreCase("email")) {

	    message
		= "Dear ASIC Connect customer,\n\n"
                + "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNumber + " was shipped on " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d) + "\n\n";


	    if(additionalRecipient) {
		String proj = msg.getProperty("CUSTOMER_PROJNAME", "");
		message += "You are receiving this CD because you are listed as an additional recipient for project: " + proj + ".\n\n";
	    }
	    else if( ! edgeUserid.equals(orderBy) )
		message += "This order was placed on your behalf by your IBM ASIC support engineer.\n\n";


	    if(airborne)
		message
		    += "You can check the status of this shipment by clicking on the link below:\n"
		    + "\n"
		    + airborneLink + "\n"
		    + "\n"
		    + "For further information, please visit http://track.airborne.com or call 1-800-AIRBORNE\n"
		    + "Please provide the following tracking number: " + track + "\n";

	    else if(international)
		message
		    += "You can check the status of this shipment by contacting " + OrderProcessor.props.getProperty("INT_SHIP_NAME", "Scott Pickel") + "\n"
		    + "email: " + OrderProcessor.props.getProperty("INT_SHIP_EMAIL", "scottpic@us.ibm.com") + "\n"
                    + "Phone: " + OrderProcessor.props.getProperty("INT_SHIP_PHONE", "(802)769-2082") + "\n"
		    + "\n"
		    + "Please provide your order number (" + orderNumber + ") as reference\n";

	    else
		message
		    += "Your order was shipped via " + carrier.replace('_', ' ') + "\n"
		    + "The tracking number for this shipment is " + track + "\n";

	    
	    message 
		+= "\n"
		+ "Thank You,\n"
		+ "ASIC Connect";

          }

          output.append("MESSAGE = " + message + "^");


	  messageHTML
            = "\n<table><tr><td>\n"
            + "<strong>Your ASIC Connect order was shipped on " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d) + "</strong>\n"
            + "<br /><br />\n";


	  if(airborne)
	    messageHTML
		+= "<a href=\""
		+ airborneLink
		+ "\" target=\"_blank\">"
		+ "Track your order"
		+ "</a><br /><br />\n"
		+ "For further information, please visit <a href=\"http://track.airborne.com\" target=\"_blank\">Airborne Express</a> or call 1-800-AIRBORNE<br />\n"
		+ "Please provide the following tracking number: " + track + "\n";

	  else if(international) {
	    String email = OrderProcessor.props.getProperty("INT_SHIP_EMAIL", "scottpic@us.ibm.com");
	    messageHTML
		+= "You can check the status of this shipment by contacting " + OrderProcessor.props.getProperty("INT_SHIP_NAME", "Scott Pickel") + "<br />\n"
		+ "email: " + "<a href=\"mailto:" + email + "\">" + email + "</a> <br />\n"
                + "Phone: " + OrderProcessor.props.getProperty("INT_SHIP_PHONE", "(802)769-2082") + "<br /><br />\n"
		+ "Please provide your order number (" + orderNumber + ") as reference\n";

	  }
	  else
	    messageHTML
		+= "Your order was shipped via " + carrier.replace('_', ' ') + "<br />\n"
		+ "The tracking number for this shipment is " + track + "\n";

	    
	  messageHTML += "</td></tr></table>\n";
	
	  output.append("MESSAGE_HTML = " + messageHTML + "^");

        } 


        else {  // if order is still pending

          if(notification.equalsIgnoreCase("email")) {

	    message
		= "Dear ASIC Connect customer,\n\n"
                + "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNumber + " is estimated to ship on " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d) + "\n\n"
	        + "We will notify you when after your order has been shipped.\n"
		+ "\n";


	    if(additionalRecipient) {
		String proj = msg.getProperty("CUSTOMER_PROJNAME", "");
		message += "You are receiving this CD because you are listed as an additional recipient for project: " + proj + ".\n\n";
	    }
	    else if( ! edgeUserid.equals(orderBy) )
		message += "This order was placed on your behalf by your IBM ASIC support engineer.\n\n";


	    message
	       += "Thank You,\n"
		+ "ASIC Connect";

          }

          output.append("MESSAGE = " + message + "^");


	  messageHTML
            = "\n<table><tr><td>\n"
            + "<strong>Your ASIC Connect order is estimated to ship on " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(d) + "</strong>\n"
            + "<br /><br />\n"
	    + "This page will be updated after your order has been shipped.\n"
	    + "</td></tr></table>\n";
	
	  output.append("MESSAGE_HTML = " + messageHTML + "^");

       }



/*
        if( emailTo.equals("NA") ) {

	  try {

            String filename = orderNumber + "." + correlationId;

	    sendMQMessage("EDQ3_CD_SHIPPED", orderNumber, output.toString(), true, true, true);

	    String str = "Sent MQ with correlation ID: EDQ3_CD_SHIPPED\n" + "MESSAGE = " + message + "^\n" + "MESSAGE_HTML = " + messageHTML + "^\n";
	    FileOutputStream out = new FileOutputStream(LOGS_DIR + "/OrderLogs/" + filename, true);
	    out.write(str.getBytes());
	    out.close();

            return true;
	  }
	  catch(Throwable t) {
	      handleMinorException(t, "thrown sending MQ with correlation ID: EDQ3_CD_SHIPPED");
	  }

          return false;

        }

        else {
*/

          try {

            String filename = orderNumber + "." + correlationId;

	    String bcc = null;
	    if(sendBccForCDmails)
	        bcc = OrderProcessor.props.getProperty("EDSD_DEV_1");

            mail(from, emailTo, ccList, bcc, replyTo, subject, message);

            String str = "Sent mail to: " + emailTo + " cc: " + ccList + " with subject: " + subject + "\nMessage:\n" + message;

            FileOutputStream out = new FileOutputStream(LOGS_DIR + "/OrderLogs/" + filename, true);
            out.write(str.getBytes());
            out.close();

            return true;
          }
          catch(Throwable t) {
              handleMinorException(t, "thrown sending mail to: " + emailTo + " cc: " + ccList + " with subject: " + subject);
          }

          return false;

    }



    private static void processIncompleteOrders() {

        print("Starting processIncompleteOrders()...", V_IMP);
        
        String[] files = new File(currentOrdersDir).list();

        if(files == null)
            handleMinorException(currentOrdersDir + " is not a valid directory name");

        else if(files.length == 0)
            print("No incomplete orders from previous run", V_IMP);

        else {
            for(int i = 0; i < files.length; i++) {
                
                int index = files[i].lastIndexOf('.');
                String orderNumber = files[i].substring(0, index);
                String correlationId = files[i].substring(index+1);
                int correlationCode = getCorrelationCode(correlationId);

                if(correlationCode == AREA_MEM || correlationCode == REPORT_MEM) {
                    new File(currentOrdersDir + files[i]).delete();
                    continue;
                }


                Properties msg = new Properties();

                try {
                    FileInputStream msgInput = new FileInputStream(currentOrdersDir + files[i]);
                    msg.load(msgInput);
                    msgInput.close();
                }
                catch(Exception e) {
                    handleMinorException(e, "thrown loading incomplete order to Properties object");
                    continue;
                }


                new MQHandler(msg, orderNumber, correlationCode, OrderProcessor.props, OrderProcessor.mqProps).start();
               
                print("Resuming processing of incomplete order (# "
                      + orderNumber
                      + ") from " 
                      + msg.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                      + " for "
                      + orderNames[correlationCode]
                      + " at "
                      + new Date()
                          , V_IMP);
            }
        }
        print("Exiting processIncompleteOrders()...", V_IMP);
    }



    private static void checkRetryDir(String dir) {

        if( ! dir.endsWith("/") )
            dir += "/";

        String[] files = new File(dir).list();

        if(files == null || files.length == 0)
            return;
        else {

            for(int i = 0; i < files.length; i++) {

                int index = files[i].lastIndexOf('.');
                String orderNumber = files[i].substring(0, index);
                String correlationId = files[i].substring(index+1);
                int correlationCode = getCorrelationCode(correlationId);


                if(correlationCode == AREA_MEM || correlationCode == REPORT_MEM) {
                    new File(dir + files[i]).delete();
                    continue;
                }


                String cmd = "/usr/bin/mv -f " + dir + files[i] + " " + currentOrdersDir;
                int ev = execute(cmd);

                if(ev != 0) {
                    handleMinorException(cmd + " returned an exit value of " + ev);
                    continue;
                }

                Properties msg = new Properties();

                try {
                    FileInputStream msgInput = new FileInputStream(currentOrdersDir + files[i]);
                    msg.load(msgInput);
                    msgInput.close();
                }
                catch(Exception e) {
                    handleMinorException(e, "thrown loading incomplete order to Properties object");
                    continue;
                }


                new MQHandler(msg, orderNumber, correlationCode, OrderProcessor.props, OrderProcessor.mqProps).start();

                print("Retrying processing of failed order (# "
                      + orderNumber
                      + ") from " 
                      + msg.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                      + " for "
                      + orderNames[correlationCode]
                      + " at "
                      + new Date()
                          , V_IMP);
            }
        }
    }



    private static String getOrderFileName(String prefix, String dir) {

        try {
            prefix = prefix.trim() + ".";
            dir = dir.trim();

            if( ! dir.endsWith("/"))
                dir += "/";

            int matches = 0;
            String filename = null;

            String[] list = new File(dir).list();

            if(list == null)
                return null;

            for(int i = 0; i < list.length; i++)
                if(list[i].startsWith(prefix)) {
                    filename = dir + list[i];
                    matches++;
                }

            if(matches == 1)
                return filename;
            else if (matches == 0)
                return null;
            else {
                handleMinorException("Matches: " + matches + " for prefix: " + prefix + " and directory: " + dir);
                return null;
            }

        }
        catch(Exception e) {
                handleMinorException(e, "thrown getting OrderFileName for prefix: " + prefix + " and directory: " + dir);
                return null;
        }

    }



    private static void sendSeriousPingAlert(long t) {

        String msg 
            = "IMPORTANT: An MQ ping sent at " + new Date(t) + " has NOT yet been received at " + new Date();

        print(msg, WARN);

        mail("IMPORTANT: Serious MQ problem on " + hostname, msg);

    }



    private static void sendPingAlert(long t) {

        String msg 
            = "An MQ ping sent at " + new Date(t) + " has NOT yet been received at " + new Date();

        if(receivedCOA == -1)
            msg += "\nCOA has NOT been received";
        else
            msg += "\nCOA was received in " + receivedCOA + " ms";

        if(receivedCOD == -1)
            msg += ", COD has NOT been received";
        else
            msg += ", COD was received in " + receivedCOD + " ms";

        // print(msg, WARN);

        mailPingAlert("Ping Alert from " + hostname, msg);

    }





    private static void sendPingAlert(long t1, long t2) {

        String msg 
            = "An MQ ping sent at " + new Date(t1) + " was received at " + new Date(t2) + "\n"
            + "The round trip time was " + ( (t2 - t1) / 1000 ) + " seconds.";

        // print(msg, WARN);

        mail("Ping Alert from " + hostname, msg);

    }



    private static void sendPingFeedbackAlert(long t1, long t2, String feedback) {

        String msg 
            = feedback + " for an MQ ping sent at " + new Date(t1) + " was received at " + new Date(t2) + "\n"
            + "The round trip time was " + ( (t2 - t1) / 1000 ) + " seconds.";

        print(hbFormatter.format(new Date(t1)) + " " + (t2 - t1) + " " +  feedback, PING_ERR);
        // print(msg, PING_ERR);

        // mail("Ping Alert from " + hostname, msg);

    }



    private static int getCorrelationCode(String correlationId) {
      
        if(correlationId != null) {
            correlationId = correlationId.trim();
            
            for(int i = 0; i < CORR_ID.length; i++)
               if(correlationId.equals(CORR_ID[i]))
                  return i;
            
        }
        return -1;
    } 



    
    static synchronized void print(String str, int importance) {
        str += "\n\n";

        if(importance == ERR)
            str 
                = "\n\n************************* !!! ERROR !!! *************************\n"
                + "Printed at: " + new Date() + "\n"
                + str
                + "\n\n";


        if(importance == WARN)
            str 
                = "\n\n************************ !!! Warning !!! ************************\n"
                + "Printed at: " + new Date() + "\n"
                + str
                + "\n\n";

        String outFile;
        
        if(importance == ERR || importance == WARN || importance == PING_ERR)
            outFile = ERR_LOG_FILE;
        
        else if(importance == HEARTBEAT)
            outFile = HEARTBEAT_LOG_FILE;

        else if(importance == PING)
            outFile = PING_LOG_FILE;
        
        else
            outFile = MAIN_LOG_FILE;


        if(importance == HEARTBEAT || importance == PING || importance <= V_IMP  || importance <= DISPLAY_LEVEL) {
            
          //    if(OrderProcessor.AFSauthenticated) {
                try {
                    FileOutputStream out = new FileOutputStream(outFile, true);   // append str to log file
                    out.write(str.getBytes());
                    out.close();
                }
                catch(Throwable e) {
                    System.out.println(
                                       "\n\n\n************************* !!! ERROR !!! *************************\n"
                                       + "WARNING!: Error writing to " + outFile + " at " 
                                       + new Date() + "\n"
                                       + "Stack Trace:\n" 
                                       + getStackTrace(e) + "\n"
                                       + "While writing:\n" 
                                       + str + "\n"
                                       + "*****************************************************************\n\n"
                                       );
                }
               //   }
               // else
                System.out.println(str);
        }
    }




    private static synchronized void updateDailyLogs(String date) {

        for(int i = 0; i < 6; i++) {
            String filename = null;
            switch(i) 
                {
                case 0: 
                    filename = MAIN_LOG_FILE;
                    break;
                case 1: 
                    filename = PERF_LOG_FILE;
                    break;
                case 2: 
                    filename = ERR_LOG_FILE;
                    break;
                case 3:
                    filename = HEARTBEAT_LOG_FILE;
                    break;
                case 4: 
                    filename = MQ_LOG_FILE;
                    break;
                case 5:
                    filename = PING_LOG_FILE;
                    break;
                default:
                    handleException("invalid switch: " + i + " in updateDailyLogs");
                }

            File f = new File(filename);
            if(f.length() != 0) {

                String archiveDir = filename + "Dir";
                File arDir = new File(archiveDir);

                if( ! arDir.isDirectory())
                    if( ! arDir.mkdirs())
                        handleException("Directory: " + archiveDir + " could not be created");

                String archiveFile = archiveDir + filename.substring(filename.lastIndexOf('/'));


                if(date == null) {
                    Date d = new Date(f.lastModified());
                    Calendar c = Calendar.getInstance();

                    int dayOfYearNow = c.get(Calendar.DAY_OF_YEAR);
                    c.setTime(d);
                    int dayOfYearThen = c.get(Calendar.DAY_OF_YEAR);

                    if(dayOfYearNow == dayOfYearThen)
                        continue;

                    date = formatter.format(d);
                }

                if( ! f.renameTo(new File(archiveFile + "." + date)))
                    handleMinorException("Failed to rename " + filename + " to " + archiveFile + "." + date);

            }
        }
    }



    static synchronized void writePerfLog(String perfString) {
        perfString += "\n";
        try {
            FileOutputStream perfFile = new FileOutputStream(PERF_LOG_FILE, true);
            perfFile.write(perfString.getBytes());
            perfFile.close();
        }
        catch(Throwable t) {
            handleMinorException(t, "thrown writing performance log");
        }
    }
  




    static synchronized void sendMQMessage(String corrId, String messageId, String message, boolean persistent) throws Throwable {

        OrderProcessor.mqSend.sendMQMessage(corrId, messageId, message, persistent);

    }




    static int execute(String cmd) {

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
            catch(Throwable ioe) {
                handleMinorException(ioe, "thrown reading output from: " + cmd);
            }

            String str 
                = cmd + " returned an exit Value of " + exitValue + "\n"
                + "stdout: " + inString + "\n"
                + "stderr: " + errString + "\n";

            if(exitValue != 0)
                handleMinorException(str);
            else
                print(str, NOT_IMP);
            
            return exitValue;
        }
        catch(Throwable e) {
            handleException(e, "thrown executing: " + cmd);
            return exitValue;
        }
    }




    private static String executeAndGetStream(String cmd) throws IOException, InterruptedException {

        String inString = "";
        String errString = "";
        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;
        String[] command = {"/bin/ksh", "-c", cmd};

        Process p = Runtime.getRuntime().exec(command);
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

        String str 
            = cmd + " returned an exit Value of " + exitValue + "\n"
            + "stdout: " + inString + "\n"
            + "stderr: " + errString + "\n";

        if(exitValue != 0)
	    handleMinorException(str);
        
        return inString;

    }




    private static String getProperty(String key) {
        String value = OrderProcessor.props.getProperty(key);
        if(value != null)
            value = value.trim();
        String defaultValue = OrderProcessor.defaultProps.getProperty(key);
        if(defaultValue != null)
            defaultValue = defaultValue.trim();
        if(defaultValue == null || defaultValue.length() == 0) {
            defaultValue = "";
            handleMinorException("WARNING!: Key: " + key + " not defined in default properties string(in code)");
        }
        if(value == null || value.length() == 0) {
            handleMinorException("WARNING!: Key: " + key + " not found in properties file. Using default value: " + defaultValue);
            return defaultValue;
        }
        else
            return value;
    }


    static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }



    static void handleException(String str) {
        handleException(new RuntimeException(), str);
    }


    static void handleException(Throwable t, String str) {

        if(t.getClass().getName().endsWith("HandledException"))
            throw new HandledException();

        else {
            String stackTrace = getStackTrace(t);
            str += "\n\nStackTrace:\n" 
                + stackTrace 
                + "\n\n" 
                + "This exception was thrown at: " 
                + new Date();
            
            print(str, ERR);

            String subject = "ORDER_PROCESSOR DOWN on " + hostname + "!!!";
            String content 
                = "Dear IBM Customer Connect Developer,\n\n" 
                + "Despite all those nights you stayed up debugging,\n"
                + "OrderProcessor just crashed!\n\n"
                + "The Exception was thrown as follows:\n\n" 
                + str + "\n\n"
                + "This is what you get for working at IBM!\n\n"
                + "Please follow your very long and miserable problem resolution procedure\n"
                + "to attempt in vain to correct the problem.\n\n"
                + "Have a great time debugging. You asked for it!\n";
            
            try {
                mail(subject, content);
            }
            catch(Throwable th) {
                print(getStackTrace(th), ERR);
            }
            
            throw new HandledException();
        }
    }



    static void handleMinorException(String str) {
        handleMinorException(new RuntimeException(), str);
    }


    static void handleMinorException(Throwable t, String str) {

        if(t.getClass().getName().endsWith("HandledException"))
            return;

        else {
            String stackTrace = getStackTrace(t);
            str += "\n\nStackTrace:\n" 
                + stackTrace 
                + "\n\n" 
                + "This exception was thrown at: " 
                + new Date();

            print(str, WARN);

            try {
                mail("Warning from OrderProcessor", str);
            }
            catch(Throwable t1) {
                print(getStackTrace(t1), ERR);
            }
        }

    }
 

   
    static synchronized int updateDB2(String sql) throws SQLException {

        if( ! useDB2)
            return 1;

        Connection con = DriverManager.getConnection(db2Url, db2User, db2Pwd);
        Statement stmt = con.createStatement();
        int rowCount = stmt.executeUpdate(sql);
        stmt.close();
        con.close();
        return rowCount;

    }


    private static void mail(String subject, String body) {
        String cc = OrderProcessor.props.getProperty("EDSD_DEV_2");
	mail(cc, subject, body);
    }


    private static void mailPingAlert(String subject, String body) {
        String cc = OrderProcessor.props.getProperty("PING_ALERT_EMAIL");
	mail(cc, subject, body);
    }


    private static void mail(String cc, String subject, String body) {

        if( ! MAIL_ON_ERROR)
            return;

        String to = OrderProcessor.props.getProperty("EDSD_DEV_1");
        String from = "EDSD_ERROR@us.ibm.com";
        String replyTo = null;

        if(to == null || to.trim().length() == 0)
            return;

        if(cc != null && cc.trim().length() == 0)
            cc = null;

        mail(from, to, cc, replyTo, subject, body);

    }



    private static void mail(String from, String to, String cc, String replyTo, String subject, String body) {
	mail(from, to, cc, null, replyTo, subject, body);
    }

    private static void mail(String from, String to, String cc, String bcc, String replyTo, String subject, String body) {
        
        String mailString 
            = "FROM: "      + from 
            + "\nTO: "      + to
            + "\nCC: "      + cc
            + "\nSUBJECT: " + subject
            + "\nBODY: "    + body;


        long sleepTime = 5*1000;

        boolean mailSent = false;

        int numRetries = 2;

        String[] emailHost = {mailHost1, mailHost2, mailHost3};

        int numHosts = emailHost.length;

        mailBlock: for(int i = 0; i <= numRetries; i++) {

            for(int j = 0; j < numHosts; j++) {
                
                try {
                    Mailer.sendMail(emailHost[j], from, to, cc, bcc, replyTo, subject, body);
                    mailSent = true;
                    break mailBlock;
                }
                catch(Throwable t) {
                    String str = 
                        "thrown while trying to send email (attempt# "
                        + ( (i*numHosts) + j + 1 )
                        + ") as follows:\n" 
                        + mailString + "\n\n" 
                        + "StackTrace:\n" + getStackTrace(t) + "\n\n"
                        + "Will Re-try " + ( (numRetries - i) * numHosts + (numHosts - 1 - j) ) + " times\n\n"
                        + "This error was thrown at: " + new Date();
                    print(str, ERR);
                }
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch(Throwable t) {
                String str = 
                    "thrown while WAITING to re-send email\n" 
                    + "StackTrace:\n" + getStackTrace(t) + "\n\n"
                    + "This error was thrown at: " + new Date();
                print(str, ERR);
            }
        }

        if( ! mailSent) {
            String str
                = "ERROR: The following email could NOT be sent despite " 
                + ( (numRetries + 1) * numHosts)
                + " attempts:\n"
                + mailString;

            print(str, ERR);
        }
    }
}
