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

package oem.edge.ets.fe.documents.webservice;

import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.UserObject;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.UserRole;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocMessages;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocUpdateDAO;

import org.apache.log4j.Logger;

public class DocumentWSDLSoapBindingImpl implements
		oem.edge.ets.fe.documents.webservice.DocumentWSDL, ServiceLifecycle {

	/** Stores the Logging object */
	private Logger m_pdLog = Logger.getLogger(DocumentWSDL.class);

	private static final String GEN_ADD_ERROR = "Error adding new attachments to document: ";
	private static final String GEN_DEL_ERROR = "Error deleting attachments from document: ";
	private static final String GEN_ADDDOC_ERROR = "Error adding document: ";
	private static final String GEN_DELDOC_ERROR = "Error deleting document: ";
	private static final String GEN_UPDDOC_ERROR = "Error updating document: ";
	private static final String GEN_UPLOADHTML_ERROR = "Error uploading project status: ";
	private static final String AUTH_ERROR = "ws.auth.error";
	
	private static final String TYPE_HTML = "HTML";
	
    ServletEndpointContext ctx = null;


	
    /* (non-Javadoc)
     * @see javax.xml.rpc.server.ServiceLifecycle#init(java.lang.Object)
     */
    public void init(Object arg0) throws javax.xml.rpc.ServiceException {
        m_pdLog.error("INIT CALLED");
        ctx = (ServletEndpointContext) arg0;
    }
    
    /**
     * @return
     */
    private String getUserName() {
        String strUserName = null;
        if (ctx != null) {
        Principal princ = ctx.getUserPrincipal();
        if (princ != null) {
                strUserName = princ.getName(); 
        }
    }
        return strUserName;
    }
    
    /* (non-Javadoc)
     * @see javax.xml.rpc.server.ServiceLifecycle#destroy()
     */
    public void destroy() {
        m_pdLog.error("DESTROY CALLED");
    }
    
	/**
	 * @param strUserId
	 * @param strPassword
	 * @return
	 */
	public Workspace[] getWorkspaces(String strUserId, String strPassword)
		throws ServiceException {

		m_pdLog.error("getWorkspaces Servicing client : " + strUserId);

		Workspace[] udWorkspaces = null;

		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		try {
			udDAO.prepare();
			// Check entitlements here
			Connection pdConn = udDAO.getConnection();
			String strEdgeUserId = AccessCntrlFuncs.getEdgeUserId(pdConn,
					strUserId);
			Vector vtEnts = AccessCntrlFuncs.getUserEntitlements(pdConn,
					strEdgeUserId, true, true);

			boolean bHasETS = false;
			boolean bHasAIC = true;

			boolean bIsAdmin = false;
			boolean bIsAICAdmin = false;
			boolean bHasAicOemSales = false;
			
			if (vtEnts.contains(Defines.ETS_ADMIN_ENTITLEMENT)) {
				bIsAdmin = true;
			}
			if (vtEnts.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bIsAICAdmin = true;
			}

			if (vtEnts != null && vtEnts.size() > 0) {
				if (bIsAdmin || vtEnts.contains(Defines.ETS_ENTITLEMENT)) {
					bHasETS = true;
				}
				if (bIsAICAdmin
						|| vtEnts.contains(Defines.COLLAB_CENTER_ENTITLEMENT)) {
					bHasAIC = true;
				}
				if (vtEnts.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) 
				        || vtEnts.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT)
				        || vtEnts.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				    bHasAicOemSales = true;
				}
			}
			

			List lstWorkspaces = new ArrayList();
			
			if (bHasETS) {
			    lstWorkspaces = 
			        udDAO.getProjects(
			            strUserId, 
			            bIsAdmin);
			}
			
			if (bHasAIC) {
				List lstAICWorkspaces = 
				    udDAO.getAICProjects(
				            strUserId, 
				            bIsAICAdmin);
				lstWorkspaces.addAll(lstAICWorkspaces);
			}
			if (bHasAicOemSales && !bIsAICAdmin) {
			    List lstAICPublicWorkspaces =
			        udDAO.getAICPublicProjects(strUserId);
			    lstWorkspaces.addAll(lstAICPublicWorkspaces);
			}

			
			List lstFilteredWS = new ArrayList();
			// Filter out workspaces where user is a VISITOR.
			for (int i = 0; i < lstWorkspaces.size(); i++) {
				Workspace udWorkspace = (Workspace) lstWorkspaces.get(i);
				String strUserRole = (new UserRole()).getUserRole(pdConn,
						strUserId, udWorkspace.getWorkspaceId(), udWorkspace
								.getWorkspaceType(), udWorkspace.isITAR(),
						udWorkspace.getPrivate());
				if (!Defines.WORKSPACE_VISITOR.equals(strUserRole)) {
					lstFilteredWS.add(udWorkspace);
				} else {
					m_pdLog.error("USER IS VISITOR IN THIS WS SO SKIPPING : "
							+ udWorkspace.getWorkspaceName());
				}
			}
			udWorkspaces = new Workspace[lstFilteredWS.size()];
			lstFilteredWS.toArray(udWorkspaces);
			
		} catch (Exception e) {
			m_pdLog.error(e);
			throw new ServiceException(
				"Error fetching Workspaces from database:" + e.getMessage());
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
					"Error fetching Workspaces from database:"
						+ e.getMessage());
			}
		}

		return udWorkspaces;
	}

	/**
	 * @param strWorkspaceId
	 * @return
	 */
	public Category[] getFolders(String strWorkspaceId) throws ServiceException {
		m_pdLog.error("getFolders Servicing client : " + strWorkspaceId);

		Category[] udFolders = null;

		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		try {
			udDAO.prepare();
			List lstFolders = udDAO.getFolders(strWorkspaceId, false);
			udFolders = new Category[lstFolders.size()];
			lstFolders.toArray(udFolders);
		} catch (Exception e) {
			m_pdLog.error(e);
			throw new ServiceException("Error fetching Folders from database:"
					+ e.getMessage());
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
					"Error fetching Workspaces from database:"
						+ e.getMessage());
			}
		}

		return udFolders;
	}

	/**
	 * @param uploadFile
	 * @param strDocName
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public boolean uploadDocument(int catid, String strDocName,
			String strDocDescription, String strDocAuthor, boolean bNotifyAll,
		javax.mail.internet.MimeMultipart uploadFile)
		throws ServiceException {
	    int iNewDocID = 
	        uploadNewDocument(
	                catid, 
	                strDocName, 
	                strDocDescription, 
	                strDocAuthor, 
	                bNotifyAll, 
	                null, 
	                uploadFile); 
	    return (iNewDocID != -1);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#updateDocument(int, java.lang.String, java.lang.String, boolean, java.util.Date, javax.mail.internet.MimeMultipart)
	 */
	public boolean updateDocument(
	        int docid,
			String strDocDescription,
			String strDocAuthor,
			boolean bNotifyAll,
			Date dtExpiration,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException {
	    
	    boolean bSuccess = true;
	    DocumentServiceDAO udDAO = new DocumentServiceDAO();
	    try {
		    
			udDAO.prepare();
	        DocReaderDAO udReaderDAO = new DocReaderDAO();
			udReaderDAO.setConnection(udDAO.getConnection());
	        DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	        udUpdateDAO.setConnection(udDAO.getConnection());

			ETSDoc udDoc = udReaderDAO.getDocById(docid);
			udDoc.setDescription(strDocDescription);
			if (dtExpiration != null) {
				udDoc.setExpiryDate(dtExpiration.getTime());
			}
			udDoc.setUpdatedBy(strDocAuthor);
			bSuccess = udUpdateDAO.addDocMethod(udDoc, new ArrayList(), udDoc.getId(), false);
			int iDocID = udDoc.getId();
			if (bSuccess) {
				ETSCat udCat = udReaderDAO.getCat(udDoc.getCatId());
				String strProjectId = udCat.getProjectId();
				String strProjectName = udReaderDAO.getProjectName(strProjectId);
				int iTopCatID = udReaderDAO.getTopCatId(strProjectId,
						Defines.DOCUMENTS_VT);
				String strMemberEmailList = udReaderDAO
						.getProjMemberEmails(strProjectId);
				
				String strProjectType = udReaderDAO.getProjectType(strProjectId);
				
				ResourceBundle udResources = null;
				String strLinkID = "";
				String strAppName = Defines.LINKID;
				if (Defines.ETS_WORKSPACE_TYPE.equals(strProjectType)) {
				    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.etsbrand");
				    if (udResources != null) {
				        strAppName = udResources.getString("ets.app_name");
				        strLinkID  = udResources.getString("ets.linkid");
				    }
				}
				else if (Defines.AIC_WORKSPACE_TYPE.equals(strProjectType)) {
				    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.aicbrand");
					if (udResources != null) {
					    strAppName = udResources.getString("aic.app_name");
					    strLinkID  = udResources.getString("aic.linkid");
					}
				}
				if (uploadFile != null) {
				    int iFileCount = 0;
				    try {
				        iFileCount = uploadFile.getCount();
				    }
				    catch(javax.mail.MessagingException e) {
				        // Means this upload might not have any documents attached
				    }
					
					m_pdLog.error("iFileCount " + iFileCount);
					for (int i = 0; i < iFileCount; i++) {
						BodyPart pdBody = uploadFile.getBodyPart(i);
						DataHandler pdHandler = pdBody.getDataHandler();
						String strContentType = pdHandler.getContentType();
						String strFileName = pdBody.getFileName();
						m_pdLog.error("CONTENT TYPE IS " + strContentType);
						m_pdLog.error("CONTENT FILE NAME IS " + strFileName);
						InputStream pdInput = pdHandler.getInputStream();
						udUpdateDAO.addDocFile(iDocID, strFileName, pdInput.available(),
								pdInput);
						pdInput.close();
					}
				}
				if (bNotifyAll) {
					udDAO.addDocNotificationList(iDocID);

					SimpleDateFormat pdDateFormat = new SimpleDateFormat(
							StringUtil.DATE_FORMAT);
					Date dtUpload = new Date(udDoc.getUploadDate());
					String strDate = pdDateFormat.format(dtUpload);
					String strEmail = NotificationMsgHelper.createAddMessage(udDoc,
							strProjectName, strProjectId, strDate, pdDateFormat,
							udDoc.getIbmOnly(), strAppName,
							Integer.toString(iTopCatID),
							Integer.toString(udCat.getId()), strLinkID)
							.toString();

					String strSubject = strAppName + " - New Document: "
							+ udDoc.getName();
					NotificationMsgHelper.sendEMail(strDocAuthor,
							strMemberEmailList, "", "", NotificationMsgHelper
									.getMailHost(), strEmail, strSubject,
							strDocAuthor);

				}
			}
	    }
	    catch(Exception e) {
	        m_pdLog.error(e);
	        bSuccess = false;
	    }
	    finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error uploading document to database:"
								+ e.getMessage());
			}
	    }
	    return bSuccess;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#uploadNewDocument(int, java.lang.String, java.lang.String, java.lang.String, boolean, java.util.Date, javax.mail.internet.MimeMultipart)
	 */
	public int uploadNewDocument(
	        int catid,
			String strDocName,
			String strDocDescription,
			String strDocAuthor,
			boolean bNotifyAll,
			Date dtExpiration,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException {

	    int iDocID = -1;
		m_pdLog.error("uploadDocument Servicing client : ");
		m_pdLog.error("catid : " + catid);
		m_pdLog.error("strDocName : " + strDocName);
		m_pdLog.error("strDocDescription : " + strDocDescription);
		m_pdLog.error("strDocAuthor : " + strDocAuthor);
		m_pdLog.error("bNotifyAll : " + bNotifyAll);
		m_pdLog.error("dtExpiration : " + dtExpiration);
		DocumentServiceDAO udDAO = new DocumentServiceDAO();

		try {
			udDAO.prepare();
			String strIBMOnly = udDAO.isCatIBMOnly(catid);
			
			DocReaderDAO udReaderDAO = new DocReaderDAO();
			udReaderDAO.setConnection(udDAO.getConnection());
			DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
			udUpdateDAO.setConnection(udDAO.getConnection());

			ETSCat udCat = udReaderDAO.getCat(catid);
			String strProjectId = udCat.getProjectId();
			String strProjectName = udReaderDAO.getProjectName(strProjectId);
			int iTopCatID = udReaderDAO.getTopCatId(strProjectId,
					Defines.DOCUMENTS_VT);
			String strMemberEmailList = udReaderDAO
					.getProjMemberEmails(strProjectId);
			
			String strProjectType = udReaderDAO.getProjectType(strProjectId);
			
			ResourceBundle udResources = null;
			String strLinkID = "";
			String strAppName = Defines.LINKID;
			if (Defines.ETS_WORKSPACE_TYPE.equals(strProjectType)) {
			    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.etsbrand");
			    if (udResources != null) {
			        strAppName = udResources.getString("ets.app_name");
			        strLinkID  = udResources.getString("ets.linkid");
			    }
			}
			else if (Defines.AIC_WORKSPACE_TYPE.equals(strProjectType)) {
			    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.aicbrand");
				if (udResources != null) {
				    strAppName = udResources.getString("aic.app_name");
				    strLinkID  = udResources.getString("aic.linkid");
				}
			}

			iDocID = udDAO.addDoc(catid, strDocName, strDocDescription,
					strDocAuthor, strIBMOnly, strProjectId, 0, dtExpiration);

			if (uploadFile != null) {
			    int iFileCount = 0;
			    try {
			        iFileCount = uploadFile.getCount();
			    }
			    catch(javax.mail.MessagingException e) {
			        // Means this upload might not have any documents attached
			    }
				
				m_pdLog.error("iFileCount " + iFileCount);
				for (int i = 0; i < iFileCount; i++) {
					BodyPart pdBody = uploadFile.getBodyPart(i);
					DataHandler pdHandler = pdBody.getDataHandler();
					String strContentType = pdHandler.getContentType();
					String strFileName = pdBody.getFileName();
					m_pdLog.error("CONTENT TYPE IS " + strContentType);
					m_pdLog.error("CONTENT FILE NAME IS " + strFileName);
					InputStream pdInput = pdHandler.getInputStream();
					udUpdateDAO.addDocFile(iDocID, strFileName, pdInput.available(),
						pdInput);
					pdInput.close();
				}
			}

			if (bNotifyAll) {
				udDAO.addDocNotificationList(iDocID);

				ETSDoc udDoc = udReaderDAO.getDocByIdAndProject(iDocID,
						strProjectId, false);
				SimpleDateFormat pdDateFormat = new SimpleDateFormat(
						StringUtil.DATE_FORMAT);
				Date dtUpload = new Date(udDoc.getUploadDate());
				String strDate = pdDateFormat.format(dtUpload);
				String strEmail = NotificationMsgHelper.createAddMessage(udDoc,
						strProjectName, strProjectId, strDate, pdDateFormat,
						udDoc.getIbmOnly(), strAppName,
					Integer.toString(iTopCatID),
						Integer.toString(udCat.getId()), strLinkID)
						.toString();

				String strSubject = strAppName + " - New Document: "
						+ udDoc.getName();
				NotificationMsgHelper.sendEMail(strDocAuthor,
						strMemberEmailList, "", "", NotificationMsgHelper
								.getMailHost(), strEmail, strSubject,
					strDocAuthor);
				
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error uploading document to database:"
								+ e.getMessage());
			}
		}

		return iDocID;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#addAttachment(int, boolean, javax.mail.internet.MimeMultipart)
	 */
	public boolean addAttachment(
	        int docid,
	        boolean bNotify,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException {
	    boolean bSuccess = false;
		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
		try {
			if (uploadFile != null) {
			    udDAO.prepare();
			    udUpdateDAO.setConnection(udDAO.getConnection());
			    int iFileCount = 0;
			    try {
			        iFileCount = uploadFile.getCount();
			    }
			    catch(javax.mail.MessagingException e) {
			        // Means this upload might not have any documents attached
			    }
				
				udReaderDAO.setConnection(udDAO.getConnection());
				ETSDoc udDoc = udReaderDAO.getDocById(docid);
				if (udDoc == null) {
				    // Means there is no document with this ID
					throw new ServiceException(GEN_ADD_ERROR + DocMessages.getMessage("ws.docid.invalid"));
				}
				else {
				    //Authorize user for this document.
				    Workspace udWorkspace = udDAO.getWorkspaceDetails(udDoc.getProjectId());
				    if (DocumentWSHelper.isWorkflowProject(udDoc.getProjectId(), udReaderDAO)) {
						throw new ServiceException(
						        GEN_ADD_ERROR + DocMessages.getMessage("ws.workflow.error"));
				    }
				    else if (udWorkspace.isITAR()) {
						throw new ServiceException(
						        GEN_ADD_ERROR + DocMessages.getMessage("ws.addattach.itar"));
				    }
				    else {
					    String strUserRole = 
					        (new UserRole()).getUserRole(
					                udDAO.getConnection(),
					                getUserName(), 
					                udWorkspace.getWorkspaceId(), 
					                udWorkspace.getWorkspaceType(), 
					                udWorkspace.isITAR(),
					                udWorkspace.getPrivate());
					    if (strUserRole.equals(Defines.WORKSPACE_VISITOR) 
					            || strUserRole.equals(Defines.ETS_EXECUTIVE) 
					            || strUserRole.equals(Defines.INVALID_USER)) {
							throw new ServiceException(
							        GEN_ADD_ERROR 
							        	+ DocMessages.getMessage(AUTH_ERROR));
					    }
					    else {
					        // Check if user is in approved list of editors for this document
					        boolean bIsEditor = 
					            udReaderDAO.checkDocumentEditPriv(docid, getUserName(), udDoc.getProjectId())
					            || udReaderDAO.checkDocumentEditPrivForGroup(docid, getUserName(), udDoc.getProjectId())
					            || getUserName().equals(udDoc.getUserId());
					        if (!bIsEditor) {
								throw new ServiceException(
								        GEN_ADD_ERROR 
								        	+ DocMessages.getMessage(AUTH_ERROR));
					        }
					        else {
							    List lstDocFiles = udDoc.getDocFiles();
							    m_pdLog.error("iFileCount " + iFileCount);
							    List lstExistingFiles = new ArrayList();
							    List lstAddedFiles = new ArrayList();
							    StringBuffer strFileNames = new StringBuffer(StringUtil.EMPTY_STRING);
							    StringBuffer strAttachmentNames = new StringBuffer(StringUtil.EMPTY_STRING);
								for (int i = 0; i < iFileCount; i++) {
									BodyPart pdBody = uploadFile.getBodyPart(i);
									DataHandler pdHandler = pdBody.getDataHandler();
									String strContentType = pdHandler.getContentType();
									String strFileName = pdBody.getFileName();
									m_pdLog.error("CONTENT TYPE IS " + strContentType);
									m_pdLog.error("CONTENT FILE NAME IS " + strFileName);
									// Check if file with this name already exists
									if (DocumentWSHelper.checkExistingFiles(lstDocFiles, strFileName) || lstAddedFiles.contains(strFileName)) {
									    lstExistingFiles.add(strFileName);
									}
									else {
										InputStream pdInput = pdHandler.getInputStream();
										int iSize = pdInput.available();
										if (iSize == 0 || iSize > DocConstants.MAX_FILE_SIZE) {
											throw new ServiceException(
											        GEN_ADD_ERROR 
											        	+ DocMessages.getMessage("ws.attach.filesize"));
										}
										bSuccess = 
										    udUpdateDAO.addDocFile(docid, strFileName, iSize,pdInput);
										pdInput.close();
										if (bSuccess) {
										    strFileNames.append(strFileName);
										    strAttachmentNames.append(strFileName);
										    if (i < iFileCount - 1) {
										        strFileNames.append(StringUtil.COMMA);
										        strAttachmentNames.append(":");
										    }
										    lstAddedFiles.add(strFileName);
										}
									}
								}
								ETSDocEditHistory etsDocEditHistory = new ETSDocEditHistory();
								etsDocEditHistory.setDocId(docid);
								etsDocEditHistory.setAction(DocConstants.ACTION_UPDATE_DOC_ATTACHMENT);
								etsDocEditHistory.setUserId( getUserName() );
								etsDocEditHistory.setActionDetails("Attachment(s) added--->  " + strFileNames.toString());
								udUpdateDAO.setEditHistory(etsDocEditHistory);
								if (bNotify) {
								    DocumentWSHelper.handleAttachmentNotification(
							            udReaderDAO, 
							            udWorkspace, 
							            udDoc, 
							            strAttachmentNames.toString(), 
							            getUserName(),
							            DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER);
								}
								
								if (lstExistingFiles.size() > 0) {
								    // Means some files already existed
								    StringBuffer strExceptionBuffer = new StringBuffer("");
								    strExceptionBuffer.append(
								            GEN_ADD_ERROR 
								            + "Following attachments already exist in the document : ");
								    for(int i=0; i < lstExistingFiles.size(); i++) {
								        strExceptionBuffer.append(lstExistingFiles.get(i));
								        if (i < lstExistingFiles.size()-1) {
								            strExceptionBuffer.append(", ");
								        }
								    }
								    throw new ServiceException(strExceptionBuffer.toString());
								}
					        }
					    }
				    }
				}
			}
		} catch (Exception e) {
            m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                throw new ServiceException(e.getMessage());
            }
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(GEN_ADD_ERROR + e.getMessage());
			}
		}
		return bSuccess;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#deleteAttachment(int, boolean, String)
	 */
	public boolean deleteAttachment(
	        int docid,
	        boolean bNotify,
			String []filenames) throws ServiceException {
	    boolean bSuccess = false;
		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		try {
		    udDAO.prepare();
			udReaderDAO.setConnection(udDAO.getConnection());
			ETSDoc udDoc = udReaderDAO.getDocById(docid);
			if (udDoc == null) {
			    // Means there is no document with this ID
				throw new ServiceException(GEN_DEL_ERROR + DocMessages.getMessage("ws.docid.invalid"));
			}
			else {
			    //Authorize user for this document.
			    Workspace udWorkspace = udDAO.getWorkspaceDetails(udDoc.getProjectId());
			    if (DocumentWSHelper.isWorkflowProject(udDoc.getProjectId(), udReaderDAO)) {
					throw new ServiceException(
					        GEN_DEL_ERROR + DocMessages.getMessage("ws.workflow.error"));
			    }
			    if (udWorkspace.isITAR()) {
					throw new ServiceException(
					        GEN_DEL_ERROR + DocMessages.getMessage("ws.delattach.itar"));
			    }
			    else {
				    String strUserRole = 
				        (new UserRole()).getUserRole(
				                udDAO.getConnection(),
				                getUserName(), 
				                udWorkspace.getWorkspaceId(), 
				                udWorkspace.getWorkspaceType(), 
				                udWorkspace.isITAR(),
				                udWorkspace.getPrivate());
				    if (strUserRole.equals(Defines.WORKSPACE_VISITOR) 
				            || strUserRole.equals(Defines.ETS_EXECUTIVE) 
				            || strUserRole.equals(Defines.INVALID_USER)) {
						throw new ServiceException(
						        GEN_DEL_ERROR + DocMessages.getMessage(AUTH_ERROR));
				    }
				    else {
				        // Check if user is in approved list of editors for this document
				        boolean bIsEditor = 
				            udReaderDAO.checkDocumentEditPriv(docid, getUserName(), udDoc.getProjectId())
				            || udReaderDAO.checkDocumentEditPrivForGroup(docid, getUserName(), udDoc.getProjectId())
			            	|| getUserName().equals(udDoc.getUserId());
				        if (!bIsEditor) {
							throw new ServiceException(
							        GEN_DEL_ERROR 
							        	+ DocMessages.getMessage(AUTH_ERROR));
				        }
				        else {
						    List lstDocFiles = udDoc.getDocFiles();
						    List lstMissingFiles = new ArrayList();
						    StringBuffer strFileNames = new StringBuffer(StringUtil.EMPTY_STRING);
						    StringBuffer strAttachmentNames = new StringBuffer(StringUtil.EMPTY_STRING);
						    int iFileCount = filenames.length;
						    for(int i=0; i < iFileCount; i++) {
						        String strFileName = filenames[i];
								if (DocumentWSHelper.checkExistingFiles(lstDocFiles, strFileName)) {
								    bSuccess = udDAO.deleteAttachment(docid, strFileName);
							    if (bSuccess) {
									    strFileNames.append(strFileName);
									    strAttachmentNames.append(strFileName);
									    if (i < iFileCount - 1) {
									        strFileNames.append(StringUtil.COMMA);
									        strAttachmentNames.append(":");
									    }
									}
								}
								else {
								    lstMissingFiles.add(strFileName);
								}
						    }
						    if (strFileNames.length() > 0) {
								    ETSDocEditHistory etsDocEditHistory = new ETSDocEditHistory();
									etsDocEditHistory.setDocId(docid);
									etsDocEditHistory.setAction("Delete Attachment");
								etsDocEditHistory.setUserId( getUserName() );
								etsDocEditHistory.setActionDetails("Attachment(s) deleted ---> " + strFileNames.toString());
									DocUpdateDAO udUpdater = new DocUpdateDAO();
									udUpdater.setConnection(udDAO.getConnection());
									udUpdater.setEditHistory(etsDocEditHistory);
									
									if (bNotify) {
								    DocumentWSHelper.handleAttachmentNotification(
								            udReaderDAO, 
								            udWorkspace, 
								            udDoc, 
							            strAttachmentNames.toString(), 
							            getUserName(),
								            DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER);
									}
							    }
							if (lstMissingFiles.size() > 0) {
							    // Means some files didn't exist
							    StringBuffer strExceptionBuffer = new StringBuffer("");
							    strExceptionBuffer.append(
							            GEN_DEL_ERROR 
							            + "Following attachments were not found for the document : ");
							    for(int i=0; i < lstMissingFiles.size(); i++) {
							        strExceptionBuffer.append(lstMissingFiles.get(i));
							        if (i < lstMissingFiles.size()-1) {
							            strExceptionBuffer.append(", ");
							}
							    }
							    throw new ServiceException(strExceptionBuffer.toString());
							}
				        }
				    }
			    }
			}
		} catch (Exception e) {
            m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                throw new ServiceException(e.getMessage());
            }
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(GEN_DEL_ERROR + e.getMessage());
			}
		}
		return bSuccess;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#getAllFolders(java.lang.String)
	 */
	public Category[] getAllFolders(String strWorkspaceId) 
		throws ServiceException {

	    Category[] udFolders = null;
		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		DocReaderDAO udReader = new DocReaderDAO();
		try {
			udDAO.prepare();
			udReader.setConnection(udDAO.getConnection());
		    //Authorize user for this workapce.
		    Workspace udWorkspace = udDAO.getWorkspaceDetails(strWorkspaceId);
		    if (DocumentWSHelper.isWorkflowProject(strWorkspaceId, udReader)) {
				throw new ServiceException(
				        GEN_DELDOC_ERROR + DocMessages.getMessage("ws.workflow.error"));
		    }
		    String strUserRole = 
		        (new UserRole()).getUserRole(
		                udDAO.getConnection(),
		                getUserName(), 
		                udWorkspace.getWorkspaceId(), 
		                udWorkspace.getWorkspaceType(), 
		                udWorkspace.isITAR(),
		                udWorkspace.getPrivate());
		    if (strUserRole.equals(Defines.INVALID_USER)) {
				throw new ServiceException(
				        DocMessages.getMessage(AUTH_ERROR));
		    }
		    else {
				List lstFolders = udDAO.getFolders(strWorkspaceId, true);
				udFolders = new Category[lstFolders.size()];
				lstFolders.toArray(udFolders);
		        // Check if user is IBMer or non-IBMer.
		        // Only Internal users can access folders with IBM_ONLY = 1/2
				UserObject udUser = 
				    AccessCntrlFuncs.getUserObject(
				            udDAO.getConnection(), 
				            getUserName(), true, false);
				if (udUser.gIBM_INTERNAL != 1) {
				    List lstFilteredFolders = new ArrayList();
				    for(int i=0; i < lstFolders.size(); i++) {
				        Category tmpCat = (Category) lstFolders.get(i);
				        if (!tmpCat.isIBMOnly()) {
				            lstFilteredFolders.add(tmpCat);
				        }
				    }
				    udFolders = new Category[lstFilteredFolders.size()];
				    lstFilteredFolders.toArray(udFolders);
				}
		    }
		} catch (Exception e) {
			m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                throw new ServiceException(e.getMessage());
            }
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error fetching folders from database:"
								+ e.getMessage());
			}
		}

		return udFolders;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#deleteDocument(int)
	 */
	public boolean deleteDocument(int iDocId) 
		throws ServiceException {
	    boolean bSuccess = false;
	    
		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		try {
			udDAO.prepare();
			DocReaderDAO udReader = new DocReaderDAO();
			udReader.setConnection(udDAO.getConnection());
			ETSDoc udDoc = udReader.getDocById(iDocId);
			if (udDoc == null || udDoc.isDeleted()) {
			    throw new ServiceException(
			            GEN_DELDOC_ERROR + DocMessages.getMessage("ws.docid.invalid"));
			}
			else {
			    //Authorize user for this workapce.
			    Workspace udWorkspace = udDAO.getWorkspaceDetails(udDoc.getProjectId());
			    if (DocumentWSHelper.isWorkflowProject(udDoc.getProjectId(), udReader)) {
					throw new ServiceException(
					        GEN_DELDOC_ERROR + DocMessages.getMessage("ws.workflow.error"));
			    }
			    String strUserRole = 
			        (new UserRole()).getUserRole(
			                udDAO.getConnection(),
			                getUserName(), 
			                udWorkspace.getWorkspaceId(), 
			                udWorkspace.getWorkspaceType(), 
			                udWorkspace.isITAR(),
			                udWorkspace.getPrivate());
			    if (strUserRole.equals(Defines.INVALID_USER)) {
					throw new ServiceException(
					        GEN_DELDOC_ERROR + DocMessages.getMessage(AUTH_ERROR));
			    }
			    else { 
			        if (!strUserRole.equals(Defines.ETS_ADMIN)) {
				        // Check if user is owner of document
				        if (!udDoc.getUserId().equals(getUserName())) {
							throw new ServiceException(
							        GEN_DELDOC_ERROR + DocMessages.getMessage(AUTH_ERROR));
				        }
				        boolean bIsEditor = 
				            udReader.checkDocumentEditPriv(iDocId, getUserName(), udDoc.getProjectId())
				            || udReader.checkDocumentEditPrivForGroup(iDocId, getUserName(), udDoc.getProjectId())
			            	|| getUserName().equals(udDoc.getUserId());
				        if (!bIsEditor) {
							throw new ServiceException(
							        GEN_DELDOC_ERROR 
							        	+ DocMessages.getMessage(AUTH_ERROR));
				        }
				        else {
					        DocUpdateDAO udUpdate = new DocUpdateDAO();
					        udUpdate.setConnection(udDAO.getConnection());
					        bSuccess = udUpdate.delDoc(udDoc, getUserName(), true);
				        }
			        }
			    }
			}
		} catch (Exception e) {
			m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                throw new ServiceException(e.getMessage());
            }
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
				        GEN_DELDOC_ERROR + e.getMessage());
			}
		}
	    
	    return bSuccess;
	}
	
    /* (non-Javadoc)
     * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#uploadProjectStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.mail.internet.MimeMultipart)
     */
    public boolean uploadProjectStatus(String strProjectId, String strCompany,
            String strTabName, String strSource, MimeMultipart uploadFile)
            throws ServiceException {
        boolean bSuccess = false;
        if (m_pdLog.isDebugEnabled()) {
            m_pdLog.debug("CHECK uploadXMLData incoming parameters");
            m_pdLog.debug("strProjectId : " + strProjectId);
            m_pdLog.debug("strCompany : " + strCompany);
            m_pdLog.debug("strTabName : " + strTabName);
            m_pdLog.debug("strSource : " + strSource);
        }
        List lstDocFiles = null;
        DocumentServiceDAO udDAO = new DocumentServiceDAO(); 
        DocReaderDAO udReader = new DocReaderDAO();
        DocUpdateDAO udUpdate = new DocUpdateDAO();
        try {
            udDAO.prepare();
            udUpdate.setConnection(udDAO.getConnection());
            udReader.setConnection(udDAO.getConnection());
		    Workspace udWorkspace = udDAO.getWorkspaceDetails(strProjectId);
		    if (udWorkspace == null) {
				throw new ServiceException(
				        GEN_UPLOADHTML_ERROR 
				        	+ DocMessages.getMessage("ws.projectid.invalid"));
		    }
		    else if (DocumentWSHelper.isWorkflowProject(strProjectId, udReader)) {
				throw new ServiceException(
				        GEN_UPLOADHTML_ERROR + DocMessages.getMessage("ws.workflow.error"));
		    }
		    
		    String strUserRole = 
		        (new UserRole()).getUserRole(
		                udDAO.getConnection(),
		                getUserName(), 
		                udWorkspace.getWorkspaceId(), 
		                udWorkspace.getWorkspaceType(), 
		                udWorkspace.isITAR(),
		                udWorkspace.getPrivate());
		    if (strUserRole.equals(Defines.INVALID_USER) 
		            || strUserRole.equals(Defines.ETS_EXECUTIVE) 
		            || strUserRole.equals(Defines.WORKSPACE_VISITOR)) {
				throw new ServiceException(
				        GEN_UPLOADHTML_ERROR 
				        	+ DocMessages.getMessage(AUTH_ERROR));
		    }
            lstDocFiles = DocumentWSHelper.getDocFiles(uploadFile, 0);
            if (lstDocFiles == null || lstDocFiles.size() == 0) {
                // Means user has not sent in the HTML file
				throw new ServiceException(
				        GEN_UPLOADHTML_ERROR 
				        	+ DocMessages.getMessage("ws.projstatus.notfound"));
            }
            else if (lstDocFiles.size() > 1) {
                // Means user has sent in more than 1 file.
				throw new ServiceException(
				        GEN_UPLOADHTML_ERROR 
				        	+ DocMessages.getMessage("ws.projstatus.multiple"));
            }
            else {
                // Check for company match
                if (!udWorkspace.getCompany().equalsIgnoreCase(strCompany)) {
                    // Means company names did not match.
    				throw new ServiceException(
    				        GEN_UPLOADHTML_ERROR 
    				        	+ DocMessages.getMessage("ws.projstatus.company.mismatch"));
                }
                
                // Check that DEST_ID AND SOURCE_ID are not blank
                if (StringUtil.isNullorEmpty(strTabName)) {
    				throw new ServiceException(
    				        GEN_UPLOADHTML_ERROR 
    				        	+ DocMessages.getMessage("ws.projstatus.destid.empty"));
                }
                else if (StringUtil.isNullorEmpty(strSource)) {
    				throw new ServiceException(
    				        GEN_UPLOADHTML_ERROR 
    				        	+ DocMessages.getMessage("ws.projstatus.srcid.empty"));
                }
                
                // If we reach here means everything was successful.
                ETSDocFile udDocFile = (ETSDocFile) lstDocFiles.get(0);
                InputStream pdInput = udDocFile.getInputStream();
                int iSize = udDocFile.getFileSize();
                if (iSize > 1048576) {
                    // Means file size is greater than 1 MB. Throw exception
    				throw new ServiceException(
    				        GEN_UPLOADHTML_ERROR 
    				        	+ DocMessages.getMessage("ws.projstatus.filesize"));
                }
                else if (iSize == 0) {
    				throw new ServiceException(
    				        GEN_UPLOADHTML_ERROR 
    				        	+ DocMessages.getMessage("ws.projstatus.fileempty"));
                }
                
                // First delete existing project status
                udUpdate.deleteExistingProjectStatus(strProjectId, strSource, strTabName);
                bSuccess = 
                    udUpdate.uploadProjectStatus(
                        strProjectId,
                        strSource,
                        strTabName,
                        TYPE_HTML,
                        pdInput,
                        iSize,
                        getUserName()
                        );
            }
            
        }
        catch(Exception e) {
            m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                e.printStackTrace(System.out);
                throw new ServiceException(e.getMessage());
            }
			}
        finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error adding document:" + e.getMessage());
	    }
	    }
        return bSuccess;
    }

    /* (non-Javadoc)
     * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#addDocument(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, int, java.lang.String[], java.lang.String[], boolean, java.lang.String[], javax.mail.internet.MimeMultipart)
     */
    public int addDocument(
            int catid, 
            String name, 
            String description,
            String keywords, 
            Date expirationDate, 
            int securityClassification,
            boolean notifyAll, 
	        String []additionalEditors,
	        String []additionalReaders,
	        String []notificationList,
	        String notifyOption,
            MimeMultipart uploadFile) throws ServiceException {
        int iDocID = -1;
        
        // Get the workspace for the project and check if user has permission
        DocumentServiceDAO udDAO = new DocumentServiceDAO();
        DocReaderDAO udReader = new DocReaderDAO();
        DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
        try {
            udDAO.prepare();
            udReader.setConnection(udDAO.getConnection());
            udUpdateDAO.setConnection(udDAO.getConnection());
            ETSCat udCategory = udReader.getCat(catid);
            if (udCategory == null) {
                //Means the catid provided does not exist
				throw new ServiceException(
				        GEN_ADDDOC_ERROR 
				        	+ DocMessages.getMessage("ws.folderid.invalid"));
            }
            String strProjectId = udCategory.getProjectId();
		    if (DocumentWSHelper.isWorkflowProject(strProjectId, udReader)) {
				throw new ServiceException(
				        GEN_ADDDOC_ERROR + DocMessages.getMessage("ws.workflow.error"));
		    }
		    Workspace udWorkspace = udDAO.getWorkspaceDetails(strProjectId);
		    String strUserRole = 
		        (new UserRole()).getUserRole(
		                udDAO.getConnection(),
		                getUserName(), 
		                udWorkspace.getWorkspaceId(), 
		                udWorkspace.getWorkspaceType(), 
		                udWorkspace.isITAR(),
		                udWorkspace.getPrivate());
		    if (strUserRole.equals(Defines.INVALID_USER) 
		            || strUserRole.equals(Defines.ETS_EXECUTIVE) 
		            || strUserRole.equals(Defines.WORKSPACE_VISITOR)) {
				throw new ServiceException(
				        GEN_ADDDOC_ERROR 
				        	+ DocMessages.getMessage(AUTH_ERROR));
		    }
		    else { 
		        // Means user has proper entitlement to access this workspace.
		        // Check if folder user is trying to add to is IBM Only. If it
		        // is check if the user is internal.
		        if (udCategory.isIbmOnlyOrConf()) {
					UserObject udUser = 
					    AccessCntrlFuncs.getUserObject(
					            udDAO.getConnection(), 
					            getUserName(), true, false);
					if (udUser.gIBM_INTERNAL != 1) {
					    // Means user is external. Throw exception
						throw new ServiceException(
						        GEN_ADDDOC_ERROR 
						        	+ DocMessages.getMessage(AUTH_ERROR));
					}
		        }
		        // If we reach here, means user has correct privliges to add
		        // a document to the folder. Begin data validation
		        DocumentWSHelper.validateDoc(GEN_ADDDOC_ERROR, name, -1, expirationDate, securityClassification, udReader, udCategory);
 			    
				ETSDoc udDoc = new ETSDoc();
				udDoc.setName(name);
				udDoc.setDescription(description);
				udDoc.setKeywords(keywords);
				udDoc.setCatId(catid);
				udDoc.setDocType(Defines.DOC);
				udDoc.setIbmOnly(String.valueOf(securityClassification));
				udDoc.setDocHits(0);

				Vector vtReaderUsers = new Vector();
				Vector vtReaderGroups = new Vector();
				Vector vtGroups = new Vector();
				Vector vtUsers = new Vector();
				HashMap pdGroupUsers = new HashMap();
				String []strGroups = 
				    DocumentWSHelper.prepareLists(
				        udReader, strProjectId, getUserName(), 
				        additionalEditors, additionalReaders, 
				        notificationList, GEN_ADDDOC_ERROR, vtReaderUsers, 
				        vtReaderGroups, vtGroups, vtUsers, pdGroupUsers);
				
				if (vtReaderUsers.size() > 0 || vtReaderGroups.size() > 0) {
				    udDoc.setDPrivate(true);
				}
				else {
				    udDoc.setDPrivate(false);
				}
				if (expirationDate != null) {
					udDoc.setExpiryDate(expirationDate.getTime());
				}
				udDoc.setUserId(getUserName());
				udReader.populateUserName(udDoc);
				udDoc.setUploadDate(new Date().getTime());
				udDoc.setProjectId(strProjectId);
				List lstDocFiles = DocumentWSHelper.getDocFiles(uploadFile, DocConstants.MAX_FILE_SIZE);
			    udUpdateDAO.addDocMethod(udDoc, lstDocFiles, -1, false);
			    iDocID = udDoc.getId();
			    
			    //Once Doc has been added, handle editors and readers
			    if (vtGroups.size() > 0) {
			        udUpdateDAO.addDocResGroupsEdit(vtGroups, iDocID, strProjectId);
			    }
			    if (vtUsers.size() > 0) {
				    udUpdateDAO.addAdditionalEditors(
		                    vtUsers, 
		                    String.valueOf(iDocID), 
		                    strProjectId);
			    }

			    if (vtReaderGroups.size() > 0) {
			        udUpdateDAO.addDocResGroups(vtGroups, iDocID, strProjectId);
			    }
			    if (vtReaderUsers.size() > 0) {
				    udUpdateDAO.addDocResUsers(
				            vtReaderUsers, 
		                    String.valueOf(iDocID), 
		                    strProjectId);
			    }
			    // Once editors and readers have been added to DB, popuplate
			    // vtReaderUsers and vtEditorUsers with all users from the groups
			    // as well.
			    for(int i=0; i < vtGroups.size(); i++) {
			        vtUsers.addAll((List) pdGroupUsers.get(vtGroups.get(i)));
			    }
			    for(int i=0; i < vtReaderGroups.size(); i++) {
			        vtReaderUsers.addAll((List) pdGroupUsers.get(vtReaderGroups.get(i)));
			    }
			    //Reader list should also have the author
			    vtReaderUsers.add(getUserName());
			    
			    //Finally handle notification
			    DocumentWSHelper.popuplateWorkspaceDetailsFromProps(udReader, strProjectId, udWorkspace);
			    String strNotifyAllFlag = (notifyAll) ? DocConstants.IND_YES : null;
			    Vector vtNotifyUsers = 
				    NotificationMsgHelper.performAddNotification( 
				    		strNotifyAllFlag,
				    		notifyOption,
				    		Integer.toString(udWorkspace.getTopCatID()),
				    		Integer.toString(udDoc.getCatId()),
				    		udWorkspace.getLinkID(),
				    		notificationList,
				    		udDoc.IsDPrivate() ? DocConstants.DOC_RESTRICTED : DocConstants.DOC_UNRESTRICTED,
				    		vtReaderUsers,
				    		vtUsers,
				    		strProjectId,
				    		udWorkspace.getWorkspaceName(),
				    		udWorkspace.getWorkspaceType(),
				    		false,
				    		udWorkspace.getAppName(),
				    		udDoc,
				    		getUserName(),
				    		udReader,
				    		true);
			    NotificationMsgHelper.finalizeNotificationList(strGroups, vtNotifyUsers, udReader);
			    udUpdateDAO.addDocNotificationList(udDoc.getId(), notifyAll, vtNotifyUsers, strGroups);
		    }
        }
        catch(Exception e) {
            m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                e.printStackTrace(System.out);
                throw new ServiceException(e.getMessage());
            }
        }
        finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error adding document:" + e.getMessage());
			}
        }
        return iDocID;
    }
    
    /* (non-Javadoc)
     * @see oem.edge.ets.fe.documents.webservice.DocumentWSDL#updateDocumentProperties(int, java.lang.String, java.lang.String, java.lang.String, java.util.Date, int, boolean, java.lang.String[], java.lang.String[], java.lang.String[])
     */
    public boolean updateDocumentProperties(
            int docid, 
            String name, 
            String description,
            String keywords, 
            Date expirationDate, 
            int securityClassification,
            boolean notifyAll, 
	        String []additionalEditors,
	        String []additionalReaders,
	        String []notificationList) throws ServiceException {
        // Get the workspace for the project and check if user has permission
        boolean bSuccess = false;
        DocumentServiceDAO udDAO = new DocumentServiceDAO();
        DocReaderDAO udReader = new DocReaderDAO();
        DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
        try {
            udDAO.prepare();
            udReader.setConnection(udDAO.getConnection());
            udUpdateDAO.setConnection(udDAO.getConnection());
            ETSDoc udDoc = udReader.getDocById(docid);
            if (udDoc == null) {
                //Means the docid provided does not exist
				throw new ServiceException(
				        GEN_UPDDOC_ERROR 
				        	+ DocMessages.getMessage("ws.docid.invalid"));
            }
            String strProjectId = udDoc.getProjectId();
		    Workspace udWorkspace = udDAO.getWorkspaceDetails(strProjectId);
		    if (DocumentWSHelper.isWorkflowProject(udDoc.getProjectId(), udReader)) {
				throw new ServiceException(
				        GEN_UPDDOC_ERROR + DocMessages.getMessage("ws.workflow.error"));
		    }
		    String strUserRole = 
		        (new UserRole()).getUserRole(
		                udDAO.getConnection(),
		                getUserName(), 
		                udWorkspace.getWorkspaceId(), 
		                udWorkspace.getWorkspaceType(), 
		                udWorkspace.isITAR(),
		                udWorkspace.getPrivate());
		    if (strUserRole.equals(Defines.INVALID_USER) 
		            || strUserRole.equals(Defines.ETS_EXECUTIVE) 
		            || strUserRole.equals(Defines.WORKSPACE_VISITOR)) {
				throw new ServiceException(
				        GEN_UPDDOC_ERROR 
				        	+ DocMessages.getMessage(AUTH_ERROR));
		    }
		    else { 
	            // Check if user has edit privilige for the document
		        if (udReader.checkDocumentEditPriv(docid, getUserName(), strProjectId) 
		                || udReader.checkDocumentEditPrivForGroup(docid, getUserName(), strProjectId) 
		                || getUserName().equalsIgnoreCase(udDoc.getUserId())) {
		            ETSCat udCategory = udReader.getCat(udDoc.getCatId());
		            DocumentWSHelper.validateDoc(GEN_UPDDOC_ERROR, name, docid, expirationDate, securityClassification, udReader, udCategory);
		            
		            //Extra validation - if Document is already IBM ONLY PERM
		            // Then security classification cannot be changed.
		            if ((udDoc.getIBMOnlyStr().equals(DocConstants.ETS_IBM_CONF)) 
		                    && (securityClassification == 0 || securityClassification == 1)) {
						throw new ServiceException(
						        GEN_UPDDOC_ERROR 
						        	+ DocMessages.getMessage("ws.doc.ibmonly.error"));
		            }
		            // If no exception is thrown means basic validation passed.
		            udDoc.setName(name);
		            if (!StringUtil.isNullorEmpty(description)) {
			            udDoc.setDescription(description);
		            }
		            if (!StringUtil.isNullorEmpty(keywords)) {
		                udDoc.setKeywords(keywords);
		            }
		            if (expirationDate != null) {
			            udDoc.setExpiryDate(expirationDate.getTime());
		            }
		            udDoc.setIbmOnly(String.valueOf(securityClassification).charAt(0));
		            udDoc.setUpdatedBy(getUserName());
		            
		            bSuccess = udUpdateDAO.updateDocProp(udDoc);
		            if (bSuccess) {
		                // Update reader/editor/notification list for this document
		                udUpdateDAO.setEditHistory(docid, getUserName(), "Update Document", "Document properties Updated");
						Vector vtReaderUsers = new Vector();
						Vector vtReaderGroups = new Vector();
						Vector vtGroups = new Vector();
						Vector vtUsers = new Vector();
						HashMap pdGroupUsers = new HashMap();
						String []strGroups = 
						    DocumentWSHelper.prepareLists(
						        udReader, strProjectId, getUserName(), 
						        additionalEditors, additionalReaders, 
						        notificationList, GEN_ADDDOC_ERROR, vtReaderUsers, 
						        vtReaderGroups, vtGroups, vtUsers, pdGroupUsers);
						udUpdateDAO.deleteAllDocRestrictions(udDoc.getId(), Defines.DOC_EDIT_ACCESS);
						udUpdateDAO.deleteAllDocRestrictions(udDoc.getId(), Defines.DOC_READ_ACCESS);
					    //Once Doc has been added, handle editors and readers
					    if (vtGroups.size() > 0) {
					        udUpdateDAO.addDocResGroupsEdit(vtGroups, udDoc.getId(), strProjectId);
					    }
					    if (vtUsers.size() > 0) {
						    udUpdateDAO.addAdditionalEditors(
				                    vtUsers, 
				                    String.valueOf(udDoc.getId()), 
				                    strProjectId);
					    }

					    if (vtReaderGroups.size() > 0) {
					        udUpdateDAO.addDocResGroups(vtGroups, udDoc.getId(), strProjectId);
					    }
					    if (vtReaderUsers.size() > 0) {
						    udUpdateDAO.addDocResUsers(
						            vtReaderUsers, 
				                    String.valueOf(udDoc.getId()), 
				                    strProjectId);
					    }
					    // Once editors and readers have been added to DB, popuplate
					    // vtReaderUsers and vtEditorUsers with all users from the groups
					    // as well.
					    for(int i=0; i < vtGroups.size(); i++) {
					        vtUsers.addAll((List) pdGroupUsers.get(vtGroups.get(i)));
					    }
					    for(int i=0; i < vtReaderGroups.size(); i++) {
					        vtReaderUsers.addAll((List) pdGroupUsers.get(vtReaderGroups.get(i)));
					    }
					    //Reader list should also have the author
					    vtReaderUsers.add(getUserName());

					    //Finally handle notification
					    DocumentWSHelper.popuplateWorkspaceDetailsFromProps(udReader, strProjectId, udWorkspace);
					    String strNotifyAllFlag = (notifyAll) ? DocConstants.IND_YES : null;
					    Vector vtNotifyUsers = 
						    NotificationMsgHelper.performAddNotification( 
						    		strNotifyAllFlag,
						    		"",
						    		Integer.toString(udWorkspace.getTopCatID()),
						    		Integer.toString(udDoc.getCatId()),
						    		udWorkspace.getLinkID(),
						    		notificationList,
						    		udDoc.IsDPrivate() ? DocConstants.DOC_RESTRICTED : DocConstants.DOC_UNRESTRICTED,
						    		vtReaderUsers,
						    		vtUsers,
						    		strProjectId,
						    		udWorkspace.getWorkspaceName(),
						    		udWorkspace.getWorkspaceType(),
						    		false,
						    		udWorkspace.getAppName(),
						    		udDoc,
						    		getUserName(),
						    		udReader,
						    		false);
					    NotificationMsgHelper.finalizeNotificationList(strGroups, vtNotifyUsers, udReader);
					    // First clear existing notification list
					    udUpdateDAO.clearNotificationList(udDoc.getId());
					    udUpdateDAO.addDocNotificationList(udDoc.getId(), notifyAll, vtNotifyUsers, strGroups);
		            }
		        }
		        else {
		            // Means user does not have edit priviliges for the document
					throw new ServiceException(
					        GEN_ADDDOC_ERROR 
					        	+ DocMessages.getMessage(AUTH_ERROR));
		        }
		    }
        }
        catch(Exception e) {
            bSuccess = false;
            m_pdLog.error(e);
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            }
            else {
                e.printStackTrace(System.out);
                throw new ServiceException(e.getMessage());
            }
        }
        finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
						"Error adding document:" + e.getMessage());
        }
        }
        return bSuccess;
    }
}
