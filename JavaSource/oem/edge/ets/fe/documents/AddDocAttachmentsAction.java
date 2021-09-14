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

/*
 * Created on Jul 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.documents;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

/**
 * @author amar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddDocAttachmentsAction extends BaseDocumentAction
{
	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(AddDocAttachmentsAction.class);

	private static final String ERR_INVALID_DOC = "error.invalid.docid";
	private static final String ERR_FILE_SIZE = "doc.file.size.error";
	private static final String ERR_FILE_ADD = "doc.file.add.error";
	private static final String ERR_FILE_ADD_INVALID_FILE = "doc.file.addInvalidFile.error";
	private static final String ERR_FILE_EMPTY = "doc.file.empty.error";
	
	private static final String ERR_UNAUTHORIZED_ACCESS =
		"documents.error.action.notallowed";

	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	private final String ERR_DOC_ADD_ATTACH = "";
	private final String ERR_NOT_ALLOWED = "";
	
	public ActionForward executeAction(
 		ActionMapping pdMapping,
 		ActionForm pdForm,
		HttpServletRequest pdRequest,
 		HttpServletResponse pdResponse)	throws Exception 
    {
		DocumentDAO udDAO = null;
		try {
		ActionErrors pdErrors = null;
		BaseDocumentForm udForm = (BaseDocumentForm)pdForm;
		ETSDoc etsDoc = udForm.getDocument();
		ETSDocEditHistory etsDocEditHistory = new ETSDocEditHistory();
		
		String strFileNames = "Attachment(s) added--->  ";
		//int intFilesUploaded = 0; 
		
		ArrayList existingFilesList = new ArrayList();
		ArrayList uploadedFilesList = new ArrayList();
		ArrayList errorFilesList = new ArrayList();
		boolean addedDocFileStatus = false; 
		
		udDAO = getDAO();
		int iDocID = Integer.parseInt(udForm.getDocid());
		
		etsDocEditHistory.setDocId(iDocID);
		
		etsDocEditHistory.setAction(DocConstants.ACTION_UPDATE_DOC_ATTACHMENT);
		String strProjectId = DocumentsHelper.getProjectID(pdRequest);
		
		ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
		EdgeAccessCntrl udEdgeAccess = DocumentsHelper.getEdgeAccess(pdRequest);
		etsDocEditHistory.setUserId( udEdgeAccess.gIR_USERN );
		if (udDoc == null)
		{
			throw new DocumentException(ERR_INVALID_DOC);
		}
		
		Vector editHistoryDocVector = udDAO.getAllVersionsDocEditHistory(udDoc);
		udForm.setEditHistoryVector(editHistoryDocVector);


		m_pdLog.debug("******In the AddDocAttachmentsAction action class******");
		
		ArrayList filesList = getCleanArrayList((ArrayList)udForm.getUploadedFiles());
		
		//		Validation for the input 
		
		pdErrors =
			validate(udForm, filesList);

		if (pdErrors.size() > 0)
		{
			throw new DocumentException(pdErrors);
		}
		
		int noFileList = filesList.size();
		
		for(int i=0; i < noFileList; i++)
		{
			
			FormFile formFile = (FormFile)filesList.get(i);
			
			udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			
			String docFileName = formFile.getFileName();
			
			boolean fileNameExists = isExistingFile(formFile, udDoc);
			
			m_pdLog.debug(strFileNames);
			m_pdLog.debug(udForm.getDocid());
			m_pdLog.debug(docFileName);
			
			if((formFile.getFileSize()==0) || (formFile.getFileSize()>=102400000))
			{
				errorFilesList.add(formFile);
				continue;
			}			
			if(fileNameExists)
			{
				existingFilesList.add(formFile);
				continue;
			}
			
			int fileSize = formFile.getFileSize();
			m_pdLog.debug(new Integer(fileSize).toString());
			
			InputStream fileInputStream = formFile.getInputStream();
			if(fileInputStream != null)
					m_pdLog.debug(fileInputStream);
			if(!StringUtil.isNullorEmpty(docFileName))
			{
				strFileNames = strFileNames + docFileName+", ";

				addedDocFileStatus = 
						udDAO.addDocFile
							(iDocID,docFileName, fileSize, fileInputStream);
				
				m_pdLog.debug("******addedDocFileStatus*****"+addedDocFileStatus);
				
				if(addedDocFileStatus)
				{
					uploadedFilesList.add(formFile);
				}
				
				//intFilesUploaded++;
				
			}//if condition
			
		}//for loop
		strFileNames = strFileNames.substring(0, strFileNames.length()-2);
		//strFileNames = strFileNames + " are added";
		
		etsDocEditHistory.setActionDetails(strFileNames);
		
		if(uploadedFilesList.size() >= 1)
		{
			try
				{
					udDAO.setEditHistory(etsDocEditHistory);
					//udDAO.cleanup();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
		}
		System.out.println("uploadedFilesList====>"+uploadedFilesList);
		System.out.println("existingFilesList====>"+existingFilesList);
		
		//set the form files in the request scope
		
		udForm.setUploadedFilesList(uploadedFilesList);
		udForm.setExistingFilesList(existingFilesList);
		udForm.setErrorFilesList(errorFilesList);
		
		if(addedDocFileStatus) {
		    
		    //Update the update by column for the document
		    udDoc.setUpdatedBy(getEdgeAccess(pdRequest).gIR_USERN);
		    udDAO.updateDocPropEdit(udDoc);
		    
			String attachmentNotifyFlag = udForm.getAttachmentNotifyFlag();
			if( StringUtil.isNullorEmpty(attachmentNotifyFlag) ) {
				List ltNotifyAllUserWithGroup = udDAO.populateNotificationList(iDocID, udDoc,udForm, true);
				ETSProj udProject = udDAO.getProjectDetails(strProjectId);
				NotificationHelper.performAttachmentNotification(
							udForm,
							udProject,
							udDoc,
							udEdgeAccess,
							udDAO,
							DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER);
			}
		}
		
		return pdMapping.findForward(FWD_SUCCESS);
	} catch(SQLException e) {
		throw e;
	} catch(Exception ex) {
		throw ex;
	}
	finally {
		super.cleanup(udDAO);
	}
		
	}



	
	
	/**
	 * @param udForm
	 * @param bIsUpdateDocProp
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm, ArrayList lstUploadedFiles){
		ActionErrors pdErrors = new ActionErrors();

		ETSDoc udDoc = udForm.getDocument();

		//Check Doc File - Only if it is Add Document / Upload new version
		
		lstUploadedFiles = getCleanArrayList((ArrayList)udForm.getUploadedFiles());
		
		// Must have at-least one file
		if (lstUploadedFiles == null || lstUploadedFiles.size() == 0) {
			// DO NOTHING.
		    //m_pdLog.error("FILE LENGTH : 0def");
			m_pdLog.debug("Checking the file names..........");
		    pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_FILE_EMPTY));
		    System.out.println("lstUploadedFiles==>"+lstUploadedFiles);
		    System.out.println("pdErrors=====>"+pdErrors.size());
		} 
		/*
		 else {
		    m_pdLog.error("FILE LENGTH : " + lstUploadedFiles.size());
		    
			boolean bIsFirstFile = true;
			for (int iCounter = 0;
				iCounter < lstUploadedFiles.size();
				iCounter++) {
				FormFile udFormFile =
					(FormFile) lstUploadedFiles.get(iCounter);

				/*
				 * if (StringUtil.isNullorEmpty(udFormFile.getFileName()) &&
				 * bIsFirstFile) { pdErrors.add(
				 * DocConstants.MSG_USER_ERROR, new
				 * ActionMessage(ERR_FILE_EMPTY)); }
				 
				
			    m_pdLog.error("FILE NAME : " + udFormFile.getFileName());
			    m_pdLog.error("FILE SIZE : " + udFormFile.getFileSize());
				if (!StringUtil.isNullorEmpty(udFormFile.getFileName())) {
					int iFileSize = udFormFile.getFileSize();
					if (iFileSize > DocConstants.MAX_FILE_SIZE) {
						pdErrors.add(
							DocConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_FILE_SIZE));
					} else if (iFileSize == 0) {
						pdErrors.add(
							DocConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_FILE_ADD_INVALID_FILE));
					}
				}
				bIsFirstFile = false;
			}
		}*/
		return pdErrors;
	}
	private ArrayList getCleanArrayList(ArrayList uploadedFilesList)
	{
		Iterator iterator= uploadedFilesList.iterator();
		
		while(iterator.hasNext())
		{
			FormFile formFile = (FormFile)iterator.next();
			
			if(formFile.getFileName().equals("")||formFile.getFileName().equals(null))
			{
				iterator.remove();
			}
		}
		return uploadedFilesList;
	}
	private boolean isExistingFile(FormFile formFile, ETSDoc udDoc)
	{
		boolean isFileExisting = false;
		
		List lstExistingFiles = udDoc.getDocFiles();
		
		Iterator iterFiles = lstExistingFiles.iterator();
		
		while(iterFiles.hasNext())
		{
			ETSDocFile etsDocFile = (ETSDocFile)iterFiles.next();
			
			if(etsDocFile.getFileName().equals(formFile.getFileName()))
			{  
				isFileExisting = true;
				break;
			}
		}
		return isFileExisting;
	}
	
	
	
}
