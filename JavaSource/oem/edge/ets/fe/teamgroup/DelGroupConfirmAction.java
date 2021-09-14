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
import oem.edge.ets.fe.ETSDatabaseManager;
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
public class DelGroupConfirmAction extends BaseGroupAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(DelGroupConfirmAction.class);

	private static final String ERR_NOT_ALLOWED = "group.delete.not.allowed";
	private static final String ERR_NO_PRIVILIGE = "group.no.privilege";
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

		ActionErrors pdErrors = null;
		boolean gNull = false;

		pdErrors =
			validate(
				udDAO,
				udForm);


		if (pdErrors.size() > 0) {
			throw new GroupException(pdErrors);
		}

		String strForward = FWD_SUCCESS;
		String strSortByParam = udForm.getSortBy();
		String strSortParam = udForm.getSort();

		if (StringUtil.isNullorEmpty(strSortByParam)) {
			strSortByParam = Defines.SORT_BY_NAME_STR;
		}
		if (StringUtil.isNullorEmpty(strSortParam)) {
			strSortParam = Defines.SORT_ASC_STR;
		}

		try {
			String strUserRole = getUserRole(pdRequest);
			String[] strGroupIds = udForm.getGroupIds();
			boolean bIsAdmin = false;
			if (strUserRole.equals(Defines.WORKSPACE_MANAGER) || strUserRole.equals(Defines.WORKSPACE_OWNER) || strUserRole.equals(Defines.ETS_ADMIN)){
				bIsAdmin = true;
			}
			
			for (int iCounter=0; iCounter < strGroupIds.length; iCounter++){
			 
				ETSGroup grpNull = udDAO.getGroupByIDAndProject(strProjectId,strGroupIds[iCounter]);
				if (grpNull == null){
					    pdRequest.setAttribute("grpRem",grpNull);
					    gNull = true;
				}
			}
			
			if(!gNull){
						
			
			Vector vtGroups = udDAO.getGroupByIDAndProject(strProjectId,strGroupIds);

			// check the user has got access to delete all grps
			pdErrors = validatePriviligesToDelete(vtGroups, strUserRole, strUserId);

			if (pdErrors.size() > 0) {
				throw new GroupException(pdErrors);
			}

			// Check if the documents are given access using the groups. 
			// if yes then show the details of the docs and request to change the access 
			// before deleting the grps

			boolean bSuccess = false;
			//bSuccess = udDAO.delGroup(strProjectId, strGroupIds, udEdgeAccess.gIR_USERN);
			udForm.setGroupList(vtGroups);
			udForm.setGroupIds(strGroupIds);
			
			//selected grpid are stored in hidden variable as attribute
			String strDelGrpIds = StringUtil.EMPTY_STRING;
			String strDelGrpIdString = StringUtil.EMPTY_STRING;
			for (int iCounter=0; iCounter < strGroupIds.length; iCounter++) {
				if (iCounter > 0){
					strDelGrpIds = strDelGrpIds + ",";
					strDelGrpIdString = strDelGrpIdString + ",";
				}
				strDelGrpIds = strDelGrpIds + strGroupIds[iCounter];
				strDelGrpIdString = strDelGrpIdString + "'" + strGroupIds[iCounter] + "'";
			}
			
			int iNoOFDependentDocs = udDAO.getAllDocsCountForGrp(strProjectId,strDelGrpIdString);
			boolean bGrpDependentNotification = false;
			Vector vtDocs = new Vector();
			if (iNoOFDependentDocs > 0) {
				vtDocs = udDAO.getAllDocsForGrp(strProjectId,strDelGrpIdString,strSortByParam,strSortParam,bIsAdmin,strUserId);
				udForm.setDisableButtons("Y");
				udForm.setGrpDependentDocuments(true);
			} else {
				bGrpDependentNotification = udDAO.chkGrpUsedForDocNotification(strProjectId,vtGroups);
				udForm.setDisableButtons("N");
				udForm.setGrpDependentDocuments(false);
			}
			int iDocTopCatId = ETSDatabaseManager.getTopCatId(strProjectId, Defines.DOCUMENTS_VT, udDAO.getConnection());
			
			udForm.setGrpDependentNotification(bGrpDependentNotification);
			udForm.setDocs(vtDocs);
			udForm.setDocumentsTopCatId(iDocTopCatId);
			
			pdRequest.setAttribute("delgrpId",strDelGrpIds);
			
			ETSGroup grpNotNull = udDAO.getGroupByIDAndProject(strProjectId,strGroupIds[0]);
			pdRequest.setAttribute("grpRem",grpNotNull);

	  	 } 

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} finally {
			super.cleanup();
		}
		return pdMapping.findForward(strForward);
	}

	/**
	 * @param udForm
	 * @param udDAO
	 * @return
	 */
	private ActionErrors validate(
		TeamGroupDAO udDAO,
		BaseGroupForm udForm)
		throws Exception {
		ActionErrors pdErrors = new ActionErrors();

		ETSGroup udGroup = udForm.getGroup();
		// Check if no grps are selected
		if ( (udForm.getGroupIds() == null) || (udForm.getGroupIds().length < 1 )) {
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_GROUP_SELECT));
		}

		return pdErrors;
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
