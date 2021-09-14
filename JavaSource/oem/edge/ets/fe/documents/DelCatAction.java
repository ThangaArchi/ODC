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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.MetricsConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author v2srikau
 */
public class DelCatAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(DelCatAction.class);

	private static final String ERR_DEL_EMPTY = "category.del.empty";
	private static final String ERR_DEL_INVALID = "category.del.invalid";
	private static final String ERR_DEL_ERROR = "category.del.error";

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

		ActionErrors pdErrors = validate(udForm);

		if (pdErrors.size() > 0) {
			throw new DocumentException(pdErrors);
		}

		int iCurrentCatId = 0;
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		DocumentDAO udDAO = null;
		try {
			String[] strDelCats = udForm.getSubmitCategories();
			udDAO = getDAO();
			ETSProj proj = udDAO.getProjectDetails(strProjectID);
			ETSCat udCat = udDAO.getCat(iCurrentCatId, strProjectID);
			EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest); 
			Vector vtCats =
				udDAO.getValidCatTreeIds(
					udCat,
					udEdgeAccess.gIR_USERN,
					strProjectID,
					ETSUtils.checkUserRole(udEdgeAccess, strProjectID, udDAO.getConnection()),
					Defines.DELETE,
					true);

			String strResMsg = null;

			for (int iCounter = 0; iCounter < strDelCats.length; iCounter++) {
				String strDelCatID = (String) strDelCats[iCounter];

				int iDelCatId = Integer.parseInt(strDelCatID);
				if (vtCats.contains(new Integer(iDelCatId))) {

					ETSCat udDelCat = ETSDatabaseManager.getCat(iDelCatId);
					if (udDelCat != null) {
						boolean bSuccessFlag =
							udDAO.delCat(udDelCat, udEdgeAccess.gIR_USERN);

						if (bSuccessFlag) {
							if (proj
								.getProjectOrProposal()
								.equals(DocConstants.TYPE_PROJECT)) {
								Metrics.appLog(
									udDAO.getConnection(),
									udEdgeAccess.gIR_USERN,
									MetricsConstants.PROJECT_CAT_DELETE);
							} else { //proposal
								Metrics.appLog(
									udDAO.getConnection(),
									udEdgeAccess.gIR_USERN,
									MetricsConstants.PROPOSAL_CAT_DELETE);
							}
						} else {
							strResMsg = ERR_DEL_ERROR;
						}
					} else {
						System.out.print("put bad current cat id message here");
						strResMsg = ERR_DEL_INVALID;
					}

					if (!StringUtil.isNullorEmpty(strResMsg)) {
						pdErrors.add(
							DocConstants.MSG_USER_ERROR,
							new ActionMessage(strResMsg));
					}
				}
			}

		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}

		if (pdErrors.size() > 0) {
			throw new DocumentException(pdErrors);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udForm
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm) {
		ActionErrors pdErrors = new ActionErrors();

		String[] strDelCats = udForm.getSubmitCategories();
		if (strDelCats == null || strDelCats.length == 0) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_DEL_EMPTY));
		}

		return pdErrors;
	}
}
