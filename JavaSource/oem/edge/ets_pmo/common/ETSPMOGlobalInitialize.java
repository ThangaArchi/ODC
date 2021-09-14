package oem.edge.ets_pmo.common;

import java.util.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.net.InetAddress;
import org.apache.log4j.Logger;
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSPMOGlobalInitialize {

	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(ETSPMOGlobalInitialize.class);

	private static java.util.ResourceBundle gwaprop = null;
	private static java.util.ResourceBundle prop = null;
	//private static java.util.ResourceBundle rtfprop  = null;
	//private static java.util.ResourceBundle databaseprop  = null;
	private static java.util.ResourceBundle logprop = null;
	private static java.util.ResourceBundle mailprop = null;
	private static java.util.ResourceBundle mqprop = null;
	private static java.util.ResourceBundle criprop = null;

	private static String serverEnv = "";
	private static boolean HandleCRI = false;

	private static int DebugValMax = -1;
	private static String dbName = null;
	private static String dbUser = null;
	private static String dbPwd = null;
	private static String driver = null;

	/*
	private static String RTFConverterFileName = null;
	private static String RTFConverterFileName_html = null;
	private static String RTFFileName = null;
	private static String TextFileName = null;
	
	*/
	private static String unknownUserId = null;

	private static String qFrom = null;
	private static String qReplyTo = null;
	private static String qTo = null;
	private static String q2To = null;
	private static String qManager = null;
	private static String qManagerReplyTo = null;
	private static String qHostName = null;
	private static String qChannelName = null;
	private static String qPort = null;
	private static String qNumRetries = null;
	private static String waitInterval = null;
	private static String sleepTime = null;
	private static String numRetries = null;
	private static String displayLevel = null;
	private static String nonFatalErrorCodes = null;

	private static String MQReceiveErrorLog = null;
	private static String MQSendErrorLog = null;
	private static String MQCOAsANDCODsLog = null;
	private static String XMLProjErrorLog = null;
	private static String XMLProjMsgLog = null;
	private static String MQSendProjAckNackErrorLog = null;

	private static int usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue =
		0;

	private static String TO = null;
	private static String TO1 = null;
	private static String TO2 = null;
	private static String RESOURCELIST_TO = null;

	private static String FROM = null;
	private static String ERROR_FROM = null;

	private static String CC = null;
	private static String RESOURCELIST_CC = null;

	private static String MailOnError = null;
	private static String mailHost1 = null;
	private static String mailHost2 = null;
	private static String mailHost3 = null;

	private static String PCR_NEWMailSubj = null;
	private static String PCR_UPDATEMailSubj = null;
	private static String PCRMailHeader = null;
	private static String PCRMailFooter = null;
	
	private static String Issue_NEWMailSubj	=	null;
	private static String Issue_UPDATEMailSubj = null;
	private static String IssueMailHeader = null;
	private static String IssueMailFooter = null;
	
	private static String ProjectSyncSubject = null;
	private static String ProjectSyncHeader = null;
	private static String ProjectSyncFooter = null;

	private static boolean loopingFlag = false;
	private static int sleepTimeBeforeTroublingMQ = -1;
	private static int sleepTimeWhenMQIsDown = -1;

	//PMOMQSend gets the file and sends to MQ. Only for test purposes
	private static String Test_ProjectXML_filename = null;

	//DOMParseXML get the file and parses it. Only for test purposes.
	// This is needed when we dont want to connect mq and xml parsing
	// and only to test xml parsing
	private static String TestProjectCreateUpdateXMLDir = null;

	private static int NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND = 1;

	private static String ProjectCreateUpdateXMLDir;
	private static String ProjectTmpDir;
	
	private static String SAX_ERROR_CODE;
	private static String SAX_WARNING_CODE;
	private static String SAX_FATALERROR_CODE;
	private static String MISSING_ID_CODE;
	private static String SUCCESS_CODE;

	//	private static String XMLVersion;

	private static String XMLCRIMsgLog;
	private static String XMLCRIErrorLog;

	private static String CR_CORR_ID;
	private static String PROJ_CORR_ID;
	private static String PROJ_ACK_CORR_ID;
	private static String PROJ_NACK_CORR_ID;
	private static String CR_ACK_CORR_ID;
	private static String CR_NACK_CORR_ID;
	private static String PROJ_ACK_ACK_CORR_ID;

	private static int CR_TIMEOUTWINDOW;
	private static int NoCRCreateElements;
	private static String CRCreateElements[];

	private static int NoCRUpdateElements;
	private static String CRUpdateElements[];
	
	private static int NoISSUECreateElements;
	private static String ISSUECreateElements[];

	private static int NoISSUEUpdateElements;
	private static String ISSUEUpdateElements[];

	private static String CRI_ROOT_USER_Id;

	private static String CRExceptionType;

	private static String CR_CREATEDINPMO_STATE;
	private static String CR_CREATEDNEW_STATE;
	private static String CR_UPDATED_STATE;
	private static String CR_ACKED_STATE;
	private static String CR_NACKEDCREATE_STATE;
	private static String CR_NACKEDUPDATE_STATE;
	private static String CR_TIMEOUT_STATE;
	private static String CR_CREATED_STATE_SENT;
	private static String CR_UPDATED_STATE_SENT;

	private static String CR_CREATEDINPMO_ETSID;
	private static int xmlValidation;

	private static Hashtable htChangeRequestRankRange;
	private static Hashtable htIssueRankRange;
	private static Hashtable htCRIRTF;
	private static Hashtable htISSUERTF;
	//private static Hashtable htStates;
	private static Hashtable htPMOtoETSChangeRequestStates;
	//private static Hashtable htETSPMOStates;
	private static Hashtable htETStoPMOChangeRequestStates;
	private static Hashtable htPMOtoETSIssueStates;
	//htETStoPMOIssueStates maps the STATE_ACTION field in ets.pmo_issue_info to PMOffice states.
	private static Hashtable htETStoPMOIssueStates;
	//htFrontEndETStoDaemonIssueStates maps STATE_ACTION to PROBLEM_STATE (both are fields in ets.pmo_issue_info). We need both of them to display Action and New State information for issues.
	private static Hashtable htFrontEndETStoDaemonIssueStates;
	//htPMOIssueStageIDRank maps STAGE_ID to RANK in PMOffice.
	private static Hashtable htPMOIssueStageIDRank;
	
	private static Hashtable htSDtypes;
	private static Hashtable htFDtypes;

	/*
	 * These tags are used in FileParser for sending a NACK when SAXParseException is encountered
	 */
	private static String transactionidStartTag;
	private static String transactionidFinishTag;
	private static String transactionVersionStartTag;
	private static String transactionVersionFinishTag;
	private static String sourceStartTag;
	private static String sourceFinishTag;
	private static String destinationStartTag;
	private static String destinationFinishTag;
	private static String appStartTag;
	private static String appFinishTag;
	private static String priortoProjectidStartTag;
	private static String projectidStartTag;
	private static String projectidFinishTag;
	private static String useridStartTag;
	private static String useridFinishTag;
	private static boolean isInited=false;
	private static String loopFlag;
	private static String environment;

	public ETSPMOGlobalInitialize() {

		Init();
	}

	/**
	* Save Bundle reader (if parametr not found result is empty string
	* Added by Valentin Korotky-Adamenko.
	* Creation date: (3.29.01 7:14:13 PM)
	* @return java.lang.String
	* @param rb java.util.ResourceBundle
	* @param param java.lang.String
	*/

	public static String getParam(ResourceBundle rb, String param) {
		String result = "";
		try {
			result = rb.getString(param);
			if (result != null) {
				result = result.trim();
			} else {
				logger.debug("Global: param=[" + param + "] init failed");
				return "";
			}
			if (result.length() == 0) {
				logger.debug("Global: param=[" + param + "] have zero length");
			}
			return result;
		} catch (MissingResourceException mre) {

			logger.error(
				"ets PROPERTY"
					+ "getParam"
					+ "Global: MissingResourceException param=["
					+ param
					+ "] "
					+ mre.getMessage());

			result = "";
		}
		return result;
	}

	/*
	 * Its the same method as above, I have a boolean MailInfoNecessary which is unnecessary. Basically
	 * this method will not send an annoying mail. This will be used in cases where we know that 
	 * certain property in the property file is not present and we are still looking for it. It will
	 * avoid annoying mails for those cases.
	 */
	public static String getParam(
		ResourceBundle rb,
		String param,
		boolean MailInfoNecessary) {
		String result = "";
		try {
			result = rb.getString(param);
			if (result != null) {
				result = result.trim();
			} else {
				logger.debug("Global: param=[" + param + "] init failed");
				return "";
			}
			if (result.length() == 0) {
				logger.debug("Global: param=[" + param + "] have zero length");
			}
			return result;
		} catch (MissingResourceException mre) {

			logger.debug(
				"ets PROPERTY"
					+ "getParam"
					+ "Global: MissingResourceException param=["
					+ param
					+ "] "
					+ mre.getMessage());

			result = "";
		}
		return result;
	}
	public static boolean Init() {
		if (isInited)
			return isInited;
		
		boolean success = true;
		StringBuffer strbuf = null;
		try {
			System.setProperty(
							"org.xml.sax.driver",
							"org.apache.xerces.parsers.SAXParser");
			/*
			 * gwa properties 
			 */
			gwaprop = ResourceBundle.getBundle("oem.edge.common.gwa");
			dbName = getParam(gwaprop, "gwa.mail_connect_string");
			dbUser = getParam(gwaprop, "gwa.db2usr");
			dbPwd = getParam(gwaprop, "gwa.db2pw");
			String flag = getParam(gwaprop, "gwa.driver");
			qHostName = getParam(gwaprop, "gwa.mqhost");
			qPort = getParam(gwaprop, "gwa.mqport");
			//serverEnv	=  getParam(gwaprop, "gwa.serverEnv");
			InetAddress inet = InetAddress.getLocalHost();
			serverEnv = inet.getHostName(); // + " " + inet.getHostAddress();
			/* It always goes in the else block no matter what i get as a parameter in 
			 * String flag = getParam(gwaprop, "gwa.driver");
			 */
			if (flag != null && flag.trim().equalsIgnoreCase("net")) {
				driver = "COM.ibm.db2.jdbc.net.DB2Driver";
			} else {
				driver = "COM.ibm.db2.jdbc.app.DB2Driver";
			}
			/*
			 *ets_pmo props 
			 */
			prop = ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo");
			ProjectTmpDir = getParam(prop, "ets_pmo.ProjectTmpDir");
			ProjectCreateUpdateXMLDir = getParam(prop, "ets_pmo.ProjectCreateUpdateXMLDir");
			SAX_ERROR_CODE = getParam(prop, "ets_pmo.SAX_ERROR_CODE");
			SAX_WARNING_CODE = getParam(prop, "ets_pmo.SAX_WARNING_CODE");
			SAX_FATALERROR_CODE = getParam(prop, "ets_pmo.SAX_FATALERROR_CODE");
			MISSING_ID_CODE = getParam(prop, "ets_pmo.MISSING_ID_CODE");
			SUCCESS_CODE = getParam(prop, "ets_pmo.SUCCESS_CODE");
			loopFlag = getParam(prop, "ets_pmo.looping");
			if (loopFlag.trim().equalsIgnoreCase("true")) {
				loopingFlag = true;
			}
			NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND =
				Integer.parseInt(
					getParam(
						prop,
						"ets_pmo.NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND"));
			sleepTimeBeforeTroublingMQ =
				Integer.parseInt(
					getParam(prop, "ets_pmo.sleepTimeBeforeTroublingMQ"));
			sleepTimeWhenMQIsDown =
				Integer.parseInt(
					getParam(prop, "ets_pmo.sleepTimeWhenMQIsDown"));
			Test_ProjectXML_filename =
				getParam(prop, "ets_pmo.TEST_PROJECT_XML_FROM_PMO");
			TestProjectCreateUpdateXMLDir =
				getParam(prop, "ets_pmo.TestProjectCreateUpdateXMLDir");
			unknownUserId = getParam(prop, "ets_pmo.unknownUserId");
			xmlValidation =
				Integer.parseInt(
					getParam(prop, "ets_pmo.xmlValidation").trim());

			htSDtypes = new Hashtable();
			int noSDTypes =
				Integer.parseInt(getParam(prop, "ets_pmo.SD.No").trim());
			for (int i = 0; i < noSDTypes; i++) {
				String SDKeyValuePair =
					getParam(prop, "ets_pmo.SD.DateType" + i);
				String key =
					SDKeyValuePair.substring(0, SDKeyValuePair.indexOf("$"));
				String value =
					SDKeyValuePair.substring(SDKeyValuePair.indexOf("$") + 1);
				htSDtypes.put(key, value);
			}
			htFDtypes = new Hashtable();
			int noFDTypes =
				Integer.parseInt(getParam(prop, "ets_pmo.FD.No").trim());
			for (int i = 0; i < noFDTypes; i++) {
				String FDKeyValuePair =
					getParam(prop, "ets_pmo.FD.DateType" + i);
				String key =
					FDKeyValuePair.substring(0, FDKeyValuePair.indexOf("$"));
				String value =
					FDKeyValuePair.substring(FDKeyValuePair.indexOf("$") + 1);
				htFDtypes.put(key, value);
			}

			transactionidStartTag =
				getParam(prop, "ets_pmo.FileParser.transactionidStartTag");
			transactionidFinishTag =
				getParam(prop, "ets_pmo.FileParser.transactionidFinishTag");
			transactionVersionStartTag =
				getParam(prop, "ets_pmo.FileParser.transactionVersionStartTag");
			transactionVersionFinishTag =
				getParam(
					prop,
					"ets_pmo.FileParser.transactionVersionFinishTag");
			sourceStartTag =
				getParam(prop, "ets_pmo.FileParser.sourceStartTag");
			sourceFinishTag =
				getParam(prop, "ets_pmo.FileParser.sourceFinishTag");
			destinationStartTag =
				getParam(prop, "ets_pmo.FileParser.destinationStartTag");
			destinationFinishTag =
				getParam(prop, "ets_pmo.FileParser.destinationFinishTag");
			appStartTag = getParam(prop, "ets_pmo.FileParser.appStartTag");
			appFinishTag = getParam(prop, "ets_pmo.FileParser.appFinishTag");
			priortoProjectidStartTag =
				getParam(prop, "ets_pmo.FileParser.priortoProjectidStartTag");
			projectidStartTag =
				getParam(prop, "ets_pmo.FileParser.projectidStartTag");
			projectidFinishTag =
				getParam(prop, "ets_pmo.FileParser.projectidFinishTag");
			useridStartTag =
				getParam(prop, "ets_pmo.FileParser.useridStartTag");
			useridFinishTag =
				getParam(prop, "ets_pmo.FileParser.useridFinishTag");
			/*
			 * rtf prop
			 */
			/* rtfprop	= ResourceBundle.getBundle(getPropertyFileLocation() + "ets_pmo_rtf");
			 
			 RTFConverterFileName		= getParam(rtfprop, "ets_pmo.RTFConverterFileName");
			 RTFConverterFileName_html	= getParam(rtfprop, "ets_pmo.RTFConverterFileName_html");
			 RTFFileName				= getParam(rtfprop, "ets_pmo.RTFFileName");
			 TextFileName				= getParam(rtfprop, "ets_pmo.TextFileName");
			 */

			/*
			 * mq prop 
				  */
			mqprop = ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo_mq");

			qFrom = getParam(mqprop, "ets_pmo.MQSeries.Queue.From.Name");
			qReplyTo = getParam(mqprop, "ets_pmo.MQSeries.Queue.ReplyTo.Name");
			qTo = getParam(mqprop, "ets_pmo.MQSeries.Queue.To.Name");
			// q2To					= getParam(mqprop, "ets_pmo.MQSeries.Queue2.To.Name");
			qManager = getParam(mqprop, "ets_pmo.MQSeries.Queue.Manager.Name");
			qManagerReplyTo =
				getParam(mqprop, "ets_pmo.MQSeries.Queue.Manager.ReplyTo.Name");

			/* I am getting the MQHostName from gwa.properties. I get the value from ets_pmo_mq
			 * only when it is defined there. If you want to test in sanjose dev box or integration box
			 * or production box, please do not define the MQHostName property in the property file
			 * This if loop will not executed. The property will be instead picked from gwa.properties
			 * file
			 */
			String HN = getParam(mqprop, "ets_pmo.MQSeries.Host.Name", true);
			if (!HN.equalsIgnoreCase("")) {
				qHostName = HN;
			}

			qChannelName = getParam(mqprop, "ets_pmo.MQSeries.Channel.Name");

			/* I am getting the MQPortName from gwa.properties. I get the value from ets_pmo_mq
			 * only when it is defined there. If you want to test in sanjose dev box or integration box
			 * or production box, please do not define the MQPort property in the property file
			 * This if loop will not executed. The property will be instead picked from gwa.properties
			 * file
			 */
			String P = getParam(mqprop, "ets_pmo.MQSeries.Port", true);
			if (!P.equalsIgnoreCase("")) {
				qPort = P;
			}

			waitInterval = getParam(mqprop, "ets_pmo.MQSeries.waitInterval");
			sleepTime = getParam(mqprop, "ets_pmo.MQSeries.sleepTime");
			numRetries = getParam(mqprop, "ets_pmo.MQSeries.numRetries");
			// displayLevel			= getParam(mqprop, "ets_pmo.MQSeries.displayLevel");
			nonFatalErrorCodes =
				getParam(mqprop, "ets_pmo.MQSeries.nonFatalErrorCodes");
			CR_CORR_ID = getParam(mqprop, "ets_pmo.MQSeries.CR_CORR_ID");
			PROJ_CORR_ID = getParam(mqprop, "ets_pmo.MQSeries.PROJ_CORR_ID");
			PROJ_ACK_CORR_ID =
				getParam(mqprop, "ets_pmo_MQSeries.PROJ_ACK_CORR_ID");
			PROJ_NACK_CORR_ID =
				getParam(mqprop, "ets_pmo_MQSeries.PROJ_NACK_CORR_ID");
			CR_ACK_CORR_ID =
				getParam(mqprop, "ets_pmo_MQSeries.CR_ACK_CORR_ID");
			CR_NACK_CORR_ID =
				getParam(mqprop, "ets_pmo_MQSeries.CR_NACK_CORR_ID");
			PROJ_ACK_ACK_CORR_ID =
				getParam(mqprop, "ets_pmo_MQSeries.PROJ_ACK_ACK_CORR_ID");

			usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue =
				Integer.parseInt(
					getParam(
						mqprop,
						"ets_pmo.MQSeries.usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue"));

			//			 environment
			environment = getParam(mqprop, "ets_pmo.MQSeries.environment");

			/*
			 * mail prop
			 */
			mailprop =
				ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo_mail");
			TO = getParam(mailprop, "ets_pmo.TO");
			TO1 = getParam(mailprop, "ets_pmo.TO1");
			TO2 = getParam(mailprop, "ets_pmo.TO2");
			FROM = getParam(mailprop, "ets_pmo.FROM");
			ERROR_FROM = getParam(mailprop, "ets_pmo.ERROR_FROM");
			CC = getParam(mailprop, "ets_pmo.CC");
			MailOnError = getParam(mailprop, "ets_pmo.MAIL_ON_ERROR");
			mailHost1 = getParam(mailprop, "ets_pmo.mailHost1");
			mailHost2 = getParam(mailprop, "ets_pmo.mailHost2");
			mailHost3 = getParam(mailprop, "ets_pmo.mailHost3");
			RESOURCELIST_TO = getParam(mailprop, "ets_pmo.RESOURCELIST_TO");
			RESOURCELIST_CC = getParam(mailprop, "ets_pmo.RESOURCELIST_CC");
			
			PCR_NEWMailSubj = getParam(mailprop, "ets_pmo.PCR_NEWMailSubj");
			PCR_UPDATEMailSubj =
				getParam(mailprop, "ets_pmo.PCR_UPDATEMailSubj");
			PCRMailHeader = getParam(mailprop, "ets_pmo.PCRMailHeader");
			PCRMailFooter = getParam(mailprop, "ets_pmo.PCRMailFooter");
			
			Issue_NEWMailSubj = getParam(mailprop, "ets_pmo.Issue_NEWMailSubj");
			Issue_UPDATEMailSubj =
							getParam(mailprop, "ets_pmo.Issue_UPDATEMailSubj");
			IssueMailHeader = getParam(mailprop, "ets_pmo.IssueMailHeader");
			IssueMailFooter = getParam(mailprop, "ets_pmo.IssueMailFooter");
						
			ProjectSyncSubject =
				getParam(mailprop, "ets_pmo.ProjectSyncSubject");
			ProjectSyncHeader = getParam(mailprop, "ets_pmo.ProjectSyncHeader");
			ProjectSyncFooter = getParam(mailprop, "ets_pmo.ProjectSyncFooter");
			
			
			/*
				 *cri prop..some cri props are in ets_pmo prop file 
				 */
			criprop = ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo_cri");

			CRExceptionType = getParam(criprop, "ets_pmo_cri.CRExceptionType");

			CR_TIMEOUTWINDOW =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.CR_TIMEOUT_WINDOW"));

			NoCRCreateElements =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NumberOfCRCreateElements"));
			NoCRUpdateElements =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NumberOfCRUpdateElements"));
			//Initializing the element vector for update
			CRCreateElements = new String[NoCRCreateElements];
			for (int i = 0; i < NoCRCreateElements; i++) {
				CRCreateElements[i] =
					getParam(criprop, "ets_pmo_cri.CRCreateElement" + i + "");

			}
			//Initializing the element vector for update
			CRUpdateElements = new String[NoCRUpdateElements];
			for (int i = 0; i < NoCRUpdateElements; i++) {
				CRUpdateElements[i] =
					getParam(criprop, "ets_pmo_cri.CRUpdateElement" + i + "");

			}

			NoISSUECreateElements =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NumberOfISSUECreateElements"));
			NoISSUEUpdateElements =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NumberOfISSUEUpdateElements"));
			//Initializing the element vector for update
			ISSUECreateElements = new String[NoISSUECreateElements];
			for (int i = 0; i < NoISSUECreateElements; i++) {
				ISSUECreateElements[i] =
					getParam(criprop, "ets_pmo_cri.ISSUECreateElement" + i + "");

			}
			//Initializing the element vector for update
			ISSUEUpdateElements = new String[NoISSUEUpdateElements];
			for (int i = 0; i < NoISSUEUpdateElements; i++) {
				ISSUEUpdateElements[i] =
					getParam(criprop, "ets_pmo_cri.ISSUEUpdateElement" + i + "");

			}

			CRI_ROOT_USER_Id = getParam(criprop, "ets_pmo_cri.CRIRoot_User_ID");

			CR_CREATEDINPMO_STATE =
				getParam(criprop, "ets_pmo_cri.CR_CREATEDINPMO_STATE");
			CR_CREATEDNEW_STATE =
				getParam(criprop, "ets_pmo_cri.CR_CREATEDNEW_STATE");
			CR_CREATED_STATE_SENT =
				getParam(criprop, "ets_pmo_cri.CR_CREATED_STATE_SENT");
			CR_UPDATED_STATE =
				getParam(criprop, "ets_pmo_cri.CR_UPDATED_STATE");
			CR_UPDATED_STATE_SENT =
				getParam(criprop, "ets_pmo_cri.CR_UPDATED_STATE_SENT");
			CR_ACKED_STATE = getParam(criprop, "ets_pmo_cri.CR_ACKED_STATE");
			CR_NACKEDCREATE_STATE =
				getParam(criprop, "ets_pmo_cri.CR_NACKEDCREATE_STATE");
			CR_NACKEDUPDATE_STATE =
				getParam(criprop, "ets_pmo_cri.CR_NACKEDUPDATE_STATE");
			CR_TIMEOUT_STATE =
				getParam(criprop, "ets_pmo_cri.CR_TIMEOUT_STATE");

			CR_CREATEDINPMO_ETSID =
				getParam(criprop, "ets_pmo_cri.CR_CREATEDINPMO_ETSID");
			int noOfRTFElementsForCR =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cr.RTF.NoOfRTFs").trim());
			int initRTFValueForCR =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cr.RTF.InitialValue").trim());
			htCRIRTF = new Hashtable();
			for (int i = initRTFValueForCR; i <= noOfRTFElementsForCR; i++) {
				String rtfMappingValue =
					getParam(criprop, "ets_pmo_cr.RTF." + i);
				htCRIRTF.put(new Integer(i), rtfMappingValue);
			}
			int noOfRTFElementsForISSUE =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_issue.RTF.NoOfRTFs").trim());
			int initRTFValueForISSUE =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_issue.RTF.InitialValue").trim());
			htISSUERTF = new Hashtable();
			for (int i = initRTFValueForISSUE; i <= noOfRTFElementsForISSUE; i++) {
				String rtfMappingValue =
					getParam(criprop, "ets_pmo_issue.RTF." + i);
				htISSUERTF.put(new Integer(i), rtfMappingValue);
			}			
			int noOfStates =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NoOfStates").trim());
			htPMOtoETSChangeRequestStates = new Hashtable();
			for (int i = 0; i < noOfStates; i++) {
				String statemapping =
					getParam(criprop, "ets_pmo_cri.State" + i);
				String pmostate =
					statemapping.substring(0, statemapping.indexOf("$"));
				String etsstate =
					statemapping.substring(statemapping.indexOf("$") + 1);
				htPMOtoETSChangeRequestStates.put(pmostate, etsstate);

			}
			int noOfETStoPMOStates =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NoOfETSToPMOStates"));
			htETStoPMOChangeRequestStates = new Hashtable();
			for (int i = 0; i < noOfETStoPMOStates; i++) {
				String statemapping =
					getParam(criprop, "ets_pmo_cri.ETStoPMOState" + i);
				String etsstate =
					statemapping.substring(0, statemapping.indexOf("$"));
				String pmostate =
					statemapping.substring(statemapping.indexOf("$") + 1);
				htETStoPMOChangeRequestStates.put(etsstate, pmostate);

			}
			/* subu 4.5.1 fix : Issue rank mappings */
			int noOfPMOtoETSIssueStates =
							Integer.parseInt(
								getParam(criprop, "ets_pmo_cri.NoOfPMOtoETSIssueStates"));
						htPMOtoETSIssueStates = new Hashtable();
						for (int i = 0; i < noOfPMOtoETSIssueStates; i++) {
							String statemapping =
								getParam(criprop, "ets_pmo_cri.IssuePMOtoETSState" + i);
							String etsstate =
								statemapping.substring(0, statemapping.indexOf("$"));
							String pmostate =
								statemapping.substring(statemapping.indexOf("$") + 1);
							htPMOtoETSIssueStates.put(etsstate, pmostate);

			}
			int noOfETStoPMOIssueStates =
										Integer.parseInt(
											getParam(criprop, "ets_pmo_cri.NoOfETStoPMOIssueStates"));
									htETStoPMOIssueStates = new Hashtable();
									for (int i = 0; i < noOfETStoPMOIssueStates; i++) {
										String statemapping =
											getParam(criprop, "ets_pmo_cri.IssueETStoPMOState" + i);
										String etsstate =
											statemapping.substring(0, statemapping.indexOf("$"));
										String pmostate =
											statemapping.substring(statemapping.indexOf("$") + 1);
										htETStoPMOIssueStates.put(etsstate, pmostate);

			}
			/* subu 4.5.1 fix : PMOffice ISSUE - RANK mapping htPMOIssueStageIDRank */
					int NoOfIssuePMOStageID		 =
												Integer.parseInt(
													getParam(criprop, "ets_pmo_cri.NoOfIssuePMOStageID"));
												htPMOIssueStageIDRank = new Hashtable();
											for (int i = 0; i < NoOfIssuePMOStageID; i++) {
												String statemapping =
													getParam(criprop, "ets_pmo_cri.PMOIssueStageIDRank" + i);
												String stageIDName =
													statemapping.substring(0, statemapping.indexOf("$"));
												String Rank =
													statemapping.substring(statemapping.indexOf("$") + 1);
												htPMOIssueStageIDRank.put(stageIDName, Rank);

					}			

			/* subu 4.5.1 fix : Issue front end to Daemon mapping htFrontEndETStoDaemonIssueStates */
			int noOfFrontEndETStoDaemonIssueStates =
										Integer.parseInt(
											getParam(criprop, "ets_pmo_cri.NoOfFrontEndETStoDaemonIssueStates"));
										htFrontEndETStoDaemonIssueStates = new Hashtable();
									for (int i = 0; i < noOfFrontEndETStoDaemonIssueStates; i++) {
										String statemapping =
											getParam(criprop, "ets_pmo_cri.IssueFrontEndETStoDaemonState" + i);
										String frontendetsstate =
											statemapping.substring(0, statemapping.indexOf("$"));
										String Daemonstate =
											statemapping.substring(statemapping.indexOf("$") + 1);
										htFrontEndETStoDaemonIssueStates.put(frontendetsstate, Daemonstate);

			}			
			/* subu 4.5.1 fix : Both Issue and ChangeRequest rank mappings*/
			int NoOfIssueRankRanges =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NoOfIssueRankRanges"));
			htIssueRankRange = new Hashtable();
			for (int i = 0; i < NoOfIssueRankRanges; i++) {
				String IssueRangeValue =
					getParam(criprop, "ets_pmo_cri.IssueRankRange" + i);
				String IssueRange =
					IssueRangeValue.substring(0, IssueRangeValue.indexOf("$"));
				String IssueValue =
					IssueRangeValue.substring(IssueRangeValue.indexOf("$") + 1);
				htIssueRankRange.put(IssueRange, IssueValue);
			}
			int noChangeRequestRanges =
				Integer.parseInt(
					getParam(criprop, "ets_pmo_cri.NoOfChangeRequestRankRanges").trim());

			htChangeRequestRankRange = new Hashtable();
			for (int i = 0; i < noChangeRequestRanges; i++) {
				String ChangeRequestRangeValue =
					getParam(criprop, "ets_pmo_cri.ChangeRequestRankRange" + i);
				String ChangeRequestRange =
					ChangeRequestRangeValue.substring(
						0,
						ChangeRequestRangeValue.indexOf("$"));
				String ChangeRequestValue =
					ChangeRequestRangeValue.substring(
						ChangeRequestRangeValue.indexOf("$") + 1);
				htChangeRequestRankRange.put(
					ChangeRequestRange,
					ChangeRequestValue);

			}

			// properties for CRIssue xmls
			//		 CRISource				= 
			//		 CRIDestination			=
			//		 XMLVersion				= getParam(prop, "ets_pmo.XMLVersion");
			//		 CRIApp					= getParam
			//		 XMLCRIMsgLog			= LogDir.trim() + getParam(prop, "ets_pmo.XMLCRIMsgLog");
			//		 XMLCRIErrorLog			= LogDir.trim() + getParam(prop, "ets_pmo.XMLCRIErrorLog");

			/*
			 * 
			 * 													PRINTING THE PROPERTIES FROM THE PROPERTY FILES
			 * 	
			 */
			logger.debug(
				"\n\n*************************HANDLING GWA PROPS*********************");
			logger.debug("dbName : [ " + dbName + " ]");
			logger.debug("dbUser : [ " + dbUser + " ]");
			////logger.debug("dbPwd : [ " + dbPwd + " ]");
			logger.debug("driver : [ " + driver + " ]");
			logger.debug(
				"serverEnv(not from the property file. Manipulating in the code) : [ "
					+ serverEnv
					+ " ]");

			logger.debug(
				"\n\n*************************HANDLING ETS_PMO PROPS*********************");
			logger.debug(
				"Test_ProjectXML_filename :[ "
					+ Test_ProjectXML_filename
					+ " ]");
			logger.debug(
				"TestProjectCreateUpdateXMLDir :[ "
					+ TestProjectCreateUpdateXMLDir
					+ " ]");
			logger.debug(
				"ProjectCreateUpdateXMLDir: [ "
					+ ProjectCreateUpdateXMLDir
					+ " ]");
			logger.debug("SAX_ERROR_CODE : [ " + SAX_ERROR_CODE + " ]");
			logger.debug("SAX_WARNING_CODE : [ " + SAX_WARNING_CODE + " ]");
			logger.debug(
				"SAX_FATALERROR_CODE : [ " + SAX_FATALERROR_CODE + " ]");
			logger.debug("MISSING_ID_CODE : [ " + MISSING_ID_CODE + " ]");
			logger.debug("SUCCESS_CODE : [ " + SUCCESS_CODE + " ]");
			logger.debug("loopingFlag : [ " + loopingFlag + " ]");
			logger.debug(
				"NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND : [ "
					+ NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND
					+ " ]");
			logger.debug(
				"sleepTimeBeforeTroublingMQ : [ "
					+ sleepTimeBeforeTroublingMQ
					+ " ]");
			logger.debug("unknownUserId : [ " + unknownUserId + " ]");
			logger.debug("xmlValidation : [ " + xmlValidation + " ]");
			
			strbuf = new StringBuffer("SD mappings \n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htSDtypes.keys(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htSDtypes.elements(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());
			strbuf = new StringBuffer("FD mappings \n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htFDtypes.keys(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htFDtypes.elements(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());

			logger.debug("appStartTag : [ " + appStartTag + " ]");
			logger.debug("appFinishTag : [ " + appFinishTag + " ]");
			logger.debug(
				"priortoProjectidStartTag : [ "
					+ priortoProjectidStartTag
					+ " ]");
			logger.debug("projectidStartTag : [ " + projectidStartTag + " ]");
			logger.debug("projectidFinishTag : [ " + projectidFinishTag + " ]");
			logger.debug("useridStartTag : [ " + useridStartTag + " ]");
			logger.debug("useridFinishTag : [ " + useridFinishTag + " ]");

			/*	 logger.debug("\n\n*************************HANDLING LOG PROPS*********************");					 		 	 				 
					 logger.debug("DebugValMax: [ " + DebugValMax + " ]");		
					 logger.debug("MQSendErrorLog : [ " + MQSendErrorLog + " ]");
					 logger.debug("MQReceiveErrorLog : [ " + MQReceiveErrorLog + " ]");
					 logger.debug("XMLProjMsgLog : [ " + XMLProjMsgLog + " ]");
					 logger.debug("XMLProjErrorLog : [ " + XMLProjErrorLog + " ]");
					 logger.debug("MQSendProjAckNackErrorLog : [ " + MQSendProjAckNackErrorLog + " ]");
					 logger.debug("XMLCRIMsgLog : [ " + XMLCRIMsgLog + " ]");
					 logger.debug("XMLCRIErrorLog : [ " + XMLCRIErrorLog + " ]");
				*/

			/*	 logger.debug("\n\n*************************HANDLING RTF PROPS*********************");					 		 	 				 	 				 
					 logger.debug("RTFConverterFileName : [ " + RTFConverterFileName + " ]");
					 logger.debug("RTFConverterFileName_html : [ " + RTFConverterFileName_html + " ]");	 				 
					 logger.debug("RTFFileName : [ " + RTFFileName + " ]");
					 logger.debug("TextFileName : [ " + TextFileName + " ]");	 				 
				*/

			logger.debug(
				"\n\n*************************HANDLING MQ PROPS*********************");
			logger.debug("qFrom : [ " + qFrom + " ]");
			logger.debug("qReplyTo : [ " + qReplyTo + " ]");
			logger.debug("qTo : [ " + qTo + " ]");
			logger.debug("q2To : [ " + q2To + " ]");
			logger.debug("qManager : [ " + qManager + " ]");
			logger.debug("qManagerReplyTo : [ " + qManagerReplyTo + " ]");
			logger.debug("qHostName : [ " + qHostName + " ]");
			logger.debug("qChannelName : [ " + qChannelName + " ]");
			logger.debug("qPort : [ " + qPort + " ]");
			logger.debug("waitInterval : [ " + waitInterval + " ]");
			logger.debug("sleepTime : [ " + sleepTime + " ]");
			logger.debug("qNumRetries : [ " + qNumRetries + " ]");
			logger.debug("displayLevel : [ " + displayLevel + " ]");
			logger.debug("nonFatalErrorCodes : [ " + nonFatalErrorCodes + " ]");
			logger.debug("CR_CORR_ID : [ " + CR_CORR_ID + " ]");
			logger.debug("PROJ_CORR_ID : [ " + " ]");
			logger.debug("PROJ_ACK_CORR_ID : [ " + PROJ_ACK_CORR_ID + " ]");
			logger.debug("PROJ_NACK_CORR_ID : [ " + PROJ_NACK_CORR_ID + " ]");
			logger.debug("CR_ACK_CORR_ID : [ " + CR_ACK_CORR_ID + " ]");
			logger.debug("CR_NACK_CORR_ID : [ " + CR_NACK_CORR_ID + " ]");
			logger.debug(
				"PROJ_ACK_ACK_CORR_ID : [ " + PROJ_ACK_ACK_CORR_ID + " ]");
			logger.debug(
				"usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue : [ "
					+ usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue
					+ " ]");

			logger.debug(
				"\n\n*************************HANDLING MAIL PROPS*********************");
			logger.debug("TO : [ " + TO + " ]");
			logger.debug("TO1 : [ " + TO1 + " ]");
			logger.debug("TO2 : [ " + TO2 + " ]");
			logger.debug("FROM : [ " + FROM + " ]");
			logger.debug("ERROR_FROM : [ " + ERROR_FROM + " ]");
			logger.debug("CC : [ " + CC + " ]");
			logger.debug("MailOnError : [ " + MailOnError + " ]");
			logger.debug("mailHost1 : [ " + mailHost1 + " ]");
			logger.debug("mailHost2 : [ " + mailHost2 + " ]");
			logger.debug("mailHost3 : [ " + mailHost3 + " ]");
			logger.debug("RESOURCELIST_TO : [ " + RESOURCELIST_TO + " ]");
			logger.debug("RESOURCELIST_CC : [ " + RESOURCELIST_CC + " ]");
			logger.debug("PCR_NEWMailSubj : [ " + PCR_NEWMailSubj + " ]");
			logger.debug("PCR_UPDATEMailSubj : [ " + PCR_UPDATEMailSubj + " ]");
			logger.debug("PCRMailHeader : [ " + PCRMailHeader + " ]");
			logger.debug("PCRMailFooter : [ " + PCRMailFooter + " ]");
			logger.debug("ProjectSyncSubject : [ " + ProjectSyncSubject + " ]");
			logger.debug("ProjectSyncHeader : [ " + ProjectSyncHeader + " ]");
			logger.debug("ProjectSyncFooter : [ " + ProjectSyncFooter + " ]");

			logger.debug(
				"\n\n***********HANDLING CR PROPS(Some cq props are in ets_pmo props)**********");
			logger.debug("CR_TIMEOUTWINDOW : [ " + CR_TIMEOUTWINDOW + " ]");
			for (int i = 0; i < NoCRCreateElements; i++) {
				logger.debug(
					"CRCreateElements" + i + " : " + CRCreateElements[i]);

			}
			for (int i = 0; i < NoCRUpdateElements; i++) {
				logger.debug(
					"CRUpdateElements" + i + " : " + CRUpdateElements[i]);
			}
			logger.debug("CRI_ROOT_USER_ID : " + CRI_ROOT_USER_Id);

			logger.debug("CRExceptionType : " + CRExceptionType);

			logger.debug("CR_CREATEDINPMO_STATE : " + CR_CREATEDINPMO_STATE);
			logger.debug("CR_CREATEDNEW_STATE : " + CR_CREATEDNEW_STATE);
			logger.debug("CR_UPDATED_STATE : " + CR_UPDATED_STATE);
			logger.debug("CR_ACKED_STATE : " + CR_ACKED_STATE);
			logger.debug("CR_NACKECREATE_STATE : " + CR_NACKEDCREATE_STATE);
			logger.debug("CR_NACKEDUPDATE_STATE : " + CR_NACKEDUPDATE_STATE);
			logger.debug("CR_MODIFIEDINPMO_STATE : " + CR_TIMEOUT_STATE);
			logger.debug("CR_CREATED_STATE_SENT : " + CR_CREATED_STATE_SENT);
			logger.debug("CR_UPDATED_STATE_SENT : " + CR_UPDATED_STATE_SENT);
			logger.debug("CR_CREATEDINPMO_ETSID : " + CR_CREATEDINPMO_ETSID);

			logger.debug("Change Request RTF Mappings :");
			for (int i = initRTFValueForCR; i <= noOfRTFElementsForCR; i++) {
				logger.debug(
					"RTF : "
						+ i
						+ " mapped to : "
						+ htCRIRTF.get(new Integer(i)));
			}
			logger.debug("Issue RTF Mappings :");
			for (int i = initRTFValueForISSUE; i <= noOfRTFElementsForISSUE; i++) {
				logger.debug(
					"RTF : "
						+ i
						+ " mapped to : "
						+ htISSUERTF.get(new Integer(i)));
			}
			strbuf = new StringBuffer("PMO to ETS state mappings\n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htPMOtoETSChangeRequestStates.keys(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\n\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htPMOtoETSChangeRequestStates.elements(); e.hasMoreElements();) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());
			
			/* subu 4.5.1 fix added Issues */
			/* Printing the ETS FrontEND to Daemon Issue state mappings */
				strbuf = null;
				strbuf = 
						new StringBuffer("PMO to ETS Issue state mappings\n");
				strbuf.append("\tKEYS: \n");
						for (Enumeration e = htFrontEndETStoDaemonIssueStates.keys();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
						strbuf.append("\tCORRESPONDING VALUES: \n");
						for (Enumeration e = htFrontEndETStoDaemonIssueStates.elements();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
				logger.debug(strbuf.toString());

			/* subu 4.5.1 fix added  */
			/* Printing the PMOffice STAGE_ID - RANK mappings*/
				strbuf = null;
				strbuf = 
						new StringBuffer("PMOffice STAGE_ID - RANK mappings\n");
				strbuf.append("\tKEYS: \n");
						for (Enumeration e = htPMOIssueStageIDRank.keys();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
						strbuf.append("\tCORRESPONDING VALUES: \n");
						for (Enumeration e = htPMOIssueStageIDRank.elements();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
				logger.debug(strbuf.toString());
			
			/* subu 4.5.1 fix added Issues */
			/* Printing the PMO to ETS Issue state mappings */
			strbuf = null;
			strbuf = 
				new StringBuffer("PMO to ETS Issue state mappings\n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htPMOtoETSIssueStates.keys();
				e.hasMoreElements();
				) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htPMOtoETSIssueStates.elements();
				e.hasMoreElements();
				) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());
			
			/* subu 4.5.1 fix added Issues */
			/* Printing the ETS to PMO Issue state mappings */
						strbuf = null;
						strbuf = 
							new StringBuffer("ETS to PMO Issue state mappings\n");
						strbuf.append("\tKEYS: \n");
						for (Enumeration e = htETStoPMOIssueStates.keys();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
						strbuf.append("\tCORRESPONDING VALUES: \n");
						for (Enumeration e = htETStoPMOIssueStates.elements();
							e.hasMoreElements();
							) {
							strbuf.append("\t" + e.nextElement() + "\n");
						}
						logger.debug(strbuf.toString());
			/* subu 4.5.1 fix added Issues and ChangeRequest */
			/* Printing the Change Request Rank to Severity/Priority mappings */
			strbuf = null;
			strbuf =
				new StringBuffer("ChangeRequest Rank to Severity/Priority mappings\n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htChangeRequestRankRange.keys();
				e.hasMoreElements();
				) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htChangeRequestRankRange.elements();
				e.hasMoreElements();
				) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());
			/* Printing the Issue Rank to Severity/Priority mappings */
			strbuf = null;
			strbuf =
				new StringBuffer("Issue Rank to Severity/Priority mappings\n");
			strbuf.append("\tKEYS: \n");
			for (Enumeration e = htIssueRankRange.keys();
					e.hasMoreElements();
					) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			strbuf.append("\tCORRESPONDING VALUES: \n");
			for (Enumeration e = htIssueRankRange.elements();
				e.hasMoreElements();
				) {
				strbuf.append("\t" + e.nextElement() + "\n");
			}
			logger.debug(strbuf.toString());
			logger.debug(
				"\n\n***********************Finished Inializing the properties********************************************\n\n\n\n");

		} catch (Exception x) {
			logger.fatal("INIT FAILED: [ " + x.getMessage() + " ]");
			success = false;
		} finally  {
			isInited = success;
		}
		
		return success;
	}

	/**
	 * Returns the prop.
	 * @return java.util.ResourceBundle
	 */
	public static java.util.ResourceBundle getProp() {
		return prop;
	}

	/**
	 * Returns the projectCreateUpdateXMLDir.
	 * @return String
	 */
	public static String getProjectCreateUpdateXMLDir() {
		return ProjectCreateUpdateXMLDir;
	}
	/**
	 * Returns the projectTmpDir.
	 * @return String
	 */
	public static String getProjectTmpDir() {
		return ProjectTmpDir;
	}
	/**
	 * Sets the prop.
	 * @param prop The prop to set
	 */
	public static void setProp(java.util.ResourceBundle prop) {
		ETSPMOGlobalInitialize.prop = prop;
	}

	/**
	 * Sets the projectCreateUpdateXMLDir.
	 * @param projectCreateUpdateXMLDir The projectCreateUpdateXMLDir to set
	 */
	public static void setProjectCreateUpdateXMLDir(String projectCreateUpdateXMLDir) {
		ProjectCreateUpdateXMLDir = projectCreateUpdateXMLDir;
	}

	public static void main(String args[]) {
		if (args.length < 1) {
			logger.warn(
				" Usage : ETSPMOGlobalInitialize <PropertyFileLocation> ");
			System.exit(0);
		}

		ETSPMOGlobalInitialize Global = new ETSPMOGlobalInitialize();
	}
	/**
	 * Returns the debugValMax.
	 * @return int
	 */
	public static int getDebugValMax() {
		return DebugValMax;
	}

	/**
	 * Sets the debugValMax.
	 * @param debugValMax The debugValMax to set
	 */
	public static void setDebugValMax(int debugValMax) {
		DebugValMax = debugValMax;
	}

	/**
	 * Returns the dbName.
	 * @return String
	 */
	public static String getDbName() {
		return dbName;
	}

	/**
	 * Returns the dbPwd.
	 * @return String
	 */
	public static String getDbPwd() {
		return dbPwd;
	}

	/**
	 * Returns the dbUser.
	 * @return String
	 */
	public static String getDbUser() {
		return dbUser;
	}

	/**
	 * Returns the driver.
	 * @return String
	 */
	public static String getDriver() {
		return driver;
	}

	/**
	 * Returns the q2To.
	 * @return String
	 */
	public static String getQ2To() {
		return q2To;
	}

	/**
	 * Returns the qChannelName.
	 * @return String
	 */
	public static String getQChannelName() {
		return qChannelName;
	}

	/**
	 * Returns the qFrom.
	 * @return String
	 */
	public static String getQFrom() {
		return qFrom;
	}

	/**
	 * Returns the qHostName.
	 * @return String
	 */
	public static String getQHostName() {
		return qHostName;
	}

	/**
	 * Returns the qManager.
	 * @return String
	 */
	public static String getQManager() {
		return qManager;
	}

	/**
	 * Returns the qManagerReplyTo.
	 * @return String
	 */
	public static String getQManagerReplyTo() {
		return qManagerReplyTo;
	}

	/**
	 * Returns the qNumRetries.
	 * @return String
	 */
	public static String getQNumRetries() {
		return qNumRetries;
	}

	/**
	 * Returns the qPort.
	 * @return String
	 */
	public static String getQPort() {
		return qPort;
	}

	/**
	 * Returns the qReplyTo.
	 * @return String
	 */
	public static String getQReplyTo() {
		return qReplyTo;
	}

	/**
	 * Returns the qTo.
	 * @return String
	 */
	public static String getQTo() {
		return qTo;
	}

	/**
	 * Returns the displayLevel.
	 * @return String
	 */
	public static String getDisplayLevel() {
		return displayLevel;
	}

	/**
	 * Returns the numRetries.
	 * @return String
	 */
	public static String getNumRetries() {
		return numRetries;
	}

	/**
	 * Returns the sleepTime.
	 * @return String
	 */
	public static String getSleepTime() {
		return sleepTime;
	}

	/**
	 * Returns the waitInterval.
	 * @return String
	 */
	public static String getWaitInterval() {
		return waitInterval;
	}

	/**
	 * Returns the nonFatalErrorCodes.
	 * @return String
	 */
	public static String getNonFatalErrorCodes() {
		return nonFatalErrorCodes;
	}

	/**
	 * Returns the errorLog.
	 * @return String
	 */
	public static String getMQReceiveErrorLog() {
		return MQReceiveErrorLog;
	}

	/**
	 * Returns the cC.
	 * @return String
	 */
	public static String getCC() {
		return CC;
	}

	/**
	 * Returns the eRROR_FROM.
	 * @return String
	 */
	public static String getERROR_FROM() {
		return ERROR_FROM;
	}

	/**
	 * Returns the fROM.
	 * @return String
	 */
	public static String getFROM() {
		return FROM;
	}

	/**
	 * Returns the mailOnError.
	 * @return String
	 */
	public static String getMailOnError() {
		return MailOnError;
	}

	/**
	 * Returns the tO.
	 * @return String
	 */
	public static String getTO() {
		return TO;
	}

	/**
	 * Returns the tO1.
	 * @return String
	 */
	public static String getTO1() {
		return TO1;
	}

	/**
	 * Returns the tO2.
	 * @return String
	 */
	public static String getTO2() {
		return TO2;
	}

	/**
	 * Returns the mailHost.
	 * @return String
	 */
	public static String getMailHost1() {
		return mailHost1;
	}

	/**
	 * Returns the mailHost2.
	 * @return String
	 */
	public static String getMailHost2() {
		return mailHost2;
	}

	/**
	 * Returns the mailHost3.
	 * @return String
	 */
	public static String getMailHost3() {
		return mailHost3;
	}

	/**
	 * Returns the loopingFlag.
	 * @return boolean
	 */
	public static boolean isLoopingFlag() {
		return loopingFlag;
	}

	/**
	 * Returns the sleepTimeBeforeTroublingMQ.
	 * @return int
	 */
	public static int getSleepTimeBeforeTroublingMQ() {
		return sleepTimeBeforeTroublingMQ;
	}

	/**
	 * Returns the xMLMsgLog.
	 * @return String
	 */
	public static String getXMLProjMsgLog() {
		return XMLProjMsgLog;
	}

	/**
	 * Returns the nO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND.
	 * @return int
	 */
	public static int getNO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND() {
		return NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND;
	}

	/**
	 * Returns the test_ProjectXML_filename.
	 * @return String
	 */
	public static String getTest_ProjectXML_filename() {
		return Test_ProjectXML_filename;
	}

	/**
	 * Returns the testProjectCreateUpdateXMLDir.
	 * @return String
	 */
	public static String getTestProjectCreateUpdateXMLDir() {
		return TestProjectCreateUpdateXMLDir;
	}

	/**
	 * Returns the xMLErrorLog.
	 * @return String
	 */
	public static String getXMLProjErrorLog() {
		return XMLProjErrorLog;
	}

	/**
	 * Returns the sAX_ERROR_CODE.
	 * @return String
	 */
	public static String getSAX_ERROR_CODE() {
		return SAX_ERROR_CODE;
	}

	/**
	 * Returns the sAX_FATALERROR_CODE.
	 * @return String
	 */
	public static String getSAX_FATALERROR_CODE() {
		return SAX_FATALERROR_CODE;
	}

	/**
	 * Returns the sAX_WARNING_CODE.
	 * @return String
	 */
	public static String getSAX_WARNING_CODE() {
		return SAX_WARNING_CODE;
	}

	/**
	 * Returns the mQSendProjAckNackErrorLog.
	 * @return String
	 */
	public static String getMQSendProjAckNackErrorLog() {
		return MQSendProjAckNackErrorLog;
	}

	/**
	 * Returns the mISSING_ID_CODE.
	 * @return String
	 */
	public static String getMISSING_ID_CODE() {
		return MISSING_ID_CODE;
	}

	/**
	 * Returns the sUCCESS_CODE.
	 * @return String
	 */
	public static String getSUCCESS_CODE() {
		return SUCCESS_CODE;
	}

	/**
	 * Returns the handleCRI.
	 * @return boolean
	 */
	public static boolean isHandleCRI() {
		return HandleCRI;
	}

	/**
	 * Sets the handleCRI.
	 * @param handleCRI The handleCRI to set
	 */
	public static void setHandleCRI(boolean handleCRI) {
		HandleCRI = handleCRI;
	}

	/**
	 * Returns the xMLCRIErrorLog.
	 * @return String
	 */
	public static String getXMLCRIErrorLog() {
		return XMLCRIErrorLog;
	}

	/**
	 * Returns the xMLCRIMsgLog.
	 * @return String
	 */
	public static String getXMLCRIMsgLog() {
		return XMLCRIMsgLog;
	}

	/**
	 * Returns the cR_ACK_CORR_ID.
	 * @return String
	 */
	public static String getCR_ACK_CORR_ID() {
		return CR_ACK_CORR_ID;
	}

	/**
	 * Returns the cR_CORR_ID.
	 * @return String
	 */
	public static String getCR_CORR_ID() {
		return CR_CORR_ID;
	}

	/**
	 * Returns the cR_NACK_CORR_ID.
	 * @return String
	 */
	public static String getCR_NACK_CORR_ID() {
		return CR_NACK_CORR_ID;
	}

	/**
	 * Returns the pROJ_ACK_CORR_ID.
	 * @return String
	 */
	public static String getPROJ_ACK_CORR_ID() {
		return PROJ_ACK_CORR_ID;
	}

	/**
	 * Returns the pROJ_CORR_ID.
	 * @return String
	 */
	public static String getPROJ_CORR_ID() {
		return PROJ_CORR_ID;
	}

	/**
	 * Returns the pROJ_NACK_CORR_ID.
	 * @return String
	 */
	public static String getPROJ_NACK_CORR_ID() {
		return PROJ_NACK_CORR_ID;
	}

	/**
	 * Returns the cR_TIMEOUTWINDOW.
	 * @return int
	 */
	public static int getCR_TIMEOUTWINDOW() {
		return CR_TIMEOUTWINDOW;
	}

	/**
	 * Returns the rESOURCELIST_CC.
	 * @return String
	 */
	public static String getRESOURCELIST_CC() {
		return RESOURCELIST_CC;
	}

	/**
	 * Returns the rESOURCELIST_TO.
	 * @return String
	 */
	public static String getRESOURCELIST_TO() {
		return RESOURCELIST_TO;
	}

	/**
	 * Returns the usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue.
	 * @return int
	 */
	public static int getUsingDranoToCleanupTheCloggedMessagesInMQReceiveQueue() {
		return usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue;
	}

	/**
	 * Returns the mQSendErrorLog.
	 * @return String
	 */
	public static String getMQSendErrorLog() {
		return MQSendErrorLog;
	}

	/**
	 * Returns the mQCOAsANDCODs.
	 * @return String
	 */
	public static String getMQCOAsANDCODsLog() {
		return MQCOAsANDCODsLog;
	}

	/**
	 * Returns the unknownUserId.
	 * @return String
	 */
	public static String getUnknownUserId() {
		return unknownUserId;
	}

	/**
	 * Returns the xmlValidation.
	 * @return int
	 */
	public static int getXmlValidation() {
		return xmlValidation;
	}

	/**
	 * Returns the pROJ_ACK_ACK_CORR_ID.
	 * @return String
	 */
	public static String getPROJ_ACK_ACK_CORR_ID() {
		return PROJ_ACK_ACK_CORR_ID;
	}

	/**
	 * Returns the sleepTimeWhenMQIsDown.
	 * @return int
	 */
	public static int getSleepTimeWhenMQIsDown() {
		return sleepTimeWhenMQIsDown;
	}

	/**
	 * Returns the cR_ACKED_STATE.
	 * @return String
	 */
	public static String getCR_ACKED_STATE() {
		return CR_ACKED_STATE;
	}

	/**
	 * Returns the cR_CREATEDNEW_STATE.
	 * @return String
	 */
	public static String getCR_CREATEDNEW_STATE() {
		return CR_CREATEDNEW_STATE;
	}

	/**
	 * Returns the cR_MODIFIEDINPMO_STATE.
	 * @return String
	 */
	public static String getCR_TIMEOUT_STATE() {
		return CR_TIMEOUT_STATE;
	}

	/**
	 * Returns the cR_NEWFROMPMO_STATE.
	 * @return String
	 */
	public static String getCR_NACKEDUPDATE_STATE() {
		return CR_NACKEDUPDATE_STATE;
	}

	/**
	 * Returns the cR_NEWFROMPMO_STATE.
	 * @return String
	 */
	public static String getCR_NACKEDCREATE_STATE() {
		return CR_NACKEDCREATE_STATE;
	}
	/**
	 * Returns the cR_UPDATED_STATE.
	 * @return String
	 */
	public static String getCR_UPDATED_STATE() {
		return CR_UPDATED_STATE;
	}

	/**
	 * Returns the cRCreateElements.
	 * @return String[]
	 */
	public static String[] getCRCreateElements() {
		return CRCreateElements;
	}

	/**
	 * Returns the cRI_ROOT_USER_Id.
	 * @return String
	 */
	public static String getCRI_ROOT_USER_Id() {
		return CRI_ROOT_USER_Id;
	}

	/**
	 * Returns the cRUpdateElements.
	 * @return String[]
	 */
	public static String[] getCRUpdateElements() {
		return CRUpdateElements;
	}

	/**
	 * Returns the serverEnv.
	 * @return String
	 */
	public static String getServerEnv() {
		return serverEnv;
	}

	/**
	 * Sets the serverEnv.
	 * @param serverEnv The serverEnv to set
	 */
	public static void setServerEnv(String serverEnv) {
		ETSPMOGlobalInitialize.serverEnv = serverEnv;
	}

	/**
	 * Returns the htCRIRTF.
	 * @return Hashtable
	 */
	public static Hashtable getHtCRIRTF() {
		return htCRIRTF;
	}

	/**
	 * Returns the htRankRange.
	 * @return Hashtable
	 */
	public static Hashtable getHtChangeRequestRankRange() {
		return htChangeRequestRankRange;
	}

	/**
	 * Returns the htStates.
	 * @return Hashtable
	 */
	//public static Hashtable getHtStates() {
	public static Hashtable getHtPMOtoETSChangeRequestStates() {
		return htPMOtoETSChangeRequestStates;
	}

	/**
	 * Returns the htFDtypes.
	 * @return Hashtable
	 */
	public static Hashtable getHtFDtypes() {
		return htFDtypes;
	}

	/**
	 * Returns the htSDtypes.
	 * @return Hashtable
	 */
	public static Hashtable getHtSDtypes() {
		return htSDtypes;
	}

	/**
	 * Returns the cR_CREATED_STATE_SENT.
	 * @return String
	 */
	public static String getCR_CREATED_STATE_SENT() {
		return CR_CREATED_STATE_SENT;
	}

	/**
	 * Returns the cR_UPDATED_STATE_SENT.
	 * @return String
	 */
	public static String getCR_UPDATED_STATE_SENT() {
		return CR_UPDATED_STATE_SENT;
	}

	/**
	 * Returns the htETSPMOStates.
	 * @return Hashtable
	 */
//	public static Hashtable getHtETStoPMOStates() {
	public static Hashtable getHtETStoPMOChangeRequestStates() {
		return htETStoPMOChangeRequestStates;
	}

	/**
	 * Returns the pCRMailFooter.
	 * @return String
	 */
	public static String getPCRMailFooter() {
		return PCRMailFooter;
	}

	/**
	 * Returns the pCRMailHeader.
	 * @return String
	 */
	public static String getPCRMailHeader() {
		return PCRMailHeader;
	}

	/**
	 * Returns the pCRMailSubj.
	 * @return String
	 */
	public static String getPCR_NEWMailSubj() {
		return PCR_NEWMailSubj;
	}

	/**
	 * Returns the projectSyncFooter.
	 * @return String
	 */
	public static String getProjectSyncFooter() {
		return ProjectSyncFooter;
	}

	/**
	 * Returns the projectSyncHeader.
	 * @return String
	 */
	public static String getProjectSyncHeader() {
		return ProjectSyncHeader;
	}

	/**
	 * Returns the projectSyncSubject.
	 * @return String
	 */
	public static String getProjectSyncSubject() {
		return ProjectSyncSubject;
	}

	/**
	 * Returns the appFinishTag.
	 * @return String
	 */
	public static String getAppFinishTag() {
		return appFinishTag;
	}

	/**
	 * Returns the appStartTag.
	 * @return String
	 */
	public static String getAppStartTag() {
		return appStartTag;
	}

	/**
	 * Returns the priortoProjectidStartTag.
	 * @return String
	 */
	public static String getPriortoProjectidStartTag() {
		return priortoProjectidStartTag;
	}

	/**
	 * Returns the projectidFinishTag.
	 * @return String
	 */
	public static String getProjectidFinishTag() {
		return projectidFinishTag;
	}

	/**
	 * Returns the projectidStartTag.
	 * @return String
	 */
	public static String getProjectidStartTag() {
		return projectidStartTag;
	}

	/**
	 * Returns the useridFinishTag.
	 * @return String
	 */
	public static String getUseridFinishTag() {
		return useridFinishTag;
	}

	/**
	 * Returns the useridStartTag.
	 * @return String
	 */
	public static String getUseridStartTag() {
		return useridStartTag;
	}

	/**
	 * Returns the destinationFinishTag.
	 * @return String
	 */
	public static String getDestinationFinishTag() {
		return destinationFinishTag;
	}

	/**
	 * Returns the destinationStartTag.
	 * @return String
	 */
	public static String getDestinationStartTag() {
		return destinationStartTag;
	}

	/**
	 * Returns the sourceFinishTag.
	 * @return String
	 */
	public static String getSourceFinishTag() {
		return sourceFinishTag;
	}

	/**
	 * Returns the sourceStartTag.
	 * @return String
	 */
	public static String getSourceStartTag() {
		return sourceStartTag;
	}

	/**
	 * Returns the transactionidFinishTag.
	 * @return String
	 */
	public static String getTransactionidFinishTag() {
		return transactionidFinishTag;
	}

	/**
	 * Returns the transactionidStartTag.
	 * @return String
	 */
	public static String getTransactionidStartTag() {
		return transactionidStartTag;
	}

	/**
	 * Returns the transactionVersionFinishTag.
	 * @return String
	 */
	public static String getTransactionVersionFinishTag() {
		return transactionVersionFinishTag;
	}

	/**
	 * Returns the transactionVersionStartTag.
	 * @return String
	 */
	public static String getTransactionVersionStartTag() {
		return transactionVersionStartTag;
	}

	/**
	 * Returns the pCR_UPDATEMailSubj.
	 * @return String
	 */
	public static String getPCR_UPDATEMailSubj() {
		return PCR_UPDATEMailSubj;
	}

	/**
	 * Returns the cR_CREATEDINPMO_STATE.
	 * @return String
	 */
	public static String getCR_CREATEDINPMO_STATE() {
		return CR_CREATEDINPMO_STATE;
	}

	/**
	 * Sets the cR_TIMEOUT_STATE.
	 * @param cR_TIMEOUT_STATE The cR_TIMEOUT_STATE to set
	 */
	public static void setCR_TIMEOUT_STATE(String cR_TIMEOUT_STATE) {
		CR_TIMEOUT_STATE = cR_TIMEOUT_STATE;
	}

	/**
	 * Returns the cR_CREATEDINPMO_ETSID.
	 * @return String
	 */
	public static String getCR_CREATEDINPMO_ETSID() {
		return CR_CREATEDINPMO_ETSID;
	}

	/**
	 * Returns the cRExceptionType.
	 * @return String
	 */
	public static String getCRExceptionType() {
		return CRExceptionType;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtIssueRankRange() {
		return htIssueRankRange;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtETStoPMOIssueStates() {
		return htETStoPMOIssueStates;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtPMOtoETSIssueStates() {
		return htPMOtoETSIssueStates;
	}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

	/**
	 * @return
	 */
	public static String getIssue_NEWMailSubj() {
		return Issue_NEWMailSubj;
	}

	/**
	 * @return
	 */
	public static String getIssue_UPDATEMailSubj() {
		return Issue_UPDATEMailSubj;
	}

	/**
	 * @return
	 */
	public static String getIssueMailFooter() {
		return IssueMailFooter;
	}

	/**
	 * @return
	 */
	public static String getIssueMailHeader() {
		return IssueMailHeader;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtFrontEndETStoDaemonIssueStates() {
		return htFrontEndETStoDaemonIssueStates;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtPMOIssueStageIDRank() {
		return htPMOIssueStageIDRank;
	}

	/**
	 * @return
	 */
	public static Hashtable getHtISSUERTF() {
		return htISSUERTF;
	}

	/**
	 * @return
	 */
	public static String[] getISSUECreateElements() {
		return ISSUECreateElements;
	}

	/**
	 * @return
	 */
	public static String[] getISSUEUpdateElements() {
		return ISSUEUpdateElements;
	}
	
	

	public static String getEnvironment() {
		return environment;
	}
	public static void setEnvironment(String environment) {
		ETSPMOGlobalInitialize.environment = environment;
	}
	
	/**
	 * @param string
	 */
	public static void setCR_CREATEDINPMO_STATE(String string) {
		CR_CREATEDINPMO_STATE = string;
	}

}
