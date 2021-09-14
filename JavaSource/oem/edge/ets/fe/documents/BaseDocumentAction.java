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

package oem.edge.ets.fe.documents;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.documents.processor.DocActionMapping;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMOffice;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public abstract class BaseDocumentAction extends Action {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(BaseDocumentAction.class);

	/** Generalized forward name for when Action needs to return successfully */
	protected static final String FWD_SUCCESS = "success";

	private static final String ERR_APP_ERROR = "doc.app.error";

	/** Generalized forward name for when Action needs to return to connect */
	protected static final String FWD_CONNECT = "/signOn.wss";

	/** Generalized forward name for when Action needs to return to error */
	protected static final String FWD_ERROR = "/displayAccessError.wss";

	/**
	 * The Message to be shown when the user does not have sufficient role
	 * to perform an action
	 */
	private static final String ERR_ACTION_NOT_ALLOWED =
		"documents.error.action.notallowed";

	/** Caches the Resource to Action Mapping */
	private static Map m_mapResourceAction = null;

	/** Caches the Action to Role Mapping */
	private static Map m_mapActionRole = null;

	private static final String SESS_EDGEACCESS = "ETS.EDGEACCESSCNTRL";
	private static final String SESS_USERROLE = "ETS.USERROLE";
	private static final String SESS_SUPERADMIN = "ETS.ISSUPERADMIN";
	private static final String SESS_EXECUTIVE = "ETS.ISEXECUTIVE";
	//protected EdgeAccessCntrl udEdgeAccess = new EdgeAccessCntrl();
//	private String m_strUserRole = null;
//	private boolean m_bIsSuperAdmin = false;
//	private boolean m_bIsExecutive = false;
//	private String m_strTransType = null;

//	private EdgeAccessCntrl m_udEdgeAccess = null;

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

		DocActionMapping udMapping = (DocActionMapping) pdMapping;
		String strTransType = udMapping.getAction();

		String strProjectID = DocumentsHelper.getProjectID(pdRequest);
		ETSProj udProject = null;

		HttpSession pdSession = pdRequest.getSession();
		EdgeAccessCntrl udEdgeAccess = new EdgeAccessCntrl();

		DocumentDAO udDAO = null;
		String strUserRole = "";

		//String strUserRole = (String) pdSession.getAttribute(SESS_USERROLE);
		try {
			udDAO = getDAO();
			udProject = udDAO.getProjectDetails(strProjectID);
			udEdgeAccess.GetProfile(
				pdResponse,
				pdRequest,
				udDAO.getConnection());

			if (pdSession.getAttribute(SESS_EDGEACCESS) == null) {
			    pdSession.setAttribute(SESS_EDGEACCESS, udEdgeAccess);
			}
			strUserRole = ETSUtils.checkUserRole(
						udEdgeAccess,
						udProject.getProjectId(),
						udDAO.getConnection());

			pdSession.setAttribute(SESS_USERROLE, strUserRole);
			
			m_pdLog.error(
				"USER ROLE for "
					+ udEdgeAccess.gIR_USERN
					+ "IS : "
					+ strUserRole);

			if (Defines.INVALID_USER.equals(strUserRole)) {
				UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(udProject.getProjectType());
				pdResponse.sendRedirect(unBrandedprop.getUnauthorizedURL());
				return pdMapping.findForward("signOn");
			}

			pdRequest.setAttribute(
				DocConstants.REQ_ATTR_USERROLE,
				strUserRole);
			pdRequest.setAttribute(
				DocConstants.REQ_ATTR_EDGEACCESS,
				udEdgeAccess);

			pdRequest.setAttribute(
				DocConstants.REQ_ATTR_PRIMARYCONTACT,
				udDAO.getProjContactInfo(strProjectID));

			boolean bContinue =
				processPath(udDAO, pdMapping, pdRequest, pdResponse);
			if (!bContinue) {
				UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(udProject.getProjectType());
				pdResponse.sendRedirect(unBrandedprop.getUnauthorizedURL());
				return pdMapping.findForward("signOn");
			}
			// check for deleted projects
			if (udProject
				.getProject_status()
				.equalsIgnoreCase(DocConstants.DELETED_PROJECT_FLAG)) {
				return pdMapping.findForward("signOn");
			}

			int iCurrentCatId = 0;

			// Get the breadcrumbs which paint the hierarchy links
			BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
			if (!StringUtil.isNullorEmpty(udForm.getCc())) {
				try {
					iCurrentCatId = Integer.parseInt(udForm.getCc());
					udForm.setBreadCrumbs(udDAO.getBreadCrumbTrail(iCurrentCatId));
				}
				catch(NumberFormatException nfe) {
					// Means it can be PMO
					ETSPMODao pmoDao = new ETSPMODao();
					ETSPMOffice udPMOOffice =
						pmoDao.getPMOfficeObjectDetail(
							udDAO.getConnection(),
							udProject.getPmo_project_id(),
							udForm.getCc());
					udForm.setBreadCrumbs(udDAO.getPMOBreadCrumbTrail(udPMOOffice));
				}
				pdRequest.setAttribute(DocConstants.DOC_FORM, udForm);
			} else if (!StringUtil.isNullorEmpty(udForm.getPmoCat())) {
				ETSPMODao udPmoDAO = new ETSPMODao();
				ETSPMOffice udPMO =
					udPmoDAO.getPMOfficeProjectDetails(
						udDAO.getConnection(),
						udForm.getPmoCat());
				udForm.setBreadCrumbs(udDAO.getPMOBreadCrumbTrail(udPMO));
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
		} finally {
			cleanup(udDAO);
		}

		// Set the Super Admin and Executive Flags - These are used by the app.
		boolean bIsSuperAdmin = false;
		boolean bIsExecutive = false;
		if (strUserRole.equals(Defines.ETS_ADMIN)) {
		    bIsSuperAdmin = true;
		}

		if (strUserRole.equals(Defines.ETS_EXECUTIVE)) {
			bIsExecutive = true;
		}
	    pdSession.setAttribute(SESS_SUPERADMIN, Boolean.valueOf(bIsSuperAdmin));
	    pdSession.setAttribute(SESS_EXECUTIVE, Boolean.valueOf(bIsExecutive));

		ActionForward pdForward = null;
		try {
			pdForward = executeAction(pdMapping, pdForm, pdRequest, pdResponse);
		} catch (DocumentException e) {
			saveErrors(pdRequest, e.getErrors());
			pdForward = pdMapping.getInputForward();
		} catch (Exception e) {
			e.printStackTrace(System.out);
			m_pdLog.error(e);
			ActionErrors pdErrors = new ActionErrors();
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionError(ERR_APP_ERROR, e.getMessage()));
			saveErrors(pdRequest, pdErrors);
			pdForward = pdMapping.getInputForward();
		}
		return pdForward;
	}

	/**
	 * @return oem.edge.ets.fe.documents.DocumentDAO
	 */
	protected DocumentDAO getDAO() throws Exception {

		DocumentDAO udDocumentDAO = new DocumentDAO();
		udDocumentDAO.prepare();

		return udDocumentDAO;
	}

	/**
	 * @throws Exception
	 */
	protected void cleanup(DocumentDAO udDAO) throws Exception {
		if (udDAO != null) {
			udDAO.cleanup();
		}
		udDAO = null;
	}

	/**
	 * @return
	 */
	protected boolean isSuperAdmin(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute(SESS_SUPERADMIN)).booleanValue();
	}

	/**
	 * @return
	 */
	protected boolean isExecutive(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute(SESS_EXECUTIVE)).booleanValue();
	}

	/**
	 * @return
	 */
	public EdgeAccessCntrl getEdgeAccess(HttpServletRequest pdRequest) {
		return (EdgeAccessCntrl) pdRequest.getSession().getAttribute(SESS_EDGEACCESS);
	}

	/**
	 * @return
	 */
	public String getUserRole(HttpServletRequest pdRequest) {
		return (String) pdRequest.getSession().getAttribute(SESS_USERROLE);
	}

	/**
	 * @return
	 */
	public String getTransType(ActionMapping pdMapping) {
		return ((DocActionMapping) pdMapping).getAction();
	}

	/**
	 * This method is implemented by all classes extending 
	 * from BaseDocumentAction.
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

	/**
	 * @param udDAO
	 * @param pdMapping
	 * @param pdRequest
	 * @param pdResponse
	 * @return
	 * @throws Exception
	 */
	protected boolean processPath(
		DocumentDAO udDAO,
		ActionMapping pdMapping,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {
		return true;
	}

	/**
	 * This method will read RESOURCES and RESOURCE_ROLES TABLES and
	 * populate the two HashMaps
	 */
	private void load(DocumentDAO udDAO) {

		try {

			// Read the RESOURCE URL to ACTION Mapping
			m_mapResourceAction = udDAO.getResources();

			// Read the ACTION TO ROLE MAPPING
			m_mapActionRole = udDAO.getActionRoles();
		} catch (Exception e) {
			m_pdLog.error(e);
		}

	}

}
