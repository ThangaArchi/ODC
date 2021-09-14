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
public class DisplayMoveCatTargetAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayMoveCatTargetAction.class);

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

		String[] strCategories = udForm.getSubmitCategories();
		// We know that this array should have only one element

		String strSelectedCategory = strCategories[0];

		int iCurrentCatId = 0;
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			udForm.setParentCategory(udDAO.getCat(iCurrentCatId));
			udForm.setCategory(
				udDAO.getCat(Integer.parseInt(strSelectedCategory)));
			udForm.setCategories(udDAO.getAllCats(strProjectID));
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}
}
