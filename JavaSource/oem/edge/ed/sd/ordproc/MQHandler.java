package oem.edge.ed.sd.ordproc;

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




import oem.edge.ed.util.PasswordUtils;
import oem.edge.ed.util.Base64;
import oem.edge.ed.util.SearchEtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;
import java.util.Vector;
import java.util.Random;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.lang.reflect.Field;
import java.net.URL;
import java.io.*;
import COM.ibm.db2.app.*;  
import java.sql.Clob;
import java.sql.*;


import oem.edge.ed.odc.dropbox.common.FileInfo;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.service.helper.*;
import oem.edge.ed.odc.dsmp.common.DboxException;


public class MQHandler extends Thread{

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private static boolean transientMsgPersistent = false;
  //  private static boolean transientMsgCOA = true;
   // private static boolean transientMsgCOD = true;

    private String corrID;
    private int orderType;

    private String orderFileName;

    private String edgeUserid, orderNumber, customerProjname, asicCodeName, customerChipname, notification, fseEmail, technology, versionNo, firstName, lastName, usersEmail, usersCompany, messageStr, orderBy;

    private String tdofString;

    private long timestamp, expirationTime;

    private boolean cd,dl;          // if CD and DL tags are 0 or 1
    String[] patchList, patchDates, platformsList, shortplatformsList;

    private boolean individualDeltas;

    boolean useSmartDownload = true;

    private boolean useAutoLicGen;

    private StringBuffer perfString = new StringBuffer();       // for performance logging
    private StringBuffer displayString = new StringBuffer();    // for message display

    private boolean toolKit; // if customer requested for a toolkit
    private boolean toolkitOnly = false;
    private boolean baseModelKit; // if shipit returned a custom model kit
    private boolean customModelKit; // if shipit returned a custom model kit
    private boolean majorAndDelta = false;
    private boolean tkFix = false;
	private boolean tkAddlComponent = false;
    private int modelKitWait = 0;   // time waited for model kit to show up

    private boolean isPrimaryCustomer = true;
    private int addShipTo;

    private int numCustomModelKits = 0;
    private int numTkFix = 0;
    private int numTkAddlComponent = 0;
    private int numDR = 0; //6.1 rel CSR10120

    private Properties message;
    private Properties ordprocProps;
    private Properties mqProps;

    private static Properties defaultOrdprocProps;
    private static Properties defaultMqProps;
    private static String LOGS_DIR;

    private static long LAST_MEM_ORDER_NUMBER;
    private static long LAST_EFPGA_ORDER_NUMBER;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(" MM/dd/yy HH:mm:ss ");   // for performance logging

    private static final SimpleDateFormat customerFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm z");    // to show customer expiration date

    private static final SimpleDateFormat licReqFormatter = new SimpleDateFormat("MM/dd/yy");    // for license requests

    private static final SimpleDateFormat DRsourceDate = new SimpleDateFormat("M/d/yyyy");      // for delta release date

    private static final SimpleDateFormat DRtargetDate = new SimpleDateFormat("MMMM d, yyyy");  // for delta release date

    private static final NumberFormat numberFormat = NumberFormat.getInstance();

    private static final String TESTING_APPEND_STRING = "IBM Customer Connect Test: PLEASE IGNORE! ";

    private static final long DAYS = 24 * 60 * 60 * 1000;

    private static final long EXP_DURATION = 5 * DAYS;

    private static final Random randomGen = new Random();

    private String SHIPIT_MODE = "ICCPROD";
    // DEV or PROD

    private String COPY_ORDER_DIR = "";
    
    //////////  Dropbox ///////
    private boolean isDropbox; //if dropbox="yes", isDropbox = true;
	private String[] uploadFileNames;
    
   //////////////////////


    //============MessageDisplay levels============================
    static final int ERR          =-1;
    static final int WARN         = 0;
    static final int V_IMP        = 1;
    static final int IMP          = 2;
    static final int NOT_IMP      = 3;



    //============Output Display Modes============================
    static final int SILENT              = 0;  // used for CHECK_AFS_SPACE_SCRIPT
    static final int NON_ZERO_EXIT_VALUE = 1;  // used for all other commands
    static final int VERBOSE             = 2;  // used for SHIPIT



    //=====================  KIT TYPES  ============================
    private static final int DESIGN       = OrderProcessor.DESIGN;
    private static final int PREVIEW      = OrderProcessor.PREVIEW;
    private static final int DELTA        = OrderProcessor.DELTA;
    private static final int FULLPD       = OrderProcessor.FULLPD;
    private static final int LIC_TOOL     = OrderProcessor.LIC_TOOL;
    private static final int LIC_FULLPD   = OrderProcessor.LIC_FULLPD;
    private static final int LIC_XMX      = OrderProcessor.LIC_XMX;
    private static final int DSE          = OrderProcessor.DSE;
    private static final int XMX          = OrderProcessor.XMX;
    private static final int MEM          = OrderProcessor.MEM;
    private static final int AREA_MEM     = OrderProcessor.AREA_MEM;
    private static final int REPORT_MEM   = OrderProcessor.REPORT_MEM;

    private static final int DK_INIT      = OrderProcessor.DK_INIT;
    private static final int DR_INIT      = OrderProcessor.DR_INIT;
  //new for 4.5.1
    private static final int EFPGA        = OrderProcessor.EFPGA;
  //new for 5.1.1
    private static final int IPDK_INIT    = OrderProcessor.IPDK_INIT;
    private static final int IPDESIGN     = OrderProcessor.IPDESIGN;
  //end of new for 5.1.1
    //=====================  MAIL TYPES  ============================
    private static final int UNEXP_ERROR      = 1;

    static final int UNEXP_WARNING    = 2;

    private static final int SHIPIT_FAIL      = 3;
    private static final int NO_MK_WAIT_1     = 4;
    private static final int NO_MK_WAIT_2     = 5;
    private static final int NO_MK_STOP       = 6;
    private static final int SHIPIT_SPACE_ERR = 7;

    private static final int FSE_REQ_LIC      = 10;
    private static final int SM_REQ_CD        = 11;
    private static final int SM_REQ_ALOC      = 12;
    private static final int ALOC_DR_ORD      = 13;
    private static final int DROPBOX_ERR_MAIL     = 14;


    //===================================================================
    // Fields for various parameters from orproc properties
    // Will be reset from edesign_edsd_ordproc.properties
    // The parent directory of this file must be in the runtime classpath

    private int DISPLAY_LEVEL;

    private int SHIPIT_WAIT_TIME;    // minutes

    //  shipit wait times in days
    private int CRITICAL_DAYS_1;
    private int CRITICAL_DAYS_2;
    private int CRITICAL_DAYS_3;

    private static String EDESIGN_HOME_DIR;
    private static String CHIPS_DL_DIR;
    private static String LINKS_DIR;
    private static String DISPLAY_DIR;
    private static String CURRENT_ORDERS_DIR;
    private static String COMPLETED_ORDERS_DIR;
    private static String FAILED_ORDERS_DIR;
    private static String RETRY_ORDERS_DIR;
    private static String TDOF_DIR;
    private static String MEM_ORDERS_DIR;
    private static String MEM_STARTED_DIR;
    private static String EFPGA_ORDERS_DIR;
    private static String EFPGA_STARTED_DIR;
    private static String EFPGA_INCOMING_DIR;
    private static String AUTO_LIC_DIR;
    private static String CHIPS_CUSTOM_BINS_DIR;

    private static String AUTO_LIC_SCRIPT;


    private static String lastReleaseDataFile;
    private static long lastUpdatedLRD;
    private static String[][] lastReleaseDataInfo;

    private static String ignoreAsicCodenameFile;
    private static long lastUpdatedIAC;
    private static String[] ignoreAsicCodenames;

    private static String ignoreEdgeUseridsFile;
    private static long lastUpdatedIEU;
    private static String[] ignoreEdgeUserids;


    private String URL_PREFIX;

    private String SHIPIT_SCRIPT;
    private String MK_SIZE_SCRIPT;
    private String COPY_ORDER_SCRIPT;

    private String GET_TOOLS_SCRIPT;
    private String MERGE_TOOLS_SCRIPT;

    private String CHECK_AFS_SPACE_SCRIPT;

    private String SEND_MQ_OR_EMAIL;
    // can be "mq" or "email" or "both";

    private String TESTING;
    // will append TESTING_APPEND_STRING only if value = "yes"


    private int EMAIL_NUM_RETRIES;
    private String EMAIL_HOST_NAME;
    private String BACKUP_1_EMAIL_HOST_NAME;
    private String BACKUP_2_EMAIL_HOST_NAME;
    private String FROM_ID;
    private String REPLY_TO;
    private String EDSD_DEV_1;
    private String EDSD_DEV_2;
    //=======================================================================
   private Hashtable tkplatform;
   
   private Hashtable tkPlatformValues;



    /**********************************************************************************************************************************************************************************************************************************/



    static void setProperties(String LOGS_DIR, Properties defaultOrdprocProps, Properties defaultMqProps, String EDESIGN_HOME_DIR, String CHIPS_DL_DIR) {

        // the lines below are required as otherwise the dates might get formatted in Pacific Time
        formatter.setCalendar(Calendar.getInstance());
        customerFormatter.setCalendar(Calendar.getInstance());
        licReqFormatter.setCalendar(Calendar.getInstance());
        DRsourceDate.setCalendar(Calendar.getInstance());
        DRtargetDate.setCalendar(Calendar.getInstance());

        numberFormat.setMaximumFractionDigits(3);

        MQHandler.LOGS_DIR = LOGS_DIR;
        MQHandler.defaultOrdprocProps = defaultOrdprocProps;
        MQHandler.defaultMqProps = defaultMqProps;

        MQHandler.EDESIGN_HOME_DIR     = EDESIGN_HOME_DIR;
        MQHandler.CHIPS_DL_DIR         = CHIPS_DL_DIR;
        MQHandler.CHIPS_CUSTOM_BINS_DIR= defaultOrdprocProps.getProperty("CHIPS_CUSTOM_BINS_DIR");
        MQHandler.LINKS_DIR            = CHIPS_DL_DIR + "ASICTECH/";

        MQHandler.DISPLAY_DIR          = LOGS_DIR + "/OrderLogs/";
        MQHandler.CURRENT_ORDERS_DIR   = LOGS_DIR + "/CurrentOrders/";
        MQHandler.COMPLETED_ORDERS_DIR = LOGS_DIR + "/CompletedOrders/";
        MQHandler.FAILED_ORDERS_DIR    = LOGS_DIR + "/FailedOrders/";
        MQHandler.RETRY_ORDERS_DIR     = LOGS_DIR + "/retry/";
        MQHandler.TDOF_DIR             = LOGS_DIR + "/TDOF/";
        MQHandler.MEM_ORDERS_DIR       = LOGS_DIR + "/MEM_ORDERS/";
        MQHandler.MEM_STARTED_DIR      = LOGS_DIR + "/MEM_STARTED_DIR/";
        MQHandler.AUTO_LIC_DIR         = LOGS_DIR + "/AUTO_LIC_ORDERS/";
       //new for 4.5.1
        MQHandler.EFPGA_ORDERS_DIR     = LOGS_DIR + "/EFPGA_ORDERS/";
        MQHandler.EFPGA_STARTED_DIR    = LOGS_DIR + "/EFPGA_STARTED_DIR/";
        MQHandler.EFPGA_INCOMING_DIR   = LOGS_DIR + "/EFPGA_INCOMING/";


        String[] dirs = {
            DISPLAY_DIR,
            CURRENT_ORDERS_DIR,
            COMPLETED_ORDERS_DIR,
            FAILED_ORDERS_DIR,
            RETRY_ORDERS_DIR,
            TDOF_DIR + "CD",
            TDOF_DIR + "DL",
            MEM_ORDERS_DIR,
            MEM_STARTED_DIR,
            AUTO_LIC_DIR,
            EFPGA_ORDERS_DIR,
            EFPGA_STARTED_DIR,
            EFPGA_INCOMING_DIR, //new for 4.5.1, comment out for 5.3, since it is on afs, not gsa
            CHIPS_CUSTOM_BINS_DIR
        };

        File f;

        for(int i = 0; i < dirs.length; i++) {

            f = new File(dirs[i]);

            if( ! f.isDirectory())
                if( ! f.mkdirs())
                    OrderProcessor.handleException("Directory: " + f.toString() + " could not be created");

        }

        lastReleaseDataFile = EDESIGN_HOME_DIR + "TechInfo/last.release.data";
        ignoreAsicCodenameFile = EDESIGN_HOME_DIR + "TechInfo/test_asic_codenames";
        ignoreEdgeUseridsFile = EDESIGN_HOME_DIR + "TechInfo/test_edge_userids";

    }



    /**********************************************************************************************************************************************************************************************************************************/




    MQHandler(Properties message, String orderNumber, int orderType, Properties ordprocProps, Properties mqProps) {
        this.message = message;
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.ordprocProps = ordprocProps;
        this.mqProps = mqProps;

        //new for 3.7.1
        tkplatform = new Hashtable();

        tkplatform.put("aix5_64", "aix564");
        tkplatform.put("aix64", "aix64");
        tkplatform.put("aix", "aix");
        tkplatform.put("hp64", "hp64");
        tkplatform.put("hp", "hp");
        tkplatform.put("solaris64", "sol64");
        tkplatform.put("solaris", "sol");
        tkplatform.put("linux32", "linux");
        tkplatform.put("linux_amd64", "linuxamd64");
        
		tkPlatformValues = new Hashtable();

		tkPlatformValues.put("aix564","aix5_64");
		tkPlatformValues.put("aix64","aix64");
		tkPlatformValues.put("aix","aix");
		tkPlatformValues.put("hp64","hp64");
		tkPlatformValues.put("hp","hp");
		tkPlatformValues.put("sol64","solaris64");
		tkPlatformValues.put("sol","solaris");
		tkPlatformValues.put("linux","linux32");
		tkPlatformValues.put("linuxamd64","linux_amd64");
        //end of new for 3.7.1
    }


    /**********************************************************************************************************************************************************************************************************************************/



    MQHandler(String messageStr, String messageId, int orderType, Properties ordprocProps, Properties mqProps) {
        this.message = new Properties();
        this.messageStr = messageStr;
        this.orderNumber = messageId;
        this.orderType = orderType;
        this.ordprocProps = ordprocProps;
        this.mqProps = mqProps;


        //new for 3.7.1
        tkplatform = new Hashtable();

        tkplatform.put("aix5_64", "aix564");
        tkplatform.put("aix64", "aix64");
        tkplatform.put("aix", "aix");
        tkplatform.put("hp64", "hp64");
        tkplatform.put("hp", "hp");
        tkplatform.put("solaris64", "sol64");
        tkplatform.put("solaris", "sol");
        tkplatform.put("linux32", "linux");
        tkplatform.put("linux_amd64", "linuxamd64");
        
		tkPlatformValues = new Hashtable();

		tkPlatformValues.put("aix564","aix5_64");
		tkPlatformValues.put("aix64","aix64");
		tkPlatformValues.put("aix","aix");
		tkPlatformValues.put("hp64","hp64");
		tkPlatformValues.put("hp","hp");
		tkPlatformValues.put("sol64","solaris64");
		tkPlatformValues.put("sol","solaris");
		tkPlatformValues.put("linux","linux32");
		tkPlatformValues.put("linuxamd64","linux_amd64");
       //end of new for 3.7.1
    }


    /**********************************************************************************************************************************************************************************************************************************/



    private void initializeThread() throws NoSuchFieldException, IllegalAccessException {

        this.corrID = OrderProcessor.CORR_ID[this.orderType];
        this.orderFileName = this.orderNumber + "." + this.corrID;



        String[] stringParameters = {
            "SEND_MQ_OR_EMAIL",
            "TESTING",
            "EMAIL_HOST_NAME",
            "BACKUP_1_EMAIL_HOST_NAME",
            "BACKUP_2_EMAIL_HOST_NAME",
            "FROM_ID",
            "REPLY_TO",
            "EDSD_DEV_1",
            "EDSD_DEV_2",
            "URL_PREFIX",
            "AUTO_LIC_SCRIPT",
            "SHIPIT_SCRIPT",
            "MK_SIZE_SCRIPT",
            "COPY_ORDER_SCRIPT",
	    "GET_TOOLS_SCRIPT",
            "CHECK_AFS_SPACE_SCRIPT",
            "MERGE_TOOLS_SCRIPT"
        };

        String[] intParameters = {
            "DISPLAY_LEVEL",
            "SHIPIT_WAIT_TIME",
            "CRITICAL_DAYS_1",
            "CRITICAL_DAYS_2",
            "CRITICAL_DAYS_3",
            "EMAIL_NUM_RETRIES"
        };

        Class c = this.getClass();
        Field f;
        String value;

        for(int i = 0; i < stringParameters.length; i++) {
            f = c.getDeclaredField(stringParameters[i]);
            f.set(this, getProperty(stringParameters[i]));
        }

        for(int i = 0; i < intParameters.length; i++) {
            f = c.getDeclaredField(intParameters[i]);
            f.setInt(this, Integer.parseInt(getProperty(intParameters[i])));
        }

        if(URL_PREFIX.endsWith("/"))
            URL_PREFIX = URL_PREFIX.substring(0, URL_PREFIX.length() - 1);


        if(getProperty("USE_SMART_DOWNLOAD").equalsIgnoreCase("no")) {
            this.useSmartDownload = false;
        }

	this.COPY_ORDER_DIR = COPY_ORDER_SCRIPT.substring(0, COPY_ORDER_SCRIPT.lastIndexOf('/'));

// if(TESTING.equalsIgnoreCase("yes"))
// this.SHIPIT_MODE = "DEV";

        print("Initialized MQHandler thread", V_IMP);
    }




    /**********************************************************************************************************************************************************************************************************************************/



    String getProperty(String key) {
        String value = this.ordprocProps.getProperty(key);
        if(value != null)
            value = value.trim();
        String defaultValue = this.defaultOrdprocProps.getProperty(key);
        if(defaultValue != null)
            defaultValue = defaultValue.trim();
        if(defaultValue == null || defaultValue.length() == 0) {
            defaultValue = "";
            print("WARNING!: Key: " + key + " not defined in default properties string(in code)", WARN);
        }
        if(value == null || value.length() == 0) {
            print("WARNING!: Key: " + key + " not found in properties file. Using default value: " + defaultValue, WARN);
            return defaultValue;
        }
        else
            return value;
    }




    /**********************************************************************************************************************************************************************************************************************************/



    String getRequiredValue(String key) {
        String value = this.message.getProperty(key);
        if(value != null)
            value = value.trim();
        if(value == null || value.length() == 0)
            handleException("ERROR!: Required Key: " + key + " not found in message or has null value");
        return value;
    }



    /**********************************************************************************************************************************************************************************************************************************/



    String getValue(String key) {
        String value = this.message.getProperty(key);
        if(value != null)
            value = value.trim();
        if(value == null || value.length() == 0) {
            print("WARNING!: Key: " + key + " not found in message. Using \"\"", WARN);
            value = "";
        }
        return value;
    }



    /**********************************************************************************************************************************************************************************************************************************/



    void print(String str, int importance) {

        if(importance == ERR)
            this.displayString.append("\n\n************************* !!! ERROR !!! *************************\n"
                                      + "Printed at: " + new Date() + "\n"
                                      + str
                                      + "\n\n\n\n");

        else if(importance == WARN)
            this.displayString.append("\n\n************************ !!! Warning !!! ************************\n"
                                      + "Printed at: " + new Date() + "\n"
                                      + str
                                      + "\n\n\n\n");

        else if(importance <= V_IMP || importance <= this.DISPLAY_LEVEL)
            this.displayString.append(str + "\n\n");
    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void writeLogs(boolean errorState) {
        syncWriteLogs(errorState, this);
    }



    /**********************************************************************************************************************************************************************************************************************************/



    private static synchronized void syncWriteLogs(boolean errorState, MQHandler callingThread) {

        callingThread.print("Starting writeLogs()...", V_IMP);

	if( ! callingThread.orderNumber.startsWith(OrderProcessor.ignoreUserid) )
            OrderProcessor.writePerfLog(callingThread.perfString.toString());

        callingThread.print("Wrote Performace Log", V_IMP);


        String displayFileName = DISPLAY_DIR + callingThread.orderFileName;


        String orderSourceName = CURRENT_ORDERS_DIR + callingThread.orderFileName;
	String orderTargetName;

	if(errorState)
	    orderTargetName = FAILED_ORDERS_DIR + callingThread.orderFileName;
	else
	    orderTargetName = COMPLETED_ORDERS_DIR + callingThread.orderFileName;


	File sf = new File(orderSourceName);
	File tf = new File(orderTargetName);

        if( (! errorState) && (callingThread.orderType == AREA_MEM || callingThread.orderType == REPORT_MEM) )
            sf.delete();
	else if( ! sf.renameTo(tf)) {
           callingThread.print(
			    "\n\n\n************************* !!! ERROR !!! *************************\n"
			    + "WARNING!: Error moving " + orderSourceName + " to " + orderTargetName
			    + " at "
			    + new Date() + "\n"
			    + "*****************************************************************\n\n",
			    ERR);

	    OrderProcessor.print
		    (
		     "\n\n\n************************* !!! ERROR !!! *************************\n"
		     + "WARNING!: Error moving " + orderSourceName + " to " + orderTargetName
		     + " at "
		     + new Date() + "\n"
		     + "*****************************************************************\n\n",
		     OrderProcessor.ERR);
                     
          //new code to handle renameTo exception that happens recently, csr 10525
          
           if (!mvFile(sf, tf, COMPLETED_ORDERS_DIR, callingThread)){
              callingThread.print("can not mvFile "+sf+" to "+tf +", has to directly delete "+ tf, V_IMP);
              sf.delete();
              
           }//end of new
	    
	}



        try {
            if(errorState) {
                OrderProcessor.print(
                                     "ERROR Processing order (# "
                                     + callingThread.orderNumber
                                     + ") from "
                                     + callingThread.message.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                                     + " for "
                                     + OrderProcessor.orderNames[callingThread.orderType]
                                     + " at "
                                     + new Date()
                                         , OrderProcessor.V_IMP
                                     );

                OrderProcessor.print(
                                     "ERROR Processing order (# "
                                     + callingThread.orderNumber
                                     + ") from "
                                     + callingThread.message.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                                     + " for "
                                     + OrderProcessor.orderNames[callingThread.orderType]
                                     + " at "
                                     + new Date()
                                         , OrderProcessor.ERR
                                     );
            }

            else if( ! callingThread.orderNumber.startsWith(OrderProcessor.ignoreUserid) )
                OrderProcessor.print(
                                     "Completed order (# "
                                     + callingThread.orderNumber
                                     + ") from "
                                     + callingThread.message.getProperty("EDGE_USERID", "NOT_DEFINED").trim()
                                     + " for "
                                     + OrderProcessor.orderNames[callingThread.orderType]
                                     + " at "
                                     + new Date()
                                         , OrderProcessor.V_IMP
                                     );
        }
        catch(Throwable t) {
            callingThread.print("Error calling OrderProcessor.print()"
                  + "StackTrace:\n" + getStackTrace(t)
                  , ERR);
        }



        if( (! errorState) && (callingThread.orderType == AREA_MEM || callingThread.orderType == REPORT_MEM) )
            return;

        try {
            callingThread.print("Writing logs and exiting MQHandler thread...", V_IMP);
            FileOutputStream displayFile = new FileOutputStream(displayFileName, true);
            displayFile.write(callingThread.displayString.toString().getBytes());
            displayFile.close();
        }
        catch(Throwable t) {
            OrderProcessor.print(
                               "\n\n\n************************* !!! ERROR !!! *************************\n"
                               + "WARNING!: Error writing to " + displayFileName
                               + " at "
                               + new Date() + "\n"
                               + "Stack Trace:\n"
                               + getStackTrace(t) + "\n"
                               + "While writing:\n"
                               + callingThread.displayString.toString() + "\n"
                               + "*****************************************************************\n\n",
                               OrderProcessor.ERR);
        }
    }



   
 /**********************************************************************************************************************************************************************************************************************************/


    private static boolean mvFile(File input, File output, String targetDir, MQHandler callingThread) {
       
       File tgDir = new File (targetDir);
       callingThread.print("SF is "+ input + " and DF is "+ tgDir +" and output file is "+output, V_IMP);
       callingThread.print("SF exists ? "+ input.exists(), V_IMP);
       callingThread.print("SF isFile ? "+ input.isFile(), V_IMP);
       callingThread.print("SF canRead ? "+input.canRead(), V_IMP);
       callingThread.print("DF exists ? "+ tgDir.exists(), V_IMP);
       callingThread.print("DF isDir ? "+ tgDir.isDirectory(), V_IMP);
       callingThread.print("DF canWrit ? "+ tgDir.canWrite(), V_IMP);
       
       int bytesRead = 0;
       byte[] buffer = new byte[2048];
       
       try{
       
          FileInputStream in = new FileInputStream (input);
          FileOutputStream out = new FileOutputStream (output);
       
       
          while ((bytesRead = in.read(buffer)) != -1)
              out.write(buffer, 0, bytesRead);
          in.close();     
          out.close();
          
        }
       catch (Exception ex){
          ex.printStackTrace();
          callingThread.print(ex.toString(), V_IMP);
          return false;
       } 
        
       if (input.delete()){
          callingThread.print(input +" is deleted. ", V_IMP);
          return true;
       }
       else {
          callingThread.print(input +" is not deleted. ", V_IMP);
          return false;  
       }
       
      
    }
    
     /**********************************************************************************************************************************************************************************************************************************/

    void flushDisplay() {
        syncFlushDisplay(this);
    }



    /**********************************************************************************************************************************************************************************************************************************/



    static synchronized void syncFlushDisplay(MQHandler callingThread) {

        callingThread.print("Flushing display to file...\n\n", V_IMP);

        String displayFileName = DISPLAY_DIR + callingThread.orderFileName;

        try {
            FileOutputStream displayFile = new FileOutputStream(displayFileName, true);
            displayFile.write(callingThread.displayString.toString().getBytes());
            displayFile.close();
            callingThread.displayString = new StringBuffer();
        }
        catch(Throwable t) {
            OrderProcessor.print(
                               "\n\n\n************************* !!! ERROR !!! *************************\n"
                               + "WARNING!: Error flushing display to " + displayFileName
                               + " at "
                               + new Date() + "\n"
                               + "Stack Trace:\n"
                               + getStackTrace(t) + "\n"
                               + "While writing:\n"
                               + callingThread.displayString.toString() + "\n"
                               + "*****************************************************************\n\n",
                               OrderProcessor.ERR);
        }
    }





    /**********************************************************************************************************************************************************************************************************************************/



    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }



    /**********************************************************************************************************************************************************************************************************************************/



    int execute(String cmd) {
        return execute(cmd, NON_ZERO_EXIT_VALUE);
    }



    /**********************************************************************************************************************************************************************************************************************************/


   class DrainInput extends Thread {//new for 5.3.1
      StringBuffer ans = null;
      BufferedInputStream bis = null;
      public DrainInput(BufferedInputStream bis, StringBuffer ans) {
         this.bis = bis;
         this.ans = ans;
      }

      public void run() {
         int arrSize = 1024;
         byte[] arr = new byte[arrSize];

         int read = 0;
         try{
            while(read >= 0) {
               ans.append(new String(arr, 0, read));
               read = bis.read(arr, 0, arrSize);
            }
            bis.close();
         }catch (java.io.IOException e){
            handleMinorException(e, "thrown reading output from DrainInput");
         }
      }
   }


   int execute(String cmd, int outputDisplayMode) {//updated for 5.3.1

        int arrSize = 1024;
        byte[] arr = new byte[arrSize];
        int exitValue = -1;
        String[] command = {"/bin/ksh", "-c", cmd};

        try {
            Process p = Runtime.getRuntime().exec(command);

            DrainInput di1 = null;
            DrainInput di2 = null;

            StringBuffer inString  = new StringBuffer();
            StringBuffer errString = new StringBuffer();

            try {
               BufferedInputStream in =
                  new BufferedInputStream(p.getInputStream());
               BufferedInputStream err =
                  new BufferedInputStream(p.getErrorStream());
               di1 = new DrainInput(in,  inString);
               di2 = new DrainInput(err, errString);
               di1.start(); di2.start();


            } catch(Throwable t) {
               handleMinorException(t, "thrown reading output from: " + cmd);
            }

            exitValue = p.waitFor();

            if(outputDisplayMode != SILENT) {

               try {
                  di1.join();
                  di2.join();
               } catch(Throwable tt) {
                  handleMinorException(tt,
                                       "thrown reading output from: " + cmd);
               }

                String str
                    = cmd + " returned an exit Value of " + exitValue + "\n"
                    + "stdout: " + inString.toString() + "\n"
                    + "stderr: " + errString.toString() + "\n";

                if(outputDisplayMode == VERBOSE)
                    print(str, V_IMP);
                else if(exitValue != 0)
                    handleMinorException(str);
                else
                    print(str, NOT_IMP);
            }

            return exitValue;
        }
        catch(Throwable t) {
            handleException(t, "thrown executing: " + cmd);
            return -1;
        }
    }




    /**********************************************************************************************************************************************************************************************************************************/


    String executeAndGetStream(String cmd) throws IOException, InterruptedException {

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
            print(str, V_IMP);
        else
            print(str, NOT_IMP);

        return inString;

    }




    /**********************************************************************************************************************************************************************************************************************************/



    private String readFile(String filename, int arraySize) {

        if( ! new File(filename).isFile())
            handleException(filename + ": file not found");

        String content = "";
        int bytesRead = 0;
        byte[] arr = new byte[arraySize];
        try {
            FileInputStream in = new FileInputStream(filename);
            while(bytesRead >= 0) {
                content += new String(arr, 0, bytesRead);
                bytesRead = in.read(arr, 0, arraySize);
            }
            in.close();
            return content.trim();
        }
        catch(IOException e) {
            handleException(e, "Thrown while trying to read contents of " + filename);
            return null;
        }
    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String padString(String s, int length) {
        StringBuffer sb = new StringBuffer();
        for(int i = s.length(); i < length; i++)
            sb.append(" ");
        return s + "," + sb.toString();
    }



    /**********************************************************************************************************************************************************************************************************************************/


    String[] getStringArray(String field, String delim) {
        return tokenizeString(getRequiredValue(field), delim);
    }


    /**********************************************************************************************************************************************************************************************************************************/


    String[] getStringArray(String field) {
        return tokenizeString(getRequiredValue(field), ",");
    }


    /**********************************************************************************************************************************************************************************************************************************/


    private String[] tokenizeString(String s) {

        return tokenizeString(s, ",");

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String[] tokenizeString(String s, String delim) {

        StringTokenizer st = new StringTokenizer(s, delim);
        int numTokens = st.countTokens();
        String[] retArray = new String[numTokens];
        int i = 0;

        while(st.hasMoreTokens())
            retArray[i++] = st.nextToken().trim();

        return retArray;

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String getLinkTarget(String link) {

        String listing = null;
        String ret = "";

        try {
            listing = executeAndGetStream("/usr/bin/ls -l " + link);
        }
        catch(Throwable t) {
            handleException(t, "thrown getting link target for " + link);
        }

        ret  = listing.substring(listing.indexOf("->") + 3).trim();
        if (! ret.startsWith("/")){

           File nf = new File(link);
           String temp = nf.getParentFile().getAbsolutePath();
           ret = temp + "/" + ret;

        }
        return ret;

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String arrayToString(String[] arr) {
        if(arr == null || arr.length == 0)
            return null;
        else {
            String str = arr[0].trim();
            for(int i = 1; i < arr.length; i++)
                str += "," + arr[i].trim();
            return str;
        }
    }




    /**********************************************************************************************************************************************************************************************************************************/



    private String getTechnologyName(String externalTechnologyName) {

        try {
            loadLRD();
        }
        catch(IOException e) {
            handleException("thrown loading " + lastReleaseDataFile);
        }

        for(int i = 0; i < lastReleaseDataInfo.length; i++) {

            if(lastReleaseDataInfo[i][4].equals(externalTechnologyName))
                return lastReleaseDataInfo[i][0];

        }

        handleException("no matching technology name for: " + externalTechnologyName + " found in " + lastReleaseDataFile);

        return null;

    }

    /**********************************************************************************************************************************************************************************************************************************/



    private String getTechnologyAlias(String technology) {

        try {
            loadLRD();
        }
        catch(IOException e) {
            handleException("thrown loading " + lastReleaseDataFile);
        }

        for(int i = 0; i < lastReleaseDataInfo.length; i++) {

            if(lastReleaseDataInfo[i][0].equals(technology))
                return lastReleaseDataInfo[i][1];

        }

        handleException("no matching technology for: " + technology + " found in " + lastReleaseDataFile);

        return null;

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private String[] getShipManagerEmail(String technology) {

        try {
            loadLRD();
        }
        catch(IOException e) {
            handleException("thrown loading " + lastReleaseDataFile);
        }

        for(int i = 0; i < lastReleaseDataInfo.length; i++) {

            if(lastReleaseDataInfo[i][0].equals(technology))
                return new String[] {lastReleaseDataInfo[i][2], lastReleaseDataInfo[i][3]};

        }

        handleException("no matching technology for: " + technology + " found in " + lastReleaseDataFile);

        return null;

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private static synchronized void loadLRD() throws IOException {

        long t = new File(lastReleaseDataFile).lastModified();

        if(t != lastUpdatedLRD) {
            lastReleaseDataInfo = ShipManager.loadFile(lastReleaseDataFile);
            lastUpdatedLRD = t;
        }

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private int[] sort(String[] arr) {

        int length = arr.length;

	int[] intArr = new int[length];
	for(int i = 0; i < length; i++) {
	    intArr[i] = Integer.parseInt(arr[i]);
	}

        int[] sorted = (int[]) intArr.clone();

        Arrays.sort(sorted);

        int[] orderList = new int[length];

        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {
                if(intArr[j] == sorted[i]) {
                    orderList[i] = j;
                    break;
                }
            }
        }

        return orderList;
    }




    /**********************************************************************************************************************************************************************************************************************************/



    private String[] sequence(String[] arr, int[] sequenceOrder) {

	if(arr.length != sequenceOrder.length)
	    throw new RuntimeException("Array length does not match sequenceOrder array length");

	String[] sequenced = new String[arr.length];

        for(int i = 0; i < arr.length; i++)
	    sequenced[i] = arr[sequenceOrder[i]];

	return sequenced;

    }



    /**********************************************************************************************************************************************************************************************************************************/



    public void run() {

        Date receivedTime = new Date();
        try {
            initializeThread();
        }
        catch(Throwable t) {
            OrderProcessor.handleMinorException(t, "thrown initializing thread for order#: " + this.orderNumber + "\tExiting thread...");
            return;
        }


        try {
            print("Starting MQHandler thread...", V_IMP);


            if(orderType == AREA_MEM || orderType == REPORT_MEM) {
                edgeUserid = "N.A.";
                orderBy = "N.A.";
                timestamp  = receivedTime.getTime();
            }
            else if(orderType == DK_INIT || orderType == DR_INIT || orderType == IPDK_INIT) {
                edgeUserid = getRequiredValue("EDGE_USERID");
                orderBy = "N.A.";
                timestamp  = Long.parseLong(getRequiredValue("TIMESTAMP"));
                usersCompany = getValue("USERS_COMPANY");
            }

            else {
                edgeUserid = getRequiredValue("EDGE_USERID");
                orderBy = getRequiredValue("ORDER_BY");
                timestamp  = Long.parseLong(getRequiredValue("TIMESTAMP"));
                usersCompany = getValue("USERS_COMPANY");
            }


            Date submitTime = new Date(timestamp);

            perfString.append(padString(this.edgeUserid, 10) + padString(this.orderNumber, 20) + padString(this.corrID, 20));       // orderNumber corrId
            perfString.append(formatter.format(submitTime) + ",");          // sent time
            perfString.append(formatter.format(receivedTime) + ",");        // received time


            if(orderType == LIC_TOOL || orderType == LIC_FULLPD || orderType == LIC_XMX)
                handleLIC();

            else if(orderType == AREA_MEM)
                handleAreaMem();

            else if(orderType == REPORT_MEM)
                handleReportMem();

            else if(orderType == DK_INIT)
                handleDKinit();

           //new for 5.1.1
            else if(orderType == IPDK_INIT)
               handleIPDKinit();
           //end of new for 5.1.1
            else if(orderType == DR_INIT)
                handleDRinit();

            else {
              // if(getRequiredValue("CD").equals("1"))
                    cd = false;
                    
				  if(getRequiredValue("DROPBOX").equalsIgnoreCase("yes"))
				  {
				  	  isDropbox = true;
				  }

             //  if(getRequiredValue("DL").equals("1"))
                    dl = true;

                if(cd && dl)
                    handleException("Both CD and DL options cannot be set to 1");
                else if( ! cd && ! dl )
                    handleException("Either CD or DL must be set to 1");

                handleDOWNLOAD();

               //   if(cd)
               //    handleCD();
            }

            Date finishedTime = new Date();                                 // finished time
            float totalTimeTaken = ((float)(finishedTime.getTime() - receivedTime.getTime())) / 1000;   //seconds
            perfString.append(formatter.format(finishedTime) + ", " + totalTimeTaken + " seconds, " + technology + " " + versionNo + ", " + usersCompany);

            writeLogs(false);
            
            //Add dropbox handling code
            //Upload is invoked at the very end because :-
            // 1) The upload can take a very long time
            // 2) Files are to be uploaded only if the order is successfully created.
			if(isDropbox)
			{
			  completeDropboxUpload(uploadFileNames);
			} 
        }
        catch(HandledException e) {
            boolean fail = true;
            try {
                fail = mqSendError();
            }
            catch(Throwable t) {
                print("thrown sending error MQ:\n" +  getStackTrace(t), ERR);
            }
            if(fail) {
                perfString.append(" ERROR, " + formatter.format(new Date()));
                print("Handled Exception - Exiting MQHandler thread at " + new Date(), V_IMP);
                writeLogs(true);
            }
        }
        catch(Throwable t) {
            handleMinorException(t, "Unhandled Exception");
            boolean fail = true;
            try {
                fail = mqSendError();
            }
            catch(Throwable t1) {
                print("thrown sending error MQ:\n" +  getStackTrace(t1), ERR);
            }
            if(fail) {
                perfString.append(" ERROR, " + formatter.format(new Date()));
                print("UNHANDLED EXCEPTION!!! - Exiting MQHandler thread at " + new Date(), ERR);
                writeLogs(true);
            }
        }

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private void handleLIC() {

        print("Starting handleLIC()...", V_IMP);

        if(orderType == LIC_TOOL || orderType == LIC_XMX) {
            if(getProperty("USE_AUTO_LICENSE_GENERATION").equalsIgnoreCase("no")) {
                this.useAutoLicGen = false;
                print("NOT using AUTO_LICENSE_GENERATION", V_IMP);
            }
            else if(getRequiredValue("IBM_INTERNAL").equalsIgnoreCase("Y")) {
                this.useAutoLicGen = false;
                print("NOT using AUTO_LICENSE_GENERATION because customer is INTERNAL", V_IMP);
            }
            else {
                this.useAutoLicGen = true;
                print("USING AUTO_LICENSE_GENERATION", V_IMP);
                generateToolkitLicense();
                return;
            }
        }

        mail(FSE_REQ_LIC);

    }




    /**********************************************************************************************************************************************************************************************************************************/



    private void handleCD() {
        print("Starting handleCD()...", V_IMP);

        String orderStr = null;

        if(orderType == DESIGN) {
            orderStr = this.tdofString;
	    if( toolkitOnly ) {
	        checkDKfields();
// execCopyOrder();
	    }
        }
        else if(orderType == PREVIEW) {
            checkPKfields();

/*
            String tdof = TDOF_DIR + "CD/PreviewKit." + orderNumber;

            try {
                orderStr = new TDOF(tdof, "CD", this, false, false, "").createPK();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
*/

            orderStr = readFile(CURRENT_ORDERS_DIR + this.orderFileName, 1024);

        }
        else
            handleException("CD option not allowed for: " + OrderProcessor.orderNames[orderType]);

        mail(SM_REQ_CD, orderStr);
    }


    /**********************************************************************************************************************************************************************************************************************************/




    private void checkDKfields() {

        technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));
        versionNo = getRequiredValue("VERSION_NO");

        getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany = getRequiredValue("USERS_COMPANY");
        fseEmail = getValue("FSE_EMAIL_1");

        getRequiredValue("BASE_MODEL_KIT");
        getRequiredValue("NONSTAND_DELIVERS_COUNT");
        getRequiredValue("CORES_COUNT");
        getRequiredValue("BASE_ORD_COUNT");

        firstName = getRequiredValue("FIRST_NAME");
        lastName = getRequiredValue("LAST_NAME");

        getRequiredValue("ADDRESS");
        getRequiredValue("CITY");
        getRequiredValue("STATE");
        getRequiredValue("ZIP");
        getValue("PHONE");
        getRequiredValue("E_MAIL");
        getRequiredValue("PLATFORMS_COUNT");
        getRequiredValue("SHIPTO_LOC_COUNT");

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private void execCopyOrder(String mediaStr) {


        String tdofDir = TDOF_DIR + mediaStr + "/";

/*
        String tdof = tdofDir + "ModelKit." + orderNumber;

        String orderStr = null;

        try {
            orderStr = new TDOF(tdof, "CD", this, true, false, "").createTDOF();
        }
        catch(Exception e) {
            handleException(e, "thrown while writing TDOF file: " + tdof);
        }
*/


        String copyOrderCommand
            = COPY_ORDER_SCRIPT
            + " "
            + SHIPIT_MODE
            + " "
            + technology
            + " "
            + versionNo
            + " "
            + orderNumber
            + " "
            + tdofDir
            + " "
            + "MAJOR"
            + " "
            + "FILTERED";


        print("Executing: " + copyOrderCommand + " at " + new Date(), V_IMP);
        flushDisplay();
        int exitValue = execute(copyOrderCommand);
        print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


        if(exitValue == 0)
            print("copyOrder executed successfully", V_IMP);
        else if(exitValue == 1)
            handleException("copyOrder returned an Exit Value of: 1 (Error copying TDOF file - could be read or write error)");
        else if(exitValue == 4)
            handleException("copyOrder returned an Exit Value of: 4 (Error creating log entry)");
        else
            handleException("copyOrder returned an Exit Value of: " + exitValue + " (Unknown Error)");

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private void checkPKfields() {

        technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));
        versionNo = getRequiredValue("VERSION_NO");
        fseEmail = getValue("FSE_EMAIL_1");
        firstName = getRequiredValue("FIRST_NAME");
        lastName = getRequiredValue("LAST_NAME");

        getRequiredValue("ADDRESS");
        getRequiredValue("CITY");
        getRequiredValue("STATE");
        getRequiredValue("ZIP");
        getValue("PHONE");
        getRequiredValue("E_MAIL");

    }



    /**********************************************************************************************************************************************************************************************************************************/

    private void handleDOWNLOAD() {

        print("Starting handleDOWNLOAD()...", V_IMP);

        // removed for 2.10
        // edgeLogonContext = getRequiredValue("PASSWORD");

       //   notification = getRequiredValue("NOTIFICATION");
        notification = getValue("NOTIFICATION");
        usersEmail   = getRequiredValue("E_MAIL");


        // removed for 2.10
        /*
        if(edgeLogonContext.equals("rO0ABXA=") && edgeUserid.equals(orderBy)) {
            if(notification.equalsIgnoreCase("email"))
                handleException("EdgeLogonContext is null for download order with email notification");
            else
                handleMinorException("EdgeLogonContext is null for download order with Inbox notification");
        }
        */


        if(orderType != DSE)
            fseEmail = getValue("FSE_EMAIL_1");
        else
            fseEmail = getRequiredValue("E_MAIL");

        if(orderType != DSE && orderType != FULLPD && orderType != XMX) {

            if(orderType == MEM || orderType == EFPGA)
                technology = getRequiredValue("TECHNOLOGY");
            else
                technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));

            versionNo  = getRequiredValue("VERSION_NO");

        }

        switch(orderType)
            {
            case DESIGN:
                handleDK();
                break;
            case PREVIEW:
                handlePK();
                break;
            case DELTA:
                handleDR();
                break;
            case FULLPD:
                handlePDK();
                break;
            case DSE:
                handleDSE();
                break;
            case XMX:
                handleXMX();
                break;
            case MEM:
                handleMem();
                break;
            case EFPGA: //new for 4.5.1
                handleEfpga();
                break;
            case IPDESIGN: //new for 5.1.1
                handleIPDK();
                break;
            default:
                handleException("Invalid orderType switch: " + orderType + " in handleDOWNLOAD()");
            }

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private String parseToolMTsFile(String toolMTsFile) throws IOException {

	String contents = readFile(toolMTsFile, 1024);
	String line;
	int index1, index2, index3;
	int toolCount = 0;

	StringBuffer TOOL_COUNT = new StringBuffer("TOOL_COUNT=");
	StringBuffer TOOL_LIST = new StringBuffer("TOOL_LIST=");
	StringBuffer MODEL_TYPES_LIST = new StringBuffer("MODEL_TYPES_LIST=");
	StringBuffer MT_SZ_KB_LIST = new StringBuffer("MT_SZ_KB_LIST=");

	BufferedReader in = new BufferedReader(new StringReader(contents));

	while( (line=in.readLine()) != null) {

	    if(line.trim().length() == 0)
		continue;

	    index1 = line.indexOf(';');
	    index2 = line.indexOf(';', index1+1);
	    index3 = line.indexOf(';', index2+1);

	    TOOL_LIST.append(line.substring(0, index1));
	    TOOL_LIST.append(',');
	    MODEL_TYPES_LIST.append(line.substring(index1+1, index2-1));
	    MODEL_TYPES_LIST.append(';');
	    MT_SZ_KB_LIST.append(line.substring(index2+1, index3-1));
	    MT_SZ_KB_LIST.append(';');

	    toolCount++;

        }
	in.close();

	if(toolCount <= 0)
	    throw new RuntimeException("TOOL_COUNT is 0");

	TOOL_COUNT.append(toolCount);

	TOOL_COUNT.append('^');
	TOOL_LIST.append('^');
	MODEL_TYPES_LIST.append('^');
 	MT_SZ_KB_LIST.append('^');

	TOOL_COUNT.append(TOOL_LIST.toString()).append(MODEL_TYPES_LIST.toString()).append(MT_SZ_KB_LIST.toString());

	String result = TOOL_COUNT.toString();

	print(result, V_IMP);

	return result;

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private void writeBinsFile(String[][] bins, String orderNumber, String file) {

        String availableBinsStr = "";

	for(int i = 0; i < bins.length; i++) {
	    String dir = bins[i][0] + "/" + orderNumber;
	    if( ! new File(dir).isDirectory() && ! new File(dir).mkdir() )
                handleException("Could not create directory: " + dir);
            availableBinsStr += dir + ";" + bins[i][1] + ";\n";
	}


	try {
	    FileOutputStream out = new FileOutputStream(file);
	    out.write(availableBinsStr.getBytes());
	    out.close();
	}
	catch(IOException e) {
	    handleException(e, "thrown writing to " + file);
	}

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String[] getCustomBuiltFiles(String deliverablesFile) throws IOException {

	String deliverables = readFile(deliverablesFile, 1024);
	Vector v = new Vector();
	int count = 0;
	BufferedReader in = new BufferedReader(new StringReader(deliverables));
	String line;

	while( (line = in.readLine()) != null ) {
	    line = line.trim();
	    v.addElement(line.substring(0, line.length() - 1));
	    count++;
	}

        String [] customBuiltFiles = new String[count];
        v.toArray(customBuiltFiles);

	return customBuiltFiles;

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private String getOrderDetails(boolean useHtmlTags) {
	return getOrderDetails(useHtmlTags, 0);
    }


    /**********************************************************************************************************************************************************************************************************************************/


    private String getOrderDetails(boolean useHtmlTags, int addShipTo) {

	String delimiter = "\n";
	if(useHtmlTags)
	    delimiter = "<br /> \n";

	StringBuffer orderDetails = new StringBuffer();

	if(orderType == DESIGN) {

	    orderDetails.append("PLATFORMS");
	    if(addShipTo == 0)
		orderDetails.append(getRequiredValue("PLATFORMS_LIST"));
	    else
               orderDetails.append(getRequiredValue(""));


	    orderDetails.append("Base Model Kit: ");
	    if(getRequiredValue("BASE_MODEL_KIT").equals("Y"))
	        orderDetails.append("Yes");
	    else
	        orderDetails.append("No");
	    orderDetails.append(delimiter);

	    orderDetails.append("");

	}
       //new for 5.1.1
        else if (orderType == IPDESIGN) {
          //TODO
        }
       //end of new for 5.1.1

	return orderDetails.toString();

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private void handleConditionalException(String s) {

	if(isPrimaryCustomer)
	    handleException(s);
	else
	    handleMinorException(s);

    }

    /**********************************************************************************************************************************************************************************************************************************/


    private void handleConditionalException(Throwable t, String s) {

	if(isPrimaryCustomer)
	    handleException(t, s);
	else
	    handleMinorException(t, s);

    }


    /**********************************************************************************************************************************************************************************************************************************/


/*
*** EDGE_USERID
*** MESSAGE_ID ( first 6 chars of userid + timestamp ? )
*** TIMESTAMP
*** TECHNOLOGY
*** VERSION_NO
*** ASIC_CODENAME
*** CUSTOMER_PROJNAME
*** USERS_COMPANY
*** FIRST_NAME
*** LAST_NAME
*** E_MAIL

      FSE_TYPE_1
      FSE_NAME_1
      FSE_LOC_1
      FSE_PHONE_1
      FSE_EMAIL_1

*** BASE_MODEL_KIT                  <------ Should be Y or N

*** NONSTAND_DELIVERS_COUNT
*    NONSTAND_DELIVERS_LIST       <--- Required if above field is non-zero
*** CORES_COUNT
*    CORES_LIST                             <--- Required if above field is non-zero
*** BASE_ORD_COUNT
*    BASE_ORD_LIST                    <--- Required if above field is non-zero
*** PLATFORMS_COUNT
*    PLATFORMS_LIST                  <--- Required if above field is non-zero (From PLATFORMS table)
*/

    private void handleDKinit() {   // cherry-picking Design Kit

        print("Starting handleDKinit()...", V_IMP);

        boolean emptyMajor = false, emptyDelta = false;

        usersEmail   = getRequiredValue("E_MAIL");
        fseEmail = getValue("FSE_EMAIL_1");
        technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));
        versionNo  = getRequiredValue("VERSION_NO");

        getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getRequiredValue("FIRST_NAME");
        lastName         = getRequiredValue("LAST_NAME");

        String tdofDir = TDOF_DIR + "DL/";
        String tdof    = tdofDir + "ModelKit." + orderNumber;
        String toolMTsFile = tdofDir + "ModelKit." + orderNumber + ".toolMTs";


        if(getRequiredValue("BASE_MODEL_KIT").equals("N") && getRequiredValue("NONSTAND_DELIVERS_COUNT").equals("0") && getRequiredValue("CORES_COUNT").equals("0") && getRequiredValue("BASE_ORD_COUNT").equals("0"))
            handleException("No modelKit or custom deliverables ordered for cherry-picking");


        int numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));

        if(numPlatforms <= 0)
            handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);

        this.platformsList = getStringArray("PLATFORMS_LIST");

        if(numPlatforms != this.platformsList.length)
            handleException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length);


        try {

            new TDOF(tdof, "DL", this, true, true, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
        }
        catch(Exception e) {
            handleException(e, "thrown while writing TDOF file: " + tdof);
        }


        String getToolsCommand
                = GET_TOOLS_SCRIPT
                + " "
                + SHIPIT_MODE
                + " "
                + technology
                + " "
                + versionNo
                + " "
                + orderNumber
                + " "
                + tdofDir
                + " "
                + "MAJOR"
                + " "
		+ COPY_ORDER_DIR;

        print("Executing: " + getToolsCommand + " at " + new Date(), V_IMP);
        flushDisplay();
        int exitValue = execute(getToolsCommand);
        print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


        if(exitValue == 0)
            print("getTools executed successfully", V_IMP);
       //new for 4.4.1
        else if (exitValue == 5){
           emptyMajor = true;
           print("No DA tools return from model kits", V_IMP);
        }
       //end of new for 4.4.1
        else {
            handleException(getToolsCommand + " returned an exitValue of: " + exitValue);
        }

       //new for 3.12.1

        String delta_indicator = getRequiredValue("DELTA_INDICATOR");
        //new for 4.2.1 fix
        int numPatches = 0;
        try {
            numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
        }
        catch(NumberFormatException e) {
            handleException(e, "PATCH_LIST_COUNT field is not numeric");
        }
       //end of new for 4.2.1 fix
        if (delta_indicator.equalsIgnoreCase("Y") && numPatches > 0 ){
           this.majorAndDelta = true;
           emptyDelta = handleDRcatchup();

           if (!emptyMajor && !emptyDelta){

              String mergeToolsCommand
                 = MERGE_TOOLS_SCRIPT
                 + " "
                 + SHIPIT_MODE
                 + " "
                 + technology
                 + " "
                 + versionNo
                 + " "
                 + orderNumber
                 + " "
                 + orderNumber
                 + " "
                 + tdofDir;

              print("Executing: " + mergeToolsCommand + " at " + new Date(), V_IMP);
              flushDisplay();
              exitValue = execute(mergeToolsCommand);
              print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);

              if(exitValue == 0)
                 print("mergeTools executed successfully", V_IMP);
              else {
                 handleException(mergeToolsCommand + " returned an exitValue of: " + exitValue);
              }

              toolMTsFile = tdofDir + "merged." + orderNumber +".toolMTs";
           }

        }
       //end of new for 3.12.1

        else if (delta_indicator.equalsIgnoreCase("Y") && numPatches <=0 ){
           print ("User selects delta, but no delta to delivery.", V_IMP);
           emptyDelta = true;
        }

       //new for 4.4.1
        else
           emptyDelta = true;
        if (emptyMajor && emptyDelta)
              handleException("No DA tools return from neither Major nor Delta "+orderNumber);
       //end of new for 4.4.1



        try {
          //new for 4.4.1
            if (emptyMajor && !emptyDelta)//in case of Delta has DA Tools return
               toolMTsFile = tdofDir + "Delta."+orderNumber+".toolMTs";
            if( ! new File(toolMTsFile).isFile() )
            handleException(getToolsCommand + " returned OK but no toolMTs file: " + toolMTsFile);
          //end of new for 4.4.1
             String mqMsg = parseToolMTsFile(toolMTsFile);

            print("Sending MQ with CorrId: EDQ3_MODEL_TYPES and messageId: " + this.orderNumber, V_IMP);
            print("Message length in bytes: " + mqMsg.length(), V_IMP);
	    flushDisplay();
            OrderProcessor.sendMQMessage("EDQ3_MODEL_TYPES", this.orderNumber, mqMsg, transientMsgPersistent);   // orderNumber is actually messageId
            print("Sent MQ", V_IMP);
            print("Message sent by MQ:\n" + mqMsg, NOT_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown parsing: " + toolMTsFile + " or while sending MQ");
        }

        print("Exiting handleDKinit()...", IMP);

    }


  /********************************************************************************************************************************************************/
  //new for 5.1.1
   private void handleIPDKinit() {   // cherry-picking Design Kit

        print("Starting handleIPDKinit()...", V_IMP);

        usersEmail   = getRequiredValue("E_MAIL");
        fseEmail = getValue("FSE_EMAIL_1");
        technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));
        versionNo  = getRequiredValue("VERSION_NO");

        getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getRequiredValue("FIRST_NAME");
        lastName         = getRequiredValue("LAST_NAME");

        String tdofDir = TDOF_DIR + "DL/";
        String tdof    = tdofDir + "ModelKit." + orderNumber;
        String toolMTsFile = tdofDir + "ModelKit." + orderNumber + ".toolMTs";


        if(getRequiredValue("BASE_MODEL_KIT_COUNT").equals("0") && getRequiredValue("CORES_COUNT").equals("0"))
            handleException("No modelKit or custom deliverables ordered for cherry-picking");


        int numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));

        if(numPlatforms <= 0)
            handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);

        this.platformsList = getStringArray("PLATFORMS_LIST");

        if(numPlatforms != this.platformsList.length)
            handleException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length);


        try {

            new TDOF(tdof, "IP", this, true, true, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
        }
        catch(Exception e) {
            handleException(e, "thrown while writing TDOF file: " + tdof);
        }


        String getToolsCommand
                = GET_TOOLS_SCRIPT
                + " "
                + SHIPIT_MODE
                + " "
                + technology
                + " "
                + versionNo
                + " "
                + orderNumber
                + " "
                + tdofDir
                + " "
                + "MAJOR"
                + " "
		+ COPY_ORDER_DIR;

        print("Executing: " + getToolsCommand + " at " + new Date(), V_IMP);
        flushDisplay();
        int exitValue = execute(getToolsCommand);
        print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


        if(exitValue == 0)

            print("getTools executed successfully", V_IMP);

        else if (exitValue == 5){

           print("No DA tools return from model kits", V_IMP);
        }

        else {
            handleException(getToolsCommand + " returned an exitValue of: " + exitValue);
        }

        try {

            String mqMsg = parseToolMTsFile(toolMTsFile);

            print("Sending MQ with CorrId: EDQ3_MODEL_TYPES and messageId: " + this.orderNumber, V_IMP);
            print("Message length in bytes: " + mqMsg.length(), V_IMP);
	    flushDisplay();
            OrderProcessor.sendMQMessage("EDQ3_MODEL_TYPES", this.orderNumber, mqMsg, transientMsgPersistent);   // orderNumber is actually messageId
            print("Sent MQ", V_IMP);
            print("Message sent by MQ:\n" + mqMsg, NOT_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown parsing: " + toolMTsFile + " or while sending MQ");
        }

        print("Exiting handleIPDKinit()...", IMP);

    }

    /**********************************************************************************************************************************************************************************************************************************/
   private void handleIPDK() {   // IP Design Kit

        String packetHistory="";
        print("Starting handleIPDK()...", V_IMP);

        asicCodeName = getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getRequiredValue("FIRST_NAME");
        lastName         = getRequiredValue("LAST_NAME");

        int shipToID = 1;
        String[] nsourceFile, nfileDesc;


	String mediaStr = "DL";

        String tdofDir = TDOF_DIR + mediaStr + "/";

        String tdof = tdofDir + "ModelKit." + orderNumber + ".filtered";

        String techDirLoc
            = LINKS_DIR
            + technology + "/"
            + versionNo + "/";

          //new for 5.4.1
         String[] tkfixName;
         Vector toolkit_list = new Vector();
         
         //new for 6.3
	     String[] tkAddlComponentName;
	     Vector toolkitAddlComponentList = new Vector();
         

         if(getRequiredValue("TOOLKIT").equals("Y"))
            this.toolKit = true;

         if (this.toolKit){
           if (getRequiredValue("TOOLKIT_FIX_IND").equalsIgnoreCase("Y")){
              if (getValue("TOOLKIT_FIX_NAME")== null || getValue("TOOLKIT_FIX_NAME").length() == 0){
                 print("User select TK Fix, but no Fix to delivery", V_IMP);
                 this.tkFix = false;
              }
              else{
                 tkfixName = getStringArray("TOOLKIT_FIX_NAME");
                 String tk;
                 for (int j=0; j<tkfixName.length; j++){

                    if (!stringAlreadyExists(tkfixName[j], toolkit_list))
                       toolkit_list.addElement(tkfixName[j]);
                 }
              this.tkFix = true;
              }

           }
           else
              this.tkFix = false;
              
        }
        
		/////////////////  6.3 Release changes ToolKit Addon  //////////////
	              
	  if (getRequiredValue("TOOLKIT_COMPONENT").equalsIgnoreCase("Y")){
		 if (getValue("TOOLKIT_COMPONENT_NAME")== null || getValue("TOOLKIT_COMPONENT_NAME").length() == 0){
			print("User select TK Additional Component, but no additional component to deliver", V_IMP);
			this.tkAddlComponent = false;
		 }
		 else{
			tkAddlComponentName = getStringArray("TOOLKIT_COMPONENT_NAME");
			String tk;
			for (int j=0; j<tkAddlComponentName.length; j++){
		
			   if (!stringAlreadyExists(tkAddlComponentName[j], toolkitAddlComponentList))
				  toolkitAddlComponentList.addElement(tkAddlComponentName[j]);
			}
		 this.tkAddlComponent = true;
		 }
		
	  }
	  else
		 this.tkAddlComponent = false;
			 

        
        ////////////////  6.3 Release changes Toolkit Addon /////
        print("toolkit_list length "+toolkit_list.size(), V_IMP);
	    print("toolkitAddlComponentList length "+toolkitAddlComponentList.size(), V_IMP);

        if(!this.customModelKit && getRequiredValue("BASE_MODEL_KIT_COUNT").equals("0") && getRequiredValue("CORES_COUNT").equals("0") && getRequiredValue("REVISION_INDICATOR").equalsIgnoreCase("N")&& this.toolKit)
           this.toolkitOnly = true;


        //end of new for 5.4.1


	if(getRequiredValue("MODEL_TYPES_LIST").equalsIgnoreCase("ALL")) {
            try {
                this.tdofString = new TDOF(tdof, "IP", this, true, false, orderNumber,TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}
	else {
            try {
                this.tdofString = new TDOF(tdof, "IP", this, true, false, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}

        String[] CUSTOM_MODEL_KITS = null;

        int numPlatforms = 0;

        numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
        if(numPlatforms <= 0)
           handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);
        this.platformsList = getStringArray("PLATFORMS_LIST");



        if(numPlatforms != this.platformsList.length) {
           handleConditionalException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length + " for shipToID: " + shipToID);

	}

        if (toolkitOnly)
           execCopyOrder(mediaStr);

        else {//big else
          //CSR IBMCC0010204
          //String packetHistory = getValue("PacketHistory");
           try{
            packetHistory = getPacketHistory(technology, versionNo, customerProjname, asicCodeName);
           }
            catch (SQLException ex){
              print("Error: Stacktrace: \n" + getStackTrace(ex), V_IMP);
           }
          //end of CSR IBMCC0010204


        if (getRequiredValue("REVISION_INDICATOR").equalsIgnoreCase("N")) {
             //write file Delta.orderID.packetHistory
            String packetFile = tdofDir + "ModelKit." + orderNumber + ".packetHistory";
            if (packetHistory == null)
                 packetHistory="";
            createPacketHistory(packetFile, packetHistory);
        }

        long[] result = getSpaceRequired(tdofDir, shipToID);
        long spaceRequired = result[0];
        print("spaceRequired "+spaceRequired, V_IMP);

        if(spaceRequired < 0) {
           handleConditionalException("THIS SHOULD NOT HAPPEN! getSpaceRequired() returned " + spaceRequired);

        }
        else if(spaceRequired == 0) {
           this.customModelKit = false;
           print("SpaceRequired return 0, no MK to delivery", V_IMP);
           if (!this.toolKit) //new for 5.4.1
              handleEmptyOrder(); //new for 5.1.1 cu65lp
        }
        else {//deal with shipit

          //this.customModelKit = true;

          //   String[][] bins = Mutex.getFreeBins((long)(spaceRequired * 1.2), this, true);
           String bin = "bin1";

           if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
               CHIPS_CUSTOM_BINS_DIR += "/";
           String dir = CHIPS_CUSTOM_BINS_DIR + bin;

           String[][] bins = {{dir, "8000000000"}};

	   writeBinsFile(bins, orderNumber, tdofDir + "ModelKit." + orderNumber + "." + shipToID + ".bins");

           try {

             	for(int i = 0; i < bins.length; i++)
                  print("Locked bin: " + bins[i][0], V_IMP);



                String shipitCommand
                      = SHIPIT_SCRIPT
                      + " "
                      + SHIPIT_MODE
                      + " "
                      + technology
                      + " "
                      + versionNo
                      + " "
                      + orderNumber
                      + " "
                      + shipToID
                      + " "
                      + tdofDir
                      + " "
                      + "MAJOR"
                      + " "
                      + "ALL";


                 print("Executing: " + shipitCommand + " at " + new Date(), V_IMP);
                 flushDisplay();
                 int exitValue = execute(shipitCommand);
                 print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                 if(exitValue == 0) {
                    print("shipit executed successfully", V_IMP);
		    CUSTOM_MODEL_KITS = getCustomBuiltFiles(tdofDir + "ModelKit." + orderNumber + "." + shipToID + ".tarList");
		    this.numCustomModelKits = CUSTOM_MODEL_KITS.length;
                     this.customModelKit = true;
		 }
                 else if(exitValue == 3) {
                    this.customModelKit = false;
                    handleMinorException("THIS SHOULD NOT HAPPEN: " + shipitCommand + " returned an exitValue of: 3 (No custom deliverables)");
                }

                else {
                    handleConditionalException("shipit returned an Exit Value of: " + exitValue + " (Unknown Error), bins might be in inconsistent state");

                }
           }
           catch(Throwable t) {
              handleConditionalException(t, "thrown before, during or after executing shipit");

           }
          /* finally {
              Mutex.unlockBins(bins);
              for(int i = 0; i < bins.length; i++)
                 print("Unlocked bin: " + bins[i][0], V_IMP);
                 }*/
        }
        }//end of big else



        String[] sourceFiles = null;
        String[] fileDesc = null;

        int numSourceFiles = 0;
        if(this.customModelKit && (!getRequiredValue("BASE_MODEL_KIT_COUNT").equals("0") || !getRequiredValue("CORES_COUNT").equals("0") || getRequiredValue("REVISION_INDICATOR").equalsIgnoreCase("Y")))
           numSourceFiles++;

        if(this.toolKit)
	   numSourceFiles += numPlatforms;
         if(this.tkFix){
           this.numTkFix = toolkit_list.size();
           numSourceFiles += numTkFix;
         }
		 
		if(this.tkAddlComponent){
		  this.numTkAddlComponent = toolkitAddlComponentList.size();
		  numSourceFiles += numTkAddlComponent;
		}


       //end of new for 5.4.1

        if(numSourceFiles > 0) {
           sourceFiles = new String[numSourceFiles];
           fileDesc = new String[numSourceFiles];
        }
        else {
           handleConditionalException("No ip ordered");

        }
        int j = 0;
        if (CUSTOM_MODEL_KITS != null){
           for(int i=0; i < CUSTOM_MODEL_KITS.length; i++) {
              sourceFiles[i] = CUSTOM_MODEL_KITS[i];
              fileDesc[i] = "IP for " + technology + " " + versionNo;
              j++;
           }
        }
       //new for 5.4.1
         if(this.toolKit) {
                for(int i = 0; i < numPlatforms; i++) {
                    sourceFiles[j]
                        = getLinkTarget(
                            techDirLoc
                            + "ToolKit" + "/"
                            + this.platformsList[i] + "/"
                            + "toolkit");

                    fileDesc[j] = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[i];
                    j++;
                }
            }
            
	   //New for 6.3  ////
	     if(this.tkAddlComponent) {
					 for(int i = 0; i < toolkitAddlComponentList.size(); i++) {
					 	
					 	String tkAddlComp = (String)toolkitAddlComponentList.get(i);
						if(tkAddlComp != null && !tkAddlComp.trim().equals("") )
						{	
							print(" in handleIPDK() this.tkAddlComponent tkAddlComp " + tkAddlComp, V_IMP);  
						 	
						 	int platformIndex = tkAddlComp.trim().indexOf("_");
						 	String tkAddlCompPlatform = getTkPlatform(tkAddlComp.substring(0,platformIndex));
							print(" in handleIPDK() this.tkAddlComponent tkAddlCompPlatform " + tkAddlCompPlatform, V_IMP);
						 	
						 	String tkAddlCompFile = tkAddlCompPlatform + tkAddlComp.substring(platformIndex );
							print(" in handleIPDK() this.tkAddlComponent tkAddlCompFile " + tkAddlCompFile, V_IMP);
							
							 sourceFiles[j]
								 = getLinkTarget(
									 techDirLoc
									 + "ToolKit" + "/"
									 + tkAddlCompFile.trim() + "/"
									 + "toolkit");
									 
							print(" in handleIPDK() this.tkAddlComponent sourceFiles[j] " + sourceFiles[j], V_IMP);
	
							 fileDesc[j] = "Additional ToolKit Component for " + technology + " " + versionNo + " for " + tkAddlCompPlatform;
							 print(" in handleIPDK() this.tkAddlComponent fileDesc[j] " + fileDesc[j], V_IMP);
							 j++;
						}
					 }
				 }



         if (this.tkFix){

                   for (int m=0; m<toolkit_list.size(); m++){

                      if (new File(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit").exists()){//check whether the file exist

                         String filepath = getLinkTarget(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit");
                         if (new File(filepath).exists()){//check whether the file exist
                            sourceFiles[j]=getLinkTarget(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit");

                            fileDesc[j]="Toolkit Fix for "+toolkit_list.elementAt(m);

                            j++;
                            print("j "+j+" "+sourceFiles.length, V_IMP);
                         }
                      }
                   }
             }
             
             
             //////New for 6.3
             
             ///End 6.3 changes

             if (j != sourceFiles.length) { //in case there are some invalid tk fix in the sourceFile and fileDesc array
                this.numTkFix = numTkFix - (sourceFiles.length -j);
                nsourceFile = new String[j];
                nfileDesc = new String[j];
                for (int k =0; k < j; k++){
                   nsourceFile[k] = sourceFiles[k];
                   nfileDesc[k] = fileDesc[k];
                }
                sourceFiles = new String[j];
                fileDesc = new String[j];
                for (int k =0; k < j; k++){
                   sourceFiles[k] = nsourceFile[k];
                   fileDesc[k] = nfileDesc[k];
                }

             }

            //end of new for 5.4.1

        String shiplistDir = TDOF_DIR + "DL/" +"ModelKit."+orderNumber + ".customer_ShipTo_01.shiplist";
        if (new File(shiplistDir).exists()){
           String DRpackets = parseShiplist(shiplistDir);
           print(DRpackets, V_IMP);
           completeDL(sourceFiles, fileDesc, DRpackets);
        }else

           completeDL(sourceFiles, fileDesc);
		
			if(isDropbox)
			{
				uploadFileNames = sourceFiles;
			}

        print("Exiting handleIPDK()...", IMP);
   }
  //end of new for 5.1.1


    /**********************************************************************************************************************************************************************************************************************************/


    private void handleDK() {   // Design Kit


        print("Starting handleDK()...", V_IMP);

        getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getRequiredValue("FIRST_NAME");
        lastName         = getRequiredValue("LAST_NAME");

        int numPatches = 0;

        //new for 4.4.1
        String[] tkfixName;
        Vector toolkit_list = new Vector();
        String[] nsourceFile, nfileDesc;
        //end of new for 4.4.1
        
		//new for 6.3
		String[] tkAddlComponentName;
		Vector toolkitAddlComponentList = new Vector();
        

	String mediaStr;
        if(cd)
	    mediaStr = "CD";
	else
	    mediaStr = "DL";


        String tdofDir = TDOF_DIR + mediaStr + "/";


        String tdof = tdofDir + "ModelKit." + orderNumber + ".filtered";

        String techDirLoc
            = LINKS_DIR
            + technology + "/"
            + versionNo + "/";

        String BASE_MODEL_KIT
            = techDirLoc
            + "ModelKit/"
            + technology + "_" + versionNo + "_BaseModelKit.tar";

        if(getRequiredValue("BASE_MODEL_KIT").equals("N")) {
            this.baseModelKit = false;
            if(getRequiredValue("NONSTAND_DELIVERS_COUNT").equals("0") && getRequiredValue("CORES_COUNT").equals("0") && getRequiredValue("BASE_ORD_COUNT").equals("0")) {
                toolkitOnly = true;
                this.customModelKit = false;
            }
        }
        else
            this.baseModelKit = true;


	if(getRequiredValue("TOOLKIT").equals("Y"))
	    this.toolKit = true;

       //new for 4.4.1
        if (this.toolKit){
           if (getRequiredValue("TOOLKIT_FIX_IND").equalsIgnoreCase("Y")){
              if (getValue("TOOLKIT_FIX_NAME")== null || getValue("TOOLKIT_FIX_NAME").length() == 0){
                 print("User select TK Fix, but no Fix to delivery", V_IMP);
                 this.tkFix = false;
              }
              else{
                 tkfixName = getStringArray("TOOLKIT_FIX_NAME");
                 String tk;
                 for (int j=0; j<tkfixName.length; j++){

                    if (!stringAlreadyExists(tkfixName[j], toolkit_list))
                       toolkit_list.addElement(tkfixName[j]);
                 }
              this.tkFix = true;
              }

           }
           else
              this.tkFix = false;
              
        }
        
		/////////////////  6.3 Release changes ToolKit Addon  //////////////
          
		 if (getRequiredValue("TOOLKIT_COMPONENT").equalsIgnoreCase("Y")){
			if (getValue("TOOLKIT_COMPONENT_NAME")== null || getValue("TOOLKIT_COMPONENT_NAME").length() == 0){
			   print("User select TK Additional Component, but no additional component to deliver", V_IMP);
			   this.tkAddlComponent = false;
			}
			else{
			   tkAddlComponentName = getStringArray("TOOLKIT_COMPONENT_NAME");
			   String tk;
			   for (int j=0; j<tkAddlComponentName.length; j++){

				  if (!stringAlreadyExists(tkAddlComponentName[j], toolkitAddlComponentList))
					 toolkitAddlComponentList.addElement(tkAddlComponentName[j]);
				print("Added " + tkAddlComponentName[j] + " to toolkitAddlComponentList", V_IMP);
			   }
			this.tkAddlComponent = true;
			}

		 }
		 else
			this.tkAddlComponent = false;
		 
			 
        print("toolkit_list length "+toolkit_list.size(), V_IMP);
       //end of new for 4.4.1
	   print("in handleDK() toolkitAddlComponentList length "+toolkitAddlComponentList.size(), V_IMP);

	if( toolkitOnly && !this.toolKit )
	    handleException("No modelKit, toolKit or custom deliverables ordered");

	if(getRequiredValue("MODEL_TYPES_COUNT").equals("0")) {
            try {
                this.tdofString = new TDOF(tdof, mediaStr, this, true, false, orderNumber,TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}
	else {
            try {
                this.tdofString = new TDOF(tdof, mediaStr, this, true, false, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}

       //new for 3.12.1

        String delta_ind = getRequiredValue("DELTA_INDICATOR");
        if (delta_ind.equalsIgnoreCase("Y") && !toolkitOnly ){

            try {
               numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
            }
            catch(NumberFormatException e) {
               handleException(e, "PATCH_LIST_COUNT field is not numeric");
            }

            if(numPatches > 0) {
               try {
                  int[] sequenceOrder = sort(getStringArray("SEQUENCE_LIST"));

                  patchList = sequence(getStringArray("PATCH_LIST"), sequenceOrder);
                  patchDates = sequence(getStringArray("DATE_LIST"), sequenceOrder);
               }
               catch(Exception e) {
                  handleException(e, "thrown sorting or sequencing arrays");
               }

               if(patchList.length != numPatches || patchDates.length != numPatches)
                  handleException("The value of PATCH_LIST_COUNT does not match one or more list fields in MQ message");


               String tdofDRDir = TDOF_DIR + "DL/";
               String tdofDR = tdofDRDir + "Delta." + orderNumber + ".filtered";


               if(getRequiredValue("MODEL_TYPES_COUNT").equals("0")) {
                  try {
                     new TDOF(tdofDR, "DL", this, false, false, orderNumber,true,TESTING).createTDOF();
                  }
                  catch(Exception e) {
                     handleException(e, "thrown while writing TDOF file: " + tdofDR);
                  }
               }
               else {
                  try {
                     new TDOF(tdofDR, "DL", this, false, false, getRequiredValue("MESSAGE_ID"),true,TESTING).createTDOF();
                  }
                  catch(Exception e) {
                     handleException(e, "thrown while writing TDOF file: " + tdof);
                  }
               }
            }
        }

       //end of new for 3.12.1

	int numDLs = Integer.parseInt(getRequiredValue("SHIPTO_LOC_COUNT")) + 1;
	boolean additionalCDs = false;

	for(int shipToID=1; shipToID <= numDLs; shipToID++) {//for loop

	    addShipTo = shipToID - 1;

	    isPrimaryCustomer = false;
           //new for 3.12.1 fixpack
            majorAndDelta = false;
           //end of new for 3.12.1 fixpack
           //new for 4.4.1
            this.numTkFix = 0;
           //end of new for 4.4.1
	    if(shipToID == 1)
		isPrimaryCustomer = true;
	    else if( ! getRequiredValue("DL_" + addShipTo).equals("1") ) {
		additionalCDs = true;  // because at least one non-download additional ship location present
		continue;
	    }


	    String[] CUSTOM_MODEL_KITS = null;
           //new for 3.12.1 fixpack
            String[] DELTA_KITS = null;
           //end of new for 3.12.1 fixpack

            int numPlatforms = 0;
	    if(isPrimaryCustomer) {
                numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
                if(numPlatforms <= 0)
                    handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);
                this.platformsList = getStringArray("PLATFORMS_LIST");
	    }
	    else {
                numPlatforms = Integer.parseInt(getRequiredValue("PLATFORM_LIST_COUNT_" + addShipTo));
	        if(numPlatforms <= 0) {
	            handleMinorException("PLATFORM_LIST_COUNT_" + addShipTo + " has invalid value: " + numPlatforms + ". Not sending DL for additional ship location");
		    continue;
	        }
                this.platformsList = getStringArray("PLATFORM_LIST_" + addShipTo);
	    }


            if(numPlatforms != this.platformsList.length) {
                handleConditionalException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length + " for shipToID: " + shipToID);
		continue;
	    }



            if(toolkitOnly) {
		execCopyOrder(mediaStr);

	    }
	    else {//big else

                long[] result = getSpaceRequired(tdofDir, shipToID);
                long spaceRequired = result[0];
                print("spaceRequired "+spaceRequired, V_IMP);
                long baseModelKitInd = result[1];
                print("baseModelKitInd "+baseModelKitInd, V_IMP);
                if(baseModelKitInd == 1 && ! this.baseModelKit) {
		    this.baseModelKit = true;
	            handleMinorException("User does not want base model kit, but mkSize disagrees");
                }
                else if(baseModelKitInd == 0 && this.baseModelKit) {
		    this.baseModelKit = false;
	            print("Not important. User wants base model kit, but mkSize disagrees", V_IMP);
                }

                if(spaceRequired < 0) {
                    handleConditionalException("THIS SHOULD NOT HAPPEN! getSpaceRequired() returned " + spaceRequired);
		    continue;
                }
                else if(spaceRequired == 0) {
                    this.customModelKit = false;
                    print("NO CUSTOM_MODEL_KIT", V_IMP);
                }
                else {//deal with shipit

                    this.customModelKit = true;

                   //    String[][] bins = Mutex.getFreeBins((long)(spaceRequired * 1.2), this, true);

                    String bin = "bin1";

                    if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
                       CHIPS_CUSTOM_BINS_DIR += "/";
                    String dir = CHIPS_CUSTOM_BINS_DIR + bin;

                    String[][] bins = {{dir, "8000000000"}};
		    writeBinsFile(bins, orderNumber, tdofDir + "ModelKit." + orderNumber + "." + shipToID + ".bins");

                    try {

		        for(int i = 0; i < bins.length; i++)
                            print("Locked bin: " + bins[i][0], V_IMP);



                            String shipitCommand
                                = SHIPIT_SCRIPT
                                + " "
                                + SHIPIT_MODE
                                + " "
                                + technology
                                + " "
                                + versionNo
                                + " "
                                + orderNumber
                                + " "
                                + shipToID
                                + " "
                                + tdofDir
                                + " "
                                + "MAJOR"
                                + " "
                                + "ALL";


                            print("Executing: " + shipitCommand + " at " + new Date(), V_IMP);
                            flushDisplay();
                            int exitValue = execute(shipitCommand);
                            print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                            if(exitValue == 0) {
                                print("shipit executed successfully", V_IMP);
			        CUSTOM_MODEL_KITS = getCustomBuiltFiles(tdofDir + "ModelKit." + orderNumber + "." + shipToID + ".tarList");
			        this.numCustomModelKits = CUSTOM_MODEL_KITS.length;
		            }
                            else if(exitValue == 3) {
                                this.customModelKit = false;
                                handleMinorException("THIS SHOULD NOT HAPPEN: " + shipitCommand + " returned an exitValue of: 3 (No custom deliverables)");
                            }

                            else {
                                handleConditionalException("shipit returned an Exit Value of: " + exitValue + " (Unknown Error), bins might be in inconsistent state");
				continue;
			    }
                    }
                    catch(Throwable t) {
                        handleConditionalException(t, "thrown before, during or after executing shipit");
			continue;
                    }
                   /* finally {
                        Mutex.unlockBins(bins);
		        for(int i = 0; i < bins.length; i++)
                            print("Unlocked bin: " + bins[i][0], V_IMP);
                            }*/
                }
            }//end of big else

           //new for 3.12.1

            if(delta_ind.equalsIgnoreCase("Y") && isPrimaryCustomer && !toolkitOnly && numPatches > 0){//if delta_ind='Y'

               long spaceRequired = getDRSpaceRequired(tdofDir, shipToID, "ALL");

               if(spaceRequired < 0) {
                    handleConditionalException("THIS SHOULD NOT HAPPEN! getSpaceRequired() returned " + spaceRequired);
		    continue;
                }
              //new for 3.12.1
               else if (spaceRequired == 0){
                  print("NO Delta to Delivery in handleDK()", V_IMP);
               }
              //end of new for 3.12.1
               else {//do have delta to shipit

                 //  String[][] bins = Mutex.getFreeBins((long)(spaceRequired * 1.2), this, true);
                    String bin = "bin1";

                    if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
                       CHIPS_CUSTOM_BINS_DIR += "/";
                    String dir = CHIPS_CUSTOM_BINS_DIR + bin;

                    String[][] bins = {{dir, "8000000000"}};
		    writeBinsFile(bins, orderNumber, tdofDir + "Delta." + orderNumber + "." + shipToID + ".bins");

                    try {

		        for(int i = 0; i < bins.length; i++)
                            print("Locked bin: " + bins[i][0], V_IMP);

                            String shipitCommand
                                = SHIPIT_SCRIPT
                                + " "
                                + SHIPIT_MODE
                                + " "
                                + technology
                                + " "
                                + versionNo
                                + " "
                                + orderNumber
                                + " "
                                + shipToID
                                + " "
                                + tdofDir
                                + " "
                                + "DELTA"
                                + " "
                                + "ALL";


                            print("Executing: " + shipitCommand + " at " + new Date(), V_IMP);
                            flushDisplay();
                            int exitValue = execute(shipitCommand);
                            print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                            if(exitValue == 0) {
                                print("shipit executed successfully", V_IMP);
			        DELTA_KITS = getCustomBuiltFiles(tdofDir + "Delta." + orderNumber + "." + shipToID + ".tarList");
                                if (DELTA_KITS !=null)
                                   this.majorAndDelta = true; //which means do have delta to delivery

		            }
                            else {
                                handleConditionalException("shipit returned an Exit Value of: " + exitValue + " (Unknown Error), bins might be in inconsistent state");
				continue;
			    }
                    }
                    catch(Throwable t) {
                        handleConditionalException(t, "thrown before, during or after executing shipit");
			continue;
                    }
                   /*  finally {
                        Mutex.unlockBins(bins);
		        for(int i = 0; i < bins.length; i++)
                            print("Unlocked bin: " + bins[i][0], V_IMP);
                            }*/
                }
            }
           //end of new for 3.12.1

            String[] sourceFiles = null;
            String[] fileDesc = null;

            int numSourceFiles = 0;

            if(this.baseModelKit)
                numSourceFiles++;

            if(this.customModelKit)
	        numSourceFiles += CUSTOM_MODEL_KITS.length;

	    if(this.toolKit)
	        numSourceFiles += numPlatforms;

            //new for 3.12.1
            if(this.majorAndDelta)
               numSourceFiles += DELTA_KITS.length;
            //end of new for 3.12.1

            //new for 4.4.1
            if(this.tkFix){
               this.numTkFix = toolkit_list.size();
               numSourceFiles += numTkFix;
            }
            //end of new for 4.4.1

		if(this.tkAddlComponent){
			  this.numTkAddlComponent = toolkitAddlComponentList.size();
			  numSourceFiles += numTkAddlComponent;
			}


            if(numSourceFiles > 0) {
                sourceFiles = new String[numSourceFiles];
                fileDesc = new String[numSourceFiles];
            }
            else {
                handleConditionalException("No tookits or base or custom model or design kits ordered");
		continue;
	    }



            int i = 0;

	    if(this.toolKit) {
                for(i = 0; i < numPlatforms; i++) {
                    sourceFiles[i]
                        = getLinkTarget(
                            techDirLoc
                            + "ToolKit" + "/"
                            + this.platformsList[i] + "/"
                            + "toolkit");

                    fileDesc[i] = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[i];
                }
            }
            
		//New for 6.3  ////
		if(this.tkAddlComponent) {
			for(int t = 0; t < toolkitAddlComponentList.size(); t++) {
 	
				String tkAddlComp = (String)toolkitAddlComponentList.get(t);
				if(tkAddlComp != null && !tkAddlComp.trim().equals("") )
				{	
					print(" in handleDK() this.tkAddlComponent tkAddlComp " + tkAddlComp, V_IMP);  
	 	
					int platformIndex = tkAddlComp.trim().indexOf("_");
					String tkAddlCompPlatform = getTkPlatform(tkAddlComp.substring(0,platformIndex));
					print(" in handleDK() this.tkAddlComponent tkAddlCompPlatform " + tkAddlCompPlatform, V_IMP);
	 	
					String tkAddlCompFile = tkAddlCompPlatform + tkAddlComp.substring(platformIndex );
					print(" in handleDK() this.tkAddlComponent tkAddlCompFile " + tkAddlCompFile, V_IMP);
		
					 sourceFiles[i]
						 = getLinkTarget(
							 techDirLoc
							 + "ToolKit" + "/"
							 + tkAddlCompFile.trim() + "/"
							 + "toolkit");
				 
					print(" in handleDK() this.tkAddlComponent sourceFiles[j] " + sourceFiles[i], V_IMP);

					 fileDesc[i] = "Additional ToolKit Component for " + technology + " " + versionNo + " for " + tkAddlCompPlatform;
					 print(" in handleDK() this.tkAddlComponent fileDesc[j] " + fileDesc[i], V_IMP);
					 i++;
				}
			 }
	    }



            print ("i "+i+" "+sourceFiles.length, V_IMP);
            if(this.customModelKit) {
	        for(int j=0; j < CUSTOM_MODEL_KITS.length; j++) {
                    sourceFiles[i] = CUSTOM_MODEL_KITS[j];
                    fileDesc[i] = "Custom Model Kit for " + technology + " " + versionNo;
                    i++;
	        }
            }

           print("i "+i+" "+sourceFiles.length, V_IMP);

           //new for 3.12.1

            if (this.majorAndDelta && DELTA_KITS.length >0){
               this.numDR = DELTA_KITS.length;
                for(int j=0; j < DELTA_KITS.length; j++){
                    sourceFiles[i] = DELTA_KITS[j];
                    fileDesc[i] = "Delta Releases for ASIC Design Kit for "+technology + " " + versionNo;
                    i++;
                }
            }
            print("i "+i+" "+sourceFiles.length, V_IMP);
            //end of new for 3.12.1
            if(this.baseModelKit) {
                sourceFiles[i] = BASE_MODEL_KIT;
                fileDesc[i] = "Base Model Kit for " + technology + " " + versionNo;
                i++; //new for 4.4.1
                print("i "+i+" "+sourceFiles.length, V_IMP);
            }

            //new for 4.4.1
             if (this.tkFix){

                   for (int m=0; m<toolkit_list.size(); m++){

                      if (new File(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit").exists()){//check whether the file exist

                         String filepath = getLinkTarget(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit");
                         if (new File(filepath).exists()){//check whether the file exist
                            sourceFiles[i]=getLinkTarget(CHIPS_DL_DIR+"FullPDKit/"+toolkit_list.elementAt(m)+"/fullpdkit");

                            fileDesc[i]="Toolkit Fix for "+toolkit_list.elementAt(m);

                            i++;
                            print("i "+i+" "+sourceFiles.length, V_IMP);
                         }
                      }
                   }
             }

             if (i != sourceFiles.length) { //in case there are some invalid tk fix in the sourceFile and fileDesc array
                this.numTkFix = numTkFix - (sourceFiles.length -i);
                nsourceFile = new String[i];
                nfileDesc = new String[i];
                for (int k =0; k < i; k++){
                   nsourceFile[k] = sourceFiles[k];
                   nfileDesc[k] = fileDesc[k];
                }
                sourceFiles = new String[i];
                fileDesc = new String[i];
                for (int k =0; k < i; k++){
                   sourceFiles[k] = nsourceFile[k];
                   fileDesc[k] = nfileDesc[k];
                }

             }
            //end of new for 4.4.1

            //new for 4.2.1
             if(majorAndDelta){

                String shiplistDir = TDOF_DIR + "DL/" +"Delta."+orderNumber + ".customer_ShipTo_01.shiplist";
                if (new File(shiplistDir).exists()){
                   String DRpackets = parseShiplist(shiplistDir);
                   print(DRpackets, V_IMP);
                   completeDL(sourceFiles, fileDesc, DRpackets);
                }
             }
             else
                completeDL(sourceFiles, fileDesc);
                
             if(isDropbox)
             {
             	uploadFileNames = sourceFiles;
             }
            //end of new for 4.2.1

	}


        if( ! cd && additionalCDs )
            mail(SM_REQ_ALOC, this.tdofString);
        // Send email to shipping manager for additional shipping locations.
        // if cd = true this info has already been sent in the cd TDOF file


        print("Exiting handleDK()...", IMP);
    }




    /**********************************************************************************************************************************************************************************************************************************/


    private long[] getSpaceRequired(String tdofDir, int shipToID) {

        String tdofSizeFile = tdofDir + "ModelKit." + orderNumber + "." + shipToID + ".size";

        String cmd =
            MK_SIZE_SCRIPT
            + " "
            + SHIPIT_MODE
            + " "
            + technology
            + " "
            + versionNo
            + " "
            + orderNumber
            + " "
            + shipToID
            + " "
            + tdofDir
            + " "
            + "MAJOR"
            + " "
            + "ALL "    //new for 3.12.1
	    + COPY_ORDER_DIR;

        String pkStopped
            = EDESIGN_HOME_DIR
            + "ASICTECH/"
            + technology + "/"
            + versionNo + "/"
            + "PreviewKit/"
            + "shipping.STOPPED";

        String dkStopped
            = EDESIGN_HOME_DIR
            + "ASICTECH/"
            + technology + "/"
            + versionNo + "/"
            + "ModelKit/"
            + "shipping.STOPPED";


        File pkShippingStopped = new File(pkStopped);
        File dkShippingStopped = new File(dkStopped);


        if(pkShippingStopped.exists() || dkShippingStopped.exists())
            mqSendQueued(0);

        int minutesWaited=60;
        while(pkShippingStopped.exists()) {

           if(minutesWaited >= 60) {
                print("waiting for file: " + pkStopped + " to disappear at " + new Date(), V_IMP);
                flushDisplay();
                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
                // shipping.STOPPED is present, so wait and try again
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + pkStopped + " to dissapear");
            }
        }

        minutesWaited=60;
        while(dkShippingStopped.exists()) {

           if(minutesWaited >= 60) {
                print("waiting for file: " + dkStopped + " to disappear at " + new Date(), V_IMP);
                flushDisplay();
                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
                // shipping.STOPPED is present, so wait and try again
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + dkStopped + " to dissapear");
            }
        }



        long spaceRequired = -1;
	long baseModelKit = -1;

        boolean tryAgain = true;

        while(tryAgain) {

            tryAgain = false;

            print("Executing: " + cmd + " at " + new Date(), V_IMP);
            flushDisplay();

            int exitValue = execute(cmd);

            if(exitValue == 0) {
                if(new File(tdofSizeFile).isFile()) {
                    String result = readFile(tdofSizeFile, 128).trim();
                    print("Executed command at " + new Date() + "\nResult:\n" + result, V_IMP);

		    int index1, index2;

		    index2 = result.indexOf('\n');
                    spaceRequired = Long.parseLong(result.substring(0,index2));

		    index1 = result.indexOf(';', index2+1);
		    index2 = result.indexOf(';', index1+1);
		    long minBinSize = Long.parseLong(result.substring(index1+1,index2));

		    index1 = result.indexOf(';', index2+1);
		    index2 = result.indexOf(';', index1+1);
		    String bmk = result.substring(index1+1,index2);

		    if(bmk.equalsIgnoreCase("Yes"))
			baseModelKit = 1;
		    else if(bmk.equalsIgnoreCase("No"))
			baseModelKit = 0;
		    else
                        handleException(cmd + " returned BaseModelKitIndicator as " + bmk);

                    if(spaceRequired < 0)
                        handleException(cmd + " returned space required as " + spaceRequired);
                }
                else
                    handleException(cmd + " returned 0 but " + tdofSizeFile + " does not exist");
            }
            else {

                print("Executed: " + cmd + " at " + new Date() + " Error exit value: " + exitValue, V_IMP);

                if(exitValue == 1) {

                    minutesWaited=60;
                    while(pkShippingStopped.exists()) {

                        tryAgain = true;

                	if(minutesWaited >= 60) {
                            print("waiting for file: " + pkStopped + " to disappear at " + new Date(), V_IMP);
                	    flushDisplay();
                	    minutesWaited = 0;
            		}

                        try {
                            // shipping.STOPPED is present, so wait and try again
                            sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		            minutesWaited += SHIPIT_WAIT_TIME;
                        }
                        catch(InterruptedException e) {
                            handleException("Interrupted while waiting for " + pkStopped + " to dissapear");
                        }
                    }

                    print("No file: " + pkStopped + " exists at " + new Date(), V_IMP);

                    if(tryAgain)
                        continue;
                    else
                        handleException("mkSize returned an Exit Value of: 1 and " + pkStopped + " does not exist");

                }
                else if(exitValue == 2) {
                    handleException("mkSize returned an Exit Value of: 2 (Invalid Command Line Parameter)");
                }
                else if(exitValue == 3) {
                    handleException("mkSize returned an Exit Value of: 3 (Invalid TDOF data or Invalid TDOF format)");
                }
                else if(exitValue == 4) {
                    handleException("mkSize returned an Exit Value of: 4 (Error creating log entry)");
                }
               //new for 3.12.1
                else if(exitValue == 5) {
                   spaceRequired = 0;

                   print("mkSize Major has nothing to delivery", V_IMP);

                   //new for 3.12.1 fixpack
                   if(new File(tdofSizeFile).isFile()) {
                    String result = readFile(tdofSizeFile, 128).trim();
                    print("Executed command at " + new Date() + "\nResult:\n" + result, V_IMP);

		    int index1, index2;

		    index2 = result.indexOf('\n');
                    spaceRequired = Long.parseLong(result.substring(0,index2));

		    index1 = result.indexOf(';', index2+1);
		    index2 = result.indexOf(';', index1+1);
		    long minBinSize = Long.parseLong(result.substring(index1+1,index2));

		    index1 = result.indexOf(';', index2+1);
		    index2 = result.indexOf(';', index1+1);
		    String bmk = result.substring(index1+1,index2);

		    if(bmk.equalsIgnoreCase("Yes"))
			baseModelKit = 1;
		    else if(bmk.equalsIgnoreCase("No"))
			baseModelKit = 0;
		    else
                        handleException(cmd + " returned BaseModelKitIndicator as " + bmk);
                   }
                  //end of new for 3.12.1 fixpack
                   //  baseModelKit = 0;//comment out for 3.12.1 fixpack

                }
               //end of new for 3.12.1
                else {
                    handleException("mkSize returned an Exit Value of: " + exitValue + " (Unknown Error)");
                }
            }
        }


        File f = new File(tdofSizeFile);
        if(f.isFile())
            f.delete();

        return new long[] {spaceRequired, baseModelKit};

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private int waitOnShipit(String modelKitName, int shipitExitValue, String shipitCommand) {

        print("Starting waitOnShipit for file: " + modelKitName + " at " + new Date() + "\nshipit Exit Value was: " + shipitExitValue, V_IMP);

        flushDisplay();

        if(shipitExitValue == 2)
            mail(SHIPIT_FAIL, shipitCommand);  // to shipping manager advising that shipit failed

        long t1 = CRITICAL_DAYS_1 * DAYS;
        long t2 = CRITICAL_DAYS_2 * DAYS;
        long t3 = CRITICAL_DAYS_3 * DAYS;

        boolean c1 = false, c2 = false, c3 = false; // indicates if critical stages 1, 2 and 3 have been passed yet.
        File modelKit = new File(modelKitName);
        long waitTime = 0;

        while( ! modelKit.isFile()) {
            if(waitTime > t1) {
                if( ! c1) {
                    mqSendQueued(shipitExitValue);
                    modelKitWait = CRITICAL_DAYS_1;
                    print("Waited for file: " + modelKitName + " for " + modelKitWait + " days " + " at " + new Date(), V_IMP);
                    flushDisplay();
                    mail(NO_MK_WAIT_1, shipitCommand);
                    c1 = true;
                }
                else if (waitTime > t2) {
                    if( ! c2) {
                        modelKitWait = CRITICAL_DAYS_2;
                        print("Waited for file: " + modelKitName + " for " + modelKitWait + " days " + " at " + new Date(), V_IMP);
                        flushDisplay();
                        mail(NO_MK_WAIT_2, shipitCommand);
                        c2 = true;
                    }
                    else if(waitTime > t3) {
                        modelKitWait = CRITICAL_DAYS_3;
                        print("Waited for file: " + modelKitName + " for " + modelKitWait + " days " + " at " + new Date() + "\nNow exiting...", V_IMP);
                        mail(NO_MK_STOP, shipitCommand);
                        return 1;    // modelKit never showed up
                    }
                }
            }

            try {
                long sleepTime = SHIPIT_WAIT_TIME * 60 * 1000;
                sleep(sleepTime);
                waitTime += sleepTime;
            }
            catch(InterruptedException ie) {
                handleException(ie, "Wait for " + modelKit + " to appear was interrupted");
            }
        }

        print("Got modelkit, exiting waitOnShipit at " + new Date(), V_IMP);
        return 0;   // modelKit is ready
    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void handlePK() {   // Preview Kit

        print("Starting handlePK()...", V_IMP);
        
        String[] sourceFile = {
            LINKS_DIR
            + technology + "/"
            + versionNo + "/"
            + "PreviewKit/"
            + technology + "_" + versionNo + "_PreviewKit.tar"
        };

        String[] fileDesc = {"Technology Preview Kit for " + technology + " " + versionNo};

        String pkStopped
            = EDESIGN_HOME_DIR
            + "ASICTECH/"
            + technology + "/"
            + versionNo + "/"
            + "PreviewKit/"
            + "shipping.STOPPED";

        File pkShippingStopped = new File(pkStopped);


	if(pkShippingStopped.exists())
	    mqSendQueued(0);


        int minutesWaited=60;
        while(pkShippingStopped.exists()) {

           if(minutesWaited >= 60) {
                print("waiting for file: " + pkStopped + " to disappear at " + new Date(), V_IMP);
                flushDisplay();
                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
                // shipping.STOPPED is present, so wait and try again
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + pkStopped + " to dissapear");
            }
        }

        print("No file: " + pkStopped + " exists at " + new Date(), V_IMP);

        completeDL(sourceFile, fileDesc);
		if(isDropbox)
		{
			uploadFileNames = sourceFile;
		}

        print("Exiting handlePK()...", IMP);
    }


    /**********************************************************************************************************************************************************************************************************************************/



/*
*** EDGE_USERID
*** MESSAGE_ID ( first 6 chars of userid + timestamp ? )
*** TIMESTAMP
*** TECHNOLOGY
*** VERSION_NO
*** ASIC_CODENAME
*** CUSTOMER_PROJNAME
*** USERS_COMPANY
*** FIRST_NAME
*** LAST_NAME
*** E_MAIL

      FSE_TYPE_1
      FSE_NAME_1
      FSE_LOC_1
      FSE_PHONE_1
      FSE_EMAIL_1

*** PATCH_LIST_COUNT
*     PATCH_LIST                    <--- required if previous field is > 0

*** PREV_CORE_COUNT
*    PREV_CORE_LIST             <--- required if previous field is > 0
*** PREV_NSTD_COUNT
*    PREV_NSTD_LIST             <--- required if previous field is > 0
*** PREV_BAS_ORD_COUNT
*    PREV_BAS_ORD_LIST       <--- required if previous field is > 0

*** PLATFORMS_COUNT
*    PLATFORMS_LIST             <--- Required if above field is non-zero (From PLATFORMS table)
*/

    private void handleDRinit() {   // cherry-picking Delta Releases

        print("Starting handleDRinit()...", V_IMP);

        usersEmail   = getRequiredValue("E_MAIL");
        fseEmail = getValue("FSE_EMAIL_1");
        technology = getTechnologyName(getRequiredValue("TECHNOLOGY"));
        versionNo  = getRequiredValue("VERSION_NO");

        getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getRequiredValue("FIRST_NAME");
        lastName         = getRequiredValue("LAST_NAME");

        String tdofDir = TDOF_DIR + "DL/";
        String tdof    = tdofDir + "Delta." + orderNumber;
        String toolMTsFile = tdofDir + "Delta." + orderNumber + ".toolMTs";

        int numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));

        if(numPlatforms <= 0)
            handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);

        this.platformsList = getStringArray("PLATFORMS_LIST");
        if(numPlatforms != this.platformsList.length)
            handleException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length);


        int numPatches = 0;
        try {
            numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
        }
        catch(NumberFormatException e) {
            handleException(e, "PATCH_LIST_COUNT field is not numeric");
        }

        if(numPatches <= 0)
            handleException("Patch List is empty");


        try {
            new TDOF(tdof, "DL", this, false, true, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
        }
        catch(Exception e) {
            handleException(e, "thrown while writing TDOF file: " + tdof);
        }


        String getToolsCommand
                = GET_TOOLS_SCRIPT
                + " "
                + SHIPIT_MODE
                + " "
                + technology
                + " "
                + versionNo
                + " "
                + orderNumber
                + " "
                + tdofDir
                + " "
                + "DELTA"
                + " "
                + COPY_ORDER_DIR;

        print("Executing: " + getToolsCommand + " at " + new Date(), V_IMP);
        flushDisplay();
        int exitValue = execute(getToolsCommand);
        print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


        if(exitValue == 0)
            print("getTools executed successfully", V_IMP);
        else {
            handleException(getToolsCommand + " returned an exitValue of: " + exitValue);
        }

        if( ! new File(toolMTsFile).isFile() )
            handleException(getToolsCommand + " returned OK but no toolMTs file: " + toolMTsFile);


        try {
	    String mqMsg = parseToolMTsFile(toolMTsFile);

            print("Sending MQ with CorrId: EDQ3_MODEL_TYPES and messageId: " + this.orderNumber, V_IMP);
            print("Message length in bytes: " + mqMsg.length(), V_IMP);
	    flushDisplay();
            OrderProcessor.sendMQMessage("EDQ3_MODEL_TYPES", this.orderNumber, mqMsg, transientMsgPersistent);   // orderNumber is actually messageId
            print("Sent MQ", V_IMP);
            print("Message sent by MQ:\n" + mqMsg, NOT_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown parsing: " + toolMTsFile + " or while sending MQ");
        }

        print("Exiting handleDRinit()...", IMP);

    }

/****************************************************************************************************************************************************************************************************************/

  //new for 3.12.1

  private boolean handleDRcatchup () {   // cherry-picking Delta Releases for catch-me-up via major release order page

        print("Starting handleDRcatchup()...", V_IMP);

        boolean rt = false;

        String tdofDRDir = TDOF_DIR + "DL/";
        String tdofDR    = tdofDRDir + "Delta." + orderNumber;
        String toolDRMTsFile = tdofDRDir + "Delta." + orderNumber + ".toolMTs";

        int numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));

        if(numPlatforms <= 0)
            handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);

        this.platformsList = getStringArray("PLATFORMS_LIST");
        if(numPlatforms != this.platformsList.length)
            handleException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length);


       /* int numPatches = 0;
        try {
            numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
        }
        catch(NumberFormatException e) {
            handleException(e, "PATCH_LIST_COUNT field is not numeric");
        }

        if(numPatches <= 0)
            handleException("Patch List is empty");
       */

        try {
            new TDOF(tdofDR, "DL", this, false, true, getRequiredValue("MESSAGE_ID"), true,TESTING).createTDOF();
        }
        catch(Exception e) {
            handleException(e, "thrown while writing TDOF file: " + tdofDR);
        }


        String getToolsCommand
                = GET_TOOLS_SCRIPT
                + " "
                + SHIPIT_MODE
                + " "
                + technology
                + " "
                + versionNo
                + " "
                + orderNumber
                + " "
                + tdofDRDir
                + " "
                + "DELTA"
                + " "
                + COPY_ORDER_DIR;

        print("Executing: " + getToolsCommand + " at " + new Date(), V_IMP);
        flushDisplay();
        int exitValue = execute(getToolsCommand);
        print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


        if(exitValue == 0)
            print("getTools executed successfully", V_IMP);
       //new for 4.4.1
        else if (exitValue == 5){
           rt = true;
           print ("No DA tools for delta", V_IMP);
        }
       //end of new for 4.4.1
        else {
            handleException(getToolsCommand + " returned an exitValue of: " + exitValue);
        }

        if( ! new File(toolDRMTsFile).isFile() )
            handleException(getToolsCommand + " returned OK but no toolMTs file: " + toolDRMTsFile);
        return rt;

    }
  //end of new for 3.12.1


    /**********************************************************************************************************************************************************************************************************************************/


    private void handleDR() {   // Delta Release

        print("Starting handleDR()...", V_IMP);
        String packetHistory="";
        asicCodeName = getRequiredValue("ASIC_CODENAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");
        usersCompany     = getRequiredValue("USERS_COMPANY");
        firstName        = getValue("FIRST_NAME");
        lastName         = getValue("LAST_NAME");

	String deltaInd = null;
        if(getRequiredValue("INDIVIDUAL_DELTAS").equals("1")) {
            individualDeltas = true;
	    deltaInd = "SEPARATE";
	}
        else {
            individualDeltas = false;
	    deltaInd = "ALL";
	}



        int numPatches = 0;
        try {
            numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
        }
        catch(NumberFormatException e) {
            handleException(e, "PATCH_LIST_COUNT field is not numeric");
        }

        if(numPatches > 0) {
            try {
                int[] sequenceOrder = sort(getStringArray("SEQUENCE_LIST"));

                patchList = sequence(getStringArray("PATCH_LIST"), sequenceOrder);
                patchDates = sequence(getStringArray("DATE_LIST"), sequenceOrder);
            }
            catch(Exception e) {
                handleException(e, "thrown sorting or sequencing arrays");
            }
        }
        else
            handleException("Patch List is empty");


        if(patchList.length != numPatches || patchDates.length != numPatches)
            handleException("The value of PATCH_LIST_COUNT does not match one or more list fields in MQ message");


	if(numPatches > 25) {
            individualDeltas = false;
            deltaInd = "ALL";
        }


        String tdofDir = TDOF_DIR + "DL/";
        String tdof = tdofDir + "Delta." + orderNumber + ".filtered";


	if(getRequiredValue("MODEL_TYPES_COUNT").equals("0")) {
            try {
                new TDOF(tdof, "DL", this, false, false, orderNumber,TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}

	else if (orderBy.equalsIgnoreCase("SYSTEM")){//new for 5.1.1
           try {
                new TDOF(tdof, "DL", this, false, false, orderNumber,TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
          //end of new for 5.1.1
        }else {
            try {
                new TDOF(tdof, "DL", this, false, false, getRequiredValue("MESSAGE_ID"),TESTING).createTDOF();
            }
            catch(Exception e) {
                handleException(e, "thrown while writing TDOF file: " + tdof);
            }
	}


// int numDLs = Integer.parseInt(getRequiredValue("SHIPTO_LOC_COUNT")) + 1;
	int numDLs = 1;
	boolean additionalCDs = false;

	for(int shipToID=1; shipToID <= numDLs; shipToID++) {

	    addShipTo = shipToID - 1;

	    isPrimaryCustomer = false;
	    if(shipToID == 1)
		isPrimaryCustomer = true;
	    else if( ! getRequiredValue("DL_" + addShipTo).equals("1") ) {
		additionalCDs = true;  // because at least one non-download additional ship location present
		continue;
	    }


	    String[] DELTA_KITS = null;

            int numPlatforms = 0;
	    if(isPrimaryCustomer) {
                numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
                if(numPlatforms <= 0)
                    handleException("PLATFORMS_COUNT has invalid value: " + numPlatforms);
                this.platformsList = getStringArray("PLATFORMS_LIST");
	    }
	    else {
                numPlatforms = Integer.parseInt(getRequiredValue("PLATFORM_LIST_COUNT_" + addShipTo));
	        if(numPlatforms <= 0) {
	            handleMinorException("PLATFORM_LIST_COUNT_" + addShipTo + " has invalid value: " + numPlatforms + ". Not sending DL for additional ship location");
		    continue;
	        }
                this.platformsList = getStringArray("PLATFORM_LIST_" + addShipTo);
	    }


            if(numPlatforms != this.platformsList.length) {
                handleConditionalException("The value of PLATFORMS_COUNT: " + numPlatforms + " does not match the number of platforms in PLATFORMS_LIST: " + this.platformsList.length + " for shipToID: " + shipToID);
		continue;
	    }


           //new for 4.2.1 per Karen
           //CSR IBMCC0010204
           //String packetHistory = getValue("PacketHistory");
            try{
             packetHistory = getPacketHistory(technology, versionNo, customerProjname, asicCodeName);
            }
            catch (SQLException ex){
              print("Error: Stacktrace: \n" + getStackTrace(ex), V_IMP);
           }
          //end of CSR IBMCC0010204
           if (getRequiredValue("CatchMeUp_Ind").equalsIgnoreCase("Y") && getRequiredValue("CatchMeUp_Type").equalsIgnoreCase("LAST_ORDER")) {
             //write file Delta.orderID.packetHistory
              String packetFile = tdofDir + "Delta." + orderNumber + ".packetHistory";
              if (packetHistory == null)
                 packetHistory="";
              createPacketHistory(packetFile, packetHistory);
           }
          //end of new for 4.2.1


                long spaceRequired = getDRSpaceRequired(tdofDir, shipToID, deltaInd);

                if(spaceRequired <= 0) {
                    handleConditionalException("THIS SHOULD NOT HAPPEN! getSpaceRequired() returned " + spaceRequired);
		    continue;
                }
                else {

                  //   String[][] bins = Mutex.getFreeBins((long)(spaceRequired * 1.2), this, true);
                    String bin = "bin1";

                    if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
                       CHIPS_CUSTOM_BINS_DIR += "/";
                    String dir = CHIPS_CUSTOM_BINS_DIR + bin;

                    String[][] bins = {{dir, "8000000000"}};
		    writeBinsFile(bins, orderNumber, tdofDir + "Delta." + orderNumber + "." + shipToID + ".bins");

                    try {

		        for(int i = 0; i < bins.length; i++)
                            print("Locked bin: " + bins[i][0], V_IMP);

                            String shipitCommand
                                = SHIPIT_SCRIPT
                                + " "
                                + SHIPIT_MODE
                                + " "
                                + technology
                                + " "
                                + versionNo
                                + " "
                                + orderNumber
                                + " "
                                + shipToID
                                + " "
                                + tdofDir
                                + " "
                                + "DELTA"
                                + " "
                                + deltaInd;


                            print("Executing: " + shipitCommand + " at " + new Date(), V_IMP);
                            flushDisplay();
                            int exitValue = execute(shipitCommand);
                            print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                            if(exitValue == 0) {
                                print("shipit executed successfully", V_IMP);
			        DELTA_KITS = getCustomBuiltFiles(tdofDir + "Delta." + orderNumber + "." + shipToID + ".tarList");
		            }
                            else {
                                handleConditionalException("shipit returned an Exit Value of: " + exitValue + " (Unknown Error), bins might be in inconsistent state");
				continue;
			    }
                    }
                    catch(Throwable t) {
                        handleConditionalException(t, "thrown before, during or after executing shipit");
			continue;
                    }
                   /*  finally {
                        Mutex.unlockBins(bins);
		        for(int i = 0; i < bins.length; i++)
                            print("Unlocked bin: " + bins[i][0], V_IMP);
                            }*/
                }


            String[] sourceFiles = null;
            String[] fileDesc = null;

            int numSourceFiles = DELTA_KITS.length;

            if(numSourceFiles > 0) {
                sourceFiles = DELTA_KITS;
                fileDesc = new String[numSourceFiles];
            }
            else {
                handleConditionalException("No kits returned");
		continue;
	    }


            String desc = "Delta Releases for ASIC Design Kit for " + technology + " " + versionNo;
	    for(int i=0; i < DELTA_KITS.length; i++)
                fileDesc[i] = desc;

           //new for 4.2.1
            String shiplistDir = TDOF_DIR + "DL/" +"Delta."+orderNumber + ".customer_ShipTo_01.shiplist";
            if (new File(shiplistDir).exists()){
               String DRpackets = parseShiplist(shiplistDir);
               print(DRpackets, V_IMP);
               completeDL(sourceFiles, fileDesc, DRpackets);
            }
		if(isDropbox)
		{
			uploadFileNames = sourceFiles;
		}

           //end of new for 4.2.1
           // completeDL(sourceFiles, fileDesc);
		
	}

        mail(ALOC_DR_ORD);
        // Send email to additional shipping locations that customer has received delta release

        print("Exiting handleDR()...", IMP);

    }


    /*************************************************************************************************         new for 4.2.1 ***************************************************************************************************************/

   private void createPacketHistory(String packetFile,String packetHistory){

        print("createPacketHistory....", V_IMP);

        String line, pk_realname;
        try {
           StringBuffer output = new StringBuffer();
           StringTokenizer tok = new StringTokenizer (packetHistory, ",");

           while (tok.hasMoreTokens()){
              line = tok.nextToken().trim();
             // pk_realname = line.substring(0, line.length()-3);
             //  output.append(pk_realname+"\n");
              output.append(line + "\n"); //fixpack of 4.5.1.
           }

          // File f = new File(packetFile);

           FileOutputStream fos = new FileOutputStream(packetFile, false);
           fos.write(output.toString().getBytes());
           fos.close();
           print(output.toString(), V_IMP);
           print("Exiting createPacketHistory...", V_IMP);

        }catch(IOException ioe){
           handleException(ioe, "failed to create packetHistory file "+packetFile);
        }


   }


    /**********************************************************************************************************************************************************************************************************************************/

    private long getDRSpaceRequired(String tdofDir, int shipToID, String packageType) {

        String tdofSizeFile = tdofDir + "Delta." + orderNumber + "." + shipToID + ".size";

        String cmd =
            MK_SIZE_SCRIPT
            + " "
            + SHIPIT_MODE
            + " "
            + technology
            + " "
            + versionNo
            + " "
            + orderNumber
            + " "
            + shipToID
            + " "
            + tdofDir
            + " "
            + "DELTA"
            + " "
            + packageType  //new for 3.12.1
            +" "
	    + COPY_ORDER_DIR;

        String drStopped
            = EDESIGN_HOME_DIR
            + "ASICTECH/"
            + technology + "/"
            + versionNo + "/"
            + "DeltaReleases/"
            + "shipping.STOPPED";

        File drShippingStopped = new File(drStopped);


        if(drShippingStopped.exists())
            mqSendQueued(0);

        int minutesWaited=60;
        while(drShippingStopped.exists()) {

           if(minutesWaited >= 60) {
                print("waiting for file: " + drStopped + " to disappear at " + new Date(), V_IMP);
                flushDisplay();
                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
                // shipping.STOPPED is present, so wait and try again
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + drStopped + " to dissapear");
            }
        }



        long spaceRequired = -1;


        print("Executing: " + cmd + " at " + new Date(), V_IMP);
        flushDisplay();

        int exitValue = execute(cmd);

        if(exitValue == 0) {
                if(new File(tdofSizeFile).isFile()) {
                    String result = readFile(tdofSizeFile, 128).trim();
                    print("Executed command at " + new Date() + "\nResult:\n" + result, V_IMP);

                    int index1, index2;

                    index2 = result.indexOf('\n');
                    spaceRequired = Long.parseLong(result.substring(0,index2));

                    index1 = result.indexOf(';', index2+1);
                    index2 = result.indexOf(';', index1+1);
                    long minBinSize = Long.parseLong(result.substring(index1+1,index2));

                    if(spaceRequired < 0)
                        handleException(cmd + " returned space required as " + spaceRequired);
                }
                else
                    handleException(cmd + " returned 0 but " + tdofSizeFile + " does not exist");
        }
       //new for 3.12.1
        else if (exitValue == 5){
           spaceRequired = 0;
           print("mkSize Delta has nothing to delivery", V_IMP);
        }
      //end of new for 3.12.1.

        else {
                print("Executed: " + cmd + " at " + new Date() + " Error exit value: " + exitValue, V_IMP);
                handleException("mkSize returned an Exit Value of: " + exitValue);
        }


        File f = new File(tdofSizeFile);
        if(f.isFile())
            f.delete();

        return spaceRequired;

    }


    /**********************************************************************************************************************************************************************************************************************************/



   private void oldHandleDR() {   // Delta Release

        print("Starting handleDR()...", V_IMP);

        String[] packetList = null;

        String tarFile
            = LINKS_DIR
            + technology + "/"
            + versionNo + "/"
            + "DeltaReleases/"
            + "common_dr.tar";

        String DR_PACKETS_DIR = getProperty("DR_PACKETS_DIR");

        if( ! DR_PACKETS_DIR.endsWith("/") )
            DR_PACKETS_DIR += "/";

        String packetsDir
            = DR_PACKETS_DIR
            + getTechnologyAlias(technology) + "/"
            + versionNo + "Patch";

        getRequiredValue("ASIC_CODENAME");

        firstName = getValue("FIRST_NAME");
        lastName = getValue("LAST_NAME");

        customerProjname = getRequiredValue("CUSTOMER_PROJNAME");

        if(getRequiredValue("INDIVIDUAL_DELTAS").equals("1"))
            individualDeltas = true;
        else
            individualDeltas = false;


        int numPatches = 0;
        try {
            numPatches = Integer.parseInt(getRequiredValue("PATCH_LIST_COUNT"));
        }
        catch(NumberFormatException e) {
            handleException(e, "PATCH_LIST_COUNT field is not numeric");
        }

        if(numPatches > 0) {
	    try {
                int[] sequenceOrder = sort(getStringArray("SEQUENCE_LIST"));

                patchList = sequence(getStringArray("PATCH_LIST"), sequenceOrder);
                patchDates = sequence(getStringArray("DATE_LIST"), sequenceOrder);
                packetList = sequence(getStringArray("PACKET_LIST", ";"), sequenceOrder);
	    }
	    catch(Exception e) {
		handleException(e, "thrown sorting or sequencing arrays");
	    }
        }
        else
            handleException("Patch List is empty");


        if(patchList.length != numPatches || patchDates.length != numPatches || packetList.length != numPatches)
            handleException("The value of PATCH_LIST_COUNT does not match one or more list fields in MQ message");


        String[] sourceFiles;

        if(individualDeltas) {

            sourceFiles = new String[numPatches];

            for(int i = 0; i < numPatches; i++) {

                int length = 0;
                String[] fileList = tokenizeString(packetList[i]);
                String files = "";
                String packet;

                for(int j = 0; j < fileList.length; j++) {
                    packet = patchList[i] + "/" + fileList[j];
                    length += new File(packetsDir + "/" + packet).length();
                    files += packet + " ";
                }

                length += new File(tarFile).length() * 2;   // twice the length because it will be in two places

                long bufferedSpaceRequiredInKB = (length / 1024) + 1024;        // allowing a buffer of 1 MB (size in kb)

               //  String destBin = Mutex.getFreeBin(length + (1024*1024), this);
                String bin = "bin1";

                if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
                   CHIPS_CUSTOM_BINS_DIR += "/";
                String destBin = CHIPS_CUSTOM_BINS_DIR + bin;


                try {

                    sourceFiles[i] = destBin + "/" + orderNumber + "/" + patchList[i] + ".tar";

                    String cmd
                        = getProperty("CREATE_DR_SCRIPT")
                        + " "
                        + destBin
                        + " "
                        + orderNumber
                        + " "
                        + patchList[i] + ".tar"       // dest file
                        + " "
                        + tarFile                     // pre-built tar file
                        + " "
                        + technology
                        + " "
                        + versionNo
                        + " "
                        + packetsDir                 // location of packets
                        + " "
                        + files;


                    print("Executing: " + cmd + " at " + new Date(), V_IMP);
                    print("Allocated " + String.valueOf(bufferedSpaceRequiredInKB) + " KB in " + destBin, V_IMP);
                    flushDisplay();
                    int exitValue = execute(cmd);
                    print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                    if(exitValue == 0)
                        print("delta release created successfully", V_IMP);
                    else
                        handleException(cmd + " returned an exitValue of " + exitValue);

                }
                catch(Throwable t) {
                    handleException(t, "thrown creating delta release");
                }
                 finally {
                    Mutex.unlockBin(destBin);
                    print("Unlocked bin: " + destBin, V_IMP);
                    }


                try {
                    Date d = DRsourceDate.parse(patchDates[i]);
                    patchDates[i] = DRtargetDate.format(d);
                }
                catch(java.text.ParseException e) {
                    handleMinorException(e, "thrown parsing date: " + patchDates[i]);
                }
            }
        }
        else {

            final long FILE_SIZE_LIMIT = 1480 * 1000 * 1000; // 1480 MB
	    final int ARG_MAX = 21 * 1024; // 21 K

            int count = 0;
            int numFiles = 0;
            boolean multipleFiles = false;

            Vector fileVector = new Vector();

            while(count < numPatches) {

                numFiles++;

                long length = new File(tarFile).length() * 2;   // twice the length because it will be in two places
	        int numChars = 0;
                String files = "";

                while(count < numPatches) {

                    String[] fileList = tokenizeString(packetList[count]);
                    String packet;

                    int deltaSize = 0;
                    String deltaFiles = "";

                    for(int j = 0; j < fileList.length; j++) {
                        packet = patchList[count] + "/" + fileList[j];
                        deltaSize += new File(packetsDir + "/" + packet).length();
                        deltaFiles += packet + " ";
                    }

                    if( (length + deltaSize) > FILE_SIZE_LIMIT || (numChars + deltaFiles.length()) > ARG_MAX) {
                        multipleFiles = true;
                        break;
                    }
                    else {
                        files += deltaFiles;
                        length += deltaSize;
		        numChars += deltaFiles.length();
                        count++;
                    }

                }


                long bufferedSpaceRequiredInKB = (length / 1024) + 1024;        // allowing a buffer of 1 MB (size in kb)

// String destBin = Mutex.getFreeBin(bufferedSpaceRequiredInKB, CHECK_AFS_SPACE_SCRIPT, this);
               // String destBin = Mutex.getFreeBin(length + (1024*1024), this);
                String bin = "bin1";

                if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
                   CHIPS_CUSTOM_BINS_DIR += "/";
                String destBin = CHIPS_CUSTOM_BINS_DIR + bin;


                try {

                    String filename;

                    if(multipleFiles)
                        filename = technology + "_" + versionNo + "_DeltaReleases_" + numFiles + ".tar";
                    else
                        filename = technology + "_" + versionNo + "_DeltaReleases.tar";

                    fileVector.addElement(destBin + "/" + orderNumber + "/" + filename);

                    String cmd
                        = getProperty("CREATE_DR_SCRIPT")
                        + " "
                        + destBin
                        + " "
                        + orderNumber
                        + " "
                        + filename                    // dest file
                        + " "
                        + tarFile                     // pre-built tar file
                        + " "
                        + technology
                        + " "
                        + versionNo
                        + " "
                        + packetsDir                 // location of packets
                        + " "
                        + files;


                    print("Executing: " + cmd + " at " + new Date(), V_IMP);
                    print("Allocated " + String.valueOf(bufferedSpaceRequiredInKB) + " KB in " + destBin, V_IMP);
                    flushDisplay();
                    int exitValue = execute(cmd);
                    print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);


                    if(exitValue == 0)
                        print("delta release created successfully", V_IMP);
                    else
                        handleException(cmd + " returned an exitValue of " + exitValue);

                }
                catch(Throwable t) {
                    handleException(t, "thrown creating delta release");
                }
/* finally {
                    Mutex.unlockBin(destBin);
                    print("Unlocked bin: " + destBin, V_IMP);
                    }*/
            }

            sourceFiles = new String[numFiles];

            fileVector.toArray(sourceFiles);

        }

        String[] fileDesc = new String[sourceFiles.length];
        String desc = "Delta Release for ASIC Design Kit for " + technology + " " + versionNo;

        for(int i = 0; i < fileDesc.length; i++)
            fileDesc[i] = desc;

        completeDL(sourceFiles, fileDesc);

        mail(ALOC_DR_ORD);
        // Send email to additional shipping locations that customer has received delta release

        print("Exiting handleDR()...", IMP);

        }



    /**********************************************************************************************************************************************************************************************************************************/



    private void handlePDK() {   // Full Physical Design Kit

       Vector toolkit_list = new Vector();

        print("Starting handlePDK()...", V_IMP);

       //new for 3.7.1 -- deal with additional ship-to
        int numDLs = Integer.parseInt(getRequiredValue("SHIPTO_LOC_COUNT")) +1;

        for (int shipToID=1; shipToID <=numDLs; shipToID++){  //for loop

           addShipTo = shipToID-1;
           isPrimaryCustomer = false;
           if (shipToID==1)
              isPrimaryCustomer = true;
           int numPlatforms = 0;

           if(isPrimaryCustomer) { //if(isPrimary)

               try {
                  numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
                  if(numPlatforms > 0)

                     this.platformsList = getStringArray("PLATFORMS_LIST");

                  else
                     handleException("PLATFORMS_LIST is empty");
               }
               catch(NumberFormatException e) {
                  handleException(e, "PLATFORMS_COUNT field is not numeric");
               }
               for (int k=0; k<platformsList.length; k++)
                  print(platformsList[k], V_IMP);
               print("platforms_count "+numPlatforms+" platforms_list "+platformsList.length, V_IMP);
               if(platformsList.length != numPlatforms)
                  handleException("The value of PLATFORMS_COUNT does not match the number of platforms in PLATFORMS_LIST");


               String[] sourceFiles = new String[numPlatforms];
               String[] fileDesc = new String[numPlatforms];

               for(int i = 0; i < numPlatforms; i++) {

                  sourceFiles[i]
                     = getLinkTarget(
                        CHIPS_DL_DIR
                        + "FullPDKit/"
                        + this.platformsList[i] + "/"
                        + "fullpdkit");

                  fileDesc[i] = "Toolkit Fix for " + platformsList[i];
                  String tk = platformsList[i].substring(platformsList[i].indexOf("tk"));
                  print("tk is "+tk, V_IMP);
                  if (!stringAlreadyExists(tk, toolkit_list))
                      toolkit_list.addElement(tk);

               }


               completeDL(sourceFiles, fileDesc);
               
				if(isDropbox)
				{
					uploadFileNames = sourceFiles;
				}
           }

           else {//else
              if(getRequiredValue("DL_"+addShipTo).equals("1")){//for download only
                 try{
                    numPlatforms = Integer.parseInt(getRequiredValue("PLATFORM_LIST_COUNT_"+addShipTo));
                    if(numPlatforms <=0){
                       handleMinorException("PLATFORM_LIST_COUNT_"+addShipTo +"  has invalid value "+numPlatforms +". Not sending DL for additional ship location");
                       continue;
                    }
                    this.shortplatformsList = getStringArray("PLATFORM_LIST_"+addShipTo);
                   // this.platformsList = getStringArray("PLATFORM_LIST_"+addShipTo);
                 }
                 catch(NumberFormatException e) {
                    handleException(e, "PLATFORM_LIST_COUNT field is not numeric");
                 }
                 print("platform_list_count "+numPlatforms+" platform_list "+shortplatformsList.length, V_IMP);
                 if(shortplatformsList.length != numPlatforms)
                    handleException("The value of PLATFORM__LIST_COUNT does not match the number of platforms in PLATFORM_LIST");


                // String[] sourceFiles = new String[numPlatforms];
                //  String[] fileDesc = new String[numPlatforms];
                 Vector sourceFiles = new Vector();
                 Vector fileDesc = new Vector();
                 this.platformsList = new String[(numPlatforms*toolkit_list.size())];
                 print("platformsList.length "+platformsList.length, V_IMP);
                 int k = 0;
                 for(int i = 0; i < numPlatforms; i++) {
                    for (int j = 0; j < toolkit_list.size(); j++){
                       this.platformsList[k]= (String) (gettkName(shortplatformsList[i])+"_fix_"+toolkit_list.elementAt(j));
                       print("platformsList "+ k +" "+platformsList[k], V_IMP);
                       sourceFiles.addElement(
                          getLinkTarget(
                             CHIPS_DL_DIR
                             + "FullPDKit/"
                             + this.platformsList[k] + "/"
                             + "fullpdkit"));

                       fileDesc.addElement("Toolkit Fix for " + platformsList[k]);
                       k++;

                    }
                 }
                 String[] sFiles = new String[sourceFiles.size()];
                 String[] fDesc = new String[sourceFiles.size()];


                 for (int i =0; i<sourceFiles.size();i++){
                    print((String)sourceFiles.elementAt(i),V_IMP);
                    sFiles[i] = (String)sourceFiles.elementAt(i);
                    fDesc[i] = (String)fileDesc.elementAt(i);

                 }

                 completeDL(sFiles, fDesc);
				 
				if(isDropbox)
				{
					uploadFileNames = sFiles;
				}
              }

           }

        }
    }
       //end of new for 3.7.1






    /**********************************************************************************************************************************************************************************************************************************/



    private void handleDSE() {

        print("Starting handleDSE()...", V_IMP);

        int numPlatforms = 0;
        try {
            numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
            if(numPlatforms > 0)
                this.platformsList = getStringArray("PLATFORMS_LIST");
            else
                handleException("PLATFORMS_LIST is empty");
        }
        catch(NumberFormatException e) {
            handleException(e, "PLATFORMS_COUNT field is not numeric");
        }

        if(platformsList.length != numPlatforms)
            handleException("The value of PLATFORMS_COUNT does not match the number of platforms in PLATFORMS_LIST");

        String[] sourceFiles = new String[numPlatforms];
        String[] fileDesc = new String[numPlatforms];

        for(int i = 0; i < numPlatforms; i++) {
            sourceFiles[i]
                = CHIPS_DL_DIR
                + "DieSizer/"
                + this.platformsList[i] + "/"
                + getDieSizerFileName(this.platformsList[i]);

            fileDesc[i] = " DieSizer Tool for " + platformsList[i];

       }

		
        completeDL(sourceFiles, fileDesc);
		
		if(isDropbox)
		{
			uploadFileNames = sourceFiles;
		}

        print("Exiting handleDSE()...", IMP);

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void handleXMX() {

        print("Starting handleXMX()...", V_IMP);

        int numPlatforms = 0;
        try {
            numPlatforms = Integer.parseInt(getRequiredValue("PLATFORMS_COUNT"));
            if(numPlatforms > 0)
                this.platformsList = getStringArray("PLATFORMS_LIST");
            else
                handleException("PLATFORMS_LIST is empty");
        }
        catch(NumberFormatException e) {
            handleException(e, "PLATFORMS_COUNT field is not numeric");
        }

        if(platformsList.length != numPlatforms)
            handleException("The value of PLATFORMS_COUNT does not match the number of platforms in PLATFORMS_LIST");

        String[] sourceFiles = new String[numPlatforms];
        String[] fileDesc = new String[numPlatforms];

        for(int i = 0; i < numPlatforms; i++) {
            sourceFiles[i]
                = getLinkTarget(
                      CHIPS_DL_DIR
                      + "XMX/"
                      + this.platformsList[i] + "/"
                      + "xmx");

            fileDesc[i] = "X Collaborative Tool for " + platformsList[i];

        }

        completeDL(sourceFiles, fileDesc);

        print("Exiting handleXMX()...", IMP);

    }


    /**********************************************************************************************************************************************************************************************************************************/



    private String getDieSizerFileName(String platform) {

        if(platform.startsWith("Windows"))
            return "install.exe";
        else
            return "install.bin";

    }



    /**********************************************************************************************************************************************************************************************************************************/




    private void handleMem() {

        print("Starting handleMem()...", V_IMP);

        getRequiredValue("USERS_COMPANY");
        getRequiredValue("FIRST_NAME");
        getRequiredValue("LAST_NAME");
        getRequiredValue("FL");

	String[] entityList = new String[0];
	String entities = getValue("ENTITY_LIST");
	if(entities.length() > 0)
	    entityList = tokenizeString(entities, ",");

        String[] modelList = getStringArray("MODEL_LIST");

        String complibname = getValue("COMPLIBNAME");
        if(complibname.length() > 0)
            complibname = "complibname " + complibname + "\n";

        String list = "";

        for(int i = 0; i < entityList.length; i++)
            list += entityList[i] + "\n";

        for(int i = 0; i < modelList.length; i++)
            list += modelList[i] + "\n";


        File memStartedDir = new File(MEM_STARTED_DIR);

        String[] dirList = memStartedDir.list();
        String memTimestamp = null;

        if( dirList != null ) {
            for(int i = 0; i < dirList.length; i++) {
                if(dirList[i].startsWith(this.orderNumber + ".")) {
                    memTimestamp = dirList[i].substring(dirList[i].lastIndexOf('.') + 1);
                    break;
                }
            }
        }

        String destDir = getProperty("BTV_CUSTOM_BIN_DIR");
        String gsaDestDir = getProperty("GSA_CUSTOM_BIN_DIR");

        if( ! destDir.endsWith("/") )
            destDir += "/";

        if( ! gsaDestDir.endsWith("/") )
            gsaDestDir += "/";

	String filename = null, dest = null, orderFileName = null, memStartedFile = null, gsaDest = null;

        if(memTimestamp != null) {
	    filename = edgeUserid + "." + technology + "." + memTimestamp;
	    dest = destDir + filename;
            gsaDest = gsaDestDir + filename;

            orderFileName = MEM_ORDERS_DIR + filename;
	    memStartedFile = MEM_STARTED_DIR + this.orderNumber + "." + memTimestamp;
            print("order previously written to " + orderFileName, V_IMP);
        }
	else {
	    memTimestamp = generateMemTimestamp();

            filename = edgeUserid + "." + technology + "." + memTimestamp;

            dest = destDir + filename;
            gsaDest = gsaDestDir + filename;

            String output =
                usersEmail                 + "\n" +
                "customer "   + edgeUserid + "\n" +
                "technology " + technology + "\n" +
                "dkversion "  + versionNo  + "\n" +
                "dest "       + dest       + "\n" +
                complibname +
                list;


            orderFileName = MEM_ORDERS_DIR + filename;
            String tmp_orderFile = MEM_ORDERS_DIR + "temp/"+filename;

            print("writing order to " + tmp_orderFile, V_IMP);

            try {
                FileOutputStream fos = new FileOutputStream(orderFileName);
                fos.write(output.getBytes());
                fos.close();
                File f = new File (tmp_orderFile);
                f.renameTo(new File (orderFileName));
            }

            catch(Throwable t) {
                handleException(t, "thrown writing order to " + orderFileName);
            }

	    try {
	        memStartedFile = MEM_STARTED_DIR + this.orderNumber + "." + memTimestamp;
		new File(memStartedFile).createNewFile();
	    }
            catch(Throwable t) {
                handleException(t, "thrown creating empty file: " + memStartedFile);
            }
	}


        File destFile = new File(gsaDest + ".tar.gz");
        File queueFile = new File(gsaDest + ".queue");
        File errFile = new File(gsaDest + ".err");


        long stopWait = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);   // 2 days
        long sendMailTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);   // 1 day
        
        

        boolean queued = false;
	boolean sentMail = false;

        int minutesWaited = 60;
		  

        while(System.currentTimeMillis() < stopWait) {//while

            if(destFile.isFile() || errFile.isFile())
                break;

            if(queueFile.isFile()) {
		if( ! queued ) {
                    queued = true;
                    print("ORDER WAS QUEUED AT " + new Date(), V_IMP);
                    flushDisplay();
                    stopWait += 3 * 24 * 60 * 60 * 1000;  // give it another 3 days
		}
                queueFile.delete();
            }


            if(minutesWaited >= 60) {
           
				if( ! sentMail && System.currentTimeMillis() > sendMailTime ) {
		                    
					//If the technology is Foundry, the email should say Foundry Connect and not ASIC Connect
					//CSR IBMCC00011217
					print("About to determine whether ASIC or Foundry before sending delay mail for technology " + technology, V_IMP);
					if( isFoundryTech(technology) )
					{
						print("Technology is Foundry. So send delay mail to Foundry Connect Customer ", V_IMP);
						mqSendQueued(3);
					}
					else
					{
						print("Technology is ASIC. So send delay mail to ASIC Connect Customer ", V_IMP);
						mqSendQueued(0);
					}
		            print("Delay email was sent at " + new Date(), V_IMP);
		            flushDisplay();
				    sentMail = true;
				}
		                print("waiting for file: " + gsaDest + ".tar.gz to appear at " + new Date(), V_IMP);
		                flushDisplay();
		                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + gsaDest + ".tar.gz to appear");
            }

        }


        if(new File(orderFileName).isFile()) {
            print("WARNING! : deleting order file: " + orderFileName, V_IMP);
            new File(orderFileName).delete();
        }


        if(queueFile.isFile())
	    queueFile.delete();


        if(destFile.isFile()) {

            long length = destFile.length() + 10000;  // buffer of 10 KB

           //  String bin = Mutex.getFreeBin(length, this);
            String bin = "bin1";

            if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
               CHIPS_CUSTOM_BINS_DIR += "/";

	    String dir = CHIPS_CUSTOM_BINS_DIR + bin + "/" + orderNumber + "/";

            if( ! new File(dir).isDirectory() && ! new File(dir).mkdir() )
                handleException("Could not create directory: " + dir);

            String[] sourceFiles = {dir + filename + ".tgz"};
            String[] fileDesc = {"Compilable Order for " + technology + " " + versionNo};

            String cmd =
                "/usr/bin/mv -f"
                + " "
                + gsaDest + ".tar.gz"
                + " "
                + sourceFiles[0];

            int exitValue = -1;

	    new File(memStartedFile).delete();

            try {

                print("Executing: " + cmd + " at " + new Date(), V_IMP);
                flushDisplay();

                exitValue = execute(cmd);

                print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);

            }
            catch(Throwable t) {
                handleException(t, "thrown executing " + cmd);
            }
/* finally {
                Mutex.unlockBin(bin);
                print("Unlocked bin: " + bin, V_IMP);
                }*/



            if(exitValue == 0)
            {   
            	completeDL(sourceFiles, fileDesc);
				if(isDropbox)
				{
					uploadFileNames = sourceFiles;
				} 
            } 
            else
                handleException(cmd + " returned an exit value of " + exitValue);

        }
        else if(errFile.isFile()) {

            handleException("Order had an error. Could not be completed");

        }
        else {

            handleException(dest + " did not appear within wait period. Ending wait and terminating order");

        }


        print("Exiting handleMem()...", IMP);
        flushDisplay();
    }


     /**********************************************************************************************************************************************************************************************************************************/

  //new for 4.5.1
   private void handleEfpga() {
        String mapping_fileName="", stamp_dataName="", stamp_modName="";

        print("Starting handleEfpga()...", V_IMP);
        byte[] mapping_file=null,stamp_data=null,stamp_mod=null;

        String userid = getRequiredValue("EDGE_USERID");
        String email = getRequiredValue("E_MAIL");
       // String order_no = getRequiredValue("ORDER_REFERENCE");
       //   String order_ts = getRequiredValue("ORDER_TIMESTAMP");
        String tech = getRequiredValue("TECHNOLOGY");
        String ver = getRequiredValue("VERSION_NO");
        String design_name = getRequiredValue("DESIGN_NAME");
        String fpga_type = getRequiredValue("FPGA_TYPE");
        String map_file_type = getRequiredValue("MAPPING_FILE_TYPE");
        String bus_dir = getRequiredValue("BUS_DIRECTION");
        String models = getRequiredValue("MODEL_LIST");
        try{
           mapping_file = Base64.decode(getRequiredValue("MAPPING_FILE"));
           stamp_data = Base64.decode(getRequiredValue("STAMP_DATA"));
           stamp_mod = Base64.decode(getRequiredValue("STAMP_MOD"));
        }catch(Throwable t) {
           handleException(t, "thrown decode exception");
        }




        String destDir = getProperty("BTV_CUSTOM_BIN_DIR");
        if( ! destDir.endsWith("/") )
            destDir += "/";

        String gsaDestDir = getProperty("GSA_CUSTOM_BIN_DIR");
        if( ! gsaDestDir.endsWith("/") )
            gsaDestDir += "/";

        String btvEfpgaIncomingDir = getProperty("BTV_EFPGA_INCOMING_DIR");
        if ( ! btvEfpgaIncomingDir.endsWith("/"))
           btvEfpgaIncomingDir += "/";

        File efpgaStartedDir = new File(EFPGA_STARTED_DIR);

        String[] dirList = efpgaStartedDir.list();
        String efpgaTimestamp = null;

        if( dirList != null ) {
            for(int i = 0; i < dirList.length; i++) {
                if(dirList[i].startsWith(this.orderNumber + ".")) {
                    efpgaTimestamp = dirList[i].substring(dirList[i].lastIndexOf('.') + 1);
                    break;
                }
            }
        }

        String filename = null, dest = null, orderFileName = null, efpgaStartedFile = null, gsaDest = null;

        if(efpgaTimestamp != null) {
	    filename = userid + "." + tech + "." + efpgaTimestamp;
	    dest = destDir + filename;
            gsaDest = gsaDestDir + filename;

            orderFileName = EFPGA_ORDERS_DIR + filename;
	    efpgaStartedFile = EFPGA_STARTED_DIR + this.orderNumber + "." + efpgaTimestamp;
            print("order previously written to " + orderFileName, V_IMP);
        }
	else {
	    efpgaTimestamp = generateEfpgaTimestamp();

            filename = userid + "." + tech + "." + efpgaTimestamp;

            dest = destDir + filename;
            gsaDest = gsaDestDir + filename;

            mapping_fileName = EFPGA_INCOMING_DIR + filename + ".map";
            stamp_dataName = EFPGA_INCOMING_DIR + filename + ".data";
            stamp_modName = EFPGA_INCOMING_DIR + filename + ".mod";

            String btv_mapping_fileName = btvEfpgaIncomingDir + filename + ".map";
            String btv_stamp_dataName = btvEfpgaIncomingDir + filename + ".data";
            String btv_stamp_modName = btvEfpgaIncomingDir + filename + ".mod";

            String tmp_mapping = EFPGA_INCOMING_DIR + filename + ".map.tmp";
            String tmp_data = EFPGA_INCOMING_DIR + filename + ".data.tmp";
            String tmp_mod = EFPGA_INCOMING_DIR + filename + ".mod.tmp";

            try {
                FileOutputStream fos = new FileOutputStream(tmp_mapping);
                fos.write(mapping_file);
                fos.close();
                File f = new File(tmp_mapping);
                f.renameTo(new File(mapping_fileName));
            }
            catch(Throwable t) {
                handleException(t, "thrown writing mapping_file to " + mapping_fileName);
            }

             try {
                FileOutputStream fos = new FileOutputStream(tmp_data);
                fos.write(stamp_data);
                fos.close();
                File f = new File(tmp_data);
                f.renameTo(new File(stamp_dataName));
            }
            catch(Throwable t) {
                handleException(t, "thrown writing stamp_data to " + stamp_dataName);
            }

             try {
                FileOutputStream fos = new FileOutputStream(tmp_mod);
                fos.write(stamp_mod);
                fos.close();
                File f = new File(tmp_mod);
                f.renameTo(new File(stamp_modName));
            }
            catch(Throwable t) {
                handleException(t, "thrown writing stamp_mod to " + stamp_modName);
            }

            String output =
                email                 + "\n" +
                "customer "   + userid + "\n" +
                "order_reference " + this.orderNumber +"\n" +
                "technology " + tech + "\n" +
                "dkversion "  + ver  + "\n" +
                "dest "       + dest       + "\n" +
                "design_name "+ design_name + "\n" +
                "fpga_type " + fpga_type + "\n" +
                "mapping_file_type "+ map_file_type + "\n" +
                "mapping_file "+ btv_mapping_fileName +"\n" +
                "stamp_data "+ btv_stamp_dataName + "\n" +
                "stamp_mod " + btv_stamp_modName + "\n" +
                "bus_direction " + bus_dir +"\n" +
                "models " + models + "\n";

            orderFileName = EFPGA_ORDERS_DIR + filename;
            String tmp_orderFileName = EFPGA_ORDERS_DIR + "temp/" + filename;

            print("writing order to " + tmp_orderFileName, V_IMP);

            try {
                FileOutputStream fos = new FileOutputStream(tmp_orderFileName);
                fos.write(output.getBytes());
                fos.close();
                File f = new File(tmp_orderFileName);
                f.renameTo(new File(orderFileName));
            }
            catch(Throwable t) {
                handleException(t, "thrown writing order to " + orderFileName);
            }

	    try {
	        efpgaStartedFile = EFPGA_STARTED_DIR + this.orderNumber + "." + efpgaTimestamp;
		new File(efpgaStartedFile).createNewFile();
	    }
            catch(Throwable t) {
                handleException(t, "thrown creating empty file: " + efpgaStartedFile);
            }
	}


        File destFile = new File(gsaDest + ".tar.gz");
        File queueFile = new File(gsaDest + ".queue");
        File errFile = new File(gsaDest + ".err");


        long stopWait = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);   // 2 days
        long sendMailTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);   // 1 day

        boolean queued = false;
	boolean sentMail = false;

        int minutesWaited = 60;

        while(System.currentTimeMillis() < stopWait) {

            if(destFile.isFile() || errFile.isFile())
                break;

            if(queueFile.isFile()) {
		if( ! queued ) {
                    queued = true;
                    print("ORDER WAS QUEUED AT " + new Date(), V_IMP);
                    flushDisplay();
                    stopWait += 3 * 24 * 60 * 60 * 1000;  // give it another 3 days
		}
                queueFile.delete();
            }


            if(minutesWaited >= 60) {
		if( ! sentMail && System.currentTimeMillis() > sendMailTime ) {
                    mqSendQueued(0);
                    print("Delay email was sent at " + new Date(), V_IMP);
		    sentMail = true;
		}
                print("waiting for file: " + dest + ".tar.gz to appear at " + new Date(), V_IMP);
                flushDisplay();
                minutesWaited = 0;
            }

            try {
                sleep(SHIPIT_WAIT_TIME * 60 * 1000);
		minutesWaited += SHIPIT_WAIT_TIME;
            }
            catch(InterruptedException e) {
                handleException(e, "Interrupted while waiting for " + dest + ".tar.gz to appear");
            }

        }


        if(new File(orderFileName).isFile()) {
            print("WARNING! : deleting order file: " + orderFileName, V_IMP);
            new File(orderFileName).delete();
        }


        if(queueFile.isFile())
	    queueFile.delete();


        if(destFile.isFile()) {

            long length = destFile.length() + 10000;  // buffer of 10 KB

           //String bin = Mutex.getFreeBin(length, this);
            String bin = "bin1";
           //   String dir = bin + "/" + orderNumber + "/";
            if( ! CHIPS_CUSTOM_BINS_DIR.endsWith("/") )
               CHIPS_CUSTOM_BINS_DIR += "/";

            String dir = CHIPS_CUSTOM_BINS_DIR + bin + "/" + orderNumber + "/";

            if( ! new File(dir).isDirectory() && ! new File(dir).mkdir() )
                handleException("Could not create directory: " + dir);

            String[] sourceFiles = {dir + filename + ".tgz"};
            String[] fileDesc = {"EFPGA Order for " + technology + " " + versionNo};

            String cmd =
                "/usr/bin/mv -f"
                + " "
                + gsaDest + ".tar.gz"
                + " "
                + sourceFiles[0];

            int exitValue = -1;

	    new File(efpgaStartedFile).delete();

            try {

                print("Executing: " + cmd + " at " + new Date(), V_IMP);
                flushDisplay();

                exitValue = execute(cmd);

                print("Executed command at " + new Date() + " Exit Value: " + exitValue, V_IMP);

            }
            catch(Throwable t) {
                handleException(t, "thrown executing " + cmd);
            }
/* finally {
                Mutex.unlockBin(bin);
                print("Unlocked bin: " + bin, V_IMP);
                }*/



            if(exitValue == 0)
            {
            	    completeDL(sourceFiles, fileDesc);
					if(isDropbox)
					{
						uploadFileNames = sourceFiles;
					} 
            } 
            else
                handleException(cmd + " returned an exit value of " + exitValue);

        }
        else if(errFile.isFile()) {

            handleException("Order had an error. Could not be completed");

        }
        else {

            handleException(dest + " did not appear within wait period. Ending wait and terminating order");

        }

        new File(mapping_fileName).delete();
        new File(stamp_dataName).delete();
        new File(stamp_modName).delete();
        print("Exiting handleEfpga()...", IMP);

    }


    private static synchronized String generateEfpgaTimestamp() {

        long ts = System.currentTimeMillis() / 1000;

        while(ts <= LAST_EFPGA_ORDER_NUMBER)
            ts++;

        LAST_EFPGA_ORDER_NUMBER = ts;

        return String.valueOf(ts);

    }
  //end of new for 4.5.1

    /**********************************************************************************************************************************************************************************************************************************/



    private static synchronized String generateMemTimestamp() {

        long ts = System.currentTimeMillis() / 1000;

        while(ts <= LAST_MEM_ORDER_NUMBER)
            ts++;

        LAST_MEM_ORDER_NUMBER = ts;

        return String.valueOf(ts);

    }




    /**********************************************************************************************************************************************************************************************************************************/






    private void handleAreaMem() {

        print("Starting handleAreaMem()...", V_IMP);

        print("Input: (" + messageStr + ")", V_IMP);

        String output = "";

        try {
            output = executeAndGetStream(messageStr);
        }
        catch(Throwable t) {
            handleException(t, "thrown reading output from: " + messageStr);
        }

        try {
            print("Sending MQ with CorrId: EDQ4_AREA_MEM_OK and messageId: " + this.orderNumber, V_IMP);
            print("Message length in bytes: " + output.length(), V_IMP);
	    flushDisplay();
            OrderProcessor.sendMQMessage("EDQ4_AREA_MEM_OK", this.orderNumber, output, transientMsgPersistent);   // orderNumber is actually messageId
            print("Sent MQ", V_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown sending MQ");
        }

        print("Message sent by MQ:\n" + output, NOT_IMP);

        print("Exiting handleAreaMem()...", IMP);

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void handleReportMem() {

        print("Starting handleReportMem()...", V_IMP);

        print("Input: (" + messageStr + ")", V_IMP);

        String output = "";

        try {
            output = executeAndGetStream(messageStr);
        }
        catch(Throwable t) {
            handleException(t, "thrown reading output from: " + messageStr);
        }

        try {
            print("Sending MQ with CorrId: EDQ4_REPORT_MEM_OK and messageId: " + this.orderNumber, V_IMP);
            print("Message length in bytes: " + output.length(), V_IMP);
	    flushDisplay();
            OrderProcessor.sendMQMessage("EDQ4_REPORT_MEM_OK", this.orderNumber, output, transientMsgPersistent);   // orderNumber is actually messageId
            print("Sent MQ", V_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown sending MQ");
        }

        print("Message sent by MQ:\n" + output, NOT_IMP);

        print("Exiting handleReportMem()...", IMP);

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void completeDL(String[] fileNames, String[] fileDesc) {
        print("Starting completeDL(a1, a2)...", V_IMP);

        int numFiles = fileNames.length;

        String[] fileEntries = new String[numFiles];

        String[] abbFileNames = new String[numFiles];
        String[] fileIDs = new String[numFiles];
        String[] fileSizes = new String[numFiles];
        String[][] downloadTimes = new String[numFiles][2];
        String mimeType = null;

        // removed for 2.10
        // long[] pswds = new long[numFiles];

        try {

            float oneMB = 1024 * 1024;

            for(int i = 0; i < numFiles; i++) {
               print("inside completeDL "+fileNames[i], V_IMP);
                File f = new File(fileNames[i]);

                if(f.isFile()) {

                    File canonicalFile = new File(f.getCanonicalPath());

                    abbFileNames[i] = canonicalFile.getName();

                    long length = canonicalFile.length();

                    fileSizes[i] = String.valueOf(length) + " bytes or " + numberFormat.format(length / oneMB) + " MB";

                    downloadTimes[i][0] = "56 Kbps       : " + formatTime(length * 8 / 56000);
                    downloadTimes[i][1] = "T1 (1.5 Mbps) : " + formatTime(length * 8 / 1500000);

                    if(fileNames[i].indexOf("BaseModelKit") >= 0)
                        print("KitSize: BaseModelKit: " + length, V_IMP);
                    else if(fileNames[i].indexOf("CustomModelKit") >= 0)
                        print("KitSize: CustomModelKit: " + length, V_IMP);
                    else if(fileNames[i].indexOf("/ToolKit/") >= 0 && this.toolKit) {
                        for(int j = 0; j < this.platformsList.length; j++) {
                            if(fileNames[i].indexOf("/" + this.platformsList[j] + "/") >= 0) {
                                print("KitSize: ToolKit: " + this.platformsList[j] + ": " + length, V_IMP);
                                break;
                            }
                        }
                    }
                    else if(fileNames[i].indexOf("/FullPDKit/") >= 0 && this.platformsList != null) {

                        for(int j = 0; j < this.platformsList.length; j++) {

                            if(fileNames[i].indexOf("/" + this.platformsList[j] + "/") >= 0) {
                                print("KitSize: FullPDKit: " + this.platformsList[j] + ": " + length, V_IMP);
                                break;
                            }
                        }
                    }
                    else
                        print("KitSize: Other: " + length, V_IMP);

                }
                else
                    handleException("File: " + fileNames[i] + " does not exist");
            }
        }
        catch(IOException e) {
            handleException(e, "thrown getting info about files to be downloaded");
        }


        this.expirationTime = System.currentTimeMillis() + EXP_DURATION;

        print("Expiration Time: " + this.expirationTime, V_IMP);

        long firstFileID = EDSDContentDeliveryTable.generateFileIds(numFiles);


        for(int i = 0; i < numFiles; i++) {

            // removed for 2.10
            // pswds[i] = generateOneTimePswd();

            mimeType = getMimeType(abbFileNames[i]);

            long fileId = firstFileID + i;

            fileIDs[i] = String.valueOf(fileId);

            fileEntries[i] = EDSDContentDeliveryTable.getFileEntry(fileId, abbFileNames[i], fileDesc[i], fileNames[i], mimeType, i+1);

            print("Added: " + fileIDs[i] + " filepath: " + fileNames[i] + " mime: " + mimeType + " (" + (i+1) + "/" + numFiles + ")", V_IMP);

        }


	String fileOwner;
	String dlOrderID;
	String dlEmail;

	if(isPrimaryCustomer) {
	    fileOwner = this.edgeUserid;
	    dlOrderID = this.orderNumber;
	    dlEmail = this.usersEmail;
	}
	else {
	    fileOwner = getRequiredValue("IR_ID_" + addShipTo);
	    dlOrderID = this.orderNumber + "." + addShipTo;
	    dlEmail = getRequiredValue("EMAIL_" + addShipTo);
	}


        try {
            EDSDContentDeliveryTable.addOrderEntry(dlOrderID, fileOwner, dlEmail, this.expirationTime, numFiles, fileEntries);
        }
        catch(IOException e) {
            handleException(e, "Exception thrown by EDSDContentDeliveryTable.addOrderEntry");
        }

        print("Sent download info to BTV server at " + new Date(), V_IMP);

        mqSendReady(abbFileNames, fileIDs, fileSizes, downloadTimes, null);

        print("Exiting completeDL()...", NOT_IMP);

    }


  //new for 4.2.1
  /**********************************************************************************************************************************************************************************************************************************/

   private void completeDL(String[] fileNames, String[] fileDesc, String DRpackets) {
        print("Starting completeDL(a1, a2, a3)...", V_IMP);

        int numFiles = fileNames.length;

        String[] fileEntries = new String[numFiles];

        String[] abbFileNames = new String[numFiles];
        String[] fileIDs = new String[numFiles];
        String[] fileSizes = new String[numFiles];
        String[][] downloadTimes = new String[numFiles][2];
        String mimeType = null;

        try {

            float oneMB = 1024 * 1024;

            for(int i = 0; i < numFiles; i++) {
               print("inside completeDL "+fileNames[i], V_IMP);
                File f = new File(fileNames[i]);

                if(f.isFile()) {

                    File canonicalFile = new File(f.getCanonicalPath());

                    abbFileNames[i] = canonicalFile.getName();

                    long length = canonicalFile.length();

                    fileSizes[i] = String.valueOf(length) + " bytes or " + numberFormat.format(length / oneMB) + " MB";

                    downloadTimes[i][0] = "56 Kbps       : " + formatTime(length * 8 / 56000);
                    downloadTimes[i][1] = "T1 (1.5 Mbps) : " + formatTime(length * 8 / 1500000);

                    if(fileNames[i].indexOf("BaseModelKit") >= 0)
                        print("KitSize: BaseModelKit: " + length, V_IMP);
                    else if(fileNames[i].indexOf("CustomModelKit") >= 0)
                        print("KitSize: CustomModelKit: " + length, V_IMP);
                    else if(fileNames[i].indexOf("/ToolKit/") >= 0 && this.toolKit) {
                        for(int j = 0; j < this.platformsList.length; j++) {
                            if(fileNames[i].indexOf("/" + this.platformsList[j] + "/") >= 0) {
                                print("KitSize: ToolKit: " + this.platformsList[j] + ": " + length, V_IMP);
                                break;
                            }
                        }
                    }
                    else if(fileNames[i].indexOf("/FullPDKit/") >= 0 && this.platformsList != null) {
                        for(int j = 0; j < this.platformsList.length; j++) {
                            if(fileNames[i].indexOf("/" + this.platformsList[j] + "/") >= 0) {
                                print("KitSize: FullPDKit: " + this.platformsList[j] + ": " + length, V_IMP);
                                break;
                            }
                        }
                    }
                    else
                        print("KitSize: Other: " + length, V_IMP);

                }
                else
                    handleException("File: " + fileNames[i] + " does not exist");
            }
        }
        catch(IOException e) {
            handleException(e, "thrown getting info about files to be downloaded");
        }


        this.expirationTime = System.currentTimeMillis() + EXP_DURATION;

        print("Expiration Time: " + this.expirationTime, V_IMP);

        long firstFileID = EDSDContentDeliveryTable.generateFileIds(numFiles);


        for(int i = 0; i < numFiles; i++) {

            mimeType = getMimeType(abbFileNames[i]);

            long fileId = firstFileID + i;

            fileIDs[i] = String.valueOf(fileId);

            fileEntries[i] = EDSDContentDeliveryTable.getFileEntry(fileId, abbFileNames[i], fileDesc[i], fileNames[i], mimeType, i+1);

            print("Added: " + fileIDs[i] + " filepath: " + fileNames[i] + " mime: " + mimeType + " (" + (i+1) + "/" + numFiles + ")", V_IMP);

        }


	String fileOwner;
	String dlOrderID;
	String dlEmail;

	if(isPrimaryCustomer) {
	    fileOwner = this.edgeUserid;
	    dlOrderID = this.orderNumber;
	    dlEmail = this.usersEmail;
	}
	else {
	    fileOwner = getRequiredValue("IR_ID_" + addShipTo);
	    dlOrderID = this.orderNumber + "." + addShipTo;
	    dlEmail = getRequiredValue("EMAIL_" + addShipTo);
	}


        try {
            EDSDContentDeliveryTable.addOrderEntry(dlOrderID, fileOwner, dlEmail, this.expirationTime, numFiles, fileEntries);
        }
        catch(IOException e) {
            handleException(e, "Exception thrown by EDSDContentDeliveryTable.addOrderEntry");
        }

        print("Sent download info to BTV server at " + new Date(), V_IMP);

        mqSendReady(abbFileNames, fileIDs, fileSizes, downloadTimes,DRpackets);

        print("Exiting completeDL()...", NOT_IMP);

    }
  //end of new for 4.2.1


    /**********************************************************************************************************************************************************************************************************************************/



    private String getMimeType(String filename) {

        String[][] mimeList = {
            {".tar",  "application/x-tar"},
            {".tgz",  "application/octet-stream"},
            {".gz",   "application/x-gzip"},
            {".Z",    "application/x-compress"},
            {".zip",  "application/zip"},
            {".html", "text/html"}
        };


        for(int i = 0; i < mimeList.length; i++)
            if(filename.endsWith(mimeList[i][0]))
                return mimeList[i][1];

        // return "application/octet-stream";
        return "application/x-binary";

    }




    /**********************************************************************************************************************************************************************************************************************************/


    private long generateOneTimePswd() {

	return randomGen.nextLong();

    }


    /**********************************************************************************************************************************************************************************************************************************/

    private void sendToInternalPage() {

        String link = "http://w3.eda.ibm.com/cgi-bin/licreprt-cgi-bin/getkey.cgi";

        String subject = "Your ASIC ToolKit license from IBM Customer Connect";

        String body =
            "Dear ASIC Connect customer,\n\n" +
            "Internal IBM'ers can get ASIC ToolKit licenses using the following link:\n" +
            link +
            "\n\nThank You,\nASIC Connect";

        // send mail to go to above link

    }

    /**********************************************************************************************************************************************************************************************************************************/


    private void generateToolkitLicense() {

        String licenseType = "";
	String project = "";
	int numMonths = 0;

	// should get this from customer
        int numLics = 0;

        int numServers = Integer.parseInt(getRequiredValue("SERVER_COUNT"));

        if(orderType == LIC_TOOL) {
            licenseType = "ASIC Toolkit";
	    numMonths = 6;
	    project = getRequiredValue("CUSTOMER_PROJNAME");
	    numLics = 5;

            getRequiredValue("ASIC_CODENAME");
        }
        else if(orderType == LIC_XMX) {
            licenseType = "XMX";
	    numMonths = 12;
	    project = "XMX";
	    numLics = 3;
        }
	else
            handleException("invalid orderType: " + orderType);


        if(numServers < 1 || numServers > 3)
            handleException("Number of licenses requested is: " + numServers + " (should be between 1 and 3)");

        String redFlexServers = "";

        if(getRequiredValue("RED_SER").equals("0"))
            redFlexServers = "FLEX,no;\n";
        else if(getRequiredValue("RED_SER").equals("1")) {
            if(numServers == 3)
                redFlexServers = "FLEX-R,yes;\n";
            else {
                redFlexServers = "FLEX,no;\n";
                print("Less than 3 servers specified. NOT using redundant servers", V_IMP);
            }
        }
        else
            handleException("Value of RED_SER is: " + getRequiredValue("RED_SER") + " (should be 0 or 1)");


        String[][] serverList = parse2DList(numServers, 3, getRequiredValue("SERVER_LIST"));

        String serverInfo = "";

        for(int i = 0; i < numServers; i++) {
            serverInfo
                += "Server:" + serverList[i][1] + "," + serverList[i][0] + "," + serverList[i][2] + "," + redFlexServers;
        }


        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, numMonths);
        String expirationDate = licReqFormatter.format(c.getTime());

        String mgmtMail = getValue("LIC_ADMIN_EMAIL");
        if(mgmtMail.length() == 0)
            mgmtMail = getRequiredValue("E_MAIL");

        String fseMail = getRequiredValue("FSE_EMAIL_1");


        StringBuffer order = new StringBuffer();

        order.append("AccountKey:;\n");
        order.append("TransactionId:;\n");
        order.append("AccountName:;\n");
        order.append("requesterEmail:" + fseMail                                                              + ";\n");
        order.append("Group:"          + "ext_cust"                                                           + ";\n");
        order.append("CompanyName:"    + getRequiredValue("USERS_COMPANY")                                    + ";\n");
        order.append("initDate:"       + licReqFormatter.format(new Date())                                   + ";\n");
        order.append("MgmtPhone:"      + getRequiredValue("PHONE")                                            + ";\n");
        order.append("MgmtContact:"    + getRequiredValue("FIRST_NAME") + " " + getRequiredValue("LAST_NAME") + ";\n");
        order.append("Project:"        + project                                                              + ";\n");
        order.append("City:"           + getRequiredValue("CITY")                                             + ";\n");
        order.append("State:"          + getRequiredValue("STATE")                                            + ";\n");
        order.append("Country:"        + getRequiredValue("COUNTRY")                                          + ";\n");

        order.append(serverInfo);

        order.append("MgmtMail:"       + mgmtMail                                                             + ";\n");

        order.append("ASICAEContact:;\n");
        order.append("AccountCode:;\n");
        order.append("Status:1;\n");
        order.append("HTMLPath: ;\n");
        order.append("MailingAddr1: ;\n");
        order.append("MailingAddr2: ;\n");
        order.append("Cust_Asic:0;\n");

	if(orderType == LIC_TOOL) {
            order.append("License:(" + numLics + ")ASIC Toolkit,(" + numLics + ")PENDING-" + expirationDate + ";\n");
	}
	else if(orderType == LIC_XMX) {
            order.append("License:(" + numLics + ")EDA XMX,(" + numLics + ")PENDING-" + expirationDate + ";\n");
	}
	else
            handleException("invalid orderType: " + orderType);

        String orderFile = AUTO_LIC_DIR + "ord." + orderNumber;
        String licFile   = AUTO_LIC_DIR + "lic." + orderNumber;
        String errFile   = AUTO_LIC_DIR + "err." + orderNumber;

        String tmp_orderFile = AUTO_LIC_DIR + "tmp." + orderNumber;

        try {

            FileOutputStream out = new FileOutputStream(tmp_orderFile);
            out.write(order.toString().getBytes());
            out.close();
            File f = new File (tmp_orderFile);
            f.renameTo(new File(orderFile));

           //new for 5.3.1
            long stopWait = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000);   // 2 days
            long sendMailTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);   // 1 day

            boolean sentMail = false;

            int minutesWaited = 10;

            while(System.currentTimeMillis() < stopWait) {//while

               if(new File(licFile).isFile())
                  break;

               if(minutesWaited >= 10) {

                  print("waiting for file: " + licFile + "to appear at " + new Date(), V_IMP);
                  flushDisplay();
                  minutesWaited = 0;
               }

               try {
                  sleep(SHIPIT_WAIT_TIME * 10 * 1000);
                  minutesWaited += SHIPIT_WAIT_TIME;
               }
               catch(InterruptedException e) {
                  handleException(e, "Interrupted while waiting for " + licFile + "to appear");
               }

            }

            if(new File(orderFile).isFile()) {
               print("WARNING! : deleting order file: " + orderFileName, V_IMP);
               new File(orderFileName).delete();
            }
            if (new File(licFile).isFile()){
               String licContents = readFile(licFile, 1024);

               String subject = "Your " + licenseType + " license from IBM Customer Connect";

               String body =
                  "Dear ASIC Connect customer,\n" +
                  "\n" +
                  "The " + licenseType + " license(s) you requested is enclosed below.\n" +
                  "\n" +
                  "Thank You,\n" +
                  "ASIC Connect\n" +
                  "\n" +
                  "\n" +
                  licContents;

               sendMail(FROM_ID, mgmtMail, fseMail, null, subject, body, true);
            }
            else if (new File(errFile).isFile()){

               String errContents = readFile(errFile, 1024);

               handleException(errContents);
            }
            else {

               handleException(licFile + " did not appear within wait period. Ending wait and terminating order");

            }
        }
        catch(Throwable t) {
           handleException(t, "thrown before, during or after executing autolic");
        }



    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String formatTime(long time) {

        if(time < 5)
            return "less than 5 sec";

        else if(time < 60)
            return (time/5*5) + " sec";

        else if(time < 3600) {
            long minutes = time / 60;
            long seconds = time % 60;

            if(seconds < 5)
                return minutes + " min";
            else
                return minutes + " min " + (seconds/5*5) + " sec";
        }

        else {
            long hours = time / 3600;
            long minutes = (time % 3600) / 60;

            if(minutes < 5)
                return hours + " hr";
            else
                return hours + " hr " + (minutes/5*5) + " min";
        }

    }


    /**********************************************************************************************************************************************************************************************************************************/



    void handleExceptionNoMail(Throwable t, String s) {

        if(t.getClass().getName().endsWith("HandledException"))
            throw new HandledException();

        else {
            s += "\nThis Order was placed on: " + new Date(timestamp) + "\n"
                + "This error was thrown on: " + new Date() + "\n\n"
                + "The stack trace is as follows:\n\n"
                + getStackTrace(t);

            print(s, ERR);
            throw new HandledException();
        }
    }


    /**********************************************************************************************************************************************************************************************************************************/


    void handleExceptionNoMail(String s) {
        handleExceptionNoMail(new RuntimeException(), s);
    }


    /**********************************************************************************************************************************************************************************************************************************/



    void handleException(Throwable t, String s) {

        if(t.getClass().getName().endsWith("HandledException"))
            throw new HandledException();

        else {
            s += "\nThis Order was placed on: " + new Date(timestamp) + "\n"
                + "This error was thrown on: " + new Date() + "\n\n"
                + "The stack trace is as follows:\n\n"
                + getStackTrace(t);
            print(s, ERR);
            try {
                mail(UNEXP_ERROR, s);
            }
            catch(Throwable th) {
                print(getStackTrace(th), ERR);
            }

            throw new HandledException();
        }
    }



    /**********************************************************************************************************************************************************************************************************************************/


    void handleException(String s) {
        handleException(new RuntimeException(), s);
    }



    /**********************************************************************************************************************************************************************************************************************************/



    void handleMinorException(Throwable t, String s) {

        if(t.getClass().getName().endsWith("HandledException"))
            return;

        else {
            s += "\nThis Order was placed on: " + new Date(timestamp) + "\n"
                + "This warning was thrown on: " + new Date() + "\n\n"
                + "The stack trace is as follows:\n\n"
                + getStackTrace(t);
            print(s, WARN);
        }
    }


    /**********************************************************************************************************************************************************************************************************************************/



    void handleMinorException(String s) {
        handleMinorException(new RuntimeException(), s);
    }



    /**********************************************************************************************************************************************************************************************************************************/
    //new for 3.7.1

    boolean stringAlreadyExists (String str, Vector vec) {
        int size = vec.size();
        String val;

        for (int i = 0; i < size; i++) {
            val = (String) vec.elementAt(i);
            if (val.equals(str))
                return true;
        }
        return false;
    }

     public String gettkName (String platformName) {
	return (String) tkplatform.get(platformName);
    }
    
	public String getTkPlatform (String platformVal) {
   return (String) tkPlatformValues.get(platformVal);
   }

    //end of new for 3.7.1
    /*********************************************************************************************************************************************************************************************************************************/

    private String[][] parse2DList(int i, int j, String list) {

        StringTokenizer st = new StringTokenizer(list, ";");

        if(i != st.countTokens())
            handleException("Incorrect parameter (i): " + i + " for parse2DList()");

        String[][] arr = new String[i][j];
        int i2 = 0;

        while(st.hasMoreTokens()) {

            String tmp = st.nextToken().trim();
            StringTokenizer st2 = new StringTokenizer(tmp, ",");

            if(j != st2.countTokens())
                handleException("Incorrect parameter (j): " + j + " for parse2DList()");

            int j2 = 0;
            while(st2.hasMoreTokens())
                arr[i2][j2++] = st2.nextToken().trim();

            i2++;

        }

        return arr;

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private void mail(int mailType) {
        mail(mailType, null);
    }



    /**********************************************************************************************************************************************************************************************************************************/



    void mail(int mailType, String mailText) {
        print("Starting mail...", V_IMP);

        //  mailTypes

        // UNEXP_ERROR   = To EDSD_DEV_1 and EDSD_DEV_2

        // UNEXP_WARNING = To EDSD_DEV_1 and EDSD_DEV_2


        // SHIPIT_FAIL  = To Shipping manager saying shipit has failed
        //                cc Program manager

        // NO_MK_WAIT_1 = To Shipping manager saying modelkit has not
        //                shown up yet after critical stage 1
        //                cc Program manager

        // NO_MK_WAIT_2 = To Shipping manager saying modelkit has not
        //                shown up yet after critical stage 2
        //                cc Program manager

        // NO_MK_STOP   = To Shipping manager saying modelkit has not
        //                shown up yet after critical stage 3. Halting Thread
        //                cc Program manager

        // SHIPIT_SPACE_ERR = To Shipping manager saying shipit_space_script
        //                    returned a non-zero exit value.Please complete order
        //                    cc Program manager



        // FSE_REQ_LIC  = To Support Engineer to send License to customer

        // SM_REQ_CD    = To Shipping Manager to send CD to customer

        // SM_REQ_ALOC  = To Shipping Manager to handle additional shipping locations

        // ALOC_DR_ORD  = To Additional shipping locations saying customer has requested delta release


        String from = "", subject = "", body = "";
        String[] to = new String[1];
        String[] cc = null;

        /*******************************FROM******************************/

        if(mailType == SHIPIT_FAIL || mailType == NO_MK_WAIT_1 || mailType == NO_MK_WAIT_2 || mailType == NO_MK_STOP || mailType == SHIPIT_SPACE_ERR)
            from = "EDSD_ERROR@us.ibm.com";
        else if(mailType == UNEXP_ERROR || mailType == UNEXP_WARNING)
            from = "EDSD_ERROR@us.ibm.com";
        else
            from = this.FROM_ID;

        /******************************************************************/


        /***************************** TO and CC **************************/
        int shiptoLocCount = 0;
        if(mailType == SM_REQ_ALOC || mailType == ALOC_DR_ORD) {
            shiptoLocCount = Integer.parseInt(getRequiredValue("SHIPTO_LOC_COUNT"));
            if(shiptoLocCount == 0)
                return; // no additional locations, so no mail to be sent
        }

        if(mailType == SHIPIT_FAIL || mailType == NO_MK_WAIT_1 || mailType == NO_MK_WAIT_2 || mailType == NO_MK_STOP || mailType == SHIPIT_SPACE_ERR) {
            if(EDSD_DEV_1 == null || EDSD_DEV_1.trim().length() == 0)
                return;
            else
                to[0] = EDSD_DEV_1;

            if(EDSD_DEV_2 != null && EDSD_DEV_2.length() != 0) {
                cc = new String[1];
                cc[0] = EDSD_DEV_2;
            }

/*
try {
String[] temp = getShipManagerEmail(this.technology);
to[0] = temp[1];  // Ship Manager
cc = new String[1];
cc[0] = temp[0];  // Program Manager
}
catch(Throwable t) {
handleException(t, "thrown trying to get ship manager email");
}
*/
        }

        else if(mailType == UNEXP_ERROR || mailType == UNEXP_WARNING) {
            if(EDSD_DEV_1 == null || EDSD_DEV_1.trim().length() == 0)
                return;
            else
                to[0] = EDSD_DEV_1;

            if(EDSD_DEV_2 != null && EDSD_DEV_2.length() != 0) {
                cc = new String[1];
                cc[0] = EDSD_DEV_2;
            }
        }

        else if(mailType == FSE_REQ_LIC) {
            if(orderType == LIC_XMX) {
                to[0] = getProperty("XMX_LIC_MAIL_TO");
                cc = new String[1];
                cc[0] = getProperty("XMX_LIC_MAIL_CC");
            }
            else {
                to[0] = getRequiredValue("FSE_EMAIL_1");
            }
        }


        else if(mailType == SM_REQ_CD || mailType == SM_REQ_ALOC)
            try {
                to[0] = getShipManagerEmail(this.technology)[1];
            }
            catch(Throwable t) {
                handleException(t, "thrown trying to get ship manager email");
            }

        else if(mailType == ALOC_DR_ORD) {
            to = new String[shiptoLocCount];
            for(int i = 0; i < shiptoLocCount; i++)
                to[i] = getRequiredValue("EMAIL_" + (i+1));
        }
        else if( mailType == DROPBOX_ERR_MAIL )
        {
        	to[0] = getRequiredValue("E_MAIL"); 
        	String ccList = getCcList();
			if( ccList != null && !ccList.equals("") )
			{
				StringTokenizer st = new StringTokenizer(ccList, ",");
				ArrayList ccUserList = new ArrayList();
				while( st.hasMoreTokens() )
				{							
					String userId = st.nextToken();	
					ccUserList.add(userId);				
				}
				cc = (String[])ccUserList.toArray( new String[ccUserList.size()] );
			}
        }
        else
            handleException("Invalid mailType parameter: " + mailType + " for method: mail()");


        /*****************************************************************/


        /*****************************SUBJECT*****************************/

        if(mailType == SHIPIT_FAIL || mailType == SHIPIT_SPACE_ERR)
            subject = "IBM Customer Connect: Unable to process order (" + this.orderNumber + ") for " + this.technology + " " + this.versionNo;

        else if(mailType == NO_MK_WAIT_1 || mailType == NO_MK_WAIT_2 || mailType == NO_MK_STOP)
            subject = "IBM Customer Connect: Order (" + this.orderNumber + ") in shipit queue for " + modelKitWait + " days for " + this.technology + " " + this.versionNo;

        else if(mailType == UNEXP_ERROR)
            subject = "WAKE UP! YOUR UNBREAKABLE CODE JUST BROKE!!!";

        else if(mailType == UNEXP_WARNING)
            subject = "MQHANDLER WARNING!";
        else if( mailType == DROPBOX_ERR_MAIL )
			subject = "IBM Customer Connect: Unable to process dropbox order (" + this.orderNumber + ") for " + this.technology + " " + this.versionNo;
        else if(mailType == FSE_REQ_LIC) {
            if(orderType == LIC_TOOL)
                subject = "IBM Customer Connect: License Request for Toolkit";
            else if(orderType == LIC_FULLPD)
                subject = "IBM Customer Connect: License Request for Full PD Kit";
            else
                subject = "IBM Customer Connect: License Request for X Collaborative Tool";
        }


        else if(mailType == SM_REQ_CD)
            subject = "IBM Customer Connect: CD Order (" + this.orderNumber + ") for " + this.technology + " " + this.versionNo + " " + OrderProcessor.orderNames[orderType];


        else if(mailType == SM_REQ_ALOC)
            subject = "IBM Customer Connect: Additional Locations for order (" + this.orderNumber + ") for " + this.technology + " " + this.versionNo + " " + OrderProcessor.orderNames[orderType];


        else if(mailType == ALOC_DR_ORD)
            subject = "IBM Customer Connect Alert: Customer order for DeltaReleases for " + getRequiredValue("TECHNOLOGY") + " " + this.versionNo;


        else
            handleException("Invalid mailType parameter: " + mailType + " for method: mail()");

        /****************************************************************/


        /******************************BODY******************************/
        if(mailType == UNEXP_ERROR || mailType == UNEXP_WARNING) {

            String outputFile = FAILED_ORDERS_DIR + this.orderFileName;

            body = "Dear IBM Customer Connect Developer,\n\n"
                + "Despite all those nights you stayed up debugging,\n"
                + "an ASIC " + OrderProcessor.orderNames[orderType] + " Order might not have been completed.\n\n"
                + "The available info is as follows:\n\n"
                + mailText
                + "\n\nThis is what you get for working at IBM!\n\n"
                + "Please follow your very long and miserable problem resolution procedure\n"
                + "to attempt in vain to correct the problem.\n\nThe relevant information is at the following location:\n\t" + outputFile
                + "\n\nHave a great time debugging. You asked for it!\n";
        }

        else {
            String outputFile = COMPLETED_ORDERS_DIR + this.orderFileName;

            if(mailType == SHIPIT_FAIL || mailType == NO_MK_WAIT_1 || mailType == NO_MK_WAIT_2 || mailType == NO_MK_STOP || mailType == SHIPIT_SPACE_ERR)
                outputFile = TDOF_DIR + "DL/ModelKit." + orderNumber + ".filtered";

            else if(mailType == SM_REQ_CD) {

                if(orderType == DESIGN) {
                    outputFile = TDOF_DIR + "CD/ModelKit." + orderNumber + ".filtered";
                }
//                else
//                    outputFile = CURRENT_ORDERS_DIR + this.orderFileName;

            }
            else if(mailType == SM_REQ_ALOC) {
                outputFile = TDOF_DIR + "DL/ModelKit." + orderNumber + ".filtered";
                // no need to create TDOF file as it was already created for shipit
            }

            if(mailType == SHIPIT_FAIL)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + customerProjname
                    + " project.\n\n"
                    + "This order failed to be processed successfully by the shipit program.\n\n"
                    + "Please follow your problem resolution procedure to correct the problem.\n\n"
                    + "The exact shipit command (and the parameters passed to it) called was as follows:\n"
                    + "\t" + mailText + "\n\n"
                    + "Thank You,\nASIC Connect";


            else if(mailType == NO_MK_WAIT_1)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + customerProjname
                    + " project.\n\nThis order has been in the shipit queue for "
                    + modelKitWait
                    + " days now.\n\nPlease be advised that we are still waiting for the file (ModelKit."
                    + this.orderNumber
                    + ".tar) to show up.\n\n"
                    + "Please follow your problem resolution procedure to correct the problem.\n\n"
                    + "The exact shipit command (and the parameters passed to it) called was as follows:\n"
                    + "\t" + mailText + "\n\n"
                    + "Thank You,\nASIC Connect";


            else if(mailType == NO_MK_WAIT_2)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + customerProjname
                    + " project.\n\nThis order has been in the shipit queue for "
                    + modelKitWait
                    + " days now.\n\nPlease be advised that we are still waiting for the file (ModelKit."
                    + this.orderNumber
                    + ".tar) to show up.\n\n"
                    + "\nWE WILL BE TERMINATING THE WAIT FOR THIS ORDER IN "
                    + (CRITICAL_DAYS_3 - CRITICAL_DAYS_2)
                    + " DAYS.\n\n"
                    + " Please follow your problem resolution procedure to correct the problem.\n\n"
                    + "The exact shipit command (and the parameters passed to it) called was as follows:\n"
                    + "\t" + mailText + "\n\n"
                    + "Thank You,\nASIC Connect";


            else if(mailType == NO_MK_STOP)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + customerProjname
                    + " project.\n\nThis order has been in the shipit queue for "
                    + modelKitWait
                    + " days now.\n\n"
                    + "\nPLEASE BE ADVISED THAT WE ARE NOW TERMINATING THE WAIT FOR THIS ORDER.\n\n"
                    + " Please follow your problem resolution procedure to correct the problem.\n\n"
                    + "The exact shipit command (and the parameters passed to it) called was as follows:\n"
                    + "\t" + mailText + "\n\n"
                    + "Thank You,\nASIC Connect";


            else if(mailType == SHIPIT_SPACE_ERR)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + customerProjname
                    + " project.\n\n"
                    + MK_SIZE_SCRIPT + " returned an exit value of " + mailText + "."
                    + "\nHence we could not complete the order."
                    + "\n\nPlease follow your problem resolution procedure to correct the problem.\n\n"
                    + "The TDOF file for this order is at the following location:\n"
                    + "\t" + outputFile + "\n\n"
                    + "Thank You,\nASIC Connect";


            else if(mailType == FSE_REQ_LIC) {

                int numServers = Integer.parseInt(getRequiredValue("SERVER_COUNT"));

                // should get this from customer
                int numLics = 5;

		if(orderType == LIC_XMX)
		    numLics = 3;

                if(numServers < 1 || numServers > 3)
                    handleException("Number of licenses requested is: " + numServers + " (should be between 1 and 3)");

                String[][] serverList = parse2DList(numServers, 3, getRequiredValue("SERVER_LIST"));

                String serverInfo = "";

                for(int i = 0; i < numServers; i++) {
                    serverInfo
                        += "\n    " + (i+1) + ". ID       : " + serverList[i][1]
                        + "\n       Name     : " + serverList[i][0]
                        + "\n       Type     : flex"
                        + "\n       Platform : " + serverList[i][2] + "\n";
                }

                String redFlexServers = "";

                if(getRequiredValue("RED_SER").equals("0"))
                    redFlexServers = "No";
                else if(getRequiredValue("RED_SER").equals("1"))
                    redFlexServers = "Yes";
                else
                    handleException("Value of RED_SER is: " + getRequiredValue("RED_SER") + " (should be 0 or 1)");


                if(orderType == LIC_TOOL) {

                    getRequiredValue("ASIC_CODENAME");

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, 6);
                    String expirationDate = licReqFormatter.format(c.getTime());

                    body
                        = "Dear " + getRequiredValue("FSE_NAME_1") + ",\n\nAn ASIC ToolKit license has been requested via IBM Customer Connect.\nThe details of this request are:\n"
                        + "\n  Kit Type                 : " + "ASIC Toolkit"
                        + "\n  Company Name             : " + getRequiredValue("USERS_COMPANY")
                        + "\n  Customer Contact         : " + getRequiredValue("FIRST_NAME") + " " + getRequiredValue("LAST_NAME")
                        + "\n  Phone Number             : " + getValue("PHONE")
                        + "\n  FAE/AE                   : " + getRequiredValue("FSE_EMAIL_1")
                        + "\n  Design Project           : " + getRequiredValue("CUSTOMER_PROJNAME")
                        + "\n  City                     : " + getRequiredValue("CITY")
                        + "\n  State                    : " + getRequiredValue("STATE")
                        + "\n  Country                  : " + getRequiredValue("COUNTRY")
                        + "\n  License Admin Email      : " + getValue("LIC_ADMIN_EMAIL")
                        + "\n  License Expiration Date  : " + expirationDate
                        + "\n  No. of Licenses          : " + numLics + "\n"
                        + "\n  License Server Selection : " + "\n" + serverInfo
                        + "\n  Redundant FLEX servers   : " + redFlexServers
                        + "\n  Justification            : " + "ASIC Connect"
                        + "\n  Requester e-mail         : " + getRequiredValue("E_MAIL")

                        + "\n\nPlease place the above request at:   http://w3.eda.ibm.com/cgi-bin/make_lic.cgi"
                        + "\n\n\nThank you,\nASIC Connect";

                }
                else {  // for both FullPD and XMX  licenses

                    String str = "";

                    if(orderType == LIC_FULLPD)
                        str = "Full PD Kit";
                    else if(orderType == LIC_XMX)
                        str = "X Collaborative Tool";

                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.MONTH, 12);
                    String expirationDate = licReqFormatter.format(c.getTime());

                    body
                        = "Dear " + getRequiredValue("FSE_NAME_1") + ",\n\nA license for " + str + " has been requested via IBM Customer Connect.\nThe details of this request are:\n"
                        + "\n  Kit Type                 : " + str
                        + "\n  Company Name             : " + getRequiredValue("USERS_COMPANY")
                        + "\n  Customer Contact         : " + getRequiredValue("FIRST_NAME") + " " + getRequiredValue("LAST_NAME")
                        + "\n  Phone Number             : " + getValue("PHONE")
                        + "\n  FAE/AE                   : " + getRequiredValue("FSE_EMAIL_1")
                        + "\n  City                     : " + getRequiredValue("CITY")
                        + "\n  State                    : " + getRequiredValue("STATE")
                        + "\n  Country                  : " + getRequiredValue("COUNTRY")
                        + "\n  License Admin Email      : " + getValue("LIC_ADMIN_EMAIL")
                        + "\n  License Expiration Date  : " + expirationDate
                        + "\n  No. of Licenses          : " + numLics + "\n"
                        + "\n  License Server Selection : " + "\n" + serverInfo
                        + "\n  Redundant FLEX servers   : " + redFlexServers
                        + "\n  Justification            : " + "ASIC Connect"
                        + "\n  Requester e-mail         : " + getRequiredValue("E_MAIL")

                        + "\n\nPlease place the above request at:   http://w3.eda.ibm.com/cgi-bin/make_lic.cgi"
                        + "\n\n\nThank you,\nASIC Connect";

                }

            }


            else if(mailType == SM_REQ_CD)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " (OrderNo.: "
                    + this.orderNumber
                    + ") has been submitted by \n"
                    + firstName
                    + " " + lastName
                    + "\n\nPlease process this order. The relevant information is at the following location:\n"
                    + "\n\t"
                    + outputFile

                    + "\n\n\n"
                    + "The contents of the order file are as follows:\n\n"
                    + mailText

                    + "\n\nThank You,\nASIC Connect";

            else if(mailType == SM_REQ_ALOC)
                body
                    = "Dear IBM ASIC Ship Manager,\n\nAn ASIC "
                    + OrderProcessor.orderNames[orderType]
                    + " (OrderNo.: "
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support their "
                    + this.customerProjname
                    + " project.\n\n"
                    + OrderProcessor.orderNames[orderType]
                    + " delivered via Download have been sent to the customer only.\n\n"
                    + "Please process any remaining ship to locations specified on this order.\n\n"
                    + "The relevant information is at the following location:\n\n"
                    + "\t" + outputFile

                    + "\n\n\n"
                    + "The contents of the order file are as follows:\n\n"
                    + mailText

                    + "\n\nThank You,\nASIC Connect";


            else if(mailType == ALOC_DR_ORD) {
                String patches = "";
                for(int i = 0; i < patchList.length; i++)
                    patches += "\n\t" + patchList[i];
                body
                    = "Dear IBM ASIC support engineer,\n\n"
                    + "The following IBM ASIC customer has ordered Delta Release(s) for "
                    + getRequiredValue("TECHNOLOGY")
                    + this.versionNo
                    + "\n\n\t Customer Name: "
                    + firstName
                    + " "
                    + lastName
                    + "\n\n\t Project Name: "
                    + this.customerProjname
                    + "\n\n The following Delta Releases were ordered:\n\n"
                    + patches
                    + "\n\nYou can also order these delta releases by visiting the\n"
                    + "IBM Customer Connect web-site at  http://www.ibm.com/technologyconnect"
                    + "\n\nThank You,\nASIC Connect";
            }
            else if( mailType == DROPBOX_ERR_MAIL )
            {
               
				body = "Dear IBM Customer Connect Customer,\n\n";
			    
			    if( this.technology != null && this.versionNo != null )
			    {
			    	body += "The ASIC " 
			    	     + OrderProcessor.orderNames[orderType]
						 + " Order ("
						 + this.orderNumber
						 + ") for "
						 + this.technology
						 + " " 
						 + this.versionNo
						 + " could not be uploaded to the dropbox. \n";
			    }
			    else
			    {
					body += "The ASIC " 
						 + OrderProcessor.orderNames[orderType]
						 + " Order ("
						 + this.orderNumber
						 + ") could not be uploaded to the dropbox. \n";
			    }
			    body += "Your request to use dropbox could not be submitted at this time due to an internal server problem. " 
					+ " \n"
					+ mailText
					+ ".\nIf you have any questions, please contact the IBM Customer Connect Help Desk \n"
                    + "by email at " + this.REPLY_TO + " or by phone at 1-888-220-3343. \n"
					+"We apologize for any inconvenience.\n\n"
		  			+"IBM Customer Connect";  
            }
            else
                handleException("Invalid mailType parameter: " + mailType + " for method: mail()");
        }


        /******************************************************************/


        if(this.TESTING.equals("yes")) {
            subject = TESTING_APPEND_STRING + subject;
            body = TESTING_APPEND_STRING + "\n\n" + body;
        }



        if(mailType == SM_REQ_CD || mailType == SM_REQ_ALOC) {

            if(this.orderType == DESIGN && isTester(getRequiredValue("ASIC_CODENAME"), "ASIC_CODENAME")) {
                print("TEST_ID - NOT SENDING MAIL TO SHIP MANAGER", V_IMP);
                return;
            }

            else if(this.orderType == PREVIEW && isTester(this.edgeUserid, "EDGE_USERID")) {
                print("TEST_ID - NOT SENDING MAIL TO SHIP MANAGER", V_IMP);
                return;
            }

        }



        if(mailType == UNEXP_ERROR || mailType == UNEXP_WARNING)
            sendMail(from, to, cc, subject, body, false);
        else
            sendMail(from, to, cc, subject, body, true);
    }




    /**********************************************************************************************************************************************************************************************************************************/



    private boolean isTester(String value, String valueType) {

        try {

            if(valueType.equals("ASIC_CODENAME")) {

                parseAsicCodenameFile();

                if(ignoreAsicCodenames == null)
                    return false;

                for(int i = 0; i < ignoreAsicCodenames.length; i++)
                    if(value.equals(ignoreAsicCodenames[i]))
                        return true;
            }

            else if(valueType.equals("EDGE_USERID")) {

                parseEdgeUseridFile();

                if(ignoreEdgeUserids == null)
                    return false;

                for(int i = 0; i < ignoreEdgeUserids.length; i++)
                    if(value.equals(ignoreEdgeUserids[i]))
                        return true;
            }

        }
        catch(IOException e) {
            handleException(e, "thrown parsing file for tester " + valueType);
        }

        return false;

    }





    /**********************************************************************************************************************************************************************************************************************************/



    private static synchronized void parseAsicCodenameFile() throws IOException {

        long t = new File(ignoreAsicCodenameFile).lastModified();

        if(t != lastUpdatedIAC) {
            ignoreAsicCodenames = parseFile(ignoreAsicCodenameFile);
            lastUpdatedIAC = t;
        }

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private static synchronized void parseEdgeUseridFile() throws IOException {

        long t = new File(ignoreEdgeUseridsFile).lastModified();

        if(t != lastUpdatedIEU) {
            ignoreEdgeUserids = parseFile(ignoreEdgeUseridsFile);
            lastUpdatedIEU = t;
        }

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private static String[] parseFile(String filename) throws IOException {

        if(new File(filename).length() <= 0)
            return null;

        String line = null;

        Vector v = new Vector();

        BufferedReader in = new BufferedReader(new FileReader(filename));

        try {
            while((line = in.readLine()) != null) {
                line = line.trim();
                if(line.length() != 0)
                    v.addElement(line);
            }
        }
        finally {
            in.close();
        }

        String[] arr = new String[v.size()];

        v.toArray(arr);

        return arr;

    }


  //new for 4.2.1

  /**********************************************************************************************************************************************************************************************************************************/


    private String parseShiplist(String filename) {

        if(new File(filename).length() <= 0)
            return null;

        String line="", output="";

        BufferedReader in = null;

        try{

           in = new BufferedReader(new FileReader(filename));

            while((line = in.readLine()) != null) {
                line = line.trim();
                if(line.length() != 0 && !line.startsWith("/*")){
                   output += line+",";
                }

            }
        }
        catch (Exception e) {
           handleException(e, "thrown parsing exception for shiplist " + filename);
        }
        finally {
           try{
              in.close();
           }
           catch(IOException ioe) {
              handleException(ioe, "thrown IOexception for shiplist " + filename);
           }
        }

        return output;

    }

  //end of new for 4.2.1


    /**********************************************************************************************************************************************************************************************************************************/




    private void sendMail(String from, String[] to, String[] cc, String subject, String body, boolean mailOnError) {

        sendMail(from, arrayToString(to), arrayToString(cc), null, subject, body, mailOnError);

    }




    /**********************************************************************************************************************************************************************************************************************************/



    private void sendMail(String from, String to, String cc, String replyTo, String subject, String body, boolean mailOnError) {

        print("Starting sendMail()...", V_IMP);

        if(from != null)
            from = from.trim();
        if(to != null)
            to   = to.trim();
        if(cc != null)
            cc = cc.trim();


        String mailString
            = "FROM: "      + from
            + "\nTO: "      + to
            + "\nCC: "      + cc
            + "\nSUBJECT: " + subject
            + "\nBODY: "    + body;


        if(from == null || to == null || from.length() == 0 || to.length() == 0) {
            if(mailOnError)
                handleException("FROM or TO for sendMail is empty in the following mail:\n" + mailString);
            else
                handleExceptionNoMail("FROM or TO for sendMail is empty in the following mail:\n" + mailString);
        }

        print("Sending mail:\n" + mailString, NOT_IMP);

        long[] sleepTime = {5000, 10000};

        boolean mailSent = false;

        int numRetries;

        if(mailOnError)
            numRetries = EMAIL_NUM_RETRIES;
        else
            numRetries = 2;

        String[] emailHost = {EMAIL_HOST_NAME, BACKUP_1_EMAIL_HOST_NAME, BACKUP_2_EMAIL_HOST_NAME};

        int numHosts = emailHost.length;

        mailBlock: for(int i = 0; i <= numRetries; i++) {

            for(int j = 0; j < numHosts; j++) {

                try {
                    print("Sending mail through SMTP host: " + emailHost[j] + " to: " + to + " cc: " + cc + " replyTo: " + replyTo  + " subject: " + subject + " at " + new Date(), V_IMP);
                    Mailer.sendMail(emailHost[j], from, to, cc, null, replyTo, subject, body);
                    mailSent = true;
                    print("Sent mail on attempt# " + ( (i*numHosts) + j + 1 ) + " at " + new Date(), V_IMP);
                    break mailBlock;
                }
                catch(Throwable t) {
                    String str =
                        "thrown while trying to send email (attempt# "
                        + ( (i*numHosts) + j + 1 )
                        + ")\n\n"
                        + "StackTrace:\n" + getStackTrace(t) + "\n\n"
                        + "Will Re-try " + ( (numRetries - i) * numHosts + (numHosts - 1 - j) ) + " times\n\n"
                        + "This error was thrown at: " + new Date();
                    print(str, ERR);
                    flushDisplay();
                }
            }

            try {
                if(i < sleepTime.length)
                    sleep(sleepTime[i]);
                else
                    sleep(sleepTime[sleepTime.length - 1]);
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
                = "ERROR: The above email could NOT be sent despite "
                + ( (numRetries + 1) * numHosts)
                + " attempts";
            if(mailOnError)
                handleException(str);
            else
                handleMinorException(str);
        }
    }




    /**********************************************************************************************************************************************************************************************************************************/



    static String getRoundArrow(String altname) {

        return "<img src=\"//www.ibm.com/i/v11/buttons/arrow_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"" + altname + "\" />";

    }



    /**********************************************************************************************************************************************************************************************************************************/



    static String getJavascriptLaunchURL(String url, String name, int height, int width) {

        name = name.replace(' ', '_');
        return
            " href=\"javascript:;\"" +
            " onclick=\"launch(\'" + url + "\', \'" + name + "\', \'" + height + "\', \'" + width + "\')\"" +
            " onkeypress=\"launch(\'" + url + "\', \'" + name + "\', \'" + height + "\', \'" + width + "\')\"";

    }


    /**********************************************************************************************************************************************************************************************************************************/



    static String getJavascriptLaunchCode() {

        return
            "\n\n\n<script language=\"javascript1.1\" type=\"text/javascript\">\n" +
            "<!--\n" +
            "function launch(url, name, height, width) {\n" +
            "open(" +
            "url" +
            ", " +
            "name" +
            ", \"height=\" + height + \",width=\" + width + \",menubar=no,resizable=yes,status=no,toolbar=no,scrollbars=yes\");\n" +
            "}\n// -->\n</script>\n\n";

    }



    /**********************************************************************************************************************************************************************************************************************************/



    private String getInboxEncoding(String action, String name, int height, int width) {

        String url = getJavascriptLaunchURL(action, "ASIC_Connect_Download_Order", height, width);

        return
//            "<a" + url + ">" + getRoundArrow(name) + "</a>\n" +
            "<a" + url + ">" + name                + "</a>\n";

    }



    /**********************************************************************************************************************************************************************************************************************************/


    private String getEmailFileEntry(String fileDesc, String fileID, String abbFileName, String fileSize, String[] downloadTimes) {

        return getEmailFileEntry(fileDesc, fileID, abbFileName, fileSize, downloadTimes, null);

    }


    /**********************************************************************************************************************************************************************************************************************************/



    private String getEmailFileEntry(String fileDesc, String fileID, String abbFileName, String fileSize, String[] downloadTimes, String patchDate) {

        if(fileDesc == null)
            fileDesc = "";
        else
            fileDesc = fileDesc + "\n\n";


        if(patchDate == null)
            patchDate = "\n";
        else
            patchDate =
              "Release date: " + patchDate + "\n\n";


        // String url = URL_PREFIX + "/" + abbFileName + "?id=" + fileID;
        String url = URL_PREFIX + "?id=" + fileID + "&file_name=" + abbFileName;

	if(isPrimaryCustomer)
            url += "&order=" + this.orderNumber;


        String smartOption = "";
        String notSmartOption = "";

        if(useSmartDownload) {

            notSmartOption = "(this is the default browser based download option)\n";

            String smartUrl = url + "&smart=yes";

            smartOption =
                  "\n"
                + "New: Use smart download application (requires one time install)\n"
                + "     (recommended if you experience broken network connections)\n\n"
                + smartUrl + "\n";

        }


        return
              "\n"
            + "-----------------------------------------------------------------\n\n"
            + fileDesc
            + url + "\n"
            + notSmartOption
            + "\n"
            + "File name: " + abbFileName + " (Size: " + fileSize + ")\n"
            + patchDate
            + "Estimated Download Time:\n"
            + "                " + downloadTimes[0] + "\n"
            + "                " + downloadTimes[1] + "\n"
            + smartOption;

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private String getInboxFileEntry(String fileDesc, String fileID, String abbFileName, String fileSize, String[] downloadTimes) {

        return getInboxFileEntry(fileDesc, fileID, abbFileName, fileSize, downloadTimes, null);

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private String getInboxFileEntry(String fileDesc, String fileID, String abbFileName, String fileSize, String[] downloadTimes, String patchDate) {

        if(patchDate == null)
            patchDate = "";
        else
            patchDate =
              "       Release date: " + patchDate + "<br />\n";


        // String url = URL_PREFIX + "/" + abbFileName + "?id=" + fileID;
        String url = URL_PREFIX + "?id=" + fileID + "&amp;file_name=" + abbFileName;

	if(isPrimaryCustomer)
            url += "&amp;order=" + this.orderNumber;

        String smartUrl = url + "&amp;smart=yes";


        if(orderType == PREVIEW)
            url = getInboxEncoding(url, fileDesc, 600, 525);
        else
            url = "       <a href=\"" + url + "\">" + fileDesc + "</a>";


        String smartOption = "   <br /> ";
        String notSmartOption = "";

        if(useSmartDownload) {

            notSmartOption = "(this is the default browser based download option) <br /> <br /> \n";

            smartOption =
//                  "New: " + getInboxEncoding(smartUrl, "Use smart download application", 600, 440) + " (requires one time install)<br />"
                  "New: " + getInboxEncoding(smartUrl, "Use smart download application", 250, 440) + " (requires one time install)<br />"
                + "     (recommended if you experience broken network connections)\n\n"
                + "   <br /> <br /> ";

        }


        String str =
              url + "<br />\n"
            + notSmartOption
            + "       File name: " + abbFileName + " (Size: " + fileSize + ")<br />\n"
            + patchDate
            + "       Estimated Download Time:\n"
            + "       <ul>"
            + "           <li>" + downloadTimes[0] + "</li>\n"
            + "           <li>" + downloadTimes[1] + "</li>\n"
            + "       </ul>\n"
            + smartOption;

        return "   <li> \n" + str + "</li> \n";

    }


    /**********************************************************************************************************************************************************************************************************************************/

    private String getCcList() {

	if( ! isPrimaryCustomer )
	    return "";

	String ccList, list = "";

	if(fseEmail.length() != 0)
	    list += this.fseEmail + ",";

        ccList = getValue("CC_LIST");
        if(ccList.length() != 0)
	    list += ccList + ",";

        ccList = getValue("RCPT_CUST_EMAILS");
        if(ccList.length() != 0)
	    list += ccList + ",";

        ccList = getValue("RCPT_FSE_EMAILS");
        if(ccList.length() != 0)
	    list += ccList + ",";

	return list;

    }


    /**********************************************************************************************************************************************************************************************************************************/


    private void mqSendReady(String[] abbFileNames, String[] fileIDs, String[] fileSizes, String[][] downloadTimes, String DRpackets)  {

        print("Starting mqSendReady()...", V_IMP);

	String orderNum;
	if(isPrimaryCustomer)
	    orderNum = this.orderNumber;
	else
	    orderNum = this.orderNumber + "-" + addShipTo;

        int numFiles = abbFileNames.length;

        String technology = null;
        if(orderType != DSE && orderType != FULLPD && orderType != XMX)
            technology = getRequiredValue("TECHNOLOGY");

        StringBuffer output = new StringBuffer();

        output.append("EDGE_USERID = " + this.edgeUserid + "^");

        output.append("ORDER_NUMBER = " + orderNum + "^");

        output.append("PREFERENCE = " + this.notification + "^");

        output.append("FROM = " + this.FROM_ID + "^");

        output.append("REPLYTO = " + this.REPLY_TO + "^");

		output.append("CC_LIST = " + getCcList() + "^");
		
		print("MQ Message Header " + output, V_IMP);
		flushDisplay();

        output.append("SUBJECT = " + "Your " + OrderProcessor.orderNames[orderType] + " order # " + orderNum + " has been processed and is ready for download" + "^");

        String header = "", footer = "", body = "";
        String emailHeader = "", emailFooter = "", emailBody = "";

        String expirationDate = customerFormatter.format(new Date(this.expirationTime));


// if(this.notification.equalsIgnoreCase("email")) {

	if(isPrimaryCustomer) {

	    String cdNote = "";
	    if(this.cd) {
		cdNote =
                      "\n"
		    + "This order was requested on CD. The CD will be shipped to the \n"
		    + "primary order recipient. In addition, we are providing this \n"
		    + "order to you by means of download for your convenience.\n";
	    }

	    if(this.edgeUserid.equals(this.orderBy)) {
		emailHeader
		    = "Dear ASIC Connect customer,\n\n"
                    + "Your " + OrderProcessor.orderNames[orderType] + " order # " + orderNum + " is ready for download.\n"
		    + cdNote;
            }
            else if (this.orderBy.equalsIgnoreCase("SYSTEM")) {//new for 5.1.1
               String inboxLink = URL_PREFIX.substring(0, URL_PREFIX.lastIndexOf('/')) + "/EdesignInboxServlet.wss?sub_func=account";
               emailHeader=
                "Dear ASIC Connect customer,\n"
                    + "\n"
                    + "A " + OrderProcessor.orderNames[orderType] + " order (# " + orderNum + ") placed on your behalf by \n"
                    + "system is ready for download. \n"
                  + cdNote
                    + "\n"
                    + "You can also access this order from your IBM Customer Connect Inbox at:\n"
                  + inboxLink + "\n";

            } //end of new for 5.1.1
            else {  // when FSE orders on behalf of customer

                String fseName = getValue("FSE_NAME_1");
                if(fseName.length() != 0)
                    fseName = "(" + fseName + ") ";

                String inboxLink = URL_PREFIX.substring(0, URL_PREFIX.lastIndexOf('/')) + "/EdesignInboxServlet.wss?sub_func=account";

                emailHeader
                    = "Dear ASIC Connect customer,\n"
                    + "\n"
                    + "A " + OrderProcessor.orderNames[orderType] + " order (# " + orderNum + ") placed on your behalf by \n"
                    + "your IBM ASIC support engineer " + fseName + "is ready for download. \n"
		    + cdNote
                    + "\n"
                    + "You can also access this order from your IBM Customer Connect Inbox at:\n"
                    + inboxLink + "\n"
                    + "\n"
                    + "You can contact your IBM ASIC support engineer at the following \n"
                    + "email address: " + this.fseEmail + "\n";

            }
	}
	else {
                String fseName = getValue("FSE_NAME_1");
                if(fseName.length() != 0)
                    fseName = "(" + fseName + ") ";

		String edgeCust = getValue("FIRST_NAME") + " " + getValue("LAST_NAME");
		String proj = getValue("CUSTOMER_PROJNAME");

                emailHeader
                    = "Dear ASIC Connect customer,\n"
                    + "\n"
                    + "A " + OrderProcessor.orderNames[orderType] + " order (# " + orderNum + ") placed by an IBM Customer Connect \n"
                    + "customer (" + edgeCust + ") is ready for download. \n"
                    + "You are listed as an additional recipient for project: " + proj + ". \n"
                    + "\n"
                    + "For further information, please contact your IBM ASIC support \n"
		    + "engineer " + fseName + "at the following email address: " + this.fseEmail + "\n";
	}

	   emailHeader       +=   "Please Note:" + "\n"  
	                    + "-----------------------------------------------------------------\n"
				        + "This order will expire on " + expirationDate + "\n"
						+ "-----------------------------------------------------------------\n";
	
	   emailHeader +=   "You can use any of the following methods to pick up your order." + "\n" 
                        + "•    Download from Browser: allows you to download files using your browser." + "\n" ; 


       if(useSmartDownload) {
      
       emailHeader +=  "•    The Smart Download from Browser:  uses a Java application, installed on your computer, " 
                        + "to provide a more robust download where you can recover from a dropped network connection.  "
                        + "\n";                 
        }
        
		if(isDropbox) {
      
	   emailHeader +=  "•    Dropbox Tools: these place the order in your Customer Connect dropbox.  " 
	                 + " You can choose to use the GUI dropbox, the web-based dropbox, or the sftp dropbox. " 
					 + "\n";                 
		}
        

	if(this.customModelKit) {
                emailHeader +=
                      "\n"
                    + "In an initiative to reduce the size of your design kit, we have \n"
		    + "only included platform-specific models relevant to you. As a result, \n"
                    + "all platform-specific models (NDRs, OLA and smart models) will now \n"
                    + "be contained in the custom model kit. To avoid any problems due to \n"
                    + "missing models, please make sure you install the custom model kit.\n";
	}
	
	if( isDropbox )  {
	
				int index = URL_PREFIX.indexOf("technologyconnect");
		
				String base_url = URL_PREFIX.substring(0,index + 18);
		        
		        emailHeader +=   "\n"
		                       + "Dropbox Options \n"  
							   + " Please note that there might be a small delay in the order being available from the dropbox. \n" 
							   + " If the order is not ready when you attempt to access it, please try a little later.  Thank you. \n"							   
                               + " \n" 
                               + " Delivery Method: Download from Web-based Dropbox \n" 
							   + " To launch the Web based dropbox tool, select this URL: \n" 
                               + " " + base_url + "EdesignServicesServlet.wss?op=7&sc=webox:op:i:p:1418567 \n" 
                               + " \n"
                               + " Delivery Method: Download from GUI Dropbox \n" 
							   + " To launch the Dropbox GUI download tool, select this URL: \n" 
							   + " " + base_url + "EdesignServicesServlet.wss?op=7 \n" 
							   + " \n"
							   + " Delivery Method: Download from SFTP or Dropboxftp Dropbox \n" 
							   + " Alternatively, you may choose to access your Dropbox account using \n"
							   + " the sftp or dropboxftp command line tools: \n"
							   + "          sftp hkrauss@us.ibm.com@dropbox.chips.ibm.com \n"
							   + "          - or -  \n"
							   + "	        dropboxftp hkrauss@us.ibm.com@edesign.chips.ibm.com \n";
	}
	

        emailFooter
                    = "\n"
                    + "-----------------------------------------------------------------\n"
		    + "This order will expire on " + expirationDate + "\n"
                    + "-----------------------------------------------------------------\n"
		    + "Any software or other materials downloaded from the IBM Customer \n"
		    + "Connect website are IBM Confidential Information. Your treatment \n"
		    + "and use of this software and materials is subject to both the \n"
                    + "Confidential Disclosure Agreement and the Design Kit License \n"
                    + "Agreement between you and IBM.\n"
                    + "-----------------------------------------------------------------\n"
                    + "\n\n"
                    + "HOW TO PICK UP YOUR ORDER:\n"
                    + "\n"
                    + "You will need your Customer Connect ID and Password to access your order.\n"
                    + "\n"
                    + "If the web address above is highlighted, click on it. You will \n"
                    + "be taken to a server where you can download your order.\n"
                    + "\n"
                    + "If the web address above is not highlighted, follow these steps:\n"
                    + "   1. Open a web browser window.\n"
                    + "   2. Copy and paste the entire web address into the \"location\"\n"
                    + "      or \"address\" bar of the browser.\n"
                    + "   3. Press enter.\n"
                    + "   4. Once you arrive at the web page, you can access your order.\n"
                    + "\n"
                    + "-----------------------------------------------------------------\n"
                    + "Delivered by ASIC Connect\n"
                    + "Please visit http://www.ibm.com/technologyconnect for more information.\n";

/*
}

else {  // Inbox notification - following email goes only to FSE

emailHeader
= "Dear IBM ASIC support engineer,\n\n"
+ "A " + OrderProcessor.orderNames[orderType] + " order (# " + orderNum + ") was placed by " + this.edgeUserid + " (" + this.usersEmail + ") The details of this order are as follows: \n";

emailFooter
= "\n"
+ "-----------------------------------------------------------------\n"
+ "This order will expire on " + expirationDate + "\n"
+ "-----------------------------------------------------------------\n"
+ "Any software or other materials downloaded from the IBM Customer \n"
+ "Connect website are IBM Confidential Information. Your treatment \n"
+ "and use of this software and materials is subject to both the \n"
+ "Confidential Disclosure Agreement and the Design Kit License \n"
+ "Agreement between you and IBM.\n"
+ "-----------------------------------------------------------------\n"
+ "Delivered by ASIC Connect\n"
+ "Please visit http://www.ibm.com/technologyconnect for more information.\n";

}
*/



        body = "";


        if(orderType == DESIGN) {

                int index = numFiles - 1;

               //new for 4.4.1
                if(this.tkFix) {
                   for(int i = 0; i < numTkFix ; i++) {

                        String temp = abbFileNames[index];
                        int ind1 = temp.indexOf("_", 20);
                        int ind2 = temp.lastIndexOf(".");
                        temp = temp.substring(ind1+1, ind2);
                        String fileDesc = "Toolkit Fix for " + temp;

                        body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                        index--;

                    }
                }
                
                
               //end of new for 4.4.1

                if(this.baseModelKit) {

                    String fileDesc = "Base Model Kit for " + technology + " " + versionNo;

                    body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                    index--;

                }

               //new for 3.12.1

                if(this.majorAndDelta) {
                   String platforms = " (";
                   int i;
                   for(i = 0; i < platformsList.length - 1; i++)
                      platforms += platformsList[i] + ", ";
                   platforms += platformsList[i] + ")";
                   for(int j=0; j < this.numDR; j++){ //6.1 rel CSR10120
                      String fileDesc = "Delta Releases for ASIC Design Kit for " + technology + " " + versionNo + " " + platforms;

                      body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
                      index--;
                   }

                }
               //end of new for 3.12.1

                if(this.customModelKit) {

		    String platforms = " (";
		    int i;
		    for(i = 0; i < platformsList.length - 1; i++)
			platforms += platformsList[i] + ", ";
		    platforms += platformsList[i] + ")";

                    String fileDesc = "Custom Model Kit for " + technology + " " + versionNo + " " + platforms;

		    for(int j=0; j < this.numCustomModelKits; j++) {
                        body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
                        index--;
		    }

                }
                
			print(" in mqSendReady().  entering getEmailFileEntry[] for tkAddlComponent ", V_IMP);  
			if(this.tkAddlComponent) {

				String platforms = " (";
				int i;
				for(i = 0; i < platformsList.length - 1; i++)
				platforms += platformsList[i] + ", ";
				platforms += platformsList[i] + ")";

				String fileDesc = "Additional ToolKit Component for " + technology + " " + versionNo + " " + platforms;
				print(" in mqSendReady().  for tkAddlComponent fileDesc " + fileDesc, V_IMP);
				for(int t = 0; t < numTkAddlComponent; t++) 
				{
					body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
					index--;
				}
				print(" in mqSendReady().  after adding getEmailFileEntry tkAddlComponent ", V_IMP); 
			}
                
			   print(" in mqSendReady().  exiting getEmailFileEntry tkAddlComponent ", V_IMP);
                
                int numToolKits = index + 1;

                for(index = 0; index < numToolKits; index++) {

                    String fileDesc = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[index];

                    body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                }

        }

       //new for 5.1.1
        else if (orderType == IPDESIGN) {
          int index = numFiles-1;
         //new for 5.4.1
           if(this.tkFix) {
              for(int i = 0; i < numTkFix ; i++) {

                 String temp = abbFileNames[index];
                 int ind1 = temp.indexOf("_", 20);
                 int ind2 = temp.lastIndexOf(".");
                 temp = temp.substring(ind1+1, ind2);
                 String fileDesc = "Toolkit Fix for " + temp;

                 body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                 index--;

              }
           }
           
		   if(this.tkAddlComponent) {

			   String fileDesc = "Additional ToolKit Component for " + technology + " " + versionNo;
			   print(" in mqSendReady().  for IPDK tkAddlComponent fileDesc " + fileDesc, V_IMP);
			   for(int t = 0; t < numTkAddlComponent; t++) 
			   {
				   body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
				   index--;
			   }
			   print(" in mqSendReady().  for IPDK after adding getEmailFileEntry tkAddlComponent ", V_IMP); 
		   }
           
           
           
           
          // int numToolKits = index + 1;
           if(this.toolKit){
              for(int k = 0; k < this.platformsList.length; k++) {

                 String fileDesc = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[k];

                 body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
                 index--;

              }
           }
          //end of new for 5.4.1
          if (index == 0){
             int j = 0;
             String platforms = " (";
             for(j = 0; j < platformsList.length-1; j++)
                platforms += platformsList[j] + ", ";
             platforms += platformsList[j] + ")";

             String fileDesc = "IP(s) for " + technology + " " + versionNo + platforms;

             body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
           }
        }
       //end of new for 5.1.1
        else if(orderType == PREVIEW) {

                    int index = 0;

                    String fileDesc = "Technology Preview Kit for " + technology + " " + versionNo;

                    body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

        }

        else if(orderType == DELTA) {

            String platforms = " (";
            int i;
            for(i = 0; i < platformsList.length - 1; i++)
                platforms += platformsList[i] + ", ";
            platforms += platformsList[i] + ")";

            String fileDesc = "Delta Releases for ASIC Design Kit for " + technology + " " + versionNo + platforms;

            if(individualDeltas) {

                        body
                            = "\n"
                            + "Important: Delta Releases must be installed in the order they were released\n"
                            + "                  (in the same order as they appear below)\n";


                        for(int index = 0; index < numFiles; index++) {

                            body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index], patchDates[index]);

                        }
            }
            else {

                        body
                            = "\n"
                            + "Important: Delta Releases must be installed in the order they appear below\n";


                        for(int index = 0; index < numFiles; index++) {

                            body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                        }

            }

        }


        else if(orderType == FULLPD) {

                    for(int index = 0; index < numFiles; index++) {

                        String fileDesc = "Toolkit Fix for " + platformsList[index];

                        body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                    }

        }


        else if(orderType == DSE) {

                    for(int index = 0; index < numFiles; index++) {

                        String fileDesc = "DieSizer Tool for " + platformsList[index];

                        body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                    }

        }


        else if(orderType == XMX) {

                    for(int index = 0; index < numFiles; index++) {

                        String fileDesc = "X Collaborative Tool for " + platformsList[index];

                        body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                    }

        }



        else if(orderType == MEM) {

                    int index = 0;

                    String fileDesc = "Compilable Order for " + technology + " " + versionNo;

                    body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

        }

       //new for 4.5.1
        else if (orderType == EFPGA){
           int index = 0;

           String fileDesc = "EFPGA Order for " + technology + " " + versionNo;

           body += getEmailFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
        }
       //end of new for 4.5.1

        else
            handleException("Order type: " + orderType + " not handled in mqSendReady()");



	emailBody = body;

        output.append("MESSAGE = " + emailHeader + emailBody + emailFooter + "^");





        // now build the Inbox portion

	String cdNote = "";
	if(this.cd) {
		cdNote =
		      "This order was requested on CD. The CD will be shipped to the \n"
		    + "primary order recipient. In addition, we are providing this \n"
		    + "order to you by means of download for your convenience. <br /><br />\n";
	}

        if(this.edgeUserid.equals(this.orderBy)) {
            header
                = getJavascriptLaunchCode()
                + "\n<table><tr><td>\n"
                + "<strong>Your " + OrderProcessor.orderNames[orderType] + " order # " + orderNum + " is ready for download.</strong>\n"
                + "<br /><br />\n"
		+ cdNote;
	}
        else {  // when FSE orders on behalf of customer

            String fseName = getValue("FSE_NAME_1");
            if(fseName.length() != 0)
                fseName = "(" + fseName + ") ";

            header
                = getJavascriptLaunchCode()
                + "\n<table><tr><td>\n"
                + "<strong>A " + OrderProcessor.orderNames[orderType] + " order # " + orderNum + " placed on your behalf by your IBM ASIC support engineer " + fseName + "is ready for download.</strong>\n"
                + "<br /><br />\n"
		+ cdNote;
	}


		 header +=   "You can use any of the following methods to pick up your order.<br />"
						+ "•   Download from Browser: allows you to download files using your browser." + " <br />  " ; 


			if(useSmartDownload) {
			      
			  header +=  "•   The Smart Download from Browser:  uses a Java application, installed on your computer, " 
									+ "to provide a more robust download where you can recover from a dropped network connection.  "
									+ " <br />";                 
			}
			        
			if(isDropbox) {
			      
			   header +=  "•   Dropbox Tools: these place the order in your Customer Connect dropbox.  " 
								 + " You can choose to use the GUI dropbox, the web-based dropbox, or the sftp dropbox. " 
								 + " <br /> <br />\n";                 
			}
			        
			
			if(this.customModelKit && orderType != IPDESIGN ) {
				header +=
								  "\n"
								+ "In an initiative to reduce the size of your design kit, we have \n"
						+ "only included platform-specific models relevant to you. As a result, \n"
								+ "all platform-specific models (NDRs, OLA and smart models) will now \n"
								+ "be contained in the custom model kit. To avoid any problems due to \n"
								+ "missing models, please make sure you install the custom model kit. <br /> <br />\n";
				}
				
			if( isDropbox )  {
				
				int index = URL_PREFIX.indexOf("technologyconnect");

				String base_url = URL_PREFIX.substring(0,index + 18);
				
				header +=   "\n"
										   + "Dropbox Options <br />"  
										   + " Please note that there might be a small delay in the order being available from the dropbox.  <br /> " 
										   + " If the order is not ready when you attempt to access it, please try a little later.  Thank you.  <br /> <br />\n"							   										   
										   + " Delivery Method: Download from Web-based Dropbox  <br /> " 
										   + " To launch the Web based dropbox tool, select this URL:  <br /> " 
										   + " <a href=\"" + base_url + "EdesignServicesServlet.wss?op=7&sc=webox:op:i:p:1418567\">" + base_url + "EdesignServicesServlet.wss?op=7&sc=webox:op:i:p:1418567</a>  <br /> " 										  
										   + " Delivery Method: Download from GUI Dropbox  <br />" 
										   + " To launch the Dropbox GUI download tool, select this URL: <br /> " 
										   + " <a href=\"" + base_url + "EdesignServicesServlet.wss?op=7\">" + base_url + "EdesignServicesServlet.wss?op=7</a>  <br /> " 
										   + " <br /> <br />\n"
										   + " Delivery Method: Download from SFTP or Dropboxftp Dropbox  <br /> " 
										   + " Alternatively, you may choose to access your Dropbox account using  <br /> "
										   + " the sftp or dropboxftp command line tools:  <br /> "
										   + "          sftp " + this.usersEmail + "@dropbox.chips.ibm.com  <br /> "
										   + "          - or -   <br /> "
										   + "	        dropboxftp " + this.usersEmail + "@edesign.chips.ibm.com  <br /> <br />\n";
				}
			
        footer
            = "<hr />\n"

            + "<strong><span style=\"color: red\">\n"
            + "   This order will expire on " + expirationDate + "\n"
            + "</span></strong>\n"

            + "<hr />\n"

            + "<em>\n"
            + "   Any software or other materials downloaded from the IBM Customer Connect website are IBM Confidential Information. Your treatment and use of this software and materials is subject to both the Confidential Disclosure Agreement and the Design Kit License Agreement between you and IBM.\n"
            + "</em>\n"
            + "</td></tr></table>\n\n\n";



        body = "";

        if(orderType == DESIGN) {

            body = "\n<ul>\n";

            int index = numFiles - 1;

            //new for 4.4.1
            if(this.tkFix) {
               for(int i = 0; i < numTkFix ; i++) {

                    String fileDesc = "Toolkit Fix for " + platformsList[i];

                    body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                    index--;

                }
            }
           //end of new for 4.4.1

            if(this.baseModelKit) {

                String fileDesc = "Base Model Kit for " + technology + " " + versionNo;

                body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                index--;

            }

           //new for 3.12.1

             if(this.majorAndDelta) {
                   String platforms = " (";
                   int i;
                   for(i = 0; i < platformsList.length - 1; i++)
                      platforms += platformsList[i] + ", ";
                   platforms += platformsList[i] + ")";
                   for(int j=0; j < this.numDR; j++){ //6.1 rel CSR10120
                      String fileDesc = "Delta Releases for ASIC Design Kit for " + technology + " " + versionNo + platforms;

                      body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
                      index--;
                   }

                }
           //end of new for 3.12.1


            if(customModelKit) {

                String platforms = " (";
                int i;
                for(i = 0; i < platformsList.length - 1; i++)
                    platforms += platformsList[i] + ", ";
                platforms += platformsList[i] + ")";

                String fileDesc = "Custom Model Kit for " + technology + " " + versionNo + platforms;

				for(int j=0; j < this.numCustomModelKits; j++) {
		                    body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
		                    index--;
				}
		
            }
			
			
			print(" in mqSendReady().  entering getInboxFileEntry[] for tkAddlComponent ", V_IMP);  
			if(this.tkAddlComponent) {

				String platforms = " (";
				int i;
				for(i = 0; i < platformsList.length - 1; i++)
				platforms += platformsList[i] + ", ";
				platforms += platformsList[i] + ")";

				String fileDesc = "Additional ToolKit Component for " + technology + " " + versionNo + platforms;
				print(" in mqSendReady().  for tkAddlComponent fileDesc " + fileDesc, V_IMP);
				for(int t = 0; t < numTkAddlComponent; t++) 
				{
					body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
					index--;
				}
				print(" in mqSendReady().  after adding getInboxFileEntry[] tkAddlComponent ", V_IMP); 
			}
                
			   print(" in mqSendReady().  exiting getInboxFileEntry[] tkAddlComponent ", V_IMP);
                


            int numToolKits = index + 1;

            for(index = 0; index < numToolKits; index++) {

                String fileDesc = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[index];

                body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

            }

            body += "\n</ul>\n";

        }


        else if(orderType == PREVIEW) {

            int index = 0;

            String fileDesc = "Technology Preview Kit for " + technology + " " + versionNo;

            body = "\n<ul>\n"
                 + getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index])
                 + "\n</ul>\n";

        }

       //new for 5.1.1
        else if(orderType == IPDESIGN) {

			int index = numFiles-1;
		   //new for 5.4.1
			 if(this.tkFix) {
				for(int i = 0; i < numTkFix ; i++) {

				   String temp = abbFileNames[index];
				   int ind1 = temp.indexOf("_", 20);
				   int ind2 = temp.lastIndexOf(".");
				   temp = temp.substring(ind1+1, ind2);
				   String fileDesc = "Toolkit Fix for " + temp;

				   body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

				   index--;

				}
			 }
			 
			if(this.tkAddlComponent) {

				String fileDesc = "Additional ToolKit Component for " + technology + " " + versionNo;
				print(" in mqSendReady()/getInboxFileEntry -  for IPDK tkAddlComponent fileDesc " + fileDesc, V_IMP);
				for(int t = 0; t < numTkAddlComponent; t++) 
				{
					body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
					index--;
				}
				print(" in mqSendReady().  for IPDK after adding getInboxFileEntry tkAddlComponent ", V_IMP); 
			}
           

			// int numToolKits = index + 1;
			 if(this.toolKit){
				for(int k = 0; k < this.platformsList.length; k++) {

				   String fileDesc = "Tool Kit for " + technology + " " + versionNo + " for " + platformsList[k];

				   body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
				   index--;

				}
			 }
			//end of new for 5.4.1
			if (index == 0){
			   int j = 0;
			   String platforms = " (";
			   for(j = 0; j < platformsList.length-1; j++)
				  platforms += platformsList[j] + ", ";
			   platforms += platformsList[j] + ")";

			   String fileDesc = "IP(s) for " + technology + " " + versionNo + platforms;

			   body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);
			 }

        }
       //end of new for 5.1.1
        else if(orderType == DELTA) {

            String platforms = " (";
            int i;
            for(i = 0; i < platformsList.length - 1; i++)
                platforms += platformsList[i] + ", ";
            platforms += platformsList[i] + ")";

            String fileDesc = "Delta Releases for ASIC Design Kit for " + technology + " " + versionNo + platforms;


            if(individualDeltas) {

                body
                    = "<br /><br />\n"
                    + "<strong>Important:</strong> Delta Releases must be installed in the order they were released<br />\n"
                    + "(in the same order as they appear below)\n"
                    + "<ol>\n";

                for(int index = 0; index < numFiles; index++) {

                    body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index], patchDates[index]);

                }

                body += "\n</ol>\n";

            }
            else {

                body
                    = "<br /><br />\n"
                    + "<strong>Important:</strong> Delta Releases must be installed in the order they appear below<br />\n"
                    + "<ol>\n";

                for(int index = 0; index < numFiles; index++) {

                    body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

                }

                body += "\n</ol>\n";

            }
        }

        else if(orderType == FULLPD) {

            body = "\n<ul>\n";

            for(int index = 0; index < numFiles; index++) {

                String fileDesc = "Toolkit Fix for " + platformsList[index];

                body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

            }

            body += "\n</ul>\n";

        }

        else if(orderType == DSE) {

            body = "\n<ul>\n";

            for(int index = 0; index < numFiles; index++) {

                String fileDesc = "DieSizer Tool for " + platformsList[index];

                body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

            }

            body += "\n</ul>\n";

        }


        else if(orderType == XMX) {

            body = "\n<ul>\n";

            for(int index = 0; index < numFiles; index++) {

                String fileDesc = "X Collaborative Tool for " + platformsList[index];

                body += getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index]);

            }

            body += "\n</ul>\n";

        }


        else if(orderType == MEM) {

            int index = 0;

            String fileDesc = "Compilable Order for " + technology + " " + versionNo;

            body = "\n<ul>\n"
                 + getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index])
                 + "\n</ul>\n";

        }


       //new for 4.5.1
        else if(orderType == EFPGA) {

            int index = 0;

            String fileDesc = "EFPGA Order for " + technology + " " + versionNo;

            body = "\n<ul>\n"
                 + getInboxFileEntry(fileDesc, fileIDs[index], abbFileNames[index], fileSizes[index], downloadTimes[index])
                 + "\n</ul>\n";

        }
       //end of new for 4.5.1

        else
            handleException("Order type: " + orderType + " not handled in mqSendReady()");



        output.append("MESSAGE_HTML = " + header + body + footer + "^");

        output.append("EXP_DATE = " + this.expirationTime + "^");

       //new for 4.2.1
       /*  if (DRpackets != null)
           output.append("DELTA_PACKETS = "+DRpackets + "^");
        else
        output.append("DELTA_PACKETS = "+"^");*/
       //end of new for 4.2.1
       //new for 6.1 fixpack CSR IBMCC0010204
        if (DRpackets !=null){
           try{
              print("Update packet history for "+edgeUserid+" "+orderNum+" with packet length "+ DRpackets.length(), V_IMP);
              updateOrderHistory(edgeUserid, orderNum, DRpackets);
           }
           catch (SQLException ex){
              print("Error: Stacktrace: \n" + getStackTrace(ex), V_IMP);
           }
           
        }
        output.append("DELTA_PACKETS = "+"^"); //don't sent delta packets via MQ
       //end of new for 6.1 fixpack


	if(isPrimaryCustomer) {
          if(SEND_MQ_OR_EMAIL.equalsIgnoreCase("mq") || SEND_MQ_OR_EMAIL.equalsIgnoreCase("both")) {
            try {
                print("Sending MQ with CorrId: EDQ3_DL_URL", V_IMP);
                print("Message length in bytes: " + output.length(), V_IMP);
	        flushDisplay();

                OrderProcessor.sendMQMessage("EDQ3_DL_URL", orderNum, output.toString(), true);
                print("Sent MQ", V_IMP);
            }
            catch(Throwable t) {
                handleException(t, "thrown sending MQ");
            }
            print("Message sent by MQ:\n" + output.toString(), NOT_IMP);
          }

		if(SEND_MQ_OR_EMAIL.equalsIgnoreCase("email") || SEND_MQ_OR_EMAIL.equalsIgnoreCase("both")) {

		  String from    = this.FROM_ID;
		  String to      = usersEmail;
		  String cc      = this.fseEmail;
		  String replyTo = this.REPLY_TO;
		  String subject = "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNum + " has been processed and is ready for download";

		  sendMail(from, to, cc, replyTo, subject, emailHeader + emailBody + emailFooter, true);
		}
	}
	else {  // not primary customer (additonal ship to)
			String from    = this.FROM_ID;
			String to      = getRequiredValue("EMAIL_" + addShipTo);
			String cc      = null;
			String replyTo = this.REPLY_TO;

			String subject = "Your " + OrderProcessor.orderNames[orderType] + " Order # " + orderNum + " has been processed and is ready for download";

			sendMail(from, to, cc, replyTo, subject, emailHeader + emailBody + emailFooter, true);
	}

        print("Exiting mqSendReady()...", NOT_IMP);

    }





    /**********************************************************************************************************************************************************************************************************************************/



    private void mqSendQueued(int code) {

        print("Starting mqSendQueued()...", V_IMP);

        if( ! isPrimaryCustomer ) {
            print("Not Primary Customer. Not sending Queued MQ", V_IMP);
            return;
        }

        String mqOutput
            = "EDGE_USERID = " + this.edgeUserid + "^"
            + "ORDER_NUMBER = " + this.orderNumber + "^";


        try {
            print("Sending MQ with CORR ID: EDQ3_ORDER_QUEUE" , V_IMP);
            print("Message length in bytes: " + mqOutput.length(), V_IMP);
            flushDisplay();
            OrderProcessor.sendMQMessage("EDQ3_ORDER_QUEUE", this.orderNumber, mqOutput, true);
            print("Sent MQ", V_IMP);
        }
        catch(Throwable t) {
            handleException(t, "thrown sending mq");
        }

        print("Message sent by MQ:\n" + mqOutput, NOT_IMP);




        String body = "";

        if(code == 1) {

            String shippingSTOPPED = EDESIGN_HOME_DIR + "ASICTECH/" + this.technology + "/" + this.versionNo + "/" + "PreviewKit/shipping.STOPPED";

            if(new File(shippingSTOPPED).isFile())
                body = readFile(shippingSTOPPED, 1024);
            else
                handleException("shipit returned an exit value of 1 but " + shippingSTOPPED + " does not exist");
        }
        else if(code == 2 || code == 0) { // for Design Kit (rc=2) or for RAM/RA
            body
                = "Dear ASIC Connect customer, \n"
                + "\n"
                + "Your " + OrderProcessor.orderNames[this.orderType] + " Order # " + this.orderNumber + " for " + getRequiredValue("TECHNOLOGY") + " " + this.versionNo + " has \n"
                + "been received and is being processed. \n"
                + "\n"
                + "Please be aware that there may be a delay of 24 to 48 hours \n"
                + "in filling your request, due to technical difficulties. If you \n"
                + "have any questions, please contact the IBM Customer Connect Help Desk \n"
                + "by email at " + this.REPLY_TO + " or by phone at 1-888-220-3343. \n"
                + "\n"
                + "We apologize for the inconvenience. \n"
                + "\n"
                + "Thank you, \n"
                + "ASIC Connect\n";
        }
        else if(code == 3) { // for Foundry SRAM
            body
                = "Dear Foundry Connect customer, \n"
                + "\n"
                + "Your " + OrderProcessor.orderNames[this.orderType] + " Order # " + this.orderNumber + " for " + getRequiredValue("TECHNOLOGY") + " " + this.versionNo + " has \n"
                + "been received and is being processed. \n"
                + "\n"
                + "Please be aware that there may be a delay of 24 to 48 hours \n"
                + "in filling your request, due to technical difficulties. If you \n"
                + "have any questions, please contact the IBM Customer Connect Help Desk \n"
                + "by email at " + this.REPLY_TO + " or by phone at 1-888-220-3343. \n"
                + "\n"
                + "We apologize for the inconvenience. \n"
                + "\n"
                + "Thank you, \n"
                + "Foundry Connect\n";
        }
        else
            handleException("code: " + code + " not handled in mqSendQueued()");


        String from    = this.FROM_ID;
        String to      = this.usersEmail;
        String cc      = this.fseEmail;
        String replyTo = this.REPLY_TO;
        String subject = "The IBM Customer Connect: Delay processing " + OrderProcessor.orderNames[this.orderType] + " order # " + this.orderNumber;

        sendMail(from, to, cc, replyTo, subject, body, true);


        print("Exiting mqSendQueued()...", NOT_IMP);
    }



    /**********************************************************************************************************************************************************************************************************************************/




    private boolean mqSendError() {

        print("Starting mqSendError()...", V_IMP);

	if( ! isPrimaryCustomer ) {
	    print("Not Primary Customer. Not sending Error MQ", V_IMP);
	    return true;
	}

        if(orderType == DK_INIT || orderType == DR_INIT || orderType == IPDK_INIT || orderType == IPDK_INIT) {
            print("No message to send. Exiting mqSendError()...", NOT_IMP);
            return true;
	}
	else if(orderType != AREA_MEM && orderType != REPORT_MEM) {

            String waitFileName = MQHandler.CURRENT_ORDERS_DIR + orderNumber + ".retry";
            String failFileName = MQHandler.CURRENT_ORDERS_DIR + orderNumber + ".fail";
            String destFileName = MQHandler.RETRY_ORDERS_DIR + orderFileName;

            int waitDays = 2;

            print("Starting wait for file: " + waitFileName + " at " + new Date() + " for " + waitDays + " days", V_IMP);

            flushDisplay();

            long totalWaitTime = waitDays * DAYS;

            File waitFile = new File(waitFileName);
            File failFile = new File(failFileName);

            long waitTime = 0;

            int minutesWaited=60;
            while( ! waitFile.isFile() && ! failFile.isFile() && waitTime < totalWaitTime ) {
                try {
                    long sleepTime = SHIPIT_WAIT_TIME * 60 * 1000;
                    sleep(sleepTime);
                    waitTime += sleepTime;
		    minutesWaited += SHIPIT_WAIT_TIME;

           	    if(minutesWaited >= 60) {
                        print("waiting for file: " + waitFileName + " at " + new Date(), V_IMP);
                	flushDisplay();
                	minutesWaited = 0;
            	    }

                }
                catch(InterruptedException ie) {
                    handleException(ie, "Wait for " + waitFileName + " to appear was interrupted");
                }
            }

            if(waitFile.isFile()) {
                File sourceFile = new File(MQHandler.CURRENT_ORDERS_DIR + orderFileName);
                File destFile = new File(destFileName);
                waitFile.delete();
                print("Exiting current thread at " + new Date() + ". But we will try again!", V_IMP);
                flushDisplay();
                sourceFile.renameTo(destFile);
                return false;
            }
            else if(failFile.isFile()) {
                failFile.delete();
            }
	}


        if(orderType == AREA_MEM) {

            try {
                String message = "Sorry, this order could not be successfully completed";
                print("Sending MQ with CorrId: EDQ4_AREA_MEM_ERR and messageId: " + this.orderNumber, V_IMP);
                print("Message length in bytes: " + message.length(), V_IMP);
                flushDisplay();
                OrderProcessor.sendMQMessage("EDQ4_AREA_MEM_ERR", this.orderNumber, message, transientMsgPersistent);   // orderNumber is actually messageId
                print("Sent MQ", V_IMP);
            }
            catch(Throwable t) {
                handleException(t, "thrown sending MQ");
            }

            print("Message sent by MQ:\n" + message, NOT_IMP);
            print("Exiting mqSendError()...", NOT_IMP);

            return true;

        }
        else if(orderType == REPORT_MEM) {

            try {
                String message = "Sorry, this order could not be successfully completed";
                print("Sending MQ with CorrId: EDQ4_REPORT_MEM_ERR and messageId: " + this.orderNumber, V_IMP);
                print("Message length in bytes: " + message.length(), V_IMP);
                flushDisplay();
                OrderProcessor.sendMQMessage("EDQ4_REPORT_MEM_ERR", this.orderNumber, message, transientMsgPersistent);   // orderNumber is actually messageId
                print("Sent MQ", V_IMP);
            }
            catch(Throwable t) {
                handleException(t, "thrown sending MQ");
            }

            print("Message sent by MQ:\n" + message, NOT_IMP);
            print("Exiting mqSendError()...", NOT_IMP);

            return true;

        }



        StringBuffer output = new StringBuffer();

        output.append("EDGE_USERID = " + this.edgeUserid + "^");

        output.append("ORDER_NUMBER = " + this.orderNumber + "^");

        output.append("PREFERENCE = " + this.notification + "^");

        output.append("FROM = " + this.FROM_ID + "^");

        output.append("REPLYTO = " + this.REPLY_TO + "^");

        output.append("CC_LIST = " + this.fseEmail + "^");

        output.append("SUBJECT = The IBM Customer Connect: " + OrderProcessor.orderNames[this.orderType] + " order Number: " + this.orderNumber + " could not be completed^");

        String body
            = "Dear ASIC Connect customer,\n"
            + "\n"
            + "Your " + OrderProcessor.orderNames[this.orderType] + " order number: " + this.orderNumber + " could not be completed \n"
            + "due to technical difficulties.\n"
            + "\n"
            + "Please resubmit your order at IBM Customer Connect. \n"
            + "If you have any questions, please contact the IBM Customer Connect Help Desk \n"
            + "by email at " + this.REPLY_TO + " or by phone at 1-888-220-3343. \n"
            + "We apologize for the inconvenience\n"
            + "\n"
            + "Thank you,\n"
            + "ASIC Connect\n";



        /*
          if(this.notification.equalsIgnoreCase("inbox")) {
          StringBuffer htmlContent = new StringBuffer();
          StringTokenizer st = new StringTokenizer(body, "\n", true);
          while(st.hasMoreTokens()) {
          String token = st.nextToken();
          if(token.equals("\n"))
          htmlContent.append("<br />\n");
          else
          htmlContent.append(token);
          }

          body
          = "\n<table><tr><td>\n"
          + htmlContent.toString() + "\n"
          + "</td></tr></table>\n";
          }
        */


        output.append("MESSAGE = " + body + "^");


        if(SEND_MQ_OR_EMAIL.equalsIgnoreCase("mq") || SEND_MQ_OR_EMAIL.equalsIgnoreCase("both")) {
            try {
                print("Sending MQ with CORR ID: EDQ3_ORDR_ERR" , V_IMP);
                print("Message length in bytes: " + output.length(), V_IMP);
                flushDisplay();
                OrderProcessor.sendMQMessage("EDQ3_ORDR_ERR", this.orderNumber, output.toString(),true);
                print("Sent MQ", V_IMP);
            }
            catch(Throwable t) {
                handleException(t, "thrown sending mq");
            }
            print("Message sent by MQ:\n" + output.toString(), NOT_IMP);
        }



        if(SEND_MQ_OR_EMAIL.equalsIgnoreCase("email") || SEND_MQ_OR_EMAIL.equalsIgnoreCase("both")) {

            String from    = this.FROM_ID;
            String to      = usersEmail;
            String cc      = this.fseEmail;
            String replyTo = this.REPLY_TO;
            String subject = "The IBM Customer Connect: Order Number: " + this.orderNumber + " could not be completed";

            sendMail(from, to, cc, replyTo, subject, body, true);
        }



        print("Exiting mqSendError()...", NOT_IMP);

        return true;

    }

    void handleEmptyOrder(){

       print("handleEmptyOrder ", V_IMP);
       String subject = "ASIC "+ OrderProcessor.orderNames[orderType]+" order "+orderNumber +" has nothing new to deliver";

       String msg = "Dear IBM Customer Connect Customer,\n\n";
       msg += "The ASIC "
                    +OrderProcessor.orderNames[orderType]
                    + " Order ("
                    + this.orderNumber
                    + ") for "
                    + this.technology
                    + this.versionNo
                    + " has been submitted by \n"
                    + firstName
                    + " "
                    + lastName
                    + " of "
                    + usersCompany
                    + " to support the "
                    + customerProjname
                    + " project.\n\n"
                    + "For the Model Kit Components you selected, there have been no updates since the last order for this project. Therefore, there is nothing to deliver for this order.\n\n"
                    + "If you would like to receive exactly what was delivered previously, you can reorder and checkthe box to 'Include all revisions I have previously received for my Model Kit selections' near the bottom of the order page.\n\n"
                    +"We apologize for any inconvenience.\n\n"
          +"IBM Customer Connect";

       sendMail(this.FROM_ID, usersEmail , getCcList(), null, subject, msg, false);
       throw new HandledException();
}

   void updateOrderHistory(String edgeId, String orderNo, String packets)throws SQLException {
      
      Connection conn = null;
      Statement sqlStatement = null;
      
      ResultSet rs = null;
      PreparedStatement pstmt = null;
      
      String db2User, URL, password, jdbcDriverClassName, db2SchemaName;
         try {
            db2User = getProperty("ed.Db.userid");
            password = getProperty("ed.Db.password");
            jdbcDriverClassName = getProperty("ed.Db.Driver");
            URL = getProperty("ed.Db.URL");
            print(db2User+" "+jdbcDriverClassName+" "+URL+" ", V_IMP);
	    Driver driver = (Driver) Class.forName(jdbcDriverClassName).newInstance();
	    DriverManager.registerDriver(driver);
	    if (db2User == null) {
	
		conn = DriverManager.getConnection(URL);
	    }
	    else {
	
		conn = DriverManager.getConnection(URL, db2User, password);
	    }
	        
	        if(packets != null && packets.length() != 0 )
	        {
	        	StringReader clobData = new StringReader(packets);
	            StringBuffer sqlbuff = new StringBuffer();
	            sqlbuff.append("SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='"+edgeId+"'");
	            print(sqlbuff.toString(), V_IMP);
	            sqlStatement = conn.createStatement();
	            rs = sqlStatement.executeQuery(sqlbuff.toString());
	            
	            if (rs.next()){
	               edgeId = rs.getString("EDGE_USERID");
	            }
	            sqlbuff = new StringBuffer();
		            
			    sqlbuff.append("UPDATE EDESIGN.EDESIGN_ORDERS SET DELTA_PACKETS=? WHERE EDGE_USERID='"+ edgeId.trim() +"' AND ORDER_NUMBER='"+orderNo+"'");
		        print(sqlbuff.toString(), V_IMP);
	            pstmt = conn.prepareStatement(sqlbuff.toString());
		        pstmt.setCharacterStream(1, clobData, packets.length());
		        pstmt.executeUpdate();
	            rs.close();
	            sqlStatement.close();
	            pstmt.close();
                    conn.close();
	        }
	}

	catch (Throwable t) {
	    print("Error: Stacktrace: \n" + getStackTrace(t), V_IMP);
            handleException(t, "during update EDESIGN_ORDERS");
        }
       
   }
   
  //new for CSR IBMCC0010204
   String getPacketHistory(String tech, String ver, String projName, String asicCode)throws SQLException {//here
   
      Connection conn;
      Statement sqlStatement;
      
      ResultSet rs = null;
      String returnPackets="";
      String db2User, URL, password, jdbcDriverClassName, db2SchemaName;
      Vector vOrderedpackets = new Vector();
      try{ 
            db2User = getProperty("ed.Db.userid");
            password = getProperty("ed.Db.password");
            jdbcDriverClassName = getProperty("ed.Db.Driver");
            URL = getProperty("ed.Db.URL");
            
	    Driver driver = (Driver) Class.forName(jdbcDriverClassName).newInstance();
	    DriverManager.registerDriver(driver);
	    if (db2User == null) {
	
		conn = DriverManager.getConnection(URL);
	    }
	    else {
	
		conn = DriverManager.getConnection(URL, db2User, password);
	    }
	   
	    sqlStatement = conn.createStatement();
            
            StringBuffer sqlbuff = new StringBuffer();
           
            tech = getMktName(tech.trim());
           //begin
            sqlbuff.append("SELECT DELTA_PACKETS FROM EDESIGN.EDESIGN_ORDERS WHERE ORDER_TYPE IN (1,2,7,8,11) AND TECHNOLOGY='"+tech.trim()+"' AND VERSION_NO='"+ver.trim()+"' AND UCASE(CUSTOMER_PROJNAME)='"+projName.toUpperCase().trim()+"' AND UCASE(ASIC_CODENAME)='"+asicCode.toUpperCase().trim()+"' AND ORDER_IND !='N' AND ORDER_STATUS IN (3,6) FOR READ ONLY");
           //end
            
	    
            print(sqlbuff.toString(), V_IMP);
	    StringTokenizer tok;
            String item;
            
	    rs = sqlStatement.executeQuery(sqlbuff.toString());
            
            while (rs.next()){
               Clob lobData = rs.getClob("DELTA_PACKETS");
			   if( lobData == null || ( lobData != null && lobData.length() == 0) )
			    {
					 continue;
				}
               
               String packets = lobData.getSubString(1, (int)lobData.length());
               if (packets == null)
                  continue;
               tok = new StringTokenizer(packets, ",");
               while (tok.hasMoreTokens()){
                  item = tok.nextToken();
                  if (item !=null && !vOrderedpackets.contains(item))
                     vOrderedpackets.addElement(item);
               }
            }
           
            for (int k=0; k< vOrderedpackets.size(); k++){
                returnPackets = returnPackets + vOrderedpackets.elementAt (k) + ",";
            }
            rs.close();
            sqlStatement.close();
      }
   
   
      
	catch (Throwable t) {
	    print("Error: Stacktrace: \n" + getStackTrace(t), V_IMP);
            handleException(t, "during update EDESIGN_ORDERS");
        }
         return returnPackets;
   }
   
   private String getMktName(String technology) {

       
        Hashtable techMktName = new Hashtable();
        
        techMktName.put("CU11", "Cu-11");
	techMktName.put("CU08", "Cu-08");
	techMktName.put("SA27E", "SA-27E");
        techMktName.put("SA12E", "SA-12E");
	techMktName.put("SA27", "SA-27");
	techMktName.put("SA12", "SA-12");      
	techMktName.put("CU65LP", "Cu-65LP");
	techMktName.put("CU65HP", "Cu-65HP");
        
        if(technology != null)
           return (String)techMktName.get(technology);
        else
           return "ERROR";
	
        

    }
    
	private void completeDropboxUpload(String[] sourceFiles)
	{
		DropboxAccess dropbox = null;
		SessionHelper sessionHelper = null;
		String uploadStatus = "";
		//There should only be one package because the sourceFiles are for one order
		PackageInfo p = null;
		String errorMsg = "";
		try
		{
				
			print("Entering completeDropboxUpload for order number " + this.orderNumber, V_IMP);
            // To workaround the cluster cacert mismatch for VPN between BTV GZ and BCO GZ
			oem.edge.ed.odc.dropbox.service.helper.HttpConnection.setAllowHostnameMismatch(true); 
			if(sourceFiles == null || (sourceFiles != null && sourceFiles.length < 1 ) )
			{
				//this scenario is not possible
				//throw exception that files cannot be uploaded.
				//log exception			
				//send email?	
				String mailText = "No source files to upload to Dropbox for " + this.orderNumber + " .";
				print(mailText, V_IMP);
				mail(DROPBOX_ERR_MAIL, mailText);
				mail(UNEXP_WARNING, mailText);
				uploadStatus = "NO FILES";
			}
			else
			{
					dropbox = getDropboxService();
					 
	     			HashMap sessionMap = null;
					String dropboxToken = getToken();
					if( dropboxToken != null && !dropboxToken.equals("") )
					{
						print("Got token from SearchEtc for functionalId swdfunc@us.ibm.com ", V_IMP);
						sessionMap = dropbox.createSession(dropboxToken);
						sessionHelper = new SessionHelper(dropbox, sessionMap);
					}
				    int retryCount = 0;
					while(sessionHelper == null && retryCount < 4 )
					{
						print("Dropbox service is unavailable", V_IMP);
 						print("Retry getDropboxService().  retryCount = " + retryCount, V_IMP);
						 if( dropboxToken != null )
						{
							print("Got token from SearchEtc for functionalId swdfunc@us.ibm.com ", V_IMP);
							sessionMap = dropbox.createSession(dropboxToken);
							sessionHelper = new SessionHelper(dropbox, sessionMap);
						}						
						retryCount++;
					}//while sessionHelper is null 
						
					if(sessionHelper == null )
					{
						String mailText = "Dropbox Service is unavailable.";
						mail(DROPBOX_ERR_MAIL, mailText);
						mail(UNEXP_WARNING);
						print("Dropbox Service Unavailable ", V_IMP);
						uploadStatus = "NO SERVICE";
						return;
					} 

				    sessionHelper.setProxyDebug(true);					
					//Check if this package has already been created or exists
					//CAUTION: - This Vector could be huge in size.  Look at ways to make it faster.
				    Vector vec = dropbox.queryPackages(this.orderNumber, true, true,false,false,false);							 					
					Iterator it = vec.iterator();					
					while(it.hasNext()) {
					 	p = (PackageInfo)it.next();
						if(p.getPackageName().equalsIgnoreCase( this.orderNumber ) && ( p.getPackageStatus() == DropboxAccess.STATUS_FAIL || p.getPackageStatus() == DropboxAccess.STATUS_INCOMPLETE ) )
						{
							//Delete this package because this package already exists
							//Delete the package because the upload maybe incomplete or u want to upload this one
							print("Package already exists in dropbox.  So delete it.  Package Status = " + p.getPackageStatus() + " for order number " + this.orderNumber, V_IMP);
							dropbox.deletePackage(p.getPackageId() );
						}
					}				 
					
					//create dropbox package first and get a packageId
					long packageId = dropbox.createPackage(this.orderNumber);
					print("package_id = " + packageId + " created for order number " + this.orderNumber, V_IMP);
				    p = dropbox.queryPackage(packageId, true);
					boolean isUploadSucc = false;									
					
					List fileNamesList = new ArrayList();
					for( int i=0;i<sourceFiles.length;i++ )
					{
						//Create a File from filename.  Use this File obj to get a fileId
						File uploadFile = new File(sourceFiles[i]);
						if(uploadFile.isFile()) {
	
							print("processing canonicalFile path = " + uploadFile.getCanonicalPath(), V_IMP);
							File canonicalFile = new File(uploadFile.getCanonicalPath());
							String fileName = canonicalFile.getName();
							print("canonicalFile name = " + fileName, V_IMP);
							long fileSize = canonicalFile.length();
							print("canonicalFile size = " + fileSize, V_IMP);
							
							if( !fileNamesList.contains(fileName) )
							{
								fileNamesList.add(fileName);
							}
							else
							{
								
								if( canonicalFile.getCanonicalPath() != null && canonicalFile.getCanonicalPath().indexOf("DieSizer") > -1 )
								{																	
									int indexOfDieSizer = canonicalFile.getCanonicalPath().indexOf("DieSizer");
									fileName = canonicalFile.getCanonicalPath().substring(indexOfDieSizer);
								}
								else
								{
									fileName = this.orderNumber + "." + i + "." + fileName;	
								}
								System.out.println(" fileName = " + fileName);
	
							}
							    
							//Use the fileName from above to get fileId
							long fileId = dropbox.uploadFileToPackage(packageId,fileName, fileSize);
							print("fileId = " + fileId + " for fileName = " + fileName + " in packageId = " + packageId + " for orderNumber = " + this.orderNumber, V_IMP);
							UploadOperation uploadOp = new UploadOperation(dropbox,canonicalFile,packageId, fileId);
								
							uploadOp.process();//Start actual file transfer
							//print("intiated uploadOp.process()");
								
							uploadOp.waitForCompletion();
								
							if( !uploadOp.validate() )
							{																						
								uploadStatus = "FAIL";
								print("uploadOp validate() Error Message " + uploadOp.getErrorMessages() + " for fileName " + fileName + " for order number " + this.orderNumber, V_IMP);
								//remove package and retry								
								isUploadSucc = false;
								errorMsg = uploadOp.getErrorMessages() + " for fileName " + fileName + " for order number " + this.orderNumber;
								mail(UNEXP_ERROR, errorMsg); //Send error message email to user and developers
								break;
								//print("initiate remove package and send email that upload failed");
							}
							else
							{ 
								uploadStatus = "COMPLETE";
								print("uploadOp successful for fileName " + fileName + " for orderNumber " + this.orderNumber, V_IMP );
								isUploadSucc = true;  
							}
							flushDisplay();
						}//close if f.isFile()
					}// close for loop
				//UPLOAD_STATUS	VARCHAR(10)	‘COMPLETE’ or ‘FAIL’ or ‘PARTIAL’
				//updateDropboxUpload(int numFiles,String uploadStatus, String listOfUsers ) 
				if( isUploadSucc )
				{
					print("uploadOp successful for orderNumber " + this.orderNumber, V_IMP );					
					if( p != null )
					{
						p.setPackageSendNotification(true);
					} 
					dropbox.addUserAcl(packageId, this.edgeUserid);
					String ccList = getCcList();
					if( ccList != null && !ccList.equals("") )
					{
						StringTokenizer st = new StringTokenizer(ccList, ",");
						while( st.hasMoreTokens() )
						{							
							String userId = st.nextToken();
							print("Adding user to dropbox user acl " + userId, V_IMP );
							try
							{
							   dropbox.addUserAcl(packageId,userId );
							}
							catch(DboxException dboxE)
							{
								print("Failed to add user to dropbox user acl " + userId, V_IMP );
								dboxE.printStackTrace();
								continue;
							}
						}
					}
								
					dropbox.commitPackage(packageId);//commit only after all the files have been uploaded
					print("package committed for orderNumber " + this.orderNumber, V_IMP );
				}
				else
				{
					print("uploadOp failed for orderNumber " + this.orderNumber + " errorMsg = " + errorMsg, V_IMP );					
					dropbox.deletePackage(p.getPackageId() );
					String mailText = "  The dropbox upload failed for " + this.orderNumber + " due to an error caused by the Dropbox Service.";
					mail(DROPBOX_ERR_MAIL,mailText);				
				}	
					
					
					//If you get a SQLException or Exception while trying to insert into table, ignore it for now and continue
					try
					{
						updateDropboxUpload( sourceFiles.length ,uploadStatus, this.edgeUserid );
					}
					catch(SQLException sqlE)
					{
						print("Insert into EDESIGN.DROPBOX_UPLOAD failed for orderNumber " + this.orderNumber, V_IMP );
						sqlE.printStackTrace();
					}
					catch(Exception e)
					{
						print("Insert into EDESIGN.DROPBOX_UPLOAD failed for orderNumber " + this.orderNumber, V_IMP );
						e.printStackTrace();
					}
			}// source files is not null
			 
		}
		catch(DboxException dboxE)
		{			
			dboxE.printStackTrace();
			//Should this be done differently?
			//Remove the package for this order because there was an exception
			try
			{
				if( dropbox != null && p != null )
				{
					dropbox.deletePackage(p.getPackageId() );
				}	
				mail(DROPBOX_ERR_MAIL, "" );				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			String mailText = "  The dropbox upload failed for " + this.orderNumber + " due to an error caused by the Dropbox Service.";
			mail(DROPBOX_ERR_MAIL,mailText);
			handleException(dboxE, "Dropbox Exception thrown while completing dropbox upload");
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			String mailText = "  The dropbox upload failed for " + this.orderNumber + " due to an error caused by the Dropbox Service.";
			mail(DROPBOX_ERR_MAIL,mailText);
			handleException(e, "Exception thrown while completing dropbox upload");
		}
		finally
		{
			if(sessionHelper != null )
			{
				sessionHelper.cleanup();
			}
			print("Exiting completeDropboxUpload for order number " + this.orderNumber, V_IMP);	
			flushDisplay();
		}
	}
	
	
	private DropboxAccess getDropboxService()
	{
		try
		{
			print("DropboxService URL from OrderProcessor " + OrderProcessor.dropboxURL, V_IMP);	
			flushDisplay();
			
			ConnectionFactory fac = new HessianConnectFactory();
			fac.setTopURL(new URL(OrderProcessor.dropboxURL));
			DropboxAccess dropbox = fac.getProxy();
			return dropbox;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			handleException(e, "Exception thrown while getting a reference to Dropbox Service");
		}
		return null;
	}
	
	private String getToken() throws Exception
	{
		Hashtable ht = new Hashtable();
		ht.put("COMMAND","XFR");
		ht.put("EDGEID", "swdfunc@us.ibm.com");
		ht.put("COMPANY","IBM");
		ht.put("COUNTRY","US");
		
		int secs = 300;
		String cipherFile = "/web/server_root/data/technologyconnect/common/boulder.key";
		//public static ODCipherRSA loadCipherFile(String cipherFile)
		return SearchEtc.createToken(SearchEtc.loadCipherFile(cipherFile), ht,  secs);
	}
	
	
	
	private void updateDropboxUpload(int numFiles,String uploadStatus, String listOfUsers ) throws SQLException,Exception 
	{
		 Connection conn = null;
		 Statement sqlStatement = null;
		 String db2User, URL, password, jdbcDriverClassName, db2SchemaName;
		try {
			   db2User = getProperty("ed.Db.userid");
			   password = getProperty("ed.Db.password");
			   jdbcDriverClassName = getProperty("ed.Db.Driver");
			   URL = getProperty("ed.Db.URL");
			   print(db2User+" "+jdbcDriverClassName+" "+URL+" ", V_IMP);
		   Driver driver = (Driver) Class.forName(jdbcDriverClassName).newInstance();
		   DriverManager.registerDriver(driver);
		   if (db2User == null) {	
		   conn = DriverManager.getConnection(URL);
		   }
		   else {	
		   conn = DriverManager.getConnection(URL, db2User, password);
		   }
		   
		   /*
		    *       PACKAGE_NAME	VARCHAR(45)	NOT NULL
					NUM_FILES	INTEGER	
					SEND_DATE	TIMESTAMP	
					RETRY_TIMES	INTEGER	
					UPLOAD_STATUS	VARCHAR(10)	‘COMPLETE’ or ‘FAIL’ or ‘PARTIAL’
					RECEIVER	VARCHAR(1024)	
					LAST_UESRID	VARCHAR(81)	
					LAST_TIMESTAMP	TIMESTAMP						
		    * 
		    * 
		    */
		   if( this.orderNumber != null && listOfUsers != null )
		   {
			   StringBuffer sbc = new StringBuffer();
			   sbc.append("INSERT INTO EDESIGN.DROPBOX_UPLOAD VALUES('" + this.orderNumber + "'," + numFiles + ",current timestamp,0,'" + uploadStatus + "','" + listOfUsers + "',user,current timestamp)");
			   sqlStatement = conn.createStatement();
			   int num = sqlStatement.executeUpdate(sbc.toString());
			   print("Updated " + num + " rows in EDESIGN.DROPBOX_UPLOAD for orderNumber " + this.orderNumber,V_IMP );
		   }
		   else
		   {
		   		print("Nothing to insert into EDESIGN.DROPBOX_UPLOAD for orderNumber " + this.orderNumber, V_IMP);
		   }
	    }
	    catch (Throwable t) {
		   print("Error: Stacktrace: \n" + getStackTrace(t), V_IMP);
			   handleException(t, "during update EDESIGN_ORDERS");
		}
	    finally
		{
	    	
	    	if(sqlStatement != null)
	    	{
	    		sqlStatement.close();
	    	}
	    	
	    	if( conn != null )
	    	{
	    		conn.close();
	    	}
		}
		flushDisplay();
	  }

		
        //Get the Asic Technologies and see if this order's tech matches an ASIC Tech.
	    // If it does, then send out an email to ASIC Connect customer.
		private boolean isAsicTech(String technologyName)
		{
			try {
	            loadLRD();
	        }
	        catch(IOException e) {
	            handleException("thrown loading " + lastReleaseDataFile);
	        }

	        for(int i = 0; i < lastReleaseDataInfo.length; i++) {

	            if(lastReleaseDataInfo[i][4].equals(technologyName))
	               return true;

	        }
			
			return false;
		}
		
		
		//Get the Foundry Technologies and see if this order's tech matches a FoundryTech.
		// If it does, then send out an email to Foundry Connect customer.
		private boolean isFoundryTech(String technologyName)
		{
			Connection conn = null;
			Statement sqlStatement = null;	      
			ResultSet rs = null;			
			try
			{
				String query = "SELECT DISTINCT TECHNOLOGY FROM EFOUNDRY.TECHNOLOGY_MASTER with ur";
				conn = getConnection();
				sqlStatement = conn.createStatement();
				rs = sqlStatement.executeQuery(query);
				while ( rs.next() )
				{
					String temp = rs.getString("TECHNOLOGY");
					
					if( temp != null && temp.equalsIgnoreCase(technologyName) )
					{
						print(" isFoundryTech() is true. TECHNOLOGY from DB= " + temp + " and order tech= " + technologyName, V_IMP);
						flushDisplay();
						return true;
					}
				}
			}
			catch(Exception e)
			{
				print("Error: Stacktrace: \n" + getStackTrace(e), V_IMP);
				return false;
			}
			finally
			{
		    	if( rs != null )
		    	{
		    		try {
		    			rs.close();
					} catch (SQLException sqlx) {
					}
		    	}
		    	
		    	if(sqlStatement != null)
		    	{		    		
		    		try {
		    			sqlStatement.close();
					} catch (SQLException sqlx) {
					}
		    	}
		    	
		    	if( conn != null )
		    	{
		    		try {
						conn.close();
					} catch (SQLException sqlx) {
					}
		    	}
			}
			flushDisplay();
			return false;
		}
		
		private Connection getConnection()
		{  
			String db2User, URL, password, jdbcDriverClassName, db2SchemaName;
			Connection conn = null;;
			try {
				   db2User = getProperty("ed.Db.userid");
				   password = getProperty("ed.Db.password");
				   jdbcDriverClassName = getProperty("ed.Db.Driver");
				   URL = getProperty("ed.Db.URL");
				   print(db2User+" "+jdbcDriverClassName+" "+URL+" ", V_IMP);
			   Driver driver = (Driver) Class.forName(jdbcDriverClassName).newInstance();
			   DriverManager.registerDriver(driver);
			   if (db2User == null) {		
			   conn = DriverManager.getConnection(URL);
			   }
			   else {	
			   conn = DriverManager.getConnection(URL, db2User, password);
			   }
			}
			catch(Exception e)
			{
				print("Failed to get connection in getConnection " + getStackTrace(e), V_IMP);
				flushDisplay();
			}
			   return conn;
		}

}
    /**********************************************************************************************************************************************************************************************************************************/
