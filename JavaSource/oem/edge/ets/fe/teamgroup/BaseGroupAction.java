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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.teamgroup.GroupConstants;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author vishal
 */
public abstract class BaseGroupAction extends Action {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(BaseGroupAction.class);

	/** Stores reference to the Group Data Access Object */
	private TeamGroupDAO m_udTeamGroupDAO;

	/** Generalized forward name for when Action needs to return successfully */
	protected static final String FWD_SUCCESS = "success";

	private static final String ERR_APP_ERROR = "group.app.error";

	/** Generalized forward name for when Action needs to return to connect */
	protected static final String FWD_CONNECT = "/signOn.wss";

	/** Generalized forward name for when Action needs to return to error */
	protected static final String FWD_ERROR = "/displayAccessError.wss";

	/**
	 * The Message to be shown when the user does not have sufficient role
	 * to perform an action
	 */
	private static final String ERR_ACTION_NOT_ALLOWED =
		"groups.error.action.notallowed";

	/** Caches the Resource to Action Mapping */
	private static Map m_mapResourceAction = null;

	/** Caches the Action to Role Mapping */
	private static Map m_mapActionRole = null;

	/**
	 * This method will get invoked by the Struts ActionServlet. It will perform
	 * Authorization (whether the specific access requested is allowed based on
	 * credentials provided), then make a call to executeAction as defined by the
	 * specific derived class.
	 * @param pdMapping
	 * @param pdForm
	 * @param pdRequest
	 * @param pdResponse
	 * @return org.apache.struts.action.ActionForward
	 * @throws java.lang.Exception
	 */
	public ActionForward execute(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		ActionMapping udMapping = (ActionMapping) pdMapping;

		String strProjectID = GroupsHelper.getProjectID(pdRequest);
		ETSProj udProject = null;

		EdgeAccessCntrl m_udEdgeAccess = new EdgeAccessCntrl();
		String m_strUserRole = Defines.INVALID_USER;
		

		try {
			TeamGroupDAO udDAO = getDAO();
			udProject = udDAO.getProjectDetails(strProjectID);
			m_udEdgeAccess.GetProfile(
				pdResponse,
				pdRequest,
				udDAO.getConnection());

			m_strUserRole =
				ETSUtils.checkUserRole(m_udEdgeAccess,udProject.getProjectId());
			m_pdLog.error(
				"USER ROLE for "
					+ m_udEdgeAccess.gIR_USERN
					+ "IS : "
					+ m_strUserRole);

			if (Defines.INVALID_USER.equals(m_strUserRole)) {
				UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(udProject.getProjectType());
				pdResponse.sendRedirect(unBrandedprop.getUnauthorizedURL());
				return pdMapping.findForward("signOn");
			}

			pdRequest.setAttribute(
				GroupConstants.REQ_ATTR_USERROLE,
				m_strUserRole);
			pdRequest.setAttribute(
			GroupConstants.REQ_ATTR_EDGEACCESS,
				m_udEdgeAccess);

			pdRequest.setAttribute(
			GroupConstants.REQ_ATTR_PRIMARYCONTACT,
				udDAO.getProjContactInfo(strProjectID));

			// check for deleted projects
			if (udProject
				.getProject_status()
				.equalsIgnoreCase(GroupConstants.DELETED_PROJECT_FLAG)) {
				return pdMapping.findForward("signOn");
			}

			// Get the breadcrumbs which paint the hierarchy links
			BaseGroupForm udForm = (BaseGroupForm) pdForm;
			pdRequest.setAttribute(GroupConstants.GROUP_FORM, udForm);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			cleanup();
		}
			
		ActionForward pdForward = null;
		try {
			pdForward = executeAction(pdMapping, pdForm, pdRequest, pdResponse);
		} catch (GroupException e) {
			saveErrors(pdRequest, e.getErrors());
			pdForward = pdMapping.getInputForward();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			m_pdLog.error(e);
			ActionErrors pdErrors = new ActionErrors();
			pdErrors.add(
				GroupConstants.MSG_USER_ERROR,
				new ActionError(ERR_APP_ERROR, e.getMessage()));
			saveErrors(pdRequest, pdErrors);
			pdForward = pdMapping.getInputForward();
		} finally {
			cleanup();
		}
		return pdForward;
	}

	/**
	 * @return oem.edge.ets.fe.teamgroup.TeamGroupDAO
	 */
	protected TeamGroupDAO getDAO() throws Exception {

		if (m_udTeamGroupDAO == null) {
			m_udTeamGroupDAO = new TeamGroupDAO();
			m_udTeamGroupDAO.prepare();
		}

		return m_udTeamGroupDAO;
	}

	/**
	 * @throws Exception
	 */
	protected void cleanup() throws Exception {
		if (m_udTeamGroupDAO != null) {
			m_udTeamGroupDAO.cleanup();
		}
		m_udTeamGroupDAO = null;
	}

	/**
	 * @return
	 */
	public EdgeAccessCntrl getEdgeAccess(HttpServletRequest pdRequest) {
		return (EdgeAccessCntrl)pdRequest.getAttribute(GroupConstants.REQ_ATTR_EDGEACCESS);
	}

	/**
	 * @return
	 */
	public String getUserRole(HttpServletRequest pdRequest) {
		return (String) pdRequest.getAttribute(GroupConstants.REQ_ATTR_USERROLE);
	}

	/**
	 * This method is implemented by all classes extending
	 * from BaseGroupAction.
	 * @param pdMapping
	 * @param pdForm
	 * @param pdRequest
	 * @param pdResponse
	 * @return org.apache.struts.action.ActionForward
	 */
	protected abstract ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception;



}
