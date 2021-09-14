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
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.teamgroup.GroupsHelper;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayAddEditDocumentAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayAddEditDocumentAction.class);

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

		String strAction = udForm.getDocAction();
		if (StringUtil.isNullorEmpty(strAction)) {
			strAction = DocConstants.ACTION_ADD_DOC;
		}

		int iParentId = 0;
		boolean reg_doc = false;
		String strMeetingId = StringUtil.EMPTY_STRING;
		String strRepeatId = StringUtil.EMPTY_STRING;
		String strSelfId = StringUtil.EMPTY_STRING;
		String strSetId = StringUtil.EMPTY_STRING;

		int iCurrentCatId = 0;

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}
		String strProjectId = DocumentsHelper.getProjectID(pdRequest);

		if (strAction.equals(DocConstants.ACTION_ADD_MEETING_DOC)) {
			iParentId = -2;
			strMeetingId =
				DocumentsHelper.getParameter(
					pdRequest,
					DocConstants.PARAM_MEETINGID);
			strRepeatId =
				DocumentsHelper.getParameter(
					pdRequest,
					DocConstants.PARAM_REPEATID);
		} else if (strAction.equals(DocConstants.ACTION_ADD_PROJECT_PLAN)) {
			iParentId = -1;
			strMeetingId = StringUtil.EMPTY_STRING;
		} else if (strAction.equals(DocConstants.ACTION_ADD_TASK_DOC)) {
			iParentId = -3;
			strMeetingId =
				DocumentsHelper.getParameter(
					pdRequest,
					DocConstants.PARAM_TASKID);
			strSelfId =
				DocumentsHelper.getParameter(
					pdRequest,
					DocConstants.PARAM_SELF);
			strSetId =
				DocumentsHelper.getParameter(pdRequest, DocConstants.PARAM_SET);
		} else if (strAction.equals(DocConstants.ACTION_ADD_ACTION_PLAN)) {
			iParentId = -4;
			strMeetingId =
				DocumentsHelper.getParameter(
					pdRequest,
					DocConstants.PARAM_SETMET);
		} else { //doc
			iParentId = iCurrentCatId;
			reg_doc = true;
			strMeetingId = StringUtil.EMPTY_STRING;
		}

		DocumentDAO udDAO = null;
		try {
			//HERE WE CREATE THE ALL USERS GROUP IF IT DOES NOT EXIST 
			GroupsHelper.checkAndCreateAllUsersGroup(strProjectId);
			
			udDAO = getDAO();
			
			Vector vtUsers = null;
			Vector vtIBMUsers = null;
			ETSCat udParentCat = udDAO.getCat(iParentId);
			if (strAction.equals(DocConstants.ACTION_ADD_DOC)
				|| strAction.equals(DocConstants.ACTION_ADD_AIC_DOC)
				|| strAction.equals(DocConstants.ACTION_UPDATE_DOC)
				|| strAction.equals(DocConstants.ACTION_UPDATE_DOC_PROPS)) {
				udForm.setParentCategory(udParentCat);
				if (udParentCat != null) {
					vtUsers = udDAO.getProjMembersWithRole(strProjectId, true);
				}
				vtIBMUsers = udDAO.getIBMMembers(vtUsers);
				if (udParentCat.isIbmOnlyOrConf()) {
					vtUsers = vtIBMUsers;
				}
			} // Pre-populate the form in case of ACTION_UPDATE_DOC
			udForm.setUsers(vtUsers);
			udForm.setIBMUsers(vtIBMUsers);
			if (strAction.equals(DocConstants.ACTION_UPDATE_DOC)
				|| strAction.equals(DocConstants.ACTION_UPDATE_DOC_PROPS)) {
				int iDocID = Integer.valueOf(udForm.getDocid()).intValue();
				ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, strProjectId);
				//udForm.setFormContext(strAction);
				if (udDoc.getExpiryDate() != 0) {
					udForm.setExpDate(
						new DocExpirationDate(udDoc.getExpiryDate()));
				}
				
				if (udDoc.IsDPrivate()) {
					udForm.setChUsers(DocConstants.DOC_RESTRICTED);
					Vector vtResUsers =
						udDAO.getAllDocRestrictedUserIds(
							udForm.getDocid(),
							strProjectId);
					if (vtResUsers != null && vtResUsers.size() > 0) {
						String[] strResUsers = new String[vtResUsers.size()];
						for (int iCounter = 0;
							iCounter < vtResUsers.size();
							iCounter++) {
							strResUsers[iCounter] =
								(String) vtResUsers.get(iCounter);
						}
						udForm.setRestrictedUsers(strResUsers);
					}
					List lstGroups =
						udDAO.getAllDocRestrictedGroupIds(
							Integer.parseInt(udForm.getDocid()));
					udForm.setSelectedGroups(lstGroups);
				} else {
					udForm.setChUsers(DocConstants.DOC_UNRESTRICTED);
				}

//////////////////////////////////////////////////////////////////////////////////////////
				
				//if (udDoc.IsDPrivateEdit()) {
				Vector vtResUsersEdit = udDAO.getAllDocRestrictedEditUserIds( udForm.getDocid(), strProjectId );
				List lstGroupsEdit = udDAO.getAllDocRestrictedEditGroupIds(Integer.parseInt(udForm.getDocid()));
                 if( !vtResUsersEdit.isEmpty() || !lstGroupsEdit.isEmpty() )
                 {
					udForm.setChUsersEdit("yes");
					udDoc.setDPrivateEdit(DocConstants.DOC_RESTRICTED);
					if (vtResUsersEdit != null && vtResUsersEdit.size() > 0) {
						String[] strResUsersEdit = new String[vtResUsersEdit.size()];
						for (int iCounter = 0;
							iCounter < vtResUsersEdit.size();
							iCounter++) {
							strResUsersEdit[iCounter] = (String) vtResUsersEdit.get(iCounter);
						}
						udForm.setRestrictedUsersEdit(strResUsersEdit);
					}
					udForm.setSelectedEditGroups(lstGroupsEdit);
                 }
				//}

//////////////////////////////////////////////////////////////////////////////////////////

				// Check for Notification List (Whether to be pre-populated)
				Vector vtNotifyList = udDAO.getDocNotifyList(udDoc.getId());
				if (vtNotifyList.size() > 0) {
					DocNotify udDocNotify = null;
					// Check the first element for the Notify All Flag
					udDocNotify = (DocNotify) vtNotifyList.get(0);
					if (!StringUtil
						.isNullorEmpty(udDocNotify.getNotifyAllFlag())
						&& DocConstants.IND_YES.equals(
							udDocNotify.getNotifyAllFlag())) {
						// Means notify all was selected.
						udForm.setNotifyFlag(udDocNotify.getNotifyAllFlag());
					} else {
						Vector vtSubList =
							DocumentDAO.getUserList(vtNotifyList);
						Vector vtTmpList = new Vector();
						for (int iCounter = 0;
							iCounter < vtSubList.size();
							iCounter++) {
							udDocNotify = (DocNotify) vtSubList.get(iCounter);
							// Check if user is in unsubscribe list
							if (udDAO.isUserInNotificationList(udDocNotify.getUserId(), iDocID)) {
								vtTmpList.add(udDocNotify.getUserId());
							}
						}
						String[] notifyUsers = new String[vtTmpList.size()];
						notifyUsers = (String []) vtTmpList.toArray(notifyUsers);						
						udForm.setNotifyUsers(notifyUsers);
						vtSubList = DocumentDAO.getGroupList(vtNotifyList);
						String[] notifyGroups = new String[vtSubList.size()];
						for (int iCounter = 0;
							iCounter < vtSubList.size();
							iCounter++) {
							udDocNotify = (DocNotify) vtSubList.get(iCounter);
							notifyGroups[iCounter] = udDocNotify.getGroupId();
						}
						udForm.setSelectedNotifyGroups(notifyGroups);
					}
				}
				udForm.setDocument(udDoc);
                if ((udForm.getRestrictedUsersEdit() == null || udForm.getRestrictedUsersEdit().length == 0) 
                        && (udForm.getSelectedEditGroups() == null || udForm.getSelectedEditGroups().size() == 0) ) {
					List lstDefaultEditors = DocumentsHelper.getDefaultEditors(pdRequest, udForm, false);
					String[] strResUsersEdit = new String[lstDefaultEditors.size()];
					for (int iCounter = 0;
						iCounter < lstDefaultEditors.size();
						iCounter++) {
						strResUsersEdit[iCounter] = ((ETSUser) lstDefaultEditors.get(iCounter)).getUserId();
					}
					udForm.setRestrictedUsersEdit(strResUsersEdit);
                }
				
                if ((udForm.getRestrictedUsers() == null || udForm.getRestrictedUsers().length == 0) 
                        && (udForm.getSelectedGroups() == null || udForm.getSelectedGroups().size() == 0) ) {
                    // Means all users can read this document, so show that
                     List lstAllUsersGroup = new ArrayList();
                     
                     lstAllUsersGroup.add(getAllUsersGroup(udDAO, strProjectId));
                     udForm.setSelectedGroups(lstAllUsersGroup); 
                 }

			} else {
				ETSDoc udDoc = udForm.getDocument();
				if (udDoc == null) {
					udDoc = new ETSDoc();
				}
				if (DocumentsHelper.isAICProject(pdRequest)) {
					//udDoc.setIBMOnlyStr(DocConstants.ETS_IBM_ONLY);
					AICDynTabTemplateBusinessDelegate udAIC =
						new AICDynTabTemplateBusinessDelegate();
					Collection udCollection = udAIC.findAllTemplates();
					Vector vtAICTemplateList = new Vector();
					if (udCollection != null && udCollection.size() > 0) {
						vtAICTemplateList.addAll(udCollection);
					}
					udForm.setAICTemplateList(vtAICTemplateList);
				}// else {
				udDoc.setIBMOnlyStr(udParentCat.getIBMOnlyStr());
				//}
				udForm.setDocument(udDoc);
				List lstDefaultEditors = DocumentsHelper.getDefaultEditors(pdRequest, udForm, true);
				String[] strResUsersEdit = new String[lstDefaultEditors.size()];
				for (int iCounter = 0;
					iCounter < lstDefaultEditors.size();
					iCounter++) {
					strResUsersEdit[iCounter] = ((ETSUser) lstDefaultEditors.get(iCounter)).getUserId();
				}
				udForm.setRestrictedUsersEdit(strResUsersEdit);
				List lstSelectedGroups = new ArrayList();
				lstSelectedGroups.add(getAllUsersGroup(udDAO, strProjectId));
				udForm.setSelectedGroups(lstSelectedGroups);
			}
			
			EdgeAccessCntrl udEdgeAccess = DocumentsHelper.getEdgeAccess(pdRequest);
			String strUserID = udEdgeAccess.gIR_USERN;
			String strUserRole = getUserRole(pdRequest);
			
			if (StringUtil.isNullorEmpty(udForm.getNotifyOption())) {
				udForm.setNotifyOption("to");
			}
			// Check if the form has been submitted with IBM Only.
			// If so, then set Users to IBM Users
//			ETSDoc udDoc = udForm.getDocument();
//			if (udDoc != null
//				&& !DocConstants.SEL_OPT_NONE.equals(udDoc.getIBMOnlyStr())) {
//				if (udDoc.isIbmOnlyOrConf()) {
//					udForm.setUsers(vtIBMUsers);
//				}
//			}

			// Check for groups
			List lstGroups = udDAO.getAllGroups(
							 strProjectId, "", "asc",(strUserRole.equals(Defines.ETS_ADMIN) ||
									strUserRole.equals(Defines.WORKSPACE_MANAGER) || 
									strUserRole.equals(Defines.WORKSPACE_OWNER)), strUserID );
			
			Vector vtIBMGroups = new Vector();
			
			m_pdLog.debug("lstGroups=====>"+lstGroups);
			
			for(int i=0; i<lstGroups.size(); i++)
			{
				Group objGroup = (Group)lstGroups.get(i);
				
				System.out.println(objGroup.getType());
				if(!objGroup.getGroupSecurityClassification().equals(GroupConstants.ETS_PUBLIC))
				{
					vtIBMGroups.add(objGroup);
					m_pdLog.debug("objGroup.getType()"+objGroup.getType());
				}
					
					
			}
			
			if(vtIBMGroups.size()>0)
				m_pdLog.debug("lstIBMGroups=====>"+vtIBMGroups);
			
			
			//ArrayList lstIBMGroups = udDAO.getIBMGroups(lstGroups);
			//System.out.println("lstIBMGroups====>  "+ lstIBMGroups);
			udForm.setGroups(lstGroups);
			udForm.setIBMGroups(vtIBMGroups);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}
	
	private Group getAllUsersGroup(DocumentDAO udDAO, String strProjectId) throws Exception {
        TeamGroupDAO udTeamDAO = new TeamGroupDAO();
        udTeamDAO.setConnection(udDAO.getConnection());
        ETSGroup udETSGroup = 
            udTeamDAO.getGroupDetailsByNameAndProject(
                Defines.GRP_ALL_USERS, 
                strProjectId);
        Group udGroup = new Group();
        udGroup.setDescription(udETSGroup.getGroupDescription());
		 udGroup.setGroupId(udETSGroup.getGroupId());
		 udGroup.setGroupName(udETSGroup.getGroupName());
		 udGroup.setGroupSecurityClassification(udETSGroup.getGroupSecurityClassification());
		 udGroup.setLastTimestamp(udETSGroup.getLastTimestamp());
		 udGroup.setOwner(udETSGroup.getGroupOwner());
		 udGroup.setProjectId(udETSGroup.getProjectId());
		 udGroup.setType(udETSGroup.getGroupType());
		 udGroup.setUserId(udETSGroup.getUserId());
		 	
		 return udGroup;
	}
}