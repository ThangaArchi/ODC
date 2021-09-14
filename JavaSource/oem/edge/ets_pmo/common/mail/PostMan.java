package oem.edge.ets_pmo.common.mail;

import oem.edge.ets_pmo.common.ETSPMOGlobalInitialize;

import java.util.Date;
import oem.edge.ets_pmo.datastore.resource.*;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.Vector;
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
public class PostMan {
	private static String CLASS_VERSION = "4.5.1";
	public static String cc;
	public static String subject;
	public static String body;
	public static String From;
	public static String ErrorFrom;
	public static String To;
	public static String To1;
	public static String To2;
	public static String mailHost1;
	public static String mailHost2;
	public static String mailHost3;
	public static String Resource_ccList;
	public static String Resource_ToList;
	public static String serverEnv;

	public static Vector vResList;

	public static boolean MAIL_ON_ERROR;
    private static boolean isInited = false;
	static Logger logger = Logger.getLogger(PostMan.class);

	public PostMan() {
		if (isInited==false)
		{
		  initialize();
		  isInited = true;
		}
	}
	public static void initialize() {
		cc = ETSPMOGlobalInitialize.getCC();
		ErrorFrom = ETSPMOGlobalInitialize.getERROR_FROM();
		if (ETSPMOGlobalInitialize
			.getMailOnError()
			.trim()
			.equalsIgnoreCase("yes")) {
			MAIL_ON_ERROR = true;
		}
		To = ETSPMOGlobalInitialize.getTO().trim();
		To1 = ETSPMOGlobalInitialize.getTO1().trim();
		To2 = ETSPMOGlobalInitialize.getTO2().trim();
		From = ETSPMOGlobalInitialize.getFROM().trim();
		mailHost1 = ETSPMOGlobalInitialize.getMailHost1().trim();
		mailHost2 = ETSPMOGlobalInitialize.getMailHost2().trim();
		mailHost3 = ETSPMOGlobalInitialize.getMailHost3().trim();
		serverEnv = ETSPMOGlobalInitialize.getEnvironment();
		if (serverEnv==null)
			serverEnv = ETSPMOGlobalInitialize.getServerEnv().trim();

		//for resource mail
		Resource_ccList = ETSPMOGlobalInitialize.getRESOURCELIST_CC();
		Resource_ToList = ETSPMOGlobalInitialize.getRESOURCELIST_TO();
	}

	public static void mail(String subject, String body) {
		// String cc = OrderProcessor.props.getProperty("EDSD_DEV_2");
		mail(cc, subject, body);
	}

	public static void mailPingAlert(String subject, String body) {
		//     String cc = OrderProcessor.props.getProperty("PING_ALERT_EMAIL");
		mail(cc, subject, body);
	}

	public static void mail(String cc, String subject, String body) {

		if (!MAIL_ON_ERROR)
			return;

		String to = To;
		String from = ErrorFrom;
		String replyTo = null;

		if (to == null || to.trim().length() == 0)
			return;

		if (cc != null && cc.trim().length() == 0)
			cc = null;

		mail(from, to, cc, replyTo, subject, body, null);

	}

	public static void ResourceInfoMail(
		String projId,
		String projName,
		String projCode,
		String workspace,
		Vector vRes) {

		vResList = vRes;
		logger.debug("vRes size is : " + vRes.size());

		//StringBuffer buff = new StringBuffer();
		String str = ETSPMOGlobalInitialize.getProjectSyncHeader() + "\nid: " + projId + "\t name: " + projName + "\n";
		if (workspace != null)
			str += "\nThis RPM project is linked to E&TS workspace '"+workspace+"' with the project code as '"+projCode+"'.";
		str += "\n\nResources:";
		/*buff.append(ETSPMOGlobalInitialize.getProjectSyncHeader());
		buff.append("\nid: " + projId + "\t name: " + projName + "\n");
		buff.append("\nResources:");
		*/
		for (int i = 0; i < vRes.size(); i++) {
			Resource res = (Resource) vRes.get(i);
			logger.info(
				"\nRESOURCE INFO MAIL TO BE SENT \n THE TO LIST: "
					+ Resource_ToList
					+ "\n THE CC LIST: "
					+ Resource_ccList
					+ "\n");
			logger.info(res.getElement_name() + " : " + res.getSecurity_id());
		/*	buff.append("\n\n\t Resource Name : " + res.getElement_name());
			buff.append("\n\t Resource Security : " + res.getSecurity_id());
		*/
		str = str + "\n\n\t Resource Name : " + res.getElement_name();
		str = str + "\n\t Resource Security : " + res.getSecurity_id();

		}

		//buff.append("\n\n\n\n" + ETSPMOGlobalInitialize.getProjectSyncFooter());
		str += "\n\n\n\n" + ETSPMOGlobalInitialize.getProjectSyncFooter();

		String subj = ETSPMOGlobalInitialize.getProjectSyncSubject();
		mail(
			From,
			Resource_ToList,
			Resource_ccList,
			null,
			null,
			subj,
			str,
			null);
		
		//buff = null;
		str =null;

	}
	public static void MQDown(String str) {
		str = 	"---- System generated Mail ----\n\n" + str; 
		String subj = "MQ Manager is Down";
		mail(From, To, null, null, null, subj, str, null);
		str = null;
	}
	public static void MQUp(String str) {
		str = 	"---- System generated Mail ----\n\n" + str; 
		String subj = "MQ Manager is Up again";
		mail(From, To, null, null, null, subj, str, null);
		//buff = null;
		str = null;
	}
	public static void DBDown(String str) {
		str = 	"---- System generated Mail ----\n\n" + str; 
		String subj = "DB Server is Down";
		mail(From, To, null, null, null, subj, str, null);
		str = null;
	}
	public static void DBUp(String str) {
		str = 	"---- System generated Mail ----\n\n" + str; 
		String subj = "DB Server is Up again";
		mail(From, To, null, null, null, subj, str, null);
		//buff = null;
		str = null;
	}
	public static void SendfileReceivedFromMQ(
		String xmlfile,
		String GoodnessOfFile) {
		
		
			/*
		StringBuffer buff = new StringBuffer();
		buff.append(
			"---- System generated mail. Please do not reply to this account ----\n\n");
		buff.append(xmlfile);
		buff.append(
			"\n\nE&TS Connect");
		*/
		String str = "---- System generated mail. Please do not reply to this account ----\n\n" + 
								
								"\n\nE&TS Connect\n\n";
		String subj = GoodnessOfFile;
		mail(From, To, null, null, null, subj, str, xmlfile);
		//buff = null;
		str = null;
	}
	public static void sendCreatedCRInfo(
		String projectMgrEmail,
		String otherUserEmails,
		String content,
		String probType) {
	
		String subj = null;
		String str = null;
		if (probType.equalsIgnoreCase("ISSUE")) {
			subj = ETSPMOGlobalInitialize.getIssue_NEWMailSubj();
			//buff.append(ETSPMOGlobalInitialize.getIssueMailHeader());
			str = ETSPMOGlobalInitialize.getIssueMailHeader();
			str += "\n\n"+subj;
			str += "\nThe details are : \n";
		} else if (probType.equalsIgnoreCase("CHANGEREQUEST")) {
			subj = ETSPMOGlobalInitialize.getPCR_NEWMailSubj();
			//buff.append(ETSPMOGlobalInitialize.getPCRMailHeader());
			str = ETSPMOGlobalInitialize.getPCRMailHeader();
			str += subj;
			str += "\nThe details are : \n";
		}

		//buff.append(content);
		str = str + content;
		//buff.append("\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter());
		str = str + "\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter();
		if(otherUserEmails != null){
			cc = cc + "," + otherUserEmails;
		}
		mail(
			From,
			projectMgrEmail,
			//cc, -> move to use bcc instead of cc
			null,
			cc,
			null,
			subj,
			str, null);
		str = null;
	//buff = null;
	}
	public static void sendUpdatedCRInfo(
		String projectMgrEmail,
		String otherUserEmails,
		String content,
		String probType) {
		//StringBuffer buff = new StringBuffer();
		String str = null;
		String subj = null;
		if (probType.equalsIgnoreCase("ISSUE")) {
			subj = ETSPMOGlobalInitialize.getIssue_UPDATEMailSubj();
			//buff.append(ETSPMOGlobalInitialize.getIssueMailHeader());
			str = ETSPMOGlobalInitialize.getIssueMailHeader();
			str += "\n\n"+subj;
			str += "\nThe details are : \n";
		} else if (probType.equalsIgnoreCase("CHANGEREQUEST")) {
			subj = ETSPMOGlobalInitialize.getPCR_UPDATEMailSubj();
			//buff.append(ETSPMOGlobalInitialize.getPCRMailHeader());
			str = ETSPMOGlobalInitialize.getPCRMailHeader();
			str += "\n\n"+subj;
			str += "\nThe details are : \n";
		}

		//buff.append(content);
		str = str + content;
		//buff.append("\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter());
		str = str + "\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter();
		if(otherUserEmails != null){
					cc = cc + "," + otherUserEmails;
				}
		mail(
			From,
			projectMgrEmail,
			//cc, -> move to use bcc instead of cc
			null,
			cc,
			null,
			subj,
			str, null);
			
		str =null;	
		//buff =null;
	}
	public static void sendCRInfoFromETS(
		boolean isCreate,
		boolean isCr,
		String content) {
		//StringBuffer buff = new StringBuffer();
		String str = ETSPMOGlobalInitialize.getPCRMailHeader() + "\n\n\n\n" + content +
								"\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter(); 
		//buff.append(ETSPMOGlobalInitialize.getPCRMailHeader());
		//buff.append("\n\n\n\n");
		//buff.append(content);
		//buff.append("\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter());
		String subj = null;

		String PCRType = "Issue";
		if (isCr == true) {
			PCRType = "ChangeRequest";
		}

		if (isCreate == true) {
			subj = "Create " + PCRType + " sent from E&TS to Rational PM";
		} else {
			subj = "Update " + PCRType + " sent from E&TS to Rational PM";
		}
		mail(From, To, null, null, null, subj, str, null);
		str =null;
	//buff = null;
	}
	public static void sendACKorNACKInfoFromETS(
		boolean isAck,
		String content) {
		//StringBuffer buff = new StringBuffer();
		String str = "---- System generated Mail ----\n\n" + content +
					"\n\n\n\n" + ETSPMOGlobalInitialize.getPCRMailFooter();
		String subj = null;
		if (isAck == true) {
			subj = "ACK from E&TS to Rational PM";
		} else {
			subj = "NACK from E&TS to Rational PM";
		}
		mail(From, To, null, null, null, subj, str, null);
		//buff =null;
		str = null;
	}

	private static void mail(
		String from,
		String to,
		String cc,
		String replyTo,
		String subject,
		String body,
		String filename) {
		mail(from, to, cc, null, replyTo, subject, body, filename);
	}

	private static void mail(
		String from,
		String to,
		String cc,
		String bcc,
		String replyTo,
		String subject,
		String body, String filename) {
		
		// add so that can call this static method directly
		if (isInited==false)
		{
			  initialize();
			  isInited = true;
		}
		subject = "[" + serverEnv + "] " + subject;

		String mailString =
			"FROM: "
				+ from
				+ "\nTO: "
				+ to
				+ "\nCC: "
				+ cc
				+ "\nBCC: "
				+ bcc
				+ "\nSUBJECT: "
				+ subject
				+ "\nBODY: "
				+ body;

		long sleepTime = 5 * 1000;

		boolean mailSent = false;

		int numRetries = 2;

		String[] emailHost = { mailHost1, mailHost2, mailHost3 };

		int numHosts = emailHost.length;
		
		if (filename!=null)
		{	
			//
			// add this checking so that it does not cause error for large files
			File file = null;
			long flimit = 12*1024*1024; // 12MB
			long fsize= -1;
			try {
				file = new File(filename);
				fsize=file.length();
				if (fsize<=0 || fsize>flimit)
				{
					logger.info("Size of file "+filename+" ="+fsize+", it is not sent");
					filename=null;
				}
			} catch (Exception e) {
				logger.info("Size of file"+filename+"="+flimit+", file is not attached");
				filename=null;
			}
		}

		mailBlock : for (int i = 0; i <= numRetries; i++) {

			for (int j = 0; j < numHosts; j++) {

				try {
					Mailer.sendMail(
						emailHost[j],
						from,
						to,
						cc,
						bcc,
						replyTo,
						subject,
						body,
						filename);
					mailSent = true;
					break mailBlock;
				} catch (Throwable t) {
					String str =
						"thrown while trying to send email (attempt# "
							+ ((i * numHosts) + j + 1)
							+ ") as follows:\n"
							+ mailString
							+ "\n\n"
							+ "StackTrace:\n"
							+ getStackTrace(t)
							+ "\n\n"
							+ "Will Re-try "
							+ ((numRetries - i) * numHosts + (numHosts - 1 - j))
							+ " times\n\n"
							+ "This error was thrown at: "
							+ new Date();

					logger.error(str);

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
				logger.error(str);
			}
		}

		if (!mailSent) {
			String str =
				"ERROR: The following email could NOT be sent despite "
					+ ((numRetries + 1) * numHosts)
					+ " attempts:\n"
					+ mailString;

			logger.error(str);
		}
	}

	static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}
	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
