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

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
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
public class DisplayAccessHistoryAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayAccessHistoryAction.class);

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

		if (!StringUtil.isNullorEmpty(udForm.getDocAction()) 
		        && udForm.getDocAction().equals("myAccess")) {
		    return pdMapping.findForward("success_user");
		}

		DocExpirationDate dtStartDate = udForm.getStartDate();
		DocExpirationDate dtEndDate = udForm.getEndDate();
		
		int iCurrentCatId = 0;

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		String strSortBy = udForm.getSortBy();
		String strSort = udForm.getSort();

		if (StringUtil.isNullorEmpty(strSortBy)) {
			strSortBy = Defines.SORT_BY_NAME_STR;
		}
		if (StringUtil.isNullorEmpty(strSort)) {
			strSort = Defines.SORT_ASC_STR;
		}

		StringBuffer strBuffer = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat(StringUtil.DATE_FORMAT);
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		DocumentDAO udDAO = null;
		try {

			udDAO = getDAO();
			ETSCat udCurrentCat = udDAO.getCat(iCurrentCatId);
			Vector vtSortedDocs = new Vector();
			if (dtStartDate.getExpires().equals(DocConstants.IND_YES)) {
			    // Means all dates have been selected. So no filter required
				vtSortedDocs =
					udDAO.getAllDocMetrics(strProjectID, strSortBy, strSort, null, null);
			}
			else {
				vtSortedDocs =
					udDAO.getAllDocMetrics(strProjectID, strSortBy, strSort, dtStartDate, dtEndDate);
			}

			EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
			
			if (vtSortedDocs != null && vtSortedDocs.size() > 0) {
				// Filter out the document that this user cannot see.
			    Vector vtFilteredDocs = new Vector();
			    Vector vtAccessibleDocs = 
				    udDAO.getAllDocs(
				        strProjectID, 
				        Defines.SORT_BY_NAME_STR, 
				        Defines.SORT_ASC_STR,
						(isSuperAdmin(pdRequest)
							|| isExecutive(pdRequest)
							|| getUserRole(pdRequest).equals(Defines.WORKSPACE_OWNER)),
						udEdgeAccess.gIR_USERN);

				
				Vector vtIBMMembers =
					udDAO.getIBMMembers(udDAO.getProjMembers(strProjectID));

				boolean bIsUserInternal = false;
				String strUserId = getEdgeAccess(pdRequest).gIR_USERN;
				for (int iCounter = 0;
					iCounter < vtIBMMembers.size();
					iCounter++) {
					ETSUser udUser = (ETSUser) vtIBMMembers.get(iCounter);
					if (udUser.getUserId().equals(strUserId)) {
						bIsUserInternal = true;
						break;
					}
				}

				if (!bIsUserInternal && !isExecutive(pdRequest) && !isSuperAdmin(pdRequest)) {
					// Filter out IBM ONLY documents
					for (int iCounter = 0;
						iCounter < vtAccessibleDocs.size();
						iCounter++) {
						ETSDoc udDoc = (ETSDoc) vtAccessibleDocs.get(iCounter);
						if (!udDoc.isIbmOnlyOrConf()) {
						    vtFilteredDocs.add(udDoc);
						}
					}
					vtAccessibleDocs.removeAllElements();
					vtAccessibleDocs.addAll(vtFilteredDocs);
					vtFilteredDocs.removeAllElements();
				}

				if (vtAccessibleDocs == null || vtAccessibleDocs.size() == 0) {
			        vtSortedDocs.removeAllElements();
			    }
			    else {
				    for (int i=0; i < vtSortedDocs.size(); i++) {
					    ETSDoc udDoc = (ETSDoc) vtSortedDocs.get(i);
					    for(int j=0; j < vtAccessibleDocs.size(); j++) {
					        ETSDoc udAccessible = (ETSDoc) vtAccessibleDocs.get(j);
					        if (udAccessible.getId() == udDoc.getId()) {
					            vtFilteredDocs.add(udDoc);
					        }
					    }
					}
				    vtSortedDocs.removeAllElements();
				    vtSortedDocs.addAll(vtFilteredDocs);
			    }
			}
			
			udForm.setCategory(udCurrentCat);
			udForm.setDocuments(vtSortedDocs);
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

}
