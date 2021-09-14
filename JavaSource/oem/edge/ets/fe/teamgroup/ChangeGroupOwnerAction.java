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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author vishal
 */
public class ChangeGroupOwnerAction extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(ChangeGroupOwnerAction.class);

	private static final String ERR_NOT_ALLOWED = "group.change.go.not.allowed";
	private static final String ERR_GROUP_SELECT = "error.invalid.groupid";
	private static final String ERR_GROUP_OWNER_SELECT = "group.go.select";

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

		String strProjectId = GroupsHelper.getProjectID(pdRequest);
		EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
		String strUserId = udEdgeAccess.gIR_USERN;
		TeamGroupDAO udDAO = getDAO();

		Connection conn = udDAO.getConnection();
		//Disable the add/modify buttons for unauthorized user
		udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));
		String strUserRole = getUserRole(pdRequest);

/*		&& ( udProject.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)
				&& udProject.getProcess().equalsIgnoreCase(Defines.AIC_WORKFLOW_PROCESS)
				&& !strUserRole.equals(Defines.WORKFLOW_ADMIN)
			)
*/
		ETSProj udProject = udDAO.getProjectDetails(strProjectId);
		if (!strUserRole.equals(Defines.WORKSPACE_OWNER) 
				&& !strUserRole.equals(Defines.WORKSPACE_MANAGER) 
				&& !strUserRole.equals(Defines.WORKFLOW_ADMIN) 
				&& !strUserRole.equals(Defines.ETS_ADMIN))
		{
				throw new GroupException(ERR_NOT_ALLOWED);
		}


		String strForward = FWD_SUCCESS;

		ActionErrors pdErrors = null;

		pdErrors = validate(udForm);

		if (pdErrors.size() > 0) {
			throw new GroupException(pdErrors);
		}

		try {
			
			String grpId = udForm.getGroup().getGroupId();
			ETSGroup udGrp = udDAO.getGroupByIDAndProject(strProjectId,grpId);
			String oldGroupOwner = udGrp.getGroupOwner();

			String strNewGO = udForm.getGroup().getGroupOwner();

			udDAO.updateGroupOwner(strProjectId,grpId,strNewGO);
			udDAO.addNewMemberToGrp(strProjectId,grpId,strNewGO,strUserId);
			
			boolean chkOldGOActiveInWrkspc = ETSDatabaseManager.isUserInProject(udGrp.getGroupOwner(),strProjectId,udDAO.getConnection());
			//sendGroupOwnerChangeEmail(strUserId, grpId, udGrp.getGroupOwner());
			if (!oldGroupOwner.equalsIgnoreCase(strNewGO) && chkOldGOActiveInWrkspc) {
				m_pdLog.debug("Sending mail to the GO on change of GO");
				ETSUserDetails last_user = new ETSUserDetails();
				last_user.setWebId(strUserId);
				last_user.extractUserDetails(udDAO.getConnection());
	
				GroupsMailHandler mailHandler = new GroupsMailHandler();
				mailHandler.sendMailToGOOnChangeGrpOwner(udProject,udGrp,last_user,strNewGO);
			}

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}
		return pdMapping.findForward(strForward);
	}

	private ActionErrors validate(BaseGroupForm udForm) {
		ActionErrors pdErrors = new ActionErrors();

		ETSGroup udGroup = udForm.getGroup();

		// Check selected group Id
		if (GroupConstants
			.SEL_OPT_NONE
			.equals(udForm.getGroup().getGroupId())) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_SELECT));
		}

		// Check selected new Group Owner
		if (GroupConstants
			.SEL_OPT_NONE
			.equals(udForm.getGroup().getGroupOwner())) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_OWNER_SELECT));
		}

		return pdErrors;

	}

}
