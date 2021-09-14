/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.decaf.DecafSuperSoldTos;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;
import oem.edge.ets.fe.aic.AICUserRequestDAO;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.workflow.util.DBHandler;


/**
 * @author v2sathis
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICUserRequestFunctions {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";

	private static final String CLASS_VERSION = "1.4";

	private static final String BUNDLE_NAME = "oem.edge.ets.fe.aic.AICResources";

	private static final ResourceBundle aic_rb = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private static final String APPLICATION_NAME = aic_rb
			.getString("aic.AICApplicationName");

	private static final String LANDING_PAGE = aic_rb
			.getString("aic.AICLandingPage");

	private static final String sCreateWSServlet = aic_rb
			.getString("aic.createWorkspaceServlet");

	private ETSParams params;

	public AICUserRequestFunctions(ETSParams parameters) {
		this.params = parameters;
	}

	public void showClientScreen(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();

		try {

			DecafSuperSoldTos companies = new DecafSuperSoldTos();
			Vector vCompany = companies.getSupSoldToList(con);

			String sComp = ETSUtils.checkNull(request
					.getParameter("client_company"));

			out.println("<br /><br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col1\" align=\"left\"><span style=\"color:#ff3333\">"
								+ sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col2\" align=\"left\">Please select the client (Company) for which you would like to request access and click on Continue.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			//printGreyDottedLine(out);

			//out.println("<br /><br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_company\">Client company:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"client_company\" id=\"w_company\" class=\"iform\" width=\"200px\" style=\"width:200px\">");

			out
					.println("<option value=\"\" selected=\"selected\">Select Company</option>");
			for (int i = 0; i < vCompany.size(); i++) {
				String sTemp = (String) vCompany.elementAt(i);
				if (sComp.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}

			}

			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col5\" align=\"left\">If you do not find your client's company then use the link below to add it to the system. </td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col6\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"col7\" align=\"left\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col8\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Request to add company\" border=\"0\" /></td><td headers=\"col9\"  align=\"left\" width=\"180\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSNewCompanyServlet.wss?linkid="
							+ params.getLinkId()
							+ "&appType=AIC\" >Request to add new company</a></td><td headers=\"col10\"  width=\"2\" valign=\"middle\" align=\"left\">[ </td><td headers=\"col11\"  width=\"20\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.ICON_ROOT
							+ "popup.gif\" width=\"16\" height=\"16\" alt=\"Help\" border=\"0\" /></td><td headers=\"col12\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?field_name=create_company&proj_type="
							+ Defines.AIC_WORKSPACE_TYPE
							+ "\" target=\"new\" class=\"fbox\" onclick=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?field_name=create_company&proj_type="
							+ Defines.AIC_WORKSPACE_TYPE
							+ "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('"
							+ Defines.SERVLET_PATH
							+ "ETSHelpServlet.wss?field_name=create_company&proj_type="
							+ Defines.AIC_WORKSPACE_TYPE
							+ "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Help</a></td><td headers=\"col13\"  width=\"20\" valign=\"middle\" align=\"left\"> ]</td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out
					.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out
					.println("<td headers=\"col14\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\""
							+ Defines.BUTTON_ROOT
							+ "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
			out
					.println("<td headers=\"col15\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col16\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col17\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICConnectServlet.wss?linkid="
							+ params.getLinkId()
							+ "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");

			out
					.println("<input type=\"hidden\" name=\"act\" value=\"clientcontinue\" />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	public void showUserEventsFilter() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();

		try {
			Vector vCompany = DBHandler
					.getValues(
							con,
							"select distinct company from ets.ets_projects a, ets.wf_def b where a.project_id = b.project_id with ur ");
			Vector vWfType = DBHandler.getValues(con,
					"select distinct wf_type from ets.wf_def with ur");
			Vector vBrand = DBHandler
					.getValues(
							con,
							"select distinct BRAND from ets.ets_projects a, ets.wf_def b where a.project_id = b.project_id with ur ");
			Vector vSCESector = DBHandler
					.getValues(
							con,
							"select distinct SCE_SECTOR from ets.ets_projects a, ets.wf_def b where a.project_id = b.project_id with ur ");
			Vector vBusSector = DBHandler
					.getValues(
							con,
							"select distinct SECTOR from ets.ets_projects a, ets.wf_def b where a.project_id = b.project_id with ur ");

			String sComp = ETSUtils.checkNull(request
					.getParameter("client_company"));
			if (sComp.equals(""))
				sComp = "ALL";
			String sWfType = ETSUtils
					.checkNull(request.getParameter("wf_type"));
			if (sWfType.equals(""))
				sWfType = "ALL";
			String sBrand = ETSUtils.checkNull(request.getParameter("brand"));
			if (sBrand.equals(""))
				sBrand = "ALL";
			String sSce = ETSUtils
					.checkNull(request.getParameter("sce_sector"));
			if (sSce.equals(""))
				sSce = "ALL";
			String sBus = ETSUtils
					.checkNull(request.getParameter("bus_sector"));
			if (sBus.equals(""))
				sBus = "ALL";

			out
					.println("<table  cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col2\" align=\"left\">Please select the filter for which you would like to see events and click on Submit.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			//printGreyDottedLine(out);

			out
					.println("<table summary=\"EVENTS_FILTER\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");

			//Company List
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_company\">Client company:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"client_company\" id=\"w_company\" style=\"width:200px\">");

			out
					.println("<option value=\"ALL\" selected=\"selected\">-- ALL --</option>");
			for (int i = 0; i < vCompany.size(); i++) {
				String sTemp = (String) vCompany.elementAt(i);
				if (sComp.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}
			}
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td></tr>");

			//WF_TYPE
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_wf_type\">WF_TYPE:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"wf_type\" id=\"w_wf_type\" style=\"width:100px\">");

			out
					.println("<option value=\"ALL\" selected=\"selected\">-- ALL --</option>");
			for (int i = 0; i < vWfType.size(); i++) {
				String sTemp = (String) vWfType.elementAt(i);
				if (sWfType.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}
			}
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td></tr>");

			//Brand
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_brand\">Brand:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"brand\" id=\"w_brand\" style=\"width:100px\">");

			out
					.println("<option value=\"ALL\" selected=\"selected\">-- ALL --</option>");
			for (int i = 0; i < vBrand.size(); i++) {
				String sTemp = (String) vBrand.elementAt(i);
				if (sBrand.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}
			}
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td></tr>");

			//SCE SECTOR
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_sce\">SCE Sector:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"sce_sector\" id=\"w_sce\" style=\"width:100px\">");

			out
					.println("<option value=\"ALL\" selected=\"selected\">-- ALL --</option>");
			for (int i = 0; i < vSCESector.size(); i++) {
				String sTemp = (String) vSCESector.elementAt(i);
				if (sSce.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}
			}
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr><td>&nbsp;</td></tr>");

			//SECTOR
			out.println("<tr>");
			out
					.println("<td headers=\"col3\" width=\"150\" align=\"left\"><b><label for=\"w_bus\">Business Sector:</label></b></td>");
			out
					.println("<td headers=\"col4\" align=\"left\"><select  class=\"iform\" name=\"bus_sector\" id=\"w_bus\" style=\"width:100px\">");

			out
					.println("<option value=\"ALL\" selected=\"selected\">-- ALL --</option>");
			for (int i = 0; i < vBusSector.size(); i++) {
				String sTemp = (String) vBusSector.elementAt(i);
				if (sBus.equalsIgnoreCase(sTemp)) {
					out
							.println("<option value=\"" + sTemp
									+ "\" selected=\"selected\">" + sTemp
									+ "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp
							+ "</option>");
				}
			}
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("<br /><br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out
					.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out
					.println("<td headers=\"col14\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\""
							+ Defines.BUTTON_ROOT
							+ "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" /></td>");
			out
					.println("<td headers=\"col15\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col16\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col17\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICConnectServlet.wss?linkid="
							+ params.getLinkId()
							+ "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");

			//out.println("<input type=\"hidden\" name=\"act\"
			// value=\"client_events\" />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	public void showWorkspacesScreen(String sError) throws SQLException,
			Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		try {

			// to get the workspace owner
			String strQuery = "SELECT LTRIM(RTRIM(USER_FNAME)) || ' ' || LTRIM(RTRIM(USER_LNAME)) FROM AMT.USERS A, ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND B.USER_PROJECT_ID = ? AND B.USER_ROLE_ID IN (SELECT ROLE_ID FROM ETS.ETS_ROLES WHERE PROJECT_ID = ? AND PRIV_ID = ?) with ur";

			// to get the pending request
			String querypending = "SELECT COUNT(PROJECT_NAME) FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = ? AND STATUS = ? with ur";

			// to check if memeber off...
			String querymember = "SELECT COUNT(USER_ID) FROM ETS.ETS_USERS WHERE USER_ID = ? AND USER_PROJECT_ID = ? AND ACTIVE_FLAG = ? with ur";

			String sCompany = ETSUtils.checkNull(request
					.getParameter("client_company"));

			out.println("<br /><br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col18\" align=\"left\"><span style=\"color:#ff3333\">"
								+ sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}
			String sProjectId = ETSUtils.checkNull(request
					.getParameter("project_id"));

			Vector vProjects = AICUserRequestDAO.getActiveWorkspacesForCompany(
					con, sCompany,es.gIR_USERN);

			if (vProjects != null && vProjects.size() > 0) {

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col19\" align=\"left\">The following workspaces are available for client company: <b>"
								+ sCompany + "</b></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out
						.println("<input type=\"hidden\" name=\"client_company\" value=\""
								+ sCompany + "\" /> ");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col20\" align=\"left\">Workspaces marked with <span style=\"color:#cc6600\">*</span> are the workspaces for which you already have access or an access request is pending with the workspace owner. Please select the workspace you require access to and click on Submit.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col21\" align=\"left\">If you do not find the workspace you are interested in below and would like to create a new workspace for this client company, please click the <b>Create new workspace</b> link at the bottom of the page. </td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br /><br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col22\" colspan=\"2\" width=\"266\" align=\"left\"><b>Name of workspace</b></td>");
				out
						.println("<td headers=\"col23\" width=\"100\" align=\"left\"><a href=\""
								+ Defines.SERVLET_PATH
								+ "ETSHelpServlet.wss?field_name=projtype&proj_type="
								+ Defines.AIC_WORKSPACE_TYPE
								+ "\" target=\"new\" class=\"fbox\" onclick=\"window.open('"
								+ Defines.SERVLET_PATH
								+ "ETSHelpServlet.wss?field_name=projtype&proj_type="
								+ Defines.AIC_WORKSPACE_TYPE
								+ "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('"
								+ Defines.SERVLET_PATH
								+ "ETSHelpServlet.wss?field_name=projtype&proj_type="
								+ Defines.AIC_WORKSPACE_TYPE
								+ "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><b>Type</b></a></td>");
				out
						.println("<td headers=\"col24\"  align=\"left\"><b>Owner</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out
						.println("<td headers=\"col25\" colspan=\"4\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out
						.println("<td headers=\"col26\" colspan=\"4\" align=\"left\">");
				printGreyDottedLine(out);
				out.println("</td>");
				out.println("</tr>");

				pstmt = con.prepareStatement(strQuery);

				pstmt1 = con.prepareStatement(querypending);

				pstmt2 = con.prepareStatement(querymember);

				for (int i = 0; i < vProjects.size(); i++) {

					ETSProj proj = (ETSProj) vProjects.elementAt(i);

					if ((i % 2) == 0) {
						out
								.println("<tr height=\"20\" style=\"background-color: #eeeeee\" >");
					} else {
						out.println("<tr height=\"20\" >");
					}

					String sWorkspaceOwner = "";
					String sWorkspaceType = "";

					if (proj.getProjectOrProposal().equalsIgnoreCase("O")) {
						sWorkspaceType = "Proposal";
					} else if (proj.getProjectOrProposal()
							.equalsIgnoreCase("P")) {
						sWorkspaceType = "Project";
					} else if (proj.getProjectOrProposal()
							.equalsIgnoreCase("C")) {
						sWorkspaceType = "Client voice";
					}

					pstmt.clearParameters();
					pstmt.setString(1, proj.getProjectId());
					pstmt.setString(2, proj.getProjectId());
					pstmt.setInt(3, Defines.OWNER);

					rs = pstmt.executeQuery();
					if (rs.next()) {
						sWorkspaceOwner = ETSUtils.checkNull(rs.getString(1));
					}

					boolean bPending = false;

					pstmt1.clearParameters();
					pstmt1.setString(1, es.gIR_USERN);
					pstmt1.setString(2, proj.getName());
					pstmt1.setString(3, Defines.ACCESS_PENDING);

					rs1 = pstmt1.executeQuery();
					if (rs1.next()) {
						int iCount = rs1.getInt(1);
						if (iCount > 0) {
							bPending = true;
						}
					}

					boolean bMember = false;

					pstmt2.clearParameters();
					pstmt2.setString(1, es.gIR_USERN);
					pstmt2.setString(2, proj.getProjectId());
					pstmt2.setString(3, Defines.USER_ENTITLED);

					rs2 = pstmt2.executeQuery();
					if (rs2.next()) {
						int iCount = rs2.getInt(1);
						if (iCount > 0) {
							bMember = true;
						}
					}

					if (proj.getProjectId().equalsIgnoreCase(sProjectId)) {
						out
								.println("<td headers=\"col27\" width=\"16\" height=\"10\" align=\"left\" ><input type=\"radio\" id=\"label_proj"
										+ i
										+ "\" name=\"project_id\" value=\""
										+ proj.getProjectId()
										+ "\" checked=\"checked\" /></td>");
					} else {
						out
								.println("<td headers=\"col28\" width=\"16\" height=\"10\" align=\"left\" ><input type=\"radio\" id=\"label_proj"
										+ i
										+ "\" name=\"project_id\" value=\""
										+ proj.getProjectId() + "\" /></td>");
					}

					if (bMember || bPending) {

						out
								.println("<td headers=\"col29\" width=\"250\" height=\"21\"  align=\"left\" ><label for=\"label_proj"
										+ i
										+ "\">"
										+ proj.getName()
										+ "</label> <span style=\"color:#cc6600\">*</span></td>");
					} else {
						out
								.println("<td headers=\"col30\" width=\"250\" height=\"21\"  align=\"left\" ><label for=\"label_proj"
										+ i
										+ "\">"
										+ proj.getName()
										+ "</label></td>");
					}
					out
							.println("<td headers=\"col31\" width=\"100\" align=\"left\" >"
									+ sWorkspaceType + "</td>");
					out.println("<td headers=\"col32\" align=\"left\" >"
							+ sWorkspaceOwner + "</td>");
					out.println("</tr>");

				}

				out.println("</table>");

				out.println("<br /><br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col33\" align=\"left\"><label for=\"label_comments\"><b>Comments for workspace owner</b>:</label></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"col34\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out
						.println("<td headers=\"col35\" align=\"left\"><textarea  class=\"iform\" cols=\"95\" rows=\"5\" name=\"comments\" id=\"label_comments\" maxlength=\"1000\"></textarea></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");

				printGreyDottedLine(out);
				out.println("<br />");

				// display the messages here...

				//				boolean bEntitlement = es.Qualify(Defines.ETS_ENTITLEMENT,
				// "tg_member=MD");
				//
				//				out.println("<table cellspacing=\"0\" cellpadding=\"0\"
				// border=\"0\" width=\"100%\">");
				//				out.println("<tr>");
				//				out.println("<td headers=\"col36\" align=\"left\">");
				//
				//				out.println("<b>Note</b>: The following will happen after you
				// click <b>Submit</b>");
				//				out.println("<br /><br />");
				//
				//				if (!bEntitlement) {
				//					String sMgrEmail =
				// ETSUtils.getManagersEMailFromDecafTable(con,es.gUSERN);
				//					if (sMgrEmail == null ||
				// sMgrEmail.trim().equalsIgnoreCase("")) {
				//						sMgrEmail = "";
				//					} else {
				//						sMgrEmail = "at " + sMgrEmail;
				//					}
				//					out.println("<ul>");
				//					out.println("<li>Your request will be forwarded to your
				// manager " + sMgrEmail + " for approval to access E&TS
				// Connect.</li>");
				//					out.println("<li>Your request will also be forwarded to the
				// workspace owner of the workspace you have selected for
				// approval.</li>");
				//					out.println("</ul>");
				//					out.println("<br />");
				//					out.println("Once your manager and the workspace owner have
				// approved your request, you will be able to access this
				// workspace.");
				//				} else {
				//					out.println("<ul>");
				//					out.println("<li>Your request will be forwarded to the
				// workspace owner of the workspace you have selected for
				// approval.</li>");
				//					out.println("</ul>");
				//					out.println("<br />");
				//					out.println("Once the workspace owner has approved your
				// request, you will be able to access this workspace.");
				//				}
				//
				//				out.println("</td>");
				//				out.println("</tr>");
				//				out.println("</table>");

				out.println("<br />");
				printGreyDottedLine(out);

				out.println("<br />");
				out
						.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out
						.println("<td headers=\"col37\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\""
								+ Defines.BUTTON_ROOT
								+ "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
				out
						.println("<td headers=\"col38\" align=\"left\" width=\"80\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col39\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
								+ Defines.BUTTON_ROOT
								+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col40\"  align=\"left\"><a href=\""
								+ Defines.SERVLET_PATH
								+ "AICConnectServlet.wss?linkid="
								+ params.getLinkId()
								+ "\" >Cancel</a></td></tr></table></td>");
				boolean bIBMer = ETSUtils.isIBMer(es.gIR_USERN, con);
				if (bIBMer) {
					out
							.println("<td headers=\"col41\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col42\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
									+ Defines.BUTTON_ROOT
									+ "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Create Collaboration center Workspace\" border=\"0\" /></td><td headers=\"col43\"  align=\"left\"><a href=\""
									+ Defines.SERVLET_PATH
									+ sCreateWSServlet
									+ "?linkid="
									+ params.getLinkId()
									+ "&workspace_company="
									+ URLEncoder.encode(sCompany)
									+ "\" >Create new Collaboration Center workspace</a></td></tr></table></td>");
				}
				out.println("</tr></table>");

				out
						.println("<input type=\"hidden\" name=\"act\" value=\"requestconfirm\" />");

				out.println("");

			} else {

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col44\" align=\"left\">There are no workspaces available for client: <b>"
								+ sCompany + "</b>.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col45\" align=\"left\">If you would like to create a new workspace for this client company, please click the <b>Create new workspace</b> link below.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");

				printGreyDottedLine(out);

				out.println("<br />");
				out
						.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				boolean bIBMer = ETSUtils.isIBMer(es.gIR_USERN, con);
				if (bIBMer) {
					out
							.println("<td headers=\"col46\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col47\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
									+ Defines.BUTTON_ROOT
									+ "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Create Collaboration center Workspace\" border=\"0\" /></td><td headers=\"col48\"  align=\"left\"><a href=\""
									+ Defines.SERVLET_PATH
									+ sCreateWSServlet
									+ "?linkid="
									+ params.getLinkId()
									+ "&workspace_company="
									+ URLEncoder.encode(sCompany)
									+ "\" >Create new Collaboration Center workspace</a></td></tr></table></td>");
				}
				out
						.println("<td headers=\"col49\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col50\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
								+ Defines.BUTTON_ROOT
								+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col51\"  align=\"left\"><a href=\""
								+ Defines.SERVLET_PATH
								+ "AICConnectServlet.wss?linkid="
								+ params.getLinkId()
								+ "\" >Cancel</a></td></tr></table></td>");
				out.println("</tr></table>");

			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(pstmt1);
			ETSDBUtils.close(pstmt2);
		}

	}

	public void confirmRequestWorkspacesScreen() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		try {

			String sCompany = ETSUtils.checkNull(request
					.getParameter("client_company"));
			String sProjectId = ETSUtils.checkNull(request
					.getParameter("project_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("comments"));
			String sWorkspaceOwnerEmail = "";
			boolean bTeamroomWrkspc = false;
			ETSProj etsProj = ETSUtils.getProjectDetails(con, sProjectId);

			String sProjectName = etsProj.getName();
			if (etsProj.getIsPrivate().equals(Defines.AIC_IS_PRIVATE_TEAMROOM)
					|| etsProj.getIsPrivate().equals(
							Defines.AIC_IS_RESTRICTED_TEAMROOM)) {
				bTeamroomWrkspc = true;
			}
			// to get the workspace owner's email
			String strQuery = "SELECT USER_EMAIL FROM AMT.USERS A, ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND B.USER_PROJECT_ID = ? AND B.USER_ROLE_ID IN (SELECT ROLE_ID FROM ETS.ETS_ROLES WHERE PROJECT_ID = ? AND PRIV_ID = ?) with ur";

			pstmt = con.prepareStatement(strQuery.toString());

			pstmt.clearParameters();
			pstmt.setString(1, sProjectId);
			pstmt.setString(2, sProjectId);
			pstmt.setInt(3, Defines.OWNER);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				sWorkspaceOwnerEmail = ETSUtils.checkNull(rs.getString(1));
			}

			if (!bTeamroomWrkspc) {
				ETSUserAccessRequest userRequest = new ETSUserAccessRequest();

				userRequest.recordRequestWithComments(con, es.gIR_USERN,
						sProjectName, sWorkspaceOwnerEmail, es.gIR_USERN,
						sComments, Defines.AIC_WORKSPACE_TYPE);

				// check if the requestor hasETSProjects entitlement, if not
				// request for one.

				boolean bEntRequested = false;

				boolean bHasEnt = AICWorkspaceDAO
						.doesUserHaveSalesCollabEntitlement(con, es.gIR_USERN);

				if (!bHasEnt) {

					ETSUserAccessRequest uar = new ETSUserAccessRequest();
					uar.setTmIrId(es.gIR_USERN);
					uar.setPmIrId(es.gIR_USERN);
					uar.setDecafEntitlement(Defines.AIC_ENTITLEMENT);
					uar.setIsAProject(true);
					uar.setUserCompany("");
					//	TODO need to check with Access Request from JV
					ETSStatus status = uar.request(con);

					if (status.getErrCode() == 0) {
						bEntRequested = true;
					} else {
						bEntRequested = false;
					}
				}

				// 	send the email to workspace owner...

				ETSMail mail = createWorkspaceOwnerRequestEmail();

				boolean bSuccess = ETSUtils.sendEmail(mail);

				out.println("<br />");

				//printGreyDottedLine(out);

				out.println("<br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col52\" align=\"left\"><b>Your request has been successfully submitted.</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"col53\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

				if (bEntRequested) {
					String sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(
							con, es.gUSERN);
					if (!sMgrEmail.trim().equalsIgnoreCase("")) {
						sMgrEmail = "at " + sMgrEmail;
					}

					out.println("<tr>");
					out
							.println("<td headers=\"col54\" align=\"left\"><ul><li>Your request has been forwarded to the workspace owner "
									+ sWorkspaceOwnerEmail
									+ " of the workspace you have selected for approval. You will receive an e-mail from the workspace owner when this has been processed.</li></ul></td>");
					out.println("</tr>");
					out.println("<tr>");
					out
							.println("<td headers=\"col55\" align=\"left\"><ul><li>Your request has been forwarded to your manager "
									+ sMgrEmail
									+ " for approval to access AIC Connect. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</li></ul></td>");
					out.println("</tr>");
					out.println("<tr>");
					out
							.println("<td headers=\"col56\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("<tr>");
					out
							.println("<td headers=\"col57\" align=\"left\">Once the workspace owner and your manager have approved your request, you will be able to access this workspace.</td>");
					out.println("</tr>");

				} else {
					out.println("<tr>");
					out
							.println("<td headers=\"col58\" align=\"left\"><ul><li>Your request has been forwarded to the workspace owner "
									+ sWorkspaceOwnerEmail
									+ " of the workspace you have selected for approval. You will receive an e-mail from the workspace owner when this has been processed.</li></ul></td>");
					out.println("</tr>");
					out.println("<tr>");
					out
							.println("<td headers=\"col59\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("<tr>");
					out
							.println("<td headers=\"col60\" align=\"left\">Once the workspace owner has approved your request, you will be able to access this workspace.</td>");
					out.println("</tr>");
				}

			} else {
				out.println("<br />");

				//printGreyDottedLine(out);

				out.println("<br />");

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col52\" align=\"left\"><b>Please request entitlement to one of the projects listed below for access to this workspace from decaf.</b></td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"coc1\" align=\"left\">"
						+ etsProj.getCompany() + " BPS OWNER "
						+ etsProj.getName() + "</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"coc2\" align=\"left\">"
						+ etsProj.getCompany() + " BPS AUTHOR "
						+ etsProj.getName() + "</td>");
				out.println("</tr>");
				out.println("<tr><td headers=\"coc3\" align=\"left\">"
						+ etsProj.getCompany() + " BPS READER "
						+ etsProj.getName() + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"col53\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

			}
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br /><br />");
			out
					.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out
					.println("<td headers=\"col61\"  width=\"250\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col62\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to Collaboration Center main page\" border=\"0\" /></td><td headers=\"col63\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICConnectServlet.wss?linkid="
							+ params.getLinkId()
							+ "\" >Go to Collaboration Center main page</a></td></tr></table></td>");
			out
					.println("<td headers=\"col64\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col65\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col66\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICUserAccessServlet.wss?linkid="
							+ params.getLinkId()
							+ "&act=clientcontinue&from=req&client_company=IBM\" >Request access to another workspace</a></td></tr></table></td>");
			out.println("</tr></table>");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}

	}

	private static void printGreyDottedLine(PrintWriter out) {

		out.println("<!-- Gray dotted line -->");
		out
				.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"col67\" width=\"1\"><img src=\""
				+ Defines.TOP_IMAGE_ROOT
				+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out
				.println("<td headers=\"col68\" background=\""
						+ Defines.V11_IMAGE_ROOT
						+ "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"col69\" width=\"1\"><img src=\""
				+ Defines.TOP_IMAGE_ROOT
				+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");

	}

	public void showRequestScreenForExternal(String sError)
			throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();

		try {

			DecafSuperSoldTos companies = new DecafSuperSoldTos();
			Vector vCompany = companies.getSupSoldToList(con);

			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out
						.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out
						.println("<td headers=\"col70\" align=\"left\"><span style=\"color:#ff3333\">"
								+ sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col71\" align=\"left\">Thank you for your interest in Collaboration Center.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col72\" align=\"left\">Please help us identify the workspace you'll need to access.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col73\" align=\"left\"><ul><li>Provide the workspace name, to the best of your knowledge.</li><li>Provide the e-mail address of your IBM contact, preferably a principal or project manager, for the workspace.</ul></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col74\" align=\"left\">You will be notified as soon as your request has been processed.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			//			printGreyDottedLine(out);
			//
			//			out.println("<br /><br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col75\" width=\"210\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"w_proj\">Workspace Name:</label></b></td>");
			out
					.println("<td headers=\"col76\" align=\"left\" colspan=\"2\"><input type=\"text\"  class=\"iform\" name=\"proj_name\" id=\"w_proj\" value=\"\" size=\"25\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"col77\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"col78\" width=\"210\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"w_email\">E-mail address of IBM contact:</label></b></td>");
			out
					.println("<td headers=\"col79\" align=\"left\" width=\"170\"><input type=\"text\"  class=\"iform\" name=\"email_id\" id=\"w_email\" value=\"\" size=\"25\" /></td>");
			out
					.println("<td headers=\"col80\" align=\"left\"><a class=\"fbox\" href=\"//www.ibm.com/contact/employees/us/\" target=\"new\" class=\"fbox\" onclick=\"window.open('//www.ibm.com/contact/employees/us/','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=750,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('//www.ibm.com/contact/employees/us/','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=750,height=500,left=150,top=120'); return false;\">Look up e-mail address of IBM contact</a></td>");

			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col81\" align=\"left\"><label for=\"w_comments\"><b>Additional comments</b>:</label></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out
					.println("<td headers=\"col82\" align=\"left\"><textarea id=\"w_comments\" name=\"add_comments\" value=\"\" cols=\"65\" rows=\"5\"></textarea></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col83\" align=\"left\"><span style=\"color:#cc6600\">*</span>Required fields are indicated with asterisk(*).</td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out
					.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out
					.println("<td headers=\"col84\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\""
							+ Defines.BUTTON_ROOT
							+ "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" /></td>");
			out
					.println("<td headers=\"col85\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col86\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\""
							+ Defines.BUTTON_ROOT
							+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"col87\"  align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICConnectServlet.wss?linkid="
							+ params.getLinkId()
							+ "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");

			out
					.println("<input type=\"hidden\" name=\"act\" value=\"externalclient\" />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	public void confirmRequestFromExternal() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();

		try {

			ETSMail mail = new ETSMail();

			String sProjectName = ETSUtils.checkNull(request
					.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request
					.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("add_comments"));

			ETSUserAccessRequest userRequest = new ETSUserAccessRequest();

			if (isValidEmail(con, sMgrEmailId)) {
				if (isWkspcOwner(con, sMgrEmailId)
						|| isWkspcManager(con, sMgrEmailId)) {
					userRequest.recordRequestWithComments(con, es.gIR_USERN,
							sProjectName, sMgrEmailId, es.gIR_USERN, sComments,
							Defines.AIC_WORKSPACE_TYPE);
					mail = createManagerExternalEmail();
				} else {
					if (isTeamMbr(con, sMgrEmailId)) {
						userRequest.recordRequestWithComments(con,
								es.gIR_USERN, sProjectName, sMgrEmailId,
								es.gIR_USERN, sComments,
								Defines.AIC_WORKSPACE_TYPE);
						mail = createTeamMemberExternalEmail();
					} else {
						mail = createAdminRequestEmail();
					}
				}
			} else {
				mail = createAdminRequestEmail();
			}

			boolean bSuccess = ETSUtils.sendEmail(mail);

			out.println("<br /><br />");

			out
					.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out
					.println("<td headers=\"col88\" align=\"left\"><b>Your request has been submitted successfully.</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br /><br />");
			out
					.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out
					.println("<td headers=\"col89\"  width=\"140\" align=\"left\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "AICConnectServlet.wss?linkid="
							+ params.getLinkId()
							+ "\" ><img src=\""
							+ Defines.BUTTON_ROOT
							+ "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr></table>");

		} catch (Exception e) {
			throw e;
		}

	}

	public String validateClientScreen() throws Exception {

		StringBuffer sError = new StringBuffer("");
		HttpServletRequest request = this.params.getRequest();

		try {

			String sCompany = ETSUtils.checkNull(request
					.getParameter("client_company"));

			if (sCompany.equalsIgnoreCase("")) {
				sError
						.append("Please select the client company from the list. <br />");
			}

		} catch (Exception e) {
			throw e;
		}

		return sError.toString();

	}

	public String validateClientExternal() throws Exception {

		StringBuffer sError = new StringBuffer("");
		HttpServletRequest request = this.params.getRequest();

		try {

			String sProjName = ETSUtils.checkNull(request
					.getParameter("proj_name"));

			if (sProjName.equalsIgnoreCase("")) {
				sError
						.append("Please enter the project or proposal name. <br />");
			}

			String sEmail = ETSUtils
					.checkNull(request.getParameter("email_id"));

			if (sEmail.equalsIgnoreCase("")) {
				sError
						.append("Please enter the e-mail address of IBM contact.<br />");
			}

		} catch (Exception e) {
			throw e;
		}

		return sError.toString();

	}

	public String validateRequestWorkspace() throws Exception {

		StringBuffer sError = new StringBuffer("");
		HttpServletRequest request = this.params.getRequest();
		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		Connection con = this.params.getConnection();

		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;

		try {

			String sProjectId = ETSUtils.checkNull(request
					.getParameter("project_id"));

			if (sProjectId.equalsIgnoreCase("")) {
				sError
						.append("Please select the workspace you want to request access to.<br />");
			}

			String sProjectName = ETSWorkspaceDAO.getWorkspaceName(con,
					sProjectId);

			boolean bPending = false;
			boolean bMember = false;

			// check to see if the member request is pending or is already a
			// member.. if yes, display error..

			// to get the pending request
			String querypending = "SELECT COUNT(PROJECT_NAME) FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = ? AND STATUS = ? with ur";

			// to check if memeber off...
			String querymember = "SELECT COUNT(USER_ID) FROM ETS.ETS_USERS WHERE USER_ID = ? AND USER_PROJECT_ID = ? AND ACTIVE_FLAG = ? with ur";

			pstmt1 = con.prepareStatement(querypending);

			pstmt1.clearParameters();
			pstmt1.setString(1, es.gIR_USERN);
			pstmt1.setString(2, sProjectName);
			pstmt1.setString(3, Defines.ACCESS_PENDING);

			rs1 = pstmt1.executeQuery();
			if (rs1.next()) {
				int iCount = rs1.getInt(1);
				if (iCount > 0) {
					bPending = true;
				}
			}

			if (!bPending) {
				pstmt2 = con.prepareStatement(querymember);

				pstmt2.clearParameters();
				pstmt2.setString(1, es.gIR_USERN);
				pstmt2.setString(2, sProjectId);
				pstmt2.setString(3, Defines.USER_ENTITLED);

				rs2 = pstmt2.executeQuery();
				if (rs2.next()) {
					int iCount = rs2.getInt(1);
					if (iCount > 0) {
						bMember = true;
					}
				}
			}

			if (bMember || bPending) {
				sError
						.append("Please select a workspace for which you are not a member or you do not have a pending access request.<br />");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(pstmt1);
			ETSDBUtils.close(pstmt2);
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(rs2);
		}

		return sError.toString();

	}

	public ETSMail createAdminRequestEmail() throws SQLException, Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		UnbrandedProperties prop = PropertyFactory
				.getProperty(Defines.AIC_WORKSPACE_TYPE);
		String sLinkId = request.getParameter("linkid");
		if (sLinkId == null || sLinkId.equals("")) {
			sLinkId = prop.getLinkID(); //210000
		}

		try {

			String sProjectName = ETSUtils.checkNull(request
					.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request
					.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("add_comments"));

			mail.setSubject("Access Request to Collaboration Center from  "
					+ es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim()
					+ " at " + es.gASSOC_COMP);

			sEmailStr
					.append("This request has been submitted but it cannot be processed automatically.\n");
			sEmailStr
					.append("The IBM Contact identified is not is a known workspace owner nor a member\n");
			sEmailStr
					.append("of any workspace. Investigate IBM contact given (find the person, \n");
			sEmailStr
					.append("are they in Collaboration Center, etc). Then work with workspace representatives to determine\n");
			sEmailStr.append("how to process this request.\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN
					+ "\n");
			sEmailStr
					.append("Name                          : "
							+ es.gFIRST_NAME.trim() + " "
							+ es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL
					+ "\n");
			sEmailStr.append("User company                  : "
					+ es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Workspace requested 			: " + sProjectName + "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId
					+ "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");

			sEmailStr
					.append("Click the link below to access Collaboration Center.\n\n");
			sEmailStr.append(oem.edge.common.Global
					.getUrl("ets/AICConnectServlet.wss?linkid=" + sLinkId
							+ "\n\n"));

			sEmailStr.append(CommonEmailHelper
					.getEmailFooter(prop.getAppName()));

			mail.setMessage(sEmailStr.toString());
			mail.setTo(sMgrEmailId);
			mail.setFrom(es.gEMAIL);
			mail.setBcc("");
			mail.setCc("");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public ETSMail createManagerExternalEmail() throws SQLException, Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		UnbrandedProperties prop = PropertyFactory
				.getProperty(Defines.AIC_WORKSPACE_TYPE);
		String sLinkId = request.getParameter("linkid");
		if (sLinkId == null || sLinkId.equals("")) {
			sLinkId = prop.getLinkID(); //210000
		}

		try {

			String sProjectName = ETSUtils.checkNull(request
					.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request
					.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("add_comments"));

			mail.setSubject("Access Request to Collaboration Center from  "
					+ es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim()
					+ " at " + es.gASSOC_COMP);

			sEmailStr
					.append("You have been identified as the Workspace Owner for the following\n");
			sEmailStr
					.append("request for access to Collaboration Center. Please process accordingly by clicking \n");
			sEmailStr
					.append("on the link(s) below. Verify information and choose to accept or reject their\n");
			sEmailStr
					.append("request. If you do not have access to this workspace please forward this to "
							+ prop.getAdminEmailID() + ".\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN
					+ "\n");
			sEmailStr
					.append("Name                          : "
							+ es.gFIRST_NAME.trim() + " "
							+ es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL
					+ "\n");
			sEmailStr.append("User company                  : "
					+ es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Workspace requested           : " + sProjectName
					+ "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId
					+ "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");

			sEmailStr
					.append("Click the link below to access Collaboration Center.\n\n");
			sEmailStr.append(oem.edge.common.Global
					.getUrl("ets/AICConnectServlet.wss?linkid=" + sLinkId
							+ "\n\n"));

			sEmailStr.append(CommonEmailHelper
					.getEmailFooter(prop.getAppName()));

			mail.setMessage(sEmailStr.toString());
			mail.setTo(sMgrEmailId);
			mail.setFrom(es.gEMAIL);
			mail.setBcc("");
			mail.setCc("");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public ETSMail createTeamMemberExternalEmail() throws SQLException,
			Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		UnbrandedProperties prop = PropertyFactory
				.getProperty(Defines.AIC_WORKSPACE_TYPE);
		String sLinkId = request.getParameter("linkid");
		if (sLinkId == null || sLinkId.equals("")) {
			sLinkId = prop.getLinkID(); //210000
		}

		try {

			String sProjectName = ETSUtils.checkNull(request
					.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request
					.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("add_comments"));

			mail.setSubject("Access Request to Collaboration Center from  "
					+ es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim()
					+ " at " + es.gASSOC_COMP);

			sEmailStr
					.append("You have been identified as the IBM contact for the following request\n");
			sEmailStr
					.append("for access to Collaboration Center. Please process by selecting the \n");
			sEmailStr
					.append("link provided below. If you do not have access to this workspace,\n");
			sEmailStr.append("please forward this to " + prop.getAdminEmailID()
					+ ".\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN
					+ "\n");
			sEmailStr
					.append("Name                          : "
							+ es.gFIRST_NAME.trim() + " "
							+ es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL
					+ "\n");
			sEmailStr.append("User company                  : "
					+ es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Workspace requested 			: " + sProjectName + "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId
					+ "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");

			sEmailStr
					.append("Click the link below to access Collaboration Center, select the workspace that matches the request\n");
			sEmailStr
					.append("and then do a \"Request add members\" under team tab.\n\n");

			sEmailStr.append(oem.edge.common.Global
					.getUrl("ets/AICConnectServlet.wss?linkid=" + sLinkId
							+ "\n\n"));

			sEmailStr.append(CommonEmailHelper
					.getEmailFooter(prop.getAppName()));

			mail.setMessage(sEmailStr.toString());
			mail.setTo(sMgrEmailId);
			mail.setFrom(es.gEMAIL);
			mail.setBcc("");
			mail.setCc("");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public ETSMail createWorkspaceOwnerRequestEmail() throws SQLException,
			Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		UnbrandedProperties prop = PropertyFactory
				.getProperty(Defines.AIC_WORKSPACE_TYPE);

		try {

			String sCompany = ETSUtils.checkNull(request
					.getParameter("client_company"));
			String sProjectId = ETSUtils.checkNull(request
					.getParameter("project_id"));
			String sComments = ETSUtils.checkNull(request
					.getParameter("comments"));
			String sLinkId = request.getParameter("linkid");
			if (sLinkId == null || sLinkId.equals("")) {
				sLinkId = prop.getLinkID(); //210000
			}

			String sWorkspaceOwnerEmail = "";

			String sProjectName = ETSWorkspaceDAO.getWorkspaceName(con,
					sProjectId);

			// to get the workspace owner's email
			String strQuery = "SELECT USER_EMAIL FROM AMT.USERS A, ETS.ETS_USERS B WHERE A.IR_USERID = B.USER_ID AND B.USER_PROJECT_ID = ? AND B.USER_ROLE_ID IN (SELECT ROLE_ID FROM ETS.ETS_ROLES WHERE PROJECT_ID = ? AND PRIV_ID = ?) with ur";

			pstmt = con.prepareStatement(strQuery.toString());

			pstmt.clearParameters();
			pstmt.setString(1, sProjectId);
			pstmt.setString(2, sProjectId);
			pstmt.setInt(3, Defines.OWNER);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				sWorkspaceOwnerEmail = ETSUtils.checkNull(rs.getString(1));
			}

			mail.setSubject("Access Request to Collaboration Center from  "
					+ es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim());

			sEmailStr.append("ACTION REQUIRED: \n\n");

			sEmailStr
					.append("You have been identified as the Workspace Owner for the following\n");
			sEmailStr
					.append("request for access to Collaboration Center. \n\n");

			sEmailStr
					.append("Click the link below to access Collaboration Center, scroll down to\n");
			sEmailStr
					.append("\"Pending access requests\" section and click on the user name which\n");
			sEmailStr.append("you want to process.\n");
			sEmailStr.append(oem.edge.common.Global
					.getUrl("ets/AICConnectServlet.wss?linkid=" + sLinkId
							+ "\n\n"));

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN
					+ "\n");
			sEmailStr
					.append("Name                          : "
							+ es.gFIRST_NAME.trim() + " "
							+ es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL
					+ "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Requested Workspace           : "
					+ ETSWorkspaceDAO.getWorkspaceName(con, sProjectId)
					+ "\n\n");

			if (!sComments.trim().equalsIgnoreCase("")) {
				sEmailStr.append("Comments entered by user:\n");
				sEmailStr.append(sComments + "\n\n");
			}

			sEmailStr.append(CommonEmailHelper
					.getEmailFooter(prop.getAppName()));

			mail.setMessage(sEmailStr.toString());
			mail.setTo(sWorkspaceOwnerEmail);
			mail.setFrom(es.gEMAIL);
			mail.setBcc("");
			mail.setCc("");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public void invalidFunctionality() {
		PrintWriter out = this.params.getWriter();
		out.println("<br /><br />");

		out
				.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
		out.println("<tr>");
		out
				.println("<td headers=\"col90\" align=\"left\"><b>Presently only IBMers are having access to the application. Please contact the System Administrator.</b></td>");
		out.println("</tr>");
		out.println("</table>");

		out.println("<br />");

	}

	private boolean isValidEmail(Connection con, String email)
			throws SQLException, Exception {
		String val = EntitledStatic.getValue(con,
				"select distinct 1 from amt.users where user_email = '" + email
						+ "' with ur");
		if (val != null && val.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isWkspcOwner(Connection con, String woEmail)
			throws SQLException, Exception {
		return this.userHasRole(con, woEmail, "Workspace Owner");
	}

	private boolean isWkspcManager(Connection con, String wmEmail)
			throws SQLException, Exception {
		return this.userHasRole(con, wmEmail, "Workspace Manager");
	}

	private boolean isTeamMbr(Connection con, String teamMbr)
			throws SQLException, Exception {
		return this.userHasRole(con, teamMbr, "Member");
	}

	public boolean userHasRole(Connection con, String emailid, String role)
			throws SQLException, Exception {
		String userid = EntitledStatic
				.getValue(
						con,
						"select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '"
								+ emailid
								+ "' and a.ir_userid = b.user_id order by a.ir_userid fetch first 1 row only with ur");
		if (userid != null) {
			// user is in ETS
			String val = EntitledStatic
					.getValue(
							con,
							"select 1 from ets.ets_users a, ets.ets_roles b where a.user_id='"
									+ userid
									+ "' and a.user_role_id=b.role_id and b.role_name='"
									+ role + "' fetch first 1 row only");
			if (val != null && val.equals("1")) {
				return true;
			} else {
				return false;
			}
		} else {
			// user not in ETS
			return false;
		}
	}
}
