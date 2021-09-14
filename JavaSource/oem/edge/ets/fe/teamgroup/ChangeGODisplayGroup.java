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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author vishal
 */
public class ChangeGODisplayGroup extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(ChangeGODisplayGroup.class);

	private static final String ERR_NOT_ALLOWED = "group.change.go.not.allowed";
	private static final String ERR_GROUP_SELECT = "error.invalid.groupid";

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

		//Disable the add/modify buttons for unauthorized user
		udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));
		String strUserRole = getUserRole(pdRequest);

		if (!strUserRole.equals(Defines.WORKSPACE_OWNER) &&
			!strUserRole.equals(Defines.WORKSPACE_MANAGER) &&
			!strUserRole.equals(Defines.WORKFLOW_ADMIN) &&
			!strUserRole.equals(Defines.ETS_ADMIN)) {
				throw new GroupException(ERR_NOT_ALLOWED);
		}

		String strForward = FWD_SUCCESS;

		try {

			Vector vtGroups = udDAO.getAllGroups(strProjectId,"","ASC",true,strUserId);

			udForm.setGroupList(vtGroups);

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}
		return pdMapping.findForward(strForward);
	}

}
