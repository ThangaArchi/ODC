package oem.edge.ets.fe.documents;

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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

/**
 * @version 1.0
 * @author tkandhas@in.ibm.com
 */
public class DeleteDocAttachmentDisplayAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(DeleteDocAttachmentDisplayAction.class);

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
		int iParentId = 0;
		int iCurrentCatId = 0;
		boolean reg_doc = false;
		String strAction = udForm.getDocAction();
		String strMeetingId = StringUtil.EMPTY_STRING;

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();

			if (StringUtil.isNullorEmpty(strAction)) {
			strAction = DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER;
		}

		int iDocID = Integer.valueOf(udForm.getDocid()).intValue();
		String strProjectId = DocumentsHelper.getProjectID(pdRequest);

		String selectedAttchments[] = udForm.getSelectedAttachments();
		ETSDocFile objETSDocFile[] = new ETSDocFile[selectedAttchments.length];
		Collection collEtsAttachments = new ArrayList();
		for(int i=0; i< selectedAttchments.length; i++){
			objETSDocFile[i] = udDAO.getDocFile(iDocID, Integer.parseInt(selectedAttchments[i] ) );
			collEtsAttachments.add(objETSDocFile[i]);
		}
		udForm.setEtsDocAttachments(collEtsAttachments);
		
		iParentId = iCurrentCatId;
		reg_doc = true;
		strMeetingId = StringUtil.EMPTY_STRING;


			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			Vector vtUsers = null;
			Vector vtIBMUsers = null;
			ETSCat udParentCat = udDAO.getCat(udDoc.getCatId());
			

				if (udParentCat != null) {
					vtUsers = udDAO.getProjMembers(strProjectId, true);
				}
				vtIBMUsers = udDAO.getIBMMembers(vtUsers);
				if (udParentCat.isIbmOnlyOrConf()) {
					vtUsers = vtIBMUsers;
				}

				iDocID = Integer.valueOf(udForm.getDocid()).intValue();
				udForm.setDocument(udDoc);
				//udForm.setFormContext(strAction);
				if (udDoc.getExpiryDate() != 0) {
					udForm.setExpDate(new DocExpirationDate(udDoc.getExpiryDate()));
				}

/*			if (bIsUserNotified) {
				udForm.setNotifyOption(DocConstants.IND_YES);
			} else {
				udForm.setNotifyOption(DocConstants.IND_NO);
			}
*/
			if (StringUtil.isNullorEmpty(udForm.getNotifyOption())) {
				udForm.setNotifyOption("to");
			}

			List ltNotifyAllUserWithGroup = udDAO.populateNotificationList(iDocID, udDoc,udForm, false);
				
			boolean bIsUserNotified = udDAO.isUserInNotificationList(
						getEdgeAccess(pdRequest).gIR_USERN, udDoc.getId());
				
			udForm.setDocument(udDoc);

/*		if (udDoc.getExpiryDate() != 0) {
				udForm.setExpDate(
					new DocExpirationDate(udDoc.getExpiryDate()));
			}
*/			
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}
	
	
}