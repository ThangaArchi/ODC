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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;

import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocNotify;

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
public class AddDocAttachmentsDisplayAction extends BaseDocumentAction
{	
	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(AddDocAttachmentsDisplayAction.class);

	private static final String ERR_INVALID_DOC = "error.invalid.docid";
	private static final String ERR_FILE_EMPTY = "doc.file.empty.error";
	private static final String ERR_FILE_SIZE = "doc.file.size.error";
	private static final String ERR_FILE_ADD = "doc.file.add.error";
	private static final String ERR_FILE_ADD_INVALID_FILE = "doc.file.addInvalidFile.error";

	private static final String ERR_UNAUTHORIZED_ACCESS =
		"documents.error.action.notallowed";
	
	private final String ERR_DOC_ADD_ATTACH = "";
	private final String ERR_NOT_ALLOWED = "";

	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	
	
	public ActionForward executeAction(
 		ActionMapping pdMapping,
 		ActionForm pdForm,
		HttpServletRequest pdRequest,
 		HttpServletResponse pdResponse)	throws Exception 
    {
		DocumentDAO udDAO = null;
		
		try {
			BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
			String strProjectId = DocumentsHelper.getProjectID(pdRequest);
			int iDocID = Integer.parseInt(udForm.getDocid());
			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			if (udDoc == null) {
				throw new DocumentException(ERR_INVALID_DOC);
			}
			udForm.setParentCategory(udDAO.getCat(udDoc.getCatId()));
			udForm.setComments(
				udDAO.getDocComments(udDoc.getId(), strProjectId, 1) );

			
			/*	Validation for the input 
			
			ActionErrors pdErrors = null;

			pdErrors =
				validate(udForm);

			if (pdErrors.size() > 0) {
				throw new DocumentException(pdErrors);
			}
			
			*/			
			

			String strUserName = getEdgeAccess(pdRequest).gIR_USERN;
				Vector vtUsers =
					udDAO.getAllDocRestrictedEditUserIds(new Integer(udDoc.getId()).toString(),strProjectId);
				List lstGroups =
					udDAO.getAllDocRestrictedEditGroupIds(udDoc.getId());

				udForm.setSelectedGroups(lstGroups);

				udForm.setUsers(vtUsers);
				
				if (   !Defines.WORKSPACE_MANAGER.equals(getUserRole(pdRequest)) 
					&& !Defines.WORKSPACE_OWNER.equals(getUserRole(pdRequest))
					&& !isSuperAdmin(pdRequest) 
					&& !strUserName.equals(udDoc.getUserId())) //workspace owner check 
				{
					boolean bCanUserAccess = false;
					for (int iCounter = 0;
						iCounter < vtUsers.size();
						iCounter++) {
						
						String udUser = (String) vtUsers.get(iCounter);
						
						if (udUser.equals(strUserName)) {
							bCanUserAccess = true;
							break;
						}
					}
					if (!bCanUserAccess) {
						// Maybe user belongs to a group which can access
						
						List lstDocGroups = lstGroups;
							//udDAO.getAllDocRestrictedEditGroupIds(udDoc.getId());
						List lstUsrGroups =
							udDAO.getUserGroups(strUserName, strProjectId);
						for(int i=0; i < lstUsrGroups.size(); i++) {
							if (lstDocGroups.contains(lstUsrGroups.get(i))) {
								bCanUserAccess = true;
								break;
							}
						}
					}
					if (!bCanUserAccess) {
						if (!udDoc.getUserId().equals(strUserName)) {
							throw new DocumentException(ERR_UNAUTHORIZED_ACCESS);
						}
					}
			}

			boolean bIsUserNotified =
				udDAO.isUserInNotificationList(
					getEdgeAccess(pdRequest).gIR_USERN,
					udDoc.getId());
 			List ltNotifyAllUserWithGroup = udDAO.populateNotificationList(iDocID, udDoc,udForm, false);

			if (StringUtil.isNullorEmpty(udForm.getNotifyOption())) {
				udForm.setNotifyOption("to");
			}

			// Log a hit on this document - ONLY if it has the required param
			String strHitReq =
				pdRequest.getParameter(DocConstants.PARAM_HITREQ);
			if (!StringUtil.isNullorEmpty(strHitReq)) {
				udDAO.logHit(udDoc.getId(), strProjectId, strUserName);
				udDoc.setDocHits(udDoc.getDocHits() + 1);
			}
			udForm.setDocument(udDoc);
		} catch(DocumentException ex) {
			ex.printStackTrace(System.err);
			throw ex;
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}
		return pdMapping.findForward(FWD_SUCCESS);
	}
	

	
	
	/**
	 * @param udForm
	 * @param bIsUpdateDocProp
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm){
		ActionErrors pdErrors = new ActionErrors();

		ETSDoc udDoc = udForm.getDocument();

		//Check Doc File - Only if it is Add Document / Upload new version
		
		if (!udForm.getDocAction().equals(DocConstants.ACTION_ADD_AIC_DOC)) {
			List lstUploadedFiles = udForm.getUploadedFiles();
			
			// Must have at-least one file
			if (lstUploadedFiles == null || lstUploadedFiles.size() == 0) {
				// DO NOTHING.
			    m_pdLog.error("FILE LENGTH : 0def");
			    
			    pdErrors.add(
						DocConstants.MSG_USER_ERROR,
						new ActionMessage(ERR_FILE_SIZE));
			    
			    
			} else {
			    m_pdLog.error("FILE LENGTH : " + lstUploadedFiles.size());
				boolean bIsFirstFile = true;
				for (int iCounter = 0;
					iCounter < lstUploadedFiles.size();
					iCounter++) {
					FormFile udFormFile =
						(FormFile) lstUploadedFiles.get(iCounter);

					/*
					if (StringUtil.isNullorEmpty(udFormFile.getFileName())
						&& bIsFirstFile) {
						pdErrors.add(
							DocConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_FILE_EMPTY));
					}
					*/
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
			}
		}
		
		

		return pdErrors;
	}

	
}
