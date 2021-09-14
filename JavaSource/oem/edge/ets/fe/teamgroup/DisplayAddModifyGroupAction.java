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

package oem.edge.ets.fe.teamgroup;

import java.sql.SQLException;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author vishal
 */
public class DisplayAddModifyGroupAction extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayAddModifyGroupAction.class);

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

		BaseGroupForm udForm = (BaseGroupForm) pdForm;
		
		String strAction = GroupsHelper.getGrpAction(pdRequest);

		if (StringUtil.isNullorEmpty(strAction)) {
			strAction = GroupConstants.ACTION_ADD_GROUP;
		}
	
		// set the page title
		if (strAction.equals(GroupConstants.ACTION_ADD_GROUP)) {
			pdRequest.setAttribute(GroupConstants.REQ_ATTR_TITLE,"Create Group");
		} else {
			pdRequest.setAttribute(GroupConstants.REQ_ATTR_TITLE,"Modify Group");
		}
		
		String strProjectId = GroupsHelper.getProjectID(pdRequest);
		String strSortBy = "";
		String ad = Defines.SORT_ASC_STR;
		//Disable the add/modify buttons for unauthorized user
		udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));
		
		//get the selected list of users if page is loaded after errors		
		List lstNewUserIds  = null;
		lstNewUserIds = GroupsHelper.getFormSelectedUsers(pdRequest,"Selmembers");

		try {
			TeamGroupDAO udDAO = getDAO();
			Vector vtUsers = null;
			Vector vtIBMUsers = null;
			List lstGroupMembers = null;
			List lstAvailableMembers = null;
			vtUsers = udDAO.getProjMembers(strProjectId, true);
			vtIBMUsers = udDAO.getIBMMembers(vtUsers);
			
			udForm.setUsers(vtUsers);
			udForm.setIbmUsers(vtIBMUsers);

			if (strAction.equals(GroupConstants.ACTION_ADD_GROUP)
				|| strAction.equals(GroupConstants.ACTION_UPDATE_GROUP)) {
					List mems = new ArrayList();
					lstGroupMembers = mems;
					lstAvailableMembers = udDAO.getProjMembersList(strProjectId, true);
			} // Pre-populate the form in case of ACTION_UPDATE_GROUP
			if (strAction.equals(GroupConstants.ACTION_UPDATE_GROUP)) {
				String strGroupID = GroupsHelper.getGrpID(pdRequest);
				ETSGroup udGroup = udDAO.getGroupByIDAndProject(strProjectId, strGroupID);
				if(udGroup == null){
					pdRequest.setAttribute("grpDel",udGroup);
				}else{
					pdRequest.setAttribute("grpDel",udGroup);	
					udForm.setGroup(udGroup);
					lstGroupMembers = udDAO.getGroupMembersList(strProjectId, strGroupID, strSortBy,ad,false);
					lstAvailableMembers = udDAO.getAvailableMembersList(strProjectId, strGroupID);
					//Disable the add/modify buttons for unauthorized user
					udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest,udGroup));

					if (udForm.getDisableButtons().equals("Y")) {
						pdRequest.setAttribute(GroupConstants.REQ_ATTR_TITLE,"View Group");
					}
				} 
			}
			setFormUsersListBox(lstGroupMembers,lstAvailableMembers,lstNewUserIds,vtUsers,strAction,udForm);
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}
	
	private void setFormUsersListBox(
		List lstGroupMembers,
		List lstAvailableMembers,
		List lstNewUserIds,
		Vector vtProjMembers,
		String strAction, 
		BaseGroupForm udForm){

			// update the list boxes, if there were errors on the page  
			if (lstNewUserIds.contains("DUMMY_USER")) {
				lstGroupMembers = new ArrayList();
				lstAvailableMembers = new ArrayList();
				for (int iCounter=0; iCounter < vtProjMembers.size(); iCounter ++) {
					String strUser = ((ETSUser)vtProjMembers.elementAt(iCounter)).getUserId();
					if (lstNewUserIds.contains(strUser)) {
						lstGroupMembers.add((ETSUser)vtProjMembers.elementAt(iCounter));
					} else {
						lstAvailableMembers.add((ETSUser)vtProjMembers.elementAt(iCounter));
						System.out.println("added to available- " + strUser);
					}
				
				}
			}		
			udForm.setAvailableMembersList(lstAvailableMembers);
			udForm.setGroupMembersList(lstGroupMembers);
			
			return;
		}
}
