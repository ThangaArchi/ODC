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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
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
public class ETSCompiledPrinterFriendlyAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	public ETSCompiledPrinterFriendlyAction() {
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

			ArrayList listexp = self.getExpectations();

			// get the overall value..
			float overallvalue = 0;
			int iCount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING) {
					overallvalue = overallvalue + exp.getRating();
					iCount = iCount + 1;
				}
			}

			if (iCount > 0) {
				overallvalue = overallvalue / iCount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("overallvalue",format.format(overallvalue));
			} else {
				request.setAttribute("overallvalue","0");
			}

			// quality value
			float qualityvalue = 0;
			int qualitycount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_QUALITY) {
					qualityvalue = qualityvalue + exp.getRating();
					qualitycount = qualitycount + 1;
				}
			}

			if (qualitycount > 0) {
				qualityvalue = qualityvalue / qualitycount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("qualityvalue",format.format(qualityvalue));
			} else {
				request.setAttribute("qualityvalue","0");
			}

			// delivery value
			float deliveryvalue = 0;
			int deliverycount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_DELIVERY) {
					deliveryvalue = deliveryvalue + exp.getRating();
					deliverycount = deliverycount + 1;
				}
			}

			if (deliverycount > 0) {
				deliveryvalue = deliveryvalue / deliverycount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("deliveryvalue",format.format(deliveryvalue));
			} else {
				request.setAttribute("deliveryvalue","0");
			}

			// cost value
			float costvalue = 0;
			int costcount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_COST_PRICE) {
					costvalue = costvalue + exp.getRating();
					costcount = costcount + 1;
				}
			}

			if (costcount > 0) {
				costvalue = costvalue / costcount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("costvalue",format.format(costvalue));
			} else {
				request.setAttribute("costvalue","0");
			}


			// tech value
			float techvalue = 0;
			int techcount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_TECHNOLOGY) {
					techvalue = techvalue + exp.getRating();
					techcount = techcount + 1;
				}
			}

			if (techcount > 0) {
				techvalue = techvalue / techcount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("techvalue",format.format(techvalue));
			} else {
				request.setAttribute("techvalue","0");
			}

			// responsive value
			float respvalue = 0;
			int respcount = 0;

			for (int i = 0; i < listexp.size(); i++) {

				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_RESPONSIVENESS) {
					respvalue = respvalue + exp.getRating();
					respcount = respcount + 1;
				}
			}

			if (respcount > 0) {
				respvalue = respvalue / respcount;
				NumberFormat format = new DecimalFormat("0.0");
				request.setAttribute("respvalue",format.format(respvalue));
			} else {
				request.setAttribute("respvalue","0");
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
