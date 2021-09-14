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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayGroupListAction extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayGroupListAction.class);

	private static final String ERR_INVALID_GROUP = "error.invalid.groupid";

	private static final String ERR_UNAUTHORIZED_ACCESS =
		"group.error.action.notallowed";

	/**
	 * @see oem.edge.ets.fe.teamgroup.BaseGrouptAction#executeAction(
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

		try {
			BaseGroupForm udForm = (BaseGroupForm) pdForm;
			String strProjectId = GroupsHelper.getProjectID(pdRequest);
			
			//Disable the add/modify buttons for unauthorized user
			udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));

			int iTopCatID = 0;
			String strTopCatId = udForm.getTc();
			
			String strUserId = GroupsHelper.getEdgeAccess(pdRequest).gIR_USERN;
			String strAction = udForm.getGroupAction();	
			
			boolean bIsAdmin = false;
			if (GroupsHelper.getUserRole(pdRequest).equals(Defines.WORKSPACE_OWNER) ||
					GroupsHelper.getUserRole(pdRequest).equals(Defines.WORKSPACE_MANAGER) ||
					GroupsHelper.getUserRole(pdRequest).equals(Defines.WORKFLOW_ADMIN) ||
					GroupsHelper.getUserRole(pdRequest).equals(Defines.ETS_ADMIN)) {
				bIsAdmin = true;
			}
			
			String strSortByParam = udForm.getSortBy();
			String strSortParam = udForm.getSort();

			if (StringUtil.isNullorEmpty(strSortByParam)) {
				strSortByParam = Defines.SORT_BY_NAME_STR;
			}
			if (StringUtil.isNullorEmpty(strSortParam)) {
				strSortParam = Defines.SORT_ASC_STR;
			}
			//check and create the default All users grp
			GroupsHelper.checkAndCreateAllUsersGroup(strProjectId);
			
			TeamGroupDAO udDAO = getDAO();
			
			Vector vt_Groups = udDAO.getAllGroups(strProjectId, strSortByParam, strSortParam, bIsAdmin, strUserId);
			String strUserName = getEdgeAccess(pdRequest).gIR_USERN;

			udForm.setGroupList(vt_Groups);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}
		return pdMapping.findForward(FWD_SUCCESS);
	}
}
