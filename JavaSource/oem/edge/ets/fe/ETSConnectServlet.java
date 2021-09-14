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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.common.Global;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.UserProfileTimeZone;
import oem.edge.ets.fe.self.ETSSelfAssessment;
import oem.edge.ets.fe.self.ETSSelfAssessmentExpectation;
import oem.edge.ets.fe.self.ETSSelfConstants;
import oem.edge.ets.fe.self.ETSSelfDAO;
import oem.edge.ets.fe.setmet.ETSSetMet;
import oem.edge.ets.fe.setmet.ETSSetMetDAO;
import oem.edge.ets.fe.setmet.ETSSetMetExpectation;
import oem.edge.ets.fe.survey.ETSSurvey;
import oem.edge.ets.fe.survey.ETSSurveyDAO;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

public class ETSConnectServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.66";

	protected ETSDatabaseManager databaseManager;
	private String mailhost;

	private static Log logger = EtsLogger.getLogger(ETSConnectServlet.class);

	/**
	 * Method displayNewsAndIdeas.
	 * @param con
	 * @param out
	 * @throws SQLException
	 * @throws Exception
	 */

	private static void displayNewsAndIdeas(Connection con, PrintWriter out) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {

			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" class=\"tblue\" height=\"18\">&nbsp;News and ideas</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			sQuery.append("SELECT IMAGE_ALT_TEXT,INFO_DESC,INFO_LINK FROM ETS.ETS_PROJECT_INFO WHERE PROJECT_ID='ETS_NEWS_IDEAS' ORDER BY INFO_MODULE for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::displayNewsAndIdeas::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sText = ETSUtils.checkNull(rs.getString(1));
				String sDesc = ETSUtils.checkNull(rs.getString(2));
				String sLink = ETSUtils.checkNull(rs.getString(3));

				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\">" + sText + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
				out.println("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\"" + sLink + "\" class=\"fbox\">" + sDesc + "</a></td>");
				out.println("</tr>");

				// divider

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
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
	 * Method getMyProposals.
	 * @param con
	 * @param out
	 * @param sUserId
	 * @param sInternal
	 * @param sLinkId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void getMyProposals(Connection con, PrintWriter out, String sUserId, String sInternal, String sLinkId, boolean bAdmin, boolean bITAR) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		// for getting sub workspaces.
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;

		String pstmtQuery = "";

		int iCount = 0;

		try {

			if (bAdmin) {
				iCount = 2;
			} else {
				iCount = getProposalCount(con, sUserId);
			}

			if (bAdmin) {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? for READ ONLY";
			} else {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = ? AND ACTIVE_FLAG = ?) for READ ONLY";
			}

			// to get the primary contact..
			String strQuery = "SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS A, ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND B.USER_PROJECT_ID = ? AND B.PRIMARY_CONTACT = ? with ur";

			if (bAdmin) {
				sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='O' AND PARENT_ID='0' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '" + sUserId + "') AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' ORDER BY PROJECT_NAME for READ ONLY");
			} else {
				sQuery.append(
					"SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='O' AND PARENT_ID='0' AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '"
						+ sUserId
						+ "' AND ACTIVE_FLAG = '"
						+ Defines.USER_ENTITLED
						+ "') AND PROJECT_STATUS != '"
						+ Defines.WORKSPACE_DELETE
						+ "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '"
						+ sUserId
						+ "') AND PROJECT_TYPE = '"
						+ Defines.ETS_WORKSPACE_TYPE
						+ "' ORDER BY PROJECT_NAME for READ ONLY");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getMyProposals::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sProjID = rs.getString(1);
				String sProjName = rs.getString(2);
				String sCompany = rs.getString(3);
				String isITAR = rs.getString(4);

				// check for ITAR here - 5.4.1 sathish
				if (isITAR != null && isITAR.equalsIgnoreCase("Y")) {
					if (bITAR) {
						vDetails.addElement(new String[] { sProjID, sProjName, sCompany });
					}
				} else {
					vDetails.addElement(new String[] { sProjID, sProjName, sCompany });
				}
			}

			if (iCount > 0) {
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr><td headers=\"\"  class=\"tgreen\">");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;My E&TS proposals</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"\"  width=\"443\">");
				out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"middle\">");
				out.println("<td headers=\"\"  style=\"background-color: #ffffff;\"  align=\"center\">");
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");

				out.println("<tr valign=\"top\" >");
				out.println("<td headers=\"\"  colspan=\"4\" align=\"right\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSCustomizeServlet.wss?type=O&flag=Y&linkid=" + sLinkId + "\">Customize this list</a></td>");
				out.println("</tr>");

			}
			
			if (vDetails != null && vDetails.size() > 0) {


				boolean bOddRow = true;

				// prepare statement to get the sub workspaces.
				pstmt = con.prepareStatement(pstmtQuery);

				// prepare statement to get the primary contact...
				pstmt1 = con.prepareStatement(strQuery);

				for (int i = 0; i < vDetails.size(); i++) {

					String sTemp[] = (String[]) vDetails.elementAt(i);

					String sProjID = sTemp[0];
					String sProjName = sTemp[1];
					String sCompany = sTemp[2];

					String sPrimaryContact = "";

					// check to see if this project has any subworkspaces defined...
					pstmt.setString(1, sProjID);
					pstmt.setString(2, Defines.WORKSPACE_DELETE);
					pstmt.setString(3, Defines.ETS_WORKSPACE_TYPE);
					if (!bAdmin) {
						pstmt.setString(4, sUserId);
						pstmt.setString(5, Defines.USER_ENTITLED);
					}

					ResultSet rset = null;

					rset = pstmt.executeQuery();

					pstmt1.setString(1, sProjID);
					pstmt1.setString(2, "Y");

					ResultSet rsPrimary = null;
					rsPrimary = pstmt1.executeQuery();

					if (rsPrimary.next()) {
						sPrimaryContact = rsPrimary.getString(1);
					}

					ETSDBUtils.close(rsPrimary);

					if (i == 0) {

						out.println("<tr valign=\"top\" >");
						out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Workspace name</b></td>");
						out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Client</b></td>");
						out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Primary contact</b></td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">");
						out.println("<!-- Gray dotted line -->");
						out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
						out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("</table>");
						out.println("<!-- End Gray dotted line -->");
						out.println("</td>");
						out.println("</tr>");

					}

					if (bOddRow) {
						out.println("<tr style=\"background-color: #eeeeee\" >");
						bOddRow = false;
					} else {
						out.println("<tr >");
						bOddRow = true;
					}

					out.println("<td headers=\"\"  width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"" + sProjName + "\" border=\"0\" /></td>");
					out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjID + "&linkid=" + sLinkId + "\" class=\"fbox\"><b>" + sProjName + "</b></a></td>");
					out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sCompany + "</td>");
					out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sPrimaryContact + "</td>");
					out.println("</tr>");

					// print the sub workspaces here if available...
					while (rset.next()) {

						String sSubWorkspaceProjectID = rset.getString(1); // cannot be null
						String sSubWorkspaceProjectName = rset.getString(2); // cannot be null
						String sSubWorkspaceCompany = rset.getString(3); // cannot be null
						String isITAR = ETSUtils.checkNull(rset.getString(4));
						String sPrimaryContact1 = "";

						// do not display itar workspaces if user does not have itar entitlement
						// changed for 5.4.1 by sathish

						boolean bDisplay = false;

						if (isITAR.equalsIgnoreCase("Y")) {
							if (bITAR) {
								bDisplay = true;
							}
						} else {
							bDisplay = true;
						}

						if (bDisplay) {

							pstmt1.clearParameters();
							pstmt1.setString(1, sSubWorkspaceProjectID);
							pstmt1.setString(2, "Y");

							ResultSet rsPrimary1 = pstmt1.executeQuery();

							if (rsPrimary1.next()) {
								sPrimaryContact1 = rsPrimary1.getString(1);
							}

							ETSDBUtils.close(rsPrimary1);

							if (bOddRow) {
								out.println("<tr style=\"background-color: #eeeeee\" >");
								bOddRow = false;
							} else {
								out.println("<tr >");
								bOddRow = true;
							}

							out.println("<td headers=\"\"  width=\"16\" valign=\"top\">&nbsp;</td>");

							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">");
							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
							out.println("<tr valign=\"top\" >");
							out.println("<td headers=\"\"  width=\"10\" align=\"left\" valign=\"top\"><span style=\"color: #ccc;font-size:15\">&#183;</span></td>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sSubWorkspaceProjectID + "&linkid=" + sLinkId + "\" class=\"fbox\"><b>" + sSubWorkspaceProjectName + "</b></a></td>");
							out.println("</tr>");
							out.println("</table>");
							out.println("</td>");

							out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sSubWorkspaceCompany + "</td>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sPrimaryContact1 + "</td>");

							out.println("</tr>");
						}
					}

					ETSDBUtils.close(rset);

				}

				ETSProperties properties = new ETSProperties();

				if (properties.displayHelp() == true) {
					out.println("<tr><td headers=\"\" colspan=\"4\" align=\"right\" bgcolor=\"#eeeeee\">");
					out.println("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpading=\"0\"><tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					out.println(
						"<td headers=\"\" width=\"30\" align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=proposal\" target=\"new\" onclick=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=proposal','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=proposal','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Help</a></td>");
					out.println("</tr></table>");
					out.println("</td></tr>");
				}


			}
			
			if (iCount > 0) {
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

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();

		Hashtable params;
		String sLink;


		boolean bClientVoiceDisplayed = false;

		try {
			
			ETSProperties properties = new ETSProperties();
			//v2sagar
			String sDate = "";
			sDate=UserProfileTimeZone.getUTCHeaderDate();

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			ETSProjectInfoBean projBean = (ETSProjectInfoBean) request.getSession(false).getAttribute("ETSProjInfo");

			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(con);
				request.getSession(false).setAttribute("ETSProjInfo", projBean);
			}

			// e&ts connect landing page.. so can set the ptoperty from defines below...
			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);

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
					header = "IBM E&TS Connect";
				} else {
					header = "IBM E&TS Connect for " + assocCompany;
				}

			} else {
				header = "IBM E&TS Connect";
			}
			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);

			EdgeHeader.setSubHeader("Welcome, " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME);
			writer.println(ETSSearchCommon.getMasthead(EdgeHeader));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());

			// display the home page for the user

			Metrics.appLog(con, es.gIR_USERN, "ETS_Landing");

			//v2sagar
			
			writer.println("<script type=\"text/javascript\" language=\"javascript\" src=\""+Global.WebRoot+"/js/UserTimeZoneCookie.js\"></script>");
			
			//writer.println("<script type=\"text/javascript\" language=\"javascript\" src=\"http://localhost:9080/technologyconnect/ets/js/UserTimeZoneCookie.js\"></script>");
		
			writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
			
			writer.println("<tr valign=\"top\"><td headers=\"\" width=\"443\" valign=\"top\" class=\"small\"><" +
					"table summary=\"\" border=\"0\" width=\"100%\">" +
					"<tr>" +
					"<td headers=\"\" width=\"60%\">" + es.gIR_USERN + "</td>" );

			//Server's Time				
		/*	writer.println("<td headers=\"\" width=\"40%\" align=\"right\"><div id=\"sTime\">" + sDate + "<a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone\" target=\"new\" onclick=\"window.open('"
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=425,height=420,left=300,top=150'); return false;\" onkeypress=\"window.open('"
					+ Defines.SERVLET_PATH
					+ "ETSHelpServlet.wss?proj=ETSTimeZone','Help','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=300,height=450,left=300,top=150'); return false;\">" 
					+ "<img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Timezone help\" border=\"0\" /></a></div></td>");*/
			
			writer.println("<td headers=\"\" width=\"25%\" align=\"right\"><div id=\"sTime\">" + sDate +"</div></td>");

			writer.println("<script type=\"text/javascript\"  language=\"javascript\">"); 
			writer.println("var newTime;");			
			writer.println("  newTime =firstTime();");
			writer.println("document.getElementById(\"sTime\").innerHTML = newTime;");
			writer.println("</script>");
			
						
			writer.println("</tr>" +
					"</table>" +
					"</td>");
		//till here v2sagar
			
			writer.println("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			writer.println("<td headers=\"\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\"  width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
			


			writer.println("   <td headers=\"\"  width=\"443\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_LANDING_1&mod=0\" width=\"443\" height=\"149\" alt=\"" + projBean.getImageAltText("ETS_LANDING_1", 0) + "\" /><br /><br />");

			// Added for ASCA recommendations
			writer.println(
					"<table summary=\"\" cellspacing=\"0\" " +
					"cellpadding=\"0\" border=\"0\" width=\"443\">");
			
			writer.println("<tr>");
			writer.println(
					"<td headers=\"td_terms\"  align=\"left\" valign=\"top\">" +
					"IBM employees are bound by IBM's Business Conduct Guidelines in their use of this workspace." +
					"</td>");
			writer.println("</tr>");
			
			writer.println("<tr><td headers=\"td_terms2\"  align=\"left\" valign=\"top\">&nbsp;</td></tr>");
			
			writer.println("<tr>");
			writer.println(
					"<td headers=\"td_terms1\"  align=\"left\" valign=\"top\">" +
					"Other workspace users are bound by any contract with IBM including the web site terms of use." +
					"<a href=\"http://www.ibm.com/legal/us/\" onclick=\"window.open('http://www.ibm.com/legal/us/','IBM Terms of use','scrollbars=yes,resizable=yes,width=600,height=600'); return false;\"  onkeypress=\"window.open('http://www.ibm.com/legal/us/','IBM Terms of use','scrollbars=yes,resizable=yes,width=600,height=600'); return false;\" target=\"new\" class=\"fbox\"><img src=\""+Defines.ICON_ROOT+"popup.gif\" height=\"16\" width=\"16\" border=\"0\" alt=\"Terms of use\" /></a>"+
					"</td>");
			writer.println("</tr>");
			
			writer.println("</table>");
			
			writer.println("<br />");
			// Added for ASCA recommendations
			
			boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD");
			boolean bExecutive = es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD");
			boolean bEntitlement = es.Qualify(Defines.ETS_ENTITLEMENT, "tg_member=MD");
			boolean bITAR = es.Qualify(Defines.ITAR_ENTITLEMENT, "tg_member=MD");

			int iProjectCount = 0;

			if (bAdmin) {
				// super admin can see all projects...
				// changed for 4.4.1
				iProjectCount = 1;
			} else if (bExecutive) {
				// executive can see all the client care workspaces...
				// changed for 4.4.1
				iProjectCount = 1;
			} else {
				// normal members can only access the workspaces if they are a member of the workspaces.
				// changed for 4.4.1
				if (!bEntitlement && !bITAR) {
					iProjectCount = 0;
				} else {
					iProjectCount = getWorkspaceCount(con, es.gIR_USERN);
				}
			}

			// check to see if the customize list has been setup, if not set it up now...
			int iCustomizeCount = getCustomizeCount(con, es.gIR_USERN);
			if (iCustomizeCount == 0 && iProjectCount > 0) {
				// this is a new user and does not have his cusomize list set up...
				if (bAdmin || bExecutive) {
					setupDefaultCutomizeList(con, es.gIR_USERN, true);
				} else {
					setupDefaultCutomizeList(con, es.gIR_USERN, false);
				}
			}

			if (iProjectCount <= 0) {
				displayNoProjectContent(con, writer);
			} else {

				boolean bDisplay = false;

				if (bAdmin || bExecutive) {
					// display all voice of client projects to super admin and executive.
					bDisplay = true;
				}

				bClientVoiceDisplayed = getVoiceOfClientProjects(con, writer, es.gIR_USERN, es.gDECAFTYPE.trim().toUpperCase(), sLink, bAdmin, bExecutive);

				// proposal / project table starts here...

				//writer.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><tr valign=\"top\">");

				// proposals

				getMyProposals(con, writer, es.gIR_USERN, es.gDECAFTYPE.trim().toUpperCase(), sLink, bDisplay, bITAR);

				// projects

				getMyProjects(con, writer, es.gIR_USERN, es.gDECAFTYPE.trim().toUpperCase(), sLink, bDisplay, bITAR);

				ETSDatabaseManager dbManager = new ETSDatabaseManager();

				Vector vOwnerOf = ETSDatabaseManager.getProjectsUserIsOwner(es.gIR_USERN, con);

				if (vOwnerOf != null && vOwnerOf.size() > 0) { // CHG5.4
					// means the logged in person is workspace owner of atleast one project...
					// so display the pending request
					displayPendingRequests(con, writer, es.gIR_USERN, es.gEMAIL, sSort);
				}
				displayAccessRequests(con, writer, es.gIR_USERN, es.gEMAIL);
			}

			// capabilities...

			writer.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
			writer.println("<tr>");
			writer.println("<td headers=\"\"  colspan=\"2\" height=\"18\" class=\"tblue\">&nbsp;Learn more</td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			writer.println("</tr>");
			writer.println("<tr>");
			//writer.println("<td headers=\"\"  colspan=\"2\">Whether you're dealing with electromigration problems on ICs, building advanced manufacturing facilities, or supplying products to remote markets, we can help. Learn more about what we offer in: </td>");
			writer.println("<td headers=\"\"  colspan=\"2\">IBM Engineering & Technology Services applies the expertise of IBM engineers, leading-edge technology and intellectual property in collaborative relationships with clients. We can help you to innovate-to excel in the development, delivery and differentiation of your products. For more details...</td>");
			writer.println("</tr>");
			writer.println("<tr>");
			writer.println("<td headers=\"\"  colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			writer.println("</tr>");

			writer.println("<tr><td headers=\"\" >");

			getCapabilities(projBean, writer);

			writer.println("</td></tr></table>");

			writer.println("<br />");

			writer.println("</td>");

			// Gutter between main and right columns
			writer.println("<td headers=\"\"  width=\"7\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" alt=\"\" /></td>");

			// right column begins...

			writer.println("<td headers=\"\"  width=\"150\" valign=\"top\">");

			// designed to help you module...

			//			writer.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			//			writer.println("<tr>");
			//			writer.println("<td headers=\"\"  style=\"background-color:#cccccc;\" height=\"18\" width=\"150\"><b style=\"color: #ffffff\">&nbsp;Designed to help you</b></td>");
			//			writer.println("</tr>");
			//			writer.println("</table>");

			//			writer.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
			//			writer.println("<tr valign=\"middle\" >");
			//			writer.println("<td headers=\"\"  style=\"background-color: #ffffff;\" align=\"center\"><a href=\"" + getInfoURL(con, "ETS_LANDING_1", 1) + "\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_LANDING_1&mod=1\" alt=\"" + projBean.getImageAltText("ETS_LANDING_1", 1) + "\" width=\"150\" height=\"149\" border=\"0\" /></a></td>");
			//			writer.println("</tr>");
			//			writer.println("</table>");
			//			writer.println("<br />");

			writer.println(
				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"150\"><tr> <td class=\"v14-header-3\">Using E&TS Connect</td> </tr> <tr> <td> <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"v14-gray-table-border\"> <tr > <td colspan=\"3\"width=\"149\"><p><span class=\"small\">Read an overview of E&TS Connect's tools and learn more through self-study modules.</span></p></td></tr><tr><td width=\"16\" valign=\"top\"><img src=\""
					+ Defines.ICON_ROOT
					+ "fw.gif\" width=\"16\" height=\"16\" alt=\"Learn more\" border=\"0\" /></td><td width=\"61\" valign=\"top\">");
			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				writer.println("<a href=\"" + properties.getInternalHelpLink() + "\" class=\"fbox\">Learn more about our tools</a>");
			} else {
				writer.println("<a href=\"" + properties.getExternalHelpLink() + "\" class=\"fbox\">Learn more about our tools</a>");
			}
			writer.println("</td><td width=\"72\"><img src=\"" + Defines.SERVLET_PATH + "ETSImageServlet.wss?proj=ETS_LANDING_1&mod=1\" alt=\"" + projBean.getImageAltText("ETS_LANDING_1", 1) + "\" width=\"72\" height=\"67\" border=\"0\" /></td></tr> </table></td> </tr> </table>");
			writer.println("<br />");

			// news and ideas module ...

			if (bClientVoiceDisplayed) {
				displayKeyRating(writer);
				writer.println("<br />");
			}

			//writer.println("<br />");

			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				displayCreateWorkspace(con, writer, es);
				writer.println("<br />");
			}

			displayOptions(con, writer, es);
			writer.println("<br />");
			displayNewsAndIdeas(con, writer);

			// need more help ?
			ETSParams etsParams = new ETSParams();
			etsParams.setWriter(writer);
			ETSProjectHome.displaySiteHelp(etsParams);

			writer.println("</td>");

			writer.println("</tr>");
			writer.println("</table>");

			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");

			writer.println("<br /><br />");
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\""
					+ Defines.ICON_ROOT
					+ "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\"  align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");

			writer.println(EdgeHeader.printBullsEyeFooter());

			
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {
				logger.error(this, e);
			}
			e.printStackTrace();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this, e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(con);
			writer.flush();
			writer.close();
		}
	}

	/**
	 * @param con
	 * @param string
	 * @param b
	 */
	private static int setupDefaultCutomizeList(Connection con, String sUserId, boolean bAdmin) throws SQLException, Exception {

		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			if (bAdmin) {
				sQuery.append("INSERT INTO ETS.ETS_USER_WS (SELECT DISTINCT '" + sUserId + "', PROJECT_ID FROM ETS.ETS_PROJECTS WHERE PROJECT_OR_PROPOSAL NOT IN ('M') AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' AND PROJECT_STATUS NOT IN ('" + Defines.WORKSPACE_DELETE + "'))");
			} else {
				sQuery.append("INSERT INTO ETS.ETS_USER_WS (SELECT DISTINCT USER_ID,USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "')");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::setupDefaultCutomizeList::Query : " + sQuery.toString());
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

	/**
	 * @param writer
	 */
	private void displayKeyRating(PrintWriter out) {

		out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"150\" class=\"tblue\" height=\"18\">&nbsp;Satisfaction score key</td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<table summary=\"\" cellpadding=\"1\" cellspacing=\"0\" border=\"0\"  style=\"background-color: #ccc\">");
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

	private Vector getUserPrivs(int roleid) {
		Vector user_privs = new Vector();
		try {
			user_privs = ETSDatabaseManager.getUserPrivs(roleid);
		} catch (SQLException se) {
			if (logger.isErrorEnabled()) {
				logger.error(this, se);
			}
			user_privs = null;
		}
		return user_privs;
	}

	private static void getCapabilities(ETSProjectInfoBean projBean, PrintWriter out) throws Exception {

		StringBuffer sQuery = new StringBuffer("");

		try {

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			String[] sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 5);
			out.println("<td headers=\"\" width=\"202\"><a href=\"" + sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
			out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 6);
			out.println("<td headers=\"\" width=\"202\"><a href=\"" + sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
			out.println("<td headers=\"\" width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 7);
			out.println("<td headers=\"\" width=\"202\"><a href=\"" + sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");

			out.println("</tr>");
//			out.println("<tr>");
//			out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"16\" height=\"4\" alt=\"\" /></td>");
//			out.println("<td headers=\"\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"202\" height=\"4\" alt=\"\" /></td>");
//			out.println("</tr>");
//			out.println("<tr valign=\"top\">");
//			out.println("<td headers=\"\" width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
//			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 7);
//			out.println("<td headers=\"\" width=\"202\"><a href=\"" + sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
//			out.println("<td headers=\"\" width=\"7\">&nbsp;</td>");
//			out.println("<td headers=\"\" width=\"16\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
//			sTemp = projBean.getInfoDescAndLink("ETS_LANDING_1", 8);
//			out.println("<td headers=\"\" width=\"202\"><a href=\"" + sTemp[0] + "\" class=\"fbox\">" + sTemp[1] + "</a></td>");
//			out.println("</tr>");
//			out.println("<tr>");
//			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
//			out.println("</tr>");

			out.println("</table>");

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	private static void displayNoProjectContent(Connection con, PrintWriter out) throws SQLException, Exception {

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		out.println("<tr><td headers=\"\"  class=\"tdblue\">");

		out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
		out.println("<tr>");
		out.println("<td headers=\"\"  height=\"18\" class=\"tdblue\">&nbsp;About E&TS Connect</td>");
		out.println("</tr>");
		out.println("<tr><td headers=\"\"  width=\"443\">");

		out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
		out.println("<tr valign=\"middle\">");
		out.println("<td headers=\"\"  style=\"background-color: #eeeeee; color: #000000; font-weight: normal\" align=\"center\" >");
		out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"3\" border=\"0\" width=\"100%\">");

		ETSProperties prop = new ETSProperties();

		out.println(
			"<td headers=\"\"  align=\"left\" valign=\"top\">To help us work with our clients as on demand partners, E&TS Connect provides clients with 24/7 online access to their proposal and project information via a secure workspace and a comprehensive suite of on demand tools. <br /><br />To access these tools, clients need <b>proper user permission</b> in addition to an IBM ID.<br /><br />If you wish to work with IBM E&TS, please <a href=\""
				+ prop.getContactUsURL()
				+ "\" class=\"fbox\">contact us</a> for more information.<br /><br /></td>");

		//out.println("<td headers=\"\"  align=\"left\" valign=\"top\">To help us work with our clients as on demand partners, E&TS Connect provides clients with 24/7 online access to their proposal and project information via a secure workspace and a comprehensive suite of on demand tools. <br /><br />To access these tools, clients need <b>proper user permission</b> in addition to an IBM ID.<br /><br />If you wish to work with IBM E&TS, please <a href=\"" + getInfoURL(con, "ETS_GEN_CONTACT", 1) + "\" class=\"fbox\">contact us</a> for more information.<br /><br /></td>");

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
	 * Method getContactURL.
	 * @param con
	 * @return String
	 */
	private static String getInfoURL(Connection con, String sProjId, int iMod) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sContactURL = "";

		try {

			sQuery.append("SELECT INFO_LINK FROM ETS.ETS_PROJECT_INFO WHERE PROJECT_ID='" + sProjId + "' AND INFO_MODULE=" + iMod + " for READ ONLY");
			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getContactURL::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			if (rs.next()) {
				sContactURL = rs.getString(1);
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return sContactURL;

	}

	private static int getWorkspaceCount(Connection con, String sUserId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG='" + Defines.USER_ENTITLED + "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getMyProposalsCount::Query : " + sQuery.toString());
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

	private static void getMyProjects(Connection con, PrintWriter out, String sUserId, String sInternal, String sLinkId, boolean bAdmin, boolean bITAR) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		// for getting sub workspaces.
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;

		String pstmtQuery = "";

		int iCount = 0;

		try {

			if (bAdmin) {
				iCount = 2;
			} else {
				iCount = getProjectCount(con, sUserId);
			}

			if (bAdmin) {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? for READ ONLY";
			} else {
				pstmtQuery = "SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE PARENT_ID = ? AND PROJECT_STATUS != ? AND PROJECT_TYPE = ? AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = ?  AND ACTIVE_FLAG = ?) for READ ONLY";
			}

			// to get the primary contact..
			String strQuery = "SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS A, ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND B.USER_PROJECT_ID = ? AND B.PRIMARY_CONTACT = ? with ur";

			if (bAdmin) {
				sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='P' AND PARENT_ID='0' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '" + sUserId + "') AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' ORDER BY PROJECT_NAME for READ ONLY");
			} else {
				sQuery.append(
					"SELECT PROJECT_ID,PROJECT_NAME,COMPANY, IS_ITAR FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='P' AND PARENT_ID='0' AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '"
						+ sUserId
						+ "' AND ACTIVE_FLAG = '"
						+ Defines.USER_ENTITLED
						+ "') AND PROJECT_STATUS != '"
						+ Defines.WORKSPACE_DELETE
						+ "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '"
						+ sUserId
						+ "') AND PROJECT_TYPE = '"
						+ Defines.ETS_WORKSPACE_TYPE
						+ "' ORDER BY PROJECT_NAME for READ ONLY");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getMyProposals::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sProjID = rs.getString(1);
				String sProjName = rs.getString(2);
				String sCompany = rs.getString(3);
				String isITAR = ETSUtils.checkNull(rs.getString(4));

				// check for itar here.. 5.4.1 sathish
				if (isITAR.equalsIgnoreCase("Y")) {
					if (bITAR) {
						vDetails.addElement(new String[] { sProjID, sProjName, sCompany });
					}
				} else {
					vDetails.addElement(new String[] { sProjID, sProjName, sCompany });
				}
			}

			if (iCount > 0) {
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr><td headers=\"\"  class=\"tgreen\">");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;My E&TS projects</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"\"  width=\"443\">");
				out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"middle\">");
				out.println("<td headers=\"\"  style=\"background-color: #ffffff;\"  align=\"center\">");
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");

				out.println("<tr valign=\"top\" >");
				out.println("<td headers=\"\"  colspan=\"4\" align=\"right\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSCustomizeServlet.wss?type=P&flag=Y&linkid=" + sLinkId + "\">Customize this list</a></td>");
				out.println("</tr>");
			}
			
			
			if (vDetails != null && vDetails.size() > 0) {


				boolean bOddRow = true;

				// prepare statement to get the sub workspaces.
				pstmt = con.prepareStatement(pstmtQuery);

				// prepare statement to get the primary contact...
				pstmt1 = con.prepareStatement(strQuery);

				for (int i = 0; i < vDetails.size(); i++) {

					String sTemp[] = (String[]) vDetails.elementAt(i);

					String sProjID = sTemp[0];
					String sProjName = sTemp[1];
					String sCompany = sTemp[2];

					String sPrimaryContact = "";

					// check to see if this project has any subworkspaces defined...
					pstmt.setString(1, sProjID);
					pstmt.setString(2, Defines.WORKSPACE_DELETE);
					pstmt.setString(3, Defines.ETS_WORKSPACE_TYPE);
					if (!bAdmin) {
						pstmt.setString(4, sUserId);
						pstmt.setString(5, Defines.USER_ENTITLED);
					}

					ResultSet rset = null;

					rset = pstmt.executeQuery();

					pstmt1.setString(1, sProjID);
					pstmt1.setString(2, "Y");

					ResultSet rsPrimary = null;
					rsPrimary = pstmt1.executeQuery();

					if (rsPrimary.next()) {
						sPrimaryContact = rsPrimary.getString(1);
					}

					ETSDBUtils.close(rsPrimary);

					if (i == 0) {


						out.println("<tr valign=\"top\" >");
						out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Workspace name</b></td>");
						out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Client</b></td>");
						out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><b>Primary contact</b></td>");
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">");
						out.println("<!-- Gray dotted line -->");
						out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
						out.println("<tr>");
						out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
						out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("</table>");
						out.println("<!-- End Gray dotted line -->");
						out.println("</td>");
						out.println("</tr>");

					}

					if (bOddRow) {
						out.println("<tr style=\"background-color: #eeeeee\" >");
						bOddRow = false;
					} else {
						out.println("<tr >");
						bOddRow = true;
					}

					out.println("<td headers=\"\"  width=\"16\" align=\"left\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"" + sProjName + "\" border=\"0\" /></td>");
					out.println("<td headers=\"\"  width=\"150\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjID + "&linkid=" + sLinkId + "\" class=\"fbox\"><b>" + sProjName + "</b></a></td>");
					out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sCompany + "</td>");
					out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sPrimaryContact + "</td>");
					out.println("</tr>");

					// print the sub workspaces here if available...
					while (rset.next()) {

						String sSubWorkspaceProjectID = rset.getString(1); // cannot be null
						String sSubWorkspaceProjectName = rset.getString(2); // cannot be null
						String sSubWorkspaceCompany = rset.getString(3); // cannot be null
						String isITAR = ETSUtils.checkNull(rset.getString(4));
						String sPrimaryContact1 = "";

						boolean bDisplay = false;

						if (isITAR.equalsIgnoreCase("Y")) {
							if (bITAR) {
								bDisplay = true;
							}
						} else {
							bDisplay = true;
						}

						if (bDisplay) {

							pstmt1.clearParameters();
							pstmt1.setString(1, sSubWorkspaceProjectID);
							pstmt1.setString(2, "Y");

							ResultSet rsPrimary1 = pstmt1.executeQuery();

							if (rsPrimary1.next()) {
								sPrimaryContact1 = rsPrimary1.getString(1);
							}

							ETSDBUtils.close(rsPrimary1);

							if (bOddRow) {
								out.println("<tr style=\"background-color: #eeeeee\" >");
								bOddRow = false;
							} else {
								out.println("<tr >");
								bOddRow = true;
							}

							out.println("<td headers=\"\"  width=\"16\" valign=\"top\">&nbsp;</td>");

							out.println("<td headers=\"\" width=\"150\" align=\"left\" valign=\"top\">");
							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
							out.println("<tr valign=\"top\" >");
							out.println("<td headers=\"\"  width=\"10\" align=\"left\" valign=\"top\"><span style=\"color: #ccc;font-size:15\">&#183;</span></td>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sSubWorkspaceProjectID + "&linkid=" + sLinkId + "\" class=\"fbox\"><b>" + sSubWorkspaceProjectName + "</b></a></td>");
							out.println("</tr>");
							out.println("</table>");
							out.println("</td>");

							out.println("<td headers=\"\"  width=\"80\" align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sSubWorkspaceCompany + "</td>");
							out.println("<td headers=\"\"  align=\"left\" valign=\"top\" style=\"color:#000000; font-weight: normal\">" + sPrimaryContact1 + "</td>");

							out.println("</tr>");
						}
					}

					ETSDBUtils.close(rset);

				}

				ETSProperties properties = new ETSProperties();

				if (properties.displayHelp() == true) {
					out.println("<tr><td headers=\"\" colspan=\"4\" align=\"right\" bgcolor=\"#eeeeee\">");
					out.println("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpading=\"0\"><tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					out.println(
						"<td headers=\"\" width=\"30\" align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project\" target=\"new\" onclick=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=project','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Help</a></td>");
					out.println("</tr></table>");
					out.println("</td></tr>");
				}


			}
			
			if (iCount > 0) {
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
	private static void displayAccessRequests(Connection con, PrintWriter out, String sUserId, String sUserEmail) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		try {

			sQuery.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID, requested_by FROM ETS.ETS_ACCESS_REQ WHERE user_id = '" + sUserId + "' AND STATUS in ('" + Defines.ACCESS_PENDING + "','" + Defines.ACCESS_APPROVED + "') and lcase(mgr_email) != '" + sUserEmail.toLowerCase() + "' and PROJECT_TYPE='ETS' union ");
			sQuery.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID, requested_by FROM ETS.ETS_ACCESS_REQ WHERE requested_by = '" + sUserId + "' AND STATUS in ('" + Defines.ACCESS_PENDING + "','" + Defines.ACCESS_APPROVED + "') and lcase(mgr_email) != '" + sUserEmail.toLowerCase() + "' and PROJECT_TYPE='ETS' order by date_requested desc for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::displayAccessRequests::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sUserFor = rs.getString(1);
				String sProjName = rs.getString(2);

				String sTempDate = rs.getTimestamp("DATE_REQUESTED").toString();
				String sDateRequested = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
				String sRequestId = rs.getString(4);
				String submittedBy = rs.getString(5);
				vDetails.addElement(new String[] { sUserFor, sProjName, sDateRequested, sRequestId, submittedBy });

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

					ETSProcReqAccessFunctions procFunc = new ETSProcReqAccessFunctions();
					String status = procFunc.getStatus(sRequestId, con);
					if (!status.equals("hasEntitlement")) {
						ETSUserDetails userDtls = new ETSUserDetails();
						userDtls.setWebId(sUserFor);
						userDtls.extractUserDetails(con);
						if (!(userDtls.getUserType() == userDtls.USER_TYPE_INTERNAL || userDtls.getUserType() == userDtls.USER_TYPE_EXTERNAL)) {
							status = "Incomplete user (profile) information ";
						}

						if (!headerPrinted) {
							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
							out.println("<tr><td headers=\"\"  class=\"tgreen\">");

							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
							out.println("<tr>");
							out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Status of requests submitted by you or for you</td>");
							out.println("</tr>");
							out.println("<tr><td headers=\"\"  width=\"443\">");
							out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
							out.println("<tr valign=\"middle\">");
							out.println("<td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"center\">");

							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

							out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"5\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td></tr>");
							out.println("<tr>");
							out.println("<th id=\"req_for\" width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>User<br />[IBM ID]</b></td>");
							out.println("<th id=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\"><b>Workspace<br /> Requested&#42;</b></td>");
							out.println("<th id=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Date<br />requested</b></td>");
							out.println("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b>Requested<br />by</b></td>");
							out.println("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b><br />Status</b></td>");
							out.println("</tr>");
							headerPrinted = true;
						}

						out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"5\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
						out.println("<tr style=\"background-color:" + (bgColor = (bgColor.equals("#eeeeee") ? "#ffffff" : "#eeeeee")) + "\" valign=\"top\">");
						out.println("<td headers=\"\" headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + ETSUtils.getUsersName(con, sUserFor) + "<br />[" + sUserFor + "]</td>");
						out.println("<td headers=\"\" headers=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\">" + sProjName + "</td>");
						out.println("<td headers=\"\" headers=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + sDateRequested + "</td>");
						out.println("<td headers=\"\" headers=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + ETSUtils.getUsersName(con, subBy) + "</td>");
						out.println("<td headers=\"\" headers=\"status\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + status + "</td>");
						out.println("</tr>");

					}
				}
				if (headerPrinted) {

					out.println("<tr valign=\"top\"><td headers=\"\" colspan=\"5\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"6\" alt=\"\" /></td></tr>");
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
	/**
	 * This method displays pending requests for a workspace owner
	 * if there is any to display.
	 * @param con
	 * @param out
	 * @param sUserId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayPendingRequests(Connection con, PrintWriter out, String sUserId, String sUserEmail, String sSortOrder) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);

			if (sSortOrder == null || sSortOrder.trim().equalsIgnoreCase("")) {
				sSortOrder = "date_requested desc ";
			} else {
				sSortOrder = sSortOrder.trim();
			}

			sQuery.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE lcase(MGR_EMAIL) = '" + sUserEmail.toLowerCase() + "' AND STATUS in ('" + Defines.ACCESS_PENDING + "','" + Defines.ACCESS_APPROVED + "') and PROJECT_TYPE='ETS' order by " + sSortOrder + " for READ ONLY");
			//sQuery.append("SELECT USER_ID,PROJECT_NAME,DATE_REQUESTED,REQUEST_ID FROM ETS.ETS_ACCESS_REQ WHERE MGR_EMAIL = '" + sUserEmail + "' AND STATUS in ('" + Defines.ACCESS_PENDING + "') order by " + sSortOrder + " for READ ONLY"); //CHG5.4

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::displayPendingRequests::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sUserFor = rs.getString(1);
				String sProjName = rs.getString(2);

				String sTempDate = rs.getTimestamp("DATE_REQUESTED").toString();
				String sDateRequested = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
				String sRequestId = rs.getString(4);
				vDetails.addElement(new String[] { sUserFor, sProjName, sDateRequested, sRequestId });

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

					ETSProcReqAccessFunctions procFunc = new ETSProcReqAccessFunctions();
					String status = procFunc.getStatus(sRequestId, con);
					if (!status.equals("hasEntitlement")) {

						ETSUserDetails userDtls = new ETSUserDetails();
						userDtls.setWebId(sUserFor);
						userDtls.extractUserDetails(con);

						if (!(userDtls.getUserType() == userDtls.USER_TYPE_INTERNAL || userDtls.getUserType() == userDtls.USER_TYPE_EXTERNAL)) {
							status = "Incomplete user (profile) information ";
						}

						if (!headerPrinted) {

							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
							out.println("<tr><td headers=\"\"  class=\"tgreen\">");

							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
							out.println("<tr>");
							out.println("<td headers=\"\"  height=\"18\" class=\"tblue\"><a name=\"pending_list\">&nbsp;Pending access requests</a></td>");
							out.println("</tr>");
							out.println("<tr><td headers=\"\"  width=\"443\">");
							out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
							out.println("<tr valign=\"middle\">");
							out.println("<td headers=\"\"  style=\"background-color: #ffffff; color: #000000; font-weight: normal;\" align=\"center\">");
							out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"2\" alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"  align=\"left\">" + sMsg + "</td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr valign=\"top\">");
							out.println("<td headers=\"\" colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"6\" alt=\"\" /></td>");
							out.println("</tr>");

							out.println("<tr>");
							out.println("<th id=\"req_for\" width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "&sort=user_id\"><b>User<br />[IBM ID]</b></a></td>");
							out.println("<th id=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "&sort=project_name\"><b>Workspace<br /> Requested&#42;</b></a></td>");
							out.println("<th id=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + unBrandprop.getLinkID() + "&sort=date_requested\"><b>Date<br />requested</b></a></td>");
							out.println("<th id=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><b><br />Status</b></td>");
							out.println("</tr>");
							headerPrinted = true;

						}

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td>");
						out.println("</tr>");
						out.println("<tr style=\"background-color:" + (bgColor = (bgColor.equals("#eeeeee") ? "#ffffff" : "#eeeeee")) + "\" valign=\"top\">");
						if (status.equals("New Request") || status.startsWith("Forwarded by")) {
							out.println("<td headers=\"\" headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\"><a class=\"fbox\" href=\"" + Defines.SERVLET_PATH + "ETSProcReqAccessServlet.wss?linkid=251000&option=showreq&requestid=" + sRequestId + "&ibmid=" + sUserFor + "\">" + ETSUtils.getUsersName(con, sUserFor) + "<br />[" + sUserFor + "]</a></td>");
						} else {
							out.println("<td headers=\"\" headers=\"req_for\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + ETSUtils.getUsersName(con, sUserFor) + "<br />[" + sUserFor + "]</td>");
						}

						out.println("<td headers=\"\" headers=\"req_proj\" width=\"143\" align=\"left\" valign=\"top\"  class=\"small\">" + sProjName + "</td>");
						out.println("<td headers=\"\" headers=\"req_date\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + sDateRequested + "</td>");
						out.println("<td headers=\"\" headers=\"req_by\" width=\"100\" align=\"left\" valign=\"top\"  class=\"small\">" + status + "</td>");
						out.println("</tr>");

					}
				}

				if (headerPrinted) {

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"6\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\">&#42;Workspace Requested as entered on the request access form</td>");
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

	private static boolean getVoiceOfClientProjects(Connection con, PrintWriter out, String sUserId, String sInternal, String sLinkId, boolean bAdmin, boolean bExecutive) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();
		boolean bHasClientVoice = false;

		int iCount = 0;

		try {

			if (bAdmin || bExecutive) {
				iCount = 2;
			} else {
				iCount = getClientVoiceCount(con, sUserId);
			}

			// added the customize part in 5.2.1
			if (bAdmin || bExecutive) {
				sQuery.append("SELECT PROJECT_ID,PROJECT_NAME,LOTUS_PROJECT_ID,COMPANY FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='C' AND PARENT_ID='0' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '" + sUserId + "') ORDER BY PROJECT_NAME for READ ONLY");
			} else {
				sQuery.append(
					"SELECT PROJECT_ID,PROJECT_NAME,LOTUS_PROJECT_ID,COMPANY FROM ETS.ETS_PROJECTS WHERE UCASE(PROJECT_OR_PROPOSAL)='C' AND PARENT_ID='0' AND PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '"
						+ sUserId
						+ "' AND ACTIVE_FLAG = '"
						+ Defines.USER_ENTITLED
						+ "') AND PROJECT_STATUS != '"
						+ Defines.WORKSPACE_DELETE
						+ "' AND PROJECT_ID IN (SELECT PROJECT_ID FROM ETS.ETS_USER_WS WHERE USER_ID = '"
						+ sUserId
						+ "') ORDER BY PROJECT_NAME for READ ONLY");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getVoiceOfClientProjects::Query : " + sQuery.toString());
			}

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			Vector vDetails = new Vector();

			while (rs.next()) {

				String sProjID = rs.getString(1);
				String sProjName = rs.getString(2);
				String sLotusID = rs.getString(3);
				if (sLotusID == null || sLotusID.trim().equals("")) {
					sLotusID = "";
				} else {
					sLotusID = sLotusID.trim();
				}
				String sCompany = rs.getString(4);

				vDetails.addElement(new String[] { sProjID, sProjName, sLotusID, sCompany });

			}

			if (iCount > 0) {
				
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr><td headers=\"\"  class=\"tgreen\">");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Client Voice</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"\"  width=\"443\">");

				out.println("<table summary=\"\" cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				out.println("<tr valign=\"middle\">");
				out.println("<td headers=\"\"  style=\"background-color: #ffffff;color: #000000;\" align=\"center\" >");
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");
				
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"4\" align=\"right\" style=\"background-color: #ffffff; font-weight: normal;\"><a href=\"" + Defines.SERVLET_PATH + "ETSCustomizeServlet.wss?type=C&flag=Y&linkid=" + sLinkId + "\">Customize this list</a></td>");
				out.println("</tr>");

			}
			
			if (vDetails != null && vDetails.size() > 0) {

				bHasClientVoice = true;

				// there is atleast one project available to be displayed.


				if (sInternal.trim().equalsIgnoreCase("I")) {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">Use this tool to monitor the health of your client relationships, as well as Set/Met interviews, satisfaction indices and loyalty levels.</td>");
					out.println("</tr>");
				} else {
					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">IBM's Set/Met process is your opportunity to provide us with the feedback we need to ensure your highest level of satisfaction.</td>");
					out.println("</tr>");
				}

				if (bAdmin || bExecutive) {

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #ffffff; font-weight: normal;\">");
					printGreyDottedLine(out);
					out.println("</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\"><b><a href=\"" + Defines.SERVLET_PATH + "ETSMetricsServlet.wss?project_id=metrics&option=list&linkid=255000\">Executive reports</a>: Client satisfaction and portfolio level reports</b></td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #ffffff; font-weight: normal;\">");
					printGreyDottedLine(out);
					out.println("</td>");
					out.println("</tr>");

					if (bAdmin) {
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">You can use the following links to upload survey master data which includes survey questions, survey reference data and survey mapping file, as well as the the file that includes the responses for all companies.</td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\"><table summary=\"\"><tr><td width=\"16\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></td><td align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyServlet.wss?action=addSurveyQuestion\" class=\"fbox\"><b>Upload survey master data</b></a></td></tr></table></td>");
						out.println("</tr>");
						
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\"><table summary=\"\"><tr><td width=\"16\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Post a message\" border=\"0\" /></td><td align=\"left\"><b><a href=\"" + Defines.SERVLET_PATH + "ETSSurveyServlet.wss?action=addSurveyData\" class=\"fbox\"><b>Upload survey results file</b></a></td></tr></table></td>");
						out.println("</tr>");
	
						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #ffffff; font-weight: normal;\">");
						printGreyDottedLine(out);
						out.println("</td>");
						out.println("</tr>");
					}

				}

				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"4\" align=\"left\" style=\"background-color: #ffffff; font-weight: normal;\">&nbsp;</td>");
				out.println("</tr>");

				for (int i = 0; i < vDetails.size(); i++) {

					if (i == 0) {

						out.println("<tr style=\"background-color:#eeeeee\">");
						if (sInternal.trim().equalsIgnoreCase("I")) {
							out.println("<td headers=\"\"  width=\"203\" align=\"left\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\">&nbsp;</td>");
							out.println("<td headers=\"\"  colspan=\"3\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b>Overall satisfaction</b></td>");
						} else {
							out.println("<td headers=\"\"  width=\"283\" align=\"left\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\">&nbsp;</td>");
							out.println("<td headers=\"\"  colspan=\"2\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b>Overall satisfaction</b></td>");
						}
						out.println("</tr>");
						out.println("<tr valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\">");
						if (sInternal.trim().equalsIgnoreCase("I")) {
							out.println("<td headers=\"\"  width=\"203\" align=\"left\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Client care accounts</span></b></td>");
							out.println("<td headers=\"\"  width=\"80\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Set/Met</span></b></td>");
							out.println("<td headers=\"\"  width=\"80\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Corporate survey</span></b></td>");
							out.println("<td headers=\"\"  width=\"80\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Self assessment</span></b></td>");
						} else {
							out.println("<td headers=\"\"  colspan=\"2\" align=\"left\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Client care accounts</span></b></td>");
							out.println("<td headers=\"\"  width=\"80\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Set/Met</span></b></td>");
							out.println("<td headers=\"\"  width=\"80\" align=\"middle\" valign=\"top\" style=\"background-color: #ffffff; font-weight: normal;\"><b><span class=\"small\">Corporate survey</span></b></td>");
						}
						out.println("</tr>");

						out.println("<tr valign=\"top\">");
						out.println("<td headers=\"\" colspan=\"4\" style=\"background-color: #ffffff; font-weight: normal;\">");
						printGreyDottedLine(out);
						out.println("</td>");
						out.println("</tr>");
					}

					String sTemp[] = (String[]) vDetails.elementAt(i);

					String sProjID = sTemp[0];
					String sProjName = sTemp[1];
					String sLotusID = sTemp[2];
					String sCompany = sTemp[3];

					if (!sInternal.trim().equalsIgnoreCase("I")) {
						// the logged in person is not internal.. treat him as client...
						out.println("<tr style=\"background-color:#ffff99\">");
					} else {
						if ((i % 2) == 0) {
							out.println("<tr valign=\"top\">");
						} else {
							out.println("<tr style=\"background-color:#eeeeee\">");
						}
					}

					ETSSetMet setmet = ETSSetMetDAO.getSetMet(con, sProjID, "");
					Vector vExp = ETSSetMetDAO.getSetMetExpectations(con, sProjID, setmet.getSetMetID());
					
					boolean bDisplayed = false;
					String sSetMetRating = "-";

					if (vExp != null) {

						for (int j = 0; j < vExp.size(); j++) {
							ETSSetMetExpectation exp = (ETSSetMetExpectation) vExp.elementAt(j);
							// hardcoding 4 here because 4 is always going to be overall satisfaction provided by E&TS connect.
							if (exp.getQuestionID() == 4) {
								bDisplayed = true;
								if (setmet.getState().equalsIgnoreCase(Defines.SETMET_OPEN)) {
									sSetMetRating = String.valueOf(exp.getExpectRating());
								} else {
									sSetMetRating = String.valueOf(exp.getFinalRating());
								}

								break;
							}
						}
					}

					if (!bDisplayed) {
						sSetMetRating = "-";
					}
					
					
					
					// get the self assessment rating
					
					String sSelfRating = "-";
					
					if (sInternal.trim().equalsIgnoreCase("I")) {
						
						ETSSelfAssessment selfAssessment = ETSSelfDAO.getLatestSelfAssessment(con,sProjID);
						
						if (selfAssessment != null) {
							ArrayList expect = selfAssessment.getExpectations();
		
							float iValue = 0;
							int iSelfCount = 0;
		
							if (expect != null) {
								for (int j =0; j < expect.size(); j++) {
									ETSSelfAssessmentExpectation exp = (ETSSelfAssessmentExpectation) expect.get(j);
									
									if (exp.getSectionId() == ETSSelfConstants.SECTION_OVERALL_RATING) {
										iValue = iValue + exp.getRating();
										iSelfCount = iSelfCount + 1;
									}
								}
							}				
								
							if (iSelfCount > 0) {
								float value = iValue / iSelfCount; 
								NumberFormat format = new DecimalFormat("0.0");
								sSelfRating = format.format(value);
							}
						}
					}
															
					// get the survey rating and average it out if more then one survey present...
					
					String sSurveyRating = "-";
					float fsurveyValue = 0;
					int iSurveyCount = 0; 
					Vector vSurveys = ETSSurveyDAO.getLatestSurveysForCompany(con,sCompany);
					
					if (vSurveys != null && vSurveys.size() > 0) {
						for (int j = 0; j < vSurveys.size(); j++) {
							ETSSurvey survey = (ETSSurvey) vSurveys.elementAt(j);
							fsurveyValue = fsurveyValue + Float.parseFloat(survey.getOverallSatisfaction());
							iSurveyCount = iSurveyCount + 1;
						}						
					}
					
					if (iSurveyCount > 0) {
						float fsurveyRating = fsurveyValue / iSurveyCount;
						NumberFormat format = new DecimalFormat("0.0");
						sSurveyRating = format.format(fsurveyRating);						
					}

					if (sInternal.trim().equalsIgnoreCase("I")) {
						out.println("<td headers=\"\"  height=\"16\" width=\"203\" align=\"left\" valign=\"middle\" style=\"font-weight: normal;\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjID + "&linkid=" + sLinkId + "&set=" + setmet.getSetMetID() + "\" class=\"fbox\"><b>" + sCompany + ":&nbsp;" + sProjName + "</b></a></td>");
						out.println("<td headers=\"\"  height=\"16\" width=\"80\" align=\"middle\" valign=\"middle\" style=\"font-weight: normal;\" ><b>" + sSetMetRating + "</b></a></td>");
						out.println("<td headers=\"\"  height=\"16\" width=\"80\" align=\"middle\" valign=\"middle\" style=\"font-weight: normal;\" ><b>" + sSurveyRating + "</b></a></td>");
						out.println("<td headers=\"\"  height=\"16\" width=\"80\" align=\"middle\" valign=\"middle\" style=\"font-weight: normal;\" ><b>" + sSelfRating + "</b></a></td>");
					} else {
						out.println("<td headers=\"\"  height=\"16\" colspan=\"2\" align=\"left\" valign=\"middle\" style=\"font-weight: normal;\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + sProjID + "&linkid=" + sLinkId + "&set=" + setmet.getSetMetID() + "\" class=\"fbox\"><b>" + sCompany + ":&nbsp;" + sProjName + "</b></a></td>");
						out.println("<td headers=\"\"  height=\"16\" width=\"80\" align=\"middle\" valign=\"middle\" style=\"font-weight: normal;\" ><b>" + sSetMetRating + "</b></a></td>");
						out.println("<td headers=\"\"  height=\"16\" width=\"80\" align=\"middle\" valign=\"middle\" style=\"font-weight: normal;\" ><b>" +  sSurveyRating + "</b></a></td>");
					}
					out.println("</tr>");
				}

				ETSProperties properties = new ETSProperties();

				if (properties.displayHelp() == true) {

					out.println("<tr><td headers=\"\" colspan=\"4\" align=\"right\" bgcolor=\"#eeeeee\">");
					out.println("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpading=\"0\"><tr valign=\"top\">");
					out.println("<td headers=\"\" width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
					out.println(
						"<td headers=\"\" width=\"30\" align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=setmet\" target=\"new\" onclick=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=setmet','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?proj=setmet','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">Help</a></td>");
					out.println("</tr></table>");
					out.println("</td></tr>");

				}

			}
			
			if (iCount > 0) {
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
		}

		return bHasClientVoice;
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
	private static void displayOptions(Connection con, PrintWriter out, EdgeAccessCntrl es) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		Hashtable hSSM = new Hashtable();

		try {

			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" class=\"tblue\" height=\"18\">&nbsp;New Requests</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			ETSUserDetails uDetails = new ETSUserDetails();
			uDetails.setWebId(es.gIR_USERN);
			uDetails.extractUserDetails(con);

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSUserAccessServlet.wss?linkid=251000&from=req\" class=\"fbox\">Request Access to Another Workspace</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			//                String isValid = EntitledStatic.getValue(con, "select count(*) from amt.s_user_access_view where userid = '"+uDetails.getEdgeId()+"' and entitlement='"+Defines.ETS_ENTITLEMENT+"' with ur");
			//                if (uDetails.getUserType()==uDetails.USER_TYPE_INTERNAL&&(Integer.parseInt(isValid)>0)){
			//                    out.println("<tr>");
			//                    out.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" /></td>");
			//                    out.println("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\""+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?linkid=251000&div=1\" class=\"fbox\">Request to Create E&TS Connect for a new project</a></td>");
			//                    out.println("</tr>");
			//
			//                    // divider
			//                    out.println("<tr valign=\"top\">");
			//                    out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			//                    out.println("</tr>");
			//                    out.println("<tr valign=\"top\">");
			//                    out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			//                    out.println("</tr>");
			//                    out.println("<tr valign=\"top\">");
			//                    out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			//                    out.println("</tr>");
			//                }
			out.println("</table>");
			out.println("<br />");

		} catch (Exception e) {
			throw e;
		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

	}

	private static void displayCreateWorkspace(Connection con, PrintWriter out, EdgeAccessCntrl es) throws SQLException, Exception {

		try {
			
			UnbrandedProperties unBrandprop = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);

			out.println("<table summary=\"\" width=\"150\" cellpadding=\"0\" cellspacing=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" class=\"tblue\" height=\"18\">&nbsp;Create a workspace</td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\" valign=\"top\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"Create a workspace\" /></td>");
			out.println("<td headers=\"\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSUserAccessServlet.wss?linkid=" + unBrandprop.getLinkID() + "&from=create\" class=\"fbox\">Create a workspace</a></td>");
			out.println("</tr>");

			// divider

			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr valign=\"top\">");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");

		} catch (Exception e) {
			throw e;
		}

	}

	private static int getProjectCount(Connection con, String sUserId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_OR_PROPOSAL = 'P' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getProjectCount::Query : " + sQuery.toString());
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

	private static int getProposalCount(Connection con, String sUserId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_OR_PROPOSAL = 'O' AND PROJECT_TYPE = '" + Defines.ETS_WORKSPACE_TYPE + "' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getProjectCount::Query : " + sQuery.toString());
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

	private static int getClientVoiceCount(Connection con, String sUserId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_OR_PROPOSAL = 'C' AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getProjectCount::Query : " + sQuery.toString());
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

	private static int getCustomizeCount(Connection con, String sUserId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		int iCount = 0;

		try {

			sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_USER_WS WHERE USER_ID = '" + sUserId + "' for READ ONLY");

			if (logger.isDebugEnabled()) {
				logger.debug("ETSConnectServlet::getCustomizeCount::Query : " + sQuery.toString());
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

}
