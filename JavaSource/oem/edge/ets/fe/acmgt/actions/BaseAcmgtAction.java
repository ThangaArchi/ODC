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

package oem.edge.ets.fe.acmgt.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author Suresh
 */
public abstract class BaseAcmgtAction extends Action {

	/** Stores the Logging object */
	private static final Log logger =
		EtsLogger.getLogger(BaseAcmgtAction.class);
	

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
		
		String m_strUserRole = null;
		String AIC_WORKFLOW_PROCESS = "Workflow" ;
		
		EdgeAccessCntrl es = null;

		
		final String REQ_ATTR_PRIMARYCONTACT =
			"document.primaryContact";
		
		es = new EdgeAccessCntrl();
		AddMembrToWrkSpcDAO addMDAO = new AddMembrToWrkSpcDAO();
		ActionMapping udMapping = (ActionMapping) pdMapping;
		ETSProj udProject = null;
		ActionForward pdForward = null;
		
		String strLinkId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
		String strProjectID = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
		String strTopCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
				
		 String strURL = "ETSProjectsServlet.wss?" 
		 	+ "proj="
	        + strProjectID
	        + "&tc="
	        + strTopCatId
	        + "&linkid="
	        + strLinkId;
						
		try {
				es.GetProfile(pdResponse,pdRequest);
				
							
				udProject =  ETSDatabaseManager.getProjectDetails(strProjectID);
				
				m_strUserRole = ETSUtils.checkUserRole(es,udProject.getProjectId());
				
				logger.error("USER ROLE for "+ es.gIR_USERN + "IS : "+ m_strUserRole);

				if (m_strUserRole.equals(Defines.INVALID_USER)) {
					UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(udProject.getProjectType());
					pdResponse.sendRedirect(unBrandedprop.getUnauthorizedURL());
					return pdMapping.findForward("signOn");
					
				}else if( ( !m_strUserRole.equals(Defines.WORKSPACE_OWNER) ) 
						&& ( !m_strUserRole.equals(Defines.WORKSPACE_MANAGER) ) 
						&& ( !m_strUserRole.equals(Defines.ETS_ADMIN) ) 
						&& ( (udProject.getProjectType().equals("AIC")) 
								&& (udProject.getProcess().equals(AIC_WORKFLOW_PROCESS)) 
								&& (!m_strUserRole.equals(Defines.WORKFLOW_ADMIN) ) ) ) {
					
						pdResponse.sendRedirect(strURL);						
				}
				
				pdRequest.setAttribute(REQ_ATTR_PRIMARYCONTACT,addMDAO.getProjContactInfo(strProjectID));
							
				pdForward = executeAction(udMapping, pdForm, pdRequest, pdResponse);
		
		} catch (Exception e) {
			e.printStackTrace(System.out);
			logger.error(e);
			pdForward = pdMapping.getInputForward();
		}
		
		
		return pdForward;
	}
	
	
	/**
	 * This method is implemented by all classes extending
	 * from BaseAcmgtAction.
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
