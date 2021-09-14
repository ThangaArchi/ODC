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


package oem.edge.ets.fe;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSDealTracker;
import oem.edge.ets.fe.ismgt.actions.EtsFeedbackProcess;
import oem.edge.ets.fe.ismgt.cntrl.ETSIssuesServlet;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.self.ETSSelfAssessRequestHandler;
import oem.edge.ets.fe.setmet.ETSAboutSetMetRequestHandler;
import oem.edge.ets.fe.setmet.ETSClientCareContact;
import oem.edge.ets.fe.setmet.ETSSetMetRequestHandler;
import oem.edge.ets.fe.survey.ETSSurveyRequestHandler;

import org.apache.commons.logging.Log;

/**
 * 03/04/2004
 * changed by Navneet
 * changed signature of method: createGraphTabs from private to public static to enable other classes to use it
 * also the instance variable: databaseManager was changed to static
 * and its assignment was moved out of init() to enable this change
 */

public class ETSProjectsServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.10.1.65";

	private static Log logger = EtsLogger.getLogger(ETSProjectsServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    response.setContentType("text/html");
		PrintWriter writer = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
	 	AccessCntrlFuncs acf = new AccessCntrlFuncs();

		Hashtable params;

		StringBuffer sHeader = new StringBuffer("");

		ETSCat topCat;

		try {

			conn = ETSDBUtils.getConnection();
     	    if (!es.GetProfile(response, request, conn)) {
				return;
			}

			String projectidStr = getParameter(request, "proj");
			ETSProj proj = ETSUtils.getProjectDetails(conn,projectidStr);
			
			if (proj == null || proj.getProjectId() == null || proj.getProjectId().equalsIgnoreCase("")) {
				// the project code entered in the url is not valid.. redirect to error page..
				response.sendRedirect("ETSErrorServlet.wss?ecode=INVALID_WORKSPACE_ID&sname=" + URLEncoder.encode("The workspace specified in the URL is not valid."));
				return;
			}
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());

			ETSHeaderFooter headerfooter = new ETSHeaderFooter();
			headerfooter.init(request,response);

			Hashtable hs = ETSUtils.getServletParameters(request);

			String sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", unBrandedprop.getLinkID());
				sLink = unBrandedprop.getLinkID();
			}

			// if not superadmin and not executive and not member, then redirect the user to unauthorized access page.
			// changed for 4.4.1
			if (ETSUtils.checkUserRole(es,proj.getProjectId(),conn).equals(Defines.INVALID_USER)) {
				response.sendRedirect(unBrandedprop.getUnauthorizedURL());
				return;
			}

			// check for deleted projects
			if (proj.getProject_status().equalsIgnoreCase("D")) {
				response.sendRedirect(unBrandedprop.getUnauthorizedURL());
				return;
			}


			boolean bAdmin = false;
			boolean bExecutive = false;

			if (ETSUtils.checkUserRole(es,proj.getProjectId(),conn).equals(Defines.ETS_ADMIN)) {
				bAdmin = true;
			}

			if (ETSUtils.checkUserRole(es,proj.getProjectId(),conn).equals(Defines.ETS_EXECUTIVE)) {
				bExecutive = true;
			}


			ETSProjectInfoBean projBean = (ETSProjectInfoBean) request.getSession(false).getAttribute("ETSProjInfo");

			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(conn);
				request.getSession(false).setAttribute("ETSProjInfo",projBean);
			}

		    Calendar cal = Calendar.getInstance();

		   	String sInMonth = request.getParameter("month");
		   	if (sInMonth == null) {
		   		sInMonth = "";
		   	} else {
		   		sInMonth = sInMonth.trim();
		   	}

		   	String sInYear = request.getParameter("year");
		   	if (sInYear == null) {
		   		sInYear = "";
		   	} else {
		   		sInYear = sInYear.trim();
		   	}

		   	if (!sInMonth.trim().equals("") && !sInYear.trim().equals("")) {
		   		projBean.setSMonth(sInMonth);
		   		projBean.setSYear(sInYear);
			    cal.set(Integer.parseInt(sInYear),Integer.parseInt(sInMonth),01);
		   	}

		   	topCat = headerfooter.getTopCat();

			ETSParams parameters = new ETSParams();
			parameters.setConnection(conn);
			parameters.setEdgeAccessCntrl(es);
			parameters.setETSProj(proj);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setTopCat(topCat.getId());
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
			parameters.setProjBeanInfo(projBean);
			parameters.setCurrentTabName(topCat.getName());
			parameters.setSuperAdmin(bAdmin);
			parameters.setExecutive(bExecutive);
			parameters.setUnbrandedProperties(unBrandedprop);

			//topCat = topCat;

			sHeader = headerfooter.getHeader();

			// end content including right side...
			if (topCat.getViewType() == Defines.MAIN_VT) { // home ???

				if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {

					writer.println(sHeader.toString());

					// to insert tab hits.
					ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"PROPOSAL-MAIN");

					Metrics.appLog(conn, es.gIR_USERN, "ETS_Projects");
					// start content...

					ETSProjectHome.displayProjectHome(parameters);

					writer.println("</td>");

					// gutter between content and right column
					writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
					// Right column start
					writer.println("<td headers=\"\" width=\"150\" valign=\"top\">");

					ETSContact contact = new ETSContact(proj.getProjectId(),request);
					contact.printContactBox(writer);

					writer.println("<br />");

					if (bAdmin || bExecutive) {
						ETSQuickSwitch quickSwitch = new ETSQuickSwitch(es.gIR_USERN,projectidStr,true, proj.getProjectType());
						writer.println(quickSwitch.getQuickSwitchBox(conn));
					} else {
						ETSQuickSwitch quickSwitch = new ETSQuickSwitch(es.gIR_USERN,projectidStr,false, proj.getProjectType());
						writer.println(quickSwitch.getQuickSwitchBox(conn));
					}

                    writer.println("<br />");

					boolean bDisplayed = ETSCalendar.displayCalendar(parameters);
					if (bDisplayed) {
						writer.println("<br />");
					}

					ETSProjectHome.displaySiteHelp(parameters);

					writer.println("<br />");


				} else {

					writer.println(sHeader.toString());
					
					// start content...

					// to insert tab hits.
					ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"PROJECT-MAIN");

					Metrics.appLog(conn, es.gIR_USERN, "ETS_Proposal");

					ETSProposalHome.displayProposalHome(parameters);

					writer.println("</td>");

					// gutter between content and right column
					writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
					// Right column start
					writer.println("<td headers=\"\" width=\"150\" valign=\"top\">");

					ETSContact contact = new ETSContact(proj.getProjectId(), request);
					contact.printContactBox(writer);
					writer.println("<br />");

					if (bAdmin || bExecutive) {
						ETSQuickSwitch quickSwitch = new ETSQuickSwitch(es.gIR_USERN,projectidStr,true, proj.getProjectType());
						writer.println(quickSwitch.getQuickSwitchBox(conn));
					} else {
						ETSQuickSwitch quickSwitch = new ETSQuickSwitch(es.gIR_USERN,projectidStr,false, proj.getProjectType());
						writer.println(quickSwitch.getQuickSwitchBox(conn));
					}

                    writer.println("<br />");

//					ETSProposalHome.designedToHelpYouModule(parameters);
//
//					writer.println("<br />");

					ETSProjectHome.displaySiteHelp(parameters);

					writer.println("<br />");

				}
			} else if (topCat.getViewType() == Defines.MEETINGS_VT) {	// meetings tab...

				writer.println(sHeader.toString());

				//to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"MEETINGS-MAIN");
				// start content...

				ETSMeetings.displayMeetingsTab(parameters);

				writer.println("</td>");

				// gutter between content and right column
				writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				writer.println("<td headers=\"\" width=\"150\" valign=\"top\">");

				ETSContact contact = new ETSContact(proj.getProjectId(), request);
				contact.printContactBox(writer);

				writer.println("<br />");

				boolean bDisplayed = ETSCalendar.displayCalendar(parameters);

			} 
			//added by Ryazuddin for Workflow main main tab
			
			else if (topCat.getViewType() == Defines.WorkFlow_Maintab) {	// WorkflowMain tab...

				System.out.println("Inside the Workflow main tab -------------------------");
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"WorkFlow-MAIN");
				String strworkflowtForward = "workflowlist.wss";
				request.setAttribute("tabname","WorkFlow-MAIN");
				RequestDispatcher pdDispatcher =
				   request.getRequestDispatcher(
				   		strworkflowtForward
						   + "?proj="
						   + proj.getProjectId()
						   + "&tc="
						   + topCat.getId());
			   pdDispatcher.forward(request, response);
			   return;		

			}else if (topCat.getViewType() == Defines.WorkFlow_Assessment) {	// Workflow-Assessment tab...
				
				//to insert tab hits.
				request.setAttribute("tabname","Assessment-MAIN");
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"Assessment-MAIN");
				String strworkflowtForward = "workflowlist.wss";
				RequestDispatcher pdDispatcher =
				   request.getRequestDispatcher(
				   		strworkflowtForward
						   + "?proj="
						   + proj.getProjectId()
						   + "&tc="
						   + topCat.getId());
			   pdDispatcher.forward(request, response);
			   return;		
			}
			
			// workflow code Ends here
			
			else if (topCat.getViewType() == Defines.ISSUES_CHANGES_VT) { // issues

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"ISSUES-MAIN");

				//writer.println(sHeader.toString());

				String actiontype = (String) hs.get("actionType");

				if (actiontype.equals("submitIssue") || actiontype.equals("viewIssue") || actiontype.equals("modifyIssue") || actiontype.equals("resolveIssue") || actiontype.equals("rejectIssue") || actiontype.equals("closeIssue") || actiontype.equals("commentIssue") ||  actiontype.equals("reqNewIssTyp")  || actiontype.equals("submitChange") || actiontype.equals("viewChange") || actiontype.equals("commentChange") || actiontype.equals("chgOwner") || actiontype.equals("withDrwIssue")|| actiontype.equals("subscrIssue") || actiontype.equals("unSubscrIssue") || actiontype.equals("feedback")) {

					ETSIssuesServlet newissue = new ETSIssuesServlet(hs, proj, topCat.getId(), writer, conn, es, acf, sLink, request, response);
					newissue.processRequest();

				} else {

					// start content...

					writer.println(sHeader.toString());

					ETSIssuesServlet newissue = new ETSIssuesServlet(hs, proj, topCat.getId(), writer, conn, es, acf, sLink, request, response);
					newissue.processRequest();

					writer.println("</td>");

					//gutter between content and right column
					writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
					//Right column start
					writer.println("<td headers=\"\" width=\"150\" valign=\"top\">");

					ETSContact contact = new ETSContact(proj.getProjectId(), request);
					contact.printContactBox(writer);
					writer.println("<br />");
					writer.println("<br />");

				}

			} else if (topCat.getViewType() == Defines.TEAM_VT) { // members list view

				writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"TEAM-MAIN");

				writer.println("</td>");

                ETSAdminServlet admin_serv = new ETSAdminServlet(parameters);
                admin_serv.ETSAdminHandler();

				writer.println("<br />");

			} else if (topCat.getViewType() == Defines.DOCUMENTS_VT) { //doc view

				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"DOCUMENTS-MAIN");
				String strDocumentForward = "displayDocumentList.wss";
				RequestDispatcher pdDispatcher =
				   request.getRequestDispatcher(
						strDocumentForward
						   + "?proj"
						   + proj.getProjectId()
						   + "&tc="
						   + topCat.getId());
			   pdDispatcher.forward(request, response);

			   return;

			} else if (topCat.getViewType() == Defines.CONTRACTS_VT) { //deal tracker view

				writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"CONTRACTS-MAIN");

				writer.println("</td>");

				ETSDealTracker tracker = new ETSDealTracker(parameters);
           	    tracker.ETSTrackerHandler();

			} else if (topCat.getViewType() == Defines.METRICS_VT) { //Metrics tab

				writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"METRICS-MAIN");

				// refer deal tracker if in confusion
				// if you want to use the three column layout, follow the same from meetings tab.

				// close this td so the rest of the tab can use 600 pixels.
				writer.println("</td>");

				// Instantiate your class here
				// handle the requests in your class here.

			} else if (topCat.getViewType() == Defines.SETMET_VT) { // set met tab

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"SETMET-MAIN");

				/**
				 *
				 *Set met has to be handled differently as it has to use all 600 pixels and
				 *it is different from other tabs.
				 *the table handling will all be done in the set met classes
				 *
				 */

				// Instantiate your class here
				// handle the requests in your class here.
				ETSSetMetRequestHandler SetMet = new ETSSetMetRequestHandler(parameters, sHeader,es);

				SetMet.handleRequest();

			} else if (topCat.getViewType() == Defines.ABOUT_SETMET_VT) { // about set met tab

				writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"ABOUT_SETMET");

				ETSAboutSetMetRequestHandler aboutSetMet = new ETSAboutSetMetRequestHandler(parameters,es);
				aboutSetMet.handleRequest();

				writer.println("</td>");

				// gutter between content and right column
				writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				writer.println("<td headers=\"\" width=\"150\" valign=\"top\">");

				ETSClientCareContact contact = new ETSClientCareContact(conn,proj.getProjectId(), request);
				contact.printContactBox(writer);
				writer.println("<br />");

			} else if (topCat.getViewType() == Defines.FEEDBACK_VT) { //feedback tab

				//writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"FEEDBACK-MAIN");

				EtsIssActionObjKeyPrep etsActKeyPrep = new EtsIssActionObjKeyPrep(hs, proj, topCat.getId(), es, sLink, request, response);
				EtsIssObjectKey etsIssObjKey = etsActKeyPrep.getEtsIssActionObjKey();


				EtsFeedbackProcess feed = new EtsFeedbackProcess(etsIssObjKey);
				writer.println(feed.processRequest());

			} else if (topCat.getViewType() == Defines.PROJECTS_VT) { //projects tab

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"SETMET-PROJECT-MAIN");

				// refer deal tracker if in confusion
				// if you want to use the three column layout, follow the same from meetings tab.

				// close this td so the rest of the tab can use 600 pixels.
				writer.println("</td>");

				// Instantiate your class here
				// handle the requests in your class here.

			} else if (topCat.getViewType() == Defines.SELF_ASSESSMENT_VT) { // self assessment tab

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"SELF-ASSESSMENT-MAIN");

				/**
				 *
				 *Self assessment t has to be handled differently as it has to use all 600 pixels and
				 *it is different from other tabs.
				 *the table handling will all be done in the set met classes
				 *
				 */

				// Instantiate your class here
				// handle the requests in your class here.
				ETSSelfAssessRequestHandler ETSSelf = new ETSSelfAssessRequestHandler(parameters, sHeader,es);

				ETSSelf.handleRequest();
			} else if (topCat.getViewType() == Defines.ASIC_VT) { //asic tab view

				writer.println(sHeader.toString());

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"ASICS-MAIN");

				writer.println("</td>");

				ETSAsic asic = new ETSAsic(parameters);
				asic.ETSAsicHandler();
				//tracker.ETSTrackerHandler();

			} else if (topCat.getViewType() == Defines.SURVEY_VT) { // survey tab

				// to insert tab hits.
				ETSUtils.insertTabHits(conn,es.gIR_USERN,proj.getProjectId(),topCat.getName(),"SURVEY-MAIN");

				/**
				 *
				 *Survey has to be handled differently as it has to use all 600 pixels and
				 *it is different from other tabs.
				 *the table handling will all be done in the set met classes
				 *
				 */

				// Instantiate your class here
				// handle the requests in your class here.
				ETSSurveyRequestHandler survey = new ETSSurveyRequestHandler(parameters, sHeader,es);
				survey.handleRequest();

			}

				// end right column...
			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");

			writer.println("<br /><br />");

			writer.println(headerfooter.getFooter());


		} catch (SQLException e) {
            e.printStackTrace();
            if(logger.isErrorEnabled()) {
            	logger.error(this,e);
            }
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on ETSProjectsServlet.");
		} catch (Exception e) {
            e.printStackTrace();
			if(logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on ETSProjectsServlet.");
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);

		if (value == null) {
			return "";
		} else {
			return value;
		}
	}





}
