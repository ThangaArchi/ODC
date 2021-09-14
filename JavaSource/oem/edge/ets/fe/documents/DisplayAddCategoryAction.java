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

import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocTranTypes;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayAddCategoryAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayAddCategoryAction.class);

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
		int iCurrentCatId = 0;

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}
		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			int iTopCatID = Integer.parseInt(udForm.getTc()); 
			udForm.setParentCategory(udDAO.getCat(iCurrentCatId));

			if (getTransType(pdMapping).equals(DocTranTypes.TRAN_TYPE_UPDCAT)) {
				// MEANS this action is getting invoked in the Update context
				String[] strSelectedCat = udForm.getSubmitCategories();

				int iSelectedCat = 0;
				if (strSelectedCat == null || strSelectedCat.length == 0) {
					iSelectedCat =
						Integer.parseInt(
							pdRequest.getParameter(
								DocConstants.PARAM_UPDATECAT));
				} else {
					// We know this array can have only one element as 
					// it is coming from a single select box. 
					iSelectedCat = Integer.parseInt(strSelectedCat[0]);
				}
				ETSCat udUpdateCat = udDAO.getCat(iSelectedCat);
				udForm.setCategory(udUpdateCat);
			} else {
				String strIBMOnly = null;
				if (iTopCatID != iCurrentCatId) {
					strIBMOnly = udForm.getParentCategory().getIBMOnlyStr();
				}
				else {
				    strIBMOnly = DocConstants.SEL_OPT_NONE;
				}
				ETSCat udCat = udForm.getCategory();
				if (udCat == null) {
					udCat = new ETSCat();
				}
				if (StringUtil.isNullorEmpty(udForm.getFormContext())) {
					udCat.setIBMOnlyStr(strIBMOnly);
				}
//				if (DocumentsHelper.isAICProject(pdRequest)
//					&& StringUtil.isNullorEmpty(udForm.getFormContext())) {
//					udCat.setIBMOnlyStr(DocConstants.ETS_IBM_ONLY);
//				} else if (
//					!DocumentsHelper.isAICProject(pdRequest)
//						&& StringUtil.isNullorEmpty(udForm.getFormContext())) {
//					udCat.setIBMOnlyStr(DocConstants.SEL_OPT_NONE);
//					udForm.setCategory(udCat);
//				}

			}
		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}

		pdRequest.setAttribute(DocConstants.DOC_FORM, udForm);

		return pdMapping.findForward(FWD_SUCCESS);
	}
}
