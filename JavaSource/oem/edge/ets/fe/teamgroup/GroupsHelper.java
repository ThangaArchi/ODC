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
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;

import org.apache.commons.logging.Log;


/**
 * @author vishal
 */
public class GroupsHelper {

	/** Stores the Logger Object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(GroupsHelper.class);

	/**
	 * @param strUserID
	 * @param strProjectID
	 * @param iPrivilige
	 * @return
	 */
	public static boolean hasPriviliges(
		String strUserID,
		String strProjectID,
		int iPrivilige) {
		
		TeamGroupDAO udTeamGroupDAO = new TeamGroupDAO();
		boolean bHasPrivilige = false;
		try {
			udTeamGroupDAO.prepare();
			bHasPrivilige =
				udTeamGroupDAO.hasProjectPriv(
					strUserID,
					strProjectID,
					iPrivilige);
			udTeamGroupDAO.cleanup();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				if (udTeamGroupDAO != null) {
					udTeamGroupDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}

		return bHasPrivilige;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static EdgeAccessCntrl getEdgeAccess(HttpServletRequest pdRequest) {
		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(
				GroupConstants.REQ_ATTR_EDGEACCESS);

		return pdAccess;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getUserRole(HttpServletRequest pdRequest) {
		String strUserRole =
			(String) pdRequest.getAttribute(GroupConstants.REQ_ATTR_USERROLE);

		return strUserRole;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isInternal(HttpServletRequest pdRequest) {
		boolean bIsInternal = false;

		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(
				GroupConstants.REQ_ATTR_EDGEACCESS);
		if (pdAccess
			.gDECAFTYPE
			.trim()
			.equals(GroupConstants.DECAFTYPE_INTERNAL)) {
			bIsInternal = true;
		}
		return bIsInternal;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getFormContext(HttpServletRequest pdRequest) {
		return (
			(BaseGroupForm) pdRequest.getAttribute(GroupConstants.GROUP_FORM))
			.getFormContext();
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static BaseGroupForm getGroupForm(ServletRequest pdRequest) {
		return (
			(BaseGroupForm) pdRequest.getAttribute(GroupConstants.GROUP_FORM));
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static BaseGroupForm getGroupForm(HttpServletRequest pdRequest) {
		return (
			(BaseGroupForm) pdRequest.getAttribute(GroupConstants.GROUP_FORM));
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getLinkID(HttpServletRequest pdRequest) {
		String strLinkID = null;
		strLinkID = pdRequest.getParameter(GroupConstants.PARAM_LINKID);

		if (StringUtil.isNullorEmpty(strLinkID)) {
			BaseGroupForm udForm = getGroupForm(pdRequest);
			if (udForm != null) {
				strLinkID = udForm.getLinkid();
			}
		}

		return strLinkID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getTopCatID(HttpServletRequest pdRequest) {
		String strTopCatID = null;
		strTopCatID = pdRequest.getParameter(GroupConstants.PARAM_TOPCATEGORY);

		if (StringUtil.isNullorEmpty(strTopCatID)) {
			BaseGroupForm udForm = getGroupForm(pdRequest);
			if (udForm != null) {
				strTopCatID = udForm.getTc();
			}
		}

		return strTopCatID;
	}
	
	
	public static Vector getAllUsers(HttpServletRequest pdRequest) {
		Vector allUsrVect = new Vector();
		BaseGroupForm udForm = getGroupForm(pdRequest);
		if (udForm != null) {
			allUsrVect = udForm.getUsers();
		}
		return allUsrVect;
	}
	
	
	public static Vector getIBMUsers(HttpServletRequest pdRequest) {
		Vector ibmUsrVect = new Vector();
		BaseGroupForm udForm = getGroupForm(pdRequest);
		if (udForm != null) {
			ibmUsrVect = udForm.getIbmUsers();
		}
		return ibmUsrVect;
	}
	

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getGrpID(HttpServletRequest pdRequest) {
		String strGrpId = null;
		strGrpId = pdRequest.getParameter(GroupConstants.PARAM_GROUPID);

		if (StringUtil.isNullorEmpty(strGrpId)) {
			BaseGroupForm udForm = getGroupForm(pdRequest);
			if (udForm != null) {
				strGrpId = udForm.getGroup().getGroupId();
			}
		}

		return strGrpId;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getGrpAction(HttpServletRequest pdRequest) {
		String strGrpAction = null;
		strGrpAction = pdRequest.getParameter(GroupConstants.ACTION_GROUP);

		//if (StringUtil.isNullorEmpty(strGrpId)) {
		//	BaseDocumentForm udForm = getDocumentForm(pdRequest);
		//	if (udForm != null) {
		//		strGrpId = udForm.getLinkid();
		//	}
		//}

		return strGrpAction;
	}


	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getProjectID(HttpServletRequest pdRequest) {
		String strProjectID = null;
		strProjectID = pdRequest.getParameter(GroupConstants.PARAM_PROJECTID);

		if (StringUtil.isNullorEmpty(strProjectID)) {
			BaseGroupForm udForm = getGroupForm(pdRequest);
			if (udForm != null) {
				strProjectID = udForm.getProj();
			}
		}

		return strProjectID;
	}


	/**
	 * @param pdRequest
	 * @param strKey
	 * @return
	 */
	public static String getParameter(
		HttpServletRequest pdRequest,
		String strKey) {
		String strValue = pdRequest.getParameter(strKey);

		if (StringUtil.isNullorEmpty(strValue)) {
			return StringUtil.EMPTY_STRING;
		} else {
			return strValue;
		}
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String isAuthorized(
		HttpServletRequest pdRequest) {

		String strUserRole = getUserRole(pdRequest);
		String m_strDisableButton = "Y";

		String strUserId = getEdgeAccess(pdRequest).gIR_USERN;
		String strUserType = getEdgeAccess(pdRequest).gDECAFTYPE;
		String strProjectId = GroupsHelper.getProjectID(pdRequest);
		ETSProj proj=null;
		try {
			proj = ETSDatabaseManager.getProjectDetails(strProjectId);
		} catch (SQLException e) {
			System.err.println(e);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		// set the add/modify disable if the user is not owner/admin
		if ((strUserRole.equals(Defines.ETS_ADMIN)
			    || strUserRole.equals(Defines.WORKSPACE_OWNER)
				|| strUserRole.equals(Defines.WORKSPACE_MANAGER)
				|| strUserRole.equals(Defines.WORKFLOW_ADMIN)
				|| strUserRole.equals(Defines.WORKSPACE_MEMBER)) &&
				(strUserType.equals("I"))){
			m_strDisableButton = "N";
		}

		return m_strDisableButton;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String isAuthorized(
		HttpServletRequest pdRequest,
		ETSGroup etsGrp) {

		String strUserRole = getUserRole(pdRequest);
		String m_strDisableButton = "Y";

		String strUserId = getEdgeAccess(pdRequest).gIR_USERN;

		// set the add/modify disable if the user is not owner/admin and group is not for all users
		if (etsGrp.getGroupName().equalsIgnoreCase(Defines.GRP_ALL_USERS)) {
			m_strDisableButton = "Y";
		} else if (strUserRole.equals(Defines.ETS_ADMIN)
				|| strUserRole.equals(Defines.WORKSPACE_OWNER)
				|| strUserRole.equals(Defines.WORKFLOW_ADMIN)
				|| strUserRole.equals(Defines.WORKSPACE_MANAGER)) { 
			m_strDisableButton = "N";
		} else {
			if (etsGrp.getGroupType().equals("PRIVATE")
					&& etsGrp.getGroupOwner().equalsIgnoreCase(strUserId)) {
				m_strDisableButton = "N";
			}
		}
		//System.out.println("In disable box::" + etsGrp.getGroupType() + etsGrp.getGroupOwner() + strUserRole + strUserId + m_strDisableButton);
		return m_strDisableButton;
	}


	/**
	 * @param pdRequest
	 * @return
	 */
	public static EtsPrimaryContactInfo getPrimaryContactInfo(HttpServletRequest pdRequest) {
		return (EtsPrimaryContactInfo) pdRequest.getAttribute(
			GroupConstants.REQ_ATTR_PRIMARYCONTACT);
	}

	/**
	 * @param pdRequest
	 * @param iTabType
	 * @return
	 */
	public static int getTopCatForTab(
		HttpServletRequest pdRequest,
		int iTabType) {

		TeamGroupDAO udDAO = new TeamGroupDAO();
		int iTopCatId = -1;
		try {
			udDAO.prepare();
			int iTopCatID =
				udDAO.getTopCatId(getProjectID(pdRequest), iTabType);
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return iTopCatId;

	}
	
	public static List getFormSelectedUsers(
		HttpServletRequest pdRequest, 
		String strKey) {
			// Get the list of seleted users from hidden variable of the page
			String strSelectedUsrs = getParameter(pdRequest,strKey);
			StringTokenizer strTokens = new StringTokenizer(strSelectedUsrs, ";");
			List lstNewUserIds  = new ArrayList();
			while (strTokens.hasMoreTokens()){
				String user = strTokens.nextToken();
				lstNewUserIds.add(user);
			}
 
 			return lstNewUserIds;
		}
	
	
	/**
	 * @param strProjectId
	 * @throws SQLException
	 * @throws Exception
	 */
	public synchronized static void checkAndCreateAllUsersGroup(String strProjectId) throws SQLException, Exception {
	    TeamGroupDAO udTeamDAO = new TeamGroupDAO();
	    try {
	        udTeamDAO.prepare();
		    ETSGroup udGroup = 
		        udTeamDAO.getGroupDetailsByNameAndProject(
		                Defines.GRP_ALL_USERS, strProjectId);
		    if (udGroup == null) {
		        // Means we need to create this group
				String strNewGroupID = TeamGroupDAO.getUniqueGroupID();

		        ETSGroup udNewGroup = new ETSGroup();
		        udNewGroup.setGroupId(strNewGroupID);
		        udNewGroup.setGroupName(Defines.GRP_ALL_USERS);
		        udNewGroup.setGroupDescription(Defines.GRP_ALL_USERS_DESC);
		        udNewGroup.setGroupType(Defines.GRP_ALL_USERS_TYPE);
		        udNewGroup.setGroupSecurityClassification(Defines.GRP_ALL_USERS_SECURITY);
		        udNewGroup.setProjectId(strProjectId);
		        udNewGroup.setGroupOwner(Defines.GRP_ALL_USERS_OWNER);
		        
		        udTeamDAO.addGroup(udNewGroup);
		    }
	    }
	    catch(SQLException e) {
	        m_pdLog.error(e);
	    }
	    finally {
	        udTeamDAO.cleanup();
	    }
	}

}