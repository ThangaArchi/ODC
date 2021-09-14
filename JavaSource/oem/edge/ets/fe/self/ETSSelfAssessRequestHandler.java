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


package oem.edge.ets.fe.self;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.dealtracker.ETSSelfDealTracker;
import oem.edge.ets.fe.setmet.ETSClientCareContact;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSSelfAssessRequestHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.20";

	private ETSParams params;
	private String header = "";
	private EdgeAccessCntrl es = null;


	public ETSSelfAssessRequestHandler(ETSParams parameters, StringBuffer sHeader, EdgeAccessCntrl es1) {

		this.params = parameters;
		this.header = sHeader.toString();
		this.es = es1;

	}

	public void handleRequest() throws SQLException, Exception {

		try {

			Connection con = this.params.getConnection();
			PrintWriter out = this.params.getWriter();
			ETSProj proj = this.params.getETSProj();
			EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
			HttpServletRequest request = this.params.getRequest();

			String sETSOp = request.getParameter("etsop");

			if (sETSOp == null || sETSOp.trim().equals("")) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}



			if (sETSOp.trim().equals("")){
				displaySelfAssessmentLandingPage();
			} else if (sETSOp.trim().equals("assess")){
				displaySelfAssessment();
			} else if (sETSOp.trim().equals("compile")){
				
				boolean projArchieved = false;
				boolean projDeleted = false;

				if (proj.getProject_status().equalsIgnoreCase("A")) {
					projArchieved = true;
				}

				if (proj.getProject_status().equalsIgnoreCase("D")) {
					projDeleted = true;
				}
				
				String sSelfID = ETSUtils.checkNull(request.getParameter("self"));
				ETSSelfAssessment self = new ETSSelfAssessment();
				self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

				String sCurrentStep = "";

				ArrayList steps = self.getStep();
				if (steps != null) {
					ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
					sCurrentStep = step.getStep();
				}

				if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					if (projArchieved || projDeleted) {
						displayArchievedMessage();
					} else {
						displayCreateCompiledSelfAssessment();
					}
					
				} else {
					displayCompiledSelfAssessment();
				}

			} else if (sETSOp.trim().equals("compileconfirm")){
				String sSelfID = ETSUtils.checkNull(request.getParameter("self"));
				ETSSelfAssessment self = new ETSSelfAssessment();
				self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

				boolean success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT,es.gIR_USERN);
				ETSSelfDAO.updateAllMemberAsCompleted(con,self.getSelfId(),self.getProjectId());
				
				ETSSelfMail mail = ETSSelfMailFunctions.createTeamAssessmentStepCompleteMail(con,sSelfID,self.getProjectId(),es.gIR_USERN);
					
				boolean bsent = ETSSelfMailFunctions.sendEmail(mail);

				displayCompiledSelfAssessment();

			} else if (sETSOp.trim().equals("action")){
				displayActionPlan();
			} else if (sETSOp.trim().equals("approve")){
				approveCompiledAssessment();
			} else if (sETSOp.trim().equals("close")){
				closeSelfAssessmentConfirm();
			} else if (sETSOp.trim().equals("closeconfirm")){
				closeSelfAssessment();
			} else if (sETSOp.trim().equals("closeap")){
				closeActionPlan();
			}

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	/**
	 *
	 */
	private void displaySelfAssessmentLandingPage() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		boolean bPrintedStars = false;
		boolean displayLegend = false;

		boolean	bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;
		boolean bPrimary = false;
		
		boolean projArchieved = false;
		boolean projDeleted = false;
		
		String sCurrentStep = "";

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));

		ETSSelfAssessment self = new ETSSelfAssessment();

		if (sSelfID.trim().equals("")) {
			// get the most current one by meeting date
			self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),"");
		} else {
			self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);
		}

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");
		
		
		//proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")
		
		if (proj.getProject_status().equalsIgnoreCase("A")) {
			projArchieved = true;
		}

		if (proj.getProject_status().equalsIgnoreCase("D")) {
			projDeleted = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
			bWorkspaceOwner = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER)) {
			bWorkspaceManager = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_ADMIN)) {
			bAdmin = true;
		}

		String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,proj.getProjectId());
		if (sPrimaryContactID.equalsIgnoreCase(es.gIR_USERN)) {
			bPrimary = true;
		}
		

		if (self != null && !self.getSelfId().trim().equals("")) {

			displayLegend = true;

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			if (self.getState().equalsIgnoreCase(ETSSelfConstants.SELF_OPEN) && !projArchieved && !projDeleted && (bWorkspaceOwner || bWorkspaceManager || bAdmin || bPrimary)) {
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"400\" align=\"left\">&nbsp;" + self.getTitle() + "</td><td headers=\"\" align=\"right\" ><table summary=\"\"><tr><td headers=\"\" width=\"20\" align=\"right\"><a href=\"" + Defines.SERVLET_PATH + "EditSelfAssessment.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=editself&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Edit\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a style=\"color: #ffffff; font-weight: normal\" href=\"" + Defines.SERVLET_PATH + "EditSelfAssessment.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=editself&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\"><b>Edit</b></a></td></tr></table></td></tr></table></td>");
			} else {
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			}

			out.println("</tr>");
			out.println("<tr><td headers=\"\"  width=\"443\">");

			// table 3
			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			out.println("<tr valign=\"middle\">");
			out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");

			// table 4
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			if (self.getState().trim().equalsIgnoreCase(ETSSelfConstants.SELF_OPEN)) {
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">This self assessment has been open for <b>" + ETSUtils.dateDiff(self.getStartDate(),new Timestamp(System.currentTimeMillis())) + "</b> days.");
				out.println("</td></tr>");
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\">");
				printGreyDottedLine(out);
				out.println("</td></tr>");
			}

			// end of table 4
			out.println("</table>");

			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"1\" border=\"0\" width=\"100%\">");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><b>Status</b></td>");
			out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\"><b>Step</b></td>");
			out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\"><b>Owner(s)</b></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><b>Due date</b></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");


			/*
			 * Check if the logged in pserson is a member of the selected self assessment
			 *
			 */

			String sAssessmentOwner = self.getAssessmentOwner();
			String sPlanOwner = self.getPlanOwner();

			boolean isMemberOfSelfAssessment = false;
			boolean isMemberCompleted = false;
			boolean isAssessmentOwner = false;
			boolean isActionPlanOwner = false;

			if (self.getAssessmentOwner().equalsIgnoreCase(es.gIR_USERN)) {
				isAssessmentOwner = true;
			}

			if (self.getPlanOwner().equalsIgnoreCase(es.gIR_USERN)) {
				isActionPlanOwner = true;
			}



			ArrayList members = self.getMembers();

			for (int i = 0; i < members.size(); i++) {

				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);

				if (member.getMemberId().equalsIgnoreCase(es.gIR_USERN)) {
					isMemberOfSelfAssessment = true;

					boolean bKeyAttributesCompleted = false;
					boolean bKeyLeveragesCompleted = false;
					boolean bPositiveEffortsCompleted = false;

					ArrayList memberstatus = ETSSelfDAO.getMemberSectionStatus(con,self.getSelfId(),self.getProjectId(),es.gIR_USERN);

					for (int j = 0; j < memberstatus.size(); j++) {

						ETSSelfMemberSectionStatus status = (ETSSelfMemberSectionStatus) memberstatus.get(j);

						if (status.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES) {
							bKeyAttributesCompleted = true;
						}
						if (status.getSectionId() == ETSSelfConstants.SECTION_KEY_LEVERAGES) {
							bKeyLeveragesCompleted = true;
						}
						if (status.getSectionId() == ETSSelfConstants.SECTION_POSITIVE_EFFORTS) {
							bPositiveEffortsCompleted = true;
						}

					}

					if (bKeyAttributesCompleted && bKeyLeveragesCompleted && bPositiveEffortsCompleted) {
						isMemberCompleted = true;
					}
					break;
				}
			}


			/*
			 * Get Due Dates
			 *
			 */

			Timestamp MemberDueDate = null;
			Timestamp CompiledDueDate = null;
			Timestamp ActionPlanDueDate = null;

			ArrayList duedates = self.getDueDates();

			for (int i = 0; i < duedates.size(); i++) {

				ETSSelfAssessmentDueDate due = (ETSSelfAssessmentDueDate) duedates.get(i);

				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					 MemberDueDate = due.getDueDate();
				}
				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT)) {
					 CompiledDueDate = due.getDueDate();
				}
				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN)) {
					 ActionPlanDueDate = due.getDueDate();
				}
			}

			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			boolean printedStep = false;

			// printing the first step: team member assessment...

			for (int i = 0; i < steps.size(); i++) {

				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(i);

				if (step.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT)) {
					printedStep = true;
					// member assessment has been completed.
					out.println("<tr style=\"font-weight: normal;\">");
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
					if (isMemberOfSelfAssessment) {
						out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=assess&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Team member assessment</a></td>");
					} else {
						out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\">Team member assessment</td>");
					}

					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");

					for (int j = 0; j < members.size(); j++) {
						ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(j);
						out.println(member.getMemberName() + "<br />");
					}

					out.println("</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(MemberDueDate) + "</td>");
					out.println("</tr>");
					break;
				}
			}

			if (!printedStep) {
				// member assessment has not been completed.
				out.println("<tr style=\"font-weight: normal;\">");
				if (isMemberOfSelfAssessment && !isMemberCompleted && sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
				}

				if (isMemberOfSelfAssessment) {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=assess&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Team member assessment</a></td>");
				} else {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">Team member assessment</td>");
				}
				out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");

				for (int j = 0; j < members.size(); j++) {

					ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(j);

					ArrayList memberstatus = ETSSelfDAO.getMemberSectionStatus(con,self.getSelfId(),self.getProjectId(),member.getMemberId());

					boolean bKeyAttributesCompleted = false;
					boolean bKeyLeveragesCompleted = false;
					boolean bPositiveEffortsCompleted = false;

					for (int i = 0; i < memberstatus.size(); i++) {

						ETSSelfMemberSectionStatus status = (ETSSelfMemberSectionStatus) memberstatus.get(i);

						if (status.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES) {
							bKeyAttributesCompleted = true;
						}
						if (status.getSectionId() == ETSSelfConstants.SECTION_KEY_LEVERAGES) {
							bKeyLeveragesCompleted = true;
						}
						if (status.getSectionId() == ETSSelfConstants.SECTION_POSITIVE_EFFORTS) {
							bPositiveEffortsCompleted = true;
						}

					}
					if (bKeyAttributesCompleted && bKeyLeveragesCompleted && bPositiveEffortsCompleted) {
						out.println(member.getMemberName() + "<br />");
					} else {
						out.println(member.getMemberName() + "<span style=\"color:#ff3333\">**</span><br />");
						bPrintedStars = true;
					}

				}

				out.println("</td>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(MemberDueDate) + "</td>");
				out.println("</tr>");
			}

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// printing second step.. compiled assessment
			printedStep = false;

			for (int i = 0; i < steps.size(); i++) {

				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(i);

				if (step.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN)) {
					printedStep = true;
					// compiled assessment has been completed.
					out.println("<tr style=\"font-weight: normal;\">");
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=compile&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Compiled assessment</a></td>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");
					out.println(ETSUtils.getUsersName(con,sAssessmentOwner));
					out.println("</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(CompiledDueDate) + "</td>");
					out.println("</tr>");
					break;
				}
			}

			if (!printedStep) {
				// compiled assessment has not been completed.
				out.println("<tr style=\"font-weight: normal;\">");
				if (isAssessmentOwner && sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT)) {
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
				}

				if (isAssessmentOwner || bWorkspaceOwner || bWorkspaceManager || bAdmin || sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT) || sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN) || sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_CLOSED)) {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=compile&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Compiled assessment</a></td>");
				} else {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">Compiled assessment</td>");
				}

				out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");
				out.println(ETSUtils.getUsersName(con,sAssessmentOwner));
				out.println("</td>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(CompiledDueDate) + "</td>");
				out.println("</tr>");
			}


			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// printing final step.. action plan
			printedStep = false;

			for (int i = 0; i < steps.size(); i++) {

				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(i);

				if (step.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_CLOSED)) {
					printedStep = true;
					// action plan has been completed.
					out.println("<tr style=\"font-weight: normal;\">");
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></td>");
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");
					out.println(ETSUtils.getUsersName(con,sPlanOwner));
					out.println("</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(ActionPlanDueDate) + "</td>");
					out.println("</tr>");
					break;
				}
			}

			if (!printedStep) {
				// action plan has not been completed.
				out.println("<tr style=\"font-weight: normal;\">");
				if (isActionPlanOwner && sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN)) {
					out.println("<td headers=\"\" width=\"50\" align=\"center\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
				}

				if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN) || sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_CLOSED)) {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></td>");
				} else {
					out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">Action plan</td>");
				}

				out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\">");
				out.println(ETSUtils.getUsersName(con,sPlanOwner));
				out.println("</td>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(ActionPlanDueDate) + "</td>");
				out.println("</tr>");
			}


			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// end of table XXX
			out.println("</table>");

			// end of table 3
			out.println("</td></tr></table>");

			// end of table 2
			out.println("</td></tr></table>");

			// end of table 1
			out.println("</td></tr></table>");

		}



		if (displayLegend) {
			//printing the legend...
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
			out.println("</tr>");

			if (bPrintedStars) {
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" class=\"small\" style=\"color:#666666\">(Completed)</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				out.println("<td headers=\"\" width=\"100\" class=\"small\" style=\"color:#666666\">(Action required)</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><span style=\"color:#ff3333\">**</span></td>");
				out.println("<td headers=\"\" align=\"left\" class=\"small\" style=\"color:#666666\">Has not completed step</td>");
				out.println("</tr>");
			} else {
				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" class=\"small\" style=\"color:#666666\">(Completed)</td>");
				out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				out.println("<td headers=\"\" align=\"left\" colspan=\"3\" class=\"small\" style=\"color:#666666\">(Action required)</td>");
				out.println("</tr>");
			}
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		out.println("<br />");

		if ((bWorkspaceOwner || bWorkspaceManager || bAdmin || bPrimary) && !projArchieved && !projDeleted) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			if (self.getState().equalsIgnoreCase(ETSSelfConstants.SELF_OPEN)) {
				out.println("<td headers=\"\"  width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  align=\"left\" width=\"140\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=close&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Close self assessment</a></td>");
			}
			out.println("<td headers=\"\"  width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "NewSelfAssessment.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=newsetmet&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Add a new self assessment</a></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
		}

		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

		// print the open set mets
		out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr><td headers=\"\" width=\"443\" valign=\"top\">");

		boolean bOpen = displayOpenSelfAssessments("");

		boolean bClosed = displayClosedSelfAssessments("",bOpen);

		out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\" valign=\"top\">");

		if (bOpen || bClosed) {
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" class=\"tblue\" height=\"18\">&nbsp;Self Assessment key</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\" class=\"tgreen\">");
			out.println("<tr align=\"center\" valign=\"middle\">");
			out.println("<td headers=\"\" width=\"150\">");
			out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\">");
			out.println("<tr style=\"background-color: #ffffff\"><td headers=\"\"  height=\"18\" width=\"40\" align=\"left\"><span class=\"small\">&nbsp;9-10</span></td><td headers=\"\"  align=\"left\" ><span class=\"small\">Exceeded</span></td></tr>");
			out.println("<tr style=\"background-color: #ffffff\"><td headers=\"\"  height=\"18\" width=\"40\" align=\"left\"><span class=\"small\">&nbsp;6-8</span></td><td headers=\"\"  align=\"left\"><span class=\"small\">Met</span></td></tr>");
			out.println("<tr style=\"background-color: #ffffff\"><td headers=\"\"  height=\"18\" width=\"40\" align=\"left\"><span class=\"small\">&nbsp;4-5</span></td><td headers=\"\"  align=\"left\"><span class=\"small\">Met some</span></td></tr>");
			out.println("<tr style=\"background-color: #ffffff\"><td headers=\"\"  height=\"18\" width=\"40\" align=\"left\"><span class=\"small\">&nbsp;0-3</span></td><td headers=\"\"  align=\"left\"><span class=\"small\">Fallen short</span></td></tr>");

			out.println("</table>");
			out.println("</td></tr></table>");

		}

		out.println("</td></tr>");
		out.println("</table>");


	}

	/**
	 *
	 */
	private void displaySelfAssessment() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		boolean bPrintedStars = false;
		boolean displayLegend = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));


		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");

		if (self != null && !self.getSelfId().trim().equals("")) {

			displayLegend = true;

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			if (self.getState().trim().equalsIgnoreCase(ETSSelfConstants.SELF_OPEN)) {
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">This self assessment has been open for <b>" + ETSUtils.dateDiff(self.getStartDate(),new Timestamp(System.currentTimeMillis())) + "</b> days.");
				out.println("</td></tr>");
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\">");
				printGreyDottedLine(out);
				out.println("</td></tr>");
			}

			// end of table 3
			out.println("</table>");

			out.println("<br />");


			// table 4
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\"><b>Step 1. Team member assessment</b>");
			out.println("</td></tr>");

			// end of table 4
			out.println("</table>");
			out.println("<br />");

			boolean showIcons = true;
			String sCurrentStep = "";
			
			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}
			
			if (sCurrentStep.trim().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
				showIcons = true;
			} else {
				showIcons = false;
			}
			
			
			boolean KeyAttributesCompleted = false;
			boolean KeyLeveragesCompleted = false;
			boolean PositiveEffortsCompleted = false;

			ArrayList memberstatus = ETSSelfDAO.getMemberSectionStatus(con,sSelfID,proj.getProjectId(),es.gIR_USERN);

			for (int i = 0; i < memberstatus.size(); i++) {

				ETSSelfMemberSectionStatus status = (ETSSelfMemberSectionStatus) memberstatus.get(i);

				if ((status.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES) && status.getStatus().equalsIgnoreCase(ETSSelfConstants.MEMBER_COMPLETED)) {
					KeyAttributesCompleted = true;
				}

				if ((status.getSectionId() == ETSSelfConstants.SECTION_KEY_LEVERAGES) && status.getStatus().equalsIgnoreCase(ETSSelfConstants.MEMBER_COMPLETED)) {
					KeyLeveragesCompleted = true;
				}

				if ((status.getSectionId() == ETSSelfConstants.SECTION_POSITIVE_EFFORTS) && status.getStatus().equalsIgnoreCase(ETSSelfConstants.MEMBER_COMPLETED)) {
					PositiveEffortsCompleted = true;
				}
			}


			/*
			 * Get Due Dates
			 *
			 */

			Timestamp MemberDueDate = null;

			ArrayList duedates = self.getDueDates();

			for (int i = 0; i < duedates.size(); i++) {
				ETSSelfAssessmentDueDate due = (ETSSelfAssessmentDueDate) duedates.get(i);
				if (due.getStep().equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
					 MemberDueDate = due.getDueDate();
				}
			}

			// table XXX
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><b>Status</b></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" valign=\"top\"><b>Step</b></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><b>Due date</b></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// key attributes
			out.println("<tr style=\"font-weight: normal;\">");
			if (showIcons) {
				if (KeyAttributesCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				}
			} else {
//				if (KeyAttributesCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
//				} else {
//					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
//				}
			}
			out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "KeyAttributes.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Key attributes</a></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(MemberDueDate) + "</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// key leverages
			out.println("<tr style=\"font-weight: normal;\">");
			if (showIcons) {
				if (KeyLeveragesCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				}
			} else {
//				if (KeyLeveragesCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
//				} else {
//					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
//				}
			}
			out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "KeyLeverages.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Key leverages</a></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(MemberDueDate) + "</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// positive efforts
			out.println("<tr style=\"font-weight: normal;\">");
			if (showIcons) {
				if (PositiveEffortsCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
				} else {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
				}
			} else {
//				if (PositiveEffortsCompleted) {
					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
//				} else {
//					out.println("<td headers=\"\" width=\"50\" align=\"left\" valign=\"top\">&nbsp;</td>");
//				}
			}
			out.println("<td headers=\"\" width=\"140\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "PositiveEfforts.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Positive efforts</a></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(MemberDueDate) + "</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");

			// end of table XXX
			out.println("</table>");

		}

		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=2\" width=\"12\" height=\"10\" alt=\"Completed\" border=\"0\" /></td>");
		out.println("<td headers=\"\" width=\"100\" align=\"left\" class=\"small\" style=\"color:#666666\">(Completed)</td>");
		out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SELF&mod=1\" width=\"12\" height=\"10\" alt=\"action required\" border=\"0\" /></td>");
		out.println("<td headers=\"\" align=\"left\" colspan=\"3\" class=\"small\" style=\"color:#666666\">(Action required)</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"6\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");


		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" >Back to self assessment reviews</a></td>");
		out.println("</tr>");
		out.println("</table>");


		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	/**
	 *
	 */
	private void displayActionPlan() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bPlanOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;		
		boolean bPlanEditable = false;
		boolean bShowCloseLink = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));


		String sPrinterFriendly = ETSUtils.checkNull(request.getParameter("skip"));
		if (sPrinterFriendly == null || sPrinterFriendly.trim().equalsIgnoreCase("")) {
			sPrinterFriendly = "N";
		} else {
			sPrinterFriendly = sPrinterFriendly.trim();
		}	

		String sCurrentStep = "";


		boolean projArchieved = false;
		boolean projDeleted = false;

		if (proj.getProject_status().equalsIgnoreCase("A")) {
			projArchieved = true;
		}

		if (proj.getProject_status().equalsIgnoreCase("D")) {
			projDeleted = true;
		}


		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);
		ArrayList steps = self.getStep();

		if (steps != null) {
			ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
			sCurrentStep = step.getStep();
		}

		if (self.getPlanOwner().equalsIgnoreCase(es.gIR_USERN)) {
			bPlanOwner = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
			bWorkspaceOwner = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER)) {
			bWorkspaceManager = true;
		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_ADMIN)) {
			bAdmin = true;
		}

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId(),sPrinterFriendly);

		out.println(header[0]);

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" >");
		out.println("<span class=\"subtitle\">Action Plan<span>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");

		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

		if (!projArchieved && !projDeleted && sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN) && self.getState().equalsIgnoreCase(ETSSelfConstants.SELF_OPEN)) {
			bPlanEditable = true;
		}		
		
		if (!projArchieved && !projDeleted && sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN) && (bAdmin || bWorkspaceManager || bWorkspaceOwner || bPlanOwner)) {
			bShowCloseLink = true;
		}		
		
		ETSSelfDealTracker tracker = new ETSSelfDealTracker(params, self.getSelfId(), self.getTitle(), bPlanEditable, bShowCloseLink);
         
		tracker.SelfTrackerHandler();


		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");

		if (!sPrinterFriendly.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" >Back to self assessment reviews</a></td>");
			out.println("</tr>");
			out.println("</table>");
		}


	}

	/**
	 *
	 */
	private void displayCompiledSelfAssessment() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		String sCurrentStep = "";

		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;
		
		boolean projArchieved = false;
		boolean projDeleted = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));


		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");

		if (proj.getProject_status().equalsIgnoreCase("A")) {
			projArchieved = true;
		}

		if (proj.getProject_status().equalsIgnoreCase("D")) {
			projDeleted = true;
		}

		if (self != null && !self.getSelfId().trim().equals("")) {

			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			if (self.getAssessmentOwner().equalsIgnoreCase(es.gIR_USERN)) {
				bAssessmentOwner = true;
			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
				bWorkspaceOwner = true;
			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER)) {
				bWorkspaceManager = true;
			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_ADMIN)) {
				bAdmin = true;
			}

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">Please review the compiled data for this Self-Assessment by clicking on each item below. You will be able to view all data.");
			out.println("</td></tr>");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\"><br />");
			printGreyDottedLine(out);
			out.println("</td></tr>");

			// end of table 3
			out.println("</table>");

			out.println("<br />");


			// table 4
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\"><b>Step 2: Compiled assessment review</b>");
			out.println("</td></tr>");

			// end of table 4
			out.println("</table>");
			out.println("<br />");

			// table 5
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\"><b>Attributes to review</b>");
			out.println("</td></tr>");

			// end of table 5
			out.println("</table>");
			out.println("<br />");


			// table XXX
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");


			// key attributes
			out.println("<tr style=\"font-weight: normal;\">");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "CompiledKeyAttributes.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Key attributes</a></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");

			// key leverages
			out.println("<tr style=\"font-weight: normal;\">");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "CompiledKeyLeverages.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Key leverages</a></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");

			// positive efforts
			out.println("<tr style=\"font-weight: normal;\">");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\" style=\"font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "CompiledPositiveEfforts.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + self.getSelfId() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Positive efforts</a></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");

			// end of table XXX
			out.println("</table>");

		}

		out.println("<br />");

		if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT) && !projArchieved && !projDeleted && (bAdmin || bWorkspaceManager || bWorkspaceOwner || bAssessmentOwner)) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"25\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "&etsop=approve\" ><img src=\"" + Defines.BUTTON_ROOT  + "arrow_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"Approve\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "&etsop=approve\" >Approve</a></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\" width=\"200\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" >Back to self assessment reviews</a></td>");
		out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "\"  target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\"><img src=\"" + Defines.ICON_ROOT + "printer.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Printer friendly\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "\"  target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "CompiledPrinterFriendly.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\">Printer friendly version</a></td>");
		out.println("</tr>");
		out.println("</table>");


		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	/**
	 *
	 */
	private void displayCreateCompiledSelfAssessment() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));

		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");

		if (self != null && !self.getSelfId().trim().equals("")) {

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");


			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
			out.println("<b><span style=\"color:#ff3333\">There are team members who have not completed the team assessment step. Some parts of the self assessment might be incomplete.</span><br /><br />If you want to complete the team assessment step and start with compiled assessment, please click on \"Continue\".</b>");
			out.println("</td></tr>");
			out.println("</table>");
			// end of table 3

			out.println("<br />");
		}


		printGreyDottedLine(out);
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"75\" align=\"left\"><table summary=\"\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "&etsop=compileconfirm\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td></tr></table></td>");
		out.println("<td headers=\"\"  align=\"left\"><table summary=\"\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"Cancel\" /></a></td><td headers=\"\" width=\"50\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\">Cancel</a></td></tr></table></td>");
		out.println("</tr> ");
		out.println("</table>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	/**
	 *
	 */
	private void displayArchievedMessage() throws SQLException, Exception {
	
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
	
		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;
	
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
		// get the Self Assessment ID..
	
		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));
	
		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);
	
		out.println(this.header);
	
		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());
	
		out.println(header[0]);
	
		out.println("<br />");
	
		if (self != null && !self.getSelfId().trim().equals("")) {
	
			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");
	
			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");
	
			// end of table 2
			out.println("</table>");
	
			// end of table 1
			out.println("</td></tr></table>");
	
			out.println("<br />");
	
			printGreyDottedLine(out);
			out.println("<br />");
	
	
			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
			out.println("<b><span style=\"color:#ff3333\">This workspace was archieved before the self assessment can complete. Compiled assessment is not available for this self assessment.</span></b>");
			out.println("</td></tr>");
			out.println("</table>");
			// end of table 3
	
			out.println("<br />");
		}
	
	
		printGreyDottedLine(out);
		out.println("<br />");
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"75\" align=\"left\"><table summary=\"\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td></tr></table></td>");
		out.println("</tr> ");
		out.println("</table>");
	
		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");
	
		out.println("<br />");
	
	}

	/**
	 *
	 */
	private void approveCompiledAssessment() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));

		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");


		if (self != null && !self.getSelfId().trim().equals("")) {

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");

			boolean success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_ACTION_PLAN,es.gIR_USERN);

			ETSSelfMail mail = ETSSelfMailFunctions.createCompiledAssessmentStepCompleteMail(con,sSelfID,self.getProjectId(),es.gIR_USERN);
					
			boolean bsent = ETSSelfMailFunctions.sendEmail(mail);

			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
			if (success) {
				out.println("<b>You have successfully approved the compiled assessment.</b>");
			}  else {
				out.println("<span style=\"color:#ff3333\"><b>There was an error when approving the compiled self assesement. Please try again. If the problem persists, please contact your client care advocate.</b></span>");
			}
			out.println("</td></tr>");

			// end of table 3
			out.println("</table>");

			out.println("<br />");
		}

		out.println("<br />");

		printGreyDottedLine(out);
		out.println("<br />");


		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + request.getParameter("self") + "\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
		out.println("</tr> ");
		out.println("</table>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	/**
	 *
	 */
	private void closeActionPlan() throws SQLException, Exception {
	
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
	
		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;
	
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	
		// get the Self Assessment ID..
	
		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));
	
		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);
	
		out.println(this.header);
	
		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());
	
		out.println(header[0]);
	
		out.println("<br />");
	
	
		if (self != null && !self.getSelfId().trim().equals("")) {
	
			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");
	
			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");
	
			// end of table 2
			out.println("</table>");
	
			// end of table 1
			out.println("</td></tr></table>");
	
			out.println("<br />");
	
			printGreyDottedLine(out);
			out.println("<br />");
	
			boolean success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_CLOSED,es.gIR_USERN);
	
			ETSSelfMail mail = ETSSelfMailFunctions.createActionPlanStepCompleteMail(con,sSelfID,self.getProjectId(),es.gIR_USERN);
					
			boolean bsent = ETSSelfMailFunctions.sendEmail(mail);
	
			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
			if (success) {
				out.println("<b>You have successfully closed the action plan for this self assessment.</b>");
			}  else {
				out.println("<span style=\"color:#ff3333\"><b>There was an error when closing the action plan. Please try again. If the problem persists, please contact your client care advocate.</b></span>");
			}
			out.println("</td></tr>");
	
			// end of table 3
			out.println("</table>");
	
			out.println("<br />");
		}
	
		out.println("<br />");
	
		printGreyDottedLine(out);
		out.println("<br />");
	
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + request.getParameter("self") + "\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
		out.println("</tr> ");
		out.println("</table>");
	
		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");
	
		out.println("<br />");
	
	}

	/**
	 *
	 */
	private void closeSelfAssessmentConfirm() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));

		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");

		if (self != null && !self.getSelfId().trim().equals("")) {

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");

			String sCurrentStep = "";

			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_CLOSED)) {
				// table 3
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
				out.println("<b>Are you sure you want to close this self assessment?</b>");
				out.println("</td></tr>");
				out.println("</table>");
				// end of table 3
			} else {
				// table 3
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
				out.println("<b><span style=\"color:#ff3333\">This self assessment has not completed all the steps</span><br /><br />Are you sure you want to close this self assessment?</b>");
				out.println("</td></tr>");
				out.println("</table>");
				// end of table 3
			}

			out.println("<br />");
		}


		printGreyDottedLine(out);
		out.println("<br />");


		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"75\" align=\"left\"><table summary=\"\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "&etsop=closeconfirm\"><img src=\"" + Defines.BUTTON_ROOT  + "arrow_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"Yes\" /></a></td><td headers=\"\" width=\"50\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "&etsop=closeconfirm\">Yes</a></td></tr></table></td>");
		out.println("<td headers=\"\"  align=\"left\"><table summary=\"\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\"><img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" border=\"0\" alt=\"No\" /></a></td><td headers=\"\" width=\"50\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\">No</a></td></tr></table></td>");
		out.println("</tr> ");
		out.println("</table>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	/**
	 *
	 */
	private void closeSelfAssessment() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bAssessmentOwner = false;
		boolean bWorkspaceOwner = false;
		boolean bWorkspaceManager = false;
		boolean bAdmin = false;

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the Self Assessment ID..

		String sSelfID = ETSUtils.checkNull(request.getParameter("self"));

		ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,proj.getProjectId(),sSelfID);

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), self.getTitle(),self.getSelfId());

		out.println(header[0]);

		out.println("<br />");

		if (self != null && !self.getSelfId().trim().equals("")) {

			// table 1
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tblue\">");

			// table 2
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + self.getTitle() + "</td>");
			out.println("</tr>");

			// end of table 2
			out.println("</table>");

			// end of table 1
			out.println("</td></tr></table>");

			out.println("<br />");

			printGreyDottedLine(out);
			out.println("<br />");

			String sCurrentStep = "";

			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			boolean success = false;

			if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT,es.gIR_USERN);
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_ACTION_PLAN,es.gIR_USERN);
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_CLOSED,es.gIR_USERN);
			} else if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_COMPILED_ASSESSMENT)) {
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_ACTION_PLAN,es.gIR_USERN);
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_CLOSED,es.gIR_USERN);
			} else if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_ACTION_PLAN)) {
				success = ETSSelfDAO.createSelfAssessmentStep(con,self.getSelfId(),self.getProjectId(),ETSSelfConstants.SELF_STEP_CLOSED,es.gIR_USERN);
			} else if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_CLOSED)) {
				success = true;
			}

			if (success) {
				
				success = ETSSelfDAO.closeSelfAssessment(con,self.getSelfId(),self.getProjectId(),es.gIR_USERN);
				
				ETSSelfMail mail = ETSSelfMailFunctions.createCloseSelfAssessmentMail(con,sSelfID,self.getProjectId(),es.gIR_USERN);
					
				boolean bsent = ETSSelfMailFunctions.sendEmail(mail);
				
			}

			// table 3
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
			if (success) {
				out.println("<b>You have successfully closed this self assessment.</b>");
			}  else {
				out.println("<span style=\"color:#ff3333\"><b>There was an error when closing the self assesement. Please try again. If the problem persists, please contact your client care advocate.</b></span>");
			}
			out.println("</td></tr>");

			// end of table 3
			out.println("</table>");

			out.println("<br />");
		}


		printGreyDottedLine(out);
		out.println("<br />");


		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "&self=" + sSelfID + "\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
		out.println("</tr> ");
		out.println("</table>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		out.println("<br />");

	}

	private String[] getHeader(String sETSOp, String sSelfAssessmentName, String sSelfID) throws Exception{

		String[] header = new String[]{};
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		try {
			if (sETSOp.equals("")){
				header = new String[]{ETSUtils.getBookMarkString("Self Assessment Reviews","#setmet", true)};
			} else if (sETSOp.equals("assess") || sETSOp.equals("compile") || sETSOp.equals("action") || sETSOp.equals("compileconfirm")) {
				header = new String[]{ETSUtils.getTitleString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" ><span class\"small\">Self assessment reviews</span></a>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\">" + sSelfAssessmentName + "</span>")};
			} else if (sETSOp.equals("approve")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Approve compiled self assessment</span>")};
			} else if (sETSOp.equals("close") || sETSOp.equals("closeconfirm")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Close self assessment</span>")};
			} else if (sETSOp.equals("close") || sETSOp.equals("closeap")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Close action plan</span>")};
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return header;

	}

	private String[] getHeader(String sETSOp, String sSelfAssessmentName, String sSelfID, String sPrinterFriendly) throws Exception{
	
		String[] header = new String[]{};
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		try {
			if (sETSOp.equals("")){
				header = new String[]{ETSUtils.getBookMarkString("Self Assessment Reviews","#setmet", true)};
			} else if (sETSOp.equals("assess") || sETSOp.equals("compile") || sETSOp.equals("action") || sETSOp.equals("compileconfirm")) {
				if (sPrinterFriendly.trim().equalsIgnoreCase("Y")) {
					header = new String[]{ETSUtils.getTitleString("<span class\"small\">Self assessment reviews</span>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\">" + sSelfAssessmentName + "</span>")};
				} else {
					header = new String[]{ETSUtils.getTitleString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&self=" + sSelfID + "&linkid=" + this.params.getLinkId() + "\" ><span class\"small\">Self assessment reviews</span></a>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\">" + sSelfAssessmentName + "</span>")};
				}
				
			} else if (sETSOp.equals("approve")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Approve compiled self assessment</span>")};
			} else if (sETSOp.equals("close") || sETSOp.equals("closeconfirm")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Close self assessment</span>")};
			} else if (sETSOp.equals("close") || sETSOp.equals("closeap")) {
				header = new String[]{ETSUtils.getTitleString("<span class=\"subtitle\">Close action plan</span>")};
			}
	
	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return header;
	
	}

	/**
		 *
		 */
		private boolean displayOpenSelfAssessments(String sCurrentSetMetID) throws SQLException, Exception {

			PrintWriter out = this.params.getWriter();
			HttpServletRequest request = this.params.getRequest();
			Connection con = this.params.getConnection();
			ETSProj proj = this.params.getETSProj();
			boolean bPrinted = false;

			try {

				String sTC = request.getParameter("tc");

				Vector vSelf = ETSSelfDAO.getOpenSelfAssessments(con,proj.getProjectId());

				if (vSelf != null) {

					for (int i = 0; i < vSelf.size(); i++) {

						if (i == 0) {
							bPrinted = true;
							out.println("<table width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr class=\"tblue\" height=\"18\"><td headers=\"\">&nbsp;Open self assessment reviews</td></tr>");

							out.println("<tr><td headers=\"\" >");

							out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

							out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall assessment rating</b></td><td headers=\"\"  width=\"90\" align=\"middle\"><b>Days open</b></td></tr>");

							out.println("<tr><td headers=\"\" colspan=\"4\">");
							printGreyDottedLine(out);
							out.println("</td></tr>");

						}

						ETSSelfAssessment self = (ETSSelfAssessment) vSelf.elementAt(i);

						String sRowColor = "#ffffff";

						if (sCurrentSetMetID.trim().equalsIgnoreCase(self.getSelfId())) {
							sRowColor = "#eeeeee";
						}

						ArrayList expect = self.getExpectations();

						float iValue = 0;
						int iCount = 0;

						if (expect != null) {
							for (int j =0; j < expect.size(); j++) {
								ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) expect.get(j);
								
								if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING) {
									iValue = iValue + exp.getRating();
									iCount = iCount + 1;
								}
							}
						}

						if (iCount > 0) {
							
							float value = iValue / iCount; 
							
							NumberFormat format = new DecimalFormat("0.0");
							
							out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&self=" + self.getSelfId() + "&tc=" + sTC + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + self.getTitle() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\">" + format.format(value) + "</td><td headers=\"\"  align=\"middle\"><b>" + ETSUtils.dateDiff(self.getStartDate(),new Timestamp(System.currentTimeMillis())) + "</b></td></tr>");
						} else {
							out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&self=" + self.getSelfId() + "&tc=" + sTC + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + self.getTitle() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\">-</td><td headers=\"\"  align=\"middle\"><b>" + ETSUtils.dateDiff(self.getStartDate(),new Timestamp(System.currentTimeMillis())) + "</b></td></tr>");
						}
					}
				}

				if (bPrinted) {
					out.println("</td></tr></table>");
					out.println("</table>");
				}

			} catch (SQLException e){
				throw e;
			} catch (Exception e) {
				throw e;
			}

			return bPrinted;
		}

	/**
	 *
	 */
	private boolean displayClosedSelfAssessments(String sCurrentSetMetID,boolean bPrintBreak) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		boolean bPrinted = false;

		try {

			String sTC = request.getParameter("tc");

			Vector vOpenSelf = ETSSelfDAO.getClosedSelfAssessments(con,proj.getProjectId());

			if (vOpenSelf != null) {


				for (int i = 0; i < vOpenSelf.size(); i++) {

					if (i == 0) {
						bPrinted = true;
						if (bPrintBreak) {
							out.println("<br />");
						}
						out.println("<table width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr class=\"tblue\" height=\"18\"><td headers=\"\">&nbsp;Closed self assessments</td></tr>");

						out.println("<tr><td headers=\"\" >");

						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

						//out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall satisfaction</b></td><td headers=\"\"  align=\"middle\"><b>Days open</b></td></tr>");
						out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall assessment rating</b></td></tr>");

						out.println("<tr><td headers=\"\" colspan=\"4\">");
						printGreyDottedLine(out);
						out.println("</td></tr>");

					}

					ETSSelfAssessment self = (ETSSelfAssessment) vOpenSelf.elementAt(i);

					String sRowColor = "#ffffff";

					if (sCurrentSetMetID.trim().equalsIgnoreCase(self.getSelfId())) {
						sRowColor = "#eeeeee";
					}

					ArrayList expect = self.getExpectations();

					float iValue = 0;
					int iCount = 0;

					if (expect != null) {

						for (int j =0; j < expect.size(); j++) {
							ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) expect.get(j);
							if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING) {
								iValue = iValue + exp.getRating();
								iCount = iCount + 1;
							}
						}
					}

					if (iCount > 0) {
						
						float value = iValue / iCount; 
							
						NumberFormat format = new DecimalFormat("0.0");
						
						out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&self=" + self.getSelfId() + "&tc=" + sTC + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + self.getTitle() + "</a></td><td headers=\"\"  align=\"middle\">" + format.format(value) + "</td></tr>");
					} else {
						out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&self=" + self.getSelfId() + "&tc=" + sTC + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + self.getTitle() + "</a></td><td headers=\"\"  align=\"middle\">-</td></tr>");
					}
				}

			}

			if (bPrinted) {
				out.println("</td></tr></table>");
				out.println("</table>");
			}



		} catch (SQLException e){
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return bPrinted;
	}

	private static void printGreyDottedLine(PrintWriter out) {

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");


	}
}
