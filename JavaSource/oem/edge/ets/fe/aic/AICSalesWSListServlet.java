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

package oem.edge.ets.fe.aic;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.*;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

import org.apache.commons.logging.Log;


public class AICSalesWSListServlet extends HttpServlet {


	public final static String Copyright =
		"(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.6";

	protected ETSDatabaseManager databaseManager;
	private String mailhost;

	private static Log logger =
		EtsLogger.getLogger(AICSalesWSListServlet.class);

	private static final String BUNDLE_NAME =
		"oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb =
		ResourceBundle.getBundle(BUNDLE_NAME);
	
	public void service(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		long fromDate=0;
		long toDate = 0;

		PrintWriter writer = response.getWriter();

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		Hashtable params;
		String sLink;
		int iFilter = 0;
		boolean isChangeQry = false;
		boolean bOddRow = true;
		int saved = 0;
		int recCt = 0;
		StringBuffer query = new StringBuffer();
		Statement stmt = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		Statement svQryStmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Vector allWSVector = null;
		String sRole = null;

		try {

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			UnbrandedProperties prop =
				PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			ETSProjectInfoBean projBean =
				(ETSProjectInfoBean) request.getSession(false).getAttribute(
					"ETSProjInfo");

			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(con);
				request.getSession(false).setAttribute("ETSProjInfo", projBean);
			}

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID()); //210000
				sLink = prop.getLinkID(); //210000
			}

			String sSort = request.getParameter("sort");
			if (sSort == null || sSort.equals("")) {
				sSort = "";
			} else {
				sSort = sSort.trim();
			}

			String wsFilter = request.getParameter("filter");
			String prmFrom = request.getParameter("from");

			if (wsFilter == null) {
				iFilter = 0;
			} else {
				iFilter = Integer.parseInt(wsFilter);
			}

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, con, hs);
			EdgeHeader.setPopUp("J");
			String header = "";

			ETSParams parameters = new ETSParams();
			parameters.setConnection(con);
			parameters.setEdgeAccessCntrl(es);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);

			if ((prmFrom != null) && (prmFrom.equals("search"))) {
				header = aic_rb.getString("aic.wslist.resHeader").trim();
			} else {
				header = aic_rb.getString("aic.wslist.header").trim();
			}
			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);
			writer.println(
				ETSSearchCommon.getMasthead(
					EdgeHeader,
					Defines.AIC_WORKSPACE_TYPE));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());
			Metrics.appLog(con, es.gIR_USERN, "AIC_Landing");
			String sUserId = es.gIR_USERN;
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" "
					+ "cellspacing=\"0\" width=\"600\" border=\"0\" >");
			writer.println(
				"<tr valign=\"top\"><td headers=\"col1\" "
					+ "width=\"443\" valign=\"top\" class=\"small\">"
					+ "<table summary=\"\" width=\"100%\"><tr>"
					+ "<td headers=\"col2\" width=\"60%\">"
					+ es.gIR_USERN
					+ "</td>"
					+ "<td headers=\"col3\" width=\"40%\" align=\"right\">"
					+ sDate
					+ "</td></tr></table></td>");
			writer.println(
				"<td headers=\"col4\" width=\"7\"><img alt=\"\" "
					+ "src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"7\" "
					+ "height=\"1\"/></td>");
			writer.println(
				"<td headers=\"col5\" class=\"small\" "
					+ "align=\"right\"><table summary=\"\" cellpadding=\"0\""
					+ "cellspacing=\"0\" border=\"0\"><tr><td headers=\"col6\" "
					+ "width=\"\" align=\"right\"><img alt=\"\" src=\""
					+ Defines.ICON_ROOT
					+ "key.gif\" width=\"16\" height=\"16\"/>"
					+ "</td><td headers=\"col7\"  width=\"90\" align=\"right\">"
					+ "Secure content</td></tr></table></td></tr>");
			writer.println("</table>");

			//get the Query for WorkSpace List...
			query =getWSListQuery(
					request,
					con,
					sUserId,
					stmt,
					rs,
					iFilter,
					prmFrom,
					fromDate,
					toDate);

			//get All WS Vector....
			allWSVector = getWSVector(request, con, stmt, rs, query);

			if (allWSVector != null && allWSVector.size() > 0) {
				int wsCounter = 0;
				boolean bTableHeaderPrint = false;
				for (int i = 0; i < allWSVector.size(); i++) {
					//get each element of WS Vector...
					AICProj proj = (AICProj) allWSVector.elementAt(i);
					//get Project Details...
					String projId = proj.getProjectId();
					String projName = proj.getName();
					logger.debug("projName  : " + projName);
					String sceSector = proj.getSce_sector();
					String sector = proj.getSector();
					String subSector = proj.getSub_sector();
					String process = proj.getProcess();
					String brand = proj.getBrand();
					String isPrivate = proj.getIsPrivate();
					//Check if Invalid user...
					sRole = ETSUtils.checkUserRole(es, projId);
					logger.debug("sRole == " + sRole);

					if (sRole != Defines.INVALID_USER) {
						    // If Valid User ...
							wsCounter++;
							if (wsCounter == 1) {
								//Prints the Header for table
								printTableHeader(parameters, iFilter);
								bTableHeaderPrint = true;
							}
							logger.debug(sUserId + " is a Valid User of "
								+ projId);
							if (bOddRow) {
								writer.println(
								"<tr style=\"background-color: #eeeeee\" >");
								bOddRow = false;
							} else {
								writer.println("<tr >");
								bOddRow = true;
							}
							String sOwnerSymbol = "";
							String sPrivateSymbol = "";
							String sRestrictedSymbol = "";

							if ((sRole == Defines.ETS_ADMIN)
								|| (sRole == Defines.WORKSPACE_MANAGER)
								|| (sRole == Defines.WORKSPACE_OWNER)) {
								logger.debug("Entering ADMIN/MANAGER/OWNER ");
								sOwnerSymbol =
									"<span class=\"ast\"><b>&#8224;"
										+ "</b></span>";
								if (isPrivate
									.equals(Defines.AIC_IS_PRIVATE_PRIVATE)) {
									sPrivateSymbol =
										"<span class=\"ast\">" + "<b>**</b>" +
										"</span>";
								} else if (
									isPrivate.equals(
										Defines.AIC_IS_PRIVATE_RESTRICTED)) {
									sRestrictedSymbol =
										"<span class=\"ast\">" + "<b>*</b>" +
										"</span>";
								}
							} else {
								logger.debug("Entering MEMBER / VISITOR " +
									"/ CLIENT ");
								// sRole = Member/Visitor/Client
								if (isPrivate
									.equals(Defines.AIC_IS_PRIVATE_PRIVATE)) {
									sPrivateSymbol =
										"<b><span class=\"ast\">" + "**" +
										"</span></b>";
								} else if (
									isPrivate.equals(
										Defines.AIC_IS_PRIVATE_RESTRICTED)) {
									sRestrictedSymbol =
										"<b><span class=\"ast\">" + "*" +
										"</span></b>";
								}
							}
							writer.println(
								"<td headers=\"col8\"  width=\"16\" " +
								"align=\"left\" valign=\"top\"></td>");
							writer.println(
								"<td headers=\"col9\"  width=\"120\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; font-weight: " +
								"normal\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "ETSProjectsServlet.wss?proj="
									+ projId
									+ "&linkid="
									+ sLink
									+ "\" class=\"fbox\"><b>"
									+ sPrivateSymbol
									+ sRestrictedSymbol
									+ sOwnerSymbol
									+ projName
									+ "</b></a></td>");
							writer.println(
								"<td headers=\"col10\"  width=\"100\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; " +
								"font-weight: normal\">"
									+ sceSector
									+ "</td>");
							writer.println(
								"<td headers=\"col11\"  width=\"100\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; " +
								"font-weight: normal\">"
									+ sector
									+ "</td>");
							writer.println(
								"<td headers=\"col12\"  width=\"100\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; " +
								"font-weight: normal\">"
									+ subSector
									+ "</td>");
							writer.println(
								"<td headers=\"col13\"  width=\"100\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; " +
								"font-weight: normal\">"
									+ process
									+ "</td>");
							writer.println(
								"<td headers=\"col14\"  width=\"100\" " +
								"align=\"left\" valign=\"top\" " +
								"style=\"color:#000000; " +
								"font-weight: normal\">"
									+ brand
									+ "</td>");
							writer.println("</tr>");

					} else {
							// If Invalid User...
							// Denote * and No link for Restricted WSs
							logger.debug("Entering INVALID USER Block... ");
							wsCounter++;
							if ((wsCounter == 1)
								&& (bTableHeaderPrint == false)) {
								//Prints the Header for table
								printTableHeader(parameters, iFilter);
								bTableHeaderPrint = true;
							}

							if (isPrivate.equals(Defines.AIC_IS_PRIVATE_RESTRICTED) || isPrivate.equals(Defines.AIC_IS_PRIVATE_PUBLIC)) {
								if (bOddRow) {
									writer.println(
									"<tr " +
									"style=\"background-color: #eeeeee\" >");
									bOddRow = false;
								} else {
									writer.println("<tr >");
									bOddRow = true;
								}
								writer.println(
									"<td headers=\"col15\"  width=\"16\" " +
									"align=\"left\" valign=\"top\"></td>");
								writer.println(
									"<td headers=\"col16\"  width=\"120\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\"><span " +
									"class=\"ast\">*</span><b>"
										+ projName
										+ "</b></td>");
								writer.println(
									"<td headers=\"col17\"  width=\"100\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\">"
										+ sceSector
										+ "</td>");
								writer.println(
									"<td headers=\"col18\"  width=\"100\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\">"
										+ sector
										+ "</td>");
								writer.println(
									"<td headers=\"col19\"  width=\"100\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\">"
										+ subSector
										+ "</td>");
								writer.println(
									"<td headers=\"col20\"  width=\"100\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\">"
										+ process
										+ "</td>");
								writer.println(
									"<td headers=\"col21\"  width=\"100\" " +
									"align=\"left\" valign=\"top\" " +
									"style=\"color:#000000; " +
									"font-weight: normal\">"
										+ brand
										+ "</td>");
								writer.println("</tr>");

						}
					}

				} // for each vector element
				if (bTableHeaderPrint) {
					writer.println("</table>");
					writer.println("</td>");
					writer.println("</tr>");
					writer.println("</table>");
					writer.println("</td>");
					writer.println("</tr>");
				} else {
					logger.debug("No WS Found");
					writer.println(
						"<table><tr valign=\"top\"><td headers=\"col22\" " +
						"align=\"left\" valign=\"top\"><span style=\"color:#ff3333\">" +
						"No Workspace Found for  "
							+ sUserId
							+ "</span></td></tr></table>");
					writer.println("</tr>");
				}

				if (prmFrom != null && prmFrom.equals("search")) {
					String saveQry = request.getParameter("chk_save_qry");
					logger.debug("saveQry : " + saveQry);
					if ((saveQry != null) && saveQry.equalsIgnoreCase("on")) {
						saved =
							saveSearchCriteria(
								request,
								con,
								sUserId,
								svQryStmt,
								rs,
								iFilter,
								query,
								prmFrom);
					}
				}

				ETSProperties properties = new ETSProperties();

				if (properties.displayHelp() == true) {
					writer.println(
						"<tr><td headers=\"col23\" colspan=\"4\" align=\"right\" " +
						"bgcolor=\"#eeeeee\">");
					writer.println(
						"<table summary=\"\" border=\"0\" cellspacing=\"0\" " +
						"cellpading=\"0\"><tr valign=\"top\">");
					writer.println(
						"<td headers=\"col24\" width=\"16\" align=\"center\">" +
						"<img src=\"" + Defines.ICON_ROOT
						+ "popup.gif\" width=\"16\" height=\"16\" alt=\"\" " +
						"border=\"0\" /></td>");
					writer.println(
						"<td headers=\"col25\" width=\"30\" align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project\" target=\"new\" "+
							  "onclick=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project','Help'," +
							"'toolbar=0,scrollbars=1,location=0,statusbar=1," +
							"menubar=0,resizable=0,width=436,height=425," +
							"left=387,top=207'); return false;\" onkeypress=" +
							"\"window.open('"+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project','Help'," +
							"'toolbar=0,scrollbars=1,location=0,statusbar=1," +
							"menubar=0,resizable=0,width=436,height=425,left=387," +
							"top=207'); return false;\">Help</a></td>");
					writer.println("</tr></table>");
					writer.println("</td></tr>");
				}

				writer.println("</table>");
				writer.println("</td></tr></table>");

				writer.println(
					"<table><tr><td headers=\"col26\" colspan=\"6\"><img src=\"//www.ibm.com/i/" +
					"c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td>");
				writer.println(
					"<td headers=\"col27\" colspan=\"6\" class=\"small\" valign=\"bottom\">" +
					"<span class=\"ast\">*</span>&nbsp;Denotes Restricted " +
					"Workspaces</td></tr></table>");
				writer.println(
					"<table><tr><td headers=\"col28\" colspan=\"6\"><img src=\"//www.ibm.com/i/" +
					"c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td>");
				writer.println(
					"<td headers=\"col29\" colspan=\"6\" class=\"small\" valign=\"bottom\">" +
					"<span class=\"ast\">**</span>&nbsp;Denotes Private " +
					"Workspaces</td></tr></table>");
				writer.println(
					"<table><tr><td headers=\"col30\" colspan=\"6\"><img src=\"//www.ibm.com/i/" +
					"c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td>");
				writer.println(
					"<td headers=\"col31\" colspan=\"6\" class=\"small\" valign=\"bottom\">" +
					"<span class=\"ast\"><b>&#8224;</b></span>&nbsp;Denotes " +
					"Sales Admin/Workspace Manager/Workspace Owner</td></tr>" +
					"</table>");
				// else of - if vector NULL
			} else {

				logger.debug("allWSVector is NULL.");
				writer.println(
					"<table><tr valign=\"top\"><td headers=\"col32\" " +
					" align=\"left\" valign=\"top\" ><span style=\"color:#ff3333\">"
						+ aic_rb.getString("aic.wslist.noResMsg").trim()
						+ "</span></td></tr></table>");
				writer.println("</tr>");
			}

			if (prmFrom != null) {
				if ((prmFrom.equals("search")) && (saved != 0)) {
					writer.println(
						"<table><tr valign=\"top\"><td headers=\"col33\" " +
						"align=\"left\" valign=\"top\"><span style=\"color:#0000ff\">"
							+ aic_rb.getString("aic.wslist.qrySvdMsg").trim()
							+ "</span></td></tr></table>");
				}
			}

			if ((iFilter == 2)
				|| (iFilter == 3)
				|| (iFilter == 4)
				|| (iFilter == 5)
				|| (iFilter == 6)) {
				writer.println(
					"<table><tr valign=\"top\"><td headers=\"col34\" align=\"left\"" +
					" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICFilterWSServlet.wss?linkid="
						+ sLink
						+ "&filter="
						+ iFilter
						+ "\">"
						+ aic_rb.getString("aic.wslist.chgQryLink").trim()
						+ "</a></td></tr></table>");
			}
			writer.println(
					"<table><tr valign=\"top\"><td headers=\"col35\" align=\"left\"" +
					" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICConnectServlet.wss?linkid="
						+ sLink
						+ "\">"
						+"Back to Collaboration Center"
						+ "</a></td></tr></table>");
			writer.println("<br /><br />");
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" " +
				"border=\"0\"><tr><td headers=\"col36\" width=\"16\" align=\"left\">" +
				"<img alt=\"protected content\" src=\""
					+ Defines.ICON_ROOT
					+ "key.gif\" width=\"16\" height=\"16\" /></td><td " +
					"headers=\"col37\" align=\"left\"><span class=\"fnt\">" +
					"A key icon displayed in a page indicates that the " +
					"page is secure and password-protected.</span></td>" +
					"</tr></table>");
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this, e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(con);
			writer.flush();
			writer.close();
		}

	} // service()


	/**
	 * Get the Workspace list Query.
	 *
	 * For Workspace List Result after Search (filter page)
	 * it takes the base(default) query and append the filter conditions
	 * and order By Sector/SCE/brand/
	 * When any of the WS Queries link is clicked on the landing
	 * page, it checks for the saved query for a particular user,
	 * if there's no saved query for the user , it'll take the
	 * default query and append order by sector/sce/brand/
	 *
	 * @param request
	 * @param con
	 * @param sUserId
	 * @param stmt
	 * @param rs
	 * @param iFilter
	 * @param prmFrom
	 * @return sQuery
	 * @throws SQLException
	 * @throws Exception
	 */
	StringBuffer getWSListQuery(
		HttpServletRequest request,
		Connection con,
		String sUserId,
		Statement stmt,
		ResultSet rs,
		int iFilter,
		String prmFrom,
		long fromDate,
		long toDate)
		throws SQLException, Exception {

		StringBuffer sQuery = new StringBuffer();
		StringBuffer wsDBQry = new StringBuffer();
		StringBuffer wsSceSectDBQry = new StringBuffer();
		StringBuffer wsSecDBQry = new StringBuffer();
		StringBuffer wsSubSecDBQry = new StringBuffer();
		StringBuffer wsProcsDBQry = new StringBuffer();
		StringBuffer wsBrandDBQry = new StringBuffer();

		String wsQry = null;
		String repAllWSQry = null;
		String wsSceSecQry = null;
		String wsSecQry = null;
		String wsSubSecQry = null;
		String wsProcsQry = null;
		String wsBrandQry = null;

		try {

			repAllWSQry = getDefaultQry(request, con, stmt, rs);

			logger.debug("Def / Base Qry == " + repAllWSQry);

			if ((prmFrom != null) && (prmFrom.equals("search"))) {



				sQuery.append(repAllWSQry);

				String sceSect[] = request.getParameterValues("sceSectList");
				if (sceSect != null) {

					if ((sceSect[0].equalsIgnoreCase("All")) == false) {

						if (sceSect.length > 1) {
							sQuery.append(
								" AND (SCE_SECTOR='" + sceSect[0] + "' ");
						} else {
							sQuery.append(
								" AND SCE_SECTOR='" + sceSect[0] + "' ");
						}

					for (int i = 1; i < sceSect.length; i++) {
							if (i == ((sceSect.length) - 1)) {
								sQuery.append(
									" OR SCE_SECTOR='" + sceSect[i] + "')");
							} else {
								sQuery.append(
									" OR SCE_SECTOR='" + sceSect[i] + "' ");
							}
					  }
				   }
				}

				String sector[] = request.getParameterValues("sectorList");
				if (sector != null) {

					if ((sector[0].equalsIgnoreCase("All")) == false) {

						if (sector.length > 1) {
							sQuery.append(" AND (SECTOR='" + sector[0] + "' ");
						} else {
							sQuery.append(" AND SECTOR='" + sector[0] + "' ");
						}

					for (int i = 1; i < sector.length; i++) {
							if (i == ((sector.length) - 1)) {
								sQuery.append(
									" OR SECTOR='" + sector[i] + "')");
							} else {
								sQuery.append(
									" OR SECTOR='" + sector[i] + "' ");
							}
					}
				  }
				}

				String subSect[] = request.getParameterValues("subSectList");
				if (subSect != null) {

					if ((subSect[0].equalsIgnoreCase("All")) == false) {

						if (subSect.length > 1) {
							sQuery.append(
								" AND (SUB_SECTOR='" + subSect[0] + "' ");
						} else {
							sQuery.append(
								" AND SUB_SECTOR='" + subSect[0] + "' ");
						}

					for (int i = 1; i < subSect.length; i++) {
						   if (i == ((subSect.length) - 1)) {
								sQuery.append(
									" OR SUB_SECTOR='" + subSect[i] + "')");
							} else {
								sQuery.append(
									" OR SUB_SECTOR='" + subSect[i] + "' ");
							}
					   }
					}
				}

				String procs[] = request.getParameterValues("procsList");
				if (procs != null) {

					if ((procs[0].equalsIgnoreCase("All")) == false) {

						if (procs.length > 1) {
							sQuery.append(" AND (PROCESS='" + procs[0] + "' ");
						} else {
							sQuery.append(" AND PROCESS='" + procs[0] + "' ");
						}

					for (int i = 1; i < procs.length; i++) {
							if (i == ((procs.length) - 1)) {
								sQuery.append(
									" OR PROCESS='" + procs[i] + "')");
							} else {
								sQuery.append(
									" OR PROCESS='" + procs[i] + "' ");
							}

						}
					}
				}

				String brand[] = request.getParameterValues("brandList");
				if (brand != null) {

					if ((brand[0].equalsIgnoreCase("All")) == false) {

						if (brand.length > 1) {
							sQuery.append(" AND (BRAND='" + brand[0] + "' ");
						} else {
							sQuery.append(" AND BRAND='" + brand[0] + "' ");
						}

					for (int i = 1; i < brand.length; i++) {
							if (i == ((brand.length) - 1)) {
								sQuery.append(" OR BRAND='" + brand[i] + "')");
							} else {
								sQuery.append(" OR BRAND='" + brand[i] + "' ");
							}

						}
					}
				}

				String allDates = request.getParameter("allDates");
				logger.debug("AICSalesWSListServlet:: allDates : " + allDates);

				if (allDates == null) {
					String fromMonth = request.getParameter("fromMonth");
					String fromDay = request.getParameter("fromDay");
					String fromYear = request.getParameter("fromYear");
					String toMonth = request.getParameter("toMonth");
					String toDay = request.getParameter("toDay");
					String toYear = request.getParameter("toYear");

					fromDate = setFromDate(fromMonth, fromDay, fromYear, fromDate);
					toDate = setToDate(toMonth, toDay, toYear, toDate);

					Timestamp fromDateTS = getFromDateTS(fromDate);
					Timestamp toDateTS = getToDateTS(toDate);

					if ((allDates == null)
						&& (fromDate != 0)
						&& (toDate != 0)) {
						sQuery.append(
							" and date(PROJECT_START)>=date('"
								+ fromDateTS
								+ "') and date(PROJECT_END) <= date('"
								+ toDateTS
								+ "')");
					}
				}

				if (iFilter == 2) {
					sQuery.append(" ORDER BY SCE_SECTOR for READ ONLY");
				} else if (iFilter == 3) {
					sQuery.append(" ORDER BY SECTOR for READ ONLY");
				} else if (iFilter == 4) {
					sQuery.append(" ORDER BY SUB_SECTOR for READ ONLY");
				} else if (iFilter == 5) {
					sQuery.append(" ORDER BY PROCESS for READ ONLY");
				} else if (iFilter == 6) {
					sQuery.append(" ORDER BY BRAND for READ ONLY");
				}

			} else {
				logger.debug("Else : sQuery == " + sQuery);
				logger.debug("Else : Def / Base Qry == " + repAllWSQry);

				switch (iFilter) {

					case 1 :
						sQuery.append(repAllWSQry);
						sQuery.append(" ORDER BY PROJECT_NAME for READ ONLY");
						break;

					case 2 :
						stmt = con.createStatement();
						wsSceSectDBQry.append(
							"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
							"WHERE QUERYID='Sav_Sce' AND USERID='"
								+ sUserId
								+ "'");
						rs = stmt.executeQuery(wsSceSectDBQry.toString());
						if ((rs.next()) && rs.getString(1) != null) {
							wsSceSecQry = rs.getString(1);
							stmt.close();
							rs.close();
							String repWSSceSectQry =
								wsSceSecQry.replace('*', '\'');
							logger.debug(
								"Repl.WS BY  SCE SECTOR Qry = = "
									+ repWSSceSectQry);
							sQuery.append(repWSSceSectQry);

						} else {
							sQuery.append(repAllWSQry);
							sQuery.append(" ORDER BY SCE_SECTOR for READ ONLY");
						}
						break;

					case 3 :
						stmt = con.createStatement();
						wsSecDBQry.append(
							"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
							"WHERE QUERYID='Sav_Sect' AND USERID='"
								+ sUserId
								+ "'");
						rs = stmt.executeQuery(wsSecDBQry.toString());
						if ((rs.next()) && rs.getString(1) != null) {
							wsSecQry = rs.getString(1);
							stmt.close();
							rs.close();
							String repWSSecQry = wsSecQry.replace('*', '\'');
							logger.debug(
								"Repl.WS BY SECTOR Qry = = " + repWSSecQry);
							sQuery.append(repWSSecQry);

						} else {
							sQuery.append(repAllWSQry);
							sQuery.append(" ORDER BY SECTOR for READ ONLY");
						}
						break;

					case 4 :
						stmt = con.createStatement();
						wsSubSecDBQry.append(
							"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
							"WHERE QUERYID='Sav_SubSect' AND USERID='"
								+ sUserId
								+ "'");
						rs = stmt.executeQuery(wsSubSecDBQry.toString());
						if ((rs.next()) && rs.getString(1) != null) {
							wsSubSecQry = rs.getString(1);
							stmt.close();
							rs.close();
							String repWSSubSecQry =
								wsSubSecQry.replace('*', '\'');
							logger.debug(
								"Repl.WS BY SUB-SECTOR Qry = = "
									+ repWSSubSecQry);
							sQuery.append(repWSSubSecQry);

						} else {
							sQuery.append(repAllWSQry);
							sQuery.append(" ORDER BY SUB_SECTOR for READ ONLY");
						}
						break;

					case 5 :
						stmt = con.createStatement();
						wsProcsDBQry.append(
							"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
							"WHERE QUERYID='Sav_Procs' AND USERID='"
								+ sUserId
								+ "'");
						rs = stmt.executeQuery(wsProcsDBQry.toString());
						if ((rs.next()) && (rs.getString(1) != null)) {
							wsProcsQry = rs.getString(1);
							stmt.close();
							rs.close();
							String repWSProcsQry =
								wsProcsQry.replace('*', '\'');
							logger.debug(
								"Repl.WS BY PROCESS Qry = = " + repWSProcsQry);
							sQuery.append(repWSProcsQry);

						} else {
							sQuery.append(repAllWSQry);
							sQuery.append(" ORDER BY PROCESS for READ ONLY");
						}
						break;

					case 6 :
						stmt = con.createStatement();
						wsBrandDBQry.append(
							"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
							"WHERE QUERYID='Sav_Brand' AND USERID='"
								+ sUserId
								+ "'");
						rs = stmt.executeQuery(wsBrandDBQry.toString());
						if ((rs.next()) && rs.getString(1) != null) {
							wsBrandQry = rs.getString(1);
							stmt.close();
							rs.close();
							String repWSBrandQry =
								wsBrandQry.replace('*', '\'');
							logger.debug(
								"Repl.WS BY BRAND Qry = = " + repWSBrandQry);
							sQuery.append(repWSBrandQry);

						} else {
							sQuery.append(repAllWSQry);
							sQuery.append(" ORDER BY BRAND for READ ONLY");
						}
						break;

					default :
						sQuery.append(repAllWSQry);
						sQuery.append(" ORDER BY PROJECT_NAME for READ ONLY");

				}

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		logger.debug("sQuery == " + sQuery.toString());
		return sQuery;

	}

	/**
	 * Save the Workspace List query.
	 *
	 * This function first checks ,if the user already got a saved search
	 * criteria , if it's there it updates the SQLSTATEMENT else creates
	 * a new record for the search criteria
	 *
	 * @param request
	 * @param con
	 * @param sUserId
	 * @param stmt
	 * @param rs
	 * @param iFilter
	 * @param query
	 * @param prmFrom
	 * @return svd
	 * @throws SQLException
	 * @throws Exception
	 */



	int saveSearchCriteria(
		HttpServletRequest request,
		Connection con,
		String sUserId,
		Statement stmt,
		ResultSet rs,
		int iFilter,
		StringBuffer query,
		String prmFrom)
		throws SQLException, Exception {

		String svQry = null;
		int svd = 0;
		String strQuery = null;

		try {

			String tQuery = query.toString();
			char c1 = '\'';
			logger.debug("char c1 == " + c1);
			char c2 = '*';
			String repQuery = tQuery.replace(c1, c2);
			logger.debug("Query after replacing : " + repQuery);

			switch (iFilter) {

				case 2 :
					strQuery =
						"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
						"WHERE QUERYID='Sav_Sce' AND USERID='"
							+ sUserId
							+ "'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(strQuery);
					if ((rs.next()) && (rs.getString(1) != null)) {
						svQry =
							"UPDATE ETS.AIC_QUERY_TABLE SET SQLSTATEMENT = '"
								+ repQuery
								+ "' WHERE QUERYID = 'Sav_Sce' AND USERID='"
								+ sUserId
								+ "'";
						logger.debug("Update = " + svQry);
					} else {
						svQry =
							"INSERT INTO ETS.AIC_QUERY_TABLE (USERID, " +
							"QUERYID, QUERYNAME, SQLSTATEMENT) VALUES ('"
								+ sUserId
								+ "', 'Sav_Sce', 'svdSceQuery', '"
								+ repQuery
								+ "')";
						logger.debug("Insert = " + svQry);
					}
					stmt.close();
					rs.close();
					break;

				case 3 :
					strQuery =
						"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
						"WHERE QUERYID='Sav_Sect' AND USERID='"
							+ sUserId
							+ "'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(strQuery);
					if ((rs.next()) && (rs.getString(1) != null)) {
						svQry =
							"UPDATE ETS.AIC_QUERY_TABLE SET SQLSTATEMENT = '"
								+ repQuery
								+ "' WHERE QUERYID = 'Sav_Sect' AND USERID='"
								+ sUserId
								+ "'";
						logger.debug("Update = " + svQry);
					} else {
						svQry =
							"INSERT INTO ETS.AIC_QUERY_TABLE (USERID, " +
							"QUERYID, QUERYNAME, SQLSTATEMENT) VALUES ('"
								+ sUserId
								+ "', 'Sav_Sect', 'svdSectQuery', '"
								+ repQuery
								+ "')";
						logger.debug("Insert = " + svQry);
					}
					stmt.close();
					rs.close();
					break;

				case 4 :
					strQuery =
						"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
						"WHERE QUERYID='Sav_SubSect' AND USERID='"
							+ sUserId
							+ "'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(strQuery);
					if ((rs.next()) && (rs.getString(1) != null)) {
						logger.debug("");
						svQry =
							"UPDATE ETS.AIC_QUERY_TABLE SET SQLSTATEMENT = '"
								+ repQuery
								+ "' WHERE QUERYID = 'Sav_SubSect' AND USERID='"
								+ sUserId
								+ "'";
						logger.debug("Update = " + svQry);
					} else {
						svQry =
							"INSERT INTO ETS.AIC_QUERY_TABLE (USERID, " +
							"QUERYID, QUERYNAME, SQLSTATEMENT) VALUES ('"
								+ sUserId
								+ "', 'Sav_SubSect', 'svdSubSectQuery', '"
								+ repQuery
								+ "')";
						logger.debug("Insert = " + svQry);
					}
					stmt.close();
					rs.close();
					break;

				case 5 :
					strQuery =
						"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
						"WHERE QUERYID='Sav_Procs' AND USERID='"
							+ sUserId
							+ "'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(strQuery);
					if ((rs.next()) && (rs.getString(1) != null)) {
						svQry =
							"UPDATE ETS.AIC_QUERY_TABLE SET SQLSTATEMENT = '"
								+ repQuery
								+ "' WHERE QUERYID = 'Sav_Procs' AND USERID='"
								+ sUserId
								+ "'";
						logger.debug("Update = " + svQry);
					} else {
						svQry =
							"INSERT INTO ETS.AIC_QUERY_TABLE (USERID, " +
							"QUERYID, QUERYNAME, SQLSTATEMENT) VALUES ('"
								+ sUserId
								+ "', 'Sav_Procs', 'svdProcsQuery', '"
								+ repQuery
								+ "')";
						logger.debug("Insert = " + svQry);
					}
					stmt.close();
					rs.close();
					break;

				case 6 :
					strQuery =
						"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
						"WHERE QUERYID='Sav_Brand' AND USERID='"
							+ sUserId
							+ "'";
					stmt = con.createStatement();
					rs = stmt.executeQuery(strQuery);
					if ((rs.next()) && (rs.getString(1) != null)) {
						svQry =
							"UPDATE ETS.AIC_QUERY_TABLE SET SQLSTATEMENT = '"
								+ repQuery
								+ "' WHERE QUERYID = 'Sav_Brand' AND USERID='"
								+ sUserId
								+ "'";
						logger.debug("Update = " + svQry);
					} else {
						svQry =
							"INSERT INTO ETS.AIC_QUERY_TABLE (USERID, " +
							"QUERYID, QUERYNAME, SQLSTATEMENT) VALUES ('"
								+ sUserId
								+ "', 'Sav_Brand', 'svdBrandQuery', '"
								+ repQuery
								+ "')";
						logger.debug("Insert = " + svQry);
					}
					stmt.close();
					rs.close();
					break;

				default :

					logger.debug("WRONG FILTER INPUT");
			}

			stmt = con.createStatement();
			svd = stmt.executeUpdate(svQry);
			con.commit();
			con.setAutoCommit(true);
			logger.debug(" svd = " + svd);
			if (svd != 0) {
				logger.debug("Query Saved Successfully");
			}
			stmt.close();
			con.close();
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return svd;

	}

	/**
	 * Get the default All Workspace query
	 * @param request
	 * @param con
	 * @param stmt
	 * @param rs
	 * @return repAllWSQry
	 * @throws SQLException
	 * @throws Exception
	 */
	String getDefaultQry(
		HttpServletRequest request,
		Connection con,
		Statement stmt,
		ResultSet rs)
		throws SQLException, Exception {

		StringBuffer allWSDBQry = new StringBuffer();
		String allWSQry = null;
		String repAllWSQry = null;

		try {
			// Getting the default / base  query........
			stmt = con.createStatement();
			allWSDBQry.append(
				"SELECT SQLSTATEMENT FROM ETS.AIC_QUERY_TABLE " +
				"WHERE QUERYID='Def_All'");
			rs = stmt.executeQuery(allWSDBQry.toString());
			if ((rs.next()) && (rs.getString(1) != null))
				logger.debug("Getting Def_ALL Query... ");
			allWSQry = rs.getString(1);
			stmt.close();
			rs.close();

			repAllWSQry = allWSQry.replace('*', '\'');
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return repAllWSQry;

	}

	/**
	 * Get the Workspace List Query
	 * @param request
	 * @param con
	 * @param stmt
	 * @param rs
	 * @param query
	 * @return vWorkspaces
	 * @throws SQLException
	 * @throws Exception
	 */
	Vector getWSVector(
		HttpServletRequest request,
		Connection con,
		Statement stmt,
		ResultSet rs,
		StringBuffer query)
		throws SQLException, Exception {

		Vector vWorkspaces = new Vector();

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query.toString());

			while (rs.next()) {

				AICProj proj = new AICProj();

				String sProjId = ETSUtils.checkNull(rs.getString("PROJECT_ID"));
				String sProjName =
					ETSUtils.checkNull(rs.getString("PROJECT_NAME"));
				String sSceSector =
					ETSUtils.checkNull(rs.getString("SCE_SECTOR"));
				String sSector = ETSUtils.checkNull(rs.getString("SECTOR"));
				String sSubSector =
					ETSUtils.checkNull(rs.getString("SUB_SECTOR"));
				String sProcess = ETSUtils.checkNull(rs.getString("PROCESS"));
				String sBrand = ETSUtils.checkNull(rs.getString("BRAND"));
				String sIsPrivate =
					ETSUtils.checkNull(rs.getString("IS_PRIVATE"));

				proj.setProjectId(sProjId);
				proj.setName(sProjName);
				proj.setSce_sector(sSceSector);
				proj.setSector(sSector);
				proj.setSub_sector(sSubSector);
				proj.setBrand(sBrand);
				proj.setProcess(sProcess);
				proj.setIsPrivate(sIsPrivate);

				vWorkspaces.add(proj);

			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return vWorkspaces;

	}

	/**
	 * Set FromDate
	 * @param iMonth
	 * @param iDay
	 * @param iYear
	 * @param fromDate
	 */
	private long setFromDate(
		String iMonth,
		String iDay,
		String iYear,
		long fromDate)
		{
		
		int month = Integer.parseInt(iMonth.trim());
		int day = Integer.parseInt(iDay.trim());
		int year = Integer.parseInt(iYear.trim());

		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		Date d = c.getTime();
		fromDate = d.getTime();
		
		return fromDate;
	}

	/**
	 * Set ToDate
	 * @param iMonth
	 * @param iDay
	 * @param iYear
	 * @param toDate
	 */
	private long setToDate(
		String iMonth,
		String iDay,
		String iYear,
		long toDate
		 ){
		
		int month = Integer.parseInt(iMonth.trim());
		int day = Integer.parseInt(iDay.trim());
		int year = Integer.parseInt(iYear.trim());

		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		Date d = c.getTime();
		toDate = d.getTime();
		
		return toDate;
	}

	/**
	 * Get From Date TimeStamp
	 * @param fromDate
	 * @return Timestamp(fromDate)
	 */
	private static Timestamp getFromDateTS(long fromDate) {
		return new Timestamp(fromDate);
	}

	/**
	 * Get To Date TimeStamp
	 * @param toDate
	 * @return Timestamp(toDate)
	 */
	private static Timestamp getToDateTS(long toDate) {
		return new Timestamp(toDate);
	}

	/**
	 * Print the Table Header of Workspace List
	 * @param params
	 * @param iFilter
	 */
	private static void printTableHeader(ETSParams params, int iFilter) {
		PrintWriter writer = params.getWriter();
		// 1 st Table
		writer.println(
			"<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" " +
			"border=\"0\"  width=\"100%\">");
		writer.println("<tr><td headers=\"col38\" class=\"tdblue\">");

		// 2 nd Table
		writer.println(
			"<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" " +
			"border=\"0\" width=\"100%\">");
		writer.println("<tr>");

		if ((iFilter != 2)
			&& (iFilter != 3)
			&& (iFilter != 4)
			&& (iFilter != 5)
			&& (iFilter != 6)) {
			writer.println(
				"<td headers=\"col39\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces</td>");
		} else if (iFilter == 2) {
			writer.println(
				"<td headers=\"col40\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces By SCE Sector</td>");
		} else if (iFilter == 3) {
			writer.println(
				"<td headers=\"col41\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces By Sector</td>");
		} else if (iFilter == 4) {
			writer.println(
				"<td headers=\"col42\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces By Sub-Sector</td>");
		} else if (iFilter == 5) {
			writer.println(
				"<td headers=\"col43\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces By Process</td>");
		} else if (iFilter == 6) {
			writer.println(
				"<td headers=\"col44\"  height=\"18\" class=\"tblue\">" +
				"&nbsp;WorkSpaces By Brand</td>");
		}

		writer.println("</tr>");
		writer.println("<tr><td headers=\"col45\"  width=\"100%\">");

		// 3 rd Table
		writer.println(
			"<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" " +
			"border=\"0\" width=\"100%\">");
		writer.println("<tr valign=\"middle\">");
		writer.println(
			"<td headers=\"col46\"  style=\"background-color: #ffffff;\"  " +
			"align=\"center\">");
		writer.println(
			"<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" " +
			"border=\"0\" width=\"100%\">");
		writer.println("<tr valign=\"top\" >");

		writer.println(
			"<td headers=\"col47\"  colspan=\"2\" width=\"10\" align=\"left\" " +
			"valign=\"top\" style=\"color:#000000; font-weight: normal\">" +
			"<b>Name</b></td>");
		writer.println(
			"<td headers=\"col48\"  width=\"20\" align=\"left\" valign=\"top\" " +
			"style=\"color:#000000; font-weight: normal\"><b>SCE Sector" +
			"</b></td>");
		writer.println(
			"<td headers=\"col49\"  width=\"20\" align=\"left\" valign=\"top\" " +
			"style=\"color:#000000; font-weight: normal\"><b>Sector</b></td>");
		writer.println(
			"<td headers=\"col50\"  width=\"20\" align=\"left\" valign=\"top\" " +
			"style=\"color:#000000; font-weight: normal\"><b>Sub Sector</b></td>");
		writer.println(
			"<td headers=\"col51\"  width=\"20\" align=\"left\" valign=\"top\" " +
			"style=\"color:#000000; font-weight: normal\"><b>Process</b></td>");
		writer.println(
			"<td headers=\"col52\"  width=\"20\" align=\"left\" valign=\"top\" " +
			"style=\"color:#000000; font-weight: normal\"><b>Brand</b></td>");
		writer.println("</tr>");

		writer.println("<tr valign=\"top\">");
		writer.println(
			"<td headers=\"col53\" colspan=\"5\" align=\"left\" " +
			"style=\"background-color: #ffffff; font-weight: normal;\">");
		writer.println("</td>");
		writer.println("</tr>");
	}
} // AICSalesWSListServlet
