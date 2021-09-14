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
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocComment;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.MetricsConstants;
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
public class PostCommentsAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(PostCommentsAction.class);

	private static final String ERR_DOC_COMMENTS = "doc.comments.size.error";

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
		HttpServletResponse pdReponse)
		throws Exception {

		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
		ActionErrors pdErrors = validate(udForm);
		if (pdErrors.size() > 0) {
			throw new DocumentException(pdErrors);
		}

		int iDocID = Integer.parseInt(udForm.getDocid());
		String strProjectId = DocumentsHelper.getProjectID(pdRequest);
		EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
		ETSDocComment udComment = new ETSDocComment();
		udComment.setComment(udForm.getDocComments());
		udComment.setId(iDocID);
		udComment.setProjectId(strProjectId);
		udComment.setUserId(udEdgeAccess.gIR_USERN);

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			ETSProj udProject = udDAO.getProjectDetails(strProjectId);
			boolean bSuccess = udDAO.addDocComment(udComment);
			if (bSuccess) {
				Vector vtNotifyUsers = new Vector();
				// NOTIFY USERS

				String attachmentNotifyFlag = udForm.getAttachmentNotifyFlag();
				if( StringUtil.isNullorEmpty(attachmentNotifyFlag) ) {
					List ltNotifyAllUserWithGroup = 
					    udDAO.populateNotificationList(iDocID, udDoc,udForm, true);
						NotificationHelper.performCommentsNotification(
						        udForm,
							udDoc,
							pdRequest.getParameter("currdocid"),
							udProject,
							udEdgeAccess,
							udDAO);
				}

				if (udProject
					.getProjectOrProposal()
					.equals(DocConstants.TYPE_PROJECT)) {
					Metrics.appLog(
						udDAO.getConnection(),
						udEdgeAccess.gIR_USERN,
						MetricsConstants.PROJECT_DOC_COMMENTS);
				} else { //proposal
					Metrics.appLog(
						udDAO.getConnection(),
						udEdgeAccess.gIR_USERN,
						MetricsConstants.PROPOSAL_DOC_COMMENTS);
				}
			}
		} catch (SQLException e) {

		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udForm
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm) {
		ActionErrors pdErrors = new ActionErrors();

		// Check comments length
		String strComments = udForm.getDocComments();
		if (StringUtil.isNullorEmpty(strComments)
			|| strComments.length() > DocConstants.MAX_COMMENTS_LENGTH) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionError(ERR_DOC_COMMENTS));
		}

		return pdErrors;
	}

}
