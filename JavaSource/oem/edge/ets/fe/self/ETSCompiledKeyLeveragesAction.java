/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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


/*
 * Created on Jan 22, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSCompiledKeyLeveragesAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	public ETSCompiledKeyLeveragesAction() {
		super();
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();

		ActionForward forward = new ActionForward();

		boolean isAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;

		try {

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return new ActionForward("/login.jsp");
			}

			String sOp = request.getParameter("etsop");

			if (sOp == null || sOp.trim().equalsIgnoreCase("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			String sProjectId = request.getParameter("proj");
			String sSelfId = request.getParameter("self");

			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);

			if (self.getAssessmentOwner().equalsIgnoreCase(es.gIR_USERN)) {
				isAssessmentOwner = true;
			}

			String sRole = ETSUtils.checkUserRole(es,sProjectId);

			if (sRole.equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
				bWorkspaceOwner = true;
			}

			if (sRole.equalsIgnoreCase(Defines.WORKSPACE_MANAGER)) {
				bWorkspaceManager = true;
			}

			if (sRole.equalsIgnoreCase(Defines.ETS_ADMIN)) {
				bAdmin = true;
			}

			String sCurrentStep = "";

			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			if (isAssessmentOwner || bWorkspaceOwner || bWorkspaceManager || bAdmin) {
				request.setAttribute("owner","true");
			} else {
				request.setAttribute("owner","false");
			}
			
			ETSProj proj = ETSUtils.getProjectDetails(con,sProjectId);
					
			if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT) && !proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
				request.setAttribute("editable","true");
			} else {
				request.setAttribute("editable","false");
			}
			

			request.setAttribute("self",self);

			request.setAttribute("userid",es.gIR_USERN);

			forward = mapping.findForward("show");

			return forward;

		} catch (SQLException e) {
			e.printStackTrace();
			return mapping.findForward("error");
		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("error");
		} finally {
			ETSDBUtils.close(con);
		}

	}
}
