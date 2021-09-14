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
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author vishal
 */
public class AddModifyGroupAction extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(AddModifyGroupAction.class);

	private static final String ERR_GROUP_NAME = "group.name.error";
	private static final String ERR_GROUP_DESC = "group.description.error";
	private static final String ERR_GROUP_MEMBERS = "group.members.error";
	private static final String ERR_GROUP_MEMBERS_IBM_ONLY = "group.ibmmembers.error";
	private static final String ERR_SEC_NOTSEL =
		"security.classification.error";
	private static final String ERR_DUP_GROUPNAME = "group.name.duplicate";
	private static final String ERR_NOT_ALLOWED = "group.create.not.allowed";
	private static final String ERR_GRP_TYP_NOTSEL = "group.type.error";
	private static final String ERR_NO_PRIV = "group.public.noprivil";
	private static final String ERR_SYSTEM_GROUPNAME = "group.system.reseved.name";

	/**
	 * @see oem.edge.ets.fe.teamgroup.BaseGroupAction#executeAction(
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
		EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
		ETSGroup udOldGroup = null;

		//Disable the add/modify buttons for unauthorized user
		udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));
		
		List lstNewUserIds  = null;
		lstNewUserIds = GroupsHelper.getFormSelectedUsers(pdRequest,"Selmembers");
		// dummy user passed to identify the error message
		lstNewUserIds.remove("DUMMY_USER");

		String strAction = GroupsHelper.getGrpAction(pdRequest);
		// set the page title
		if (strAction.equals(GroupConstants.ACTION_UPDATE_GROUP)) {
			pdRequest.setAttribute(GroupConstants.REQ_ATTR_TITLE,"Modify Group");
		} else {
			pdRequest.setAttribute(GroupConstants.REQ_ATTR_TITLE,"Create Group");
		}

		ETSGroup udGroup = udForm.getGroup();

		String  strOldGroupID = "";
		boolean bIsUpdateGroup = GroupConstants.ACTION_UPDATE_GROUP.equals(strAction);


		if (bIsUpdateGroup && !StringUtil.isNullorEmpty(udGroup.getGroupId())) {
			strOldGroupID = udGroup.getGroupId();
		}

		try {
			String strProjectID = GroupsHelper.getProjectID(pdRequest);
			udGroup.setProjectId(strProjectID);
			
			TeamGroupDAO udDAO = getDAO();
			ETSProj udProject = udDAO.getProjectDetails(strProjectID);
			
			// check user is authorized to add/modify the group
			String strUserRole = GroupsHelper.getUserRole(pdRequest);
			if (!strUserRole.equals(Defines.WORKSPACE_MANAGER) 
			    && !strUserRole.equals(Defines.WORKSPACE_OWNER) 
			    && !strUserRole.equals(Defines.ETS_ADMIN)
			    && !strUserRole.equals(Defines.WORKFLOW_ADMIN)
				&& ! strUserRole.equals(Defines.WORKSPACE_MEMBER)) {
					throw new GroupException(ERR_NOT_ALLOWED);
			}
			
			ActionErrors pdErrors = null;

			pdErrors =
				validate(
					udDAO,
					udForm,
					lstNewUserIds,
					bIsUpdateGroup);

			if (pdErrors.size() > 0) {
				throw new GroupException(pdErrors);
			}

			udGroup.setProjectId(strProjectID);
			String strLastUserName = getEdgeAccess(pdRequest).gIR_USERN;
			List lstOldGroupMembers = new ArrayList();
			String strDisableFlag = "";
			if (bIsUpdateGroup) {
				//udGroup.setGroupOwner(strLastUserName);
				udOldGroup = udDAO.getGroupByIDAndProject(strProjectID,strOldGroupID);
				//boolean bSuccess = udDAO.updateGroupProperties(udGroup);
				lstOldGroupMembers = udDAO.getGroupMembersList(strProjectID,strOldGroupID,"","ASC",false);
				strDisableFlag = GroupsHelper.isAuthorized(pdRequest,udOldGroup);
				
				// If the Grp type is Public check the GO is having correct Privilge
				if (udForm.getGroup().getGroupType().equals("PUBLIC")) {
					String grpOwner = udOldGroup.getGroupOwner();
					if (!ETSDatabaseManager.hasProjectPriv(grpOwner,strProjectID,Defines.ADMIN)) {
						throw new GroupException(ERR_NO_PRIV);
					}
				}

			} 
			else {
				//String strNewGroupID = udDAO.getUniqueGroupID();
				//udGroup.setGroupId(strNewGroupID);
				udGroup.setGroupOwner(strLastUserName);
				//boolean bSuccess = udDAO.addGroup(udGroup);
				strDisableFlag = GroupsHelper.isAuthorized(pdRequest,udGroup);
			}
			udForm.setGroup(udGroup);

			/**********Check the user is eligible to create a Group with correct Sec classification ****/
			
			
			udForm.setDisableButtons(strDisableFlag);
			if (strDisableFlag.equals("Y")) {
				throw new GroupException(ERR_NOT_ALLOWED);
			}
			/****************************/

			// Create the list of users which needs to be added/deleted
			String strAddUser = StringUtil.EMPTY_STRING;
			String strRemoveUser = StringUtil.EMPTY_STRING;	
			List lstOldUserIds = new ArrayList();
			
			for (int i = 0; i < lstOldGroupMembers.size(); i++) {
				String strUserIds = ((ETSUser)lstOldGroupMembers.get(i)).getUserId();
				lstOldUserIds.add(strUserIds);
			}

			// Get the IBMUsers for the workspace
			Vector vtUsers = udDAO.getProjMembers(strProjectID, true);
			Vector vtIBMUsers = udDAO.getIBMMembers(vtUsers);
			List lstIBMUsers = new ArrayList();
			for (int i=0; i < vtIBMUsers.size(); i++) {
				String strUser = ((ETSUser)vtIBMUsers.elementAt(i)).getUserId();
				lstIBMUsers.add(strUser);
			}
			/* if Group security classification is IBM only remove Non IBMERS from selected list */
			if (udGroup.getGroupSecurityClassification().equals(String.valueOf(Defines.ETS_IBM_ONLY))){
				List lstNewIBMUserIds = new ArrayList();
				for (int i=0; i < lstNewUserIds.size(); i++) {
					String struser = (String) lstNewUserIds.get(i);
					if (lstIBMUsers.contains(struser)) {
						lstNewIBMUserIds.add(struser);
					}
				}
				lstNewUserIds = new ArrayList();
				// removed non-ibmers from the list
				lstNewUserIds.addAll(lstNewIBMUserIds);
				// if no users are selected for IBM only grp .. throw excep
				if (lstNewIBMUserIds.size() < 1 ) {
					pdErrors.add(
							GroupConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_GROUP_MEMBERS_IBM_ONLY));
					if (pdErrors.size() > 0) {
						throw new GroupException(pdErrors);
					}
				}
			}
			// update grp details
			if (bIsUpdateGroup) {
				boolean bSuccess = udDAO.updateGroupProperties(udGroup);
			} 
			else {
				String strNewGroupID = TeamGroupDAO.getUniqueGroupID();
				udGroup.setGroupId(strNewGroupID);
				udGroup.setGroupOwner(strLastUserName);
				boolean bSuccess = udDAO.addGroup(udGroup);				 		
			}
			udForm.setGroup(udGroup);

			// users who are not seleted while modification of group details
			for(int i=0; i < lstOldUserIds.size(); i++) {
				String strUserid = (String) lstOldUserIds.get(i);
				if (!lstNewUserIds.contains(strUserid)) {
					if (!strRemoveUser
						.equals(StringUtil.EMPTY_STRING))
						strRemoveUser = strRemoveUser + ",'" + strUserid + "'";
					else
						strRemoveUser = "'" + strUserid + "'";
				}
			}
			//new users who are seleted while creation/modification of group details
			for(int i=0; i < lstNewUserIds.size(); i++) {
				String strUserid = (String) lstNewUserIds.get(i);
				if (!lstOldUserIds.contains(strUserid)) {
					if (!strAddUser
						.equals(StringUtil.EMPTY_STRING))
						strAddUser =
								strAddUser
								+ ",('"
								+ udGroup.getGroupId()
								+ "','"
								+ strUserid
								+ "','"
								+ strLastUserName
								+ "',current timestamp)";
					else
						strAddUser =
								strAddUser
								+ "('"
								+ udGroup.getGroupId()
								+ "','"
								+ strUserid
								+ "','"
								+ strLastUserName
								+ "',current timestamp)";
				}
			}
			boolean bSuccess = udDAO.updateGroupMembersList(strAddUser, strRemoveUser, udGroup);
			
			
			//Add group owner to the group always
			udGroup = udDAO.getGroupByIDAndProject(strProjectID,udGroup.getGroupId());
			m_pdLog.debug("Group owner added to the group members list::" + udGroup.getGroupId() + "::" + udGroup.getGroupOwner());
			udDAO.addNewMemberToGrp(strProjectID,udGroup.getGroupId(),udGroup.getGroupOwner(),strLastUserName);

			// send mail to GO , if the editor is not GO
			boolean chkGroupOwnerActiveInWrkspc = ETSDatabaseManager.isUserInProject(udGroup.getGroupOwner(),udProject.getProjectId(),udDAO.getConnection());
			if (!strLastUserName.equalsIgnoreCase(udGroup.getGroupOwner()) 
					&& chkGroupOwnerActiveInWrkspc ) {
				ETSUserDetails last_user = new ETSUserDetails();
				last_user.setWebId(strLastUserName);
				last_user.extractUserDetails(udDAO.getConnection());
				m_pdLog.debug("Group Edit mail sent for grpId::" + udGroup.getGroupId() + "::Group Owner::" + udGroup.getGroupOwner());
				GroupsMailHandler mailHandler = new GroupsMailHandler();
				mailHandler.sendMailToGOOnEditGrp(udProject,udGroup,last_user);
			}
			
		} catch (SQLException e) {
			e.printStackTrace(System.out);
			throw e;
		} finally {
			super.cleanup();
		}

		pdRequest.setAttribute(GroupConstants.GROUP_FORM, udForm);

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udDAO
	 * @param udForm
	 * @param lstNewUserIds
	 * @param bIsUpdateGroup
	 * @return
	 */
	private ActionErrors validate(
		TeamGroupDAO udDAO,
		BaseGroupForm udForm,
		List lstNewUserIds,
		boolean bIsUpdateGroup)
		throws SQLException {
		ActionErrors pdErrors = new ActionErrors();

		ETSGroup udGroup = udForm.getGroup();
		String strGrpName = StringUtil.trim(udGroup.getGroupName());
		// Check Group Name
		if ((!bIsUpdateGroup) && (StringUtil.isNullorEmpty(strGrpName))
			|| (strGrpName.length() > GroupConstants.MAX_NAME_LENGTH)) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_NAME));
		} else if (!bIsUpdateGroup) {
			String strProjectId = udGroup.getProjectId();
			int iCount = udDAO.getGroupByNameAndProject(strProjectId, strGrpName);
			if (iCount > 0) {
				// Means group with this name already exists in this category
				pdErrors.add(
					GroupConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_DUP_GROUPNAME));
			}
			boolean systemReservedGrpName = parseGroupName(strGrpName);
			if (systemReservedGrpName) {
				pdErrors.add(GroupConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_SYSTEM_GROUPNAME));
			}
		}
		
		// check if there is atleast one member for the selected group
		if (lstNewUserIds.size() < 1) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_MEMBERS));
		}
		// Check Group Descrption
		if (!StringUtil.isNullorEmpty(udGroup.getGroupDescription())
			&& (udGroup.getGroupDescription().length()
				> GroupConstants.MAX_DESCRIPTION_LENGTH)) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_DESC));
		}

		// Check Group Type
		if (GroupConstants
			.SEL_OPT_NONE
			.equals(udForm.getGroup().getGroupType())) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GRP_TYP_NOTSEL));
		}

		// Check Security Classification
		if (GroupConstants
			.SEL_OPT_NONE
			.equals(udForm.getGroup().getGroupSecurityClassification())) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_SEC_NOTSEL));
		}

		return pdErrors;
	}

	private boolean parseGroupName(String strGroupName) {
		boolean bSystemReservedName = false;
		
		if (strGroupName.equalsIgnoreCase("allusers") || strGroupName.equalsIgnoreCase(Defines.GRP_ALL_USERS)) {
			bSystemReservedName = true;
		} else {
			String delim = " \t\n\r\f,'-+.\"";
			StringTokenizer st = new StringTokenizer(strGroupName,delim,false);
			if (st.countTokens() == 2) {
				String firstToken = st.nextToken();
				String secondToken = st.nextToken();
				if (firstToken.equalsIgnoreCase("All") && (secondToken.equalsIgnoreCase("users"))) {
					bSystemReservedName = true;
				}
			} else if(st.countTokens() == 1) {
				String firstToken = st.nextToken();
				if (firstToken.equalsIgnoreCase("AllUsers")) {
					bSystemReservedName = true;
				}
			}
		}
		
		return bSystemReservedName;
	}
}
