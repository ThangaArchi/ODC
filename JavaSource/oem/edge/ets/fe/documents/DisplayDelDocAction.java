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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author v2srikau
 */
public class DisplayDelDocAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayDelDocAction.class);

	private static final String ERR_DOC_NOT_EXIST="error.invalid.docid";
	private static final String FWD_FAILURE="failure";

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
		//added by govind
		
		String appType = (String)pdRequest.getParameter("appType");
		String workflowID = (String)pdRequest.getParameter("workflowID");
		
		//end
		
		int iDocID = 0;
		String strDocID = udForm.getDocid();
		if (!StringUtil.isNullorEmpty(strDocID)) {
			iDocID = Integer.parseInt(strDocID);
		}
		int iCurrentCatId = 0;

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		String strProjectId = DocumentsHelper.getProjectID(pdRequest);

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			if (udDoc == null) {
				ActionErrors pdErrors = new ActionErrors();
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_DOC_NOT_EXIST));
				saveErrors(pdRequest, pdErrors);
				return pdMapping.findForward(FWD_FAILURE);
			}
			udForm.setDocument(udDoc);
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}
			return pdMapping.findForward(FWD_SUCCESS);
	}
}
