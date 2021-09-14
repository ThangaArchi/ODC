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

package oem.edge.ets.fe.documents.mdb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.ejb.EJBException;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocMQMessage;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocumentEJBDAO;

import org.apache.log4j.Logger;

import com.ibm.jms.JMSObjectMessage;

/**
 * @author v2srikau
 */
public class DocumentMDBBean
	implements javax.ejb.MessageDrivenBean, javax.jms.MessageListener {
	public static final String VERSION = "1.1";
	private static final Logger m_pdLog =
		Logger.getLogger(DocumentMDBBean.class);
	private javax.ejb.MessageDrivenContext fMessageDrivenCtx;

	private static final String MSG_SEP = ":";
	private static final String ITAR_STATUS_COMPLETE = "C";
	private static final int MAX_ARRAY_SIZE = 1000000;

	/**
	 * @return
	 */
	public javax.ejb.MessageDrivenContext getMessageDrivenContext() {
		return fMessageDrivenCtx;
	}

	/**
	 * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(
	 * javax.ejb.MessageDrivenContext)
	 */
	public void setMessageDrivenContext(javax.ejb.MessageDrivenContext ctx) {
		fMessageDrivenCtx = ctx;
	}

	/**
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(javax.jms.Message pdMessage) {
		try {
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("GOT MESSAGE");
			}
			JMSObjectMessage pdObjectMessage = (JMSObjectMessage) pdMessage;
			Object pdInputObject = pdObjectMessage.getObject();
			

			if (pdInputObject instanceof DocMQMessage) {

				DocMQMessage udMessage = (DocMQMessage) pdInputObject;

				if (udMessage != null) {
					String strDocId = udMessage.getDocId();
					String strProjId = udMessage.getProjId();
					String strDocFileNames = udMessage.getFileNames();
					String strDocFileSizes = udMessage.getFileSizes();

				if (!isNullorEmpty(strDocId)
					&& !isNullorEmpty(strProjId)) {

				int iDocId = Integer.parseInt(strDocId);
					DocumentEJBDAO udDAO = new DocumentEJBDAO();
				udDAO.prepare();

						ETSDocEditHistory etsDocEditHistory = new ETSDocEditHistory();
						String strFileNames = "New Attachment(s) added--->  ";
						boolean bHasFiles = (!StringUtil.isNullorEmpty(strDocFileNames) && !StringUtil.isNullorEmpty(strDocFileSizes));
						if (bHasFiles) {
						if (strDocFileNames.indexOf(MSG_SEP) == -1
							|| strDocFileSizes.indexOf(MSG_SEP) == -1) {
							// Means there is only one record to be added
							udDAO.addDocFile(
								iDocId,
								strDocFileNames,
								Integer.parseInt(strDocFileSizes),
								null,
								udMessage.getFileDescription(),
								udMessage.getFileStatus());
							strFileNames = strFileNames + strDocFileNames+",";
						} else {
				StringTokenizer strNameTokens =
						new StringTokenizer(strDocFileNames, MSG_SEP);

				StringTokenizer strSizeTokens =
						new StringTokenizer(strDocFileSizes, MSG_SEP);

				int iTokenLength = strNameTokens.countTokens();
				if (iTokenLength == strSizeTokens.countTokens()) {

					for (int iCounter = 0;
						iCounter < iTokenLength;
						iCounter++) {
									String strFileName =
										strNameTokens.nextToken();
									String strFileSize =
										strSizeTokens.nextToken();

						// Store the meta data of the file in the BLD
						// database. Only the actual file is not stored here.
									strFileNames = strFileNames + strFileName+",";
						udDAO.addDocFile(
							iDocId,
							strFileName,
							Integer.parseInt(strFileSize),
										null,
										udMessage.getFileDescription(),
										udMessage.getFileStatus());
					}
							}
						}
						}

				udDAO.updateDocStatus(
					iDocId,
					strProjId,
							ITAR_STATUS_COMPLETE);

								// Send Email to Notification List
								DocReaderDAO udReaderDAO = new DocReaderDAO();
								udReaderDAO.setConnection(
									udDAO.getConnection());

								ETSDoc udDoc =
									udReaderDAO.getDocByIdAndProject(
										iDocId,
										strProjId,
										true);
								
								try {
									udDoc.setUpdatedBy(udReaderDAO.getUsersName(udMessage.getUserId()));
								}
								catch(Exception e) {
								    // Consume exception here.....
								    
								}
								//Once files details have been added to the doc
								// If the doc is a meeting doc, we also need to
								// update the name, description and keywords.
								if ("MEETINGS".equalsIgnoreCase(udMessage.getFileDescription())) {
								    udDoc.setName(udMessage.getDocName());
								    if (!StringUtil.isNullorEmpty(udMessage.getDocDescription())) {
								        udDoc.setDescription(udMessage.getDocDescription());
								    }
								    if (!StringUtil.isNullorEmpty(udMessage.getKeywords())) {
								        udDoc.setKeywords(udMessage.getKeywords());
								    }
								    udDAO.updateDocProperties(udDoc);
								}
								if (!"adddoc".equalsIgnoreCase(udMessage.getFileDescription())) {
								etsDocEditHistory.setDocId(iDocId);
								etsDocEditHistory.setAction(DocConstants.ACTION_UPDATE_DOC_ATTACHMENT);
								etsDocEditHistory.setUserId( udMessage.getUserId() );
								etsDocEditHistory.setActionDetails(strFileNames);
								udDAO.setEditHistory(etsDocEditHistory);
								}
								
								Vector vtNotifyList =
									udReaderDAO.getDocNotifyList(iDocId);
								if (vtNotifyList != null
									&& vtNotifyList.size() > 0) {
									DocNotify udNotify =
										(DocNotify) vtNotifyList.get(0);
									SimpleDateFormat pdDateFormat =
										new SimpleDateFormat(
											StringUtil.DATE_FORMAT);
									Date dtUpload =
										new Date(udDoc.getUploadDate());
									String strDate =
										pdDateFormat.format(dtUpload);
									ETSCat udCat =
										udReaderDAO.getCat(udDoc.getCatId());
									String strAppName = "E&TS Connect";
									String strProjectId = udCat.getProjectId();
									int iTopCatID =
										udReaderDAO.getTopCatId(
											strProjectId,
											Defines.DOCUMENTS_VT);

									String strProjectName =
										udReaderDAO.getProjectName(
											strProjectId);
									String strMemberEmailList =
										udReaderDAO.getProjMemberEmails(
											strProjectId,
											vtNotifyList);
									if ("adddoc".equalsIgnoreCase(udMessage.getFileDescription())) {
									String strEmail =
										NotificationMsgHelper
											.createAddMessage(
												udDoc,
												strProjectName,
												strProjectId,
												strDate,
												pdDateFormat,
												udDoc.getIbmOnly(),
												strAppName,
												Integer.toString(iTopCatID),
												Integer.toString(udCat.getId()),
												Defines.LINKID)
											.toString();

									String strSubject =
										    CommonEmailHelper.IBM 
												+ strAppName
											+ " - New Document: "
											+ udDoc.getName();

										if (isNullorEmpty(udMessage.getNotifyOption())) {
										    // Means USER did not supress notification
										    // Send the Email
										    NotificationMsgHelper.sendEMail(
													udDoc.getUserId(),
													strMemberEmailList,
													"",
													"",
													NotificationMsgHelper.getMailHost(),
													strEmail,
													strSubject,
													udDoc.getUserId());
										}
									}
									else if ("addFiles".equalsIgnoreCase(udMessage.getFileDescription())) {
											String strEmail =
												NotificationMsgHelper
													.createAttachmentMessage(
														udDoc,
														Integer.toString(iTopCatID),
														Integer.toString(udCat.getId()),
														Defines.LINKID,
														strAppName,
														strProjectName,
														strDocFileNames,
														DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER
														)
													.toString();

											String strSubject =
											    CommonEmailHelper.IBM 
													+ strAppName
													+ " - Document update: "
													+ udDoc.getName();

											if (isNullorEmpty(udMessage.getNotifyOption())) {
											    // Means USER did not supress notification
											    // Send the Email
									NotificationMsgHelper.sendEMail(
										udDoc.getUserId(),
										strMemberEmailList,
										"",
										"",
										NotificationMsgHelper.getMailHost(),
										strEmail,
										strSubject,
										udDoc.getUserId());
											}

								}
									}
				udDAO.cleanup();
				}

				}
			} else {
				m_pdLog.error(
					"Object recieved was not DocMQMessage : "
						+ pdInputObject.getClass().getName());

			}

		} catch (Exception e) {
			m_pdLog.error(e);
			// Do nothing. Consume this error
		}
	}

	/**
	 * ejbCreate
	 */
	public void ejbCreate() {
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug("ejbCreate");
		}
	}
 
	/**
	 * @see javax.ejb.MessageDrivenBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException {
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug("ejbRemove");
		}
	}

	/**
	 * Check whether the Input String is Null OR an Empty String
	 * @param strInput Input String to be checked
	 * @return Null or Empty status flag
	 */
	private static boolean isNullorEmpty(String strInput) {
		boolean bIsNullOrEmpty =
			((strInput == null)
				|| (strInput.length() == 0)
				|| strInput.equalsIgnoreCase("NULL"));
		return bIsNullOrEmpty;
	}
}
