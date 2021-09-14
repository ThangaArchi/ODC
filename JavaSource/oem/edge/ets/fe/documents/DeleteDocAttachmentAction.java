package oem.edge.ets.fe.documents;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

/**
 * @version 1.0
 * @author tkandhas@in.ibm.com
 */
public class DeleteDocAttachmentAction extends BaseDocumentAction
{
	private static final Log m_pdLog = EtsLogger.getLogger(DeleteDocAttachmentAction.class);
	private static final String ERR_INVALID_DOC = "error.invalid.docid";
	private static final String ERR_UNAUTHORIZED_ACCESS = "documents.error.action.notallowed";


    public ActionForward executeAction(	ActionMapping pdMapping,
    		ActionForm pdForm,
			HttpServletRequest pdRequest,
			HttpServletResponse pdResponse)
			throws Exception {

    	ActionErrors errors = new ActionErrors();
    	ActionForward forward = new ActionForward(); // return value
		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
		
		//added by govind
		String appType = (String)pdRequest.getParameter("appType");
		String workflowID = (String)pdRequest.getParameter("workflowID");
        //addition ends

		StringTokenizer fileId = new StringTokenizer(pdRequest.getParameter("delDocFileIds"),",");
		String delDocFileIDS[] = new String[fileId.countTokens()];
		int idCount=0;
		Vector vtDocFileId = new Vector();
		boolean bCanUserAccessRead, bCanUserAccessEdit = false;
		EdgeAccessCntrl udEdgeAccess = DocumentsHelper.getEdgeAccess(pdRequest);
		while(fileId.hasMoreTokens())
		{
			delDocFileIDS[idCount]= fileId.nextToken();
			vtDocFileId.add(delDocFileIDS[idCount]);
			idCount++;
		}

		String strAction = udForm.getDocAction();
		String delDocAttachmentNames = udForm.getDelDocAttachmentNames();
		DocumentDAO udDAO = null;
		int iDocID = Integer.valueOf(udForm.getDocid()).intValue();
		try {
			udDAO = getDAO();
			boolean bIsDeleted = udDAO.deleteAttachments(vtDocFileId, iDocID);
			
			String strProjectId = DocumentsHelper.getProjectID(pdRequest);
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
			
			String strUserName = getEdgeAccess(pdRequest).gIR_USERN;
			
			ETSDocEditHistory objETSDocEditHistory = new ETSDocEditHistory();
			objETSDocEditHistory.setDocId(udDoc.getId());
			objETSDocEditHistory.setUserId( strUserName );
			objETSDocEditHistory.setAction("Delete Attachment");
			objETSDocEditHistory.setActionDetails("Attachment(s) deleted ---> " + delDocAttachmentNames);
			
			udDAO.setEditHistory(objETSDocEditHistory);

			if (StringUtil.isNullorEmpty(strAction)) {
				strAction = DocConstants.ACTION_ADD_ATTACHMENT_CURRENT_VER;
			}

/*			String selectedAttchments[] = udForm.getSelectedAttachments();
			ETSDocFile objETSDocFile[] = new ETSDocFile[selectedAttchments.length];
			Collection collEtsAttachments = new ArrayList();
			for(int i=0; i< selectedAttchments.length; i++){
				objETSDocFile[i] = udDAO.getDocFile(iDocID, Integer.parseInt(selectedAttchments[i] ) );
				collEtsAttachments.add(objETSDocFile[i]);
			}
			udForm.setEtsDocAttachments(collEtsAttachments); */
			
			Vector vtUsers = null;
			Vector vtIBMUsers = null;
			ETSCat udParentCat = udDAO.getCat(udDoc.getCatId());

			if (udParentCat != null) {
				vtUsers = udDAO.getProjMembers(strProjectId, true);
			}
			vtIBMUsers = udDAO.getIBMMembers(vtUsers);
			if (udParentCat.isIbmOnlyOrConf()) {
				vtUsers = vtIBMUsers;
			}
			udForm.setDocument(udDoc);
			if (udDoc.getExpiryDate() != 0) {
				udForm.setExpDate(new DocExpirationDate(udDoc.getExpiryDate()));
			}
				udForm.setParentCategory(udDAO.getCat(udDoc.getCatId()));
				udForm.setComments(
				udDAO.getDocComments(udDoc.getId(), strProjectId, 1) );

				Vector vtUsersEdit = udDAO.getRestrictedProjMembersEdit( strProjectId, udDoc.getId(), false, false);
				List lstGroupsEdit = udDAO.getAllDocRestrictedEditGroupIds(udDoc.getId());
				udForm.setSelectedEditGroups(lstGroupsEdit);
				udForm.setEditUsers(vtUsersEdit);

				if (udDoc.IsDPrivate()) {
					Vector vtUsersRead = udDAO.getRestrictedProjMembers(	strProjectId, udDoc.getId(), false, false);
					List lstGroupsRead = udDAO.getAllDocRestrictedGroupIds(udDoc.getId());

					udForm.setSelectedGroups(lstGroupsRead);
					udForm.setUsers(vtUsersRead);

					boolean bIsUserNotified = udDAO.isUserInNotificationList(getEdgeAccess(pdRequest).gIR_USERN, udDoc.getId());
					if (bIsUserNotified) {
						udForm.setNotifyOption(DocConstants.IND_YES);
					} else {
						udForm.setNotifyOption(DocConstants.IND_NO);
					}

					// 	Log a hit on this document - ONLY if it has the required param
					String strHitReq = pdRequest.getParameter(DocConstants.PARAM_HITREQ);
					if (!StringUtil.isNullorEmpty(strHitReq)) {
						udDAO.logHit(udDoc.getId(), strProjectId, strUserName);
						udDoc.setDocHits(udDoc.getDocHits() + 1);
					}
				}

				Vector editHistoryDocVector = udDAO.getAllVersionsDocEditHistory(udDoc);
				udForm.setEditHistoryVector(editHistoryDocVector);
				
				if(bIsDeleted) {
				    //Update the update by column for the document
				    udDoc.setUpdatedBy(getEdgeAccess(pdRequest).gIR_USERN);
				    udDAO.updateDocPropEdit(udDoc);

					String attachmentNotifyFlag = udForm.getAttachmentNotifyFlag();
					if( StringUtil.isNullorEmpty(attachmentNotifyFlag) ) {
						List ltNotifyAllUserWithGroup = udDAO.populateNotificationList(iDocID, udDoc,udForm, true);
						ETSProj udProject = udDAO.getProjectDetails(strProjectId);
						udForm.setNotifyOption("");
							NotificationHelper.performAttachmentNotification(
									udForm,
									udProject,
									udDoc,
									udEdgeAccess,
									udDAO,
									DocConstants.ACTION_DELETE_ATTACHMENT_CURRENT_VER);
					}
				}
				
				udForm.setDocument(udDoc);
		}
		catch (SQLException e) {
			e.printStackTrace(System.err);
		} finally {
			super.cleanup(udDAO);
		}


        if (!errors.isEmpty()) {
            saveErrors(pdRequest, errors);
        }

        forward = pdMapping.findForward("success");

       // return pdMapping.findForward(FWD_SUCCESS);
//      added by govind
        if(!"workflow".equalsIgnoreCase(appType))
        	return pdMapping.findForward(FWD_SUCCESS);
        else{
        	return new ActionForward("/displayWorkflowDocumentDetails.wss?appType="+appType+"&proj="+udForm.getProj()+"&workflowID="+workflowID+"&tc="+udForm.getTc());
        }
        //addition ends
    }



    

}
