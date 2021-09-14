/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
/*                                                                           */
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
package oem.edge.ets.fe.aic.metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Calendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

public class AICMetricsServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";
	static private final String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec" };

	protected static ETSDatabaseManager databaseManager = new ETSDatabaseManager();
	private String mailhost;
	

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		
		boolean bIsOwner;
		PrintWriter writer = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

		Hashtable params;

		StringBuffer sHeader = new StringBuffer("");

		int topCatId = 0;
		ETSCat topCat;
		ETSCat subCat;
		ETSCat currentCat;
		ETSUser user;
		String sLink;

		try {

			conn = ETSDBUtils.getConnection();
			System.out.println("After ETS DB Connection");
			if (!es.GetProfile(response, request, conn)) {
				return;
			}
			System.out.println("***USERID=" + es.gIR_USERN);

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID());
				sLink = prop.getLinkID();
			}

			String project_type = getParameter(request, Defines.ETS_PROJ_VAR);
			if (project_type == null || project_type.equals(""))
				project_type = "AIC";

       		boolean bAdmin = false;
	 		boolean bExecutive = false;
			boolean bEntitlement = false;
			boolean bOemSales = false;

			bIsOwner = AICMetricsDAO.isAnOwner(es.gIR_USERN, project_type);
			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(conn,es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(conn,edgeuserid,true, true);
			//WORKFLOW_ADMIN in the first "else if" clause below added by KP for AIC Workflow
			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				bAdmin = true;
				bEntitlement = true;
				bOemSales = true;
				bIsOwner = true;  // Admin is by default owner of all WS
			} else if (userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) ||
					userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) ||
					userents.contains(Defines.WORKFLOW_ADMIN) ||
					userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				bEntitlement = true;
				bOemSales = true;
			} else if (!es.gDECAFTYPE.equals("I")) { // Redirecting to the Landing page if the user doesn't have correct entitlements
				bEntitlement = false;
				response.sendRedirect("AICConnectServlet.wss");
				return;
			} else if (!bIsOwner){
				response.sendRedirect("AICUnAuthorizedServlet.wss");
				return;
			}

			//boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD");
			//boolean bExecutive = es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD");
			//boolean bEntitlement = es.Qualify(Defines.ETS_ENTITLEMENT, "tg_member=MD");


			System.out.println(es.gIR_USERN + "  " + bAdmin + " " + bExecutive + "  " + bIsOwner);


			String projectidStr = getParameter(request, "project_id");
			if (projectidStr == null || projectidStr.equals("")) {
				projectidStr = "metrics";
			}

			ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);

			AICMetricsFunctions metFuncs = new AICMetricsFunctions();

			///// download option/////
			boolean optionDown = false;
			String optionDown1 = request.getParameter("downloadopt"); // get the download option
			String optionDown2 = request.getParameter("downloadopt.x"); // get the download option
			String optionDown3 = request.getParameter("downloadopt.y"); // get the download option
			if (optionDown1 != null || optionDown2 != null || optionDown3 != null)
				optionDown = true;

			///// print option/////
			boolean optionPrint = false;
			String optionPrint1 = request.getParameter("printopt"); // get the download option
			String optionPrint2 = request.getParameter("printopt.x"); // get the download option
			String optionPrint3 = request.getParameter("printopt.y"); // get the download option
			if (optionPrint1 != null || optionPrint2 != null || optionPrint3 != null) {
				System.out.println("print");
				optionPrint = true;
			}
			String optionNoPrint1 = request.getParameter("noprintopt"); // get the download option
			String optionNoPrint2 = request.getParameter("noprintopt.x"); // get the download option
			String optionNoPrint3 = request.getParameter("noprintopt.y"); // get the download option
			if (optionNoPrint1 != null || optionNoPrint2 != null || optionNoPrint3 != null) {
				System.out.println("noprint");
				optionPrint = false;
			}

			if (!optionDown) {
				System.out.println("In download null");
			} else {
				if (optionDown) {
					// download the data
					ETSParams parameters = new ETSParams();
					parameters.setConnection(conn);
					parameters.setEdgeAccessCntrl(es);
					parameters.setETSProj(proj);
					parameters.setRequest(request);
					parameters.setResponse(response);
					parameters.setTopCat(topCatId);
					parameters.setWriter(writer);
					parameters.setLinkId(sLink);
					parameters.setExecutive(bExecutive);
					parameters.setSuperAdmin(bAdmin);
					metFuncs.setETSParams(parameters);
					AICMetricsResultObj obj = metFuncs.getReportPageInfo();

					StringBuffer sb_csv = downloadReportPage(obj);
					response.setHeader("Content-disposition", "attachment; filename=" + obj.getEncReportName() + ".csv");
					//response.setHeader("Content-disposition","attachment; filename=metrics.csv");
					response.setHeader("Content-Type", "application/octet-stream");
					//response.setContentType("application/csv");
					response.setContentLength(sb_csv.length());
					PrintWriter out = response.getWriter();
					out.println(sb_csv.toString());
					out.close();
					out.flush();
					return;
				}
			}

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPopUp("J");

			// for help...
			ETSProperties properties = new ETSProperties();

			if (properties.displayHelp() == true) {
				EdgeHeader.setPageHelp(Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=" + proj.getProjectId());
			}
			// end of help..
			EdgeHeader.setPageTitle("Collaboration Center Metrics");
			EdgeHeader.setHeader("Collaboration Center Metrics");
			EdgeHeader.setSubHeader("Workspace for " + proj.getName());

			Vector graphTabs = ETSDatabaseManager.getTopCats(proj.getProjectId());

			if (graphTabs.size() == 0) {
				sHeader.append("There are no folders associated with this project.");
			} else {

				String topCatStr = getParameter(request, "tc");
				if (!topCatStr.equals("")) {
					topCatId = (new Integer(topCatStr)).intValue();
				} else {
					topCatId = ((ETSCat) graphTabs.elementAt(0)).getId();
					topCatStr = String.valueOf(topCatId);
				}

				ETSProjectInfoBean projBean = (ETSProjectInfoBean) request.getSession(false).getAttribute("ETSProjInfo");

				if (projBean == null || !projBean.isLoaded()) {
					projBean = ETSUtils.getProjInfoBean(conn);
					request.getSession(false).setAttribute("ETSProjInfo", projBean);
				}

				topCat = (ETSCat) ETSDatabaseManager.getCat(topCatId);

				ETSParams parameters = new ETSParams();
				parameters.setConnection(conn);
				parameters.setEdgeAccessCntrl(es);
				parameters.setETSProj(proj);
				parameters.setRequest(request);
				parameters.setResponse(response);
				parameters.setTopCat(topCatId);
				parameters.setWriter(writer);
				parameters.setLinkId(sLink);
				parameters.setProjBeanInfo(projBean);
				parameters.setCurrentTabName(topCat.getName());
				parameters.setExecutive(bExecutive);
				parameters.setSuperAdmin(bAdmin);

				currentCat = topCat;

				sHeader.insert(0, EdgeHeader.printSubHeader());
				if (!optionPrint) {
					sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
				}
				sHeader.insert(0, EdgeHeader.printETSBullsEyeHeader(projectidStr, proj.getName(), sLink));

				// top table to define the content and right sides..
				sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				sHeader.append("<tr valign=\"top\"><td headers=\"col1\" width=\"443\" valign=\"top\">");

				sHeader.append(createGraphTabs(graphTabs, topCatId, sLink, conn));
				writer.println(sHeader.toString());

				String option = "";
				option = request.getParameter("option"); // get the option
				if (option == null || option.equals("")) {
					option = "list";
				} // default no-option to list

				metFuncs.setETSParams(parameters);
				// list all the available reports
				if (option.equals("list")) {
					AICMetricsResultObj obj = metFuncs.getReportList();
					StringBuffer sb = printReportList(parameters, obj, bIsOwner, bOemSales);
					writer.println(sb.toString());
				} else if (option.equals("report")) {
					AICMetricsResultObj obj = metFuncs.getReportPageInfo();
					StringBuffer sb = printReportPage(obj, optionPrint, sLink, project_type);
					writer.println(sb.toString());
				}

				writer.println("</td>");
				writer.println("</tr>");
				writer.println("</table>");

				writer.println(EdgeHeader.printBullsEyeFooter());

			}

		} catch (SQLException e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on E&TS Connect.");
		} catch (Exception e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer, ETSErrorCodes.getErrorCode(e), "Error occurred on IBM E&TS Connect.");
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}
	}

	/**
	 * Method getCatName.
	 * @param conn
	 * @param projectidStr
	 * @param curCatStr
	 * @return String
	 */
	private String getCatName(Connection conn, String curCatStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sCatName = "";

		try {

			sQuery.append("SELECT CAT_NAME FROM ETS.ETS_CAT WHERE CAT_ID=" + curCatStr + " for READ ONLY");

			SysLog.log(SysLog.DEBUG, "ETSProjectsServlet::getCatName()", "QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {
				sCatName = ETSUtils.checkNull(rs.getString("CAT_NAME"));
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sCatName;

	}

	public static StringBuffer createGraphTabs(Vector graphTabs, int topCat, String sLink, Connection conn) {
		return ETSHeaderFooter.createGraphTabs(graphTabs,topCat,sLink,conn);
		
	}	
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			mailhost = Global.mailHost;
			System.out.println("Using " + mailhost);
			if (mailhost == null) {
				mailhost = "us.ibm.com";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);

		if (value == null) {
			return "";
		} else {
			return value;
		}
	}

	// ======================================================
	private StringBuffer printReportList(ETSParams parameters, AICMetricsResultObj obj, boolean bIsOwner, boolean bOemSales) {
		StringBuffer buf = new StringBuffer();
		String project_type = parameters.getRequest().getParameter(Defines.ETS_PROJ_VAR);
		buf.append("<table cellpadding=\"0\" cellspacing=\"2\" width=\"600\" border=\"0\">");

		boolean hasPrintedTitleRow = false;

		hasPrintedTitleRow = false;
		AICMetricsListValue[] palist = obj.getProjectActivityList();
		for (int i = 0; i < palist.length; i++) {
			//if(parameters.isSuperAdmin || !AICMetricsList.isAdminRestricted(i,palist)){
			if (canAccess(parameters, i, palist) && bIsOwner) {
				if (!hasPrintedTitleRow) {
					buf.append("<tr><td headers=\"col16\" class=\"tdblue\" colspan=\"2\" height=\"18\">&nbsp;Project Activity</td></tr>");
					buf.append("<tr><td headers=\"col17\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					hasPrintedTitleRow = true;
				}
				String link = AICMetricsList.getLink(i, palist) + "&" + Defines.ETS_PROJ_VAR + "=" + Defines.AIC_WORKSPACE_TYPE;
				buf.append("<tr><td headers=\"col18\" valign=\"top\" align=\"left\"><a href=\"" + link + "\">" + AICMetricsList.getTitle(i, palist) + "</a></td>");
				buf.append("<td headers=\"col19\" valign=\"top\" align=\"left\">" + AICMetricsList.getDescription(i, palist) + "</td></tr>");
			}
		}
		if (hasPrintedTitleRow)
			buf.append("<tr><td headers=\"col20\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

		hasPrintedTitleRow = false;
		AICMetricsListValue[] salist = obj.getSAList();
		for (int i = 0; i < salist.length; i++) {

			//if(parameters.isSuperAdmin || !AICMetricsList.isAdminRestricted(i,salist)){
			if (canAccess(parameters, i, salist)) {
				if (!hasPrintedTitleRow) {
					buf.append("<tr><td headers=\"col21\" class=\"tdblue\" colspan=\"2\" height=\"18\">&nbsp;Super Admin Reports</td></tr>");
					buf.append("<tr><td headers=\"col22\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					hasPrintedTitleRow = true;
				}
				String link = AICMetricsList.getLink(i, salist) + "&" + Defines.ETS_PROJ_VAR + "=" + Defines.AIC_WORKSPACE_TYPE;
				buf.append("<tr><td headers=\"col23\" valign=\"top\" align=\"left\"><a href=\"" + link + "\">" + AICMetricsList.getTitle(i, salist) + "</a></td>");
				buf.append("<td headers=\"col24\" valign=\"top\" align=\"left\">" + AICMetricsList.getDescription(i, salist) + "</td></tr>");
			}
		}
		if (hasPrintedTitleRow)
			buf.append("<tr><td headers=\"col25\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

		hasPrintedTitleRow = false;
		AICMetricsListValue[] clientreportlist = obj.getClientReportsList();
		for (int i = 0; i < clientreportlist.length; i++) {

			//if(parameters.isSuperAdmin || !AICMetricsList.isAdminRestricted(i,salist)){
			if (canAccess(parameters, i, clientreportlist) || bOemSales ) {
				if (!hasPrintedTitleRow) {
					buf.append("<tr><td headers=\"col26\" class=\"tdblue\" colspan=\"2\" height=\"18\">&nbsp;Client Contact Reporting</td></tr>");
					buf.append("<tr><td headers=\"col27\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					hasPrintedTitleRow = true;
				}
				String link = AICMetricsList.getLink(i, clientreportlist) + "&" + Defines.ETS_PROJ_VAR + "=" + Defines.AIC_WORKSPACE_TYPE;
				buf.append("<tr><td headers=\"col28\" valign=\"top\" align=\"left\"><a href=\"" + link + "\">" + AICMetricsList.getTitle(i, clientreportlist) + "</a></td>");
				buf.append("<td headers=\"col29\" valign=\"top\" align=\"left\">" + AICMetricsList.getDescription(i, clientreportlist) + "</td></tr>");
			}
		}
		if (hasPrintedTitleRow)
			buf.append("<tr><td headers=\"col30\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
		// added for workflow Report
		
		hasPrintedTitleRow = false;
		AICMetricsListValue[] salesreportlist = obj.getSalesreportslist();
		for (int i = 0; i < salesreportlist.length; i++) {

			//if(parameters.isSuperAdmin || !AICMetricsList.isAdminRestricted(i,salist)){
			if (canAccess(parameters, i, salesreportlist) || bOemSales ) {
				if (!hasPrintedTitleRow) {
					buf.append("<tr><td headers=\"col26\" class=\"tdblue\" colspan=\"2\" height=\"18\">&nbsp;Sales Management Reporting</td></tr>");
					buf.append("<tr><td headers=\"col27\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					hasPrintedTitleRow = true;
				}
				String link = AICMetricsList.getLink(i, salesreportlist) + "&" + Defines.ETS_PROJ_VAR + "=" + Defines.AIC_WORKSPACE_TYPE;
				buf.append("<tr><td headers=\"col28\" valign=\"top\" align=\"left\"><a href=\"" + link + "\">" + AICMetricsList.getTitle(i, salesreportlist) + "</a></td>");
				buf.append("<td headers=\"col29\" valign=\"top\" align=\"left\">" + AICMetricsList.getDescription(i, salesreportlist) + "</td></tr>");
			}
		}
		if (hasPrintedTitleRow)
			buf.append("<tr><td headers=\"col30\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
		
		// code for workflow Ends here
		
		
		buf.append("</table>");
		// Print back to homepage link
		buf.append("<table><tr><td>&nbsp;&nbsp;</td></tr></table>");
		buf.append("<table><tr><td>");
		buf.append("<td headers=\"col36\" align=\"right\" valign=\"middle\"><a href=\"AICMetricsServlet.wss?linkid=" + parameters.getLinkId() + "\">");
		buf.append("<img src=\"" + Defines.ICON_ROOT + "bk_c.gif\"  border=\"0\"  height=\"16\" width=\"16\" alt=\"Back\" /></a></td>");
		buf.append("<td headers=\"col37\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"2\" height=\"1\" alt=\"\" /></td>");
		buf.append("<td headers=\"col38\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"AICConnectServlet.wss?linkid=" + parameters.getLinkId() + "\">Go to Collaboration center main page</a></td>");
		buf.append("</tr></table>");

		return buf;
	}

	private StringBuffer printReportPage(AICMetricsResultObj obj, boolean printOpt, String linkid, String project_type) {
		StringBuffer buf = new StringBuffer();

		buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td headers=\"col39\" align=\"right\"><span style=\"color:#ff3333;\"><b>IBM Confidential</b></span></td></tr>");
		buf.append("</table>");

		buf.append("<form action=\"AICMetricsServlet.wss#res\" method=\"post\" name=\"MetricsForm\">");
		buf.append("<input type=\"hidden\" name=\"option\" value=\"report\" />");
		buf.append("<input type=\"hidden\" name=\"reportid\" value=\"" + obj.getReportId() + "\" />");
		buf.append("<input type=\"hidden\" name=\"option2\" value=\"search\" />");
		buf.append("<input type=\"hidden\" name=\"" + Defines.ETS_PROJ_VAR + "\" value=\"" + project_type + "\" />");
		buf.append(printReportTitle(obj));
		buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf = printFilters(buf, obj);
		buf.append(printButtons(printOpt, linkid));
		buf.append(printColumns(obj));
		buf.append("</table>");
		buf.append("</form>");

		buf = printSearchResults(buf, obj);

		return buf;
	}

	private StringBuffer printReportTitle(AICMetricsResultObj obj) {
		StringBuffer b = new StringBuffer();
		b.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		b.append("<tr bgcolor=\"#ffff99\"><td headers=\"col40\" height=\"20\"\"><span class=\"greytext\">&nbsp;<b>Report:</b>");
		b.append("<img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"4\" height=\"1\" alt=\"\" />");
		b.append("<span style=\"color:#006666;\"> <b>" + obj.getReportName() + "</b> </span></td></tr>");
		b.append("<tr><td><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		b.append("</table>");
		//b.append("<span class=\"greytext\"><b>Report:</b></span> ");
		//b.append("<span style=\"color:#006666\"><b>"+obj.getReportName()+"</b></span><br />");
		//b.append("<img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /><br />");
		return b;
	}

	private StringBuffer printFilters(StringBuffer buf, AICMetricsResultObj obj) {
		Hashtable h = obj.getFiltersShown();

		if (h.contains("1")) {
			buf.append("<tr><td headers=\"col41\" colspan=\"2\" class=\"tdblue\" height=\"18\"> &nbsp;Filter condition(s)</td></tr>");
			buf.append("<tr><td headers=\"col42\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (obj.getErrorFlag() && (!obj.getErrorField().equals("columnSelOptions"))) {
			buf.append("<a name=\"res\" id=\"res\" href=\"#res\"></a>");
			buf.append("<tr><td headers=\"col43\" colspan=\"2\"><span style=\"color:#ff3333;\"><b>Input error: \"" + obj.getErrorMsg() + "\"</b></span></td></tr>");
			buf.append("<tr><td headers=\"col44\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("wkspaces").equals("1")) {
			String wkspcStr = "Workspaces:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("wkspace")) {
				wkspcStr = "<span style=\"color:#ff3333;\">" + wkspcStr + "</span>";
			}

			buf.append("<tr><td headers=\"col45\" valign=\"top\" width=\"150\"><label for=\"wkspcs\"><b>" + wkspcStr + "</b></label></td>");

			buf.append("<td headers=\"col46\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"wkspcs\" name=\"wkspcs\" multiple=\"multiple\" size=\"4\">");
			Vector wkspcs = obj.getWorkspaceList();

			for (int i = 0; i < wkspcs.size(); i++) {
				String s = "";
				String[] wkspc = (String[]) wkspcs.elementAt(i);
				if (obj.getSelectedWorkspaces().size() > 0 && obj.getSelectedWorkspaces().contains(wkspc[1])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + wkspc[1] + "\" " + s + ">" + wkspc[0] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col47\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("users").equals("1")) {
			String userStr = "Users:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("users")) {
				userStr = "<span style=\"color:#ff3333;\">" + userStr + "</span>";
			}

			buf.append("<tr><td headers=\"col48\" valign=\"top\" width=\"150\"><label for=\"users\"><b>" + userStr + "</b></label></td>");

			buf.append("<td headers=\"col49\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"users\" name=\"users\" multiple=\"multiple\" size=\"4\">");
			Vector users = obj.getUserList();

			for (int i = 0; i < users.size(); i++) {
				String s = "";
				String[] user = (String[]) users.elementAt(i);
				if (obj.getSelectedUsers().size() > 0 && obj.getSelectedUsers().contains(user[1])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				String user_name = user[0] + "[ " + user[1] + " ]";
				if (user[1].equals("0All values0")) {
					user_name = user[0];
				}
				buf.append("<option value=\"" + user[1] + "\" " + s + ">" + user_name + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col50\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("companyList").equals("1")) {
			String compStr = "Company:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("companyList")) {
				compStr = "<span style=\"color:#ff3333;\">" + compStr + "</span>";
			}

			buf.append("<tr><td headers=\"col51\" valign=\"top\" width=\"150\"><label for=\"comp\"><b>" + compStr + "</b></label></td>");

			buf.append("<td headers=\"col52\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"comp\" name=\"comp\" multiple=\"multiple\" size=\"4\">");
			Vector comps = obj.getCompanyList();

			for (int i = 0; i < comps.size(); i++) {
				String s = "";
				if (obj.getSelectedComps().size() > 0 && obj.getSelectedComps().contains(comps.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + comps.elementAt(i) + "\" " + s + ">" + comps.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col52\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("inds").equals("1")) {
			String indsStr = "Industries:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("inds")) {
				indsStr = "<span style=\"color:#ff3333;\">" + indsStr + "</span>";
			}

			buf.append("<tr><td headers=\"col53\" valign=\"top\" width=\"150\"><label for=\"inds\"><b>" + indsStr + "</b></label></td>");

			buf.append("<td headers=\"col54\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"inds\" name=\"inds\" multiple=\"multiple\" size=\"4\">");
			Vector inds = obj.getIndsList();

			for (int i = 0; i < inds.size(); i++) {
				String s = "";
				String ind = (String) inds.elementAt(i);

				if (obj.getSelectedInds().size() > 0 && obj.getSelectedInds().contains(ind)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + ind + "\" " + s + ">" + ind + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col55\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("brand").equals("1")) {
			String brandStr = "Brand:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("brand")) {
				brandStr = "<span style=\"color:#ff3333;\">" + brandStr + "</span>";
			}

			buf.append("<tr><td headers=\"col56\" valign=\"top\" width=\"150\"><label for=\"bran\"><b>" + brandStr + "</b></label></td>");

			buf.append("<td headers=\"col57\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"brand\" name=\"brand\" multiple=\"multiple\" size=\"4\">");
			Vector brand = obj.getBrandList();

			for (int i = 0; i < brand.size(); i++) {
				String s = "";
				String bran = (String) brand.elementAt(i);

				if (obj.getSelectedBrand().size() > 0 && obj.getSelectedBrand().contains(bran)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + bran + "\" " + s + ">" + bran + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col58\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("process").equals("1")) {
			String processStr = "Process:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("process")) {
				processStr = "<span style=\"color:#ff3333;\">" + processStr + "</span>";
			}

			buf.append("<tr><td headers=\"col59\" valign=\"top\" width=\"150\"><label for=\"proc\"><b>" + processStr + "</b></label></td>");

			buf.append("<td headers=\"col60\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"process\" name=\"process\" multiple=\"multiple\" size=\"4\">");
			Vector process = obj.getProcessList();

			for (int i = 0; i < process.size(); i++) {
				String s = "";
				String proc = (String) process.elementAt(i);

				if (obj.getSelectedProcess().size() > 0 && obj.getSelectedProcess().contains(proc)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + proc + "\" " + s + ">" + proc + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col61\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("sector").equals("1")) {
			String sectorStr = "Business Sector:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("sector")) {
				sectorStr = "<span style=\"color:#ff3333;\">" + sectorStr + "</span>";
			}

			buf.append("<tr><td headers=\"col62\" valign=\"top\" width=\"150\"><label for=\"sect\"><b>" + sectorStr + "</b></label></td>");

			buf.append("<td headers=\"col63\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"sector\" name=\"sector\" multiple=\"multiple\" size=\"4\">");
			Vector sector = obj.getSectorList();

			for (int i = 0; i < sector.size(); i++) {
				String s = "";
				String sect = (String) sector.elementAt(i);

				if (obj.getSelectedSector().size() > 0 && obj.getSelectedSector().contains(sect)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + sect + "\" " + s + ">" + sect + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col64\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("sceSector").equals("1")) {
			String sceSectorStr = "SCE Sector:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("sceSector")) {
				sceSectorStr = "<span style=\"color:#ff3333;\">" + sceSectorStr + "</span>";
			}

			buf.append("<tr><td headers=\"col65\" valign=\"top\" width=\"150\"><label for=\"sceSect\"><b>" + sceSectorStr + "</b></label></td>");

			buf.append("<td headers=\"col66\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"sceSector\" name=\"sceSector\" multiple=\"multiple\" size=\"4\">");
			Vector sceSector = obj.getSceSectorList();

			for (int i = 0; i < sceSector.size(); i++) {
				String s = "";
				String sceSect = (String) sceSector.elementAt(i);

				if (obj.getSelectedSceSector().size() > 0 && obj.getSelectedSceSector().contains(sceSect)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + sceSect + "\" " + s + ">" + sceSect + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col67\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("geos").equals("1")) {
			String geosStr = "Geographies:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("geos")) {
				geosStr = "<span style=\"color:#ff3333;\">" + geosStr + "</span>";
			}

			buf.append("<tr><td headers=\"col68\" valign=\"top\" width=\"150\"><label for=\"geos\"><b>" + geosStr + "</b></label></td>");

			buf.append("<td headers=\"col69\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"geos\" name=\"geos\" multiple=\"multiple\" size=\"4\">");
			Vector geos = obj.getGeosList();

			for (int i = 0; i < geos.size(); i++) {
				String s = "";
				String geo = (String) geos.elementAt(i);
				if (obj.getSelectedGeos().size() > 0 && obj.getSelectedGeos().contains(geo)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + geo + "\" " + s + ">" + geo + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col70\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("clientDesList").equals("1")) {
			String clDesStr = "Client Designation:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("clientDesList")) {
				clDesStr = "<span style=\"color:#ff3333;\">" + clDesStr + "</span>";
			}
			buf.append("<tr><td headers=\"col71\" valign=\"top\"><label for=\"cdlist\" width=\"150\"><b>" + clDesStr + "</b></label></td>");
			buf.append("<td headers=\"col72\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"cdlist\" name=\"cdlist\" multiple=\"multiple\" size=\"4\">");
			String[] cdlist = obj.getClientDesList();
			String[] cdvallist = obj.getClientDesValList();
			for (int i = 0; i < cdlist.length; i++) {
				String s = "";
				if (obj.getSelectedClientDes().size() > 0 && obj.getSelectedClientDes().contains(cdvallist[i])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + cdvallist[i] + "\" " + s + ">" + cdlist[i] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col73\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("intext").equals("1")) {
			String ieStr = "Opened by:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("intext")) {
				ieStr = "<span style=\"color:#ff3333;\">" + ieStr + "</span>";
			}

			buf.append("<tr><td headers=\"col74\" valign=\"top\" width=\"150\"><label for=\"intext\"><b>" + ieStr + "</b></label></td>");

			buf.append("<td headers=\"col75\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"intext\" name=\"intext\">");
			String[] ies = obj.getIntExtList();

			for (int i = 0; i < ies.length; i++) {
				String s = "";
				if (obj.getSelectedIntExt().equals(String.valueOf(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + i + "\" " + s + ">" + ies[i] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col76\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("exp_cat").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String expCatStr = "Expectation category:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("exp_cat"))) { // || obj.getErrorField().equals("exp_catsa"))){
				expCatStr = "<span style=\"color:#ff3333;\">" + expCatStr + "</span>";
			}
			buf.append("<tr><td headers=\"col77\" valign=\"top\"><label for=\"excatlist\"><b>" + expCatStr + "</b></label></td>");
			buf.append("<td headers=\"col78\" align=\"left\" valign=\"top\"><select id=\"expcat\" name=\"expcat\" multiple=\"multiple\" size=\"4\">");
			Vector expcat = obj.getExpCatList();
			for (int i = 0; i < expcat.size(); i++) {
				String s = "";
				if (obj.getSelectedExpCats().size() > 0 && obj.getSelectedExpCats().contains(expcat.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + expcat.elementAt(i) + "\" " + s + ">" + expcat.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col79\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("deliveryTeams").equals("1")) {
			String delTeamStr = "Delivery Teams:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("deliveryTeams")) {
				delTeamStr = "<span style=\"color:#ff3333;\">" + delTeamStr + "</span>";
			}
			buf.append("<tr><td headers=\"col80\" valign=\"top\" width=\"150\"><label for=\"delteams\"><b>" + delTeamStr + "</b></label></td>");
			buf.append("<td headers=\"col81\" align=\"left\" valign=\"top\"><select id=\"delteams\" name=\"delteams\" multiple=\"multiple\" size=\"4\">");
			Vector teams = obj.getDeliveryTeamList();
			for (int i = 0; i < teams.size(); i++) {
				String s = "";
				if (obj.getSelectedTeams().size() > 0 && obj.getSelectedTeams().contains(teams.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + teams.elementAt(i) + "\" " + s + ">" + teams.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col82\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("wsTypes").equals("1")) {
			String wsTypesStr = "Workspace types:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("wsTypes")) {
				wsTypesStr = "<span style=\"color:#ff3333;\">" + wsTypesStr + "</span>";
			}

			buf.append("<tr><td headers=\"col83\" valign=\"top\" width=\"150\"><label for=\"wstypes\"><b>" + wsTypesStr + "</b></label></td>");
			buf.append("<td headers=\"col84\" align=\"left\" valign=\"top\"><select id=\"wstypes\" name=\"wstypes\" multiple=\"multiple\" size=\"4\">");
			String[] wstypes = obj.getWSTypesList();
			for (int i = 0; i < wstypes.length; i++) {
				String s = "";
				if (obj.getSelectedWsTypes().size() > 0 && obj.getSelectedWsTypes().contains(wstypes[i])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + wstypes[i] + "\" " + s + ">" + wstypes[i] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col85\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("roles").equals("1")) {
			String rolesStr = "User roles:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("roles")) {
				rolesStr = "<span style=\"color:#ff3333;\">" + rolesStr + "</span>";
			}

			buf.append("<tr><td headers=\"col86\" valign=\"top\" width=\"150\"><label for=\"roles\"><b>" + rolesStr + "</b></label></td>");

			buf.append("<td headers=\"col87\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"roles\" name=\"roles\" multiple=\"multiple\" size=\"4\">");
			Vector roles = obj.getRolesList();

			for (int i = 0; i < roles.size(); i++) {
				String s = "";
				String role = (String) roles.elementAt(i);
				if (obj.getSelectedRoles().size() > 0 && obj.getSelectedRoles().contains(role)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + role + "\" " + s + ">" + role + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col88\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("tabNames").equals("1")) {
			String tabNamesStr = "Tab names:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("tabNames")) {
				tabNamesStr = "<span style=\"color:#ff3333;\">" + tabNamesStr + "</span>";
			}

			buf.append("<tr><td headers=\"col89\" valign=\"top\"><label for=\"tabname\"><b>" + tabNamesStr + "</b></label></td>");
			buf.append("<td headers=\"col90\" align=\"left\" valign=\"top\"><select id=\"tabname\" name=\"tabname\" multiple=\"multiple\" size=\"4\">");
			Vector tabnames = obj.getTabNameList();
			for (int i = 0; i < tabnames.size(); i++) {
				String s = "";
				if (obj.getSelectedTabNames().size() > 0 && obj.getSelectedTabNames().contains(tabnames.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + tabnames.elementAt(i) + "\" " + s + ">" + tabnames.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col91\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("issueStatus").equals("1")) {
			String issStatusStr = "Issue status:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("issueStatus")) {
				issStatusStr = "<span style=\"color:#ff3333;\">" + issStatusStr + "</span>";
			}

			buf.append("<tr><td headers=\"col92\" valign=\"top\" width=\"150\"><label for=\"issStatus\"><b>" + issStatusStr + "</b></label></td>");
			buf.append("<td headers=\"col93\" align=\"left\" valign=\"top\"><select id=\"issStatus\" name=\"issStatus\" multiple=\"multiple\" size=\"4\">");
			String[] issStatus = obj.getIssuesStatusList();
			for (int i = 0; i < issStatus.length; i++) {
				String s = "";
				if (obj.getSelectedIssueStatus().size() > 0 && obj.getSelectedIssueStatus().contains(issStatus[i])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + issStatus[i] + "\" " + s + ">" + issStatus[i] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col94\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("advocate").equals("1")) {
			String advStr = "Advocate:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("advocate")) {
				advStr = "<span style=\"color:#ff3333;\">" + advStr + "</span>";
			}

			buf.append("<tr><td headers=\"col95\" valign=\"top\" width=\"150\"><label for=\"advocate\"><b>" + advStr + "</b></label></td>");

			buf.append("<td headers=\"col96\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"advocate\" name=\"advocate\" multiple=\"multiple\" size=\"4\">");
			Vector advs = obj.getAdvocateList();

			for (int i = 0; i < advs.size(); i++) {
				String s = "";
				String[] adv = (String[]) advs.elementAt(i);
				if (obj.getSelectedAdvocate().size() > 0 && obj.getSelectedAdvocate().contains(adv[1])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				String adv_name = adv[0] + "[ " + adv[1] + " ]";
				if (adv[1].equals("0All values0")) {
					adv_name = adv[0];
				}
				buf.append("<option value=\"" + adv[1] + "\" " + s + ">" + adv_name + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col97\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		// Added for Client Reports
		if (h.get("crCompany").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crCompany = "Client Company:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("crCompany"))) { // || obj.getErrorField().equals("exp_catsa"))){
				crCompany = "<span style=\"color:#ff3333;\">" + crCompany + "</span>";
			}
			buf.append("<tr><td headers=\"col98\" valign=\"top\"><label for=\"excatlist\"><b>" + crCompany + "</b></label></td>");
			buf.append("<td headers=\"col99\" align=\"left\" valign=\"top\"><select id=\"crCompany\" name=\"crCompany\" multiple=\"multiple\" size=\"4\">");
			// for adding All values dstring at the top of the company lsit
			String sAllValues = "";
			if (obj.getSelectedCRcompany().size() > 0 && obj.getSelectedCRcompany().contains("All values")) {
				sAllValues = "selected=\"selected\"";
			} else if (obj.getOption2().equals("")) {
				sAllValues = "selected=\"selected\"";
			}
			buf.append("<option value=\"" + "All values" + "\" " + sAllValues + ">" + "All values" + "</option>");

			Vector company = obj.getCRcompanyList();
			for (int i = 0; i < company.size(); i++) {
				String s = "";
				if (obj.getSelectedCRcompany().size() > 0 && obj.getSelectedCRcompany().contains(company.elementAt(i))) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + company.elementAt(i) + "\" " + s + ">" + company.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col100\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("crFname").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crName = "First Name:";

			String sFNameString = obj.getSelectedCRFname();
			if (sFNameString== null || sFNameString.trim().equals("")) {
				sFNameString = "";
			} else
			sFNameString = sFNameString.trim();


			if (obj.getErrorFlag() && (obj.getErrorField().equals("crFname"))) { // || obj.getErrorField().equals("exp_catsa"))){
				crName = "<span style=\"color:#ff3333;\">" + crName + "</span>";
			}
			buf.append("<tr><td headers=\"col101\" valign=\"top\"><label for=\"crFname\"><b>" + crName + "</b></label></td>");
			buf.append("<td headers=\"col102\" align=\"left\" valign=\"top\"><input type=\"text\"  name=\"crFname\" value=\"" + sFNameString + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col103\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("crLname").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crName = "Last Name:";

			String sLNameString = obj.getSelectedCRLname();
			if (sLNameString== null || sLNameString.trim().equals("")) {
				sLNameString = "";
			} else
			sLNameString = sLNameString.trim();


			if (obj.getErrorFlag() && (obj.getErrorField().equals("crLname"))) {
				crName = "<span style=\"color:#ff3333;\">" + crName + "</span>";
			}
			buf.append("<tr><td headers=\"col104\" valign=\"top\"><label for=\"crLname\"><b>" + crName + "</b></label></td>");
			buf.append("<td headers=\"col105\" align=\"left\" valign=\"top\"><input type=\"text\"  name=\"crLname\" value=\"" + sLNameString + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col106\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("crEmail").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crEmail = "E-mail Id:";

			String sEmailString = obj.getSelectedCREmail();
			if (sEmailString== null || sEmailString.trim().equals("")) {
				sEmailString = "";
			} else
			sEmailString = sEmailString.trim();


			if (obj.getErrorFlag() && (obj.getErrorField().equals("crEmail"))) {
				crEmail = "<span style=\"color:#ff3333;\">" + crEmail + "</span>";
			}
			buf.append("<tr><td headers=\"col107\" valign=\"top\"><label for=\"crLname\"><b>" + crEmail + "</b></label></td>");
			buf.append("<td headers=\"col108\" align=\"left\" valign=\"top\"><input type=\"text\"  name=\"crEmail\" value=\"" + sEmailString + "\" id=\"w_name\" maxlength=\"100\" size=\"40\" class=\"iform\" /></td>");
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col109\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("crCountry").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crCountry = "Client Country:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("crCountry"))) { // || obj.getErrorField().equals("exp_catsa"))){
				crCountry = "<span style=\"color:#ff3333;\">" + crCountry + "</span>";
			}
			buf.append("<tr><td headers=\"col110\" valign=\"top\"><label for=\"excatlist\"><b>" + crCountry + "</b></label></td>");
			buf.append("<td headers=\"col111\" align=\"left\" valign=\"top\"><select id=\"crCountry\" name=\"crCountry\" multiple=\"multiple\" size=\"4\">");
			Vector country = obj.getCRcountryList();
			for (int i = 0; i < country.size(); i++) {
				String s = "";
				if (obj.getSelectedCRcountry().size() > 0 && obj.getSelectedCRcountry().contains(country.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + country.elementAt(i) + "\" " + s + ">" + country.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col112\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("crDatasource").equals("1")) { //|| h.get("exp_catsa").equals("1")){
			String crDatasource = "Client Information Source:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("crDatasource"))) { // || obj.getErrorField().equals("exp_catsa"))){
				crDatasource = "<span style=\"color:#ff3333;\">" + crDatasource + "</span>";
			}
			buf.append("<tr><td headers=\"col113\" valign=\"top\"><label for=\"excatlist\"><b>" + crDatasource + "</b></label></td>");
			buf.append("<td headers=\"col114\" align=\"left\" valign=\"top\"><select id=\"crDatasource\" name=\"crDatasource\" multiple=\"multiple\" size=\"4\">");
			Vector dataSource = obj.getCRdatasourceList();
			for (int i = 0; i < dataSource.size(); i++) {
				String s = "";
				if (obj.getSelectedCRdatasource().size() > 0 && obj.getSelectedCRdatasource().contains(dataSource.elementAt(i))) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + dataSource.elementAt(i) + "\" " + s + ">" + dataSource.elementAt(i) + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col115\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("wsowner").equals("1")) {
			String wsownStr = "Workspace owner:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("wsowner")) {
				wsownStr = "<span style=\"color:#ff3333;\">" + wsownStr + "</span>";
			}

			buf.append("<tr><td headers=\"col116\" valign=\"top\" width=\"150\"><label for=\"wsowner\"><b>" + wsownStr + "</b></label></td>");

			buf.append("<td headers=\"col117\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"wsowner\" name=\"wsowner\" multiple=\"multiple\" size=\"4\">");
			Vector owners = obj.getWSOwnerList();

			for (int i = 0; i < owners.size(); i++) {
				String s = "";
				String[] own = (String[]) owners.elementAt(i);
				if (obj.getSelectedWSOwner().size() > 0 && obj.getSelectedWSOwner().contains(own[1])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				String own_name = own[0] + "[ " + own[1] + " ]";
				if (own[1].equals("0All values0")) {
					own_name = own[0];
				}
				buf.append("<option value=\"" + own[1] + "\" " + s + ">" + own_name + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col118\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		if (h.get("source").equals("1")) {
			String sourceStr = "Source:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("source")) {
				sourceStr = "<span style=\"color:#ff3333;\">" + sourceStr + "</span>";
			}
			buf.append("<tr><td headers=\"col119\" valign=\"top\"><label for=\"cvsource\" width=\"150\"><b>" + sourceStr + "</b></label></td>");
			buf.append("<td headers=\"col120\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"cvsource\" name=\"cvsource\" multiple=\"multiple\" size=\"3\">");
			String[] cvsourcelist = obj.getCVSourceList();
			for (int i = 0; i < cvsourcelist.length; i++) {
				String s = "";
				if (obj.getSelectedCVSource().size() > 0 && obj.getSelectedWsTypes().contains(cvsourcelist[i])) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + cvsourcelist[i] + "\" " + s + ">" + cvsourcelist[i] + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col121\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		String sel_str = "";
		if (h.get("date").equals("1")) {
			Calendar cal = Calendar.getInstance();
			String fromStr = "From:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("fromdate") || obj.getErrorField().equals("bothdate"))) {
				fromStr = "<span style=\"color:#ff3333;\">" + fromStr + "</span>";
			}
			buf.append("<tr><td headers=\"col122\" valign=\"top\"><label for=\"fromdate\"><b>" + fromStr + "</b></label></td>");
			buf.append("<td headers=\"col123\" valign=\"top\"><select name=\"frommonth\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"-1\" selected=\"selected\">&nbsp;</option>");
			for (int m = 0; m < 12; m++) {
				sel_str = "";
				if (obj.getFromMonth().equals(String.valueOf(m)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + m + "\" " + sel_str + ">" + months[m] + "</option>");
			}
			buf.append("</select>\n");

			buf.append("<select name=\"fromday\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int d = 1; d <= 31; d++) {
				sel_str = "";
				if (obj.getFromDay().equals(String.valueOf(d)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + d + "\" " + sel_str + ">" + d + "</option>");
			}
			buf.append("</select>\n");

			int year = (cal.get(Calendar.YEAR));
			buf.append("<select name=\"fromyear\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int c = 2002; c <= year; c++) {
				sel_str = "";
				if (obj.getFromYear().equals(String.valueOf(c)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + c + "\" " + sel_str + ">" + c + "</option>");
			}
			buf.append("</select>\n");
			buf.append("<img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"15\" height=\"1\" alt=\"\" />");
			if (obj.getAllDates())
				sel_str = "checked=\"checked\"";
			buf.append("<input type=\"checkbox\" name=\"alldates\" value=\"alldates\" " + sel_str + " id=\"alldates\" />");
			buf.append("<label for=\"alldates\"><b>All Dates</b></label></td></tr>");

			String toStr = "To:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("todate") || obj.getErrorField().equals("bothdate"))) {
				toStr = "<span style=\"color:#ff3333;\">" + toStr + "</span>";
			}

			buf.append("<tr><td headers=\"col124\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			buf.append("<tr><td headers=\"col125\" valign=\"top\"><label for=\"todate\"><b>" + toStr + "</b></label></td>");
			buf.append("<td headers=\"col126\" valign=\"top\"><select name=\"tomonth\" id=\"todate\" class=\"iform\">");
			//buf.append("<option value=\"-1\" selected=\"selected\">&nbsp;</option>");
			for (int m = 0; m < 12; m++) {
				sel_str = "";
				if (obj.getToMonth().equals(String.valueOf(m)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + m + "\" " + sel_str + ">" + months[m] + "</option>");
			}
			buf.append("</select>\n");

			buf.append("<select name=\"today\" id=\"todate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int d = 1; d <= 31; d++) {
				sel_str = "";
				if (obj.getToDay().equals(String.valueOf(d)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + d + "\" " + sel_str + ">" + d + "</option>");
			}
			buf.append("</select>\n");

			buf.append("<select name=\"toyear\" id=\"todate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int c = 2002; c <= year; c++) {
				sel_str = "";
				if (obj.getToYear().equals(String.valueOf(c)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + c + "\" " + sel_str + ">" + c + "</option>");
			}
			buf.append("</select>\n");
			buf.append("</td></tr>");
			buf.append("<tr><td headers=\"col127\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}

		sel_str = "";
		if (h.get("datefrom").equals("1")) {
			Calendar cal = Calendar.getInstance();
			String fromStr = "From:";

			if (obj.getErrorFlag() && (obj.getErrorField().equals("fromdate"))) {
				fromStr = "<span style=\"color:#ff3333;\">" + fromStr + "</span>";
			}
			buf.append("<tr><td headers=\"col128\" valign=\"top\"><label for=\"fromdate\"><b>" + fromStr + "</b></label></td>");
			buf.append("<td headers=\"col129\" valign=\"top\"><select name=\"frommonth\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"-1\" selected=\"selected\">&nbsp;</option>");
			for (int m = 0; m < 12; m++) {
				sel_str = "";
				if (obj.getFromMonth().equals(String.valueOf(m)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + m + "\" " + sel_str + ">" + months[m] + "</option>");
			}
			buf.append("</select>\n");

			buf.append("<select name=\"fromday\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int d = 1; d <= 31; d++) {
				sel_str = "";
				if (obj.getFromDay().equals(String.valueOf(d)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + d + "\" " + sel_str + ">" + d + "</option>");
			}
			buf.append("</select>\n");

			int year = (cal.get(Calendar.YEAR));
			buf.append("<select name=\"fromyear\" id=\"fromdate\" class=\"iform\">");
			//buf.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int c = 2002; c <= year; c++) {
				sel_str = "";
				if (obj.getFromYear().equals(String.valueOf(c)))
					sel_str = "selected=\"selected\"";
				buf.append("<option value=\"" + c + "\" " + sel_str + ">" + c + "</option>");
			}
			buf.append("</select>\n");
			buf.append("</td></tr>");
			buf.append("<tr><td headers=\"col130\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}
		if (h.get("sortBy").equals("1")) {
			String sortBy = "Sort By:";

			if (obj.getErrorFlag() && obj.getErrorField().equals("sortBy")) {
				sortBy = "<span style=\"color:#ff3333;\">" + sortBy + "</span>";
			}

			buf.append("<tr><td headers=\"col131\" valign=\"top\" width=\"150\"><label for=\"sortBy\"><b>" + sortBy + "</b></label></td>");

			buf.append("<td headers=\"col132\" align=\"left\" width=\"450\" valign=\"top\"><select id=\"sortBy\" name=\"sortBy\" size=\"1\">");
			Vector sortOrder = obj.getSortOrderList();

			for (int i = 0; i < sortOrder.size(); i++) {
				String s = "";
				String sort = (String) sortOrder.elementAt(i);

				if (obj.getSelectedSortOrder().size() > 0 && obj.getSelectedSortOrder().contains(sort)) {
					s = "selected=\"selected\"";
				} else if (obj.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}
				buf.append("<option value=\"" + sort + "\" " + s + ">" + sort + "</option>");
			}
			buf.append("</select></td></tr>");
			buf.append("<tr><td headers=\"col133\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td></tr>");
		}


		buf.append("</table>");

		return buf;
	}

	private StringBuffer printButtons(boolean printOpt, String linkid) {
		StringBuffer buf = new StringBuffer();
		buf.append("<tr><td headers=\"col134\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		buf.append("<tr><td headers=\"col135\" colspan=\"2\"><table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
		buf.append("<tr>");
		buf.append("<td headers=\"col136\" align=\"left\" valign=\"middle\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\"  name=\"submitopt\" value=\"submitopt\"  border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /></td>");
		buf.append("<td headers=\"col137\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"2\" height=\"1\" alt=\"\" /></td>");

		//download
		buf.append("<td headers=\"col138\" align=\"right\" valign=\"middle\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "download_now_rd.gif\"  name=\"downloadopt\" value=\"downloadopt\" border=\"0\"  height=\"21\" width=\"21\" alt=\"download\" /></td>");
		buf.append("<td headers=\"col139\" align=\"left\" valign=\"middle\">&nbsp;<b>Download</b></td>");
		buf.append("<td headers=\"col140\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"2\" height=\"1\" alt=\"\" /></td>");

		//print
		if (!printOpt) {
			buf.append("<td headers=\"col141\" align=\"right\" valign=\"middle\">");
			buf.append("<input type=\"image\" src=\"" + Defines.ICON_ROOT + "printer.gif\"  name=\"printopt\" value=\"printopt\" border=\"0\"  height=\"16\" width=\"16\" alt=\"printer friendly\" /></td>");
			buf.append("<td headers=\"col142\" align=\"left\" valign=\"middle\">&nbsp;<b>Printable version</b></td>");
		} else {
			buf.append("<td headers=\"col143\" align=\"right\" valign=\"middle\">");
			buf.append("<input type=\"hidden\" name=\"printopt\" value=\"yes\">");
			buf.append("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\"  name=\"noprintopt\" value=\"noprintopt\" border=\"0\"  height=\"21\" width=\"21\" alt=\"not printer friendly\" /></td>");
			buf.append("<td headers=\"col144\" align=\"left\" valign=\"middle\">&nbsp;<b>Back from printable version</b></td>");
		}
		buf.append("<td headers=\"col145\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"2\" height=\"1\" alt=\"\" /></td>");

		buf.append("<td headers=\"col146\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"AICMetricsServlet.wss?linkid=" + linkid + "\">Back to report listing</a></td>");

		// Back to home page link
		buf.append("<td headers=\"col147\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"2\" height=\"1\" alt=\"\" /></td>");
		buf.append("<td headers=\"col148\" align=\"left\" valign=\"middle\">&nbsp;<a href=\"AICConnectServlet.wss?linkid=" + linkid + "\">Collaboration center</a></td>");

		buf.append("</tr></table></td></tr>");

		buf.append("<tr><td headers=\"col149\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

		return buf;

	}
	private StringBuffer printColumns(AICMetricsResultObj resobj) {
		return AICMetricsPrintResults.printColumns(resobj);
	}

	private StringBuffer printSearchResults(StringBuffer buf, AICMetricsResultObj resobj) {
		buf.append(AICMetricsPrintResults.printSearchResults(resobj, false));
		return buf;
	}
	private StringBuffer downloadReportPage(AICMetricsResultObj resobj) {
		StringBuffer b = new StringBuffer();
		b = AICMetricsPrintResults.printSearchResults(resobj, true);

		return b;

	}

	private boolean canAccess(ETSParams params, int index, AICMetricsListValue[] list) {
		boolean b = false;

		int restriction = AICMetricsList.getRestricted(index, list);

		if (restriction == 2 && params.isSuperAdmin()) {
			return true;
		} else if (restriction == 1 && (params.isSuperAdmin() || params.isExecutive())) {
			return true;
		} else if (restriction == 0) {
			return true;
		}
		return b;
	}
}
