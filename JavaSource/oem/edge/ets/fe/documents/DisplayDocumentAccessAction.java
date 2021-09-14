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
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayDocumentAccessAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayDocumentAccessAction.class);

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

		int iDocID = Integer.parseInt(udForm.getDocid());

		String strSortBy = udForm.getSortBy();
		String strSort = udForm.getSort();

		if (StringUtil.isNullorEmpty(strSortBy)) {
			strSortBy = Defines.SORT_BY_DATE_STR;
		}
		if (StringUtil.isNullorEmpty(strSort)) {
			strSort = Defines.SORT_ASC_STR;
		}

		DocExpirationDate dtStartDate = udForm.getStartDate();
		DocExpirationDate dtEndDate = udForm.getEndDate();

		StringBuffer strBuffer = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat(StringUtil.DATE_FORMAT);
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		DocumentDAO udDAO = null;
		try {

			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectID);
			Vector vtUsers = new Vector();
			pdRequest.setAttribute(DocConstants.REQ_ATTR_START_DATE, dtStartDate);
			pdRequest.setAttribute(DocConstants.REQ_ATTR_END_DATE, dtEndDate);
			if (DocConstants.IND_YES.equals(dtStartDate.getExpires()) 
			        || pdMapping.getParameter().equals(DocConstants.FRM_CTX_FROM_DETAILS)) {
			    vtUsers = udDAO.getDocMetrics(iDocID, strProjectID, strSort, null, null);    
			}
			else {
			    vtUsers = udDAO.getDocMetrics(iDocID, strProjectID, strSort, dtStartDate, dtEndDate);
			}

			udForm.setDocument(udDoc);
			udForm.setParentCategory(udDAO.getCat(iCurrentCatId));
			udForm.setUsers(vtUsers);
			udForm.setFormContext(pdMapping.getParameter());
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

}
