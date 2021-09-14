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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayPostCommentsAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayPostCommentsAction.class);

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
		int iDocID = 0;
		int iCurrentDocID = 0;
		String strDocID = udForm.getDocid();
		String strCurrentDocID = pdRequest.getParameter("currdocid");
		String strProjectId = DocumentsHelper.getProjectID(pdRequest);
		if (!StringUtil.isNullorEmpty(strDocID)) {
			iDocID = Integer.parseInt(strDocID);
		}
		if (!StringUtil.isNullorEmpty(strCurrentDocID)) {
			iCurrentDocID = Integer.parseInt(strCurrentDocID);
		}

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSDoc udDoc = null;
			if (iDocID != 0) {
				udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
				udForm.setDocument(udDoc);
			}

			// By default - select To option
			udForm.setNotifyOption("to");
			
			// Populate the notification lists
			List ltNotifyAllUserWithGroup = 
			    udDAO.populateNotificationList(iDocID, udDoc,udForm, false);
			
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}
}
