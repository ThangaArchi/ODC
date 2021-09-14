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
public class ETSSelfKeyAttributesAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.5";

	public ETSSelfKeyAttributesAction() {
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

			String sOp = request.getParameter("etsop");

			if (sOp == null || sOp.trim().equalsIgnoreCase("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			if (sOp.equals("")) {
				String sProjectId = request.getParameter("proj");
				String sSelfId = request.getParameter("self");

				ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
				request.setAttribute("self",self);

				request.setAttribute("userid",es.gIR_USERN);

				String sCurrentStep = "";
				ArrayList steps = self.getStep();

				if (steps != null) {
					ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
					sCurrentStep = step.getStep();
				}

				if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					
					ETSProj proj = ETSUtils.getProjectDetails(con,sProjectId);
					
					if (proj.getProject_status().equalsIgnoreCase("A") || proj.getProject_status().equalsIgnoreCase("D")) {
						request.setAttribute("editable","false");
					} else {
						ArrayList liststatus = ETSSelfDAO.getMemberSectionStatus(con,sSelfId,sProjectId,es.gIR_USERN);
	
						boolean bCompleted = false;
	
						for (int i = 0; i < liststatus.size(); i++) {
							ETSSelfMemberSectionStatus status = (ETSSelfMemberSectionStatus) liststatus.get(i);
	
							if (status.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES) {
								bCompleted = true;
							}
						}
	
						if (bCompleted) {
							request.setAttribute("editable","false");
						} else {
							request.setAttribute("editable","true");
						}
					}
				} else {
					request.setAttribute("editable","false");
				}

				forward = mapping.findForward("show");

			} else if (sOp.equals("draft")) {

				forward = mapping.findForward("draft");
				StringBuffer sPath = new StringBuffer(forward.getPath());

				sPath.append("?success=true");

				forward = new ActionForward(sPath.toString());

			} else if (sOp.equals("finish")) {

				String sProjectId = request.getParameter("proj");
				String sSelfId = request.getParameter("self");

				ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
				
				if (validateAtttributesComplete(self,es)) {
					
					forward = mapping.findForward("finish");
					StringBuffer sPath = new StringBuffer(forward.getPath());


					boolean created = ETSSelfDAO.completeMemberSection(con,sSelfId,sProjectId,ETSSelfConstants.SECTION_KEY_ATTRIBUTES,es.gIR_USERN);

					// update member status
					ETSSelfAssessmentStatus.completeMemberStatus(con,self,es.gIR_USERN);

					self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);

					// check if self assessment can move to next step
					boolean completed = ETSSelfAssessmentStatus.checkIfTeamAssessmentStepComplete(con,self);

					if (completed) {
						// move self assessment to next step...
						boolean bcreated = ETSSelfDAO.createSelfAssessmentStep(con,sSelfId,sProjectId,ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT,es.gIR_USERN);
					
						ETSSelfMail mail = ETSSelfMailFunctions.createTeamAssessmentStepCompleteMail(con,sSelfId,sProjectId,"");
					
						boolean bsent = ETSSelfMailFunctions.sendEmail(mail);
					}

					if (created) {
						sPath.append("?success=true");
					} else {
						sPath.append("?success=false");
					}


					forward = new ActionForward(sPath.toString());
					
				} else {
					
					forward = mapping.findForward("incomplete");
					
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
	
	
	private boolean validateAtttributesComplete(ETSSelfAssessment self, EdgeAccessCntrl es) throws Exception {
		
		boolean complete = false;
		
		try {
			
			int qualityvalue = 0;
			int qualitycount = 0;
			int deliveryvalue = 0;
			int deliverycount = 0;
			int costvalue = 0;
			int costcount = 0;
			int techvalue = 0;
			int techcount = 0;
			int respvalue = 0;
			int respcount = 0;
			
			ArrayList listexp = self.getExpectations();
			
			for (int i = 0; i < listexp.size(); i++) {
				
				ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) listexp.get(i);

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_QUALITY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					qualityvalue = qualityvalue + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_QUALITY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					qualitycount = qualitycount + 1;
				}


				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_DELIVERY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					deliveryvalue = deliveryvalue + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_DELIVERY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					deliverycount = deliverycount + 1;
				}


				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_COST_PRICE && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					costvalue = costvalue + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_COST_PRICE && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					costcount = costcount + 1;
				}


				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_TECHNOLOGY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					techvalue = techvalue + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_TECHNOLOGY && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					techcount = techcount + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_RESPONSIVENESS && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					respvalue = respvalue + 1;
				}

				if (exp.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES && exp.getSubSectionId() == ETSSelfConstants.SUB_SECTION_RESPONSIVENESS && exp.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					respcount = respcount + 1;
				}
				
			}
			
			if (qualityvalue > 0 && qualitycount > 0 && deliveryvalue > 0 && deliverycount > 0 && costvalue > 0 && costcount > 0 && techvalue > 0 && techcount > 0 && respvalue > 0 && respcount > 0) {
				complete = true;
			} else {
				complete = false;
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		return complete;
		
		
	}
}
