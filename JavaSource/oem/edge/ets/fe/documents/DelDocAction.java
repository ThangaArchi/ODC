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

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.MetricsConstants;
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
public class DelDocAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(DelDocAction.class);

	private static final String ERR_NOT_ALLOWED = "document.delete.not.allowed";
	private static final String ERR_DOC_DELETE = "document.delete.error.occured";

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
		
		String appType = (String)pdRequest.getParameter("appType");
		String workflowID = (String)pdRequest.getParameter("workflowID");
		
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
		EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
		String strUserId = udEdgeAccess.gIR_USERN;

		String strForward = FWD_SUCCESS;
		DocumentDAO udDAO = null;
		
		try {
			String strDelAll = pdRequest.getParameter("alldel");
			udDAO = getDAO();
			String strUserRole = getUserRole(pdRequest);
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);

			if (udDoc == null) {
				// Means this doc disappeared.
				ActionErrors pdErrors = new ActionErrors();
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_DOC_DELETE));
				saveErrors(pdRequest, pdErrors);
                
				if(!"workflow".equalsIgnoreCase(appType))
						return pdMapping.findForward(FWD_SUCCESS);
				else{
					    String str = "Error in deleting the document";
					    return new ActionForward("/displayWorkflowDocumentDetails.wss?cc="+udForm.getTc()+"&appType="+appType+"&proj="+udForm.getProj()+"&workflowID="+workflowID+"&tc="+udForm.getTc()+"&docid="+strDocID+"&message="+str+"&hitreques=true");
				}


			}
			if (!udDoc.isLatestVersion() && !"workflow".equalsIgnoreCase(appType)) {
				strForward = strForward + "_prev";
			}

			ETSProj udProject = udDAO.getProjectDetails(strProjectId);
			
			//check User has edit privilige to the doc
			Vector vtEditUsers = udDAO.getRestrictedProjMembersEdit(
					strProjectId, udDoc.getId(), false, false);
			List lstEditGroups = udDAO
					.getAllDocRestrictedEditGroupIds(udDoc.getId());
			udForm.setSelectedEditGroups(lstEditGroups);
			udForm.setEditUsers(vtEditUsers);

			boolean bCanUserAccessEditor = false;
			for (int iCounter = 0; iCounter < vtEditUsers.size(); iCounter++) {
				ETSUser udUser = (ETSUser) vtEditUsers.get(iCounter);
				if (udUser.getUserId().equals(strUserId)) {
					bCanUserAccessEditor = true;
					break;
				}
			}
			if (!bCanUserAccessEditor) {
			// Maybe user belongs to a group which can access
				List lstDocEditGroups = udDAO
						.getAllDocRestrictedEditGroupIds(udDoc.getId());
				List lstUsrEditGroups = udDAO.getUserEditGroups(strUserId, strProjectId);
				for (int i = 0; i < lstUsrEditGroups.size(); i++) {
					if (lstDocEditGroups.contains(lstUsrEditGroups.get(i))) {
						bCanUserAccessEditor = true;
						break;
					}
				}
			}


			if ((!udDoc.getUserId().equals(udEdgeAccess.gIR_USERN)
				&& !strUserRole.equals(Defines.WORKSPACE_OWNER)
				&& !strUserRole.equals(Defines.WORKSPACE_MANAGER)
				&& !strUserRole.equals(Defines.ETS_ADMIN)
				&& ! bCanUserAccessEditor)  // if edit access then allow delete
				|| (udDoc.hasExpired()
					&& strUserRole.equals(Defines.WORKSPACE_MANAGER))) {
				throw new DocumentException(ERR_NOT_ALLOWED);
			}

			boolean bSuccess = false;
			if (StringUtil.isNullorEmpty(strDelAll)) {
				bSuccess = udDAO.delDoc(udDoc, udEdgeAccess.gIR_USERN);
			} else {
				bSuccess = udDAO.delDoc(udDoc, udEdgeAccess.gIR_USERN, true);
			}

			if (bSuccess) {
				if (StringUtil.isNullorEmpty(strDelAll)) {
					if (udProject
						.getProjectOrProposal()
						.equals(DocConstants.TYPE_PROJECT)) {
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gIR_USERN,
							MetricsConstants.PROJECT_DOC_DELETE);
					} else { //proposal
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gIR_USERN,
							MetricsConstants.PROPOSAL_DOC_DELETE);
					}
				} else {
					if (udProject
						.getProjectOrProposal()
						.equals(DocConstants.TYPE_PROJECT)) {
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gIR_USERN,
							MetricsConstants.PROJECT_DOC_DELETE_ALL);
					} else { //proposal
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gIR_USERN,
							MetricsConstants.PROPOSAL_DOC_DELETE_ALL);
					}
				}
			}
			
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}
		
		if(!"workflow".equalsIgnoreCase(appType))
			return pdMapping.findForward(strForward);
		else{
			ValidateDocumentStageDAO.deleteAttachment(iDocID);
			return new ActionForward("/showstage.wss?appType="+appType+"&proj="+udForm.getProj()+"&workflowID="+workflowID+"&tc="+udForm.getTc());
		}
	}
}



