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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
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
public class MoveDocAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(MoveDocAction.class);

	private static final String ERR_NO_CAT = "doc.move.error.nofolderselected";

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

		String strTargetCat =
			pdRequest.getParameter(DocConstants.PARAM_TARGETCATEGORY);

		String strConfirm = pdRequest.getParameter("confirm");

		if (StringUtil.isNullorEmpty(strConfirm)) {
			ActionErrors pdErrors = validate(udForm, strTargetCat);
			if (pdErrors.size() > 0) {
				throw new DocumentException(pdErrors);
			}
		}

		int iDocID = udForm.getDocument().getId();

		int iCurrentCatId = 0;

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		String strProjectId = DocumentsHelper.getProjectID(pdRequest);

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			ETSCat udTargetCat = udDAO.getCat(Integer.parseInt(strTargetCat));
			udForm.setDocument(udDoc);
			udForm.setCategory(udTargetCat);

			if (!StringUtil.isNullorEmpty(strConfirm)) {
				// Means we have already shown the confirm page.
				// process the move document.
				char cIBMOnly = 'x';
				if (((udDoc.getIbmOnly() == Defines.ETS_PUBLIC)
					&& udTargetCat.isIbmOnlyOrConf())
					|| ((udDoc.getIbmOnly() == Defines.ETS_IBM_ONLY)
						&& (udTargetCat.getIbmOnly() == Defines.ETS_IBM_CONF))) {
					if (udTargetCat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						cIBMOnly = Defines.ETS_IBM_ONLY;
					} else {
						cIBMOnly = Defines.ETS_IBM_CONF;
					}
				}

				udForm.setIBMOnly(String.valueOf(cIBMOnly));

				boolean bSuccess =
					udDAO.updateParentId(
						Defines.NODE_DOC,
						udDoc.getId(),
						udTargetCat.getId(),
						cIBMOnly,
						strProjectId,
						getEdgeAccess(pdRequest).gIR_USERN);

			}
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udForm
	 * @param strTargetCat
	 * @return
	 */
	public ActionErrors validate(
		BaseDocumentForm udForm,
		String strTargetCat) {
		ActionErrors pdErrors = new ActionErrors();

		if (StringUtil.isNullorEmpty(strTargetCat)) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionError(ERR_NO_CAT));
		}

		return pdErrors;
	}
}
