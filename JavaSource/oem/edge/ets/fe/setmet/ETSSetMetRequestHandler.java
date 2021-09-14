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


package oem.edge.ets.fe.setmet;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocumentManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSDealTracker;
import oem.edge.ets.fe.wspace.ETSManageWorkspaceHandler;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSSetMetRequestHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.45";
	
	private static Log logger = EtsLogger.getLogger(ETSSetMetRequestHandler.class);

	private ETSParams params;
	private String header = "";
	private EdgeAccessCntrl es = null;


	public ETSSetMetRequestHandler(ETSParams parameters, StringBuffer sHeader, EdgeAccessCntrl es1) {

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
				displayCurrentSetMet();
			} else if (sETSOp.trim().equals("inter")){
				displayInterviewTab();
			} else if (sETSOp.trim().equals("demo")){
				displayDemographics();
			} else if (sETSOp.trim().equals("action")){
				displayActionPlanDealTracker();
//			} else if (sETSOp.trim().equalsIgnoreCase("addactionplan")) {
//				displayUploadPlan();
//			} else if (sETSOp.trim().equalsIgnoreCase("delactionplan")) {
//				displayActionPlanDeleteConfirmPlan();
//			} else if (sETSOp.trim().equalsIgnoreCase("delplan")) {
//				boolean success = ETSDatabaseManager.deleteActionPlan(proj.getProjectId(),es.gIR_USERN,con);
//				String sSetMetID = ETSUtils.checkNull(this.params.getRequest().getParameter("set"));
//				this.params.getResponse().sendRedirect("ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=action&set=" + sSetMetID + "&linkid=" + params.getLinkId());
			} else if (sETSOp.trim().equalsIgnoreCase("approve") || sETSOp.trim().equalsIgnoreCase("closeap")) {
				displayApproveConfirmation();
			} else if (sETSOp.trim().equalsIgnoreCase("confirmapprove")) {
				String sSetMetID = approveInterview();
				this.params.getResponse().sendRedirect("ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&set=" + sSetMetID + "&linkid=" + params.getLinkId());
			} else if (sETSOp.trim().equalsIgnoreCase("newsetmet")) {
				this.params.getRequest().getSession().removeAttribute("SETMET-DEMO");
				displayNewSetMetForm("");
			} else if (sETSOp.trim().equalsIgnoreCase("createinterview")) {
				String sAddMember = request.getParameter("hidden_addmember");
				if (sAddMember == null || sAddMember.trim().equalsIgnoreCase("")) {
					String sError = validateCreateSetMetInterview();
					if (!sError.trim().equalsIgnoreCase("")) {
						displayNewSetMetForm(sError);
					} else {
						String sSetMetID = createInterview();
						this.params.getResponse().sendRedirect("ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&etsop=inter&set=" + sSetMetID + "&linkid=" + params.getLinkId());
					}
				} else {
					
					// dump all the values into session...
					
					String sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_month"));
					String sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_day"));
					String sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_year"));
					String sClient = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_client"));
					String sPrincipal = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_principal"));
					String sPM = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_pm"));
					String sTitle = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_title"));
					String sTeam[] = this.params.getRequest().getParameterValues("setmet_team");
					
					String sPrincipalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_day"));
					String sPrincipalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_month"));
					String sPrincipalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_year"));

					String sActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_day"));
					String sActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_month"));
					String sActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_year"));

					String sActionImplementDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_day"));
					String sActionImplementMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_month"));
					String sActionImplementYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_year"));

					String sAfterActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("after_day"));
					String sAfterActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("after_month"));
					String sAfterActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("after_year"));
					
					ETSSetMetAddMember values = new ETSSetMetAddMember();
					
					values.setSetMetClient(sClient);
					values.setSetMetDay(sCalDay);
					values.setSetMetMonth(sCalMonth);
					values.setSetMetPM(sPM);
					values.setSetMetPrincipal(sPrincipal);
					values.setSetMetTeam(sTeam);
					values.setSetMetTitle(sTitle);
					values.setSetMetYear(sCalYear);
					values.setPrincipalReviewDay(sPrincipalDay);
					values.setPrincipalReviewMonth(sPrincipalMonth);
					values.setPrincipalReviewYear(sPrincipalYear);
					values.setActionPlanCreationDay(sActionDay);
					values.setActionPlanCreationMonth(sActionMonth);
					values.setActionPlanCreationYear(sActionYear);
					values.setActionPlanImplementDay(sActionImplementDay);
					values.setActionPlanImplementMonth(sActionImplementMonth);
					values.setActionPlanImplementYear(sActionImplementYear);
					values.setAfterActionDay(sAfterActionDay);
					values.setAfterActionMonth(sAfterActionMonth);
					values.setAfterActionYear(sAfterActionYear);
					
					this.params.getRequest().getSession().setAttribute("SETMET-DEMO",values);
					
					String sTeamTopCat = ETSUtils.getTopCatId(con,proj.getProjectId(),Defines.TEAM_VT);
					this.params.getResponse().sendRedirect("ETSProjectsServlet.wss?action=admin_add&proj=" + proj.getProjectId() + "&tc=" + sTeamTopCat + "&cc=" + sTeamTopCat + "&linkid=251000&backto=setmet");
				}
			} else if (sETSOp.trim().equalsIgnoreCase("manage_wspace")) {
				displayManageWorkspace();
			}
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	/**
	 *
	 */
	private void displayActionPlanDeleteConfirmPlan() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		String sSetMetID = ETSUtils.checkNull(this.params.getRequest().getParameter("set"));

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), "","");

		out.println(header[0]);


		out.println("<form name=\"DelProjPlan\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"etsop\" value=\"delplan\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"set\" value=\"" + sSetMetID + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" class=\"subtitle\">Delete action plan confirmation</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" >&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\" height=\"18\" ><b>Please click on \"Submit\" to delete the action plan.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<!-- Gray dotted line -->");
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");

		out.println("<br />");
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=action\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=action\" >Cancel</a></td></tr></table></td>");
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

		out.println("</form>");


	}

	/**
	 *
	 */
	private void displayNewSetMetForm(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), "","");

		out.println(header[0]);


		String sCalMonth = "";
		String sCalDay = "";
		String sCalYear = "";
		String sClient = "";
		String sPrincipal = "";
		String sPM = "";
		String sTitle = "";
		String sTeam[] = new String[]{};
		String sInterviewDate = "";
		
		String sPrincipalDay = "";
		String sPrincipalMonth = "";
		String sPrincipalYear = "";

		String sActionDay = "";
		String sActionMonth = "";
		String sActionYear = "";

//		String sActionImplementDay = "";
//		String sActionImplementMonth = "";
//		String sActionImplementYear = "";

		String sAfterActionDay = "";
		String sAfterActionMonth  = "";
		String sAfterActionYear  = "";
		
		String SupressClient1 = "";
		String SupressClient2 = "";
		
		
		String sClientName = "";

		if (!sError.trim().equalsIgnoreCase("")) {

			sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_month"));
			sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_day"));
			sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_year"));
			sClient = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_client"));
			sPrincipal = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_principal"));
			sPM = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_pm"));
			sTitle = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_title"));
			sTeam = this.params.getRequest().getParameterValues("setmet_team");
			
			sPrincipalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_day"));
			sPrincipalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_month"));
			sPrincipalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_year"));

			sActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_day"));
			sActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_month"));
			sActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_year"));

//			sActionImplementDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_day"));
//			sActionImplementMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_month"));
//			sActionImplementYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_year"));

			sAfterActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("after_day"));
			sAfterActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("after_month"));
			sAfterActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("after_year"));
			
			SupressClient1 = ETSUtils.checkNull(this.params.getRequest().getParameter("supress_client1"));
			SupressClient2 = ETSUtils.checkNull(this.params.getRequest().getParameter("supress_client2"));
			
			sClientName = ETSUtils.checkNull(this.params.getRequest().getParameter("clientname"));
			
		}

		out.println("<form name=\"SetMet\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		// start of the static content
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" >A Set/Met is created as a 5 step process as follows:</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");		
		
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr><td headers=\"\"  class=\"tdblue\">");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Set/Met Title</td>");
		out.println("</tr>");
		out.println("<tr><td headers=\"\"  width=\"443\">");

		out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\">");
		out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

		out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");

		out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\"><b>Step</b></td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\"><b>Owner</b></td>");
		out.println("<td headers=\"\" align=\"left\"><b>Date</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">Initial interview</td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Client care advocate</td>");
		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">Client review/approve interview</td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Client</td>");
		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">Principal review and comments</td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Principal</td>");
		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">Action plan</td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Program manager</td>");
		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
//		out.println("<tr>");
//		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">After action review</td>");
//		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Client care advocate</td>");
//		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
//		out.println("</tr>");
//		out.println("<tr>");
//		out.println("<td headers=\"\" colspan=\"3\" align=\"left\">");
//		printGreyDottedLine(out);
//		out.println("</td>");
//		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"170\" align=\"left\" valign=\"top\">Set/Met final rating</td>");
		out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">Client</td>");
		out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		out.println("</tr>");
		out.println("</table>");
		
		
		out.println("</td></tr></table>");
		out.println("</td></tr></table>");
		out.println("</td></tr></table>");
		out.println("</td></tr></table>");
		
		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" >Please select the step owners and due dates for this Set/Met review. Note that client actions do not require a target date. The system will reflect actual dates when client completes a step.</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br /><br />");
		
		// end of static content

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" ><span class=\"small\">Fields marked with <span style=\"color:#ff0000\">*</span> are mandatory fields.</span></td>");
		out.println("</tr>");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" >");
		printGreyDottedLine(out);
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr valign=\"top\">");
		out.println("<td headers=\"\" >&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		if (!sError.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\"><span style=\"color:#ff3333\">" + sError + "<span><br /></td>");
		}
		out.println("</tr>");
		out.println("</table>");
		

		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"etsop\" value=\"createinterview\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + this.params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + this.params.getTopCat() + "\" />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr><td headers=\"\"  class=\"tdblue\">");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Add a new Set/Met review</td>");
		out.println("</tr>");
		out.println("<tr><td headers=\"\"  width=\"443\">");

		out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\">");
		out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

		out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");

		out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Company:</b></td>");
		out.println("<td headers=\"\"  align=\"left\"><b>" + proj.getCompany() + "</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"title\">Set/Met title:</label></b></td>");
		if (sError.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\"><span class=\"small\">[Max. 50 characters]<br /><input type=\"text\" name=\"setmet_title\" class=\"iform\" id=\"title\" maxlength=\"50\" size=\"45\" /></td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\"><span class=\"small\">[Max. 50 characters]<br /><input type=\"text\" name=\"setmet_title\" class=\"iform\" id=\"title\" maxlength=\"50\" size=\"45\" value=\"" + sTitle + "\" /></td>");
		}
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b>Client care advocate:</b></td>");
		out.println("<td headers=\"\"  align=\"left\"><b>" + ETSUtils.getUsersName(con,ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId())) + "</b></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_date\">Interview date:</label></b></td>");
		out.println("<td headers=\"\"  align=\"left\">");

		String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");

		String sStartMonth = sTodaysDate.substring(0, 2);
		String sStartDate = sTodaysDate.substring(3, 5);
		String sStartYear = sTodaysDate.substring(6, 10);

		if (sError.trim().equalsIgnoreCase("")) {
			showDate(out,sStartMonth,sStartDate,sStartYear,"setmet_month","setmet_day","setmet_year","label_date");
		} else {
			showDate(out,sCalMonth,sCalDay,sCalYear,"setmet_month","setmet_day","setmet_year","label_date");
		}

		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		
		
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"client\">Client name:</label></b></td>");
		if (sError.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\">" + displayClientAsSelect(con,"setmet_client","client",proj.getProjectId(),"",es.gIR_USERN) + " <b>or</b> </td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\">" + displayClientAsSelect(con,"setmet_client","client",proj.getProjectId(),sClient,es.gIR_USERN) + " <b>or</b> </td>");
		}
		out.println("</tr>");


		// added for 5.4.1 - sathish

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  align=\"left\">enter a client name</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\">&nbsp;</td>");
		out.println("<td headers=\"\"  align=\"left\"><input type=\"text\" name=\"clientname\" class=\"iform\" id=\"client\" maxlength=\"80\" size=\"45\" value=\"" + sClientName + "\" /></td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		
			
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b>Client e-mail notification:</label></b></td>");
		if (SupressClient1.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client1\" id=\"client_supress1\" value=\"Y\" /></td><td headers=\"\"><label for=\"client_supress1\">Supress client e-mail notification for <b>Client review/approve interview</b> step.</label></td></tr></table></td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client1\" id=\"client_supress1\" value=\"Y\" checked=\"checked\" selected=\"selected\" /></td><td headers=\"\"><label for=\"client_supress1\">Supress client e-mail notification for <b>Client review/approve interview</b> step.</label></td></tr></table></td>");
		}
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\">&nbsp;</td>");
		if (SupressClient2.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client2\" id=\"client_supress2\" value=\"Y\" /></td><td headers=\"\"><label for=\"client_supress2\">Supress client e-mail notification for <b>Set/Met final rating</b> step.</label></td></tr></table></td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\"><table><tr><td headers=\"\" width=\"16\" valign=\"top\"><input type=\"checkbox\" class=\"iform\" name=\"supress_client2\" id=\"client_supress2\" value=\"Y\" checked=\"checked\" selected=\"selected\" /></td><td headers=\"\"><label for=\"client_supress2\">Supress client e-mail notification for <b>Set/Met final rating</b> step.</label></td></tr></table></td>");
		}
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");


		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"principal\">Principal:</label></b></td>");
		if (sError.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_principal","principal",proj.getProjectId(),"",es.gIR_USERN) + "</td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_principal","principal",proj.getProjectId(),sPrincipal,es.gIR_USERN) + "</td>");
		}
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_pdate\">Principal review date:</label></b></td>");
		out.println("<td headers=\"\"  align=\"left\">");

		if (sError.trim().equalsIgnoreCase("")) {
			showDate(out,sStartMonth,sStartDate,sStartYear,"principal_month","principal_day","principal_year","label_pdate");
		} else {
			showDate(out,sPrincipalMonth,sPrincipalDay,sPrincipalYear,"principal_month","principal_day","principal_year","label_pdate");
		}

		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\"><b><label for=\"pm\">Program manager:</label></b></td>");
		if (sError.trim().equalsIgnoreCase("")) {
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_pm","pm",proj.getProjectId(),"",es.gIR_USERN) + "</td>");
		} else {
			out.println("<td headers=\"\"  align=\"left\">" + displayInviteesAsSelect(con,"setmet_pm","pm",proj.getProjectId(),sPM,es.gIR_USERN) + "</td>");
		}
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");

		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"2\" valign=\"top\" align=\"left\"><span style=\"color:#ff0000\">*</span></td>");
		out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\"><b><label for=\"label_adate\">Action plan date:</label></b></td>");
		out.println("<td headers=\"\"  align=\"left\">");

		if (sError.trim().equalsIgnoreCase("")) {
			showDate(out,sStartMonth,sStartDate,sStartYear,"action_month","action_day","action_year","label_adate");
		} else {
			showDate(out,sActionMonth,sActionDay,sActionYear,"action_month","action_day","action_year","label_adate");
		}

		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
		out.println("</tr>");
		
		out.println("</table>");

		out.println("</td></tr>");
		out.println("</table>");

		out.println("</td></tr>");
		out.println("</table>");

		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
		out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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

		out.println("</form>");


	}

	/**
	 *
	 */
	private String approveInterview() throws SQLException, Exception {

		// approve the interview and based on the current state..

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		ETSProj proj = this.params.getETSProj();
		Connection con = this.params.getConnection();
		String sSetMetID = "";

		try {


			sSetMetID = ETSUtils.checkNull(this.params.getRequest().getParameter("set"));
			
			String sCurrentStep = getCurrentState(sSetMetID);
			
			//String sCurrentStep = ETSUtils.checkNull(this.params.getRequest().getParameter("current_step"));
			
			String sStep = "";
			String sOpen = Defines.SETMET_OPEN;

			if (sCurrentStep.trim().equals("")) {
				sStep = Defines.SETMET_CLIENT_INTERVIEW;
			} else if (sCurrentStep.trim().equals(Defines.SETMET_CLIENT_INTERVIEW)) {
				sStep = Defines.SETMET_CLIENT_APPROVED;
			} else if (sCurrentStep.trim().equals(Defines.SETMET_CLIENT_APPROVED)) {
				sStep = Defines.SETMET_PRINCIPAL_APPROVED;
			} else if (sCurrentStep.trim().equals(Defines.SETMET_PRINCIPAL_APPROVED)) {
				sStep = Defines.SETMET_ACTION_PLAN;
			} else if (sCurrentStep.trim().equals(Defines.SETMET_ACTION_PLAN)) {
//				sStep = Defines.SETMET_CLOSE;
//			} else if (sCurrentStep.trim().equals(Defines.SETMET_CLOSE)) {
				sStep = Defines.SETMET_FINAL_RATING;
				sOpen = Defines.SETMET_CLOSED;
			}

			ETSSetMetActionState state = new ETSSetMetActionState();

			state.setActionBy(es.gIR_USERN);
			state.setActionDate(new Timestamp(System.currentTimeMillis()));
			state.setLastTimestamp(new Timestamp(System.currentTimeMillis()));
			state.setProjectID(proj.getProjectId());
			state.setSetMetID(sSetMetID);
			state.setStep(sStep);

			// update the state...

			int iCount = ETSSetMetDAO.insertApproval(con,state);

			// update the rating ...

			String sRatingEditable = ETSUtils.checkNull(this.params.getRequest().getParameter("rating_editable"));

			if (sRatingEditable.trim().equalsIgnoreCase("Y")) {

				String[] sQuestionSeq = this.params.getRequest().getParameterValues("question_seqno");
				int i = 0;
				if (sQuestionSeq != null) {
					while (i < sQuestionSeq.length) {
						String sValue = sQuestionSeq[i];
						String sQuestionNo = sValue.substring(0,sValue.indexOf("-"));
						String sSeqNo = sValue.substring(sValue.indexOf("-") + 1);

						String sRating = ETSUtils.checkNull(this.params.getRequest().getParameter("init_rating" + sValue));

						if (!sRating.trim().equals("")) {
							int iReturn = ETSSetMetDAO.updateSetMetInitialRating(con,sSetMetID,proj.getProjectId(),Integer.parseInt(sQuestionNo),Integer.parseInt(sSeqNo),Double.parseDouble(sRating),es.gIR_USERN);
						}
						i++;
					}

				}

			}

			// update final rating...

			String sFinalRating = ETSUtils.checkNull(this.params.getRequest().getParameter("final_rating_editable"));

			if (sFinalRating.trim().equalsIgnoreCase("Y")) {

				String[] sQuestionSeq = this.params.getRequest().getParameterValues("question_seqno");
				int i = 0;
				if (sQuestionSeq != null) {
					while (i < sQuestionSeq.length) {
						String sValue = sQuestionSeq[i];
						String sQuestionNo = sValue.substring(0,sValue.indexOf("-"));
						String sSeqNo = sValue.substring(sValue.indexOf("-") + 1);

						String sRating = ETSUtils.checkNull(this.params.getRequest().getParameter("final_rating" + sValue));

						
						if (!sRating.trim().equals("")) {
							int iReturn = ETSSetMetDAO.updateSetMetFinalRating(con,sSetMetID,proj.getProjectId(),Integer.parseInt(sQuestionNo),Integer.parseInt(sSeqNo),Double.parseDouble(sRating),es.gIR_USERN);
						}
						i++;
					}

				}

			}


			// update the main if closed...

			if (iCount > 0 && sStep == Defines.SETMET_FINAL_RATING) {
				ETSSetMetDAO.closeSetMet(con,proj.getProjectId(),sSetMetID);
			}

			sendEmailNotification();


		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}

		return sSetMetID;

	}

	/**
	 *
	 */
	private String displayApproveConfirmation() throws SQLException, Exception {

		// approve the interview and based on the current state..

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		ETSProj proj = this.params.getETSProj();
		Connection con = this.params.getConnection();
		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();

		String sSetMetID = "";

		try {


			out.println(this.header);

			sSetMetID = ETSUtils.checkNull(this.params.getRequest().getParameter("set"));

			ETSSetMet SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);

			String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), SetMet.getSetMetName(),SetMet.getSetMetID());

			out.println(header[0]);


			String sCurrentState = getCurrentState(SetMet.getSetMetID());

			String sCurrentStep = ETSUtils.checkNull(this.params.getRequest().getParameter("current_step"));
			String sRatingEditable = ETSUtils.checkNull(this.params.getRequest().getParameter("rating_editable"));
			String[] sQuestionSeq = this.params.getRequest().getParameterValues("question_seqno");
			String sFinalRating = ETSUtils.checkNull(this.params.getRequest().getParameter("final_rating_editable"));

			String sFrom = ETSUtils.checkNull(this.params.getRequest().getParameter("from"));

			out.println("<form name=\"DelProjPlan\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
			out.println("<input type=\"hidden\" name=\"etsop\" value=\"confirmapprove\" />");
			out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + params.getLinkId() + "\" />");
			out.println("<input type=\"hidden\" name=\"set\" value=\"" + sSetMetID + "\" />");
			out.println("<input type=\"hidden\" name=\"tc\" value=\"" + params.getTopCat() + "\" />");
			out.println("<input type=\"hidden\" name=\"current_step\" value=\"" + sCurrentStep + "\" />");
			out.println("<input type=\"hidden\" name=\"rating_editable\" value=\"" + sRatingEditable + "\" />");
			out.println("<input type=\"hidden\" name=\"final_rating_editable\" value=\"" + sFinalRating + "\" />");

			int i = 0;
			if (sQuestionSeq != null) {
				while (i < sQuestionSeq.length) {
					String sValue = sQuestionSeq[i];
					String sRating = ETSUtils.checkNull(this.params.getRequest().getParameter("init_rating" + sValue));
					String sFinalRatingValue = ETSUtils.checkNull(this.params.getRequest().getParameter("final_rating" + sValue));
					out.println("<input type=\"hidden\" name=\"question_seqno\" value=\"" + sValue + "\" />");
					out.println("<input type=\"hidden\" name=\"init_rating" + sValue + "\" value=\"" + sRating + "\" />");
					out.println("<input type=\"hidden\" name=\"final_rating" + sValue + "\" value=\"" + sFinalRatingValue + "\" />");
					i++;
				}
			}

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  align=\"left\" height=\"18\" class=\"subtitle\">Set/Met approval confirmation</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  align=\"left\" height=\"18\" >&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  align=\"left\" height=\"18\" >");

			if (sCurrentState.trim().equalsIgnoreCase("")) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below to send the Set/Met interview details to your client for review and approval.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW)) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below to send the Set/Met interview details to IBM.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below to begin creation of action plan for this Set/Met.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				//out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below to send the Set/Met action plan for review and implementation.</b></td>");
				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below if action plan for this Set/Met has been implemented.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
//			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
//				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
//				out.println("<tr style=\"background-color: #eeeeee\">");
//				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below if after action review for this Set/Met has been done.</b></td>");
//				out.println("</tr>");
//				out.println("</table>");
//				out.println("<br />");
//			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN_APPROVED)) {
//				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
//				out.println("<tr style=\"background-color: #eeeeee\">");
//				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below if action plan for this Set/Met has been implemented.</b></td>");
//				out.println("</tr>");
//				out.println("</table>");
//				out.println("<br />");
			} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  align=\"left\"><b>Please click \"Submit\" button below to close this Set/Met.</b></td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("<br />");
			}

			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<!-- Gray dotted line -->");
			out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<!-- End Gray dotted line -->");

			out.println("<br />");
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
			if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=action\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=action\" >Cancel</a></td></tr></table></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=inter\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&set=" + sSetMetID + "&etsop=inter\" >Cancel</a></td></tr></table></td>");
			}

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

			out.println("</form>");



		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}

		return sSetMetID;

	}

	/**
	 *
	 */
	private void displayCurrentSetMet() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
	
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")), SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"257\">");
			out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=manage_wspace\" >Manage this workspace</a>");
			out.println("</td>");
			out.println("<td headers=\"\"  width=\"100\" align=\"right\">");
			out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\"><img align=\"top\" src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\"  border=\"0\" alt=\"About Set/met\" /></a>");
			out.println("</td>");
			out.println("<td headers=\"\"  align=\"left\">");
			out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\">About Set/Met</a>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
		} else {
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"257\">");
			out.println("&nbsp;");
			out.println("</td>");
			out.println("<td headers=\"\"  width=\"100\" align=\"right\">");
			out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\"><img align=\"top\" src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\"  border=\"0\" alt=\"About Set/met\" /></a>");
			out.println("</td>");
			out.println("<td headers=\"\"  align=\"left\">");
			out.println("<a href=\"" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSAboutSetMetServlet.wss','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120');return false;\">About Set/Met</a>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
		}


		//out.println("Set/Met");

		String sPrimaryContactID = ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId());

//		Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, con);
//		ETSUser own =  new ETSUser();
//		if (owners != null) {
//			own = (ETSUser) owners.elementAt(0);
//		}

		String sPrincipal = SetMet.getSetMetBSE();
		String sPM = SetMet.getSetMetPractice();
		String sInterviewBy = SetMet.getInterviewByIRID();
		
		Vector vNotifications = ETSSetMetDAO.getSetMetNotifications(con,proj.getProjectId(),SetMet.getSetMetID());
		
		Timestamp tPrincipalDueDate = null;
		Timestamp tActionDueDate = null;
		Timestamp tActionImplementDueDate = null;
		Timestamp tAfterActionDueDate = null;

		
		if (vNotifications != null && vNotifications.size() > 0) {
					
			for (int i = 0; i < vNotifications.size(); i++) {
						
				ETSSetMetNotify notify = (ETSSetMetNotify) vNotifications.elementAt(i);

				if (notify.getStep().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
					tPrincipalDueDate = notify.getDueDate();
				} else if (notify.getStep().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
					tActionDueDate = notify.getDueDate();
//				} else if (notify.getStep().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN_APPROVED)) {
//					tActionImplementDueDate = notify.getDueDate();
				} else if (notify.getStep().equalsIgnoreCase(Defines.SETMET_CLOSE)) {
					tAfterActionDueDate = notify.getDueDate();
				}
			}
		}
		

		if (SetMet != null && !SetMet.getSetMetID().trim().equals("")) {
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			out.println("<tr><td headers=\"\"  class=\"tdblue\">");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			if (SetMet.getState().equalsIgnoreCase(Defines.SETMET_OPEN) && (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || (ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId()).equalsIgnoreCase(es.gIR_USERN) && !proj.getProject_status().equalsIgnoreCase("A")))) {
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"400\" align=\"left\">&nbsp;" + SetMet.getSetMetName() + "</td><td headers=\"\" align=\"right\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td><td headers=\"\" align=\"left\" style=\"color: #ffffff; font-weight: normal\"><a  style=\"color: #ffffff; font-weight: normal\" href=\"" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "\" target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><b>Edit</b></a></td></tr></table></td>");
			} else {
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;" + SetMet.getSetMetName() + "</td>");
			}
			
			out.println("</tr>");
			out.println("<tr><td headers=\"\"  width=\"443\">");

			out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
			out.println("<tr valign=\"middle\">");
			out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");


			String sCurrentState = getCurrentState(SetMet.getSetMetID());

			// get the workspace manager...

			boolean bDisplay = false;
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
				bDisplay = true;
			} else { 
				if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
					bDisplay = true;
				}
			}

			if (bDisplay) {
				if (sCurrentState.trim().equalsIgnoreCase("") && es.gIR_USERN.trim().equalsIgnoreCase(sInterviewBy)) {
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">Use the 'initial interview form' to document the notes from your " + ETSUtils.formatDate(SetMet.getMeetingDate()) + " Set/Met client interview.</td></tr></table>");
					out.println("</td></tr>");
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View initial interview</a></td></tr></table>");
					out.println("</td></tr>");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID())) {
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'initial interview' has been documented and is ready for your review and approval.</td></tr></table>");
					out.println("</td></tr>");
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View initial interview</a></td></tr></table>");
					out.println("</td></tr>");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPrincipal)) {
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'initial interview' has been approved by the client and is ready for your (Principal) review and comment.</td></tr></table>");
					out.println("</td></tr>");
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View initial interview</a></td></tr></table>");
					out.println("</td></tr>");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPM)) {
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'action plan' is under development.</td></tr></table>");
					out.println("</td></tr>");
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View action plan draft</a></td></tr></table>");
					out.println("</td></tr>");
//				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(sPM)) {
//					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
//					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'action plan' is completed. Please document your final rating for the Set/Met.</td></tr></table>");
//					out.println("</td></tr>");
//					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
//					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View action plan</a></td></tr></table>");
//					out.println("</td></tr>");
//				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sInterviewBy)) {
//					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
//					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'action plan' is completed. Use the 'after action review' to document your after action interview.</td></tr></table>");
//					out.println("</td></tr>");
//					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
//					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">View after action review</a></td></tr></table>");
//					out.println("</td></tr>");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID())) {
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  valign=\"top\" align=\"left\">The 'action plan' is completed. Please document your final rating for the Set/Met.</td></tr></table>");
					out.println("</td></tr>");
					out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" >");
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"\" cellspacing=\"0\" border=\"0\"><tr style=\"background-color: #ffff99\"><td headers=\"\"  height=\"21\" width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  height=\"21\" align=\"left\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Final rating</a></td></tr></table>");
					out.println("</td></tr>");
				}
			}
			
			if (SetMet.getState().trim().equalsIgnoreCase(Defines.SETMET_OPEN)) {
				
				int iDateDiff = ETSUtils.dateDiff(SetMet.getMeetingDate(),new Timestamp(System.currentTimeMillis()));
					
				if (iDateDiff < 0) {
					iDateDiff = 0;
				}
				
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">This Set/Met review has been open for <b>" + iDateDiff + "</b> days.");
				out.println("</td></tr>");
				out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\">");
				printGreyDottedLine(out);
				out.println("</td></tr>");
			} else {
				//out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\"  height=\"24\">This Set/Met review was open for <b>" + ETSUtils.dateDiff(SetMet.getMeetingDate(),SetMet.getLastTimestamp()) + "</b> days.");
				//out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\"  height=\"24\">&nbsp;");
				//out.println("</td></tr>");
			}



			out.println("<tr><td headers=\"\">");

			Vector vActionStates = SetMet.getSetMetStates();

			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\" align=\"left\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><span class=\"small\">Step</span></td>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\"><span class=\"small\">Owner</span></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">Target / Actual</span></td>");
			out.println("</tr>");
			out.println("<tr height=\"21\" style=\"font-weight: normal;\">");

			if (vActionStates != null) {

				boolean bAvailable = false;

				for (int i = 0; i < vActionStates.size(); i++) {

					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);

					String sState = state.getStep();
					Timestamp tDate = state.getActionDate();
					String sBy = state.getActionBy();

					if (sState.equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW)) {
						bAvailable = true;
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Initial interview</a></td>");
						//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sInterviewBy) + "</td>");
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
						out.println("</tr>");
						break;
					}

				}

				if (!bAvailable) {
					if (sCurrentState.trim().equalsIgnoreCase("") && es.gIR_USERN.trim().equalsIgnoreCase(sInterviewBy) && bDisplay) {
						out.println("<tr height=\"21\" style=\"font-weight: normal; background-color: #ffff99\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					} else {
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
					}
					out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Initial interview</a></td>");
					out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sInterviewBy) + "</td>");
					out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
					out.println("</tr>");
				}


				bAvailable = false;

				for (int i = 0; i < vActionStates.size(); i++) {

					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);

					String sState = state.getStep();
					Timestamp tDate = state.getActionDate();
					String sBy = state.getActionBy();

					if (sState.equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
						bAvailable = true;
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Client review/approve interview</a></td>");
						//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
						if (!SetMet.getClientIRID().equalsIgnoreCase("")) {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,SetMet.getClientIRID()) + "</td>");
						} else {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + SetMet.getClientName() + "</td>");
						}
						
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
						out.println("</tr>");
						break;
					}

				}

				if (!bAvailable) {
					if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID()) && bDisplay) {
						out.println("<tr height=\"21\" style=\"font-weight: normal; background-color: #ffff99\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					} else {
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
					}
					out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Client review/approve interview</a></td>");
					if (!SetMet.getClientIRID().equalsIgnoreCase("")) {
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,SetMet.getClientIRID()) + "</td>");
					} else {
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + SetMet.getClientName() + "</td>");
					}
					
					out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
					out.println("</tr>");
				}

				bAvailable = false;
			
				// client should not see the principal review and approve step.
				if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {
					// also check to see if the logged in person is internal. if yes, then display
					if (es.gDECAFTYPE.equalsIgnoreCase("I")) {
						
						for (int i = 0; i < vActionStates.size(); i++) {
		
							ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);
		
							String sState = state.getStep();
							Timestamp tDate = state.getActionDate();
							String sBy = state.getActionBy();
		
							if (sState.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
								bAvailable = true;
								out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
								out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
								out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Principal review and comments</a></td>");
								//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
								out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPrincipal) + "</td>");
								out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
								out.println("</tr>");
								break;
							}
		
						}
		
						if (!bAvailable) {
		
							if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPrincipal) && bDisplay) {
		
								out.println("<tr height=\"21\" style=\"font-weight: normal;  background-color: #ffff99\">");
								out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
								out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Principal review and comments</a></td>");
								out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPrincipal) + "</td>");
								if (tPrincipalDueDate == null) {
									out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
								} else {
									out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tPrincipalDueDate) + "</td>");
								}
								out.println("</tr>");
		
							} else {
								out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
								out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Principal review and comments</a></td>");
								out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPrincipal) + "</td>");
								if (tPrincipalDueDate == null) {
									out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
								} else {
									out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tPrincipalDueDate) + "</td>");
								}
								out.println("</tr>");
							}
						}
					}
				}
				
				bAvailable = false;

				for (int i = 0; i < vActionStates.size(); i++) {

					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);

					String sState = state.getStep();
					Timestamp tDate = state.getActionDate();
					String sBy = state.getActionBy();

					if (sState.equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
						bAvailable = true;
						out.println("<tr height=\"21\" style=\"font-weight: normal; \">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></td>");
						//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPM) + "</td>");
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
						out.println("</tr>");
						break;
					}

				}

				if (!bAvailable) {

					if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPM) && bDisplay) {

						out.println("<tr height=\"21\" style=\"font-weight: normal;  background-color: #ffff99\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></td>");
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPM) + "</td>");
						if (tActionDueDate == null) {
							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
						} else {
							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tActionDueDate) + "</td>");
						}
						out.println("</tr>");

					} else {
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></td>");
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPM) + "</td>");
						if (tActionDueDate == null) {
							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
						} else {
							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tActionDueDate) + "</td>");
						}
						out.println("</tr>");
					}
				}

//				bAvailable = false;
//
//				for (int i = 0; i < vActionStates.size(); i++) {
//
//					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);
//
//					String sState = state.getStep();
//					Timestamp tDate = state.getActionDate();
//					String sBy = state.getActionBy();
//
//					if (sState.equalsIgnoreCase(Defines.SETMET_ACTION_PLAN_APPROVED)) {
//						bAvailable = true;
//						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan implementation</a></td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
//						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
//						out.println("</tr>");
//						break;
//					}
//
//				}
//
//				if (!bAvailable) {
//
//					if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(sPM) && bDisplay) {
//
//						out.println("<tr height=\"21\" style=\"font-weight: normal;  background-color: #ffff99\">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan implementation</a></td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPM) + "</td>");
//						if (tActionImplementDueDate == null) {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
//						} else {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tActionImplementDueDate) + "</td>");
//						}
//						out.println("</tr>");
//					} else {
//						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan implementation</a></td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sPM) + "</td>");
//						if (tActionImplementDueDate == null) {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
//						} else {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tActionImplementDueDate) + "</td>");
//						}
//						out.println("</tr>");
//					}
//				}

//				bAvailable = false;
//
//				for (int i = 0; i < vActionStates.size(); i++) {
//
//					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);
//
//					String sState = state.getStep();
//					Timestamp tDate = state.getActionDate();
//					String sBy = state.getActionBy();
//
//					if (sState.equalsIgnoreCase(Defines.SETMET_CLOSE)) {
//						bAvailable = true;
//						out.println("<tr height=\"21\" style=\"font-weight: normal; \">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">After action review</a></td>");
//						//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sInterviewBy) + "</td>");
//						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
//						out.println("</tr>");
//						break;
//					}
//
//				}
//
//				if (!bAvailable) {
//
//					if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(sInterviewBy) && bDisplay) {
//						out.println("<tr height=\"21\" style=\"font-weight: normal; background-color: #ffff99\">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">After action review</a></td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sInterviewBy) + "</td>");
//						if (tAfterActionDueDate == null) {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
//						} else {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tAfterActionDueDate) + "</td>");
//						}
//						out.println("</tr>");
//					} else {
//						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
//						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
//						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">After action review</a></td>");
//						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sInterviewBy) + "</td>");
//						if (tAfterActionDueDate == null) {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
//						} else {
//							out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tAfterActionDueDate) + "</td>");
//						}
//						out.println("</tr>");
//					}
//				}

				bAvailable = false;

				for (int i = 0; i < vActionStates.size(); i++) {

					ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);

					String sState = state.getStep();
					Timestamp tDate = state.getActionDate();
					String sBy = state.getActionBy();

					if (sState.equalsIgnoreCase(Defines.SETMET_FINAL_RATING)) {
						bAvailable = true;
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=0\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Set/Met final rating</a></td>");
						//out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,sBy) + "</td>");
						if (!SetMet.getClientIRID().equalsIgnoreCase("")) {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,SetMet.getClientIRID()) + "</td>");
						} else {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + SetMet.getClientName() + "</td>");
						}
						
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\">" + ETSUtils.formatDate(tDate)+ "</td>");
						out.println("</tr>");
						break;
					}

				}

				if (!bAvailable) {

					if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID()) && bDisplay) {
						out.println("<tr height=\"21\" style=\"font-weight: normal;  background-color: #ffff99\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Set/Met final rating</a></td>");
						out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,SetMet.getClientIRID()) + "</td>");
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
						out.println("</tr>");
					} else {
						out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
						out.println("<td headers=\"\" width=\"16\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"197\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Set/Met final rating</a></td>");
						if (!SetMet.getClientIRID().equalsIgnoreCase("")) {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + ETSUtils.getUsersName(con,SetMet.getClientIRID()) + "</td>");
						} else {
							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">" + SetMet.getClientName() + "</td>");
						}
						
						out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"></td>");
						out.println("</tr>");
					}
				}
			}

			out.println("</table>");

			out.println("</td></tr>");

			out.println("</table>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("</td></tr></table>");

			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=SETMET&mod=1\" width=\"16\" height=\"16\" alt=\"Completed\" border=\"0\" /></td>");
			out.println("<td headers=\"\" width=\"100\" align=\"left\" class=\"small\" style=\"color:#666666\">(Completed)</td>");
			out.println("<td headers=\"\" width=\"16\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "aud.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			out.println("<td headers=\"\" align=\"left\" class=\"small\" style=\"color:#666666\">(Action required)</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"5\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");

			printGreyDottedLine(out);

			out.println("<br />");

		}

		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER) || (ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId()).equalsIgnoreCase(es.gIR_USERN) && !proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D"))) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"16\" align=\"left\" valign=\"middle\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=newsetmet&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">Add a new Set/Met</a></td>");
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

		// print the open set mets
		out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr><td headers=\"\" width=\"443\" valign=\"top\">");

		boolean bOpen = displayOpenSetMets(SetMet.getSetMetID());

		boolean bClosed = displayClosedSetMets(SetMet.getSetMetID(),bOpen);

		out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\" valign=\"top\">");

		if (bOpen || bClosed) {
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" class=\"tblue\" height=\"18\">&nbsp;Set/Met key</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\"  style=\"background-color: #006699\">");
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
	private boolean displayOpenSetMets(String sCurrentSetMetID) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		boolean bPrinted = false;

		try {

			Vector vOpenSetMet = ETSSetMetDAO.getOpenSetMets(con,proj.getProjectId());


			if (vOpenSetMet != null) {

				for (int i = 0; i < vOpenSetMet.size(); i++) {

					if (i == 0) {
						bPrinted = true;
						out.println("<table width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr class=\"tblue\" height=\"18\"><td headers=\"\">&nbsp;Open Set/Met reviews</td></tr>");

						out.println("<tr><td headers=\"\" >");

						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

						out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall satisfaction</b></td><td headers=\"\"  width=\"90\" align=\"middle\"><b>Days open</b></td></tr>");

						out.println("<tr><td headers=\"\" colspan=\"4\">");
						printGreyDottedLine(out);
						out.println("</td></tr>");

					}

					ETSSetMet setmet = (ETSSetMet) vOpenSetMet.elementAt(i);

					String sRowColor = "#ffffff";

					if (sCurrentSetMetID.trim().equalsIgnoreCase(setmet.getSetMetID())) {
						sRowColor = "#eeeeee";
					}

					Vector vExpectation = ETSSetMetDAO.getSetMetExpectations(con,proj.getProjectId(),setmet.getSetMetID());

					boolean bDisplayed = false;
					
					int iDateDiff = ETSUtils.dateDiff(setmet.getMeetingDate(),new Timestamp(System.currentTimeMillis()));
					
					if (iDateDiff < 0) {
						iDateDiff = 0;
					}

					if (vExpectation != null) {

						for (int j =0; j < vExpectation.size(); j++) {
							ETSSetMetExpectation exp = (ETSSetMetExpectation) vExpectation.elementAt(j);
							// hardcoding 4 here because 4 is always going to be overall satisfaction.
							if (exp.getQuestionID() == 4) {
								bDisplayed = true;
								out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&set=" + setmet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + setmet.getSetMetName() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\"><b>" + exp.getExpectRating() + "</b></td><td headers=\"\"  align=\"middle\"><b>" + iDateDiff + "</b></td></tr>");
								break;
							}
						}
					}

					if (!bDisplayed) {
						out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&set=" + setmet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + setmet.getSetMetName() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\">-</td><td headers=\"\"  align=\"middle\"><b>" + iDateDiff + "</b></td></tr>");
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
	private boolean displayClosedSetMets(String sCurrentSetMetID,boolean bPrintBreak) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		boolean bPrinted = false;

		try {

			Vector vOpenSetMet = ETSSetMetDAO.getClosedSetMets(con,proj.getProjectId());


			if (vOpenSetMet != null) {


				for (int i = 0; i < vOpenSetMet.size(); i++) {

					if (i == 0) {
						bPrinted = true;
						if (bPrintBreak) {
							out.println("<br />");
						}
						out.println("<table width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr class=\"tblue\" height=\"18\"><td headers=\"\">&nbsp;Closed Set/Met reviews</td></tr>");

						out.println("<tr><td headers=\"\" >");

						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

						//out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall satisfaction</b></td><td headers=\"\"  align=\"middle\"><b>Days open</b></td></tr>");
						out.println("<tr class=\"small\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\">&nbsp;</td><td headers=\"\"  width=\"100\" align=\"middle\"><b>Overall satisfaction</b></td><td headers=\"\"  width=\"90\" align=\"middle\">&nbsp;</td></tr>");

						out.println("<tr><td headers=\"\" colspan=\"4\">");
						printGreyDottedLine(out);
						out.println("</td></tr>");

					}

					ETSSetMet setmet = (ETSSetMet) vOpenSetMet.elementAt(i);

					String sRowColor = "#ffffff";

					if (sCurrentSetMetID.trim().equalsIgnoreCase(setmet.getSetMetID())) {
						sRowColor = "#eeeeee";
					}

					Vector vExpectation = ETSSetMetDAO.getSetMetExpectations(con,proj.getProjectId(),setmet.getSetMetID());

					boolean bDisplayed = false;

					if (vExpectation != null) {

						for (int j =0; j < vExpectation.size(); j++) {
							ETSSetMetExpectation exp = (ETSSetMetExpectation) vExpectation.elementAt(j);
							// hardcoding 4 here because 4 is always going to be overall satisfaction.
							if (exp.getQuestionID() == 4) {
								bDisplayed = true;
								out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&set=" + setmet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + setmet.getSetMetName() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\"><b>" + exp.getFinalRating() + "</b></td><td headers=\"\"  align=\"middle\">&nbsp;</td></tr>");
								break;
							}
						}
					}

					if (!bDisplayed) {
						//out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&set=" + setmet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + setmet.getSetMetName() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\">-</td><td headers=\"\"  align=\"middle\"><b>" + ETSUtils.dateDiff(setmet.getMeetingDate(),setmet.getLastTimestamp()) + "<b/></td></tr>");
						out.println("<tr style=\"background-color: " + sRowColor + "\"><td headers=\"\"  width=\"16\"  height=\"18\">&nbsp;</td><td headers=\"\"  width=\"250\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&set=" + setmet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" class=\"fbox\">" + setmet.getSetMetName() + "</a></td><td headers=\"\"  width=\"100\" align=\"middle\">-</td><td headers=\"\"  align=\"middle\">&nbsp;</td></tr>");
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
	private void displayInterviewTab() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

		String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		//printGreyDottedLine(out);

		String sStep = getCurrentState(SetMet.getSetMetID());

		out.println("<form name=\"SetMet\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"set\" value=\"" + SetMet.getSetMetID() + "\" />");
		out.println("<input type=\"hidden\" name=\"etsop\" value=\"approve\" />");
		out.println("<input type=\"hidden\" name=\"current_step\" value=\"" + sStep + "\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + this.params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + this.params.getTopCat() + "\" />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"70\" align=\"left\"><span class=\"small\"><a name=\"section_top\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=demo&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Demographics</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"  width=\"55\" align=\"left\"><span class=\"small\"><b>Interview</b></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"   align=\"left\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></span></td>");
		out.println("</tr></table>");

		//printGreyDottedLine(out);

		out.println("<br />");

		printActionStates(SetMet.getSetMetID());

		printGreyDottedLine(out);

		printQuestions(SetMet);

		printGreyDottedLine(out);

		out.println("<br />");
		printActionButtons(SetMet);

		out.println("<br /></td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

		// print all the questions and expectations...

		displaySetMetInterview(con,proj.getProjectId(),SetMet);

		out.println("<br />");
		printActionButtons(SetMet);

		out.println("</form>");

	}

	/**
	 *
	 */
	private void printActionButtons(ETSSetMet SetMet) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.es;

		try {

			boolean bDisplay = false;
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
				bDisplay = true;
			} else { 
				if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
					bDisplay = true;
				}
			}
				
			if (bDisplay) {
				
				String sStep = getCurrentState(SetMet.getSetMetID());
	
	
				if (sStep.trim().equalsIgnoreCase("")) {
					// interview has not been created..
	
					if (SetMet.getInterviewByIRID().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
						// primary contact is logged in.. provide the approve button...
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("</tr>");
						out.println("</table>");
	
					}
				}
	
	
				if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW)) {
					// interview has been created.. waiting for approval from client...
	
					if (SetMet.getClientIRID().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
						// client is logged in.. provide the approve button...
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("</tr>");
						out.println("</table>");
	
					}
				}
	
				if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
					// client has approved the interview.. waiting for approval from principal...
	
					if (SetMet.getSetMetBSE().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
						// client is logged in.. provide the approve button...
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("</tr>");
						out.println("</table>");
	
					}
				}
	
	//			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
	//				// ibm principal has approved the interview.. waiting for action plan creation...
	//
	//				if (SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
	//					// client is logged in.. provide the approve button...
	//					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
	//					out.println("<tr>");
	//					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
	//					out.println("</tr>");
	//					out.println("</table>");
	//
	//				}
	//			}
	//
	//			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
	//
	//				// action plan has been created. waiting for PM approval...
	//
	//				if (SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
	//					// client is logged in.. provide the approve button...
	//					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
	//					out.println("<tr>");
	//					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
	//					out.println("</tr>");
	//					out.println("</table>");
	//
	//				}
	//			}
	
	
//				if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
//					// action plan is done. person who created teh interview has to close the set met
//	
//					if (SetMet.getInterviewByIRID().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
//						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
//						out.println("<tr>");
//						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
//						out.println("</tr>");
//						out.println("</table>");
//					}
//				}
	
				if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
					// setmet is closed.. client has to do the final rating...
	
					if (SetMet.getClientIRID().trim().equalsIgnoreCase(es.gIR_USERN)  || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
						out.println("</tr>");
						out.println("</table>");
	
					}
				}
	
				//if (sStep.trim().equalsIgnoreCase(Defines.SETMET_FINAL_RATING)) {
				// do nothing as the setmet has been rated by client and it is closed.

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}




	}

	/**
	 *
	 */
	private String getCurrentState(String sSetMetID) throws SQLException, Exception {

		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		String sCurrentState = "";

		try {

			ETSSetMet SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);

			Vector vStates = SetMet.getSetMetStates();

			if (vStates != null) {
				// get the last to get the current state the set met is in...
				if (vStates.size() > 0) {
					ETSSetMetActionState state = (ETSSetMetActionState) vStates.elementAt(vStates.size()-1);
					sCurrentState = state.getStep();
				}
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return sCurrentState;


	}

	/**
	 *
	 */
	private void displayDemographics() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

		String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		//printGreyDottedLine(out);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"70\" align=\"left\"><span class=\"small\"><b>Demographics</b></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"  width=\"55\" align=\"left\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Interview</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"   align=\"left\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Action plan</a></span></td>");
		out.println("</tr></table>");

		//printGreyDottedLine(out);

		out.println("<br />");

		printGreyDottedLine(out);

		printInterviewHeader(SetMet.getSetMetID());

		printGreyDottedLine(out);

		displayInterviewDemographics(sSetMetID);
		
//		if (!SetMet.getState().equalsIgnoreCase(Defines.SETMET_CLOSED) && (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER))) {
//			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
//			out.println("<tr>");
//			out.println("<td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetEditServlet.wss?proj=" + proj.getProjectId() + "&setmet=" + SetMet.getSetMetID() + "','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit demographics</a></td>");
//			out.println("</tr></table>");
//			out.println("<br />");
//		}

		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");




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


		boolean bDisplay = false;
			
		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
			bDisplay = true;
		} else { 
			if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
				bDisplay = true;
			}
		}		
		
		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

		String sStep = getCurrentState(SetMet.getSetMetID());

		String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		//printGreyDottedLine(out);

		out.println("<form name=\"SetMet\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\">");

		out.println("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
		out.println("<input type=\"hidden\" name=\"set\" value=\"" + SetMet.getSetMetID() + "\" />");
		out.println("<input type=\"hidden\" name=\"etsop\" value=\"approve\" />");
		out.println("<input type=\"hidden\" name=\"current_step\" value=\"" + sStep + "\" />");
		out.println("<input type=\"hidden\" name=\"linkid\" value=\"" + this.params.getLinkId() + "\" />");
		out.println("<input type=\"hidden\" name=\"tc\" value=\"" + this.params.getTopCat() + "\" />");
		out.println("<input type=\"hidden\" name=\"from\" value=\"action\" />");


		if (sStep.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getSetMetPractice()) && bDisplay) {
			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr style=\"background-color: #eeeeee\">");
			out.println("<td headers=\"\"  align=\"left\"><b>Use the controls below to upload/delete draft versions of your action plan. When the document is completed, please select \"Submit\" to complete the action plan for this Set/Met.</b></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
//		} else if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getSetMetPractice()) && bDisplay) {
//			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
//			out.println("<tr style=\"background-color: #eeeeee\">");
//			out.println("<td headers=\"\"  align=\"left\"><b>Please review the action plan document. Once action plan document review is completed, please select \"Submit\" to get the action plan implemented.</b></td>");
//			out.println("</tr>");
//			out.println("</table>");
//			out.println("<br />");
		}

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"70\" align=\"left\"><span class=\"small\"><a name=\"section_top\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=demo&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Demographics</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"  width=\"55\" align=\"left\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Interview</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"   align=\"left\"><span class=\"small\"><b>Action plan</b></span></td>");
		out.println("</tr></table>");

		out.println("<br />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\">The action plan addresses expectations detailed in the interview.</td>");
		out.println("</tr></table>");

		out.println("<br />");

		printGreyDottedLine(out);

		printActionPlanDocuments(SetMet.getSetMetID());

		out.println("<br />");

		printGreyDottedLine(out);
		out.println("<br />");

		if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) || sStep.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
			
			if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {
			
				// removed set met team members in 5.2.1 
				//boolean bIsSetMetMember = ETSSetMetDAO.checkIfUserMemberOfSetMet(con,proj.getProjectId(),SetMet.getSetMetID(),es.gIR_USERN);
	
				//if (bIsSetMetMember || SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
				if (SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
	
					if (bDisplay) {
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr>");
						out.println("<tr>");
						out.println("<td headers=\"\"  colspan=\"5\" align=\"left\" valign=\"top\"><b>Note:</b> If you upload a new action plan, it would replace the existing action plan.</td>");
						out.println("</tr>");
						out.println("</table>");
		
						out.println("<br />");
		
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr>");
						out.println("<td headers=\"\" width=\"16\" align=\"center\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addactionplan&action=addactionplan&setmet=" + SetMet.getSetMetID() + "&cc="+params.getTopCat()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Upload new action plan\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=addactionplan&action=addactionplan&setmet=" + SetMet.getSetMetID() + "&cc="+params.getTopCat()+"\" class=\"fbox\">Upload new action plan</a></td>");
						out.println("<td headers=\"\" width=\"16\" align=\"center\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=delactionplan&action=delactionplan&set=" + SetMet.getSetMetID() + "&cc="+params.getTopCat()+"\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Delete action plan\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&etsop=delactionplan&action=delactionplan&set=" + SetMet.getSetMetID() + "&cc="+params.getTopCat()+"\" class=\"fbox\">Delete action plan</a></td>");
						out.println("</tr></table>");
					}
				}
			}
		}

		if (sStep.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
			// ibm principal has approved the interview.. waiting for action plan creation...

			if (SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
				// client is logged in.. provide the approve button...
				if (bDisplay) {
					out.println("<br /><br />");
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
					out.println("</tr>");
					out.println("</table>");
				}
			}
		}

		if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {

			// action plan has been created. waiting for PM approval...

			if (SetMet.getSetMetPractice().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
				// client is logged in.. provide the approve button...
				if (bDisplay) {
					out.println("<br /><br />");
					out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
					out.println("</tr>");
					out.println("</table>");
				}
			}
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

		out.println("</form>");



	}

		/**
		 *
		 */
		private void displayActionPlanDealTracker() throws SQLException, Exception {
	
			PrintWriter out = this.params.getWriter();
			HttpServletRequest request = this.params.getRequest();
			Connection con = this.params.getConnection();
			ETSProj proj = this.params.getETSProj();
			EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
	
	
			// get the setmet id..
	
			String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));
	
			String sPrinterFriendly = ETSUtils.checkNull(request.getParameter("skip"));
			if (sPrinterFriendly == null || sPrinterFriendly.trim().equalsIgnoreCase("")) {
				sPrinterFriendly = "N";
			} else {
				sPrinterFriendly = sPrinterFriendly.trim();
			}	
	
			ETSSetMet SetMet = new ETSSetMet();
	
			if (sSetMetID.trim().equals("")) {
				// get the most current one by meeting date
				SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
			} else {
				SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
			}
	
			out.println(this.header);
	
			String sOp = ETSUtils.checkNull(request.getParameter("etsop"));
	
			String sStep = getCurrentState(SetMet.getSetMetID());
			
			String sPM = SetMet.getSetMetPractice();
	
			String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID(),sPrinterFriendly);
	
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
	
			boolean projArchieved = false;
			boolean projDeleted = false;
			
			boolean bPlanOwner = false;
			boolean bWorkspaceOwner = false;
			boolean bAdmin = false;

			if (proj.getProject_status().equalsIgnoreCase("A")) {
				projArchieved = true;
			}

			if (proj.getProject_status().equalsIgnoreCase("D")) {
				projDeleted = true;
			}

			if (sPM.equalsIgnoreCase(es.gIR_USERN)) {
				bPlanOwner = true;
			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
				bWorkspaceOwner = true;
			}

//			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_MANAGER)) {
//				bWorkspaceManager = true;
//			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.ETS_ADMIN)) {
				bAdmin = true;
			}
	
	
			boolean bPlanEditable = false;
			boolean bShowCloseLink = false;
	
			if (!projArchieved && !projDeleted && sStep.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && SetMet.getState().equalsIgnoreCase(Defines.SETMET_OPEN)) {
				bPlanEditable = true;
			}		
		
			if (!projArchieved && !projDeleted && sStep.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && (bAdmin || bWorkspaceOwner || bPlanOwner)) {
				bShowCloseLink = true;
			}			
	
			ETSDealTracker tracker = new ETSDealTracker(params,SetMet.getSetMetID(),SetMet.getSetMetName(),bPlanEditable,bShowCloseLink,"Action plan");
			tracker.ETSTrackerHandler();		

			out.println("<br />");
			printGreyDottedLine(out);
			out.println("<br />");
			
			if (!sPrinterFriendly.trim().equalsIgnoreCase("Y")) {
				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\" >Back to SetMet reviews</a></td>");
				out.println("</tr>");
				out.println("</table>");
			}
	
	
		}

	/**
	 *
	 */
	private void displayUploadPlan() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

//
//		boolean bAdmin = false;
//		
//		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
//			bAdmin = true;
//		}
//		
//		if (!bAdmin) {
//			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_EXECUTIVE)) {
//				bAdmin = true;
//			}
//		}	

		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

		String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		//printGreyDottedLine(out);

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"70\" align=\"left\"><span class=\"small\"><a name=\"section_top\" href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=demo&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Demographics</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"  width=\"55\" align=\"left\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + this.params.getLinkId() + "\">Interview</a></span></td>");
		out.println("<td headers=\"\"  width=\"5\" align=\"left\"><span class=\"small\">&nbsp;|&nbsp;</span></td>");
		out.println("<td headers=\"\"   align=\"left\"><span class=\"small\"><b>Action plan</b></span></td>");
		out.println("</tr></table>");

		out.println("<br />");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  align=\"left\">The action plan addresses expectations detailed in the interview.</td>");
		out.println("</tr></table>");

		out.println("<br />");

		ETSDocumentManager dm = new ETSDocumentManager(params);
		dm.ETSDocumentHandler();

		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");




	}

	/**
	 *
	 */
	private void displayManageWorkspace() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		// get the setmet id..

		String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

		ETSSetMet SetMet = new ETSSetMet();

		if (sSetMetID.trim().equals("")) {
			// get the most current one by meeting date
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
		} else {
			SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
		}

		out.println(this.header);

		String sOp = ETSUtils.checkNull(request.getParameter("etsop"));

		String[] header = getHeader(sOp, SetMet.getSetMetName(),SetMet.getSetMetID());

		out.println(header[0]);

		ETSManageWorkspaceHandler workspace = new ETSManageWorkspaceHandler(params,es);
		workspace.handleRequest();

		out.println("</td>");

		//gutter between content and right column
		out.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
		// Right column start
		out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
		ETSClientCareContact contact = new ETSClientCareContact(con,proj.getProjectId(), request);
		contact.printContactBox(out);
		out.println("</td></tr>");
		out.println("</table>");

	}

	/**
	 * @param string
	 */
	private void printActionPlanDocuments(String sSetMetID) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		try {

			boolean bPrinted = false;

			Vector vSetMetDocs = ETSDatabaseManager.getDocsForSetMet(con,proj.getProjectId(),sSetMetID);

			if (vSetMetDocs != null) {

				for (int i = 0; i < vSetMetDocs.size(); i++) {

					if (i == 0) {
						bPrinted = true;
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
						out.println("<tr>");
						out.println("<td headers=\"\"  width=\"20\" align=\"left\">&nbsp;</td>");
						out.println("<td headers=\"\"  width=\"170\" align=\"left\"><span class=\"small\"><b>Name</b></span></td>");
						out.println("<td headers=\"\"  width=\"80\" align=\"left\"><span class=\"small\"><b>Posted</b></span></td>");
						out.println("<td headers=\"\"  width=\"50\" align=\"left\"><span class=\"small\"><b>Type</b></span></td>");
						out.println("<td headers=\"\"  align=\"left\"><span class=\"small\"><b>Author</b></span></td>");
						out.println("</tr>");
					}

					ETSDoc doc = (ETSDoc) vSetMetDocs.elementAt(i);

					//doc.getFileUpdateDate();

					java.util.Date date = new java.util.Date(doc.getFileUpdateDate());
					String sDocDate = df.format(date);

					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  width=\"20\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"\" border=\"0\" /></td>");
					out.println("<td headers=\"\"  width=\"170\" align=\"left\" valign=\"top\"><label for=\"label_doc" + i + "\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + doc.getFileName() +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000\" class=\"fbox\" target=\"new\">" + doc.getName() + "</a></label><br /><span class=\"small\">" + doc.getDescription() + "</span></td>");
					out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">" + sDocDate + "</span></td>");
					out.println("<td headers=\"\"  width=\"50\" align=\"left\" valign=\"top\"><span class=\"small\">" + doc.getFileType() + "</span></td>");
					out.println("<td headers=\"\"  align=\"left\" valign=\"top\"><span class=\"small\">" + ETSUtils.getUsersName(con,doc.getUserId()) + "</span></td>");
					out.println("</tr>");

					out.println("<tr >");
					out.println("<td headers=\"\"  width=\"20\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + doc.getFileName() +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000&download=Y\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "dn.gif\" width=\"16\" height=\"16\" alt=\"Download action plan\" border=\"0\" /></a></td>");
					out.println("<td headers=\"\"  colspan=\"4\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + doc.getFileName() +"?projid=" + proj.getProjectId() + "&docid=" + doc.getId() + "&linkid=120000&download=Y\" class=\"fbox\">Download action plan</a></td>");
					out.println("</tr>");

				}


			}

			if (bPrinted) {
				out.println("</table>");
			} else {
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  align=\"left\"><br /><b>Action plan is not available yet.</b></td>");
				out.println("</tr>");
				out.println("</table>");
			}



		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}


	}

	/**
	 * @param con
	 * @param string
	 * @param sSetMetID
	 */
	private void displayInterviewDemographics(String sSetMetID) throws SQLException, Exception {

		Connection con = this.params.getConnection();
		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();


		try {

			ETSSetMet setmet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);

			String sPrimaryContactID = ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId());

			if (setmet != null) {

				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"small\"><b>" + setmet.getSetMetName() + "</b></span></td>");
				out.println("</tr>");

				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  height=\"18\" width=\"200\" align=\"left\"><b>Client care advocate:</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.getUsersName(con,sPrimaryContactID) + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" width=\"200\" align=\"left\"><b>Principal:</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.getUsersName(con,setmet.getSetMetBSE()) + "</td>");
				out.println("</tr>");


				out.println("<tr style=\"background-color: #eeeeee\">");
				out.println("<td headers=\"\"  height=\"18\" width=\"200\" align=\"left\"><b>Program manager(s):</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.getUsersName(con,setmet.getSetMetPractice()) + "</td>");
				out.println("</tr>");

				// removed in release 5.2.1
				// get the workspace members...
//				Vector members = ETSSetMetDAO.getSetMetTeamMembers(con,proj.getProjectId(),sSetMetID);
//
//				boolean bMemberDisplayed = false;
//				
//				for (int i = 0; i < members.size(); i++) {
//
//					String sUserID = (String) members.elementAt(i); 
//
//					if (!bMemberDisplayed) {
//						out.println("<tr>");
//						out.println("<td headers=\"\"  height=\"18\" width=\"200\" align=\"left\"><b>Team member(s):</b></td>");
//						out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.getUsersName(con,sUserID) + "</td>");
//						out.println("</tr>");
//					} else {
//						out.println("<tr>");
//						out.println("<td headers=\"\"  height=\"18\" width=\"200\" align=\"left\">&nbsp;</td>");
//						out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.getUsersName(con,sUserID) + "</td>");
//						out.println("</tr>");
//					}
//
//					bMemberDisplayed = true;
//				}

//				// spacer
//				out.println("<tr>");
//				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
//				out.println("</tr>");


				out.println("</table>");
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}


	}

	/**
	 * @param string
	 */
	private void printInterviewHeader(String sSetMetID) throws SQLException, Exception {


		Connection con = this.params.getConnection();
		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		try {

			ETSSetMet setmet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);

			if (setmet != null) {

				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><span class=\"small\"><b>Interview information</b></span></td>");
				out.println("</tr>");

				// spacer
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" width=\"100\" align=\"left\"><b>Interview date:</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\">" + ETSUtils.formatDate(setmet.getMeetingDate()) + "</td>");
				out.println("</tr>");


				if (!setmet.getClientIRID().equalsIgnoreCase("")) {
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" align=\"left\"><b>Client:</b></td>");
					out.println("<td headers=\"\"  align=\"left\">" + ETSUtils.getUsersName(con,setmet.getClientIRID()) + "</td>");
					out.println("</tr>");
				} else {
					out.println("<tr>");
					out.println("<td headers=\"\"  width=\"100\" align=\"left\"><b>Client:</b></td>");
					out.println("<td headers=\"\"  align=\"left\">" + setmet.getClientName() + "</td>");
					out.println("</tr>");
				}

				// spacer
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");


				out.println("</table>");
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}



	}

	/**
	 * @param con
	 * @param string
	 * @param SetMet.getSetMetID()
	 */
	private void displaySetMetInterview(Connection con, String sProjectID, ETSSetMet SetMet) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();

		try {
			
			NumberFormat format = new DecimalFormat("0");

			boolean bDisplay = false;
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
				bDisplay = true;
			} else { 
				if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
					bDisplay = true;
				}
			}
			
			Vector vQuestions = ETSSetMetDAO.getQuestions(con);
			Vector vExp = ETSSetMetDAO.getSetMetExpectations(con,sProjectID,SetMet.getSetMetID());

			Vector vExpect = ETSSetMetDAO.getExpectationCode(con);

			String sState = SetMet.getState();

			String sStep = getCurrentState(SetMet.getSetMetID());
			boolean bEditable = false;

			String sPrincipal = SetMet.getSetMetBSE();
			String sPM = SetMet.getSetMetPractice();
			String sInterviewBy = SetMet.getInterviewByIRID();

			if (sStep.trim().equalsIgnoreCase("")) {
				// interview not completed yet
				if (es.gIR_USERN.trim().equalsIgnoreCase(sInterviewBy) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
					bEditable = true;
				}
			}


			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW)) {
				// interview has been created.. waiting for approval from client...
				if (SetMet.getClientIRID().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
					bEditable = true;
				}
			}

			boolean bPrincipal = false;

			if (sPrincipal.trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
				bPrincipal = true;
			}


			boolean bFinal = false;
			//if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLOSE)) {
			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN)) {
				// final step.. allow edit of ratings..
				if (SetMet.getClientIRID().trim().equalsIgnoreCase(es.gIR_USERN) || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)) {
					bFinal = true;
				}
			}

			// print the main questions here...

			for (int i = 0; i < vQuestions.size(); i++) {

				ETSSetMetQuestion question = (ETSSetMetQuestion) vQuestions.elementAt(i);

				if (question.getQuestionType().trim().equalsIgnoreCase("M")) {

					out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\">");
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\"><a name=\"question_" + String.valueOf(i+1) + "\"><b>" + String.valueOf(i+1) + ".</b></td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\"><b>" + question.getQuestionDesc() + "</b><br /><br /></td>");
						out.println("</tr>");
						out.println("</table>");
					if (bEditable && bDisplay) {
						out.println("<input type=\"hidden\" name=\"rating_editable\" value=\"Y\" />");
					}
					if (bFinal && bDisplay) {
						out.println("<input type=\"hidden\" name=\"final_rating_editable\" value=\"Y\" />");
					}
					out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
					out.println("<td headers=\"\" width=\"150\">&nbsp;</td>");
					out.println("</tr></table>");

					int iCount = 1;
					for (int j = 0; j < vExp.size(); j++) {

						ETSSetMetExpectation exp = (ETSSetMetExpectation) vExp.elementAt(j);

						if (exp.getQuestionID() == question.getQuestionID()) {

							out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"443\">");
								out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

								if (iCount > 1) {
									out.println("<tr >");
									out.println("<td headers=\"\" colspan=\"2\" >");
									printGreyDottedLine(out);
									out.println("</td>");
									out.println("</tr>");
								}

								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" /></td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Client comments: " + String.valueOf(iCount) + "</td>");
								out.println("</tr>");
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >" + exp.getExpectDesc() + "</td>");
								out.println("</tr>");
								// spacer
								out.println("<tr height=\"10\">");
								out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
								out.println("</tr>");

								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Expectation action (optional): </td>");
								out.println("</tr>");
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\">" + exp.getExpectAction() + "</td>");
								out.println("</tr>");

								// spacer
								out.println("<tr height=\"10\">");
								out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
								out.println("</tr>");

							if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {	
							//if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
								// display expectation code only to internals...
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Category code: </td>");
								out.println("</tr>");
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");

								if (vExpect != null) {
									for (int k = 0; k < vExpect.size(); k++) {
										ETSSetMetExpectCode code = (ETSSetMetExpectCode) vExpect.elementAt(k);
										if (code.getExpectID() == exp.getExpectID()) {
											out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\">" + code.getExpectDesc() + "</td>");
											break;
										}
									}
								}

								out.println("</tr>");

								// spacer
								out.println("<tr height=\"10\">");
								out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
								out.println("</tr>");
							}

								out.println("</table>");
							//printGreyDottedLine(out);
							out.println("</td>");
							out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"150\" valign=\"top\">");

							// print the rating and add/edit and delete buttons...

							out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr>");
							out.println("<td headers=\"\" width=\"150\" colspan=\"2\">");

							if (bEditable && bDisplay) {
								out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
								out.println("<tr>");
								out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" class=\"small\" style=\"color:#666666\">How would you rate us on this item?</td>");
								out.println("</tr>");
								out.println("<tr>");
								out.println("<td headers=\"\" width=\"80\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><b>Rating:</b></td>");
								out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + format.format(exp.getExpectRating()) + "</td>");
								out.println("</tr>");
								out.println("</table>");
							} else {
								out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
								out.println("<tr>");
								out.println("<td headers=\"\" width=\"100\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><b>Original rating:</b></td>");
								out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + format.format(exp.getExpectRating()) + "</td>");
								out.println("</tr>");
								if (SetMet.getState().trim().equalsIgnoreCase(Defines.SETMET_CLOSED)) {
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"100\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><b>Final rating:</b></td>");
									out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + format.format(exp.getFinalRating()) + "</td>");
									out.println("</tr>");
								}
								out.println("</table>");
								if (bFinal && bDisplay) {
									out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"100\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><label for=\"final_rate\"><b>Final rating:</label></b></td>");
									out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\">");
									out.println("<select name=\"final_rating" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" id=\"rating_rate\" style=\"width:50px\" width=\"50px\">");

//									int iTempRate = -1;
//
//									if (exp.getFinalRating() == -1) {
//										iTempRate = exp.getExpectRating();
//									} else {
//										iTempRate = exp.getFinalRating();
//									}

									double dTempRate = -1;
									
									if (exp.getFinalRating() == -1) {
										dTempRate = exp.getExpectRating();
									} else {
										dTempRate = exp.getFinalRating();
									}

									double dCount = 0;
									
									while (dCount <= 10) {
										if (dTempRate == dCount) {
											out.println("<option value=\"" + String.valueOf(dCount) + "\" selected=\"selected\" >" + String.valueOf(format.format(dCount)) + "</option>");
										} else {
											out.println("<option value=\"" + String.valueOf(dCount) + "\">" + String.valueOf(format.format(dCount)) + "</option>");
										}
										dCount = dCount + 1;									
									}

//									for (int iRate = 0; iRate <= 10; iRate++) {
//
//										if (iTempRate == iRate) {
//											out.println("<option value=\"" + String.valueOf(iRate) + "\" selected=\"selected\" >" + String.valueOf(iRate) + "</option>");
//										} else {
//											out.println("<option value=\"" + String.valueOf(iRate) + "\">" + String.valueOf(iRate) + "</option>");
//										}
//									}
									out.println("</select>");
									out.println("</td>");
									out.println("</tr>");
									out.println("</table>");
								}

							}
							out.println("</td>");
							out.println("</tr>");

							if (bEditable && bDisplay) {
								//out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add</a></td></tr>");
								out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit</a></td></tr>");
								out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Delete</a></td></tr>");
								//out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetCommentsServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetCommentsServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetCommentsServlet.wss?proj=" + sProjectID + "&op=delete&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add/Edit comments</a></td></tr>");
							}
							out.println("</table>");

							out.println("</td></tr>");
							out.println("</table>");


							iCount = iCount + 1;

						}

					}


					if (bEditable && bDisplay) {
						out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						if (iCount > 1) {
							out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add additional expectations/actions</a></td></tr>");
						} else {
							out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=m','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add expectations/actions</a></td></tr>");
						}
						
						out.println("</table>");
					}

					out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\" align=\"left\">");
					printGreyDottedLine(out);
					out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\"><table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"middle\" valign=\"top\" ><img src=\"" + Defines.ICON_ROOT + "u.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\"><a href=\"#main\" class=\"fbox\">Back to top</a></td></tr></table></td></tr>");
					out.println("</table>");

				}

			}



			// print the general questions here...
			for (int i = 0; i < vQuestions.size(); i++) {

				ETSSetMetQuestion question = (ETSSetMetQuestion) vQuestions.elementAt(i);

				if (question.getQuestionType().trim().equalsIgnoreCase("G")) {

					boolean bPrinted = false;

					out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\">");
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\"><a name=\"question_" + String.valueOf(i+1) + "\"><b>" + String.valueOf(i+1) + ".</b></td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\"><b>" + question.getQuestionDesc() + "</b><br /><br /></td>");
						out.println("</tr>");
						out.println("</table>");
					out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
					out.println("<td headers=\"\" width=\"150\">&nbsp;</td>");
					out.println("</tr></table>");

					for (int j = 0; j < vExp.size(); j++) {

						ETSSetMetExpectation exp = (ETSSetMetExpectation) vExp.elementAt(j);

						if (exp.getQuestionID() == question.getQuestionID()) {

							bPrinted = true;

							out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"443\" valign=\"top\">");

							out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

							out.println("<tr >");
							out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" /></td>");
							out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
							out.println("</tr>");

							out.println("<tr >");
							out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >" + exp.getComments() + "</td>");
							out.println("</tr>");
							// spacer
							out.println("<tr height=\"10\">");
							out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
							out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
							out.println("</tr>");


							out.println("</table>");
							out.println("</td>");
							out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"150\" valign=\"top\">");

							// print the rating and add/edit and delete buttons...

							out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr>");
							out.println("<td headers=\"\" width=\"150\" colspan=\"2\">");

							if (bEditable && bDisplay) {
								out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
								out.println("<tr>");
								out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" class=\"small\" style=\"color:#666666\">How would you rate us on this item?</td>");
								out.println("</tr>");
								out.println("<tr>");
								out.println("<td headers=\"\" width=\"80\" height=\"21\"  style=\"background-color: #eeeeee\" align=\"left\"><b>Rating:</b></td>");
								out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + exp.getExpectRating() + "</td>");
								out.println("</tr>");
								out.println("</table>");
							} else {
								out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
								out.println("<tr>");
								out.println("<td headers=\"\" width=\"100\" height=\"21\"  style=\"background-color: #eeeeee\" align=\"left\"><b>Original rating:</b></td>");
								out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + exp.getExpectRating() + "</td>");
								out.println("</tr>");
								if (SetMet.getState().trim().equalsIgnoreCase(Defines.SETMET_CLOSED)) {
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"100\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><b>Final rating:</b></td>");
									out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\"><b>" + exp.getFinalRating() + "</td>");
									out.println("</tr>");
								}

								out.println("</table>");
								if (bFinal && bDisplay) {
									out.println("<table width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"100\" height=\"21\" style=\"background-color: #eeeeee\" align=\"left\"><label for=\"final_rate\"><b>Final rating:</label></b></td>");
									out.println("<td headers=\"\" style=\"background-color: #cccccc\"  align=\"middle\">");
									out.println("<select name=\"final_rating" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" id=\"rating_rate\" style=\"width:50px\" width=\"50px\">");

//									int iTempRate = -1;
//
//									if (exp.getFinalRating() == -1) {
//										iTempRate = exp.getExpectRating();
//									} else {
//										iTempRate = exp.getFinalRating();
//									}
//
//									for (int iRate = 0; iRate <= 10; iRate++) {
//
//										if (iTempRate == iRate) {
//											out.println("<option value=\"" + String.valueOf(iRate) + "\" selected=\"selected\" >" + String.valueOf(iRate) + "</option>");
//										} else {
//											out.println("<option value=\"" + String.valueOf(iRate) + "\">" + String.valueOf(iRate) + "</option>");
//										}
//									}

									double dTempRate = -1;
									
									if (exp.getFinalRating() == -1) {
										dTempRate = exp.getExpectRating();
									} else {
										dTempRate = exp.getFinalRating();
									}

									double dCount = 0;
									
									while (dCount <= 10) {
										if (dTempRate == dCount) {
											out.println("<option value=\"" + String.valueOf(dCount) + "\" selected=\"selected\" >" + String.valueOf(dCount) + "</option>");
										} else {
											out.println("<option value=\"" + String.valueOf(dCount) + "\">" + String.valueOf(dCount) + "</option>");
										}
										dCount = dCount + 0.5;									
									}

									out.println("</select>");
									out.println("</td>");
									out.println("</tr>");
									out.println("</table>");
								}
							}
							out.println("</select>");
							out.println("</td>");
							out.println("</tr>");

							// only edit option for general questions...
							if (bEditable && bDisplay) {
								out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=g\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=g','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=g','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit</a></td></tr>");
							}

							out.println("</table>");

							out.println("</td></tr>");
							out.println("</table>");

						}

					}

					if (!bPrinted) {

//						ETSSetMetExpectation exp = new ETSSetMetExpectation();
//
//						exp.setComments("None.");
//						exp.setExpectAction("");
//						exp.setExpectDesc("");
//						exp.setExpectID(0);
//						exp.setExpectRating(0);
//						exp.setFinalRating(-1);
//						exp.setLastTimestamp(new Timestamp(System.currentTimeMillis()));
//						exp.setLastUserID(es.gIR_USERN);
//						exp.setProjectID(proj.getProjectId());
//						exp.setQuestionID(question.getQuestionID());
//						exp.setSeqNo(1);
//						exp.setSetMetID(SetMet.getSetMetID());
//
//						ETSSetMetDAO.insertSetMetExpectation(con,exp);

						out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr><td headers=\"\" width=\"443\">");

						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

						out.println("<tr >");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-0\" /></td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
						out.println("</tr>");

						out.println("<tr >");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >None.</td>");
						out.println("</tr>");
						// spacer
						out.println("<tr height=\"10\">");
						out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
						out.println("</tr>");


						out.println("</table>");
						out.println("</td>");
						out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
						out.println("</td></tr>");
						out.println("</table>");

						if (bEditable && bDisplay) {
							out.println("<br />");
							out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=g\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=g','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=g','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add comments</a></td></tr>");
							out.println("</table>");
						}

					}

					out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\" align=\"left\">");
					printGreyDottedLine(out);
					out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\"><table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"middle\" valign=\"top\" ><img src=\"" + Defines.ICON_ROOT + "u.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\"><a href=\"#main\"  class=\"fbox\">Back to top</a></td></tr></table></td></tr>");
					out.println("</table>");


				}
			}

			// print the client comments here...
			for (int i = 0; i < vQuestions.size(); i++) {

				ETSSetMetQuestion question = (ETSSetMetQuestion) vQuestions.elementAt(i);

				if (question.getQuestionType().trim().equalsIgnoreCase("C")) {

					boolean bPrinted = false;

					out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\">");
						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\"><a name=\"question_" + String.valueOf(i+1) + "\"><b>" + String.valueOf(i+1) + ".</b></td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\"><b>" + question.getQuestionDesc() + "</b><br /><br /></td>");
						out.println("</tr>");
						out.println("</table>");
					out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
					out.println("<td headers=\"\" width=\"150\">&nbsp;</td>");
					out.println("</tr></table>");

					for (int j = 0; j < vExp.size(); j++) {

						ETSSetMetExpectation exp = (ETSSetMetExpectation) vExp.elementAt(j);

						if (exp.getQuestionID() == question.getQuestionID()) {

							bPrinted = true;

							out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"443\">");

							out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

							out.println("<tr >");
							out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" /></td>");
							out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
							out.println("</tr>");

							out.println("<tr >");
							out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >" + exp.getComments() + "</td>");
							out.println("</tr>");
							// spacer
							out.println("<tr height=\"10\">");
							out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
							out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
							out.println("</tr>");

							out.println("</table>");

							out.println("</td>");
							out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
							// only edit option for general questions...
							if (bEditable && bDisplay) {
								out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=c\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit</a></td></tr></table>");
							}
							out.println("</td>");
							out.println("</tr>");
							out.println("</table>");

						}

					}

					if (!bPrinted) {

						out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr><td headers=\"\" width=\"443\">");

						out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

						out.println("<tr >");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-0\" /></td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
						out.println("</tr>");

						out.println("<tr >");
						out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >None.</td>");
						out.println("</tr>");
						// spacer
						out.println("<tr height=\"10\">");
						out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
						out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
						out.println("</tr>");

						out.println("</table>");

						out.println("</td>");
						out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
						out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
						out.println("</td></tr>");
						out.println("</table>");

						if (bEditable && bDisplay) {
							out.println("<br />");
							out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add comments</a></td></tr>");
							out.println("</table>");
						}

					}

					out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr><td headers=\"\" width=\"443\" align=\"left\">");
					printGreyDottedLine(out);
					out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\"><table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"middle\" valign=\"top\" ><img src=\"" + Defines.ICON_ROOT + "u.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\"><a href=\"#main\"  class=\"fbox\">Back to top</a></td></tr></table></td></tr>");
					out.println("</table>");



				}
			}

			// DONT display the principal's comments to client
			if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {
				
				// also check to see if the logged in person is internal. if yes, then display
				if (es.gDECAFTYPE.equalsIgnoreCase("I")) {
				
					// print the principal comments here...
					for (int i = 0; i < vQuestions.size(); i++) {
	
						ETSSetMetQuestion question = (ETSSetMetQuestion) vQuestions.elementAt(i);
	
						if (question.getQuestionType().trim().equalsIgnoreCase("P")) {
	
							boolean bPrinted = false;
	
							out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"443\">");
								
								
								String sCurrentState = getCurrentState(SetMet.getSetMetID());
								
								if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPrincipal)) {
									// display in yellow color for principals....

									out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr style=\"background-color: #ffff99\" >");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\"><a name=\"question_" + String.valueOf(i+1) + "\"><b>" + String.valueOf(i+1) + ".</b></td>");
									out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\"><b>" + question.getQuestionDesc() + "</b><br /><br /></td>");
									out.println("</tr>");
									out.println("</table>");
									
									out.println("<br />");
									
									out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td><td headers=\"\" align=\"left\" valign=\"top\">Enter your comments on client interview and approve it. These comments are not viewable by client.</td>");
									out.println("</tr>");
									out.println("</table>");
									
									out.println("<br />");
									
								} else {
									out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\"><a name=\"question_" + String.valueOf(i+1) + "\"><b>" + String.valueOf(i+1) + ".</b></td>");
									out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\"><b>" + question.getQuestionDesc() + "</b><br /><br /></td>");
									out.println("</tr>");
									out.println("</table>");
									
									//out.println("<br />");
									
									out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td><td headers=\"\" align=\"left\" valign=\"top\">These comments are not viewable by client.</td>");
									out.println("</tr>");
									out.println("</table>");
									
									out.println("<br />");
									
								}
							out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
							out.println("<td headers=\"\" width=\"150\">&nbsp;</td>");
							out.println("</tr></table>");
	
							for (int j = 0; j < vExp.size(); j++) {
	
								ETSSetMetExpectation exp = (ETSSetMetExpectation) vExp.elementAt(j);
	
								if (exp.getQuestionID() == question.getQuestionID()) {
	
									bPrinted = true;
	
									out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr><td headers=\"\" width=\"443\">");
	
									out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
	
									out.println("<tr >");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-" + exp.getSeqNo() + "\" /></td>");
									out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
									out.println("</tr>");
	
									out.println("<tr >");
									out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
									out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >" + exp.getComments() + "</td>");
									out.println("</tr>");
									// spacer
									out.println("<tr height=\"10\">");
									out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
									out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
									out.println("</tr>");
	
									out.println("</table>");
	
									out.println("</td>");
									out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
									out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
	
									// only edit option for general questions...
									if (bPrincipal && sState.trim().equalsIgnoreCase(Defines.SETMET_OPEN) && bDisplay) {
										out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=p\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=p','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=edit&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&seqno=" + exp.getSeqNo() + "&qt=p','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit</a></td></tr></table>");
									}
	
									out.println("</td></tr>");
									out.println("</table>");
	
								}
	
							}
	
							if (!bPrinted) {
	
								out.println("<table summary=\"\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
								out.println("<tr><td headers=\"\" width=\"443\">");
	
								out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
	
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;<input type=\"hidden\" name=\"question_seqno\" value=\"" + question.getQuestionID() + "-0\" /></td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" class=\"small\" style=\"color:#666666\">Comments: </td>");
								out.println("</tr>");
	
								out.println("<tr >");
								out.println("<td headers=\"\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"428\" align=\"left\" valign=\"top\" >None.</td>");
								out.println("</tr>");
								// spacer
								out.println("<tr height=\"10\">");
								out.println("<td headers=\"\" height=\"10\" width=\"15\" align=\"left\" valign=\"top\">&nbsp;</td>");
								out.println("<td headers=\"\" height=\"10\" width=\"428\" align=\"left\" valign=\"top\" >&nbsp;</td>");
								out.println("</tr>");
	
								out.println("</table>");
	
								out.println("</td>");
								out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
								out.println("<td headers=\"\" width=\"150\" valign=\"top\">");
								out.println("</td></tr>");
								out.println("</table>");
	
								if (bPrincipal && sState.trim().equalsIgnoreCase(Defines.SETMET_OPEN) && bDisplay) {
									out.println("<br />");
									out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
									out.println("<tr><td headers=\"\" width=\"16\" valign=\"top\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" align=\"left\" class=\"fbox\"><a href=\"" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSetMetExpectServlet.wss?proj=" + sProjectID + "&op=add&setmet=" + SetMet.getSetMetID() + "&question=" + question.getQuestionID() + "&qt=c','SetMet','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Add comments</a></td></tr>");
									out.println("</table>");
								}
							}
	
							out.println("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
							out.println("<tr><td headers=\"\" width=\"443\" align=\"left\">");
							printGreyDottedLine(out);
							out.println("</td><td headers=\"\" width=\"7\">&nbsp;</td><td headers=\"\" width=\"150\"><table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"middle\" valign=\"top\" ><img src=\"" + Defines.ICON_ROOT + "u.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td><td headers=\"\" width=\"135\" align=\"left\"><a href=\"#main\"  class=\"fbox\">Back to top</a></td></tr></table></td></tr>");
							out.println("</table>");
	
						}
					}
				}
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}

	}

	/**
	 *
	 */
	private void printQuestions(ETSSetMet setmet) throws SQLException, Exception {

		Connection con = this.params.getConnection();
		PrintWriter out = this.params.getWriter();
		ETSProj proj = this.params.getETSProj();

		try {

			String sCurrentState = getCurrentState(setmet.getSetMetID());

			String sPrincipal = setmet.getSetMetBSE();

			Vector vQuestions = ETSSetMetDAO.getQuestions(con);

			if (vQuestions != null) {

				out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");


				for (int i = 0; i < vQuestions.size(); i++) {

					ETSSetMetQuestion ques = (ETSSetMetQuestion) vQuestions.elementAt(i);

					if (i == 0) {
						// spacer
						out.println("<tr>");
						out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
						out.println("</tr>");

					}

					if (ques.getQuestionType().equalsIgnoreCase("P")) {
						// display the principal's comments if only super workspace admin, workspace owner, workspace manager.
						// changed for 4.4.1
						if (!ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {
							// also check to see if the logged in person is internal. if yes, then display
							if (es.gDECAFTYPE.equalsIgnoreCase("I")) {
								
								if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(sPrincipal)) {
									// display in yellow color for principals....
									out.println("<tr style=\"background-color: #ffff99\">");
									out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"middle\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\"><img src=\"" + Defines.ICON_ROOT + "d.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
									out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"left\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + String.valueOf(i + 1) + ".</a></td>");
									out.println("<td headers=\"\" align=\"left\" height=\"19\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + ques.getQuestionDesc() + "</a></td>");
									out.println("</tr>");
								} else{
									out.println("<tr>");
									out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"middle\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\"><img src=\"" + Defines.ICON_ROOT + "d.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
									out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"left\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + String.valueOf(i + 1) + ".</a></td>");
									out.println("<td headers=\"\" align=\"left\" height=\"19\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + ques.getQuestionDesc() + "</a></td>");
									out.println("</tr>");
								}
								
							}
						}
					} else {
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"middle\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\"><img src=\"" + Defines.ICON_ROOT + "d.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
						out.println("<td headers=\"\" width=\"16\" height=\"19\" align=\"left\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + String.valueOf(i + 1) + ".</a></td>");
						out.println("<td headers=\"\" align=\"left\" height=\"19\" valign=\"top\"><a class=\"fbox\" href=\"#question_" + String.valueOf(i+1) + "\">" + ques.getQuestionDesc() + "</a></td>");
						out.println("</tr>");
					}

				}

				// spacer
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
				out.println("</tr>");

				out.println("</table>");
			}


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 *
	 */
	private void printActionStates(String sSetMetID) throws SQLException, Exception {


		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		try {
			
			boolean bDisplay = false;
			
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN)) {
				bDisplay = true;
			} else { 
				if (!proj.getProject_status().equalsIgnoreCase("A") && !proj.getProject_status().equalsIgnoreCase("D")) {
					bDisplay = true;
				}
			}
			
			if (bDisplay) {			
				
				// get the setmet id..
				ETSSetMet SetMet = new ETSSetMet();
	
				if (sSetMetID.trim().equals("")) {
					// get the most current one by meeting date
					SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
				} else {
					SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
				}
	
				String sCurrentState = getCurrentState(SetMet.getSetMetID());
	
				if (sCurrentState.trim().equalsIgnoreCase("") && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getInterviewByIRID())) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  align=\"left\"><b>For each question below, document the appropriate expectation/actions. Select \"Submit\" when you are ready to send this to your clients review.</b></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID())) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  align=\"left\"><b>For each question below, please review the appropriate expectation/actions for each question. Click on \"Edit\" to edit them. Once you have reviewed the details, please select \"Submit\".</b></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getSetMetBSE())) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  align=\"left\"><b>In step. 6, enter your comments on client interview and approve it.</b></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getSetMetPractice())) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  align=\"left\"><b>Once action plan is implemented, please select \"Submit\" to send this Set/Met to your client for final rating.</b></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				} else if (sCurrentState.trim().equalsIgnoreCase(Defines.SETMET_CLOSE) && es.gIR_USERN.trim().equalsIgnoreCase(SetMet.getClientIRID())) {
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
					out.println("<tr style=\"background-color: #eeeeee\">");
					out.println("<td headers=\"\"  align=\"left\"><b>Please select \"Submit\" once your final rating is done on this Set/Met.</b></td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<br />");
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private String[] getHeader(String sETSOp, String sSetMetName, String sSetMetID) throws Exception{

		String[] header = new String[]{};
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		try {
			if (sETSOp.equals("") || sETSOp.equals("confirmapprove")){
				header = new String[]{ETSUtils.getBookMarkString("Set/Met Reviews","#setmet", true)};
			} else if (sETSOp.equals("inter") || sETSOp.equals("demo") || sETSOp.equals("action") || sETSOp.equals("addactionplan") || sETSOp.equals("delactionplan") || sETSOp.equals("approve") || sETSOp.equals("closeap")) {
				header = new String[]{ETSUtils.getTitleString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&set=" + sSetMetID + "&linkid=" + this.params.getLinkId() + "\" ><span class\"small\"><b>Set/Met Reviews</b></span></a>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\"><b>" + sSetMetName + "</b></span>")};
			} else if (sETSOp.trim().equals("newsetmet") || sETSOp.trim().equals("createinterview") || sETSOp.trim().equals("fromteam")) {
				header = new String[]{ETSUtils.getBookMarkString("Add new Set/Met","", false)};
			} else if (sETSOp.trim().equals("manage_wspace")) {
				header = new String[]{ETSUtils.getBookMarkString("","", false)};
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return header;

	}

	private String[] getHeader(String sETSOp, String sSetMetName, String sSetMetID, String sPrinterFriendly) throws Exception{
	
		String[] header = new String[]{};
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
	
		try {
			if (sETSOp.equals("") || sETSOp.equals("confirmapprove")){
				header = new String[]{ETSUtils.getBookMarkString("Set/Met Reviews","#setmet", true)};
			} else if (sETSOp.equals("inter") || sETSOp.equals("demo") || sETSOp.equals("action") || sETSOp.equals("addactionplan") || sETSOp.equals("delactionplan") || sETSOp.equals("approve") || sETSOp.equals("closeap")) {
				if (sPrinterFriendly.trim().equalsIgnoreCase("Y")) {
					header = new String[]{ETSUtils.getTitleString("<span class\"small\"><b>Set/Met Reviews</b></span>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\"><b>" + sSetMetName + "</b></span>")}; 
				} else {
					header = new String[]{ETSUtils.getTitleString("<a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&set=" + sSetMetID + "&linkid=" + this.params.getLinkId() + "\" ><span class\"small\"><b>Set/Met Reviews</b></span></a>&nbsp;&nbsp;>&nbsp;&nbsp;<span class\"small\"><b>" + sSetMetName + "</b></span>")};
				}
				
			} else if (sETSOp.trim().equals("newsetmet") || sETSOp.trim().equals("createinterview") || sETSOp.trim().equals("fromteam")) {
				header = new String[]{ETSUtils.getBookMarkString("Add new Set/Met","", false)};
			} else if (sETSOp.trim().equals("manage_wspace")) {
				header = new String[]{ETSUtils.getBookMarkString("","", false)};
			}
	
	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return header;
	
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

	private static void showDate(PrintWriter out, String m, String d, String yy, String mname, String dname, String yname, String sId) {

		String mon[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		Calendar cal = Calendar.getInstance();
		int y = cal.get(Calendar.YEAR);

		out.println("<table summary=\"\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td headers=\"\"  align=\"left\" width=\"40\"><label for=\"month\">Month</label><br />");
		out.println("<select id=\"" + sId + " month\" class=\"iform\" name=\"" + mname + "\">");

		for (int k = 1; k < 13; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (m.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + mon[Integer.parseInt(qq) - 1] + "</option>");
		}

		out.println("</select></td><td headers=\"\"  width=\"30\" align=\"left\">&nbsp;&nbsp;<label for=\"day\">Day</label><br /><select id=\"" + sId + " day\" class=\"iform\" name=\"" + dname + "\">");
		for (int k = 1; k < 32; k++) {
			String qq = "" + k;
			if (k < 10)
				qq = "0" + qq;
			out.println("<option value=\"" + qq + "\"");
			if (d.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select></td><td headers=\"\"   align=\"left\">&nbsp;&nbsp;<label for=\"year\">Year</label><br /><select id=\"" + sId + " year\" class=\"iform\" name=\"" + yname + "\">");
		for (int k = 0; k < 20; k++) {
			String qq = "" + (y + k - 2);
			out.println("<option value=\"" + qq + "\"");
			if (yy.equals(qq))
				out.println(" selected=\"selected\" ");
			out.println(">" + qq + "</option>");
		}

		out.println("</select>");
		out.println("</td></tr></table>");

	}

	private static String displayInviteesAsSelect(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {

		StringBuffer out = new StringBuffer("");

		try {

			String sAvailable = "," + sInviteesList + ",";
			out.append("<select style=\"width:250px\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
			out.append("<option value=\"\" selected=\"selected\">Please select a user</option>");

			//Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,con);
			
			Vector vMembers = ETSWorkspaceDAO.getInternalUsersInWorkspace(con,sProjectId);

			if (vMembers != null && vMembers.size() > 0) {

				for (int i = 0; i < vMembers.size(); i++) {

					ETSUser user = (ETSUser) vMembers.elementAt(i);

					if (!ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.CLIENT,con) && !ETSDatabaseManager.hasProjectPriv(user.getUserId(),sProjectId,Defines.VISITOR,con)) {

						if (sAvailable.indexOf("," + user.getUserId().trim() + ",") >= 0)  {
							out.append("<option value=\"" + user.getUserId().trim() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						} else {
							out.append("<option value=\"" + user.getUserId().trim() + "\" >" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						}

					}
				}
			}

			out.append("</select>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

		return out.toString();

	}

	private static String displayClientAsSelect(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {
	
		StringBuffer out = new StringBuffer("");
	
		try {
	
			String sAvailable = "," + sInviteesList + ",";
			out.append("<select style=\"width:250px\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
			out.append("<option value=\"\" selected=\"selected\">Please select a client</option>");
	
			Vector vMembers = ETSDatabaseManager.getUsersByProjectPriv(sProjectId, Defines.CLIENT, con);
	
			if (vMembers != null && vMembers.size() > 0) {
	
				for (int i = 0; i < vMembers.size(); i++) {
	
					ETSUser user = (ETSUser) vMembers.elementAt(i);
					if (sAvailable.indexOf("," + user.getUserId().trim() + ",") >= 0)  {
						out.append("<option value=\"" + user.getUserId().trim() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
					} else {
						out.append("<option value=\"" + user.getUserId().trim() + "\" >" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
					}
				}
			}
	
			out.append("</select>");
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
		return out.toString();
	
	}

	private static String displayTeamMembers(Connection con, String sSelectName, String sLabelId, String sProjectId, String sInviteesList, String sLoggedInID) throws SQLException, Exception {
	
		StringBuffer out = new StringBuffer("");
	
		try {
	
			String sAvailable = "," + sInviteesList + ",";
			out.append("<select size=\"5\" style=\"width:250px\" multiple=\"multiple\" width=\"250px\" name=\"" + sSelectName + "\" id=\"" + sLabelId + "\" class=\"iform\">");
	
			//Vector vMembers = ETSDatabaseManager.getProjMembersWithOutPriv(sProjectId,Defines.CLIENT,false,con);
			
			Vector vMembers = ETSDatabaseManager.getProjMembers(sProjectId,con);

			if (vMembers != null && vMembers.size() > 0) {

				for (int i = 0; i < vMembers.size(); i++) {

					ETSUser user = (ETSUser) vMembers.elementAt(i);

					//if (!user.getUserId().equalsIgnoreCase(sLoggedInID)) {

						if (sAvailable.indexOf("," + user.getUserId().trim() + ",") >= 0)  {
							out.append("<option value=\"" + user.getUserId().trim() + "\" selected=\"selected\">" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						} else {
							out.append("<option value=\"" + user.getUserId().trim() + "\" >" + ETSUtils.getUsersName(con,user.getUserId().trim()) + "</option>");
						}

					//}
				}
			}
	
			out.append("</select>");
	
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
		return out.toString();
	
	}

	/**
	 *
	 */
	private String createInterview() throws SQLException, Exception {

		// approve the interview and based on the current state..

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		ETSProj proj = this.params.getETSProj();
		Connection con = this.params.getConnection();
		String sSetMetID =  "";

		try {

			String sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_month"));
			String sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_day"));
			String sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_year"));

			String SupressClient1 = ETSUtils.checkNull(this.params.getRequest().getParameter("supress_client1"));
			String SupressClient2 = ETSUtils.checkNull(this.params.getRequest().getParameter("supress_client2"));
			
			String sClientIRID = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_client"));
			String sClientName = ETSUtils.checkNull(this.params.getRequest().getParameter("clientname"));
			
			Timestamp timeStart = Timestamp.valueOf(sCalYear + "-" + sCalMonth + "-" + sCalDay + " 00:00:00.000000000");

			ETSSetMet setmet = new ETSSetMet();

			sSetMetID = getNewSetMetID();

			setmet.setClientIRID(sClientIRID);
			setmet.setInterviewByIRID(es.gIR_USERN);
			setmet.setLastTimestamp(new Timestamp(System.currentTimeMillis()));
			setmet.setMeetingDate(timeStart);
			setmet.setNextMeetingDate(new Timestamp(System.currentTimeMillis()));
			setmet.setProjectID(proj.getProjectId());
			setmet.setSetMetBSE(ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_principal"))); // store the principal here.
			setmet.setSetMetID(sSetMetID);
			setmet.setSetMetName(ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_title")));
			setmet.setSetMetPractice(ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_pm"))); // store the program manager here.
			setmet.setSetMetStates(null);
			setmet.setState("OPEN");
			setmet.setClientName(sClientName);
			
			String sSupress1 = "0";
			String sSupress2 = "0";
			String sSupress3 = "0";
			String sSupress4 = "0";
			String sSupress5 = "0";
			String sSupress6 = "0";
			
			if (SupressClient1.equalsIgnoreCase("Y")) {
				sSupress2 = "1";
			}

			if (SupressClient2.equalsIgnoreCase("Y")) {
				sSupress6 = "1";
			}
			
			setmet.setSupressFlags(sSupress1 + sSupress2 + sSupress3 + sSupress4 + sSupress5 + sSupress6);

			int iCount = ETSSetMetDAO.insertNewSetMetDemographics(con,setmet);
			
			// removed set met team members in 5.2.1
			//String sTeamMembers[] = this.params.getRequest().getParameterValues("setmet_team");
			//iCount = ETSSetMetDAO.insertSetMetTeamMembers(con,sSetMetID,proj.getProjectId(),sTeamMembers);
			
			String sPrincipalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_day"));
			String sPrincipalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_month"));
			String sPrincipalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_year"));

			String sActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_day"));
			String sActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_month"));
			String sActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_year"));

			String sActionImplementDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_day"));
			String sActionImplementMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_month"));
			String sActionImplementYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_implement_year"));

			String sAfterActionDay = ETSUtils.checkNull(this.params.getRequest().getParameter("after_day"));
			String sAfterActionMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("after_month"));
			String sAfterActionYear = ETSUtils.checkNull(this.params.getRequest().getParameter("after_year"));

			// steps for the setmet
			// blank						Interview not completed
			//SETMET_CLIENT_INTERVIEW		Interview completed, waiting for client approval
			//SETMET_CLIENT_APPROVED		client approved interview
			//SETMET_PRINCIPAL_APPROVED		principal approved
			//SETMET_ACTION_PLAN			action plan created
			//SETMET_ACTION_PLAN_APPROVED	action plan implemented - removed in 5.2.1
			//SETMET_CLOSE					after action review
			//SETMET_FINAL_RATING			set met completed.
			
			Timestamp timePrincipal = Timestamp.valueOf(sPrincipalYear + "-" + sPrincipalMonth + "-" + sPrincipalDay + " 00:00:00.000000000");
			iCount = ETSSetMetDAO.insertSetMetNofification(con,sSetMetID,proj.getProjectId(),Defines.SETMET_PRINCIPAL_APPROVED,timePrincipal);

			Timestamp timeAction = Timestamp.valueOf(sActionYear + "-" + sActionMonth + "-" + sActionDay + " 00:00:00.000000000");
			iCount = ETSSetMetDAO.insertSetMetNofification(con,sSetMetID,proj.getProjectId(),Defines.SETMET_ACTION_PLAN,timeAction);
			
//			Timestamp timeActionImplement = Timestamp.valueOf(sActionImplementYear + "-" + sActionImplementMonth + "-" + sActionImplementDay + " 00:00:00.000000000");
//			iCount = ETSSetMetDAO.insertSetMetNofification(con,sSetMetID,proj.getProjectId(),Defines.SETMET_ACTION_PLAN_APPROVED,timeActionImplement);
			
			//Timestamp timeAfterAction = Timestamp.valueOf(sAfterActionYear + "-" + sAfterActionMonth + "-" + sAfterActionDay + " 00:00:00.000000000");
			//iCount = ETSSetMetDAO.insertSetMetNofification(con,sSetMetID,proj.getProjectId(),Defines.SETMET_CLOSE,timeAfterAction);

		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}

		return sSetMetID;

	}

	private static synchronized String getNewSetMetID() throws SQLException, Exception {

		Random rand = new Random();

		String sUniqueId = "";

		Long lDate = new Long(System.currentTimeMillis());
		sUniqueId = lDate + "-" + rand.nextInt(1000) + "";

		return sUniqueId;
	}


	private String validateCreateSetMetInterview() throws Exception {

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		ETSProj proj = this.params.getETSProj();
		Connection con = this.params.getConnection();
		String sSetMetID =  "";
		StringBuffer sError = new StringBuffer("");

		try {
			

			String sTitle = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_title"));
			if (sTitle.equals("")) {
				sError.append("<b>Set/Met title</b> cannot be empty. Please enter the Set/Met title.<br />");
			}

			String sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_month"));
			String sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_day"));
			String sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_year"));

			int month = Integer.parseInt(sCalMonth.trim());
			int day = Integer.parseInt(sCalDay.trim());
			int year = Integer.parseInt(sCalYear.trim());

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month -1);
			int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);
			} else{
				sError.append("<b>Interview date</b> is not valid. Please set the interview date correctly.<br />");
			}


			String sClient = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_client"));
			String sClientName = ETSUtils.checkNull(this.params.getRequest().getParameter("clientname"));
			if (sClient.equals("") && sClientName.equals("")) {
				sError.append("<b>Client name</b> is not set. Please choose the appropriate client name or key in the client name.<br />");
			}
			
			String sPrincipal = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_principal"));
			if (sPrincipal.equals("")) {
				sError.append("<b>Principal</b> is not set. Please choose the appropriate principal.<br />");
			}
			
			sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_month"));
			sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_day"));
			sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_year"));

			month = Integer.parseInt(sCalMonth.trim());
			day = Integer.parseInt(sCalDay.trim());
			year = Integer.parseInt(sCalYear.trim());

			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month -1);
			iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);
			} else{
				sError.append("<b>Principal review date</b> is not valid. Please set the principal review date correctly.<br />");
			}
			
			
			String sPM = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_pm"));
			if (sPM.equals("")) {
				sError.append("<b>Program manager</b> is not set. Please choose the appropriate program manager.<br />");
			}


			sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("action_month"));
			sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("action_day"));
			sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("action_year"));

			month = Integer.parseInt(sCalMonth.trim());
			day = Integer.parseInt(sCalDay.trim());
			year = Integer.parseInt(sCalYear.trim());

			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month -1);
			iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);
			} else{
				sError.append("<b>Action plan date</b> is not valid. Please set the action plan creation date correctly.<br />");
			}


//			sCalMonth = ETSUtils.checkNull(this.params.getRequest().getParameter("after_month"));
//			sCalDay = ETSUtils.checkNull(this.params.getRequest().getParameter("after_day"));
//			sCalYear = ETSUtils.checkNull(this.params.getRequest().getParameter("after_year"));
//
//			month = Integer.parseInt(sCalMonth.trim());
//			day = Integer.parseInt(sCalDay.trim());
//			year = Integer.parseInt(sCalYear.trim());
//
//			cal.set(Calendar.YEAR,year);
//			cal.set(Calendar.MONTH,month -1);
//			iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//			iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
//
//
//			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
//				cal.set(Calendar.DAY_OF_MONTH,day);
//			} else{
//				sError.append("<b>After action review date</b> is not valid. Please set the after action review date correctly.<br />");
//			}
			
			String sMonth1 = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_month"));
			String sDay1 = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_day"));
			String sYear1 = ETSUtils.checkNull(this.params.getRequest().getParameter("setmet_year"));

			String sMonth2 = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_month"));
			String sDay2 = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_day"));
			String sYear2 = ETSUtils.checkNull(this.params.getRequest().getParameter("principal_year"));

			String sMonth3 = ETSUtils.checkNull(this.params.getRequest().getParameter("action_month"));
			String sDay3 = ETSUtils.checkNull(this.params.getRequest().getParameter("action_day"));
			String sYear3 = ETSUtils.checkNull(this.params.getRequest().getParameter("action_year"));

			//String sMonth4 = ETSUtils.checkNull(this.params.getRequest().getParameter("after_month"));
			//String sDay4 = ETSUtils.checkNull(this.params.getRequest().getParameter("after_day"));
			//String sYear4 = ETSUtils.checkNull(this.params.getRequest().getParameter("after_year"));

			long interviewdate = Timestamp.valueOf(sYear1 + "-" + sMonth1 + "-" + sDay1 + " 00:00:00.000000000").getTime();
			long principalreviewdate = Timestamp.valueOf(sYear2 + "-" + sMonth2 + "-" + sDay2 + " 00:00:00.000000000").getTime();
			long actionplandate = Timestamp.valueOf(sYear3 + "-" + sMonth3 + "-" + sDay3 + " 00:00:00.000000000").getTime();
			//long afteractionreviewdate = Timestamp.valueOf(sYear4 + "-" + sMonth4 + "-" + sDay4 + " 00:00:00.000000000").getTime();
		
			if (interviewdate > principalreviewdate) {
				sError.append("Interview date cannot be greater than Principal review date. <br />");
			}
		
			if (principalreviewdate > actionplandate) {
				sError.append("Principal review date cannot be greater than Action plan date. <br />");
			}
		
			//if (actionplandate > afteractionreviewdate) {
			//	sError.append("Action plan date cannot be greater than After action review date. <br />");
			//}			
			
		} catch (Exception e) {
			throw e;
		}

		return sError.toString();

	}

	private boolean sendEmailNotification() throws Exception {


		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		boolean bSent = false;

		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
			
			String sPrimaryContactID = ETSSetMetDAO.getPrimaryContact(con,proj.getProjectId());
			
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
			// get the setmet id..

			String sSetMetID = ETSUtils.checkNull(request.getParameter("set"));

			ETSSetMet SetMet = new ETSSetMet();

			if (sSetMetID.trim().equals("")) {
				// get the most current one by meeting date
				SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),"");
			} else {
				SetMet = ETSSetMetDAO.getSetMet(con,proj.getProjectId(),sSetMetID);
			}

			String sStep = getCurrentState(SetMet.getSetMetID());

			Vector vActionStates = SetMet.getSetMetStates();

			String sEmailSubject = "";
			StringBuffer sEmailStr = new StringBuffer("");

			String sBy = "";
			Timestamp tOn = null;
			String sTo = "";

			int iTCForSetMet = ETSDatabaseManager.getTopCatId(proj.getProjectId(),Defines.SETMET_VT);
			
			String SupressFlags = SetMet.getSupressFlags();
			String SupressClient1 = "";
			String SupressClient2 = "";
				
			if (SupressFlags == null || SupressFlags.equalsIgnoreCase("")) {
				SupressFlags = "000000";
			}

			if (SupressFlags.substring(1,2).equalsIgnoreCase("1")) {
				SupressClient1 = "Y";
			}

			if (SupressFlags.substring(5,6).equalsIgnoreCase("1")) {
				SupressClient2 = "Y";
			}

			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_INTERVIEW) && !SupressClient1.equalsIgnoreCase("Y")) {
				// interview has been created.. waiting for approval from client...
				
				if (!SetMet.getClientIRID().trim().equalsIgnoreCase("")) {
					
					//user in project and has been selected from the list and not keyed in

					sTo = ETSUtils.getUserEmail(con,SetMet.getClientIRID());
	
					sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " Set/Met notification: " + SetMet.getSetMetName() + " initial interview is ready for review");
	
					sEmailStr.append("Set/Met: " + SetMet.getSetMetName() + "\n\n");
	
					sEmailStr.append("Your initial interview for the " + SetMet.getSetMetName() + " Set/Met has been documented\n");
					sEmailStr.append("and is ready for you to review and approve.\n\n");
	
					sEmailStr.append("To review and approve the Set/Met, click on the following URL:\n");
					sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + proj.getProjectId() + "&tc=" + iTCForSetMet + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + params.getLinkId() + "\n\n");
	
					sEmailStr.append(prop.getEmailFooter());
//					sEmailStr.append("===============================================================\n");
//					sEmailStr.append("Delivered by E&TS Connect. \n");
//					sEmailStr.append("This is a system generated email. \n");
//					sEmailStr.append("===============================================================\n\n");
	
					bSent = ETSUtils.sendEMail(sPrimaryContactEmail,sTo,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,sPrimaryContactEmail);

				}

			}

			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
				// client has approved the interview.. waiting for approval from principal...

				sTo = ETSUtils.getUserEmail(con,SetMet.getSetMetBSE());

				sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " Set/Met notification: " + SetMet.getSetMetName() + " interview documentation has been approved");

				if (vActionStates != null) {
					for (int i = 0; i < vActionStates.size(); i++) {
						ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);
						String sState = state.getStep();
						if (sState.equalsIgnoreCase(Defines.SETMET_CLIENT_APPROVED)) {
							tOn = state.getActionDate();
							sBy = state.getActionBy();
							break;
						}
					}
				}

				sEmailStr.append("Set/Met: " + SetMet.getSetMetName() + "\n\n");

				sEmailStr.append(ETSUtils.getUsersName(con,sBy) + " has approved the initial interview documentation.\n");
				sEmailStr.append("The Set/Met is ready for the Principal's comments.\n\n");

				sEmailStr.append("To review and approve the Set/Met, click on the following URL:\n");
				sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + proj.getProjectId() + "&tc=" + iTCForSetMet + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + params.getLinkId() + "\n\n");

				sEmailStr.append(prop.getEmailFooter());

				bSent = ETSUtils.sendEMail(sPrimaryContactEmail,sTo,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,sPrimaryContactEmail);
				
			}

			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
				// ibm principal has approved the interview.. waiting for action plan creation...

				sTo = ETSUtils.getUserEmail(con,SetMet.getSetMetPractice());

				sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " Set/Met notification: " + SetMet.getSetMetName() + " Principal review has been completed");

				if (vActionStates != null) {
					for (int i = 0; i < vActionStates.size(); i++) {
						ETSSetMetActionState state = (ETSSetMetActionState) vActionStates.elementAt(i);
						String sState = state.getStep();
						if (sState.equalsIgnoreCase(Defines.SETMET_PRINCIPAL_APPROVED)) {
							tOn = state.getActionDate();
							sBy = state.getActionBy();
							break;
						}
					}
				}

				sEmailStr.append("Set/Met: " + SetMet.getSetMetName() + "\n\n");

				sEmailStr.append(ETSUtils.getUsersName(con,sBy) + " has reviewed the interview and has added any necessary\n");
				sEmailStr.append("comments. Development of action plan can now begin.\n\n");

				sEmailStr.append("To review interview and create the action plan, click on the following URL:\n");
				sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + proj.getProjectId() + "&tc=" + iTCForSetMet + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + params.getLinkId() + "\n\n");

				sEmailStr.append(prop.getEmailFooter());
				
				bSent = ETSUtils.sendEMail(sPrimaryContactEmail,sTo,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,sPrimaryContactEmail);
				
			}


			if (sStep.trim().equalsIgnoreCase(Defines.SETMET_ACTION_PLAN) && !SupressClient2.equalsIgnoreCase("Y")) {
				// setmet is closed.. client has to do the final rating...
				
				if (!SetMet.getClientIRID().trim().equalsIgnoreCase("")) {
				
					//user in project and has been selected from the list and not keyed in
				
					sTo = ETSUtils.getUserEmail(con,SetMet.getClientIRID());
					
					
					sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " Set/Met notification: " + SetMet.getSetMetName() + "  action plan has been implemented");
	
					sEmailStr.append("Set/Met: " + SetMet.getSetMetName() + "\n\n");
	
					sEmailStr.append("The action plan developed for your " + SetMet.getSetMetName() + " Set/Met has been completed\n");
					sEmailStr.append("and is available for your final review and rating.\n\n");
	
					sEmailStr.append("To give your final rating on the Set/Met, click on the following URL:\n");
					sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + proj.getProjectId() + "&tc=" + iTCForSetMet + "&etsop=inter&set=" + SetMet.getSetMetID() + "&linkid=" + params.getLinkId() + "\n\n");
	
					sEmailStr.append(prop.getEmailFooter());
					
					bSent = ETSUtils.sendEMail(sPrimaryContactEmail,sTo,"",Global.mailHost,sEmailStr.toString(),sEmailSubject,sPrimaryContactEmail);
				}				
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return bSent;
	}

}
