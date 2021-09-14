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

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.upload.FormFile;

/**
 * @author v2srikau
 */
public class NotificationHelper {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(NotificationHelper.class);

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
		ETSProj udProject,
		ETSDoc udDoc,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO)
		throws SQLException, Exception {

		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		char cIBMOnly = udDoc.getIbmOnly();

		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
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
				NotificationMsgHelper.getAuthorizedMembers(
					vtMembers,
					vtResUsers,
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
				NotificationMsgHelper.createAddMessage(
					udDoc,
					udProject.getName(),
					udProject.getProjectId(),
					strDate,
					pdDateFormat,
					cIBMOnly,
					udProps.getAppName(),
					strTopCatID,
					strCurrentCatID,
					strLinkID);
			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							ETSUtils.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {

					}
				}

				String subject =
					CommonEmailHelper.IBM 
						+ udProps.getAppName()
						+ " - New Document: "
						+ udDoc.getName();
				subject = ETSUtils.formatEmailSubject(subject);

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

					if (!udProject.isITAR()) {
					bSent =
						NotificationMsgHelper.sendEMail(
							udEdgeAccess.gEMAIL,
							toList,
							StringUtil.EMPTY_STRING,
							bccList,
							Global.mailHost,
							strMessageBuffer.toString(),
							subject,
							udEdgeAccess.gEMAIL);
				}
				}

				if (bSent) {
					udDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Add document",
						udEdgeAccess.gIR_USERN,
						udProject.getProjectId(),
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

///////////////////////////////////////////////////////////////////////////////
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
		ETSProj udProject,
		ETSDoc udDoc,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO)
		throws SQLException, Exception {

		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(udDAO.getConnection());
		return NotificationMsgHelper.performAddNotification(
		        strNotifyAllFlag, strNotifyOption, strTopCatID, 
		        strCurrentCatID, strLinkID, strNotifyList, 
		        strChUsers, vtResUsers, vtResUsersEdit, 
		        udProject.getProjectId(), udProject.getName(), 
		        udProject.getProjectType(), udProject.isITAR(), 
		        udProps.getAppName(), udDoc, udEdgeAccess.gIR_USERN, 
		        udReaderDAO, true);
	}
///////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * @param udForm
	 * @param vtResUsers
	 * @param strProjectID
	 * @param udDoc
	 * @param udDAO
	 * @throws SQLException
	 */
	public static Vector performUpdateNotification(
		BaseDocumentForm udForm,
		Vector vtResUsers,
		ETSProj udProject,
		ETSDoc udDoc,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO)
		throws SQLException, Exception {
		String strNotifyAllFlag = udForm.getNotifyFlag();
		String strNotifyOption = udForm.getNotifyOption();
		String[] strNotifyList = DocumentsHelper.getUniqueUserList(udForm);

		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		char cIBMOnly = udDoc.getIbmOnly();

		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
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
				NotificationMsgHelper.getAuthorizedMembers(
					vtMembers,
					vtResUsers,
					udDoc.getIbmOnly(),
					udForm.getChUsers(),
					udDAO.getConnection());
			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUploadDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;
			StringBuffer strMessageBuffer = new StringBuffer();

			strMessageBuffer.append("\n\n");

			strMessageBuffer.append(
				"A new version of a document that you are subscribed to has " +
				"been added to the following workspace on IBM Customer Connect: \n"
					+ udProject.getName()
					+ ". \n\n");
			strMessageBuffer.append(
				"The details of the document are as follows: \n\n");

			strMessageBuffer.append(
				"============================================"
					+ "==================\n");

			strMessageBuffer.append(
				"  Name:           "
					+ ETSUtils.formatEmailStr(udDoc.getName())
					+ "\n");

/*			strMessageBuffer.append(
				"  Description:    "
					+ ETSUtils.formatEmailStr(udDoc.getDescription())
					+ "\n");
*/
			strMessageBuffer.append(
				"  Keywords:       "
					+ ETSUtils.formatEmailStr(udDoc.getKeywords())
					+ " \n");
			strMessageBuffer.append(
				"  Author:         "
					+ ETSUtils.formatEmailStr(
						ETSUtils.getUsersName(
							udDAO.getConnection(),
							udDoc.getUserId()))
					+ "\n");
			strMessageBuffer.append(
				"  Date:           " + strDate + " (mm/dd/yyyy)\n\n");

			if (udDoc.getExpiryDate() != 0) {
				java.util.Date exdate =
					new java.util.Date(udDoc.getExpiryDate());
				String exdateStr = pdDateFormat.format(exdate);
				strMessageBuffer.append(
					"  Expiry Date:    " + exdateStr + " (mm/dd/yyyy)\n\n");
			}
			if (cIBMOnly == Defines.ETS_IBM_CONF
				|| cIBMOnly == Defines.ETS_IBM_ONLY) {
				strMessageBuffer.append(
					"  This document is marked IBM Only\n\n");
			}
			strMessageBuffer.append(
				"To view this document, click on the following URL and log-in:  \n");
			String url =
				Global.getUrl("ets/displayDocumentDetails.wss")
					+ "?proj="
					+ udDoc.getProjectId()
					+ "&tc="
					+ udForm.getTc()
					+ "&cc="
					+ udForm.getCc()
					+ "&docid="
					+ udDoc.getId()
					+ "&linkid="
					+ udForm.getLinkid()
					+ "&hitrequest=true";
			strMessageBuffer.append(url + "\n");

			strMessageBuffer.append(NotificationMsgHelper.getUnsubscribeText());
			
			strMessageBuffer.append(
			        NotificationMsgHelper.getEmailFooter(udProps.getAppName()));

			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							ETSUtils.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {
						//writer.println("amt exception caught. e= "+ae);
					}
				}

				String subject =
				    CommonEmailHelper.IBM 
						+ udProps.getAppName()
						+ " - New Document: "
						+ udDoc.getName();
				subject = ETSUtils.formatEmailSubject(subject);

				String toList = "";
				String bccList = "";

				if (strNotifyOption.equals("bcc")) {
					bccList = strEmailIDs;
				} else {
					toList = strEmailIDs;
				}

				boolean bSent = false;

				if (!toList.trim().equals(StringUtil.EMPTY_STRING)
					|| !bccList.trim().equals(StringUtil.EMPTY_STRING)) {

					bSent =
						NotificationMsgHelper.sendEMail(
							udEdgeAccess.gEMAIL,
							toList,
							StringUtil.EMPTY_STRING,
							bccList,
							Global.mailHost,
							strMessageBuffer.toString(),
							subject,
							udEdgeAccess.gEMAIL);
				}

				if (bSent) {
					udDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Update document",
						udEdgeAccess.gIR_USERN,
						udProject.getProjectId(),
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
	 * @param udForm
	 * @param vtResUsers
	 * @param vtResUsersEdit
	 * @param strProjectID
	 * @param udDoc
	 * @param udDAO
	 * @throws SQLException
	 */
	public static Vector performUpdateNotification(
		BaseDocumentForm udForm,
		Vector vtResUsers,
		Vector vtResUsersEdit,
		ETSProj udProject,
		ETSDoc udDoc,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO)
		throws SQLException, Exception {
		String strNotifyAllFlag = udForm.getNotifyFlag();
		String strNotifyOption = udForm.getNotifyOption();
		String[] strNotifyList = DocumentsHelper.getUniqueUserList(udForm);

		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		char cIBMOnly = udDoc.getIbmOnly();

		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
			// vtMembers = vtResUsersEdit;
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
				NotificationMsgHelper.getAuthorizedMembers(
					vtMembers,
					vtResUsers,
					vtResUsersEdit, 
					udDoc.getIbmOnly(),
					udForm.getChUsers(),
					udDAO.getConnection());
			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUploadDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;
			StringBuffer strMessageBuffer = new StringBuffer();

			strMessageBuffer.append("\n\n");

			strMessageBuffer.append(
				"A new version of a document that you are subscribed to has " +
				"been added to the following workspace on IBM Customer Connect: \n"
					+ udProject.getName()
					+ ". \n\n");
			strMessageBuffer.append(
				"The details of the document are as follows: \n\n");

			strMessageBuffer.append(
				"============================================"
					+ "==================\n");

			strMessageBuffer.append(
				"  Name:           "
					+ ETSUtils.formatEmailStr(udDoc.getName())
					+ "\n");
/*
			strMessageBuffer.append(
				"  Description:    "
					+ ETSUtils.formatEmailStr(udDoc.getDescription())
					+ "\n");

*/			strMessageBuffer.append(
				"  Keywords:       "
					+ ETSUtils.formatEmailStr(udDoc.getKeywords())
					+ " \n");
			strMessageBuffer.append(
				"  Author:         "
					+ ETSUtils.formatEmailStr(
						ETSUtils.getUsersName(
							udDAO.getConnection(),
							udDoc.getUserId()))
					+ "\n");
			strMessageBuffer.append(
				"  Date:           " + strDate + " (mm/dd/yyyy)\n\n");

			if (udDoc.getExpiryDate() != 0) {
				java.util.Date exdate =
					new java.util.Date(udDoc.getExpiryDate());
				String exdateStr = pdDateFormat.format(exdate);
				strMessageBuffer.append(
					"  Expiry Date:    " + exdateStr + " (mm/dd/yyyy)\n\n");
			}
			if (cIBMOnly == Defines.ETS_IBM_CONF
				|| cIBMOnly == Defines.ETS_IBM_ONLY) {
				strMessageBuffer.append(
					"  This document is marked IBM Only\n\n");
			}
			strMessageBuffer.append(
				"To view this document, click on the following URL and log-in:  \n");
			String url =
				Global.getUrl("ets/displayDocumentDetails.wss")
					+ "?proj="
					+ udDoc.getProjectId()
					+ "&tc="
					+ udForm.getTc()
					+ "&cc="
					+ udForm.getCc()
					+ "&docid="
					+ udDoc.getId()
					+ "&linkid="
					+ udForm.getLinkid()
					+ "&hitrequest=true";
			strMessageBuffer.append(url + "\n");

			strMessageBuffer.append(
			        NotificationMsgHelper.getUnsubscribeText());
			strMessageBuffer.append(
			        NotificationMsgHelper.getEmailFooter(udProps.getAppName()));

			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							ETSUtils.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {
						//writer.println("amt exception caught. e= "+ae);
					}
				}

				String subject =
				    CommonEmailHelper.IBM 
						+ udProps.getAppName()
						+ " - New Document: "
						+ udDoc.getName();
				subject = ETSUtils.formatEmailSubject(subject);

				String toList = "";
				String bccList = "";

				if (strNotifyOption.equals("bcc")) {
					bccList = strEmailIDs;
				} else {
					toList = strEmailIDs;
				}

				boolean bSent = false;

				if (!toList.trim().equals(StringUtil.EMPTY_STRING)
					|| !bccList.trim().equals(StringUtil.EMPTY_STRING)) {

					bSent =
						NotificationMsgHelper.sendEMail(
							udEdgeAccess.gEMAIL,
							toList,
							StringUtil.EMPTY_STRING,
							bccList,
							Global.mailHost,
							strMessageBuffer.toString(),
							subject,
							udEdgeAccess.gEMAIL);
				}

				if (bSent) {
					udDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Update document",
						udEdgeAccess.gIR_USERN,
						udProject.getProjectId(),
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
	 * @param udForm
	 * @param vtResUsers
	 * @param vtResUsersEdit
	 * @param strNotifyList
	 * @param strProjectID
	 * @param udDoc
	 * @param udDAO
	 * @param strDocAction
	 * @throws SQLException
	 */
	public static Vector performUpdateNotification(
		BaseDocumentForm udForm,
		Vector vtResUsers,
		Vector vtResUsersEdit,
		String[] strNotificationList,
		ETSProj udProject,
		ETSDoc udDoc,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO,
		String strDocAction)
		throws SQLException, Exception {
		String strNotifyAllFlag = udForm.getNotifyFlag();
		String strNotifyOption = udForm.getNotifyOption();
		String[] strNotifyList = DocumentsHelper.getUniqueUserList(udForm);
		if (strDocAction.equals(DocConstants.ACTION_UPDATE_DOC_PROPS) ) {
			strNotifyList = strNotificationList;
		}

		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		char cIBMOnly = udDoc.getIbmOnly();

		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
			// vtMembers = vtResUsersEdit;
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
				NotificationMsgHelper.getAuthorizedMembers(
					vtMembers,
					vtResUsers,
					vtResUsersEdit, 
					udDoc.getIbmOnly(),
					udForm.getChUsers(),
					udDAO.getConnection());
		//	if started - for checking Update doc properties Action
		if (!strDocAction.equals(DocConstants.ACTION_UPDATE_DOC_PROPS) ) {
		//&& (strDocAction.equals(DocConstants.ACTION_UPDATE_DOC_PROPS))   ) {			
			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUploadDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;
			StringBuffer strMessageBuffer = new StringBuffer();

			strMessageBuffer.append("\n\n");

			strMessageBuffer.append(
				"A new version of a document that you are subscribed to has " +
				"been added to the following workspace on IBM Customer Connect: \n"
					+ udProject.getName()
					+ ". \n\n");
			strMessageBuffer.append(
				"The details of the document are as follows: \n\n");

			strMessageBuffer.append(
				"============================================"
					+ "==================\n");

			strMessageBuffer.append(
				"  Name:           "
					+ ETSUtils.formatEmailStr(udDoc.getName())
					+ "\n");
/*			strMessageBuffer.append(
				"  Description:    "
					+ ETSUtils.formatEmailStr(udDoc.getDescription())
					+ "\n");
*/			strMessageBuffer.append(
				"  Keywords:       "
					+ ETSUtils.formatEmailStr(udDoc.getKeywords())
					+ " \n");
			strMessageBuffer.append(
				"  Author:         "
					+ ETSUtils.formatEmailStr(
						ETSUtils.getUsersName(
							udDAO.getConnection(),
							udDoc.getUserId()))
					+ "\n");
			strMessageBuffer.append(
				"  Date:           " + strDate + " (mm/dd/yyyy)\n\n");

			if (udDoc.getExpiryDate() != 0) {
				java.util.Date exdate =
					new java.util.Date(udDoc.getExpiryDate());
				String exdateStr = pdDateFormat.format(exdate);
				strMessageBuffer.append(
					"  Expiry Date:    " + exdateStr + " (mm/dd/yyyy)\n\n");
			}
			if (cIBMOnly == Defines.ETS_IBM_CONF
				|| cIBMOnly == Defines.ETS_IBM_ONLY) {
				strMessageBuffer.append(
					"  This document is marked IBM Only\n\n");
			}
			strMessageBuffer.append(
				"To view this document, click on the following URL and log-in:  \n");
			String url =
				Global.getUrl("ets/displayDocumentDetails.wss")
					+ "?proj="
					+ udDoc.getProjectId()
					+ "&tc="
					+ udForm.getTc()
					+ "&cc="
					+ udForm.getCc()
					+ "&docid="
					+ udDoc.getId()
					+ "&linkid="
					+ udForm.getLinkid()
					+ "&hitrequest=true";
			strMessageBuffer.append(url + "\n");

			strMessageBuffer.append(
			        NotificationMsgHelper.getUnsubscribeText());
			strMessageBuffer.append(
			        NotificationMsgHelper.getEmailFooter(udProps.getAppName()));

			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							ETSUtils.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {
						//writer.println("amt exception caught. e= "+ae);
					}
				}

				String subject =
				    CommonEmailHelper.IBM 
						+ udProps.getAppName()
						+ " - New Document: "
						+ udDoc.getName();
				subject = ETSUtils.formatEmailSubject(subject);

				String toList = "";
				String bccList = "";

				if (strNotifyOption.equals("bcc")) {
					bccList = strEmailIDs;
				} else {
					toList = strEmailIDs;
				}

				boolean bSent = false;

				if (!toList.trim().equals(StringUtil.EMPTY_STRING)
					|| !bccList.trim().equals(StringUtil.EMPTY_STRING)) {

					bSent =
						NotificationMsgHelper.sendEMail(
							udEdgeAccess.gEMAIL,
							toList,
							StringUtil.EMPTY_STRING,
							bccList,
							Global.mailHost,
							strMessageBuffer.toString(),
							subject,
							udEdgeAccess.gEMAIL);
				}

				if (bSent) {
					udDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Update document",
						udEdgeAccess.gIR_USERN,
						udProject.getProjectId(),
						subject,
						toList,
						StringUtil.EMPTY_STRING);
				} else {
					m_pdLog.error(
						"Error occurred while notifying project members.");
				}
			}   // if closed - for checking Update doc properties Action 

			}
		}
		
		return vtMembers;

	}
	
	/**
	 * @param vtNotifyUsers
	 * @param strComment
	 * @param udDoc
	 * @param strCurrentDocId
	 * @param strNotifyOpt
	 * @param strProjectName
	 * @param strTopCat
	 * @param strCurrentCat
	 * @param strLinkID
	 * @param udDAO
	 */
	public static void performCommentsNotification(
	    BaseDocumentForm udForm,
		ETSDoc udDoc,
		String strCurrentDocId,
		ETSProj udProject,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO) {
		try {
			String strNotifyAllFlag = udForm.getAttachmentNotifyFlag();
			String strNotifyOption = udForm.getNotifyOption();
			String[] strNotifyList = DocumentsHelper.getAllUniqueUserList(udForm);
			char cIBMOnly = udDoc.getIbmOnly();
			String strComment = udForm.getDocComments();
			String strTopCat = udForm.getTc(); 
			String strCurrentCat = udForm.getCc();
			String strLinkID = udForm.getLinkid();
			String strNotifyOpt = udForm.getNotifyOption(); 
			
			Vector vtMembers = new Vector();
			if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
				Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
				for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
					vtMembers.add(
						((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
				}
			} else {
				// vtMembers = vtResUsersEdit;
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
							getAuthorizedMembersForNotification(
							vtMembers,
							cIBMOnly,
							udDAO);

				SimpleDateFormat pdDateFormat =
					new SimpleDateFormat(StringUtil.DATE_FORMAT);
				java.util.Date date = new java.util.Date();
				String dateStr = pdDateFormat.format(date);

				UnbrandedProperties udProps =
					PropertyFactory.getProperty(udProject.getProjectType());

				AccessCntrlFuncs udAccess = new AccessCntrlFuncs();

				String strEmailIDs = StringUtil.EMPTY_STRING;
				StringBuffer strMessage = new StringBuffer();

				strMessage.append("\n\n");
				if (udDoc.isLatestVersion()) {
					strMessage.append(
						"A new comment was added to a document that you are " +
						"subscribed to in the following workspace on IBM Customer Connect: \n");
				} else {
					strMessage.append(
						"A new comment was added to a "
							+ "previous version of a document that you are " +
									"subscribed to in the following workspace on IBM Customer Connect: \n");
				}
				strMessage.append(udProject.getName() + " \n\n");
				strMessage.append(
					"The details of the comment are as follows: \n\n");
				strMessage.append(
					"===================================="
						+ "==========================\n");

				strMessage.append(
					"  User:           "
						+ ETSUtils.formatEmailStr(udEdgeAccess.gIR_USERN)
						+ "\n");
				strMessage.append(
					"  Document name:  "
						+ ETSUtils.formatEmailStr(udDoc.getName())
						+ "\n");
				strMessage.append(
					"  Project name:   "
						+ ETSUtils.formatEmailStr(udProject.getName())
						+ " \n");
				strMessage.append(
					"  Date:           " + dateStr + " (mm/dd/yyyy)\n");
				strMessage.append(
					"  Comment:        "
						+ ETSUtils.formatEmailStr(strComment)
						+ " \n\n");

				if (udDoc.isIbmOnlyOrConf()) {
					strMessage.append("  This document is marked IBM Only\n\n");
				}

				strMessage.append(
					"To view this document, click on the following  URL:  \n");
				String url =
					Global.getUrl("ets/displayDocumentDetails.wss")
						+ "?proj="
						+ udDoc.getProjectId()
						+ "&tc="
						+ strTopCat
						+ "&cc="
						+ strCurrentCat
						+ "&docid="
						+ udDoc.getId()
						+ "&linkid="
						+ strLinkID
						+ "&hitrequest=true";
				if (!udDoc.isLatestVersion()) {
					url =
						Global.getUrl("ets/displayDocumentDetails.wss")
							+ "?proj="
							+ udDoc.getProjectId()
							+ "&tc="
							+ strTopCat
							+ "&cc="
							+ strCurrentCat
							+ "&currdocid="
							+ strCurrentDocId
							+ "&docid="
							+ udDoc.getId()
							+ "&linkid="
							+ strLinkID
							+ "&hitrequest=true";
				}
				strMessage.append(url + "\n");

				strMessage.append(
				        NotificationMsgHelper.getUnsubscribeText());

				strMessage.append(
				        NotificationMsgHelper.getEmailFooter(udProps.getAppName()));

				for (int i = 0; i < vtMembers.size(); i++) {
						String strMember = (String) vtMembers.elementAt(i);
						try {
							String strUserEmail =
								ETSUtils.getUserEmail(
									udDAO.getConnection(),
									strMember);
							strEmailIDs =
								strEmailIDs + strUserEmail + StringUtil.COMMA;
						} catch (AMTException ae) {
							//writer.println("amt exception caught. e= "+ae);
						}
					}

					String strSubject =
					    CommonEmailHelper.IBM 
							+ udProps.getAppName()
							+ " - New Comment for:"
							+ udDoc.getName();
					strSubject = ETSUtils.formatEmailSubject(strSubject);

					String strToList = StringUtil.EMPTY_STRING;
					String strBccList = StringUtil.EMPTY_STRING;

					if ("bcc".equals(strNotifyOpt)) {
						strBccList = strEmailIDs;
					} else {
						strToList = strEmailIDs;
					}

					boolean bSent = false;

					if (!strToList.trim().equals(StringUtil.EMPTY_STRING)
						|| !strBccList.trim().equals(StringUtil.EMPTY_STRING)) {
						bSent =
						NotificationMsgHelper.sendEMail(
								udEdgeAccess.gEMAIL,
								strToList,
								StringUtil.EMPTY_STRING,
								strBccList,
								Global.mailHost,
								strMessage.toString(),
								strSubject,
								udEdgeAccess.gEMAIL);
					}

					if (bSent) {
						udDAO.addEmailLog(
							"DocComment",
							String.valueOf(udDoc.getId()),
							"Add doc comment",
							udEdgeAccess.gIR_USERN,
							udDoc.getProjectId(),
							strSubject,
							strToList,
							StringUtil.EMPTY_STRING);
					} else {
						m_pdLog.error(
							"Error occurred while notifying project members.");
					}

				}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * @param udForm
	 * @param vtResUsers
	 * @param vtResUsersEdit
	 * @param strProjectID
	 * @param udDoc
	 * @param udDAO
	 * @throws SQLException
	 */
	public static Vector performAttachmentNotification(
		BaseDocumentForm udForm,
		ETSProj udProject,
		ETSDoc udDoc,
		//Vector vtNotifyAllUsers,
		//Vector vtNotifyUsers,
		//List ltNotifyGroups,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO,
		String strAttachmentAction)
		throws SQLException, Exception {
		String strNotifyAllFlag = udForm.getAttachmentNotifyFlag();
		String strNotifyOption = udForm.getNotifyOption();
		String[] strNotifyList = DocumentsHelper.getAllUniqueUserList(udForm);


		
		UnbrandedProperties udProps =
			PropertyFactory.getProperty(udProject.getProjectType());
		char cIBMOnly = udDoc.getIbmOnly();

		Vector vtMembers = new Vector();
		if (!StringUtil.isNullorEmpty(strNotifyAllFlag)) {
			Vector vtTmp = udDAO.getProjMembers(udProject.getProjectId(), true);
			for (int iCounter = 0; iCounter < vtTmp.size(); iCounter++) {
				vtMembers.add(
					((ETSUser) vtTmp.elementAt(iCounter)).getUserId());
			}
		} else {
			// vtMembers = vtResUsersEdit;
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
						getAuthorizedMembersForNotification(
						vtMembers,
						cIBMOnly,
						udDAO);

			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUploadDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;
			StringBuffer strMessageBuffer = attachmentNotificationContent(
					udDoc, 
					udForm, 
					udDAO, 
					udProps,
					udProject.getName(), 
					strAttachmentAction);
			
			if (vtMembers.size() > 0) {
				for (int i = 0; i < vtMembers.size(); i++) {
					//get amt information
					String strMember = (String) vtMembers.elementAt(i);
					try {
						String userEmail =
							ETSUtils.getUserEmail(
								udDAO.getConnection(),
								strMember);
						strEmailIDs = strEmailIDs + userEmail + ",";
					} catch (AMTException ae) {
						//writer.println("amt exception caught. e= "+ae);
					}
				}

				String subject = 
				    CommonEmailHelper.IBM 
						+ udProps.getAppName() 
						+ " - Document update: " 
						+ udDoc.getName();
				subject = ETSUtils.formatEmailSubject(subject);

				String toList = "";
				String bccList = "";

				if (strNotifyOption.equals("bcc")) {
					bccList = strEmailIDs;
				} else {
					toList = strEmailIDs;
				}

				boolean bSent = false;

				if (!toList.trim().equals(StringUtil.EMPTY_STRING)
					|| !bccList.trim().equals(StringUtil.EMPTY_STRING)) {

					bSent =
						NotificationMsgHelper.sendEMail(
							udEdgeAccess.gEMAIL,
							toList,
							StringUtil.EMPTY_STRING,
							bccList,
							Global.mailHost,
							strMessageBuffer.toString(),
							subject,
							udEdgeAccess.gEMAIL);
				}

				if (bSent) {
					udDAO.addEmailLog(
						"Document",
						String.valueOf(udDoc.getId()),
						"Update document",
						udEdgeAccess.gIR_USERN,
						udProject.getProjectId(),
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
	 * @param vtMembers
	 * @param vtResUsers
	 * @param cIBMOnly
	 * @param strIsRestricted
	 * @param udDAO
	 * @return
	 */
	private static Vector getAuthorizedMembersForNotification(
		Vector vtNotifyList,
		char cIBMOnly,
		DocumentDAO udDAO) {
		Vector vtNotifyMembers = new Vector();

		boolean bIsInternal = false;

		if ( (cIBMOnly != Defines.ETS_PUBLIC)) {
			for (int i = 0; i < vtNotifyList.size(); i++) {
				String strMember = (String) vtNotifyList.elementAt(i);
				try {
					bIsInternal = false;
					String edge_userid =
						AccessCntrlFuncs.getEdgeUserId(
							udDAO.getConnection(),
							strMember);
					String decaftype =
						AccessCntrlFuncs.decafType(
							edge_userid,
							udDAO.getConnection());
					if (decaftype.equals("I")) {
						bIsInternal = true;
					}
							
					if ( vtNotifyList.contains(strMember)
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
			vtNotifyMembers = vtNotifyList;
		}

		return vtNotifyMembers;
	}

	

/*
 *	@param udDoc, 
 *	@param udForm, 
 *  @param udDAO,
 *	@param strProjectName
 *  @param strAttachmentAction
 *	return
 *  
 */
	
	public static StringBuffer attachmentNotificationContent(
			ETSDoc udDoc, 
			BaseDocumentForm udForm, 
			DocumentDAO udDAO,
			UnbrandedProperties udProps,
			String strProjectName,
			String strAttachmentAction) throws Exception {
		StringBuffer strMessageBuffer = new StringBuffer();
		try {
			char cIBMOnly = udDoc.getIbmOnly();
			SimpleDateFormat pdDateFormat =
				new SimpleDateFormat(StringUtil.DATE_FORMAT);
			java.util.Date dtUploadDate =
				new java.util.Date(udDoc.getUpdateDate());
			String strDate = pdDateFormat.format(dtUploadDate);

			AccessCntrlFuncs udAccessCtrlFuncs = new AccessCntrlFuncs();

			String strEmailIDs = StringUtil.EMPTY_STRING;

			strMessageBuffer.append("\n\n");

			if(strAttachmentAction.equals( DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER )) {			
				strMessageBuffer.append(
						"New attachment(s) were added to a document that you are subscribed to in the following workspace on IBM Customer Connect: "
						+ strProjectName + ". \n\n");
			} else if(strAttachmentAction.equals( DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER )) {			
				strMessageBuffer.append(
						"Attachment(s) have been deleted from a document that you are subscribed to in the following workspace on IBM Customer Connect: "
						+ strProjectName + ". \n\n");
			}

			strMessageBuffer.append("The details of the document are as follows: \n\n");

			strMessageBuffer.append(
				"============================================"
					+ "==================\n");

			strMessageBuffer.append(
				"  Document name:           "
					+ ETSUtils.formatEmailStr(udDoc.getName())
					+ "\n");
			strMessageBuffer.append(
				"  Editor:                  "
					+ ETSUtils.formatEmailStr(ETSUtils.getUsersName(udDAO.getConnection(), udDoc.getUpdatedBy()))
					+ "\n");

	if(strAttachmentAction.equals(DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER)) {
			ArrayList uploadedFiles = udForm.getUploadedFilesList();
			strMessageBuffer.append("  Attachments added:       ");
			Iterator itrFiles = uploadedFiles.iterator();
			int iCounter = 0;
			while ( itrFiles.hasNext() ) {
				FormFile formFile = (FormFile)itrFiles.next();
			    if (iCounter > 0) {
			        strMessageBuffer.append("                           ");
			    }
			    
				strMessageBuffer.append(formFile.getFileName() +"\n");
				iCounter++;
			}
					//+ " \n");
	} else if(strAttachmentAction.equals(DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER)) {			
		String delDocAttachmentNames = udForm.getDelDocAttachmentNames();
			strMessageBuffer.append("  Attachments deleted:     ");
			int iCounter =0;
			StringTokenizer strTokenDeletedAttachments = new StringTokenizer(delDocAttachmentNames, ",");
			while ( strTokenDeletedAttachments.hasMoreElements() ) {
			    if (iCounter > 0) {
			        strMessageBuffer.append("                           ");
			    }
			    
				strMessageBuffer.append(strTokenDeletedAttachments.nextElement() +"\n");
				iCounter++;
			}
	}
			strMessageBuffer.append(
					"  Date:                    " + strDate + " (mm/dd/yyyy)\n\n");

/*			if (udDoc.getExpiryDate() != 0) {
				java.util.Date exdate =
					new java.util.Date(udDoc.getExpiryDate());
				String exdateStr = pdDateFormat.format(exdate);
				strMessageBuffer.append(
					"  Expiry Date:    " + exdateStr + " (mm/dd/yyyy)\n\n");
			}
*/
/*			if (cIBMOnly == Defines.ETS_IBM_CONF
				|| cIBMOnly == Defines.ETS_IBM_ONLY) {
				strMessageBuffer.append(
					"  This document is marked IBM Only\n\n");
			}
*/
			strMessageBuffer.append(
				"To view this document, click on the following URL and log-in:  \n");
			String url =
				Global.getUrl("ets/displayDocumentDetails.wss")
					+ "?proj="
					+ udDoc.getProjectId()
					+ "&tc="
					+ udForm.getTc()
					+ "&cc="
					+ udForm.getCc()
					+ "&docid="
					+ udDoc.getId()
					+ "&linkid="
					+ udForm.getLinkid()
					+ "&hitrequest=true";
			strMessageBuffer.append(url + "\n");
			
			strMessageBuffer.append(
					NotificationMsgHelper.getUnsubscribeText());

			strMessageBuffer.append(
			        NotificationMsgHelper.getEmailFooter(udProps.getAppName()));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strMessageBuffer;
	}
}