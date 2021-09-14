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

import java.util.Date;

/**
 * @author v2srikau
 */
public interface DocumentWSDL {

	/**
	 * @param strUserId
	 * @param strPassword
	 * @return
	 * @throws ServiceException
	 */
	public Workspace[] getWorkspaces(String strUserId, String strPassword)
		throws ServiceException;

	/**
	 * @param strWorkspaceId
	 * @return
	 * @throws ServiceException
	 */
	public Category[] getFolders(String strWorkspaceId)
		throws ServiceException;

	/**
	 * @param iCatId
	 * @param strDocName
	 * @param strDocDescription
	 * @param uploadFile
	 * @return	 
	 * @throws java.rmi.RemoteException
	 */
	public boolean uploadDocument(
		int catid,
		String strDocName,
		String strDocDescription,
		String strDocAuthor,
		boolean bNotifyAll,
		javax.mail.internet.MimeMultipart uploadFile)
		throws ServiceException;

	/**
	 * @param catid
	 * @param strDocName
	 * @param strDocDescription
	 * @param strDocAuthor
	 * @param bNotifyAll
	 * @param dtExpiration
	 * @param uploadFile
	 * @return
	 * @throws ServiceException
	 */
	public int uploadNewDocument(
	        int catid,
			String strDocName,
			String strDocDescription,
			String strDocAuthor,
			boolean bNotifyAll,
			Date dtExpiration,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException;

	/**
	 * @param docid
	 * @param strDocDescription
	 * @param strDocAuthor
	 * @param bNotifyAll
	 * @param dtExpiration
	 * @param uploadFile
	 * @return
	 * @throws ServiceException
	 */
	public boolean updateDocument(
	        int docid,
			String strDocDescription,
			String strDocAuthor,
			boolean bNotifyAll,
			Date dtExpiration,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException;

	/**
	 * @param docid
	 * @param bNotify
	 * @param uploadFile
	 * @return
	 * @throws ServiceException
	 */
	public boolean addAttachment(
	        int docid,
	        boolean bNotify,
			javax.mail.internet.MimeMultipart uploadFile) throws ServiceException;
	

	/**
	 * @param docid
	 * @param bNotify
	 * @param filename
	 * @return
	 * @throws java.rmi.RemoteException
	 * @throws ServiceException
	 */
	public boolean deleteAttachment(
	        int docid, 
	        boolean bNotify,
	        String []filenames) 
			throws java.rmi.RemoteException, ServiceException;

	/**
	 * @param strWorkspaceId
	 * @return
	 * @throws ServiceException
	 */
	public Category[] getAllFolders(String strWorkspaceId)
		throws ServiceException;

	/**
	 * @param iDocId
	 * @return
	 * @throws ServiceException
	 */
	public boolean deleteDocument(int iDocId) 
		throws ServiceException;
	
	/**
	 * @param strProjectId
	 * @param strCompany
	 * @param strTabName
	 * @param strSource
	 * @param uploadFile
	 * @return
	 * @throws ServiceException
	 */
	public boolean uploadProjectStatus(
	        String strProjectId, 
	        String strCompany, 
	        String strTabName, 
	        String strSource,
	        javax.mail.internet.MimeMultipart uploadFile) throws ServiceException;

	/**
	 * @param strProjectId
	 * @param strCompany
	 * @param strTabName
	 * @param uploadFile
	 * @return
	 * @throws ServiceException
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
	        javax.mail.internet.MimeMultipart uploadFile) throws ServiceException;

	/**
	 * @param docid
	 * @param name
	 * @param description
	 * @param keywords
	 * @param expirationDate
	 * @param securityClassification
	 * @param notifyAll
	 * @param additionalEditors
	 * @param additionalReaders
	 * @param notificationList
	 * @return
	 * @throws ServiceException
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
	        String []notificationList) throws ServiceException;
}
