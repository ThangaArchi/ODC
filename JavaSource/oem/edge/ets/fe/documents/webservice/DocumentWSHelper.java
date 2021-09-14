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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.UserObject;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.documents.NotificationMsgHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocMessages;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;

import org.apache.log4j.Logger;

public class DocumentWSHelper {

	/** Stores the Logging object */
	private static final Logger m_pdLog = Logger.getLogger(DocumentWSHelper.class);
	
    /**
     * @param strErrorType
     * @param name
     * @param iDocID
     * @param expirationDate
     * @param securityClassification
     * @param udReader
     * @param udCategory
     * @throws ServiceException
     * @throws SQLException
     */
    public static void validateDoc(
            String strErrorType,
            String name, 
            int iDocID, 
            Date expirationDate, 
            int securityClassification, 
            DocReaderDAO udReader, 
            ETSCat udCategory) throws ServiceException, SQLException {
        if (StringUtil.isNullorEmpty(name)) {
            // DOC Name cannot be empty
			throw new ServiceException(
			        strErrorType 
			        	+ DocMessages.getMessage("doc.name.error"));
        }
        else {
            // Check for duplicate doc name
            int iDocCount = udReader.getDocByNameAndCat(udCategory.getId(), name, iDocID);
            if (iDocCount > 0) {
                // Duplicate doc name found
				throw new ServiceException(
				        strErrorType 
				        	+ DocMessages.getMessage("doc.name.duplicate"));
            }
        }
        
        // Check expiration date
        if (expirationDate != null) {
		    if (expirationDate.before(new java.util.Date())) {
		        // Expiration Date cannot be before current date
				throw new ServiceException(
				        strErrorType 
				        	+ DocMessages.getMessage("doc.expdate.past.error"));
		    }
		}

		// Check security classification
        if (securityClassification > 2) {
			throw new ServiceException(
			        strErrorType 
			        	+ DocMessages.getMessage("ws.security.invalid"));
        }
		if ( ((udCategory.getIbmOnly() == '1') && (securityClassification <= 0)) 
		        || ((udCategory.getIbmOnly() == '2') && (securityClassification <= 1)) ) {
		    
			throw new ServiceException(
			        strErrorType 
			        	+ DocMessages.getMessage("ws.security.error"));
		}
    }
    
    /**
     * @param checkList
     * @param lstWSGroups
     * @param vtProjectMembers
     * @param strMissingMembers
     * @param strMissingGroups
     */
    private static void validateUsersAndGroups(
            String []checkList, 
            List lstWSGroups, 
            Vector vtProjectMembers, 
            StringBuffer strMissingMembers, 
            StringBuffer strMissingGroups) {
	    if (checkList != null && checkList.length > 0) {
	        for(int i=0; i < checkList.length; i++) {
		        String strEachElement = checkList[i];
		        if (strEachElement.startsWith(DocConstants.GROUP_PREFIX)) {
		            String strGroupID = groupExists(lstWSGroups, strEachElement);
		            if (StringUtil.isNullorEmpty(strGroupID)) {
			            if (strMissingGroups.length() != 0) {
			                strMissingGroups.append(", ");
			            }
		                strMissingGroups.append(strEachElement.substring(DocConstants.GROUP_PREFIX_LENGTH));
		            }
		            else {
		                checkList[i] = strGroupID;
		            }
		        }
		        else {
		            if (!memberExists(vtProjectMembers, strEachElement)) {
			            if (strMissingMembers.length() != 0) {
			                strMissingMembers.append(", ");
			            }
		                strMissingMembers.append(strEachElement);

		            }
		        }
	        }
	    }
    }
    
    /**
     * @param vtUsers
     * @param vtGroups
     * @param pdGroupUsers
     */
    private static void filterGroupUsers(Vector vtUsers, Vector vtGroups, HashMap pdGroupUsers) {
	    // Filter out editors who are already part of a group
	    if (vtGroups.size() > 0) {
	        Vector vtTmpEditors = new Vector();
            for(int j=0; j < vtUsers.size(); j++) {
                String strUserId = (String) vtUsers.get(j);
                boolean bUserExistsInGroup = false;
                for(int i=0; i < vtGroups.size(); i++) {
		            String strGroupId = (String) vtGroups.get(i);
		            List lstGroupMembers = (List) pdGroupUsers.get(strGroupId);
	                if (lstGroupMembers.contains(strUserId)) {
	                    bUserExistsInGroup = true;
	                }
		        }
                if (!bUserExistsInGroup) {
                    vtTmpEditors.add(strUserId);
                }
            }
            vtUsers.removeAllElements();
            vtUsers.addAll(vtTmpEditors);
	    }
    }
    
    /**
     * @param lstWSGroups
     * @param strGroup
     * @return
     */
    private static String groupExists(List lstWSGroups, String strGroup) {
        String strReturnGroupID = null;
        String strGroupId = strGroup.substring(DocConstants.GROUP_PREFIX_LENGTH);
        if (lstWSGroups != null && lstWSGroups.size() > 0) {
            for(int i=0; i < lstWSGroups.size(); i++) {
                Group udGroup = (Group) lstWSGroups.get(i);
                // Check provided Group against name and ID
                if (udGroup.getGroupId().equalsIgnoreCase(strGroupId)) {
                    strReturnGroupID = DocConstants.GROUP_PREFIX + udGroup.getGroupId();
                }
                else if (udGroup.getGroupName().equalsIgnoreCase(strGroupId)) {
                    strReturnGroupID = DocConstants.GROUP_PREFIX + udGroup.getGroupId();
                }
            }
        }
        return strReturnGroupID;
    }
    
    /**
     * @param vtMembers
     * @param strUser
     * @return
     */
    private static boolean memberExists(Vector vtMembers, String strUser) {
        boolean bExists = false;
        if (vtMembers != null && vtMembers.size() > 0) {
            for(int i=0; i < vtMembers.size(); i++) {
                ETSUser udEachMember = (ETSUser) vtMembers.get(i);
                if (udEachMember.getUserId().equalsIgnoreCase(strUser)) {
                    bExists = true;
                    break;
                }
            }
        }
        return bExists;
    }
    
    /**
     * @param uploadFile
     * @return
     * @throws Exception
     */
    public static List getDocFiles(MimeMultipart uploadFile, long lMaxFileSize) throws ServiceException {
        List lstDocFiles = new ArrayList();
        try {
			for (int i = 0; i < uploadFile.getCount(); i++) {
				BodyPart pdBody = uploadFile.getBodyPart(i);
				DataHandler pdHandler = pdBody.getDataHandler();
				String strContentType = pdHandler.getContentType();
				String strFileName = pdBody.getFileName();
				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("CONTENT TYPE IS " + strContentType);
					m_pdLog.debug("CONTENT FILE NAME IS " + strFileName);
				}
				int iSize = pdHandler.getInputStream().available();
				if (lMaxFileSize > 0) {
					if (iSize == 0 || iSize > DocConstants.MAX_FILE_SIZE) {
						throw new ServiceException(
						        "Error adding new attachments to document: "
						        	+ DocMessages.getMessage("ws.attach.filesize"));
					}
				}
				ETSDocFile udDocFile = new ETSDocFile();
				udDocFile.setFileName(strFileName);
				udDocFile.setInputStream(pdHandler.getInputStream());
				udDocFile.setFileSize(iSize);
				lstDocFiles.add(udDocFile);
			}
	    }
	    catch(javax.mail.MessagingException e) {
	        // Means this upload might not have any documents attached
	    }
	    catch(IOException e) {
	        throw new ServiceException(e.getMessage());
	    }
	    return lstDocFiles;
    }
    
	/**
	 * @param lstDocFiles
	 * @param strNewFileName
	 * @return
	 */
	public static boolean checkExistingFiles(List lstDocFiles, String strNewFileName) {
	    boolean bExists = false;
	    for(int i=0; i < lstDocFiles.size(); i++) {
	        ETSDocFile udDocfile = (ETSDocFile) lstDocFiles.get(i);
	        if (udDocfile.getFileName().equalsIgnoreCase(strNewFileName)) {
	            bExists = true;
	            break;
	        }
	    }
	    
	    return bExists;
	}

	/**
	 * @param udReaderDAO
	 * @param strProjectId
	 * @param udWorkspace
	 */
	public static void popuplateWorkspaceDetailsFromProps(
	        DocReaderDAO udReaderDAO, String strProjectId, Workspace udWorkspace) {
		ResourceBundle udResources = null;
		String strLinkID = "";
		String strAppName = Defines.LINKID;
		if (Defines.ETS_WORKSPACE_TYPE.equals(udWorkspace.getWorkspaceType())) {
		    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.etsbrand");
		    if (udResources != null) {
		        strAppName = udResources.getString("ets.app_name");
		        strLinkID  = udResources.getString("ets.linkid");
		    }
		}
		else if (Defines.AIC_WORKSPACE_TYPE.equals(udWorkspace.getWorkspaceType())) {
		    udResources = ResourceBundle.getBundle("oem.edge.ets.fe.aicbrand");
			if (udResources != null) {
			    strAppName = udResources.getString("aic.app_name");
			    strLinkID  = udResources.getString("aic.linkid");
			}
		}
		udWorkspace.setAppName(strAppName);
		udWorkspace.setLinkID(strLinkID);
		try {
			udWorkspace.setTopCatID(
			        udReaderDAO.getTopCatId(strProjectId, Defines.DOCUMENTS_VT));
		}
		catch(SQLException e) {
		    m_pdLog.error(e);
		}
	}
	
	/**
	 * @param udReaderDAO
	 * @param udWorkspace
	 * @param udDoc
	 * @param strAttachmentNames
	 * @param strAction
	 * @throws Exception
	 */
	public static void handleAttachmentNotification(
	        DocReaderDAO udReaderDAO, 
	        Workspace udWorkspace, 
	        ETSDoc udDoc, 
	        String strAttachmentNames, 
	        String strUserId,
	        String strAction) throws Exception {
	    
	    popuplateWorkspaceDetailsFromProps(udReaderDAO, udDoc.getProjectId(), udWorkspace);
	    StringBuffer strEmail = 
	        NotificationMsgHelper.createAttachmentMessage(
	            udDoc,
				Integer.toString(udWorkspace.getTopCatID()),
				Integer.toString(udDoc.getCatId()),
				udWorkspace.getLinkID(),
				udWorkspace.getAppName(),
				udWorkspace.getWorkspaceName(),
				strAttachmentNames,
				strAction);
	    
		UserObject udUser = 
		    AccessCntrlFuncs.getUserObject(
		            udReaderDAO.getConnection(), 
		            strUserId, true, false);
		Vector vtNotifyList =
			udReaderDAO.getDocNotifyList(udDoc.getId());
		if (vtNotifyList != null && vtNotifyList.size() > 0) {
			String strMemberEmailList =
				udReaderDAO.getProjMemberEmails(
					udWorkspace.getWorkspaceId(),
					vtNotifyList);
			String strSubject = 
			    CommonEmailHelper.IBM 
					+ udWorkspace.getAppName() 
					+ " - Document update: " 
					+ udDoc.getName();
		    NotificationMsgHelper.sendEMail(
		            udUser.gEMAIL,
					strMemberEmailList,
					"",
					"",
					NotificationMsgHelper.getMailHost(),
					strEmail.toString(),
					strSubject,
					udUser.gEMAIL);
		}
	}

	/**
	 * @param udReader
	 * @param strProjectId
	 * @param strCallerPrincipal
	 * @param additionalEditors
	 * @param additionalReaders
	 * @param notificationList
	 * @param strErrorType
	 * @param vtReaderUsers
	 * @param vtReaderGroups
	 * @param vtGroups
	 * @param vtUsers
	 * @param pdGroupUsers
	 * @return
	 * @throws ServiceException
	 * @throws SQLException
	 */
	public static String[] prepareLists(
	        DocReaderDAO udReader, 
	        String strProjectId,
	        String strCallerPrincipal,
	        String []additionalEditors, 
	        String []additionalReaders, 
	        String []notificationList, 
	        String strErrorType,
			Vector vtReaderUsers,
			Vector vtReaderGroups,
			Vector vtGroups,
			Vector vtUsers,
			HashMap pdGroupUsers) throws ServiceException, SQLException {
	    // First check that all in readers/editors/notification list as
	    // listed in the team for the workspace
        StringBuffer strMissingMembers = new StringBuffer(StringUtil.EMPTY_STRING); 
        StringBuffer strMissingGroups  = new StringBuffer(StringUtil.EMPTY_STRING);

        // First get all groups defined for the workspace
        List lstWSGroups = udReader.getGroups(strProjectId);
	    Vector vtProjectMembers = udReader.getProjMembers(strProjectId);
	    validateUsersAndGroups(additionalEditors, lstWSGroups, vtProjectMembers, strMissingMembers, strMissingGroups);
	    validateUsersAndGroups(additionalReaders, lstWSGroups, vtProjectMembers, strMissingMembers, strMissingGroups);
	    validateUsersAndGroups(notificationList, lstWSGroups, vtProjectMembers, strMissingMembers, strMissingGroups);
        if (strMissingMembers.length() > 0) {
            throw new ServiceException(
                    strErrorType 
                    + DocMessages.getMessage("ws.user.notfound") + strMissingMembers.toString());
        }
        else if (strMissingGroups.length() > 0) {
            throw new ServiceException(
                    strErrorType 
                    + DocMessages.getMessage("ws.group.notfound") + strMissingGroups.toString());
        }
        
	    if (additionalEditors != null && additionalEditors.length > 0) {
	        for(int i=0; i < additionalEditors.length; i++) {
	            String strEditor = additionalEditors[i];
	            if (strEditor.startsWith(DocConstants.GROUP_PREFIX)) {
	                String strGroupId = 
	                    strEditor.substring(DocConstants.GROUP_PREFIX_LENGTH);
	                vtGroups.add(strGroupId);
	                pdGroupUsers.put(
	                        strGroupId, udReader.getGroupUsers(strGroupId));
	            }
	            else {
	                //Exclude the Author from additional editor list
//	                if (!strEditor.equals(strCallerPrincipal)) {
		                vtUsers.add(strEditor);
//	                }
	                }
	            }
	        }
	    filterGroupUsers(vtUsers, vtGroups, pdGroupUsers);
	    
        if (additionalReaders != null && additionalReaders.length > 0) {
	        for(int i=0; i < additionalReaders.length; i++) {
	            String strReader = additionalReaders[i];
	            if (strReader.startsWith(DocConstants.GROUP_PREFIX)) {
	                String strGroupId = 
	                    strReader.substring(DocConstants.GROUP_PREFIX_LENGTH);
	                vtReaderGroups.add(strGroupId);
	                pdGroupUsers.put(
	                        strGroupId, udReader.getGroupUsers(strGroupId));
	            }
	            else {
	                vtReaderUsers.add(strReader);
	            }
	        }
	        filterGroupUsers(vtReaderUsers, vtReaderGroups, pdGroupUsers);
		    // further filter users and groups who are already in editors list
		    //vtReaderUsers.removeAll(vtUsers);
		    //vtReaderGroups.removeAll(vtGroups);
	    }
        
        //Create list of groups for notification
        List lstGroups = new ArrayList();
        List lstNotifyUsers = new ArrayList();
        if (notificationList != null && notificationList.length > 0) {
	        for(int i=0; i < notificationList.length; i++) {
	            String strNotify = notificationList[i];
	            if (strNotify.startsWith(DocConstants.GROUP_PREFIX)) {
	                lstGroups.add(strNotify);
	                List lstGroupUsers = 
	                    udReader.getGroupUsers(strNotify.substring(DocConstants.GROUP_PREFIX_LENGTH));
	                lstNotifyUsers.addAll(lstNotifyUsers);
	            }
	            else {
	                lstNotifyUsers.add(strNotify);
	            }
	        }
	        
        }
        String []strGroups = new String[lstGroups.size()];
        lstGroups.toArray(strGroups);
        notificationList = new String[lstNotifyUsers.size()];
        lstNotifyUsers.toArray(notificationList);
	    
        return strGroups;
	}

	/**
	 * @param strProjectId
	 * @param udReader
	 * @return
	 */
	public static boolean isWorkflowProject(String strProjectId, DocReaderDAO udReader) {
	    boolean bIsWorkflow = false;
	    
	    try {
		    bIsWorkflow = udReader.isWorkflowProject(strProjectId);
	    }
	    catch(SQLException e) {
	        m_pdLog.error(e);
	    }
	    
	    return bIsWorkflow;
	}
}
