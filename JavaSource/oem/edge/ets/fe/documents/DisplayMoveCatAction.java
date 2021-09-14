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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayMoveCatAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayMoveCatAction.class);

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
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSCat udCurrCat = udDAO.getCat(iCurrentCatId);
			Vector vtCats =
				udDAO.getValidCatTree(
					udCurrCat,
					getEdgeAccess(pdRequest).gIR_USERN,
					strProjectID,
					getUserRole(pdRequest),
					Defines.UPDATE,
					false);

			//remove Meeting Documents folder
			int iTopCatID = Integer.parseInt(udForm.getTc());
			if (vtCats != null) {
				Vector vtTmp = new Vector();
				for(int iCounter=0; iCounter < vtCats.size(); iCounter++) {
				    ETSCat udCat = (ETSCat) vtCats.get(iCounter);
				    if (udCat.getName().equalsIgnoreCase(DocConstants.MEETINGS_DOC_FOLDER) && (udCat.getParentId() == iTopCatID)) {
				        continue;
				    }
				    else {
				        vtTmp.add(udCat);
				    }
				}
				vtCats.removeAllElements();
				vtCats.addAll(vtTmp);
			}
			udForm.setCategories(vtCats);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}
		return pdMapping.findForward(FWD_SUCCESS);
	}
}
