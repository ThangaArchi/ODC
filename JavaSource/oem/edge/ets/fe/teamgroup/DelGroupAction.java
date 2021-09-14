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

import java.util.Vector;
import java.util.StringTokenizer;
import java.sql.SQLException;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;


/**
 * @author vishal
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DelGroupAction extends BaseGroupAction {

	private static final Log m_pdLog = EtsLogger.getLogger(DelGroupAction.class);

	private static final String ERR_NOT_ALLOWED = "group.delete.not.allowed";
	private static final String ERR_NO_PRIVILIGE = "group.no.privilege";
	private static final String ERR_DEPEND_DOCS_EXIST = "group.docs.dependent";

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.teamgroup.BaseGroupAction#executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	/** Stores the Logging object */

	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {
		// TODO Auto-generated method stub
		BaseGroupForm udForm = (BaseGroupForm) pdForm;

		String strProjectId = GroupsHelper.getProjectID(pdRequest);
		EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
		String strUserId = udEdgeAccess.gIR_USERN;
		TeamGroupDAO udDAO = getDAO();
		ETSProj udProject = udDAO.getProjectDetails(strProjectId);


		//Disable the add/modify buttons for unauthorized user
		udForm.setDisableButtons(GroupsHelper.isAuthorized(pdRequest));

		String strForward = FWD_SUCCESS;

		try {
			String strUserRole = getUserRole(pdRequest);
			// get grpid from attribute and change to array of string
			String strDelGrpIds = StringUtil.EMPTY_STRING;
			strDelGrpIds = (String) pdRequest.getParameter("delgrpId");
			String[] strGroupIds = convertStringToArray(strDelGrpIds);

			Vector vtGroups = udDAO.getGroupByIDAndProject(strProjectId,strGroupIds);

			//ETSProj udProject = udDAO.getProjectDetails(strProjectId);

			// check the user has got access to delete all grps
			ActionErrors pdErrors = null;
			pdErrors = validatePriviligesToDelete(vtGroups, strUserRole, strUserId);

			if (pdErrors.size() > 0) {
				throw new GroupException(pdErrors);
			}

			// check dependent docs
			String strDelGrpIdString = StringUtil.EMPTY_STRING;
			for (int iCounter=0; iCounter < strGroupIds.length; iCounter++) {
				if (iCounter > 0){
					strDelGrpIdString = strDelGrpIdString + ",";
				}
				strDelGrpIdString = strDelGrpIdString + "'" + strGroupIds[iCounter] + "'";
			}
			
			int iNoOFDependentDocs = udDAO.getAllDocsCountForGrp(strProjectId,strDelGrpIdString);

			if (iNoOFDependentDocs > 0) {
				throw new GroupException(ERR_DEPEND_DOCS_EXIST);
			}
			boolean bSuccess = false;

			bSuccess = udDAO.delGroup(strProjectId, strGroupIds, udEdgeAccess.gIR_USERN);
			GroupsMailHandler mailHandler = new GroupsMailHandler();
//			udForm.setGroupIds(null);
			//send mail to Group Owner for the deleted Grps
			for (int i=0; i < vtGroups.size(); i++) {
				ETSGroup grp = (ETSGroup)vtGroups.elementAt(i);
				String strGrpOwner = grp.getGroupOwner();
				if (!strGrpOwner.equalsIgnoreCase(strUserId)) {
					boolean chkUserInWrkspc = ETSDatabaseManager.isUserInProject(strGrpOwner,strProjectId,udDAO.getConnection());
					if (chkUserInWrkspc) {
						ETSUserDetails userDets = new ETSUserDetails();
						userDets.setWebId(strUserId);
						userDets.extractUserDetails(udDAO.getConnection());
						mailHandler.sendMailToGOOnDelGroup(udProject,grp,userDets);
					} else {
						m_pdLog.debug("The Group Owner is not active in Wrkspce");
					}
				}
			} // end of send mail

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}
		return pdMapping.findForward(strForward);

	}

	private String[] convertStringToArray(String strDelGrpIds){
		StringTokenizer strTokens = new StringTokenizer(strDelGrpIds, ",");
		String[] strGrpIds = new String[strTokens.countTokens()];
		int iCounter = 0;
		while (strTokens.hasMoreTokens()){
			strGrpIds[iCounter] = strTokens.nextToken();
			iCounter++;
		}

		return strGrpIds;
	}

	/**
	 * @param vtGrpsToBeDeleted
	 * @param strUserRole
	 * @return
	 */
	private ActionErrors validatePriviligesToDelete(
		Vector vtGrpsToBeDeleted,
		String strUserRole,
		String strUserId)
		throws Exception {
		
		ActionErrors pdErrors = new ActionErrors();
		int icounter = 0;
		String strGrpsNoAccess = "";
		// Check if no grps are selected
		for (icounter = 0; icounter < vtGrpsToBeDeleted.size(); icounter ++) {
			ETSGroup grp = (ETSGroup) vtGrpsToBeDeleted.elementAt(icounter);
			if (grp.getGroupSecurityClassification().equals("PRIVATE")) {
				if ((strUserRole.equals(Defines.WORKSPACE_MANAGER) || 
					strUserRole.equals(Defines.WORKSPACE_OWNER) ||
					strUserRole.equals(Defines.ETS_ADMIN)) || 
					(strUserRole.equals(Defines.WORKSPACE_MEMBER) &&
					grp.getGroupOwner().equalsIgnoreCase(strUserId))) {
					break;
				} else {
					if (!strGrpsNoAccess.equals(StringUtil.EMPTY_STRING)){
						strGrpsNoAccess = strGrpsNoAccess + ", ";
					}
					strGrpsNoAccess = strGrpsNoAccess + grp.getGroupName();
				}
			}
		}

		if (!strGrpsNoAccess.equals(StringUtil.EMPTY_STRING)) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_NO_PRIVILIGE, strGrpsNoAccess));
		}

		return pdErrors;
	}


}
