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

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author vishal
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DisplayGroupMembersAction extends BaseGroupAction {
	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayGroupMembersAction.class);

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
			String strUserName = getEdgeAccess(pdRequest).gIR_USERN;
			
			int iTopCatID = 0;
			String strTopCatId = udForm.getTc();
			
			String strUserId = "";
			String strAction = udForm.getGroupAction();	
			String[] strGroupIds = udForm.getGroupIds();
			
			TeamGroupDAO udDAO = getDAO();
			
			Vector vt_Groups = udDAO.getGroupsMembers(strProjectId, strGroupIds);
			

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
