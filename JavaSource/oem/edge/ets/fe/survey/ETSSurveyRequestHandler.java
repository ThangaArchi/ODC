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


package oem.edge.ets.fe.survey;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSSelfDealTracker;
import oem.edge.ets.fe.setmet.ETSClientCareContact;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSSurveyRequestHandler {


	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.4";
	
	private static Log logger = EtsLogger.getLogger(ETSSurveyRequestHandler.class);

	private ETSParams params;
	private String header = "";
	private EdgeAccessCntrl es = null;


	public ETSSurveyRequestHandler(ETSParams parameters, StringBuffer sHeader, EdgeAccessCntrl es1) {

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
				displaySurveyLandingPage();
			} else if (sETSOp.trim().equals("survey")){
				displaySurvey();
			} else if (sETSOp.trim().equals("action")){
				displayActionPlan();
			} else if (sETSOp.trim().equals("closeap")){
				closeActionPlan();
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
	private void displaySurveyLandingPage() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
	
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

		// get all surveys and display them here...

		boolean bSurveyDisplayed = false;

		out.println(this.header);

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")));

		out.println(header[0]);
		
		boolean bClient = false;
			
		if (ETSUtils.checkUserRole(es,proj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {
			bClient = true;
		}
		
		Vector vOpenSurveys = ETSSurveyDAO.getSurveysForCompany(con,proj.getCompany(), ETSSurveyConstants.SURVEY_OPEN);
		
		if (vOpenSurveys != null && vOpenSurveys.size() > 0) {
			
			bSurveyDisplayed = true;
			
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"subtitle\"><b>Open surveys</b></span></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
					
			out.println("<br />");
			
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">The following surveys have a status of 'open'. Click on a survey results link to view and/or print results. To work with a survey's action plan, click the desired action plan link. You must complete the action plan in order to close the survey. </td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
	
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr class=\"tblue\">");
			if (bClient) { 
				out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\">Surveys</span></td>");
			} else {
				out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\">Action plans</span></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">Surveys</span></td>");
			}
			out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">Overall satisfaction</span></td>");
			out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">Respondent</span></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">Survey date</span></td>");
			out.println("</tr>");
			
			String Year = "";
			int iCount = 1;
			
			for (int i = 0; i < vOpenSurveys.size(); i++) {
				ETSSurvey survey = (ETSSurvey) vOpenSurveys.elementAt(i);
				if (!Year.equalsIgnoreCase(survey.getYear())){
					out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
					if (bClient) {
						out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\"><b>" + survey.getYear() + " surveys</b></span></td>");
					} else {
						out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Action plan</a></span></td>");
						out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\"><b>" + survey.getYear() + " surveys</b></span></td>");
					}
					out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("</tr>");
					Year = survey.getYear();
					iCount = 1;
				}
				out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
				if (bClient) {
					out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=survey&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Survey " + iCount + "</a></span></td>");
				} else {
					out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=survey&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Survey " + iCount + "</a></span></td>");
				}
				out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">" + survey.getOverallSatisfaction() + "</span></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">" + survey.getFirstName() + " " + survey.getLastName() + "</span></td>");
				out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">" + survey.getSurveyDate() + "</span></td>");
				out.println("</tr>");
				iCount = iCount + 1;
			}
	
			out.println("</table>");

		}
		
		out.println("<br /><br />");


		Vector vClosedSurveys = ETSSurveyDAO.getSurveysForCompany(con,proj.getCompany(), ETSSurveyConstants.SURVEY_CLOSED);
		
		if (vClosedSurveys != null && vClosedSurveys.size() > 0) {
			
			bSurveyDisplayed = true;
			
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><span class=\"subtitle\"><b>Closed surveys</b></span></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
					
			out.println("<br />");
			
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">The following surveys have a status of 'closed'. Click a survey results link to view and/or print results. Click the Action Plan link to view the Action Plan.</td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
	
			out.println("<br />");
	
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			out.println("<tr class=\"tblue\">");
			if (bClient) {
				out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\">Surveys</span></td>");
			} else {
				out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\">Action plans</span></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">Surveys</span></td>");
			}
			out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">Overall satisfaction</span></td>");
			out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">Respondent</span></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">Survey date</span></td>");
			out.println("</tr>");
			
			String Year = "";
			int iCount = 1;
			
			for (int i = 0; i < vClosedSurveys.size(); i++) {
				ETSSurvey survey = (ETSSurvey) vClosedSurveys.elementAt(i);
				if (!Year.equalsIgnoreCase(survey.getYear())){
					out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
					if (bClient) {
						out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\"><b>" + survey.getYear() + " surveys</b></span></td>");
					} else {
						out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=action&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Action plan</a></span></td>");
						out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\"><b>" + survey.getYear() + " surveys</b></span></td>");
					}
					out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("</tr>");
					Year = survey.getYear();
					iCount = 1;
				}
				out.println("<tr height=\"21\" style=\"font-weight: normal;\">");
				if (bClient) {
					out.println("<td headers=\"\" width=\"190\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=survey&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Survey " + iCount + "</a></span></td>");
				} else {
					out.println("<td headers=\"\" width=\"90\" align=\"left\" valign=\"top\"><span class=\"small\">&nbsp;</span></td>");
					out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&etsop=survey&survey_year=" + survey.getYear() + "&response_id=" + survey.getResponseID() + "&linkid=" + this.params.getLinkId() + "\" >Survey " + iCount + "</a></span></td>");
				}
				out.println("<td headers=\"\" width=\"60\" align=\"middle\" valign=\"top\"><span class=\"small\">" + survey.getOverallSatisfaction() + "</span></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">" + survey.getFirstName() + " " + survey.getLastName() + "</span></td>");
				out.println("<td headers=\"\" width=\"80\" align=\"left\" valign=\"top\"><span class=\"small\">" + survey.getSurveyDate() + "</span></td>");
				out.println("</tr>");
				iCount = iCount + 1;
			}
	
			out.println("</table>");
		}
		
		if (!bSurveyDisplayed) {
			out.println("<table width=\"100%\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\">There are no surveys in the system at this time.</td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

		}

		out.println("<br /><br />");

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
	
	private String[] getHeader(String sETSOp) throws Exception{

		String[] header = new String[]{};
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		ETSProj proj = this.params.getETSProj();

		try {
			if (sETSOp.equals("")){
				header = new String[]{ETSUtils.getBookMarkString("Surveys","#survey", true)};
			} else if (sETSOp.equals("survey") || sETSOp.equals("action") || sETSOp.equals("addactionplan") || sETSOp.equals("delactionplan") || sETSOp.equals("approve") || sETSOp.equals("closeap")){
				header = new String[]{ETSUtils.getBookMarkString("Surveys","#survey", false)};
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return header;

	}
	
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


		String sAction = ETSUtils.checkNull(request.getParameter("action"));

		// get the Self Assessment ID..

		String sYear = ETSUtils.checkNull(request.getParameter("survey_year"));
		
		// in case year is not passed, then get the selfid (selfid contains the surveyid) and get the plan based on it
		String sSurveyId = ETSUtils.checkNull(request.getParameter("self"));
		
		ETSSurveyActionPlan plan = new ETSSurveyActionPlan(); 
		
		if (!sYear.equalsIgnoreCase("")) {
			// check to see if status record has been inserted.. if not insert a new record...
			plan = ETSSurveyDAO.getSurveyActionPlan(con,sYear,proj.getCompany());
		} else {
			plan = ETSSurveyDAO.getSurveyActionPlanFromSurveyID(con,sSurveyId);
		}

		// get survey status here...
		String sSurveyStatus = ETSUtils.checkNull(plan.getStatus());

		// get survey action plan owner...
		String sActionPlanOwner = ETSUtils.checkNull(plan.getPlanOwnerId());
		

		String sPrinterFriendly = ETSUtils.checkNull(request.getParameter("skip"));
		if (sPrinterFriendly == null || sPrinterFriendly.trim().equalsIgnoreCase("")) {
			sPrinterFriendly = "N";
		} else {
			sPrinterFriendly = sPrinterFriendly.trim();
		}	

		boolean projArchieved = false;
		boolean projDeleted = false;
		boolean bSurveyClosed  = false;

		if (proj.getProject_status().equalsIgnoreCase("A")) {
			projArchieved = true;
		}

		if (proj.getProject_status().equalsIgnoreCase("D")) {
			projDeleted = true;
		}
		
		if (sSurveyStatus.equalsIgnoreCase(ETSSurveyConstants.SURVEY_CLOSED)) {
			bSurveyClosed = true;
		}


		if (sActionPlanOwner.equalsIgnoreCase(es.gIR_USERN)) {
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

		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")));

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

		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Action plan owner:</b></td>");
		if (sActionPlanOwner.equalsIgnoreCase("")) {
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
		} else {
			out.println("<td headers=\"\" align=\"left\">" + ETSUtils.getUsersName(con,plan.getPlanOwnerId()) + "</td>");
		}
		
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Action plan due date:</b></td>");
		out.println("<td headers=\"\" align=\"left\">" + ETSUtils.formatDate(plan.getPlanDueDate()) + "</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");

		if (sAction.equalsIgnoreCase("") && !projArchieved && !projDeleted && !bSurveyClosed && (bAdmin || bWorkspaceOwner) && !sPrinterFriendly.equalsIgnoreCase("Y")) { 
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "','Action','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "','Action','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Edit action plan details\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" ><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "','Action','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyActionPlanServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "','Action','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Edit action plan details</a></td>");
			out.println("</tr>");
			out.println("</table>");
		}

		out.println("<br />");
		out.println("<br />");
		
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\">The following are the action plan items for this survey.</td>");
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

		if (!projArchieved && !projDeleted && !bSurveyClosed) {
			bPlanEditable = true;
		}		
		
		if (!projArchieved && !projDeleted && !bSurveyClosed && (bAdmin || bWorkspaceManager || bWorkspaceOwner || bPlanOwner)) {
			bShowCloseLink = true;
		}		
		
		ETSSelfDealTracker tracker = new ETSSelfDealTracker(params, plan.getSurveyId(), "Survey Action Plan", bPlanEditable, bShowCloseLink,true);
		tracker.SelfTrackerHandler();


		out.println("<br />");
		printGreyDottedLine(out);
		out.println("<br />");

		if (!sPrinterFriendly.trim().equalsIgnoreCase("Y")) {
			out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&linkid=" + this.params.getLinkId() + "\" >Back to surveys</a></td>");
			out.println("</tr>");
			out.println("</table>");
		}


	}

	private void displaySurvey() throws SQLException, Exception {
	
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
	
		String sYear = ETSUtils.checkNull(request.getParameter("survey_year"));
		String sResponseId = ETSUtils.checkNull(request.getParameter("response_id"));
		
		ETSSurvey survey = ETSSurveyDAO.getSurveyData(con,sYear,sResponseId);
	
		out.println(this.header);
	
		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")));
	
		out.println(header[0]);
	
		out.println("<br />");
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Respondent name:</b></td>");
		out.println("<td headers=\"\" align=\"left\">" + survey.getFirstName() + " " + survey.getLastName() + "</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Respondent title:</b></td>");
		out.println("<td headers=\"\" align=\"left\">" + survey.getTitle() + "</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Respondent country:</b></td>");
		out.println("<td headers=\"\" align=\"left\">" + survey.getCountry() + "</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\"><b>Survey date:</b></td>");
		out.println("<td headers=\"\" align=\"left\">" + survey.getSurveyDate() + "</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td headers=\"\" colspan=\"2\">&nbsp;</td>");
		out.println("</tr>");
		
		out.println("</table>");
		
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
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" ><b>Note:</b> Rating of <b>" + ETSSurveyConstants.RATING_NA_REPLACE + "</b> means Don't know / No answer</td>");
		out.println("</tr>");
		out.println("</table>");
		
		out.println("<br />");
		
		// get the survey details...
		
		ETSSurveyDetails details = ETSSurveyFuncs.getSurveyDetails(con,sYear,sResponseId);
		
		Vector vSurveyData = details.getData();
		
		if (vSurveyData != null && vSurveyData.size() >=0) {

			out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> <tr> <td class=\"tblue\" height=\"18\">Survey rating</td> </tr> <tr> <td>");
			
			
			out.println("<table summary=\"\" width=\"600\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\" class=\"v14-gray-table-border\">");
			out.println("<tr style=\"background-color: #eeeeee\">");
			out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"240\" align=\"left\" valign=\"top\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getDivision() + "</b></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider1() + "</b></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider2() + "</b></td>");
			out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + details.getProvider3() + "</b></td>");
			out.println("</tr>");
			
			
			int iCount = 1;
			for (int i = 0; i < vSurveyData.size(); i++) {
				ETSSurveyQAData data = (ETSSurveyQAData) vSurveyData.elementAt(i);
				if (data.getAnswerType().equalsIgnoreCase(ETSSurveyConstants.DISPLAY_RATING)) {
					if ((iCount % 2) == 0) {
						out.println("<tr style=\"background-color: #eeeeee\">");
					} else {
						out.println("<tr >");
					}
					iCount = iCount + 1;
					out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\"><b>" + data.getQuestionNo() + ".</b></td>");
					out.println("<td headers=\"\" width=\"240\" align=\"left\" valign=\"top\">" + data.getQuestionText() + "</td>");
					out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer1() + "</b></td>");
					out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer2() + "</b></td>");
					out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer3() + "</b></td>");
					out.println("<td headers=\"\" width=\"80\" align=\"middle\" valign=\"top\"><b>" + data.getAnswer4() + "</b></td>");
					out.println("</tr>");
//					out.println("<tr >");
//					out.println("<td headers=\"\" colspan=\"6\" align=\"middle\" valign=\"top\">");
//					printGreyDottedLine(out);
//					out.println("</td>");
//					out.println("</tr>");
				}
			}
			out.println("</table>");
			out.println("</td> </tr> </table>"); 


			out.println("<br /><br />");

			out.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> <tr> <td class=\"tblue\" height=\"18\">Survey details</td> </tr> <tr> <td>");
			
			out.println("<table summary=\"\" width=\"600\" cellpadding=\"1\" cellspacing=\"1\" border=\"0\">");
			
			for (int i = 0; i < vSurveyData.size(); i++) {
				ETSSurveyQAData data = (ETSSurveyQAData) vSurveyData.elementAt(i);
				if (data.getAnswerType().equalsIgnoreCase(ETSSurveyConstants.DISPLAY_TEXT) && !data.getAnswer1().equalsIgnoreCase("")) {
					out.println("<tr >");
					out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\"><b>" + data.getQuestionNo() + ".</b></td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\"><b>" + data.getQuestionText() + "</b></td>");
					out.println("</tr>");
					out.println("<tr >");
					out.println("<td headers=\"\" width=\"40\" align=\"left\" valign=\"top\">&nbsp;</td>");
					out.println("<td headers=\"\" align=\"left\" valign=\"top\">" + data.getAnswer1() + "</td>");
					out.println("</tr>");
					out.println("<tr >");
					out.println("<td headers=\"\" colspan=\"2\" align=\"left\" valign=\"top\">&nbsp;</td>");
					out.println("</tr>");
				}
			}
			out.println("</table>");
			out.println("</td> </tr> </table>"); 
		}
				
		out.println("<br /><br />");				

	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&linkid=" + this.params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk_c.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a></td><td headers=\"\" align=\"left\" width=\"130\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + this.params.getTopCat() + "&linkid=" + this.params.getLinkId() + "\" >Back to surveys</a></td>");
		out.println("<td headers=\"\" align=\"left\" width=\"18\"><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "\"  target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\"><img src=\"" + Defines.ICON_ROOT + "printer.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"Printer friendly\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "\"  target=\"new\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSSurveyPrinterFriendlyServlet.wss?proj=" + proj.getProjectId() + "&survey_year=" + sYear + "&response_id=" + sResponseId + "','New','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=650,height=500,left=150,top=120');return false;\">Printer friendly version</a></td>");
		out.println("</tr>");
		out.println("</table>");
	
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
	
		String sSurveyID = ETSUtils.checkNull(request.getParameter("self"));
	
		out.println(this.header);
	
		String[] header = getHeader(ETSUtils.checkNull(request.getParameter("etsop")));
	
		out.println(header[0]);
	
		out.println("<br />");
	
	
		// table 1
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr><td headers=\"\"  class=\"tblue\">");

		// table 2
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Close action plan for survey</td>");
		out.println("</tr>");

		// end of table 2
		out.println("</table>");

		// end of table 1
		out.println("</td></tr></table>");

		out.println("<br />");

		printGreyDottedLine(out);
		out.println("<br />");
		
		
		ETSSurveyActionPlan plan = new ETSSurveyActionPlan();
		plan = ETSSurveyDAO.getSurveyActionPlanFromSurveyID(con,sSurveyID);
		
		plan.setSurveyId(sSurveyID);
		plan.setCompany(proj.getCompany());
		plan.setLastTimestamp(new Timestamp(System.currentTimeMillis()));
		plan.setLastUserId(es.gIR_USERN);
		plan.setStatus(ETSSurveyConstants.SURVEY_CLOSED);

		boolean success = ETSSurveyDAO.updateActionPlanStatusBySurveyID(con,plan);

		// table 3
		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr style=\"font-weight: normal;\"><td headers=\"\" align=\"left\" height=\"24\">");
		if (success) {
			out.println("<b>You have successfully closed this action plan and survey.</b>");
		}  else {
			out.println("<span style=\"color:#ff3333\"><b>There was an error when closing the action plan. Please try again. If the problem persists, please contact your client care advocate.</b></span>");
		}
		out.println("</td></tr>");

		// end of table 3
		out.println("</table>");

		out.println("<br />");
	
		out.println("<br />");
	
		printGreyDottedLine(out);
		out.println("<br />");
	
	
		out.println("<table summary=\"\" width=\"443\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + request.getParameter("proj") + "&tc=" + request.getParameter("tc") + "&linkid=" + request.getParameter("linkid") + "\"><img src=\"" + Defines.BUTTON_ROOT  + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
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
}
