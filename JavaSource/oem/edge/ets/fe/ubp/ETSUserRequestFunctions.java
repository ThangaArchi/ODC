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


package oem.edge.ets.fe.ubp;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.decaf.DecafSuperSoldTos;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

import org.apache.commons.logging.Log;



/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSUserRequestFunctions {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.22";

	private static Log logger = EtsLogger.getLogger(ETSUserRequestFunctions.class);

	private ETSParams params;

	public ETSUserRequestFunctions(ETSParams parameters) {
		this.params = parameters;
	}


	public void showClientScreen(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();

		try {

			DecafSuperSoldTos companies = new DecafSuperSoldTos();
			Vector vCompany = companies.getSupSoldToList(con);

			String sComp = ETSUtils.checkNull(request.getParameter("client_company"));

			out.println("<br /><br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Please select the client (Company) for which you would like to request access and click on Continue.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			//printGreyDottedLine(out);

			//out.println("<br /><br />");


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"150\" align=\"left\"><b><label for=\"w_company\">Client company:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\"><select  class=\"iform\" name=\"client_company\" id=\"w_company\" class=\"iform\" width=\"200px\" style=\"width:200px\">");

			out.println("<option value=\"\" selected=\"selected\">Select Company</option>");
			for (int i = 0; i < vCompany.size(); i++) {
				String sTemp = (String) vCompany.elementAt(i);
				if (sComp.equalsIgnoreCase(sTemp)) {
					out.println("<option value=\"" + sTemp + "\" selected=\"selected\">" + sTemp + "</option>");
				} else {
					out.println("<option value=\"" + sTemp + "\">" + sTemp + "</option>");
				}

			}

			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">If you do not find your client's company then use the link below to add it to the system. </td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Request to add company\" border=\"0\" /></td><td headers=\"\"  align=\"left\" width=\"180\"><a href=\"" + Defines.SERVLET_PATH + "ETSNewCompanyServlet.wss?linkid=" + params.getLinkId() + "\" >Request to add new company</a></td><td headers=\"\"  width=\"2\" valign=\"middle\" align=\"left\">[ </td><td headers=\"\"  width=\"20\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.ICON_ROOT + "popup.gif\" width=\"16\" height=\"16\" alt=\"Help\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=create_company&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=create_company&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=create_company&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Help</a></td><td headers=\"\"  width=\"20\" valign=\"middle\" align=\"left\"> ]</td></tr></table></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");

			out.println("<input type=\"hidden\" name=\"act\" value=\"clientcontinue\" />");


		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}

	}

	public void showWorkspacesScreen(String sError) throws SQLException, Exception {

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
			String querypending = "SELECT COUNT(PROJECT_NAME) FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = ? AND STATUS = ? AND PROJECT_TYPE = ? with ur";

			// to check if memeber off...
			String querymember = "SELECT COUNT(USER_ID) FROM ETS.ETS_USERS WHERE USER_ID = ? AND USER_PROJECT_ID = ? AND ACTIVE_FLAG = ? with ur";


			String sCompany = ETSUtils.checkNull(request.getParameter("client_company"));

			out.println("<br /><br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}
			String sProjectId = ETSUtils.checkNull(request.getParameter("project_id"));

			Vector vProjects = ETSUserRequestDAO.getActiveWorkspacesForCompany(con,sCompany);

			if (vProjects != null && vProjects.size() > 0) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">The following workspaces are available for client company: <b>" + sCompany + "</b></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<input type=\"hidden\" name=\"client_company\" value=\"" + sCompany + "\" /> ");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">Workspaces marked with <span style=\"color:#cc6600\">*</span> are the workspaces for which you already have access or an access request is pending with the workspace owner. Please select the workspace you require access to and click on Submit.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">If you do not find the workspace you are interested in below and would like to create a new workspace for this client company, please click the <b>Create new workspace</b> link at the bottom of the page. </td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br /><br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\" width=\"266\" align=\"left\"><b>Name of workspace</b></td>");
				out.println("<td headers=\"\" width=\"100\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=projtype&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><b>Type</b></a></td>");
				out.println("<td headers=\"\"  align=\"left\"><b>Owner</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"4\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"4\" align=\"left\">");
				printGreyDottedLine(out);
				out.println("</td>");
				out.println("</tr>");

				pstmt = con.prepareStatement(strQuery);

				pstmt1 = con.prepareStatement(querypending);

				pstmt2 = con.prepareStatement(querymember);


				for (int i = 0; i < vProjects.size(); i++) {

					ETSProj proj = (ETSProj) vProjects.elementAt(i);

					if ((i % 2) == 0) {
						out.println("<tr height=\"20\" style=\"background-color: #eeeeee\" >");
					} else {
						out.println("<tr height=\"20\" >");
					}

					String sWorkspaceOwner = "";
					String sWorkspaceType = "";

					if (proj.getProjectOrProposal().equalsIgnoreCase("O")) {
						sWorkspaceType = "Proposal";
					} else if (proj.getProjectOrProposal().equalsIgnoreCase("P")) {
						sWorkspaceType = "Project";
					} else if (proj.getProjectOrProposal().equalsIgnoreCase("C")) {
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
					pstmt1.setString(4, Defines.ETS_WORKSPACE_TYPE);

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
						out.println("<td headers=\"\" width=\"16\" height=\"10\" align=\"left\" ><input type=\"radio\" id=\"label_proj" + i + "\" name=\"project_id\" value=\"" +  proj.getProjectId() + "\" checked=\"checked\" /></td>");
					} else {
						out.println("<td headers=\"\" width=\"16\" height=\"10\" align=\"left\" ><input type=\"radio\" id=\"label_proj" + i + "\" name=\"project_id\" value=\"" +  proj.getProjectId() + "\" /></td>");
					}

					if (bMember || bPending) {

						out.println("<td headers=\"\" width=\"250\" height=\"21\"  align=\"left\" ><label for=\"label_proj" + i + "\">" + proj.getName() + "</label> <span style=\"color:#cc6600\">*</span></td>");
					} else {
						out.println("<td headers=\"\" width=\"250\" height=\"21\"  align=\"left\" ><label for=\"label_proj" + i + "\">" + proj.getName() + "</label></td>");
					}
					out.println("<td headers=\"\" width=\"100\" align=\"left\" >" + sWorkspaceType + "</td>");
					out.println("<td headers=\"\" align=\"left\" >" + sWorkspaceOwner + "</td>");
					out.println("</tr>");

				}

				out.println("</table>");

				out.println("<br /><br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><label for=\"label_comments\"><b>Comments for workspace owner</b>:</label></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><textarea  class=\"iform\" cols=\"95\" rows=\"5\" name=\"comments\" id=\"label_comments\" maxlength=\"1000\"></textarea></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");

				printGreyDottedLine(out);
				out.println("<br />");

				// display the messages here...


				out.println("<br />");
				printGreyDottedLine(out);

				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
				out.println("<td headers=\"\" align=\"left\" width=\"80\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSUserAccessServlet.wss?client_company=" + URLEncoder.encode(sCompany) + "&from=" + request.getParameter("from") + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Request to add company\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(sCompany) + "\" >Create new workspace</a></td></tr></table></td>");
				out.println("</tr></table>");

				out.println("<input type=\"hidden\" name=\"act\" value=\"requestconfirm\" />");

				out.println("");

			} else {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">There are no workspaces available for client: <b>" + sCompany + "</b>.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">If you would like to create a new workspace for this client company, please click the <b>Create new workspace</b> link below.</td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br /><br />");

				printGreyDottedLine(out);

				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\" width=\"170\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Request to add company\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSCreateWorkspaceServlet.wss?linkid=" + params.getLinkId() + "&workspace_company=" + URLEncoder.encode(sCompany) + "\" >Create new workspace</a></td></tr></table></td>");
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSUserAccessServlet.wss?client_company=" + URLEncoder.encode(sCompany) + "&from=" + request.getParameter("from") + "&linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
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

				String sCompany = ETSUtils.checkNull(request.getParameter("client_company"));
				String sProjectId = ETSUtils.checkNull(request.getParameter("project_id"));
				String sComments = ETSUtils.checkNull(request.getParameter("comments"));
				String sWorkspaceOwnerEmail = "";

				ETSProj proj = ETSUtils.getProjectDetails(con,sProjectId);

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

				ETSUserAccessRequest userRequest = new ETSUserAccessRequest();

				userRequest.recordRequestWithComments(con,es.gIR_USERN,proj.getName(),sWorkspaceOwnerEmail,es.gIR_USERN,sComments,Defines.ETS_WORKSPACE_TYPE);

				// check if the requestor hasETSProjects entitlement, if not request for one.

				boolean bEntRequested = false;

				boolean bHasEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,es.gIR_USERN,Defines.ETS_ENTITLEMENT);

				if (!bHasEnt) {

					ETSUserAccessRequest uar = new ETSUserAccessRequest();
					uar.setTmIrId(es.gIR_USERN);
					uar.setPmIrId(es.gIR_USERN);
					uar.setDecafEntitlement(Defines.REQUEST_PROJECT);
					uar.setIsAProject(true);
					uar.setUserCompany("");
					ETSStatus status = uar.request(con);

					if (status.getErrCode() == 0) {
						bEntRequested = true;
					} else {
						bEntRequested  = false;
					}
				}


				boolean bITARRequested = false;

				if (proj.isITAR()) {
					boolean bHasITAREnt = ETSWorkspaceDAO.doesUserHaveEntitlement(con,es.gIR_USERN,Defines.ITAR_ENTITLEMENT);

					if (!bHasEnt) {

						ETSUserAccessRequest uar = new ETSUserAccessRequest();
						uar.setTmIrId(es.gIR_USERN);
						uar.setPmIrId(es.gIR_USERN);
						uar.setDecafEntitlement(Defines.ITAR_PROJECT);
						uar.setIsAProject(true);
						uar.setUserCompany("");
						ETSStatus status = uar.request(con);

						if (status.getErrCode() == 0) {
							bITARRequested = true;
						} else {
							bITARRequested  = false;
						}
					}
				}

				// send the email to workspace owner...

				ETSMail mail = createWorkspaceOwnerRequestEmail();

				boolean bSuccess = ETSUtils.sendEmail(mail);

				out.println("<br />");

				//printGreyDottedLine(out);

				out.println("<br />");

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><b>Your request has been successfully submitted.</b></td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

				if (bEntRequested) {
					String sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(con,es.gUSERN);
					if (!sMgrEmail.trim().equalsIgnoreCase("")) {
						sMgrEmail = "at " + sMgrEmail;
					}

					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\"><ul><li>Your request has been forwarded to the workspace owner " + sWorkspaceOwnerEmail + " of the workspace you have selected for approval. You will receive an e-mail from the workspace owner when this has been processed.</li></ul></td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\"><ul><li>Your request has been forwarded to your manager " + sMgrEmail + " for approval to access E&TS Connect. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</li></ul></td>");
					out.println("</tr>");
					if (bITARRequested) {
						out.println("<tr>");
						out.println("<td headers=\"\" align=\"left\"><ul><li>An ITAR entitlement request has been forwarded to your manager " + sMgrEmail + " for approval to access ITAR workspace. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</li></ul></td>");
						out.println("</tr>");
					}
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\">Once the workspace owner and your manager have approved your request, you will be able to access this workspace.</td>");
					out.println("</tr>");

				} else {
					String sMgrEmail = ETSUtils.getManagersEMailFromDecafTable(con,es.gUSERN);
					if (!sMgrEmail.trim().equalsIgnoreCase("")) {
						sMgrEmail = "at " + sMgrEmail;
					}

					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\"><ul><li>Your request has been forwarded to the workspace owner " + sWorkspaceOwnerEmail + " of the workspace you have selected for approval. You will receive an e-mail from the workspace owner when this has been processed.</li></ul></td>");
					out.println("</tr>");
					if (bITARRequested) {
						out.println("<tr>");
						out.println("<td headers=\"\" align=\"left\"><ul><li>An ITAR entitlement request has been forwarded to your manager " + sMgrEmail + " for approval to access ITAR workspace. You will receive an e-mail from \"IBM Customer Connect\" when this has been processed.</li></ul></td>");
						out.println("</tr>");
					}
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\">&nbsp;</td>");
					out.println("</tr>");
					out.println("<tr>");
					out.println("<td headers=\"\" align=\"left\">Once the workspace owner has approved your request, you will be able to access this workspace.</td>");
					out.println("</tr>");
				}

				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br /><br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\"  width=\"250\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"Go to E&TS Connect main page\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Go to E&TS Connect main page</a></td></tr></table></td>");
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSUserAccessServlet.wss?linkid=" + params.getLinkId() + "&from=req\" >Request access to another workspace</a></td></tr></table></td>");
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
		out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		out.println("<tr>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("<!-- End Gray dotted line -->");


	}

	public void showRequestScreenForExternal(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();

		try {

			DecafSuperSoldTos companies = new DecafSuperSoldTos();
			Vector vCompany = companies.getSupSoldToList(con);

			out.println("<br />");

			if (!sError.trim().equalsIgnoreCase("")) {

				out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				out.println("<tr>");
				out.println("<td headers=\"\" align=\"left\"><span style=\"color:#ff3333\">" + sError.toString() + "</span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");

			}

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Thank you for your interest in E&TS Connect.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Please help us identify the workspace you'll need to access.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><ul><li>Provide the project or proposal name, to the best of your knowledge.</li><li>Provide the e-mail address of your IBM contact, preferably a principal or project manager, for the project or proposal.</ul></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">You will be notified as soon as your request has been processed.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

//			printGreyDottedLine(out);
//
//			out.println("<br /><br />");


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"210\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"w_proj\">Project or proposal:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" colspan=\"2\"><input type=\"text\"  class=\"iform\" name=\"proj_name\" id=\"w_proj\" value=\"\" size=\"25\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"3\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"210\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"w_email\">E-mail address of IBM contact:</label></b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"170\"><input type=\"text\"  class=\"iform\" name=\"email_id\" id=\"w_email\" value=\"\" size=\"25\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><a class=\"fbox\" href=\"//www.ibm.com/contact/employees/us/\" target=\"new\" class=\"fbox\" onclick=\"window.open('//www.ibm.com/contact/employees/us/','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=750,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('//www.ibm.com/contact/employees/us/','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=750,height=500,left=150,top=120'); return false;\">Look up e-mail address of IBM contact</a></td>");

			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><label for=\"w_comments\"><b>Additional comments</b>:</label></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><textarea id=\"w_comments\" name=\"add_comments\" value=\"\" cols=\"65\" rows=\"5\"></textarea></td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><span style=\"color:#cc6600\">*</span>Required fields are indicated with asterisk(*).</td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");

			printGreyDottedLine(out);

			out.println("<br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" /></td>");
			out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			out.println("</tr></table>");

			out.println("<input type=\"hidden\" name=\"act\" value=\"externalclient\" />");


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

			String sProjectName = ETSUtils.checkNull(request.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request.getParameter("add_comments"));

			ETSUserAccessRequest userRequest = new ETSUserAccessRequest();

			if (isValidEmail(con,sMgrEmailId)){
				if (isWkspcOwner(con,sMgrEmailId)||isWkspcManager(con,sMgrEmailId)){
					userRequest.recordRequestWithComments(con,es.gIR_USERN,sProjectName,sMgrEmailId,es.gIR_USERN,sComments,Defines.ETS_WORKSPACE_TYPE);
					mail = createManagerExternalEmail();
				} else {
					if (isTeamMbr(con,sMgrEmailId)){
						userRequest.recordRequestWithComments(con,es.gIR_USERN,sProjectName,sMgrEmailId,es.gIR_USERN,sComments,Defines.ETS_WORKSPACE_TYPE);
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

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Your request has been submitted successfully.</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			printGreyDottedLine(out);

			out.println("<br /><br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
			out.println("</tr></table>");

		} catch (Exception e) {
			throw e;
		}

	}

	public void showRequestNewCompany(String sError) throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		Connection con = this.params.getConnection();
		HttpServletRequest request = this.params.getRequest();
		String appType = ETSUtils.checkNull(request.getParameter("appType"));

		try {

			String strProjectType = "";
			if (appType.equals(Defines.AIC_WORKSPACE_TYPE)) {
				strProjectType = Defines.AIC_WORKSPACE_TYPE;
			} else {
				strProjectType = Defines.ETS_WORKSPACE_TYPE;
			}
			UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(strProjectType);

			if (!sError.equalsIgnoreCase("")) {

				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span style=\"color:#ff3333\"><b>Please fix the following error(s) before clicking on Submit.</b><span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td headers=\"\" colspan=\"2\"><span style=\"color:#ff3333\">" + sError.toString() + "<span></td>");
				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);
			}


			String sCompany = ETSUtils.checkNull(request.getParameter("company_name"));
			String sDivision = ETSUtils.checkNull(request.getParameter("division"));
			String sAddress = ETSUtils.checkNull(request.getParameter("address"));
			String sCity = ETSUtils.checkNull(request.getParameter("city"));
			String sState = ETSUtils.checkNull(request.getParameter("state"));
			String sCountry = ETSUtils.checkNull(request.getParameter("country"));
			String sZip = ETSUtils.checkNull(request.getParameter("zip"));
			String sPhone = ETSUtils.checkNull(request.getParameter("phone"));
			String sFax = ETSUtils.checkNull(request.getParameter("fax"));
			String sDesc = ETSUtils.checkNull(request.getParameter("desc"));
			String sQ1 = ETSUtils.checkNull(request.getParameter("radio_q1"));
			String sQ11 = ETSUtils.checkNull(request.getParameter("radio_nuc"));
			String sQ12 = ETSUtils.checkNull(request.getParameter("radio_chc"));
			String sQ13 = ETSUtils.checkNull(request.getParameter("radio_guc"));
			String sQ2 = ETSUtils.checkNull(request.getParameter("radio_q2"));
			String sQ3 = ETSUtils.checkNull(request.getParameter("radio_q3"));
			String sQ4 = ETSUtils.checkNull(request.getParameter("radio_q4"));
			String sQ5 = ETSUtils.checkNull(request.getParameter("radio_q5"));


			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>Please read the following instructions before you send your request for new company creation.</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">The United States government is extremly focused on US Company's compliance to US export regulations. The Bureau of Industry and Security has increased their work force by 1/3 all in enforcement of US regulations. The new screening questions MUST be answered by the IBMer who knows the customer. The documentation to support these answers MUST be retained per corporate record retention requirements.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

//			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
//			out.println("<tr>");
//			out.println("<td headers=\"\" align=\"left\"><b>Please provide answers to questions 1-4 and a description of product being accessed, or if services, the IBM technology description.</b></td>");
//			out.println("</tr>");
//			out.println("</table>");
//
//			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">More than ever before it is extremly important for IBMers to know their customers, our product / technology / software / services being sold, and the END USE of our products.</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\"><b>It may take up to 5 business days to add a new company to IBM SAP systems and into Customer Connect. If you have any questions or want to follow-up on your requests, please send an email to "+ unbrandedProp.getAdminEmailID() +".</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");



			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</td>");
			out.println("</tr>");
			out.println("</table>");

			//printGreyDottedLine(out);

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr class=\"tdblue\" >");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><b>&nbsp;Customer connect input form</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"2\" border=\"0\" width=\"100%\">");


			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_company\">Company's legal name</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\" class=\"iform\" name=\"company_name\" id=\"label_company\" size=\"40\" value=\"" + sCompany + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_div\">Division</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\" class=\"iform\" name=\"division\" id=\"label_div\" size=\"40\" value=\"" + sDivision + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_add\">Address</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\" class=\"iform\" name=\"address\" id=\"label_add\" size=\"40\" value=\"" + sAddress + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_city\">City</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\" class=\"iform\" name=\"city\" id=\"label_city\" size=\"40\" value=\"" + sCity + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_state\">State</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\"  class=\"iform\" name=\"state\" id=\"label_state\" size=\"40\" value=\"" + sState + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_country\">Country</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\"  class=\"iform\" name=\"country\" id=\"label_country\" size=\"40\" value=\"" + sCountry + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_zip\">Zip code</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\"  class=\"iform\" name=\"zip\" id=\"label_zip\" size=\"40\" value=\"" + sZip + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_phone\">Phone</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\"  class=\"iform\" name=\"phone\" id=\"label_phone\" size=\"40\" value=\"" + sPhone + "\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\"><b><label for=\"label_fax\">Fax</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"text\"  class=\"iform\" name=\"fax\" id=\"label_fax\" size=\"40\" value=\"" + sFax + "\" /></td>");
			out.println("</tr>");

//			out.println("<tr>");
//			out.println("<td headers=\"\" colspan=\"2\" height=\"21\">&nbsp;</td>");
//			out.println("</tr>");

			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"200\" align=\"left\" height=\"21\" valign=\"top\"><b><label for=\"label_desc\">Description of product being accessed, or if services, the IBM technology description</label></b>:</td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><textarea name=\"desc\"  class=\"iform\" id=\"label_desc\" cols=\"40\" rows=\"5\">" + sDesc + "</textarea></td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("<br /><br />");


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr class=\"tdblue\" >");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><b>&nbsp;Customer proliferation and military screening questions</b></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			// new question

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>1.</b></td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\" valign=\"top\"><b>Is this sold-to a direct business relation to 'Hughes Networking Systems Beijing Co' or 'NHS Beijing Co'?</b></td>");
			if (sQ1.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q1\" value=\"Yes\" id=\"label_q1yes\" checked=\"checked\" /><label for=\"label_q1yes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q1\" value=\"Yes\" id=\"label_q1yes\" /><label for=\"label_q1yes\">Yes</label></td>");
			}
			if (sQ1.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q1\" value=\"No\" id=\"label_q1no\" checked=\"checked\" /><label for=\"label_q1no\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q1\" value=\"No\" id=\"label_q1no\" /><label for=\"label_q1no\">No</label></td>");
			}

			out.println("</tr>");
			out.println("</table>");

			out.println("<br /><br />");


			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>2.</b></td>");
			out.println("<td headers=\"\" align=\"left\" height=\"21\"><b>Is there any reason to believe or suspect this non-IBM customer may be DIRECTLY or INDIRECTLY involved in the production, development or marketing of technology or products used in relation to:</td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\"><ul><li>Nuclear weapons or energy systems</li></ul></td>");
			if (sQ11.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_nuc\" value=\"Yes\" id=\"label_nucyes\" checked=\"checked\" /><label for=\"label_nucyes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_nuc\" value=\"Yes\" id=\"label_nucyes\" /><label for=\"label_nucyes\">Yes</label></td>");
			}
			if (sQ11.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_nuc\" value=\"No\" id=\"label_nucno\" checked=\"checked\" /><label for=\"label_nucno\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_nuc\" value=\"No\" id=\"label_nucno\" /><label for=\"label_nucno\">No</label></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\"><ul><li>Chemical or biological weapons systems</li></ul></td>");
			if (sQ12.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_chc\" value=\"Yes\" id=\"label_chyes\" checked=\"checked\" /><label for=\"label_chyes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_chc\" value=\"Yes\" id=\"label_chyes\" /><label for=\"label_chyes\">Yes</label></td>");
			}
			if (sQ12.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_chc\" value=\"No\" id=\"label_chno\" checked=\"checked\" /><label for=\"label_chno\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_chc\" value=\"No\" id=\"label_chno\" /><label for=\"label_chno\">No</label></td>");
			}


			out.println("</tr>");
			out.println("</table>");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\"><ul><li>Guided missile control systems</li></ul></td>");
			if (sQ13.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_guc\" value=\"Yes\" id=\"label_guyes\" checked=\"checked\" /><label for=\"label_guyes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_guc\" value=\"Yes\" id=\"label_guyes\" /><label for=\"label_guyes\">Yes</label></td>");
			}
			if (sQ13.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_guc\" value=\"No\" id=\"label_guno\" checked=\"checked\" /><label for=\"label_guno\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><input type=\"radio\" name=\"radio_guc\" value=\"No\" id=\"label_guno\" /><label for=\"label_guno\">No</label></td>");
			}


			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr >");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 2 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 2 help</a></td>");
			}else {
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 2 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q1&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 2 help</a></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<br />");

			// question 2

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>3.</b></td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\" valign=\"top\"><b>Is there any reason to believe or suspect IBM exports to this non-IBM customer may be illegally diverted to another foreign country, or be used in any manner contrary to U.S export laws?</td>");
			if (sQ2.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q2\" value=\"Yes\" id=\"label_q2yes\" checked=\"checked\" /><label for=\"label_q2yes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q2\" value=\"Yes\" id=\"label_q2yes\" /><label for=\"label_q2yes\">Yes</label></td>");
			}
			if (sQ2.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q2\" value=\"No\" id=\"label_q2no\" checked=\"checked\" /><label for=\"label_q2no\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q2\" value=\"No\" id=\"label_q2no\" /><label for=\"label_q2no\">No</label></td>");
			}

			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr >");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 3 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 3 help</a></td>");
			}else {
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 3 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q2&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 3 help</a></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<br />");

			// question 3

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>4.</b></td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\" valign=\"top\"><b>Is customer / consignee a foreign government entity?</td>");
			if (sQ3.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q3\" value=\"Yes\" id=\"label_q3yes\" checked=\"checked\" /><label for=\"label_q3yes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q3\" value=\"Yes\" id=\"label_q3yes\" /><label for=\"label_q3yes\">Yes</label></td>");
			}
			if (sQ3.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q3\" value=\"No\" id=\"label_q3no\" checked=\"checked\" /><label for=\"label_q3no\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q3\" value=\"No\" id=\"label_q3no\" /><label for=\"label_q3no\">No</label></td>");
			}


			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr >");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 4 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 4 help</a></td>");
			}else{
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 4 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q3&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 4 help</a></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<br />");


            // question 4

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>5.</b></td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\" valign=\"top\"><b>Is this non-IBM entity a military end user?</td>");
			if (sQ4.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q4\" value=\"Yes\" id=\"label_q4yes\" checked=\"checked\" /><label for=\"label_q4yes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q4\" value=\"Yes\" id=\"label_q4yes\" /><label for=\"label_q4yes\">Yes</label></td>");
			}
			if (sQ4.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q4\" value=\"No\" id=\"label_q4no\" checked=\"checked\" /><label for=\"label_q4no\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q4\" value=\"No\" id=\"label_q4no\" /><label for=\"label_q4no\">No</label></td>");
			}

			out.println("</tr>");
			out.println("</table>");



			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr >");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 5 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 5 help</a></td>");
			}else{
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 5 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q4&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 5 help</a></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<br />");


			// question 5

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"10\" align=\"left\" height=\"21\" valign=\"top\"><span style=\"color:#cc6600\">*</span></td>");
			out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\" valign=\"top\"><b>6.</b></td>");
			out.println("<td headers=\"\" width=\"400\" align=\"left\" height=\"21\" valign=\"top\"><b>Is a written assurance for technology and software under restriction (TSR) on file for this entity ?</td>");
			if (sQ5.equalsIgnoreCase("Yes")) {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q5\" value=\"Yes\" id=\"label_q5yes\" checked=\"checked\" /><label for=\"label_q5yes\">Yes</label></td>");
			} else {
				out.println("<td headers=\"\" width=\"50\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q5\" value=\"Yes\" id=\"label_q5yes\" /><label for=\"label_q5yes\">Yes</label></td>");
			}
			if (sQ5.equalsIgnoreCase("No")) {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q5\" value=\"No\" id=\"label_q5no\" checked=\"checked\" /><label for=\"label_q5no\">No</label></td>");
			} else {
				out.println("<td headers=\"\" align=\"left\" height=\"21\" valign=\"top\"><input type=\"radio\" name=\"radio_q5\" value=\"No\" id=\"label_q5no\" /><label for=\"label_q5no\">No</label></td>");
			}

			out.println("</tr>");
			out.println("</table>");



			out.println("<br />");

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr >");
			out.println("<td headers=\"\" width=\"30\" align=\"left\" height=\"21\">&nbsp;</td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 6 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.AIC_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 6 help</a></td>");
			}else{
				out.println("<td headers=\"\" width=\"20\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\"><img  src=\"" + Defines.ICON_ROOT + "popup.gif\" height=\"18\" width=\"18\" align=\"top\" valign=\"top\" border=\"0\" alt=\"Question 6 help\" /></a></td>");
				out.println("<td headers=\"\" align=\"left\" height=\"21\"><a href=\"" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "\" target=\"new\" class=\"fbox\" onclick=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\" onkeypress=\"window.open('" + Defines.SERVLET_PATH + "ETSHelpServlet.wss?field_name=q5&proj_type=" + Defines.ETS_WORKSPACE_TYPE + "','Help','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=500,left=150,top=120'); return false;\">Open this section for question 6 help</a></td>");
			}
			out.println("</tr>");
			out.println("</table>");

			out.println("<br />");
			out.println("<br />");

			
			printGreyDottedLine(out);

			out.println("<br />");
			out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
			out.println("<td headers=\"\"  width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" /></td>");
			if(appType.equals("AIC")){
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			}else{
				out.println("<td headers=\"\" align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\"  align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + params.getLinkId() + "\" >Cancel</a></td></tr></table></td>");
			}
			out.println("</tr></table>");

			out.println("<input type=\"hidden\" name=\"act\" value=\"newcompanyconfirm\" />");
			out.println("<input type=\"hidden\" name=\"appType\" value=\""+appType+"\" />");	

		} catch (Exception e) {
			throw e;
		}

	}

		public String validateRequestNewCompany() throws Exception {
			
			PrintWriter out = this.params.getWriter();
			StringBuffer sError = new StringBuffer("");
			HttpServletRequest request = this.params.getRequest();
			Connection con = this.params.getConnection();
			String appType = ETSUtils.checkNull(request.getParameter("appType"));
			out.println("<input type=\"hidden\" name=\"appType\" value=\""+appType+"\" />");

			try {

				String sCompany = ETSUtils.checkNull(request.getParameter("company_name"));
				String sDivision = ETSUtils.checkNull(request.getParameter("division"));
				String sAddress = ETSUtils.checkNull(request.getParameter("address"));
				String sCity = ETSUtils.checkNull(request.getParameter("city"));
				String sState = ETSUtils.checkNull(request.getParameter("state"));
				String sCountry = ETSUtils.checkNull(request.getParameter("country"));
				String sZip = ETSUtils.checkNull(request.getParameter("zip"));
				String sPhone = ETSUtils.checkNull(request.getParameter("phone"));
				String sFax = ETSUtils.checkNull(request.getParameter("fax"));
				String sDesc = ETSUtils.checkNull(request.getParameter("desc"));
				String sQ1 = ETSUtils.checkNull(request.getParameter("radio_q1"));
				String sQ11 = ETSUtils.checkNull(request.getParameter("radio_nuc"));
				String sQ12 = ETSUtils.checkNull(request.getParameter("radio_chc"));
				String sQ13 = ETSUtils.checkNull(request.getParameter("radio_guc"));
				String sQ2 = ETSUtils.checkNull(request.getParameter("radio_q2"));
				String sQ3 = ETSUtils.checkNull(request.getParameter("radio_q3"));
				String sQ4 = ETSUtils.checkNull(request.getParameter("radio_q4"));
				String sQ5 = ETSUtils.checkNull(request.getParameter("radio_q5"));

				if (sCompany.equalsIgnoreCase("")) {
					sError.append("Please enter the company's legal name. <br />");
				}

				// check if the company name entered already exists...
				if (!sCompany.equalsIgnoreCase("")) {

					DecafSuperSoldTos companies = new DecafSuperSoldTos();
					Vector vCompany = companies.getSupSoldToList(con);

					boolean bExists = false;

					for (int i = 0; i < vCompany.size(); i++) {
						String sTemp = (String) vCompany.elementAt(i);

						if (sCompany.trim().equalsIgnoreCase(sTemp)) {
							bExists = true;
						}
					}

					if (bExists) {
						sError.append("The client company (" + sCompany + ") already exists in the system. Please enter a new client company.<br />");
					}

				}

				if (sAddress.equalsIgnoreCase("")) {
					sError.append("Please enter the company's address. <br />");
				}

				if (sCity.equalsIgnoreCase("")) {
					sError.append("Please enter the company's city. <br />");
				}

				if (sState.equalsIgnoreCase("")) {
					sError.append("Please enter the company's state. <br />");
				}

				if (sCountry.equalsIgnoreCase("")) {
					sError.append("Please enter the company's country. <br />");
				}

				if (sZip.equalsIgnoreCase("")) {
					sError.append("Please enter the company's zip code. <br />");
				}

				if (sPhone.equalsIgnoreCase("")) {
					sError.append("Please enter the company's phone. <br />");
				}

				if (sDesc.equalsIgnoreCase("")) {
					sError.append("Please enter the description of product being accessed. <br />");
				}

				if (sQ1.equalsIgnoreCase("")) {
					sError.append("Please select a value for question 1.<br />");
				}

				if (sQ11.equalsIgnoreCase("")) {
					sError.append("Please select a value for <b>Nuclear weapons or energy systems</b> in question 2. <br />");
				}

				if (sQ12.equalsIgnoreCase("")) {
					sError.append("Please select a value for <b>Chemical or biological weapons systems</b> in question 2. <br />");
				}

				if (sQ13.equalsIgnoreCase("")) {
					sError.append("Please select a value for <b>Guided missile control system</b> in question 2. <br />");
				}

				if (sQ2.equalsIgnoreCase("")) {
					sError.append("Please select a value for question 3. <br />");
				}

				if (sQ3.equalsIgnoreCase("")) {
					sError.append("Please select a value for question 4. <br />");
				}

				if (sQ4.equalsIgnoreCase("")) {
					sError.append("Please select a value for question 5. <br />");
				}
				
				if (sQ5.equalsIgnoreCase("")) {
					sError.append("Please select a value for question 6. <br />");
				}



			} catch (Exception e) {
				throw e;
			}

			return sError.toString();

		}

		public String validateClientScreen() throws Exception {

			StringBuffer sError = new StringBuffer("");
			HttpServletRequest request = this.params.getRequest();

			try {

				String sCompany = ETSUtils.checkNull(request.getParameter("client_company"));

				if (sCompany.equalsIgnoreCase("")) {
					sError.append("Please select the client company from the list. <br />");
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

				String sProjName = ETSUtils.checkNull(request.getParameter("proj_name"));

				if (sProjName.equalsIgnoreCase("")) {
					sError.append("Please enter the project or proposal name. <br />");
				}

				String sEmail = ETSUtils.checkNull(request.getParameter("email_id"));

				if (sEmail.equalsIgnoreCase("")) {
					sError.append("Please enter the e-mail address of IBM contact.<br />");
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

				String sProjectId = ETSUtils.checkNull(request.getParameter("project_id"));

				if (sProjectId.equalsIgnoreCase("")) {
					sError.append("Please select the workspace you want to request access to.<br />");
				}

				String sProjectName = ETSWorkspaceDAO.getWorkspaceName(con,sProjectId);
				String sProjectType = getWorkspaceType(con,sProjectId);

				boolean bPending = false;
				boolean bMember = false;

				// check to see if the member request is pending or is already a member.. if yes, display error..

				// to get the pending request
				String querypending = "SELECT COUNT(PROJECT_NAME) FROM ETS.ETS_ACCESS_REQ WHERE USER_ID = ? AND PROJECT_NAME = ? AND STATUS = ? AND PROJECT_TYPE = ? with ur";

				// to check if memeber off...
				String querymember = "SELECT COUNT(USER_ID) FROM ETS.ETS_USERS WHERE USER_ID = ? AND USER_PROJECT_ID = ? AND ACTIVE_FLAG = ? with ur";

				pstmt1 = con.prepareStatement(querypending);

				pstmt1.clearParameters();
				pstmt1.setString(1, es.gIR_USERN);
				pstmt1.setString(2, sProjectName);
				pstmt1.setString(3, Defines.ACCESS_PENDING);
				pstmt1.setString(4, sProjectType);

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
					sError.append("Please select a workspace for which you are not a member or you do not have a pending access request.<br />");
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

	public ETSMail createCompanyRequestEmail() throws SQLException, Exception {

		PrintWriter out = this.params.getWriter();
		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		String strProjectType = "";
		String appType = ETSUtils.checkNull(request.getParameter("appType"));
		if (appType.equals(Defines.AIC_WORKSPACE_TYPE)) {
			strProjectType = Defines.AIC_WORKSPACE_TYPE;
		} else {
			strProjectType = Defines.ETS_WORKSPACE_TYPE;
		}
		UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(strProjectType);
		
		try {

			ETSProperties prop = new ETSProperties();

			String sCompany = ETSUtils.checkNull(request.getParameter("company_name"));
			String sDivision = ETSUtils.checkNull(request.getParameter("division"));
			String sAddress = ETSUtils.checkNull(request.getParameter("address"));
			String sCity = ETSUtils.checkNull(request.getParameter("city"));
			String sState = ETSUtils.checkNull(request.getParameter("state"));
			String sCountry = ETSUtils.checkNull(request.getParameter("country"));
			String sZip = ETSUtils.checkNull(request.getParameter("zip"));
			String sPhone = ETSUtils.checkNull(request.getParameter("phone"));
			String sFax = ETSUtils.checkNull(request.getParameter("fax"));
			String sDesc = ETSUtils.checkNull(request.getParameter("desc"));
			String sQ1 = ETSUtils.checkNull(request.getParameter("radio_q1"));
			String sQ11 = ETSUtils.checkNull(request.getParameter("radio_nuc"));
			String sQ12 = ETSUtils.checkNull(request.getParameter("radio_chc"));
			String sQ13 = ETSUtils.checkNull(request.getParameter("radio_guc"));
			String sQ2 = ETSUtils.checkNull(request.getParameter("radio_q2"));
			String sQ3 = ETSUtils.checkNull(request.getParameter("radio_q3"));
			String sQ4 = ETSUtils.checkNull(request.getParameter("radio_q4"));
			String sQ5 = ETSUtils.checkNull(request.getParameter("radio_q5"));


			mail.setSubject(unbrandedProp.getAppName() +": Request for new company creation (" + sCompany + ")");

			sEmailStr.append("A request to create a new company has been generated from " + unbrandedProp.getAppName() + ".\n\n");

			sEmailStr.append("The new request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                   : " + es.gIR_USERN + "\n");
			sEmailStr.append("Name                 : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail               : " + es.gEMAIL + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Company's legal name : " + sCompany + "\n");
			sEmailStr.append("Division             : " + sDivision + "\n");
			sEmailStr.append("Address              : " + sAddress + "\n");
			sEmailStr.append("City                 : " + sCity + "\n");
			sEmailStr.append("State                : " + sState + "\n");
			sEmailStr.append("Country              : " + sCountry + "\n");
			sEmailStr.append("Zip                  : " + sZip + "\n");
			sEmailStr.append("Phone                : " + sPhone + "\n");
			sEmailStr.append("Fax                  : " + sFax + "\n\n");
			sEmailStr.append("Description of products being accessed, or if sevices,\n");
			sEmailStr.append("the IBM technology description: \n");
			sEmailStr.append(sDesc + "\n\n");

			sEmailStr.append("1. Is this sold-to a direct business relation to 'Hughes \n");
			sEmailStr.append("   Networking Systems Beijing Co' or 'NHS Beijing Co'? " + sQ1 + "\n\n");

			sEmailStr.append("2. Is there any reason to believe or suspect this non-IBM customer\n");
			sEmailStr.append("   may be DIRECTLY or INDIRECTLY involved in the production, development\n");
			sEmailStr.append("   or marketing of technology or products used in relation to:\n");
			sEmailStr.append("    * Nuclear weapons or energy systems      : " + sQ11 + "\n");
			sEmailStr.append("    * Chemical or biological weapons systems : " + sQ12 + "\n");
			sEmailStr.append("    * Guided missile control systems         : " + sQ13 + "\n\n");

			sEmailStr.append("3. Is there any reason to believe or suspect IBM exports to this\n");
			sEmailStr.append("   non-IBM customer may be illegally diverted to another foreign\n");
			sEmailStr.append("   country, or be used in any manner contrary to\n");
			sEmailStr.append("   U.S export laws? " + sQ2 + "\n\n");

			sEmailStr.append("4. Is customer / consignee a foreign government entity? " + sQ3 + "\n\n");

			sEmailStr.append("5. Is this non-IBM entity a military end user? " + sQ4 + "\n\n");
			
			sEmailStr.append("6. Is a written assurance for technology and software under restriction (TSR) on file for this entity ? " + sQ5 + "\n\n");

			sEmailStr.append("Note: Since the e-mail recipient is an admin, we are assuming that\n admin knows what to do and further clarification in this e-mail is not necessary.\n\n");

			sEmailStr.append(CommonEmailHelper.getEmailFooter(unbrandedProp.getAppName()));

			mail.setMessage(sEmailStr.toString());
			mail.setTo(unbrandedProp.getAdminEmailID());
			mail.setFrom(es.gEMAIL);
			mail.setBcc("");
			mail.setCc("");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public ETSMail createAdminRequestEmail() throws SQLException, Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();

		try {

			String sProjectName = ETSUtils.checkNull(request.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request.getParameter("add_comments"));

			mail.setSubject("Access Request to E&TS Connect from  " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + " at " + es.gASSOC_COMP);

			sEmailStr.append("This request has been submitted but it cannot be processed automatically.\n");
			sEmailStr.append("The IBM Contact identified is not is a known workspace owner nor a member\n");
			sEmailStr.append("of any workspace. Investigate IBM contact given (find the person, \n");
			sEmailStr.append("are they in E&TS Connect, etc). Then work with proposal/project representatives to determine\n");
			sEmailStr.append("how to process this request.\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN + "\n");
			sEmailStr.append("Name                          : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL + "\n");
			sEmailStr.append("User company                  : " + es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Project or proposal requested : " + sProjectName + "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId + "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");

			sEmailStr.append("Click the link below to access E&TS Connect.\n\n");
			sEmailStr.append(oem.edge.common.Global.getUrl("ets/ETSConnectServlet.wss?linkid=" + this.params.getLinkId() + "\n\n"));

			sEmailStr.append("===============================================================\n");
			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			sEmailStr.append("of on demand tools that is available online 24/7.\n");
			sEmailStr.append("===============================================================\n");
			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			sEmailStr.append("===============================================================\n\n");

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

		try {

			String sProjectName = ETSUtils.checkNull(request.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request.getParameter("add_comments"));

			mail.setSubject("Access Request to E&TC Connect from  " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + " at " + es.gASSOC_COMP);

			sEmailStr.append("You have been identified as the Proposal/Project Owner for the following\n");
			sEmailStr.append("request for access to E&TC Connect. Please process accordingly by clicking \n");
			sEmailStr.append("on the link(s) below. Verify information and choose to accept or reject their\n");
			sEmailStr.append("request. If you do not have access to this project please forward this to etsadmin@us.ibm.com.\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN + "\n");
			sEmailStr.append("Name                          : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL + "\n");
			sEmailStr.append("User company                  : " + es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Project or proposal requested : " + sProjectName + "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId + "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");

			sEmailStr.append("Click the link below to access E&TS Connect.\n\n");
			sEmailStr.append(oem.edge.common.Global.getUrl("ets/ETSConnectServlet.wss?linkid=" + this.params.getLinkId() + "\n\n"));
			
			UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			sEmailStr.append(CommonEmailHelper.getEmailFooter(unbrandedProp.getAppName()));
			
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			//sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			//sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			//sEmailStr.append("of on demand tools that is available online 24/7.\n");
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			//sEmailStr.append("===============================================================\n\n");

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

	public ETSMail createTeamMemberExternalEmail() throws SQLException, Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();


		try {

			String sProjectName = ETSUtils.checkNull(request.getParameter("proj_name"));
			String sMgrEmailId = ETSUtils.checkNull(request.getParameter("email_id"));
			String sComments = ETSUtils.checkNull(request.getParameter("add_comments"));

			mail.setSubject("Access Request to E&TS Connect from  " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + " at " + es.gASSOC_COMP);

			sEmailStr.append("You have been identified as the IBM contact for the following request\n");
			sEmailStr.append("for access to E&TS Connect. Please process by selecting the \n");
			sEmailStr.append("link provided below. If you do not have access to this workspace,\n");
			sEmailStr.append("please forward this to etsadmin@us.ibm.com.\n\n");

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN + "\n");
			sEmailStr.append("Name                          : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL + "\n");
			sEmailStr.append("User company                  : " + es.gASSOC_COMP + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Project or proposal requested : " + sProjectName + "\n");
			sEmailStr.append("IBM contact e-mail            : " + sMgrEmailId + "\n");

			sEmailStr.append("Additional comments: \n");
			sEmailStr.append(sComments + "\n\n");


			sEmailStr.append("Click the link below to access E&TS Connect, select the workspace that matches the request\n");
			sEmailStr.append("and then do a \"Request add members\" under team tab.\n\n");

			sEmailStr.append(oem.edge.common.Global.getUrl("ets/ETSConnectServlet.wss?linkid=" + this.params.getLinkId() + "\n\n"));

			UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			sEmailStr.append(CommonEmailHelper.getEmailFooter(unbrandedProp.getAppName()));
			
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			//sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			//sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			//sEmailStr.append("of on demand tools that is available online 24/7.\n");
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			//sEmailStr.append("===============================================================\n\n");

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

	public ETSMail createWorkspaceOwnerRequestEmail() throws SQLException, Exception {

		ETSMail mail = new ETSMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");

		EdgeAccessCntrl es = this.params.getEdgeAccessCntrl();
		HttpServletRequest request = this.params.getRequest();
		Connection con = this.params.getConnection();

		PreparedStatement pstmt = null;
		ResultSet rs = null;


		try {

			String sCompany = ETSUtils.checkNull(request.getParameter("client_company"));
			String sProjectId = ETSUtils.checkNull(request.getParameter("project_id"));
			String sComments = ETSUtils.checkNull(request.getParameter("comments"));

			String sWorkspaceOwnerEmail = "";

			String sProjectName = ETSWorkspaceDAO.getWorkspaceName(con,sProjectId);

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


			mail.setSubject("Access Request to E&TS Connect from  " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim());

			sEmailStr.append("ACTION REQUIRED: \n\n");

			sEmailStr.append("You have been identified as the Workspace Owner for the following\n");
			sEmailStr.append("request for access to E&TS Connect. \n\n");

			sEmailStr.append("Click the link below to access E&TS Connect, scroll down to\n");
			sEmailStr.append("\"Pending access requests\" section and click on the user name which\n");
			sEmailStr.append("you want to process.\n");
			sEmailStr.append(oem.edge.common.Global.getUrl("ets/ETSConnectServlet.wss?linkid=" + this.params.getLinkId() + "\n\n"));

			sEmailStr.append("The request details are as follows.\n\n");

			sEmailStr.append("Requestor details:\n");
			sEmailStr.append("ID                            : " + es.gIR_USERN + "\n");
			sEmailStr.append("Name                          : " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME.trim() + "\n");
			sEmailStr.append("E-mail                        : " + es.gEMAIL + "\n\n");

			sEmailStr.append("Details entered on request form:\n");
			sEmailStr.append("Requested Workspace           : " + ETSWorkspaceDAO.getWorkspaceName(con,sProjectId) + "\n\n");

			if (!sComments.trim().equalsIgnoreCase("")) {
				sEmailStr.append("Comments entered by user:\n");
				sEmailStr.append(sComments + "\n\n");
			}


			UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(Defines.ETS_WORKSPACE_TYPE);
			sEmailStr.append(CommonEmailHelper.getEmailFooter(unbrandedProp.getAppName()));
			
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
			//sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
			//sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
			//sEmailStr.append("of on demand tools that is available online 24/7.\n");
			//sEmailStr.append("===============================================================\n");
			//sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
			//sEmailStr.append("===============================================================\n\n");

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

		public void showCreateCompanyConfirmation(boolean bSuccess) throws SQLException, Exception {

			PrintWriter out = this.params.getWriter();
			Connection con = this.params.getConnection();
			HttpServletRequest request = this.params.getRequest();
			String appType = ETSUtils.checkNull(request.getParameter("appType"));

			try {

				String strProjectType = "";
				if (appType.equals(Defines.AIC_WORKSPACE_TYPE)) {
					strProjectType = Defines.AIC_WORKSPACE_TYPE;
				} else {
					strProjectType = Defines.ETS_WORKSPACE_TYPE;
				}
				UnbrandedProperties unbrandedProp = PropertyFactory.getProperty(strProjectType);

				out.println("<br />");

				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				if (bSuccess) {
					out.println("<td headers=\"\" colspan=\"2\"><b>Your request has been submitted successfully. <br /><br />It may take up to 5 business days to add a new company to IBM SAP systems and into Customer Connect. If you have any questions or want to follow-up on your requests, please send an email to <a href=\"mailto:" + unbrandedProp.getAdminEmailID() + "\">" + unbrandedProp.getAdminEmailID() + "</a></b></td>");
				} else {
					out.println("<td headers=\"\" colspan=\"2\"><span style=\"color:#ff3333\"><b>There has been an error processing your request. Please try to submit the request again. If the problem persists, please contact your primary contact.</b><span></td>");
				}

				out.println("</tr>");
				out.println("</table>");

				out.println("<br />");

				printGreyDottedLine(out);

				out.println("<br />");
				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				if(appType.equals("AIC")){
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "AICConnectServlet.wss?linkid=" + this.params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
				}else{
					out.println("<td headers=\"\"  width=\"140\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSConnectServlet.wss?linkid=" + this.params.getLinkId() + "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" border=\"0\" alt=\"Continue\" /></a></td>");
				}
				out.println("</tr></table>");


			} catch (Exception e) {
				throw e;
			}

		}

		private boolean isValidEmail(Connection con,String email) throws SQLException, Exception{
		    String val=EntitledStatic.getValue(con,"select distinct 1 from amt.users where user_email = '"+email+"' with ur");
		    if (val!=null && val.equals("1")){
		        return true;
		    } else{
		        return false;
		    }
		}

		private boolean isWkspcOwner(Connection con, String woEmail)throws SQLException, Exception{
		    return this.userHasRole(con,woEmail,"Workspace Owner");
		}

		private boolean isWkspcManager(Connection con,String wmEmail)throws SQLException, Exception{
		    return this.userHasRole(con,wmEmail,"Workspace Manager");
		}

		private boolean isTeamMbr(Connection con,String teamMbr)throws SQLException, Exception{
		    return this.userHasRole(con,teamMbr,"Member");
		}

		public boolean userHasRole(Connection con, String emailid, String role) throws SQLException, Exception{
		    String userid = EntitledStatic.getValue(con,"select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '"+emailid+"' and a.ir_userid = b.user_id order by a.ir_userid fetch first 1 row only with ur");
		    if (userid!=null){
		        // user is in ETS
		        String val=EntitledStatic.getValue(con,"select 1 from ets.ets_users a, ets.ets_roles b where a.user_id='"+userid+"' and a.user_role_id=b.role_id and b.role_name='"+role+"' fetch first 1 row only");
		        if (val!=null && val.equals("1")){ return true;} else{return false;}
		    } else {
		        // user not in ETS
		        return false;
		    }
		}

		/**
		 * @return
		 */
		public static String getWorkspaceType(Connection con, String sProjectID) throws SQLException, Exception {

			Statement stmt = null;
			ResultSet rs = null;

			StringBuffer sQuery = new StringBuffer("");

			String sProjectType = "";


			try {

				sQuery.append("SELECT PROJECT_TYPE FROM ETS.ETS_PROJECTS WHERE PROJECT_ID='" + sProjectID + "' for READ ONLY");

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				while (rs.next()) {

					sProjectType = rs.getString(1);

				}

			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
			}

			return sProjectType;

		}

}
