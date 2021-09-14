/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.documents;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.internet.InternetAddress;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocUpdateDAO;

import org.apache.log4j.Logger;

/**
 * @author v2srikau
 */
public class NotificationMsgHelper {

	private static final Logger m_pdLog = Logger.getLogger(NotificationMsgHelper.class);
	/**
	 * @return
	 * @throws Exception
	 */
	public static String getMailHost() throws Exception {

		String strMailHost = "";

		try {

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.common.gwa");

			if (rb == null) {
				strMailHost = "westrelay.us.ibm.com";
				return strMailHost;
			}

			strMailHost = rb.getString("gwa.mailHost");
			if (strMailHost == null || strMailHost.trim().equals("")) {
				strMailHost = "westrelay.us.ibm.com";
			} else {
				strMailHost = strMailHost.trim();
			}

		} catch (Exception e) {
			throw e;
		}

		return strMailHost;
	}

	/**
	 * @param udDoc
	 * @param strProjectName
	 * @param strProjectId
	 * @param strDate
	 * @param pdDateFormat
	 * @param cIBMOnly
	 * @param strAppName
	 * @param strTopCatID
	 * @param strCurrentCatID
	 * @param strLinkID
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer createAddMessage(
		ETSDoc udDoc,
		String strProjectName,
		String strProjectId,
		String strDate,
		SimpleDateFormat pdDateFormat,
		char cIBMOnly,
		String strAppName,
		String strTopCatID,
		String strCurrentCatID,
		String strLinkID)
		throws Exception {
		StringBuffer strMessageBuffer = new StringBuffer();

		strMessageBuffer.append("\n\n");
		if (udDoc.getDocStatus() == Defines.DOC_SUB_APP)
			strMessageBuffer.append(
				"A new document was added to the project for your approval: \n");
		else
			strMessageBuffer.append(
				"A new document was added to the following workspace on IBM " +
				"Customer Connect and you have been identified by the author " +
				"to receive this automatic notice: \n");
		strMessageBuffer.append(strProjectName + " \n\n");
		strMessageBuffer.append(
			"The details of the document are as follows: \n\n");
		strMessageBuffer.append(
			"==============================================================\n");

		strMessageBuffer.append(
			"  Name:           "
				+ StringUtil.formatEmailStr(udDoc.getName())
				+ "\n");
/*		
		strMessageBuffer.append(
			"  Description:    "
				+ StringUtil.formatEmailStr(udDoc.getDescription())
				+ "\n");
*/		
		strMessageBuffer.append(
			"  Keywords:       "
				+ StringUtil.formatEmailStr(udDoc.getKeywords())
				+ " \n");
		strMessageBuffer.append(
			"  Author:         " + udDoc.getUserName() + "\n");
		strMessageBuffer.append(
			"  Date:           " + strDate + " (mm/dd/yyyy)\n\n");

		if (udDoc.getExpiryDate() != 0) {
			java.util.Date dtExpiryDate =
				new java.util.Date(udDoc.getExpiryDate());
			String strExpiryDate = pdDateFormat.format(dtExpiryDate);
			strMessageBuffer.append(
				"  Expiry Date:    " + strExpiryDate + " (mm/dd/yyyy)\n\n");
		}

		if (cIBMOnly == Defines.ETS_IBM_ONLY
			|| cIBMOnly == Defines.ETS_IBM_CONF) {
			strMessageBuffer.append("  This document is marked IBM Only\n\n");
		}

		strMessageBuffer.append(
			"To view this document, click on the following  URL and log-in:  \n");
		String url =
			Global.getUrl("ets/displayDocumentDetails.wss")
				+ "?proj="
				+ strProjectId
				+ "&tc="
				+ strTopCatID
				+ "&cc="
				+ strCurrentCatID
				+ "&docid="
				+ udDoc.getId()
				+ "&linkid="
				+ strLinkID
				+ "&hitrequest=true";

		strMessageBuffer.append(url + "\n");

		strMessageBuffer.append(getUnsubscribeText());
		strMessageBuffer.append(getEmailFooter(strAppName));
		return strMessageBuffer;
	}

	/**
	 * @param strFrom
	 * @param strTo
	 * @param strCC
	 * @param strBCC
	 * @param strHost
	 * @param strMessage
	 * @param strSubject
	 * @param strReply
	 * @return
	 * @throws Exception
	 */
	public static boolean sendEMail(
		String strFrom,
		String strTo,
		String strCC,
		String strBCC,
		String strHost,
		String strMessage,
		String strSubject,
		String strReply)
		throws Exception {

		boolean debug = false;
		long sleepTime = 1000 * 5; // sleep for ...sleepTime

		boolean mailSent = false;

		// create some properties and get the default Session
		Properties props = new Properties();
		props.put("mail.smtp.host", strHost);
		javax.mail.Session session =
			javax.mail.Session.getInstance(props, null);
		session.setDebug(debug);

		try {

			InternetAddress[] tolist = InternetAddress.parse(strTo, false);
			InternetAddress[] cclist = InternetAddress.parse(strCC, false);
			InternetAddress[] bcclist = InternetAddress.parse(strBCC, false);

			// create a message
			javax.mail.Message msg =
				new javax.mail.internet.MimeMessage(session);
			msg.setFrom(new javax.mail.internet.InternetAddress(strFrom));

			msg.setRecipients(javax.mail.Message.RecipientType.TO, tolist);
			msg.setRecipients(javax.mail.Message.RecipientType.CC, cclist);
			msg.setRecipients(javax.mail.Message.RecipientType.BCC, bcclist);

			if (strReply != null && !strReply.trim().equals("")) {
				javax.mail.internet.InternetAddress[] replyto =
					{ new javax.mail.internet.InternetAddress(strReply)};
				msg.setReplyTo(replyto);
			}

			msg.setSubject(strSubject);

			InetAddress addr = InetAddress.getLocalHost();
			String hostName = addr.getHostName();
			msg.setText(strMessage);

			for (int i = 0; i <= 5; i++) {
				try {
					javax.mail.Transport.send(msg);
					mailSent = true;
					break;
				} catch (Exception ex) {
				    ex.printStackTrace(System.err);
				}

				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
				    e.printStackTrace(System.err);
				}
			}

		} catch (Exception ex) {
		    ex.printStackTrace(System.err);
			throw ex;
		}

		return mailSent;
	}

	/**
	 * @param udDoc
	 * @param strTopCatID
	 * @param strCurrentCatID
	 * @param strLinkID
	 * @param strAppName
	 * @param strProjectName
	 * @param strDocAttachmentNames
	 * @param strAttachmentAction
	 * @return
	 * @throws Exception
	 */
	public static StringBuffer createAttachmentMessage(
			ETSDoc udDoc, 
			String strTopCatID,
			String strCurrentCatID,
			String strLinkID,
			String strAppName,
			String strProjectName,
			String strDocAttachmentNames,
			String strAttachmentAction) throws Exception {

	    StringBuffer strMessageBuffer = new StringBuffer();
		SimpleDateFormat pdDateFormat =
			new SimpleDateFormat(StringUtil.DATE_FORMAT);
		java.util.Date dtUploadDate =
			new java.util.Date(udDoc.getUpdateDate());
		String strDate = pdDateFormat.format(dtUploadDate);

		strMessageBuffer.append("\n\n");

		if(strAttachmentAction.equals( 
		        DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER )) {			
			strMessageBuffer.append(
					"New attachment(s) were added to a document that you are " +
					"subscribed to in the following workspace on IBM Customer Connect: "
					+ strProjectName + ". \n\n");
		} else if(
		        strAttachmentAction.equals( 
		                DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER )) {			
			strMessageBuffer.append(
					"Attachment(s) have been deleted from a document that you " +
					"are subscribed to in the following workspace on IBM Customer Connect: "
					+ strProjectName + ". \n\n");
		}

		strMessageBuffer.append("The details of the document are as follows: \n\n");

		strMessageBuffer.append(
			"============================================"
				+ "==================\n");

		strMessageBuffer.append(
			"  Document name:           "
				+ StringUtil.formatEmailStr(udDoc.getName())
				+ "\n");
		strMessageBuffer.append(
		        "  Editor:                  "
				+ StringUtil.formatEmailStr(udDoc.getUpdatedBy())
				+ "\n");

		if(strAttachmentAction.equals(
		        DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER)) {			
				strMessageBuffer.append("  Attachments added:       ");
				StringTokenizer strTokenDeletedAttachments = 
				    new StringTokenizer(strDocAttachmentNames, ":");
				int iCounter = 0;
				while ( strTokenDeletedAttachments.hasMoreElements() ) {
				    if (iCounter > 0) {
				        strMessageBuffer.append("                           ");
				    }
					strMessageBuffer.append(
					        strTokenDeletedAttachments.nextElement() +"\n");
				    iCounter++;
				}
		} else if(strAttachmentAction.equals(
		        DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER)) {			
				strMessageBuffer.append("  Attachments deleted:     ");
				StringTokenizer strTokenDeletedAttachments = 
				    new StringTokenizer(strDocAttachmentNames, StringUtil.COMMA);
				int iCounter = 0;
				while ( strTokenDeletedAttachments.hasMoreElements() ) {
				    if (iCounter > 0) {
				        strMessageBuffer.append("                           ");
				    }
					strMessageBuffer.append(
					        strTokenDeletedAttachments.nextElement() +"\n");
				    iCounter++;
				}
		}

		strMessageBuffer.append(
		        "  Date:                    " + strDate + " (mm/dd/yyyy)\n\n");

		strMessageBuffer.append(
			"To view this document, click on the following URL and log-in:  \n");
		String url =
			Global.getUrl("ets/displayDocumentDetails.wss")
				+ "?proj="
				+ udDoc.getProjectId()
				+ "&tc="
				+ strTopCatID
				+ "&cc="
				+ strCurrentCatID
				+ "&docid="
				+ udDoc.getId()
				+ "&linkid="
				+ strLinkID
				+ "&hitrequest=true";
		strMessageBuffer.append(url + "\n");
		
		strMessageBuffer.append(getUnsubscribeText());

		strMessageBuffer.append(getEmailFooter(strAppName));
		
		return strMessageBuffer;
	}
	
	public static String getUnsubscribeText() {
	    StringBuffer strUnsubscribe = new StringBuffer();
	    
	    strUnsubscribe.append(
		        "\nIf you no longer wish to receive notices on this document, " 
		        + "go to the URL above, log-in, and click on the \"unsubscribe\" " 
		        + "link just above the document details.");

		return strUnsubscribe.toString();
	}
	
	public static String getEmailFooter(String strAppName) {
	    return CommonEmailHelper.getEmailFooter(strAppName);
	}

	/**
	 * @param vtMembers
	 * @param vtResUsers
	 * @param cIBMOnly
	 * @param strIsRestricted
	 * @param udDAO
	 * @return
	 */
	public static Vector getAuthorizedMembers(
		Vector vtMembers,
		Vector vtResUsers,
		char cIBMOnly,
		String strIsRestricted,
		Connection pdConnection) {
		Vector vtNotifyMembers = new Vector();

		boolean bIsInternal = false;

		if ((!StringUtil.isNullorEmpty(strIsRestricted))
			|| (cIBMOnly != Defines.ETS_PUBLIC)) {
			for (int i = 0; i < vtMembers.size(); i++) {
				String strMember = (String) vtMembers.elementAt(i);
				try {
					bIsInternal = false;
					String edge_userid =
						AccessCntrlFuncs.getEdgeUserId(
							pdConnection,
							strMember);
					String decaftype =
						AccessCntrlFuncs.decafType(
							edge_userid,
							pdConnection);
					if (decaftype.equals("I")) {
						bIsInternal = true;
					}
							
					if ((StringUtil.isNullorEmpty(strIsRestricted)
						|| DocConstants.DOC_UNRESTRICTED.equals(strIsRestricted) 
						|| vtResUsers.contains(strMember))
						&& (cIBMOnly == Defines.ETS_PUBLIC || bIsInternal)) {
						vtNotifyMembers.addElement(strMember);
					}

				} catch (AMTException a) {
					m_pdLog.error("amt exception in getibmmembers err= " + a);
				} catch (SQLException s) {
					m_pdLog.error("sql exception in getibmmembers err= " + s);
				}
			}
		} else {
			vtNotifyMembers = vtMembers;
		}

		return vtNotifyMembers;
	}

	/**
	 * @param vtMembers
	 * @param vtResUsers
	 * @param cIBMOnly
	 * @param strIsRestricted
	 * @param udDAO
	 * @return
	 */
	public static Vector getAuthorizedMembers(
		Vector vtMembers,
		Vector vtResUsers,
		Vector vtResUsersEdit,
		char cIBMOnly,
		String strIsRestricted,
		Connection pdConnection) {
		Vector vtNotifyMembers = new Vector();

		boolean bIsInternal = false;

		if ( (!StringUtil.isNullorEmpty(strIsRestricted))
			|| (cIBMOnly != Defines.ETS_PUBLIC) 
			|| (!vtResUsersEdit.isEmpty()) )  {
			for (int i = 0; i < vtMembers.size(); i++) {
				String strMember = (String) vtMembers.elementAt(i);
				try {
					bIsInternal = false;
					String edge_userid =
						AccessCntrlFuncs.getEdgeUserId(
						    pdConnection,
							strMember);
					String decaftype =
						AccessCntrlFuncs.decafType(
							edge_userid,
							pdConnection);
					if (decaftype.equals("I")) {
						bIsInternal = true;
					}
							
					if( (StringUtil.isNullorEmpty(strIsRestricted)
						|| DocConstants.DOC_UNRESTRICTED.equals(strIsRestricted) 
						|| vtResUsers.contains(strMember) 
						|| vtResUsersEdit.contains(strMember) )
						&& (cIBMOnly == Defines.ETS_PUBLIC || bIsInternal)) {
						vtNotifyMembers.addElement(strMember);
					}

				} catch (AMTException a) {
					m_pdLog.error("amt exception in getibmmembers err= " + a);
				} catch (SQLException s) {
					m_pdLog.error("sql exception in getibmmembers err= " + s);
				}
			}
		} else {
			vtNotifyMembers = vtMembers;
		}

		return vtNotifyMembers;
	}

	/**
	 * @param strNotifyAllFlag
	 * @param strNotifyOption
	 * @param strTopCatID
	 * @param strCurrentCatID
	 * @param strLinkID
	 * @param strNotifyList
	 * @param strChUsers
	 * @param vtResUsers
	 * @param udProject
	 * @param udDoc
	 * @param udEdgeAccess
	 * @param udDAO
	 * @param bSendEmail
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Vector performAddNotification(
		String strNotifyAllFlag,
		String strNotifyOption,
		String strTopCatID,
		String strCurrentCatID,
		String strLinkID,
		String strNotifyList[],
		String strChUsers,
		Vector vtResUsers,
		Vector vtResUsersEdit,
		String strProjectId,
		String strProjectName,
		String strProjectType,
		boolean bIsITAR,
		String strAppName,
		ETSDoc udDoc,
		String strUserID,
		DocReaderDAO udDAO,
		boolean bSendEmail)
		throws SQLException, Exception {

	    // IF bSendEmail is true then send the actual email
	    // Otherwise this method is just used to create the record list.
		char cIBMOnly = udDoc.getIbmOnly();

		String strSenderEmail = 
				CommonEmailHelper.getUserEmail(
				        udDAO.getConnection(),
				        strUserID);
		
		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(strProjectId, true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
			//vtMembers = vtResUsersEdit;
			if (strNotifyList != null && strNotifyList.length > 0) {
				for (int iCounter = 0;
					iCounter < strNotifyList.length;
					iCounter++) {
					vtMembers.add(strNotifyList[iCounter]);
				}
			}
		}

		if (vtMembers.size() > 0) {
			vtMembers =
				getAuthorizedMembers(
					vtMembers,
					vtResUsers,
					vtResUsersEdit,
					udDoc.getIbmOnly(),
					strChUsers,
					udDAO.getConnection());
			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUploadDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;

			StringBuffer strMessageBuffer =
				createAddMessage(
					udDoc,
					strProjectName,
					strProjectId,
					strDate,
					pdDateFormat,
					cIBMOnly,
					strAppName,
					strTopCatID,
					strCurrentCatID,
					strLinkID);
			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							CommonEmailHelper.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {

					}
				}

				String subject =
				    CommonEmailHelper.IBM 
						+ strAppName
						+ " - New Document: "
						+ udDoc.getName();
				subject = CommonEmailHelper.formatEmailSubject(subject);

				String toList = "";
				String bccList = "";

				if ("bcc".equals(strNotifyOption)) {
					bccList = strEmailIDs;
				} else {
					toList = strEmailIDs;
				}

				boolean bSent = false;

				if (!toList.trim().equals(StringUtil.EMPTY_STRING)
					|| !bccList.trim().equals(StringUtil.EMPTY_STRING)) {

					if (!bIsITAR) {
					    if (bSendEmail) {
							bSent = 
							sendEMail(
							    strSenderEmail,
								toList,
								StringUtil.EMPTY_STRING,
								bccList,
								Global.mailHost,
								strMessageBuffer.toString(),
								subject,
								strSenderEmail);
					}
					    else {
					        bSent = true;
					    }
					}
				}

				if (bSent && bSendEmail) {
				    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
				    udUpdateDAO.setConnection(udDAO.getConnection());
				    udUpdateDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Add document",
						strUserID,
						strProjectId,
						subject,
						toList,
						StringUtil.EMPTY_STRING);
				} else {
					m_pdLog.error(
						"Error occurred while notifying project members.");
				}

			}
		}

		return vtMembers;

	}
	
	/**
	 * @param lstGroups
	 * @param vtNotifyMembers
	 * @param udDAO
	 * @throws Exception
	 */
	public static void finalizeNotificationList(String []lstGroups, Vector vtNotifyMembers, DocReaderDAO udDAO) throws Exception {
		// Check which users are already in notification list
		// as part of a group. If there are any, then remove them
		List lstUsers = new ArrayList();
		for (int iCounter = 0; iCounter < lstGroups.length; iCounter++) {
			List lstGrpUsers = udDAO.getGroupUsers(lstGroups[iCounter]);
			for (int iUsers = 0; iUsers < lstGrpUsers.size(); iUsers++) {
				String strUserId = (String) lstGrpUsers.get(iUsers);
				if (!lstUsers.contains(strUserId)) {
					lstUsers.add(strUserId);
				}
			}
		}
		
		Vector vtUniqueNotifyMembers = new Vector();
		for (int i=0; i < vtNotifyMembers.size(); i++) {
		    String strMember = (String) vtNotifyMembers.get(i);
		    if (!lstUsers.contains(strMember)) {
		        vtUniqueNotifyMembers.add(strMember);
		    }
		}
		vtNotifyMembers.removeAllElements();
		vtNotifyMembers.addAll(vtUniqueNotifyMembers);
	}
}