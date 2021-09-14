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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class UpdateDocStatusAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(UpdateDocStatusAction.class);

	private static final String FWD_ADD_FILES = "details";
	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
		String strForward = FWD_SUCCESS;
		DocumentDAO udDAO = null;
		try {

			udDAO = getDAO();

			int iDocId = Integer.parseInt(udForm.getDocid());

			DocReaderDAO udReaderDAO = new DocReaderDAO();
			udReaderDAO.setConnection(udDAO.getConnection());
			ETSDoc udDoc = udReaderDAO.getDocById(iDocId);

			udDoc.setUpdatedBy(getEdgeAccess(pdRequest).gIR_USERN);
			udDAO.updateDocProp(udDoc);
			
			// Re-read the object as we want updated date value
			udDoc = udReaderDAO.getDocById(iDocId);
			udDoc.setUpdatedBy(ETSUtils.getUsersName(udDAO.getConnection(), getEdgeAccess(pdRequest).gIR_USERN));
			
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("UPDATING DOC STATUS FOR DOC ID : " + iDocId);
			}

			String strError = pdRequest.getParameter("error");
			if (!StringUtil.isNullorEmpty(strError)) {
				ActionErrors pdErrors = new ActionErrors();
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionError(strError));
				saveErrors(pdRequest, pdErrors);
			}

			//udDAO.updateDocStatus(iDocId, DocConstants.ITAR_STATUS_COMPLETE);

			//Mini Release 6.2
			String strDocAction = udForm.getDocAction();
			if (strDocAction != null) { 
			    if (strDocAction.equals("addFiles")) {
			    strForward = FWD_ADD_FILES;
			    }
			    else if (strDocAction.equals("delFiles")) {
			        // Perform delete here
					ETSDocEditHistory etsDocEditHistory = new ETSDocEditHistory();
					String strFileNames = "Attachment(s) deleted--->  ";
					strFileNames = strFileNames + udForm.getDelDocAttachmentNames();
					etsDocEditHistory.setDocId(iDocId);
					etsDocEditHistory.setAction(DocConstants.ACTION_UPDATE_DOC_ATTACHMENT);
					etsDocEditHistory.setUserId( getEdgeAccess(pdRequest).gIR_USERN );
					etsDocEditHistory.setActionDetails(strFileNames);
					udDAO.setEditHistory(etsDocEditHistory);

			        String strDocFileIds = pdRequest.getParameter("docFileIds");
					StringTokenizer strTokens = new StringTokenizer(strDocFileIds, ",");
					while (strTokens.hasMoreTokens()) {
					    String strDocFileId = strTokens.nextToken();
					    if (!StringUtil.isNullorEmpty(strDocFileId)) {
					        try {
					            int iDocFileId = Integer.parseInt(strDocFileId);
								udDAO.deleteDocFile(iDocFileId, iDocId);
					        }
					        catch(NumberFormatException e) {
					            // DO NOTHING
					        }
					    }
					}
					if (StringUtil.isNullorEmpty(udForm.getAttachmentNotifyFlag())) {
					    // Means user did not select supress notification FLAG
					    // So we need to send notification.
						String strProjectId = udDoc.getProjectId();
					    int iTopCatID =
							udReaderDAO.getTopCatId(
								strProjectId,
								Defines.DOCUMENTS_VT);

						String strProjectName =
							udReaderDAO.getProjectName(
								strProjectId);
						String strAppName = "E&TS Connect";
						
						Vector vtNotifyList =
							udReaderDAO.getDocNotifyList(iDocId);

						String strMemberEmailList =
							udReaderDAO.getProjMemberEmails(
								strProjectId,
								vtNotifyList);

						StringBuffer strDeleteAttachmentMsg = 
					        NotificationMsgHelper.createAttachmentMessage(
					                udDoc, 
					                String.valueOf(iTopCatID), 
					                String.valueOf(udDoc.getCatId()), 
					                Defines.LINKID, 
					                strAppName, 
					                strProjectName, 
					                udForm.getDelDocAttachmentNames(), 
					                DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER);

						String strSubject = strAppName + " - Document update: " + udDoc.getName();

						NotificationMsgHelper.sendEMail(
								udDoc.getUserId(),
								strMemberEmailList,
								"",
								"",
								NotificationMsgHelper.getMailHost(),
								strDeleteAttachmentMsg.toString(),
								strSubject,
								udDoc.getUserId());

					}
			        strForward = FWD_ADD_FILES;
			    }
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(strForward);
	}
}
