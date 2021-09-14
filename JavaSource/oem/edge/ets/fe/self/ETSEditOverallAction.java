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
import oem.edge.ets.fe.ETSDBUtils;

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
public class ETSEditOverallAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";


	public ETSEditOverallAction() {
		super();
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();

		ActionForward forward = new ActionForward();

		try {

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return new ActionForward("/login.jsp");
			}

			ETSOverallAttributeForm key = (ETSOverallAttributeForm) form;


			String sSubSection = request.getParameter("subsection");
			
			String sOp = request.getParameter("etsop");
			if (sOp == null || sOp.trim().equalsIgnoreCase("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			
			if (sOp.trim().equalsIgnoreCase("") || sOp.trim().equalsIgnoreCase("edit")) {

				String sProjectId = request.getParameter("proj");
				String sSelfId = request.getParameter("self");
				
				ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
				
				ArrayList listexpect = self.getExpectations();
				
				for (int i = 0; i < listexpect.size(); i++) {
					
					ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexpect.get(i);
					
					if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == Integer.parseInt(sSubSection)) {
						key.setOverallRating(String.valueOf(exp.getRating()));
						break;
					}
					
				}

				forward = mapping.findForward("new");

			} else if (sOp.equalsIgnoreCase("save")) {

				forward = mapping.findForward("saved");
				StringBuffer sPath = new StringBuffer(forward.getPath());
				
				// set the overall value...
				key.setMemberId(es.gIR_USERN);
				key.setSectionId(String.valueOf(ETSSelfConstants.SECTION_OVERALL_RATING));
				key.setRating(key.getOverallRating());
				
				boolean created = ETSSelfDAO.updateSelfAssessmentExpectation(con,key,es.gIR_USERN);

				if (created) {
					sPath.append("?success=true&from=edit");
					forward = new ActionForward(sPath.toString());
				} else {
					sPath.append("?success=false&from=edit");
					forward = new ActionForward(sPath.toString());
				}
			}


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
