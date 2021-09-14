/* Copyright Header Check */
/* -------------------------------------------------------------------- */
/*                                                                          */
/* OCO Source Materials */
/*                                                                          */
/* Product(s): PROFIT */
/*                                                                          */
/* (C)Copyright IBM Corp. 2003-2004 */
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.common.Global;
//import oem.edge.ets.fe.*;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSSearchCommon;
import oem.edge.ets.fe.ETSParams;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import org.apache.commons.logging.Log;

public class AICConnectServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";

	private static final String CLASS_VERSION = "1.8";

	protected ETSDatabaseManager databaseManager;

	private String mailhost;

	private static Log logger = EtsLogger.getLogger(AICConnectServlet.class);

	private static final String BUNDLE_NAME = "oem.edge.ets.fe.aic.AICResources";

	private static final ResourceBundle aic_rb = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		PrintWriter writer = response.getWriter();
		Hashtable params;
		String sLink;

		try {

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			/*
			 * e&ts connect landing page.. so can set the ptoperty from defines
			 * below...
			 */
			UnbrandedProperties prop = PropertyFactory
					.getProperty(Defines.AIC_WORKSPACE_TYPE);

			ETSProjectInfoBean projBean = (ETSProjectInfoBean) request
					.getSession(false).getAttribute("ETSProjInfo");

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

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, con, hs);
			EdgeHeader.setPopUp("J");
			String assocCompany = es.gASSOC_COMP;
			String header = "";
			if (!assocCompany.equals("")) {
				// if user id internal (!external) make assoc company IBM
				if (!es.gDECAFTYPE.equals("E")) {
					header = aic_rb.getString("aic.header").trim();
				} else {
					header = aic_rb.getString("aic.header_comp") + assocCompany;
				}

			} else {
				header = aic_rb.getString("aic.header").trim();
			}

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
			} else if (userents
					.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT)
					|| userents
							.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT)) {
				bOEMSales = true;
			} else if (userents.contains(Defines.WORKFLOW_ADMIN)) {
				bWorkflowAdmin = true;
			} else if (!es.gDECAFTYPE.equals("I")) {
				// if the external user doesn't have entitlements
				if (!userents.contains(Defines.COLLAB_CENTER_ENTITLEMENT)) {
					bEntitlement = true;
				}
			}

			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);

			EdgeHeader.setSubHeader("Welcome, " + es.gFIRST_NAME.trim() + " "
					+ es.gLAST_NAME);
			writer.println(ETSSearchCommon.getMasthead(EdgeHeader,
					Defines.AIC_WORKSPACE_TYPE));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());

			//display the home page for the user

			Metrics.appLog(con, es.gIR_USERN, "AIC_Landing");

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
							+ "border=\"0\"><tr><td headers=\"icon_root\" width=\"\" "
							+ "align=\"right\"><img alt=\"\" src=\""
							+ Defines.ICON_ROOT
							+ "key.gif\" width=\"16\" height=\"16\" /></td>"
							+ "<td headers=\"secure_content\"  width=\"90\" align=\"right\">"
							+ "Secure content</td></tr></table></td></tr>");
			writer.println("<tr valign=\"top\">");

			writer
					.println("<td headers=\"aic_landing_1\"  width=\"443\" valign=\"top\"><img src=\""
							+ Defines.SERVLET_PATH
							+ "ETSImageServlet.wss?proj=AIC_LANDING_1&mod=0\" "
							+ "width=\"443\" height=\"149\" alt=\"AIC"
							+ projBean.getImageAltText("AIC_LANDING_1", 0)
							+ "\"/>");

			writer.println("<br />");

			writer.println("<table summary=\"\" cellspacing=\"0\" "
					+ "cellpadding=\"0\" border=\"0\" width=\"443\">");

			writer.println("<tr>");
			writer
					.println("<td headers=\"td_terms\"  align=\"left\" valign=\"top\">"
							+ "IBM employees are bound by IBM's Business Conduct Guidelines in their use of this workspace."
							+ "</td>");
			writer.println("</tr>");

			writer
					.println("<tr><td headers=\"td_terms2\"  align=\"left\" valign=\"top\">&nbsp;</td></tr>");

			writer.println("<tr>");
			writer
					.println("<td headers=\"td_terms1\"  align=\"left\" valign=\"top\">"
							+ "Other workspace users are bound by any contract with IBM including the web site terms of use."
							+ "<a href=\"http://www.ibm.com/legal/us/\" onclick=\"window.open('http://www.ibm.com/legal/us/','IBM Terms of use','scrollbars=yes,resizable=yes,width=600,height=600'); return false;\"  onkeypress=\"window.open('http://www.ibm.com/legal/us/','IBM Terms of use','scrollbars=yes,resizable=yes,width=600,height=600'); return false;\" target=\"new\" class=\"fbox\"><img src=\""
							+ Defines.ICON_ROOT
							+ "popup.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"\" /></a>"
							+ "</td>");
			writer.println("</tr>");

			writer.println("</table>");

			writer.println("<br />");

			int iProjectCount = 0;

			if (bAdmin || bWorkflowAdmin) {
				// super admin (sales admin) can see all projects...
				iProjectCount = 1;
			} else if (bExecutive) {
				// sales executive can see all the sales workspaces...
				iProjectCount = 1;
			} else {
				/*
				 * normal members can only access the workspaces if they are a
				 * member of the workspaces.
				 */
				if (!bEntitlement) {
					iProjectCount = 0;
				} else {
					iProjectCount = getWorkspaceCount(con, es.gIR_USERN);
				}
			}

			//	check to see if the customize list has been setup, if not set it
			// up now...
			int iCustomizeCount = getCustomizeCount(con, es.gIR_USERN);
			if (iCustomizeCount == 0 && iProjectCount > 0) {
				// this is a new user and does not have his cusomize list set
				// up...
				if (bAdmin || bExecutive || bWorkflowAdmin) {
					setupDefaultCutomizeList(con, es.gIR_USERN, bAdmin,
							bWorkflowAdmin);
				} else {
					setupDefaultCutomizeList(con, es.gIR_USERN, false, false);
				}
			}

			if (iProjectCount <= 0) {
				displayNoProjectContent(con, writer);
			} else {

				boolean bDisplay = false;

				if (bAdmin || bExecutive) {
					// display all voice of client projects to super admin and
					// executive.
					bDisplay = true;
				}

				getMyProjects(con, writer, es.gIR_USERN, es.gDECAFTYPE.trim()
						.toUpperCase(), sLink, bDisplay, bWorkflowAdmin);

			}

			if (bOEMSales || bAdmin) {
				displayWorkspaceQueries(writer);
			}
			displayPendingRequests(con, writer, es.gIR_USERN, es.gEMAIL, sSort);
			displayAccessRequests(con, writer, es.gIR_USERN, es.gEMAIL);

			//	capabilities...
			writer
					.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" "
							+ "border=\"0\"  width=\"443\">");
			writer.println("<tr>");
			writer
					.println("<td headers=\"learn_more\"  colspan=\"2\" height=\"18\" "
							+ "class=\"tblue\">&nbsp;Learn more</td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"topimageroot2\"  colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"learn_content\"  colspan=\"2\">Whether you're dealing "
							+ "with electromigration problems on ICs, building advanced "
							+ "manufacturing facilities, or supplying products to remote "
							+ "markets, we can help. Learn more about what we offer in: "
							+ "</td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"topimageroot3\"  colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			writer.println("</tr>");

			writer.println("<tr><td headers=\"td_capabilities\" >");

			getCapabilities(projBean, writer);

			writer.println("</td></tr></table>");
			writer.println("<br />");

			// Gutter between main and right columns
			writer
					.println("<td headers=\"topimageroot4\"  width=\"7\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"7\" height=\"1\" alt=\"\" /></td>");

			// right column begins...
			writer
					.println("<td headers=\"td_right_col\"  width=\"150\" valign=\"top\">");
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				displayCreateWorkspace(con, writer, es, sLink);
			}

			if (bAdmin) {
				displayManageTemplates(con, writer, es, sLink);
			}
			// Metrics Reports
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				displayMetricsReports(con, writer, es, sLink);
			}
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				displayWorkflowEvents(con, writer, es, sLink);
			}
			displayOptions(con, writer, es, sLink);
			displayNewsAndIdeas(con, writer);
			// need more help
			ETSParams etsParams = new ETSParams();
			etsParams.setWriter(writer);
			displaySiteHelp(etsParams, sLink);
			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");
			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");
			writer.println("<br /><br />");
			writer
					.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" "
							+ "border=\"0\"><tr><td headers=\"protected_content\" width=\"16\" "
							+ "align=\"left\"><img alt=\"protected content\" src=\""
							+ Defines.ICON_ROOT
							+ "key.gif\" width=\"16\" height=\"16\" />"
							+ "</td><td headers=\"\"  align=\"left\">"
							+ "<span class=\"fnt\">A key icon displayed in a page "
							+ "indicates that the page is secure and "
							+ "password-protected.</span></td></tr></table>");
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this, e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(con);
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	/**
	 * Get the Workspaces available for the User
	 * 
	 * @param con
	 * @param out
	 * @param sUserId
	 * @param sInternal
	 * @param sLinkId
	 * @param bAdmin
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void getMyProjects(Connection con, PrintWriter out,
			String sUserId, String sInternal, String sLinkId, boolean bAdmin,
			boolean bWorkflowAdmin) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		String pstmtQuery = "";
		int iCount = 0;
		boolean bPrintNote = false;
		try {

			if (bAdmin || bWorkflowAdmin) {
				iCount = 2;
			} else {
				iCount = getProjectCount(con, sUserId);
				logger.debug("Project Count === " + iCount);
			}

			if (bAdmin) {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY,IBM_ONLY FROM "
						+ "ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND "
						+ "PROJECT_STATUS != ? AND PROJECT_TYPE = ? with ur";
			} else if (bWorkflowAdmin) {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY,IBM_ONLY FROM "
						+ "ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND "
						+ "PROJECT_STATUS != ? AND PROJECT_TYPE = ? "
						+ "and ((is_private='A' and process='Workflow') or "
						+ "(project_id in (SELECT USER_PROJECT_ID FROM "
						+ "ETS.ETS_USERS WHERE USER_ID ='"
						+ sUserId
						+ "' and active_flag='A'))) with ur";

			} else {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY,IBM_ONLY FROM "
						+ "ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND "
						+ "PROJECT_STATUS != ? AND PROJECT_TYPE = ? AND "
						+ "PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
						+ "ETS.ETS_USERS WHERE USER_ID = ?  "
						+ "AND ACTIVE_FLAG = ?) with ur";
			}

			// to get the primary contact..
			String strQuery = "SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || "
					+ "LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS A, "
					+ "ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND "
					+ "B.USER_PROJECT_ID = ? AND B.PRIMARY_CONTACT = ? with ur";

			if (bAdmin || bWorkflowAdmin) {
				sQuery
						.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,IBM_ONLY FROM "
								+ "ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='P' "
								+ "AND PARENT_ID='0' AND PROJECT_TYPE = 'AIC' "
								+ "AND PROJECT_STATUS != '"
								+ Defines.WORKSPACE_DELETE
								+ "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM "
								+ "ETS.ETS_USER_WS WHERE USER_ID = '"
								+ sUserId
								+ "') ORDER BY PROJECT_NAME with ur");
			} else {
				sQuery
						.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY,IBM_ONLY FROM "
								+ "ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='P' "
								+ "AND PARENT_ID='0' AND PROJECT_TYPE = 'AIC' AND "
								+ "PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
								+ "ETS.ETS_USERS WHERE USER_ID = '"
								+ sUserId
								+ "' AND ACTIVE_FLAG = '"
								+ Defines.USER_ENTITLED
								+ "') AND PROJECT_STATUS != '"
								+ Defines.WORKSPACE_DELETE
								+ "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM "
								+ "ETS.ETS_USER_WS WHERE USER_ID = '"
								+ sUserId
								+ "') ORDER BY PROJECT_NAME with ur");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("AICConnectServlet::getMyProjects::Query : "
						+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sProjID = rs.getString(1);
				String sProjName = rs.getString(2);
				String sCompany = rs.getString(3);
				String sIBMOnly = rs.getString(4);

				vDetails.addElement(new String[] { sProjID, sProjName,
						sCompany, sIBMOnly });

			}

			if (vDetails != null && vDetails.size() > 0) {

				out.println("<table summary=\"\" cellspacing=\"0\" "
						+ "cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out
						.println("<tr><td headers=\"project_td1\"  class=\"tdblue\">");
				out.println("<table summary=\"\" cellspacing=\"0\" "
						+ "cellpadding=\"0\" border=\"0\" width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"project_td2\"  height=\"18\" "
						+ "class=\"tblue\">&nbsp;My Sales WorkSpaces</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"project_td3\"  width=\"443\">");
				out.println("<table summary=\"\" cellspacing=\"1\" "
						+ "cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"middle\">");
				out
						.println("<td headers=\"project_td4\"  style=\"background-color: #ffffff;\" "
								+ "align=\"center\">");
				out.println("<table summary=\"\" cellspacing=\"0\" "
						+ "cellpadding=\"2\" border=\"0\" width=\"100%\">");

				boolean bOddRow = true;

				pstmt = con.prepareStatement(pstmtQuery);
				logger.debug("Stmt of AIC Workspace === " + pstmt);

				// prepare statement to get the primary contact...
				pstmt1 = con.prepareStatement(strQuery);
				logger.debug("Stmt of primary contact === " + pstmt1);

				for (int i = 0; i < vDetails.size(); i++) {
					String sTemp[] = (String[]) vDetails.elementAt(i);
					String sProjID = sTemp[0];
					String sProjName = sTemp[1];
					String sCompany = sTemp[2];
					String sIbmOnly = sTemp[3];
					String sPrimaryContact = "";

					pstmt.setString(1, sProjID);
					pstmt.setString(2, Defines.WORKSPACE_DELETE);
					pstmt.setString(3, Defines.AIC_WORKSPACE_TYPE);
					if (!bAdmin && !bWorkflowAdmin) {
						pstmt.setString(4, sUserId);
						pstmt.setString(5, Defines.USER_ENTITLED);
					}

					ResultSet rset = null;

					rset = pstmt.executeQuery();
					logger.debug("AIC Proj Res =" + rset);

					pstmt1.setString(1, sProjID);
					pstmt1.setString(2, "Y");

					ResultSet rsPrimary = null;
					rsPrimary = pstmt1.executeQuery();

					if (rsPrimary.next()) {
						sPrimaryContact = rsPrimary.getString(1);
					}

					ETSDBUtils.close(rsPrimary);

					if (i == 0) {

						if (iCount > 1) {
							logger.debug("Inside iCount > 1");
							out.println("<tr valign=\"top\" >");
							out
									.println("<td headers=\"td_customize\"  colspan=\"4\" "
											+ "align=\"right\" valign=\"top\" "
											+ "style=\"color:#000000; "
											+ "font-weight: normal\"><a href=\""
											+ Defines.SERVLET_PATH
											+ "AICCustomizeServlet.wss?"
											+ "type=P&flag=Y&linkid="
											+ sLinkId
											+ "\">Customize this list</a></td>");
							out.println("</tr>");
						}

						out.println("<tr valign=\"top\" >");
						out
								.println("<td headers=\"wsname\"  colspan=\"2\" align=\"left\" "
										+ "valign=\"top\" style=\"color:#000000; "
										+ "font-weight: normal\"><b>Workspace name</b>"
										+ "</td>");
						out
								.println("<td headers=\"client\"  width=\"80\" align=\"left\" "
										+ "valign=\"top\" style=\"color:#000000; "
										+ "font-weight: normal\"><b>Client</b></td>");
						out
								.println("<td headers=\"contact\"  align=\"left\" valign=\"top\" "
										+ "style=\"color:#000000; font-weight: normal\"><b>"
										+ "Primary contact</b></td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out
								.println("<td headers=\"td_gray\" colspan=\"4\" align=\"left\" "
										+ "style=\"background-color: #ffffff; "
										+ "font-weight: normal;\">");
						out.println("<!-- Gray dotted line -->");
						out.println("<table width=\"100%\" cellpadding=\"0\" "
								+ "cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out
								.println("<td headers=\"topimage\" width=\"1\"><img src=\""
										+ Defines.TOP_IMAGE_ROOT
										+ "c.gif\" width=\"1\" height=\"1\" "
										+ "alt=\"\" /></td>");
						out.println("<td headers=\"v11image\" background=\""
								+ Defines.V11_IMAGE_ROOT
								+ "gray_dotted_line.gif\" width=\"431\">"
								+ "<img alt=\"\" border=\"0\" height=\"1\" "
								+ "width=\"100%\" src=\"//www.ibm.com/i/"
								+ "c.gif\"/></td>");
						out
								.println("<td headers=\"topimage2\" width=\"1\"><img src=\""
										+ Defines.TOP_IMAGE_ROOT
										+ "c.gif\" width=\"1\" height=\"1\" "
										+ "alt=\"\" /></td>");
						out.println("</tr>");
						out.println("</table>");
						out.println("<!-- End Gray dotted line -->");
						out.println("</td>");
						out.println("</tr>");

					}

					if (bOddRow) {
						out
								.println("<tr style=\"background-color: #eeeeee\" >");
						bOddRow = false;
					} else {
						out.println("<tr >");
						bOddRow = true;
					}

					String sAstForExtWrkspc = "";
					if (!sInternal.equalsIgnoreCase("E")
							&& sIbmOnly.equalsIgnoreCase("N")) {
						sAstForExtWrkspc = "<span class=\"ast\">*</span>";
						bPrintNote = true;
					}

					out
							.println("<td headers=\"iconroot\"  width=\"16\" align=\"left\" "
									+ "valign=\"top\"><img src=\""
									+ Defines.ICON_ROOT
									+ "fw.gif\" width=\"16\" height=\"16\" alt=\""
									+ sProjName + "\" border=\"0\"/></td>");
					out
							.println("<td headers=\"projservlet\"  width=\"150\" align=\"left\" "
									+ "valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "ETSProjectsServlet.wss?proj="
									+ sProjID
									+ "&linkid="
									+ sLinkId
									+ "\" class=\"fbox\"><b>"
									+ sProjName
									+ "</b></a>" + sAstForExtWrkspc + "</td>");
					out
							.println("<td headers=\"scompany\"  width=\"80\" align=\"left\" "
									+ "valign=\"top\" style=\"color:#000000; "
									+ "font-weight: normal\">"
									+ sCompany
									+ "</td>");
					out.println("<td headers=\"sprimary\"  align=\"left\" "
							+ "valign=\"top\" style=\"color:#000000; "
							+ "font-weight: normal\">" + sPrimaryContact
							+ "</td>");
					out.println("</tr>");

				}
				// print the note at the bottom of table
				String sAst = "<span class=\"ast\">*</span>";
				if (bPrintNote) {
					out
							.println("<tr><td header=\"info\" colspan=\"4\" class=\"small\" align=\"left\">"
									+ sAst
									+ "<span style=\"color:#000000;font-weight:normal;\" >Denotes workspace may also be used by external customers</span></td></tr>");
				}
				// end of comment
				out.println("</table>");
				out.println("</td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("</td></tr></table>");
				out.println("<br />");

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(pstmt1);

		}
	}

	/**
	 * Get the Count of Projects
	 * 
	 * @param con
	 * @param sUserId
	 * @return iCount
	 * @throws SQLException
	 * @throws Exception
	 */
	private static int getProjectCount(Connection con, String sUserId)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery
					.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS "
							+ "WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
							+ "ETS.ETS_USERS WHERE USER_ID = '"
							+ sUserId
							+ "' AND ACTIVE_FLAG = '"
							+ Defines.USER_ENTITLED
							+ "') AND PROJECT_OR_PROPOSAL = 'P' AND PROJECT_STATUS != '"
							+ Defines.WORKSPACE_DELETE
							+ "' AND PROJECT_TYPE='AIC' with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("AICConnectServlet::getProjectCount::Query : "
						+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {

				iCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return iCount;
	}

	/**
	 * Get the Count of Workspaces
	 * 
	 * @param con
	 * @param sUserId
	 * @return iCount
	 * @throws SQLException
	 * @throws Exception
	 */
	private static int getWorkspaceCount(Connection con, String sUserId)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS "
					+ "WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM "
					+ "ETS.ETS_USERS WHERE USER_ID = '" + sUserId
					+ "' and active_flag='A' ) AND PROJECT_STATUS != '"
					+ Defines.WORKSPACE_DELETE
					+ "' AND PROJECT_TYPE='AIC' with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("AICConnectServlet::getWorkspaceCount::Query : "
						+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {

				iCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return iCount;
	}

	/**
	 * Get the Customize count
	 * 
	 * @param con
	 * @param sUserId
	 * @return iCount
	 * @throws SQLException
	 * @throws Exception
	 */
	private static int getCustomizeCount(Connection con, String sUserId)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery
					.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_USER_WS WHERE "
							+ "USER_ID = '"
							+ sUserId
							+ "' AND project_id in "
							+ " ( select a.project_id from ets.ets_projects a , ets.ets_users b where a.project_type='AIC' "
							+ " and a.project_status !='D'  and a.project_id = b.user_project_id ) "
							+ " with ur ");

			if (logger.isDebugEnabled()) {
				logger.debug("AICConnectServlet::getCustomizeCount::Query : "
						+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {

				iCount = rs.getInt(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return iCount;
	}

	/**
	 * Get the Default Customize List
	 * 
	 * @param con
	 * @param sUserId
	 * @param bAdmin
	 * @return iCount
	 * @throws SQLException
	 * @throws Exception
	 */
	private static int setupDefaultCutomizeList(Connection con, String sUserId,
			boolean bAdmin, boolean bWorkflowAdmin) throws SQLException,
			Exception {

		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			if (bAdmin) {
				sQuery.append("INSERT INTO ETS.ETS_USER_WS (SELECT DISTINCT '"
						+ sUserId + "', PROJECT_ID FROM ETS.ETS_PROJECTS "
						+ "WHERE PROJECT_OR_PROPOSAL NOT IN ('M') AND "
						+ "PROJECT_TYPE='AIC' AND PROJECT_STATUS NOT IN ('"
						+ Defines.WORKSPACE_DELETE + "'))");
			} else if (bWorkflowAdmin) {
				sQuery
						.append("INSERT INTO ETS.ETS_USER_WS (SELECT DISTINCT '"
								+ sUserId
								+ "',PROJECT_ID FROM ETS.ETS_PROJECTS "
								+ "WHERE project_type='AIC' and project_status !='D' "
								+ "and ((is_private='A' and process='Workflow') or "
								+ " (project_id in (select user_project_id from ets.ets_users where user_id='"
								+ sUserId + "' and active_flag='A'))) )");

			} else {
				sQuery
						.append("INSERT INTO ETS.ETS_USER_WS (SELECT DISTINCT "
								+ "USER_ID,USER_PROJECT_ID FROM ETS.ETS_USERS "
								+ "WHERE USER_ID = '"
								+ sUserId
								+ "' and active_flag='A' and user_project_id in"
								+ " (select project_id from ets.ets_projects where project_type='AIC' and project_status !='D') )");
			}

			if (logger.isDebugEnabled()) {
				logger
						.debug("AICConnectServlet::setupDefaultCutomizeList::Query : "
								+ sQuery.toString());
			}

			stmt = con.createStatement();
			iCount = stmt.executeUpdate(sQuery.toString());

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(stmt);
		}

		return iCount;

	}

	private static void displayCreateWorkspace(Connection con, PrintWriter out,
			EdgeAccessCntrl es, String sLink) throws SQLException, Exception {

		try {

			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" "
					+ "cellspacing=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"td_createws\" colspan=\"2\" class=\"tblue\" "
							+ "height=\"18\">&nbsp;Create a workspace</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimg\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out
					.println("<td headers=\"createws\" width=\"16\" valign=\"top\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" width=\"16\" height=\"16\" "
							+ "alt=\"Create a workspace\" /></td>");
			out
					.println("<td headers=\"useraccess\" align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICUserAccessServlet.wss?linkid="
							+ sLink
							+ "&from=create\""
							+ "class=\"fbox\">Create a workspace</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot1\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot2\" colspan=\"2\" "
					+ "style=\"background-color: #cccccc\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot3\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");
			out.println("<br />");

		} catch (Exception e) {
			throw e;
		}

	}

	private static void displayWorkflowEvents(Connection con, PrintWriter out,
			EdgeAccessCntrl es, String sLink) throws SQLException, Exception {

		try {

			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" "
					+ "cellspacing=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"td_wfevents\" colspan=\"2\" class=\"tblue\" "
							+ "height=\"18\">&nbsp;Workflow Events</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimg\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out
					.println("<td headers=\"wfevents\" width=\"16\" valign=\"top\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" width=\"16\" height=\"16\" "
							+ "alt=\"Show Events\" /></td>");
			out
					.println("<td headers=\"events\" align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICUserEventsServlet.wss?linkid="
							+ sLink
							+ "&from=events\""
							+ "class=\"fbox\">Workflow Events Calendar</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot1\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot2\" colspan=\"2\" "
					+ "style=\"background-color: #cccccc\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"topimgroot3\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");
			out.println("<br />");

		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Display NoProject Message
	 * 
	 * @param con
	 * @param out
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayNoProjectContent(Connection con, PrintWriter out)
			throws SQLException, Exception {

		out.println("<table summary=\"\" cellspacing=\"0\" "
				+ "cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr><td headers=\"td_noproj1\"  class=\"tdblue\">");
		out.println("<table summary=\"\" cellspacing=\"0\" "
				+ "cellpadding=\"0\" border=\"0\" width=\"443\">");
		out.println("<tr>");
		out
				.println("<td headers=\"td_noproj2\"  height=\"18\" class=\"tdblue\">"
						+ "&nbsp;About Sales Collboration</td>");
		out.println("</tr>");
		out.println("<tr><td headers=\"td_noproj3\"  width=\"443\">");

		out.println("<table summary=\"\" cellspacing=\"1\" "
				+ "cellpadding=\"2\" border=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\">");
		out
				.println("<td headers=\"td_noproj4\"  style=\"background-color: #eeeeee; "
						+ "color: #000000; font-weight: normal\" align=\"center\" >");
		out.println("<table summary=\"\" cellspacing=\"1\" "
				+ "cellpadding=\"3\" border=\"0\" width=\"100%\">");

		ETSProperties prop = new ETSProperties();

		out
				.println("<td headers=\"td_noproj5\"  align=\"left\" valign=\"top\">"
						+ "To help us work with our clients as on demand partners the "
						+ "Collboration Center provides clients with 24/7 online access "
						+ "to their pertinent business and project information via a secure "
						+ "workspace and a comprehensive suite of on demand tools. <br />"
						+ "<br />To access these tools, clients need <b>proper user "
						+ "permission</b> in addition to an IBM ID.<br /><br />"
						+ "If you wish to work with IBM Sales Colloboration, please "
						+ "<a href=\""
						+ prop.getContactUsURL()
						+ "\" class=\"fbox\">contact us</a> for more information."
						+ "<br /><br /></td>");
		out.println("</table>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("</td></tr></table>");

		out.println("<br />");

	}

	/**
	 * Display News about Sales
	 * 
	 * @param con
	 * @param out
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayNewsAndIdeas(Connection con, PrintWriter out)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {

			out.println("<table summary=\"\" width=\"150\" "
					+ "cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"newsabtsales\" colspan=\"2\" class=\"tblue\" "
							+ "height=\"18\">&nbsp;News about Sales</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"imgroot1\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			sQuery.append("SELECT IMAGE_ALT_TEXT,INFO_DESC,INFO_LINK FROM "
					+ "ETS.ETS_PROJECT_INFO WHERE PROJECT_ID='ETS_NEWS_IDEAS' "
					+ "ORDER BY INFO_MODULE with ur");

			if (logger.isDebugEnabled()) {
				logger.debug("displayNewsAndIdeas::Query : "
						+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sText = ETSUtils.checkNull(rs.getString(1));
				String sDesc = ETSUtils.checkNull(rs.getString(2));
				String sLink = ETSUtils.checkNull(rs.getString(3));

				out.println("<tr>");
				out.println("<td headers=\"stext\" colspan=\"2\">" + sText
						+ "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out
						.println("<td headers=\"icroot\" width=\"16\" valign=\"top\"><img src=\""
								+ Defines.ICON_ROOT
								+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				out
						.println("<td headers=\"slink\" align=\"left\" valign=\"top\"><a href=\""
								+ sLink
								+ "\" class=\"fbox\">"
								+ sDesc
								+ "</a></td>");
				out.println("</tr>");

				// divider

				out.println("<tr valign=\"top\">");
				out
						.println("<td headers=\"img_news1\" colspan=\"2\"><img src=\""
								+ Defines.TOP_IMAGE_ROOT
								+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"img_news2\" colspan=\"2\" "
						+ "style=\"background-color: #cccccc\"><img src=\""
						+ Defines.TOP_IMAGE_ROOT
						+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out
						.println("<td headers=\"img_news3\" colspan=\"2\"><img src=\""
								+ Defines.TOP_IMAGE_ROOT
								+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

			}

			out.println("</table>");
			out.println("<br />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	/**
	 * Display Manage Templates
	 * 
	 * @param con
	 * @param out
	 * @param es
	 * @param sLink
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayManageTemplates(Connection con, PrintWriter out,
			EdgeAccessCntrl es, String sLink) throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {
			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" "
					+ "cellspacing=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"man_templates1\" colspan=\"2\" class=\"tblue\" "
							+ "height=\"18\">&nbsp;Dynamic Table &nbsp;Templates</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"man_templates2\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"man_templates3\" width=\"16\" valign=\"top\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			out
					.println("<td headers=\"man_templates4\" align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "displayTemplateMenu.wss?linkid="
							+ sLink
							+ "\" class=\"fbox\">Manage Templates</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"man_templates5\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"man_templates6\" colspan=\"2\" "
					+ "style=\"background-color: #cccccc\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"man_templates7\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	/**
	 * Display Metrics Reports
	 * 
	 * @param con
	 * @param out
	 * @param es
	 * @param sLink
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayMetricsReports(Connection con, PrintWriter out,
			EdgeAccessCntrl es, String sLink) throws SQLException, Exception {
		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {
			out.println("<table summary=\"\" width=\"150\" "
					+ "cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"metrics_rep1\" colspan=\"2\" "
					+ "class=\"tblue\" height=\"18\">"
					+ "&nbsp;Collaboration Center Reports</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"metrics_rep2\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"metrics_rep3\" width=\"16\" valign=\"top\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			out
					.println("<td headers=\"metrics_rep4\" align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICMetricsServlet.wss?"
							+ "project_id=metrics&option=list&linkid="
							+ sLink
							+ "&func=AIC\" class=\"fbox\">Generate Report</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"metrics_rep5\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"metrics_rep6\" colspan=\"2\" "
					+ "style=\"background-color: #cccccc\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out
					.println("<td headers=\"metrics_rep7\" colspan=\"2\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	/**
	 * Display New Requests
	 * 
	 * @param con
	 * @param out
	 * @param es
	 * @param sLink
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayOptions(Connection con, PrintWriter out,
			EdgeAccessCntrl es, String sLink) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {
			out.println("<table summary=\"\" width=\"150\" "
					+ "cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out
					.println("<td headers=\"disp_opt1\" colspan=\"2\" class=\"tblue\" "
							+ "height=\"18\">&nbsp;New Requests</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_opt2\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out
					.println("<td headers=\"disp_opt3\" width=\"16\" valign=\"top\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			out
					.println("<td headers=\"disp_opt4\" align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICUserAccessServlet.wss?linkid="
							+ sLink
							+ "&from=req\" "
							+ "class=\"fbox\">Request Access to "
							+ "Another Workspace</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_opt5\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_opt6\" colspan=\"2\" "
					+ "style=\"background-color: #cccccc\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_opt7\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<br />");

		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	/**
	 * This method displays pending requests for a workspace owner if there is
	 * any to display.
	 * 
	 * @param con
	 * @param out
	 * @param sUserId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayPendingRequests(Connection con, PrintWriter out,
			String sUserId, String sUserEmail, String sSortOrder)
			throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			UnbrandedProperties unBrandprop = PropertyFactory
					.getProperty(Defines.AIC_WORKSPACE_TYPE);

			if (sSortOrder == null || sSortOrder.trim().equalsIgnoreCase("")) {
				sSortOrder = "date_requested desc ";
			} else {
				sSortOrder = sSortOrder.trim();
			}

			sQuery
					.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,"
							+ "REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE lcase(MGR_EMAIL) = '"
							+ sUserEmail.toLowerCase() + "' AND STATUS in ('"
							+ Defines.ACCESS_PENDING + "','"
							+ Defines.ACCESS_APPROVED + "') "
							+ " AND PROJECT_TYPE='AIC' order by " + sSortOrder
							+ " with ur");

			if (logger.isDebugEnabled()) {
				logger
						.debug("AICConnectServlet::displayPendingRequests::Query : "
								+ sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sUserFor = rs.getString(1);
				String sProjName = rs.getString(2);

				String sTempDate = rs.getTimestamp("DATE_REQUESTED").toString();
				String sDateRequested = sTempDate.substring(5, 7) + "/"
						+ sTempDate.substring(8, 10) + "/"
						+ sTempDate.substring(0, 4);
				String sRequestId = rs.getString(4);
				vDetails.addElement(new String[] { sUserFor, sProjName,
						sDateRequested, sRequestId });

			}
			boolean headerPrinted = false;

			if (vDetails != null && vDetails.size() > 0) {

				String bgColor = "#ffffff";

				for (int i = 0; i < vDetails.size(); i++) {

					String sTemp[] = (String[]) vDetails.elementAt(i);

					String sUserFor = sTemp[0];
					String sProjName = sTemp[1];
					String sDateRequested = sTemp[2];
					String sRequestId = sTemp[3];

					ETSProperties prop = new ETSProperties();
					String sMsg = prop.getSApprovalDefault();

					AICProcReqAccessFunctions procFunc = new AICProcReqAccessFunctions();
					String status = procFunc.getStatus(sRequestId, con);
					if (!status.equals("hasEntitlement")) {

						ETSUserDetails userDtls = new ETSUserDetails();
						userDtls.setWebId(sUserFor);
						userDtls.extractUserDetails(con);

						if (!(userDtls.getUserType() == userDtls.USER_TYPE_INTERNAL || userDtls
								.getUserType() == userDtls.USER_TYPE_EXTERNAL)) {
							status = "Incomplete user (profile) information ";
						}

						if (!headerPrinted) {

							out
									.println("<table summary=\"\" cellspacing=\"0\" "
											+ "cellpadding=\"0\" border=\"0\"  "
											+ "width=\"443\">");
							out
									.println("<tr><td headers=\"disp_pend1\"  class=\"tdblue\">");

							out
									.println("<table summary=\"\" cellspacing=\"0\" "
											+ "cellpadding=\"0\" border=\"0\" "
											+ "width=\"443\">");
							out.println("<tr>");
							out
									.println("<td headers=\"disp_pend2\"  height=\"18\" "
											+ "class=\"tblue\"><a name=\"pending_list\">"
											+ "&nbsp;Pending access requests</a></td>");
							out.println("</tr>");
							out.println("<tr><td headers=\"disp_pend3\"  "
									+ "width=\"443\">");
							out
									.println("<table summary=\"\" cellspacing=\"1\" "
											+ "cellpadding=\"2\" border=\"0\" "
											+ "width=\"100%\">");
							out.println("<tr valign=\"middle\">");
							out.println("<td headers=\"disp_pend4\"  "
									+ "style=\"background-color: #ffffff; "
									+ "color: #000000; font-weight: normal;\" "
									+ "align=\"center\">");
							out
									.println("<table summary=\"\" cellspacing=\"0\" "
											+ "cellpadding=\"0\" border=\"0\" "
											+ "width=\"100%\">");

							out.println("<tr valign=\"top\">");
							out
									.println("<td headers=\"disp_pend5\" colspan=\"4\" ><img src=\""
											+ Defines.TOP_IMAGE_ROOT
											+ "c.gif\" width=\"1\" "
											+ "height=\"2\" alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out
									.println("<td headers=\"disp_pend6\" colspan=\"4\"  "
											+ "align=\"left\">"
											+ sMsg
											+ "</td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out
									.println("<td headers=\"disp_pend7\" colspan=\"4\"><img src=\""
											+ Defines.TOP_IMAGE_ROOT
											+ "c.gif\" width=\"1\" height=\"4\" "
											+ "alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out
									.println("<td headers=\"disp_pend8\" colspan=\"4\" "
											+ "style=\"background-color: #cccccc\">"
											+ "<img src=\""
											+ Defines.TOP_IMAGE_ROOT
											+ "c.gif\" width=\"1\" height=\"1\" "
											+ "alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out
									.println("<td headers=\"disp_pend9\" colspan=\"4\"><img src=\""
											+ Defines.TOP_IMAGE_ROOT
											+ "c.gif\" width=\"1\" height=\"6\" "
											+ "alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr>");
							out.println("<th id=\"req_for\" width=\"100\" "
									+ "align=\"left\" valign=\"top\" "
									+ "class=\"small\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICConnectServlet.wss?linkid="
									+ unBrandprop.getLinkID()
									+ "&sort=user_id\"><b>User<br />"
									+ "[IBM ID]</b></a></td>");
							out.println("<th id=\"req_proj\" width=\"143\" "
									+ "align=\"left\" valign=\"top\" "
									+ "class=\"small\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICConnectServlet.wss?linkid="
									+ unBrandprop.getLinkID()
									+ "&sort=project_name\">"
									+ "<b>Workspace<br /> Requested&#42;</b>"
									+ "</a></td>");
							out.println("<th id=\"req_date\" width=\"100\" "
									+ "align=\"left\" valign=\"top\"  "
									+ "class=\"small\"><a href=\""
									+ Defines.SERVLET_PATH
									+ "AICConnectServlet.wss?linkid="
									+ unBrandprop.getLinkID()
									+ "&sort=date_requested\"><b>Date<br />"
									+ "requested</b></a></td>");
							out
									.println("<th id=\"req_by\" width=\"100\" align=\"left\" "
											+ "valign=\"top\"  class=\"small\"><b><br />Status"
											+ "</b></td>");
							out.println("</tr>");
							headerPrinted = true;

						}

						out.println("<tr valign=\"top\">");
						out
								.println("<td headers=\"disp_pend10\" colspan=\"4\" ><img src=\""
										+ Defines.TOP_IMAGE_ROOT
										+ "c.gif\" width=\"1\" height=\"5\" "
										+ "alt=\"\" /></td>");
						out.println("</tr>");
						out
								.println("<tr style=\"background-color:"
										+ (bgColor = (bgColor.equals("#eeeeee") ? "#ffffff"
												: "#eeeeee"))
										+ "\" valign=\"top\">");
						if (status.equals("New Request")
								|| status.startsWith("Forwarded by")) {
							out.println("<td headers=\"req_for\" "
									+ "width=\"100\" align=\"left\" "
									+ "valign=\"top\" class=\"small\">"
									+ "<a class=\"fbox\" href=\""
									+ Defines.SERVLET_PATH
									+ "AICProcReqAccessServlet.wss?linkid="
									+ unBrandprop.getLinkID()
									+ "&option=showreq&requestid=" + sRequestId
									+ "&ibmid=" + sUserFor + "\">"
									+ ETSUtils.getUsersName(con, sUserFor)
									+ "<br />[" + sUserFor + "]</a></td>");
						} else {
							out
									.println("<td headers=\"req_suser\" "
											+ "width=\"100\" align=\"left\" valign=\"top\"  "
											+ "class=\"small\">"
											+ ETSUtils.getUsersName(con,
													sUserFor) + "<br />["
											+ sUserFor + "]</td>");
						}

						out
								.println("<td headers=\"req_proj\" "
										+ "width=\"143\" align=\"left\" valign=\"top\"  "
										+ "class=\"small\">" + sProjName
										+ "</td>");
						out
								.println("<td headers=\"req_date\" "
										+ "width=\"100\" align=\"left\" valign=\"top\"  "
										+ "class=\"small\">" + sDateRequested
										+ "</td>");
						out
								.println("<td headers=\"req_by\" "
										+ "width=\"100\" align=\"left\" valign=\"top\"  "
										+ "class=\"small\">" + status + "</td>");
						out.println("</tr>");

					}
				}

				if (headerPrinted) {

					out.println("<tr valign=\"top\">");
					out
							.println("<td headers=\"disp_pend11\" colspan=\"4\" ><img src=\""
									+ Defines.TOP_IMAGE_ROOT
									+ "c.gif\" width=\"1\" height=\"6\" "
									+ "alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"disp_pend12\" colspan=\"4\" "
							+ "style=\"background-color: #cccccc\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"1\" height=\"1\" "
							+ "alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"disp_pend13\" colspan=\"4\" "
							+ "align=\"left\">&#42;Workspace Requested as "
							+ "entered on the request access form</td>");
					out.println("</tr>");

					out.println("</table>");
					out.println("</td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("</td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("</td></tr></table>");

					out.println("<br />");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	private static void getCapabilities(ETSProjectInfoBean projBean,
			PrintWriter out) throws Exception {

		StringBuffer sQuery = new StringBuffer("");

		try {

			out
					.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" "
							+ "border=\"0\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_pend14\" width=\"16\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" width=\"16\" height=\"16\" "
					+ "alt=\"\" border=\"0\" /></td>");
			String[] sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 5);
			out.println("<td headers=\"disp_pend15\" width=\"202\"><a href=\""
					+ sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
			out.println("<td headers=\"disp_pend16\" width=\"7\">&nbsp;</td>");
			out.println("<td headers=\"disp_pend17\" width=\"16\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" "
					+ "border=\"0\" /></td>");
			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 6);
			out.println("<td headers=\"disp_pend18\" width=\"202\"><a href=\""
					+ sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");

			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"disp_pend19\"><img src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\"16\" height=\"4\" "
							+ "alt=\"\" /></td>");
			out.println("<td headers=\"disp_pend20\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"202\" height=\"4\" "
					+ "alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"disp_pend21\" width=\"16\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" width=\"16\" height=\"16\" "
					+ "alt=\"\" border=\"0\" /></td>");
			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 7);
			out.println("<td headers=\"disp_pend22\" width=\"202\"><a href=\""
					+ sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
			out.println("<td headers=\"disp_pend23\" width=\"7\">&nbsp;</td>");
			out.println("<td headers=\"disp_pend24\" width=\"16\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" width=\"16\" height=\"16\" alt=\"\" "
					+ "border=\"0\" /></td>");
			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 8);
			out.println("<td headers=\"disp_pend25\" width=\"202\"><a href=\""
					+ sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"disp_pend26\" colspan=\"2\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	private static void displayAccessRequests(Connection con, PrintWriter out,
			String sUserId, String sUserEmail) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery
					.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID, requested_by FROM ETS.ETS_ACCESS_REQ WHERE user_id = '"
							+ sUserId
							+ "' AND STATUS in ('"
							+ Defines.ACCESS_PENDING
							+ "','"
							+ Defines.ACCESS_APPROVED
							+ "') and lcase(mgr_email) != '"
							+ sUserEmail.toLowerCase()
							+ "' and PROJECT_TYPE='AIC' union ");
			sQuery
					.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID, requested_by FROM ETS.ETS_ACCESS_REQ WHERE requested_by = '"
							+ sUserId
							+ "' AND STATUS in ('"
							+ Defines.ACCESS_PENDING
							+ "','"
							+ Defines.ACCESS_APPROVED
							+ "') and lcase(mgr_email) != '"
							+ sUserEmail.toLowerCase()
							+ "' and PROJECT_TYPE='AIC' order by date_requested desc with ur");

			if (logger.isDebugEnabled()) {
				logger
						.debug("ETSConnectServlet::displayAccessRequests::Query : "
								+ sQuery.toString());
			}
			System.out.println("hello the query is" + sQuery);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sUserFor = rs.getString(1);
				String sProjName = rs.getString(2);

				String sTempDate = rs.getTimestamp("DATE_REQUESTED").toString();
				String sDateRequested = sTempDate.substring(5, 7) + "/"
						+ sTempDate.substring(8, 10) + "/"
						+ sTempDate.substring(0, 4);
				String sRequestId = rs.getString(4);
				String submittedBy = rs.getString(5);
				vDetails.addElement(new String[] { sUserFor, sProjName,
						sDateRequested, sRequestId, submittedBy });

			}
			boolean headerPrinted = false;

			if (vDetails != null && vDetails.size() > 0) {

				String bgColor = "#ffffff";
				for (int i = 0; i < vDetails.size(); i++) {
					String sTemp[] = (String[]) vDetails.elementAt(i);
					String sUserFor = sTemp[0];
					String sProjName = sTemp[1];
					String sDateRequested = sTemp[2];
					String sRequestId = sTemp[3];
					String subBy = sTemp[4];

					AICProcReqAccessFunctions procFunc = new AICProcReqAccessFunctions();
					String status = procFunc.getStatus(sRequestId, con);
					if (!status.equals("hasEntitlement")) {
						ETSUserDetails userDtls = new ETSUserDetails();
						userDtls.setWebId(sUserFor);
						userDtls.extractUserDetails(con);
						if (!(userDtls.getUserType() == userDtls.USER_TYPE_INTERNAL || userDtls
								.getUserType() == userDtls.USER_TYPE_EXTERNAL)) {
							status = "Incomplete user (profile) information ";
						}

						if (!headerPrinted) {
							out
									.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
							out
									.println("<tr><td headers=\"acc_req1\"  class=\"tdblue\">");

							out
									.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
							out.println("<tr>");
							out
									.println("<td headers=\"acc_req2\"  height=\"18\" class=\"tblue\">&nbsp;Status of requests submitted by you or for you</td>");
							out.println("</tr>");
							out
									.println("<tr><td headers=\"acc_req3\"  width=\"443\">");
							out
									.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
							out.println("<tr valign=\"middle\">");
							out
									.println("<td headers=\"acc_req4\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"center\">");

							out
									.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

							out
									.println("<tr valign=\"top\"><td headers=\"acc_req5\" colspan=\"5\" ><img src=\""
											+ Defines.TOP_IMAGE_ROOT
											+ "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
							out.println("<tr>");
							out
									.println("<th id=\"req_for\" width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>User<br />[IBM ID]</b></td>");
							out
									.println("<th id=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\"><b>Workspace<br /> Requested&#42;</b></td>");
							out
									.println("<th id=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Date<br />requested</b></td>");
							out
									.println("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Requested<br />by</b></td>");
							out
									.println("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b><br />Status</b></td>");
							out.println("</tr>");
							headerPrinted = true;
						}

						out
								.println("<tr valign=\"top\"><td headers=\"acc_req6\" colspan=\"5\" ><img src=\""
										+ Defines.TOP_IMAGE_ROOT
										+ "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
						out
								.println("<tr style=\"background-color:"
										+ (bgColor = (bgColor.equals("#eeeeee") ? "#ffffff"
												: "#eeeeee"))
										+ "\" valign=\"top\">");
						out
								.println("<td headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">"
										+ ETSUtils.getUsersName(con, sUserFor)
										+ "<br />[" + sUserFor + "]</td>");
						out
								.println("<td headers=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\">"
										+ sProjName + "</td>");
						out
								.println("<td headers=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">"
										+ sDateRequested + "</td>");
						out
								.println("<td headers=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">"
										+ ETSUtils.getUsersName(con, subBy)
										+ "</td>");
						out
								.println("<td headers=\"status\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">"
										+ status + "</td>");
						out.println("</tr>");

					}
				}
				if (headerPrinted) {

					out
							.println("<tr valign=\"top\"><td headers=\"acc_req7\" colspan=\"5\" ><img src=\""
									+ Defines.TOP_IMAGE_ROOT
									+ "c.gif\" width=\"1\" height=\"6\" alt=\"\" /></td></tr>");
					out.println("</table>");

					out.println("</td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("</td>");
					out.println("</tr>");
					out.println("</table>");

					out.println("</td></tr></table>");

					out.println("<br />");
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	public static void displayWorkspaceQueries(PrintWriter writer)
			throws Exception {

		UnbrandedProperties prop = PropertyFactory
				.getProperty(Defines.AIC_WORKSPACE_TYPE);

		//WorkSpace Queries
		writer.println("<table summary=\"\" cellspacing=\"0\" "
				+ "cellpadding=\"0\" border=\"0\"  width=\"443\">");
		writer.println("<tr>");
		writer
				.println("<td headers=\"ws_queries\"  colspan=\"2\" height=\"18\" "
						+ "class=\"tblue\">&nbsp;WorkSpace Queries</td>");
		writer.println("</tr>");
		writer.println("<tr></tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter11\" width=\"16\" valign=\"top\"></td>");
		writer
				.println("<td headers=\"salesteamhome\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=11\" class=\"fbox\">"
						+ aic_rb.getString("aic.salesteamhome").trim()
						+ "</a></td>");
		writer.println("</tr>");
		writer.println("<tr></tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter1\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"All Workspaces\"/></td>");
		writer
				.println("<td headers=\"allws\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=1\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.all").trim() + "</a></td>");
		writer.println("</tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter2\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Workspaces By Sce-Sector\"/></td>");
		writer
				.println("<td headers=\"ws_sce\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=2\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.sceSect").trim()
						+ "</a></td>");
		writer.println("</tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter3\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Workspaces By Sector\"/></td>");
		writer
				.println("<td headers=\"wssector\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=3\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.sector").trim()
						+ "</a></td>");
		writer.println("</tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter4\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Workspaces By Sub-Sector\"/></td>");
		writer
				.println("<td headers=\"ws_subsect\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=4\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.subSect").trim()
						+ "</a></td>");
		writer.println("</tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter5\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Workspaces By Process\"/></td>");
		writer
				.println("<td headers=\"ws_process\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=5\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.process").trim()
						+ "</a></td>");
		writer.println("</tr>");
		writer.println("<tr>");
		writer
				.println("<td headers=\"wsfilter6\" width=\"16\" valign=\"top\"><img src=\""
						+ Defines.ICON_ROOT
						+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Workspaces By Brand\"/></td>");
		writer
				.println("<td headers=\"ws_brand\" align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "AICSalesWSListServlet.wss?linkid="
						+ prop.getLinkID()
						+ "&filter=6\" class=\"fbox\">"
						+ aic_rb.getString("aic.ws.brand").trim() + "</a></td>");
		writer.println("</tr>");
		writer.println("</table>");

		writer.println("<br />");

	}

	public static void displaySiteHelp(ETSParams params, String linkid)
			throws Exception {

		try {
			PrintWriter writer = params.getWriter();
			writer
					.println("<table summary=\"Right navigation for need more help\" cellspacing=\"0\" cellpadding=\"0\" width=\"150\" border=\"0\">");
			writer
					.println("<tr><th id=\"web_support\" colspan=\"2\" class=\"tblue\" width=\"150\" height=\"18\" align=\"left\">&nbsp;&nbsp;Web site support</th>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"site_help1\" colspan=\"2\" valign=\"top\"><img height=\"4\" width=\"1\" alt=\"\" src=\""
							+ Defines.V11_IMAGE_ROOT + " c.gif\" /></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"site_help2\" valign=\"top\" width=\"16\"><a href=\"/technologyconnect/cchelp.html\" onclick=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\"  onkeypress=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" target=\"new\" class=\"fbox\"><img src=\""
							+ Defines.ICON_ROOT
							+ "popup.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"\" /></a></td>");
			writer
					.println("<td headers=\"site_help3\" width=\"134\"><a href=\"/technologyconnect/cchelp.html\" onclick=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" onkeypress=\"window.open('/technologyconnect/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" target=\"new\" class=\"fbox\">Contact help desk</a></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer
					.println("<td headers=\"site_help4\" valign=\"top\" width=\"16\"><a href=\""
							+ Global.getUrl("EdCQMDServlet.wss?linkid="
									+ linkid)
							+ "\" class=\"fbox\"><img src=\""
							+ Defines.ICON_ROOT
							+ "fw.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"\" /></a></td>");
			writer
					.println("<td headers=\"site_help5\" width=\"134\"><a href=\""
							+ Global.getUrl("EdCQMDServlet.wss?linkid="
									+ linkid)
							+ "\" class=\"fbox\">Report a problem online</a></td>");
			writer.println("</tr>");
			writer.println("</table>");
		} catch (Exception eX) {
			throw eX;
		}

	}

}