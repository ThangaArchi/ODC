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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayDocDetailsAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger
			.getLogger(DisplayDocDetailsAction.class);

	private static final String ERR_INVALID_DOC = "error.invalid.docid";

	private static final String ERR_UNAUTHORIZED_ACCESS = "documents.error.action.notallowed";

	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping pdMapping,
			ActionForm pdForm, HttpServletRequest pdRequest,
			HttpServletResponse pdResponse) throws Exception {

		DocumentDAO udDAO = null;
		try {
			BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
			String strProjectId = DocumentsHelper.getProjectID(pdRequest);
			int iDocID = Integer.parseInt(udForm.getDocid());
			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);

			Vector editHistoryDocVector = udDAO
					.getAllVersionsDocEditHistory(udDoc);
			udForm.setEditHistoryVector(editHistoryDocVector);

			if (udDoc == null) {
				throw new DocumentException(ERR_INVALID_DOC);
			}
			udForm.setParentCategory(udDAO.getCat(udDoc.getCatId()));
			Vector vtComments = new Vector();
			if (udDoc.hasPreviousVersion()) {
				Vector vtPrevVersions = udDAO.getPreviousVersions(iDocID,
						iDocID + 1);
				if (vtPrevVersions != null && vtPrevVersions.size() > 0) {
					int iSize = vtPrevVersions.size();
					for (int iCounter = 0; iCounter < iSize; iCounter++) {
						ETSDoc udPrevDoc = (ETSDoc) vtPrevVersions
								.get(iCounter);
						vtComments.addAll(udDAO.getDocComments(udPrevDoc
								.getId(), strProjectId, iSize - iCounter));
					}
				}
			} else {
				vtComments = udDAO.getDocComments(udDoc.getId(), strProjectId,
						1);
			}
			udForm.setComments(vtComments);

			String strUserName = getEdgeAccess(pdRequest).gIR_USERN;
			Vector vtResUsersEdit = udDAO.getAllDocRestrictedEditUserIds(udForm
					.getDocid(), strProjectId);
			List lstGroupsEdit = udDAO.getAllDocRestrictedEditGroupIds(Integer
					.parseInt(udForm.getDocid()));
			if (!vtResUsersEdit.isEmpty() || !lstGroupsEdit.isEmpty()) {
				udForm.setChUsersEdit("yes");
				udDoc.setDPrivateEdit(DocConstants.DOC_RESTRICTED);
			}

			/* ************************************************************************************* */
			boolean bCanUserAccess = false;
			boolean bCanUserAccessEditor = false;
			boolean bAccessCheck = false;
			if (udDoc.IsDPrivateEdit()) {
				bAccessCheck = true;
				Vector vtEditUsers = udDAO.getRestrictedProjMembersEdit(
						strProjectId, udDoc.getId(), false, false);
				List lstEditGroups = udDAO
						.getAllDocRestrictedEditGroupIds(udDoc.getId());
				udForm.setSelectedEditGroups(lstEditGroups);
				udForm.setEditUsers(vtEditUsers);

				if (!Defines.WORKSPACE_OWNER.equals(getUserRole(pdRequest))
						&& !isSuperAdmin(pdRequest)) {
					for (int iCounter = 0; iCounter < vtEditUsers.size(); iCounter++) {
						ETSUser udUser = (ETSUser) vtEditUsers.get(iCounter);
						if (udUser.getUserId().equals(strUserName)) {
							bCanUserAccessEditor = true;
							break;
						}
					}
					if (!bCanUserAccessEditor) {
						// Maybe user belongs to a group which can access
						List lstDocEditGroups = udDAO
								.getAllDocRestrictedEditGroupIds(udDoc.getId());
						List lstUsrEditGroups = udDAO.getUserEditGroups(
								strUserName, strProjectId);
						for (int i = 0; i < lstUsrEditGroups.size(); i++) {
							if (lstDocEditGroups.contains(lstUsrEditGroups
									.get(i))) {
								bCanUserAccessEditor = true;
								break;
							}
						}
					}
				}
			}
			/* ************************************************************************************* */
			if (udDoc.IsDPrivate()) {
				bAccessCheck = true;
				Vector vtUsers = udDAO.getRestrictedProjMembers(strProjectId,
						udDoc.getId(), false, false);
				List lstGroups = udDAO.getAllDocRestrictedGroupIds(udDoc
						.getId());
				udForm.setSelectedGroups(lstGroups);
				udForm.setUsers(vtUsers);

				if (!Defines.WORKSPACE_OWNER.equals(getUserRole(pdRequest))
						&& !isSuperAdmin(pdRequest)) {
					for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
						ETSUser udUser = (ETSUser) vtUsers.get(iCounter);
						if (udUser.getUserId().equals(strUserName)) {
							bCanUserAccess = true;
							break;
						}
					}
					if (!bCanUserAccess) {
						// Maybe user belongs to a group which can access
						List lstDocGroups = udDAO
								.getAllDocRestrictedGroupIds(udDoc.getId());
						List lstUsrGroups = udDAO.getUserGroups(strUserName,
								strProjectId);
						for (int i = 0; i < lstUsrGroups.size(); i++) {
							if (lstDocGroups.contains(lstUsrGroups.get(i))) {
								bCanUserAccess = true;
								break;
							}
						}
					}
				}
			}

// User is not WORKSPACE_OWNER and not SUPER_ADMIN
if (  !Defines.WORKSPACE_OWNER.equals(getUserRole(pdRequest)) && !isSuperAdmin(pdRequest) ) {

	// DOC IS RESTRICTED, but no read access & having no additional editors
	if( (udDoc.IsDPrivate() && (!bCanUserAccess)) && (!udDoc.IsDPrivateEdit()) ) {
		if (!udDoc.getUserId().equals(strUserName)) {
			throw new DocumentException(ERR_UNAUTHORIZED_ACCESS);
		}
	}
	// DOC IS RESTRICTED, but no read access, having additional editors, but no edit access
	else if( (udDoc.IsDPrivate() && (!bCanUserAccess)) && ((udDoc.IsDPrivateEdit()) && (!bCanUserAccessEditor)) ) {
		if (!udDoc.getUserId().equals(strUserName)) {
			throw new DocumentException(ERR_UNAUTHORIZED_ACCESS);
		}
	}

}

			/* ************************************************************************************* */

			List ltNotificationList = udDAO.populateNotificationList(iDocID, udDoc, udForm, false);

			boolean bIsUserNotified = udDAO.isUserInNotificationList(
					getEdgeAccess(pdRequest).gIR_USERN, udDoc.getId());
			if (bIsUserNotified) {
				udForm.setNotifyOption(DocConstants.IND_YES);
			} else {
				udForm.setNotifyOption(DocConstants.IND_NO);
			}

			
			// Log a hit on this document - ONLY if it has the required param
			String strHitReq = pdRequest
					.getParameter(DocConstants.PARAM_HITREQ);
			if (!StringUtil.isNullorEmpty(strHitReq)) {
				udDAO.logHit(udDoc.getId(), strProjectId, strUserName);
				udDoc.setDocHits(udDoc.getDocHits() + 1);
			}
			udForm.setDocument(udDoc);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}
		return pdMapping.findForward(FWD_SUCCESS);
	}
	



}