/* Copyright Header Check */
/* -------------------------------------------------------------------- */
/*                                                                          */
/* OCO Source Materials */
/*                                                                          */
/* Product(s): PROFIT */
/*                                                                          */
/* (C)Copyright IBM Corp. 2001-2004 */
/*                                                                          */
/* All Rights Reserved */
/* US Government Users Restricted Rigts */
/*                                                                          */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the US Copyright Office. */
/*                                                                          */
/* -------------------------------------------------------------------- */
/* Please do not remove any of these commented lines 20 lines */
/* -------------------------------------------------------------------- */
/* Copyright Footer Check */

package oem.edge.ets.fe.aic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCal;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSSearchCommon;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.workflow.util.DBHandler;

import org.apache.commons.logging.Log;

public class AICUserEventsServlet extends HttpServlet implements Servlet {

	public final static String Copyright = "© Copyright IBM Corp.  2001 - 2004";

	public static final String CLASS_VERSION = "1.3";

	private static Log logger = EtsLogger.getLogger(AICUserEventsServlet.class);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		Connection conn = null;
		String Msg = null;
		Hashtable params = null;
		String sLink = "";

		StringBuffer sHeader = new StringBuffer("");
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		PrintWriter writer = response.getWriter();

		try {

			UnbrandedProperties prop = PropertyFactory
					.getProperty(Defines.AIC_WORKSPACE_TYPE);

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			conn = ETSDBUtils.getConnection();
			System.out.println("After DB Connection");
			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID());
				sLink = prop.getLinkID();
			}

			ETSParams parameters = new ETSParams(); // set the parameters
			parameters.setConnection(conn);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
			parameters.setEdgeAccessCntrl(es);

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPageTitle("Workflow Events Calendar");
			EdgeHeader.setPopUp("J");

			String from = ETSUtils.checkNull(request.getParameter("from"));
			String action = ETSUtils.checkNull(request.getParameter("act"));

			System.out.println("action " + action);

			AICUserRequestFunctions userRequestFunctions = new AICUserRequestFunctions(
					parameters);

			//if (from.equalsIgnoreCase("req")) {
			EdgeHeader.setHeader("Workflow Events Calendar");
			EdgeHeader.setSubHeader("Choose Filter");
			//}
			writer.println(ETSSearchCommon.getMasthead(EdgeHeader,
					Defines.AIC_WORKSPACE_TYPE));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());
			
			//sHeader.insert(0, EdgeHeader.printSubHeader());
			//sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
			//sHeader.insert(0, EdgeHeader.printETSBullsEyeHeader());
			//writer.println(sHeader.toString());
			
			
			writer
					.println("<form name=\"request\" method=\"post\" action=\"AICUserEventsServlet.wss\">");
			writer.println("<table summary=\"\" cellpadding=\"0\" "
					+ "cellspacing=\"0\" width=\"600\" border=\"0\" >");
			writer
					.println("<tr valign=\"top\"><td headers=\"user\" width=\"443\" "
							+ "valign=\"top\" class=\"small\">"
							+ "<table summary=\"\" width=\"100%\"><tr>"
							+ "<td headers=\"user_name\" width=\"60%\">"
							+ es.gIR_USERN
							+ "</td><td headers=\"sdate\" width=\"40%\" align=\"right\">"
							+ sDate + "</td></tr></table></td>");
			writer
					.println("<td headers=\"top_image_root\" width=\"7\"><img alt=\"\" src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"7\" height=\"1\" /></td>");
			writer
					.println("<td headers=\"secure\" class=\"small\" align=\"right\">"
							+ "<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" "
							+ "border=\"0\"><tr><td headers=\"icon_root\" "
							+ "align=\"right\"><img alt=\"\" src=\""
							+ Defines.ICON_ROOT
							+ "key.gif\" width=\"16\" height=\"16\" /></td>"
							+ "<td headers=\"secure_content\"  width=\"90\" align=\"right\">"
							+ "Secure content</td></tr></table></td></tr>");

			writer.println("<!-- Gray dotted line -->");

			writer.println("<tr>");

			writer
					.println("<td background=\""
							+ Defines.V11_IMAGE_ROOT
							+ "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			writer.println("<td width=\"1\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td width=\"1\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			writer.println("</tr>");
			writer.println("<!-- End Gray dotted line -->");

			writer.println("<tr><td width=\"300\" valign=\"top\">");
			userRequestFunctions.showUserEventsFilter();

			writer.println("</td>");
			writer.println("<td width=\"1\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td width=\"157\" valign=\"top\" align=\"right\">");
			showUserEventsCalendar(parameters);
			writer.println("</td>");
			writer.println("</tr>");
			String sETSOp = request.getParameter("etsop");

			if (sETSOp == null || sETSOp.trim().equals("")) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}
			if (sETSOp.trim().equalsIgnoreCase("caldetail")) {
				writer
				.println("<tr><td colspan=\"3\" width=\"433\" valign=\"top\">");
				displayTodaysCalendarMeetings(parameters);
				writer.println("</td></tr>");
				
				//
				writer
						.println("<tr><td colspan=\"3\" width=\"433\" valign=\"top\">");
				displayTodaysCalendarEvents(parameters);
				writer.println("</td></tr>");
			}

			writer.println("</table>");
			// default form variables
			writer.println("<input type=\"hidden\" name=\"linkid\" value=\""
					+ parameters.getLinkId() + "\" />");
			writer.println("<input type=\"hidden\" name=\"from\" value=\""
					+ from + "\" />");
			writer.println("</form>");

			// content ends
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e),
					"Error occurred Workflow Events Servlet");
		} catch (Exception e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e),
					"Error occurred Workflow Events Servlet");
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

	/**
	 * Method displayCalendar.
	 * 
	 * @param con
	 * @param request
	 * @param out
	 * @param sProjectId
	 * @param iTopCat
	 * @throws SQLException
	 * @throws Exception
	 */
	public static boolean showUserEventsCalendar(ETSParams params)
			throws SQLException, Exception {

		boolean bDisplayed = false;

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			String sLinkId = params.getLinkId();

			//get filter values
			String company = request.getParameter("client_company");
			if (company == null)
				company = "";
			String wf_type = request.getParameter("wf_type");
			if (wf_type == null)
				wf_type = "";
			String brand = request.getParameter("brand");
			if (brand == null)
				brand = "";
			String sce_sector = request.getParameter("sce_sector");
			if (sce_sector == null)
				sce_sector = "";
			String bus_sector = request.getParameter("bus_sector");
			if (bus_sector == null)
				bus_sector = "";
			bus_sector = URLEncoder.encode(bus_sector);

			boolean bAdmin = false;
			boolean bWorkflowAdmin = false;
			boolean bExecutive = false;
			boolean bOEMSales = false;
			boolean bEntitlement = true;
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,
					es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,
					edgeuserid, true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
				bWorkflowAdmin = true;
			} else if (userents
					.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				bOEMSales = true;
			} else if (!es.gDECAFTYPE.equals("I")) {
				// if the external user doesn't have entitlements
				if (!userents.contains(Defines.COLLAB_CENTER_ENTITLEMENT)) {
					bEntitlement = true;
				}
			}

			Hashtable hMeetingDates = getMeetingEventDates(params);

			bDisplayed = true;

			String sInDate = ETSUtils.checkNull(request
					.getParameter("showdate"));
			String sMeetId = ETSUtils.checkNull(request.getParameter("meetid"));

			Calendar cal = Calendar.getInstance();

			//String sInMonth = projBean.getSMonth();
			String sInMonth = request.getParameter("month");
			if (sInMonth == null) {
				sInMonth = "";
			} else {
				sInMonth = sInMonth.trim();
			}

			//String sInYear = projBean.getSYear();
			String sInYear = request.getParameter("year");
			;
			if (sInYear == null) {
				sInYear = "";
			} else {
				sInYear = sInYear.trim();
			}

			String sETSOp = request.getParameter("etsop");
			if (sETSOp == null) {
				sETSOp = "";
			} else {
				sETSOp = sETSOp.trim();
			}

			boolean bDisplayLinks = true;
			if (!sInMonth.trim().equals("") && !sInYear.trim().equals("")) {
				logger.debug("ETS Events Calendar : Calendar Month : "
						+ sInMonth);
				logger.debug("ETS Events Calendar : Calendar Year  : "
						+ sInYear);
				cal.set(Integer.parseInt(sInYear), Integer.parseInt(sInMonth),
						01);
			} else {
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 01);
			}

			String todayDate = "";
			int month = cal.get(Calendar.MONTH) + 1;

			if (month > 9) {
				todayDate = String.valueOf(month);
			} else {
				todayDate = "0" + String.valueOf(month);
			}

			int day = cal.get(Calendar.DAY_OF_MONTH);
			todayDate = todayDate + "/";
			if (day > 9) {
				todayDate = todayDate + String.valueOf(day);
			} else {
				todayDate = todayDate + "0" + String.valueOf(day);
			}

			int year = cal.get(Calendar.YEAR);
			todayDate = todayDate + "/" + String.valueOf(year);

			int iMaxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);

			int iDay = cal.get(Calendar.DAY_OF_WEEK);

			int iMonth = cal.get(Calendar.MONTH);

			out
					.println("<table summary=\"CALENDER\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			out.println("<tr><td headers=\"\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
			out.println("<tr>");
			out
					.println("<td align=\"center\" headers=\"\" class=\"tblue\" height=\"18\" width=\"150\"><b>Events Calendar</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out
					.println("<table summary=\"CALENDER1\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"\" class=\"tblue\" valign=\"top\" width=\"150\">");

			out
					.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\">");
			out
					.println("<tr style=\"background-color: #ffffff; color: #000000;\">");
			out.println("<td headers=\"\" valign=\"top\" width=\"150\">");

			out
					.println("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\"> ");
			out.println("<tr valign=\"middle\">");

			if (bDisplayLinks) {

				switch (iMonth) {

				case (0):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">January "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month=11&year="
									+ (year - 1)
									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector
									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year
									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector
									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (1):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">February "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" align=\"center\"  valign=\"middle\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (2):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">March "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (3):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">April "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (4):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">May "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (5):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">June "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (6):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">July "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (7):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">August "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (8):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">September "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (9):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">October "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (10):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">November "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth + 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				case (11):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">December "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\" align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month="
									+ (iMonth - 1)
									+ "&year="
									+ year

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "bk.gif\" height=\"16\" width=\"16\" alt=\"previous\" border=\"0\" /></a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICUserEventsServlet.wss?month=0&year="
									+ (year + 1)

									+ "&client_company="
									+ company
									+ "&wf_type="
									+ wf_type
									+ "&brand="
									+ brand
									+ "&sce_sector="
									+ sce_sector
									+ "&bus_sector="
									+ bus_sector

									+ "&meetid="
									+ sMeetId
									+ "&showdate="
									+ sInDate
									+ "&etsop="
									+ sETSOp
									+ "&linkid="
									+ sLinkId
									+ "\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" height=\"16\" width=\"16\" alt=\"next\" border=\"0\" /></a></td>");
					break;
				}
			} else {
				switch (iMonth) {

				case (0):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">January "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (1):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">February "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (2):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">March "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (3):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">April "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (4):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">May "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (5):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">June "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"   align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</a></td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (6):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">July "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (7):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">August "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (8):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">September "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (9):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">October "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (10):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">November "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				case (11):
					out
							.println("<td headers=\"\"  colspan=\"5\" style=\"background-color: #cccccc; color: #000000;\">December "
									+ year
									+ "</td><td headers=\"\"  width=\"21\" valign=\"middle\" align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td><td headers=\"\"  width=\"21\" valign=\"middle\"  align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
					break;
				}
			}
			out.println("</tr>");
			out.println("<tr valign=\"middle\">");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>S</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>M</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>T</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>W</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>T</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>F</b></td>");
			out
					.println("<td headers=\"\"  width=\"21\" align=\"center\" class=\"tgray\"><b>S</b></td>");
			out.println("</tr>");

			int iCount = 1;
			int iDayCount = 1;
			System.out.println("hMeetingDates:" + hMeetingDates.toString());
			for (int i = 1; i <= 42; i++) {

				if (iCount == 1) {
					out.println("<tr>");
				}

				if (i < iDay || iDayCount > iMaxDaysInMonth) {
					out
							.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;\">&nbsp;</td>");
				} else {

					String sDisplayDay = "";
					String sDisplayMonth = "";
					String sDisplayYear = "";

					if (iDayCount <= 9) {
						sDisplayDay = "0" + String.valueOf(iDayCount);
					} else {
						sDisplayDay = String.valueOf(iDayCount);
					}

					if (iMonth + 1 <= 9) {
						sDisplayMonth = "0" + String.valueOf(iMonth + 1);
					} else {
						sDisplayMonth = String.valueOf(iMonth + 1);
					}

					//System.out.println("date_str" + sDisplayMonth + "/" +
					// sDisplayDay + "/" + String.valueOf(year));

					String sAvailable = (String) hMeetingDates
							.get(sDisplayMonth + "/" + sDisplayDay + "/"
									+ String.valueOf(year));
					if (sAvailable == null || sAvailable.trim().equals("")) {
						sAvailable = "N";
					} else {
						sAvailable = sAvailable.trim();
					}

					//System.out.println("savailable:" + sAvailable);

					if (sAvailable.equalsIgnoreCase("Y")) {
						if (bDisplayLinks) {
							out
									.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #eeeeee; font-weight: normal;\"><a href=\""
											+ Defines.SERVLET_PATH
											+ "AICUserEventsServlet.wss?etsop=caldetail&linkid="
											+ sLinkId
											+ "&month="
											+ iMonth
											+ "&year="
											+ year

											+ "&client_company="
											+ company
											+ "&wf_type="
											+ wf_type
											+ "&brand="
											+ brand
											+ "&sce_sector="
											+ sce_sector
											+ "&bus_sector="
											+ bus_sector

											+ "&showdate="
											+ sDisplayMonth
											+ "/"
											+ sDisplayDay
											+ "/"
											+ year
											+ "\">" + iDayCount + "</a></td>");
						} else {
							out
									.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;  font-weight: normal;\">"
											+ iDayCount + "</td>");
						}
					} else {
						out
								.println("<td headers=\"\"  width=\"21\" align=\"center\" style=\"background-color: #cccccc; color: #000000;  font-weight: normal;\">"
										+ iDayCount + "</td>");
					}

					iDayCount++;
				}

				iCount++;

				if (iCount == 8) {
					out.println("</tr>");
					iCount = 1;
				}
			}
			out.println("</table>");

			out.println("</td></tr></table>");
			out.println("</td></tr></table>");
			//out.println("</td></tr></table>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		}

		return bDisplayed;

	}

	/**
	 * Method getMeetingEventDates.
	 * 
	 * @param con
	 * @param sProjectId
	 * @param sIRId
	 * @return Hashtable
	 * @throws SQLException
	 * @throws Exception
	 */
	public static Hashtable getMeetingEventDates(ETSParams params)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hDates = new Hashtable();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			boolean bAdmin = false;
			boolean bWorkflowAdmin = false;
			boolean bExecutive = false;
			boolean bOEMSales = false;
			boolean bEntitlement = true;
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,
					es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,
					edgeuserid, true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
				System.out.println("in collab admin");
			} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
				System.out.println("in workflow admin");
				bWorkflowAdmin = true;
			} else if (userents
					.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				bOEMSales = true;
			} else if (!es.gDECAFTYPE.equals("I")) {
				// if the external user doesn't have entitlements
				if (!userents.contains(Defines.COLLAB_CENTER_ENTITLEMENT)) {
					bEntitlement = true;
				}
			}
			//System.out.println("user entls vec:" + userents);
			Vector vProj = getUserProjects(params);
			String proj_sql_in_str = oem.edge.ets.fe.workflow.util.GenFunctions
					.buildInClause(vProj);
			if (proj_sql_in_str != null && proj_sql_in_str.equals("")) {
				proj_sql_in_str = "''";
			}
			if (bAdmin || bWorkflowAdmin) {
				// display all the meetings if admin or executive...
				// changed for 4.4.1
				sQuery
						.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE IN ('E', 'M') AND PROJECT_ID IN ( "
								+ proj_sql_in_str
								+ ") ORDER BY START_TIME,CALENDAR_TYPE WITH UR");
			} else {
				//sQuery.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM
				// ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID =
				// '" + proj.getProjectId() + "' AND (',' || INVITEES_ID || ','
				// LIKE '%," + es.gIR_USERN + ",%' OR SCHEDULED_BY = '" +
				// es.gIR_USERN + "') UNION ");
				sQuery
						.append("SELECT START_TIME,CALENDAR_TYPE,DURATION FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE IN ('E', 'M')  AND PROJECT_ID IN ( "
								+ proj_sql_in_str
								+ ") AND (',' || INVITEES_ID || ',' LIKE '%,"
								+ es.gIR_USERN
								+ ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '"
								+ es.gIR_USERN
								+ "') ORDER BY START_TIME,CALENDAR_TYPE WITH UR");
			}

			logger.debug("ETSEventsCalendar::getMeetingEventDates()::QUERY : "
					+ sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sDate = rs.getString("START_TIME");
				String sCalendarType = ETSUtils.checkNull(rs
						.getString("CALENDAR_TYPE"));
				int iDuration = rs.getInt("DURATION");

				if (sCalendarType.trim().equalsIgnoreCase("M")) {
					// meeting entry...
					//formatting in mm/dd/yyyy format and store as key...
					hDates.put(sDate.substring(5, 7) + "/"
							+ sDate.substring(8, 10) + "/"
							+ sDate.substring(0, 4), "Y");

				} else {
					// events entry...
					for (int i = 0; i < iDuration; i++) {

						Calendar cal = Calendar.getInstance();
						cal.clear();

						String sDay = sDate.substring(8, 10);
						int iDay = Integer.parseInt(sDay) + i;

						cal.set(Integer.parseInt(sDate.substring(0, 4)),
								Integer.parseInt(sDate.substring(5, 7)) - 1,
								iDay);

						String sMonth = "";
						int month = cal.get(Calendar.MONTH) + 1;
						if (month > 9) {
							sMonth = String.valueOf(month);
						} else {
							sMonth = "0" + String.valueOf(month);
						}

						String sCurrentDay = "";
						int day = cal.get(Calendar.DAY_OF_MONTH);
						if (day > 9) {
							sCurrentDay = String.valueOf(day);
						} else {
							sCurrentDay = "0" + String.valueOf(day);
						}

						String sYear = "";
						int year = cal.get(Calendar.YEAR);
						sYear = String.valueOf(year);

						hDates.put(sMonth + "/" + sCurrentDay + "/" + sYear,
								"Y");

					}

				}

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return hDates;
	}

	public static void displayTodaysCalendarMeetings(ETSParams params)
			throws SQLException, Exception {

		try {

			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			String sInDate = request.getParameter("showdate");

			String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");
			String sStartMonth = sTodaysDate.substring(0, 2);
			String sStartDate = sTodaysDate.substring(3, 5);
			String sStartYear = sTodaysDate.substring(6, 10);

			Vector vAlerts = getTodaysMeetings(params, sInDate);
			StringBuffer sbuf = new StringBuffer();

			out.println("<br />");
			out
					.println("<table summary=\"TODAY_MEETINGS\" cellspacing=\"1\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
			out.println("<tr>");
			out
					.println("<td headers=\"\"  colspan=\"9\" height=\"18\"><b>Calendar Meetings</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Company</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Workflow Type</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Brand</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>SCE Sector</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Business Sector</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Project Name</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Meeting Title</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Date Time</b></td>");
			//out.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Repeats</b></td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/"
							+ sTempDate.substring(8, 10) + "/"
							+ sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sHour1 = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10)
							sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10)
							sHour = "0" + sHour;
						sAMPM = "PM";
					}
					//for meeting this is duration of meeting in minutes
					int iDuration = calEntry.getIDuration();
					
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(calEntry.getSStartTime().getTime());
					c.add(Calendar.MINUTE, iDuration);
					SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
					
					String sEndTime = sdf.format(c.getTime());
					
					//repeat logic
					/*
					String repeat_id = calEntry.getSCalendarId();
					String repeat_cnt = DBHandler.getValue(con, "select count(repeat_id) from ets.ets_calendar where repeat_id = '" + repeat_id + "' with ur");
					String repeat_type = DBHandler.getValue(con, "select repeat_type from ets.ets_calendar where calendar_id = '" + calEntry.getSCalendarId() + "' with ur");
					
					if (repeat_type != null && repeat_type.equals("D")) {
						repeat_type = "daily";
					} else if (repeat_type != null && repeat_type.equals("W")) {
						repeat_type = "weekly";
					} else if (repeat_type != null && repeat_type.equals("M")) {
						repeat_type = "monthly";
					}*/
					
					//get Project details
					sbuf.setLength(0);
					sbuf
							.append("SELECT company, wf_type, brand, sce_sector, sector, project_name ");
					sbuf
							.append("FROM ets.ets_projects a, ets.wf_def b WHERE a.project_id = b.project_id ");
					sbuf.append("and a.project_id = '"
							+ calEntry.getSProjectId() + "' ");
					sbuf
							.append("ORDER BY company, wf_type, brand, sce_sector, sector, project_name WITH UR");
					Vector vProj = DBHandler.getVQueryResult(con, sbuf
							.toString(), 6);
					String company = "";
					String wf_type = "";
					String brand = "";
					String sce_sector = "";
					String bus_sector = "";
					String proj_name = "";

					if (vProj != null && vProj.size() > 0) {
						String vals[] = (String[]) vProj.elementAt(0);
						company = vals[0];
						wf_type = vals[1];
						brand = vals[2];
						sce_sector = vals[3];
						bus_sector = vals[4];
						proj_name = vals[5];
					}

					out.println("<tr>");
					out
							.println("<td headers=\"\"  colspan=\"9\"><img src=\""
									+ Defines.TOP_IMAGE_ROOT
									+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					if (i % 2 == 0)
						out.println("<tr valign=\"top\">");
					else
						out.println("<tr bgcolor=\"#dfe8ef\" valign=\"top\">");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ company + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ wf_type + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ brand + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ sce_sector + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ bus_sector + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ proj_name + " </td>");
					out
							.println("<td headers=\"\" width=\"160\" align=\"center\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Event\" border=\"0\" />");
					out.println("<img src=\"" + Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" />");
					out
							.println("<a href=\""
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=M&calid="
									+ calEntry.getSCalendarId()
									+ "\" target=\"new\" class=\"fbox\" onclick=\"window.open('"
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=M&calid="
									+ calEntry.getSCalendarId()
									+ "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('"
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=M&calid="
									+ calEntry.getSCalendarId()
									+ "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">"
									+ calEntry.getSSubject() + "</a>");
					if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
						out.println("[&nbsp;<span class=\"small\" style=\"color:#cc6600\">Cancelled</span>&nbsp;]");
					} 
					out.println("</td>");

					out
							.println("<td headers=\"\" width=\"16\" align=\"left\">");
					out.println("<span style=\"color:#666666\">");
					out.println("<b>" + sDate + "</b>");
					out.println("<br /><span class=\"small\">" + sHour + ":"
							+ sMin + sAMPM.toLowerCase() + " to " + sEndTime + "&nbsp;Eastern US</span>");
					out.println("</span>");
					out.println("</td>");
					/*
					out.println("<td headers=\"\" width=\"16\" align=\"left\">");
					out.println("Repeats " + repeat_type + " for " + repeat_cnt + " times");
					out.println("</td>");
					*/
					out.println("</tr>");

					boolean bDisplayEdit = true;

					long lCurrentDate = new Timestamp(System
							.currentTimeMillis()).getTime();
					long lMeetingDate = Timestamp.valueOf(
							sTempDate.substring(0, 4) + "-"
									+ sTempDate.substring(5, 7) + "-"
									+ sTempDate.substring(8, 10)
									+ " 23:59:00.000000000").getTime();

					if (lCurrentDate > lMeetingDate) {
						bDisplayEdit = false;
					}
				}

			} else {
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\">&nbsp;</td>");
				out.println("</tr>");
				out
						.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>There are no meetings on "
								+ sInDate + "</b></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\""
						+ Defines.TOP_IMAGE_ROOT
						+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out
						.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\""
								+ Defines.TOP_IMAGE_ROOT
								+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\""
						+ Defines.TOP_IMAGE_ROOT
						+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
			}

			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	public static void displayTodaysCalendarEvents(ETSParams params)
			throws SQLException, Exception {
		try {
			Connection con = params.getConnection();
			PrintWriter out = params.getWriter();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			ETSProj proj = params.getETSProj();
			HttpServletRequest request = params.getRequest();

			String sInDate = request.getParameter("showdate");

			String sTodaysDate = DatesArithmatic.TodaysDate("MM-DD-YYYY");
			String sStartMonth = sTodaysDate.substring(0, 2);
			String sStartDate = sTodaysDate.substring(3, 5);
			String sStartYear = sTodaysDate.substring(6, 10);

			Vector vAlerts = getTodaysEvents(params, sInDate);
			StringBuffer sbuf = new StringBuffer();

			out.println("<br />");
			out
					.println("<table summary=\"TODAY_EVENTS\" cellspacing=\"1\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
			out.println("<tr>");
			out
					.println("<td headers=\"\"  colspan=\"9\" height=\"18\"><b>Calendar Events</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Company</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Workflow Type</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Brand</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>SCE Sector</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Business Sector</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Project Name</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Event Title</b></td>");
			out
					.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Date Time</b></td>");
			//out.println("<td headers=\"\" height=\"18\" class=\"tblue\"><b>Duration</b></td>");
			out.println("</tr>");

			if (vAlerts != null && vAlerts.size() > 0) {

				for (int i = 0; i < vAlerts.size(); i++) {

					ETSCal calEntry = (ETSCal) vAlerts.elementAt(i);

					String sTempDate = calEntry.getSStartTime().toString();

					String sDate = sTempDate.substring(5, 7) + "/"
							+ sTempDate.substring(8, 10) + "/"
							+ sTempDate.substring(0, 4);
					String sTime = sTempDate.substring(11, 16);

					String sHour = sTempDate.substring(11, 13);
					String sHour1 = sTempDate.substring(11, 13);
					String sMin = sTempDate.substring(14, 16);
					String sAMPM = "AM";

					if (Integer.parseInt(sHour) == 0) {
						sHour = "12";
						sAMPM = "AM";
					} else if (Integer.parseInt(sHour) == 12) {
						sHour = String.valueOf(Integer.parseInt(sHour));
						if (Integer.parseInt(sHour) < 10)
							sHour = "0" + sHour;
						sAMPM = "PM";
					} else if (Integer.parseInt(sHour) > 12) {
						sHour = String.valueOf(Integer.parseInt(sHour) - 12);
						if (Integer.parseInt(sHour) < 10)
							sHour = "0" + sHour;
						sAMPM = "PM";
					}

					int iDuration = calEntry.getIDuration();
					
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis(calEntry.getSRepeatEnd().getTime());
					SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");					
					String sEndTime = sdf.format(c.getTime());

					//get Project details
					sbuf.setLength(0);
					sbuf
							.append("SELECT company, wf_type, brand, sce_sector, sector, project_name ");
					sbuf
							.append("FROM ets.ets_projects a, ets.wf_def b WHERE a.project_id = b.project_id ");
					sbuf.append("and a.project_id = '"
							+ calEntry.getSProjectId() + "' ");
					sbuf
							.append("ORDER BY company, wf_type, brand, sce_sector, sector, project_name WITH UR");
					Vector vProj = DBHandler.getVQueryResult(con, sbuf
							.toString(), 6);
					String company = "";
					String wf_type = "";
					String brand = "";
					String sce_sector = "";
					String bus_sector = "";
					String proj_name = "";

					if (vProj != null && vProj.size() > 0) {
						String vals[] = (String[]) vProj.elementAt(0);
						company = vals[0];
						wf_type = vals[1];
						brand = vals[2];
						sce_sector = vals[3];
						bus_sector = vals[4];
						proj_name = vals[5];
					}

					out.println("<tr>");
					out
							.println("<td headers=\"\"  colspan=\"9\"><img src=\""
									+ Defines.TOP_IMAGE_ROOT
									+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
					out.println("</tr>");

					if (i % 2 == 0)
						out.println("<tr valign=\"top\">");
					else
						out.println("<tr bgcolor=\"#dfe8ef\" valign=\"top\">");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ company + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ wf_type + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ brand + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ sce_sector + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ bus_sector + " </td>");
					out
							.println("<td headers=\"\" width=\"16\" align=\"left\"> "
									+ proj_name + " </td>");
					out
							.println("<td headers=\"\" width=\"160\" align=\"center\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Event\" border=\"0\" />");
					out.println("<img src=\"" + Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" />");
					out
							.println("<a href=\""
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=E&calid="
									+ calEntry.getSCalendarId()
									+ "\" target=\"new\" class=\"fbox\" onclick=\"window.open('"
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=E&calid="
									+ calEntry.getSCalendarId()
									+ "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('"
									+ Defines.SERVLET_PATH
									+ "ETSCalDisplayServlet.wss?proj="
									+ calEntry.getSProjectId()
									+ "&caltype=E&calid="
									+ calEntry.getSCalendarId()
									+ "','Calendar','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">"
									+ calEntry.getSSubject() + "</a>");
						if (calEntry.getSCancelFlag().trim().equalsIgnoreCase("Y")) {
							out.println("[&nbsp;<span class=\"small\" style=\"color:#cc6600\">Cancelled</span>&nbsp;]");
						} 
					out.println("</td>");

					out
							.println("<td headers=\"\" width=\"16\" align=\"left\">");
					out.println("<span style=\"color:#666666\">");
					out.println("<b>" + sDate + "</b>");
					out.println("<br /><span class=\"small\">" + sHour + ":"
							+ sMin + sAMPM.toLowerCase() + " to " + sEndTime + "&nbsp;Eastern US</span>");
					out.println("</span>");
					out.println("</td>");
					
					/*
					out.println("<td headers=\"\" width=\"16\" align=\"left\">");
					out.println("This event repeats for "
							+ String.valueOf(iDuration) + " days");
					out.println("</td>");
					*/
					out.println("</tr>");

					boolean bDisplayEdit = true;

					long lCurrentDate = new Timestamp(System
							.currentTimeMillis()).getTime();
					long lMeetingDate = Timestamp.valueOf(
							sTempDate.substring(0, 4) + "-"
									+ sTempDate.substring(5, 7) + "-"
									+ sTempDate.substring(8, 10)
									+ " 23:59:00.000000000").getTime();

					if (lCurrentDate > lMeetingDate) {
						bDisplayEdit = false;
					}
				}
			} else {
				out.println("<tr>");
				out.println("<td headers=\"\"  colspan=\"2\">&nbsp;</td>");
				out.println("</tr>");
				out
						.println("<td headers=\"\"  colspan=\"2\" height=\"18\" ><b>There are no events on "
								+ sInDate + "</b></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\""
						+ Defines.TOP_IMAGE_ROOT
						+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out
						.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\""
								+ Defines.TOP_IMAGE_ROOT
								+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\""
						+ Defines.TOP_IMAGE_ROOT
						+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
			}

			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	public static Vector getTodaysMeetings(ETSParams params, String sDate)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();

			boolean bAdmin = params.isSuperAdmin();
			boolean bExecutive = params.isExecutive();
			boolean bWorkflowAdmin = false;
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,
					es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,
					edgeuserid, true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
				bWorkflowAdmin = true;
			}

			Vector vProj = getUserProjects(params);
			String proj_sql_in_str = oem.edge.ets.fe.workflow.util.GenFunctions
					.buildInClause(vProj);
			if (proj_sql_in_str != null && proj_sql_in_str.equals("")) {
				proj_sql_in_str = "''";
			}

			if (bAdmin || bWorkflowAdmin) {

				sQuery
						.append("SELECT PROJECT_ID,CALENDAR_ID,SCHEDULE_DATE,"
								+ "SCHEDULED_BY,START_TIME,DURATION,SUBJECT,"
								+ "DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,"
								+ "CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,"
								+ "PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID "
								+ "FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' ");
				sQuery.append(" AND PROJECT_ID IN ( " + proj_sql_in_str
						+ ") AND ");
				sQuery.append("DATE(START_TIME) = '" + sDate
						+ "' ORDER BY START_TIME WITH UR ");
			} else {
				sQuery
						.append("SELECT PROJECT_ID,CALENDAR_ID,SCHEDULE_DATE,"
								+ "SCHEDULED_BY,START_TIME,DURATION,SUBJECT,"
								+ "DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,"
								+ "CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,"
								+ "PASS_CODE,REPEAT_TYPE,REPEAT_ID,REPEAT_END,FOLDER_ID "
								+ "FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' ");
				sQuery.append(" AND PROJECT_ID IN ( " + proj_sql_in_str
						+ ") AND ");
				sQuery.append(" (',' || INVITEES_ID || ',' LIKE '%,"
						+ es.gIR_USERN + ",%' OR SCHEDULED_BY = '"
						+ es.gIR_USERN + "') AND ");
				sQuery.append("DATE(START_TIME) = '" + sDate
						+ "' ORDER BY START_TIME WITH UR ");
			}

			logger.debug("UserEventsServlet::getTodaysMeetings()::QUERY : "
					+ sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {
				String sProjectID = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				//String sProjectID = proj.getProjectId();
				String sCalType = "m";
				Timestamp tScheduleDate = rs.getTimestamp("SCHEDULE_DATE");
				String sScheduleBy = ETSUtils.checkNull(rs
						.getString("SCHEDULED_BY"));
				Timestamp tStartTime = rs.getTimestamp("START_TIME");
				int iDuration = rs.getInt("DURATION");
				String sSubject = ETSUtils.checkNull(rs.getString("SUBJECT"));
				String sDescription = ETSUtils.checkNull(rs
						.getString("DESCRIPTION"));
				String sInvitees = ETSUtils.checkNull(rs
						.getString("INVITEES_ID"));
				String sCCList = ETSUtils.checkNull(rs.getString("CC_LIST"));
				String sWebFlag = ETSUtils.checkNull(rs
						.getString("WEB_CONF_FLAG"));
				String sCancelFlag = ETSUtils.checkNull(rs
						.getString("CANCEL_FLAG"));
				String sEmailFlag = ETSUtils.checkNull(rs
						.getString("EMAIL_FLAG"));
				String sIBMOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));
				String sCallIn = ETSUtils.checkNull(rs
						.getString("CALLIN_NUMBER"));
				String sPassCode = ETSUtils
						.checkNull(rs.getString("PASS_CODE"));
				String sRepeatType = ETSUtils.checkNull(rs
						.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils
						.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");
				int iFolderId = rs.getInt("FOLDER_ID");

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectID);
				cal.setSCalType(sCalType);
				cal.setSScheduleDate(tScheduleDate);
				cal.setSScheduleBy(sScheduleBy);
				cal.setSStartTime(tStartTime);
				cal.setIDuration(iDuration);
				cal.setSSubject(sSubject);
				cal.setSDescription(sDescription);
				cal.setSInviteesID(sInvitees);
				cal.setSCCList(sCCList);
				cal.setSWebFlag(sWebFlag);
				cal.setSCancelFlag(sCancelFlag);
				cal.setSEmailFlag(sEmailFlag);
				cal.setSIBMOnly(sIBMOnly);
				cal.setSCallIn(sCallIn);
				cal.setSPass(sPassCode);
				cal.setSRepeatType(sRepeatType);
				cal.setSRepeatID(sRepeatID);
				cal.setSRepeatEnd(tRepeatEnd);
				cal.setFolderId(iFolderId);

				vEvents.addElement(cal);

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vEvents;

	}

	public static Vector getTodaysEvents(ETSParams params, String sDate)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Vector vEvents = new Vector();

		try {

			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			EdgeAccessCntrl es = params.getEdgeAccessCntrl();
			boolean bAdmin = params.isSuperAdmin();
			boolean bExecutive = params.isExecutive();
			boolean bWorkflowAdmin = false;
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,
					es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,
					edgeuserid, true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
			} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
				bWorkflowAdmin = true;
			}

			Vector vProj = getUserProjects(params);
			String proj_sql_in_str = oem.edge.ets.fe.workflow.util.GenFunctions
					.buildInClause(vProj);
			if (proj_sql_in_str != null && proj_sql_in_str.equals("")) {
				proj_sql_in_str = "''";
			}

			if (bAdmin || bWorkflowAdmin) {
				sQuery.append("SELECT PROJECT_ID,CALENDAR_ID,SCHEDULE_DATE,"
						+ "SCHEDULED_BY,START_TIME,DURATION,SUBJECT,"
						+ "DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,"
						+ "CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,"
						+ "PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR "
						+ "WHERE CALENDAR_TYPE = 'E' ");
				sQuery.append(" AND PROJECT_ID IN ( " + proj_sql_in_str
						+ ") AND ");
				sQuery.append("'" + sDate + "' BETWEEN DATE(START_TIME) AND "
						+ "DATE(START_TIME + (DURATION -1) DAYS) WITH UR");
			} else {
				sQuery.append("SELECT PROJECT_ID,CALENDAR_ID,SCHEDULE_DATE,"
						+ "SCHEDULED_BY,START_TIME,DURATION,SUBJECT,"
						+ "DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,"
						+ "CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,CALLIN_NUMBER,"
						+ "PASS_CODE,REPEAT_END FROM ETS.ETS_CALENDAR "
						+ "WHERE CALENDAR_TYPE = 'E' ");

				sQuery.append("AND PROJECT_ID IN ( " + proj_sql_in_str
						+ ") AND (',' || INVITEES_ID || ',' LIKE '%,"
						+ es.gIR_USERN
						+ ",%' OR INVITEES_ID LIKE 'ALL' OR SCHEDULED_BY = '"
						+ es.gIR_USERN + "') AND ");
				sQuery.append("'" + sDate + "' BETWEEN DATE(START_TIME) AND "
						+ "DATE(START_TIME + (DURATION -1) DAYS) WITH UR");
			}

			logger.debug("ETSEventsCalendar::getTodayEvents()::QUERY : "
					+ sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sProjectID = ETSUtils.checkNull(rs
						.getString("PROJECT_ID"));
				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				//String sProjectID = proj.getProjectId();
				String sCalType = "E";
				Timestamp tScheduleDate = rs.getTimestamp("SCHEDULE_DATE");
				String sScheduleBy = ETSUtils.checkNull(rs
						.getString("SCHEDULED_BY"));
				Timestamp tStartTime = rs.getTimestamp("START_TIME");
				int iDuration = rs.getInt("DURATION");
				String sSubject = ETSUtils.checkNull(rs.getString("SUBJECT"));
				String sDescription = ETSUtils.checkNull(rs
						.getString("DESCRIPTION"));
				String sInvitees = ETSUtils.checkNull(rs
						.getString("INVITEES_ID"));
				String sCCList = ETSUtils.checkNull(rs.getString("CC_LIST"));
				String sWebFlag = ETSUtils.checkNull(rs
						.getString("WEB_CONF_FLAG"));
				String sCancelFlag = ETSUtils.checkNull(rs
						.getString("CANCEL_FLAG"));
				String sEmailFlag = ETSUtils.checkNull(rs
						.getString("EMAIL_FLAG"));
				String sIBMOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));
				String sCallIn = ETSUtils.checkNull(rs
						.getString("CALLIN_NUMBER"));
				String sPass = ETSUtils.checkNull(rs.getString("PASS_CODE"));
				Timestamp tRepEndTime = rs.getTimestamp("REPEAT_END");
				

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectID);
				cal.setSCalType(sCalType);
				cal.setSScheduleDate(tScheduleDate);
				cal.setSScheduleBy(sScheduleBy);
				cal.setSStartTime(tStartTime);
				cal.setIDuration(iDuration);
				cal.setSSubject(sSubject);
				cal.setSDescription(sDescription);
				cal.setSInviteesID(sInvitees);
				cal.setSCCList(sCCList);
				cal.setSWebFlag(sWebFlag);
				cal.setSCancelFlag(sCancelFlag);
				cal.setSEmailFlag(sEmailFlag);
				cal.setSIBMOnly(sIBMOnly);
				cal.setSCallIn(sCallIn);
				cal.setSPass(sPass);
				cal.setSRepeatEnd(tRepEndTime);

				vEvents.addElement(cal);

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vEvents;

	}

	public static Vector getUserProjects(ETSParams params) throws SQLException,
			Exception {

		StringBuffer sQuery = new StringBuffer();

		Connection con = params.getConnection();
		EdgeAccessCntrl es = params.getEdgeAccessCntrl();
		HttpServletRequest request = params.getRequest();

		String sUserId = es.gIR_USERN;

		//get filter values
		String company = request.getParameter("client_company");
		String wf_type = request.getParameter("wf_type");
		String brand = request.getParameter("brand");
		String sce_sector = request.getParameter("sce_sector");
		String bus_sector = request.getParameter("bus_sector");

		boolean bAdmin = false;
		boolean bWorkflowAdmin = false;
		String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con, es.gIR_USERN);
		Vector userents = AccessCntrlFuncs.getUserEntitlements(con, edgeuserid,
				true, true);

		if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
			bAdmin = true;
		} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
			bWorkflowAdmin = true;
		}

		if (bAdmin || bWorkflowAdmin) {
			sQuery.append("SELECT distinct a.PROJECT_ID FROM "
					+ "ETS.ETS_PROJECTS a, ETS.WF_DEF b "
					+ "WHERE a.project_id = b.project_id ");

			if (wf_type != null && !wf_type.equals("ALL")
					&& !wf_type.equals(""))
				sQuery.append("and wf_type = '" + wf_type + "' ");
			if (company != null && !company.equals("ALL")
					&& !company.equals(""))
				sQuery.append("and company = '" + company + "' ");
			if (brand != null && !brand.equals("ALL") && !brand.equals(""))
				sQuery.append("and brand = '" + brand + "' ");
			if (sce_sector != null && !sce_sector.equals("ALL")
					&& !sce_sector.equals(""))
				sQuery.append("and sce_sector = '" + sce_sector + "' ");
			if (bus_sector != null && !bus_sector.equals("ALL")
					&& !bus_sector.equals(""))
				sQuery.append("and sector = '" + bus_sector + "' ");

			sQuery.append("AND UCASE(PROJECT_OR_PROPOSAL)='P' "
					+ "AND PARENT_ID='0' AND PROJECT_TYPE = 'AIC' "
					+ "AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' "
					//+ " AND a.PROJECT_ID IN (SELECT PROJECT_ID FROM "
					//+ "ETS.ETS_USER_WS WHERE USER_ID = '" + sUserId + "')"
					+ " ORDER BY 1 with ur");
		} else {
			sQuery.append("SELECT distinct a.PROJECT_ID FROM "
					+ "ETS.ETS_PROJECTS a, ETS.WF_DEF b "
					+ "WHERE a.project_id = b.project_id ");

			if (wf_type != null && !wf_type.equals("ALL")
					&& !wf_type.equals(""))
				sQuery.append("and wf_type = '" + wf_type + "' ");
			if (company != null && !company.equals("ALL")
					&& !company.equals(""))
				sQuery.append("and company = '" + company + "' ");
			if (brand != null && !brand.equals("ALL") && !brand.equals(""))
				sQuery.append("and brand = '" + brand + "' ");
			if (sce_sector != null && !sce_sector.equals("ALL")
					&& !sce_sector.equals(""))
				sQuery.append("and sce_sector = '" + sce_sector + "' ");
			if (bus_sector != null && !bus_sector.equals("ALL")
					&& !bus_sector.equals(""))
				sQuery.append("and sector = '" + bus_sector + "' ");

			sQuery.append("AND UCASE(PROJECT_OR_PROPOSAL)='P' "
					+ "AND PARENT_ID='0' AND PROJECT_TYPE = 'AIC' AND "
					+ "a.PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
					+ "ETS.ETS_USERS WHERE USER_ID = '" + sUserId
					+ "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED
					+ "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE
					+ "' AND a.PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
					+ "ETS.ETS_USERS WHERE USER_ID = '" + sUserId
					+ "') ORDER BY 1 with ur");
		}

		return DBHandler.getValues(con, sQuery.toString());

	}
}
