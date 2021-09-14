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
public class ETSAddKeyAttributeAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";


	public ETSAddKeyAttributeAction() {
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

			ETSAttributeForm key = (ETSAttributeForm) form;

			String sOp = request.getParameter("etsop");
			if (sOp == null || sOp.trim().equalsIgnoreCase("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			if (sOp.equalsIgnoreCase("edit")) {

				if (key.getOperation().trim().equalsIgnoreCase("save")) {

					forward = mapping.findForward("saved");
					StringBuffer sPath = new StringBuffer(forward.getPath());

					key.setMemberId(es.gIR_USERN);

					boolean created = ETSSelfDAO.updateSelfAssessmentExpectation(con,key,es.gIR_USERN);

					if (created) {
						sPath.append("?success=true");
						forward = new ActionForward(sPath.toString());
					} else {
						sPath.append("?success=false");
						forward = new ActionForward(sPath.toString());
					}

				} else {

					String sProjectId = request.getParameter("proj");
					String sSelfId = request.getParameter("self");
					String sSection = request.getParameter("section");
					String sSubSection = request.getParameter("subsection");
					String sSeqNo = request.getParameter("seqno");


					System.out.println("Op" + sOp + "//Proj:" + sProjectId + "//self:" + sSelfId + "//section" + sSection + "//subsection" + sSubSection + "//seq" + sSeqNo);

					ArrayList listexpect = ETSSelfDAO.getExpectations(con);
					request.setAttribute("expect",listexpect);

					// request.setAttribute("userid",es.gIR_USERN);

					ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);

					ArrayList listexp = self.getExpectations();

					for (int i = 0; i < listexp.size(); i++) {

						ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

						//if (exp.getSectionId() == Integer.parseInt(sSection) && exp.getSubSectionId() == Integer.parseInt(sSubSection) && exp.getSequenceNo() == Integer.parseInt(sSeqNo) && exp.getProjectId().equalsIgnoreCase(sProjectId) && exp.getSelfId().equalsIgnoreCase(sSelfId) && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
						if (exp.getSectionId() == Integer.parseInt(sSection) && exp.getSubSectionId() == Integer.parseInt(sSubSection) && exp.getSequenceNo() == Integer.parseInt(sSeqNo) && exp.getProjectId().equalsIgnoreCase(sProjectId) && exp.getSelfId().equalsIgnoreCase(sSelfId)) {

							key.setSelfId(sSelfId);
							key.setProjectId(sProjectId);
							key.setSectionId(sSection);
							key.setSubSectionId(sSubSection);
							key.setSequenceNo(sSeqNo);
							key.setMemberId(exp.getMemberId());
							key.setComments(exp.getComments());
							key.setRating(String.valueOf(exp.getRating()));
							key.setExpectId(String.valueOf(exp.getExpectId()));

						}
					}

					forward = mapping.findForward("new");

				}


			} else {

				if (key.getOperation() == null || key.getOperation().trim().equalsIgnoreCase("")) {

					String sProjectId = request.getParameter("proj");
					String sSelfId = request.getParameter("self");

					ArrayList listexpect = ETSSelfDAO.getExpectations(con);
					request.setAttribute("expect",listexpect);

					//request.setAttribute("userid",es.gIR_USERN);

					forward = mapping.findForward("new");

				} else if (key.getOperation().trim().equalsIgnoreCase("save")) {

					forward = mapping.findForward("saved");
					StringBuffer sPath = new StringBuffer(forward.getPath());

					if (sOp.trim().equalsIgnoreCase("edit")) {

						boolean created = ETSSelfDAO.updateSelfAssessmentExpectation(con,key,es.gIR_USERN);

						if (created) {
							sPath.append("?success=true");
							forward = new ActionForward(sPath.toString());
						} else {
							sPath.append("?success=false");
							forward = new ActionForward(sPath.toString());
						}

					} else {

						key.setMemberId(es.gIR_USERN);
						key.setSequenceNo(String.valueOf(ETSSelfDAO.getNextSeqNo(con,key.getSelfId(),key.getProjectId(),Integer.parseInt(key.getSectionId()),Integer.parseInt(key.getSubSectionId()))));

						boolean created = ETSSelfDAO.createSelfAssessmentExpectation(con,key,es.gIR_USERN);

						if (created) {
							sPath.append("?success=true");
							forward = new ActionForward(sPath.toString());
						} else {
							sPath.append("?success=false");
							forward = new ActionForward(sPath.toString());
						}
					}
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
